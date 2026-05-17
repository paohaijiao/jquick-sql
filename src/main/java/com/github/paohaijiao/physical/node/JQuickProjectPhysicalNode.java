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
import com.github.paohaijiao.logic.domain.JQuickProjectNode;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;

import java.util.List;

public class JQuickProjectPhysicalNode implements JQuickPhysicalPlanNode {
    private final List<JQuickProjectNode.SelectItem> selectItems;
    private final JQuickPhysicalPlanNode child;
    private final boolean distinct;

    public JQuickProjectPhysicalNode(List<JQuickProjectNode.SelectItem> selectItems, JQuickPhysicalPlanNode child, boolean distinct) {
        this.selectItems = selectItems;
        this.child = child;
        this.distinct = distinct;
    }

    @Override
    public JQuickDataSet execute(JQuickExecutionContext context) {
        JQuickDataSet data = child.execute(context);
        JQuickDataSet.Builder builder = JQuickDataSet.builder();
        for (JQuickProjectNode.SelectItem item : selectItems) {
            builder.addColumn(item.getAlias(), item.getExpression().getType(), "projection");
        }

        for (JQuickRow row : data.getRows()) {
            JQuickRow newRow = new JQuickRow();
            for (JQuickProjectNode.SelectItem item : selectItems) {
                newRow.put(item.getAlias(), item.getExpression().evaluate(row));
            }
            builder.addRow(newRow);
        }

        JQuickDataSet result = builder.build();
        if (distinct) {
            result = result.distinct();
        }

        return result;
    }

    @Override
    public String getNodeType() {
        return "Project";
    }

    @Override
    public long getEstimatedCost() {
        return child.getEstimatedCost();
    }
}
