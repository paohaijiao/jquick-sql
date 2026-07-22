package com.github.paohaijiao.distributed.worker;

import com.github.paohaijiao.console.JConsole;
import com.github.paohaijiao.datasource.JQuickDataSourceManager;
import com.github.paohaijiao.distributed.proto.JQuickProtoService;
import com.github.paohaijiao.enums.JQuickExchangeType;
import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.expression.domain.JQuickBinaryExpression;
import com.github.paohaijiao.expression.domain.JQuickColumnRefExpression;
import com.github.paohaijiao.expression.domain.JQuickFunctionCallExpression;
import com.github.paohaijiao.expression.domain.JQuickLiteralExpression;
import com.github.paohaijiao.physical.JQuickPhysicalPlanNode;
import com.github.paohaijiao.physical.domain.JQuickPhysicalColumn;
import com.github.paohaijiao.physical.node.*;
import com.github.paohaijiao.proto.JQuickExecuteTaskRequest;
import com.github.paohaijiao.proto.JQuickFragmentProto;
import com.github.paohaijiao.proto.JQuickMemoryPartitionProto;
import com.github.paohaijiao.statement.JQuickColumnMeta;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 节点执行服务 - 负责执行各种物理计划节点
 */
public class JQuickNodeExecutor {

    private JConsole console=JConsole.initConsoleEnvironment();

    private final JQuickWorker worker;

    private final JQuickExpressionEvaluator expressionEvaluator;

    private final JQuickPartitionManager partitionManager;

    private final JQuickDataConverter dataConverter;

    private final JQuickProtoService jQuickprotoService;


    public JQuickNodeExecutor(JQuickWorker worker, JQuickExpressionEvaluator expressionEvaluator, JQuickPartitionManager partitionManager, JQuickDataConverter dataConverter) {
        this.worker = worker;
        this.expressionEvaluator = expressionEvaluator;
        this.partitionManager = partitionManager;
        this.dataConverter = dataConverter;
        this.jQuickprotoService = new JQuickProtoService();
    }

    /**
     * 执行片段的核心方法
     */
    public JQuickDataSet executeFragment(JQuickFragmentProto fragment, JQuickWorker.JQuickTaskContext context) {
        JQuickPhysicalPlanNode rootNode = jQuickprotoService.buildPhysicalNode(fragment.getPlan());
        JQuickDataSet result = executeNode(rootNode, context);
        console.info("executeFragment: returning " + result.size() + " rows from executeNode ["+rootNode.getNodeType()+"]");
        return result;
    }

    /**
     * 递归执行物理计划节点
     */
    public JQuickDataSet executeNode(JQuickPhysicalPlanNode node, JQuickWorker.JQuickTaskContext context) {
        if (node == null) {
            return JQuickDataSet.builder().build();
        }
        if (node instanceof JQuickTableScanPhysicalNode) {//1
            return executeTableScan((JQuickTableScanPhysicalNode) node, context);
        } else if (node instanceof JQuickFilterPhysicalNode) {//2
            return executeFilter((JQuickFilterPhysicalNode) node, context);
        } else if (node instanceof JQuickProjectPhysicalNode) {//3
            return executeProject((JQuickProjectPhysicalNode) node, context);
        } else if (node instanceof JQuickHashJoinPhysicalNode) {
            return executeHashJoin((JQuickHashJoinPhysicalNode) node, context);
        } else if (node instanceof JQuickNestedLoopJoinPhysicalNode) {
            return executeNestedLoopJoin((JQuickNestedLoopJoinPhysicalNode) node, context);
        }else if (node instanceof JQuickTopNPhysicalNode) {//7
            return executeTopN((JQuickTopNPhysicalNode) node, context);
        } else if (node instanceof JQuickSortPhysicalNode) {//6
            return executeSort((JQuickSortPhysicalNode) node, context);
        } else if (node instanceof JQuickLimitPhysicalNode) {//5
            return executeLimit((JQuickLimitPhysicalNode) node, context);
        }  else if (node instanceof JQuickWindowPhysicalNode) {//4
            return executeWindow((JQuickWindowPhysicalNode) node, context);
        } else if (node instanceof JQuickHashAggregatePhysicalNode) {//5
            return executeHashAggregate((JQuickHashAggregatePhysicalNode) node, context);
        } else if (node instanceof JQuickExchangePhysicalNode) {
            return executeExchange((JQuickExchangePhysicalNode) node, context);
        }else if (node instanceof JQuickSetOperationPhysicalNode) {//8
            return executeSetOperation((JQuickSetOperationPhysicalNode) node, context);
        } else if (node instanceof JQuickValuesPhysicalNode) {
            return executeValues((JQuickValuesPhysicalNode) node, context);
        } else if (node instanceof JQuickEmptyPhysicalNode) {
            return JQuickDataSet.builder().build();
        } else if (node instanceof JQuickRecursiveUnionPhysicalNode) {//9
            return executeRecursiveUnion((JQuickRecursiveUnionPhysicalNode) node, context);
        }
        throw new UnsupportedOperationException("Unknown node type: " + node.getNodeType());
    }


    /**
     * 执行物理计划（用于相关子查询，传递外部行数据）
     */
    public JQuickDataSet executePhysicalPlan(JQuickPhysicalPlanNode node, JQuickRow parentRow) {
        JQuickPhysicalPlanNode actualNode = node;
        if (node instanceof JQuickExchangePhysicalNode) {
            JQuickExchangePhysicalNode exchangeNode = (JQuickExchangePhysicalNode) node;
            if (exchangeNode.getExchangeType() == JQuickExchangeType.SHUFFLE || 
                exchangeNode.getExchangeType() == JQuickExchangeType.BROADCAST ||
                exchangeNode.getExchangeType() == JQuickExchangeType.GATHER) {
                if (exchangeNode.getChild() != null) {
                    actualNode = exchangeNode.getChild();
                }
            }
        }
        
        Set<JQuickTableScanPhysicalNode> tableScans = collectTableScans(actualNode);
        JQuickExecuteTaskRequest.Builder requestBuilder = JQuickExecuteTaskRequest.newBuilder()
                .setQueryId("subquery")
                .setTaskId("subquery_task_" + System.currentTimeMillis())
                .setMemoryLimitBytes(1024 * 1024 * 1024);
        for (JQuickTableScanPhysicalNode tableScan : tableScans) {
            JQuickDataSet tableData = readFromDataSource(tableScan.getTableName());
            if (parentRow != null) {
                List<JQuickRow> filteredRows = new ArrayList<>();
                for (JQuickRow row : tableData.getRows()) {
                    JQuickRow mergedRow = new JQuickRow();
                    mergedRow.putAll(row);
                    for (String key : parentRow.keySet()) {
                        mergedRow.put("outer_" + key, parentRow.get(key));
                    }
                    filteredRows.add(mergedRow);
                }
                tableData = new JQuickDataSet(tableData.getColumns(), filteredRows);
            }
            JQuickMemoryPartitionProto partition = JQuickMemoryPartitionProto.newBuilder()
                    .setPartitionId("subquery_partition_" + tableScan.getTableName())
                    .setPartitionIndex(0)
                    .setTotalPartitions(1)
                    .setData(dataConverter.convertToProto(tableData))
                    .build();
            requestBuilder.addInputPartitions(partition);
        }
        JQuickExecuteTaskRequest request = requestBuilder.build();
        JQuickWorker.JQuickTaskContext context = worker.new JQuickTaskContext(request.getTaskId(), request);
        return executeNode(actualNode, context);
    }

    private Set<JQuickTableScanPhysicalNode> collectTableScans(JQuickPhysicalPlanNode node) {
        Set<JQuickTableScanPhysicalNode> tableScans = new HashSet<>();
        collectTableScansRecursive(node, tableScans);
        return tableScans;
    }

    private void collectTableScansRecursive(JQuickPhysicalPlanNode node, Set<JQuickTableScanPhysicalNode> tableScans) {
        if (node instanceof JQuickTableScanPhysicalNode) {
            tableScans.add((JQuickTableScanPhysicalNode) node);
        }
        for (JQuickPhysicalPlanNode child : node.getChildren()) {
            collectTableScansRecursive(child, tableScans);
        }
    }

