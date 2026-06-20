package com.github.paohaijiao.distribute.nodeExecutor.executeNode.window;

import com.github.paohaijiao.datasource.JQuickDataSourceManager;
import com.github.paohaijiao.distributed.worker.*;
import com.github.paohaijiao.enums.JQuickBinaryOperator;
import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.expression.domain.JQuickBinaryExpression;
import com.github.paohaijiao.expression.domain.JQuickColumnRefExpression;
import com.github.paohaijiao.expression.domain.JQuickLiteralExpression;
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
 * JQuickWindowPhysicalNode WindowSpec 测试
 * 
 * 测试范围：
 * 1. 带分区的 ROW_NUMBER（部门内排名）
 * 2. 带分区和排序的 RANK/DENSE_RANK
 * 3. 带分区的聚合函数（SUM, AVG, COUNT, MAX, MIN）
 * 4. 带分区和排序的 LEAD/LAG
 * 5. 多列分区
 * 6. 与 Filter 组合
 */
public class JQuickWindowPhysicalNodeWindowSpecTest {

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
        // employee 表 - 8 条记录
        List<JQuickColumnMeta> employeeColumns = Arrays.asList(
                new JQuickColumnMeta("emp_id", Long.class, "employee"),
                new JQuickColumnMeta("emp_name", String.class, "employee"),
                new JQuickColumnMeta("dept_id", Long.class, "employee"),
                new JQuickColumnMeta("salary", Double.class, "employee"),
                new JQuickColumnMeta("age", Integer.class, "employee")
        );
        List<JQuickRow> employeeRows = new ArrayList<>();
        // 添加 8 条测试数据
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

