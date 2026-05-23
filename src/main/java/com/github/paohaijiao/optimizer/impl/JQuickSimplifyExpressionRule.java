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
import com.github.paohaijiao.logic.domain.JQuickFilterNode;
import com.github.paohaijiao.optimizer.JQuickOptimizerRule;

/**
 * 表达式简化：简化复杂的表达式
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/5/23
 */
public class JQuickSimplifyExpressionRule implements JQuickOptimizerRule {
        @Override
        public JQuickLogicalPlanNode apply(JQuickLogicalPlanNode node) {
            if (node instanceof JQuickFilterNode) {
                JQuickFilterNode filter = (JQuickFilterNode) node;
                JQuickExpression simplified = simplifyExpression(filter.getPredicate());
                return new JQuickFilterNode(simplified, filter.getChild());

            }
            return node;
        }

        private JQuickExpression simplifyExpression(JQuickExpression expr) {
            if (expr instanceof JQuickLiteralExpression) {
                return expr;  // 常量不需要简化
            }
            if (expr instanceof JQuickBinaryExpression) {
                JQuickBinaryExpression binary = (JQuickBinaryExpression) expr;
                JQuickExpression left = simplifyExpression(binary.getLeft());
                JQuickExpression right = simplifyExpression(binary.getRight());
                if (binary.getOperator() == JQuickBinaryOperator.AND) {//  x AND true → x
                    if (isTrue(right)) return left;
                    if (isTrue(left)) return right;
                    if (isFalse(right) || isFalse(left)) {// x AND false → false
                        return new JQuickLiteralExpression(false);
                    }
                    if (left.equals(right)) return left;// x AND x → x
                }
                if (binary.getOperator() == JQuickBinaryOperator.OR) { // x OR false → x
                    if (isFalse(right)) return left;
                    if (isFalse(left)) return right;
                    if (isTrue(right) || isTrue(left)) {// x OR true → true
                        return new JQuickLiteralExpression(true);
                    }
                    if (left.equals(right)) return left; // x OR x → x
                }
                if (binary.getOperator() == JQuickBinaryOperator.PLUS) {//x + 0 → x
                    if (isZero(right)) return left;
                    if (isZero(left)) return right;
                }


                if (binary.getOperator() == JQuickBinaryOperator.MINUS) {//x - 0 → x
                    if (isZero(right)) return left;
                }


                if (binary.getOperator() == JQuickBinaryOperator.MULTIPLY) {// x * 1 → x
                    if (isOne(right)) return left;
                    if (isOne(left)) return right;
                    if (isZero(right) || isZero(left)) { // x * 0 → 0
                        return new JQuickLiteralExpression(0);
                    }
                }
                if (binary.getOperator() == JQuickBinaryOperator.DIVIDE) {//x / 1 → x
                    if (isOne(right)) return left;
                }

                if (binary.getOperator() == JQuickBinaryOperator.EQ) {// x = x → true
                    if (left.equals(right)) {
                        return new JQuickLiteralExpression(true);
                    }
                }
                if (binary.getOperator() == JQuickBinaryOperator.NE) {  // x != x → false
                    if (left.equals(right)) {
                        return new JQuickLiteralExpression(false);
                    }
                }
                //常量比较折叠
                if (left instanceof JQuickLiteralExpression && right instanceof JQuickLiteralExpression) {
                    Object result = binary.getOperator().apply(((JQuickLiteralExpression) left).getValue(), ((JQuickLiteralExpression) right).getValue());
                    return new JQuickLiteralExpression(result);
                }
                return new JQuickBinaryExpression(left, right, binary.getOperator());
            }

            return expr;
        }
        private boolean isTrue(JQuickExpression expr) {
            if (expr instanceof JQuickLiteralExpression) {
                Object value = ((JQuickLiteralExpression) expr).getValue();
                return Boolean.TRUE.equals(value);
            }
            return false;
        }

        private boolean isFalse(JQuickExpression expr) {
            if (expr instanceof JQuickLiteralExpression) {
                Object value = ((JQuickLiteralExpression) expr).getValue();
                return Boolean.FALSE.equals(value);
            }
            return false;
        }

        private boolean isZero(JQuickExpression expr) {
            if (expr instanceof JQuickLiteralExpression) {
                Object value = ((JQuickLiteralExpression) expr).getValue();
                if (value instanceof Number) {
                    return ((Number) value).doubleValue() == 0;
                }
            }
            return false;
        }

        private boolean isOne(JQuickExpression expr) {
            if (expr instanceof JQuickLiteralExpression) {
                Object value = ((JQuickLiteralExpression) expr).getValue();
                if (value instanceof Number) {
                    return ((Number) value).doubleValue() == 1;
                }
            }
            return false;
        }
}
