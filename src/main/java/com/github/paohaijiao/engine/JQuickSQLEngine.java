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
import com.github.paohaijiao.scheduler.*;
import com.github.paohaijiao.statement.JQuickDataSet;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class JQuickSQLEngine {

    private static JConsole console = JConsole.initConsoleEnvironment();

    private final JQuickLogicalPlanOptimizer optimizer;

    private final JQuickPhysicalPlanGenerator physicalGenerator;

    private boolean distributedMode = false;

    private JQuickFragmenter fragmenter;

    private  WorkerManager workerManager ;
    JQuickASTToLogicalPlanVisitor visitor ;


    public JQuickSQLEngine() {
        this.optimizer = new JQuickLogicalPlanOptimizer();
        this.physicalGenerator = new JQuickPhysicalPlanGenerator();
        this.fragmenter = new JQuickFragmenter();
        workerManager = new WorkerManager();
        visitor= new JQuickASTToLogicalPlanVisitor();
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
            JQuickPhysicalPlanNode physicalPlan = physicalGenerator.generate(optimizedPlan);
            JQuickFragmenter fragmenter = new JQuickFragmenter(4);
            JQuickDistributedPlan distributedPlan = fragmenter.fragment(physicalPlan);
            fragmenter.printFragments(distributedPlan);
            workerManager.startDiscovery(9999);
            workerManager.registerWorker(new WorkerInfo("worker-1", "localhost", 8001, 9001, 4));
            workerManager.registerWorker(new WorkerInfo("worker-2", "localhost", 8002, 9002, 4));
            workerManager.registerWorker(new WorkerInfo("worker-3", "localhost", 8003, 9003, 4));
            JQuickTaskScheduler scheduler = new JQuickTaskScheduler(distributedPlan, workerManager, JQuickTaskScheduler.SchedulingStrategy.DATA_LOCALITY);
            JQuickSchedulePlan schedulePlan = scheduler.schedule();
            printSchedulePlan(schedulePlan);
            Map<String, JQuickWorker> workers = startWorkers(workerManager);
            submitTasksToWorkers(schedulePlan, workers);
            monitorExecution(schedulePlan);
            return null;
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

}
