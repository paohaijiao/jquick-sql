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
public class JQuickTableScanPhysicalNodeTest {

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
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", "e", null, null);
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
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", "e", requiredColumns, null);
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
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("non_existent_table", null, null, null);
        nodeExecutor.executeNode(scanNode, taskContext);
    }
}