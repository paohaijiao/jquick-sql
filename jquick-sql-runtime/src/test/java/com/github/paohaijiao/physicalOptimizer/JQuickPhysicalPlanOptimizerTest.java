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
package com.github.paohaijiao.physicalOptimizer;

import com.github.paohaijiao.enums.JQuickJoinType;
import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.expression.domain.JQuickColumnRefExpression;
import com.github.paohaijiao.optimizer.JQuickPhysicalPlanOptimizer;
import com.github.paohaijiao.physical.JQuickPhysicalPlanNode;
import com.github.paohaijiao.physical.domain.JQuickPhysicalStats;
import com.github.paohaijiao.physical.node.*;
import com.google.gson.Gson;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

/**
 * packageName com.github.paohaijiao.physicalOptimizer
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/5/25
 */
public class JQuickPhysicalPlanOptimizerTest {

    private JQuickPhysicalPlanOptimizer optimizer;
    Gson gson = new Gson();

    @Before
    public void setUp() {
        optimizer = new JQuickPhysicalPlanOptimizer();
    }



    /**
     * 测试用的表扫描节点
     */
    static class TestTableScan extends JQuickTableScanPhysicalNode {

        private final long rowCount;

        public TestTableScan(String tableName, long rowCount) {
            super(tableName, null, new HashSet<>(Arrays.asList("id", "name", "age")), null);
            this.rowCount = rowCount;
        }

        @Override
        public JQuickPhysicalStats getStats() {
            return new JQuickPhysicalStats(rowCount, rowCount * 100, new HashMap<>());
        }
    }

    /**
     * 测试用的简单表达式
     */
    private JQuickExpression expr(String col) {
        return new JQuickColumnRefExpression(col);
    }

    /**
     * 创建Join Key对
     */
    private JQuickHashJoinPhysicalNode.JoinKeyPair joinKey(String left, String right) {
        return new JQuickHashJoinPhysicalNode.JoinKeyPair(expr(left), expr(right));
    }

    /**
     * 创建聚合函数
     */
    private JQuickHashAggregatePhysicalNode.AggregateFunction aggFunc(String name, String arg, String alias) {
        return new JQuickHashAggregatePhysicalNode.AggregateFunction(name, expr(arg), false, alias);
    }
    @Test
    public void testJoinOptimization_BroadcastHash() {
        TestTableScan left = new TestTableScan("small_left", 100);
        TestTableScan right = new TestTableScan("small_right", 50);
        List<JQuickHashJoinPhysicalNode.JoinKeyPair> joinKeys = Arrays.asList(joinKey("id", "id"));
        JQuickHashJoinPhysicalNode join = new JQuickHashJoinPhysicalNode(JQuickJoinType.INNER, left, right, null, joinKeys, JQuickHashJoinPhysicalNode.BuildSide.LEFT, JQuickHashJoinPhysicalNode.JoinDistribution.LOCAL);
        JQuickPhysicalPlanNode optimized = optimizer.optimize(join);
        String string=gson.toJson(optimized);
        System.out.println(string);
        System.out.println(optimized);
    }

    @Test
    public void testJoinOptimization_Local() {
        // 中等表Join：左表5000行，右表3000行（都小于10000）
        TestTableScan left = new TestTableScan("medium_left", 5000);
        TestTableScan right = new TestTableScan("medium_right", 3000);
        List<JQuickHashJoinPhysicalNode.JoinKeyPair> joinKeys = Arrays.asList(joinKey("id", "id"));
        JQuickHashJoinPhysicalNode join = new JQuickHashJoinPhysicalNode(JQuickJoinType.INNER, left, right, null, joinKeys, JQuickHashJoinPhysicalNode.BuildSide.LEFT, JQuickHashJoinPhysicalNode.JoinDistribution.SHUFFLE_HASH);
        JQuickPhysicalPlanNode optimized = optimizer.optimize(join);
        String string=gson.toJson(optimized);
        System.out.println(string);
        System.out.println(optimized);

    }

