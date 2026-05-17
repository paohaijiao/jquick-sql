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

import com.github.paohaijiao.fragment.JQuickFragment;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.util.JQuickStringUtil;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 任务执行实例 - 管理分布式任务的生命周期
 */
public class JQuickTaskExecution {

    private static final AtomicLong idGenerator = new AtomicLong(0);

    private final long taskId;

    private final JQuickFragment fragment;

    private final List<String> assignedWorkers;

    private final List<JQuickFragmentResult> results;

    private TaskStatus status;

    private int retryCount;

    private Throwable error;
    /**
     * 子任务列表（用于复合任务）
     */
    private final List<JQuickTaskExecution> subTasks = new ArrayList<>();
    /**
     * 扩展属性
     */
    private final Map<String, Object> attributes = new HashMap<>();
    /**
     * 执行结果
     */
    private JQuickDataSet result;
    /**
     * 开始时间
     */
    private long startTime;
    /**
     * 结束时间
     */
    private long endTime;
    /**
     * 扫描的行数
     */
    private long scannedRows = 0;
    /**
     * 返回的行数
     */
    private long returnedRows = 0;
    /**
     * 过滤的行数
     */
    private long filteredRows = 0;
    /**
     * 网络传输字节数
     */
    private long networkBytesTransferred = 0;
    /**
     * 磁盘读写字节数
     */
    private long diskBytesRead = 0;

    private long diskBytesWritten = 0;
    /**
     * 取消标志
     */
    private boolean cancelled = false;
    /**
     * 任务名称
     */
    private String taskName;
    /**
     * 任务开始执行的时间戳（用于计算执行时间）
     */
    private long executionStartTime;
    /**
     * 任务结束执行的时间戳
     */
    private long executionEndTime;

    public JQuickTaskExecution(long taskId, JQuickFragment fragment, List<String> assignedWorkers) {
        this.taskId = taskId;
        this.fragment = fragment;
        this.assignedWorkers = assignedWorkers != null ? new ArrayList<>(assignedWorkers) : new ArrayList<>();
        this.results = new ArrayList<>();
        this.status = TaskStatus.PENDING;
        this.retryCount = 0;
        this.startTime = System.currentTimeMillis();
    }


    public JQuickTaskExecution(JQuickFragment fragment, List<String> assignedWorkers) {
        this(idGenerator.incrementAndGet(), fragment, assignedWorkers);
    }

    public JQuickTaskExecution(JQuickFragment fragment, String workerHost) {
        this(fragment, Collections.singletonList(workerHost));
    }

    public static Builder builder(JQuickFragment fragment) {
        return new Builder(fragment);
    }

    /**
     * 标记任务开始执行
     */
    public void markRunning() {
        this.status = TaskStatus.RUNNING;
        this.executionStartTime = System.currentTimeMillis();
        this.startTime = executionStartTime;
    }

    /**
     * 标记任务完成
     */
    public void markCompleted() {
        this.status = TaskStatus.COMPLETED;
        this.executionEndTime = System.currentTimeMillis();
        this.endTime = executionEndTime;
    }

    /**
     * 标记任务完成并设置结果
     */
    public void markCompleted(JQuickDataSet result) {
        this.result = result;
        this.returnedRows = result != null ? result.size() : 0;
        this.status = TaskStatus.COMPLETED;
        this.executionEndTime = System.currentTimeMillis();
        this.endTime = executionEndTime;
    }

    /**
     * 标记任务失败
     */
    public void markFailed(Throwable e) {
        this.status = TaskStatus.FAILED;
        this.error = e;
        this.executionEndTime = System.currentTimeMillis();
        this.endTime = executionEndTime;
    }

    /**
     * 标记任务失败
     */
    public void markFailed(String errorMessage) {
        this.status = TaskStatus.FAILED;
        this.error = new RuntimeException(errorMessage);
        this.executionEndTime = System.currentTimeMillis();
        this.endTime = executionEndTime;
    }

    /**
     * 取消任务
     */
    public void cancel() {
        if (status == TaskStatus.PENDING || status == TaskStatus.RUNNING) {
            this.cancelled = true;
            this.status = TaskStatus.CANCELLED;
            this.executionEndTime = System.currentTimeMillis();
            this.endTime = executionEndTime;
            for (JQuickTaskExecution subTask : subTasks) {            // 取消所有子任务
                subTask.cancel();
            }
        }
    }

