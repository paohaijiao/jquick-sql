package com.github.paohaijiao.distribute.nodeExecutor.executeNode;

import com.github.andrewoma.dexx.collection.Sets;
import com.github.paohaijiao.datasource.JQuickDataSourceManager;
import com.github.paohaijiao.distributed.worker.*;
import com.github.paohaijiao.enums.JQuickBinaryOperator;
import com.github.paohaijiao.enums.JQuickSQLOperationType;
import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.expression.domain.JQuickBinaryExpression;
import com.github.paohaijiao.expression.domain.JQuickColumnRefExpression;
import com.github.paohaijiao.expression.domain.JQuickLiteralExpression;
import com.github.paohaijiao.function.manager.JQuickMethodInvocationManager;
import com.github.paohaijiao.physical.node.*;
import com.github.paohaijiao.proto.JQuickExecuteTaskRequest;
import com.github.paohaijiao.statement.JQuickColumnMeta;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;


public class JQuickNodeExecutorComprehensiveTest {

    private JQuickWorker worker;

    private JQuickNodeExecutor nodeExecutor;

    private JQuickExpressionEvaluator expressionEvaluator;

    private JQuickDataConverter dataConverter;

    private JQuickPartitionManager partitionManager;

    private JQuickWorker.JQuickTaskContext taskContext;

    @Before
    public void setUp() {
        JQuickMethodInvocationManager functionManager = JQuickMethodInvocationManager.getInstance();
        expressionEvaluator = new JQuickExpressionEvaluator(functionManager);
        dataConverter = new JQuickDataConverter();
        partitionManager = new JQuickPartitionManager();
        worker = new JQuickWorker("test-worker", 0);
        nodeExecutor = new JQuickNodeExecutor(worker, expressionEvaluator, partitionManager, dataConverter);
        JQuickExecuteTaskRequest request = JQuickExecuteTaskRequest.newBuilder()
                .setTaskId("test-task-1")
                .setQueryId("test-query-1")
                .setTaskIndex(0)
                .setTotalTasks(1)
                .setMemoryLimitBytes(1024 * 1024)
                .build();
        taskContext = worker.new JQuickTaskContext("test-task-1", request);
        JQuickDataSourceManager.clearAll();
        registerTestTables();
    }

    @After
    public void tearDown() {
        JQuickDataSourceManager.clearAll();
    }

    /**
     * 注册测试表数据
     */
    private void registerTestTables() {
        List<JQuickColumnMeta> employeeColumns = Arrays.asList(
                new JQuickColumnMeta("id", Long.class, "employee"),
                new JQuickColumnMeta("name", String.class, "employee"),
                new JQuickColumnMeta("age", Integer.class, "employee"),
                new JQuickColumnMeta("salary", Double.class, "employee"),
                new JQuickColumnMeta("department", String.class, "employee")
        );
        List<JQuickRow> employeeRows = Arrays.asList(
                createRow("id", 1L, "name", "张三", "age", 25, "salary", 8000.0, "department", "技术部"),
                createRow("id", 2L, "name", "李四", "age", 30, "salary", 10000.0, "department", "技术部"),
                createRow("id", 3L, "name", "王五", "age", 28, "salary", 9000.0, "department", "市场部"),
                createRow("id", 4L, "name", "赵六", "age", 35, "salary", 12000.0, "department", "市场部"),
                createRow("id", 5L, "name", "钱七", "age", 22, "salary", 6000.0, "department", "销售部")
        );
        JQuickDataSet employeeTable = new JQuickDataSet(employeeColumns, employeeRows);
        JQuickDataSourceManager.registerTable("employee", employeeTable);

        // department 表
        List<JQuickColumnMeta> deptColumns = Arrays.asList(
                new JQuickColumnMeta("dept_id", Long.class, "department"),
                new JQuickColumnMeta("dept_name", String.class, "department"),
                new JQuickColumnMeta("location", String.class, "department")
        );
        List<JQuickRow> deptRows = Arrays.asList(
                createRow("dept_id", 1L, "dept_name", "技术部", "location", "北京"),
                createRow("dept_id", 2L, "dept_name", "市场部", "location", "上海"),
                createRow("dept_id", 3L, "dept_name", "销售部", "location", "深圳")
        );
        JQuickDataSet deptTable = new JQuickDataSet(deptColumns, deptRows);
        JQuickDataSourceManager.registerTable("department", deptTable);

        // users 表
        List<JQuickColumnMeta> userColumns = Arrays.asList(
                new JQuickColumnMeta("id", Integer.class, "users"),
                new JQuickColumnMeta("name", String.class, "users"),
                new JQuickColumnMeta("age", Integer.class, "users"),
                new JQuickColumnMeta("status", String.class, "users")
        );
        List<JQuickRow> userRows = Arrays.asList(
                createRow("id", 1, "name", "Alice", "age", 25, "status", "active"),
                createRow("id", 2, "name", "Bob", "age", 30, "status", "active"),
                createRow("id", 3, "name", "Charlie", "age", 20, "status", "pending"),
                createRow("id", 4, "name", "David", "age", 35, "status", "inactive"),
                createRow("id", 5, "name", "Eve", "age", 28, "status", "active"),
                createRow("id", 6, "name", "Frank", "age", 22, "status", "pending"),
                createRow("id", 7, "name", "Grace", "age", 40, "status", "active"),
                createRow("id", 8, "name", "Henry", "age", 19, "status", "inactive")
        );
        JQuickDataSet usersTable = new JQuickDataSet(userColumns, userRows);
        JQuickDataSourceManager.registerTable("users", usersTable);
    }

