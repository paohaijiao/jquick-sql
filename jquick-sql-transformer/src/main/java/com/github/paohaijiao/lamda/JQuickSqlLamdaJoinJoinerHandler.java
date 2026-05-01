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
package com.github.paohaijiao.lamda;

import com.github.paohaijiao.condition.JQuickSqlCondition;
import com.github.paohaijiao.evalue.JQuickSqlConditionEvaluator;
import com.github.paohaijiao.exception.JAssert;
import com.github.paohaijiao.expression.JQuickSqlColumnExpression;
import com.github.paohaijiao.expression.JQuickSqlExpression;
import com.github.paohaijiao.expression.JQuickSqlFunctionCallExpression;
import com.github.paohaijiao.expression.JQuickSqlOrderByExpression;
import com.github.paohaijiao.factory.JQuickSqlDataSetJoinerStrategy;
import com.github.paohaijiao.function.JQuickSqlAggregateFunctionFactory;
import com.github.paohaijiao.handler.JQuickSqlBaseHandler;
import com.github.paohaijiao.join.JQuickSqlJoinCondition;
import com.github.paohaijiao.statement.JQuickColumnMeta;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * packageName com.github.paohaijiao.lamda
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/8/17
 */
public class JQuickSqlLamdaJoinJoinerHandler extends JQuickSqlBaseHandler implements JQuickSqlDataSetJoinerStrategy {
    @Override
    public JQuickDataSet innerJoin(JQuickDataSet left, JQuickDataSet right, JQuickSqlJoinCondition condition) {
        List<JQuickRow> resultRows = new ArrayList<>();
        List<JQuickColumnMeta> resultColumns = mergeColumns(left, right);
        List<JQuickRow> list = left.getRows();
        for (JQuickRow leftRow : list) {
            List<JQuickRow> rightRows = right.getRows();
            for (JQuickRow rightRow : rightRows) {
                if (condition.test(leftRow, rightRow)) {
                    resultRows.add(mergeRows(leftRow, rightRow));
                }
            }
        }
        return new JQuickDataSet(resultColumns, resultRows);
    }

    @Override
    public JQuickDataSet leftJoin(JQuickDataSet left, JQuickDataSet right, JQuickSqlJoinCondition condition) {
        List<JQuickRow> resultRows = new ArrayList<>();
        List<JQuickColumnMeta> resultColumns = mergeColumns(left, right);
        List<JQuickRow> list = left.getRows();
        for (JQuickRow leftRow : list) {
            boolean hasMatch = false;
            List<JQuickRow> rightRows = right.getRows();
            for (JQuickRow rightRow : rightRows) {
                if (condition.test(leftRow, rightRow)) {
                    resultRows.add(mergeRows(leftRow, rightRow));
                    hasMatch = true;
                }
            }
            if (!hasMatch) {
                resultRows.add(mergeRows(leftRow, createNullRow(right)));
            }
        }
        return new JQuickDataSet(resultColumns, resultRows);
    }

    @Override
    public JQuickDataSet rightJoin(JQuickDataSet left, JQuickDataSet right, JQuickSqlJoinCondition condition) {
        List<JQuickRow> resultRows = new ArrayList<>();
        List<JQuickColumnMeta> resultColumns = mergeColumns(left, right);
        int matchCount = 0;
        int totalComparisons = 0;
        for (JQuickRow rightRow : right.getRows()) {
            boolean hasMatch = false;
            for (JQuickRow leftRow : left.getRows()) {
                totalComparisons++;
                if (condition.test(leftRow, rightRow)) {
                    matchCount++;
                    hasMatch = true;
                    resultRows.add(mergeRows(leftRow, rightRow));
                }
            }
            if (!hasMatch) {
                resultRows.add(mergeRows(createNullRow(left), rightRow));
            }
        }
        return new JQuickDataSet(resultColumns, resultRows);
    }


    @Override
    public JQuickDataSet fullOuterJoin(JQuickDataSet left, JQuickDataSet right, JQuickSqlJoinCondition condition) {
        List<JQuickColumnMeta> resultColumns = mergeColumns(left, right);
        Map<JQuickRow, Set<JQuickRow>> matches = new HashMap<>();
        for (JQuickRow leftRow : left.getRows()) {
            for (JQuickRow rightRow : right.getRows()) {
                if (condition.test(leftRow, rightRow)) {
                    matches.computeIfAbsent(leftRow, k -> new HashSet<>()).add(rightRow);
                }
            }
        }
        List<JQuickRow> resultRows = new ArrayList<>();
        for (Map.Entry<JQuickRow, Set<JQuickRow>> entry : matches.entrySet()) {
            for (JQuickRow rightRow : entry.getValue()) {
                resultRows.add(mergeRows(entry.getKey(), rightRow));
            }
        }
        for (JQuickRow leftRow : left.getRows()) {
            if (!matches.containsKey(leftRow)) {
                resultRows.add(mergeRows(leftRow, createNullRow(right)));
            }
        }
        Set<JQuickRow> allMatchedRightRows = matches.values().stream()
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
        for (JQuickRow rightRow : right.getRows()) {
            if (!allMatchedRightRows.contains(rightRow)) {
                resultRows.add(mergeRows(createNullRow(left), rightRow));
            }
        }
        return new JQuickDataSet(resultColumns, resultRows);
    }

