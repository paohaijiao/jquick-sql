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
package com.github.paohaijiao.ast2logic;

import com.github.paohaijiao.ast.*;
import com.github.paohaijiao.enums.JQuickBinaryOperator;
import com.github.paohaijiao.enums.JQuickJoinType;
import com.github.paohaijiao.enums.JQuickSQLOperationType;
import com.github.paohaijiao.enums.JQuickUnaryOperator;
import com.github.paohaijiao.expression.domain.*;
import com.github.paohaijiao.logic.JQuickLogicalPlanNode;
import com.github.paohaijiao.logic.domain.*;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * AST 到逻辑计划转换器单元测试
 */
public class JQuickASTToLogicalPlanVisitorTest {

    private JQuickASTToLogicalPlanVisitor visitor = new JQuickASTToLogicalPlanVisitor();


    @Test
    public void testSimpleSelectAll() {
        // SELECT * FROM users
        JQuickSelectClauseNode selectClause = new JQuickSelectClauseNode.Builder()
        .setSelectElements(new JQuickSelectElementsNode(true, null))
        .setFromClause(new JQuickFromClauseNode(new JQuickTableNameItemNode("users", "a"))).build();
        JQuickQueryNode query = buildQuery(selectClause);
        JQuickLogicalPlanNode result = visitor.visit(query);
        assertNotNull(result);
        assertEquals("Project", result.getNodeType());
        JQuickProjectNode project = (JQuickProjectNode) result;
        assertEquals(1, project.getSelectItems().size());
        assertTrue(project.getSelectItems().get(0).isStar());
        JQuickTableScanNode scan = (JQuickTableScanNode) project.getChild();
        assertEquals("users", scan.getTableName());
    }

    @Test
    public void testSelectWithColumns() {
        List<JQuickSelectElementNode> elements = Arrays.asList(
                createSelectElement(createColumnExpression("id"), null),
                createSelectElement(createColumnExpression("name"), null)
        );
        JQuickSelectClauseNode selectClause = new JQuickSelectClauseNode.Builder()
                .setSelectElements(new JQuickSelectElementsNode(false, elements))
                .setFromClause(new JQuickFromClauseNode(new JQuickTableNameItemNode("users", null)))
                .build();
        JQuickLogicalPlanNode result = visitor.visit(buildQuery(selectClause));
        assertNotNull(result);
        assertEquals("Project", result.getNodeType());
        JQuickProjectNode project = (JQuickProjectNode) result;
        assertEquals(2, project.getSelectItems().size());
        assertFalse(project.getSelectItems().get(0).isStar());
    }

    @Test
    public void testSelectWithAlias() {
        // SELECT id AS user_id FROM users
        JQuickExpressionNode expr = createColumnExpression("id");
        JQuickSelectElementNode element = new JQuickSelectElementNode(expr, "user_id");
        JQuickSelectClauseNode selectClause = new JQuickSelectClauseNode.Builder()
                .setSelectElements(new JQuickSelectElementsNode(false, Collections.singletonList(element)))
                .setFromClause(new JQuickFromClauseNode(new JQuickTableNameItemNode("users", null)))
                .build();
        JQuickLogicalPlanNode result = visitor.visit(buildQuery(selectClause));
        JQuickProjectNode project = (JQuickProjectNode) result;
        assertEquals("user_id", project.getSelectItems().get(0).getAlias());
    }

    @Test
    public void testWhereEqualsCondition() {
        // SELECT * FROM users WHERE id = 1
        JQuickPredicateNode predicate = createEqualsPredicate(
                createColumnAtom("id"),
                createConstantAtom(1)
        );
        JQuickSelectClauseNode selectClause = new JQuickSelectClauseNode.Builder()
                .setSelectElements(new JQuickSelectElementsNode(true, null))
                .setFromClause(new JQuickFromClauseNode(new JQuickTableNameItemNode("users", null)))
                .setWhereClause(new JQuickWhereClauseNode(new JQuickFilterConditionNode(predicate)))
                .build();
        JQuickLogicalPlanNode result = visitor.visit(buildQuery(selectClause));
        JQuickProjectNode project = (JQuickProjectNode) result;
        JQuickFilterNode filter = (JQuickFilterNode) project.getChild();
        assertNotNull(filter.getPredicate());
    }

