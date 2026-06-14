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


import com.github.paohaijiao.console.JConsole;
import com.github.paohaijiao.datasource.JQuickDataSourceManager;
import com.github.paohaijiao.distributed.JQuickDistributedPlan;
import com.github.paohaijiao.distributed.coordinator.JQuickCoordinator;
import com.github.paohaijiao.distributed.worker.JQuickDataConverter;
import com.github.paohaijiao.distributed.worker.JQuickExpressionEvaluator;
import com.github.paohaijiao.distributed.worker.JQuickPartitionManager;
import com.github.paohaijiao.distributed.worker.JQuickWorker;
import com.github.paohaijiao.enums.JQuickBinaryOperator;
import com.github.paohaijiao.enums.JQuickExchangeType;
import com.github.paohaijiao.enums.JQuickJoinType;
import com.github.paohaijiao.enums.JQuickPartitionStrategy;
import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.expression.domain.JQuickBinaryExpression;
import com.github.paohaijiao.expression.domain.JQuickColumnRefExpression;
import com.github.paohaijiao.expression.domain.JQuickLiteralExpression;
import com.github.paohaijiao.fragment.JQuickFragmenter;
import com.github.paohaijiao.function.manager.JQuickMethodInvocationManager;
import com.github.paohaijiao.physical.node.*;
import com.github.paohaijiao.proto.*;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.*;
import static org.junit.Assert.assertNotNull;

/**
 * JQuickCoordinator 数据分区发送和接收单元测试
 * <p>
 * 测试覆盖：
 * 1. Coordinator 与 Worker 通信
 * 2. 数据分区（Hash、Range、RoundRobin、Broadcast）
 * 3. 数据发送和接收
 * 4. 多 Worker 并行处理
 * 5. 流式数据传输
 * 6. 任务失败重试
 * 7. 广播表功能
 */
public class JQuickCoordinatorPartitionTest {

    // Worker 端口配置
    private static final int WORKER1_PORT = 19001;

    private static final int WORKER2_PORT = 19002;

    private static final int WORKER3_PORT = 19003;

    private static JConsole console;

    private static JQuickDataConverter dataConverter;
    // 测试数据
    private static JQuickDataSet employeeData;

    private static JQuickDataSet largeDataSet;

    private JQuickWorker worker1;

    private JQuickWorker worker2;

    private JQuickWorker worker3;

    private JQuickCoordinator coordinator;

    private List<JQuickCoordinator.WorkerEndpoint> endpoints;
    private List<JQuickWorker> workers;

    @BeforeClass
    public static void setUpAll() {
        console = JConsole.initConsoleEnvironment();
        dataConverter = new JQuickDataConverter();
        initTestData();
        console.info("测试环境初始化完成");
    }

    /**
     * 初始化测试数据
     */
    private static void initTestData() {
        employeeData = JQuickDataSet.builder()
                .addColumn("id", Integer.class, "employees")
                .addColumn("name", String.class, "employees")
                .addColumn("age", Integer.class, "employees")
                .addColumn("salary", Double.class, "employees")
                .addColumn("city", String.class, "employees")
                .addColumn("dept_id", Integer.class, "employees")
                .addRow(createRow(1, "张三", 25, 8000.0, "北京", 1))
                .addRow(createRow(2, "李四", 32, 15000.0, "上海", 1))
                .addRow(createRow(3, "王五", 28, 10000.0, "北京", 2))
                .addRow(createRow(4, "赵六", 35, 18000.0, "深圳", 2))
                .addRow(createRow(5, "钱七", 29, 12000.0, "上海", 3))
                .addRow(createRow(6, "孙八", 41, 25000.0, "北京", 1))
                .addRow(createRow(7, "周九", 26, 7500.0, "深圳", 3))
                .addRow(createRow(8, "吴十", 33, 14000.0, "广州", 2))
                .addRow(createRow(9, "郑十一", 27, 9000.0, "北京", 1))
                .addRow(createRow(10, "王十二", 31, 11000.0, "上海", 2))
                .build();

        // 大数据集用于性能测试
        JQuickDataSet.Builder largeBuilder = JQuickDataSet.builder()
                .addColumn("id", Integer.class, "large")
                .addColumn("value", Integer.class, "large");
        for (int i = 1; i <= 1000; i++) {
            largeBuilder.addRow(createLargeRow(i, i * 10));
        }
        largeDataSet = largeBuilder.build();
    }

