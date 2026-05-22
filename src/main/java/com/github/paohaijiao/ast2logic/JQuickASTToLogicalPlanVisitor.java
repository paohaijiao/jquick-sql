/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Copyright (c) [2025-2099] Martin (goudingcheng@gmail.com)
 */
package com.github.paohaijiao.ast2logic;


import com.github.paohaijiao.ast.*;
import com.github.paohaijiao.enums.*;
import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.expression.domain.*;
import com.github.paohaijiao.logic.JQuickLogicalPlanNode;
import com.github.paohaijiao.logic.domain.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * AST 到逻辑计划转换器
 * 将 AST 节点转换为纯描述性的逻辑计划节点
 */
public class JQuickASTToLogicalPlanVisitor {


    /**
     * 转换查询节点（最顶层入口）
     * @param node JQuickQueryNode - 整个SQL查询的根节点
     * @return JQuickLogicalPlanNode - 转换后的逻辑计划根节点
     * AST结构：Query -> SelectStatement
     */
    public JQuickLogicalPlanNode visit(JQuickQueryNode node) {
        return visit(node.getSelectStatement());
    }

    /**
     * 转换 SELECT 语句节点
     * @param node JQuickSelectStatementNode - SELECT语句节点
     * @return JQuickLogicalPlanNode - 逻辑计划节点
     * 处理两种类型：
     * 1. WITH CTE查询：SELECT ... FROM (WITH ...) 或 WITH ... SELECT ...
     * 2. 普通查询：直接SELECT
     */
    public JQuickLogicalPlanNode visit(JQuickSelectStatementNode node) {
        if (node.isCteQuery()) { // 判断是否为CTE查询（WITH子句）
            Map<String, JQuickLogicalPlanNode> cteMap = new LinkedHashMap<>();
            Map<String, JQuickLogicalPlanNode> recursiveCteMap = new LinkedHashMap<>();
            for (JQuickCommonTableExpressionNode cte : node.getCtes()) { // 遍历所有CTE定义
                if (cte.isRecursive()) {
                    JQuickLogicalPlanNode initialPlan = null;
                    JQuickLogicalPlanNode recursivePlan = null;
                    if (cte.getInitialQuery() != null) {
                        initialPlan = visit(cte.getInitialQuery());
                    }
                    if (cte.getRecursivePart() != null) {
                        recursivePlan = visit(cte.getRecursivePart());
                    }
                    JQuickLogicalPlanNode recursiveCtePlan = buildRecursiveCTEPlan(
                            cte.getName(),
                            cte.getColumnNames(),
                            initialPlan,
                            recursivePlan,
                            cte.isUnionAll()
                    );
                    recursiveCteMap.put(cte.getName(), recursiveCtePlan);

                } else if (cte.getQuery() != null) {
                    // 处理非递归CTE
                    JQuickLogicalPlanNode ctePlan = visit(cte.getQuery());
                    cteMap.put(cte.getName(), ctePlan);
                }
            }

            // 合并CTE Map（非递归CTE和递归CTE）
            Map<String, JQuickLogicalPlanNode> allCteMap = new LinkedHashMap<>();
            allCteMap.putAll(cteMap);
            allCteMap.putAll(recursiveCteMap);
            JQuickLogicalPlanNode mainPlan = visit(node.getSelectExpression());
            return new JQuickWithNode(mainPlan, allCteMap);
        } else { // 普通查询
            return visit(node.getSelectExpression());
        }
    }

    /**
     * 转换 SELECT 表达式节点
     * @param node JQuickSelectExpressionNode - SELECT表达式节点
     * @return JQuickLogicalPlanNode - 逻辑计划节点
     * SelectExpression 是 DataSetOp 的包装
     * DataSetOp 可能包含多个SELECT子句通过集合操作连接
     */
    public JQuickLogicalPlanNode visit(JQuickSelectExpressionNode node) {
        JQuickLogicalPlanNode logicalPlanNode= visit(node.getDataSetOp());
        return logicalPlanNode;
    }

