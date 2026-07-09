package com.github.paohaijiao.distribute.nodeExecutor.executeNode.project;

import com.github.paohaijiao.datasource.JQuickDataSourceManager;
import com.github.paohaijiao.distributed.worker.*;
import com.github.paohaijiao.enums.JQuickBinaryOperator;
import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.expression.domain.JQuickBinaryExpression;
import com.github.paohaijiao.expression.domain.JQuickColumnRefExpression;
import com.github.paohaijiao.expression.domain.JQuickLiteralExpression;
import com.github.paohaijiao.function.manager.JQuickMethodInvocationManager;
import com.github.paohaijiao.physical.node.JQuickProjectPhysicalNode;
import com.github.paohaijiao.physical.node.JQuickTableScanPhysicalNode;
import com.github.paohaijiao.proto.JQuickExecuteTaskRequest;
import com.github.paohaijiao.statement.JQuickColumnMeta;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

/**
 * JQuickProjectPhysicalNode 单元测试
 * 
 * 测试范围：
 * 1. 基本投影（选择列）
 * 2. 带别名的投影
 * 3. DISTINCT 投影
 * 4. 表达式投影（计算列）
 * 5. 多列投影
 * 6. 边界条件
 */
public class JQuickProjectPhysicalNodeTest {

    private JQuickWorker worker;

    private JQuickNodeExecutor nodeExecutor;

    private JQuickExpressionEvaluator expressionEvaluator;

    private JQuickDataConverter dataConverter;

    private JQuickPartitionManager partitionManager;

    @Before
    public void setUp() {
        JQuickMethodInvocationManager functionManager = JQuickMethodInvocationManager.getInstance();
        expressionEvaluator = new JQuickExpressionEvaluator(functionManager);
        dataConverter = new JQuickDataConverter();
        partitionManager = new JQuickPartitionManager();
        worker = new JQuickWorker("test-worker", 0);
        nodeExecutor = new JQuickNodeExecutor(worker, expressionEvaluator, partitionManager, dataConverter);
        JQuickDataSourceManager.clearAll();
        registerTestTables();
    }

    @After
    public void tearDown() {
        JQuickDataSourceManager.clearAll();
        if (partitionManager != null) {
            partitionManager.shutdown();
        }
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
        List<JQuickRow> employeeRows = new ArrayList<>();


        employeeRows.add(createEmployeeRow(1L, "Alice", 25, 8000.0, "技术部"));
        employeeRows.add(createEmployeeRow(2L, "Bob", 30, 10000.0, "技术部"));
        employeeRows.add(createEmployeeRow(3L, "Charlie", 35, 12000.0, "市场部"));
        employeeRows.add(createEmployeeRow(4L, "David", 28, 9000.0, "市场部"));
        employeeRows.add(createEmployeeRow(5L, "Eve", 22, 7000.0, "人事部"));
        employeeRows.add(createEmployeeRow(6L, "Frank", 40, 15000.0, "技术部"));
        employeeRows.add(createEmployeeRow(7L, "Grace", 32, 11000.0, "销售部"));
        employeeRows.add(createEmployeeRow(8L, "Henry", 27, 8500.0, "销售部"));
        employeeRows.add(createEmployeeRow(9L, "Alice", 25, 8000.0, "技术部"));
        employeeRows.add(createEmployeeRow(10L, "Bob", 30, 10000.0, "技术部"));
        JQuickDataSet employeeTable = new JQuickDataSet(employeeColumns, employeeRows);
        JQuickDataSourceManager.registerTable("employee", employeeTable);
    }

    private JQuickRow createEmployeeRow(Long id, String name, Integer age, Double salary, String department) {
        JQuickRow row = new JQuickRow();
        row.put("id", id);
        row.put("name", name);
        row.put("age", age);
        row.put("salary", salary);
        row.put("department", department);
        return row;
    }

    /**
     * 创建任务上下文
     */
    private JQuickWorker.JQuickTaskContext createTaskContext() {
        JQuickExecuteTaskRequest request = JQuickExecuteTaskRequest.newBuilder()
                .setTaskId("test-task-0")
                .setQueryId("test-query-1")
                .setTaskIndex(0)
                .setTotalTasks(1)
                .setMemoryLimitBytes(1024 * 1024)
                .build();
        return worker.new JQuickTaskContext("test-task-0", request);
    }

