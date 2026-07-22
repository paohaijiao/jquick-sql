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

import static org.junit.Assert.*;

/**
 * packageName com.github.paohaijiao.optimizer
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/5/23
 */
public class JQuickPredicatePushdownRuleTest {
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
     * SQL: SELECT * FROM products WHERE price > 100
     * 优化前: Filter(price > 100) -> TableScan(products)
     * 优化后: TableScan(products, filter=price > 100)
     */
    @Test
    public void testSql_SimpleWhereCondition() {
        // SQL: SELECT * FROM products WHERE price > 100
        JQuickExpression predicate = binary(column("price"), literal(100), JQuickBinaryOperator.GT);
        JQuickFilterNode filter = new JQuickFilterNode(predicate, new JQuickTableScanNode("products"));
        JQuickLogicalPlanNode result = rule.optimize(filter);
        assertTrue(result instanceof JQuickTableScanNode);
        JQuickTableScanNode scan = (JQuickTableScanNode) result;
        assertNotNull(scan.getFilterPredicate());
        assertEquals("products", scan.getTableName());
    }
    /**
     * SQL: SELECT * FROM users WHERE age >= 18 AND city = 'Beijing'
     * 优化前: Filter(age >= 18 AND city = 'Beijing') -> TableScan(users)
     * 优化后: TableScan(users, filter=age >= 18 AND city = 'Beijing')
     */
    @Test
    public void testSql_MultipleWhereConditions() {
        // SQL: SELECT * FROM users WHERE age >= 18 AND city = 'Beijing'
        JQuickExpression cond1 = binary(column("age"), literal(18), JQuickBinaryOperator.GE);
        JQuickExpression cond2 = binary(column("city"), literal("Beijing"), JQuickBinaryOperator.EQ);
        JQuickExpression predicate = binary(cond1, cond2, JQuickBinaryOperator.AND);
        JQuickFilterNode filter = new JQuickFilterNode(predicate, new JQuickTableScanNode("users"));
        JQuickLogicalPlanNode result = rule.optimize(filter);
        assertTrue(result instanceof JQuickTableScanNode);
        JQuickTableScanNode scan = (JQuickTableScanNode) result;
        assertNotNull(scan.getFilterPredicate());
    }
    /**
     * SQL: SELECT name, price FROM products WHERE price > 100
     * 优化前: Project(name, price) -> Filter(price > 100) -> TableScan(products)
     * 优化后: Filter(price > 100) -> Project(name, price) -> TableScan(products)
     */
    @Test
    public void testSql_SelectWithWhere() {
        // SQL: SELECT name, price FROM products WHERE price > 100
        JQuickProjectNode.SelectItem nameItem = new JQuickProjectNode.SelectItem(column("name"), "name");
        JQuickProjectNode.SelectItem priceItem = new JQuickProjectNode.SelectItem(column("price"), "price");
        JQuickProjectNode project = new JQuickProjectNode(Arrays.asList(nameItem, priceItem), new JQuickTableScanNode("products"));
        JQuickExpression predicate = binary(column("price"), literal(100), JQuickBinaryOperator.GT);
        JQuickFilterNode filter = new JQuickFilterNode(predicate, project);
        JQuickLogicalPlanNode result = rule.optimize(filter);
        assertTrue(result instanceof JQuickProjectNode);
        JQuickProjectNode resultProject = (JQuickProjectNode) result;
    }
    /**
     * SQL: SELECT * FROM orders o JOIN customers c ON o.customer_id = c.id WHERE o.amount > 1000
     * 优化前: Filter(o.amount > 1000) -> Join(o, c)
     * 优化后: Join(Filter(o.amount > 1000), c)
     */
    @Test
    public void testSql_JoinWithLeftTableFilter() {
        // SQL: SELECT * FROM orders o JOIN customers c ON o.customer_id = c.id WHERE o.amount > 1000
        JQuickTableScanNode orders = new JQuickTableScanNode("orders", "o");
        JQuickTableScanNode customers = new JQuickTableScanNode("customers", "c");
        JQuickExpression joinCondition = binary(column("o.customer_id"), column("c.id"), JQuickBinaryOperator.EQ);
        JQuickJoinNode join = new JQuickJoinNode(JQuickJoinType.INNER, orders, customers, joinCondition,null);
        JQuickExpression predicate = binary(column("o.amount"), literal(1000), JQuickBinaryOperator.GT);
        JQuickFilterNode filter = new JQuickFilterNode(predicate, join);
        JQuickLogicalPlanNode result = rule.optimize(filter);
        assertTrue(result instanceof JQuickJoinNode);
        JQuickJoinNode resultJoin = (JQuickJoinNode) result;
        // 左表应该有 Filter
        assertTrue(resultJoin.getLeft() instanceof JQuickFilterNode);
        JQuickFilterNode leftFilter = (JQuickFilterNode) resultJoin.getLeft();
        JQuickExpression resultPredicate = leftFilter.getPredicate();
        assertTrue(resultPredicate instanceof JQuickBinaryExpression);
        assertEquals(JQuickBinaryOperator.GT, ((JQuickBinaryExpression) resultPredicate).getOperator());
    }
    @Test
    public void testSql_WindowFunctionWithFilter() {
        JQuickTableScanNode scan = new JQuickTableScanNode("employees");
        JQuickExpression predicate = binary(column("salary"), literal(3000), JQuickBinaryOperator.GT);
        JQuickFilterNode filter = new JQuickFilterNode(predicate, scan);
        JQuickWindowNode.WindowSpec windowSpec = new JQuickWindowNode.WindowSpec(
                Collections.singletonList(column("department")),
                Collections.singletonList(new JQuickSortNode.OrderByItem("salary", true)),
                null
        );
        JQuickWindowNode.WindowFunction windowFunc = new JQuickWindowNode.WindowFunction(
                "ROW_NUMBER", null, windowSpec, "rn"
        );
        JQuickWindowNode window = new JQuickWindowNode(Collections.singletonList(windowFunc), filter);
        JQuickLogicalPlanNode result = rule.optimize(window);
        System.out.println(result);
    }
}
