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


import com.github.paohaijiao.plan.logical.ExecutionContext;
import com.github.paohaijiao.statement.JQuickDataSet;

import java.util.*;

/**
 * CTE物理节点 - 处理 WITH 子句
 * 支持递归CTE和非递归CTE
 */
public class WithPhysicalNode implements PhysicalPlanNode {

    private final PhysicalPlanNode child;
    private final Map<String, PhysicalPlanNode> ctes;
    private final boolean recursive;

    public WithPhysicalNode(PhysicalPlanNode child, Map<String, PhysicalPlanNode> ctes) {
        this(child, ctes, false);
    }

    public WithPhysicalNode(PhysicalPlanNode child, Map<String, PhysicalPlanNode> ctes, boolean recursive) {
        this.child = child;
        this.ctes = Collections.unmodifiableMap(new LinkedHashMap<>(ctes));
        this.recursive = recursive;
    }

    @Override
    public JQuickDataSet execute(ExecutionContext context) {
        // 先执行所有非递归CTE
        for (Map.Entry<String, PhysicalPlanNode> entry : ctes.entrySet()) {
            if (!recursive) {
                JQuickDataSet cteData = entry.getValue().execute(context);
                context.cacheCTE(entry.getKey(), cteData);
            }
        }

        // 处理递归CTE
        if (recursive) {
            executeRecursiveCTE(context);
        }

        // 执行主查询
        return child.execute(context);
    }

    /**
     * 执行递归CTE
     */
    private void executeRecursiveCTE(ExecutionContext context) {
        // 假设递归CTE只有一个，且第一个是初始查询，第二个是递归部分
        Iterator<Map.Entry<String, PhysicalPlanNode>> iterator = ctes.entrySet().iterator();
        if (!iterator.hasNext()) return;

        Map.Entry<String, PhysicalPlanNode> cteEntry = iterator.next();
        String cteName = cteEntry.getKey();
        PhysicalPlanNode recursivePlan = cteEntry.getValue();

        // 需要解析递归CTE的结构
        // 实际实现中，递归CTE应该由两部分组成：初始查询和递归查询
        // 这里简化处理

        JQuickDataSet result = null;
        JQuickDataSet previous = null;
        int maxIterations = 100;  // 防止无限递归

        for (int i = 0; i < maxIterations; i++) {
            // 执行递归查询
            JQuickDataSet current = recursivePlan.execute(context);

            if (i == 0) {
                result = current;
                previous = current;
            } else {
                // 合并新结果
                if (result != null) {
                    result = result.concat(current);
                }
                // 检查是否还有新数据
                if (previous != null && current.size() == 0) {
                    break;
                }
                previous = current;
            }

            // 将当前结果缓存，供下一次递归使用
            context.cacheCTE(cteName, current);
        }

        if (result != null) {
            context.cacheCTE(cteName, result);
        }
    }

    @Override
    public String getNodeType() {
        return "With";
    }

    @Override
    public long getEstimatedCost() {
        long totalCost = 0;
        for (PhysicalPlanNode cte : ctes.values()) {
            totalCost += cte.getEstimatedCost();
        }
        totalCost += child.getEstimatedCost();
        return totalCost;
    }
}
