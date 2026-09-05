package com.github.paohaijiao.distribute.nodeExecutor;

import com.github.paohaijiao.ast.*;
import com.github.paohaijiao.ast2logic.JQuickASTToLogicalPlanVisitor;
import com.github.paohaijiao.distributed.JQuickDistributedPlan;
import com.github.paohaijiao.fragment.JQuickFragmenter;
import com.github.paohaijiao.logic.JQuickLogicalPlanNode;
import com.github.paohaijiao.logic2physical.JQuickPhysicalPlanGenerator;
import com.github.paohaijiao.physical.JQuickPhysicalPlanNode;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class JQuickPredicateTest {

    private final JQuickASTToLogicalPlanVisitor visitor = new JQuickASTToLogicalPlanVisitor();

    private JQuickPhysicalPlanGenerator generator= new JQuickPhysicalPlanGenerator();

    private JQuickFragmenter fragmenter = new JQuickFragmenter(4);

    private JQuickFragmenter verboseFragmenter = new JQuickFragmenter(8);

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
     * 创建 SELECT 子句（带 WHERE 条件）
     */
    private JQuickSelectClauseNode createSelectClauseWithWhere(JQuickSelectElementsNode selectElements, JQuickFromClauseNode fromClause, JQuickPredicateNode predicate) {
        JQuickFilterConditionNode filterCondition = new JQuickFilterConditionNode(predicate);
        JQuickWhereClauseNode whereClause = new JQuickWhereClauseNode(filterCondition);
        return new JQuickSelectClauseNode.Builder()
                .setSelectElements(selectElements)
                .setFromClause(fromClause)
                .setWhereClause(whereClause)
                .build();
    }

    /**
     * 创建简单的 SELECT *
     */
    private JQuickSelectElementsNode createSelectAll() {
        return new JQuickSelectElementsNode(true, null);
    }

    /**
     * 检查计划中是否存在 Filter 节点
     */
    private boolean isFilterNodePresent(JQuickLogicalPlanNode plan) {
        JQuickLogicalPlanNode current = plan;
        while (current != null) {
            if ("Filter".equals(current.getNodeType())) {
                return true;
            }
            if (current.getChildren() != null && !current.getChildren().isEmpty()) {
                current = current.getChildren().get(0);
            } else {
                break;
            }
        }
        return false;
    }
    /**
     * 测试 IS NULL 谓词
     * SQL: SELECT * FROM users u WHERE u.deleted_at IS NULL
     */
    @Test
    public void testIsNullPredicate() {
        JQuickExpressionAtomNode columnAtom = createColumnAtom("u", "deleted_at");
        JQuickPredicateNode innerPredicate = new JQuickPredicateNode(columnAtom);
        JQuickPredicateNode predicate = new JQuickPredicateNode(innerPredicate, false); // false = IS NULL
        JQuickSelectClauseNode selectClause = createSelectClauseWithWhere(          createSelectAll(),       createFromClause("users", "u"),      predicate  );
        JQuickLogicalPlanNode plan = visitor.visit(selectClause);
        assertNotNull(plan);
        assertTrue(isFilterNodePresent(plan));
        JQuickPhysicalPlanNode planNode= generator.generate(plan);
        System.out.println(planNode);
        JQuickDistributedPlan distributedPlan= fragmenter.fragment(planNode);
        fragmenter.printFragments(distributedPlan);
    }
    /**
     * 测试 IS NOT NULL 谓词
     * SQL: SELECT * FROM users u WHERE u.email IS NOT NULL
     */
    @Test
    public void testIsNotNullPredicate() {
        JQuickExpressionAtomNode columnAtom = createColumnAtom("u", "email");
        JQuickPredicateNode innerPredicate = new JQuickPredicateNode(columnAtom);
        JQuickPredicateNode predicate = new JQuickPredicateNode(innerPredicate, true); // true = IS NOT NULL
        JQuickSelectClauseNode selectClause = createSelectClauseWithWhere(createSelectAll(), createFromClause("users", "u"), predicate);
        JQuickLogicalPlanNode plan = visitor.visit(selectClause);
        assertNotNull(plan);
        assertTrue(isFilterNodePresent(plan));
        JQuickPhysicalPlanNode planNode= generator.generate(plan);
        System.out.println(planNode);
        JQuickDistributedPlan distributedPlan= fragmenter.fragment(planNode);
        fragmenter.printFragments(distributedPlan);
    }
    /**
     * 测试大于 (>) 比较谓词
     * SQL: SELECT * FROM products p WHERE p.price > 100
     */
    @Test
    public void testGtComparisonPredicate() {
        JQuickPredicateNode left = new JQuickPredicateNode(createColumnAtom("p", "price"));
        JQuickPredicateNode right = new JQuickPredicateNode(createConstantAtom(100, JQuickConstantNode.ConstantType.DECIMAL));
        JQuickPredicateNode predicate = new JQuickPredicateNode(left, right, JQuickPredicateNode.ComparisonOperator.GT);
        JQuickSelectClauseNode selectClause = createSelectClauseWithWhere(createSelectAll(), createFromClause("products", "p"), predicate);
        JQuickLogicalPlanNode plan = visitor.visit(selectClause);
        assertNotNull(plan);
        assertTrue(isFilterNodePresent(plan));
        JQuickPhysicalPlanNode planNode= generator.generate(plan);
        System.out.println(planNode);
        JQuickDistributedPlan distributedPlan= fragmenter.fragment(planNode);
        fragmenter.printFragments(distributedPlan);
    }
    /**
     * 测试 BETWEEN 谓词
     * SQL: SELECT * FROM products p WHERE p.price BETWEEN 100 AND 500
     */
    @Test
    public void testBetweenPredicate() {
        JQuickPredicateNode target = new JQuickPredicateNode(createColumnAtom("p", "price"));
        JQuickPredicateNode low = new JQuickPredicateNode(createConstantAtom(100, JQuickConstantNode.ConstantType.DECIMAL));
        JQuickPredicateNode high = new JQuickPredicateNode(createConstantAtom(500, JQuickConstantNode.ConstantType.DECIMAL));
        JQuickPredicateNode predicate = new JQuickPredicateNode(target, false, low, high);
        JQuickSelectClauseNode selectClause = createSelectClauseWithWhere(
                createSelectAll(),
                createFromClause("products", "p"),
                predicate
        );
        JQuickLogicalPlanNode plan = visitor.visit(selectClause);
        assertNotNull(plan);
        assertTrue(isFilterNodePresent(plan));
        JQuickPhysicalPlanNode planNode= generator.generate(plan);
        System.out.println(planNode);
        JQuickDistributedPlan distributedPlan= fragmenter.fragment(planNode);
        fragmenter.printFragments(distributedPlan);
    }
    /**
     * 测试 NOT BETWEEN 谓词
     * SQL: SELECT * FROM products p WHERE p.price NOT BETWEEN 100 AND 500
     */
    @Test
    public void testNotBetweenPredicate() {
        JQuickPredicateNode target = new JQuickPredicateNode(createColumnAtom("p", "price"));
        JQuickPredicateNode low = new JQuickPredicateNode(createConstantAtom(100, JQuickConstantNode.ConstantType.DECIMAL));
        JQuickPredicateNode high = new JQuickPredicateNode(createConstantAtom(500, JQuickConstantNode.ConstantType.DECIMAL));
        JQuickPredicateNode predicate = new JQuickPredicateNode(target, true, low, high);
        JQuickSelectClauseNode selectClause = createSelectClauseWithWhere(
                createSelectAll(),
                createFromClause("products", "p"),
                predicate
        );
        JQuickLogicalPlanNode plan = visitor.visit(selectClause);
        assertNotNull(plan);
        assertTrue(isFilterNodePresent(plan));
        JQuickPhysicalPlanNode planNode= generator.generate(plan);
        System.out.println(planNode);
        JQuickDistributedPlan distributedPlan= fragmenter.fragment(planNode);
        fragmenter.printFragments(distributedPlan);

    }
    /**
     * 测试 IN 谓词（值列表）
     * SQL: SELECT * FROM users u WHERE u.status IN ('active', 'pending', 'verified')
     */
    @Test
    public void testInPredicateWithValues() {
        JQuickPredicateNode target = new JQuickPredicateNode(createColumnAtom("u", "status"));
        List<JQuickExpressionNode> values = new ArrayList<>();
        values.add(createExpressionNode(createConstantAtom("active", JQuickConstantNode.ConstantType.STRING)));
        values.add(createExpressionNode(createConstantAtom("pending", JQuickConstantNode.ConstantType.STRING)));
        values.add(createExpressionNode(createConstantAtom("verified", JQuickConstantNode.ConstantType.STRING)));
        JQuickExpressionsNode expressions = new JQuickExpressionsNode(values);
        JQuickPredicateNode predicate = new JQuickPredicateNode(target, false, expressions);
        JQuickSelectClauseNode selectClause = createSelectClauseWithWhere(
                createSelectAll(),
                createFromClause("users", "u"),
                predicate
        );
        JQuickLogicalPlanNode plan = visitor.visit(selectClause);
        assertNotNull(plan);
        assertTrue(isFilterNodePresent(plan));
        JQuickPhysicalPlanNode planNode= generator.generate(plan);
        System.out.println(planNode);
        JQuickDistributedPlan distributedPlan= fragmenter.fragment(planNode);
        fragmenter.printFragments(distributedPlan);
    }

    /**
     * 测试 NOT IN 谓词
     * SQL: SELECT * FROM products p WHERE p.category NOT IN ('discontinued', 'archived')
     */
    @Test
    public void testNotInPredicateWithValues() {
        JQuickPredicateNode target = new JQuickPredicateNode(createColumnAtom("p", "category"));
        List<JQuickExpressionNode> values = new ArrayList<>();
        values.add(createExpressionNode(createConstantAtom("discontinued", JQuickConstantNode.ConstantType.STRING)));
        values.add(createExpressionNode(createConstantAtom("archived", JQuickConstantNode.ConstantType.STRING)));
        JQuickExpressionsNode expressions = new JQuickExpressionsNode(values);
        JQuickPredicateNode predicate = new JQuickPredicateNode(target, true, expressions);
        JQuickSelectClauseNode selectClause = createSelectClauseWithWhere(
                createSelectAll(),
                createFromClause("products", "p"),
                predicate
        );
        JQuickLogicalPlanNode plan = visitor.visit(selectClause);
        assertNotNull(plan);
        assertTrue(isFilterNodePresent(plan));
        JQuickPhysicalPlanNode planNode= generator.generate(plan);
        System.out.println(planNode);
        JQuickDistributedPlan distributedPlan= fragmenter.fragment(planNode);
        fragmenter.printFragments(distributedPlan);
    }
    /**
     * 测试 LIKE 谓词
     * SQL: SELECT * FROM users u WHERE u.name LIKE 'John%'
     */
    @Test
    public void testLikePredicate() {
        JQuickPredicateNode target = new JQuickPredicateNode(createColumnAtom("u", "name"));
        JQuickPredicateNode pattern = new JQuickPredicateNode(createConstantAtom("John%", JQuickConstantNode.ConstantType.STRING));
        JQuickPredicateNode predicate = new JQuickPredicateNode(target, false, pattern, JQuickPredicateNode.PredicateSubType.LIKE);
        JQuickSelectClauseNode selectClause = createSelectClauseWithWhere(createSelectAll(), createFromClause("users", "u"), predicate);
        JQuickLogicalPlanNode plan = visitor.visit(selectClause);
        assertNotNull(plan);
        assertTrue(isFilterNodePresent(plan));
        JQuickPhysicalPlanNode planNode= generator.generate(plan);
        System.out.println(planNode);
        JQuickDistributedPlan distributedPlan= fragmenter.fragment(planNode);
        fragmenter.printFragments(distributedPlan);
    }
    /**
     * 测试 LIKE 谓词
     * SQL: SELECT * FROM users u WHERE u.name not LIKE 'John%'
     */
    @Test
    public void testLikePredicate1() {
        JQuickPredicateNode target = new JQuickPredicateNode(createColumnAtom("u", "name"));
        JQuickPredicateNode pattern = new JQuickPredicateNode(createConstantAtom("John%", JQuickConstantNode.ConstantType.STRING));
        JQuickPredicateNode predicate = new JQuickPredicateNode(target, true, pattern, JQuickPredicateNode.PredicateSubType.LIKE);
        JQuickSelectClauseNode selectClause = createSelectClauseWithWhere(createSelectAll(), createFromClause("users", "u"), predicate);
        JQuickLogicalPlanNode plan = visitor.visit(selectClause);
        assertNotNull(plan);
        assertTrue(isFilterNodePresent(plan));
        JQuickPhysicalPlanNode planNode= generator.generate(plan);
        System.out.println(planNode);
        JQuickDistributedPlan distributedPlan= fragmenter.fragment(planNode);
        fragmenter.printFragments(distributedPlan);
    }
    /**
     * 测试 REGEXP  谓词
     * SQL: SELECT * FROM users u WHERE u.name REGEXP  'John%'
     */
    @Test
    public void testREGEXPPredicate() {
        JQuickPredicateNode target = new JQuickPredicateNode(createColumnAtom("u", "name"));
        JQuickPredicateNode pattern = new JQuickPredicateNode(createConstantAtom("John", JQuickConstantNode.ConstantType.STRING));
        JQuickPredicateNode predicate = new JQuickPredicateNode(target, false, pattern, JQuickPredicateNode.PredicateSubType.REGEXP);
        JQuickSelectClauseNode selectClause = createSelectClauseWithWhere(createSelectAll(), createFromClause("users", "u"), predicate);
        JQuickLogicalPlanNode plan = visitor.visit(selectClause);
        assertNotNull(plan);
        assertTrue(isFilterNodePresent(plan));
        JQuickPhysicalPlanNode planNode= generator.generate(plan);
        System.out.println(planNode);
        JQuickDistributedPlan distributedPlan= fragmenter.fragment(planNode);
        fragmenter.printFragments(distributedPlan);
    }
    /**
     * 测试 LIKE 谓词
     * SQL: SELECT * FROM users u WHERE u.name not REGEXP 'John'
     */
    @Test
    public void testREGEXPPredicate1() {
        JQuickPredicateNode target = new JQuickPredicateNode(createColumnAtom("u", "name"));
        JQuickPredicateNode pattern = new JQuickPredicateNode(createConstantAtom("John", JQuickConstantNode.ConstantType.STRING));
        JQuickPredicateNode predicate = new JQuickPredicateNode(target, true, pattern, JQuickPredicateNode.PredicateSubType.REGEXP);
        JQuickSelectClauseNode selectClause = createSelectClauseWithWhere(createSelectAll(), createFromClause("users", "u"), predicate);
        JQuickLogicalPlanNode plan = visitor.visit(selectClause);
        assertNotNull(plan);
        assertTrue(isFilterNodePresent(plan));
        JQuickPhysicalPlanNode planNode= generator.generate(plan);
        System.out.println(planNode);
        JQuickDistributedPlan distributedPlan= fragmenter.fragment(planNode);
        fragmenter.printFragments(distributedPlan);
    }
    /**
     * 测试 EXISTS 谓词
     * SQL: SELECT * FROM users u WHERE EXISTS (SELECT 1 FROM orders o WHERE o.user_id = u.id)
     *
     * EXISTS 的子查询是一个 JQuickSelectStatementNode，需要包装在 JQuickExpressionNode 中
     */
    @Test
    public void testExistsPredicate() {
        // 构建子查询: SELECT 1 FROM orders o WHERE o.user_id = u.id
        JQuickSelectElementsNode subSelectElements = new JQuickSelectElementsNode(false, Arrays.asList(new JQuickSelectElementNode(createExpressionNode(createConstantAtom(1, JQuickConstantNode.ConstantType.DECIMAL)), null)));
        JQuickFromClauseNode subFromClause = createFromClause("orders", "o");
        // 子查询的 WHERE 条件: o.user_id = u.id
        JQuickPredicateNode subWherePredicate = new JQuickPredicateNode(
                new JQuickPredicateNode(createColumnAtom("o", "user_id")),
                new JQuickPredicateNode(createColumnAtom("u", "id")),
                JQuickPredicateNode.ComparisonOperator.EQ
        );
        JQuickFilterConditionNode subFilterCondition = new JQuickFilterConditionNode(subWherePredicate);
        JQuickWhereClauseNode subWhereClause = new JQuickWhereClauseNode(subFilterCondition);
        JQuickSelectClauseNode subSelectClause = new JQuickSelectClauseNode.Builder()
                .setSelectElements(subSelectElements)
                .setFromClause(subFromClause)
                .setWhereClause(subWhereClause)
                .build();
        JQuickDataSetOpNode subDataSetOp = new JQuickDataSetOpNode(Arrays.asList(subSelectClause), new ArrayList<>());
        JQuickSelectExpressionNode subSelectExpression = new JQuickSelectExpressionNode(subDataSetOp);
        JQuickSelectStatementNode subSelectStatement = new JQuickSelectStatementNode(subSelectExpression);
        // 将子查询包装为 ExpressionAtom -> Expression
        JQuickExpressionAtomNode subqueryAtom = new JQuickExpressionAtomNode(subSelectStatement);
        JQuickExpressionNode existsExpression = new JQuickExpressionNode(subqueryAtom);
        // EXISTS 谓词 - 使用 JQuickExpressionNode 参数
        JQuickPredicateNode predicate = new JQuickPredicateNode(existsExpression, true);
        JQuickSelectClauseNode selectClause = createSelectClauseWithWhere(
                createSelectAll(),
                createFromClause("users", "u"),
                predicate
        );
        JQuickLogicalPlanNode plan = visitor.visit(selectClause);
        assertNotNull(plan);
        JQuickPhysicalPlanNode planNode= generator.generate(plan);
        System.out.println(planNode);
        JQuickDistributedPlan distributedPlan= fragmenter.fragment(planNode);
        fragmenter.printFragments(distributedPlan);
    }




}
