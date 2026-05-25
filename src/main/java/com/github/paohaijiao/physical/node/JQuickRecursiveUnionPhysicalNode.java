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

/**
 * 递归CTE物理执行节点
 * 用于执行 WITH RECURSIVE 定义的递归公共表表达式
 */
public class JQuickRecursiveUnionPhysicalNode implements JQuickPhysicalPlanNode {

    private final String cteName;

    private final List<String> columnNames;

    private final JQuickPhysicalPlanNode initialPlan;

    private final JQuickPhysicalPlanNode recursivePlan;

    private final boolean unionAll;

    private final int maxRecursionDepth;

    public JQuickRecursiveUnionPhysicalNode(String cteName, List<String> columnNames, JQuickPhysicalPlanNode initialPlan, JQuickPhysicalPlanNode recursivePlan, boolean unionAll) {
        this(cteName, columnNames, initialPlan, recursivePlan, unionAll, 1000); // 默认最大递归深度1000
    }

    public JQuickRecursiveUnionPhysicalNode(String cteName, List<String> columnNames, JQuickPhysicalPlanNode initialPlan, JQuickPhysicalPlanNode recursivePlan, boolean unionAll, int maxRecursionDepth) {
        this.cteName = cteName;
        this.columnNames = columnNames != null ? new ArrayList<>(columnNames) : null;
        this.initialPlan = initialPlan;
        this.recursivePlan = recursivePlan;
        this.unionAll = unionAll;
        this.maxRecursionDepth = maxRecursionDepth;
    }

    @Override
    public String getNodeType() {
        return "RecursiveUnion";
    }

    @Override
    public List<JQuickPhysicalPlanNode> getChildren() {
        List<JQuickPhysicalPlanNode> children = new ArrayList<>();
        if (initialPlan != null) {
            children.add(initialPlan);
        }
        if (recursivePlan != null) {
            children.add(recursivePlan);
        }
        return Collections.unmodifiableList(children);
    }

    @Override
    public List<JQuickPhysicalColumn> getOutputSchema() {
        if (columnNames != null && !columnNames.isEmpty()) {
            List<JQuickPhysicalColumn> columns = new ArrayList<>();
            for (String colName : columnNames) {
                columns.add(new JQuickPhysicalColumn(colName, Object.class, cteName, true));
            }
            return columns;
        }
        if (initialPlan != null) {
            return initialPlan.getOutputSchema();
        }
        return Collections.emptyList();
    }

    @Override
    public JQuickPhysicalPlanNode clone() {
        JQuickPhysicalPlanNode clonedInitial = initialPlan != null ? initialPlan.clone() : null;
        JQuickPhysicalPlanNode clonedRecursive = recursivePlan != null ? recursivePlan.clone() : null;
        List<String> clonedColumnNames = null;
        if (columnNames != null) {
            clonedColumnNames = new ArrayList<>(columnNames);
        }
        return new JQuickRecursiveUnionPhysicalNode(cteName, clonedColumnNames, clonedInitial, clonedRecursive, unionAll, maxRecursionDepth);
    }

    @Override
    public void accept(JQuickPhysicalPlanVisitor visitor) {
        visitor.visit(this);
    }

    public String getCteName() {
        return cteName;
    }

    public List<String> getColumnNames() {
        return columnNames != null ? Collections.unmodifiableList(columnNames) : null;
    }

    public JQuickPhysicalPlanNode getInitialPlan() {
        return initialPlan;
    }

    public JQuickPhysicalPlanNode getRecursivePlan() {
        return recursivePlan;
    }

    public boolean isUnionAll() {
        return unionAll;
    }

    public int getMaxRecursionDepth() {
        return maxRecursionDepth;
    }

    @Override
    public JQuickPhysicalStats getStats() {
        JQuickPhysicalStats initialStats = initialPlan != null ? initialPlan.getStats() : null;
        if (initialStats == null) {
            return JQuickPhysicalStats.empty();
        }
        long initialRows = initialStats.getEstimatedRowCount();
        int depth = Math.min(maxRecursionDepth, 100); // 最多估算100层
        long totalRows = initialRows;
        for (int i = 0; i < depth; i++) {
            long newRows = totalRows / 2; // 每次增加 50%
            if (newRows == 0) break;
            totalRows += newRows;
            if (totalRows > 10000000) {
                totalRows = 10000000;
                break;
            }
        }
        if (!unionAll) {
            totalRows = Math.min(totalRows, initialRows * 10);
        }
        long estimatedDataSize = totalRows * 200;
        return new JQuickPhysicalStats(totalRows, estimatedDataSize, new HashMap<>());
    }
}
