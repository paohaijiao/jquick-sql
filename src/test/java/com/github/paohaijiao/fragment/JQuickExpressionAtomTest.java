package com.github.paohaijiao.fragment;

import com.github.paohaijiao.ast.*;
import com.github.paohaijiao.ast2logic.JQuickASTToLogicalPlanVisitor;
import com.github.paohaijiao.distributed.JQuickDistributedPlan;
import com.github.paohaijiao.logic.JQuickLogicalPlanNode;
import com.github.paohaijiao.logic2physical.JQuickPhysicalPlanGenerator;
import com.github.paohaijiao.physical.JQuickPhysicalPlanNode;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertNotNull;

public class JQuickExpressionAtomTest {

    private JQuickASTToLogicalPlanVisitor visitor;

    private JQuickPhysicalPlanGenerator generator= new JQuickPhysicalPlanGenerator();
    private JQuickFragmenter fragmenter;
    private JQuickFragmenter verboseFragmenter;


    @Before
    public void setUp() {
        visitor = new JQuickASTToLogicalPlanVisitor();
        fragmenter = new JQuickFragmenter(4);
        verboseFragmenter = new JQuickFragmenter(8);
    }
    /**
     * 创建 FROM 子句
     */
    private JQuickFromClauseNode createFromClause(String tableName, String alias) {
        return new JQuickFromClauseNode(new JQuickTableNameItemNode(tableName, alias));
    }

    /**
     * 创建列引用表达式原子
     */
    private JQuickExpressionAtomNode createColumnAtom(String columnName) {
        return new JQuickExpressionAtomNode(new JQuickFullColumnNameNode(columnName, null));
    }

    /**
     * 创建列引用表达式原子（带表别名）
     */
    private JQuickExpressionAtomNode createColumnAtom(String tableAlias, String columnName) {
        return new JQuickExpressionAtomNode(new JQuickFullColumnNameNode(tableAlias, columnName));
    }

    /**
     * 创建常量表达式原子
     */
    private JQuickExpressionAtomNode createConstantAtom(Object value, JQuickConstantNode.ConstantType type) {
        return new JQuickExpressionAtomNode(new JQuickConstantNode(value, type));
    }

    /**
     * 创建表达式节点
     */
    private JQuickExpressionNode createExpressionNode(JQuickExpressionAtomNode atom) {
        return new JQuickExpressionNode(atom);
    }

    /**
     * 创建 SELECT 元素
     */
    private JQuickSelectElementNode createSelectElement(JQuickExpressionNode expression, String alias) {
        return new JQuickSelectElementNode(expression, alias);
    }

    /**
     * 创建简单的 SELECT 子句
     */
    private JQuickSelectClauseNode createSelectClause(JQuickSelectElementsNode selectElements, JQuickFromClauseNode fromClause) {
        return new JQuickSelectClauseNode.Builder()
                .setSelectElements(selectElements)
                .setFromClause(fromClause)
                .build();
    }
    /**
     * 测试日期常量
     * SQL: SELECT  '2024-01-01' AS start_date
     */
    @Test
    public void testDateConstant() {
        JQuickExpressionAtomNode constantAtom = createConstantAtom("2024-01-01", JQuickConstantNode.ConstantType.DATE);
        JQuickExpressionNode expr = createExpressionNode(constantAtom);
        JQuickSelectElementNode selectElement = createSelectElement(expr, "start_date");
        JQuickSelectElementsNode selectElements = new JQuickSelectElementsNode(false, Arrays.asList(selectElement));
        JQuickFromClauseNode fromClause = createFromClause("dual", null);
        JQuickSelectClauseNode selectClause = createSelectClause(selectElements, fromClause);
        JQuickLogicalPlanNode plan = visitor.visit(selectClause);
        assertNotNull(plan);
        JQuickPhysicalPlanNode planNode= generator.generate(plan);
        JQuickDistributedPlan distributedPlan= fragmenter.fragment(planNode);
//        distributedPlan.printPlan();
        fragmenter.printFragments(distributedPlan);
    }

