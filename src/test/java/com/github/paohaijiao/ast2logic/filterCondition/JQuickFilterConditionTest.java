package com.github.paohaijiao.ast2logic.filterCondition;

import com.github.paohaijiao.ast.*;
import com.github.paohaijiao.ast2logic.JQuickASTToLogicalPlanVisitor;
import com.github.paohaijiao.logic.JQuickLogicalPlanNode;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertNotNull;

public class JQuickFilterConditionTest {

    private final JQuickASTToLogicalPlanVisitor visitor = new JQuickASTToLogicalPlanVisitor();
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
     * 创建简单比较谓词
     * @param left 左表达式原子
     * @param right 右表达式原子
     * @param op 比较操作符
     */
    private JQuickPredicateNode createComparisonPredicate(JQuickExpressionAtomNode left, JQuickExpressionAtomNode right,
                                                          JQuickPredicateNode.ComparisonOperator op) {
        JQuickPredicateNode leftPred = new JQuickPredicateNode(left);
        JQuickPredicateNode rightPred = new JQuickPredicateNode(right);
        return new JQuickPredicateNode(leftPred, rightPred, op);
    }

    /**
     * 创建 IS NULL 谓词
     */
    private JQuickPredicateNode createIsNullPredicate(JQuickExpressionAtomNode expr, boolean isNotNull) {
        JQuickPredicateNode pred = new JQuickPredicateNode(expr);
        return new JQuickPredicateNode(pred, isNotNull);
    }

    /**
     * 创建 BETWEEN 谓词
     */
    private JQuickPredicateNode createBetweenPredicate(JQuickExpressionAtomNode target,
                                                       JQuickExpressionAtomNode low,
                                                       JQuickExpressionAtomNode high,
                                                       boolean not) {
        JQuickPredicateNode targetPred = new JQuickPredicateNode(target);
        JQuickPredicateNode lowPred = new JQuickPredicateNode(low);
        JQuickPredicateNode highPred = new JQuickPredicateNode(high);
        return new JQuickPredicateNode(targetPred, not, lowPred, highPred);
    }

    /**
     * 创建 IN 谓词（值列表）
     */
    private JQuickPredicateNode createInPredicateWithValues(JQuickExpressionAtomNode target,
                                                            List<JQuickExpressionNode> values,
                                                            boolean not) {
        JQuickPredicateNode targetPred = new JQuickPredicateNode(target);
        JQuickExpressionsNode expressions = new JQuickExpressionsNode(values);
        return new JQuickPredicateNode(targetPred, not, expressions);
    }

    /**
     * 创建 LIKE 谓词
     */
    private JQuickPredicateNode createLikePredicate(JQuickExpressionAtomNode target,
                                                    JQuickExpressionAtomNode pattern,
                                                    boolean not) {
        JQuickPredicateNode targetPred = new JQuickPredicateNode(target);
        JQuickPredicateNode patternPred = new JQuickPredicateNode(pattern);
        return new JQuickPredicateNode(targetPred, not, patternPred);
    }

    /**
     * 创建过滤条件节点（谓词）
     */
    private JQuickFilterConditionNode createFilterCondition(JQuickPredicateNode predicate) {
        return new JQuickFilterConditionNode(predicate);
    }

    /**
     * 创建二元逻辑运算过滤条件
     */
    private JQuickFilterConditionNode createBinaryFilterCondition(JQuickFilterConditionNode left, JQuickFilterConditionNode right, JQuickFilterConditionNode.LogicalOperator op) {
        return new JQuickFilterConditionNode(left, right, op);
    }

    /**
     * 创建括号表达式过滤条件
     */
    private JQuickFilterConditionNode createParenFilterCondition(JQuickFilterConditionNode inner) {
        return new JQuickFilterConditionNode(inner, true);
    }

    /**
     * 创建 SELECT 子句（带 WHERE 条件）
     */
    private JQuickSelectClauseNode createSelectClauseWithWhere(JQuickSelectElementsNode selectElements, JQuickFromClauseNode fromClause, JQuickFilterConditionNode whereCondition) {
        JQuickWhereClauseNode whereClause = new JQuickWhereClauseNode(whereCondition);
        return new JQuickSelectClauseNode.Builder()
                .setSelectElements(selectElements)
                .setFromClause(fromClause)
                .setWhereClause(whereClause)
                .build();
    }

