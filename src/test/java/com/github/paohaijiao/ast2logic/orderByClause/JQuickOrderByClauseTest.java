package com.github.paohaijiao.ast2logic.orderByClause;

import com.github.paohaijiao.ast.*;
import com.github.paohaijiao.ast2logic.JQuickASTToLogicalPlanVisitor;
import com.github.paohaijiao.enums.JQuickSQLOperationType;
import com.github.paohaijiao.logic.JQuickLogicalPlanNode;
import com.github.paohaijiao.logic.domain.JQuickProjectNode;
import com.github.paohaijiao.logic.domain.JQuickSortNode;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class JQuickOrderByClauseTest {

    private JQuickASTToLogicalPlanVisitor visitor;

    @Before
    public void setUp() {
        visitor = new JQuickASTToLogicalPlanVisitor();
    }
    /**
     * 创建基础查询
     */
    private JQuickSelectClauseNode createBaseSelectClause(String tableName, String tableAlias) {
        JQuickTableNameItemNode tableItem = new JQuickTableNameItemNode(tableName, tableAlias);
        JQuickFromClauseNode fromClause = new JQuickFromClauseNode(tableItem);
        JQuickSelectElementsNode selectElements = new JQuickSelectElementsNode(true, null);
        return new JQuickSelectClauseNode.Builder().setSelectElements(selectElements).setFromClause(fromClause).build();
    }
    private JQuickExpressionNode buildColumnExpr(String columnName, String tableAlias) {
        JQuickFullColumnNameNode column = new JQuickFullColumnNameNode(columnName, tableAlias);
        JQuickExpressionAtomNode atom = new JQuickExpressionAtomNode(column);
        return new JQuickExpressionNode(atom);
    }
    private JQuickExpressionNode buildColumnExpr(String columnName) {
        return buildColumnExpr(columnName, null);
    }
    private JQuickQueryNode buildQuery(JQuickSelectClauseNode selectClause) {
        JQuickDataSetOpNode dataSetOp = new JQuickDataSetOpNode(Collections.singletonList(selectClause), Collections.<JQuickSQLOperationType>emptyList());
        JQuickSelectExpressionNode selectExpr = new JQuickSelectExpressionNode(dataSetOp);
        JQuickSelectStatementNode selectStmt = new JQuickSelectStatementNode(selectExpr);
        return new JQuickQueryNode(selectStmt);
    }
    @Test
    public void testOrderBySingleColumnAsc() {
        // SELECT * FROM users ORDER BY name ASC
        JQuickSelectClauseNode baseClause = createBaseSelectClause("users", null);
        JQuickOrderByExpressionNode orderByExpr = new JQuickOrderByExpressionNode(buildColumnExpr("name"), true);
        JQuickOrderByClauseNode orderByClause = new JQuickOrderByClauseNode(Collections.singletonList(orderByExpr));
        JQuickSelectClauseNode selectClause = new JQuickSelectClauseNode.Builder()
                .setSelectElements(baseClause.getSelectElements())
                .setFromClause(baseClause.getFromClause())
                .setOrderByClause(orderByClause)
                .build();
        JQuickQueryNode query = buildQuery(selectClause);
        JQuickLogicalPlanNode result = visitor.visit(query);
        JQuickSortNode project = (JQuickSortNode) result;
        assertEquals(1, project.getOrderByItems().size());
        assertEquals("name", project.getOrderByItems().get(0).getColumnName());
        assertTrue(project.getOrderByItems().get(0).isAscending());
    }
    @Test
    public void testOrderByMultipleColumns() {
        // SELECT * FROM users ORDER BY category ASC, price DESC
        JQuickSelectClauseNode baseClause = createBaseSelectClause("products", null);
        List<JQuickOrderByExpressionNode> orderByExprs = new ArrayList<>();
        orderByExprs.add(new JQuickOrderByExpressionNode(buildColumnExpr("category"), true));
        orderByExprs.add(new JQuickOrderByExpressionNode(buildColumnExpr("price"), false));
        JQuickOrderByClauseNode orderByClause = new JQuickOrderByClauseNode(orderByExprs);
        JQuickSelectClauseNode selectClause = new JQuickSelectClauseNode.Builder()
                .setSelectElements(baseClause.getSelectElements())
                .setFromClause(baseClause.getFromClause())
                .setOrderByClause(orderByClause)
                .build();
        JQuickQueryNode query = buildQuery(selectClause);
        JQuickLogicalPlanNode result = visitor.visit(query);
        JQuickSortNode sort = (JQuickSortNode) result;
        assertEquals(2, sort.getOrderByItems().size());
        assertEquals("category", sort.getOrderByItems().get(0).getColumnName());
        assertTrue(sort.getOrderByItems().get(0).isAscending());
        assertEquals("price", sort.getOrderByItems().get(1).getColumnName());
        assertFalse(sort.getOrderByItems().get(1).isAscending());
    }
}