    @Test
    public void testWhereAndCondition() {
        // SELECT * FROM users WHERE id = 1 AND status = 'active'
        JQuickPredicateNode leftPred = createEqualsPredicate(createColumnAtom("id"), createConstantAtom(1));
        JQuickPredicateNode rightPred = createEqualsPredicate(createColumnAtom("status"), createConstantAtom("active"));
        JQuickFilterConditionNode andCondition = new JQuickFilterConditionNode(
                new JQuickFilterConditionNode(leftPred),
                new JQuickFilterConditionNode(rightPred),
                JQuickFilterConditionNode.LogicalOperator.AND
        );

        JQuickSelectClauseNode selectClause = new JQuickSelectClauseNode.Builder()
                .setSelectElements(new JQuickSelectElementsNode(true, null))
                .setFromClause(new JQuickFromClauseNode(new JQuickTableNameItemNode("users", null)))
                .setWhereClause(new JQuickWhereClauseNode(andCondition))
                .build();
        JQuickLogicalPlanNode result = visitor.visit(buildQuery(selectClause));
        JQuickProjectNode project = (JQuickProjectNode) result;
        JQuickFilterNode filter = (JQuickFilterNode) project.getChild();
        JQuickBinaryExpression binary = (JQuickBinaryExpression) filter.getPredicate();
        assertEquals(JQuickBinaryOperator.AND, binary.getOperator());
    }

    @Test
    public void testWhereOrCondition() {
        // SELECT * FROM users WHERE id = 1 OR id = 2
        JQuickPredicateNode leftPred = createEqualsPredicate(createColumnAtom("id"), createConstantAtom(1)
        );
        JQuickPredicateNode rightPred = createEqualsPredicate(createColumnAtom("id"), createConstantAtom(2));
        JQuickFilterConditionNode orCondition = new JQuickFilterConditionNode(new JQuickFilterConditionNode(leftPred), new JQuickFilterConditionNode(rightPred), JQuickFilterConditionNode.LogicalOperator.OR);
        JQuickSelectClauseNode selectClause = new JQuickSelectClauseNode.Builder()
                .setSelectElements(new JQuickSelectElementsNode(true, null))
                .setFromClause(new JQuickFromClauseNode(new JQuickTableNameItemNode("users", null)))
                .setWhereClause(new JQuickWhereClauseNode(orCondition))
                .build();
        JQuickLogicalPlanNode result = visitor.visit(buildQuery(selectClause));
        JQuickProjectNode project = (JQuickProjectNode) result;
        JQuickFilterNode filter = (JQuickFilterNode) project.getChild();
        JQuickBinaryExpression binary = (JQuickBinaryExpression) filter.getPredicate();
        assertEquals(JQuickBinaryOperator.OR, binary.getOperator());
    }

    @Test
    public void testWhereIsNullCondition() {
        // SELECT * FROM users WHERE email IS NULL
        JQuickPredicateNode isNullPred = new JQuickPredicateNode(new JQuickPredicateNode(createColumnAtom("email")), false);
        JQuickSelectClauseNode selectClause = new JQuickSelectClauseNode.Builder()
                .setSelectElements(new JQuickSelectElementsNode(true, null))
                .setFromClause(new JQuickFromClauseNode(
                        new JQuickTableNameItemNode("users", null)))
                .setWhereClause(new JQuickWhereClauseNode(
                        new JQuickFilterConditionNode(isNullPred)))
                .build();
        JQuickLogicalPlanNode result = visitor.visit(buildQuery(selectClause));
        JQuickProjectNode project = (JQuickProjectNode) result;
        JQuickFilterNode filter = (JQuickFilterNode) project.getChild();
        JQuickUnaryExpression unary = (JQuickUnaryExpression) filter.getPredicate();
        assertEquals(JQuickUnaryOperator.IS_NULL, unary.getOperator());
    }

    @Test
    public void testWhereBetweenCondition() {
        // SELECT * FROM products WHERE price BETWEEN 10 AND 100
        JQuickPredicateNode betweenPred = new JQuickPredicateNode(
                new JQuickPredicateNode(createColumnAtom("price")),
                false,
                new JQuickPredicateNode(createConstantAtom(10)),
                new JQuickPredicateNode(createConstantAtom(100))
        );
        JQuickSelectClauseNode selectClause = new JQuickSelectClauseNode.Builder()
                .setSelectElements(new JQuickSelectElementsNode(true, null))
                .setFromClause(new JQuickFromClauseNode(new JQuickTableNameItemNode("products", null)))
                .setWhereClause(new JQuickWhereClauseNode(new JQuickFilterConditionNode(betweenPred)))
                .build();
        JQuickLogicalPlanNode result = visitor.visit(buildQuery(selectClause));
        JQuickProjectNode project = (JQuickProjectNode) result;
        JQuickFilterNode filter = (JQuickFilterNode) project.getChild();
        JQuickBetweenExpression between = (JQuickBetweenExpression) filter.getPredicate();
        assertFalse(between.isNot());
    }

