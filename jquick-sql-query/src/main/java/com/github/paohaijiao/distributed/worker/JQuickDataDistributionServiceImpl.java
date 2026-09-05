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
import com.github.paohaijiao.proto.*;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 数据分发服务实现类
 * <p>
 * 实现 gRPC 服务 JQuickDataDistributionService 中定义的所有方法：
 * - sendData: 发送数据流（接收端）
 * - receiveData: 接收数据（主动拉取）
 * - broadcastData: 广播数据
 * <p>
 * 该类负责 Worker 之间的数据传输，支持：
 * - 流式数据接收和缓存
 * - 主动拉取数据
 * - 广播模式数据分发
 * - 自动缓存清理
 */
public class JQuickDataDistributionServiceImpl extends JQuickDataDistributionServiceGrpc.JQuickDataDistributionServiceImplBase {

    private static final JConsole console = JConsole.initConsoleEnvironment();
    // 缓存过期时间（毫秒），默认30分钟
    private static final long CACHE_EXPIRY_MS = 30 * 60 * 1000;
    private final JQuickWorker worker;
    // 数据接收缓存：partitionId -> List<JQuickDataChunkProto>
    private final Map<String, List<JQuickDataChunkProto>> receivedDataCache;
    // 数据接收完成标记：partitionId -> CompletableFuture
    private final Map<String, CompletableFuture<Void>> receiveCompletionFutures;
    // 广播接收器注册表：broadcastId -> List<StreamObserver<JQuickDataChunkProto>>
    private final Map<String, List<StreamObserver<JQuickDataChunkProto>>> broadcastReceivers;
    // 数据块序列号生成器
    private final AtomicLong sequenceGenerator;
    // 清理线程调度器
    private final ScheduledExecutorService cleanupScheduler;
    // 最后一次访问时间记录
    private final Map<String, Long> lastAccessTime;

    public JQuickDataDistributionServiceImpl(JQuickWorker worker) {
        this.worker = worker;
        this.receivedDataCache = new ConcurrentHashMap<>();
        this.receiveCompletionFutures = new ConcurrentHashMap<>();
        this.broadcastReceivers = new ConcurrentHashMap<>();
        this.sequenceGenerator = new AtomicLong(0);
        this.lastAccessTime = new ConcurrentHashMap<>();
        this.cleanupScheduler = Executors.newSingleThreadScheduledExecutor();
        cleanupScheduler.scheduleAtFixedRate(this::cleanupExpiredCache, 5, 5, TimeUnit.MINUTES);
        console.info("JQuickDataDistributionServiceImpl initialized");
    }

    /**
     * 发送数据 - 接收来自其他 Worker 的数据流
     * <p>
     * 这是服务端接收数据的方法，客户端通过此方法将数据块发送过来。
     *
     * @param responseObserver 响应观察者，用于通知发送端接收完成
     * @return 数据接收流观察者，用于接收客户端发送的数据块
     */
    @Override
    public StreamObserver<JQuickDataChunkProto> sendData(StreamObserver<JQuickEmptyNodeProto> responseObserver) {
        console.info("New data stream connection established");
        return new DataReceiveStreamObserver(responseObserver);
    }

