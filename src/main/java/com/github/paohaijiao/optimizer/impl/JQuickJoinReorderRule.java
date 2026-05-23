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
import com.github.paohaijiao.logic.domain.JQuickFilterNode;
import com.github.paohaijiao.logic.domain.JQuickJoinNode;
import com.github.paohaijiao.logic.domain.JQuickProjectNode;
import com.github.paohaijiao.logic.domain.JQuickTableScanNode;
import com.github.paohaijiao.optimizer.JQuickOptimizerRule;

import java.util.*;

/**
 * Join重排序：根据表大小和选择率重新排列Join顺序
 * 只对INNER JOIN进行重排序，保持OUTER JOIN的语义
 */
public class JQuickJoinReorderRule implements JQuickOptimizerRule {
    @Override
    public JQuickLogicalPlanNode apply(JQuickLogicalPlanNode node) {
        if (node instanceof JQuickJoinNode) {
            JQuickJoinNode join = (JQuickJoinNode) node;
            if (join.getJoinType() == JQuickJoinType.INNER) {
                return reorderInnerJoins(join);
            }
        }
        return node;
    }

    /**
     * 重排序INNER JOIN
     * 策略：只将有过滤条件的表提前
     */
    private JQuickLogicalPlanNode reorderInnerJoins(JQuickJoinNode join) {
        JoinGraph graph = buildJoinGraph(join); //收集所有参与Join的表和对应的Join条件
        if (graph.tables.size() <= 1) {
            return join;
        }
        //只做一种优化：将有过滤条件的表移到前面
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
        if (isSameOrder(graph.tables, reordered)) { //如果顺序没变，直接返回
            return join;
        }
        return buildJoinTree(reordered, graph);//重建Join树
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
            if (join.getCondition() != null) {// 记录Join条件
                graph.addCondition(join);
            }
            collectJoinInfo(join.getLeft(), graph); // 递归处理子节点
            collectJoinInfo(join.getRight(), graph);
        } else {
            graph.addTable(node);// 叶子节点（表）
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
        return false;
    }

    /**
     * 提取表名（用于匹配Join条件）
     */
    private String extractTableName(JQuickLogicalPlanNode node) {
        if (node instanceof JQuickTableScanNode) {
            return ((JQuickTableScanNode) node).getTableName();
        }
        if (node instanceof JQuickProjectNode) {
            return extractTableName(((JQuickProjectNode) node).getChild());
        }
        if (node instanceof JQuickFilterNode) {
            return extractTableName(((JQuickFilterNode) node).getChild());
        }
        // 对于子查询或复杂节点，使用节点ID
        JAssert.throwNewException( "tableName  is null");
        return null;
    }

    /**
     * 获取表涉及的列（用于匹配Join条件）
     * 通过分析子树中的所有表达式
     */
    private Set<String> getTableColumns(JQuickLogicalPlanNode node) {
        Set<String> columns = new HashSet<>();
        if (node instanceof JQuickTableScanNode) {
            JQuickTableScanNode scan = (JQuickTableScanNode) node;
            if (scan.getRequiredColumns() != null) {
                columns.addAll(scan.getRequiredColumns());
            }
        } else if (node instanceof JQuickProjectNode) {
            JQuickProjectNode project = (JQuickProjectNode) node;
            for (JQuickProjectNode.SelectItem item : project.getSelectItems()) {
                columns.addAll(item.getExpression().getReferencedColumns());
            }
            columns.addAll(getTableColumns(project.getChild()));// 递归获取子节点列
        } else if (node instanceof JQuickFilterNode) {
            JQuickFilterNode filter = (JQuickFilterNode) node;
            columns.addAll(filter.getPredicate().getReferencedColumns());
            columns.addAll(getTableColumns(filter.getChild()));
        } else if (node instanceof JQuickJoinNode) {
            JQuickJoinNode join = (JQuickJoinNode) node;
            if (join.getCondition() != null) {
                columns.addAll(join.getCondition().getReferencedColumns());
            }
            columns.addAll(getTableColumns(join.getLeft()));
            columns.addAll(getTableColumns(join.getRight()));
        }

        return columns;
    }

    /**
     * 判断两个表是否相同
     */
    private boolean isSameTable(JQuickLogicalPlanNode a, JQuickLogicalPlanNode b) {
        return extractTableName(a).equals(extractTableName(b));
    }

    /**
     * 检查顺序是否相同
     */
    private boolean isSameOrder(List<JQuickLogicalPlanNode> original, List<JQuickLogicalPlanNode> reordered) {
        if (original.size() != reordered.size()) {
            return false;
        }
        for (int i = 0; i < original.size(); i++) {
            if (!isSameTable(original.get(i), reordered.get(i))) {
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
            // 查找两个表之间的Join条件
            JQuickExpression condition = graph.findCondition(extractTableName(result), extractTableName(right));
            result = new JQuickJoinNode(JQuickJoinType.INNER, result, right, condition  );// 可能为null（笛卡尔积）
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
            String leftName = extractTableNameFromNode(join.getLeft());
            String rightName = extractTableNameFromNode(join.getRight());
            conditions.computeIfAbsent(leftName, k -> new HashMap()).put(rightName, join.getCondition());
            conditions.computeIfAbsent(rightName, k -> new HashMap()).put(leftName, join.getCondition());
        }

        JQuickExpression findCondition(String left, String right) {
            Map<String, JQuickExpression> leftMap = conditions.get(left);
            if (leftMap != null) {
                return leftMap.get(right);
            }
            return null;
        }

        private String extractTableNameFromNode(JQuickLogicalPlanNode node) {
            if (node instanceof JQuickTableScanNode) {
                return ((JQuickTableScanNode) node).getTableName();
            }
            if (node instanceof JQuickProjectNode) {
                return extractTableNameFromNode(((JQuickProjectNode) node).getChild());
            }
            if (node instanceof JQuickFilterNode) {
                return extractTableNameFromNode(((JQuickFilterNode) node).getChild());
            }
            JAssert.throwNewException( "tableName is null");
            if (node instanceof JQuickJoinNode) {
                return "subquery_" + System.identityHashCode(node);    // 如果是Join，返回组合名
            }
            return "unknown_" + System.identityHashCode(node);
        }
    }
}
