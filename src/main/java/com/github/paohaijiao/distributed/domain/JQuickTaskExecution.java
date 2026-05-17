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
package com.github.paohaijiao.distributed.domain;


import com.github.paohaijiao.context.JQuickExecutionContext;
import com.github.paohaijiao.physical.JQuickPhysicalPlanNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 任务执行管理器 - 管理单个任务的执行生命周期
 */
public class JQuickTaskExecution {

    private final JQuickTask task;
    private final JQuickExecutionContext context;
    private final JQuickPhysicalPlanExecutor executor;

    private TaskState state;
    private long startTime;
    private long endTime;
    private String workerId;
    private int attemptCount;
    private String errorMessage;
    private Throwable error;
    private TaskResult result;

    private final CountDownLatch completionLatch;
    private final List<TaskListener> listeners;
    private ScheduledFuture<?> timeoutFuture;
    private ScheduledExecutorService timeoutScheduler;

    public enum TaskState {
        PENDING,      // 等待执行
        INITIALIZING, // 初始化中
        RUNNING,      // 执行中
        COMPLETED,    // 成功完成
        FAILED,       // 失败
        CANCELLED,    // 已取消
        TIMED_OUT     // 超时
    }

    /**
     * 任务监听器
     */
    public interface TaskListener {
        void onTaskStart(JQuickTaskExecution execution);
        void onTaskProgress(JQuickTaskExecution execution, double progress, String message);
        void onTaskComplete(JQuickTaskExecution execution, TaskResult result);
        void onTaskFailed(JQuickTaskExecution execution, Throwable error);
        void onTaskCancelled(JQuickTaskExecution execution);
        void onTaskRetry(JQuickTaskExecution execution, int attempt);
    }

    /**
     * 任务指标
     */
    public static class TaskMetrics {
        private final AtomicLong processedRows = new AtomicLong(0);
        private final AtomicLong processedBytes = new AtomicLong(0);
        private final AtomicLong outputRows = new AtomicLong(0);
        private final AtomicLong outputBytes = new AtomicLong(0);
        private final Map<String, Long> customMetrics = new ConcurrentHashMap<>();

        public void incrementProcessedRows(long count) { processedRows.addAndGet(count); }
        public void incrementProcessedBytes(long bytes) { processedBytes.addAndGet(bytes); }
        public void incrementOutputRows(long count) { outputRows.addAndGet(count); }
        public void incrementOutputBytes(long bytes) { outputBytes.addAndGet(bytes); }
        public void setCustomMetric(String name, long value) { customMetrics.put(name, value); }
        public long getProcessedRows() { return processedRows.get(); }
        public long getProcessedBytes() { return processedBytes.get(); }
        public long getOutputRows() { return outputRows.get(); }
        public long getOutputBytes() { return outputBytes.get(); }
        public Map<String, Long> getCustomMetrics() { return new HashMap<>(customMetrics); }
    }

    private final TaskMetrics metrics;

    public JQuickTaskExecution(JQuickTask task, JQuickExecutionContext context) {
        this.task = task;
        this.context = context;
        this.executor = new JQuickPhysicalPlanExecutor();
        this.state = TaskState.PENDING;
        this.attemptCount = 0;
        this.completionLatch = new CountDownLatch(1);
        this.listeners = new CopyOnWriteArrayList<>();
        this.metrics = new TaskMetrics();
        this.timeoutScheduler = Executors.newSingleThreadScheduledExecutor();
    }


    /**
     * 执行任务
     */
    public CompletableFuture<TaskResult> execute() {
        return executeAsync().thenApply(this::getResult);
    }

    /**
     * 异步执行任务
     */
    public CompletableFuture<TaskResult> executeAsync() {
        CompletableFuture<TaskResult> future = new CompletableFuture<>();

        try {
            // 更新状态
            this.state = TaskState.INITIALIZING;
            this.startTime = System.currentTimeMillis();
            this.workerId = task.getWorkerId();
            this.attemptCount++;

            // 通知监听器
            notifyTaskStart();

            // 设置超时
            setupTimeout();

            // 执行任务
            executeInternal(future);

        } catch (Exception e) {
            failTask(e, future);
        }

        return future;
    }

