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
package com.github.paohaijiao.logic.domain;


import com.github.paohaijiao.context.JQuickExecutionContext;
import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.logic.JQuickLogicalPlanNode;
import com.github.paohaijiao.logic.JQuickLogicalPlanVisitor;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;

import java.util.*;

/**
 * 聚合节点 - GROUP BY 子句（增强版）
 * 支持普通聚合、分组集（ROLLUP/CUBE/GROUPING SETS）、DISTINCT聚合
 */
public class JQuickAggregateNode implements JQuickLogicalPlanNode {

    private final List<JQuickExpression> groupKeys;
    private final List<GroupingSet> groupingSets;
    private final List<AggregateFunction> aggregates;
    private final JQuickLogicalPlanNode child;
    private final JQuickExpression havingCondition;
    private final boolean distinct;


    public JQuickAggregateNode(List<JQuickExpression> groupKeys, List<AggregateFunction> aggregates, JQuickLogicalPlanNode child, JQuickExpression havingCondition, boolean distinct) {
        this.groupKeys = groupKeys != null ? Collections.unmodifiableList(new ArrayList<>(groupKeys)) : null;
        this.groupingSets = null;
        this.aggregates = Collections.unmodifiableList(new ArrayList<>(aggregates));
        this.child = child;
        this.havingCondition = havingCondition;
        this.distinct = distinct;
    }

    // 分组集构造器（ROLLUP/CUBE/GROUPING SETS）
    public JQuickAggregateNode(List<GroupingSet> groupingSets, List<AggregateFunction> aggregates, JQuickLogicalPlanNode child, JQuickExpression havingCondition) {
        this.groupKeys = null;
        this.groupingSets = Collections.unmodifiableList(new ArrayList<>(groupingSets));
        this.aggregates = Collections.unmodifiableList(new ArrayList<>(aggregates));
        this.child = child;
        this.havingCondition = havingCondition;
        this.distinct = false;
    }

    @Override
    public JQuickDataSet execute(JQuickExecutionContext context) {
        JQuickDataSet data = child.execute(context);
        // 如果有 DISTINCT，先去重
        if (distinct) {
            data = data.distinct();
        }
        // 支持分组集
        if (groupingSets != null && !groupingSets.isEmpty()) {
            return executeGroupingSets(data);
        }
        // 普通分组聚合
        return executeNormalAggregate(data);
    }

    /**
     * 普通分组聚合
     */
    private JQuickDataSet executeNormalAggregate(JQuickDataSet data) {
        // 分组聚合
        Map<GroupKey, Map<String, Object>> groups = new LinkedHashMap<>();

        for (JQuickRow row : data.getRows()) {
            GroupKey key = new GroupKey(groupKeys, row);
            Map<String, Object> aggState = groups.computeIfAbsent(key, k -> new HashMap<>());
            // 初始化分组键值
            for (int i = 0; i < groupKeys.size(); i++) {
                String colName = "group_" + i;
                if (!aggState.containsKey(colName)) {
                    aggState.put(colName, groupKeys.get(i).evaluate(row));
                }
            }

            // 执行聚合
            for (AggregateFunction agg : aggregates) {
                updateAggregate(aggState, agg, row);
            }
        }

        // 完成聚合计算
        for (Map<String, Object> aggState : groups.values()) {
            for (AggregateFunction agg : aggregates) {
                finalizeAggregate(aggState, agg);
            }
        }

        return buildResultDataSet(groups);
    }