    @Test
    public void testJoinOptimization_Partitioned() {
        // 大表Join：左表200000行，右表150000行（大于100000）
        TestTableScan left = new TestTableScan("large_left", 200000);
        TestTableScan right = new TestTableScan("large_right", 150000);
        List<JQuickHashJoinPhysicalNode.JoinKeyPair> joinKeys = Arrays.asList(joinKey("id", "id"));
        JQuickHashJoinPhysicalNode join = new JQuickHashJoinPhysicalNode(
                JQuickJoinType.INNER, left, right, null, joinKeys,
                JQuickHashJoinPhysicalNode.BuildSide.LEFT,
                JQuickHashJoinPhysicalNode.JoinDistribution.LOCAL
        );
        JQuickPhysicalPlanNode optimized = optimizer.optimize(join);
        String string=gson.toJson(optimized);
        System.out.println(string);
  }

    @Test
    public void testJoinOptimization_BuildSideSelection() {
        // 左表100行（小），右表10000行（大），小表应作为Build侧
        TestTableScan left = new TestTableScan("small", 100);
        TestTableScan right = new TestTableScan("large", 10000);
        List<JQuickHashJoinPhysicalNode.JoinKeyPair> joinKeys = Arrays.asList(joinKey("id", "id"));
        JQuickHashJoinPhysicalNode join = new JQuickHashJoinPhysicalNode(JQuickJoinType.INNER, left, right, null, joinKeys, JQuickHashJoinPhysicalNode.BuildSide.RIGHT, JQuickHashJoinPhysicalNode.JoinDistribution.LOCAL);
        JQuickPhysicalPlanNode optimized = optimizer.optimize(join);
        String string=gson.toJson(optimized);
        System.out.println(string);
        System.out.println(optimized);
    }

    @Test
    public void testJoinOptimization_DefaultCase() {
        // 边界情况：左表10000行，右表10000行（等于阈值）
        TestTableScan left = new TestTableScan("left", 10000);
        TestTableScan right = new TestTableScan("right", 10000);
        List<JQuickHashJoinPhysicalNode.JoinKeyPair> joinKeys = Arrays.asList(joinKey("id", "id"));
        JQuickHashJoinPhysicalNode join = new JQuickHashJoinPhysicalNode(
                JQuickJoinType.INNER, left, right, null, joinKeys,
                JQuickHashJoinPhysicalNode.BuildSide.LEFT,
                JQuickHashJoinPhysicalNode.JoinDistribution.LOCAL
        );
        JQuickPhysicalPlanNode optimized = optimizer.optimize(join);
        String string=gson.toJson(optimized);
        System.out.println(string);
        System.out.println(optimized);
    }
    @Test
    public void testAggregateOptimization_TwoPhase() {
        // 大数据量聚合（>10000行且有分组键）应转换为两阶段聚合
        TestTableScan child = new TestTableScan("large_table", 20000);
        List<JQuickExpression> groupKeys = Arrays.asList(expr("age"));
        List<JQuickHashAggregatePhysicalNode.AggregateFunction> aggregates = Arrays.asList(aggFunc("COUNT", "id", "cnt"), aggFunc("SUM", "amount", "total"));
        JQuickHashAggregatePhysicalNode aggregate = new JQuickHashAggregatePhysicalNode(groupKeys, aggregates, child, null, JQuickHashAggregatePhysicalNode.AggregateStage.SINGLE);
        JQuickPhysicalPlanNode optimized = optimizer.optimize(aggregate);
        String string=gson.toJson(optimized);
        System.out.println(string);
        System.out.println(optimized);

    }

    @Test
    public void testAggregateOptimization_SinglePhase() {
        // 小数据量聚合（<10000行）应保持单阶段
        TestTableScan child = new TestTableScan("small_table", 5000);
        List<JQuickExpression> groupKeys = Arrays.asList(expr("age"));
        List<JQuickHashAggregatePhysicalNode.AggregateFunction> aggregates = Arrays.asList(aggFunc("COUNT", "id", "cnt"));
        JQuickHashAggregatePhysicalNode aggregate = new JQuickHashAggregatePhysicalNode(groupKeys, aggregates, child, null, JQuickHashAggregatePhysicalNode.AggregateStage.SINGLE);
        JQuickPhysicalPlanNode optimized = optimizer.optimize(aggregate);
        String string=gson.toJson(optimized);
        System.out.println(string);
        System.out.println(optimized);
    }