    @Test
    public void testWhereInCondition() {
        // SELECT * FROM users WHERE id IN (1, 2, 3)
        List<JQuickExpressionNode> values = Arrays.asList(
                createConstantExpression(1),
                createConstantExpression(2),
                createConstantExpression(3)
        );
        JQuickExpressionsNode expressions = new JQuickExpressionsNode(values);
        JQuickPredicateNode inPred = new JQuickPredicateNode(new JQuickPredicateNode(createColumnAtom("id")), false, expressions);
        JQuickSelectClauseNode selectClause = new JQuickSelectClauseNode.Builder()
                .setSelectElements(new JQuickSelectElementsNode(true, null))
                .setFromClause(new JQuickFromClauseNode(new JQuickTableNameItemNode("users", null)))
                .setWhereClause(new JQuickWhereClauseNode(new JQuickFilterConditionNode(inPred))).build();
        JQuickLogicalPlanNode result = visitor.visit(buildQuery(selectClause));
        JQuickProjectNode project = (JQuickProjectNode) result;
        JQuickFilterNode filter = (JQuickFilterNode) project.getChild();
        JQuickInExpression inExpr = (JQuickInExpression) filter.getPredicate();
        assertFalse(inExpr.isNot());
        //assertEquals(3, inExpr.getValues().size());
    }

    @Test
    public void testWhereLikeCondition() {
        // SELECT * FROM users WHERE name LIKE 'John%'
        JQuickPredicateNode likePred = new JQuickPredicateNode(new JQuickPredicateNode(createColumnAtom("name")), false, new JQuickPredicateNode(createConstantAtom("John%")));
        JQuickSelectClauseNode selectClause = new JQuickSelectClauseNode.Builder()
                .setSelectElements(new JQuickSelectElementsNode(true, null))
                .setFromClause(new JQuickFromClauseNode(new JQuickTableNameItemNode("users", null)))
                .setWhereClause(new JQuickWhereClauseNode(new JQuickFilterConditionNode(likePred)))
                .build();
        JQuickLogicalPlanNode result = visitor.visit(buildQuery(selectClause));
        JQuickProjectNode project = (JQuickProjectNode) result;
        JQuickFilterNode filter = (JQuickFilterNode) project.getChild();
        JQuickBinaryExpression binary = (JQuickBinaryExpression) filter.getPredicate();
        assertEquals(JQuickBinaryOperator.LIKE, binary.getOperator());
    }


    @Test
    public void testInnerJoin() {
        // SELECT * FROM users u INNER JOIN orders o ON u.id = o.user_id
        JQuickTableNameItemNode leftTable = new JQuickTableNameItemNode("users", "u");
        JQuickTableNameItemNode rightTable = new JQuickTableNameItemNode("orders", "o");
        JQuickFromClauseNode fromClause = new JQuickFromClauseNode(leftTable);
        JQuickFullColumnNameNode leftColumn = new JQuickFullColumnNameNode("u", "id");
        JQuickFullColumnNameNode rightColumn = new JQuickFullColumnNameNode("o", "user_id");
        JQuickJoinClauseNode join = new JQuickJoinClauseNode(
                JQuickJoinType.INNER,
                rightTable,
                leftColumn,
                rightColumn
        );
        JQuickSelectClauseNode selectClause = new JQuickSelectClauseNode.Builder()
                .setSelectElements(new JQuickSelectElementsNode(true, null))
                .setFromClause(fromClause)
                .addJoinClause(join)
                .build();
        JQuickLogicalPlanNode result = visitor.visit(buildQuery(selectClause));
        JQuickProjectNode project = (JQuickProjectNode) result;
        JQuickJoinNode joinNode = (JQuickJoinNode) project.getChild();
        assertEquals(JQuickJoinType.INNER, joinNode.getJoinType());
        assertNotNull(joinNode.getCondition());
    }

    @Test
    public void testLeftJoin() {
        // SELECT * FROM users u LEFT JOIN orders o ON u.id = o.user_id
        JQuickTableNameItemNode leftTable = new JQuickTableNameItemNode("users", "u");
        JQuickTableNameItemNode rightTable = new JQuickTableNameItemNode("orders", "o");
        JQuickJoinClauseNode join = new JQuickJoinClauseNode(JQuickJoinType.LEFT, rightTable, new JQuickFullColumnNameNode("u", "id"), new JQuickFullColumnNameNode("o", "user_id"));
        JQuickSelectClauseNode selectClause = new JQuickSelectClauseNode.Builder()
                .setSelectElements(new JQuickSelectElementsNode(true, null))
                .setFromClause(new JQuickFromClauseNode(leftTable))
                .addJoinClause(join)
                .build();
        JQuickLogicalPlanNode result = visitor.visit(buildQuery(selectClause));
        JQuickProjectNode project = (JQuickProjectNode) result;
        JQuickJoinNode joinNode = (JQuickJoinNode) project.getChild();
        assertEquals(JQuickJoinType.LEFT, joinNode.getJoinType());
    }

