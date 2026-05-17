/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Copyright (c) [2025-2099] Martin (goudingcheng@gmail.com)
 */
package com.github.paohaijiao.physical;

import com.github.paohaijiao.enums.JQuickBinaryOperator;
import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.expression.domain.JQuickBinaryExpression;
import com.github.paohaijiao.expression.domain.JQuickColumnRefExpression;
import com.github.paohaijiao.logic.JQuickLogicalPlanNode;
import com.github.paohaijiao.physical.node.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 逻辑计划 → 物理计划转换器
 * 根据成本模型选择最优的物理实现
 */
public class JQuickPhysicalPlanGenerator {

    private final JQuickCostModel costModel;
    private final JQuickStatisticsManager statsManager;

    public JQuickPhysicalPlanGenerator() {
        this.costModel = new JQuickCostModel();
        this.statsManager = new JQuickStatisticsManager();
    }

    public JQuickPhysicalPlanNode generate(JQuickLogicalPlanNode logicalPlan) {
        return convert(logicalPlan, new JQuickConversionContext());
    }

    public JQuickPhysicalPlanNode convert(JQuickLogicalPlanNode logicalPlan) {
        if (logicalPlan instanceof JQuickTableScanNode) {
            return convertTableScan((JQuickTableScanNode) logicalPlan);
        } else if (logicalPlan instanceof JQuickProjectNode) {
            return convertProject((JQuickProjectNode) logicalPlan);
        } else if (logicalPlan instanceof JQuickFilterNode) {
            return convertFilter((JQuickFilterNode) logicalPlan);
        } else if (logicalPlan instanceof JQuickJoinNode) {
            return convertJoin((JQuickJoinNode) logicalPlan);
        } else if (logicalPlan instanceof JQuickGroupByNode) {
            return convertGroupBy((JQuickGroupByNode) logicalPlan);
        } else if (logicalPlan instanceof JQuickSortNode) {
            return convertSort((JQuickSortNode) logicalPlan);
        } else if (logicalPlan instanceof JQuickLimitNode) {
            return convertLimit((JQuickLimitNode) logicalPlan);
        }
        throw new RuntimeException("Unknown logical plan node: " + logicalPlan.getClass());
    }

    /**
     * 表扫描物理计划 - 可选择索引扫描或全表扫描
     */
    private JQuickPhysicalPlanNode convertTableScan(JQuickTableScanNode node) {
        long tableSize = statsManager.getTableSize(node.getTableName());
        // 如果有过滤条件下推，考虑使用索引
        if (node.getFilterPredicate() != null) {
            String indexColumn = findIndexColumn(node.getFilterPredicate(), node.getTableName());
            if (indexColumn != null && tableSize > 10000) {
                return new JQuickIndexScanPhysicalNode(
                        node.getTableName(),
                        node.getAlias(),
                        node.getRequiredColumns(),
                        node.getFilterPredicate(),
                        indexColumn
                );
            }
            return new JQuickFilteredTableScanPhysicalNode(
                    node.getTableName(),
                    node.getAlias(),
                    node.getRequiredColumns(),
                    node.getFilterPredicate()
            );
        }

        return new JQuickTableScanPhysicalNode(
                node.getTableName(),
                node.getAlias(),
                node.getRequiredColumns()
        );
    }

    /**
     * 投影物理计划
     */
    private JQuickPhysicalPlanNode convertProject(JQuickProjectNode node, JQuickConversionContext ctx) {
        JQuickPhysicalPlanNode child = convert(node.getChild(), ctx);

        // 如果投影列很少且数据量大，使用延迟投影
        if (node.getSelectItems().size() <= 3 &&
                statsManager.estimateRows(node.getChild()) > 50000) {
            return new JQuickLazyProjectPhysicalNode(node.getSelectItems(), child);
        }

        return new JQuickProjectPhysicalNode(node.getSelectItems(), child, node.isDistinct());
    }

    /**
     * 过滤物理计划 - 可选择不同的过滤策略
     */
    private JQuickPhysicalPlanNode convertFilter(JQuickFilterNode node, JQuickConversionContext ctx) {
        JQuickPhysicalPlanNode child = convert(node.getChild(), ctx);
        JQuickExpression predicate = node.getPredicate();

        // 如果过滤条件选择率很低，考虑提前终止
        double selectivity = estimateSelectivity(predicate);
        long inputRows = statsManager.estimateRows(node.getChild());

        if (selectivity < 0.1 && inputRows > 100000) {
            return new JQuickEarlyTerminationFilterPhysicalNode(predicate, child);
        }

        // 如果过滤条件可以使用布隆过滤器加速
        if (canUseBloomFilter(predicate)) {
            return new BloomFilterPhysicalNode(predicate, child);
        }

        return new JQuickFilterPhysicalNode(predicate, child);
    }

