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
import com.github.paohaijiao.distributed.JQuickDistributedPlan;
import com.github.paohaijiao.distributed.worker.*;
import com.github.paohaijiao.enums.*;
import com.github.paohaijiao.exchange.JQuickExchangeNode;
import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.expression.domain.JQuickBinaryExpression;
import com.github.paohaijiao.expression.domain.JQuickColumnRefExpression;
import com.github.paohaijiao.expression.domain.JQuickLiteralExpression;
import com.github.paohaijiao.fragment.JQuickFragment;
import com.github.paohaijiao.fragment.JQuickFragmenter;
import com.github.paohaijiao.function.manager.JQuickMethodInvocationManager;
import com.github.paohaijiao.physical.JQuickPhysicalPlanNode;
import com.github.paohaijiao.physical.node.*;
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
 * Fragment 切分器测试用例
 */
public class JQuickFragmentTest {

    private JQuickFragmenter fragmenter;
    private JQuickFragmenter verboseFragmenter;


    @Before
    public void setUp() {
        fragmenter = new JQuickFragmenter(4);
        verboseFragmenter = new JQuickFragmenter(8);
    }

    @After
    public void tearDown() {
    }


    @Test
    public void testCreateFragment() {
        JQuickTableScanPhysicalNode scanNode = createTableScanNode("users");
        JQuickFragment fragment = new JQuickFragment(JQuickFragmentType.SOURCE, scanNode);
        assertNotNull(fragment);
        assertNotNull(fragment.getFragmentId());
        assertEquals(JQuickFragmentType.SOURCE, fragment.getType());
        assertEquals(scanNode, fragment.getPlan());
        assertEquals(1, fragment.getParallelism());
        assertTrue(fragment.getChildren().isEmpty());
        assertTrue(fragment.getInputs().isEmpty());
        assertNull(fragment.getOutput());
        JQuickDistributedPlan plan = fragmenter.fragment(scanNode);
        assertNotNull(plan);
        JQuickFragment root = plan.getRootFragment();
        // Filter 应该被切分为独立的 Fragment
        assertEquals(JQuickFragmentType.SINK, root.getType());
        // 打印 Fragment 结构用于调试
        fragmenter.printFragments(plan);

    }

    @Test
    public void testFragmentWithParallelism() {
        JQuickTableScanPhysicalNode scanNode = createTableScanNode("users");
        JQuickFragment fragment = new JQuickFragment(JQuickFragmentType.SOURCE, scanNode);
        fragment.setParallelism(8);
        assertEquals(8, fragment.getParallelism());
        JQuickDistributedPlan plan = fragmenter.fragment(scanNode);
        assertNotNull(plan);
        JQuickFragment root = plan.getRootFragment();
        // Filter 应该被切分为独立的 Fragment
        assertEquals(JQuickFragmentType.SINK, root.getType());
        // 打印 Fragment 结构用于调试
        fragmenter.printFragments(plan);
    }

    @Test
    public void testFragmentWithChildren() {
        JQuickFragment parent = new JQuickFragment(JQuickFragmentType.INTERMEDIATE, createTableScanNode("orders"));
        JQuickFragment child1 = new JQuickFragment(JQuickFragmentType.SOURCE, createTableScanNode("users"));
        JQuickFragment child2 = new JQuickFragment(JQuickFragmentType.SOURCE, createTableScanNode("products"));
        parent.addChild(child1);
        parent.addChild(child2);
        assertEquals(2, parent.getChildren().size());
        assertTrue(parent.getChildren().contains(child1));
        assertTrue(parent.getChildren().contains(child2));

    }

    @Test
    public void testFragmentWithExchange() {
        JQuickTableScanPhysicalNode scanNode = createTableScanNode("users");
        JQuickFragment fragment = new JQuickFragment(JQuickFragmentType.SOURCE, scanNode);
        JQuickExchangeNode outputExchange = new JQuickExchangeNode("exchange_1", JQuickExchangeType.SHUFFLE, JQuickPartitionStrategy.HASH, Collections.singletonList(new JQuickColumnRefExpression("id")), 4);
        fragment.setOutput(outputExchange);
        JQuickExchangeNode inputExchange = new JQuickExchangeNode("input_exchange_1", JQuickExchangeType.RECEIVE, JQuickPartitionStrategy.HASH, Collections.singletonList(new JQuickColumnRefExpression("id")), 4);
        fragment.addInput(inputExchange);
        // 打印 Fragment 结构用于调试
        fragmenter.printFragment(fragment,0);
    }

