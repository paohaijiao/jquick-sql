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
package com.github.paohaijiao.logic.service;

import com.github.paohaijiao.ast.*;
import com.github.paohaijiao.enums.JQuickBinaryOperator;
import com.github.paohaijiao.enums.JQuickJoinType;
import com.github.paohaijiao.enums.JQuickSQLOperationType;
import com.github.paohaijiao.expression.*;
import com.github.paohaijiao.expression.domain.*;
import com.github.paohaijiao.logic.JQuickLogicalPlanNode;
import com.github.paohaijiao.logic.domain.*;


import java.util.*;
import java.util.stream.Collectors;

/**
 * AST到逻辑计划转换器
 * 将ANTLR解析生成的AST转换为逻辑计划节点
 */
public class JQuickASTToLogicalPlanVisitor {

    /**
     * 转换查询节点
     */
    public JQuickLogicalPlanNode visit(JQuickQueryNode node) {
        return visit(node.getSelectStatement());
    }

    /**
     * 转换SELECT语句节点
     */
    public JQuickLogicalPlanNode visit(JQuickSelectStatementNode node) {
        if (node.isCteQuery()) {// 处理CTE查询
            Map<String, JQuickLogicalPlanNode> cteMap = new LinkedHashMap<>();
            for (JQuickCommonTableExpressionNode cte : node.getCtes()) {
                JQuickLogicalPlanNode ctePlan = visit(cte.getQuery());
                cteMap.put(cte.getName(), ctePlan);
            }
            JQuickLogicalPlanNode mainPlan = visit(node.getSelectExpression());
            return new JQuickWithNode(mainPlan, cteMap);
        } else { // 普通查询
            return visit(node.getSelectExpression());
        }
    }

    /**
     * 转换SELECT表达式节点
     */
    public JQuickLogicalPlanNode visit(JQuickSelectExpressionNode node) {
        return visit(node.getDataSetOp());
    }

    /**
     * 转换数据集操作节点
     */
    public JQuickLogicalPlanNode visit(JQuickDataSetOpNode node) {
        List<JQuickSelectClauseNode> clauses = node.getSelectClauses();
        if (clauses.size() == 1) {
            return visit(clauses.get(0));
        }
        // 多个子查询的集合操作
        JQuickLogicalPlanNode result = visit(clauses.get(0));
        for (int i = 1; i < clauses.size(); i++) {
            JQuickLogicalPlanNode right = visit(clauses.get(i));
            JQuickSQLOperationType opType = convertSetOperator(node.getOperators().get(i - 1));
            result = new JQuickSetOperationNode(result, right, opType);
        }
        return result;
    }
    /**
     * 转换SELECT子句节点
     */
    public JQuickLogicalPlanNode visit(JQuickSelectClauseNode node) {
        //处理FROM和JOIN
        JQuickLogicalPlanNode root = visit(node.getFromClause());
        //处理JOIN
        if (node.getJoinClauses() != null && !node.getJoinClauses().isEmpty()) {
            for (JQuickJoinClauseNode join : node.getJoinClauses()) {
                root = visitJoin(root, join);
            }
        }
        // 处理WHERE
        if (node.getWhereClause() != null) {
            JQuickExpression predicate = visit(node.getWhereClause().getFilterCondition());
            root = new JQuickFilterNode(predicate, root);
        }

        // 4. 处理GROUP BY和HAVING
        if (node.getGroupByClause() != null) {
            // 提取分组键
            List<JQuickExpression> groupKeys = visitExpressions(node.getGroupByClause().getExpressions());

            // 提取聚合函数
            List<JQuickAggregateNode.AggregateFunction> aggregates = extractAggregates(node.getSelectElements());

            // 处理HAVING
            JQuickExpression havingExpr = node.getHavingClause() != null
                    ? visit(node.getHavingClause().getFilterCondition()) : null;

            // 创建聚合节点
            JQuickAggregateNode aggregateNode = new JQuickAggregateNode(groupKeys, aggregates, root, havingExpr);

            // 重建投影（只包含分组键和聚合结果）
            List<JQuickProjectNode.SelectItem> selectItems = rebuildSelectItems(
                    node.getSelectElements(), groupKeys, aggregates);
            root = new JQuickProjectNode(selectItems, aggregateNode, node.isDistinct());
        } else {
            // 5. 处理SELECT投影（无GROUP BY）
            List<JQuickProjectNode.SelectItem> selectItems = visitSelectElements(node.getSelectElements());
            root = new JQuickProjectNode(selectItems, root, node.isDistinct());
        }

        // 6. 处理ORDER BY
        if (node.getOrderByClause() != null) {
            List<JQuickSortNode.OrderByItem> orderByItems = visitOrderByClause(node.getOrderByClause());
            root = new JQuickSortNode(orderByItems, root);
        }

        // 7. 处理LIMIT
        if (node.getLimitClause() != null) {
            root = visitLimit(node.getLimitClause(), root);
        }

        return root;
    }

