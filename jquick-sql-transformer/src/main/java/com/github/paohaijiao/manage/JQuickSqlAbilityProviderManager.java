package com.github.paohaijiao.manage;

import com.github.paohaijiao.client.impl.JQuickForkJoinClient;
import com.github.paohaijiao.console.JConsole;
import com.github.paohaijiao.forkjoin.JQuickSqlForkJoinAbilityProvider;
import com.github.paohaijiao.provider.JQuickSqlAbilityProvider;
import com.github.paohaijiao.spi.ServiceLoader;
import com.github.paohaijiao.spi.constants.PriorityConstants;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.spark.sql.SparkSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * JQuickSqlAbilityProvider 管理器
 * 负责管理和选择最优的 Provider 实现
 */
public class JQuickSqlAbilityProviderManager {


    private static volatile JQuickSqlAbilityProviderManager instance;

    private final Map<String, JQuickSqlAbilityProvider> providerCache;

    private final List<ProviderChangeListener> listeners;

    private JQuickSqlAbilityProvider defaultProvider;

    private JQuickSqlAbilityProvider currentProvider;

    private ProviderSelectionStrategy selectionStrategy;

    private boolean autoOptimize = true;

    private JQuickSqlAbilityProviderManager() {
        this.providerCache = new ConcurrentHashMap<>();
        this.listeners = new CopyOnWriteArrayList<>();
        this.selectionStrategy = new DefaultSelectionStrategy();
        loadAllProviders();
    }

    public static JQuickSqlAbilityProviderManager getInstance() {
        if (instance == null) {
            synchronized (JQuickSqlAbilityProviderManager.class) {
                if (instance == null) {
                    instance = new JQuickSqlAbilityProviderManager();
                }
            }
        }
        return instance;
    }

    /**
     * 加载所有 Provider
     */
    private void loadAllProviders() {
        JConsole console = JConsole.initConsoleEnvironment();
        console.info("开始加载 JQuickSqlAbilityProvider 实现...");
        List<JQuickSqlAbilityProvider> providers = ServiceLoader.loadServicesByPriority(JQuickSqlAbilityProvider.class);
        if (providers.isEmpty()) {
            console.warn("未找到任何 JQuickSqlAbilityProvider 实现，将使用默认实现");
            defaultProvider = new com.github.paohaijiao.local.JQuickSqlLocalAbilityProvider();
            currentProvider = defaultProvider;
            providerCache.put("default", defaultProvider);
        } else {
            defaultProvider = providers.get(0);
            currentProvider = defaultProvider;
            console.info("找到 " + providers.size() + " 个 Provider 实现");
            ServiceLoader.printServicePriorities(JQuickSqlAbilityProvider.class);
        }
        notifyProviderLoaded();
    }

    /**
     * 获取默认 Provider（优先级最高的）
     */
    public JQuickSqlAbilityProvider getDefaultProvider() {
        return defaultProvider;
    }

    /**
     * 获取当前使用的 Provider
     */
    public JQuickSqlAbilityProvider getCurrentProvider() {
        return currentProvider;
    }

    /**
     * 切换 Provider
     */
    public synchronized void switchProvider(JQuickSqlAbilityProvider provider) {
        JConsole console = JConsole.initConsoleEnvironment();
        if (provider == null) {
            throw new IllegalArgumentException("Provider cannot be null");
        }
        JQuickSqlAbilityProvider oldProvider = this.currentProvider;
        this.currentProvider = provider;
        console.info(String.format("切换 Provider: %s -> %s", oldProvider.getClass().getSimpleName(), provider.getClass().getSimpleName()));
        notifyProviderChanged(oldProvider, provider);
    }

    /**
     * 根据名称获取 Provider
     */
    public JQuickSqlAbilityProvider getProvider(String name) {
        return providerCache.computeIfAbsent(name, key -> {
            List<JQuickSqlAbilityProvider> providers = ServiceLoader.loadServices(JQuickSqlAbilityProvider.class);
            return providers.stream()
                    .filter(p -> p.getClass().getSimpleName().toLowerCase().contains(key.toLowerCase()))
                    .findFirst()
                    .orElse(defaultProvider);
        });
    }

