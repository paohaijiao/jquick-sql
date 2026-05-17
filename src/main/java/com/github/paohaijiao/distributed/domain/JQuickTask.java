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

import java.util.*;

/**
 * 执行任务 - 调度器分配给Worker的最小执行单元
 */
public class JQuickTask {

    private final String taskId;
    private final int stageId;
    private final String workerId;
    private final JQuickPhysicalPlanNode physicalPlan;
    private final Map<String, Object> context;
    private final List<Integer> upstreamStageIds;
    private final JQuickExchangePhysicalNode.ExchangeType exchangeType;
    private final JQuickExchangePhysicalNode.PartitionStrategy partitionStrategy;

    public JQuickTask(String taskId, int stageId, String workerId,
                      JQuickPhysicalPlanNode physicalPlan,
                      Map<String, Object> context,
                      List<Integer> upstreamStageIds,
                      JQuickExchangePhysicalNode.ExchangeType exchangeType,
                      JQuickExchangePhysicalNode.PartitionStrategy partitionStrategy) {
        this.taskId = taskId;
        this.stageId = stageId;
        this.workerId = workerId;
        this.physicalPlan = physicalPlan;
        this.context = context != null ? new HashMap<>(context) : new HashMap<>();
        this.upstreamStageIds = upstreamStageIds != null ? new ArrayList<>(upstreamStageIds) : new ArrayList<>();
        this.exchangeType = exchangeType;
        this.partitionStrategy = partitionStrategy;
    }

    public String getTaskId() {
        return taskId;
    }

    public int getStageId() {
        return stageId;
    }

    public String getWorkerId() {
        return workerId;
    }

    public JQuickPhysicalPlanNode getPhysicalPlan() {
        return physicalPlan;
    }

    public Map<String, Object> getContext() {
        return Collections.unmodifiableMap(context);
    }

    public List<Integer> getUpstreamStageIds() {
        return upstreamStageIds;
    }

    public JQuickExchangePhysicalNode.ExchangeType getExchangeType() {
        return exchangeType;
    }

    public JQuickExchangePhysicalNode.PartitionStrategy getPartitionStrategy() {
        return partitionStrategy;
    }

    public boolean hasExchange() {
        return exchangeType != null;
    }

    public <T> T getContextValue(String key, Class<T> type) {
        Object value = context.get(key);
        if (value != null && type.isAssignableFrom(value.getClass())) {
            return type.cast(value);
        }
        return null;
    }

    @Override
    public String toString() {
        return String.format("Task{id='%s', stage=%d, worker='%s', exchange=%s}",
                taskId, stageId, workerId, exchangeType);
    }
}
