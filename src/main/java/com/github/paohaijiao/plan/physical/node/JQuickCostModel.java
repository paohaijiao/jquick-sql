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

/**
 * packageName com.github.paohaijiao.plan.physical
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/5/16
 */
public class JQuickCostModel {
    // 各种操作的CPU成本系数
    private static final double CPU_COST_PER_ROW = 1.0;
    private static final double IO_COST_PER_PAGE = 10.0;
    private static final double NETWORK_COST_PER_ROW = 5.0;

    public JoinCost estimateJoinCost(long leftRows, long rightRows) {
        JoinCost cost = new JoinCost();

        // 嵌套循环连接成本
        cost.nestedLoopCost = leftRows * rightRows * CPU_COST_PER_ROW;

        // 哈希连接成本
        cost.hashJoinCost = (leftRows + rightRows) * CPU_COST_PER_ROW +
                Math.min(leftRows, rightRows) * CPU_COST_PER_ROW;

        // 排序合并连接成本
        cost.sortMergeCost = (leftRows * Math.log(leftRows) +
                rightRows * Math.log(rightRows)) * CPU_COST_PER_ROW +
                (leftRows + rightRows) * CPU_COST_PER_ROW;

        return cost;
    }
    public static class JoinCost {
        double nestedLoopCost;
        double hashJoinCost;
        double sortMergeCost;

        public double getMinCost() {
            return Math.min(nestedLoopCost, Math.min(hashJoinCost, sortMergeCost));
        }

        public String getBestAlgorithm() {
            if (nestedLoopCost <= hashJoinCost && nestedLoopCost <= sortMergeCost) {
                return "NESTED_LOOP";
            } else if (hashJoinCost <= sortMergeCost) {
                return "HASH_JOIN";
            } else {
                return "SORT_MERGE";
            }
        }
    }
}
