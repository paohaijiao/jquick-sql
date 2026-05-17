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
package com.github.paohaijiao.engine;

import com.github.paohaijiao.ast.JQuickQueryNode;
import com.github.paohaijiao.ast2logic.JQuickASTToLogicalPlanVisitor;
import com.github.paohaijiao.config.JQuickClusterConfig;
import com.github.paohaijiao.config.JQuickConfiguration;
import com.github.paohaijiao.console.JConsole;
import com.github.paohaijiao.context.JQuickExecutionContext;
import com.github.paohaijiao.distributed.domain.JQuickExecutionPlan;
import com.github.paohaijiao.distributed.domain.TaskResult;
import com.github.paohaijiao.distributed.domain.WorkerManagerConfig;
import com.github.paohaijiao.distributed.domain.WorkerRpcClient;
import com.github.paohaijiao.executor.JQuickSQLExecutor;
import com.github.paohaijiao.fragment.JQuickFragmenter;
import com.github.paohaijiao.logic.JQuickLogicalPlanNode;
import com.github.paohaijiao.logic.domain.JQuickSortNode;
import com.github.paohaijiao.logic2physical.JQuickPhysicalPlanGenerator;
import com.github.paohaijiao.optimizer.JQuickLogicalPlanOptimizer;
import com.github.paohaijiao.parser.JQuickSQLLexer;
import com.github.paohaijiao.parser.JQuickSQLParser;
import com.github.paohaijiao.physical.JQuickPhysicalPlanNode;
import com.github.paohaijiao.physical.domain.JQuickPhysicalColumn;
import com.github.paohaijiao.scheduler.JQuickScheduler;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.toplogy.JQuickClusterTopology;
import com.github.paohaijiao.worker.JQuickWorkerManager;
import com.github.paohaijiao.worker.JQuickWorkerNode;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

public class JQuickSQLEngine {

    private static JConsole console = JConsole.initConsoleEnvironment();

    private final JQuickLogicalPlanOptimizer optimizer;

    private final JQuickPhysicalPlanGenerator physicalGenerator;

    private boolean distributedMode = false;

    private JQuickFragmenter fragmenter;

    private JQuickScheduler scheduler;

    private JQuickWorkerManager workerManager;

    public JQuickSQLEngine() {
        this.optimizer = new JQuickLogicalPlanOptimizer();
        this.physicalGenerator = new JQuickPhysicalPlanGenerator();
        this.workerManager = new JQuickWorkerManager();
        this.fragmenter = new JQuickFragmenter();
        this.scheduler = new JQuickScheduler(null, workerManager);
    }

    public JQuickDataSet execute(String sql) {
        return execute(sql, new JQuickExecutionContext());
    }

    public JQuickDataSet execute(String sql, JQuickExecutionContext context) {
        try {
            JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(sql));
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            //语法分析
            JQuickSQLParser parser = new JQuickSQLParser(tokens);
            JQuickSQLParser.QueryContext parseTree = parser.query();
            // 构建AST
            JQuickQueryNode ast = buildAST(sql);
            // AST → 逻辑计划
            JQuickASTToLogicalPlanVisitor visitor = new JQuickASTToLogicalPlanVisitor();
            JQuickLogicalPlanNode logicalPlan = visitor.visit(ast);
            // 5. 逻辑计划优化
            JQuickLogicalPlanNode optimizedPlan = optimizer.optimize(logicalPlan);

            // 6. 逻辑计划 → 物理计划（带成本优化）
            JQuickPhysicalPlanGenerator physicalGenerator = new JQuickPhysicalPlanGenerator();
            JQuickPhysicalPlanNode physicalPlan = physicalGenerator.generate(optimizedPlan);
            // 7. 根据执行模式选择执行方式
            if (isDistributedMode()) {
                // 分布式执行
                return executeDistributed(physicalPlan, context);
            } else {
                // 单机执行（使用执行器）
                return executeLocal(physicalPlan, context);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute SQL: " + sql, e);
        }
    }

