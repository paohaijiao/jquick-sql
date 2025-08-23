package com.github.paohaijiao.condition;

import com.github.paohaijiao.expression.JColumnExpression;
import com.github.paohaijiao.expression.JExpression;
import com.github.paohaijiao.expression.JLiteralExpression;

import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

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
    private boolean patternMatch(String input, String pattern, boolean isRegex) {
        if (isRegex) {
            return input.matches(pattern);
        } else {
            String regex = pattern.replace("%", ".*").replace("_", ".");
            return input.matches(regex);
        }
    }
    private boolean evaluateStringComparison(JComparisonCondition cond, Map<String, Object> row) {
        Object leftVal = evaluateExpression(cond.getLeft(), row);
        Object rightVal = evaluateExpression(cond.getRight(), row);
        String type=cond.getOperator().getSymbol();
        switch (type) {
            case "LIKE":
                return likeMatch(leftVal.toString(), rightVal.toString(), null);
            case "NOT LIKE":
                return !likeMatch(leftVal.toString(), rightVal.toString(), null);
            case "REGEXP":
            case "RLIKE":
                return regexpMatch(leftVal.toString(), rightVal.toString(), false);
           default:
                throw new UnsupportedOperationException("Unsupported operator: " + cond.getOperator());
        }
    }
    private boolean evaluateComparison(JComparisonCondition cond, Map<String, Object> row) {
        Object leftVal = evaluateExpression(cond.getLeft(), row);
        Object rightVal = evaluateExpression(cond.getRight(), row);
        switch (cond.getOperator()) {
            case EQ: return Objects.equals(leftVal, rightVal);
            case NEQ: return !Objects.equals(leftVal, rightVal);
            case GT: return compare(leftVal, rightVal) > 0;
            case GE: return compare(leftVal, rightVal) >= 0;
            case LT: return compare(leftVal, rightVal) < 0;
            case LE: return compare(leftVal, rightVal) <= 0;
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
    public boolean likeMatch(String input, String pattern, Character escapeChar) {
        StringBuilder regex = new StringBuilder();
        boolean escaping = false;
        for (int i = 0; i < pattern.length(); i++) {
            char c = pattern.charAt(i);
            if (escaping) {
                regex.append(Pattern.quote(String.valueOf(c)));
                escaping = false;
            } else {
                if (escapeChar != null && c == escapeChar) {
                    escaping = true;
                    continue;
                }
                switch (c) {
                    case '%':
                        regex.append(".*");
                        break;
                    case '_':
                        regex.append(".");
                        break;
                    default:
                        regex.append(Pattern.quote(String.valueOf(c)));
                }
            }
        }
        String regexPattern = "^" + regex + "$";
        return input.matches(regexPattern);
    }

    public boolean regexpMatch(String input, String pattern, boolean caseSensitive) {
        String javaPattern = pattern
                .replace("[[:<:]]", "\\b")
                .replace("[[:>:]]", "\\b");
        int flags = caseSensitive ? 0 : Pattern.CASE_INSENSITIVE;
        Pattern compiledPattern;
        try {
            compiledPattern = Pattern.compile(javaPattern, flags);
        } catch (PatternSyntaxException e) {
            throw new RuntimeException("Invalid regex pattern: " + pattern, e);
        }
        return compiledPattern.matcher(input).find();
    }
}
