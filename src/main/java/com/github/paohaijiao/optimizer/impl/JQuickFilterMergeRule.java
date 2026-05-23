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
import com.github.paohaijiao.logic.JQuickLogicalPlanNode;
import com.github.paohaijiao.logic.domain.JQuickFilterNode;
import com.github.paohaijiao.optimizer.JQuickOptimizerRule;

/**
 * 过滤合并：将连续的Filter节点合并为一个
 * -- 原始：嵌套过滤
 * WHERE age > 18
 *   AND WHERE city = 'Beijing'
 * -- 合并后：单一过滤
 * WHERE age > 18 AND city = 'Beijing'
 */
public class JQuickFilterMergeRule implements JQuickOptimizerRule {
    @Override
    public JQuickLogicalPlanNode apply(JQuickLogicalPlanNode node) {
        if (node instanceof JQuickFilterNode) {
            JQuickFilterNode outer = (JQuickFilterNode) node;
            if (outer.getChild() instanceof JQuickFilterNode) {
                JQuickFilterNode inner = (JQuickFilterNode) outer.getChild();
                JQuickExpression combined = new JQuickBinaryExpression(outer.getPredicate(), inner.getPredicate(), JQuickBinaryOperator.AND);
                return new JQuickFilterNode(combined, inner.getChild());
            }
        }
        return node;
    }
}
