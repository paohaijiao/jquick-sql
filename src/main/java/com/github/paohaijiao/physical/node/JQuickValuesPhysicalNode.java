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
package com.github.paohaijiao.physical.node;


import com.github.paohaijiao.physical.JQuickPhysicalPlanNode;
import com.github.paohaijiao.physical.JQuickPhysicalPlanVisitor;
import com.github.paohaijiao.physical.domain.JQuickPhysicalColumn;
import com.github.paohaijiao.physical.domain.JQuickPhysicalStats;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class JQuickValuesPhysicalNode implements JQuickPhysicalPlanNode {

    private final List<List<Object>> rows;

    private final List<String> columnNames;

    private final List<Class<?>> columnTypes;

    public JQuickValuesPhysicalNode(List<List<Object>> rows, List<String> columnNames, List<Class<?>> columnTypes) {
        this.rows = rows;
        this.columnNames = columnNames;
        this.columnTypes = columnTypes;
    }

    @Override
    public String getNodeType() {
        return "Values";
    }

    @Override
    public List<JQuickPhysicalPlanNode> getChildren() {
        return Collections.emptyList();
    }

    @Override
    public List<JQuickPhysicalColumn> getOutputSchema() {
        List<JQuickPhysicalColumn> columns = new ArrayList<>();
        for (int i = 0; i < columnNames.size(); i++) {
            columns.add(new JQuickPhysicalColumn(columnNames.get(i), columnTypes.get(i), null, false));
        }
        return columns;
    }


    @Override
    public JQuickPhysicalPlanNode clone() {
        List<List<Object>> clonedRows = new ArrayList<>();
        for (List<Object> row : rows) {
            clonedRows.add(new ArrayList<>(row));
        }
        return new JQuickValuesPhysicalNode(clonedRows, new ArrayList<>(columnNames), new ArrayList<>(columnTypes));
    }

    @Override
    public void accept(JQuickPhysicalPlanVisitor visitor) {
        visitor.visit(this);
    }

    public List<List<Object>> getRows() { return rows; }

    public List<String> getColumnNames() { return columnNames; }

    public List<Class<?>> getColumnTypes() { return columnTypes; }

    @Override
    public JQuickPhysicalStats getStats() {
        long rowCount = rows != null ? rows.size() : 0;
        long dataSize = rowCount * 100;
        return new JQuickPhysicalStats(rowCount, dataSize, new HashMap<>());
    }
}
