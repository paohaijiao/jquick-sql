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
package com.github.paohaijiao.config;

import lombok.Data;

/**
 * packageName io.github.paohaijiao.config
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/9/5
 */
@Data
public class WorkerEndpoint {

    private final String workerId;

    private final String host;

    private final int port;

    private final int index;

    private volatile boolean healthy;

    private volatile long lastHeartbeat;

    public WorkerEndpoint(String workerId, String host, int port, int index) {
        this.workerId = workerId;
        this.host = host;
        this.port = port;
        this.index = index;
        this.healthy = true;
        this.lastHeartbeat = System.currentTimeMillis();
    }

    public String getWorkerId() {
        return workerId;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public int getIndex() {
        return index;
    }

    public boolean isHealthy() {
        return healthy;
    }

    public void setHealthy(boolean healthy) {
        this.healthy = healthy;
    }

    public void updateHeartbeat() {
        this.lastHeartbeat = System.currentTimeMillis();
    }

    public long getLastHeartbeat() {
        return lastHeartbeat;
    }

    @Override
    public String toString() {
        return String.format("WorkerEndpoint{id=%s, host=%s:%d, healthy=%s}", workerId, host, port, healthy);
    }
}