    /**
     * 增加重试次数
     */
    public void incrementRetry() {
        this.retryCount++;
    }

    /**
     * 重置任务（用于重试）
     */
    public void resetForRetry() {
        this.status = TaskStatus.PENDING;
        this.error = null;
        this.cancelled = false;
        this.results.clear();
        this.result = null;
        this.scannedRows = 0;
        this.returnedRows = 0;
        this.filteredRows = 0;
        this.networkBytesTransferred = 0;
        this.executionStartTime = 0;
        this.executionEndTime = 0;
    }


    public void addResult(JQuickFragmentResult result) {
        results.add(result);
        if (result.getData() != null) {// 聚合结果数据
            this.returnedRows += result.getData().size();
        }
        this.scannedRows += result.getScannedRows();
        this.networkBytesTransferred += result.getNetworkBytes();
    }

    public void addResults(List<JQuickFragmentResult> resultList) {
        for (JQuickFragmentResult result : resultList) {
            addResult(result);
        }
    }

    public JQuickFragmentResult getFirstResult() {
        return results.isEmpty() ? null : results.get(0);
    }

    public List<JQuickFragmentResult> getResults() {
        return Collections.unmodifiableList(results);
    }


    public void addSubTask(JQuickTaskExecution subTask) {
        this.subTasks.add(subTask);
    }

    public void addSubTasks(List<JQuickTaskExecution> subTaskList) {
        this.subTasks.addAll(subTaskList);
    }

    public List<JQuickTaskExecution> getSubTasks() {
        return Collections.unmodifiableList(subTasks);
    }

    public void addScannedRows(long count) {
        this.scannedRows += count;
    }

    public void addReturnedRows(long count) {
        this.returnedRows += count;
    }

    public void addFilteredRows(long count) {
        this.filteredRows += count;
    }

    public void addNetworkBytesTransferred(long bytes) {
        this.networkBytesTransferred += bytes;
    }

    public void addDiskBytesRead(long bytes) {
        this.diskBytesRead += bytes;
    }

    public void addDiskBytesWritten(long bytes) {
        this.diskBytesWritten += bytes;
    }


