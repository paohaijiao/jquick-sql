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

import com.github.paohaijiao.context.JQuickExecutionContext;
import com.github.paohaijiao.distributed.domain.JQuickTask;
import com.github.paohaijiao.distributed.domain.TaskResultCollector;
import com.github.paohaijiao.physical.JQuickPhysicalPlanNode;
import com.github.paohaijiao.scheduler.JQuickScheduler;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.worker.JQuickWorkerManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 作业执行管理器 - 管理整个SQL作业的执行生命周期
 */
public class JQuickJobExecution {

    private final String jobId;
    private final String queryId;
    private final String sql;
    private final JQuickPhysicalPlanNode physicalPlan;
    private final JQuickExecutionContext context;
    private final JQuickWorkerManager workerManager;

    private JobState state;
    private long startTime;
    private long endTime;
    private String errorMessage;
    private Throwable error;

    private final Map<Integer, StageExecution> stageExecutions;
    private final Map<String, TaskExecution> taskExecutions;
    private final TaskResultCollector resultCollector;
    private final CountDownLatch completionLatch;
    private final AtomicLong completedTasks;
    private final AtomicLong failedTasks;

    private final List<JobListener> listeners;
    private final ScheduledExecutorService timeoutScheduler;
    private ScheduledFuture<?> timeoutFuture;

    public enum JobState {
        PENDING,      // 等待执行
        SCHEDULED,    // 已调度
        RUNNING,      // 执行中
        COMPLETED,    // 成功完成
        FAILED,       // 失败
        CANCELLED,    // 已取消
        TIMED_OUT     // 超时
    }

    /**
     * 作业监听器
     */
    public interface JobListener {
        void onJobStart(JQuickJobExecution job);
        void onStageStart(JQuickJobExecution job, StageExecution stage);
        void onStageComplete(JQuickJobExecution job, StageExecution stage);
        void onTaskStart(JQuickJobExecution job, TaskExecution task);
        void onTaskComplete(JQuickJobExecution job, TaskExecution task);
        void onJobComplete(JQuickJobExecution job, JQuickDataSet result);
        void onJobFailed(JQuickJobExecution job, Throwable error);
        void onJobCancelled(JQuickJobExecution job);
    }

    /**
     * Stage执行信息
     */
    public static class StageExecution {
        private final int stageId;
        private StageState state;
        private long startTime;
        private long endTime;
        private final List<TaskExecution> tasks;
        private final int totalTasks;
        private final AtomicLong completedTasks;
        private final AtomicLong failedTasks;

        public enum StageState {
            PENDING, RUNNING, COMPLETED, FAILED
        }

        public StageExecution(int stageId, int totalTasks) {
            this.stageId = stageId;
            this.totalTasks = totalTasks;
            this.state = StageState.PENDING;
            this.tasks = new ArrayList<>();
            this.completedTasks = new AtomicLong(0);
            this.failedTasks = new AtomicLong(0);
        }

        public int getStageId() { return stageId; }
        public StageState getState() { return state; }
        public void setState(StageState state) { this.state = state; }
        public long getStartTime() { return startTime; }
        public void setStartTime(long startTime) { this.startTime = startTime; }
        public long getEndTime() { return endTime; }
        public void setEndTime(long endTime) { this.endTime = endTime; }
        public List<TaskExecution> getTasks() { return Collections.unmodifiableList(tasks); }
        public void addTask(TaskExecution task) { tasks.add(task); }
        public int getTotalTasks() { return totalTasks; }
        public long getCompletedTasks() { return completedTasks.get(); }
        public long getFailedTasks() { return failedTasks.get(); }
        public void incrementCompletedTasks() { completedTasks.incrementAndGet(); }
        public void incrementFailedTasks() { failedTasks.incrementAndGet(); }
        public long getDurationMs() {
            return endTime > 0 ? endTime - startTime : System.currentTimeMillis() - startTime;
        }
        public boolean isComplete() {
            return completedTasks.get() + failedTasks.get() >= totalTasks;
        }
    }

    /**
     * Task执行信息
     */
    public static class TaskExecution {
        private final JQuickTask task;
        private TaskState state;
        private long startTime;
        private long endTime;
        private String workerId;
        private int retryCount;
        private String errorMessage;

        public enum TaskState {
            PENDING, ASSIGNED, RUNNING, COMPLETED, FAILED, CANCELLED
        }

        public TaskExecution(JQuickTask task) {
            this.task = task;
            this.state = TaskState.PENDING;
            this.retryCount = 0;
        }