    private static JQuickRow createRow(Integer id, String name, Integer age, Double salary, String city, Integer deptId) {
        JQuickRow row = new JQuickRow();
        row.put("id", id);
        row.put("name", name);
        row.put("age", age);
        row.put("salary", salary);
        row.put("city", city);
        row.put("dept_id", deptId);
        return row;
    }

    private static JQuickRow createLargeRow(Integer id, Integer value) {
        JQuickRow row = new JQuickRow();
        row.put("id", id);
        row.put("value", value);
        return row;
    }

    @Before
    public void setUp() throws IOException, InterruptedException {
        // 清理数据源
        JQuickDataSourceManager.clearAll();
        // 创建 Worker 列表
        workers = new ArrayList<>();
        worker1 = new JQuickWorker("worker-1", WORKER1_PORT);
        worker2 = new JQuickWorker("worker-2", WORKER2_PORT);
        worker3 = new JQuickWorker("worker-3", WORKER3_PORT);
        workers.add(worker1);
        workers.add(worker2);
        workers.add(worker3);
        worker1.start();
        worker2.start();
        worker3.start();
        Thread.sleep(1000);
        // 清理 Worker 的数据缓存
        worker1.clearReceivedDataCache();
        worker2.clearReceivedDataCache();
        worker3.clearReceivedDataCache();
        endpoints = new ArrayList<>();
        endpoints.add(new JQuickCoordinator.WorkerEndpoint("worker-1", "localhost", WORKER1_PORT, 0));
        endpoints.add(new JQuickCoordinator.WorkerEndpoint("worker-2", "localhost", WORKER2_PORT, 1));
        endpoints.add(new JQuickCoordinator.WorkerEndpoint("worker-3", "localhost", WORKER3_PORT, 2));
        coordinator = new JQuickCoordinator("coordinator-1", endpoints, 30000, 3, 1000);
        for (JQuickWorker worker : workers) {
            worker.setWorkerEndpoints(endpoints);
        }
        console.info("测试环境启动完成");
    }

    @After
    public void tearDown() {
        if (coordinator != null) {
            coordinator.shutdown();
        }
        if (worker1 != null) {
            worker1.stop();
        }
        if (worker2 != null) {
            worker2.stop();
        }
        if (worker3 != null) {
            worker3.stop();
        }
        JQuickDataSourceManager.clearAll();
        console.info("测试环境清理完成");
    }

    @Test
    public void testHashPartition() throws Exception {
        console.info("=== 测试 Hash 分区 ===");
        // 在测试开始时清理缓存，确保每次执行都是干净的
        worker1.clearReceivedDataCache();
        worker2.clearReceivedDataCache();
        worker3.clearReceivedDataCache();
        // 注册表到 Coordinator
        coordinator.broadcastTable("employees", employeeData, true).get(30, TimeUnit.SECONDS);
        // 创建 Hash 分区 Exchange 节点
        List<JQuickExpression> partitionKeys = new ArrayList<>();
        partitionKeys.add(new JQuickColumnRefExpression("city"));
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employees", null, null, null);
        JQuickExchangePhysicalNode exchangeNode = new JQuickExchangePhysicalNode(JQuickExchangeType.SHUFFLE, JQuickPartitionStrategy.HASH, partitionKeys, 3, scanNode);
        // 包装 GATHER Exchange 来收集分区后的数据
        JQuickExchangePhysicalNode gatherNode = new JQuickExchangePhysicalNode(JQuickExchangeType.GATHER, JQuickPartitionStrategy.REPLICATE, null, 1, exchangeNode);
        // 创建物理计划
        JQuickFragmenter fragmenter = new JQuickFragmenter(3);
        JQuickDistributedPlan plan = fragmenter.fragment(gatherNode);
        // 执行查询（使用创建好的计划，而不是让 Coordinator 重新切分）
        String queryId = "hash_partition_" + System.currentTimeMillis();
        JQuickDataSet result = coordinator.executeQueryWithPlan(queryId, plan);
        result.printTable();
        assertEquals(10l, result.size());
        assertNotNull(result);
        console.info("Hash 分区测试完成，结果行数: " + result.size());
    }

