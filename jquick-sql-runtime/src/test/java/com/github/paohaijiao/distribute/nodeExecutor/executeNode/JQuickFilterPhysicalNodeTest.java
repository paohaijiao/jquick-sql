package com.github.paohaijiao.distribute.nodeExecutor.executeNode;

import com.github.paohaijiao.datasource.JQuickDataSourceManager;
import com.github.paohaijiao.distributed.worker.*;
import com.github.paohaijiao.enums.JQuickBinaryOperator;
import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.expression.domain.JQuickBinaryExpression;
import com.github.paohaijiao.expression.domain.JQuickColumnRefExpression;
import com.github.paohaijiao.expression.domain.JQuickLiteralExpression;
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

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class JQuickFilterPhysicalNodeTest {

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

}