    /**
     * 根据类名获取 Provider
     */
    public JQuickSqlAbilityProvider getProviderByClass(Class<? extends JQuickSqlAbilityProvider> clazz) {
        List<JQuickSqlAbilityProvider> providers = ServiceLoader.loadServices(JQuickSqlAbilityProvider.class);
        return providers.stream()
                .filter(p -> p.getClass().equals(clazz))
                .findFirst()
                .orElse(null);
    }

    /**
     * 获取所有 Provider
     */
    public List<JQuickSqlAbilityProvider> getAllProviders() {
        return ServiceLoader.loadServicesByPriority(JQuickSqlAbilityProvider.class);
    }

    /**
     * 获取 Spark Provider（需要传入 SparkSession）
     */
    public JQuickSqlAbilityProvider getSparkProvider(SparkSession spark) {
        String cacheKey = "spark_" + spark.hashCode();
        return providerCache.computeIfAbsent(cacheKey, key -> new com.github.paohaijiao.spark.JQuickSqlSparkAbilityProvider(spark));
    }

    /**
     * 获取 Flink Provider（需要传入 StreamExecutionEnvironment）
     */
    public JQuickSqlAbilityProvider getFlinkProvider(StreamExecutionEnvironment env) {
        String cacheKey = "flink_" + env.hashCode();
        return providerCache.computeIfAbsent(cacheKey, key -> new com.github.paohaijiao.flink.JQuickSqlFlinkAbilityProvider(env));
    }

    /**
     * 获取本地多线程 Provider
     */
    public JQuickSqlAbilityProvider getLocalNProvider() {
        return providerCache.computeIfAbsent("localN", key -> new JQuickSqlForkJoinAbilityProvider());
    }
    public JQuickSqlAbilityProvider getForkJoin(JQuickForkJoinClient client) {
        return providerCache.computeIfAbsent("forkjoin", key -> new JQuickSqlForkJoinAbilityProvider());
    }


    /**
     * 获取本地单线程 Provider
     */
    public JQuickSqlAbilityProvider getLocalProvider() {
        return providerCache.computeIfAbsent("local", key -> new com.github.paohaijiao.local.JQuickSqlLocalAbilityProvider());
    }

    /**
     * 获取 MapReduce Provider
     */
    public JQuickSqlAbilityProvider getMapReduceProvider() {
        return providerCache.computeIfAbsent("mapreduce", key -> new com.github.paohaijiao.mapreduce.JQuickSqlMapReduceAbilityProvider());
    }

    /**
     * 根据条件动态选择 Provider
     */
    public JQuickSqlAbilityProvider selectProvider(JQuickSqlProviderContext context) {
        JQuickSqlAbilityProvider selected = selectionStrategy.select(context, this);
        if (autoOptimize && selected != currentProvider) {
            switchProvider(selected);
        }
        return selected;
    }

    /**
     * 根据数据量自动选择
     */
    public JQuickSqlAbilityProvider selectByDataSize(long dataSize) {
        JQuickSqlProviderContext context = new JQuickSqlProviderContext()
                .setDataSetSize(dataSize)
                .setParallelEnabled(true);
        return selectProvider(context);
    }

    /**
     * 根据执行环境自动选择
     */
    public JQuickSqlAbilityProvider selectByEnvironment() {
        JConsole console = JConsole.initConsoleEnvironment();
        try {
            // Spark 2.x 使用 SparkSession.getActiveSession()
            Object activeSession = SparkSession.getActiveSession();
            boolean isDefined = (boolean) activeSession.getClass().getMethod("isDefined").invoke(activeSession);
            if (isDefined) {
                Object sparkObj = activeSession.getClass().getMethod("get").invoke(activeSession);
                if (sparkObj instanceof SparkSession) {
                    console.debug("检测到 Spark 环境，选择 Spark Provider");
                    return getSparkProvider((SparkSession) sparkObj);
                }
            }
        } catch (Exception e) {
            console.debug("Spark 环境不可用: " + e.getMessage());
        }
        try {
            // Flink 环境检测逻辑
            Class.forName("org.apache.flink.streaming.api.environment.StreamExecutionEnvironment");
            console.debug("检测到 Flink 环境，选择 Flink Provider");
             return getFlinkProvider(StreamExecutionEnvironment.getExecutionEnvironment());
        } catch (ClassNotFoundException e) {
            console.debug("Flink 环境不可用");
        }
        // 默认返回本地多线程
        return getLocalNProvider();
    }