    @Test
    public void testRangePartition() throws Exception {
        console.info("=== 测试 Range 分区 ===");
        coordinator.broadcastTable("employees", employeeData, true).get(30, TimeUnit.SECONDS);
        // 创建 Range 分区 Exchange 节点（按 age 范围分区）
        List<JQuickExpression> partitionKeys = new ArrayList<>();
        partitionKeys.add(new JQuickColumnRefExpression("age"));
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employees", null, null, null);
        JQuickExchangePhysicalNode exchangeNode = new JQuickExchangePhysicalNode(JQuickExchangeType.SHUFFLE, JQuickPartitionStrategy.RANGE, partitionKeys, 3, scanNode);
        // 包装 GATHER Exchange 来收集分区后的数据
        JQuickExchangePhysicalNode gatherNode = new JQuickExchangePhysicalNode(JQuickExchangeType.GATHER, JQuickPartitionStrategy.REPLICATE, null, 1, exchangeNode);
        // 执行查询
        String queryId = "range_partition_" + System.currentTimeMillis();
        JQuickDataSet result = coordinator.executeQuery(queryId, gatherNode);
        result.printTable();
        assertNotNull(result);
        console.info("Range 分区测试完成，结果行数: " + result.size());
    }

    @Test
    public void testRoundRobinPartition() throws Exception {
        console.info("=== 测试 RoundRobin 分区 ===");
        coordinator.broadcastTable("employees", employeeData, true).get(30, TimeUnit.SECONDS);
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employees", null, null, null);
        JQuickExchangePhysicalNode exchangeNode = new JQuickExchangePhysicalNode(JQuickExchangeType.SHUFFLE, JQuickPartitionStrategy.ROUND_ROBIN, null, 3, scanNode);
        // 包装 GATHER Exchange 来收集分区后的数据
        JQuickExchangePhysicalNode gatherNode = new JQuickExchangePhysicalNode(JQuickExchangeType.GATHER, JQuickPartitionStrategy.REPLICATE, null, 1, exchangeNode);
        String queryId = "roundrobin_" + System.currentTimeMillis();
        JQuickDataSet result = coordinator.executeQuery(queryId, gatherNode);
        assertNotNull(result);
        console.info("RoundRobin 分区测试完成，结果行数: " + result.size());
    }

    @Test
    public void testBroadcastPartition() throws Exception {
        console.info("=== 测试 Broadcast 分区 ===");
        coordinator.broadcastTable("employees", employeeData, true).get(30, TimeUnit.SECONDS);
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employees", null, null, null);
        JQuickExchangePhysicalNode exchangeNode = new JQuickExchangePhysicalNode(JQuickExchangeType.BROADCAST, JQuickPartitionStrategy.REPLICATE, null, 3, scanNode);
        // 包装 GATHER Exchange 来收集分区后的数据
        JQuickExchangePhysicalNode gatherNode = new JQuickExchangePhysicalNode(JQuickExchangeType.GATHER, JQuickPartitionStrategy.REPLICATE, null, 1, exchangeNode);
        String queryId = "broadcast_" + System.currentTimeMillis();
        JQuickDataSet result = coordinator.executeQuery(queryId, gatherNode);
        assertNotNull(result);
        console.info("Broadcast 分区测试完成，结果行数: " + result.size());
    }


