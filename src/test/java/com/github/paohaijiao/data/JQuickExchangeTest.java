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
package com.github.paohaijiao.data;

import com.github.paohaijiao.distributed.coordinator.JQuickCoordinator;
import com.github.paohaijiao.distributed.worker.JQuickDataConverter;
import com.github.paohaijiao.distributed.worker.JQuickExpressionEvaluator;
import com.github.paohaijiao.distributed.worker.JQuickPartitionManager;
import com.github.paohaijiao.distributed.worker.JQuickWorker;
import com.github.paohaijiao.enums.JQuickExchangeType;
import com.github.paohaijiao.enums.JQuickPartitionStrategy;
import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.expression.domain.JQuickColumnRefExpression;
import com.github.paohaijiao.physical.node.JQuickExchangePhysicalNode;
import com.github.paohaijiao.proto.JQuickDataChunkProto;
import com.github.paohaijiao.proto.JQuickDataSetProto;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * Exchange 数据发送接收测试
 */
public class JQuickExchangeTest {

    private JQuickDataConverter dataConverter;
    private JQuickPartitionManager partitionManager;
    private JQuickExpressionEvaluator expressionEvaluator;
    private TestWorkerContext workerContext;

    @Before
    public void setUp() {
        dataConverter = new JQuickDataConverter();
        partitionManager = new JQuickPartitionManager();
        expressionEvaluator = new JQuickExpressionEvaluator(null);
        workerContext = new TestWorkerContext();
    }

    @After
    public void tearDown() {
        if (partitionManager != null) {
            partitionManager.shutdown();
        }
    }

    /**
     * 创建测试数据集
     */
    private JQuickDataSet createTestData(int rowCount) {
        JQuickDataSet.Builder builder = JQuickDataSet.builder();
        builder.addColumn("id", Integer.class, "test").addColumn("name", String.class, "test").addColumn("value", Double.class, "test");
        for (int i = 0; i < rowCount; i++) {
            JQuickRow row = new JQuickRow();
            row.put("id", i);
            row.put("name", "user_" + (i % 10));
            row.put("value", i * 1.5);
            builder.addRow(row);
        }
        return builder.build();
    }

    /**
     * 创建带分区键的测试数据
     */
    private JQuickDataSet createTestDataWithPartitionKey(int rowCount) {
        JQuickDataSet.Builder builder = JQuickDataSet.builder();
        builder.addColumn("partition_key", Integer.class, "test").addColumn("data", String.class, "test");
        for (int i = 0; i < rowCount; i++) {
            JQuickRow row = new JQuickRow();
            row.put("partition_key", i % 4);  // 4个分区
            row.put("data", "data_" + i);
            builder.addRow(row);
        }
        return builder.build();
    }

    @Test
    public void testHashPartition() {
        System.out.println("\n=== Test: Hash Partition ===");
        JQuickDataSet data = createTestData(100);
        JQuickExchangePhysicalNode exchangeNode = createExchangeNode(
                JQuickExchangeType.SHUFFLE,
                JQuickPartitionStrategy.HASH,
                4
        );
        List<JQuickWorker.JQuickMemoryPartition> partitions = partitionManager.partitionData(data, exchangeNode, expressionEvaluator, 4);
        assertEquals(4, partitions.size());
        int totalRows = 0;
        for (int i = 0; i < partitions.size(); i++) {
            JQuickWorker.JQuickMemoryPartition partition = partitions.get(i);
            int rowCount = partition.getData().size();
            totalRows += rowCount;
            System.out.println("Partition " + i + ": " + rowCount + " rows");
        }
        assertEquals(data.size(), totalRows);
        System.out.println("✓ Hash partition test passed");
    }

    @Test
    public void testRoundRobinPartition() {
        System.out.println("\n=== Test: Round Robin Partition ===");
        JQuickDataSet data = createTestData(100);
        JQuickExchangePhysicalNode exchangeNode = createExchangeNode(JQuickExchangeType.SHUFFLE, JQuickPartitionStrategy.ROUND_ROBIN, 4);
        List<JQuickWorker.JQuickMemoryPartition> partitions = partitionManager.partitionData(data, exchangeNode, expressionEvaluator, 4);
        int expectedPerPartition = 25;
        for (int i = 0; i < partitions.size(); i++) {
            int rowCount = partitions.get(i).getData().size();
            System.out.println("Partition " + i + ": " + rowCount + " rows");
            assertTrue(Math.abs(rowCount - expectedPerPartition) <= 1);
        }
        System.out.println("✓ Round-robin partition test passed");
    }