    /**
     * 执行 TableScan
     */
    private JQuickDataSet executeTableScan(JQuickTableScanPhysicalNode node, JQuickWorker.JQuickTaskContext context) {
        String tableName = node.getTableName();
        console.info("executeTableScan - tableName: " + tableName + ", partitionInfo: " + (node.getPartitionInfo() != null ? "exists" : "null"));
        Set<String> requiredColumns = node.getRequiredColumns();
        JQuickDataSet data;
        boolean isSourceFragment = context != null && context.getRequest() != null && context.getRequest().getInputPartitionsCount() > 0;
        if (isSourceFragment) {
            JQuickDataSet partitionData = readFromInputPartitions(tableName, context.getRequest());
            if (partitionData != null && !partitionData.isEmpty()) {
                console.info("Reading from input partitions (source fragment): " + tableName + ", rows: " + partitionData.size());
                data = partitionData;
            } else if (node.getPartitionInfo() != null) {
                console.info("Reading from memory partition: " + tableName);
                data = readFromMemoryPartition(tableName);
            } else if (JQuickDataSourceManager.containsTable(tableName)) {
                console.info("Reading from data source (fallback): " + tableName);
                data = JQuickDataSourceManager.getTable(tableName);
            } else {
                console.info("Reading from data source: " + tableName);
                data = readFromDataSource(tableName);
            }
        } else if (JQuickDataSourceManager.containsTable(tableName)) {
            console.info("Reading from data source (CTE or registered table): " + tableName);
            data = JQuickDataSourceManager.getTable(tableName);
        } else if (node.getPartitionInfo() != null) {
            console.info("Reading from memory partition: " + tableName);
            data = readFromMemoryPartition(tableName);
        } else {
            console.info("Reading from data source: " + tableName);
            data = readFromDataSource(tableName);
        }
        console.info("Table data loaded - rows: " + data.size() + ", columns: " + data.getColumns().size());
        if (node.getFilterPredicate() != null) {
            data = applyFilter(data, node.getFilterPredicate());
            console.info("After filter - rows: " + data.size());
        }
        if (requiredColumns != null && !requiredColumns.isEmpty()) {
            String alias=node.getAlias();
            Set<String> columns = new HashSet<>();
            for (String column : requiredColumns) {
                if (null!=alias&&!alias.equals(column)) {
                    columns.add(column.replace( alias+".",""));
                }else {
                    columns.add(column);
                }
            }
            data= data.select(columns.toArray(new String[0]));
            console.info("After projection - columns: " + data.getColumns().size());
        }
        context.addProcessedRows(data.size());
        return data;
    }
    
    /**
     * 从 input partitions 读取数据（分布式场景）
     */
    private JQuickDataSet readFromInputPartitions(String tableName, JQuickExecuteTaskRequest request) {
        String targetPartitionId = "subquery_partition_" + tableName;
        for (JQuickMemoryPartitionProto partition : request.getInputPartitionsList()) {
            console.info("readFromInputPartitions - checking partition: " + partition.getPartitionId() + ", hasData: " + partition.hasData());
            if (partition.getPartitionId().equals(targetPartitionId) && partition.hasData()) {
                JQuickDataSet partitionData = dataConverter.convertFromProto(partition.getData());
                console.info("readFromInputPartitions - found data in partition: " + partition.getPartitionId() + ", rows: " + partitionData.size());
                return partitionData;
            }
        }
        console.warn("readFromInputPartitions - no data found for table: " + tableName);
        return JQuickDataSet.builder().build();
    }
    /**
     * 执行 Filter
     */
    private JQuickDataSet executeFilter(JQuickFilterPhysicalNode node, JQuickWorker.JQuickTaskContext context) {
        JQuickDataSet input = executeNode(node.getChild(), context);
        JQuickDataSet result = input.filter(row -> expressionEvaluator.evaluatePredicate(row, node.getPredicate()));
        context.addProcessedRows(input.size());
        return result;
    }
    /**
     * 从 Fragment 中提取列元数据
     */
    private List<JQuickColumnMeta> extractColumnMetasFromFragment(JQuickFragmentProto fragment) {
        try {
            JQuickPhysicalPlanNode rootNode = jQuickprotoService.buildPhysicalNode(fragment.getPlan());
            if (rootNode != null) {
                List<JQuickPhysicalColumn> outputSchema = rootNode.getOutputSchema();
                if (outputSchema != null && !outputSchema.isEmpty()) {
                    List<JQuickColumnMeta> metas = new ArrayList<>();
                    for (JQuickPhysicalColumn col : outputSchema) {
                        metas.add(new JQuickColumnMeta(col.getName(), col.getType() != null ? col.getType() : Object.class, col.getSourceTable() != null ? col.getSourceTable() : ""));
                    }
                    return metas;
                }
            }
        } catch (Exception e) {
            console.warn("Failed to extract column metas from fragment: " + e.getMessage());
        }
        return null;
    }

    /**
     * 执行 Project
     */
    private JQuickDataSet executeProject(JQuickProjectPhysicalNode node, JQuickWorker.JQuickTaskContext context) {
        JQuickDataSet input = executeNode(node.getChild(), context);
        if (node.isStar()) {
            if (node.isDistinct()) {
                List<JQuickRow> distinctRows = input.getRows().stream().distinct().collect(Collectors.toList());
                return new JQuickDataSet(input.getColumns(), distinctRows);
            }
            return input;
        }
        List<JQuickRow> projectedRows = new ArrayList<>();
        for (JQuickRow row : input.getRows()) {
            JQuickRow newRow = new JQuickRow();
            for (JQuickProjectPhysicalNode.SelectItem item : node.getSelectItems()) {
                Object value = expressionEvaluator.evaluateExpression(row, item.getExpression());
                String col="";
                if (item.getExpression() instanceof JQuickColumnRefExpression){
                    col=((JQuickColumnRefExpression) item.getExpression()).getColumnName();
                }
                String alias = item.getAlias() != null ? item.getAlias() : col;
                newRow.put(alias, value);
            }
            projectedRows.add(newRow);
        }

        if (node.isDistinct()) {
            projectedRows = projectedRows.stream().distinct().collect(Collectors.toList());
        }
        List<JQuickColumnMeta> columnMetas = buildColumnMetasForProject(node);
        return new JQuickDataSet(columnMetas, projectedRows);
    }

    /**
     * 执行 Hash Join
     */
    private JQuickDataSet executeHashJoin(JQuickHashJoinPhysicalNode node, JQuickWorker.JQuickTaskContext context) {
        if (node.getJoinType() == com.github.paohaijiao.enums.JQuickJoinType.CROSS || 
            node.getJoinKeys() == null || node.getJoinKeys().isEmpty()) {
            return executeCrossJoin(node, context);
        }
        
        JQuickPhysicalPlanNode buildSide = node.getBuildSide() == JQuickHashJoinPhysicalNode.BuildSide.LEFT ? node.getLeft() : node.getRight();
        JQuickPhysicalPlanNode probeSide = node.getBuildSide() == JQuickHashJoinPhysicalNode.BuildSide.LEFT ? node.getRight() : node.getLeft();
        List<JQuickPhysicalColumn> leftSchema = node.getLeft().getOutputSchema();
        List<JQuickPhysicalColumn> rightSchema = node.getRight().getOutputSchema();
        Map<String, String> aliasToTable = new HashMap<>();
        Map<String, String> columnAliasToActual = new HashMap<>();
        String leftAlias = extractTableAlias(node.getLeft());
        String rightAlias = extractTableAlias(node.getRight());
        if (leftAlias != null) {
            aliasToTable.put(leftAlias, "left");
            for (JQuickPhysicalColumn col : leftSchema) {
                columnAliasToActual.put(leftAlias + "." + col.getName(), col.getName());
            }
        }
        if (rightAlias != null) {
            aliasToTable.put(rightAlias, "right");
            for (JQuickPhysicalColumn col : rightSchema) {
                columnAliasToActual.put(rightAlias + "." + col.getName(), col.getName());
            }
        }
        expressionEvaluator.setAliasContext(aliasToTable, columnAliasToActual);
        try {
        JQuickDataSet buildData = executeNode(buildSide, context);
        // 构建表使用正确的键：如果 buildSide = LEFT，使用左键；如果 buildSide = RIGHT，使用右键
        boolean useLeftKeyForBuild = node.getBuildSide() == JQuickHashJoinPhysicalNode.BuildSide.LEFT;
        Map<Object, List<JQuickRow>> hashTable = buildHashTable(buildData, node, useLeftKeyForBuild);
        // 记录构建表中哪些行被匹配了（用于 RIGHT JOIN 和 FULL JOIN）
        Set<JQuickRow> matchedBuildRows = new HashSet<>();
        JQuickDataSet probeData = executeNode(probeSide, context);
        List<JQuickRow> resultRows = new ArrayList<>();
        // 探测表使用与构建表相反的键
        boolean useLeftKeyForProbe = !useLeftKeyForBuild;
        for (JQuickRow probeRow : probeData.getRows()) {
            Object joinKey = extractJoinKey(probeRow, node, useLeftKeyForProbe);
            List<JQuickRow> matchingRows = hashTable.get(joinKey);
            if (matchingRows != null) {
                for (JQuickRow buildRow : matchingRows) {
                    JQuickRow joined = joinRows(probeRow, buildRow, node.getJoinType(), node.getBuildSide() == JQuickHashJoinPhysicalNode.BuildSide.LEFT);
                    if (joined != null) {
                        if (node.getCondition() == null || expressionEvaluator.evaluatePredicate(joined, node.getCondition())) {
                            resultRows.add(joined);
                            matchedBuildRows.add(buildRow);
                        }
                    }
                }
            } else if (node.getJoinType() == com.github.paohaijiao.enums.JQuickJoinType.LEFT || node.getJoinType() == com.github.paohaijiao.enums.JQuickJoinType.FULL) {
                JQuickRow joined = joinRows(probeRow, null, node.getJoinType(), true);
                if (joined != null) {
                    if (node.getCondition() == null || expressionEvaluator.evaluatePredicate(joined, node.getCondition())) {
                        resultRows.add(joined);
                    }
                }
            }
        }
        // 处理 RIGHT JOIN 和 FULL JOIN：返回构建表中没有被匹配的行
        if (node.getJoinType() == com.github.paohaijiao.enums.JQuickJoinType.RIGHT || node.getJoinType() == com.github.paohaijiao.enums.JQuickJoinType.FULL) {
            for (List<JQuickRow> buildRows : hashTable.values()) {
                for (JQuickRow buildRow : buildRows) {
                    if (!matchedBuildRows.contains(buildRow)) {
                        // 构建表行没有被匹配，返回构建表行（探测表为 null）
                        // 注意：当 buildSide = RIGHT 时，构建表是右表，探测表是左表
                        // joinRows(leftRow, rightRow, joinType, leftIsBuild)
                        // 如果 buildSide = RIGHT，则 leftIsBuild = false，buildRow 是右表行
                        JQuickRow joined;
                        if (node.getBuildSide() == JQuickHashJoinPhysicalNode.BuildSide.RIGHT) {
                            // 右表是构建表，buildRow 是右表行，左表为 null
                            joined = joinRows(null, buildRow, node.getJoinType(), false);
                        } else {
                            // 左表是构建表，buildRow 是左表行，右表为 null
                            joined = joinRows(buildRow, null, node.getJoinType(), true);
                        }
                        if (joined != null) {
                            if (node.getCondition() == null || expressionEvaluator.evaluatePredicate(joined, node.getCondition())) {
                                resultRows.add(joined);
                            }
                        }
                    }
                }
            }
        }
        List<JQuickColumnMeta> columnMetas = convertPhysicalColumnsToMeta(dataConverter.buildOutputSchema(node));
        return new JQuickDataSet(columnMetas, resultRows);
        } finally {
            expressionEvaluator.clearAliasContext();
        }
    }