    /**
     * 转换FROM子句
     */
    public JQuickLogicalPlanNode visit(JQuickFromClauseNode node) {
        return visit(node.getTableNameItem());
    }

    /**
     * 转换表名项（表或子查询）
     */
    public JQuickLogicalPlanNode visit(JQuickTableNameItemNode node) {
        if (node.isSubquery()) {
            // 子查询
            JQuickLogicalPlanNode subPlan = visit(node.getSubquery());
            // 子查询作为派生表，需要保留别名
            return subPlan;
        } else {
            // 普通表
            return new JQuickTableScanNode(node.getTableName(), node.getAlias());
        }
    }

    /**
     * 转换JOIN子句
     */
    public JQuickLogicalPlanNode visitJoin(JQuickLogicalPlanNode left, JQuickJoinClauseNode join) {
        JQuickLogicalPlanNode right = visit(join.getTableNameItem());

        JQuickJoinType joinType = convertJoinType(join.getJoinType());

        JQuickExpression condition = null;
        if (join.hasOnCondition()) {
            // 构建等值条件
            JQuickExpression leftExpr = new JQuickColumnRefExpression(
                    join.getLeftColumn().getColumnName(),
                    join.getLeftColumn().getTableAlias()
            );
            JQuickExpression rightExpr = new JQuickColumnRefExpression(
                    join.getRightColumn().getColumnName(),
                    join.getRightColumn().getTableAlias()
            );
            condition = new JQuickBinaryExpression(leftExpr, rightExpr, JQuickBinaryOperator.EQ);
        }

        return new JQuickJoinNode(joinType, left, right, condition);
    }

    /**
     * 转换SELECT元素列表
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
     * 转换单个SELECT元素
     */
    public JQuickProjectNode.SelectItem visitSelectElement(JQuickSelectElementNode node) {
        JQuickExpression expr = visitExpression(node.getExpression());
        String alias = node.getAlias();
        return new JQuickProjectNode.SelectItem(expr, alias);
    }

    /**
     * 转换过滤条件
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
     */
    public JQuickExpression visitPredicate(JQuickPredicateNode node) {
        switch (node.getType()) {
            case EXPRESSION_ATOM:
                return visitExpressionAtom(node.getExpressionAtom());

            case IS_NULL:
                JQuickExpression expr = visitPredicate(node.getIsNullPredicate());
                return new JQuickUnaryExpression(
                        node.isNotNull() ? JQuickUnaryExpression.UnaryOperator.IS_NOT_NULL
                                : JQuickUnaryExpression.UnaryOperator.IS_NULL,
                        expr
                );

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

            case EXISTS:
                return visitExpression(node.getExistsExpression());

            default:
                throw new RuntimeException("Unknown predicate type: " + node.getType());
        }
    }
    /**
     * 转换表达式
     */
    public JQuickExpression visitExpression(JQuickExpressionNode node) {
        switch (node.getType()) {
            case ATOM:
                return visitExpressionAtom(node.getExpressionAtom());
            case PAREN:
                return visitExpression(node.getInnerExpression());
            case NOT:
                JQuickExpression inner = visitExpression(node.getInnerExpression());
                return new JQuickUnaryExpression(JQuickUnaryExpression.UnaryOperator.NOT, inner);
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
                JQuickUnaryExpression.UnaryOperator unaryOp = convertUnaryOperator(node.getUnaryOperator());
                return new JQuickUnaryExpression(unaryOp, operand);
            default:
                throw new RuntimeException("Unknown expression atom type: " + node.getType());
        }
    }

