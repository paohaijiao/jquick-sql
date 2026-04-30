package com.github.paohaijiao.factory;

import com.github.paohaijiao.condition.JQuickSqlCondition;
import com.github.paohaijiao.dataset.DataSet;
import com.github.paohaijiao.expression.JQuickSqlExpression;
import com.github.paohaijiao.expression.JQuickSqlFunctionCallExpression;
import com.github.paohaijiao.expression.JQuickSqlOrderByExpression;
import com.github.paohaijiao.join.JQuickSqlJoinCondition;

import java.util.List;
import java.util.Map;

public interface JQuickSqlDataSetJoinerStrategy {

    DataSet innerJoin(DataSet left, DataSet right, JQuickSqlJoinCondition condition);

    DataSet leftJoin(DataSet left, DataSet right, JQuickSqlJoinCondition condition);

    DataSet rightJoin(DataSet left, DataSet right, JQuickSqlJoinCondition condition);

    DataSet fullOuterJoin(DataSet left, DataSet right, JQuickSqlJoinCondition condition);

    DataSet crossJoin(DataSet left, DataSet right);

    DataSet naturalJoin(DataSet left, DataSet right);

    public DataSet union(DataSet ds1, DataSet ds2);

    public DataSet intersect(DataSet ds1, DataSet ds2);

    public DataSet minus(DataSet ds1, DataSet ds2);

    public DataSet selectColumns(DataSet dataset, List<String> columnNames);

    public DataSet filter(DataSet dataset, JQuickSqlCondition condition);

    public DataSet transform(DataSet dataset, Map<String, JQuickSqlFunctionCallExpression> transformations);

    public DataSet sort(DataSet dataset, List<JQuickSqlOrderByExpression> orderByExpressions);

    public DataSet aggregate(DataSet dataset, List<String> groupBy, Map<String, JQuickSqlFunctionCallExpression> aggregations);

    public DataSet alias(DataSet dataset, Map<String, JQuickSqlExpression> aliases);

    public DataSet limit(DataSet dataset, Integer limit, Integer offset);

}
