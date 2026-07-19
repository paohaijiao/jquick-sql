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
import com.github.paohaijiao.enums.JQuickJoinType;
import com.github.paohaijiao.expression.domain.JQuickColumnRefExpression;
import com.github.paohaijiao.fragment.JQuickFragmenter;
import com.github.paohaijiao.logic.domain.JQuickTableScanNode;
import com.github.paohaijiao.logic2physical.JQuickPhysicalPlanGenerator;
import com.github.paohaijiao.physical.JQuickPhysicalPlanNode;
import com.github.paohaijiao.physical.node.JQuickHashJoinPhysicalNode;
import com.github.paohaijiao.statement.JQuickColumnMeta;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

/**
 * JQuickHashJoinPhysicalNode 单元测试
 * 
 * 测试范围：
 * 1. INNER JOIN - 内连接
 * 2. LEFT JOIN - 左连接
 * 3. RIGHT JOIN - 右连接
 * 4. FULL JOIN - 全连接
 * 5. CROSS JOIN - 笛卡尔积
 * 6. 多列连接键
 * 7. 带额外条件的 JOIN
 */
@Slf4j
public class JQuickHashJoinPhysicalNodeTest {

    private static final String TABLE_USERS = "users";
    private static final String TABLE_DEPARTMENTS = "departments";
    private static final String TABLE_ORDERS = "orders";

    private static final int WORKER1_PORT = 19001;
    private static final int WORKER2_PORT = 19002;
    private static final int WORKER3_PORT = 19003;

    private static JConsole console;
    private static JQuickDataConverter dataConverter;

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
        JQuickDataSourceManager.clearAll();
        registerTestData();
        console.info("测试数据已注册到数据源管理器");
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
        JQuickSqlConfig config = new JQuickSqlConfig();
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

    private void registerTestData() {
        List<JQuickColumnMeta> userColumns = Arrays.asList(
                new JQuickColumnMeta("id", Integer.class, TABLE_USERS),
                new JQuickColumnMeta("name", String.class, TABLE_USERS),
                new JQuickColumnMeta("department_id", Integer.class, TABLE_USERS),
                new JQuickColumnMeta("salary", Double.class, TABLE_USERS),
                new JQuickColumnMeta("city", String.class, TABLE_USERS)
        );
        List<JQuickRow> userRows = Arrays.asList(
                createRow("id", 1, "name", "Alice", "department_id", 1, "salary", 5000.0, "city", "Beijing"),
                createRow("id", 2, "name", "Bob", "department_id", 2, "salary", 6000.0, "city", "Shanghai"),
                createRow("id", 3, "name", "Charlie", "department_id", 1, "salary", 4500.0, "city", "Beijing"),
                createRow("id", 4, "name", "David", "department_id", 3, "salary", 7000.0, "city", "Guangzhou"),
                createRow("id", 5, "name", "Eve", "department_id", null, "salary", 5500.0, "city", "Shenzhen"),
                createRow("id", 6, "name", "Frank", "department_id", 2, "salary", 8000.0, "city", "Shanghai"),
                createRow("id", 7, "name", "Grace", "department_id", 4, "salary", 4000.0, "city", "Hangzhou"),
                createRow("id", 8, "name", "Henry", "department_id", 1, "salary", 6500.0, "city", "Beijing")
        );
        JQuickDataSet usersData = new JQuickDataSet(userColumns, userRows);
        JQuickDataSourceManager.registerTable(TABLE_USERS, usersData);

        List<JQuickColumnMeta> deptColumns = Arrays.asList(
                new JQuickColumnMeta("id", Integer.class, TABLE_DEPARTMENTS),
                new JQuickColumnMeta("name", String.class, TABLE_DEPARTMENTS),
                new JQuickColumnMeta("location", String.class, TABLE_DEPARTMENTS),
                new JQuickColumnMeta("city", String.class, TABLE_DEPARTMENTS)
        );
        List<JQuickRow> deptRows = Arrays.asList(
                createRow("id", 1, "name", "Engineering", "location", "Building A", "city", "Beijing"),
                createRow("id", 2, "name", "Marketing", "location", "Building B", "city", "Shanghai"),
                createRow("id", 3, "name", "Sales", "location", "Building C", "city", "Guangzhou"),
                createRow("id", 5, "name", "HR", "location", "Building D", "city", "Chengdu")
        );
        JQuickDataSet deptData = new JQuickDataSet(deptColumns, deptRows);
        JQuickDataSourceManager.registerTable(TABLE_DEPARTMENTS, deptData);

        List<JQuickColumnMeta> orderColumns = Arrays.asList(
                new JQuickColumnMeta("order_id", Integer.class, TABLE_ORDERS),
                new JQuickColumnMeta("user_id", Integer.class, TABLE_ORDERS),
                new JQuickColumnMeta("amount", Double.class, TABLE_ORDERS),
                new JQuickColumnMeta("city", String.class, TABLE_ORDERS)
        );
        List<JQuickRow> orderRows = Arrays.asList(
                createRow("order_id", 101, "user_id", 1, "amount", 100.0, "city", "Beijing"),
                createRow("order_id", 102, "user_id", 1, "amount", 200.0, "city", "Beijing"),
                createRow("order_id", 103, "user_id", 2, "amount", 150.0, "city", "Shanghai"),
                createRow("order_id", 104, "user_id", 3, "amount", 300.0, "city", "Beijing"),
                createRow("order_id", 105, "user_id", 5, "amount", 250.0, "city", "Shenzhen"),
                createRow("order_id", 106, "user_id", 9, "amount", 180.0, "city", "Tianjin")
        );
        JQuickDataSet orderData = new JQuickDataSet(orderColumns, orderRows);
        JQuickDataSourceManager.registerTable(TABLE_ORDERS, orderData);
    }

