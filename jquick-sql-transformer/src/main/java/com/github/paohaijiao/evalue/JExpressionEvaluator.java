package com.github.paohaijiao.evalue;

import com.github.paohaijiao.enums.JLogicalOperator;
import com.github.paohaijiao.exception.JAssert;
import com.github.paohaijiao.expression.*;
import com.github.paohaijiao.function.JAggregateFunctionFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class JExpressionEvaluator extends JBaseEvaluator implements JSqlEvaluator<JExpression, Object> {

    public Object evaluate(JExpression expression, Map<String, Object> row) {
        if (expression instanceof JColumnExpression) {
            return evaluateExpression(expression, row);
        } else if (expression instanceof JLiteralExpression) {
            return evaluateExpression(expression, row);
        } else if (expression instanceof JBinaryExpression) {
            return evaluateBinaryExpression((JBinaryExpression) expression, row);
        } else if (expression instanceof JUnaryExpression) {
            return evaluateUnaryExpression((JUnaryExpression) expression, row);
        } else if (expression instanceof JNotExpression) {
            return evaluateNotExpression((JNotExpression) expression, row);
        } else if (expression instanceof JParenthesizedExpression) {
            return evaluateParenthesizedExpression((JParenthesizedExpression) expression, row);
        } else if (expression instanceof JFunctionCallExpression) {
            return evaluateFunctionCallExpression((JFunctionCallExpression) expression, row);
        } else if (expression instanceof JCaseExpression) {
            return evaluateCaseExpression((JCaseExpression) expression, row);
        } else if (expression instanceof JLogicalExpression) {
            return evaluateLogicalExpression((JLogicalExpression) expression, row);
        } else if (expression instanceof JDataSetExpression) {
            return evaluateDataSetExpression((JDataSetExpression) expression, row);
        } else if (null == expression) {
            return null;
        }
        throw new UnsupportedOperationException("Unsupported expression type: " + expression.getType());
    }

    private Object evaluateNotExpression(JNotExpression expr, Map<String, Object> row) {
        Object value = evaluateExpression(expr.getExpression(), row);
        JAssert.isTrue(value instanceof Boolean, "the expression is not boolean");
        return !(Boolean) value;
    }

    private Object evaluateBinaryExpression(JBinaryExpression expr, Map<String, Object> row) {
        Object left = evaluate(expr.getLeft(), row);
        Object right = evaluate(expr.getRight(), row);
        switch (expr.getOperator()) {
            case EQ:
                return Objects.equals(left, right);
            case NEQ:
                return !Objects.equals(left, right);
            case GT:
                return compareNumbers(left, right) > 0;
            case GE:
                return compareNumbers(left, right) >= 0;
            case LT:
                return compareNumbers(left, right) < 0;
            case LE:
                return compareNumbers(left, right) <= 0;
            case ADD:
                return add(left, right);
            case SUBTRACT:
                return subtract(left, right);
            case MULTIPLY:
                return multiply(left, right);
            case DIVIDE:
                return divide(left, right);
            case MOD:
                return modulo(left, right);
            case AND:
                return convertToBoolean(left) && convertToBoolean(right);
            case OR:
                return convertToBoolean(left) || convertToBoolean(right);
            default:
                throw new UnsupportedOperationException("Unsupported binary operator: " + expr.getOperator());
        }
    }

    private Object evaluateUnaryExpression(JUnaryExpression expr, Map<String, Object> row) {
        Object value = evaluate(expr.getExpression(), row);
        switch (expr.getOperator()) {
            case PLUS:
                return value;
            case MINUS:
                return negate(value);
            case NOT:
                return !convertToBoolean(value);
            case BIT_NOT:
                return bitwiseNot(value);
            default:
                throw new UnsupportedOperationException("unsupported unary operator: " + expr.getOperator());
        }
    }

    private Object evaluateParenthesizedExpression(JParenthesizedExpression expr, Map<String, Object> row) {
        return evaluate(expr.getExpression(), row);
    }

    private Object evaluateFunctionCallExpression(JFunctionCallExpression expr, Map<String, Object> row) {
        String functionName = expr.getFunctionName().toUpperCase();
        List<JExpression> arguments = expr.getArguments();
        List<Object> list = new ArrayList<>();
        for (JExpression argument : arguments) {
            list.add(evaluateExpression(argument, row));
        }
        if (JAggregateFunctionFactory.containsFunction(functionName)) {//AggregateFunction
            Function<List<Object>, Object> function = JAggregateFunctionFactory.getFunction(functionName);
            return function.apply(list);
        } else {//normal function
            Object object = JEvaluator.evaluateFunction(functionName, list);
            return object;
        }


    }

    private Object evaluateCaseExpression(JCaseExpression expr, Map<String, Object> row) {
        if (expr.getElseExpression() != null) {
            return evaluate(expr.getElseExpression(), row);
        }
        return null;
    }

    private Object evaluateLogicalExpression(JLogicalExpression expr, Map<String, Object> row) {
        JAssert.notNull(expr.getLeft(), "the left expression  require not null\"");
        JAssert.notNull(expr.getRight(), "the right expression  require not null\"");
        JAssert.notNull(expr.getOperator(), "the operator  require not null");
        Object left = evaluate(expr.getLeft(), row);
        Object right = evaluate(expr.getRight(), row);
        JLogicalOperator operator = expr.getOperator();
        JAssert.notNull(operator, "the operator  invalid");
        if (JLogicalOperator.And.equals(operator)) {
            return convertToBoolean(left) && convertToBoolean(right);
        }
        if (JLogicalOperator.Or.equals(operator)) {
            return convertToBoolean(left) || convertToBoolean(right);
        }
        if (JLogicalOperator.XOR.equals(operator)) {
            return convertToBoolean(left) != convertToBoolean(right);
        }
        JAssert.throwNewException("unsupported logical operator: " + expr.getOperator());
        return null;
    }

    private Object evaluateDataSetExpression(JDataSetExpression expr, Map<String, Object> row) {
        throw new UnsupportedOperationException("Subquery evaluation not implemented yet");
    }

    private int compareNumbers(Object left, Object right) {
        Number leftNum = convertToNumber(left);
        Number rightNum = convertToNumber(right);
        return Double.compare(leftNum.doubleValue(), rightNum.doubleValue());
    }

    private Number convertToNumber(Object value) {
        if (value instanceof Number) {
            return (Number) value;
        } else if (value instanceof String) {
            try {
                return Double.parseDouble((String) value);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Cannot convert to number: " + value);
            }
        }
        throw new IllegalArgumentException("Cannot convert to number: " + value);
    }

    private Object add(Object left, Object right) {
        if (left instanceof String || right instanceof String) {
            return left.toString() + right.toString();
        }
        Number leftNum = convertToNumber(left);
        Number rightNum = convertToNumber(right);
        return leftNum.doubleValue() + rightNum.doubleValue();
    }

    private Object subtract(Object left, Object right) {
        Number leftNum = convertToNumber(left);
        Number rightNum = convertToNumber(right);
        return leftNum.doubleValue() - rightNum.doubleValue();
    }

    private Object multiply(Object left, Object right) {
        Number leftNum = convertToNumber(left);
        Number rightNum = convertToNumber(right);
        return leftNum.doubleValue() * rightNum.doubleValue();
    }

    private Object divide(Object left, Object right) {
        Number leftNum = convertToNumber(left);
        Number rightNum = convertToNumber(right);
        if (rightNum.doubleValue() == 0) {
            throw new ArithmeticException("Division by zero");
        }
        return leftNum.doubleValue() / rightNum.doubleValue();
    }

    private Object modulo(Object left, Object right) {
        Number leftNum = convertToNumber(left);
        Number rightNum = convertToNumber(right);
        if (rightNum.doubleValue() == 0) {
            throw new ArithmeticException("Division by zero");
        }
        return leftNum.doubleValue() % rightNum.doubleValue();
    }

    private Object negate(Object value) {
        Number num = convertToNumber(value);
        return -num.doubleValue();
    }

    private Object bitwiseNot(Object value) {
        if (value instanceof Integer) {
            return ~((Integer) value);
        } else if (value instanceof Long) {
            return ~((Long) value);
        }
        throw new IllegalArgumentException("Bitwise NOT only supports integer types");
    }

    protected boolean convertToBoolean(Object value) {
        if (value instanceof Boolean) {
            return (Boolean) value;
        } else if (value instanceof Number) {
            return ((Number) value).doubleValue() != 0;
        } else if (value instanceof String) {
            return !((String) value).isEmpty();
        } else {
            return value != null;
        }
    }
}
