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
import com.github.paohaijiao.logic.domain.JQuickFilterNode;
import com.github.paohaijiao.logic.domain.JQuickProjectNode;
import com.github.paohaijiao.logic.domain.JQuickTableScanNode;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;


/**
 * 常量折叠优化规则测试用例 - 包含完整 SELECT 语句
 * <p>
 * 测试场景：
 * 1. SELECT 子句中的常量表达式折叠
 * 2. WHERE 子句中的常量表达式折叠
 * 3. 嵌套表达式折叠
 * 4. 混合常量和列引用的部分折叠
 */
public class JQuickFoldConstantsTest {

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
     * 测试：SELECT 1 + 1 AS result FROM users
     * 预期：SELECT 2 AS result FROM users
     */
    @Test
    public void testSelect_AdditionConstantFolding() {
        // 原始 SQL: SELECT 1 + 1 AS result FROM users
        JQuickExpression expr = binary(literal(1), literal(1), JQuickBinaryOperator.PLUS);
        JQuickProjectNode.SelectItem item = new JQuickProjectNode.SelectItem(expr, "result");
        JQuickProjectNode project = new JQuickProjectNode(Collections.singletonList(item), new JQuickTableScanNode("users"));
        JQuickLogicalPlanNode result = rule.optimize(project);
        assertTrue(result instanceof JQuickProjectNode);
        JQuickProjectNode resultProject = (JQuickProjectNode) result;
        JQuickProjectNode.SelectItem resultItem = resultProject.getSelectItems().get(0);
        assertTrue(resultItem.getExpression() instanceof JQuickLiteralExpression);
        assertEquals(2.0d, ((JQuickLiteralExpression) resultItem.getExpression()).getValue());
        assertEquals("result", resultItem.getAlias());
    }

    /**
     * 测试：SELECT 10 - 3 AS diff, 5 * 4 AS product, 20 / 2 AS quotient FROM orders
     * 预期：SELECT 7 AS diff, 20 AS product, 10 AS quotient FROM orders
     */
    @Test
    public void testSelect_MultipleArithmeticConstantFolding() {
        // 原始 SQL: SELECT 10 - 3 AS diff, 5 * 4 AS product, 20 / 2 AS quotient FROM orders
        List<JQuickProjectNode.SelectItem> items = Arrays.asList(
                new JQuickProjectNode.SelectItem(binary(literal(10), literal(3), JQuickBinaryOperator.MINUS), "diff"),
                new JQuickProjectNode.SelectItem(binary(literal(5), literal(4), JQuickBinaryOperator.MULTIPLY), "product"),
                new JQuickProjectNode.SelectItem(binary(literal(20), literal(2), JQuickBinaryOperator.DIVIDE), "quotient")
        );
        JQuickProjectNode project = new JQuickProjectNode(items, new JQuickTableScanNode("orders"));
        JQuickLogicalPlanNode result = rule.optimize(project);
        assertTrue(result instanceof JQuickProjectNode);
        JQuickProjectNode resultProject = (JQuickProjectNode) result;
        List<JQuickProjectNode.SelectItem> resultItems = resultProject.getSelectItems();
        // 10 - 3 = 7
        assertTrue(resultItems.get(0).getExpression() instanceof JQuickLiteralExpression);
        assertEquals(7.0d, ((JQuickLiteralExpression) resultItems.get(0).getExpression()).getValue());
        assertEquals("diff", resultItems.get(0).getAlias());
        // 5 * 4 = 20
        assertTrue(resultItems.get(1).getExpression() instanceof JQuickLiteralExpression);
        assertEquals(20.0d, ((JQuickLiteralExpression) resultItems.get(1).getExpression()).getValue());
        assertEquals("product", resultItems.get(1).getAlias());
        // 20 / 2 = 10
        assertTrue(resultItems.get(2).getExpression() instanceof JQuickLiteralExpression);
        assertEquals(10.0d, ((JQuickLiteralExpression) resultItems.get(2).getExpression()).getValue());
        assertEquals("quotient", resultItems.get(2).getAlias());
    }