    private JQuickRow createRow(Object... keyValues) {
        JQuickRow row = new JQuickRow();
        for (int i = 0; i < keyValues.length; i += 2) {
            row.put((String) keyValues[i], keyValues[i + 1]);
        }
        return row;
    }


    /**
     * 测试 executeTableScan - 基本表扫描
     * <p>
     * 目的：验证能够正确扫描并返回表中的所有数据
     * 预期：返回 employee 表的 5 条数据
     */
    @Test
    public void testExecuteTableScan_Basic() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null);
        JQuickDataSet result = nodeExecutor.executeNode(scanNode, taskContext);
        assertNotNull("结果不应为 null", result);
        assertEquals("employee 表应该有 5 条数据", 5, result.size());
        result.printTable();
    }

    /**
     * 测试 executeTableScan - 带过滤条件的表扫描
     * <p>
     * 目的：验证能够在表扫描时应用过滤条件
     * 预期：只返回满足条件的数据
     */
    @Test
    public void testExecuteTableScan_WithFilter() {
        JQuickExpression filterPredicate = new JQuickBinaryExpression(new JQuickColumnRefExpression("department"), new JQuickLiteralExpression("技术部"), JQuickBinaryOperator.EQ);
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, filterPredicate);
        JQuickDataSet result = nodeExecutor.executeNode(scanNode, taskContext);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("技术部应该有 2 条数据", 2, result.size());
    }
    /**
     * 测试 executeTableScan - 带过滤条件的表扫描
     * <p>
     * 目的：验证能够在表扫描时应用过滤条件
     * 预期：只返回满足条件的数据
     */
    @Test
    public void testExecuteTableScan_WithRequireColumn() {
        JQuickExpression filterPredicate = new JQuickBinaryExpression(new JQuickColumnRefExpression("department"), new JQuickLiteralExpression("技术部"), JQuickBinaryOperator.EQ);
        Set<String> columns= new HashSet<>();
        columns.add("id");
        columns.add("name");
        columns.add("age");
        columns.add("department");
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, columns, filterPredicate);
        JQuickDataSet result = nodeExecutor.executeNode(scanNode, taskContext);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("技术部应该有 2 条数据", 2, result.size());
    }

    /**
     * 测试 executeTableScan - 空表扫描
     * <p>
     * 目的：验证扫描空表时返回空结果
     * 预期：返回空数据集
     */
    @Test
    public void testExecuteTableScan_EmptyTable() {
        JQuickDataSourceManager.registerTable("empty_table", JQuickDataSet.builder().build());
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("empty_table", null, null, null);
        JQuickDataSet result = nodeExecutor.executeNode(scanNode, taskContext);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertTrue("空表应该返回空结果", result.isEmpty());
    }

    /**
     * 测试 executeFilter - 单条件过滤
     * <p>
     * 目的：验证能够正确过滤数据
     * SQL: SELECT * FROM employee WHERE department = '技术部'
     * 预期：返回 2 条数据
     */
    @Test
    public void testExecuteFilter_SingleCondition() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null);
        JQuickExpression predicate = new JQuickBinaryExpression(
                new JQuickColumnRefExpression("department"),
                new JQuickLiteralExpression("技术部"),
                JQuickBinaryOperator.EQ);
        JQuickFilterPhysicalNode filterNode = new JQuickFilterPhysicalNode(predicate, scanNode);
        JQuickDataSet result = nodeExecutor.executeNode(filterNode, taskContext);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("技术部应该有 2 条数据", 2, result.size());
        for (JQuickRow row : result.getRows()) {
            assertEquals("所有结果应该是技术部", "技术部", row.get("department"));
        }
    }

    /**
     * 测试 executeFilter - AND 条件过滤
     * <p>
     * 目的：验证能够正确处理 AND 逻辑
     * SQL: SELECT * FROM employee WHERE department = '技术部' AND salary > 9000
     * 预期：返回 1 条数据（李四）
     */
    @Test
    public void testExecuteFilter_AndCondition() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null);
        JQuickExpression condition1 = new JQuickBinaryExpression(
                new JQuickColumnRefExpression("department"),
                new JQuickLiteralExpression("技术部"),
                JQuickBinaryOperator.EQ);
        JQuickExpression condition2 = new JQuickBinaryExpression(
                new JQuickColumnRefExpression("salary"),
                new JQuickLiteralExpression(9000.0),
                JQuickBinaryOperator.GT);
        JQuickExpression predicate = new JQuickBinaryExpression(condition1, condition2, JQuickBinaryOperator.AND);
        JQuickFilterPhysicalNode filterNode = new JQuickFilterPhysicalNode(predicate, scanNode);

        JQuickDataSet result = nodeExecutor.executeNode(filterNode, taskContext);

        assertNotNull("结果不应为 null", result);
        assertEquals("应该只有 1 条数据满足条件", 1, result.size());
        assertEquals("应该是李四", "李四", result.getRows().get(0).get("name"));
    }

    /**
     * 测试 executeFilter - OR 条件过滤
     * <p>
     * 目的：验证能够正确处理 OR 逻辑
     * SQL: SELECT * FROM employee WHERE department = '技术部' OR department = '市场部'
     * 预期：返回 4 条数据
     */
    @Test
    public void testExecuteFilter_OrCondition() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null);
        JQuickExpression condition1 = new JQuickBinaryExpression(
                new JQuickColumnRefExpression("department"),
                new JQuickLiteralExpression("技术部"),
                JQuickBinaryOperator.EQ);
        JQuickExpression condition2 = new JQuickBinaryExpression(
                new JQuickColumnRefExpression("department"),
                new JQuickLiteralExpression("市场部"),
                JQuickBinaryOperator.EQ);
        JQuickExpression predicate = new JQuickBinaryExpression(condition1, condition2, JQuickBinaryOperator.OR);
        JQuickFilterPhysicalNode filterNode = new JQuickFilterPhysicalNode(predicate, scanNode);

        JQuickDataSet result = nodeExecutor.executeNode(filterNode, taskContext);

        assertNotNull("结果不应为 null", result);
        assertEquals("技术部和市场部共 4 条数据", 4, result.size());
    }

    /**
     * 测试 executeFilter - 无匹配结果
     * <p>
     * 目的：验证过滤条件无匹配时返回空结果
     * 预期：返回空数据集
     */
    @Test
    public void testExecuteFilter_NoMatch() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null);
        JQuickExpression predicate = new JQuickBinaryExpression(
                new JQuickColumnRefExpression("department"),
                new JQuickLiteralExpression("不存在的部门"),
                JQuickBinaryOperator.EQ);
        JQuickFilterPhysicalNode filterNode = new JQuickFilterPhysicalNode(predicate, scanNode);

        JQuickDataSet result = nodeExecutor.executeNode(filterNode, taskContext);

        assertNotNull("结果不应为 null", result);
        assertTrue("无匹配应该返回空结果", result.isEmpty());
    }


    /**
     * 测试 executeProject - 基本投影
     * <p>
     * 目的：验证能够正确选择指定列
     * SQL: SELECT id, name FROM employee
     * 预期：返回 5 条数据，每条只有 id 和 name 两列
     */
    @Test
    public void testExecuteProject_Basic() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null);
        List<JQuickProjectPhysicalNode.SelectItem> selectItems = new ArrayList<>();
        selectItems.add(new JQuickProjectPhysicalNode.SelectItem(new JQuickColumnRefExpression("id"), "id"));
        selectItems.add(new JQuickProjectPhysicalNode.SelectItem(new JQuickColumnRefExpression("name"), "name"));
        JQuickProjectPhysicalNode projectNode = new JQuickProjectPhysicalNode(selectItems, scanNode);

        JQuickDataSet result = nodeExecutor.executeNode(projectNode, taskContext);

        assertNotNull("结果不应为 null", result);
        assertEquals("应该有 5 条数据", 5, result.size());
        for (JQuickRow row : result.getRows()) {
            assertEquals("每行应该只有 2 列", 2, row.size());
            assertTrue("应该包含 id 列", row.containsKey("id"));
            assertTrue("应该包含 name 列", row.containsKey("name"));
        }
    }

    /**
     * 测试 executeProject - 带别名的投影
     * <p>
     * 目的：验证能够正确处理列别名
     * SQL: SELECT id AS employee_id, name AS employee_name FROM employee
     * 预期：返回的数据使用别名
     */
    @Test
    public void testExecuteProject_WithAlias() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null);
        List<JQuickProjectPhysicalNode.SelectItem> selectItems = new ArrayList<>();
        selectItems.add(new JQuickProjectPhysicalNode.SelectItem(new JQuickColumnRefExpression("id"), "employee_id"));
        selectItems.add(new JQuickProjectPhysicalNode.SelectItem(new JQuickColumnRefExpression("name"), "employee_name"));
        JQuickProjectPhysicalNode projectNode = new JQuickProjectPhysicalNode(selectItems, scanNode);

        JQuickDataSet result = nodeExecutor.executeNode(projectNode, taskContext);

        assertNotNull("结果不应为 null", result);
        for (JQuickRow row : result.getRows()) {
            assertTrue("应该包含 employee_id 列", row.containsKey("employee_id"));
            assertTrue("应该包含 employee_name 列", row.containsKey("employee_name"));
        }
    }

    /**
     * 测试 executeProject - DISTINCT 投影
     * <p>
     * 目的：验证能够正确去重
     * SQL: SELECT DISTINCT department FROM employee
     * 预期：返回 3 个不同的部门
     */
    @Test
    public void testExecuteProject_Distinct() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null);
        List<JQuickProjectPhysicalNode.SelectItem> selectItems = new ArrayList<>();
        selectItems.add(new JQuickProjectPhysicalNode.SelectItem(new JQuickColumnRefExpression("department"), "department"));
        JQuickProjectPhysicalNode projectNode = new JQuickProjectPhysicalNode(selectItems, scanNode, true);

        JQuickDataSet result = nodeExecutor.executeNode(projectNode, taskContext);

        assertNotNull("结果不应为 null", result);
        assertEquals("应该有 3 个不同的部门", 3, result.size());
    }


    /**
     * 测试 executeHashAggregate - 全局聚合（无 GROUP BY）
     *
     * 目的：验证能够正确执行全局聚合
     * SQL: SELECT COUNT(*), AVG(salary), MAX(age) FROM employee
     * 预期：返回 1 条聚合结果
     */
