package com.github.paohaijiao.factory;

import com.github.paohaijiao.condition.JCondition;
import com.github.paohaijiao.dataset.JDataSet;
import com.github.paohaijiao.expression.JAggregateExpression;
import com.github.paohaijiao.expression.JExpression;
import com.github.paohaijiao.expression.JFunctionCallExpression;
import com.github.paohaijiao.expression.JOrderByExpression;
import com.github.paohaijiao.func.JoinCondition;

import java.util.List;
import java.util.Map;

public interface JDataSetJoinerStrategy {

    JDataSet innerJoin(JDataSet left, JDataSet right, JoinCondition condition);

    JDataSet leftJoin(JDataSet left, JDataSet right, JoinCondition condition);

    JDataSet fullOuterJoin(JDataSet left, JDataSet right, JoinCondition condition);

    JDataSet crossJoin(JDataSet left, JDataSet right);

    JDataSet naturalJoin(JDataSet left, JDataSet right);

    public  JDataSet union(JDataSet ds1, JDataSet ds2);

    public  JDataSet intersect(JDataSet ds1, JDataSet ds2);

    public  JDataSet minus(JDataSet ds1, JDataSet ds2);

    public  JDataSet selectColumns(JDataSet dataset, List<String> columnNames);

    public  JDataSet filter(JDataSet dataset, JCondition condition);

    public  JDataSet transform(JDataSet dataset, Map<String, JFunctionCallExpression> transformations);

    public  JDataSet sort(JDataSet dataset, List<JOrderByExpression> orderByExpressions);

    public  JDataSet aggregate(JDataSet dataset, List<String> groupBy, Map<String, JAggregateExpression> aggregations);

    public  JDataSet alias(JDataSet dataset, Map<String, JExpression> aliases) ;

}
