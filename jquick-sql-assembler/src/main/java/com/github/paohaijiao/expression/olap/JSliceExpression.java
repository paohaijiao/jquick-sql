package com.github.paohaijiao.expression.olap;

import com.github.paohaijiao.enums.JExpressionType;
import com.github.paohaijiao.expression.JExpression;
import lombok.Getter;

@Getter
public class JSliceExpression extends JExpression {

    private String dimension;

    private JExpression expression;

    public JSliceExpression(String dimension, JExpression expression) {
        this.type = JExpressionType.OLAP;
        this.dimension = dimension;
        this.expression = expression;
    }
}
