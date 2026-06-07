/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Copyright (c) [2025-2099] Martin (goudingcheng@gmail.com)
 */
package com.github.paohaijiao.distribute.nodeExecutor;

import com.github.paohaijiao.datasource.JQuickDataSourceManager;
import com.github.paohaijiao.distributed.worker.*;
import com.github.paohaijiao.enums.JQuickBinaryOperator;
import com.github.paohaijiao.enums.JQuickExchangeType;
import com.github.paohaijiao.enums.JQuickJoinType;
import com.github.paohaijiao.enums.JQuickPartitionStrategy;
import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.expression.domain.JQuickBinaryExpression;
import com.github.paohaijiao.expression.domain.JQuickColumnRefExpression;
import com.github.paohaijiao.expression.domain.JQuickLiteralExpression;
import com.github.paohaijiao.function.manager.JQuickMethodInvocationManager;
import com.github.paohaijiao.physical.node.*;
import com.github.paohaijiao.proto.*;
import com.github.paohaijiao.statement.JQuickColumnMeta;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

/**
 * JQuickNodeExecutor 单元测试
 * 测试各类物理计划节点的执行逻辑
 */
public class JQuickNodeExecutorTest {

    private JQuickWorker worker;
    private JQuickNodeExecutor nodeExecutor;
    private JQuickExpressionEvaluator expressionEvaluator;
    private JQuickDataConverter dataConverter;
    private JQuickPartitionManager partitionManager;
    private JQuickWorker.JQuickTaskContext taskContext;

    @Before
    public void setUp() {
        JQuickMethodInvocationManager functionManager = JQuickMethodInvocationManager.getInstance();
        expressionEvaluator = new JQuickExpressionEvaluator(functionManager);
        dataConverter = new JQuickDataConverter();
        partitionManager = new JQuickPartitionManager();
        worker = new JQuickWorker("test-worker", 0);
        nodeExecutor = new JQuickNodeExecutor(worker, expressionEvaluator, partitionManager, dataConverter);
        JQuickExecuteTaskRequest request = JQuickExecuteTaskRequest.newBuilder()
                .setTaskId("test-task-1")
                .setQueryId("test-query-1")
                .setTaskIndex(0)
                .setTotalTasks(1)
                .setMemoryLimitBytes(1024 * 1024)
                .build();
        taskContext = worker.new JQuickTaskContext("test-task-1", request);
        JQuickDataSourceManager.clearAll();
        registerTestTables();
    }

    @After
    public void tearDown() {
        JQuickDataSourceManager.clearAll();
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
                new JQuickColumnMeta("department", String.class, "employee")
        );

