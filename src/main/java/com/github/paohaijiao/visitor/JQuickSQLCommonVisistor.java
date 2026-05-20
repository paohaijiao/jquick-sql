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
import com.github.paohaijiao.enums.JQuickSQLOperationType;
import com.github.paohaijiao.param.JContext;
import com.github.paohaijiao.parser.JQuickSQLParser;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.util.ArrayList;
import java.util.List;

/**
 * packageName com.github.paohaijiao.visitor
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/5/17
 */
public class JQuickSQLCommonVisistor extends JQuickSelectSpecVisitor {

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
        List<JQuickSelectClauseNode> selectClauses = new ArrayList<>();
        List<JQuickSQLOperationType> operators = new ArrayList<>();
        selectClauses.add((JQuickSelectClauseNode) visit(ctx.selectClause(0)));
        for (int i = 1; i < ctx.selectClause().size(); i++) {
            int operatorIndex = i * 2 - 1;
            TerminalNode operatorNode = (TerminalNode) ctx.getChild(operatorIndex);
            if (operatorNode.getSymbol().getType() == JQuickSQLParser.UNION) {
                operators.add(JQuickSQLOperationType.UNION);
            } else if (operatorNode.getSymbol().getType() == JQuickSQLParser.MINUS) {
                operators.add(JQuickSQLOperationType.MINUS);
            } else if (operatorNode.getSymbol().getType() == JQuickSQLParser.INTERSECT) {
                operators.add(JQuickSQLOperationType.INTERSECT);
            }
            selectClauses.add((JQuickSelectClauseNode) visit(ctx.selectClause(i)));
        }
        return new JQuickDataSetOpNode(selectClauses, operators);
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
































}