    /**
     * 测试：SELECT price + 0 AS price, quantity * 1 AS quantity, total / 1 AS total FROM products
     * 预期：SELECT price AS price, quantity AS quantity, total AS total FROM products
     */
    @Test
    public void testSelect_IdentityOptimizations() {
        // 原始 SQL: SELECT price + 0 AS price, quantity * 1 AS quantity, total / 1 AS total FROM products
        List<JQuickProjectNode.SelectItem> items = Arrays.asList(
                new JQuickProjectNode.SelectItem(binary(column("price"), literal(0), JQuickBinaryOperator.PLUS), "price"),
                new JQuickProjectNode.SelectItem(binary(column("quantity"), literal(1), JQuickBinaryOperator.MULTIPLY), "quantity"),
                new JQuickProjectNode.SelectItem(binary(column("total"), literal(1), JQuickBinaryOperator.DIVIDE), "total")
        );
        JQuickProjectNode project = new JQuickProjectNode(items, new JQuickTableScanNode("products"));
        JQuickLogicalPlanNode result = rule.optimize(project);
        assertTrue(result instanceof JQuickProjectNode);
        JQuickProjectNode resultProject = (JQuickProjectNode) result;
        List<JQuickProjectNode.SelectItem> resultItems = resultProject.getSelectItems();
        // price + 0 → price
        assertTrue(resultItems.get(0).getExpression() instanceof JQuickColumnRefExpression);
        assertEquals("price", ((JQuickColumnRefExpression) resultItems.get(0).getExpression()).getColumnName());
        // quantity * 1 → quantity
        assertTrue(resultItems.get(1).getExpression() instanceof JQuickColumnRefExpression);
        assertEquals("quantity", ((JQuickColumnRefExpression) resultItems.get(1).getExpression()).getColumnName());
        // total / 1 → total
        assertTrue(resultItems.get(2).getExpression() instanceof JQuickColumnRefExpression);
        assertEquals("total", ((JQuickColumnRefExpression) resultItems.get(2).getExpression()).getColumnName());
    }

    /**
     * 测试：SELECT price * 0 AS zero, quantity * 0 AS zero_qty FROM products
     * 预期：SELECT 0 AS zero, 0 AS zero_qty FROM products
     */
    @Test
    public void testSelect_MultiplyByZero() {
        // 原始 SQL: SELECT price * 0 AS zero, quantity * 0 AS zero_qty FROM products
        List<JQuickProjectNode.SelectItem> items = Arrays.asList(
                new JQuickProjectNode.SelectItem(binary(column("price"), literal(0), JQuickBinaryOperator.MULTIPLY), "zero"),
                new JQuickProjectNode.SelectItem(binary(column("quantity"), literal(0), JQuickBinaryOperator.MULTIPLY), "zero_qty")
        );
        JQuickProjectNode project = new JQuickProjectNode(items, new JQuickTableScanNode("products"));
        JQuickLogicalPlanNode result = rule.optimize(project);
        assertTrue(result instanceof JQuickProjectNode);
        JQuickProjectNode resultProject = (JQuickProjectNode) result;
        List<JQuickProjectNode.SelectItem> resultItems = resultProject.getSelectItems();
        // price * 0 → 0
        assertTrue(resultItems.get(0).getExpression() instanceof JQuickLiteralExpression);
        assertEquals(0, ((JQuickLiteralExpression) resultItems.get(0).getExpression()).getValue());
        // quantity * 0 → 0
        assertTrue(resultItems.get(1).getExpression() instanceof JQuickLiteralExpression);
        assertEquals(0, ((JQuickLiteralExpression) resultItems.get(1).getExpression()).getValue());
    }

