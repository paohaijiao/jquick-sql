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

import com.github.paohaijiao.condition.JCondition;
import com.github.paohaijiao.condition.JConditionEvaluator;
import com.github.paohaijiao.dataset.JColumnMeta;
import com.github.paohaijiao.dataset.JDataSet;
import com.github.paohaijiao.dataset.JRow;
import com.github.paohaijiao.enums.JNullsOrder;
import com.github.paohaijiao.enums.JSortDirection;
import com.github.paohaijiao.expression.*;
import com.github.paohaijiao.func.JoinCondition;
import com.github.paohaijiao.function.JAggregateFunction;

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
        List<JRow> resultRows = new ArrayList<>();
        List<JColumnMeta> resultColumns = mergeColumns(left, right);
        List<JRow> list=left.getRows();
        for (JRow leftRow : list) {
            List<JRow> rightRows=right.getRows();
            for (JRow rightRow : rightRows) {
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
        List<JRow> resultRows = new ArrayList<>();
        List<JColumnMeta> resultColumns = mergeColumns(left, right);
        List<JRow> list=left.getRows();
        for (JRow leftRow : list) {
            boolean hasMatch = false;
            List<JRow> rightRows=right.getRows();
            for (JRow rightRow : rightRows) {
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
        List<JRow> resultRows = left.getRows().stream()
                .flatMap(leftRow -> right.getRows().stream()
                        .filter(rightRow -> isMatch(leftRow, rightRow, commonColumns))
                        .map(rightRow -> mergeRows(leftRow, rightRow)))
                .collect(Collectors.toList());

        return new JDataSet(resultColumns, resultRows);
    }
    private static boolean isMatch(JRow leftRow,
                                   JRow rightRow,
                                   Set<String> commonColumns) {
        return commonColumns.stream()
                .allMatch(col -> Objects.equals(
                        leftRow.get(col),
                        rightRow.get(col)));
    }
    public static JDataSet union(JDataSet ds1, JDataSet ds2) {
        validateUnionCompatible(ds1, ds2);
        List<JRow> combinedRows = new ArrayList<>();
        combinedRows.addAll(ds1.getRows());
        combinedRows.addAll(ds2.getRows());
        return new JDataSet(ds1.getColumns(), combinedRows);
    }

    public static JDataSet intersect(JDataSet ds1, JDataSet ds2) {
        validateUnionCompatible(ds1, ds2);
        Set<JRow> set1 = new HashSet<>(ds1.getRows());
        Set<JRow> set2 = new HashSet<>(ds2.getRows());
        set1.retainAll(set2);
        return new JDataSet(ds1.getColumns(), new ArrayList<>(set1));
    }

    public static JDataSet minus(JDataSet ds1, JDataSet ds2) {
        validateUnionCompatible(ds1, ds2);
        Set<JRow> set1 = new HashSet<>(ds1.getRows());
        Set<JRow> set2 = new HashSet<>(ds2.getRows());
        set1.removeAll(set2);
        return new JDataSet(ds1.getColumns(), new ArrayList<>(set1));
    }

    private static List<JColumnMeta> mergeColumns(JDataSet left, JDataSet right) {
        List<JColumnMeta> result = new ArrayList<>(left.getColumns());
        result.addAll(right.getColumns());
        return result;
    }
    private static JRow mergeRows(JRow left,JRow right) {
        JRow merged = new JRow(left);
        merged.putAll(right);
        return merged;
    }
    private static JRow createNullRow(JDataSet ds) {
        System.out.println(ds);
        List<String> list=ds.getColumnNames();
        JRow nullRow = new JRow();
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
    public static JDataSet selectColumns(JDataSet dataset, List<String> columnNames) {
        List<JColumnMeta> currentColumns = dataset.getColumns();
        List<JRow> currentRows = dataset.getRows();
        List<JColumnMeta> newColumns = currentColumns.stream()
                .filter(col -> columnNames.contains(col.getName()))
                .collect(Collectors.toList());
        List<JRow> newRows = currentRows.stream()
                .map(row -> {
                    JRow jrow = (row instanceof JRow)
                            ? new JRow(((JRow) row).getTableName())
                            : new JRow();
                    columnNames.forEach(col -> jrow.put(col, row.get(col)));
                    return jrow;
                })
                .collect(Collectors.toList());
        return new JDataSet(newColumns, newRows);
    }
    /**
     * Filters a dataset based on a condition and returns a new filtered dataset.
     * @param dataset The source dataset to filter
     * @param condition The condition to apply for filtering
     * @return New JDataSet containing only rows that match the condition
     */
    public static JDataSet filter(JDataSet dataset, JCondition condition) {
        JConditionEvaluator evaluator = new JConditionEvaluator();
        List<JRow> filteredRows = dataset.getRows().stream()
                .filter(row -> evaluator.evaluate(condition, row))
                .map(row -> {
                    JRow jrow = new JRow();
                    jrow.putAll(row);
                    return jrow;
                })
                .collect(Collectors.toList());
        return new JDataSet(dataset.getColumns(), filteredRows);
    }
    public static JDataSet transform(JDataSet dataset, Map<String, JFunctionCallExpression> transformations) {
        List<JColumnMeta> newColumns = new ArrayList<>();
        for (JColumnMeta column : dataset.getColumns()) {
            if (transformations.containsKey(column.getName())) {
                newColumns.add(new JColumnMeta(
                        column.getName(),
                        Object.class,
                        column.getSource() + "_transformed"
                ));
            } else {
                newColumns.add(column);
            }
        }
        List<JRow> newRows = dataset.getRows().stream()
                .map(row -> transformRow(row, transformations))
                .collect(Collectors.toList());
        return new JDataSet(newColumns, newRows);
    }
    private static JRow transformRow(JRow row, Map<String, JFunctionCallExpression> transformations) {
        JRow newRow = new JRow();
        for (String column : row.keySet()) {
            if (transformations.containsKey(column)) {
                JFunctionCallExpression function=transformations.get(column);
                String functionName = function.getFunctionName().toUpperCase();
                //List<Object> args = function.getArguments();
             //   int i= evaluateFunction(transformations.get(column), row);
                newRow.put(column,1);
            } else {
                newRow.put(column, row.get(column));
            }
        }

        return newRow;
    }
    /**
     * Sorts a dataset based on order by expressions and returns a new sorted dataset
     * @param dataset The source dataset to sort
     * @param orderByExpressions List of order by expressions
     * @return New JDataSet with sorted rows
     */
    public static JDataSet sort(JDataSet dataset, List<JOrderByExpression> orderByExpressions) {
        if (orderByExpressions == null || orderByExpressions.isEmpty()) {
            return dataset; // return original if no sorting specified
        }
        List<JRow> sortedRows = new ArrayList<>(dataset.getRows());
        // Create comparator chain
        Comparator<Map<String, Object>> comparator = createComparatorChain(orderByExpressions);
        sortedRows.sort(comparator);
        return new JDataSet(dataset.getColumns(), sortedRows);
    }
    private static Comparator<Map<String, Object>> createComparatorChain(List<JOrderByExpression> orderByExpressions) {
        Comparator<Map<String, Object>> comparator = null;
        for (JOrderByExpression orderBy : orderByExpressions) {
            Comparator<Map<String, Object>> current = createSingleComparator(orderBy);
            comparator = (comparator == null) ? current : comparator.thenComparing(current);
        }
        return comparator;
    }
    @SuppressWarnings("unchecked")
    private static Comparator<Map<String, Object>> createSingleComparator(JOrderByExpression orderBy) {
        // Get the column name from the expression (assuming it's a column expression)
        String columnName = getColumnNameFromExpression(orderBy.getExpression());
        Comparator<Map<String, Object>> comparator = (row1, row2) -> {
            Object val1 = row1.get(columnName);
            Object val2 = row2.get(columnName);
            if (val1 == null && val2 == null) return 0;
            if (val1 == null) return orderBy.getNullsOrder() == JNullsOrder.FIRST ? -1 : 1;
            if (val2 == null) return orderBy.getNullsOrder() == JNullsOrder.FIRST ? 1 : -1;

            if (val1 instanceof Comparable && val2 instanceof Comparable) {
                return ((Comparable<Object>) val1).compareTo(val2);
            }
            return 0; // if not comparable, treat as equal
        };
        if (orderBy.getDirection() == JSortDirection.DESC) {
            comparator = comparator.reversed();
        }

        return comparator;
    }

    private static String getColumnNameFromExpression(JExpression expression) {
        if (expression instanceof JColumnExpression) {
            return ((JColumnExpression) expression).getColumnName();
        }
        throw new UnsupportedOperationException("Only column expressions are supported for sorting");
    }
    /**
     * Creates a new dataset with column aliases based on expressions
     * @param dataset The source dataset
     * @param aliases Map of alias expressions (key: new column name, value: expression for the column)
     * @return New JDataSet with aliased columns
     */
    public static JDataSet alias(JDataSet dataset, Map<String, JExpression> aliases) {
        // Create new column metadata
        List<JColumnMeta> newColumns = new ArrayList<>();

        // 1. First add all original columns that aren't being aliased
        for (JColumnMeta column : dataset.getColumns()) {
            if (!aliases.containsValue(new JColumnExpression(column.getName()))) {
                newColumns.add(column);
            }
        }

        // 2. Add new aliased columns
        for (Map.Entry<String, JExpression> entry : aliases.entrySet()) {
            String alias = entry.getKey();
            JExpression expr = entry.getValue();

            // Determine the type (simplified as Object.class - should derive from expression in real implementation)
            Class<?> type = determineExpressionType(expr);
            newColumns.add(new JColumnMeta(alias, type, "alias"));
        }
        List<JRow> newRows = dataset.getRows().stream()
                .map(row -> createAliasedRow(row, aliases))
                .collect(Collectors.toList());

        return new JDataSet(newColumns, newRows);
    }

    private static JRow createAliasedRow(JRow originalRow, Map<String, JExpression> aliases) {
        JRow newRow = new JRow(originalRow);
        for (Map.Entry<String, JExpression> entry : aliases.entrySet()) {
            String alias = entry.getKey();
            JExpression expr = entry.getValue();
            newRow.put(alias, evaluateExpression(expr, originalRow));
        }
        aliases.values().stream()
                .filter(expr -> expr instanceof JColumnExpression)
                .map(expr -> ((JColumnExpression) expr).getColumnName())
                .forEach(newRow::remove);
        return newRow;
    }

    private static Class<?> determineExpressionType(JExpression expr) {
        if (expr instanceof JLiteralExpression) {
            return ((JLiteralExpression) expr).getValue().getClass();
        } else if (expr instanceof JColumnExpression) {
            return Object.class;
        } else if (expr instanceof JFunctionCallExpression) {
            return Object.class;
        }
        return Object.class;
    }

    private static Object evaluateExpression(JExpression expr, Map<String, Object> row) {
        if (expr instanceof JColumnExpression) {
            return row.get(((JColumnExpression) expr).getColumnName());
        } else if (expr instanceof JLiteralExpression) {
            return ((JLiteralExpression) expr).getValue();
        }
        throw new UnsupportedOperationException("Unsupported expression type for aliasing: " + expr.getType());
    }
    /**
     * Aggregates a dataset based on group by columns and aggregate expressions
     * @param dataset The source dataset
     * @param groupBy List of column names to group by
     * @param aggregations Map of aggregation expressions (key: result column name, value: aggregate expression)
     * @return New JDataSet with aggregated results
     */
    public static JDataSet aggregate(JDataSet dataset, List<String> groupBy, Map<String, JAggregateExpression> aggregations) {
        Map<List<Object>, List<Map<String, Object>>> groups = dataset.getRows().stream()
                .collect(Collectors.groupingBy(
                        row -> groupBy.stream()
                                .map(row::get)
                                .collect(Collectors.toList())
                ));
        List<JColumnMeta> newColumns = new ArrayList<>();
        for (String col : groupBy) {
            Class<?> type = dataset.getColumns().stream()
                    .filter(c -> c.getName().equals(col))
                    .findFirst()
                    .<Class<?>>map(JColumnMeta::getType)
                    .orElse(Object.class);
            newColumns.add(new JColumnMeta(col, type, "group_by"));
        }
        for (String aggCol : aggregations.keySet()) {
            newColumns.add(new JColumnMeta(aggCol, Object.class, "aggregate"));
        }

        List<JRow> resultRows = new ArrayList<>();
        for (Map.Entry<List<Object>, List<Map<String, Object>>> entry : groups.entrySet()) {
            JRow resultRow = new JRow();
            for (int i = 0; i < groupBy.size(); i++) {
                resultRow.put(groupBy.get(i), entry.getKey().get(i));
            }
            for (Map.Entry<String, JAggregateExpression> aggEntry : aggregations.entrySet()) {
                String colName = aggEntry.getKey();
                JAggregateExpression aggExpr = aggEntry.getValue();
                JAggregateFunction function = aggExpr.getFunction();
                List<Object> values = entry.getValue().stream()
                        .map(row -> {
                            if (function.getArgument() instanceof JColumnExpression) {
                                return row.get(((JColumnExpression) function.getArgument()).getColumnName());
                            }
                            throw new UnsupportedOperationException("Only column expressions supported");
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

                if (function.isDistinct()) {
                    values = values.stream().distinct().collect(Collectors.toList());
                }
                Object aggValue = applyAggregateFunction(aggExpr.getFunction(), values);
                resultRow.put(colName, aggValue);
            }

            resultRows.add(resultRow);
        }

        return new JDataSet(newColumns, resultRows);
    }

    private static Object applyAggregateFunction(JAggregateFunction function, List<Object> values) {
        switch (function.getAggregateType()) {
            case COUNT:
                return (long) values.size();
            case SUM:
                return values.stream()
                        .mapToDouble(v -> ((Number) v).doubleValue())
                        .sum();
            case AVG:
                return values.stream()
                        .mapToDouble(v -> ((Number) v).doubleValue())
                        .average()
                        .orElse(0);
            case MIN:
                return values.stream()
                        .map(v -> (Comparable) v)
                        .min(Comparable::compareTo)
                        .orElse(null);
            case MAX:
                return values.stream()
                        .map(v -> (Comparable) v)
                        .max(Comparable::compareTo)
                        .orElse(null);
            default:
                throw new UnsupportedOperationException("Unsupported aggregate function");
        }
    }

}