    /**
     * 分组集聚合（ROLLUP/CUBE/GROUPING SETS）
     */
    private JQuickDataSet executeGroupingSets(JQuickDataSet data) {
        List<JQuickRow> allResultRows = new ArrayList<>();

        for (GroupingSet gs : groupingSets) {
            List<JQuickExpression> keys = gs.getKeys();

            // 对当前分组集进行聚合
            Map<GroupKey, Map<String, Object>> groups = new LinkedHashMap<>();

            for (JQuickRow row : data.getRows()) {
                GroupKey key = new GroupKey(keys, row);
                Map<String, Object> aggState = groups.computeIfAbsent(key, k -> new HashMap<>());

                // 初始化分组键值
                for (int i = 0; i < keys.size(); i++) {
                    String colName = "group_" + i;
                    if (!aggState.containsKey(colName)) {
                        Object value = keys.get(i).evaluate(row);
                        aggState.put(colName, value);
                    }
                }

                // 添加 GROUPING 函数标记
                for (int i = 0; i < keys.size(); i++) {
                    String groupingCol = "grouping_" + i;
                    if (!aggState.containsKey(groupingCol)) {
                        // 对于当前分组集，所有键都参与分组，所以 grouping = 0
                        aggState.put(groupingCol, 0L);
                    }
                }

                // 执行聚合
                for (AggregateFunction agg : aggregates) {
                    updateAggregate(aggState, agg, row);
                }
            }

            // 完成聚合计算
            for (Map<String, Object> aggState : groups.values()) {
                for (AggregateFunction agg : aggregates) {
                    finalizeAggregate(aggState, agg);
                }

                // 转换为行
                JQuickRow resultRow = new JQuickRow();
                for (Map.Entry<String, Object> entry : aggState.entrySet()) {
                    if (!entry.getKey().endsWith("_state")) {
                        resultRow.put(entry.getKey(), entry.getValue());
                    }
                }
                allResultRows.add(resultRow);
            }
        }

        // 构建结果数据集
        JQuickDataSet.Builder builder = JQuickDataSet.builder();

        // 获取最大分组键数量
        int maxGroupKeys = groupingSets.stream()
                .mapToInt(gs -> gs.getKeys().size())
                .max()
                .orElse(0);

        // 添加分组列
        for (int i = 0; i < maxGroupKeys; i++) {
            builder.addColumn("group_" + i, Object.class, "group_by");
            builder.addColumn("grouping_" + i, Long.class, "grouping");
        }

        // 添加聚合列
        for (AggregateFunction agg : aggregates) {
            builder.addColumn(agg.getAlias(), agg.getReturnType(), "aggregate");
        }

        for (JQuickRow row : allResultRows) {
            builder.addRow(row);
        }

        JQuickDataSet result = builder.build();

        // 应用HAVING过滤
        if (havingCondition != null) {
            result = result.filter(row -> {
                Object val = havingCondition.evaluate(row);
                return val instanceof Boolean && (Boolean) val;
            });
        }

        return result;
    }

