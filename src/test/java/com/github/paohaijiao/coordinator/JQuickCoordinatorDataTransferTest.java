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
package com.github.paohaijiao.coordinator;
import com.github.paohaijiao.datasource.JQuickDataSourceManager;
import com.github.paohaijiao.distributed.coordinator.JQuickCoordinator;
import com.github.paohaijiao.distributed.coordinator.JQuickCoordinator.WorkerEndpoint;
import com.github.paohaijiao.distributed.worker.JQuickWorker;
import com.github.paohaijiao.enums.JQuickExchangeType;
import com.github.paohaijiao.enums.JQuickPartitionStrategy;
import com.github.paohaijiao.expression.domain.JQuickColumnRefExpression;
import com.github.paohaijiao.physical.node.*;
import com.github.paohaijiao.proto.*;
import com.github.paohaijiao.statement.JQuickColumnMeta;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;

/**
 * Coordinator 发送数据到 Worker 的测试用例
 *
 * 测试场景：
 * 1. Coordinator 通过 Exchange 节点分发数据到多个 Worker
 * 2. Worker 接收并处理分区数据
 * 3. 验证数据正确到达目标 Worker
 */
public class JQuickCoordinatorDataTransferTest {

    private JQuickCoordinator coordinator;

    private List<JQuickWorker> workers;

    private List<WorkerEndpoint> endpoints;

    private static final int WORKER_COUNT = 3;

    private static final int BASE_PORT = 20000;

    private static final String TEST_TABLE = "test_data";

    @Before
    public void setUp() throws Exception {
        // 创建 Workers
        workers = new ArrayList<>();
        endpoints = new ArrayList<>();
        for (int i = 0; i < WORKER_COUNT; i++) {
            String workerId = "worker_" + i;
            int port = BASE_PORT + i;
            JQuickWorker worker = new JQuickWorker(workerId, port);
            worker.start();
            workers.add(worker);
            endpoints.add(new WorkerEndpoint(workerId, "localhost", port, i));
        }
        for (JQuickWorker worker : workers) {
            worker.setWorkerEndpoints(endpoints);
        }
        registerTestData();
        coordinator = new JQuickCoordinator("coordinator_test", endpoints);

        Thread.sleep(2000);
    }

    @After
    public void tearDown() {
        if (coordinator != null) {
            coordinator.shutdown();
        }
        for (JQuickWorker worker : workers) {
            worker.stop();
        }
        JQuickDataSourceManager.clearAll();
    }

