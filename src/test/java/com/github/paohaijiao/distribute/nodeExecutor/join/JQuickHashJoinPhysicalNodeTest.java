package com.github.paohaijiao.distribute.nodeExecutor.join;

import com.github.paohaijiao.datasource.JQuickDataSourceManager;
import com.github.paohaijiao.distributed.worker.*;
import com.github.paohaijiao.enums.JQuickJoinType;
import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.expression.domain.JQuickBinaryExpression;
import com.github.paohaijiao.expression.domain.JQuickColumnRefExpression;
import com.github.paohaijiao.expression.domain.JQuickLiteralExpression;
import com.github.paohaijiao.function.manager.JQuickMethodInvocationManager;
import com.github.paohaijiao.physical.node.JQuickHashJoinPhysicalNode;
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
 * JQuickHashJoinPhysicalNode 单元测试
 * 
 * 测试范围：
 * 1. INNER JOIN
 * 2. LEFT JOIN
 * 3. RIGHT JOIN
 * 4. FULL JOIN
 * 5. CROSS JOIN
 * 6. 多条件 JOIN
 * 7. 边界条件
 */
public class JQuickHashJoinPhysicalNodeTest {

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

        // 小表 - 用于测试广播 JOIN
        List<JQuickColumnMeta> smallTableColumns = Arrays.asList(
                new JQuickColumnMeta("code", String.class, "small_table"),
                new JQuickColumnMeta("description", String.class, "small_table")
        );
        List<JQuickRow> smallTableRows = new ArrayList<>();
        smallTableRows.add(createSmallTableRow("A", "类型A"));
        smallTableRows.add(createSmallTableRow("B", "类型B"));
        JQuickDataSet smallTable = new JQuickDataSet(smallTableColumns, smallTableRows);
        JQuickDataSourceManager.registerTable("small_table", smallTable);
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

