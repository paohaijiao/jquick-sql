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
import com.github.paohaijiao.plan.logical.ExecutionContext;
import com.github.paohaijiao.statement.JQuickDataSet;

public class JQuickLimitPhysicalNode implements JQuickPhysicalPlanNode {
    private final int limit;
    private final int offset;
    private final JQuickPhysicalPlanNode child;

    public JQuickLimitPhysicalNode(int limit, int offset, JQuickPhysicalPlanNode child) {
        this.limit = limit;
        this.offset = offset;
        this.child = child;
    }

    @Override
    public JQuickDataSet execute(ExecutionContext context) {
        JQuickDataSet data = child.execute(context);

        if (offset > 0) {
            data = data.skip(offset);
        }
        return data.limit(limit);
    }

    @Override
    public String getNodeType() {
        return "Limit";
    }

    @Override
    public long getEstimatedCost() {
        return Math.min(child.getEstimatedCost(), limit + offset);
    }
}
