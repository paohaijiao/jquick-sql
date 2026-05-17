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


/**
 * 集群配置
 */
public class JQuickClusterConfig {

    private final int heartbeatIntervalMs;

    private final int heartbeatTimeoutMs;

    private final int loadBalanceThreshold;

    private final int maxConcurrentTasksPerWorker;

    private final int defaultParallelism;

    private final boolean enableDataLocality;

    private final boolean enableRackLocality;

    private JQuickClusterConfig(Builder builder) {
        this.heartbeatIntervalMs = builder.heartbeatIntervalMs;
        this.heartbeatTimeoutMs = builder.heartbeatTimeoutMs;
        this.loadBalanceThreshold = builder.loadBalanceThreshold;
        this.maxConcurrentTasksPerWorker = builder.maxConcurrentTasksPerWorker;
        this.defaultParallelism = builder.defaultParallelism;
        this.enableDataLocality = builder.enableDataLocality;
        this.enableRackLocality = builder.enableRackLocality;
    }

    public JQuickClusterConfig() {
        this(new Builder());
    }

    public int getHeartbeatIntervalMs() { return heartbeatIntervalMs; }

    public int getHeartbeatTimeoutMs() { return heartbeatTimeoutMs; }

    public int getLoadBalanceThreshold() { return loadBalanceThreshold; }

    public int getMaxConcurrentTasksPerWorker() { return maxConcurrentTasksPerWorker; }

    public int getDefaultParallelism() { return defaultParallelism; }

    public boolean isEnableDataLocality() { return enableDataLocality; }

    public boolean isEnableRackLocality() { return enableRackLocality; }

    public static class Builder {

        private int heartbeatIntervalMs = 5000;

        private int heartbeatTimeoutMs = 30000;

        private int loadBalanceThreshold = 2;

        private int maxConcurrentTasksPerWorker = 4;

        private int defaultParallelism = 4;

        private boolean enableDataLocality = true;

        private boolean enableRackLocality = true;

        public Builder heartbeatIntervalMs(int interval) {
            this.heartbeatIntervalMs = interval;
            return this;
        }

        public Builder heartbeatTimeoutMs(int timeout) {
            this.heartbeatTimeoutMs = timeout;
            return this;
        }

        public Builder loadBalanceThreshold(int threshold) {
            this.loadBalanceThreshold = threshold;
            return this;
        }

        public Builder maxConcurrentTasksPerWorker(int max) {
            this.maxConcurrentTasksPerWorker = max;
            return this;
        }

        public Builder defaultParallelism(int parallelism) {
            this.defaultParallelism = parallelism;
            return this;
        }

        public Builder enableDataLocality(boolean enable) {
            this.enableDataLocality = enable;
            return this;
        }

        public Builder enableRackLocality(boolean enable) {
            this.enableRackLocality = enable;
            return this;
        }

        public JQuickClusterConfig build() {
            return new JQuickClusterConfig(this);
        }
    }
}
