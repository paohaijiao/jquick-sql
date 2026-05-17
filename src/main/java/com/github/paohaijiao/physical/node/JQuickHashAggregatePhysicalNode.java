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
package com.github.paohaijiao.physical.node;


import com.github.paohaijiao.context.JQuickExecutionContext;
import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.logic.domain.JQuickGroupByNode;

import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;

import java.util.*;

public class JQuickHashAggregatePhysicalNode implements JQuickPhysicalPlanNode {
    private final List<JQuickExpression> groupKeys;
    private final List<JQuickGroupByNode.AggregateItem> aggregateItems;
    private final JQuickExpression havingCondition;
    private final JQuickPhysicalPlanNode child;

    public JQuickHashAggregatePhysicalNode(List<JQuickExpression> groupKeys,
                                           List<JQuickGroupByNode.AggregateItem> aggregateItems,
                                           JQuickExpression havingCondition,
                                           JQuickPhysicalPlanNode child) {
        this.groupKeys = groupKeys;
        this.aggregateItems = aggregateItems;
        this.havingCondition = havingCondition;
        this.child = child;
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
            for (JQuickGroupByNode.AggregateItem item : aggregateItems) {
                updateAggregate(aggState, item, row);
            }
        }

        // 完成聚合计算（如AVG需要最终计算）
        for (Map<String, Object> aggState : groups.values()) {
            for (JQuickGroupByNode.AggregateItem item : aggregateItems) {
                finalizeAggregate(aggState, item);
            }
        }

        // 构建结果
        JQuickDataSet.Builder builder = JQuickDataSet.builder();

        for (int i = 0; i < groupKeys.size(); i++) {
            builder.addColumn("group_" + i, Object.class, "group_by");
        }
        for (JQuickGroupByNode.AggregateItem item : aggregateItems) {
            builder.addColumn(item.getAlias(), item.getOutputType(), "aggregate");
        }

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
    private void updateAggregate(Map<String, Object> state, JQuickGroupByNode.AggregateItem item, JQuickRow row) {
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
        }
    }

    @SuppressWarnings("unchecked")
    private void finalizeAggregate(Map<String, Object> state, JQuickGroupByNode.AggregateItem item) {
        if (item.getFunctionName().equalsIgnoreCase("avg")) {
            Map<String, Object> avgState = (Map<String, Object>) state.get(item.getAlias() + "_state");
            if (avgState != null) {
                Double sum = (Double) avgState.get("sum");
                Long count = (Long) avgState.get("count");
                state.put(item.getAlias(), count > 0 ? sum / count : null);
            }
        }
    }

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

    @Override
    public String getNodeType() {
        return "HashAggregate";
    }

    @Override
    public long getEstimatedCost() {
        return child.getEstimatedCost();
    }
}
