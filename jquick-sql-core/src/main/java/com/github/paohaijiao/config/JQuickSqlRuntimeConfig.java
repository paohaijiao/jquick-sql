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
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    private static final String CONFIG_PROPERTIES = "jquick-sql.properties";

    private static final String[] CONFIG_YAML_FILES = {"jquick-sql.yml", "jquick-sql.yaml"};

    private int defaultParallelism=1;//Runtime.getRuntime().availableProcessors()

    private int maxFileSize=64 * 1024 * 1024;

    private int maxTaskRetries=1;

    private int taskTimeoutMs=1;

    private int executionMode=1;

    /**
     * Base gRPC port for embedded workers; worker i uses grpcPort + i.
     */
    private int grpcPort = 19001;

    private List<WorkerEndpoint> workers;


    /**
     * Loads jquick-sql.yml/jquick-sql.yaml and jquick-sql.properties from the classpath;
     * properties take precedence over yml.
     */
    public static JQuickSqlRuntimeConfig loadFromProperties() {
        JQuickSqlRuntimeConfig config = new JQuickSqlRuntimeConfig();
        config.applyProperties(loadClasspathProperties());
        return config;
    }

    private static Properties loadClasspathProperties() {
        Properties props = new Properties();
        ClassLoader classLoader = JQuickSqlRuntimeConfig.class.getClassLoader();
        for (String yamlFile : CONFIG_YAML_FILES) {
            try (InputStream input = classLoader.getResourceAsStream(yamlFile)) {
                if (input != null) {
                    props.putAll(loadYaml(input));
                }
            } catch (Exception e) {
                // 使用默认值
            }
        }
        try (InputStream input = classLoader.getResourceAsStream(CONFIG_PROPERTIES)) {
            if (input != null) {
                Properties fileProps = new Properties();
                fileProps.load(input);
                props.putAll(fileProps);
            }
        } catch (Exception e) {
            // 使用默认值
        }
        return props;
    }

    /**
     * Flattens YAML into properties, e.g. jquick.runtime.grpcPort.
     */
    public static Properties loadYaml(InputStream input) {
        Properties props = new Properties();
        Object data = new Yaml().load(input);
        if (data instanceof Map) {
            flattenProperties("", (Map<?, ?>) data, props);
        }
        return props;
    }

    private static void flattenProperties(String prefix, Map<?, ?> source, Properties props) {
        for (Map.Entry<?, ?> entry : source.entrySet()) {
            if (entry.getKey() == null) {
                continue;
            }
            String key = prefix.isEmpty() ? String.valueOf(entry.getKey()) : prefix + "." + entry.getKey();
            Object value = entry.getValue();
            if (value instanceof Map) {
                flattenProperties(key, (Map<?, ?>) value, props);
            } else if (value != null) {
                props.setProperty(key, String.valueOf(value));
            }
        }
    }

    /**
     * Applies jquick.runtime.* properties.
     */
    public void applyProperties(Properties props) {
        setIfPresent(props, "jquick.runtime.grpcPort", value -> this.grpcPort = Integer.parseInt(value.trim()));
        setIfPresent(props, "jquick.runtime.defaultParallelism", value -> this.defaultParallelism = Integer.parseInt(value.trim()));
        setIfPresent(props, "jquick.runtime.maxFileSize", value -> this.maxFileSize = Integer.parseInt(value.trim()));
        setIfPresent(props, "jquick.runtime.maxTaskRetries", value -> this.maxTaskRetries = Integer.parseInt(value.trim()));
        setIfPresent(props, "jquick.runtime.taskTimeoutMs", value -> this.taskTimeoutMs = Integer.parseInt(value.trim()));
        setIfPresent(props, "jquick.runtime.executionMode", value -> this.executionMode = Integer.parseInt(value.trim()));
    }

    private void setIfPresent(Properties props, String key, PropertySetter setter) {
        String value = props.getProperty(key);
        if (value != null && !value.trim().isEmpty()) {
            setter.set(value);
        }
    }

    @FunctionalInterface
    private interface PropertySetter {
        void set(String value);
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
        List<WorkerEndpoint> workerList = new ArrayList<>();
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
                    workerList.add(new WorkerEndpoint(workerId, host, port, i - 1));
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
        this.workers.add(new WorkerEndpoint("worker-0", "localhost", 9000, 0));
    }
    /**
     * 手动设置 workers
     */
    public void setWorkers(List<WorkerEndpoint> workers) {
        this.workers = workers;
    }

}
