package com.github.paohaijiao.distribute.nodeExecutor.join;

import com.github.paohaijiao.datasource.JQuickDataSourceManager;
import com.github.paohaijiao.distributed.worker.*;
import com.github.paohaijiao.enums.JQuickJoinType;
import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.expression.domain.JQuickBinaryExpression;
import com.github.paohaijiao.expression.domain.JQuickColumnRefExpression;
import com.github.paohaijiao.expression.domain.JQuickLiteralExpression;
import com.github.paohaijiao.function.manager.JQuickMethodInvocationManager;
import com.github.paohaijiao.physical.node.JQuickNestedLoopJoinPhysicalNode;
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
 * JQuickNestedLoopJoinPhysicalNode 单元测试
 * 
 * 测试范围：
 * 1. INNER JOIN
 * 2. LEFT JOIN
 * 3. RIGHT JOIN
 * 4. FULL JOIN
 * 5. CROSS JOIN
 * 6. 带条件的 JOIN
 * 7. 边界条件
 */
public class JQuickNestedLoopJoinPhysicalNodeTest {

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
                new JQuickColumnMeta("salary", Double.class, "employee")
        );
        List<JQuickRow> employeeRows = new ArrayList<>();
        employeeRows.add(createEmployeeRow(1L, "Alice", 1L, 8000.0));
        employeeRows.add(createEmployeeRow(2L, "Bob", 1L, 10000.0));
        employeeRows.add(createEmployeeRow(3L, "Charlie", 2L, 12000.0));
        employeeRows.add(createEmployeeRow(4L, "David", 2L, 9000.0));
        employeeRows.add(createEmployeeRow(5L, "Eve", 3L, 7000.0));
        employeeRows.add(createEmployeeRow(6L, "Frank", null, 15000.0)); // 没有部门的员工
        JQuickDataSet employeeTable = new JQuickDataSet(employeeColumns, employeeRows);
        JQuickDataSourceManager.registerTable("employee", employeeTable);
        // department 表
        List<JQuickColumnMeta> deptColumns = Arrays.asList(
                new JQuickColumnMeta("dept_id", Long.class, "department"),
                new JQuickColumnMeta("dept_name", String.class, "department"),
                new JQuickColumnMeta("location", String.class, "department")
        );
        List<JQuickRow> deptRows = new ArrayList<>();
        deptRows.add(createDeptRow(1L, "技术部", "北京"));
        deptRows.add(createDeptRow(2L, "市场部", "上海"));
        deptRows.add(createDeptRow(3L, "人事部", "广州"));
        deptRows.add(createDeptRow(4L, "财务部", "深圳")); // 没有员工的部门
        JQuickDataSet deptTable = new JQuickDataSet(deptColumns, deptRows);
        JQuickDataSourceManager.registerTable("department", deptTable);
        // project 表
        List<JQuickColumnMeta> projectColumns = Arrays.asList(
                new JQuickColumnMeta("project_id", Long.class, "project"),
                new JQuickColumnMeta("project_name", String.class, "project"),
                new JQuickColumnMeta("leader_id", Long.class, "project")
        );
        List<JQuickRow> projectRows = new ArrayList<>();
        projectRows.add(createProjectRow(101L, "项目A", 1L));
        projectRows.add(createProjectRow(102L, "项目B", 2L));
        projectRows.add(createProjectRow(103L, "项目C", 3L));
        projectRows.add(createProjectRow(104L, "项目D", null)); // 没有负责人的项目
        JQuickDataSet projectTable = new JQuickDataSet(projectColumns, projectRows);
        JQuickDataSourceManager.registerTable("project", projectTable);
    }

    private JQuickRow createEmployeeRow(Long empId, String empName, Long deptId, Double salary) {
        JQuickRow row = new JQuickRow();
        row.put("emp_id", empId);
        row.put("emp_name", empName);
        row.put("dept_id", deptId);
        row.put("salary", salary);
        return row;
    }

    private JQuickRow createDeptRow(Long deptId, String deptName, String location) {
        JQuickRow row = new JQuickRow();
        row.put("dept_id", deptId);
        row.put("dept_name", deptName);
        row.put("location", location);
        return row;
    }

    private JQuickRow createProjectRow(Long projectId, String projectName, Long leaderId) {
        JQuickRow row = new JQuickRow();
        row.put("project_id", projectId);
        row.put("project_name", projectName);
        row.put("leader_id", leaderId);
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
     * 测试 INNER JOIN - 基本连接
     * 
     * 目的：验证 Nested Loop INNER JOIN 能够正确连接两个表
     * 预期：只返回匹配的行
     */
    @Test
    public void testInnerJoin_Basic() {
        JQuickTableScanPhysicalNode leftScan = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickTableScanPhysicalNode rightScan = new JQuickTableScanPhysicalNode("department", null, null, null, null);
        // JOIN 条件：employee.dept_id = department.dept_id
        // 使用前缀区分左右表的列
        JQuickExpression condition = new JQuickBinaryExpression(new JQuickColumnRefExpression("left.dept_id"), new JQuickColumnRefExpression("right.dept_id"), com.github.paohaijiao.enums.JQuickBinaryOperator.EQ);
        JQuickNestedLoopJoinPhysicalNode joinNode = new JQuickNestedLoopJoinPhysicalNode(JQuickJoinType.INNER, leftScan, rightScan, condition);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(joinNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        // employee 有 6 条，其中 Frank 的 dept_id 为 null
        // department 有 4 条，其中财务部没有员工
        // INNER JOIN 应该返回 5 条（排除 Frank 和财务部）
        assertEquals("INNER JOIN 应该返回 5 条数据", 5, result.size());
    }

    /**
     * 测试 INNER JOIN - 带额外过滤条件
     * 
     * 目的：验证 Nested Loop INNER JOIN 带额外过滤条件
     * 预期：返回满足连接条件和过滤条件的数据
     */
    @Test
    public void testInnerJoin_WithCondition() {
        JQuickTableScanPhysicalNode leftScan = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickTableScanPhysicalNode rightScan = new JQuickTableScanPhysicalNode("department", null, null, null, null);
        // JOIN 条件：employee.dept_id = department.dept_id AND salary > 8000
        JQuickExpression eqCondition = new JQuickBinaryExpression(new JQuickColumnRefExpression("left.dept_id"), new JQuickColumnRefExpression("right.dept_id"), com.github.paohaijiao.enums.JQuickBinaryOperator.EQ);
        JQuickExpression salaryCondition = new JQuickBinaryExpression(new JQuickColumnRefExpression("salary"), new JQuickLiteralExpression(8000.0), com.github.paohaijiao.enums.JQuickBinaryOperator.GT);
        JQuickExpression condition = new JQuickBinaryExpression(eqCondition, salaryCondition, com.github.paohaijiao.enums.JQuickBinaryOperator.AND);
        JQuickNestedLoopJoinPhysicalNode joinNode = new JQuickNestedLoopJoinPhysicalNode(JQuickJoinType.INNER, leftScan, rightScan, condition);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(joinNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        // 只有 salary > 8000 的员工：Bob(10000), Charlie(12000), David(9000)
        assertEquals("带条件的 INNER JOIN 应该返回 3 条数据", 3, result.size());
    }

    /**
     * 测试 INNER JOIN - 多条件
     * 
     * 目的：验证 Nested Loop INNER JOIN 多条件连接
     * 预期：所有条件都满足才返回
     */
    @Test
    public void testInnerJoin_MultipleConditions() {
        JQuickTableScanPhysicalNode leftScan = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickTableScanPhysicalNode rightScan = new JQuickTableScanPhysicalNode("department", null, null, null, null);
        // JOIN 条件：employee.dept_id = department.dept_id AND location = '北京'
        JQuickExpression eqCondition = new JQuickBinaryExpression(new JQuickColumnRefExpression("left.dept_id"), new JQuickColumnRefExpression("right.dept_id"), com.github.paohaijiao.enums.JQuickBinaryOperator.EQ);
        JQuickExpression locationCondition = new JQuickBinaryExpression(new JQuickColumnRefExpression("location"), new JQuickLiteralExpression("北京"), com.github.paohaijiao.enums.JQuickBinaryOperator.EQ);
        JQuickExpression condition = new JQuickBinaryExpression(eqCondition, locationCondition, com.github.paohaijiao.enums.JQuickBinaryOperator.AND);
        JQuickNestedLoopJoinPhysicalNode joinNode = new JQuickNestedLoopJoinPhysicalNode(JQuickJoinType.INNER, leftScan, rightScan, condition);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(joinNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        // 只有北京的技术部员工：Alice, Bob
        assertEquals("多条件 INNER JOIN 应该返回 2 条数据", 2, result.size());
    }
    /**
     * 测试 LEFT JOIN - 基本连接
     * 
     * 目的：验证 Nested Loop LEFT JOIN 能够正确返回左表所有行
     * 预期：左表所有行都返回，右表不匹配则为 null
     */
    @Test
    public void testLeftJoin_Basic() {
        JQuickTableScanPhysicalNode leftScan = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickTableScanPhysicalNode rightScan = new JQuickTableScanPhysicalNode("department", null, null, null, null);
        // JOIN 条件：employee.dept_id = department.dept_id
        JQuickExpression condition = new JQuickBinaryExpression(new JQuickColumnRefExpression("left.dept_id"), new JQuickColumnRefExpression("right.dept_id"), com.github.paohaijiao.enums.JQuickBinaryOperator.EQ);
        JQuickNestedLoopJoinPhysicalNode joinNode = new JQuickNestedLoopJoinPhysicalNode(JQuickJoinType.LEFT, leftScan, rightScan, condition);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(joinNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        // employee 有 6 条，LEFT JOIN 应该返回 6 条
        assertEquals("LEFT JOIN 应该返回 6 条数据", 6, result.size());
        // 验证 Frank 的部门信息为 null
        boolean foundFrank = false;
        for (JQuickRow row : result.getRows()) {
            if ("Frank".equals(row.get("emp_name"))) {
                foundFrank = true;
                assertNull("Frank 的 dept_name 应该为 null", row.get("dept_name"));
            }
        }
        assertTrue("应该找到 Frank", foundFrank);
    }

    /**
     * 测试 LEFT JOIN - 右表无匹配
     * 
     * 目的：验证 Nested Loop LEFT JOIN 在右表无匹配时的行为
     * 预期：左表行保留，右表列为 null
     */
    @Test
    public void testLeftJoin_NoRightMatch() {
        // 创建测试数据
        JQuickDataSet.Builder leftBuilder = JQuickDataSet.builder();
        leftBuilder.addColumn("id", Long.class, "left_table");
        leftBuilder.addColumn("name", String.class, "left_table");
        JQuickRow row = new JQuickRow();
        row.put("id", 999L);
        row.put("name", "NoMatch");
        leftBuilder.addRow(row);
        JQuickDataSourceManager.registerTable("left_table", leftBuilder.build());
        JQuickTableScanPhysicalNode leftScan = new JQuickTableScanPhysicalNode("left_table", null, null, null, null);
        JQuickTableScanPhysicalNode rightScan = new JQuickTableScanPhysicalNode("department", null, null, null, null);
        JQuickExpression condition = new JQuickBinaryExpression(new JQuickColumnRefExpression("id"), new JQuickColumnRefExpression("dept_id"), com.github.paohaijiao.enums.JQuickBinaryOperator.EQ);
        JQuickNestedLoopJoinPhysicalNode joinNode = new JQuickNestedLoopJoinPhysicalNode(JQuickJoinType.LEFT, leftScan, rightScan, condition);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(joinNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("LEFT JOIN 应该返回 1 条数据", 1, result.size());
        assertNull("右表列应该为 null", result.getRows().get(0).get("dept_name"));
    }
    /**
     * 测试 RIGHT JOIN - 基本连接
     * 
     * 目的：验证 Nested Loop RIGHT JOIN 能够正确返回右表所有行
     * 预期：右表所有行都返回，左表不匹配则为 null
     */
    @Test
    public void testRightJoin_Basic() {
        JQuickTableScanPhysicalNode leftScan = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickTableScanPhysicalNode rightScan = new JQuickTableScanPhysicalNode("department", null, null, null, null);
        JQuickExpression condition = new JQuickBinaryExpression(new JQuickColumnRefExpression("left.dept_id"), new JQuickColumnRefExpression("right.dept_id"), com.github.paohaijiao.enums.JQuickBinaryOperator.EQ);
        JQuickNestedLoopJoinPhysicalNode joinNode = new JQuickNestedLoopJoinPhysicalNode(JQuickJoinType.RIGHT, leftScan, rightScan, condition);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(joinNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("RIGHT JOIN 应该返回 6 条数据", 6, result.size());
        // 验证财务部没有员工
        boolean foundFinance = false;
        for (JQuickRow row : result.getRows()) {
            if ("财务部".equals(row.get("dept_name"))) {
                foundFinance = true;
                assertNull("财务部的 emp_name 应该为 null", row.get("emp_name"));
            }
        }
        assertTrue("应该找到财务部", foundFinance);
    }
    /**
     * 测试 FULL JOIN - 基本连接
     * 
     * 目的：验证 Nested Loop FULL JOIN 能够返回两表所有行
     * 预期：两表所有行都返回，不匹配的用 null 填充
     */
    @Test
    public void testFullJoin_Basic() {
        JQuickTableScanPhysicalNode leftScan = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickTableScanPhysicalNode rightScan = new JQuickTableScanPhysicalNode("department", null, null, null, null);
        // JOIN 条件：employee.dept_id = department.dept_id
        JQuickExpression condition = new JQuickBinaryExpression(new JQuickColumnRefExpression("dept_id"), new JQuickColumnRefExpression("dept_id"), com.github.paohaijiao.enums.JQuickBinaryOperator.EQ);
        JQuickNestedLoopJoinPhysicalNode joinNode = new JQuickNestedLoopJoinPhysicalNode(JQuickJoinType.FULL, leftScan, rightScan, condition);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(joinNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        // employee 有 6 条（Frank 无部门），department 有 4 条（财务部无员工）
        // FULL JOIN 应该返回：5（匹配）+ 1（Frank）+ 1（财务部）= 7 条
        assertEquals("FULL JOIN 应该返回 7 条数据", 7, result.size());
    }
    /**
     * 测试 CROSS JOIN - 笛卡尔积
     * 
     * 目的：验证 Nested Loop CROSS JOIN 能够正确计算笛卡尔积
     * 预期：返回两表的笛卡尔积
     */
    @Test
    public void testCrossJoin_Basic() {
        JQuickTableScanPhysicalNode leftScan = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickTableScanPhysicalNode rightScan = new JQuickTableScanPhysicalNode("department", null, null, null, null);
        JQuickNestedLoopJoinPhysicalNode joinNode = new JQuickNestedLoopJoinPhysicalNode(JQuickJoinType.CROSS, leftScan, rightScan, null);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(joinNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        // employee 6 条 × department 4 条 = 24 条
        assertEquals("CROSS JOIN 应该返回 24 条数据", 24, result.size());
    }

    /**
     * 测试 CROSS JOIN - 小表
     * 
     * 目的：验证 Nested Loop CROSS JOIN 小表
     * 预期：正确计算笛卡尔积
     */
    @Test
    public void testCrossJoin_SmallTables() {
        JQuickDataSet.Builder table1Builder = JQuickDataSet.builder();
        table1Builder.addColumn("a", String.class, "table1");
        JQuickRow row1 = new JQuickRow();
        row1.put("a", "A1");
        table1Builder.addRow(row1);
        JQuickRow row2 = new JQuickRow();
        row2.put("a", "A2");
        table1Builder.addRow(row2);
        JQuickDataSourceManager.registerTable("table1", table1Builder.build());
        JQuickDataSet.Builder table2Builder = JQuickDataSet.builder();
        table2Builder.addColumn("b", String.class, "table2");
        JQuickRow row3 = new JQuickRow();
        row3.put("b", "B1");
        table2Builder.addRow(row3);
        JQuickRow row4 = new JQuickRow();
        row4.put("b", "B2");
        table2Builder.addRow(row4);
        JQuickRow row5 = new JQuickRow();
        row5.put("b", "B3");
        table2Builder.addRow(row5);
        JQuickDataSourceManager.registerTable("table2", table2Builder.build());
        JQuickTableScanPhysicalNode leftScan = new JQuickTableScanPhysicalNode("table1", null, null, null, null);
        JQuickTableScanPhysicalNode rightScan = new JQuickTableScanPhysicalNode("table2", null, null, null, null);
        JQuickNestedLoopJoinPhysicalNode joinNode = new JQuickNestedLoopJoinPhysicalNode(JQuickJoinType.CROSS, leftScan, rightScan, null);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(joinNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        // table1 2 条 × table2 3 条 = 6 条
        assertEquals("CROSS JOIN 应该返回 6 条数据", 6, result.size());
    }
    /**
     * 测试空表 JOIN
     * 
     * 目的：验证 Nested Loop JOIN 空表的行为
     * 预期：返回空结果或另一表的数据
     */
    @Test
    public void testJoin_EmptyTable() {
        JQuickDataSourceManager.registerTable("empty_table", JQuickDataSet.builder().build());
        JQuickTableScanPhysicalNode leftScan = new JQuickTableScanPhysicalNode("empty_table", null, null, null, null);
        JQuickTableScanPhysicalNode rightScan = new JQuickTableScanPhysicalNode("department", null, null, null, null);
        JQuickExpression condition = new JQuickBinaryExpression(new JQuickColumnRefExpression("id"), new JQuickColumnRefExpression("dept_id"), com.github.paohaijiao.enums.JQuickBinaryOperator.EQ);
        JQuickNestedLoopJoinPhysicalNode joinNode = new JQuickNestedLoopJoinPhysicalNode(JQuickJoinType.INNER, leftScan, rightScan, condition);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(joinNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertTrue("空表 INNER JOIN 应该返回空结果", result.isEmpty());
    }

    /**
     * 测试两表都为空
     * 
     * 目的：验证 Nested Loop JOIN 两个空表的行为
     * 预期：返回空结果
     */
    @Test
    public void testJoin_BothEmpty() {
        JQuickDataSourceManager.registerTable("empty1", JQuickDataSet.builder().build());
        JQuickDataSourceManager.registerTable("empty2", JQuickDataSet.builder().build());
        JQuickTableScanPhysicalNode leftScan = new JQuickTableScanPhysicalNode("empty1", null, null, null, null);
        JQuickTableScanPhysicalNode rightScan = new JQuickTableScanPhysicalNode("empty2", null, null, null, null);
        JQuickNestedLoopJoinPhysicalNode joinNode = new JQuickNestedLoopJoinPhysicalNode(JQuickJoinType.INNER, leftScan, rightScan, null);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(joinNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertTrue("两个空表 JOIN 应该返回空结果", result.isEmpty());
    }

    /**
     * 测试包含 null 值的 JOIN
     * 
     * 目的：验证 Nested Loop JOIN 键包含 null 值时的行为
     * 预期：null 值不参与匹配
     */
    @Test
    public void testJoin_WithNullKeys() {
        // employee 表中 Frank 的 dept_id 为 null
        JQuickTableScanPhysicalNode leftScan = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickTableScanPhysicalNode rightScan = new JQuickTableScanPhysicalNode("department", null, null, null, null);
        JQuickExpression condition = new JQuickBinaryExpression(new JQuickColumnRefExpression("left.dept_id"), new JQuickColumnRefExpression("right.dept_id"), com.github.paohaijiao.enums.JQuickBinaryOperator.EQ);
        JQuickNestedLoopJoinPhysicalNode joinNode = new JQuickNestedLoopJoinPhysicalNode(JQuickJoinType.INNER, leftScan, rightScan, condition);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(joinNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        // Frank 的 dept_id 为 null，不会匹配任何部门
        assertEquals("INNER JOIN 应该返回 5 条数据（排除 null 键）", 5, result.size());
    }

    /**
     * 测试大数据量 JOIN
     * 
     * 目的：验证 Nested Loop JOIN 大数据量的性能
     * 预期：能够正确处理大数据量
     */
    @Test
    public void testJoin_LargeData() {
        // 创建大数据量表
        JQuickDataSet.Builder leftBuilder = JQuickDataSet.builder();
        leftBuilder.addColumn("id", Long.class, "large_left");
        leftBuilder.addColumn("key", Long.class, "large_left");
        int leftCount = 100;
        for (int i = 0; i < leftCount; i++) {
            JQuickRow row = new JQuickRow();
            row.put("id", (long) i);
            row.put("key", (long) (i % 10));
            leftBuilder.addRow(row);
        }
        JQuickDataSourceManager.registerTable("large_left", leftBuilder.build());
        JQuickDataSet.Builder rightBuilder = JQuickDataSet.builder();
        rightBuilder.addColumn("key", Long.class, "large_right");
        rightBuilder.addColumn("value", String.class, "large_right");
        int rightCount = 10;
        for (int i = 0; i < rightCount; i++) {
            JQuickRow row = new JQuickRow();
            row.put("key", (long) i);
            row.put("value", "value_" + i);
            rightBuilder.addRow(row);
        }
        JQuickDataSourceManager.registerTable("large_right", rightBuilder.build());
        JQuickTableScanPhysicalNode leftScan = new JQuickTableScanPhysicalNode("large_left", null, null, null, null);
        JQuickTableScanPhysicalNode rightScan = new JQuickTableScanPhysicalNode("large_right", null, null, null, null);
        JQuickExpression condition = new JQuickBinaryExpression(new JQuickColumnRefExpression("left.key"), new JQuickColumnRefExpression("right.key"), com.github.paohaijiao.enums.JQuickBinaryOperator.EQ);
        JQuickNestedLoopJoinPhysicalNode joinNode = new JQuickNestedLoopJoinPhysicalNode(JQuickJoinType.INNER, leftScan, rightScan, condition);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        long startTime = System.currentTimeMillis();
        JQuickDataSet result = nodeExecutor.executeNode(joinNode, context);
        result.printTable();
        long endTime = System.currentTimeMillis();
        assertNotNull("结果不应为 null", result);
        assertEquals("INNER JOIN 应该返回 100 条数据", 100, result.size());
        System.out.println("Nested Loop JOIN " + leftCount + " × " + rightCount + " 条数据耗时: " + (endTime - startTime) + "ms");
    }
    /**
     * 测试多表 JOIN
     * 
     * 目的：验证 Nested Loop 多表连接的正确性
     * 预期：正确执行多表 JOIN
     */
    @Test
    public void testJoin_MultipleTables() {
        // employee JOIN department ON dept_id
        JQuickTableScanPhysicalNode empScan = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickTableScanPhysicalNode deptScan = new JQuickTableScanPhysicalNode("department", null, null, null, null);
        JQuickExpression condition1 = new JQuickBinaryExpression(new JQuickColumnRefExpression("dept_id"), new JQuickColumnRefExpression("dept_id"), com.github.paohaijiao.enums.JQuickBinaryOperator.EQ);
        JQuickNestedLoopJoinPhysicalNode join1 = new JQuickNestedLoopJoinPhysicalNode(JQuickJoinType.INNER, empScan, deptScan, condition1);
        JQuickTableScanPhysicalNode projectScan = new JQuickTableScanPhysicalNode("project", null, null, null, null);
        JQuickExpression condition2 = new JQuickBinaryExpression(new JQuickColumnRefExpression("emp_id"), new JQuickColumnRefExpression("leader_id"), com.github.paohaijiao.enums.JQuickBinaryOperator.EQ);
        JQuickNestedLoopJoinPhysicalNode join2 = new JQuickNestedLoopJoinPhysicalNode(JQuickJoinType.INNER, join1, projectScan, condition2);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(join2, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        // Alice(1), Bob(2), Charlie(3) 是项目负责人
        assertEquals("多表 JOIN 应该返回 3 条数据", 3, result.size());
    }

    /**
     * 测试 OR 条件 JOIN
     * 
     * 目的：验证 Nested Loop JOIN OR 条件
     * 预期：满足任一条件即返回
     */
    @Test
    public void testJoin_OrCondition() {
        JQuickTableScanPhysicalNode leftScan = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickTableScanPhysicalNode rightScan = new JQuickTableScanPhysicalNode("department", null, null, null, null);
        // JOIN 条件：employee.dept_id = department.dept_id OR salary > 10000
        JQuickExpression eqCondition = new JQuickBinaryExpression(new JQuickColumnRefExpression("dept_id"), new JQuickColumnRefExpression("dept_id"), com.github.paohaijiao.enums.JQuickBinaryOperator.EQ);
        JQuickExpression salaryCondition = new JQuickBinaryExpression(new JQuickColumnRefExpression("salary"), new JQuickLiteralExpression(10000.0), com.github.paohaijiao.enums.JQuickBinaryOperator.GT);
        JQuickExpression condition = new JQuickBinaryExpression(eqCondition, salaryCondition, com.github.paohaijiao.enums.JQuickBinaryOperator.OR);
        JQuickNestedLoopJoinPhysicalNode joinNode = new JQuickNestedLoopJoinPhysicalNode(JQuickJoinType.INNER, leftScan, rightScan, condition);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(joinNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        // 匹配 dept_id 的 5 条 + salary > 10000 的额外匹配
        assertTrue("OR 条件 JOIN 应该返回数据", result.size() > 0);
    }

    /**
     * 测试不等值 JOIN
     * 
     * 目的：验证 Nested Loop JOIN 不等值条件
     * 预期：正确处理不等值条件
     */
    @Test
    public void testJoin_NonEquiCondition() {
        JQuickTableScanPhysicalNode leftScan = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickTableScanPhysicalNode rightScan = new JQuickTableScanPhysicalNode("department", null, null, null, null);
        // JOIN 条件：employee.dept_id = department.dept_id AND salary > 9000
        JQuickExpression eqCondition = new JQuickBinaryExpression(
                new JQuickColumnRefExpression("dept_id"),
                new JQuickColumnRefExpression("dept_id"),
                com.github.paohaijiao.enums.JQuickBinaryOperator.EQ);
        JQuickExpression salaryCondition = new JQuickBinaryExpression(
                new JQuickColumnRefExpression("salary"),
                new JQuickLiteralExpression(9000.0),
                com.github.paohaijiao.enums.JQuickBinaryOperator.GT);
        JQuickExpression condition = new JQuickBinaryExpression(
                eqCondition,
                salaryCondition,
                com.github.paohaijiao.enums.JQuickBinaryOperator.AND);
        
        JQuickNestedLoopJoinPhysicalNode joinNode = new JQuickNestedLoopJoinPhysicalNode(JQuickJoinType.INNER, leftScan, rightScan, condition);
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(joinNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        // 只有 Bob(10000) 和 Charlie(12000) 满足 salary > 9000
        assertEquals("不等值 JOIN 应该返回 2 条数据", 2, result.size());
    }
}