    /**
     * 测试带表别名的列引用
     * SQL: SELECT u.name FROM users u
     */
    @Test
    public void testColumnReferenceWithAlias() {
        JQuickExpressionAtomNode columnAtom = createColumnAtom("u", "name");
        JQuickExpressionNode expr = createExpressionNode(columnAtom);
        JQuickSelectElementNode selectElement = createSelectElement(expr, "name");
        JQuickSelectElementsNode selectElements = new JQuickSelectElementsNode(false, Arrays.asList(selectElement));
        JQuickFromClauseNode fromClause = createFromClause("users", "u");
        JQuickSelectClauseNode selectClause = createSelectClause(selectElements, fromClause);
        JQuickLogicalPlanNode plan = visitor.visit(selectClause);
        assertNotNull(plan);
        JQuickPhysicalPlanNode planNode= generator.generate(plan);
        JQuickDistributedPlan distributedPlan= fragmenter.fragment(planNode);
//        distributedPlan.printPlan();
        fragmenter.printFragments(distributedPlan);
    }
    /**
     * 测试函数调用
     * SQL: SELECT UPPER(name) AS upper_name FROM users
     */
    @Test
    public void testFunctionCall() {
        JQuickFunctionCallNode functionCall = new JQuickFunctionCallNode("UPPER", Arrays.asList(new JQuickFunctionArgNode(createExpressionNode(createColumnAtom("name")))), false);
        JQuickExpressionAtomNode functionAtom = new JQuickExpressionAtomNode(functionCall);
        JQuickExpressionNode expr = createExpressionNode(functionAtom);
        JQuickSelectElementNode selectElement = createSelectElement(expr, "upper_name");
        JQuickSelectElementsNode selectElements = new JQuickSelectElementsNode(false, Arrays.asList(selectElement));
        JQuickFromClauseNode fromClause = createFromClause("users", null);
        JQuickSelectClauseNode selectClause = createSelectClause(selectElements, fromClause);
        JQuickLogicalPlanNode plan = visitor.visit(selectClause);
        assertNotNull(plan);
        JQuickPhysicalPlanNode planNode= generator.generate(plan);
        JQuickDistributedPlan distributedPlan= fragmenter.fragment(planNode);
//        distributedPlan.printPlan();
        fragmenter.printFragments(distributedPlan);
    }
    /**
     * 测试多层嵌套表达式
     * SQL: SELECT ((1 + 2) * 3) AS result
     */
    @Test
    public void testMultipleNestedExpression() {
        // 内层: 1 + 2
        JQuickExpressionAtomNode left1 = createConstantAtom(1, JQuickConstantNode.ConstantType.DECIMAL);
        JQuickExpressionAtomNode right1 = createConstantAtom(2, JQuickConstantNode.ConstantType.DECIMAL);
        JQuickExpressionAtomNode innerMath = new JQuickExpressionAtomNode(left1, right1, JQuickExpressionAtomNode.MathOperator.PLUS);
        JQuickExpressionNode innerExpr = createExpressionNode(innerMath);
        // 中层: (1 + 2) 作为嵌套
        List<JQuickExpressionNode> middleNested = Arrays.asList(innerExpr);
        JQuickExpressionAtomNode middleAtom = new JQuickExpressionAtomNode(middleNested);
        JQuickExpressionNode middleExpr = createExpressionNode(middleAtom);
        // 外层: (1 + 2) * 3
        JQuickExpressionAtomNode outerRight = createConstantAtom(3, JQuickConstantNode.ConstantType.DECIMAL);
        JQuickExpressionAtomNode outerMath = new JQuickExpressionAtomNode(middleAtom, outerRight, JQuickExpressionAtomNode.MathOperator.MULTIPLY);
        JQuickExpressionNode outerExpr = createExpressionNode(outerMath);
        // 最外层嵌套
        List<JQuickExpressionNode> outerNested = Arrays.asList(outerExpr);
        JQuickExpressionAtomNode finalAtom = new JQuickExpressionAtomNode(outerNested);
        JQuickExpressionNode finalExpr = createExpressionNode(finalAtom);
        JQuickSelectElementNode selectElement = createSelectElement(finalExpr, "result");
        JQuickSelectElementsNode selectElements = new JQuickSelectElementsNode(false, Arrays.asList(selectElement));
        JQuickFromClauseNode fromClause = createFromClause("dual", null);
        JQuickSelectClauseNode selectClause = createSelectClause(selectElements, fromClause);
        JQuickLogicalPlanNode plan = visitor.visit(selectClause);
        assertNotNull(plan);
        JQuickPhysicalPlanNode planNode= generator.generate(plan);
        System.out.println(planNode);
        JQuickDistributedPlan distributedPlan= fragmenter.fragment(planNode);
//        distributedPlan.printPlan();
        fragmenter.printFragments(distributedPlan);
    }

