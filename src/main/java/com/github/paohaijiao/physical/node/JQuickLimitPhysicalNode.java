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
import com.github.paohaijiao.physical.domain.JQuickPhysicalStats;

import java.util.HashMap;
import java.util.List;

public class JQuickLimitPhysicalNode extends JQuickAbstractPhysicalNode {

    private final int limit;

    private final int offset;

    public JQuickLimitPhysicalNode(int limit, int offset, JQuickPhysicalPlanNode child) {
        super(child);
        this.limit = limit;
        this.offset = offset;
    }

    public JQuickLimitPhysicalNode(int limit, JQuickPhysicalPlanNode child) {
        this(limit, 0, child);
    }

    @Override
    public String getNodeType() {
        return "Limit";
    }

    @Override
    public List<JQuickPhysicalColumn> getOutputSchema() {
        return children.get(0).getOutputSchema();
    }

    @Override
    public JQuickPhysicalPlanNode clone() {
        return new JQuickLimitPhysicalNode(limit, offset, children.get(0).clone());
    }

    @Override
    public void accept(JQuickPhysicalPlanVisitor visitor) {
        visitor.visit(this);
    }

    public int getLimit() { return limit; }

    public int getOffset() { return offset; }
    @Override
    public JQuickPhysicalStats getStats() {
        JQuickPhysicalPlanNode child = getChild();
        if (child == null) {
            return JQuickPhysicalStats.empty();
        }
        JQuickPhysicalStats childStats = child.getStats();
        long childRows = childStats.getEstimatedRowCount();
        long estimatedRows = Math.min(limit, Math.max(0, childRows - offset));
        if (offset >= childRows) {// 如果 offset 大于子节点行数，结果为 0
            estimatedRows = 0;
        }
        long estimatedDataSize = estimatedRows * 200;
        return new JQuickPhysicalStats(estimatedRows, estimatedDataSize, new HashMap<>());
    }
}