package com.github.paohaijiao.distributed.proto;

import com.github.paohaijiao.enums.JQuickBinaryOperator;
import com.github.paohaijiao.enums.JQuickExchangeType;
import com.github.paohaijiao.enums.JQuickPartitionStrategy;
import com.github.paohaijiao.enums.JQuickSQLOperationType;
import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.expression.domain.*;
import com.github.paohaijiao.physical.JQuickPhysicalPlanNode;
import com.github.paohaijiao.physical.domain.JQuickColumnStats;
import com.github.paohaijiao.physical.domain.JQuickPhysicalColumn;
import com.github.paohaijiao.physical.domain.JQuickPhysicalStats;
import com.github.paohaijiao.physical.domain.JQuickTablePartitionInfo;
import com.github.paohaijiao.physical.node.*;
import com.github.paohaijiao.proto.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JQuickprotoService {
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

        // 转换统计信息
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
            builder.addChildNodeIds("child_" + child.getNodeType());
        }
        return builder.build();
    }

    /**
     * 转换表达式为 Proto
     */
    public JQuickExpressionProto convertExpressionToProto(JQuickExpression expr) {
        if (expr == null) {
            return JQuickExpressionProto.newBuilder()
                    .setType(JQuickExpressionTypeProto.EXPR_LITERAL)
                    .setValue("null")
                    .build();
        }
        JQuickExpressionProto.Builder builder = JQuickExpressionProto.newBuilder();
        if (expr instanceof JQuickColumnRefExpression) {
            builder.setType(JQuickExpressionTypeProto.EXPR_COLUMN_REF).setValue(((JQuickColumnRefExpression) expr).getColumnName());
        } else if (expr instanceof JQuickLiteralExpression) {
            Object value = ((JQuickLiteralExpression) expr).getValue();
            builder.setType(JQuickExpressionTypeProto.EXPR_LITERAL).setValue(value != null ? value.toString() : "null");
        } else if (expr instanceof JQuickBinaryExpression) {
            JQuickBinaryExpression binary = (JQuickBinaryExpression) expr;
            builder.setType(JQuickExpressionTypeProto.EXPR_BINARY_OPERATOR).setBinaryOperator(convertBinaryOperatorToProto(binary.getOperator()));
            builder.addChildren(convertExpressionToProto(binary.getLeft()));
            builder.addChildren(convertExpressionToProto(binary.getRight()));
        } else if (expr instanceof JQuickUnaryExpression) {
            JQuickUnaryExpression unary = (JQuickUnaryExpression) expr;
            builder.setType(JQuickExpressionTypeProto.EXPR_UNARY_OPERATOR);
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
        }

        return builder.build();
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

    public JQuickBinaryOperatorProto convertBinaryOperatorToProto(com.github.paohaijiao.enums.JQuickBinaryOperator operator) {
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
            case PLUS: return JQuickBinaryOperatorProto.OP_PLUS;
            case MINUS: return JQuickBinaryOperatorProto.OP_MINUS;
            case MULTIPLY: return JQuickBinaryOperatorProto.OP_MULTIPLY;
            case DIVIDE: return JQuickBinaryOperatorProto.OP_DIVIDE;
            case MODULO: return JQuickBinaryOperatorProto.OP_MOD;
            default: return JQuickBinaryOperatorProto.OP_EQ;
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


}
