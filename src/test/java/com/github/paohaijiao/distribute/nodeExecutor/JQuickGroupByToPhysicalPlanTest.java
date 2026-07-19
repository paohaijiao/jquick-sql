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
import com.github.paohaijiao.expression.domain.*;
import com.github.paohaijiao.fragment.JQuickFragmenter;
import com.github.paohaijiao.logic.domain.*;
import com.github.paohaijiao.logic2physical.JQuickPhysicalPlanGenerator;
import com.github.paohaijiao.optimizer.JQuickPhysicalPlanOptimizer;
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
 * 两阶段聚合协议测试
 *
 * 测试使用逻辑层 JQuickGroupByNode，让优化器自动生成两阶段聚合：
 *   PARTIAL HashAggregate → Exchange (SHUFFLE) → FINAL HashAggregate
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/7/18
 */
@Slf4j
public class JQuickGroupByToPhysicalPlanTest {

    private static final String TABLE_USERS = "users";

    private static final String TABLE_EMPLOYEES = "employees";

    private static final String TABLE_SALES = "sales";

    private static final int WORKER1_PORT = 19101;

    private static final int WORKER2_PORT = 19102;

    private static final int WORKER3_PORT = 19103;

    private static JConsole console;

    private static JQuickDataConverter dataConverter;

    private JQuickWorker worker1;

    private JQuickWorker worker2;

    private JQuickWorker worker3;

    private JQuickCoordinator coordinator;

    private JQuickPhysicalPlanGenerator generator;

    private JQuickPhysicalPlanOptimizer optimizer;

    private List<JQuickCoordinator.WorkerEndpoint> endpoints;

    private List<JQuickWorker> workers;

    @Before
    public void setUp() throws IOException, InterruptedException {
        console = JConsole.initConsoleEnvironment();
        dataConverter = new JQuickDataConverter();
        console.info("测试环境初始化完成");
        generator = new JQuickPhysicalPlanGenerator();
        optimizer = new JQuickPhysicalPlanOptimizer();
        JQuickDataSourceManager.clearAll();
        registerLargeTestData();
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

    private void registerLargeTestData() {
        List<JQuickColumnMeta> userColumns = Arrays.asList(
                new JQuickColumnMeta("id", Integer.class, TABLE_USERS),
                new JQuickColumnMeta("name", String.class, TABLE_USERS),
                new JQuickColumnMeta("age", Integer.class, TABLE_USERS),
                new JQuickColumnMeta("status", String.class, TABLE_USERS)
        );
        List<JQuickRow> userRows = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            String status = i % 3 == 0 ? "active" : (i % 3 == 1 ? "pending" : "inactive");
            userRows.add(createRow("id", i, "name", "User" + i, "age", 20 + (i % 30), "status", status));
        }
        JQuickDataSet usersData = new JQuickDataSet(userColumns, userRows);
        JQuickDataSourceManager.registerTable(TABLE_USERS, usersData);

        List<JQuickColumnMeta> employeeColumns = Arrays.asList(
                new JQuickColumnMeta("id", Integer.class, TABLE_EMPLOYEES),
                new JQuickColumnMeta("name", String.class, TABLE_EMPLOYEES),
                new JQuickColumnMeta("department", String.class, TABLE_EMPLOYEES),
                new JQuickColumnMeta("salary", Double.class, TABLE_EMPLOYEES)
        );
        List<JQuickRow> employeeRows = new ArrayList<>();
        String[] departments = {"engineering", "sales", "marketing", "finance", "hr"};
        for (int i = 1; i <= 12000; i++) {
            String dept = departments[i % departments.length];
            employeeRows.add(createRow("id", i, "name", "Emp" + i, "department", dept, "salary", 4000.0 + (i % 6000)));
        }
        JQuickDataSet employeesData = new JQuickDataSet(employeeColumns, employeeRows);
        JQuickDataSourceManager.registerTable(TABLE_EMPLOYEES, employeesData);

        List<JQuickColumnMeta> salesColumns = Arrays.asList(
                new JQuickColumnMeta("id", Integer.class, TABLE_SALES),
                new JQuickColumnMeta("category", String.class, TABLE_SALES),
                new JQuickColumnMeta("amount", Double.class, TABLE_SALES)
        );
        List<JQuickRow> salesRows = new ArrayList<>();
        String[] categories = {"electronics", "clothing", "books", "food", "home"};
        for (int i = 1; i <= 18000; i++) {
            String category = categories[i % categories.length];
            salesRows.add(createRow("id", i, "category", category, "amount", 100.0 + (i % 1000)));
        }
        JQuickDataSet salesData = new JQuickDataSet(salesColumns, salesRows);
        JQuickDataSourceManager.registerTable(TABLE_SALES, salesData);
    }

