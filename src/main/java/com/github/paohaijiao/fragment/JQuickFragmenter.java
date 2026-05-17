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
package com.github.paohaijiao.fragment;

import com.github.paohaijiao.distributed.JQuickDistributedPlan;
import com.github.paohaijiao.exchange.ExchangeNode;
import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.physical.JQuickPhysicalPlanNode;
import com.github.paohaijiao.physical.node.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 计划切分器 - 将物理计划切分为分布式片段
 */
public class JQuickFragmenter {

    private final int defaultParallelism;

    private JQuickFragment currentFragment;

    private final Map<JQuickPhysicalPlanNode, JQuickFragment> nodeToFragment = new HashMap<>();

    public JQuickFragmenter() {
        this(4);
    }

    public JQuickFragmenter(int defaultParallelism) {
        this.defaultParallelism = defaultParallelism;
    }

    /**
     * 将物理计划切分为分布式计划
     */
    public JQuickDistributedPlan fragment(JQuickPhysicalPlanNode rootPlan) {
        // 创建根片段
        JQuickFragment rootFragment = new JQuickFragment(JQuickFragment.FragmentType.SINK, rootPlan);
        currentFragment = rootFragment;
        // 递归处理计划树
        processNode(rootPlan, rootFragment);

        // 构建分布式计划
        JQuickDistributedPlan plan = new JQuickDistributedPlan(rootFragment);
        plan.setDefaultParallelism(defaultParallelism);

        return plan;
    }

    /**
     * 处理物理计划节点，决定是否需要切分片段
     */
    private void processNode(JQuickPhysicalPlanNode node, JQuickFragment fragment) {
        nodeToFragment.put(node, fragment);
        // 根据节点类型决定是否需要切分
        if (needsExchange(node)) {
            // 需要数据交换，创建新的片段
            createExchangeFragment(node, fragment);
        } else {
            // 继续在当前片段中处理子节点
            for (JQuickPhysicalPlanNode child : getChildren(node)) {
                processNode(child, fragment);
            }
        }
    }

    /**
     * 判断节点是否需要数据交换
     */
    private boolean needsExchange(JQuickPhysicalPlanNode node) {
        // JOIN 操作需要数据交换（除非是广播连接）
        if (node instanceof JQuickHashJoinPhysicalNode || node instanceof JQuickNestedLoopJoinPhysicalNode) {
            return true;
        }

        // 聚合操作需要数据交换
        if (node instanceof JQuickHashAggregatePhysicalNode || node instanceof SortAggregatePhysicalNode) {
            return true;
        }

        // 排序操作如果数据量大，需要交换
        if (node instanceof ExternalSortPhysicalNode) {
            return true;
        }

        return false;
    }

    /**
     * 创建交换片段
     */
    private void createExchangeFragment(JQuickPhysicalPlanNode node, JQuickFragment parentFragment) {
        // 确定分区策略
        ExchangeNode exchange = createExchange(node);

        // 创建新的片段处理子节点
        JQuickFragment childFragment = new JQuickFragment(JQuickFragment.FragmentType.INTERMEDIATE, node);
        childFragment.setParallelism(defaultParallelism);
        childFragment.setOutput(exchange);

        // 建立父子关系
        parentFragment.addChild(childFragment);
        childFragment.addInput(exchange);

        // 递归处理子节点
        currentFragment = childFragment;
        for (JQuickPhysicalPlanNode child : getChildren(node)) {
            processNode(child, childFragment);
        }
        currentFragment = parentFragment;
    }

    /**
     * 创建数据交换节点
     */
    private ExchangeNode createExchange(JQuickPhysicalPlanNode node) {
        if (node instanceof JQuickHashJoinPhysicalNode) {
            // 哈希连接需要根据连接键进行哈希重分区
            JQuickHashJoinPhysicalNode join = (JQuickHashJoinPhysicalNode) node;
            return new ExchangeNode(
                    ExchangeNode.ExchangeType.SHUFFLE,
                    ExchangeNode.PartitionStrategy.HASH,
                    extractJoinKey(join),
                    defaultParallelism
            );
        }

        if (node instanceof JQuickHashAggregatePhysicalNode) {
            // 聚合需要根据分组键进行哈希重分区
            JQuickHashAggregatePhysicalNode agg = (JQuickHashAggregatePhysicalNode) node;
            return new ExchangeNode(
                    ExchangeNode.ExchangeType.SHUFFLE,
                    ExchangeNode.PartitionStrategy.HASH,
                    extractGroupKey(agg),
                    defaultParallelism
            );
        }

        // 默认：广播
        return new ExchangeNode(ExchangeNode.ExchangeType.BROADCAST, ExchangeNode.PartitionStrategy.REPLICATE, null, defaultParallelism);
    }

    /**
     * 提取连接键
     */
    private JQuickExpression extractJoinKey(JQuickHashJoinPhysicalNode join) {
        // 从连接条件中提取连接键
        return null;
    }

    /**
     * 提取分组键
     */
    private JQuickExpression extractGroupKey(JQuickHashAggregatePhysicalNode agg) {
        // 提取分组键
        return null;
    }

    /**
     * 获取物理节点的子节点
     */
    private List<JQuickPhysicalPlanNode> getChildren(JQuickPhysicalPlanNode node) {
        // 简化实现，实际需要根据节点类型返回子节点
        return new ArrayList<>();
    }
}