    /**
     * 连接物理计划 - 根据成本选择最优连接算法
     */
    private JQuickPhysicalPlanNode convertJoin(JQuickJoinNode node, JQuickConversionContext ctx) {
        JQuickPhysicalPlanNode left = convert(node.getLeft(), ctx);
        JQuickPhysicalPlanNode right = convert(node.getRight(), ctx);

        long leftRows = statsManager.estimateRows(node.getLeft());
        long rightRows = statsManager.estimateRows(node.getRight());
        String joinColumn = extractJoinColumn(node.getCondition());

        JQuickCostModel.JoinCost leftCost = costModel.estimateJoinCost(leftRows, rightRows);
        JQuickCostModel.JoinCost rightCost = costModel.estimateJoinCost(rightRows, leftRows);

        // 选择小表作为驱动表
        boolean leftIsSmaller = leftRows < rightRows;

        // 检查是否有索引
        boolean hasIndexOnRight = statsManager.hasIndexOnColumn(
                getTableName(node.getRight()), joinColumn);
        boolean hasIndexOnLeft = statsManager.hasIndexOnColumn(
                getTableName(node.getLeft()), joinColumn);

        // 选择最优连接算法
        if (hasIndexOnRight && rightRows > 10000) {
            return new JQuickIndexNestedLoopJoinPhysicalNode(
                    node.getJoinType(), left, right, node.getCondition(), true);
        }

        if (hasIndexOnLeft && leftRows > 10000) {
            return new IndexNestedLoopJoinPhysicalNode(
                    node.getJoinType(), left, right, node.getCondition(), false);
        }

        if (Math.min(leftRows, rightRows) < 1000) {
            return new JQuickNestedLoopJoinPhysicalNode(
                    node.getJoinType(), left, right, node.getCondition(), leftIsSmaller);
        }

        if (leftRows > 10000 && rightRows > 10000) {
            return new JQuickHashJoinPhysicalNode(
                    node.getJoinType(), left, right, node.getCondition(), leftIsSmaller);
        }

        if (node.getJoinType() == JQuickJoinNode.JoinType.INNER) {
            return new SortMergeJoinPhysicalNode(
                    node.getJoinType(), left, right, node.getCondition());
        }

        return new JQuickNestedLoopJoinPhysicalNode(
                node.getJoinType(), left, right, node.getCondition(), leftIsSmaller);
    }

    /**
     * 分组聚合物理计划
     */
    private JQuickPhysicalPlanNode convertGroupBy(JQuickGroupByNode node, JQuickConversionContext ctx) {
        JQuickPhysicalPlanNode child = convert(node.getChild(), ctx);
        long inputRows = statsManager.estimateRows(node.getChild());
        int groupKeyCount = node.getGroupKeys().size();
        int distinctValues = estimateDistinctValues(node.getGroupKeys(), node.getChild());

        // 如果分组键基数小，使用哈希聚合
        if (distinctValues < 10000 || inputRows < 50000) {
            return new JQuickHashAggregatePhysicalNode(
                    node.getGroupKeys(), node.getAggregateItems(),
                    node.getHavingCondition(), child);
        }

        // 如果分组键基数大且有序，使用排序聚合
        if (isSorted(node.getChild(), node.getGroupKeys())) {
            return new SortAggregatePhysicalNode(
                    node.getGroupKeys(), node.getAggregateItems(),
                    node.getHavingCondition(), child);
        }

        // 大数据集使用外部聚合
        if (inputRows > 1000000) {
            return new ExternalHashAggregatePhysicalNode(
                    node.getGroupKeys(), node.getAggregateItems(),
                    node.getHavingCondition(), child);
        }

        return new JQuickHashAggregatePhysicalNode(
                node.getGroupKeys(), node.getAggregateItems(),
                node.getHavingCondition(), child);
    }

    /**
     * 排序物理计划
     */
    private JQuickPhysicalPlanNode convertSort(JQuickSortNode node, JQuickConversionContext ctx) {
        JQuickPhysicalPlanNode child = convert(node.getChild(), ctx);
        long inputRows = statsManager.estimateRows(node.getChild());

        // 小数据集使用内存排序
        if (inputRows < 100000) {
            return new JQuickInMemorySortPhysicalNode(node.getOrderByItems(), child);
        }

        // 大数据集使用外部排序
        return new ExternalSortPhysicalNode(node.getOrderByItems(), child);
    }