    private JQuickRow createSmallTableRow(String code, String description) {
        JQuickRow row = new JQuickRow();
        row.put("code", code);
        row.put("description", description);
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
     * 目的：验证 INNER JOIN 能够正确连接两个表
     * 预期：只返回匹配的行
     */
    @Test
    public void testInnerJoin_Basic() {
        JQuickTableScanPhysicalNode leftScan = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickTableScanPhysicalNode rightScan = new JQuickTableScanPhysicalNode("department", null, null, null, null);
        List<JQuickHashJoinPhysicalNode.JoinKeyPair> joinKeys = Arrays.asList(new JQuickHashJoinPhysicalNode.JoinKeyPair(new JQuickColumnRefExpression("dept_id"), new JQuickColumnRefExpression("dept_id")));
        JQuickHashJoinPhysicalNode joinNode = new JQuickHashJoinPhysicalNode(
                JQuickJoinType.INNER,
                leftScan,
                rightScan,
                null,
                joinKeys,
                JQuickHashJoinPhysicalNode.BuildSide.RIGHT,
                JQuickHashJoinPhysicalNode.JoinDistribution.LOCAL
        );
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(joinNode, context);
        
        assertNotNull("结果不应为 null", result);
        // employee 有 6 条，其中 Frank 的 dept_id 为 null
        // department 有 4 条，其中财务部没有员工
        // INNER JOIN 应该返回 5 条（排除 Frank 和财务部）
        assertEquals("INNER JOIN 应该返回 5 条数据", 5, result.size());
    }

    /**
     * 测试 INNER JOIN - 带条件
     * 
     * 目的：验证 INNER JOIN 带额外过滤条件
     * 预期：返回满足连接条件和过滤条件的数据
     */
    @Test
    public void testInnerJoin_WithCondition() {
        JQuickTableScanPhysicalNode leftScan = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickTableScanPhysicalNode rightScan = new JQuickTableScanPhysicalNode("department", null, null, null, null);
        List<JQuickHashJoinPhysicalNode.JoinKeyPair> joinKeys = Arrays.asList(new JQuickHashJoinPhysicalNode.JoinKeyPair(new JQuickColumnRefExpression("dept_id"), new JQuickColumnRefExpression("dept_id")));
        JQuickExpression condition = new JQuickBinaryExpression(
                new JQuickColumnRefExpression("salary"),
                new JQuickLiteralExpression(8000.0),
                com.github.paohaijiao.enums.JQuickBinaryOperator.GT);
        
        JQuickHashJoinPhysicalNode joinNode = new JQuickHashJoinPhysicalNode(
                JQuickJoinType.INNER,
                leftScan,
                rightScan,
                condition,
                joinKeys,
                JQuickHashJoinPhysicalNode.BuildSide.RIGHT,
                JQuickHashJoinPhysicalNode.JoinDistribution.LOCAL
        );
        
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(joinNode, context);
        
        assertNotNull("结果不应为 null", result);
        // INNER JOIN 匹配 5 条，但 condition salary > 8000 过滤后只剩 3 条
        // Bob(10000), Charlie(12000), David(9000) 满足条件
        assertEquals("带条件的 INNER JOIN 应该返回 3 条数据", 3, result.size());
    }

    /**
     * 测试 INNER JOIN - 多键连接
     * 
     * 目的：验证多键连接的正确性
     * 预期：所有键都匹配才返回
     */
    @Test
    public void testInnerJoin_MultipleKeys() {
        // 创建测试表
        JQuickDataSet.Builder table1Builder = JQuickDataSet.builder();
        table1Builder.addColumn("key1", String.class, "table1");
        table1Builder.addColumn("key2", Integer.class, "table1");
        table1Builder.addColumn("value1", String.class, "table1");
        JQuickRow row1 = new JQuickRow();
        row1.put("key1", "A");
        row1.put("key2", 1);
        row1.put("value1", "v1");
        table1Builder.addRow(row1);
        
        JQuickRow row2 = new JQuickRow();
        row2.put("key1", "A");
        row2.put("key2", 2);
        row2.put("value1", "v2");
        table1Builder.addRow(row2);
        
        JQuickDataSourceManager.registerTable("table1", table1Builder.build());
        
        JQuickDataSet.Builder table2Builder = JQuickDataSet.builder();
        table2Builder.addColumn("key1", String.class, "table2");
        table2Builder.addColumn("key2", Integer.class, "table2");
        table2Builder.addColumn("value2", String.class, "table2");
        
        JQuickRow row3 = new JQuickRow();
        row3.put("key1", "A");
        row3.put("key2", 1);
        row3.put("value2", "w1");
        table2Builder.addRow(row3);
        
        JQuickRow row4 = new JQuickRow();
        row4.put("key1", "B");
        row4.put("key2", 1);
        row4.put("value2", "w2");
        table2Builder.addRow(row4);
        
        JQuickDataSourceManager.registerTable("table2", table2Builder.build());
        
        JQuickTableScanPhysicalNode leftScan = new JQuickTableScanPhysicalNode("table1", null, null, null, null);
        JQuickTableScanPhysicalNode rightScan = new JQuickTableScanPhysicalNode("table2", null, null, null, null);
        
        List<JQuickHashJoinPhysicalNode.JoinKeyPair> joinKeys = Arrays.asList(
                new JQuickHashJoinPhysicalNode.JoinKeyPair(
                        new JQuickColumnRefExpression("key1"),
                        new JQuickColumnRefExpression("key1")),
                new JQuickHashJoinPhysicalNode.JoinKeyPair(
                        new JQuickColumnRefExpression("key2"),
                        new JQuickColumnRefExpression("key2"))
        );
        JQuickHashJoinPhysicalNode joinNode = new JQuickHashJoinPhysicalNode(
                JQuickJoinType.INNER,
                leftScan,
                rightScan,
                null,
                joinKeys,
                JQuickHashJoinPhysicalNode.BuildSide.RIGHT,
                JQuickHashJoinPhysicalNode.JoinDistribution.LOCAL
        );
        
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(joinNode, context);
        
        assertNotNull("结果不应为 null", result);
        // 只有 key1='A' AND key2=1 匹配
        assertEquals("多键 INNER JOIN 应该返回 1 条数据", 1, result.size());
    }
    /**
     * 测试 LEFT JOIN - 基本连接
     * 
     * 目的：验证 LEFT JOIN 能够正确返回左表所有行
     * 预期：左表所有行都返回，右表不匹配则为 null
     */
    @Test
    public void testLeftJoin_Basic() {
        JQuickTableScanPhysicalNode leftScan = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickTableScanPhysicalNode rightScan = new JQuickTableScanPhysicalNode("department", null, null, null, null);
        List<JQuickHashJoinPhysicalNode.JoinKeyPair> joinKeys = Arrays.asList(
                new JQuickHashJoinPhysicalNode.JoinKeyPair(new JQuickColumnRefExpression("dept_id"), new JQuickColumnRefExpression("dept_id"))
        );
        JQuickHashJoinPhysicalNode joinNode = new JQuickHashJoinPhysicalNode(
                JQuickJoinType.LEFT,
                leftScan,
                rightScan,
                null,
                joinKeys,
                JQuickHashJoinPhysicalNode.BuildSide.RIGHT,
                JQuickHashJoinPhysicalNode.JoinDistribution.LOCAL
        );
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(joinNode, context);
        
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
     * 目的：验证 LEFT JOIN 在右表无匹配时的行为
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
        List<JQuickHashJoinPhysicalNode.JoinKeyPair> joinKeys = Arrays.asList(
                new JQuickHashJoinPhysicalNode.JoinKeyPair(new JQuickColumnRefExpression("id"), new JQuickColumnRefExpression("dept_id"))
        );
        JQuickHashJoinPhysicalNode joinNode = new JQuickHashJoinPhysicalNode(
                JQuickJoinType.LEFT,
                leftScan,
                rightScan,
                null,
                joinKeys,
                JQuickHashJoinPhysicalNode.BuildSide.RIGHT,
                JQuickHashJoinPhysicalNode.JoinDistribution.LOCAL
        );
        
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(joinNode, context);
        
        assertNotNull("结果不应为 null", result);
        assertEquals("LEFT JOIN 应该返回 1 条数据", 1, result.size());
        assertNull("右表列应该为 null", result.getRows().get(0).get("dept_name"));
    }


    /**
     * 测试 RIGHT JOIN - 基本连接
     * 
     * 目的：验证 RIGHT JOIN 能够正确返回右表所有行
     * 预期：右表所有行都返回，左表不匹配则为 null
     */
    @Test
    public void testRightJoin_Basic() {
        JQuickTableScanPhysicalNode leftScan = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickTableScanPhysicalNode rightScan = new JQuickTableScanPhysicalNode("department", null, null, null, null);
        List<JQuickHashJoinPhysicalNode.JoinKeyPair> joinKeys = Arrays.asList(
                new JQuickHashJoinPhysicalNode.JoinKeyPair(
                        new JQuickColumnRefExpression("dept_id"),
                        new JQuickColumnRefExpression("dept_id"))
        );
        
        JQuickHashJoinPhysicalNode joinNode = new JQuickHashJoinPhysicalNode(
                JQuickJoinType.RIGHT,
                leftScan,
                rightScan,
                null,
                joinKeys,
                JQuickHashJoinPhysicalNode.BuildSide.RIGHT,
                JQuickHashJoinPhysicalNode.JoinDistribution.LOCAL
        );
        
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(joinNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        // department 有 4 条，RIGHT JOIN 应该返回 4 条
        assertEquals("RIGHT JOIN 应该返回 6 条数据", 6, result.size());
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
     * 目的：验证 FULL JOIN 能够返回两表所有行
     * 预期：两表所有行都返回，不匹配的用 null 填充
     */
    @Test
    public void testFullJoin_Basic() {
        JQuickTableScanPhysicalNode leftScan = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickTableScanPhysicalNode rightScan = new JQuickTableScanPhysicalNode("department", null, null, null, null);
        List<JQuickHashJoinPhysicalNode.JoinKeyPair> joinKeys = Arrays.asList(
                new JQuickHashJoinPhysicalNode.JoinKeyPair(new JQuickColumnRefExpression("dept_id"), new JQuickColumnRefExpression("dept_id"))
        );
        JQuickHashJoinPhysicalNode joinNode = new JQuickHashJoinPhysicalNode(
                JQuickJoinType.FULL,
                leftScan,
                rightScan,
                null,
                joinKeys,
                JQuickHashJoinPhysicalNode.BuildSide.RIGHT,
                JQuickHashJoinPhysicalNode.JoinDistribution.LOCAL
        );
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
     * 目的：验证 CROSS JOIN 能够正确计算笛卡尔积
     * 预期：返回两表的笛卡尔积
     */
    @Test
    public void testCrossJoin_Basic() {
        JQuickTableScanPhysicalNode leftScan = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickTableScanPhysicalNode rightScan = new JQuickTableScanPhysicalNode("department", null, null, null, null);
        JQuickHashJoinPhysicalNode joinNode = new JQuickHashJoinPhysicalNode(
                JQuickJoinType.CROSS,
                leftScan,
                rightScan,
                null,
                Collections.emptyList(),
                JQuickHashJoinPhysicalNode.BuildSide.RIGHT,
                JQuickHashJoinPhysicalNode.JoinDistribution.LOCAL
        );
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
     * 目的：验证小表的 CROSS JOIN
     * 预期：正确计算笛卡尔积
     */
    @Test
    public void testCrossJoin_SmallTables() {
        JQuickTableScanPhysicalNode leftScan = new JQuickTableScanPhysicalNode("small_table", null, null, null, null);
        JQuickTableScanPhysicalNode rightScan = new JQuickTableScanPhysicalNode("department", null, null, null, null);
        JQuickHashJoinPhysicalNode joinNode = new JQuickHashJoinPhysicalNode(
                JQuickJoinType.CROSS,
                leftScan,
                rightScan,
                null,
                Collections.emptyList(),
                JQuickHashJoinPhysicalNode.BuildSide.RIGHT,
                JQuickHashJoinPhysicalNode.JoinDistribution.LOCAL
        );
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(joinNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        // small_table 2 条 × department 4 条 = 8 条
        assertEquals("CROSS JOIN 应该返回 8 条数据", 8, result.size());
    }

    /**
     * 测试 BuildSide - LEFT
     * 
     * 目的：验证左表作为构建表的 JOIN
     * 预期：正确执行 JOIN
     */
    @Test
    public void testBuildSide_Left() {
        JQuickTableScanPhysicalNode leftScan = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickTableScanPhysicalNode rightScan = new JQuickTableScanPhysicalNode("department", null, null, null, null);
        List<JQuickHashJoinPhysicalNode.JoinKeyPair> joinKeys = Arrays.asList(
                new JQuickHashJoinPhysicalNode.JoinKeyPair(
                        new JQuickColumnRefExpression("dept_id"),
                        new JQuickColumnRefExpression("dept_id"))
        );
        JQuickHashJoinPhysicalNode joinNode = new JQuickHashJoinPhysicalNode(
                JQuickJoinType.INNER,
                leftScan,
                rightScan,
                null,
                joinKeys,
                JQuickHashJoinPhysicalNode.BuildSide.LEFT,
                JQuickHashJoinPhysicalNode.JoinDistribution.LOCAL
        );
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(joinNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("INNER JOIN 应该返回 5 条数据", 5, result.size());
    }

    /**
     * 测试 BuildSide - RIGHT
     * 
     * 目的：验证右表作为构建表的 JOIN
     * 预期：正确执行 JOIN
     */
    @Test
    public void testBuildSide_Right() {
        JQuickTableScanPhysicalNode leftScan = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickTableScanPhysicalNode rightScan = new JQuickTableScanPhysicalNode("department", null, null, null, null);
        List<JQuickHashJoinPhysicalNode.JoinKeyPair> joinKeys = Arrays.asList(
                new JQuickHashJoinPhysicalNode.JoinKeyPair(
                        new JQuickColumnRefExpression("dept_id"),
                        new JQuickColumnRefExpression("dept_id"))
        );
        JQuickHashJoinPhysicalNode joinNode = new JQuickHashJoinPhysicalNode(
                JQuickJoinType.INNER,
                leftScan,
                rightScan,
                null,
                joinKeys,
                JQuickHashJoinPhysicalNode.BuildSide.RIGHT,
                JQuickHashJoinPhysicalNode.JoinDistribution.LOCAL
        );
        
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(joinNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("INNER JOIN 应该返回 5 条数据", 5, result.size());
    }

    /**
     * 测试空表 JOIN
     * 
     * 目的：验证空表参与 JOIN 的行为
     * 预期：返回空结果或另一表的数据
     */
    @Test
    public void testJoin_EmptyTable() {
        JQuickDataSourceManager.registerTable("empty_table", JQuickDataSet.builder().build());
        JQuickTableScanPhysicalNode leftScan = new JQuickTableScanPhysicalNode("empty_table", null, null, null, null);
        JQuickTableScanPhysicalNode rightScan = new JQuickTableScanPhysicalNode("department", null, null, null, null);
        List<JQuickHashJoinPhysicalNode.JoinKeyPair> joinKeys = Arrays.asList(new JQuickHashJoinPhysicalNode.JoinKeyPair(new JQuickColumnRefExpression("id"), new JQuickColumnRefExpression("dept_id")));
        JQuickHashJoinPhysicalNode joinNode = new JQuickHashJoinPhysicalNode(
                JQuickJoinType.INNER,
                leftScan,
                rightScan,
                null,
                joinKeys,
                JQuickHashJoinPhysicalNode.BuildSide.RIGHT,
                JQuickHashJoinPhysicalNode.JoinDistribution.LOCAL
        );
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(joinNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertTrue("空表 INNER JOIN 应该返回空结果", result.isEmpty());
    }

    /**
     * 测试两表都为空
     * 
     * 目的：验证两个空表 JOIN 的行为
     * 预期：返回空结果
     */
    @Test
    public void testJoin_BothEmpty() {
        JQuickDataSourceManager.registerTable("empty1", JQuickDataSet.builder().build());
        JQuickDataSourceManager.registerTable("empty2", JQuickDataSet.builder().build());
        JQuickTableScanPhysicalNode leftScan = new JQuickTableScanPhysicalNode("empty1", null, null, null, null);
        JQuickTableScanPhysicalNode rightScan = new JQuickTableScanPhysicalNode("empty2", null, null, null, null);
        JQuickHashJoinPhysicalNode joinNode = new JQuickHashJoinPhysicalNode(
                JQuickJoinType.INNER,
                leftScan,
                rightScan,
                null,
                Collections.emptyList(),
                JQuickHashJoinPhysicalNode.BuildSide.RIGHT,
                JQuickHashJoinPhysicalNode.JoinDistribution.LOCAL
        );
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(joinNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertTrue("两个空表 JOIN 应该返回空结果", result.isEmpty());
    }

    /**
     * 测试包含 null 值的 JOIN
     * 
     * 目的：验证 JOIN 键包含 null 值时的行为
     * 预期：null 值不参与匹配
     */
    @Test
    public void testJoin_WithNullKeys() {
        // employee 表中 Frank 的 dept_id 为 null
        JQuickTableScanPhysicalNode leftScan = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickTableScanPhysicalNode rightScan = new JQuickTableScanPhysicalNode("department", null, null, null, null);
        List<JQuickHashJoinPhysicalNode.JoinKeyPair> joinKeys = Arrays.asList(
                new JQuickHashJoinPhysicalNode.JoinKeyPair(
                        new JQuickColumnRefExpression("dept_id"),
                        new JQuickColumnRefExpression("dept_id"))
        );
        
        JQuickHashJoinPhysicalNode joinNode = new JQuickHashJoinPhysicalNode(
                JQuickJoinType.INNER,
                leftScan,
                rightScan,
                null,
                joinKeys,
                JQuickHashJoinPhysicalNode.BuildSide.RIGHT,
                JQuickHashJoinPhysicalNode.JoinDistribution.LOCAL
        );
        
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
     * 目的：验证大数据量 JOIN 的性能
     * 预期：能够正确处理大数据量
     */
    @Test
    public void testJoin_LargeData() {
        // 创建大数据量表
        JQuickDataSet.Builder leftBuilder = JQuickDataSet.builder();
        leftBuilder.addColumn("id", Long.class, "large_left");
        leftBuilder.addColumn("key", Long.class, "large_left");
        
        int leftCount = 1000;
        for (int i = 0; i < leftCount; i++) {
            JQuickRow row = new JQuickRow();
            row.put("id", (long) i);
            row.put("key", (long) (i % 100));
            leftBuilder.addRow(row);
        }
        JQuickDataSourceManager.registerTable("large_left", leftBuilder.build());
        
        JQuickDataSet.Builder rightBuilder = JQuickDataSet.builder();
        rightBuilder.addColumn("key", Long.class, "large_right");
        rightBuilder.addColumn("value", String.class, "large_right");
        
        int rightCount = 100;
        for (int i = 0; i < rightCount; i++) {
            JQuickRow row = new JQuickRow();
            row.put("key", (long) i);
            row.put("value", "value_" + i);
            rightBuilder.addRow(row);
        }
        JQuickDataSourceManager.registerTable("large_right", rightBuilder.build());
        
        JQuickTableScanPhysicalNode leftScan = new JQuickTableScanPhysicalNode("large_left", null, null, null, null);
        JQuickTableScanPhysicalNode rightScan = new JQuickTableScanPhysicalNode("large_right", null, null, null, null);
        
        List<JQuickHashJoinPhysicalNode.JoinKeyPair> joinKeys = Arrays.asList(
                new JQuickHashJoinPhysicalNode.JoinKeyPair(
                        new JQuickColumnRefExpression("key"),
                        new JQuickColumnRefExpression("key"))
        );
        
        JQuickHashJoinPhysicalNode joinNode = new JQuickHashJoinPhysicalNode(
                JQuickJoinType.INNER,
                leftScan,
                rightScan,
                null,
                joinKeys,
                JQuickHashJoinPhysicalNode.BuildSide.RIGHT,
                JQuickHashJoinPhysicalNode.JoinDistribution.LOCAL
        );
        
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        long startTime = System.currentTimeMillis();
        JQuickDataSet result = nodeExecutor.executeNode(joinNode, context);
        result.printTable();
        long endTime = System.currentTimeMillis();
        
        assertNotNull("结果不应为 null", result);
        assertEquals("INNER JOIN 应该返回 1000 条数据", 1000, result.size());
        
        System.out.println("JOIN " + leftCount + " × " + rightCount + " 条数据耗时: " + (endTime - startTime) + "ms");
    }



    /**
     * 测试 LOCAL 分布
     * 
     * 目的：验证 LOCAL 分布的 JOIN
     * 预期：正确执行本地 JOIN
     */
    @Test
    public void testDistribution_Local() {
        JQuickTableScanPhysicalNode leftScan = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickTableScanPhysicalNode rightScan = new JQuickTableScanPhysicalNode("department", null, null, null, null);
        List<JQuickHashJoinPhysicalNode.JoinKeyPair> joinKeys = Arrays.asList(
                new JQuickHashJoinPhysicalNode.JoinKeyPair(
                        new JQuickColumnRefExpression("dept_id"),
                        new JQuickColumnRefExpression("dept_id"))
        );
        JQuickHashJoinPhysicalNode joinNode = new JQuickHashJoinPhysicalNode(
                JQuickJoinType.INNER,
                leftScan,
                rightScan,
                null,
                joinKeys,
                JQuickHashJoinPhysicalNode.BuildSide.RIGHT,
                JQuickHashJoinPhysicalNode.JoinDistribution.LOCAL
        );
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(joinNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("LOCAL JOIN 应该返回 5 条数据", 5, result.size());
    }

    /**
     * 测试 BROADCAST_HASH 分布
     * 
     * 目的：验证广播 JOIN
     * 预期：正确执行广播 JOIN
     */
    @Test
    public void testDistribution_BroadcastHash() {
        JQuickTableScanPhysicalNode leftScan = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickTableScanPhysicalNode rightScan = new JQuickTableScanPhysicalNode("small_table", null, null, null, null);
        List<JQuickHashJoinPhysicalNode.JoinKeyPair> joinKeys = Collections.emptyList();
        JQuickHashJoinPhysicalNode joinNode = new JQuickHashJoinPhysicalNode(
                JQuickJoinType.CROSS,
                leftScan,
                rightScan,
                null,
                joinKeys,
                JQuickHashJoinPhysicalNode.BuildSide.RIGHT,
                JQuickHashJoinPhysicalNode.JoinDistribution.BROADCAST_HASH
        );
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(joinNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        // employee 6 条 × small_table 2 条 = 12 条
        assertEquals("BROADCAST_HASH JOIN 应该返回 12 条数据", 12, result.size());
    }


    /**
     * 测试多表 JOIN
     * 
     * 目的：验证多表连接的正确性
     * 预期：正确执行多表 JOIN
     */
    @Test
    public void testJoin_MultipleTables() {
        // employee JOIN department ON dept_id
        JQuickTableScanPhysicalNode empScan = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickTableScanPhysicalNode deptScan = new JQuickTableScanPhysicalNode("department", null, null, null, null);
        
        List<JQuickHashJoinPhysicalNode.JoinKeyPair> joinKeys1 = Arrays.asList(
                new JQuickHashJoinPhysicalNode.JoinKeyPair(
                        new JQuickColumnRefExpression("dept_id"),
                        new JQuickColumnRefExpression("dept_id"))
        );
        
        JQuickHashJoinPhysicalNode join1 = new JQuickHashJoinPhysicalNode(
                JQuickJoinType.INNER,
                empScan,
                deptScan,
                null,
                joinKeys1,
                JQuickHashJoinPhysicalNode.BuildSide.RIGHT,
                JQuickHashJoinPhysicalNode.JoinDistribution.LOCAL
        );
        // (employee JOIN department) JOIN project ON emp_id = leader_id
        JQuickTableScanPhysicalNode projectScan = new JQuickTableScanPhysicalNode("project", null, null, null, null);
        List<JQuickHashJoinPhysicalNode.JoinKeyPair> joinKeys2 = Arrays.asList(
                new JQuickHashJoinPhysicalNode.JoinKeyPair(
                        new JQuickColumnRefExpression("emp_id"),
                        new JQuickColumnRefExpression("leader_id"))
        );
        
        JQuickHashJoinPhysicalNode join2 = new JQuickHashJoinPhysicalNode(
                JQuickJoinType.INNER,
                join1,
                projectScan,
                null,
                joinKeys2,
                JQuickHashJoinPhysicalNode.BuildSide.RIGHT,
                JQuickHashJoinPhysicalNode.JoinDistribution.LOCAL
        );
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(join2, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        // Alice(1), Bob(2), Charlie(3) 是项目负责人
        assertEquals("多表 JOIN 应该返回 3 条数据", 3, result.size());
    }
}