//    @Test
//    public void testExecuteHashAggregate_Global() {
//        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null);
//
//        List<JQuickHashAggregatePhysicalNode.AggregateFunction> aggregates = new ArrayList<>();
//        aggregates.add(new JQuickHashAggregatePhysicalNode.AggregateFunction("COUNT", null, "total_count"));
//        aggregates.add(new JQuickHashAggregatePhysicalNode.AggregateFunction("AVG", new JQuickColumnRefExpression("salary"), "avg_salary"));
//        aggregates.add(new JQuickHashAggregatePhysicalNode.AggregateFunction("MAX", new JQuickColumnRefExpression("age"), "max_age"));
//
//        JQuickHashAggregatePhysicalNode aggNode = new JQuickHashAggregatePhysicalNode(
//                Collections.emptyList(), aggregates, null, scanNode);
//
//        JQuickDataSet result = nodeExecutor.executeNode(aggNode, taskContext);
//
//        assertNotNull("结果不应为 null", result);
//        assertEquals("全局聚合应该返回 1 条数据", 1, result.size());
//        assertEquals("总数应该是 5", 5L, result.getRows().get(0).get("total_count"));
//        assertEquals("最大年龄应该是 35", 35, result.getRows().get(0).get("max_age"));
//        result.printTable();
//    }

    /**
     * 测试 executeHashAggregate - 分组聚合
     *
     * 目的：验证能够正确执行分组聚合
     * SQL: SELECT department, COUNT(*), AVG(salary) FROM employee GROUP BY department
     * 预期：返回 3 条分组聚合结果
     */
