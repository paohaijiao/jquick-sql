package com.github.paohaijiao.distributed.proto;

import com.github.paohaijiao.enums.*;
import com.github.paohaijiao.exception.JAssert;
import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.expression.domain.*;
import com.github.paohaijiao.physical.JQuickPhysicalPlanNode;
import com.github.paohaijiao.physical.domain.JQuickColumnStats;
import com.github.paohaijiao.physical.domain.JQuickPhysicalColumn;
import com.github.paohaijiao.physical.domain.JQuickPhysicalStats;
import com.github.paohaijiao.physical.domain.JQuickTablePartitionInfo;
import com.github.paohaijiao.physical.node.*;
import com.github.paohaijiao.proto.*;
import com.github.paohaijiao.statement.JQuickColumnMeta;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;
import com.github.paohaijiao.util.JQuickAnyTypeConverterFactory;
import com.google.protobuf.Any;
import com.google.protobuf.NullValue;
import com.google.protobuf.Value;

import java.util.*;

public class JQuickProtoService {

    public static JQuickAnyTypeConverterFactory typeConverterFactory=JQuickAnyTypeConverterFactory.getInstance();
    /**
     * 将 Proto 转换为内部 DataSet
     */
    public JQuickDataSet convertFromProto(JQuickDataSetProto proto) {
        List<JQuickColumnMeta> columns = new ArrayList<>();
        for (JQuickColumnMetaProto colProto : proto.getColumnsList()) {
            try {
                Class<?> clazz = Class.forName(colProto.getTypeName());
                columns.add(new JQuickColumnMeta(
                        colProto.getName(),
                        clazz,
                        colProto.getSource()
                ));
            } catch (ClassNotFoundException e) {
                columns.add(new JQuickColumnMeta(
                        colProto.getName(),
                        Object.class,
                        colProto.getSource()
                ));
            }
        }
        List<JQuickRow> rows = new ArrayList<>();
        for (JQuickRowProto rowProto : proto.getRowsList()) {
            JQuickRow row = new JQuickRow();
            for (Map.Entry<String, Any> entry : rowProto.getDataMap().entrySet()) {
                try {
                    if (!entry.getValue().is(Value.class)) {
                        row.put(entry.getKey(), entry.getValue().toString());
                    } else {
                        Value value = entry.getValue().unpack(Value.class);
                        row.put(entry.getKey(), value.getStringValue());
                    }
                } catch (Exception e) {
                    row.put(entry.getKey(), entry.getValue().toString());
                }
            }
            rows.add(row);
        }
        return new JQuickDataSet(columns, rows);
    }
    /**
     * 转换物理列为 Proto
     */
    public JQuickPhysicalColumnProto convertPhysicalColumnToProto(JQuickPhysicalColumn col) {
        return JQuickPhysicalColumnProto.newBuilder()
                .setName(col.getName())
                .setTypeName(col.getType().getName())
                .setSourceTable(col.getSourceTable() != null ? col.getSourceTable() : "")
                .setNullable(col.isNullable())
                .build();
    }

    /**
     * 转换统计信息为 Proto
     */
    public JQuickPhysicalStatsProto convertStatsToProto(JQuickPhysicalStats stats) {
        JQuickPhysicalStatsProto.Builder builder = JQuickPhysicalStatsProto.newBuilder()
                .setEstimatedRowCount(stats.getEstimatedRowCount())
                .setEstimatedDataSize(stats.getEstimatedDataSize());
        for (Map.Entry<String, JQuickColumnStats> entry : stats.getColumnStats().entrySet()) {
            JQuickColumnStats colStats = entry.getValue();
            builder.putColumnStats(entry.getKey(), JQuickColumnStatsProto.newBuilder()
                    .setDistinctCount(colStats.getDistinctCount())
                    .setNullFraction(colStats.getNullFraction())
                    .build());
        }

        return builder.build();
    }

    /**
     * 转换 TableScan 节点
     */
    public JQuickTableScanNodeProto convertTableScanToProto(JQuickTableScanPhysicalNode node) {
        JQuickTableScanNodeProto.Builder builder = JQuickTableScanNodeProto.newBuilder().setTableName(node.getTableName()).setAlias(node.getAlias() != null ? node.getAlias() : "");
        if (node.getRequiredColumns() != null) {
            builder.addAllRequiredColumns(node.getRequiredColumns());
        }
        if (node.getFilterPredicate() != null) {
            builder.setFilterPredicate(convertExpressionToProto(node.getFilterPredicate()));
        }
        if (node.getPartitionInfo() != null) {
            builder.setPartitionInfo(convertPartitionInfoToProto(node.getPartitionInfo()));
        }
        return builder.build();
    }

    /**
     * 转换 Filter 节点
     */
    public JQuickFilterNodeProto convertFilterToProto(JQuickFilterPhysicalNode node) {
        return JQuickFilterNodeProto.newBuilder().setPredicate(convertExpressionToProto(node.getPredicate())).build();
    }

    /**
     * 转换 Project 节点
     */
    public JQuickProjectNodeProto convertProjectToProto(JQuickProjectPhysicalNode node) {
        JQuickProjectNodeProto.Builder builder = JQuickProjectNodeProto.newBuilder().setDistinct(node.isDistinct());
        for (JQuickProjectPhysicalNode.SelectItem item : node.getSelectItems()) {
            JQuickProjectNodeProto.SelectItemProto.Builder itemBuilder = JQuickProjectNodeProto.SelectItemProto.newBuilder().setExpression(convertExpressionToProto(item.getExpression()));
            if (item.getAlias() != null) {
                itemBuilder.setAlias(item.getAlias());
            }
            builder.addSelectItems(itemBuilder.build());
        }
        return builder.build();
    }

    /**
     * 转换 HashJoin 节点
     */
    public JQuickHashJoinNodeProto convertHashJoinToProto(JQuickHashJoinPhysicalNode node) {
        JQuickHashJoinNodeProto.Builder builder = JQuickHashJoinNodeProto.newBuilder().setJoinType(convertJoinTypeToProto(node.getJoinType())).setBuildSide(convertBuildSideToProto(node.getBuildSide())).setDistribution(convertJoinDistributionToProto(node.getDistribution()));
        if (node.getCondition() != null) {
            builder.setCondition(convertExpressionToProto(node.getCondition()));
        }
        for (JQuickHashJoinPhysicalNode.JoinKeyPair keyPair : node.getJoinKeys()) {
            builder.addJoinKeys(JQuickHashJoinNodeProto.JoinKeyPairProto.newBuilder()
                    .setLeftKey(convertExpressionToProto(keyPair.getLeftKey()))
                    .setRightKey(convertExpressionToProto(keyPair.getRightKey()))
                    .build());
        }
        return builder.build();
    }

