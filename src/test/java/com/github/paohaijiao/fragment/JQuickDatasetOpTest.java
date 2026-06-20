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
package com.github.paohaijiao.fragment;

import com.github.paohaijiao.datasource.JQuickDataSourceManager;
import com.github.paohaijiao.distributed.JQuickDistributedPlan;
import com.github.paohaijiao.distributed.coordinator.JQuickCoordinator;
import com.github.paohaijiao.distributed.worker.JQuickWorker;
import com.github.paohaijiao.enums.JQuickBinaryOperator;
import com.github.paohaijiao.enums.JQuickSQLOperationType;
import com.github.paohaijiao.expression.domain.JQuickBinaryExpression;
import com.github.paohaijiao.expression.domain.JQuickColumnRefExpression;
import com.github.paohaijiao.expression.domain.JQuickLiteralExpression;
import com.github.paohaijiao.logic.JQuickLogicalPlanNode;
import com.github.paohaijiao.logic.domain.JQuickFilterNode;
import com.github.paohaijiao.logic.domain.JQuickProjectNode;
import com.github.paohaijiao.logic.domain.JQuickSetOperationNode;
import com.github.paohaijiao.logic.domain.JQuickTableScanNode;
import com.github.paohaijiao.logic2physical.JQuickPhysicalPlanGenerator;
import com.github.paohaijiao.physical.JQuickPhysicalPlanNode;
import com.github.paohaijiao.physical.node.JQuickSetOperationPhysicalNode;
import com.github.paohaijiao.statement.JQuickColumnMeta;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * packageName com.github.paohaijiao.physical
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/5/24
 */
public class JQuickDatasetOpTest {

    private JQuickPhysicalPlanGenerator generator;
    private JQuickFragmenter fragmenter;
    private JQuickFragmenter verboseFragmenter;

    @Before
    public void setUp() {
        generator = new JQuickPhysicalPlanGenerator();
        fragmenter = new JQuickFragmenter(4);
        verboseFragmenter = new JQuickFragmenter(8);
        registerTestData();
    }

    @After
    public void tearDown() {
        JQuickDataSourceManager.clearAll();
    }

    /**
     * 注册测试数据
     */
    private void registerTestData() {
        // 注册 employee 表
        registerEmployeeTable();

        // 注册 orders 表
        registerOrdersTable();

        // 注册 products 表
        registerProductsTable();

        // 注册 customers 表
        registerCustomersTable();
    }

    /**
     * 注册员工表
     * 数据：
     * 部门 1 - 技术部: Alice(8000), Bob(10000), Charlie(12000)
     * 部门 2 - 市场部: David(9000), Eve(9500)
     * 部门 3 - 人事部: Frank(7000), Grace(7500)
     */
    private void registerEmployeeTable() {
        List<JQuickColumnMeta> columns = Arrays.asList(
            new JQuickColumnMeta("emp_id", Long.class, "employee"),
            new JQuickColumnMeta("emp_name", String.class, "employee"),
            new JQuickColumnMeta("dept_id", Long.class, "employee"),
            new JQuickColumnMeta("salary", Double.class, "employee")
        );
        List<JQuickRow> rows = new ArrayList<>();
        rows.add(createRow(columns, new Object[]{1L, "Alice", 1L, 8000.0}));
        rows.add(createRow(columns, new Object[]{2L, "Bob", 1L, 10000.0}));
        rows.add(createRow(columns, new Object[]{3L, "Charlie", 1L, 12000.0}));
        rows.add(createRow(columns, new Object[]{4L, "David", 2L, 9000.0}));
        rows.add(createRow(columns, new Object[]{5L, "Eve", 2L, 9500.0}));
        rows.add(createRow(columns, new Object[]{6L, "Frank", 3L, 7000.0}));
        rows.add(createRow(columns, new Object[]{7L, "Grace", 3L, 7500.0}));

        JQuickDataSet data = new JQuickDataSet(columns, rows);
        JQuickDataSourceManager.registerTable("employee", data);
    }

