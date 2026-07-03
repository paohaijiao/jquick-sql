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

import com.github.paohaijiao.ast2logic.JQuickASTToLogicalPlanVisitor;
import com.github.paohaijiao.config.JQuickSqlConfig;
import com.github.paohaijiao.console.JConsole;
import com.github.paohaijiao.datasource.JQuickDataSourceManager;
import com.github.paohaijiao.distributed.JQuickDistributedPlan;
import com.github.paohaijiao.distributed.coordinator.JQuickCoordinator;
import com.github.paohaijiao.distributed.worker.JQuickDataConverter;
import com.github.paohaijiao.distributed.worker.JQuickWorker;
import com.github.paohaijiao.enums.JQuickBinaryOperator;
import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.expression.domain.JQuickBinaryExpression;
import com.github.paohaijiao.expression.domain.JQuickColumnRefExpression;
import com.github.paohaijiao.expression.domain.JQuickLiteralExpression;
import com.github.paohaijiao.fragment.JQuickFragmenter;
import com.github.paohaijiao.logic.JQuickLogicalPlanNode;
import com.github.paohaijiao.logic.domain.JQuickAggregateNode;
import com.github.paohaijiao.logic.domain.JQuickProjectNode;
import com.github.paohaijiao.logic.domain.JQuickTableScanNode;
import com.github.paohaijiao.logic.domain.JQuickWithNode;
import com.github.paohaijiao.logic2physical.JQuickPhysicalPlanGenerator;
import com.github.paohaijiao.physical.JQuickPhysicalPlanNode;
import com.github.paohaijiao.physical.node.JQuickTableScanPhysicalNode;
import com.github.paohaijiao.statement.JQuickColumnMeta;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

import static org.junit.Assert.*;

/**
 * packageName com.github.paohaijiao.physical
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/5/24
 */
