package com.github.paohaijiao.ast2logic.selectElement;

import com.github.paohaijiao.ast.*;
import com.github.paohaijiao.ast2logic.JQuickASTToLogicalPlanVisitor;
import com.github.paohaijiao.enums.JQuickSQLOperationType;
import com.github.paohaijiao.logic.JQuickLogicalPlanNode;
import com.github.paohaijiao.logic.domain.JQuickProjectNode;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class JQuickSelectElementTest {

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
    private JQuickExpressionNode buildColumnExpr(String columnName, String tableAlias) {
        JQuickFullColumnNameNode column = new JQuickFullColumnNameNode(columnName, tableAlias);
        JQuickExpressionAtomNode atom = new JQuickExpressionAtomNode(column);
        return new JQuickExpressionNode(atom);
    }
    private JQuickExpressionNode buildColumnExpr(String columnName) {
        return buildColumnExpr(columnName, null);
    }

    @Test
    public void testSelectMultipleColumns1() {
        // SELECT id, name, email FROM users
        List<JQuickSelectElementNode> elements = new ArrayList<>();
        elements.add(new JQuickSelectElementNode(buildColumnExpr("id"), "a"));
        elements.add(new JQuickSelectElementNode(buildColumnExpr("name"), "b"));
        elements.add(new JQuickSelectElementNode(buildColumnExpr("email"), "c"));
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
    }


}
