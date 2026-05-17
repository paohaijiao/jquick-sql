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
package com.github.paohaijiao.scheduler;

import com.github.paohaijiao.distributed.JQuickDistributedPlan;
import com.github.paohaijiao.fragment.JQuickFragment;
import com.github.paohaijiao.worker.JQuickFragmentResult;
import com.github.paohaijiao.worker.JQuickJobExecution;
import com.github.paohaijiao.worker.JQuickTaskExecution;
import com.github.paohaijiao.worker.JQuickWorkerManager;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 分布式调度器 - 负责任务调度和资源管理
 */
public class JQuickScheduler {

    private final JQuickDistributedPlan plan;

    private final JQuickWorkerManager workerManager;

    private final ExecutorService schedulerExecutor;

    private final Map<Long, JQuickTaskExecution> runningTasks;

    private final AtomicLong taskIdGenerator;

    public JQuickScheduler(JQuickDistributedPlan plan, JQuickWorkerManager workerManager) {
        this.plan = plan;
        this.workerManager = workerManager;
        this.schedulerExecutor = Executors.newFixedThreadPool(4);
        this.runningTasks = new ConcurrentHashMap<>();
        this.taskIdGenerator = new AtomicLong(0);
    }

    /**
     * 提交分布式计划执行
     */
    public JQuickJobExecution submit() {
        JQuickJobExecution job = new JQuickJobExecution(plan);
        // 拓扑排序，确定执行顺序
        List<JQuickFragment> executionOrder = topologicalSort();

        // 提交任务
        schedulerExecutor.submit(() -> {
            try {
                executeFragments(executionOrder, job);
            } catch (Exception e) {
                job.setError(e);
            }
        });

        return job;
    }

    /**
     * 拓扑排序片段
     */
    private List<JQuickFragment> topologicalSort() {
        List<JQuickFragment> order = new ArrayList<>();
        Set<Long> visited = new HashSet<>();

        for (JQuickFragment fragment : plan.getFragments().values()) {
            if (!visited.contains(fragment.getFragmentId())) {
                dfs(fragment, visited, order);
            }
        }

        return order;
    }

    private void dfs(JQuickFragment fragment, Set<Long> visited, List<JQuickFragment> order) {
        visited.add(fragment.getFragmentId());
        for (JQuickFragment child : fragment.getChildren()) {
            if (!visited.contains(child.getFragmentId())) {
                dfs(child, visited, order);
            }
        }
        order.add(fragment);
    }

    /**
     * 执行片段
     */
    private void executeFragments(List<JQuickFragment> fragments, JQuickJobExecution job) {
        // 按拓扑顺序执行
        for (JQuickFragment fragment : fragments) {
            if (job.isCancelled()) {
                break;
            }

            // 分配资源
            List<String> assignedWorkers = workerManager.assignWorkers(
                    fragment.getParallelism());

            // 创建任务
            JQuickTaskExecution task = new JQuickTaskExecution(
                    taskIdGenerator.incrementAndGet(),
                    fragment,
                    assignedWorkers
            );

            runningTasks.put(task.getTaskId(), task);
            job.addTask(task);

            // 提交到工作节点
            List<Future<JQuickFragmentResult>> futures = new ArrayList<>();
            for (String worker : assignedWorkers) {
                Future<JQuickFragmentResult> future = workerManager.submitTask(worker, fragment);
                futures.add(future);
            }

            // 等待任务完成
            for (Future<JQuickFragmentResult> future : futures) {
                try {
                    JQuickFragmentResult result = future.get(30, TimeUnit.MINUTES);
                    task.addResult(result);
                } catch (Exception e) {
                    // 故障处理：重试或失败
                    handleFailure(task, e);
                }
            }

            task.markCompleted();
            runningTasks.remove(task.getTaskId());
        }

        job.markCompleted();
    }

    /**
     * 故障处理
     */
    private void handleFailure(JQuickTaskExecution task, Exception e) {
        // 简化实现：重试一次
        if (task.getRetryCount() < 3) {
            task.incrementRetry();
            // 重新调度
        } else {
            task.markFailed(e);
        }
    }

    /**
     * 取消作业
     */
    public void cancelJob(String jobId) {
        // 取消所有运行中的任务
        for (JQuickTaskExecution task : runningTasks.values()) {
            workerManager.cancelTask(task.getTaskId());
        }
    }
}
