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
import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.expression.domain.JQuickBinaryExpression;
import com.github.paohaijiao.expression.domain.JQuickColumnRefExpression;
import com.github.paohaijiao.expression.domain.JQuickLiteralExpression;
import com.github.paohaijiao.expression.domain.JQuickUnaryExpression;
import com.github.paohaijiao.fragment.JQuickFragmenter;
import com.github.paohaijiao.logic.domain.JQuickFilterNode;
import com.github.paohaijiao.logic.domain.JQuickProjectNode;
import com.github.paohaijiao.logic.domain.JQuickRecursiveUnionNode;
import com.github.paohaijiao.logic.domain.JQuickTableScanNode;
import com.github.paohaijiao.logic2physical.JQuickPhysicalPlanGenerator;
import com.github.paohaijiao.physical.JQuickPhysicalPlanNode;
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
 * JQuickRecursiveUnionPhysicalNode 分布式测试
 * 
 * 测试场景：
 * 1. 数字序列生成（1到10）
 * 2. 组织结构层级查询
 * 3. UNION ALL vs UNION 去重
 * 4. 最大递归深度限制
 */
@Slf4j
public class JQuickRecursiveUnionPhysicalNodeTest {

    private static final String TABLE_ORGANIZATION = "organization";

    private static final String TABLE_NUMBERS = "numbers";

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
        List<JQuickColumnMeta> orgColumns = Arrays.asList(
                new JQuickColumnMeta("id", Integer.class, TABLE_ORGANIZATION),
                new JQuickColumnMeta("name", String.class, TABLE_ORGANIZATION),
                new JQuickColumnMeta("parent_id", Integer.class, TABLE_ORGANIZATION)
        );
        List<JQuickRow> orgRows = Arrays.asList(
                createRow("id", 1, "name", "总公司", "parent_id", null),
                createRow("id", 2, "name", "研发部", "parent_id", 1),
                createRow("id", 3, "name", "产品部", "parent_id", 1),
                createRow("id", 4, "name", "前端组", "parent_id", 2),
                createRow("id", 5, "name", "后端组", "parent_id", 2),
                createRow("id", 6, "name", "测试组", "parent_id", 2),
                createRow("id", 7, "name", "产品策划组", "parent_id", 3),
                createRow("id", 8, "name", "产品运营组", "parent_id", 3)
        );
        JQuickDataSet orgData = new JQuickDataSet(orgColumns, orgRows);
        JQuickDataSourceManager.registerTable(TABLE_ORGANIZATION, orgData);

