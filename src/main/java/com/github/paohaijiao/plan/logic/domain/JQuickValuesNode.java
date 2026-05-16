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
package com.github.paohaijiao.plan.logic.domain;

import com.github.paohaijiao.executor.JQuickExecutionContext;
import com.github.paohaijiao.plan.logic.JQuickLogicalPlanNode;
import com.github.paohaijiao.plan.logic.JQuickLogicalPlanVisitor;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 常量值节点 - 用于 VALUES 子句或内联数据
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
    public JQuickDataSet execute(JQuickExecutionContext context) {
        JQuickDataSet.Builder builder = JQuickDataSet.builder();

        // 添加列元数据
        for (int i = 0; i < columnNames.size(); i++) {
            builder.addColumn(columnNames.get(i), columnTypes.get(i), "values");
        }

        // 添加行数据
        for (List<Object> rowValues : rows) {
            JQuickRow row = new JQuickRow();
            for (int i = 0; i < columnNames.size() && i < rowValues.size(); i++) {
                row.put(columnNames.get(i), rowValues.get(i));
            }
            builder.addRow(row);
        }

        return builder.build();
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
}
