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
package com.github.paohaijiao.distributed.domain;


import com.github.paohaijiao.physical.JQuickPhysicalPlanNode;
import com.github.paohaijiao.physical.node.JQuickExchangePhysicalNode;
import com.github.paohaijiao.physical.node.JQuickHashAggregatePhysicalNode;
import com.github.paohaijiao.physical.node.JQuickHashJoinPhysicalNode;
import com.github.paohaijiao.physical.node.JQuickTableScanPhysicalNode;
import com.github.paohaijiao.toplogy.JQuickClusterTopology;

/**
 * 成本估算器 - 用于调度决策
 */
public class CostEstimator {

    private final JQuickClusterTopology cluster;

    public CostEstimator(JQuickClusterTopology cluster) {
        this.cluster = cluster;
    }

    /**
     * 估算节点执行成本
     */
    public double estimateCost(JQuickPhysicalPlanNode node) {
        if (node instanceof JQuickTableScanPhysicalNode) {
            return estimateScanCost((JQuickTableScanPhysicalNode) node);
        } else if (node instanceof JQuickHashJoinPhysicalNode) {
            return estimateJoinCost((JQuickHashJoinPhysicalNode) node);
        } else if (node instanceof JQuickHashAggregatePhysicalNode) {
            return estimateAggregateCost((JQuickHashAggregatePhysicalNode) node);
        } else if (node instanceof JQuickExchangePhysicalNode) {
            return estimateExchangeCost((JQuickExchangePhysicalNode) node);
        } else {
            return node.getStats().getEstimatedRowCount();
        }
    }

    private double estimateScanCost(JQuickTableScanPhysicalNode scan) {
        long rowCount = scan.getStats().getEstimatedRowCount();
        // 考虑分区并行带来的加速
        int parallelism = cluster.getConfig().getDefaultParallelism();
        return (double) rowCount / parallelism;
    }

    private double estimateJoinCost(JQuickHashJoinPhysicalNode join) {
        long leftRows = join.getLeft().getStats().getEstimatedRowCount();
        long rightRows = join.getRight().getStats().getEstimatedRowCount();

        // Hash Join 成本: 构建哈希表 + 探测
        return leftRows + rightRows;
    }

    private double estimateAggregateCost(JQuickHashAggregatePhysicalNode agg) {
        long inputRows = agg.getChild().getStats().getEstimatedRowCount();
        long estimatedGroups = agg.getStats().getEstimatedRowCount();

        // 聚合成本: 遍历输入行 + 分组维护
        return inputRows + estimatedGroups;
    }

    private double estimateExchangeCost(JQuickExchangePhysicalNode exchange) {
        long dataSize = exchange.getChild().getStats().getEstimatedDataSize();

        // 网络传输成本
        double networkCost = dataSize / (100 * 1024 * 1024.0); // 假设100MB/s

        if (exchange.getExchangeType() == JQuickExchangePhysicalNode.ExchangeType.BROADCAST) {
            networkCost *= cluster.getAvailableWorkers();
        }

        return networkCost;
    }
}