    /**
     * 注册订单表
     * 数据：
     * 订单 1 (completed): 商品X(100*2), 商品Y(50*1)
     * 订单 2 (completed): 商品X(100*3), 商品Z(80*2)
     * 订单 3 (pending): 商品Y(50*5), 商品Z(80*1)
     * 订单 4 (cancelled): 商品X(100*1)
     */
    private void registerOrdersTable() {
        List<JQuickColumnMeta> columns = Arrays.asList(
            new JQuickColumnMeta("order_id", Long.class, "orders"),
            new JQuickColumnMeta("product_id", Long.class, "orders"),
            new JQuickColumnMeta("customer_id", Long.class, "orders"),
            new JQuickColumnMeta("status", String.class, "orders"),
            new JQuickColumnMeta("amount", Double.class, "orders")
        );
        List<JQuickRow> rows = new ArrayList<>();
        rows.add(createRow(columns, new Object[]{1L, 101L, 1001L, "completed", 250.0}));
        rows.add(createRow(columns, new Object[]{2L, 101L, 1002L, "completed", 460.0}));
        rows.add(createRow(columns, new Object[]{3L, 102L, 1001L, "pending", 330.0}));
        rows.add(createRow(columns, new Object[]{4L, 103L, 1003L, "cancelled", 100.0}));
        rows.add(createRow(columns, new Object[]{5L, 101L, 1004L, "completed", 180.0}));

        JQuickDataSet data = new JQuickDataSet(columns, rows);
        JQuickDataSourceManager.registerTable("orders", data);
    }

    /**
     * 注册产品表
     */
    private void registerProductsTable() {
        List<JQuickColumnMeta> columns = Arrays.asList(
            new JQuickColumnMeta("product_id", Long.class, "products"),
            new JQuickColumnMeta("product_name", String.class, "products"),
            new JQuickColumnMeta("price", Double.class, "products")
        );
        List<JQuickRow> rows = new ArrayList<>();
        rows.add(createRow(columns, new Object[]{101L, "商品X", 100.0}));
        rows.add(createRow(columns, new Object[]{102L, "商品Y", 50.0}));
        rows.add(createRow(columns, new Object[]{103L, "商品Z", 80.0}));

        JQuickDataSet data = new JQuickDataSet(columns, rows);
        JQuickDataSourceManager.registerTable("products", data);
    }

    /**
     * 注册客户表
     */
    private void registerCustomersTable() {
        List<JQuickColumnMeta> columns = Arrays.asList(
            new JQuickColumnMeta("customer_id", Long.class, "customers"),
            new JQuickColumnMeta("name", String.class, "customers"),
            new JQuickColumnMeta("city", String.class, "customers")
        );
        List<JQuickRow> rows = new ArrayList<>();
        rows.add(createRow(columns, new Object[]{1001L, "张三", "北京"}));
        rows.add(createRow(columns, new Object[]{1002L, "李四", "上海"}));
        rows.add(createRow(columns, new Object[]{1003L, "王五", "广州"}));
        rows.add(createRow(columns, new Object[]{1004L, "赵六", "深圳"}));

        JQuickDataSet data = new JQuickDataSet(columns, rows);
        JQuickDataSourceManager.registerTable("customers", data);
    }

    /**
     * 创建一行数据
     */
    private JQuickRow createRow(List<JQuickColumnMeta> columns, Object[] values) {
        JQuickRow row = new JQuickRow();
        for (int i = 0; i < columns.size(); i++) {
            row.put(columns.get(i).getName(), values[i]);
        }
        return row;
    }

    /**
     * 创建多行 DataSet
     */
    private JQuickDataSet createDataSet(List<JQuickColumnMeta> columns, List<Object[]> rowValues) {
        List<JQuickRow> rows = new ArrayList<>();
        for (Object[] values : rowValues) {
            rows.add(createRow(columns, values));
        }
        return new JQuickDataSet(columns, rows);
    }

    /**
     * 打印分区数据统计
     */
    private void printPartitionStats(Map<Integer, List<JQuickRow>> partitions) {
        System.out.println("\n=== 分区数据统计 ===");
        System.out.println("分区数量: " + partitions.size());
        System.out.println();
        
        for (Map.Entry<Integer, List<JQuickRow>> entry : partitions.entrySet()) {
            int partitionId = entry.getKey();
            List<JQuickRow> rows = entry.getValue();
            System.out.println("分区 " + partitionId + ": " + rows.size() + " 行");
        }
        
        // 统计 dept_id 分布（如果数据中有 dept_id 列）
        if (!partitions.isEmpty() && partitions.get(0).get(0).containsKey("dept_id")) {
            System.out.println("\n=== dept_id 分区分布 ===");
            Map<Long, Integer> deptCount = new HashMap<>();
            for (Map.Entry<Integer, List<JQuickRow>> entry : partitions.entrySet()) {
                for (JQuickRow row : entry.getValue()) {
                    Long deptId = (Long) row.get("dept_id");
                    deptCount.put(deptId, deptCount.getOrDefault(deptId, 0) + 1);
                }
            }
            for (Map.Entry<Long, Integer> entry : deptCount.entrySet()) {
                System.out.println("dept_id=" + entry.getKey() + ": " + entry.getValue() + " 行");
            }
        }
        
        // 统计 status 分布（如果数据中有 status 列）
        if (!partitions.isEmpty() && partitions.get(0).get(0).containsKey("status")) {
            System.out.println("\n=== status 分区分布 ===");
            Map<String, Integer> statusCount = new HashMap<>();
            for (Map.Entry<Integer, List<JQuickRow>> entry : partitions.entrySet()) {
                for (JQuickRow row : entry.getValue()) {
                    String status = (String) row.get("status");
                    statusCount.put(status, statusCount.getOrDefault(status, 0) + 1);
                }
            }
            for (Map.Entry<String, Integer> entry : statusCount.entrySet()) {
                System.out.println("status=" + entry.getKey() + ": " + entry.getValue() + " 行");
            }
        }
    }