//    @Test
//    public void testExecuteHashAggregate_Grouped() {
//        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null);
//
//        List<JQuickExpression> groupKeys = new ArrayList<>();
//        groupKeys.add(new JQuickColumnRefExpression("department"));
//
//        List<JQuickHashAggregatePhysicalNode.AggregateFunction> aggregates = new ArrayList<>();
//        aggregates.add(new JQuickHashAggregatePhysicalNode.AggregateFunction("COUNT", null, "count"));
//        aggregates.add(new JQuickHashAggregatePhysicalNode.AggregateFunction("AVG", new JQuickColumnRefExpression("salary"), "avg_salary"));
//
//        JQuickHashAggregatePhysicalNode aggNode = new JQuickHashAggregatePhysicalNode(
//                groupKeys, aggregates, null, scanNode);
//
//        JQuickDataSet result = nodeExecutor.executeNode(aggNode, taskContext);
//
//        assertNotNull("结果不应为 null", result);
//        assertEquals("应该有 3 个分组", 3, result.size());
//        result.printTable();
//    }

    /**
     * 测试 executeHashAggregate - 带 HAVING 条件
     *
     * 目的：验证能够正确处理 HAVING 条件
     * SQL: SELECT department, COUNT(*) FROM employee GROUP BY department HAVING COUNT(*) >= 2
     * 预期：返回满足 HAVING 条件的分组
     */
