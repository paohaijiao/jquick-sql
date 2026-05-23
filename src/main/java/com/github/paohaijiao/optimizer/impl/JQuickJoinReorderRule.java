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
package com.github.paohaijiao.optimizer.impl;

import com.github.paohaijiao.enums.JQuickJoinType;
import com.github.paohaijiao.exception.JAssert;
import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.logic.JQuickLogicalPlanNode;
import com.github.paohaijiao.logic.domain.*;
import com.github.paohaijiao.optimizer.JQuickOptimizerRule;

import java.util.*;

/**
 * Join重排序：根据表大小和选择率重新排列Join顺序
 * 只对INNER JOIN进行重排序，保持OUTER JOIN的语义
 */
public class JQuickJoinReorderRule implements JQuickOptimizerRule {
    @Override
    public JQuickLogicalPlanNode apply(JQuickLogicalPlanNode node) {
        node = processChildren(node);
        if (node instanceof JQuickJoinNode) {
            JQuickJoinNode join = (JQuickJoinNode) node;
            if (join.getJoinType() == JQuickJoinType.INNER) {
                return reorderInnerJoins(join);
            }
        }
        return node;
    }

    /**
     * 递归处理所有子节点
     */
    private JQuickLogicalPlanNode processChildren(JQuickLogicalPlanNode node) {
        List<JQuickLogicalPlanNode> children = node.getChildren();
        if (children == null || children.isEmpty()) {
            return node;
        }
        List<JQuickLogicalPlanNode> newChildren = new ArrayList<>();
        for (JQuickLogicalPlanNode child : children) {
            newChildren.add(apply(child));
        }
        return rebuildNode(node, newChildren);
    }

    /**
     * 根据原节点和优化后的子节点重建节点
     */
    private JQuickLogicalPlanNode rebuildNode(JQuickLogicalPlanNode node, List<JQuickLogicalPlanNode> newChildren) {
        if (node instanceof JQuickJoinNode) {
            JQuickJoinNode join = (JQuickJoinNode) node;
            return new JQuickJoinNode(join.getJoinType(), newChildren.get(0), newChildren.get(1), join.getCondition());
        }
        if (node instanceof JQuickProjectNode) {
            JQuickProjectNode project = (JQuickProjectNode) node;
            return new JQuickProjectNode(project.getSelectItems(), newChildren.get(0), project.isDistinct());
        }
        if (node instanceof JQuickFilterNode) {
            JQuickFilterNode filter = (JQuickFilterNode) node;
            return new JQuickFilterNode(filter.getPredicate(), newChildren.get(0));
        }
        if (node instanceof JQuickGroupByNode) {
            JQuickGroupByNode groupBy = (JQuickGroupByNode) node;
            return new JQuickGroupByNode(groupBy.getGroupKeys(), groupBy.getAggregateItems(), newChildren.get(0), groupBy.getHavingCondition());
        }
        if (node instanceof JQuickSortNode) {
            JQuickSortNode sort = (JQuickSortNode) node;
            return new JQuickSortNode(sort.getOrderByItems(), newChildren.get(0));
        }
        if (node instanceof JQuickLimitNode) {
            JQuickLimitNode limit = (JQuickLimitNode) node;
            return new JQuickLimitNode(limit.getLimit(), limit.getOffset(), newChildren.get(0));
        }
        return node;
    }

    /**
     * 重排序INNER JOIN
     * 策略：将有过滤条件的表提前
     */
    private JQuickLogicalPlanNode reorderInnerJoins(JQuickJoinNode join) {
        JoinGraph graph = buildJoinGraph(join);
        if (graph.tables.size() <= 1) {
            return join;
        }
        List<JQuickLogicalPlanNode> reordered = new ArrayList<>();
        List<JQuickLogicalPlanNode> noFilter = new ArrayList<>();
        for (JQuickLogicalPlanNode table : graph.tables) {
            if (hasFilterPredicate(table)) {
                reordered.add(table);
            } else {
                noFilter.add(table);
            }
        }
        reordered.addAll(noFilter);
        if (isSameOrder(graph.tables, reordered)) {
            return join;
        }
        return buildJoinTree(reordered, graph);
    }

    /**
     * 构建Join图：收集所有表和表之间的Join条件
     */
    private JoinGraph buildJoinGraph(JQuickJoinNode join) {
        JoinGraph graph = new JoinGraph();
        collectJoinInfo(join, graph);
        return graph;
    }

    /**
     * 递归收集Join信息
     */
    private void collectJoinInfo(JQuickLogicalPlanNode node, JoinGraph graph) {
        if (node instanceof JQuickJoinNode) {
            JQuickJoinNode join = (JQuickJoinNode) node;
            if (join.getCondition() != null) {
                graph.addCondition(join);
            }
            collectJoinInfo(join.getLeft(), graph);
            collectJoinInfo(join.getRight(), graph);
        } else {
            graph.addTable(node);
        }
    }