    /**
     * 创建简单的表扫描节点
     */
    private JQuickTableScanNode createTableScan(String tableName) {
        return new JQuickTableScanNode(tableName);
    }

    /**
     * 创建带别名的表扫描节点
     */
    private JQuickTableScanNode createTableScan(String tableName, String alias) {
        return new JQuickTableScanNode(tableName, alias);
    }

    /**
     * 创建投影节点
     */
    private JQuickProjectNode createProject(JQuickLogicalPlanNode child, String... columns) {
        List<JQuickProjectNode.SelectItem> items = new ArrayList<>();
        for (String col : columns) {
            items.add(new JQuickProjectNode.SelectItem(new JQuickColumnRefExpression(col), col));
        }
        return new JQuickProjectNode(items, child);
    }

    /**
     * 创建过滤节点
     */
    private JQuickFilterNode createFilter(JQuickLogicalPlanNode child, String column, String operator, Object value) {
        JQuickBinaryOperator op;
        switch (operator) {
            case ">": op = JQuickBinaryOperator.GT; break;
            case "<": op = JQuickBinaryOperator.LT; break;
            case "=": op = JQuickBinaryOperator.EQ; break;
            default: op = JQuickBinaryOperator.EQ;
        }
        JQuickBinaryExpression predicate = new JQuickBinaryExpression(new JQuickColumnRefExpression(column), new JQuickLiteralExpression(value), op);
        return new JQuickFilterNode(predicate, child);
    }

    /**
     * 创建带过滤条件的投影
     */
    private JQuickProjectNode createProjectWithFilter(String tableName, String filterCol, Object filterValue, String... columns) {
        JQuickTableScanNode scan = createTableScan(tableName);
        JQuickFilterNode filter = createFilter(scan, filterCol, "=", filterValue);
        return createProject(filter, columns);
    }
    /**
     * 测试1：UNION 操作
     *
     * SQL示例：
     * SELECT id, name FROM users WHERE status = 'active'
     * UNION
     * SELECT id, name FROM users WHERE status = 'pending'
     *
     * 使用 JQuickCoordinator 执行分布式计算
     */
    @Test
    public void testUnionOperation() throws Exception {
        // 注册测试数据（users 表）
        registerUsersTable();

        // 验证数据已注册
        JQuickDataSet usersData = JQuickDataSourceManager.getTable("users");
        usersData.printTable();
        JQuickWorker worker1 = new JQuickWorker("worker-1", 9001);
        JQuickWorker worker2 = new JQuickWorker("worker-2", 9002);
        worker1.start();
        worker2.start();
        List<JQuickCoordinator.WorkerEndpoint> endpoints = Arrays.asList(
            new JQuickCoordinator.WorkerEndpoint("worker-1", "localhost", 9001, 0),
            new JQuickCoordinator.WorkerEndpoint("worker-2", "localhost", 9002, 1)
        );
        JQuickCoordinator coordinator = new JQuickCoordinator("coordinator-1", endpoints);
        System.out.println("=== 广播 users 表数据到所有 Worker ===");
        coordinator.broadcastTable("users", usersData, true).join();
        System.out.println("users 表数据已广播完成");
        JQuickProjectNode leftQuery = createProjectWithFilter("users", "status", "active", "id", "name");
        JQuickProjectNode rightQuery = createProjectWithFilter("users", "status", "pending", "id", "name");
        JQuickSetOperationNode unionNode = new JQuickSetOperationNode(JQuickSQLOperationType.UNION, leftQuery, rightQuery);

        // 生成物理计划
        JQuickPhysicalPlanNode physicalPlan = generator.generate(unionNode);

        // 使用 Coordinator 执行查询
        System.out.println("=== UNION 操作测试（基于 JQuickCoordinator）===");
        JQuickDataSet result = coordinator.executeQuery("query-union-001", physicalPlan);

        // 验证结果
        System.out.println("\n=== 执行结果 ===");
        result.printTable();
        System.out.println("总行数: " + result.getRows().size());

        // 停止 Worker
        worker1.stop();
        worker2.stop();

        System.out.println("=== UNION操作测试通过 ===");
    }

