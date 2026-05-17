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
import com.github.paohaijiao.collector.JQuickResultCollector;
import com.github.paohaijiao.statement.JQuickColumnMeta;
import com.github.paohaijiao.statement.JQuickRow;
import com.github.paohaijiao.physical.domain.JQuickPhysicalColumn;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 任务输出 - 管理任务产出的数据
 */
public class JQuickTaskOutput {

    private final String outputId;

    private final OutputType type;

    private final List<JQuickExchangeChannel> channels;

    // Schema 信息
    private List<JQuickColumnMeta> schema;

    private final Map<String, Class<?>> schemaInfo;

    private final Map<String, String> schemaSource;

    // 结果收集器（用于 SINK 任务）

    private JQuickResultCollector resultCollector;

    private Long taskId;

    // 本地输出队列（同进程内数据传输）
    private BlockingQueue<JQuickRow> localOutputQueue;

    // 输出统计
    private long outputRowCount;

    private long outputBytes;

    private long startTime;

    private long endTime;

    // 输出状态
    private volatile boolean completed;

    private volatile boolean failed;

    private Throwable failureCause;

    public enum OutputType {
        LOCAL,      // 本地输出（同 Worker 内）
        SHUFFLE,    // Shuffle 输出（需要网络传输）
        BROADCAST,  // 广播输出（发送到所有下游）
        COLLECT,    // 收集输出（发送到 ResultCollector）
        MULTICAST   // 多播输出（发送到多个目标）
    }

    public JQuickTaskOutput(String outputId, OutputType type) {
        this.outputId = outputId;
        this.type = type;
        this.channels = new ArrayList<>();
        this.schemaInfo = new ConcurrentHashMap<>();
        this.schemaSource = new ConcurrentHashMap<>();
        this.outputRowCount = 0;
        this.outputBytes = 0;
        this.startTime = System.currentTimeMillis();
        this.completed = false;
        this.failed = false;

        if (type == OutputType.LOCAL) {
            this.localOutputQueue = new LinkedBlockingQueue<>();
        }
    }

    /**
     * 设置输出 Schema（从物理列转换）
     */
    public void setSchema(List<JQuickPhysicalColumn> physicalColumns) {
        if (physicalColumns == null) {
            return;
        }

        this.schema = new ArrayList<>();
        this.schemaInfo.clear();
        this.schemaSource.clear();

        for (JQuickPhysicalColumn col : physicalColumns) {
            JQuickColumnMeta meta = new JQuickColumnMeta(
                    col.getName(),
                    col.getType() != null ? col.getType() : Object.class,
                    col.getSourceTable() != null ? col.getSourceTable() : "unknown"
            );
            this.schema.add(meta);
            this.schemaInfo.put(col.getName(), col.getType() != null ? col.getType() : Object.class);
            this.schemaSource.put(col.getName(), col.getSourceTable());
        }
    }

    /**
     * 设置输出 Schema（从列元数据）
     */
    public void setSchemaFromMeta(List<JQuickColumnMeta> columnMetas) {
        if (columnMetas == null) {
            return;
        }

        this.schema = new ArrayList<>(columnMetas);
        this.schemaInfo.clear();
        this.schemaSource.clear();

        for (JQuickColumnMeta meta : columnMetas) {
            this.schemaInfo.put(meta.getName(), meta.getType());
            this.schemaSource.put(meta.getName(), meta.getSource());
        }
    }

    /**
     * 添加列到 Schema
     */
    public void addColumn(String name, Class<?> type) {
        addColumn(name, type, "unknown");
    }

    /**
     * 添加列到 Schema（带来源）
     */
    public void addColumn(String name, Class<?> type, String source) {
        schemaInfo.put(name, type);
        schemaSource.put(name, source);

        if (schema != null) {
            schema.add(new JQuickColumnMeta(name, type, source));
        }
    }

    /**
     * 添加多个列
     */
    public void addColumns(Map<String, Class<?>> columns) {
        for (Map.Entry<String, Class<?>> entry : columns.entrySet()) {
            addColumn(entry.getKey(), entry.getValue());
        }
    }

    /**
     * 获取 Schema 列表
     */
    public List<JQuickColumnMeta> getSchema() {
        if (schema == null && !schemaInfo.isEmpty()) {
            schema = new ArrayList<>();
            for (Map.Entry<String, Class<?>> entry : schemaInfo.entrySet()) {
                String source = schemaSource.getOrDefault(entry.getKey(), "unknown");
                schema.add(new JQuickColumnMeta(entry.getKey(), entry.getValue(), source));
            }
        }
        return schema != null ? Collections.unmodifiableList(schema) : Collections.emptyList();
    }

