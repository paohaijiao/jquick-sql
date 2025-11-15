package com.github.paohaijiao.factory;

import com.github.paohaijiao.condition.JCondition;
import com.github.paohaijiao.dataset.DataSet;
import com.github.paohaijiao.expression.JExpression;
import com.github.paohaijiao.expression.JFunctionCallExpression;
import com.github.paohaijiao.expression.JOrderByExpression;
import com.github.paohaijiao.join.JoinCondition;

import java.util.List;
import java.util.Map;

public interface JDataSetJoinerStrategy {

    DataSet innerJoin(DataSet left, DataSet right, JoinCondition condition);

    DataSet leftJoin(DataSet left, DataSet right, JoinCondition condition);

    DataSet rightJoin(DataSet left, DataSet right, JoinCondition condition);

    DataSet fullOuterJoin(DataSet left, DataSet right, JoinCondition condition);

    DataSet crossJoin(DataSet left, DataSet right);

    DataSet naturalJoin(DataSet left, DataSet right);

    public  DataSet union(DataSet ds1, DataSet ds2);

    public  DataSet intersect(DataSet ds1, DataSet ds2);

    public  DataSet minus(DataSet ds1, DataSet ds2);

    public  DataSet selectColumns(DataSet dataset, List<String> columnNames);

    public  DataSet filter(DataSet dataset, JCondition condition);

    public  DataSet transform(DataSet dataset, Map<String, JFunctionCallExpression> transformations);

    public  DataSet sort(DataSet dataset, List<JOrderByExpression> orderByExpressions);

    public  DataSet aggregate(DataSet dataset, List<String> groupBy, Map<String, JFunctionCallExpression> aggregations);

    public  DataSet alias(DataSet dataset, Map<String, JExpression> aliases) ;

    public  DataSet limit(DataSet dataset,Integer limit ,Integer offset) ;

}