    /**
     * 转换数据集操作节点（UNION/INTERSECT/MINUS/EXCEPT）
     * @param node JQuickDataSetOpNode - 数据集操作节点
     * @return JQuickLogicalPlanNode - 逻辑计划节点
     * 处理三种情况：
     * 1. 单个SELECT：直接返回SELECT的逻辑计划
     * 2. 多个SELECT通过UNION等连接：构建二叉树结构的集合操作
     */
    public JQuickLogicalPlanNode visit(JQuickDataSetOpNode node) {
        List<JQuickSelectClauseNode> clauses = node.getSelectClauses();
        if (clauses.size() == 1) {
            return visit(clauses.get(0));
        }
        JQuickLogicalPlanNode result = visit(clauses.get(0)); // 多个子查询的集合操作
        for (int i = 1; i < clauses.size(); i++) {
            JQuickLogicalPlanNode right = visit(clauses.get(i));
            JQuickSQLOperationType opType = node.getOperators().get(i - 1);
            result = new JQuickSetOperationNode(opType, result, right);
        }
        return result;
    }

    /**
     * 转换 SELECT 子句节点 - 核心转换逻辑
     * @param node JQuickSelectClauseNode - SELECT子句节点
     * @return JQuickLogicalPlanNode - 逻辑计划节点
     *
     * 转换顺序（从数据流向看是反的，但从构建顺序看是从底向上）：
     * 1. FROM + JOIN -> TableScan
     * 2. WHERE -> Filter
     * 3. GROUP BY -> GroupBy
     * 4. HAVING -> 附加到GroupBy
     * 5. SELECT -> Project
     * 6. ORDER BY -> Sort
     * 7. LIMIT -> Limit
     *
     * 逻辑计划的执行顺序是从叶子节点向上执行：
     * Limit
     *   Sort
     *     Project
     *       GroupBy
     *         Filter
     *           Join
     *             TableScan
     */
    public JQuickLogicalPlanNode visit(JQuickSelectClauseNode node) {
        JQuickLogicalPlanNode root = visit(node.getFromClause());        //数据来源的起点
        //处理 JOIN
        if (node.getJoinClauses() != null && !node.getJoinClauses().isEmpty()) {
            for (JQuickJoinClauseNode join : node.getJoinClauses()) {
                root = visitJoin(root, join);
            }
        }
        //处理 WHERE
        if (node.getWhereClause() != null) {
            JQuickExpression predicate = visit(node.getWhereClause().getFilterCondition());
            root = new JQuickFilterNode(predicate, root);
        }

        if (node.getGroupByClause() != null) { //处理 GROUP BY 和 HAVING
            List<JQuickExpression> groupKeys = visitExpressions(node.getGroupByClause().getExpressions());// 提取分组键
            List<JQuickGroupByNode.AggregateItem> aggregates = extractAggregates(node.getSelectElements());// 提取聚合函数
            JQuickExpression havingExpr = node.getHavingClause() != null ? visit(node.getHavingClause().getFilterCondition()) : null; // 处理 HAVING
            root = new JQuickGroupByNode(groupKeys, aggregates, root, havingExpr);// 创建分组节点
            List<JQuickProjectNode.SelectItem> selectItems = rebuildSelectItems(node.getSelectElements(), groupKeys, aggregates);// 重建投影（GROUP BY 后只能选择分组键和聚合结果）
            root = new JQuickProjectNode(selectItems, root, node.isDistinct());
        } else {//处理 SELECT 投影（无 GROUP BY）
            List<JQuickProjectNode.SelectItem> selectItems = visitSelectElements(node.getSelectElements());
            root = new JQuickProjectNode(selectItems, root, node.isDistinct());
        }
        if (node.getOrderByClause() != null) {//处理 ORDER BY
            List<JQuickSortNode.OrderByItem> orderByItems = visitOrderByClause(node.getOrderByClause());
            root = new JQuickSortNode(orderByItems, root);
        }
        if (node.getLimitClause() != null) {//处理 LIMIT
            root = visitLimit(node.getLimitClause(), root);
        }
        return root;
    }

    /**
     * 转换 FROM 子句
     * @param node JQuickFromClauseNode - FROM子句节点
     * @return JQuickLogicalPlanNode - 逻辑计划节点
     * FROM子句只包含一个表名项，直接转发
     */
    public JQuickLogicalPlanNode visit(JQuickFromClauseNode node) {
        return visit(node.getTableNameItem());
    }

