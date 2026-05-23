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

import com.github.paohaijiao.enums.JQuickBinaryOperator;
import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.expression.domain.JQuickBinaryExpression;
import com.github.paohaijiao.expression.domain.JQuickLiteralExpression;
import com.github.paohaijiao.logic.JQuickLogicalPlanNode;
import com.github.paohaijiao.logic.domain.*;
import com.github.paohaijiao.optimizer.JQuickOptimizerRule;
import com.github.paohaijiao.optimizer.JQuickRecursiveOptimizerRule;

import java.util.ArrayList;
import java.util.List;

/**
 * 常减少运行时计算开销
 * 未优化：扫描100万行 → 计算100万次乘法
 * 优化后：扫描100万行 → 计算0次（常量已提前计算）
 * 例如：1 + 2 → 3, age > 10 AND age < 20 → age BETWEEN 11 AND 19
 */
public class JQuickConstantFoldingRule extends JQuickRecursiveOptimizerRule implements JQuickOptimizerRule {
    @Override
    protected JQuickLogicalPlanNode optimizeNode(JQuickLogicalPlanNode node) {
        if (node instanceof JQuickFilterNode) {
            JQuickFilterNode filter = (JQuickFilterNode) node;
            JQuickExpression folded = foldConstants(filter.getPredicate());
            return new JQuickFilterNode(folded, filter.getChild());
        }
        else if (node instanceof JQuickProjectNode) {
            JQuickProjectNode project = (JQuickProjectNode) node;
            List<JQuickProjectNode.SelectItem> newItems = new ArrayList<>();
            for (JQuickProjectNode.SelectItem item : project.getSelectItems()) {
                JQuickExpression folded = foldConstants(item.getExpression());
                newItems.add(new JQuickProjectNode.SelectItem(folded, item.getAlias()));
            }
            return new JQuickProjectNode(newItems, project.getChild(), project.isDistinct());
        }
        else if (node instanceof JQuickGroupByNode) {
            JQuickGroupByNode groupBy = (JQuickGroupByNode) node;
            // 折叠 HAVING 条件
            JQuickExpression foldedHaving = groupBy.getHavingCondition() != null ?
                    foldConstants(groupBy.getHavingCondition()) : null;
            // 折叠聚合项中的表达式
            List<JQuickGroupByNode.AggregateItem> newItems = new ArrayList<>();
            for (JQuickGroupByNode.AggregateItem item : groupBy.getAggregateItems()) {
                JQuickExpression folded = foldConstants(item.getExpression());
                newItems.add(new JQuickGroupByNode.AggregateItem(folded, item.getFunctionName(),
                        item.getAlias(), item.isCountStar()));
            }
            return new JQuickGroupByNode(groupBy.getGroupKeys(), newItems,
                    groupBy.getChild(), foldedHaving);
        }
        else if (node instanceof JQuickJoinNode) {
            JQuickJoinNode join = (JQuickJoinNode) node;
            JQuickExpression folded = join.getCondition() != null ?
                    foldConstants(join.getCondition()) : null;
            return new JQuickJoinNode(join.getJoinType(), join.getLeft(), join.getRight(), folded);
        }
        else if (node instanceof JQuickWindowNode) {
            JQuickWindowNode window = (JQuickWindowNode) node;
            List<JQuickWindowNode.WindowFunction> newFunctions = new ArrayList<>();
            for (JQuickWindowNode.WindowFunction wf : window.getWindowFunctions()) {
                JQuickExpression folded = wf.getArgument() != null ? foldConstants(wf.getArgument()) : null;
                newFunctions.add(new JQuickWindowNode.WindowFunction(
                        wf.getFunctionName(), folded, wf.getWindowSpec(), wf.getAlias()));
            }
            return new JQuickWindowNode(newFunctions, window.getChild());
        }
        else if (node instanceof JQuickAggregateNode) {
            JQuickAggregateNode agg = (JQuickAggregateNode) node;
            JQuickExpression foldedHaving = agg.getHavingCondition() != null ? foldConstants(agg.getHavingCondition()) : null;
            if (agg.getGroupKeys() != null) {
                return new JQuickAggregateNode(agg.getGroupKeys(), agg.getAggregates(), agg.getChild(), foldedHaving, agg.isDistinct());
            } else {
                return new JQuickAggregateNode(agg.getGroupingSets(), agg.getAggregates(), agg.getChild(), foldedHaving);
            }
        }

        return node;
    }

    private JQuickExpression foldConstants(JQuickExpression expr) {
        if (expr instanceof JQuickBinaryExpression) {
            JQuickBinaryExpression binary = (JQuickBinaryExpression) expr;
            JQuickExpression left = foldConstants(binary.getLeft());
            JQuickExpression right = foldConstants(binary.getRight());
            if (left.isConstant() && right.isConstant()) {
                Object value = binary.getOperator().apply(left.evaluate(null), right.evaluate(null));
                return new JQuickLiteralExpression(value);
            }

            if (binary.getOperator() == JQuickBinaryOperator.PLUS) {
                if (isZero(right)) return left;
                if (isZero(left)) return right;
            }
            if (binary.getOperator() == JQuickBinaryOperator.MINUS) {
                if (isZero(right)) return left;
            }
            if (binary.getOperator() == JQuickBinaryOperator.MULTIPLY) {
                if (isOne(right)) return left;
                if (isOne(left)) return right;
                if (isZero(right) || isZero(left)) {
                    return new JQuickLiteralExpression(0);
                }
            }
            if (binary.getOperator() == JQuickBinaryOperator.DIVIDE) {
                if (isOne(right)) return left;
            }

            return new JQuickBinaryExpression(left, right, binary.getOperator());
        }
        return expr;
    }

    private boolean isZero(JQuickExpression expr) {
        if (expr instanceof JQuickLiteralExpression) {
            Object value = ((JQuickLiteralExpression) expr).getValue();
            return value instanceof Number && ((Number) value).doubleValue() == 0;
        }
        return false;
    }

    private boolean isOne(JQuickExpression expr) {
        if (expr instanceof JQuickLiteralExpression) {
            Object value = ((JQuickLiteralExpression) expr).getValue();
            return value instanceof Number && ((Number) value).doubleValue() == 1;
        }
        return false;
    }
}
