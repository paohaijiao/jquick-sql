
package com.github.paohaijiao.mapreduce;



import com.github.paohaijiao.condition.JQuickSqlCondition;
import com.github.paohaijiao.expression.JQuickSqlExpression;
import com.github.paohaijiao.expression.JQuickSqlFunctionCallExpression;
import com.github.paohaijiao.expression.JQuickSqlOrderByExpression;
import com.github.paohaijiao.factory.JQuickSqlDataSetJoinerStrategy;
import com.github.paohaijiao.join.JQuickSqlJoinCondition;
import com.github.paohaijiao.statement.JQuickDataSet;

import java.util.List;
import java.util.Map;

/**
 * packageName com.github.paohaijiao.mr
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/8/17
 */
public class JQuickSqlMapReduceJoiner implements JQuickSqlDataSetJoinerStrategy {


    @Override
    public JQuickDataSet innerJoin(JQuickDataSet left, JQuickDataSet right, JQuickSqlJoinCondition condition) {
        return null;
    }

    @Override
    public JQuickDataSet leftJoin(JQuickDataSet left, JQuickDataSet right, JQuickSqlJoinCondition condition) {
        return null;
    }

    @Override
    public JQuickDataSet rightJoin(JQuickDataSet left, JQuickDataSet right, JQuickSqlJoinCondition condition) {
        return null;
    }

    @Override
    public JQuickDataSet fullOuterJoin(JQuickDataSet left, JQuickDataSet right, JQuickSqlJoinCondition condition) {
        return null;
    }

    @Override
    public JQuickDataSet crossJoin(JQuickDataSet left, JQuickDataSet right) {
        return null;
    }

    @Override
    public JQuickDataSet naturalJoin(JQuickDataSet left, JQuickDataSet right) {
        return null;
    }

    @Override
    public JQuickDataSet union(JQuickDataSet ds1, JQuickDataSet ds2) {
        return null;
    }

    @Override
    public JQuickDataSet intersect(JQuickDataSet ds1, JQuickDataSet ds2) {
        return null;
    }

    @Override
    public JQuickDataSet minus(JQuickDataSet ds1, JQuickDataSet ds2) {
        return null;
    }

    @Override
    public JQuickDataSet selectColumns(JQuickDataSet dataset, List<String> columnNames) {
        return null;
    }

    @Override
    public JQuickDataSet filter(JQuickDataSet dataset, JQuickSqlCondition condition) {
        return null;
    }

    @Override
    public JQuickDataSet transform(JQuickDataSet dataset, Map<String, JQuickSqlFunctionCallExpression> transformations) {
        return null;
    }

    @Override
    public JQuickDataSet sort(JQuickDataSet dataset, List<JQuickSqlOrderByExpression> orderByExpressions) {
        return null;
    }

    @Override
    public JQuickDataSet aggregate(JQuickDataSet dataset, List<String> groupBy, Map<String, JQuickSqlFunctionCallExpression> aggregations) {
        return null;
    }

    @Override
    public JQuickDataSet alias(JQuickDataSet dataset, Map<String, JQuickSqlExpression> aliases) {
        return null;
    }

    @Override
    public JQuickDataSet limit(JQuickDataSet dataset, Integer limit, Integer offset) {
        return null;
    }
}