    @Test
    public void testFragmentIdIncrement() {
        JQuickFragment fragment1 = new JQuickFragment(JQuickFragmentType.SOURCE, createTableScanNode("t1"));
        JQuickFragment fragment2 = new JQuickFragment(JQuickFragmentType.SOURCE, createTableScanNode("t2"));
        JQuickFragment fragment3 = new JQuickFragment(JQuickFragmentType.SOURCE, createTableScanNode("t3"));
        assertTrue(fragment2.getFragmentId() > fragment1.getFragmentId());
        assertTrue(fragment3.getFragmentId() > fragment2.getFragmentId());
        fragmenter.printFragment(fragment3,0);
    }

    @Test
    public void testFragmentToString() {
        JQuickFragment fragment = new JQuickFragment(JQuickFragmentType.SOURCE, createTableScanNode("users"));
        fragment.setParallelism(4);
        String toString = fragment.toString();
        assertTrue(toString.contains("Fragment"));
        assertTrue(toString.contains(String.valueOf(fragment.getFragmentId())));
        assertTrue(toString.contains("SOURCE"));
        assertTrue(toString.contains("parallelism=4"));
        fragmenter.printFragment(fragment,0);
    }

    @Test
    public void testFragmentSimpleTableScan() {
        JQuickTableScanPhysicalNode scanNode = createTableScanNode("users");
        JQuickDistributedPlan plan = fragmenter.fragment(scanNode);
        assertNotNull(plan);
        assertNotNull(plan.getRootFragment());
        JQuickFragment root = plan.getRootFragment();
        assertEquals(JQuickFragmentType.SINK, root.getType());
        assertEquals(1, root.getParallelism());
        fragmenter.printFragments(plan);
    }

    @Test
    public void testFragmentWithFilter() {
        // 创建过滤条件: age > 18
        JQuickExpression predicate = new JQuickBinaryExpression(
                new JQuickColumnRefExpression("age"),
                new JQuickLiteralExpression(18),
                com.github.paohaijiao.enums.JQuickBinaryOperator.GT
        );
        JQuickTableScanPhysicalNode scanNode = createTableScanNode("users");
        JQuickFilterPhysicalNode filterNode = new JQuickFilterPhysicalNode(predicate, scanNode);
        JQuickDistributedPlan plan = fragmenter.fragment(filterNode);
        assertNotNull(plan);
        JQuickFragment root = plan.getRootFragment();
        // Filter 应该被切分为独立的 Fragment
        assertEquals(JQuickFragmentType.SINK, root.getType());
        // 打印 Fragment 结构用于调试
        fragmenter.printFragments(plan);
        // 验证 Fragment 结构
        List<JQuickFragment> allFragments = plan.getAllFragments();
        assertTrue("Should have at least 2 fragments (SINK + FILTER)", allFragments.size() >= 2);
    }

    @Test
    public void testFragmentWithProject() {
        List<JQuickProjectPhysicalNode.SelectItem> selectItems = Arrays.asList(
                new JQuickProjectPhysicalNode.SelectItem(new JQuickColumnRefExpression("name"), "user_name"),
                new JQuickProjectPhysicalNode.SelectItem(new JQuickColumnRefExpression("age"), "user_age")
        );
        JQuickTableScanPhysicalNode scanNode = createTableScanNode("users");
        JQuickProjectPhysicalNode projectNode = new JQuickProjectPhysicalNode(selectItems, scanNode, false);
        JQuickDistributedPlan plan = fragmenter.fragment(projectNode);
        assertNotNull(plan);
        fragmenter.printFragments(plan);
        List<JQuickFragment> allFragments = plan.getAllFragments();
        assertTrue("Should have at least 2 fragments", allFragments.size() >= 2);
    }

