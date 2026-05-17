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

import com.github.paohaijiao.toplogy.JQuickClusterTopology;

import java.util.*;

/**
 * 任务规划器 - 负责任务的依赖排序和资源分配
 */
public class TaskPlanner {

    private final JQuickClusterTopology cluster;

    public TaskPlanner(JQuickClusterTopology cluster) {
        this.cluster = cluster;
    }

    /**
     * 规划任务执行顺序
     */
    public List<List<JQuickTask>> planExecutionOrder(List<JQuickTask> tasks, Map<Integer, List<Integer>> dependencies) {
        // 按Stage分组
        Map<Integer, List<JQuickTask>> tasksByStage = new HashMap<>();
        for (JQuickTask task : tasks) {
            tasksByStage.computeIfAbsent(task.getStageId(), k -> new ArrayList<>()).add(task);
        }

        // 拓扑排序Stage
        List<Integer> stageOrder = topologicalSortStage(dependencies);

        // 按Stage顺序组织任务
        List<List<JQuickTask>> executionOrder = new ArrayList<>();
        for (int stageId : stageOrder) {
            List<JQuickTask> stageTasks = tasksByStage.get(stageId);
            if (stageTasks != null && !stageTasks.isEmpty()) {
                executionOrder.add(stageTasks);
            }
        }

        return executionOrder;
    }

    private List<Integer> topologicalSortStage(Map<Integer, List<Integer>> dependencies) {
        List<Integer> order = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();

        for (Integer stageId : dependencies.keySet()) {
            dfs(stageId, dependencies, visited, order);
        }

        return order;
    }

    private void dfs(int stageId, Map<Integer, List<Integer>> dependencies,
                     Set<Integer> visited, List<Integer> order) {
        if (visited.contains(stageId)) return;
        visited.add(stageId);

        List<Integer> deps = dependencies.getOrDefault(stageId, Collections.emptyList());
        for (int dep : deps) {
            dfs(dep, dependencies, visited, order);
        }

        order.add(stageId);
    }
}
