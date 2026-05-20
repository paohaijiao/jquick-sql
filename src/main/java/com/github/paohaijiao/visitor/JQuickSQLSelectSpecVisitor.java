package com.github.paohaijiao.visitor;

import com.github.paohaijiao.ast.*;
import com.github.paohaijiao.enums.JQuickJoinType;
import com.github.paohaijiao.parser.JQuickSQLParser;

import java.util.ArrayList;
import java.util.List;

public class JQuickSQLSelectSpecVisitor extends JQuickSQLPredictVisistor{
    @Override
    public JQuickFilterConditionNode visitFilterCondition(JQuickSQLParser.FilterConditionContext ctx) {
        int childCount = ctx.getChildCount();
        if (childCount == 1 && ctx.predicate() != null) {
            return new JQuickFilterConditionNode((JQuickPredicateNode) visit(ctx.predicate()));
        }
        if (childCount == 3 && ctx.getChild(0).getText().equals("(")) {
            JQuickFilterConditionNode inner = (JQuickFilterConditionNode) visit(ctx.filterCondition(0));
            return new JQuickFilterConditionNode(inner, true);
        }
        if (childCount == 3 && ctx.filterCondition().size() == 2) {
            JQuickFilterConditionNode left = (JQuickFilterConditionNode) visit(ctx.filterCondition(0));
            JQuickFilterConditionNode right = (JQuickFilterConditionNode) visit(ctx.filterCondition(1));
            if (ctx.getChild(1).getText().equalsIgnoreCase("AND")) {
                return new JQuickFilterConditionNode(left, right, JQuickFilterConditionNode.LogicalOperator.AND);
            } else if (ctx.getChild(1).getText().equalsIgnoreCase("OR")) {
                return new JQuickFilterConditionNode(left, right, JQuickFilterConditionNode.LogicalOperator.OR);
            }
        }
        throw new RuntimeException("Invalid filter condition structure: " + ctx.getText());
    }
    @Override
    public List<JQuickFunctionArgNode> visitFunctionArgs(JQuickSQLParser.FunctionArgsContext ctx) {
        List<JQuickFunctionArgNode> arguments = new ArrayList<>();
        for (JQuickSQLParser.FunctionArgContext argCtx : ctx.functionArg()) {
            JQuickFunctionArgNode functionArg = (JQuickFunctionArgNode) visit(argCtx);
            arguments.add(functionArg);
        }
        return arguments;
    }
    @Override
    public JQuickFunctionArgNode visitFunctionArg(JQuickSQLParser.FunctionArgContext ctx) {
        JQuickExpressionNode expression = (JQuickExpressionNode) visit(ctx.expression());
        return new JQuickFunctionArgNode(expression);
    }
    @Override
    public JQuickFunctionCallNode visitFunctionCall(JQuickSQLParser.FunctionCallContext ctx) {
        String functionName = ((JQuickUidNode) visit(ctx.uid())).getValue();
        List<JQuickFunctionArgNode> arguments = new ArrayList<>();
        boolean isStarArg = false;
        if (ctx.arg() != null) {
            String argText = ctx.arg().getText();
            if ("*".equals(argText)) {
                isStarArg = true;
            } else if (ctx.arg().functionArgs() != null) {
                for (JQuickSQLParser.FunctionArgContext argCtx : ctx.arg().functionArgs().functionArg()) {
                    JQuickFunctionArgNode functionArg = (JQuickFunctionArgNode) visit(argCtx);
                    arguments.add(functionArg);
                }
            }
        }
        return new JQuickFunctionCallNode(functionName, arguments, isStarArg);
    }
    @Override
    public JQuickSelectStatementNode visitRecursivePart(JQuickSQLParser.RecursivePartContext ctx) {
        return (JQuickSelectStatementNode) visit(ctx.selectStatement());
    }
    @Override
    public JQuickSelectStatementNode visitInitialQuery(JQuickSQLParser.InitialQueryContext ctx) {
        return (JQuickSelectStatementNode) visit(ctx.selectStatement());
    }
    @Override
    public List<String> visitColumnNames(JQuickSQLParser.ColumnNamesContext ctx) {
        List<String> columnNames = new ArrayList<>();
        for (JQuickSQLParser.UidContext uidCtx : ctx.uid()) {
            JQuickUidNode uid = (JQuickUidNode) visit(uidCtx);
            columnNames.add(uid.getValue());
        }
        return columnNames;
    }
    @Override
    public JQuickCommonTableExpressionNode visitCommonTableExpression(JQuickSQLParser.CommonTableExpressionContext ctx) {
        String name = ((JQuickUidNode) visit(ctx.uid())).getValue();
        List<String> columnNames = null;
        if (ctx.columnNames() != null) {
            columnNames = new ArrayList<>();
            for (JQuickSQLParser.UidContext uidCtx : ctx.columnNames().uid()) {
                columnNames.add(((JQuickUidNode) visit(uidCtx)).getValue());
            }
        }
        if (ctx.initialQuery() != null && ctx.recursivePart() != null) {
            JQuickSelectStatementNode initialQuery = (JQuickSelectStatementNode) visit(ctx.initialQuery());
            JQuickSelectStatementNode recursivePart = (JQuickSelectStatementNode) visit(ctx.recursivePart());
            boolean unionAll = ctx.UNION() != null && ctx.ALL() != null;
            return new JQuickCommonTableExpressionNode(name, columnNames, initialQuery, recursivePart, unionAll);
        } else {
            JQuickSelectStatementNode query = (JQuickSelectStatementNode) visit(ctx.selectStatement());
            return new JQuickCommonTableExpressionNode(name, columnNames, query);
        }
    }
    @Override
    public JQuickLimitClauseNode visitLimitWithOffset(JQuickSQLParser.LimitWithOffsetContext ctx) {
        JQuickExpressionNode offsetExpr = (JQuickExpressionNode) visit(ctx.offset);
        JQuickExpressionNode limitExpr = (JQuickExpressionNode) visit(ctx.limit);
        return new JQuickLimitClauseNode(offsetExpr, limitExpr);
    }
    @Override
    public JQuickExpressionNode visitLimitOnly(JQuickSQLParser.LimitOnlyContext ctx) {
        return (JQuickExpressionNode) visit(ctx.expression());
    }
    @Override
    public JQuickLimitClauseNode visitLimitClause(JQuickSQLParser.LimitClauseContext ctx) {
        if (ctx.limitOnly() != null) {
            JQuickExpressionNode limitExpr = (JQuickExpressionNode) visit(ctx.limitOnly().expression());
            return new JQuickLimitClauseNode(limitExpr);
        }
        if (ctx.limitWithOffset() != null) {
            JQuickExpressionNode offsetExpr = (JQuickExpressionNode) visit(ctx.limitWithOffset().offset);
            JQuickExpressionNode limitExpr = (JQuickExpressionNode) visit(ctx.limitWithOffset().limit);
            return new JQuickLimitClauseNode(offsetExpr, limitExpr);
        }
        throw new RuntimeException("Unknown limit clause type");
    }
    @Override
    public JQuickOrderByExpressionNode visitOrderByExpression(JQuickSQLParser.OrderByExpressionContext ctx) {
        JQuickExpressionNode expression = (JQuickExpressionNode) visit(ctx.expression());
        boolean ascending = true;
        if (ctx.DESC() != null) {
            ascending = false;
        }
        return new JQuickOrderByExpressionNode(expression, ascending);
    }
    @Override
    public JQuickOrderByClauseNode visitOrderByClause(JQuickSQLParser.OrderByClauseContext ctx) {
        List<JQuickOrderByExpressionNode> orderByExpressions = new ArrayList<>();
        for (JQuickSQLParser.OrderByExpressionContext obeCtx : ctx.orderByExpression()) {
            JQuickOrderByExpressionNode orderByExpr = (JQuickOrderByExpressionNode) visit(obeCtx);
            orderByExpressions.add(orderByExpr);
        }
        return new JQuickOrderByClauseNode(orderByExpressions);
    }
    @Override
    public JQuickJoinType visitJoinType(JQuickSQLParser.JoinTypeContext ctx) {
        if (ctx.INNER() != null) {
            return JQuickJoinType.INNER;
        } else if (ctx.CROSS() != null) {
            return JQuickJoinType.CROSS;
        } else if (ctx.LEFT() != null) {
            return JQuickJoinType.LEFT;
        } else if (ctx.RIGHT() != null) {
            return JQuickJoinType.RIGHT;
        } else if (ctx.NATURAL() != null) {
            return JQuickJoinType.NATURAL;
        } else if (ctx.FULL() != null) {
            return JQuickJoinType.FULL;
        }
        throw new RuntimeException("Unknown join type: " + ctx.getText());
    }
    @Override
    public String visitTableNameSpec(JQuickSQLParser.TableNameSpecContext ctx) {
        String tableName = ctx.IDENTIFIER().getText();
        return tableName;
    }
    @Override
    public JQuickTableNameItemNode visitTableNameItem(JQuickSQLParser.TableNameItemContext ctx) {
        if (ctx.tableNameSpec() != null) {
            String tableName = ctx.tableNameSpec().getText();
            String alias = null;
            if (ctx.uid() != null) {
                JQuickUidNode uid = (JQuickUidNode) visit(ctx.uid());
                alias = uid.getValue();
            }
            return new JQuickTableNameItemNode(tableName, alias);
        }
        if (ctx.selectExpression() != null) {
            JQuickSelectExpressionNode subquery = (JQuickSelectExpressionNode) visit(ctx.selectExpression());
            JQuickUidNode uid = (JQuickUidNode) visit(ctx.uid());
            String alias = uid.getValue();
            return new JQuickTableNameItemNode(subquery, alias);
        }
        throw new RuntimeException("Unknown tableNameItem type: " + ctx.getText());
    }
    @Override
    public JQuickJoinClauseNode visitJoinClause(JQuickSQLParser.JoinClauseContext ctx) {
        JQuickJoinType joinType = parseJoinType(ctx.joinType());
        JQuickTableNameItemNode tableNameItem = (JQuickTableNameItemNode) visit(ctx.tableNameItem());
        JQuickFullColumnNameNode leftColumn = null;
        JQuickFullColumnNameNode rightColumn = null;
        if (ctx.fullColumnName() != null && ctx.fullColumnName().size() == 2) {
            leftColumn = (JQuickFullColumnNameNode) visit(ctx.fullColumnName(0));
            rightColumn = (JQuickFullColumnNameNode) visit(ctx.fullColumnName(1));
        }
        return new JQuickJoinClauseNode(joinType, tableNameItem, leftColumn, rightColumn);
    }
    @Override
    public JQuickFromClauseNode visitFromClause(JQuickSQLParser.FromClauseContext ctx) {
        JQuickTableNameItemNode tableNameItem = (JQuickTableNameItemNode) visit(ctx.tableNameItem());
        return new JQuickFromClauseNode(tableNameItem);
    }
    @Override
    public JQuickSelectElementsNode visitSelectElements(JQuickSQLParser.SelectElementsContext ctx) {
        if (ctx.star != null) {
            return new JQuickSelectElementsNode(true, null);
        }
        List<JQuickSelectElementNode> selectElements = new ArrayList<>();
        for (JQuickSQLParser.SelectElementContext elementCtx : ctx.selectElement()) {
            JQuickSelectElementNode element = (JQuickSelectElementNode) visit(elementCtx);
            selectElements.add(element);
        }
        return new JQuickSelectElementsNode(false, selectElements);
    }
    @Override
    public JQuickSelectSpecNode visitSelectSpec(JQuickSQLParser.SelectSpecContext ctx) {
        boolean distinct = ctx.DISTINCT() != null;
        return new JQuickSelectSpecNode(distinct);
    }
}