    /**
     * 测试子查询表达式
     * SQL: SELECT (SELECT COUNT(*) FROM orders) AS order_count
     */
    @Test
    public void testSubqueryExpression() {
        // 构建子查询: SELECT COUNT(*) FROM orders
        JQuickFunctionCallNode countFunc = new JQuickFunctionCallNode("COUNT", null, true);
        JQuickExpressionNode countExpr = new JQuickExpressionNode(new JQuickExpressionAtomNode(countFunc));
        JQuickSelectElementNode subSelectElement = new JQuickSelectElementNode(countExpr, null);
        JQuickSelectElementsNode subSelectElements = new JQuickSelectElementsNode(false, Arrays.asList(subSelectElement));
        JQuickFromClauseNode subFromClause = createFromClause("orders", null);
        JQuickSelectClauseNode subSelectClause = new JQuickSelectClauseNode.Builder()
                .setSelectElements(subSelectElements)
                .setFromClause(subFromClause)
                .build();

        JQuickDataSetOpNode subDataSetOp = new JQuickDataSetOpNode(Arrays.asList(subSelectClause), new ArrayList<>());
        JQuickSelectExpressionNode subSelectExpression = new JQuickSelectExpressionNode(subDataSetOp);
        JQuickSelectStatementNode subSelectStatement = new JQuickSelectStatementNode(subSelectExpression);
        // 子查询原子
        JQuickExpressionAtomNode subqueryAtom = new JQuickExpressionAtomNode(subSelectStatement);
        JQuickExpressionNode expr = createExpressionNode(subqueryAtom);
        JQuickSelectElementNode selectElement = createSelectElement(expr, "order_count");
        JQuickSelectElementsNode selectElements = new JQuickSelectElementsNode(false, Arrays.asList(selectElement));
        JQuickFromClauseNode fromClause = createFromClause("dual", null);
        JQuickSelectClauseNode selectClause = createSelectClause(selectElements, fromClause);
        JQuickLogicalPlanNode plan = visitor.visit(selectClause);
        assertNotNull(plan);
        JQuickPhysicalPlanNode planNode= generator.generate(plan);
        System.out.println(planNode);
        JQuickDistributedPlan distributedPlan= fragmenter.fragment(planNode);
//        distributedPlan.printPlan();
        fragmenter.printFragments(distributedPlan);
    }
    /**
     * 测试 NOT 一元运算
     * SQL: SELECT NOT is_active AS inactive FROM users
     */
    @Test
    public void testUnaryNot() {
        JQuickExpressionAtomNode columnAtom = createColumnAtom("is_active");
        JQuickExpressionAtomNode unaryAtom = new JQuickExpressionAtomNode(JQuickExpressionAtomNode.UnaryOperator.NOT, columnAtom);
        JQuickExpressionNode expr = createExpressionNode(unaryAtom);
        JQuickSelectElementNode selectElement = createSelectElement(expr, "inactive");
        JQuickSelectElementsNode selectElements = new JQuickSelectElementsNode(false, Arrays.asList(selectElement));
        JQuickFromClauseNode fromClause = createFromClause("users", null);
        JQuickSelectClauseNode selectClause = createSelectClause(selectElements, fromClause);
        JQuickLogicalPlanNode plan = visitor.visit(selectClause);
        assertNotNull(plan);
        JQuickPhysicalPlanNode planNode= generator.generate(plan);
        System.out.println(planNode);
        JQuickDistributedPlan distributedPlan= fragmenter.fragment(planNode);
//        distributedPlan.printPlan();
        fragmenter.printFragments(distributedPlan);
    }
    /**
     * 测试多种 ExpressionAtom 类型混合
     * SQL: SELECT (price * 0.9) + UPPER(name) - CASE WHEN status = 1 THEN 10 ELSE 5 END FROM products
     */
    @Test
    public void testMixedExpressionAtoms() {
        // price * 0.9
        JQuickExpressionAtomNode price = createColumnAtom("price");
        JQuickExpressionAtomNode discount = createConstantAtom(0.9, JQuickConstantNode.ConstantType.DECIMAL);
        JQuickExpressionAtomNode multiplied = new JQuickExpressionAtomNode(price, discount, JQuickExpressionAtomNode.MathOperator.MULTIPLY);
        JQuickFunctionCallNode upperFunc = new JQuickFunctionCallNode("UPPER", Arrays.asList(new JQuickFunctionArgNode(createExpressionNode(createColumnAtom("name")))), false);
        JQuickExpressionAtomNode upperAtom = new JQuickExpressionAtomNode(upperFunc);
        // CASE WHEN status = 1 THEN 10 ELSE 5 END
        List<JQuickCaseWhenNode.WhenClause> whenClauses = new ArrayList<>();
        JQuickPredicateNode condition = new JQuickPredicateNode(new JQuickPredicateNode(createColumnAtom("status")), new JQuickPredicateNode(createConstantAtom(1, JQuickConstantNode.ConstantType.DECIMAL)), JQuickPredicateNode.ComparisonOperator.EQ);
        JQuickExpressionNode thenResult = createExpressionNode(createConstantAtom(10, JQuickConstantNode.ConstantType.DECIMAL));
        whenClauses.add(new JQuickCaseWhenNode.WhenClause(condition, thenResult));
        JQuickExpressionNode elseResult = createExpressionNode(createConstantAtom(5, JQuickConstantNode.ConstantType.DECIMAL));
        JQuickCaseWhenNode caseWhenNode = new JQuickCaseWhenNode(null, whenClauses, elseResult);
        JQuickExpressionAtomNode caseWhenAtom = new JQuickExpressionAtomNode(caseWhenNode);
        // 组合: (price * 0.9) + UPPER(name) - caseWhen
        JQuickExpressionAtomNode sum1 = new JQuickExpressionAtomNode(multiplied, upperAtom, JQuickExpressionAtomNode.MathOperator.PLUS);
        JQuickExpressionAtomNode finalExpr = new JQuickExpressionAtomNode(sum1, caseWhenAtom, JQuickExpressionAtomNode.MathOperator.MINUS);
        JQuickExpressionNode expr = createExpressionNode(finalExpr);
        JQuickSelectElementNode selectElement = createSelectElement(expr, "calculated");
        JQuickSelectElementsNode selectElements = new JQuickSelectElementsNode(false, Arrays.asList(selectElement));
        JQuickFromClauseNode fromClause = createFromClause("products", null);
        JQuickSelectClauseNode selectClause = createSelectClause(selectElements, fromClause);
        JQuickLogicalPlanNode plan = visitor.visit(selectClause);
        assertNotNull(plan);
        JQuickPhysicalPlanNode planNode= generator.generate(plan);
        System.out.println(planNode);
        JQuickDistributedPlan distributedPlan= fragmenter.fragment(planNode);
//        distributedPlan.printPlan();
        fragmenter.printFragments(distributedPlan);
    }

}
