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


import com.github.paohaijiao.worker.JQuickWorkerNode;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * JQuick框架配置类
 */
public class JQuickConfiguration {

    private int defaultParallelism;

    private int maxTaskRetries;

    private long taskTimeoutMs;

    private String executionMode;

    private boolean enableAdaptiveParallelism;

    private boolean enableVectorizedExecution;

    private int batchSize;

    private String clusterName;

    private List<JQuickWorkerNode> workerNodes;

    private String serviceDiscoveryUrl;

    private String serviceDiscoveryType;

    private int serviceDiscoveryTimeoutMs;

    private int heartbeatIntervalMs;

    private int heartbeatTimeoutMs;

    private int healthCheckIntervalMs;

    private int maxHeartbeatMisses;

    private int maxConcurrentTasksPerWorker;

    private int defaultWorkerCpuCores;

    private long defaultWorkerMemoryMb;

    private String defaultWorkerRack;

    private String defaultWorkerLocation;

    private int networkTimeoutMs;

    private int networkRetryCount;

    private long networkRetryDelayMs;

    private int maxPacketSize;

    private boolean enableCompression;

    private boolean enableEncryption;

    private String tempDir;

    private long spillToDiskThresholdBytes;

    private long maxResultSetSizeBytes;

    private boolean enableDiskSpilling;

    private boolean enableJoinReorder;

    private boolean enablePredicatePushdown;

    private boolean enableConstantFolding;

    private boolean enableColumnPruning;

    private boolean enableLimitPushdown;

    private boolean enableAggregationPushdown;

    private int joinReorderMaxTables;

    private int broadcastJoinThresholdBytes;

    private boolean enableQueryLogging;

    private boolean enableMetricsCollection;

    private String metricsExporter;

    private String logLevel;

    private final Map<String, String> properties;

    private final Map<String, Object> extensions;


    public JQuickConfiguration() {
        // 初始化默认值
        this.defaultParallelism = Runtime.getRuntime().availableProcessors();
        this.maxTaskRetries = 3;
        this.taskTimeoutMs = TimeUnit.HOURS.toMillis(1);
        this.executionMode = "auto";
        this.enableAdaptiveParallelism = true;
        this.enableVectorizedExecution = true;
        this.batchSize = 1000;
        this.clusterName = "jquick-cluster";
        this.workerNodes = new ArrayList<>();
        this.serviceDiscoveryUrl = null;
        this.serviceDiscoveryType = "none";
        this.serviceDiscoveryTimeoutMs = 30000;
        this.heartbeatIntervalMs = 5000;
        this.heartbeatTimeoutMs = 30000;
        this.healthCheckIntervalMs = 10000;
        this.maxHeartbeatMisses = 3;
        this.maxConcurrentTasksPerWorker = 4;
        this.defaultWorkerCpuCores = 4;
        this.defaultWorkerMemoryMb = 4096;
        this.defaultWorkerRack = "/default-rack";
        this.defaultWorkerLocation = "/default-rack/default-node";
        this.networkTimeoutMs = 30000;
        this.networkRetryCount = 3;
        this.networkRetryDelayMs = 1000;
        this.maxPacketSize = 64 * 1024 * 1024; // 64MB
        this.enableCompression = true;
        this.enableEncryption = false;
        this.tempDir = System.getProperty("java.io.tmpdir") + "/jquick";
        this.spillToDiskThresholdBytes = 100 * 1024 * 1024; // 100MB
        this.maxResultSetSizeBytes = 1024 * 1024 * 1024; // 1GB
        this.enableDiskSpilling = true;
        this.enableJoinReorder = true;
        this.enablePredicatePushdown = true;
        this.enableConstantFolding = true;
        this.enableColumnPruning = true;
        this.enableLimitPushdown = true;
        this.enableAggregationPushdown = true;
        this.joinReorderMaxTables = 10;
        this.broadcastJoinThresholdBytes = 10 * 1024 * 1024; // 10MB
        this.enableQueryLogging = false;
        this.enableMetricsCollection = true;
        this.metricsExporter = "console";
        this.logLevel = "INFO";
        this.properties = new ConcurrentHashMap<>();
        this.extensions = new ConcurrentHashMap<>();
        loadDefaultConfiguration();
    }

    /**
     * 从文件加载配置
     */
    public static JQuickConfiguration loadFromFile(String path) throws IOException {
        JQuickConfiguration config = new JQuickConfiguration();
        config.loadPropertiesFile(path);
        return config;
    }

    /**
     * 从Properties对象加载配置
     */
    public static JQuickConfiguration loadFromProperties(Properties props) {
        JQuickConfiguration config = new JQuickConfiguration();
        config.applyProperties(props);
        return config;
    }