    /**
     * 转换表名项（表或子查询）
     * @param node JQuickTableNameItemNode - 表名项节点
     * @return JQuickLogicalPlanNode - 逻辑计划节点
     * 处理两种类型：
     * 1. 普通表：创建TableScan节点
     * 2. 子查询：递归转换子查询为逻辑计划
     */
    public JQuickLogicalPlanNode visit(JQuickTableNameItemNode node) {
        if (node.isSubquery()) {// 子查询作为派生表
            JQuickLogicalPlanNode subPlan = visit(node.getSubquery());
            return subPlan;
        } else {// 普通表
            return new JQuickTableScanNode(node.getTableName(), node.getAlias());
        }
    }

    /**
     * 转换 JOIN 子句
     * @param left 左子树逻辑计划
     * @param join JOIN子句节点
     * @return JQuickLogicalPlanNode - Join节点
     * 创建Join节点，连接左右两个数据源
     */
    public JQuickLogicalPlanNode visitJoin(JQuickLogicalPlanNode left, JQuickJoinClauseNode join) {
        JQuickLogicalPlanNode right = visit(join.getTableNameItem());
        JQuickJoinType joinType = join.getJoinType();
        JQuickExpression condition = null;
        if (join.hasOnCondition()) {
            JQuickExpression leftExpr = new JQuickColumnRefExpression(join.getLeftColumn().getColumnName(), join.getLeftColumn().getTableAlias());
            JQuickExpression rightExpr = new JQuickColumnRefExpression(join.getRightColumn().getColumnName(), join.getRightColumn().getTableAlias());
            condition = new JQuickBinaryExpression(leftExpr, rightExpr, JQuickBinaryOperator.EQ);
        }
        return new JQuickJoinNode(joinType, left, right, condition);
    }

    /**
     * 转换 SELECT 元素列表
     * @param node JQuickSelectElementsNode - SELECT元素列表节点
     * @return List<JQuickProjectNode.SelectItem> - 投影项列表
     * 处理两种形式：
     * 1. SELECT * - 通配符选择所有列
     * 2. SELECT col1, col2, ... - 显式列出列
     */
    public List<JQuickProjectNode.SelectItem> visitSelectElements(JQuickSelectElementsNode node) {
        List<JQuickProjectNode.SelectItem> items = new ArrayList<>();
        if (node.isStar()) {
            // SELECT *
            items.add(JQuickProjectNode.SelectItem.star());
        } else {
            for (JQuickSelectElementNode elem : node.getSelectElements()) {
                items.add(visitSelectElement(elem));
            }
        }

        return items;
    }

    /**
     * 转换单个 SELECT 元素
     * @param node JQuickSelectElementNode - 单个SELECT元素节点
     * @return JQuickProjectNode.SelectItem - 投影项
     * 处理：expression AS alias
     * 例如：SELECT id AS user_id, name
     */
    public JQuickProjectNode.SelectItem visitSelectElement(JQuickSelectElementNode node) {
        JQuickExpression expr = visitExpression(node.getExpression());
        String alias = node.getAlias();
        return new JQuickProjectNode.SelectItem(expr, alias);
    }

    /**
     * 转换表达式
     * @param node JQuickExpressionNode - 表达式节点
     * @return JQuickExpression - 逻辑计划表达式
     * 处理四种表达式类型：
     * 1. 原子表达式（常量、列、函数调用等）
     * 2. 括号表达式
     * 3. NOT表达式（逻辑非）
     * 4. SELECT子查询表达式
     */
    public JQuickExpression visitExpression(JQuickExpressionNode node) {
        switch (node.getType()) {
            case ATOM:
                return visitExpressionAtom(node.getExpressionAtom());
            case PAREN:
                return visitExpression(node.getInnerExpression());
            case NOT:
                JQuickExpression inner = visitExpression(node.getInnerExpression());
                return new JQuickUnaryExpression(JQuickUnaryOperator.NOT, inner);
            case SELECT:
                // 子查询作为标量值
                JQuickLogicalPlanNode subPlan = visit(node.getSelectClause());
                return new JQuickSubqueryExpression(subPlan);
            default:
                throw new RuntimeException("Unknown expression type: " + node.getType());
        }
    }

