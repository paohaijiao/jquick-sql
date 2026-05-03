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
package com.github.paohaijiao.factory;

import com.github.paohaijiao.client.impl.JQuickFlinkJClient;
import com.github.paohaijiao.client.impl.JQuickForkJoinClient;
import com.github.paohaijiao.client.impl.JQuickLocalNClient;
import com.github.paohaijiao.client.impl.JQuickSparkClient;
import com.github.paohaijiao.condition.JQuickSqlCondition;
import com.github.paohaijiao.config.JQuickClientConfig;
import com.github.paohaijiao.console.JConsole;
import com.github.paohaijiao.exception.JAssert;
import com.github.paohaijiao.expression.JQuickSqlExpression;
import com.github.paohaijiao.expression.JQuickSqlFunctionCallExpression;
import com.github.paohaijiao.expression.JQuickSqlOrderByExpression;
import com.github.paohaijiao.forkjoin.JQuickSqlForkJoinAbilityProvider;
import com.github.paohaijiao.join.JQuickSqlJoinCondition;
import com.github.paohaijiao.manage.JQuickSqlAbilityProviderManager;
import com.github.paohaijiao.provider.JQuickSqlAbilityProvider;
import com.github.paohaijiao.statement.JQuickDataSet;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.spark.sql.SparkSession;

import java.util.List;
import java.util.Map;

/**
 * JQuickSqlAbilityProvider 工厂类
 * 提供便捷的 Provider 创建和管理方法
 */
public class JQuickSqlAbilityProviderFactory {

    private static JQuickClientConfig config=null;


    private static final JQuickSqlAbilityProviderManager manager = JQuickSqlAbilityProviderManager.getInstance();

    static {
        manager.addListener(new JQuickSqlAbilityProviderManager.ProviderChangeListener() {
            @Override
            public void onProviderLoaded(JQuickSqlAbilityProvider provider) {
                JConsole console = JConsole.initConsoleEnvironment();
                console.info("Provider 已加载: " + provider.getClass().getSimpleName());
            }

            @Override
            public void onProviderChanged(JQuickSqlAbilityProvider oldProvider, JQuickSqlAbilityProvider newProvider) {
                JConsole console = JConsole.initConsoleEnvironment();
                console.info(String.format("Provider 已切换: %s -> %s", oldProvider.getClass().getSimpleName(), newProvider.getClass().getSimpleName()));
            }
        });
    }
    public JQuickSqlAbilityProviderFactory(JQuickClientConfig config){
        JAssert.notNull(config,"the client config required nnot null ");
        this.config=config;
    }



    /**
     * 创建最优的 Provider（自动选择）
     */
    public static JQuickSqlAbilityProvider create() {
        JConsole console = JConsole.initConsoleEnvironment();
        JQuickSqlAbilityProvider provider = manager.getDefaultProvider();
        console.debug("创建默认 Provider: " + provider.getClass().getSimpleName());
        return provider;
    }

    /**
     * 创建指定类型的 Provider
     */
    public static JQuickSqlAbilityProvider create(String type) {
        JConsole console = JConsole.initConsoleEnvironment();
        JQuickSqlAbilityProvider provider;
        switch (type) {
            case "SPARK":
                JQuickSparkClient sparkClient=new JQuickSparkClient(config);
                SparkSession session=sparkClient.getSparkSession();
                return  create(session);
            case "FLINK":
                JQuickFlinkJClient flinkJClient=new JQuickFlinkJClient(config);
                StreamExecutionEnvironment env=flinkJClient.getExecutionEnvironment();
                return create(env);
            case "FORKJOIN":
                JQuickForkJoinClient localNClient=new JQuickForkJoinClient(config);
                return create(localNClient);
            case "LOCAL_N":
                JQuickLocalNClient nClient=new JQuickLocalNClient(config);
                return create(nClient);
            case "LOCAL":
                provider = manager.getLocalProvider();
                break;
            case "MAPREDUCE":
                provider = manager.getMapReduceProvider();
                break;
            default:
                provider = manager.getDefaultProvider();
        }
        console.debug("创建 " + type + " Provider: " + provider.getClass().getSimpleName());
        return provider;
    }

    /**
     * 创建 Spark Provider
     */
    public static JQuickSqlAbilityProvider create(SparkSession spark) {
        JConsole console = JConsole.initConsoleEnvironment();
        JQuickSqlAbilityProvider provider = manager.getSparkProvider(spark);
        console.debug("创建 Spark Provider");
        return provider;
    }