    private JQuickRow createRow(Object... keyValuePairs) {
        JQuickRow row = new JQuickRow();
        for (int i = 0; i < keyValuePairs.length; i += 2) {
            row.put(keyValuePairs[i].toString(), keyValuePairs[i + 1]);
        }
        return row;
    }

    private JQuickPhysicalPlanNode buildJoinPlan(
            String leftTable, String rightTable,
            JQuickJoinType joinType,
            List<JQuickHashJoinPhysicalNode.JoinKeyPair> joinKeys,
            JQuickHashJoinPhysicalNode.BuildSide buildSide,
            JQuickHashJoinPhysicalNode.JoinDistribution distribution) {
        JQuickTableScanNode leftScan = new JQuickTableScanNode(leftTable, "l");
        JQuickTableScanNode rightScan = new JQuickTableScanNode(rightTable, "r");
        JQuickPhysicalPlanNode leftPlan = generator.generate(leftScan);
        JQuickPhysicalPlanNode rightPlan = generator.generate(rightScan);
        return new JQuickHashJoinPhysicalNode(joinType, leftPlan, rightPlan, null, joinKeys, buildSide, distribution);
    }

    private void executeAndPrint(JQuickPhysicalPlanNode plan, String testName) {
        JQuickDistributedPlan distributedPlan = new JQuickFragmenter(1).fragment(plan);
        String queryId = testName + "_" + System.currentTimeMillis();
        JQuickDataSet result = coordinator.executeQueryWithPlan(queryId, distributedPlan);
        console.info("=== " + testName + " ===");
        result.printTable();
    }

    /**
     * 测试 INNER JOIN（内连接）
     *
     * SQL示例：SELECT u.id, u.name, d.name as dept_name 
     *          FROM users u INNER JOIN departments d ON u.department_id = d.id
     */
    @Test
    public void testInnerJoin() {
        List<JQuickHashJoinPhysicalNode.JoinKeyPair> joinKeys = Arrays.asList(
                new JQuickHashJoinPhysicalNode.JoinKeyPair(
                        new JQuickColumnRefExpression("department_id"),
                        new JQuickColumnRefExpression("id")
                )
        );
        JQuickPhysicalPlanNode physicalPlan = buildJoinPlan(
                TABLE_USERS, TABLE_DEPARTMENTS,
                JQuickJoinType.INNER,
                joinKeys,
                JQuickHashJoinPhysicalNode.BuildSide.RIGHT,
                JQuickHashJoinPhysicalNode.JoinDistribution.LOCAL
        );
        JQuickDistributedPlan plan = new JQuickFragmenter(1).fragment(physicalPlan);
        String queryId = "single_phase_test_" + System.currentTimeMillis();
        JQuickDataSet result = coordinator.executeQueryWithPlan(queryId, plan);
        result.printTable();
    }