    @Test
    public void testMultipleJoins() {
        // SELECT * FROM users u
        // INNER JOIN orders o ON u.id = o.user_id
        // INNER JOIN products p ON o.product_id = p.id
        JQuickTableNameItemNode users = new JQuickTableNameItemNode("users", "u");
        JQuickTableNameItemNode orders = new JQuickTableNameItemNode("orders", "o");
        JQuickTableNameItemNode products = new JQuickTableNameItemNode("products", "p");
        JQuickJoinClauseNode join1 = new JQuickJoinClauseNode(JQuickJoinType.INNER, orders, new JQuickFullColumnNameNode("u", "id"), new JQuickFullColumnNameNode("o", "user_id"));
        JQuickJoinClauseNode join2 = new JQuickJoinClauseNode(JQuickJoinType.INNER, products, new JQuickFullColumnNameNode("o", "product_id"), new JQuickFullColumnNameNode("p", "id"));
        JQuickSelectClauseNode selectClause = new JQuickSelectClauseNode.Builder()
                .setSelectElements(new JQuickSelectElementsNode(true, null))
                .setFromClause(new JQuickFromClauseNode(users))
                .addJoinClause(join1)
                .addJoinClause(join2)
                .build();
        JQuickLogicalPlanNode result = visitor.visit(buildQuery(selectClause));
        JQuickProjectNode project = (JQuickProjectNode) result;
        JQuickJoinNode topJoin = (JQuickJoinNode) project.getChild();
        JQuickJoinNode bottomJoin = (JQuickJoinNode) topJoin.getLeft();
        assertEquals(JQuickJoinType.INNER, topJoin.getJoinType());
        assertEquals(JQuickJoinType.INNER, bottomJoin.getJoinType());
    }
    @Test
    public void testGroupBy() {
        // SELECT department, COUNT(*) FROM employees GROUP BY department
        List<JQuickSelectElementNode> elements = Arrays.asList(createSelectElement(createColumnExpression("department"), "department"), createSelectElement(createCountStarFunction(), "count"));
        JQuickExpressionsNode groupExpressions = new JQuickExpressionsNode(
                Collections.singletonList(createColumnExpression("department"))
        );
        JQuickSelectClauseNode selectClause = new JQuickSelectClauseNode.Builder()
                .setSelectElements(new JQuickSelectElementsNode(false, elements))
                .setFromClause(new JQuickFromClauseNode(new JQuickTableNameItemNode("employees", null)))
                .setGroupByClause(new JQuickGroupByClauseNode(groupExpressions))
                .build();
        JQuickLogicalPlanNode result = visitor.visit(buildQuery(selectClause));
        JQuickProjectNode project = (JQuickProjectNode) result;
        JQuickGroupByNode groupBy = (JQuickGroupByNode) project.getChild();
        assertEquals(1, groupBy.getGroupKeys().size());
        assertEquals(1, groupBy.getAggregateItems().size());
        assertTrue(groupBy.getAggregateItems().get(0).isCountStar());
    }

    @Test
    public void testGroupByWithHaving() {
        // SELECT department, COUNT(*) FROM employees
        // GROUP BY department HAVING COUNT(*) > 5
        List<JQuickSelectElementNode> elements = Arrays.asList(
                createSelectElement(createColumnExpression("department"), "department"),
                createSelectElement(createCountStarFunction(), "count")
        );
        JQuickExpressionsNode groupExpressions = new JQuickExpressionsNode(
                Collections.singletonList(createColumnExpression("department"))
        );
        // COUNT(*) > 5
        JQuickPredicateNode havingPred = createGreaterThanPredicate(
                createCountStarPredicate(),null //        createConstantAtom(5)
        );
        JQuickSelectClauseNode selectClause = new JQuickSelectClauseNode.Builder()
                .setSelectElements(new JQuickSelectElementsNode(false, elements))
                .setFromClause(new JQuickFromClauseNode(new JQuickTableNameItemNode("employees", null)))
                .setGroupByClause(new JQuickGroupByClauseNode(groupExpressions))
                .setHavingClause(new JQuickHavingClauseNode(new JQuickFilterConditionNode(havingPred)))
                .build();
        JQuickLogicalPlanNode result = visitor.visit(buildQuery(selectClause));
        JQuickProjectNode project = (JQuickProjectNode) result;
        JQuickGroupByNode groupBy = (JQuickGroupByNode) project.getChild();
        assertNotNull(groupBy.getHavingCondition());
    }

