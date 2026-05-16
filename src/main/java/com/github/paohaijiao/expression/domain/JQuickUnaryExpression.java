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

import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.statement.JQuickRow;

import java.util.List;

/**
 * 一元表达式 - 如 NOT a, -a, +a
 */
public class JQuickUnaryExpression implements JQuickExpression {

    public enum UnaryOperator {
        NOT("NOT", false),
        PLUS("+", false),
        MINUS("-", false),
        IS_NULL("IS NULL", false),
        IS_NOT_NULL("IS NOT NULL", false);

        private final String symbol;
        private final boolean isPostfix;

        UnaryOperator(String symbol, boolean isPostfix) {
            this.symbol = symbol;
            this.isPostfix = isPostfix;
        }

        public String getSymbol() { return symbol; }
        public boolean isPostfix() { return isPostfix; }
    }

    private final UnaryOperator operator;
    private final JQuickExpression expression;

    public JQuickUnaryExpression(UnaryOperator operator, JQuickExpression expression) {
        this.operator = operator;
        this.expression = expression;
    }

    @Override
    public Object evaluate(JQuickRow row) {
        Object value = expression.evaluate(row);

        switch (operator) {
            case NOT:
                if (value instanceof Boolean) {
                    return !(Boolean) value;
                }
                return null;
            case PLUS:
                if (value instanceof Number) {
                    return ((Number) value).doubleValue();
                }
                return null;
            case MINUS:
                if (value instanceof Number) {
                    return -((Number) value).doubleValue();
                }
                return null;
            case IS_NULL:
                return value == null;
            case IS_NOT_NULL:
                return value != null;
            default:
                return null;
        }
    }

    @Override
    public Class<?> getType() {
        switch (operator) {
            case NOT:
            case IS_NULL:
            case IS_NOT_NULL:
                return Boolean.class;
            default:
                return Number.class;
        }
    }

    @Override
    public boolean isConstant() {
        return expression.isConstant();
    }

    @Override
    public List<String> getReferencedColumns() {
        return expression.getReferencedColumns();
    }

    @Override
    public String toSql() {
        if (operator.isPostfix()) {
            return expression.toSql() + " " + operator.getSymbol();
        }
        return operator.getSymbol() + " " + expression.toSql();
    }

    @Override
    public JQuickExpression clone() {
        return new JQuickUnaryExpression(operator, expression.clone());
    }

    public UnaryOperator getOperator() { return operator; }
    public JQuickExpression getExpression() { return expression; }
}