    /**
     * 创建 Flink Provider
     */
    public static JQuickSqlAbilityProvider create(StreamExecutionEnvironment env) {
        JConsole console = JConsole.initConsoleEnvironment();
        JQuickSqlAbilityProvider provider = manager.getFlinkProvider(env);
        console.debug("创建 Flink Provider");
        return provider;
    }

    public static JQuickSqlAbilityProvider create(JQuickForkJoinClient client) {
        JConsole console = JConsole.initConsoleEnvironment();
        JQuickSqlAbilityProvider provider = manager.getForkJoin(client);
        console.debug("创建 ForkJoin Provider");
        return provider;
    }
    public static JQuickSqlAbilityProvider create(JQuickLocalNClient client) {
        JConsole console = JConsole.initConsoleEnvironment();
        JQuickSqlAbilityProvider provider = manager.getLocalNProvider();
        console.debug("创建 Flink Provider");
        return provider;
    }
    /**
     * 创建带配置的 Provider
     */
    public static JQuickSqlAbilityProvider create(ProviderConfig config) {
        JQuickSqlAbilityProviderManager.JQuickSqlProviderContext context = new JQuickSqlAbilityProviderManager.JQuickSqlProviderContext()
                .setDistributed(config.isDistributed())
                .setStreaming(config.isStreaming())
                .setDataSetSize(config.getDataSetSize())
                .setParallelEnabled(config.isParallelEnabled());

        if (config.getSparkSession() != null) {
            context.setSparkSession(config.getSparkSession());
        }
        if (config.getFlinkEnv() != null) {
            context.setFlinkEnv(config.getFlinkEnv());
        }
        return manager.selectProvider(context);
    }

    /**
     * 根据数据量智能创建
     */
    public static JQuickSqlAbilityProvider createByDataSize(long dataSize) {
        JConsole console = JConsole.initConsoleEnvironment();
        JQuickSqlAbilityProvider provider = manager.selectByDataSize(dataSize);
        console.info(String.format("根据数据量 %d 选择 Provider: %s", dataSize, provider.getClass().getSimpleName()));
        return provider;
    }

    /**
     * 根据环境智能创建
     */
    public static JQuickSqlAbilityProvider createByEnvironment() {
        JConsole console = JConsole.initConsoleEnvironment();
        JQuickSqlAbilityProvider provider = manager.selectByEnvironment();
        console.info("根据环境选择 Provider: " + provider.getClass().getSimpleName());
        return provider;
    }

    /**
     * 根据需求智能创建
     */
    public static JQuickSqlAbilityProvider createByRequirement(JQuickSqlAbilityProviderManager.ProviderRequirement requirement) {
        JConsole console = JConsole.initConsoleEnvironment();
        JQuickSqlAbilityProvider provider = manager.getRecommendedProvider(requirement);
        console.info("根据需求选择 Provider: " + provider.getClass().getSimpleName());
        return provider;
    }

    /**
     * 创建并包装带监控的 Provider
     */
    public static JQuickSqlAbilityProvider createWithMonitor() {
        JQuickSqlAbilityProvider original = create();
        return new MonitoredProvider(original);
    }

    /**
     * 创建并包装带监控的 Provider（指定类型）
     */
    public static JQuickSqlAbilityProvider createWithMonitor(String type) {
        JQuickSqlAbilityProvider original = create(type);
        return new MonitoredProvider(original);
    }

    /**
     * 使用默认 Provider 执行操作
     */
    public static <T> T execute(ProviderExecutor<T> executor) {
        JQuickSqlAbilityProvider provider = manager.getCurrentProvider();
        return executor.execute(provider);
    }

    /**
     * 使用指定类型的 Provider 执行操作
     */
    public static <T> T execute(String type, ProviderExecutor<T> executor) {
        JQuickSqlAbilityProvider provider = create(type);
        return executor.execute(provider);
    }

    /**
     * 使用最合适的 Provider 执行操作
     */
    public static <T> T executeSmart(JQuickDataSet dataSet, ProviderExecutor<T> executor) {
        JQuickSqlAbilityProvider provider = manager.selectByDataSize(dataSet.size());
        return executor.execute(provider);
    }

    /**
     * 切换当前 Provider
     */
    public static void switchTo(String type) {
        JQuickSqlAbilityProvider provider = create(type);
        manager.switchProvider(provider);
    }

