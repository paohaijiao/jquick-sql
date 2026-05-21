package com.github.paohaijiao.ast2logic.selectClause;
import com.github.paohaijiao.ast.*;
import com.github.paohaijiao.ast2logic.JQuickASTToLogicalPlanVisitor;
import com.github.paohaijiao.enums.JQuickBinaryOperator;
import com.github.paohaijiao.enums.JQuickJoinType;
import com.github.paohaijiao.enums.JQuickSQLOperationType;
import com.github.paohaijiao.expression.domain.JQuickBinaryExpression;
import com.github.paohaijiao.logic.JQuickLogicalPlanNode;
import com.github.paohaijiao.logic.domain.*;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class JQuickSelectClauseTest {

    private JQuickASTToLogicalPlanVisitor visitor;

    @Before
    public void setUp() {
        visitor = new JQuickASTToLogicalPlanVisitor();
    }
    /**
     * 构建简单的列表达式
     */
    private JQuickExpressionNode buildColumnExpr(String columnName, String tableAlias) {
        JQuickFullColumnNameNode column = new JQuickFullColumnNameNode(columnName, tableAlias);
        JQuickExpressionAtomNode atom = new JQuickExpressionAtomNode(column);
        return new JQuickExpressionNode(atom);
    }
    /**
     * 构建完整的 Query
     */
    private JQuickQueryNode buildQuery(JQuickSelectClauseNode selectClause) {
        JQuickDataSetOpNode dataSetOp = new JQuickDataSetOpNode(Collections.singletonList(selectClause), Collections.emptyList());
        JQuickSelectExpressionNode selectExpr = new JQuickSelectExpressionNode(dataSetOp);
        JQuickSelectStatementNode selectStmt = new JQuickSelectStatementNode(selectExpr);
        return new JQuickQueryNode(selectStmt);
    }
    /**
     * 构建表达式原子
     */
    private JQuickExpressionAtomNode buildColumnAtom(String columnName, String tableAlias) {
        JQuickFullColumnNameNode column = new JQuickFullColumnNameNode(columnName, tableAlias);
        return new JQuickExpressionAtomNode(column);
    }
    private JQuickExpressionAtomNode buildConstantAtom(Object value, JQuickConstantNode.ConstantType type) {
        JQuickConstantNode constant = new JQuickConstantNode(value, type);
        return new JQuickExpressionAtomNode(constant);
    }
    /**
     * 构建比较谓词
     * 正确的构建方式：先将 ExpressionAtom 包装成 Predicate，再进行比较
     */
    private JQuickPredicateNode buildComparisonPredicate(JQuickExpressionAtomNode leftAtom, JQuickExpressionAtomNode rightAtom, JQuickPredicateNode.ComparisonOperator op) {
        JQuickPredicateNode leftPred = new JQuickPredicateNode(leftAtom);
        JQuickPredicateNode rightPred = new JQuickPredicateNode(rightAtom);
        return new JQuickPredicateNode(leftPred, rightPred, op);
    }
    private JQuickExpressionNode buildConstantExpr(Object value, JQuickConstantNode.ConstantType type) {
        JQuickConstantNode constant = new JQuickConstantNode(value, type);
        JQuickExpressionAtomNode atom = new JQuickExpressionAtomNode(constant);
        return new JQuickExpressionNode(atom);
    }
    @Test
    public void testSimpleSelectStar() {
        // SELECT * FROM users
        JQuickTableNameItemNode tableItem = new JQuickTableNameItemNode("users", null);
        JQuickFromClauseNode fromClause = new JQuickFromClauseNode(tableItem);
        JQuickSelectElementsNode selectElements = new JQuickSelectElementsNode(true, null);
        JQuickSelectClauseNode selectClause = new JQuickSelectClauseNode.Builder().setSelectElements(selectElements).setFromClause(fromClause).build();
        JQuickQueryNode query = buildQuery(selectClause);
        JQuickLogicalPlanNode result = visitor.visit(query);
        assertNotNull(result);
        assertEquals("Project", result.getNodeType());
        JQuickProjectNode project = (JQuickProjectNode) result;
        assertTrue(project.getSelectItems().get(0).isStar());
        JQuickTableScanNode scan = (JQuickTableScanNode) project.getChild();
        assertEquals("users", scan.getTableName());
        assertNull(scan.getAlias());
    }
    @Test
    public void testSelectColumns() {
        // SELECT id, name, email FROM users
        List<JQuickSelectElementNode> elements = new ArrayList<JQuickSelectElementNode>();
        for (String col : Arrays.asList("id", "name", "email")) {
            elements.add(new JQuickSelectElementNode(buildColumnExpr(col, null), null));
        }
        JQuickSelectElementsNode selectElements = new JQuickSelectElementsNode(false, elements);
        JQuickTableNameItemNode tableItem = new JQuickTableNameItemNode("users", null);
        JQuickFromClauseNode fromClause = new JQuickFromClauseNode(tableItem);
        JQuickSelectClauseNode selectClause = new JQuickSelectClauseNode.Builder()
                .setSelectElements(selectElements)
                .setFromClause(fromClause)
                .build();
        JQuickQueryNode query = buildQuery(selectClause);
        JQuickLogicalPlanNode result = visitor.visit(query);
        JQuickProjectNode project = (JQuickProjectNode) result;
        assertEquals(3, project.getSelectItems().size());
        assertFalse(project.getSelectItems().get(0).isStar());
        JQuickTableScanNode scan = (JQuickTableScanNode) project.getChild();
        assertEquals("users", scan.getTableName());
    }
    @Test
    public void testSelectWithAlias() {
        // SELECT id AS user_id, name AS user_name FROM users
        List<JQuickSelectElementNode> elements = new ArrayList<JQuickSelectElementNode>();
        elements.add(new JQuickSelectElementNode(buildColumnExpr("id", null), "user_id"));
        elements.add(new JQuickSelectElementNode(buildColumnExpr("name", null), "user_name"));
        JQuickSelectElementsNode selectElements = new JQuickSelectElementsNode(false, elements);
        JQuickTableNameItemNode tableItem = new JQuickTableNameItemNode("users", null);
        JQuickFromClauseNode fromClause = new JQuickFromClauseNode(tableItem);
        JQuickSelectClauseNode selectClause = new JQuickSelectClauseNode.Builder().setSelectElements(selectElements).setFromClause(fromClause).build();
        JQuickQueryNode query = buildQuery(selectClause);
        JQuickLogicalPlanNode result = visitor.visit(query);
        JQuickProjectNode project = (JQuickProjectNode) result;
        assertEquals(2, project.getSelectItems().size());
        assertEquals("user_id", project.getSelectItems().get(0).getAlias());
        assertEquals("user_name", project.getSelectItems().get(1).getAlias());
    }
    @Test
    public void testInnerJoin() {
        // SELECT * FROM users u INNER JOIN orders o ON u.id = o.user_id
        JQuickTableNameItemNode leftTable = new JQuickTableNameItemNode("users", "u");
        JQuickFromClauseNode fromClause = new JQuickFromClauseNode(leftTable);
        JQuickTableNameItemNode rightTable = new JQuickTableNameItemNode("orders", "o");
        JQuickFullColumnNameNode leftColumn = new JQuickFullColumnNameNode("id", "u");
        JQuickFullColumnNameNode rightColumn = new JQuickFullColumnNameNode("user_id", "o");
        JQuickJoinClauseNode joinClause = new JQuickJoinClauseNode(JQuickJoinType.INNER, rightTable, leftColumn, rightColumn);
        JQuickSelectElementsNode selectElements = new JQuickSelectElementsNode(true, null);
        JQuickSelectClauseNode selectClause = new JQuickSelectClauseNode.Builder()
                .setSelectElements(selectElements)
                .setFromClause(fromClause)
                .addJoinClause(joinClause)
                .build();
        JQuickQueryNode query = buildQuery(selectClause);
        JQuickLogicalPlanNode result = visitor.visit(query);
        JQuickProjectNode project = (JQuickProjectNode) result;
        JQuickJoinNode join = (JQuickJoinNode) project.getChild();
        assertEquals(JQuickJoinType.INNER, join.getJoinType());
        JQuickTableScanNode left = (JQuickTableScanNode) join.getLeft();
        JQuickTableScanNode right = (JQuickTableScanNode) join.getRight();
        assertEquals("users", left.getTableName());
        assertEquals("u", left.getAlias());
        assertEquals("orders", right.getTableName());
        assertEquals("o", right.getAlias());
    }
    @Test
    public void testSelectWithWhereEquals() {
        // SELECT * FROM users WHERE id = 1
        JQuickExpressionAtomNode idAtom = buildColumnAtom("id", null);
        JQuickExpressionAtomNode constAtom = buildConstantAtom(1, JQuickConstantNode.ConstantType.DECIMAL);
        JQuickPredicateNode predicate = buildComparisonPredicate(idAtom, constAtom, JQuickPredicateNode.ComparisonOperator.EQ);
        JQuickFilterConditionNode filterCondition = new JQuickFilterConditionNode(predicate);
        JQuickWhereClauseNode whereClause = new JQuickWhereClauseNode(filterCondition);
        JQuickTableNameItemNode tableItem = new JQuickTableNameItemNode("users", null);
        JQuickFromClauseNode fromClause = new JQuickFromClauseNode(tableItem);
        JQuickSelectElementsNode selectElements = new JQuickSelectElementsNode(true, null);
        JQuickSelectClauseNode selectClause = new JQuickSelectClauseNode.Builder()
                .setSelectElements(selectElements)
                .setFromClause(fromClause)
                .setWhereClause(whereClause)
                .build();
        JQuickQueryNode query = buildQuery(selectClause);
        JQuickLogicalPlanNode result = visitor.visit(query);
        JQuickProjectNode project = (JQuickProjectNode) result;
        JQuickFilterNode filter = (JQuickFilterNode) project.getChild();
        JQuickTableScanNode scan = (JQuickTableScanNode) filter.getChild();
        assertEquals("users", scan.getTableName());
        JQuickBinaryExpression predicateExpr = (JQuickBinaryExpression) filter.getPredicate();
        assertEquals(JQuickBinaryOperator.EQ, predicateExpr.getOperator());
    }
    @Test
    public void testSelectWithWhereAnd() {
        // SELECT * FROM users WHERE id > 1 AND status = 'active'
        // 构建 id > 1
        JQuickExpressionAtomNode idAtom = buildColumnAtom("id", null);
        JQuickExpressionAtomNode constAtom1 = buildConstantAtom(1, JQuickConstantNode.ConstantType.DECIMAL);
        JQuickPredicateNode pred1 = buildComparisonPredicate(idAtom, constAtom1, JQuickPredicateNode.ComparisonOperator.GT);
        // 构建 status = 'active'
        JQuickExpressionAtomNode statusAtom = buildColumnAtom("status", null);
        JQuickExpressionAtomNode constAtom2 = buildConstantAtom("active", JQuickConstantNode.ConstantType.STRING);
        JQuickPredicateNode pred2 = buildComparisonPredicate(statusAtom, constAtom2, JQuickPredicateNode.ComparisonOperator.EQ);
        // 构建 AND
        JQuickFilterConditionNode cond1 = new JQuickFilterConditionNode(pred1);
        JQuickFilterConditionNode cond2 = new JQuickFilterConditionNode(pred2);
        JQuickFilterConditionNode andCondition = new JQuickFilterConditionNode(cond1, cond2, JQuickFilterConditionNode.LogicalOperator.AND);
        JQuickWhereClauseNode whereClause = new JQuickWhereClauseNode(andCondition);
        JQuickTableNameItemNode tableItem = new JQuickTableNameItemNode("users", null);
        JQuickFromClauseNode fromClause = new JQuickFromClauseNode(tableItem);
        JQuickSelectElementsNode selectElements = new JQuickSelectElementsNode(true, null);
        JQuickSelectClauseNode selectClause = new JQuickSelectClauseNode.Builder()
                .setSelectElements(selectElements)
                .setFromClause(fromClause)
                .setWhereClause(whereClause)
                .build();

        JQuickQueryNode query = buildQuery(selectClause);
        JQuickLogicalPlanNode result = visitor.visit(query);
        JQuickProjectNode project = (JQuickProjectNode) result;
        JQuickFilterNode filter = (JQuickFilterNode) project.getChild();
        JQuickBinaryExpression andExpr = (JQuickBinaryExpression) filter.getPredicate();
        assertEquals(JQuickBinaryOperator.AND, andExpr.getOperator());
    }
    @Test
    public void testGroupByWithCount() {
        // SELECT category, COUNT(*) FROM products GROUP BY category
        List<JQuickSelectElementNode> elements = new ArrayList<>();
        elements.add(new JQuickSelectElementNode(buildColumnExpr("category", null), null));
        JQuickFunctionCallNode countFunc = new JQuickFunctionCallNode("COUNT", null, true);
        JQuickExpressionAtomNode countAtom = new JQuickExpressionAtomNode(countFunc);
        JQuickExpressionNode countExpr = new JQuickExpressionNode(countAtom);
        elements.add(new JQuickSelectElementNode(countExpr, "cnt"));
        JQuickSelectElementsNode selectElements = new JQuickSelectElementsNode(false, elements);
        JQuickTableNameItemNode tableItem = new JQuickTableNameItemNode("products", null);
        JQuickFromClauseNode fromClause = new JQuickFromClauseNode(tableItem);
        JQuickExpressionsNode groupExpressions = new JQuickExpressionsNode(
                Collections.singletonList(buildColumnExpr("category", null)));
        JQuickGroupByClauseNode groupByClause = new JQuickGroupByClauseNode(groupExpressions);
        JQuickSelectClauseNode selectClause = new JQuickSelectClauseNode.Builder()
                .setSelectElements(selectElements)
                .setFromClause(fromClause)
                .setGroupByClause(groupByClause)
                .build();
        JQuickQueryNode query = buildQuery(selectClause);
        JQuickLogicalPlanNode result = visitor.visit(query);
        JQuickProjectNode project = (JQuickProjectNode) result;
        JQuickGroupByNode groupBy = (JQuickGroupByNode) project.getChild();
        assertEquals(1, groupBy.getGroupKeys().size());
        assertEquals(1, groupBy.getAggregateItems().size());
        JQuickGroupByNode.AggregateItem agg = groupBy.getAggregateItems().get(0);
        assertEquals("COUNT", agg.getFunctionName().toUpperCase());
    }
    @Test
    public void testHaving() {
        // SELECT category, COUNT(*) as cnt FROM products GROUP BY category HAVING COUNT(*) > 5
        List<JQuickSelectElementNode> elements = new ArrayList<>();
        elements.add(new JQuickSelectElementNode(buildColumnExpr("category", null), null));
        JQuickFunctionCallNode countFunc = new JQuickFunctionCallNode("COUNT", null, true);
        JQuickExpressionAtomNode countAtom = new JQuickExpressionAtomNode(countFunc);
        JQuickExpressionNode countExpr = new JQuickExpressionNode(countAtom);
        elements.add(new JQuickSelectElementNode(countExpr, "cnt"));
        JQuickSelectElementsNode selectElements = new JQuickSelectElementsNode(false, elements);
        JQuickTableNameItemNode tableItem = new JQuickTableNameItemNode("products", null);
        JQuickFromClauseNode fromClause = new JQuickFromClauseNode(tableItem);
        JQuickExpressionsNode groupExpressions = new JQuickExpressionsNode(Collections.singletonList(buildColumnExpr("category", null)));
        JQuickGroupByClauseNode groupByClause = new JQuickGroupByClauseNode(groupExpressions);
        // HAVING COUNT(*) > 5
        JQuickPredicateNode havingPred = new JQuickPredicateNode(countAtom);
        JQuickPredicateNode fivePred = new JQuickPredicateNode(buildConstantAtom(5, JQuickConstantNode.ConstantType.DECIMAL));
        JQuickPredicateNode havingCondition = new JQuickPredicateNode(havingPred, fivePred, JQuickPredicateNode.ComparisonOperator.GT);
        JQuickFilterConditionNode havingFilter = new JQuickFilterConditionNode(havingCondition);
        JQuickHavingClauseNode havingClause = new JQuickHavingClauseNode(havingFilter);
        JQuickSelectClauseNode selectClause = new JQuickSelectClauseNode.Builder()
                .setSelectElements(selectElements)
                .setFromClause(fromClause)
                .setGroupByClause(groupByClause)
                .setHavingClause(havingClause)
                .build();
        JQuickQueryNode query = buildQuery(selectClause);
        JQuickLogicalPlanNode result = visitor.visit(query);
        JQuickProjectNode project = (JQuickProjectNode) result;
        JQuickGroupByNode groupBy = (JQuickGroupByNode) project.getChild();
        assertNotNull(groupBy.getHavingCondition());
        JQuickBinaryExpression having = (JQuickBinaryExpression) groupBy.getHavingCondition();
        assertEquals(JQuickBinaryOperator.GT, having.getOperator());
    }
    @Test
    public void testOrderByAsc() {
        // SELECT * FROM users ORDER BY name ASC
        JQuickTableNameItemNode tableItem = new JQuickTableNameItemNode("users", null);
        JQuickFromClauseNode fromClause = new JQuickFromClauseNode(tableItem);
        JQuickSelectElementsNode selectElements = new JQuickSelectElementsNode(true, null);
        JQuickOrderByExpressionNode orderByExpr = new JQuickOrderByExpressionNode(buildColumnExpr("name", null), true);
        JQuickOrderByClauseNode orderByClause = new JQuickOrderByClauseNode(Collections.singletonList(orderByExpr));
        JQuickSelectClauseNode selectClause = new JQuickSelectClauseNode.Builder()
                .setSelectElements(selectElements)
                .setFromClause(fromClause)
                .setOrderByClause(orderByClause)
                .build();
        JQuickQueryNode query = buildQuery(selectClause);
        JQuickLogicalPlanNode result = visitor.visit(query);
        System.out.println(result);
    }
    @Test
    public void testLimit() {
        // SELECT * FROM users LIMIT 10
        JQuickTableNameItemNode tableItem = new JQuickTableNameItemNode("users", null);
        JQuickFromClauseNode fromClause = new JQuickFromClauseNode(tableItem);
        JQuickSelectElementsNode selectElements = new JQuickSelectElementsNode(true, null);
        JQuickLimitClauseNode limitClause = new JQuickLimitClauseNode(buildConstantExpr(10, JQuickConstantNode.ConstantType.DECIMAL));
        JQuickSelectClauseNode selectClause = new JQuickSelectClauseNode.Builder()
                .setSelectElements(selectElements)
                .setFromClause(fromClause)
                .setLimitClause(limitClause)
                .build();
        JQuickQueryNode query = buildQuery(selectClause);
        JQuickLogicalPlanNode result = visitor.visit(query);
        JQuickLimitNode limit = (JQuickLimitNode) result;
        assertEquals(10, limit.getLimit());
        assertEquals(0, limit.getOffset());
    }


}
