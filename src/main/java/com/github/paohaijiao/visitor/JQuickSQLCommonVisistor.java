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

import com.github.paohaijiao.ast.JQuickQueryNode;
import com.github.paohaijiao.ast.JQuickSelectStatementNode;
import com.github.paohaijiao.config.JQuickSqlConfig;
import com.github.paohaijiao.param.JContext;
import com.github.paohaijiao.parser.JQuickSQLParser;

/**
 * packageName com.github.paohaijiao.visitor
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/5/17
 */
public class JQuickSQLCommonVisistor extends JQuickSQLCoreVisistor {

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


}
