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
package com.github.paohaijiao.merge;

import com.github.paohaijiao.logic.domain.JQuickSortNode;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;

import java.util.*;

/**
 * 结果合并器 - 合并多个工作节点的结果
 */
public class Merge {

    private final MergeType type;
    private final List<JQuickSortNode.OrderByItem> orderByKeys;

    public enum MergeType {
        CONCAT,      // 直接拼接
        SORT_MERGE,  // 排序归并
        HASH_MERGE,  // 哈希合并（用于聚合）
        REDUCE       // 规约合并
    }

    public Merge(MergeType type) {
        this(type, null);
    }

    public Merge(MergeType type, List<JQuickSortNode.OrderByItem> orderByKeys) {
        this.type = type;
        this.orderByKeys = orderByKeys;
    }

    /**
     * 合并多个结果集
     */
    public JQuickDataSet merge(List<JQuickDataSet> results) {
        if (results.isEmpty()) {
            return new JQuickDataSet(new ArrayList<>(), new ArrayList<>());
        }

        if (results.size() == 1) {
            return results.get(0);
        }

        switch (type) {
            case CONCAT:
                return concatMerge(results);
            case SORT_MERGE:
                return sortMerge(results);
            case HASH_MERGE:
                return hashMerge(results);
            case REDUCE:
                return reduceMerge(results);
            default:
                return concatMerge(results);
        }
    }

    /**
     * 拼接合并
     */
    private JQuickDataSet concatMerge(List<JQuickDataSet> results) {
        List<JQuickRow> allRows = new ArrayList<>();
        JQuickDataSet first = results.get(0);

        for (JQuickDataSet data : results) {
            allRows.addAll(data.getRows());
        }

        return new JQuickDataSet(first.getColumns(), allRows);
    }

    /**
     * 排序归并合并（用于全局排序）
     */
    private JQuickDataSet sortMerge(List<JQuickDataSet> results) {
        if (orderByKeys == null || orderByKeys.isEmpty()) {
            return concatMerge(results);
        }

        // 使用优先级队列进行多路归并
        PriorityQueue<RowWithSource> heap = new PriorityQueue<>((a, b) -> {
            for (JQuickSortNode.OrderByItem key : orderByKeys) {
                Object v1 = a.row.get(key.getColumnName());
                Object v2 = b.row.get(key.getColumnName());

                if (v1 == null && v2 == null) continue;
                if (v1 == null) return 1;
                if (v2 == null) return -1;

                @SuppressWarnings({"rawtypes", "unchecked"})
                int cmp = ((Comparable) v1).compareTo(v2);
                if (cmp != 0) {
                    return key.isAscending() ? cmp : -cmp;
                }
            }
            return 0;
        });

        // 初始化：从每个结果集取第一行
        List<Iterator<JQuickRow>> iterators = new ArrayList<>();
        for (JQuickDataSet data : results) {
            iterators.add(data.getRows().iterator());
        }

        for (int i = 0; i < iterators.size(); i++) {
            if (iterators.get(i).hasNext()) {
                heap.offer(new RowWithSource(iterators.get(i).next(), i));
            }
        }

        // 归并
        List<JQuickRow> merged = new ArrayList<>();
        while (!heap.isEmpty()) {
            RowWithSource entry = heap.poll();
            merged.add(entry.row);

            if (iterators.get(entry.sourceIndex).hasNext()) {
                heap.offer(new RowWithSource(iterators.get(entry.sourceIndex).next(), entry.sourceIndex));
            }
        }

        JQuickDataSet first = results.get(0);
        return new JQuickDataSet(first.getColumns(), merged);
    }

    /**
     * 哈希合并（用于聚合）
     */
    private JQuickDataSet hashMerge(List<JQuickDataSet> results) {
        // 按聚合键分组合并
        Map<Object, List<JQuickRow>> grouped = new LinkedHashMap<>();

        for (JQuickDataSet data : results) {
            for (JQuickRow row : data.getRows()) {
                // 使用第一列作为分组键（简化）
                Object key = row.get(data.getColumnNames().get(0));
                grouped.computeIfAbsent(key, k -> new ArrayList<>()).add(row);
            }
        }

        // 合并每个分组
        List<JQuickRow> mergedRows = new ArrayList<>();
        for (List<JQuickRow> rows : grouped.values()) {
            if (rows.size() == 1) {
                mergedRows.add(rows.get(0));
            } else {
                mergedRows.add(mergeRows(rows));
            }
        }

        JQuickDataSet first = results.get(0);
        return new JQuickDataSet(first.getColumns(), mergedRows);
    }

    /**
     * 规约合并
     */
    private JQuickDataSet reduceMerge(List<JQuickDataSet> results) {
        // 使用规约函数合并
        JQuickDataSet result = results.get(0);
        for (int i = 1; i < results.size(); i++) {
            result = result.concat(results.get(i));
        }
        return result;
    }

    /**
     * 合并同一分组的多行
     */
    private JQuickRow mergeRows(List<JQuickRow> rows) {
        JQuickRow merged = new JQuickRow();

        // 简化实现：取第一行，累加数值列
        if (rows.isEmpty()) return merged;

        JQuickRow first = rows.get(0);
        for (String col : first.keySet()) {
            Object value = first.get(col);

            // 如果是数值，尝试累加
            if (value instanceof Number) {
                double sum = 0.0;
                for (JQuickRow row : rows) {
                    Object val = row.get(col);
                    if (val instanceof Number) {
                        sum += ((Number) val).doubleValue();
                    }
                }
                merged.put(col, sum);
            } else {
                merged.put(col, value);
            }
        }

        return merged;
    }

    private static class RowWithSource {
        final JQuickRow row;
        final int sourceIndex;

        RowWithSource(JQuickRow row, int sourceIndex) {
            this.row = row;
            this.sourceIndex = sourceIndex;
        }
    }
}