    /**
     * 测试：SELECT (1 + 2) * 3 AS nested, (10 - 5) / (1 + 1) AS complex FROM users
     * 预期：SELECT 9 AS nested, 2.5 AS complex FROM users
     */
    @Test
    public void testSelect_NestedExpressions() {
        // 原始 SQL: SELECT (1 + 2) * 3 AS nested, (10 - 5) / (1 + 1) AS complex FROM users
        // (1 + 2) * 3
        JQuickExpression nestedInner = binary(literal(1), literal(2), JQuickBinaryOperator.PLUS);
        JQuickExpression nestedExpr = binary(nestedInner, literal(3), JQuickBinaryOperator.MULTIPLY);
        // (10 - 5) / (1 + 1)
        JQuickExpression complexLeft = binary(literal(10), literal(5), JQuickBinaryOperator.MINUS);
        JQuickExpression complexRight = binary(literal(1), literal(1), JQuickBinaryOperator.PLUS);
        JQuickExpression complexExpr = binary(complexLeft, complexRight, JQuickBinaryOperator.DIVIDE);
        List<JQuickProjectNode.SelectItem> items = Arrays.asList(new JQuickProjectNode.SelectItem(nestedExpr, "nested"), new JQuickProjectNode.SelectItem(complexExpr, "complex"));
        JQuickProjectNode project = new JQuickProjectNode(items, new JQuickTableScanNode("users"));
        JQuickLogicalPlanNode result = rule.optimize(project);
        assertTrue(result instanceof JQuickProjectNode);
        JQuickProjectNode resultProject = (JQuickProjectNode) result;
        List<JQuickProjectNode.SelectItem> resultItems = resultProject.getSelectItems();
        // (1 + 2) * 3 = 9
        assertTrue(resultItems.get(0).getExpression() instanceof JQuickLiteralExpression);
        assertEquals(9.0d, ((JQuickLiteralExpression) resultItems.get(0).getExpression()).getValue());
        // (10 - 5) / (1 + 1) = 5 / 2 = 2.5
        assertTrue(resultItems.get(1).getExpression() instanceof JQuickLiteralExpression);
        assertEquals(2.5d, ((JQuickLiteralExpression) resultItems.get(1).getExpression()).getValue());
    }

    /**
     * 测试：SELECT price + (5 + 3) AS calc FROM products
     * 预期：SELECT price + 8 AS calc FROM products（部分折叠）
     */
    @Test
    public void testSelect_PartialConstantFolding() {
        // 原始 SQL: SELECT price + (5 + 3) AS calc FROM products
        JQuickExpression inner = binary(literal(5), literal(3), JQuickBinaryOperator.PLUS);
        JQuickExpression outer = binary(column("price"), inner, JQuickBinaryOperator.PLUS);
        JQuickProjectNode.SelectItem item = new JQuickProjectNode.SelectItem(outer, "calc");
        JQuickProjectNode project = new JQuickProjectNode(Collections.singletonList(item), new JQuickTableScanNode("products"));
        JQuickLogicalPlanNode result = rule.optimize(project);
        assertTrue(result instanceof JQuickProjectNode);
        JQuickProjectNode resultProject = (JQuickProjectNode) result;
        JQuickExpression resultExpr = resultProject.getSelectItems().get(0).getExpression();
        // 部分折叠：price + (5+3) → price + 8
        assertTrue(resultExpr instanceof JQuickBinaryExpression);
        JQuickBinaryExpression binaryExpr = (JQuickBinaryExpression) resultExpr;
        assertTrue(binaryExpr.getLeft() instanceof JQuickColumnRefExpression);
        assertEquals("price", ((JQuickColumnRefExpression) binaryExpr.getLeft()).getColumnName());
        assertTrue(binaryExpr.getRight() instanceof JQuickLiteralExpression);
        assertEquals(8d, ((JQuickLiteralExpression) binaryExpr.getRight()).getValue());
        assertEquals(JQuickBinaryOperator.PLUS, binaryExpr.getOperator());
    }