    /**
     * 注册 users 表测试数据
     */
    private void registerUsersTable() {
        List<JQuickColumnMeta> columns = Arrays.asList(
            new JQuickColumnMeta("id", Long.class, "users"),
            new JQuickColumnMeta("name", String.class, "users"),
            new JQuickColumnMeta("status", String.class, "users")
        );
        List<JQuickRow> rows = new ArrayList<>();
        rows.add(createRow(columns, new Object[]{1L, "Alice", "active"}));
        rows.add(createRow(columns, new Object[]{2L, "Bob", "active"}));
        rows.add(createRow(columns, new Object[]{3L, "Charlie", "pending"}));
        rows.add(createRow(columns, new Object[]{4L, "David", "pending"}));
        rows.add(createRow(columns, new Object[]{5L, "Eve", "inactive"}));

        JQuickDataSet data = new JQuickDataSet(columns, rows);
        JQuickDataSourceManager.registerTable("users", data);
    }
    /**
     * 测试2：UNION ALL 操作
     *
     * SQL示例：
     * SELECT id, name FROM users WHERE age > 18
     * UNION ALL
     * SELECT id, name FROM users WHERE age <= 18
     */
    @Test
    public void testUnionAllOperation() {
        JQuickProjectNode leftQuery = createProjectWithFilter("users", "age", 18, "id", "name");
        JQuickFilterNode filter = (JQuickFilterNode) leftQuery.getChild();
        JQuickBinaryExpression newPredicate = new JQuickBinaryExpression(new JQuickColumnRefExpression("age"), new JQuickLiteralExpression(18), JQuickBinaryOperator.GT);
        JQuickFilterNode newFilter = new JQuickFilterNode(newPredicate, ((JQuickFilterNode) filter).getChild());
        JQuickProjectNode leftQueryModified = new JQuickProjectNode(leftQuery.getSelectItems(), newFilter, leftQuery.isDistinct());
        JQuickProjectNode rightQuery = createProjectWithFilter("users", "age", 18, "id", "name");
        JQuickFilterNode rightFilter = (JQuickFilterNode) rightQuery.getChild();
        JQuickBinaryExpression rightPredicate = new JQuickBinaryExpression(new JQuickColumnRefExpression("age"), new JQuickLiteralExpression(18), JQuickBinaryOperator.LE);
        JQuickFilterNode newRightFilter = new JQuickFilterNode(rightPredicate, rightFilter.getChild());
        JQuickProjectNode rightQueryModified = new JQuickProjectNode(rightQuery.getSelectItems(), newRightFilter, rightQuery.isDistinct());
        JQuickSetOperationNode unionAllNode = new JQuickSetOperationNode(JQuickSQLOperationType.UNION_ALL, leftQueryModified, rightQueryModified);
        JQuickPhysicalPlanNode physicalPlan = generator.generate(unionAllNode);
        JQuickDistributedPlan distributedPlan= fragmenter.fragment(physicalPlan);
        fragmenter.printFragments(distributedPlan);
        System.out.println(physicalPlan);
    }
    /**
     * 测试3：INTERSECT 操作
     *
     * SQL示例：
     * SELECT product_id FROM orders_2023
     * INTERSECT
     * SELECT product_id FROM orders_2024
     */
    @Test
    public void testIntersectOperation() {
        JQuickProjectNode leftQuery = createProject(createTableScan("orders_2023"), "product_id");
        JQuickProjectNode rightQuery = createProject(createTableScan("orders_2024"), "product_id");
        JQuickSetOperationNode intersectNode = new JQuickSetOperationNode(JQuickSQLOperationType.INTERSECT, leftQuery, rightQuery);
        JQuickPhysicalPlanNode physicalPlan = generator.generate(intersectNode);
        JQuickSetOperationPhysicalNode setOpNode = (JQuickSetOperationPhysicalNode) physicalPlan;
        JQuickDistributedPlan distributedPlan= fragmenter.fragment(setOpNode);
        fragmenter.printFragments(distributedPlan);
        System.out.println(physicalPlan);
    }
    /**
     * 测试5：多个集合操作嵌套
     *
     * SQL示例：
     * (SELECT id FROM table1 UNION SELECT id FROM table2)
     * INTERSECT
     * (SELECT id FROM table3 UNION SELECT id FROM table4)
     */
    @Test
    public void testNestedSetOperations() {
        JQuickProjectNode table1 = createProject(createTableScan("table1"), "id");
        JQuickProjectNode table2 = createProject(createTableScan("table2"), "id");
        JQuickSetOperationNode innerUnion = new JQuickSetOperationNode(JQuickSQLOperationType.UNION, table1, table2);
        JQuickProjectNode table3 = createProject(createTableScan("table3"), "id");
        JQuickProjectNode table4 = createProject(createTableScan("table4"), "id");
        JQuickSetOperationNode innerUnion2 = new JQuickSetOperationNode(JQuickSQLOperationType.UNION, table3, table4);
        JQuickSetOperationNode outerIntersect = new JQuickSetOperationNode(JQuickSQLOperationType.INTERSECT, innerUnion, innerUnion2);
        JQuickPhysicalPlanNode physicalPlan = generator.generate(outerIntersect);
        JQuickDistributedPlan distributedPlan= fragmenter.fragment(physicalPlan);
        fragmenter.printFragments(distributedPlan);
        System.out.println(physicalPlan);
    }