    /**
     * 切换到 Spark Provider
     */
    public static void switchToSpark(SparkSession spark) {
        JQuickSqlAbilityProvider provider = manager.getSparkProvider(spark);
        manager.switchProvider(provider);
    }

    /**
     * 切换到 Flink Provider
     */
    public static void switchToFlink(StreamExecutionEnvironment env) {
        JQuickSqlAbilityProvider provider = manager.getFlinkProvider(env);
        manager.switchProvider(provider);
    }

    /**
     * 重置为默认 Provider
     */
    public static void switchToDefault() {
        manager.switchProvider(manager.getDefaultProvider());
    }

    /**
     * 设置选择策略
     */
    public static void setSelectionStrategy(JQuickSqlAbilityProviderManager.ProviderSelectionStrategy strategy) {
        manager.setSelectionStrategy(strategy);
    }

    /**
     * 启用/禁用自动优化
     */
    public static void setAutoOptimize(boolean autoOptimize) {
        manager.setAutoOptimize(autoOptimize);
    }

    /**
     * 打印所有 Provider 信息
     */
    public static void printInfo() {
        manager.printProviderInfo();
    }

    /**
     * 打印性能报告
     */
    public static void printPerformance() {
        manager.printPerformanceReport();
    }

    /**
     * 获取当前 Provider
     */
    public static JQuickSqlAbilityProvider getCurrent() {
        return manager.getCurrentProvider();
    }

    /**
     * 获取默认 Provider
     */
    public static JQuickSqlAbilityProvider getDefault() {
        return manager.getDefaultProvider();
    }

    /**
     * 获取所有可用 Provider
     */
    public static List<JQuickSqlAbilityProvider> getAll() {
        return manager.getAllProviders();
    }

    /**
     * 获取 Provider 统计信息
     */
    public static Map<String, JQuickSqlAbilityProviderManager.ProviderStats> getStats() {
        return manager.getProviderStats();
    }

    /**
     * 比较两个 Provider
     */
    public static void compare(JQuickSqlAbilityProvider p1, JQuickSqlAbilityProvider p2) {
        JQuickSqlAbilityProviderManager.ProviderComparison comparison = manager.compareProviders(p1, p2);
        comparison.print();
    }

    /**
     * 重新加载所有 Provider
     */
    public static void reload() {
        manager.reload();
    }

    /**
     * 关闭所有 Provider
     */
    public static void shutdown() {
        JConsole console = JConsole.initConsoleEnvironment();
        JQuickSqlAbilityProvider localN = manager.getLocalNProvider();  // 关闭本地多线程 Provider 的线程池
        if (localN instanceof JQuickSqlForkJoinAbilityProvider) {
            ((JQuickSqlForkJoinAbilityProvider) localN).shutdown();
        }
        console.info("所有 Provider 已关闭");
    }




    /**
     * Provider 执行器接口
     */
    @FunctionalInterface
    public interface ProviderExecutor<T> {
        T execute(JQuickSqlAbilityProvider provider);
    }

    /**
     * Provider 配置
     */
    public static class ProviderConfig {

        private boolean distributed = false;

        private boolean streaming = false;

        private long dataSetSize = 0;

        private boolean parallelEnabled = true;

        private SparkSession sparkSession;

        private StreamExecutionEnvironment flinkEnv;

        public static ProviderConfig of() {
            return new ProviderConfig();
        }

        public boolean isDistributed() {
            return distributed;
        }

        public ProviderConfig setDistributed(boolean distributed) {
            this.distributed = distributed;
            return this;
        }

        public boolean isStreaming() {
            return streaming;
        }

        public ProviderConfig setStreaming(boolean streaming) {
            this.streaming = streaming;
            return this;
        }

        public long getDataSetSize() {
            return dataSetSize;
        }

        public ProviderConfig setDataSetSize(long dataSetSize) {
            this.dataSetSize = dataSetSize;
            return this;
        }

        public boolean isParallelEnabled() {
            return parallelEnabled;
        }

        public ProviderConfig setParallelEnabled(boolean parallelEnabled) {
            this.parallelEnabled = parallelEnabled;
            return this;
        }

        public SparkSession getSparkSession() {
            return sparkSession;
        }

        public ProviderConfig setSparkSession(SparkSession sparkSession) {
            this.sparkSession = sparkSession;
            return this;
        }

