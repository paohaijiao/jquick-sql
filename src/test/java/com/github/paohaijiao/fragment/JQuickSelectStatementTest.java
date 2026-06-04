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

import com.github.paohaijiao.ast2logic.JQuickASTToLogicalPlanVisitor;
import com.github.paohaijiao.distributed.JQuickDistributedPlan;
import com.github.paohaijiao.enums.JQuickBinaryOperator;
import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.expression.domain.JQuickBinaryExpression;
import com.github.paohaijiao.expression.domain.JQuickColumnRefExpression;
import com.github.paohaijiao.expression.domain.JQuickLiteralExpression;
import com.github.paohaijiao.logic.JQuickLogicalPlanNode;
import com.github.paohaijiao.logic.domain.JQuickAggregateNode;
import com.github.paohaijiao.logic.domain.JQuickProjectNode;
import com.github.paohaijiao.logic.domain.JQuickTableScanNode;
import com.github.paohaijiao.logic.domain.JQuickWithNode;
import com.github.paohaijiao.logic2physical.JQuickPhysicalPlanGenerator;
import com.github.paohaijiao.physical.JQuickPhysicalPlanNode;
import com.github.paohaijiao.physical.node.JQuickProjectPhysicalNode;
import com.github.paohaijiao.physical.node.JQuickTableScanPhysicalNode;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

/**
 * packageName com.github.paohaijiao.physical
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/5/24
 */
public class JQuickSelectStatementTest {
    private JQuickPhysicalPlanGenerator generator;
    private JQuickASTToLogicalPlanVisitor visitor;
    private JQuickFragmenter fragmenter;
    private JQuickFragmenter verboseFragmenter;

    @Before
    public void setUp() {
        generator = new JQuickPhysicalPlanGenerator();
        visitor=new JQuickASTToLogicalPlanVisitor();
        fragmenter = new JQuickFragmenter(4);
        verboseFragmenter = new JQuickFragmenter(8);
    }

    /**
     * 创建简单的表扫描节点
     */
    private JQuickTableScanNode createTableScan(String tableName) {
        return new JQuickTableScanNode(tableName);
    }

    /**
     * 创建带别名的表扫描节点
     */
    private JQuickTableScanNode createTableScan(String tableName, String alias) {
        return new JQuickTableScanNode(tableName, alias);
    }

    /**
     * 创建投影节点
     */
    private JQuickProjectNode createProject(JQuickLogicalPlanNode child, String... columns) {
        List<JQuickProjectNode.SelectItem> items = new ArrayList<>();
        for (String col : columns) {
            items.add(new JQuickProjectNode.SelectItem(
                    new JQuickColumnRefExpression(col), col));
        }
        return new JQuickProjectNode(items, child);
    }