    /**
     * 获取 Schema 信息（列名 -> 类型）
     */
    public Map<String, Class<?>> getSchemaInfo() {
        return Collections.unmodifiableMap(schemaInfo);
    }

    /**
     * 获取 Schema 来源（列名 -> 源表）
     */
    public Map<String, String> getSchemaSource() {
        return Collections.unmodifiableMap(schemaSource);
    }

    /**
     * 获取列的类型
     */
    public Class<?> getColumnType(String columnName) {
        return schemaInfo.get(columnName);
    }

    /**
     * 检查是否包含某列
     */
    public boolean hasColumn(String columnName) {
        return schemaInfo.containsKey(columnName);
    }

    /**
     * 获取列数量
     */
    public int getColumnCount() {
        return schemaInfo.size();
    }

    /**
     * 添加输出通道
     */
    public void addChannel(JQuickExchangeChannel channel) {
        this.channels.add(channel);
    }

    /**
     * 添加多个输出通道
     */
    public void addChannels(List<JQuickExchangeChannel> channels) {
        this.channels.addAll(channels);
    }

    /**
     * 输出单行数据
     */
    public void emit(JQuickRow row) {
        if (completed || failed) {
            throw new IllegalStateException("Output already completed or failed");
        }

        outputRowCount++;
        outputBytes += estimateRowSize(row);

        // 输出时验证 Schema（可选）
        validateRowSchema(row);

        switch (type) {
            case COLLECT:
                if (resultCollector != null && taskId != null) {
                    resultCollector.receiveRow(taskId, row);
                }
                break;

            case LOCAL:
                if (localOutputQueue != null) {
                    try {
                        localOutputQueue.offer(row, 100, TimeUnit.MILLISECONDS);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Failed to emit row to local queue", e);
                    }
                }
                break;

            case SHUFFLE:
            case BROADCAST:
            case MULTICAST:
                for (JQuickExchangeChannel channel : channels) {
                    sendToChannel(channel, row);
                }
                break;
        }
    }

    /**
     * 输出批量数据
     */
    public void emitBatch(List<JQuickRow> rows) {
        if (completed || failed) {
            throw new IllegalStateException("Output already completed or failed");
        }

        outputRowCount += rows.size();

        for (JQuickRow row : rows) {
            outputBytes += estimateRowSize(row);
            validateRowSchema(row);
        }

        switch (type) {
            case COLLECT:
                if (resultCollector != null && taskId != null) {
                    resultCollector.receiveRows(taskId, rows);
                }
                break;

            case LOCAL:
                if (localOutputQueue != null) {
                    localOutputQueue.addAll(rows);
                }
                break;

            case SHUFFLE:
            case BROADCAST:
            case MULTICAST:
                for (JQuickRow row : rows) {
                    for (JQuickExchangeChannel channel : channels) {
                        sendToChannel(channel, row);
                    }
                }
                break;
        }
    }

    /**
     * 验证行数据是否符合 Schema
     */
    private void validateRowSchema(JQuickRow row) {
        if (schemaInfo.isEmpty()) {
            // 如果没有预定义 Schema，从数据中推断
            for (Map.Entry<String, Object> entry : row.entrySet()) {
                if (!schemaInfo.containsKey(entry.getKey())) {
                    Class<?> type = entry.getValue() != null ? entry.getValue().getClass() : Object.class;
                    schemaInfo.put(entry.getKey(), type);
                    schemaSource.put(entry.getKey(), "inferred");
                }
            }
        }
    }

    /**
     * 输出完成信号
     */
    public void complete() {
        if (completed) {
            return;
        }

        completed = true;
        endTime = System.currentTimeMillis();

        switch (type) {
            case COLLECT:
                if (resultCollector != null && taskId != null) {
                    resultCollector.markTaskComplete(taskId);
                }
                break;

            case LOCAL:
                if (localOutputQueue != null) {
                    localOutputQueue.offer(END_MARKER);
                }
                break;

            default:
                for (JQuickExchangeChannel channel : channels) {
                    sendCompleteSignal(channel);
                }
                break;
        }
    }

    /**
     * 输出失败信号
     */
    public void fail(Throwable cause) {
        this.failed = true;
        this.failureCause = cause;
        this.endTime = System.currentTimeMillis();

        switch (type) {
            case COLLECT:
                if (resultCollector != null && taskId != null) {
                    //resultCollector.markTaskFailed(taskId, cause);
                }
                break;

            default:
                for (JQuickExchangeChannel channel : channels) {
                    sendFailureSignal(channel, cause);
                }
                break;
        }
    }

