package com.github.paohaijiao.expression;

import com.github.paohaijiao.enums.JLogicalOperator;
import lombok.Getter;

import java.util.Objects;

@Getter
public class JLogicalExpression extends JExpression {

    protected final JExpression left;

    protected final JExpression right;

    protected final JLogicalOperator operator;

    public JLogicalExpression(JExpression left, JLogicalOperator operator, JExpression right) {
        this.left = Objects.requireNonNull(left);
        this.operator = Objects.requireNonNull(operator);
        this.right = right;
    }
}