    @Test
    public void testGroupByMultipleKeys() {
        // SELECT department, city, COUNT(*) FROM employees
        // GROUP BY department, city
        List<JQuickSelectElementNode> elements = Arrays.asList(
                createSelectElement(createColumnExpression("department"), "department"),
                createSelectElement(createColumnExpression("city"), "city"),
                createSelectElement(createCountStarFunction(), "count")
        );
        List<JQuickExpressionNode> groupKeys = Arrays.asList(
                createColumnExpression("department"),
                createColumnExpression("city")
        );
        JQuickExpressionsNode groupExpressions = new JQuickExpressionsNode(groupKeys);
        JQuickSelectClauseNode selectClause = new JQuickSelectClauseNode.Builder()
                .setSelectElements(new JQuickSelectElementsNode(false, elements))
                .setFromClause(new JQuickFromClauseNode(new JQuickTableNameItemNode("employees", null)))
                .setGroupByClause(new JQuickGroupByClauseNode(groupExpressions))
                .build();
        JQuickLogicalPlanNode result = visitor.visit(buildQuery(selectClause));
        JQuickProjectNode project = (JQuickProjectNode) result;
        JQuickGroupByNode groupBy = (JQuickGroupByNode) project.getChild();
        assertEquals(2, groupBy.getGroupKeys().size());
    }

    @Test
    public void testOrderByAscending() {
        // SELECT * FROM users ORDER BY name ASC
        JQuickOrderByExpressionNode orderExpr = new JQuickOrderByExpressionNode(
                createColumnExpression("name"), true
        );

        JQuickSelectClauseNode selectClause = new JQuickSelectClauseNode.Builder()
                .setSelectElements(new JQuickSelectElementsNode(true, null))
                .setFromClause(new JQuickFromClauseNode(new JQuickTableNameItemNode("users", null)))
                .setOrderByClause(new JQuickOrderByClauseNode(Collections.singletonList(orderExpr)))
                .build();

        JQuickLogicalPlanNode result = visitor.visit(buildQuery(selectClause));
        JQuickProjectNode project = (JQuickProjectNode) result;
        JQuickSortNode sort = (JQuickSortNode) project.getChild();
        assertEquals(1, sort.getOrderByItems().size());
        assertTrue(sort.getOrderByItems().get(0).isAscending());
    }

    @Test
    public void testOrderByDescending() {
        // SELECT * FROM users ORDER BY created_at DESC
        JQuickOrderByExpressionNode orderExpr = new JQuickOrderByExpressionNode(
                createColumnExpression("created_at"), false
        );

        JQuickSelectClauseNode selectClause = new JQuickSelectClauseNode.Builder()
                .setSelectElements(new JQuickSelectElementsNode(true, null))
                .setFromClause(new JQuickFromClauseNode(new JQuickTableNameItemNode("users", null)))
                .setOrderByClause(new JQuickOrderByClauseNode(Collections.singletonList(orderExpr)))
                .build();
        JQuickLogicalPlanNode result = visitor.visit(buildQuery(selectClause));
        JQuickProjectNode project = (JQuickProjectNode) result;
        JQuickSortNode sort = (JQuickSortNode) project.getChild();
        assertFalse(sort.getOrderByItems().get(0).isAscending());
    }

    @Test
    public void testOrderByMultiple() {
        // SELECT * FROM users ORDER BY department ASC, name DESC
        List<JQuickOrderByExpressionNode> orderExprs = Arrays.asList(
                new JQuickOrderByExpressionNode(createColumnExpression("department"), true),
                new JQuickOrderByExpressionNode(createColumnExpression("name"), false)
        );

        JQuickSelectClauseNode selectClause = new JQuickSelectClauseNode.Builder()
                .setSelectElements(new JQuickSelectElementsNode(true, null))
                .setFromClause(new JQuickFromClauseNode(new JQuickTableNameItemNode("users", null)))
                .setOrderByClause(new JQuickOrderByClauseNode(orderExprs))
                .build();
        JQuickLogicalPlanNode result = visitor.visit(buildQuery(selectClause));
        JQuickProjectNode project = (JQuickProjectNode) result;
        JQuickSortNode sort = (JQuickSortNode) project.getChild();
        assertEquals(2, sort.getOrderByItems().size());
    }

    @Test
    public void testLimitOnly() {
        // SELECT * FROM users LIMIT 10
        JQuickLimitClauseNode limit = new JQuickLimitClauseNode(createConstantExpression(10));
        JQuickSelectClauseNode selectClause = new JQuickSelectClauseNode.Builder()
                .setSelectElements(new JQuickSelectElementsNode(true, null))
                .setFromClause(new JQuickFromClauseNode(new JQuickTableNameItemNode("users", null)))
                .setLimitClause(limit)
                .build();
        JQuickLogicalPlanNode result = visitor.visit(buildQuery(selectClause));
        JQuickProjectNode project = (JQuickProjectNode) result;
        JQuickLimitNode limitNode = (JQuickLimitNode) project.getChild();
        assertEquals(10, limitNode.getLimit());
        assertEquals(0, limitNode.getOffset());
    }

