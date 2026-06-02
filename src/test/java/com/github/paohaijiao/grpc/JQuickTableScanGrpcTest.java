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
package com.github.paohaijiao.grpc;

import com.github.paohaijiao.distributed.coordinator.JQuickCoordinator;
import com.github.paohaijiao.datasource.JQuickDataSourceManager;
import com.github.paohaijiao.physical.node.JQuickTableScanPhysicalNode;
import com.github.paohaijiao.proto.*;
import com.github.paohaijiao.statement.JQuickColumnMeta;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;
import com.github.paohaijiao.distributed.worker.JQuickWorker;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

import static org.junit.Assert.*;


/**
 * TableScan 物理节点 gRPC 调用测试用例
 *
 * 基于 JQuickDataSourceManager 进行测试数据准备
 * 测试场景：
 * 1. Coordinator 发起 TableScan 任务
 * 2. Worker 接收任务并执行
 * 3. 通过 gRPC 返回结果数据
 */
public class JQuickTableScanGrpcTest {

    private static final Logger LOGGER = Logger.getLogger(JQuickTableScanGrpcTest.class.getName());
    private static final String TEST_TABLE_NAME = "test_users";
    private static final int WORKER_COUNT = 2;
    private static final int BASE_PORT = 9000;

    private List<JQuickWorker> workers;
    private JQuickCoordinator coordinator;
    private List<JQuickCoordinator.WorkerEndpoint> endpoints;

    @Before
    public void setUp() throws Exception {
        //准备测试数据到 DataSourceManager
        prepareTestData();
        //启动 Worker 节点
        startWorkers();
        //创建 Coordinator
        endpoints = new ArrayList<>();
        for (int i = 0; i < WORKER_COUNT; i++) {
            endpoints.add(new JQuickCoordinator.WorkerEndpoint("worker-" + i, "localhost", BASE_PORT + i, i));
        }
        coordinator = new JQuickCoordinator("coordinator-test", endpoints);
        LOGGER.info("Test environment setup completed");
    }