    @Test
    public void testFragmentWithFilterAndProject() {
        // Filter: age > 18
        JQuickExpression predicate = new JQuickBinaryExpression(
                new JQuickColumnRefExpression("age"),
                new JQuickLiteralExpression(18),
                com.github.paohaijiao.enums.JQuickBinaryOperator.GT
        );

        // Project: select name, age
        List<JQuickProjectPhysicalNode.SelectItem> selectItems = Arrays.asList(
                new JQuickProjectPhysicalNode.SelectItem(new JQuickColumnRefExpression("name"), "user_name"),
                new JQuickProjectPhysicalNode.SelectItem(new JQuickColumnRefExpression("age"), "user_age")
        );

        JQuickTableScanPhysicalNode scanNode = createTableScanNode("users");
        JQuickFilterPhysicalNode filterNode = new JQuickFilterPhysicalNode(predicate, scanNode);
        JQuickProjectPhysicalNode projectNode = new JQuickProjectPhysicalNode(selectItems, filterNode, false);
        JQuickDistributedPlan plan = fragmenter.fragment(projectNode);
        assertNotNull(plan);
        fragmenter.printFragments(plan);
        // 验证 Fragment 数量
        List<JQuickFragment> allFragments = plan.getAllFragments();
        // TableScan, Filter, Project 可能被切分为多个 Fragment
        assertTrue("Should have at least 2 fragments", allFragments.size() >= 2);
    }

    @Test
    public void testFragmentWithLimit() {
        JQuickTableScanPhysicalNode scanNode = createTableScanNode("users");
        JQuickLimitPhysicalNode limitNode = new JQuickLimitPhysicalNode(10, scanNode);
        JQuickDistributedPlan plan = fragmenter.fragment(limitNode);
        assertNotNull(plan);
        fragmenter.printFragments(plan);
    }

    @Test
    public void testFragmentWithSort() {
        JQuickTableScanPhysicalNode scanNode = createTableScanNode("users");
        List<JQuickSortPhysicalNode.OrderByItem> orderBy = Arrays.asList(
                new JQuickSortPhysicalNode.OrderByItem("age", false),  // DESC
                new JQuickSortPhysicalNode.OrderByItem("name", true)    // ASC
        );
        JQuickSortPhysicalNode sortNode = new JQuickSortPhysicalNode(orderBy, scanNode);
        JQuickDistributedPlan plan = fragmenter.fragment(sortNode);
        assertNotNull(plan);
        fragmenter.printFragments(plan);
    }

    @Test
    public void testFragmentWithAggregate() {
        JQuickTableScanPhysicalNode scanNode = createTableScanNode("orders");
        List<JQuickExpression> groupKeys = Arrays.asList(
                new JQuickColumnRefExpression("category")
        );
        List<JQuickHashAggregatePhysicalNode.AggregateFunction> aggregates = Arrays.asList(
                new JQuickHashAggregatePhysicalNode.AggregateFunction("count", null, false, "order_count"),
                new JQuickHashAggregatePhysicalNode.AggregateFunction("sum", new JQuickColumnRefExpression("amount"), false, "total_amount")
        );
        JQuickHashAggregatePhysicalNode aggNode = new JQuickHashAggregatePhysicalNode(
                groupKeys, aggregates, scanNode, null,
                JQuickHashAggregatePhysicalNode.AggregateStage.SINGLE
        );
        JQuickDistributedPlan plan = fragmenter.fragment(aggNode);
        assertNotNull(plan);
        fragmenter.printFragments(plan);
    }

    @Test
    public void testFragmentWithJoin() {
        JQuickTableScanPhysicalNode leftScan = createTableScanNode("orders");
        JQuickTableScanPhysicalNode rightScan = createTableScanNode("users");

        List<JQuickHashJoinPhysicalNode.JoinKeyPair> joinKeys = Arrays.asList(
                new JQuickHashJoinPhysicalNode.JoinKeyPair(
                        new JQuickColumnRefExpression("user_id"),
                        new JQuickColumnRefExpression("id")
                )
        );

        JQuickHashJoinPhysicalNode joinNode = new JQuickHashJoinPhysicalNode(
                JQuickJoinType.INNER, leftScan, rightScan, null, joinKeys,
                JQuickHashJoinPhysicalNode.BuildSide.RIGHT,
                JQuickHashJoinPhysicalNode.JoinDistribution.SHUFFLE_HASH
        );

        JQuickDistributedPlan plan = fragmenter.fragment(joinNode);

        assertNotNull(plan);
        fragmenter.printFragments(plan);
        List<JQuickFragment> allFragments = plan.getAllFragments();
        assertTrue("Join should create multiple fragments", allFragments.size() >= 2);
    }

