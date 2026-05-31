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
import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.physical.JQuickPhysicalPlanNode;
import com.github.paohaijiao.physical.JQuickPhysicalPlanVisitor;
import com.github.paohaijiao.physical.domain.JQuickPhysicalColumn;
import com.github.paohaijiao.physical.domain.JQuickPhysicalStats;
import com.github.paohaijiao.physical.domain.JQuickTablePartitionInfo;

import java.util.*;

public class JQuickTableScanPhysicalNode implements JQuickPhysicalPlanNode {

    private final String tableName;

    private final String alias;

    private final Set<String> requiredColumns;

    private final JQuickExpression filterPredicate;

    private final JQuickTablePartitionInfo partitionInfo;


    public JQuickTableScanPhysicalNode(String tableName, String alias, Set<String> requiredColumns, JQuickExpression filterPredicate) {
        this(tableName, alias, requiredColumns, filterPredicate, null);
    }

    public JQuickTableScanPhysicalNode(String tableName, String alias, Set<String> requiredColumns, JQuickExpression filterPredicate, JQuickTablePartitionInfo partitionInfo) {
        this.tableName = tableName;
        this.alias = alias;
        this.requiredColumns = requiredColumns != null ? new HashSet<>(requiredColumns) : null;
        this.filterPredicate = filterPredicate;
        this.partitionInfo = partitionInfo;
    }

    @Override
    public String getNodeType() {
        return "TableScan";
    }

    @Override
    public List<JQuickPhysicalPlanNode> getChildren() {
        return Collections.emptyList();
    }

    @Override
    public List<JQuickPhysicalColumn> getOutputSchema() {
        List<JQuickPhysicalColumn> columns = new ArrayList<>();
        if (requiredColumns != null) {
            for (String col : requiredColumns) {
                String colName = alias != null ? alias + "." + col : col;
                columns.add(new JQuickPhysicalColumn(colName, Object.class, tableName, true));
            }
        }
        return columns;
    }

    @Override
    public JQuickPhysicalPlanNode clone() {
        return new JQuickTableScanPhysicalNode(tableName, alias, requiredColumns, filterPredicate != null ? filterPredicate.clone() : null, partitionInfo);
    }

    @Override
    public void accept(JQuickPhysicalPlanVisitor visitor) {
        visitor.visit(this);
    }

    public String getTableName() { return tableName; }

    public String getAlias() { return alias; }

    public Set<String> getRequiredColumns() { return requiredColumns; }

    public JQuickExpression getFilterPredicate() { return filterPredicate; }

    public JQuickTablePartitionInfo getPartitionInfo() { return partitionInfo; }

    @Override
    public JQuickPhysicalStats getStats() {
        long rowCount = JQuickDataSourceManager.getRowCount(tableName);
        long dataSize = JQuickDataSourceManager.getEstimatedDataSize(tableName);
        if (requiredColumns != null && !requiredColumns.isEmpty()) {
            int totalCols = JQuickDataSourceManager.getColumnNames(tableName).size();
            dataSize = dataSize * requiredColumns.size() / totalCols;
        }
        if (filterPredicate != null) {
            rowCount = rowCount / 2;
            dataSize = dataSize / 2;
        }
        return new JQuickPhysicalStats(rowCount, dataSize, new HashMap<>());
    }

}