    /**
     * 转换表达式原子
     * @param node JQuickExpressionAtomNode - 表达式原子节点
     * @return JQuickExpression - 逻辑计划表达式
     * 处理所有原子表达式类型：
     * 1. CONSTANT - 常量（数字、字符串、布尔、NULL、日期）
     * 2. COLUMN - 列引用（如：users.id 或 name）
     * 3. FUNCTION - 函数调用（如：COUNT(*), UPPER(name)）
     * 4. NESTED - 嵌套表达式（如：(1+2)）
     * 5. SUBQUERY - 子查询
     * 6. MATH - 数学运算（+、-、*、/、%）
     * 7. UNARY - 一元运算（!、~、+、-）
     */
    public JQuickExpression visitExpressionAtom(JQuickExpressionAtomNode node) {
        switch (node.getType()) {
            case CONSTANT:
                return visitConstant(node.getConstant());
            case COLUMN:
                return visitColumn(node.getFullColumnName());
            case FUNCTION:
                return visitFunction(node.getFunctionCall());
            case NESTED:
                // 嵌套表达式，取第一个
                if (node.getNestedExpressions() != null && !node.getNestedExpressions().isEmpty()) {
                    return visitExpression(node.getNestedExpressions().get(0));
                }
                throw new RuntimeException("Empty nested expression");
            case SUBQUERY:
                JQuickLogicalPlanNode subPlan = visit(node.getSubquery());
                return new JQuickSubqueryExpression(subPlan);
            case MATH:
                JQuickExpression left = visitExpressionAtom(node.getLeft());
                JQuickExpression right = visitExpressionAtom(node.getRight());
                JQuickBinaryOperator mathOp = convertMathOperator(node.getMathOperator());
                return new JQuickBinaryExpression(left, right, mathOp);
            case UNARY:
                JQuickExpression operand = visitExpressionAtom(node.getLeft());
                JQuickUnaryOperator unaryOp = convertUnaryOperator(node.getUnaryOperator());
                return new JQuickUnaryExpression(unaryOp, operand);
            case CASE_WHEN:
                return visitCaseWhen(node.getCaseWhen());
            default:
                throw new RuntimeException("Unknown expression atom type: " + node.getType());
        }
    }
    /**
     * 转换 CASE WHEN 表达式
     * @param node JQuickCaseWhenNode - CASE WHEN 节点
     * @return JQuickExpression - CASE WHEN 表达式
     */
    private JQuickExpression visitCaseWhen(JQuickCaseWhenNode node) {
        List<JQuickExpression> conditions = new ArrayList<>();
        List<JQuickExpression> results = new ArrayList<>();
        if (node.isSimpleCase()) {
            // 简单 CASE: CASE caseBase WHEN value1 THEN result1 ...
            JQuickExpression caseBaseExpr = visitExpression(node.getCaseBase());
            for (JQuickCaseWhenNode.WhenClause whenClause : node.getWhenClauses()) {
                JQuickExpression whenValue = visitPredicate(whenClause.getCondition());
                JQuickExpression condition = new JQuickBinaryExpression(caseBaseExpr, whenValue, JQuickBinaryOperator.EQ);
                conditions.add(condition);
                JQuickExpression result = visitExpression(whenClause.getResult());
                results.add(result);
            }
        } else {
            // 搜索 CASE: CASE WHEN condition1 THEN result1 ...
            for (JQuickCaseWhenNode.WhenClause whenClause : node.getWhenClauses()) {
                JQuickExpression condition = visitPredicate(whenClause.getCondition());
                conditions.add(condition);
                JQuickExpression result = visitExpression(whenClause.getResult());
                results.add(result);
            }
        }
        JQuickExpression elseExpr = null;
        if (node.getElseExpression() != null) {
            elseExpr = visitExpression(node.getElseExpression());
        }
        return new JQuickCaseWhenExpression(conditions, results, elseExpr);
    }
    /**
     * 转换常量
     * @param node JQuickConstantNode - 常量节点
     * @return JQuickExpression - 字面量表达式
     */
    public JQuickExpression visitConstant(JQuickConstantNode node) {
        return JQuickLiteralExpression.fromConstant(node);
    }

    /**
     * 转换列引用
     * @param node JQuickFullColumnNameNode - 完整列名节点
     * @return JQuickExpression - 列引用表达式
     * 处理：tableAlias.columnName 或 columnName
     */
    public JQuickExpression visitColumn(JQuickFullColumnNameNode node) {
        return new JQuickColumnRefExpression(node.getColumnName(), node.getTableAlias());
    }