        List<JQuickColumnMeta> numberColumns = Arrays.asList(
                new JQuickColumnMeta("n", Integer.class, TABLE_NUMBERS)
        );
        List<JQuickRow> numberRows = Arrays.asList(
                createRow("n", 1)
        );
        JQuickDataSet numberData = new JQuickDataSet(numberColumns, numberRows);
        JQuickDataSourceManager.registerTable(TABLE_NUMBERS, numberData);
    }

    private JQuickRow createRow(Object... keyValues) {
        JQuickRow row = new JQuickRow();
        for (int i = 0; i < keyValues.length; i += 2) {
            row.put((String) keyValues[i], keyValues[i + 1]);
        }
        return row;
    }

    /**
     * 测试数字序列生成（使用递归CTE生成1到10的数字序列）
     *
     * SQL示例：
     * WITH RECURSIVE nums(n) AS (
     *   SELECT 1
     *   UNION ALL
     *   SELECT n + 1 FROM nums WHERE n < 10
     * )
     * SELECT n FROM nums;
     */
    @Test
    public void testRecursiveNumberSequence() {
        JQuickTableScanNode initialScan = new JQuickTableScanNode(TABLE_NUMBERS);
        JQuickTableScanNode recursiveScan = new JQuickTableScanNode("nums");
        JQuickBinaryExpression recursiveCondition = new JQuickBinaryExpression(
                new JQuickColumnRefExpression("n"),
                new JQuickLiteralExpression(10),
                JQuickBinaryOperator.LT
        );
        JQuickFilterNode recursiveFilter = new JQuickFilterNode(recursiveCondition, recursiveScan);
        List<JQuickProjectNode.SelectItem> projectItems = Arrays.asList(
                new JQuickProjectNode.SelectItem(
                        new JQuickBinaryExpression(
                                new JQuickColumnRefExpression("n"),
                                new JQuickLiteralExpression(1),
                                JQuickBinaryOperator.PLUS
                        ),
                        "n"
                )
        );
        JQuickProjectNode recursiveProject = new JQuickProjectNode(projectItems, recursiveFilter);
        JQuickRecursiveUnionNode recursiveUnionNode = new JQuickRecursiveUnionNode(
                "nums",
                Arrays.asList("n"),
                initialScan,
                recursiveProject,
                true
        );
        JQuickPhysicalPlanNode physicalPlan = generator.generate(recursiveUnionNode);
        JQuickDistributedPlan plan = new JQuickFragmenter(1).fragment(physicalPlan);
        String queryId = "recursive_sequence_test_" + System.currentTimeMillis();
        JQuickDataSet result = coordinator.executeQueryWithPlan(queryId, plan);
        result.printTable();
    }

    /**
     * 测试组织结构层级查询（递归查找所有子节点）
     *
     * SQL示例：
     * WITH RECURSIVE org_hierarchy(id, name, parent_id) AS (
     *   SELECT id, name, parent_id FROM organization WHERE parent_id IS NULL
     *   UNION ALL
     *   SELECT o.id, o.name, o.parent_id FROM organization o, org_hierarchy h WHERE o.parent_id = h.id
     * )
     * SELECT id, name, parent_id FROM org_hierarchy;
     */
    @Test
    public void testOrganizationHierarchy() {
        JQuickTableScanNode orgScan = new JQuickTableScanNode(TABLE_ORGANIZATION, "o");
        JQuickUnaryExpression isNullCondition = new JQuickUnaryExpression(
                com.github.paohaijiao.enums.JQuickUnaryOperator.IS_NULL,
                new JQuickColumnRefExpression("parent_id")
        );
        JQuickFilterNode initialFilter = new JQuickFilterNode(isNullCondition, orgScan);
        JQuickTableScanNode recursiveScan = new JQuickTableScanNode("org_hierarchy", "h");
        JQuickRecursiveUnionNode recursiveUnionNode = new JQuickRecursiveUnionNode(
                "org_hierarchy",
                Arrays.asList("id", "name", "parent_id"),
                initialFilter,
                recursiveScan,
                true
        );
        JQuickPhysicalPlanNode physicalPlan = generator.generate(recursiveUnionNode);
        JQuickDistributedPlan plan = new JQuickFragmenter(1).fragment(physicalPlan);
        String queryId = "org_hierarchy_test_" + System.currentTimeMillis();
        JQuickDataSet result = coordinator.executeQueryWithPlan(queryId, plan);
        result.printTable();
    }

    /**
     * 测试UNION ALL（不去重）递归
     *
     * SQL示例：
     * WITH RECURSIVE seq(n) AS (
     *   SELECT 1
     *   UNION ALL
     *   SELECT n + 1 FROM seq WHERE n < 5
     * )
     * SELECT n FROM seq;
     */
    @Test
    public void testRecursiveUnionAll() {
        JQuickTableScanNode initialScan = new JQuickTableScanNode(TABLE_NUMBERS);
        JQuickTableScanNode recursiveScan = new JQuickTableScanNode("seq");
        JQuickBinaryExpression condition = new JQuickBinaryExpression(
                new JQuickColumnRefExpression("n"),
                new JQuickLiteralExpression(5),
                JQuickBinaryOperator.LT
        );
        JQuickFilterNode recursiveFilter = new JQuickFilterNode(condition, recursiveScan);
        List<JQuickProjectNode.SelectItem> projectItems = Arrays.asList(
                new JQuickProjectNode.SelectItem(
                        new JQuickBinaryExpression(
                                new JQuickColumnRefExpression("n"),
                                new JQuickLiteralExpression(1),
                                JQuickBinaryOperator.PLUS
                        ),
                        "n"
                )
        );
        JQuickProjectNode recursiveProject = new JQuickProjectNode(projectItems, recursiveFilter);
        JQuickRecursiveUnionNode recursiveUnionNode = new JQuickRecursiveUnionNode(
                "seq",
                Arrays.asList("n"),
                initialScan,
                recursiveProject,
                true
        );
        JQuickPhysicalPlanNode physicalPlan = generator.generate(recursiveUnionNode);
        JQuickDistributedPlan plan = new JQuickFragmenter(1).fragment(physicalPlan);
        String queryId = "recursive_union_all_test_" + System.currentTimeMillis();
        JQuickDataSet result = coordinator.executeQueryWithPlan(queryId, plan);
        result.printTable();
    }

    /**
     * 测试UNION（去重）递归
     *
     * SQL示例：
     * WITH RECURSIVE seq(n) AS (
     *   SELECT 1
     *   UNION
     *   SELECT n + 1 FROM seq WHERE n < 5
     * )
     * SELECT n FROM seq;
     */
    @Test
    public void testRecursiveUnionWithDeduplication() {
        JQuickTableScanNode initialScan = new JQuickTableScanNode(TABLE_NUMBERS);
        JQuickTableScanNode recursiveScan = new JQuickTableScanNode("seq");
        JQuickBinaryExpression condition = new JQuickBinaryExpression(
                new JQuickColumnRefExpression("n"),
                new JQuickLiteralExpression(5),
                JQuickBinaryOperator.LT
        );
        JQuickFilterNode recursiveFilter = new JQuickFilterNode(condition, recursiveScan);
        List<JQuickProjectNode.SelectItem> projectItems = Arrays.asList(
                new JQuickProjectNode.SelectItem(
                        new JQuickBinaryExpression(
                                new JQuickColumnRefExpression("n"),
                                new JQuickLiteralExpression(1),
                                JQuickBinaryOperator.PLUS
                        ),
                        "n"
                )
        );
        JQuickProjectNode recursiveProject = new JQuickProjectNode(projectItems, recursiveFilter);
        JQuickRecursiveUnionNode recursiveUnionNode = new JQuickRecursiveUnionNode(
                "seq",
                Arrays.asList("n"),
                initialScan,
                recursiveProject,
                false
        );
        JQuickPhysicalPlanNode physicalPlan = generator.generate(recursiveUnionNode);
        JQuickDistributedPlan plan = new JQuickFragmenter(1).fragment(physicalPlan);
        String queryId = "recursive_union_dedup_test_" + System.currentTimeMillis();
        JQuickDataSet result = coordinator.executeQueryWithPlan(queryId, plan);
        result.printTable();
    }

    /**
     * 测试最大递归深度限制
     *
     * SQL示例：
     * WITH RECURSIVE deep(n) AS (
     *   SELECT 1
     *   UNION ALL
     *   SELECT n + 1 FROM deep
     * )
     * SELECT n FROM deep;
     */
    @Test
    public void testMaxRecursionDepth() {
        JQuickTableScanNode initialScan = new JQuickTableScanNode(TABLE_NUMBERS);
        JQuickTableScanNode recursiveScan = new JQuickTableScanNode("deep");
        List<JQuickProjectNode.SelectItem> projectItems = Arrays.asList(
                new JQuickProjectNode.SelectItem(
                        new JQuickBinaryExpression(
                                new JQuickColumnRefExpression("n"),
                                new JQuickLiteralExpression(1),
                                JQuickBinaryOperator.PLUS
                        ),
                        "n"
                )
        );
        JQuickProjectNode recursiveProject = new JQuickProjectNode(projectItems, recursiveScan);
        JQuickRecursiveUnionNode recursiveUnionNode = new JQuickRecursiveUnionNode(
                "deep",
                Arrays.asList("n"),
                initialScan,
                recursiveProject,
                true
        );
        JQuickPhysicalPlanNode physicalPlan = generator.generate(recursiveUnionNode);
        JQuickDistributedPlan plan = new JQuickFragmenter(1).fragment(physicalPlan);
        String queryId = "max_recursion_test_" + System.currentTimeMillis();
        JQuickDataSet result = coordinator.executeQueryWithPlan(queryId, plan);
        result.printTable();
    }
}
