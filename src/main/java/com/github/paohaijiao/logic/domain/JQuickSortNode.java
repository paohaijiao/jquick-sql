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


import com.github.paohaijiao.logic.JQuickLogicalPlanNode;
import com.github.paohaijiao.logic.JQuickLogicalPlanVisitor;

import java.util.*;

/**
 * 排序节点 - 描述 ORDER BY 子句
 */
public class JQuickSortNode implements JQuickLogicalPlanNode {

    private final List<OrderByItem> orderByItems;

    private final JQuickLogicalPlanNode child;

    public JQuickSortNode(List<OrderByItem> orderByItems, JQuickLogicalPlanNode child) {
        this.orderByItems = Collections.unmodifiableList(new ArrayList<>(orderByItems));
        this.child = child;
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
        List<OrderByItem> clonedItems = new ArrayList<>();
        for (OrderByItem item : orderByItems) {
            clonedItems.add(item.clone());
        }
        return new JQuickSortNode(clonedItems, child.clone());
    }

    public List<OrderByItem> getOrderByItems() { return orderByItems; }

    public JQuickLogicalPlanNode getChild() { return child; }

    /**
     * 排序项
     */
    public static class OrderByItem {

        private final String columnName;

        private final boolean ascending;

        private final boolean nullsFirst;

        public OrderByItem(String columnName, boolean ascending) {
            this(columnName, ascending, false);
        }

        public OrderByItem(String columnName, boolean ascending, boolean nullsFirst) {
            this.columnName = columnName;
            this.ascending = ascending;
            this.nullsFirst = nullsFirst;
        }

        public String getColumnName() { return columnName; }

        public boolean isAscending() { return ascending; }

        public boolean isNullsFirst() { return nullsFirst; }

        public OrderByItem clone() {
            return new OrderByItem(columnName, ascending, nullsFirst);
        }
    }
}