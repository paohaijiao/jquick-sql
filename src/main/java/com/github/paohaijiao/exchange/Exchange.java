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
package com.github.paohaijiao.exchange;
import com.github.paohaijiao.statement.JQuickColumnMeta;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * 数据交换层 - 处理Fragment之间的数据传输
 */
public class Exchange {

    private final ExchangeNode config;
    private final Map<Integer, BlockingQueue<DataBatch>> partitions;
    private final ExecutorService networkExecutor;

    public Exchange(ExchangeNode config) {
        this.config = config;
        this.partitions = new ConcurrentHashMap<>();
        this.networkExecutor = Executors.newCachedThreadPool();

        for (int i = 0; i < config.getParallelism(); i++) {
            partitions.put(i, new LinkedBlockingQueue<>());
        }
    }

    /**
     * 发送数据
     */
    public CompletableFuture<Void> send(JQuickDataSet data) {
        return CompletableFuture.supplyAsync(() -> {
            for (JQuickRow row : data.getRows()) {
                int partition = getPartition(row);
                try {
                    partitions.get(partition).put(new DataBatch(row));
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(e);
                }
            }
            return null;
        }, networkExecutor);
    }

    /**
     * 接收数据（阻塞）
     */
    public JQuickDataSet receive() {
        List<JQuickRow> allRows = new ArrayList<>();

        // 从所有分区收集数据
        for (int i = 0; i < config.getParallelism(); i++) {
            try {
                BlockingQueue<DataBatch> queue = partitions.get(i);
                DataBatch batch = queue.poll(30, TimeUnit.SECONDS);
                if (batch != null) {
                    allRows.add(batch.row);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        // 构建数据集
        if (allRows.isEmpty()) {
            return new JQuickDataSet(new ArrayList<>(), new ArrayList<>());
        }

        List<String> columns = new ArrayList<>(allRows.get(0).keySet());
        List<JQuickColumnMeta> columnMetas = columns.stream()
                .map(col -> new JQuickColumnMeta(col, Object.class, "exchange"))
                .collect(Collectors.toList());

        return new JQuickDataSet(columnMetas, allRows);
    }

    /**
     * 计算分区号
     */
    private int getPartition(JQuickRow row) {
        switch (config.getPartitionStrategy()) {
            case HASH:
                return getHashPartition(row);
            case ROUND_ROBIN:
                return getRoundRobinPartition();
            case REPLICATE:
                return 0;  // 广播到所有分区
            default:
                return 0;
        }
    }

    private int getHashPartition(JQuickRow row) {
        if (config.getPartitionKey() == null) {
            return row.hashCode() % config.getParallelism();
        }
        Object key = config.getPartitionKey().evaluate(row);
        int hash = key != null ? key.hashCode() : 0;
        return Math.abs(hash) % config.getParallelism();
    }

    private int roundRobinCounter = 0;
    private synchronized int getRoundRobinPartition() {
        return (roundRobinCounter++) % config.getParallelism();
    }

    /**
     * 关闭交换层
     */
    public void close() {
        networkExecutor.shutdown();
        try {
            networkExecutor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static class DataBatch {
        final JQuickRow row;

        DataBatch(JQuickRow row) {
            this.row = row;
        }
    }
}
