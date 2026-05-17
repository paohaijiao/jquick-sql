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
package com.github.paohaijiao.worker;

import com.github.paohaijiao.distributed.JQuickDistributedPlan;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.stats.JQuickExecutionStats;
import com.github.paohaijiao.util.JQuickStringUtil;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 作业执行实例 - 管理分布式查询作业的生命周期
 */
public class JQuickJobExecution {

    private static final AtomicLong idGenerator = new AtomicLong(0);

    private final long jobId;
    private final JQuickDistributedPlan plan;
    private final List<JQuickTaskExecution> tasks;
    private JobStatus status;
    private Throwable error;
    private long startTime;
    private long endTime;

    // ========== 新增属性 ==========

    /** 最终执行结果 */
    private JQuickDataSet result;

    /** 作业名称（可选） */
    private String jobName;

    /** 提交用户 */
    private String submittedBy;

    /** 执行统计 */
    private final JQuickExecutionStats stats;

    /** 作业优先级（1-10，数字越大优先级越高） */
    private int priority = 5;

    /** 作业标签 */
    private final Set<String> tags = new HashSet<>();

    /** 作业进度（0-100） */
    private int progress = 0;

    /** 已完成任务数 */
    private int completedTasks = 0;

    /** 失败任务数 */
    private int failedTasks = 0;

    /** 取消回调 */
    private Runnable onCancelCallback;

    /** 完成回调 */
    private Runnable onCompleteCallback;

    /** 进度回调 */
    private java.util.function.Consumer<Integer> onProgressCallback;

    /** 扩展属性 */
    private final Map<String, Object> attributes = new HashMap<>();

    public enum JobStatus {
        PENDING,    // 等待调度
        RUNNING,    // 执行中
        COMPLETED,  // 成功完成
        FAILED,     // 执行失败
        CANCELLED,  // 已取消
        TIMEOUT     // 超时
    }

    // ========== 构造器 ==========

    public JQuickJobExecution(JQuickDistributedPlan plan) {
        this.jobId = idGenerator.incrementAndGet();
        this.plan = plan;
        this.tasks = new ArrayList<>();
        this.status = JobStatus.PENDING;
        this.startTime = System.currentTimeMillis();
        this.stats = new JQuickExecutionStats();
    }

    public JQuickJobExecution(JQuickDistributedPlan plan, String jobName) {
        this(plan);
        this.jobName = jobName;
    }

    // ========== 任务管理 ==========

    public void addTask(JQuickTaskExecution task) {
        tasks.add(task);
    }

    public void addTasks(List<JQuickTaskExecution> taskList) {
        tasks.addAll(taskList);
    }

    public List<JQuickTaskExecution> getTasks() {
        return Collections.unmodifiableList(tasks);
    }

    public JQuickTaskExecution getTask(long taskId) {
        return tasks.stream()
                .filter(t -> t.getTaskId() == taskId)
                .findFirst()
                .orElse(null);
    }

    // ========== 状态管理 ==========

    public void markRunning() {
        this.status = JobStatus.RUNNING;
        this.startTime = System.currentTimeMillis();
    }

    public void markCompleted() {
        this.status = JobStatus.COMPLETED;
        this.endTime = System.currentTimeMillis();
        if (onCompleteCallback != null) {
            onCompleteCallback.run();
        }
    }

    public void markCompleted(JQuickDataSet result) {
        this.result = result;
        this.status = JobStatus.COMPLETED;
        this.endTime = System.currentTimeMillis();
        if (onCompleteCallback != null) {
            onCompleteCallback.run();
        }
    }

    public void markFailed(Throwable e) {
        this.status = JobStatus.FAILED;
        this.error = e;
        this.endTime = System.currentTimeMillis();
    }

    public void markFailed(String errorMessage) {
        this.status = JobStatus.FAILED;
        this.error = new RuntimeException(errorMessage);
        this.endTime = System.currentTimeMillis();
    }

    public void markCancelled() {
        this.status = JobStatus.CANCELLED;
        this.endTime = System.currentTimeMillis();
        if (onCancelCallback != null) {
            onCancelCallback.run();
        }
    }

    public void markTimeout() {
        this.status = JobStatus.TIMEOUT;
        this.error = new RuntimeException("Job execution timeout");
        this.endTime = System.currentTimeMillis();
    }

    public void setError(Throwable e) {
        this.error = e;
        this.status = JobStatus.FAILED;
    }

