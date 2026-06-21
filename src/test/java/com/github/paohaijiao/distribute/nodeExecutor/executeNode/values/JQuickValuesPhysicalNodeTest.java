package com.github.paohaijiao.distribute.nodeExecutor.executeNode.values;

import com.github.paohaijiao.datasource.JQuickDataSourceManager;
import com.github.paohaijiao.distributed.worker.*;
import com.github.paohaijiao.function.manager.JQuickMethodInvocationManager;
import com.github.paohaijiao.physical.node.JQuickValuesPhysicalNode;
import com.github.paohaijiao.proto.JQuickExecuteTaskRequest;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

/**
 * JQuickValuesPhysicalNode 测试
 * 
 * 测试范围：
 * 1. 基本 VALUES 操作
 * 2. 多列 VALUES
 * 3. 不同数据类型
 * 4. 空值处理
 * 5. 单行数据
 * 6. 大量数据
 * 7. 与其他节点组合
 */
public class JQuickValuesPhysicalNodeTest {

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
    }

    @After
    public void tearDown() {
        JQuickDataSourceManager.clearAll();
        if (partitionManager != null) {
            partitionManager.shutdown();
        }
    }

    /**
     * 测试基本 VALUES 操作
     * 
     * 场景：创建一个简单的 VALUES 表
     * SQL: SELECT * FROM (VALUES (1, 'Alice'), (2, 'Bob'), (3, 'Charlie')) AS t(id, name)
     * 预期：返回 3 条数据
     */
    @Test
    public void testBasicValues() {
        List<List<Object>> rows = new ArrayList<>();
        rows.add(Arrays.asList(1, "Alice"));
        rows.add(Arrays.asList(2, "Bob"));
        rows.add(Arrays.asList(3, "Charlie"));
        List<String> columnNames = Arrays.asList("id", "name");
        List<Class<?>> columnTypes = Arrays.asList(Long.class, String.class);
        JQuickValuesPhysicalNode valuesNode = new JQuickValuesPhysicalNode(rows, columnNames, columnTypes);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(valuesNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 3 条数据", 3, result.size());
        assertEquals("应该有 2 列", 2, result.getColumns().size());
        assertEquals("第一列应该是 id", "id", result.getColumns().get(0).getName());
        assertEquals("第二列应该是 name", "name", result.getColumns().get(1).getName());
        JQuickRow row0 = result.getRows().get(0);
        assertEquals("第一行 id 应该为 1", 1, row0.get("id"));
        assertEquals("第一行 name 应该为 Alice", "Alice", row0.get("name"));
        
        JQuickRow row1 = result.getRows().get(1);
        assertEquals("第二行 id 应该为 2", 2, row1.get("id"));
        assertEquals("第二行 name 应该为 Bob", "Bob", row1.get("name"));
        
        JQuickRow row2 = result.getRows().get(2);
        assertEquals("第三行 id 应该为 3", 3, row2.get("id"));
        assertEquals("第三行 name 应该为 Charlie", "Charlie", row2.get("name"));
    }

    /**
     * 测试多列 VALUES
     * 
     * 场景：创建一个包含多列的 VALUES 表
     * SQL: SELECT * FROM (VALUES (1, 'Alice', 25, 8000.0), (2, 'Bob', 30, 10000.0)) AS t(id, name, age, salary)
     * 预期：返回 2 条数据，每条 4 列
     */
    @Test
    public void testMultipleColumns() {
        // 创建 VALUES 数据
        List<List<Object>> rows = new ArrayList<>();
        rows.add(Arrays.asList(1, "Alice", 25, 8000.0));
        rows.add(Arrays.asList(2, "Bob", 30, 10000.0));
        List<String> columnNames = Arrays.asList("id", "name", "age", "salary");
        List<Class<?>> columnTypes = Arrays.asList(Long.class, String.class, Integer.class, Double.class);
        JQuickValuesPhysicalNode valuesNode = new JQuickValuesPhysicalNode(rows, columnNames, columnTypes);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(valuesNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 2 条数据", 2, result.size());
        assertEquals("应该有 4 列", 4, result.getColumns().size());
        JQuickRow row0 = result.getRows().get(0);
        assertEquals("第一行 id 应该为 1", 1, row0.get("id"));
        assertEquals("第一行 name 应该为 Alice", "Alice", row0.get("name"));
        assertEquals("第一行 age 应该为 25", 25, row0.get("age"));
        assertEquals("第一行 salary 应该为 8000.0", 8000.0, row0.get("salary"));
        JQuickRow row1 = result.getRows().get(1);
        assertEquals("第二行 id 应该为 2", 2, row1.get("id"));
        assertEquals("第二行 name 应该为 Bob", "Bob", row1.get("name"));
        assertEquals("第二行 age 应该为 30", 30, row1.get("age"));
        assertEquals("第二行 salary 应该为 10000.0", 10000.0, row1.get("salary"));
    }

    /**
     * 测试不同数据类型
     * 
     * 场景：创建包含多种数据类型的 VALUES 表
     * SQL: SELECT * FROM (VALUES (1, 'test', 3.14, true, '2024-01-01')) AS t(id, name, price, active, date)
     * 预期：返回 1 条数据，包含不同类型
     */
    @Test
    public void testDifferentDataTypes() {
        List<List<Object>> rows = new ArrayList<>();
        rows.add(Arrays.asList(1L, "test", 3.14, true, "2024-01-01"));
        List<String> columnNames = Arrays.asList("id", "name", "price", "active", "date");
        List<Class<?>> columnTypes = Arrays.asList(Long.class, String.class, Double.class, Boolean.class, String.class);
        JQuickValuesPhysicalNode valuesNode = new JQuickValuesPhysicalNode(rows, columnNames, columnTypes);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(valuesNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 1 条数据", 1, result.size());
        JQuickRow row = result.getRows().get(0);
        assertTrue("id 应该是 Long 类型", row.get("id") instanceof Long);
        assertTrue("name 应该是 String 类型", row.get("name") instanceof String);
        assertTrue("price 应该是 Double 类型", row.get("price") instanceof Double);
        assertTrue("active 应该是 Boolean 类型", row.get("active") instanceof Boolean);
        assertTrue("date 应该是 String 类型", row.get("date") instanceof String);
        assertEquals("id 应该为 1", 1L, row.get("id"));
        assertEquals("name 应该为 test", "test", row.get("name"));
        assertEquals("price 应该为 3.14", 3.14, row.get("price"));
        assertEquals("active 应该为 true", true, row.get("active"));
        assertEquals("date 应该为 2024-01-01", "2024-01-01", row.get("date"));
    }

    /**
     * 测试空值处理
     * 
     * 场景：创建包含 NULL 值的 VALUES 表
     * SQL: SELECT * FROM (VALUES (1, 'Alice', NULL), (2, NULL, 8000.0), (NULL, 'Charlie', 9000.0)) AS t(id, name, salary)
     * 预期：返回 3 条数据，包含 NULL 值
     */
    @Test
    public void testNullValues() {
        List<List<Object>> rows = new ArrayList<>();
        rows.add(Arrays.asList(1, "Alice", null));
        rows.add(Arrays.asList(2, null, 8000.0));
        rows.add(Arrays.asList(null, "Charlie", 9000.0));
        List<String> columnNames = Arrays.asList("id", "name", "salary");
        List<Class<?>> columnTypes = Arrays.asList(Long.class, String.class, Double.class);
        JQuickValuesPhysicalNode valuesNode = new JQuickValuesPhysicalNode(rows, columnNames, columnTypes);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(valuesNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 3 条数据", 3, result.size());
        JQuickRow row0 = result.getRows().get(0);
        assertEquals("第一行 id 应该为 1", 1, row0.get("id"));
        assertEquals("第一行 name 应该为 Alice", "Alice", row0.get("name"));
        assertNull("第一行 salary 应该为 null", row0.get("salary"));
        
        // 验证第二行（name 为 null）
        JQuickRow row1 = result.getRows().get(1);
        assertEquals("第二行 id 应该为 2", 2, row1.get("id"));
        assertNull("第二行 name 应该为 null", row1.get("name"));
        assertEquals("第二行 salary 应该为 8000.0", 8000.0, row1.get("salary"));
        
        // 验证第三行（id 为 null）
        JQuickRow row2 = result.getRows().get(2);
        assertNull("第三行 id 应该为 null", row2.get("id"));
        assertEquals("第三行 name 应该为 Charlie", "Charlie", row2.get("name"));
        assertEquals("第三行 salary 应该为 9000.0", 9000.0, row2.get("salary"));
    }

    /**
     * 测试单行数据
     * 
     * 场景：创建只包含一行数据的 VALUES 表
     * SQL: SELECT * FROM (VALUES (1, 'Alice', 8000.0)) AS t(id, name, salary)
     * 预期：返回 1 条数据
     */
    @Test
    public void testSingleRow() {
        List<List<Object>> rows = new ArrayList<>();
        rows.add(Arrays.asList(1, "Alice", 8000.0));
        List<String> columnNames = Arrays.asList("id", "name", "salary");
        List<Class<?>> columnTypes = Arrays.asList(Long.class, String.class, Double.class);
        JQuickValuesPhysicalNode valuesNode = new JQuickValuesPhysicalNode(rows, columnNames, columnTypes);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(valuesNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 1 条数据", 1, result.size());
        JQuickRow row = result.getRows().get(0);
        assertEquals("id 应该为 1", 1, row.get("id"));
        assertEquals("name 应该为 Alice", "Alice", row.get("name"));
        assertEquals("salary 应该为 8000.0", 8000.0, row.get("salary"));
    }

    /**
     * 测试空 VALUES
     * 
     * 场景：创建不包含任何数据的 VALUES 表
     * SQL: SELECT * FROM (VALUES) AS t(id, name)
     * 预期：返回 0 条数据
     */
    @Test
    public void testEmptyValues() {
        List<List<Object>> rows = new ArrayList<>();
        List<String> columnNames = Arrays.asList("id", "name");
        List<Class<?>> columnTypes = Arrays.asList(Long.class, String.class);
        JQuickValuesPhysicalNode valuesNode = new JQuickValuesPhysicalNode(rows, columnNames, columnTypes);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(valuesNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 0 条数据", 0, result.size());
        assertEquals("应该有 2 列", 2, result.getColumns().size());
    }

    /**
     * 测试大量数据
     * 
     * 场景：创建包含大量数据的 VALUES 表
     * 预期：返回 1000 条数据
     */
    @Test
    public void testLargeDataSet() {
        List<List<Object>> rows = new ArrayList<>();
        for (int i = 1; i <= 1000; i++) {
            rows.add(Arrays.asList(i, "User" + i, 5000.0 + i * 10));
        }
        List<String> columnNames = Arrays.asList("id", "name", "salary");
        List<Class<?>> columnTypes = Arrays.asList(Integer.class, String.class, Double.class);
        JQuickValuesPhysicalNode valuesNode = new JQuickValuesPhysicalNode(rows, columnNames, columnTypes);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(valuesNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 1000 条数据", 1000, result.size());
        JQuickRow firstRow = result.getRows().get(0);
        assertEquals("第一条 id 应该为 1", 1, firstRow.get("id"));
        assertEquals("第一条 name 应该为 User1", "User1", firstRow.get("name"));
        
        JQuickRow lastRow = result.getRows().get(999);
        assertEquals("最后一条 id 应该为 1000", 1000, lastRow.get("id"));
        assertEquals("最后一条 name 应该为 User1000", "User1000", lastRow.get("name"));
    }

    /**
     * 测试单列 VALUES
     * 
     * 场景：创建只包含一列的 VALUES 表
     * SQL: SELECT * FROM (VALUES (1), (2), (3)) AS t(id)
     * 预期：返回 3 条数据，每条 1 列
     */
    @Test
    public void testSingleColumn() {
        List<List<Object>> rows = new ArrayList<>();
        rows.add(Arrays.asList(1));
        rows.add(Arrays.asList(2));
        rows.add(Arrays.asList(3));
        List<String> columnNames = Arrays.asList("id");
        List<Class<?>> columnTypes = Arrays.asList(Integer.class);
        JQuickValuesPhysicalNode valuesNode = new JQuickValuesPhysicalNode(rows, columnNames, columnTypes);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(valuesNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 3 条数据", 3, result.size());
        assertEquals("应该有 1 列", 1, result.getColumns().size());
        assertEquals("第一行 id 应该为 1", 1, result.getRows().get(0).get("id"));
        assertEquals("第二行 id 应该为 2", 2, result.getRows().get(1).get("id"));
        assertEquals("第三行 id 应该为 3", 3, result.getRows().get(2).get("id"));
    }

    /**
     * 测试字符串类型数据
     * 
     * 场景：创建只包含字符串的 VALUES 表
     * SQL: SELECT * FROM (VALUES ('Alice'), ('Bob'), ('Charlie')) AS t(name)
     * 预期：返回 3 条字符串数据
     */
    @Test
    public void testStringValues() {
        // 创建 VALUES 数据
        List<List<Object>> rows = new ArrayList<>();
        rows.add(Arrays.asList("Alice"));
        rows.add(Arrays.asList("Bob"));
        rows.add(Arrays.asList("Charlie"));
        List<String> columnNames = Arrays.asList("name");
        List<Class<?>> columnTypes = Arrays.asList(String.class);
        JQuickValuesPhysicalNode valuesNode = new JQuickValuesPhysicalNode(rows, columnNames, columnTypes);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(valuesNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 3 条数据", 3, result.size());
        assertEquals("第一行 name 应该为 Alice", "Alice", result.getRows().get(0).get("name"));
        assertEquals("第二行 name 应该为 Bob", "Bob", result.getRows().get(1).get("name"));
        assertEquals("第三行 name 应该为 Charlie", "Charlie", result.getRows().get(2).get("name"));
    }

    /**
     * 测试数值类型数据
     * 
     * 场景：创建包含整数和浮点数的 VALUES 表
     * SQL: SELECT * FROM (VALUES (1, 100), (2, 200), (3, 300)) AS t(id, value)
     * 预期：返回 3 条数值数据
     */
    @Test
    public void testNumericValues() {
        List<List<Object>> rows = new ArrayList<>();
        rows.add(Arrays.asList(1, 100));
        rows.add(Arrays.asList(2, 200));
        rows.add(Arrays.asList(3, 300));
        List<String> columnNames = Arrays.asList("id", "value");
        List<Class<?>> columnTypes = Arrays.asList(Integer.class, Integer.class);
        JQuickValuesPhysicalNode valuesNode = new JQuickValuesPhysicalNode(rows, columnNames, columnTypes);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(valuesNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 3 条数据", 3, result.size());
        assertEquals("第一行 value 应该为 100", 100, result.getRows().get(0).get("value"));
        assertEquals("第二行 value 应该为 200", 200, result.getRows().get(1).get("value"));
        assertEquals("第三行 value 应该为 300", 300, result.getRows().get(2).get("value"));
    }

    /**
     * 测试布尔类型数据
     * 
     * 场景：创建包含布尔值的 VALUES 表
     * SQL: SELECT * FROM (VALUES (true), (false), (true)) AS t(active)
     * 预期：返回 3 条布尔数据
     */
    @Test
    public void testBooleanValues() {
        // 创建 VALUES 数据
        List<List<Object>> rows = new ArrayList<>();
        rows.add(Arrays.asList(true));
        rows.add(Arrays.asList(false));
        rows.add(Arrays.asList(true));
        
        List<String> columnNames = Arrays.asList("active");
        List<Class<?>> columnTypes = Arrays.asList(Boolean.class);
        JQuickValuesPhysicalNode valuesNode = new JQuickValuesPhysicalNode(rows, columnNames, columnTypes);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(valuesNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 3 条数据", 3, result.size());
        assertEquals("第一行 active 应该为 true", true, result.getRows().get(0).get("active"));
        assertEquals("第二行 active 应该为 false", false, result.getRows().get(1).get("active"));
        assertEquals("第三行 active 应该为 true", true, result.getRows().get(2).get("active"));
    }

    /**
     * 测试列数不匹配（值数量少于列数）
     * 
     * 场景：VALUES 数据的值数量少于列定义
     * 预期：缺失的列值为 null
     */
    @Test
    public void testColumnCountMismatchLess() {
        List<List<Object>> rows = new ArrayList<>();
        rows.add(Arrays.asList(1, "Alice")); // 只有 2 个值，但定义了 3 列
        List<String> columnNames = Arrays.asList("id", "name", "salary");
        List<Class<?>> columnTypes = Arrays.asList(Long.class, String.class, Double.class);
        JQuickValuesPhysicalNode valuesNode = new JQuickValuesPhysicalNode(rows, columnNames, columnTypes);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(valuesNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 1 条数据", 1, result.size());
        JQuickRow row = result.getRows().get(0);
        assertEquals("id 应该为 1", 1, row.get("id"));
        assertEquals("name 应该为 Alice", "Alice", row.get("name"));
        assertNull("salary 应该为 null（缺失值）", row.get("salary"));
    }

    /**
     * 测试克隆功能
     * 
     * 场景：克隆 VALUES 节点
     * 预期：克隆后的节点与原节点数据一致
     */
    @Test
    public void testClone() {
        List<List<Object>> rows = new ArrayList<>();
        rows.add(Arrays.asList(1, "Alice", 8000.0));
        rows.add(Arrays.asList(2, "Bob", 10000.0));
        List<String> columnNames = Arrays.asList("id", "name", "salary");
        List<Class<?>> columnTypes = Arrays.asList(Long.class, String.class, Double.class);
        JQuickValuesPhysicalNode valuesNode = new JQuickValuesPhysicalNode(rows, columnNames, columnTypes);
        JQuickValuesPhysicalNode clonedNode = (JQuickValuesPhysicalNode) valuesNode.clone();
        assertNotNull("克隆节点不应为 null", clonedNode);
        assertEquals("克隆节点的行数应该相同", valuesNode.getRows().size(), clonedNode.getRows().size());
        assertEquals("克隆节点的列名应该相同", valuesNode.getColumnNames(), clonedNode.getColumnNames());
        assertEquals("克隆节点的列类型应该相同", valuesNode.getColumnTypes(), clonedNode.getColumnTypes());
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(clonedNode, context);
        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 2 条数据", 2, result.size());
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
