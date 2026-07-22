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
package com.github.paohaijiao.optimizer;

import com.github.paohaijiao.enums.JQuickBinaryOperator;
import com.github.paohaijiao.enums.JQuickJoinType;
import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.expression.domain.JQuickBinaryExpression;
import com.github.paohaijiao.expression.domain.JQuickColumnRefExpression;
import com.github.paohaijiao.expression.domain.JQuickLiteralExpression;
import com.github.paohaijiao.logic.JQuickLogicalPlanNode;
import com.github.paohaijiao.logic.domain.*;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * packageName com.github.paohaijiao.optimizer
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/5/23
 */
public class JQuickProjectionMergeRuleTest {
    JQuickLogicalPlanOptimizer rule=new JQuickLogicalPlanOptimizer();
    /**
     * 创建字面量表达式
     */
    private JQuickLiteralExpression literal(Object value) {
        return new JQuickLiteralExpression(value);
    }

    /**
     * 创建列引用表达式
     */
    private JQuickColumnRefExpression column(String name) {
        return new JQuickColumnRefExpression(name);
    }

    /**
     * 创建二元表达式
     */
    private JQuickBinaryExpression binary(JQuickExpression left, JQuickExpression right, JQuickBinaryOperator op) {
        return new JQuickBinaryExpression(left, right, op);
    }

    /**
     * 在表达式中查找指定操作符的二元表达式
     */
    private JQuickExpression findBinaryWithOperator(JQuickExpression expr, JQuickBinaryOperator targetOp) {
        if (expr instanceof JQuickBinaryExpression) {
            JQuickBinaryExpression binary = (JQuickBinaryExpression) expr;
            if (binary.getOperator() == targetOp) {
                return binary;
            }
            JQuickExpression left = findBinaryWithOperator(binary.getLeft(), targetOp);
            if (left != null) return left;
            return findBinaryWithOperator(binary.getRight(), targetOp);
        }
        return null;
    }

    /**
     * SQL: SELECT name, age FROM (SELECT id, name, age, city FROM users) t
     *
     * 优化前: Project(name, age) -> Project(id, name, age, city) -> TableScan
     * 优化后: Project(name, age) -> TableScan
     */
    @Test
    public void testMergeTwoProjects() {
        // 内层 Project: SELECT id, name, age, city FROM users
        JQuickProjectNode.SelectItem idItem = new JQuickProjectNode.SelectItem(column("id"), "id");
        JQuickProjectNode.SelectItem nameItem1 = new JQuickProjectNode.SelectItem(column("name"), "name");
        JQuickProjectNode.SelectItem ageItem1 = new JQuickProjectNode.SelectItem(column("age"), "age");
        JQuickProjectNode.SelectItem cityItem = new JQuickProjectNode.SelectItem(column("city"), "city");
        JQuickProjectNode innerProject = new JQuickProjectNode(Arrays.asList(idItem, nameItem1, ageItem1, cityItem), new JQuickTableScanNode("users"));
        // 外层 Project: SELECT name, age FROM (...)
        JQuickProjectNode.SelectItem nameItem2 = new JQuickProjectNode.SelectItem(column("name"), "name");
        JQuickProjectNode.SelectItem ageItem2 = new JQuickProjectNode.SelectItem(column("age"), "age");
        JQuickProjectNode outerProject = new JQuickProjectNode(Arrays.asList(nameItem2, ageItem2), innerProject);
        JQuickLogicalPlanNode result = rule.optimize(outerProject);
        assertTrue(result instanceof JQuickProjectNode);
        JQuickProjectNode resultProject = (JQuickProjectNode) result;
        assertTrue(resultProject.getChild() instanceof JQuickTableScanNode);
        assertEquals(2, resultProject.getSelectItems().size());
        assertEquals("name", resultProject.getSelectItems().get(0).getAlias());
        assertEquals("age", resultProject.getSelectItems().get(1).getAlias());
    }