    /**
     * 接收数据 - 主动拉取数据
     * <p>
     * 客户端通过此方法主动拉取指定分区的数据。
     *
     * @param request          拉取数据请求，包含分区ID、块索引等信息
     * @param responseObserver 响应观察者，用于返回数据块
     */
    @Override
    public void receiveData(JQuickFetchDataRequest request, StreamObserver<JQuickFetchDataResponse> responseObserver) {
        String partitionId = request.getPartitionId();
        int chunkIndex = request.getChunkIndex();
        boolean streaming = request.getStreaming();
        updateAccessTime(partitionId);
        console.info(String.format("Receive data request - partitionId: %s, chunkIndex: %d, streaming: %s", partitionId, chunkIndex, streaming));
        try {
            List<JQuickDataChunkProto> chunks = receivedDataCache.get(partitionId);
            if (chunks == null || chunks.isEmpty()) {
                // 没有数据，返回空响应
                JQuickFetchDataResponse emptyResponse = JQuickFetchDataResponse.newBuilder()
                        .setPartitionId(partitionId)
                        .setChunkIndex(chunkIndex)
                        .setIsLast(true)
                        .setDataSizeBytes(0)
                        .setFromMemory(false)
                        .build();
                responseObserver.onNext(emptyResponse);
                responseObserver.onCompleted();
                return;
            }

            if (streaming) {
                // 流式返回所有数据块
                for (int i = chunkIndex; i < chunks.size(); i++) {
                    JQuickDataChunkProto chunk = chunks.get(i);
                    JQuickFetchDataResponse response = buildFetchResponse(partitionId, i, chunk, i == chunks.size() - 1);
                    responseObserver.onNext(response);
                    // 流控：如果返回的数据量较大，稍微延迟避免内存压力
                    if (chunk.getData().getSerializedSize() > 1024 * 1024) {
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                }
                responseObserver.onCompleted();
            } else {
                // 单次返回指定块
                if (chunkIndex < chunks.size()) {
                    JQuickDataChunkProto chunk = chunks.get(chunkIndex);
                    JQuickFetchDataResponse response = buildFetchResponse(partitionId, chunkIndex, chunk, chunkIndex == chunks.size() - 1);
                    responseObserver.onNext(response);
                } else {
                    JQuickFetchDataResponse response = JQuickFetchDataResponse.newBuilder()
                            .setPartitionId(partitionId)
                            .setChunkIndex(chunkIndex)
                            .setIsLast(true)
                            .setDataSizeBytes(0)
                            .setFromMemory(false)
                            .build();
                    responseObserver.onNext(response);
                }
                responseObserver.onCompleted();
            }

        } catch (Exception e) {
            console.warn(String.format("Receive data failed - partitionId: %s", partitionId), e);
            responseObserver.onError(e);
        }
    }


    /**
     * 订阅广播 - 注册接收广播数据
     *
     * @param broadcastId 广播ID
     * @param observer    数据接收观察者
     */
    public void subscribeToBroadcast(String broadcastId, StreamObserver<JQuickDataChunkProto> observer) {
        broadcastReceivers.computeIfAbsent(broadcastId, k -> new CopyOnWriteArrayList<>()).add(observer);
        console.info(String.format("Subscribed to broadcast - broadcastId: %s", broadcastId));
    }

    /**
     * 取消订阅广播
     *
     * @param broadcastId 广播ID
     * @param observer    数据接收观察者
     */
    public void unsubscribeFromBroadcast(String broadcastId, StreamObserver<JQuickDataChunkProto> observer) {
        List<StreamObserver<JQuickDataChunkProto>> receivers = broadcastReceivers.get(broadcastId);
        if (receivers != null) {
            receivers.remove(observer);
            if (receivers.isEmpty()) {
                broadcastReceivers.remove(broadcastId);
                console.info(String.format("Broadcast removed - broadcastId: %s", broadcastId));
            }
        }
    }

    /**
     * 获取分区数据（合并所有数据块）
     *
     * @param partitionId 分区ID
     * @return 分区的 DataSet
     */
    public JQuickDataSet getPartitionData(String partitionId) {
        updateAccessTime(partitionId);
        List<JQuickDataChunkProto> chunks = receivedDataCache.get(partitionId);
        if (chunks == null || chunks.isEmpty()) {
            return JQuickDataSet.builder().build();
        }
        // 合并所有数据块
        JQuickDataSet result = null;
        for (JQuickDataChunkProto chunk : chunks) {
            JQuickDataSet dataSet = worker.getDataConverter().convertFromProto(chunk.getData());
            if (result == null) {
                result = dataSet;
            } else {
                List<JQuickRow> allRows = new ArrayList<>(result.getRows());
                allRows.addAll(dataSet.getRows());
                result = new JQuickDataSet(result.getColumns(), allRows);
            }
        }
        return result != null ? result : JQuickDataSet.builder().build();
    }

    /**
     * 检查分区数据是否接收完成
     *
     * @param partitionId 分区ID
     * @return 是否完成
     */
    public boolean isPartitionComplete(String partitionId) {
        CompletableFuture<Void> future = receiveCompletionFutures.get(partitionId);
        return future != null && future.isDone();
    }

    /**
     * 等待分区数据接收完成
     *
     * @param partitionId 分区ID
     * @param timeout     超时时间
     * @param unit        时间单位
     * @return 是否成功（未超时）
     * @throws InterruptedException 等待被中断
     * @throws TimeoutException     超时
     * @throws ExecutionException   执行异常
     */
    public boolean awaitPartitionCompletion(String partitionId, long timeout, TimeUnit unit) throws InterruptedException, TimeoutException, ExecutionException {
        CompletableFuture<Void> future = receiveCompletionFutures.get(partitionId);
        if (future == null) {
            return true; // 没有数据需要等待
        }
        future.get(timeout, unit);
        return true;
    }

    /**
     * 发送数据到指定 Worker
     *
     * @param targetWorkerId 目标 Worker ID
     * @param host           主机地址
     * @param port           端口
     * @param chunk          数据块
     * @return 异步结果
     */
    public CompletableFuture<Void> sendDataToWorker(String targetWorkerId, String host, int port, JQuickDataChunkProto chunk) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        try {
            ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port).usePlaintext().maxInboundMessageSize(64 * 1024 * 1024).build();
            JQuickDataDistributionServiceGrpc.JQuickDataDistributionServiceStub stub = JQuickDataDistributionServiceGrpc.newStub(channel);
            StreamObserver<JQuickDataChunkProto> requestObserver = stub.sendData(
                    new StreamObserver<JQuickEmptyNodeProto>() {
                        @Override
                        public void onNext(JQuickEmptyNodeProto value) {
                        }

                        @Override
                        public void onError(Throwable t) {
                            console.warn(String.format("Send data to worker failed - target: %s:%d, workerId: %s", host, port, targetWorkerId), t);
                            future.completeExceptionally(t);
                            channel.shutdown();
                        }

                        @Override
                        public void onCompleted() {
                            console.warn(String.format("Send data to worker completed - target: %s:%d, workerId: %s", host, port, targetWorkerId));
                            future.complete(null);
                            channel.shutdown();
                        }
                    }
            );
            requestObserver.onNext(chunk);
            requestObserver.onCompleted();
        } catch (Exception e) {
            console.warn(String.format("Send data to worker error - target: %s:%d", host, port), e);
            future.completeExceptionally(e);
        }

