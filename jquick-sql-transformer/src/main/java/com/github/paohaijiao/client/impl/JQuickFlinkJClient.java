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
package com.github.paohaijiao.client.impl;

import com.github.paohaijiao.client.JQuickAbstractJQuickClient;
import com.github.paohaijiao.config.JQuickClientConfig;
import com.github.paohaijiao.console.JConsole;
import org.apache.flink.api.common.RuntimeExecutionMode;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.CoreOptions;
import org.apache.flink.configuration.JobManagerOptions;
import org.apache.flink.configuration.TaskManagerOptions;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

/**
 * Flink 客户端实现
 */
public class JQuickFlinkJClient extends JQuickAbstractJQuickClient {

    private StreamExecutionEnvironment env;

    private Configuration flinkConfig;

    public JQuickFlinkJClient() {
        super("flink");
    }

    public JQuickFlinkJClient(JQuickClientConfig config) {
        super("flink", config);
    }

    @Override
    protected void doInit() {
        try {
            // 构建 Flink 配置
            flinkConfig = new Configuration();
            flinkConfig.set(CoreOptions.DEFAULT_PARALLELISM, config.getParallelism());// 设置并行度
            flinkConfig.set(TaskManagerOptions.NUM_TASK_SLOTS, config.getParallelism());
            flinkConfig.set(CoreOptions.DEFAULT_PARALLELISM, config.getParallelism());
            flinkConfig.set(JobManagerOptions.TOTAL_PROCESS_MEMORY, org.apache.flink.configuration.MemorySize.ofMebiBytes(1024));
            flinkConfig.set(TaskManagerOptions.NUM_TASK_SLOTS, config.getParallelism());
            flinkConfig.set(TaskManagerOptions.TOTAL_PROCESS_MEMORY, org.apache.flink.configuration.MemorySize.ofMebiBytes(2048));
            // 设置额外属性
            for (java.util.Map.Entry<String, String> entry : config.getProperties().entrySet()) {
                flinkConfig.setString(entry.getKey(), entry.getValue());
            }
            for (java.util.Map.Entry<String, String> entry : config.getProperties().entrySet()) {// 设置额外属性
                flinkConfig.setString(entry.getKey(), entry.getValue());
            }
            if (config.getFlinkConfigDir() != null) { // 设置配置目录
                flinkConfig.setString("flink.config.dir", config.getFlinkConfigDir());
            }
            flinkConfig.setString("pipeline.name", config.getAppName());// 设置应用名称
            env = StreamExecutionEnvironment.getExecutionEnvironment(flinkConfig);// 创建执行环境
            env.setParallelism(config.getParallelism());// 设置并行度
            if (config.isStreamingMode()) { // 设置运行模式
                env.setRuntimeMode(RuntimeExecutionMode.STREAMING);
            } else {
                env.setRuntimeMode(RuntimeExecutionMode.BATCH);
            }
            JConsole console=JConsole.initConsoleEnvironment();
            console.info("Flink 客户端初始化成功 - AppName:"+config.getAppName()+", Parallelism: "+config.getParallelism()+", StreamingMode: "+config.isStreamingMode() );
        } catch (Exception e) {
            JConsole console=JConsole.initConsoleEnvironment();
            console.error("Flink 客户端初始化失败", e);
            throw new RuntimeException("Flink 客户端初始化失败", e);
        }
    }

    @Override
    protected void doClose() {
        // Flink 的 StreamExecutionEnvironment 不需要显式关闭
        env = null;
    }

    public StreamExecutionEnvironment getExecutionEnvironment() {
        checkAvailable();
        return env;
    }

    public Configuration getFlinkConfig() {
        return flinkConfig;
    }

    private void checkAvailable() {
        if (!isAvailable()) {
            throw new IllegalStateException("Flink 客户端未初始化或已关闭");
        }
    }
}
