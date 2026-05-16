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
package com.github.paohaijiao.plan.physical.node;

/**
 * packageName com.github.paohaijiao.plan.physical
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/5/16
 */

import com.github.paohaijiao.datasource.JQuickDataSourceManager;
import com.github.paohaijiao.plan.logical.ExecutionContext;
import com.github.paohaijiao.statement.JQuickDataSet;

import java.util.Set;

public class JQuickTableScanPhysicalNode implements JQuickPhysicalPlanNode {
    private final String tableName;
    private final String alias;
    private final Set<String> requiredColumns;

    public JQuickTableScanPhysicalNode(String tableName, String alias, Set<String> requiredColumns) {
        this.tableName = tableName;
        this.alias = alias;
        this.requiredColumns = requiredColumns;
    }

    @Override
    public JQuickDataSet execute(ExecutionContext context) {
        JQuickDataSet data = JQuickDataSourceManager.getTable(tableName);

        if (requiredColumns != null && !requiredColumns.isEmpty()) {
            data = data.select(requiredColumns.toArray(new String[0]));
        }

        if (alias != null && !alias.equals(tableName)) {
            data = renameColumns(data, alias);
        }

        return data;
    }

    private JQuickDataSet renameColumns(JQuickDataSet data, String prefix) {
        // 为列添加别名前缀
        JQuickDataSet.Builder builder = JQuickDataSet.builder();
        for (JQuickColumnMeta col : data.getColumns()) {
            builder.addColumn(prefix + "." + col.getName(), col.getType(), col.getSource());
        }
        for (JQuickRow row : data.getRows()) {
            JQuickRow newRow = new JQuickRow();
            for (JQuickColumnMeta col : data.getColumns()) {
                newRow.put(prefix + "." + col.getName(), row.get(col.getName()));
            }
            builder.addRow(newRow);
        }
        return builder.build();
    }

    @Override
    public String getNodeType() {
        return "TableScan";
    }

    @Override
    public long getEstimatedCost() {
        return JQuickDataSourceManager.getTableSize(tableName);
    }
