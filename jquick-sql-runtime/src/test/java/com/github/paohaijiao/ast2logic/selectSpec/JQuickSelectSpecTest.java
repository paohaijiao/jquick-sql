package com.github.paohaijiao.ast2logic.selectSpec;

import com.github.paohaijiao.ast.*;
import com.github.paohaijiao.ast2logic.JQuickASTToLogicalPlanVisitor;
import com.github.paohaijiao.enums.JQuickSQLOperationType;
import com.github.paohaijiao.logic.JQuickLogicalPlanNode;
import com.github.paohaijiao.logic.domain.JQuickProjectNode;
import com.github.paohaijiao.logic.domain.JQuickTableScanNode;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class JQuickSelectSpecTest {

    private JQuickASTToLogicalPlanVisitor visitor;

    @Before
    public void setUp() {
        visitor = new JQuickASTToLogicalPlanVisitor();
    }
    /**
     * 构建列表达式
     */
    private JQuickExpressionNode buildColumnExpr(String columnName, String tableAlias) {
        JQuickFullColumnNameNode column = new JQuickFullColumnNameNode(columnName, tableAlias);
        JQuickExpressionAtomNode atom = new JQuickExpressionAtomNode(column);
        return new JQuickExpressionNode(atom);
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
    public void testSelectDistinctSingleColumn() {
        // SELECT DISTINCT category FROM products
        JQuickSelectSpecNode selectSpec = new JQuickSelectSpecNode(true); // distinct = true
        List<JQuickSelectElementNode> elements = new ArrayList<>();
        elements.add(new JQuickSelectElementNode(buildColumnExpr("category", null), null));
        JQuickSelectElementsNode selectElements = new JQuickSelectElementsNode(false, elements);
        JQuickTableNameItemNode tableItem = new JQuickTableNameItemNode("products", null);
        JQuickFromClauseNode fromClause = new JQuickFromClauseNode(tableItem);
        JQuickSelectClauseNode selectClause = new JQuickSelectClauseNode.Builder()
                .setSelectSpec(selectSpec)
                .setSelectElements(selectElements)
                .setFromClause(fromClause)
                .build();
        JQuickQueryNode query = buildQuery(selectClause);
        JQuickLogicalPlanNode result = visitor.visit(query);
        JQuickProjectNode project = (JQuickProjectNode) result;
        assertTrue("Project node should have distinct=true", project.isDistinct());
        JQuickTableScanNode scan = (JQuickTableScanNode) project.getChild();
        assertEquals("products", scan.getTableName());
    }
    @Test
    public void testSelectAll() {
        // SELECT ALL category FROM products (ALL is the default, but explicit)
        JQuickSelectSpecNode selectSpec = new JQuickSelectSpecNode(false); // distinct = false means ALL
        List<JQuickSelectElementNode> elements = new ArrayList<>();
        elements.add(new JQuickSelectElementNode(buildColumnExpr("category", null), null));
        JQuickSelectElementsNode selectElements = new JQuickSelectElementsNode(false, elements);
        JQuickTableNameItemNode tableItem = new JQuickTableNameItemNode("products", null);
        JQuickFromClauseNode fromClause = new JQuickFromClauseNode(tableItem);
        JQuickSelectClauseNode selectClause = new JQuickSelectClauseNode.Builder()
                .setSelectSpec(selectSpec)
                .setSelectElements(selectElements)
                .setFromClause(fromClause)
                .build();
        JQuickQueryNode query = buildQuery(selectClause);
        JQuickLogicalPlanNode result = visitor.visit(query);
        JQuickProjectNode project = (JQuickProjectNode) result;
        assertFalse("Project node should have distinct=false for ALL", project.isDistinct());
    }
}
