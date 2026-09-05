package com.github.paohaijiao.distribute.nodeExecutor.executeNode.setoperation;

import com.github.paohaijiao.datasource.JQuickDataSourceManager;
import com.github.paohaijiao.distributed.worker.*;
import com.github.paohaijiao.enums.JQuickSQLOperationType;
import com.github.paohaijiao.function.manager.JQuickMethodInvocationManager;
import com.github.paohaijiao.physical.node.JQuickSetOperationPhysicalNode;
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
 * JQuickSetOperationPhysicalNode 测试
 * 
 * 测试范围：
 * 1. UNION - 去重合并
 * 2. UNION_ALL - 不去重合并
 * 3. INTERSECT - 交集
 * 4. EXCEPT - 差集
 * 5. 多表组合
 * 6. 与 Filter 组合
 */
public class JQuickSetOperationPhysicalNodeTest {

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
        // employee_a 表 - 部门 A 的员工
        List<JQuickColumnMeta> employeeColumns = Arrays.asList(
                new JQuickColumnMeta("emp_id", Long.class, "employee_a"),
                new JQuickColumnMeta("emp_name", String.class, "employee_a"),
                new JQuickColumnMeta("dept_id", Long.class, "employee_a"),
                new JQuickColumnMeta("salary", Double.class, "employee_a")
        );
        List<JQuickRow> employeeARows = new ArrayList<>();
        employeeARows.add(createRow(employeeColumns, new Object[]{1L, "Alice", 1L, 8000.0}));
        employeeARows.add(createRow(employeeColumns, new Object[]{2L, "Bob", 1L, 10000.0}));
        employeeARows.add(createRow(employeeColumns, new Object[]{3L, "Charlie", 2L, 12000.0}));
        employeeARows.add(createRow(employeeColumns, new Object[]{4L, "David", 2L, 11000.0}));
        JQuickDataSet employeeAData = new JQuickDataSet(employeeColumns, employeeARows);
        JQuickDataSourceManager.registerTable("employee_a", employeeAData);

        // employee_b 表 - 部门 B 的员工（有部分重复）
        List<JQuickColumnMeta> employeeBColumns = Arrays.asList(
                new JQuickColumnMeta("emp_id", Long.class, "employee_b"),
                new JQuickColumnMeta("emp_name", String.class, "employee_b"),
                new JQuickColumnMeta("dept_id", Long.class, "employee_b"),
                new JQuickColumnMeta("salary", Double.class, "employee_b")
        );
        List<JQuickRow> employeeBRows = new ArrayList<>();
        employeeBRows.add(createRow(employeeBColumns, new Object[]{3L, "Charlie", 2L, 12000.0})); // 重复
        employeeBRows.add(createRow(employeeBColumns, new Object[]{4L, "David", 2L, 11000.0})); // 重复
        employeeBRows.add(createRow(employeeBColumns, new Object[]{5L, "Eve", 3L, 9000.0}));
        employeeBRows.add(createRow(employeeBColumns, new Object[]{6L, "Frank", 3L, 7500.0}));
        JQuickDataSet employeeBData = new JQuickDataSet(employeeBColumns, employeeBRows);
        JQuickDataSourceManager.registerTable("employee_b", employeeBData);

        // employee_c 表 - 部门 C 的员工
        List<JQuickColumnMeta> employeeCColumns = Arrays.asList(
                new JQuickColumnMeta("emp_id", Long.class, "employee_c"),
                new JQuickColumnMeta("emp_name", String.class, "employee_c"),
                new JQuickColumnMeta("dept_id", Long.class, "employee_c"),
                new JQuickColumnMeta("salary", Double.class, "employee_c")
        );
        List<JQuickRow> employeeCRows = new ArrayList<>();
        employeeCRows.add(createRow(employeeCColumns, new Object[]{5L, "Eve", 3L, 9000.0})); // 与 employee_b 重复
        employeeCRows.add(createRow(employeeCColumns, new Object[]{6L, "Frank", 3L, 7500.0})); // 与 employee_b 重复
        employeeCRows.add(createRow(employeeCColumns, new Object[]{7L, "Grace", 4L, 9500.0}));
        employeeCRows.add(createRow(employeeCColumns, new Object[]{8L, "Henry", 4L, 10500.0}));
        JQuickDataSet employeeCData = new JQuickDataSet(employeeCColumns, employeeCRows);
        JQuickDataSourceManager.registerTable("employee_c", employeeCData);

