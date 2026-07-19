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
import com.github.paohaijiao.fragment.JQuickFragmenter;
import com.github.paohaijiao.logic.JQuickLogicalPlanNode;
import com.github.paohaijiao.logic.domain.JQuickTableScanNode;
import com.github.paohaijiao.logic2physical.JQuickPhysicalPlanGenerator;
import com.github.paohaijiao.physical.JQuickPhysicalPlanNode;
import com.github.paohaijiao.physical.node.JQuickLimitPhysicalNode;
import com.github.paohaijiao.physical.node.JQuickSortPhysicalNode;
import com.github.paohaijiao.physical.node.JQuickTopNPhysicalNode;
import com.github.paohaijiao.statement.JQuickColumnMeta;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * JQuickSort/Limit/TopN 物理节点单元测试
 * 
 * 测试范围：
 * 1. JQuickSortPhysicalNode - 排序操作
 *    - 升序排序
 *    - 降序排序
 *    - 多列排序
 *    - NULL 值处理
 * 
 * 2. JQuickLimitPhysicalNode - 限制操作
 *    - 基本 LIMIT
 *    - 带 OFFSET 的 LIMIT
 *    - OFFSET 超过数据量
 * 
 * 3. JQuickTopNPhysicalNode - TopN 操作
 *    - 基本 TopN（排序 + LIMIT）
 *    - 带 OFFSET 的 TopN
 *    - 升序/降序 TopN
 *    - 多列排序 TopN
 */
@Slf4j
public class JQuickSortLimitTopNPhysicalNodeTest {