    /**
     * 转换函数调用
     * @param node JQuickFunctionCallNode - 函数调用节点
     * @return JQuickExpression - 函数调用表达式
     * 支持普通函数和聚合函数
     * 例如：UPPER(name), COUNT(*), SUM(price)
     */
    public JQuickExpression visitFunction(JQuickFunctionCallNode node) {
        List<JQuickExpression> args = new ArrayList<>();
        if (node.getArguments() != null) {
            for (JQuickFunctionArgNode arg : node.getArguments()) {
                args.add(visitExpression(arg.getExpression()));
            }
        }
        return new JQuickFunctionCallExpression(node.getFunctionName(), args, node.isStarArg());
    }

    /**
     * 转换过滤条件（WHERE/HAVING）
     * @param node JQuickFilterConditionNode - 过滤条件节点
     * @return JQuickExpression - 布尔表达式
     * 处理三种形式：
     * 1. 谓词：id = 1, name LIKE '%John%'
     * 2. 二元逻辑：condition1 AND condition2
     * 3. 括号表达式：(condition)
     */
    public JQuickExpression visit(JQuickFilterConditionNode node) {
        if (node.isPredicate()) {
            return visitPredicate(node.getPredicate());
        } else if (node.isBinary()) {
            JQuickExpression left = visit(node.getLeft());
            JQuickExpression right = visit(node.getRight());
            JQuickBinaryOperator op = node.getOperator() == JQuickFilterConditionNode.LogicalOperator.AND ? JQuickBinaryOperator.AND : JQuickBinaryOperator.OR;
            return new JQuickBinaryExpression(left, right, op);
        } else if (node.isParenExpression()) {
            return visit(node.getLeft());
        }
        throw new RuntimeException("Unknown filter condition type");
    }

    /**
     * 转换谓词
     * @param node JQuickPredicateNode - 谓词节点
     * @return JQuickExpression - 布尔表达式
     * 支持所有SQL谓词类型：
     * 1. EXPRESSION_ATOM - 原子表达式（如：id）
     * 2. IS_NULL - IS NULL / IS NOT NULL
     * 3. BINARY_COMPARISON - 比较运算（=, !=, >, <, >=, <=）
     * 4. BETWEEN - BETWEEN ... AND ...
     * 5. IN - IN (...)
     * 6. LIKE - LIKE
     * 7. EXISTS - EXISTS
     */
    public JQuickExpression visitPredicate(JQuickPredicateNode node) {
        switch (node.getType()) {
            case EXPRESSION_ATOM:
                return visitExpressionAtom(node.getExpressionAtom());
            case IS_NULL:
                JQuickExpression expr = visitPredicate(node.getIsNullPredicate());
                return new JQuickUnaryExpression(node.isNotNull() ? JQuickUnaryOperator.IS_NOT_NULL : JQuickUnaryOperator.IS_NULL, expr);
            case BINARY_COMPARISON:
                JQuickExpression left = visitPredicate(node.getLeft());
                JQuickExpression right = visitPredicate(node.getRight());
                JQuickBinaryOperator op = convertComparisonOperator(node.getComparisonOperator());
                return new JQuickBinaryExpression(left, right, op);
            case BETWEEN:
                JQuickExpression target = visitPredicate(node.getBetweenPredicate());
                JQuickExpression low = visitPredicate(node.getBetweenLow());
                JQuickExpression high = visitPredicate(node.getBetweenHigh());
                return new JQuickBetweenExpression(target, low, high, node.isBetweenNot());
            case IN:
                JQuickExpression inTarget = visitPredicate(node.getInPredicate());
                List<JQuickExpression> inValues = new ArrayList<>();
                if (node.getInExpressions() != null) {
                    for (JQuickExpressionNode expr1 : node.getInExpressions().getExpressions()) {
                        inValues.add(visitExpression(expr1));
                    }
                }
                return new JQuickInExpression(inTarget, inValues, node.isInNot());

            case LIKE:
                JQuickExpression likeTarget = visitPredicate(node.getLikePredicate());
                JQuickExpression pattern = visitPredicate(node.getLikePattern());
                JQuickBinaryOperator likeOp = node.isLikeNot() ? JQuickBinaryOperator.NOT_LIKE : JQuickBinaryOperator.LIKE;
                return new JQuickBinaryExpression(likeTarget, pattern, likeOp);
            case REGEXP:
                JQuickExpression regexpTarget = visitPredicate(node.getRegexpPredicate());
                JQuickExpression regexpPattern = visitPredicate(node.getRegexpPattern());
                JQuickBinaryOperator regexpOp = node.isLikeNot() ? JQuickBinaryOperator.NOT_REGEX : JQuickBinaryOperator.REGEX;
                return new JQuickBinaryExpression(regexpTarget, regexpPattern, regexpOp);
            case EXISTS:
                JQuickExpression existsExpr = visitExpression(node.getExistsExpression());
                return existsExpr;
            default:
                throw new RuntimeException("Unknown predicate type: " + node.getType());
        }
    }

