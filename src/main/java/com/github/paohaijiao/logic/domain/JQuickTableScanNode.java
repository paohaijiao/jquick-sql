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

/**
 * packageName com.github.paohaijiao.logic.domain
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/5/17
 */
import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.logic.JQuickLogicalPlanNode;
import com.github.paohaijiao.logic.JQuickLogicalPlanVisitor;

import java.util.*;

/**
 * 表扫描节点 - 描述从数据源读取表数据
 */
public class JQuickTableScanNode implements JQuickLogicalPlanNode {

    private final String tableName;

    private final String alias;
    /**
     * 列裁剪（Column Pruning）优化
     */
    private final Set<String> requiredColumns;

    private final JQuickExpression filterPredicate;

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

    public String getTableName() { return tableName; }

    public String getAlias() { return alias; }

    public Set<String> getRequiredColumns() { return requiredColumns; }

    public JQuickExpression getFilterPredicate() { return filterPredicate; }

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