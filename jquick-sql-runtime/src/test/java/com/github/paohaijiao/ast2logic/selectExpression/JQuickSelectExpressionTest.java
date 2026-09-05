package com.github.paohaijiao.ast2logic.selectExpression;

import com.github.paohaijiao.ast.*;
import com.github.paohaijiao.ast2logic.JQuickASTToLogicalPlanVisitor;
import com.github.paohaijiao.enums.JQuickSQLOperationType;
import com.github.paohaijiao.logic.JQuickLogicalPlanNode;
import com.github.paohaijiao.logic.domain.JQuickSetOperationNode;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class JQuickSelectExpressionTest {

    private JQuickASTToLogicalPlanVisitor visitor;
    /**
     * 构建 SELECT 指定列的 SelectClause
     */
    private JQuickSelectClauseNode buildSelectColumnsFrom(String tableName, String alias, List<String> columns) {
        List<JQuickSelectElementNode> elements = new ArrayList<JQuickSelectElementNode>();
        for (String col : columns) {
            JQuickFullColumnNameNode column = new JQuickFullColumnNameNode(col, null);
            JQuickExpressionAtomNode atom = new JQuickExpressionAtomNode(column);
            JQuickExpressionNode expr = new JQuickExpressionNode(atom);
            elements.add(new JQuickSelectElementNode(expr, null));
        }
        JQuickSelectElementsNode selectElements = new JQuickSelectElementsNode(false, elements);
        JQuickTableNameItemNode tableItem = new JQuickTableNameItemNode(tableName, alias);
        JQuickFromClauseNode fromClause = new JQuickFromClauseNode(tableItem);
        return new JQuickSelectClauseNode.Builder().setSelectElements(selectElements).setFromClause(fromClause).build();
    }
    /**
     * 构建带集合操作的查询 (多个 SelectClause)
     */
    private JQuickQueryNode buildSetOperationQuery(List<JQuickSelectClauseNode> selectClauses, List<JQuickSQLOperationType> operators) {
        JQuickDataSetOpNode dataSetOp = new JQuickDataSetOpNode(selectClauses, operators);
        JQuickSelectExpressionNode selectExpr = new JQuickSelectExpressionNode(dataSetOp);
        JQuickSelectStatementNode selectStmt = new JQuickSelectStatementNode(selectExpr);
        return new JQuickQueryNode(selectStmt);
    }

    @Before
    public void setUp() {
        visitor = new JQuickASTToLogicalPlanVisitor();
    }

    // 例如：SELECT1 UNION SELECT2 INTERSECT SELECT3
    // 构建结果树：
    //     INTERSECT
    //    /         \
    //  UNION      SELECT3
    //  /   \
    // S1   S2
    @Test
    public void testDataSetOp_Union() {
        // SELECT id FROM users1 UNION SELECT id FROM users2
        JQuickSelectClauseNode select1 = buildSelectColumnsFrom("users1", null, Arrays.asList("id"));
        JQuickSelectClauseNode select2 = buildSelectColumnsFrom("users2", null, Arrays.asList("id"));
        List<JQuickSelectClauseNode> clauses = Arrays.asList(select1, select2);
        List<JQuickSQLOperationType> operators = Arrays.asList(JQuickSQLOperationType.UNION);
        JQuickQueryNode query = buildSetOperationQuery(clauses, operators);
        JQuickLogicalPlanNode result = visitor.visit(query);
        JQuickSetOperationNode setOp = (JQuickSetOperationNode) result;
        assertEquals(JQuickSQLOperationType.UNION, setOp.getOperationType());
    }
}