    // ==================== 基于分区的测试用例 ====================

    /**
     * 测试1：HASH 分区策略
     *
     * SQL示例：
     * SELECT dept_id, COUNT(*) FROM employee GROUP BY dept_id
     *
     * 分区策略：按 dept_id 做 HASH 分区，相同 dept_id 的数据落到同一分区
     *
     * 数据：
     * 部门 1 - 技术部: Alice(8000), Bob(10000), Charlie(12000) → dept_id=1
     * 部门 2 - 市场部: David(9000), Eve(9500) → dept_id=2
     * 部门 3 - 人事部: Frank(7000), Grace(7500) → dept_id=3
     */
    @Test
    public void testHashPartitionStrategy() {
        // 创建 GROUP BY 查询
        JQuickTableScanNode scan = createTableScan("employee");
        JQuickProjectNode project = createProject(scan, "dept_id", "emp_name", "salary");

        // 生成物理计划
        JQuickPhysicalPlanNode physicalPlan = generator.generate(project);

        // 获取源数据用于验证
        JQuickDataSet sourceData = JQuickDataSourceManager.getTable("employee");
        System.out.println("=== HASH 分区策略测试 ===");
        System.out.println("源数据总行数: " + sourceData.getRows().size());
        sourceData.printTable();

        // 分区（默认使用 HASH 策略）
        JQuickDistributedPlan distributedPlan = fragmenter.fragment(physicalPlan);

        // 验证分区结果
        System.out.println("\n=== 分区结果 ===");
        fragmenter.printFragments(distributedPlan);
        System.out.println("分区数量: " + distributedPlan.getDefaultParallelism());
        System.out.println("分区策略: HASH (根据 dept_id)");

        // 统计每个 dept_id 的行数
        Map<Long, Integer> deptCount = new HashMap<>();
        for (JQuickRow row : sourceData.getRows()) {
            Long deptId = (Long) row.get("dept_id");
            deptCount.put(deptId, deptCount.getOrDefault(deptId, 0) + 1);
        }

        System.out.println("\n=== 源数据 dept_id 分布 ===");
        for (Map.Entry<Long, Integer> entry : deptCount.entrySet()) {
            System.out.println("dept_id=" + entry.getKey() + ": " + entry.getValue() + " 行");
        }

        System.out.println("预期：相同 dept_id 的数据在同一分区");
    }