        public JQuickTask getTask() { return task; }
        public String getTaskId() { return task.getTaskId(); }
        public TaskState getState() { return state; }
        public void setState(TaskState state) { this.state = state; }
        public long getStartTime() { return startTime; }
        public void setStartTime(long startTime) { this.startTime = startTime; }
        public long getEndTime() { return endTime; }
        public void setEndTime(long endTime) { this.endTime = endTime; }
        public String getWorkerId() { return workerId; }
        public void setWorkerId(String workerId) { this.workerId = workerId; }
        public int getRetryCount() { return retryCount; }
        public void incrementRetryCount() { retryCount++; }
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
        public long getDurationMs() {
            return endTime > 0 ? endTime - startTime : System.currentTimeMillis() - startTime;
        }
    }

    // ========== 构造函数 ==========

    public JQuickJobExecution(String jobId, String queryId, String sql,
                              JQuickPhysicalPlanNode physicalPlan,
                              JQuickExecutionContext context,
                              JQuickWorkerManager workerManager) {
        this.jobId = jobId;
        this.queryId = queryId;
        this.sql = sql;
        this.physicalPlan = physicalPlan;
        this.context = context;
        this.workerManager = workerManager;

        this.state = JobState.PENDING;
        this.stageExecutions = new ConcurrentHashMap<>();
        this.taskExecutions = new ConcurrentHashMap<>();
        this.resultCollector = new TaskResultCollector();
        this.completionLatch = new CountDownLatch(1);
        this.completedTasks = new AtomicLong(0);
        this.failedTasks = new AtomicLong(0);
        this.listeners = new CopyOnWriteArrayList<>();
        this.timeoutScheduler = Executors.newSingleThreadScheduledExecutor();
    }

    // ========== 执行方法 ==========

    /**
     * 执行作业
     */
    public CompletableFuture<JQuickDataSet> execute() {
        return executeAsync().thenApply(this::getResult);
    }

    /**
     * 异步执行作业
     */
    public CompletableFuture<JQuickExecutionResult> executeAsync() {
        CompletableFuture<JQuickExecutionResult> future = new CompletableFuture<>();

        try {
            // 更新状态
            this.state = JobState.SCHEDULED;
            this.startTime = System.currentTimeMillis();

            // 通知监听器
            notifyJobStart();

            // 设置超时
            setupTimeout();

            // 开始执行
            executeInternal(future);

        } catch (Exception e) {
            failJob(e, future);
        }

        return future;
    }

    /**
     * 内部执行逻辑
     */
    private void executeInternal(CompletableFuture<JQuickExecutionResult> future) {
        // 获取执行计划
        JQuickScheduler scheduler = new JQuickScheduler(workerManager.getCluster());
        JQuickExecutionPlan executionPlan = scheduler.schedule(physicalPlan);

        // 更新状态
        this.state = JobState.RUNNING;

        // 按Stage顺序执行
        List<Integer> stageOrder = executionPlan.getStageExecutionOrder();

        // 递归执行Stage
        executeStages(stageOrder, 0, executionPlan, future);
    }

    /**
     * 递归执行Stage
     */
    private void executeStages(List<Integer> stageOrder, int index,
                               JQuickExecutionPlan executionPlan,
                               CompletableFuture<JQuickExecutionResult> future) {
        if (index >= stageOrder.size()) {
            // 所有Stage执行完成
            completeJob(future);
            return;
        }

        int stageId = stageOrder.get(index);
        StageExecution stageExecution = createStageExecution(stageId, executionPlan);

        // 执行Stage
        executeStage(stageExecution, executionPlan)
                .thenRun(() -> {
                    // 继续执行下一个Stage
                    executeStages(stageOrder, index + 1, executionPlan, future);
                })
                .exceptionally(throwable -> {
                    // Stage执行失败
                    failJob(throwable, future);
                    return null;
                });
    }

    /**
     * 执行单个Stage
     */
    private CompletableFuture<Void> executeStage(StageExecution stageExecution,
                                                 JQuickExecutionPlan executionPlan) {
        CompletableFuture<Void> stageFuture = new CompletableFuture<>();

        // 更新Stage状态
        stageExecution.setState(StageExecution.StageState.RUNNING);
        stageExecution.setStartTime(System.currentTimeMillis());
        notifyStageStart(stageExecution);

        List<JQuickTask> tasks = executionPlan.getTasksForStage(stageExecution.getStageId());
        List<CompletableFuture<TaskResult>> taskFutures = new ArrayList<>();

        for (JQuickTask task : tasks) {
            TaskExecution taskExecution = createTaskExecution(task);
            CompletableFuture<TaskResult> taskFuture = submitTask(taskExecution);
            taskFutures.add(taskFuture);
        }

        // 等待所有任务完成
        CompletableFuture.allOf(taskFutures.toArray(new CompletableFuture[0]))
                .thenRun(() -> {
                    // 收集Stage结果
                    for (CompletableFuture<TaskResult> future : taskFutures) {
                        try {
                            TaskResult result = future.get();
                            resultCollector.addResult(result);
                        } catch (Exception e) {
                            stageFuture.completeExceptionally(e);
                            return;
                        }
                    }

                    // 更新Stage状态
                    stageExecution.setState(StageExecution.StageState.COMPLETED);
                    stageExecution.setEndTime(System.currentTimeMillis());
                    notifyStageComplete(stageExecution);

                    stageFuture.complete(null);
                })
                .exceptionally(throwable -> {
                    stageExecution.setState(StageExecution.StageState.FAILED);
                    stageExecution.setEndTime(System.currentTimeMillis());
                    stageFuture.completeExceptionally(throwable);
                    return null;
                });

        return stageFuture;
    }

