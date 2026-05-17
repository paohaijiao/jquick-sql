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

import com.github.paohaijiao.physical.domain.JQuickPhysicalColumn;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 分布式执行计划
 */
public class JQuickExecutionPlan {

    private final List<JQuickTask> tasks;

    private final Map<String, List<JQuickTask>> tasksByWorker;

    private final Map<Integer, ExecutionStage> stages;

    private final Map<Integer, List<Integer>> dependencies;

    private final List<JQuickPhysicalColumn> outputSchema;

    private final long timeout;

    private final TimeUnit timeoutUnit;

    public JQuickExecutionPlan(List<JQuickTask> tasks, List<JQuickPhysicalColumn> outputSchema, List<ExecutionStage> stages, Map<Integer, List<Integer>> dependencies) {
        this.tasks = tasks;
        this.outputSchema = outputSchema;
        this.dependencies = dependencies;
        this.tasksByWorker = new HashMap<>();
        this.stages = new HashMap<>();
        for (JQuickTask task : tasks) {
            tasksByWorker.computeIfAbsent(task.getWorkerId(), k -> new ArrayList<>()).add(task);
        }
        for (ExecutionStage stage : stages) {
            this.stages.put(stage.getStageId(), stage);
        }
        this.timeout = 3600;
        this.timeoutUnit = TimeUnit.SECONDS;
    }

    public List<JQuickTask> getTasks() {
        return tasks;
    }

    public List<JQuickTask> getTasksForWorker(String workerId) {
        return tasksByWorker.getOrDefault(workerId, Collections.emptyList());
    }

    public ExecutionStage getStage(int stageId) {
        return stages.get(stageId);
    }

    public List<Integer> getDependencies(int stageId) {
        return dependencies.getOrDefault(stageId, Collections.emptyList());
    }

    public List<JQuickPhysicalColumn> getOutputSchema() {
        return outputSchema;
    }

    public long getTimeout() {
        return timeout;
    }

    public TimeUnit getTimeoutUnit() {
        return timeoutUnit;
    }

    /**
     * 获取Stage执行顺序（拓扑排序）
     */
    public List<Integer> getStageExecutionOrder() {

        List<Integer> order = new ArrayList<>();

        Set<Integer> visited = new HashSet<>();

        for (Integer stageId : stages.keySet()) {
            topologicalSort(stageId, visited, order);
        }

        return order;
    }

    private void topologicalSort(int stageId, Set<Integer> visited, List<Integer> order) {
        if (visited.contains(stageId)) return;
        visited.add(stageId);
        List<Integer> deps = dependencies.getOrDefault(stageId, Collections.emptyList());
        for (int dep : deps) {
            topologicalSort(dep, visited, order);
        }
        order.add(stageId);
    }

    @Override
    public String toString() {
        return String.format("ExecutionPlan{stages=%d, tasks=%d, workers=%d}", stages.size(), tasks.size(), tasksByWorker.size());
    }
}