    /**
     * 测试：SELECT * FROM orders WHERE 1 + 1 = 2
     * 预期：SELECT * FROM orders WHERE true（常量折叠后变为 true）
     */
    @Test
    public void testWhere_ConstantComparison() {
        // 原始 SQL: SELECT * FROM orders WHERE 1 + 1 = 2
        JQuickExpression left = binary(literal(1), literal(1), JQuickBinaryOperator.PLUS);
        JQuickExpression condition = binary(left, literal(2), JQuickBinaryOperator.EQ);
        JQuickFilterNode filter = new JQuickFilterNode(condition, new JQuickTableScanNode("orders"));
        JQuickLogicalPlanNode result = rule.optimize(filter);
        System.out.println(result);
    }

    /**
     * 测试：SELECT * FROM products WHERE price + 0 > 100
     * 预期：SELECT * FROM products WHERE price > 100
     */
    @Test
    public void testWhere_AddZeroOptimization() {
        // 原始 SQL: SELECT * FROM products WHERE price + 0 > 100
        JQuickExpression left = binary(column("price"), literal(0), JQuickBinaryOperator.PLUS);
        JQuickExpression condition = binary(left, literal(100), JQuickBinaryOperator.GT);
        JQuickFilterNode filter = new JQuickFilterNode(condition, new JQuickTableScanNode("products"));
        JQuickLogicalPlanNode result = rule.optimize(filter);
        System.out.println(result);
    }

    /**
     * 测试：SELECT * FROM users WHERE age * 0 = 0
     * 预期：SELECT * FROM users WHERE 0 = 0 → true（始终为真）
     */
    @Test
    public void testWhere_MultiplyByZeroComparison() {
        // 原始 SQL: SELECT * FROM users WHERE age * 0 = 0
        JQuickExpression left = binary(column("age"), literal(0), JQuickBinaryOperator.MULTIPLY);
        JQuickExpression condition = binary(left, literal(0), JQuickBinaryOperator.EQ);
        JQuickFilterNode filter = new JQuickFilterNode(condition, new JQuickTableScanNode("users"));
        JQuickLogicalPlanNode result = rule.optimize(filter);
        System.out.println(result);
    }

    /**
     * 测试：SELECT * FROM users WHERE 1 = 0
     * 预期：返回 EmptyNode（由 RedundantFilterRemovalRule 处理，但常量折叠先将其变为 false）
     */
    @Test
    public void testWhere_AlwaysFalseCondition() {
        // 原始 SQL: SELECT * FROM users WHERE 1 = 0
        JQuickExpression condition = binary(literal(1), literal(0), JQuickBinaryOperator.EQ);
        JQuickFilterNode filter = new JQuickFilterNode(condition, new JQuickTableScanNode("users"));
        JQuickLogicalPlanNode result = rule.optimize(filter);
        System.out.println(result);
    }