    /**
     * 从系统属性加载配置
     */
    public static JQuickConfiguration loadFromSystemProperties() {
        JQuickConfiguration config = new JQuickConfiguration();
        config.applyProperties(System.getProperties());
        return config;
    }

    /**
     * 加载默认配置
     */
    private void loadDefaultConfiguration() {
        // 尝试从classpath加载
        try (InputStream is = getClass().getResourceAsStream("/jquick-default.properties")) {
            if (is != null) {
                Properties props = new Properties();
                props.load(is);
                applyProperties(props);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (InputStream is = getClass().getResourceAsStream("/jquick.properties")) {
            if (is != null) {
                Properties props = new Properties();
                props.load(is);
                applyProperties(props);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String configPath = System.getProperty("jquick.config.path");
        if (configPath != null) {
            try {
                loadPropertiesFile(configPath);
            } catch (IOException e) {
                System.err.println("Failed to load config from " + configPath + ": " + e.getMessage());
            }
        }
    }

    /**
     * 加载属性文件
     */
    public void loadPropertiesFile(String path) throws IOException {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(path)) {
            props.load(fis);
        }
        applyProperties(props);
    }

    /**
     * 应用Properties配置
     */
    private void applyProperties(Properties props) {
        // 执行配置
        setIfPresent(props, "jquick.parallelism.default", v -> defaultParallelism = Integer.parseInt(v));
        setIfPresent(props, "jquick.task.retries.max", v -> maxTaskRetries = Integer.parseInt(v));
        setIfPresent(props, "jquick.task.timeout.ms", v -> taskTimeoutMs = Long.parseLong(v));
        setIfPresent(props, "jquick.execution.mode", v -> executionMode = v);
        setIfPresent(props, "jquick.adaptive.parallelism", v -> enableAdaptiveParallelism = Boolean.parseBoolean(v));
        setIfPresent(props, "jquick.vectorized.execution", v -> enableVectorizedExecution = Boolean.parseBoolean(v));
        setIfPresent(props, "jquick.batch.size", v -> batchSize = Integer.parseInt(v));

        // 集群配置
        setIfPresent(props, "jquick.cluster.name", v -> clusterName = v);
        setIfPresent(props, "jquick.service.discovery.url", v -> serviceDiscoveryUrl = v);
        setIfPresent(props, "jquick.service.discovery.type", v -> serviceDiscoveryType = v);
        setIfPresent(props, "jquick.service.discovery.timeout.ms", v -> serviceDiscoveryTimeoutMs = Integer.parseInt(v));

        // 心跳配置
        setIfPresent(props, "jquick.heartbeat.interval.ms", v -> heartbeatIntervalMs = Integer.parseInt(v));
        setIfPresent(props, "jquick.heartbeat.timeout.ms", v -> heartbeatTimeoutMs = Integer.parseInt(v));
        setIfPresent(props, "jquick.healthcheck.interval.ms", v -> healthCheckIntervalMs = Integer.parseInt(v));
        setIfPresent(props, "jquick.heartbeat.max.misses", v -> maxHeartbeatMisses = Integer.parseInt(v));

        // Worker配置
        setIfPresent(props, "jquick.worker.max.concurrent", v -> maxConcurrentTasksPerWorker = Integer.parseInt(v));
        setIfPresent(props, "jquick.worker.default.cpu.cores", v -> defaultWorkerCpuCores = Integer.parseInt(v));
        setIfPresent(props, "jquick.worker.default.memory.mb", v -> defaultWorkerMemoryMb = Long.parseLong(v));
        setIfPresent(props, "jquick.worker.default.rack", v -> defaultWorkerRack = v);
        setIfPresent(props, "jquick.worker.default.location", v -> defaultWorkerLocation = v);

        // 网络配置
        setIfPresent(props, "jquick.network.timeout.ms", v -> networkTimeoutMs = Integer.parseInt(v));
        setIfPresent(props, "jquick.network.retry.count", v -> networkRetryCount = Integer.parseInt(v));
        setIfPresent(props, "jquick.network.retry.delay.ms", v -> networkRetryDelayMs = Long.parseLong(v));
        setIfPresent(props, "jquick.network.max.packet.bytes", v -> maxPacketSize = Integer.parseInt(v));
        setIfPresent(props, "jquick.network.compression", v -> enableCompression = Boolean.parseBoolean(v));
        setIfPresent(props, "jquick.network.encryption", v -> enableEncryption = Boolean.parseBoolean(v));

        // 存储配置
        setIfPresent(props, "jquick.temp.dir", v -> tempDir = v);
        setIfPresent(props, "jquick.spill.threshold.bytes", v -> spillToDiskThresholdBytes = Long.parseLong(v));
        setIfPresent(props, "jquick.max.resultset.bytes", v -> maxResultSetSizeBytes = Long.parseLong(v));
        setIfPresent(props, "jquick.disk.spilling", v -> enableDiskSpilling = Boolean.parseBoolean(v));

        // 优化配置
        setIfPresent(props, "jquick.optimizer.join.reorder", v -> enableJoinReorder = Boolean.parseBoolean(v));
        setIfPresent(props, "jquick.optimizer.predicate.pushdown", v -> enablePredicatePushdown = Boolean.parseBoolean(v));
        setIfPresent(props, "jquick.optimizer.constant.folding", v -> enableConstantFolding = Boolean.parseBoolean(v));
        setIfPresent(props, "jquick.optimizer.column.pruning", v -> enableColumnPruning = Boolean.parseBoolean(v));
        setIfPresent(props, "jquick.optimizer.limit.pushdown", v -> enableLimitPushdown = Boolean.parseBoolean(v));
        setIfPresent(props, "jquick.optimizer.aggregation.pushdown", v -> enableAggregationPushdown = Boolean.parseBoolean(v));
        setIfPresent(props, "jquick.optimizer.join.reorder.max.tables", v -> joinReorderMaxTables = Integer.parseInt(v));
        setIfPresent(props, "jquick.optimizer.broadcast.threshold.bytes", v -> broadcastJoinThresholdBytes = Integer.parseInt(v));

        // 日志配置
        setIfPresent(props, "jquick.query.logging", v -> enableQueryLogging = Boolean.parseBoolean(v));
        setIfPresent(props, "jquick.metrics.collection", v -> enableMetricsCollection = Boolean.parseBoolean(v));
        setIfPresent(props, "jquick.metrics.exporter", v -> metricsExporter = v);
        setIfPresent(props, "jquick.log.level", v -> logLevel = v);

        // 自定义属性
        for (String key : props.stringPropertyNames()) {
            if (key.startsWith("jquick.custom.")) {
                properties.put(key, props.getProperty(key));
            }
        }
    }

    private void setIfPresent(Properties props, String key, PropertySetter setter) {
        String value = props.getProperty(key);
        if (value != null) {
            try {
                setter.set(value);
            } catch (Exception e) {
                System.err.println("Failed to set property " + key + ": " + e.getMessage());
            }
        }
    }

    @FunctionalInterface
    private interface PropertySetter {
        void set(String value);
    }
    /**
     * 添加Worker节点
     */
    public void addWorkerNode(JQuickWorkerNode worker) {
        this.workerNodes.add(worker);
    }

    /**
     * 添加Worker节点（通过地址）
     */
    public void addWorkerNode(String host, int port) {
        String workerId = "worker-" + workerNodes.size();
        JQuickWorkerNode worker = new JQuickWorkerNode(workerId, host, port,
                defaultWorkerRack, defaultWorkerLocation + "-" + workerNodes.size(),
                defaultWorkerCpuCores, defaultWorkerMemoryMb * 1024 * 1024,
                new HashMap<>());
        this.workerNodes.add(worker);
    }

    /**
     * 批量添加Worker节点
     */
    public void addWorkerNodes(String... addresses) {
        for (String address : addresses) {
            String[] parts = address.split(":");
            if (parts.length >= 2) {
                String host = parts[0];
                int port = Integer.parseInt(parts[1]);
                addWorkerNode(host, port);
            }
        }
    }

    /**
     * 移除Worker节点
     */
    public void removeWorkerNode(String workerId) {
        workerNodes.removeIf(w -> w.getWorkerId().equals(workerId));
    }

    /**
     * 设置自定义属性
     */
    public void setProperty(String key, String value) {
        properties.put(key, value);
    }

    /**
     * 获取自定义属性
     */
    public String getProperty(String key) {
        return properties.get(key);
    }

    /**
     * 获取自定义属性（带默认值）
     */
    public String getProperty(String key, String defaultValue) {
        return properties.getOrDefault(key, defaultValue);
    }

    /**
     * 设置扩展对象
     */
    public void setExtension(String key, Object value) {
        extensions.put(key, value);
    }

    /**
     * 获取扩展对象
     */
    @SuppressWarnings("unchecked")
    public <T> T getExtension(String key, Class<T> type) {
        Object value = extensions.get(key);
        if (value != null && type.isAssignableFrom(value.getClass())) {
            return (T) value;
        }
        return null;
    }


    /**
     * 验证配置是否有效
     */
    public void validate() {
        if (defaultParallelism < 1) {
            throw new IllegalArgumentException("defaultParallelism must be at least 1");
        }
        if (maxTaskRetries < 0) {
            throw new IllegalArgumentException("maxTaskRetries cannot be negative");
        }
        if (taskTimeoutMs <= 0) {
            throw new IllegalArgumentException("taskTimeoutMs must be positive");
        }
        if (heartbeatIntervalMs <= 0 || heartbeatIntervalMs >= heartbeatTimeoutMs) {
            throw new IllegalArgumentException("Invalid heartbeat configuration");
        }
        if (maxConcurrentTasksPerWorker < 1) {
            throw new IllegalArgumentException("maxConcurrentTasksPerWorker must be at least 1");
        }
        if (broadcastJoinThresholdBytes <= 0) {
            throw new IllegalArgumentException("broadcastJoinThresholdBytes must be positive");
        }
    }


    // 执行配置
    public int getDefaultParallelism() { return defaultParallelism; }

    public int getMaxTaskRetries() { return maxTaskRetries; }

    public long getTaskTimeoutMs() { return taskTimeoutMs; }

    public String getExecutionMode() { return executionMode; }

    public boolean isEnableAdaptiveParallelism() { return enableAdaptiveParallelism; }

    public boolean isEnableVectorizedExecution() { return enableVectorizedExecution; }

    public int getBatchSize() { return batchSize; }

    // 集群配置
    public String getClusterName() { return clusterName; }

    public List<JQuickWorkerNode> getWorkerNodes() { return Collections.unmodifiableList(workerNodes); }

    public String getServiceDiscoveryUrl() { return serviceDiscoveryUrl; }

    public String getServiceDiscoveryType() { return serviceDiscoveryType; }

    public int getServiceDiscoveryTimeoutMs() { return serviceDiscoveryTimeoutMs; }

    // 心跳配置
    public int getHeartbeatIntervalMs() { return heartbeatIntervalMs; }

    public int getHeartbeatTimeoutMs() { return heartbeatTimeoutMs; }

    public int getHealthCheckIntervalMs() { return healthCheckIntervalMs; }

    public int getMaxHeartbeatMisses() { return maxHeartbeatMisses; }

    // Worker配置
    public int getMaxConcurrentTasksPerWorker() { return maxConcurrentTasksPerWorker; }

    public int getDefaultWorkerCpuCores() { return defaultWorkerCpuCores; }

    public long getDefaultWorkerMemoryMb() { return defaultWorkerMemoryMb; }

    public String getDefaultWorkerRack() { return defaultWorkerRack; }

    public String getDefaultWorkerLocation() { return defaultWorkerLocation; }

    // 网络配置
    public int getNetworkTimeoutMs() { return networkTimeoutMs; }

    public int getNetworkRetryCount() { return networkRetryCount; }

    public long getNetworkRetryDelayMs() { return networkRetryDelayMs; }

    public int getMaxPacketSize() { return maxPacketSize; }

    public boolean isEnableCompression() { return enableCompression; }

    public boolean isEnableEncryption() { return enableEncryption; }

    // 存储配置
    public String getTempDir() { return tempDir; }

    public long getSpillToDiskThresholdBytes() { return spillToDiskThresholdBytes; }

    public long getMaxResultSetSizeBytes() { return maxResultSetSizeBytes; }

    public boolean isEnableDiskSpilling() { return enableDiskSpilling; }

    // 优化配置
    public boolean isEnableJoinReorder() { return enableJoinReorder; }

    public boolean isEnablePredicatePushdown() { return enablePredicatePushdown; }

    public boolean isEnableConstantFolding() { return enableConstantFolding; }

    public boolean isEnableColumnPruning() { return enableColumnPruning; }

    public boolean isEnableLimitPushdown() { return enableLimitPushdown; }

    public boolean isEnableAggregationPushdown() { return enableAggregationPushdown; }

    public int getJoinReorderMaxTables() { return joinReorderMaxTables; }

    public long getBroadcastJoinThresholdBytes() { return broadcastJoinThresholdBytes; }

    // 日志配置
    public boolean isEnableQueryLogging() { return enableQueryLogging; }

    public boolean isEnableMetricsCollection() { return enableMetricsCollection; }

    public String getMetricsExporter() { return metricsExporter; }

    public String getLogLevel() { return logLevel; }

    public JQuickConfiguration setDefaultParallelism(int defaultParallelism) {
        this.defaultParallelism = defaultParallelism;
        return this;
    }

    public JQuickConfiguration setMaxTaskRetries(int maxTaskRetries) {
        this.maxTaskRetries = maxTaskRetries;
        return this;
    }

    public JQuickConfiguration setTaskTimeoutMs(long taskTimeoutMs) {
        this.taskTimeoutMs = taskTimeoutMs;
        return this;
    }

    public JQuickConfiguration setExecutionMode(String executionMode) {
        this.executionMode = executionMode;
        return this;
    }

    public JQuickConfiguration setEnableAdaptiveParallelism(boolean enable) {
        this.enableAdaptiveParallelism = enable;
        return this;
    }

    public JQuickConfiguration setEnableVectorizedExecution(boolean enable) {
        this.enableVectorizedExecution = enable;
        return this;
    }

    public JQuickConfiguration setBatchSize(int batchSize) {
        this.batchSize = batchSize;
        return this;
    }

    public JQuickConfiguration setClusterName(String clusterName) {
        this.clusterName = clusterName;
        return this;
    }

    public JQuickConfiguration setWorkerNodes(List<JQuickWorkerNode> workerNodes) {
        this.workerNodes = new ArrayList<>(workerNodes);
        return this;
    }

    public JQuickConfiguration setServiceDiscoveryUrl(String url) {
        this.serviceDiscoveryUrl = url;
        if (url != null && url.startsWith("zookeeper://")) {
            this.serviceDiscoveryType = "zookeeper";
        } else if (url != null && url.startsWith("etcd://")) {
            this.serviceDiscoveryType = "etcd";
        } else if (url != null && url.startsWith("consul://")) {
            this.serviceDiscoveryType = "consul";
        }
        return this;
    }

    public JQuickConfiguration setHeartbeatIntervalMs(int intervalMs) {
        this.heartbeatIntervalMs = intervalMs;
        return this;
    }

    public JQuickConfiguration setHeartbeatTimeoutMs(int timeoutMs) {
        this.heartbeatTimeoutMs = timeoutMs;
        return this;
    }

    public JQuickConfiguration setMaxConcurrentTasksPerWorker(int max) {
        this.maxConcurrentTasksPerWorker = max;
        return this;
    }

    public JQuickConfiguration setTempDir(String tempDir) {
        this.tempDir = tempDir;
        return this;
    }

    public JQuickConfiguration setEnableJoinReorder(boolean enable) {
        this.enableJoinReorder = enable;
        return this;
    }

    public JQuickConfiguration setEnablePredicatePushdown(boolean enable) {
        this.enablePredicatePushdown = enable;
        return this;
    }

    public JQuickConfiguration setEnableQueryLogging(boolean enable) {
        this.enableQueryLogging = enable;
        return this;
    }

    public JQuickConfiguration setEnableMetricsCollection(boolean enable) {
        this.enableMetricsCollection = enable;
        return this;
    }

    public JQuickConfiguration setLogLevel(String logLevel) {
        this.logLevel = logLevel;
        return this;
    }
    /**
     * 创建配置副本
     */
    public JQuickConfiguration copy() {
        JQuickConfiguration copy = new JQuickConfiguration();
        copy.defaultParallelism = this.defaultParallelism;
        copy.maxTaskRetries = this.maxTaskRetries;
        copy.taskTimeoutMs = this.taskTimeoutMs;
        copy.executionMode = this.executionMode;
        copy.enableAdaptiveParallelism = this.enableAdaptiveParallelism;
        copy.enableVectorizedExecution = this.enableVectorizedExecution;
        copy.batchSize = this.batchSize;
        copy.clusterName = this.clusterName;
        copy.workerNodes = new ArrayList<>(this.workerNodes);
        copy.serviceDiscoveryUrl = this.serviceDiscoveryUrl;
        copy.serviceDiscoveryType = this.serviceDiscoveryType;
        copy.heartbeatIntervalMs = this.heartbeatIntervalMs;
        copy.heartbeatTimeoutMs = this.heartbeatTimeoutMs;
        copy.maxConcurrentTasksPerWorker = this.maxConcurrentTasksPerWorker;
        copy.tempDir = this.tempDir;
        copy.enableJoinReorder = this.enableJoinReorder;
        copy.enablePredicatePushdown = this.enablePredicatePushdown;
        copy.enableQueryLogging = this.enableQueryLogging;
        copy.enableMetricsCollection = this.enableMetricsCollection;
        copy.logLevel = this.logLevel;
        copy.properties.putAll(this.properties);
        copy.extensions.putAll(this.extensions);
        return copy;
    }

    @Override
    public String toString() {
        return String.format("JQuickConfiguration{cluster='%s', parallelism=%d, workers=%d, mode='%s'}", clusterName, defaultParallelism, workerNodes.size(), executionMode);
    }
}