    /**
     * 转换 NestedLoopJoin 节点
     */
    public JQuickNestedLoopJoinNodeProto convertNestedLoopJoinToProto(JQuickNestedLoopJoinPhysicalNode node) {
        JQuickNestedLoopJoinNodeProto.Builder builder = JQuickNestedLoopJoinNodeProto.newBuilder().setJoinType(convertJoinTypeToProto(node.getJoinType()));
        if (node.getCondition() != null) {
            builder.setCondition(convertExpressionToProto(node.getCondition()));
        }
        return builder.build();
    }

    /**
     * 转换 HashAggregate 节点
     */
    public JQuickHashAggregateNodeProto convertHashAggregateToProto(JQuickHashAggregatePhysicalNode node) {
        JQuickHashAggregateNodeProto.Builder builder = JQuickHashAggregateNodeProto.newBuilder().setStage(convertAggregateStageToProto(node.getStage()));
        for (JQuickExpression groupKey : node.getGroupKeys()) {
            builder.addGroupKeys(convertExpressionToProto(groupKey));
        }
        for (JQuickHashAggregatePhysicalNode.AggregateFunction agg : node.getAggregates()) {
            JQuickHashAggregateNodeProto.AggregateFunctionProto.Builder aggBuilder = JQuickHashAggregateNodeProto.AggregateFunctionProto.newBuilder()
                    .setFunctionName(agg.getFunctionName())
                    .setDistinct(agg.isDistinct())
                    .setAlias(agg.getAlias())
                    .setIsCountStar(agg.isCountStar())
                    .setInternalStage(convertAggregateStageToProto(agg.getInternalStage()));
            if (agg.getArgument() != null) {
                aggBuilder.setArgument(convertExpressionToProto(agg.getArgument()));
            }
            if (agg.getSeparator() != null) {
                aggBuilder.setSeparator(agg.getSeparator());
            }

            builder.addAggregates(aggBuilder.build());
        }
        if (node.getHavingCondition() != null) {
            builder.setHavingCondition(convertExpressionToProto(node.getHavingCondition()));
        }
        return builder.build();
    }

    /**
     * 转换 Sort 节点
     */
    public JQuickSortNodeProto convertSortToProto(JQuickSortPhysicalNode node) {
        JQuickSortNodeProto.Builder builder = JQuickSortNodeProto.newBuilder();
        for (JQuickSortPhysicalNode.OrderByItem item : node.getOrderByItems()) {
            builder.addOrderByItems(JQuickSortNodeProto.OrderByItemProto.newBuilder()
                    .setColumnName(item.getColumnName())
                    .setAscending(item.isAscending())
                    .setNullsFirst(item.isNullsFirst())
                    .build());
        }

        return builder.build();
    }

    /**
     * 转换 Limit 节点
     */
    public JQuickLimitNodeProto convertLimitToProto(JQuickLimitPhysicalNode node) {
        return JQuickLimitNodeProto.newBuilder()
                .setLimit(node.getLimit())
                .setOffset(node.getOffset())
                .build();
    }

    /**
     * 转换 Exchange 节点
     */
    public JQuickExchangeNodeProto convertExchangeToProto(JQuickExchangePhysicalNode node) {
        return JQuickExchangeNodeProto.newBuilder()
                .setExchangeId("exchange_" + System.currentTimeMillis())
                .setExchangeType(convertExchangeTypeToProto(node.getExchangeType()))
                .setPartitionStrategy(convertPartitionStrategyToProto(node.getPartitionStrategy()))
                .setParallelism(node.getTargetParallelism())
                .build();
    }

    /**
     * 转换 Values 节点
     */
    public JQuickValuesNodeProto convertValuesToProto(JQuickValuesPhysicalNode node) {
        JQuickValuesNodeProto.Builder builder = JQuickValuesNodeProto.newBuilder().addAllColumnNames(node.getColumnNames());
        for (Class<?> type : node.getColumnTypes()) {
            builder.addColumnTypes(type.getName());
        }
        for (List<Object> rowValues : node.getRows()) {
            JQuickRowProto.Builder rowBuilder = JQuickRowProto.newBuilder();
            for (int i = 0; i < rowValues.size() && i < node.getColumnNames().size(); i++) {
                Object value = rowValues.get(i);
                com.google.protobuf.Any anyValue = com.google.protobuf.Any.pack(com.google.protobuf.Value.newBuilder().setStringValue(value != null ? value.toString() : "").build());
                rowBuilder.putData(node.getColumnNames().get(i), anyValue);
            }
            builder.addRows(rowBuilder.build());
        }
        return builder.build();
    }

    /**
     * 转换 Window 节点
     */
    public JQuickWindowNodeProto convertWindowToProto(JQuickWindowPhysicalNode node) {
        JQuickWindowNodeProto.Builder builder = JQuickWindowNodeProto.newBuilder();
        for (JQuickWindowPhysicalNode.WindowFunction wf : node.getWindowFunctions()) {
            JQuickWindowNodeProto.WindowFunctionProto.Builder wfBuilder = JQuickWindowNodeProto.WindowFunctionProto.newBuilder().setFunctionName(wf.getFunctionName()).setAlias(wf.getAlias());
            if (wf.getArgument() != null) {
                wfBuilder.setArgument(convertExpressionToProto(wf.getArgument()));
            }
            if (wf.getWindowSpec() != null) {
                wfBuilder.setWindowSpec(convertWindowSpecToProto(wf.getWindowSpec()));
            }

            builder.addWindowFunctions(wfBuilder.build());
        }

        return builder.build();
    }

    /**
     * 转换 WindowSpec 到 Proto
     */
    public JQuickWindowNodeProto.WindowSpecProto convertWindowSpecToProto(JQuickWindowPhysicalNode.WindowSpec spec) {
        JQuickWindowNodeProto.WindowSpecProto.Builder builder = JQuickWindowNodeProto.WindowSpecProto.newBuilder();
        for (JQuickExpression partitionKey : spec.getPartitionKeys()) {
            builder.addPartitionKeys(convertExpressionToProto(partitionKey));
        }
        for (JQuickSortPhysicalNode.OrderByItem orderKey : spec.getOrderKeys()) {
            builder.addOrderKeys(JQuickSortNodeProto.OrderByItemProto.newBuilder()
                    .setColumnName(orderKey.getColumnName())
                    .setAscending(orderKey.isAscending())
                    .setNullsFirst(orderKey.isNullsFirst())
                    .build());
        }
        if (spec.getFrame() != null) {
            builder.setFrame(convertWindowFrameToProto(spec.getFrame()));
        }
        return builder.build();
    }