    @Override
    public JQuickDataSet crossJoin(JQuickDataSet left, JQuickDataSet right) {
        return innerJoin(left, right, (l, r) -> true);

    }

    @Override
    public JQuickDataSet naturalJoin(JQuickDataSet left, JQuickDataSet right) {
        Set<String> leftColumns = new HashSet<>(left.getColumnNames());
        ;
        Set<String> rightColumns = new HashSet<>(right.getColumnNames());
        Set<String> commonColumns = leftColumns.stream()
                .filter(rightColumns::contains)
                .collect(Collectors.toSet());
        if (commonColumns.isEmpty()) {
            return crossJoin(left, right);
        }
        List<JQuickColumnMeta> resultColumns = new ArrayList<>();
        resultColumns.addAll(left.getColumns());
        resultColumns.addAll(right.getColumns());
        List<JQuickRow> resultRows = left.getRows().stream()
                .flatMap(leftRow -> right.getRows().stream()
                        .filter(rightRow -> isMatch(leftRow, rightRow, commonColumns))
                        .map(rightRow -> mergeRows(leftRow, rightRow)))
                .collect(Collectors.toList());
        return new JQuickDataSet(resultColumns, resultRows);
    }

    @Override
    public JQuickDataSet union(JQuickDataSet ds1, JQuickDataSet ds2) {
        validateUnionCompatible(ds1, ds2);
        List<JQuickRow> combinedRows = new ArrayList<>();
        combinedRows.addAll(ds1.getRows());
        combinedRows.addAll(ds2.getRows());
        return new JQuickDataSet(ds1.getColumns(), combinedRows);
    }

    @Override
    public JQuickDataSet intersect(JQuickDataSet ds1, JQuickDataSet ds2) {
        validateUnionCompatible(ds1, ds2);
        Set<JQuickRow> set1 = new HashSet<>(ds1.getRows());
        Set<JQuickRow> set2 = new HashSet<>(ds2.getRows());
        set1.retainAll(set2);
        return new JQuickDataSet(ds1.getColumns(), new ArrayList<>(set1));
    }

    @Override
    public JQuickDataSet minus(JQuickDataSet ds1, JQuickDataSet ds2) {
        validateUnionCompatible(ds1, ds2);
        Set<JQuickRow> set1 = new HashSet<>(ds1.getRows());
        Set<JQuickRow> set2 = new HashSet<>(ds2.getRows());
        set1.removeAll(set2);
        return new JQuickDataSet(ds1.getColumns(), new ArrayList<>(set1));
    }

    @Override
    public JQuickDataSet selectColumns(JQuickDataSet dataset, List<String> columnNames) {
        List<JQuickColumnMeta> currentColumns = dataset.getColumns();
        List<JQuickRow> currentRows = dataset.getRows();
        List<JQuickColumnMeta> newColumns = currentColumns.stream()
                .filter(col -> columnNames.contains(col.getName()))
                .collect(Collectors.toList());
        List<JQuickRow> newRows = currentRows.stream()
                .map(row -> {
                    JQuickRow Row = (row instanceof JQuickRow)
                            ? new JQuickRow(((JQuickRow) row))
                            : new JQuickRow();
                    columnNames.forEach(col -> Row.put(col, row.get(col)));
                    return Row;
                })
                .collect(Collectors.toList());
        return new JQuickDataSet(newColumns, newRows);
    }

    @Override
    public JQuickDataSet filter(JQuickDataSet dataset, JQuickSqlCondition condition) {
        JQuickSqlConditionEvaluator evaluator = new JQuickSqlConditionEvaluator();
        List<JQuickRow> filteredRows = dataset.getRows().stream()
                .filter(row -> evaluator.evaluateCondition(condition, row))
                .map(row -> {
                    JQuickRow Row = new JQuickRow();
                    Row.putAll(row);
                    return Row;
                })
                .collect(Collectors.toList());
        return new JQuickDataSet(dataset.getColumns(), filteredRows);
    }

    @Override
    public JQuickDataSet transform(JQuickDataSet dataset, Map<String, JQuickSqlFunctionCallExpression> transformations) {
        List<JQuickColumnMeta> newColumns = new ArrayList<>();
        for (JQuickColumnMeta column : dataset.getColumns()) {
            if (transformations.containsKey(column.getName())) {
                newColumns.add(new JQuickColumnMeta(
                        column.getName(),
                        Object.class,
                        column.getSource() + "_transformed"
                ));
            } else {
                newColumns.add(column);
            }
        }
        List<JQuickRow> newRows = dataset.getRows().stream()
                .map(row -> transformRow(row, transformations))
                .collect(Collectors.toList());
        return new JQuickDataSet(newColumns, newRows);
    }