    /**
     * 内部执行逻辑
     */
    private void executeInternal(CompletableFuture<TaskResult> future) {
        // 更新状态
        this.state = TaskState.RUNNING;

        // 提交到线程池执行
        CompletableFuture.supplyAsync(() -> {
            try {
                // 执行物理计划
                JQuickPhysicalPlanNode plan = task.getPhysicalPlan();
                Object data = executor.execute(plan, createExecutionContext());

                // 创建结果
                TaskResult result = TaskResult.success(
                        task.getTaskId(),
                        task.getStageId(),
                        workerId,
                        data
                );

                // 添加指标
                result.withMetric("processedRows", metrics.getProcessedRows())
                        .withMetric("processedBytes", metrics.getProcessedBytes())
                        .withMetric("outputRows", metrics.getOutputRows())
                        .withMetric("outputBytes", metrics.getOutputBytes())
                        .withMetric("attemptCount", attemptCount);

                for (Map.Entry<String, Long> entry : metrics.getCustomMetrics().entrySet()) {
                    result.withMetric(entry.getKey(), entry.getValue());
                }

                return result;

            } catch (Exception e) {
                throw new CompletionException(e);
            }
        }).whenComplete((result, error) -> {
            if (error != null) {
                Throwable cause = error instanceof CompletionException ? error.getCause() : error;
                failTask(cause, future);
            } else {
                completeTask(result, future);
            }
        });
    }

    /**
     * 完成任务
     */
    private void completeTask(TaskResult result, CompletableFuture<TaskResult> future) {
        this.state = TaskState.COMPLETED;
        this.endTime = System.currentTimeMillis();
        this.result = result;

        // 取消超时
        if (timeoutFuture != null) {
            timeoutFuture.cancel(false);
        }

        notifyTaskComplete(result);
        completionLatch.countDown();
        future.complete(result);
    }

    /**
     * 任务失败
     */
    private void failTask(Throwable error, CompletableFuture<TaskResult> future) {
        this.state = TaskState.FAILED;
        this.error = error;
        this.errorMessage = error.getMessage();
        this.endTime = System.currentTimeMillis();

        // 取消超时
        if (timeoutFuture != null) {
            timeoutFuture.cancel(false);
        }

        notifyTaskFailed(error);
        completionLatch.countDown();
        future.completeExceptionally(error);
    }

    /**
     * 设置超时
     */
    private void setupTimeout() {
        long timeoutMs = context.getConfiguration() != null ?
                context.getConfiguration().getTaskTimeoutMs() : 3600000;

        timeoutFuture = timeoutScheduler.schedule(() -> {
            if (!isTerminal()) {
                timeout();
            }
        }, timeoutMs, TimeUnit.MILLISECONDS);
    }

    /**
     * 超时处理
     */
    private void timeout() {
        this.state = TaskState.TIMED_OUT;
        this.errorMessage = "Task execution timed out";
        this.endTime = System.currentTimeMillis();

        notifyTaskFailed(new TimeoutException(errorMessage));
        completionLatch.countDown();
    }

    /**
     * 取消任务
     */
    public void cancel() {
        if (isTerminal()) {
            return;
        }

        this.state = TaskState.CANCELLED;
        this.endTime = System.currentTimeMillis();

        if (timeoutFuture != null) {
            timeoutFuture.cancel(false);
        }

        notifyTaskCancelled();
        completionLatch.countDown();
    }

    /**
     * 重试任务
     */
    public CompletableFuture<TaskResult> retry() {
        if (isTerminal() && state != TaskState.COMPLETED) {
            this.state = TaskState.PENDING;
            this.error = null;
            this.errorMessage = null;
            notifyTaskRetry(attemptCount + 1);
            return executeAsync();
        }
        return CompletableFuture.failedFuture(
                new IllegalStateException("Task cannot be retried in state: " + state));
    }