//    @Test
//    public void testExecuteHashAggregate_WithHaving() {
//        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null);
//
//        List<JQuickExpression> groupKeys = new ArrayList<>();
//        groupKeys.add(new JQuickColumnRefExpression("department"));
//
//        List<JQuickHashAggregatePhysicalNode.AggregateFunction> aggregates = new ArrayList<>();
//        aggregates.add(new JQuickHashAggregatePhysicalNode.AggregateFunction("COUNT", null, "count"));
//
//        JQuickExpression havingCondition = new JQuickBinaryExpression(
//                new JQuickColumnRefExpression("count"),
//                new JQuickLiteralExpression(2L),
//                JQuickBinaryOperator.GE);
//
//        JQuickHashAggregatePhysicalNode aggNode = new JQuickHashAggregatePhysicalNode(
//                groupKeys, aggregates, havingCondition, scanNode);
//
//        JQuickDataSet result = nodeExecutor.executeNode(aggNode, taskContext);
//
//        assertNotNull("结果不应为 null", result);
//        // 所有部门都有至少 2 人
//        assertTrue("应该返回至少 1 个分组", result.size() >= 1);
//        result.printTable();
//    }


    /**
     * 测试 executeSort - 升序排序
     * <p>
     * 目的：验证能够正确按升序排序
     * SQL: SELECT * FROM employee ORDER BY age ASC
     * 预期：按年龄从小到大排序
     */
    @Test
    public void testExecuteSort_Ascending() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null);
        List<JQuickSortPhysicalNode.OrderByItem> orderByItems = new ArrayList<>();
        orderByItems.add(new JQuickSortPhysicalNode.OrderByItem("age", true, false));
        JQuickSortPhysicalNode sortNode = new JQuickSortPhysicalNode(orderByItems, scanNode);

        JQuickDataSet result = nodeExecutor.executeNode(sortNode, taskContext);

        assertNotNull("结果不应为 null", result);
        assertEquals("应该有 5 条数据", 5, result.size());

        // 验证排序顺序
        List<Integer> ages = new ArrayList<>();
        for (JQuickRow row : result.getRows()) {
            ages.add((Integer) row.get("age"));
        }
        for (int i = 1; i < ages.size(); i++) {
            assertTrue("年龄应该是升序的", ages.get(i - 1) <= ages.get(i));
        }
    }

    /**
     * 测试 executeSort - 降序排序
     * <p>
     * 目的：验证能够正确按降序排序
     * SQL: SELECT * FROM employee ORDER BY salary DESC
     * 预期：按工资从大到小排序
     */
    @Test
    public void testExecuteSort_Descending() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null);
        List<JQuickSortPhysicalNode.OrderByItem> orderByItems = new ArrayList<>();
        orderByItems.add(new JQuickSortPhysicalNode.OrderByItem("salary", false, false));
        JQuickSortPhysicalNode sortNode = new JQuickSortPhysicalNode(orderByItems, scanNode);

        JQuickDataSet result = nodeExecutor.executeNode(sortNode, taskContext);

        assertNotNull("结果不应为 null", result);
        assertEquals("应该有 5 条数据", 5, result.size());

        // 验证排序顺序
        List<Double> salaries = new ArrayList<>();
        for (JQuickRow row : result.getRows()) {
            salaries.add((Double) row.get("salary"));
        }
        for (int i = 1; i < salaries.size(); i++) {
            assertTrue("工资应该是降序的", salaries.get(i - 1) >= salaries.get(i));
        }
    }

    /**
     * 测试 executeSort - 多列排序
     * <p>
     * 目的：验证能够正确处理多列排序
     * SQL: SELECT * FROM employee ORDER BY department ASC, salary DESC
     * 预期：先按部门升序，再按工资降序
     */
    @Test
    public void testExecuteSort_MultipleColumns() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null);
        List<JQuickSortPhysicalNode.OrderByItem> orderByItems = new ArrayList<>();
        orderByItems.add(new JQuickSortPhysicalNode.OrderByItem("department", true, false));
        orderByItems.add(new JQuickSortPhysicalNode.OrderByItem("salary", false, false));
        JQuickSortPhysicalNode sortNode = new JQuickSortPhysicalNode(orderByItems, scanNode);

        JQuickDataSet result = nodeExecutor.executeNode(sortNode, taskContext);

        assertNotNull("结果不应为 null", result);
        assertEquals("应该有 5 条数据", 5, result.size());
        result.printTable();
    }


    /**
     * 测试 executeLimit - 基本 LIMIT
     * <p>
     * 目的：验证能够正确限制返回行数
     * SQL: SELECT * FROM employee LIMIT 3
     * 预期：返回前 3 条数据
     */
    @Test
    public void testExecuteLimit_Basic() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null);
        JQuickLimitPhysicalNode limitNode = new JQuickLimitPhysicalNode(3, 0, scanNode);

        JQuickDataSet result = nodeExecutor.executeNode(limitNode, taskContext);

        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 3 条数据", 3, result.size());
    }

    /**
     * 测试 executeLimit - LIMIT OFFSET
     * <p>
     * 目的：验证能够正确处理 OFFSET
     * SQL: SELECT * FROM employee LIMIT 2 OFFSET 2
     * 预期：跳过前 2 条，返回第 3-4 条
     */
    @Test
    public void testExecuteLimit_WithOffset() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null);
        JQuickLimitPhysicalNode limitNode = new JQuickLimitPhysicalNode(2, 2, scanNode);

        JQuickDataSet result = nodeExecutor.executeNode(limitNode, taskContext);

        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 2 条数据", 2, result.size());
    }

    /**
     * 测试 executeLimit - OFFSET 超出范围
     * <p>
     * 目的：验证 OFFSET 超出数据范围时返回空结果
     * 预期：返回空数据集
     */
    @Test
    public void testExecuteLimit_OffsetOutOfRange() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null);
        JQuickLimitPhysicalNode limitNode = new JQuickLimitPhysicalNode(10, 100, scanNode);

        JQuickDataSet result = nodeExecutor.executeNode(limitNode, taskContext);

        assertNotNull("结果不应为 null", result);
        assertTrue("OFFSET 超出范围应该返回空结果", result.isEmpty());
    }


    /**
     * 测试 executeTopN - 基本 TopN
     * <p>
     * 目的：验证能够正确返回 TopN 结果
     * SQL: SELECT * FROM employee ORDER BY salary DESC LIMIT 3
     * 预期：返回工资最高的 3 条数据
     */
    @Test
    public void testExecuteTopN_Basic() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null);
        List<JQuickSortPhysicalNode.OrderByItem> orderByItems = new ArrayList<>();
        orderByItems.add(new JQuickSortPhysicalNode.OrderByItem("salary", false, false));
        JQuickTopNPhysicalNode topNNode = new JQuickTopNPhysicalNode(orderByItems, 3, 0, scanNode);

        JQuickDataSet result = nodeExecutor.executeNode(topNNode, taskContext);

        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 3 条数据", 3, result.size());

        // 验证是工资最高的 3 人
        List<Double> salaries = new ArrayList<>();
        for (JQuickRow row : result.getRows()) {
            salaries.add((Double) row.get("salary"));
        }
        // 验证降序
        for (int i = 1; i < salaries.size(); i++) {
            assertTrue("应该是降序的", salaries.get(i - 1) >= salaries.get(i));
        }
    }


    /**
     * 测试 executeSetOperation - UNION
     * <p>
     * 目的：验证 UNION 能够正确去重合并
     * SQL: SELECT id, name FROM users WHERE status = 'active'
     * UNION
     * SELECT id, name FROM users WHERE status = 'pending'
     * 预期：返回 6 条数据（4 active + 2 pending）
     */
    @Test
    public void testExecuteSetOperation_Union() {
        // 左查询: status = 'active'
        JQuickTableScanPhysicalNode leftScan = new JQuickTableScanPhysicalNode("users", null, null, null);
        JQuickExpression leftPredicate = new JQuickBinaryExpression(
                new JQuickColumnRefExpression("status"),
                new JQuickLiteralExpression("active"),
                JQuickBinaryOperator.EQ);
        JQuickFilterPhysicalNode leftFilter = new JQuickFilterPhysicalNode(leftPredicate, leftScan);

        List<JQuickProjectPhysicalNode.SelectItem> selectItems = new ArrayList<>();
        selectItems.add(new JQuickProjectPhysicalNode.SelectItem(new JQuickColumnRefExpression("id"), "id"));
        selectItems.add(new JQuickProjectPhysicalNode.SelectItem(new JQuickColumnRefExpression("name"), "name"));
        JQuickProjectPhysicalNode leftProject = new JQuickProjectPhysicalNode(selectItems, leftFilter);

        // 右查询: status = 'pending'
        JQuickTableScanPhysicalNode rightScan = new JQuickTableScanPhysicalNode("users", null, null, null);
        JQuickExpression rightPredicate = new JQuickBinaryExpression(
                new JQuickColumnRefExpression("status"),
                new JQuickLiteralExpression("pending"),
                JQuickBinaryOperator.EQ);
        JQuickFilterPhysicalNode rightFilter = new JQuickFilterPhysicalNode(rightPredicate, rightScan);
        JQuickProjectPhysicalNode rightProject = new JQuickProjectPhysicalNode(selectItems, rightFilter);

        // UNION 操作
        JQuickSetOperationPhysicalNode unionNode = new JQuickSetOperationPhysicalNode(
                JQuickSQLOperationType.UNION, leftProject, rightProject);

        JQuickDataSet result = nodeExecutor.executeNode(unionNode, taskContext);

        assertNotNull("结果不应为 null", result);
        assertEquals("UNION 应该返回 6 条数据", 6, result.size());
        result.printTable();
    }

    /**
     * 测试 executeSetOperation - UNION ALL
     * <p>
     * 目的：验证 UNION ALL 保留所有数据（包括重复）
     * 预期：返回所有数据，不去重
     */
    @Test
    public void testExecuteSetOperation_UnionAll() {
        JQuickTableScanPhysicalNode leftScan = new JQuickTableScanPhysicalNode("users", null, null, null);
        JQuickExpression leftPredicate = new JQuickBinaryExpression(
                new JQuickColumnRefExpression("status"),
                new JQuickLiteralExpression("active"),
                JQuickBinaryOperator.EQ);
        JQuickFilterPhysicalNode leftFilter = new JQuickFilterPhysicalNode(leftPredicate, leftScan);

        List<JQuickProjectPhysicalNode.SelectItem> selectItems = new ArrayList<>();
        selectItems.add(new JQuickProjectPhysicalNode.SelectItem(new JQuickColumnRefExpression("id"), "id"));
        selectItems.add(new JQuickProjectPhysicalNode.SelectItem(new JQuickColumnRefExpression("name"), "name"));
        JQuickProjectPhysicalNode leftProject = new JQuickProjectPhysicalNode(selectItems, leftFilter);

        JQuickTableScanPhysicalNode rightScan = new JQuickTableScanPhysicalNode("users", null, null, null);
        JQuickExpression rightPredicate = new JQuickBinaryExpression(
                new JQuickColumnRefExpression("status"),
                new JQuickLiteralExpression("active"),
                JQuickBinaryOperator.EQ);
        JQuickFilterPhysicalNode rightFilter = new JQuickFilterPhysicalNode(rightPredicate, rightScan);
        JQuickProjectPhysicalNode rightProject = new JQuickProjectPhysicalNode(selectItems, rightFilter);

        JQuickSetOperationPhysicalNode unionAllNode = new JQuickSetOperationPhysicalNode(
                JQuickSQLOperationType.UNION_ALL, leftProject, rightProject);

        JQuickDataSet result = nodeExecutor.executeNode(unionAllNode, taskContext);

        assertNotNull("结果不应为 null", result);
        assertEquals("UNION ALL 应该返回 8 条数据 (4+4)", 8, result.size());
    }

    /**
     * 测试 executeSetOperation - INTERSECT
     * <p>
     * 目的：验证 INTERSECT 返回交集
     * 预期：返回两个查询的交集
     */
    @Test
    public void testExecuteSetOperation_Intersect() {
        JQuickTableScanPhysicalNode leftScan = new JQuickTableScanPhysicalNode("users", null, null, null);
        JQuickExpression leftPredicate = new JQuickBinaryExpression(
                new JQuickColumnRefExpression("status"),
                new JQuickLiteralExpression("active"),
                JQuickBinaryOperator.EQ);
        JQuickFilterPhysicalNode leftFilter = new JQuickFilterPhysicalNode(leftPredicate, leftScan);

        List<JQuickProjectPhysicalNode.SelectItem> selectItems = new ArrayList<>();
        selectItems.add(new JQuickProjectPhysicalNode.SelectItem(new JQuickColumnRefExpression("id"), "id"));
        selectItems.add(new JQuickProjectPhysicalNode.SelectItem(new JQuickColumnRefExpression("name"), "name"));
        JQuickProjectPhysicalNode leftProject = new JQuickProjectPhysicalNode(selectItems, leftFilter);

        JQuickTableScanPhysicalNode rightScan = new JQuickTableScanPhysicalNode("users", null, null, null);
        JQuickExpression rightPredicate = new JQuickBinaryExpression(
                new JQuickColumnRefExpression("age"),
                new JQuickLiteralExpression(25),
                JQuickBinaryOperator.GT);
        JQuickFilterPhysicalNode rightFilter = new JQuickFilterPhysicalNode(rightPredicate, rightScan);
        JQuickProjectPhysicalNode rightProject = new JQuickProjectPhysicalNode(selectItems, rightFilter);

        JQuickSetOperationPhysicalNode intersectNode = new JQuickSetOperationPhysicalNode(
                JQuickSQLOperationType.INTERSECT, leftProject, rightProject);

        JQuickDataSet result = nodeExecutor.executeNode(intersectNode, taskContext);

        assertNotNull("结果不应为 null", result);
        assertTrue("INTERSECT 应该返回交集数据", result.size() > 0);
        result.printTable();
    }

    /**
     * 测试 executeSetOperation - EXCEPT
     * <p>
     * 目的：验证 EXCEPT 返回差集
     * 预期：返回左查询减去右查询的结果
     */
    @Test
    public void testExecuteSetOperation_Except() {
        JQuickTableScanPhysicalNode leftScan = new JQuickTableScanPhysicalNode("users", null, null, null);
        JQuickExpression leftPredicate = new JQuickBinaryExpression(
                new JQuickColumnRefExpression("status"),
                new JQuickLiteralExpression("active"),
                JQuickBinaryOperator.EQ);
        JQuickFilterPhysicalNode leftFilter = new JQuickFilterPhysicalNode(leftPredicate, leftScan);

        List<JQuickProjectPhysicalNode.SelectItem> selectItems = new ArrayList<>();
        selectItems.add(new JQuickProjectPhysicalNode.SelectItem(new JQuickColumnRefExpression("id"), "id"));
        selectItems.add(new JQuickProjectPhysicalNode.SelectItem(new JQuickColumnRefExpression("name"), "name"));
        JQuickProjectPhysicalNode leftProject = new JQuickProjectPhysicalNode(selectItems, leftFilter);

        JQuickTableScanPhysicalNode rightScan = new JQuickTableScanPhysicalNode("users", null, null, null);
        JQuickExpression rightPredicate = new JQuickBinaryExpression(
                new JQuickColumnRefExpression("age"),
                new JQuickLiteralExpression(30),
                JQuickBinaryOperator.GT);
        JQuickFilterPhysicalNode rightFilter = new JQuickFilterPhysicalNode(rightPredicate, rightScan);
        JQuickProjectPhysicalNode rightProject = new JQuickProjectPhysicalNode(selectItems, rightFilter);

        JQuickSetOperationPhysicalNode exceptNode = new JQuickSetOperationPhysicalNode(
                JQuickSQLOperationType.EXCEPT, leftProject, rightProject);

        JQuickDataSet result = nodeExecutor.executeNode(exceptNode, taskContext);

        assertNotNull("结果不应为 null", result);
        assertTrue("EXCEPT 应该返回差集数据", result.size() > 0);
        result.printTable();
    }


    /**
     * 测试 executeValues - 基本值列表
     *
     * 目的：验证能够正确处理 VALUES 子句
     * SQL: VALUES (1, 'a'), (2, 'b'), (3, 'c')
     * 预期：返回 3 条数据
     //     */