    /**
     * 测试：完整的 SELECT 语句，包含 SELECT、WHERE 子句
     * SQL:
     * SELECT
     * (1 + 2) * 3 AS calc,
     * price + 0 AS final_price,
     * quantity * 1 AS final_qty
     * FROM products
     * WHERE price > (10 - 5) AND status = 'ACTIVE'
     */
    @Test
    public void testCompleteSelectStatement() {
        // (1 + 2) * 3
        JQuickExpression selectInner = binary(literal(1), literal(2), JQuickBinaryOperator.PLUS);
        JQuickExpression selectCalc = binary(selectInner, literal(3), JQuickBinaryOperator.MULTIPLY);
        // price + 0
        JQuickExpression selectPrice = binary(column("price"), literal(0), JQuickBinaryOperator.PLUS);
        // quantity * 1
        JQuickExpression selectQty = binary(column("quantity"), literal(1), JQuickBinaryOperator.MULTIPLY);
        List<JQuickProjectNode.SelectItem> selectItems = Arrays.asList(
                new JQuickProjectNode.SelectItem(selectCalc, "calc"),
                new JQuickProjectNode.SelectItem(selectPrice, "final_price"),
                new JQuickProjectNode.SelectItem(selectQty, "final_qty")
        );
        JQuickTableScanNode scan = new JQuickTableScanNode("products");
        // price > (10 - 5)
        JQuickExpression whereRight = binary(literal(10), literal(5), JQuickBinaryOperator.MINUS);
        JQuickExpression wherePrice = binary(column("age"), whereRight, JQuickBinaryOperator.GT);
        // status = 'ACTIVE'
        JQuickExpression whereStatus = binary(column("status"), new JQuickLiteralExpression("ACTIVE"), JQuickBinaryOperator.EQ);
        // WHERE 条件组合 (AND)
        JQuickExpression whereCondition = binary(wherePrice, whereStatus, JQuickBinaryOperator.AND);
        JQuickFilterNode filter = new JQuickFilterNode(whereCondition, scan);
        JQuickProjectNode project = new JQuickProjectNode(selectItems, filter);
        // 应用常量折叠优化
        JQuickLogicalPlanNode result = rule.optimize(project);
        assertTrue(result instanceof JQuickProjectNode);
        JQuickProjectNode resultProject = (JQuickProjectNode) result;
        // 验证 SELECT 子句优化结果
        List<JQuickProjectNode.SelectItem> resultSelectItems = resultProject.getSelectItems();
        // (1+2)*3 → 9
        assertTrue(resultSelectItems.get(0).getExpression() instanceof JQuickLiteralExpression);
        assertEquals(9.0d, ((JQuickLiteralExpression) resultSelectItems.get(0).getExpression()).getValue());
        // price + 0 → price
        assertTrue(resultSelectItems.get(1).getExpression() instanceof JQuickColumnRefExpression);
        assertEquals("price", ((JQuickColumnRefExpression) resultSelectItems.get(1).getExpression()).getColumnName());
        // quantity * 1 → quantity
        assertTrue(resultSelectItems.get(2).getExpression() instanceof JQuickColumnRefExpression);
        assertEquals("quantity", ((JQuickColumnRefExpression) resultSelectItems.get(2).getExpression()).getColumnName());
        // 验证 WHERE 子句优化结果
        assertTrue(resultProject.getChild() instanceof JQuickFilterNode);
        JQuickFilterNode resultFilter = (JQuickFilterNode) resultProject.getChild();
        // price > (10-5) → price > 5
        JQuickExpression whereResult = resultFilter.getPredicate();
        assertTrue(whereResult instanceof JQuickBinaryExpression);
        // 由于 AND 连接，需要找到 GT 条件
        JQuickExpression gtCondition = findBinaryWithOperator(whereResult, JQuickBinaryOperator.GT);
        assertNotNull(gtCondition);
        JQuickBinaryExpression gtBinary = (JQuickBinaryExpression) gtCondition;
        assertTrue(gtBinary.getRight() instanceof JQuickLiteralExpression);
        assertEquals(5.0d, ((JQuickLiteralExpression) gtBinary.getRight()).getValue());
    }

    /**
     * 测试：包含 DISTINCT 的 SELECT 语句
     * SQL: SELECT DISTINCT 1 + 1 AS const FROM users
     * 预期：SELECT DISTINCT 2 AS const FROM users
     */
    @Test
    public void testSelectDistinctWithConstantFolding() {
        JQuickExpression expr = binary(literal(1), literal(1), JQuickBinaryOperator.PLUS);
        JQuickProjectNode.SelectItem item = new JQuickProjectNode.SelectItem(expr, "const");
        JQuickProjectNode project = new JQuickProjectNode(
                Collections.singletonList(item),
                new JQuickTableScanNode("users"),
                true  // distinct = true
        );
        JQuickLogicalPlanNode result = rule.optimize(project);
        assertTrue(result instanceof JQuickProjectNode);
        JQuickProjectNode resultProject = (JQuickProjectNode) result;
        assertTrue(resultProject.isDistinct());
        assertTrue(resultProject.getSelectItems().get(0).getExpression() instanceof JQuickLiteralExpression);
        assertEquals(2.0d, ((JQuickLiteralExpression) resultProject.getSelectItems().get(0).getExpression()).getValue());
    }

