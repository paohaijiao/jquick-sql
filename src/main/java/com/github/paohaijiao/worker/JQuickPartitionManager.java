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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * 分区管理服务 - 处理数据分区和分发
 */
public class JQuickPartitionManager {

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
     * 计算哈希分区
     */
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

    /**
     * 计算范围分区
     */
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

    /**
     * 计算桶分区
     */
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
     * 发送数据到目标 Worker
     */
    public void sendToWorker(JQuickWorker.JQuickMemoryPartition partition, int targetParallelism, JQuickExchangeType exchangeType, JQuickWorker worker) {
        if (exchangeType == JQuickExchangeType.GATHER) {
            sendToSingleWorker(partition, 0, worker);
        } else if (exchangeType == JQuickExchangeType.BROADCAST) {
            sendToAllWorkers(partition, worker);
        } else {
            int targetWorkerId = partition.getIndex() % targetParallelism;
            sendToSingleWorker(partition, targetWorkerId, worker);
        }
    }

    /**
     * 发送到单个 Worker
     */
    private void sendToSingleWorker(JQuickWorker.JQuickMemoryPartition partition, int targetWorkerId, JQuickWorker worker) {
        JQuickDataChunkProto chunk = buildDataChunk(partition, worker);
        sendChunkAsync(chunk, targetWorkerId, worker);
    }

    /**
     * 发送到所有 Worker
     */
    private void sendToAllWorkers(JQuickWorker.JQuickMemoryPartition partition, JQuickWorker worker) {
        JQuickDataChunkProto chunk = buildDataChunk(partition, worker);
        for (int i = 0; i < 4; i++) {
            sendChunkAsync(chunk, i, worker);
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
     * 异步发送数据块
     */
    private void sendChunkAsync(JQuickDataChunkProto chunk, int targetWorkerId, JQuickWorker worker) {
        worker.getExecutor().submit(() -> {
            try {
                JQuickDataDistributionServiceGrpc.JQuickDataDistributionServiceStub stub = getDistributionStub(targetWorkerId, worker);
                CompletableFuture<Void> future = new CompletableFuture<>();
                stub.sendData(new StreamObserver<JQuickEmptyNodeProto>() {
                    @Override
                    public void onNext(JQuickEmptyNodeProto value) {
                    }
                    @Override
                    public void onError(Throwable t) {
                        future.completeExceptionally(t);
                    }

                    @Override
                    public void onCompleted() {
                        future.complete(null);
                    }
                }).onNext(chunk);
                future.get(30, TimeUnit.SECONDS);
            } catch (Exception e) {
               e.printStackTrace();
            }
        });
    }

    /**
     * 获取分发服务 Stub
     */
    private JQuickDataDistributionServiceGrpc.JQuickDataDistributionServiceStub getDistributionStub(int workerId, JQuickWorker worker) {
        return worker.getDistributionStubs().computeIfAbsent(workerId, id -> {
            ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9000 + id).usePlaintext().build();
            worker.getWorkerChannels().put(id, channel);
            return JQuickDataDistributionServiceGrpc.newStub(channel);
        });
    }
}