    @Test
    public void testAggregateOptimization_WithoutGroupKeys() {
        // 无分组键的聚合（全表聚合）应保持单阶段
        TestTableScan child = new TestTableScan("large_table", 20000);
        List<JQuickExpression> groupKeys = Collections.emptyList();
        List<JQuickHashAggregatePhysicalNode.AggregateFunction> aggregates = Arrays.asList(aggFunc("COUNT", "*", "total"));
        JQuickHashAggregatePhysicalNode aggregate = new JQuickHashAggregatePhysicalNode(groupKeys, aggregates, child, null, JQuickHashAggregatePhysicalNode.AggregateStage.SINGLE);
        JQuickPhysicalPlanNode optimized = optimizer.optimize(aggregate);
        String string=gson.toJson(optimized);
        System.out.println(string);
        System.out.println(optimized);
    }

    @Test
    public void testAggregateOptimization_AvgFunctionSplit() {
        // AVG函数应在Partial阶段拆分为SUM和COUNT
        TestTableScan child = new TestTableScan("large_table", 20000);
        List<JQuickExpression> groupKeys = Arrays.asList(expr("dept_id"));
        List<JQuickHashAggregatePhysicalNode.AggregateFunction> aggregates = Arrays.asList(aggFunc("AVG", "salary", "avg_salary"));
        JQuickHashAggregatePhysicalNode aggregate = new JQuickHashAggregatePhysicalNode(groupKeys, aggregates, child, null, JQuickHashAggregatePhysicalNode.AggregateStage.SINGLE);
        JQuickPhysicalPlanNode optimized = optimizer.optimize(aggregate);
        // 获取Partial阶段聚合节点
        JQuickHashAggregatePhysicalNode finalAgg = (JQuickHashAggregatePhysicalNode) optimized;
        JQuickExchangePhysicalNode exchange = (JQuickExchangePhysicalNode) finalAgg.getChild();
        JQuickHashAggregatePhysicalNode partialAgg = (JQuickHashAggregatePhysicalNode) exchange.getChild();
        // Partial阶段应该有2个聚合函数（SUM和COUNT）
        assertEquals(2, partialAgg.getAggregates().size());
        String func1 = partialAgg.getAggregates().get(0).getFunctionName();
        String func2 = partialAgg.getAggregates().get(1).getFunctionName();
        assertTrue((func1.equals("SUM") && func2.equals("COUNT")) || (func1.equals("COUNT") && func2.equals("SUM")));
        String string=gson.toJson(optimized);
        System.out.println(string);
        System.out.println(optimized);
    }

    @Test
    public void testAggregateOptimization_MaxMinFunctions() {
        // MAX/MIN函数在Partial阶段应保持原样
        TestTableScan child = new TestTableScan("large_table", 20000);
        List<JQuickExpression> groupKeys = Arrays.asList(expr("category"));
        List<JQuickHashAggregatePhysicalNode.AggregateFunction> aggregates = Arrays.asList(aggFunc("MAX", "price", "max_price"), aggFunc("MIN", "price", "min_price"));
        JQuickHashAggregatePhysicalNode aggregate = new JQuickHashAggregatePhysicalNode(groupKeys, aggregates, child, null, JQuickHashAggregatePhysicalNode.AggregateStage.SINGLE);
        JQuickPhysicalPlanNode optimized = optimizer.optimize(aggregate);
        JQuickHashAggregatePhysicalNode finalAgg = (JQuickHashAggregatePhysicalNode) optimized;
        JQuickExchangePhysicalNode exchange = (JQuickExchangePhysicalNode) finalAgg.getChild();
        JQuickHashAggregatePhysicalNode partialAgg = (JQuickHashAggregatePhysicalNode) exchange.getChild();
        assertEquals(2, partialAgg.getAggregates().size());
        assertEquals("MAX", partialAgg.getAggregates().get(0).getFunctionName());
        assertEquals("MIN", partialAgg.getAggregates().get(1).getFunctionName());
        String string=gson.toJson(optimized);
        System.out.println(string);
        System.out.println(optimized);
    }

