package com.github.paohaijiao.ast2logic.commonTableExpression;

import com.github.paohaijiao.ast.*;
import com.github.paohaijiao.ast2logic.JQuickASTToLogicalPlanVisitor;
import com.github.paohaijiao.enums.JQuickJoinType;
import com.github.paohaijiao.enums.JQuickSQLOperationType;
import com.github.paohaijiao.logic.JQuickLogicalPlanNode;
import com.github.paohaijiao.logic.domain.JQuickRecursiveUnionNode;
import com.github.paohaijiao.logic.domain.JQuickWithNode;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class JQuickCommonTableExpressionTest1 {

    private JQuickASTToLogicalPlanVisitor visitor;

    @Before
    public void setUp() {
        visitor = new JQuickASTToLogicalPlanVisitor();
    }
    private JQuickSelectClauseNode buildSimpleSelectClause(String tableName, String tableAlias) {
        JQuickSelectElementsNode selectElements = new JQuickSelectElementsNode(true, null);
        JQuickTableNameItemNode tableItem = new JQuickTableNameItemNode(tableName, tableAlias);
        JQuickFromClauseNode fromClause = new JQuickFromClauseNode(tableItem);
        return new JQuickSelectClauseNode.Builder().setSelectElements(selectElements).setFromClause(fromClause).build();
    }

    private JQuickSelectClauseNode buildSimpleSelectClause(String tableName) {
        return buildSimpleSelectClause(tableName, null);
    }

    /**
     * 构建 SelectStatement (普通查询)
     */
    private JQuickSelectStatementNode buildSelectStatement(JQuickSelectClauseNode selectClause) {
        JQuickDataSetOpNode dataSetOp = new JQuickDataSetOpNode(Collections.singletonList(selectClause), Collections.<JQuickSQLOperationType>emptyList());
        JQuickSelectExpressionNode selectExpr = new JQuickSelectExpressionNode(dataSetOp);
        return new JQuickSelectStatementNode(selectExpr);
    }

    /**
     * 构建带 CTE 的 SelectStatement (非递归)
     */
    private JQuickSelectStatementNode buildSelectStatementWithCte(List<JQuickCommonTableExpressionNode> ctes, JQuickSelectClauseNode mainSelectClause) {
        JQuickDataSetOpNode dataSetOp = new JQuickDataSetOpNode(Collections.singletonList(mainSelectClause), Collections.<JQuickSQLOperationType>emptyList());
        JQuickSelectExpressionNode selectExpr = new JQuickSelectExpressionNode(dataSetOp);
        return new JQuickSelectStatementNode(false, ctes, selectExpr);
    }

    /**
     * 构建带递归 CTE 的 SelectStatement
     */
    private JQuickSelectStatementNode buildSelectStatementWithRecursiveCte(List<JQuickCommonTableExpressionNode> ctes, JQuickSelectClauseNode mainSelectClause) {
        JQuickDataSetOpNode dataSetOp = new JQuickDataSetOpNode(Collections.singletonList(mainSelectClause), Collections.<JQuickSQLOperationType>emptyList());
        JQuickSelectExpressionNode selectExpr = new JQuickSelectExpressionNode(dataSetOp);
        return new JQuickSelectStatementNode(true, ctes, selectExpr);
    }

    /**
     * 构建完整的 Query
     */
    private JQuickQueryNode buildQuery(JQuickSelectStatementNode selectStatement) {
        return new JQuickQueryNode(selectStatement);
    }

    /**
     * 构建递归CTE：组织架构树
     * SQL:
     * WITH RECURSIVE org_tree(employee_id, employee_name, manager_id, level) AS (
     *     SELECT employee_id, employee_name, manager_id, 1 as level
     *     FROM employees
     *     WHERE manager_id IS NULL
     *     UNION ALL
     *     SELECT e.employee_id, e.employee_name, e.manager_id, org.level + 1
     *     FROM employees e
     *     INNER JOIN org_tree org ON e.manager_id = org.employee_id
     *     WHERE org.level < 10
     * )
     * SELECT * FROM org_tree ORDER BY level, employee_id
     */
    @Test
    public void testRecursiveCte_OrganizationTree() {
        JQuickSelectStatementNode initialQuery = buildInitialQuery_RootEmployees();
        // 2. 构建 recursivePart: 递归查询下属
        JQuickSelectStatementNode recursivePart = buildRecursivePart_ChildEmployees();
        JQuickCommonTableExpressionNode cte = new JQuickCommonTableExpressionNode(
                "org_tree",
                Arrays.asList("employee_id", "employee_name", "manager_id", "level"),
                initialQuery,
                recursivePart,
                true  // UNION ALL
        );
        JQuickSelectClauseNode mainSelectClause = buildSelectClause_StarFromOrgTree();
        JQuickOrderByClauseNode orderByClause = buildOrderByClause(Arrays.asList("level", "employee_id"));
        JQuickSelectClauseNode mainSelectWithOrder = new JQuickSelectClauseNode.Builder()
                .setSelectElements(mainSelectClause.getSelectElements())
                .setFromClause(mainSelectClause.getFromClause())
                .setOrderByClause(orderByClause)
                .build();

        List<JQuickCommonTableExpressionNode> ctes = new ArrayList<>();
        ctes.add(cte);
        JQuickSelectStatementNode selectStmt = buildSelectStatementWithRecursiveCte(ctes, mainSelectWithOrder);
        JQuickQueryNode query = buildQuery(selectStmt);
        JQuickLogicalPlanNode result = visitor.visit(query);
        assertNotNull(result);
        assertEquals("With", result.getNodeType());
        JQuickWithNode withNode = (JQuickWithNode) result;
        assertEquals(1, withNode.getCtes().size());
        assertTrue(withNode.getCtes().containsKey("org_tree"));
        JQuickLogicalPlanNode ctePlan = withNode.getCtes().get("org_tree");
        assertTrue(ctePlan instanceof JQuickRecursiveUnionNode);
        JQuickRecursiveUnionNode recursiveNode = (JQuickRecursiveUnionNode) ctePlan;
        assertEquals("org_tree", recursiveNode.getCteName());
        assertEquals(Arrays.asList("employee_id", "employee_name", "manager_id", "level"), recursiveNode.getColumnNames());
        assertTrue(recursiveNode.isUnionAll());
        assertNotNull(recursiveNode.getInitialPlan());
        assertNotNull(recursiveNode.getRecursivePlan());
        assertEquals(2, recursiveNode.getChildren().size());
        List<String> outputColumns = recursiveNode.getOutputColumns();
        assertEquals(4, outputColumns.size());
        System.out.println("递归CTE测试通过: " + recursiveNode.getCteName());
    }

    /**
     * 构建 initialQuery: 查询根节点
     * SELECT employee_id, employee_name, manager_id, 1 as level
     * FROM employees WHERE manager_id IS NULL
     */
    private JQuickSelectStatementNode buildInitialQuery_RootEmployees() {
        JQuickFullColumnNameNode employeeIdCol = new JQuickFullColumnNameNode("employee_id", null);
        JQuickFullColumnNameNode employeeNameCol = new JQuickFullColumnNameNode("employee_name", null);
        JQuickFullColumnNameNode managerIdCol = new JQuickFullColumnNameNode("manager_id", null);
        JQuickConstantNode levelOne = new JQuickConstantNode(1L, JQuickConstantNode.ConstantType.DECIMAL);
        JQuickSelectElementNode idElement = new JQuickSelectElementNode(new JQuickExpressionNode(new JQuickExpressionAtomNode(employeeIdCol)), null);
        JQuickSelectElementNode nameElement = new JQuickSelectElementNode(new JQuickExpressionNode(new JQuickExpressionAtomNode(employeeNameCol)), null);
        JQuickSelectElementNode managerIdElement = new JQuickSelectElementNode(new JQuickExpressionNode(new JQuickExpressionAtomNode(managerIdCol)), null);
        JQuickSelectElementNode levelElement = new JQuickSelectElementNode(new JQuickExpressionNode(new JQuickExpressionAtomNode(levelOne)), "level");
        JQuickSelectElementsNode selectElements = new JQuickSelectElementsNode(false, Arrays.asList(idElement, nameElement, managerIdElement, levelElement));
        JQuickFromClauseNode fromClause = new JQuickFromClauseNode(new JQuickTableNameItemNode("employees", null));
        JQuickExpressionAtomNode managerIdAtom = new JQuickExpressionAtomNode(managerIdCol);
        JQuickPredicateNode isNullPredicate = new JQuickPredicateNode(new JQuickPredicateNode(managerIdAtom), false);
        JQuickWhereClauseNode whereClause = new JQuickWhereClauseNode(new JQuickFilterConditionNode(isNullPredicate));
        JQuickSelectClauseNode selectClause = new JQuickSelectClauseNode.Builder().setSelectElements(selectElements).setFromClause(fromClause).setWhereClause(whereClause).build();
        return buildSelectStatement(selectClause);
    }

    /**
     * 构建 recursivePart: 递归查询下属
     * SELECT e.employee_id, e.employee_name, e.manager_id, org.level + 1
     * FROM employees e
     * INNER JOIN org_tree org ON e.manager_id = org.employee_id
     * WHERE org.level < 10
     */
    private JQuickSelectStatementNode buildRecursivePart_ChildEmployees() {
        JQuickFullColumnNameNode eEmployeeId = new JQuickFullColumnNameNode("employee_id", "e");
        JQuickFullColumnNameNode eEmployeeName = new JQuickFullColumnNameNode("employee_name", "e");
        JQuickFullColumnNameNode eManagerId = new JQuickFullColumnNameNode("manager_id", "e");
        JQuickFullColumnNameNode orgEmployeeId = new JQuickFullColumnNameNode("employee_id", "org");
        JQuickFullColumnNameNode orgLevel = new JQuickFullColumnNameNode("level", "org");
        JQuickConstantNode one = new JQuickConstantNode(1L, JQuickConstantNode.ConstantType.DECIMAL);
        JQuickExpressionAtomNode orgLevelAtom = new JQuickExpressionAtomNode(orgLevel);
        JQuickExpressionAtomNode oneAtom = new JQuickExpressionAtomNode(one);
        JQuickExpressionAtomNode levelPlusOne = new JQuickExpressionAtomNode(orgLevelAtom, oneAtom, JQuickExpressionAtomNode.MathOperator.PLUS);
        JQuickSelectElementNode idElement = new JQuickSelectElementNode(new JQuickExpressionNode(new JQuickExpressionAtomNode(eEmployeeId)), null);
        JQuickSelectElementNode nameElement = new JQuickSelectElementNode(new JQuickExpressionNode(new JQuickExpressionAtomNode(eEmployeeName)), null);
        JQuickSelectElementNode managerIdElement = new JQuickSelectElementNode(new JQuickExpressionNode(new JQuickExpressionAtomNode(eManagerId)), null);
        JQuickSelectElementNode levelElement = new JQuickSelectElementNode(new JQuickExpressionNode(levelPlusOne), "level");
        JQuickSelectElementsNode selectElements = new JQuickSelectElementsNode(false, Arrays.asList(idElement, nameElement, managerIdElement, levelElement));
        JQuickFromClauseNode fromClause = new JQuickFromClauseNode(new JQuickTableNameItemNode("employees", "e"));

        JQuickTableNameItemNode orgTable = new JQuickTableNameItemNode("org_tree", "org");
        JQuickJoinClauseNode joinClause = new JQuickJoinClauseNode(JQuickJoinType.INNER, orgTable, eManagerId, orgEmployeeId);

        JQuickConstantNode ten = new JQuickConstantNode(10L, JQuickConstantNode.ConstantType.DECIMAL);
        JQuickExpressionAtomNode tenAtom = new JQuickExpressionAtomNode(ten);
        JQuickPredicateNode levelPredicate = new JQuickPredicateNode(new JQuickPredicateNode(orgLevelAtom), new JQuickPredicateNode(tenAtom), JQuickPredicateNode.ComparisonOperator.LT);
        JQuickWhereClauseNode whereClause = new JQuickWhereClauseNode(new JQuickFilterConditionNode(levelPredicate));
        JQuickSelectClauseNode selectClause = new JQuickSelectClauseNode.Builder()
                .setSelectElements(selectElements)
                .setFromClause(fromClause)
                .addJoinClause(joinClause)
                .setWhereClause(whereClause)
                .build();
        return buildSelectStatement(selectClause);
    }

    /**
     * 构建 SELECT * FROM org_tree
     */
    private JQuickSelectClauseNode buildSelectClause_StarFromOrgTree() {
        JQuickSelectElementsNode selectElements = new JQuickSelectElementsNode(true, null);
        JQuickFromClauseNode fromClause = new JQuickFromClauseNode(new JQuickTableNameItemNode("org_tree", null));
        return new JQuickSelectClauseNode.Builder().setSelectElements(selectElements).setFromClause(fromClause).build();
    }

    /**
     * 构建 ORDER BY子句
     */
    private JQuickOrderByClauseNode buildOrderByClause(List<String> columns) {
        List<JQuickOrderByExpressionNode> expressions = new ArrayList<>();
        for (String column : columns) {
            JQuickFullColumnNameNode fullColumn = new JQuickFullColumnNameNode(column, null);
            JQuickExpressionAtomNode atom = new JQuickExpressionAtomNode(fullColumn);
            JQuickExpressionNode expr = new JQuickExpressionNode(atom);
            expressions.add(new JQuickOrderByExpressionNode(expr, true));
        }
        return new JQuickOrderByClauseNode(expressions);
    }

    @Test
    public void testSimpleCteCreation() {
        // WITH active_users AS (SELECT * FROM users WHERE status = 'active')
        // SELECT * FROM active_users
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