    /**
     * 提交任务执行
     */
    private CompletableFuture<TaskResult> submitTask(TaskExecution taskExecution) {
        CompletableFuture<TaskResult> future = new CompletableFuture<>();

        JQuickTask task = taskExecution.getTask();
        taskExecution.setState(TaskExecution.TaskState.ASSIGNED);
        taskExecution.setWorkerId(task.getWorkerId());

        // 提交到Worker管理器
        CompletableFuture<TaskResult> submitted = workerManager.submitTask(task);

        submitted.whenComplete((result, error) -> {
            if (error != null) {
                // 任务失败
                taskExecution.setState(TaskExecution.TaskState.FAILED);
                taskExecution.setEndTime(System.currentTimeMillis());
                taskExecution.setErrorMessage(error.getMessage());
                failedTasks.incrementAndGet();
                notifyTaskComplete(taskExecution);
                future.completeExceptionally(error);
            } else {
                // 任务成功
                taskExecution.setState(TaskExecution.TaskState.COMPLETED);
                taskExecution.setEndTime(System.currentTimeMillis());
                completedTasks.incrementAndGet();
                notifyTaskComplete(taskExecution);
                future.complete(result);
            }
        });

        taskExecution.setState(TaskExecution.TaskState.RUNNING);
        taskExecution.setStartTime(System.currentTimeMillis());
        notifyTaskStart(taskExecution);

        return future;
    }

    /**
     * 创建Stage执行信息
     */
    private StageExecution createStageExecution(int stageId, JQuickExecutionPlan executionPlan) {
        List<JQuickTask> tasks = executionPlan.getTasksForStage(stageId);
        StageExecution stage = new StageExecution(stageId, tasks.size());

        for (JQuickTask task : tasks) {
            TaskExecution taskExecution = new TaskExecution(task);
            stage.addTask(taskExecution);
            taskExecutions.put(task.getTaskId(), taskExecution);
        }

        stageExecutions.put(stageId, stage);
        return stage;
    }

    /**
     * 创建Task执行信息
     */
    private TaskExecution createTaskExecution(JQuickTask task) {
        TaskExecution execution = new TaskExecution(task);
        taskExecutions.put(task.getTaskId(), execution);
        return execution;
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
        this.state = JobState.TIMED_OUT;
        this.errorMessage = "Job execution timed out";
        cancel();
        completionLatch.countDown();

        for (JobListener listener : listeners) {
            listener.onJobFailed(this, new TimeoutException(errorMessage));
        }
    }

    /**
     * 完成作业
     */
    private void completeJob(CompletableFuture<JQuickExecutionResult> future) {
        this.state = JobState.COMPLETED;
        this.endTime = System.currentTimeMillis();

        // 取消超时
        if (timeoutFuture != null) {
            timeoutFuture.cancel(false);
        }

        JQuickDataSet result = resultCollector.mergeResults();
        JQuickExecutionResult executionResult = new JQuickExecutionResult(
                jobId, queryId, sql, state, result,
                startTime, endTime, getStatistics()
        );

        completionLatch.countDown();
        notifyJobComplete(result);
        future.complete(executionResult);
    }

    /**
     * 作业失败
     */
    private void failJob(Throwable error, CompletableFuture<JQuickExecutionResult> future) {
        this.state = JobState.FAILED;
        this.error = error;
        this.errorMessage = error.getMessage();
        this.endTime = System.currentTimeMillis();

        // 取消超时
        if (timeoutFuture != null) {
            timeoutFuture.cancel(false);
        }

        JQuickExecutionResult executionResult = new JQuickExecutionResult(
                jobId, queryId, sql, state, null,
                startTime, endTime, getStatistics()
        );

        completionLatch.countDown();
        notifyJobFailed(error);
        future.completeExceptionally(error);
    }

