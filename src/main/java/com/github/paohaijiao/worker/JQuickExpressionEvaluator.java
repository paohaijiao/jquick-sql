/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Copyright (c) [2025-2099] Martin (goudingcheng@gmail.com)
 */
package com.github.paohaijiao.worker;

import com.github.paohaijiao.enums.JQuickBinaryOperator;
import com.github.paohaijiao.enums.JQuickUnaryOperator;
import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.expression.domain.*;
import com.github.paohaijiao.function.core.JQuickMethodFunctionProvider;
import com.github.paohaijiao.function.manager.JQuickMethodInvocationManager;
import com.github.paohaijiao.statement.JQuickRow;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 表达式求值服务
 */
public class JQuickExpressionEvaluator {

    private final JQuickMethodInvocationManager functionManager;

    public JQuickExpressionEvaluator(JQuickMethodInvocationManager functionManager) {
        this.functionManager = functionManager;
    }

    /**
     * 判断谓词条件
     */
    public boolean evaluatePredicate(JQuickRow row, JQuickExpression predicate) {
        if (predicate == null) return true;
        Object result = evaluateExpression(row, predicate);
        if (result instanceof Boolean) {
            return (Boolean) result;
        }
        return result != null;
    }

    /**
     * 表达式求值 - 核心方法
     */
    public Object evaluateExpression(JQuickRow row, JQuickExpression expr) {
        if (expr == null) return null;
        if (expr instanceof JQuickColumnRefExpression) {
            return row.get(((JQuickColumnRefExpression) expr).getColumnName());
        } else if (expr instanceof JQuickLiteralExpression) {
            return ((JQuickLiteralExpression) expr).getValue();

        } else if (expr instanceof JQuickBinaryExpression) {
            JQuickBinaryExpression binary = (JQuickBinaryExpression) expr;
            Object left = evaluateExpression(row, binary.getLeft());
            Object right = evaluateExpression(row, binary.getRight());
            return applyBinaryOperator(left, right, binary.getOperator());

        } else if (expr instanceof JQuickUnaryExpression) {
            JQuickUnaryExpression unary = (JQuickUnaryExpression) expr;
            Object value = evaluateExpression(row, unary.getExpression());
            return applyUnaryOperator(value, unary.getOperator());
        } else if (expr instanceof JQuickFunctionCallExpression) {
            JQuickFunctionCallExpression func = (JQuickFunctionCallExpression) expr;
            List<Object> args = new ArrayList<>();
            for (JQuickExpression arg : func.getArguments()) {
                args.add(evaluateExpression(row, arg));
            }
            return evaluateFunctionViaSPI(func.getFunctionName(), args);
        } else if (expr instanceof JQuickBetweenExpression) {
            JQuickBetweenExpression between = (JQuickBetweenExpression) expr;
            Object value = evaluateExpression(row, between.getExpression());
            Object low = evaluateExpression(row, between.getLow());
            Object high = evaluateExpression(row, between.getHigh());
            return evaluateBetween(value, low, high, between.isNot());
        } else if (expr instanceof JQuickInExpression) {
            JQuickInExpression in = (JQuickInExpression) expr;
            Object left = evaluateExpression(row, in.getLeft());
            boolean found = false;
            for (JQuickExpression rightExpr : in.getRightList()) {
                Object right = evaluateExpression(row, rightExpr);
                if (Objects.equals(left, right)) {
                    found = true;
                    break;
                }
            }
            return in.isNot() ? !found : found;

        } else if (expr instanceof JQuickCaseWhenExpression) {
            JQuickCaseWhenExpression caseWhen = (JQuickCaseWhenExpression) expr;
            for (int i = 0; i < caseWhen.getConditions().size(); i++) {
                Object condition = evaluateExpression(row, caseWhen.getConditions().get(i));
                if (condition instanceof Boolean && (Boolean) condition) {
                    return evaluateExpression(row, caseWhen.getResults().get(i));
                }
            }
            if (caseWhen.getElseResult() != null) {
                return evaluateExpression(row, caseWhen.getElseResult());
            }
            return null;
        }

        return null;
    }

    public Object evaluateFunctionViaSPI(String functionName, List<Object> args) {
        Collection<JQuickMethodFunctionProvider> providers = functionManager.getAllInvokers();
        for (JQuickMethodFunctionProvider provider : providers) {
            if (provider.getMethodName().equalsIgnoreCase(functionName)) {
                try {
                    return provider.invoke(args);
                } catch (Exception e) {
                    return evaluateBuiltinFunction(functionName, args);
                }
            }
        }
        return evaluateBuiltinFunction(functionName, args);
    }

