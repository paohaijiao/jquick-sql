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

import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.physical.JQuickPhysicalPlanNode;
import com.github.paohaijiao.physical.node.JQuickExchangePhysicalNode;
import com.github.paohaijiao.worker.JQuickWorkerAssignment;

import java.util.ArrayList;
import java.util.List;

/**
 * 执行阶段 - 一组可以并行执行的任务
 */
public class ExecutionStage {

    private final int stageId;
    private final List<Integer> inputStageIds;
    private JQuickPhysicalPlanNode rootNode;
    private int parentStageId;
    private int parallelism;
    private List<JQuickWorkerAssignment> workerAssignments;

    // Exchange相关
    private JQuickExchangePhysicalNode.ExchangeType exchangeType;
    private JQuickExchangePhysicalNode.PartitionStrategy partitionStrategy;
    private List<JQuickExpression> partitionKeys;
    private int targetParallelism;

    public ExecutionStage(int stageId, JQuickPhysicalPlanNode rootNode) {
        this.stageId = stageId;
        this.rootNode = rootNode;
        this.inputStageIds = new ArrayList<>();
        this.parentStageId = -1;
        this.parallelism = 1;
        this.workerAssignments = new ArrayList<>();
    }

    public int getStageId() {
        return stageId;
    }

    public JQuickPhysicalPlanNode getRootNode() {
        return rootNode;
    }

    public void setRootNode(JQuickPhysicalPlanNode node) {
        this.rootNode = node;
    }

    public List<Integer> getInputStageIds() {
        return inputStageIds;
    }

    public void setInputStageIds(List<Integer> stageIds) {
        this.inputStageIds.clear();
        this.inputStageIds.addAll(stageIds);
    }

    public void addInputStage(int stageId) {
        this.inputStageIds.add(stageId);
    }

    public int getParentStageId() {
        return parentStageId;
    }

    public void setParentStageId(int parentStageId) {
        this.parentStageId = parentStageId;
    }

    public int getParallelism() {
        return parallelism;
    }

    public void setParallelism(int parallelism) {
        this.parallelism = parallelism;
    }

    public List<JQuickWorkerAssignment> getWorkerAssignments() {
        return workerAssignments;
    }

    public void setWorkerAssignments(List<JQuickWorkerAssignment> assignments) {
        this.workerAssignments = assignments;
    }

    public JQuickExchangePhysicalNode.ExchangeType getExchangeType() {
        return exchangeType;
    }

    public void setExchangeType(JQuickExchangePhysicalNode.ExchangeType type) {
        this.exchangeType = type;
    }

    public JQuickExchangePhysicalNode.PartitionStrategy getPartitionStrategy() {
        return partitionStrategy;
    }

    public void setPartitionStrategy(JQuickExchangePhysicalNode.PartitionStrategy strategy) {
        this.partitionStrategy = strategy;
    }

    public List<JQuickExpression> getPartitionKeys() {
        return partitionKeys;
    }

    public void setPartitionKeys(List<JQuickExpression> keys) {
        this.partitionKeys = keys;
    }

    public int getTargetParallelism() {
        return targetParallelism;
    }

    public void setTargetParallelism(int parallelism) {
        this.targetParallelism = parallelism;
    }

    public boolean hasExchange() {
        return exchangeType != null;
    }

    @Override
    public String toString() {
        return String.format("Stage{id=%d, parallelism=%d, inputs=%s, exchange=%s}",
                stageId, parallelism, inputStageIds, exchangeType);
    }
}