    private JQuickRow createRow(Object... keyValues) {
        JQuickRow row = new JQuickRow();
        for (int i = 0; i < keyValues.length; i += 2) {
            row.put((String) keyValues[i], keyValues[i + 1]);
        }
        return row;
    }

    private JQuickTableScanNode createTableScan(String tableName) {
        return new JQuickTableScanNode(tableName);
    }

    private JQuickTableScanNode createTableScan(String tableName, String alias) {
        return new JQuickTableScanNode(tableName, alias);
    }

    private List<JQuickExpression> createGroupKeys(String... keys) {
        List<JQuickExpression> groupKeys = new ArrayList<>();
        for (String key : keys) {
            groupKeys.add(new JQuickColumnRefExpression(key));
        }
        return groupKeys;
    }

    private List<JQuickGroupByNode.AggregateItem> createAggregates(String... aggSpecs) {
        List<JQuickGroupByNode.AggregateItem> aggregates = new ArrayList<>();
        for (String spec : aggSpecs) {
            String[] parts = spec.split(":");
            String functionName = parts[0];
            String column = parts.length > 1 && !parts[1].isEmpty() ? parts[1] : null;
            String alias = parts.length > 2 ? parts[2] : (column != null ? functionName + "_" + column : functionName);
            JQuickExpression argument = column != null ? new JQuickColumnRefExpression(column) : null;
            boolean isCountStar = functionName.equals("COUNT") && column == null;
            aggregates.add(new JQuickGroupByNode.AggregateItem(argument, functionName, alias, isCountStar));
        }
        return aggregates;
    }

    /**
     * 测试两阶段聚合：GROUP BY + COUNT(*)
     *
     * SQL示例：SELECT status, COUNT(*) FROM users GROUP BY status
     *
     * 两阶段协议（数据量 > 10000行时优化器自动触发）：
     *   PARTIAL COUNT → SHUFFLE (HASH) → FINAL COUNT
     */
    @Test
    public void testTwoPhaseCount() {
        JQuickTableScanNode usersScan = createTableScan(TABLE_USERS);
        List<JQuickExpression> groupKeys = createGroupKeys("status");
        List<JQuickGroupByNode.AggregateItem> aggregates = createAggregates("COUNT::count");
        JQuickGroupByNode groupByNode = new JQuickGroupByNode(groupKeys, aggregates, usersScan, null);
        JQuickPhysicalPlanNode physicalPlan = generator.generate(groupByNode);
        physicalPlan = optimizer.optimize(physicalPlan);
        JQuickDistributedPlan plan = new JQuickFragmenter(1).fragment(physicalPlan);
        String queryId = "twophase_count_test_" + System.currentTimeMillis();
        JQuickDataSet result = coordinator.executeQueryWithPlan(queryId, plan);
        result.printTable();
    }

    /**
     * 测试两阶段聚合：GROUP BY + SUM
     *
     * SQL示例：SELECT category, SUM(amount) FROM sales GROUP BY category
     */
    @Test
    public void testTwoPhaseSum() {
        JQuickTableScanNode salesScan = createTableScan(TABLE_SALES);
        List<JQuickExpression> groupKeys = createGroupKeys("category");
        List<JQuickGroupByNode.AggregateItem> aggregates = createAggregates("SUM:amount:total_amount");
        JQuickGroupByNode groupByNode = new JQuickGroupByNode(groupKeys, aggregates, salesScan, null);
        JQuickPhysicalPlanNode physicalPlan = generator.generate(groupByNode);
        physicalPlan = optimizer.optimize(physicalPlan);
        JQuickDistributedPlan plan = new JQuickFragmenter(1).fragment(physicalPlan);
        String queryId = "twophase_sum_test_" + System.currentTimeMillis();
        JQuickDataSet result = coordinator.executeQueryWithPlan(queryId, plan);
        result.printTable();
    }

    /**
     * 测试两阶段聚合：GROUP BY + AVG
     *
     * SQL示例：SELECT department, AVG(salary) FROM employees GROUP BY department
     */
    @Test
    public void testTwoPhaseAvg() {
        JQuickTableScanNode employeesScan = createTableScan(TABLE_EMPLOYEES);
        List<JQuickExpression> groupKeys = createGroupKeys("department");
        List<JQuickGroupByNode.AggregateItem> aggregates = createAggregates("AVG:salary:avg_salary");
        JQuickGroupByNode groupByNode = new JQuickGroupByNode(groupKeys, aggregates, employeesScan, null);
        JQuickPhysicalPlanNode physicalPlan = generator.generate(groupByNode);
        physicalPlan = optimizer.optimize(physicalPlan);
        JQuickDistributedPlan plan = new JQuickFragmenter(1).fragment(physicalPlan);
        String queryId = "twophase_avg_test_" + System.currentTimeMillis();
        JQuickDataSet result = coordinator.executeQueryWithPlan(queryId, plan);
        result.printTable();
    }

