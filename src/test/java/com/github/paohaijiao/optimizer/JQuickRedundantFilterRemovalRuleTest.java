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
import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.expression.domain.JQuickBinaryExpression;
import com.github.paohaijiao.expression.domain.JQuickColumnRefExpression;
import com.github.paohaijiao.expression.domain.JQuickLiteralExpression;
import com.github.paohaijiao.logic.JQuickLogicalPlanNode;
import com.github.paohaijiao.logic.domain.JQuickEmptyNode;
import com.github.paohaijiao.logic.domain.JQuickFilterNode;
import com.github.paohaijiao.logic.domain.JQuickTableScanNode;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * packageName com.github.paohaijiao.optimizer
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/5/23
 */
public class JQuickRedundantFilterRemovalRuleTest {


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
     * SQL: SELECT * FROM products WHERE 1 = 1
     *
     * 优化前: Filter(1=1) -> TableScan
     * 优化后: TableScan
     */
    @Test
    public void testRemoveAlwaysTrueFilter() {
        JQuickExpression predicate = binary(literal(1), literal(1), JQuickBinaryOperator.EQ);
        JQuickFilterNode filter = new JQuickFilterNode(predicate, new JQuickTableScanNode("products"));
        JQuickLogicalPlanNode result = rule.optimize(filter);
        assertTrue(result instanceof JQuickTableScanNode);
        JQuickTableScanNode scan = (JQuickTableScanNode) result;
        assertEquals("products", scan.getTableName());
    }

    /**
     * SQL: SELECT * FROM products WHERE 1 = 0
     *
     * 优化前: Filter(1=0) -> TableScan
     * 优化后: EmptyNode
     */
    @Test
    public void testReplaceAlwaysFalseWithEmpty() {
        JQuickExpression predicate = binary(literal(1), literal(0), JQuickBinaryOperator.EQ);
        JQuickFilterNode filter = new JQuickFilterNode(predicate, new JQuickTableScanNode("products"));
        JQuickLogicalPlanNode result = rule.optimize(filter);
    }

    /**
     * SQL: SELECT * FROM products WHERE TRUE
     *
     * 优化前: Filter(TRUE) -> TableScan
     * 优化后: TableScan
     */
    @Test
   public  void testRemoveAlwaysTrueLiteral() {
        JQuickExpression predicate = new JQuickLiteralExpression(true);
        JQuickFilterNode filter = new JQuickFilterNode(predicate, new JQuickTableScanNode("products"));
        JQuickLogicalPlanNode result = rule.optimize(filter);
        assertTrue(result instanceof JQuickTableScanNode);
    }

    /**
     * SQL: SELECT * FROM products WHERE FALSE
     *
     * 优化前: Filter(FALSE) -> TableScan
     * 优化后: EmptyNode
     */
    @Test
   public  void testReplaceAlwaysFalseLiteral() {
        JQuickExpression predicate = new JQuickLiteralExpression(false);
        JQuickFilterNode filter = new JQuickFilterNode(predicate, new JQuickTableScanNode("products"));
        JQuickLogicalPlanNode result = rule.optimize(filter);
        System.out.println(result);
    }
    /**
     * SQL: SELECT * FROM products WHERE 1 = 1 WHERE price > 100
     *
     * 优化前: Filter(1=1) -> Filter(price > 100) -> TableScan
     * 优化后: Filter(price > 100) -> TableScan
     */
    @Test
    public void testRecursiveRemoveNestedAlwaysTrue() {
        JQuickExpression innerPredicate = binary(column("price"), literal(100), JQuickBinaryOperator.GT);
        JQuickFilterNode innerFilter = new JQuickFilterNode(innerPredicate, new JQuickTableScanNode("products"));
        JQuickExpression outerPredicate = binary(literal(1), literal(1), JQuickBinaryOperator.EQ);
        JQuickFilterNode outerFilter = new JQuickFilterNode(outerPredicate, innerFilter);
        JQuickLogicalPlanNode result = rule.optimize(outerFilter);
        System.out.println(result);
    }
}