    /**
     * 转换 WindowFrame 到 Proto
     */
    public JQuickWindowNodeProto.WindowFrameProto convertWindowFrameToProto(JQuickWindowPhysicalNode.WindowFrame frame) {
        JQuickWindowNodeProto.WindowFrameProto.Builder builder = JQuickWindowNodeProto.WindowFrameProto.newBuilder()
                .setFrameType(frame.getFrameType() == JQuickWindowPhysicalNode.WindowFrame.FrameType.ROWS ? JQuickWindowNodeProto.WindowFrameProto.FrameType.FRAME_ROWS : JQuickWindowNodeProto.WindowFrameProto.FrameType.FRAME_RANGE)
                .setStartType(convertBoundaryTypeToProto(frame.getStartType()))
                .setEndType(convertBoundaryTypeToProto(frame.getEndType()));
        if (frame.getStartOffset() != null) {
            builder.setStartOffset(convertExpressionToProto(frame.getStartOffset()));
        }
        if (frame.getEndOffset() != null) {
            builder.setEndOffset(convertExpressionToProto(frame.getEndOffset()));
        }
        return builder.build();
    }

    /**
     * 转换 SetOperation 节点
     */
    public JQuickSetOperationNodeProto convertSetOperationToProto(JQuickSetOperationPhysicalNode node) {
        JQuickSetOperationNodeProto.Builder builder = JQuickSetOperationNodeProto.newBuilder()
                .setOperationType(convertSQLOperationTypeToProto(node.getOperationType()));
        // 添加 children
        for (JQuickPhysicalPlanNode child : node.getChildren()) {
            builder.addChildren(convertPhysicalPlanToProto(child));
        }
        return builder.build();
    }

    /**
     * 转换 TopN 节点
     */
    public JQuickTopNNodeProto convertTopNToProto(JQuickTopNPhysicalNode node) {
        JQuickTopNNodeProto.Builder builder = JQuickTopNNodeProto.newBuilder()
                .setLimit(node.getLimit())
                .setOffset(node.getOffset());
        for (JQuickSortPhysicalNode.OrderByItem item : node.getOrderByItems()) {
            builder.addOrderByItems(JQuickSortNodeProto.OrderByItemProto.newBuilder()
                    .setColumnName(item.getColumnName())
                    .setAscending(item.isAscending())
                    .setNullsFirst(item.isNullsFirst())
                    .build());
        }

        return builder.build();
    }

    /**
     * 转换 RecursiveUnion 节点
     */
    public JQuickRecursiveUnionNodeProto convertRecursiveUnionToProto(JQuickRecursiveUnionPhysicalNode node) {
        JQuickRecursiveUnionNodeProto.Builder builder = JQuickRecursiveUnionNodeProto.newBuilder()
                .setCteName(node.getCteName())
                .setUnionAll(node.isUnionAll())
                .setMaxRecursionDepth(node.getMaxRecursionDepth());
        if (node.getColumnNames() != null) {
            builder.addAllColumnNames(node.getColumnNames());
        }

        return builder.build();
    }

    /**
     * 转换 PartitionInfo 到 Proto
     */
    public JQuickTablePartitionInfoProto convertPartitionInfoToProto(JQuickTablePartitionInfo info) {
        JQuickTablePartitionInfoProto.Builder builder = JQuickTablePartitionInfoProto.newBuilder()
                .setTableName(info.getTableName())
                .setPartitionColumn(info.getPartitionColumn())
                .setPartitionCount(info.getTotalPartitions());
        for (JQuickTablePartitionInfo.Partition partition : info.getPartitions()) {
            JQuickMemoryPartitionProto.Builder partitionBuilder = JQuickMemoryPartitionProto.newBuilder()
                    .setPartitionId(partition.getLocation())
                    .setPartitionIndex(0)
                    .setTotalPartitions(info.getTotalPartitions())
                    .setEstimatedSize(partition.getSize());
            for (String key : partition.getPartitionValues().keySet()) {
                partitionBuilder.addPartitionKeyNames(key);
            }
            builder.addPartitions(partitionBuilder.build());
        }

        return builder.build();
    }

    /**
     * 转换物理计划节点为 Proto
     */
    public JQuickPhysicalPlanNodeProto convertPhysicalPlanToProto(JQuickPhysicalPlanNode node) {
        if (node == null || node.getNodeType().equalsIgnoreCase("Empty")) {
            return JQuickPhysicalPlanNodeProto.newBuilder()
                    .setNodeId("empty")
                    .setNodeType("Empty")
                    .setEmpty(JQuickEmptyNodeProto.newBuilder().build())
                    .build();
        }
        JQuickPhysicalPlanNodeProto.Builder builder = JQuickPhysicalPlanNodeProto.newBuilder()
                .setNodeId(UUID.randomUUID().toString())
                .setNodeType(node.getNodeType());
        for (JQuickPhysicalColumn col : node.getOutputSchema()) {
            builder.addOutputSchema(convertPhysicalColumnToProto(col));
        }
        JQuickPhysicalStats stats = node.getStats();
        if (stats != null && stats.getEstimatedRowCount() > 0) {
            builder.setStats(convertStatsToProto(stats));
        }
        String nodeType = node.getNodeType();
        switch (nodeType) {
            case "TableScan":
                builder.setTableScan(convertTableScanToProto((JQuickTableScanPhysicalNode) node));
                break;
            case "Filter":
                builder.setFilter(convertFilterToProto((JQuickFilterPhysicalNode) node));
                break;
            case "Project":
                builder.setProject(convertProjectToProto((JQuickProjectPhysicalNode) node));
                break;
            case "HashJoin":
                builder.setHashJoin(convertHashJoinToProto((JQuickHashJoinPhysicalNode) node));
                break;
            case "NestedLoopJoin":
                builder.setNestedLoopJoin(convertNestedLoopJoinToProto((JQuickNestedLoopJoinPhysicalNode) node));
                break;
            case "HashAggregate":
                builder.setHashAggregate(convertHashAggregateToProto((JQuickHashAggregatePhysicalNode) node));
                break;
            case "Sort":
                builder.setSort(convertSortToProto((JQuickSortPhysicalNode) node));
                break;
            case "Limit":
                builder.setLimit(convertLimitToProto((JQuickLimitPhysicalNode) node));
                break;
            case "Exchange":
                builder.setExchange(convertExchangeToProto((JQuickExchangePhysicalNode) node));
                break;
            case "Values":
                builder.setValues(convertValuesToProto((JQuickValuesPhysicalNode) node));
                break;
            case "Empty":
                builder.setEmpty(JQuickEmptyNodeProto.newBuilder().build());
                break;
            case "Window":
                builder.setWindow(convertWindowToProto((JQuickWindowPhysicalNode) node));
                break;
            case "SetOperation":
                builder.setSetOperation(convertSetOperationToProto((JQuickSetOperationPhysicalNode) node));
                break;
            case "TopN":
                builder.setTopN(convertTopNToProto((JQuickTopNPhysicalNode) node));
                break;
            case "RecursiveUnion":
                builder.setRecursiveUnion(convertRecursiveUnionToProto((JQuickRecursiveUnionPhysicalNode) node));
                break;
            default:
                builder.setEmpty(JQuickEmptyNodeProto.newBuilder().build());
        }
        for (JQuickPhysicalPlanNode child : node.getChildren()) {
            builder.addChildNodeIds(child.getNodeType());
            builder.addChildren(convertPhysicalPlanToProto(child));
        }
        return builder.build();
    }

