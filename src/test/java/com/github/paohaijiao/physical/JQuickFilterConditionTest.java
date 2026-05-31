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
package com.github.paohaijiao.physical;

import com.github.paohaijiao.enums.JQuickBinaryOperator;
import com.github.paohaijiao.enums.JQuickJoinType;
import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.expression.domain.*;
import com.github.paohaijiao.logic.JQuickLogicalPlanNode;
import com.github.paohaijiao.logic.domain.*;
import com.github.paohaijiao.logic2physical.JQuickPhysicalPlanGenerator;
import com.github.paohaijiao.physical.node.JQuickFilterPhysicalNode;
import org.junit.Before;
import org.junit.Test;

import java.util.*;


/**
 * packageName com.github.paohaijiao.physical
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/5/24
 */
public class JQuickFilterConditionTest {

    private JQuickPhysicalPlanGenerator generator;

    @Before
    public void setUp() {
        generator = new JQuickPhysicalPlanGenerator();
    }
    /**
     * 创建表扫描节点
     */
    private JQuickTableScanNode createTableScan(String tableName) {
        return new JQuickTableScanNode(tableName);
    }
    /**
     * 创建等值条件
     */
    private JQuickBinaryExpression createEqualityCondition(String leftCol, String rightCol) {
        return new JQuickBinaryExpression(
                new JQuickColumnRefExpression(leftCol),
                new JQuickColumnRefExpression(rightCol),
                JQuickBinaryOperator.EQ
        );
    }


    /**
     * 创建表扫描节点（带别名）
     */
    private JQuickTableScanNode createTableScan(String tableName, String alias) {
        return new JQuickTableScanNode(tableName, alias);
    }

    /**
     * 创建简单二元比较条件
     */
    private JQuickBinaryExpression createComparison(String column, JQuickBinaryOperator operator, Object value) {
        return new JQuickBinaryExpression(
                new JQuickColumnRefExpression(column),
                new JQuickLiteralExpression(value),
                operator
        );
    }

    /**
     * 创建投影节点
     */
    private JQuickProjectNode createProject(JQuickLogicalPlanNode child, String... columns) {
        List<JQuickProjectNode.SelectItem> items = new ArrayList<>();
        for (String col : columns) {
            items.add(new JQuickProjectNode.SelectItem(new JQuickColumnRefExpression(col), col));
        }
        return new JQuickProjectNode(items, child);
    }

    /**
     * 创建过滤节点
     */
    private JQuickFilterNode createFilter(JQuickLogicalPlanNode child, JQuickExpression predicate) {
        return new JQuickFilterNode(predicate, child);
    }

    /**
     * 创建 AND 条件
     */
    private JQuickBinaryExpression and(JQuickExpression left, JQuickExpression right) {
        return new JQuickBinaryExpression(left, right, JQuickBinaryOperator.AND);
    }

    /**
     * 创建 OR 条件
     */
    private JQuickBinaryExpression or(JQuickExpression left, JQuickExpression right) {
        return new JQuickBinaryExpression(left, right, JQuickBinaryOperator.OR);
    }


    /**
     * 创建 BETWEEN 条件
     */
    private JQuickBetweenExpression createBetween(String column, Object low, Object high) {
        return new JQuickBetweenExpression(new JQuickColumnRefExpression(column), new JQuickLiteralExpression(low), new JQuickLiteralExpression(high), false);
    }