    /**
     * 更新进度
     */
    public void updateProgress(double progress, String message) {
        notifyTaskProgress(progress, message);
    }

    /**
     * 更新指标
     */
    public void updateMetrics(TaskMetricsUpdater updater) {
        updater.update(metrics);
    }

    /**
     * 创建执行上下文
     */
    private JQuickExecutionContext createExecutionContext() {
        JQuickExecutionContext taskContext = context.createChildContext();

//        // 设置任务特定信息
//        taskContext.setExtension("taskId", task.getTaskId());
//        taskContext.setExtension("stageId", task.getStageId());
//        taskContext.setExtension("attemptCount", attemptCount);
//        taskContext.setExtension("taskContext", task.getContext());
//
//        // 设置指标收集器
//        taskContext.setExtension("metricsCollector", new TaskMetricsCollector(this));

        return taskContext;
    }

    public JQuickTask getTask() { return task; }
    public String getTaskId() { return task.getTaskId(); }
    public int getStageId() { return task.getStageId(); }
    public TaskState getState() { return state; }
    public long getStartTime() { return startTime; }
    public long getEndTime() { return endTime; }
    public long getDurationMs() {
        long end = endTime > 0 ? endTime : System.currentTimeMillis();
        return end - startTime;
    }
    public String getWorkerId() { return workerId; }
    public int getAttemptCount() { return attemptCount; }
    public String getErrorMessage() { return errorMessage; }
    public Throwable getError() { return error; }
    public TaskResult getResult() { return result; }
    public TaskMetrics getMetrics() { return metrics; }

    public boolean isTerminal() {
        return state == TaskState.COMPLETED ||
                state == TaskState.FAILED ||
                state == TaskState.CANCELLED ||
                state == TaskState.TIMED_OUT;
    }

    public boolean isSuccess() { return state == TaskState.COMPLETED; }

    public void awaitCompletion() throws InterruptedException {
        completionLatch.await();
    }

    public boolean awaitCompletion(long timeout, TimeUnit unit) throws InterruptedException {
        return completionLatch.await(timeout, unit);
    }

    // ========== 监听器管理 ==========

    public void addListener(TaskListener listener) {
        listeners.add(listener);
    }

    public void removeListener(TaskListener listener) {
        listeners.remove(listener);
    }

    private void notifyTaskStart() {
        for (TaskListener listener : listeners) {
            listener.onTaskStart(this);
        }
    }

    private void notifyTaskProgress(double progress, String message) {
        for (TaskListener listener : listeners) {
            listener.onTaskProgress(this, progress, message);
        }
    }

    private void notifyTaskComplete(TaskResult result) {
        for (TaskListener listener : listeners) {
            listener.onTaskComplete(this, result);
        }
    }

    private void notifyTaskFailed(Throwable error) {
        for (TaskListener listener : listeners) {
            listener.onTaskFailed(this, error);
        }
    }

    private void notifyTaskCancelled() {
        for (TaskListener listener : listeners) {
            listener.onTaskCancelled(this);
        }
    }

    private void notifyTaskRetry(int attempt) {
        for (TaskListener listener : listeners) {
            listener.onTaskRetry(this, attempt);
        }
    }

    // ========== 内部类 ==========

    @FunctionalInterface
    public interface TaskMetricsUpdater {
        void update(TaskMetrics metrics);
    }

    private static class TaskMetricsCollector {
        private final JQuickTaskExecution execution;

        TaskMetricsCollector(JQuickTaskExecution execution) {
            this.execution = execution;
        }

        public void recordProcessedRows(long count) {
            execution.metrics.incrementProcessedRows(count);
        }

        public void recordProcessedBytes(long bytes) {
            execution.metrics.incrementProcessedBytes(bytes);
        }

        public void recordOutputRows(long count) {
            execution.metrics.incrementOutputRows(count);
        }

        public void recordOutputBytes(long bytes) {
            execution.metrics.incrementOutputBytes(bytes);
        }
    }

    // ========== 关闭资源 ==========

    public void close() {
        timeoutScheduler.shutdown();
        try {
            timeoutScheduler.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