    /**
     * 转换 ORDER BY 子句
     * @param node JQuickOrderByClauseNode - ORDER BY子句节点
     * @return List<JQuickSortNode.OrderByItem> - 排序项列表
     * 处理：ORDER BY col1 ASC, col2 DESC
     */
    public List<JQuickSortNode.OrderByItem> visitOrderByClause(JQuickOrderByClauseNode node) {
        List<JQuickSortNode.OrderByItem> items = new ArrayList<>();
        for (JQuickOrderByExpressionNode orderExpr : node.getOrderByExpressions()) {
            String columnName = extractColumnName(orderExpr.getExpression());
            items.add(new JQuickSortNode.OrderByItem(columnName, orderExpr.isAscending()));
        }
        return items;
    }

    /**
     * 转换 LIMIT 子句
     * @param node JQuickLimitClauseNode - LIMIT子句节点
     * @param child - 子逻辑计划
     * @return JQuickLimitNode - 限制节点
     * 处理两种形式：
     * 1. LIMIT n
     * 2. LIMIT m, n 或 LIMIT n OFFSET m
     */
    public JQuickLimitNode visitLimit(JQuickLimitClauseNode node, JQuickLogicalPlanNode child) {
        int limit = evaluateConstant(node.getLimit());
        int offset = node.hasOffset() ? evaluateConstant(node.getOffset()) : 0;
        return new JQuickLimitNode(limit, offset, child);
    }

    /**
     * 转换表达式列表
     * @param node JQuickExpressionsNode - 表达式列表节点
     * @return List<JQuickExpression> - 表达式列表
     * 用于GROUP BY子句：GROUP BY col1, col2
     */
    public List<JQuickExpression> visitExpressions(JQuickExpressionsNode node) {
        return node.getExpressions().stream()
                .map(this::visitExpression)
                .collect(Collectors.toList());
    }


    /**
     * 提取 SELECT 中的聚合函数
     * @param selectElements SELECT元素列表
     * @return List<JQuickGroupByNode.AggregateItem> - 聚合项列表
     * 聚合函数包括：COUNT, SUM, AVG, MAX, MIN
     * 用于GROUP BY分析
     */
    private List<JQuickGroupByNode.AggregateItem> extractAggregates(JQuickSelectElementsNode selectElements) {
        List<JQuickGroupByNode.AggregateItem> aggregates = new ArrayList<>();
        if (selectElements.isStar()) return aggregates;
        for (JQuickSelectElementNode elem : selectElements.getSelectElements()) {
            extractAggregatesFromExpression(elem.getExpression(), aggregates, elem.getAlias());
        }
        return aggregates;
    }

    /**
     * 从表达式中提取聚合函数
     * @param expr 表达式节点
     * @param aggregates 聚合项列表（输出参数）
     * @param alias 别名
     * 递归提取表达式中的聚合函数
     */
    private void extractAggregatesFromExpression(JQuickExpressionNode expr, List<JQuickGroupByNode.AggregateItem> aggregates, String alias) {
        // 表达式可能是包装类型，需要获取内部的 ExpressionAtom
        JQuickExpressionAtomNode atom = getExpressionAtom(expr);
        if (atom != null && atom.getType() == JQuickExpressionAtomNode.AtomType.FUNCTION) {
            JQuickFunctionCallNode func = atom.getFunctionCall();
            if (func != null) {
                String funcName = func.getFunctionName().toLowerCase();
                if (JQuickAggregateFunction.isAggregateFunction(funcName)) {
                    // 获取函数参数
                    JQuickExpressionNode arg = null;
                    if (func.getArguments() != null && !func.getArguments().isEmpty()) {
                        JQuickFunctionArgNode argNode = func.getArguments().get(0);
                        if (argNode != null) {
                            arg = argNode.getExpression();
                        }
                    }
                    JQuickExpression aggExpr = arg != null ? visitExpression(arg) : null;
                    boolean isCountStar = funcName.equals("count") && func.isStarArg();
                    aggregates.add(new JQuickGroupByNode.AggregateItem(aggExpr, funcName, alias, isCountStar));
                }
            }
        }
    }

