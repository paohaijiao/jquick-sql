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

import com.github.paohaijiao.coordinator.JQuickCoordinator.QueryExecution;
import com.github.paohaijiao.coordinator.JQuickCoordinator.TaskExecution;
import com.github.paohaijiao.coordinator.JQuickCoordinator.WorkerEndpoint;
import com.github.paohaijiao.datasource.JQuickDataSourceManager;
import com.github.paohaijiao.enums.JQuickBinaryOperator;
import com.github.paohaijiao.enums.JQuickJoinType;
import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.expression.domain.JQuickBinaryExpression;
import com.github.paohaijiao.expression.domain.JQuickColumnRefExpression;
import com.github.paohaijiao.expression.domain.JQuickLiteralExpression;
import com.github.paohaijiao.physical.JQuickPhysicalPlanNode;
import com.github.paohaijiao.physical.domain.JQuickPhysicalColumn;
import com.github.paohaijiao.physical.node.*;
import com.github.paohaijiao.proto.JQuickExecuteTaskResponse;
import com.github.paohaijiao.proto.JQuickTaskStatusProto;
import com.github.paohaijiao.statement.JQuickColumnMeta;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;
import com.github.paohaijiao.worker.JQuickWorker;
import io.grpc.inprocess.InProcessChannelBuilder;
import io.grpc.inprocess.InProcessServerBuilder;
import io.grpc.stub.StreamObserver;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.*;

/**
 * JQuickCoordinator 测试用例（完整版，包含测试数据）
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/12/16
 */
public class JQuickCoordinatorWithDataTest {

    private JQuickCoordinator coordinator;

    private List<JQuickWorker> workers;

    private List<JQuickCoordinator.WorkerEndpoint> endpoints;

    private static final int WORKER_COUNT = 3;

    private static final int BASE_PORT = 19000;

    private static final String TEST_TABLE_USERS = "users";

    private static final String TEST_TABLE_ORDERS = "orders";