    /**
     * 测试 LEFT JOIN（左连接）
     *
     * SQL示例：SELECT u.id, u.name, d.name as dept_name 
     *          FROM users u LEFT JOIN departments d ON u.department_id = d.id
     */
    @Test
    public void testLeftJoin() {
        List<JQuickHashJoinPhysicalNode.JoinKeyPair> joinKeys = Arrays.asList(
                new JQuickHashJoinPhysicalNode.JoinKeyPair(
                        new JQuickColumnRefExpression("department_id"),
                        new JQuickColumnRefExpression("id")
                )
        );
        JQuickPhysicalPlanNode plan = buildJoinPlan(
                TABLE_USERS, TABLE_DEPARTMENTS,
                JQuickJoinType.LEFT,
                joinKeys,
                JQuickHashJoinPhysicalNode.BuildSide.RIGHT,
                JQuickHashJoinPhysicalNode.JoinDistribution.LOCAL
        );
        executeAndPrint(plan, "testLeftJoin");
    }

    /**
     * 测试 RIGHT JOIN（右连接）
     *
     * SQL示例：SELECT u.id, u.name, d.name as dept_name 
     *          FROM users u RIGHT JOIN departments d ON u.department_id = d.id
     */
    @Test
    public void testRightJoin() {
        List<JQuickHashJoinPhysicalNode.JoinKeyPair> joinKeys = Arrays.asList(
                new JQuickHashJoinPhysicalNode.JoinKeyPair(
                        new JQuickColumnRefExpression("department_id"),
                        new JQuickColumnRefExpression("id")
                )
        );
        JQuickPhysicalPlanNode plan = buildJoinPlan(
                TABLE_USERS, TABLE_DEPARTMENTS,
                JQuickJoinType.RIGHT,
                joinKeys,
                JQuickHashJoinPhysicalNode.BuildSide.LEFT,
                JQuickHashJoinPhysicalNode.JoinDistribution.LOCAL
        );
        executeAndPrint(plan, "testRightJoin");
    }

    /**
     * 测试 FULL JOIN（全连接）
     *
     * SQL示例：SELECT u.id, u.name, d.name as dept_name 
     *          FROM users u FULL JOIN departments d ON u.department_id = d.id
     */
    @Test
    public void testFullJoin() {
        List<JQuickHashJoinPhysicalNode.JoinKeyPair> joinKeys = Arrays.asList(
                new JQuickHashJoinPhysicalNode.JoinKeyPair(
                        new JQuickColumnRefExpression("department_id"),
                        new JQuickColumnRefExpression("id")
                )
        );
        JQuickPhysicalPlanNode plan = buildJoinPlan(
                TABLE_USERS, TABLE_DEPARTMENTS,
                JQuickJoinType.FULL,
                joinKeys,
                JQuickHashJoinPhysicalNode.BuildSide.RIGHT,
                JQuickHashJoinPhysicalNode.JoinDistribution.LOCAL
        );
        executeAndPrint(plan, "testFullJoin");
    }

    /**
     * 测试 CROSS JOIN（笛卡尔积）
     *
     * SQL示例：SELECT u.id, u.name, d.name as dept_name 
     *          FROM users u CROSS JOIN departments d
     */
    @Test
    public void testCrossJoin() {
        JQuickPhysicalPlanNode plan = buildJoinPlan(
                TABLE_USERS, TABLE_DEPARTMENTS,
                JQuickJoinType.CROSS,
                null,
                JQuickHashJoinPhysicalNode.BuildSide.RIGHT,
                JQuickHashJoinPhysicalNode.JoinDistribution.LOCAL
        );
        executeAndPrint(plan, "testCrossJoin");
    }

    /**
     * 测试多列连接键
     *
     * SQL示例：SELECT u.id, u.name, o.order_id, o.amount 
     *          FROM users u JOIN orders o ON u.id = o.user_id AND u.city = o.city
     */
    @Test
    public void testMultiColumnJoinKey() {
        List<JQuickHashJoinPhysicalNode.JoinKeyPair> joinKeys = Arrays.asList(
                new JQuickHashJoinPhysicalNode.JoinKeyPair(
                        new JQuickColumnRefExpression("id"),
                        new JQuickColumnRefExpression("user_id")
                ),
                new JQuickHashJoinPhysicalNode.JoinKeyPair(
                        new JQuickColumnRefExpression("city"),
                        new JQuickColumnRefExpression("city")
                )
        );
        JQuickPhysicalPlanNode plan = buildJoinPlan(
                TABLE_USERS, TABLE_ORDERS,
                JQuickJoinType.INNER,
                joinKeys,
                JQuickHashJoinPhysicalNode.BuildSide.RIGHT,
                JQuickHashJoinPhysicalNode.JoinDistribution.LOCAL
        );
        executeAndPrint(plan, "testMultiColumnJoinKey");
    }