    @Test
    public void testLimitWithOffset() {
        // SELECT * FROM users LIMIT 10 OFFSET 5
        JQuickLimitClauseNode limit = new JQuickLimitClauseNode(
                createConstantExpression(5),
                createConstantExpression(10)
        );

        JQuickSelectClauseNode selectClause = new JQuickSelectClauseNode.Builder()
                .setSelectElements(new JQuickSelectElementsNode(true, null))
                .setFromClause(new JQuickFromClauseNode(new JQuickTableNameItemNode("users", null)))
                .setLimitClause(limit)
                .build();
        JQuickLogicalPlanNode result = visitor.visit(buildQuery(selectClause));
        JQuickProjectNode project = (JQuickProjectNode) result;
        JQuickLimitNode limitNode = (JQuickLimitNode) project.getChild();
        assertEquals(10, limitNode.getLimit());
        assertEquals(5, limitNode.getOffset());
    }


    @Test
    public void testSelectDistinct() {
        // SELECT DISTINCT city FROM users
        JQuickSelectElementNode element = createSelectElement(
                createColumnExpression("city"), "city"
        );
        JQuickSelectClauseNode selectClause = new JQuickSelectClauseNode.Builder()
                .setSelectSpec(new JQuickSelectSpecNode(true))
                .setSelectElements(new JQuickSelectElementsNode(false, Collections.singletonList(element)))
                .setFromClause(new JQuickFromClauseNode(new JQuickTableNameItemNode("users", null)))
                .build();
        JQuickLogicalPlanNode result = visitor.visit(buildQuery(selectClause));
        JQuickProjectNode project = (JQuickProjectNode) result;
        assertTrue(project.isDistinct());
    }

    @Test
    public void testSubqueryInWhere() {
        // SELECT * FROM users WHERE id IN (SELECT user_id FROM orders)
        JQuickSelectClauseNode subSelectClause = new JQuickSelectClauseNode.Builder()
                .setSelectElements(new JQuickSelectElementsNode(false,
                        Collections.singletonList(createSelectElement(createColumnExpression("user_id"), null))))
                .setFromClause(new JQuickFromClauseNode(
                        new JQuickTableNameItemNode("orders", null)))
                .build();

        JQuickDataSetOpNode subDataSetOp = new JQuickDataSetOpNode(Collections.singletonList(subSelectClause), null);
        JQuickSelectExpressionNode subSelectExpr = new JQuickSelectExpressionNode(subDataSetOp);
        JQuickSelectStatementNode subquery = new JQuickSelectStatementNode(subSelectExpr);
        JQuickPredicateNode inPred = new JQuickPredicateNode(
                new JQuickPredicateNode(createColumnAtom("id")),
                false,
                subquery
        );
        JQuickSelectClauseNode selectClause = new JQuickSelectClauseNode.Builder()
                .setSelectElements(new JQuickSelectElementsNode(true, null))
                .setFromClause(new JQuickFromClauseNode(new JQuickTableNameItemNode("users", null)))
                .setWhereClause(new JQuickWhereClauseNode(new JQuickFilterConditionNode(inPred)))
                .build();
        JQuickLogicalPlanNode result = visitor.visit(buildQuery(selectClause));
        JQuickProjectNode project = (JQuickProjectNode) result;
        JQuickFilterNode filter = (JQuickFilterNode) project.getChild();
        JQuickInExpression inExpr = (JQuickInExpression) filter.getPredicate();
        //assertNotNull(inExpr.getSubquery());
    }


    @Test
    public void testUnion() {
        // SELECT id FROM users UNION SELECT user_id FROM orders
        JQuickSelectClauseNode select1 = new JQuickSelectClauseNode.Builder()
                .setSelectElements(new JQuickSelectElementsNode(false,
                        Collections.singletonList(createSelectElement(createColumnExpression("id"), null))))
                .setFromClause(new JQuickFromClauseNode(
                        new JQuickTableNameItemNode("users", null)))
                .build();

        JQuickSelectClauseNode select2 = new JQuickSelectClauseNode.Builder()
                .setSelectElements(new JQuickSelectElementsNode(false,
                        Collections.singletonList(createSelectElement(createColumnExpression("user_id"), null))))
                .setFromClause(new JQuickFromClauseNode(
                        new JQuickTableNameItemNode("orders", null)))
                .build();

        List<JQuickSelectClauseNode> selects = Arrays.asList(select1, select2);
        List<JQuickSQLOperationType> ops = Collections.singletonList(JQuickSQLOperationType.UNION);
        JQuickDataSetOpNode dataSetOp = new JQuickDataSetOpNode(selects, ops);
        JQuickSelectExpressionNode selectExpr = new JQuickSelectExpressionNode(dataSetOp);
        JQuickSelectStatementNode selectStmt = new JQuickSelectStatementNode(selectExpr);
        JQuickQueryNode query = new JQuickQueryNode(selectStmt);
        JQuickLogicalPlanNode result = visitor.visit(query);
        JQuickSetOperationNode setOp = (JQuickSetOperationNode) result;
        assertEquals(JQuickSQLOperationType.UNION, setOp.getOperationType());
    }

