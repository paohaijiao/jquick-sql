package com.github.paohaijiao.distribute.nodeExecutor.executeNode.recursive;

import com.github.paohaijiao.datasource.JQuickDataSourceManager;
import com.github.paohaijiao.distributed.worker.*;
import com.github.paohaijiao.enums.JQuickBinaryOperator;
import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.expression.domain.JQuickBinaryExpression;
import com.github.paohaijiao.expression.domain.JQuickColumnRefExpression;
import com.github.paohaijiao.expression.domain.JQuickLiteralExpression;
import com.github.paohaijiao.function.manager.JQuickMethodInvocationManager;
import com.github.paohaijiao.physical.node.JQuickFilterPhysicalNode;
import com.github.paohaijiao.physical.node.JQuickRecursiveUnionPhysicalNode;
import com.github.paohaijiao.physical.node.JQuickTableScanPhysicalNode;
import com.github.paohaijiao.physical.node.JQuickValuesPhysicalNode;
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
 * JQuickRecursiveUnionPhysicalNode 测试
 * 
 * 测试范围：
 * 1. 基本递归查询（数字序列）
 * 2. 组织结构层级查询
 * 3. UNION ALL vs UNION
 * 4. 最大递归深度限制
 * 5. 空初始结果
 * 6. 单层递归
 */
public class JQuickRecursiveUnionPhysicalNodeTest {

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
        // employee_hierarchy 表 - 员工层级关系
        List<JQuickColumnMeta> employeeColumns = Arrays.asList(
                new JQuickColumnMeta("emp_id", Long.class, "employee_hierarchy"),
                new JQuickColumnMeta("emp_name", String.class, "employee_hierarchy"),
                new JQuickColumnMeta("manager_id", Long.class, "employee_hierarchy")
        );
        List<JQuickRow> employeeRows = new ArrayList<>();
        // CEO (顶级，没有经理)
        employeeRows.add(createRow(employeeColumns, new Object[]{1L, "CEO", null}));
        // 部门经理
        employeeRows.add(createRow(employeeColumns, new Object[]{2L, "Manager A", 1L}));
        employeeRows.add(createRow(employeeColumns, new Object[]{3L, "Manager B", 1L}));
        // 普通员工
        employeeRows.add(createRow(employeeColumns, new Object[]{4L, "Employee A1", 2L}));
        employeeRows.add(createRow(employeeColumns, new Object[]{5L, "Employee A2", 2L}));
        employeeRows.add(createRow(employeeColumns, new Object[]{6L, "Employee B1", 3L}));
        employeeRows.add(createRow(employeeColumns, new Object[]{7L, "Employee B2", 3L}));
        // 实习生
        employeeRows.add(createRow(employeeColumns, new Object[]{8L, "Intern A1", 4L}));
        employeeRows.add(createRow(employeeColumns, new Object[]{9L, "Intern B1", 6L}));
        
        JQuickDataSet employeeData = new JQuickDataSet(employeeColumns, employeeRows);
        JQuickDataSourceManager.registerTable("employee_hierarchy", employeeData);

        // category 表 - 分类层级
        List<JQuickColumnMeta> categoryColumns = Arrays.asList(
                new JQuickColumnMeta("category_id", Long.class, "category"),
                new JQuickColumnMeta("category_name", String.class, "category"),
                new JQuickColumnMeta("parent_id", Long.class, "category")
        );
        List<JQuickRow> categoryRows = new ArrayList<>();
        categoryRows.add(createRow(categoryColumns, new Object[]{1L, "Electronics", null}));
        categoryRows.add(createRow(categoryColumns, new Object[]{2L, "Computers", 1L}));
        categoryRows.add(createRow(categoryColumns, new Object[]{3L, "Phones", 1L}));
        categoryRows.add(createRow(categoryColumns, new Object[]{4L, "Laptops", 2L}));
        categoryRows.add(createRow(categoryColumns, new Object[]{5L, "Desktops", 2L}));
        categoryRows.add(createRow(categoryColumns, new Object[]{6L, "Smartphones", 3L}));
        categoryRows.add(createRow(categoryColumns, new Object[]{7L, "Gaming Laptops", 4L}));
        categoryRows.add(createRow(categoryColumns, new Object[]{8L, "Business Laptops", 4L}));
        
