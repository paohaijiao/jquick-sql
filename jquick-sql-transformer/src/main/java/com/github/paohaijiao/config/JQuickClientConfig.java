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
 * packageName com.github.paohaijiao.config
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/5/3
 */

import com.github.paohaijiao.client.ClientType;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 客户端配置类
 */
public class JQuickClientConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    private ClientType clientType = ClientType.AUTO;

    private int parallelism = Runtime.getRuntime().availableProcessors();

    private String appName = "JQuickClient";

    private String master = "local[*]";

    private Map<String, String> properties = new HashMap<>();

    private boolean enableLogging = true;

    private long timeout = 60000;

    // Spark 特有配置

    private String sparkHome;

    private String sparkJars;

    // Flink 特有配置
    private String flinkConfigDir;

    private boolean streamingMode = false;

    // ForkJoin 特有配置
    private int forkJoinPoolSize = Runtime.getRuntime().availableProcessors();

    private boolean asyncMode = false;

    public ClientType getClientType() {
        return clientType;
    }

    public JQuickClientConfig setClientType(ClientType clientType) {
        this.clientType = clientType;
        return this;
    }

    public int getParallelism() {
        return parallelism;
    }

    public JQuickClientConfig setParallelism(int parallelism) {
        this.parallelism = parallelism;
        return this;
    }

    public String getAppName() {
        return appName;
    }

    public JQuickClientConfig setAppName(String appName) {
        this.appName = appName;
        return this;
    }

    public String getMaster() {
        return master;
    }

    public JQuickClientConfig setMaster(String master) {
        this.master = master;
        return this;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public JQuickClientConfig setProperties(Map<String, String> properties) {
        this.properties = properties;
        return this;
    }

    public JQuickClientConfig addProperty(String key, String value) {
        this.properties.put(key, value);
        return this;
    }

    public boolean isEnableLogging() {
        return enableLogging;
    }

    public JQuickClientConfig setEnableLogging(boolean enableLogging) {
        this.enableLogging = enableLogging;
        return this;
    }

    public long getTimeout() {
        return timeout;
    }

    public JQuickClientConfig setTimeout(long timeout) {
        this.timeout = timeout;
        return this;
    }

    public String getSparkHome() {
        return sparkHome;
    }

    public JQuickClientConfig setSparkHome(String sparkHome) {
        this.sparkHome = sparkHome;
        return this;
    }

    public String getSparkJars() {
        return sparkJars;
    }

    public JQuickClientConfig setSparkJars(String sparkJars) {
        this.sparkJars = sparkJars;
        return this;
    }

    public String getFlinkConfigDir() {
        return flinkConfigDir;
    }

    public JQuickClientConfig setFlinkConfigDir(String flinkConfigDir) {
        this.flinkConfigDir = flinkConfigDir;
        return this;
    }

    public boolean isStreamingMode() {
        return streamingMode;
    }

    public JQuickClientConfig setStreamingMode(boolean streamingMode) {
        this.streamingMode = streamingMode;
        return this;
    }

    public int getForkJoinPoolSize() {
        return forkJoinPoolSize;
    }

    public JQuickClientConfig setForkJoinPoolSize(int forkJoinPoolSize) {
        this.forkJoinPoolSize = forkJoinPoolSize;
        return this;
    }

    public boolean isAsyncMode() {
        return asyncMode;
    }

    public JQuickClientConfig setAsyncMode(boolean asyncMode) {
        this.asyncMode = asyncMode;
        return this;
    }

    public static JQuickClientConfig defaultConfig() {
        return new JQuickClientConfig();
    }

    public static JQuickClientConfig sparkConfig() {
        return new JQuickClientConfig()
                .setClientType(ClientType.SPARK)
                .setAppName("JQuickSparkClient")
                .setMaster("local[*]");
    }

    public static JQuickClientConfig flinkConfig() {
        return new JQuickClientConfig()
                .setClientType(ClientType.FLINK)
                .setAppName("JQuickFlinkClient");
    }

    public static JQuickClientConfig forkJoinConfig() {
        return new JQuickClientConfig()
                .setClientType(ClientType.FORK_JOIN)
                .setForkJoinPoolSize(Runtime.getRuntime().availableProcessors());
    }
}