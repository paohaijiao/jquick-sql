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
import com.github.paohaijiao.cleanup.JQuickCleanup;
import com.github.paohaijiao.collector.JQuickResultCollector;
import com.github.paohaijiao.config.JQuickConfiguration;
import com.github.paohaijiao.console.JConsole;
import com.github.paohaijiao.context.JQuickExecutionContext;
import com.github.paohaijiao.distributed.JQuickDistributedPlan;
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
import com.github.paohaijiao.scheduler.*;
import com.github.paohaijiao.statement.JQuickColumnMeta;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class JQuickSQLEngine {

    private static JConsole console = JConsole.initConsoleEnvironment();

    private final JQuickLogicalPlanOptimizer optimizer;

    private final JQuickPhysicalPlanGenerator physicalGenerator;

    private final JQuickFragmenter fragmenter;

    private final WorkerManager workerManager;

    private final JQuickResultCollector resultCollector;

    private final JQuickCleanup cleanup;

    private Map<String, JQuickWorker> currentWorkers;

    private JQuickSchedulePlan currentSchedulePlan;

    public JQuickSQLEngine() {
        this.optimizer = new JQuickLogicalPlanOptimizer();
        this.cleanup = new JQuickCleanup();
        this.physicalGenerator = new JQuickPhysicalPlanGenerator();
        this.fragmenter = new JQuickFragmenter(4);
        this.workerManager = new WorkerManager();
        this.resultCollector = new JQuickResultCollector();
        this.currentWorkers = new ConcurrentHashMap<>();

    }

    public JQuickDataSet execute(String sql) {
        return execute(sql, new JQuickExecutionContext());
    }

    public JQuickDataSet execute(String sql, JQuickExecutionContext context) {
        try {
            System.out.println("=== SQL Execution Started ===");
            System.out.println("SQL: " + sql);
            // 1. 词法分析
            JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(sql));
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            // 2. 语法分析
            JQuickSQLParser parser = new JQuickSQLParser(tokens);
            JQuickSQLParser.QueryContext parseTree = parser.query();
            // 3. 构建AST
            JQuickQueryNode ast = buildAST(sql);
            // 4. AST → 逻辑计划
            JQuickASTToLogicalPlanVisitor visitor = new JQuickASTToLogicalPlanVisitor();
            JQuickLogicalPlanNode logicalPlan = visitor.visit(ast);
            // 5. 逻辑计划优化
            JQuickLogicalPlanNode optimizedPlan = optimizer.optimize(logicalPlan);
            // 6. 逻辑计划 → 物理计划
            JQuickPhysicalPlanNode physicalPlan = physicalGenerator.generate(optimizedPlan);
            // 7. 物理计划 → 分布式计划
            JQuickDistributedPlan distributedPlan = fragmenter.fragment(physicalPlan);
            fragmenter.printFragments(distributedPlan);
            // 8. 启动 Worker 发现服务
            workerManager.startDiscovery(9999);
            workerManager.registerWorker(new WorkerInfo("worker-1", "localhost", 8001, 9001, 4));
            workerManager.registerWorker(new WorkerInfo("worker-2", "localhost", 8002, 9002, 4));
            workerManager.registerWorker(new WorkerInfo("worker-3", "localhost", 8003, 9003, 4));
            // 9. 任务调度
            JQuickTaskScheduler scheduler = new JQuickTaskScheduler(distributedPlan, workerManager, JQuickTaskScheduler.SchedulingStrategy.DATA_LOCALITY);
            JQuickSchedulePlan currentSchedulePlan = scheduler.schedule();
            printSchedulePlan(currentSchedulePlan);
            // 10. 启动 Workers 并执行任务
            Map<String, JQuickWorker> workers = startWorkers(workerManager);
            // 11. 注册结果收集器
            registerResultCollector(currentSchedulePlan);
            // 12. 提交任务并收集结果
            JQuickDataSet result = executeAndCollect(currentSchedulePlan, workers);
            //清理资源
            cleanup(workers);
            System.out.println("=== SQL Execution Completed ===");
            System.out.println("Result rows: " + result.size());
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute SQL: " + sql, e);
        }
    }
    /**
     * 注册结果收集器到任务的输出通道
     */
    private void registerResultCollector(JQuickSchedulePlan schedulePlan) {
        for (JQuickTask task : schedulePlan.getAllTasks()) {
            if (task.getOutput() != null) {
                task.getOutput().setCollector(resultCollector);
            }
        }
    }
    /**
     * 执行并收集结果
     */
    private JQuickDataSet executeAndCollect(JQuickSchedulePlan schedulePlan, Map<String, JQuickWorker> workers) throws InterruptedException, ExecutionException {

        // 清空之前的收集结果
        resultCollector.clear();

        // 获取根任务（SINK 类型）
        List<JQuickTask> rootTasks = schedulePlan.getRootTasks();

        // 创建完成信号计数器
        CountDownLatch completionLatch = new CountDownLatch(rootTasks.size());

        // 为每个根任务设置结果收集器
        for (JQuickTask task : rootTasks) {
            resultCollector.registerTask(task.getTaskId(), completionLatch);

            // 设置任务的输出收集器
            if (task.getOutput() != null) {
                task.getOutput().setCollector(resultCollector);
            }
        }

        // 提交所有任务到 Workers
        for (JQuickTask task : schedulePlan.getAllTasks()) {
            JQuickWorker worker = workers.get(task.getAssignedWorker());
            if (worker != null) {
                worker.submitTask(task);
                System.out.println("Submitted task " + task.getTaskId() +
                        " to " + task.getAssignedWorker());
            }
        }

        // 等待所有根任务完成
        boolean completed = completionLatch.await(300, TimeUnit.SECONDS);
        if (!completed) {
            throw new RuntimeException("Query execution timeout");
        }

        // 收集所有结果行
        List<JQuickRow> allRows = resultCollector.getAllRows();

        // 获取输出 Schema
        List<JQuickColumnMeta> outputSchema = getOutputSchema(rootTasks);

        return new JQuickDataSet(outputSchema, allRows);
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
     * 清理所有资源
     */
    public JQuickCleanup.CleanupResult cleanup(Map<String, JQuickWorker> map) {
        System.out.println("=== Starting Cleanup ===");

        // 调用 JQuickCleanup 的 cleanup 方法，传入当前 Workers
        JQuickCleanup.CleanupResult result = cleanup.cleanup(currentWorkers,
                currentSchedulePlan != null ?
                        currentSchedulePlan.getAllTasks() : null,
                currentSchedulePlan);

        // 清空引用
        currentWorkers.clear();
        currentSchedulePlan = null;

        System.out.println("=== Cleanup Result ===");
        System.out.println(result);

        return result;
    }




    private  void printSchedulePlan(JQuickSchedulePlan plan) {
        System.out.println("=== Schedule Plan ===");
        System.out.println("Plan ID: " + plan.getPlanId());
        System.out.println("Total Tasks: " + plan.getAllTasks().size());
        System.out.println("Workers: " + plan.getWorkers().size());
        System.out.println();

        // 按 Worker 分组打印任务
        Map<String, List<JQuickTask>> tasksByWorker = new HashMap<>();
        for (JQuickTask task : plan.getAllTasks()) {
            tasksByWorker.computeIfAbsent(task.getAssignedWorker(), k -> new ArrayList<>())
                    .add(task);
        }

        for (Map.Entry<String, List<JQuickTask>> entry : tasksByWorker.entrySet()) {
            System.out.println("Worker " + entry.getKey() + ":");
            for (JQuickTask task : entry.getValue()) {
                System.out.println("  " + task);
            }
        }
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
    private static Map<String, JQuickWorker> startWorkers(WorkerManager workerManager) {
        Map<String, JQuickWorker> workers = new HashMap<>();
        for (WorkerInfo info : workerManager.getWorkers()) {
            JQuickWorker worker = new JQuickWorker(info.getWorkerId(), info.getHost(), info.getControlPort(), info.getDataPort(), info.getTotalSlots());
            worker.start();
            workers.put(info.getWorkerId(), worker);
        }
        return workers;
    }
    private static void submitTasksToWorkers(JQuickSchedulePlan plan, Map<String, JQuickWorker> workers) {
        for (JQuickTask task : plan.getAllTasks()) {
            JQuickWorker worker = workers.get(task.getAssignedWorker());
            if (worker != null) {
                worker.submitTask(task);
                System.out.println("Submitted " + task + " to " + task.getAssignedWorker());
            }
        }
    }
    private static void monitorExecution(JQuickSchedulePlan plan) {
        boolean allFinished = false;
        while (!allFinished) {
            allFinished = plan.getAllTasks().stream().allMatch(t -> t.getStatus() == JQuickTask.TaskStatus.FINISHED || t.getStatus() == JQuickTask.TaskStatus.FAILED);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                break;
            }
        }

        System.out.println("\n=== Execution Summary ===");
        for (JQuickTask task : plan.getAllTasks()) {
            System.out.printf("Task %d: %s (time: %d ms)%n", task.getTaskId(), task.getStatus(), task.getExecutionTime());
        }
    }
    /**
     * 从任务列表获取输出 Schema
     */
    public static List<JQuickColumnMeta> getOutputSchema(List<JQuickTask> rootTasks) {
        if (rootTasks == null || rootTasks.isEmpty()) {
            return Collections.emptyList();
        }
        JQuickTask sinkTask = rootTasks.stream()
                .filter(task -> task.getType() == JQuickTask.TaskType.SINK_TASK)
                .findFirst()
                .orElse(rootTasks.get(0));
        return getOutputSchemaFromTask(sinkTask);
    }
    /**
     * 从单个任务获取输出 Schema
     */
    public static List<JQuickColumnMeta> getOutputSchemaFromTask(JQuickTask task) {
        if (task == null || task.getFragment() == null) {
            return Collections.emptyList();
        }
        JQuickPhysicalPlanNode plan = task.getFragment().getPlan();
        return getOutputSchemaFromPlan(plan);
    }
    /**
     * 从物理计划节点获取输出 Schema
     */
    public static List<JQuickColumnMeta> getOutputSchemaFromPlan(JQuickPhysicalPlanNode plan) {
        if (plan == null) {
            return Collections.emptyList();
        }

        List<JQuickPhysicalColumn> physicalColumns = plan.getOutputSchema();
        if (physicalColumns == null || physicalColumns.isEmpty()) {
            return getDefaultSchema(plan);
        }
        return physicalColumns.stream()
                .map(col -> new JQuickColumnMeta(
                        col.getName(),
                        col.getType() != null ? col.getType() : Object.class,
                        col.getSourceTable() != null ? col.getSourceTable() : "unknown"
                ))
                .collect(Collectors.toList());
    }
    /**
     * 获取默认 Schema（当物理计划无法提供时）
     */
    private static List<JQuickColumnMeta> getDefaultSchema(JQuickPhysicalPlanNode plan) {
        String nodeType = plan.getNodeType();
        switch (nodeType) {
            case "TableScan":
                return getTableScanSchema(plan);
            case "Project":
                return getProjectSchema(plan);
            case "HashJoin":
                return getJoinSchema(plan);
            case "HashAggregate":
                return getAggregateSchema(plan);
            case "Limit":
                // Limit 继承子节点的 Schema
                if (plan.getChildren() != null && !plan.getChildren().isEmpty()) {
                    return getOutputSchemaFromPlan(plan.getChildren().get(0));
                }
                break;
            case "Sort":
                if (plan.getChildren() != null && !plan.getChildren().isEmpty()) {
                    return getOutputSchemaFromPlan(plan.getChildren().get(0));
                }
                break;
            case "Filter":
                if (plan.getChildren() != null && !plan.getChildren().isEmpty()) {
                    return getOutputSchemaFromPlan(plan.getChildren().get(0));
                }
                break;
        }
        return Collections.singletonList(new JQuickColumnMeta("result", Object.class, "unknown"));
    }
    /**
     * 从任务输出获取 Schema
     */
    public static List<JQuickColumnMeta> getOutputSchemaFromOutput(JQuickTaskOutput output) {
        if (output == null) {
            return Collections.emptyList();
        }
        // 从输出中获取 Schema 信息
        Map<String, Class<?>> schemaInfo = output.getSchemaInfo();
        if (schemaInfo == null || schemaInfo.isEmpty()) {
            return Collections.emptyList();
        }
        return schemaInfo.entrySet().stream()
                .map(entry -> new JQuickColumnMeta(
                        entry.getKey(),
                        entry.getValue(),
                        "output"
                ))
                .collect(Collectors.toList());
    }
    /**
     * 获取表扫描的 Schema
     */
    private static List<JQuickColumnMeta> getTableScanSchema(JQuickPhysicalPlanNode plan) {
        List<JQuickColumnMeta> schema = new ArrayList<>();
        if (schema.isEmpty()) {
            schema.add(new JQuickColumnMeta("id", Long.class, "table"));
            schema.add(new JQuickColumnMeta("name", String.class, "table"));
        }
        return schema;
    }
    /**
     * 获取投影的 Schema
     */
    private static List<JQuickColumnMeta> getProjectSchema(JQuickPhysicalPlanNode plan) {
        List<JQuickColumnMeta> schema = new ArrayList<>();

        return schema;
    }
    private static List<JQuickColumnMeta> getJoinSchema(JQuickPhysicalPlanNode plan) {
        List<JQuickColumnMeta> schema = new ArrayList<>();
        if (plan.getChildren() != null) {
            for (JQuickPhysicalPlanNode child : plan.getChildren()) {
                schema.addAll(getOutputSchemaFromPlan(child));
            }
        }

        return schema;
    }
    /**
     * 获取聚合的 Schema
     */
    private static List<JQuickColumnMeta> getAggregateSchema(JQuickPhysicalPlanNode plan) {
        List<JQuickColumnMeta> schema = new ArrayList<>();
        return schema;
    }
}
