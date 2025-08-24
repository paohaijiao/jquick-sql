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

import com.github.paohaijiao.dataset.JDataSet;
import com.github.paohaijiao.enums.JEngineEnums;
import com.github.paohaijiao.exception.JAssert;
import com.github.paohaijiao.param.JContext;
import com.github.paohaijiao.parser.JQuickSQLLexer;
import com.github.paohaijiao.parser.JQuickSQLParser;
import com.github.paohaijiao.support.JDataSetHolder;
import org.antlr.v4.runtime.CommonTokenStream;

/**
 * packageName com.github.paohaijiao.visitor
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/8/11
 */
public class JQuikSQLCommonVisitor extends JQuickSQLOlapVisitor{

    public JQuikSQLCommonVisitor(JDataSetHolder dataSetHolder, JContext context, JQuickSQLLexer lexer, CommonTokenStream tokenStream, JQuickSQLParser parser) {
        this.context = context;
        this.lexer = lexer;
        this.tokenStream = tokenStream;
        this.parser = parser;
        this.dataSetHolder = dataSetHolder;
    }

    public JQuikSQLCommonVisitor(JDataSetHolder dataSetHolder, JQuickSQLLexer lexer, CommonTokenStream tokenStream, JQuickSQLParser parser) {
        this.context = new JContext();
        this.dataSetHolder = dataSetHolder;
        this.lexer = lexer;
        this.tokenStream = tokenStream;
        this.parser = parser;
    }
    public JQuikSQLCommonVisitor() {
        this.context = new JContext();
    }
    public JQuikSQLCommonVisitor engine(JEngineEnums engine) {
        this.engine=engine;
        return this;
    }
    public JQuikSQLCommonVisitor engine(JDataSetHolder holder) {
        this.dataSetHolder=holder;
        return this;
    }
    @Override
    public JDataSet visitQuery(JQuickSQLParser.QueryContext ctx) {
        return (JDataSet)visit(ctx.selectStatement());
    }
    @Override
    public JDataSet visitSingleQuery(JQuickSQLParser.SingleQueryContext ctx) {
        return visitSelectExpression(ctx.selectExpression());
    }
    @Override
    public JDataSet visitSelectExpression(JQuickSQLParser.SelectExpressionContext ctx) {
        String text=ctx.getText();
       if(ctx.selectClause()!=null){
            return visitSelectClause(ctx.selectClause());
        }
        JAssert.throwNewException("not support this statement");
        return null;
    }




}
