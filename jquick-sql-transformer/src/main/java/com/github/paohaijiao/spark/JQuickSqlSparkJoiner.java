package com.github.paohaijiao.spark;

import com.github.paohaijiao.condition.JQuickSqlCondition;
import com.github.paohaijiao.expression.*;
import com.github.paohaijiao.extra.JQuickSqlSparkRender;
import com.github.paohaijiao.factory.JQuickSqlDataSetJoinerStrategy;
import com.github.paohaijiao.join.JQuickSqlJoinCondition;
import com.github.paohaijiao.statement.JQuickDataSet;
import org.apache.spark.sql.*;
import org.apache.spark.sql.expressions.Window;

import java.util.*;
import java.util.stream.Collectors;

import static org.apache.spark.sql.functions.col;

/**
 * 基于 Apache Spark 的实现类
 * 利用 Spark SQL 的分布式计算能力处理数据集操作
 */
public class JQuickSqlSparkJoiner extends JQuickSqlSparkRender implements JQuickSqlDataSetJoinerStrategy {


    public JQuickSqlSparkJoiner(SparkSession spark) {
        this.spark = spark;
    }

    @Override
    public JQuickDataSet innerJoin(JQuickDataSet left, JQuickDataSet right, JQuickSqlJoinCondition condition) {
        Dataset<Row> leftDf = toDataFrame(left, "l");
        Dataset<Row> rightDf = toDataFrame(right, "r");
        Column joinExpr = col("l." + condition.getLeftColumn())
                .equalTo(col("r." + condition.getRightColumn()));
        Dataset<Row> joined = leftDf.join(rightDf, joinExpr, "inner");
        return fromDataFrame(joined);
    }

    @Override
    public JQuickDataSet leftJoin(JQuickDataSet left, JQuickDataSet right, JQuickSqlJoinCondition condition) {
        Dataset<Row> leftDf = toDataFrame(left, "l");
        Dataset<Row> rightDf = toDataFrame(right, "r");
        Column joinExpr = col("l." + condition.getLeftColumn())
                .equalTo(col("r." + condition.getRightColumn()));
        Dataset<Row> joined = leftDf.join(rightDf, joinExpr, "left");
        Dataset<Row> selected = resolveColumnConflict(joined, left, right);
        return fromDataFrame(selected);

    }

    @Override
    public JQuickDataSet rightJoin(JQuickDataSet left, JQuickDataSet right, JQuickSqlJoinCondition condition) {
        Dataset<Row> leftDf = toDataFrame(left, "l");
        Dataset<Row> rightDf = toDataFrame(right, "r");
        Column joinExpr = col("l." + condition.getLeftColumn())
                .equalTo(col("r." + condition.getRightColumn()));
        Dataset<Row> joined = leftDf.join(rightDf, joinExpr, "right");
        Dataset<Row> selected = resolveColumnConflict(joined, left, right);
        return fromDataFrame(selected);
    }

    @Override
    public JQuickDataSet fullOuterJoin(JQuickDataSet left, JQuickDataSet right, JQuickSqlJoinCondition condition) {
        Dataset<Row> leftDf = toDataFrame(left, "l");
        Dataset<Row> rightDf = toDataFrame(right, "r");
        Column joinExpr = col("l." + condition.getLeftColumn())
                .equalTo(col("r." + condition.getRightColumn()));
        Dataset<Row> joined = leftDf.join(rightDf, joinExpr, "outer");
        Dataset<Row> selected = resolveColumnConflict(joined, left, right);
        return fromDataFrame(selected);
    }

    @Override
    public JQuickDataSet crossJoin(JQuickDataSet left, JQuickDataSet right) {
        Dataset<Row> leftDf = toDataFrame(left, "l");
        Dataset<Row> rightDf = toDataFrame(right, "r");
        spark.conf().set("spark.sql.crossJoin.enabled", "true");
        Dataset<Row> joined = leftDf.crossJoin(rightDf);
        Dataset<Row> selected = resolveColumnConflict(joined, left, right);
        return fromDataFrame(selected);
    }

    @Override
    public JQuickDataSet naturalJoin(JQuickDataSet left, JQuickDataSet right) {
        List<String> leftCols = left.getColumnNames();
        List<String> rightCols = right.getColumnNames();
        List<String> commonCols = leftCols.stream()
                .filter(rightCols::contains)
                .collect(Collectors.toList());
        Dataset<Row> leftDf = toDataFrame(left, "l");
        Dataset<Row> rightDf = toDataFrame(right, "r");
        Dataset<Row> joined;
        if (commonCols.isEmpty()) {// 没有公共列 → CROSS JOIN
            spark.conf().set("spark.sql.crossJoin.enabled", "true");
            joined = leftDf.crossJoin(rightDf);
        } else {//构建多条件 join
            Column joinExpr = null;
            for (String colName : commonCols) {
                Column eq = col("l." + colName).equalTo(col("r." + colName));
                joinExpr = (joinExpr == null) ? eq : joinExpr.and(eq);
            }
            //INNER JOIN（NATURAL 默认是 inner）
            joined = leftDf.join(rightDf, joinExpr, "inner");
        }
        //去掉右表重复列（NATURAL JOIN 特性）
        Dataset<Row> selected = selectNaturalColumns(joined, left, right, commonCols);
        return fromDataFrame(selected);
    }

    @Override
    public JQuickDataSet union(JQuickDataSet ds1, JQuickDataSet ds2) {
        Dataset<Row> df1 = toDataFrame(ds1, "t1");
        Dataset<Row> df2 = toDataFrame(ds2, "t2");
        Dataset<Row> alignedDf2 = alignSchema(df1, df2);
        Dataset<Row> result = df1.unionByName(alignedDf2).distinct();
        return fromDataFrame(result);
    }