    @Test
    public void testSendDataToWorker() throws Exception {
        console.info("=== 测试发送数据到 Worker ===");
        // 创建测试分区
        JQuickWorker.JQuickMemoryPartition partition = new JQuickWorker.JQuickMemoryPartition(0, 3);
        partition.setData(employeeData);
        // 获取目标 Worker 端点
        JQuickCoordinator.WorkerEndpoint targetEndpoint = endpoints.get(0);
        // 发送数据
        CompletableFuture<Void> sendFuture = new CompletableFuture<>();
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", WORKER1_PORT).usePlaintext().build();
        JQuickDataDistributionServiceGrpc.JQuickDataDistributionServiceStub stub = JQuickDataDistributionServiceGrpc.newStub(channel);
        JQuickDataChunkProto chunk = JQuickDataChunkProto.newBuilder()
                .setPartitionId(partition.getPartitionId())
                .setData(dataConverter.convertToProto(employeeData))
                .setChunkIndex(0)
                .setIsLast(true)
                .setSequenceId(System.currentTimeMillis())
                .build();
        stub.sendData(new StreamObserver<JQuickEmptyNodeProto>() {
            @Override
            public void onNext(JQuickEmptyNodeProto value) {
            }

            @Override
            public void onError(Throwable t) {
                sendFuture.completeExceptionally(t);
                channel.shutdown();
            }

            @Override
            public void onCompleted() {
                sendFuture.complete(null);
                channel.shutdown();
            }
        }).onNext(chunk);
        sendFuture.get(30, TimeUnit.SECONDS);
        console.info("数据发送成功");
    }

    @Test
    public void testReceiveDataFromWorker() throws Exception {
        console.info("=== 测试从 Worker 接收数据 ===");
        // 先在 Worker1 上注册表
        JQuickDataSourceManager.registerTable("test_receive", employeeData);
        // 从 Worker1 拉取数据
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", WORKER1_PORT)
                .usePlaintext()
                .build();
        JQuickDataDistributionServiceGrpc.JQuickDataDistributionServiceBlockingStub stub = JQuickDataDistributionServiceGrpc.newBlockingStub(channel);
        JQuickFetchDataRequest request = JQuickFetchDataRequest.newBuilder()
                .setPartitionId("0_1")
                .setChunkIndex(0)
                .setStreaming(false)
                .build();
        java.util.Iterator<JQuickFetchDataResponse> response = stub.receiveData(request);
        while (response.hasNext()) {
            assertNotNull(response);
            console.info("数据接收成功，数据大小: " + response.next().getDataSizeBytes());
        }
        channel.shutdown();
    }

    @Test
    public void testStreamingDataTransfer() throws Exception {
        console.info("=== 测试流式数据传输 ===");
        // 创建大数据集（1000行）
        JQuickDataSet largeData = largeDataSet;
        // 流式发送
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", WORKER1_PORT)
                .usePlaintext()
                .build();
        JQuickDataDistributionServiceGrpc.JQuickDataDistributionServiceStub stub = JQuickDataDistributionServiceGrpc.newStub(channel);
        AtomicInteger receivedChunks = new AtomicInteger(0);
        CompletableFuture<Void> receiveComplete = new CompletableFuture<>();
        stub.sendData(new StreamObserver<JQuickEmptyNodeProto>() {
            @Override
            public void onNext(JQuickEmptyNodeProto value) {
            }
            @Override
            public void onError(Throwable t) {
                receiveComplete.completeExceptionally(t);
            }

            @Override
            public void onCompleted() {
                receiveComplete.complete(null);
                channel.shutdown();
            }
        }).onNext(createChunk(largeData, 0, true));
        receiveComplete.get(30, TimeUnit.SECONDS);
        console.info("流式传输完成，接收块数: " + receivedChunks.get());
    }

    @Test
    public void testParallelProcessingOnMultipleWorkers() throws Exception {
        console.info("=== 测试多 Worker 并行处理 ===");
        coordinator.broadcastTable("employees", employeeData, true).get(30, TimeUnit.SECONDS);
        // 创建需要并行处理的聚合查询
        List<JQuickExpression> groupKeys = new ArrayList<>();
        groupKeys.add(new JQuickColumnRefExpression("city"));
        List<JQuickHashAggregatePhysicalNode.AggregateFunction> aggregates = new ArrayList<>();
        aggregates.add(new JQuickHashAggregatePhysicalNode.AggregateFunction("count", null, false, "emp_count"));
        aggregates.add(new JQuickHashAggregatePhysicalNode.AggregateFunction("avg", new JQuickColumnRefExpression("salary"), false, "avg_salary"));
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employees", null, null, null);
        JQuickHashAggregatePhysicalNode aggNode = new JQuickHashAggregatePhysicalNode(groupKeys, aggregates, scanNode, null, JQuickHashAggregatePhysicalNode.AggregateStage.FINAL);
        // 执行查询
        String queryId = "parallel_" + System.currentTimeMillis();
        JQuickDataSet result = coordinator.executeQuery(queryId, aggNode);
        assertNotNull(result);
        assertTrue(result.size() > 0);

        console.info("并行处理完成，结果行数: " + result.size());
    }