    /**
     * 更新聚合状态
     */
    @SuppressWarnings("unchecked")
    private void updateAggregate(Map<String, Object> state, AggregateFunction agg, JQuickRow row) {
        String key = agg.getAlias();
        String funcName = agg.getFunctionName().toLowerCase();
        Object value = agg.getArgument() != null ? agg.getArgument().evaluate(row) : null;

        // DISTINCT 聚合需要去重
        if (agg.isDistinct()) {
            Set<Object> distinctSet = (Set<Object>) state.get(key + "_distinct");
            if (distinctSet == null) {
                distinctSet = new HashSet<>();
                state.put(key + "_distinct", distinctSet);
            }
            if (value != null) {
                distinctSet.add(value);
            }
            // 对于 DISTINCT 聚合，实际值需要从 distinctSet 计算
            return;
        }

        switch (funcName) {
            case "count":
                Long count = (Long) state.get(key);
                if (count == null) count = 0L;
                if (value != null || agg.isCountStar()) count++;
                state.put(key, count);
                break;

            case "sum":
                Double sum = (Double) state.get(key);
                if (sum == null) sum = 0.0;
                if (value instanceof Number) {
                    sum += ((Number) value).doubleValue();
                }
                state.put(key, sum);
                break;

            case "avg":
                Map<String, Object> avgState = (Map<String, Object>) state.get(key + "_state");
                if (avgState == null) {
                    avgState = new HashMap<>();
                    avgState.put("sum", 0.0);
                    avgState.put("count", 0L);
                    state.put(key + "_state", avgState);
                }
                if (value instanceof Number) {
                    avgState.put("sum", ((Double) avgState.get("sum")) + ((Number) value).doubleValue());
                    avgState.put("count", ((Long) avgState.get("count")) + 1);
                }
                break;

            case "max":
                if (state.get(key) == null || (value instanceof Comparable &&
                        ((Comparable) value).compareTo(state.get(key)) > 0)) {
                    state.put(key, value);
                }
                break;

            case "min":
                if (state.get(key) == null || (value instanceof Comparable &&
                        ((Comparable) value).compareTo(state.get(key)) < 0)) {
                    state.put(key, value);
                }
                break;

            case "group_concat":
            case "string_agg":
                List<String> list = (List<String>) state.get(key + "_list");
                if (list == null) {
                    list = new ArrayList<>();
                    state.put(key + "_list", list);
                }
                if (value != null) {
                    list.add(value.toString());
                }
                break;

            case "bit_and":
                Long bitAnd = (Long) state.get(key);
                if (bitAnd == null && value instanceof Number) {
                    bitAnd = ((Number) value).longValue();
                } else if (value instanceof Number) {
                    bitAnd &= ((Number) value).longValue();
                }
                state.put(key, bitAnd);
                break;

            case "bit_or":
                Long bitOr = (Long) state.get(key);
                if (bitOr == null && value instanceof Number) {
                    bitOr = ((Number) value).longValue();
                } else if (value instanceof Number) {
                    bitOr |= ((Number) value).longValue();
                }
                state.put(key, bitOr);
                break;
        }
    }

    /**
     * 完成聚合计算
     */
    @SuppressWarnings("unchecked")
    private void finalizeAggregate(Map<String, Object> state, AggregateFunction agg) {
        String key = agg.getAlias();
        String funcName = agg.getFunctionName().toLowerCase();

        // 处理 DISTINCT 聚合
        if (agg.isDistinct()) {
            Set<Object> distinctSet = (Set<Object>) state.get(key + "_distinct");
            if (distinctSet != null) {
                switch (funcName) {
                    case "count":
                        state.put(key, (long) distinctSet.size());
                        break;
                    case "sum":
                        double sum = distinctSet.stream()
                                .filter(v -> v instanceof Number)
                                .mapToDouble(v -> ((Number) v).doubleValue())
                                .sum();
                        state.put(key, sum);
                        break;
                    case "avg":
                        double avg = distinctSet.stream()
                                .filter(v -> v instanceof Number)
                                .mapToDouble(v -> ((Number) v).doubleValue())
                                .average()
                                .orElse(0.0);
                        state.put(key, avg);
                        break;
                    case "max":
                    case "min":
                        Optional<?> result = distinctSet.stream()
                                .filter(v -> v instanceof Comparable)
                                .reduce(funcName.equals("max") ?
                                        (a, b) -> ((Comparable) a).compareTo(b) > 0 ? a : b :
                                        (a, b) -> ((Comparable) a).compareTo(b) < 0 ? a : b);
                        state.put(key, result.orElse(null));
                        break;
                }
                state.remove(key + "_distinct");
            }
            return;
        }

        // 处理 AVG
        if (funcName.equals("avg")) {
            Map<String, Object> avgState = (Map<String, Object>) state.get(key + "_state");
            if (avgState != null) {
                Double sum = (Double) avgState.get("sum");
                Long count = (Long) avgState.get("count");
                state.put(key, count > 0 ? sum / count : null);
                state.remove(key + "_state");
            }
        }

        // 处理 GROUP_CONCAT / STRING_AGG
        if (funcName.equals("group_concat") || funcName.equals("string_agg")) {
            List<String> list = (List<String>) state.get(key + "_list");
            if (list != null) {
                String separator = agg.getSeparator() != null ? agg.getSeparator() : ",";
                state.put(key, String.join(separator, list));
                state.remove(key + "_list");
            }
        }
    }

