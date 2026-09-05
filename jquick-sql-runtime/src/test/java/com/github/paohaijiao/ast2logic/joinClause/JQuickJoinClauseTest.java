package com.github.paohaijiao.ast2logic.joinClause;

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

public class JQuickJoinClauseTest {

    private JQuickASTToLogicalPlanVisitor visitor;

    @Before
    public void setUp() {
        visitor = new JQuickASTToLogicalPlanVisitor();
    }
    private JQuickQueryNode buildQuery(JQuickSelectClauseNode selectClause) {
        JQuickDataSetOpNode dataSetOp = new JQuickDataSetOpNode(
                Collections.singletonList(selectClause),
                Collections.<JQuickSQLOperationType>emptyList()
        );
        JQuickSelectExpressionNode selectExpr = new JQuickSelectExpressionNode(dataSetOp);
        JQuickSelectStatementNode selectStmt = new JQuickSelectStatementNode(selectExpr);
        return new JQuickQueryNode(selectStmt);
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


}