    /**
     * SQL: SELECT name FROM (SELECT name, age FROM (SELECT id, name, age, city FROM users) t1) t2
     *
     * 优化前: 三层 Project 嵌套
     * 优化后: Project(name) -> TableScan
     */
    @Test
    public void testMergeThreeProjects() {
        JQuickProjectNode.SelectItem idItem = new JQuickProjectNode.SelectItem(column("id"), "id");
        JQuickProjectNode.SelectItem nameItem1 = new JQuickProjectNode.SelectItem(column("name"), "name");
        JQuickProjectNode.SelectItem ageItem1 = new JQuickProjectNode.SelectItem(column("age"), "age");
        JQuickProjectNode.SelectItem cityItem = new JQuickProjectNode.SelectItem(column("city"), "city");
        JQuickProjectNode innermostProject = new JQuickProjectNode(Arrays.asList(idItem, nameItem1, ageItem1, cityItem), new JQuickTableScanNode("users"));
        JQuickProjectNode.SelectItem nameItem2 = new JQuickProjectNode.SelectItem(column("name"), "name");
        JQuickProjectNode.SelectItem ageItem2 = new JQuickProjectNode.SelectItem(column("age"), "age");
        JQuickProjectNode middleProject = new JQuickProjectNode(Arrays.asList(nameItem2, ageItem2), innermostProject);
        JQuickProjectNode.SelectItem nameItem3 = new JQuickProjectNode.SelectItem(column("name"), "name");
        JQuickProjectNode outerProject = new JQuickProjectNode(Collections.singletonList(nameItem3), middleProject);
        JQuickLogicalPlanNode result = rule.optimize(outerProject);
        assertTrue(result instanceof JQuickProjectNode);
        JQuickProjectNode resultProject = (JQuickProjectNode) result;
        assertTrue(resultProject.getChild() instanceof JQuickTableScanNode);
        assertEquals(1, resultProject.getSelectItems().size());
        assertEquals("name", resultProject.getSelectItems().get(0).getAlias());
    }
    /**
     * SQL: SELECT price * 2 FROM (SELECT price FROM products) t
     *
     * 优化前: Project(price * 2) -> Project(price) -> TableScan
     * 优化后: Project(price * 2) -> TableScan
     */
    @Test
    public void testMergeProjectsWithExpression() {
        JQuickProjectNode.SelectItem priceItem = new JQuickProjectNode.SelectItem(column("price"), "price");
        JQuickProjectNode innerProject = new JQuickProjectNode(Collections.singletonList(priceItem), new JQuickTableScanNode("products"));
        JQuickExpression priceTimes2 = binary(column("price"), literal(2), JQuickBinaryOperator.MULTIPLY);
        JQuickProjectNode.SelectItem exprItem = new JQuickProjectNode.SelectItem(priceTimes2, "double_price");
        JQuickProjectNode outerProject = new JQuickProjectNode(Collections.singletonList(exprItem), innerProject);
        JQuickLogicalPlanNode result = rule.optimize(outerProject);
        assertTrue(result instanceof JQuickProjectNode);
        JQuickProjectNode resultProject = (JQuickProjectNode) result;
        assertTrue(resultProject.getChild() instanceof JQuickTableScanNode);
        JQuickExpression resultExpr = resultProject.getSelectItems().get(0).getExpression();
        assertTrue(resultExpr instanceof JQuickBinaryExpression);
        JQuickBinaryExpression binaryExpr = (JQuickBinaryExpression) resultExpr;
        assertTrue(binaryExpr.getLeft() instanceof JQuickColumnRefExpression);
        assertEquals("price", ((JQuickColumnRefExpression) binaryExpr.getLeft()).getColumnName());
    }
    /**
     * SQL: SELECT (price + tax) * 2 FROM (SELECT price, tax FROM products) t
     *
     * 验证：复杂表达式正确替换
     */
    @Test
    public void testMergeProjectsWithComplexExpression() {
        JQuickProjectNode.SelectItem priceItem = new JQuickProjectNode.SelectItem(column("price"), "price");
        JQuickProjectNode.SelectItem taxItem = new JQuickProjectNode.SelectItem(column("tax"), "tax");
        JQuickProjectNode innerProject = new JQuickProjectNode(Arrays.asList(priceItem, taxItem), new JQuickTableScanNode("products"));
        JQuickExpression pricePlusTax = binary(column("price"), column("tax"), JQuickBinaryOperator.PLUS);
        JQuickExpression times2 = binary(pricePlusTax, literal(2), JQuickBinaryOperator.MULTIPLY);
        JQuickProjectNode.SelectItem exprItem = new JQuickProjectNode.SelectItem(times2, "total");
        JQuickProjectNode outerProject = new JQuickProjectNode(Collections.singletonList(exprItem), innerProject);
        JQuickLogicalPlanNode result = rule.optimize(outerProject);
        assertTrue(result instanceof JQuickProjectNode);
        JQuickProjectNode resultProject = (JQuickProjectNode) result;
        assertTrue(resultProject.getChild() instanceof JQuickTableScanNode);
        JQuickExpression resultExpr = resultProject.getSelectItems().get(0).getExpression();
        assertTrue(resultExpr instanceof JQuickBinaryExpression);
        JQuickBinaryExpression times2Expr = (JQuickBinaryExpression) resultExpr;
        JQuickBinaryExpression plusExpr = (JQuickBinaryExpression) times2Expr.getLeft();
        assertTrue(plusExpr.getLeft() instanceof JQuickColumnRefExpression);
        assertEquals("price", ((JQuickColumnRefExpression) plusExpr.getLeft()).getColumnName());
        assertTrue(plusExpr.getRight() instanceof JQuickColumnRefExpression);
        assertEquals("tax", ((JQuickColumnRefExpression) plusExpr.getRight()).getColumnName());
    }