    @Test
    public void testGetAllFragments() {
        // 构建多级 Fragment 树
        JQuickFragment root = new JQuickFragment(JQuickFragmentType.SINK, createTableScanNode("root"));
        JQuickFragment child1 = new JQuickFragment(JQuickFragmentType.INTERMEDIATE, createTableScanNode("child1"));
        JQuickFragment child2 = new JQuickFragment(JQuickFragmentType.INTERMEDIATE, createTableScanNode("child2"));
        JQuickFragment grandchild = new JQuickFragment(JQuickFragmentType.SOURCE, createTableScanNode("grandchild"));
        root.addChild(child1);
        root.addChild(child2);
        child1.addChild(grandchild);
        JQuickDistributedPlan plan = new JQuickDistributedPlan(root);
        List<JQuickFragment> allFragments = plan.getAllFragments();
        assertEquals(4, allFragments.size());
        assertTrue(allFragments.contains(root));
        assertTrue(allFragments.contains(child1));
        assertTrue(allFragments.contains(child2));
        assertTrue(allFragments.contains(grandchild));
    }

    @Test
    public void testGetSourceFragments() {
        JQuickFragment root = new JQuickFragment(JQuickFragmentType.SINK, createTableScanNode("root"));
        JQuickFragment source1 = new JQuickFragment(JQuickFragmentType.SOURCE, createTableScanNode("source1"));
        JQuickFragment source2 = new JQuickFragment(JQuickFragmentType.SOURCE, createTableScanNode("source2"));
        JQuickFragment intermediate = new JQuickFragment(JQuickFragmentType.INTERMEDIATE, createTableScanNode("intermediate"));

        root.addChild(source1);
        root.addChild(intermediate);
        intermediate.addChild(source2);

        JQuickDistributedPlan plan = new JQuickDistributedPlan(root);
        List<JQuickFragment> sourceFragments = plan.getSourceFragments();

        assertEquals(2, sourceFragments.size());
        assertTrue(sourceFragments.contains(source1));
        assertTrue(sourceFragments.contains(source2));
    }

    @Test
    public void testGetExecutionOrder() {
        JQuickFragment root = new JQuickFragment(JQuickFragmentType.SINK, createTableScanNode("root"));
        JQuickFragment child1 = new JQuickFragment(JQuickFragmentType.INTERMEDIATE, createTableScanNode("child1"));
        JQuickFragment child2 = new JQuickFragment(JQuickFragmentType.INTERMEDIATE, createTableScanNode("child2"));
        JQuickFragment leaf1 = new JQuickFragment(JQuickFragmentType.SOURCE, createTableScanNode("leaf1"));
        JQuickFragment leaf2 = new JQuickFragment(JQuickFragmentType.SOURCE, createTableScanNode("leaf2"));

        root.addChild(child1);
        root.addChild(child2);
        child1.addChild(leaf1);
        child2.addChild(leaf2);

        JQuickDistributedPlan plan = new JQuickDistributedPlan(root);
        List<JQuickFragment> executionOrder = plan.getExecutionOrder();

        // 叶子节点应该先执行
        int leaf1Index = executionOrder.indexOf(leaf1);
        int leaf2Index = executionOrder.indexOf(leaf2);
        int child1Index = executionOrder.indexOf(child1);
        int child2Index = executionOrder.indexOf(child2);
        int rootIndex = executionOrder.indexOf(root);

        assertTrue(leaf1Index < child1Index);
        assertTrue(leaf2Index < child2Index);
        assertTrue(child1Index < rootIndex);
        assertTrue(child2Index < rootIndex);
    }

