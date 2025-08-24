package com.github.paohaijiao.support;

import com.github.paohaijiao.condition.*;
import com.github.paohaijiao.exception.JAssert;
import com.github.paohaijiao.expression.JColumnExpression;
import com.github.paohaijiao.expression.JExpression;
import com.github.paohaijiao.expression.JLiteralExpression;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.PatternSyntaxException;

public class JConditionEvaluator {

    public boolean evaluate(JCondition condition, Map<String, Object> row) {
        if (condition instanceof JComparisonCondition) {
            return evaluateComparison((JComparisonCondition) condition, row);
        } else if (condition instanceof JLogicalCondition) {
            return evaluateLogical((JLogicalCondition) condition, row);
        } else if (condition instanceof JBetweenCondition) {
            return evaluateBetween((JBetweenCondition) condition, row);
        } else if (condition instanceof JIsNullCondition) {
            return evaluateIsNull((JIsNullCondition) condition, row);
        } else if (condition instanceof JInCondition) {
            return evaluateValueInList((JInCondition) condition, row);
        }else if (condition instanceof JLikeCondition) {
            return evaluateLike((JLikeCondition) condition, row);
        }else if (condition instanceof JRegexCondition) {
            return evaluateRegex((JRegexCondition) condition, row);
        }else if (condition instanceof JExistsCondition) {
            return evaluateExists((JExistsCondition) condition, row);
        }else if (condition instanceof JNotCondtion) {
            return evaluateNot((JNotCondtion) condition, row);
        } else if (condition instanceof JExpressionAtomPredicateCondition) {
            return evaluateAtomPredicate((JExpressionAtomPredicateCondition) condition, row);
        }else if (condition instanceof JParenthesesCondition) {
            return evaluateParentheses((JParenthesesCondition) condition, row);
        } else if (condition instanceof JAndCondition) {
            return evaluateAnd((JAndCondition) condition, row);
        } else if (condition instanceof JOrCondition) {
            return evaluateOr((JOrCondition) condition, row);
        }
        throw new UnsupportedOperationException("Unsupported condition type: " + condition.getType());
    }
    private boolean evaluateParentheses(JParenthesesCondition cond, Map<String, Object> row) {
        if (!cond.hasInnerCondition()) {
            throw new IllegalArgumentException("Parentheses condition must have an inner condition");
        }
        return evaluate(cond.getInnerCondition(), row);
    }
    private boolean evaluateAnd(JAndCondition cond, Map<String, Object> row) {
        if (cond.isEmpty()) {
            throw new IllegalArgumentException("AND expression must have at least one condition");
        }
        return cond.getConditions().stream()
                .allMatch(c -> evaluate(c, row));
    }

    private boolean evaluateOr(JOrCondition cond, Map<String, Object> row) {
        if (cond.isEmpty()) {
            throw new IllegalArgumentException("OR expression must have at least one condition");
        }
        return cond.getConditions().stream()
                .anyMatch(c -> evaluate(c, row));
    }
    private boolean evaluateAtomPredicate(JExpressionAtomPredicateCondition cond, Map<String, Object> row) {
        Object val = evaluateExpression(cond.getExpression(), row);
        if(val==null){
            return false;
        }
        JAssert.isTrue(val instanceof Boolean,"the expression is not a boolean type");
        return (Boolean)val;
    }
    private boolean evaluateExists(JExistsCondition cond, Map<String, Object> row) {
        return !cond.getDataSet().isEmpty();
    }
    private boolean evaluateNot(JNotCondtion cond, Map<String, Object> row) {
        Object val = evaluateExpression(cond.getExpression(), row);
        if(val==null){
            return false;
        }
        JAssert.isTrue(val instanceof Boolean,"the expression is not a boolean type");
        return !(Boolean)val;
    }
    private boolean evaluateRegex(JRegexCondition cond, Map<String, Object> row) {
        Object val = evaluateExpression(cond.getExpression(), row);
        String regex=cond.getRegex();
        boolean isNot=cond.getNot();
        String input = val.toString();
        boolean matches;
        try {
            matches = input.matches(regex);
        } catch (PatternSyntaxException e) {
            throw new RuntimeException("Invalid regex pattern: " + regex);
        }
        return isNot != matches;
    }
    private boolean evaluateLike(JLikeCondition cond, Map<String, Object> row) {
        Object val = evaluateExpression(cond.getExpression(), row);
        String pattern=cond.getPattern();
        boolean isNot=cond.getNot();
        String input = val.toString();
        boolean matches = likeMatch(input, pattern);
        return isNot != matches;
    }