    /**
     * 测试两阶段聚合：GROUP BY + MIN/MAX
     *
     * SQL示例：SELECT category, MIN(amount), MAX(amount) FROM sales GROUP BY category
     */
    @Test
    public void testTwoPhaseMinMax() {
        JQuickTableScanNode salesScan = createTableScan(TABLE_SALES);
        List<JQuickExpression> groupKeys = createGroupKeys("category");
        List<JQuickGroupByNode.AggregateItem> aggregates = createAggregates(
                "MIN:amount:min_amount",
                "MAX:amount:max_amount"
        );
        JQuickGroupByNode groupByNode = new JQuickGroupByNode(groupKeys, aggregates, salesScan, null);
        JQuickPhysicalPlanNode physicalPlan = generator.generate(groupByNode);
        physicalPlan = optimizer.optimize(physicalPlan);
        JQuickDistributedPlan plan = new JQuickFragmenter(1).fragment(physicalPlan);
        String queryId = "twophase_minmax_test_" + System.currentTimeMillis();
        JQuickDataSet result = coordinator.executeQueryWithPlan(queryId, plan);
        result.printTable();
    }

    /**
     * 测试两阶段聚合：GROUP BY + 多个聚合函数组合
     *
     * SQL示例：SELECT category, COUNT(*), SUM(amount), AVG(amount) FROM sales GROUP BY category
     */
    @Test
    public void testTwoPhaseMultipleAggregates() {
        JQuickTableScanNode salesScan = createTableScan(TABLE_SALES);
        List<JQuickExpression> groupKeys = createGroupKeys("category");
        List<JQuickGroupByNode.AggregateItem> aggregates = createAggregates(
                "COUNT::count",
                "SUM:amount:total_amount",
                "AVG:amount:avg_amount"
        );
        JQuickGroupByNode groupByNode = new JQuickGroupByNode(groupKeys, aggregates, salesScan, null);
        JQuickPhysicalPlanNode physicalPlan = generator.generate(groupByNode);
        physicalPlan = optimizer.optimize(physicalPlan);
        JQuickDistributedPlan plan = new JQuickFragmenter(1).fragment(physicalPlan);
        String queryId = "twophase_multi_agg_test_" + System.currentTimeMillis();
        JQuickDataSet result = coordinator.executeQueryWithPlan(queryId, plan);
        result.printTable();
    }

    /**
     * 测试两阶段聚合：多列分组
     *
     * SQL示例：SELECT status, age, COUNT(*) FROM users GROUP BY status, age
     */
    @Test
    public void testTwoPhaseMultipleGroupColumns() {
        JQuickTableScanNode usersScan = createTableScan(TABLE_USERS);
        List<JQuickExpression> groupKeys = createGroupKeys("status", "age");
        List<JQuickGroupByNode.AggregateItem> aggregates = createAggregates("COUNT::count");
        JQuickGroupByNode groupByNode = new JQuickGroupByNode(groupKeys, aggregates, usersScan, null);
        JQuickPhysicalPlanNode physicalPlan = generator.generate(groupByNode);
        physicalPlan = optimizer.optimize(physicalPlan);
        JQuickDistributedPlan plan = new JQuickFragmenter(1).fragment(physicalPlan);
        String queryId = "twophase_multi_col_test_" + System.currentTimeMillis();
        JQuickDataSet result = coordinator.executeQueryWithPlan(queryId, plan);
        result.printTable();
    }

    /**
     * 测试单阶段聚合：小数据量（< 10000行），优化器不会触发两阶段聚合
     *
     * SQL示例：SELECT department, COUNT(*) FROM employees LIMIT 100 GROUP BY department
     */
    @Test
    public void testSinglePhaseAggregate() {
        JQuickTableScanNode employeesScan = createTableScan(TABLE_EMPLOYEES);
        List<JQuickExpression> groupKeys = createGroupKeys("department");
        List<JQuickGroupByNode.AggregateItem> aggregates = createAggregates("COUNT::count");
        JQuickGroupByNode groupByNode = new JQuickGroupByNode(groupKeys, aggregates, employeesScan, null);
        JQuickPhysicalPlanNode physicalPlan = generator.generate(groupByNode);
        JQuickDistributedPlan plan = new JQuickFragmenter(1).fragment(physicalPlan);
        String queryId = "single_phase_test_" + System.currentTimeMillis();
        JQuickDataSet result = coordinator.executeQueryWithPlan(queryId, plan);
        result.printTable();
    }
}
