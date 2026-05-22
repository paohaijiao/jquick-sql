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


/**
 * Worker 节点信息
 */
public class WorkerInfo {

    private final String workerId;

    private final String host;

    private final int controlPort;

    private final int dataPort;

    private final int totalSlots;

    private int runningTasks;

    private WorkerStatus status;

    public enum WorkerStatus {
        HEALTHY, UNHEALTHY, OFFLINE
    }

    public WorkerInfo(String workerId, String host, int controlPort, int dataPort, int totalSlots) {
        this.workerId = workerId;
        this.host = host;
        this.controlPort = controlPort;
        this.dataPort = dataPort;
        this.totalSlots = totalSlots;
        this.runningTasks = 0;
        this.status = WorkerStatus.HEALTHY;
    }

    public void incrementRunningTasks() { runningTasks++; }

    public void decrementRunningTasks() { runningTasks--; }

    public String getWorkerId() { return workerId; }

    public String getHost() { return host; }

    public int getControlPort() { return controlPort; }

    public int getDataPort() { return dataPort; }

    public int getTotalSlots() { return totalSlots; }

    public int getRunningTasks() { return runningTasks; }

    public int getAvailableSlots() { return totalSlots - runningTasks; }

    public WorkerStatus getStatus() { return status; }

    public void setStatus(WorkerStatus status) { this.status = status; }
}