    /**
     * 转换常量
     */
    public JQuickExpression visitConstant(JQuickConstantNode node) {
        return new JQuickLiteralExpression(node.getValue());
    }

    /**
     * 转换列引用
     */
    public JQuickExpression visitColumn(JQuickFullColumnNameNode node) {
        return new JQuickColumnRefExpression(node.getColumnName(), node.getTableAlias());
    }

    /**
     * 转换函数调用
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
     * 转换表达式列表
     */
    public List<JQuickExpression> visitExpressions(JQuickExpressionsNode node) {
        return node.getExpressions().stream()
                .map(this::visitExpression)
                .collect(Collectors.toList());
    }

    /**
     * 转换ORDER BY子句
     */
    public List<JQuickSortNode.OrderByItem> visitOrderByClause(JQuickOrderByClauseNode node) {
        return node.getOrderByExpressions().stream()
                .map(this::visitOrderByExpression)
                .collect(Collectors.toList());
    }

    /**
     * 转换单个ORDER BY表达式
     */
    public JQuickSortNode.OrderByItem visitOrderByExpression(JQuickOrderByExpressionNode node) {
        String columnName = extractColumnName(node.getExpression());
        return new JQuickSortNode.OrderByItem(columnName, node.isAscending());
    }


    /**
     * 转换LIMIT子句
     */
    public JQuickLimitNode visitLimit(JQuickLimitClauseNode node, JQuickLogicalPlanNode child) {
        int limit = evaluateConstant(node.getLimit());
        int offset = node.hasOffset() ? evaluateConstant(node.getOffset()) : 0;
        return new JQuickLimitNode(limit, offset, child);
    }

    /**
     * 提取SELECT中的聚合函数
     */
    private List<JQuickAggregateNode.AggregateFunction> extractAggregates(JQuickSelectElementsNode selectElements) {
        List<JQuickAggregateNode.AggregateFunction> aggregates = new ArrayList<>();
        if (selectElements.isStar()) return aggregates;

        for (JQuickSelectElementNode elem : selectElements.getSelectElements()) {
            extractAggregatesFromExpression(elem.getExpression(), aggregates, elem.getAlias());
        }
        return aggregates;
    }

    /**
     * 从表达式中提取聚合函数
     */
    private void extractAggregatesFromExpression(JQuickExpressionNode expr,
                                                 List<JQuickAggregateNode.AggregateFunction> aggregates,
                                                 String alias) {
        if (expr instanceof JQuickFunctionCallNode) {
            JQuickFunctionCallNode func = (JQuickFunctionCallNode) expr;
            String funcName = func.getFunctionName().toLowerCase();
            if (isAggregateFunction(funcName)) {
                JQuickExpressionNode arg = (func.getArguments() != null && !func.getArguments().isEmpty())
                        ? func.getArguments().get(0).getExpression() : null;
                JQuickExpression aggExpr = arg != null ? visitExpression(arg) : null;
                boolean isCountStar = funcName.equals("count") && func.isStarArg();
                aggregates.add(new JQuickAggregateNode.AggregateFunction(
                        funcName, aggExpr, false, alias, isCountStar, null));
            }
        }
    }

