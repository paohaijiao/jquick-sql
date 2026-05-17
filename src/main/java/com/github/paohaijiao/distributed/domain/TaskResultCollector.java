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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * 任务结果收集器 - 用于收集和合并多个任务的结果
 */
public class TaskResultCollector {

    private final Map<String, TaskResult> results;
    private final Map<Integer, List<TaskResult>> resultsByStage;
    private final AtomicLong totalRows;
    private final AtomicLong totalBytes;
    private final long startTime;

    public TaskResultCollector() {
        this.results = new ConcurrentHashMap<>();
        this.resultsByStage = new ConcurrentHashMap<>();
        this.totalRows = new AtomicLong(0);
        this.totalBytes = new AtomicLong(0);
        this.startTime = System.currentTimeMillis();
    }

    /**
     * 添加结果
     */
    public void addResult(TaskResult result) {
        results.put(result.getTaskId(), result);
        resultsByStage.computeIfAbsent(result.getStageId(), k -> new ArrayList<>())
                .add(result);

        if (result.isSuccess() && result.getData() != null) {
            if (result.getData() instanceof JQuickDataSet) {
                JQuickDataSet ds = (JQuickDataSet) result.getData();
                totalRows.addAndGet(ds.getRowCount());
                // 估算字节数
                totalBytes.addAndGet(estimateDataSize(ds));
            }
        }
    }

    /**
     * 获取指定Stage的所有结果
     */
    public List<TaskResult> getResultsForStage(int stageId) {
        return resultsByStage.getOrDefault(stageId, Collections.emptyList());
    }

    /**
     * 获取所有成功的结果
     */
    public List<TaskResult> getSuccessfulResults() {
        return results.values().stream()
                .filter(TaskResult::isSuccess)
                .collect(Collectors.toList());
    }

    /**
     * 获取所有失败的结果
     */
    public List<TaskResult> getFailedResults() {
        return results.values().stream()
                .filter(r -> !r.isSuccess())
                .collect(Collectors.toList());
    }

    /**
     * 合并所有结果为单个DataSet
     */
    public JQuickDataSet mergeResults() {
        List<JQuickDataSet> dataSets = new ArrayList<>();
        List<String> columnNames = null;

        for (TaskResult result : getSuccessfulResults()) {
            JQuickDataSet ds = result.getDataSet();
            if (ds != null) {
                dataSets.add(ds);
                if (columnNames == null) {
                    columnNames = ds.getColumnNames();
                }
            }
        }

        if (dataSets.isEmpty()) {
            return new JQuickDataSet(new ArrayList<>(), new ArrayList<>());
        }

        // 合并所有行
        List<List<Object>> allRows = new ArrayList<>();
        for (JQuickDataSet ds : dataSets) {
            allRows.addAll(ds.getRows());
        }

        return new JQuickDataSet(allRows, columnNames);
    }

    /**
     * 检查是否所有任务都完成了
     */
    public boolean isAllCompleted(int expectedTaskCount) {
        return results.size() >= expectedTaskCount;
    }

    /**
     * 检查是否有失败的任务
     */
    public boolean hasFailures() {
        return !getFailedResults().isEmpty();
    }

    /**
     * 获取失败消息
     */
    public List<String> getFailureMessages() {
        return getFailedResults().stream()
                .map(r -> "Task " + r.getTaskId() + " failed: " + r.getErrorMessage())
                .collect(Collectors.toList());
    }

    /**
     * 获取统计信息
     */
    public ResultStats getStats() {
        long completedCount = results.size();
        long successCount = getSuccessfulResults().size();
        long failedCount = getFailedResults().size();
        long durationMs = System.currentTimeMillis() - startTime;

        return new ResultStats(
                completedCount,
                successCount,
                failedCount,
                totalRows.get(),
                totalBytes.get(),
                durationMs
        );
    }

    /**
     * 估算数据大小
     */
    private long estimateDataSize(JQuickDataSet dataSet) {
        // 粗略估算：每行200字节
        return dataSet.getRowCount() * 200;
    }

    /**
     * 清空收集器
     */
    public void clear() {
        results.clear();
        resultsByStage.clear();
        totalRows.set(0);
        totalBytes.set(0);
    }

    /**
     * 结果统计
     */
    public static class ResultStats {
        private final long completedCount;
        private final long successCount;
        private final long failedCount;
        private final long totalRows;
        private final long totalBytes;
        private final long durationMs;

        public ResultStats(long completedCount, long successCount, long failedCount,
                           long totalRows, long totalBytes, long durationMs) {
            this.completedCount = completedCount;
            this.successCount = successCount;
            this.failedCount = failedCount;
            this.totalRows = totalRows;
            this.totalBytes = totalBytes;
            this.durationMs = durationMs;
        }

        public long getCompletedCount() { return completedCount; }
        public long getSuccessCount() { return successCount; }
        public long getFailedCount() { return failedCount; }
        public long getTotalRows() { return totalRows; }
        public long getTotalBytes() { return totalBytes; }
        public long getDurationMs() { return durationMs; }
        public double getSuccessRate() {
            long total = successCount + failedCount;
            return total > 0 ? (double) successCount / total : 1.0;
        }

        @Override
        public String toString() {
            return String.format("ResultStats{completed=%d, success=%d, failed=%d, rows=%d, bytes=%d, duration=%dms, rate=%.2f%%}",
                    completedCount, successCount, failedCount, totalRows, totalBytes, durationMs, getSuccessRate() * 100);
        }
    }
}
