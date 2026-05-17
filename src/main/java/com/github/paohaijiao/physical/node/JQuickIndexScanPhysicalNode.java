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

import com.github.paohaijiao.datasource.JQuickDataSourceManager;
import com.github.paohaijiao.enums.JQuickBinaryOperator;
import com.github.paohaijiao.context.JQuickExecutionContext;;
import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.expression.domain.JQuickBinaryExpression;
import com.github.paohaijiao.expression.domain.JQuickColumnRefExpression;
import com.github.paohaijiao.expression.domain.JQuickLiteralExpression;
import com.github.paohaijiao.physical.JQuickPhysicalPlanNode;
import com.github.paohaijiao.statement.JQuickColumnMeta;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;

import java.util.Set;

/**
 * 索引扫描物理节点 - 使用索引快速定位数据
 */
public class JQuickIndexScanPhysicalNode implements JQuickPhysicalPlanNode {
    private final String tableName;
    private final String alias;
    private final Set<String> requiredColumns;
    private final JQuickExpression filter;
    private final String indexColumn;

    public JQuickIndexScanPhysicalNode(String tableName, String alias, Set<String> requiredColumns,
                                       JQuickExpression filter, String indexColumn) {
        this.tableName = tableName;
        this.alias = alias;
        this.requiredColumns = requiredColumns;
        this.filter = filter;
        this.indexColumn = indexColumn;
    }

    @Override
    public JQuickDataSet execute(JQuickExecutionContext context) {
        // 从过滤条件中提取索引列的
        Object indexValue = extractIndexValue(filter, indexColumn);
        // 使用索引快速查找
        JQuickDataSet data = JQuickDataSourceManager.getTableByIndex(tableName, indexColumn, indexValue);
        if (data == null) {
            data = JQuickDataSourceManager.getTable(tableName);
            data = data.filter(row -> {
                Object result = filter.evaluate(row);
                return result instanceof Boolean && (Boolean) result;
            });
        }

        if (requiredColumns != null && !requiredColumns.isEmpty()) {
            data = data.select(requiredColumns.toArray(new String[0]));
        }

        if (alias != null && !alias.equals(tableName)) {
            data = renameColumns(data, alias);
        }

        return data;
    }

    private Object extractIndexValue(JQuickExpression predicate, String columnName) {
        if (predicate instanceof JQuickBinaryExpression) {
            JQuickBinaryExpression binary = (JQuickBinaryExpression) predicate;
            if (binary.getOperator() == JQuickBinaryOperator.EQ) {
                // 提取等值条件中的值
                if (binary.getLeft() instanceof JQuickColumnRefExpression &&
                        ((JQuickColumnRefExpression) binary.getLeft()).getColumnName().equals(columnName)) {
                    if (binary.getRight() instanceof JQuickLiteralExpression) {
                        return ((JQuickLiteralExpression) binary.getRight()).getValue();
                    }
                }
                if (binary.getRight() instanceof JQuickColumnRefExpression &&
                        ((JQuickColumnRefExpression) binary.getRight()).getColumnName().equals(columnName)) {
                    if (binary.getLeft() instanceof JQuickLiteralExpression) {
                        return ((JQuickLiteralExpression) binary.getLeft()).getValue();
                    }
                }
            }
        }
        return null;
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
        return "IndexScan";
    }

    @Override
    public long getEstimatedCost() {
        return 100; // 索引扫描成本很低
    }
}