    private static final String TEST_TABLE_EMPLOYEES = "employees";

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
        JQuickDataSet testData = createTestUsersData();
        JQuickDataSourceManager.registerTable(TEST_TABLE_USERS, testData);
        coordinator = new JQuickCoordinator("coordinator_test", endpoints);
        registerTestData();
    }
    private JQuickDataSet createTestUsersData() {
        JQuickDataSet.Builder builder = JQuickDataSet.builder();
        builder.addColumn("id", Integer.class, TEST_TABLE_USERS);
        builder.addColumn("name", String.class, TEST_TABLE_USERS);
        builder.addColumn("age", Integer.class, TEST_TABLE_USERS);
        for (int i = 1; i <= 10; i++) {
            JQuickRow row = new JQuickRow();
            row.put("id", i);
            row.put("name", "user_" + i);
            row.put("age", 20 + i);
            builder.addRow(row);
        }
        return builder.build();
    }
    /**
     * 注册测试数据到 JQuickDataSourceManager
     */
    private void registerTestData() {
        // 创建 users 表测试数据
        JQuickDataSet usersData = createUsersDataSet();
        JQuickDataSourceManager.registerTable(TEST_TABLE_USERS, usersData);

        // 创建 orders 表测试数据
        JQuickDataSet ordersData = createOrdersDataSet();
        JQuickDataSourceManager.registerTable(TEST_TABLE_ORDERS, ordersData);

        // 创建 employees 表测试数据
        JQuickDataSet employeesData = createEmployeesDataSet();
        JQuickDataSourceManager.registerTable(TEST_TABLE_EMPLOYEES, employeesData);
    }

    /**
     * 创建 users 表测试数据
     * 表结构: id, name, age, email, city
     */
    private JQuickDataSet createUsersDataSet() {
        List<JQuickColumnMeta> columns = Arrays.asList(
                new JQuickColumnMeta("id", Integer.class, TEST_TABLE_USERS),
                new JQuickColumnMeta("name", String.class, TEST_TABLE_USERS),
                new JQuickColumnMeta("age", Integer.class, TEST_TABLE_USERS),
                new JQuickColumnMeta("email", String.class, TEST_TABLE_USERS),
                new JQuickColumnMeta("city", String.class, TEST_TABLE_USERS)
        );

        List<JQuickRow> rows = new ArrayList<>();

        // 添加 10 条测试数据
        rows.add(createRow(new Object[][]{
                {"id", 1}, {"name", "Alice"}, {"age", 25}, {"email", "alice@example.com"}, {"city", "Beijing"}
        }));
        rows.add(createRow(new Object[][]{
                {"id", 2}, {"name", "Bob"}, {"age", 30}, {"email", "bob@example.com"}, {"city", "Shanghai"}
        }));
        rows.add(createRow(new Object[][]{
                {"id", 3}, {"name", "Charlie"}, {"age", 35}, {"email", "charlie@example.com"}, {"city", "Beijing"}
        }));
        rows.add(createRow(new Object[][]{
                {"id", 4}, {"name", "Diana"}, {"age", 28}, {"email", "diana@example.com"}, {"city", "Guangzhou"}
        }));
        rows.add(createRow(new Object[][]{
                {"id", 5}, {"name", "Eve"}, {"age", 22}, {"email", "eve@example.com"}, {"city", "Shanghai"}
        }));
        rows.add(createRow(new Object[][]{
                {"id", 6}, {"name", "Frank"}, {"age", 40}, {"email", "frank@example.com"}, {"city", "Beijing"}
        }));
        rows.add(createRow(new Object[][]{
                {"id", 7}, {"name", "Grace"}, {"age", 27}, {"email", "grace@example.com"}, {"city", "Shenzhen"}
        }));
        rows.add(createRow(new Object[][]{
                {"id", 8}, {"name", "Henry"}, {"age", 33}, {"email", "henry@example.com"}, {"city", "Guangzhou"}
        }));
        rows.add(createRow(new Object[][]{
                {"id", 9}, {"name", "Ivy"}, {"age", 26}, {"email", "ivy@example.com"}, {"city", "Beijing"}
        }));
        rows.add(createRow(new Object[][]{
                {"id", 10}, {"name", "Jack"}, {"age", 29}, {"email", "jack@example.com"}, {"city", "Shanghai"}
        }));

        return new JQuickDataSet(columns, rows);
    }

    /**
     * 创建 orders 表测试数据
     * 表结构: order_id, user_id, product, amount, order_date
     */
    private JQuickDataSet createOrdersDataSet() {
        List<JQuickColumnMeta> columns = Arrays.asList(
                new JQuickColumnMeta("order_id", Integer.class, TEST_TABLE_ORDERS),
                new JQuickColumnMeta("user_id", Integer.class, TEST_TABLE_ORDERS),
                new JQuickColumnMeta("product", String.class, TEST_TABLE_ORDERS),
                new JQuickColumnMeta("amount", Double.class, TEST_TABLE_ORDERS),
                new JQuickColumnMeta("order_date", String.class, TEST_TABLE_ORDERS)
        );

        List<JQuickRow> rows = new ArrayList<>();

        rows.add(createRow(new Object[][]{
                {"order_id", 101}, {"user_id", 1}, {"product", "Laptop"}, {"amount", 5999.0}, {"order_date", "2024-01-15"}
        }));
        rows.add(createRow(new Object[][]{
                {"order_id", 102}, {"user_id", 1}, {"product", "Mouse"}, {"amount", 99.0}, {"order_date", "2024-01-20"}
        }));
        rows.add(createRow(new Object[][]{
                {"order_id", 103}, {"user_id", 2}, {"product", "Keyboard"}, {"amount", 299.0}, {"order_date", "2024-02-10"}
        }));
        rows.add(createRow(new Object[][]{
                {"order_id", 104}, {"user_id", 3}, {"product", "Monitor"}, {"amount", 1999.0}, {"order_date", "2024-02-15"}
        }));
        rows.add(createRow(new Object[][]{
                {"order_id", 105}, {"user_id", 3}, {"product", "USB Cable"}, {"amount", 29.0}, {"order_date", "2024-03-01"}
        }));
        rows.add(createRow(new Object[][]{
                {"order_id", 106}, {"user_id", 5}, {"product", "Headset"}, {"amount", 399.0}, {"order_date", "2024-03-10"}
        }));
        rows.add(createRow(new Object[][]{
                {"order_id", 107}, {"user_id", 7}, {"product", "Webcam"}, {"amount", 499.0}, {"order_date", "2024-03-15"}
        }));
        rows.add(createRow(new Object[][]{
                {"order_id", 108}, {"user_id", 8}, {"product", "Desk"}, {"amount", 899.0}, {"order_date", "2024-04-01"}
        }));
        rows.add(createRow(new Object[][]{
                {"order_id", 109}, {"user_id", 10}, {"product", "Chair"}, {"amount", 1299.0}, {"order_date", "2024-04-05"}
        }));
        rows.add(createRow(new Object[][]{
                {"order_id", 110}, {"user_id", 2}, {"product", "Mouse Pad"}, {"amount", 49.0}, {"order_date", "2024-04-10"}
        }));

        return new JQuickDataSet(columns, rows);
    }

    /**
     * 创建 employees 表测试数据
     * 表结构: emp_id, name, department, salary, hire_date
     */
    private JQuickDataSet createEmployeesDataSet() {
        List<JQuickColumnMeta> columns = Arrays.asList(
                new JQuickColumnMeta("emp_id", Integer.class, TEST_TABLE_EMPLOYEES),
                new JQuickColumnMeta("name", String.class, TEST_TABLE_EMPLOYEES),
                new JQuickColumnMeta("department", String.class, TEST_TABLE_EMPLOYEES),
                new JQuickColumnMeta("salary", Double.class, TEST_TABLE_EMPLOYEES),
                new JQuickColumnMeta("hire_date", String.class, TEST_TABLE_EMPLOYEES)
        );

        List<JQuickRow> rows = new ArrayList<>();

        rows.add(createRow(new Object[][]{
                {"emp_id", 1}, {"name", "Alice"}, {"department", "Engineering"}, {"salary", 85000.0}, {"hire_date", "2020-01-15"}
        }));
        rows.add(createRow(new Object[][]{
                {"emp_id", 2}, {"name", "Bob"}, {"department", "Sales"}, {"salary", 65000.0}, {"hire_date", "2019-06-20"}
        }));
        rows.add(createRow(new Object[][]{
                {"emp_id", 3}, {"name", "Charlie"}, {"department", "Engineering"}, {"salary", 95000.0}, {"hire_date", "2018-03-10"}
        }));
        rows.add(createRow(new Object[][]{
                {"emp_id", 4}, {"name", "Diana"}, {"department", "HR"}, {"salary", 55000.0}, {"hire_date", "2021-02-01"}
        }));
        rows.add(createRow(new Object[][]{
                {"emp_id", 5}, {"name", "Eve"}, {"department", "Sales"}, {"salary", 70000.0}, {"hire_date", "2020-08-15"}
        }));
        rows.add(createRow(new Object[][]{
                {"emp_id", 6}, {"name", "Frank"}, {"department", "Engineering"}, {"salary", 110000.0}, {"hire_date", "2017-11-20"}
        }));
        rows.add(createRow(new Object[][]{
                {"emp_id", 7}, {"name", "Grace"}, {"department", "Marketing"}, {"salary", 60000.0}, {"hire_date", "2021-07-01"}
        }));
        rows.add(createRow(new Object[][]{
                {"emp_id", 8}, {"name", "Henry"}, {"department", "Engineering"}, {"salary", 78000.0}, {"hire_date", "2020-12-10"}
        }));
        rows.add(createRow(new Object[][]{
                {"emp_id", 9}, {"name", "Ivy"}, {"department", "Sales"}, {"salary", 72000.0}, {"hire_date", "2019-09-05"}
        }));
        rows.add(createRow(new Object[][]{
                {"emp_id", 10}, {"name", "Jack"}, {"department", "Engineering"}, {"salary", 89000.0}, {"hire_date", "2020-04-25"}
        }));

        return new JQuickDataSet(columns, rows);
    }

    /**
     * 辅助方法：创建单行数据
     */
    private JQuickRow createRow(Object[][] keyValues) {
        JQuickRow row = new JQuickRow();
        for (Object[] kv : keyValues) {
            row.put((String) kv[0], kv[1]);
        }
        return row;
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
                TEST_TABLE_USERS, "t", requiredColumns, null
        );
        String queryId = "test_scan_" + System.currentTimeMillis();
        CompletableFuture<JQuickDataSet> future = coordinator.executeQuery(queryId, scanNode);
        JQuickDataSet result = future.get(30, TimeUnit.SECONDS);
        result.printTable();
        assertNotNull("Result should not be null", result);
        assertEquals("Should have 10 rows", 10, result.size());
        assertEquals("Should have 3 columns", 3, result.getColumnNames().size());
        // 验证列名
        List<String> columnNames = result.getColumnNames();
        assertTrue(columnNames.contains("id"));
        assertTrue(columnNames.contains("name"));
        assertTrue(columnNames.contains("age"));
    }

    @Test
    public void testExecuteTableScanAllColumns() throws Exception {
        // 不指定 requiredColumns，应该返回所有列
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode(TEST_TABLE_USERS, "t", null, null);
        String queryId = "test_scan_all_" + System.currentTimeMillis();
        CompletableFuture<JQuickDataSet> future = coordinator.executeQuery(queryId, scanNode);
        JQuickDataSet result = future.get(30, TimeUnit.SECONDS);
        result.printTable();
        assertNotNull(result);
        assertEquals("Should have 10 rows", 10, result.size());
        assertEquals("Should have 5 columns (all columns)", 5, result.getColumnNames().size());
    }
    @Test
    public void testExecuteFilterQuery() throws Exception {
        // 创建过滤条件: age > 28
        JQuickExpression predicate = new JQuickBinaryExpression(new JQuickColumnRefExpression("age"), new JQuickLiteralExpression(28), JQuickBinaryOperator.GT);
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode(TEST_TABLE_USERS, "u", new HashSet<>(Arrays.asList("id", "name", "age")), null);
        JQuickFilterPhysicalNode filterNode = new JQuickFilterPhysicalNode(predicate, scanNode);
        String queryId = "test_filter_" + System.currentTimeMillis();
        CompletableFuture<JQuickDataSet> future = coordinator.executeQuery(queryId, filterNode);
        JQuickDataSet result = future.get(30, TimeUnit.SECONDS);
        result.printTable();
        assertNotNull(result);
        // 验证过滤结果（age > 28 的用户：Bob(30), Charlie(35), Frank(40), Henry(33), Jack(29) -> 5人）
        assertEquals("Should have 5 rows with age > 28", 5, result.size());
        // 验证所有行的 age 都大于 28
        for (JQuickRow row : result.getRows()) {
            int age = row.getInt("age");
            assertTrue("Age should be > 28, but was: " + age, age > 28);
        }
    }

    @Test
    public void testExecuteFilterWithAndCondition() throws Exception {
        // 创建过滤条件: age > 25 AND city = 'Beijing'
        JQuickExpression ageCondition = new JQuickBinaryExpression(new JQuickColumnRefExpression("age"), new JQuickLiteralExpression(25), JQuickBinaryOperator.GT);
        JQuickExpression cityCondition = new JQuickBinaryExpression(new JQuickColumnRefExpression("city"), new JQuickLiteralExpression("Beijing"), JQuickBinaryOperator.EQ);
        JQuickExpression andCondition = new JQuickBinaryExpression(ageCondition, cityCondition, JQuickBinaryOperator.AND);
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode(TEST_TABLE_USERS, "u", new HashSet<>(Arrays.asList("id", "name", "age", "city")), null);
        JQuickFilterPhysicalNode filterNode = new JQuickFilterPhysicalNode(andCondition, scanNode);
        String queryId = "test_and_filter_" + System.currentTimeMillis();
        CompletableFuture<JQuickDataSet> future = coordinator.executeQuery(queryId, filterNode);
        JQuickDataSet result = future.get(30, TimeUnit.SECONDS);
        assertNotNull(result);
        // 验证结果：age > 25 且 city = 'Beijing' 的用户：Alice(25不满足), Charlie(35), Frank(40), Ivy(26) -> 3人
        assertEquals("Should have 3 rows", 3, result.size());
        for (JQuickRow row : result.getRows()) {
            int age = row.getInt("age");
            String city = row.getString("city");
            assertTrue("Age should be > 25", age > 25);
            assertEquals("City should be Beijing", "Beijing", city);
        }
    }
    @Test
    public void testExecuteProjectQuery() throws Exception {
        // 创建 Project 节点: SELECT name, age, age * 2 as double_age
        List<JQuickProjectPhysicalNode.SelectItem> selectItems = Arrays.asList(
                new JQuickProjectPhysicalNode.SelectItem(new JQuickColumnRefExpression("name"), "user_name"),
                new JQuickProjectPhysicalNode.SelectItem(new JQuickColumnRefExpression("age"), "user_age"),
                new JQuickProjectPhysicalNode.SelectItem(new JQuickBinaryExpression(new JQuickColumnRefExpression("age"), new JQuickLiteralExpression(2), JQuickBinaryOperator.MULTIPLY), "double_age")
        );
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode(TEST_TABLE_USERS, "u", new HashSet<>(Arrays.asList("name", "age")), null);
        JQuickProjectPhysicalNode projectNode = new JQuickProjectPhysicalNode(selectItems, scanNode, false);
        String queryId = "test_project_" + System.currentTimeMillis();
        CompletableFuture<JQuickDataSet> future = coordinator.executeQuery(queryId, projectNode);
        JQuickDataSet result = future.get(30, TimeUnit.SECONDS);
        assertNotNull(result);
        assertEquals("Should have 10 rows", 10, result.size());
        List<String> columnNames = result.getColumnNames();
        assertTrue(columnNames.contains("user_name"));
        assertTrue(columnNames.contains("user_age"));
        assertTrue(columnNames.contains("double_age"));
        // 验证计算列
        for (JQuickRow row : result.getRows()) {
            int age = row.getInt("user_age");
            double doubleAge = row.getDouble("double_age");
            assertEquals("double_age should be age * 2", age * 2, doubleAge, 0.01);
        }
    }

    @Test
    public void testExecuteProjectWithDistinct() throws Exception {
        // 创建 Project 节点: SELECT DISTINCT city
        List<JQuickProjectPhysicalNode.SelectItem> selectItems = Arrays.asList(new JQuickProjectPhysicalNode.SelectItem(new JQuickColumnRefExpression("city"), "city"));
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode(TEST_TABLE_USERS, "u", new HashSet<>(Collections.singletonList("city")), null);
        JQuickProjectPhysicalNode projectNode = new JQuickProjectPhysicalNode(selectItems, scanNode, true);
        String queryId = "test_distinct_" + System.currentTimeMillis();
        CompletableFuture<JQuickDataSet> future = coordinator.executeQuery(queryId, projectNode);
        JQuickDataSet result = future.get(30, TimeUnit.SECONDS);
        assertNotNull(result);
        // 去重后的城市：Beijing, Shanghai, Guangzhou, Shenzhen -> 4个
        assertEquals("Should have 4 distinct cities", 4, result.size());
    }

    @Test
    public void testExecuteLimitQuery() throws Exception {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode(TEST_TABLE_USERS, "t", new HashSet<>(Arrays.asList("id", "name")), null);
        JQuickLimitPhysicalNode limitNode = new JQuickLimitPhysicalNode(5, scanNode);
        String queryId = "test_limit_" + System.currentTimeMillis();
        CompletableFuture<JQuickDataSet> future = coordinator.executeQuery(queryId, limitNode);
        JQuickDataSet result = future.get(30, TimeUnit.SECONDS);
        assertNotNull(result);
        assertEquals("Should have 5 rows", 5, result.size());
    }

    @Test
    public void testExecuteLimitWithOffset() throws Exception {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode(TEST_TABLE_USERS, "t", new HashSet<>(Arrays.asList("id", "name")), null);
        JQuickLimitPhysicalNode limitNode = new JQuickLimitPhysicalNode(3, 2, scanNode);
        String queryId = "test_offset_" + System.currentTimeMillis();
        CompletableFuture<JQuickDataSet> future = coordinator.executeQuery(queryId, limitNode);
        JQuickDataSet result = future.get(30, TimeUnit.SECONDS);
        assertNotNull(result);
        assertEquals("Should have 3 rows", 3, result.size());
    }

    @Test
    public void testExecuteQueryWithZeroLimit() throws Exception {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode(TEST_TABLE_USERS, "t", new HashSet<>(Arrays.asList("id", "name")), null);
        JQuickLimitPhysicalNode limitNode = new JQuickLimitPhysicalNode(0, scanNode);
        String queryId = "test_zero_limit_" + System.currentTimeMillis();
        CompletableFuture<JQuickDataSet> future = coordinator.executeQuery(queryId, limitNode);
        JQuickDataSet result = future.get(30, TimeUnit.SECONDS);
        assertNotNull(result);
        assertTrue("Zero limit should return empty result", result.isEmpty());
    }

    @Test
    public void testExecuteSortQueryAscending() throws Exception {
        List<JQuickSortPhysicalNode.OrderByItem> orderByItems = Arrays.asList(new JQuickSortPhysicalNode.OrderByItem("age", true));// ASC
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode(TEST_TABLE_USERS, "u", new HashSet<>(Arrays.asList("id", "name", "age")), null);
        JQuickSortPhysicalNode sortNode = new JQuickSortPhysicalNode(orderByItems, scanNode);
        String queryId = "test_sort_asc_" + System.currentTimeMillis();
        CompletableFuture<JQuickDataSet> future = coordinator.executeQuery(queryId, sortNode);
        JQuickDataSet result = future.get(30, TimeUnit.SECONDS);
        assertNotNull(result);
        assertEquals(10, result.size());
        // 验证升序
        int prevAge = -1;
        for (JQuickRow row : result.getRows()) {
            int age = row.getInt("age");
            assertTrue("Age should be in ascending order", age >= prevAge);
            prevAge = age;
        }
    }

    @Test
    public void testExecuteSortQueryDescending() throws Exception {
        List<JQuickSortPhysicalNode.OrderByItem> orderByItems = Arrays.asList(new JQuickSortPhysicalNode.OrderByItem("age", false));
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode(TEST_TABLE_USERS, "u", new HashSet<>(Arrays.asList("id", "name", "age")), null);
        JQuickSortPhysicalNode sortNode = new JQuickSortPhysicalNode(orderByItems, scanNode);
        String queryId = "test_sort_desc_" + System.currentTimeMillis();
        CompletableFuture<JQuickDataSet> future = coordinator.executeQuery(queryId, sortNode);
        JQuickDataSet result = future.get(30, TimeUnit.SECONDS);
        assertNotNull(result);
        assertEquals(10, result.size());
        // 验证降序
        int prevAge = Integer.MAX_VALUE;
        for (JQuickRow row : result.getRows()) {
            int age = row.getInt("age");
            assertTrue("Age should be in descending order", age <= prevAge);
            prevAge = age;
        }
    }

    @Test
    public void testExecuteSortWithMultipleColumns() throws Exception {
        // 先按 city 升序，再按 age 降序
        List<JQuickSortPhysicalNode.OrderByItem> orderByItems = Arrays.asList(
                new JQuickSortPhysicalNode.OrderByItem("city", true),
                new JQuickSortPhysicalNode.OrderByItem("age", false)
        );

        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode(TEST_TABLE_USERS, "u", new HashSet<>(Arrays.asList("name", "age", "city")), null);
        JQuickSortPhysicalNode sortNode = new JQuickSortPhysicalNode(orderByItems, scanNode);
        String queryId = "test_multi_sort_" + System.currentTimeMillis();
        CompletableFuture<JQuickDataSet> future = coordinator.executeQuery(queryId, sortNode);
        JQuickDataSet result = future.get(30, TimeUnit.SECONDS);
        assertNotNull(result);
        // 验证排序：city 升序，同一 city 内 age 降序
        String prevCity = "";
        int prevAge = Integer.MAX_VALUE;
        for (JQuickRow row : result.getRows()) {
            String city = row.getString("city");
            int age = row.getInt("age");
            if (!city.equals(prevCity)) {
                prevAge = Integer.MAX_VALUE;
                prevCity = city;
            }
            assertTrue("Within same city, age should be descending", age <= prevAge);
            prevAge = age;
        }
    }


    @Test
    public void testExecuteAggregateCountQuery() throws Exception {
        // 创建聚合节点: COUNT(*)
        List<JQuickHashAggregatePhysicalNode.AggregateFunction> aggregates = Arrays.asList(new JQuickHashAggregatePhysicalNode.AggregateFunction("count", null, false, "total_count"));
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode(TEST_TABLE_USERS, "u", new HashSet<>(Arrays.asList("id")), null);
        JQuickHashAggregatePhysicalNode aggNode = new JQuickHashAggregatePhysicalNode(null, aggregates, scanNode, null, JQuickHashAggregatePhysicalNode.AggregateStage.SINGLE);
        String queryId = "test_count_" + System.currentTimeMillis();
        CompletableFuture<JQuickDataSet> future = coordinator.executeQuery(queryId, aggNode);
        JQuickDataSet result = future.get(30, TimeUnit.SECONDS);
        assertNotNull(result);
        assertEquals(1, result.size());
        Long count = result.getRows().get(0).getAs("total_count", Long.class);
        assertEquals("Count should be 10", Long.valueOf(10), count);
    }

    @Test
    public void testExecuteAggregateSumQuery() throws Exception {
        List<JQuickHashAggregatePhysicalNode.AggregateFunction> aggregates = Arrays.asList(new JQuickHashAggregatePhysicalNode.AggregateFunction("sum", new JQuickColumnRefExpression("salary"), false, "total_salary"));
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode(TEST_TABLE_EMPLOYEES, "e", new HashSet<>(Arrays.asList("salary")), null);
        JQuickHashAggregatePhysicalNode aggNode = new JQuickHashAggregatePhysicalNode(null, aggregates, scanNode, null, JQuickHashAggregatePhysicalNode.AggregateStage.SINGLE);
        String queryId = "test_sum_" + System.currentTimeMillis();
        CompletableFuture<JQuickDataSet> future = coordinator.executeQuery(queryId, aggNode);
        JQuickDataSet result = future.get(30, TimeUnit.SECONDS);
        assertNotNull(result);
        assertEquals(1, result.size());
        Double totalSalary = result.getRows().get(0).getAs("total_salary", Double.class);
        // 期望总和: 85000 + 65000 + 95000 + 55000 + 70000 + 110000 + 60000 + 78000 + 72000 + 89000 = 783000
        assertEquals(Double.valueOf(783000.0), totalSalary, 0.01);
    }

    @Test
    public void testExecuteAggregateAvgQuery() throws Exception {
        List<JQuickHashAggregatePhysicalNode.AggregateFunction> aggregates = Arrays.asList(new JQuickHashAggregatePhysicalNode.AggregateFunction("avg", new JQuickColumnRefExpression("age"), false, "avg_age"));
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode(TEST_TABLE_USERS, "u", new HashSet<>(Arrays.asList("age")), null);
        JQuickHashAggregatePhysicalNode aggNode = new JQuickHashAggregatePhysicalNode(null, aggregates, scanNode, null, JQuickHashAggregatePhysicalNode.AggregateStage.SINGLE);
        String queryId = "test_avg_" + System.currentTimeMillis();
        CompletableFuture<JQuickDataSet> future = coordinator.executeQuery(queryId, aggNode);
        JQuickDataSet result = future.get(30, TimeUnit.SECONDS);
        assertNotNull(result);
        Double avgAge = result.getRows().get(0).getAs("avg_age", Double.class);
        // 期望平均值: (25+30+35+28+22+40+27+33+26+29) / 10 = 29.5
        assertEquals(Double.valueOf(29.5), avgAge, 0.01);
    }

    @Test
    public void testExecuteGroupByAggregateQuery() throws Exception {
        // GROUP BY department, COUNT(*), AVG(salary)
        List<JQuickExpression> groupKeys = Arrays.asList(new JQuickColumnRefExpression("department"));
        List<JQuickHashAggregatePhysicalNode.AggregateFunction> aggregates = Arrays.asList(new JQuickHashAggregatePhysicalNode.AggregateFunction("count", null, false, "emp_count"), new JQuickHashAggregatePhysicalNode.AggregateFunction("avg", new JQuickColumnRefExpression("salary"), false, "avg_salary"));
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode(TEST_TABLE_EMPLOYEES, "e", new HashSet<>(Arrays.asList("department", "salary")), null);
        JQuickHashAggregatePhysicalNode aggNode = new JQuickHashAggregatePhysicalNode(groupKeys, aggregates, scanNode, null, JQuickHashAggregatePhysicalNode.AggregateStage.SINGLE);
        String queryId = "test_groupby_" + System.currentTimeMillis();
        CompletableFuture<JQuickDataSet> future = coordinator.executeQuery(queryId, aggNode);
        JQuickDataSet result = future.get(30, TimeUnit.SECONDS);
        assertNotNull(result);
        // 验证分组结果
        Map<String, JQuickRow> groupResults = new HashMap<>();
        for (JQuickRow row : result.getRows()) {
            String dept = row.getString("department");
            groupResults.put(dept, row);
        }
        // Engineering: 5人 (Alice, Charlie, Frank, Henry, Jack)
        assertTrue(groupResults.containsKey("Engineering"));
        assertEquals(Long.valueOf(5), groupResults.get("Engineering").getAs("emp_count", Long.class));
        // Sales: 3人 (Bob, Eve, Ivy)
        assertTrue(groupResults.containsKey("Sales"));
        assertEquals(Long.valueOf(3), groupResults.get("Sales").getAs("emp_count", Long.class));
        // HR: 1人 (Diana)
        assertTrue(groupResults.containsKey("HR"));
        assertEquals(Long.valueOf(1), groupResults.get("HR").getAs("emp_count", Long.class));
        // Marketing: 1人 (Grace)
        assertTrue(groupResults.containsKey("Marketing"));
        assertEquals(Long.valueOf(1), groupResults.get("Marketing").getAs("emp_count", Long.class));
    }

    @Test
    public void testExecuteHashJoinQuery() throws Exception {
        // 左表: orders
        JQuickTableScanPhysicalNode leftScan = new JQuickTableScanPhysicalNode(TEST_TABLE_ORDERS, "o", new HashSet<>(Arrays.asList("order_id", "user_id", "amount")), null);
        // 右表: users
        JQuickTableScanPhysicalNode rightScan = new JQuickTableScanPhysicalNode(TEST_TABLE_USERS, "u", new HashSet<>(Arrays.asList("id", "name", "city")), null);
        // Join 条件: o.user_id = u.id
        List<JQuickHashJoinPhysicalNode.JoinKeyPair> joinKeys = Arrays.asList(new JQuickHashJoinPhysicalNode.JoinKeyPair(new JQuickColumnRefExpression("user_id"), new JQuickColumnRefExpression("id")));
        JQuickHashJoinPhysicalNode joinNode = new JQuickHashJoinPhysicalNode(JQuickJoinType.INNER, leftScan, rightScan, null, joinKeys, JQuickHashJoinPhysicalNode.BuildSide.LEFT, JQuickHashJoinPhysicalNode.JoinDistribution.LOCAL);
        String queryId = "test_join_" + System.currentTimeMillis();
        CompletableFuture<JQuickDataSet> future = coordinator.executeQuery(queryId, joinNode);
        JQuickDataSet result = future.get(30, TimeUnit.SECONDS);
        assertNotNull(result);
        // 验证 Join 结果
        for (JQuickRow row : result.getRows()) {
            Integer orderId = row.getInt("order_id");
            Integer userId = row.getInt("user_id");
            Integer userIdFromUser = row.getInt("id");
            String userName = row.getString("name");
            assertEquals("user_id should match id", userId, userIdFromUser);
            assertNotNull(userName);
        }
    }

    @Test
    public void testExecuteLeftJoinQuery() throws Exception {
        // 左表: users
        JQuickTableScanPhysicalNode leftScan = new JQuickTableScanPhysicalNode(TEST_TABLE_USERS, "u", new HashSet<>(Arrays.asList("id", "name")), null);

        // 右表: orders
        JQuickTableScanPhysicalNode rightScan = new JQuickTableScanPhysicalNode(TEST_TABLE_ORDERS, "o", new HashSet<>(Arrays.asList("order_id", "user_id", "amount")), null);
        List<JQuickHashJoinPhysicalNode.JoinKeyPair> joinKeys = Arrays.asList(new JQuickHashJoinPhysicalNode.JoinKeyPair(new JQuickColumnRefExpression("id"), new JQuickColumnRefExpression("user_id")));
        JQuickHashJoinPhysicalNode joinNode = new JQuickHashJoinPhysicalNode(
                JQuickJoinType.LEFT, leftScan, rightScan, null, joinKeys,
                JQuickHashJoinPhysicalNode.BuildSide.LEFT,
                JQuickHashJoinPhysicalNode.JoinDistribution.LOCAL
        );
        String queryId = "test_left_join_" + System.currentTimeMillis();
        CompletableFuture<JQuickDataSet> future = coordinator.executeQuery(queryId, joinNode);
        JQuickDataSet result = future.get(30, TimeUnit.SECONDS);
        assertNotNull(result);
        // LEFT JOIN 应该包含所有 users (10条)
        assertTrue("Should have at least 10 rows", result.size() >= 10);
    }

    @Test
    public void testExecuteTopNQuery() throws Exception {
        // 按 salary 降序取前 3 名
        List<JQuickSortPhysicalNode.OrderByItem> orderByItems = Arrays.asList(new JQuickSortPhysicalNode.OrderByItem("salary", false));
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode(TEST_TABLE_EMPLOYEES, "e", new HashSet<>(Arrays.asList("name", "department", "salary")), null);
        JQuickTopNPhysicalNode topNNode = new JQuickTopNPhysicalNode(orderByItems, 3, 0, scanNode);
        String queryId = "test_topn_" + System.currentTimeMillis();
        CompletableFuture<JQuickDataSet> future = coordinator.executeQuery(queryId, topNNode);
        JQuickDataSet result = future.get(30, TimeUnit.SECONDS);
        assertNotNull(result);
        assertEquals(3, result.size());
        // 验证 Top 3 是工资最高的
        double prevSalary = Double.MAX_VALUE;
        for (JQuickRow row : result.getRows()) {
            double salary = row.getDouble("salary");
            assertTrue(salary <= prevSalary);
            prevSalary = salary;
        }
    }

    @Test
    public void testExecuteValuesQuery() throws Exception {
        List<String> columnNames = Arrays.asList("id", "name", "score");
        List<Class<?>> columnTypes = Arrays.asList(Integer.class, String.class, Double.class);
        List<List<Object>> rows = Arrays.asList(Arrays.asList(1, "Alice", 95.5), Arrays.asList(2, "Bob", 87.0), Arrays.asList(3, "Charlie", 92.3));
        JQuickValuesPhysicalNode valuesNode = new JQuickValuesPhysicalNode(rows, columnNames, columnTypes);
        String queryId = "test_values_" + System.currentTimeMillis();
        CompletableFuture<JQuickDataSet> future = coordinator.executeQuery(queryId, valuesNode);
        JQuickDataSet result = future.get(30, TimeUnit.SECONDS);
        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(3, result.getColumnNames().size());
    }

    @Test
    public void testExecuteEmptyQuery() throws Exception {
        String queryId = "test_empty_" + System.currentTimeMillis();
        CompletableFuture<JQuickDataSet> future = coordinator.executeQuery(queryId, JQuickEmptyPhysicalNode.INSTANCE);
        JQuickDataSet result = future.get(30, TimeUnit.SECONDS);
        assertNotNull(result);
        assertTrue("Empty query should return empty dataset", result.isEmpty());
    }

    @Test
    public void testCancelQuery() throws Exception {
        // 创建一个复杂查询
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode(TEST_TABLE_ORDERS, "o", new HashSet<>(Arrays.asList("order_id", "user_id", "amount")), null);
        String queryId = "test_cancel_" + System.currentTimeMillis();
        CompletableFuture<JQuickDataSet> future = coordinator.executeQuery(queryId, scanNode);
        // 等待一小段时间让查询开始执行
        Thread.sleep(500);
        // 取消查询
        CompletableFuture<Boolean> cancelFuture = coordinator.cancelQuery(queryId, "Test cancellation");
        Boolean cancelled = cancelFuture.get(10, TimeUnit.SECONDS);
        // 取消操作应该成功
        assertTrue(cancelled);
    }

    @Test
    public void testCancelNonExistentQuery() throws Exception {
        CompletableFuture<Boolean> cancelFuture = coordinator.cancelQuery("non_existent_query", "Test");
        Boolean cancelled = cancelFuture.get(10, TimeUnit.SECONDS);
        assertFalse("Cancelling non-existent query should return false", cancelled);
    }

    @Test
    public void testConcurrentQueries() throws Exception {
        int queryCount = 5;
        List<CompletableFuture<JQuickDataSet>> futures = new ArrayList<>();
        for (int i = 0; i < queryCount; i++) {
            JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode(TEST_TABLE_USERS, "t", new HashSet<>(Arrays.asList("id", "name")), null);
            String queryId = "concurrent_" + i + "_" + System.currentTimeMillis();
            futures.add(coordinator.executeQuery(queryId, scanNode));
        }
        // 等待所有查询完成
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get(60, TimeUnit.SECONDS);
        for (CompletableFuture<JQuickDataSet> future : futures) {
            assertTrue("Query should be completed", future.isDone());
            JQuickDataSet result = future.get();
            assertEquals(10, result.size());
        }
    }
    @Test
    public void testExecuteComplexQuery() throws Exception {
        // 查询: 北京用户的订单总金额
        // 过滤北京用户
        JQuickExpression cityFilter = new JQuickBinaryExpression(new JQuickColumnRefExpression("city"), new JQuickLiteralExpression("Beijing"), JQuickBinaryOperator.EQ);
        JQuickTableScanPhysicalNode usersScan = new JQuickTableScanPhysicalNode(TEST_TABLE_USERS, "u", new HashSet<>(Arrays.asList("id", "name", "city")), null);
        JQuickFilterPhysicalNode userFilter = new JQuickFilterPhysicalNode(cityFilter, usersScan);
        // Join 订单表
        JQuickTableScanPhysicalNode ordersScan = new JQuickTableScanPhysicalNode(TEST_TABLE_ORDERS, "o", new HashSet<>(Arrays.asList("order_id", "user_id", "amount")), null);
        List<JQuickHashJoinPhysicalNode.JoinKeyPair> joinKeys = Arrays.asList(new JQuickHashJoinPhysicalNode.JoinKeyPair(new JQuickColumnRefExpression("id"), new JQuickColumnRefExpression("user_id")));
        JQuickHashJoinPhysicalNode joinNode = new JQuickHashJoinPhysicalNode(JQuickJoinType.INNER, userFilter, ordersScan, null, joinKeys, JQuickHashJoinPhysicalNode.BuildSide.LEFT, JQuickHashJoinPhysicalNode.JoinDistribution.LOCAL);
        //聚合计算总金额
        List<JQuickHashAggregatePhysicalNode.AggregateFunction> aggregates = Arrays.asList(new JQuickHashAggregatePhysicalNode.AggregateFunction("sum", new JQuickColumnRefExpression("amount"), false, "total_amount"));
        JQuickHashAggregatePhysicalNode aggNode = new JQuickHashAggregatePhysicalNode(null, aggregates, joinNode, null, JQuickHashAggregatePhysicalNode.AggregateStage.SINGLE);
        String queryId = "test_complex_" + System.currentTimeMillis();
        CompletableFuture<JQuickDataSet> future = coordinator.executeQuery(queryId, aggNode);
        JQuickDataSet result = future.get(30, TimeUnit.SECONDS);
        assertNotNull(result);

        // 北京用户: Alice, Charlie, Frank, Ivy
        // 相关订单: Alice(101,102), Charlie(104,105), Frank(无订单), Ivy(无订单)
        // 总金额: 5999 + 99 + 1999 + 29 = 8126
        Double totalAmount = result.getRows().get(0).getAs("total_amount", Double.class);
        assertEquals(Double.valueOf(8126.0), totalAmount, 0.01);
    }
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


    @Test
    public void testPrintStatistics() {
        // 只是验证方法不会抛出异常
        try {
            coordinator.printStatistics();
        } catch (Exception e) {
            fail("printStatistics should not throw exception: " + e.getMessage());
        }
    }
}
