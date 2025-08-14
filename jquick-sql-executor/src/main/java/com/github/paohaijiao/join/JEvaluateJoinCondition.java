package com.github.paohaijiao.join;

import com.github.paohaijiao.enums.JBinaryOperator;
import com.github.paohaijiao.expression.*;
import com.github.paohaijiao.util.JEntityAccessor;
import java.util.Objects;

public class JEvaluateJoinCondition<T,R> {

    private final JEntityAccessor<T> leftAccessor;

    private final JEntityAccessor<R> rightAccessor;


    public JEvaluateJoinCondition(JEntityAccessor<T> leftAccessor, JEntityAccessor<R> rightAccessor) {
        this.leftAccessor = Objects.requireNonNull(leftAccessor);
        this.rightAccessor = Objects.requireNonNull(rightAccessor);
    }
    public boolean evaluateJoinCondition(T leftRecord, R rightRecord, JExpression condition) {
        if (condition instanceof JBinaryExpression) {//col1 = col2
            JBinaryExpression binaryExpr = (JBinaryExpression) condition;
            Object leftValue = evaluateOperand(leftRecord, rightRecord, binaryExpr.getLeft());
            Object rightValue = evaluateOperand(leftRecord, rightRecord, binaryExpr.getRight());
            return compareValues(leftValue, rightValue, binaryExpr.getOperator());
        }
        // AND/OR
        if (condition instanceof JLogicalExpression) {
            JLogicalExpression logicalExpr = (JLogicalExpression) condition;
            boolean leftResult = evaluateJoinCondition(leftRecord, rightRecord, logicalExpr.getLeft());
            boolean rightResult = evaluateJoinCondition(leftRecord, rightRecord, logicalExpr.getRight());
            switch (logicalExpr.getOperator()) {
                case "AND": return leftResult && rightResult;
                case "OR":  return leftResult || rightResult;
                default:    throw new UnsupportedOperationException("Unsupported logical operator: " + logicalExpr.getOperator());
            }
        }

        throw new UnsupportedOperationException("Unsupported expression type: " + condition.getClass());
    }


    private Object evaluateOperand(T leftRecord, R rightRecord, JExpression operand) {
        if (operand instanceof JColumnExpression) {
            JColumnExpression colExpr = (JColumnExpression) operand;
            String[] parts = colExpr.getColumnName().split("\\.");
            if (parts.length == 2) {//as t1.id
                String tableAlias = parts[0];
                String columnName = parts[1];
//                if (tableAlias.equals(leftAccessor.getgetAlias())) {
//                    return leftAccessor.getValue(leftRecord, columnName);
//                } else if (tableAlias.equals(rightAccessor.getAlias())) {
//                    return rightAccessor.getValue(rightRecord, columnName);
//                }
                throw new IllegalArgumentException("Unknown table alias: " + tableAlias);
            } else {
                return tryResolveColumn(leftRecord, rightRecord, colExpr.getColumnName());
            }
        }

        if (operand instanceof JLiteralExpression) {
            return ((JLiteralExpression) operand).getValue();
        }

        throw new UnsupportedOperationException("Unsupported operand type: " + operand.getClass());
    }

    /**
     * 自动解析列所属表
     */
    private Object tryResolveColumn(T leftRecord, R rightRecord, String columnName) {
        if (leftAccessor.hasField(columnName)) {
            return leftAccessor.getValue(leftRecord, columnName);
        }
        if (rightAccessor.hasField(columnName)) {
            return rightAccessor.getValue(rightRecord, columnName);
        }
        throw new IllegalArgumentException("Column not found in either table: " + columnName);
    }
    private boolean compareValues(Object left, Object right, JBinaryOperator operator) {
        if (left == null || right == null) {
            return handleNullComparison(left, right, operator);
        }
        Comparable<Object> leftComp = convertToComparable(left);
        Comparable<Object> rightComp = convertToComparable(right);
        int cmp;
        try {
            cmp = leftComp.compareTo(rightComp);
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Cannot compare " + left.getClass() + " with " + right.getClass());
        }
        switch (operator) {
            case EQ:  return cmp == 0;
            case NEQ: return cmp != 0;
            case LT:  return cmp < 0;
            case LTE: return cmp <= 0;
            case GT:  return cmp > 0;
            case GTE: return cmp >= 0;
            default: throw new UnsupportedOperationException("Unsupported operator: " + operator);
        }
    }

    private boolean handleNullComparison(Object left, Object right, JBinaryOperator operator) {
        switch (operator) {
            case EQ:  return left == null && right == null;
            case NEQ: return left != null || right != null;
            case IS: return left == null && right == null;
            case NOT: return left != null || right != null;
            default: return false;
        }
    }

    @SuppressWarnings("unchecked")
    private Comparable<Object> convertToComparable(Object value) {
        if (value instanceof Comparable) {
            return (Comparable<Object>) value;
        }
        return (Comparable<Object>) (Comparable<?>) value.toString();
    }

}