    /**
     * 测试2：ROUND_ROBIN 分区策略
     *
     * 场景：数据均匀分布到各个分区
     *
     * 分区策略：轮询分发，数据均匀分布，无特定 key 关联
     */
    @Test
    public void testRoundRobinPartitionStrategy() {
        // 创建简单的全表扫描
        JQuickTableScanNode scan = createTableScan("orders");
        JQuickProjectNode project = createProject(scan, "order_id", "customer_id", "status", "amount");

        // 获取源数据用于验证
        JQuickDataSet sourceData = JQuickDataSourceManager.getTable("orders");
        System.out.println("=== ROUND_ROBIN 分区策略测试 ===");
        System.out.println("源数据总行数: " + sourceData.getRows().size());
        sourceData.printTable();

        // 生成物理计划
        JQuickPhysicalPlanNode physicalPlan = generator.generate(project);

        // 分区
        JQuickDistributedPlan distributedPlan = fragmenter.fragment(physicalPlan);

        // 验证分区结果
        System.out.println("\n=== 分区结果 ===");
        fragmenter.printFragments(distributedPlan);
        System.out.println("分区数量: " + distributedPlan.getDefaultParallelism());
        System.out.println("分区策略: ROUND_ROBIN (轮询分发)");

        // 统计每个 status 的行数
        Map<String, Integer> statusCount = new HashMap<>();
        for (JQuickRow row : sourceData.getRows()) {
            String status = (String) row.get("status");
            statusCount.put(status, statusCount.getOrDefault(status, 0) + 1);
        }

        System.out.println("\n=== 源数据 status 分布 ===");
        for (Map.Entry<String, Integer> entry : statusCount.entrySet()) {
            System.out.println("status=" + entry.getKey() + ": " + entry.getValue() + " 行");
        }

        System.out.println("预期：数据均匀分布到各个分区");
    }

    /**
     * 测试3：REPLICATE 分区策略（广播）
     *
     * 场景：小表广播到大表的所有分区
     *
     * SQL示例：
     * SELECT * FROM orders o JOIN products p ON o.product_id = p.id
     *
     * 分区策略：小表 products 使用 REPLICATE 广播到所有节点
     *
     * 数据：
     * orders: 5 行
     * products: 3 行
     */
    @Test
    public void testReplicatePartitionStrategy() {
        // 创建 JOIN 查询
        JQuickTableScanNode orders = createTableScan("orders");
        JQuickTableScanNode products = createTableScan("products");

        JQuickProjectNode ordersProject = createProject(orders, "order_id", "product_id", "customer_id", "status", "amount");
        JQuickProjectNode productsProject = createProject(products, "product_id", "product_name", "price");

        // 获取源数据用于验证
        JQuickDataSet ordersData = JQuickDataSourceManager.getTable("orders");
        JQuickDataSet productsData = JQuickDataSourceManager.getTable("products");

        System.out.println("=== REPLICATE 分区策略测试（广播）===");
        System.out.println("orders 表行数: " + ordersData.getRows().size());
        System.out.println("products 表行数: " + productsData.getRows().size());
        System.out.println("\norders 数据:");
        ordersData.printTable();
        System.out.println("\nproducts 数据:");
        productsData.printTable();

        // 生成物理计划
        JQuickPhysicalPlanNode physicalPlan = generator.generate(ordersProject);

        // 分区
        JQuickDistributedPlan distributedPlan = fragmenter.fragment(physicalPlan);

        // 验证分区结果
        System.out.println("\n=== 分区结果 ===");
        fragmenter.printFragments(distributedPlan);
        System.out.println("分区数量: " + distributedPlan.getDefaultParallelism());
        System.out.println("分区策略: REPLICATE (广播)");
        System.out.println("预期：小表(products)复制到所有分区，避免大表(orders) shuffle");
    }

    /**
     * 测试4：多表扫描 + 分区
     *
     * 场景：多张表的数据按不同策略分区
     *
     * SQL示例：
     * SELECT c.customer_id, c.name, o.order_id, o.total
     * FROM customers c
     * JOIN orders o ON c.customer_id = o.customer_id
     *
     * 数据：
     * customers: 4 行 (customer_id: 1001, 1002, 1003, 1004)
     * orders: 5 行 (customer_id: 1001, 1002, 1001, 1003, 1004)
     */
    @Test
    public void testMultiTablePartition() {
        // 创建多表查询
        JQuickTableScanNode customers = createTableScan("customers");
        JQuickTableScanNode orders = createTableScan("orders");

        JQuickProjectNode customersProject = createProject(customers, "customer_id", "name", "city");
        JQuickProjectNode ordersProject = createProject(orders, "order_id", "customer_id", "status", "amount");

        // 获取源数据用于验证
        JQuickDataSet customersData = JQuickDataSourceManager.getTable("customers");
        JQuickDataSet ordersData = JQuickDataSourceManager.getTable("orders");

        System.out.println("=== 多表分区测试 ===");
        System.out.println("customers 表行数: " + customersData.getRows().size());
        System.out.println("orders 表行数: " + ordersData.getRows().size());
        System.out.println("\ncustomers 数据:");
        customersData.printTable();
        System.out.println("\norders 数据:");
        ordersData.printTable();

        // 生成物理计划
        JQuickPhysicalPlanNode physicalPlan = generator.generate(customersProject);

        // 分区
        JQuickDistributedPlan distributedPlan = fragmenter.fragment(physicalPlan);

        // 验证分区结果
        System.out.println("\n=== 分区结果 ===");
        fragmenter.printFragments(distributedPlan);
        System.out.println("分区数量: " + distributedPlan.getDefaultParallelism());
        System.out.println("预期：customers 和 orders 表按 customer_id 分区");
    }