        // manager 表 - 经理表
        List<JQuickColumnMeta> managerColumns = Arrays.asList(
                new JQuickColumnMeta("emp_id", Long.class, "manager"),
                new JQuickColumnMeta("emp_name", String.class, "manager"),
                new JQuickColumnMeta("dept_id", Long.class, "manager"),
                new JQuickColumnMeta("salary", Double.class, "manager")
        );
        List<JQuickRow> managerRows = new ArrayList<>();
        managerRows.add(createRow(managerColumns, new Object[]{2L, "Bob", 1L, 10000.0})); // 与 employee_a 重复
        managerRows.add(createRow(managerColumns, new Object[]{3L, "Charlie", 2L, 12000.0})); // 与 employee_a 重复
        managerRows.add(createRow(managerColumns, new Object[]{9L, "Ivy", 5L, 13000.0}));
        JQuickDataSet managerData = new JQuickDataSet(managerColumns, managerRows);
        JQuickDataSourceManager.registerTable("manager", managerData);
    }

    private JQuickRow createRow(List<JQuickColumnMeta> columns, Object[] values) {
        JQuickRow row = new JQuickRow();
        for (int i = 0; i < columns.size(); i++) {
            row.put(columns.get(i).getName(), values[i]);
        }
        return row;
    }

    /**
     * 测试 UNION 操作
     * 
     * 场景：合并 employee_a 和 employee_b，去重
     * SQL: SELECT * FROM employee_a UNION SELECT * FROM employee_b
     * 预期：返回 6 条数据（去重后的合并结果）
     */
    @Test
    public void testUnion() {
        JQuickTableScanPhysicalNode leftScan = new JQuickTableScanPhysicalNode("employee_a", null, null, null, null);
        JQuickTableScanPhysicalNode rightScan = new JQuickTableScanPhysicalNode("employee_b", null, null, null, null);
        JQuickSetOperationPhysicalNode unionNode = new JQuickSetOperationPhysicalNode(
            JQuickSQLOperationType.UNION,
            leftScan,
            rightScan
        );
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(unionNode, context);
        assertNotNull("结果不应为 null", result);
        assertEquals("UNION 应该返回 6 条数据（去重后）", 6, result.size());
        Set<String> empNames = new HashSet<>();
        for (JQuickRow row : result.getRows()) {
            empNames.add((String) row.get("emp_name"));
        }
        assertTrue("应该包含 Alice", empNames.contains("Alice"));
        assertTrue("应该包含 Bob", empNames.contains("Bob"));
        assertTrue("应该包含 Charlie", empNames.contains("Charlie"));
        assertTrue("应该包含 David", empNames.contains("David"));
        assertTrue("应该包含 Eve", empNames.contains("Eve"));
        assertTrue("应该包含 Frank", empNames.contains("Frank"));
    }

    /**
     * 测试 UNION_ALL 操作
     * 
     * 场景：合并 employee_a 和 employee_b，不去重
     * SQL: SELECT * FROM employee_a UNION ALL SELECT * FROM employee_b
     * 预期：返回 8 条数据（包含重复）
     */
    @Test
    public void testUnionAll() {
        JQuickTableScanPhysicalNode leftScan = new JQuickTableScanPhysicalNode("employee_a", null, null, null, null);
        JQuickTableScanPhysicalNode rightScan = new JQuickTableScanPhysicalNode("employee_b", null, null, null, null);
        JQuickSetOperationPhysicalNode unionAllNode = new JQuickSetOperationPhysicalNode(
            JQuickSQLOperationType.UNION_ALL,
            leftScan,
            rightScan
        );
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(unionAllNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("UNION ALL 应该返回 8 条数据（包含重复）", 8, result.size());
        int charlieCount = 0;
        int davidCount = 0;
        for (JQuickRow row : result.getRows()) {
            String empName = (String) row.get("emp_name");
            if ("Charlie".equals(empName)) {
                charlieCount++;
            }
            if ("David".equals(empName)) {
                davidCount++;
            }
        }
        assertEquals("Charlie 应该出现 2 次", 2, charlieCount);
        assertEquals("David 应该出现 2 次", 2, davidCount);
    }

    /**
     * 测试 INTERSECT 操作
     * 
     * 场景：获取 employee_a 和 employee_b 的交集
     * SQL: SELECT * FROM employee_a INTERSECT SELECT * FROM employee_b
     * 预期：返回 2 条数据（Charlie 和 David）
     */
    @Test
    public void testIntersect() {
        JQuickTableScanPhysicalNode leftScan = new JQuickTableScanPhysicalNode("employee_a", null, null, null, null);
        
        JQuickTableScanPhysicalNode rightScan = new JQuickTableScanPhysicalNode("employee_b", null, null, null, null);
        JQuickSetOperationPhysicalNode intersectNode = new JQuickSetOperationPhysicalNode(
            JQuickSQLOperationType.INTERSECT,
            leftScan,
            rightScan
        );
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(intersectNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("INTERSECT 应该返回 2 条数据", 2, result.size());
        Set<String> empNames = new HashSet<>();
        for (JQuickRow row : result.getRows()) {
            empNames.add((String) row.get("emp_name"));
        }
        
        assertTrue("应该包含 Charlie", empNames.contains("Charlie"));
        assertTrue("应该包含 David", empNames.contains("David"));
        assertEquals("应该只包含 2 个员工", 2, empNames.size());
    }

    /**
     * 测试 EXCEPT 操作
     * 
     * 场景：获取 employee_a 中有但 employee_b 中没有的员工
     * SQL: SELECT * FROM employee_a EXCEPT SELECT * FROM employee_b
     * 预期：返回 2 条数据（Alice 和 Bob）
     */
    @Test
    public void testExcept() {
        JQuickTableScanPhysicalNode leftScan = new JQuickTableScanPhysicalNode("employee_a", null, null, null, null);
        JQuickTableScanPhysicalNode rightScan = new JQuickTableScanPhysicalNode("employee_b", null, null, null, null);
        JQuickSetOperationPhysicalNode exceptNode = new JQuickSetOperationPhysicalNode(
            JQuickSQLOperationType.EXCEPT,
            leftScan,
            rightScan
        );
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(exceptNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("EXCEPT 应该返回 2 条数据", 2, result.size());
        Set<String> empNames = new HashSet<>();
        for (JQuickRow row : result.getRows()) {
            empNames.add((String) row.get("emp_name"));
        }
        assertTrue("应该包含 Alice", empNames.contains("Alice"));
        assertTrue("应该包含 Bob", empNames.contains("Bob"));
        assertEquals("应该只包含 2 个员工", 2, empNames.size());
    }

    /**
     * 测试反向 EXCEPT 操作
     * 
     * 场景：获取 employee_b 中有但 employee_a 中没有的员工
     * SQL: SELECT * FROM employee_b EXCEPT SELECT * FROM employee_a
     * 预期：返回 2 条数据（Eve 和 Frank）
     */
    @Test
    public void testExceptReverse() {
        JQuickTableScanPhysicalNode leftScan = new JQuickTableScanPhysicalNode("employee_b", null, null, null, null);
        JQuickTableScanPhysicalNode rightScan = new JQuickTableScanPhysicalNode("employee_a", null, null, null, null);
        JQuickSetOperationPhysicalNode exceptNode = new JQuickSetOperationPhysicalNode(
            JQuickSQLOperationType.EXCEPT,
            leftScan,
            rightScan
        );
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(exceptNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("反向 EXCEPT 应该返回 2 条数据", 2, result.size());
        Set<String> empNames = new HashSet<>();
        for (JQuickRow row : result.getRows()) {
            empNames.add((String) row.get("emp_name"));
        }
        
        assertTrue("应该包含 Eve", empNames.contains("Eve"));
        assertTrue("应该包含 Frank", empNames.contains("Frank"));
        assertEquals("应该只包含 2 个员工", 2, empNames.size());
    }

    /**
     * 测试多表 UNION
     * 
     * 场景：合并 employee_a、employee_b 和 employee_c
     * SQL: SELECT * FROM employee_a UNION SELECT * FROM employee_b UNION SELECT * FROM employee_c
     * 预期：返回 8 条数据（所有员工，去重）
     */
    @Test
    public void testMultipleUnion() {
        JQuickTableScanPhysicalNode leftScan = new JQuickTableScanPhysicalNode("employee_a", null, null, null, null);
        JQuickTableScanPhysicalNode rightScan = new JQuickTableScanPhysicalNode("employee_b", null, null, null, null);
        JQuickSetOperationPhysicalNode firstUnion = new JQuickSetOperationPhysicalNode(
            JQuickSQLOperationType.UNION,
            leftScan,
            rightScan
        );
        JQuickTableScanPhysicalNode thirdScan = new JQuickTableScanPhysicalNode("employee_c", null, null, null, null);
        JQuickSetOperationPhysicalNode secondUnion = new JQuickSetOperationPhysicalNode(
            JQuickSQLOperationType.UNION,
            firstUnion,
            thirdScan
        );
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(secondUnion, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("多表 UNION 应该返回 8 条数据", 8, result.size());
        Set<String> empNames = new HashSet<>();
        for (JQuickRow row : result.getRows()) {
            empNames.add((String) row.get("emp_name"));
        }
        
        assertTrue("应该包含 Alice", empNames.contains("Alice"));
        assertTrue("应该包含 Bob", empNames.contains("Bob"));
        assertTrue("应该包含 Charlie", empNames.contains("Charlie"));
        assertTrue("应该包含 David", empNames.contains("David"));
        assertTrue("应该包含 Eve", empNames.contains("Eve"));
        assertTrue("应该包含 Frank", empNames.contains("Frank"));
        assertTrue("应该包含 Grace", empNames.contains("Grace"));
        assertTrue("应该包含 Henry", empNames.contains("Henry"));
    }

    /**
     * 测试 UNION 和 INTERSECT 组合
     * 
     * 场景：先 UNION 再 INTERSECT
     * SQL: (SELECT * FROM employee_a UNION SELECT * FROM employee_b) INTERSECT SELECT * FROM manager
     * 预期：返回 2 条数据（Bob 和 Charlie，既是员工又是经理）
     */
    @Test
    public void testUnionAndIntersect() {
        JQuickTableScanPhysicalNode leftScan = new JQuickTableScanPhysicalNode("employee_a", null, null, null, null);
        JQuickTableScanPhysicalNode rightScan = new JQuickTableScanPhysicalNode("employee_b", null, null, null, null);
        JQuickSetOperationPhysicalNode unionNode = new JQuickSetOperationPhysicalNode(
            JQuickSQLOperationType.UNION,
            leftScan,
            rightScan
        );
        JQuickTableScanPhysicalNode managerScan = new JQuickTableScanPhysicalNode("manager", null, null, null, null);
        JQuickSetOperationPhysicalNode intersectNode = new JQuickSetOperationPhysicalNode(
            JQuickSQLOperationType.INTERSECT,
            unionNode,
            managerScan
        );
        
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(intersectNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("UNION 和 INTERSECT 组合应该返回 2 条数据", 2, result.size());
        Set<String> empNames = new HashSet<>();
        for (JQuickRow row : result.getRows()) {
            empNames.add((String) row.get("emp_name"));
        }
        assertTrue("应该包含 Bob", empNames.contains("Bob"));
        assertTrue("应该包含 Charlie", empNames.contains("Charlie"));
        assertEquals("应该只包含 2 个员工", 2, empNames.size());
    }

    /**
     * 测试 UNION ALL 和 EXCEPT 组合
     * 
     * 场景：先 UNION ALL 再 EXCEPT
     * SQL: (SELECT * FROM employee_a UNION ALL SELECT * FROM employee_b) EXCEPT SELECT * FROM manager
     * 预期：返回 4 条数据（Alice, Eve, Frank, 以及一个重复的 Charlie 或 David）
     */
    @Test
    public void testUnionAllAndExcept() {
        JQuickTableScanPhysicalNode leftScan = new JQuickTableScanPhysicalNode("employee_a", null, null, null, null);
        JQuickTableScanPhysicalNode rightScan = new JQuickTableScanPhysicalNode("employee_b", null, null, null, null);
        JQuickSetOperationPhysicalNode unionAllNode = new JQuickSetOperationPhysicalNode(
            JQuickSQLOperationType.UNION_ALL,
            leftScan,
            rightScan
        );
        JQuickTableScanPhysicalNode managerScan = new JQuickTableScanPhysicalNode("manager", null, null, null, null);
        JQuickSetOperationPhysicalNode exceptNode = new JQuickSetOperationPhysicalNode(
            JQuickSQLOperationType.EXCEPT,
            unionAllNode,
            managerScan
        );
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(exceptNode, context);
        result.printTable();
    }

    /**
     * 测试空表 UNION
     * 
     * 场景：空表与非空表 UNION
     * 预期：返回非空表的所有数据
     */
    @Test
    public void testUnionWithEmptyTable() {
        List<JQuickColumnMeta> emptyColumns = Arrays.asList(
                new JQuickColumnMeta("emp_id", Long.class, "empty_table"),
                new JQuickColumnMeta("emp_name", String.class, "empty_table"),
                new JQuickColumnMeta("dept_id", Long.class, "empty_table"),
                new JQuickColumnMeta("salary", Double.class, "empty_table")
        );
        JQuickDataSet emptyData = new JQuickDataSet(emptyColumns, new ArrayList<>());
        JQuickDataSourceManager.registerTable("empty_table", emptyData);
        JQuickTableScanPhysicalNode leftScan = new JQuickTableScanPhysicalNode("empty_table", null, null, null, null);
        JQuickTableScanPhysicalNode rightScan = new JQuickTableScanPhysicalNode("employee_a", null, null, null, null);
        JQuickSetOperationPhysicalNode unionNode = new JQuickSetOperationPhysicalNode(
            JQuickSQLOperationType.UNION,
            leftScan,
            rightScan
        );
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(unionNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("空表 UNION 应该返回 4 条数据", 4, result.size());
    }

    /**
     * 测试 INTERSECT 无交集
     * 
     * 场景：两个无交集的表进行 INTERSECT
     * 预期：返回 0 条数据
     */
    @Test
    public void testIntersectNoCommon() {
        JQuickTableScanPhysicalNode leftScan = new JQuickTableScanPhysicalNode("employee_a", null, null, null, null);
        JQuickTableScanPhysicalNode rightScan = new JQuickTableScanPhysicalNode("employee_c", null, null, null, null);
        JQuickSetOperationPhysicalNode intersectNode = new JQuickSetOperationPhysicalNode(
            JQuickSQLOperationType.INTERSECT,
            leftScan,
            rightScan
        );
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(intersectNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("无交集的 INTERSECT 应该返回 0 条数据", 0, result.size());
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