    /**
     * 转换表达式为 Proto
     */
    public JQuickExpressionProto convertExpressionToProto(JQuickExpression expr) {
        if (expr == null) {
            Any  nullAny=typeConverterFactory.packNull();
            return JQuickExpressionProto.newBuilder()
                    .setType(JQuickExpressionTypeProto.EXPR_LITERAL)
                    .setValue(nullAny)
                    .build();
        }
        JQuickExpressionProto.Builder builder = JQuickExpressionProto.newBuilder();
        if (expr instanceof JQuickColumnRefExpression) {
            Any type=typeConverterFactory.toAny(((JQuickColumnRefExpression) expr).getColumnName());
            builder.setType(JQuickExpressionTypeProto.EXPR_COLUMN_REF).setValue(type);
        } else if (expr instanceof JQuickLiteralExpression) {
            Object value = ((JQuickLiteralExpression) expr).getValue();
            Any type=typeConverterFactory.toAny(value);
            builder.setType(JQuickExpressionTypeProto.EXPR_LITERAL).setValue(type);
        } else if (expr instanceof JQuickBinaryExpression) {
            JQuickBinaryExpression binary = (JQuickBinaryExpression) expr;
            builder.setType(JQuickExpressionTypeProto.EXPR_BINARY_OPERATOR).setBinaryOperator(convertBinaryOperatorToProto(binary.getOperator()));
            builder.addChildren(convertExpressionToProto(binary.getLeft()));
            builder.addChildren(convertExpressionToProto(binary.getRight()));
        } else if (expr instanceof JQuickUnaryExpression) {
            JQuickUnaryExpression unary = (JQuickUnaryExpression) expr;
            builder.setType(JQuickExpressionTypeProto.EXPR_UNARY_OPERATOR);
            builder.setUnaryOperator(convertUnaryOperatorToProto(unary.getOperator()));
            builder.addChildren(convertExpressionToProto(unary.getExpression()));
        } else if (expr instanceof JQuickFunctionCallExpression) {
            JQuickFunctionCallExpression func = (JQuickFunctionCallExpression) expr;
            builder.setType(JQuickExpressionTypeProto.EXPR_FUNCTION).setFunctionName(func.getFunctionName());
            for (JQuickExpression arg : func.getArguments()) {
                builder.addArguments(convertExpressionToProto(arg));
            }
        } else if (expr instanceof JQuickBetweenExpression) {
            JQuickBetweenExpression between = (JQuickBetweenExpression) expr;
            builder.setType(JQuickExpressionTypeProto.EXPR_BETWEEN);
            builder.addChildren(convertExpressionToProto(between.getExpression()));
            builder.addChildren(convertExpressionToProto(between.getLow()));
            builder.addChildren(convertExpressionToProto(between.getHigh()));
            if (between.isNot()) {
                builder.putAttributes("not", "true");
            }
        } else if (expr instanceof JQuickInExpression) {
            JQuickInExpression in = (JQuickInExpression) expr;
            builder.setType(JQuickExpressionTypeProto.EXPR_IN);
            builder.addChildren(convertExpressionToProto(in.getLeft()));
            for (JQuickExpression rightExpr : in.getRightList()) {
                builder.addChildren(convertExpressionToProto(rightExpr));
            }
            if (in.isNot()) {
                builder.putAttributes("not", "true");
            }
        } else if (expr instanceof JQuickCaseWhenExpression) {
            JQuickCaseWhenExpression caseWhen = (JQuickCaseWhenExpression) expr;
            builder.setType(JQuickExpressionTypeProto.EXPR_CASE_WHEN);
            for (int i = 0; i < caseWhen.getConditions().size(); i++) {
                builder.addChildren(convertExpressionToProto(caseWhen.getConditions().get(i)));
                builder.addChildren(convertExpressionToProto(caseWhen.getResults().get(i)));
            }
            if (caseWhen.getElseResult() != null) {
                builder.addChildren(convertExpressionToProto(caseWhen.getElseResult()));
            }
        } else if (expr instanceof JQuickSubqueryExpression) {
            JQuickSubqueryExpression subqueryExpr = (JQuickSubqueryExpression) expr;
            builder.setType(JQuickExpressionTypeProto.EXPR_SUBQUERY);
            builder.putAttributes("subqueryType", subqueryExpr.getSubqueryType().name());
            if (subqueryExpr.getLeftExpression() != null) {
                builder.addChildren(convertExpressionToProto(subqueryExpr.getLeftExpression()));
            }
            if (subqueryExpr.getRightExpression() != null) {
                builder.addChildren(convertExpressionToProto(subqueryExpr.getRightExpression()));
            }
            if (subqueryExpr.getSubquery() != null) {
                com.github.paohaijiao.logic2physical.JQuickPhysicalPlanGenerator planGenerator = 
                        new com.github.paohaijiao.logic2physical.JQuickPhysicalPlanGenerator();
                JQuickPhysicalPlanNode physicalPlan = planGenerator.generate(subqueryExpr.getSubquery());
                if (physicalPlan != null) {
                    builder.setSubqueryPlan(convertPhysicalPlanToProto(physicalPlan));
                }
            }
        } else if (expr instanceof JQuickExistsExpression) {
            JQuickExistsExpression existsExpr = (JQuickExistsExpression) expr;
            builder.setType(JQuickExpressionTypeProto.EXPR_SUBQUERY);
            builder.putAttributes("subqueryType", existsExpr.isNotExists() ? "NOT_EXISTS" : "EXISTS");
            builder.putAttributes("exprKind", "EXISTS_EXPR");
            JQuickSubqueryExpression subqueryExpr = existsExpr.getSubquery();
            if (subqueryExpr != null && subqueryExpr.getSubquery() != null) {
                com.github.paohaijiao.logic2physical.JQuickPhysicalPlanGenerator planGenerator = 
                        new com.github.paohaijiao.logic2physical.JQuickPhysicalPlanGenerator();
                JQuickPhysicalPlanNode physicalPlan = planGenerator.generate(subqueryExpr.getSubquery());
                if (physicalPlan != null) {
                    builder.setSubqueryPlan(convertPhysicalPlanToProto(physicalPlan));
                }
            }
        }

        JQuickExpressionProto expressionProto= builder.build();
        return expressionProto;
    }


