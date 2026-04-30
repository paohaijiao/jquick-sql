package com.github.paohaijiao.expression.olap;

import com.github.paohaijiao.enums.JQuickSqlExpressionType;
import com.github.paohaijiao.expression.JQuickSqlExpression;
import lombok.Getter;

@Getter
public class JQuickSqlSliceExpression extends JQuickSqlExpression {

    private String dimension;

    private JQuickSqlExpression expression;

    public JQuickSqlSliceExpression(String dimension, JQuickSqlExpression expression) {
        this.type = JQuickSqlExpressionType.OLAP;
        this.dimension = dimension;
        this.expression = expression;
    }
}
