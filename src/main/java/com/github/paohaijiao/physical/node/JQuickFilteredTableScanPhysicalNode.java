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

import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.plan.logical.ExecutionContext;
import com.github.paohaijiao.statement.JQuickColumnMeta;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;

import java.util.Set;

/**
 * 带过滤条件的表扫描 - 在扫描时直接过滤，减少内存使用
 */
public class JQuickFilteredTableScanPhysicalNode implements JQuickPhysicalPlanNode {
    private final String tableName;
    private final String alias;
    private final Set<String> requiredColumns;
    private final JQuickExpression filter;

    public JQuickFilteredTableScanPhysicalNode(String tableName, String alias,
                                               Set<String> requiredColumns, JQuickExpression filter) {
        this.tableName = tableName;
        this.alias = alias;
        this.requiredColumns = requiredColumns;
        this.filter = filter;
    }

    @Override
    public JQuickDataSet execute(ExecutionContext context) {
        JQuickDataSet data = DataSourceManager.getTable(tableName);

        // 先过滤再投影，减少数据传输
        data = data.filter(row -> {
            Object result = filter.evaluate(row);
            return result instanceof Boolean && (Boolean) result;
        });

        if (requiredColumns != null && !requiredColumns.isEmpty()) {
            data = data.select(requiredColumns.toArray(new String[0]));
        }

        if (alias != null && !alias.equals(tableName)) {
            data = renameColumns(data, alias);
        }

        return data;
    }

    private JQuickDataSet renameColumns(JQuickDataSet data, String prefix) {
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
        return "FilteredTableScan";
    }

    @Override
    public long getEstimatedCost() {
        return DataSourceManager.getTableSize(tableName) / 10; // 假设过滤掉90%
    }
}
