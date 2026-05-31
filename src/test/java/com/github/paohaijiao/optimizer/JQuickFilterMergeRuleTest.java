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
import com.github.paohaijiao.optimizer.impl.JQuickFilterMergeRule;
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
public class JQuickFilterMergeRuleTest {

    private final JQuickFilterMergeRule rule = new JQuickFilterMergeRule();
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
     * SQL: SELECT * FROM products WHERE price > 100 WHERE status = 'ACTIVE'
     *
     * 优化前: Filter(price > 100) -> Filter(status = 'ACTIVE') -> TableScan
     * 优化后: Filter(price > 100 AND status = 'ACTIVE') -> TableScan
     */
    @Test
    public void testMergeTwoFilters() {
        JQuickExpression innerPredicate = binary(column("status"), literal("ACTIVE"), JQuickBinaryOperator.EQ);
        JQuickFilterNode innerFilter = new JQuickFilterNode(innerPredicate, new JQuickTableScanNode("products"));
        JQuickExpression outerPredicate = binary(column("price"), literal(100), JQuickBinaryOperator.GT);
        JQuickFilterNode outerFilter = new JQuickFilterNode(outerPredicate, innerFilter);
        JQuickLogicalPlanNode result = rule.apply(outerFilter);
        assertTrue(result instanceof JQuickFilterNode);
        JQuickFilterNode resultFilter = (JQuickFilterNode) result;
        JQuickExpression combined = resultFilter.getPredicate();
        assertTrue(combined instanceof JQuickBinaryExpression);
        JQuickBinaryExpression binary = (JQuickBinaryExpression) combined;
        assertEquals(JQuickBinaryOperator.AND, binary.getOperator());
        assertTrue(resultFilter.getChild() instanceof JQuickTableScanNode);
    }
    /**
     * SQL: SELECT * FROM products WHERE price > 100 WHERE status = 'ACTIVE' WHERE category = 'Electronics'
     *
     * 优化前: 三层 Filter 嵌套
     * 优化后: Filter(price > 100 AND status = 'ACTIVE' AND category = 'Electronics') -> TableScan
     */
    @Test
    public void testMergeThreeFilters() {
        JQuickExpression categoryPredicate = binary(column("category"), literal("Electronics"), JQuickBinaryOperator.EQ);
        JQuickFilterNode categoryFilter = new JQuickFilterNode(categoryPredicate, new JQuickTableScanNode("products"));
        JQuickExpression statusPredicate = binary(column("status"), literal("ACTIVE"), JQuickBinaryOperator.EQ);
        JQuickFilterNode statusFilter = new JQuickFilterNode(statusPredicate, categoryFilter);
        JQuickExpression pricePredicate = binary(column("price"), literal(100), JQuickBinaryOperator.GT);
        JQuickFilterNode priceFilter = new JQuickFilterNode(pricePredicate, statusFilter);
        JQuickLogicalPlanNode result = rule.apply(priceFilter);
        result = rule.apply(result);
        assertTrue(result instanceof JQuickFilterNode);
        JQuickFilterNode resultFilter = (JQuickFilterNode) result;
        assertTrue(resultFilter.getChild() instanceof JQuickTableScanNode);
        JQuickExpression combined = resultFilter.getPredicate();
        assertTrue(combined instanceof JQuickBinaryExpression);
    }
    /**
     * SQL: SELECT name, price FROM products WHERE price > 100 WHERE price < 500
     *
     * 验证：Filter -> Filter -> Project -> TableScan
     * 合并后：Filter -> Project -> TableScan
     */
    @Test
    public void testMergeFiltersWithProject() {
        JQuickTableScanNode scan = new JQuickTableScanNode("products");
        JQuickProjectNode.SelectItem nameItem = new JQuickProjectNode.SelectItem(column("name"), "name");
        JQuickProjectNode.SelectItem priceItem = new JQuickProjectNode.SelectItem(column("price"), "price");
        JQuickProjectNode project = new JQuickProjectNode(Arrays.asList(nameItem, priceItem), scan);
        JQuickExpression innerPredicate = binary(column("price"), literal(500), JQuickBinaryOperator.LT);
        JQuickFilterNode innerFilter = new JQuickFilterNode(innerPredicate, project);
        JQuickExpression outerPredicate = binary(column("price"), literal(100), JQuickBinaryOperator.GT);
        JQuickFilterNode outerFilter = new JQuickFilterNode(outerPredicate, innerFilter);
        JQuickLogicalPlanNode result = rule.apply(outerFilter);
        assertTrue(result instanceof JQuickFilterNode);
        JQuickFilterNode resultFilter = (JQuickFilterNode) result;
        assertTrue(resultFilter.getChild() instanceof JQuickProjectNode);
        JQuickProjectNode resultProject = (JQuickProjectNode) resultFilter.getChild();
        assertTrue(resultProject.getChild() instanceof JQuickTableScanNode);
    }


