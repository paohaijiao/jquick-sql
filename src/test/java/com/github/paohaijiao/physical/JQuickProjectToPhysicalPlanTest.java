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
import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.expression.domain.*;
import com.github.paohaijiao.logic.JQuickLogicalPlanNode;
import com.github.paohaijiao.logic.domain.*;
import com.github.paohaijiao.logic2physical.JQuickPhysicalPlanGenerator;
import com.github.paohaijiao.physical.node.JQuickHashAggregatePhysicalNode;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * packageName com.github.paohaijiao.physical
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/5/24
 */
public class JQuickProjectToPhysicalPlanTest {

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
     * 创建带别名的表扫描节点
     */
    private JQuickTableScanNode createTableScan(String tableName, String alias) {
        return new JQuickTableScanNode(tableName, alias);
    }

    /**
     * 创建简单列投影
     */
    private JQuickProjectNode createSimpleProject(JQuickLogicalPlanNode child, String... columns) {
        List<JQuickProjectNode.SelectItem> items = new ArrayList<>();
        for (String col : columns) {
            items.add(new JQuickProjectNode.SelectItem(new JQuickColumnRefExpression(col), col));
        }
        return new JQuickProjectNode(items, child);
    }

    /**
     * 创建带别名的投影
     */
    private JQuickProjectNode createProjectWithAlias(JQuickLogicalPlanNode child, Map<String, String> columnToAlias) {
        List<JQuickProjectNode.SelectItem> items = new ArrayList<>();
        for (Map.Entry<String, String> entry : columnToAlias.entrySet()) {
            items.add(new JQuickProjectNode.SelectItem(
                    new JQuickColumnRefExpression(entry.getKey()), entry.getValue()));
        }
        return new JQuickProjectNode(items, child);
    }

    /**
     * 创建二元运算表达式投影
     */
    private JQuickProjectNode createBinaryExpressionProject(JQuickLogicalPlanNode child, String leftCol, String rightCol, JQuickBinaryOperator operator, String alias) {
        JQuickBinaryExpression expr = new JQuickBinaryExpression(new JQuickColumnRefExpression(leftCol), new JQuickColumnRefExpression(rightCol), operator);
        List<JQuickProjectNode.SelectItem> items = new ArrayList<>();
        items.add(new JQuickProjectNode.SelectItem(expr, alias));
        return new JQuickProjectNode(items, child);
    }

    /**
     * 创建常量表达式投影
     */
    private JQuickProjectNode createConstantProject(JQuickLogicalPlanNode child, Object constant, String alias) {
        JQuickLiteralExpression constantExpr = new JQuickLiteralExpression(constant);
        List<JQuickProjectNode.SelectItem> items = new ArrayList<>();
        items.add(new JQuickProjectNode.SelectItem(constantExpr, alias));
        return new JQuickProjectNode(items, child);
    }

    /**
     * 创建函数调用投影
     */
    private JQuickProjectNode createFunctionProject(JQuickLogicalPlanNode child, String functionName, String argument, String alias) {
        JQuickFunctionCallExpression functionCall = new JQuickFunctionCallExpression(functionName, Collections.singletonList(new JQuickColumnRefExpression(argument)));
        List<JQuickProjectNode.SelectItem> items = new ArrayList<>();
        items.add(new JQuickProjectNode.SelectItem(functionCall, alias));
        return new JQuickProjectNode(items, child);
    }