//    @Test
//    public void testExecuteValues_Basic() {
//        List<String> columnNames = Arrays.asList("id", "name");
//        List<Class<?>> columnTypes = Arrays.asList(Integer.class, String.class);
//        List<List<Object>> rows = Arrays.asList(
//                Arrays.asList(1, "a"),
//                Arrays.asList(2, "b"),
//                Arrays.asList(3, "c")
//        );
//        JQuickValuesPhysicalNode valuesNode = new JQuickValuesPhysicalNode(columnNames, columnTypes, rows);
//
//        JQuickDataSet result = nodeExecutor.executeNode(valuesNode, taskContext);
//
//        assertNotNull("结果不应为 null", result);
//        assertEquals("应该返回 3 条数据", 3, result.size());
//        assertEquals("第一行的 id 应该是 1", 1, result.getRows().get(0).get("id"));
//        assertEquals("第一行的 name 应该是 a", "a", result.getRows().get(0).get("name"));
//    }

    /**
     * 测试 executeValues - 空值列表
     *
     * 目的：验证空 VALUES 返回空结果
     * 预期：返回空数据集
     */
//    @Test
//    public void testExecuteValues_Empty() {
//        List<String> columnNames = Arrays.asList("id", "name");
//        List<Class<?>> columnTypes = Arrays.asList(Integer.class, String.class);
//        List<List<Object>> rows = Collections.emptyList();
//        JQuickValuesPhysicalNode valuesNode = new JQuickValuesPhysicalNode(columnNames, columnTypes, rows);
//
//        JQuickDataSet result = nodeExecutor.executeNode(valuesNode, taskContext);
//
//        assertNotNull("结果不应为 null", result);
//        assertTrue("空 VALUES 应该返回空结果", result.isEmpty());
//    }


    /**
     * 测试组合操作 - Filter + Project + Sort + Limit
     * <p>
     * 目的：验证多个操作的组合执行
     * SQL: SELECT id, name, salary FROM employee WHERE department = '技术部' ORDER BY salary DESC LIMIT 2
     * 预期：返回技术部工资最高的 2 人
     */
    @Test
    public void testCombined_FilterProjectSortLimit() {
        // TableScan
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null);

        // Filter
        JQuickExpression predicate = new JQuickBinaryExpression(
                new JQuickColumnRefExpression("department"),
                new JQuickLiteralExpression("技术部"),
                JQuickBinaryOperator.EQ);
        JQuickFilterPhysicalNode filterNode = new JQuickFilterPhysicalNode(predicate, scanNode);

        // Project
        List<JQuickProjectPhysicalNode.SelectItem> selectItems = new ArrayList<>();
        selectItems.add(new JQuickProjectPhysicalNode.SelectItem(new JQuickColumnRefExpression("id"), "id"));
        selectItems.add(new JQuickProjectPhysicalNode.SelectItem(new JQuickColumnRefExpression("name"), "name"));
        selectItems.add(new JQuickProjectPhysicalNode.SelectItem(new JQuickColumnRefExpression("salary"), "salary"));
        JQuickProjectPhysicalNode projectNode = new JQuickProjectPhysicalNode(selectItems, filterNode);

        // Sort
        List<JQuickSortPhysicalNode.OrderByItem> orderByItems = new ArrayList<>();
        orderByItems.add(new JQuickSortPhysicalNode.OrderByItem("salary", false, false));
        JQuickSortPhysicalNode sortNode = new JQuickSortPhysicalNode(orderByItems, projectNode);

        // Limit
        JQuickLimitPhysicalNode limitNode = new JQuickLimitPhysicalNode(2, 0, sortNode);

        JQuickDataSet result = nodeExecutor.executeNode(limitNode, taskContext);

        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 2 条数据", 2, result.size());

        // 验证结果
        assertEquals("工资最高的应该是李四", "李四", result.getRows().get(0).get("name"));
        assertEquals("第二高应该是张三", "张三", result.getRows().get(1).get("name"));

        result.printTable();
    }

    /**
     * 测试组合操作 - Filter + Aggregate
     *
     * 目的：验证过滤后聚合
     * SQL: SELECT department, COUNT(*), AVG(salary) FROM employee WHERE age > 25 GROUP BY department
     * 预期：返回过滤后的分组聚合结果
     */
