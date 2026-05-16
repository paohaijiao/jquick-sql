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

import java.util.Collections;
import java.util.List;

/**
 * 常量表达式 - 表示固定的字面量值
 */
public class JQuickLiteralExpression implements JQuickExpression {

    private final Object value;
    private final Class<?> type;

    public JQuickLiteralExpression(Object value) {
        this.value = value;
        this.type = value != null ? value.getClass() : Object.class;
    }

    public JQuickLiteralExpression(Object value, Class<?> type) {
        this.value = value;
        this.type = type;
    }

    @Override
    public Object evaluate(JQuickRow row) {
        return value;
    }

    @Override
    public Class<?> getType() {
        return type;
    }

    @Override
    public boolean isConstant() {
        return true;
    }

    @Override
    public List<String> getReferencedColumns() {
        return Collections.emptyList();
    }

    @Override
    public String toSql() {
        if (value == null) {
            return "NULL";
        }
        if (value instanceof String) {
            return "'" + escapeString((String) value) + "'";
        }
        if (value instanceof Boolean) {
            return (Boolean) value ? "TRUE" : "FALSE";
        }
        return value.toString();
    }

    private String escapeString(String s) {
        return s.replace("'", "''");
    }

    @Override
    public JQuickExpression clone() {
        return new JQuickLiteralExpression(value, type);
    }

    public Object getValue() { return value; }
}