    @Test
    public void testGatherFromMultipleWorkers() throws Exception {
        console.info("=== 测试从多个 Worker 收集结果 ===");

        coordinator.broadcastTable("employees", employeeData, true).get(30, TimeUnit.SECONDS);

        // 创建 Exchange 节点将数据分发到多个 Worker，然后收集
        List<JQuickExpression> partitionKeys = new ArrayList<>();
        partitionKeys.add(new JQuickColumnRefExpression("city"));

        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode(
                "employees", null, null, null);

        JQuickExchangePhysicalNode exchangeNode = new JQuickExchangePhysicalNode(
                JQuickExchangeType.SHUFFLE, JQuickPartitionStrategy.HASH,
                partitionKeys, 3, scanNode);

        // 执行查询
        String queryId = "gather_" + System.currentTimeMillis();
        JQuickDataSet future = coordinator.executeQuery(queryId, exchangeNode);

    }


    @Test
    public void testPartitionManagerHashPartitioning() {
        console.info("=== 测试分区管理器 Hash 分区 ===");

        JQuickPartitionManager partitionManager = new JQuickPartitionManager();
        partitionManager.setWorkerEndpoints(endpoints);

        JQuickMethodInvocationManager functionManager = JQuickMethodInvocationManager.getInstance();
        JQuickExpressionEvaluator evaluator = new JQuickExpressionEvaluator(functionManager);

        List<JQuickExpression> partitionKeys = new ArrayList<>();
        partitionKeys.add(new JQuickColumnRefExpression("city"));

        JQuickExchangePhysicalNode exchangeNode = new JQuickExchangePhysicalNode(
                JQuickExchangeType.SHUFFLE, JQuickPartitionStrategy.HASH,
                partitionKeys, 3, null);

        List<JQuickWorker.JQuickMemoryPartition> partitions =
                partitionManager.partitionData(employeeData, exchangeNode, evaluator, 3);

        assertEquals(3, partitions.size());

        // 验证分区数据
        int totalRows = 0;
        for (JQuickWorker.JQuickMemoryPartition partition : partitions) {
            totalRows += partition.getData().size();
        }
        assertEquals(employeeData.size(), totalRows);

        console.info("Hash 分区完成，分区数: " + partitions.size());
    }

    @Test
    public void testPartitionManagerRoundRobinPartitioning() {
        console.info("=== 测试分区管理器 RoundRobin 分区 ===");

        JQuickPartitionManager partitionManager = new JQuickPartitionManager();
        partitionManager.setWorkerEndpoints(endpoints);

        JQuickMethodInvocationManager functionManager = JQuickMethodInvocationManager.getInstance();
        JQuickExpressionEvaluator evaluator = new JQuickExpressionEvaluator(functionManager);

        JQuickExchangePhysicalNode exchangeNode = new JQuickExchangePhysicalNode(
                JQuickExchangeType.SHUFFLE, JQuickPartitionStrategy.ROUND_ROBIN,
                null, 3, null);

        List<JQuickWorker.JQuickMemoryPartition> partitions =
                partitionManager.partitionData(employeeData, exchangeNode, evaluator, 3);

        assertEquals(3, partitions.size());

        // RoundRobin 应该均匀分布
        int[] sizes = new int[3];
        for (int i = 0; i < 3; i++) {
            sizes[i] = partitions.get(i).getData().size();
        }

        // 验证分布基本均匀（相差不超过1）
        int maxSize = Arrays.stream(sizes).max().getAsInt();
        int minSize = Arrays.stream(sizes).min().getAsInt();
        assertTrue(maxSize - minSize <= 1);

        console.info("RoundRobin 分区完成，分区大小: " + Arrays.toString(sizes));
    }

