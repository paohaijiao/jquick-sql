package com.github.paohaijiao.distributed.worker;

import com.github.paohaijiao.console.JConsole;
import com.github.paohaijiao.datasource.JQuickDataSourceManager;
import com.github.paohaijiao.enums.JQuickBinaryOperator;
import com.github.paohaijiao.enums.JQuickExchangeType;
import com.github.paohaijiao.enums.JQuickPartitionStrategy;
import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.expression.domain.JQuickBinaryExpression;
import com.github.paohaijiao.expression.domain.JQuickColumnRefExpression;
import com.github.paohaijiao.expression.domain.JQuickFunctionCallExpression;
import com.github.paohaijiao.expression.domain.JQuickLiteralExpression;
import com.github.paohaijiao.physical.JQuickPhysicalPlanNode;
import com.github.paohaijiao.physical.domain.JQuickPhysicalColumn;
import com.github.paohaijiao.physical.node.*;
import com.github.paohaijiao.proto.*;
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


    public JQuickNodeExecutor(JQuickWorker worker, JQuickExpressionEvaluator expressionEvaluator, JQuickPartitionManager partitionManager, JQuickDataConverter dataConverter) {
        this.worker = worker;
        this.expressionEvaluator = expressionEvaluator;
        this.partitionManager = partitionManager;
        this.dataConverter = dataConverter;
    }

    /**
     * 执行片段的核心方法
     */
    public JQuickDataSet executeFragment(JQuickFragmentProto fragment, JQuickWorker.JQuickTaskContext context) {
        JQuickPhysicalPlanNode rootNode = buildPhysicalNode(fragment.getPlan());
        JQuickDataSet result = executeNode(rootNode, context);
        
        // 如果执行结果已经有数据，直接返回
        if (!result.isEmpty()) {
            console.info("executeFragment: returning " + result.size() + " rows from executeNode");
            return result;
        }
        
        // 如果是 SINK Fragment，尝试从 gRPC 接收的数据中收集结果
        if (fragment.getType() == JQuickFragmentTypeProto.FRAGMENT_SINK) {
            console.info("executeFragment: SINK Fragment, trying to collect from gRPC");
            List<JQuickRow> allRows = new ArrayList<>();
            List<JQuickColumnMeta> columns = null;
            for (String partitionId : worker.getAllReceivedPartitions()) {
                JQuickDataSet data = worker.getReceivedPartitionData(partitionId);
                if (data != null && !data.isEmpty()) {
                    if (columns == null && !data.getColumns().isEmpty()) {
                        columns = data.getColumns();
                    }
                    allRows.addAll(data.getRows());
                    console.info("SINK Fragment collected " + data.size() + " rows from partition " + partitionId);
                }
            }
            
            // 如果接收到数据，合并后返回
            if (!allRows.isEmpty() && columns != null) {
                console.info("SINK Fragment total collected " + allRows.size() + " rows from gRPC received data");
                return new JQuickDataSet(columns, allRows);
            }
            
            console.warn("SINK Fragment received no data from gRPC!");
        }
        
        return result;
    }

    /**
     * 递归执行物理计划节点
     */
    public JQuickDataSet executeNode(JQuickPhysicalPlanNode node, JQuickWorker.JQuickTaskContext context) {
        if (node == null) {
            return JQuickDataSet.builder().build();
        }
        if (node instanceof JQuickTableScanPhysicalNode) {
            return executeTableScan((JQuickTableScanPhysicalNode) node, context);
        } else if (node instanceof JQuickFilterPhysicalNode) {
            return executeFilter((JQuickFilterPhysicalNode) node, context);
        } else if (node instanceof JQuickProjectPhysicalNode) {
            return executeProject((JQuickProjectPhysicalNode) node, context);
        } else if (node instanceof JQuickHashJoinPhysicalNode) {
            return executeHashJoin((JQuickHashJoinPhysicalNode) node, context);
        } else if (node instanceof JQuickNestedLoopJoinPhysicalNode) {
            return executeNestedLoopJoin((JQuickNestedLoopJoinPhysicalNode) node, context);
        } else if (node instanceof JQuickHashAggregatePhysicalNode) {
            return executeHashAggregate((JQuickHashAggregatePhysicalNode) node, context);
        } else if (node instanceof JQuickSortPhysicalNode) {
            return executeSort((JQuickSortPhysicalNode) node, context);
        } else if (node instanceof JQuickExchangePhysicalNode) {
            return executeExchange((JQuickExchangePhysicalNode) node, context);
        } else if (node instanceof JQuickLimitPhysicalNode) {
            return executeLimit((JQuickLimitPhysicalNode) node, context);
        } else if (node instanceof JQuickTopNPhysicalNode) {
            return executeTopN((JQuickTopNPhysicalNode) node, context);
        } else if (node instanceof JQuickWindowPhysicalNode) {
            return executeWindow((JQuickWindowPhysicalNode) node, context);
        } else if (node instanceof JQuickSetOperationPhysicalNode) {
            return executeSetOperation((JQuickSetOperationPhysicalNode) node, context);
        } else if (node instanceof JQuickValuesPhysicalNode) {
            return executeValues((JQuickValuesPhysicalNode) node, context);
        } else if (node instanceof JQuickEmptyPhysicalNode) {
            return JQuickDataSet.builder().build();
        } else if (node instanceof JQuickRecursiveUnionPhysicalNode) {
            return executeRecursiveUnion((JQuickRecursiveUnionPhysicalNode) node, context);
        }
        throw new UnsupportedOperationException("Unknown node type: " + node.getNodeType());
    }

    /**
     * 执行 TableScan
     */
    private JQuickDataSet executeTableScan(JQuickTableScanPhysicalNode node, JQuickWorker.JQuickTaskContext context) {
        String tableName = node.getTableName();
        Set<String> requiredColumns = node.getRequiredColumns();
        JQuickDataSet data;
        boolean useMemoryDist = true;
        if (node.getPartitionInfo() != null && useMemoryDist) {
            data = readFromMemoryPartition(tableName, requiredColumns);
        } else {
            data = readFromDataSource(tableName, requiredColumns);
        }
        if (node.getFilterPredicate() != null) {
            data = applyFilter(data, node.getFilterPredicate());
        }
        context.addProcessedRows(data.size());
        return data;
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
     * 执行 Project
     */
    private JQuickDataSet executeProject(JQuickProjectPhysicalNode node, JQuickWorker.JQuickTaskContext context) {
        JQuickDataSet input = executeNode(node.getChild(), context);
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
        //将 JQuickPhysicalColumn 转换为 JQuickColumnMeta
        List<JQuickColumnMeta> columnMetas = buildColumnMetasForProject(node);
        return new JQuickDataSet(columnMetas, projectedRows);
    }

    /**
     * 执行 Hash Join
     */
    private JQuickDataSet executeHashJoin(JQuickHashJoinPhysicalNode node, JQuickWorker.JQuickTaskContext context) {
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
        Map<Object, List<JQuickRow>> hashTable = buildHashTable(buildData, node);
        JQuickDataSet probeData = executeNode(probeSide, context);
        List<JQuickRow> resultRows = new ArrayList<>();
        for (JQuickRow probeRow : probeData.getRows()) {
            Object joinKey = extractJoinKey(probeRow, node, false);
            List<JQuickRow> matchingRows = hashTable.get(joinKey);
            if (matchingRows != null) {
                for (JQuickRow buildRow : matchingRows) {
                    JQuickRow joined = joinRows(probeRow, buildRow, node.getJoinType(), node.getBuildSide() == JQuickHashJoinPhysicalNode.BuildSide.LEFT);
                    if (joined != null) resultRows.add(joined);
                }
            } else if (node.getJoinType() == com.github.paohaijiao.enums.JQuickJoinType.LEFT || node.getJoinType() == com.github.paohaijiao.enums.JQuickJoinType.FULL) {
                JQuickRow joined = joinRows(probeRow, null, node.getJoinType(), true);
                if (joined != null) resultRows.add(joined);
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
        for (JQuickRow leftRow : leftData.getRows()) {
            for (JQuickRow rightRow : rightData.getRows()) {
                JQuickRow joined = joinRows(leftRow, rightRow, node.getJoinType(), true);
                if (joined != null) {
                    if (node.getCondition() == null ||
                            expressionEvaluator.evaluatePredicate(joined, node.getCondition())) {
                        resultRows.add(joined);
                    }
                }
            }
        }
        //合并左右两侧的列元数据
        List<JQuickColumnMeta> columnMetas = new ArrayList<>();
        columnMetas.addAll(leftData.getColumns());
        columnMetas.addAll(rightData.getColumns());
        return new JQuickDataSet(columnMetas, resultRows);
    }

    /**
     * 构建哈希表
     */
    private Map<Object, List<JQuickRow>> buildHashTable(JQuickDataSet data, JQuickHashJoinPhysicalNode node) {
        Map<Object, List<JQuickRow>> hashTable = new HashMap<>();
        List<JQuickHashJoinPhysicalNode.JoinKeyPair> joinKeys = node.getJoinKeys();
        boolean isCompositeKey = joinKeys.size() > 1;
        for (JQuickRow row : data.getRows()) {
            Object key;
            if (isCompositeKey) {
                List<Object> compositeKey = new ArrayList<>();
                for (JQuickHashJoinPhysicalNode.JoinKeyPair keyPair : joinKeys) {
                    Object keyValue = expressionEvaluator.evaluateExpression(row, keyPair.getLeftKey());
                    compositeKey.add(keyValue);
                }
                key = compositeKey;
            } else {
                key = expressionEvaluator.evaluateExpression(row, joinKeys.get(0).getLeftKey());
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
        Map<JQuickRow, List<JQuickRow>> groups = new HashMap<>();
        for (JQuickRow row : input.getRows()) {
            JQuickRow groupKey = extractGroupKey(row, node.getGroupKeys());
            groups.computeIfAbsent(groupKey, k -> new ArrayList<>()).add(row);
        }
        List<JQuickRow> resultRows = new ArrayList<>();
        for (Map.Entry<JQuickRow, List<JQuickRow>> entry : groups.entrySet()) {
            JQuickRow aggregated = new JQuickRow();
            for (Map.Entry<String, Object> keyEntry : entry.getKey().entrySet()) {
                aggregated.put(keyEntry.getKey(), keyEntry.getValue());
            }
            for (JQuickHashAggregatePhysicalNode.AggregateFunction agg : node.getAggregates()) {
                Object value = computeAggregate(entry.getValue(), agg);
                String alias = agg.getAlias() != null ? agg.getAlias() : agg.getFunctionName();
                aggregated.put(alias, value);
            }
            resultRows.add(aggregated);
        }
        if (node.getHavingCondition() != null) {
            resultRows = resultRows.stream()
                    .filter(row -> expressionEvaluator.evaluatePredicate(row, node.getHavingCondition()))
                    .collect(Collectors.toList());
        }
        //构建聚合结果列元数据
        List<JQuickColumnMeta> columnMetas = buildColumnMetasForAggregate(node);
        return new JQuickDataSet(columnMetas, resultRows);
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
            return JQuickDataSet.builder().build();
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
            return JQuickDataSet.builder().build();
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
        JQuickDataSet leftData = executeNode(node.getLeft(), context);
        JQuickDataSet rightData = executeNode(node.getRight(), context);
        List<JQuickRow> resultRows;
        switch (node.getOperationType()) {
            case UNION:
                Set<JQuickRow> unionSet = new HashSet<>(leftData.getRows());
                unionSet.addAll(rightData.getRows());
                resultRows = new ArrayList<>(unionSet);
                break;
            case UNION_ALL:
                resultRows = new ArrayList<>(leftData.getRows());
                resultRows.addAll(rightData.getRows());
                break;
            case INTERSECT:
                Set<JQuickRow> intersectSet = new HashSet<>(leftData.getRows());
                intersectSet.retainAll(new HashSet<>(rightData.getRows()));
                resultRows = new ArrayList<>(intersectSet);
                break;
            case EXCEPT:
                Set<JQuickRow> exceptSet = new HashSet<>(leftData.getRows());
                exceptSet.removeAll(new HashSet<>(rightData.getRows()));
                resultRows = new ArrayList<>(exceptSet);
                break;
            default:
                resultRows = new ArrayList<>(leftData.getRows());
        }

        return new JQuickDataSet(leftData.getColumns(), resultRows);
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
        //从 Values 节点构建列元数据
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
        Set<JQuickRow> seenRows = new HashSet<>(result.getRows());
        JQuickDataSet workingSet = result;
        int depth = 0;
        while (depth < node.getMaxRecursionDepth() && !workingSet.isEmpty()) {
            JQuickDataSet newRows = executeNode(node.getRecursivePlan(), context);
            List<JQuickRow> filteredRows = newRows.getRows().stream().filter(row -> !seenRows.contains(row)).collect(Collectors.toList());
            if (filteredRows.isEmpty()) break;
            if (node.isUnionAll()) {
                List<JQuickRow> allRows = new ArrayList<>(result.getRows());
                allRows.addAll(filteredRows);
                result = new JQuickDataSet(result.getColumns(), allRows);
            } else {
                seenRows.addAll(filteredRows);
                result = new JQuickDataSet(result.getColumns(), new ArrayList<>(seenRows));
            }
            workingSet = new JQuickDataSet(result.getColumns(), filteredRows);
            depth++;
        }

        return result;
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
            // 收集所有通过 gRPC 接收到的分区数据
            for (String partitionId : worker.getAllReceivedPartitions()) {
                JQuickDataSet partitionData = worker.getReceivedPartitionData(partitionId);
                if (partitionData != null && !partitionData.isEmpty()) {
                    allRows.addAll(partitionData.getRows());
                    if (columns == null) {
                        columns = partitionData.getColumns();
                    }
                    console.info("GATHER collected " + partitionData.size() + " rows from partition " + partitionId);
                }
            }
            
            // 如果从 gRPC 收到了数据，返回合并后的结果
            if (!allRows.isEmpty() && columns != null) {
                console.info("GATHER Exchange: returning " + allRows.size() + " rows from gRPC");
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
        
        // 发送数据到其他 Worker，同时保留当前 Worker 应该接收的数据
        List<JQuickRow> localRows = new ArrayList<>();
        List<JQuickColumnMeta> columns = null;
        int currentWorkerIndex = worker.getWorkerIndex();
        console.info("Current worker index: " + currentWorkerIndex);
        
        for (JQuickWorker.JQuickMemoryPartition partition : partitions) {
            int targetWorkerId = partition.getIndex() % node.getTargetParallelism();
            console.info("Partition " + partition.getIndex() + " target worker: " + targetWorkerId);
            
            if (targetWorkerId == currentWorkerIndex) {
                // 当前 Worker 应该接收这个分区的数据，保留在本地
                localRows.addAll(partition.getData().getRows());
                if (columns == null) {
                    columns = partition.getData().getColumns();
                }
                console.info("Partition " + partition.getIndex() + " kept locally, rows: " + partition.getData().size());
            } else {
                // 发送到其他 Worker
                partitionManager.sendToWorker(partition, node.getTargetParallelism(), node.getExchangeType(), worker);
                console.info("Sent partition " + partition.getIndex() + " to worker " + targetWorkerId);
            }
        }
        
        // 返回当前 Worker 应该接收的数据
        if (!localRows.isEmpty() && columns != null) {
            console.info("SHUFFLE Exchange: returning " + localRows.size() + " rows for current worker");
            return new JQuickDataSet(columns, localRows);
        }
        
        console.info("SHUFFLE Exchange: no data for current worker, returning empty");
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

    private Object extractJoinKey(JQuickRow row, JQuickHashJoinPhysicalNode node, boolean isBuildSide) {
        List<JQuickHashJoinPhysicalNode.JoinKeyPair> joinKeys = node.getJoinKeys();
        if (joinKeys.isEmpty()) return null;
        if (joinKeys.size() == 1) {
            JQuickHashJoinPhysicalNode.JoinKeyPair keyPair = joinKeys.get(0);
            JQuickExpression keyExpr = isBuildSide ? keyPair.getLeftKey() : keyPair.getRightKey();
            return expressionEvaluator.evaluateExpression(row, keyExpr);
        } else {
            List<Object> compositeKey = new ArrayList<>();
            for (JQuickHashJoinPhysicalNode.JoinKeyPair keyPair : joinKeys) {
                JQuickExpression keyExpr = isBuildSide ? keyPair.getLeftKey() : keyPair.getRightKey();
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

    private Object computeAggregate(List<JQuickRow> rows, JQuickHashAggregatePhysicalNode.AggregateFunction agg) {
        String funcName = agg.getFunctionName().toLowerCase();
        switch (funcName) {
            case "count":
                if (agg.isDistinct()) {
                    return rows.stream()
                            .map(r -> expressionEvaluator.evaluateExpression(r, agg.getArgument()))
                            .distinct().count();
                }
                return (long) rows.size();
            case "sum":
                return rows.stream().mapToDouble(r -> {
                    Object val = expressionEvaluator.evaluateExpression(r, agg.getArgument());
                    return val instanceof Number ? ((Number) val).doubleValue() : 0.0;
                }).sum();
            case "avg":
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
            default:
                List<Object> args = rows.stream().map(r -> expressionEvaluator.evaluateExpression(r, agg.getArgument())).collect(Collectors.toList());
                return expressionEvaluator.evaluateFunction(funcName, args);
        }
    }

    private Object evaluateWindowFunction(JQuickDataSet data, JQuickRow currentRow, JQuickWindowPhysicalNode.WindowFunction wf) {
        String funcName = wf.getFunctionName().toLowerCase();
        List<JQuickRow> windowRows = data.getRows();
        int currentIdx = windowRows.indexOf(currentRow);
        switch (funcName) {
            case "row_number":
                return (long) (currentIdx + 1);
            case "rank":
                return (long) (currentIdx + 1);
            case "dense_rank":
                return (long) (currentIdx + 1);
            case "lead":
                if (currentIdx + 1 < windowRows.size()) {
                    return expressionEvaluator.evaluateExpression(windowRows.get(currentIdx + 1), wf.getArgument());
                }
                return null;
            case "lag":
                if (currentIdx - 1 >= 0) {
                    return expressionEvaluator.evaluateExpression(windowRows.get(currentIdx - 1), wf.getArgument());
                }
                return null;
            default:
                List<Object> args = new ArrayList<>();
                if (wf.getArgument() != null) {
                    args.add(expressionEvaluator.evaluateExpression(currentRow, wf.getArgument()));
                }
                return expressionEvaluator.evaluateFunction(funcName, args);
        }
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

    private JQuickDataSet readFromDataSource(String tableName, Set<String> columns) {
        if (tableName == null) {
            return JQuickDataSet.builder().build();
        }
        JQuickDataSet tableData = JQuickDataSourceManager.getTable(tableName);
        if (tableData == null) {
            return JQuickDataSet.builder().build();
        }
        if (columns != null && !columns.isEmpty()) {
            return tableData.select(columns.toArray(new String[0]));
        }

        return tableData;
    }

    private JQuickDataSet readFromMemoryPartition(String partitionId, Set<String> columns) {
        JQuickWorker.JQuickMemoryPartition partition = worker.getMemoryPartitions().get(partitionId);
        if (partition == null) {
            return JQuickDataSet.builder().build();
        }
        JQuickDataSet data = partition.getData();
        if (columns == null || columns.isEmpty()) {
            return data;
        }
        return data.select(columns.toArray(new String[0]));
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

    private JQuickPhysicalPlanNode buildPhysicalNode(JQuickPhysicalPlanNodeProto proto) {
        if (proto == null) return null;

        switch (proto.getNodeCase()) {
            case TABLE_SCAN:
                JQuickTableScanNodeProto scanProto = proto.getTableScan();
                JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode(scanProto.getTableName(), scanProto.getAlias(), new HashSet<>(scanProto.getRequiredColumnsList()), null, null);
                return scanNode;
            case FILTER:
                return new JQuickFilterPhysicalNode(buildExpression(proto.getFilter().getPredicate()), null);
            case PROJECT:
                JQuickProjectNodeProto projectProto = proto.getProject();
                List<JQuickProjectPhysicalNode.SelectItem> selectItems = new ArrayList<>();
                for (JQuickProjectNodeProto.SelectItemProto itemProto : projectProto.getSelectItemsList()) {
                    selectItems.add(new JQuickProjectPhysicalNode.SelectItem(buildExpression(itemProto.getExpression()), itemProto.getAlias()));
                }
                return new JQuickProjectPhysicalNode(selectItems, null, projectProto.getDistinct());
            case HASH_JOIN:
                JQuickHashJoinNodeProto joinProto = proto.getHashJoin();
                return new JQuickHashJoinPhysicalNode(convertJoinType(joinProto.getJoinType()), null, null, buildExpression(joinProto.getCondition()), new ArrayList<>(), joinProto.getBuildSide() == JQuickBuildSideProto.BUILD_SIDE_LEFT ? JQuickHashJoinPhysicalNode.BuildSide.LEFT : JQuickHashJoinPhysicalNode.BuildSide.RIGHT, JQuickHashJoinPhysicalNode.JoinDistribution.LOCAL);
            case EXCHANGE:
                JQuickExchangeNodeProto exchangeProto = proto.getExchange();
                return new JQuickExchangePhysicalNode(convertExchangeType(exchangeProto.getExchangeType()), convertPartitionStrategy(exchangeProto.getPartitionStrategy()), new ArrayList<>(), exchangeProto.getParallelism(), null);
            case LIMIT:
                JQuickLimitNodeProto limitProto = proto.getLimit();
                return new JQuickLimitPhysicalNode(limitProto.getLimit(), limitProto.getOffset(), null);
            case EMPTY:
                return JQuickEmptyPhysicalNode.INSTANCE;
            default:
                return null;
        }
    }

    private JQuickExpression buildExpression(JQuickExpressionProto proto) {
        if (proto == null) return null;
        switch (proto.getType()) {
            case EXPR_COLUMN_REF:
                return new JQuickColumnRefExpression(proto.getValue());
            case EXPR_LITERAL:
                return new JQuickLiteralExpression(proto.getValue());
            case EXPR_BINARY_OPERATOR:
                List<JQuickExpression> children = new ArrayList<>();
                for (JQuickExpressionProto child : proto.getChildrenList()) {
                    children.add(buildExpression(child));
                }
                if (children.size() >= 2) {
                    return new JQuickBinaryExpression(children.get(0), children.get(1), convertBinaryOperator(proto.getBinaryOperator()));
                }
                return null;
            default:
                return null;
        }
    }

    private com.github.paohaijiao.enums.JQuickJoinType convertJoinType(JQuickJoinTypeProto proto) {
        switch (proto) {
            case JOIN_INNER:
                return com.github.paohaijiao.enums.JQuickJoinType.INNER;
            case JOIN_LEFT:
                return com.github.paohaijiao.enums.JQuickJoinType.LEFT;
            case JOIN_RIGHT:
                return com.github.paohaijiao.enums.JQuickJoinType.RIGHT;
            case JOIN_FULL:
                return com.github.paohaijiao.enums.JQuickJoinType.FULL;
            default:
                return com.github.paohaijiao.enums.JQuickJoinType.INNER;
        }
    }

    private JQuickExchangeType convertExchangeType(JQuickExchangeTypeProto proto) {
        switch (proto) {
            case EX_SHUFFLE:
                return JQuickExchangeType.SHUFFLE;
            case EX_BROADCAST:
                return JQuickExchangeType.BROADCAST;
            case EX_GATHER:
                return JQuickExchangeType.GATHER;
            default:
                return JQuickExchangeType.SHUFFLE;
        }
    }

    /**
     * 将 Proto 分区策略转换为内部枚举
     */
    private JQuickPartitionStrategy convertPartitionStrategy(JQuickPartitionStrategyProto proto) {
        switch (proto) {
            case PARTITION_HASH:
                return JQuickPartitionStrategy.HASH;
            case PARTITION_RANGE:
                return JQuickPartitionStrategy.RANGE;
            case PARTITION_ROUND_ROBIN:
                return JQuickPartitionStrategy.ROUND_ROBIN;
            case PARTITION_BROADCAST:
                return JQuickPartitionStrategy.REPLICATE;
            case PARTITION_FORWARD:
            default:
                return JQuickPartitionStrategy.HASH;
        }
    }

    /**
     * 将内部枚举分区策略转换为 Proto
     */
    private JQuickPartitionStrategyProto toProtoPartitionStrategy(JQuickPartitionStrategy strategy) {
        switch (strategy) {
            case HASH:
                return JQuickPartitionStrategyProto.PARTITION_HASH;
            case RANGE:
                return JQuickPartitionStrategyProto.PARTITION_RANGE;
            case ROUND_ROBIN:
                return JQuickPartitionStrategyProto.PARTITION_ROUND_ROBIN;
            case BUCKET:
                return JQuickPartitionStrategyProto.PARTITION_HASH;  // BUCKET 使用 HASH 类型传输
            case REPLICATE:
                return JQuickPartitionStrategyProto.PARTITION_BROADCAST;
            default:
                return JQuickPartitionStrategyProto.PARTITION_HASH;
        }
    }

    private JQuickBinaryOperator convertBinaryOperator(JQuickBinaryOperatorProto proto) {
        switch (proto) {
            case OP_EQ:
                return JQuickBinaryOperator.EQ;
            case OP_NE:
                return JQuickBinaryOperator.NE;
            case OP_LT:
                return JQuickBinaryOperator.LT;
            case OP_GT:
                return JQuickBinaryOperator.GT;
            case OP_PLUS:
                return JQuickBinaryOperator.PLUS;
            case OP_MINUS:
                return JQuickBinaryOperator.MINUS;
            case OP_MULTIPLY:
                return JQuickBinaryOperator.MULTIPLY;
            case OP_DIVIDE:
                return JQuickBinaryOperator.DIVIDE;
            default:
                return JQuickBinaryOperator.EQ;
        }
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