    /**
     * SQL: SELECT name FROM (SELECT name FROM users WHERE age > 18) t
     *
     * 验证：两个 Project 合并为一个，Filter 被下推到 TableScan
     * 优化后: Project(name) -> TableScan(users, filter=age > 18)
     */
    @Test
    public void testMergeProjectsWithFilter() {
        JQuickExpression predicate = binary(column("age"), literal(18), JQuickBinaryOperator.GT);
        JQuickFilterNode filter = new JQuickFilterNode(predicate, new JQuickTableScanNode("users"));
        JQuickProjectNode.SelectItem nameItem1 = new JQuickProjectNode.SelectItem(column("name"), "name");
        JQuickProjectNode innerProject = new JQuickProjectNode(Collections.singletonList(nameItem1), filter);
        JQuickProjectNode.SelectItem nameItem2 = new JQuickProjectNode.SelectItem(column("name"), "name");
        JQuickProjectNode outerProject = new JQuickProjectNode(Collections.singletonList(nameItem2), innerProject);
        JQuickLogicalPlanNode result = rule.optimize(outerProject);
        assertTrue(result instanceof JQuickProjectNode);
        JQuickProjectNode resultProject = (JQuickProjectNode) result;
        assertTrue(resultProject.getChild() instanceof JQuickTableScanNode);
        JQuickTableScanNode resultScan = (JQuickTableScanNode) resultProject.getChild();
        assertTrue(resultScan.getFilterPredicate() != null);
        assertEquals(1, resultProject.getSelectItems().size());
        assertEquals("name", resultProject.getSelectItems().get(0).getAlias());
    }
    /**
     * SQL: SELECT name FROM (SELECT name FROM users ORDER BY id) t
     *
     * 验证：Project 和 Sort 之间的合并（不能跨 Sort 合并）
     */
    @Test
   public  void testMergeProjectsWithSort() {
        JQuickSortNode sort = new JQuickSortNode(Collections.singletonList(new JQuickSortNode.OrderByItem("id", true)), new JQuickTableScanNode("users"));
        JQuickProjectNode.SelectItem nameItem1 = new JQuickProjectNode.SelectItem(column("name"), "name");
        JQuickProjectNode innerProject = new JQuickProjectNode(Collections.singletonList(nameItem1), sort);
        JQuickProjectNode.SelectItem nameItem2 = new JQuickProjectNode.SelectItem(column("name"), "name");
        JQuickProjectNode outerProject = new JQuickProjectNode(Collections.singletonList(nameItem2), innerProject);
        JQuickLogicalPlanNode result = rule.optimize(outerProject);
        assertTrue(result instanceof JQuickProjectNode);
        JQuickProjectNode resultProject = (JQuickProjectNode) result;
        assertTrue(resultProject.getChild() instanceof JQuickSortNode);
        JQuickSortNode resultSort = (JQuickSortNode) resultProject.getChild();
        assertTrue(resultSort.getChild() instanceof JQuickTableScanNode);
    }
    /**
     * SQL: SELECT name FROM (SELECT name FROM users LIMIT 10) t
     *
     * 验证：Project 和 Limit 之间的合并（不能跨 Limit 合并）
     */
    @Test
    public void testMergeProjectsWithLimit() {
        JQuickLimitNode limit = new JQuickLimitNode(10, new JQuickTableScanNode("users"));
        JQuickProjectNode.SelectItem nameItem1 = new JQuickProjectNode.SelectItem(column("name"), "name");
        JQuickProjectNode innerProject = new JQuickProjectNode(Collections.singletonList(nameItem1), limit);
        JQuickProjectNode.SelectItem nameItem2 = new JQuickProjectNode.SelectItem(column("name"), "name");
        JQuickProjectNode outerProject = new JQuickProjectNode(Collections.singletonList(nameItem2), innerProject);
        JQuickLogicalPlanNode result = rule.optimize(outerProject);
        assertTrue(result instanceof JQuickProjectNode);
        JQuickProjectNode resultProject = (JQuickProjectNode) result;
        assertTrue(resultProject.getChild() instanceof JQuickLimitNode);
        JQuickLimitNode resultLimit = (JQuickLimitNode) resultProject.getChild();
        assertTrue(resultLimit.getChild() instanceof JQuickTableScanNode);
    }
    /**
     * SQL: SELECT t1.name FROM (SELECT o.name, o.price FROM orders o JOIN customers c ON o.customer_id = c.id) t
     *
     * 验证：Project 不能跨 JOIN 合并
     */
    @Test
    public void testMergeProjectsWithJoin() {
        JQuickTableScanNode orders = new JQuickTableScanNode("orders", "o");
        JQuickTableScanNode customers = new JQuickTableScanNode("customers", "c");
        JQuickExpression joinCondition = binary(column("o.customer_id"), column("c.id"), JQuickBinaryOperator.EQ);
        JQuickJoinNode join = new JQuickJoinNode(JQuickJoinType.INNER, orders, customers, joinCondition,null);
        JQuickProjectNode.SelectItem nameItem1 = new JQuickProjectNode.SelectItem(column("o.name"), "name");
        JQuickProjectNode.SelectItem priceItem = new JQuickProjectNode.SelectItem(column("o.price"), "price");
        JQuickProjectNode innerProject = new JQuickProjectNode(Arrays.asList(nameItem1, priceItem), join);
        JQuickProjectNode.SelectItem nameItem2 = new JQuickProjectNode.SelectItem(column("name"), "name");
        JQuickProjectNode outerProject = new JQuickProjectNode(Collections.singletonList(nameItem2), innerProject);
        JQuickLogicalPlanNode result = rule.optimize(outerProject);
        assertTrue(result instanceof JQuickProjectNode);
        JQuickProjectNode resultProject = (JQuickProjectNode) result;
        assertTrue(resultProject.getChild() instanceof JQuickJoinNode);
    }

}