    /**
     * 取消作业
     */
    public void cancel() {
        if (status == JobStatus.PENDING || status == JobStatus.RUNNING) {
            this.status = JobStatus.CANCELLED;
            this.endTime = System.currentTimeMillis();
            // 取消所有运行中的任务
            for (JQuickTaskExecution task : tasks) {
                if (task.getStatus() == JQuickTaskExecution.TaskStatus.RUNNING) {
                    task.cancel();
                }
            }

            if (onCancelCallback != null) {
                onCancelCallback.run();
            }
        }
    }

    /**
     * 检查是否已取消
     */
    public boolean isCancelled() {
        return status == JobStatus.CANCELLED;
    }

    /**
     * 检查是否已完成（成功或失败）
     */
    public boolean isDone() {
        return status == JobStatus.COMPLETED ||
                status == JobStatus.FAILED ||
                status == JobStatus.CANCELLED ||
                status == JobStatus.TIMEOUT;
    }

    /**
     * 检查是否成功
     */
    public boolean isSuccess() {
        return status == JobStatus.COMPLETED;
    }

    /**
     * 检查是否失败
     */
    public boolean isFailed() {
        return status == JobStatus.FAILED;
    }

    // ========== Getter/Setter ==========

    public long getJobId() { return jobId; }
    public JobStatus getStatus() { return status; }
    public long getExecutionTime() { return endTime - startTime; }
    public long getStartTime() { return startTime; }
    public long getEndTime() { return endTime; }
    public Throwable getError() { return error; }
    public String getErrorMessage() {
        return error != null ? error.getMessage() : null;
    }
    public JQuickDistributedPlan getPlan() { return plan; }

    /**
     * 获取执行结果
     */
    public JQuickDataSet getResult() {
        return result;
    }

    public void setResult(JQuickDataSet result) {
        this.result = result;
    }

    public String getJobName() { return jobName; }
    public void setJobName(String jobName) { this.jobName = jobName; }

    public String getSubmittedBy() { return submittedBy; }
    public void setSubmittedBy(String submittedBy) { this.submittedBy = submittedBy; }

    public JQuickExecutionStats getStats() { return stats; }

    public int getPriority() { return priority; }
    public void setPriority(int priority) {
        this.priority = Math.max(1, Math.min(10, priority));
    }

    public Set<String> getTags() { return Collections.unmodifiableSet(tags); }
    public void addTag(String tag) { tags.add(tag); }
    public void addTags(Collection<String> tagList) { tags.addAll(tagList); }
    public boolean hasTag(String tag) { return tags.contains(tag); }

    // ========== 进度管理 ==========

    public int getProgress() { return progress; }

    public int getCompletedTasks() { return completedTasks; }

    public int getFailedTasks() { return failedTasks; }

    /**
     * 更新作业进度
     */
    public void updateProgress() {
        if (tasks.isEmpty()) {
            this.progress = status == JobStatus.COMPLETED ? 100 : 0;
        } else {
            long completed = tasks.stream()
                    .filter(t -> t.getStatus() == JQuickTaskExecution.TaskStatus.COMPLETED)
                    .count();
            long failed = tasks.stream()
                    .filter(t -> t.getStatus() == JQuickTaskExecution.TaskStatus.FAILED)
                    .count();

            this.completedTasks = (int) completed;
            this.failedTasks = (int) failed;
            this.progress = (int) ((completed + failed) * 100 / tasks.size());
        }

        if (onProgressCallback != null) {
            onProgressCallback.accept(progress);
        }
    }

    // ========== 回调设置 ==========

    public void setOnCancelCallback(Runnable callback) {
        this.onCancelCallback = callback;
    }

    public void setOnCompleteCallback(Runnable callback) {
        this.onCompleteCallback = callback;
    }

    public void setOnProgressCallback(java.util.function.Consumer<Integer> callback) {
        this.onProgressCallback = callback;
    }

    // ========== 扩展属性 ==========

    public void setAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getAttribute(String key) {
        return (T) attributes.get(key);
    }

    public <T> T getAttribute(String key, T defaultValue) {
        T value = getAttribute(key);
        return value != null ? value : defaultValue;
    }

    public Map<String, Object> getAttributes() {
        return Collections.unmodifiableMap(attributes);
    }

    // ========== 等待方法 ==========

    /**
     * 等待作业完成
     */
    public JQuickJobExecution await() throws InterruptedException {
        return await(0, null);
    }

