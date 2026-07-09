package com.github.paohaijiao.distribute.nodeExecutor.executeNode.topn;

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
import com.github.paohaijiao.physical.node.JQuickTopNPhysicalNode;
import com.github.paohaijiao.proto.JQuickExecuteTaskRequest;
import com.github.paohaijiao.statement.JQuickColumnMeta;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * JQuickTopNPhysicalNode 单元测试
 * 
 * 测试范围：
 * 1. 基本 TopN（排序 + LIMIT）
 * 2. 带 OFFSET 的 TopN
 * 3. 升序排序 TopN
 * 4. 降序排序 TopN
 * 5. 多列排序 TopN
 * 6. NULL 值处理
 * 7. 与 Filter 组合
 * 8. 边界条件
 */
public class JQuickTopNPhysicalNodeTest {

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
        employeeRows.add(createRow(employeeColumns, new Object[]{9L, "Ivy", 5L, 8500.0, 26}));
        employeeRows.add(createRow(employeeColumns, new Object[]{10L, "Jack", 5L, 9200.0, 31}));
        employeeRows.add(createRow(employeeColumns, new Object[]{11L, "Kate", null, 8800.0, 24}));
        employeeRows.add(createRow(employeeColumns, new Object[]{12L, "Leo", 6L, null, 34}));
        JQuickDataSet employeeData = new JQuickDataSet(employeeColumns, employeeRows);
        JQuickDataSourceManager.registerTable("employee", employeeData);
        List<JQuickColumnMeta> productColumns = Arrays.asList(
                new JQuickColumnMeta("product_id", Long.class, "product"),
                new JQuickColumnMeta("product_name", String.class, "product"),
                new JQuickColumnMeta("price", Double.class, "product"),
                new JQuickColumnMeta("stock", Integer.class, "product")
        );
        List<JQuickRow> productRows = new ArrayList<>();
        productRows.add(createRow(productColumns, new Object[]{1L, "鼠标", 99.99, 300}));
        productRows.add(createRow(productColumns, new Object[]{2L, "键盘", 149.99, 200}));
        productRows.add(createRow(productColumns, new Object[]{3L, "显示器", 1999.99, 50}));
        productRows.add(createRow(productColumns, new Object[]{4L, "耳机", 399.99, 100}));
        productRows.add(createRow(productColumns, new Object[]{5L, "摄像头", 299.99, 150}));
        productRows.add(createRow(productColumns, new Object[]{6L, "麦克风", 199.99, 80}));
        productRows.add(createRow(productColumns, new Object[]{7L, "音箱", 599.99, 60}));
        productRows.add(createRow(productColumns, new Object[]{8L, "鼠标垫", 29.99, 500}));
        JQuickDataSet productData = new JQuickDataSet(productColumns, productRows);
        JQuickDataSourceManager.registerTable("product", productData);
    }

    private JQuickRow createRow(List<JQuickColumnMeta> columns, Object[] values) {
        JQuickRow row = new JQuickRow();
        for (int i = 0; i < columns.size(); i++) {
            row.put(columns.get(i).getName(), values[i]);
        }
        return row;
    }

    /**
     * 测试基本 TopN（获取工资最高的前 3 名员工）
     * 
     * 目的：验证 TopN 节点同时执行排序和限制
     * 预期：返回工资最高的 3 个人（Charlie, Henry, Bob）
     */
    @Test
    public void testTopN_Basic() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        List<JQuickSortPhysicalNode.OrderByItem> orderByItems = new ArrayList<>();
        orderByItems.add(new JQuickSortPhysicalNode.OrderByItem("salary", false,true));
        JQuickTopNPhysicalNode topNNode = new JQuickTopNPhysicalNode(orderByItems, 3, 0, scanNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(topNNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 3 条数据", 3, result.size());
        assertEquals("第一行应该是 Charlie（最高工资）", "Charlie", result.getRows().get(0).get("emp_name"));
        assertEquals("工资应该是 12000", 12000.0, result.getRows().get(0).get("salary"));
        assertEquals("第二行应该是 David", "David", result.getRows().get(1).get("emp_name"));
        assertEquals("工资应该是 11000.0", 11000.0, result.getRows().get(1).get("salary"));
        assertEquals("第三行应该是 Henry", "Henry", result.getRows().get(2).get("emp_name"));
        assertEquals("工资应该是 10500.0", 10500.0, result.getRows().get(2).get("salary"));
    }

    /**
     * 测试 TopN 获取工资最低的前 3 名员工
     * 
     * 目的：验证升序排序的 TopN
     * 预期：返回工资最低的 3 个人
     */
    @Test
    public void testTopN_Ascending() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        List<JQuickSortPhysicalNode.OrderByItem> orderByItems = new ArrayList<>();
        orderByItems.add(new JQuickSortPhysicalNode.OrderByItem("salary", true));
        JQuickTopNPhysicalNode topNNode = new JQuickTopNPhysicalNode(orderByItems, 3, 0, scanNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(topNNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 3 条数据", 3, result.size());
        // Frank: 7500, Alice: 8000, Ivy: 8500
        assertEquals("第一行应该是 Frank（最低工资）", "Frank", result.getRows().get(0).get("emp_name"));
        assertEquals("工资应该是 7500", 7500.0, result.getRows().get(0).get("salary"));
        assertEquals("第二行应该是 Alice", "Alice", result.getRows().get(1).get("emp_name"));
        assertEquals("工资应该是 8000", 8000.0, result.getRows().get(1).get("salary"));
        assertEquals("第三行应该是 Ivy", "Ivy", result.getRows().get(2).get("emp_name"));
        assertEquals("工资应该是 8500", 8500.0, result.getRows().get(2).get("salary"));
    }

    /**
     * 测试 TopN 与 OFFSET
     * 
     * 目的：验证 TopN 带 OFFSET 参数
     * 预期：返回工资第 4-6 高的员工
     */
    @Test
    public void testTopN_WithOffset() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        List<JQuickSortPhysicalNode.OrderByItem> orderByItems = new ArrayList<>();
        orderByItems.add(new JQuickSortPhysicalNode.OrderByItem("salary", false));
        JQuickTopNPhysicalNode topNNode = new JQuickTopNPhysicalNode(orderByItems, 3, 3, scanNode);
        // 执行查询
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(topNNode, context);
        result.printTable();
        // 验证结果
        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 3 条数据", 3, result.size());
        // 验证返回的是工资第 4-6 高的员工
        // David: 11000, Grace: 9500, Jack: 9200
        assertEquals("第一行应该是 Henry", "Henry", result.getRows().get(0).get("emp_name"));
        assertEquals("工资应该是 10500", 10500.0, result.getRows().get(0).get("salary"));
        assertEquals("第二行应该是 Bob", "Bob", result.getRows().get(1).get("emp_name"));
        assertEquals("工资应该是 10000", 10000.0, result.getRows().get(1).get("salary"));
        assertEquals("第三行应该是 Grace", "Grace", result.getRows().get(2).get("emp_name"));
        assertEquals("工资应该是 9500", 9500.0, result.getRows().get(2).get("salary"));
    }

    /**
     * 测试 TopN 按年龄排序
     * 
     * 目的：验证按其他列排序的 TopN
     * 预期：返回年龄最小的 3 个人
     */
    @Test
    public void testTopN_ByAge() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        List<JQuickSortPhysicalNode.OrderByItem> orderByItems = new ArrayList<>();
        orderByItems.add(new JQuickSortPhysicalNode.OrderByItem("age", true));
        JQuickTopNPhysicalNode topNNode = new JQuickTopNPhysicalNode(orderByItems, 3, 0, scanNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(topNNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 3 条数据", 3, result.size());
        // Kate: 24, Alice: 25, Ivy: 26
        assertEquals("第一行应该是 Kate（最小年龄）", "Kate", result.getRows().get(0).get("emp_name"));
        assertEquals("年龄应该是 24", 24, result.getRows().get(0).get("age"));
        assertEquals("第二行应该是 Alice", "Alice", result.getRows().get(1).get("emp_name"));
        assertEquals("年龄应该是 25", 25, result.getRows().get(1).get("age"));
        assertEquals("第三行应该是 Ivy", "Ivy", result.getRows().get(2).get("emp_name"));
        assertEquals("年龄应该是 26", 26, result.getRows().get(2).get("age"));
    }

    /**
     * 测试 TopN 多列排序
     * 
     * 目的：验证按多个列排序的 TopN
     * 预期：先按 dept_id 升序，再按 salary 降序，返回前 4 条
     */
    @Test
    public void testTopN_MultipleColumns() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        List<JQuickSortPhysicalNode.OrderByItem> orderByItems = new ArrayList<>();
        orderByItems.add(new JQuickSortPhysicalNode.OrderByItem("dept_id", true));
        orderByItems.add(new JQuickSortPhysicalNode.OrderByItem("salary", false));
        JQuickTopNPhysicalNode topNNode = new JQuickTopNPhysicalNode(orderByItems, 4, 0, scanNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(topNNode, context);
        result.printTable();

    }

    /**
     * 测试 TopN 处理 NULL 值（默认 nulls last）
     * 
     * 目的：验证 TopN 对 NULL 值的处理
     * 预期：NULL 值应该在排序结果的最后
     */
    @Test
    public void testTopN_NullValues_Default() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        List<JQuickSortPhysicalNode.OrderByItem> orderByItems = new ArrayList<>();
        orderByItems.add(new JQuickSortPhysicalNode.OrderByItem("salary", true));
        JQuickTopNPhysicalNode topNNode = new JQuickTopNPhysicalNode(orderByItems, 4, 0, scanNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(topNNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 4 条数据", 4, result.size());
        // 验证返回的结果（Leo 的 salary 为 null，应该在最后）
        // Frank: 7500, Alice: 8000, Ivy: 8500, Kate: 8800
        assertEquals("第一行应该是 Frank", "Frank", result.getRows().get(0).get("emp_name"));
        assertEquals("工资应该是 7500", 7500.0, result.getRows().get(0).get("salary"));
        assertEquals("第二行应该是 Alice", "Alice", result.getRows().get(1).get("emp_name"));
        assertEquals("工资应该是 8000", 8000.0, result.getRows().get(1).get("salary"));
        assertEquals("第三行应该是 Ivy", "Ivy", result.getRows().get(2).get("emp_name"));
        assertEquals("工资应该是 8500", 8500.0, result.getRows().get(2).get("salary"));
        assertEquals("第四行应该是 Kate", "Kate", result.getRows().get(3).get("emp_name"));
        assertEquals("工资应该是 8800", 8800.0, result.getRows().get(3).get("salary"));
        // Leo 的 salary 为 null，应该不在前 4 名中
        assertNotEquals("Leo 不应该在前 4 名中", "Leo", result.getRows().get(3).get("emp_name"));
    }

    /**
     * 测试 TopN 处理 NULL 值（nulls first）
     * 
     * 目的：验证 TopN 显式指定 nulls first
     * 预期：NULL 值应该在排序结果的最前面
     */
    @Test
    public void testTopN_NullValues_NullsFirst() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        List<JQuickSortPhysicalNode.OrderByItem> orderByItems = new ArrayList<>();
        orderByItems.add(new JQuickSortPhysicalNode.OrderByItem("salary", true, true)); // nullsFirst = true
        JQuickTopNPhysicalNode topNNode = new JQuickTopNPhysicalNode(orderByItems, 3, 0, scanNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(topNNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 3 条数据", 3, result.size());
        assertEquals("第一行应该是 Leo（salary 为 null，nulls first）", "Leo", result.getRows().get(0).get("emp_name"));
    }

    /**
     * 测试 TopN 与 Filter 组合
     * 
     * 目的：验证 TopN 与 Filter 节点组合使用
     * 预期：先过滤 dept_id=1 的员工，然后返回工资最高的前 2 名
     */
    @Test
    public void testTopN_WithFilter() {
        JQuickExpression filterExpr = createBinaryExpression(new JQuickColumnRefExpression("dept_id"), new JQuickLiteralExpression(1L), JQuickBinaryOperator.EQ);
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickFilterPhysicalNode filterNode = new JQuickFilterPhysicalNode(filterExpr, scanNode);
        List<JQuickSortPhysicalNode.OrderByItem> orderByItems = new ArrayList<>();
        orderByItems.add(new JQuickSortPhysicalNode.OrderByItem("salary", false));
        JQuickTopNPhysicalNode topNNode = new JQuickTopNPhysicalNode(orderByItems, 2, 0, filterNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(topNNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 2 条数据", 2, result.size());
        // 验证返回的是 dept_id=1 中工资最高的 2 个人
        // Bob: 10000, Alice: 8000
        assertEquals("第一行应该是 Bob（dept_id=1 中工资最高）", "Bob", result.getRows().get(0).get("emp_name"));
        assertEquals("工资应该是 10000", 10000.0, result.getRows().get(0).get("salary"));
        assertEquals("第二行应该是 Alice", "Alice", result.getRows().get(1).get("emp_name"));
        assertEquals("工资应该是 8000", 8000.0, result.getRows().get(1).get("salary"));
    }

    /**
     * 测试 TopN Top-1
     * 
     * 目的：验证 TopN 获取单一最值
     * 预期：返回工资最高的员工
     */
    @Test
    public void testTopN_One() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        List<JQuickSortPhysicalNode.OrderByItem> orderByItems = new ArrayList<>();
        orderByItems.add(new JQuickSortPhysicalNode.OrderByItem("salary", false));
        JQuickTopNPhysicalNode topNNode = new JQuickTopNPhysicalNode(orderByItems, 1, 0, scanNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(topNNode, context);
        result.printTable();
    }

    /**
     * 测试 TopN N=0
     * 
     * 目的：验证 TopN limit=0 的情况
     * 预期：返回空结果集
     */
    @Test
    public void testTopN_Zero() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        List<JQuickSortPhysicalNode.OrderByItem> orderByItems = new ArrayList<>();
        orderByItems.add(new JQuickSortPhysicalNode.OrderByItem("salary", false));
        JQuickTopNPhysicalNode topNNode = new JQuickTopNPhysicalNode(orderByItems, 0, 0, scanNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(topNNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 0 条数据", 0, result.size());
    }

    /**
     * 测试 TopN N 大于结果集大小
     * 
     * 目的：验证 TopN limit 大于结果集大小
     * 预期：返回所有匹配的记录
     */
    @Test
    public void testTopN_GreaterThanResultSet() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        List<JQuickSortPhysicalNode.OrderByItem> orderByItems = new ArrayList<>();
        orderByItems.add(new JQuickSortPhysicalNode.OrderByItem("salary", false));
        JQuickTopNPhysicalNode topNNode = new JQuickTopNPhysicalNode(orderByItems, 100, 0, scanNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(topNNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回所有 12 条数据", 12, result.size());
        for (int i = 0; i < result.size() - 1; i++) {
            Double current = (Double) result.getRows().get(i).get("salary");
            Double next = (Double) result.getRows().get(i + 1).get("salary");
            if (current != null && next != null) {
                assertTrue("第 " + (i+1) + " 条记录的工资应该 >= 第 " + (i+2) + " 条记录",
                    current >= next);
            }
        }
    }

    /**
     * 测试 TopN OFFSET 等于结果集大小
     * 
     * 目的：验证 OFFSET 等于结果集大小
     * 预期：返回空结果集
     */
    @Test
    public void testTopN_OffsetEqualsResultSet() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        List<JQuickSortPhysicalNode.OrderByItem> orderByItems = new ArrayList<>();
        orderByItems.add(new JQuickSortPhysicalNode.OrderByItem("salary", false));
        JQuickTopNPhysicalNode topNNode = new JQuickTopNPhysicalNode(orderByItems, 5, 12, scanNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(topNNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 0 条数据", 0, result.size());
    }

    /**
     * 测试 TopN 在空结果集上
     * 
     * 目的：验证对空结果集应用 TopN
     * 预期：返回空结果集
     */
    @Test
    public void testTopN_OnEmptyResultSet() {
        JQuickExpression filterExpr = createBinaryExpression(new JQuickColumnRefExpression("emp_id"), new JQuickLiteralExpression(999L), JQuickBinaryOperator.EQ);
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickFilterPhysicalNode filterNode = new JQuickFilterPhysicalNode(filterExpr, scanNode);
        List<JQuickSortPhysicalNode.OrderByItem> orderByItems = new ArrayList<>();
        orderByItems.add(new JQuickSortPhysicalNode.OrderByItem("salary", false));
        JQuickTopNPhysicalNode topNNode = new JQuickTopNPhysicalNode(orderByItems, 5, 0, filterNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(topNNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 0 条数据", 0, result.size());
    }

    /**
     * 测试 TopN 保持列元数据
     * 
     * 目的：验证 TopN 不改变列的元数据
     * 预期：返回的 DataSet 包含正确的列信息
     */
    @Test
    public void testTopN_PreservesMetadata() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        List<JQuickSortPhysicalNode.OrderByItem> orderByItems = new ArrayList<>();
        orderByItems.add(new JQuickSortPhysicalNode.OrderByItem("salary", false));
        JQuickTopNPhysicalNode topNNode = new JQuickTopNPhysicalNode(orderByItems, 3, 0, scanNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(topNNode, context);
        result.printTable();
        assertNotNull("列元数据不应为 null", result.getColumns());
        assertEquals("应该有 5 列", 5, result.getColumns().size());
        List<JQuickColumnMeta> columns = result.getColumns();
        assertEquals("第一列应该是 emp_id", "emp_id", columns.get(0).getName());
        assertEquals("第二列应该是 emp_name", "emp_name", columns.get(1).getName());
        assertEquals("第三列应该是 dept_id", "dept_id", columns.get(2).getName());
        assertEquals("第四列应该是 salary", "salary", columns.get(3).getName());
        assertEquals("第五列应该是 age", "age", columns.get(4).getName());
    }

    /**
     * 测试 TopN 分页查询
     * 
     * 目的：验证 TopN 用于分页查询
     * 预期：每页返回 3 条记录
     */
    @Test
    public void testTopN_Pagination() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        List<JQuickSortPhysicalNode.OrderByItem> orderByItems = new ArrayList<>();
        orderByItems.add(new JQuickSortPhysicalNode.OrderByItem("salary", false));
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickTopNPhysicalNode page1Node = new JQuickTopNPhysicalNode(orderByItems, 3, 0, scanNode);
        JQuickDataSet page1 = nodeExecutor.executeNode(page1Node, context);
        page1.printTable();

    }

    /**
     * 测试 TopN 按字符串排序
     * 
     * 目的：验证按字符串列排序的 TopN
     * 预期：返回名字字典序最后的 3 个人
     */
    @Test
    public void testTopN_ByStringColumn() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        List<JQuickSortPhysicalNode.OrderByItem> orderByItems = new ArrayList<>();
        orderByItems.add(new JQuickSortPhysicalNode.OrderByItem("emp_name", false));
        JQuickTopNPhysicalNode topNNode = new JQuickTopNPhysicalNode(orderByItems, 3, 0, scanNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(topNNode, context);
        result.printTable();

    }

    /**
     * 创建二元表达式
     */
    private JQuickExpression createBinaryExpression(JQuickExpression left, JQuickExpression right, com.github.paohaijiao.enums.JQuickBinaryOperator operator) {
        return new JQuickBinaryExpression(left, right, operator);
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