    @Test
    public void testPartitionManagerBroadcastPartitioning() {
        console.info("=== 测试分区管理器 Broadcast 分区 ===");

        JQuickPartitionManager partitionManager = new JQuickPartitionManager();
        partitionManager.setWorkerEndpoints(endpoints);

        JQuickMethodInvocationManager functionManager = JQuickMethodInvocationManager.getInstance();
        JQuickExpressionEvaluator evaluator = new JQuickExpressionEvaluator(functionManager);

        JQuickExchangePhysicalNode exchangeNode = new JQuickExchangePhysicalNode(
                JQuickExchangeType.BROADCAST, JQuickPartitionStrategy.REPLICATE,
                null, 3, null);

        List<JQuickWorker.JQuickMemoryPartition> partitions =
                partitionManager.partitionData(employeeData, exchangeNode, evaluator, 3);

        assertEquals(3, partitions.size());

        // Broadcast 模式下，每个分区都应该包含全部数据
        for (JQuickWorker.JQuickMemoryPartition partition : partitions) {
            assertEquals(employeeData.size(), partition.getData().size());
        }

        console.info("Broadcast 分区完成，每个分区都有 " + employeeData.size() + " 行");
    }


    @Test
    public void testBroadcastTableToAllWorkers() throws Exception {
        console.info("=== 测试广播表到所有 Worker ===");

        JQuickDataSet broadcastData = JQuickDataSet.builder().build();
//                .addColumn("config_key", String.class, "config")
//                .addColumn("config_value", String.class, "config")
//                .addRow(new JQuickRow().put("config_key", "app.name").put("config_value", "JQuick"))
//                .addRow(new JQuickRow().put("config_key", "version").put("config_value", "1.0.0"))
//                .build();

        CompletableFuture<Void> future = coordinator.broadcastTable("config_table", broadcastData, true);
        future.get(30, TimeUnit.SECONDS);

        console.info("广播表完成");
    }

    @Test
    public void testBroadcastMultipleTables() throws Exception {
        console.info("=== 测试广播多个表 ===");

        Map<String, JQuickDataSet> tables = new HashMap<>();
        tables.put("table_a", employeeData);
        tables.put("table_b", largeDataSet);

        CompletableFuture<Void> future = coordinator.broadcastTables(tables, true);
        future.get(30, TimeUnit.SECONDS);

        console.info("批量广播表完成");
    }

    //任务调度和重试测试

    @Test
    public void testTaskRetryOnFailure() throws Exception {
        console.info("=== 测试任务失败重试 ===");

        coordinator.broadcastTable("employees", employeeData, true).get(30, TimeUnit.SECONDS);

        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode(
                "employees", null, null, null);

        // 执行查询（Coordinator 会自动处理重试）
        String queryId = "retry_" + System.currentTimeMillis();
        JQuickDataSet future = coordinator.executeQuery(queryId, scanNode);
    }

    @Test
    public void testQueryCancellation() throws Exception {
        console.info("=== 测试查询取消 ===");

        coordinator.broadcastTable("employees", employeeData, true).get(30, TimeUnit.SECONDS);

        // 创建一个可能会执行较长时间的查询
        List<JQuickExpression> groupKeys = new ArrayList<>();
        groupKeys.add(new JQuickColumnRefExpression("city"));

        List<JQuickHashAggregatePhysicalNode.AggregateFunction> aggregates = new ArrayList<>();
        aggregates.add(new JQuickHashAggregatePhysicalNode.AggregateFunction(
                "count", null, false, "cnt"));

        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode(
                "employees", null, null, null);

        JQuickHashAggregatePhysicalNode aggNode = new JQuickHashAggregatePhysicalNode(
                groupKeys, aggregates, scanNode, null,
                JQuickHashAggregatePhysicalNode.AggregateStage.FINAL);

        String queryId = "cancel_" + System.currentTimeMillis();

        // 异步执行查询
        JQuickDataSet future = coordinator.executeQuery(queryId, aggNode);

        // 等待一小段时间后取消
        Thread.sleep(100);
        CompletableFuture<Boolean> cancelFuture = coordinator.cancelQuery(queryId, "测试取消");

        Boolean cancelled = cancelFuture.get(10, TimeUnit.SECONDS);

        // 查询应该被取消
        assertTrue(cancelled);
        console.info("查询取消测试完成");
    }