    /**
     * 执行 Nested Loop Join
     */
    private JQuickDataSet executeNestedLoopJoin(JQuickNestedLoopJoinPhysicalNode node, JQuickWorker.JQuickTaskContext context) {
        JQuickDataSet leftData = executeNode(node.getLeft(), context);
        JQuickDataSet rightData = executeNode(node.getRight(), context);
        List<JQuickRow> resultRows = new ArrayList<>();
        
        // 对于 CROSS JOIN，没有条件，直接计算笛卡尔积
        if (node.getCondition() == null) {
            for (JQuickRow leftRow : leftData.getRows()) {
                for (JQuickRow rightRow : rightData.getRows()) {
                    JQuickRow joined = joinRows(leftRow, rightRow, node.getJoinType(), true);
                    if (joined != null) {
                        resultRows.add(joined);
                    }
                }
            }
        } else {
            // 对于有条件的 JOIN，需要在连接前评估条件
            // 使用临时行来评估条件，避免列名冲突
            
            // 对于 RIGHT JOIN 和 FULL JOIN，需要跟踪哪些右表行找到了匹配
            Set<JQuickRow> matchedRightRows = new HashSet<>();
            
            // 设置列名别名上下文，以便表达式求值器能正确解析列名
            Map<String, String> aliasToTable = new HashMap<>();
            aliasToTable.put("left", "left");
            aliasToTable.put("right", "right");
            
            Map<String, String> columnAliasToActual = new HashMap<>();
            for (String col : leftData.getColumns().stream().map(c -> c.getName()).collect(Collectors.toList())) {
                columnAliasToActual.put("left." + col, col);
            }
            for (String col : rightData.getColumns().stream().map(c -> c.getName()).collect(Collectors.toList())) {
                columnAliasToActual.put("right." + col, col);
            }
            
            expressionEvaluator.setAliasContext(aliasToTable, columnAliasToActual);
            
            try {
            for (JQuickRow leftRow : leftData.getRows()) {
                boolean foundMatch = false;
                for (JQuickRow rightRow : rightData.getRows()) {
                    // 创建临时行用于评估条件
                    JQuickRow tempRow = new JQuickRow();
                    // 添加左右表的所有列
                    // 首先添加左表列
                    for (Map.Entry<String, Object> entry : leftRow.entrySet()) {
                        tempRow.put("left." + entry.getKey(), entry.getValue());
                        tempRow.put(entry.getKey(), entry.getValue()); // 保留原始列名，优先使用
                    }
                    // 然后添加右表列，如果有冲突则不覆盖原始列名
                    for (Map.Entry<String, Object> entry : rightRow.entrySet()) {
                        tempRow.put("right." + entry.getKey(), entry.getValue());
                        // 只在左表没有该列时添加原始列名，避免覆盖
                        if (!tempRow.containsKey(entry.getKey())) {
                            tempRow.put(entry.getKey(), entry.getValue());
                        }
                    }
                    
                    // 评估条件
                    if (expressionEvaluator.evaluatePredicate(tempRow, node.getCondition())) {
                        JQuickRow joined = joinRows(leftRow, rightRow, node.getJoinType(), true);
                        if (joined != null) {
                            resultRows.add(joined);
                            foundMatch = true;
                            matchedRightRows.add(rightRow);
                        }
                    }
                }
                
                // 对于 LEFT JOIN 和 FULL JOIN，如果左表行没有找到匹配，添加左表行（右表列为 null）
                if (!foundMatch && (node.getJoinType() == com.github.paohaijiao.enums.JQuickJoinType.LEFT || 
                                    node.getJoinType() == com.github.paohaijiao.enums.JQuickJoinType.FULL)) {
                    JQuickRow joined = joinRows(leftRow, null, node.getJoinType(), true);
                    if (joined != null) {
                        resultRows.add(joined);
                    }
                }
            }
            
            // 对于 RIGHT JOIN 和 FULL JOIN，添加没有匹配的右表行（左表列为 null）
            if (node.getJoinType() == com.github.paohaijiao.enums.JQuickJoinType.RIGHT || 
                node.getJoinType() == com.github.paohaijiao.enums.JQuickJoinType.FULL) {
                for (JQuickRow rightRow : rightData.getRows()) {
                    if (!matchedRightRows.contains(rightRow)) {
                        JQuickRow joined = joinRows(null, rightRow, node.getJoinType(), true);
                        if (joined != null) {
                            resultRows.add(joined);
                        }
                    }
                }
            }
            } finally {
                expressionEvaluator.clearAliasContext();
            }
        }
        
        // 合并左右两侧的列元数据
        List<JQuickColumnMeta> columnMetas = new ArrayList<>();
        columnMetas.addAll(leftData.getColumns());
        columnMetas.addAll(rightData.getColumns());
        return new JQuickDataSet(columnMetas, resultRows);
    }

    /**
     * 执行 CROSS JOIN（笛卡尔积）
     */
    private JQuickDataSet executeCrossJoin(JQuickHashJoinPhysicalNode node, JQuickWorker.JQuickTaskContext context) {
        JQuickDataSet leftData = executeNode(node.getLeft(), context);
        JQuickDataSet rightData = executeNode(node.getRight(), context);
        List<JQuickRow> resultRows = new ArrayList<>();
        for (JQuickRow leftRow : leftData.getRows()) {
            for (JQuickRow rightRow : rightData.getRows()) {
                JQuickRow joined = joinRows(leftRow, rightRow, node.getJoinType(), true);
                if (joined != null) {
                    if (node.getCondition() == null || expressionEvaluator.evaluatePredicate(joined, node.getCondition())) {
                        resultRows.add(joined);
                    }
                }
            }
        }
        List<JQuickColumnMeta> columnMetas = convertPhysicalColumnsToMeta(dataConverter.buildOutputSchema(node));
        return new JQuickDataSet(columnMetas, resultRows);
    }