    /**
     * 创建过滤节点
     */
    private JQuickFilterNode createFilter(JQuickLogicalPlanNode child, String column, JQuickBinaryOperator operator, Object value) {
        JQuickBinaryExpression predicate = new JQuickBinaryExpression(new JQuickColumnRefExpression(column), new JQuickLiteralExpression(value), operator);
        return new JQuickFilterNode(predicate, child);
    }
    /**
     * 创建分组键列表
     */
    private List<JQuickExpression> createGroupKeys(String... keys) {
        List<JQuickExpression> groupKeys = new ArrayList<>();
        for (String key : keys) {
            groupKeys.add(new JQuickColumnRefExpression(key));
        }
        return groupKeys;
    }
    /**
     * 创建聚合函数列表
     */
    private List<JQuickGroupByNode.AggregateItem> createAggregates(String... aggSpecs) {
        List<JQuickGroupByNode.AggregateItem> aggregates = new ArrayList<>();
        for (String spec : aggSpecs) {
            String[] parts = spec.split(":");
            String functionName = parts[0];
            String column = parts.length > 1 ? parts[1] : null;
            String alias = parts.length > 2 ? parts[2] : functionName + "_" + column;
            JQuickExpression argument = column != null ? new JQuickColumnRefExpression(column) : null;
            boolean isCountStar = functionName.equals("COUNT") && column == null;
            aggregates.add(new JQuickGroupByNode.AggregateItem(argument, functionName, alias, isCountStar));
        }
        return aggregates;
    }
    /**
     * 创建 HAVING 条件
     */
    private JQuickExpression createHavingCondition(String column, JQuickBinaryOperator operator, Object value) {
        return new JQuickBinaryExpression(new JQuickColumnRefExpression(column), new JQuickLiteralExpression(value), operator);
    }
    /**
     * 测试1：简单列投影
     *
     * SQL示例：SELECT id, name, age FROM users
     */
    @Test
    public void testSimpleColumnProjection() {
        JQuickTableScanNode usersScan = createTableScan("users");
        JQuickProjectNode projectNode = createSimpleProject(usersScan, "id", "name", "age");
        JQuickPhysicalPlanNode physicalPlan = generator.generate(projectNode);
        System.out.println(physicalPlan);
    }
    /**
     * 测试3：表达式投影（算术运算）
     *
     * SQL示例：SELECT id, salary * 1.1 AS new_salary FROM employees
     */
    @Test
    public void testArithmeticExpressionProjection() {
        JQuickTableScanNode employeesScan = createTableScan("employees");
        JQuickBinaryExpression multiplyExpr = new JQuickBinaryExpression(new JQuickColumnRefExpression("salary"), new JQuickLiteralExpression(1.1), JQuickBinaryOperator.MULTIPLY);
        List<JQuickProjectNode.SelectItem> items = new ArrayList<>();
        items.add(new JQuickProjectNode.SelectItem(new JQuickColumnRefExpression("id"), "id"));
        items.add(new JQuickProjectNode.SelectItem(multiplyExpr, "new_salary"));
        JQuickProjectNode projectNode = new JQuickProjectNode(items, employeesScan);
        JQuickPhysicalPlanNode physicalPlan = generator.generate(projectNode);
        assertNotNull(physicalPlan);
    }
    /**
     * 测试5：函数调用投影
     *
     * SQL示例：SELECT id, UPPER(name) AS upper_name, LENGTH(email) AS email_len FROM users
     */
    @Test
    public void testFunctionCallProjection() {
        JQuickTableScanNode usersScan = createTableScan("users");
        List<JQuickProjectNode.SelectItem> items = new ArrayList<>();
        items.add(new JQuickProjectNode.SelectItem(new JQuickColumnRefExpression("id"), "id"));
        JQuickFunctionCallExpression upperFunc = new JQuickFunctionCallExpression("UPPER", Collections.singletonList(new JQuickColumnRefExpression("name")));
        items.add(new JQuickProjectNode.SelectItem(upperFunc, "upper_name"));
        JQuickFunctionCallExpression lengthFunc = new JQuickFunctionCallExpression("LENGTH", Collections.singletonList(new JQuickColumnRefExpression("email")));
        items.add(new JQuickProjectNode.SelectItem(lengthFunc, "email_len"));
        JQuickProjectNode projectNode = new JQuickProjectNode(items, usersScan);
        JQuickPhysicalPlanNode physicalPlan = generator.generate(projectNode);
        assertNotNull(physicalPlan);
    }
    /**
     *
     * SQL示例：SELECT u.id, u.name, o.order_date
     *          FROM users u JOIN orders o ON u.id = o.user_id
     */
    @Test
    public void testJoinProjection() {
        JQuickTableScanNode usersScan = createTableScan("users", "u");
        JQuickTableScanNode ordersScan = createTableScan("orders", "o");
        JQuickBinaryExpression joinCondition = new JQuickBinaryExpression(new JQuickColumnRefExpression("u.id"), new JQuickColumnRefExpression("o.user_id"), JQuickBinaryOperator.EQ);
        JQuickJoinNode joinNode = new JQuickJoinNode(com.github.paohaijiao.enums.JQuickJoinType.INNER, usersScan, ordersScan, joinCondition);
        List<JQuickProjectNode.SelectItem> items = new ArrayList<>();
        items.add(new JQuickProjectNode.SelectItem(new JQuickColumnRefExpression("u.id"), "id"));
        items.add(new JQuickProjectNode.SelectItem(new JQuickColumnRefExpression("u.name"), "name"));
        items.add(new JQuickProjectNode.SelectItem(new JQuickColumnRefExpression("o.order_date"), "order_date"));
        JQuickProjectNode projectNode = new JQuickProjectNode(items, joinNode);
        JQuickPhysicalPlanNode physicalPlan = generator.generate(projectNode);
        assertNotNull(physicalPlan);
    }
    /**
     * 测试11：多层嵌套投影
     *
     * SQL示例：SELECT * FROM (SELECT id, name FROM users) t
     */
    @Test
    public void testNestedProjection() {
        JQuickTableScanNode usersScan = createTableScan("users");
        JQuickProjectNode innerProject = createSimpleProject(usersScan, "id", "name");
        JQuickTableScanNode innerRef = createTableScan("inner", "t");
        JQuickProjectNode outerProject = createSimpleProject(innerRef, "id", "name");
        JQuickPhysicalPlanNode physicalPlan = generator.generate(innerProject);
        assertNotNull(physicalPlan);
        assertEquals("Project", physicalPlan.getNodeType());
    }
    /**
     *
     * SQL示例：SELECT id, name FROM users ORDER BY name
     */
    @Test
    public void testProjectionWithSort() {
        JQuickTableScanNode usersScan = createTableScan("users");
        JQuickProjectNode projectNode = createSimpleProject(usersScan, "id", "name");
        List<JQuickSortNode.OrderByItem> orderByItems = new ArrayList<>();
        orderByItems.add(new JQuickSortNode.OrderByItem("name", true));
        JQuickSortNode sortNode = new JQuickSortNode(orderByItems, projectNode);
        JQuickPhysicalPlanNode physicalPlan = generator.generate(sortNode);
        assertNotNull(physicalPlan);
    }
    /***
     * SQL示例：SELECT id, name FROM users LIMIT 10
     */
    @Test
    public void testProjectionWithLimit() {
        JQuickTableScanNode usersScan = createTableScan("users");
        JQuickProjectNode projectNode = createSimpleProject(usersScan, "id", "name");
        JQuickLimitNode limitNode = new JQuickLimitNode(10, projectNode);
        JQuickPhysicalPlanNode physicalPlan = generator.generate(limitNode);
        assertNotNull(physicalPlan);
    }
    /**
     * 测试16：CASE WHEN 表达式投影
     *
     * SQL示例：SELECT id, CASE WHEN score >= 60 THEN 'PASS' ELSE 'FAIL' END AS result
     */
    @Test
    public void testCaseWhenProjection() {
        JQuickTableScanNode studentsScan = createTableScan("students");
        JQuickBinaryExpression condition = new JQuickBinaryExpression(new JQuickColumnRefExpression("score"), new JQuickLiteralExpression(60), JQuickBinaryOperator.GE);
        JQuickCaseWhenExpression caseWhenExpr = new JQuickCaseWhenExpression(Arrays.asList(condition),Arrays.asList( new JQuickLiteralExpression("PASS")),new JQuickLiteralExpression("FAIL"));
        List<JQuickProjectNode.SelectItem> items = new ArrayList<>();
        items.add(new JQuickProjectNode.SelectItem(new JQuickColumnRefExpression("id"), "id"));
        items.add(new JQuickProjectNode.SelectItem(caseWhenExpr, "result"));
        JQuickProjectNode projectNode = new JQuickProjectNode(items, studentsScan);
        JQuickPhysicalPlanNode physicalPlan = generator.generate(projectNode);
        assertNotNull(physicalPlan);
        assertEquals("Project", physicalPlan.getNodeType());
    }

