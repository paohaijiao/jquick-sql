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

import com.github.paohaijiao.condition.JCondition;
import com.github.paohaijiao.console.JConsole;
import com.github.paohaijiao.dataset.ColumnMeta;
import com.github.paohaijiao.enums.JLogLevel;
import com.github.paohaijiao.evalue.JConditionEvaluator;
import com.github.paohaijiao.dataset.DataSet;
import com.github.paohaijiao.dataset.Row;
import com.github.paohaijiao.exception.JAssert;
import com.github.paohaijiao.expression.JColumnExpression;
import com.github.paohaijiao.expression.JExpression;
import com.github.paohaijiao.expression.JFunctionCallExpression;
import com.github.paohaijiao.expression.JOrderByExpression;
import com.github.paohaijiao.factory.JDataSetJoinerStrategy;
import com.github.paohaijiao.join.JoinCondition;
import com.github.paohaijiao.function.JAggregateFunctionFactory;
import com.github.paohaijiao.handler.JBaseHandler;


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
public class JLamdaJoinJoinerHandler extends JBaseHandler implements JDataSetJoinerStrategy {
    @Override
    public DataSet innerJoin(DataSet left, DataSet right, JoinCondition condition) {
        List<Row> resultRows = new ArrayList<>();
        List<ColumnMeta> resultColumns = mergeColumns(left, right);
        List<Row> list=left.getRows();
        for (Row leftRow : list) {
            List<Row> rightRows=right.getRows();
            for (Row rightRow : rightRows) {
                if (condition.test(leftRow, rightRow)) {
                    resultRows.add(mergeRows(leftRow, rightRow));
                }
            }
        }
        return new DataSet(resultColumns, resultRows);
    }

    @Override
    public DataSet leftJoin(DataSet left, DataSet right, JoinCondition condition) {
        List<Row> resultRows = new ArrayList<>();
        List<ColumnMeta> resultColumns = mergeColumns(left, right);
        List<Row> list=left.getRows();
        for (Row leftRow : list) {
            boolean hasMatch = false;
            List<Row> rightRows=right.getRows();
            for (Row rightRow : rightRows) {
                if (condition.test(leftRow, rightRow)) {
                    resultRows.add(mergeRows(leftRow, rightRow));
                    hasMatch = true;
                }
            }
            if (!hasMatch) {
                resultRows.add(mergeRows(leftRow, createNullRow(right)));
            }
        }
        return new DataSet(resultColumns, resultRows);
    }

