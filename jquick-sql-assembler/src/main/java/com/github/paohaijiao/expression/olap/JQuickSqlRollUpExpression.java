package com.github.paohaijiao.expression.olap;

import com.github.paohaijiao.enums.JQuickSqlExpressionType;
import com.github.paohaijiao.expression.JQuickSqlExpression;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Getter
public class JQuickSqlRollUpExpression extends JQuickSqlExpression {

    private List<String> groupByColumns;

    private Map<String, Function<List<Object>, Object>> aggregations;


    public JQuickSqlRollUpExpression(List<String> groupByColumns, Map<String, Function<List<Object>, Object>> aggregations) {
        this.type = JQuickSqlExpressionType.OLAP;
        this.groupByColumns = groupByColumns;
        this.aggregations = aggregations;
    }
}
