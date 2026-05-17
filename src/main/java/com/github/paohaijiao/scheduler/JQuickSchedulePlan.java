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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 调度计划 - 包含所有 Task 和依赖关系
 */
public class JQuickSchedulePlan {
    private final Collection<JQuickTask> allTasks;
    private final List<JQuickTask> rootTasks;
    private final Map<Integer, List<Integer>> stageDependencies;
    private final List<WorkerInfo> workers;
    private final long planId;

    public JQuickSchedulePlan(Collection<JQuickTask> allTasks,
                              List<JQuickTask> rootTasks,
                              Map<Integer, List<Integer>> stageDependencies,
                              List<WorkerInfo> workers) {
        this.planId = System.currentTimeMillis();
        this.allTasks = allTasks;
        this.rootTasks = rootTasks;
        this.stageDependencies = stageDependencies;
        this.workers = workers;
    }

    public Collection<JQuickTask> getAllTasks() { return allTasks; }
    public List<JQuickTask> getRootTasks() { return rootTasks; }
    public List<JQuickTask> getTasksByWorker(String workerId) {
        List<JQuickTask> result = new ArrayList<>();
        for (JQuickTask task : allTasks) {
            if (workerId.equals(task.getAssignedWorker())) {
                result.add(task);
            }
        }
        return result;
    }

    public Map<Integer, List<Integer>> getStageDependencies() { return stageDependencies; }
    public List<WorkerInfo> getWorkers() { return workers; }
    public long getPlanId() { return planId; }

    @Override
    public String toString() {
        return String.format("SchedulePlan{id=%d, tasks=%d, workers=%d}", planId, allTasks.size(), workers.size());
    }
}