    private Object evaluateBuiltinFunction(String functionName, List<Object> args) {
        functionName = functionName.toLowerCase();

        // 字符串函数
        if (functionName.equals("upper") || functionName.equals("toupper")) {
            return args.isEmpty() || args.get(0) == null ? null : args.get(0).toString().toUpperCase();
        }
        if (functionName.equals("lower") || functionName.equals("tolower")) {
            return args.isEmpty() || args.get(0) == null ? null : args.get(0).toString().toLowerCase();
        }
        if (functionName.equals("length")) {
            return args.isEmpty() || args.get(0) == null ? 0L : (long) args.get(0).toString().length();
        }
        if (functionName.equals("concat")) {
            StringBuilder sb = new StringBuilder();
            for (Object arg : args) {
                if (arg != null) sb.append(arg);
            }
            return sb.toString();
        }
        if (functionName.equals("substring") || functionName.equals("substr")) {
            if (args.size() < 2) return null;
            String str = args.get(0) == null ? "" : args.get(0).toString();
            int start = ((Number) args.get(1)).intValue() - 1;
            if (start < 0) start = 0;
            if (start >= str.length()) return "";
            if (args.size() >= 3) {
                int length = ((Number) args.get(2)).intValue();
                int end = Math.min(start + length, str.length());
                return str.substring(start, end);
            }
            return str.substring(start);
        }
        if (functionName.equals("trim")) {
            return args.isEmpty() || args.get(0) == null ? null : args.get(0).toString().trim();
        }
        if (functionName.equals("replace")) {
            if (args.size() < 3) return null;
            String str = args.get(0) == null ? "" : args.get(0).toString();
            String target = args.get(1) == null ? "" : args.get(1).toString();
            String replacement = args.get(2) == null ? "" : args.get(2).toString();
            return str.replace(target, replacement);
        }
        if (functionName.equals("split")) {
            if (args.size() < 2) return null;
            String str = args.get(0) == null ? "" : args.get(0).toString();
            String delimiter = args.get(1) == null ? "," : args.get(1).toString();
            return Arrays.asList(str.split(delimiter));
        }
        if (functionName.equals("join")) {
            if (args.size() < 2) return null;
            Collection<?> collection = args.get(0) instanceof Collection ? (Collection<?>) args.get(0) : null;
            String delimiter = args.get(1) == null ? "" : args.get(1).toString();
            if (collection == null) return "";
            return collection.stream().map(String::valueOf).collect(Collectors.joining(delimiter));
        }

        // 数学函数
        if (functionName.equals("abs")) {
            return args.isEmpty() || args.get(0) == null ? null : Math.abs(asNumber(args.get(0)).doubleValue());
        }
        if (functionName.equals("round")) {
            if (args.isEmpty() || args.get(0) == null) return null;
            double value = asNumber(args.get(0)).doubleValue();
            if (args.size() >= 2) {
                int scale = ((Number) args.get(1)).intValue();
                double factor = Math.pow(10, scale);
                return Math.round(value * factor) / factor;
            }
            return (double) Math.round(value);
        }
        if (functionName.equals("ceil")) {
            return args.isEmpty() || args.get(0) == null ? null : Math.ceil(asNumber(args.get(0)).doubleValue());
        }
        if (functionName.equals("floor")) {
            return args.isEmpty() || args.get(0) == null ? null : Math.floor(asNumber(args.get(0)).doubleValue());
        }
        if (functionName.equals("pow")) {
            if (args.size() < 2) return null;
            double base = asNumber(args.get(0)).doubleValue();
            double exponent = asNumber(args.get(1)).doubleValue();
            return Math.pow(base, exponent);
        }
        if (functionName.equals("sqrt")) {
            return args.isEmpty() || args.get(0) == null ? null : Math.sqrt(asNumber(args.get(0)).doubleValue());
        }
        if (functionName.equals("mod")) {
            if (args.size() < 2) return null;
            double a = asNumber(args.get(0)).doubleValue();
            double b = asNumber(args.get(1)).doubleValue();
            return b == 0 ? 0 : a % b;
        }
        if (functionName.equals("max") || functionName.equals("greatest")) {
            return args.stream()
                    .filter(Objects::nonNull)
                    .map(v -> asNumber(v).doubleValue())
                    .max(Double::compareTo)
                    .orElse(null);
        }
        if (functionName.equals("min") || functionName.equals("least")) {
            return args.stream()
                    .filter(Objects::nonNull)
                    .map(v -> asNumber(v).doubleValue())
                    .min(Double::compareTo)
                    .orElse(null);
        }

        // 日期函数
        if (functionName.equals("year")) {
            return extractYear(getDateValue(args));
        }
        if (functionName.equals("month")) {
            return extractMonth(getDateValue(args));
        }
        if (functionName.equals("day")) {
            return extractDay(getDateValue(args));
        }
        if (functionName.equals("now")) {
            return LocalDateTime.now();
        }
        if (functionName.equals("current_date") || functionName.equals("today")) {
            return LocalDate.now();
        }
        if (functionName.equals("add_days") || functionName.equals("addDays")) {
            if (args.size() < 2) return null;
            Object date = args.get(0);
            int days = ((Number) args.get(1)).intValue();
            if (date instanceof LocalDate) return ((LocalDate) date).plusDays(days);
            if (date instanceof LocalDateTime) return ((LocalDateTime) date).plusDays(days);
            if (date instanceof Date) {
                Calendar cal = Calendar.getInstance();
                cal.setTime((Date) date);
                cal.add(Calendar.DAY_OF_MONTH, days);
                return cal.getTime();
            }
            return null;
        }

        // 条件函数
        if (functionName.equals("if")) {
            if (args.size() < 3) return null;
            Boolean condition = args.get(0) instanceof Boolean ? (Boolean) args.get(0) : false;
            return condition ? args.get(1) : args.get(2);
        }
        if (functionName.equals("coalesce") || functionName.equals("nvl")) {
            for (Object arg : args) {
                if (arg != null) return arg;
            }
            return null;
        }
        if (functionName.equals("between")) {
            if (args.size() < 3) return false;
            double value = asNumber(args.get(0)).doubleValue();
            double min = asNumber(args.get(1)).doubleValue();
            double max = asNumber(args.get(2)).doubleValue();
            boolean inclusive = args.size() >= 4 && args.get(3) instanceof Boolean ? (Boolean) args.get(3) : true;
            return inclusive ? value >= min && value <= max : value > min && value < max;
        }

        // 类型转换函数
        if (functionName.equals("to_int") || functionName.equals("toInt")) {
            if (args.isEmpty()) return null;
            Number num = asNumber(args.get(0));
            return num != null ? num.intValue() : null;
        }
        if (functionName.equals("to_long") || functionName.equals("toLong")) {
            if (args.isEmpty()) return null;
            Number num = asNumber(args.get(0));
            return num != null ? num.longValue() : null;
        }
        if (functionName.equals("to_double") || functionName.equals("toDouble")) {
            if (args.isEmpty()) return null;
            Number num = asNumber(args.get(0));
            return num != null ? num.doubleValue() : null;
        }
        if (functionName.equals("to_string") || functionName.equals("toString")) {
            if (args.isEmpty()) return null;
            return args.get(0) != null ? args.get(0).toString() : null;
        }
        if (functionName.equals("to_boolean") || functionName.equals("toBoolean")) {
            if (args.isEmpty()) return null;
            Object val = args.get(0);
            if (val instanceof Boolean) return val;
            if (val instanceof String) {
                String str = ((String) val).toLowerCase();
                return str.equals("true") || str.equals("yes") || str.equals("1");
            }
            if (val instanceof Number) return ((Number) val).doubleValue() != 0;
            return false;
        }
        if (functionName.equals("cast")) {
            if (args.size() < 2) return args.isEmpty() ? null : args.get(0);
            Object value = args.get(0);
            String targetType = args.get(1) == null ? "string" : args.get(1).toString().toLowerCase();
            try {
                switch (targetType) {
                    case "int":
                    case "integer":
                        return asNumber(value).intValue();
                    case "long":
                        return asNumber(value).longValue();
                    case "double":
                        return asNumber(value).doubleValue();
                    case "string":
                    case "varchar":
                        return value != null ? value.toString() : null;
                    case "boolean":
                        if (value instanceof Boolean) return value;
                        if (value instanceof String) return Boolean.parseBoolean((String) value);
                        return value != null;
                    default:
                        return value;
                }
            } catch (Exception e) {
                return null;
            }
        }

        return null;
    }

