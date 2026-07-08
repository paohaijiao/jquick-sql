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
package com.github.paohaijiao.distributed.worker;

import com.github.paohaijiao.console.JConsole;
import com.github.paohaijiao.enums.JQuickBinaryOperator;
import com.github.paohaijiao.enums.JQuickUnaryOperator;
import com.github.paohaijiao.exception.JAssert;
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

    private final JConsole console=JConsole.initConsoleEnvironment();

    private final JQuickMethodInvocationManager functionManager;

    private Map<String, String> aliasToTableMap = new HashMap<>();

    private Map<String, String> columnAliasMap = new HashMap<>();

    /**
     * 设置别名上下文（在 JOIN 时使用）
     */
    public void setAliasContext(Map<String, String> aliasToTable, Map<String, String> columnAliasToActual) {
        this.aliasToTableMap = aliasToTable != null ? aliasToTable : new HashMap<>();
        this.columnAliasMap = columnAliasToActual != null ? columnAliasToActual : new HashMap<>();
    }

    /**
     * 清除别名上下文
     */
    public void clearAliasContext() {
        this.aliasToTableMap.clear();
        this.columnAliasMap.clear();
    }

    /**
     * 解析带别名的列名
     */
    private String resolveColumnName(String columnName) {
        if (columnAliasMap.containsKey(columnName)) {
            return columnAliasMap.get(columnName);
        }
        if (columnName.contains(".")) {
            String[] parts = columnName.split("\\.", 2);
            String alias = parts[0];
            String actualColumn = parts[1];
            if (aliasToTableMap.containsKey(alias)) {
                return actualColumn;
            }
            return columnName;
        }
        return columnName;
    }

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
            String rawColumnName = ((JQuickColumnRefExpression) expr).getColumnName();
            String resolvedColumnName = resolveColumnName(rawColumnName);
            Object value = row.get(rawColumnName);
            if (value != null || row.containsKey(rawColumnName)) {
                return value;
            }
            value = row.get(resolvedColumnName);
            if (value != null || row.containsKey(resolvedColumnName)) {
                return value;
            }
            for (Map.Entry<String, Object> entry : row.entrySet()) {
                if (entry.getKey().equalsIgnoreCase(rawColumnName) || entry.getKey().equalsIgnoreCase(resolvedColumnName)) {
                    return entry.getValue();
                }
            }
            return row.get(((JQuickColumnRefExpression) expr).getColumnName());
        } else if (expr instanceof JQuickLiteralExpression) {
            return ((JQuickLiteralExpression) expr).getValue();
        } else if (expr instanceof JQuickBinaryExpression) {
            JQuickBinaryExpression binary = (JQuickBinaryExpression) expr;
            Object left = evaluateExpression(row, binary.getLeft());
            Object right = evaluateExpression(row, binary.getRight());
            if ((binary.getOperator() == com.github.paohaijiao.enums.JQuickBinaryOperator.EQ ||
                 binary.getOperator() == com.github.paohaijiao.enums.JQuickBinaryOperator.NE ||
                 binary.getOperator() == com.github.paohaijiao.enums.JQuickBinaryOperator.LT ||
                 binary.getOperator() == com.github.paohaijiao.enums.JQuickBinaryOperator.LE ||
                 binary.getOperator() == com.github.paohaijiao.enums.JQuickBinaryOperator.GT ||
                 binary.getOperator() == com.github.paohaijiao.enums.JQuickBinaryOperator.GE) &&
                binary.getLeft() instanceof JQuickColumnRefExpression &&
                binary.getRight() instanceof JQuickColumnRefExpression) {
                String leftCol = ((JQuickColumnRefExpression) binary.getLeft()).getColumnName();
                String rightCol = ((JQuickColumnRefExpression) binary.getRight()).getColumnName();
                // 如果两个列名相同且不带前缀，尝试分别解析到左右表的值
                if (leftCol.equals(rightCol) && !leftCol.contains(".")) {
                    Object leftVal = row.get("left." + leftCol);
                    Object rightVal = row.get("right." + rightCol);
                    // 如果左右表都有这个列，则使用左右表的值进行比较
                    if (leftVal != null || row.containsKey("left." + leftCol)) {
                        left = leftVal;
                    }
                    if (rightVal != null || row.containsKey("right." + rightCol)) {
                        right = rightVal;
                    }
                }
            }
            
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
            return evaluateFunction(func.getFunctionName(), args);
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

    public Object evaluateFunction(String functionName, List<Object> args) {
        Collection<JQuickMethodFunctionProvider> providers = functionManager.getAllInvokers();
        for (JQuickMethodFunctionProvider provider : providers) {
            if (provider.getMethodName().equalsIgnoreCase(functionName)) {
                try {
                    return provider.invoke(args);
                } catch (Exception e) {
                    console.error("evaluefunction occurred exception",e);
                }
            }
        }
        return evaluateBuiltinFunction(functionName, args);
    }

    private Object evaluateBuiltinFunction(String functionName, List<Object> args) {
        String name = functionName.toLowerCase();
        JAssert.notNull(functionName, "the function name  require not  null");
        Optional<JQuickMethodFunctionProvider> provider = functionManager.getInvoker(name);
        boolean exists=provider.isPresent();
        JAssert.isTrue(exists, "the function name  not exists");
        return provider.get().invoke(args);
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
        boolean match= value.matches(regex);
        return match;
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