    @Override
    public JQuickDataSet sort(JQuickDataSet dataset, List<JQuickSqlOrderByExpression> orderByExpressions) {
        if (orderByExpressions == null || orderByExpressions.isEmpty()) {
            return dataset;
        }
        List<JQuickRow> sortedRows = new ArrayList<>(dataset.getRows());
        Comparator<Map<String, Object>> comparator = createComparatorChain(orderByExpressions);
        sortedRows.sort(comparator);
        return new JQuickDataSet(dataset.getColumns(), sortedRows);
    }

    @Override
    public JQuickDataSet aggregate(JQuickDataSet dataset, List<String> groupBy, Map<String, JQuickSqlFunctionCallExpression> aggregations) {
        Map<List<Object>, List<Map<String, Object>>> groups = dataset.getRows().stream()
                .collect(Collectors.groupingBy(
                        row -> groupBy.stream()
                                .map(row::get)
                                .collect(Collectors.toList())
                ));
        List<JQuickColumnMeta> newColumns = new ArrayList<>();
        for (String col : groupBy) {
            Class<?> type = dataset.getColumns().stream()
                    .filter(c -> c.getName().equals(col))
                    .findFirst()
                    .<Class<?>>map(JQuickColumnMeta::getType)
                    .orElse(Object.class);
            newColumns.add(new JQuickColumnMeta(col, type, "group_by"));
        }
        for (String aggCol : aggregations.keySet()) {
            newColumns.add(new JQuickColumnMeta(aggCol, Object.class, "aggregate"));
        }
        List<JQuickRow> resultRows = new ArrayList<>();
        for (Map.Entry<List<Object>, List<Map<String, Object>>> entry : groups.entrySet()) {
            JQuickRow resultRow = new JQuickRow();
            for (int i = 0; i < groupBy.size(); i++) {
                resultRow.put(groupBy.get(i), entry.getKey().get(i));
            }
            for (Map.Entry<String, JQuickSqlFunctionCallExpression> aggEntry : aggregations.entrySet()) {
                String colName = aggEntry.getKey();
                JQuickSqlFunctionCallExpression function = aggEntry.getValue();
                JAssert.isTrue(function.getArguments().size() == 1, "the aggregation function must have exactly one argument");
                JQuickSqlExpression jExpression = function.getArguments().get(0);
                List<Object> values = entry.getValue().stream()
                        .map(row -> {
                            if (jExpression instanceof JQuickSqlColumnExpression) {
                                return row.get(((JQuickSqlColumnExpression) jExpression).getColumnName());
                            }
                            throw new UnsupportedOperationException("only column expressions supported");
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

                if (function.isDistinct()) {
                    values = values.stream().distinct().collect(Collectors.toList());
                }
                Function<List<Object>, Object> func = JQuickSqlAggregateFunctionFactory.getFunction(function.getFunctionName());
                JAssert.notNull(func, "function not supported");
                Object aggValue = func.apply(values);
//                Object aggValue = applyAggregateFunction(aggExpr.getFunction(), values);
                resultRow.put(colName, aggValue);
            }
            resultRows.add(resultRow);
        }
        return new JQuickDataSet(newColumns, resultRows);
    }

    @Override
    public JQuickDataSet alias(JQuickDataSet dataset, Map<String, JQuickSqlExpression> aliases) {
        List<JQuickColumnMeta> newColumns = new ArrayList<>();
        for (JQuickColumnMeta column : dataset.getColumns()) {
            if (!aliases.containsValue(new JQuickSqlColumnExpression(column.getName()))) {
                newColumns.add(column);
            }
        }
        for (Map.Entry<String, JQuickSqlExpression> entry : aliases.entrySet()) {
            String alias = entry.getKey();
            JQuickSqlExpression expr = entry.getValue();
            Class<?> type = determineExpressionType(expr);
            newColumns.add(new JQuickColumnMeta(alias, type, "alias"));
        }
        List<JQuickRow> newRows = dataset.getRows().stream()
                .map(row -> createAliasedRow(row, aliases))
                .collect(Collectors.toList());

        return new JQuickDataSet(newColumns, newRows);
    }

    @Override
    public JQuickDataSet limit(JQuickDataSet dataset, Integer limit, Integer offset) {
        int finalLimit = limit != null ? limit : Integer.MAX_VALUE;
        int finalOffset = offset != null ? offset : 0;
        if (finalLimit <= 0 || finalOffset < 0) {
            return new JQuickDataSet(dataset.getColumns(), Collections.emptyList());
        }
        List<JQuickRow> processedRows = dataset.getRows().stream()
                .skip(finalOffset)
                .limit(finalLimit)
                .collect(Collectors.toList());
        return new JQuickDataSet(dataset.getColumns(), processedRows);
    }

}
