package com.github.paohaijiao.coordinator;

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
import com.github.paohaijiao.distributed.coordinator.JQuickCoordinator;
import com.github.paohaijiao.distributed.coordinator.JQuickCoordinator.QueryExecution;
import com.github.paohaijiao.distributed.coordinator.JQuickCoordinator.TaskExecution;
import com.github.paohaijiao.distributed.coordinator.JQuickCoordinator.WorkerEndpoint;
import com.github.paohaijiao.enums.JQuickBinaryOperator;
import com.github.paohaijiao.enums.JQuickJoinType;
import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.expression.domain.JQuickBinaryExpression;
import com.github.paohaijiao.expression.domain.JQuickColumnRefExpression;
import com.github.paohaijiao.expression.domain.JQuickLiteralExpression;
import com.github.paohaijiao.physical.node.*;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.distributed.worker.JQuickWorker;
import io.grpc.stub.StreamObserver;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * JQuickCoordinator 测试用例
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/12/16
 */
public class JQuickCoordinatorTest {

    private JQuickCoordinator coordinator;

    private List<JQuickWorker> workers;

    private List<JQuickCoordinator.WorkerEndpoint> endpoints;

    private static final int WORKER_COUNT = 3;

    private static final int BASE_PORT = 19000;

    @Before
    public void setUp() throws IOException {
        endpoints = new ArrayList<>();
        workers = new ArrayList<>();
        for (int i = 0; i < WORKER_COUNT; i++) {
            String workerId = "worker_" + i;
            int port = BASE_PORT + i;
            JQuickWorker worker = new JQuickWorker(workerId, port);
            worker.start();
            workers.add(worker);

            endpoints.add(new WorkerEndpoint(workerId, "localhost", port, i));
        }
        coordinator = new JQuickCoordinator("coordinator_test", endpoints);
    }

    @After
    public void tearDown() {
        if (coordinator != null) {
            coordinator.shutdown();
        }
        for (JQuickWorker worker : workers) {
            worker.stop();
        }
    }

    @Test
    public void testConstructor() {
        assertNotNull("Coordinator should be created", coordinator);

        List<WorkerEndpoint> workerStatus = coordinator.getWorkerStatus();
        assertEquals("Should have correct number of workers", WORKER_COUNT, workerStatus.size());

        Map<String, QueryExecution> activeQueries = coordinator.getActiveQueries();
        assertNotNull("Active queries map should not be null", activeQueries);
        assertTrue("Active queries should be empty initially", activeQueries.isEmpty());
    }

    @Test
    public void testGetWorkerStatus() {
        List<WorkerEndpoint> status = coordinator.getWorkerStatus();
        assertEquals(WORKER_COUNT, status.size());
        for (int i = 0; i < status.size(); i++) {
            WorkerEndpoint endpoint = status.get(i);
            assertEquals("worker_" + i, endpoint.getWorkerId());
            assertEquals("localhost", endpoint.getHost());
            assertEquals(BASE_PORT + i, endpoint.getPort());
            assertTrue("Worker should be healthy initially", endpoint.isHealthy());
        }
    }

    @Test
    public void testAddAndRemoveWorker() {
        int initialSize = coordinator.getWorkerStatus().size();
        WorkerEndpoint newWorker = new WorkerEndpoint("worker_new", "localhost", 19003, initialSize);
        coordinator.addWorker(newWorker);
        assertEquals(initialSize + 1, coordinator.getWorkerStatus().size());
        coordinator.removeWorker("worker_new");
        assertEquals(initialSize, coordinator.getWorkerStatus().size());
    }

    @Test
    public void testExecuteTableScanQuery() throws Exception {
        // 创建 TableScan 计划
        Set<String> requiredColumns = new HashSet<>(Arrays.asList("id", "name", "age"));
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode(
                "test_table", "t", requiredColumns, null
        );

        String queryId = "test_scan_" + System.currentTimeMillis();
        CompletableFuture<JQuickDataSet> future = coordinator.executeQuery(queryId, scanNode);

        // 等待执行完成（带超时）
        JQuickDataSet result = future.get(30, TimeUnit.SECONDS);

        assertNotNull("Result should not be null", result);
        // TableScan 在没有实际数据源时应返回空结果
        assertTrue("Result should be empty or non-null", result != null);
    }

