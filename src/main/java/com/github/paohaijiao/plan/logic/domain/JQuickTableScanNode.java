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

import com.github.paohaijiao.datasource.JQuickDataSourceManager;
import com.github.paohaijiao.executor.JQuickExecutionContext;
import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.plan.logic.JQuickLogicalPlanNode;
import com.github.paohaijiao.plan.logic.JQuickLogicalPlanVisitor;
import com.github.paohaijiao.statement.JQuickColumnMeta;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;

import java.util.*;

/**
 * 表扫描节点 - 从数据源读取表数据
 */
public class JQuickTableScanNode implements JQuickLogicalPlanNode {

    private final String tableName;
    private final String alias;
    private final Set<String> requiredColumns;
    private final JQuickExpression filterPredicate;  // 下推的过滤条件

    public JQuickTableScanNode(String tableName) {
        this(tableName, null, null, null);
    }

    public JQuickTableScanNode(String tableName, String alias) {
        this(tableName, alias, null, null);
    }

    public JQuickTableScanNode(String tableName, String alias, Set<String> requiredColumns) {
        this(tableName, alias, requiredColumns, null);
    }

    public JQuickTableScanNode(String tableName, String alias, Set<String> requiredColumns, JQuickExpression filterPredicate) {
        this.tableName = tableName;
        this.alias = alias;
        this.requiredColumns = requiredColumns != null ? new HashSet<>(requiredColumns) : null;
        this.filterPredicate = filterPredicate;
    }

    @Override
    public JQuickDataSet execute(JQuickExecutionContext context) {
        // 检查是否是CTE引用
        if (context.hasCTE(tableName)) {
            JQuickDataSet cteData = context.getCTE(tableName);
            if (alias != null && !alias.equals(tableName)) {
                cteData = renameColumns(cteData, alias);
            }
            return applyFilterAndProjection(cteData);
        }

        // 从数据源获取表数据
        JQuickDataSet data = JQuickDataSourceManager.getTable(tableName);

        // 应用下推的过滤条件
        data = applyFilterAndProjection(data);

        // 如果有别名，重命名列
        if (alias != null && !alias.equals(tableName)) {
            data = renameColumns(data, alias);
        }

        return data;
    }

    private JQuickDataSet applyFilterAndProjection(JQuickDataSet data) {
        // 应用下推的过滤条件
        if (filterPredicate != null) {
            data = data.filter(row -> {
                Object result = filterPredicate.evaluate(row);
                return result instanceof Boolean && (Boolean) result;
            });
        }

        // 应用列裁剪
        if (requiredColumns != null && !requiredColumns.isEmpty()) {
            data = data.select(requiredColumns.toArray(new String[0]));
        }

        return data;
    }

    private JQuickDataSet renameColumns(JQuickDataSet data, String prefix) {
        JQuickDataSet.Builder builder = JQuickDataSet.builder();

        for (JQuickColumnMeta col : data.getColumns()) {
            String newName = prefix + "." + col.getName();
            builder.addColumn(newName, col.getType(), col.getSource());
        }

        for (JQuickRow row : data.getRows()) {
            JQuickRow newRow = new JQuickRow();
            for (JQuickColumnMeta col : data.getColumns()) {
                String newName = prefix + "." + col.getName();
                newRow.put(newName, row.get(col.getName()));
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
    public List<JQuickLogicalPlanNode> getChildren() {
        return Collections.emptyList();
    }

    @Override
    public void accept(JQuickLogicalPlanVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public List<String> getOutputColumns() {
        if (requiredColumns != null) {
            if (alias != null) {
                List<String> result = new ArrayList<>();
                for (String col : requiredColumns) {
                    result.add(alias + "." + col);
                }
                return result;
            }
            return new ArrayList<>(requiredColumns);
        }
        return Collections.emptyList();
    }

    @Override
    public JQuickLogicalPlanNode clone() {
        return new JQuickTableScanNode(tableName, alias, requiredColumns, filterPredicate);
    }

    public String getTableName() {
        return tableName;
    }

    public String getAlias() {
        return alias;
    }

    public Set<String> getRequiredColumns() {
        return requiredColumns;
    }

    public JQuickExpression getFilterPredicate() {
        return filterPredicate;
    }

    public JQuickTableScanNode withFilterPredicate(JQuickExpression predicate) {
        return new JQuickTableScanNode(tableName, alias, requiredColumns, predicate);
    }

    public JQuickTableScanNode withRequiredColumns(Set<String> columns) {
        return new JQuickTableScanNode(tableName, alias, columns, filterPredicate);
    }

    public JQuickTableScanNode withAlias(String newAlias) {
        return new JQuickTableScanNode(tableName, newAlias, requiredColumns, filterPredicate);
    }
}