    public long getTaskId() {
        return taskId;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public Throwable getError() {
        return error;
    }

    public String getErrorMessage() {
        return error != null ? error.getMessage() : null;
    }

    public JQuickFragment getFragment() {
        return fragment;
    }

    public List<String> getAssignedWorkers() {
        return Collections.unmodifiableList(assignedWorkers);
    }

    public JQuickDataSet getResult() {
        return result;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    /**
     * 获取执行时间（毫秒）
     */
    public long getExecutionTime() {
        return executionEndTime - executionStartTime;
    }

    /**
     * 获取总时间（包括等待时间）
     */
    public long getTotalTime() {
        return endTime - startTime;
    }

    public long getScannedRows() {
        return scannedRows;
    }

    public long getReturnedRows() {
        return returnedRows;
    }

    public long getFilteredRows() {
        return filteredRows;
    }

    public long getNetworkBytesTransferred() {
        return networkBytesTransferred;
    }

    public long getDiskBytesRead() {
        return diskBytesRead;
    }

    public long getDiskBytesWritten() {
        return diskBytesWritten;
    }

    /**
     * 获取选择性（返回行数/扫描行数）
     */
    public double getSelectivity() {
        return scannedRows == 0 ? 0 : (double) returnedRows / scannedRows;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public boolean isDone() {
        return status == TaskStatus.COMPLETED ||
                status == TaskStatus.FAILED ||
                status == TaskStatus.CANCELLED;
    }

    public boolean isSuccess() {
        return status == TaskStatus.COMPLETED;
    }

    public boolean isFailed() {
        return status == TaskStatus.FAILED;
    }


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


    /**
     * 等待任务完成
     */
    public JQuickTaskExecution await() throws InterruptedException {
        return await(0);
    }

    /**
     * 等待任务完成（带超时）
     */
    public JQuickTaskExecution await(long timeoutMillis) throws InterruptedException {
        long start = System.currentTimeMillis();
        long interval = 100;

        while (!isDone()) {
            if (timeoutMillis > 0 && (System.currentTimeMillis() - start) > timeoutMillis) {
                cancel();
                break;
            }
            Thread.sleep(interval);
        }

        return this;
    }


    /**
     * 格式化字节大小
     */
    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.2f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.2f MB", bytes / (1024.0 * 1024));
        return String.format("%.2f GB", bytes / (1024.0 * 1024 * 1024));
    }

    /**
     * 格式化时间
     */
    private String formatTime(long millis) {
        if (millis < 1000) return millis + " ms";
        return String.format("%.2f s", millis / 1000.0);
    }

    /**
     * 打印任务摘要
     */
    public void printSummary() {
        System.out.println();
        System.out.println("┌─────────────────────────────────────────────────────────────────┐");
        System.out.printf("│ Task Execution Summary: %s%n", centerString(47, taskName != null ? taskName : "Task #" + taskId));
        System.out.println("├─────────────────────────────────────────────────────────────────┤");
        System.out.printf("│ Task ID:       %d%n", taskId);
        System.out.printf("│ Status:        %s%n", status);
        System.out.printf("│ Fragment:      %s%n", fragment != null ? fragment.getFragmentId() : "N/A");
        System.out.printf("│ Workers:       %s%n", assignedWorkers);
        System.out.printf("│ Start Time:    %s%n", new Date(startTime));
        System.out.printf("│ End Time:      %s%n", new Date(endTime));
        System.out.printf("│ Execution:     %s%n", formatTime(getExecutionTime()));
        System.out.printf("│ Total:         %s%n", formatTime(getTotalTime()));
        System.out.printf("│ Retry Count:   %d%n", retryCount);
        System.out.println("├─────────────────────────────────────────────────────────────────┤");
        System.out.println("│ Statistics:                                                      │");
        System.out.printf("│   Scanned:     %,d rows%n", scannedRows);
        System.out.printf("│   Returned:    %,d rows%n", returnedRows);
        System.out.printf("│   Filtered:    %,d rows%n", filteredRows);
        System.out.printf("│   Selectivity: %.2f%%%n", getSelectivity() * 100);
        System.out.printf("│   Network:     %s%n", formatBytes(networkBytesTransferred));
        System.out.printf("│   Disk Read:   %s%n", formatBytes(diskBytesRead));
        System.out.printf("│   Disk Write:  %s%n", formatBytes(diskBytesWritten));

        if (error != null) {
            System.out.println("├─────────────────────────────────────────────────────────────────┤");
            System.out.printf("│ Error:         %s%n", truncateString(error.getMessage(), 50));
        }

        if (!subTasks.isEmpty()) {
            System.out.println("├─────────────────────────────────────────────────────────────────┤");
            System.out.println("│ Sub-tasks:                                                      │");
            for (JQuickTaskExecution subTask : subTasks) {
                System.out.printf("│   - Task %d: %s (%,d rows, %s)%n",
                        subTask.getTaskId(), subTask.getStatus(),
                        subTask.getReturnedRows(), formatTime(subTask.getExecutionTime()));
            }
        }

        System.out.println("└─────────────────────────────────────────────────────────────────┘");
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


    public enum TaskStatus {
        PENDING,    // 等待执行
        RUNNING,    // 执行中
        COMPLETED,  // 成功完成
        FAILED,     // 执行失败
        CANCELLED   // 已取消
    }

    public static class Builder {
        private final JQuickTaskExecution execution;

        public Builder(JQuickFragment fragment) {
            this.execution = new JQuickTaskExecution(fragment, Collections.emptyList());
        }

        public Builder taskId(long taskId) {
            // taskId是final的，需要通过反射或修改设计
            return this;
        }

        public Builder taskName(String taskName) {
            execution.setTaskName(taskName);
            return this;
        }

        public Builder assignedWorker(String worker) {
            execution.assignedWorkers.add(worker);
            return this;
        }

        public Builder assignedWorkers(List<String> workers) {
            execution.assignedWorkers.addAll(workers);
            return this;
        }

        public Builder attribute(String key, Object value) {
            execution.setAttribute(key, value);
            return this;
        }

        public JQuickTaskExecution build() {
            return execution;
        }
    }
}