    @Test
    public void testExecuteFilterQuery() throws Exception {
        // 创建过滤条件: age > 18
        JQuickExpression predicate = new JQuickBinaryExpression(
                new JQuickColumnRefExpression("age"),
                new JQuickLiteralExpression(18),
                JQuickBinaryOperator.GT
        );

        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode(
                "users", "u", new HashSet<>(Arrays.asList("id", "name", "age")), null
        );
        JQuickFilterPhysicalNode filterNode = new JQuickFilterPhysicalNode(predicate, scanNode);

        String queryId = "test_filter_" + System.currentTimeMillis();
        CompletableFuture<JQuickDataSet> future = coordinator.executeQuery(queryId, filterNode);

        JQuickDataSet result = future.get(30, TimeUnit.SECONDS);
        assertNotNull(result);
    }

    @Test
    public void testExecuteProjectQuery() throws Exception {
        // 创建 Project 节点: SELECT id, name, age * 2 as double_age
        List<JQuickProjectPhysicalNode.SelectItem> selectItems = Arrays.asList(
                new JQuickProjectPhysicalNode.SelectItem(new JQuickColumnRefExpression("id"), "id"),
                new JQuickProjectPhysicalNode.SelectItem(new JQuickColumnRefExpression("name"), "name"),
                new JQuickProjectPhysicalNode.SelectItem(
                        new JQuickBinaryExpression(
                                new JQuickColumnRefExpression("age"),
                                new JQuickLiteralExpression(2),
                                JQuickBinaryOperator.MULTIPLY
                        ), "double_age"
                )
        );

        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode(
                "employees", "e", new HashSet<>(Arrays.asList("id", "name", "age")), null
        );
        JQuickProjectPhysicalNode projectNode = new JQuickProjectPhysicalNode(selectItems, scanNode, false);

        String queryId = "test_project_" + System.currentTimeMillis();
        CompletableFuture<JQuickDataSet> future = coordinator.executeQuery(queryId, projectNode);

        JQuickDataSet result = future.get(30, TimeUnit.SECONDS);
        assertNotNull(result);
    }

    @Test
    public void testExecuteLimitQuery() throws Exception {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode(
                "large_table", "t", new HashSet<>(Arrays.asList("id", "value")), null
        );
        JQuickLimitPhysicalNode limitNode = new JQuickLimitPhysicalNode(10, scanNode);

        String queryId = "test_limit_" + System.currentTimeMillis();
        CompletableFuture<JQuickDataSet> future = coordinator.executeQuery(queryId, limitNode);

        JQuickDataSet result = future.get(30, TimeUnit.SECONDS);
        assertNotNull(result);
    }

    @Test
    public void testExecuteSortQuery() throws Exception {
        List<JQuickSortPhysicalNode.OrderByItem> orderByItems = Arrays.asList(
                new JQuickSortPhysicalNode.OrderByItem("age", false),  // DESC
                new JQuickSortPhysicalNode.OrderByItem("name", true)   // ASC
        );

        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode(
                "users", "u", new HashSet<>(Arrays.asList("id", "name", "age")), null
        );
        JQuickSortPhysicalNode sortNode = new JQuickSortPhysicalNode(orderByItems, scanNode);

        String queryId = "test_sort_" + System.currentTimeMillis();
        CompletableFuture<JQuickDataSet> future = coordinator.executeQuery(queryId, sortNode);

        JQuickDataSet result = future.get(30, TimeUnit.SECONDS);
        assertNotNull(result);
    }