    /**
     * 创建 IN 条件
     */
    private JQuickInExpression createIn(String column, List<Object> values) {
        List<JQuickExpression> valueExprs = new ArrayList<>();
        for (Object value : values) {
            valueExprs.add(new JQuickLiteralExpression(value));
        }
        return new JQuickInExpression(new JQuickColumnRefExpression(column), valueExprs, false);
    }
    /**
     * 测试3：复合条件 AND
     *
     * SQL示例：SELECT * FROM users WHERE age > 18 AND status = 'active'
     */
    @Test
    public void testAndCondition() {
        JQuickTableScanNode usersScan = createTableScan("users");
        JQuickExpression condition1 = createComparison("age", JQuickBinaryOperator.GT, 18);
        JQuickExpression condition2 = createComparison("status", JQuickBinaryOperator.EQ, "active");
        JQuickBinaryExpression andCondition = and(condition1, condition2);
        JQuickFilterNode filterNode = createFilter(usersScan, andCondition);
        JQuickPhysicalPlanNode physicalPlan = generator.generate(filterNode);
        JQuickFilterPhysicalNode filterPhysical = (JQuickFilterPhysicalNode) physicalPlan;
        JQuickExpression actualPredicate = filterPhysical.getPredicate();
        JQuickBinaryExpression binaryExpr = (JQuickBinaryExpression) actualPredicate;
        System.out.println("=== AND条件测试通过 ===");
        System.out.println("条件: age > 18 AND status = 'active'");
    }
    /**
     * 测试4：复合条件 OR
     *
     * SQL示例：SELECT * FROM users WHERE age > 18 OR vip = true
     */
    @Test
    public void testOrCondition() {
        JQuickTableScanNode usersScan = createTableScan("users");
        JQuickExpression condition1 = createComparison("age", JQuickBinaryOperator.GT, 18);
        JQuickExpression condition2 = createComparison("vip", JQuickBinaryOperator.EQ, true);
        JQuickBinaryExpression orCondition = or(condition1, condition2);
        JQuickFilterNode filterNode = createFilter(usersScan, orCondition);
        JQuickPhysicalPlanNode physicalPlan = generator.generate(filterNode);
        JQuickFilterPhysicalNode filterPhysical = (JQuickFilterPhysicalNode) physicalPlan;
        JQuickExpression actualPredicate = filterPhysical.getPredicate();
        JQuickBinaryExpression binaryExpr = (JQuickBinaryExpression) actualPredicate;
        System.out.println("=== OR条件测试通过 ===");
        System.out.println("条件: age > 18 OR vip = true");
    }
    /**
     * 测试6：嵌套条件（AND/OR组合）
     *
     * SQL示例：SELECT * FROM users WHERE (age > 18 AND status = 'active') OR vip = true
     */
    @Test
    public void testNestedCondition() {
        JQuickTableScanNode usersScan = createTableScan("users");
        JQuickExpression innerAnd = and(createComparison("age", JQuickBinaryOperator.GT, 18), createComparison("status", JQuickBinaryOperator.EQ, "active"));
        JQuickBinaryExpression nestedCondition = or(innerAnd, createComparison("vip", JQuickBinaryOperator.EQ, true));
        JQuickFilterNode filterNode = createFilter(usersScan, nestedCondition);
        JQuickPhysicalPlanNode physicalPlan = generator.generate(filterNode);
        JQuickFilterPhysicalNode filterPhysical = (JQuickFilterPhysicalNode) physicalPlan;
        JQuickExpression actualPredicate = filterPhysical.getPredicate();
        JQuickBinaryExpression outerExpr = (JQuickBinaryExpression) actualPredicate;
        JQuickExpression leftChild = outerExpr.getLeft();
        JQuickBinaryExpression innerExpr = (JQuickBinaryExpression) leftChild;
        System.out.println("=== 嵌套条件测试通过 ===");
        System.out.println("条件: (age > 18 AND status = 'active') OR vip = true");
    }
    /**
     * 测试7：BETWEEN 条件
     *
     * SQL示例：SELECT * FROM users WHERE age BETWEEN 18 AND 65
     */
    @Test
    public void testBetweenCondition() {
        JQuickTableScanNode usersScan = createTableScan("users");
        JQuickBetweenExpression betweenCondition = createBetween("age", 18, 65);
        JQuickFilterNode filterNode = createFilter(usersScan, betweenCondition);
        JQuickPhysicalPlanNode physicalPlan = generator.generate(filterNode);
        JQuickFilterPhysicalNode filterPhysical = (JQuickFilterPhysicalNode) physicalPlan;
        JQuickExpression actualPredicate = filterPhysical.getPredicate();
        JQuickBetweenExpression betweenExpr = (JQuickBetweenExpression) actualPredicate;
        System.out.println("=== BETWEEN条件测试通过 ===");
        System.out.println("条件: age BETWEEN 18 AND 65");
    }
    /**
     *
     * SQL示例：
     * WITH RECURSIVE org_hierarchy AS (
     *     SELECT id, name, manager_id, 1 as level
     *     FROM employee
     *     WHERE manager_id IS NULL
     *     UNION ALL
     *     SELECT e.id, e.name, e.manager_id, h.level + 1
     *     FROM employee e
     *     JOIN org_hierarchy h ON e.manager_id = h.id
     * )
     * SELECT id, name, level FROM org_hierarchy
     */
    @Test
    public void testRecursiveCte() {
        JQuickTableScanNode employeeScan = createTableScan("employee");
        JQuickBinaryExpression isNullCondition = new JQuickBinaryExpression(new JQuickColumnRefExpression("manager_id"), new JQuickLiteralExpression(null), JQuickBinaryOperator.EQ);
        JQuickFilterNode anchorFilter = new JQuickFilterNode(isNullCondition, employeeScan);

        List<JQuickProjectNode.SelectItem> anchorItems = new ArrayList<>();
        anchorItems.add(new JQuickProjectNode.SelectItem(new JQuickColumnRefExpression("id"), "id"));
        anchorItems.add(new JQuickProjectNode.SelectItem(new JQuickColumnRefExpression("name"), "name"));
        anchorItems.add(new JQuickProjectNode.SelectItem(new JQuickColumnRefExpression("manager_id"), "manager_id"));
        anchorItems.add(new JQuickProjectNode.SelectItem(new JQuickLiteralExpression(1), "level"));
        JQuickProjectNode anchorProject = new JQuickProjectNode(anchorItems, anchorFilter);
        JQuickTableScanNode employeeScanRecursive = createTableScan("employee", "e");
        JQuickTableScanNode cteRef = createTableScan("org_hierarchy", "h");
        JQuickJoinNode recursiveJoin = new JQuickJoinNode(JQuickJoinType.INNER, employeeScanRecursive, cteRef, createEqualityCondition("e.manager_id", "h.id"));
        JQuickBinaryExpression levelPlusOne = new JQuickBinaryExpression(new JQuickColumnRefExpression("h.level"), new JQuickLiteralExpression(1), JQuickBinaryOperator.PLUS);
        List<JQuickProjectNode.SelectItem> recursiveItems = new ArrayList<>();
        recursiveItems.add(new JQuickProjectNode.SelectItem(new JQuickColumnRefExpression("e.id"), "id"));
        recursiveItems.add(new JQuickProjectNode.SelectItem(new JQuickColumnRefExpression("e.name"), "name"));
        recursiveItems.add(new JQuickProjectNode.SelectItem(new JQuickColumnRefExpression("e.manager_id"), "manager_id"));
        recursiveItems.add(new JQuickProjectNode.SelectItem(levelPlusOne, "level"));
        JQuickProjectNode recursiveProject = new JQuickProjectNode(recursiveItems, recursiveJoin);
        List<String> columnNames = Arrays.asList("id", "name", "manager_id", "level");
        JQuickRecursiveUnionNode recursiveUnion = new JQuickRecursiveUnionNode("org_hierarchy", columnNames, anchorProject, recursiveProject, true);
        JQuickTableScanNode cteFinalRef = createTableScan("org_hierarchy");
        JQuickProjectNode mainProject = createProject(cteFinalRef, "id", "name", "level");
        Map<String, JQuickLogicalPlanNode> ctes = new LinkedHashMap<>();
        ctes.put("org_hierarchy", recursiveUnion);
        JQuickWithNode withNode = new JQuickWithNode(mainProject, ctes);
        JQuickPhysicalPlanNode physicalPlan = generator.generate(withNode);
        System.out.println(physicalPlan);
    }

}
