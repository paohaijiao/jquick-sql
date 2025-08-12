package com.github.paohaijiao.visitor;

import com.github.paohaijiao.expression.*;

public interface JExpressionVisitor<T>{

    T visit(JColumnExpression expr);

    T visit(JLiteralExpression expr);

    T visit(JBinaryExpression expr);

    T visit(JUnaryExpression expr);

    T visit(JFunctionCallExpression expr);

    T visit(JAggregateExpression expr);

    T visit(JCaseExpression expr);

    T visit(JSubqueryExpression expr);

    T visit(JParenthesizedExpression expr);
}