    public JQuickJoinTypeProto convertJoinTypeToProto(com.github.paohaijiao.enums.JQuickJoinType joinType) {
        switch (joinType) {
            case INNER: return JQuickJoinTypeProto.JOIN_INNER;
            case LEFT: return JQuickJoinTypeProto.JOIN_LEFT;
            case RIGHT: return JQuickJoinTypeProto.JOIN_RIGHT;
            case FULL: return JQuickJoinTypeProto.JOIN_FULL;
            case CROSS: return JQuickJoinTypeProto.JOIN_CROSS;
            case SEMI: return JQuickJoinTypeProto.JOIN_SEMI;
            case ANTI: return JQuickJoinTypeProto.JOIN_ANTI;
            default: return JQuickJoinTypeProto.JOIN_INNER;
        }
    }

    public JQuickBuildSideProto convertBuildSideToProto(JQuickHashJoinPhysicalNode.BuildSide buildSide) {
        return buildSide == JQuickHashJoinPhysicalNode.BuildSide.LEFT ? JQuickBuildSideProto.BUILD_SIDE_LEFT : JQuickBuildSideProto.BUILD_SIDE_RIGHT;
    }

    public JQuickJoinDistributionProto convertJoinDistributionToProto(JQuickHashJoinPhysicalNode.JoinDistribution distribution) {
        switch (distribution) {
            case LOCAL: return JQuickJoinDistributionProto.JOIN_DIST_LOCAL;
            case SHUFFLE_HASH: return JQuickJoinDistributionProto.JOIN_DIST_SHUFFLE;
            case BROADCAST_HASH: return JQuickJoinDistributionProto.JOIN_DIST_BROADCAST;
            case PARTITIONED: return JQuickJoinDistributionProto.JOIN_DIST_PARTITIONED;
            default: return JQuickJoinDistributionProto.JOIN_DIST_LOCAL;
        }
    }

    public JQuickAggregateStageProto convertAggregateStageToProto(JQuickHashAggregatePhysicalNode.AggregateStage stage) {
        switch (stage) {
            case PARTIAL: return JQuickAggregateStageProto.AGG_PARTIAL;
            case FINAL: return JQuickAggregateStageProto.AGG_FINAL;
            case SINGLE: return JQuickAggregateStageProto.AGG_SINGLE;
            default: return JQuickAggregateStageProto.AGG_SINGLE;
        }
    }

    public JQuickHashAggregatePhysicalNode.AggregateStage convertAggregateStage(JQuickAggregateStageProto proto) {
        switch (proto) {
            case AGG_PARTIAL: return JQuickHashAggregatePhysicalNode.AggregateStage.PARTIAL;
            case AGG_FINAL: return JQuickHashAggregatePhysicalNode.AggregateStage.FINAL;
            case AGG_SINGLE: return JQuickHashAggregatePhysicalNode.AggregateStage.SINGLE;
            default: return JQuickHashAggregatePhysicalNode.AggregateStage.SINGLE;
        }
    }

    public JQuickExchangeTypeProto convertExchangeTypeToProto(com.github.paohaijiao.enums.JQuickExchangeType exchangeType) {
        switch (exchangeType) {
            case SHUFFLE: return JQuickExchangeTypeProto.EX_SHUFFLE;
            case BROADCAST: return JQuickExchangeTypeProto.EX_BROADCAST;
            case GATHER: return JQuickExchangeTypeProto.EX_GATHER;
            case REPARTITION: return JQuickExchangeTypeProto.EX_REPARTITION;
            case PIPELINE: return JQuickExchangeTypeProto.EX_PIPELINE;
            default: return JQuickExchangeTypeProto.EX_SHUFFLE;
        }
    }

    public JQuickPartitionStrategyProto convertPartitionStrategyToProto(com.github.paohaijiao.enums.JQuickPartitionStrategy strategy) {
        switch (strategy) {
            case HASH: return JQuickPartitionStrategyProto.PARTITION_HASH;
            case RANGE: return JQuickPartitionStrategyProto.PARTITION_RANGE;
            case ROUND_ROBIN: return JQuickPartitionStrategyProto.PARTITION_ROUND_ROBIN;
            case REPLICATE: return JQuickPartitionStrategyProto.PARTITION_BROADCAST;
            case BUCKET: return JQuickPartitionStrategyProto.PARTITION_HASH;
            default: return JQuickPartitionStrategyProto.PARTITION_HASH;
        }
    }

    public JQuickBinaryOperatorProto convertBinaryOperatorToProto(JQuickBinaryOperator operator) {
        switch (operator) {
            case EQ: return JQuickBinaryOperatorProto.OP_EQ;
            case NE: return JQuickBinaryOperatorProto.OP_NE;
            case LT: return JQuickBinaryOperatorProto.OP_LT;
            case GT: return JQuickBinaryOperatorProto.OP_GT;
            case LE: return JQuickBinaryOperatorProto.OP_LTE;
            case GE: return JQuickBinaryOperatorProto.OP_GTE;
            case AND: return JQuickBinaryOperatorProto.OP_AND;
            case OR: return JQuickBinaryOperatorProto.OP_OR;
            case LIKE: return JQuickBinaryOperatorProto.OP_LIKE;
            case NOT_LIKE: return JQuickBinaryOperatorProto.OP_NOT_LIKE;
            case PLUS: return JQuickBinaryOperatorProto.OP_PLUS;
            case MINUS: return JQuickBinaryOperatorProto.OP_MINUS;
            case MULTIPLY: return JQuickBinaryOperatorProto.OP_MULTIPLY;
            case DIVIDE: return JQuickBinaryOperatorProto.OP_DIVIDE;
            case MODULO: return JQuickBinaryOperatorProto.OP_MOD;
            case REGEX: return JQuickBinaryOperatorProto.OP_REGEX;
            case NOT_REGEX: return JQuickBinaryOperatorProto.OP_NOT_REGEX;
//            case EXIST: return JQuickBinaryOperatorProto.OP_EXIST;
            default: return JQuickBinaryOperatorProto.OP_EQ;
        }
    }

