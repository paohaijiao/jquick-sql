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
package com.github.paohaijiao.distributed.worker;
import com.github.paohaijiao.console.JConsole;
import com.github.paohaijiao.distributed.coordinator.JQuickCoordinator.WorkerEndpoint;
import com.github.paohaijiao.enums.JQuickExchangeType;
import com.github.paohaijiao.enums.JQuickPartitionStrategy;
import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.physical.node.JQuickExchangePhysicalNode;
import com.github.paohaijiao.proto.JQuickDataChunkProto;
import com.github.paohaijiao.proto.JQuickDataDistributionServiceGrpc;
import com.github.paohaijiao.proto.JQuickEmptyNodeProto;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 分区管理服务 - 处理数据分区和分发
 */
public class JQuickPartitionManager {

    private JConsole console = JConsole.initConsoleEnvironment();

    // Worker 端点映射：workerId -> WorkerEndpoint
    private final Map<String, WorkerEndpoint> workerEndpoints;

    // gRPC 通道缓存
    private final Map<String, ManagedChannel> channelCache;

    // gRPC Stub 缓存
    private final Map<String, JQuickDataDistributionServiceGrpc.JQuickDataDistributionServiceStub> stubCache;

    public JQuickPartitionManager() {
        this.workerEndpoints = new ConcurrentHashMap<>();
        this.channelCache = new ConcurrentHashMap<>();
        this.stubCache = new ConcurrentHashMap<>();
    }

    /**
     * 设置 Worker 端点信息（从 Coordinator 传入）
     */
    public void setWorkerEndpoints(List<WorkerEndpoint> endpoints) {
        workerEndpoints.clear();
        for (WorkerEndpoint endpoint : endpoints) {
            workerEndpoints.put(endpoint.getWorkerId(), endpoint);
            console.info("Registered worker endpoint: " + endpoint.getWorkerId() + " -> " + endpoint.getHost() + ":" + endpoint.getPort());
        }
    }

    /**
     * 获取 Worker 端点
     */
    public WorkerEndpoint getWorkerEndpoint(String workerId) {
        return workerEndpoints.get(workerId);
    }

    /**
     * 获取 Worker 端点（通过索引）
     */
    public WorkerEndpoint getWorkerEndpointByIndex(int index) {
        for (WorkerEndpoint endpoint : workerEndpoints.values()) {
            if (endpoint.getIndex() == index) {
                return endpoint;
            }
        }
        return null;
    }

    /**
     * 获取所有 Worker 端点
     */
    public java.util.Collection<WorkerEndpoint> getWorkerEndpoints() {
        return workerEndpoints.values();
    }

    /**
     * 数据分区逻辑
     */
    public List<JQuickWorker.JQuickMemoryPartition> partitionData(JQuickDataSet data, JQuickExchangePhysicalNode node, JQuickExpressionEvaluator expressionEvaluator, int targetParallelism) {
        int numPartitions = targetParallelism;
        List<JQuickWorker.JQuickMemoryPartition> partitions = new ArrayList<>();
        JQuickPartitionStrategy strategy = node.getPartitionStrategy();
        for (int i = 0; i < numPartitions; i++) {
            partitions.add(new JQuickWorker.JQuickMemoryPartition(i, numPartitions));
        }
        if (data.isEmpty()) {
            return partitions;
        }
        switch (strategy) {
            case HASH:
                for (JQuickRow row : data.getRows()) {
                    int partition = computeHashPartition(row, node.getPartitionKeys(), numPartitions, expressionEvaluator);
                    partitions.get(partition).addRow(row);
                }
                break;
            case RANGE:
                for (JQuickRow row : data.getRows()) {
                    int partition = computeRangePartition(row, node.getPartitionKeys(), numPartitions, expressionEvaluator);
                    partitions.get(partition).addRow(row);
                }
                break;
            case ROUND_ROBIN:
                int idx = 0;
                for (JQuickRow row : data.getRows()) {
                    partitions.get(idx++ % numPartitions).addRow(row);
                }
                break;
            case BUCKET:
                for (JQuickRow row : data.getRows()) {
                    int bucketId = computeBucketPartition(row, node.getPartitionKeys(), numPartitions, expressionEvaluator);
                    partitions.get(bucketId).addRow(row);
                }
                break;
            case REPLICATE:
                for (JQuickRow row : data.getRows()) {
                    for (JQuickWorker.JQuickMemoryPartition partition : partitions) {
                        partition.addRow(row);
                    }
                }
                break;
            default:
                if (!partitions.isEmpty()) {
                    for (JQuickRow row : data.getRows()) {
                        partitions.get(0).addRow(row);
                    }
                }
                break;
        }

        return partitions;
    }

