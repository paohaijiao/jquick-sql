package com.github.paohaijiao.distribute.nodeExecutor.aggregate;

import com.github.paohaijiao.datasource.JQuickDataSourceManager;
import com.github.paohaijiao.distributed.worker.*;
import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.expression.domain.JQuickBinaryExpression;
import com.github.paohaijiao.expression.domain.JQuickColumnRefExpression;
import com.github.paohaijiao.expression.domain.JQuickLiteralExpression;
import com.github.paohaijiao.function.manager.JQuickMethodInvocationManager;
import com.github.paohaijiao.physical.node.JQuickFilterPhysicalNode;
import com.github.paohaijiao.physical.node.JQuickHashAggregatePhysicalNode;
import com.github.paohaijiao.physical.node.JQuickSortPhysicalNode;
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
 * JQuickHashAggregatePhysicalNode 测试
 * 
 * 测试范围：
 * 1. 基本 COUNT(*) 聚合
 * 2. SUM 聚合
 * 3. AVG 聚合
 * 4. MAX/MIN 聚合
 * 5. 多列分组
 * 6. HAVING 条件
 * 7. COUNT DISTINCT
 * 8. 多聚合函数组合
 * 9. 全局聚合（无分组键）
 * 10. NULL 值处理
 */
public class JQuickHashAggregatePhysicalNodeTest {

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
        // employee 表 - 员工信息表
        List<JQuickColumnMeta> employeeColumns = Arrays.asList(
                new JQuickColumnMeta("emp_id", Long.class, "employee"),
                new JQuickColumnMeta("emp_name", String.class, "employee"),
                new JQuickColumnMeta("dept_id", Long.class, "employee"),
                new JQuickColumnMeta("dept_name", String.class, "employee"),
                new JQuickColumnMeta("salary", Double.class, "employee"),
                new JQuickColumnMeta("age", Integer.class, "employee")
        );
        List<JQuickRow> employeeRows = new ArrayList<>();
        // 部门 1 - 技术部
        employeeRows.add(createRow(employeeColumns, new Object[]{1L, "Alice", 1L, "技术部", 8000.0, 25}));
        employeeRows.add(createRow(employeeColumns, new Object[]{2L, "Bob", 1L, "技术部", 10000.0, 30}));
        employeeRows.add(createRow(employeeColumns, new Object[]{3L, "Charlie", 1L, "技术部", 12000.0, 35}));
        // 部门 2 - 市场部
        employeeRows.add(createRow(employeeColumns, new Object[]{4L, "David", 2L, "市场部", 9000.0, 28}));
        employeeRows.add(createRow(employeeColumns, new Object[]{5L, "Eve", 2L, "市场部", 9500.0, 32}));
        // 部门 3 - 人事部
        employeeRows.add(createRow(employeeColumns, new Object[]{6L, "Frank", 3L, "人事部", 7000.0, 27}));
        employeeRows.add(createRow(employeeColumns, new Object[]{7L, "Grace", 3L, "人事部", 7500.0, 29}));
        
        JQuickDataSet employeeData = new JQuickDataSet(employeeColumns, employeeRows);
        JQuickDataSourceManager.registerTable("employee", employeeData);

        // product_sales 表 - 产品销售表
        List<JQuickColumnMeta> salesColumns = Arrays.asList(
                new JQuickColumnMeta("product_id", Long.class, "product_sales"),
                new JQuickColumnMeta("product_name", String.class, "product_sales"),
                new JQuickColumnMeta("category", String.class, "product_sales"),
                new JQuickColumnMeta("price", Double.class, "product_sales"),
                new JQuickColumnMeta("quantity", Integer.class, "product_sales"),
                new JQuickColumnMeta("region", String.class, "product_sales")
        );
        List<JQuickRow> salesRows = new ArrayList<>();
        salesRows.add(createRow(salesColumns, new Object[]{1L, "产品A", "电子产品", 100.0, 10, "北京"}));
        salesRows.add(createRow(salesColumns, new Object[]{2L, "产品B", "电子产品", 200.0, 5, "北京"}));
        salesRows.add(createRow(salesColumns, new Object[]{3L, "产品C", "生活用品", 50.0, 20, "上海"}));
        salesRows.add(createRow(salesColumns, new Object[]{4L, "产品D", "生活用品", 80.0, 15, "上海"}));
        salesRows.add(createRow(salesColumns, new Object[]{5L, "产品E", "电子产品", 150.0, 8, "广州"}));
        salesRows.add(createRow(salesColumns, new Object[]{6L, "产品F", "生活用品", 60.0, 12, "广州"}));
        
        JQuickDataSet salesData = new JQuickDataSet(salesColumns, salesRows);
        JQuickDataSourceManager.registerTable("product_sales", salesData);