    /**
     * 创建等值条件
     */
    private JQuickExpression createEqualityCondition(String leftCol, String rightCol) {
        return new JQuickBinaryExpression(new JQuickColumnRefExpression(leftCol), new JQuickColumnRefExpression(rightCol), JQuickBinaryOperator.EQ);
    }
    /**
     * SQL: SELECT * FROM users AS u
     *
     * 逻辑：全表扫描，返回所有列
     */
    @Test
    public void testTableScanConversion() {
        JQuickTableScanNode logicalScan = new JQuickTableScanNode("users", "u");
        JQuickPhysicalPlanNode physicalPlan = generator.generate(logicalScan);
        assertNotNull(physicalPlan);
        assertEquals("TableScan", physicalPlan.getNodeType());
        assertTrue(physicalPlan instanceof JQuickTableScanPhysicalNode);
        JQuickTableScanPhysicalNode scanNode = (JQuickTableScanPhysicalNode) physicalPlan;
        assertEquals("users", scanNode.getTableName());
        assertEquals("u", scanNode.getAlias());
        JQuickDistributedPlan distributedPlan= fragmenter.fragment(physicalPlan);
        fragmenter.printFragments(distributedPlan);
    }
    /**
     * SQL: SELECT id, name, email FROM users AS u
     *
     * 逻辑：列裁剪优化，只读取需要的列
     */
    @Test
    public void testTableScanWithColumnPruning() {
        Set<String> requiredCols = new HashSet<>(Arrays.asList("id", "name", "email"));
        JQuickTableScanNode logicalScan = new JQuickTableScanNode("users", "u", requiredCols);
        JQuickPhysicalPlanNode physicalPlan = generator.generate(logicalScan);
        JQuickTableScanPhysicalNode scanNode = (JQuickTableScanPhysicalNode) physicalPlan;
        assertEquals(requiredCols, scanNode.getRequiredColumns());
        assertEquals(3, scanNode.getOutputSchema().size());
        JQuickDistributedPlan distributedPlan= fragmenter.fragment(physicalPlan);
        fragmenter.printFragments(distributedPlan);
    }
    /**
     * SQL: SELECT * FROM users WHERE age > 18
     *
     * 逻辑：表扫描时下推过滤条件（谓词下推优化）
     */
    @Test
    public void testTableScanWithFilterPredicate() {
        JQuickExpression filter = new JQuickBinaryExpression(new JQuickColumnRefExpression("age"), new JQuickLiteralExpression(18), JQuickBinaryOperator.GT);
        JQuickTableScanNode logicalScan = new JQuickTableScanNode("users", null, null, filter);
        JQuickPhysicalPlanNode physicalPlan = generator.generate(logicalScan);
        JQuickTableScanPhysicalNode scanNode = (JQuickTableScanPhysicalNode) physicalPlan;
        assertNotNull(scanNode.getFilterPredicate());
        JQuickDistributedPlan distributedPlan= fragmenter.fragment(physicalPlan);
        fragmenter.printFragments(distributedPlan);
    }
    /**
     * 测试1：简单CTE - 单层CTE，主查询直接引用
     *
     * SQL示例：
     * WITH sales_summary AS (
     *     SELECT category, SUM(amount) as total
     *     FROM orders
     *     GROUP BY category
     * )
     * SELECT category, total FROM sales_summary
     */
    @Test
    public void testSimpleCte() {
        JQuickTableScanNode ordersScan = createTableScan("orders");
        List<JQuickAggregateNode.AggregateFunction> aggregates = new ArrayList<>();
        aggregates.add(new JQuickAggregateNode.AggregateFunction("SUM", new JQuickColumnRefExpression("amount"), false, "total"));
        List<JQuickExpression> groupKeys = new ArrayList<>();
        groupKeys.add(new JQuickColumnRefExpression("category"));
        JQuickAggregateNode cteAggregate = new JQuickAggregateNode(groupKeys, aggregates, ordersScan, null, false);
        List<JQuickProjectNode.SelectItem> cteProjectItems = new ArrayList<>();
        cteProjectItems.add(new JQuickProjectNode.SelectItem(new JQuickColumnRefExpression("category"), "category"));
        cteProjectItems.add(new JQuickProjectNode.SelectItem(new JQuickColumnRefExpression("total"), "total"));
        JQuickProjectNode cteProject = new JQuickProjectNode(cteProjectItems, cteAggregate);
        JQuickTableScanNode cteRef = createTableScan("sales_summary");
        JQuickProjectNode mainProject = createProject(cteRef, "category", "total");
        Map<String, JQuickLogicalPlanNode> ctes = new LinkedHashMap<>();
        ctes.put("sales_summary", cteProject);
        JQuickWithNode withNode = new JQuickWithNode(mainProject, ctes);
        JQuickPhysicalPlanNode physicalPlan = generator.generate(withNode);
        JQuickDistributedPlan distributedPlan= fragmenter.fragment(physicalPlan);
        fragmenter.printFragments(distributedPlan);

    }


}