    /**
     * 注册测试数据
     */
    private void registerTestData() {
        List<JQuickColumnMeta> columns = Arrays.asList(
                new JQuickColumnMeta("id", Integer.class, TEST_TABLE),
                new JQuickColumnMeta("name", String.class, TEST_TABLE),
                new JQuickColumnMeta("value", Integer.class, TEST_TABLE),
                new JQuickColumnMeta("partition_key", String.class, TEST_TABLE)
        );

        List<JQuickRow> rows = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            JQuickRow row = new JQuickRow();
            row.put("id", i);
            row.put("name", "user_" + i);
            row.put("value", i * 10);
            row.put("partition_key", "key_" + (i % WORKER_COUNT));
            rows.add(row);
        }
        JQuickDataSet data = new JQuickDataSet(columns, rows);
        JQuickDataSourceManager.registerTable(TEST_TABLE, data);
    }

    /**
     * 测试1：基本的数据分发 - Shuffle 方式
     *
     * 场景：使用 SHUFFLE 方式将数据分发到所有 Worker
     */
    @Test
    public void testShuffleDataToAllWorkers() throws Exception {
            System.out.println("\n=== Test: Shuffle Data to All Workers (with result collection) ===");
            JQuickDataSet testData = JQuickDataSourceManager.getTable(TEST_TABLE);
            CompletableFuture<Void> broadcastFuture = coordinator.broadcastTable(TEST_TABLE, testData, true);
            broadcastFuture.get(30, TimeUnit.SECONDS);  // 等待广播完成
            System.out.println("Table data broadcasted to all workers");

            // 5. 也在 Coordinator 本地注册（用于统计等）
            JQuickDataSourceManager.registerTable(TEST_TABLE, testData);

            // 6. 构建物理计划
            JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode(
                    TEST_TABLE, "t",
                    new HashSet<>(Arrays.asList("id", "name", "value")),
                    null
            );

            JQuickExchangePhysicalNode shuffleNode = new JQuickExchangePhysicalNode(
                    JQuickExchangeType.SHUFFLE,
                    JQuickPartitionStrategy.HASH,
                    Collections.singletonList(new JQuickColumnRefExpression("id")),
                    WORKER_COUNT,
                    scanNode
            );

            JQuickExchangePhysicalNode gatherNode = new JQuickExchangePhysicalNode(
                    JQuickExchangeType.GATHER,
                    JQuickPartitionStrategy.REPLICATE,
                    null,
                    1,
                    shuffleNode
            );

            // 7. 执行查询
            String queryId = "test_shuffle_" + System.currentTimeMillis();
            CompletableFuture<JQuickDataSet> future = coordinator.executeQuery(queryId, gatherNode);
            JQuickDataSet result = future.get(6000000, TimeUnit.SECONDS);

            assertNotNull(result);
            assertEquals("Should have all 100 rows after shuffle + gather", 100, result.size());
            System.out.println("Shuffle + Gather completed, result size: " + result.size());
            result.printSummary();

            for (JQuickWorker worker : workers) {
                worker.stop();
            }
    }

    /**
     * 测试2：广播数据到所有 Worker
     *
     * 场景：使用 BROADCAST 方式将相同的数据发送到所有 Worker
     */
    @Test
    public void testBroadcastDataToAllWorkers() throws Exception {
        System.out.println("\n=== Test: Broadcast Data to All Workers ===");
        // 创建 TableScan 节点
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode(
                TEST_TABLE, "t",
                new HashSet<>(Arrays.asList("id", "name", "value")),
                null
        );

        // 创建 Exchange 节点（BROADCAST 分发）
        JQuickExchangePhysicalNode exchangeNode = new JQuickExchangePhysicalNode(
                JQuickExchangeType.BROADCAST,
                JQuickPartitionStrategy.REPLICATE,
                null,
                WORKER_COUNT,
                scanNode
        );

        String queryId = "test_broadcast_" + System.currentTimeMillis();
        CompletableFuture<JQuickDataSet> future = coordinator.executeQuery(queryId, exchangeNode);

        JQuickDataSet result = future.get(30, TimeUnit.SECONDS);

        assertNotNull(result);
        System.out.println("Broadcast completed, result size: " + result.size());
    }

    /**
     * 测试3：GATHER 方式 - 从所有 Worker 收集数据到单个 Worker
     *
     * 场景：将分散在多个 Worker 的数据收集到 Coordinator
     */
    @Test
    public void testGatherDataToCoordinator() throws Exception {
        System.out.println("\n=== Test: Gather Data to Coordinator ===");

        // 创建 TableScan 节点
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode(
                TEST_TABLE, "t",
                new HashSet<>(Arrays.asList("id", "name", "value")),
                null
        );

        // 创建 Exchange 节点（GATHER 收集）
        JQuickExchangePhysicalNode exchangeNode = new JQuickExchangePhysicalNode(
                JQuickExchangeType.GATHER,
                JQuickPartitionStrategy.REPLICATE,
                null,
                1,  // 收集到单个节点
                scanNode
        );

        String queryId = "test_gather_" + System.currentTimeMillis();
        CompletableFuture<JQuickDataSet> future = coordinator.executeQuery(queryId, exchangeNode);

        JQuickDataSet result = future.get(30, TimeUnit.SECONDS);

        assertNotNull(result);
        // GATHER 应该返回所有数据
        assertEquals("Should have 100 rows", 100, result.size());

        System.out.println("Gathered " + result.size() + " rows successfully");
        result.printSummary();
    }

    /**
     * 测试4：基于哈希的数据分区
     *
     * 场景：验证数据按分区键正确分发到不同 Worker
     */
    @Test
    public void testHashPartitionDataDistribution() throws Exception {
        System.out.println("\n=== Test: Hash Partition Data Distribution ===");

        // 记录每个 Worker 收到的数据量
        Map<String, AtomicInteger> workerDataCount = new HashMap<>();
        for (JQuickWorker worker : workers) {
            workerDataCount.put(worker.getWorkerId(), new AtomicInteger(0));
        }

        // 创建 TableScan 节点
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode(
                TEST_TABLE, "t",
                new HashSet<>(Arrays.asList("id", "name", "value", "partition_key")),
                null
        );

        // 按 partition_key 进行哈希分区
        JQuickExchangePhysicalNode exchangeNode = new JQuickExchangePhysicalNode(
                JQuickExchangeType.SHUFFLE,
                JQuickPartitionStrategy.HASH,
                Collections.singletonList(new com.github.paohaijiao.expression.domain.JQuickColumnRefExpression("partition_key")),
                WORKER_COUNT,
                scanNode
        );

        String queryId = "test_hash_partition_" + System.currentTimeMillis();
        CompletableFuture<JQuickDataSet> future = coordinator.executeQuery(queryId, exchangeNode);

        JQuickDataSet result = future.get(30, TimeUnit.SECONDS);

        assertNotNull(result);

        // 等待数据分发完成
        Thread.sleep(2000);

        System.out.println("Hash partition distribution completed");
        System.out.println("Worker data distribution:");
        for (Map.Entry<String, AtomicInteger> entry : workerDataCount.entrySet()) {
            System.out.println("  " + entry.getKey() + ": " + entry.getValue().get() + " rows");
        }
    }

    /**
     * 测试5：两阶段数据分发（SHUFFLE -> GATHER）
     *
     * 场景：先 Shuffle 再 Gather，模拟完整的分布式处理流程
     */
    @Test
    public void testTwoStageDataTransfer() throws Exception {
        System.out.println("\n=== Test: Two-Stage Data Transfer (Shuffle -> Gather) ===");

        // 第一阶段：TableScan
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode(
                TEST_TABLE, "t",
                new HashSet<>(Arrays.asList("id", "name", "value")),
                null
        );

        // 第二阶段：Shuffle 分发
        JQuickExchangePhysicalNode shuffleNode = new JQuickExchangePhysicalNode(
                JQuickExchangeType.SHUFFLE,
                JQuickPartitionStrategy.HASH,
                Collections.singletonList(new com.github.paohaijiao.expression.domain.JQuickColumnRefExpression("id")),
                WORKER_COUNT,
                scanNode
        );

        // 第三阶段：Gather 收集
        JQuickExchangePhysicalNode gatherNode = new JQuickExchangePhysicalNode(
                JQuickExchangeType.GATHER,
                JQuickPartitionStrategy.REPLICATE,
                null,
                1,
                shuffleNode
        );

        String queryId = "test_two_stage_" + System.currentTimeMillis();
        CompletableFuture<JQuickDataSet> future = coordinator.executeQuery(queryId, gatherNode);

        JQuickDataSet result = future.get(60, TimeUnit.SECONDS);

        assertNotNull(result);
        // 最终应该收集到所有数据
        assertEquals("Should have all 100 rows after two-stage transfer", 100, result.size());

        System.out.println("Two-stage transfer completed, final result size: " + result.size());
        result.printSummary();
    }

    /**
     * 测试6：大数据量传输性能测试
     */
    @Test
    public void testLargeDataTransfer() throws Exception {
        System.out.println("\n=== Test: Large Data Transfer Performance ===");

        // 注册大数据量表
        registerLargeTestData();

        long startTime = System.currentTimeMillis();

        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode(
                "large_table", "t",
                new HashSet<>(Arrays.asList("id", "name", "value")),
                null
        );

        JQuickExchangePhysicalNode exchangeNode = new JQuickExchangePhysicalNode(
                JQuickExchangeType.SHUFFLE,
                JQuickPartitionStrategy.ROUND_ROBIN,
                null,
                WORKER_COUNT,
                scanNode
        );

        String queryId = "test_large_data_" + System.currentTimeMillis();
        CompletableFuture<JQuickDataSet> future = coordinator.executeQuery(queryId, exchangeNode);

        JQuickDataSet result = future.get(120, TimeUnit.SECONDS);

        long endTime = System.currentTimeMillis();

        assertNotNull(result);
        System.out.println("Large data transfer completed in " + (endTime - startTime) + "ms");
        System.out.println("Result size: " + result.size());
    }

    /**
     * 测试7：直接发送数据块到 Worker（底层 API 测试）
     */
    @Test
    public void testDirectDataChunkSend() throws Exception {
        System.out.println("\n=== Test: Direct Data Chunk Send ===");

        // 创建测试数据
        JQuickDataSet testData = createTestDataSet(50);

        // 选择目标 Worker
        WorkerEndpoint targetWorker = endpoints.get(0);

        // 构建数据块
        JQuickDataChunkProto chunk = JQuickDataChunkProto.newBuilder()
                .setPartitionId("test_partition")
                .setChunkIndex(0)
                .setIsLast(true)
                .setSequenceId(System.currentTimeMillis())
                .setOriginalSize(1024)
                .build();

        // 通过 gRPC 发送数据
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress(targetWorker.getHost(), targetWorker.getPort())
                .usePlaintext()
                .build();

        try {
            JQuickDataDistributionServiceGrpc.JQuickDataDistributionServiceStub stub =
                    JQuickDataDistributionServiceGrpc.newStub(channel);

            CompletableFuture<Boolean> sendFuture = new CompletableFuture<>();

            stub.sendData(new io.grpc.stub.StreamObserver<JQuickEmptyNodeProto>() {
                @Override
                public void onNext(JQuickEmptyNodeProto value) {
                    System.out.println("Received acknowledgment");
                }

                @Override
                public void onError(Throwable t) {
                    System.err.println("Send error: " + t.getMessage());
                    sendFuture.complete(false);
                }

                @Override
                public void onCompleted() {
                    System.out.println("Send completed");
                    sendFuture.complete(true);
                }
            }).onNext(chunk);

            Boolean success = sendFuture.get(10, TimeUnit.SECONDS);
            assertTrue("Data chunk should be sent successfully", success);

            System.out.println("Direct data chunk sent successfully");

        } finally {
            channel.shutdown();
        }
    }

    /**
     * 测试8：验证数据完整性
     */
    @Test
    public void testDataIntegrityAfterTransfer() throws Exception {
        System.out.println("\n=== Test: Data Integrity After Transfer ===");

        // 原始数据
        JQuickDataSet originalData = JQuickDataSourceManager.getTable(TEST_TABLE);
        Map<Integer, JQuickRow> originalMap = new HashMap<>();
        for (JQuickRow row : originalData.getRows()) {
            originalMap.put(row.getInt("id"), row);
        }

        // 执行数据分发并收集
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode(
                TEST_TABLE, "t",
                new HashSet<>(Arrays.asList("id", "name", "value")),
                null
        );

        JQuickExchangePhysicalNode exchangeNode = new JQuickExchangePhysicalNode(
                JQuickExchangeType.GATHER,
                JQuickPartitionStrategy.REPLICATE,
                null,
                1,
                scanNode
        );

        String queryId = "test_integrity_" + System.currentTimeMillis();
        CompletableFuture<JQuickDataSet> future = coordinator.executeQuery(queryId, exchangeNode);

        JQuickDataSet result = future.get(30, TimeUnit.SECONDS);

        // 验证数据完整性
        assertEquals("Row count should match", originalData.size(), result.size());

        for (JQuickRow row : result.getRows()) {
            Integer id = row.getInt("id");
            JQuickRow original = originalMap.get(id);
            assertNotNull("Row with id " + id + " should exist", original);
            assertEquals("Name should match", original.getString("name"), row.getString("name"));
            assertEquals("Value should match", original.getInt("value"), row.getInt("value"));
        }

        System.out.println("Data integrity verified: all " + result.size() + " rows match");
    }

    // ==================== 辅助方法 ====================

    /**
     * 创建测试数据集
     */
    private JQuickDataSet createTestDataSet(int rowCount) {
        List<JQuickColumnMeta> columns = Arrays.asList(
                new JQuickColumnMeta("id", Integer.class, "test"),
                new JQuickColumnMeta("name", String.class, "test"),
                new JQuickColumnMeta("value", Integer.class, "test")
        );

        List<JQuickRow> rows = new ArrayList<>();
        for (int i = 1; i <= rowCount; i++) {
            JQuickRow row = new JQuickRow();
            row.put("id", i);
            row.put("name", "item_" + i);
            row.put("value", i * 100);
            rows.add(row);
        }

        return new JQuickDataSet(columns, rows);
    }

    /**
     * 注册大数据量测试数据
     */
    private void registerLargeTestData() {
        List<JQuickColumnMeta> columns = Arrays.asList(
                new JQuickColumnMeta("id", Integer.class, "large_table"),
                new JQuickColumnMeta("name", String.class, "large_table"),
                new JQuickColumnMeta("value", Integer.class, "large_table")
        );

        List<JQuickRow> rows = new ArrayList<>();
        int largeRowCount = 10000;  // 1万条数据
        for (int i = 1; i <= largeRowCount; i++) {
            JQuickRow row = new JQuickRow();
            row.put("id", i);
            row.put("name", "large_item_" + i);
            row.put("value", i);
            rows.add(row);
        }

        JQuickDataSet data = new JQuickDataSet(columns, rows);
        JQuickDataSourceManager.registerTable("large_table", data);
        System.out.println("Registered large table with " + largeRowCount + " rows");
    }
}
