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
package com.github.paohaijiao.fragment;

import com.github.paohaijiao.distributed.JQuickDistributedPlan;
import com.github.paohaijiao.enums.JQuickExchangeType;
import com.github.paohaijiao.enums.JQuickFragmentType;
import com.github.paohaijiao.enums.JQuickJoinType;
import com.github.paohaijiao.enums.JQuickPartitionStrategy;
import com.github.paohaijiao.exchange.JQuickExchangeNode;
import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.expression.domain.JQuickBinaryExpression;
import com.github.paohaijiao.expression.domain.JQuickColumnRefExpression;
import com.github.paohaijiao.expression.domain.JQuickLiteralExpression;
import com.github.paohaijiao.physical.JQuickPhysicalPlanNode;
import com.github.paohaijiao.physical.node.*;
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
    }

    @Test
    public void testFragmentWithParallelism() {
        JQuickTableScanPhysicalNode scanNode = createTableScanNode("users");
        JQuickFragment fragment = new JQuickFragment(JQuickFragmentType.SOURCE, scanNode);
        fragment.setParallelism(8);
        assertEquals(8, fragment.getParallelism());
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
        JQuickExchangeNode outputExchange = new JQuickExchangeNode(
                "exchange_1",
                JQuickExchangeType.SHUFFLE,
                JQuickPartitionStrategy.HASH,
                Collections.singletonList(new JQuickColumnRefExpression("id")),
                4
        );
        fragment.setOutput(outputExchange);
        JQuickExchangeNode inputExchange = new JQuickExchangeNode(
                "input_exchange_1",
                JQuickExchangeType.RECEIVE,
                JQuickPartitionStrategy.HASH,
                Collections.singletonList(new JQuickColumnRefExpression("id")),
                4
        );
        fragment.addInput(inputExchange);
        assertEquals(outputExchange, fragment.getOutput());
        assertEquals(1, fragment.getInputs().size());
        assertEquals(inputExchange, fragment.getInputs().get(0));
    }

    @Test
    public void testFragmentIdIncrement() {
        JQuickFragment fragment1 = new JQuickFragment(JQuickFragmentType.SOURCE, createTableScanNode("t1"));
        JQuickFragment fragment2 = new JQuickFragment(JQuickFragmentType.SOURCE, createTableScanNode("t2"));
        JQuickFragment fragment3 = new JQuickFragment(JQuickFragmentType.SOURCE, createTableScanNode("t3"));
        assertTrue(fragment2.getFragmentId() > fragment1.getFragmentId());
        assertTrue(fragment3.getFragmentId() > fragment2.getFragmentId());
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
        assertEquals(scanNode, root.getPlan());
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
}
