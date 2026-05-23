package com.github.paohaijiao.ast2logic.selectStatement;

import com.github.paohaijiao.ast.*;
import com.github.paohaijiao.ast2logic.JQuickASTToLogicalPlanVisitor;
import com.github.paohaijiao.enums.JQuickSQLOperationType;
import com.github.paohaijiao.logic.JQuickLogicalPlanNode;
import com.github.paohaijiao.logic.domain.JQuickProjectNode;
import com.github.paohaijiao.logic.domain.JQuickTableScanNode;
import com.github.paohaijiao.logic.domain.JQuickWithNode;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class JQuickASTToLogicalPlanVisitorTest {

    private JQuickASTToLogicalPlanVisitor visitor;

    @Before
    public void setUp() {
        visitor = new JQuickASTToLogicalPlanVisitor();
    }
    /**
     * 构建普通查询的 JQuickQueryNode
     * 结构：Query -> SelectStatement(singleQuery) -> SelectExpression -> DataSetOp -> SelectClause
     */
    private JQuickQueryNode buildNormalQuery(JQuickSelectClauseNode selectClause) {
        JQuickDataSetOpNode dataSetOp = new JQuickDataSetOpNode(
                Collections.singletonList(selectClause),
                Collections.<JQuickSQLOperationType>emptyList()
        );
        JQuickSelectExpressionNode selectExpr = new JQuickSelectExpressionNode(dataSetOp);
        JQuickSelectStatementNode selectStmt = new JQuickSelectStatementNode(selectExpr);
        return new JQuickQueryNode(selectStmt);
    }

    /**
     * 构建 CTE 查询的 JQuickQueryNode
     * 结构：Query -> SelectStatement(cteQuery) -> SelectExpression -> DataSetOp -> SelectClause
     */
    private JQuickQueryNode buildCteQuery(boolean withRecursive, List<JQuickCommonTableExpressionNode> ctes, JQuickSelectClauseNode mainSelectClause) {
        JQuickDataSetOpNode mainDataSetOp = new JQuickDataSetOpNode(Collections.singletonList(mainSelectClause), Collections.<JQuickSQLOperationType>emptyList());
        JQuickSelectExpressionNode mainSelectExpr = new JQuickSelectExpressionNode(mainDataSetOp);
        JQuickSelectStatementNode selectStmt = new JQuickSelectStatementNode(withRecursive, ctes, mainSelectExpr);
        return new JQuickQueryNode(selectStmt);
    }

    /**
     * 构建表达式原子节点
     */
    private JQuickExpressionAtomNode buildColumnAtom(String columnName, String tableAlias) {
        JQuickFullColumnNameNode column = new JQuickFullColumnNameNode(columnName, tableAlias);
        return new JQuickExpressionAtomNode(column);
    }
    /**
     * 构建常量原子节点
     */
    private JQuickExpressionAtomNode buildConstantAtom(Object value, JQuickConstantNode.ConstantType type) {
        JQuickConstantNode constant = new JQuickConstantNode(value, type);
        return new JQuickExpressionAtomNode(constant);
    }

    /**
     * 从原子节点构建谓词
     */
    private JQuickPredicateNode buildPredicateFromAtom(JQuickExpressionAtomNode atom) {
        return new JQuickPredicateNode(atom);
    }

    /**
     * 构建 SELECT * 的 SelectClause
     */
    private JQuickSelectClauseNode buildSelectStarFrom(String tableName, String alias) {
        JQuickTableNameItemNode tableItem = new JQuickTableNameItemNode(tableName, alias);
        JQuickFromClauseNode fromClause = new JQuickFromClauseNode(tableItem);
        JQuickSelectElementsNode selectElements = new JQuickSelectElementsNode(true, null);
        return new JQuickSelectClauseNode.Builder()
                .setSelectElements(selectElements)
                .setFromClause(fromClause)
                .build();
    }
    /**
     * 构建比较谓词
     */
    private JQuickPredicateNode buildComparisonPredicate(JQuickExpressionAtomNode left, JQuickExpressionAtomNode right,
                                                         JQuickPredicateNode.ComparisonOperator op) {
        JQuickPredicateNode leftPred = new JQuickPredicateNode(left);
        JQuickPredicateNode rightPred = new JQuickPredicateNode(right);
        return new JQuickPredicateNode(leftPred, rightPred, op);
    }
    @Test
    public void testNormalQuery_SelectStar() {
        // SELECT * FROM users
        JQuickSelectClauseNode selectClause = buildSelectStarFrom("users", null);
        JQuickQueryNode query = buildNormalQuery(selectClause);
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
    public void testCteQuery_SingleCte() {
        // WITH active_users AS (SELECT * FROM users WHERE status = 'active')
        // SELECT * FROM active_users
        JQuickExpressionAtomNode statusAtom = buildColumnAtom("status", null);
        JQuickExpressionAtomNode activeAtom = buildConstantAtom("active", JQuickConstantNode.ConstantType.STRING);
        JQuickPredicateNode statusPred = buildComparisonPredicate(statusAtom, activeAtom, JQuickPredicateNode.ComparisonOperator.EQ);
        JQuickFilterConditionNode filterCondition = new JQuickFilterConditionNode(statusPred);
        JQuickWhereClauseNode whereClause = new JQuickWhereClauseNode(filterCondition);
        JQuickSelectClauseNode innerSelectClause = new JQuickSelectClauseNode.Builder()
                .setSelectElements(new JQuickSelectElementsNode(true, null))
                .setFromClause(new JQuickFromClauseNode(new JQuickTableNameItemNode("users", null)))
                .setWhereClause(whereClause)
                .build();
        JQuickDataSetOpNode innerDataSetOp = new JQuickDataSetOpNode(Collections.singletonList(innerSelectClause), Collections.emptyList());
        JQuickSelectExpressionNode innerSelectExpr = new JQuickSelectExpressionNode(innerDataSetOp);
        JQuickSelectStatementNode innerSelectStmt = new JQuickSelectStatementNode(innerSelectExpr);
        JQuickCommonTableExpressionNode cte = new JQuickCommonTableExpressionNode("active_users", null, innerSelectStmt);
        List<JQuickCommonTableExpressionNode> ctes = new ArrayList<JQuickCommonTableExpressionNode>();
        ctes.add(cte);
        JQuickSelectClauseNode mainSelectClause = buildSelectStarFrom("active_users", null);
        JQuickQueryNode query = buildCteQuery(false, ctes, mainSelectClause);
        JQuickLogicalPlanNode result = visitor.visit(query);
        assertNotNull(result);
        assertEquals("With", result.getNodeType());
        JQuickWithNode withNode = (JQuickWithNode) result;
        assertEquals(1, withNode.getCtes().size());
        assertTrue(withNode.getCtes().containsKey("active_users"));
        JQuickProjectNode mainProject = (JQuickProjectNode) withNode.getChild();
        assertEquals("Project", mainProject.getNodeType());
    }


}