    @Test
    public void testAggregateOptimization_BoundaryRowCount() {
        // 边界情况：恰好10000行
        TestTableScan child = new TestTableScan("boundary_table", 10000);
        List<JQuickExpression> groupKeys = Arrays.asList(expr("age"));
        List<JQuickHashAggregatePhysicalNode.AggregateFunction> aggregates = Arrays.asList(aggFunc("COUNT", "id", "cnt"));
        JQuickHashAggregatePhysicalNode aggregate = new JQuickHashAggregatePhysicalNode(groupKeys, aggregates, child, null, JQuickHashAggregatePhysicalNode.AggregateStage.SINGLE);
        JQuickPhysicalPlanNode optimized = optimizer.optimize(aggregate);
        String string=gson.toJson(optimized);
        System.out.println(string);
        System.out.println(optimized);
    }

    @Test
    public void testLimitPushdown_SortAndLimitToTopN() {
        // Limit + Sort 应合并为 TopN
        TestTableScan child = new TestTableScan("table", 1000);
        List<JQuickSortPhysicalNode.OrderByItem> orderBy = Arrays.asList(new JQuickSortPhysicalNode.OrderByItem("age", true), new JQuickSortPhysicalNode.OrderByItem("name", false));
        JQuickSortPhysicalNode sort = new JQuickSortPhysicalNode(orderBy, child);
        JQuickLimitPhysicalNode limit = new JQuickLimitPhysicalNode(10, sort);
        JQuickPhysicalPlanNode optimized = optimizer.optimize(limit);
        assertTrue(optimized instanceof JQuickTopNPhysicalNode);
        JQuickTopNPhysicalNode topN = (JQuickTopNPhysicalNode) optimized;
        assertEquals(10, topN.getLimit());
        assertEquals(0, topN.getOffset());
        assertEquals(2, topN.getOrderByItems().size());
        assertEquals("age", topN.getOrderByItems().get(0).getColumnName());
        assertTrue(topN.getOrderByItems().get(0).isAscending());
        assertEquals("name", topN.getOrderByItems().get(1).getColumnName());
        assertFalse(topN.getOrderByItems().get(1).isAscending());
        String string=gson.toJson(optimized);
        System.out.println(string);
        System.out.println(optimized);
    }

    @Test
    public void testLimitPushdown_WithOffset() {
        // Limit + Offset + Sort 应合并为带Offset的TopN
        TestTableScan child = new TestTableScan("table", 1000);
        List<JQuickSortPhysicalNode.OrderByItem> orderBy = Arrays.asList(new JQuickSortPhysicalNode.OrderByItem("id", true));
        JQuickSortPhysicalNode sort = new JQuickSortPhysicalNode(orderBy, child);
        JQuickLimitPhysicalNode limit = new JQuickLimitPhysicalNode(20, 5, sort);
        JQuickPhysicalPlanNode optimized = optimizer.optimize(limit);
        assertTrue(optimized instanceof JQuickTopNPhysicalNode);
        JQuickTopNPhysicalNode topN = (JQuickTopNPhysicalNode) optimized;
        assertEquals(20, topN.getLimit());
        assertEquals(5, topN.getOffset());
        String string=gson.toJson(optimized);
        System.out.println(string);
        System.out.println(optimized);
    }

    @Test
    public void testLimitPushdown_LimitWithoutSort() {
        // 只有Limit没有Sort，不应转换为TopN
        TestTableScan child = new TestTableScan("table", 1000);
        JQuickLimitPhysicalNode limit = new JQuickLimitPhysicalNode(10, child);
        JQuickPhysicalPlanNode optimized = optimizer.optimize(limit);
        assertTrue(optimized instanceof JQuickLimitPhysicalNode);
        assertFalse(optimized instanceof JQuickTopNPhysicalNode);
        String string=gson.toJson(optimized);
        System.out.println(string);
        System.out.println(optimized);
    }

    @Test
    public void testLimitPushdown_SortWithoutLimit() {
        // 只有Sort没有Limit，不应转换
        TestTableScan child = new TestTableScan("table", 1000);
        List<JQuickSortPhysicalNode.OrderByItem> orderBy = Arrays.asList(new JQuickSortPhysicalNode.OrderByItem("age", true));
        JQuickSortPhysicalNode sort = new JQuickSortPhysicalNode(orderBy, child);
        JQuickPhysicalPlanNode optimized = optimizer.optimize(sort);
        assertTrue(optimized instanceof JQuickSortPhysicalNode);
        assertFalse(optimized instanceof JQuickTopNPhysicalNode);
        String string=gson.toJson(optimized);
        System.out.println(string);
        System.out.println(optimized);
    }