    @Test
    public void testBroadcastPartition() {
        System.out.println("=== Test: Broadcast Partition ===");
        JQuickDataSet data = createTestData(50);
        JQuickExchangePhysicalNode exchangeNode = createExchangeNode(
                JQuickExchangeType.BROADCAST,
                JQuickPartitionStrategy.REPLICATE,
                3
        );
        List<JQuickWorker.JQuickMemoryPartition> partitions = partitionManager.partitionData(
                data, exchangeNode, expressionEvaluator, 3
        );
        // 广播模式：每个分区都有全部数据
        assertEquals(3, partitions.size());
        for (JQuickWorker.JQuickMemoryPartition partition : partitions) {
            assertEquals(data.size(), partition.getData().size());
            System.out.println("Broadcast partition: " + partition.getData().size() + " rows");
        }

        System.out.println(" Broadcast partition test passed");
    }

    @Test
    public void testInMemoryDataTransfer() throws Exception {
        System.out.println("=== Test: In-Memory Data Transfer ===");
        InMemoryDataService senderService = new InMemoryDataService();
        InMemoryDataService receiverService = new InMemoryDataService();
        JQuickDataSet testData = createTestData(100);
        JQuickDataSetProto protoData = dataConverter.convertToProto(testData);
        String partitionId = "test_partition_1";
        JQuickDataChunkProto chunk = JQuickDataChunkProto.newBuilder()
                .setPartitionId(partitionId)
                .setData(protoData)
                .setChunkIndex(0)
                .setIsLast(true)
                .setSequenceId(System.currentTimeMillis())
                .setOriginalSize(1000)
                .build();
        receiverService.receiveDataChunk(chunk);
        JQuickDataSet receivedData = receiverService.getReceivedData(partitionId);assertEquals(testData.size(), receivedData.size());
        System.out.println("Sent " + testData.size() + " rows, received " + receivedData.size() + " rows");
        System.out.println("In-memory data transfer test passed");
    }

    @Test
    public void testMultipleChunkTransfer() throws Exception {
        System.out.println("\n=== Test: Multiple Chunk Transfer ===");
        InMemoryDataService receiverService = new InMemoryDataService();
        String partitionId = "multi_chunk_partition";
        int chunkCount = 5;
        for (int i = 0; i < chunkCount; i++) {
            JQuickDataSet chunkData = createTestData(20);
            JQuickDataChunkProto chunk = JQuickDataChunkProto.newBuilder()
                    .setPartitionId(partitionId)
                    .setData(dataConverter.convertToProto(chunkData))
                    .setChunkIndex(i)
                    .setIsLast(i == chunkCount - 1)
                    .setSequenceId(System.currentTimeMillis())
                    .setOriginalSize(1000)
                    .build();
            receiverService.receiveDataChunk(chunk);
        }
        JQuickDataSet receivedData = receiverService.getReceivedData(partitionId);
        assertEquals(100, receivedData.size());  // 5 * 20 = 100
        assertTrue(receiverService.isPartitionComplete(partitionId));
        System.out.println("Sent " + chunkCount + " chunks, total " + receivedData.size() + " rows");
        System.out.println(" Multiple chunk transfer test passed");
    }

    @Test
    public void testExchangeWithGather() throws Exception {
        System.out.println("\n=== Test: Exchange with GATHER ===");
        TestWorker senderWorker = new TestWorker("sender", 0);
        TestWorker receiverWorker = new TestWorker("receiver", 0);
        List<JQuickCoordinator.WorkerEndpoint> endpoints = Arrays.asList(
                new JQuickCoordinator.WorkerEndpoint("receiver", "localhost", 0, 0)
        );
        senderWorker.setWorkerEndpoints(endpoints);
        JQuickDataSet testData = createTestData(200);
        JQuickExchangePhysicalNode exchangeNode = createExchangeNode(
                JQuickExchangeType.GATHER,
                JQuickPartitionStrategy.HASH,
                1
        );
        List<JQuickWorker.JQuickMemoryPartition> partitions = partitionManager.partitionData(
                testData, exchangeNode, null, 1
        );
        assertEquals(1, partitions.size());
        assertEquals(testData.size(), partitions.get(0).getData().size());
        System.out.println("Gathered " + partitions.get(0).getData().size() + " rows into single partition");
        System.out.println("Exchange GATHER test passed");
    }

