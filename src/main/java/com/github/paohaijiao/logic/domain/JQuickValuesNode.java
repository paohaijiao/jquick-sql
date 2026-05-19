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
package com.github.paohaijiao.logic.domain;

import com.github.paohaijiao.logic.JQuickLogicalPlanNode;
import com.github.paohaijiao.logic.JQuickLogicalPlanVisitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 常量值节点 - 用于 VALUES 子句
 */
public class JQuickValuesNode implements JQuickLogicalPlanNode {

    private final List<List<Object>> rows;

    private final List<String> columnNames;

    private final List<Class<?>> columnTypes;

    public JQuickValuesNode(List<List<Object>> rows, List<String> columnNames, List<Class<?>> columnTypes) {
        this.rows = rows;
        this.columnNames = columnNames;
        this.columnTypes = columnTypes;
    }

    @Override
    public String getNodeType() {
        return "Values";
    }

    @Override
    public List<JQuickLogicalPlanNode> getChildren() {
        return Collections.emptyList();
    }

    @Override
    public void accept(JQuickLogicalPlanVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public List<String> getOutputColumns() {
        return new ArrayList<>(columnNames);
    }

    @Override
    public JQuickLogicalPlanNode clone() {
        return new JQuickValuesNode(rows, columnNames, columnTypes);
    }

    public List<List<Object>> getRows() { return rows; }

    public List<String> getColumnNames() { return columnNames; }

    public List<Class<?>> getColumnTypes() { return columnTypes; }
}