    public JQuickUnaryOperatorProto convertUnaryOperatorToProto(JQuickUnaryOperator operator) {
        switch (operator) {
            case NOT: return JQuickUnaryOperatorProto.OP_UNARY_NOT;
            case PLUS: return JQuickUnaryOperatorProto.OP_UNARY_PLUS;
            case MINUS: return JQuickUnaryOperatorProto.OP_UNARY_MINUS;
            case IS_NULL: return JQuickUnaryOperatorProto.OP_UNARY_IS_NULL;
            case IS_NOT_NULL: return JQuickUnaryOperatorProto.OP_UNARY_IS_NOT_NULL;
            default: return JQuickUnaryOperatorProto.OP_UNARY_NOT;
        }
    }

    public JQuickSQLOperationTypeProto convertSQLOperationTypeToProto(com.github.paohaijiao.enums.JQuickSQLOperationType type) {
        switch (type) {
            case UNION: return JQuickSQLOperationTypeProto.SET_UNION;
            case UNION_ALL: return JQuickSQLOperationTypeProto.SET_UNION_ALL;
            case INTERSECT: return JQuickSQLOperationTypeProto.SET_INTERSECT;
            case EXCEPT: return JQuickSQLOperationTypeProto.SET_EXCEPT;
            default: return JQuickSQLOperationTypeProto.SET_UNION;
        }
    }

    public JQuickWindowNodeProto.WindowFrameProto.BoundaryType convertBoundaryTypeToProto(JQuickWindowPhysicalNode.WindowFrame.BoundaryType type) {
        switch (type) {
            case UNBOUNDED_PRECEDING: return JQuickWindowNodeProto.WindowFrameProto.BoundaryType.BOUND_UNBOUNDED_PRECEDING;
            case PRECEDING: return JQuickWindowNodeProto.WindowFrameProto.BoundaryType.BOUND_PRECEDING;
            case CURRENT_ROW: return JQuickWindowNodeProto.WindowFrameProto.BoundaryType.BOUND_CURRENT_ROW;
            case FOLLOWING: return JQuickWindowNodeProto.WindowFrameProto.BoundaryType.BOUND_FOLLOWING;
            case UNBOUNDED_FOLLOWING: return JQuickWindowNodeProto.WindowFrameProto.BoundaryType.BOUND_UNBOUNDED_FOLLOWING;
            default: return JQuickWindowNodeProto.WindowFrameProto.BoundaryType.BOUND_CURRENT_ROW;
        }
    }
    /**
     * 转换 Set Operation 类型
     */
    public JQuickSQLOperationType convertSetOperationType(JQuickSQLOperationTypeProto proto) {
        switch (proto) {
            case SET_UNION:
                return JQuickSQLOperationType.UNION;
            case SET_UNION_ALL:
                return JQuickSQLOperationType.UNION_ALL;
            case SET_INTERSECT:
                return JQuickSQLOperationType.INTERSECT;
            case SET_EXCEPT:
                return JQuickSQLOperationType.EXCEPT;
            default:
                return JQuickSQLOperationType.UNION;
        }
    }
    public JQuickExpression buildExpression(JQuickExpressionProto proto) {
        if (proto == null) return null;
        switch (proto.getType()) {
            case EXPR_COLUMN_REF:
                String type=typeConverterFactory.toType(proto.getValue(),String.class);
                return new JQuickColumnRefExpression(type);
            case EXPR_LITERAL:
                Object val=typeConverterFactory.fromAny(proto.getValue());
                return new JQuickLiteralExpression(val);
            case EXPR_FUNCTION:
                String functionName=proto.getFunctionName();
                JAssert.notNull(functionName,"the functionName ["+functionName+"] load failed from spi");
                List<JQuickExpression> arguments = new ArrayList<>();
                for (JQuickExpressionProto argProto : proto.getArgumentsList()) {
                    arguments.add(buildExpression(argProto));
                }
                boolean isStarArg = "true".equals(proto.getAttributesMap().get("is_star"));
                return new JQuickFunctionCallExpression(functionName, arguments, isStarArg);
            case EXPR_CASE_WHEN:
                List<JQuickExpression> conditions = new ArrayList<>();
                List<JQuickExpression> results = new ArrayList<>();
                List<JQuickExpressionProto> children = proto.getChildrenList();
                int i = 0;
                while (i + 1 < children.size()) {
                    conditions.add(buildExpression(children.get(i)));
                    results.add(buildExpression(children.get(i + 1)));
                    i += 2;
                }
                JQuickExpression elseResult = null;
                if (i < children.size()) {
                    elseResult = buildExpression(children.get(i));
                }
                return new JQuickCaseWhenExpression(conditions, results, elseResult);
            case EXPR_SUBQUERY:
                String subqueryTypeStr = proto.getAttributesMap().get("subqueryType");
                if (subqueryTypeStr != null) {
                    JQuickSubqueryType subqueryType = JQuickSubqueryType.valueOf(subqueryTypeStr);
                    List<JQuickExpression> subqueryChildren = new ArrayList<>();
                    for (JQuickExpressionProto child : proto.getChildrenList()) {
                        subqueryChildren.add(buildExpression(child));
                    }
                    JQuickExpression leftExpr = subqueryChildren.size() > 0 ? subqueryChildren.get(0) : null;
                    JQuickExpression rightExpr = subqueryChildren.size() > 1 ? subqueryChildren.get(1) : null;
                    JQuickPhysicalPlanNode subqueryPlan = null;
                    if (proto.hasSubqueryPlan()) {
                        subqueryPlan = buildPhysicalNode(proto.getSubqueryPlan());
                    }

                    String exprKind = proto.getAttributesMap().get("exprKind");
                    if ("EXISTS_EXPR".equals(exprKind)) {
                        JQuickSubqueryExpression subqueryExpr = new JQuickSubqueryExpression(subqueryPlan, subqueryType, leftExpr, rightExpr);
                        return new JQuickExistsExpression(subqueryExpr, subqueryType == JQuickSubqueryType.NOT_EXISTS);
                    }

                    return new JQuickSubqueryExpression(subqueryPlan, subqueryType, leftExpr, rightExpr);
                }
                return null;
            case EXPR_IN:
                List<JQuickExpression> inChildren = new ArrayList<>();
                for (JQuickExpressionProto child : proto.getChildrenList()) {
                    inChildren.add(buildExpression(child));
                }
                if (inChildren.size() >= 2) {
                    JQuickExpression left = inChildren.get(0);
                    List<JQuickExpression> rightList = inChildren.subList(1, inChildren.size());
                    boolean isNot = "true".equals(proto.getAttributesMap().get("not"));
                    return new JQuickInExpression(left, rightList, isNot);
                }
                return null;
            case EXPR_IS_NULL:
                List<JQuickExpression> isNullChildren = new ArrayList<>();
                for (JQuickExpressionProto child : proto.getChildrenList()) {
                    isNullChildren.add(buildExpression(child));
                }
                if (!isNullChildren.isEmpty()) {
                    boolean isNot = "true".equals(proto.getAttributesMap().get("not"));
                    JQuickUnaryOperator operator = isNot ? JQuickUnaryOperator.IS_NOT_NULL : JQuickUnaryOperator.IS_NULL;
                    return new JQuickUnaryExpression(operator, isNullChildren.get(0));
                }
                return null;
            case EXPR_BINARY_OPERATOR:
                List<JQuickExpression> binChildren = new ArrayList<>();
                for (JQuickExpressionProto child : proto.getChildrenList()) {
                    binChildren.add(buildExpression(child));
                }
                if (binChildren.size() >= 2) {
                    return new JQuickBinaryExpression(binChildren.get(0), binChildren.get(1), convertBinaryOperator(proto.getBinaryOperator()));
                }
                return null;
            case EXPR_UNARY_OPERATOR:
                List<JQuickExpression> unAryChildren = new ArrayList<>();
                for (JQuickExpressionProto child : proto.getChildrenList()) {
                    unAryChildren.add(buildExpression(child));
                }
                if (!unAryChildren.isEmpty()) {
                    return new JQuickUnaryExpression(convertUnaryOperator(proto.getUnaryOperator()), unAryChildren.get(0));
                }
                return null;
            case EXPR_BETWEEN:
                List<JQuickExpression> betweenChildren = new ArrayList<>();
                for (JQuickExpressionProto child : proto.getChildrenList()) {
                    betweenChildren.add(buildExpression(child));
                }
                if (betweenChildren.size() >= 3) {
                    boolean isNot = "true".equals(proto.getAttributesMap().get("not"));
                    return new JQuickBetweenExpression(betweenChildren.get(0), betweenChildren.get(1), betweenChildren.get(2), isNot);
                }
                return null;
            default:
                return null;
        }
    }

