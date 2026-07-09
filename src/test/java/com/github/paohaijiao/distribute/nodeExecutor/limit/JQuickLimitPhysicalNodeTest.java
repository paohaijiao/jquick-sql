package com.github.paohaijiao.distribute.nodeExecutor.limit;

import com.github.paohaijiao.datasource.JQuickDataSourceManager;
import com.github.paohaijiao.distributed.worker.*;
import com.github.paohaijiao.enums.JQuickBinaryOperator;
import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.expression.domain.JQuickBinaryExpression;
import com.github.paohaijiao.expression.domain.JQuickColumnRefExpression;
import com.github.paohaijiao.expression.domain.JQuickLiteralExpression;
import com.github.paohaijiao.function.manager.JQuickMethodInvocationManager;
import com.github.paohaijiao.physical.node.JQuickFilterPhysicalNode;
import com.github.paohaijiao.physical.node.JQuickLimitPhysicalNode;
import com.github.paohaijiao.physical.node.JQuickTableScanPhysicalNode;
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
 * JQuickLimitPhysicalNode 单元测试
 * 
 * 测试范围：
 * 1. 基本 LIMIT
 * 2. 带 OFFSET 的 LIMIT
 * 3. LIMIT 0
 * 4. LIMIT 大于结果集大小
 * 5. OFFSET 大于结果集大小
 * 6. 与其他节点（Filter、Sort）组合
 * 7. 边界条件
 */