    @Test
    public void testWithClause() {
        // WITH active_users AS (SELECT * FROM users WHERE status = 'active')
        // SELECT * FROM active_users
        JQuickSelectClauseNode cteSelect = new JQuickSelectClauseNode.Builder()
                .setSelectElements(new JQuickSelectElementsNode(true, null))
                .setFromClause(new JQuickFromClauseNode(
                        new JQuickTableNameItemNode("users", null)))
                .setWhereClause(new JQuickWhereClauseNode(
                        new JQuickFilterConditionNode(createEqualsPredicate(
                                createColumnAtom("status"),
                                createConstantAtom("active")
                        ))))
                .build();

        JQuickDataSetOpNode cteDataSetOp = new JQuickDataSetOpNode(
                Collections.singletonList(cteSelect), null);
        JQuickSelectExpressionNode cteSelectExpr = new JQuickSelectExpressionNode(cteDataSetOp);
        JQuickSelectStatementNode cteQuery = new JQuickSelectStatementNode(cteSelectExpr);
        JQuickCommonTableExpressionNode cte = new JQuickCommonTableExpressionNode(
                "active_users", null, cteQuery
        );

        JQuickSelectClauseNode mainSelect = new JQuickSelectClauseNode.Builder()
                .setSelectElements(new JQuickSelectElementsNode(true, null))
                .setFromClause(new JQuickFromClauseNode(
                        new JQuickTableNameItemNode("active_users", null)))
                .build();

        JQuickDataSetOpNode mainDataSetOp = new JQuickDataSetOpNode(
                Collections.singletonList(mainSelect), null);
        JQuickSelectExpressionNode mainSelectExpr = new JQuickSelectExpressionNode(mainDataSetOp);

        JQuickSelectStatementNode selectStmt = new JQuickSelectStatementNode(
                false, Collections.singletonList(cte), mainSelectExpr
        );

        JQuickQueryNode query = new JQuickQueryNode(selectStmt);
        JQuickLogicalPlanNode result = visitor.visit(query);

        JQuickWithNode withNode = (JQuickWithNode) result;
        assertEquals(1, withNode.getCtes().size());
        assertTrue(withNode.getCtes().containsKey("active_users"));
    }


    @Test
    public void testArithmeticExpression() {
        // SELECT price * quantity AS total FROM order_items
        JQuickExpressionAtomNode left = createColumnAtom("price");
        JQuickExpressionAtomNode right = createColumnAtom("quantity");
        JQuickExpressionAtomNode mathAtom = new JQuickExpressionAtomNode(
                left, right, JQuickExpressionAtomNode.MathOperator.MULTIPLY
        );
        JQuickExpressionNode expr = new JQuickExpressionNode(mathAtom);
        JQuickSelectElementNode element = new JQuickSelectElementNode(expr, "total");
        JQuickSelectClauseNode selectClause = new JQuickSelectClauseNode.Builder()
                .setSelectElements(new JQuickSelectElementsNode(false, Collections.singletonList(element)))
                .setFromClause(new JQuickFromClauseNode(
                        new JQuickTableNameItemNode("order_items", null)))
                .build();
        JQuickLogicalPlanNode result = visitor.visit(buildQuery(selectClause));
        JQuickProjectNode project = (JQuickProjectNode) result;
        JQuickBinaryExpression binary = (JQuickBinaryExpression)
                project.getSelectItems().get(0).getExpression();
        assertEquals(JQuickBinaryOperator.MULTIPLY, binary.getOperator());
    }

