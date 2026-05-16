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
package com.github.paohaijiao.expression.domain;

import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.spi.JQuickFunctionProvider;
import com.github.paohaijiao.spi.SpiLoader;
import com.github.paohaijiao.statement.JQuickRow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 函数调用表达式 - 调用内置函数或自定义函数
 */
public class JQuickFunctionCallExpression implements JQuickExpression {

    private final String functionName;
    private final List<JQuickExpression> arguments;
    private final boolean isStarArg;
    private JQuickFunctionProvider functionProvider;

    private static final Map<String, JQuickFunctionProvider> FUNCTION_CACHE = new ConcurrentHashMap<>();

    public JQuickFunctionCallExpression(String functionName, List<JQuickExpression> arguments) {
        this(functionName, arguments, false);
    }

    public JQuickFunctionCallExpression(String functionName, List<JQuickExpression> arguments, boolean isStarArg) {
        this.functionName = functionName.toLowerCase();
        this.arguments = arguments != null ? arguments : Collections.emptyList();
        this.isStarArg = isStarArg;
        this.functionProvider = loadFunctionProvider(this.functionName);
    }

    private JQuickFunctionProvider loadFunctionProvider(String name) {
        if (FUNCTION_CACHE.containsKey(name)) {
            return FUNCTION_CACHE.get(name);
        }

        try {
            List<JQuickFunctionProvider> providers = SpiLoader.loadAll(JQuickFunctionProvider.class);
            for (JQuickFunctionProvider provider : providers) {
                if (provider.getFunctionName().equalsIgnoreCase(name)) {
                    FUNCTION_CACHE.put(name, provider);
                    return provider;
                }
            }
        } catch (Exception e) {
            // SPI加载失败，使用内置函数
            return null;
        }

        return null;
    }

    @Override
    public Object evaluate(JQuickRow row) {
        List<Object> evaluatedArgs = new ArrayList<>();

        if (isStarArg) {
            // COUNT(*) 特殊处理
            evaluatedArgs.add(1L);
        } else {
            for (JQuickExpression arg : arguments) {
                evaluatedArgs.add(arg.evaluate(row));
            }
        }

        if (functionProvider != null) {
            return functionProvider.invoke(evaluatedArgs);
        }

        // 内置函数处理
        return evaluateBuiltin(row);
    }

    private Object evaluateBuiltin(JQuickRow row) {
        switch (functionName) {
            // 字符串函数
            case "upper":
                return upper();
            case "lower":
                return lower();
            case "length":
                return length();
            case "concat":
                return concat();
            case "substring":
                return substring();
            case "trim":
                return trim();
            case "replace":
                return replace();

            // 数学函数
            case "abs":
                return abs();
            case "round":
                return round();
            case "ceil":
                return ceil();
            case "floor":
                return floor();
            case "pow":
                return pow();
            case "sqrt":
                return sqrt();

            // 日期函数
            case "year":
                return year();
            case "month":
                return month();
            case "day":
                return day();
            case "now":
                return new java.util.Date();
            case "current_date":
                return java.time.LocalDate.now();

            // 聚合函数（在 GroupByNode 中处理）
            case "count":
            case "sum":
            case "avg":
            case "max":
            case "min":
                return null;

            // 类型转换
            case "cast":
                return cast();

            default:
                return null;
        }
    }

    private String upper() {
        if (arguments.isEmpty()) return null;
        Object val = arguments.get(0).evaluate(null);
        return val != null ? val.toString().toUpperCase() : null;
    }

    private String lower() {
        if (arguments.isEmpty()) return null;
        Object val = arguments.get(0).evaluate(null);
        return val != null ? val.toString().toLowerCase() : null;
    }

    private Long length() {
        if (arguments.isEmpty()) return null;
        Object val = arguments.get(0).evaluate(null);
        return val != null ? (long) val.toString().length() : 0L;
    }

    private String concat() {
        StringBuilder sb = new StringBuilder();
        for (JQuickExpression arg : arguments) {
            Object val = arg.evaluate(null);
            if (val != null) {
                sb.append(val.toString());
            }
        }
        return sb.toString();
    }

    private String substring() {
        if (arguments.size() < 2) return null;
        Object str = arguments.get(0).evaluate(null);
        Object start = arguments.get(1).evaluate(null);

        if (str == null || start == null) return null;

        String s = str.toString();
        int startIdx = ((Number) start).intValue() - 1; // SQL索引从1开始

        if (startIdx < 0) startIdx = 0;
        if (startIdx >= s.length()) return "";

        if (arguments.size() >= 3) {
            Object length = arguments.get(2).evaluate(null);
            if (length != null) {
                int len = ((Number) length).intValue();
                int endIdx = Math.min(startIdx + len, s.length());
                return s.substring(startIdx, endIdx);
            }
        }

        return s.substring(startIdx);
    }

    private String trim() {
        if (arguments.isEmpty()) return null;
        Object val = arguments.get(0).evaluate(null);
        return val != null ? val.toString().trim() : null;
    }

    private String replace() {
        if (arguments.size() < 3) return null;
        Object str = arguments.get(0).evaluate(null);
        Object search = arguments.get(1).evaluate(null);
        Object replacement = arguments.get(2).evaluate(null);

        if (str == null || search == null || replacement == null) return null;

        return str.toString().replace(search.toString(), replacement.toString());
    }

