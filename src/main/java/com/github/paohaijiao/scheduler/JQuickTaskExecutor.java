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
package com.github.paohaijiao.scheduler;


import com.github.paohaijiao.physical.JQuickPhysicalPlanNode;

import java.util.*;

/**
 * 任务执行器 - 执行具体的物理计划节点
 */
public class JQuickTaskExecutor {
    private final JQuickTask task;
    private final DataExchangeService dataExchange;
    private final JQuickPhysicalPlanNode plan;

    public JQuickTaskExecutor(JQuickTask task, DataExchangeService dataExchange) {
        this.task = task;
        this.dataExchange = dataExchange;
        this.plan = task.getFragment().getPlan();
    }

    /**
     * 执行任务
     */
    public void execute() throws Exception {
        System.out.println("Executing task: " + task);

        // 1. 准备输入数据
        Map<String, Iterator<Object>> inputs = prepareInputs();

        // 2. 执行物理计划
        Iterator<Object> result = executePlan(plan, inputs);

        // 3. 输出结果
        if (task.getOutput() != null) {
            emitResults(result);
        }
    }

    /**
     * 准备输入数据
     */
    private Map<String, Iterator<Object>> prepareInputs() {
        Map<String, Iterator<Object>> inputs = new HashMap<>();

        for (JQuickTaskInput input : task.getInputs()) {
            // 从数据交换服务获取输入流
            Iterator<Object> dataStream = dataExchange.receive(input.getChannel());
            inputs.put(input.getInputId(), dataStream);
        }

        return inputs;
    }

    /**
     * 执行计划节点
     */
    private Iterator<Object> executePlan(JQuickPhysicalPlanNode node, Map<String, Iterator<Object>> inputs) {
        // 根据节点类型执行不同的逻辑
        String nodeType = node.getNodeType();

        switch (nodeType) {
            case "TableScan":
                return executeTableScan(node, inputs);
            case "Filter":
                return executeFilter(node, inputs);
            case "Project":
                return executeProject(node, inputs);
            case "HashJoin":
                return executeHashJoin(node, inputs);
            case "HashAggregate":
                return executeHashAggregate(node, inputs);
            case "Sort":
                return executeSort(node, inputs);
            case "Limit":
                return executeLimit(node, inputs);
            default:
                throw new UnsupportedOperationException("Unsupported node type: " + nodeType);
        }
    }

    /**
     * 执行表扫描
     */
    private Iterator<Object> executeTableScan(JQuickPhysicalPlanNode node, Map<String, Iterator<Object>> inputs) {
        // 实现表扫描逻辑
        return Collections.emptyIterator();
    }

    /**
     * 执行 Filter
     */
    private Iterator<Object> executeFilter(JQuickPhysicalPlanNode node, Map<String, Iterator<Object>> inputs) {
        // 获取子节点输入
        Iterator<Object> childInput = getChildInput(node, inputs);
        // 应用过滤条件
        return new FilterIterator(childInput, node);
    }

    /**
     * 执行 Project
     */
    private Iterator<Object> executeProject(JQuickPhysicalPlanNode node, Map<String, Iterator<Object>> inputs) {
        Iterator<Object> childInput = getChildInput(node, inputs);
        return new ProjectIterator(childInput, node);
    }

    /**
     * 执行 Hash Join
     */
    private Iterator<Object> executeHashJoin(JQuickPhysicalPlanNode node, Map<String, Iterator<Object>> inputs) {
        // 构建 Hash 表
        // 探测并输出结果
        return Collections.emptyIterator();
    }

    /**
     * 执行 Hash Aggregate
     */
    private Iterator<Object> executeHashAggregate(JQuickPhysicalPlanNode node, Map<String, Iterator<Object>> inputs) {
        Iterator<Object> childInput = getChildInput(node, inputs);
        // 聚合计算
        Map<Object, Object> aggResult = new HashMap<>();
        while (childInput.hasNext()) {
            Object row = childInput.next();
            // 执行聚合逻辑
        }
        return aggResult.values().iterator();
    }

    /**
     * 执行 Sort
     */
    private Iterator<Object> executeSort(JQuickPhysicalPlanNode node, Map<String, Iterator<Object>> inputs) {
        Iterator<Object> childInput = getChildInput(node, inputs);
        List<Object> rows = new ArrayList<>();
        childInput.forEachRemaining(rows::add);
        // 排序
        rows.sort((a, b) -> 0);  // 根据排序键比较
        return rows.iterator();
    }

    /**
     * 执行 Limit
     */
    private Iterator<Object> executeLimit(JQuickPhysicalPlanNode node, Map<String, Iterator<Object>> inputs) {
        Iterator<Object> childInput = getChildInput(node, inputs);
        return new LimitIterator(childInput, node);
    }

    /**
     * 获取子节点输入
     */
    private Iterator<Object> getChildInput(JQuickPhysicalPlanNode node, Map<String, Iterator<Object>> inputs) {
        // 获取节点的第一个子节点
        List<JQuickPhysicalPlanNode> children = node.getChildren();
        if (children.isEmpty()) {
            return Collections.emptyIterator();
        }
        return executePlan(children.get(0), inputs);
    }

    /**
     * 输出结果
     */
    private void emitResults(Iterator<Object> results) {
        while (results.hasNext()) {
            Object result = results.next();
            // 发送到输出通道
            for (JQuickExchangeChannel channel : task.getOutput().getChannels()) {
                dataExchange.send(channel, result);
            }
        }
        dataExchange.complete(task.getOutput());
    }

    // 内部迭代器类
    private static class FilterIterator implements Iterator<Object> {
        private final Iterator<Object> input;
        private final JQuickPhysicalPlanNode filter;
        private Object next;

        FilterIterator(Iterator<Object> input, JQuickPhysicalPlanNode filter) {
            this.input = input;
            this.filter = filter;
            advance();
        }

        private void advance() {
            while (input.hasNext()) {
                Object row = input.next();
                // 检查是否符合过滤条件
                if (true) { // 实际需要评估 predicate
                    next = row;
                    return;
                }
            }
            next = null;
        }

        @Override
        public boolean hasNext() { return next != null; }

        @Override
        public Object next() {
            Object result = next;
            advance();
            return result;
        }
    }

    private static class ProjectIterator implements Iterator<Object> {
        private final Iterator<Object> input;
        private final JQuickPhysicalPlanNode project;

        ProjectIterator(Iterator<Object> input, JQuickPhysicalPlanNode project) {
            this.input = input;
            this.project = project;
        }

        @Override
        public boolean hasNext() { return input.hasNext(); }

        @Override
        public Object next() {
            Object row = input.next();
            // 应用投影表达式
            return row;
        }
    }

    private static class LimitIterator implements Iterator<Object> {
        private final Iterator<Object> input;
        private int remaining;

        LimitIterator(Iterator<Object> input, JQuickPhysicalPlanNode limit) {
            this.input = input;
            // 从节点获取 limit 值
            this.remaining = 10;  // 实际需要从节点获取
        }

        @Override
        public boolean hasNext() {
            return remaining > 0 && input.hasNext();
        }

        @Override
        public Object next() {
            remaining--;
            return input.next();
        }
    }
}
