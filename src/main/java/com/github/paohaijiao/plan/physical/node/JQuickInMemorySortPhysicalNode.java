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
package com.github.paohaijiao.plan.physical.node;

import com.github.paohaijiao.plan.logical.ExecutionContext;
import com.github.paohaijiao.plan.logical.SortNode;
import com.github.paohaijiao.statement.JQuickDataSet;

import java.util.List;

public class JQuickInMemorySortPhysicalNode implements JQuickPhysicalPlanNode {
    private final List<SortNode.OrderByItem> orderByItems;
    private final JQuickPhysicalPlanNode child;

    public JQuickInMemorySortPhysicalNode(List<SortNode.OrderByItem> orderByItems, JQuickPhysicalPlanNode child) {
        this.orderByItems = orderByItems;
        this.child = child;
    }

    @Override
    public JQuickDataSet execute(ExecutionContext context) {
        JQuickDataSet data = child.execute(context);

        for (SortNode.OrderByItem item : orderByItems) {
            data = data.orderBy(item.getColumnName(), item.isAscending());
        }

        return data;
    }

    @Override
    public String getNodeType() {
        return "InMemorySort";
    }

    @Override
    public long getEstimatedCost() {
        long rows = child.getEstimatedCost();
        return (long) (rows * Math.log(rows));
    }
}