    /**
     * 构建哈希表
     */
    private Map<Object, List<JQuickRow>> buildHashTable(JQuickDataSet data, JQuickHashJoinPhysicalNode node, boolean useLeftKey) {
        Map<Object, List<JQuickRow>> hashTable = new HashMap<>();
        List<JQuickHashJoinPhysicalNode.JoinKeyPair> joinKeys = node.getJoinKeys();
        boolean isCompositeKey = joinKeys.size() > 1;
        for (JQuickRow row : data.getRows()) {
            Object key;
            if (isCompositeKey) {
                List<Object> compositeKey = new ArrayList<>();
                for (JQuickHashJoinPhysicalNode.JoinKeyPair keyPair : joinKeys) {
                    JQuickExpression keyExpr = useLeftKey ? keyPair.getLeftKey() : keyPair.getRightKey();
                    Object keyValue = expressionEvaluator.evaluateExpression(row, keyExpr);
                    compositeKey.add(keyValue);
                }
                key = compositeKey;
            } else {
                JQuickExpression keyExpr = useLeftKey ? joinKeys.get(0).getLeftKey() : joinKeys.get(0).getRightKey();
                key = expressionEvaluator.evaluateExpression(row, keyExpr);
            }
            if (key != null) {
                hashTable.computeIfAbsent(key, k -> new ArrayList<>()).add(row);
            }
        }
        return hashTable;
    }

    /**
     * 执行 Hash Aggregate
     */
    private JQuickDataSet executeHashAggregate(JQuickHashAggregatePhysicalNode node, JQuickWorker.JQuickTaskContext context) {
        JQuickDataSet input = executeNode(node.getChild(), context);
        if (node.getGroupKeys() == null || node.getGroupKeys().isEmpty()) {
            return executeGlobalAggregate(input, node);
        } else {
            return executeGroupedAggregate(input, node);
        }
    }

    /**
     * 分组聚合
     */
    private JQuickDataSet executeGroupedAggregate(JQuickDataSet input, JQuickHashAggregatePhysicalNode node) {
        Map<String, List<JQuickRow>> groups = new HashMap<>();
        for (JQuickRow row : input.getRows()) {
            String groupKey = extractGroupKeyString(row, node.getGroupKeys());
            groups.computeIfAbsent(groupKey, k -> new ArrayList<>()).add(row);
        }
        List<JQuickRow> resultRows = new ArrayList<>();
        for (Map.Entry<String, List<JQuickRow>> entry : groups.entrySet()) {
            JQuickRow aggregated = new JQuickRow();
            JQuickRow firstRow = entry.getValue().get(0);
            for (JQuickExpression keyExpr : node.getGroupKeys()) {
                if (keyExpr instanceof JQuickColumnRefExpression) {
                    String colName = ((JQuickColumnRefExpression) keyExpr).getColumnName();
                    aggregated.put(colName, firstRow.get(colName));
                }
            }
            for (JQuickHashAggregatePhysicalNode.AggregateFunction agg : node.getAggregates()) {
                Object value = computeAggregate(entry.getValue(), agg);
                String alias = agg.getAlias() != null ? agg.getAlias() : agg.getFunctionName();
                aggregated.put(alias, value);
            }
            resultRows.add(aggregated);
        }
        if (node.getHavingCondition() != null) {
            Map<String, String> aggToAlias = new HashMap<>();
            for (JQuickHashAggregatePhysicalNode.AggregateFunction agg : node.getAggregates()) {
                StringBuilder sig = new StringBuilder();
                sig.append(agg.getFunctionName().toUpperCase()).append("(");
                if (agg.getArgument() != null) {
                    if (agg.getArgument() instanceof JQuickColumnRefExpression) {
                        sig.append(((JQuickColumnRefExpression) agg.getArgument()).getColumnName());
                    } else {
                        sig.append(agg.getArgument().toString());
                    }
                } else if (agg.isCountStar()) {
                    sig.append("*");
                }
                sig.append(")");
                aggToAlias.put(sig.toString(), agg.getAlias() != null ? agg.getAlias() : agg.getFunctionName());
            }
            JQuickExpression resolvedHaving = resolveAggRefs(node.getHavingCondition(), aggToAlias);
            resultRows = resultRows.stream().filter(row -> expressionEvaluator.evaluatePredicate(row, resolvedHaving)).collect(Collectors.toList());
        }
        List<JQuickColumnMeta> columnMetas = buildColumnMetasForAggregate(node);
        return new JQuickDataSet(columnMetas, resultRows);
    }

    /**
     * 将 HAVING 条件中的聚合函数引用替换为列别名
     */
    private JQuickExpression resolveAggRefs(JQuickExpression expr, Map<String, String> aggToAlias) {
        if (expr instanceof JQuickFunctionCallExpression) {
            JQuickFunctionCallExpression func = (JQuickFunctionCallExpression) expr;
            StringBuilder sig = new StringBuilder();
            sig.append(func.getFunctionName().toUpperCase()).append("(");
            if (func.isStarArg()) {
                sig.append("*");
            } else if (func.getArguments() != null && !func.getArguments().isEmpty()) {
                JQuickExpression arg = func.getArguments().get(0);
                if (arg instanceof JQuickColumnRefExpression) {
                    sig.append(((JQuickColumnRefExpression) arg).getColumnName());
                } else {
                    sig.append(arg.toString());
                }
            }
            sig.append(")");
            String alias = aggToAlias.get(sig.toString());
            if (alias != null) {
                return new JQuickColumnRefExpression(alias);
            }
            return expr;
        } else if (expr instanceof JQuickBinaryExpression) {
            JQuickBinaryExpression binary = (JQuickBinaryExpression) expr;
            return new JQuickBinaryExpression(
                resolveAggRefs(binary.getLeft(), aggToAlias),
                resolveAggRefs(binary.getRight(), aggToAlias),
                binary.getOperator()
            );
        }
        return expr;
    }

    /**
     * 全局聚合
     */
    private JQuickDataSet executeGlobalAggregate(JQuickDataSet input, JQuickHashAggregatePhysicalNode node) {
        JQuickRow result = new JQuickRow();
        for (JQuickHashAggregatePhysicalNode.AggregateFunction agg : node.getAggregates()) {
            Object value = computeAggregate(input.getRows(), agg);
            String alias = agg.getAlias() != null ? agg.getAlias() : agg.getFunctionName();
            result.put(alias, value);
        }
        //构建聚合结果列元数据
        List<JQuickColumnMeta> columnMetas = buildColumnMetasForAggregate(node);
        return new JQuickDataSet(columnMetas, Collections.singletonList(result));
    }

    /**
     * 执行 Sort
     */
    private JQuickDataSet executeSort(JQuickSortPhysicalNode node, JQuickWorker.JQuickTaskContext context) {
        JQuickDataSet input = executeNode(node.getChild(), context);
        List<JQuickRow> sortedRows = new ArrayList<>(input.getRows());
        sortedRows.sort((row1, row2) -> {
            for (JQuickSortPhysicalNode.OrderByItem item : node.getOrderByItems()) {
                Object v1 = row1.get(item.getColumnName());
                Object v2 = row2.get(item.getColumnName());
                int cmp = compareValues(v1, v2, item.isNullsFirst());
                if (cmp != 0) {
                    return item.isAscending() ? cmp : -cmp;
                }
            }
            return 0;
        });
        return new JQuickDataSet(input.getColumns(), sortedRows);
    }

    /**
     * 执行 TopN
     */
    private JQuickDataSet executeTopN(JQuickTopNPhysicalNode node, JQuickWorker.JQuickTaskContext context) {
        JQuickDataSet sorted = executeSort(node, context);
        List<JQuickRow> rows = sorted.getRows();
        int limit = node.getLimit();
        int offset = node.getOffset();
        if (offset >= rows.size()) {
            return new JQuickDataSet(sorted.getColumns(), new ArrayList<>());
        }
        int endIndex = Math.min(offset + limit, rows.size());
        List<JQuickRow> limitedRows = rows.subList(offset, endIndex);
        return new JQuickDataSet(sorted.getColumns(), new ArrayList<>(limitedRows));
    }

    /**
     * 执行 Limit
     */
    private JQuickDataSet executeLimit(JQuickLimitPhysicalNode node, JQuickWorker.JQuickTaskContext context) {
        JQuickDataSet input = executeNode(node.getChild(), context);
        List<JQuickRow> rows = input.getRows();
        int limit = node.getLimit();
        int offset = node.getOffset();
        if (offset >= rows.size()) {
            return new JQuickDataSet(input.getColumns(), new ArrayList<>());
        }
        int endIndex = Math.min(offset + limit, rows.size());
        List<JQuickRow> limitedRows = rows.subList(offset, endIndex);
        return new JQuickDataSet(input.getColumns(), new ArrayList<>(limitedRows));
    }