    /**
     * 构建结果数据集
     */
    private JQuickDataSet buildResultDataSet(Map<GroupKey, Map<String, Object>> groups) {
        JQuickDataSet.Builder builder = JQuickDataSet.builder();

        // 添加分组列
        for (int i = 0; i < groupKeys.size(); i++) {
            builder.addColumn("group_" + i, Object.class, "group_by");
        }

        // 添加聚合列
        for (AggregateFunction agg : aggregates) {
            builder.addColumn(agg.getAlias(), agg.getReturnType(), "aggregate");
        }

        // 添加行
        for (Map<String, Object> aggState : groups.values()) {
            JQuickRow row = new JQuickRow();
            for (Map.Entry<String, Object> entry : aggState.entrySet()) {
                if (!entry.getKey().endsWith("_state") && !entry.getKey().endsWith("_distinct")) {
                    row.put(entry.getKey(), entry.getValue());
                }
            }
            builder.addRow(row);
        }

        JQuickDataSet result = builder.build();

        // 应用HAVING过滤
        if (havingCondition != null) {
            result = result.filter(row -> {
                Object val = havingCondition.evaluate(row);
                return val instanceof Boolean && (Boolean) val;
            });
        }

        return result;
    }

    @Override
    public String getNodeType() {
        return "Aggregate";
    }

    @Override
    public List<JQuickLogicalPlanNode> getChildren() {
        return Collections.singletonList(child);
    }