    private static final String TABLE_USERS = "users";

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
                new JQuickColumnMeta("age", Integer.class, TABLE_USERS),
                new JQuickColumnMeta("salary", Double.class, TABLE_USERS),
                new JQuickColumnMeta("department", String.class, TABLE_USERS),
                new JQuickColumnMeta("bonus", Double.class, TABLE_USERS)
        );
        List<JQuickRow> userRows = Arrays.asList(
                createRow("id", 1, "name", "Alice", "age", 25, "salary", 5000.0, "department", "Engineering", "bonus", 1000.0),
                createRow("id", 2, "name", "Bob", "age", 30, "salary", 6000.0, "department", "Marketing", "bonus", 800.0),
                createRow("id", 3, "name", "Charlie", "age", 20, "salary", 4500.0, "department", "Engineering", "bonus", 500.0),
                createRow("id", 4, "name", "David", "age", 35, "salary", 7000.0, "department", "Sales", "bonus", 1500.0),
                createRow("id", 5, "name", "Eve", "age", 28, "salary", 5500.0, "department", "Marketing", "bonus", 600.0),
                createRow("id", 6, "name", "Frank", "age", 40, "salary", 8000.0, "department", "Engineering", "bonus", 2000.0),
                createRow("id", 7, "name", "Grace", "age", 22, "salary", 4000.0, "department", "Sales", "bonus", 400.0),
                createRow("id", 8, "name", "Henry", "age", 32, "salary", 6500.0, "department", "Marketing", "bonus", 900.0),
                createRow("id", 9, "name", "Ivy", "age", 27, "salary", 5200.0, "department", "Engineering", "bonus", 700.0),
                createRow("id", 10, "name", "Jack", "age", 38, "salary", 7500.0, "department", "Sales", "bonus", 1200.0),
                createRow("id", 11, "name", "Kate", "age", 24, "salary", 4800.0, "department", "Marketing", "bonus", 550.0),
                createRow("id", 12, "name", "Leo", "age", 33, "salary", 6200.0, "department", "Engineering", "bonus", 1100.0),
                createRow("id", 13, "name", "Mike", "age", 26, "salary", 4900.0, "department", "Sales", "bonus", null),
                createRow("id", 14, "name", "Nancy", "age", 31, "salary", 6300.0, "department", "Marketing", "bonus", 850.0),
                createRow("id", 15, "name", "Oliver", "age", 36, "salary", 7200.0, "department", "Engineering", "bonus", 1300.0)
        );
        JQuickDataSet usersData = new JQuickDataSet(userColumns, userRows);
        JQuickDataSourceManager.registerTable(TABLE_USERS, usersData);
    }

    private JQuickRow createRow(Object... keyValuePairs) {
        JQuickRow row = new JQuickRow();
        for (int i = 0; i < keyValuePairs.length; i += 2) {
            row.put(keyValuePairs[i].toString(), keyValuePairs[i + 1]);
        }
        return row;
    }

    private JQuickTableScanNode createTableScan(String tableName, String alias) {
        return new JQuickTableScanNode(tableName, alias);
    }

    private JQuickPhysicalPlanNode buildSortPlan(JQuickTableScanNode scan, List<JQuickSortPhysicalNode.OrderByItem> orderByItems) {
        JQuickPhysicalPlanNode physicalPlan = generator.generate(scan);
        return new JQuickSortPhysicalNode(orderByItems, physicalPlan);
    }

    private JQuickPhysicalPlanNode buildLimitPlan(JQuickPhysicalPlanNode child, int limit, int offset) {
        return new JQuickLimitPhysicalNode(limit, offset, child);
    }

    private JQuickPhysicalPlanNode buildTopNPlan(JQuickTableScanNode scan, List<JQuickSortPhysicalNode.OrderByItem> orderByItems, int limit, int offset) {
        JQuickPhysicalPlanNode physicalPlan = generator.generate(scan);
        return new JQuickTopNPhysicalNode(orderByItems, limit, offset, physicalPlan);
    }

    private void executeAndPrint(JQuickPhysicalPlanNode plan, String testName) {
        JQuickDistributedPlan distributedPlan = new JQuickFragmenter(1).fragment(plan);
        String queryId = testName + "_" + System.currentTimeMillis();
        JQuickDataSet result = coordinator.executeQueryWithPlan(queryId, distributedPlan);
        console.info("=== " + testName + " ===");
        result.printTable();
    }


    /**
     * 测试升序排序
     *
     * SQL示例：SELECT id, name, age FROM users ORDER BY age ASC
     */
    @Test
    public void testSortAscending() {
        JQuickTableScanNode scan = createTableScan(TABLE_USERS, "u");
        List<JQuickSortPhysicalNode.OrderByItem> orderByItems = Arrays.asList(
                new JQuickSortPhysicalNode.OrderByItem("age", true)
        );
        JQuickPhysicalPlanNode plan = buildSortPlan(scan, orderByItems);
        JQuickDistributedPlan distributedPlan = new JQuickFragmenter(1).fragment(plan);
        String queryId="order_001";
        JQuickDataSet result = coordinator.executeQueryWithPlan(queryId, distributedPlan);
        result.printTable();
    }

    /**
     * 测试降序排序
     *
     * SQL示例：SELECT id, name, salary FROM users ORDER BY salary DESC
     */
    @Test
    public void testSortDescending() {
        JQuickTableScanNode scan = createTableScan(TABLE_USERS, "u");
        List<JQuickSortPhysicalNode.OrderByItem> orderByItems = Arrays.asList(
                new JQuickSortPhysicalNode.OrderByItem("salary", false)
        );
        JQuickPhysicalPlanNode plan = buildSortPlan(scan, orderByItems);
        JQuickDistributedPlan distributedPlan = new JQuickFragmenter(1).fragment(plan);
        String queryId="order_002";
        JQuickDataSet result = coordinator.executeQueryWithPlan(queryId, distributedPlan);
        result.printTable();
    }

    /**
     * 测试多列排序
     *
     * SQL示例：SELECT id, name, department, salary FROM users ORDER BY department ASC, salary DESC
     */
    @Test
    public void testSortMultipleColumns() {
        JQuickTableScanNode scan = createTableScan(TABLE_USERS, "u");
        List<JQuickSortPhysicalNode.OrderByItem> orderByItems = Arrays.asList(
                new JQuickSortPhysicalNode.OrderByItem("department", true),
                new JQuickSortPhysicalNode.OrderByItem("salary", false)
        );
        JQuickPhysicalPlanNode plan = buildSortPlan(scan, orderByItems);
        JQuickDistributedPlan distributedPlan = new JQuickFragmenter(1).fragment(plan);
        String queryId="order_003";
        JQuickDataSet result = coordinator.executeQueryWithPlan(queryId, distributedPlan);
        result.printTable();
    }

    /**
     * 测试包含 NULL 值的排序
     *
     * SQL示例：SELECT id, name, bonus FROM users ORDER BY bonus ASC
     */
    @Test
    public void testSortWithNulls() {
        JQuickTableScanNode scan = createTableScan(TABLE_USERS, "u");
        List<JQuickSortPhysicalNode.OrderByItem> orderByItems = Arrays.asList(
                new JQuickSortPhysicalNode.OrderByItem("bonus", true, true)
        );
        JQuickPhysicalPlanNode plan = buildSortPlan(scan, orderByItems);
        JQuickDistributedPlan distributedPlan = new JQuickFragmenter(1).fragment(plan);
        String queryId="order_004";
        JQuickDataSet result = coordinator.executeQueryWithPlan(queryId, distributedPlan);
        result.printTable();
    }


    /**
     * 测试基本 LIMIT
     *
     * SQL示例：SELECT id, name FROM users LIMIT 5
     */
    @Test
    public void testLimitBasic() {
        JQuickTableScanNode scan = createTableScan(TABLE_USERS, "u");
        JQuickPhysicalPlanNode physicalPlan = generator.generate(scan);
        JQuickPhysicalPlanNode plan = buildLimitPlan(physicalPlan, 5, 0);
        JQuickDistributedPlan distributedPlan = new JQuickFragmenter(1).fragment(plan);
        String queryId="limit_001";
        JQuickDataSet result = coordinator.executeQueryWithPlan(queryId, distributedPlan);
        result.printTable();
    }

    /**
     * 测试带 OFFSET 的 LIMIT
     *
     * SQL示例：SELECT id, name FROM users LIMIT 5 OFFSET 3
     */
    @Test
    public void testLimitWithOffset() {
        JQuickTableScanNode scan = createTableScan(TABLE_USERS, "u");
        JQuickPhysicalPlanNode physicalPlan = generator.generate(scan);
        JQuickPhysicalPlanNode plan = buildLimitPlan(physicalPlan, 5, 3);
        JQuickDistributedPlan distributedPlan = new JQuickFragmenter(1).fragment(plan);
        String queryId="limit_002";
        JQuickDataSet result = coordinator.executeQueryWithPlan(queryId, distributedPlan);
        result.printTable();
    }

    /**
     * 测试 OFFSET 超过数据量
     *
     * SQL示例：SELECT id, name FROM users LIMIT 5 OFFSET 100
     */
    @Test
    public void testLimitOffsetExceedsData() {
        JQuickTableScanNode scan = createTableScan(TABLE_USERS, "u");
        JQuickPhysicalPlanNode physicalPlan = generator.generate(scan);
        JQuickPhysicalPlanNode plan = buildLimitPlan(physicalPlan, 5, 100);
        JQuickDistributedPlan distributedPlan = new JQuickFragmenter(1).fragment(plan);
        String queryId="limit_002";
        JQuickDataSet result = coordinator.executeQueryWithPlan(queryId, distributedPlan);
        result.printTable();
    }

    /**
     * 测试 LIMIT 超过数据量
     *
     * SQL示例：SELECT id, name FROM users LIMIT 100
     */
    @Test
    public void testLimitExceedsData() {
        JQuickTableScanNode scan = createTableScan(TABLE_USERS, "u");
        JQuickPhysicalPlanNode physicalPlan = generator.generate(scan);
        JQuickPhysicalPlanNode plan = buildLimitPlan(physicalPlan, 100, 0);
        JQuickDistributedPlan distributedPlan = new JQuickFragmenter(1).fragment(plan);
        String queryId="limit_003";
        JQuickDataSet result = coordinator.executeQueryWithPlan(queryId, distributedPlan);
        result.printTable();
    }

    /**
     * 测试基本 TopN（排序 + LIMIT）
     *
     * SQL示例：SELECT id, name, salary FROM users ORDER BY salary DESC LIMIT 3
     */
    @Test
    public void testTopNDescending() {
        JQuickTableScanNode scan = createTableScan(TABLE_USERS, "u");
        List<JQuickSortPhysicalNode.OrderByItem> orderByItems = Arrays.asList(
                new JQuickSortPhysicalNode.OrderByItem("salary", false)
        );
        JQuickPhysicalPlanNode plan = buildTopNPlan(scan, orderByItems, 3, 0);
        JQuickDistributedPlan distributedPlan = new JQuickFragmenter(1).fragment(plan);
        String queryId="limit_003";
        JQuickDataSet result = coordinator.executeQueryWithPlan(queryId, distributedPlan);
        result.printTable();
    }

    /**
     * 测试升序 TopN
     *
     * SQL示例：SELECT id, name, age FROM users ORDER BY age ASC LIMIT 3
     */
    @Test
    public void testTopNAscending() {
        JQuickTableScanNode scan = createTableScan(TABLE_USERS, "u");
        List<JQuickSortPhysicalNode.OrderByItem> orderByItems = Arrays.asList(
                new JQuickSortPhysicalNode.OrderByItem("age", true)
        );
        JQuickPhysicalPlanNode plan = buildTopNPlan(scan, orderByItems, 3, 0);
        JQuickDistributedPlan distributedPlan = new JQuickFragmenter(1).fragment(plan);
        String queryId="limit_004";
        JQuickDataSet result = coordinator.executeQueryWithPlan(queryId, distributedPlan);
        result.printTable();
    }

    /**
     * 测试带 OFFSET 的 TopN
     *
     * SQL示例：SELECT id, name, salary FROM users ORDER BY salary DESC LIMIT 3 OFFSET 2
     */
    @Test
    public void testTopNWithOffset() {
        JQuickTableScanNode scan = createTableScan(TABLE_USERS, "u");
        List<JQuickSortPhysicalNode.OrderByItem> orderByItems = Arrays.asList(
                new JQuickSortPhysicalNode.OrderByItem("salary", false)
        );
        JQuickPhysicalPlanNode plan = buildTopNPlan(scan, orderByItems, 3, 2);
        executeAndPrint(plan, "testTopNWithOffset");
    }

    /**
     * 测试多列排序 TopN
     *
     * SQL示例：SELECT id, name, department, salary FROM users ORDER BY department ASC, salary DESC LIMIT 4
     */
    @Test
    public void testTopNMultipleColumns() {
        JQuickTableScanNode scan = createTableScan(TABLE_USERS, "u");
        List<JQuickSortPhysicalNode.OrderByItem> orderByItems = Arrays.asList(
                new JQuickSortPhysicalNode.OrderByItem("department", true),
                new JQuickSortPhysicalNode.OrderByItem("salary", false)
        );
        JQuickPhysicalPlanNode plan = buildTopNPlan(scan, orderByItems, 4, 0);
        JQuickDistributedPlan distributedPlan = new JQuickFragmenter(1).fragment(plan);
        String queryId="order_003";
        JQuickDataSet result = coordinator.executeQueryWithPlan(queryId, distributedPlan);
        result.printTable();
    }

    /**
     * 测试 TopN 边界条件（OFFSET 超过数据量）
     *
     * SQL示例：SELECT id, name, salary FROM users ORDER BY salary DESC LIMIT 3 OFFSET 100
     */
    @Test
    public void testTopNOffsetExceedsData() {
        JQuickTableScanNode scan = createTableScan(TABLE_USERS, "u");
        List<JQuickSortPhysicalNode.OrderByItem> orderByItems = Arrays.asList(
                new JQuickSortPhysicalNode.OrderByItem("salary", false)
        );
        JQuickPhysicalPlanNode plan = buildTopNPlan(scan, orderByItems, 3, 100);
        JQuickDistributedPlan distributedPlan = new JQuickFragmenter(1).fragment(plan);
        String queryId="limit_004";
        JQuickDataSet result = coordinator.executeQueryWithPlan(queryId, distributedPlan);
        result.printTable();
    }

    /**
     * 测试 Sort + Limit 的组合（优化器会转换为 TopN）
     *
     * SQL示例：SELECT id, name, age FROM users ORDER BY age ASC LIMIT 5 OFFSET 2
     */
    @Test
    public void testSortThenLimit() {
        JQuickTableScanNode scan = createTableScan(TABLE_USERS, "u");
        JQuickPhysicalPlanNode physicalPlan = generator.generate(scan);
        List<JQuickSortPhysicalNode.OrderByItem> orderByItems = Arrays.asList(
                new JQuickSortPhysicalNode.OrderByItem("age", true)
        );
        JQuickSortPhysicalNode sort = new JQuickSortPhysicalNode(orderByItems, physicalPlan);
        JQuickLimitPhysicalNode limit = new JQuickLimitPhysicalNode(5, 2, sort);
        JQuickDistributedPlan distributedPlan = new JQuickFragmenter(1).fragment(limit);
        String queryId="limit_005";
        JQuickDataSet result = coordinator.executeQueryWithPlan(queryId, distributedPlan);
        result.printTable();
    }

    /**
     * 测试 Sort + Limit（降序）
     *
     * SQL示例：SELECT id, name, salary FROM users ORDER BY salary DESC LIMIT 5
     */
    @Test
    public void testSortDescendingThenLimit() {
        JQuickTableScanNode scan = createTableScan(TABLE_USERS, "u");
        JQuickPhysicalPlanNode physicalPlan = generator.generate(scan);
        List<JQuickSortPhysicalNode.OrderByItem> orderByItems = Arrays.asList(
                new JQuickSortPhysicalNode.OrderByItem("salary", false)
        );
        JQuickSortPhysicalNode sort = new JQuickSortPhysicalNode(orderByItems, physicalPlan);
        JQuickLimitPhysicalNode limit = new JQuickLimitPhysicalNode(5, 0, sort);
        JQuickDistributedPlan distributedPlan = new JQuickFragmenter(1).fragment(limit);
        String queryId="limit_006";
        JQuickDataSet result = coordinator.executeQueryWithPlan(queryId, distributedPlan);
        result.printTable();
    }
}
