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
import com.github.paohaijiao.param.JContext;
import com.github.paohaijiao.parser.JQuickSQLParser;

import java.util.ArrayList;
import java.util.List;

/**
 * packageName com.github.paohaijiao.visitor
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/5/17
 */
public class JQuickSQLCommonVisistor extends JQuickSQLSelectStatement {

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