    /**
     * 发送数据到目标 Worker（使用 WorkerEndpoint）
     */
    public void sendToWorker(JQuickWorker.JQuickMemoryPartition partition, int targetParallelism, JQuickExchangeType exchangeType, JQuickWorker worker) {
        sendToWorker(partition, targetParallelism, exchangeType, worker, null);
    }

    /**
     * 发送数据到目标 Worker（带目标 Worker 索引）
     */
    public void sendToWorker(JQuickWorker.JQuickMemoryPartition partition, int targetParallelism, JQuickExchangeType exchangeType, JQuickWorker worker, Integer specificWorkerIndex) {
        console.info("=== sendToWorker Debug ===");
        console.info("Partition index: " + partition.getIndex());
        console.info("Partition data size: " + partition.getData().size());
        if (partition.getData().isEmpty()) {
            console.info("Partition data is empty, skipping send");
            return;
        }
        WorkerEndpoint targetEndpoint = null;
        if (specificWorkerIndex != null) {
            // 使用指定的 Worker 索引
            targetEndpoint = getWorkerEndpointByIndex(specificWorkerIndex);
            console.info("Using specific worker index: " + specificWorkerIndex);
        } else if (exchangeType == JQuickExchangeType.GATHER) {
            // GATHER：发送到 Worker 0（Coordinator 所在节点）
            targetEndpoint = getWorkerEndpointByIndex(0);
            console.info("Using GATHER, sending to worker 0");
        } else if (exchangeType == JQuickExchangeType.SHUFFLE) {
            // SHUFFLE：根据分区索引决定目标 Worker
            int targetWorkerId = partition.getIndex() % targetParallelism;
            targetEndpoint = getWorkerEndpointByIndex(targetWorkerId);
            console.info("Using SHUFFLE, target worker index: " + targetWorkerId);
        } else if (exchangeType == JQuickExchangeType.BROADCAST) {
            // BROADCAST：发送到所有 Worker
            for (WorkerEndpoint endpoint : workerEndpoints.values()) {
                sendToSingleWorker(partition, endpoint, worker);
            }
            return;
        } else {
            // 默认：发送到 Worker 0
            targetEndpoint = getWorkerEndpointByIndex(0);
        }

        if (targetEndpoint == null) {
            console.error("No target worker endpoint found for exchange type: " + exchangeType);
            return;
        }
        sendToSingleWorker(partition, targetEndpoint, worker);
    }

    /**
     * 发送到单个 Worker（使用 WorkerEndpoint）
     */
    private void sendToSingleWorker(JQuickWorker.JQuickMemoryPartition partition, WorkerEndpoint targetEndpoint, JQuickWorker worker) {
        JQuickDataChunkProto chunk = buildDataChunk(partition, worker);
        sendChunkAsync(chunk, targetEndpoint, worker);
    }

    /**
     * 发送到所有 Worker
     */
    private void sendToAllWorkers(JQuickWorker.JQuickMemoryPartition partition, JQuickWorker worker) {
        JQuickDataChunkProto chunk = buildDataChunk(partition, worker);
        for (WorkerEndpoint endpoint : workerEndpoints.values()) {
            sendChunkAsync(chunk, endpoint, worker);
        }
    }

    /**
     * 构建数据块
     */
    private JQuickDataChunkProto buildDataChunk(JQuickWorker.JQuickMemoryPartition partition, JQuickWorker worker) {
        return JQuickDataChunkProto.newBuilder()
                .setPartitionId(partition.getPartitionId())
                .setData(worker.getDataConverter().convertToProto(partition.getData()))
                .setChunkIndex(partition.getChunkIndex())
                .setIsLast(partition.isLast())
                .setSequenceId(System.currentTimeMillis())
                .setOriginalSize(partition.getDataSize())
                .build();
    }

