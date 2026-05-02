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

import com.github.paohaijiao.condition.JQuickSqlCondition;
import com.github.paohaijiao.expression.JQuickSqlExpression;
import com.github.paohaijiao.expression.JQuickSqlFunctionCallExpression;
import com.github.paohaijiao.expression.JQuickSqlOrderByExpression;
import com.github.paohaijiao.statement.JQuickColumnMeta;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;
import org.apache.spark.sql.*;
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.util.*;
import java.util.stream.Collectors;

import static org.apache.spark.sql.functions.col;
import static org.apache.spark.sql.functions.lit;

/**
 * packageName com.github.paohaijiao.extra
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/5/2
 */
public abstract class JQuickSqlSparkRender {

    protected SparkSession spark;

    protected Dataset<Row> toDataFrame(JQuickDataSet ds, String alias) {
        List<StructField> fields = new ArrayList<>();
        for (JQuickColumnMeta col : ds.getColumns()) {
            fields.add(DataTypes.createStructField(
                    col.getName(),
                    javaToSparkType(col.getType()),
                    true
            ));
        }
        StructType schema = DataTypes.createStructType(fields);
        List<Row> rows = ds.getRows().stream()
                .map(r -> RowFactory.create(
                        ds.getColumnNames().stream()
                                .map(r::get)
                                .toArray()
                ))
                .collect(Collectors.toList());

        Dataset<Row> df = spark.createDataFrame(rows, schema);
        return df.alias(alias);

    }

    protected DataType javaToSparkType(Class<?> clazz) {
        if (clazz == String.class) return DataTypes.StringType;
        if (clazz == Integer.class) return DataTypes.IntegerType;
        if (clazz == Long.class) return DataTypes.LongType;
        if (clazz == Double.class) return DataTypes.DoubleType;
        if (clazz == Boolean.class) return DataTypes.BooleanType;
        if (clazz == Date.class) return DataTypes.TimestampType;
        return DataTypes.StringType; // fallback
    }

    protected JQuickDataSet fromDataFrame(Dataset<Row> df) {
        List<JQuickColumnMeta> columns = Arrays.stream(df.schema().fields())
                .map(f -> new JQuickColumnMeta(
                        f.name(),
                        sparkTypeToJava(f.dataType()),
                        "spark"
                ))
                .collect(Collectors.toList());
        List<JQuickRow> rows = df.collectAsList().stream().map(row -> {
            JQuickRow r = new JQuickRow();
            for (int i = 0; i < row.size(); i++) {
                r.put(df.columns()[i], row.get(i));
            }
            return r;
        }).collect(Collectors.toList());
        return new JQuickDataSet(columns, rows);
    }

    protected Class<?> sparkTypeToJava(DataType type) {
        if (type.equals(DataTypes.StringType)) return String.class;
        if (type.equals(DataTypes.IntegerType)) return Integer.class;
        if (type.equals(DataTypes.LongType)) return Long.class;
        if (type.equals(DataTypes.DoubleType)) return Double.class;
        if (type.equals(DataTypes.BooleanType)) return Boolean.class;
        if (type.equals(DataTypes.TimestampType)) return Date.class;
        return Object.class;
    }

    protected Dataset<Row> resolveColumnConflict(Dataset<Row> df, JQuickDataSet left, JQuickDataSet right) {
        List<Column> columns = new ArrayList<>();
        for (String colName : left.getColumnNames()) {// 左表字段保持原样
            columns.add(col("l." + colName).alias(colName));
        }
        for (String colName : right.getColumnNames()) { // 右表字段加前缀（避免覆盖）
            if (left.getColumnNames().contains(colName)) {
                columns.add(col("r." + colName).alias("r_" + colName));
            } else {
                columns.add(col("r." + colName));
            }
        }
        return df.select(columns.toArray(new Column[0]));
    }