        // order_items 表 - 订单明细表
        List<JQuickColumnMeta> orderColumns = Arrays.asList(
                new JQuickColumnMeta("order_id", Long.class, "order_items"),
                new JQuickColumnMeta("item_name", String.class, "order_items"),
                new JQuickColumnMeta("price", Double.class, "order_items"),
                new JQuickColumnMeta("quantity", Integer.class, "order_items"),
                new JQuickColumnMeta("status", String.class, "order_items")
        );
        List<JQuickRow> orderRows = new ArrayList<>();
        orderRows.add(createRow(orderColumns, new Object[]{1L, "商品X", 100.0, 2, "completed"}));
        orderRows.add(createRow(orderColumns, new Object[]{1L, "商品Y", 50.0, 1, "completed"}));
        orderRows.add(createRow(orderColumns, new Object[]{2L, "商品X", 100.0, 3, "completed"}));
        orderRows.add(createRow(orderColumns, new Object[]{2L, "商品Z", 80.0, 2, "completed"}));
        orderRows.add(createRow(orderColumns, new Object[]{3L, "商品Y", 50.0, 5, "pending"}));
        orderRows.add(createRow(orderColumns, new Object[]{3L, "商品Z", 80.0, 1, "pending"}));
        orderRows.add(createRow(orderColumns, new Object[]{4L, "商品X", 100.0, 1, "cancelled"}));
        