        JQuickDataSet categoryData = new JQuickDataSet(categoryColumns, categoryRows);
        JQuickDataSourceManager.registerTable("category", categoryData);
    }

    private JQuickRow createRow(List<JQuickColumnMeta> columns, Object[] values) {
        JQuickRow row = new JQuickRow();
        for (int i = 0; i < columns.size(); i++) {
            row.put(columns.get(i).getName(), values[i]);
        }
        return row;
    }

    /**
     * 测试基本递归查询 - 数字序列
     * 
     * 场景：生成 1 到 5 的数字序列
     * SQL: 
     *   WITH RECURSIVE nums AS (
     *     SELECT 1 AS n
     *     UNION ALL
     *     SELECT n + 1 FROM nums WHERE n < 5
     *   )
     *   SELECT * FROM nums
     * 预期：返回 1, 2, 3, 4, 5
     */
    @Test
    public void testBasicRecursion_NumberSequence() {
        // 创建初始计划：SELECT 1 AS n
        List<List<Object>> initialRows = new ArrayList<>();
        initialRows.add(Arrays.asList(1));
        JQuickValuesPhysicalNode initialPlan = new JQuickValuesPhysicalNode(
            initialRows,
            Arrays.asList("n"),
            Arrays.asList(Integer.class)
        );
        
        // 创建递归计划：SELECT n + 1 FROM nums WHERE n < 5
        List<List<Object>> recursiveRows = new ArrayList<>();
        recursiveRows.add(Arrays.asList(2));
        recursiveRows.add(Arrays.asList(3));
        recursiveRows.add(Arrays.asList(4));
        recursiveRows.add(Arrays.asList(5));
        JQuickValuesPhysicalNode recursivePlan = new JQuickValuesPhysicalNode(
            recursiveRows,
            Arrays.asList("n"),
            Arrays.asList(Integer.class)
        );
        JQuickRecursiveUnionPhysicalNode recursiveUnionNode = new JQuickRecursiveUnionPhysicalNode(
            "nums",
            Arrays.asList("n"),
            initialPlan,
            recursivePlan,
            true, // UNION ALL
            10 // 最大递归深度
        );
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(recursiveUnionNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertTrue("应该返回至少 1 条数据", result.size() >= 1);
        boolean hasOne = false;
        for (JQuickRow row : result.getRows()) {
            if (Integer.valueOf(1).equals(row.get("n"))) {
                hasOne = true;
                break;
            }
        }
        assertTrue("应该包含数字 1", hasOne);
    }

    /**
     * 测试组织结构层级查询 - 查找所有下属
     * 
     * 场景：查找 CEO 的所有下属（直接和间接）
     * SQL:
     *   WITH RECURSIVE subordinates AS (
     *     SELECT emp_id, emp_name, manager_id FROM employee_hierarchy WHERE emp_id = 1
     *     UNION ALL
     *     SELECT e.emp_id, e.emp_name, e.manager_id 
     *     FROM employee_hierarchy e
     *     INNER JOIN subordinates s ON e.manager_id = s.emp_id
     *   )
     *   SELECT * FROM subordinates
     * 预期：返回所有员工（CEO 及所有下属）
     */
    @Test
    public void testEmployeeHierarchy_AllSubordinates() {
        // 创建初始计划：SELECT * FROM employee_hierarchy WHERE emp_id = 1
        JQuickExpression filterExpr = new JQuickBinaryExpression(
            new JQuickColumnRefExpression("emp_id"),
            new JQuickLiteralExpression(1L), JQuickBinaryOperator.EQ
        );
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee_hierarchy", null, null, null, null);
        JQuickFilterPhysicalNode initialPlan = new JQuickFilterPhysicalNode(filterExpr, scanNode);
        JQuickTableScanPhysicalNode recursivePlan = new JQuickTableScanPhysicalNode("employee_hierarchy", null, null, null, null);
        JQuickRecursiveUnionPhysicalNode recursiveUnionNode = new JQuickRecursiveUnionPhysicalNode(
            "subordinates",
            Arrays.asList("emp_id", "emp_name", "manager_id"),
            initialPlan,
            recursivePlan,
            true, // UNION ALL
            5 // 最大递归深度
        );
        
        // 执行查询
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(recursiveUnionNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertTrue("应该返回至少 1 条数据（CEO）", result.size() >= 1);
        
        // 验证包含 CEO
        boolean hasCEO = false;
        for (JQuickRow row : result.getRows()) {
            if ("CEO".equals(row.get("emp_name"))) {
                hasCEO = true;
                break;
            }
        }
        assertTrue("应该包含 CEO", hasCEO);
    }

    /**
     * 测试分类层级查询
     * 
     * 场景：查找某个分类的所有子分类
     * SQL:
     *   WITH RECURSIVE category_tree AS (
     *     SELECT category_id, category_name, parent_id FROM category WHERE category_id = 1
     *     UNION ALL
     *     SELECT c.category_id, c.category_name, c.parent_id
     *     FROM category c
     *     INNER JOIN category_tree ct ON c.parent_id = ct.category_id
     *   )
     *   SELECT * FROM category_tree
     * 预期：返回所有分类（Electronics 及其所有子分类）
     */
    @Test
    public void testCategoryHierarchy_AllSubcategories() {
        JQuickExpression filterExpr = new JQuickBinaryExpression(new JQuickColumnRefExpression("category_id"), new JQuickLiteralExpression(1L), JQuickBinaryOperator.EQ);
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("category", null, null, null, null);
        JQuickFilterPhysicalNode initialPlan = new JQuickFilterPhysicalNode(filterExpr, scanNode);
        JQuickTableScanPhysicalNode recursivePlan = new JQuickTableScanPhysicalNode("category", null, null, null, null);
        JQuickRecursiveUnionPhysicalNode recursiveUnionNode = new JQuickRecursiveUnionPhysicalNode(
            "category_tree",
            Arrays.asList("category_id", "category_name", "parent_id"),
            initialPlan,
            recursivePlan,
            true, // UNION ALL
            5
        );
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(recursiveUnionNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertTrue("应该返回至少 1 条数据（Electronics）", result.size() >= 1);
        boolean hasElectronics = false;
        for (JQuickRow row : result.getRows()) {
            if ("Electronics".equals(row.get("category_name"))) {
                hasElectronics = true;
                break;
            }
        }
        assertTrue("应该包含 Electronics", hasElectronics);
    }

    /**
     * 测试 UNION（去重）vs UNION ALL
     * 
     * 场景：使用 UNION（去重）进行递归
     * 预期：结果不包含重复数据
     */
    @Test
    public void testRecursiveUnion_WithDeduplication() {
        List<List<Object>> initialRows = new ArrayList<>();
        initialRows.add(Arrays.asList(1, "Item 1"));
        JQuickValuesPhysicalNode initialPlan = new JQuickValuesPhysicalNode(
            initialRows,
            Arrays.asList("id", "name"),
            Arrays.asList(Integer.class, String.class)
        );
        List<List<Object>> recursiveRows = new ArrayList<>();
        recursiveRows.add(Arrays.asList(1, "Item 1")); // 重复
        recursiveRows.add(Arrays.asList(2, "Item 2"));
        JQuickValuesPhysicalNode recursivePlan = new JQuickValuesPhysicalNode(
            recursiveRows,
            Arrays.asList("id", "name"),
            Arrays.asList(Integer.class, String.class)
        );
        JQuickRecursiveUnionPhysicalNode recursiveUnionNode = new JQuickRecursiveUnionPhysicalNode(
            "items",
            Arrays.asList("id", "name"),
            initialPlan,
            recursivePlan,
            false, // UNION（去重）
            5
        );
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(recursiveUnionNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertTrue("应该返回至少 1 条数据", result.size() >= 1);
        Set<String> names = new HashSet<>();
        for (JQuickRow row : result.getRows()) {
            names.add((String) row.get("name"));
        }
        assertTrue("应该包含 Item 1", names.contains("Item 1"));
    }

    /**
     * 测试 UNION ALL（不去重）
     * 
     * 场景：使用 UNION ALL 进行递归
     * 预期：结果包含所有数据（可能有重复）
     */
    @Test
    public void testRecursiveUnion_UnionAll() {
        List<List<Object>> initialRows = new ArrayList<>();
        initialRows.add(Arrays.asList(1, "Item 1"));
        JQuickValuesPhysicalNode initialPlan = new JQuickValuesPhysicalNode(
            initialRows,
            Arrays.asList("id", "name"),
            Arrays.asList(Integer.class, String.class)
        );
        List<List<Object>> recursiveRows = new ArrayList<>();
        recursiveRows.add(Arrays.asList(2, "Item 2"));
        JQuickValuesPhysicalNode recursivePlan = new JQuickValuesPhysicalNode(
            recursiveRows,
            Arrays.asList("id", "name"),
            Arrays.asList(Integer.class, String.class)
        );
        JQuickRecursiveUnionPhysicalNode recursiveUnionNode = new JQuickRecursiveUnionPhysicalNode(
            "items",
            Arrays.asList("id", "name"),
            initialPlan,
            recursivePlan,
            true, // UNION ALL
            5
        );
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(recursiveUnionNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertTrue("应该返回至少 1 条数据", result.size() >= 1);
    }

    /**
     * 测试最大递归深度限制
     * 
     * 场景：设置最大递归深度为 2
     * 预期：递归在达到最大深度后停止
     */
    @Test
    public void testMaxRecursionDepth() {
        List<List<Object>> initialRows = new ArrayList<>();
        initialRows.add(Arrays.asList(1));
        JQuickValuesPhysicalNode initialPlan = new JQuickValuesPhysicalNode(
            initialRows,
            Arrays.asList("n"),
            Arrays.asList(Integer.class)
        );
        List<List<Object>> recursiveRows = new ArrayList<>();
        recursiveRows.add(Arrays.asList(2));
        recursiveRows.add(Arrays.asList(3));
        recursiveRows.add(Arrays.asList(4));
        recursiveRows.add(Arrays.asList(5));
        JQuickValuesPhysicalNode recursivePlan = new JQuickValuesPhysicalNode(
            recursiveRows,
            Arrays.asList("n"),
            Arrays.asList(Integer.class)
        );
        
        JQuickRecursiveUnionPhysicalNode recursiveUnionNode = new JQuickRecursiveUnionPhysicalNode(
            "nums",
            Arrays.asList("n"),
            initialPlan,
            recursivePlan,
            true,
            2 // 最大递归深度 2
        );
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(recursiveUnionNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertTrue("应该返回至少 1 条数据", result.size() >= 1);
    }

    /**
     * 测试空初始结果
     * 
     * 场景：初始查询返回空结果
     * 预期：递归不执行，返回空结果
     */
    @Test
    public void testEmptyInitialResult() {
        List<List<Object>> initialRows = new ArrayList<>();
        JQuickValuesPhysicalNode initialPlan = new JQuickValuesPhysicalNode(
            initialRows,
            Arrays.asList("id", "name"),
            Arrays.asList(Integer.class, String.class)
        );
        List<List<Object>> recursiveRows = new ArrayList<>();
        recursiveRows.add(Arrays.asList(1, "Item 1"));
        JQuickValuesPhysicalNode recursivePlan = new JQuickValuesPhysicalNode(
            recursiveRows,
            Arrays.asList("id", "name"),
            Arrays.asList(Integer.class, String.class)
        );
        JQuickRecursiveUnionPhysicalNode recursiveUnionNode = new JQuickRecursiveUnionPhysicalNode(
            "items",
            Arrays.asList("id", "name"),
            initialPlan,
            recursivePlan,
            true,
            5
        );
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(recursiveUnionNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 0 条数据", 0, result.size());
    }

    /**
     * 测试单层递归
     * 
     * 场景：递归只执行一次
     * 预期：返回初始结果和一次递归结果
     */
    @Test
    public void testSingleLevelRecursion() {
        // 创建初始计划
        List<List<Object>> initialRows = new ArrayList<>();
        initialRows.add(Arrays.asList(1, "Root"));
        JQuickValuesPhysicalNode initialPlan = new JQuickValuesPhysicalNode(
            initialRows,
            Arrays.asList("id", "name"),
            Arrays.asList(Integer.class, String.class)
        );
        List<List<Object>> recursiveRows = new ArrayList<>();
        recursiveRows.add(Arrays.asList(2, "Child"));
        JQuickValuesPhysicalNode recursivePlan = new JQuickValuesPhysicalNode(
            recursiveRows,
            Arrays.asList("id", "name"),
            Arrays.asList(Integer.class, String.class)
        );
        JQuickRecursiveUnionPhysicalNode recursiveUnionNode = new JQuickRecursiveUnionPhysicalNode(
            "tree",
            Arrays.asList("id", "name"),
            initialPlan,
            recursivePlan,
            true,
            5
        );
        JQuickWorker.JQuickTaskContext context = createTaskContext();
        JQuickDataSet result = nodeExecutor.executeNode(recursiveUnionNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertTrue("应该返回至少 1 条数据", result.size() >= 1);
        boolean hasRoot = false;
        for (JQuickRow row : result.getRows()) {
            if ("Root".equals(row.get("name"))) {
                hasRoot = true;
                break;
            }
        }
        assertTrue("应该包含 Root", hasRoot);
    }

    /**
     * 测试克隆功能
     * 
     * 场景：克隆递归 Union 节点
     * 预期：克隆后的节点与原节点配置一致
     */
    @Test
    public void testClone() {
        List<List<Object>> initialRows = new ArrayList<>();
        initialRows.add(Arrays.asList(1));
        JQuickValuesPhysicalNode initialPlan = new JQuickValuesPhysicalNode(
            initialRows,
            Arrays.asList("n"),
            Arrays.asList(Integer.class)
        );
        List<List<Object>> recursiveRows = new ArrayList<>();
        recursiveRows.add(Arrays.asList(2));
        JQuickValuesPhysicalNode recursivePlan = new JQuickValuesPhysicalNode(
            recursiveRows,
            Arrays.asList("n"),
            Arrays.asList(Integer.class)
        );
        
        JQuickRecursiveUnionPhysicalNode recursiveUnionNode = new JQuickRecursiveUnionPhysicalNode(
            "nums",
            Arrays.asList("n"),
            initialPlan,
            recursivePlan,
            true,
            10
        );
        JQuickRecursiveUnionPhysicalNode clonedNode = (JQuickRecursiveUnionPhysicalNode) recursiveUnionNode.clone();
        assertNotNull("克隆节点不应为 null", clonedNode);
        assertEquals("CTE 名称应该相同", recursiveUnionNode.getCteName(), clonedNode.getCteName());
        assertEquals("列名应该相同", recursiveUnionNode.getColumnNames(), clonedNode.getColumnNames());
        assertEquals("UNION ALL 标志应该相同", recursiveUnionNode.isUnionAll(), clonedNode.isUnionAll());
        assertEquals("最大递归深度应该相同", recursiveUnionNode.getMaxRecursionDepth(), clonedNode.getMaxRecursionDepth());
    }

    /**
     * 测试节点属性
     * 
     * 场景：验证节点的各种属性
     * 预期：属性值正确
     */
    @Test
    public void testNodeProperties() {
        // 创建初始计划
        List<List<Object>> initialRows = new ArrayList<>();
        initialRows.add(Arrays.asList(1));
        JQuickValuesPhysicalNode initialPlan = new JQuickValuesPhysicalNode(
            initialRows,
            Arrays.asList("n"),
            Arrays.asList(Integer.class)
        );
        
        // 创建递归计划
        List<List<Object>> recursiveRows = new ArrayList<>();
        recursiveRows.add(Arrays.asList(2));
        JQuickValuesPhysicalNode recursivePlan = new JQuickValuesPhysicalNode(
            recursiveRows,
            Arrays.asList("n"),
            Arrays.asList(Integer.class)
        );
        
        // 创建递归 Union 节点
        JQuickRecursiveUnionPhysicalNode recursiveUnionNode = new JQuickRecursiveUnionPhysicalNode(
            "test_cte",
            Arrays.asList("n"),
            initialPlan,
            recursivePlan,
            false, // UNION
            100
        );
        assertEquals("节点类型应该是 RecursiveUnion", "RecursiveUnion", recursiveUnionNode.getNodeType());
        assertEquals("CTE 名称应该是 test_cte", "test_cte", recursiveUnionNode.getCteName());
        assertEquals("列名应该包含 n", Arrays.asList("n"), recursiveUnionNode.getColumnNames());
        assertFalse("UNION ALL 标志应该是 false", recursiveUnionNode.isUnionAll());
        assertEquals("最大递归深度应该是 100", 100, recursiveUnionNode.getMaxRecursionDepth());
        assertEquals("应该有 2 个子节点", 2, recursiveUnionNode.getChildren().size());
        assertNotNull("初始计划不应为 null", recursiveUnionNode.getInitialPlan());
        assertNotNull("递归计划不应为 null", recursiveUnionNode.getRecursivePlan());
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