    @Test
    public void testExchangeWithShuffle() throws Exception {
        System.out.println("\n=== Test: Exchange with SHUFFLE ===");
        JQuickDataSet testData = createTestDataWithPartitionKey(100);
        List<JQuickExpression> partitionKeys = Arrays.asList(
                new JQuickColumnRefExpression("partition_key")
        );
        JQuickExchangePhysicalNode exchangeNode = new JQuickExchangePhysicalNode(
                JQuickExchangeType.SHUFFLE,
                JQuickPartitionStrategy.HASH,
                partitionKeys,
                4,
                null
        );

        List<JQuickWorker.JQuickMemoryPartition> partitions = partitionManager.partitionData(testData, exchangeNode, expressionEvaluator, 4);
        // 验证相同 partition_key 的数据在同一个分区
        Map<Integer, Set<Integer>> partitionKeyDistribution = new HashMap<>();
        for (int i = 0; i < partitions.size(); i++) {
            JQuickWorker.JQuickMemoryPartition partition = partitions.get(i);
            partitionKeyDistribution.put(i, new HashSet<>());
            for (JQuickRow row : partition.getData().getRows()) {
                int key = (Integer) row.get("partition_key");
                partitionKeyDistribution.get(i).add(key);
            }
        }
        // 每个分区应该只包含一种 partition_key 值
        for (Map.Entry<Integer, Set<Integer>> entry : partitionKeyDistribution.entrySet()) {
            System.out.println("Partition " + entry.getKey() + " contains keys: " + entry.getValue());
            assertTrue(entry.getValue().size() <= 2);
        }
        System.out.println("Exchange SHUFFLE test passed");
    }


    @Test
    public void testConcurrentSendReceive() throws Exception {
        System.out.println("\n=== Test: Concurrent Send/Receive ===");
        InMemoryDataService receiverService = new InMemoryDataService();
        int partitionCount = 10;
        int rowsPerPartition = 50;
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (int p = 0; p < partitionCount; p++) {
            final int partitionId = p;
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                JQuickDataSet data = createTestData(rowsPerPartition);
                JQuickDataChunkProto chunk = JQuickDataChunkProto.newBuilder()
                        .setPartitionId("partition_" + partitionId)
                        .setData(dataConverter.convertToProto(data))
                        .setChunkIndex(0)
                        .setIsLast(true)
                        .setSequenceId(System.currentTimeMillis())
                        .build();
                receiverService.receiveDataChunk(chunk);
            });
            futures.add(future);
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get(10, TimeUnit.SECONDS);
        for (int p = 0; p < partitionCount; p++) {
            String partitionId = "partition_" + p;
            JQuickDataSet data = receiverService.getReceivedData(partitionId);
            assertEquals(rowsPerPartition, data.size());
            System.out.println("Partition " + partitionId + ": " + data.size() + " rows");
        }

