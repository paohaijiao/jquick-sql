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

import com.github.paohaijiao.plan.logic.JQuickLogicalPlanNode;
import com.github.paohaijiao.plan.logic.domain.JQuickFilterNode;
import com.github.paohaijiao.plan.logic.domain.JQuickJoinNode;
import com.github.paohaijiao.plan.logic.domain.JQuickTableScanNode;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JQuickStatisticsManager {

    private final Map<String, TableStats> tableStats = new ConcurrentHashMap<>();

    public long getTableSize(String tableName) {
        TableStats stats = tableStats.get(tableName);
        return stats != null ? stats.rowCount : 10000; // 默认估算
    }

    public boolean hasIndexOnColumn(String tableName, String columnName) {
        TableStats stats = tableStats.get(tableName);
        if (stats == null) return false;
        return stats.indexes.containsKey(columnName);
    }

    public long estimateRows(JQuickLogicalPlanNode plan) {
        if (plan instanceof JQuickTableScanNode) {
            return getTableSize(((JQuickTableScanNode) plan).getTableName());
        } else if (plan instanceof JQuickFilterNode) {
            return (long) (estimateRows(((JQuickFilterNode) plan).getChild()) * 0.1);
        } else if (plan instanceof JQuickJoinNode) {
            long leftRows = estimateRows(((JQuickJoinNode) plan).getLeft());
            long rightRows = estimateRows(((JQuickJoinNode) plan).getRight());
            return leftRows * rightRows;
        }
        return 1000;
    }

    public void updateTableStats(String tableName, long rowCount, Map<String, ColumnStats> columns) {
        TableStats stats = new TableStats();
        stats.rowCount = rowCount;
        stats.columnStats = columns;
        tableStats.put(tableName, stats);
    }

    public void addIndex(String tableName, String columnName) {
        TableStats stats = tableStats.computeIfAbsent(tableName, k -> new TableStats());
        stats.indexes.put(columnName, true);
    }

    private static class TableStats {
        long rowCount;
        Map<String, ColumnStats> columnStats = new HashMap<>();
        Map<String, Boolean> indexes = new HashMap<>();
    }

    private static class ColumnStats {
        long nullCount;
        long distinctCount;
        Object minValue;
        Object maxValue;
    }
}