    /**
     * 执行 Window Function
     */
    private JQuickDataSet executeWindow(JQuickWindowPhysicalNode node, JQuickWorker.JQuickTaskContext context) {
        JQuickDataSet input = executeNode(node.getChild(), context);
        List<JQuickRow> resultRows = new ArrayList<>();
        for (JQuickRow row : input.getRows()) {
            JQuickRow newRow = new JQuickRow(row);
            for (JQuickWindowPhysicalNode.WindowFunction wf : node.getWindowFunctions()) {
                Object value = evaluateWindowFunction(input, row, wf);
                newRow.put(wf.getAlias(), value);
            }
            resultRows.add(newRow);
        }
        List<JQuickColumnMeta> columnMetas = new ArrayList<>(input.getColumns());
        for (JQuickWindowPhysicalNode.WindowFunction wf : node.getWindowFunctions()) {
            columnMetas.add(new JQuickColumnMeta(wf.getAlias(), Object.class, "window"));
        }
        return new JQuickDataSet(columnMetas, resultRows);
    }

    /**
     * 执行 Set Operation
     */
    private JQuickDataSet executeSetOperation(JQuickSetOperationPhysicalNode node, JQuickWorker.JQuickTaskContext context) {
        JQuickDataSet leftData;
        JQuickDataSet rightData;
        
        // 如果 children 为空（分布式场景），从 inputPartitions 读取数据
        if (node.getLeft() == null || node.getRight() == null) {
            JQuickExecuteTaskRequest request = context.getRequest();
            List<JQuickDataSet> datasets = readFromInputPartitionsForSetOperation(request);
            if (datasets.size() >= 2) {
                leftData = datasets.get(0);
                rightData = datasets.get(1);
            } else if (datasets.size() == 1) {
                // 同一个数据集作为左右
                leftData = datasets.get(0);
                rightData = datasets.get(0);
            } else {
                leftData = JQuickDataSet.builder().build();
                rightData = JQuickDataSet.builder().build();
            }
        } else {
            leftData = executeNode(node.getLeft(), context);
            rightData = executeNode(node.getRight(), context);
        }
        
        List<JQuickRow> resultRows;
        switch (node.getOperationType()) {
            case UNION:
                // 使用基于内容的比较实现去重
                resultRows = unionWithContentComparison(leftData.getRows(), rightData.getRows());
                break;
            case UNION_ALL:
                resultRows = new ArrayList<>(leftData.getRows());
                resultRows.addAll(rightData.getRows());
                break;
            case INTERSECT:
                // 使用基于内容的比较实现交集
                resultRows = intersectWithContentComparison(leftData.getRows(), rightData.getRows());
                break;
            case EXCEPT:
                // 使用基于内容的比较实现差集
                resultRows = exceptWithContentComparison(leftData.getRows(), rightData.getRows());
                break;
            default:
                resultRows = new ArrayList<>(leftData.getRows());
        }
        return new JQuickDataSet(leftData.getColumns(), resultRows);
    }
    
    /**
     * 从 input partitions 读取数据用于 Set Operation（分布式场景）
     * 返回多个数据集，分别对应左右操作数
     */
    private List<JQuickDataSet> readFromInputPartitionsForSetOperation(JQuickExecuteTaskRequest request) {
        List<JQuickDataSet> datasets = new ArrayList<>();
        for (JQuickMemoryPartitionProto partition : request.getInputPartitionsList()) {
            if (partition.hasData()) {
                JQuickDataSet partitionData = dataConverter.convertFromProto(partition.getData());
                if (!partitionData.isEmpty()) {
                    datasets.add(partitionData);
                }
            }
        }
        return datasets;
    }
    
    /**
     * 使用基于内容的比较实现 UNION
     */
    private List<JQuickRow> unionWithContentComparison(List<JQuickRow> leftRows, List<JQuickRow> rightRows) {
        List<JQuickRow> result = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        for (JQuickRow row : leftRows) {
            String key = generateRowKey(row);
            if (!seen.contains(key)) {
                seen.add(key);
                result.add(row);
            }
        }
        for (JQuickRow row : rightRows) {
            String key = generateRowKey(row);
            if (!seen.contains(key)) {
                seen.add(key);
                result.add(row);
            }
        }
        return result;
    }
    
    /**
     * 使用基于内容的比较实现 INTERSECT
     */
    private List<JQuickRow> intersectWithContentComparison(List<JQuickRow> leftRows, List<JQuickRow> rightRows) {
        Set<String> rightKeys = new HashSet<>();
        for (JQuickRow row : rightRows) {
            rightKeys.add(generateRowKey(row));
        }
        List<JQuickRow> result = new ArrayList<>();
        for (JQuickRow row : leftRows) {
            String key = generateRowKey(row);
            if (rightKeys.contains(key)) {
                result.add(row);
            }
        }
        return result;
    }
    
    /**
     * 使用基于内容的比较实现 EXCEPT
     */
    private List<JQuickRow> exceptWithContentComparison(List<JQuickRow> leftRows, List<JQuickRow> rightRows) {
        Set<String> rightKeys = new HashSet<>();
        for (JQuickRow row : rightRows) {
            rightKeys.add(generateRowKey(row));
        }
        List<JQuickRow> result = new ArrayList<>();
        for (JQuickRow row : leftRows) {
            String key = generateRowKey(row);
            if (!rightKeys.contains(key)) {
                result.add(row);
            }
        }
        return result;
    }
    
    /**
     * 生成行的内容键（用于基于内容的比较）
     */
    private String generateRowKey(JQuickRow row) {
        StringBuilder key = new StringBuilder();
        // 按键排序以确保一致的顺序
        List<String> keys = new ArrayList<>(row.keySet());
        Collections.sort(keys);
        for (String k : keys) {
            if (key.length() > 0) {
                key.append("|");
            }
            key.append(k).append("=").append(String.valueOf(row.get(k)));
        }
        return key.toString();
    }

    /**
     * 执行 Values
     */
    private JQuickDataSet executeValues(JQuickValuesPhysicalNode node, JQuickWorker.JQuickTaskContext context) {
        List<JQuickRow> rows = new ArrayList<>();
        for (List<Object> rowValues : node.getRows()) {
            JQuickRow row = new JQuickRow();
            for (int i = 0; i < node.getColumnNames().size() && i < rowValues.size(); i++) {
                row.put(node.getColumnNames().get(i), rowValues.get(i));
            }
            rows.add(row);
        }
        List<JQuickColumnMeta> columnMetas = new ArrayList<>();
        for (int i = 0; i < node.getColumnNames().size(); i++) {
            Class<?> type = i < node.getColumnTypes().size() ? node.getColumnTypes().get(i) : Object.class;
            columnMetas.add(new JQuickColumnMeta(node.getColumnNames().get(i), type, "values"));
        }
        return new JQuickDataSet(columnMetas, rows);
    }

    /**
     * 执行递归 Union
     */
    private JQuickDataSet executeRecursiveUnion(JQuickRecursiveUnionPhysicalNode node, JQuickWorker.JQuickTaskContext context) {
        JQuickDataSet result = executeNode(node.getInitialPlan(), context);
        Set<String> seenRowKeys = new HashSet<>();
        for (JQuickRow row : result.getRows()) {
            seenRowKeys.add(createRowKey(row));
        }
        List<JQuickRow> workingRows = new ArrayList<>(result.getRows());
        int depth = 0;
        String ctePartitionId = "cte_partition_" + node.getCteName();
        while (depth < node.getMaxRecursionDepth() && !workingRows.isEmpty()) {
            JQuickDataSet workingDataSet = new JQuickDataSet(result.getColumns(), workingRows);
            JQuickWorker.JQuickMemoryPartition ctePartition = new JQuickWorker.JQuickMemoryPartition(0, 1);
            ctePartition.setPartitionId(ctePartitionId);
            ctePartition.setData(workingDataSet);
            worker.getMemoryPartitions().put(ctePartitionId, ctePartition);
            JQuickDataSourceManager.registerOrReplace(node.getCteName(), workingDataSet);
            try {
                JQuickDataSet newRows = executeNode(node.getRecursivePlan(), context);
                List<JQuickRow> filteredRows = new ArrayList<>();
                for (JQuickRow row : newRows.getRows()) {
                    String rowKey = createRowKey(row);
                    if (!seenRowKeys.contains(rowKey)) {
                        filteredRows.add(row);
                        seenRowKeys.add(rowKey);
                    }
                }
                if (filteredRows.isEmpty()) break;
                List<JQuickRow> allRows = new ArrayList<>(result.getRows());
                allRows.addAll(filteredRows);
                result = new JQuickDataSet(result.getColumns(), allRows);
                workingRows = filteredRows;
                depth++;
            } finally {
                worker.getMemoryPartitions().remove(ctePartitionId);
                JQuickDataSourceManager.removeTable(node.getCteName());
            }
        }

        return result;
    }

