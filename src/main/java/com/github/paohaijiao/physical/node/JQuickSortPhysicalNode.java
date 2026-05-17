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

import com.github.paohaijiao.physical.JQuickPhysicalPlanNode;
import com.github.paohaijiao.physical.JQuickPhysicalPlanVisitor;
import com.github.paohaijiao.physical.domain.JQuickPhysicalColumn;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class JQuickSortPhysicalNode extends JQuickAbstractPhysicalNode {

    private final List<OrderByItem> orderByItems;

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

    public JQuickSortPhysicalNode(List<OrderByItem> orderByItems, JQuickPhysicalPlanNode child) {
        super(child);
        this.orderByItems = new ArrayList<>(orderByItems);
    }

    @Override
    public String getNodeType() {
        return "Sort";
    }

    @Override
    public List<JQuickPhysicalColumn> getOutputSchema() {
        return children.get(0).getOutputSchema();
    }

    @Override
    public JQuickPhysicalPlanNode clone() {
        List<OrderByItem> clonedItems = orderByItems.stream()
                .map(OrderByItem::clone)
                .collect(Collectors.toList());
        return new JQuickSortPhysicalNode(clonedItems, children.get(0).clone());
    }

    @Override
    public void accept(JQuickPhysicalPlanVisitor visitor) {
        visitor.visit(this);
    }

    public List<OrderByItem> getOrderByItems() { return orderByItems; }
}
