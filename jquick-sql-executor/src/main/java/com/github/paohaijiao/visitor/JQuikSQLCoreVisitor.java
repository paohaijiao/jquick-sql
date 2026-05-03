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
package com.github.paohaijiao.visitor;

import com.github.paohaijiao.condition.JQuickSqlComparisonCondition;
import com.github.paohaijiao.condition.JQuickSqlCondition;
import com.github.paohaijiao.console.JConsole;
import com.github.paohaijiao.enums.JQuickSqlComparisonOperator;
import com.github.paohaijiao.exception.JAssert;
import com.github.paohaijiao.expression.JQuickSqlBinaryExpression;
import com.github.paohaijiao.expression.JQuickSqlExpression;
import com.github.paohaijiao.param.JContext;
import com.github.paohaijiao.parser.JQuickSQLBaseVisitor;
import com.github.paohaijiao.parser.JQuickSQLLexer;
import com.github.paohaijiao.parser.JQuickSQLParser;
import com.github.paohaijiao.provider.JQuickSqlAbilityProvider;
import com.github.paohaijiao.statement.JQuickColumnMeta;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;
import com.github.paohaijiao.support.JQuickSqlDataSetHolder;
import org.antlr.v4.runtime.CommonTokenStream;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * packageName com.github.paohaijiao.visitor
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/8/11
 */
public class JQuikSQLCoreVisitor extends JQuickSQLBaseVisitor {

    protected final Map<String, JQuickDataSet> tableRegistry = new HashMap<>();

    protected JContext context;

    protected JQuickSQLLexer lexer;

    protected CommonTokenStream tokenStream;

    protected JQuickSQLParser parser;

    protected JQuickSqlAbilityProvider provider;

    protected JQuickSqlDataSetHolder dataSetHolder = new JQuickSqlDataSetHolder();

    public static String trim(String str) {
        if (null == str || "".equals(str)) {
            return str;
        }
        String newStr = str.replaceAll("\"", "");
        newStr = str.replaceAll("'", "");
        return newStr;
    }

    public void registerDataSet(String tableName, JQuickDataSet dataSet) {
        tableRegistry.put(tableName, dataSet);
    }

    protected JQuickDataSet aliasColumns(JQuickDataSet dataset, String alias) {
        List<JQuickColumnMeta> newColumns = new ArrayList<>();
        List<JQuickRow> newRows = new ArrayList<>();
        for (JQuickColumnMeta column : dataset.getColumns()) {
            newColumns.add(new JQuickColumnMeta(
                    alias + "." + column.getName(),
                    column.getType(),
                    column.getSource()
            ));
        }
        for (JQuickRow row : dataset.getRows()) {
            JQuickRow newRow = new JQuickRow();
            for (String key : row.keySet()) {
                newRow.put(alias + "." + key, row.get(key));
            }
            newRows.add(newRow);
        }
        return new JQuickDataSet(newColumns, newRows);
    }

    protected Number getNumber(String number) {
        NumberFormat format = NumberFormat.getInstance();
        Number num = null;
        try {
            num = format.parse(number);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return num;
    }

    protected Number convertToNumber(Object value) {
        if (value instanceof Number) {
            return (Number) value;
        } else if (value instanceof String) {
            try {
                return getNumber((String) value);
            } catch (NumberFormatException e) {
                throw new RuntimeException("Cannot convert to number: " + value);
            }
        }
        throw new RuntimeException("Unsupported numeric type: " + value.getClass());
    }

    protected boolean convertToBoolean(Object value) {
        if (value instanceof Boolean) {
            return (Boolean) value;
        } else {
            JAssert.throwNewException("Cannot convert to boolean: " + value);
            return false;
        }
    }

    protected int compareValues(Object a, Object b) {
        if (a instanceof Number && b instanceof Number) {
            double d1 = ((Number) a).doubleValue();
            double d2 = ((Number) b).doubleValue();
            return Double.compare(d1, d2);
        } else if (a instanceof Comparable && b instanceof Comparable) {
            return ((Comparable) a).compareTo(b);
        }
        return a.toString().compareTo(b.toString());
    }

    protected JQuickSqlCondition convertExpressionToCondition(JQuickSqlExpression expression) {
        JAssert.isTrue(expression instanceof JQuickSqlBinaryExpression, "the expression is not a JBinaryExpression");
        JQuickSqlBinaryExpression compExpr = (JQuickSqlBinaryExpression) expression;
        JQuickSqlComparisonOperator comparisonOperator = JQuickSqlComparisonOperator.symbolOf(compExpr.getOperator().getSymbol());
        return new JQuickSqlComparisonCondition(compExpr.getLeft(), comparisonOperator, compExpr.getRight());
    }
}