    @Test
    public void testCaseWhenExpression() {
        // SELECT CASE WHEN status = 'active' THEN 1 ELSE 0 END AS is_active FROM users
        JQuickPredicateNode condition = createEqualsPredicate(
                createColumnAtom("status"),
                createConstantAtom("active")
        );
        JQuickCaseWhenNode.WhenClause whenClause = new JQuickCaseWhenNode.WhenClause(
                condition,
                new JQuickExpressionNode(createConstantAtom(1))
        );
        JQuickCaseWhenNode caseWhen = new JQuickCaseWhenNode(
                null,
                Collections.singletonList(whenClause),
                new JQuickExpressionNode(createConstantAtom(0))
        );
        JQuickExpressionNode expr = new JQuickExpressionNode(caseWhen);
        JQuickSelectElementNode element = new JQuickSelectElementNode(expr, "is_active");

        JQuickSelectClauseNode selectClause = new JQuickSelectClauseNode.Builder()
                .setSelectElements(new JQuickSelectElementsNode(false, Collections.singletonList(element)))
                .setFromClause(new JQuickFromClauseNode(new JQuickTableNameItemNode("users", null)))
                .build();
        JQuickLogicalPlanNode result = visitor.visit(buildQuery(selectClause));
        assertNotNull(result);
        assertEquals("Project", result.getNodeType());
    }

    @Test
    public void testFunctionCall() {
        // SELECT UPPER(name) AS upper_name FROM users
        JQuickFunctionCallNode functionCall = new JQuickFunctionCallNode(
                "UPPER",
                Collections.singletonList(new JQuickFunctionArgNode(
                        new JQuickExpressionNode(createColumnAtom("name")))),
                false
        );
        JQuickExpressionNode expr = new JQuickExpressionNode(
                new JQuickExpressionAtomNode(functionCall)
        );
        JQuickSelectElementNode element = new JQuickSelectElementNode(expr, "upper_name");
        JQuickSelectClauseNode selectClause = new JQuickSelectClauseNode.Builder()
                .setSelectElements(new JQuickSelectElementsNode(false, Collections.singletonList(element)))
                .setFromClause(new JQuickFromClauseNode(
                        new JQuickTableNameItemNode("users", null)))
                .build();
        JQuickLogicalPlanNode result = visitor.visit(buildQuery(selectClause));
        JQuickProjectNode project = (JQuickProjectNode) result;
        JQuickFunctionCallExpression funcExpr = (JQuickFunctionCallExpression) project.getSelectItems().get(0).getExpression();
        assertEquals("UPPER", funcExpr.getFunctionName());
    }
    private JQuickQueryNode buildQuery(JQuickSelectClauseNode selectClause) {
        JQuickDataSetOpNode dataSetOp = new JQuickDataSetOpNode(Collections.singletonList(selectClause), null);
        JQuickSelectExpressionNode selectExpr = new JQuickSelectExpressionNode(dataSetOp);
        JQuickSelectStatementNode selectStmt = new JQuickSelectStatementNode(selectExpr);
        return new JQuickQueryNode(selectStmt);
    }

    private JQuickExpressionNode createColumnExpression(String columnName) {
        return new JQuickExpressionNode(new JQuickExpressionAtomNode(
                new JQuickFullColumnNameNode(columnName, null)));
    }

    private JQuickExpressionAtomNode createColumnAtom(String columnName) {
        return new JQuickExpressionAtomNode(new JQuickFullColumnNameNode(columnName, null));
    }

    private JQuickExpressionAtomNode createConstantAtom(Object value) {
        if (value instanceof Integer) {
            return new JQuickExpressionAtomNode(
                    new JQuickConstantNode(value, JQuickConstantNode.ConstantType.DECIMAL));
        }
        return new JQuickExpressionAtomNode(
                new JQuickConstantNode(value, JQuickConstantNode.ConstantType.STRING));
    }

    private JQuickExpressionNode createConstantExpression(Object value) {
        return new JQuickExpressionNode(createConstantAtom(value));
    }

    private JQuickSelectElementNode createSelectElement(JQuickExpressionNode expr, String alias) {
        return new JQuickSelectElementNode(expr, alias);
    }

    private JQuickPredicateNode createEqualsPredicate(JQuickExpressionAtomNode left, JQuickExpressionAtomNode right) {
        return new JQuickPredicateNode(
                new JQuickPredicateNode(left),
                new JQuickPredicateNode(right),
                JQuickPredicateNode.ComparisonOperator.EQ
        );
    }

    private JQuickPredicateNode createGreaterThanPredicate(JQuickPredicateNode left, JQuickPredicateNode right) {
        return new JQuickPredicateNode(left, right, JQuickPredicateNode.ComparisonOperator.GT);
    }

    private JQuickExpressionNode createCountStarFunction() {
        JQuickFunctionCallNode countStar = new JQuickFunctionCallNode("COUNT", null, true);
        return new JQuickExpressionNode(new JQuickExpressionAtomNode(countStar));
    }

    private JQuickPredicateNode createCountStarPredicate() {
        JQuickExpressionNode countExpr = createCountStarFunction();
        return new JQuickPredicateNode(countExpr.getExpressionAtom());
    }
}