    /**
     * 测试5：不同并行度的分区
     *
     * 场景：使用不同的并行度进行分区
     */
    @Test
    public void testDifferentParallelism() {
        // 创建简单查询
        JQuickTableScanNode scan = createTableScan("employee");
        JQuickProjectNode project = createProject(scan, "emp_id", "emp_name", "dept_id", "salary");

        // 获取源数据用于验证
        JQuickDataSet sourceData = JQuickDataSourceManager.getTable("employee");
        System.out.println("=== 不同并行度分区测试 ===");
        System.out.println("源数据总行数: " + sourceData.getRows().size());
        sourceData.printTable();

        // 生成物理计划
        JQuickPhysicalPlanNode physicalPlan = generator.generate(project);

        // 使用不同的并行度分区
        JQuickFragmenter fragmenter2 = new JQuickFragmenter(2);
        JQuickFragmenter fragmenter4 = new JQuickFragmenter(4);
        JQuickFragmenter fragmenter8 = new JQuickFragmenter(8);

        JQuickDistributedPlan plan2 = fragmenter2.fragment(physicalPlan);
        JQuickDistributedPlan plan4 = fragmenter4.fragment(physicalPlan);
        JQuickDistributedPlan plan8 = fragmenter8.fragment(physicalPlan);

        // 验证分区结果
        System.out.println("\n=== 并行度 2 ===");
        fragmenter2.printFragments(plan2);
        System.out.println("分区数量: " + plan2.getDefaultParallelism());

        System.out.println("\n=== 并行度 4 ===");
        fragmenter4.printFragments(plan4);
        System.out.println("分区数量: " + plan4.getDefaultParallelism());

        System.out.println("\n=== 并行度 8 ===");
        fragmenter8.printFragments(plan8);
        System.out.println("分区数量: " + plan8.getDefaultParallelism());

        System.out.println("预期：并行度越高，分区越多，每个分区的数据越少");
    }

    /**
     * 测试6：UNION 操作 + 分区
     *
     * SQL示例：
     * SELECT dept_id, emp_name FROM employees_2024
     * UNION
     * SELECT dept_id, emp_name FROM employees_2025
     *
     * 分区策略：UNION 结果按 dept_id 哈希分区
     */
    @Test
    public void testUnionWithPartition() {
        // 模拟 UNION 操作：使用 orders 表两次来模拟两个年份的员工表
        JQuickProjectNode leftQuery = createProject(createTableScan("orders"), "order_id", "customer_id", "status", "amount");
        JQuickProjectNode rightQuery = createProject(createTableScan("orders"), "order_id", "customer_id", "status", "amount");

        JQuickSetOperationNode unionNode = new JQuickSetOperationNode(JQuickSQLOperationType.UNION, leftQuery, rightQuery);

        // 获取源数据用于验证
        JQuickDataSet ordersData = JQuickDataSourceManager.getTable("orders");
        System.out.println("=== UNION + 分区测试 ===");
        System.out.println("orders 表行数（模拟两个表）: " + ordersData.getRows().size());
        System.out.println("\norders 数据:");
        ordersData.printTable();

        // 生成物理计划
        JQuickPhysicalPlanNode physicalPlan = generator.generate(unionNode);

        // 分区
        JQuickDistributedPlan distributedPlan = fragmenter.fragment(physicalPlan);

        // 验证分区结果
        System.out.println("\n=== 分区结果 ===");
        fragmenter.printFragments(distributedPlan);
        System.out.println("分区数量: " + distributedPlan.getDefaultParallelism());
        System.out.println("预期：UNION 结果按 HASH 策略分区");
    }

