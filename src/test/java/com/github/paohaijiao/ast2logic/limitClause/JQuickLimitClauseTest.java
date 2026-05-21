package com.github.paohaijiao.ast2logic.limitClause;

import com.github.paohaijiao.ast.*;
import com.github.paohaijiao.ast2logic.JQuickASTToLogicalPlanVisitor;
import com.github.paohaijiao.enums.JQuickSQLOperationType;
import com.github.paohaijiao.logic.JQuickLogicalPlanNode;
import com.github.paohaijiao.logic.domain.JQuickLimitNode;
import com.github.paohaijiao.logic.domain.JQuickProjectNode;
import com.github.paohaijiao.logic.domain.JQuickSortNode;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class JQuickLimitClauseTest {

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
    private JQuickSelectClauseNode createBaseSelectClause(String tableName) {
        return createBaseSelectClause(tableName, null);
    }
    private JQuickExpressionNode buildConstantExpr(Object value, JQuickConstantNode.ConstantType type) {
        JQuickConstantNode constant = new JQuickConstantNode(value, type);
        JQuickExpressionAtomNode atom = new JQuickExpressionAtomNode(constant);
        return new JQuickExpressionNode(atom);
    }
    private JQuickExpressionNode buildNumberConstantExpr(Number value) {
        return buildConstantExpr(value, JQuickConstantNode.ConstantType.DECIMAL);
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
    public void testLimitOnly() {
        // SELECT * FROM users LIMIT 10
        JQuickSelectClauseNode baseClause = createBaseSelectClause("users");
        JQuickLimitClauseNode limitClause = new JQuickLimitClauseNode(buildNumberConstantExpr(10));
        JQuickSelectClauseNode selectClause = new JQuickSelectClauseNode.Builder()
                .setSelectElements(baseClause.getSelectElements())
                .setFromClause(baseClause.getFromClause())
                .setLimitClause(limitClause)
                .build();
        JQuickQueryNode query = buildQuery(selectClause);
        JQuickLogicalPlanNode result = visitor.visit(query);
        JQuickLimitNode limit = (JQuickLimitNode) result;
        assertEquals(10, limit.getLimit());
        assertEquals(0, limit.getOffset());
        assertFalse(limitClause.hasOffset());
    }
    @Test
    public void testLimitWithOffset() {
        // SELECT * FROM users LIMIT 10 OFFSET 5
        JQuickSelectClauseNode baseClause = createBaseSelectClause("users");
        JQuickLimitClauseNode limitClause = new JQuickLimitClauseNode(
                buildNumberConstantExpr(5),  // offset
                buildNumberConstantExpr(10)); // limit
        JQuickSelectClauseNode selectClause = new JQuickSelectClauseNode.Builder()
                .setSelectElements(baseClause.getSelectElements())
                .setFromClause(baseClause.getFromClause())
                .setLimitClause(limitClause)
                .build();
        JQuickQueryNode query = buildQuery(selectClause);
        JQuickLogicalPlanNode result = visitor.visit(query);
        JQuickLimitNode limit = (JQuickLimitNode) result;
        assertEquals(10, limit.getLimit());
        assertEquals(5, limit.getOffset());
        assertTrue(limitClause.hasOffset());
    }
}