    /**
     * 获取 Provider 性能统计
     */
    public Map<String, ProviderStats> getProviderStats() {
        Map<String, ProviderStats> stats = new ConcurrentHashMap<>();
        for (Map.Entry<String, JQuickSqlAbilityProvider> entry : providerCache.entrySet()) {
            stats.put(entry.getKey(), new ProviderStats(entry.getValue()));
        }
        return stats;
    }

    /**
     * 记录操作性能
     */
    public void recordOperation(String operation, long duration, boolean success) {
        JConsole console = JConsole.initConsoleEnvironment();
        if (currentProvider != null) {
            String providerName = currentProvider.getClass().getSimpleName();
            console.debug(String.format("[Performance] %s.%s - duration: %d ms, success: %s", providerName, operation, duration, success));
        }
    }

    /**
     * 比较两个 Provider 的性能
     */
    public ProviderComparison compareProviders(JQuickSqlAbilityProvider provider1, JQuickSqlAbilityProvider provider2) {
        return new ProviderComparison(provider1, provider2);
    }

    /**
     * 获取推荐 Provider
     */
    public JQuickSqlAbilityProvider getRecommendedProvider(ProviderRequirement requirement) {
        List<JQuickSqlAbilityProvider> providers = getAllProviders();
        // 根据需求打分
        Map<JQuickSqlAbilityProvider, Integer> scores = new HashMap<>();
        for (JQuickSqlAbilityProvider provider : providers) {
            int score = calculateScore(provider, requirement);
            scores.put(provider, score);
        }
        return scores.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(defaultProvider);
    }

    private int calculateScore(JQuickSqlAbilityProvider provider, ProviderRequirement requirement) {
        int score = 0;
        String name = provider.getClass().getSimpleName().toLowerCase();
        if (requirement.isDistributed()) {
            if (name.contains("spark") || name.contains("flink")) score += 10;
            if (name.contains("local")) score -= 5;
        }
        if (requirement.isStreaming()) {
            if (name.contains("flink")) score += 10;
            if (name.contains("spark")) score += 5;
        }
        if (requirement.isHighPerformance()) {
            if (name.contains("n") || name.contains("spark")) score += 10;
        }
        if (requirement.isLowMemory()) {
            if (name.contains("local") && !name.contains("n")) score += 10;
        }
        int priority = getProviderPriority(provider);
        score += (10000 - priority) / 100;
        return score;
    }

    /**
     * 添加监听器
     */
    public void addListener(ProviderChangeListener listener) {
        if (listener != null) {
            listeners.add(listener);
        }
    }

    /**
     * 移除监听器
     */
    public void removeListener(ProviderChangeListener listener) {
        listeners.remove(listener);
    }

    private void notifyProviderLoaded() {
        for (ProviderChangeListener listener : listeners) {
            listener.onProviderLoaded(currentProvider);
        }
    }

    private void notifyProviderChanged(JQuickSqlAbilityProvider oldProvider, JQuickSqlAbilityProvider newProvider) {
        for (ProviderChangeListener listener : listeners) {
            listener.onProviderChanged(oldProvider, newProvider);
        }
    }

    public void setSelectionStrategy(ProviderSelectionStrategy strategy) {
        JConsole console = JConsole.initConsoleEnvironment();
        this.selectionStrategy = strategy;
        console.info("切换选择策略: " + strategy.getClass().getSimpleName());
    }

    public boolean isAutoOptimize() {
        return autoOptimize;
    }

    public void setAutoOptimize(boolean autoOptimize) {
        JConsole console = JConsole.initConsoleEnvironment();
        this.autoOptimize = autoOptimize;
        console.info("自动优化: " + (autoOptimize ? "启用" : "禁用"));
    }