    /**
     * 从 JQuickExpressionNode 中获取 JQuickExpressionAtomNode
     * @param expr 表达式节点
     * @return 最内层的表达式原子
     * 跳过括号和NOT包装，获取真正的原子表达式
     */
    private JQuickExpressionAtomNode getExpressionAtom(JQuickExpressionNode expr) {
        if (expr == null) {
            return null;
        }
        switch (expr.getType()) {
            case ATOM:
                return expr.getExpressionAtom();
            case PAREN:
                return getExpressionAtom(expr.getInnerExpression());
            case NOT:
                return getExpressionAtom(expr.getInnerExpression());
            default:
                return null;
        }
    }

    /**
     * 重建 SELECT 项（用于 GROUP BY 后）
     * @param original 原始的SELECT元素列表
     * @param groupKeys 分组键列表
     * @param aggregates 聚合项列表
     * @return 新的投影项列表
     * GROUP BY后的SELECT只能包含：
     * 1. 分组键列
     * 2. 聚合函数结果
     */
    private List<JQuickProjectNode.SelectItem> rebuildSelectItems(JQuickSelectElementsNode original, List<JQuickExpression> groupKeys, List<JQuickGroupByNode.AggregateItem> aggregates) {
        List<JQuickProjectNode.SelectItem> items = new ArrayList<>();
        for (int i = 0; i < groupKeys.size(); i++) { // 添加分组键
            JQuickExpression expression=groupKeys.get(i);
            if(expression instanceof JQuickColumnRefExpression) {
                JQuickColumnRefExpression columnRefExpr = (JQuickColumnRefExpression) expression;
                items.add(new JQuickProjectNode.SelectItem(new JQuickColumnRefExpression(columnRefExpr.getColumnName()), columnRefExpr.getTableAlias()));
            }
        }
        for (JQuickGroupByNode.AggregateItem agg : aggregates) {// 添加聚合结果
            items.add(new JQuickProjectNode.SelectItem(new JQuickColumnRefExpression(agg.getAlias()), agg.getAlias()));
        }
        return items;
    }

    /**
     * 提取列名（用于 ORDER BY）
     * @param node 表达式节点
     * @return 列名字符串
     * 简化处理，从表达式中提取列名
     */
    private String extractColumnName(JQuickExpressionNode node) {
        if (node == null) {
            return "";
        }
        switch (node.getType()) {
            case ATOM:
                return extractColumnNameFromAtom(node.getExpressionAtom());
            case PAREN:
                return extractColumnName(node.getInnerExpression());
            case NOT:
                return extractColumnName(node.getInnerExpression());
            default:
                return "col_" + System.identityHashCode(node);
        }

    }
    /**
     * 从表达式原子中提取列名
     */
    private String extractColumnNameFromAtom(JQuickExpressionAtomNode atom) {
        if (atom == null) {
            return "";
        }
        switch (atom.getType()) {
            case COLUMN:
                JQuickFullColumnNameNode column = atom.getFullColumnName();
                if (column != null) {
                    return column.getColumnName();
                }
                return "";
            case FUNCTION:
                JQuickFunctionCallNode func = atom.getFunctionCall();
                if (func != null) {
                    return func.getFunctionName();
                }
                return "";
            case MATH:
                // 对于数学表达式，生成一个别名
                return "expr_" + System.identityHashCode(atom);
            default:
                return "expr_" + System.identityHashCode(atom);
        }
    }