    /**
     * SQL: SELECT * FROM orders o JOIN customers c ON o.customer_id = c.id
     *       WHERE o.amount > 100 WHERE c.status = 'VIP'
     *
     * 验证：Filter -> Filter -> Join
     * 合并后：Filter -> Join（Filter 合并为一个）
     */
    @Test
    public void testMergeFiltersWithJoin() {
        JQuickTableScanNode orders = new JQuickTableScanNode("orders", "o");
        JQuickTableScanNode customers = new JQuickTableScanNode("customers", "c");
        JQuickExpression joinCondition = binary(column("o.customer_id"), column("c.id"), JQuickBinaryOperator.EQ);
        JQuickJoinNode join = new JQuickJoinNode(JQuickJoinType.INNER, orders, customers, joinCondition);
        // 内层 Filter: c.status = 'VIP'
        JQuickExpression innerPredicate = binary(column("c.status"), literal("VIP"), JQuickBinaryOperator.EQ);
        JQuickFilterNode innerFilter = new JQuickFilterNode(innerPredicate, join);
        // 外层 Filter: o.amount > 100
        JQuickExpression outerPredicate = binary(column("o.amount"), literal(100), JQuickBinaryOperator.GT);
        JQuickFilterNode outerFilter = new JQuickFilterNode(outerPredicate, innerFilter);
        JQuickLogicalPlanNode result = rule.apply(outerFilter);
        // 应该合并为一个 Filter -> Join
        assertTrue(result instanceof JQuickFilterNode);
        JQuickFilterNode resultFilter = (JQuickFilterNode) result;
        assertTrue(resultFilter.getChild() instanceof JQuickJoinNode);
        // 验证合并后的条件是 AND
        JQuickExpression combined = resultFilter.getPredicate();
        assertTrue(combined instanceof JQuickBinaryExpression);
        assertEquals(JQuickBinaryOperator.AND, ((JQuickBinaryExpression) combined).getOperator());
    }
    /**
     * SQL: SELECT department, SUM(salary) FROM employees
     *       WHERE salary > 5000 WHERE age > 25
     *       GROUP BY department
     *
     * 优化前: GroupBy -> Filter(salary > 5000) -> Filter(age > 25) -> TableScan
     * 优化后: GroupBy -> Filter(salary > 5000 AND age > 25) -> TableScan
     */
    @Test
    public void testMergeFiltersBeforeGroupBy() {
        JQuickTableScanNode scan = new JQuickTableScanNode("employees");
        JQuickExpression agePredicate = binary(column("age"), literal(25), JQuickBinaryOperator.GT);
        JQuickFilterNode ageFilter = new JQuickFilterNode(agePredicate, scan);
        JQuickExpression salaryPredicate = binary(column("salary"), literal(5000), JQuickBinaryOperator.GT);
        JQuickFilterNode salaryFilter = new JQuickFilterNode(salaryPredicate, ageFilter);
        JQuickGroupByNode groupBy = new JQuickGroupByNode(Collections.singletonList(column("department")), Collections.singletonList(new JQuickGroupByNode.AggregateItem(column("salary"), "SUM", "total_salary")), salaryFilter, null);
        JQuickLogicalPlanNode result = rule.apply(groupBy);
        result = rule.apply(result);  // 递归处理子节点
        assertTrue(result instanceof JQuickGroupByNode);
        JQuickGroupByNode resultGroupBy = (JQuickGroupByNode) result;
        assertTrue(resultGroupBy.getChild() instanceof JQuickFilterNode);
        JQuickFilterNode resultFilter = (JQuickFilterNode) resultGroupBy.getChild();
        JQuickExpression combined = resultFilter.getPredicate();
        assertTrue(combined instanceof JQuickBinaryExpression);
        assertEquals(JQuickBinaryOperator.AND, ((JQuickBinaryExpression) combined).getOperator());
        assertTrue(resultFilter.getChild() instanceof JQuickTableScanNode);
    }
    /**
     * SQL: SELECT department, SUM(salary) FROM employees
     *       GROUP BY department
     *       HAVING SUM(salary) > 10000 HAVING AVG(age) > 30
     *
     * 优化前: Filter(HAVING2) -> Filter(HAVING1) -> GroupBy -> TableScan
     * 优化后: Filter(HAVING1 AND HAVING2) -> GroupBy -> TableScan
     */
    @Test
    public void testMergeFiltersAfterGroupBy() {
        JQuickTableScanNode scan = new JQuickTableScanNode("employees");
        JQuickGroupByNode groupBy = new JQuickGroupByNode(Collections.singletonList(column("department")),
                Arrays.asList(
                        new JQuickGroupByNode.AggregateItem(column("salary"), "SUM", "total_salary"),
                        new JQuickGroupByNode.AggregateItem(column("age"), "AVG", "avg_age")
                ),
                scan,
                null
        );

        JQuickExpression avgPredicate = binary(column("avg_age"), literal(30), JQuickBinaryOperator.GT);
        JQuickFilterNode avgFilter = new JQuickFilterNode(avgPredicate, groupBy);

        JQuickExpression sumPredicate = binary(column("total_salary"), literal(10000), JQuickBinaryOperator.GT);
        JQuickFilterNode sumFilter = new JQuickFilterNode(sumPredicate, avgFilter);
        JQuickLogicalPlanNode result = rule.apply(sumFilter);
        result = rule.apply(result);
        assertTrue(result instanceof JQuickFilterNode);
        JQuickFilterNode resultFilter = (JQuickFilterNode) result;
        JQuickExpression combined = resultFilter.getPredicate();
        assertTrue(combined instanceof JQuickBinaryExpression);
        assertEquals(JQuickBinaryOperator.AND, ((JQuickBinaryExpression) combined).getOperator());
        assertTrue(resultFilter.getChild() instanceof JQuickGroupByNode);
    }