        return future;
    }

    /**
     * 获取缓存中的数据块数量
     *
     * @param partitionId 分区ID
     * @return 数据块数量
     */
    public int getCachedChunkCount(String partitionId) {
        List<JQuickDataChunkProto> chunks = receivedDataCache.get(partitionId);
        return chunks != null ? chunks.size() : 0;
    }

    /**
     * 获取所有缓存的分区ID
     *
     * @return 分区ID集合
     */
    public Set<String> getAllCachedPartitions() {
        return new HashSet<>(receivedDataCache.keySet());
    }

    /**
     * 手动清理指定分区的缓存
     *
     * @param partitionId 分区ID
     */
    public void clearPartitionCache(String partitionId) {
        receivedDataCache.remove(partitionId);
        receiveCompletionFutures.remove(partitionId);
        lastAccessTime.remove(partitionId);
        console.info(String.format("Cleared partition cache - partitionId: %s", partitionId));
    }

    /**
     * 清理所有分区缓存
     */
    public void clearAllCachedPartitions() {
        receivedDataCache.clear();
        receiveCompletionFutures.clear();
        lastAccessTime.clear();
        console.info("Cleared all partition caches");
    }

    /**
     * 关闭服务，清理资源
     */
    public void shutdown() {
        console.info("Shutting down JQuickDataDistributionServiceImpl...");
        cleanupScheduler.shutdown();
        try {
            if (!cleanupScheduler.awaitTermination(30, TimeUnit.SECONDS)) {
                cleanupScheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            cleanupScheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
        // 关闭所有广播接收器
        for (Map.Entry<String, List<StreamObserver<JQuickDataChunkProto>>> entry : broadcastReceivers.entrySet()) {
            for (StreamObserver<JQuickDataChunkProto> observer : entry.getValue()) {
                try {
                    observer.onCompleted();
                } catch (Exception e) {
                }
            }
        }
        receivedDataCache.clear();
        receiveCompletionFutures.clear();
        broadcastReceivers.clear();
        lastAccessTime.clear();
        console.info("JQuickDataDistributionServiceImpl shutdown complete");
    }

    /**
     * 构建获取数据响应
     */
    private JQuickFetchDataResponse buildFetchResponse(String partitionId, int chunkIndex, JQuickDataChunkProto chunk, boolean isLast) {
        return JQuickFetchDataResponse.newBuilder()
                .setPartitionId(partitionId)
                .setChunkIndex(chunkIndex)
                .setIsLast(isLast)
                .setData(chunk.getData())
                .setDataSizeBytes(chunk.getCompressedSize() > 0 ? chunk.getCompressedSize() : chunk.getOriginalSize())
                .setFromMemory(true)
                .build();
    }

    /**
     * 更新分区最后访问时间
     */
    private void updateAccessTime(String partitionId) {
        lastAccessTime.put(partitionId, System.currentTimeMillis());
    }

    /**
     * 清理过期的缓存数据
     */
    private void cleanupExpiredCache() {
        long now = System.currentTimeMillis();
        Set<String> toRemove = new HashSet<>();
        for (Map.Entry<String, List<JQuickDataChunkProto>> entry : receivedDataCache.entrySet()) {
            String partitionId = entry.getKey();
            Long lastAccess = lastAccessTime.get(partitionId);
            // 如果分区已完成且超过过期时间，或者长时间未访问
            if (isPartitionComplete(partitionId)) {
                if (lastAccess == null || (now - lastAccess) > CACHE_EXPIRY_MS) {
                    toRemove.add(partitionId);
                }
            } else if (lastAccess != null && (now - lastAccess) > CACHE_EXPIRY_MS * 2) {
                // 未完成但长时间未访问，也清理（可能是 abandoned）
                toRemove.add(partitionId);
                console.warn(String.format("Removing abandoned incomplete partition - partitionId: %s", partitionId));
            }
        }

        for (String partitionId : toRemove) {
            receivedDataCache.remove(partitionId);
            receiveCompletionFutures.remove(partitionId);
            lastAccessTime.remove(partitionId);
            console.info(String.format("Cleaned up expired cache - partitionId: %s", partitionId));
        }
        if (!toRemove.isEmpty()) {
            console.info(String.format("Cache cleanup completed - removed %d partitions", toRemove.size()));
        }
    }

    /**
     * 数据接收流观察者
     * <p>
     * 负责接收客户端发送的数据块，并按分区缓存
     */
    private class DataReceiveStreamObserver implements StreamObserver<JQuickDataChunkProto> {
        private final StreamObserver<JQuickEmptyNodeProto> responseObserver;
        private final List<JQuickDataChunkProto> receivedChunks;
        private String currentPartitionId;

        DataReceiveStreamObserver(StreamObserver<JQuickEmptyNodeProto> responseObserver) {
            this.responseObserver = responseObserver;
            this.receivedChunks = new ArrayList<>();
            this.currentPartitionId = null;
        }

        @Override
        public void onNext(JQuickDataChunkProto chunk) {
            String chunkPartitionId = chunk.getPartitionId();
            this.currentPartitionId = chunkPartitionId;
            // 获取或创建分区缓存
            List<JQuickDataChunkProto> chunks = receivedDataCache.computeIfAbsent(chunkPartitionId, k -> new CopyOnWriteArrayList<>());
            // 存储数据块
            chunks.add(chunk);
            receivedChunks.add(chunk);
            // 更新访问时间
            updateAccessTime(chunkPartitionId);
            // 按序列号排序
            chunks.sort(Comparator.comparingLong(JQuickDataChunkProto::getSequenceId));
            console.info(String.format("Received data chunk - partitionId: %s, chunkIndex: %d, isLast: %s, dataSize: %d", chunkPartitionId, chunk.getChunkIndex(), chunk.getIsLast(), chunk.getData().getSerializedSize()));
            // 如果是最后一个块，标记完成
            if (chunk.getIsLast()) {
                CompletableFuture<Void> future = receiveCompletionFutures.computeIfAbsent(chunkPartitionId, k -> new CompletableFuture<>());
                future.complete(null);
                console.info(String.format("Partition complete - partitionId: %s, totalChunks: %d", chunkPartitionId, chunks.size()));
            }
        }

        @Override
        public void onError(Throwable t) {
            console.warn("Data receive stream error", t);
            // 清理不完整的数据
            if (currentPartitionId != null) {
                List<JQuickDataChunkProto> chunks = receivedDataCache.get(currentPartitionId);
                if (chunks != null) {
                    for (JQuickDataChunkProto chunk : receivedChunks) {
                        chunks.remove(chunk);
                    }
                    if (chunks.isEmpty()) {
                        receivedDataCache.remove(currentPartitionId);
                        receiveCompletionFutures.remove(currentPartitionId);
                        lastAccessTime.remove(currentPartitionId);
                    }
                }
            }

            responseObserver.onError(t);
        }

        @Override
        public void onCompleted() {
            console.info("Data receive stream completed");
            responseObserver.onNext(JQuickEmptyNodeProto.newBuilder().build());
            responseObserver.onCompleted();
        }
    }

    /**
     * 广播数据接收器
     * <p>
     * 负责接收广播数据并转发给所有订阅者
     */
    private class BroadcastDataReceiver implements StreamObserver<JQuickDataChunkProto> {

        private final String broadcastId;

        private final StreamObserver<JQuickBroadcastResponse> responseObserver;

        private final List<String> failedWorkers;

        private int receivedCount;

        private int successCount;

        BroadcastDataReceiver(String broadcastId, StreamObserver<JQuickBroadcastResponse> responseObserver) {
            this.broadcastId = broadcastId;
            this.responseObserver = responseObserver;
            this.receivedCount = 0;
            this.successCount = 0;
            this.failedWorkers = new ArrayList<>();
        }

        @Override
        public void onNext(JQuickDataChunkProto chunk) {
            receivedCount++;
            List<StreamObserver<JQuickDataChunkProto>> receivers = broadcastReceivers.get(broadcastId);
            if (receivers != null) {
                for (StreamObserver<JQuickDataChunkProto> receiver : receivers) {
                    try {
                        receiver.onNext(chunk);
                        successCount++;
                    } catch (Exception e) {
                        failedWorkers.add(receiver.toString());
                        console.warn(String.format("Broadcast to receiver failed - broadcastId: %s", broadcastId), e);
                    }
                }
            }
            console.warn(String.format("Broadcast chunk sent - broadcastId: %s, chunkIndex: %d, receivers: %d", broadcastId, chunk.getChunkIndex(), receivers != null ? receivers.size() : 0));
        }

        @Override
        public void onError(Throwable t) {
            console.warn(String.format("Broadcast error - broadcastId: %s", broadcastId), t);
            JQuickBroadcastResponse response = JQuickBroadcastResponse.newBuilder()
                    .setSuccess(false)
                    .setSuccessCount(successCount)
                    .addAllFailedWorkers(failedWorkers)
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            // 清理广播接收器
            broadcastReceivers.remove(broadcastId);
        }

        @Override
        public void onCompleted() {
            console.info(String.format("Broadcast completed - broadcastId: %s, chunks: %d, successCount: %d", broadcastId, receivedCount, successCount));
            JQuickBroadcastResponse response = JQuickBroadcastResponse.newBuilder().setSuccess(true).setSuccessCount(successCount).addAllFailedWorkers(failedWorkers).build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            // 通知所有接收者完成
            List<StreamObserver<JQuickDataChunkProto>> receivers = broadcastReceivers.remove(broadcastId);
            if (receivers != null) {
                for (StreamObserver<JQuickDataChunkProto> receiver : receivers) {
                    try {
                        receiver.onCompleted();
                    } catch (Exception e) {
                        console.info("Error completing broadcast receiver", e);
                    }
                }
            }
        }
    }
}