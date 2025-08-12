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
package com.github.paohaijiao.handler.impl;

import com.github.paohaijiao.enums.JOlapOperationType;
import com.github.paohaijiao.expression.JExpression;
import com.github.paohaijiao.handler.JQueryHandler;
import com.github.paohaijiao.query.JQueryPlan;
import com.github.paohaijiao.util.JEntityAccessor;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * packageName com.github.paohaijiao.handler.impl
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/8/12
 */
public class JGroupByHandler<T> implements JQueryHandler<T> {
    private final List<JExpression> groupByItems;
    private final JOlapOperationType olapType;
    private final JEntityAccessor<T> accessor;
    private final Class<T> entityClass;

    public JGroupByHandler(List<JExpression> groupByItems,
                           JOlapOperationType olapType,
                           Class<T> entityClass) {
        this.groupByItems = groupByItems;
        this.olapType = olapType;
        this.entityClass = entityClass;
        this.accessor = new JEntityAccessor<>(entityClass);
    }

    @Override
    public List<T> handle(List<T> dataset, JQueryPlan plan) {
//        if (olapType != null) {
//            return handleOlapOperation(dataset);
//        }
        return handleRegularGroupBy(dataset);
    }

    private List<T> handleRegularGroupBy(List<T> dataset) {
        Map<List<Object>, List<T>> grouped = dataset.stream()
                .collect(Collectors.groupingBy(
                        entity -> groupByItems.stream()
                                .map(item -> accessor.getValue(entity, item))
                                .collect(Collectors.toList())
                ));

        return grouped.values().stream()
                .map(this::createAggregateResult)
                .collect(Collectors.toList());
    }
    private T createAggregateResult(List<T> groupData) {
        if (groupData == null || groupData.isEmpty()) {
            return null;
        }
        T result = createNewEntity();
        copyGroupingDimensions(result, groupData.get(0));
        calculateAggregates(result, groupData);
        markAsAggregate(result);
        return result;
    }
    private void copyGroupingDimensions(T target, T source) {
//        for (String dimensionField : getGroupingDimensionFields()) {
//            Object value = accessor.getValue(source, dimensionField);
//            accessor.setValue(target, dimensionField, value);
//        }
    }

    /**
     * 计算聚合值
     */
    private void calculateAggregates(T result, List<T> groupData) {
        accessor.getFieldNames().stream()
                .filter(field -> field.startsWith("agg_"))
                .forEach(field -> {
                    String aggType = field.substring(4); // 去掉"agg_"前缀
                    Object aggValue = calculateAggregate(aggType, field, groupData);
                    accessor.setValue(result, field, aggValue);
                });
    }

    /**
     * 计算单个聚合值
     */
    private Object calculateAggregate(String aggType, String fieldName, List<T> groupData) {
        switch (aggType.toUpperCase()) {
            case "SUM":
                return groupData.stream()
                        .mapToDouble(e -> ((Number)accessor.getValue(e, fieldName)).doubleValue())
                        .sum();
            case "COUNT":
                return groupData.size();
            case "AVG":
                return groupData.stream()
                        .mapToDouble(e -> ((Number)accessor.getValue(e, fieldName)).doubleValue())
                        .average().orElse(0);
            case "MAX":
                return groupData.stream()
                        .map(e -> (Comparable)accessor.getValue(e, fieldName))
                        .max(Comparator.naturalOrder()).orElse(null);
            case "MIN":
                return groupData.stream()
                        .map(e -> (Comparable)accessor.getValue(e, fieldName))
                        .min(Comparator.naturalOrder()).orElse(null);
            default:
                throw new IllegalArgumentException("Unsupported aggregate type: " + aggType);
        }
    }

    /**
     * 标记为聚合行（可选）
     */
    private void markAsAggregate(T entity) {
        try {
            accessor.setValue(entity, "isAggregate", true);
        } catch (Exception e) {
          e.printStackTrace();
        }
    }
    private T createNewEntity() {
        try {
            return entityClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to create entity instance", e);
        }
    }

//    private List<T> handleOlapOperation(List<T> dataset) {
//        switch (olapType) {
//            case ROLLUP:
//                return processRollup(dataset);
//            case DRILLDOWN:
//                return processDrillDown(dataset);
//            default:
//                return dataset;
//        }
//    }
//
//    private List<T> processRollup(List<T> data) {
//        List<T> result = new ArrayList<>();
//        return result;
//    }


}
