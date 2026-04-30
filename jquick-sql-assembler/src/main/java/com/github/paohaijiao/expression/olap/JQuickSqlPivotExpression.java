package com.github.paohaijiao.expression.olap;

import com.github.paohaijiao.enums.JQuickSqlExpressionType;
import com.github.paohaijiao.expression.JQuickSqlExpression;
import lombok.Getter;

import java.util.List;
import java.util.function.Function;

@Getter
public class JQuickSqlPivotExpression extends JQuickSqlExpression {

    private String pivotColumn;

    private String valueColumn;

    private Function<List<Object>, Object> aggregator;


    public JQuickSqlPivotExpression(String pivotColumn, String valueColumn, Function<List<Object>, Object> aggregator) {
        this.type = JQuickSqlExpressionType.OLAP;
        this.pivotColumn = pivotColumn;
        this.valueColumn = valueColumn;
        this.aggregator = aggregator;
    }
}
