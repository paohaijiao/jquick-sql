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

import com.github.paohaijiao.distributed.coordinator.JQuickCoordinator;
import lombok.Data;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * packageName com.github.paohaijiao.config
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/5/17
 */
@Data
public class JQuickSqlRuntimeConfig {

    private int defaultParallelism=1;//Runtime.getRuntime().availableProcessors()

    private int maxFileSize=64 * 1024 * 1024;

    private int maxTaskRetries=1;
    private int taskTimeoutMs=1;

    private int executionMode=1;

    private List<JQuickCoordinator.WorkerEndpoint> workers;


    public static JQuickSqlRuntimeConfig loadFromProperties() {
        JQuickSqlRuntimeConfig config = new JQuickSqlRuntimeConfig();
        try (InputStream input = JQuickBannerConfig.class.getClassLoader().getResourceAsStream("jquick-sql.properties")) {
            if (input != null) {
                Properties props = new Properties();
                props.load(input);
                config.setDefaultParallelism(Integer.getInteger(props.getProperty("jquick.runtime.defaultParallelism", "1")));
                config.setMaxFileSize(Integer.getInteger(props.getProperty("jquick.runtime.maxFileSize", "64 * 1024 * 1024")));
                config.setMaxTaskRetries(Integer.getInteger(props.getProperty("jquick.runtime.maxTaskRetries", "1")));
                config.setMaxTaskRetries(Integer.getInteger(props.getProperty("jquick.runtime.taskTimeoutMs", "1")));
                config.setMaxTaskRetries(Integer.getInteger(props.getProperty("jquick.runtime.executionMode", "1")));
            }
        } catch (Exception e) {
            // 使用默认值
        }
        return config;
    }
    /**
     * 从 properties 初始化 workers
     * 支持格式: jquick.runtime.workers.1.host=192.168.1.100
     *          jquick.runtime.workers.1.port=9000
     *          jquick.runtime.workers.1.id=worker-1
     *          jquick.runtime.workers.2.host=192.168.1.101
     *          jquick.runtime.workers.2.port=9000
     */
    private void initWorkersFromProperties(Properties props) {
        List<JQuickCoordinator.WorkerEndpoint> workerList = new ArrayList<>();
        String workerPrefix = "jquick.runtime.workers.";
        AtomicInteger maxIndex = new AtomicInteger(0);
        props.stringPropertyNames().stream()
                .filter(key -> key.startsWith(workerPrefix))
                .forEach(key -> {
                    String suffix = key.substring(workerPrefix.length());
                    if (suffix.contains(".")) {
                        String indexStr = suffix.substring(0, suffix.indexOf('.'));
                        try {
                            int index = Integer.parseInt(indexStr);
                            if (index > maxIndex.get()) {
                                maxIndex.set(index);
                            }
                        } catch (NumberFormatException e) {
                            // 忽略非数字索引
                        }
                    }
                });

        for (int i = 1; i <= maxIndex.get(); i++) {
            String baseKey = workerPrefix + i + ".";
            String host = props.getProperty(baseKey + "host");
            String portStr = props.getProperty(baseKey + "port");
            if (host != null && portStr != null) {
                try {
                    int port = Integer.parseInt(portStr);
                    String workerId = props.getProperty(baseKey + "id", "worker-" + i);
                    workerList.add(new JQuickCoordinator.WorkerEndpoint(workerId, host, port, i - 1));
                } catch (NumberFormatException e) {
                }
            }
        }
        if (!workerList.isEmpty()) {
            this.workers = workerList;
        } else {
            initDefaultWorkers();
        }
    }
    /**
     * 初始化默认 workers
     */
    private void initDefaultWorkers() {
        this.workers = new ArrayList<>();
        this.workers.add(new JQuickCoordinator.WorkerEndpoint("worker-0", "localhost", 9000, 0));
    }
    /**
     * 手动设置 workers
     */
    public void setWorkers(List<JQuickCoordinator.WorkerEndpoint> workers) {
        this.workers = workers;
    }

}
