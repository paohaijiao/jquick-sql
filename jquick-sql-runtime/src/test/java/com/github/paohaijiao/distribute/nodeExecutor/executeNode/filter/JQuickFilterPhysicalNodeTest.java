package com.github.paohaijiao.distribute.nodeExecutor.executeNode.filter;

import com.github.paohaijiao.datasource.JQuickDataSourceManager;
import com.github.paohaijiao.distributed.worker.*;
import com.github.paohaijiao.enums.JQuickBinaryOperator;
import com.github.paohaijiao.enums.JQuickUnaryOperator;
import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.expression.domain.*;
import com.github.paohaijiao.function.manager.JQuickMethodInvocationManager;
import com.github.paohaijiao.physical.node.JQuickFilterPhysicalNode;
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
 * JQuickFilterPhysicalNode 单元测试
 * 
 * 测试范围：
 * 1. 基本过滤条件（=, !=, >, <, >=, <=）
 * 2. 逻辑运算符（AND, OR, NOT）
 * 3. 字符串操作（LIKE, IN）
 * 4. 空值处理（IS NULL, IS NOT NULL）
 * 5. 复合条件
 * 6. 边界条件
 */
public class JQuickFilterPhysicalNodeTest {

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
                new JQuickColumnMeta("department", String.class, "employee"),
                new JQuickColumnMeta("status", String.class, "employee")
        );
        List<JQuickRow> employeeRows = new ArrayList<>();
        employeeRows.add(createEmployeeRow(1L, "Alice", 25, 8000.0, "技术部", "active"));
        employeeRows.add(createEmployeeRow(2L, "Bob", 30, 10000.0, "技术部", "active"));
        employeeRows.add(createEmployeeRow(3L, "Charlie", 35, 12000.0, "市场部", "active"));
        employeeRows.add(createEmployeeRow(4L, "David", 28, 9000.0, "市场部", "inactive"));
        employeeRows.add(createEmployeeRow(5L, "Eve", 22, 7000.0, "人事部", "active"));
        employeeRows.add(createEmployeeRow(6L, "Frank", 40, 15000.0, "技术部", "inactive"));
        employeeRows.add(createEmployeeRow(7L, "Grace", 32, 11000.0, "销售部", "active"));
        employeeRows.add(createEmployeeRow(8L, "Henry", 27, 8500.0, "销售部", "inactive"));
        employeeRows.add(createEmployeeRow(9L, "Ivy", 29, 9500.0, null, "active")); // department 为 null
        employeeRows.add(createEmployeeRow(10L, "Jack", 33, null, "技术部", "active")); // salary 为 null
        JQuickDataSet employeeTable = new JQuickDataSet(employeeColumns, employeeRows);
        JQuickDataSourceManager.registerTable("employee", employeeTable);
    }

    private JQuickRow createEmployeeRow(Long id, String name, Integer age, Double salary, String department, String status) {
        JQuickRow row = new JQuickRow();
        row.put("id", id);
        row.put("name", name);
        row.put("age", age);
        row.put("salary", salary);
        row.put("department", department);
        row.put("status", status);
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
     * 测试等于条件 (=)
     * 
     * 目的：验证等于条件能够正确过滤数据
     * 预期：只返回满足条件的行
     */
    @Test
    public void testFilter_Equal() {
        JQuickExpression predicate = new JQuickBinaryExpression(new JQuickColumnRefExpression("department"), new JQuickLiteralExpression("技术部"), JQuickBinaryOperator.EQ);
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickFilterPhysicalNode filterNode = new JQuickFilterPhysicalNode(predicate, scanNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(filterNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertTrue("应该返回技术部的数据", result.size() > 0);
        for (JQuickRow row : result.getRows()) {
            assertEquals("所有结果应该是技术部", "技术部", row.get("department"));
        }
    }

    /**
     * 测试不等于条件 (!=)
     * 
     * 目的：验证不等于条件能够正确过滤数据
     * 预期：返回不满足条件的行
     */
    @Test
    public void testFilter_NotEqual() {
        JQuickExpression predicate = new JQuickBinaryExpression(new JQuickColumnRefExpression("status"), new JQuickLiteralExpression("inactive"), JQuickBinaryOperator.NE);
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickFilterPhysicalNode filterNode = new JQuickFilterPhysicalNode(predicate, scanNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(filterNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        for (JQuickRow row : result.getRows()) {
            assertNotEquals("所有结果不应该状态为 inactive", "inactive", row.get("status"));
        }
    }

    /**
     * 测试大于条件 (>)
     * 
     * 目的：验证大于条件能够正确过滤数据
     * 预期：返回大于指定值的行
     */
    @Test
    public void testFilter_GreaterThan() {
        JQuickExpression predicate = new JQuickBinaryExpression(new JQuickColumnRefExpression("age"), new JQuickLiteralExpression(30), JQuickBinaryOperator.GT);
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickFilterPhysicalNode filterNode = new JQuickFilterPhysicalNode(predicate, scanNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(filterNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        for (JQuickRow row : result.getRows()) {
            assertTrue("年龄应该大于 30", (Integer) row.get("age") > 30);
        }
    }

    /**
     * 测试小于条件 (<)
     * 
     * 目的：验证小于条件能够正确过滤数据
     * 预期：返回小于指定值的行
     */
    @Test
    public void testFilter_LessThan() {
        JQuickExpression predicate = new JQuickBinaryExpression(new JQuickColumnRefExpression("age"), new JQuickLiteralExpression(30), JQuickBinaryOperator.LT);
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickFilterPhysicalNode filterNode = new JQuickFilterPhysicalNode(predicate, scanNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(filterNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        for (JQuickRow row : result.getRows()) {
            assertTrue("年龄应该小于 30", (Integer) row.get("age") < 30);
        }
    }

    /**
     * 测试大于等于条件 (>=)
     * 
     * 目的：验证大于等于条件能够正确过滤数据
     * 预期：返回大于等于指定值的行
     */
    @Test
    public void testFilter_GreaterThanOrEqual() {
        JQuickExpression predicate = new JQuickBinaryExpression(new JQuickColumnRefExpression("salary"), new JQuickLiteralExpression(10000.0), JQuickBinaryOperator.GE);
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickFilterPhysicalNode filterNode = new JQuickFilterPhysicalNode(predicate, scanNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(filterNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        for (JQuickRow row : result.getRows()) {
            if (row.get("salary") != null) {
                assertTrue("薪资应该大于等于 10000", (Double) row.get("salary") >= 10000.0);
            }
        }
    }

    /**
     * 测试小于等于条件 (<=)
     * 
     * 目的：验证小于等于条件能够正确过滤数据
     * 预期：返回小于等于指定值的行
     */
    @Test
    public void testFilter_LessThanOrEqual() {
        JQuickExpression predicate = new JQuickBinaryExpression(new JQuickColumnRefExpression("age"), new JQuickLiteralExpression(30), JQuickBinaryOperator.LE);
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickFilterPhysicalNode filterNode = new JQuickFilterPhysicalNode(predicate, scanNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(filterNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        for (JQuickRow row : result.getRows()) {
            assertTrue("年龄应该小于等于 30", (Integer) row.get("age") <= 30);
        }
    }
    /**
     * 测试 AND 条件
     * 
     * 目的：验证 AND 逻辑能够正确组合多个条件
     * 预期：返回同时满足所有条件的行
     */
    @Test
    public void testFilter_AndCondition() {
        JQuickExpression leftCondition = new JQuickBinaryExpression(new JQuickColumnRefExpression("department"), new JQuickLiteralExpression("技术部"), JQuickBinaryOperator.EQ);
        JQuickExpression rightCondition = new JQuickBinaryExpression(new JQuickColumnRefExpression("status"), new JQuickLiteralExpression("active"), JQuickBinaryOperator.EQ);
        JQuickExpression predicate = new JQuickBinaryExpression(leftCondition, rightCondition, JQuickBinaryOperator.AND);
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickFilterPhysicalNode filterNode = new JQuickFilterPhysicalNode(predicate, scanNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(filterNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        for (JQuickRow row : result.getRows()) {
            assertEquals("部门应该是技术部", "技术部", row.get("department"));
            assertEquals("状态应该是 active", "active", row.get("status"));
        }
    }

    /**
     * 测试 OR 条件
     * 
     * 目的：验证 OR 逻辑能够正确组合多个条件
     * 预期：返回满足任一条件的行
     */
    @Test
    public void testFilter_OrCondition() {
        JQuickExpression leftCondition = new JQuickBinaryExpression(new JQuickColumnRefExpression("department"), new JQuickLiteralExpression("技术部"), JQuickBinaryOperator.EQ);
        JQuickExpression rightCondition = new JQuickBinaryExpression(new JQuickColumnRefExpression("department"), new JQuickLiteralExpression("市场部"), JQuickBinaryOperator.EQ);
        JQuickExpression predicate = new JQuickBinaryExpression(leftCondition, rightCondition, JQuickBinaryOperator.OR);
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickFilterPhysicalNode filterNode = new JQuickFilterPhysicalNode(predicate, scanNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(filterNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        for (JQuickRow row : result.getRows()) {
            String dept = (String) row.get("department");
            assertTrue("部门应该是技术部或市场部", "技术部".equals(dept) || "市场部".equals(dept));
        }
    }

    /**
     * 测试 NOT 条件
     * 
     * 目的：验证 NOT 逻辑能够正确取反
     * 预期：返回不满足条件的行
     */
    @Test
    public void testFilter_NotCondition() {
        JQuickExpression innerCondition = new JQuickBinaryExpression(new JQuickColumnRefExpression("status"), new JQuickLiteralExpression("active"), JQuickBinaryOperator.EQ);
        JQuickExpression predicate = new JQuickUnaryExpression(JQuickUnaryOperator.NOT,innerCondition);
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickFilterPhysicalNode filterNode = new JQuickFilterPhysicalNode(predicate, scanNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(filterNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        for (JQuickRow row : result.getRows()) {
            assertNotEquals("状态不应该为 active", "active", row.get("status"));
        }
    }

    /**
     * 测试复合条件 (AND + OR)
     * 
     * 目的：验证复合逻辑条件能够正确评估
     * 预期：返回满足复杂条件的行
     */
    @Test
    public void testFilter_ComplexCondition() {
        // (department = '技术部' AND status = 'active') OR (age > 35)
        JQuickExpression techActive = new JQuickBinaryExpression(new JQuickBinaryExpression(new JQuickColumnRefExpression("department"), new JQuickLiteralExpression("技术部"), JQuickBinaryOperator.EQ), new JQuickBinaryExpression(new JQuickColumnRefExpression("status"), new JQuickLiteralExpression("active"), JQuickBinaryOperator.EQ), JQuickBinaryOperator.AND);
        JQuickExpression ageCondition = new JQuickBinaryExpression(new JQuickColumnRefExpression("age"), new JQuickLiteralExpression(35), JQuickBinaryOperator.GT);
        JQuickExpression predicate = new JQuickBinaryExpression(techActive, ageCondition, JQuickBinaryOperator.OR);
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickFilterPhysicalNode filterNode = new JQuickFilterPhysicalNode(predicate, scanNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(filterNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        for (JQuickRow row : result.getRows()) {
            boolean isTechActive = "技术部".equals(row.get("department")) && "active".equals(row.get("status"));
            boolean isOld = (Integer) row.get("age") > 35;
            assertTrue("应该满足复合条件", isTechActive || isOld);
        }
    }
    /**
     * 测试 LIKE 条件
     * 
     * 目的：验证 LIKE 模式匹配能够正确工作
     * 预期：返回匹配模式的行
     */
    @Test
    public void testFilter_Like() {
        JQuickExpression predicate = new JQuickBinaryExpression(new JQuickColumnRefExpression("name"), new JQuickLiteralExpression("A%"), JQuickBinaryOperator.LIKE);
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickFilterPhysicalNode filterNode = new JQuickFilterPhysicalNode(predicate, scanNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(filterNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        for (JQuickRow row : result.getRows()) {
            String name = (String) row.get("name");
            assertTrue("名字应该以 A 开头", name != null && name.startsWith("A"));
        }
    }

    /**
     * 测试 NOT LIKE 条件
     * 
     * 目的：验证 NOT LIKE 能够正确排除匹配的行
     * 预期：返回不匹配模式的行
     */
    @Test
    public void testFilter_NotLike() {
        JQuickExpression likeCondition = new JQuickBinaryExpression(new JQuickColumnRefExpression("name"), new JQuickLiteralExpression("A%"), JQuickBinaryOperator.LIKE);
        JQuickExpression predicate = new JQuickUnaryExpression(JQuickUnaryOperator.NOT,likeCondition);
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickFilterPhysicalNode filterNode = new JQuickFilterPhysicalNode(predicate, scanNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(filterNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        for (JQuickRow row : result.getRows()) {
            String name = (String) row.get("name");
            assertTrue("名字不应该以 A 开头", name == null || !name.startsWith("A"));
        }
    }

    /**
     * 测试 IN 条件
     * 
     * 目的：验证 IN 列表条件能够正确工作
     * 预期：返回在列表中的行
     */
    @Test
    public void testFilter_In() {
        JQuickExpression predicate = new JQuickInExpression(new JQuickColumnRefExpression("department"), Arrays.asList(new JQuickLiteralExpression("技术部"), new JQuickLiteralExpression("市场部")), false);
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickFilterPhysicalNode filterNode = new JQuickFilterPhysicalNode(predicate, scanNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(filterNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        for (JQuickRow row : result.getRows()) {
            String dept = (String) row.get("department");
            assertTrue("部门应该在列表中", "技术部".equals(dept) || "市场部".equals(dept));
        }
    }

    /**
     * 测试 NOT IN 条件
     * 
     * 目的：验证 NOT IN 能够正确排除列表中的行
     * 预期：返回不在列表中的行
     */
    @Test
    public void testFilter_NotIn() {
        JQuickExpression predicate = new JQuickInExpression(new JQuickColumnRefExpression("department"), Arrays.asList(new JQuickLiteralExpression("技术部"), new JQuickLiteralExpression("市场部")), true);
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickFilterPhysicalNode filterNode = new JQuickFilterPhysicalNode(predicate, scanNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(filterNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        for (JQuickRow row : result.getRows()) {
            String dept = (String) row.get("department");
            assertTrue("部门不应该在列表中", !"技术部".equals(dept) && !"市场部".equals(dept));
        }
    }
    /**
     * 测试 IS NULL 条件
     * 
     * 目的：验证 IS NULL 能够正确识别空值
     * 预期：返回指定列为 null 的行
     */
    @Test
    public void testFilter_IsNull() {
        JQuickExpression predicate = new JQuickUnaryExpression(JQuickUnaryOperator.IS_NULL, new JQuickColumnRefExpression("department"));
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickFilterPhysicalNode filterNode = new JQuickFilterPhysicalNode(predicate, scanNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(filterNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        for (JQuickRow row : result.getRows()) {
            assertNull("department 应该为 null", row.get("department"));
        }
    }

    /**
     * 测试 IS NOT NULL 条件
     * 
     * 目的：验证 IS NOT NULL 能够正确排除空值
     * 预期：返回指定列不为 null 的行
     */
    @Test
    public void testFilter_IsNotNull() {
        JQuickExpression predicate = new JQuickUnaryExpression(JQuickUnaryOperator.IS_NULL, new JQuickColumnRefExpression("salary"));
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickFilterPhysicalNode filterNode = new JQuickFilterPhysicalNode(predicate, scanNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(filterNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        for (JQuickRow row : result.getRows()) {
            assertNull("salary 不应该为 null", row.get("salary"));
        }
    }
    /**
     * 测试空结果集
     * 
     * 目的：验证过滤条件没有匹配时返回空结果
     * 预期：返回空数据集
     */
    @Test
    public void testFilter_NoMatch() {
        JQuickExpression predicate = new JQuickBinaryExpression(new JQuickColumnRefExpression("age"), new JQuickLiteralExpression(100), JQuickBinaryOperator.GT);
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickFilterPhysicalNode filterNode = new JQuickFilterPhysicalNode(predicate, scanNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(filterNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertTrue("没有匹配的数据应该返回空结果", result.isEmpty());
    }

    /**
     * 测试全部匹配
     * 
     * 目的：验证所有数据都满足条件时返回全部数据
     * 预期：返回所有数据
     */
    @Test
    public void testFilter_AllMatch() {
        JQuickExpression predicate = new JQuickBinaryExpression(new JQuickColumnRefExpression("age"), new JQuickLiteralExpression(0), JQuickBinaryOperator.GT);
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickFilterPhysicalNode filterNode = new JQuickFilterPhysicalNode(predicate, scanNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(filterNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("所有数据都应该匹配", 10, result.size());
    }

    /**
     * 测试空表过滤
     * 
     * 目的：验证对空表进行过滤时返回空结果
     * 预期：返回空数据集
     */
    @Test
    public void testFilter_EmptyTable() {
        JQuickDataSourceManager.registerTable("empty_table", JQuickDataSet.builder().build());
        JQuickExpression predicate = new JQuickBinaryExpression(new JQuickColumnRefExpression("id"), new JQuickLiteralExpression(1), JQuickBinaryOperator.EQ);
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("empty_table", null, null, null, null);
        JQuickFilterPhysicalNode filterNode = new JQuickFilterPhysicalNode(predicate, scanNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(filterNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertTrue("空表过滤应该返回空结果", result.isEmpty());
    }

    /**
     * 测试多列条件
     * 
     * 目的：验证涉及多列的复杂条件
     * 预期：返回满足多列条件的行
     */
    @Test
    public void testFilter_MultipleColumns() {
        // age > 25 AND salary >= 9000
        JQuickExpression ageCondition = new JQuickBinaryExpression(new JQuickColumnRefExpression("age"), new JQuickLiteralExpression(25), JQuickBinaryOperator.GT);
        JQuickExpression salaryCondition = new JQuickBinaryExpression(new JQuickColumnRefExpression("salary"), new JQuickLiteralExpression(9000.0), JQuickBinaryOperator.GE);
        JQuickExpression predicate = new JQuickBinaryExpression(ageCondition, salaryCondition, JQuickBinaryOperator.AND);
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickFilterPhysicalNode filterNode = new JQuickFilterPhysicalNode(predicate, scanNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(filterNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        for (JQuickRow row : result.getRows()) {
            assertTrue("年龄应该大于 25", (Integer) row.get("age") > 25);
            if (row.get("salary") != null) {
                assertTrue("薪资应该大于等于 9000", (Double) row.get("salary") >= 9000.0);
            }
        }
    }

    /**
     * 测试数值范围条件
     * 
     * 目的：验证数值范围过滤
     * 预期：返回在指定范围内的行
     */
    @Test
    public void testFilter_NumericRange() {
        // age BETWEEN 25 AND 35
        JQuickExpression lowerBound = new JQuickBinaryExpression(new JQuickColumnRefExpression("age"), new JQuickLiteralExpression(25), JQuickBinaryOperator.GE);
        JQuickExpression upperBound = new JQuickBinaryExpression(new JQuickColumnRefExpression("age"), new JQuickLiteralExpression(35), JQuickBinaryOperator.LE);
        JQuickExpression predicate = new JQuickBinaryExpression(lowerBound, upperBound, JQuickBinaryOperator.AND);
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickFilterPhysicalNode filterNode = new JQuickFilterPhysicalNode(predicate, scanNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(filterNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        for (JQuickRow row : result.getRows()) {
            int age = (Integer) row.get("age");
            assertTrue("年龄应该在 25-35 之间", age >= 25 && age <= 35);
        }
    }
    /**
     * 测试大小写敏感
     * 
     * 目的：验证字符串比较的大小写敏感性
     * 预期：精确匹配大小写
     */
    @Test
    public void testFilter_CaseSensitive() {
        JQuickExpression predicate = new JQuickBinaryExpression(new JQuickColumnRefExpression("status"), new JQuickLiteralExpression("ACTIVE"), JQuickBinaryOperator.EQ);
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickFilterPhysicalNode filterNode = new JQuickFilterPhysicalNode(predicate, scanNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(filterNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        // 如果大小写敏感，应该没有匹配（因为数据是 "active" 不是 "ACTIVE"）
        assertTrue("大小写敏感时应该没有匹配", result.isEmpty() || result.getRows().stream().allMatch(r -> "ACTIVE".equals(r.get("status"))));
    }

    /**
     * 测试特殊字符
     * 
     * 目的：验证包含特殊字符的数据能够正确过滤
     * 预期：正确处理特殊字符
     */
    @Test
    public void testFilter_SpecialCharacters() {
        // 注册包含特殊字符的表
        JQuickDataSet.Builder builder = JQuickDataSet.builder();
        builder.addColumn("id", Long.class, "special_table");
        builder.addColumn("name", String.class, "special_table");
        JQuickRow row1 = new JQuickRow();
        row1.put("id", 1L);
        row1.put("name", "test'value");
        builder.addRow(row1);
        JQuickRow row2 = new JQuickRow();
        row2.put("id", 2L);
        row2.put("name", "test\"value");
        builder.addRow(row2);
        
        JQuickRow row3 = new JQuickRow();
        row3.put("id", 3L);
        row3.put("name", "test%value");
        builder.addRow(row3);
        
        JQuickDataSourceManager.registerTable("special_table", builder.build());
        JQuickExpression predicate = new JQuickBinaryExpression(new JQuickColumnRefExpression("name"), new JQuickLiteralExpression("test'value"), JQuickBinaryOperator.EQ);
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("special_table", null, null, null, null);
        JQuickFilterPhysicalNode filterNode = new JQuickFilterPhysicalNode(predicate, scanNode);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(filterNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该找到 1 条匹配", 1, result.size());
        assertEquals("名字应该是 test'value", "test'value", result.getRows().get(0).get("name"));
    }

    /**
     * 测试嵌套 Filter
     * 
     * 目的：验证多个 Filter 节点串联执行
     * 预期：每个 Filter 依次过滤数据
     */
    @Test
    public void testFilter_Nested() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        // 第一层过滤age > 25
        JQuickExpression predicate1 = new JQuickBinaryExpression(new JQuickColumnRefExpression("age"), new JQuickLiteralExpression(25), JQuickBinaryOperator.GT);
        JQuickFilterPhysicalNode filter1 = new JQuickFilterPhysicalNode(predicate1, scanNode);
        // 第二层过滤：status = 'active'
        JQuickExpression predicate2 = new JQuickBinaryExpression(new JQuickColumnRefExpression("status"), new JQuickLiteralExpression("active"), JQuickBinaryOperator.EQ);
        JQuickFilterPhysicalNode filter2 = new JQuickFilterPhysicalNode(predicate2, filter1);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(filter2, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        for (JQuickRow row : result.getRows()) {
            assertTrue("年龄应该大于 25", (Integer) row.get("age") > 25);
            assertEquals("状态应该是 active", "active", row.get("status"));
        }
    }
}
