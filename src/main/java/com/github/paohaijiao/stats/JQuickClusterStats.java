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
package com.github.paohaijiao.stats;


/**
 * 集群统计信息
 */
public class JQuickClusterStats {

    private final int totalWorkers;

    private final int healthyWorkers;

    private final long totalCpuCores;

    private final long totalMemoryBytes;

    private final long totalCompletedTasks;

    public JQuickClusterStats(int totalWorkers, int healthyWorkers, long totalCpuCores, long totalMemoryBytes) {
        this(totalWorkers, healthyWorkers, totalCpuCores, totalMemoryBytes, 0);
    }

    public JQuickClusterStats(int totalWorkers, int healthyWorkers, long totalCpuCores, long totalMemoryBytes, long totalCompletedTasks) {
        this.totalWorkers = totalWorkers;
        this.healthyWorkers = healthyWorkers;
        this.totalCpuCores = totalCpuCores;
        this.totalMemoryBytes = totalMemoryBytes;
        this.totalCompletedTasks = totalCompletedTasks;
    }

    public int getTotalWorkers() { return totalWorkers; }

    public int getHealthyWorkers() { return healthyWorkers; }

    public long getTotalCpuCores() { return totalCpuCores; }

    public long getTotalMemoryBytes() { return totalMemoryBytes; }

    public long getTotalCompletedTasks() { return totalCompletedTasks; }

    public double getAverageCpuCores() {
        return totalWorkers > 0 ? (double) totalCpuCores / totalWorkers : 0;
    }

    public double getAverageMemoryBytes() {
        return totalWorkers > 0 ? (double) totalMemoryBytes / totalWorkers : 0;
    }

    public double getHealthyRatio() {
        return totalWorkers > 0 ? (double) healthyWorkers / totalWorkers : 0;
    }

    @Override
    public String toString() {
        return String.format("ClusterStats{workers=%d/%d, cpu=%d, memory=%d MB, completed=%d}", healthyWorkers, totalWorkers, totalCpuCores, totalMemoryBytes / (1024 * 1024), totalCompletedTasks);
    }
}