    /**
     * 发送数据到通道
     */
    private void sendToChannel(JQuickExchangeChannel channel, JQuickRow row) {

        if (channel.isLocal()) {
            // 本地传输
        } else {
            // 远程传输，序列化并发送
        }
    }

    /**
     * 发送完成信号
     */
    private void sendCompleteSignal(JQuickExchangeChannel channel) {
        // 发送结束信号
    }

    /**
     * 发送失败信号
     */
    private void sendFailureSignal(JQuickExchangeChannel channel, Throwable cause) {
        // 发送失败信号
    }

    /**
     * 估算行大小
     */
    private long estimateRowSize(JQuickRow row) {
        long size = 0;
        for (Object value : row.values()) {
            if (value != null) {
                if (value instanceof String) {
                    size += ((String) value).getBytes().length;
                } else if (value instanceof byte[]) {
                    size += ((byte[]) value).length;
                } else {
                    size += value.toString().getBytes().length;
                }
            }
        }
        return size;
    }

    /**
     * 从本地输出队列接收数据（阻塞）
     */
    public JQuickRow receiveLocal() {
        if (localOutputQueue == null) {
            return null;
        }
        try {
            JQuickRow row = localOutputQueue.take();
            if (row == END_MARKER) {
                return null;
            }
            return row;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }

    /**
     * 从本地输出队列接收数据（非阻塞）
     */
    public JQuickRow receiveLocalNonBlocking() {
        if (localOutputQueue == null) {
            return null;
        }
        JQuickRow row = localOutputQueue.poll();
        if (row == END_MARKER) {
            return null;
        }
        return row;
    }

    /**
     * 从本地输出队列接收数据（带超时）
     */
    public JQuickRow receiveLocal(long timeout, TimeUnit unit) throws InterruptedException {
        if (localOutputQueue == null) {
            return null;
        }
        JQuickRow row = localOutputQueue.poll(timeout, unit);
        if (row == END_MARKER) {
            return null;
        }
        return row;
    }

    /**
     * 获取本地输出队列的迭代器
     */
    public Iterator<JQuickRow> localIterator() {
        return new LocalOutputIterator();
    }

    /**
     * 设置结果收集器
     */
    public void setCollector(JQuickResultCollector collector) {
        this.resultCollector = collector;
    }

    /**
     * 设置任务 ID
     */
    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    /**
     * 重置统计信息
     */
    public void resetStats() {
        this.outputRowCount = 0;
        this.outputBytes = 0;
        this.startTime = System.currentTimeMillis();
        this.endTime = 0;
    }

    // Getters
    public String getOutputId() { return outputId; }
    public OutputType getType() { return type; }
    public List<JQuickExchangeChannel> getChannels() { return Collections.unmodifiableList(channels); }
    public long getOutputRowCount() { return outputRowCount; }
    public long getOutputBytes() { return outputBytes; }
    public long getStartTime() { return startTime; }
    public long getEndTime() { return endTime; }
    public long getDuration() { return endTime > 0 ? endTime - startTime : System.currentTimeMillis() - startTime; }
    public boolean isCompleted() { return completed; }
    public boolean isFailed() { return failed; }
    public Throwable getFailureCause() { return failureCause; }

    /**
     * 获取输出摘要
     */
    public String getSummary() {
        return String.format("Output{id='%s', type=%s, rows=%d, bytes=%d, duration=%dms, columns=%d}",
                outputId, type, outputRowCount, outputBytes, getDuration(), getColumnCount());
    }

    // 结束标记
    private static final JQuickRow END_MARKER = new JQuickRow();

    /**
     * 本地输出迭代器
     */
    private class LocalOutputIterator implements Iterator<JQuickRow> {
        private JQuickRow next;
        private boolean finished = false;

        LocalOutputIterator() {
            advance();
        }

        private void advance() {
            if (finished) {
                next = null;
                return;
            }

            try {
                next = localOutputQueue.poll(100, TimeUnit.MILLISECONDS);
                if (next == END_MARKER) {
                    finished = true;
                    next = null;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                next = null;
            }
        }

        @Override
        public boolean hasNext() {
            return next != null;
        }

        @Override
        public JQuickRow next() {
            JQuickRow current = next;
            advance();
            return current;
        }
    }

    @Override
    public String toString() {
        return getSummary();
    }
}