    /**
     * 计算常量表达式值
     */
    /**
     * 计算常量表达式的值（用于 LIMIT/OFFSET）
     */
    private int evaluateConstant(JQuickExpressionNode node) {
        if (node == null) {
            throw new RuntimeException("Expected constant expression for LIMIT/OFFSET");
        }
        Object value = extractConstantValue(node);
        if (value == null) {
            throw new RuntimeException("Expected constant expression for LIMIT/OFFSET");
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        if (value instanceof String) {
            return Integer.parseInt((String) value);
        }
        throw new RuntimeException("Expected numeric constant for LIMIT/OFFSET, got: " + value.getClass());
    }
    private Object extractConstantValue(JQuickExpressionNode node) {
        if (node == null) {
            return null;
        }
        switch (node.getType()) {
            case ATOM:
                return extractConstantFromAtom(node.getExpressionAtom());
            case PAREN:
                return extractConstantValue(node.getInnerExpression());
            default:
                return null;
        }
    }
    /**
     * 从 ExpressionAtom 中提取常量值
     */
    private Object extractConstantFromAtom(JQuickExpressionAtomNode atom) {
        if (atom == null) {
            return null;
        }

        switch (atom.getType()) {
            case CONSTANT:
                JQuickConstantNode constant = atom.getConstant();
                if (constant != null) {
                    return constant.getValue();
                }
                return null;
            case NESTED:
                if (atom.getNestedExpressions() != null && !atom.getNestedExpressions().isEmpty()) {
                    return extractConstantValue(atom.getNestedExpressions().get(0));
                }
                return null;
            default:
                return null;
        }
    }








    private JQuickBinaryOperator convertComparisonOperator(JQuickPredicateNode.ComparisonOperator op) {
        switch (op) {
            case EQ:
                return JQuickBinaryOperator.EQ;
            case NE:
                return JQuickBinaryOperator.NE;
            case GT:
                return JQuickBinaryOperator.GT;
            case LT:
                return JQuickBinaryOperator.LT;
            case GE:
                return JQuickBinaryOperator.GE;
            case LE:
                return JQuickBinaryOperator.LE;
            default:
                return JQuickBinaryOperator.EQ;
        }
    }

    private JQuickBinaryOperator convertMathOperator(JQuickExpressionAtomNode.MathOperator op) {
        switch (op) {
            case PLUS:
                return JQuickBinaryOperator.PLUS;
            case MINUS:
                return JQuickBinaryOperator.MINUS;
            case MULTIPLY:
                return JQuickBinaryOperator.MULTIPLY;
            case DIVIDE:
                return JQuickBinaryOperator.DIVIDE;
            case MODULO:
                return JQuickBinaryOperator.MODULO;
            default:
                return JQuickBinaryOperator.PLUS;
        }
    }

    private JQuickUnaryOperator convertUnaryOperator(JQuickExpressionAtomNode.UnaryOperator op) {
        switch (op) {
            case NOT:
                return JQuickUnaryOperator.NOT;
            case PLUS:
                return JQuickUnaryOperator.PLUS;
            case MINUS:
                return JQuickUnaryOperator.MINUS;
            default:
                return JQuickUnaryOperator.NOT;
        }
    }
    /**
     * 构建递归CTE的逻辑计划
     *
     * 递归CTE需要特殊的节点类型来处理循环引用
     * 通常需要将initialPlan和recursivePlan通过Union操作连接
     * 并且recursivePlan中可以引用CTE自身（通过名称）
     *
     * @param name CTE名称
     * @param columnNames 列名列表（可能为null）
     * @param initialPlan 初始查询（锚点）的逻辑计划
     * @param recursivePlan 递归部分的逻辑计划
     * @param isUnionAll true表示UNION ALL，false表示UNION
     * @return 递归CTE的逻辑计划节点
     */
    private JQuickLogicalPlanNode buildRecursiveCTEPlan(String name, List<String> columnNames,JQuickLogicalPlanNode initialPlan, JQuickLogicalPlanNode recursivePlan, boolean isUnionAll) {
        JQuickLogicalPlanNode result = null;
        if (initialPlan != null && recursivePlan != null) {
            // 递归CTE的结构: initialPlan UNION [ALL] recursivePlan
            result = new JQuickRecursiveUnionNode(name, columnNames, initialPlan, recursivePlan, isUnionAll);
        } else if (initialPlan != null) {
            // 只有initialPlan，没有recursivePart（边界情况）
            result = initialPlan;
        } else if (recursivePlan != null) {
            // 只有recursivePart（不应该发生，但保留处理）
            result = recursivePlan;
        }

        return result;
    }
}