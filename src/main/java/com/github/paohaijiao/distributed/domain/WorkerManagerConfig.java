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

/**
 * Worker管理器配置
 */
public class WorkerManagerConfig {

    private int heartbeatIntervalMs;

    private int heartbeatTimeoutMs;

    private int maxRetries;

    private long taskTimeoutMs;

    private int maxConcurrentTasksPerWorker;

    private boolean enableAutoRecovery;

    public WorkerManagerConfig() {
        this.heartbeatIntervalMs = 5000;
        this.heartbeatTimeoutMs = 30000;
        this.maxRetries = 3;
        this.taskTimeoutMs = 3600000; // 1小时
        this.maxConcurrentTasksPerWorker = 4;
        this.enableAutoRecovery = true;
    }

    public int getHeartbeatIntervalMs() {
        return heartbeatIntervalMs;
    }

    public WorkerManagerConfig setHeartbeatIntervalMs(int intervalMs) {
        this.heartbeatIntervalMs = intervalMs;
        return this;
    }

    public int getHeartbeatTimeoutMs() {
        return heartbeatTimeoutMs;
    }

    public WorkerManagerConfig setHeartbeatTimeoutMs(int timeoutMs) {
        this.heartbeatTimeoutMs = timeoutMs;
        return this;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public WorkerManagerConfig setMaxRetries(int maxRetries) {
        this.maxRetries = maxRetries;
        return this;
    }

    public long getTaskTimeoutMs() {
        return taskTimeoutMs;
    }

    public WorkerManagerConfig setTaskTimeoutMs(long timeoutMs) {
        this.taskTimeoutMs = timeoutMs;
        return this;
    }

    public int getMaxConcurrentTasksPerWorker() {
        return maxConcurrentTasksPerWorker;
    }

    public WorkerManagerConfig setMaxConcurrentTasksPerWorker(int max) {
        this.maxConcurrentTasksPerWorker = max;
        return this;
    }

    public boolean isEnableAutoRecovery() {
        return enableAutoRecovery;
    }

    public WorkerManagerConfig setEnableAutoRecovery(boolean enable) {
        this.enableAutoRecovery = enable;
        return this;
    }

    public WorkerManagerConfig copy() {
        return new WorkerManagerConfig()
                .setHeartbeatIntervalMs(heartbeatIntervalMs)
                .setHeartbeatTimeoutMs(heartbeatTimeoutMs)
                .setMaxRetries(maxRetries)
                .setTaskTimeoutMs(taskTimeoutMs)
                .setMaxConcurrentTasksPerWorker(maxConcurrentTasksPerWorker)
                .setEnableAutoRecovery(enableAutoRecovery);
    }
}
