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
 * BETWEEN 表达式 - 如 a BETWEEN 1 AND 10
 */
public class JQuickBetweenExpression implements JQuickExpression {

    private final JQuickExpression expression;
    private final JQuickExpression low;
    private final JQuickExpression high;
    private final boolean isNot;

    public JQuickBetweenExpression(JQuickExpression expression, JQuickExpression low, JQuickExpression high, boolean isNot) {
        this.expression = expression;
        this.low = low;
        this.high = high;
        this.isNot = isNot;
    }

    @Override
    public Object evaluate(JQuickRow row) {
        Object value = expression.evaluate(row);
        Object lowVal = low.evaluate(row);
        Object highVal = high.evaluate(row);

        if (value == null || lowVal == null || highVal == null) {
            return null;
        }

        @SuppressWarnings({"rawtypes", "unchecked"})
        boolean result = ((Comparable) value).compareTo(lowVal) >= 0 &&
                ((Comparable) value).compareTo(highVal) <= 0;

        return isNot ? !result : result;
    }

    @Override
    public Class<?> getType() {
        return Boolean.class;
    }

    @Override
    public boolean isConstant() {
        return expression.isConstant() && low.isConstant() && high.isConstant();
    }

    @Override
    public List<String> getReferencedColumns() {
        List<String> columns = expression.getReferencedColumns();
        columns.addAll(low.getReferencedColumns());
        columns.addAll(high.getReferencedColumns());
        return columns;
    }

    @Override
    public String toSql() {
        StringBuilder sb = new StringBuilder();
        sb.append(expression.toSql());
        if (isNot) {
            sb.append(" NOT BETWEEN ");
        } else {
            sb.append(" BETWEEN ");
        }
        sb.append(low.toSql()).append(" AND ").append(high.toSql());
        return sb.toString();
    }

    @Override
    public JQuickExpression clone() {
        return new JQuickBetweenExpression(expression.clone(), low.clone(), high.clone(), isNot);
    }

    public JQuickExpression getExpression() { return expression; }
    public JQuickExpression getLow() { return low; }
    public JQuickExpression getHigh() { return high; }
    public boolean isNot() { return isNot; }
}