    /**
     * 等待作业完成（带超时）
     */
    public JQuickJobExecution await(long timeoutMillis) throws InterruptedException {
        return await(timeoutMillis, null);
    }

    /**
     * 等待作业完成（带超时和状态检查间隔）
     */
    public JQuickJobExecution await(long timeoutMillis, Long checkIntervalMillis) throws InterruptedException {
        long start = System.currentTimeMillis();
        long interval = checkIntervalMillis != null ? checkIntervalMillis : 100;

        while (!isDone()) {
            if (timeoutMillis > 0 && (System.currentTimeMillis() - start) > timeoutMillis) {
                markTimeout();
                break;
            }
            Thread.sleep(interval);
        }

        return this;
    }

    // ========== 统计更新 ==========

    public void recordTaskStart() {
        stats.startExecute();
    }

    public void recordTaskComplete(JQuickTaskExecution task) {
        stats.recordStep("task_" + task.getTaskId(), task.getExecutionTime());
        stats.addScannedRows(task.getScannedRows());
        stats.addReturnedRows(task.getReturnedRows());
        stats.addNetworkBytesTransferred(task.getNetworkBytesTransferred());
        updateProgress();
    }


    /**
     * 打印作业摘要
     */
    public void printSummary() {
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════════════════════╗");
        System.out.printf("║ Job Execution Summary: %s%n", centerString(40, jobName != null ? jobName : "Job #" + jobId));
        System.out.println("╠══════════════════════════════════════════════════════════════════╣");
        System.out.printf("║ Job ID:      %d%n", jobId);
        System.out.printf("║ Status:      %s%n", status);
        System.out.printf("║ Start Time:  %s%n", new Date(startTime));
        System.out.printf("║ End Time:    %s%n", new Date(endTime));
        System.out.printf("║ Duration:    %,d ms%n", getExecutionTime());
        System.out.printf("║ Progress:    %d%% (%d/%d tasks)%n", progress, completedTasks, tasks.size());

        if (error != null) {
            System.out.printf("║ Error:       %s%n", truncateString(error.getMessage(), 50));
        }

        System.out.println("╠══════════════════════════════════════════════════════════════════╣");
        System.out.println("║ Task Summary:                                                  ║");

        for (JQuickTaskExecution task : tasks) {
            System.out.printf("║   Task %d: %s (%,d rows, %,d ms)%n",
                    task.getTaskId(), task.getStatus(), task.getReturnedRows(), task.getExecutionTime());
        }

        System.out.println("╚══════════════════════════════════════════════════════════════════╝");
        System.out.println();
    }

    private String centerString(int width, String s) {
        if (s.length() >= width) return s.substring(0, width);
        int left = (width - s.length()) / 2;
        int right = width - s.length() - left;
        return JQuickStringUtil.repeat(left) + s + JQuickStringUtil.repeat(right);
    }

    private String truncateString(String s, int maxLength) {
        if (s == null) return "null";
        if (s.length() <= maxLength) return s;
        return s.substring(0, maxLength - 3) + "...";
    }

    public static Builder builder(JQuickDistributedPlan plan) {
        return new Builder(plan);
    }

    public static class Builder {
        private final JQuickJobExecution execution;

        public Builder(JQuickDistributedPlan plan) {
            this.execution = new JQuickJobExecution(plan);
        }

        public Builder jobName(String jobName) {
            execution.setJobName(jobName);
            return this;
        }

        public Builder submittedBy(String submittedBy) {
            execution.setSubmittedBy(submittedBy);
            return this;
        }

        public Builder priority(int priority) {
            execution.setPriority(priority);
            return this;
        }

        public Builder addTag(String tag) {
            execution.addTag(tag);
            return this;
        }

        public Builder addTags(Collection<String> tags) {
            execution.addTags(tags);
            return this;
        }

        public Builder attribute(String key, Object value) {
            execution.setAttribute(key, value);
            return this;
        }

        public Builder onCancel(Runnable callback) {
            execution.setOnCancelCallback(callback);
            return this;
        }

        public Builder onComplete(Runnable callback) {
            execution.setOnCompleteCallback(callback);
            return this;
        }

        public Builder onProgress(java.util.function.Consumer<Integer> callback) {
            execution.setOnProgressCallback(callback);
            return this;
        }

        public JQuickJobExecution build() {
            return execution;
        }
    }
}