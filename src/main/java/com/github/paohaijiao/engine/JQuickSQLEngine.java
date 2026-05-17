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
import com.github.paohaijiao.context.JQuickExecutionContext;
import com.github.paohaijiao.executor.JQuickSQLExecutor;
import com.github.paohaijiao.fragment.JQuickFragmenter;
import com.github.paohaijiao.logic.JQuickLogicalPlanNode;
import com.github.paohaijiao.logic.domain.JQuickSortNode;
import com.github.paohaijiao.logic.service.JQuickASTToLogicalPlanVisitor;
import com.github.paohaijiao.optimizer.JQuickLogicalPlanOptimizer;
import com.github.paohaijiao.parser.JQuickSQLLexer;
import com.github.paohaijiao.parser.JQuickSQLParser;
import com.github.paohaijiao.physical.JQuickPhysicalPlanGenerator;
import com.github.paohaijiao.physical.node.JQuickPhysicalPlanNode;
import com.github.paohaijiao.scheduler.JQuickScheduler;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.worker.JQuickJobExecution;
import com.github.paohaijiao.worker.JQuickWorkerManager;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.util.List;
import java.util.Map;

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
            JQuickPhysicalPlanNode physicalPlan = physicalGenerator.generate(optimizedPlan);

            // 7. 执行物理计划
            JQuickDataSet result = physicalPlan.execute(context);

            return finalizeResult(result, context);

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
     * 等待分布式执行结果
     */
    private JQuickDataSet waitForResult(JQuickJobExecution job) throws InterruptedException {
        while (job.getStatus() == JQuickJobExecution.JobStatus.RUNNING || job.getStatus() == JQuickJobExecution.JobStatus.PENDING) {
            Thread.sleep(100);
            // 超时检查
            if (job.getExecutionTime() > 300000) { // 5分钟超时
                job.cancel();
                throw new RuntimeException("Query execution timeout");
            }
        }

        if (job.getStatus() == JQuickJobExecution.JobStatus.FAILED) {
            throw new RuntimeException("Query execution failed", job.getError());
        }

        return job.getResult();
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
