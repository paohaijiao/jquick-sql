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
import com.github.paohaijiao.enums.JQuickSqlNullsOrder;
import com.github.paohaijiao.enums.JQuickSqlSortDirection;
import com.github.paohaijiao.expression.*;
import com.github.paohaijiao.factory.JQuickSqlDataSetJoinerStrategy;
import com.github.paohaijiao.statement.JQuickColumnMeta;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;

import java.util.*;

/**
 * packageName com.github.paohaijiao.handler
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/8/17
 */
public abstract class JQuickSqlBaseHandler implements JQuickSqlDataSetJoinerStrategy {

    protected static JQuickRow mergeRows(JQuickRow left, JQuickRow right) {
        JQuickRow merged = new JQuickRow(left);
        merged.putAll(right);
        return merged;
    }

    protected static List<JQuickColumnMeta> mergeColumns(JQuickDataSet right, JQuickDataSet left) {
        List<JQuickColumnMeta> result = new ArrayList<>(right.getColumns());
        result.addAll(left.getColumns());
        return result;
    }

    protected static JQuickRow createNullRow(JQuickDataSet ds) {
        System.out.println(ds);
        List<String> list = ds.getColumnNames();
        JQuickRow nullRow = new JQuickRow();
        List<String> columns = ds.getColumnNames();
        for (int i = 0; i < columns.size(); i++) {
            nullRow.put(columns.get(i), null);
        }
        return nullRow;
    }

    protected static boolean isMatch(JQuickRow leftRow, JQuickRow rightRow, Set<String> commonColumns) {
        return commonColumns.stream()
                .allMatch(col -> Objects.equals(
                        leftRow.get(col),
                        rightRow.get(col)));
    }

    protected static void validateUnionCompatible(JQuickDataSet ds1, JQuickDataSet ds2) {
        if (ds1.getColumns().size() != ds2.getColumns().size()) {
            throw new IllegalArgumentException("Datasets have different number of columns");
        }
    }

    protected static JQuickRow transformRow(JQuickRow row, Map<String, JQuickSqlFunctionCallExpression> transformations) {
        JQuickRow newRow = new JQuickRow();
        for (String column : row.keySet()) {
            if (transformations.containsKey(column)) {
                JQuickSqlFunctionCallExpression function = transformations.get(column);
                String functionName = function.getFunctionName().toUpperCase();
                //List<Object> args = function.getArguments();
                //   int i= evaluateFunction(transformations.get(column), row);
                newRow.put(column, 1);
            } else {
                newRow.put(column, row.get(column));
            }
        }
        return newRow;
    }

    protected static Comparator<Map<String, Object>> createComparatorChain(List<JQuickSqlOrderByExpression> orderByExpressions) {
        Comparator<Map<String, Object>> comparator = null;
        for (JQuickSqlOrderByExpression orderBy : orderByExpressions) {
            Comparator<Map<String, Object>> current = createSingleComparator(orderBy);
            comparator = (comparator == null) ? current : comparator.thenComparing(current);
        }
        return comparator;
    }

    protected static Comparator<Map<String, Object>> createSingleComparator(JQuickSqlOrderByExpression orderBy) {
        // Get the column name from the expression (assuming it's a column expression)
        String columnName = getColumnNameFromExpression(orderBy.getExpression());
        Comparator<Map<String, Object>> comparator = (row1, row2) -> {
            Object val1 = row1.get(columnName);
            Object val2 = row2.get(columnName);
            if (val1 == null && val2 == null) return 0;
            if (val1 == null) return orderBy.getNullsOrder() == JQuickSqlNullsOrder.FIRST ? -1 : 1;
            if (val2 == null) return orderBy.getNullsOrder() == JQuickSqlNullsOrder.FIRST ? 1 : -1;

            if (val1 instanceof Comparable && val2 instanceof Comparable) {
                return ((Comparable<Object>) val1).compareTo(val2);
            }
            return 0; // if not comparable, treat as equal
        };
        if (orderBy.getDirection() == JQuickSqlSortDirection.DESC) {
            comparator = comparator.reversed();
        }

        return comparator;
    }

    protected static String getColumnNameFromExpression(JQuickSqlExpression expression) {
        if (expression instanceof JQuickSqlColumnExpression) {
            return ((JQuickSqlColumnExpression) expression).getColumnName();
        }
        throw new UnsupportedOperationException("Only column expressions are supported for sorting");
    }

    protected static Class<?> determineExpressionType(JQuickSqlExpression expr) {
        if (expr instanceof JQuickSqlLiteralExpression) {
            return ((JQuickSqlLiteralExpression) expr).getValue().getClass();
        } else if (expr instanceof JQuickSqlColumnExpression) {
            return Object.class;
        } else if (expr instanceof JQuickSqlFunctionCallExpression) {
            return Object.class;
        }
        return Object.class;
    }

    protected static JQuickRow createAliasedRow(JQuickRow originalRow, Map<String, JQuickSqlExpression> aliases) {
        JQuickRow newRow = new JQuickRow(originalRow);
        for (Map.Entry<String, JQuickSqlExpression> entry : aliases.entrySet()) {
            String alias = entry.getKey();
            JQuickSqlExpression expr = entry.getValue();
            newRow.put(alias, evaluateExpression(expr, originalRow));
        }
        aliases.values().stream()
                .filter(expr -> expr instanceof JQuickSqlColumnExpression)
                .map(expr -> ((JQuickSqlColumnExpression) expr).getColumnName())
                .forEach(newRow::remove);
        return newRow;
    }

    private static Object evaluateExpression(JQuickSqlExpression expr, Map<String, Object> row) {
        if (expr instanceof JQuickSqlColumnExpression) {
            return row.get(((JQuickSqlColumnExpression) expr).getColumnName());
        } else if (expr instanceof JQuickSqlLiteralExpression) {
            return ((JQuickSqlLiteralExpression) expr).getValue();
        }
        throw new UnsupportedOperationException("Unsupported expression type for aliasing: " + expr.getType());
    }
}