    /**
     * 重建SELECT项（用于GROUP BY后）
     */
    private List<JQuickProjectNode.SelectItem> rebuildSelectItems(JQuickSelectElementsNode original, List<JQuickExpression> groupKeys, List<JQuickAggregateNode.AggregateFunction> aggregates) {
        List<JQuickProjectNode.SelectItem> items = new ArrayList<>();

        // 添加分组键
        for (int i = 0; i < groupKeys.size(); i++) {
            String alias = "group_" + i;
            items.add(new JQuickProjectNode.SelectItem(new JQuickColumnRefExpression(alias), alias));
        }

        // 添加聚合结果
        for (JQuickAggregateNode.AggregateFunction agg : aggregates) {
            items.add(new JQuickProjectNode.SelectItem(
                    new JQuickColumnRefExpression(agg.getAlias()), agg.getAlias()));
        }

        return items;
    }

    /**
     * 提取列名（用于ORDER BY）
     */
    private String extractColumnName(JQuickExpressionNode node) {
        if (node instanceof JQuickFullColumnNameNode) {
            return ((JQuickFullColumnNameNode) node).getColumnName();
        }
        if (node instanceof JQuickColumnRefExpression) {
            return ((JQuickColumnRefExpression) node).getColumnName();
        }
        return node.toSql();
    }

    /**
     * 计算常量表达式值
     */
    private int evaluateConstant(JQuickExpressionNode node) {
        if (node instanceof JQuickConstantNode) {
            Object value = ((JQuickConstantNode) node).getValue();
            if (value instanceof Number) {
                return ((Number) value).intValue();
            }
            if (value instanceof String) {
                return Integer.parseInt((String) value);
            }
        }
        throw new RuntimeException("Expected constant expression for LIMIT/OFFSET");
    }

    private boolean isAggregateFunction(String funcName) {
        return funcName.equals("count") || funcName.equals("sum") ||
                funcName.equals("avg") || funcName.equals("max") ||
                funcName.equals("min");
    }

    private JQuickSQLOperationType convertSetOperator(JQuickDataSetOpNode.SetOperator op) {
        switch (op) {
            case UNION: return JQuickSQLOperationType.UNION;
            case MINUS: return JQuickSQLOperationType.EXCEPT;
            case INTERSECT: return JQuickSQLOperationType.INTERSECT;
            default: return JQuickSQLOperationType.UNION;
        }
    }

    private JQuickJoinType convertJoinType(JQuickJoinType joinType) {
        switch (joinType) {
            case INNER: return JQuickJoinType.INNER;
            case LEFT: return JQuickJoinType.LEFT;
            case RIGHT: return JQuickJoinType.RIGHT;
            case FULL: return JQuickJoinType.FULL;
            case CROSS: return JQuickJoinType.CROSS;
            default: return JQuickJoinType.INNER;
        }
    }

    private JQuickBinaryOperator convertComparisonOperator(JQuickPredicateNode.ComparisonOperator op) {
        switch (op) {
            case EQ: return JQuickBinaryOperator.EQ;
            case NE: return JQuickBinaryOperator.NE;
            case GT: return JQuickBinaryOperator.GT;
            case LT: return JQuickBinaryOperator.LT;
            case GE: return JQuickBinaryOperator.GE;
            case LE: return JQuickBinaryOperator.LE;
            default: return JQuickBinaryOperator.EQ;
        }
    }

    private JQuickBinaryOperator convertMathOperator(JQuickExpressionAtomNode.MathOperator op) {
        switch (op) {
            case PLUS: return JQuickBinaryOperator.PLUS;
            case MINUS: return JQuickBinaryOperator.MINUS;
            case MULTIPLY: return JQuickBinaryOperator.MULTIPLY;
            case DIVIDE: return JQuickBinaryOperator.DIVIDE;
            case MODULO: return JQuickBinaryOperator.MODULO;
            default: return JQuickBinaryOperator.PLUS;
        }
    }

    private JQuickUnaryExpression.UnaryOperator convertUnaryOperator(JQuickExpressionAtomNode.UnaryOperator op) {
        switch (op) {
            case NOT: return JQuickUnaryExpression.UnaryOperator.NOT;
            case PLUS: return JQuickUnaryExpression.UnaryOperator.PLUS;
            case MINUS: return JQuickUnaryExpression.UnaryOperator.MINUS;
            default: return JQuickUnaryExpression.UnaryOperator.NOT;
        }
    }
}