    @Test
    public void testEndToEndQueryWithPartitioning() throws Exception {
        console.info("=== 测试端到端查询与分区 ===");

        coordinator.broadcastTable("employees", employeeData, true).get(30, TimeUnit.SECONDS);

        // 构建完整查询计划：扫描 -> 过滤 -> 聚合
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode(
                "employees", null, null, null);

        // 过滤：年龄大于28
        JQuickExpression filterPredicate = new JQuickBinaryExpression(
                new JQuickColumnRefExpression("age"),
                new JQuickLiteralExpression(28),
                JQuickBinaryOperator.GT);

        JQuickFilterPhysicalNode filterNode = new JQuickFilterPhysicalNode(filterPredicate, scanNode);

        // 按城市分组聚合
        List<JQuickExpression> groupKeys = new ArrayList<>();
        groupKeys.add(new JQuickColumnRefExpression("city"));

        List<JQuickHashAggregatePhysicalNode.AggregateFunction> aggregates = new ArrayList<>();
        aggregates.add(new JQuickHashAggregatePhysicalNode.AggregateFunction(
                "count", null, false, "emp_count"));
        aggregates.add(new JQuickHashAggregatePhysicalNode.AggregateFunction(
                "avg", new JQuickColumnRefExpression("salary"), false, "avg_salary"));

        JQuickHashAggregatePhysicalNode aggNode = new JQuickHashAggregatePhysicalNode(
                groupKeys, aggregates, filterNode, null,
                JQuickHashAggregatePhysicalNode.AggregateStage.FINAL);

        // 执行查询
        String queryId = "e2e_" + System.currentTimeMillis();
        JQuickDataSet future = coordinator.executeQuery(queryId, aggNode);

    }

    @Test
    public void testJoinQueryWithPartitioning() throws Exception {
        console.info("=== 测试 Join 查询与分区 ===");
        JQuickRow row1 = new JQuickRow();
        row1.put("dept_id", 1);
        row1.put("dept_name", "技术部");

        JQuickRow row2 = new JQuickRow();
        row2.put("dept_id", 2);
        row2.put("dept_name", "销售部");

        JQuickRow row3 = new JQuickRow();
        row3.put("dept_id", 3);
        row3.put("dept_name", "市场部");

        JQuickDataSet deptData = JQuickDataSet.builder()
                .addColumn("dept_id", Integer.class, "departments")
                .addColumn("dept_name", String.class, "departments")
                .addRow(row1)
                .addRow(row2)
                .addRow(row3)
                .build();

        coordinator.broadcastTable("employees", employeeData, true).get(30, TimeUnit.SECONDS);
        coordinator.broadcastTable("departments", deptData, true).get(30, TimeUnit.SECONDS);

        // 创建 Join 节点
        JQuickTableScanPhysicalNode empScan = new JQuickTableScanPhysicalNode(
                "employees", "e", null, null);
        JQuickTableScanPhysicalNode deptScan = new JQuickTableScanPhysicalNode(
                "departments", "d", null, null);

        List<JQuickHashJoinPhysicalNode.JoinKeyPair> joinKeys = new ArrayList<>();
        joinKeys.add(new JQuickHashJoinPhysicalNode.JoinKeyPair(
                new JQuickColumnRefExpression("dept_id"),
                new JQuickColumnRefExpression("dept_id")));

        JQuickHashJoinPhysicalNode joinNode = new JQuickHashJoinPhysicalNode(
                JQuickJoinType.INNER, empScan, deptScan, null, joinKeys,
                JQuickHashJoinPhysicalNode.BuildSide.LEFT,
                JQuickHashJoinPhysicalNode.JoinDistribution.SHUFFLE_HASH);

        // 执行查询
        String queryId = "join_" + System.currentTimeMillis();
        JQuickDataSet future = coordinator.executeQuery(queryId, joinNode);

    }