//    @Test
//    public void testCombined_FilterAggregate() {
//        // TableScan
//        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null);
//
//        // Filter: age > 25
//        JQuickExpression predicate = new JQuickBinaryExpression(
//                new JQuickColumnRefExpression("age"),
//                new JQuickLiteralExpression(25),
//                JQuickBinaryOperator.GT);
//        JQuickFilterPhysicalNode filterNode = new JQuickFilterPhysicalNode(predicate, scanNode);
//
//        // Aggregate
//        List<JQuickExpression> groupKeys = new ArrayList<>();
//        groupKeys.add(new JQuickColumnRefExpression("department"));
//
//        List<JQuickHashAggregatePhysicalNode.AggregateFunction> aggregates = new ArrayList<>();
//        aggregates.add(new JQuickHashAggregatePhysicalNode.AggregateFunction("COUNT", null, "count"));
//        aggregates.add(new JQuickHashAggregatePhysicalNode.AggregateFunction("AVG", new JQuickColumnRefExpression("salary"), "avg_salary"));
//
//        JQuickHashAggregatePhysicalNode aggNode = new JQuickHashAggregatePhysicalNode(
//                groupKeys, aggregates, null, filterNode);
//
//        JQuickDataSet result = nodeExecutor.executeNode(aggNode, taskContext);
//
//        assertNotNull("结果不应为 null", result);
//        assertTrue("应该返回分组聚合结果", result.size() > 0);
//        result.printTable();
//    }


    /**
     * 测试 null 节点
     * <p>
     * 目的：验证传入 null 节点时返回空结果
     * 预期：返回空数据集
     */
    @Test
    public void testNullNode() {
        JQuickDataSet result = nodeExecutor.executeNode(null, taskContext);

        assertNotNull("结果不应为 null", result);
        assertTrue("null 节点应该返回空结果", result.isEmpty());
    }

    /**
     * 测试空数据集的聚合
     *
     * 目的：验证空数据集的聚合操作
     * 预期：返回正确的聚合结果（COUNT 为 0）
     */
//    @Test
//    public void testAggregate_EmptyInput() {
//        JQuickDataSourceManager.registerTable("empty_table", JQuickDataSet.builder().build());
//        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("empty_table", null, null, null);
//
//        List<JQuickHashAggregatePhysicalNode.AggregateFunction> aggregates = new ArrayList<>();
//        aggregates.add(new JQuickHashAggregatePhysicalNode.AggregateFunction("COUNT", null, "count"));
//
//        JQuickHashAggregatePhysicalNode aggNode = new JQuickHashAggregatePhysicalNode(
//                Collections.emptyList(), aggregates, null, scanNode);
//
//        JQuickDataSet result = nodeExecutor.executeNode(aggNode, taskContext);
//
//        assertNotNull("结果不应为 null", result);
//        assertEquals("空表的全局 COUNT 应该返回 1 条数据", 1, result.size());
//        assertEquals("COUNT 应该是 0", 0L, result.getRows().get(0).get("count"));
//    }
}
