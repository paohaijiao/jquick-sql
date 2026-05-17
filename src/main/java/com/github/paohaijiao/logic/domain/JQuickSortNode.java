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
import com.github.paohaijiao.logic.JQuickLogicalPlanNode;
import com.github.paohaijiao.logic.JQuickLogicalPlanVisitor;
import com.github.paohaijiao.statement.JQuickDataSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 排序节点 - ORDER BY 子句
 */
public class JQuickSortNode implements JQuickLogicalPlanNode {

    private final List<OrderByItem> orderByItems;
    private final JQuickLogicalPlanNode child;

    public JQuickSortNode(List<OrderByItem> orderByItems, JQuickLogicalPlanNode child) {
        this.orderByItems = Collections.unmodifiableList(new ArrayList<>(orderByItems));
        this.child = child;
    }

    @Override
    public JQuickDataSet execute(JQuickExecutionContext context) {
        JQuickDataSet data = child.execute(context);
        JQuickDataSet result = data;

        // 从后往前排序，保持多列排序的正确顺序
        for (int i = orderByItems.size() - 1; i >= 0; i--) {
            OrderByItem item = orderByItems.get(i);
            result = result.orderBy(item.getColumnName(), item.isAscending());
        }

        return result;
    }

    @Override
    public String getNodeType() {
        return "Sort";
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
        return child.getOutputColumns();
    }

    @Override
    public JQuickLogicalPlanNode clone() {
        return new JQuickSortNode(orderByItems, child.clone());
    }

    public List<OrderByItem> getOrderByItems() {
        return orderByItems;
    }

    public JQuickLogicalPlanNode getChild() {
        return child;
    }

    /**
     * 排序项
     */
    public static class OrderByItem {
        private final String columnName;
        private final boolean ascending;

        public OrderByItem(String columnName, boolean ascending) {
            this.columnName = columnName;
            this.ascending = ascending;
        }

        public String getColumnName() {
            return columnName;
        }

        public boolean isAscending() {
            return ascending;
        }

        @Override
        public String toString() {
            return columnName + (ascending ? " ASC" : " DESC");
        }
    }
}
