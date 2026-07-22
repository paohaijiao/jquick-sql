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
public class JQuickProjectionPushdownRuleLimitTest {

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
     * SQL: SELECT * FROM products ORDER BY price LIMIT 10
     *
     * 优化前: Limit 10 -> Sort(price)
     * 优化后: Limit 10 -> Sort(price) (保持不变，可进一步优化为 Top-N)
     */
    @Test
    public void testLimitOverSort() {
        JQuickSortNode sort = new JQuickSortNode(Collections.singletonList(new JQuickSortNode.OrderByItem("price", true)), new JQuickTableScanNode("products"));
        JQuickLimitNode limit = new JQuickLimitNode(10, sort);
        JQuickLogicalPlanNode result = rule.optimize(limit);
        assertTrue(result instanceof JQuickLimitNode);
        JQuickLimitNode resultLimit = (JQuickLimitNode) result;
        assertTrue(resultLimit.getChild() instanceof JQuickSortNode);
        assertEquals(10, resultLimit.getLimit());
    }
    /**
     * SQL: SELECT * FROM products WHERE price > 100 LIMIT 10
     *
     * 优化前: Limit 10 -> Filter -> TableScan
     * 优化后: Filter -> Limit 10 -> TableScan
     */
    @Test
    public void testLimitPushdownOverFilter() {
        JQuickExpression predicate = binary(column("price"), literal(100), JQuickBinaryOperator.GT);
        JQuickFilterNode filter = new JQuickFilterNode(predicate, new JQuickTableScanNode("products"));
        JQuickLimitNode limit = new JQuickLimitNode(10, filter);
        JQuickLogicalPlanNode result = rule.optimize(limit);
        assertTrue(result instanceof JQuickFilterNode);
        JQuickFilterNode resultFilter = (JQuickFilterNode) result;
        assertTrue(resultFilter.getChild() instanceof JQuickLimitNode);
        JQuickLimitNode resultLimit = (JQuickLimitNode) resultFilter.getChild();
        assertTrue(resultLimit.getChild() instanceof JQuickTableScanNode);
        assertEquals(10, resultLimit.getLimit());
    }
    /**
     * SQL: SELECT name, price FROM products ORDER BY price LIMIT 10
     *
     * 优化前: Limit 10 -> Project(name, price) -> Sort(price) -> TableScan
     * 优化后: Project(name, price) -> Limit 10 -> Sort(price) -> TableScan
     */
    @Test
    public void testLimitPushdownOverProject() {
        JQuickSortNode sort = new JQuickSortNode(Collections.singletonList(new JQuickSortNode.OrderByItem("price", true)), new JQuickTableScanNode("products"));
        JQuickProjectNode.SelectItem nameItem = new JQuickProjectNode.SelectItem(column("name"), "name");
        JQuickProjectNode.SelectItem priceItem = new JQuickProjectNode.SelectItem(column("price"), "price");
        JQuickProjectNode project = new JQuickProjectNode(Arrays.asList(nameItem, priceItem), sort);
        JQuickLimitNode limit = new JQuickLimitNode(10, project);
        JQuickLogicalPlanNode result = rule.optimize(limit);
        assertTrue(result instanceof JQuickProjectNode);
        JQuickProjectNode resultProject = (JQuickProjectNode) result;
        assertTrue(resultProject.getChild() instanceof JQuickLimitNode);
        JQuickLimitNode resultLimit = (JQuickLimitNode) resultProject.getChild();
        assertTrue(resultLimit.getChild() instanceof JQuickSortNode);
        assertEquals(10, resultLimit.getLimit());
    }

    /**
     * SQL: SELECT o.order_id, o.amount FROM orders o JOIN customers c ON o.customer_id = c.id LIMIT 10
     *
     * 优化前: Limit 10 -> Join
     * 优化后: Join( Limit 10 -> orders, customers )  （下推到左表）
     */
    @Test
    public void testLimitPushdownOverJoin() {
        JQuickTableScanNode orders = new JQuickTableScanNode("orders", "o");
        JQuickTableScanNode customers = new JQuickTableScanNode("customers", "c");
        JQuickExpression joinCondition = binary(column("o.customer_id"), column("c.id"), JQuickBinaryOperator.EQ);
        JQuickJoinNode join = new JQuickJoinNode(JQuickJoinType.INNER, orders, customers, joinCondition,null);
        JQuickLimitNode limit = new JQuickLimitNode(10, join);
        JQuickLogicalPlanNode result = rule.optimize(limit);
        assertTrue(result instanceof JQuickLimitNode);
        JQuickLimitNode resultLimit = (JQuickLimitNode) result;
        assertTrue(resultLimit.getChild() instanceof JQuickJoinNode);
        JQuickJoinNode resultJoin = (JQuickJoinNode) resultLimit.getChild();
        assertTrue(resultJoin.getLeft() instanceof JQuickLimitNode);
        JQuickLimitNode leftLimit = (JQuickLimitNode) resultJoin.getLeft();
        assertEquals(10, leftLimit.getLimit());
        assertTrue(resultJoin.getRight() instanceof JQuickTableScanNode);
    }

}
