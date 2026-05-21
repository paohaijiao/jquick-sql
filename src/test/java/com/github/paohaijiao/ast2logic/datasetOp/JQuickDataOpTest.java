package com.github.paohaijiao.ast2logic.datasetOp;

import com.github.paohaijiao.ast.*;
import com.github.paohaijiao.ast2logic.JQuickASTToLogicalPlanVisitor;
import com.github.paohaijiao.enums.JQuickSQLOperationType;
import com.github.paohaijiao.logic.JQuickLogicalPlanNode;
import com.github.paohaijiao.logic.domain.JQuickProjectNode;
import com.github.paohaijiao.logic.domain.JQuickSetOperationNode;
import com.github.paohaijiao.logic.domain.JQuickTableScanNode;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class JQuickDataOpTest {

    private JQuickASTToLogicalPlanVisitor visitor;

    @Before
    public void setUp() {
        visitor = new JQuickASTToLogicalPlanVisitor();
    }
    /**
     * 构建基本的 SelectClause (SELECT * FROM table)
     */
    private JQuickSelectClauseNode buildSimpleSelect(String tableName, String alias) {
        JQuickTableNameItemNode tableItem = new JQuickTableNameItemNode(tableName, alias);
        JQuickFromClauseNode fromClause = new JQuickFromClauseNode(tableItem);
        JQuickSelectElementsNode selectElements = new JQuickSelectElementsNode(true, null);
        return new JQuickSelectClauseNode.Builder().setSelectElements(selectElements).setFromClause(fromClause).build();
    }
    /**
     * 构建 DataSetOp 并转换为 Query
     */
    private JQuickQueryNode buildQueryFromDataSetOp(List<JQuickSelectClauseNode> selectClauses, List<JQuickSQLOperationType> operators) {
        JQuickDataSetOpNode dataSetOp = new JQuickDataSetOpNode(selectClauses, operators);
        JQuickSelectExpressionNode selectExpr = new JQuickSelectExpressionNode(dataSetOp);
        JQuickSelectStatementNode selectStmt = new JQuickSelectStatementNode(selectExpr);
        return new JQuickQueryNode(selectStmt);
    }
    /**
     * 构建指定列的 SelectClause (SELECT col1, col2 FROM table)
     */
    private JQuickSelectClauseNode buildSelectWithColumns(String tableName, List<String> columns) {
        List<JQuickSelectElementNode> elements = new ArrayList<>();
        for (String col : columns) {
            JQuickFullColumnNameNode column = new JQuickFullColumnNameNode(col, null);
            JQuickExpressionAtomNode atom = new JQuickExpressionAtomNode(column);
            JQuickExpressionNode expr = new JQuickExpressionNode(atom);
            elements.add(new JQuickSelectElementNode(expr, null));
        }
        JQuickSelectElementsNode selectElements = new JQuickSelectElementsNode(false, elements);
        JQuickTableNameItemNode tableItem = new JQuickTableNameItemNode(tableName, null);
        JQuickFromClauseNode fromClause = new JQuickFromClauseNode(tableItem);
        return new JQuickSelectClauseNode.Builder().setSelectElements(selectElements).setFromClause(fromClause).build();
    }
    @Test
    public void testSingleSelect() {
        // SELECT * FROM users
        JQuickSelectClauseNode selectClause = buildSimpleSelect("users", null);
        JQuickQueryNode query = buildQueryFromDataSetOp(Collections.singletonList(selectClause), Collections.emptyList());
        JQuickLogicalPlanNode result = visitor.visit(query);
        assertNotNull(result);
        assertEquals("Project", result.getNodeType());
        JQuickProjectNode project = (JQuickProjectNode) result;
        assertTrue(project.getSelectItems().get(0).isStar());
        JQuickTableScanNode scan = (JQuickTableScanNode) project.getChild();
        assertEquals("users", scan.getTableName());
    }
    @Test
    public void testUnion() {
        // SELECT id FROM users1 UNION SELECT id FROM users2
        JQuickSelectClauseNode select1 = buildSelectWithColumns("users1", Arrays.asList("id"));
        JQuickSelectClauseNode select2 = buildSelectWithColumns("users2", Arrays.asList("id"));
        List<JQuickSelectClauseNode> clauses = Arrays.asList(select1, select2);
        List<JQuickSQLOperationType> operators = Arrays.asList(JQuickSQLOperationType.UNION);
        JQuickQueryNode query = buildQueryFromDataSetOp(clauses, operators);
        JQuickLogicalPlanNode result = visitor.visit(query);
        JQuickSetOperationNode setOp = (JQuickSetOperationNode) result;
        assertEquals(JQuickSQLOperationType.UNION, setOp.getOperationType());
        JQuickProjectNode left = (JQuickProjectNode) setOp.getLeft();
        JQuickProjectNode right = (JQuickProjectNode) setOp.getRight();
        JQuickTableScanNode leftScan = (JQuickTableScanNode) left.getChild();
        JQuickTableScanNode rightScan = (JQuickTableScanNode) right.getChild();
        assertEquals("users1", leftScan.getTableName());
        assertEquals("users2", rightScan.getTableName());
    }
    @Test
    public void testMinus() {
        // SELECT id FROM users1 MINUS SELECT id FROM users2
        JQuickSelectClauseNode select1 = buildSelectWithColumns("users1", Arrays.asList("id"));
        JQuickSelectClauseNode select2 = buildSelectWithColumns("users2", Arrays.asList("id"));
        List<JQuickSelectClauseNode> clauses = Arrays.asList(select1, select2);
        List<JQuickSQLOperationType> operators = Arrays.asList(JQuickSQLOperationType.MINUS);
        JQuickQueryNode query = buildQueryFromDataSetOp(clauses, operators);
        JQuickLogicalPlanNode result = visitor.visit(query);
        JQuickSetOperationNode setOp = (JQuickSetOperationNode) result;
        assertEquals(JQuickSQLOperationType.MINUS, setOp.getOperationType());
        JQuickProjectNode left = (JQuickProjectNode) setOp.getLeft();
        JQuickProjectNode right = (JQuickProjectNode) setOp.getRight();
        JQuickTableScanNode leftScan = (JQuickTableScanNode) left.getChild();
        JQuickTableScanNode rightScan = (JQuickTableScanNode) right.getChild();
        assertEquals("users1", leftScan.getTableName());
        assertEquals("users2", rightScan.getTableName());
    }
    @Test
    public void testIntersect() {
        // SELECT id FROM users1 INTERSECT SELECT id FROM users2
        JQuickSelectClauseNode select1 = buildSelectWithColumns("users1", Arrays.asList("id"));
        JQuickSelectClauseNode select2 = buildSelectWithColumns("users2", Arrays.asList("id"));
        List<JQuickSelectClauseNode> clauses = Arrays.asList(select1, select2);
        List<JQuickSQLOperationType> operators = Arrays.asList(JQuickSQLOperationType.INTERSECT);
        JQuickQueryNode query = buildQueryFromDataSetOp(clauses, operators);
        JQuickLogicalPlanNode result = visitor.visit(query);
        JQuickSetOperationNode setOp = (JQuickSetOperationNode) result;
        assertEquals(JQuickSQLOperationType.INTERSECT, setOp.getOperationType());
        JQuickProjectNode left = (JQuickProjectNode) setOp.getLeft();
        JQuickProjectNode right = (JQuickProjectNode) setOp.getRight();
        JQuickTableScanNode leftScan = (JQuickTableScanNode) left.getChild();
        JQuickTableScanNode rightScan = (JQuickTableScanNode) right.getChild();
        assertEquals("users1", leftScan.getTableName());
        assertEquals("users2", rightScan.getTableName());
    }
}
