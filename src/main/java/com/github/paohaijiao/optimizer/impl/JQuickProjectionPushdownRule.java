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

import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.expression.domain.JQuickBinaryExpression;
import com.github.paohaijiao.expression.domain.JQuickColumnRefExpression;
import com.github.paohaijiao.logic.JQuickLogicalPlanNode;
import com.github.paohaijiao.logic.domain.JQuickJoinNode;
import com.github.paohaijiao.logic.domain.JQuickProjectNode;
import com.github.paohaijiao.logic.domain.JQuickTableScanNode;
import com.github.paohaijiao.optimizer.JQuickOptimizerRule;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 投影下推：只保留需要的列，减少数据传输
 */
public class JQuickProjectionPushdownRule implements JQuickOptimizerRule {

    @Override
    public JQuickLogicalPlanNode apply(JQuickLogicalPlanNode node) {
        node = processChildren(node);
        return pushdownProjectionIfNeeded(node);
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
        if (node instanceof JQuickProjectNode) {
            JQuickProjectNode project = (JQuickProjectNode) node;
            return new JQuickProjectNode(project.getSelectItems(), newChildren.get(0), project.isDistinct());
        }
        if (node instanceof JQuickJoinNode) {
            JQuickJoinNode join = (JQuickJoinNode) node;
            return new JQuickJoinNode(join.getJoinType(), newChildren.get(0), newChildren.get(1), join.getCondition());
        }
        return node;
    }

    /**
     * 投影下推的核心逻辑
     */
    private JQuickLogicalPlanNode pushdownProjectionIfNeeded(JQuickLogicalPlanNode node) {
        if (node instanceof JQuickProjectNode) {
            JQuickProjectNode project = (JQuickProjectNode) node;
            Set<String> requiredColumns = project.getSelectItems().stream()
                    .map(JQuickProjectNode.SelectItem::getAlias)
                    .collect(Collectors.toSet());
            return pushdownProjection(project, requiredColumns);
        }
        return node;
    }

    /**
     * 投影下推的核心实现
     */
    private JQuickLogicalPlanNode pushdownProjection(JQuickProjectNode project, Set<String> requiredColumns) {
        JQuickLogicalPlanNode child = project.getChild();
        if (child instanceof JQuickTableScanNode) {
            JQuickTableScanNode scan = (JQuickTableScanNode) child;
            Set<String> actualRequiredColumns = extractActualColumns(project.getSelectItems(), requiredColumns);
            Set<String> columnNames = removeTableAliasPrefix(actualRequiredColumns, scan.getAlias());
            boolean selectAll=false;
            if(!project.getSelectItems().isEmpty()&&project.getSelectItems().get(0).isStar() ){
                selectAll=true;
            }
            return new JQuickProjectNode(project.getSelectItems(), new JQuickTableScanNode(scan.getTableName(), scan.getAlias(), selectAll||columnNames.isEmpty() ? null : columnNames, scan.getFilterPredicate()), project.isDistinct());
        } else if (child instanceof JQuickProjectNode) {
            JQuickProjectNode childProject = (JQuickProjectNode) child;
            Map<String, JQuickExpression> innerExprMap = new HashMap<>();
            for (JQuickProjectNode.SelectItem item : childProject.getSelectItems()) {
                innerExprMap.put(item.getAlias(), item.getExpression());
            }
            List<JQuickProjectNode.SelectItem> mergedItems = new ArrayList<>();
            Set<String> newRequiredColumns = new HashSet<>();
            for (JQuickProjectNode.SelectItem outerItem : project.getSelectItems()) {
                if (requiredColumns.contains(outerItem.getAlias())) {
                    JQuickExpression replaced = replaceColumns(outerItem.getExpression(), innerExprMap);
                    mergedItems.add(new JQuickProjectNode.SelectItem(replaced, outerItem.getAlias()));
                    newRequiredColumns.addAll(replaced.getReferencedColumns());
                }
            }
            JQuickLogicalPlanNode newChild = pushdownProjection(childProject, newRequiredColumns);
            boolean distinct = project.isDistinct() || childProject.isDistinct();
            return new JQuickProjectNode(mergedItems, newChild, distinct);
        } else if (child instanceof JQuickJoinNode) {
            JQuickJoinNode join = (JQuickJoinNode) child;
            Set<String> actualRequiredColumns = extractActualColumns(project.getSelectItems(), requiredColumns);
            Set<String> allRequired = new HashSet<>(actualRequiredColumns);
            if (join.getCondition() != null) {
                allRequired.addAll(join.getCondition().getReferencedColumns());
            }
            
            String leftAlias = getTableAlias(join.getLeft());
            String rightAlias = getTableAlias(join.getRight());
            
            Set<String> leftColumns = extractColumnsForTable(allRequired, leftAlias);
            Set<String> rightColumns = extractColumnsForTable(allRequired, rightAlias);
            
            JQuickLogicalPlanNode newLeft = pushdownToNode(join.getLeft(), leftColumns);
            JQuickLogicalPlanNode newRight = pushdownToNode(join.getRight(), rightColumns);
            JQuickJoinNode newJoin = new JQuickJoinNode(join.getJoinType(), newLeft, newRight, join.getCondition());
            return new JQuickProjectNode(project.getSelectItems(), newJoin, project.isDistinct());
        }

        return project;
    }