    protected Dataset<Row> selectNaturalColumns(Dataset<Row> df, JQuickDataSet left, JQuickDataSet right, List<String> commonCols) {
        List<Column> columns = new ArrayList<>();
        for (String colName : commonCols) {//公共列（只保留一份，用 left 的）
            columns.add(col("l." + colName).alias(colName));
        }
        for (String colName : left.getColumnNames()) {//left 独有列
            if (!commonCols.contains(colName)) {
                columns.add(col("l." + colName));
            }
        }
        for (String colName : right.getColumnNames()) {//right 独有列
            if (!commonCols.contains(colName)) {
                if (left.getColumnNames().contains(colName)) { //兜底
                    columns.add(col("r." + colName).alias("r_" + colName));
                } else {
                    columns.add(col("r." + colName));
                }
            }
        }
        return df.select(columns.toArray(new Column[0]));
    }
    protected Dataset<Row> alignSchema(Dataset<Row> base, Dataset<Row> other) {
        List<String> baseCols = Arrays.asList(base.columns());
        List<String> otherCols = Arrays.asList(other.columns());
        List<Column> columns = new ArrayList<>();
        for (String colName : baseCols) {
            if (otherCols.contains(colName)) {
                columns.add(col(colName));
            } else { // 缺失列补 null
                columns.add(lit(null).alias(colName));
            }
        }
        return other.select(columns.toArray(new Column[0]));
    }
    protected Column buildCondition(JQuickSqlCondition cond) {

//        Column column = col(cond.getColumn());
//        Object value = cond.getValue();
//
//        switch (cond.getOperator().toLowerCase()) {
//            case "=":
//                return column.equalTo(lit(value));
//            case "!=":
//                return column.notEqual(lit(value));
//            case ">":
//                return column.gt(lit(value));
//            case "<":
//                return column.lt(lit(value));
//            case ">=":
//                return column.geq(lit(value));
//            case "<=":
//                return column.leq(lit(value));
//            case "like":
//                return column.like(value.toString());
//            case "in":
//                if (value instanceof Collection) {
//                    return column.isin(((Collection<?>) value).toArray());
//                }
//                throw new IllegalArgumentException("IN requires collection");
//            case "is null":
//                return column.isNull();
//            case "is not null":
//                return column.isNotNull();
//            default:
//                throw new UnsupportedOperationException("Unsupported operator: " + cond.getOperator());
//        }
        return null;
    }
    protected Column buildExpression(JQuickSqlFunctionCallExpression expr) {
//        String fn = expr.getFunctionName().toLowerCase();
//        List<Object> args = expr.getArguments();
//
//        List<Column> columns = args.stream()
//                .map(this::toColumn)
//                .collect(Collectors.toList());
//
//        switch (fn) {
//
//            case "add":
//                return columns.get(0).plus(columns.get(1));
//            case "sub":
//                return columns.get(0).minus(columns.get(1));
//            case "mul":
//                return columns.get(0).multiply(columns.get(1));
//            case "div":
//                return columns.get(0).divide(columns.get(1));
//
//            //字符串函数
//            case "upper":
//                return functions.upper(columns.get(0));
//            case "lower":
//                return functions.lower(columns.get(0));
//            case "length":
//                return functions.length(columns.get(0));
//            case "concat":
//                return functions.concat(columns.toArray(new Column[0]));
//
//            //数学函数
//            case "abs":
//                return functions.abs(columns.get(0));
//
//            default:
//                throw new UnsupportedOperationException("Unsupported function: " + fn);
//        }
        return null;
    }
    private Column toColumn(Object arg) {
        if (arg instanceof String) {
            return col((String) arg);
        }

        if (arg instanceof Number || arg instanceof Boolean) {
            return lit(arg);
        }

        if (arg instanceof JQuickSqlFunctionCallExpression) {
            return buildExpression((JQuickSqlFunctionCallExpression) arg);
        }

        return lit(arg);
    }
    protected Column buildOrderColumn(JQuickSqlOrderByExpression expr) {
//        Column colExpr = col(expr.getColumn());
//        if (expr.isAscending()) {
//            colExpr = colExpr.asc();
//        } else {
//            colExpr = colExpr.desc();
//        }
//
//        // NULL 排序（
//        if (expr.isNullsFirst()) {
//            colExpr = expr.isAscending()
//                    ? col(expr.getColumn()).asc_nulls_first()
//                    : col(expr.getColumn()).desc_nulls_first();
//        } else {
//            colExpr = expr.isAscending()
//                    ? col(expr.getColumn()).asc_nulls_last()
//                    : col(expr.getColumn()).desc_nulls_last();
//        }

        return null;
    }
    protected Column buildAggregationExpression(JQuickSqlFunctionCallExpression expr) {
//
//        String fn = expr.getFunctionName().toLowerCase();
//        List<Object> args = expr.getArguments();
//
//        Column colExpr = args.isEmpty()
//                ? null
//                : toColumn(args.get(0)); // 通常聚合只一个参数
//
//        switch (fn) {
//
//            case "count":
//                if (args.isEmpty()) {
//                    return functions.count(lit(1)); // count(*)
//                }
//                return functions.count(colExpr);
//
//            case "count_distinct":
//                return functions.countDistinct(colExpr);
//
//            case "sum":
//                return functions.sum(colExpr);
//
//            case "avg":
//                return functions.avg(colExpr);
//
//            case "max":
//                return functions.max(colExpr);
//
//            case "min":
//                return functions.min(colExpr);
//
//            default:
//                throw new UnsupportedOperationException("Unsupported aggregation: " + fn);
//        }
        return null;
    }
    protected Column buildGenericExpression(JQuickSqlExpression expr) {
//
//        if (expr instanceof ColumnRef) {
//            return col(((ColumnRef) expr).getColumn());
//        }
//
//        if (expr instanceof Literal) {
//            return lit(((Literal) expr).getValue());
//        }
//
//        if (expr instanceof FunctionCall) {
//            return buildFunctionExpression((FunctionCall) expr);
//        }

        throw new UnsupportedOperationException("Unsupported expression: " + expr);
    }
//    class ColumnRef implements JQuickSqlExpression {
//        String column;
//    }
//
//    class Literal implements JQuickSqlExpression {
//        Object value;
//    }
//
//    class FunctionCall implements JQuickSqlExpression {
//        String functionName;
//        List<JQuickSqlExpression> args;
//    }
}
