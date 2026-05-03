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
package com.github.paohaijiao.extra;

import com.github.paohaijiao.expression.JQuickSqlOrderByExpression;
import com.github.paohaijiao.statement.JQuickColumnMeta;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;

import java.util.*;
import java.util.stream.Collectors;

/**
 * packageName com.github.paohaijiao.extra
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/5/3
 */
public abstract class JQuickFlinkRender {
    protected JQuickRow mergeRow(JQuickRow leftRow, JQuickRow rightRow, JQuickDataSet left, JQuickDataSet right) {
        JQuickRow merged = new JQuickRow();
        if (leftRow != null) {
            merged.putAll(leftRow);
        } else {
            for (JQuickColumnMeta col : left.getColumns()) {
                merged.put(col.getName(), null);
            }
        }
        for (JQuickColumnMeta col : right.getColumns()) {
            String name = col.getName();
            if (merged.containsKey(name)) {
                merged.put("right_" + name, rightRow != null ? rightRow.get(name) : null);
            } else {
                merged.put(name, rightRow != null ? rightRow.get(name) : null);
            }
        }
        return merged;
    }
    protected List<JQuickColumnMeta> buildColumns(JQuickDataSet left, JQuickDataSet right) {
        List<JQuickColumnMeta> merged = new ArrayList<>();
        merged.addAll(left.getColumns());
        Set<String> leftNames = left.getColumns()
                .stream()
                .map(JQuickColumnMeta::getName)
                .collect(Collectors.toSet());
        for (JQuickColumnMeta col : right.getColumns()) {
            if (leftNames.contains(col.getName())) {
                merged.add(new JQuickColumnMeta(
                        "right_" + col.getName(),
                        col.getType(),
                        col.getSource()
                ));
            } else {
                merged.add(col);
            }
        }

        return merged;
    }
    protected String buildKey(JQuickRow row, List<String> cols) {
        return cols.stream().map(c -> Objects.toString(row.get(c), "null")).collect(Collectors.joining("_"));
    }
    protected JQuickRow mergeNaturalRow(JQuickRow left, JQuickRow right, List<String> joinCols) {
        JQuickRow merged = new JQuickRow();
        // left 全部放入
        merged.putAll(left);
        // right 只放非 join 列
        for (String col : right.keySet()) {
            if (!joinCols.contains(col)) {
                merged.put(col, right.get(col));
            }
        }
        return merged;
    }
    protected List<JQuickColumnMeta> buildNaturalColumns(JQuickDataSet left, JQuickDataSet right, List<String> joinCols) {
        List<JQuickColumnMeta> merged = new ArrayList<>();
        // left 全部
        merged.addAll(left.getColumns());
        // right 去掉 join 列
        for (JQuickColumnMeta col : right.getColumns()) {
            if (!joinCols.contains(col.getName())) {
                merged.add(col);
            }
        }
        return merged;
    }
    protected void validateSchema(JQuickDataSet ds1, JQuickDataSet ds2) {
        List<String> cols1 = ds1.getColumnNames();
        List<String> cols2 = ds2.getColumnNames();
        if (cols1.size() != cols2.size()) {
            throw new IllegalArgumentException("Column size mismatch");
        }
        for (int i = 0; i < cols1.size(); i++) {
            if (!cols1.get(i).equals(cols2.get(i))) {
                throw new IllegalArgumentException(
                        "Column mismatch at position " + i + ": " + cols1.get(i) + " vs " + cols2.get(i));
            }
        }
    }
    protected String buildRowKey(JQuickRow row, List<String> columns) {
        return columns.stream().map(c -> Objects.toString(row.get(c), "null")).collect(Collectors.joining("|"));
    }
    protected void validateColumns(JQuickDataSet dataset, List<String> columnNames) {
        Set<String> existing = dataset.getColumnNames().stream().collect(Collectors.toSet());
        for (String col : columnNames) {
            if (!existing.contains(col)) {
                throw new IllegalArgumentException("Column not found: " + col);
            }
        }
    }
    protected Comparator<JQuickRow> buildComparator(List<JQuickSqlOrderByExpression> orderBys) {
//        return (r1, r2) -> {
//            for (JQuickSqlOrderByExpression expr : orderBys) {
//                String col = expr.getColumn();
//                boolean asc = expr.isAsc();
//                Comparable v1 = (Comparable) r1.get(col);
//                Comparable v2 = (Comparable) r2.get(col);
//
//                int cmp;
//
//                // null 处理（SQL: nulls last 默认）
//                if (v1 == null && v2 == null) {
//                    cmp = 0;
//                } else if (v1 == null) {
//                    cmp = 1;
//                } else if (v2 == null) {
//                    cmp = -1;
//                } else {
//                    cmp = v1.compareTo(v2);
//                }
//
//                if (cmp != 0) {
//                    return asc ? cmp : -cmp;
//                }
//            }
//
//            return 0;
//        };
        return null;
    }
    protected String buildGroupKey(JQuickRow row, List<String> cols) {
        return cols.stream().map(c -> Objects.toString(row.get(c), "null")).collect(Collectors.joining("|"));
    }
}
