package com.github.paohaijiao.support;

import com.github.paohaijiao.condition.*;
import com.github.paohaijiao.exception.JAssert;
import com.github.paohaijiao.expression.*;

import java.util.Map;

public class JExpressionEvaluator {

    public Object evaluate(JExpression expression, Map<String, Object> row) {
        if (expression instanceof JNotExpression) {
            return evaluateNotExpression((JNotExpression) expression, row);
        }
        throw new UnsupportedOperationException("Unsupported expression type: " + expression.getType());
    }
    private Object evaluateNotExpression(JNotExpression expr, Map<String, Object> row) {
        Object value = evaluateExpression(expr.getExpression(), row);
        JAssert.isTrue(value instanceof Boolean,"the expression is not boolean");
        return !(Boolean) value;
    }
    private Object evaluateExpression(JExpression expr, Map<String, Object> row) {
        if (expr instanceof JColumnExpression) {
            return row.get(((JColumnExpression) expr).getColumnName());
        } else if (expr instanceof JLiteralExpression) {
            return ((JLiteralExpression) expr).getValue();
        }
        throw new UnsupportedOperationException("unsupported expression type: " + expr.getType());
    }
    protected boolean convertToBoolean(Object value) {
        if (value instanceof Boolean) {
            return (Boolean) value;
        } else {
            JAssert.throwNewException("Cannot convert to boolean: " + value);
            return false;
        }
    }
}