    private Double abs() {
        if (arguments.isEmpty()) return null;
        Object val = arguments.get(0).evaluate(null);
        if (val instanceof Number) {
            return Math.abs(((Number) val).doubleValue());
        }
        return null;
    }

    private Double round() {
        if (arguments.isEmpty()) return null;
        Object val = arguments.get(0).evaluate(null);
        if (val instanceof Number) {
            double d = ((Number) val).doubleValue();
            if (arguments.size() >= 2) {
                Object scale = arguments.get(1).evaluate(null);
                if (scale instanceof Number) {
                    double factor = Math.pow(10, ((Number) scale).intValue());
                    return Math.round(d * factor) / factor;
                }
            }
            return (double) Math.round(d);
        }
        return null;
    }

    private Double ceil() {
        if (arguments.isEmpty()) return null;
        Object val = arguments.get(0).evaluate(null);
        if (val instanceof Number) {
            return Math.ceil(((Number) val).doubleValue());
        }
        return null;
    }

    private Double floor() {
        if (arguments.isEmpty()) return null;
        Object val = arguments.get(0).evaluate(null);
        if (val instanceof Number) {
            return Math.floor(((Number) val).doubleValue());
        }
        return null;
    }

    private Double pow() {
        if (arguments.size() < 2) return null;
        Object base = arguments.get(0).evaluate(null);
        Object exponent = arguments.get(1).evaluate(null);
        if (base instanceof Number && exponent instanceof Number) {
            return Math.pow(((Number) base).doubleValue(), ((Number) exponent).doubleValue());
        }
        return null;
    }

    private Double sqrt() {
        if (arguments.isEmpty()) return null;
        Object val = arguments.get(0).evaluate(null);
        if (val instanceof Number) {
            return Math.sqrt(((Number) val).doubleValue());
        }
        return null;
    }

    private Integer year() {
        Object val = getDateValue();
        if (val instanceof java.util.Date) {
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTime((java.util.Date) val);
            return cal.get(java.util.Calendar.YEAR);
        }
        if (val instanceof java.time.LocalDate) {
            return ((java.time.LocalDate) val).getYear();
        }
        return null;
    }

    private Integer month() {
        Object val = getDateValue();
        if (val instanceof java.util.Date) {
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTime((java.util.Date) val);
            return cal.get(java.util.Calendar.MONTH) + 1;
        }
        if (val instanceof java.time.LocalDate) {
            return ((java.time.LocalDate) val).getMonthValue();
        }
        return null;
    }

    private Integer day() {
        Object val = getDateValue();
        if (val instanceof java.util.Date) {
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTime((java.util.Date) val);
            return cal.get(java.util.Calendar.DAY_OF_MONTH);
        }
        if (val instanceof java.time.LocalDate) {
            return ((java.time.LocalDate) val).getDayOfMonth();
        }
        return null;
    }

    private Object getDateValue() {
        if (arguments.isEmpty()) return null;
        return arguments.get(0).evaluate(null);
    }

    private Object cast() {
        if (arguments.size() < 2) return null;
        Object value = arguments.get(0).evaluate(null);
        Object targetType = arguments.get(1).evaluate(null);

        if (value == null || targetType == null) return null;

        String typeName = targetType.toString().toLowerCase();
        try {
            switch (typeName) {
                case "int":
                case "integer":
                    if (value instanceof Number) return ((Number) value).intValue();
                    return Integer.parseInt(value.toString());
                case "long":
                    if (value instanceof Number) return ((Number) value).longValue();
                    return Long.parseLong(value.toString());
                case "double":
                    if (value instanceof Number) return ((Number) value).doubleValue();
                    return Double.parseDouble(value.toString());
                case "string":
                case "varchar":
                    return value.toString();
                case "boolean":
                    if (value instanceof Boolean) return value;
                    return Boolean.parseBoolean(value.toString());
                default:
                    return value;
            }
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public Class<?> getType() {
        switch (functionName) {
            case "upper": case "lower": case "concat": case "trim":
            case "replace": case "substring": case "cast":
                return String.class;
            case "length": case "year": case "month": case "day":
                return Long.class;
            case "abs": case "round": case "ceil": case "floor":
            case "pow": case "sqrt":
                return Double.class;
            default:
                return Object.class;
        }
    }

    @Override
    public boolean isConstant() {
        return arguments.stream().allMatch(JQuickExpression::isConstant);
    }

    @Override
    public List<String> getReferencedColumns() {
        List<String> columns = new ArrayList<>();
        for (JQuickExpression arg : arguments) {
            columns.addAll(arg.getReferencedColumns());
        }
        return columns;
    }

    @Override
    public String toSql() {
        StringBuilder sb = new StringBuilder();
        sb.append(functionName).append("(");

        if (isStarArg) {
            sb.append("*");
        } else {
            for (int i = 0; i < arguments.size(); i++) {
                if (i > 0) sb.append(", ");
                sb.append(arguments.get(i).toSql());
            }
        }

        sb.append(")");
        return sb.toString();
    }

    @Override
    public JQuickExpression clone() {
        List<JQuickExpression> clonedArgs = new ArrayList<>();
        for (JQuickExpression arg : arguments) {
            clonedArgs.add(arg.clone());
        }
        return new JQuickFunctionCallExpression(functionName, clonedArgs, isStarArg);
    }

    public String getFunctionName() { return functionName; }
    public List<JQuickExpression> getArguments() { return arguments; }
    public boolean isStarArg() { return isStarArg; }
}