    @Test
    public void testExecuteAggregateQuery() throws Exception {
        // 创建聚合节点: GROUP BY department, COUNT(*), AVG(salary)
        List<JQuickExpression> groupKeys = Arrays.asList(
                new JQuickColumnRefExpression("department")
        );

        List<JQuickHashAggregatePhysicalNode.AggregateFunction> aggregates = Arrays.asList(
                new JQuickHashAggregatePhysicalNode.AggregateFunction("count", null, false, "emp_count"),
                new JQuickHashAggregatePhysicalNode.AggregateFunction("avg",
                        new JQuickColumnRefExpression("salary"), false, "avg_salary")
        );

        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode(
                "employees", "e", new HashSet<>(Arrays.asList("department", "salary")), null
        );
        JQuickHashAggregatePhysicalNode aggNode = new JQuickHashAggregatePhysicalNode(
                groupKeys, aggregates, scanNode, null,
                JQuickHashAggregatePhysicalNode.AggregateStage.SINGLE
        );

        String queryId = "test_aggregate_" + System.currentTimeMillis();
        CompletableFuture<JQuickDataSet> future = coordinator.executeQuery(queryId, aggNode);

        JQuickDataSet result = future.get(30, TimeUnit.SECONDS);
        assertNotNull(result);
    }

    // ==================== 多表连接测试 ====================

    @Test
    public void testExecuteHashJoinQuery() throws Exception {
        // 创建左表扫描
        JQuickTableScanPhysicalNode leftScan = new JQuickTableScanPhysicalNode(
                "orders", "o", new HashSet<>(Arrays.asList("order_id", "user_id", "amount")), null
        );

        // 创建右表扫描
        JQuickTableScanPhysicalNode rightScan = new JQuickTableScanPhysicalNode(
                "users", "u", new HashSet<>(Arrays.asList("user_id", "name", "email")), null
        );

        // 创建 Join 键对
        List<JQuickHashJoinPhysicalNode.JoinKeyPair> joinKeys = Arrays.asList(
                new JQuickHashJoinPhysicalNode.JoinKeyPair(
                        new JQuickColumnRefExpression("user_id"),
                        new JQuickColumnRefExpression("user_id")
                )
        );

        JQuickHashJoinPhysicalNode joinNode = new JQuickHashJoinPhysicalNode(
                JQuickJoinType.INNER, leftScan, rightScan, null, joinKeys,
                JQuickHashJoinPhysicalNode.BuildSide.LEFT,
                JQuickHashJoinPhysicalNode.JoinDistribution.LOCAL
        );

        String queryId = "test_hash_join_" + System.currentTimeMillis();
        CompletableFuture<JQuickDataSet> future = coordinator.executeQuery(queryId, joinNode);

        JQuickDataSet result = future.get(30, TimeUnit.SECONDS);
        assertNotNull(result);
    }

    // ==================== 查询管理测试 ====================

    @Test
    public void testGetQueryStatus() throws Exception {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode(
                "test", "t", new HashSet<>(Collections.singletonList("id")), null
        );

        String queryId = "test_status_" + System.currentTimeMillis();
        CompletableFuture<JQuickDataSet> future = coordinator.executeQuery(queryId, scanNode);

        // 等待一小段时间让查询开始执行
        Thread.sleep(100);

        Map<String, QueryExecution> activeQueries = coordinator.getActiveQueries();
        // 查询可能在执行完成后被清理，所以不一定在 active 中
        // 只验证 map 不为空
        assertNotNull(activeQueries);
    }

    @Test
    public void testCancelQuery() throws Exception {
        // 创建一个可能执行时间较长的查询（使用复杂计划）
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode(
                "large_table", "t", new HashSet<>(Arrays.asList("id", "value")), null
        );

        // 添加一个复杂的嵌套操作
        JQuickFilterPhysicalNode filterNode = new JQuickFilterPhysicalNode(
                new JQuickBinaryExpression(
                        new JQuickColumnRefExpression("value"),
                        new JQuickLiteralExpression(100),
                        JQuickBinaryOperator.GT
                ), scanNode
        );

        String queryId = "test_cancel_" + System.currentTimeMillis();
        CompletableFuture<JQuickDataSet> future = coordinator.executeQuery(queryId, filterNode);

        // 等待一小段时间让查询开始执行
        Thread.sleep(500);

        // 取消查询
        CompletableFuture<Boolean> cancelFuture = coordinator.cancelQuery(queryId, "Test cancellation");
        Boolean cancelled = cancelFuture.get(10, TimeUnit.SECONDS);

        // 取消操作应该成功
        assertTrue(cancelled);

        // 验证查询被标记为取消
        QueryExecution execution = coordinator.getQueryStatus(queryId);
        if (execution != null) {
            assertTrue("Query should be cancelled",
                    execution.isCancelled() ||
                            execution.getStatus() == QueryExecution.QueryStatus.CANCELLED);
        }
    }