    @Test
    public void testLargeDataPartitionPerformance() throws Exception {
        console.info("=== 测试大数据分区性能 ===");

        // 创建大数据集（10000行）
        JQuickDataSet.Builder largeBuilder = JQuickDataSet.builder()
                .addColumn("id", Integer.class, "large")
                .addColumn("category", Integer.class, "large")
                .addColumn("value", Double.class, "large");

        Random random = new Random(42);
        for (int i = 1; i <= 10000; i++) {
            JQuickRow row = new JQuickRow();
            row.put("id", i);
            row.put("category", random.nextInt(10));
            row.put("value", random.nextDouble() * 1000);
            largeBuilder.addRow(row);
        }
        JQuickDataSet largeData = largeBuilder.build();

        long startTime = System.currentTimeMillis();

        coordinator.broadcastTable("large_table", largeData, true).get(60, TimeUnit.SECONDS);

        // 创建分区查询
        List<JQuickExpression> partitionKeys = new ArrayList<>();
        partitionKeys.add(new JQuickColumnRefExpression("category"));

        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode(
                "large_table", null, null, null);

        JQuickExchangePhysicalNode exchangeNode = new JQuickExchangePhysicalNode(
                JQuickExchangeType.SHUFFLE, JQuickPartitionStrategy.HASH,
                partitionKeys, 3, scanNode);

        String queryId = "perf_" + System.currentTimeMillis();
        JQuickDataSet future = coordinator.executeQuery(queryId, exchangeNode);

        long duration = System.currentTimeMillis() - startTime;
        console.info("大数据分区测试完成，耗时: " + duration + "ms，数据量: " + largeData.size() + "行");
    }

    //边界条件测试

    @Test
    public void testEmptyDataPartition() throws Exception {
        console.info("=== 测试空数据分区 ===");

        JQuickDataSet emptyData = JQuickDataSet.builder().build();
        coordinator.broadcastTable("empty_table", emptyData, true).get(30, TimeUnit.SECONDS);

        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode(
                "empty_table", null, null, null);

        String queryId = "empty_" + System.currentTimeMillis();
        JQuickDataSet future = coordinator.executeQuery(queryId, scanNode);

        console.info("空数据分区测试完成");
    }

    @Test
    public void testSingleRowDataPartition() throws Exception {
        console.info("=== 测试单行数据分区 ===");
        JQuickRow row = new JQuickRow();
        row.put("id", 1);
        row.put("name", "测试");
        JQuickDataSet singleRow = JQuickDataSet.builder()
                .addColumn("id", Integer.class, "single")
                .addColumn("name", String.class, "single")
                .addRow(row)
                .build();

        coordinator.broadcastTable("single_table", singleRow, true).get(30, TimeUnit.SECONDS);

        List<JQuickExpression> partitionKeys = new ArrayList<>();
        partitionKeys.add(new JQuickColumnRefExpression("id"));

        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode(
                "single_table", null, null, null);

        JQuickExchangePhysicalNode exchangeNode = new JQuickExchangePhysicalNode(
                JQuickExchangeType.SHUFFLE, JQuickPartitionStrategy.HASH,
                partitionKeys, 3, scanNode);

        String queryId = "single_" + System.currentTimeMillis();
        JQuickDataSet future = coordinator.executeQuery(queryId, exchangeNode);
        console.info("单行数据分区测试完成");
    }


    private JQuickDataChunkProto createChunk(JQuickDataSet data, int chunkIndex, boolean isLast) {
        return JQuickDataChunkProto.newBuilder()
                .setPartitionId("test_partition")
                .setData(dataConverter.convertToProto(data))
                .setChunkIndex(chunkIndex)
                .setIsLast(isLast)
                .setSequenceId(System.currentTimeMillis())
                .setOriginalSize(data.size() * 100L)
                .build();
    }
}
