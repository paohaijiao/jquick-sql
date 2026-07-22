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
import com.github.paohaijiao.logic.domain.JQuickFilterNode;
import com.github.paohaijiao.logic.domain.JQuickJoinNode;
import com.github.paohaijiao.logic.domain.JQuickTableScanNode;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * packageName com.github.paohaijiao.optimizer
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/5/23
 */
public class JQuickJoinReorderRuleTest {
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
     * SQL: SELECT * FROM orders o JOIN customers c ON o.customer_id = c.id
     * 两个表都没有过滤条件，顺序不变
     */
    @Test
    public void testTwoTablesNoFilter() {
        JQuickTableScanNode orders = new JQuickTableScanNode("orders", "o");
        JQuickTableScanNode customers = new JQuickTableScanNode("customers", "c");
        JQuickExpression joinCondition = binary(column("o.customer_id"), column("c.id"), JQuickBinaryOperator.EQ);
        JQuickJoinNode join = new JQuickJoinNode(JQuickJoinType.INNER, orders, customers, joinCondition,null);
        JQuickLogicalPlanNode result = rule.optimize(join);
        assertTrue(result instanceof JQuickJoinNode);
        JQuickJoinNode resultJoin = (JQuickJoinNode) result;
        assertTrue(resultJoin.getLeft() instanceof JQuickTableScanNode);
        assertEquals("orders", ((JQuickTableScanNode) resultJoin.getLeft()).getTableName());
        assertTrue(resultJoin.getRight() instanceof JQuickTableScanNode);
        assertEquals("customers", ((JQuickTableScanNode) resultJoin.getRight()).getTableName());
    }
    /**
     * SQL: SELECT * FROM orders o JOIN customers c ON o.customer_id = c.id WHERE c.status = 'VIP'
     * customers 有过滤条件，应该被提前到左表
     */
    @Test
    public void testTwoTablesRightHasFilter() {
        JQuickTableScanNode orders = new JQuickTableScanNode("orders", "o");
        JQuickExpression filterPredicate = binary(column("c.status"), literal("VIP"), JQuickBinaryOperator.EQ);
        JQuickFilterNode customersFilter = new JQuickFilterNode(filterPredicate, new JQuickTableScanNode("customers", "c"));
        JQuickExpression joinCondition = binary(column("o.customer_id"), column("c.id"), JQuickBinaryOperator.EQ);
        JQuickJoinNode join = new JQuickJoinNode(JQuickJoinType.INNER, orders, customersFilter, joinCondition,null);
        JQuickLogicalPlanNode result = rule.optimize(join);
        System.out.println(result);
    }
    /**
     * SQL: SELECT * FROM orders o JOIN customers c ON o.customer_id = c.id WHERE o.amount > 1000
     * orders 有过滤条件，应该被提前
     */
    @Test
    public void testTwoTablesLeftHasFilter() {
        JQuickExpression filterPredicate = binary(column("o.amount"), literal(1000), JQuickBinaryOperator.GT);
        JQuickFilterNode ordersFilter = new JQuickFilterNode(filterPredicate, new JQuickTableScanNode("orders", "o"));
        JQuickTableScanNode customers = new JQuickTableScanNode("customers", "c");
        JQuickExpression joinCondition = binary(column("o.customer_id"), column("c.id"), JQuickBinaryOperator.EQ);
        JQuickJoinNode join = new JQuickJoinNode(JQuickJoinType.INNER, ordersFilter, customers, joinCondition,null);
        JQuickLogicalPlanNode result = rule.optimize(join);
        System.out.println(result);
    }
    /**
     * SQL: SELECT * FROM orders o JOIN customers c ON o.customer_id = c.id
     * WHERE c.status = 'VIP' AND o.amount > 1000
     * 两个表都有过滤条件，顺序保持不变（左表已经是过滤后的）
     */
    @Test
    public void testTwoTablesBothHaveFilter() {
        JQuickExpression orderFilter = binary(column("o.amount"), literal(1000), JQuickBinaryOperator.GT);
        JQuickFilterNode ordersFilter = new JQuickFilterNode(orderFilter, new JQuickTableScanNode("orders", "o"));
        JQuickExpression customerFilter = binary(column("c.status"), literal("VIP"), JQuickBinaryOperator.EQ);
        JQuickFilterNode customersFilter = new JQuickFilterNode(customerFilter, new JQuickTableScanNode("customers", "c"));
        JQuickExpression joinCondition = binary(column("o.customer_id"), column("c.id"), JQuickBinaryOperator.EQ);
        JQuickJoinNode join = new JQuickJoinNode(JQuickJoinType.INNER, ordersFilter, customersFilter, joinCondition,null);
        JQuickLogicalPlanNode result = rule.optimize(join);
        assertTrue(result instanceof JQuickJoinNode);
        JQuickJoinNode resultJoin = (JQuickJoinNode) result;
        System.out.println(result);
    }
    /**
     * SQL: SELECT * FROM orders o
     *       JOIN customers c ON o.customer_id = c.id
     *       JOIN products p ON o.product_id = p.id
     *       WHERE c.status = 'VIP'
     * customers 有过滤条件，应该被提前
     */
    @Test
    public void testThreeTablesOneHasFilter() {
        JQuickTableScanNode orders = new JQuickTableScanNode("orders", "o");
        JQuickTableScanNode products = new JQuickTableScanNode("products", "p");
        JQuickExpression customerFilter = binary(column("c.status"), literal("VIP"), JQuickBinaryOperator.EQ);
        JQuickFilterNode customersFilter = new JQuickFilterNode(customerFilter, new JQuickTableScanNode("customers", "c"));
        JQuickExpression joinCond1 = binary(column("o.customer_id"), column("c.id"), JQuickBinaryOperator.EQ);
        JQuickExpression joinCond2 = binary(column("o.product_id"), column("p.id"), JQuickBinaryOperator.EQ);
        JQuickJoinNode join1 = new JQuickJoinNode(JQuickJoinType.INNER, orders, customersFilter, joinCond1,null);
        JQuickJoinNode join2 = new JQuickJoinNode(JQuickJoinType.INNER, join1, products, joinCond2,null);
        JQuickLogicalPlanNode result = rule.optimize(join2);
        assertTrue(result instanceof JQuickJoinNode);
        assertNotNull(result);
    }
}
