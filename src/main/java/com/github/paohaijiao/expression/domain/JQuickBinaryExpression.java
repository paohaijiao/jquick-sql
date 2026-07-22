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
package com.github.paohaijiao.expression.domain;


import com.github.paohaijiao.enums.JQuickBinaryOperator;
import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.statement.JQuickRow;

import java.util.ArrayList;
import java.util.List;

/**
 * 二元表达式 - 如 a + b, a > b, a AND b
 */
public class JQuickBinaryExpression implements JQuickExpression {

    private final JQuickExpression left;

    private final JQuickExpression right;

    private final JQuickBinaryOperator operator;

    public JQuickBinaryExpression(JQuickExpression left, JQuickExpression right, JQuickBinaryOperator operator) {
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    @Override
    public Object evaluate(JQuickRow row) {
        Object leftVal = left.evaluate(row);
        Object rightVal = right.evaluate(row);
        return operator.apply(leftVal, rightVal);
    }

    @Override
    public Class<?> getType() {
        switch (operator) {
            case AND: case OR:
            case EQ: case NE: case GT: case LT: case GE: case LE:
            case LIKE: case NOT_LIKE:
                return Boolean.class;
            default:
                return Number.class;
        }
    }

    @Override
    public boolean isConstant() {
        return left.isConstant() && right.isConstant();
    }

    @Override
    public List<String> getReferencedColumns() {
        List<String> columns = new ArrayList<>();
        columns.addAll(left.getReferencedColumns());
        columns.addAll(right.getReferencedColumns());
        return columns;
    }

    @Override
    public String toSql() {
        boolean needParen = needParentheses();
        String leftSql = left.toSql();
        String rightSql = right.toSql();
        if (needParen) {
            return "(" + leftSql + " " + operator.getSymbol() + " " + rightSql + ")";
        }
        return leftSql + " " + operator.getSymbol() + " " + rightSql;
    }

    private boolean needParentheses() {
        if (left instanceof JQuickBinaryExpression) {
            JQuickBinaryExpression leftBinary = (JQuickBinaryExpression) left;
            if (leftBinary.operator.getPrecedence() < this.operator.getPrecedence()) {
                return true;
            }
        }
        if (right instanceof JQuickBinaryExpression) {
            JQuickBinaryExpression rightBinary = (JQuickBinaryExpression) right;
            if (rightBinary.operator.getPrecedence() < this.operator.getPrecedence()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public JQuickExpression clone() {
        return new JQuickBinaryExpression(left.clone(), right.clone(), operator);
    }

    public JQuickExpression getLeft() { return left; }

    public JQuickExpression getRight() { return right; }

    public JQuickBinaryOperator getOperator() { return operator; }
}