        JQuickDataSet orderData = new JQuickDataSet(orderColumns, orderRows);
        JQuickDataSourceManager.registerTable("order_items", orderData);
    }

    private JQuickRow createRow(List<JQuickColumnMeta> columns, Object[] values) {
        JQuickRow row = new JQuickRow();
        for (int i = 0; i < columns.size(); i++) {
            row.put(columns.get(i).getName(), values[i]);
        }
        return row;
    }

    /**
     * 测试 COUNT(*) 聚合
     * 
     * 场景：按部门统计员工数量
     * SQL: SELECT dept_id, COUNT(*) FROM employee GROUP BY dept_id
     * 预期：返回 3 个部门，每个部门的员工数量
     */
    @Test
    public void testCountStar() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        List<JQuickExpression> groupKeys = Arrays.asList(
            new JQuickColumnRefExpression("dept_id")
        );
        List<JQuickHashAggregatePhysicalNode.AggregateFunction> aggregates = Arrays.asList(
            new JQuickHashAggregatePhysicalNode.AggregateFunction("count", null, false, "emp_count")
        );
        JQuickHashAggregatePhysicalNode aggregateNode = new JQuickHashAggregatePhysicalNode(
            groupKeys, aggregates, scanNode, null, JQuickHashAggregatePhysicalNode.AggregateStage.SINGLE
        );
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(aggregateNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 3 个分组", 3, result.size());
        Map<Long, Long> deptCounts = new HashMap<>();
        for (JQuickRow row : result.getRows()) {
            Long deptId = (Long) row.get("dept_id");
            Long count = (Long) row.get("emp_count");
            deptCounts.put(deptId, count);
        }
        
        assertEquals("部门 1 应该有 3 个员工", Long.valueOf(3), deptCounts.get(1L));
        assertEquals("部门 2 应该有 2 个员工", Long.valueOf(2), deptCounts.get(2L));
        assertEquals("部门 3 应该有 2 个员工", Long.valueOf(2), deptCounts.get(3L));
    }

    /**
     * 测试 SUM 聚合
     * 
     * 场景：按部门统计工资总额
     * SQL: SELECT dept_id, SUM(salary) FROM employee GROUP BY dept_id
     * 预期：返回 3 个部门的工资总额
     */
    @Test
    public void testSum() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        List<JQuickExpression> groupKeys = Arrays.asList(new JQuickColumnRefExpression("dept_id"));
        List<JQuickHashAggregatePhysicalNode.AggregateFunction> aggregates = Arrays.asList(new JQuickHashAggregatePhysicalNode.AggregateFunction("sum", new JQuickColumnRefExpression("salary"), false, "total_salary"));
        JQuickHashAggregatePhysicalNode aggregateNode = new JQuickHashAggregatePhysicalNode(
            groupKeys, aggregates, scanNode, null, JQuickHashAggregatePhysicalNode.AggregateStage.SINGLE
        );
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(aggregateNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 3 个分组", 3, result.size());
        Map<Long, Double> deptSalaries = new HashMap<>();
        for (JQuickRow row : result.getRows()) {
            Long deptId = (Long) row.get("dept_id");
            Double totalSalary = (Double) row.get("total_salary");
            deptSalaries.put(deptId, totalSalary);
        }
        
        // 技术部: 8000 + 10000 + 12000 = 30000
        assertEquals("部门 1 工资总额应为 30000.0", 30000.0, deptSalaries.get(1L), 0.01);
        // 市场部: 9000 + 9500 = 18500
        assertEquals("部门 2 工资总额应为 18500.0", 18500.0, deptSalaries.get(2L), 0.01);
        // 人事部: 7000 + 7500 = 14500
        assertEquals("部门 3 工资总额应为 14500.0", 14500.0, deptSalaries.get(3L), 0.01);
    }

    /**
     * 测试 AVG 聚合
     * 
     * 场景：按部门统计平均工资
     * SQL: SELECT dept_id, AVG(salary) FROM employee GROUP BY dept_id
     * 预期：返回 3 个部门的平均工资
     */
    @Test
    public void testAvg() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        List<JQuickExpression> groupKeys = Arrays.asList(
            new JQuickColumnRefExpression("dept_id")
        );
        List<JQuickHashAggregatePhysicalNode.AggregateFunction> aggregates = Arrays.asList(
            new JQuickHashAggregatePhysicalNode.AggregateFunction("avg", new JQuickColumnRefExpression("salary"), false, "avg_salary")
        );
        JQuickHashAggregatePhysicalNode aggregateNode = new JQuickHashAggregatePhysicalNode(
            groupKeys, aggregates, scanNode, null, JQuickHashAggregatePhysicalNode.AggregateStage.SINGLE
        );
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(aggregateNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 3 个分组", 3, result.size());
        Map<Long, Double> deptAvgSalaries = new HashMap<>();
        for (JQuickRow row : result.getRows()) {
            Long deptId = (Long) row.get("dept_id");
            Double avgSalary = (Double) row.get("avg_salary");
            deptAvgSalaries.put(deptId, avgSalary);
        }
        // 技术部: (8000 + 10000 + 12000) / 3 = 10000
        assertEquals("部门 1 平均工资应为 10000.0", 10000.0, deptAvgSalaries.get(1L), 0.01);
        // 市场部: (9000 + 9500) / 2 = 9250
        assertEquals("部门 2 平均工资应为 9250.0", 9250.0, deptAvgSalaries.get(2L), 0.01);
        // 人事部: (7000 + 7500) / 2 = 7250
        assertEquals("部门 3 平均工资应为 7250.0", 7250.0, deptAvgSalaries.get(3L), 0.01);
    }

    /**
     * 测试 MAX/MIN 聚合
     * 
     * 场景：按部门统计最高和最低工资
     * SQL: SELECT dept_id, MAX(salary), MIN(salary) FROM employee GROUP BY dept_id
     * 预期：返回 3 个部门的最高和最低工资
     */
    @Test
    public void testMaxMin() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        List<JQuickExpression> groupKeys = Arrays.asList(
            new JQuickColumnRefExpression("dept_id")
        );
        List<JQuickHashAggregatePhysicalNode.AggregateFunction> aggregates = Arrays.asList(
            new JQuickHashAggregatePhysicalNode.AggregateFunction("max", new JQuickColumnRefExpression("salary"), false, "max_salary"),
            new JQuickHashAggregatePhysicalNode.AggregateFunction("min", new JQuickColumnRefExpression("salary"), false, "min_salary")
        );
        
        JQuickHashAggregatePhysicalNode aggregateNode = new JQuickHashAggregatePhysicalNode(
            groupKeys, aggregates, scanNode, null, JQuickHashAggregatePhysicalNode.AggregateStage.SINGLE
        );
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(aggregateNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 3 个分组", 3, result.size());
        Map<Long, Double> deptMaxSalaries = new HashMap<>();
        Map<Long, Double> deptMinSalaries = new HashMap<>();
        for (JQuickRow row : result.getRows()) {
            Long deptId = (Long) row.get("dept_id");
            deptMaxSalaries.put(deptId, (Double) row.get("max_salary"));
            deptMinSalaries.put(deptId, (Double) row.get("min_salary"));
        }
        
        // 技术部: max=12000, min=8000
        assertEquals("部门 1 最高工资应为 12000.0", 12000.0, deptMaxSalaries.get(1L), 0.01);
        assertEquals("部门 1 最低工资应为 8000.0", 8000.0, deptMinSalaries.get(1L), 0.01);
        // 市场部: max=9500, min=9000
        assertEquals("部门 2 最高工资应为 9500.0", 9500.0, deptMaxSalaries.get(2L), 0.01);
        assertEquals("部门 2 最低工资应为 9000.0", 9000.0, deptMinSalaries.get(2L), 0.01);
        // 人事部: max=7500, min=7000
        assertEquals("部门 3 最高工资应为 7500.0", 7500.0, deptMaxSalaries.get(3L), 0.01);
        assertEquals("部门 3 最低工资应为 7000.0", 7000.0, deptMinSalaries.get(3L), 0.01);
    }

    /**
     * 测试多列分组
     * 
     * 场景：按商品类别和地区统计销售总额
     * SQL: SELECT category, region, SUM(price * quantity) FROM product_sales GROUP BY category, region
     * 预期：返回不同类别和地区的销售总额
     */
    @Test
    public void testMultipleGroupKeys() {
        // 创建表扫描节点
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("product_sales", null, null, null, null);
        // 创建聚合节点
        List<JQuickExpression> groupKeys = Arrays.asList(
            new JQuickColumnRefExpression("category"),
            new JQuickColumnRefExpression("region")
        );
        List<JQuickHashAggregatePhysicalNode.AggregateFunction> aggregates = Arrays.asList(
            new JQuickHashAggregatePhysicalNode.AggregateFunction("sum", new JQuickColumnRefExpression("price"), false, "total_price")
        );
        JQuickHashAggregatePhysicalNode aggregateNode = new JQuickHashAggregatePhysicalNode(
            groupKeys, aggregates, scanNode, null, JQuickHashAggregatePhysicalNode.AggregateStage.SINGLE
        );
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(aggregateNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        // 电子产品: 北京(100+200), 广州(150) = 450
        // 生活用品: 北京(50+80), 上海(50+80), 广州(60) = 240
        assertTrue("应该有至少 2 个分组", result.size() >= 2);
    }

    /**
     * 测试 HAVING 条件
     * 
     * 场景：按部门统计，只返回员工数量大于 2 的部门
     * SQL: SELECT dept_id, COUNT(*) FROM employee GROUP BY dept_id HAVING COUNT(*) > 2
     * 预期：只返回部门 1（3 个员工）
     */
    @Test
    public void testHavingCondition() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        List<JQuickExpression> groupKeys = Arrays.asList(
            new JQuickColumnRefExpression("dept_id")
        );
        List<JQuickHashAggregatePhysicalNode.AggregateFunction> aggregates = Arrays.asList(
            new JQuickHashAggregatePhysicalNode.AggregateFunction("count", null, false, "emp_count")
        );
        JQuickExpression havingCondition = new JQuickBinaryExpression(new JQuickColumnRefExpression("emp_count"), new JQuickLiteralExpression(2L),com.github.paohaijiao.enums.JQuickBinaryOperator.GT);
        JQuickHashAggregatePhysicalNode aggregateNode = new JQuickHashAggregatePhysicalNode(
            groupKeys, aggregates, scanNode, havingCondition, JQuickHashAggregatePhysicalNode.AggregateStage.SINGLE
        );
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(aggregateNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该只返回 1 个分组（部门 1）", 1, result.size());
        JQuickRow row = result.getRows().get(0);
        assertEquals("部门 ID 应该是 1", Long.valueOf(1), row.get("dept_id"));
        assertEquals("员工数量应该是 3", Long.valueOf(3), row.get("emp_count"));
    }

    /**
     * 测试 COUNT DISTINCT
     * 
     * 场景：统计每个部门的员工数量（去重）
     * SQL: SELECT dept_id, COUNT(DISTINCT emp_id) FROM employee GROUP BY dept_id
     * 预期：每个部门的去重员工数量
     */
    @Test
    public void testCountDistinct() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        List<JQuickExpression> groupKeys = Arrays.asList(
            new JQuickColumnRefExpression("dept_id")
        );
        List<JQuickHashAggregatePhysicalNode.AggregateFunction> aggregates = Arrays.asList(
            new JQuickHashAggregatePhysicalNode.AggregateFunction("count", new JQuickColumnRefExpression("emp_id"), true, "distinct_emp_count")
        );
        JQuickHashAggregatePhysicalNode aggregateNode = new JQuickHashAggregatePhysicalNode(
            groupKeys, aggregates, scanNode, null, JQuickHashAggregatePhysicalNode.AggregateStage.SINGLE
        );
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(aggregateNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 3 个分组", 3, result.size());
        
        // 验证结果数据
        Map<Long, Long> deptDistinctCounts = new HashMap<>();
        for (JQuickRow row : result.getRows()) {
            Long deptId = (Long) row.get("dept_id");
            Long count = (Long) row.get("distinct_emp_count");
            deptDistinctCounts.put(deptId, count);
        }
        
        assertEquals("部门 1 去重员工数应为 3", Long.valueOf(3), deptDistinctCounts.get(1L));
        assertEquals("部门 2 去重员工数应为 2", Long.valueOf(2), deptDistinctCounts.get(2L));
        assertEquals("部门 3 去重员工数应为 2", Long.valueOf(2), deptDistinctCounts.get(3L));
    }

    /**
     * 测试多聚合函数组合
     * 
     * 场景：按部门统计员工数量、平均工资、最高工资、最低工资
     * SQL: SELECT dept_id, COUNT(*), AVG(salary), MAX(salary), MIN(salary) 
     *      FROM employee GROUP BY dept_id
     * 预期：每个部门的完整统计信息
     */
    @Test
    public void testMultipleAggregates() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        List<JQuickExpression> groupKeys = Arrays.asList(
            new JQuickColumnRefExpression("dept_id")
        );
        List<JQuickHashAggregatePhysicalNode.AggregateFunction> aggregates = Arrays.asList(
            new JQuickHashAggregatePhysicalNode.AggregateFunction("count", null, false, "emp_count"),
            new JQuickHashAggregatePhysicalNode.AggregateFunction("avg", new JQuickColumnRefExpression("salary"), false, "avg_salary"),
            new JQuickHashAggregatePhysicalNode.AggregateFunction("max", new JQuickColumnRefExpression("salary"), false, "max_salary"),
            new JQuickHashAggregatePhysicalNode.AggregateFunction("min", new JQuickColumnRefExpression("salary"), false, "min_salary")
        );
        
        JQuickHashAggregatePhysicalNode aggregateNode = new JQuickHashAggregatePhysicalNode(
            groupKeys, aggregates, scanNode, null, JQuickHashAggregatePhysicalNode.AggregateStage.SINGLE
        );
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(aggregateNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 3 个分组", 3, result.size());
        assertEquals("应该输出 5 列（1 个分组键 + 4 个聚合）", 5, result.getColumns().size());
        Map<Long, JQuickRow> deptStats = new HashMap<>();
        for (JQuickRow row : result.getRows()) {
            Long deptId = (Long) row.get("dept_id");
            deptStats.put(deptId, row);
        }
        JQuickRow dept1 = deptStats.get(1L);
        assertEquals("部门 1 员工数应为 3", Long.valueOf(3), dept1.get("emp_count"));
        assertEquals("部门 1 平均工资应为 10000.0", 10000.0, (Double) dept1.get("avg_salary"), 0.01);
        assertEquals("部门 1 最高工资应为 12000.0", 12000.0, dept1.get("max_salary"));
        assertEquals("部门 1 最低工资应为 8000.0", 8000.0, dept1.get("min_salary"));
    }

    /**
     * 测试全局聚合（无分组键）
     * 
     * 场景：统计所有员工的平均工资
     * SQL: SELECT AVG(salary) FROM employee
     * 预期：返回全局平均工资
     */
    @Test
    public void testGlobalAggregate() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        List<JQuickExpression> groupKeys = new ArrayList<>();
        List<JQuickHashAggregatePhysicalNode.AggregateFunction> aggregates = Arrays.asList(
            new JQuickHashAggregatePhysicalNode.AggregateFunction("avg", new JQuickColumnRefExpression("salary"), false, "global_avg_salary")
        );
        
        JQuickHashAggregatePhysicalNode aggregateNode = new JQuickHashAggregatePhysicalNode(
            groupKeys, aggregates, scanNode, null, JQuickHashAggregatePhysicalNode.AggregateStage.SINGLE
        );
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(aggregateNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 1 行（全局聚合）", 1, result.size());
        JQuickRow row = result.getRows().get(0);
        // (8000 + 10000 + 12000 + 9000 + 9500 + 7000 + 7500) / 7 = 63000 / 7 = 9000
        assertEquals("全局平均工资应为 9000.0", 9000.0, (Double) row.get("global_avg_salary"), 0.01);
    }

    /**
     * 测试 COUNT(*) 全局聚合
     * 
     * 场景：统计所有员工数量
     * SQL: SELECT COUNT(*) FROM employee
     * 预期：返回 7
     */
    @Test
    public void testCountStarGlobal() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        List<JQuickExpression> groupKeys = new ArrayList<>();
        List<JQuickHashAggregatePhysicalNode.AggregateFunction> aggregates = Arrays.asList(
            new JQuickHashAggregatePhysicalNode.AggregateFunction("count", null, false, "total_count")
        );
        JQuickHashAggregatePhysicalNode aggregateNode = new JQuickHashAggregatePhysicalNode(
            groupKeys, aggregates, scanNode, null, JQuickHashAggregatePhysicalNode.AggregateStage.SINGLE
        );
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(aggregateNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 1 行（全局聚合）", 1, result.size());
        JQuickRow row = result.getRows().get(0);
        assertEquals("总员工数应为 7", Long.valueOf(7), row.get("total_count"));
    }

    /**
     * 测试克隆功能
     * 
     * 场景：克隆聚合节点
     * 预期：克隆后的节点与原节点配置一致
     */
    @Test
    public void testClone() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        List<JQuickExpression> groupKeys = Arrays.asList(
            new JQuickColumnRefExpression("dept_id")
        );
        List<JQuickHashAggregatePhysicalNode.AggregateFunction> aggregates = Arrays.asList(
            new JQuickHashAggregatePhysicalNode.AggregateFunction("count", null, false, "emp_count")
        );
        
        JQuickHashAggregatePhysicalNode aggregateNode = new JQuickHashAggregatePhysicalNode(
            groupKeys, aggregates, scanNode, null, JQuickHashAggregatePhysicalNode.AggregateStage.SINGLE
        );
        JQuickHashAggregatePhysicalNode clonedNode = (JQuickHashAggregatePhysicalNode) aggregateNode.clone();
        assertNotNull("克隆节点不应为 null", clonedNode);
        assertEquals("分组键数量应该相同", 1, clonedNode.getGroupKeys().size());
        assertEquals("聚合函数数量应该相同", 1, clonedNode.getAggregates().size());
        assertEquals("阶段应该相同", JQuickHashAggregatePhysicalNode.AggregateStage.SINGLE, clonedNode.getStage());
    }

    /**
     * 测试节点属性
     * 
     * 场景：验证节点的各种属性
     * 预期：属性值正确
     */
    @Test
    public void testNodeProperties() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        List<JQuickExpression> groupKeys = Arrays.asList(
            new JQuickColumnRefExpression("dept_id")
        );
        List<JQuickHashAggregatePhysicalNode.AggregateFunction> aggregates = Arrays.asList(
            new JQuickHashAggregatePhysicalNode.AggregateFunction("count", null, false, "emp_count"),
            new JQuickHashAggregatePhysicalNode.AggregateFunction("sum", new JQuickColumnRefExpression("salary"), false, "total_salary")
        );
        
        JQuickHashAggregatePhysicalNode aggregateNode = new JQuickHashAggregatePhysicalNode(
            groupKeys, aggregates, scanNode, null, JQuickHashAggregatePhysicalNode.AggregateStage.PARTIAL
        );
        assertEquals("节点类型应该是 HashAggregate", "HashAggregate", aggregateNode.getNodeType());
        assertEquals("分组键数量应该是 1", 1, aggregateNode.getGroupKeys().size());
        assertEquals("聚合函数数量应该是 2", 2, aggregateNode.getAggregates().size());
        assertEquals("阶段应该是 PARTIAL", JQuickHashAggregatePhysicalNode.AggregateStage.PARTIAL, aggregateNode.getStage());
        assertNull("HAVING 条件应该为 null", aggregateNode.getHavingCondition());
        assertEquals("输出 Schema 列数应该是 3（2 个分组键 + 1 个聚合）", 3, aggregateNode.getOutputSchema().size());
        assertNotNull("子节点不应为 null", aggregateNode.getChild());
    }

    /**
     * 测试按订单统计
     * 
     * 场景：按订单 ID 统计商品数量和总金额
     * SQL: SELECT order_id, SUM(price * quantity), COUNT(*) FROM order_items GROUP BY order_id
     * 预期：每个订单的总金额和商品数量
     */
    @Test
    public void testOrderAggregation() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("order_items", null, null, null, null);
        List<JQuickExpression> groupKeys = Arrays.asList(
            new JQuickColumnRefExpression("order_id")
        );
        List<JQuickHashAggregatePhysicalNode.AggregateFunction> aggregates = Arrays.asList(
            new JQuickHashAggregatePhysicalNode.AggregateFunction("count", null, false, "item_count")
        );
        
        JQuickHashAggregatePhysicalNode aggregateNode = new JQuickHashAggregatePhysicalNode(
            groupKeys, aggregates, scanNode, null, JQuickHashAggregatePhysicalNode.AggregateStage.SINGLE
        );
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(aggregateNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
    }

    /**
     * 测试按状态统计订单
     * 
     * 场景：按订单状态统计数量
     * SQL: SELECT status, COUNT(*) FROM order_items GROUP BY status
     * 预期：每种状态的订单数量
     */
    @Test
    public void testStatusAggregation() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("order_items", null, null, null, null);
        List<JQuickExpression> groupKeys = Arrays.asList(
            new JQuickColumnRefExpression("status")
        );
        List<JQuickHashAggregatePhysicalNode.AggregateFunction> aggregates = Arrays.asList(
            new JQuickHashAggregatePhysicalNode.AggregateFunction("count", null, false, "count")
        );
        
        JQuickHashAggregatePhysicalNode aggregateNode = new JQuickHashAggregatePhysicalNode(
            groupKeys, aggregates, scanNode, null, JQuickHashAggregatePhysicalNode.AggregateStage.SINGLE
        );
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(aggregateNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 3 种状态", 3, result.size());
        
        // 验证结果数据
        Map<String, Long> statusCounts = new HashMap<>();
        for (JQuickRow row : result.getRows()) {
            String status = (String) row.get("status");
            Long count = (Long) row.get("count");
            statusCounts.put(status, count);
        }
        
        // completed: 4 个商品
        assertEquals("completed 状态应有 4 个商品", Long.valueOf(4), statusCounts.get("completed"));
        // pending: 2 个商品
        assertEquals("pending 状态应有 2 个商品", Long.valueOf(2), statusCounts.get("pending"));
        // cancelled: 1 个商品
        assertEquals("cancelled 状态应有 1 个商品", Long.valueOf(1), statusCounts.get("cancelled"));
    }

    /**
     * 测试聚合后按聚合结果排序（升序）
     * 
     * 场景：按部门统计员工数量，并按员工数量升序排序
     * SQL: SELECT dept_id, COUNT(*) as emp_count FROM employee GROUP BY dept_id ORDER BY emp_count ASC
     * 预期：按员工数量升序排列（部门 3: 2, 部门 2: 2, 部门 1: 3）
     */
    @Test
    public void testAggregateWithOrderByAsc() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        List<JQuickExpression> groupKeys = Arrays.asList(
            new JQuickColumnRefExpression("dept_id")
        );
        List<JQuickHashAggregatePhysicalNode.AggregateFunction> aggregates = Arrays.asList(
            new JQuickHashAggregatePhysicalNode.AggregateFunction("count", null, false, "emp_count")
        );
        JQuickHashAggregatePhysicalNode aggregateNode = new JQuickHashAggregatePhysicalNode(
            groupKeys, aggregates, scanNode, null, JQuickHashAggregatePhysicalNode.AggregateStage.SINGLE
        );
        List<JQuickSortPhysicalNode.OrderByItem> orderByItems = Arrays.asList(
            new JQuickSortPhysicalNode.OrderByItem("emp_count", true, true)
        );
        JQuickSortPhysicalNode sortNode = new JQuickSortPhysicalNode(orderByItems, aggregateNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(sortNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 3 个分组", 3, result.size());
        
        // 验证排序结果（升序）
        // 部门 3 和部门 2 都是 2 个员工，部门 1 有 3 个员工
        // 排序后应该是: 2, 2, 3 (或 3, 2, 2 取决于稳定排序)
        List<Long> empCounts = new ArrayList<>();
        for (JQuickRow row : result.getRows()) {
            empCounts.add((Long) row.get("emp_count"));
        }
        
        // 验证最小的两个是 2
        assertTrue("第一个或第二个应该是 2", empCounts.get(0) == 2 || empCounts.get(1) == 2);
        assertTrue("第一个或第二个应该是 2", empCounts.get(0) == 2 || empCounts.get(1) == 2);
        // 验证最大的一个是 3
        assertTrue("最后一个应该是 3", empCounts.get(2) == 3);
    }

    /**
     * 测试聚合后按聚合结果排序（降序）
     * 
     * 场景：按部门统计员工数量，并按员工数量降序排序
     * SQL: SELECT dept_id, COUNT(*) as emp_count FROM employee GROUP BY dept_id ORDER BY emp_count DESC
     * 预期：按员工数量降序排列（部门 1: 3, 部门 2: 2, 部门 3: 2）
     */
    @Test
    public void testAggregateWithOrderByDesc() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        List<JQuickExpression> groupKeys = Arrays.asList(
            new JQuickColumnRefExpression("dept_id")
        );
        List<JQuickHashAggregatePhysicalNode.AggregateFunction> aggregates = Arrays.asList(
            new JQuickHashAggregatePhysicalNode.AggregateFunction("count", null, false, "emp_count")
        );
        
        JQuickHashAggregatePhysicalNode aggregateNode = new JQuickHashAggregatePhysicalNode(
            groupKeys, aggregates, scanNode, null, JQuickHashAggregatePhysicalNode.AggregateStage.SINGLE
        );
        List<JQuickSortPhysicalNode.OrderByItem> orderByItems = Arrays.asList(
            new JQuickSortPhysicalNode.OrderByItem("emp_count", false, true)
        );
        JQuickSortPhysicalNode sortNode = new JQuickSortPhysicalNode(orderByItems, aggregateNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(sortNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 3 个分组", 3, result.size());
        // 部门 1 有 3 个员工（最多），部门 2 和部门 3 有 2 个员工
        JQuickRow firstRow = result.getRows().get(0);
        JQuickRow lastRow = result.getRows().get(2);
        assertEquals("第一个应该是 3（部门 1）", Long.valueOf(3), firstRow.get("emp_count"));
        assertEquals("最后一个应该是 2（部门 2 或 3）", Long.valueOf(2), lastRow.get("emp_count"));
    }

    /**
     * 测试 HAVING 条件 + 按聚合结果排序
     * 
     * 场景：按部门统计员工数量，筛选员工数量 >= 2 的部门，并按员工数量降序排序
     * SQL: SELECT dept_id, COUNT(*) as emp_count FROM employee GROUP BY dept_id HAVING COUNT(*) >= 2 ORDER BY emp_count DESC
     * 预期：返回 3 个部门（都有 >= 2 个员工），按员工数量降序排列
     */
    @Test
    public void testAggregateWithHavingAndOrderBy() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        List<JQuickExpression> groupKeys = Arrays.asList(
            new JQuickColumnRefExpression("dept_id")
        );
        List<JQuickHashAggregatePhysicalNode.AggregateFunction> aggregates = Arrays.asList(
            new JQuickHashAggregatePhysicalNode.AggregateFunction("count", null, false, "emp_count")
        );
        JQuickExpression havingCondition = new JQuickBinaryExpression(
            new JQuickColumnRefExpression("emp_count"),
            new JQuickLiteralExpression(2L),            com.github.paohaijiao.enums.JQuickBinaryOperator.GE
        );
        JQuickHashAggregatePhysicalNode aggregateNode = new JQuickHashAggregatePhysicalNode(
            groupKeys, aggregates, scanNode, havingCondition, JQuickHashAggregatePhysicalNode.AggregateStage.SINGLE
        );
        List<JQuickSortPhysicalNode.OrderByItem> orderByItems = Arrays.asList(
            new JQuickSortPhysicalNode.OrderByItem("emp_count", false, true)
        );
        JQuickSortPhysicalNode sortNode = new JQuickSortPhysicalNode(orderByItems, aggregateNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(sortNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 3 个分组（所有部门都满足 >= 2）", 3, result.size());
        JQuickRow firstRow = result.getRows().get(0);
        JQuickRow lastRow = result.getRows().get(2);
        assertEquals("第一个应该是 3（部门 1）", Long.valueOf(3), firstRow.get("emp_count"));
        assertEquals("最后一个应该是 2（部门 2 或 3）", Long.valueOf(2), lastRow.get("emp_count"));
    }

    /**
     * 测试按分组键排序 + 聚合结果排序
     * 
     * 场景：按部门统计员工数量，按部门 ID 升序 + 员工数量降序排序
     * SQL: SELECT dept_id, COUNT(*) as emp_count FROM employee GROUP BY dept_id ORDER BY dept_id ASC, emp_count DESC
     * 预期：先按部门 ID 升序，再按员工数量降序
     */
    @Test
    public void testAggregateWithMultipleOrderBy() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        List<JQuickExpression> groupKeys = Arrays.asList(
            new JQuickColumnRefExpression("dept_id")
        );
        List<JQuickHashAggregatePhysicalNode.AggregateFunction> aggregates = Arrays.asList(
            new JQuickHashAggregatePhysicalNode.AggregateFunction("count", null, false, "emp_count")
        );
        JQuickHashAggregatePhysicalNode aggregateNode = new JQuickHashAggregatePhysicalNode(
            groupKeys, aggregates, scanNode, null, JQuickHashAggregatePhysicalNode.AggregateStage.SINGLE
        );
        List<JQuickSortPhysicalNode.OrderByItem> orderByItems = Arrays.asList(
            new JQuickSortPhysicalNode.OrderByItem("dept_id", true, true),
            new JQuickSortPhysicalNode.OrderByItem("emp_count", false, true)
        );
        JQuickSortPhysicalNode sortNode = new JQuickSortPhysicalNode(orderByItems, aggregateNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(sortNode, context);
        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 3 个分组", 3, result.size());
        // dept_id ASC: 1, 2, 3
        // emp_count DESC (当 dept_id 相同时): N/A（每个部门只有一行）
        JQuickRow row1 = result.getRows().get(0);
        JQuickRow row2 = result.getRows().get(1);
        JQuickRow row3 = result.getRows().get(2);
        
        assertEquals("第一个应该是部门 1", Long.valueOf(1), row1.get("dept_id"));
        assertEquals("第二个应该是部门 2", Long.valueOf(2), row2.get("dept_id"));
        assertEquals("第三个应该是部门 3", Long.valueOf(3), row3.get("dept_id"));
        
        // emp_count 应该是 3, 2, 2 (按 emp_count DESC，但由于 dept_id ASC primary sort)
        assertEquals("部门 1 应该有 3 个员工", Long.valueOf(3), row1.get("emp_count"));
        assertEquals("部门 2 应该有 2 个员工", Long.valueOf(2), row2.get("emp_count"));
        assertEquals("部门 3 应该有 2 个员工", Long.valueOf(2), row3.get("emp_count"));
    }

    /**
     * 测试 SUM 聚合 + 按 SUM 结果排序
     * 
     * 场景：按部门统计工资总额，并按工资总额降序排序
     * SQL: SELECT dept_id, SUM(salary) as total_salary FROM employee GROUP BY dept_id ORDER BY total_salary DESC
     * 预期：按工资总额降序排列（部门 1: 30000, 部门 2: 18500, 部门 3: 14500）
     */
    @Test
    public void testSumWithOrderBy() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        List<JQuickExpression> groupKeys = Arrays.asList(
            new JQuickColumnRefExpression("dept_id")
        );
        List<JQuickHashAggregatePhysicalNode.AggregateFunction> aggregates = Arrays.asList(
            new JQuickHashAggregatePhysicalNode.AggregateFunction("sum", new JQuickColumnRefExpression("salary"), false, "total_salary")
        );
        JQuickHashAggregatePhysicalNode aggregateNode = new JQuickHashAggregatePhysicalNode(
            groupKeys, aggregates, scanNode, null, JQuickHashAggregatePhysicalNode.AggregateStage.SINGLE
        );
        List<JQuickSortPhysicalNode.OrderByItem> orderByItems = Arrays.asList(
            new JQuickSortPhysicalNode.OrderByItem("total_salary", false, true)
        );
        JQuickSortPhysicalNode sortNode = new JQuickSortPhysicalNode(orderByItems, aggregateNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(sortNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 3 个分组", 3, result.size());
        JQuickRow firstRow = result.getRows().get(0);
        JQuickRow lastRow = result.getRows().get(2);
        // 技术部: 8000 + 10000 + 12000 = 30000 (最高)
        assertEquals("第一个应该是部门 1（工资总额最高）", Long.valueOf(1), firstRow.get("dept_id"));
        assertEquals("部门 1 工资总额应为 30000.0", 30000.0, (Double) firstRow.get("total_salary"), 0.01);
        
        // 人事部: 7000 + 7500 = 14500 (最低)
        assertEquals("最后一个应该是部门 3（工资总额最低）", Long.valueOf(3), lastRow.get("dept_id"));
        assertEquals("部门 3 工资总额应为 14500.0", 14500.0, (Double) lastRow.get("total_salary"), 0.01);
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