    /**
     * 检查节点是否有过滤条件
     */
    private boolean hasFilterPredicate(JQuickLogicalPlanNode node) {
        if (node instanceof JQuickFilterNode) {
            return true;
        }
        if (node instanceof JQuickTableScanNode) {
            return ((JQuickTableScanNode) node).getFilterPredicate() != null;
        }
        if (node instanceof JQuickProjectNode) {
            return hasFilterPredicate(((JQuickProjectNode) node).getChild());
        }
        if (node instanceof JQuickWithNode) {
            return hasFilterPredicateInCTE((JQuickWithNode) node);
        }
        return false;
    }

    /**
     * 检查 CTE 中是否有过滤条件
     */
    private boolean hasFilterPredicateInCTE(JQuickWithNode withNode) {
        for (JQuickLogicalPlanNode cte : withNode.getCtes().values()) {
            if (hasFilterPredicate(cte)) {
                return true;
            }
        }
        return hasFilterPredicate(withNode.getChild());
    }

    /**
     * 提取节点标识（优先使用别名）
     */
    private String extractNodeId(JQuickLogicalPlanNode node) {
        if (node instanceof JQuickTableScanNode) {
            JQuickTableScanNode scan = (JQuickTableScanNode) node;
            String alias = scan.getAlias();
            if (alias != null && !alias.isEmpty()) {
                return alias;
            }
            return scan.getTableName();
        }
        if (node instanceof JQuickProjectNode) {
            return extractNodeId(((JQuickProjectNode) node).getChild());
        }
        if (node instanceof JQuickFilterNode) {
            return extractNodeId(((JQuickFilterNode) node).getChild());
        }
        JAssert.throwNewException("Cannot extract node id from: " + node.getClass().getSimpleName());
        return null;
    }

    /**
     * 检查顺序是否相同
     */
    private boolean isSameOrder(List<JQuickLogicalPlanNode> original, List<JQuickLogicalPlanNode> reordered) {
        if (original.size() != reordered.size()) {
            return false;
        }
        for (int i = 0; i < original.size(); i++) {
            if (!extractNodeId(original.get(i)).equals(extractNodeId(reordered.get(i)))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 根据排序后的表和Join条件重建Join树
     */
    private JQuickLogicalPlanNode buildJoinTree(List<JQuickLogicalPlanNode> orderedTables, JoinGraph graph) {
        if (orderedTables.isEmpty()) return null;
        if (orderedTables.size() == 1) return orderedTables.get(0);
        JQuickLogicalPlanNode result = orderedTables.get(0);
        for (int i = 1; i < orderedTables.size(); i++) {
            JQuickLogicalPlanNode right = orderedTables.get(i);
            String leftId = extractNodeId(result);
            String rightId = extractNodeId(right);
            JQuickExpression condition = graph.findCondition(leftId, rightId);
            result = new JQuickJoinNode(JQuickJoinType.INNER, result, right, condition);
        }
        return result;
    }

    /**
     * Join图：存储所有表和Join条件
     */
    private static class JoinGraph {
        List<JQuickLogicalPlanNode> tables = new ArrayList<>();
        Map<String, Map<String, JQuickExpression>> conditions = new HashMap<>();
        void addTable(JQuickLogicalPlanNode node) {
            tables.add(node);
        }

        void addCondition(JQuickJoinNode join) {
            String leftId = extractNodeIdFromNode(join.getLeft());
            String rightId = extractNodeIdFromNode(join.getRight());
            conditions.computeIfAbsent(leftId, k -> new HashMap()).put(rightId, join.getCondition());
            conditions.computeIfAbsent(rightId, k -> new HashMap()).put(leftId, join.getCondition());
        }

        JQuickExpression findCondition(String left, String right) {
            Map<String, JQuickExpression> leftMap = conditions.get(left);
            if (leftMap != null) {
                return leftMap.get(right);
            }
            return null;
        }

        /**
         * 提取节点标识（用于 JoinGraph 内部）
         */
        private String extractNodeIdFromNode(JQuickLogicalPlanNode node) {
            if (node instanceof JQuickTableScanNode) {
                JQuickTableScanNode scan = (JQuickTableScanNode) node;
                String alias = scan.getAlias();
                if (alias != null && !alias.isEmpty()) {
                    return alias;
                }
                return scan.getTableName();
            }
            if (node instanceof JQuickProjectNode) {
                return extractNodeIdFromNode(((JQuickProjectNode) node).getChild());
            }
            if (node instanceof JQuickFilterNode) {
                return extractNodeIdFromNode(((JQuickFilterNode) node).getChild());
            }
            JAssert.throwNewException("Cannot extract node id from node: " + node.getClass().getSimpleName());
            return null;
        }
    }
}
