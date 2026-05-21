package com.github.paohaijiao.ast2logic.joinType;

import com.github.paohaijiao.ast.*;
import com.github.paohaijiao.ast2logic.JQuickASTToLogicalPlanVisitor;
import com.github.paohaijiao.enums.JQuickJoinType;
import com.github.paohaijiao.enums.JQuickSQLOperationType;
import com.github.paohaijiao.logic.JQuickLogicalPlanNode;
import com.github.paohaijiao.logic.domain.JQuickJoinNode;
import com.github.paohaijiao.logic.domain.JQuickProjectNode;
import com.github.paohaijiao.logic.domain.JQuickTableScanNode;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class JQuickJoinTypeTest {
    private JQuickASTToLogicalPlanVisitor visitor;

    @Before
    public void setUp() {
        visitor = new JQuickASTToLogicalPlanVisitor();
    }
    /**
     * 构建 JOIN 查询
     */
    private JQuickSelectClauseNode buildJoinQuery(String leftTable, String leftAlias, String rightTable, String rightAlias, JQuickJoinType joinType, String leftColumn, String rightColumn) {
        JQuickTableNameItemNode leftTableItem = new JQuickTableNameItemNode(leftTable, leftAlias);
        JQuickFromClauseNode fromClause = new JQuickFromClauseNode(leftTableItem);
        JQuickTableNameItemNode rightTableItem = new JQuickTableNameItemNode(rightTable, rightAlias);
        JQuickFullColumnNameNode leftCol = new JQuickFullColumnNameNode(leftColumn, leftAlias);
        JQuickFullColumnNameNode rightCol = new JQuickFullColumnNameNode(rightColumn, rightAlias);
        JQuickJoinClauseNode joinClause = new JQuickJoinClauseNode(joinType, rightTableItem, leftCol, rightCol);
        JQuickSelectElementsNode selectElements = new JQuickSelectElementsNode(true, null);
        return new JQuickSelectClauseNode.Builder().setSelectElements(selectElements).setFromClause(fromClause).addJoinClause(joinClause).build();
    }

    private JQuickQueryNode buildQuery(JQuickSelectClauseNode selectClause) {
        JQuickDataSetOpNode dataSetOp = new JQuickDataSetOpNode(Collections.singletonList(selectClause), Collections.<JQuickSQLOperationType>emptyList());
        JQuickSelectExpressionNode selectExpr = new JQuickSelectExpressionNode(dataSetOp);
        JQuickSelectStatementNode selectStmt = new JQuickSelectStatementNode(selectExpr);
        return new JQuickQueryNode(selectStmt);
    }
    @Test
    public void testInnerJoin() {
        // SELECT * FROM users u INNER JOIN orders o ON u.id = o.user_id
        JQuickSelectClauseNode selectClause = buildJoinQuery("users", "u", "orders", "o", JQuickJoinType.INNER, "id", "user_id");
        JQuickQueryNode query = buildQuery(selectClause);
        JQuickLogicalPlanNode result = visitor.visit(query);
        JQuickProjectNode project = (JQuickProjectNode) result;
        JQuickJoinNode join = (JQuickJoinNode) project.getChild();
        assertEquals(JQuickJoinType.INNER, join.getJoinType());
        JQuickTableScanNode left = (JQuickTableScanNode) join.getLeft();
        JQuickTableScanNode right = (JQuickTableScanNode) join.getRight();
        assertEquals("users", left.getTableName());
        assertEquals("orders", right.getTableName());
    }
    @Test
    public void testCrossJoin() {
        // SELECT * FROM users u CROSS JOIN orders o
        JQuickTableNameItemNode leftTable = new JQuickTableNameItemNode("users", "u");
        JQuickFromClauseNode fromClause = new JQuickFromClauseNode(leftTable);
        JQuickTableNameItemNode rightTable = new JQuickTableNameItemNode("orders", "o");
        // CROSS JOIN 没有 ON 条件
        JQuickJoinClauseNode joinClause = new JQuickJoinClauseNode(JQuickJoinType.CROSS, rightTable, null, null);
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
        assertEquals(JQuickJoinType.CROSS, join.getJoinType());
        assertNull(join.getCondition());
    }
    @Test
    public void testLeftJoin() {
        // SELECT * FROM users u LEFT JOIN orders o ON u.id = o.user_id
        JQuickSelectClauseNode selectClause = buildJoinQuery(
                "users", "u", "orders", "o",
                JQuickJoinType.LEFT, "id", "user_id");
        JQuickQueryNode query = buildQuery(selectClause);
        JQuickLogicalPlanNode result = visitor.visit(query);
        JQuickProjectNode project = (JQuickProjectNode) result;
        JQuickJoinNode join = (JQuickJoinNode) project.getChild();
        assertEquals(JQuickJoinType.LEFT, join.getJoinType());
    }
    @Test
    public void testRightJoin() {
        // SELECT * FROM users u RIGHT JOIN orders o ON u.id = o.user_id
        JQuickSelectClauseNode selectClause = buildJoinQuery(
                "users", "u", "orders", "o",
                JQuickJoinType.RIGHT, "id", "user_id");
        JQuickQueryNode query = buildQuery(selectClause);
        JQuickLogicalPlanNode result = visitor.visit(query);
        JQuickProjectNode project = (JQuickProjectNode) result;
        JQuickJoinNode join = (JQuickJoinNode) project.getChild();
        assertEquals(JQuickJoinType.RIGHT, join.getJoinType());
    }
    @Test
    public void testFullJoin() {
        // SELECT * FROM users u FULL JOIN orders o ON u.id = o.user_id
        JQuickSelectClauseNode selectClause = buildJoinQuery(
                "users", "u", "orders", "o",
                JQuickJoinType.FULL, "id", "user_id");
        JQuickQueryNode query = buildQuery(selectClause);
        JQuickLogicalPlanNode result = visitor.visit(query);
        JQuickProjectNode project = (JQuickProjectNode) result;
        JQuickJoinNode join = (JQuickJoinNode) project.getChild();
        assertEquals(JQuickJoinType.FULL, join.getJoinType());
    }
    @Test
    public void testNaturalJoin() {
        // SELECT * FROM employees NATURAL JOIN departments
        // NATURAL JOIN 默认是 INNER JOIN
        JQuickTableNameItemNode leftTable = new JQuickTableNameItemNode("employees", null);
        JQuickFromClauseNode fromClause = new JQuickFromClauseNode(leftTable);
        JQuickTableNameItemNode rightTable = new JQuickTableNameItemNode("departments", null);
        JQuickJoinClauseNode joinClause = new JQuickJoinClauseNode(
                JQuickJoinType.NATURAL, rightTable, null, null);
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
        assertEquals(JQuickJoinType.NATURAL, join.getJoinType());
    }

}
