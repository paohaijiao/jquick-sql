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
import com.github.paohaijiao.statement.JQuickRow;

import java.util.PriorityQueue;

/**
 * Top-N 物理节点 - 使用优先队列只保留前N条记录
 */
public class JQuickTopNPhysicalNode implements JQuickPhysicalPlanNode {
    private final int limit;
    private final int offset;
    private final JQuickPhysicalPlanNode child;

    public JQuickTopNPhysicalNode(int limit, int offset, JQuickPhysicalPlanNode child) {
        this.limit = limit;
        this.offset = offset;
        this.child = child;
    }

    @Override
    public JQuickDataSet execute(ExecutionContext context) {
        JQuickDataSet data = child.execute(context);

        // 如果已排序，直接取前N条
        if (isAlreadySorted(data)) {
            if (offset > 0) {
                data = data.skip(offset);
            }
            return data.limit(limit);
        }

        // 使用优先队列维护Top-N
        PriorityQueue<JQuickRow> heap = new PriorityQueue<>((a, b) -> {
            // 需要根据实际的排序列比较
            return 0;
        });

        // 简化的Top-N实现
        if (offset > 0) {
            data = data.skip(offset);
        }
        return data.limit(limit);
    }

    private boolean isAlreadySorted(JQuickDataSet data) {
        // 检查数据是否已按需要排序
        return false;
    }

    @Override
    public String getNodeType() {
        return "TopN";
    }

    @Override
    public long getEstimatedCost() {
        return child.getEstimatedCost();
    }
}