        // sales 表 - 12 条记录
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
     * 测试带分区的 ROW_NUMBER
     * 
     * 场景：按部门分组，部门内按 emp_id 排序，为每行分配部门内序号
     * SQL: SELECT *, ROW_NUMBER() OVER (PARTITION BY dept_id ORDER BY emp_id) as dept_row_num FROM employee
     */
    @Test
    public void testRowNumberWithPartitionByDept() {
        List<JQuickSortPhysicalNode.OrderByItem> orderByItems = new ArrayList<>();
        orderByItems.add(new JQuickSortPhysicalNode.OrderByItem("dept_id", true));
        orderByItems.add(new JQuickSortPhysicalNode.OrderByItem("emp_id", true));
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        com.github.paohaijiao.physical.node.JQuickSortPhysicalNode sortNode = new com.github.paohaijiao.physical.node.JQuickSortPhysicalNode(orderByItems, scanNode);
        List<JQuickExpression> partitionKeys = new ArrayList<>();
        partitionKeys.add(new JQuickColumnRefExpression("dept_id"));
        JQuickWindowPhysicalNode.WindowSpec windowSpec = new JQuickWindowPhysicalNode.WindowSpec(partitionKeys, null, null);
        List<JQuickWindowPhysicalNode.WindowFunction> windowFunctions = new ArrayList<>();
        windowFunctions.add(new JQuickWindowPhysicalNode.WindowFunction(
            "ROW_NUMBER",
            null,
            windowSpec,
            "dept_row_num"
        ));
        JQuickWindowPhysicalNode windowNode = new JQuickWindowPhysicalNode(windowFunctions, sortNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(windowNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 8 条数据", 8, result.size());
        assertEquals("Alice 的 dept_id 应该为 1", 1L, result.getRows().get(0).get("dept_id"));
        assertEquals("Alice 的 dept_row_num 应该为 1", 1L, result.getRows().get(0).get("dept_row_num"));
        assertEquals("Bob 的 dept_id 应该为 1", 1L, result.getRows().get(1).get("dept_id"));
        assertEquals("Bob 的 dept_row_num 应该为 2", 2L, result.getRows().get(1).get("dept_row_num"));
        assertEquals("Charlie 的 dept_id 应该为 2", 2L, result.getRows().get(2).get("dept_id"));
        assertEquals("Charlie 的 dept_row_num 应该为 1", 1L, result.getRows().get(2).get("dept_row_num"));
        assertEquals("David 的 dept_id 应该为 2", 2L, result.getRows().get(3).get("dept_id"));
        assertEquals("David 的 dept_row_num 应该为 2", 2L, result.getRows().get(3).get("dept_row_num"));
        assertEquals("Eve 的 dept_id 应该为 3", 3L, result.getRows().get(4).get("dept_id"));
        assertEquals("Eve 的 dept_row_num 应该为 1", 1L, result.getRows().get(4).get("dept_row_num"));
        assertEquals("Frank 的 dept_id 应该为 3", 3L, result.getRows().get(5).get("dept_id"));
        assertEquals("Frank 的 dept_row_num 应该为 2", 2L, result.getRows().get(5).get("dept_row_num"));
        assertEquals("Grace 的 dept_id 应该为 4", 4L, result.getRows().get(6).get("dept_id"));
        assertEquals("Grace 的 dept_row_num 应该为 1", 1L, result.getRows().get(6).get("dept_row_num"));
        assertEquals("Henry 的 dept_id 应该为 4", 4L, result.getRows().get(7).get("dept_id"));
        assertEquals("Henry 的 dept_row_num 应该为 2", 2L, result.getRows().get(7).get("dept_row_num"));
    }

    /**
     * 测试带分区和排序的 RANK
     * 
     * 场景：按部门分组，部门内按工资降序排序，计算工资排名
     * SQL: SELECT *, RANK() OVER (PARTITION BY dept_id ORDER BY salary DESC) as salary_rank FROM employee
     */
    @Test
    public void testRankWithPartitionAndSortBySalary() {
        List<JQuickSortPhysicalNode.OrderByItem> orderByItems = new ArrayList<>();
        orderByItems.add(new JQuickSortPhysicalNode.OrderByItem("dept_id", true));
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        com.github.paohaijiao.physical.node.JQuickSortPhysicalNode sortNode = new com.github.paohaijiao.physical.node.JQuickSortPhysicalNode(orderByItems, scanNode);
        List<JQuickExpression> partitionKeys = new ArrayList<>();
        partitionKeys.add(new JQuickColumnRefExpression("dept_id"));
        List<JQuickSortPhysicalNode.OrderByItem> windowOrderByItems = new ArrayList<>();
        windowOrderByItems.add(new JQuickSortPhysicalNode.OrderByItem("salary", false)); // 降序
        JQuickWindowPhysicalNode.WindowSpec windowSpec = new JQuickWindowPhysicalNode.WindowSpec(partitionKeys, windowOrderByItems, null);
        List<JQuickWindowPhysicalNode.WindowFunction> windowFunctions = new ArrayList<>();
        windowFunctions.add(new JQuickWindowPhysicalNode.WindowFunction(
            "RANK",
            null,
            windowSpec,
            "salary_rank"
        ));
        JQuickWindowPhysicalNode windowNode = new JQuickWindowPhysicalNode(windowFunctions, sortNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(windowNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 8 条数据", 8, result.size());
        assertEquals("Bob 的工资应该是 10000", 10000.0, result.getRows().get(1).get("salary"));
        assertEquals("Bob 的工资排名应该为 1", 1L, result.getRows().get(1).get("salary_rank"));
        
        // Alice: salary=8000, rank=2
        assertEquals("Alice 的工资应该是 8000", 8000.0, result.getRows().get(0).get("salary"));
        assertEquals("Alice 的工资排名应该为 2", 2L, result.getRows().get(0).get("salary_rank"));
        
        // Charlie: salary=12000, rank=1
        assertEquals("Charlie 的工资应该是 12000", 12000.0, result.getRows().get(2).get("salary"));
        assertEquals("Charlie 的工资排名应该为 1", 1L, result.getRows().get(2).get("salary_rank"));
        
        // David: salary=11000, rank=2
        assertEquals("David 的工资应该是 11000", 11000.0, result.getRows().get(3).get("salary"));
        assertEquals("David 的工资排名应该为 2", 2L, result.getRows().get(3).get("salary_rank"));
        
        // Eve: salary=9000, rank=1
        assertEquals("Eve 的工资应该是 9000", 9000.0, result.getRows().get(4).get("salary"));
        assertEquals("Eve 的工资排名应该为 1", 1L, result.getRows().get(4).get("salary_rank"));
        
        // Frank: salary=7500, rank=2
        assertEquals("Frank 的工资应该是 7500", 7500.0, result.getRows().get(5).get("salary"));
        assertEquals("Frank 的工资排名应该为 2", 2L, result.getRows().get(5).get("salary_rank"));
        
        // Henry: salary=10500, rank=1
        assertEquals("Henry 的工资应该是 10500", 10500.0, result.getRows().get(7).get("salary"));
        assertEquals("Henry 的工资排名应该为 1", 1L, result.getRows().get(7).get("salary_rank"));
        
        // Grace: salary=9500, rank=2
        assertEquals("Grace 的工资应该是 9500", 9500.0, result.getRows().get(6).get("salary"));
        assertEquals("Grace 的工资排名应该为 2", 2L, result.getRows().get(6).get("salary_rank"));
    }

    /**
     * 测试带分区的 SUM 聚合
     * 
     * 场景：按部门分组，计算每个部门的工资总和
     * SQL: SELECT *, SUM(salary) OVER (PARTITION BY dept_id) as dept_total_salary FROM employee
     */
    @Test
    public void testSumWithPartitionByDept() {
        List<JQuickSortPhysicalNode.OrderByItem> orderByItems = new ArrayList<>();
        orderByItems.add(new JQuickSortPhysicalNode.OrderByItem("dept_id", true));
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        com.github.paohaijiao.physical.node.JQuickSortPhysicalNode sortNode = new com.github.paohaijiao.physical.node.JQuickSortPhysicalNode(orderByItems, scanNode);
        List<JQuickExpression> partitionKeys = new ArrayList<>();
        partitionKeys.add(new JQuickColumnRefExpression("dept_id"));
        JQuickWindowPhysicalNode.WindowSpec windowSpec = new JQuickWindowPhysicalNode.WindowSpec(partitionKeys, null, null);
        List<JQuickWindowPhysicalNode.WindowFunction> windowFunctions = new ArrayList<>();
        windowFunctions.add(new JQuickWindowPhysicalNode.WindowFunction(
            "SUM",
            new JQuickColumnRefExpression("salary"),
            windowSpec,
            "dept_total_salary"
        ));
        JQuickWindowPhysicalNode windowNode = new JQuickWindowPhysicalNode(windowFunctions, sortNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(windowNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 8 条数据", 8, result.size());
        assertEquals("Alice 所在部门的工资总和应该是 18000", 18000.0, result.getRows().get(0).get("dept_total_salary"));
        assertEquals("Bob 所在部门的工资总和应该是 18000", 18000.0, result.getRows().get(1).get("dept_total_salary"));
        assertEquals("Charlie 所在部门的工资总和应该是 23000", 23000.0, result.getRows().get(2).get("dept_total_salary"));
        assertEquals("David 所在部门的工资总和应该是 23000", 23000.0, result.getRows().get(3).get("dept_total_salary"));
        assertEquals("Eve 所在部门的工资总和应该是 16500", 16500.0, result.getRows().get(4).get("dept_total_salary"));
        assertEquals("Frank 所在部门的工资总和应该是 16500", 16500.0, result.getRows().get(5).get("dept_total_salary"));
        assertEquals("Grace 所在部门的工资总和应该是 20000", 20000.0, result.getRows().get(6).get("dept_total_salary"));
        assertEquals("Henry 所在部门的工资总和应该是 20000", 20000.0, result.getRows().get(7).get("dept_total_salary"));
    }

    /**
     * 测试带分区的 AVG 聚合
     * 
     * 场景：按部门分组，计算每个部门的平均工资
     * SQL: SELECT *, AVG(salary) OVER (PARTITION BY dept_id) as dept_avg_salary FROM employee
     */
    @Test
    public void testAvgWithPartitionByDept() {
        List<JQuickSortPhysicalNode.OrderByItem> orderByItems = new ArrayList<>();
        orderByItems.add(new JQuickSortPhysicalNode.OrderByItem("dept_id", true));
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        com.github.paohaijiao.physical.node.JQuickSortPhysicalNode sortNode = new com.github.paohaijiao.physical.node.JQuickSortPhysicalNode(orderByItems, scanNode);
        List<JQuickExpression> partitionKeys = new ArrayList<>();
        partitionKeys.add(new JQuickColumnRefExpression("dept_id"));
        JQuickWindowPhysicalNode.WindowSpec windowSpec = new JQuickWindowPhysicalNode.WindowSpec(partitionKeys, null, null);
        List<JQuickWindowPhysicalNode.WindowFunction> windowFunctions = new ArrayList<>();
        windowFunctions.add(new JQuickWindowPhysicalNode.WindowFunction(
            "AVG",
            new JQuickColumnRefExpression("salary"),
            windowSpec,
            "dept_avg_salary"
        ));
        JQuickWindowPhysicalNode windowNode = new JQuickWindowPhysicalNode(windowFunctions, sortNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(windowNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 8 条数据", 8, result.size());
        assertEquals("Alice 所在部门的平均工资应该是 9000", 9000.0, result.getRows().get(0).get("dept_avg_salary"));
        assertEquals("Bob 所在部门的平均工资应该是 9000", 9000.0, result.getRows().get(1).get("dept_avg_salary"));
        assertEquals("Charlie 所在部门的平均工资应该是 11500", 11500.0, result.getRows().get(2).get("dept_avg_salary"));
        assertEquals("David 所在部门的平均工资应该是 11500", 11500.0, result.getRows().get(3).get("dept_avg_salary"));
        assertEquals("Eve 所在部门的平均工资应该是 8250", 8250.0, result.getRows().get(4).get("dept_avg_salary"));
        assertEquals("Frank 所在部门的平均工资应该是 8250", 8250.0, result.getRows().get(5).get("dept_avg_salary"));
        assertEquals("Grace 所在部门的平均工资应该是 10000", 10000.0, result.getRows().get(6).get("dept_avg_salary"));
        assertEquals("Henry 所在部门的平均工资应该是 10000", 10000.0, result.getRows().get(7).get("dept_avg_salary"));
    }

    /**
     * 测试带分区的 COUNT 聚合
     * 
     * 场景：按部门分组，统计每个部门的员工数
     * SQL: SELECT *, COUNT(*) OVER (PARTITION BY dept_id) as dept_count FROM employee
     */
    @Test
    public void testCountWithPartitionByDept() {
        List<JQuickSortPhysicalNode.OrderByItem> orderByItems = new ArrayList<>();
        orderByItems.add(new JQuickSortPhysicalNode.OrderByItem("dept_id", true));
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        com.github.paohaijiao.physical.node.JQuickSortPhysicalNode sortNode = 
            new com.github.paohaijiao.physical.node.JQuickSortPhysicalNode(orderByItems, scanNode);
        List<JQuickExpression> partitionKeys = new ArrayList<>();
        partitionKeys.add(new JQuickColumnRefExpression("dept_id"));
        JQuickWindowPhysicalNode.WindowSpec windowSpec = new JQuickWindowPhysicalNode.WindowSpec(partitionKeys, null, null);
        List<JQuickWindowPhysicalNode.WindowFunction> windowFunctions = new ArrayList<>();
        windowFunctions.add(new JQuickWindowPhysicalNode.WindowFunction(
            "COUNT",
            null,
            windowSpec,
            "dept_count"
        ));
        JQuickWindowPhysicalNode windowNode = new JQuickWindowPhysicalNode(windowFunctions, sortNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(windowNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 8 条数据", 8, result.size());
        for (JQuickRow row : result.getRows()) {
            assertEquals("每个部门的员工数应该是 2", 2L, row.get("dept_count"));
        }
    }

    /**
     * 测试带分区的 MAX 和 MIN 聚合
     * 
     * 场景：按部门分组，计算每个部门的最高和最低工资
     * SQL: SELECT *, MAX(salary) OVER (PARTITION BY dept_id) as dept_max_salary, MIN(salary) OVER (PARTITION BY dept_id) as dept_min_salary FROM employee
     */
    @Test
    public void testMaxMinWithPartitionByDept() {
        List<JQuickSortPhysicalNode.OrderByItem> orderByItems = new ArrayList<>();
        orderByItems.add(new JQuickSortPhysicalNode.OrderByItem("dept_id", true));
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        com.github.paohaijiao.physical.node.JQuickSortPhysicalNode sortNode = new com.github.paohaijiao.physical.node.JQuickSortPhysicalNode(orderByItems, scanNode);
        List<JQuickExpression> partitionKeys = new ArrayList<>();
        partitionKeys.add(new JQuickColumnRefExpression("dept_id"));
        JQuickWindowPhysicalNode.WindowSpec windowSpec = new JQuickWindowPhysicalNode.WindowSpec(partitionKeys, null, null);
        List<JQuickWindowPhysicalNode.WindowFunction> windowFunctions = new ArrayList<>();
        windowFunctions.add(new JQuickWindowPhysicalNode.WindowFunction(
            "MAX",
            new JQuickColumnRefExpression("salary"),
            windowSpec,
            "dept_max_salary"
        ));
        windowFunctions.add(new JQuickWindowPhysicalNode.WindowFunction(
            "MIN",
            new JQuickColumnRefExpression("salary"),
            windowSpec,
            "dept_min_salary"
        ));
        // 创建窗口节点
        JQuickWindowPhysicalNode windowNode = new JQuickWindowPhysicalNode(windowFunctions, sortNode);
        // 执行查询
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(windowNode, context);
        result.printTable();
        // 验证结果
        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 8 条数据", 8, result.size());
        assertEquals("Alice 所在部门的最高工资应该是 10000", 10000.0, result.getRows().get(0).get("dept_max_salary"));
        assertEquals("Alice 所在部门的最低工资应该是 8000", 8000.0, result.getRows().get(0).get("dept_min_salary"));
        assertEquals("Bob 所在部门的最高工资应该是 10000", 10000.0, result.getRows().get(1).get("dept_max_salary"));
        assertEquals("Bob 所在部门的最低工资应该是 8000", 8000.0, result.getRows().get(1).get("dept_min_salary"));
        assertEquals("Charlie 所在部门的最高工资应该是 12000", 12000.0, result.getRows().get(2).get("dept_max_salary"));
        assertEquals("Charlie 所在部门的最低工资应该是 11000", 11000.0, result.getRows().get(2).get("dept_min_salary"));
        assertEquals("David 所在部门的最高工资应该是 12000", 12000.0, result.getRows().get(3).get("dept_max_salary"));
        assertEquals("David 所在部门的最低工资应该是 11000", 11000.0, result.getRows().get(3).get("dept_min_salary"));
        assertEquals("Eve 所在部门的最高工资应该是 9000", 9000.0, result.getRows().get(4).get("dept_max_salary"));
        assertEquals("Eve 所在部门的最低工资应该是 7500", 7500.0, result.getRows().get(4).get("dept_min_salary"));
        assertEquals("Frank 所在部门的最高工资应该是 9000", 9000.0, result.getRows().get(5).get("dept_max_salary"));
        assertEquals("Frank 所在部门的最低工资应该是 7500", 7500.0, result.getRows().get(5).get("dept_min_salary"));
        assertEquals("Grace 所在部门的最高工资应该是 10500", 10500.0, result.getRows().get(6).get("dept_max_salary"));
        assertEquals("Grace 所在部门的最低工资应该是 9500", 9500.0, result.getRows().get(6).get("dept_min_salary"));
        assertEquals("Henry 所在部门的最高工资应该是 10500", 10500.0, result.getRows().get(7).get("dept_max_salary"));
        assertEquals("Henry 所在部门的最低工资应该是 9500", 9500.0, result.getRows().get(7).get("dept_min_salary"));
    }

    /**
     * 测试带分区的 LEAD 和 LAG
     * 
     * 场景：按部门分组，部门内按工资降序排序，获取上一个和下一个员工的工资
     * SQL: SELECT *, LAG(salary) OVER (PARTITION BY dept_id ORDER BY salary DESC) as prev_salary, LEAD(salary) OVER (PARTITION BY dept_id ORDER BY salary DESC) as next_salary FROM employee
     */
    @Test
    public void testLeadLagWithPartitionByDept() {
        List<JQuickSortPhysicalNode.OrderByItem> orderByItems = new ArrayList<>();
        orderByItems.add(new JQuickSortPhysicalNode.OrderByItem("dept_id", true));
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        com.github.paohaijiao.physical.node.JQuickSortPhysicalNode sortNode = new com.github.paohaijiao.physical.node.JQuickSortPhysicalNode(orderByItems, scanNode);
        List<JQuickExpression> partitionKeys = new ArrayList<>();
        partitionKeys.add(new JQuickColumnRefExpression("dept_id"));
        
        List<JQuickSortPhysicalNode.OrderByItem> windowOrderByItems = new ArrayList<>();
        windowOrderByItems.add(new JQuickSortPhysicalNode.OrderByItem("salary", false)); // 降序
        
        JQuickWindowPhysicalNode.WindowSpec windowSpec = new JQuickWindowPhysicalNode.WindowSpec(partitionKeys, windowOrderByItems, null);
        List<JQuickWindowPhysicalNode.WindowFunction> windowFunctions = new ArrayList<>();
        windowFunctions.add(new JQuickWindowPhysicalNode.WindowFunction(
            "LEAD",
            new JQuickColumnRefExpression("salary"),
            windowSpec,
            "next_salary"
        ));
        windowFunctions.add(new JQuickWindowPhysicalNode.WindowFunction(
            "LAG",
            new JQuickColumnRefExpression("salary"),
            windowSpec,
            "prev_salary"
        ));
        
        // 创建窗口节点
        JQuickWindowPhysicalNode windowNode = new JQuickWindowPhysicalNode(windowFunctions, sortNode);
        
        // 执行查询
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(windowNode, context);
        result.printTable();
        // 验证结果
        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 8 条数据", 8, result.size());

        assertEquals("Bob 的工资应该是 10000", 10000.0, result.getRows().get(1).get("salary"));
        assertEquals("Bob 的下一个工资应该是 8000", 8000.0, result.getRows().get(1).get("next_salary"));
        assertNull("Bob 的上一个工资应该是 null", result.getRows().get(1).get("prev_salary"));
        
        // Alice: salary=8000, LEAD=null, LAG=10000
        assertEquals("Alice 的工资应该是 8000", 8000.0, result.getRows().get(0).get("salary"));
        assertNull("Alice 的下一个工资应该是 null", result.getRows().get(0).get("next_salary"));
        assertEquals("Alice 的上一个工资应该是 10000", 10000.0, result.getRows().get(0).get("prev_salary"));
        
        // Charlie: salary=12000, LEAD=11000, LAG=null
        assertEquals("Charlie 的工资应该是 12000", 12000.0, result.getRows().get(2).get("salary"));
        assertEquals("Charlie 的下一个工资应该是 11000", 11000.0, result.getRows().get(2).get("next_salary"));
        assertNull("Charlie 的上一个工资应该是 null", result.getRows().get(2).get("prev_salary"));
        
        // David: salary=11000, LEAD=null, LAG=12000
        assertEquals("David 的工资应该是 11000", 11000.0, result.getRows().get(3).get("salary"));
        assertNull("David 的下一个工资应该是 null", result.getRows().get(3).get("next_salary"));
        assertEquals("David 的上一个工资应该是 12000", 12000.0, result.getRows().get(3).get("prev_salary"));
        
        // Eve: salary=9000, LEAD=7500, LAG=null
        assertEquals("Eve 的工资应该是 9000", 9000.0, result.getRows().get(4).get("salary"));
        assertEquals("Eve 的下一个工资应该是 7500", 7500.0, result.getRows().get(4).get("next_salary"));
        assertNull("Eve 的上一个工资应该是 null", result.getRows().get(4).get("prev_salary"));
        
        // Frank: salary=7500, LEAD=null, LAG=9000
        assertEquals("Frank 的工资应该是 7500", 7500.0, result.getRows().get(5).get("salary"));
        assertNull("Frank 的下一个工资应该是 null", result.getRows().get(5).get("next_salary"));
        assertEquals("Frank 的上一个工资应该是 9000", 9000.0, result.getRows().get(5).get("prev_salary"));
        
        // Henry: salary=10500, LEAD=9500, LAG=null
        assertEquals("Henry 的工资应该是 10500", 10500.0, result.getRows().get(7).get("salary"));
        assertEquals("Henry 的下一个工资应该是 9500", 9500.0, result.getRows().get(7).get("next_salary"));
        assertNull("Henry 的上一个工资应该是 null", result.getRows().get(7).get("prev_salary"));
        
        // Grace: salary=9500, LEAD=null, LAG=10500
        assertEquals("Grace 的工资应该是 9500", 9500.0, result.getRows().get(6).get("salary"));
        assertNull("Grace 的下一个工资应该是 null", result.getRows().get(6).get("next_salary"));
        assertEquals("Grace 的上一个工资应该是 10500", 10500.0, result.getRows().get(6).get("prev_salary"));
    }

    /**
     * 测试带分区的多个窗口函数组合
     * 
     * 场景：按部门分组，计算部门内排名和部门工资占比
     * SQL: SELECT *, RANK() OVER (PARTITION BY dept_id ORDER BY salary DESC) as salary_rank, salary / SUM(salary) OVER (PARTITION BY dept_id) as salary_ratio FROM employee
     */
    @Test
    public void testMultipleWindowFunctionsWithPartition() {
        // 创建排序节点（按 dept_id 排序）
        List<JQuickSortPhysicalNode.OrderByItem> orderByItems = new ArrayList<>();
        orderByItems.add(new JQuickSortPhysicalNode.OrderByItem("dept_id", true));
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        com.github.paohaijiao.physical.node.JQuickSortPhysicalNode sortNode = 
            new com.github.paohaijiao.physical.node.JQuickSortPhysicalNode(orderByItems, scanNode);
        
        List<JQuickExpression> partitionKeys = new ArrayList<>();
        partitionKeys.add(new JQuickColumnRefExpression("dept_id"));
        
        List<JQuickSortPhysicalNode.OrderByItem> windowOrderByItems = new ArrayList<>();
        windowOrderByItems.add(new JQuickSortPhysicalNode.OrderByItem("salary", false)); // 降序
        
        JQuickWindowPhysicalNode.WindowSpec windowSpec = 
            new JQuickWindowPhysicalNode.WindowSpec(partitionKeys, windowOrderByItems, null);
        
        // 创建窗口规范（不带排序，用于 SUM）
        JQuickWindowPhysicalNode.WindowSpec sumWindowSpec = 
            new JQuickWindowPhysicalNode.WindowSpec(partitionKeys, null, null);
        
        // 创建窗口函数
        List<JQuickWindowPhysicalNode.WindowFunction> windowFunctions = new ArrayList<>();
        windowFunctions.add(new JQuickWindowPhysicalNode.WindowFunction(
            "RANK",
            null,
            windowSpec,
            "salary_rank"
        ));
        windowFunctions.add(new JQuickWindowPhysicalNode.WindowFunction(
            "SUM",
            new JQuickColumnRefExpression("salary"),
            sumWindowSpec,
            "dept_total_salary"
        ));
        windowFunctions.add(new JQuickWindowPhysicalNode.WindowFunction(
            "ROW_NUMBER",
            null,
            windowSpec,
            "dept_row_num"
        ));
        
        // 创建窗口节点
        JQuickWindowPhysicalNode windowNode = new JQuickWindowPhysicalNode(windowFunctions, sortNode);
        
        // 执行查询
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(windowNode, context);
        result.printTable();
        // 验证结果
        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 8 条数据", 8, result.size());

        assertEquals("Bob 的工资应该是 10000", 10000.0, result.getRows().get(1).get("salary"));
        assertEquals("Bob 的排名应该是 1", 1L, result.getRows().get(1).get("salary_rank"));
        assertEquals("Bob 的行号应该是 1", 1L, result.getRows().get(1).get("dept_row_num"));
        assertEquals("Bob 所在部门的工资总和应该是 18000", 18000.0, result.getRows().get(1).get("dept_total_salary"));
        
        // Alice
        assertEquals("Alice 的工资应该是 8000", 8000.0, result.getRows().get(0).get("salary"));
        assertEquals("Alice 的排名应该是 2", 2L, result.getRows().get(0).get("salary_rank"));
        assertEquals("Alice 的行号应该是 2", 2L, result.getRows().get(0).get("dept_row_num"));
        assertEquals("Alice 所在部门的工资总和应该是 18000", 18000.0, result.getRows().get(0).get("dept_total_salary"));
        
        // Charlie
        assertEquals("Charlie 的工资应该是 12000", 12000.0, result.getRows().get(2).get("salary"));
        assertEquals("Charlie 的排名应该是 1", 1L, result.getRows().get(2).get("salary_rank"));
        assertEquals("Charlie 的行号应该是 1", 1L, result.getRows().get(2).get("dept_row_num"));
        assertEquals("Charlie 所在部门的工资总和应该是 23000", 23000.0, result.getRows().get(2).get("dept_total_salary"));
        
        // David
        assertEquals("David 的工资应该是 11000", 11000.0, result.getRows().get(3).get("salary"));
        assertEquals("David 的排名应该是 2", 2L, result.getRows().get(3).get("salary_rank"));
        assertEquals("David 的行号应该是 2", 2L, result.getRows().get(3).get("dept_row_num"));
        assertEquals("David 所在部门的工资总和应该是 23000", 23000.0, result.getRows().get(3).get("dept_total_salary"));
    }

    /**
     * 测试带 Filter 和 WindowSpec 的组合
     * 
     * 场景：先过滤出工资大于 8000 的员工，然后按部门分组计算部门工资总和
     * SQL: SELECT * FROM (SELECT * FROM employee WHERE salary > 8000) t, SUM(salary) OVER (PARTITION BY dept_id) as dept_total_salary
     */
    @Test
    public void testFilterWithWindowFunctionPartition() {
        JQuickExpression filterExpr = new JQuickBinaryExpression(
            new JQuickColumnRefExpression("salary"),
            new JQuickLiteralExpression(8000.0), JQuickBinaryOperator.GT
        );
        List<JQuickSortPhysicalNode.OrderByItem> orderByItems = new ArrayList<>();
        orderByItems.add(new JQuickSortPhysicalNode.OrderByItem("dept_id", true));
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        com.github.paohaijiao.physical.node.JQuickSortPhysicalNode sortNode = new com.github.paohaijiao.physical.node.JQuickSortPhysicalNode(orderByItems, scanNode);
        JQuickFilterPhysicalNode filterNode = new JQuickFilterPhysicalNode(filterExpr, sortNode);
        List<JQuickExpression> partitionKeys = new ArrayList<>();
        partitionKeys.add(new JQuickColumnRefExpression("dept_id"));
        JQuickWindowPhysicalNode.WindowSpec windowSpec = new JQuickWindowPhysicalNode.WindowSpec(partitionKeys, null, null);
        List<JQuickWindowPhysicalNode.WindowFunction> windowFunctions = new ArrayList<>();
        windowFunctions.add(new JQuickWindowPhysicalNode.WindowFunction(
            "SUM",
            new JQuickColumnRefExpression("salary"),
            windowSpec,
            "dept_total_salary"
        ));
        JQuickWindowPhysicalNode windowNode = new JQuickWindowPhysicalNode(windowFunctions, filterNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(windowNode, context);
        result.printTable();

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