    @Test
    public void testGetFragmentById() {
        JQuickFragment root = new JQuickFragment(JQuickFragmentType.SINK, createTableScanNode("root"));
        JQuickFragment child = new JQuickFragment(JQuickFragmentType.SOURCE, createTableScanNode("child"));
        root.addChild(child);

        JQuickDistributedPlan plan = new JQuickDistributedPlan(root);

        assertEquals(root, plan.getFragmentById(root.getFragmentId()));
        assertEquals(child, plan.getFragmentById(child.getFragmentId()));
        assertNull(plan.getFragmentById(99999L));
    }


    @Test(expected = IllegalArgumentException.class)
    public void testFragmentWithNullPlan() {
        fragmenter.fragment(null);
    }

    @Test
    public void testFragmentWithEmptyChildren() {
        JQuickEmptyPhysicalNode emptyNode = JQuickEmptyPhysicalNode.INSTANCE;
        JQuickDistributedPlan plan = fragmenter.fragment(emptyNode);
        assertNotNull(plan);
        assertNotNull(plan.getRootFragment());
    }

    @Test
    public void testFragmentWithValuesNode() {
        List<List<Object>> rows = Arrays.asList(
                Arrays.asList(1, "Alice"),
                Arrays.asList(2, "Bob"),
                Arrays.asList(3, "Charlie")
        );
        List<String> columns = Arrays.asList("id", "name");
        List<Class<?>> types = Arrays.asList(Integer.class, String.class);

        JQuickValuesPhysicalNode valuesNode = new JQuickValuesPhysicalNode(rows, columns, types);
        JQuickDistributedPlan plan = fragmenter.fragment(valuesNode);

        assertNotNull(plan);
        fragmenter.printFragments(plan);
    }

    @Test
    public void testDefaultParallelism() {
        assertEquals(4, fragmenter.fragment(createTableScanNode("users")).getDefaultParallelism());
        assertEquals(8, verboseFragmenter.fragment(createTableScanNode("users")).getDefaultParallelism());
    }

    @Test
    public void testFragmentParallelismPropagation() {
        JQuickTableScanPhysicalNode scanNode = createTableScanNode("users");
        JQuickFilterPhysicalNode filterNode = new JQuickFilterPhysicalNode(null, scanNode);
        JQuickDistributedPlan plan = fragmenter.fragment(filterNode);
        for (JQuickFragment fragment : plan.getAllFragments()) {
            if (fragment.getType() == JQuickFragmentType.SOURCE) {
                assertEquals(4, fragment.getParallelism());
            }
        }
    }

    private JQuickTableScanPhysicalNode createTableScanNode(String tableName) {
        Set<String> columns = new HashSet<>(Arrays.asList("id", "name", "age", "email"));
        return new JQuickTableScanPhysicalNode(tableName, null, columns, null);
    }

    @Test
    public void testLargePlanFragmentation() {
        // 构建一个较大的计划树进行性能测试
        JQuickPhysicalPlanNode root = createLargePlan(10);
        long startTime = System.currentTimeMillis();
        JQuickDistributedPlan plan = fragmenter.fragment(root);
        long duration = System.currentTimeMillis() - startTime;
        System.out.println("Large plan fragmentation took: " + duration + "ms");
        System.out.println("Generated " + plan.getAllFragments().size() + " fragments");
        assertTrue(duration < 5000); // 应该在5秒内完成
    }

