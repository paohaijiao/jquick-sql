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
import com.github.paohaijiao.expression.domain.JQuickLiteralExpression;
import com.github.paohaijiao.logic.JQuickLogicalPlanNode;
import com.github.paohaijiao.logic.domain.*;
import com.github.paohaijiao.optimizer.JQuickOptimizerRule;

import java.util.ArrayList;
import java.util.List;

/**
 * 冗余过滤移除：移除始终为true或重复的过滤条件
 */
public class JQuickRedundantFilterRemovalRule implements JQuickOptimizerRule {

    @Override
    public JQuickLogicalPlanNode apply(JQuickLogicalPlanNode node) {
        JQuickLogicalPlanNode result = removeRedundantFilters(node);
        return processChildren(result);
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
        if (node instanceof JQuickFilterNode) {
            JQuickFilterNode filter = (JQuickFilterNode) node;
            return new JQuickFilterNode(filter.getPredicate(), newChildren.get(0));
        }
        if (node instanceof JQuickProjectNode) {
            JQuickProjectNode project = (JQuickProjectNode) node;
            return new JQuickProjectNode(project.getSelectItems(), newChildren.get(0), project.isDistinct());
        }
        if (node instanceof JQuickJoinNode) {
            JQuickJoinNode join = (JQuickJoinNode) node;
            return new JQuickJoinNode(join.getJoinType(), newChildren.get(0), newChildren.get(1), join.getCondition(),join.getJoinKeys());
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
     * 冗余过滤移除的核心逻辑
     */
    private JQuickLogicalPlanNode removeRedundantFilters(JQuickLogicalPlanNode node) {
        if (node instanceof JQuickFilterNode) {
            JQuickFilterNode filter = (JQuickFilterNode) node;
            JQuickExpression predicate = filter.getPredicate();
            if (isAlwaysFalse(predicate)) {
                return new JQuickEmptyNode();
            }
            if (isAlwaysTrue(predicate)) {
                return filter.getChild();
            }
        }
        return node;
    }

    /**
     * 判断表达式是否为恒真
     */
    private boolean isAlwaysTrue(JQuickExpression expr) {
        if (expr instanceof JQuickLiteralExpression) {
            Object value = ((JQuickLiteralExpression) expr).getValue();
            return Boolean.TRUE.equals(value);
        }
        if (expr instanceof JQuickBinaryExpression) {
            JQuickBinaryExpression binary = (JQuickBinaryExpression) expr;
            if (binary.getOperator() == com.github.paohaijiao.enums.JQuickBinaryOperator.EQ) {
                JQuickExpression left = binary.getLeft();
                JQuickExpression right = binary.getRight();
                if (left.isConstant() && right.isConstant()) {
                    Object leftVal = left.evaluate(null);
                    Object rightVal = right.evaluate(null);
                    return leftVal.equals(rightVal);
                }
            }
        }

        return false;
    }

    /**
     * 判断表达式是否为恒假
     */
    private boolean isAlwaysFalse(JQuickExpression expr) {
        if (expr instanceof JQuickLiteralExpression) {
            Object value = ((JQuickLiteralExpression) expr).getValue();
            return Boolean.FALSE.equals(value);
        }
        if (expr instanceof JQuickBinaryExpression) {
            JQuickBinaryExpression binary = (JQuickBinaryExpression) expr;
            if (binary.getOperator() == com.github.paohaijiao.enums.JQuickBinaryOperator.EQ) {
                JQuickExpression left = binary.getLeft();
                JQuickExpression right = binary.getRight();
                if (left.isConstant() && right.isConstant()) {
                    Object leftVal = left.evaluate(null);
                    Object rightVal = right.evaluate(null);
                    return !leftVal.equals(rightVal);
                }
            }
        }
        return false;
    }
}