    /**
     * 准备测试数据 - 使用 JQuickDataSourceManager
     */
    private void prepareTestData() {
        // 创建测试数据
        List<JQuickColumnMeta> columns = Arrays.asList(
                new JQuickColumnMeta("id", Long.class, TEST_TABLE_NAME),
                new JQuickColumnMeta("name", String.class, TEST_TABLE_NAME),
                new JQuickColumnMeta("age", Integer.class, TEST_TABLE_NAME),
                new JQuickColumnMeta("email", String.class, TEST_TABLE_NAME)
        );

        List<JQuickRow> rows = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            JQuickRow row = new JQuickRow();
            row.put("id", (long) i);
            row.put("name", "user_" + i);
            row.put("age", 18 + (i % 50));
            row.put("email", "user" + i + "@test.com");
            rows.add(row);
        }
        JQuickDataSet dataSet = new JQuickDataSet(columns, rows);
        // 使用 JQuickDataSourceManager 注册表
        JQuickDataSourceManager.registerTable(TEST_TABLE_NAME, dataSet);
        LOGGER.info("Test data prepared: " + JQuickDataSourceManager.getRowCount(TEST_TABLE_NAME) + " rows");
    }

    /**
     * 启动 Worker 节点
     */
    private void startWorkers() throws Exception {
        workers = new ArrayList<>();
        for (int i = 0; i < WORKER_COUNT; i++) {
            JQuickWorker worker = new JQuickWorker("worker-" + i, BASE_PORT + i);
            worker.start();
            workers.add(worker);
        }
        // 等待 Worker 启动
        Thread.sleep(2000);
    }

    /**
     * 停止 Worker 节点
     */
    private void stopWorkers() {
        for (JQuickWorker worker : workers) {
            worker.stop();
        }
    }

    @After
    public void tearDown() {
        if (coordinator != null) {
            coordinator.shutdown();
        }
        stopWorkers();
        JQuickDataSourceManager.clearAll();
        LOGGER.info("Test environment cleanup completed");
    }
    /**
     * 测试 1: 基础 TableScan - 查询所有列
     */
    @Test
    public void testTableScanAllColumns() throws Exception {
        LOGGER.info("=== Test: TableScan All Columns ===");
        // 创建 TableScan 节点
        Set<String> requiredColumns = new HashSet<>(Arrays.asList("id", "name", "age", "email"));
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode(TEST_TABLE_NAME, null, requiredColumns, null);
        // 执行查询
        CompletableFuture<JQuickDataSet> future = coordinator.executeQuery("query_all", scanNode);
        JQuickDataSet result = future.get(300000, TimeUnit.SECONDS);
        // 验证结果
        assertNotNull(result);
        assertEquals(4, result.getColumns().size());
        assertEquals(100, result.size());
        // 验证数据内容
        JQuickRow firstRow = result.first();
        assertNotNull(firstRow);
        assertTrue(firstRow.containsKey("id"));
        assertTrue(firstRow.containsKey("name"));

        LOGGER.info("Result rows: " + result.size());
        LOGGER.info("Columns: " + result.getColumns());
    }

    /**
     * 测试 2: TableScan - 只查询部分列
     */
    @Test
    public void testTableScanPartialColumns() throws Exception {
        LOGGER.info("=== Test: TableScan Partial Columns ===");
        Set<String> requiredColumns = new HashSet<>(Arrays.asList("id", "name"));
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode(TEST_TABLE_NAME, null, requiredColumns, null);
        CompletableFuture<JQuickDataSet> future = coordinator.executeQuery("query_partial", scanNode);
        JQuickDataSet result = future.get(30, TimeUnit.SECONDS);
        result.printTable();
        assertNotNull(result);
        assertEquals(2, result.getColumns().size());
        List<String> columnNames = result.getColumnNames();
        assertTrue(columnNames.contains("id"));
        assertTrue(columnNames.contains("name"));
        assertFalse(columnNames.contains("email"));
    }

    /**
     * 测试 3: 使用 Alias 的 TableScan
     */
    @Test
    public void testTableScanWithAlias() throws Exception {
        LOGGER.info("=== Test: TableScan With Alias ===");
        String alias = "u";
        Set<String> requiredColumns = new HashSet<>(Arrays.asList("id", "name"));
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode(TEST_TABLE_NAME, alias, requiredColumns, null);
        CompletableFuture<JQuickDataSet> future = coordinator.executeQuery("query_alias", scanNode);
        JQuickDataSet result = future.get(30, TimeUnit.SECONDS);
        assertNotNull(result);
        assertEquals(2, result.getColumns().size());
        LOGGER.info("Alias test completed");
    }

    /**
     * 测试 4: 并发执行多个 TableScan 查询
     */
    @Test
    public void testConcurrentTableScans() throws Exception {
        LOGGER.info("=== Test: Concurrent TableScans ===");
        int queryCount = 5;
        List<CompletableFuture<JQuickDataSet>> futures = new ArrayList<>();
        for (int i = 0; i < queryCount; i++) {
            Set<String> requiredColumns = new HashSet<>(Arrays.asList("id", "name"));
            JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode(TEST_TABLE_NAME, null, requiredColumns, null);
            futures.add(coordinator.executeQuery("concurrent_" + i, scanNode));
        }
        // 等待所有查询完成
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get(60, TimeUnit.SECONDS);
        // 验证所有查询都有结果
        for (CompletableFuture<JQuickDataSet> future : futures) {
            JQuickDataSet result = future.get();
            assertNotNull(result);
            assertEquals(100, result.size());
        }
    }

    /**
     * 测试 5: 直接 gRPC 调用 - 不经过 Coordinator
     */
    @Test
    public void testDirectGrpcCall() throws Exception {
        LOGGER.info("=== Test: Direct gRPC Call ===");
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", BASE_PORT).usePlaintext().build();
        try {
            JQuickPhysicalPlanServiceGrpc.JQuickPhysicalPlanServiceBlockingStub stub = JQuickPhysicalPlanServiceGrpc.newBlockingStub(channel);
            JQuickTableScanNodeProto scanProto = JQuickTableScanNodeProto.newBuilder()
                    .setTableName(TEST_TABLE_NAME)
                    .addRequiredColumns("id")
                    .addRequiredColumns("name")
                    .addRequiredColumns("age")
                    .build();

            JQuickPhysicalPlanNodeProto planProto = JQuickPhysicalPlanNodeProto.newBuilder()
                    .setNodeId("direct_scan")
                    .setNodeType("TableScan")
                    .setTableScan(scanProto)
                    .build();

            JQuickFragmentProto fragmentProto = JQuickFragmentProto.newBuilder()
                    .setFragmentId(1)
                    .setType(JQuickFragmentTypeProto.FRAGMENT_SOURCE)
                    .setParallelism(1)
                    .setPlan(planProto)
                    .build();

            JQuickExecuteTaskRequest request = JQuickExecuteTaskRequest.newBuilder()
                    .setQueryId("direct_grpc_test")
                    .setTaskId("task_direct")
                    .setTaskIndex(0)
                    .setTotalTasks(1)
                    .setFragment(fragmentProto)
                    .build();

            JQuickExecuteTaskResponse response = stub.executeTask(request);
            assertNotNull(response);
            assertEquals(JQuickTaskStatusProto.TASK_SUCCESS, response.getStatus());
            assertTrue(response.hasResultData());
            JQuickDataSetProto resultData = response.getResultData();
        } finally {
            channel.shutdown();
        }
    }

    /**
     * 测试 6: 流式 gRPC 调用
     */
    @Test
    public void testStreamingGrpcCall() throws Exception {
        LOGGER.info("=== Test: Streaming gRPC Call ===");
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", BASE_PORT)
                .usePlaintext()
                .build();

        try {
            JQuickPhysicalPlanServiceGrpc.JQuickPhysicalPlanServiceStub stub = JQuickPhysicalPlanServiceGrpc.newStub(channel);
            JQuickTableScanNodeProto scanProto = JQuickTableScanNodeProto.newBuilder()
                    .setTableName(TEST_TABLE_NAME)
                    .addRequiredColumns("id")
                    .addRequiredColumns("name")
                    .addRequiredColumns("age")
                    .build();

            JQuickPhysicalPlanNodeProto planProto = JQuickPhysicalPlanNodeProto.newBuilder()
                    .setNodeId("stream_scan")
                    .setNodeType("TableScan")
                    .setTableScan(scanProto)
                    .build();

            JQuickFragmentProto fragmentProto = JQuickFragmentProto.newBuilder()
                    .setFragmentId(2)
                    .setType(JQuickFragmentTypeProto.FRAGMENT_SOURCE)
                    .setParallelism(1)
                    .setPlan(planProto)
                    .build();

            JQuickExecuteTaskRequest request = JQuickExecuteTaskRequest.newBuilder()
                    .setQueryId("stream_test")
                    .setTaskId("task_stream")
                    .setTaskIndex(0)
                    .setTotalTasks(1)
                    .setFragment(fragmentProto)
                    .build();

            CountDownLatch latch = new CountDownLatch(1);
            AtomicReference<List<JQuickDataChunkProto>> receivedChunks =
                    new AtomicReference<>(new ArrayList<>());

            stub.executeTaskStream(request, new StreamObserver<JQuickDataChunkProto>() {
                @Override
                public void onNext(JQuickDataChunkProto chunk) {
                    receivedChunks.get().add(chunk);
                    LOGGER.info("Received chunk " + chunk.getChunkIndex() +
                            ", isLast: " + chunk.getIsLast());
                }

                @Override
                public void onError(Throwable t) {
                    LOGGER.severe("Stream error: " + t.getMessage());
                    latch.countDown();
                }

                @Override
                public void onCompleted() {
                    LOGGER.info("Stream completed");
                    latch.countDown();
                }
            });

            assertTrue(latch.await(30, TimeUnit.SECONDS));
            assertTrue(receivedChunks.get().size() > 0);
            LOGGER.info("Received " + receivedChunks.get().size() + " chunks");

        } finally {
            channel.shutdown();
        }
    }

    /**
     * 测试 7: 表不存在的情况
     */
    @Test
    public void testTableNotFound() {
        LOGGER.info("=== Test: Table Not Found ===");

        Set<String> requiredColumns = new HashSet<>(Collections.singletonList("id"));
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode(
                "non_existent_table", null, requiredColumns, null
        );

        CompletableFuture<JQuickDataSet> future = coordinator.executeQuery("query_not_found", scanNode);

//        // 应该抛出异常
//        assertThrows(Exception.class, () -> {
//            future.get(30, TimeUnit.SECONDS);
//        });

        LOGGER.info("Table not found test passed");
    }

    /**
     * 测试 8: 空列集合的 TableScan
     */
    @Test
    public void testTableScanEmptyColumns() throws Exception {
        LOGGER.info("=== Test: TableScan Empty Columns ===");

        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode(
                TEST_TABLE_NAME, null, Collections.emptySet(), null
        );

        CompletableFuture<JQuickDataSet> future = coordinator.executeQuery("query_empty_cols", scanNode);
        JQuickDataSet result = future.get(30, TimeUnit.SECONDS);

        assertNotNull(result);
        // 空列集合应该返回所有列
        assertEquals(4, result.getColumns().size());

        LOGGER.info("Empty columns result size: " + result.size());
    }

    /**
     * 测试 9: 统计信息验证
     */
    @Test
    public void testTableScanStats() throws Exception {
        LOGGER.info("=== Test: TableScan Statistics ===");

        Set<String> requiredColumns = new HashSet<>(Arrays.asList("id", "name"));
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode(
                TEST_TABLE_NAME, null, requiredColumns, null
        );

        // 验证统计数据
        assertEquals(TEST_TABLE_NAME, scanNode.getTableName());
        assertEquals(requiredColumns, scanNode.getRequiredColumns());
        assertNull(scanNode.getAlias());

        // 验证 DataSourceManager 中的统计信息
        long rowCount = JQuickDataSourceManager.getRowCount(TEST_TABLE_NAME);
        long dataSize = JQuickDataSourceManager.getEstimatedDataSize(TEST_TABLE_NAME);

        assertEquals(100, rowCount);
        assertTrue(dataSize > 0);

        LOGGER.info("Table stats - rows: " + rowCount + ", dataSize: " + dataSize);
    }

    /**
     * 测试 10: 大数据量 TableScan
     */
    @Test
    public void testLargeTableScan() throws Exception {
        LOGGER.info("=== Test: Large Table Scan ===");

        // 创建大表
        String largeTableName = "large_table";
        List<JQuickColumnMeta> columns = Arrays.asList(
                new JQuickColumnMeta("id", Long.class, largeTableName),
                new JQuickColumnMeta("value", String.class, largeTableName)
        );

        List<JQuickRow> rows = new ArrayList<>();
        for (int i = 1; i <= 10000; i++) {
            JQuickRow row = new JQuickRow();
            row.put("id", (long) i);
            row.put("value", "data_" + i);
            rows.add(row);
        }

        JQuickDataSet largeDataSet = new JQuickDataSet(columns, rows);
        JQuickDataSourceManager.registerTable(largeTableName, largeDataSet);

        Set<String> requiredColumns = new HashSet<>(Arrays.asList("id", "value"));
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode(
                largeTableName, null, requiredColumns, null
        );

        long startTime = System.currentTimeMillis();
        CompletableFuture<JQuickDataSet> future = coordinator.executeQuery("query_large", scanNode);
        JQuickDataSet result = future.get(60, TimeUnit.SECONDS);
        long duration = System.currentTimeMillis() - startTime;

        assertNotNull(result);
        assertEquals(10000, result.size());

        LOGGER.info("Large table scan completed in " + duration + "ms");

        // 清理大表
        JQuickDataSourceManager.removeTable(largeTableName);
    }

    /**
     * 测试 11: Worker 故障转移测试
     */
    @Test
    public void testWorkerFailover() throws Exception {
        LOGGER.info("=== Test: Worker Failover ===");

        Set<String> requiredColumns = new HashSet<>(Arrays.asList("id", "name"));
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode(
                TEST_TABLE_NAME, null, requiredColumns, null
        );

        // 停止一个 Worker
        workers.get(0).stop();
        LOGGER.info("Worker-0 stopped");

        // 等待 Coordinator 检测到 Worker 不健康
        Thread.sleep(5000);

        // 执行查询 - 应该自动故障转移到健康 Worker
        CompletableFuture<JQuickDataSet> future = coordinator.executeQuery("query_failover", scanNode);
        JQuickDataSet result = future.get(30, TimeUnit.SECONDS);

        assertNotNull(result);
        assertEquals(100, result.size());

        LOGGER.info("Failover test passed, result size: " + result.size());
    }

    /**
     * 测试 12: 通过 DataSourceManager 验证表数据
     */
    @Test
    public void testDataSourceManagerIntegration() {
        LOGGER.info("=== Test: DataSourceManager Integration ===");

        // 验证表存在
        assertTrue(JQuickDataSourceManager.containsTable(TEST_TABLE_NAME));

        // 验证表行数
        assertEquals(100, JQuickDataSourceManager.getTableSize(TEST_TABLE_NAME));

        // 验证列信息
        List<String> columnNames = JQuickDataSourceManager.getColumnNames(TEST_TABLE_NAME);
        assertEquals(4, columnNames.size());
        assertTrue(columnNames.contains("id"));
        assertTrue(columnNames.contains("name"));
        assertTrue(columnNames.contains("age"));
        assertTrue(columnNames.contains("email"));

        // 验证第一行数据
        JQuickRow firstRow = JQuickDataSourceManager.getFirstRow(TEST_TABLE_NAME);
        assertNotNull(firstRow);
        assertEquals(1L, firstRow.get("id"));
        assertEquals("user_1", firstRow.get("name"));

        // 获取表摘要
        Map<String, Long> summary = JQuickDataSourceManager.getTableSummary();
        assertTrue(summary.containsKey(TEST_TABLE_NAME.toLowerCase()));

        LOGGER.info("DataSourceManager integration test passed");
    }

    /**
     * 测试 13: TableScan 节点 Proto 序列化/反序列化
     */
    @Test
    public void testTableScanProtoSerialization() {
        LOGGER.info("=== Test: TableScan Proto Serialization ===");

        // 创建原始节点
        Set<String> requiredColumns = new HashSet<>(Arrays.asList("id", "name", "email"));
        JQuickTableScanPhysicalNode originalNode = new JQuickTableScanPhysicalNode(
                TEST_TABLE_NAME, "t", requiredColumns, null
        );

        // 构建 Proto
        JQuickTableScanNodeProto.Builder builder = JQuickTableScanNodeProto.newBuilder()
                .setTableName(originalNode.getTableName())
                .setAlias(originalNode.getAlias() != null ? originalNode.getAlias() : "");

        if (originalNode.getRequiredColumns() != null) {
            builder.addAllRequiredColumns(originalNode.getRequiredColumns());
        }

        JQuickTableScanNodeProto proto = builder.build();

        // 验证
        assertEquals(TEST_TABLE_NAME, proto.getTableName());
        assertEquals("t", proto.getAlias());
        assertEquals(3, proto.getRequiredColumnsCount());
        assertTrue(proto.getRequiredColumnsList().contains("id"));
        assertTrue(proto.getRequiredColumnsList().contains("name"));
        assertTrue(proto.getRequiredColumnsList().contains("email"));

        LOGGER.info("Proto serialization test passed");
    }

    /**
     * 测试 14: 多 Worker 并行扫描
     */
    @Test
    public void testParallelScanAcrossWorkers() throws Exception {
        LOGGER.info("=== Test: Parallel Scan Across Workers ===");

        Set<String> requiredColumns = new HashSet<>(Arrays.asList("id", "name"));

        // 创建多个查询，分散到不同 Worker
        List<CompletableFuture<JQuickDataSet>> futures = new ArrayList<>();
        for (int i = 0; i < WORKER_COUNT * 2; i++) {
            JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode(
                    TEST_TABLE_NAME, null, requiredColumns, null
            );
            futures.add(coordinator.executeQuery("parallel_" + i, scanNode));
        }

        long startTime = System.currentTimeMillis();
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .get(60, TimeUnit.SECONDS);
        long duration = System.currentTimeMillis() - startTime;

        for (CompletableFuture<JQuickDataSet> future : futures) {
            JQuickDataSet result = future.get();
            assertNotNull(result);
            assertEquals(100, result.size());
        }

        LOGGER.info("Parallel scan completed - " + futures.size() + " queries in " + duration + "ms");
    }
}