    private JQuickPhysicalPlanNode createLargePlan(int depth) {
        if (depth <= 0) {
            return createTableScanNode("table_" + depth);
        }

        JQuickTableScanPhysicalNode scanNode = createTableScanNode("table_" + depth);
        JQuickFilterPhysicalNode filterNode = new JQuickFilterPhysicalNode(null, scanNode);

        List<JQuickProjectPhysicalNode.SelectItem> selectItems = Arrays.asList(
                new JQuickProjectPhysicalNode.SelectItem(new JQuickColumnRefExpression("id"), "id"),
                new JQuickProjectPhysicalNode.SelectItem(new JQuickColumnRefExpression("name"), "name")
        );

        return new JQuickProjectPhysicalNode(selectItems, filterNode, false);
    }
    @Test
    public void testFragmentSerialization() {
        JQuickFragment fragment = new JQuickFragment(JQuickFragmentType.INTERMEDIATE, createTableScanNode("users"));
        fragment.setParallelism(8);
        fragment.setAssignedHost("192.168.1.100");
        JQuickExchangeNode exchange = new JQuickExchangeNode(
                "exchange_test",
                JQuickExchangeType.SHUFFLE,
                JQuickPartitionStrategy.HASH,
                Collections.singletonList(new JQuickColumnRefExpression("id")),
                4
        );
        fragment.setOutput(exchange);

        // 测试 toString 包含所有必要信息
        String str = fragment.toString();
        assertTrue(str.contains(String.valueOf(fragment.getFragmentId())));
        assertTrue(str.contains("INTERMEDIATE"));
        assertTrue(str.contains("parallelism=8"));
    }

    public static class JQuickHashJoinPhysicalNodeTest {

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

        @After
        public void tearDown() {
            JQuickDataSourceManager.clearAll();
        }

        @Test
        public void testExecuteHashJoin_InnerJoin() {
            JQuickTableScanPhysicalNode leftScan = new JQuickTableScanPhysicalNode("employee", "e", null, null);
            JQuickTableScanPhysicalNode rightScan = new JQuickTableScanPhysicalNode("department", "d", null, null);
            // Join key: e.department = d.dept_name
            List<JQuickHashJoinPhysicalNode.JoinKeyPair> joinKeys = Collections.singletonList(new JQuickHashJoinPhysicalNode.JoinKeyPair(new JQuickColumnRefExpression("d.department"), new JQuickColumnRefExpression("e.dept_name")));
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

            JQuickTableScanPhysicalNode leftScan = new JQuickTableScanPhysicalNode("employee_ext", "e", null, null);
            JQuickTableScanPhysicalNode rightScan = new JQuickTableScanPhysicalNode("department", "d", null, null);
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
            result.printTable();
            assertNotNull(result);
        }

