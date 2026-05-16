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
package com.github.paohaijiao.plan.logic.domain;

import com.github.paohaijiao.executor.JQuickExecutionContext;
import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.plan.logic.JQuickLogicalPlanNode;
import com.github.paohaijiao.plan.logic.JQuickLogicalPlanVisitor;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;

import java.util.*;

/**
 * 分组聚合节点 - GROUP BY 子句
 */
public class JQuickGroupByNode implements JQuickLogicalPlanNode {

    private final List<JQuickExpression> groupKeys;
    private final List<AggregateItem> aggregateItems;
    private final JQuickLogicalPlanNode child;
    private final JQuickExpression havingCondition;

    public JQuickGroupByNode(List<JQuickExpression> groupKeys, List<AggregateItem> aggregateItems,
                             JQuickLogicalPlanNode child, JQuickExpression havingCondition) {
        this.groupKeys = Collections.unmodifiableList(new ArrayList<>(groupKeys));
        this.aggregateItems = Collections.unmodifiableList(new ArrayList<>(aggregateItems));
        this.child = child;
        this.havingCondition = havingCondition;
    }

    @Override
    public JQuickDataSet execute(JQuickExecutionContext context) {
        JQuickDataSet data = child.execute(context);

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
            for (AggregateItem item : aggregateItems) {
                updateAggregate(aggState, item, row);
            }
        }

        // 完成聚合计算（如AVG需要最终计算）
        for (Map<String, Object> aggState : groups.values()) {
            for (AggregateItem item : aggregateItems) {
                finalizeAggregate(aggState, item);
            }
        }

        // 构建结果数据集
        JQuickDataSet.Builder builder = JQuickDataSet.builder();

        // 添加分组列
        for (int i = 0; i < groupKeys.size(); i++) {
            builder.addColumn("group_" + i, Object.class, "group_by");
        }

        // 添加聚合列
        for (AggregateItem item : aggregateItems) {
            builder.addColumn(item.getAlias(), item.getOutputType(), "aggregate");
        }

        // 添加行
        for (Map<String, Object> aggState : groups.values()) {
            JQuickRow row = new JQuickRow();
            for (Map.Entry<String, Object> entry : aggState.entrySet()) {
                if (!entry.getKey().endsWith("_state")) {
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

    @SuppressWarnings("unchecked")
    private void updateAggregate(Map<String, Object> state, AggregateItem item, JQuickRow row) {
        String key = item.getAlias();
        String funcName = item.getFunctionName().toLowerCase();
        Object value = item.getExpression().evaluate(row);

        switch (funcName) {
            case "count":
                Long count = (Long) state.get(key);
                if (count == null) count = 0L;
                if (value != null || item.isCountStar()) count++;
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

            case "first":
                if (!state.containsKey(key)) {
                    state.put(key, value);
                }
                break;

            case "last":
                state.put(key, value);
                break;

            case "count_distinct":
                Set<Object> distinctSet = (Set<Object>) state.get(key + "_set");
                if (distinctSet == null) {
                    distinctSet = new HashSet<>();
                    state.put(key + "_set", distinctSet);
                }
                if (value != null) {
                    distinctSet.add(value);
                }
                state.put(key, (long) distinctSet.size());
                break;
        }
    }

    @SuppressWarnings("unchecked")
    private void finalizeAggregate(Map<String, Object> state, AggregateItem item) {
        String funcName = item.getFunctionName().toLowerCase();

        if (funcName.equals("avg")) {
            Map<String, Object> avgState = (Map<String, Object>) state.get(item.getAlias() + "_state");
            if (avgState != null) {
                Double sum = (Double) avgState.get("sum");
                Long count = (Long) avgState.get("count");
                state.put(item.getAlias(), count > 0 ? sum / count : null);
                state.remove(item.getAlias() + "_state");
            }
        }
    }

    @Override
    public String getNodeType() {
        return "GroupBy";
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
        for (int i = 0; i < groupKeys.size(); i++) {
            columns.add("group_" + i);
        }
        for (AggregateItem item : aggregateItems) {
            columns.add(item.getAlias());
        }
        return columns;
    }

    @Override
    public JQuickLogicalPlanNode clone() {
        return new JQuickGroupByNode(groupKeys, aggregateItems, child.clone(), havingCondition);
    }

    public List<JQuickExpression> getGroupKeys() {
        return groupKeys;
    }

    public List<AggregateItem> getAggregateItems() {
        return aggregateItems;
    }

    public JQuickLogicalPlanNode getChild() {
        return child;
    }

    public JQuickExpression getHavingCondition() {
        return havingCondition;
    }

    /**
     * 聚合项
     */
    public static class AggregateItem {
        private final JQuickExpression expression;
        private final String functionName;
        private final String alias;
        private final boolean isCountStar;

        public AggregateItem(JQuickExpression expression, String functionName, String alias) {
            this(expression, functionName, alias, false);
        }

        public AggregateItem(JQuickExpression expression, String functionName, String alias, boolean isCountStar) {
            this.expression = expression;
            this.functionName = functionName;
            this.alias = alias;
            this.isCountStar = isCountStar;
        }

        public Class<?> getOutputType() {
            switch (functionName.toLowerCase()) {
                case "count":
                    return Long.class;
                case "sum":
                case "avg":
                    return Double.class;
                default:
                    return Object.class;
            }
        }

        public JQuickExpression getExpression() {
            return expression;
        }

        public String getFunctionName() {
            return functionName;
        }

        public String getAlias() {
            return alias;
        }

        public boolean isCountStar() {
            return isCountStar;
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
