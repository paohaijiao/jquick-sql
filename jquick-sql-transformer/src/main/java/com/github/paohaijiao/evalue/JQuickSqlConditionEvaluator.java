package com.github.paohaijiao.evalue;

import com.github.paohaijiao.condition.*;
import com.github.paohaijiao.exception.JAssert;
import com.github.paohaijiao.expression.JQuickSqlExpression;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.PatternSyntaxException;

public class JQuickSqlConditionEvaluator extends JQuickSqlBaseEvaluator implements JQuickSqlEvaluator<JQuickSqlCondition, Boolean> {

    public Boolean evaluate(JQuickSqlCondition condition, Map<String, Object> row) {
        if (condition instanceof JQuickSqlComparisonCondition) {
            return evaluateComparison((JQuickSqlComparisonCondition) condition, row);
        } else if (condition instanceof JQuickSqlLogicalCondition) {
            return evaluateLogical((JQuickSqlLogicalCondition) condition, row);
        } else if (condition instanceof JQuickSqlBetweenCondition) {
            return evaluateBetween((JQuickSqlBetweenCondition) condition, row);
        } else if (condition instanceof JQuickSqlIsNullCondition) {
            return evaluateIsNull((JQuickSqlIsNullCondition) condition, row);
        } else if (condition instanceof JQuickSqlInCondition) {
            return evaluateValueInList((JQuickSqlInCondition) condition, row);
        } else if (condition instanceof JQuickSqlLikeCondition) {
            return evaluateLike((JQuickSqlLikeCondition) condition, row);
        } else if (condition instanceof JQuickSqlRegexCondition) {
            return evaluateRegex((JQuickSqlRegexCondition) condition, row);
        } else if (condition instanceof JQuickSqlExistsCondition) {
            return evaluateExists((JQuickSqlExistsCondition) condition, row);
        } else if (condition instanceof JQuickSqlNotCondtion) {
            return evaluateNot((JQuickSqlNotCondtion) condition, row);
        } else if (condition instanceof JQuickSqlExpressionAtomPredicateCondition) {
            return evaluateAtomPredicate((JQuickSqlExpressionAtomPredicateCondition) condition, row);
        } else if (condition instanceof JQuickSqlParenthesesCondition) {
            return evaluateParentheses((JQuickSqlParenthesesCondition) condition, row);
        } else if (condition instanceof JQuickSqlAndCondition) {
            return evaluateAnd((JQuickSqlAndCondition) condition, row);
        } else if (condition instanceof JQuickSqlOrCondition) {
            return evaluateOr((JQuickSqlOrCondition) condition, row);
        }
        throw new UnsupportedOperationException("Unsupported condition type: " + condition.getType());
    }

    public boolean evaluateCondition(JQuickSqlCondition condition, Map<String, Object> row) {
        if (condition instanceof JQuickSqlAndCondition) {
            JQuickSqlAndCondition andExpr = (JQuickSqlAndCondition) condition;
            return andExpr.getConditions().stream().allMatch(cond -> evaluateCondition(cond, row));
        }
        if (condition instanceof JQuickSqlOrCondition) {
            JQuickSqlOrCondition orExpr = (JQuickSqlOrCondition) condition;
            return orExpr.getConditions().stream().anyMatch(cond -> evaluateCondition(cond, row));
        }

        if (condition instanceof JQuickSqlParenthesesCondition) {
            JQuickSqlParenthesesCondition parenExpr = (JQuickSqlParenthesesCondition) condition;
            if (parenExpr.hasInnerCondition()) {
                return evaluateCondition(parenExpr.getInnerCondition(), row);
            }
            return false;
        }
        return evaluate(condition, row);
    }

    private boolean evaluateParentheses(JQuickSqlParenthesesCondition cond, Map<String, Object> row) {
        if (!cond.hasInnerCondition()) {
            throw new IllegalArgumentException("Parentheses condition must have an inner condition");
        }
        return evaluate(cond.getInnerCondition(), row);
    }