    /**
     * SQL: SELECT * FROM products WHERE price > 100 WHERE status = 'ACTIVE' ORDER BY price
     *
     * 优化前: Sort -> Filter(price > 100) -> Filter(status = 'ACTIVE') -> TableScan
     * 优化后: Sort -> Filter(price > 100 AND status = 'ACTIVE') -> TableScan
     */
    @Test
    public void testMergeFiltersBeforeSort() {
        JQuickTableScanNode scan = new JQuickTableScanNode("products");
        JQuickExpression statusPredicate = binary(column("status"), literal("ACTIVE"), JQuickBinaryOperator.EQ);
        JQuickFilterNode statusFilter = new JQuickFilterNode(statusPredicate, scan);
        JQuickExpression pricePredicate = binary(column("price"), literal(100), JQuickBinaryOperator.GT);
        JQuickFilterNode priceFilter = new JQuickFilterNode(pricePredicate, statusFilter);
        JQuickSortNode sort = new JQuickSortNode(Collections.singletonList(new JQuickSortNode.OrderByItem("price", true)), priceFilter);
        JQuickLogicalPlanNode result = rule.apply(sort);
        result = rule.apply(result);
        assertTrue(result instanceof JQuickSortNode);
        JQuickSortNode resultSort = (JQuickSortNode) result;
        assertTrue(resultSort.getChild() instanceof JQuickFilterNode);
        JQuickFilterNode resultFilter = (JQuickFilterNode) resultSort.getChild();
        JQuickExpression combined = resultFilter.getPredicate();
        assertTrue(combined instanceof JQuickBinaryExpression);
        assertEquals(JQuickBinaryOperator.AND, ((JQuickBinaryExpression) combined).getOperator());
        assertTrue(resultFilter.getChild() instanceof JQuickTableScanNode);
    }
    /**
     * SQL: SELECT * FROM products WHERE price > 100 WHERE status = 'ACTIVE' LIMIT 10
     *
     * 优化前: Limit -> Filter(price > 100) -> Filter(status = 'ACTIVE') -> TableScan
     * 优化后: Limit -> Filter(price > 100 AND status = 'ACTIVE') -> TableScan
     */
    @Test
    public void testMergeFiltersBeforeLimit() {
        JQuickTableScanNode scan = new JQuickTableScanNode("products");
        JQuickExpression statusPredicate = binary(column("status"), literal("ACTIVE"), JQuickBinaryOperator.EQ);
        JQuickFilterNode statusFilter = new JQuickFilterNode(statusPredicate, scan);
        JQuickExpression pricePredicate = binary(column("price"), literal(100), JQuickBinaryOperator.GT);
        JQuickFilterNode priceFilter = new JQuickFilterNode(pricePredicate, statusFilter);
        JQuickLimitNode limit = new JQuickLimitNode(10, priceFilter);
        JQuickLogicalPlanNode result = rule.apply(limit);
        result = rule.apply(result);
        assertTrue(result instanceof JQuickLimitNode);
        JQuickLimitNode resultLimit = (JQuickLimitNode) result;
        assertTrue(resultLimit.getChild() instanceof JQuickFilterNode);
        JQuickFilterNode resultFilter = (JQuickFilterNode) resultLimit.getChild();
        JQuickExpression combined = resultFilter.getPredicate();
        assertTrue(combined instanceof JQuickBinaryExpression);
        assertEquals(JQuickBinaryOperator.AND, ((JQuickBinaryExpression) combined).getOperator());
        assertTrue(resultFilter.getChild() instanceof JQuickTableScanNode);
        assertEquals(10, resultLimit.getLimit());
    }
}
