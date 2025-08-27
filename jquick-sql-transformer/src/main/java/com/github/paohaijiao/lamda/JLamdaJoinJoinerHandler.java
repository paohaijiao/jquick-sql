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
import com.github.paohaijiao.evalue.JConditionEvaluator;
import com.github.paohaijiao.dataset.JColumnMeta;
import com.github.paohaijiao.dataset.JDataSet;
import com.github.paohaijiao.dataset.JRow;
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
    public JDataSet innerJoin(JDataSet left, JDataSet right, JoinCondition condition) {
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

    @Override
    public JDataSet leftJoin(JDataSet left, JDataSet right, JoinCondition condition) {
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

    @Override
    public JDataSet fullOuterJoin(JDataSet left, JDataSet right, JoinCondition condition) {
        JDataSet leftJoin = leftJoin(left, right, condition);
        JDataSet rightJoin = leftJoin(right, left, (r, l) -> condition.test(l, r));
        return union(leftJoin, rightJoin);
    }

    @Override
    public JDataSet crossJoin(JDataSet left, JDataSet right) {
        return innerJoin(left, right, (l, r) -> true);

    }

    @Override
    public JDataSet naturalJoin(JDataSet left, JDataSet right) {
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

    @Override
    public JDataSet union(JDataSet ds1, JDataSet ds2) {
        validateUnionCompatible(ds1, ds2);
        List<JRow> combinedRows = new ArrayList<>();
        combinedRows.addAll(ds1.getRows());
        combinedRows.addAll(ds2.getRows());
        return new JDataSet(ds1.getColumns(), combinedRows);
    }

    @Override
    public JDataSet intersect(JDataSet ds1, JDataSet ds2) {
        validateUnionCompatible(ds1, ds2);
        Set<JRow> set1 = new HashSet<>(ds1.getRows());
        Set<JRow> set2 = new HashSet<>(ds2.getRows());
        set1.retainAll(set2);
        return new JDataSet(ds1.getColumns(), new ArrayList<>(set1));
    }

    @Override
    public JDataSet minus(JDataSet ds1, JDataSet ds2) {
        validateUnionCompatible(ds1, ds2);
        Set<JRow> set1 = new HashSet<>(ds1.getRows());
        Set<JRow> set2 = new HashSet<>(ds2.getRows());
        set1.removeAll(set2);
        return new JDataSet(ds1.getColumns(), new ArrayList<>(set1));
    }

    @Override
    public JDataSet selectColumns(JDataSet dataset, List<String> columnNames) {
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

    @Override
    public JDataSet filter(JDataSet dataset, JCondition condition) {
        JConditionEvaluator evaluator = new JConditionEvaluator();
        List<JRow> filteredRows = dataset.getRows().stream()
                .filter(row -> evaluator.evaluateCondition( condition, row))
                .map(row -> {
                    JRow jrow = new JRow();
                    jrow.putAll(row);
                    return jrow;
                })
                .collect(Collectors.toList());
        return new JDataSet(dataset.getColumns(), filteredRows);
    }

    @Override
    public JDataSet transform(JDataSet dataset, Map<String, JFunctionCallExpression> transformations) {
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

    @Override
    public JDataSet sort(JDataSet dataset, List<JOrderByExpression> orderByExpressions) {
        if (orderByExpressions == null || orderByExpressions.isEmpty()) {
            return dataset;
        }
        List<JRow> sortedRows = new ArrayList<>(dataset.getRows());
        Comparator<Map<String, Object>> comparator = createComparatorChain(orderByExpressions);
        sortedRows.sort(comparator);
        return new JDataSet(dataset.getColumns(), sortedRows);
    }

    @Override
    public JDataSet aggregate(JDataSet dataset, List<String> groupBy, Map<String, JFunctionCallExpression> aggregations) {
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
        return new JDataSet(newColumns, resultRows);
    }

    @Override
    public JDataSet alias(JDataSet dataset, Map<String, JExpression> aliases) {
        List<JColumnMeta> newColumns = new ArrayList<>();
        for (JColumnMeta column : dataset.getColumns()) {
            if (!aliases.containsValue(new JColumnExpression(column.getName()))) {
                newColumns.add(column);
            }
        }
        for (Map.Entry<String, JExpression> entry : aliases.entrySet()) {
            String alias = entry.getKey();
            JExpression expr = entry.getValue();
            Class<?> type = determineExpressionType(expr);
            newColumns.add(new JColumnMeta(alias, type, "alias"));
        }
        List<JRow> newRows = dataset.getRows().stream()
                .map(row -> createAliasedRow(row, aliases))
                .collect(Collectors.toList());

        return new JDataSet(newColumns, newRows);
    }

    @Override
    public JDataSet limit(JDataSet dataset, Integer limit, Integer offset) {
        int finalLimit = limit != null ? limit : Integer.MAX_VALUE;
        int finalOffset = offset != null ? offset : 0;
        if (finalLimit <= 0 || finalOffset < 0) {
            return new JDataSet(dataset.getColumns(), Collections.emptyList());
        }
        List<JRow> processedRows = dataset.getRows().stream()
                .skip(finalOffset)
                .limit(finalLimit)
                .collect(Collectors.toList());
        return new JDataSet(dataset.getColumns(), processedRows);
    }

}