    /**
     * 从投影项中提取实际需要的原始列
     * <p>
     * 例如：SELECT name, price * 2 AS double_price
     * requiredColumns = {"name", "double_price"}
     * 实际需要的列 = {"name", "price"}
     */
    private Set<String> extractActualColumns(List<JQuickProjectNode.SelectItem> selectItems, Set<String> requiredColumns) {
        Set<String> actualColumns = new HashSet<>();
        if (requiredColumns == null || requiredColumns.isEmpty()) {
            return actualColumns;
        }
        Map<String, JQuickExpression> aliasToExpr = new HashMap<>();
        for (JQuickProjectNode.SelectItem item : selectItems) {
            aliasToExpr.put(item.getAlias(), item.getExpression());
        }
        for (String alias : requiredColumns) {
            JQuickExpression expr = aliasToExpr.get(alias);
            if (expr != null) {
                actualColumns.addAll(expr.getReferencedColumns());
            } else {
                actualColumns.add(alias);
            }
        }

        return actualColumns;
    }

    /**
     * 获取表的别名
     */
    private String getTableAlias(JQuickLogicalPlanNode node) {
        if (node instanceof JQuickTableScanNode) {
            return ((JQuickTableScanNode) node).getAlias();
        } else if (node instanceof JQuickProjectNode) {
            return getTableAlias(((JQuickProjectNode) node).getChild());
        } else if (node instanceof JQuickJoinNode) {
            return null;
        }
        return null;
    }

    /**
     * 根据表别名提取该表需要的列（移除别名前缀）
     */
    private Set<String> extractColumnsForTable(Set<String> allColumns, String tableAlias) {
        Set<String> result = new HashSet<>();
        for (String col : allColumns) {
            if (col.startsWith(tableAlias + ".")) {
                result.add(col.substring(tableAlias.length() + 1));
            } else if (tableAlias == null && !col.contains(".")) {
                result.add(col);
            }
        }
        return result;
    }

    /**
     * 移除列名中的表别名前缀
     */
    private Set<String> removeTableAliasPrefix(Set<String> columns, String tableAlias) {
        Set<String> result = new HashSet<>();
        if (tableAlias == null) {
            return columns;
        }
        for (String col : columns) {
            if (col.startsWith(tableAlias + ".")) {
                result.add(col.substring(tableAlias.length() + 1));
            } else {
                result.add(col);
            }
        }
        return result;
    }