    /**
     * 测试等于 (=) 谓词
     * SQL: SELECT * FROM users u WHERE u.id = 1
     */
    private JQuickSelectElementsNode createSelectAll() {
        return new JQuickSelectElementsNode(true, null);
    }
    @Test
    public void testEqPredicate() {
        JQuickExpressionAtomNode left = createColumnAtom("u", "id");
        JQuickExpressionAtomNode right = createConstantAtom(1, JQuickConstantNode.ConstantType.DECIMAL);
        JQuickPredicateNode predicate = createComparisonPredicate(left, right, JQuickPredicateNode.ComparisonOperator.EQ);
        JQuickFilterConditionNode filterCondition = createFilterCondition(predicate);
        JQuickSelectClauseNode selectClause = createSelectClauseWithWhere(
                createSelectAll(),
                createFromClause("users", "u"),
                filterCondition
        );
        JQuickLogicalPlanNode plan = visitor.visit(selectClause);
        assertNotNull(plan);
    }
    /**
     * 测试 AND 逻辑运算
     * SQL: SELECT * FROM users u WHERE u.age >= 18 AND u.status = 'active'
     */
    @Test
    public void testAndOperator() {
        // 条件1: age >= 18
        JQuickPredicateNode cond1 = createComparisonPredicate(createColumnAtom("u", "age"), createConstantAtom(18, JQuickConstantNode.ConstantType.DECIMAL), JQuickPredicateNode.ComparisonOperator.GE);
        // 条件2: status = 'active'
        JQuickPredicateNode cond2 = createComparisonPredicate(createColumnAtom("u", "status"), createConstantAtom("active", JQuickConstantNode.ConstantType.STRING), JQuickPredicateNode.ComparisonOperator.EQ);
        JQuickFilterConditionNode leftCond = createFilterCondition(cond1);
        JQuickFilterConditionNode rightCond = createFilterCondition(cond2);
        JQuickFilterConditionNode andCondition = createBinaryFilterCondition(leftCond, rightCond, JQuickFilterConditionNode.LogicalOperator.AND);
        JQuickSelectClauseNode selectClause = createSelectClauseWithWhere(createSelectAll(), createFromClause("users", "u"), andCondition);
        JQuickLogicalPlanNode plan = visitor.visit(selectClause);
        assertNotNull(plan);
    }
    /**
     * 测试 OR 逻辑运算
     * SQL: SELECT * FROM products p WHERE p.category = 'electronics' OR p.category = 'computers'
     */
    @Test
    public void testOrOperator() {
        JQuickPredicateNode cond1 = createComparisonPredicate(createColumnAtom("p", "category"), createConstantAtom("electronics", JQuickConstantNode.ConstantType.STRING), JQuickPredicateNode.ComparisonOperator.EQ);
        JQuickPredicateNode cond2 = createComparisonPredicate(createColumnAtom("p", "category"), createConstantAtom("computers", JQuickConstantNode.ConstantType.STRING), JQuickPredicateNode.ComparisonOperator.EQ);
        JQuickFilterConditionNode leftCond = createFilterCondition(cond1);
        JQuickFilterConditionNode rightCond = createFilterCondition(cond2);
        JQuickFilterConditionNode orCondition = createBinaryFilterCondition(leftCond, rightCond, JQuickFilterConditionNode.LogicalOperator.OR);
        JQuickSelectClauseNode selectClause = createSelectClauseWithWhere(createSelectAll(), createFromClause("products", "p"), orCondition);
        JQuickLogicalPlanNode plan = visitor.visit(selectClause);
        assertNotNull(plan);
    }
    /**
     * 测试嵌套括号表达式 - 复杂嵌套
     * SQL: SELECT * FROM users u WHERE ((u.age > 18) AND (u.status = 'active' OR u.sex = '女'))
     *
     * 表达式结构：
     * ((u.age > 18) AND (u.status = 'active' OR u.sex = '女'))
     *
     * 逻辑：
     * - 外层：AND 连接两个子条件
     * - 左子条件：(u.age > 18) - 括号包裹的比较条件
     * - 右子条件：(u.status = 'active' OR u.sex = '女') - 括号包裹的 OR 条件
     */
    @Test
    public void testComplexNestedParenExpression() {
        //u.age > 18 条件
        JQuickPredicateNode ageCondition = createComparisonPredicate(createColumnAtom("u", "age"), createConstantAtom(18, JQuickConstantNode.ConstantType.DECIMAL), JQuickPredicateNode.ComparisonOperator.GT);
        // 左子条件：(u.age > 18) - 外层括号
        JQuickFilterConditionNode leftParenCondition = createParenFilterCondition(createFilterCondition(ageCondition));
        // 构建 u.status = 'active' 条件
        JQuickPredicateNode statusCondition = createComparisonPredicate(
                createColumnAtom("u", "status"),
                createConstantAtom("active", JQuickConstantNode.ConstantType.STRING),
                JQuickPredicateNode.ComparisonOperator.EQ
        );
        // u.sex = '女'
        JQuickPredicateNode sexCondition = createComparisonPredicate(createColumnAtom("u", "sex"), createConstantAtom("女", JQuickConstantNode.ConstantType.STRING), JQuickPredicateNode.ComparisonOperator.EQ);

        // OR 条件：u.status = 'active' OR u.sex = '女'
        JQuickFilterConditionNode orCondition = createBinaryFilterCondition(
                createFilterCondition(statusCondition),
                createFilterCondition(sexCondition),
                JQuickFilterConditionNode.LogicalOperator.OR
        );

        // 右子条件：(u.status = 'active' OR u.sex = '女') - 外层括号
        JQuickFilterConditionNode rightParenCondition = createParenFilterCondition(orCondition);
        // 最终 AND 条件：(leftParenCondition) AND (rightParenCondition)
        JQuickFilterConditionNode finalAndCondition = createBinaryFilterCondition(
                leftParenCondition,
                rightParenCondition,
                JQuickFilterConditionNode.LogicalOperator.AND
        );

        // 匹配 SQL 中的双层括号
        JQuickFilterConditionNode outermostParen = createParenFilterCondition(finalAndCondition);
        JQuickSelectClauseNode selectClause = createSelectClauseWithWhere(
                createSelectAll(),
                createFromClause("users", "u"),
                outermostParen
        );
        JQuickLogicalPlanNode plan = visitor.visit(selectClause);
        assertNotNull(plan);}

    /**
     * 测试 IS NULL 谓词
     * SQL: SELECT * FROM users u WHERE u.deleted_at IS NULL
     */
    @Test
    public void testIsNullPredicate() {
        JQuickPredicateNode isNullPred = createIsNullPredicate(createColumnAtom("u", "deleted_at"), false);
        JQuickFilterConditionNode filterCondition = createFilterCondition(isNullPred);
        JQuickSelectClauseNode selectClause = createSelectClauseWithWhere(createSelectAll(), createFromClause("users", "u"), filterCondition);
        JQuickLogicalPlanNode plan = visitor.visit(selectClause);
        assertNotNull(plan);
    }


}