    private boolean evaluateAnd(JQuickSqlAndCondition cond, Map<String, Object> row) {
        if (cond.isEmpty()) {
            throw new IllegalArgumentException("AND expression must have at least one condition");
        }
        return cond.getConditions().stream().allMatch(c -> evaluate(c, row));
    }

    private boolean evaluateOr(JQuickSqlOrCondition cond, Map<String, Object> row) {
        if (cond.isEmpty()) {
            throw new IllegalArgumentException("OR expression must have at least one condition");
        }
        return cond.getConditions().stream().anyMatch(c -> evaluate(c, row));
    }

    private boolean evaluateAtomPredicate(JQuickSqlExpressionAtomPredicateCondition cond, Map<String, Object> row) {
        Object val = evaluateExpression(cond.getExpression(), row);
        if (val == null) {
            return false;
        }
        JAssert.isTrue(val instanceof Boolean, "the expression is not a boolean type");
        return (Boolean) val;
    }

    private boolean evaluateExists(JQuickSqlExistsCondition cond, Map<String, Object> row) {
        return !cond.getDataSet().isEmpty();
    }

    private boolean evaluateNot(JQuickSqlNotCondtion cond, Map<String, Object> row) {
        Object val = evaluateExpression(cond.getExpression(), row);
        if (val == null) {
            return false;
        }
        JAssert.isTrue(val instanceof Boolean, "the expression is not a boolean type");
        return !(Boolean) val;
    }

    private boolean evaluateRegex(JQuickSqlRegexCondition cond, Map<String, Object> row) {
        Object val = evaluateExpression(cond.getExpression(), row);
        String regex = cond.getRegex();
        boolean isNot = cond.getNot();
        String input = val.toString();
        boolean matches;
        try {
            matches = input.matches(regex);
        } catch (PatternSyntaxException e) {
            throw new RuntimeException("Invalid regex pattern: " + regex);
        }
        return isNot != matches;
    }

    private boolean evaluateLike(JQuickSqlLikeCondition cond, Map<String, Object> row) {
        Object val = evaluateExpression(cond.getExpression(), row);
        String pattern = cond.getPattern();
        boolean isNot = cond.getNot();
        String input = val.toString();
        boolean matches = likeMatch(input, pattern);
        return isNot != matches;
    }

    private boolean evaluateValueInList(JQuickSqlInCondition cond, Map<String, Object> row) {
        Object val = evaluateExpression(cond.getExpression(), row);
        List<JQuickSqlExpression> list = cond.getList();
        for (JQuickSqlExpression item : list) {
            Object value = evaluateExpression(item, row);
            if (value == null) {
                continue;
            }
            if (compareValues(val, value) == 0) {
                return true;
            }
        }
        return false;
    }

    private boolean evaluateIsNull(JQuickSqlIsNullCondition cond, Map<String, Object> row) {
        Object val = evaluateExpression(cond.getExpression(), row);
        boolean requireNull = cond.getNot();
        if (requireNull) {
            return val != null;
        } else {
            return val == null;
        }
    }

    private boolean evaluateComparison(JQuickSqlComparisonCondition cond, Map<String, Object> row) {
        Object leftVal = evaluateExpression(cond.getLeft(), row);
        Object rightVal = evaluateExpression(cond.getRight(), row);
        switch (cond.getOperator()) {
            case EQ:
                return Objects.equals(leftVal, rightVal);
            case NEQ:
                return !Objects.equals(leftVal, rightVal);
            case GT:
                return compare((Number) leftVal, (Number) rightVal) > 0;
            case GE:
                return compare((Number) leftVal, (Number) rightVal) >= 0;
            case LT:
                return compare((Number) leftVal, (Number) rightVal) < 0;
            case LE:
                return compare((Number) leftVal, (Number) rightVal) <= 0;
            default:
                throw new UnsupportedOperationException("Unsupported operator: " + cond.getOperator());
        }
    }

    private boolean evaluateLogical(JQuickSqlLogicalCondition cond, Map<String, Object> row) {
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

    private boolean evaluateBetween(JQuickSqlBetweenCondition cond, Map<String, Object> row) {
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
