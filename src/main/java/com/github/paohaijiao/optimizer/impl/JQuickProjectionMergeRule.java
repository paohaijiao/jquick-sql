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
import com.github.paohaijiao.logic.domain.JQuickProjectNode;
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
        if (node instanceof JQuickProjectNode) {
            JQuickProjectNode outer = (JQuickProjectNode) node;
            if (outer.getChild() instanceof JQuickProjectNode) {
                JQuickProjectNode inner = (JQuickProjectNode) outer.getChild();
                Map<String, JQuickExpression> innerExprMap = new HashMap<>();// 构建内层投影的表达式映射
                for (JQuickProjectNode.SelectItem item : inner.getSelectItems()) {
                    innerExprMap.put(item.getAlias(), item.getExpression());
                }
                List<JQuickProjectNode.SelectItem> merged = new ArrayList<>();// 替换外层投影中的列引用
                for (JQuickProjectNode.SelectItem outerItem : outer.getSelectItems()) {
                    JQuickExpression replaced = replaceColumns(outerItem.getExpression(), innerExprMap);
                    merged.add(new JQuickProjectNode.SelectItem(replaced, outerItem.getAlias()));
                }
                return new JQuickProjectNode(merged, inner.getChild(), outer.isDistinct() || inner.isDistinct());
            }
        }
        return node;
    }

    private JQuickExpression replaceColumns(JQuickExpression expr, Map<String, JQuickExpression> columnMap) {
        if (expr instanceof JQuickColumnRefExpression) {
            JQuickColumnRefExpression col = (JQuickColumnRefExpression) expr;
            JQuickExpression replacement = columnMap.get(col.getColumnName());
            return replacement != null ? replacement : expr;
        } else if (expr instanceof JQuickBinaryExpression) {
            JQuickBinaryExpression binary = (JQuickBinaryExpression) expr;
            return new JQuickBinaryExpression(replaceColumns(binary.getLeft(), columnMap), replaceColumns(binary.getRight(), columnMap), binary.getOperator());
        }
        return expr;
    }
}