        List<JQuickRow> employeeRows = Arrays.asList(
                createRow("id", 1L, "name", "张三", "age", 25, "salary", 8000.0, "department", "技术部"),
                createRow("id", 2L, "name", "李四", "age", 30, "salary", 10000.0, "department", "技术部"),
                createRow("id", 3L, "name", "王五", "age", 28, "salary", 9000.0, "department", "市场部"),
                createRow("id", 4L, "name", "赵六", "age", 35, "salary", 12000.0, "department", "市场部"),
                createRow("id", 5L, "name", "钱七", "age", 22, "salary", 6000.0, "department", "销售部")
        );
        JQuickDataSet employeeTable = new JQuickDataSet(employeeColumns, employeeRows);
        JQuickDataSourceManager.registerTable("employee", employeeTable);
        List<JQuickColumnMeta> deptColumns = Arrays.asList(
                new JQuickColumnMeta("dept_id", Long.class, "department"),
                new JQuickColumnMeta("dept_name", String.class, "department"),
                new JQuickColumnMeta("location", String.class, "department")
        );
        List<JQuickRow> deptRows = Arrays.asList(
                createRow("dept_id", 1L, "dept_name", "技术部", "location", "北京"),
                createRow("dept_id", 2L, "dept_name", "市场部", "location", "上海"),
                createRow("dept_id", 3L, "dept_name", "销售部", "location", "深圳")
        );
        JQuickDataSet deptTable = new JQuickDataSet(deptColumns, deptRows);
        JQuickDataSourceManager.registerTable("department", deptTable);
        JQuickDataSourceManager.registerTable("empty_table", JQuickDataSet.builder().build());
    }

    /**
     * 创建行的辅助方法
     */
    private JQuickRow createRow(Object... keyValues) {
        JQuickRow row = new JQuickRow();
        for (int i = 0; i < keyValues.length; i += 2) {
            row.put((String) keyValues[i], keyValues[i + 1]);
        }
        return row;
    }
    @Test
    public void testExecuteTableScan_WithAllColumns() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null);
        JQuickDataSet result = nodeExecutor.executeNode(scanNode, taskContext);
        result.printTable();
        assertNotNull(result);
        assertEquals(5, result.size());
        assertTrue(result.getColumnNames().contains("id"));
        assertTrue(result.getColumnNames().contains("name"));
        assertTrue(result.getColumnNames().contains("age"));
        assertTrue(result.getColumnNames().contains("salary"));
    }

    @Test
    public void testExecuteTableScan_WithSelectedColumns() {
        Set<String> requiredColumns = new HashSet<>(Arrays.asList("id", "name", "salary"));
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, requiredColumns, null);
        JQuickDataSet result = nodeExecutor.executeNode(scanNode, taskContext);
        result.printTable();
        assertNotNull(result);
        assertEquals(5, result.size());
        assertEquals(3, result.getColumnNames().size());
        assertTrue(result.getColumnNames().contains("id"));
        assertTrue(result.getColumnNames().contains("name"));
        assertTrue(result.getColumnNames().contains("salary"));
        assertFalse(result.getColumnNames().contains("age"));
    }

    @Test
    public void testExecuteTableScan_WithFilterPredicate() {
        JQuickExpression predicate = new JQuickBinaryExpression(new JQuickColumnRefExpression("age"), new JQuickLiteralExpression(25), JQuickBinaryOperator.GT);
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, predicate);
        JQuickDataSet result = nodeExecutor.executeNode(scanNode, taskContext);
        result.printTable();
        assertNotNull(result);
        // 年龄 > 25 的有: 李四(30)、王五(28)、赵六(35)
        assertEquals(3, result.size());
        for (JQuickRow row : result.getRows()) {
            int age = (Integer) row.get("age");
            assertTrue(age > 25);
        }
    }

    @Test
    public void testExecuteTableScan_EmptyTable() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("empty_table", null, null, null);
        JQuickDataSet result = nodeExecutor.executeNode(scanNode, taskContext);
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test(expected = RuntimeException.class)
    public void testExecuteTableScan_TableNotFound() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode(
                "non_existent_table", null, null, null
        );

        nodeExecutor.executeNode(scanNode, taskContext);
    }

    @Test
    public void testExecuteFilter() {
        // 创建 TableScan 作为输入
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null);
        // 创建 Filter: department = "技术部"
        JQuickExpression predicate = new JQuickBinaryExpression(new JQuickColumnRefExpression("department"), new JQuickLiteralExpression("技术部"), JQuickBinaryOperator.EQ);
        JQuickFilterPhysicalNode filterNode = new JQuickFilterPhysicalNode(predicate, scanNode);
        JQuickDataSet result = nodeExecutor.executeNode(filterNode, taskContext);
        result.printTable();
        assertNotNull(result);
        assertEquals(2, result.size());
        for (JQuickRow row : result.getRows()) {
            assertEquals("技术部", row.get("department"));
        }
    }

    @Test
    public void testExecuteFilter_WithAndCondition() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null);
        // 条件: department = "技术部" AND salary > 9000
        JQuickExpression condition1 = new JQuickBinaryExpression(new JQuickColumnRefExpression("department"), new JQuickLiteralExpression("技术部"), JQuickBinaryOperator.EQ);
        JQuickExpression condition2 = new JQuickBinaryExpression(new JQuickColumnRefExpression("salary"), new JQuickLiteralExpression(9000.0), JQuickBinaryOperator.GT);
        JQuickExpression predicate = new JQuickBinaryExpression(condition1, condition2, JQuickBinaryOperator.AND);
        JQuickFilterPhysicalNode filterNode = new JQuickFilterPhysicalNode(predicate, scanNode);
        JQuickDataSet result = nodeExecutor.executeNode(filterNode, taskContext);
        result.printTable();
        assertNotNull(result);
        // 只有李四符合条件（技术部，工资10000）
        assertEquals(1, result.size());
        assertEquals("李四", result.getRows().get(0).get("name"));
    }
    @Test
    public void testExecuteProject() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null);
        // 投影: name, salary, 计算 bonus (salary * 0.1)
        List<JQuickProjectPhysicalNode.SelectItem> selectItems = Arrays.asList(
                new JQuickProjectPhysicalNode.SelectItem(new JQuickColumnRefExpression("name"), "name"),
                new JQuickProjectPhysicalNode.SelectItem(new JQuickColumnRefExpression("salary"), "salary"),
                new JQuickProjectPhysicalNode.SelectItem(new JQuickBinaryExpression(new JQuickColumnRefExpression("salary"), new JQuickLiteralExpression(0.1), JQuickBinaryOperator.MULTIPLY), "bonus")
        );
        JQuickProjectPhysicalNode projectNode = new JQuickProjectPhysicalNode(selectItems, scanNode, false);
        JQuickDataSet result = nodeExecutor.executeNode(projectNode, taskContext);
        result.printTable();
        assertNotNull(result);
        assertEquals(5, result.size());
        assertEquals(3, result.getColumnNames().size());
        assertTrue(result.getColumnNames().contains("name"));
        assertTrue(result.getColumnNames().contains("salary"));
        assertTrue(result.getColumnNames().contains("bonus"));
        for (JQuickRow row : result.getRows()) {
            double salary = (Double) row.get("salary");
            double bonus = (Double) row.get("bonus");
            assertEquals(salary * 0.1, bonus, 0.001);
        }
    }

    @Test
    public void testExecuteProject_WithDistinct() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null);
        List<JQuickProjectPhysicalNode.SelectItem> selectItems = Collections.singletonList(new JQuickProjectPhysicalNode.SelectItem(new JQuickColumnRefExpression("department"), "department"));
        JQuickProjectPhysicalNode projectNode = new JQuickProjectPhysicalNode(selectItems, scanNode, true);
        JQuickDataSet result = nodeExecutor.executeNode(projectNode, taskContext);
        result.printTable();
        assertNotNull(result);
        // 去重后的部门: 技术部、市场部、销售部
        assertEquals(3, result.size());
        Set<String> departments = new HashSet<>();
        for (JQuickRow row : result.getRows()) {
            departments.add((String) row.get("department"));
        }
        assertEquals(3, departments.size());
        assertTrue(departments.contains("技术部"));
        assertTrue(departments.contains("市场部"));
        assertTrue(departments.contains("销售部"));
    }
    @Test
    public void testExecuteHashJoin_InnerJoin() {
        JQuickTableScanPhysicalNode leftScan = new JQuickTableScanPhysicalNode("employee", "e", null, null);
        JQuickTableScanPhysicalNode rightScan = new JQuickTableScanPhysicalNode("department", "d", null, null);
        // Join key: e.department = d.dept_name
        List<JQuickHashJoinPhysicalNode.JoinKeyPair> joinKeys = Collections.singletonList(new JQuickHashJoinPhysicalNode.JoinKeyPair(new JQuickColumnRefExpression("e.department"), new JQuickColumnRefExpression("d.dept_name")));
        JQuickHashJoinPhysicalNode joinNode = new JQuickHashJoinPhysicalNode(
                JQuickJoinType.INNER,
                leftScan, rightScan,
                null,
                joinKeys,
                JQuickHashJoinPhysicalNode.BuildSide.LEFT,
                JQuickHashJoinPhysicalNode.JoinDistribution.LOCAL
        );
        JQuickDataSet result = nodeExecutor.executeNode(joinNode, taskContext);
        result.printTable();
    }

    @Test
    public void testExecuteHashJoin_LeftJoin() {
        // 添加一个没有部门的员工
        List<JQuickColumnMeta> cols = Arrays.asList(
                new JQuickColumnMeta("id", Long.class, "employee"),
                new JQuickColumnMeta("name", String.class, "employee"),
                new JQuickColumnMeta("department", String.class, "employee")
        );
        List<JQuickRow> rows = Collections.singletonList(
                createRow("id", 6L, "name", "孙八", "department", "财务部")
        );
        JQuickDataSourceManager.registerTable("employee_ext", new JQuickDataSet(cols, rows));

        JQuickTableScanPhysicalNode leftScan = new JQuickTableScanPhysicalNode(
                "employee_ext", "e", null, null
        );
        JQuickTableScanPhysicalNode rightScan = new JQuickTableScanPhysicalNode(
                "department", "d", null, null
        );

        List<JQuickHashJoinPhysicalNode.JoinKeyPair> joinKeys = Collections.singletonList(
                new JQuickHashJoinPhysicalNode.JoinKeyPair(
                        new JQuickColumnRefExpression("e.department"),
                        new JQuickColumnRefExpression("d.dept_name")
                )
        );

        JQuickHashJoinPhysicalNode joinNode = new JQuickHashJoinPhysicalNode(
                JQuickJoinType.LEFT,
                leftScan, rightScan,
                null,
                joinKeys,
                JQuickHashJoinPhysicalNode.BuildSide.LEFT,
                JQuickHashJoinPhysicalNode.JoinDistribution.LOCAL
        );

        JQuickDataSet result = nodeExecutor.executeNode(joinNode, taskContext);

        assertNotNull(result);
        assertEquals(1, result.size());

        JQuickRow row = result.getRows().get(0);
        assertEquals("孙八", row.get("e.name"));
        assertNull(row.get("d.location"));  // 右表字段为 null
    }

    // ==================== HashAggregate 测试 ====================

    @Test
    public void testExecuteHashAggregate_GroupBy() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode(
                "employee", null, null, null
        );

        // 分组键: department
        List<JQuickExpression> groupKeys = Collections.singletonList(
                new JQuickColumnRefExpression("department")
        );

        // 聚合函数: COUNT(*), AVG(salary), MAX(age)
        List<JQuickHashAggregatePhysicalNode.AggregateFunction> aggregates = Arrays.asList(
                new JQuickHashAggregatePhysicalNode.AggregateFunction("count", null, false, "emp_count"),
                new JQuickHashAggregatePhysicalNode.AggregateFunction(
                        "avg", new JQuickColumnRefExpression("salary"), false, "avg_salary"),
                new JQuickHashAggregatePhysicalNode.AggregateFunction(
                        "max", new JQuickColumnRefExpression("age"), false, "max_age")
        );

        JQuickHashAggregatePhysicalNode aggNode = new JQuickHashAggregatePhysicalNode(
                groupKeys, aggregates, scanNode, null,
                JQuickHashAggregatePhysicalNode.AggregateStage.SINGLE
        );

        JQuickDataSet result = nodeExecutor.executeNode(aggNode, taskContext);

        assertNotNull(result);
        assertEquals(3, result.size());  // 三个部门

        // 验证聚合结果
        for (JQuickRow row : result.getRows()) {
            String dept = (String) row.get("department");
            Long count = (Long) row.get("emp_count");
            Double avgSalary = (Double) row.get("avg_salary");

            if ("技术部".equals(dept)) {
                assertEquals(2L, count.longValue());
                assertEquals(9000.0, avgSalary, 0.001);
            } else if ("市场部".equals(dept)) {
                assertEquals(2L, count.longValue());
                assertEquals(10500.0, avgSalary, 0.001);
            } else if ("销售部".equals(dept)) {
                assertEquals(1L, count.longValue());
                assertEquals(6000.0, avgSalary, 0.001);
            }
        }
    }

    @Test
    public void testExecuteHashAggregate_GlobalAggregate() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode(
                "employee", null, null, null
        );

        // 无分组键 - 全局聚合
        List<JQuickHashAggregatePhysicalNode.AggregateFunction> aggregates = Arrays.asList(
                new JQuickHashAggregatePhysicalNode.AggregateFunction("count", null, false, "total_count"),
                new JQuickHashAggregatePhysicalNode.AggregateFunction(
                        "avg", new JQuickColumnRefExpression("salary"), false, "avg_salary"),
                new JQuickHashAggregatePhysicalNode.AggregateFunction(
                        "sum", new JQuickColumnRefExpression("salary"), false, "total_salary")
        );

        JQuickHashAggregatePhysicalNode aggNode = new JQuickHashAggregatePhysicalNode(
                Collections.emptyList(), aggregates, scanNode, null,
                JQuickHashAggregatePhysicalNode.AggregateStage.SINGLE
        );

        JQuickDataSet result = nodeExecutor.executeNode(aggNode, taskContext);

        assertNotNull(result);
        assertEquals(1, result.size());

        JQuickRow row = result.getRows().get(0);
        assertEquals(5L, row.get("total_count"));
        assertEquals(9000.0, (Double) row.get("avg_salary"), 0.001);
        assertEquals(45000.0, (Double) row.get("total_salary"), 0.001);
    }

    @Test
    public void testExecuteHashAggregate_WithHaving() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode(
                "employee", null, null, null
        );

        List<JQuickExpression> groupKeys = Collections.singletonList(
                new JQuickColumnRefExpression("department")
        );

        List<JQuickHashAggregatePhysicalNode.AggregateFunction> aggregates = Collections.singletonList(
                new JQuickHashAggregatePhysicalNode.AggregateFunction("count", null, false, "emp_count")
        );

        // Having: COUNT(*) > 1
        JQuickExpression havingCondition = new JQuickBinaryExpression(
                new JQuickColumnRefExpression("emp_count"),
                new JQuickLiteralExpression(1L),
                JQuickBinaryOperator.GT
        );

        JQuickHashAggregatePhysicalNode aggNode = new JQuickHashAggregatePhysicalNode(
                groupKeys, aggregates, scanNode, havingCondition,
                JQuickHashAggregatePhysicalNode.AggregateStage.SINGLE
        );

        JQuickDataSet result = nodeExecutor.executeNode(aggNode, taskContext);

        assertNotNull(result);
        // 员工数 > 1 的部门: 技术部(2), 市场部(2)
        assertEquals(2, result.size());

        Set<String> depts = new HashSet<>();
        for (JQuickRow row : result.getRows()) {
            depts.add((String) row.get("department"));
        }
        assertTrue(depts.contains("技术部"));
        assertTrue(depts.contains("市场部"));
        assertFalse(depts.contains("销售部"));
    }

    // ==================== Sort 测试 ====================

    @Test
    public void testExecuteSort_Ascending() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode(
                "employee", null, null, null
        );

        List<JQuickSortPhysicalNode.OrderByItem> orderByItems = Collections.singletonList(
                new JQuickSortPhysicalNode.OrderByItem("age", true)  // 升序
        );

        JQuickSortPhysicalNode sortNode = new JQuickSortPhysicalNode(orderByItems, scanNode);

        JQuickDataSet result = nodeExecutor.executeNode(sortNode, taskContext);

        assertNotNull(result);
        assertEquals(5, result.size());

        // 验证升序
        int prevAge = -1;
        for (JQuickRow row : result.getRows()) {
            int age = (Integer) row.get("age");
            assertTrue(age >= prevAge);
            prevAge = age;
        }
    }

    @Test
    public void testExecuteSort_Descending() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode(
                "employee", null, null, null
        );

        List<JQuickSortPhysicalNode.OrderByItem> orderByItems = Collections.singletonList(
                new JQuickSortPhysicalNode.OrderByItem("salary", false)  // 降序
        );

        JQuickSortPhysicalNode sortNode = new JQuickSortPhysicalNode(orderByItems, scanNode);

        JQuickDataSet result = nodeExecutor.executeNode(sortNode, taskContext);

        assertNotNull(result);
        assertEquals(5, result.size());

        // 验证降序
        double prevSalary = Double.MAX_VALUE;
        for (JQuickRow row : result.getRows()) {
            double salary = (Double) row.get("salary");
            assertTrue(salary <= prevSalary);
            prevSalary = salary;
        }

        // 第一个应该是最高工资 12000
        assertEquals(12000.0, (Double) result.getRows().get(0).get("salary"), 0.001);
    }

    @Test
    public void testExecuteSort_MultipleColumns() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode(
                "employee", null, null, null
        );

        List<JQuickSortPhysicalNode.OrderByItem> orderByItems = Arrays.asList(
                new JQuickSortPhysicalNode.OrderByItem("department", true),
                new JQuickSortPhysicalNode.OrderByItem("salary", false)
        );

        JQuickSortPhysicalNode sortNode = new JQuickSortPhysicalNode(orderByItems, scanNode);

        JQuickDataSet result = nodeExecutor.executeNode(sortNode, taskContext);

        assertNotNull(result);
        assertEquals(5, result.size());

        // 验证先按部门排序，部门内按工资降序
        String lastDept = null;
        double lastSalary = Double.MAX_VALUE;
        for (JQuickRow row : result.getRows()) {
            String dept = (String) row.get("department");
            double salary = (Double) row.get("salary");

            if (lastDept != null && dept.equals(lastDept)) {
                assertTrue(salary <= lastSalary);
            }
            lastDept = dept;
            lastSalary = salary;
        }
    }

    // ==================== Limit 测试 ====================

    @Test
    public void testExecuteLimit() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode(
                "employee", null, null, null
        );

        JQuickLimitPhysicalNode limitNode = new JQuickLimitPhysicalNode(3, 0, scanNode);

        JQuickDataSet result = nodeExecutor.executeNode(limitNode, taskContext);

        assertNotNull(result);
        assertEquals(3, result.size());
    }

    @Test
    public void testExecuteLimit_WithOffset() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode(
                "employee", null, null, null
        );

        // offset = 2, limit = 2
        JQuickLimitPhysicalNode limitNode = new JQuickLimitPhysicalNode(2, 2, scanNode);

        JQuickDataSet result = nodeExecutor.executeNode(limitNode, taskContext);

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    public void testExecuteLimit_OffsetExceedsSize() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode(
                "employee", null, null, null
        );

        JQuickLimitPhysicalNode limitNode = new JQuickLimitPhysicalNode(10, 100, scanNode);

        JQuickDataSet result = nodeExecutor.executeNode(limitNode, taskContext);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    // ==================== TopN 测试 ====================

    @Test
    public void testExecuteTopN() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode(
                "employee", null, null, null
        );

        List<JQuickSortPhysicalNode.OrderByItem> orderByItems = Collections.singletonList(
                new JQuickSortPhysicalNode.OrderByItem("salary", false)
        );

        JQuickTopNPhysicalNode topNNode = new JQuickTopNPhysicalNode(orderByItems, 3, 0, scanNode);

        JQuickDataSet result = nodeExecutor.executeNode(topNNode, taskContext);

        assertNotNull(result);
        assertEquals(3, result.size());

        // 工资前三: 12000, 10000, 9000
        assertEquals(12000.0, (Double) result.getRows().get(0).get("salary"), 0.001);
        assertEquals(10000.0, (Double) result.getRows().get(1).get("salary"), 0.001);
        assertEquals(9000.0, (Double) result.getRows().get(2).get("salary"), 0.001);
    }

    @Test
    public void testExecuteTopN_WithOffset() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode(
                "employee", null, null, null
        );

        List<JQuickSortPhysicalNode.OrderByItem> orderByItems = Collections.singletonList(
                new JQuickSortPhysicalNode.OrderByItem("salary", false)
        );

        // 跳过第1名，取第2、3名
        JQuickTopNPhysicalNode topNNode = new JQuickTopNPhysicalNode(orderByItems, 2, 1, scanNode);

        JQuickDataSet result = nodeExecutor.executeNode(topNNode, taskContext);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(10000.0, (Double) result.getRows().get(0).get("salary"), 0.001);
        assertEquals(9000.0, (Double) result.getRows().get(1).get("salary"), 0.001);
    }

    // ==================== Values 测试 ====================

    @Test
    public void testExecuteValues() {
        List<List<Object>> rows = Arrays.asList(
                Arrays.asList(1, "Alice", 25),
                Arrays.asList(2, "Bob", 30),
                Arrays.asList(3, "Charlie", 35)
        );

        List<String> columnNames = Arrays.asList("id", "name", "age");
        List<Class<?>> columnTypes = Arrays.asList(Integer.class, String.class, Integer.class);

        JQuickValuesPhysicalNode valuesNode = new JQuickValuesPhysicalNode(rows, columnNames, columnTypes);

        JQuickDataSet result = nodeExecutor.executeNode(valuesNode, taskContext);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(3, result.getColumnNames().size());

        JQuickRow firstRow = result.getRows().get(0);
        assertEquals(1, firstRow.get("id"));
        assertEquals("Alice", firstRow.get("name"));
        assertEquals(25, firstRow.get("age"));
    }

    @Test
    public void testExecuteValues_Empty() {
        JQuickValuesPhysicalNode valuesNode = new JQuickValuesPhysicalNode(
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList()
        );

        JQuickDataSet result = nodeExecutor.executeNode(valuesNode, taskContext);
        assertNotNull(result);
        assertEquals(0, result.size());
    }




    private JQuickValuesPhysicalNode createValuesNode(List<Integer> ids, List<String> names) {
        List<List<Object>> rows = new ArrayList<>();
        for (int i = 0; i < ids.size(); i++) {
            rows.add(Arrays.asList(ids.get(i)));
        }
        return new JQuickValuesPhysicalNode(rows,
                Arrays.asList("id", "name"),
                Arrays.asList(Integer.class, String.class));
    }


    @Test
    public void testExecuteExchange_HashPartition() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode(
                "employee", null, null, null
        );

        List<JQuickExpression> partitionKeys = Collections.singletonList(
                new JQuickColumnRefExpression("department")
        );

        JQuickExchangePhysicalNode exchangeNode = new JQuickExchangePhysicalNode(
                JQuickExchangeType.SHUFFLE,
                JQuickPartitionStrategy.HASH,
                partitionKeys,
                3,
                scanNode
        );

        // 执行 exchange 应该返回空结果（数据被发送到其他 worker）
        JQuickDataSet result = nodeExecutor.executeNode(exchangeNode, taskContext);

        assertNotNull(result);
        // Exchange 节点执行后返回空 DataSet
        assertEquals(0, result.size());
    }

    @Test
    public void testExpressionEvaluation_ColumnRef() {
        JQuickRow row = createRow("name", "张三", "age", 25);

        JQuickColumnRefExpression expr = new JQuickColumnRefExpression("name");
        Object result = expressionEvaluator.evaluateExpression(row, expr);

        assertEquals("张三", result);
    }

    @Test
    public void testExpressionEvaluation_Literal() {
        JQuickRow row = new JQuickRow();

        JQuickLiteralExpression expr = new JQuickLiteralExpression(100);
        Object result = expressionEvaluator.evaluateExpression(row, expr);

        assertEquals(100, result);
    }

    @Test
    public void testExpressionEvaluation_BinaryOperator() {
        JQuickRow row = createRow("a", 10, "b", 5);

        JQuickBinaryExpression plusExpr = new JQuickBinaryExpression(
                new JQuickColumnRefExpression("a"),
                new JQuickColumnRefExpression("b"),
                JQuickBinaryOperator.PLUS
        );

        Object result = expressionEvaluator.evaluateExpression(row, plusExpr);
        assertEquals(15.0, result);
    }

    @Test
    public void testExpressionEvaluation_Comparison() {
        JQuickRow row = createRow("salary", 8000);

        JQuickBinaryExpression gtExpr = new JQuickBinaryExpression(
                new JQuickColumnRefExpression("salary"),
                new JQuickLiteralExpression(5000),
                JQuickBinaryOperator.GT
        );

        Object result = expressionEvaluator.evaluateExpression(row, gtExpr);
        assertEquals(true, result);
    }

    // ==================== 边界条件和异常测试 ====================

    @Test
    public void testExecuteNode_NullNode() {
        JQuickDataSet result = nodeExecutor.executeNode(null, taskContext);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    public void testExecuteNode_EmptyNode() {
        JQuickDataSet result = nodeExecutor.executeNode(JQuickEmptyPhysicalNode.INSTANCE, taskContext);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    public void testExecuteNode_FilterWithNoMatchingRows() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode(
                "employee", null, null, null
        );

        // 不可能满足的条件: age > 100
        JQuickExpression predicate = new JQuickBinaryExpression(
                new JQuickColumnRefExpression("age"),
                new JQuickLiteralExpression(100),
                JQuickBinaryOperator.GT
        );

        JQuickFilterPhysicalNode filterNode = new JQuickFilterPhysicalNode(predicate, scanNode);

        JQuickDataSet result = nodeExecutor.executeNode(filterNode, taskContext);

        assertNotNull(result);
        assertEquals(0, result.size());
    }

    @Test
    public void testExecuteProject_WithNullValues() {
        // 创建包含 null 值的表
        List<JQuickColumnMeta> cols = Collections.singletonList(
                new JQuickColumnMeta("nullable_col", String.class, "test")
        );
        List<JQuickRow> rows = Arrays.asList(
                createRow("nullable_col", "value1"),
                createRow("nullable_col", null),
                createRow("nullable_col", "value3")
        );
        JQuickDataSourceManager.registerTable("test_null", new JQuickDataSet(cols, rows));

        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode(
                "test_null", null, null, null
        );

        List<JQuickProjectPhysicalNode.SelectItem> selectItems = Collections.singletonList(
                new JQuickProjectPhysicalNode.SelectItem(
                        new JQuickColumnRefExpression("nullable_col"), "nullable_col")
        );

        JQuickProjectPhysicalNode projectNode = new JQuickProjectPhysicalNode(
                selectItems, scanNode, false
        );

        JQuickDataSet result = nodeExecutor.executeNode(projectNode, taskContext);

        assertNotNull(result);
        assertEquals(3, result.size());

        // 验证 null 值保持不变
        assertNull(result.getRows().get(1).get("nullable_col"));
    }


    @Test
    public void testTaskContext_ProcessedRowsTracking() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode(
                "employee", null, null, null
        );

        nodeExecutor.executeNode(scanNode, taskContext);

        assertEquals(5, taskContext.getProcessedRows());
    }

    @Test
    public void testTaskContext_ExecutionTime() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode(
                "employee", null, null, null
        );

        nodeExecutor.executeNode(scanNode, taskContext);

        assertTrue(taskContext.getExecutionTimeMs() >= 0);
    }
}
