package com.github.paohaijiao.ast2logic.commonTableExpression;

import com.github.paohaijiao.ast.*;
import com.github.paohaijiao.ast2logic.JQuickASTToLogicalPlanVisitor;
import com.github.paohaijiao.enums.JQuickSQLOperationType;
import com.github.paohaijiao.logic.JQuickLogicalPlanNode;
import com.github.paohaijiao.logic.domain.JQuickWithNode;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class JQuickCommonTableExpressionTest {
    private JQuickASTToLogicalPlanVisitor visitor;

    @Before
    public void setUp() {
        visitor = new JQuickASTToLogicalPlanVisitor();
    }
    private JQuickSelectClauseNode buildSimpleSelectClause(String tableName, String tableAlias) {
        JQuickSelectElementsNode selectElements = new JQuickSelectElementsNode(true, null);
        JQuickTableNameItemNode tableItem = new JQuickTableNameItemNode(tableName, tableAlias);
        JQuickFromClauseNode fromClause = new JQuickFromClauseNode(tableItem);

        return new JQuickSelectClauseNode.Builder()
                .setSelectElements(selectElements)
                .setFromClause(fromClause)
                .build();
    }

    private JQuickSelectClauseNode buildSimpleSelectClause(String tableName) {
        return buildSimpleSelectClause(tableName, null);
    }

    /**
     * 构建 SelectStatement (普通查询)
     */
    private JQuickSelectStatementNode buildSelectStatement(JQuickSelectClauseNode selectClause) {
        JQuickDataSetOpNode dataSetOp = new JQuickDataSetOpNode(
                Collections.singletonList(selectClause),
                Collections.<JQuickSQLOperationType>emptyList()
        );
        JQuickSelectExpressionNode selectExpr = new JQuickSelectExpressionNode(dataSetOp);
        return new JQuickSelectStatementNode(selectExpr);
    }

    /**
     * 构建带 CTE 的 SelectStatement
     */
    private JQuickSelectStatementNode buildSelectStatementWithCte(
            List<JQuickCommonTableExpressionNode> ctes,
            JQuickSelectClauseNode mainSelectClause) {
        JQuickDataSetOpNode dataSetOp = new JQuickDataSetOpNode(
                Collections.singletonList(mainSelectClause),
                Collections.<JQuickSQLOperationType>emptyList()
        );
        JQuickSelectExpressionNode selectExpr = new JQuickSelectExpressionNode(dataSetOp);
        return new JQuickSelectStatementNode(false, ctes, selectExpr);
    }

    /**
     * 构建完整的 Query
     */
    private JQuickQueryNode buildQuery(JQuickSelectStatementNode selectStatement) {
        return new JQuickQueryNode(selectStatement);
    }
    @Test
    public void testSimpleCteCreation() {
        // WITH active_users AS (SELECT * FROM users WHERE status = 'active')
        // SELECT * FROM active_users
        // SELECT * FROM users
        JQuickSelectClauseNode innerSelectClause = buildSimpleSelectClause("users");
        // WHERE status = 'active'
        JQuickFullColumnNameNode statusColumn = new JQuickFullColumnNameNode("status", null);
        JQuickExpressionAtomNode statusAtom = new JQuickExpressionAtomNode(statusColumn);
        JQuickConstantNode activeConst = new JQuickConstantNode("active", JQuickConstantNode.ConstantType.STRING);
        JQuickExpressionAtomNode activeAtom = new JQuickExpressionAtomNode(activeConst);
        JQuickPredicateNode leftPred = new JQuickPredicateNode(statusAtom);
        JQuickPredicateNode rightPred = new JQuickPredicateNode(activeAtom);
        JQuickPredicateNode eqPred = new JQuickPredicateNode(leftPred, rightPred, JQuickPredicateNode.ComparisonOperator.EQ);
        JQuickFilterConditionNode filterCondition = new JQuickFilterConditionNode(eqPred);
        JQuickWhereClauseNode whereClause = new JQuickWhereClauseNode(filterCondition);
        JQuickSelectClauseNode innerSelectWithWhere = new JQuickSelectClauseNode.Builder()
                .setSelectElements(innerSelectClause.getSelectElements())
                .setFromClause(innerSelectClause.getFromClause())
                .setWhereClause(whereClause)
                .build();
        JQuickSelectStatementNode innerSelectStmt = buildSelectStatement(innerSelectWithWhere);

        JQuickCommonTableExpressionNode cte = new JQuickCommonTableExpressionNode("active_users", null, innerSelectStmt);

        List<JQuickCommonTableExpressionNode> ctes = new ArrayList<>();
        ctes.add(cte);
        JQuickSelectClauseNode mainSelectClause = buildSimpleSelectClause("active_users");
        JQuickSelectStatementNode selectStmt = buildSelectStatementWithCte(ctes, mainSelectClause);
        JQuickQueryNode query = buildQuery(selectStmt);
        JQuickLogicalPlanNode result = visitor.visit(query);
        assertNotNull(result);
        assertEquals("With", result.getNodeType());
        JQuickWithNode withNode = (JQuickWithNode) result;
        assertEquals(1, withNode.getCtes().size());
        assertTrue(withNode.getCtes().containsKey("active_users"));
    }


}
