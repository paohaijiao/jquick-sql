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
import com.github.paohaijiao.context.JQuickExecutionContext;
import com.github.paohaijiao.executor.JQuickSQLExecutor;
import com.github.paohaijiao.fragment.JQuickFragmenter;
import com.github.paohaijiao.logic.JQuickLogicalPlanNode;
import com.github.paohaijiao.logic.domain.JQuickSortNode;
import com.github.paohaijiao.logic2physical.JQuickPhysicalPlanGenerator;
import com.github.paohaijiao.optimizer.JQuickLogicalPlanOptimizer;
import com.github.paohaijiao.parser.JQuickSQLLexer;
import com.github.paohaijiao.parser.JQuickSQLParser;
import com.github.paohaijiao.physical.JQuickPhysicalPlanGenerator;
import com.github.paohaijiao.physical.JQuickPhysicalPlanNode;
import com.github.paohaijiao.scheduler.JQuickScheduler;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.worker.JQuickJobExecution;
import com.github.paohaijiao.worker.JQuickWorkerManager;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class JQuickSQLEngine {

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
        JQuickSQLExecutor executor=new JQuickSQLExecutor();
        JQuickQueryNode node = executor.execute(sql);
        return node;
    }

    /**
     * 分布式执行模式
     */
    private JQuickDataSet executeDistributed(JQuickPhysicalPlanNode physicalPlan, JQuickExecutionContext context) {
        // 1. 获取集群拓扑
        ClusterTopology cluster = context.getClusterTopology();
        if (cluster == null) {
            // 没有集群配置，回退到单机执行
            return executeLocal(physicalPlan, context);
        }

        // 2. 调度器生成执行计划
        JQuickScheduler scheduler = new JQuickScheduler(cluster);
        JQuickExecutionPlan executionPlan = scheduler.schedule(physicalPlan);

        // 3. 提交任务到各Worker
        List<CompletableFuture<TaskResult>> futures = new ArrayList<>();
        Map<String, JQuickWorker> workers = cluster.getWorkers();

        for (Map.Entry<String, JQuickWorker> entry : workers.entrySet()) {
            String workerId = entry.getKey();
            JQuickWorker worker = entry.getValue();
            List<JQuickTask> tasks = executionPlan.getTasksForWorker(workerId);

            for (JQuickTask task : tasks) {
                futures.add(worker.executeTask(task));
            }
        }

        // 4. 等待所有任务完成
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                futures.toArray(new CompletableFuture[0])
        );

        try {
            allFutures.get(executionPlan.getTimeout(), executionPlan.getTimeoutUnit());
        } catch (Exception e) {
            throw new JQuickSQLException("Distributed execution failed", e);
        }

        // 5. 收集并合并结果
        List<TaskResult> results = futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());

        return mergeResults(results, executionPlan);
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
        List<PhysicalColumn> schema = plan.getOutputSchema();
        List<String> columnNames = schema.stream()
                .map(PhysicalColumn::getName)
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
}