    /**
     * 测试 NULL 值处理（LEFT JOIN）
     *
     * SQL示例：SELECT u.id, u.name, d.name as dept_name 
     *          FROM users u LEFT JOIN departments d ON u.department_id = d.id
     *          WHERE u.department_id IS NULL
     */
    @Test
    public void testJoinWithNullKey() {
        List<JQuickHashJoinPhysicalNode.JoinKeyPair> joinKeys = Arrays.asList(
                new JQuickHashJoinPhysicalNode.JoinKeyPair(
                        new JQuickColumnRefExpression("department_id"),
                        new JQuickColumnRefExpression("id")
                )
        );
        JQuickPhysicalPlanNode plan = buildJoinPlan(
                TABLE_USERS, TABLE_DEPARTMENTS,
                JQuickJoinType.LEFT,
                joinKeys,
                JQuickHashJoinPhysicalNode.BuildSide.RIGHT,
                JQuickHashJoinPhysicalNode.JoinDistribution.LOCAL
        );
        executeAndPrint(plan, "testJoinWithNullKey");
    }

    /**
     * 测试 LEFT JOIN（users 和 orders）
     *
     * SQL示例：SELECT u.id, u.name, o.order_id, o.amount 
     *          FROM users u LEFT JOIN orders o ON u.id = o.user_id
     */
    @Test
    public void testLeftJoinUsersOrders() {
        List<JQuickHashJoinPhysicalNode.JoinKeyPair> joinKeys = Arrays.asList(
                new JQuickHashJoinPhysicalNode.JoinKeyPair(
                        new JQuickColumnRefExpression("id"),
                        new JQuickColumnRefExpression("user_id")
                )
        );
        JQuickPhysicalPlanNode plan = buildJoinPlan(
                TABLE_USERS, TABLE_ORDERS,
                JQuickJoinType.LEFT,
                joinKeys,
                JQuickHashJoinPhysicalNode.BuildSide.RIGHT,
                JQuickHashJoinPhysicalNode.JoinDistribution.LOCAL
        );
        executeAndPrint(plan, "testLeftJoinUsersOrders");
    }

    /**
     * 测试 RIGHT JOIN（orders 和 users）
     *
     * SQL示例：SELECT o.order_id, o.amount, u.name 
     *          FROM orders o RIGHT JOIN users u ON o.user_id = u.id
     */
    @Test
    public void testRightJoinOrdersUsers() {
        List<JQuickHashJoinPhysicalNode.JoinKeyPair> joinKeys = Arrays.asList(
                new JQuickHashJoinPhysicalNode.JoinKeyPair(
                        new JQuickColumnRefExpression("user_id"),
                        new JQuickColumnRefExpression("id")
                )
        );
        JQuickPhysicalPlanNode plan = buildJoinPlan(
                TABLE_ORDERS, TABLE_USERS,
                JQuickJoinType.RIGHT,
                joinKeys,
                JQuickHashJoinPhysicalNode.BuildSide.LEFT,
                JQuickHashJoinPhysicalNode.JoinDistribution.LOCAL
        );
        executeAndPrint(plan, "testRightJoinOrdersUsers");
    }

    /**
     * 测试 RIGHT JOIN（users 和 orders，显示右表不匹配的行）
     *
     * SQL示例：SELECT u.id, u.name, o.order_id, o.amount 
     *          FROM users u RIGHT JOIN orders o ON u.id = o.user_id
     */
    @Test
    public void testRightJoinUsersOrders() {
        List<JQuickHashJoinPhysicalNode.JoinKeyPair> joinKeys = Arrays.asList(
                new JQuickHashJoinPhysicalNode.JoinKeyPair(
                        new JQuickColumnRefExpression("id"),
                        new JQuickColumnRefExpression("user_id")
                )
        );
        JQuickPhysicalPlanNode plan = buildJoinPlan(
                TABLE_USERS, TABLE_ORDERS,
                JQuickJoinType.RIGHT,
                joinKeys,
                JQuickHashJoinPhysicalNode.BuildSide.LEFT,
                JQuickHashJoinPhysicalNode.JoinDistribution.LOCAL
        );
        executeAndPrint(plan, "testRightJoinUsersOrders");
    }
}