    /**
     * 将需要的列下推到节点
     */
    private JQuickLogicalPlanNode pushdownToNode(JQuickLogicalPlanNode node, Set<String> requiredColumns) {
        String tableAlias = getTableAlias(node);
        
        if (node instanceof JQuickTableScanNode) {
            JQuickTableScanNode scan = (JQuickTableScanNode) node;
            Set<String> columnNames = removeTableAliasPrefix(requiredColumns, tableAlias);
            return new JQuickTableScanNode(scan.getTableName(), scan.getAlias(), columnNames.isEmpty() ? null : columnNames, scan.getFilterPredicate());
        } else if (node instanceof JQuickProjectNode) {
            JQuickProjectNode project = (JQuickProjectNode) node;
            List<JQuickProjectNode.SelectItem> filtered = new ArrayList<>();
            Set<String> newRequired = new HashSet<>();
            Map<String, JQuickExpression> aliasToExpr = new HashMap<>();
            for (JQuickProjectNode.SelectItem item : project.getSelectItems()) {
                aliasToExpr.put(item.getAlias(), item.getExpression());
            }
            for (String col : requiredColumns) {
                String unprefixedCol = tableAlias != null && col.startsWith(tableAlias + ".") 
                        ? col.substring(tableAlias.length() + 1) : col;
                JQuickExpression expr = aliasToExpr.get(unprefixedCol);
                if (expr != null) {
                    for (JQuickProjectNode.SelectItem item : project.getSelectItems()) {
                        if (item.getAlias().equals(unprefixedCol)) {
                            filtered.add(item);
                            if (tableAlias != null) {
                                for (String ref : expr.getReferencedColumns()) {
                                    newRequired.add(tableAlias + "." + ref);
                                }
                            } else {
                                newRequired.addAll(expr.getReferencedColumns());
                            }
                            break;
                        }
                    }
                } else {
                    newRequired.add(col);
                }
            }
            if (filtered.isEmpty() && newRequired.isEmpty()) {
                return pushdownToNode(project.getChild(), requiredColumns);
            }
            JQuickLogicalPlanNode newChild = pushdownToNode(project.getChild(), newRequired);
            if (filtered.isEmpty()) {
                return newChild;
            }
            return new JQuickProjectNode(filtered, newChild, project.isDistinct());
        } else if (node instanceof JQuickJoinNode) {
            JQuickJoinNode join = (JQuickJoinNode) node;
            Set<String> joinRequired = new HashSet<>(requiredColumns);
            if (join.getCondition() != null) {
                joinRequired.addAll(join.getCondition().getReferencedColumns());
            }
            
            String leftAlias = getTableAlias(join.getLeft());
            String rightAlias = getTableAlias(join.getRight());
            
            Set<String> leftColumns = extractColumnsForTable(joinRequired, leftAlias);
            Set<String> rightColumns = extractColumnsForTable(joinRequired, rightAlias);
            
            JQuickLogicalPlanNode newLeft = pushdownToNode(join.getLeft(), leftColumns);
            JQuickLogicalPlanNode newRight = pushdownToNode(join.getRight(), rightColumns);
            return new JQuickJoinNode(join.getJoinType(), newLeft, newRight, join.getCondition());
        }
        return node;
    }

    /**
     * 替换表达式中的列引用（带克隆）
     */
    private JQuickExpression replaceColumns(JQuickExpression expr, Map<String, JQuickExpression> columnMap) {
        if (expr instanceof JQuickColumnRefExpression) {
            JQuickColumnRefExpression col = (JQuickColumnRefExpression) expr;
            JQuickExpression replacement = columnMap.get(col.getColumnName());
            if (replacement != null) {
                return replacement.clone();
            }
            return col.clone();
        } else if (expr instanceof JQuickBinaryExpression) {
            JQuickBinaryExpression binary = (JQuickBinaryExpression) expr;
            JQuickExpression newLeft = replaceColumns(binary.getLeft(), columnMap);
            JQuickExpression newRight = replaceColumns(binary.getRight(), columnMap);
            return new JQuickBinaryExpression(newLeft, newRight, binary.getOperator());
        }return expr;
    }
}