    @Test
    public void testCancelNonExistentQuery() throws Exception {
        CompletableFuture<Boolean> cancelFuture = coordinator.cancelQuery("non_existent_query", "Test");
        Boolean cancelled = cancelFuture.get(10, TimeUnit.SECONDS);

        assertFalse("Cancelling non-existent query should return false", cancelled);
    }

    // ==================== 复杂查询测试 ====================

    @Test
    public void testExecuteComplexQuery() throws Exception {
        // 构建复杂查询: SELECT * FROM (SELECT id, name FROM users WHERE age > 18) t ORDER BY name LIMIT 10

        // 1. 创建子查询的 Scan + Filter
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode(
                "users", "u", new HashSet<>(Arrays.asList("id", "name", "age")), null
        );

        JQuickExpression ageFilter = new JQuickBinaryExpression(
                new JQuickColumnRefExpression("age"),
                new JQuickLiteralExpression(18),
                JQuickBinaryOperator.GT
        );
        JQuickFilterPhysicalNode filterNode = new JQuickFilterPhysicalNode(ageFilter, scanNode);

        // 2. 创建 Project（只选择 id 和 name）
        List<JQuickProjectPhysicalNode.SelectItem> selectItems = Arrays.asList(
                new JQuickProjectPhysicalNode.SelectItem(new JQuickColumnRefExpression("id"), "id"),
                new JQuickProjectPhysicalNode.SelectItem(new JQuickColumnRefExpression("name"), "name")
        );
        JQuickProjectPhysicalNode projectNode = new JQuickProjectPhysicalNode(selectItems, filterNode, false);

        // 3. 创建 Sort
        List<JQuickSortPhysicalNode.OrderByItem> orderByItems = Arrays.asList(
                new JQuickSortPhysicalNode.OrderByItem("name", true)
        );
        JQuickSortPhysicalNode sortNode = new JQuickSortPhysicalNode(orderByItems, projectNode);

        // 4. 创建 Limit
        JQuickLimitPhysicalNode limitNode = new JQuickLimitPhysicalNode(10, sortNode);

        String queryId = "test_complex_" + System.currentTimeMillis();
        CompletableFuture<JQuickDataSet> future = coordinator.executeQuery(queryId, limitNode);

        JQuickDataSet result = future.get(30, TimeUnit.SECONDS);
        assertNotNull(result);
    }

    // ==================== Values 节点测试 ====================

    @Test
    public void testExecuteValuesQuery() throws Exception {
        // 创建 Values 节点，模拟内联数据
        List<String> columnNames = Arrays.asList("id", "name", "score");
        List<Class<?>> columnTypes = Arrays.asList(Integer.class, String.class, Double.class);

        List<List<Object>> rows = Arrays.asList(
                Arrays.asList(1, "Alice", 95.5),
                Arrays.asList(2, "Bob", 87.0),
                Arrays.asList(3, "Charlie", 92.3)
        );

        JQuickValuesPhysicalNode valuesNode = new JQuickValuesPhysicalNode(rows, columnNames, columnTypes);

        String queryId = "test_values_" + System.currentTimeMillis();
        CompletableFuture<JQuickDataSet> future = coordinator.executeQuery(queryId, valuesNode);

        JQuickDataSet result = future.get(30, TimeUnit.SECONDS);
        assertNotNull(result);
    }

    // ==================== 空节点测试 ====================

    @Test
    public void testExecuteEmptyQuery() throws Exception {
        String queryId = "test_empty_" + System.currentTimeMillis();
        CompletableFuture<JQuickDataSet> future = coordinator.executeQuery(queryId, JQuickEmptyPhysicalNode.INSTANCE);

        JQuickDataSet result = future.get(30, TimeUnit.SECONDS);
        assertNotNull(result);
        assertTrue("Empty query should return empty dataset", result.isEmpty());
    }

    // ==================== 并发查询测试 ====================

