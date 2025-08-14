package com.github.paohaijiao.condition;

import com.github.paohaijiao.expression.JColumnExpression;
import com.github.paohaijiao.expression.JExpression;
import com.github.paohaijiao.expression.JLiteralExpression;

import java.util.Map;
import java.util.Objects;

public class JConditionEvaluator {

    public boolean evaluate(JCondition condition, Map<String, Object> row) {
        if (condition instanceof JComparisonCondition) {
            return evaluateComparison((JComparisonCondition) condition, row);
        } else if (condition instanceof JLogicalCondition) {
            return evaluateLogical((JLogicalCondition) condition, row);
        } else if (condition instanceof JBetweenCondition) {
            return evaluateBetween((JBetweenCondition) condition, row);
        }
        throw new UnsupportedOperationException("Unsupported condition type: " + condition.getType());
    }

    private boolean evaluateComparison(JComparisonCondition cond, Map<String, Object> row) {
        Object leftVal = evaluateExpression(cond.getLeft(), row);
        Object rightVal = evaluateExpression(cond.getRight(), row);

        switch (cond.getOperator()) {
            case EQ: return Objects.equals(leftVal, rightVal);
            case NEQ: return !Objects.equals(leftVal, rightVal);
            case GT: return compare(leftVal, rightVal) > 0;
            case GTE: return compare(leftVal, rightVal) >= 0;
            case LT: return compare(leftVal, rightVal) < 0;
            case LTE: return compare(leftVal, rightVal) <= 0;
            default:
                throw new UnsupportedOperationException("Unsupported operator: " + cond.getOperator());
        }
    }

    private boolean evaluateLogical(JLogicalCondition cond, Map<String, Object> row) {
        switch (cond.getType()) {
            case AND:
                return cond.getConditions().stream().allMatch(c -> evaluate(c, row));
            case OR:
                return cond.getConditions().stream().anyMatch(c -> evaluate(c, row));
            case NOT:
                return !evaluate(cond.getConditions().get(0), row);
            default:
                throw new UnsupportedOperationException("Unsupported logical condition: " + cond.getType());
        }
    }

    private boolean evaluateBetween(JBetweenCondition cond, Map<String, Object> row) {
        Object val = evaluateExpression(cond.getExpression(), row);
        Object lower = evaluateExpression(cond.getLowerBound(), row);
        Object upper = evaluateExpression(cond.getUpperBound(), row);

        boolean between = compare(val, lower) >= 0 && compare(val, upper) <= 0;
        return cond.isNot() ? !between : between;
    }

    @SuppressWarnings("unchecked")
    private int compare(Object left, Object right) {
        if (left == null || right == null) {
            throw new IllegalArgumentException("Cannot compare null values");
        }
        if (left instanceof Comparable && right instanceof Comparable) {
            return ((Comparable<Object>) left).compareTo(right);
        }
        throw new IllegalArgumentException("Values are not comparable: " + left.getClass() + " and " + right.getClass());
    }

    private Object evaluateExpression(JExpression expr, Map<String, Object> row) {
        if (expr instanceof JColumnExpression) {
            return row.get(((JColumnExpression) expr).getColumnName());
        } else if (expr instanceof JLiteralExpression) {
            return ((JLiteralExpression) expr).getValue();
        }
        throw new UnsupportedOperationException("Unsupported expression type: " + expr.getType());
    }
}