    public void reload() {
        JConsole console = JConsole.initConsoleEnvironment();
        console.info("重新加载所有 Provider...");
        providerCache.clear();
        ServiceLoader.reload(JQuickSqlAbilityProvider.class);
        loadAllProviders();
    }

    /**
     * 获取所有 Provider 信息
     */
    public List<ProviderInfo> getAllProviderInfo() {
        List<JQuickSqlAbilityProvider> providers = getAllProviders();
        List<ProviderInfo> infos = new ArrayList<>();
        for (JQuickSqlAbilityProvider provider : providers) {
            infos.add(new ProviderInfo(provider));
        }
        return infos;
    }

    /**
     * 获取可用的 Provider 类型
     */
    public List<String> getAvailableProviderTypes() {
        return getAllProviders().stream()
                .map(p -> p.getClass().getSimpleName())
                .collect(Collectors.toList());
    }

    /**
     * 检查 Provider 是否可用
     */
    public boolean isProviderAvailable(Class<? extends JQuickSqlAbilityProvider> clazz) {
        return getAllProviders().stream()
                .anyMatch(p -> p.getClass().equals(clazz));
    }

    /**
     * 获取 Provider 优先级
     */
    public int getProviderPriority(JQuickSqlAbilityProvider provider) {
        com.github.paohaijiao.spi.anno.Priority annotation =
                provider.getClass().getAnnotation(com.github.paohaijiao.spi.anno.Priority.class);
        return annotation != null ? annotation.value() : PriorityConstants.DEFAULT;
    }

    /**
     * 打印所有 Provider 信息
     */
    public void printProviderInfo() {
        JConsole console = JConsole.initConsoleEnvironment();
        console.info("=== JQuickSqlAbilityProvider 信息 ===");
        console.info("当前 Provider: " + (currentProvider != null ? currentProvider.getClass().getSimpleName() : "null"));
        console.info("默认 Provider: " + (defaultProvider != null ? defaultProvider.getClass().getSimpleName() : "null"));
        console.info("自动优化: " + autoOptimize);
        console.info("");
        ServiceLoader.printServicePriorities(JQuickSqlAbilityProvider.class);
    }

    /**
     * 打印性能报告
     */
    public void printPerformanceReport() {
        JConsole console = JConsole.initConsoleEnvironment();
        console.info("=== Provider 性能报告 ===");
        Map<String, ProviderStats> stats = getProviderStats();
        for (Map.Entry<String, ProviderStats> entry : stats.entrySet()) {
            console.info(entry.getValue().toString());
        }
    }

    /**
     * 执行带监控的操作
     */
    public <T> T executeWithMonitor(String operationName, ProviderOperation<T> operation) {
        long startTime = System.currentTimeMillis();
        boolean success = false;

        try {
            T result = operation.execute();
            success = true;
            return result;
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            recordOperation(operationName, duration, success);
        }
    }

    /**
     * Provider 选择策略接口
     */
    public interface ProviderSelectionStrategy {
        JQuickSqlAbilityProvider select(JQuickSqlProviderContext context, JQuickSqlAbilityProviderManager manager);
    }

    /**
     * Provider 变更监听器
     */
    public interface ProviderChangeListener {

        void onProviderLoaded(JQuickSqlAbilityProvider provider);

        void onProviderChanged(JQuickSqlAbilityProvider oldProvider, JQuickSqlAbilityProvider newProvider);
    }

    /**
     * Provider 操作接口
     */
    @FunctionalInterface
    public interface ProviderOperation<T> {
        T execute();
    }

    /**
     * 默认选择策略
     */
    public static class DefaultSelectionStrategy implements ProviderSelectionStrategy {
        @Override
        public JQuickSqlAbilityProvider select(JQuickSqlProviderContext context, JQuickSqlAbilityProviderManager manager) {
            if (context == null) {
                return manager.getDefaultProvider();
            }
            if (context.isDistributed()) {
                if (context.isStreaming() && context.getFlinkEnv() != null) {
                    return manager.getFlinkProvider(context.getFlinkEnv());
                }
                if (context.getSparkSession() != null) {
                    return manager.getSparkProvider(context.getSparkSession());
                }
            }
            if (context.getDataSetSize() > 10000 && context.isParallelEnabled()) {
                return manager.getLocalNProvider();
            }

            return manager.getLocalProvider();
        }
    }

