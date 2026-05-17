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
package com.github.paohaijiao.worker;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Worker节点信息
 */
public class JQuickWorkerNode {

    private final String workerId;

    private final String host;

    private final int port;

    private final String rack;

    private final String location;

    private final int cpuCores;

    private final long memoryBytes;

    private final Map<String, String> attributes;

    private final AtomicBoolean healthy;

    private final AtomicLong lastHeartbeat;

    private final AtomicLong currentLoad;

    private final AtomicLong completedTasks;

    public JQuickWorkerNode(String workerId, String host, int port) {
        this(workerId, host, port, null, null, Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().maxMemory(), new HashMap<>());
    }

    public JQuickWorkerNode(String workerId, String host, int port, String rack, String location, int cpuCores, long memoryBytes, Map<String, String> attributes) {
        this.workerId = workerId;
        this.host = host;
        this.port = port;
        this.rack = rack;
        this.location = location;
        this.cpuCores = cpuCores;
        this.memoryBytes = memoryBytes;
        this.attributes = attributes != null ? new HashMap<>(attributes) : new HashMap<>();
        this.healthy = new AtomicBoolean(true);
        this.lastHeartbeat = new AtomicLong(System.currentTimeMillis());
        this.currentLoad = new AtomicLong(0);
        this.completedTasks = new AtomicLong(0);
    }

    public String getWorkerId() { return workerId; }

    public String getHost() { return host; }

    public int getPort() { return port; }

    public String getRack() { return rack; }

    public String getLocation() { return location; }

    public int getCpuCores() { return cpuCores; }

    public long getMemoryBytes() { return memoryBytes; }

    public Map<String, String> getAttributes() { return Collections.unmodifiableMap(attributes); }

    public boolean isHealthy() { return healthy.get(); }

    public void setHealthy(boolean healthy) { this.healthy.set(healthy); }

    public long getLastHeartbeat() { return lastHeartbeat.get(); }

    public void updateHeartbeat() {
        lastHeartbeat.set(System.currentTimeMillis());
        healthy.set(true);
    }

    public long getCurrentLoad() { return currentLoad.get(); }

    public void setCurrentLoad(long load) { currentLoad.set(load); }

    public void incrementLoad() { currentLoad.incrementAndGet(); }

    public void decrementLoad() { currentLoad.decrementAndGet(); }

    public long getCompletedTasks() { return completedTasks.get(); }

    public void incrementCompletedTasks() { completedTasks.incrementAndGet(); }

    public String getAddress() {
        return host + ":" + port;
    }

    @Override
    public String toString() {
        return String.format("WorkerNode{id='%s', address=%s, healthy=%s, load=%d}", workerId, getAddress(), healthy.get(), currentLoad.get());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JQuickWorkerNode that = (JQuickWorkerNode) o;
        return Objects.equals(workerId, that.workerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(workerId);
    }
}
