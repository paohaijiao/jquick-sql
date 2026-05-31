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
import com.github.paohaijiao.logic.domain.JQuickJoinNode;
import com.github.paohaijiao.logic.domain.JQuickProjectNode;
import com.github.paohaijiao.logic.domain.JQuickTableScanNode;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * packageName com.github.paohaijiao.optimizer
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/5/23
 */
public class JQuickProjectionPushdownRuleSqlTest {

    private final JQuickLogicalPlanOptimizer rule = new JQuickLogicalPlanOptimizer();

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
     * SQL: SELECT name FROM products
     *
     * 优化前: Project(name) -> TableScan(*)
     * 优化后: Project(name) -> TableScan(name)
     */
    @Test
    public void testSql_SelectSingleColumn() {
        // SELECT name FROM products
        JQuickProjectNode.SelectItem nameItem = new JQuickProjectNode.SelectItem(column("name"), "name");
        JQuickProjectNode project = new JQuickProjectNode(Collections.singletonList(nameItem), new JQuickTableScanNode("products"));
        JQuickLogicalPlanNode result = rule.optimize(project);
        assertTrue(result instanceof JQuickProjectNode);
        JQuickProjectNode resultProject = (JQuickProjectNode) result;
        JQuickTableScanNode scan = (JQuickTableScanNode) resultProject.getChild();
        Set<String> requiredColumns = scan.getRequiredColumns();
        assertEquals(1, requiredColumns.size());
        assertTrue(requiredColumns.contains("name"));
    }
    /**
     * SQL: SELECT name FROM (SELECT name, price, category FROM products) t
     *
     * 优化前: Project(name) -> Project(name, price, category) -> TableScan
     * 优化后: Project(name) -> TableScan(name)
     */
    @Test
    public void testSql_MergeNestedProjects() {
        // 内层: SELECT name, price, category FROM products
        JQuickProjectNode.SelectItem nameItem1 = new JQuickProjectNode.SelectItem(column("name"), "name");
        JQuickProjectNode.SelectItem priceItem = new JQuickProjectNode.SelectItem(column("price"), "price");
        JQuickProjectNode.SelectItem categoryItem = new JQuickProjectNode.SelectItem(column("category"), "category");
        JQuickProjectNode innerProject = new JQuickProjectNode(Arrays.asList(nameItem1, priceItem, categoryItem), new JQuickTableScanNode("products"));
        // 外层: SELECT name FROM (...)
        JQuickProjectNode.SelectItem nameItem2 = new JQuickProjectNode.SelectItem(column("name"), "name");
        JQuickProjectNode outerProject = new JQuickProjectNode(Collections.singletonList(nameItem2), innerProject);
        JQuickLogicalPlanNode result = rule.optimize(outerProject);
        assertTrue(result instanceof JQuickProjectNode);
        JQuickProjectNode resultProject = (JQuickProjectNode) result;
        assertTrue(resultProject.getChild() instanceof JQuickTableScanNode);
        JQuickTableScanNode scan = (JQuickTableScanNode) resultProject.getChild();
        Set<String> requiredColumns = scan.getRequiredColumns();
        assertEquals(1, requiredColumns.size());
        assertTrue(requiredColumns.contains("name"));
    }
    /**
     * SQL: SELECT name, price * 2 FROM (SELECT name, price, category FROM products) t
     *
     * 优化前: 两层 Project
     * 优化后: 合并为一个 Project，TableScan 只读需要的列
     */
    @Test
    public void testSql_MergeNestedProjectsWithExpression() {
        // 内层: SELECT name, price, category FROM products
        JQuickProjectNode.SelectItem nameItem1 = new JQuickProjectNode.SelectItem(column("name"), "name");
        JQuickProjectNode.SelectItem priceItem = new JQuickProjectNode.SelectItem(column("price"), "price");
        JQuickProjectNode.SelectItem categoryItem = new JQuickProjectNode.SelectItem(column("category"), "category");
        JQuickProjectNode innerProject = new JQuickProjectNode(Arrays.asList(nameItem1, priceItem, categoryItem), new JQuickTableScanNode("products"));

        // 外层: SELECT name, price * 2 FROM (...)
        JQuickExpression priceTimes2 = binary(column("price"), literal(2), JQuickBinaryOperator.MULTIPLY);
        JQuickProjectNode.SelectItem nameItem2 = new JQuickProjectNode.SelectItem(column("name"), "name");
        JQuickProjectNode.SelectItem exprItem = new JQuickProjectNode.SelectItem(priceTimes2, "double_price");
        JQuickProjectNode outerProject = new JQuickProjectNode(Arrays.asList(nameItem2, exprItem), innerProject);
        JQuickLogicalPlanNode result = rule.optimize(outerProject);
        assertTrue(result instanceof JQuickProjectNode);
        JQuickProjectNode resultProject = (JQuickProjectNode) result;
        // 子节点应该是 TableScan
        assertTrue(resultProject.getChild() instanceof JQuickTableScanNode);
        JQuickTableScanNode scan = (JQuickTableScanNode) resultProject.getChild();
        Set<String> requiredColumns = scan.getRequiredColumns();
        // 需要 name 和 price（表达式引用了 price）
        assertTrue(requiredColumns.contains("name"));
        assertTrue(requiredColumns.contains("price"));
        assertFalse(requiredColumns.contains("category"));
    }
    @Test
    public void testJoin_SelectColumnsFromLeftTable() {
        // SELECT o.order_id, o.amount FROM orders o JOIN customers c ON o.customer_id = c.id
        JQuickTableScanNode orders = new JQuickTableScanNode("orders", "o");
        JQuickTableScanNode customers = new JQuickTableScanNode("customers", "c");
        JQuickExpression joinCondition = binary(column("o.customer_id"), column("c.id"), JQuickBinaryOperator.EQ);
        JQuickJoinNode join = new JQuickJoinNode(JQuickJoinType.INNER, orders, customers, joinCondition);
        JQuickProjectNode.SelectItem orderIdItem = new JQuickProjectNode.SelectItem(column("o.order_id"), "order_id");
        JQuickProjectNode.SelectItem amountItem = new JQuickProjectNode.SelectItem(column("o.amount"), "amount");
        JQuickProjectNode project = new JQuickProjectNode(Arrays.asList(orderIdItem, amountItem), join);
        JQuickLogicalPlanNode result = rule.optimize(project);
        assertTrue(result instanceof JQuickProjectNode);
        JQuickProjectNode resultProject = (JQuickProjectNode) result;
        assertTrue(resultProject.getChild() instanceof JQuickJoinNode);
        JQuickJoinNode resultJoin = (JQuickJoinNode) resultProject.getChild();
        // 验证左表 TableScan 的列
        assertTrue(resultJoin.getLeft() instanceof JQuickTableScanNode);
        JQuickTableScanNode leftScan = (JQuickTableScanNode) resultJoin.getLeft();
        Set<String> leftColumns = leftScan.getRequiredColumns();
    }

}