        public StreamExecutionEnvironment getFlinkEnv() {
            return flinkEnv;
        }

        public ProviderConfig setFlinkEnv(StreamExecutionEnvironment flinkEnv) {
            this.flinkEnv = flinkEnv;
            return this;
        }
    }

    /**
     * 带监控的 Provider 包装器
     */
    private static class MonitoredProvider implements JQuickSqlAbilityProvider {
        private final JQuickSqlAbilityProvider delegate;
        private final JQuickSqlAbilityProviderManager.ProviderStats stats;

        public MonitoredProvider(JQuickSqlAbilityProvider delegate) {
            this.delegate = delegate;
            this.stats = new JQuickSqlAbilityProviderManager.ProviderStats(delegate);
        }

        @Override
        public JQuickDataSet innerJoin(JQuickDataSet left, JQuickDataSet right, JQuickSqlJoinCondition condition) {
            return monitor(() -> delegate.innerJoin(left, right, condition));
        }

        @Override
        public JQuickDataSet leftJoin(JQuickDataSet left, JQuickDataSet right, JQuickSqlJoinCondition condition) {
            return monitor(() -> delegate.leftJoin(left, right, condition));
        }

        @Override
        public JQuickDataSet rightJoin(JQuickDataSet left, JQuickDataSet right, JQuickSqlJoinCondition condition) {
            return monitor(() -> delegate.rightJoin(left, right, condition));
        }

        @Override
        public JQuickDataSet fullOuterJoin(JQuickDataSet left, JQuickDataSet right, JQuickSqlJoinCondition condition) {
            return monitor(() -> delegate.fullOuterJoin(left, right, condition));
        }

        @Override
        public JQuickDataSet crossJoin(JQuickDataSet left, JQuickDataSet right) {
            return monitor(() -> delegate.crossJoin(left, right));
        }

        @Override
        public JQuickDataSet naturalJoin(JQuickDataSet left, JQuickDataSet right) {
            return monitor(() -> delegate.naturalJoin(left, right));
        }

        @Override
        public JQuickDataSet union(JQuickDataSet ds1, JQuickDataSet ds2) {
            return monitor(() -> delegate.union(ds1, ds2));
        }

        @Override
        public JQuickDataSet intersect(JQuickDataSet ds1, JQuickDataSet ds2) {
            return monitor(() -> delegate.intersect(ds1, ds2));
        }

        @Override
        public JQuickDataSet minus(JQuickDataSet ds1, JQuickDataSet ds2) {
            return monitor(() -> delegate.minus(ds1, ds2));
        }

        @Override
        public JQuickDataSet selectColumns(JQuickDataSet dataset, List<String> columnNames) {
            return monitor(() -> delegate.selectColumns(dataset, columnNames));
        }

        @Override
        public JQuickDataSet filter(JQuickDataSet dataset, JQuickSqlCondition condition) {
            return monitor(() -> delegate.filter(dataset, condition));
        }

        @Override
        public JQuickDataSet transform(JQuickDataSet dataset, Map<String, JQuickSqlFunctionCallExpression> transformations) {
            return monitor(() -> delegate.transform(dataset, transformations));
        }

        @Override
        public JQuickDataSet sort(JQuickDataSet dataset, List<JQuickSqlOrderByExpression> orderByExpressions) {
            return monitor(() -> delegate.sort(dataset, orderByExpressions));
        }

        @Override
        public JQuickDataSet aggregate(JQuickDataSet dataset, List<String> groupBy, Map<String, JQuickSqlFunctionCallExpression> aggregations) {
            return monitor(() -> delegate.aggregate(dataset, groupBy, aggregations));
        }

        @Override
        public JQuickDataSet alias(JQuickDataSet dataset, Map<String, JQuickSqlExpression> aliases) {
            return monitor(() -> delegate.alias(dataset, aliases));
        }

        @Override
        public JQuickDataSet limit(JQuickDataSet dataset, Integer limit, Integer offset) {
            return monitor(() -> delegate.limit(dataset, limit, offset));
        }

        private <T> T monitor(Supplier<T> supplier) {
            long start = System.currentTimeMillis();
            boolean success = false;
            try {
                T result = supplier.get();
                success = true;
                return result;
            } finally {
                long duration = System.currentTimeMillis() - start;
                stats.recordOperation(duration, success);
            }
        }

        @FunctionalInterface
        private interface Supplier<T> {
            T get();
        }
    }
}
