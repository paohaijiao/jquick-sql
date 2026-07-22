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
import com.github.paohaijiao.logic.domain.*;
import com.github.paohaijiao.optimizer.JQuickOptimizerRule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 投影合并：将连续的Project节点合并为一个，减少不必要的数据转换和列映射开销
 * -- 原始：嵌套投影
 * SELECT name, age FROM (
 *     SELECT id, name, age, city FROM users
 * ) t
 *
 * -- 合并后：单一投影
 * SELECT name, age FROM users
 */
public class JQuickProjectionMergeRule implements JQuickOptimizerRule {

    @Override
    public JQuickLogicalPlanNode apply(JQuickLogicalPlanNode node) {
        node = processChildren(node);
        return mergeProjections(node);
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
        if (node instanceof JQuickFilterNode) {
            JQuickFilterNode filter = (JQuickFilterNode) node;
            return new JQuickFilterNode(filter.getPredicate(), newChildren.get(0));
        }
        if (node instanceof JQuickJoinNode) {
            JQuickJoinNode join = (JQuickJoinNode) node;
            return new JQuickJoinNode(join.getJoinType(), newChildren.get(0), newChildren.get(1), join.getCondition(),join.getJoinKeys());
        }
        if (node instanceof JQuickGroupByNode) {
            JQuickGroupByNode groupBy = (JQuickGroupByNode) node;
            return new JQuickGroupByNode(groupBy.getGroupKeys(), groupBy.getAggregateItems(),
                    newChildren.get(0), groupBy.getHavingCondition());
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
     * 投影合并的核心逻辑（递归合并所有连续的 Project）
     */
    private JQuickLogicalPlanNode mergeProjections(JQuickLogicalPlanNode node) {
        if (node instanceof JQuickProjectNode) {
            JQuickProjectNode outer = (JQuickProjectNode) node;
            JQuickLogicalPlanNode child = outer.getChild();
            if (child instanceof JQuickProjectNode) {
                JQuickProjectNode inner = (JQuickProjectNode) child;
                Map<String, JQuickExpression> innerExprMap = new HashMap<>();
                for (JQuickProjectNode.SelectItem item : inner.getSelectItems()) {
                    innerExprMap.put(item.getAlias(), item.getExpression());
                }
                List<JQuickProjectNode.SelectItem> merged = new ArrayList<>();
                for (JQuickProjectNode.SelectItem outerItem : outer.getSelectItems()) {
                    JQuickExpression replaced = replaceColumns(outerItem.getExpression(), innerExprMap);
                    merged.add(new JQuickProjectNode.SelectItem(replaced, outerItem.getAlias()));
                }
                boolean distinct = outer.isDistinct() || inner.isDistinct();
                return new JQuickProjectNode(merged, inner.getChild(), distinct);
            }
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
        }
        else if (expr instanceof JQuickBinaryExpression) {
            JQuickBinaryExpression binary = (JQuickBinaryExpression) expr;
            return new JQuickBinaryExpression(replaceColumns(binary.getLeft(), columnMap), replaceColumns(binary.getRight(), columnMap), binary.getOperator());
        }
        return expr;
    }
}
