package com.github.paohaijiao.ast2logic.functionCall;

import com.github.paohaijiao.ast.*;
import com.github.paohaijiao.ast2logic.JQuickASTToLogicalPlanVisitor;
import com.github.paohaijiao.logic.JQuickLogicalPlanNode;
import com.github.paohaijiao.logic.domain.JQuickProjectNode;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class JQuickFunctionCallTest {

    private final JQuickASTToLogicalPlanVisitor visitor = new JQuickASTToLogicalPlanVisitor();
    /**
     * 创建 FROM 子句
     */
    private JQuickFromClauseNode createFromClause(String tableName, String alias) {
        return new JQuickFromClauseNode(new JQuickTableNameItemNode(tableName, alias));
    }

    /**
     * 创建列引用表达式
     */
    private JQuickExpressionNode createColumnExpression(String columnName) {
        return new JQuickExpressionNode(
                new JQuickExpressionAtomNode(
                        new JQuickFullColumnNameNode(columnName, null)
                )
        );
    }

    /**
     * 创建列引用表达式（带表别名）
     */
    private JQuickExpressionNode createColumnExpression(String tableAlias, String columnName) {
        return new JQuickExpressionNode(
                new JQuickExpressionAtomNode(
                        new JQuickFullColumnNameNode(tableAlias, columnName)
                )
        );
    }

    /**
     * 创建常量表达式
     */
    private JQuickExpressionNode createConstantExpression(Object value, JQuickConstantNode.ConstantType type) {
        return new JQuickExpressionNode(
                new JQuickExpressionAtomNode(new JQuickConstantNode(value, type))
        );
    }

    /**
     * 创建标量函数调用表达式
     */
    private JQuickExpressionNode createScalarFunctionExpression(String functionName, JQuickExpressionNode arg) {
        List<JQuickFunctionArgNode> args = new ArrayList<>();
        args.add(new JQuickFunctionArgNode(arg));
        JQuickFunctionCallNode functionCall = new JQuickFunctionCallNode(functionName, args, false);
        return new JQuickExpressionNode(new JQuickExpressionAtomNode(functionCall));
    }

    /**
     * 创建标量函数调用表达式（多参数）
     */
    private JQuickExpressionNode createScalarFunctionExpression(String functionName, List<JQuickExpressionNode> args) {
        List<JQuickFunctionArgNode> argNodes = new ArrayList<>();
        for (JQuickExpressionNode arg : args) {
            argNodes.add(new JQuickFunctionArgNode(arg));
        }
        JQuickFunctionCallNode functionCall = new JQuickFunctionCallNode(functionName, argNodes, false);
        return new JQuickExpressionNode(new JQuickExpressionAtomNode(functionCall));
    }

    /**
     * 创建聚合函数调用表达式
     */
    private JQuickExpressionNode createAggregateFunctionExpression(String functionName, JQuickExpressionNode arg) {
        List<JQuickFunctionArgNode> args = new ArrayList<>();
        args.add(new JQuickFunctionArgNode(arg));
        JQuickFunctionCallNode functionCall = new JQuickFunctionCallNode(functionName, args, false);
        return new JQuickExpressionNode(new JQuickExpressionAtomNode(functionCall));
    }

    /**
     * 创建 COUNT(*) 表达式
     */
    private JQuickExpressionNode createCountStarExpression() {
        JQuickFunctionCallNode functionCall = new JQuickFunctionCallNode("COUNT", null, true);
        return new JQuickExpressionNode(new JQuickExpressionAtomNode(functionCall));
    }

    /**
     * 创建 SELECT 元素
     */
    private JQuickSelectElementNode createSelectElement(JQuickExpressionNode expression, String alias) {
        return new JQuickSelectElementNode(expression, alias);
    }

    /**
     * 创建 SELECT 子句
     */
    private JQuickSelectClauseNode createSelectClause(JQuickSelectElementsNode selectElements, JQuickFromClauseNode fromClause) {
        return new JQuickSelectClauseNode.Builder()
                .setSelectElements(selectElements)
                .setFromClause(fromClause)
                .build();
    }


    /**
     * 测试 UPPER 函数 - 转换为大写
     * SQL: SELECT UPPER(name) AS upper_name FROM users u
     */
    @Test
    public void testUpperFunction() {
        // 构造 AST
        JQuickExpressionNode upperExpr = createScalarFunctionExpression("UPPER", createColumnExpression("name"));
        JQuickSelectElementNode selectElement = createSelectElement(upperExpr, "upper_name");
        JQuickSelectElementsNode selectElements = new JQuickSelectElementsNode(false, Arrays.asList(selectElement));
        JQuickFromClauseNode fromClause = createFromClause("users", "u");
        JQuickSelectClauseNode selectClause = createSelectClause(selectElements, fromClause);
        JQuickLogicalPlanNode plan = visitor.visit(selectClause);
        assertNotNull(plan);
        assertEquals("Project", plan.getNodeType());
        JQuickProjectNode projectNode = (JQuickProjectNode) plan;
        assertEquals(1, projectNode.getSelectItems().size());
        JQuickProjectNode.SelectItem item = projectNode.getSelectItems().get(0);
        assertEquals("upper_name", item.getAlias());
        assertNotNull(item.getExpression());
    }

    /**
     * 测试 CONCAT 函数 - 字符串拼接（多参数）
     * SQL: SELECT CONCAT(first_name, ' ', last_name) AS full_name FROM users u
     */
    @Test
    public void testConcatFunction() {
        List<JQuickExpressionNode> args = new ArrayList<>();
        args.add(createColumnExpression("first_name"));
        args.add(createConstantExpression(" ", JQuickConstantNode.ConstantType.STRING));
        args.add(createColumnExpression("last_name"));
        JQuickExpressionNode concatExpr = createScalarFunctionExpression("CONCAT", args);
        JQuickSelectElementNode selectElement = createSelectElement(concatExpr, "full_name");
        JQuickSelectElementsNode selectElements = new JQuickSelectElementsNode(false, Arrays.asList(selectElement));
        JQuickFromClauseNode fromClause = createFromClause("users", "u");
        JQuickSelectClauseNode selectClause = createSelectClause(selectElements, fromClause);
        JQuickLogicalPlanNode plan = visitor.visit(selectClause);
        assertNotNull(plan);
        assertEquals("Project", plan.getNodeType());
        JQuickProjectNode projectNode = (JQuickProjectNode) plan;
        assertEquals("full_name", projectNode.getSelectItems().get(0).getAlias());
    }
    /**
     * 测试 SUM 函数
     * SQL: SELECT SUM(amount) AS total_amount FROM orders o
     */
    @Test
    public void testSumFunction() {
        JQuickExpressionNode sumExpr = createAggregateFunctionExpression("SUM", createColumnExpression("amount"));
        JQuickSelectElementNode selectElement = createSelectElement(sumExpr, "total_amount");
        JQuickSelectElementsNode selectElements = new JQuickSelectElementsNode(false, Arrays.asList(selectElement));
        JQuickFromClauseNode fromClause = createFromClause("orders", "o");
        JQuickSelectClauseNode selectClause = createSelectClause(selectElements, fromClause);
        JQuickLogicalPlanNode plan = visitor.visit(selectClause);
        assertNotNull(plan);
        assertTrue(plan.getNodeType().equals("GroupBy") || plan.getNodeType().equals("Aggregate") || plan.getNodeType().equals("Project"));
    }
}