    @Test
    public void testLimitPushdown_NestedLimitAndSort() {
        // 嵌套场景：Sort -> Limit -> Project
        TestTableScan child = new TestTableScan("table", 1000);
        List<JQuickSortPhysicalNode.OrderByItem> orderBy = Arrays.asList(new JQuickSortPhysicalNode.OrderByItem("age", true));
        JQuickSortPhysicalNode sort = new JQuickSortPhysicalNode(orderBy, child);
        JQuickLimitPhysicalNode limit = new JQuickLimitPhysicalNode(10, sort);
        List<JQuickProjectPhysicalNode.SelectItem> selectItems = Arrays.asList( new JQuickProjectPhysicalNode.SelectItem(expr("id"), "id"),      new JQuickProjectPhysicalNode.SelectItem(expr("name"), "name") );
        JQuickProjectPhysicalNode project = new JQuickProjectPhysicalNode(selectItems, limit, false);
        JQuickPhysicalPlanNode optimized = optimizer.optimize(project);
        // Project应该还在顶层，其子节点应该是TopN
        assertTrue(optimized instanceof JQuickProjectPhysicalNode);
        JQuickProjectPhysicalNode resultProject = (JQuickProjectPhysicalNode) optimized;
        assertTrue(resultProject.getChild() instanceof JQuickTopNPhysicalNode);
        String string=gson.toJson(optimized);
        System.out.println(string);
        System.out.println(optimized);
    }
    @Test
    public void testExchangeInjection_HashJoin() {
        // HashJoin应该被注入Exchange
        TestTableScan left = new TestTableScan("left", 50000);
        TestTableScan right = new TestTableScan("right", 50000);
        List<JQuickHashJoinPhysicalNode.JoinKeyPair> joinKeys = Arrays.asList(joinKey("id", "id"));
        JQuickHashJoinPhysicalNode join = new JQuickHashJoinPhysicalNode( JQuickJoinType.INNER, left, right, null, joinKeys, JQuickHashJoinPhysicalNode.BuildSide.LEFT,  JQuickHashJoinPhysicalNode.JoinDistribution.LOCAL);
        JQuickPhysicalPlanNode optimized = optimizer.optimize(join);
        // Join可能被Exchange包装（取决于数据量）
        if (optimized instanceof JQuickExchangePhysicalNode) {
            JQuickExchangePhysicalNode exchange = (JQuickExchangePhysicalNode) optimized;
        }
        String string=gson.toJson(optimized);
        System.out.println(string);
        System.out.println(optimized);
    }

    @Test
    public void testExchangeInjection_FilterNotNeeded() {
        // Filter节点不应注入Exchange
        TestTableScan child = new TestTableScan("table", 1000);
        JQuickFilterPhysicalNode filter = new JQuickFilterPhysicalNode(expr("age > 18"), child);
        JQuickPhysicalPlanNode optimized = optimizer.optimize(filter);
        assertTrue(optimized instanceof JQuickFilterPhysicalNode);
        assertFalse(optimized instanceof JQuickExchangePhysicalNode);
        String string=gson.toJson(optimized);
        System.out.println(string);
        System.out.println(optimized);
    }

    @Test
    public void testExchangeInjection_ProjectNotNeeded() {
        TestTableScan child = new TestTableScan("table", 1000);
        List<JQuickProjectPhysicalNode.SelectItem> selectItems = Arrays.asList(new JQuickProjectPhysicalNode.SelectItem(expr("id"), "id"));
        JQuickProjectPhysicalNode project = new JQuickProjectPhysicalNode(selectItems, child, false);
        JQuickPhysicalPlanNode optimized = optimizer.optimize(project);
        assertTrue(optimized instanceof JQuickProjectPhysicalNode);
        assertFalse(optimized instanceof JQuickExchangePhysicalNode);
        String string=gson.toJson(optimized);
        System.out.println(string);
        System.out.println(optimized);
    }

}
