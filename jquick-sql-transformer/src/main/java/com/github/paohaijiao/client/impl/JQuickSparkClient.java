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
import org.apache.spark.SparkConf;
import org.apache.spark.sql.SparkSession;

/**
 * Spark 客户端实现
 */
public class JQuickSparkClient extends JQuickAbstractJQuickClient {

    private SparkSession sparkSession;
    private SparkConf sparkConf;

    public JQuickSparkClient() {
        super("spark");
    }

    public JQuickSparkClient(JQuickClientConfig config) {
        super("spark", config);
    }

    @Override
    protected void doInit() {
        try {
            sparkConf = new SparkConf()
                    .setAppName(config.getAppName())
                    .setMaster(config.getMaster())
                    .set("spark.sql.adaptive.enabled", "true")
                    .set("spark.sql.adaptive.coalescePartitions.enabled", "true")
                    .set("spark.sql.adaptive.skewJoin.enabled", "true")
                    .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer");
            sparkConf.set("spark.sql.shuffle.partitions", String.valueOf(config.getParallelism()));  // 设置并行度
            for (java.util.Map.Entry<String, String> entry : config.getProperties().entrySet()) {// 设置额外属性
                sparkConf.set(entry.getKey(), entry.getValue());
            }
            if (config.getSparkHome() != null) {// 设置 Spark 路径
                sparkConf.set("spark.home", config.getSparkHome());
            }

            SparkSession.Builder builder = SparkSession.builder()// 创建 SparkSession
                    .config(sparkConf);
            if (config.getSparkJars() != null && !config.getSparkJars().isEmpty()) {// 设置 jar 包
                builder.config("spark.jars", config.getSparkJars());
            }

            sparkSession = builder.getOrCreate();
            JConsole console=JConsole.initConsoleEnvironment();
            console.info("Spark 客户端初始化成功 - AppName: "+config.getAppName()+", Master: "+config.getMaster()+", Parallelism: " +config.getParallelism());

        } catch (Exception e) {
            JConsole console=JConsole.initConsoleEnvironment();
            console.error("Spark 客户端初始化失败", e);
            throw new RuntimeException("Spark 客户端初始化失败", e);
        }
    }

    @Override
    protected void doClose() {
        if (sparkSession != null) {
            sparkSession.close();
            sparkSession = null;
        }
    }

    public SparkSession getSparkSession() {
        checkAvailable();
        return sparkSession;
    }

    public SparkConf getSparkConf() {
        return sparkConf;
    }

    private void checkAvailable() {
        if (!isAvailable()) {
            throw new IllegalStateException("Spark 客户端未初始化或已关闭");
        }
    }
}