public class JQuickSelectStatementTest {
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
            items.add(new JQuickProjectNode.SelectItem(
                    new JQuickColumnRefExpression(col), col));
        }
        return new JQuickProjectNode(items, child);
    }

    /**
     * 创建等值条件
     */
    private JQuickExpression createEqualityCondition(String leftCol, String rightCol) {
        return new JQuickBinaryExpression(new JQuickColumnRefExpression(leftCol), new JQuickColumnRefExpression(rightCol), JQuickBinaryOperator.EQ);
    }
    /**
     * SQL: SELECT * FROM users AS u
     *
     * 逻辑：全表扫描，返回所有列
     */
    @Test
    public void testTableScanConversion() {
        JQuickTableScanNode logicalScan = new JQuickTableScanNode("users", "u");
        JQuickPhysicalPlanNode physicalPlan = generator.generate(logicalScan);
        assertNotNull(physicalPlan);
        assertEquals("TableScan", physicalPlan.getNodeType());
        assertTrue(physicalPlan instanceof JQuickTableScanPhysicalNode);
        JQuickTableScanPhysicalNode scanNode = (JQuickTableScanPhysicalNode) physicalPlan;
        assertEquals("users", scanNode.getTableName());
        assertEquals("u", scanNode.getAlias());
        JQuickFragmenter fragmenter = new JQuickFragmenter(1);
        JQuickDistributedPlan plan = fragmenter.fragment(scanNode);
        String queryId = "hash_partition_" + System.currentTimeMillis();
        JQuickDataSet result = coordinator.executeQueryWithPlan(queryId, plan);
        result.printTable();
    }
    /**
     * SQL: SELECT id, name, email FROM users AS u
     *
     * 逻辑：列裁剪优化，只读取需要的列
     */
    @Test
    public void testTableScanWithColumnPruning() {
        Set<String> requiredCols = new HashSet<>(Arrays.asList("id", "name", "age"));
        JQuickTableScanNode logicalScan = new JQuickTableScanNode("users", "u", requiredCols);
        JQuickPhysicalPlanNode physicalPlan = generator.generate(logicalScan);
        JQuickTableScanPhysicalNode scanNode = (JQuickTableScanPhysicalNode) physicalPlan;
        assertEquals(requiredCols, scanNode.getRequiredColumns());
        assertEquals(3, scanNode.getOutputSchema().size());
        JQuickFragmenter fragmenter = new JQuickFragmenter(1);
        JQuickDistributedPlan plan = fragmenter.fragment(scanNode);
        String queryId = "hash_partition_" + System.currentTimeMillis();
        JQuickDataSet result = coordinator.executeQueryWithPlan(queryId, plan);
        result.printTable();
    }
    @Test
    public void testTableScanWithColumnPruningWithAlias() {
        Set<String> requiredCols = new HashSet<>(Arrays.asList("u.id", "u.name", "u.age"));
        JQuickTableScanNode logicalScan = new JQuickTableScanNode("users", "u", requiredCols);
        JQuickPhysicalPlanNode physicalPlan = generator.generate(logicalScan);
        JQuickTableScanPhysicalNode scanNode = (JQuickTableScanPhysicalNode) physicalPlan;
        assertEquals(requiredCols, scanNode.getRequiredColumns());
        assertEquals(3, scanNode.getOutputSchema().size());
        JQuickFragmenter fragmenter = new JQuickFragmenter(1);
        JQuickDistributedPlan plan = fragmenter.fragment(scanNode);
        String queryId = "hash_partition_" + System.currentTimeMillis();
        JQuickDataSet result = coordinator.executeQueryWithPlan(queryId, plan);
        result.printTable();
    }
    /**
     * SQL: SELECT * FROM users WHERE age > 18
     *
     * 逻辑：表扫描时下推过滤条件（谓词下推优化）
     */
    @Test
    public void testTableScanWithFilterPredicate() {
        JQuickExpression filter = new JQuickBinaryExpression(new JQuickColumnRefExpression("age"), new JQuickLiteralExpression(30), JQuickBinaryOperator.GE);
        JQuickTableScanNode logicalScan = new JQuickTableScanNode("users", null, null, filter);
        JQuickPhysicalPlanNode physicalPlan = generator.generate(logicalScan);
        JQuickTableScanPhysicalNode scanNode = (JQuickTableScanPhysicalNode) physicalPlan;
        assertNotNull(scanNode.getFilterPredicate());
        JQuickFragmenter fragmenter = new JQuickFragmenter(1);
        JQuickDistributedPlan plan = fragmenter.fragment(scanNode);
        String queryId = "hash_partition_" + System.currentTimeMillis();
        JQuickDataSet result = coordinator.executeQueryWithPlan(queryId, plan);
        result.printTable();
    }
    /**
     * 测试1：简单CTE - 单层CTE，主查询直接引用
     *
     * SQL示例：
     * WITH sales_summary AS (
     *     SELECT category, SUM(amount) as total
     *     FROM orders
     *     GROUP BY category
     * )
     * SELECT category, total FROM sales_summary
     */
    @Test
    public void testSimpleCte() {
        JQuickTableScanNode ordersScan = createTableScan("orders");
        List<JQuickAggregateNode.AggregateFunction> aggregates = new ArrayList<>();
        aggregates.add(new JQuickAggregateNode.AggregateFunction("SUM", new JQuickColumnRefExpression("amount"), false, "total"));
        List<JQuickExpression> groupKeys = new ArrayList<>();
        groupKeys.add(new JQuickColumnRefExpression("category"));
        JQuickAggregateNode cteAggregate = new JQuickAggregateNode(groupKeys, aggregates, ordersScan, null, false);
        List<JQuickProjectNode.SelectItem> cteProjectItems = new ArrayList<>();
        cteProjectItems.add(new JQuickProjectNode.SelectItem(new JQuickColumnRefExpression("category"), "category"));
        cteProjectItems.add(new JQuickProjectNode.SelectItem(new JQuickColumnRefExpression("total"), "total"));
        JQuickProjectNode cteProject = new JQuickProjectNode(cteProjectItems, cteAggregate);
        JQuickTableScanNode cteRef = createTableScan("sales_summary");
        JQuickProjectNode mainProject = createProject(cteRef, "category", "total");
        Map<String, JQuickLogicalPlanNode> ctes = new LinkedHashMap<>();
        ctes.put("sales_summary", cteProject);
        JQuickWithNode withNode = new JQuickWithNode(mainProject, ctes);
        JQuickPhysicalPlanNode physicalPlan = generator.generate(withNode);
//        JQuickDistributedPlan distributedPlan= fragmenter.fragment(physicalPlan);
//        fragmenter.printFragments(distributedPlan);

    }


}