    /**
     * 高性能选择策略（优先使用分布式）
     */
    public static class HighPerformanceStrategy implements ProviderSelectionStrategy {
        @Override
        public JQuickSqlAbilityProvider select(JQuickSqlProviderContext context, JQuickSqlAbilityProviderManager manager) {
            if (context.getSparkSession() != null) {
                return manager.getSparkProvider(context.getSparkSession());
            }
            if (context.getFlinkEnv() != null) {
                return manager.getFlinkProvider(context.getFlinkEnv());
            }
            return manager.getLocalNProvider();
        }
    }

    /**
     * 低内存选择策略
     */
    public static class LowMemoryStrategy implements ProviderSelectionStrategy {
        @Override
        public JQuickSqlAbilityProvider select(JQuickSqlProviderContext context, JQuickSqlAbilityProviderManager manager) {
            return manager.getLocalProvider();
        }
    }

    /**
     * Provider 上下文信息
     */
    public static class JQuickSqlProviderContext {

        private boolean distributed = false;

        private boolean streaming = false;

        private long dataSetSize = 0;

        private boolean parallelEnabled = true;

        private SparkSession sparkSession;

        private StreamExecutionEnvironment flinkEnv;

        private String preferredType;

        public boolean isDistributed() {
            return distributed;
        }

        public JQuickSqlProviderContext setDistributed(boolean distributed) {
            this.distributed = distributed;
            return this;
        }

        public boolean isStreaming() {
            return streaming;
        }

        public JQuickSqlProviderContext setStreaming(boolean streaming) {
            this.streaming = streaming;
            return this;
        }

        public long getDataSetSize() {
            return dataSetSize;
        }

        public JQuickSqlProviderContext setDataSetSize(long dataSetSize) {
            this.dataSetSize = dataSetSize;
            return this;
        }

        public boolean isParallelEnabled() {
            return parallelEnabled;
        }

        public JQuickSqlProviderContext setParallelEnabled(boolean parallelEnabled) {
            this.parallelEnabled = parallelEnabled;
            return this;
        }

        public SparkSession getSparkSession() {
            return sparkSession;
        }

        public JQuickSqlProviderContext setSparkSession(SparkSession sparkSession) {
            this.sparkSession = sparkSession;
            if (sparkSession != null) {
                this.distributed = true;
            }
            return this;
        }

        public StreamExecutionEnvironment getFlinkEnv() {
            return flinkEnv;
        }

        public JQuickSqlProviderContext setFlinkEnv(StreamExecutionEnvironment flinkEnv) {
            this.flinkEnv = flinkEnv;
            if (flinkEnv != null) {
                this.distributed = true;
                this.streaming = true;
            }
            return this;
        }

        public String getPreferredType() {
            return preferredType;
        }

        public JQuickSqlProviderContext setPreferredType(String preferredType) {
            this.preferredType = preferredType;
            return this;
        }
    }

    /**
     * Provider 需求描述
     */
    public static class ProviderRequirement {

        private boolean distributed = false;

        private boolean streaming = false;

        private boolean highPerformance = false;

        private boolean lowMemory = false;

        private long estimatedDataSize = 0;

        public boolean isDistributed() {
            return distributed;
        }

        public ProviderRequirement setDistributed(boolean distributed) {
            this.distributed = distributed;
            return this;
        }

        public boolean isStreaming() {
            return streaming;
        }

        public ProviderRequirement setStreaming(boolean streaming) {
            this.streaming = streaming;
            return this;
        }

        public boolean isHighPerformance() {
            return highPerformance;
        }

        public ProviderRequirement setHighPerformance(boolean highPerformance) {
            this.highPerformance = highPerformance;
            return this;
        }

