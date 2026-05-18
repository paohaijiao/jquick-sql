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
package com.github.paohaijiao.executor;

import com.github.paohaijiao.antlr.impl.JAbstractAntlrExecutor;
import com.github.paohaijiao.ast.JQuickQueryNode;
import com.github.paohaijiao.config.JQuickSqlConfig;
import com.github.paohaijiao.console.JConsole;
import com.github.paohaijiao.exception.JAntlrExecutionException;
import com.github.paohaijiao.param.JContext;
import com.github.paohaijiao.parser.JQuickSQLLexer;
import com.github.paohaijiao.parser.JQuickSQLParser;
import com.github.paohaijiao.visitor.JQuickSQLCommonVisistor;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.TokenStream;

/**
 * packageName com.github.paohaijiao.executor
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/5/17
 */
public class JQuickSQLExecutor extends JAbstractAntlrExecutor<String, JQuickQueryNode> {

    private JContext context=new JContext();

    private JQuickSqlConfig config=new JQuickSqlConfig();

    private JConsole console=JConsole.initConsoleEnvironment();

    public JQuickSQLExecutor(JContext jcontext,JQuickSqlConfig config){
        if(null!=jcontext&&!jcontext.isEmpty()){
            this.context.putAll(jcontext);
        }
        if(null!=config){
            this.config=config;
        }
    }
    public JQuickSQLExecutor(JQuickSqlConfig config){
        this(null,config);
    }

    public JQuickSQLExecutor(JContext context){
        this(context,null);
    }

    public JQuickSQLExecutor( ){
        this(null,null);
    }

    @Override
    protected Lexer createLexer(CharStream input) {
        return new JQuickSQLLexer(input);
    }

    @Override
    protected Parser createParser(TokenStream tokens) {
        return new JQuickSQLParser(tokens);
    }

    @Override
    protected JQuickQueryNode parse(Parser parser) throws JAntlrExecutionException {
        JQuickSQLParser calcParser = (JQuickSQLParser) parser;
        JQuickSQLParser.QueryContext tree = calcParser.query();
        JQuickSQLCommonVisistor visitor = new JQuickSQLCommonVisistor(this.context,this.config);
        JQuickQueryNode queryNode= visitor.visitQuery(tree);
        return queryNode;
    }
}