    /**
     * 测试单列投影
     * 
     * 目的：验证能够正确投影单列数据
     * 预期：只返回指定的列
     */
    @Test
    public void testProject_SingleColumn() {
        List<JQuickProjectPhysicalNode.SelectItem> selectItems = Arrays.asList(new JQuickProjectPhysicalNode.SelectItem(new JQuickColumnRefExpression("name"), "name"));
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickProjectPhysicalNode projectNode = new JQuickProjectPhysicalNode(selectItems, scanNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(projectNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该有 10 条数据", 10, result.size());
        assertTrue("应该包含 name 列", result.getRows().get(0).containsKey("name"));
    }

    /**
     * 测试多列投影
     * 
     * 目的：验证能够正确投影多列数据
     * 预期：返回指定的多列
     */
    @Test
    public void testProject_MultipleColumns() {
        List<JQuickProjectPhysicalNode.SelectItem> selectItems = Arrays.asList(
                new JQuickProjectPhysicalNode.SelectItem(new JQuickColumnRefExpression("id"), "id"),
                new JQuickProjectPhysicalNode.SelectItem(new JQuickColumnRefExpression("name"), "name"),
                new JQuickProjectPhysicalNode.SelectItem(new JQuickColumnRefExpression("salary"), "salary")
        );
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickProjectPhysicalNode projectNode = new JQuickProjectPhysicalNode(selectItems, scanNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(projectNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该有 10 条数据", 10, result.size());
        JQuickRow firstRow = result.getRows().get(0);
        assertTrue("应该包含 id 列", firstRow.containsKey("id"));
        assertTrue("应该包含 name 列", firstRow.containsKey("name"));
        assertTrue("应该包含 salary 列", firstRow.containsKey("salary"));
    }

    /**
     * 测试全部列投影
     * 
     * 目的：验证能够投影所有列
     * 预期：返回所有列
     */
    @Test
    public void testProject_AllColumns() {
        List<JQuickProjectPhysicalNode.SelectItem> selectItems = Arrays.asList(
                new JQuickProjectPhysicalNode.SelectItem(new JQuickColumnRefExpression("id"), "id"),
                new JQuickProjectPhysicalNode.SelectItem(new JQuickColumnRefExpression("name"), "name"),
                new JQuickProjectPhysicalNode.SelectItem(new JQuickColumnRefExpression("age"), "age"),
                new JQuickProjectPhysicalNode.SelectItem(new JQuickColumnRefExpression("salary"), "salary"),
                new JQuickProjectPhysicalNode.SelectItem(new JQuickColumnRefExpression("department"), "department")
        );
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickProjectPhysicalNode projectNode = new JQuickProjectPhysicalNode(selectItems, scanNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(projectNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该有 10 条数据", 10, result.size());
        assertEquals("应该有 5 列", 5, result.getColumns().size());
    }
    /**
     * 测试带别名的投影
     * 
     * 目的：验证能够正确设置列别名
     * 预期：返回带别名的列
     */
    @Test
    public void testProject_WithAlias() {
        List<JQuickProjectPhysicalNode.SelectItem> selectItems = Arrays.asList(
                new JQuickProjectPhysicalNode.SelectItem(new JQuickColumnRefExpression("id"), "employee_id"),
                new JQuickProjectPhysicalNode.SelectItem(new JQuickColumnRefExpression("name"), "employee_name"),
                new JQuickProjectPhysicalNode.SelectItem(new JQuickColumnRefExpression("salary"), "monthly_salary")
        );
        
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickProjectPhysicalNode projectNode = new JQuickProjectPhysicalNode(selectItems, scanNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(projectNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        JQuickRow firstRow = result.getRows().get(0);
        assertTrue("应该包含 employee_id 列", firstRow.containsKey("employee_id"));
        assertTrue("应该包含 employee_name 列", firstRow.containsKey("employee_name"));
        assertTrue("应该包含 monthly_salary 列", firstRow.containsKey("monthly_salary"));
    }

    /**
     * 测试混合别名和无别名
     * 
     * 目的：验证混合使用别名和原列名
     * 预期：正确处理混合情况
     */
    @Test
    public void testProject_MixedAlias() {
        List<JQuickProjectPhysicalNode.SelectItem> selectItems = Arrays.asList(
                new JQuickProjectPhysicalNode.SelectItem(new JQuickColumnRefExpression("id"), "id"),
                new JQuickProjectPhysicalNode.SelectItem(new JQuickColumnRefExpression("name"), "employee_name"),
                new JQuickProjectPhysicalNode.SelectItem(new JQuickColumnRefExpression("salary"), "salary")
        );
        
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickProjectPhysicalNode projectNode = new JQuickProjectPhysicalNode(selectItems, scanNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(projectNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        JQuickRow firstRow = result.getRows().get(0);
        assertTrue("应该包含 id 列", firstRow.containsKey("id"));
        assertTrue("应该包含 employee_name 列", firstRow.containsKey("employee_name"));
        assertTrue("应该包含 salary 列", firstRow.containsKey("salary"));
    }
    /**
     * 测试 DISTINCT 投影
     * 
     * 目的：验证 DISTINCT 能够正确去重
     * 预期：返回不重复的数据
     */
    @Test
    public void testProject_Distinct() {
        List<JQuickProjectPhysicalNode.SelectItem> selectItems = Arrays.asList(
                new JQuickProjectPhysicalNode.SelectItem(new JQuickColumnRefExpression("name"), "name"),
                new JQuickProjectPhysicalNode.SelectItem(new JQuickColumnRefExpression("age"), "age")
        );
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickProjectPhysicalNode projectNode = new JQuickProjectPhysicalNode(selectItems, scanNode, true);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(projectNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        // 原始数据有 10 条，但 Alice 和 Bob 各有 2 条重复，所以去重后应该是 8 条
        assertEquals("DISTINCT 后应该有 8 条数据", 8, result.size());
    }

    /**
     * 测试 DISTINCT 单列
     * 
     * 目的：验证单列 DISTINCT 去重
     * 预期：返回不重复的值
     */
    @Test
    public void testProject_DistinctSingleColumn() {
        List<JQuickProjectPhysicalNode.SelectItem> selectItems = Arrays.asList(
                new JQuickProjectPhysicalNode.SelectItem(new JQuickColumnRefExpression("department"), "department")
        );
        
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickProjectPhysicalNode projectNode = new JQuickProjectPhysicalNode(selectItems, scanNode, true);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(projectNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("DISTINCT 后应该有 4 个部门", 4, result.size());
        Set<String> departments = new HashSet<>();
        for (JQuickRow row : result.getRows()) {
            departments.add((String) row.get("department"));
        }
        assertEquals("应该有 4 个不同的部门", 4, departments.size());
    }

    /**
     * 测试 DISTINCT 全部唯一
     * 
     * 目的：验证所有数据都唯一时 DISTINCT 的行为
     * 预期：返回全部数据
     */
    @Test
    public void testProject_DistinctAllUnique() {
        List<JQuickProjectPhysicalNode.SelectItem> selectItems = Arrays.asList(
                new JQuickProjectPhysicalNode.SelectItem(new JQuickColumnRefExpression("id"), "id"),
                new JQuickProjectPhysicalNode.SelectItem(new JQuickColumnRefExpression("name"), "name")
        );
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickProjectPhysicalNode projectNode = new JQuickProjectPhysicalNode(selectItems, scanNode, true);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(projectNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        // id 是唯一的，所以 DISTINCT 后仍然是 10 条
        assertEquals("DISTINCT 后应该有 10 条数据", 10, result.size());
    }
    /**
     * 测试算术表达式投影
     * 
     * 目的：验证能够正确计算算术表达式
     * 预期：返回计算后的结果
     */
    @Test
    public void testProject_ArithmeticExpression() {
        // salary * 12 AS annual_salary
        List<JQuickProjectPhysicalNode.SelectItem> selectItems = Arrays.asList(
                new JQuickProjectPhysicalNode.SelectItem(new JQuickColumnRefExpression("name"), "name"),
                new JQuickProjectPhysicalNode.SelectItem(new JQuickBinaryExpression(new JQuickColumnRefExpression("salary"), new JQuickLiteralExpression(12), JQuickBinaryOperator.MULTIPLY), "annual_salary")
        );
        
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickProjectPhysicalNode projectNode = new JQuickProjectPhysicalNode(selectItems, scanNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(projectNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该有 10 条数据", 10, result.size());
        JQuickRow firstRow = result.getRows().get(0);
        assertTrue("应该包含 annual_salary 列", firstRow.containsKey("annual_salary"));
        // 验证计算结果：8000 * 12 = 96000
        assertEquals("年薪应该是 96000", 96000.0, (Double) firstRow.get("annual_salary"), 0.01);
    }

    /**
     * 测试复杂算术表达式
     * 
     * 目的：验证复杂算术表达式的计算
     * 预期：正确计算复杂表达式
     */
    @Test
    public void testProject_ComplexArithmetic() {
        // (salary + 1000) * 1.1 AS adjusted_salary
        JQuickExpression innerExpr = new JQuickBinaryExpression(new JQuickColumnRefExpression("salary"), new JQuickLiteralExpression(1000.0), JQuickBinaryOperator.PLUS);
        JQuickExpression outerExpr = new JQuickBinaryExpression(innerExpr, new JQuickLiteralExpression(1.1), JQuickBinaryOperator.MULTIPLY);
        List<JQuickProjectPhysicalNode.SelectItem> selectItems = Arrays.asList(
                new JQuickProjectPhysicalNode.SelectItem(new JQuickColumnRefExpression("name"), "name"),
                new JQuickProjectPhysicalNode.SelectItem(outerExpr, "adjusted_salary")
        );
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickProjectPhysicalNode projectNode = new JQuickProjectPhysicalNode(selectItems, scanNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(projectNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        JQuickRow firstRow = result.getRows().get(0);
        // (8000 + 1000) * 1.1 = 9900
        assertEquals("调整后薪资应该是 9900", 9900.0, (Double) firstRow.get("adjusted_salary"), 0.01);
    }

    /**
     * 测试字面量投影
     * 
     * 目的：验证能够投影常量值
     * 预期：返回常量列
     */
    @Test
    public void testProject_Literal() {
        List<JQuickProjectPhysicalNode.SelectItem> selectItems = Arrays.asList(
                new JQuickProjectPhysicalNode.SelectItem(new JQuickColumnRefExpression("name"), "name"),
                new JQuickProjectPhysicalNode.SelectItem(new JQuickLiteralExpression("Employee"), "type"),
                new JQuickProjectPhysicalNode.SelectItem(new JQuickLiteralExpression(2024), "year")
        );
        
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickProjectPhysicalNode projectNode = new JQuickProjectPhysicalNode(selectItems, scanNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(projectNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        for (JQuickRow row : result.getRows()) {
            assertEquals("type 列应该是 Employee", "Employee", row.get("type"));
            assertEquals("year 列应该是 2024", 2024, row.get("year"));
        }
    }
    /**
     * 测试空表投影
     * 
     * 目的：验证对空表进行投影时返回空结果
     * 预期：返回空数据集
     */
    @Test
    public void testProject_EmptyTable() {
        JQuickDataSourceManager.registerTable("empty_table", JQuickDataSet.builder().build());
        List<JQuickProjectPhysicalNode.SelectItem> selectItems = Arrays.asList(
                new JQuickProjectPhysicalNode.SelectItem(new JQuickColumnRefExpression("id"), "id")
        );
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("empty_table", null, null, null, null);
        JQuickProjectPhysicalNode projectNode = new JQuickProjectPhysicalNode(selectItems, scanNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(projectNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertTrue("空表投影应该返回空结果", result.isEmpty());
    }

    /**
     * 测试空投影列表
     * 
     * 目的：验证投影列表为空时的行为
     * 预期：返回空数据集或抛出异常
     */
    @Test
    public void testProject_EmptySelectItems() {
        List<JQuickProjectPhysicalNode.SelectItem> selectItems = Collections.emptyList();
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickProjectPhysicalNode projectNode = new JQuickProjectPhysicalNode(selectItems, scanNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(projectNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
    }

    /**
     * 测试包含 null 值的投影
     * 
     * 目的：验证能够正确处理 null 值
     * 预期：null 值被正确传递
     */
    @Test
    public void testProject_WithNullValues() {
        // 创建包含 null 值的表
        JQuickDataSet.Builder builder = JQuickDataSet.builder();
        builder.addColumn("id", Long.class, "null_table");
        builder.addColumn("name", String.class, "null_table");
        builder.addColumn("value", Double.class, "null_table");
        for (int i = 0; i < 5; i++) {
            JQuickRow row = new JQuickRow();
            row.put("id", (long) i);
            row.put("name", i % 2 == 0 ? "name_" + i : null);
            row.put("value", i % 2 == 0 ? i * 1.5 : null);
            builder.addRow(row);
        }
        JQuickDataSourceManager.registerTable("null_table", builder.build());
        List<JQuickProjectPhysicalNode.SelectItem> selectItems = Arrays.asList(
                new JQuickProjectPhysicalNode.SelectItem(new JQuickColumnRefExpression("id"), "id"),
                new JQuickProjectPhysicalNode.SelectItem(new JQuickColumnRefExpression("name"), "name"),
                new JQuickProjectPhysicalNode.SelectItem(new JQuickColumnRefExpression("value"), "value")
        );
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("null_table", null, null, null, null);
        JQuickProjectPhysicalNode projectNode = new JQuickProjectPhysicalNode(selectItems, scanNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(projectNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该有 5 条数据", 5, result.size());
        int nullCount = 0;
        for (JQuickRow row : result.getRows()) {
            if (row.get("name") == null) nullCount++;
        }
        assertEquals("应该有 2 个 null 值", 2, nullCount);
    }

    /**
     * 测试大数据量投影
     * 
     * 目的：验证大数据量投影的性能
     * 预期：能够正确处理大数据量
     */
    @Test
    public void testProject_LargeData() {
        // 创建大数据量表
        JQuickDataSet.Builder builder = JQuickDataSet.builder();
        builder.addColumn("id", Long.class, "large_table");
        builder.addColumn("data", String.class, "large_table");
        int rowCount = 10000;
        for (int i = 0; i < rowCount; i++) {
            JQuickRow row = new JQuickRow();
            row.put("id", (long) i);
            row.put("data", "data_" + i);
            builder.addRow(row);
        }
        JQuickDataSourceManager.registerTable("large_table", builder.build());
        List<JQuickProjectPhysicalNode.SelectItem> selectItems = Arrays.asList(
                new JQuickProjectPhysicalNode.SelectItem(new JQuickColumnRefExpression("id"), "id"),
                new JQuickProjectPhysicalNode.SelectItem(new JQuickColumnRefExpression("data"), "data")
        );
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("large_table", null, null, null, null);
        JQuickProjectPhysicalNode projectNode = new JQuickProjectPhysicalNode(selectItems, scanNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        long startTime = System.currentTimeMillis();
        JQuickDataSet result = nodeExecutor.executeNode(projectNode, context);
        result.printTable();
        long endTime = System.currentTimeMillis();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该有 10000 条数据", rowCount, result.size());
        System.out.println("投影 " + rowCount + " 条数据耗时: " + (endTime - startTime) + "ms");
    }


    /**
     * 测试嵌套投影
     * 
     * 目的：验证多个投影节点串联执行
     * 预期：每个投影依次处理数据
     */
    @Test
    public void testProject_Nested() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        List<JQuickProjectPhysicalNode.SelectItem> selectItems1 = Arrays.asList(
                new JQuickProjectPhysicalNode.SelectItem(new JQuickColumnRefExpression("id"), "id"),
                new JQuickProjectPhysicalNode.SelectItem(new JQuickColumnRefExpression("name"), "name"),
                new JQuickProjectPhysicalNode.SelectItem(new JQuickColumnRefExpression("salary"), "salary")
        );
        JQuickProjectPhysicalNode project1 = new JQuickProjectPhysicalNode(selectItems1, scanNode);
        // 第二层投影：选择 name, salary * 12
        List<JQuickProjectPhysicalNode.SelectItem> selectItems2 = Arrays.asList(
                new JQuickProjectPhysicalNode.SelectItem(new JQuickColumnRefExpression("name"), "name"),
                new JQuickProjectPhysicalNode.SelectItem(new JQuickBinaryExpression(new JQuickColumnRefExpression("salary"), new JQuickLiteralExpression(12), JQuickBinaryOperator.MULTIPLY), "annual_salary")
        );
        JQuickProjectPhysicalNode project2 = new JQuickProjectPhysicalNode(selectItems2, project1);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(project2, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该有 10 条数据", 10, result.size());
        JQuickRow firstRow = result.getRows().get(0);
        assertTrue("应该包含 name 列", firstRow.containsKey("name"));
        assertTrue("应该包含 annual_salary 列", firstRow.containsKey("annual_salary"));
        assertFalse("不应该包含 id 列", firstRow.containsKey("id"));
    }

    /**
     * 测试列顺序
     * 
     * 目的：验证投影列的顺序正确
     * 预期：列按指定顺序返回
     */
    @Test
    public void testProject_ColumnOrder() {
        List<JQuickProjectPhysicalNode.SelectItem> selectItems = Arrays.asList(
                new JQuickProjectPhysicalNode.SelectItem(new JQuickColumnRefExpression("department"), "department"),
                new JQuickProjectPhysicalNode.SelectItem(new JQuickColumnRefExpression("name"), "name"),
                new JQuickProjectPhysicalNode.SelectItem(new JQuickColumnRefExpression("id"), "id")
        );
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickProjectPhysicalNode projectNode = new JQuickProjectPhysicalNode(selectItems, scanNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(projectNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        // 验证列顺序
        List<String> columnNames = new ArrayList<>();
        for (JQuickColumnMeta col : result.getColumns()) {
            columnNames.add(col.getName());
        }
        assertEquals("第一列应该是 department", "department", columnNames.get(0));
        assertEquals("第二列应该是 name", "name", columnNames.get(1));
        assertEquals("第三列应该是 id", "id", columnNames.get(2));
    }
}