    private boolean evaluateValueInList(JInCondition cond, Map<String, Object> row) {
        Object val = evaluateExpression(cond.getExpression(), row);
        List<Object> list=cond.getList();
        for (Object item : list) {
            if (item == null) {
                continue;
            }
            if (compareValues(val, item) == 0) {
                return true;
            }
        }
        return false;
    }
    private boolean evaluateIsNull(JIsNullCondition cond, Map<String, Object> row) {
        Object val = evaluateExpression(cond.getExpression(), row);
        boolean requireNull = cond.getNot();
        if(requireNull){
            return val != null;
        }else{
            return val == null;
        }
    }
    private boolean evaluateComparison(JComparisonCondition cond, Map<String, Object> row) {
        Object leftVal = evaluateExpression(cond.getLeft(), row);
        Object rightVal = evaluateExpression(cond.getRight(), row);
        switch (cond.getOperator()) {
            case EQ: return Objects.equals(leftVal, rightVal);
            case NEQ: return !Objects.equals(leftVal, rightVal);
            case GT: return compare((Number)leftVal, (Number)rightVal) > 0;
            case GE: return compare((Number)leftVal,(Number) rightVal) >= 0;
            case LT: return compare((Number)leftVal, (Number)rightVal) < 0;
            case LE: return compare((Number)leftVal, (Number)rightVal) <= 0;
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
            BigDecimal thisDecimal = new BigDecimal(left.toString());
            BigDecimal otherDecimal = new BigDecimal(right.toString());
            return thisDecimal.compareTo(otherDecimal);
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


    private int compareValues(Object a, Object b) {
        if (a instanceof Number && b instanceof Number) {
            double d1 = ((Number) a).doubleValue();
            double d2 = ((Number) b).doubleValue();
            return Double.compare(d1, d2);
        } else if (a instanceof Comparable && b instanceof Comparable) {
            return ((Comparable) a).compareTo(b);
        }
        return a.toString().compareTo(b.toString());
    }
    private String removeQuotes(String str) {
        if (str == null || str.length() < 2) {
            return str;
        }
        char firstChar = str.charAt(0);
        char lastChar = str.charAt(str.length() - 1);
        if ((firstChar == '\'' && lastChar == '\'') ||
                (firstChar == '"' && lastChar == '"')) {
            return str.substring(1, str.length() - 1);
        }
        return str;
    }
    private boolean likeMatch(String input, String pattern) {
        String cleanInput = removeQuotes(input);
        String cleanPattern = removeQuotes(pattern);
        if (cleanPattern.equals("%")) {
            return !cleanInput.isEmpty();
        }
        if (cleanPattern.startsWith("%") && cleanPattern.endsWith("%")) {
            String middle = cleanPattern.substring(1, cleanPattern.length() - 1);
            return cleanInput.contains(middle);
        }
        if (cleanPattern.endsWith("%")) {
            String prefix = cleanPattern.substring(0, cleanPattern.length() - 1);
            return cleanInput.startsWith(prefix);
        }
        if (cleanPattern.startsWith("%")) {
            String suffix = cleanPattern.substring(1);
            return cleanInput.endsWith(suffix);
        }
        return cleanInput.equals(cleanPattern);
    }
}