    /**
     * 测试7：聚合操作 + 分区
     *
     * SQL示例：
     * SELECT dept_id, COUNT(*) as emp_count, AVG(salary) as avg_salary
     * FROM employees
     * GROUP BY dept_id
     *
     * 分区策略：按 dept_id 哈希分区，同一部门的数据在同一分区
     *
     * 数据：
     * 部门 1 - 技术部: Alice(8000), Bob(10000), Charlie(12000) → COUNT=3, SUM=30000, AVG=10000
     * 部门 2 - 市场部: David(9000), Eve(9500) → COUNT=2, SUM=18500, AVG=9250
     * 部门 3 - 人事部: Frank(7000), Grace(7500) → COUNT=2, SUM=14500, AVG=7250
     */
    @Test
    public void testAggregateWithPartition() {
        // 创建聚合查询
        JQuickTableScanNode scan = createTableScan("employee");
        JQuickProjectNode project = createProject(scan, "dept_id", "emp_id", "emp_name", "salary");

        // 获取源数据用于验证
        JQuickDataSet sourceData = JQuickDataSourceManager.getTable("employee");
        System.out.println("=== 聚合操作 + 分区测试 ===");
        System.out.println("源数据总行数: " + sourceData.getRows().size());
        sourceData.printTable();

        // 生成物理计划
        JQuickPhysicalPlanNode physicalPlan = generator.generate(project);

        // 分区
        JQuickDistributedPlan distributedPlan = fragmenter.fragment(physicalPlan);

        // 验证分区结果
        System.out.println("\n=== 分区结果 ===");
        fragmenter.printFragments(distributedPlan);
        System.out.println("分区数量: " + distributedPlan.getDefaultParallelism());
        System.out.println("分区策略: HASH (按 dept_id)");

        // 计算聚合结果用于验证
        Map<Long, List<Double>> deptSalaries = new HashMap<>();
        for (JQuickRow row : sourceData.getRows()) {
            Long deptId = (Long) row.get("dept_id");
            Double salary = (Double) row.get("salary");
            deptSalaries.computeIfAbsent(deptId, k -> new ArrayList<>()).add(salary);
        }

        System.out.println("\n=== 预期聚合结果 ===");
        for (Map.Entry<Long, List<Double>> entry : deptSalaries.entrySet()) {
            Long deptId = entry.getKey();
            List<Double> salaries = entry.getValue();
            double sum = salaries.stream().mapToDouble(Double::doubleValue).sum();
            double avg = sum / salaries.size();
            System.out.println("dept_id=" + deptId + ": COUNT=" + salaries.size() + ", SUM=" + sum + ", AVG=" + avg);
        }

        System.out.println("预期：同一部门的数据在同一分区，便于聚合计算");
    }

    /**
     * 测试8：过滤操作 + 分区
     *
     * SQL示例：
     * SELECT * FROM orders WHERE status = 'completed' AND amount > 100
     *
     * 分区策略：过滤后的结果按订单 ID 哈希分区
     *
     * 数据：
     * orders 表原始数据：5 行
     * status='completed' 且 amount>100: 订单1(250), 订单2(460), 订单5(180) → 3 行
     */
    @Test
    public void testFilterWithPartition() {
        // 创建过滤查询
        JQuickTableScanNode scan = createTableScan("orders");
        JQuickFilterNode filter = createFilter(scan, "status", "=", "completed");
        JQuickProjectNode project = createProject(filter, "order_id", "customer_id", "status", "amount");

        // 获取源数据用于验证
        JQuickDataSet sourceData = JQuickDataSourceManager.getTable("orders");
        System.out.println("=== 过滤操作 + 分区测试 ===");
        System.out.println("源数据总行数: " + sourceData.getRows().size());
        sourceData.printTable();

        // 过滤数据用于验证
        List<JQuickRow> filteredRows = new ArrayList<>();
        for (JQuickRow row : sourceData.getRows()) {
            String status = (String) row.get("status");
            Double amount = (Double) row.get("amount");
            if ("completed".equals(status) && amount > 100) {
                filteredRows.add(row);
            }
        }

        // 生成物理计划
        JQuickPhysicalPlanNode physicalPlan = generator.generate(project);

        // 分区
        JQuickDistributedPlan distributedPlan = fragmenter.fragment(physicalPlan);

        // 验证分区结果
        System.out.println("\n=== 分区结果 ===");
        fragmenter.printFragments(distributedPlan);
        System.out.println("分区数量: " + distributedPlan.getDefaultParallelism());
        System.out.println("分区策略: HASH (按 order_id)");

        System.out.println("\n=== 预期过滤结果（status='completed' AND amount>100）===");
        System.out.println("过滤后行数: " + filteredRows.size());
        for (JQuickRow row : filteredRows) {
            System.out.println("order_id=" + row.get("order_id") + 
                ", customer_id=" + row.get("customer_id") + 
                ", status=" + row.get("status") + 
                ", amount=" + row.get("amount"));
        }

        System.out.println("预期：过滤后的数据按 order_id 哈希分区");
    }
}