    /**
     * 同步发送数据块（使用 WorkerEndpoint）
     * 修改为同步方式，确保数据发送完成后再返回
     */
    private void sendChunkAsync(JQuickDataChunkProto chunk, WorkerEndpoint targetEndpoint, JQuickWorker worker) {
        String endpointKey = targetEndpoint.getWorkerId();
        try {
            JQuickDataDistributionServiceGrpc.JQuickDataDistributionServiceStub stub = getDistributionStub(targetEndpoint, worker);
            CompletableFuture<Void> future = new CompletableFuture<>();
            StreamObserver<JQuickDataChunkProto> requestObserver = stub.sendData(new StreamObserver<JQuickEmptyNodeProto>() {
                @Override
                public void onNext(JQuickEmptyNodeProto value) {
                    // 接收确认
                }

                @Override
                public void onError(Throwable t) {
                    console.error("Send data to worker failed: " + targetEndpoint.getWorkerId(), t);
                    future.completeExceptionally(t);
                }

                @Override
                public void onCompleted() {
                    console.info("Send data to worker completed: " + targetEndpoint.getWorkerId());
                    future.complete(null);
                }
            });
            requestObserver.onNext(chunk);
            requestObserver.onCompleted();
            future.get(30, TimeUnit.SECONDS);
            console.info("Sent partition " + chunk.getPartitionId() + " to worker " + targetEndpoint.getWorkerId());

        } catch (Exception e) {
            console.error("Failed to send chunk to worker: " + targetEndpoint.getWorkerId(), e);
            throw new RuntimeException("Failed to send data to worker: " + targetEndpoint.getWorkerId(), e);
        }
    }

    /**
     * 获取分发服务 Stub（使用 WorkerEndpoint）
     */
    private JQuickDataDistributionServiceGrpc.JQuickDataDistributionServiceStub getDistributionStub(WorkerEndpoint endpoint, JQuickWorker worker) {
        return stubCache.computeIfAbsent(endpoint.getWorkerId(), id -> {
            ManagedChannel channel = ManagedChannelBuilder
                    .forAddress(endpoint.getHost(), endpoint.getPort())
                    .usePlaintext()
                    .build();
            channelCache.put(endpoint.getWorkerId(), channel);
            worker.getWorkerChannels().put(endpoint.getIndex(), channel);
            console.info("Created gRPC channel to worker: " + endpoint.getWorkerId() + " at " + endpoint.getHost() + ":" + endpoint.getPort());
            return JQuickDataDistributionServiceGrpc.newStub(channel);
        });
    }

    private int computeHashPartition(JQuickRow row, List<JQuickExpression> partitionKeys, int numPartitions, JQuickExpressionEvaluator evaluator) {
        int hash = 0;
        if (partitionKeys == null || partitionKeys.isEmpty()) {
            return Math.abs(row.hashCode()) % numPartitions;
        }
        for (JQuickExpression key : partitionKeys) {
            Object value = evaluator.evaluateExpression(row, key);
            hash = 31 * hash + (value != null ? value.hashCode() : 0);
        }
        return Math.abs(hash) % numPartitions;
    }

    private int computeRangePartition(JQuickRow row, List<JQuickExpression> partitionKeys, int numPartitions, JQuickExpressionEvaluator evaluator) {
        if (partitionKeys == null || partitionKeys.isEmpty()) {
            return 0;
        }
        Object value = evaluator.evaluateExpression(row, partitionKeys.get(0));
        if (value == null) {
            return 0;
        }
        return Math.abs(value.hashCode()) % numPartitions;
    }

    private int computeBucketPartition(JQuickRow row, List<JQuickExpression> partitionKeys, int numPartitions, JQuickExpressionEvaluator evaluator) {
        if (partitionKeys == null || partitionKeys.isEmpty()) {
            return Math.abs(row.hashCode()) % numPartitions;
        }
        int hash = 0;
        for (JQuickExpression key : partitionKeys) {
            Object value = evaluator.evaluateExpression(row, key);
            if (value != null) {
                hash = 31 * hash + value.hashCode();
            }
        }
        return Math.abs(hash) % numPartitions;
    }

    /**
     * 关闭所有连接
     */
    public void shutdown() {
        for (ManagedChannel channel : channelCache.values()) {
            try {
                channel.shutdown();
            } catch (Exception e) {
                // ignore
            }
        }
        channelCache.clear();
        stubCache.clear();
        workerEndpoints.clear();
    }
}