    @Override
    public DataSet rightJoin(DataSet left, DataSet right, JoinCondition condition) {
        List<Row> resultRows = new ArrayList<>();
        List<ColumnMeta> resultColumns = mergeColumns(left, right);
        int matchCount = 0;
        int totalComparisons = 0;
        for (Row rightRow : right.getRows()) {
            boolean hasMatch = false;
            for (Row leftRow : left.getRows()) {
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
        return new DataSet(resultColumns, resultRows);
    }


    @Override
    public DataSet fullOuterJoin(DataSet left, DataSet right, JoinCondition condition) {
        List<ColumnMeta> resultColumns = mergeColumns(left, right);
        Map<Row, Set<Row>> matches = new HashMap<>();
        for (Row leftRow : left.getRows()) {
            for (Row rightRow : right.getRows()) {
                if (condition.test(leftRow, rightRow)) {
                    matches.computeIfAbsent(leftRow, k -> new HashSet<>()).add(rightRow);
                }
            }
        }
        List<Row> resultRows = new ArrayList<>();
        for (Map.Entry<Row, Set<Row>> entry : matches.entrySet()) {
            for (Row rightRow : entry.getValue()) {
                resultRows.add(mergeRows(entry.getKey(), rightRow));
            }
        }
        for (Row leftRow : left.getRows()) {
            if (!matches.containsKey(leftRow)) {
                resultRows.add(mergeRows(leftRow, createNullRow(right)));
            }
        }
        Set<Row> allMatchedRightRows = matches.values().stream()
                .flatMap(Set::stream)
                .collect(Collectors.toSet());
        for (Row rightRow : right.getRows()) {
            if (!allMatchedRightRows.contains(rightRow)) {
                resultRows.add(mergeRows(createNullRow(left), rightRow));
            }
        }
        return new DataSet(resultColumns, resultRows);
    }

    @Override
    public DataSet crossJoin(DataSet left, DataSet right) {
        return innerJoin(left, right, (l, r) -> true);

    }

    @Override
    public DataSet naturalJoin(DataSet left, DataSet right) {
        Set<String> leftColumns = new HashSet<>(left.getColumnNames());;
        Set<String> rightColumns =new HashSet<>(right.getColumnNames());
        Set<String> commonColumns = leftColumns.stream()
                .filter(rightColumns::contains)
                .collect(Collectors.toSet());
        if (commonColumns.isEmpty()) {
            return crossJoin(left, right);
        }
        List<ColumnMeta> resultColumns = new ArrayList<>();
        resultColumns.addAll(left.getColumns());
        resultColumns.addAll(right.getColumns());
        List<Row> resultRows = left.getRows().stream()
                .flatMap(leftRow -> right.getRows().stream()
                        .filter(rightRow -> isMatch(leftRow, rightRow, commonColumns))
                        .map(rightRow -> mergeRows(leftRow, rightRow)))
                .collect(Collectors.toList());
        return new DataSet(resultColumns, resultRows);
    }

    @Override
    public DataSet union(DataSet ds1, DataSet ds2) {
        validateUnionCompatible(ds1, ds2);
        List<Row> combinedRows = new ArrayList<>();
        combinedRows.addAll(ds1.getRows());
        combinedRows.addAll(ds2.getRows());
        return new DataSet(ds1.getColumns(), combinedRows);
    }

    @Override
    public DataSet intersect(DataSet ds1, DataSet ds2) {
        validateUnionCompatible(ds1, ds2);
        Set<Row> set1 = new HashSet<>(ds1.getRows());
        Set<Row> set2 = new HashSet<>(ds2.getRows());
        set1.retainAll(set2);
        return new DataSet(ds1.getColumns(), new ArrayList<>(set1));
    }

    @Override
    public DataSet minus(DataSet ds1, DataSet ds2) {
        validateUnionCompatible(ds1, ds2);
        Set<Row> set1 = new HashSet<>(ds1.getRows());
        Set<Row> set2 = new HashSet<>(ds2.getRows());
        set1.removeAll(set2);
        return new DataSet(ds1.getColumns(), new ArrayList<>(set1));
    }

    @Override
    public DataSet selectColumns(DataSet dataset, List<String> columnNames) {
        List<ColumnMeta> currentColumns = dataset.getColumns();
        List<Row> currentRows = dataset.getRows();
        List<ColumnMeta> newColumns = currentColumns.stream()
                .filter(col -> columnNames.contains(col.getName()))
                .collect(Collectors.toList());
        List<Row> newRows = currentRows.stream()
                .map(row -> {
                    Row Row = (row instanceof Row)
                            ? new Row(((Row) row))
                            : new Row();
                    columnNames.forEach(col -> Row.put(col, row.get(col)));
                    return Row;
                })
                .collect(Collectors.toList());
        return new DataSet(newColumns, newRows);
    }

    @Override
    public DataSet filter(DataSet dataset, JCondition condition) {
        JConditionEvaluator evaluator = new JConditionEvaluator();
        List<Row> filteredRows = dataset.getRows().stream()
                .filter(row -> evaluator.evaluateCondition( condition, row))
                .map(row -> {
                    Row Row = new Row();
                    Row.putAll(row);
                    return Row;
                })
                .collect(Collectors.toList());
        return new DataSet(dataset.getColumns(), filteredRows);
    }

    @Override
    public DataSet transform(DataSet dataset, Map<String, JFunctionCallExpression> transformations) {
        List<ColumnMeta> newColumns = new ArrayList<>();
        for (ColumnMeta column : dataset.getColumns()) {
            if (transformations.containsKey(column.getName())) {
                newColumns.add(new ColumnMeta(
                        column.getName(),
                        Object.class,
                        column.getSource() + "_transformed"
                ));
            } else {
                newColumns.add(column);
            }
        }
        List<Row> newRows = dataset.getRows().stream()
                .map(row -> transformRow(row, transformations))
                .collect(Collectors.toList());
        return new DataSet(newColumns, newRows);
    }

    @Override
    public DataSet sort(DataSet dataset, List<JOrderByExpression> orderByExpressions) {
        if (orderByExpressions == null || orderByExpressions.isEmpty()) {
            return dataset;
        }
        List<Row> sortedRows = new ArrayList<>(dataset.getRows());
        Comparator<Map<String, Object>> comparator = createComparatorChain(orderByExpressions);
        sortedRows.sort(comparator);
        return new DataSet(dataset.getColumns(), sortedRows);
    }

    @Override
    public DataSet aggregate(DataSet dataset, List<String> groupBy, Map<String, JFunctionCallExpression> aggregations) {
        Map<List<Object>, List<Map<String, Object>>> groups = dataset.getRows().stream()
                .collect(Collectors.groupingBy(
                        row -> groupBy.stream()
                                .map(row::get)
                                .collect(Collectors.toList())
                ));
        List<ColumnMeta> newColumns = new ArrayList<>();
        for (String col : groupBy) {
            Class<?> type = dataset.getColumns().stream()
                    .filter(c -> c.getName().equals(col))
                    .findFirst()
                    .<Class<?>>map(ColumnMeta::getType)
                    .orElse(Object.class);
            newColumns.add(new ColumnMeta(col, type, "group_by"));
        }
        for (String aggCol : aggregations.keySet()) {
            newColumns.add(new ColumnMeta(aggCol, Object.class, "aggregate"));
        }
        List<Row> resultRows = new ArrayList<>();
        for (Map.Entry<List<Object>, List<Map<String, Object>>> entry : groups.entrySet()) {
            Row resultRow = new Row();
            for (int i = 0; i < groupBy.size(); i++) {
                resultRow.put(groupBy.get(i), entry.getKey().get(i));
            }
            for (Map.Entry<String, JFunctionCallExpression> aggEntry : aggregations.entrySet()) {
                String colName = aggEntry.getKey();
                JFunctionCallExpression function = aggEntry.getValue();
                JAssert.isTrue(function.getArguments().size()==1,"the aggregation function must have exactly one argument");
                JExpression jExpression=function.getArguments().get(0);
                List<Object> values = entry.getValue().stream()
                        .map(row -> {
                            if (jExpression instanceof JColumnExpression) {
                                return row.get(((JColumnExpression) jExpression).getColumnName());
                            }
                            throw new UnsupportedOperationException("only column expressions supported");
                        })
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

                if (function.isDistinct()) {
                    values = values.stream().distinct().collect(Collectors.toList());
                }
                Function<List<Object>, Object>  func=JAggregateFunctionFactory.getFunction(function.getFunctionName());
                JAssert.notNull(func, "function not supported");
                Object aggValue=func.apply(values);
//                Object aggValue = applyAggregateFunction(aggExpr.getFunction(), values);
                resultRow.put(colName, aggValue);
            }
            resultRows.add(resultRow);
        }
        return new DataSet(newColumns, resultRows);
    }

    @Override
    public DataSet alias(DataSet dataset, Map<String, JExpression> aliases) {
        List<ColumnMeta> newColumns = new ArrayList<>();
        for (ColumnMeta column : dataset.getColumns()) {
            if (!aliases.containsValue(new JColumnExpression(column.getName()))) {
                newColumns.add(column);
            }
        }
        for (Map.Entry<String, JExpression> entry : aliases.entrySet()) {
            String alias = entry.getKey();
            JExpression expr = entry.getValue();
            Class<?> type = determineExpressionType(expr);
            newColumns.add(new ColumnMeta(alias, type, "alias"));
        }
        List<Row> newRows = dataset.getRows().stream()
                .map(row -> createAliasedRow(row, aliases))
                .collect(Collectors.toList());

        return new DataSet(newColumns, newRows);
    }

    @Override
    public DataSet limit(DataSet dataset, Integer limit, Integer offset) {
        int finalLimit = limit != null ? limit : Integer.MAX_VALUE;
        int finalOffset = offset != null ? offset : 0;
        if (finalLimit <= 0 || finalOffset < 0) {
            return new DataSet(dataset.getColumns(), Collections.emptyList());
        }
        List<Row> processedRows = dataset.getRows().stream()
                .skip(finalOffset)
                .limit(finalLimit)
                .collect(Collectors.toList());
        return new DataSet(dataset.getColumns(), processedRows);
    }

}