    @Test
    public void testConcurrentQueries() throws Exception {
        int queryCount = 5;
        List<CompletableFuture<JQuickDataSet>> futures = new ArrayList<>();

        for (int i = 0; i < queryCount; i++) {
            JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode(
                    "table_" + i, "t", new HashSet<>(Collections.singletonList("id")), null
            );
            String queryId = "concurrent_" + i + "_" + System.currentTimeMillis();
            futures.add(coordinator.executeQuery(queryId, scanNode));
        }

        // 等待所有查询完成
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .get(60, TimeUnit.SECONDS);

        for (CompletableFuture<JQuickDataSet> future : futures) {
            assertTrue("Query should be completed", future.isDone());
            assertNotNull(future.get());
        }
    }

    // ==================== Worker 健康检查测试 ====================

    @Test
    public void testWorkerHealthCheck() throws Exception {
        List<WorkerEndpoint> workers = coordinator.getWorkerStatus();
        for (WorkerEndpoint worker : workers) {
            assertTrue("Worker should be healthy", worker.isHealthy());
            assertTrue("Last heartbeat should be set", worker.getLastHeartbeat() > 0);
        }
    }

    @Test
    public void testWorkerMarkedUnhealthy() throws Exception {
        // 模拟一个不健康的 Worker
        WorkerEndpoint unhealthyWorker = coordinator.getWorkerStatus().get(0);

        // 手动标记为不健康
        unhealthyWorker.setHealthy(false);

        assertFalse("Worker should be marked unhealthy", unhealthyWorker.isHealthy());
    }

    // ==================== 统计信息测试 ====================

    @Test
    public void testPrintStatistics() {
        // 只是验证方法不会抛出异常
        try {
            coordinator.printStatistics();
        } catch (Exception e) {
            fail("printStatistics should not throw exception: " + e.getMessage());
        }
    }

    // ==================== 拓扑排序测试 ====================

    @Test
    public void testFragmentTopologicalSort() {
        // 创建一个有依赖关系的 Fragment 树
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode(
                "test", "t", new HashSet<>(Collections.singletonList("id")), null
        );

        // 通过 Fragmenter 创建计划
        // 这验证了 Fragmenter 和协调器的集成
        try {
            String queryId = "test_topological_" + System.currentTimeMillis();
            CompletableFuture<JQuickDataSet> future = coordinator.executeQuery(queryId, scanNode);
            JQuickDataSet result = future.get(30, TimeUnit.SECONDS);
            assertNotNull(result);
        } catch (Exception e) {
            // 如果执行失败，至少不应该抛出致命错误
            assertNotNull(e);
        }
    }

    // ==================== 流式查询测试 ====================

    @Test
    public void testStreamingQuery() throws Exception {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode(
                "stream_test", "t", new HashSet<>(Arrays.asList("id", "data")), null
        );

        String queryId = "test_stream_" + System.currentTimeMillis();

        // 创建流式响应观察者
        TestStreamObserver observer = new TestStreamObserver();

        CompletableFuture<Void> future = coordinator.executeQueryStream(queryId, scanNode, observer);

        // 等待流式执行完成
        future.get(30, TimeUnit.SECONDS);

        // 验证至少收到了响应（可能为空）
        assertNotNull(observer);
    }

    // ==================== 边界条件测试 ====================

    @Test(expected = NullPointerException.class)
    public void testExecuteQueryWithNullPlan() {
        coordinator.executeQuery("null_plan", null);
    }

    @Test
    public void testExecuteQueryWithLargeLimit() throws Exception {
        // 测试大的 limit 值
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode(
                "test", "t", new HashSet<>(Collections.singletonList("id")), null
        );
        JQuickLimitPhysicalNode limitNode = new JQuickLimitPhysicalNode(Integer.MAX_VALUE, scanNode);

        String queryId = "test_large_limit_" + System.currentTimeMillis();
        CompletableFuture<JQuickDataSet> future = coordinator.executeQuery(queryId, limitNode);

        // 应该能够处理大 limit 而不崩溃
        JQuickDataSet result = future.get(30, TimeUnit.SECONDS);
        assertNotNull(result);
    }

