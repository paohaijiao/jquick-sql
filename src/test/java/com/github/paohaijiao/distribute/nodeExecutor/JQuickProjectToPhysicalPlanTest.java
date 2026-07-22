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
import com.github.paohaijiao.logic.JQuickLogicalPlanNode;
import com.github.paohaijiao.logic.domain.*;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * packageName com.github.paohaijiao.physical
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/5/24
 */
@Slf4j
public class JQuickProjectToPhysicalPlanTest {

    private static final String TABLE_USERS = "users";

    private static final String TABLE_EMPLOYEES = "employees";

    private static final String TABLE_STUDENTS = "students";

    private static final String TABLE_SALES = "sales";

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
                new JQuickColumnMeta("age", Integer.class, TABLE_USERS),
                new JQuickColumnMeta("status", String.class, TABLE_USERS),
                new JQuickColumnMeta("enabled", Boolean.class, TABLE_USERS),
                new JQuickColumnMeta("first_name", String.class, TABLE_USERS),
                new JQuickColumnMeta("last_name", String.class, TABLE_USERS),
                new JQuickColumnMeta("email", String.class, TABLE_USERS)
        );
        List<JQuickRow> userRows = Arrays.asList(
                createRow("id", 1, "name", "Alice", "age", 25, "status", "active", "enabled", true, "first_name", "Alice", "last_name", "Smith", "email", "alice@test.com"),
                createRow("id", 2, "name", "Bob", "age", 30, "status", "active", "enabled", true, "first_name", "Bob", "last_name", "Johnson", "email", "bob@test.com"),
                createRow("id", 3, "name", "Charlie", "age", 20, "status", "pending", "enabled", true, "first_name", "Charlie", "last_name", "Brown", "email", "charlie@test.com"),
                createRow("id", 4, "name", "David", "age", 35, "status", "inactive", "enabled", false, "first_name", "David", "last_name", "Wilson", "email", "david@test.com"),
                createRow("id", 5, "name", "Eve", "age", 28, "status", "active", "enabled", false, "first_name", "Eve", "last_name", "Davis", "email", "eve@test.com"),
                createRow("id", 5, "name", "Eve", "age", 28, "status", "active", "enabled", false, "first_name", "Eve", "last_name", "Davis", "email", "eve@test.com")
        );
        JQuickDataSet usersData = new JQuickDataSet(userColumns, userRows);
        JQuickDataSourceManager.registerTable(TABLE_USERS, usersData);

        List<JQuickColumnMeta> employeeColumns = Arrays.asList(
                new JQuickColumnMeta("id", Integer.class, TABLE_EMPLOYEES),
                new JQuickColumnMeta("name", String.class, TABLE_EMPLOYEES),
                new JQuickColumnMeta("salary", Double.class, TABLE_EMPLOYEES)
        );
        List<JQuickRow> employeeRows = Arrays.asList(
                createRow("id", 1, "name", "Alice", "salary", 5000.0),
                createRow("id", 2, "name", "Bob", "salary", 6000.0),
                createRow("id", 3, "name", "Charlie", "salary", 4500.0)
        );
        JQuickDataSet employeesData = new JQuickDataSet(employeeColumns, employeeRows);
        JQuickDataSourceManager.registerTable(TABLE_EMPLOYEES, employeesData);

        List<JQuickColumnMeta> studentColumns = Arrays.asList(
                new JQuickColumnMeta("id", Integer.class, TABLE_STUDENTS),
                new JQuickColumnMeta("name", String.class, TABLE_STUDENTS),
                new JQuickColumnMeta("score", Integer.class, TABLE_STUDENTS)
        );
        List<JQuickRow> studentRows = Arrays.asList(
                createRow("id", 1, "name", "Alice", "score", 85),
                createRow("id", 2, "name", "Bob", "score", 55),
                createRow("id", 3, "name", "Charlie", "score", 70),
                createRow("id", 4, "name", "David", "score", 90)
        );
        JQuickDataSet studentsData = new JQuickDataSet(studentColumns, studentRows);
        JQuickDataSourceManager.registerTable(TABLE_STUDENTS, studentsData);

        List<JQuickColumnMeta> salesColumns = Arrays.asList(
                new JQuickColumnMeta("id", Integer.class, TABLE_SALES),
                new JQuickColumnMeta("category", String.class, TABLE_SALES),
                new JQuickColumnMeta("amount", Double.class, TABLE_SALES)
        );
        List<JQuickRow> salesRows = Arrays.asList(
                createRow("id", 1, "category", "electronics", "amount", 500.0),
                createRow("id", 2, "category", "clothing", "amount", 300.0),
                createRow("id", 3, "category", "electronics", "amount", 800.0),
                createRow("id", 4, "category", "clothing", "amount", 200.0),
                createRow("id", 5, "category", "books", "amount", 150.0)
        );
        JQuickDataSet salesData = new JQuickDataSet(salesColumns, salesRows);
        JQuickDataSourceManager.registerTable(TABLE_SALES, salesData);

        List<JQuickColumnMeta> orderColumns = Arrays.asList(
                new JQuickColumnMeta("id", Integer.class, TABLE_ORDERS),
                new JQuickColumnMeta("user_id", Integer.class, TABLE_ORDERS),
                new JQuickColumnMeta("order_date", String.class, TABLE_ORDERS)
        );
        List<JQuickRow> orderRows = Arrays.asList(
                createRow("id", 1, "user_id", 1, "order_date", "2024-01-01"),
                createRow("id", 2, "user_id", 1, "order_date", "2024-02-01"),
                createRow("id", 3, "user_id", 2, "order_date", "2024-01-15"),
                createRow("id", 4, "user_id", 3, "order_date", "2024-03-01")
        );
        JQuickDataSet ordersData = new JQuickDataSet(orderColumns, orderRows);
        JQuickDataSourceManager.registerTable(TABLE_ORDERS, ordersData);
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

    private JQuickProjectNode createSimpleProject(JQuickLogicalPlanNode child, String... columns) {
        List<JQuickProjectNode.SelectItem> items = new ArrayList<>();
        for (String col : columns) {
            items.add(new JQuickProjectNode.SelectItem(new JQuickColumnRefExpression(col), col));
        }
        return new JQuickProjectNode(items, child);
    }

    private JQuickProjectNode createProjectWithAlias(JQuickLogicalPlanNode child, Map<String, String> columnToAlias) {
        List<JQuickProjectNode.SelectItem> items = new ArrayList<>();
        for (Map.Entry<String, String> entry : columnToAlias.entrySet()) {
            items.add(new JQuickProjectNode.SelectItem(
                    new JQuickColumnRefExpression(entry.getKey()), entry.getValue()));
        }
        return new JQuickProjectNode(items, child);
    }

    private JQuickProjectNode createBinaryExpressionProject(JQuickLogicalPlanNode child, String leftCol, String rightCol, JQuickBinaryOperator operator, String alias) {
        JQuickBinaryExpression expr = new JQuickBinaryExpression(new JQuickColumnRefExpression(leftCol), new JQuickColumnRefExpression(rightCol), operator);
        List<JQuickProjectNode.SelectItem> items = new ArrayList<>();
        items.add(new JQuickProjectNode.SelectItem(expr, alias));
        return new JQuickProjectNode(items, child);
    }

    private JQuickProjectNode createConstantProject(JQuickLogicalPlanNode child, Object constant, String alias) {
        JQuickLiteralExpression constantExpr = new JQuickLiteralExpression(constant);
        List<JQuickProjectNode.SelectItem> items = new ArrayList<>();
        items.add(new JQuickProjectNode.SelectItem(constantExpr, alias));
        return new JQuickProjectNode(items, child);
    }

    private JQuickProjectNode createFunctionProject(JQuickLogicalPlanNode child, String functionName, String argument, String alias) {
        JQuickFunctionCallExpression functionCall = new JQuickFunctionCallExpression(functionName, Collections.singletonList(new JQuickColumnRefExpression(argument)));
        List<JQuickProjectNode.SelectItem> items = new ArrayList<>();
        items.add(new JQuickProjectNode.SelectItem(functionCall, alias));
        return new JQuickProjectNode(items, child);
    }

    private JQuickFilterNode createFilter(JQuickLogicalPlanNode child, String column, JQuickBinaryOperator operator, Object value) {
        JQuickBinaryExpression predicate = new JQuickBinaryExpression(new JQuickColumnRefExpression(column), new JQuickLiteralExpression(value), operator);
        return new JQuickFilterNode(predicate, child);
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
            String column = parts.length > 1 ? parts[1] : null;
            String alias = parts.length > 2 ? parts[2] : functionName + "_" + column;
            JQuickExpression argument = column != null ? new JQuickColumnRefExpression(column) : null;
            boolean isCountStar = functionName.equals("COUNT") && column == null;
            aggregates.add(new JQuickGroupByNode.AggregateItem(argument, functionName, alias, isCountStar));
        }
        return aggregates;
    }

    private JQuickExpression createHavingCondition(String column, JQuickBinaryOperator operator, Object value) {
        return new JQuickBinaryExpression(new JQuickColumnRefExpression(column), new JQuickLiteralExpression(value), operator);
    }
    /**
     * 测试简单列投影
     *
     * SQL示例：SELECT *FROM users
     */
    @Test
    public void testStarColumnProjection() {
        JQuickTableScanNode usersScan = createTableScan("users", "u");
        List<JQuickProjectNode.SelectItem> items = new ArrayList<>();
        items.add(JQuickProjectNode.SelectItem.star());
        JQuickProjectNode projectNode = new JQuickProjectNode(items, usersScan);
        JQuickPhysicalPlanNode physicalPlan = generator.generate(projectNode);
        JQuickDistributedPlan plan = new JQuickFragmenter(1).fragment(physicalPlan);
        String queryId = "simple_project_test_" + System.currentTimeMillis();
        JQuickDataSet result = coordinator.executeQueryWithPlan(queryId, plan);
        result.printTable();
    }
    /**
     * 测试简单列投影
     *
     * SQL示例：SELECT distinct *FROM users
     */
    @Test
    public void testDistinctStarColumnProjection() {
        JQuickTableScanNode usersScan = createTableScan("users", "u");
        List<JQuickProjectNode.SelectItem> items = new ArrayList<>();
        items.add(JQuickProjectNode.SelectItem.star());
        JQuickProjectNode projectNode = new JQuickProjectNode(items, usersScan,true);
        JQuickPhysicalPlanNode physicalPlan = generator.generate(projectNode);
        JQuickDistributedPlan plan = new JQuickFragmenter(1).fragment(physicalPlan);
        String queryId = "simple_project_test_" + System.currentTimeMillis();
        JQuickDataSet result = coordinator.executeQueryWithPlan(queryId, plan);
        result.printTable();
    }
    /**
     * 测试简单列投影
     *
     * SQL示例：SELECT id, name, age FROM users
     */
    @Test
    public void testSimpleColumnProjection() {
        JQuickTableScanNode usersScan = createTableScan("users", "u");
        JQuickProjectNode projectNode = createSimpleProject(usersScan, "id", "name", "age");
        JQuickPhysicalPlanNode physicalPlan = generator.generate(projectNode);
        JQuickDistributedPlan plan = new JQuickFragmenter(1).fragment(physicalPlan);
        String queryId = "simple_project_test_" + System.currentTimeMillis();
        JQuickDataSet result = coordinator.executeQueryWithPlan(queryId, plan);
        result.printTable();
    }

    /**
     * 测试算术表达式投影
     *
     * SQL示例：SELECT id, salary * 1.1 AS new_salary FROM employees
     */
    @Test
    public void testArithmeticExpressionProjection() {
        JQuickTableScanNode employeesScan = createTableScan("employees", "e");
        JQuickBinaryExpression multiplyExpr = new JQuickBinaryExpression(new JQuickColumnRefExpression("salary"), new JQuickLiteralExpression(1.1), JQuickBinaryOperator.MULTIPLY);
        List<JQuickProjectNode.SelectItem> items = new ArrayList<>();
        items.add(new JQuickProjectNode.SelectItem(new JQuickColumnRefExpression("id"), "id"));
        items.add(new JQuickProjectNode.SelectItem(multiplyExpr, "new_salary"));
        JQuickProjectNode projectNode = new JQuickProjectNode(items, employeesScan);
        JQuickPhysicalPlanNode physicalPlan = generator.generate(projectNode);
        JQuickDistributedPlan plan = new JQuickFragmenter(1).fragment(physicalPlan);
        String queryId = "arithmetic_project_test_" + System.currentTimeMillis();
        JQuickDataSet result = coordinator.executeQueryWithPlan(queryId, plan);
        result.printTable();
    }

    /**
     * 测试函数调用投影
     *
     * SQL示例：SELECT id, toUpper(name) AS upper_name, LENGTH(email) AS email_len FROM users
     */
    @Test
    public void testFunctionCallProjection() {
        JQuickTableScanNode usersScan = createTableScan("users", "u");
        List<JQuickProjectNode.SelectItem> items = new ArrayList<>();
        items.add(new JQuickProjectNode.SelectItem(new JQuickColumnRefExpression("id"), "id"));
        JQuickFunctionCallExpression upperFunc = new JQuickFunctionCallExpression("toUpper", Collections.singletonList(new JQuickColumnRefExpression("name")));
        items.add(new JQuickProjectNode.SelectItem(upperFunc, "upper_name"));
        JQuickFunctionCallExpression lengthFunc = new JQuickFunctionCallExpression("length", Collections.singletonList(new JQuickColumnRefExpression("email")));
        items.add(new JQuickProjectNode.SelectItem(lengthFunc, "email_len"));
        JQuickProjectNode projectNode = new JQuickProjectNode(items, usersScan);
        JQuickPhysicalPlanNode physicalPlan = generator.generate(projectNode);
        JQuickDistributedPlan plan = new JQuickFragmenter(1).fragment(physicalPlan);
        String queryId = "function_project_test_" + System.currentTimeMillis();
        JQuickDataSet result = coordinator.executeQueryWithPlan(queryId, plan);
        result.printTable();
    }

    /**
     * 测试CASE WHEN表达式投影
     *
     * SQL示例：SELECT id, CASE WHEN score >= 60 THEN 'PASS' ELSE 'FAIL' END AS result FROM students
     */
    @Test
    public void testCaseWhenProjection() {
        JQuickTableScanNode studentsScan = createTableScan("students", "s");
        JQuickBinaryExpression condition = new JQuickBinaryExpression(new JQuickColumnRefExpression("score"), new JQuickLiteralExpression(60), JQuickBinaryOperator.GE);
        JQuickCaseWhenExpression caseWhenExpr = new JQuickCaseWhenExpression(Arrays.asList(condition), Arrays.asList(new JQuickLiteralExpression("PASS")), new JQuickLiteralExpression("FAIL"));
        List<JQuickProjectNode.SelectItem> items = new ArrayList<>();
        items.add(new JQuickProjectNode.SelectItem(new JQuickColumnRefExpression("id"), "id"));
        items.add(new JQuickProjectNode.SelectItem(caseWhenExpr, "result"));
        JQuickProjectNode projectNode = new JQuickProjectNode(items, studentsScan);
        JQuickPhysicalPlanNode physicalPlan = generator.generate(projectNode);
        JQuickDistributedPlan plan = new JQuickFragmenter(1).fragment(physicalPlan);
        String queryId = "casewhen_project_test_" + System.currentTimeMillis();
        JQuickDataSet result = coordinator.executeQueryWithPlan(queryId, plan);
        result.printTable();
    }
    /**
     * 测试：子查询表达式投影（标量子查询）
     *
     * SQL示例：SELECT u.id, u.name, (SELECT COUNT(*) FROM orders o WHERE o.user_id = u.id) AS order_count
     *          FROM users u
     */
    @Test
    public void testSubqueryExpressionAtomProjection() {
        JQuickTableScanNode ordersScan = createTableScan("orders", "o");
        JQuickBinaryExpression subqueryFilter = new JQuickBinaryExpression(
                new JQuickColumnRefExpression("user_id", "o"),
                new JQuickColumnRefExpression("id", "u"),
                JQuickBinaryOperator.EQ
        );
        JQuickFilterNode filterNode = new JQuickFilterNode(subqueryFilter, ordersScan);
        List<JQuickGroupByNode.AggregateItem> aggregates = new ArrayList<>();
        aggregates.add(new JQuickGroupByNode.AggregateItem(null, "COUNT", "count", true));
        JQuickGroupByNode countNode = new JQuickGroupByNode(new ArrayList<>(), aggregates, filterNode, null);
        JQuickSubqueryExpression subqueryExpr = new JQuickSubqueryExpression(countNode);
        JQuickTableScanNode usersScan = createTableScan("users", "u");
        List<JQuickProjectNode.SelectItem> items = new ArrayList<>();
        items.add(new JQuickProjectNode.SelectItem(new JQuickColumnRefExpression("id", "u"), "id"));
        items.add(new JQuickProjectNode.SelectItem(new JQuickColumnRefExpression("name", "u"), "name"));
        items.add(new JQuickProjectNode.SelectItem(subqueryExpr, "order_count"));
        JQuickProjectNode projectNode = new JQuickProjectNode(items, usersScan);
        JQuickPhysicalPlanNode physicalPlan = generator.generate(projectNode);
        assertNotNull(physicalPlan);
        assertEquals("Project", physicalPlan.getNodeType());
        JQuickDistributedPlan plan = new JQuickFragmenter(1).fragment(physicalPlan);
        String queryId = "casewhen_project_test_" + System.currentTimeMillis();
        JQuickDataSet result = coordinator.executeQueryWithPlan(queryId, plan);
        result.printTable();
    }

    /**
     * 测试：CASE WHEN 表达式投影（多条件嵌套）
     *
     * SQL示例：SELECT id, CASE
     *                      WHEN score >= 90 THEN 'A'
     *                      WHEN score >= 80 THEN 'B'
     *                      WHEN score >= 60 THEN 'C'
     *                      ELSE 'D'
     *                    END AS grade
     *          FROM students
     */
    @Test
    public void testCaseWhenExpressionAtomProjection() {
        JQuickTableScanNode studentsScan = createTableScan("students");
        List<JQuickExpression> conditions = new ArrayList<>();
        List<JQuickExpression> results = new ArrayList<>();
        conditions.add(new JQuickBinaryExpression(new JQuickColumnRefExpression("score"), new JQuickLiteralExpression(90), JQuickBinaryOperator.GE));
        results.add(new JQuickLiteralExpression("A"));
        conditions.add(new JQuickBinaryExpression(new JQuickColumnRefExpression("score"), new JQuickLiteralExpression(80), JQuickBinaryOperator.GE));
        results.add(new JQuickLiteralExpression("B"));
        conditions.add(new JQuickBinaryExpression(new JQuickColumnRefExpression("score"), new JQuickLiteralExpression(60), JQuickBinaryOperator.GE));
        results.add(new JQuickLiteralExpression("C"));
        JQuickCaseWhenExpression caseWhenExpr = new JQuickCaseWhenExpression(conditions, results, new JQuickLiteralExpression("D"));
        List<JQuickProjectNode.SelectItem> items = new ArrayList<>();
        items.add(new JQuickProjectNode.SelectItem(new JQuickColumnRefExpression("id"), "id"));
        items.add(new JQuickProjectNode.SelectItem(caseWhenExpr, "grade"));
        JQuickProjectNode projectNode = new JQuickProjectNode(items, studentsScan);
        JQuickPhysicalPlanNode physicalPlan = generator.generate(projectNode);
        assertNotNull(physicalPlan);
        assertEquals("Project", physicalPlan.getNodeType());
        JQuickDistributedPlan plan = new JQuickFragmenter(1).fragment(physicalPlan);
        String queryId = "casewhen_project_test_" + System.currentTimeMillis();
        JQuickDataSet result = coordinator.executeQueryWithPlan(queryId, plan);
        result.printTable();
    }

    /**
     * 测试：数学表达式投影（复杂算术运算）
     *
     * SQL示例：SELECT id, (salary * 1.1 + bonus) * (1 - tax_rate) AS net_salary
     *          FROM employees
     */
    @Test
    public void testMathExpressionAtomProjection() {
        JQuickTableScanNode employeesScan = createTableScan("employees");
        JQuickBinaryExpression salaryTimesRate = new JQuickBinaryExpression(
                new JQuickColumnRefExpression("salary"),
                new JQuickLiteralExpression(1.1),
                JQuickBinaryOperator.MULTIPLY
        );
        JQuickBinaryExpression salaryPlusBonus = new JQuickBinaryExpression(
                salaryTimesRate,
                new JQuickColumnRefExpression("bonus"),
                JQuickBinaryOperator.PLUS
        );
        JQuickBinaryExpression oneMinusTax = new JQuickBinaryExpression(
                new JQuickLiteralExpression(1),
                new JQuickColumnRefExpression("tax_rate"),
                JQuickBinaryOperator.MINUS
        );
        JQuickBinaryExpression netSalary = new JQuickBinaryExpression(
                salaryPlusBonus,
                oneMinusTax,
                JQuickBinaryOperator.MULTIPLY
        );
        List<JQuickProjectNode.SelectItem> items = new ArrayList<>();
        items.add(new JQuickProjectNode.SelectItem(new JQuickColumnRefExpression("id"), "id"));
        items.add(new JQuickProjectNode.SelectItem(netSalary, "net_salary"));
        JQuickProjectNode projectNode = new JQuickProjectNode(items, employeesScan);
        JQuickPhysicalPlanNode physicalPlan = generator.generate(projectNode);
        assertNotNull(physicalPlan);
        assertEquals("Project", physicalPlan.getNodeType());
        JQuickDistributedPlan plan = new JQuickFragmenter(1).fragment(physicalPlan);
        String queryId = "casewhen_project_test_" + System.currentTimeMillis();
        JQuickDataSet result = coordinator.executeQueryWithPlan(queryId, plan);
        result.printTable();
    }

    /**
     * 测试：嵌套表达式投影（函数嵌套和表达式组合）
     *
     * SQL示例：SELECT id, UPPER(CONCAT(first_name, ' ', last_name)) AS full_name_upper,
     *                 LENGTH(TRIM(email)) AS email_length
     *          FROM users
     */
    @Test
    public void testNestedExpressionAtomProjection() {
        JQuickTableScanNode usersScan = createTableScan("users");
        List<JQuickExpression> concatArgs = new ArrayList<>();
        concatArgs.add(new JQuickColumnRefExpression("first_name"));
        concatArgs.add(new JQuickLiteralExpression(" "));
        concatArgs.add(new JQuickColumnRefExpression("last_name"));
        JQuickFunctionCallExpression concatFunc = new JQuickFunctionCallExpression("CONCAT", concatArgs);
        JQuickFunctionCallExpression upperFunc = new JQuickFunctionCallExpression("toUPPER", Collections.singletonList(concatFunc));
        JQuickFunctionCallExpression trimFunc = new JQuickFunctionCallExpression("TRIM", Collections.singletonList(new JQuickColumnRefExpression("email")));
        JQuickFunctionCallExpression lengthFunc = new JQuickFunctionCallExpression("LENGTH", Collections.singletonList(trimFunc));
        List<JQuickProjectNode.SelectItem> items = new ArrayList<>();
        items.add(new JQuickProjectNode.SelectItem(new JQuickColumnRefExpression("id"), "id"));
        items.add(new JQuickProjectNode.SelectItem(upperFunc, "full_name_upper"));
        items.add(new JQuickProjectNode.SelectItem(lengthFunc, "email_length"));
        JQuickProjectNode projectNode = new JQuickProjectNode(items, usersScan);
        JQuickPhysicalPlanNode physicalPlan = generator.generate(projectNode);
        assertNotNull(physicalPlan);
        assertEquals("Project", physicalPlan.getNodeType());
        JQuickDistributedPlan plan = new JQuickFragmenter(1).fragment(physicalPlan);
        String queryId = "casewhen_project_test_" + System.currentTimeMillis();
        JQuickDataSet result = coordinator.executeQueryWithPlan(queryId, plan);
        result.printTable();
    }
    /**
     * 测试JOIN投影
     *
     * SQL示例：SELECT u.id, u.name, o.order_date FROM users u JOIN orders o ON u.id = o.user_id
     */
    @Test
    public void testJoinProjection() {
        JQuickTableScanNode usersScan = createTableScan("users", "u");
        JQuickTableScanNode ordersScan = createTableScan("orders", "o");
        JQuickBinaryExpression joinCondition = new JQuickBinaryExpression(new JQuickColumnRefExpression("u.id"), new JQuickColumnRefExpression("o.user_id"), JQuickBinaryOperator.EQ);
        JQuickJoinNode joinNode = new JQuickJoinNode(com.github.paohaijiao.enums.JQuickJoinType.INNER, usersScan, ordersScan, joinCondition,null);
        List<JQuickProjectNode.SelectItem> items = new ArrayList<>();
        items.add(new JQuickProjectNode.SelectItem(new JQuickColumnRefExpression("u.id"), "id"));
        items.add(new JQuickProjectNode.SelectItem(new JQuickColumnRefExpression("u.name"), "name"));
        items.add(new JQuickProjectNode.SelectItem(new JQuickColumnRefExpression("o.order_date"), "order_date"));
        JQuickProjectNode projectNode = new JQuickProjectNode(items, joinNode);
        JQuickPhysicalPlanNode physicalPlan = generator.generate(projectNode);
        JQuickDistributedPlan plan = new JQuickFragmenter(1).fragment(physicalPlan);
        String queryId = "join_project_test_" + System.currentTimeMillis();
        JQuickDataSet result = coordinator.executeQueryWithPlan(queryId, plan);
        result.printTable();
    }

    /**
     * 测试嵌套投影
     *
     * SQL示例：SELECT id, name FROM users
     */
    @Test
    public void testNestedProjection() {
        JQuickTableScanNode usersScan = createTableScan("users", "u");
        JQuickProjectNode innerProject = createSimpleProject(usersScan, "id", "name");
        JQuickPhysicalPlanNode physicalPlan = generator.generate(innerProject);
        assertNotNull(physicalPlan);
        assertEquals("Project", physicalPlan.getNodeType());
        JQuickDistributedPlan plan = new JQuickFragmenter(1).fragment(physicalPlan);
        String queryId = "nested_project_test_" + System.currentTimeMillis();
        JQuickDataSet result = coordinator.executeQueryWithPlan(queryId, plan);
        result.printTable();
    }


    /**
     * 测试投影+排序
     *
     * SQL示例：SELECT id, name FROM users ORDER BY name
     */
    @Test
    public void testProjectionWithSort() {
        JQuickTableScanNode usersScan = createTableScan("users", "u");
        JQuickProjectNode projectNode = createSimpleProject(usersScan, "id", "name");
        List<JQuickSortNode.OrderByItem> orderByItems = new ArrayList<>();
        orderByItems.add(new JQuickSortNode.OrderByItem("name", true));
        JQuickSortNode sortNode = new JQuickSortNode(orderByItems, projectNode);
        JQuickPhysicalPlanNode physicalPlan = generator.generate(sortNode);
        JQuickDistributedPlan plan = new JQuickFragmenter(1).fragment(physicalPlan);
        String queryId = "project_sort_test_" + System.currentTimeMillis();
        JQuickDataSet result = coordinator.executeQueryWithPlan(queryId, plan);
        result.printTable();
    }

    /**
     * 测试投影+限制
     *
     * SQL示例：SELECT id, name FROM users LIMIT 3
     */
    @Test
    public void testProjectionWithLimit() {
        JQuickTableScanNode usersScan = createTableScan("users", "u");
        JQuickProjectNode projectNode = createSimpleProject(usersScan, "id", "name");
        JQuickLimitNode limitNode = new JQuickLimitNode(3, projectNode);
        JQuickPhysicalPlanNode physicalPlan = generator.generate(limitNode);
        JQuickDistributedPlan plan = new JQuickFragmenter(1).fragment(physicalPlan);
        String queryId = "project_limit_test_" + System.currentTimeMillis();
        JQuickDataSet result = coordinator.executeQueryWithPlan(queryId, plan);
        result.printTable();
    }



    /**
     * 测试字符串拼接投影
     *
     * SQL示例：SELECT id, CONCAT(first_name, ' ', last_name) AS full_name FROM users
     */
    @Test
    public void testStringConcatProjection() {
        JQuickTableScanNode usersScan = createTableScan("users", "u");
        List<JQuickExpression> concatArgs = new ArrayList<>();
        concatArgs.add(new JQuickColumnRefExpression("first_name"));
        concatArgs.add(new JQuickLiteralExpression(" "));
        concatArgs.add(new JQuickColumnRefExpression("last_name"));
        JQuickFunctionCallExpression concatFunc = new JQuickFunctionCallExpression("CONCAT", concatArgs);
        List<JQuickProjectNode.SelectItem> items = new ArrayList<>();
        items.add(new JQuickProjectNode.SelectItem(new JQuickColumnRefExpression("id"), "id"));
        items.add(new JQuickProjectNode.SelectItem(concatFunc, "full_name"));
        JQuickProjectNode projectNode = new JQuickProjectNode(items, usersScan);
        JQuickPhysicalPlanNode physicalPlan = generator.generate(projectNode);
        JQuickDistributedPlan plan = new JQuickFragmenter(1).fragment(physicalPlan);
        String queryId = "concat_project_test_" + System.currentTimeMillis();
        JQuickDataSet result = coordinator.executeQueryWithPlan(queryId, plan);
        result.printTable();
    }

    /**
     * 测试GROUP BY聚合投影
     *
     * SQL示例：SELECT category, SUM(amount) AS total FROM sales GROUP BY category HAVING SUM(amount) > 100
     */
    @Test
    public void testGroupByWithSort() {
        JQuickTableScanNode salesScan = createTableScan("sales", "s");
        List<JQuickExpression> groupKeys = createGroupKeys("category");
        List<JQuickGroupByNode.AggregateItem> aggregates = createAggregates("SUM:amount:total");
        JQuickExpression havingCondition = createHavingCondition("total", JQuickBinaryOperator.GT, 100);
        JQuickGroupByNode groupByNode = new JQuickGroupByNode(groupKeys, aggregates, salesScan, havingCondition);
        JQuickPhysicalPlanNode physicalPlan = generator.generate(groupByNode);
        JQuickDistributedPlan plan = new JQuickFragmenter(1).fragment(physicalPlan);
        String queryId = "groupby_project_test_" + System.currentTimeMillis();
        JQuickDataSet result = coordinator.executeQueryWithPlan(queryId, plan);
        result.printTable();
    }

    /**
     * 测试带别名的投影
     *
     * SQL示例：SELECT id AS user_id, name AS user_name, age AS user_age FROM users
     */
    @Test
    public void testProjectionWithAlias() {
        JQuickTableScanNode usersScan = createTableScan("users", "u");
        Map<String, String> columnToAlias = new HashMap<>();
        columnToAlias.put("id", "user_id");
        columnToAlias.put("name", "user_name");
        columnToAlias.put("age", "user_age");
        JQuickProjectNode projectNode = createProjectWithAlias(usersScan, columnToAlias);
        JQuickPhysicalPlanNode physicalPlan = generator.generate(projectNode);
        JQuickDistributedPlan plan = new JQuickFragmenter(1).fragment(physicalPlan);
        String queryId = "project_alias_test_" + System.currentTimeMillis();
        JQuickDataSet result = coordinator.executeQueryWithPlan(queryId, plan);
        result.printTable();
    }

    /**
     * 测试投影+过滤组合
     *
     * SQL示例：SELECT id, name, age FROM users WHERE status = 'active'
     */
    @Test
    public void testProjectionWithFilter() {
        JQuickTableScanNode usersScan = createTableScan("users", "u");
        JQuickFilterNode filterNode = createFilter(usersScan, "status", JQuickBinaryOperator.EQ, "active");
        JQuickProjectNode projectNode = createSimpleProject(filterNode, "id", "name", "age");
        JQuickPhysicalPlanNode physicalPlan = generator.generate(projectNode);
        JQuickDistributedPlan plan = new JQuickFragmenter(1).fragment(physicalPlan);
        String queryId = "project_filter_test_" + System.currentTimeMillis();
        JQuickDataSet result = coordinator.executeQueryWithPlan(queryId, plan);
        result.printTable();
    }
}