    /**
     * 限制物理计划
     */
    private JQuickPhysicalPlanNode convertLimit(JQuickLimitNode node, JQuickConversionContext ctx) {
        JQuickPhysicalPlanNode child = convert(node.getChild(), ctx);

        // 如果有限制且子节点可以提前终止，使用Top-N优化
        if (node.getLimit() < 100 && canUseTopN(child)) {
            return new JQuickTopNPhysicalNode(node.getLimit(), node.getOffset(), child);
        }

        return new JQuickLimitPhysicalNode(node.getLimit(), node.getOffset(), child);
    }

    private JQuickPhysicalPlanNode convertWith(JQuickWithNode node, JQuickConversionContext ctx) {
        Map<String, JQuickPhysicalPlanNode> physicalCtes = new LinkedHashMap<>();
        for (Map.Entry<String, JQuickLogicalPlanNode> entry : node.getCtes().entrySet()) {
            physicalCtes.put(entry.getKey(), convert(entry.getValue(), ctx));
        }
        JQuickPhysicalPlanNode child = convert(node.getChild(), ctx);
        return new WithPhysicalNode(child, physicalCtes);
    }

    private JQuickPhysicalPlanNode convertSetOperation(JQuickSetOperationNode node, JQuickConversionContext ctx) {
        JQuickPhysicalPlanNode left = convert(node.getLeft(), ctx);
        JQuickPhysicalPlanNode right = convert(node.getRight(), ctx);
        return new SetOperationPhysicalNode(left, right, node.getOperationType());
    }
    private double estimateSelectivity(JQuickExpression predicate) {
        if (predicate instanceof JQuickBinaryExpression) {
            JQuickBinaryExpression binary = (JQuickBinaryExpression) predicate;
            switch (binary.getOperator()) {
                case EQ: return 0.1;
                case GT: case LT: case GE: case LE: return 0.33;
                case AND:
                    return estimateSelectivity(binary.getLeft()) * estimateSelectivity(binary.getRight());
                case OR:
                    return estimateSelectivity(binary.getLeft()) +
                            estimateSelectivity(binary.getRight()) -
                            estimateSelectivity(binary.getLeft()) *
                                    estimateSelectivity(binary.getRight());
                default: return 0.5;
            }
        }
        return 0.5;
    }

    private String findIndexColumn(JQuickExpression predicate, String tableName) {
        // 提取等值条件的列名，检查是否有索引
        if (predicate instanceof JQuickBinaryExpression) {
            JQuickBinaryExpression binary = (JQuickBinaryExpression) predicate;
            if (binary.getOperator() == JQuickBinaryOperator.EQ) {
                String column = extractColumnName(binary.getLeft());
                if (column != null && statsManager.hasIndexOnColumn(tableName, column)) {
                    return column;
                }
                column = extractColumnName(binary.getRight());
                if (column != null && statsManager.hasIndexOnColumn(tableName, column)) {
                    return column;
                }
            }
        }
        return null;
    }

    private String extractColumnName(JQuickExpression expr) {
        if (expr instanceof JQuickColumnRefExpression) {
            return ((JQuickColumnRefExpression) expr).getColumnName();
        }
        return null;
    }

    private String extractJoinColumn(JQuickExpression condition) {
        if (condition instanceof JQuickBinaryExpression) {
            JQuickBinaryExpression binary = (JQuickBinaryExpression) condition;
            if (binary.getOperator() == JQuickBinaryOperator.EQ) {
                String left = extractColumnName(binary.getLeft());
                String right = extractColumnName(binary.getRight());
                return left != null ? left : right;
            }
        }
        return null;
    }

    private String getTableName(JQuickLogicalPlanNode node) {
        if (node instanceof JQuickTableScanNode) {
            return ((JQuickTableScanNode) node).getTableName();
        }
        return null;
    }

    private int estimateDistinctValues(List<JQuickExpression> groupKeys, LogicalPlanNode child) {
        // 估算分组键的基数
        return 1000; // 默认估算
    }

    private boolean isSorted(JQuickLogicalPlanNode node, List<JQuickExpression> groupKeys) {
        // 检查子节点是否已按分组键排序
        return node instanceof JQuickSortNode;
    }

    private boolean canUseTopN(JQuickPhysicalPlanNode node) {
        return node instanceof JQuickInMemorySortPhysicalNode ||
                node instanceof JQuickTableScanPhysicalNode;
    }

    private boolean canUseBloomFilter(JQuickExpression predicate) {
        // 判断是否可以使用布隆过滤器加速
        return predicate instanceof JQuickBinaryExpression &&
                ((JQuickBinaryExpression) predicate).getOperator() == BinaryOperator.EQ;
    }
}
