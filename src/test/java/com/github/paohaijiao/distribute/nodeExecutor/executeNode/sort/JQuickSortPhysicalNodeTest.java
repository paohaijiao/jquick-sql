package com.github.paohaijiao.distribute.nodeExecutor.executeNode.sort;

import com.github.paohaijiao.datasource.JQuickDataSourceManager;
import com.github.paohaijiao.distributed.worker.*;
import com.github.paohaijiao.function.manager.JQuickMethodInvocationManager;
import com.github.paohaijiao.physical.node.JQuickSortPhysicalNode;
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
 * JQuickSortPhysicalNode 单元测试
 * 
 * 测试范围：
 * 1. 升序排序
 * 2. 降序排序
 * 3. 多列排序
 * 4. NULL 值处理
 * 5. 数字、字符串、日期排序
 * 6. 边界条件
 */
public class JQuickSortPhysicalNodeTest {

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
        // employee 表
        List<JQuickColumnMeta> employeeColumns = Arrays.asList(
                new JQuickColumnMeta("emp_id", Long.class, "employee"),
                new JQuickColumnMeta("emp_name", String.class, "employee"),
                new JQuickColumnMeta("dept_id", Long.class, "employee"),
                new JQuickColumnMeta("salary", Double.class, "employee"),
                new JQuickColumnMeta("age", Integer.class, "employee")
        );
        List<JQuickRow> employeeRows = new ArrayList<>();
        // 添加测试数据（故意不按顺序）
        employeeRows.add(createRow(employeeColumns, new Object[]{3L, "Charlie", 2L, 12000.0, 28}));
        employeeRows.add(createRow(employeeColumns, new Object[]{1L, "Alice", 1L, 8000.0, 25}));
        employeeRows.add(createRow(employeeColumns, new Object[]{5L, "Eve", 3L, 9000.0, 32}));
        employeeRows.add(createRow(employeeColumns, new Object[]{2L, "Bob", 1L, 10000.0, 30}));
        employeeRows.add(createRow(employeeColumns, new Object[]{4L, "David", 2L, 11000.0, 35}));
        employeeRows.add(createRow(employeeColumns, new Object[]{6L, "Frank", null, 7500.0, null}));
        
        JQuickDataSet employeeData = new JQuickDataSet(employeeColumns, employeeRows);
        JQuickDataSourceManager.registerTable("employee", employeeData);

