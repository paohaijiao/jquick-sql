package com.github.paohaijiao.ast2logic.tableNameItem;

import com.github.paohaijiao.ast.*;
import com.github.paohaijiao.ast2logic.JQuickASTToLogicalPlanVisitor;
import com.github.paohaijiao.enums.JQuickSQLOperationType;
import com.github.paohaijiao.logic.JQuickLogicalPlanNode;
import com.github.paohaijiao.logic.domain.JQuickProjectNode;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class JQuickTableNameItemTest {
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
    private JQuickSelectClauseNode createSimpleSelectClause(String tableName, List<String> columns) {
        List<JQuickSelectElementNode> elements = new ArrayList<>();
        for (String col : columns) {
            elements.add(new JQuickSelectElementNode(buildColumnExpr(col), null));
        }
        JQuickSelectElementsNode selectElements = new JQuickSelectElementsNode(false, elements);
        JQuickTableNameItemNode tableItem = new JQuickTableNameItemNode(tableName, null);
        JQuickFromClauseNode fromClause = new JQuickFromClauseNode(tableItem);
        return new JQuickSelectClauseNode.Builder().setSelectElements(selectElements).setFromClause(fromClause).build();
    }

    /**
     * 构建子查询 SelectExpression
     */
    private JQuickSelectExpressionNode buildSubquerySelectExpression(JQuickSelectClauseNode selectClause) {
        JQuickDataSetOpNode dataSetOp = new JQuickDataSetOpNode(
                Collections.singletonList(selectClause),
                Collections.<JQuickSQLOperationType>emptyList()
        );
        return new JQuickSelectExpressionNode(dataSetOp);
    }
    @Test
    public void testSubqueryAsTable() {
        // SELECT * FROM (SELECT id, name FROM users) AS t
        JQuickSelectClauseNode subSelectClause = createSimpleSelectClause("users", Arrays.asList("id", "name"));
        JQuickSelectExpressionNode subquerySelectExpr = buildSubquerySelectExpression(subSelectClause);
        JQuickTableNameItemNode tableItem = new JQuickTableNameItemNode(subquerySelectExpr, "t");
        JQuickFromClauseNode fromClause = new JQuickFromClauseNode(tableItem);
        JQuickSelectElementsNode selectElements = new JQuickSelectElementsNode(true, null);
        JQuickSelectClauseNode selectClause = new JQuickSelectClauseNode.Builder()
                .setSelectElements(selectElements)
                .setFromClause(fromClause)
                .build();
        JQuickQueryNode query = buildQuery(selectClause);
        JQuickLogicalPlanNode result = visitor.visit(query);
        assertTrue(tableItem.isSubquery());
        assertEquals("t", tableItem.getAlias());
        assertNotNull(tableItem.getSubquery());
        JQuickProjectNode outerProject = (JQuickProjectNode) result;
        JQuickProjectNode innerProject = (JQuickProjectNode) outerProject.getChild();
        assertEquals("Project", innerProject.getNodeType());
    }
}