        public boolean isLowMemory() {
            return lowMemory;
        }

        public ProviderRequirement setLowMemory(boolean lowMemory) {
            this.lowMemory = lowMemory;
            return this;
        }

        public long getEstimatedDataSize() {
            return estimatedDataSize;
        }

        public ProviderRequirement setEstimatedDataSize(long estimatedDataSize) {
            this.estimatedDataSize = estimatedDataSize;
            return this;
        }
    }

    /**
     * Provider 信息类
     */
    public static class ProviderInfo {

        private final String className;

        private final String simpleName;

        private final int priority;

        private final String priorityLevel;

        private final boolean isDefault;

        private final boolean isCurrent;

        public ProviderInfo(JQuickSqlAbilityProvider provider) {
            this.className = provider.getClass().getName();
            this.simpleName = provider.getClass().getSimpleName();
            this.priority = getPriorityValue(provider);
            this.priorityLevel = PriorityConstants.getPriorityName(priority);
            this.isDefault = provider.equals(JQuickSqlAbilityProviderManager.getInstance().defaultProvider);
            this.isCurrent = provider.equals(JQuickSqlAbilityProviderManager.getInstance().currentProvider);
        }

        private int getPriorityValue(JQuickSqlAbilityProvider provider) {
            com.github.paohaijiao.spi.anno.Priority annotation =
                    provider.getClass().getAnnotation(com.github.paohaijiao.spi.anno.Priority.class);
            return annotation != null ? annotation.value() : PriorityConstants.DEFAULT;
        }

        public String getClassName() {
            return className;
        }

        public String getSimpleName() {
            return simpleName;
        }

        public int getPriority() {
            return priority;
        }

        public String getPriorityLevel() {
            return priorityLevel;
        }

        public boolean isDefault() {
            return isDefault;
        }

        public boolean isCurrent() {
            return isCurrent;
        }

        @Override
        public String toString() {
            return String.format("%s [Priority: %d (%s)] %s%s", simpleName, priority, priorityLevel, isDefault ? "[DEFAULT] " : "", isCurrent ? "[CURRENT]" : "");
        }
    }

    /**
     * Provider 统计信息
     */
    public static class ProviderStats {

        private final String providerName;

        private long operationCount = 0;

        private long totalDuration = 0;

        private long successCount = 0;

        private long failCount = 0;

        public ProviderStats(JQuickSqlAbilityProvider provider) {
            this.providerName = provider.getClass().getSimpleName();
        }

        public synchronized void recordOperation(long duration, boolean success) {
            operationCount++;
            totalDuration += duration;
            if (success) {
                successCount++;
            } else {
                failCount++;
            }
        }

        public double getAvgDuration() {
            return operationCount > 0 ? (double) totalDuration / operationCount : 0;
        }

        public double getSuccessRate() {
            return operationCount > 0 ? (double) successCount / operationCount * 100 : 0;
        }

        @Override
        public String toString() {
            return String.format("%s - Operations: %d, Avg Duration: %.2f ms, Success Rate: %.2f%%", providerName, operationCount, getAvgDuration(), getSuccessRate());
        }
    }

    /**
     * Provider 比较结果
     */
    public static class ProviderComparison {

        private final JQuickSqlAbilityProvider provider1;

        private final JQuickSqlAbilityProvider provider2;

        public ProviderComparison(JQuickSqlAbilityProvider provider1, JQuickSqlAbilityProvider provider2) {
            this.provider1 = provider1;
            this.provider2 = provider2;
        }

        public void print() {
            JConsole console = JConsole.initConsoleEnvironment();
            console.info("=== Provider 比较 ===");
            console.info("Provider 1: " + provider1.getClass().getSimpleName());
            console.info("Provider 2: " + provider2.getClass().getSimpleName());
            int priority1 = getInstance().getProviderPriority(provider1);
            int priority2 = getInstance().getProviderPriority(provider2);
            console.info("优先级: " + priority1 + " vs " + priority2);
            console.info("推荐: " + (priority1 <= priority2 ? provider1.getClass().getSimpleName() : provider2.getClass().getSimpleName()));
        }
    }
}