    /**
     * 测试：复杂的嵌套常量折叠
     * SQL: SELECT ((1 + 2) * (3 + 4)) - (5 - 2) FROM dual
     * 预期：SELECT (3 * 7) - 3 → 21 - 3 → 18
     */
    @Test
    public void testComplexNestedConstantFolding() {
        // ((1 + 2) * (3 + 4)) - (5 - 2)
        JQuickExpression left1 = binary(literal(1), literal(2), JQuickBinaryOperator.PLUS);
        JQuickExpression left2 = binary(literal(3), literal(4), JQuickBinaryOperator.PLUS);
        JQuickExpression left = binary(left1, left2, JQuickBinaryOperator.MULTIPLY);
        JQuickExpression right = binary(literal(5), literal(2), JQuickBinaryOperator.MINUS);
        JQuickExpression expr = binary(left, right, JQuickBinaryOperator.MINUS);
        JQuickProjectNode.SelectItem item = new JQuickProjectNode.SelectItem(expr, "result");
        JQuickProjectNode project = new JQuickProjectNode(Collections.singletonList(item), new JQuickTableScanNode("dual"));
        JQuickLogicalPlanNode result = rule.optimize(project);
        assertTrue(result instanceof JQuickProjectNode);
        JQuickProjectNode resultProject = (JQuickProjectNode) result;
        JQuickExpression resultExpr = resultProject.getSelectItems().get(0).getExpression();
        // 完全折叠为常量
        assertTrue(resultExpr instanceof JQuickLiteralExpression);
        assertEquals(18.0d, ((JQuickLiteralExpression) resultExpr).getValue());
    }

    /**
     * 测试：混合常量和列引用的复杂表达式
     * SQL: SELECT (price * (1 + 1)) + (10 - 5) FROM products
     * 预期：SELECT (price * 2) + 5 FROM products
     */
    @Test
    public void testMixedConstantAndColumn() {
        // (price * (1 + 1)) + (10 - 5)
        JQuickExpression multiplyRight = binary(literal(1), literal(1), JQuickBinaryOperator.PLUS);
        JQuickExpression multiply = binary(column("price"), multiplyRight, JQuickBinaryOperator.MULTIPLY);
        JQuickExpression addRight = binary(literal(10), literal(5), JQuickBinaryOperator.MINUS);
        JQuickExpression expr = binary(multiply, addRight, JQuickBinaryOperator.PLUS);
        JQuickProjectNode.SelectItem item = new JQuickProjectNode.SelectItem(expr, "calc");
        JQuickProjectNode project = new JQuickProjectNode(
                Collections.singletonList(item),
                new JQuickTableScanNode("products")
        );
        JQuickLogicalPlanNode result = rule.optimize(project);
        assertTrue(result instanceof JQuickProjectNode);
        JQuickProjectNode resultProject = (JQuickProjectNode) result;
        JQuickExpression resultExpr = resultProject.getSelectItems().get(0).getExpression();
        // 部分折叠后应该是 (price * 2) + 5
        assertTrue(resultExpr instanceof JQuickBinaryExpression);
        JQuickBinaryExpression resultBinary = (JQuickBinaryExpression) resultExpr;
        assertEquals(JQuickBinaryOperator.PLUS, resultBinary.getOperator());
        // 左边是 (price * 2)
        assertTrue(resultBinary.getLeft() instanceof JQuickBinaryExpression);
        JQuickBinaryExpression leftBinary = (JQuickBinaryExpression) resultBinary.getLeft();
        assertEquals(JQuickBinaryOperator.MULTIPLY, leftBinary.getOperator());
        assertTrue(leftBinary.getLeft() instanceof JQuickColumnRefExpression);
        assertEquals("price", ((JQuickColumnRefExpression) leftBinary.getLeft()).getColumnName());
        assertTrue(leftBinary.getRight() instanceof JQuickLiteralExpression);
        assertEquals(2.0d, ((JQuickLiteralExpression) leftBinary.getRight()).getValue());
        assertTrue(resultBinary.getRight() instanceof JQuickLiteralExpression);
        assertEquals(5d, ((JQuickLiteralExpression) resultBinary.getRight()).getValue());
    }

}