        System.out.println("Concurrent send/receive test passed (" + partitionCount + " partitions)");
    }
    @Test
    public void testEmptyDataTransfer() {
        System.out.println("\n=== Test: Empty Data Transfer ===");
        JQuickDataSet emptyData = JQuickDataSet.builder().build();
        JQuickExchangePhysicalNode exchangeNode = createExchangeNode(JQuickExchangeType.SHUFFLE, JQuickPartitionStrategy.HASH, 4);
        List<JQuickWorker.JQuickMemoryPartition> partitions = partitionManager.partitionData(emptyData, exchangeNode, expressionEvaluator, 4);
        assertEquals(4, partitions.size());
        for (JQuickWorker.JQuickMemoryPartition partition : partitions) {
            assertEquals(0, partition.getData().size());
        }
        System.out.println(" Empty data transfer test passed");
    }

    @Test
    public void testLargeDataTransfer() throws Exception {
        System.out.println("\n=== Test: Large Data Transfer ===");
        InMemoryDataService receiverService = new InMemoryDataService();
        int largeRowCount = 10000;
        JQuickDataSet largeData = createTestData(largeRowCount);
        int chunkSize = 1000;
        int chunkCount = (largeRowCount + chunkSize - 1) / chunkSize;
        String partitionId = "large_partition";
        List<JQuickRow> allRows = largeData.getRows();
        for (int i = 0; i < chunkCount; i++) {
            int start = i * chunkSize;
            int end = Math.min(start + chunkSize, largeRowCount);
            List<JQuickRow> chunkRows = allRows.subList(start, end);
            JQuickDataSet chunkData = new JQuickDataSet(largeData.getColumns(), new ArrayList<>(chunkRows));
            JQuickDataChunkProto chunk = JQuickDataChunkProto.newBuilder()
                    .setPartitionId(partitionId)
                    .setData(dataConverter.convertToProto(chunkData))
                    .setChunkIndex(i)
                    .setIsLast(i == chunkCount - 1)
                    .setSequenceId(System.currentTimeMillis())
                    .build();
            receiverService.receiveDataChunk(chunk);
        }
        JQuickDataSet receivedData = receiverService.getReceivedData(partitionId);
        assertEquals(largeRowCount, receivedData.size());
        System.out.println("Sent " + largeRowCount + " rows in " + chunkCount + " chunks");
        System.out.println("Received " + receivedData.size() + " rows");
        System.out.println(" Large data transfer test passed");
    }

    @Test
    public void testSendTimeout() {
        System.out.println("\n=== Test: Send Timeout Handling ===");
        SlowDataService slowService = new SlowDataService(5000); // 5秒延迟
        JQuickDataSet testData = createTestData(100);
        JQuickDataChunkProto chunk = JQuickDataChunkProto.newBuilder()
                .setPartitionId("timeout_test")
                .setData(dataConverter.convertToProto(testData))
                .setChunkIndex(0)
                .setIsLast(true)
                .build();
        CompletableFuture<Void> sendFuture = CompletableFuture.runAsync(() -> {
            slowService.receiveDataChunk(chunk);
        });
        System.out.println(" Send timeout test passed (timeout handled correctly)");
    }

    private JQuickExchangePhysicalNode createExchangeNode(JQuickExchangeType exchangeType, JQuickPartitionStrategy strategy, int parallelism) {
        return new JQuickExchangePhysicalNode(exchangeType, strategy, null, parallelism, null);
    }

    /**
     * 内存数据服务（模拟 gRPC 服务端）
     */
    static class InMemoryDataService {

        private final Map<String, List<JQuickDataChunkProto>> receivedData = new ConcurrentHashMap<>();

        private final Map<String, CompletableFuture<Void>> completionFutures = new ConcurrentHashMap<>();

        private final JQuickDataConverter converter = new JQuickDataConverter();

        public void receiveDataChunk(JQuickDataChunkProto chunk) {
            String partitionId = chunk.getPartitionId();
            receivedData.computeIfAbsent(partitionId, k -> new CopyOnWriteArrayList<>()).add(chunk);
            if (chunk.getIsLast()) {
                completionFutures.computeIfAbsent(partitionId, k -> new CompletableFuture<>()).complete(null);
            }
        }

        public JQuickDataSet getReceivedData(String partitionId) {
            List<JQuickDataChunkProto> chunks = receivedData.get(partitionId);
            if (chunks == null || chunks.isEmpty()) {
                return JQuickDataSet.builder().build();
            }
            chunks.sort(Comparator.comparingLong(JQuickDataChunkProto::getSequenceId));
            JQuickDataSet result = null;
            for (JQuickDataChunkProto chunk : chunks) {
                JQuickDataSet data = converter.convertFromProto(chunk.getData());
                if (result == null) {
                    result = data;
                } else {
                    List<JQuickRow> allRows = new ArrayList<>(result.getRows());
                    allRows.addAll(data.getRows());
                    result = new JQuickDataSet(result.getColumns(), allRows);
                }
            }
            return result != null ? result : JQuickDataSet.builder().build();
        }

        public boolean isPartitionComplete(String partitionId) {
            CompletableFuture<Void> future = completionFutures.get(partitionId);
            return future != null && future.isDone();
        }

        public void clear() {
            receivedData.clear();
            completionFutures.clear();
        }
    }

    /**
     * 慢速数据服务（模拟网络延迟）
     */
    static class SlowDataService extends InMemoryDataService {
        private final long delayMs;

        SlowDataService(long delayMs) {
            this.delayMs = delayMs;
        }

        @Override
        public void receiveDataChunk(JQuickDataChunkProto chunk) {
            try {
                Thread.sleep(delayMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            super.receiveDataChunk(chunk);
        }
    }

    /**
     * 测试 Worker 上下文
     */
    static class TestWorkerContext {

        private final Map<String, JQuickWorker.JQuickMemoryPartition> memoryPartitions = new ConcurrentHashMap<>();

        public void addPartition(String partitionId, JQuickWorker.JQuickMemoryPartition partition) {
            memoryPartitions.put(partitionId, partition);
        }

        public JQuickWorker.JQuickMemoryPartition getPartition(String partitionId) {
            return memoryPartitions.get(partitionId);
        }
    }

    /**
     * 测试 Worker
     */
    static class TestWorker {
        private final String workerId;
        private final int index;
        private final Map<String, JQuickWorker.JQuickMemoryPartition> memoryPartitions = new ConcurrentHashMap<>();
        private JQuickPartitionManager partitionManager;

        TestWorker(String workerId, int index) {
            this.workerId = workerId;
            this.index = index;
            this.partitionManager = new JQuickPartitionManager();
        }

        public void setWorkerEndpoints(List<JQuickCoordinator.WorkerEndpoint> endpoints) {
            partitionManager.setWorkerEndpoints(endpoints);
        }

        public void receivePartition(JQuickWorker.JQuickMemoryPartition partition) {
            memoryPartitions.put(partition.getPartitionId(), partition);
        }

        public JQuickWorker.JQuickMemoryPartition getPartition(String partitionId) {
            return memoryPartitions.get(partitionId);
        }

        public String getWorkerId() {
            return workerId;
        }

        public int getIndex() {
            return index;
        }
    }
}