    public com.github.paohaijiao.enums.JQuickJoinType convertJoinType(JQuickJoinTypeProto proto) {
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

    public JQuickExchangeType convertExchangeType(JQuickExchangeTypeProto proto) {
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
    public JQuickPartitionStrategy convertPartitionStrategy(JQuickPartitionStrategyProto proto) {
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
    public JQuickPartitionStrategyProto toProtoPartitionStrategy(JQuickPartitionStrategy strategy) {
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

    public JQuickBinaryOperator convertBinaryOperator(JQuickBinaryOperatorProto proto) {
        switch (proto) {
            case OP_EQ:
                return JQuickBinaryOperator.EQ;
            case OP_NE:
                return JQuickBinaryOperator.NE;
            case OP_LT:
                return JQuickBinaryOperator.LT;
            case OP_LTE:
                return JQuickBinaryOperator.LE;
            case OP_GT:
                return JQuickBinaryOperator.GT;
            case OP_GTE:
                return JQuickBinaryOperator.GE;
            case OP_AND:
                return JQuickBinaryOperator.AND;
            case OP_OR:
                return JQuickBinaryOperator.OR;
            case OP_LIKE:
                return JQuickBinaryOperator.LIKE;
            case OP_NOT_LIKE:
                return JQuickBinaryOperator.NOT_LIKE;
            case OP_REGEX:
                return JQuickBinaryOperator.REGEX;
            case OP_NOT_REGEX:
                return JQuickBinaryOperator.NOT_REGEX;
            case OP_PLUS:
                return JQuickBinaryOperator.PLUS;
            case OP_MINUS:
                return JQuickBinaryOperator.MINUS;
            case OP_MULTIPLY:
                return JQuickBinaryOperator.MULTIPLY;
            case OP_DIVIDE:
                return JQuickBinaryOperator.DIVIDE;
            case OP_MOD:
                return JQuickBinaryOperator.MODULO;
            default:
                return JQuickBinaryOperator.EQ;
        }
    }

    public JQuickUnaryOperator convertUnaryOperator(JQuickUnaryOperatorProto proto) {
        switch (proto) {
            case OP_UNARY_NOT:
                return JQuickUnaryOperator.NOT;
            case OP_UNARY_PLUS:
                return JQuickUnaryOperator.PLUS;
            case OP_UNARY_MINUS:
                return JQuickUnaryOperator.MINUS;
            case OP_UNARY_IS_NULL:
                return JQuickUnaryOperator.IS_NULL;
            case OP_UNARY_IS_NOT_NULL:
                return JQuickUnaryOperator.IS_NOT_NULL;
            default:
                return JQuickUnaryOperator.NOT;
        }
    }
    public JQuickPhysicalPlanNode buildPhysicalNode(JQuickPhysicalPlanNodeProto proto) {
        if (proto == null) return null;

        List<JQuickPhysicalPlanNode> children = new ArrayList<>();
        for (JQuickPhysicalPlanNodeProto childProto : proto.getChildrenList()) {
            children.add(buildPhysicalNode(childProto));
        }

        switch (proto.getNodeCase()) {
            case TABLE_SCAN:
                JQuickTableScanNodeProto scanProto = proto.getTableScan();
                JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode(scanProto.getTableName(), scanProto.getAlias(), new HashSet<>(scanProto.getRequiredColumnsList()),
                        scanProto.hasFilterPredicate()?buildExpression(scanProto.getFilterPredicate()):null);
                return scanNode;
            case FILTER:
                return new JQuickFilterPhysicalNode(buildExpression(proto.getFilter().getPredicate()), 
                        children.size() > 0 ? children.get(0) : null);
            case PROJECT:
                JQuickProjectNodeProto projectProto = proto.getProject();
                List<JQuickProjectPhysicalNode.SelectItem> selectItems = new ArrayList<>();
                for (JQuickProjectNodeProto.SelectItemProto itemProto : projectProto.getSelectItemsList()) {
                    selectItems.add(new JQuickProjectPhysicalNode.SelectItem(buildExpression(itemProto.getExpression()), itemProto.getAlias()));
                }
                return new JQuickProjectPhysicalNode(selectItems, 
                        children.size() > 0 ? children.get(0) : null, projectProto.getDistinct());
            case HASH_JOIN:
                JQuickHashJoinNodeProto joinProto = proto.getHashJoin();
                return new JQuickHashJoinPhysicalNode(convertJoinType(joinProto.getJoinType()), 
                        children.size() > 0 ? children.get(0) : null, 
                        children.size() > 1 ? children.get(1) : null, 
                        buildExpression(joinProto.getCondition()), new ArrayList<>(), 
                        joinProto.getBuildSide() == JQuickBuildSideProto.BUILD_SIDE_LEFT ? JQuickHashJoinPhysicalNode.BuildSide.LEFT : JQuickHashJoinPhysicalNode.BuildSide.RIGHT, 
                        JQuickHashJoinPhysicalNode.JoinDistribution.LOCAL);
            case EXCHANGE:
                JQuickExchangeNodeProto exchangeProto = proto.getExchange();
                return new JQuickExchangePhysicalNode(convertExchangeType(exchangeProto.getExchangeType()), 
                        convertPartitionStrategy(exchangeProto.getPartitionStrategy()), 
                        new ArrayList<>(), exchangeProto.getParallelism(), 
                        children.size() > 0 ? children.get(0) : null);
            case LIMIT:
                JQuickLimitNodeProto limitProto = proto.getLimit();
                return new JQuickLimitPhysicalNode(limitProto.getLimit(), limitProto.getOffset(), 
                        children.size() > 0 ? children.get(0) : null);
            case SET_OPERATION:
                JQuickSetOperationNodeProto setOpProto = proto.getSetOperation();
                JQuickSQLOperationType opType = convertSetOperationType(setOpProto.getOperationType());
                return new JQuickSetOperationPhysicalNode(opType,
                        children.size() > 0 ? children.get(0) : null,
                        children.size() > 1 ? children.get(1) : null);
            case NESTED_LOOP_JOIN:
                JQuickNestedLoopJoinNodeProto nestedJoinProto = proto.getNestedLoopJoin();
                return new JQuickNestedLoopJoinPhysicalNode(convertJoinType(nestedJoinProto.getJoinType()),
                        children.size() > 0 ? children.get(0) : null,
                        children.size() > 1 ? children.get(1) : null,
                        buildExpression(nestedJoinProto.getCondition()));
            case HASH_AGGREGATE:
                JQuickHashAggregateNodeProto aggProto = proto.getHashAggregate();
                List<JQuickExpression> groupKeys = new ArrayList<>();
                for (JQuickExpressionProto keyProto : aggProto.getGroupKeysList()) {
                    groupKeys.add(buildExpression(keyProto));
                }
                List<JQuickHashAggregatePhysicalNode.AggregateFunction> aggregates = new ArrayList<>();
                for (JQuickHashAggregateNodeProto.AggregateFunctionProto funcProto : aggProto.getAggregatesList()) {
                    aggregates.add(new JQuickHashAggregatePhysicalNode.AggregateFunction(
                            funcProto.getFunctionName(),
                            buildExpression(funcProto.getArgument()),
                            funcProto.getDistinct(),
                            funcProto.getAlias(),
                            funcProto.getIsCountStar(),
                            funcProto.getSeparator(),
                            convertAggregateStage(funcProto.getInternalStage())
                    ));
                }
                return new JQuickHashAggregatePhysicalNode(groupKeys, aggregates,
                        children.size() > 0 ? children.get(0) : null,
                        buildExpression(aggProto.getHavingCondition()),
                        convertAggregateStage(aggProto.getStage())
                );
            case SORT:
                JQuickSortNodeProto sortProto = proto.getSort();
                List<JQuickSortPhysicalNode.OrderByItem> sortItems = new ArrayList<>();
                for (JQuickSortNodeProto.OrderByItemProto itemProto : sortProto.getOrderByItemsList()) {
                    sortItems.add(new JQuickSortPhysicalNode.OrderByItem(itemProto.getColumnName(), itemProto.getAscending(), itemProto.getNullsFirst()));
                }
                return new JQuickSortPhysicalNode(sortItems, children.size() > 0 ? children.get(0) : null);
            case VALUES:
                JQuickValuesNodeProto valuesProto = proto.getValues();
                return new JQuickValuesPhysicalNode(new ArrayList<>(), new ArrayList<>(valuesProto.getColumnNamesList()), new ArrayList<>());
            case WINDOW:
                return new JQuickWindowPhysicalNode(new ArrayList<>(), children.size() > 0 ? children.get(0) : null);
            case TOP_N:
                JQuickTopNNodeProto topNProto = proto.getTopN();
                List<JQuickSortPhysicalNode.OrderByItem> topNItems = new ArrayList<>();
                for (JQuickSortNodeProto.OrderByItemProto itemProto : topNProto.getOrderByItemsList()) {
                    topNItems.add(new JQuickSortPhysicalNode.OrderByItem(itemProto.getColumnName(), itemProto.getAscending(), itemProto.getNullsFirst()));
                }
                return new JQuickTopNPhysicalNode(topNItems, topNProto.getLimit(), topNProto.getOffset(), children.size() > 0 ? children.get(0) : null);
            case RECURSIVE_UNION:
                JQuickRecursiveUnionNodeProto recursiveProto = proto.getRecursiveUnion();
                return new JQuickRecursiveUnionPhysicalNode(recursiveProto.getCteName(),
                        new ArrayList<>(recursiveProto.getColumnNamesList()),
                        children.size() > 0 ? children.get(0) : null,
                        children.size() > 1 ? children.get(1) : null,
                        recursiveProto.getUnionAll());
            case EMPTY:
                return JQuickEmptyPhysicalNode.INSTANCE;
            default:
                return null;
        }
    }

}
