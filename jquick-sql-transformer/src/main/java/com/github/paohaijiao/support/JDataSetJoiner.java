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
package com.github.paohaijiao.support;

import com.github.paohaijiao.dataset.JColumnMeta;
import com.github.paohaijiao.dataset.JDataSet;
import com.github.paohaijiao.func.JoinCondition;

import java.util.*;
import java.util.stream.Collectors;
/**
 * packageName com.github.paohaijiao.support
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/8/13
 */
public class JDataSetJoiner {
    /**
     * INNER JOIN
     * @param left
     * @param right
     * @param condition
     * @return
     */
    public static JDataSet innerJoin(JDataSet left, JDataSet right, JoinCondition condition) {
        List<Map<String, Object>> resultRows = new ArrayList<>();
        List<JColumnMeta> resultColumns = mergeColumns(left, right);
        List<Map<String, Object>> list=left.getRows();
        for (Map<String, Object> leftRow : list) {
            List<Map<String, Object>> rightRows=right.getRows();
            for (Map<String, Object> rightRow : rightRows) {
                if (condition.test(leftRow, rightRow)) {
                    resultRows.add(mergeRows(leftRow, rightRow));
                }
            }
        }
        return new JDataSet(resultColumns, resultRows);
    }

    /**
     * LEFT JOIN
     * @param left
     * @param right
     * @param condition
     * @return
     */
    public static JDataSet leftJoin(JDataSet left, JDataSet right, JoinCondition condition) {
        List<Map<String, Object>> resultRows = new ArrayList<>();
        List<JColumnMeta> resultColumns = mergeColumns(left, right);
        List<Map<String, Object>> list=left.getRows();
        for (Map<String, Object> leftRow : list) {
            boolean hasMatch = false;
            List<Map<String, Object>> rightRows=right.getRows();
            for (Map<String, Object> rightRow : rightRows) {
                if (condition.test(leftRow, rightRow)) {
                    resultRows.add(mergeRows(leftRow, rightRow));
                    hasMatch = true;
                }
            }

            if (!hasMatch) {
                resultRows.add(mergeRows(leftRow, createNullRow(right)));
            }
        }
        return new JDataSet(resultColumns, resultRows);
    }

    /**
     * FULL OUTER JOIN
     * @param left
     * @param right
     * @param condition
     * @return
     */
    public static JDataSet fullOuterJoin(JDataSet left, JDataSet right, JoinCondition condition) {
        JDataSet leftJoin = leftJoin(left, right, condition);
        JDataSet rightJoin = leftJoin(right, left, (r, l) -> condition.test(l, r));
        return union(leftJoin, rightJoin);
    }

    /**
     *  CROSS JOIN
     * @param left
     * @param right
     * @return
     */
    public static JDataSet crossJoin(JDataSet left, JDataSet right) {
        return innerJoin(left, right, (l, r) -> true);
    }

    public static JDataSet naturalJoin(JDataSet left, JDataSet right) {
        Set<String> leftColumns = new HashSet<>(left.getColumnNames());;
        Set<String> rightColumns =new HashSet<>(right.getColumnNames());
        Set<String> commonColumns = leftColumns.stream()
                .filter(rightColumns::contains)
                .collect(Collectors.toSet());
        if (commonColumns.isEmpty()) {
            return crossJoin(left, right);
        }
        List<JColumnMeta> resultColumns = new ArrayList<>();
        resultColumns.addAll(left.getColumns());
        resultColumns.addAll(right.getColumns());
        List<Map<String, Object>> resultRows = left.getRows().stream()
                .flatMap(leftRow -> right.getRows().stream()
                        .filter(rightRow -> isMatch(leftRow, rightRow, commonColumns))
                        .map(rightRow -> mergeRows(leftRow, rightRow)))
                .collect(Collectors.toList());

        return new JDataSet(resultColumns, resultRows);
    }
    private static boolean isMatch(Map<String, Object> leftRow,
                                   Map<String, Object> rightRow,
                                   Set<String> commonColumns) {
        return commonColumns.stream()
                .allMatch(col -> Objects.equals(
                        leftRow.get(col),
                        rightRow.get(col)));
    }
    public static JDataSet union(JDataSet ds1, JDataSet ds2) {
        validateUnionCompatible(ds1, ds2);
        List<Map<String, Object>> combinedRows = new ArrayList<>();
        combinedRows.addAll(ds1.getRows());
        combinedRows.addAll(ds2.getRows());
        return new JDataSet(ds1.getColumns(), combinedRows);
    }

    public static JDataSet intersect(JDataSet ds1, JDataSet ds2) {
        validateUnionCompatible(ds1, ds2);
        Set<Map<String, Object>> set1 = new HashSet<>(ds1.getRows());
        Set<Map<String, Object>> set2 = new HashSet<>(ds2.getRows());
        set1.retainAll(set2);
        return new JDataSet(ds1.getColumns(), new ArrayList<>(set1));
    }

    public static JDataSet minus(JDataSet ds1, JDataSet ds2) {
        validateUnionCompatible(ds1, ds2);
        Set<Map<String, Object>> set1 = new HashSet<>(ds1.getRows());
        Set<Map<String, Object>> set2 = new HashSet<>(ds2.getRows());
        set1.removeAll(set2);
        return new JDataSet(ds1.getColumns(), new ArrayList<>(set1));
    }

    private static List<JColumnMeta> mergeColumns(JDataSet left, JDataSet right) {
        List<JColumnMeta> result = new ArrayList<>(left.getColumns());
        result.addAll(right.getColumns());
        return result;
    }
    private static Map<String, Object> mergeRows(Map<String, Object> left, Map<String, Object> right) {
        Map<String, Object> merged = new HashMap<>(left);
        merged.putAll(right);
        return merged;
    }
    private static Map<String, Object> createNullRow(JDataSet ds) {
        System.out.println(ds);
        List<String> list=ds.getColumnNames();
        Map<String, Object> nullRow = new HashMap<>();
        List<String> columns = ds.getColumnNames();
        for (int i = 0; i < columns.size(); i++) {
            nullRow.put(columns.get(i), null);
        }
        return nullRow;
    }

    private static void validateUnionCompatible(JDataSet ds1, JDataSet ds2) {
        if (ds1.getColumns().size() != ds2.getColumns().size()) {
            throw new IllegalArgumentException("Datasets have different number of columns");
        }
    }

}
