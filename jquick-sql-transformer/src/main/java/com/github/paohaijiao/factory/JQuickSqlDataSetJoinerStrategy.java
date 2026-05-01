package com.github.paohaijiao.factory;

import com.github.paohaijiao.condition.JQuickSqlCondition;
import com.github.paohaijiao.expression.JQuickSqlExpression;
import com.github.paohaijiao.expression.JQuickSqlFunctionCallExpression;
import com.github.paohaijiao.expression.JQuickSqlOrderByExpression;
import com.github.paohaijiao.join.JQuickSqlJoinCondition;
import com.github.paohaijiao.statement.JQuickDataSet;

import java.util.List;
import java.util.Map;

public interface JQuickSqlDataSetJoinerStrategy {

    JQuickDataSet innerJoin(JQuickDataSet left, JQuickDataSet right, JQuickSqlJoinCondition condition);

    JQuickDataSet leftJoin(JQuickDataSet left, JQuickDataSet right, JQuickSqlJoinCondition condition);

    JQuickDataSet rightJoin(JQuickDataSet left, JQuickDataSet right, JQuickSqlJoinCondition condition);

    JQuickDataSet fullOuterJoin(JQuickDataSet left, JQuickDataSet right, JQuickSqlJoinCondition condition);

    JQuickDataSet crossJoin(JQuickDataSet left, JQuickDataSet right);

    JQuickDataSet naturalJoin(JQuickDataSet left, JQuickDataSet right);

    public JQuickDataSet union(JQuickDataSet ds1, JQuickDataSet ds2);

    public JQuickDataSet intersect(JQuickDataSet ds1, JQuickDataSet ds2);

    public JQuickDataSet minus(JQuickDataSet ds1, JQuickDataSet ds2);

    public JQuickDataSet selectColumns(JQuickDataSet dataset, List<String> columnNames);

    public JQuickDataSet filter(JQuickDataSet dataset, JQuickSqlCondition condition);

    public JQuickDataSet transform(JQuickDataSet dataset, Map<String, JQuickSqlFunctionCallExpression> transformations);

    public JQuickDataSet sort(JQuickDataSet dataset, List<JQuickSqlOrderByExpression> orderByExpressions);

    public JQuickDataSet aggregate(JQuickDataSet dataset, List<String> groupBy, Map<String, JQuickSqlFunctionCallExpression> aggregations);

    public JQuickDataSet alias(JQuickDataSet dataset, Map<String, JQuickSqlExpression> aliases);

    public JQuickDataSet limit(JQuickDataSet dataset, Integer limit, Integer offset);

}
