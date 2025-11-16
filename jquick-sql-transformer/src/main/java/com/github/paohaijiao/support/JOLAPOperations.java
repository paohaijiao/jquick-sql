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

import com.github.paohaijiao.dataset.ColumnMeta;
import com.github.paohaijiao.dataset.DataSet;
import com.github.paohaijiao.dataset.Row;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * packageName com.github.paohaijiao.support
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/8/17
 */
public class JOLAPOperations {

    /**
     * 上rollUp
     *
     * @param dataset        dataset
     * @param groupByColumns groupByColumns
     * @param aggregations   aggregations (columnname -> aggregations)
     * @return dataset
     */
    public static DataSet rollUp(DataSet dataset, List<String> groupByColumns, Map<String, Function<List<Object>, Object>> aggregations) {
        Map<List<Object>, List<Row>> grouped = dataset.getRows().stream()
                .collect(Collectors.groupingBy(
                        row -> groupByColumns.stream()
                                .map(row::get)
                                .collect(Collectors.toList())
                ));
        List<ColumnMeta> newColumns = new ArrayList<>();
        for (ColumnMeta column : dataset.getColumns()) {
            if (groupByColumns.contains(column.getName())) {
                newColumns.add(column);
            }
        }
        aggregations.keySet().forEach(col -> {
            newColumns.add(new ColumnMeta(col, Object.class, "aggregated"));
        });
        List<Row> aggregatedRows = new ArrayList<>();
        for (Map.Entry<List<Object>, List<Row>> entry : grouped.entrySet()) {
            Row newRow = new Row();
            for (int i = 0; i < groupByColumns.size(); i++) {
                newRow.put(groupByColumns.get(i), entry.getKey().get(i));
            }
            for (Map.Entry<String, Function<List<Object>, Object>> agg : aggregations.entrySet()) {
                String column = agg.getKey();
                List<Object> values = entry.getValue().stream()
                        .map(row -> row.get(column))
                        .collect(Collectors.toList());
                newRow.put(column, agg.getValue().apply(values));
            }
            aggregatedRows.add(newRow);
        }
        return new DataSet(newColumns, aggregatedRows);
    }

    /**
     * drillDown
     *
     * @param dataset              dataset
     * @param additionalDimensions additionalDimensions
     * @param aggregations         aggregations (column -> aggregations)
     * @return dataset
     */
    public static DataSet drillDown(DataSet dataset, List<String> additionalDimensions, Map<String, Function<List<Object>, Object>> aggregations) {
        List<String> currentGroupBy = Collections.singletonList(dataset.getColumns().get(0).getName());
        List<String> newGroupBy = new ArrayList<>(currentGroupBy);
        newGroupBy.addAll(additionalDimensions);
        return rollUp(dataset, newGroupBy, aggregations);
    }

    /**
     * slice
     *
     * @param dataset   dataset
     * @param dimension dimension
     * @param value     value
     * @return dataset
     */
    public static DataSet slice(DataSet dataset, String dimension, Object value) {
        List<Row> filteredRows = dataset.getRows().stream()
                .filter(row -> Objects.equals(row.get(dimension), value))
                .collect(Collectors.toList());
        return new DataSet(dataset.getColumns(), filteredRows);
    }

    /**
     * dice
     *
     * @param dataset    dataset
     * @param conditions condition (column -> conditionValue)
     * @return dataset
     */
    public static DataSet dice(DataSet dataset, Map<String, Object> conditions) {
        List<Row> filteredRows = dataset.getRows().stream()
                .filter(row -> conditions.entrySet().stream()
                        .allMatch(cond -> Objects.equals(row.get(cond.getKey()), cond.getValue())))
                .collect(Collectors.toList());
        return new DataSet(dataset.getColumns(), filteredRows);
    }

    /**
     * pivot
     *
     * @param dataset     dataset
     * @param pivotColumn pivotColumn
     * @param valueColumn valueColumn
     * @param aggregator  aggregator
     * @return dataset
     */
    public static DataSet pivot(DataSet dataset, String pivotColumn, String valueColumn,
                                Function<List<Object>, Object> aggregator) {
        Set<String> otherColumns = dataset.getColumns().stream()
                .map(ColumnMeta::getName)
                .filter(name -> !name.equals(pivotColumn) && !name.equals(valueColumn))
                .collect(Collectors.toSet());
        Map<List<Object>, Map<Object, List<Object>>> grouped = dataset.getRows().stream()
                .collect(Collectors.groupingBy(
                        row -> otherColumns.stream()
                                .map(row::get)
                                .collect(Collectors.toList()),
                        Collectors.groupingBy(
                                row -> row.get(pivotColumn),
                                Collectors.mapping(row -> row.get(valueColumn), Collectors.toList())
                        )
                ));
        Set<Object> pivotValues = dataset.getRows().stream()
                .map(row -> row.get(pivotColumn))
                .collect(Collectors.toSet());
        List<ColumnMeta> newColumns = new ArrayList<>();
        for (String col : otherColumns) {
            newColumns.add(dataset.getColumns().stream()
                    .filter(c -> c.getName().equals(col))
                    .findFirst()
                    .orElse(new ColumnMeta(col, Object.class, "pivoted")));
        }
        for (Object pivotValue : pivotValues) {
            newColumns.add(new ColumnMeta(pivotValue.toString(), Object.class, "pivoted"));
        }

        List<Row> pivotedRows = new ArrayList<>();
        for (Map.Entry<List<Object>, Map<Object, List<Object>>> entry : grouped.entrySet()) {
            Row newRow = new Row();
            for (int i = 0; i < otherColumns.size(); i++) {
                newRow.put(newColumns.get(i).getName(), entry.getKey().get(i));
            }
            Map<Object, List<Object>> pivotMap = entry.getValue();
            for (Object pivotValue : pivotValues) {
                List<Object> values = pivotMap.getOrDefault(pivotValue, Collections.emptyList());
                Object aggregatedValue = values.isEmpty() ? null :
                        (values.size() == 1 ? values.get(0) : aggregator.apply(values));
                newRow.put(pivotValue.toString(), aggregatedValue);
            }
            pivotedRows.add(newRow);
        }

        return new DataSet(newColumns, pivotedRows);
    }


}