public class JQuickLimitPhysicalNodeTest {

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
        JQuickDataSet employeeData = new JQuickDataSet(employeeColumns, employeeRows);
        JQuickDataSourceManager.registerTable("employee", employeeData);
    }

    private JQuickRow createRow(List<JQuickColumnMeta> columns, Object[] values) {
        JQuickRow row = new JQuickRow();
        for (int i = 0; i < columns.size(); i++) {
            row.put(columns.get(i).getName(), values[i]);
        }
        return row;
    }

    /**
     * 测试基本 LIMIT
     * 
     * 目的：验证 LIMIT n 返回前 n 条记录
     * 预期：返回前 3 条记录
     */
    @Test
    public void testLimit_Basic() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickLimitPhysicalNode limitNode = new JQuickLimitPhysicalNode(3, scanNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(limitNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 3 条数据", 3, result.size());
        assertEquals("第一行应该是 Alice", "Alice", result.getRows().get(0).get("emp_name"));
        assertEquals("第二行应该是 Bob", "Bob", result.getRows().get(1).get("emp_name"));
        assertEquals("第三行应该是 Charlie", "Charlie", result.getRows().get(2).get("emp_name"));
    }

    /**
     * 测试 LIMIT 1
     * 
     * 目的：验证 LIMIT 1 只返回第一条记录
     * 预期：返回 1 条记录（Alice）
     */
    @Test
    public void testLimit_One() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickLimitPhysicalNode limitNode = new JQuickLimitPhysicalNode(1, scanNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(limitNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 1 条数据", 1, result.size());
        assertEquals("应该返回 Alice", "Alice", result.getRows().get(0).get("emp_name"));
    }

    /**
     * 测试 LIMIT 0
     * 
     * 目的：验证 LIMIT 0 返回空结果集
     * 预期：返回 0 条记录
     */
    @Test
    public void testLimit_Zero() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickLimitPhysicalNode limitNode = new JQuickLimitPhysicalNode(0, scanNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(limitNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 0 条数据", 0, result.size());
    }

    /**
     * 测试 LIMIT 大于结果集大小
     * 
     * 目的：验证 LIMIT n 当 n 大于结果集大小时，返回所有记录
     * 预期：返回所有 10 条记录
     */
    @Test
    public void testLimit_GreaterThanResultSet() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickLimitPhysicalNode limitNode = new JQuickLimitPhysicalNode(100, scanNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(limitNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回所有 10 条数据", 10, result.size());
    }

    /**
     * 测试带 OFFSET 的 LIMIT
     * 
     * 目的：验证 LIMIT m OFFSET n 返回从第 n 条开始的 m 条记录
     * 预期：返回第 4-6 条记录（David, Eve, Frank）
     */
    @Test
    public void testLimit_WithOffset() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickLimitPhysicalNode limitNode = new JQuickLimitPhysicalNode(3, 3, scanNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(limitNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 3 条数据", 3, result.size());
        assertEquals("第一行应该是 David", "David", result.getRows().get(0).get("emp_name"));
        assertEquals("第二行应该是 Eve", "Eve", result.getRows().get(1).get("emp_name"));
        assertEquals("第三行应该是 Frank", "Frank", result.getRows().get(2).get("emp_name"));
    }

    /**
     * 测试 OFFSET 等于结果集大小
     * 
     * 目的：验证 OFFSET 等于结果集大小时返回空结果集
     * 预期：返回 0 条记录
     */
    @Test
    public void testLimit_OffsetEqualsResultSetSize() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickLimitPhysicalNode limitNode = new JQuickLimitPhysicalNode(3, 10, scanNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(limitNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 0 条数据", 0, result.size());
    }

    /**
     * 测试 OFFSET 大于结果集大小
     * 
     * 目的：验证 OFFSET 大于结果集大小时返回空结果集
     * 预期：返回 0 条记录
     */
    @Test
    public void testLimit_OffsetGreaterThanResultSet() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickLimitPhysicalNode limitNode = new JQuickLimitPhysicalNode(3, 15, scanNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(limitNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 0 条数据", 0, result.size());
    }

    /**
     * 测试 LIMIT 和 OFFSET 组合（最后一页）
     * 
     * 目的：验证 LIMIT 和 OFFSET 组合获取最后一页数据
     * 预期：返回第 8-10 条记录（Henry, Ivy, Jack）
     */
    @Test
    public void testLimit_LastPage() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickLimitPhysicalNode limitNode = new JQuickLimitPhysicalNode(3, 7, scanNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(limitNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 3 条数据", 3, result.size());
        assertEquals("第一行应该是 Henry", "Henry", result.getRows().get(0).get("emp_name"));
        assertEquals("第二行应该是 Ivy", "Ivy", result.getRows().get(1).get("emp_name"));
        assertEquals("第三行应该是 Jack", "Jack", result.getRows().get(2).get("emp_name"));
    }

    /**
     * 测试 OFFSET 0
     * 
     * 目的：验证 OFFSET 0 等同于没有 OFFSET
     * 预期：返回前 5 条记录
     */
    @Test
    public void testLimit_OffsetZero() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickLimitPhysicalNode limitNode = new JQuickLimitPhysicalNode(5, 0, scanNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(limitNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 5 条数据", 5, result.size());
        assertEquals("第一行应该是 Alice", "Alice", result.getRows().get(0).get("emp_name"));
        assertEquals("第二行应该是 Bob", "Bob", result.getRows().get(1).get("emp_name"));
        assertEquals("第三行应该是 Charlie", "Charlie", result.getRows().get(2).get("emp_name"));
        assertEquals("第四行应该是 David", "David", result.getRows().get(3).get("emp_name"));
        assertEquals("第五行应该是 Eve", "Eve", result.getRows().get(4).get("emp_name"));
    }

    /**
     * 测试 LIMIT 与 Filter 组合
     * 
     * 目的：验证 LIMIT 与 Filter 节点组合使用
     * 预期：先过滤 dept_id=1 的记录，然后返回前 2 条
     */
    @Test
    public void testLimit_WithFilter() {
        JQuickExpression filterExpr = createBinaryExpression(new JQuickColumnRefExpression("dept_id"), new JQuickLiteralExpression(1L), JQuickBinaryOperator.EQ);
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickFilterPhysicalNode filterNode = new JQuickFilterPhysicalNode(filterExpr, scanNode);
        JQuickLimitPhysicalNode limitNode = new JQuickLimitPhysicalNode(2, filterNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(limitNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 2 条数据", 2, result.size());
        assertEquals("第一行应该是 Alice", "Alice", result.getRows().get(0).get("emp_name"));
        assertEquals("第二行应该是 Bob", "Bob", result.getRows().get(1).get("emp_name"));
    }

    /**
     * 测试 LIMIT 与 Sort 组合
     * 
     * 目的：验证 LIMIT 与 Sort 节点组合使用（获取 Top N）
     * 预期：先按 salary 降序排序，然后返回前 3 条
     */
    @Test
    public void testLimit_WithSort() {
        List<com.github.paohaijiao.physical.node.JQuickSortPhysicalNode.OrderByItem> orderByItems = new ArrayList<>();
        orderByItems.add(new com.github.paohaijiao.physical.node.JQuickSortPhysicalNode.OrderByItem("salary", false));
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        com.github.paohaijiao.physical.node.JQuickSortPhysicalNode sortNode = new com.github.paohaijiao.physical.node.JQuickSortPhysicalNode(orderByItems, scanNode);
        JQuickLimitPhysicalNode limitNode = new JQuickLimitPhysicalNode(3, sortNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(limitNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 3 条数据", 3, result.size());
        assertEquals("第一行应该是 Charlie（最高工资）", "Charlie", result.getRows().get(0).get("emp_name"));
        assertEquals("工资应该是 12000", 12000.0, result.getRows().get(0).get("salary"));
        assertEquals("第二行应该是 Henry", "Henry", result.getRows().get(2).get("emp_name"));
        assertEquals("工资应该是 10500", 10500.0, result.getRows().get(2).get("salary"));
    }

    /**
     * 测试 LIMIT 与 Filter 和 Sort 组合
     * 
     * 目的：验证 LIMIT 与 Filter 和 Sort 节点组合使用
     * 预期：过滤 dept_id=2 的记录，按 salary 降序排序，返回前 1 条
     */
    @Test
    public void testLimit_WithFilterAndSort() {
        JQuickExpression filterExpr = createBinaryExpression(new JQuickColumnRefExpression("dept_id"), new JQuickLiteralExpression(2L), JQuickBinaryOperator.EQ);
        List<com.github.paohaijiao.physical.node.JQuickSortPhysicalNode.OrderByItem> orderByItems = new ArrayList<>();
        orderByItems.add(new com.github.paohaijiao.physical.node.JQuickSortPhysicalNode.OrderByItem("salary", false));
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        com.github.paohaijiao.physical.node.JQuickSortPhysicalNode sortNode = new com.github.paohaijiao.physical.node.JQuickSortPhysicalNode(orderByItems, scanNode);
        JQuickFilterPhysicalNode filterNode = new JQuickFilterPhysicalNode(filterExpr, sortNode);
        JQuickLimitPhysicalNode limitNode = new JQuickLimitPhysicalNode(1, filterNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(limitNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 1 条数据", 1, result.size());
        assertEquals("应该返回 Charlie（dept_id=2 中工资最高）", "Charlie", result.getRows().get(0).get("emp_name"));
        assertEquals("工资应该是 12000", 12000.0, result.getRows().get(0).get("salary"));
    }

    /**
     * 测试空结果集上的 LIMIT
     * 
     * 目的：验证对空结果集应用 LIMIT
     * 预期：返回空结果集
     */
    @Test
    public void testLimit_OnEmptyResultSet() {
        JQuickExpression filterExpr = createBinaryExpression(new JQuickColumnRefExpression("emp_id"), new JQuickLiteralExpression(999L), JQuickBinaryOperator.EQ);
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickFilterPhysicalNode filterNode = new JQuickFilterPhysicalNode(filterExpr, scanNode);
        JQuickLimitPhysicalNode limitNode = new JQuickLimitPhysicalNode(5, filterNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(limitNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 0 条数据", 0, result.size());
    }

    /**
     * 测试 LIMIT 保持列元数据
     * 
     * 目的：验证 LIMIT 不改变列的元数据
     * 预期：返回的 DataSet 包含正确的列信息
     */
    @Test
    public void testLimit_PreservesMetadata() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickLimitPhysicalNode limitNode = new JQuickLimitPhysicalNode(2, scanNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(limitNode, context);
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
     * 测试 LIMIT 与 OFFSET 组合（分页）
     * 
     * 目的：验证分页查询的正确性
     * 预期：第 1 页返回 3 条，第 2 页返回 3 条
     */
    @Test
    public void testLimit_Pagination() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickLimitPhysicalNode page1Node = new JQuickLimitPhysicalNode(3, 0, scanNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet page1 = nodeExecutor.executeNode(page1Node, context);
        page1.printTable();
        assertNotNull("第 1 页结果不应为 null", page1);
        assertEquals("第 1 页应该返回 3 条数据", 3, page1.size());
        assertEquals("第 1 页第一行应该是 Alice", "Alice", page1.getRows().get(0).get("emp_name"));
        JQuickLimitPhysicalNode page2Node = new JQuickLimitPhysicalNode(3, 3, scanNode);
        JQuickDataSet page2 = nodeExecutor.executeNode(page2Node, context);
        page2.printTable();
        assertNotNull("第 2 页结果不应为 null", page2);
        assertEquals("第 2 页应该返回 3 条数据", 3, page2.size());
        assertEquals("第 2 页第一行应该是 David", "David", page2.getRows().get(0).get("emp_name"));
        assertNotEquals("第 1 页和第 2 页不应该相同", page1.getRows().get(0).get("emp_id"), page2.getRows().get(0).get("emp_id"));
    }

    /**
     * 创建二元表达式
     */
    private JQuickExpression createBinaryExpression(JQuickExpression left, JQuickExpression right, com.github.paohaijiao.enums.JQuickBinaryOperator operator) {
        return new JQuickBinaryExpression(left, right,operator);
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
