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
package com.github.paohaijiao.function;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * packageName com.github.paohaijiao.function
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/8/17
 */
public class JAggregateFunctionFactory {

    public static final String SUM = "SUM";
    public static final String AVG = "AVG";
    public static final String MAX = "MAX";
    public static final String MIN = "MIN";
    public static final String COUNT = "COUNT";
    public static final String FIRST = "FIRST";
    public static final String LAST = "LAST";
    public static final String STDDEV = "STDDEV";
    public static final String VARIANCE = "VARIANCE";
    public static final String MEDIAN = "MEDIAN";
    public static final String MODE = "MODE";
    public static final String QUARTILE1 = "QUARTILE1";
    public static final String QUARTILE3 = "QUARTILE3";
    public static final String RANGE = "RANGE";
    public static final String PRODUCT = "PRODUCT";
    public static final String CONCAT = "CONCAT";
    public static final String DISTINCT_COUNT = "DISTINCT_COUNT";
    private static final Map<String, Function<List<Object>, Object>> FUNCTION_REGISTRY = new ConcurrentHashMap<>();

    static {
        registerBuiltInFunctions();
    }

    private static void registerBuiltInFunctions() {
        registerFunction(SUM, values ->
                values.stream().filter(Objects::nonNull)
                        .mapToDouble(v -> ((Number) v).doubleValue())
                        .sum());
        registerFunction(AVG, values ->
                values.stream().filter(Objects::nonNull)
                        .mapToDouble(v -> ((Number) v).doubleValue())
                        .average().orElse(0));
        registerFunction(MAX, values ->
                values.stream().filter(Objects::nonNull)
                        .map(v -> (Comparable) v)
                        .max(Comparator.naturalOrder())
                        .orElse(null));

        registerFunction(MIN, values ->
                values.stream().filter(Objects::nonNull)
                        .map(v -> (Comparable) v)
                        .min(Comparator.naturalOrder())
                        .orElse(null));
        registerFunction(COUNT, List::size);
        registerFunction(FIRST, values ->
                values.isEmpty() ? null : values.get(0));
        registerFunction(LAST, values ->
                values.isEmpty() ? null : values.get(values.size() - 1));
        registerFunction(STDDEV, values -> {
            DoubleSummaryStatistics stats = values.stream()
                    .filter(Objects::nonNull)
                    .mapToDouble(v -> ((Number) v).doubleValue())
                    .summaryStatistics();

            if (stats.getCount() == 0) return Double.NaN;
            double avg = stats.getAverage();
            double sum = values.stream()
                    .filter(Objects::nonNull)
                    .mapToDouble(v -> Math.pow(((Number) v).doubleValue() - avg, 2))
                    .sum();
            return Math.sqrt(sum / stats.getCount());
        });

        registerFunction(VARIANCE, values -> {
            DoubleSummaryStatistics stats = values.stream()
                    .filter(Objects::nonNull)
                    .mapToDouble(v -> ((Number) v).doubleValue())
                    .summaryStatistics();

            if (stats.getCount() == 0) return Double.NaN;
            double avg = stats.getAverage();
            return values.stream()
                    .filter(Objects::nonNull)
                    .mapToDouble(v -> Math.pow(((Number) v).doubleValue() - avg, 2))
                    .sum() / stats.getCount();
        });

        registerFunction(MEDIAN, values -> {
            List<Double> sorted = values.stream()
                    .filter(Objects::nonNull)
                    .map(v -> ((Number) v).doubleValue())
                    .sorted()
                    .collect(Collectors.toList());

            if (sorted.isEmpty()) return Double.NaN;
            int size = sorted.size();
            if (size % 2 == 1) {
                return sorted.get(size / 2);
            } else {
                return (sorted.get(size / 2 - 1) + sorted.get(size / 2)) / 2.0;
            }
        });

        registerFunction(MODE, values -> {
            Map<Object, Long> frequencyMap = values.stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

            return frequencyMap.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse(null);
        });

        registerFunction(QUARTILE1, values -> calculatePercentile(values, 0.25));
        registerFunction(QUARTILE3, values -> calculatePercentile(values, 0.75));

        registerFunction(RANGE, values -> {
            DoubleSummaryStatistics stats = values.stream()
                    .filter(Objects::nonNull)
                    .mapToDouble(v -> ((Number) v).doubleValue())
                    .summaryStatistics();
            return stats.getMax() - stats.getMin();
        });
        registerFunction(PRODUCT, values ->
                values.stream().filter(Objects::nonNull)
                        .mapToDouble(v -> ((Number) v).doubleValue())
                        .reduce(1.0, (a, b) -> a * b));

        registerFunction(CONCAT, values ->
                values.stream().filter(Objects::nonNull)
                        .map(Object::toString)
                        .collect(Collectors.joining(", ")));
        registerFunction(DISTINCT_COUNT, values ->
                values.stream().filter(Objects::nonNull).distinct().count());
    }

    private static Double calculatePercentile(List<Object> values, double percentile) {
        List<Double> sorted = values.stream()
                .filter(Objects::nonNull)
                .map(v -> ((Number) v).doubleValue())
                .sorted()
                .collect(Collectors.toList());
        if (sorted.isEmpty()) return Double.NaN;
        double index = percentile * (sorted.size() - 1);
        int lower = (int) Math.floor(index);
        int upper = (int) Math.ceil(index);
        if (lower == upper) {
            return sorted.get(lower);
        }
        double weight = index - lower;
        return sorted.get(lower) * (1 - weight) + sorted.get(upper) * weight;
    }

    /**
     * registerFunction
     *
     * @param name     name
     * @param function function
     */
    public static void registerFunction(String name, Function<List<Object>, Object> function) {
        if (name == null || function == null) {
            throw new IllegalArgumentException("function name and implementation cannot be null");
        }
        FUNCTION_REGISTRY.put(name.toUpperCase(), function);
    }

    /**
     * getFunction
     *
     * @param name NAME
     * @return Function
     * @throws IllegalArgumentException Exception
     */
    public static Function<List<Object>, Object> getFunction(String name) {
        for (String method : FUNCTION_REGISTRY.keySet()) {
            if (method.equalsIgnoreCase(name)) {
                return FUNCTION_REGISTRY.get(method);
            }
        }
        throw new IllegalArgumentException("unknown aggregate function: " + name);
    }

    /**
     * containsFunction
     *
     * @param name name
     * @return exists or not
     */
    public static boolean containsFunction(String name) {
        for (String method : FUNCTION_REGISTRY.keySet()) {
            if (method.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * getRegisteredFunctions
     *
     * @return function set
     */
    public static Set<String> getRegisteredFunctions() {
        return Collections.unmodifiableSet(FUNCTION_REGISTRY.keySet());
    }
}
