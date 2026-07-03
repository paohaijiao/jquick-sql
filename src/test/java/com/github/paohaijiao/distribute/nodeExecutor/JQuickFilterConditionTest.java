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
package com.github.paohaijiao.distribute.nodeExecutor;

import com.github.paohaijiao.config.JQuickSqlConfig;
import com.github.paohaijiao.console.JConsole;
import com.github.paohaijiao.datasource.JQuickDataSourceManager;
import com.github.paohaijiao.distributed.JQuickDistributedPlan;
import com.github.paohaijiao.distributed.coordinator.JQuickCoordinator;
import com.github.paohaijiao.distributed.worker.JQuickDataConverter;
import com.github.paohaijiao.distributed.worker.JQuickWorker;
import com.github.paohaijiao.enums.JQuickBinaryOperator;
import com.github.paohaijiao.enums.JQuickJoinType;
import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.expression.domain.*;
import com.github.paohaijiao.fragment.JQuickFragmenter;
import com.github.paohaijiao.logic.JQuickLogicalPlanNode;
import com.github.paohaijiao.logic.domain.*;
import com.github.paohaijiao.logic2physical.JQuickPhysicalPlanGenerator;
import com.github.paohaijiao.physical.JQuickPhysicalPlanNode;
import com.github.paohaijiao.physical.node.JQuickFilterPhysicalNode;
import com.github.paohaijiao.statement.JQuickColumnMeta;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.*;


/**
 * packageName com.github.paohaijiao.physical
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/5/24
 */
public class JQuickFilterConditionTest {

    private static final String TABLE_USERS = "users";

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

    private JQuickPhysicalPlanGenerator generator;


    private List<JQuickCoordinator.WorkerEndpoint> endpoints;
    private List<JQuickWorker> workers;

    @Before
    public void setUp() throws IOException, InterruptedException {
        console = JConsole.initConsoleEnvironment();
        dataConverter = new JQuickDataConverter();
        console.info("测试环境初始化完成");
        generator = new JQuickPhysicalPlanGenerator();
        // 清理数据源
        JQuickDataSourceManager.clearAll();
        // 注册测试数据到数据源管理器（替代 broadcastTable）
        registerTestData();
        console.info("测试数据已注册到数据源管理器");
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
        worker1.clearReceivedDataCache();
        worker2.clearReceivedDataCache();
        worker3.clearReceivedDataCache();
        endpoints = new ArrayList<>();
        endpoints.add(new JQuickCoordinator.WorkerEndpoint("worker-1", "localhost", WORKER1_PORT, 0));
        endpoints.add(new JQuickCoordinator.WorkerEndpoint("worker-2", "localhost", WORKER2_PORT, 1));
        endpoints.add(new JQuickCoordinator.WorkerEndpoint("worker-3", "localhost", WORKER3_PORT, 2));
        JQuickSqlConfig config=new JQuickSqlConfig();
        config.setWorkers(endpoints);
        coordinator = new JQuickCoordinator(config);
        for (JQuickWorker worker : workers) {
            worker.setWorkerEndpoints(endpoints);
        }
        console.info("测试环境启动完成");
    }
    @After
    public void tearDown() {
        JQuickDataSourceManager.clearAll();
    }
    /**
     * 注册测试数据
     */
    private void registerTestData() {
        List<JQuickColumnMeta> userColumns = Arrays.asList(
                new JQuickColumnMeta("id", Integer.class, TABLE_USERS),
                new JQuickColumnMeta("name", String.class, TABLE_USERS),
                new JQuickColumnMeta("age", Integer.class, TABLE_USERS),
                new JQuickColumnMeta("status", String.class, TABLE_USERS)
        );
        List<JQuickRow> userRows = Arrays.asList(
                createRow("id", 1, "name", "1Alice", "age", 25, "status", "active"),
                createRow("id", 2, "name", "2Bob", "age", 30, "status", "active"),
                createRow("id", 3, "name", "3Charlie", "age", 20, "status", "pending"),
                createRow("id", 4, "name", "4David", "age", 35, "status", "inactive"),
                createRow("id", 5, "name", "5Eve", "age", 28, "status", "active"),
                createRow("id", 6, "name", "6Frank", "age", 22, "status", "pending"),
                createRow("id", 7, "name", "7Grace", "age", 40, "status", "active"),
                createRow("id", 8, "name", "8Henry", "age", 19, "status", "inactive")
        );
        JQuickDataSet usersData = new JQuickDataSet(userColumns, userRows);
        JQuickDataSourceManager.registerTable(TABLE_USERS, usersData);
    }

