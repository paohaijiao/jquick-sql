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
import com.github.paohaijiao.expression.domain.JQuickLiteralExpression;
import com.github.paohaijiao.logic.JQuickLogicalPlanNode;
import com.github.paohaijiao.logic.domain.JQuickEmptyNode;
import com.github.paohaijiao.logic.domain.JQuickFilterNode;
import com.github.paohaijiao.optimizer.JQuickOptimizerRule;

/**
 * 冗余过滤移除：移除始终为true或重复的过滤条件
 */
public class JQuickRedundantFilterRemovalRule  implements JQuickOptimizerRule {
    @Override
    public JQuickLogicalPlanNode apply(JQuickLogicalPlanNode node) {
        if (node instanceof JQuickFilterNode) {
            JQuickFilterNode filter = (JQuickFilterNode) node;
            JQuickExpression predicate = filter.getPredicate();
            if (isAlwaysTrue(predicate)) { // 检查是否为恒真条件
                return filter.getChild();//直接返回子节点，移除了Filter
            }
            if (isAlwaysFalse(predicate)) {
                return new JQuickEmptyNode();
            }
        }
        return node;
    }
    private boolean isAlwaysTrue(JQuickExpression expr) {
        if (expr instanceof JQuickLiteralExpression) {
            Object value = ((JQuickLiteralExpression) expr).getValue();
            return Boolean.TRUE.equals(value);
        }
        return false;
    }
    private boolean isAlwaysFalse(JQuickExpression expr) {
        if (expr instanceof JQuickLiteralExpression) {
            Object value = ((JQuickLiteralExpression) expr).getValue();
            return Boolean.FALSE.equals(value);
        }
        return false;
    }
}
