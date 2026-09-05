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
package com.github.paohaijiao.collector;


import com.github.paohaijiao.statement.JQuickRow;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 结果收集器 - 收集分散在多个 Worker 上的执行结果
 */
public class JQuickResultCollector {

    private final Map<Long, BlockingQueue<JQuickRow>> taskResultQueues;

    private final List<JQuickRow> collectedRows;

    private final Set<Long> completedTasks;

    private final AtomicLong totalRowsReceived;

    public JQuickResultCollector() {
        this.taskResultQueues = new ConcurrentHashMap<>();
        this.collectedRows = Collections.synchronizedList(new ArrayList<>());
        this.completedTasks = ConcurrentHashMap.newKeySet();
        this.totalRowsReceived = new AtomicLong(0);
    }

    /**
     * 注册任务的结果队列
     */
    public void registerTask(long taskId, CountDownLatch completionLatch) {
        BlockingQueue<JQuickRow> queue = new LinkedBlockingQueue<>();
        taskResultQueues.put(taskId, queue);
        // 启动收集线程
        startCollectorThread(taskId, queue, completionLatch);
    }

    /**
     * 启动结果收集线程
     */
    private void startCollectorThread(long taskId, BlockingQueue<JQuickRow> queue, CountDownLatch completionLatch) {
        Thread collector = new Thread(() -> {
            try {
                while (!completedTasks.contains(taskId)) {
                    JQuickRow row = queue.poll(100, TimeUnit.MILLISECONDS);
                    if (row != null) {
                        collectedRows.add(row);
                        totalRowsReceived.incrementAndGet();
                    }
                }
                // 清空队列中剩余的数据
                List<JQuickRow> remaining = new ArrayList<>();
                queue.drainTo(remaining);
                collectedRows.addAll(remaining);
                totalRowsReceived.addAndGet(remaining.size());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                completionLatch.countDown();
            }
        });
        collector.setDaemon(true);
        collector.setName("ResultCollector-" + taskId);
        collector.start();
    }

    /**
     * 接收单行结果
     */
    public void receiveRow(long taskId, JQuickRow row) {
        BlockingQueue<JQuickRow> queue = taskResultQueues.get(taskId);
        if (queue != null) {
            queue.offer(row);
        }
    }

    /**
     * 接收批量结果
     */
    public void receiveRows(long taskId, List<JQuickRow> rows) {
        BlockingQueue<JQuickRow> queue = taskResultQueues.get(taskId);
        if (queue != null) {
            queue.addAll(rows);
        }
    }

    /**
     * 标记任务完成
     */
    public void markTaskComplete(long taskId) {
        completedTasks.add(taskId);
    }

    /**
     * 获取所有收集的行
     */
    public List<JQuickRow> getAllRows() {
        return new ArrayList<>(collectedRows);
    }

    /**
     * 获取行数
     */
    public long getRowCount() {
        return totalRowsReceived.get();
    }

    /**
     * 清空收集器
     */
    public void clear() {
        collectedRows.clear();
        completedTasks.clear();
        taskResultQueues.clear();
        totalRowsReceived.set(0);
    }
}