    /**
     * 创建行的唯一键（基于内容）
     */
    private String createRowKey(JQuickRow row) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> entry : row.entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append(";");
        }
        return sb.toString();
    }

    /**
     * 执行 Exchange - 数据分发或接收
     */
    private JQuickDataSet executeExchange(JQuickExchangePhysicalNode node, JQuickWorker.JQuickTaskContext context) {
        console.info("=== Exchange Debug ===");
        console.info("Exchange type: " + node.getExchangeType());
        console.info("Target parallelism: " + node.getTargetParallelism());
        console.info("Partition strategy: " + node.getPartitionStrategy());
        // GATHER Exchange: 收集数据（从 gRPC 接收的数据中收集）
        if (node.getExchangeType() == JQuickExchangeType.GATHER) {
            console.info("GATHER Exchange: collecting data from gRPC received partitions");
            List<JQuickRow> allRows = new ArrayList<>();
            List<JQuickColumnMeta> columns = null;
            Set<String> rowHashes = new HashSet<>();
            for (String partitionId : worker.getAllReceivedPartitions()) {
                JQuickDataSet partitionData = worker.getReceivedPartitionData(partitionId);
                if (partitionData != null && !partitionData.isEmpty()) {
                    for (JQuickRow row : partitionData.getRows()) {
                        String rowHash = row.toString();
                        if (!rowHashes.contains(rowHash)) {
                            rowHashes.add(rowHash);
                            allRows.add(row);
                        }
                    }
                    if (columns == null) {
                        columns = partitionData.getColumns();
                    }
                    console.info("GATHER collected " + partitionData.size() + " rows from partition " + partitionId + ", total unique: " + allRows.size());
                }
            }
            if (!allRows.isEmpty() && columns != null) {
                console.info("GATHER Exchange: returning " + allRows.size() + " unique rows from gRPC");
                return new JQuickDataSet(columns, allRows);
            }
            // 如果没有从 gRPC 收到数据，尝试直接执行子节点（本地测试场景）
            console.info("GATHER Exchange: no data from gRPC, trying child node");
            JQuickDataSet childData = executeNode(node.getChild(), context);
            console.info("GATHER Exchange: collected " + childData.size() + " rows from child node");
            return childData;
        }
        // RECEIVE Exchange: 从 gRPC 接收的数据中收集数据（由上游 Worker 发送过来）
        if (node.getExchangeType() == JQuickExchangeType.RECEIVE) {
            console.info("RECEIVE Exchange: collecting data from received partitions");
            List<JQuickRow> allRows = new ArrayList<>();
            List<JQuickColumnMeta> columns = null;
            // 收集所有通过 gRPC 接收到的分区数据
            for (String partitionId : worker.getAllReceivedPartitions()) {
                JQuickDataSet partitionData = worker.getReceivedPartitionData(partitionId);
                if (partitionData != null && !partitionData.isEmpty()) {
                    allRows.addAll(partitionData.getRows());
                    if (columns == null) {
                        columns = partitionData.getColumns();
                    }
                    console.info("Collected " + partitionData.size() + " rows from partition " + partitionId);
                }
            }
            console.info("RECEIVE Exchange: total rows collected: " + allRows.size());
            // 如果没有列信息，返回空数据集
            if (allRows.isEmpty() || columns == null) {
                console.warn("RECEIVE Exchange returned empty result!");
                return JQuickDataSet.builder().build();
            }
            return new JQuickDataSet(columns, allRows);
        }
        // SHUFFLE/BROADCAST Exchange: 发送数据到其他 Worker
        // 但首先检查是否有 input partitions（Fragment 输入边界）
        if (context != null && context.getRequest() != null && context.getRequest().getInputPartitionsCount() > 0) {
            console.info("SHUFFLE Exchange: reading from input partitions (Fragment input boundary)");
            List<JQuickRow> allRows = new ArrayList<>();
            List<JQuickColumnMeta> columns = null;
            for (JQuickMemoryPartitionProto partition : context.getRequest().getInputPartitionsList()) {
                JQuickDataSet partitionData = dataConverter.convertFromProto(partition.getData());
                if (partitionData != null && !partitionData.isEmpty()) {
                    allRows.addAll(partitionData.getRows());
                    if (columns == null) {
                        columns = partitionData.getColumns();
                    }
                    console.info("Read " + partitionData.size() + " rows from input partition " + partition.getPartitionId());
                }
            }
            if (!allRows.isEmpty() && columns != null) {
                console.info("SHUFFLE Exchange: returning " + allRows.size() + " rows from input partitions");
                return new JQuickDataSet(columns, allRows);
            }
            console.info("SHUFFLE Exchange: no data from input partitions, falling through to child execution");
        }
        JQuickDataSet input = executeNode(node.getChild(), context);//获取数据
        console.info("Input data rows: " + input.size());
        if (input.isEmpty()) {
            console.warn("Input data is empty, skipping send");
            return JQuickDataSet.builder().build();
        }
        List<JQuickWorker.JQuickMemoryPartition> partitions = partitionManager.partitionData(input, node, expressionEvaluator, node.getTargetParallelism());//进行分区
        console.info("Created " + partitions.size() + " partitions");
        for (int i = 0; i < partitions.size(); i++) {//获取分区数据信息
            JQuickWorker.JQuickMemoryPartition partition = partitions.get(i);
            console.info("Partition " + i + ": " + partition.getData().size() + " rows");
        }
        // 发送所有分区数据到目标 Worker（包括当前 Worker）
        int currentWorkerIndex = worker.getWorkerIndex();
        console.info("Current worker index: " + currentWorkerIndex);
        for (JQuickWorker.JQuickMemoryPartition partition : partitions) {
            int targetWorkerId = partition.getIndex() % node.getTargetParallelism();
            console.info("Partition " + partition.getIndex() + " target worker: " + targetWorkerId);
            // 发送所有分区到目标 Worker（包括当前 Worker）
            partitionManager.sendToWorker(partition, node.getTargetParallelism(), node.getExchangeType(), worker);
            console.info("Sent partition " + partition.getIndex() + " to worker " + targetWorkerId);
        }
        // SHUFFLE Exchange 不返回数据，所有数据都通过 gRPC 发送
        console.info("SHUFFLE Exchange: all partitions sent via gRPC, returning empty");
        return JQuickDataSet.builder().build();
    }
    /**
     * 将 JQuickPhysicalColumn 列表转换为 JQuickColumnMeta 列表
     */
    private List<JQuickColumnMeta> convertPhysicalColumnsToMeta(List<JQuickPhysicalColumn> physicalColumns) {
        List<JQuickColumnMeta> columnMetas = new ArrayList<>();
        if (physicalColumns != null) {
            for (JQuickPhysicalColumn col : physicalColumns) {
                columnMetas.add(new JQuickColumnMeta(col.getName(), col.getType(), col.getSourceTable() != null ? col.getSourceTable() : ""));
            }
        }
        return columnMetas;
    }

    /**
     * 为 Project 节点构建列元数据
     */
    private List<JQuickColumnMeta> buildColumnMetasForProject(JQuickProjectPhysicalNode node) {
        List<JQuickColumnMeta> columnMetas = new ArrayList<>();
        if (node.getSelectItems() == null) {
            return columnMetas;
        }
        for (JQuickProjectPhysicalNode.SelectItem item : node.getSelectItems()) {
            String name = item.getAlias() != null ? item.getAlias() : generateColumnName(item.getExpression());
            Class<?> type = inferExpressionType(item.getExpression());
            columnMetas.add(new JQuickColumnMeta(name, type, "project"));
        }
        return columnMetas;
    }

    /**
     * 为 Aggregate 节点构建列元数据
     */
    private List<JQuickColumnMeta> buildColumnMetasForAggregate(JQuickHashAggregatePhysicalNode node) {
        List<JQuickColumnMeta> columnMetas = new ArrayList<>();
        // 添加分组键列
        if (node.getGroupKeys() != null) {
            for (JQuickExpression expr : node.getGroupKeys()) {
                String name = extractColumnName(expr);
                columnMetas.add(new JQuickColumnMeta(name, Object.class, "group"));
            }
        }
        // 添加聚合函数列
        if (node.getAggregates() != null) {
            for (JQuickHashAggregatePhysicalNode.AggregateFunction agg : node.getAggregates()) {
                String name = agg.getAlias() != null ? agg.getAlias() : agg.getFunctionName();
                Class<?> type = inferAggregateType(agg);
                columnMetas.add(new JQuickColumnMeta(name, type, "aggregate"));
            }
        }

        return columnMetas;
    }

    /**
     * 从表达式提取列名
     */
    private String extractColumnName(JQuickExpression expr) {
        if (expr instanceof JQuickColumnRefExpression) {
            return ((JQuickColumnRefExpression) expr).getColumnName();
        }
        return "expr";
    }

    /**
     * 推断聚合函数返回类型
     */
    private Class<?> inferAggregateType(JQuickHashAggregatePhysicalNode.AggregateFunction agg) {
        String funcName = agg.getFunctionName().toLowerCase();
        switch (funcName) {
            case "count":
                return Long.class;
            case "sum":
            case "avg":
                return Double.class;
            case "max":
            case "min":
                return Object.class;
            default:
                return Object.class;
        }
    }

    private Object extractJoinKey(JQuickRow row, JQuickHashJoinPhysicalNode node, boolean useLeftKey) {
        List<JQuickHashJoinPhysicalNode.JoinKeyPair> joinKeys = node.getJoinKeys();
        if (joinKeys.isEmpty()) return null;
        if (joinKeys.size() == 1) {
            JQuickHashJoinPhysicalNode.JoinKeyPair keyPair = joinKeys.get(0);
            JQuickExpression keyExpr = useLeftKey ? keyPair.getLeftKey() : keyPair.getRightKey();
            return expressionEvaluator.evaluateExpression(row, keyExpr);
        } else {
            List<Object> compositeKey = new ArrayList<>();
            for (JQuickHashJoinPhysicalNode.JoinKeyPair keyPair : joinKeys) {
                JQuickExpression keyExpr = useLeftKey ? keyPair.getLeftKey() : keyPair.getRightKey();
                Object keyValue = expressionEvaluator.evaluateExpression(row, keyExpr);
                compositeKey.add(keyValue);
            }
            return compositeKey;
        }
    }

    private JQuickRow extractGroupKey(JQuickRow row, List<JQuickExpression> groupKeys) {
        JQuickRow key = new JQuickRow();
        for (JQuickExpression expr : groupKeys) {
            if (expr instanceof JQuickColumnRefExpression) {
                String colName = ((JQuickColumnRefExpression) expr).getColumnName();
                key.put(colName, row.get(colName));
            }
        }
        return key;
    }

    private String extractGroupKeyString(JQuickRow row, List<JQuickExpression> groupKeys) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < groupKeys.size(); i++) {
            if (i > 0) sb.append("|");
            JQuickExpression expr = groupKeys.get(i);
            if (expr instanceof JQuickColumnRefExpression) {
                String colName = ((JQuickColumnRefExpression) expr).getColumnName();
                Object val = row.get(colName);
                sb.append(val != null ? val.toString() : "null");
            }
        }
        return sb.toString();
    }

    private Object computeAggregate(List<JQuickRow> rows, JQuickHashAggregatePhysicalNode.AggregateFunction agg) {
        String funcName = agg.getFunctionName().toLowerCase();
        JQuickHashAggregatePhysicalNode.AggregateStage stage = agg.getInternalStage();
        switch (funcName) {
            case "count":
                if (stage == JQuickHashAggregatePhysicalNode.AggregateStage.FINAL) {
                    return rows.stream().mapToLong(r -> {
                        Object val = expressionEvaluator.evaluateExpression(r, agg.getArgument());
                        return val instanceof Number ? ((Number) val).longValue() : 0L;
                    }).sum();
                }
                if (agg.isDistinct()) {
                    return rows.stream()
                            .map(r -> expressionEvaluator.evaluateExpression(r, agg.getArgument()))
                            .distinct().count();
                }
                return (long) rows.size();
            case "sum":
                if (stage == JQuickHashAggregatePhysicalNode.AggregateStage.FINAL) {
                    return rows.stream().mapToDouble(r -> {
                        Object val = expressionEvaluator.evaluateExpression(r, agg.getArgument());
                        return val instanceof Number ? ((Number) val).doubleValue() : 0.0;
                    }).sum();
                }
                return rows.stream().mapToDouble(r -> {
                    Object val = expressionEvaluator.evaluateExpression(r, agg.getArgument());
                    return val instanceof Number ? ((Number) val).doubleValue() : 0.0;
                }).sum();
            case "avg":
                if (stage == JQuickHashAggregatePhysicalNode.AggregateStage.FINAL) {
                    double sum = rows.stream().mapToDouble(r -> {
                        Object val = r.get(agg.getAlias() + "_sum");
                        return val instanceof Number ? ((Number) val).doubleValue() : 0.0;
                    }).sum();
                    long count = rows.stream().mapToLong(r -> {
                        Object val = r.get(agg.getAlias() + "_count");
                        return val instanceof Number ? ((Number) val).longValue() : 0L;
                    }).sum();
                    return count > 0 ? sum / count : 0.0;
                }
                return rows.stream().mapToDouble(r -> {
                    Object val = expressionEvaluator.evaluateExpression(r, agg.getArgument());
                    return val instanceof Number ? ((Number) val).doubleValue() : 0.0;
                }).average().orElse(0.0);
            case "max":
                return rows.stream()
                        .map(r -> expressionEvaluator.evaluateExpression(r, agg.getArgument()))
                        .max((a, b) -> compareValues(a, b, false)).orElse(null);
            case "min":
                return rows.stream().map(r -> expressionEvaluator.evaluateExpression(r, agg.getArgument())).min((a, b) -> compareValues(a, b, false)).orElse(null);
            case "divide":
                double divSum = rows.stream().mapToDouble(r -> {
                    Object val = r.get(agg.getAlias() + "_sum");
                    return val instanceof Number ? ((Number) val).doubleValue() : 0.0;
                }).sum();
                long divCount = rows.stream().mapToLong(r -> {
                    Object val = r.get(agg.getAlias() + "_count");
                    return val instanceof Number ? ((Number) val).longValue() : 0L;
                }).sum();
                return divCount > 0 ? divSum / divCount : 0.0;
            default:
                List<Object> args = rows.stream().map(r -> expressionEvaluator.evaluateExpression(r, agg.getArgument())).collect(Collectors.toList());
                return expressionEvaluator.evaluateFunction(funcName, args);
        }
    }

    private Object evaluateWindowFunction(JQuickDataSet data, JQuickRow currentRow, JQuickWindowPhysicalNode.WindowFunction wf) {
        String funcName = wf.getFunctionName().toLowerCase();
        List<JQuickRow> allRows = data.getRows();
        int currentIdx = allRows.indexOf(currentRow);
        JQuickWindowPhysicalNode.WindowSpec windowSpec = wf.getWindowSpec();
        List<JQuickRow> windowRows;
        if (windowSpec != null && windowSpec.getPartitionKeys() != null && !windowSpec.getPartitionKeys().isEmpty()) {
            windowRows = getPartitionRows(allRows, currentRow, windowSpec);
        } else {
            windowRows = allRows;
        }
        if (windowSpec != null && windowSpec.getOrderKeys() != null && !windowSpec.getOrderKeys().isEmpty()) {
            windowRows = sortWindowRows(windowRows, windowSpec);
        }
        int windowIdx = windowRows.indexOf(currentRow);
        switch (funcName) {
            case "row_number":
                return (long) (windowIdx + 1);
            case "rank":
                Object currentOrderValue = getOrderValue(windowRows.get(windowIdx), windowSpec);
                int rank = 1;
                for (int i = 0; i < windowIdx; i++) {
                    Object prevValue = getOrderValue(windowRows.get(i), windowSpec);
                    if (!Objects.equals(currentOrderValue, prevValue)) {
                        rank = i + 2;
                    }
                }
                return (long) rank;
            case "dense_rank":
                // 计算密集排名（无跳跃）
                Object denseCurrentValue = getOrderValue(windowRows.get(windowIdx), windowSpec);
                int denseRank = 1;
                for (int i = 0; i < windowIdx; i++) {
                    Object prevValue = getOrderValue(windowRows.get(i), windowSpec);
                    if (!Objects.equals(denseCurrentValue, prevValue)) {
                        denseRank++;
                    }
                }
                return (long) denseRank;
            case "lead":
                if (windowIdx + 1 < windowRows.size()) {
                    return expressionEvaluator.evaluateExpression(windowRows.get(windowIdx + 1), wf.getArgument());
                }
                return null;
            case "lag":
                if (windowIdx - 1 >= 0) {
                    return expressionEvaluator.evaluateExpression(windowRows.get(windowIdx - 1), wf.getArgument());
                }
                return null;
            case "count":
                return (long) windowRows.size();
            case "sum":
                if (wf.getArgument() == null) {
                    return null;
                }
                double sum = 0;
                for (JQuickRow row : windowRows) {
                    Object val = expressionEvaluator.evaluateExpression(row, wf.getArgument());
                    if (val instanceof Number) {
                        sum += ((Number) val).doubleValue();
                    }
                }
                return sum;
            case "avg":
                if (wf.getArgument() == null) {
                    return null;
                }
                double total = 0;
                int count = 0;
                for (JQuickRow row : windowRows) {
                    Object val = expressionEvaluator.evaluateExpression(row, wf.getArgument());
                    if (val instanceof Number) {
                        total += ((Number) val).doubleValue();
                        count++;
                    }
                }
                return count > 0 ? total / count : null;
            case "max":
                if (wf.getArgument() == null) {
                    return null;
                }
                Double maxVal = null;
                for (JQuickRow row : windowRows) {
                    Object val = expressionEvaluator.evaluateExpression(row, wf.getArgument());
                    if (val instanceof Number) {
                        double num = ((Number) val).doubleValue();
                        if (maxVal == null || num > maxVal) {
                            maxVal = num;
                        }
                    }
                }
                return maxVal;
            case "min":
                if (wf.getArgument() == null) {
                    return null;
                }
                Double minVal = null;
                for (JQuickRow row : windowRows) {
                    Object val = expressionEvaluator.evaluateExpression(row, wf.getArgument());
                    if (val instanceof Number) {
                        double num = ((Number) val).doubleValue();
                        if (minVal == null || num < minVal) {
                            minVal = num;
                        }
                    }
                }
                return minVal;
            default:
                List<Object> args = new ArrayList<>();
                if (wf.getArgument() != null) {
                    args.add(expressionEvaluator.evaluateExpression(currentRow, wf.getArgument()));
                }
                return expressionEvaluator.evaluateFunction(funcName, args);
        }
    }
    
    /**
     * 获取当前行所在分区的所有行
     */
    private List<JQuickRow> getPartitionRows(List<JQuickRow> allRows, JQuickRow currentRow, JQuickWindowPhysicalNode.WindowSpec windowSpec) {
        List<JQuickRow> partitionRows = new ArrayList<>();
        for (JQuickRow row : allRows) {
            if (isSamePartition(currentRow, row, windowSpec.getPartitionKeys())) {
                partitionRows.add(row);
            }
        }
        return partitionRows;
    }
    
    /**
     * 判断两行是否在同一个分区
     */
    private boolean isSamePartition(JQuickRow row1, JQuickRow row2, List<JQuickExpression> partitionKeys) {
        for (JQuickExpression key : partitionKeys) {
            if (!(key instanceof JQuickColumnRefExpression)) {
                continue;
            }
            String columnName = ((JQuickColumnRefExpression) key).getColumnName();
            Object val1 = row1.get(columnName);
            Object val2 = row2.get(columnName);
            if (!Objects.equals(val1, val2)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * 对窗口内的行进行排序
     */
    private List<JQuickRow> sortWindowRows(List<JQuickRow> rows, JQuickWindowPhysicalNode.WindowSpec windowSpec) {
        List<JQuickRow> sortedRows = new ArrayList<>(rows);
        sortedRows.sort((row1, row2) -> {
            for (JQuickSortPhysicalNode.OrderByItem orderItem : windowSpec.getOrderKeys()) {
                Object v1 = row1.get(orderItem.getColumnName());
                Object v2 = row2.get(orderItem.getColumnName());
                int cmp = compareValues(v1, v2, orderItem.isNullsFirst());
                if (cmp != 0) {
                    return orderItem.isAscending() ? cmp : -cmp;
                }
            }
            return 0;
        });
        return sortedRows;
    }
    
    /**
     * 获取用于排序的值
     */
    private Object getOrderValue(JQuickRow row, JQuickWindowPhysicalNode.WindowSpec windowSpec) {
        if (windowSpec == null || windowSpec.getOrderKeys() == null || windowSpec.getOrderKeys().isEmpty()) {
            return null;
        }
        // 返回第一个排序键的值
        return row.get(windowSpec.getOrderKeys().get(0).getColumnName());
    }

    private JQuickRow joinRows(JQuickRow leftRow, JQuickRow rightRow, com.github.paohaijiao.enums.JQuickJoinType joinType, boolean leftIsBuild) {
        JQuickRow result = new JQuickRow();
        if (leftIsBuild) {
            if (leftRow != null) result.putAll(leftRow);
            if (rightRow != null) result.putAll(rightRow);
        } else {
            if (rightRow != null) result.putAll(rightRow);
            if (leftRow != null) result.putAll(leftRow);
        }
        return result;
    }

    private JQuickDataSet applyFilter(JQuickDataSet data, JQuickExpression predicate) {
        return data.filter(row -> expressionEvaluator.evaluatePredicate(row, predicate));
    }

    private int compareValues(Object v1, Object v2, boolean nullsFirst) {
        if (v1 == null && v2 == null) return 0;
        if (v1 == null) return nullsFirst ? -1 : 1;
        if (v2 == null) return nullsFirst ? 1 : -1;
        if (v1 instanceof Number || v2 instanceof Number) {
            double d1 = Double.parseDouble(v1.toString());
            double d2 = Double.parseDouble(v2.toString());
            return Double.compare(d1, d2);
        }
        if (v1 instanceof String || v2 instanceof String) {
            String s1 = v1.toString();
            String s2 = v2.toString();
            return s1.compareTo(s2);
        }
        if (v1 instanceof Comparable && v2 instanceof Comparable) {
            int cmp = ((Comparable<Object>) v1).compareTo(v2);
            return cmp;
        }
        return v1.toString().compareTo(v2.toString());
    }

    private JQuickDataSet readFromDataSource(String tableName) {
        if (tableName == null) {
            console.warn("readFromDataSource - tableName is null");
            return JQuickDataSet.builder().build();
        }
        JQuickDataSet tableData = JQuickDataSourceManager.getTable(tableName);
        if (tableData == null) {
            console.warn("readFromDataSource - table not found: " + tableName);
            console.info("Available tables: " + JQuickDataSourceManager.getTableNames());
            return JQuickDataSet.builder().build();
        }
        console.info("readFromDataSource - table found: " + tableName + ", rows: " + tableData.size());
        return tableData;
    }

    private JQuickDataSet readFromMemoryPartition(String partitionId) {
        JQuickWorker.JQuickMemoryPartition partition = worker.getMemoryPartitions().get(partitionId);
        if (partition == null) {
            return JQuickDataSet.builder().build();
        }
        JQuickDataSet data = partition.getData();
        return data;
    }

    private String generateColumnName(JQuickExpression expr) {
        if (expr == null) return "col";
        if (expr instanceof JQuickColumnRefExpression) {
            return ((JQuickColumnRefExpression) expr).getColumnName();
        }
        if (expr instanceof JQuickFunctionCallExpression) {
            return ((JQuickFunctionCallExpression) expr).getFunctionName();
        }
        return "expr";
    }

    private Class<?> inferExpressionType(JQuickExpression expr) {
        if (expr == null) return Object.class;
        if (expr instanceof JQuickLiteralExpression) {
            Object value = ((JQuickLiteralExpression) expr).getValue();
            return value != null ? value.getClass() : Object.class;
        }
        if (expr instanceof JQuickFunctionCallExpression) {
            String functionName = ((JQuickFunctionCallExpression) expr).getFunctionName().toLowerCase();
            if (functionName.equals("to_int") || functionName.equals("toInt")) return Integer.class;
            if (functionName.equals("to_long") || functionName.equals("toLong")) return Long.class;
            if (functionName.equals("to_double") || functionName.equals("toDouble")) return Double.class;
            if (functionName.equals("to_string") || functionName.equals("toString")) return String.class;
            if (functionName.equals("to_boolean") || functionName.equals("toBoolean")) return Boolean.class;
            if (functionName.equals("year") || functionName.equals("month") || functionName.equals("day"))
                return Long.class;
            if (functionName.equals("now") || functionName.equals("current_date")) return java.time.LocalDate.class;
        }
        return Object.class;
    }


    /**
     * 提取表别名
     */
    private String extractTableAlias(JQuickPhysicalPlanNode node) {
        if (node instanceof JQuickTableScanPhysicalNode) {
            return ((JQuickTableScanPhysicalNode) node).getAlias();
        }
        for (JQuickPhysicalPlanNode child : node.getChildren()) {
            String alias = extractTableAlias(child);
            if (alias != null) return alias;
        }
        return null;
    }



}