    /**
     * 测试17：字符串拼接投影
     *
     * SQL示例：SELECT id, CONCAT(first_name, ' ', last_name) AS full_name
     */
    @Test
    public void testStringConcatProjection() {
        JQuickTableScanNode usersScan = createTableScan("users");
        List<JQuickExpression> concatArgs = new ArrayList<>();
        concatArgs.add(new JQuickColumnRefExpression("first_name"));
        concatArgs.add(new JQuickLiteralExpression(" "));
        concatArgs.add(new JQuickColumnRefExpression("last_name"));
        JQuickFunctionCallExpression concatFunc = new JQuickFunctionCallExpression("CONCAT", concatArgs);
        List<JQuickProjectNode.SelectItem> items = new ArrayList<>();
        items.add(new JQuickProjectNode.SelectItem(new JQuickColumnRefExpression("id"), "id"));
        items.add(new JQuickProjectNode.SelectItem(concatFunc, "full_name"));
        JQuickProjectNode projectNode = new JQuickProjectNode(items, usersScan);
        JQuickPhysicalPlanNode physicalPlan = generator.generate(projectNode);
        assertNotNull(physicalPlan);
        System.out.println("=== 字符串拼接投影测试通过 ===");
        System.out.println("表达式: CONCAT(first_name, ' ', last_name) AS full_name");
    }
    /**
    **
     *
     * SQL示例：SELECT category, SUM(amount) total FROM sales GROUP BY category HAVING SUM(amount) > 1000
     */
    @Test
    public void testGroupByWithSort() {
        JQuickTableScanNode salesScan = createTableScan("sales");
        List<JQuickExpression> groupKeys = createGroupKeys("category");
        List<JQuickGroupByNode.AggregateItem> aggregates = createAggregates("SUM:amount:total");
        JQuickExpression havingCondition = createHavingCondition("total", JQuickBinaryOperator.GT, 1000);
        JQuickGroupByNode groupByNode = new JQuickGroupByNode(groupKeys, aggregates, salesScan, havingCondition);
        JQuickPhysicalPlanNode physicalPlan = generator.generate(groupByNode);
        assertNotNull(physicalPlan);
        JQuickHashAggregatePhysicalNode aggNode = (JQuickHashAggregatePhysicalNode) physicalPlan;
        System.out.println(physicalPlan);

    }





}
