package com.github.paohaijiao.expression;

import lombok.Getter;

import java.util.Objects;

@Getter
public class JLogicalExpression extends JExpression {

    protected final JExpression left;

    protected final JExpression right;

    protected final String operator;

    protected JLogicalExpression(JExpression left, String operator, JExpression right) {
        this.left = Objects.requireNonNull(left);
        this.operator = Objects.requireNonNull(operator);
        this.right = right;
    }
}
