package com.github.paohaijiao.distribute.nodeExecutor.executeNode.window;

import com.github.paohaijiao.datasource.JQuickDataSourceManager;
import com.github.paohaijiao.distributed.worker.*;
import com.github.paohaijiao.enums.JQuickBinaryOperator;
import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.expression.domain.JQuickBinaryExpression;
import com.github.paohaijiao.expression.domain.JQuickColumnRefExpression;
import com.github.paohaijiao.function.manager.JQuickMethodInvocationManager;
import com.github.paohaijiao.physical.node.JQuickFilterPhysicalNode;
import com.github.paohaijiao.physical.node.JQuickSortPhysicalNode;
import com.github.paohaijiao.physical.node.JQuickTableScanPhysicalNode;
import com.github.paohaijiao.physical.node.JQuickWindowPhysicalNode;
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
 * JQuickWindowPhysicalNode 单元测试
 * 
 * 测试范围：
 * 1. ROW_NUMBER
 * 2. RANK
 * 3. DENSE_RANK
 * 4. LEAD
 * 5. LAG
 * 6. 聚合函数（SUM, AVG, MAX, MIN, COUNT）
 * 7. 与其他节点（Filter、Sort）组合
 * 8. 边界条件
 */
public class JQuickWindowPhysicalNodeTest {

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
                new JQuickColumnMeta("emp_id", Long.class, "employee"),
                new JQuickColumnMeta("emp_name", String.class, "employee"),
                new JQuickColumnMeta("dept_id", Long.class, "employee"),
                new JQuickColumnMeta("salary", Double.class, "employee"),
                new JQuickColumnMeta("age", Integer.class, "employee")
        );
        List<JQuickRow> employeeRows = new ArrayList<>();
        employeeRows.add(createRow(employeeColumns, new Object[]{1L, "Alice", 1L, 8000.0, 25}));
        employeeRows.add(createRow(employeeColumns, new Object[]{2L, "Bob", 1L, 10000.0, 30}));
        employeeRows.add(createRow(employeeColumns, new Object[]{3L, "Charlie", 2L, 12000.0, 28}));
        employeeRows.add(createRow(employeeColumns, new Object[]{4L, "David", 2L, 11000.0, 35}));
        employeeRows.add(createRow(employeeColumns, new Object[]{5L, "Eve", 3L, 9000.0, 32}));
        employeeRows.add(createRow(employeeColumns, new Object[]{6L, "Frank", 3L, 7500.0, 27}));
        employeeRows.add(createRow(employeeColumns, new Object[]{7L, "Grace", 4L, 9500.0, 29}));
        employeeRows.add(createRow(employeeColumns, new Object[]{8L, "Henry", 4L, 10500.0, 33}));
        JQuickDataSet employeeData = new JQuickDataSet(employeeColumns, employeeRows);
        JQuickDataSourceManager.registerTable("employee", employeeData);
        List<JQuickColumnMeta> salesColumns = Arrays.asList(
                new JQuickColumnMeta("sale_id", Long.class, "sales"),
                new JQuickColumnMeta("emp_id", Long.class, "sales"),
                new JQuickColumnMeta("product", String.class, "sales"),
                new JQuickColumnMeta("amount", Double.class, "sales"),
                new JQuickColumnMeta("sale_date", String.class, "sales")
        );
        List<JQuickRow> salesRows = new ArrayList<>();
        salesRows.add(createRow(salesColumns, new Object[]{1L, 1L, "产品A", 1000.0, "2024-01-01"}));
        salesRows.add(createRow(salesColumns, new Object[]{2L, 1L, "产品B", 1500.0, "2024-01-02"}));
        salesRows.add(createRow(salesColumns, new Object[]{3L, 2L, "产品A", 2000.0, "2024-01-01"}));
        salesRows.add(createRow(salesColumns, new Object[]{4L, 2L, "产品C", 1800.0, "2024-01-03"}));
        salesRows.add(createRow(salesColumns, new Object[]{5L, 3L, "产品B", 2200.0, "2024-01-01"}));
        salesRows.add(createRow(salesColumns, new Object[]{6L, 3L, "产品A", 1600.0, "2024-01-02"}));
        salesRows.add(createRow(salesColumns, new Object[]{7L, 1L, "产品C", 900.0, "2024-01-04"}));
        salesRows.add(createRow(salesColumns, new Object[]{8L, 2L, "产品B", 2100.0, "2024-01-04"}));
        salesRows.add(createRow(salesColumns, new Object[]{9L, 3L, "产品A", 1900.0, "2024-01-03"}));
        salesRows.add(createRow(salesColumns, new Object[]{10L, 1L, "产品D", 1200.0, "2024-01-05"}));
        salesRows.add(createRow(salesColumns, new Object[]{11L, 2L, "产品A", 1700.0, "2024-01-05"}));
        salesRows.add(createRow(salesColumns, new Object[]{12L, 3L, "产品C", 1300.0, "2024-01-05"}));
        JQuickDataSet salesData = new JQuickDataSet(salesColumns, salesRows);
        JQuickDataSourceManager.registerTable("sales", salesData);
    }

    private JQuickRow createRow(List<JQuickColumnMeta> columns, Object[] values) {
        JQuickRow row = new JQuickRow();
        for (int i = 0; i < columns.size(); i++) {
            row.put(columns.get(i).getName(), values[i]);
        }
        return row;
    }

    /**
     * 测试 ROW_NUMBER 函数
     * 
     * 目的：验证 ROW_NUMBER 为每一行分配唯一的序号
     * 预期：每一行都有一个从 1 开始的唯一序号
     */
    @Test
    public void testWindowFunction_RowNumber() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        List<JQuickWindowPhysicalNode.WindowFunction> windowFunctions = new ArrayList<>();
        windowFunctions.add(new JQuickWindowPhysicalNode.WindowFunction("ROW_NUMBER", null, null, "row_num"));
        JQuickWindowPhysicalNode windowNode = new JQuickWindowPhysicalNode(windowFunctions, scanNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(windowNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 8 条数据", 8, result.size());
        for (int i = 0; i < result.size(); i++) {
            JQuickRow row = result.getRows().get(i);
            assertEquals("ROW_NUMBER 应该为 " + (i + 1), (long) (i + 1), row.get("row_num"));
        }
    }

    /**
     * 测试 RANK 函数
     * 
     * 目的：验证 RANK 为每一行分配排名（有间隙）
     * 预期：相同值有相同排名，后续排名会跳过
     */
    @Test
    public void testWindowFunction_Rank() {
        List<JQuickSortPhysicalNode.OrderByItem> orderByItems = new ArrayList<>();
        orderByItems.add(new JQuickSortPhysicalNode.OrderByItem("dept_id", true));
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        com.github.paohaijiao.physical.node.JQuickSortPhysicalNode sortNode = new com.github.paohaijiao.physical.node.JQuickSortPhysicalNode(orderByItems, scanNode);
        List<JQuickWindowPhysicalNode.WindowFunction> windowFunctions = new ArrayList<>();
        windowFunctions.add(new JQuickWindowPhysicalNode.WindowFunction(
            "RANK",
            null,
            null,
            "rank_val"
        ));
        JQuickWindowPhysicalNode windowNode = new JQuickWindowPhysicalNode(windowFunctions, sortNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(windowNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 8 条数据", 8, result.size());
        
        // 验证 RANK 值
        // dept_id = 1: Alice(1), Bob(2)
        // dept_id = 2: Charlie(3), David(4)
        // dept_id = 3: Eve(5), Frank(6)
        // dept_id = 4: Grace(7), Henry(8)
        for (int i = 0; i < result.size(); i++) {
            JQuickRow row = result.getRows().get(i);
            assertEquals("RANK 应该为 " + (i + 1), (long) (i + 1), row.get("rank_val"));
        }
    }

    /**
     * 测试 LEAD 函数
     * 
     * 目的：验证 LEAD 获取当前行之后的数据
     * 预期：每行显示下一个员工的工资
     */
    @Test
    public void testWindowFunction_Lead() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        List<JQuickWindowPhysicalNode.WindowFunction> windowFunctions = new ArrayList<>();
        windowFunctions.add(new JQuickWindowPhysicalNode.WindowFunction(
            "LEAD",
            new JQuickColumnRefExpression("salary"),
            null,
            "next_salary"
        ));
        JQuickWindowPhysicalNode windowNode = new JQuickWindowPhysicalNode(windowFunctions, scanNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(windowNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 8 条数据", 8, result.size());
        assertEquals("Alice 的下一行工资应该是 10000", 10000.0, result.getRows().get(0).get("next_salary"));
        assertEquals("Bob 的下一行工资应该是 12000", 12000.0, result.getRows().get(1).get("next_salary"));
        assertEquals("Charlie 的下一行工资应该是 11000", 11000.0, result.getRows().get(2).get("next_salary"));
        assertEquals("David 的下一行工资应该是 9000", 9000.0, result.getRows().get(3).get("next_salary"));
        assertEquals("Eve 的下一行工资应该是 7500", 7500.0, result.getRows().get(4).get("next_salary"));
        assertEquals("Frank 的下一行工资应该是 9500", 9500.0, result.getRows().get(5).get("next_salary"));
        assertEquals("Grace 的下一行工资应该是 10500", 10500.0, result.getRows().get(6).get("next_salary"));
        assertNull("Henry 的下一行工资应该是 null", result.getRows().get(7).get("next_salary"));
    }

    /**
     * 测试 LAG 函数
     * 
     * 目的：验证 LAG 获取当前行之前的数据
     * 预期：每行显示上一个员工的工资
     */
    @Test
    public void testWindowFunction_Lag() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        List<JQuickWindowPhysicalNode.WindowFunction> windowFunctions = new ArrayList<>();
        windowFunctions.add(new JQuickWindowPhysicalNode.WindowFunction(
            "LAG",
            new JQuickColumnRefExpression("salary"),
            null,
            "prev_salary"
        ));
        JQuickWindowPhysicalNode windowNode = new JQuickWindowPhysicalNode(windowFunctions, scanNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(windowNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 8 条数据", 8, result.size());
        assertNull("Alice 的上一行工资应该是 null", result.getRows().get(0).get("prev_salary"));
        assertEquals("Bob 的上一行工资应该是 8000", 8000.0, result.getRows().get(1).get("prev_salary"));
        assertEquals("Charlie 的上一行工资应该是 10000", 10000.0, result.getRows().get(2).get("prev_salary"));
        assertEquals("David 的上一行工资应该是 12000", 12000.0, result.getRows().get(3).get("prev_salary"));
        assertEquals("Eve 的上一行工资应该是 11000", 11000.0, result.getRows().get(4).get("prev_salary"));
        assertEquals("Frank 的上一行工资应该是 9000", 9000.0, result.getRows().get(5).get("prev_salary"));
        assertEquals("Grace 的上一行工资应该是 7500", 7500.0, result.getRows().get(6).get("prev_salary"));
        assertEquals("Henry 的上一行工资应该是 9500", 9500.0, result.getRows().get(7).get("prev_salary"));
    }

    /**
     * 测试 COUNT 聚合函数
     * 
     * 目的：验证 COUNT 统计行数
     * 预期：每行显示总行数
     */
    @Test
    public void testWindowFunction_Count() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        List<JQuickWindowPhysicalNode.WindowFunction> windowFunctions = new ArrayList<>();
        windowFunctions.add(new JQuickWindowPhysicalNode.WindowFunction(
            "COUNT",
            new JQuickColumnRefExpression("emp_id"),
            null,
            "total_count"
        ));
        JQuickWindowPhysicalNode windowNode = new JQuickWindowPhysicalNode(windowFunctions, scanNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(windowNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 8 条数据", 8, result.size());
        for (JQuickRow row : result.getRows()) {
            assertEquals("COUNT 应该为 8", 8L, row.get("total_count"));
        }
    }

    /**
     * 测试 SUM 聚合函数
     * 
     * 目的：验证 SUM 计算总和
     * 预期：每行显示所有工资的总和
     */
    @Test
    public void testWindowFunction_Sum() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        List<JQuickWindowPhysicalNode.WindowFunction> windowFunctions = new ArrayList<>();
        windowFunctions.add(new JQuickWindowPhysicalNode.WindowFunction(
            "SUM",
            new JQuickColumnRefExpression("salary"),
            null,
            "total_salary"
        ));
        JQuickWindowPhysicalNode windowNode = new JQuickWindowPhysicalNode(windowFunctions, scanNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(windowNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 8 条数据", 8, result.size());
        
        // 验证每行的 SUM 值都应该是总工资和
        // 8000+10000+12000+11000+9000+7500+9500+10500 = 77500
        for (JQuickRow row : result.getRows()) {
            assertEquals("SUM 应该为 77500.0", 77500.0, row.get("total_salary"));
        }
    }

    /**
     * 测试 AVG 聚合函数
     * 
     * 目的：验证 AVG 计算平均值
     * 预期：每行显示所有工资的平均值
     */
    @Test
    public void testWindowFunction_Avg() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        List<JQuickWindowPhysicalNode.WindowFunction> windowFunctions = new ArrayList<>();
        windowFunctions.add(new JQuickWindowPhysicalNode.WindowFunction(
            "AVG",
            new JQuickColumnRefExpression("salary"),
            null,
            "avg_salary"
        ));
        JQuickWindowPhysicalNode windowNode = new JQuickWindowPhysicalNode(windowFunctions, scanNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(windowNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 8 条数据", 8, result.size());
        // 验证每行的 AVG 值都应该是平均工资
        // 77500 / 8 = 9687.5
        for (JQuickRow row : result.getRows()) {
            assertEquals("AVG 应该为 9687.5", 9687.5, row.get("avg_salary"));
        }
    }

    /**
     * 测试 MAX 聚合函数
     * 
     * 目的：验证 MAX 获取最大值
     * 预期：每行显示所有工资的最大值
     */
    @Test
    public void testWindowFunction_Max() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        List<JQuickWindowPhysicalNode.WindowFunction> windowFunctions = new ArrayList<>();
        windowFunctions.add(new JQuickWindowPhysicalNode.WindowFunction(
            "MAX",
            new JQuickColumnRefExpression("salary"),
            null,
            "max_salary"
        ));
        JQuickWindowPhysicalNode windowNode = new JQuickWindowPhysicalNode(windowFunctions, scanNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(windowNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 8 条数据", 8, result.size());
        for (JQuickRow row : result.getRows()) {
            assertEquals("MAX 应该为 12000.0", 12000.0, row.get("max_salary"));
        }
    }

    /**
     * 测试 MIN 聚合函数
     * 
     * 目的：验证 MIN 获取最小值
     * 预期：每行显示所有工资的最小值
     */
    @Test
    public void testWindowFunction_Min() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        List<JQuickWindowPhysicalNode.WindowFunction> windowFunctions = new ArrayList<>();
        windowFunctions.add(new JQuickWindowPhysicalNode.WindowFunction(
            "MIN",
            new JQuickColumnRefExpression("salary"),
            null,
            "min_salary"
        ));
        JQuickWindowPhysicalNode windowNode = new JQuickWindowPhysicalNode(windowFunctions, scanNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(windowNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 8 条数据", 8, result.size());
        for (JQuickRow row : result.getRows()) {
            assertEquals("MIN 应该为 7500.0", 7500.0, row.get("min_salary"));
        }
    }

    /**
     * 测试多个窗口函数组合
     * 
     * 目的：验证同时使用多个窗口函数
     * 预期：每行同时包含 ROW_NUMBER、LEAD 和 LAG 的值
     */
    @Test
    public void testWindowFunction_MultipleFunctions() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        List<JQuickWindowPhysicalNode.WindowFunction> windowFunctions = new ArrayList<>();
        windowFunctions.add(new JQuickWindowPhysicalNode.WindowFunction(
            "ROW_NUMBER",
            null,
            null,
            "row_num"
        ));
        windowFunctions.add(new JQuickWindowPhysicalNode.WindowFunction(
            "LEAD",
            new JQuickColumnRefExpression("salary"),
            null,
            "next_salary"
        ));
        windowFunctions.add(new JQuickWindowPhysicalNode.WindowFunction(
            "LAG",
            new JQuickColumnRefExpression("salary"),
            null,
            "prev_salary"
        ));
        JQuickWindowPhysicalNode windowNode = new JQuickWindowPhysicalNode(windowFunctions, scanNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(windowNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 8 条数据", 8, result.size());
        JQuickRow firstRow = result.getRows().get(0);
        assertEquals("ROW_NUMBER 应该为 1", 1L, firstRow.get("row_num"));
        assertEquals("LEAD 值应该是 10000", 10000.0, firstRow.get("next_salary"));
        assertNull("LAG 值应该是 null", firstRow.get("prev_salary"));
        JQuickRow middleRow = result.getRows().get(3);
        assertEquals("ROW_NUMBER 应该为 4", 4L, middleRow.get("row_num"));
        assertEquals("LEAD 值应该是 9000", 9000.0, middleRow.get("next_salary"));
        assertEquals("LAG 值应该是 12000", 12000.0, middleRow.get("prev_salary"));
        JQuickRow lastRow = result.getRows().get(7);
        assertEquals("ROW_NUMBER 应该为 8", 8L, lastRow.get("row_num"));
        assertNull("LEAD 值应该是 null", lastRow.get("next_salary"));
        assertEquals("LAG 值应该是 9500", 9500.0, lastRow.get("prev_salary"));
    }

    /**
     * 测试窗口函数与 Filter 组合
     * 
     * 目的：验证窗口函数与 Filter 节点组合使用
     * 预期：先过滤 dept_id=1 的员工，然后为每行添加 ROW_NUMBER
     */
    @Test
    public void testWindowFunction_WithFilter() {
        JQuickExpression filterExpr = new JQuickBinaryExpression(new JQuickColumnRefExpression("dept_id"), new com.github.paohaijiao.expression.domain.JQuickLiteralExpression(1L), JQuickBinaryOperator.EQ);
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickFilterPhysicalNode filterNode = new JQuickFilterPhysicalNode(filterExpr, scanNode);
        List<JQuickWindowPhysicalNode.WindowFunction> windowFunctions = new ArrayList<>();
        windowFunctions.add(new JQuickWindowPhysicalNode.WindowFunction(
            "ROW_NUMBER",
            null,
            null,
            "row_num"
        ));
        JQuickWindowPhysicalNode windowNode = new JQuickWindowPhysicalNode(windowFunctions, filterNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(windowNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 2 条数据", 2, result.size());
        assertEquals("第一行 ROW_NUMBER 应该为 1", 1L, result.getRows().get(0).get("row_num"));
        assertEquals("第二行 ROW_NUMBER 应该为 2", 2L, result.getRows().get(1).get("row_num"));
    }

    /**
     * 测试窗口函数与 Sort 组合
     * 
     * 目的：验证窗口函数与 Sort 节点组合使用
     * 预期：先按工资降序排序，然后为每行添加 ROW_NUMBER
     */
    @Test
    public void testWindowFunction_WithSort() {
        List<JQuickSortPhysicalNode.OrderByItem> orderByItems = new ArrayList<>();
        orderByItems.add(new JQuickSortPhysicalNode.OrderByItem("salary", false));
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        com.github.paohaijiao.physical.node.JQuickSortPhysicalNode sortNode = new com.github.paohaijiao.physical.node.JQuickSortPhysicalNode(orderByItems, scanNode);
        List<JQuickWindowPhysicalNode.WindowFunction> windowFunctions = new ArrayList<>();
        windowFunctions.add(new JQuickWindowPhysicalNode.WindowFunction("ROW_NUMBER", null, null, "row_num"));
        JQuickWindowPhysicalNode windowNode = new JQuickWindowPhysicalNode(windowFunctions, sortNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(windowNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 8 条数据", 8, result.size());
        // Charlie: 12000, Henry: 10500, Bob: 10000, David: 11000, Grace: 9500, Eve: 9000, Alice: 8000, Frank: 7500
        assertEquals("第一行应该是 Charlie", "Charlie", result.getRows().get(0).get("emp_name"));
        assertEquals("ROW_NUMBER 应该为 1", 1L, result.getRows().get(0).get("row_num"));
        
        assertEquals("第二行应该是 Henry", "Henry", result.getRows().get(2).get("emp_name"));
        assertEquals("ROW_NUMBER 应该为 2", 3L, result.getRows().get(2).get("row_num"));
        
        assertEquals("第三行应该是 David", "David", result.getRows().get(1).get("emp_name"));
        assertEquals("ROW_NUMBER 应该为 3", 2L, result.getRows().get(1).get("row_num"));
    }

    /**
     * 测试窗口函数保持列元数据
     * 
     * 目的：验证窗口函数不改变原有列的元数据
     * 预期：返回的 DataSet 包含原始列和窗口函数结果列
     */
    @Test
    public void testWindowFunction_PreservesMetadata() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        List<JQuickWindowPhysicalNode.WindowFunction> windowFunctions = new ArrayList<>();
        windowFunctions.add(new JQuickWindowPhysicalNode.WindowFunction(
            "ROW_NUMBER",
            null,
            null,
            "row_num"
        ));
        JQuickWindowPhysicalNode windowNode = new JQuickWindowPhysicalNode(windowFunctions, scanNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(windowNode, context);
        result.printTable();
        assertNotNull("列元数据不应为 null", result.getColumns());
        assertEquals("应该有 6 列（5 个原始列 + 1 个窗口函数结果列）", 6, result.getColumns().size());
        List<JQuickColumnMeta> columns = result.getColumns();
        assertEquals("第一列应该是 emp_id", "emp_id", columns.get(0).getName());
        assertEquals("第二列应该是 emp_name", "emp_name", columns.get(1).getName());
        assertEquals("第三列应该是 dept_id", "dept_id", columns.get(2).getName());
        assertEquals("第四列应该是 salary", "salary", columns.get(3).getName());
        assertEquals("第五列应该是 age", "age", columns.get(4).getName());
        
        // 验证窗口函数结果列
        assertEquals("第六列应该是 row_num", "row_num", columns.get(5).getName());
    }

    /**
     * 测试窗口函数在空结果集上
     * 
     * 目的：验证对空结果集应用窗口函数
     * 预期：返回空结果集
     */
    @Test
    public void testWindowFunction_OnEmptyResultSet() {
        JQuickExpression filterExpr = new JQuickBinaryExpression(new JQuickColumnRefExpression("emp_id"), new com.github.paohaijiao.expression.domain.JQuickLiteralExpression(999L), com.github.paohaijiao.enums.JQuickBinaryOperator.EQ);
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickFilterPhysicalNode filterNode = new JQuickFilterPhysicalNode(filterExpr, scanNode);
        List<JQuickWindowPhysicalNode.WindowFunction> windowFunctions = new ArrayList<>();
        windowFunctions.add(new JQuickWindowPhysicalNode.WindowFunction(
            "ROW_NUMBER",
            null,
            null,
            "row_num"
        ));
        JQuickWindowPhysicalNode windowNode = new JQuickWindowPhysicalNode(windowFunctions, filterNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(windowNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 0 条数据", 0, result.size());
    }

    /**
     * 测试窗口函数在单行数据上
     * 
     * 目的：验证对单行数据应用窗口函数
     * 预期：返回一行数据，ROW_NUMBER 为 1
     */
    @Test
    public void testWindowFunction_SingleRow() {
        JQuickExpression filterExpr = new JQuickBinaryExpression(
            new JQuickColumnRefExpression("emp_id"),
            new com.github.paohaijiao.expression.domain.JQuickLiteralExpression(1L),
                com.github.paohaijiao.enums.JQuickBinaryOperator.EQ
        );
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickFilterPhysicalNode filterNode = new JQuickFilterPhysicalNode(filterExpr, scanNode);
        List<JQuickWindowPhysicalNode.WindowFunction> windowFunctions = new ArrayList<>();
        windowFunctions.add(new JQuickWindowPhysicalNode.WindowFunction(
            "ROW_NUMBER",
            null,
            null,
            "row_num"
        ));
        JQuickWindowPhysicalNode windowNode = new JQuickWindowPhysicalNode(windowFunctions, filterNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(windowNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 1 条数据", 1, result.size());
        assertEquals("ROW_NUMBER 应该为 1", 1L, result.getRows().get(0).get("row_num"));
    }

    /**
     * 测试 LEAD 和 LAG 函数获取不同列
     * 
     * 目的：验证 LEAD 和 LAG 可以获取不同列的值
     * 预期：每行显示下一个员工的 emp_name 和上一个员工的 age
     */
    @Test
    public void testWindowFunction_LeadLagDifferentColumns() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        List<JQuickWindowPhysicalNode.WindowFunction> windowFunctions = new ArrayList<>();
        windowFunctions.add(new JQuickWindowPhysicalNode.WindowFunction(
            "LEAD",
            new JQuickColumnRefExpression("emp_name"),
            null,
            "next_emp_name"
        ));
        windowFunctions.add(new JQuickWindowPhysicalNode.WindowFunction(
            "LAG",
            new JQuickColumnRefExpression("age"),
            null,
            "prev_age"
        ));
        JQuickWindowPhysicalNode windowNode = new JQuickWindowPhysicalNode(windowFunctions, scanNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(windowNode, context);
        
        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 8 条数据", 8, result.size());
        
        JQuickRow firstRow = result.getRows().get(0);
        assertEquals("LEAD emp_name 应该是 Bob", "Bob", firstRow.get("next_emp_name"));
        assertNull("LAG age 应该是 null", firstRow.get("prev_age"));
        
        JQuickRow middleRow = result.getRows().get(3);
        assertEquals("LEAD emp_name 应该是 Eve", "Eve", middleRow.get("next_emp_name"));
        assertEquals("LAG age 应该是 28", 28, middleRow.get("prev_age"));
        JQuickRow lastRow = result.getRows().get(7);
        assertNull("LEAD emp_name 应该是 null", lastRow.get("next_emp_name"));
        assertEquals("LAG age 应该是 29", 29, lastRow.get("prev_age"));
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
}
