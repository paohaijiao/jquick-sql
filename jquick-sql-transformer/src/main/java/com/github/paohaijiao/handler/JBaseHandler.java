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
package com.github.paohaijiao.handler;

import com.github.paohaijiao.dataset.JColumnMeta;
import com.github.paohaijiao.dataset.JDataSet;
import com.github.paohaijiao.dataset.JRow;
import com.github.paohaijiao.enums.JNullsOrder;
import com.github.paohaijiao.enums.JSortDirection;
import com.github.paohaijiao.expression.*;
import com.github.paohaijiao.factory.JDataSetJoinerStrategy;

import java.util.*;

/**
 * packageName com.github.paohaijiao.handler
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/8/17
 */
public abstract class JBaseHandler implements JDataSetJoinerStrategy {

    protected static JRow mergeRows(JRow left, JRow right) {
        JRow merged = new JRow(left);
        merged.putAll(right);
        return merged;
    }
    protected static List<JColumnMeta> mergeColumns(JDataSet left, JDataSet right) {
        List<JColumnMeta> result = new ArrayList<>(left.getColumns());
        result.addAll(right.getColumns());
        return result;
    }
    protected static JRow createNullRow(JDataSet ds) {
        System.out.println(ds);
        List<String> list=ds.getColumnNames();
        JRow nullRow = new JRow();
        List<String> columns = ds.getColumnNames();
        for (int i = 0; i < columns.size(); i++) {
            nullRow.put(columns.get(i), null);
        }
        return nullRow;
    }
    protected static boolean isMatch(JRow leftRow,
                                   JRow rightRow,
                                   Set<String> commonColumns) {
        return commonColumns.stream()
                .allMatch(col -> Objects.equals(
                        leftRow.get(col),
                        rightRow.get(col)));
    }
    protected static void validateUnionCompatible(JDataSet ds1, JDataSet ds2) {
        if (ds1.getColumns().size() != ds2.getColumns().size()) {
            throw new IllegalArgumentException("Datasets have different number of columns");
        }
    }
    protected static JRow transformRow(JRow row, Map<String, JFunctionCallExpression> transformations) {
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
    protected static Comparator<Map<String, Object>> createComparatorChain(List<JOrderByExpression> orderByExpressions) {
        Comparator<Map<String, Object>> comparator = null;
        for (JOrderByExpression orderBy : orderByExpressions) {
            Comparator<Map<String, Object>> current = createSingleComparator(orderBy);
            comparator = (comparator == null) ? current : comparator.thenComparing(current);
        }
        return comparator;
    }
    protected static Comparator<Map<String, Object>> createSingleComparator(JOrderByExpression orderBy) {
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
    protected static String getColumnNameFromExpression(JExpression expression) {
        if (expression instanceof JColumnExpression) {
            return ((JColumnExpression) expression).getColumnName();
        }
        throw new UnsupportedOperationException("Only column expressions are supported for sorting");
    }
    protected static Class<?> determineExpressionType(JExpression expr) {
        if (expr instanceof JLiteralExpression) {
            return ((JLiteralExpression) expr).getValue().getClass();
        } else if (expr instanceof JColumnExpression) {
            return Object.class;
        } else if (expr instanceof JFunctionCallExpression) {
            return Object.class;
        }
        return Object.class;
    }
    protected static JRow createAliasedRow(JRow originalRow, Map<String, JExpression> aliases) {
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

    private static Object evaluateExpression(JExpression expr, Map<String, Object> row) {
        if (expr instanceof JColumnExpression) {
            return row.get(((JColumnExpression) expr).getColumnName());
        } else if (expr instanceof JLiteralExpression) {
            return ((JLiteralExpression) expr).getValue();
        }
        throw new UnsupportedOperationException("Unsupported expression type for aliasing: " + expr.getType());
    }
}