    private Object getDateValue(List<Object> args) {
        if (args.isEmpty()) return null;
        Object val = args.get(0);
        if (val instanceof Date) return val;
        if (val instanceof LocalDate) return val;
        if (val instanceof LocalDateTime) return val;
        if (val instanceof String) {
            try {
                return LocalDate.parse((String) val);
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    private Integer extractYear(Object date) {
        if (date == null) return null;
        if (date instanceof Date) {
            Calendar cal = Calendar.getInstance();
            cal.setTime((Date) date);
            return cal.get(Calendar.YEAR);
        }
        if (date instanceof LocalDate) return ((LocalDate) date).getYear();
        if (date instanceof LocalDateTime) return ((LocalDateTime) date).getYear();
        return null;
    }

    private Integer extractMonth(Object date) {
        if (date == null) return null;
        if (date instanceof Date) {
            Calendar cal = Calendar.getInstance();
            cal.setTime((Date) date);
            return cal.get(Calendar.MONTH) + 1;
        }
        if (date instanceof LocalDate) return ((LocalDate) date).getMonthValue();
        if (date instanceof LocalDateTime) return ((LocalDateTime) date).getMonthValue();
        return null;
    }

    private Integer extractDay(Object date) {
        if (date == null) return null;
        if (date instanceof Date) {
            Calendar cal = Calendar.getInstance();
            cal.setTime((Date) date);
            return cal.get(Calendar.DAY_OF_MONTH);
        }
        if (date instanceof LocalDate) return ((LocalDate) date).getDayOfMonth();
        if (date instanceof LocalDateTime) return ((LocalDateTime) date).getDayOfMonth();
        return null;
    }

    private Object applyBinaryOperator(Object left, Object right, JQuickBinaryOperator operator) {
        if (left == null || right == null) {
            if (isComparisonOperator(operator)) return null;
            return false;
        }

        switch (operator) {
            case EQ:
                return Objects.equals(left, right);
            case NE:
                return !Objects.equals(left, right);
            case GT:
                return compareValues(left, right, false) > 0;
            case LT:
                return compareValues(left, right, false) < 0;
            case GE:
                return compareValues(left, right, false) >= 0;
            case LE:
                return compareValues(left, right, false) <= 0;
            case AND:
                return (left instanceof Boolean && (Boolean) left) && (right instanceof Boolean && (Boolean) right);
            case OR:
                return (left instanceof Boolean && (Boolean) left) || (right instanceof Boolean && (Boolean) right);
            case PLUS:
                return asNumber(left).doubleValue() + asNumber(right).doubleValue();
            case MINUS:
                return asNumber(left).doubleValue() - asNumber(right).doubleValue();
            case MULTIPLY:
                return asNumber(left).doubleValue() * asNumber(right).doubleValue();
            case DIVIDE:
                double divisor = asNumber(right).doubleValue();
                return divisor == 0 ? null : asNumber(left).doubleValue() / divisor;
            case MODULO:
                double modDivisor = asNumber(right).doubleValue();
                return modDivisor == 0 ? null : asNumber(left).doubleValue() % modDivisor;
            case LIKE:
                return likeMatch(left.toString(), right.toString());
            case NOT_LIKE:
                return !likeMatch(left.toString(), right.toString());
            default:
                return false;
        }
    }

    private boolean isComparisonOperator(JQuickBinaryOperator operator) {
        return operator == JQuickBinaryOperator.EQ || operator == JQuickBinaryOperator.NE ||
                operator == JQuickBinaryOperator.GT || operator == JQuickBinaryOperator.LT ||
                operator == JQuickBinaryOperator.GE || operator == JQuickBinaryOperator.LE ||
                operator == JQuickBinaryOperator.LIKE || operator == JQuickBinaryOperator.NOT_LIKE;
    }

    private Object applyUnaryOperator(Object value, JQuickUnaryOperator operator) {
        switch (operator) {
            case NOT:
                return value instanceof Boolean ? !(Boolean) value : null;
            case PLUS:
                return value instanceof Number ? asNumber(value).doubleValue() : null;
            case MINUS:
                return value instanceof Number ? -asNumber(value).doubleValue() : null;
            case IS_NULL:
                return value == null;
            case IS_NOT_NULL:
                return value != null;
            default:
                return null;
        }
    }

    private Object evaluateBetween(Object value, Object low, Object high, boolean isNot) {
        if (value == null || low == null || high == null) return null;
        boolean result = compareValues(value, low, false) >= 0 && compareValues(value, high, false) <= 0;
        return isNot ? !result : result;
    }

    private boolean likeMatch(String value, String pattern) {
        if (value == null || pattern == null) return false;
        String regex = pattern.replace("%", ".*").replace("_", ".");
        return value.matches(regex);
    }

    private Number asNumber(Object value) {
        if (value instanceof Number) return (Number) value;
        if (value instanceof String) {
            try {
                return Double.parseDouble((String) value);
            } catch (NumberFormatException e) {
                return 0.0;
            }
        }
        if (value instanceof Boolean) return ((Boolean) value) ? 1.0 : 0.0;
        return 0.0;
    }

    private int compareValues(Object v1, Object v2, boolean nullsFirst) {
        if (v1 == null && v2 == null) return 0;
        if (v1 == null) return nullsFirst ? -1 : 1;
        if (v2 == null) return nullsFirst ? 1 : -1;
        try {
            double d1 = asNumber(v1).doubleValue();
            double d2 = asNumber(v2).doubleValue();
            return Double.compare(d1, d2);
        } catch (Exception e) {
            // 回退到字符串比较
        }
        if (v1 instanceof Comparable && v2 instanceof Comparable) {
            @SuppressWarnings("unchecked")
            int cmp = ((Comparable<Object>) v1).compareTo(v2);
            return cmp;
        }
        return v1.toString().compareTo(v2.toString());
    }
}
