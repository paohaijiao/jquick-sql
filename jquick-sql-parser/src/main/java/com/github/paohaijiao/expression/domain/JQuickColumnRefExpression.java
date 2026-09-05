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
 * 列引用表达式 - 引用一行中的某一列
 */
public class JQuickColumnRefExpression implements JQuickExpression {

    private final String columnName;

    private final String tableAlias;

    public JQuickColumnRefExpression(String columnName) {
        this(columnName, null);
    }

    public JQuickColumnRefExpression(String columnName, String tableAlias) {
        this.columnName = columnName;
        this.tableAlias = tableAlias;
    }

    @Override
    public Object evaluate(JQuickRow row) {
        return row.get(columnName);
    }

    @Override
    public Class<?> getType() {
        return Object.class;
    }

    @Override
    public boolean isConstant() {
        return false;
    }

    @Override
    public List<String> getReferencedColumns() {
        String fullName = tableAlias != null ? tableAlias + "." + columnName : columnName;
        return Collections.singletonList(fullName);
    }

    @Override
    public String toSql() {
        return tableAlias != null ? tableAlias + "." + columnName : columnName;
    }

    @Override
    public JQuickExpression clone() {
        return new JQuickColumnRefExpression(columnName, tableAlias);
    }

    public String getColumnName() { return columnName; }

    public String getTableAlias() { return tableAlias; }
}