    /**
     * 创建行的辅助方法
     */
    private JQuickRow createRow(Object... keyValues) {
        JQuickRow row = new JQuickRow();
        for (int i = 0; i < keyValues.length; i += 2) {
            row.put((String) keyValues[i], keyValues[i + 1]);
        }
        return row;
    }

    /**
     * 创建等值条件
     */
    private JQuickBinaryExpression createEqualityCondition(String leftCol, String rightCol) {
        return new JQuickBinaryExpression(
                new JQuickColumnRefExpression(leftCol),
                new JQuickColumnRefExpression(rightCol),
                JQuickBinaryOperator.EQ
        );
    }


    /**
     * 创建表扫描节点（带别名）
     */
    private JQuickTableScanNode createTableScan(String tableName, String alias) {
        return new JQuickTableScanNode(tableName, alias);
    }

    /**
     * 创建简单二元比较条件
     */
    private JQuickBinaryExpression createComparison(String column, JQuickBinaryOperator operator, Object value) {
        return new JQuickBinaryExpression(
                new JQuickColumnRefExpression(column),
                new JQuickLiteralExpression(value),
                operator
        );
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
    private JQuickFilterNode createFilter(JQuickLogicalPlanNode child, JQuickExpression predicate) {
        return new JQuickFilterNode(predicate, child);
    }

    /**
     * 创建 AND 条件
     */
    private JQuickBinaryExpression and(JQuickExpression left, JQuickExpression right) {
        return new JQuickBinaryExpression(left, right, JQuickBinaryOperator.AND);
    }

    /**
     * 创建 OR 条件
     */
    private JQuickBinaryExpression or(JQuickExpression left, JQuickExpression right) {
        return new JQuickBinaryExpression(left, right, JQuickBinaryOperator.OR);
    }


    /**
     * 创建 BETWEEN 条件
     */
    private JQuickBetweenExpression createBetween(String column, Object low, Object high) {
        return new JQuickBetweenExpression(new JQuickColumnRefExpression(column), new JQuickLiteralExpression(low), new JQuickLiteralExpression(high), false);
    }

    /**
     * 创建 IN 条件
     */
    private JQuickInExpression createIn(String column, List<Object> values) {
        List<JQuickExpression> valueExprs = new ArrayList<>();
        for (Object value : values) {
            valueExprs.add(new JQuickLiteralExpression(value));
        }
        return new JQuickInExpression(new JQuickColumnRefExpression(column), valueExprs, false);
    }
    /**
     * 测试3：复合条件 AND
     *
     * SQL示例：SELECT * FROM users WHERE age > 20 AND status = 'active'
     */
    @Test
    public void testAndCondition() {
        JQuickTableScanNode usersScan = createTableScan("users","u");
        JQuickExpression condition1 = createComparison("age", JQuickBinaryOperator.GT, 20);
        JQuickExpression condition2 = createComparison("status", JQuickBinaryOperator.EQ, "active");
        JQuickBinaryExpression andCondition = and(condition1, condition2);
        JQuickFilterNode filterNode = createFilter(usersScan, andCondition);
        JQuickPhysicalPlanNode physicalPlan = generator.generate(filterNode);
        JQuickFilterPhysicalNode filterPhysical = (JQuickFilterPhysicalNode) physicalPlan;
        JQuickExpression actualPredicate = filterPhysical.getPredicate();
        JQuickBinaryExpression binaryExpr = (JQuickBinaryExpression) actualPredicate;
        System.out.println("=== AND条件测试通过 ===");
        System.out.println("条件: age > 18 AND status = 'active'");
        JQuickFragmenter fragmenter = new JQuickFragmenter(1);
        JQuickDistributedPlan plan = fragmenter.fragment(filterPhysical);
        String queryId = "hash_partition_" + System.currentTimeMillis();
        JQuickDataSet result = coordinator.executeQueryWithPlan(queryId, plan);
        result.printTable();

    }
    /**
     * 测试4：复合条件 OR
     *
     * SQL示例：SELECT * FROM users WHERE age > 34 OR status = 'pending'
     */
    @Test
    public void testOrCondition() {
        JQuickTableScanNode usersScan = createTableScan("users","u");
        JQuickExpression condition1 = createComparison("age", JQuickBinaryOperator.GT, 34);
        JQuickExpression condition2 = createComparison("status", JQuickBinaryOperator.EQ, "pending");
        JQuickBinaryExpression orCondition = or(condition1, condition2);
        JQuickFilterNode filterNode = createFilter(usersScan, orCondition);
        JQuickPhysicalPlanNode physicalPlan = generator.generate(filterNode);
        JQuickFilterPhysicalNode filterPhysical = (JQuickFilterPhysicalNode) physicalPlan;
        JQuickExpression actualPredicate = filterPhysical.getPredicate();
        JQuickBinaryExpression binaryExpr = (JQuickBinaryExpression) actualPredicate;
        System.out.println("=== OR条件测试通过 ===");
        System.out.println("条件: age > 18 OR vip = true");
        JQuickFragmenter fragmenter = new JQuickFragmenter(1);
        JQuickDistributedPlan plan = fragmenter.fragment(filterPhysical);
        String queryId = "hash_partition_" + System.currentTimeMillis();
        JQuickDataSet result = coordinator.executeQueryWithPlan(queryId, plan);
        result.printTable();
    }
    /**
     * 测试6：嵌套条件（AND/OR组合）
     *
     * SQL示例：SELECT * FROM users WHERE (age > 25 AND status = 'active') OR name  = '3Charlie'
     */
    @Test
    public void testNestedCondition() {
        JQuickTableScanNode usersScan = createTableScan("users","u");
        JQuickExpression innerAnd = and(createComparison("age", JQuickBinaryOperator.GT, 25), createComparison("status", JQuickBinaryOperator.EQ, "active"));
        JQuickBinaryExpression nestedCondition = or(innerAnd, createComparison("name", JQuickBinaryOperator.EQ, "3Charlie"));
        JQuickFilterNode filterNode = createFilter(usersScan, nestedCondition);
        JQuickPhysicalPlanNode physicalPlan = generator.generate(filterNode);
        JQuickFilterPhysicalNode filterPhysical = (JQuickFilterPhysicalNode) physicalPlan;
        JQuickExpression actualPredicate = filterPhysical.getPredicate();
        JQuickBinaryExpression outerExpr = (JQuickBinaryExpression) actualPredicate;
        JQuickExpression leftChild = outerExpr.getLeft();
        JQuickBinaryExpression innerExpr = (JQuickBinaryExpression) leftChild;
        System.out.println("=== 嵌套条件测试通过 ===");
        System.out.println("条件: (age > 18 AND status = 'active') OR vip = true");
        JQuickFragmenter fragmenter = new JQuickFragmenter(1);
        JQuickDistributedPlan plan = fragmenter.fragment(filterPhysical);
        String queryId = "hash_partition_" + System.currentTimeMillis();
        JQuickDataSet result = coordinator.executeQueryWithPlan(queryId, plan);
        result.printTable();
    }
    /**
     * 测试7：BETWEEN 条件
     *
     * SQL示例：SELECT * FROM users WHERE age BETWEEN 18 AND 65
     */
    @Test
    public void testBetweenCondition() {
        JQuickTableScanNode usersScan = createTableScan("users","u");
        JQuickBetweenExpression betweenCondition = createBetween("age", 22, 30);
        JQuickFilterNode filterNode = createFilter(usersScan, betweenCondition);
        JQuickPhysicalPlanNode physicalPlan = generator.generate(filterNode);
        JQuickFilterPhysicalNode filterPhysical = (JQuickFilterPhysicalNode) physicalPlan;
        JQuickExpression actualPredicate = filterPhysical.getPredicate();
        System.out.println("=== BETWEEN条件测试通过 ===");
        System.out.println("条件: age BETWEEN 18 AND 65");
        JQuickFragmenter fragmenter = new JQuickFragmenter(1);
        JQuickDistributedPlan plan = fragmenter.fragment(filterPhysical);
        String queryId = "hash_partition_" + System.currentTimeMillis();
        JQuickDataSet result = coordinator.executeQueryWithPlan(queryId, plan);
        result.printTable();
    }
    /**
     *
     * SQL示例：
     * WITH RECURSIVE org_hierarchy AS (
     *     SELECT id, name, manager_id, 1 as level
     *     FROM employee
     *     WHERE manager_id IS NULL
     *     UNION ALL
     *     SELECT e.id, e.name, e.manager_id, h.level + 1
     *     FROM employee e
     *     JOIN org_hierarchy h ON e.manager_id = h.id
     * )
     * SELECT id, name, level FROM org_hierarchy
     */
    @Test
    public void testRecursiveCte() {
        JQuickTableScanNode employeeScan = createTableScan("employee","u");
        JQuickBinaryExpression isNullCondition = new JQuickBinaryExpression(new JQuickColumnRefExpression("manager_id"), new JQuickLiteralExpression(null), JQuickBinaryOperator.EQ);
        JQuickFilterNode anchorFilter = new JQuickFilterNode(isNullCondition, employeeScan);

        List<JQuickProjectNode.SelectItem> anchorItems = new ArrayList<>();
        anchorItems.add(new JQuickProjectNode.SelectItem(new JQuickColumnRefExpression("id"), "id"));
        anchorItems.add(new JQuickProjectNode.SelectItem(new JQuickColumnRefExpression("name"), "name"));
        anchorItems.add(new JQuickProjectNode.SelectItem(new JQuickColumnRefExpression("manager_id"), "manager_id"));
        anchorItems.add(new JQuickProjectNode.SelectItem(new JQuickLiteralExpression(1), "level"));
        JQuickProjectNode anchorProject = new JQuickProjectNode(anchorItems, anchorFilter);
        JQuickTableScanNode employeeScanRecursive = createTableScan("employee", "e");
        JQuickTableScanNode cteRef = createTableScan("org_hierarchy", "h");
        JQuickJoinNode recursiveJoin = new JQuickJoinNode(JQuickJoinType.INNER, employeeScanRecursive, cteRef, createEqualityCondition("e.manager_id", "h.id"));
        JQuickBinaryExpression levelPlusOne = new JQuickBinaryExpression(new JQuickColumnRefExpression("h.level"), new JQuickLiteralExpression(1), JQuickBinaryOperator.PLUS);
        List<JQuickProjectNode.SelectItem> recursiveItems = new ArrayList<>();
        recursiveItems.add(new JQuickProjectNode.SelectItem(new JQuickColumnRefExpression("e.id"), "id"));
        recursiveItems.add(new JQuickProjectNode.SelectItem(new JQuickColumnRefExpression("e.name"), "name"));
        recursiveItems.add(new JQuickProjectNode.SelectItem(new JQuickColumnRefExpression("e.manager_id"), "manager_id"));
        recursiveItems.add(new JQuickProjectNode.SelectItem(levelPlusOne, "level"));
        JQuickProjectNode recursiveProject = new JQuickProjectNode(recursiveItems, recursiveJoin);
        List<String> columnNames = Arrays.asList("id", "name", "manager_id", "level");
        JQuickRecursiveUnionNode recursiveUnion = new JQuickRecursiveUnionNode("org_hierarchy", columnNames, anchorProject, recursiveProject, true);
        JQuickTableScanNode cteFinalRef = createTableScan("org_hierarchy","u");
        JQuickProjectNode mainProject = createProject(cteFinalRef, "id", "name", "level");
        Map<String, JQuickLogicalPlanNode> ctes = new LinkedHashMap<>();
        ctes.put("org_hierarchy", recursiveUnion);
        JQuickWithNode withNode = new JQuickWithNode(mainProject, ctes);
        JQuickPhysicalPlanNode physicalPlan = generator.generate(withNode);
        System.out.println(physicalPlan);
        JQuickFragmenter fragmenter = new JQuickFragmenter(1);
        JQuickDistributedPlan plan = fragmenter.fragment(null);
        String queryId = "hash_partition_" + System.currentTimeMillis();
        JQuickDataSet result = coordinator.executeQueryWithPlan(queryId, plan);
        result.printTable();
    }

}
