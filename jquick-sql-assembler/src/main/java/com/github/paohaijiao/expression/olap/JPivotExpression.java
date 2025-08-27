package com.github.paohaijiao.expression.olap;

import com.github.paohaijiao.enums.JExpressionType;
import com.github.paohaijiao.expression.JExpression;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Getter
public class JPivotExpression extends JExpression {

    private String pivotColumn;

    private String valueColumn;

    private Function<List<Object>, Object> aggregator;


    public JPivotExpression(String pivotColumn, String valueColumn, Function<List<Object>, Object> aggregator) {
        this.type = JExpressionType.OLAP;
        this.pivotColumn = pivotColumn;
        this.valueColumn = valueColumn;
        this.aggregator = aggregator;
    }
}