    @Override
    public JQuickDataSet intersect(JQuickDataSet ds1, JQuickDataSet ds2) {
        Dataset<Row> df1 = toDataFrame(ds1, "t1");
        Dataset<Row> df2 = toDataFrame(ds2, "t2");
        Dataset<Row> alignedDf2 = alignSchema(df1, df2);
        Dataset<Row> result = df1.intersect(alignedDf2);
        return fromDataFrame(result);
    }

    @Override
    public JQuickDataSet minus(JQuickDataSet ds1, JQuickDataSet ds2) {
        Dataset<Row> df1 = toDataFrame(ds1, "t1");
        Dataset<Row> df2 = toDataFrame(ds2, "t2");
        Dataset<Row> alignedDf2 = alignSchema(df1, df2);
        Dataset<Row> result = df1.except(alignedDf2);
        return fromDataFrame(result);
    }

    @Override
    public JQuickDataSet selectColumns(JQuickDataSet dataset, List<String> columnNames) {
        Dataset<Row> df = toDataFrame(dataset, "t");
        //校验列是否存在
        List<String> existingCols = Arrays.asList(df.columns());
        for (String colName : columnNames) {
            if (!existingCols.contains(colName)) {
                throw new IllegalArgumentException("Column not found: " + colName);
            }
        }
        //构建select列
        Column[] cols = columnNames.stream()
                .map(org.apache.spark.sql.functions::col)
                .toArray(Column[]::new);
        Dataset<Row> selected = df.select(cols);
        return fromDataFrame(selected);
    }

    @Override
    public JQuickDataSet filter(JQuickDataSet dataset, JQuickSqlCondition condition) {
        Dataset<Row> df = toDataFrame(dataset, "t");
        Column filterExpr = buildCondition(condition);
        Dataset<Row> filtered = df.filter(filterExpr);
        return fromDataFrame(filtered);
    }

    @Override
    public JQuickDataSet transform(JQuickDataSet dataset, Map<String, JQuickSqlFunctionCallExpression> transformations) {
        Dataset<Row> df = toDataFrame(dataset, "t");
        List<Column> columns = new ArrayList<>();
        //原始列
        for (String colName : df.columns()) {
            columns.add(col(colName));
        }
        //构建表达式列
        for (Map.Entry<String, JQuickSqlFunctionCallExpression> entry : transformations.entrySet()) {
            String alias = entry.getKey();
            JQuickSqlFunctionCallExpression expr = entry.getValue();
            Column colExpr = buildExpression(expr).alias(alias);
            columns.add(colExpr);
        }
        //select
        Dataset<Row> result = df.select(columns.toArray(new Column[0]));
        //转回
        return fromDataFrame(result);
    }

    @Override
    public JQuickDataSet sort(JQuickDataSet dataset, List<JQuickSqlOrderByExpression> orderByExpressions) {
        Dataset<Row> df = toDataFrame(dataset, "t");
        //构建排序列
        Column[] orderCols = orderByExpressions.stream()
                .map(this::buildOrderColumn)
                .toArray(Column[]::new);
        Dataset<Row> sorted = df.orderBy(orderCols);
        return fromDataFrame(sorted);
    }

    @Override
    public JQuickDataSet aggregate(JQuickDataSet dataset, List<String> groupBy, Map<String, JQuickSqlFunctionCallExpression> aggregations) {
        Dataset<Row> df = toDataFrame(dataset, "t");
        Column[] groupCols = groupBy.stream()
                .map(org.apache.spark.sql.functions::col)
                .toArray(Column[]::new);
        RelationalGroupedDataset grouped = df.groupBy(groupCols);
        //构建聚合表达式
        List<Column> aggCols = new ArrayList<>();
        for (Map.Entry<String, JQuickSqlFunctionCallExpression> entry : aggregations.entrySet()) {
            String alias = entry.getKey();
            Column aggExpr = buildAggregationExpression(entry.getValue()).alias(alias);
            aggCols.add(aggExpr);
        }
        //执行聚合
        Dataset<Row> result = grouped.agg(aggCols.get(0),
                aggCols.subList(1, aggCols.size()).toArray(new Column[0]));
        return fromDataFrame(result);

    }

    @Override
    public JQuickDataSet alias(JQuickDataSet dataset, Map<String, JQuickSqlExpression> aliases) {
        Dataset<Row> df = toDataFrame(dataset, "t");
        List<Column> columns = new ArrayList<>();
        //留原列（可选策略）
        for (String colName : df.columns()) {
            columns.add(col(colName));
        }
        //构建 alias 表达式
        for (Map.Entry<String, JQuickSqlExpression> entry : aliases.entrySet()) {
            String aliasName = entry.getKey();
            JQuickSqlExpression expr = entry.getValue();
            Column colExpr = buildGenericExpression(expr).alias(aliasName);
            columns.add(colExpr);
        }
        //select
        Dataset<Row> result = df.select(columns.toArray(new Column[0]));
        return fromDataFrame(result);
    }

    @Override
    public JQuickDataSet limit(JQuickDataSet dataset, Integer limit, Integer offset) {
        Dataset<Row> df = toDataFrame(dataset, "t");
        int off = (offset == null) ? 0 : offset;
        int lim = (limit == null) ? Integer.MAX_VALUE : limit;
        Dataset<Row> result;
        if (off == 0) {
            //纯 limit
            result = df.limit(lim);
        } else {
            //模拟offset
            Dataset<Row> withRowNum = df.withColumn(
                    "__row_num",
                    functions.row_number().over(
                            Window.orderBy(functions.monotonically_increasing_id())
                    )
            );
            result = withRowNum
                    .filter(functions.col("__row_num").gt(off))
                    .limit(lim)
                    .drop("__row_num");
        }
        return fromDataFrame(result);
    }
}