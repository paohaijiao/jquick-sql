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
package com.github.paohaijiao.visitor;

import com.github.paohaijiao.ast.*;
import com.github.paohaijiao.config.JQuickSqlConfig;
import com.github.paohaijiao.enums.JQuickJoinType;
import com.github.paohaijiao.param.JContext;
import com.github.paohaijiao.parser.JQuickSQLParser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * packageName com.github.paohaijiao.visitor
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/5/17
 */
public class JQuickSQLCommonVisistor extends JQuickSQLPredictVisistor {

    public JQuickSQLCommonVisistor(JContext jcontext, JQuickSqlConfig config){
        if(null!=jcontext&&!jcontext.isEmpty()){
            this.context.putAll(jcontext);
        }
        if(null!=config){
            this.config=config;
        }
    }

    public JQuickSQLCommonVisistor(JQuickSqlConfig config){
        this(null,config);
    }

    public JQuickSQLCommonVisistor(JContext context){
        this(context,null);
    }

    public JQuickSQLCommonVisistor( ){
        this(null,null);
    }


    @Override
    public JQuickQueryNode visitQuery(JQuickSQLParser.QueryContext ctx) {
        JQuickSelectStatementNode selectStatement = (JQuickSelectStatementNode) visit(ctx.selectStatement());
        return new JQuickQueryNode(selectStatement);
    }
    @Override
    public JQuickSelectStatementNode visitCteQuery(JQuickSQLParser.CteQueryContext ctx) {
        boolean withRecursive = ctx.RECURSIVE() != null;
        List<JQuickCommonTableExpressionNode> ctes = new ArrayList<>();
        for (JQuickSQLParser.CommonTableExpressionContext cteCtx : ctx.commonTableExpression()) {
            JQuickCommonTableExpressionNode cte = (JQuickCommonTableExpressionNode) visit(cteCtx);
            ctes.add(cte);
        }
        JQuickSelectExpressionNode selectExpression = (JQuickSelectExpressionNode) visit(ctx.selectExpression());
        return new JQuickSelectStatementNode(withRecursive, ctes, selectExpression);
    }
    @Override
    public JQuickSelectStatementNode visitSingleQuery(JQuickSQLParser.SingleQueryContext ctx) {
        JQuickSelectExpressionNode selectExpression = (JQuickSelectExpressionNode) visit(ctx.selectExpression());
        return new JQuickSelectStatementNode(selectExpression);
    }
    @Override
    public JQuickSelectExpressionNode visitSelectExpression(JQuickSQLParser.SelectExpressionContext ctx) {
        JQuickDataSetOpNode dataSetOp = (JQuickDataSetOpNode) visit(ctx.datasetOp());
        return new JQuickSelectExpressionNode(dataSetOp);
    }
    @Override
    public JQuickDataSetOpNode visitDatasetOp(JQuickSQLParser.DatasetOpContext ctx) {
//        List<JQuickSelectClauseNode> selectClauses = new ArrayList<>();
//        List<JQuickDataSetOpNode.SetOperator> operators = new ArrayList<>();
//        selectClauses.add((JQuickSelectClauseNode) visit(ctx.selectClause(0)));
//        for (int i = 1; i < ctx.selectClause().size(); i++) {
//            int operatorIndex = i * 2 - 1;
//            TerminalNode operatorNode = (TerminalNode) ctx.getChild(operatorIndex);
//            if (operatorNode.getSymbol().getType() == JQuickSQLParser.UNION) {
//                operators.add(JQuickDataSetOpNode.SetOperator.UNION);
//            } else if (operatorNode.getSymbol().getType() == JQuickSQLParser.MINUS) {
//                operators.add(JQuickDataSetOpNode.SetOperator.MINUS);
//            } else if (operatorNode.getSymbol().getType() == JQuickSQLParser.INTERSECT) {
//                operators.add(JQuickDataSetOpNode.SetOperator.INTERSECT);
//            }
//            selectClauses.add((JQuickSelectClauseNode) visit(ctx.selectClause(i)));
//        }
        return new JQuickDataSetOpNode(null, null);
    }
    @Override
    public JQuickSelectClauseNode visitSelectClause(JQuickSQLParser.SelectClauseContext ctx) {
        JQuickSelectClauseNode.Builder builder = new JQuickSelectClauseNode.Builder();
        if (ctx.selectSpec() != null) {
            JQuickSelectSpecNode selectSpec = (JQuickSelectSpecNode) visit(ctx.selectSpec());
            builder.setSelectSpec(selectSpec);
        }
        JQuickSelectElementsNode selectElements = (JQuickSelectElementsNode) visit(ctx.selectElements());
        builder.setSelectElements(selectElements);
        JQuickFromClauseNode fromClause = (JQuickFromClauseNode) visit(ctx.fromClause());
        builder.setFromClause(fromClause);
        for (JQuickSQLParser.JoinClauseContext joinCtx : ctx.joinClause()) {
            JQuickJoinClauseNode joinClause = (JQuickJoinClauseNode) visit(joinCtx);
            builder.addJoinClause(joinClause);
        }
        if (ctx.whereClause() != null) {
            JQuickWhereClauseNode whereClause = (JQuickWhereClauseNode) visit(ctx.whereClause());
            builder.setWhereClause(whereClause);
        }
        if (ctx.groupByClause() != null) {
            JQuickGroupByClauseNode groupByClause = (JQuickGroupByClauseNode) visit(ctx.groupByClause());
            builder.setGroupByClause(groupByClause);
        }
        if (ctx.havingClause() != null) {
            JQuickHavingClauseNode havingClause = (JQuickHavingClauseNode) visit(ctx.havingClause());
            builder.setHavingClause(havingClause);
        }
        if (ctx.orderByClause() != null) {
            JQuickOrderByClauseNode orderByClause = (JQuickOrderByClauseNode) visit(ctx.orderByClause());
            builder.setOrderByClause(orderByClause);
        }
        if (ctx.limitClause() != null) {
            JQuickLimitClauseNode limitClause = (JQuickLimitClauseNode) visit(ctx.limitClause());
            builder.setLimitClause(limitClause);
        }
        return builder.build();
    }
    @Override
    public JQuickSelectSpecNode visitSelectSpec(JQuickSQLParser.SelectSpecContext ctx) {
        boolean distinct = ctx.DISTINCT() != null;
        return new JQuickSelectSpecNode(distinct);
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
    public JQuickFromClauseNode visitFromClause(JQuickSQLParser.FromClauseContext ctx) {
        JQuickTableNameItemNode tableNameItem = (JQuickTableNameItemNode) visit(ctx.tableNameItem());
        return new JQuickFromClauseNode(tableNameItem);
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
    private JQuickJoinType parseJoinType(JQuickSQLParser.JoinTypeContext ctx) {
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
        return JQuickJoinType.INNER;
    }
    @Override
    public JQuickWhereClauseNode visitWhereClause(JQuickSQLParser.WhereClauseContext ctx) {
        JQuickFilterConditionNode filterCondition = (JQuickFilterConditionNode) visit(ctx.filterCondition());
        return new JQuickWhereClauseNode(filterCondition);
    }
    @Override
    public JQuickGroupByClauseNode visitGroupByClause(JQuickSQLParser.GroupByClauseContext ctx) {
        JQuickExpressionsNode expressions = (JQuickExpressionsNode) visit(ctx.expressions());
        return new JQuickGroupByClauseNode(expressions);
    }
    @Override
    public JQuickHavingClauseNode visitHavingClause(JQuickSQLParser.HavingClauseContext ctx) {
        JQuickFilterConditionNode filterCondition = (JQuickFilterConditionNode) visit(ctx.filterCondition());
        return new JQuickHavingClauseNode(filterCondition);
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
    public JQuickSelectElementNode visitSelectElement(JQuickSQLParser.SelectElementContext ctx) {
        JQuickExpressionNode expression = (JQuickExpressionNode) visit(ctx.expression());
        String alias = null;
        if (ctx.uid() != null) {
            JQuickUidNode uid = (JQuickUidNode) visit(ctx.uid());
            alias = uid.getValue();
        }
        return new JQuickSelectElementNode(expression, alias);
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
    public JQuickFullColumnNameNode visitFullColumnName(JQuickSQLParser.FullColumnNameContext ctx) {
        JQuickUidNode uid = (JQuickUidNode) visit(ctx.uid());
        String uidValue = uid.getValue();
        String dottedId = null;
        if (ctx.dottedId() != null) {
            JQuickUidNode dottedUid = (JQuickUidNode) visit(ctx.dottedId().uid());
            dottedId = dottedUid.getValue();
        }
        return new JQuickFullColumnNameNode(uidValue, dottedId);
    }
    @Override
    public String visitTableNameSpec(JQuickSQLParser.TableNameSpecContext ctx) {
        String tableName = ctx.IDENTIFIER().getText();
        return tableName;
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
    public JQuickExpressionNode visitLimitOnly(JQuickSQLParser.LimitOnlyContext ctx) {
        return (JQuickExpressionNode) visit(ctx.expression());
    }
    @Override
    public JQuickLimitClauseNode visitLimitWithOffset(JQuickSQLParser.LimitWithOffsetContext ctx) {
        JQuickExpressionNode offsetExpr = (JQuickExpressionNode) visit(ctx.offset);
        JQuickExpressionNode limitExpr = (JQuickExpressionNode) visit(ctx.limit);
        return new JQuickLimitClauseNode(offsetExpr, limitExpr);
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
    public List<String> visitColumnNames(JQuickSQLParser.ColumnNamesContext ctx) {
        List<String> columnNames = new ArrayList<>();
        for (JQuickSQLParser.UidContext uidCtx : ctx.uid()) {
            JQuickUidNode uid = (JQuickUidNode) visit(uidCtx);
            columnNames.add(uid.getValue());
        }
        return columnNames;
    }
    @Override
    public JQuickSelectStatementNode visitInitialQuery(JQuickSQLParser.InitialQueryContext ctx) {
        return (JQuickSelectStatementNode) visit(ctx.selectStatement());
    }
    @Override
    public JQuickSelectStatementNode visitRecursivePart(JQuickSQLParser.RecursivePartContext ctx) {
        return (JQuickSelectStatementNode) visit(ctx.selectStatement());
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
























}
