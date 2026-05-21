package com.github.paohaijiao.ast2logic.query;

import com.github.paohaijiao.ast.*;
import com.github.paohaijiao.ast2logic.JQuickASTToLogicalPlanVisitor;
import com.github.paohaijiao.logic.JQuickLogicalPlanNode;
import com.github.paohaijiao.logic.domain.JQuickProjectNode;
import com.github.paohaijiao.logic.domain.JQuickTableScanNode;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.*;

public class JQuickSelectClauseTest {


    private JQuickASTToLogicalPlanVisitor visitor;

    @Before
    public void setUp() {
        visitor = new JQuickASTToLogicalPlanVisitor();
    }
    @Test
    public void testSimpleSelectStar() {
        // SELECT * FROM users
        JQuickTableNameItemNode tableItem = new JQuickTableNameItemNode("users", null);
        JQuickFromClauseNode fromClause = new JQuickFromClauseNode(tableItem);
        JQuickSelectElementsNode selectElements = new JQuickSelectElementsNode(true, null);
        JQuickSelectClauseNode selectClause = new JQuickSelectClauseNode.Builder()
                .setSelectElements(selectElements)
                .setFromClause(fromClause)
                .build();
        JQuickDataSetOpNode dataSetOp = new JQuickDataSetOpNode(Collections.singletonList(selectClause), Collections.emptyList());
        JQuickSelectExpressionNode selectExpr = new JQuickSelectExpressionNode(dataSetOp);
        JQuickSelectStatementNode selectStmt = new JQuickSelectStatementNode(selectExpr);
        JQuickQueryNode query = new JQuickQueryNode(selectStmt);
        JQuickLogicalPlanNode result = visitor.visit(query);
        assertNotNull(result);
        assertEquals("Project", result.getNodeType());
        JQuickProjectNode project = (JQuickProjectNode) result;
        assertTrue(project.getSelectItems().get(0).isStar());
        JQuickTableScanNode scan = (JQuickTableScanNode) project.getChild();
        assertEquals("users", scan.getTableName());
        assertNull(scan.getAlias());
    }



}
