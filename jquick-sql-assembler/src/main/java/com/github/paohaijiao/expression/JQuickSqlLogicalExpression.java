package com.github.paohaijiao.expression;

import com.github.paohaijiao.enums.JQuickSqlLogicalOperator;
import lombok.Getter;

import java.util.Objects;

@Getter
public class JQuickSqlLogicalExpression extends JQuickSqlExpression {

    protected final JQuickSqlExpression left;

    protected final JQuickSqlExpression right;

    protected final JQuickSqlLogicalOperator operator;

    public JQuickSqlLogicalExpression(JQuickSqlExpression left, JQuickSqlLogicalOperator operator, JQuickSqlExpression right) {
        this.left = Objects.requireNonNull(left);
        this.operator = Objects.requireNonNull(operator);
        this.right = right;
    }
}
