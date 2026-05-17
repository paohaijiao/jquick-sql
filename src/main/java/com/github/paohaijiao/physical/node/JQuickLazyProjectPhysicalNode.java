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


import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.plan.logical.ExecutionContext;
import com.github.paohaijiao.plan.logical.ProjectNode;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;

/**
 * 延迟投影 - 只在需要时才计算表达式
 * 适用于投影列少且计算成本高的情况
 */
public class JQuickLazyProjectPhysicalNode implements JQuickPhysicalPlanNode {
    private final List<ProjectNode.SelectItem> selectItems;
    private final JQuickPhysicalPlanNode child;

    public JQuickLazyProjectPhysicalNode(List<ProjectNode.SelectItem> selectItems, JQuickPhysicalPlanNode child) {
        this.selectItems = selectItems;
        this.child = child;
    }

    @Override
    public JQuickDataSet execute(ExecutionContext context) {
        JQuickDataSet data = child.execute(context);

        JQuickDataSet.Builder builder = JQuickDataSet.builder();

        for (ProjectNode.SelectItem item : selectItems) {
            builder.addColumn(item.getAlias(), item.getExpression().getType(), "projection");
        }

        for (JQuickRow row : data.getRows()) {
            JQuickRow newRow = new LazyRow(row);
            for (ProjectNode.SelectItem item : selectItems) {
                // 延迟计算 - 先保存表达式和原始行，需要时才计算
                newRow.put(item.getAlias(), new LazyValue(item.getExpression(), row));
            }
            builder.addRow(newRow);
        }

        return builder.build();
    }

    /**
     * 延迟计算的值
     */
    private static class LazyValue {
        private final JQuickExpression expression;
        private final JQuickRow row;
        private Object cachedValue;
        private boolean computed = false;

        LazyValue(JQuickExpression expression, JQuickRow row) {
            this.expression = expression;
            this.row = row;
        }

        public Object get() {
            if (!computed) {
                cachedValue = expression.evaluate(row);
                computed = true;
            }
            return cachedValue;
        }
    }

    private static class LazyRow extends JQuickRow {
        LazyRow(JQuickRow original) {
            super(original);
        }

        @Override
        public Object get(Object key) {
            Object value = super.get(key);
            if (value instanceof LazyValue) {
                return ((LazyValue) value).get();
            }
            return value;
        }
    }

    @Override
    public String getNodeType() {
        return "LazyProject";
    }

    @Override
    public long getEstimatedCost() {
        return child.getEstimatedCost();
    }
}