        @Test
        public void testExecuteHashAggregate_GroupBy() {
            JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null);
            List<JQuickExpression> groupKeys = Collections.singletonList(new JQuickColumnRefExpression("department"));
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
            result.printTable();
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
            JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null);
            // 无分组键 - 全局聚合
            List<JQuickHashAggregatePhysicalNode.AggregateFunction> aggregates = Arrays.asList(new JQuickHashAggregatePhysicalNode.AggregateFunction("count", null, false, "total_count"),
                    new JQuickHashAggregatePhysicalNode.AggregateFunction("avg", new JQuickColumnRefExpression("salary"), false, "avg_salary"),
                    new JQuickHashAggregatePhysicalNode.AggregateFunction("sum", new JQuickColumnRefExpression("salary"), false, "total_salary")
            );
            JQuickHashAggregatePhysicalNode aggNode = new JQuickHashAggregatePhysicalNode(
                    Collections.emptyList(), aggregates, scanNode, null,
                    JQuickHashAggregatePhysicalNode.AggregateStage.SINGLE
            );
            JQuickDataSet result = nodeExecutor.executeNode(aggNode, taskContext);
            result.printTable();
            assertNotNull(result);
            assertEquals(1, result.size());
            JQuickRow row = result.getRows().get(0);
            assertEquals(5L, row.get("total_count"));
            assertEquals(9000.0, (Double) row.get("avg_salary"), 0.001);
            assertEquals(45000.0, (Double) row.get("total_salary"), 0.001);
        }

        @Test
        public void testExecuteHashAggregate_WithHaving() {
            JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null);
            List<JQuickExpression> groupKeys = Collections.singletonList(new JQuickColumnRefExpression("department"));
            List<JQuickHashAggregatePhysicalNode.AggregateFunction> aggregates = Collections.singletonList(
                    new JQuickHashAggregatePhysicalNode.AggregateFunction("count", null, false, "emp_count")
            );
            // Having: COUNT(*) > 1
            JQuickExpression havingCondition = new JQuickBinaryExpression(
                    new JQuickColumnRefExpression("emp_count"),
                    new JQuickLiteralExpression(1L),
                    JQuickBinaryOperator.GT
            );
            JQuickHashAggregatePhysicalNode aggNode = new JQuickHashAggregatePhysicalNode(groupKeys, aggregates, scanNode, havingCondition, JQuickHashAggregatePhysicalNode.AggregateStage.SINGLE);
            JQuickDataSet result = nodeExecutor.executeNode(aggNode, taskContext);
            result.printTable();
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
        @Test
        public void testExecuteSort_Ascending() {
            JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null);
            List<JQuickSortPhysicalNode.OrderByItem> orderByItems = Collections.singletonList(new JQuickSortPhysicalNode.OrderByItem("age", true));
            JQuickSortPhysicalNode sortNode = new JQuickSortPhysicalNode(orderByItems, scanNode);
            JQuickDataSet result = nodeExecutor.executeNode(sortNode, taskContext);
            result.printTable();
            assertNotNull(result);
            assertEquals(5, result.size());
            int prevAge = -1;
            for (JQuickRow row : result.getRows()) {
                int age = (Integer) row.get("age");
                assertTrue(age >= prevAge);
                prevAge = age;
            }
        }

        @Test
        public void testExecuteSort_Descending() {
            JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null);
            List<JQuickSortPhysicalNode.OrderByItem> orderByItems = Collections.singletonList(new JQuickSortPhysicalNode.OrderByItem("salary", false));
            JQuickSortPhysicalNode sortNode = new JQuickSortPhysicalNode(orderByItems, scanNode);
            JQuickDataSet result = nodeExecutor.executeNode(sortNode, taskContext);
            result.printTable();
            assertNotNull(result);
            assertEquals(5, result.size());
            double prevSalary = Double.MAX_VALUE;
            for (JQuickRow row : result.getRows()) {
                double salary = (Double) row.get("salary");
                assertTrue(salary <= prevSalary);
                prevSalary = salary;
            }
            assertEquals(12000.0, (Double) result.getRows().get(0).get("salary"), 0.001);
        }

        @Test
        public void testExecuteSort_MultipleColumns() {
            JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null);
            List<JQuickSortPhysicalNode.OrderByItem> orderByItems = Arrays.asList(
                    new JQuickSortPhysicalNode.OrderByItem("department", true),
                    new JQuickSortPhysicalNode.OrderByItem("salary", false)
            );
            JQuickSortPhysicalNode sortNode = new JQuickSortPhysicalNode(orderByItems, scanNode);
            JQuickDataSet result = nodeExecutor.executeNode(sortNode, taskContext);
            result.printTable();
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

        @Test
        public void testExecuteLimit() {
            JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null);
            JQuickLimitPhysicalNode limitNode = new JQuickLimitPhysicalNode(3, 0, scanNode);
            JQuickDataSet result = nodeExecutor.executeNode(limitNode, taskContext);
            result.printTable();
            assertNotNull(result);
            assertEquals(3, result.size());
        }

        @Test
        public void testExecuteLimit_WithOffset() {
            JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null);
            // offset = 2, limit = 2
            JQuickLimitPhysicalNode limitNode = new JQuickLimitPhysicalNode(2, 2, scanNode);
            JQuickDataSet result = nodeExecutor.executeNode(limitNode, taskContext);
            result.printTable();
            assertNotNull(result);
            assertEquals(2, result.size());
        }

        @Test
        public void testExecuteLimit_OffsetExceedsSize() {
            JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null);
            JQuickLimitPhysicalNode limitNode = new JQuickLimitPhysicalNode(10, 100, scanNode);
            JQuickDataSet result = nodeExecutor.executeNode(limitNode, taskContext);
            result.printTable();
            assertNotNull(result);
            assertEquals(0, result.size());
        }

        @Test
        public void testExecuteTopN() {
            JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null);
            List<JQuickSortPhysicalNode.OrderByItem> orderByItems = Collections.singletonList(new JQuickSortPhysicalNode.OrderByItem("salary", false));
            JQuickTopNPhysicalNode topNNode = new JQuickTopNPhysicalNode(orderByItems, 3, 0, scanNode);
            JQuickDataSet result = nodeExecutor.executeNode(topNNode, taskContext);
            result.printTable();
            assertNotNull(result);
            assertEquals(3, result.size());
            // 工资前三: 12000, 10000, 9000
            assertEquals(12000.0, (Double) result.getRows().get(0).get("salary"), 0.001);
            assertEquals(10000.0, (Double) result.getRows().get(1).get("salary"), 0.001);
            assertEquals(9000.0, (Double) result.getRows().get(2).get("salary"), 0.001);
        }

        @Test
        public void testExecuteTopN_WithOffset() {
            JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null);
            List<JQuickSortPhysicalNode.OrderByItem> orderByItems = Collections.singletonList(new JQuickSortPhysicalNode.OrderByItem("salary", false));
            JQuickTopNPhysicalNode topNNode = new JQuickTopNPhysicalNode(orderByItems, 2, 1, scanNode);
            JQuickDataSet result = nodeExecutor.executeNode(topNNode, taskContext);
            result.printTable();
            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals(10000.0, (Double) result.getRows().get(0).get("salary"), 0.001);
            assertEquals(9000.0, (Double) result.getRows().get(1).get("salary"), 0.001);
        }
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
            JQuickValuesPhysicalNode valuesNode = new JQuickValuesPhysicalNode(Collections.emptyList(), Collections.emptyList(), Collections.emptyList());
            JQuickDataSet result = nodeExecutor.executeNode(valuesNode, taskContext);
            assertNotNull(result);
            assertEquals(0, result.size());
        }




        private JQuickValuesPhysicalNode createValuesNode(List<Integer> ids, List<String> names) {
            List<List<Object>> rows = new ArrayList<>();
            for (int i = 0; i < ids.size(); i++) {
                rows.add(Arrays.asList(ids.get(i)));
            }
            return new JQuickValuesPhysicalNode(rows, Arrays.asList("id", "name"), Arrays.asList(Integer.class, String.class));
        }


        @Test
        public void testExecuteExchange_HashPartition() {
            JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null);
            List<JQuickExpression> partitionKeys = Collections.singletonList(new JQuickColumnRefExpression("department"));
            JQuickExchangePhysicalNode exchangeNode = new JQuickExchangePhysicalNode(JQuickExchangeType.SHUFFLE, JQuickPartitionStrategy.HASH, partitionKeys, 3, scanNode);
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
            JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null);
            JQuickExpression predicate = new JQuickBinaryExpression(
                    new JQuickColumnRefExpression("age"),
                    new JQuickLiteralExpression(100),
                    JQuickBinaryOperator.GT
            );
            JQuickFilterPhysicalNode filterNode = new JQuickFilterPhysicalNode(predicate, scanNode);
            JQuickDataSet result = nodeExecutor.executeNode(filterNode, taskContext);
            result.printTable();
            assertNotNull(result);
            assertEquals(0, result.size());
        }

        @Test
        public void testExecuteProject_WithNullValues() {
            List<JQuickColumnMeta> cols = Collections.singletonList(new JQuickColumnMeta("nullable_col", String.class, "test"));
            List<JQuickRow> rows = Arrays.asList(
                    createRow("nullable_col", "value1"),
                    createRow("nullable_col", null),
                    createRow("nullable_col", "value3")
            );
            JQuickDataSourceManager.registerTable("test_null", new JQuickDataSet(cols, rows));
            JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("test_null", null, null, null);
            List<JQuickProjectPhysicalNode.SelectItem> selectItems = Collections.singletonList(new JQuickProjectPhysicalNode.SelectItem(new JQuickColumnRefExpression("nullable_col"), "nullable_col"));
            JQuickProjectPhysicalNode projectNode = new JQuickProjectPhysicalNode(
                    selectItems, scanNode, false
            );
            JQuickDataSet result = nodeExecutor.executeNode(projectNode, taskContext);
            result.printTable();
            assertNotNull(result);
            assertEquals(3, result.size());

            // 验证 null 值保持不变
            assertNull(result.getRows().get(1).get("nullable_col"));
        }


        @Test
        public void testTaskContext_ProcessedRowsTracking() {
            JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null);
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
}