    /**
     * 取消作业
     */
    public void cancel() {
        if (isTerminal()) {
            return;
        }

        this.state = JobState.CANCELLED;
        this.endTime = System.currentTimeMillis();

        // 取消所有正在执行的任务
        for (TaskExecution taskExecution : taskExecutions.values()) {
            if (taskExecution.getState() == TaskExecution.TaskState.RUNNING ||
                    taskExecution.getState() == TaskExecution.TaskState.ASSIGNED) {
                workerManager.cancelTask(taskExecution.getTaskId());
                taskExecution.setState(TaskExecution.TaskState.CANCELLED);
            }
        }

        completionLatch.countDown();
        notifyJobCancelled();
    }

    // ========== 查询方法 ==========

    /**
     * 获取作业ID
     */
    public String getJobId() { return jobId; }

    /**
     * 获取查询ID
     */
    public String getQueryId() { return queryId; }

    /**
     * 获取SQL
     */
    public String getSql() { return sql; }

    /**
     * 获取作业状态
     */
    public JobState getState() { return state; }

    /**
     * 获取开始时间
     */
    public long getStartTime() { return startTime; }

    /**
     * 获取结束时间
     */
    public long getEndTime() { return endTime; }

    /**
     * 获取执行耗时
     */
    public long getDurationMs() {
        long end = endTime > 0 ? endTime : System.currentTimeMillis();
        return end - startTime;
    }

    /**
     * 获取错误信息
     */
    public String getErrorMessage() { return errorMessage; }

    /**
     * 获取错误
     */
    public Throwable getError() { return error; }

    /**
     * 检查是否已完成（成功、失败、取消、超时）
     */
    public boolean isTerminal() {
        return state == JobState.COMPLETED ||
                state == JobState.FAILED ||
                state == JobState.CANCELLED ||
                state == JobState.TIMED_OUT;
    }

    /**
     * 检查是否成功
     */
    public boolean isSuccess() {
        return state == JobState.COMPLETED;
    }

    /**
     * 等待完成
     */
    public void awaitCompletion() throws InterruptedException {
        completionLatch.await();
    }

    /**
     * 等待完成（带超时）
     */
    public boolean awaitCompletion(long timeout, TimeUnit unit) throws InterruptedException {
        return completionLatch.await(timeout, unit);
    }

    /**
     * 获取执行结果
     */
    public JQuickDataSet getResult() {
        return resultCollector.mergeResults();
    }

    /**
     * 获取Stage执行信息
     */
    public List<StageExecution> getStageExecutions() {
        return new ArrayList<>(stageExecutions.values());
    }

    /**
     * 获取Task执行信息
     */
    public List<TaskExecution> getTaskExecutions() {
        return new ArrayList<>(taskExecutions.values());
    }

    /**
     * 获取统计信息
     */
    public JobStatistics getStatistics() {
        return new JobStatistics(
                jobId, queryId, state,
                startTime, endTime,
                completedTasks.get(),
                failedTasks.get(),
                stageExecutions.size(),
                taskExecutions.size()
        );
    }

    // ========== 监听器管理 ==========

    /**
     * 添加监听器
     */
    public void addListener(JobListener listener) {
        listeners.add(listener);
    }

    /**
     * 移除监听器
     */
    public void removeListener(JobListener listener) {
        listeners.remove(listener);
    }

    private void notifyJobStart() {
        for (JobListener listener : listeners) {
            listener.onJobStart(this);
        }
    }

    private void notifyStageStart(StageExecution stage) {
        for (JobListener listener : listeners) {
            listener.onStageStart(this, stage);
        }
    }

    private void notifyStageComplete(StageExecution stage) {
        for (JobListener listener : listeners) {
            listener.onStageComplete(this, stage);
        }
    }

    private void notifyTaskStart(TaskExecution task) {
        for (JobListener listener : listeners) {
            listener.onTaskStart(this, task);
        }
    }

    private void notifyTaskComplete(TaskExecution task) {
        for (JobListener listener : listeners) {
            listener.onTaskComplete(this, task);
        }
    }

    private void notifyJobComplete(JQuickDataSet result) {
        for (JobListener listener : listeners) {
            listener.onJobComplete(this, result);
        }
    }

    private void notifyJobFailed(Throwable error) {
        for (JobListener listener : listeners) {
            listener.onJobFailed(this, error);
        }
    }

    private void notifyJobCancelled() {
        for (JobListener listener : listeners) {
            listener.onJobCancelled(this);
        }
    }

    // ========== 关闭资源 ==========

    /**
     * 关闭资源
     */
    public void close() {
        timeoutScheduler.shutdown();
        try {
            timeoutScheduler.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

