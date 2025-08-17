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

import com.github.paohaijiao.config.JQuickSQLConfig;
import com.github.paohaijiao.dataset.JDataSet;
import com.github.paohaijiao.param.JContext;
import com.github.paohaijiao.parser.JQuickSQLLexer;
import com.github.paohaijiao.parser.JQuickSQLParser;
import com.github.paohaijiao.visitor.JQuikSQLCommonVisitor;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

/**
 * packageName com.github.paohaijiao.executor
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/8/17
 */
public class JQuickSQLExecutor {

    private JContext context=new JContext();

    private JQuickSQLConfig config=new JQuickSQLConfig();

    public JQuickSQLExecutor(JContext context){
        this.context = context;
    }
    public JQuickSQLExecutor(JQuickSQLConfig config){
        this.context = context;
        this.config =  config;
    }
    public JQuickSQLExecutor(JContext context,JQuickSQLConfig config){
        this.context = context;
        this.config =  config;
    }
    public JQuickSQLExecutor(){
        this.context = new JContext();
    }

    public JDataSet execute(String sql){
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(sql));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.QueryContext tree = parser.query();
        JContext params = new JContext();
        JQuikSQLCommonVisitor tv = new JQuikSQLCommonVisitor(params,lexer,tokens,parser);
        Object object = tv.visit(tree);
        return (JDataSet)object;
    }
}
