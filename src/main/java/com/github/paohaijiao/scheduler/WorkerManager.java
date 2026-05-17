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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Worker 管理器
 */
public class WorkerManager {
    private final Map<String, WorkerInfo> workers;
    private final WorkerDiscovery discovery;

    public WorkerManager() {
        this.workers = new ConcurrentHashMap<>();
        this.discovery = new WorkerDiscovery(this);
    }

    /**
     * 注册 Worker
     */
    public void registerWorker(WorkerInfo worker) {
        workers.put(worker.getWorkerId(), worker);
        System.out.println("Worker registered: " + worker.getWorkerId());
    }

    /**
     * 注销 Worker
     */
    public void unregisterWorker(String workerId) {
        workers.remove(workerId);
        System.out.println("Worker unregistered: " + workerId);
    }

    /**
     * 获取可用的 Worker 列表
     */
    public List<WorkerInfo> getAvailableWorkers() {
        return workers.values().stream()
                .filter(w -> w.getStatus() == WorkerInfo.WorkerStatus.HEALTHY)
                .filter(w -> w.getAvailableSlots() > 0)
                .collect(Collectors.toList());
    }

    /**
     * 获取所有 Worker
     */
    public List<WorkerInfo> getWorkers() {
        return new ArrayList<>(workers.values());
    }

    /**
     * 获取 Worker 信息
     */
    public WorkerInfo getWorker(String workerId) {
        return workers.get(workerId);
    }

    /**
     * 更新 Worker 状态
     */
    public void updateWorkerStatus(String workerId, WorkerInfo.WorkerStatus status) {
        WorkerInfo worker = workers.get(workerId);
        if (worker != null) {
            worker.setStatus(status);
        }
    }

    /**
     * 启动 Worker 发现服务
     */
    public void startDiscovery(int port) {
        discovery.start(port);
    }

    /**
     * 停止 Worker 发现服务
     */
    public void stopDiscovery() {
        discovery.stop();
    }
}