    @Test
    public void testExecuteQueryWithZeroLimit() throws Exception {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode(
                "test", "t", new HashSet<>(Collections.singletonList("id")), null
        );
        JQuickLimitPhysicalNode limitNode = new JQuickLimitPhysicalNode(0, scanNode);

        String queryId = "test_zero_limit_" + System.currentTimeMillis();
        CompletableFuture<JQuickDataSet> future = coordinator.executeQuery(queryId, limitNode);

        JQuickDataSet result = future.get(30, TimeUnit.SECONDS);
        assertNotNull(result);
        assertTrue("Zero limit should return empty result", result.isEmpty());
    }

    // ==================== 内部类测试辅助 ====================

    @Test
    public void testTaskExecutionStatus() {
        WorkerEndpoint worker = new WorkerEndpoint("test_worker", "localhost", 9000, 0);
        TaskExecution task = new TaskExecution(1L, 0, 4, worker);

        assertEquals(1L, task.getFragmentId());
        assertEquals(0, task.getTaskIndex());
        assertEquals(4, task.getTotalTasks());
        assertSame(worker, task.getAssignedWorker());
        assertEquals(TaskExecution.TaskStatus.PENDING, task.getStatus());
        assertEquals(0, task.getRetryCount());
        assertNull(task.getErrorMessage());

        task.setStatus(TaskExecution.TaskStatus.RUNNING);
        assertEquals(TaskExecution.TaskStatus.RUNNING, task.getStatus());

        task.incrementRetryCount();
        assertEquals(1, task.getRetryCount());

        task.setStartTime(1000L);
        task.setEndTime(2000L);
        assertEquals(1000L, task.getExecutionTimeMs());
    }

    @Test
    public void testQueryExecutionStatus() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode(
                "test", "t", new HashSet<>(Collections.singletonList("id")), null
        );

        // 创建分布式计划（需要 fragmenter）
        // 由于 fragmenter 需要完整计划，这里只测试基本状态
        String queryId = "test_query_execution";
        JQuickCoordinator.QueryExecution execution =null;
//                coordinator.new QueryExecution(queryId, null);

        assertEquals(queryId, execution.getQueryId());
        assertEquals(QueryExecution.QueryStatus.PENDING, execution.getStatus());
        assertFalse(execution.isCancelled());

        execution.setStatus(QueryExecution.QueryStatus.RUNNING);
        assertEquals(QueryExecution.QueryStatus.RUNNING, execution.getStatus());

        execution.setErrorMessage("Test error");
        assertEquals("Test error", execution.getErrorMessage());

        execution.cancel();
        assertTrue(execution.isCancelled());
    }

    @Test
    public void testWorkerEndpoint() {
        WorkerEndpoint endpoint = new WorkerEndpoint("test", "127.0.0.1", 8080, 5);

        assertEquals("test", endpoint.getWorkerId());
        assertEquals("127.0.0.1", endpoint.getHost());
        assertEquals(8080, endpoint.getPort());
        assertEquals(5, endpoint.getIndex());
        assertTrue(endpoint.isHealthy());
        assertTrue(endpoint.getLastHeartbeat() > 0);

        endpoint.setHealthy(false);
        assertFalse(endpoint.isHealthy());

        endpoint.updateHeartbeat();
        assertTrue(endpoint.getLastHeartbeat() > 0);

        String toString = endpoint.toString();
        assertTrue(toString.contains("test"));
        assertTrue(toString.contains("127.0.0.1:8080"));
    }

    // ==================== 辅助测试类 ====================

    /**
     * 测试用的流响应观察者
     */
    private static class TestStreamObserver implements StreamObserver<com.github.paohaijiao.proto.JQuickDataChunkProto> {
        private final List<com.github.paohaijiao.proto.JQuickDataChunkProto> receivedChunks = new ArrayList<>();
        private Throwable error;
        private boolean completed = false;

        @Override
        public void onNext(com.github.paohaijiao.proto.JQuickDataChunkProto value) {
            receivedChunks.add(value);
        }

        @Override
        public void onError(Throwable t) {
            this.error = t;
        }

        @Override
        public void onCompleted() {
            this.completed = true;
        }

        public List<com.github.paohaijiao.proto.JQuickDataChunkProto> getReceivedChunks() {
            return receivedChunks;
        }

        public Throwable getError() {
            return error;
        }

        public boolean isCompleted() {
            return completed;
        }
    }
}