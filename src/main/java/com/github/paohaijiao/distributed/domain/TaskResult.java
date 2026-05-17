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

import com.github.paohaijiao.statement.JQuickDataSet;

import java.io.Serializable;
import java.util.*;

/**
 * 任务执行结果
 */
public class TaskResult implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String taskId;
    private final int stageId;
    private final String workerId;
    private final boolean success;
    private final Object data;
    private final Throwable error;
    private final String errorMessage;
    private final long startTime;
    private final long endTime;
    private final long durationMs;
    private final Map<String, Object> metrics;
    private final List<String> outputPartitions;

    private TaskResult(Builder builder) {
        this.taskId = builder.taskId;
        this.stageId = builder.stageId;
        this.workerId = builder.workerId;
        this.success = builder.success;
        this.data = builder.data;
        this.error = builder.error;
        this.errorMessage = builder.errorMessage;
        this.startTime = builder.startTime;
        this.endTime = builder.endTime;
        this.durationMs = builder.endTime - builder.startTime;
        this.metrics = builder.metrics != null ? new HashMap<>(builder.metrics) : new HashMap<>();
        this.outputPartitions = builder.outputPartitions != null ? new ArrayList<>(builder.outputPartitions) : new ArrayList<>();
    }

    /**
     * 创建成功结果
     */
    public static TaskResult success(String taskId, String workerId, Object data) {
        return success(taskId, 0, workerId, data, null);
    }

    /**
     * 创建成功结果（带Stage ID）
     */
    public static TaskResult success(String taskId, int stageId, String workerId, Object data) {
        return success(taskId, stageId, workerId, data, null);
    }

    /**
     * 创建成功结果（带输出分区）
     */
    public static TaskResult success(String taskId, int stageId, String workerId,
                                     Object data, List<String> outputPartitions) {
        long now = System.currentTimeMillis();
        return new Builder()
                .taskId(taskId)
                .stageId(stageId)
                .workerId(workerId)
                .success(true)
                .data(data)
                .startTime(now)
                .endTime(now)
                .outputPartitions(outputPartitions)
                .build();
    }

    /**
     * 创建带执行时间的成功结果
     */
    public static TaskResult successWithTiming(String taskId, int stageId, String workerId,
                                               Object data, long startTime, long endTime) {
        return new Builder()
                .taskId(taskId)
                .stageId(stageId)
                .workerId(workerId)
                .success(true)
                .data(data)
                .startTime(startTime)
                .endTime(endTime)
                .build();
    }

    /**
     * 创建失败结果
     */
    public static TaskResult failure(String taskId, String workerId, Throwable error) {
        return failure(taskId, 0, workerId, error);
    }

    /**
     * 创建失败结果（带Stage ID）
     */
    public static TaskResult failure(String taskId, int stageId, String workerId, Throwable error) {
        long now = System.currentTimeMillis();
        return new Builder()
                .taskId(taskId)
                .stageId(stageId)
                .workerId(workerId)
                .success(false)
                .error(error)
                .errorMessage(error != null ? error.getMessage() : "Unknown error")
                .startTime(now)
                .endTime(now)
                .build();
    }

    /**
     * 创建失败结果（带错误消息）
     */
    public static TaskResult failure(String taskId, int stageId, String workerId, String errorMessage) {
        long now = System.currentTimeMillis();
        return new Builder()
                .taskId(taskId)
                .stageId(stageId)
                .workerId(workerId)
                .success(false)
                .errorMessage(errorMessage)
                .startTime(now)
                .endTime(now)
                .build();
    }

    // ========== Getter方法 ==========

    public String getTaskId() { return taskId; }
    public int getStageId() { return stageId; }
    public String getWorkerId() { return workerId; }
    public boolean isSuccess() { return success; }
    public Object getData() { return data; }
    public Throwable getError() { return error; }
    public String getErrorMessage() { return errorMessage; }
    public long getStartTime() { return startTime; }
    public long getEndTime() { return endTime; }
    public long getDurationMs() { return durationMs; }
    public Map<String, Object> getMetrics() { return Collections.unmodifiableMap(metrics); }
    public List<String> getOutputPartitions() { return Collections.unmodifiableList(outputPartitions); }

    /**
     * 获取结果数据并转换为JQuickDataSet
     */
    public JQuickDataSet getDataSet() {
        if (data instanceof JQuickDataSet) {
            return (JQuickDataSet) data;
        }
        return null;
    }

    /**
     * 获取结果数据并转换为List
     */
    @SuppressWarnings("unchecked")
    public List<List<Object>> getRows() {
        if (data instanceof JQuickDataSet) {
            return ((JQuickDataSet) data).getRows();
        } else if (data instanceof List) {
            return (List<List<Object>>) data;
        }
        return null;
    }

    /**
     * 获取指标值
     */
    @SuppressWarnings("unchecked")
    public <T> T getMetric(String key, Class<T> type) {
        Object value = metrics.get(key);
        if (value != null && type.isAssignableFrom(value.getClass())) {
            return (T) value;
        }
        return null;
    }

    /**
     * 添加指标
     */
    public TaskResult withMetric(String key, Object value) {
        metrics.put(key, value);
        return this;
    }

    // ========== Builder ==========

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String taskId;
        private int stageId;
        private String workerId;
        private boolean success;
        private Object data;
        private Throwable error;
        private String errorMessage;
        private long startTime;
        private long endTime;
        private Map<String, Object> metrics;
        private List<String> outputPartitions;

        public Builder taskId(String taskId) {
            this.taskId = taskId;
            return this;
        }

        public Builder stageId(int stageId) {
            this.stageId = stageId;
            return this;
        }

        public Builder workerId(String workerId) {
            this.workerId = workerId;
            return this;
        }

        public Builder success(boolean success) {
            this.success = success;
            return this;
        }

        public Builder data(Object data) {
            this.data = data;
            return this;
        }

        public Builder error(Throwable error) {
            this.error = error;
            if (error != null) {
                this.errorMessage = error.getMessage();
            }
            return this;
        }

        public Builder errorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

        public Builder startTime(long startTime) {
            this.startTime = startTime;
            return this;
        }

        public Builder endTime(long endTime) {
            this.endTime = endTime;
            return this;
        }

        public Builder metrics(Map<String, Object> metrics) {
            this.metrics = metrics;
            return this;
        }

        public Builder addMetric(String key, Object value) {
            if (this.metrics == null) {
                this.metrics = new HashMap<>();
            }
            this.metrics.put(key, value);
            return this;
        }

        public Builder outputPartitions(List<String> outputPartitions) {
            this.outputPartitions = outputPartitions;
            return this;
        }

        public Builder addOutputPartition(String partition) {
            if (this.outputPartitions == null) {
                this.outputPartitions = new ArrayList<>();
            }
            this.outputPartitions.add(partition);
            return this;
        }

        public TaskResult build() {
            return new TaskResult(this);
        }
    }

    // ========== 辅助方法 ==========

    @Override
    public String toString() {
        return String.format("TaskResult{taskId='%s', stageId=%d, workerId='%s', success=%s, duration=%dms, data=%s}",
                taskId, stageId, workerId, success, durationMs,
                data != null ? data.getClass().getSimpleName() : "null");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskResult that = (TaskResult) o;
        return Objects.equals(taskId, that.taskId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskId);
    }
}