        // product 表
        List<JQuickColumnMeta> productColumns = Arrays.asList(
                new JQuickColumnMeta("product_id", Long.class, "product"),
                new JQuickColumnMeta("product_name", String.class, "product"),
                new JQuickColumnMeta("price", Double.class, "product"),
                new JQuickColumnMeta("stock", Integer.class, "product")
        );
        List<JQuickRow> productRows = new ArrayList<>();
        productRows.add(createRow(productColumns, new Object[]{3L, "键盘", 299.99, 150}));
        productRows.add(createRow(productColumns, new Object[]{1L, "鼠标", 99.99, 300}));
        productRows.add(createRow(productColumns, new Object[]{5L, "显示器", 1999.99, 50}));
        productRows.add(createRow(productColumns, new Object[]{2L, "键盘", 149.99, 200}));
        productRows.add(createRow(productColumns, new Object[]{4L, "耳机", 399.99, 100}));
        productRows.add(createRow(productColumns, new Object[]{6L, "摄像头", null, 80}));
        
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
     * 测试升序排序
     * 
     * 目的：验证按单个列升序排序的正确性
     * 预期：按 emp_id 升序排列
     */
    @Test
    public void testSort_Ascending() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        List<JQuickSortPhysicalNode.OrderByItem> orderByItems = new ArrayList<>();
        orderByItems.add(new JQuickSortPhysicalNode.OrderByItem("emp_id", true));
        JQuickSortPhysicalNode sortNode = new JQuickSortPhysicalNode(orderByItems, scanNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(sortNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 6 条数据", 6, result.size());
        assertEquals("第一行应该是 Alice", 1L, result.getRows().get(0).get("emp_id"));
        assertEquals("第二行应该是 Bob", 2L, result.getRows().get(1).get("emp_id"));
        assertEquals("第三行应该是 Charlie", 3L, result.getRows().get(2).get("emp_id"));
        assertEquals("第四行应该是 David", 4L, result.getRows().get(3).get("emp_id"));
        assertEquals("第五行应该是 Eve", 5L, result.getRows().get(4).get("emp_id"));
        assertEquals("第六行应该是 Frank", 6L, result.getRows().get(5).get("emp_id"));
    }

    /**
     * 测试降序排序
     * 
     * 目的：验证按单个列降序排序的正确性
     * 预期：按 salary 降序排列
     */
    @Test
    public void testSort_Descending() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        List<JQuickSortPhysicalNode.OrderByItem> orderByItems = new ArrayList<>();
        orderByItems.add(new JQuickSortPhysicalNode.OrderByItem("salary", false));
        JQuickSortPhysicalNode sortNode = new JQuickSortPhysicalNode(orderByItems, scanNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(sortNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 6 条数据", 6, result.size());
        // David: 11000, Charlie: 12000, Bob: 10000, Eve: 9000, Alice: 8000, Frank: 7500
        assertEquals("第一行应该是 Charlie（最高工资）", "Charlie", result.getRows().get(0).get("emp_name"));
        assertEquals("第二行应该是 David", "David", result.getRows().get(1).get("emp_name"));
        assertEquals("第三行应该是 Bob", "Bob", result.getRows().get(2).get("emp_name"));
        assertEquals("第四行应该是 Eve", "Eve", result.getRows().get(3).get("emp_name"));
        assertEquals("第五行应该是 Alice", "Alice", result.getRows().get(4).get("emp_name"));
        assertEquals("第六行应该是 Frank（最低工资）", "Frank", result.getRows().get(5).get("emp_name"));
    }

    /**
     * 测试多列排序
     * 
     * 目的：验证按多个列排序的正确性（先按第一列排序，相同时按第二列排序）
     * 预期：先按 dept_id 升序，再按 salary 升序
     */
    @Test
    public void testSort_MultipleColumns() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        List<JQuickSortPhysicalNode.OrderByItem> orderByItems = new ArrayList<>();
        orderByItems.add(new JQuickSortPhysicalNode.OrderByItem("dept_id", true));
        orderByItems.add(new JQuickSortPhysicalNode.OrderByItem("salary", true));
        JQuickSortPhysicalNode sortNode = new JQuickSortPhysicalNode(orderByItems, scanNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(sortNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 6 条数据", 6, result.size());
        JQuickRow row0 = result.getRows().get(0);
        assertEquals("第一行应该是 Alice", "Alice", row0.get("emp_name"));
        assertEquals("dept_id 应该为 1", 1L, row0.get("dept_id"));
        
        JQuickRow row1 = result.getRows().get(1);
        assertEquals("第二行应该是 Bob", "Bob", row1.get("emp_name"));
        assertEquals("dept_id 应该为 1", 1L, row1.get("dept_id"));
        
        JQuickRow row2 = result.getRows().get(2);
        assertEquals("第三行应该是 David", "David", row2.get("emp_name"));
        assertEquals("dept_id 应该为 2", 2L, row2.get("dept_id"));
        
        JQuickRow row3 = result.getRows().get(3);
        assertEquals("第四行应该是 Charlie", "Charlie", row3.get("emp_name"));
        assertEquals("dept_id 应该为 2", 2L, row3.get("dept_id"));
        
        JQuickRow row4 = result.getRows().get(4);
        assertEquals("第五行应该是 Eve", "Eve", row4.get("emp_name"));
        assertEquals("dept_id 应该为 3", 3L, row4.get("dept_id"));
        
        JQuickRow row5 = result.getRows().get(5);
        assertEquals("第六行应该是 Frank（dept_id 为 null）", "Frank", row5.get("emp_name"));
    }

    /**
     * 测试字符串排序
     * 
     * 目的：验证字符串列排序的正确性（按字典顺序）
     * 预期：按 emp_name 升序排列
     */
    @Test
    public void testSort_StringColumn() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        List<JQuickSortPhysicalNode.OrderByItem> orderByItems = new ArrayList<>();
        orderByItems.add(new JQuickSortPhysicalNode.OrderByItem("emp_name", true));
        JQuickSortPhysicalNode sortNode = new JQuickSortPhysicalNode(orderByItems, scanNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(sortNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 6 条数据", 6, result.size());
        assertEquals("第一行应该是 Alice", "Alice", result.getRows().get(0).get("emp_name"));
        assertEquals("第二行应该是 Bob", "Bob", result.getRows().get(1).get("emp_name"));
        assertEquals("第三行应该是 Charlie", "Charlie", result.getRows().get(2).get("emp_name"));
        assertEquals("第四行应该是 David", "David", result.getRows().get(3).get("emp_name"));
        assertEquals("第五行应该是 Eve", "Eve", result.getRows().get(4).get("emp_name"));
        assertEquals("第六行应该是 Frank", "Frank", result.getRows().get(5).get("emp_name"));
    }

    /**
     * 测试 NULL 值排序（默认 nulls last）
     * 
     * 目的：验证 NULL 值在排序时的处理
     * 预期：NULL 值应该在最后（默认行为）
     */
    @Test
    public void testSort_NullValues_Default() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        List<JQuickSortPhysicalNode.OrderByItem> orderByItems = new ArrayList<>();
        orderByItems.add(new JQuickSortPhysicalNode.OrderByItem("dept_id", true));
        JQuickSortPhysicalNode sortNode = new JQuickSortPhysicalNode(orderByItems, scanNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(sortNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 6 条数据", 6, result.size());
        
        assertEquals("第一行应该是 Alice", "Alice", result.getRows().get(0).get("emp_name"));
        assertEquals("第二行应该是 Bob", "Bob", result.getRows().get(1).get("emp_name"));
        assertEquals("第三行应该是 Charlie", "Charlie", result.getRows().get(2).get("emp_name"));
        assertEquals("第四行应该是 David", "David", result.getRows().get(3).get("emp_name"));
        assertEquals("第五行应该是 Eve", "Eve", result.getRows().get(4).get("emp_name"));
        assertEquals("第六行应该是 Frank（dept_id 为 null，应该在最后）", "Frank", result.getRows().get(5).get("emp_name"));
    }

    /**
     * 测试 NULL 值排序（nulls first）
     * 
     * 目的：验证显式指定 nulls first 时的处理
     * 预期：NULL 值应该在最前面
     */
    @Test
    public void testSort_NullValues_NullsFirst() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        List<JQuickSortPhysicalNode.OrderByItem> orderByItems = new ArrayList<>();
        orderByItems.add(new JQuickSortPhysicalNode.OrderByItem("dept_id", true, true));
        JQuickSortPhysicalNode sortNode = new JQuickSortPhysicalNode(orderByItems, scanNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(sortNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 6 条数据", 6, result.size());
        assertEquals("第一行应该是 Frank（dept_id 为 null，nulls first）", "Frank", result.getRows().get(0).get("emp_name"));
        assertEquals("第二行应该是 Alice", "Alice", result.getRows().get(1).get("emp_name"));
        assertEquals("第三行应该是 Bob", "Bob", result.getRows().get(2).get("emp_name"));
        assertEquals("第四行应该是 Charlie", "Charlie", result.getRows().get(3).get("emp_name"));
        assertEquals("第五行应该是 David", "David", result.getRows().get(4).get("emp_name"));
        assertEquals("第六行应该是 Eve", "Eve", result.getRows().get(5).get("emp_name"));
    }

    /**
     * 测试数字排序
     * 
     * 目的：验证数字列排序的正确性
     * 预期：按 salary 升序排列
     */
    @Test
    public void testSort_NumericColumn() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        List<JQuickSortPhysicalNode.OrderByItem> orderByItems = new ArrayList<>();
        orderByItems.add(new JQuickSortPhysicalNode.OrderByItem("salary", true));
        JQuickSortPhysicalNode sortNode = new JQuickSortPhysicalNode(orderByItems, scanNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(sortNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 6 条数据", 6, result.size());
        assertEquals("第一行应该是 Frank（最低工资）", "Frank", result.getRows().get(0).get("emp_name"));
        assertEquals("工资应该是 7500", 7500.0, result.getRows().get(0).get("salary"));
        assertEquals("第二行应该是 Alice", "Alice", result.getRows().get(1).get("emp_name"));
        assertEquals("工资应该是 8000", 8000.0, result.getRows().get(1).get("salary"));
        assertEquals("第三行应该是 Eve", "Eve", result.getRows().get(2).get("emp_name"));
        assertEquals("工资应该是 9000", 9000.0, result.getRows().get(2).get("salary"));
        assertEquals("第四行应该是 Bob", "Bob", result.getRows().get(3).get("emp_name"));
        assertEquals("工资应该是 10000", 10000.0, result.getRows().get(3).get("salary"));
        assertEquals("第五行应该是 David", "David", result.getRows().get(4).get("emp_name"));
        assertEquals("工资应该是 11000", 11000.0, result.getRows().get(4).get("salary"));
        
        assertEquals("第六行应该是 Charlie（最高工资）", "Charlie", result.getRows().get(5).get("emp_name"));
        assertEquals("工资应该是 12000", 12000.0, result.getRows().get(5).get("salary"));
    }

    /**
     * 测试混合排序（升序和降序混合）
     * 
     * 目的：验证多个列不同排序方向的正确性
     * 预期：先按 dept_id 升序，再按 salary 降序
     */
    @Test
    public void testSort_MixedAscendingDescending() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        List<JQuickSortPhysicalNode.OrderByItem> orderByItems = new ArrayList<>();
        orderByItems.add(new JQuickSortPhysicalNode.OrderByItem("dept_id", true));
        orderByItems.add(new JQuickSortPhysicalNode.OrderByItem("salary", false));
        JQuickSortPhysicalNode sortNode = new JQuickSortPhysicalNode(orderByItems, scanNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(sortNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 6 条数据", 6, result.size());
        assertEquals("第一行应该是 Bob（dept_id=1, salary=10000）", "Bob", result.getRows().get(0).get("emp_name"));
        assertEquals("dept_id 应该为 1", 1L, result.getRows().get(0).get("dept_id"));
        
        assertEquals("第二行应该是 Alice（dept_id=1, salary=8000）", "Alice", result.getRows().get(1).get("emp_name"));
        assertEquals("dept_id 应该为 1", 1L, result.getRows().get(1).get("dept_id"));
        
        assertEquals("第三行应该是 Charlie（dept_id=2, salary=12000）", "Charlie", result.getRows().get(2).get("emp_name"));
        assertEquals("dept_id 应该为 2", 2L, result.getRows().get(2).get("dept_id"));
        
        assertEquals("第四行应该是 David（dept_id=2, salary=11000）", "David", result.getRows().get(3).get("emp_name"));
        assertEquals("dept_id 应该为 2", 2L, result.getRows().get(3).get("dept_id"));
        
        assertEquals("第五行应该是 Eve（dept_id=3）", "Eve", result.getRows().get(4).get("emp_name"));
        assertEquals("dept_id 应该为 3", 3L, result.getRows().get(4).get("dept_id"));
        
        assertEquals("第六行应该是 Frank（dept_id 为 null）", "Frank", result.getRows().get(5).get("emp_name"));
    }

    /**
     * 测试空结果集排序
     * 
     * 目的：验证对空结果集排序的处理
     * 预期：返回空结果集，不抛出异常
     */
    @Test
    public void testSort_EmptyResultSet() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        List<JQuickSortPhysicalNode.OrderByItem> orderByItems = new ArrayList<>();
        orderByItems.add(new JQuickSortPhysicalNode.OrderByItem("emp_id", true));
        JQuickSortPhysicalNode sortNode = new JQuickSortPhysicalNode(orderByItems, scanNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(sortNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertFalse("结果集不应为空", result.getRows().isEmpty());
    }

    /**
     * 测试单行数据排序
     * 
     * 目的：验证单行数据的排序
     * 预期：返回单行数据，不抛出异常
     */
    @Test
    public void testSort_SingleRow() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        List<JQuickSortPhysicalNode.OrderByItem> orderByItems = new ArrayList<>();
        orderByItems.add(new JQuickSortPhysicalNode.OrderByItem("emp_id", true));
        JQuickSortPhysicalNode sortNode = new JQuickSortPhysicalNode(orderByItems, scanNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(sortNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 6 条数据", 6, result.size());
        assertEquals("emp_id 应该为 1L", 1L, result.getRows().get(0).get("emp_id"));
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