    public JQuickDataSet execute(String sql, Map<String, Object> parameters) {
        JQuickExecutionContext context = new JQuickExecutionContext();
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            Map<String, Object> map = context.getParameters();
            map.put(entry.getKey(), entry.getValue());
        }
        return execute(sql, context);
    }

    private JQuickQueryNode buildAST(String sql) {
        JQuickSQLExecutor executor = new JQuickSQLExecutor();
        JQuickQueryNode node = executor.execute(sql);
        return node;
    }

    /**
     * 分布式执行模式
     */
    private JQuickDataSet executeDistributed(JQuickPhysicalPlanNode physicalPlan, JQuickExecutionContext context) {
        long startTime = System.currentTimeMillis();

        try {
            JQuickClusterTopology cluster = getOrCreateClusterTopology(context);
            if (cluster == null || cluster.getAvailableWorkers() == 0) {
                console.warn("No available workers, falling back to local execution");
                return executeLocal(physicalPlan, context);
            }
            console.info("Starting distributed execution with {} workers", cluster.getAvailableWorkers());
            // 2. 创建Worker管理器
            JQuickWorkerManager workerManager = createWorkerManager(cluster, context);

            // 3. 创建调度器并生成执行计划
            JQuickScheduler scheduler = new JQuickScheduler(cluster);
            JQuickExecutionPlan executionPlan = scheduler.schedule(physicalPlan);

            console.info("Generated execution plan: stages={}, tasks={}", executionPlan.getStageCount(), executionPlan.getTaskCount());

            // 4. 注册Worker节点到管理器
            registerWorkers(workerManager, cluster);

            // 5. 按Stage顺序执行任务
            JQuickDataSet result = executeStages(workerManager, executionPlan, context);

            long duration = System.currentTimeMillis() - startTime;
            console.info("Distributed execution completed in {} ms", duration);

            // 6. 记录执行统计
            logExecutionStats(workerManager, duration);

            return result;

        } catch (Exception e) {
            console.error("Distributed execution failed", e);
            console.info("Falling back to local execution");
            return executeLocal(physicalPlan, context);
        }
    }

    /**
     * 单机执行模式（使用执行器）
     */
    private JQuickDataSet executeLocal(JQuickPhysicalPlanNode physicalPlan, JQuickExecutionContext context) {
        // 使用物理计划执行器执行
        JQuickPhysicalPlanExecutor executor = new JQuickPhysicalPlanExecutor();
        return executor.execute(physicalPlan, context);
    }

    /**
     * 判断是否为分布式模式
     */
    private boolean isDistributedMode() {
        // 可以从配置中读取
        return System.getProperty("jquick.distributed.enabled", "false").equals("true");
    }

    private JQuickDataSet mergeResults(List<TaskResult> results, JQuickExecutionPlan plan) {
        if (results.isEmpty()) {
            return new JQuickDataSet(new ArrayList<>(), new ArrayList<>());
        }

        // 获取输出Schema
        List<JQuickPhysicalColumn> schema = plan.getOutputSchema();
        List<String> columnNames = schema.stream()
                .map(JQuickPhysicalColumn::getName)
                .collect(Collectors.toList());

        // 收集所有行
        List<List<Object>> allRows = new ArrayList<>();
        for (TaskResult result : results) {
            if (result.isSuccess() && result.getData() != null) {
                if (result.getData() instanceof JQuickDataSet) {
                    allRows.addAll(((JQuickDataSet) result.getData()).getRows());
                } else if (result.getData() instanceof List) {
                    allRows.addAll((List<List<Object>>) result.getData());
                }
            }
        }

        // 如果有ORDER BY，需要排序
        if (plan.hasGlobalSort()) {
            allRows.sort(plan.getComparator());
        }

        // 如果有LIMIT，需要截断
        if (plan.hasLimit()) {
            int limit = plan.getLimit();
            int offset = plan.getOffset();
            if (offset < allRows.size()) {
                int end = Math.min(offset + limit, allRows.size());
                allRows = allRows.subList(offset, end);
            } else {
                allRows = new ArrayList<>();
            }
        }

        return new JQuickDataSet(allRows, columnNames);
    }

    /**
     * 汇聚最终结果集
     * 包括：排序、去重、聚合等最终处理
     */
    private JQuickDataSet finalizeResult(JQuickDataSet result, JQuickExecutionContext context) {
        if (result == null || result.isEmpty()) {
            return result;
        }
        // 1. 如果有全局排序要求，进行最终排序
        if (context.hasGlobalSort()) {
            result = applyGlobalSort(result, context);
        }
        // 2. 如果有全局去重要求，进行最终去重
        if (context.hasGlobalDistinct()) {
            result = result.distinct();
        }
        // 3. 如果有LIMIT限制，进行最终截断
        if (context.hasLimit()) {
            result = applyGlobalLimit(result, context);
        }
        // 4. 记录执行统计
        if (context.isProfileEnabled()) {
            context.recordResultSize(result.size());
        }
        return result;
    }

    /**
     * 应用全局排序
     */
    private JQuickDataSet applyGlobalSort(JQuickDataSet result, JQuickExecutionContext context) {
        List<JQuickSortNode.OrderByItem> sortItems = context.getGlobalSortItems();
        if (sortItems == null || sortItems.isEmpty()) {
            return result;
        }
        JQuickDataSet sorted = result;
        for (JQuickSortNode.OrderByItem item : sortItems) {
            sorted = sorted.orderBy(item.getColumnName(), item.isAscending());
        }
        return sorted;
    }

    /**
     * 应用全局LIMIT
     */
    private JQuickDataSet applyGlobalLimit(JQuickDataSet result, JQuickExecutionContext context) {
        int limit = context.getLimit();
        int offset = context.getOffset();

        if (offset > 0) {
            result = result.skip(offset);
        }
        return result.limit(limit);
    }

    /**
     * 设置分布式模式
     */
    public void setDistributedMode(boolean enabled) {
        this.distributedMode = enabled;
    }

    /**
     * 添加工作节点
     */
    public void addWorker(String host, int cores) {
        workerManager.registerWorker(host, cores);
    }

    /**
     * 获取或创建集群拓扑
     */
    private JQuickClusterTopology getOrCreateClusterTopology(JQuickExecutionContext context) {
        //从上下文获取已有的集群拓扑
        JQuickClusterTopology existingTopology = context.getClusterTopology();
        if (existingTopology != null && existingTopology.getAvailableWorkers() > 0) {
            console.debug("Using existing cluster topology with {} workers", existingTopology.getAvailableWorkers());
            return existingTopology;
        }

        //从配置创建新的集群拓扑
        JQuickConfiguration config = context.getConfiguration();
        if (config == null) {
            config = loadDefaultConfiguration();
        }

        List<JQuickWorkerNode> workers = new ArrayList<>();
        workers.addAll(config.getWorkerNodes());
        workers.addAll(loadWorkersFromEnv());
        workers.addAll(loadWorkersFromSystemProperties());
        if (workers.isEmpty()) {
            workers.addAll(discoverWorkersFromServiceRegistry(config));
        }
        if (workers.isEmpty()) {
            workers.addAll(loadWorkersFromStaticConfig());
        }

        if (workers.isEmpty()) {
            console.warn("No workers found, distributed execution not available");
            return null;
        }
        // 4. 创建集群配置
        JQuickClusterConfig clusterConfig = createClusterConfig(config);
        // 5. 构建并返回集群拓扑
        JQuickClusterTopology topology = new JQuickClusterTopology(workers, clusterConfig);
        // 6. 缓存到上下文
        context.setClusterTopology(topology);
        console.info("Created cluster topology with {} workers ({} available)", workers.size(), topology.getAvailableWorkers());
        return topology;
    }

    /**
     * 加载默认配置
     */
    private JQuickConfiguration loadDefaultConfiguration() {
        JQuickConfiguration config = new JQuickConfiguration();

        // 设置默认值
        config.setDefaultParallelism(Runtime.getRuntime().availableProcessors());
        config.setMaxTaskRetries(3);
        config.setTaskTimeoutMs(3600000); // 1小时
        config.setHeartbeatIntervalMs(5000);
        config.setHeartbeatTimeoutMs(30000);
        config.setMaxConcurrentTasksPerWorker(4);

        // 尝试加载配置文件
        try {
            // 从 classpath 加载 jquick.properties
            InputStream is = getClass().getResourceAsStream("/jquick.properties");
            if (is != null) {
                Properties props = new Properties();
                props.load(is);
                applyProperties(config, props);
            }

            // 从外部配置文件加载
            String configPath = System.getProperty("jquick.config.path");
            if (configPath != null) {
                File configFile = new File(configPath);
                if (configFile.exists()) {
                    try (FileInputStream fis = new FileInputStream(configFile)) {
                        Properties props = new Properties();
                        props.load(fis);
                        applyProperties(config, props);
                    }
                }
            }
        } catch (IOException e) {
            console.warn("Failed to load configuration: {}", e.getMessage());
        }

        return config;
    }

    /**
     * 应用配置属性
     */
    private void applyProperties(JQuickConfiguration config, Properties props) {
        if (props.containsKey("jquick.parallelism.default")) {
            config.setDefaultParallelism(Integer.parseInt(props.getProperty("jquick.parallelism.default")));
        }
        if (props.containsKey("jquick.task.retries.max")) {
            config.setMaxTaskRetries(Integer.parseInt(props.getProperty("jquick.task.retries.max")));
        }
        if (props.containsKey("jquick.task.timeout.ms")) {
            config.setTaskTimeoutMs(Long.parseLong(props.getProperty("jquick.task.timeout.ms")));
        }
        if (props.containsKey("jquick.heartbeat.interval.ms")) {
            config.setHeartbeatIntervalMs(Integer.parseInt(props.getProperty("jquick.heartbeat.interval.ms")));
        }
        if (props.containsKey("jquick.heartbeat.timeout.ms")) {
            config.setHeartbeatTimeoutMs(Integer.parseInt(props.getProperty("jquick.heartbeat.timeout.ms")));
        }
        if (props.containsKey("jquick.worker.max.concurrent")) {
            config.setMaxConcurrentTasksPerWorker(Integer.parseInt(props.getProperty("jquick.worker.max.concurrent")));
        }
        if (props.containsKey("jquick.service.discovery.url")) {
            config.setServiceDiscoveryUrl(props.getProperty("jquick.service.discovery.url"));
        }
    }

    /**
     * 从环境变量加载Worker节点
     */
    private List<JQuickWorkerNode> loadWorkersFromEnv() {
        List<JQuickWorkerNode> workers = new ArrayList<>();
        // 环境变量格式: JQUICK_WORKERS=host1:port1,host2:port2,host3:port3
        String workersEnv = System.getenv("JQUICK_WORKERS");
        if (workersEnv != null && !workersEnv.isEmpty()) {
            String[] workerStrs = workersEnv.split(",");
            for (int i = 0; i < workerStrs.length; i++) {
                String workerStr = workerStrs[i].trim();
                String[] parts = workerStr.split(":");
                if (parts.length >= 2) {
                    String host = parts[0];
                    int port = Integer.parseInt(parts[1]);
                    String workerId = "worker-" + i;
                    JQuickWorkerNode worker = createWorkerNode(workerId, host, port, i);
                    workers.add(worker);
                }
            }
        }

        // 环境变量格式: JQUICK_WORKER_1_HOST, JQUICK_WORKER_1_PORT
        int index = 1;
        while (true) {
            String host = System.getenv("JQUICK_WORKER_" + index + "_HOST");
            String portStr = System.getenv("JQUICK_WORKER_" + index + "_PORT");
            if (host == null || portStr == null) {
                break;
            }
            int port = Integer.parseInt(portStr);
            String workerId = "worker-" + (index - 1);
            JQuickWorkerNode worker = createWorkerNode(workerId, host, port, index - 1);
            workers.add(worker);
            index++;
        }

        return workers;
    }

    /**
     * 从系统属性加载Worker节点
     */
    private List<JQuickWorkerNode> loadWorkersFromSystemProperties() {
        List<JQuickWorkerNode> workers = new ArrayList<>();
        // 系统属性格式: -Djquick.workers=host1:port1,host2:port2
        String workersProp = System.getProperty("jquick.workers");
        if (workersProp != null && !workersProp.isEmpty()) {
            String[] workerStrs = workersProp.split(",");
            for (int i = 0; i < workerStrs.length; i++) {
                String workerStr = workerStrs[i].trim();
                String[] parts = workerStr.split(":");
                if (parts.length >= 2) {
                    String host = parts[0];
                    int port = Integer.parseInt(parts[1]);
                    String workerId = System.getProperty("jquick.worker." + i + ".id", "worker-" + i);
                    JQuickWorkerNode worker = createWorkerNode(workerId, host, port, i);
                    workers.add(worker);
                }
            }
        }

        // 系统属性格式: -Djquick.worker.0.host=host1 -Djquick.worker.0.port=9001
        int index = 0;
        while (true) {
            String host = System.getProperty("jquick.worker." + index + ".host");
            String portStr = System.getProperty("jquick.worker." + index + ".port");
            if (host == null || portStr == null) {
                break;
            }
            int port = Integer.parseInt(portStr);
            String workerId = System.getProperty("jquick.worker." + index + ".id", "worker-" + index);
            JQuickWorkerNode worker = createWorkerNode(workerId, host, port, index);
            workers.add(worker);
            index++;
        }

        return workers;
    }

    /**
     * 从服务注册中心发现Worker节点
     */
    private List<JQuickWorkerNode> discoverWorkersFromServiceRegistry(JQuickConfiguration config) {
        List<JQuickWorkerNode> workers = new ArrayList<>();
        String discoveryUrl = config.getServiceDiscoveryUrl();
        if (discoveryUrl == null || discoveryUrl.isEmpty()) {
            return workers;
        }
        console.info("Discovering workers from service registry: {}", discoveryUrl);
        try {
            // 解析服务发现URL
            URI uri = new URI(discoveryUrl);
            String scheme = uri.getScheme();
            switch (scheme) {
                case "zookeeper":
                    workers.addAll(discoverFromZookeeper(uri));
                    break;
                case "etcd":
                    workers.addAll(discoverFromEtcd(uri));
                    break;
                case "consul":
                    workers.addAll(discoverFromConsul(uri));
                    break;
                case "kubernetes":
                    workers.addAll(discoverFromKubernetes(uri));
                    break;
                default:
                    console.warn("Unknown service discovery scheme: {}", scheme);
            }
        } catch (Exception e) {
            console.error("Failed to discover workers from service registry: {}", e.getMessage());
        }

        return workers;
    }

    /**
     * 从ZooKeeper发现Worker
     */
    private List<JQuickWorkerNode> discoverFromZookeeper(URI uri) {
        List<JQuickWorkerNode> workers = new ArrayList<>();
        // TODO: 实现ZooKeeper服务发现
        // String zkHosts = uri.getAuthority();
        // String path = uri.getPath();
        console.info("ZooKeeper discovery not fully implemented yet");
        return workers;
    }

    /**
     * 从Etcd发现Worker
     */
    private List<JQuickWorkerNode> discoverFromEtcd(URI uri) {
        List<JQuickWorkerNode> workers = new ArrayList<>();
        // TODO: 实现Etcd服务发现
        console.info("Etcd discovery not fully implemented yet");
        return workers;
    }

    /**
     * 从Consul发现Worker
     */
    private List<JQuickWorkerNode> discoverFromConsul(URI uri) {
        List<JQuickWorkerNode> workers = new ArrayList<>();
        // TODO: 实现Consul服务发现
        console.info("Consul discovery not fully implemented yet");
        return workers;
    }

    /**
     * 从Kubernetes发现Worker
     */
    private List<JQuickWorkerNode> discoverFromKubernetes(URI uri) {
        List<JQuickWorkerNode> workers = new ArrayList<>();
        // TODO: 实现Kubernetes服务发现
        // 使用K8s API查询Pod
        console.info("Kubernetes discovery not fully implemented yet");
        return workers;
    }

    /**
     * 从静态配置加载Worker（作为fallback）
     */
    private List<JQuickWorkerNode> loadWorkersFromStaticConfig() {
        List<JQuickWorkerNode> workers = new ArrayList<>();
        // 默认本地Worker用于测试
        String defaultWorkers = System.getProperty("jquick.workers.default", "localhost:9001");
        String[] workerStrs = defaultWorkers.split(",");
        for (int i = 0; i < workerStrs.length; i++) {
            String workerStr = workerStrs[i].trim();
            String[] parts = workerStr.split(":");
            if (parts.length >= 2) {
                String host = parts[0];
                int port = Integer.parseInt(parts[1]);
                JQuickWorkerNode worker = createWorkerNode("default-worker-" + i, host, port, i);
                workers.add(worker);
            }
        }

        return workers;
    }

    /**
     * 创建Worker节点
     */
    private JQuickWorkerNode createWorkerNode(String workerId, String host, int port, int index) {
        // 获取机架和位置信息
        String rack = getRackForWorker(workerId, host);
        String location = getLocationForWorker(workerId, host);
        // 获取资源信息
        int cpuCores = getWorkerCpuCores(workerId, index);
        long memoryBytes = getWorkerMemoryBytes(workerId, index);
        // 获取扩展属性
        Map<String, String> attributes = getWorkerAttributes(workerId, host, index);
        return new JQuickWorkerNode(workerId, host, port, rack, location, cpuCores, memoryBytes, attributes);
    }

    /**
     * 获取Worker所在的机架
     */
    private String getRackForWorker(String workerId, String host) {
        // 可以从配置或DNS获取
        String rack = System.getProperty("jquick.worker." + workerId + ".rack");
        if (rack == null) {
            rack = System.getProperty("jquick.worker.rack.default", "/default-rack");
        }
        return rack;
    }

    /**
     * 获取Worker的位置
     */
    private String getLocationForWorker(String workerId, String host) {
        String location = System.getProperty("jquick.worker." + workerId + ".location");
        if (location == null) {
            location = System.getProperty("jquick.worker.location.default", "/default-rack/" + host);
        }
        return location;
    }

    /**
     * 获取Worker的CPU核心数
     */
    private int getWorkerCpuCores(String workerId, int index) {
        String coresProp = System.getProperty("jquick.worker." + workerId + ".cpu.cores");
        if (coresProp != null) {
            return Integer.parseInt(coresProp);
        }
        // 默认值：本地Worker使用本机核心数，远程Worker使用配置的默认值
        if ("localhost".equals(host) || "127.0.0.1".equals(host)) {
            return Runtime.getRuntime().availableProcessors();
        }
        return Integer.getInteger("jquick.worker.cpu.default", 4);
    }

    /**
     * 获取Worker的内存大小（字节）
     */
    private long getWorkerMemoryBytes(String workerId, int index) {
        String memoryProp = System.getProperty("jquick.worker." + workerId + ".memory.mb");
        if (memoryProp != null) {
            return Long.parseLong(memoryProp) * 1024 * 1024;
        }
        // 默认值：4GB
        return Long.getLong("jquick.worker.memory.default.mb", 4096) * 1024 * 1024;
    }

    /**
     * 获取Worker的扩展属性
     */
    private Map<String, String> getWorkerAttributes(String workerId, String host, int index) {
        Map<String, String> attributes = new HashMap<>();
        attributes.put("index", String.valueOf(index));
        attributes.put("host", host);
        // 从系统属性加载自定义属性
        String prefix = "jquick.worker." + workerId + ".attr.";
        for (Map.Entry<Object, Object> entry : System.getProperties().entrySet()) {
            String key = entry.getKey().toString();
            if (key.startsWith(prefix)) {
                String attrName = key.substring(prefix.length());
                attributes.put(attrName, entry.getValue().toString());
            }
        }

        return attributes;
    }

    /**
     * 创建集群配置
     */
    private JQuickClusterConfig createClusterConfig(JQuickClusterConfig config) {
        return new JQuickClusterConfig.Builder()
                .defaultParallelism(config.getDefaultParallelism())
                .heartbeatIntervalMs(config.getHeartbeatIntervalMs())
                .heartbeatTimeoutMs(config.getHeartbeatTimeoutMs())
                .maxConcurrentTasksPerWorker(config.getMaxConcurrentTasksPerWorker())
                .loadBalanceThreshold(2)
                .enableDataLocality(true)
                .enableRackLocality(true)
                .build();
    }

    /**
     * 验证Worker节点是否可用
     */
    private boolean validateWorker(JQuickWorkerNode worker) {
        try {
            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress(worker.getHost(), worker.getPort()), 3000);
                return true;
            }
        } catch (IOException e) {
            console.debug("Worker {}:{} is not reachable: {}", worker.getHost(), worker.getPort(), e.getMessage());
            return false;
        }
    }
    /**
     * 创建Worker管理器
     */
    private JQuickWorkerManager createWorkerManager(JQuickClusterTopology cluster, JQuickExecutionContext context) {
        // 1. 获取配置
        JQuickConfiguration config = context.getConfiguration();
        if (config == null) {
            config = new JQuickConfiguration();
        }

        // 2. 创建Worker管理器配置
        WorkerManagerConfig workerManagerConfig = createWorkerManagerConfig(config, cluster);

        // 3. 创建Worker管理器
        JQuickWorkerManager workerManager = new JQuickWorkerManager(cluster, workerManagerConfig);

        // 4. 设置事件监听器
        setupWorkerEventListeners(workerManager, context);

        // 5. 初始化RPC客户端
        initializeRpcClient(workerManager, config);

        // 6. 启动健康检查
        startHealthCheck(workerManager, config);

        // 7. 注册Worker节点
        registerWorkersToManager(workerManager, cluster, config);

        return workerManager;
    }
    /**
     * 创建Worker管理器配置
     */
    private WorkerManagerConfig createWorkerManagerConfig(JQuickConfiguration config, JQuickClusterTopology cluster) {
        WorkerManagerConfig workerConfig = new WorkerManagerConfig();

        // 从JQuickConfiguration映射到WorkerManagerConfig
        workerConfig.setHeartbeatIntervalMs(config.getHeartbeatIntervalMs())
                .setHeartbeatTimeoutMs(config.getHeartbeatTimeoutMs())
                .setMaxRetries(config.getMaxTaskRetries())
                .setTaskTimeoutMs(config.getTaskTimeoutMs())
                .setMaxConcurrentTasksPerWorker(config.getMaxConcurrentTasksPerWorker())
                .setEnableAutoRecovery(true);

        // 从集群配置中获取额外的设置
        if (cluster != null && cluster.getConfig() != null) {
            JQuickClusterConfig clusterConfig = cluster.getConfig();
            workerConfig.setHeartbeatIntervalMs(clusterConfig.getHeartbeatIntervalMs())
                    .setHeartbeatTimeoutMs(clusterConfig.getHeartbeatTimeoutMs())
                    .setMaxConcurrentTasksPerWorker(clusterConfig.getMaxConcurrentTasksPerWorker());
        }

        // 从扩展配置中获取
        Integer customMaxRetries = config.getExtension("worker.manager.max.retries", Integer.class);
        if (customMaxRetries != null) {
            workerConfig.setMaxRetries(customMaxRetries);
        }

        Long customTaskTimeout = config.getExtension("worker.manager.task.timeout.ms", Long.class);
        if (customTaskTimeout != null) {
            workerConfig.setTaskTimeoutMs(customTaskTimeout);
        }

        return workerConfig;
    }

    /**
     * 设置Worker事件监听器
     */
    private void setupWorkerEventListeners(JQuickWorkerManager workerManager, JQuickExecutionContext context) {
    }
    /**
     * 初始化RPC客户端
     */
    private void initializeRpcClient(JQuickWorkerManager workerManager, JQuickConfiguration config) {
        // 创建RPC客户端配置
        WorkerRpcClient rpcClient = new WorkerRpcClient();

        // 配置网络参数
        rpcClient.setConnectTimeoutMs(config.getNetworkTimeoutMs());
        rpcClient.setReadTimeoutMs(config.getNetworkTimeoutMs());
        rpcClient.setRetryCount(config.getNetworkRetryCount());
        rpcClient.setRetryDelayMs(config.getNetworkRetryDelayMs());
        rpcClient.setMaxPacketSize(config.getMaxPacketSize());
        rpcClient.setEnableCompression(config.isEnableCompression());
        rpcClient.setEnableEncryption(config.isEnableEncryption());

        // 设置认证信息（如果需要）
        String authToken = config.getProperty("jquick.auth.token");
        if (authToken != null && !authToken.isEmpty()) {
            rpcClient.setAuthToken(authToken);
        }

        // 设置自定义头信息
        Map<String, String> customHeaders = new HashMap<>();
        customHeaders.put("X-JQuick-Cluster", config.getClusterName());
        customHeaders.put("X-JQuick-Version", getVersion());
        rpcClient.setCustomHeaders(customHeaders);

        // 添加到Worker管理器
        workerManager.setRpcClient(rpcClient);

        LoggerFactory.getLogger(JQuickWorkerManager.class)
                .info("RPC client initialized with timeout={}ms, retries={}",
                        config.getNetworkTimeoutMs(), config.getNetworkRetryCount());
    }

}