    @Override
    public void accept(JQuickLogicalPlanVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public List<String> getOutputColumns() {
        List<String> columns = new ArrayList<>();
        if (groupKeys != null) {
            for (int i = 0; i < groupKeys.size(); i++) {
                columns.add("group_" + i);
            }
        }
        for (AggregateFunction agg : aggregates) {
            columns.add(agg.getAlias());
        }
        return columns;
    }

    @Override
    public JQuickLogicalPlanNode clone() {
        if (groupKeys != null) {
            List<JQuickExpression> clonedKeys = new ArrayList<>();
            for (JQuickExpression key : groupKeys) {
                clonedKeys.add(key.clone());
            }
            List<AggregateFunction> clonedAggs = new ArrayList<>();
            for (AggregateFunction agg : aggregates) {
                clonedAggs.add(agg.clone());
            }
            JQuickExpression clonedHaving = havingCondition != null ? havingCondition.clone() : null;
            return new JQuickAggregateNode(clonedKeys, clonedAggs, child.clone(), clonedHaving, distinct);
        } else {
            List<GroupingSet> clonedGroupingSets = new ArrayList<>();
            for (GroupingSet gs : groupingSets) {
                clonedGroupingSets.add(gs.clone());
            }
            List<AggregateFunction> clonedAggs = new ArrayList<>();
            for (AggregateFunction agg : aggregates) {
                clonedAggs.add(agg.clone());
            }
            JQuickExpression clonedHaving = havingCondition != null ? havingCondition.clone() : null;
            return new JQuickAggregateNode(clonedGroupingSets, clonedAggs, child.clone(), clonedHaving);
        }
    }

    public List<JQuickExpression> getGroupKeys() {
        return groupKeys;
    }

    public List<GroupingSet> getGroupingSets() {
        return groupingSets;
    }

    public List<AggregateFunction> getAggregates() {
        return aggregates;
    }

    public JQuickLogicalPlanNode getChild() {
        return child;
    }

    public JQuickExpression getHavingCondition() {
        return havingCondition;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public boolean isGroupingSets() {
        return groupingSets != null && !groupingSets.isEmpty();
    }

    /**
     * 聚合函数
     */
    public static class AggregateFunction {
        private final String functionName;
        private final JQuickExpression argument;
        private final boolean distinct;
        private final String alias;
        private final boolean countStar;
        private final String separator;  // 用于 GROUP_CONCAT

        public AggregateFunction(String functionName, JQuickExpression argument,
                                 boolean distinct, String alias) {
            this(functionName, argument, distinct, alias, false, null);
        }

        public AggregateFunction(String functionName, JQuickExpression argument,
                                 boolean distinct, String alias, boolean countStar, String separator) {
            this.functionName = functionName;
            this.argument = argument;
            this.distinct = distinct;
            this.alias = alias != null ? alias : functionName;
            this.countStar = countStar;
            this.separator = separator;
        }

        public Class<?> getReturnType() {
            switch (functionName.toLowerCase()) {
                case "count":
                    return Long.class;
                case "sum":
                case "avg":
                    return Double.class;
                case "bit_and":
                case "bit_or":
                    return Long.class;
                case "group_concat":
                case "string_agg":
                    return String.class;
                default:
                    return Object.class;
            }
        }

        public String getFunctionName() {
            return functionName;
        }

        public JQuickExpression getArgument() {
            return argument;
        }

        public boolean isDistinct() {
            return distinct;
        }

        public String getAlias() {
            return alias;
        }

        public boolean isCountStar() {
            return countStar;
        }

        public String getSeparator() {
            return separator;
        }

        public AggregateFunction clone() {
            JQuickExpression clonedArg = argument != null ? argument.clone() : null;
            return new AggregateFunction(functionName, clonedArg, distinct, alias, countStar, separator);
        }
    }

    /**
     * 分组集（用于 ROLLUP/CUBE/GROUPING SETS）
     */
    public static class GroupingSet {
        private final List<JQuickExpression> keys;
        private final GroupingSetType type;

        public GroupingSet(List<JQuickExpression> keys, GroupingSetType type) {
            this.keys = keys;
            this.type = type;
        }

        public List<JQuickExpression> getKeys() {
            return keys;
        }

        public GroupingSetType getType() {
            return type;
        }

        /**
         * 对于 ROLLUP，返回所有子分组集
         */
        public List<List<JQuickExpression>> expandRollup() {
            List<List<JQuickExpression>> result = new ArrayList<>();
            for (int i = 0; i <= keys.size(); i++) {
                result.add(keys.subList(0, i));
            }
            return result;
        }

        /**
         * 对于 CUBE，返回所有子分组集
         */
        public List<List<JQuickExpression>> expandCube() {
            List<List<JQuickExpression>> result = new ArrayList<>();
            int n = keys.size();
            for (int i = 0; i < (1 << n); i++) {
                List<JQuickExpression> subset = new ArrayList<>();
                for (int j = 0; j < n; j++) {
                    if ((i & (1 << j)) != 0) {
                        subset.add(keys.get(j));
                    }
                }
                result.add(subset);
            }
            return result;
        }

        public GroupingSet clone() {
            List<JQuickExpression> clonedKeys = new ArrayList<>();
            for (JQuickExpression key : keys) {
                clonedKeys.add(key.clone());
            }
            return new GroupingSet(clonedKeys, type);
        }

        public enum GroupingSetType {
            SIMPLE,      // 普通分组集 (a, b)
            ROLLUP,      // ROLLUP (a, b, c)
            CUBE,        // CUBE (a, b, c)
            GROUPING_SET // GROUPING SETS ((a), (b), (a,b))
        }
    }

    /**
     * 分组键
     */
    private static class GroupKey {
        private final List<Object> values;

        GroupKey(List<JQuickExpression> groupKeys, JQuickRow row) {
            this.values = new ArrayList<>();
            for (JQuickExpression key : groupKeys) {
                values.add(key.evaluate(row));
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            GroupKey groupKey = (GroupKey) o;
            return Objects.equals(values, groupKey.values);
        }

        @Override
        public int hashCode() {
            return Objects.hash(values);
        }
    }
}