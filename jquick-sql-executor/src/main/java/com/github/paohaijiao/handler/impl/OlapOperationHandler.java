///*
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// *
// * Copyright (c) [2025-2099] Martin (goudingcheng@gmail.com)
// */
//package com.github.paohaijiao.handler.impl;
//
//import com.github.paohaijiao.entity.DiceCondition;
//import com.github.paohaijiao.entity.PivotSpec;
//import com.github.paohaijiao.entity.SliceCondition;
//import com.github.paohaijiao.enums.AggregateFunctionType;
//import com.github.paohaijiao.expression.ColumnExpression;
//import com.github.paohaijiao.expression.Expression;
//import com.github.paohaijiao.function.AggregateFunction;
//import com.github.paohaijiao.query.QueryPlan;
//import com.github.paohaijiao.util.EntityAccessor;
//
//import java.util.ArrayList;
//import java.util.Comparator;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
///**
// * packageName com.github.paohaijiao.handler.impl
// *
// * @author Martin
// * @version 1.0.0
// * @since 2025/8/12
// */
//public class OlapOperationHandler <T>{
//    private final EntityAccessor<T> accessor;
//
//    public OlapOperationHandler(Class<T> entityClass) {
//        this.accessor = new EntityAccessor<>(entityClass);
//    }
//    public List<T> handleOlapOperation(List<T> data, QueryPlan plan) {
//        switch (plan.getOlapOperationType()) {
//            case ROLLUP:
//                return processRollup(data, plan.getGroupBy());
//            case DRILLDOWN:
//                return processDrillDown(data, plan.getGroupBy(),
//                        plan.getDrillDownDimensions());
//            case SLICE:
//                return processSlice(data, plan.getSliceCondition());
//            case DICE:
//                return processDice(data, plan.getDiceConditions());
//            case PIVOT:
//                return processPivot(data, plan.getPivotSpec());
//            default:
//                return data;
//        }
//    }
//    private List<T> processRollup(List<T> data, List<Expression> groupByItems) {
//        if (groupByItems == null || groupByItems.isEmpty()) {
//            return data;
//        }
//
//        List<String> dimensions = groupByItems.stream()
//                .map(item -> ((ColumnExpression)item.getExpression()).getColumnName())
//                .collect(Collectors.toList());
//
//        List<T> result = new ArrayList<>(data);
//        for (int i = dimensions.size() - 1; i >= 0; i--) {
//            List<String> currentLevelDims = dimensions.subList(0, i);
//            if (!currentLevelDims.isEmpty()) {
//                Map<List<Object>, List<T>> groups = groupData(data, currentLevelDims);
//
//                for (List<T> group : groups.values()) {
//                    T subtotal = createSubtotalEntity(group, currentLevelDims);
//                    // 标记为小计行
//                    setRollupLevel(subtotal, currentLevelDims.size());
//                    result.add(subtotal);
//                }
//            }
//        }
//
//        // 添加总计
//        T grandTotal = createGrandTotalEntity(data);
//        setRollupLevel(grandTotal, 0); // 0表示总计
//        result.add(grandTotal);
//        return result;
//    }
//    private void setRollupLevel(T entity, int level) {
//        try {
//            accessor.setValue(entity, "__rollup_level", level);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//    private List<T> processDrillDown(List<T> data,
//                                     List<Expression> baseGroupBy,
//                                     List<String> drillDimensions) {
//        if (baseGroupBy == null || baseGroupBy.isEmpty() ||
//                drillDimensions == null || drillDimensions.isEmpty()) {
//            return data;
//        }
//
//        List<String> baseDimensions = baseGroupBy.stream()
//                .map(item -> ((ColumnExpression)item.getExpression()).getColumnName())
//                .collect(Collectors.toList());
//
//        // 先按基础维度分组
//        Map<List<Object>, List<T>> baseGroups = groupData(data, baseDimensions);
//
//        List<T> result = new ArrayList<>();
//
//        for (Map.Entry<List<Object>, List<T>> entry : baseGroups.entrySet()) {
//            // 1. 添加基础汇总行
//            T summaryRow = createSummaryEntity(entry.getValue(), baseDimensions);
//            setDrillLevel(summaryRow, 1); // 1表示基础汇总级别
//            result.add(summaryRow);
//
//            // 2. 添加下钻维度的详细分组
//            Map<List<Object>, List<T>> drillGroups = groupData(entry.getValue(), drillDimensions);
//
//            for (Map.Entry<List<Object>, List<T>> drillEntry : drillGroups.entrySet()) {
//                T drillRow = createDrillEntity(drillEntry.getValue(),
//                        baseDimensions, entry.getKey(),
//                        drillDimensions, drillEntry.getKey());
//                setDrillLevel(drillRow, 2); // 2表示下钻级别
//                result.add(drillRow);
//            }
//        }
//
//        return result;
//    }
//
//    private T createDrillEntity(List<T> groupData,
//                                List<String> baseDimensions,
//                                List<Object> baseKeys,
//                                List<String> drillDimensions,
//                                List<Object> drillKeys) {
//        T entity = createNewEntity();
//        for (int i = 0; i < baseDimensions.size(); i++) {
//            accessor.setValue(entity, baseDimensions.get(i), baseKeys.get(i));
//        }
//        for (int i = 0; i < drillDimensions.size(); i++) {
//            accessor.setValue(entity, drillDimensions.get(i), drillKeys.get(i));
//        }
//        calculateMeasures(entity, groupData);
//
//        return entity;
//    }
//    private List<T> processSlice(List<T> data, SliceCondition condition) {
//        if (condition == null) {
//            return data;
//        }
//
//        return data.stream()
//                .filter(entity -> {
//                    Object value = accessor.getValue(entity, condition.getDimension());
//                    return condition.getValue().equals(value);
//                })
//                .collect(Collectors.toList());
//    }
//    private List<T> processDice(List<T> data, List<DiceCondition> conditions) {
//        if (conditions == null || conditions.isEmpty()) {
//            return data;
//        }
//
//        return data.stream()
//                .filter(entity -> {
//                    for (DiceCondition cond : conditions) {
//                        Object value = accessor.getValue(entity, cond.getDimension());
//                        if (!evaluateDiceCondition(value, cond)) {
//                            return false;
//                        }
//                    }
//                    return true;
//                })
//                .collect(Collectors.toList());
//    }
//
//    // 评估切块条件
//    private boolean evaluateDiceCondition(Object value, DiceCondition condition) {
//        switch (condition.getOperator()) {
//            case "=": return value.equals(condition.getValue());
//            case ">": return compare(value, condition.getValue()) > 0;
//            case "<": return compare(value, condition.getValue()) < 0;
//            case ">=": return compare(value, condition.getValue()) >= 0;
//            case "<=": return compare(value, condition.getValue()) <= 0;
//            case "<>": return !value.equals(condition.getValue());
//            default: throw new IllegalArgumentException("Unsupported operator: " + condition.getOperator());
//        }
//    }
//
//    @SuppressWarnings("unchecked")
//    private int compare(Object a, Object b) {
//        if (a == null || b == null) return 0;
//        if (a instanceof Comparable && b instanceof Comparable) {
//            return ((Comparable<Object>)a).compareTo(b);
//        }
//        return a.toString().compareTo(b.toString());
//    }
//    private List<T> processPivot(List<T> data, PivotSpec pivotSpec) {
//        if (pivotSpec == null) {
//            return data;
//        }
//        List<String> nonPivotDims = data.stream()
//                .flatMap(e -> accessor.getFieldNames().stream())
//                .filter(name -> !name.equals(pivotSpec.getPivotColumn()))
//                .collect(Collectors.toList());
//
//        Map<List<Object>, List<T>> groups = groupData(data, nonPivotDims);
//
//        // 2. 为每个分组创建透视行
//        List<T> result = new ArrayList<>();
//
//        for (List<T> group : groups.values()) {
//            // 创建基础行(复制非透视维度值)
//            T baseRow = createNewEntity();
//            for (String dim : nonPivotDims) {
//                Object val = accessor.getValue(group.get(0), dim);
//                accessor.setValue(baseRow, dim, val);
//            }
//
//            // 为每个透视值创建度量列
//            for (Object pivotValue : pivotSpec.getPivotValues()) {
//                String measureColName = pivotSpec.getAggregateFunction().name().toLowerCase()
//                        + "_" + pivotValue.toString().toLowerCase();
//
//                // 筛选当前透视值的数据
//                List<T> filtered = group.stream()
//                        .filter(e -> pivotValue.equals(accessor.getValue(e, pivotSpec.getPivotColumn())))
//                        .collect(Collectors.toList());
//
//                // 计算聚合值
//                Object aggValue = calculateAggregate(filtered, pivotSpec.getAggregateFunction());
//                accessor.setValue(baseRow, measureColName, aggValue);
//            }
//
//            result.add(baseRow);
//        }
//
//        return result;
//    }
//
//    // 计算聚合值
//    private Object calculateAggregate(List<T> data, AggregateFunctionType aggFunc) {
//        if (data == null || data.isEmpty()) {
//            return null;
//        }
//
//        switch (aggFunc) {
//            case COUNT: return data.size();
//            case SUM: return data.stream()
//                    .mapToDouble(e -> ((Number)accessor.getValue(e, aggFunc.getMeasureColumn())).doubleValue())
//                    .sum();
//            case AVG: return data.stream()
//                    .mapToDouble(e -> ((Number)accessor.getValue(e, aggFunc.getMeasureColumn())).doubleValue())
//                    .average().orElse(0);
//            case MAX: return data.stream()
//                    .map(e -> (Comparable)accessor.getValue(e, aggFunc.getMeasureColumn()))
//                    .max(Comparator.naturalOrder()).orElse(null);
//            case MIN: return data.stream()
//                    .map(e -> (Comparable)accessor.getValue(e, aggFunc.getMeasureColumn()))
//                    .min(Comparator.naturalOrder()).orElse(null);
//            default: throw new IllegalArgumentException("Unsupported aggregate function: " + aggFunc);
//        }
//    }
//    private Map<List<Object>, List<T>> groupData(List<T> data, List<String> dimensions) {
//        return data.stream()
//                .collect(Collectors.groupingBy(
//                        entity -> dimensions.stream()
//                                .map(dim -> accessor.getValue(entity, dim))
//                                .collect(Collectors.toList())
//                ));
//    }
//    private T createSubtotalEntity(List<T> groupData, List<String> subtotalDims) {
//        T subtotal = createNewEntity();
//        for (String dim : subtotalDims) {
//            Object val = accessor.getValue(groupData.get(0), dim);
//            accessor.setValue(subtotal, dim, val);
//        }
//        calculateMeasures(subtotal, groupData);
//        return subtotal;
//    }
//
//    private void calculateMeasures(T target, List<T> source) {
//        accessor.getFieldNames().stream()
//                .filter(name -> name.startsWith("measure_"))
//                .forEach(measure -> {
//                    double sum = source.stream()
//                            .mapToDouble(e -> ((Number)accessor.getValue(e, measure)).doubleValue())
//                            .sum();
//                    accessor.setValue(target, measure, sum);
//                });
//    }
//    private T createNewEntity() {
//        try {
//            return entityClass.newInstance();
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to create entity instance", e);
//        }
//    }
//
//}
