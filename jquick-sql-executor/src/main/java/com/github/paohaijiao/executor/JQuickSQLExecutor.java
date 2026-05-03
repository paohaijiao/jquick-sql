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
import com.github.paohaijiao.environment.JQuickSQLRuntimeEnvironment;
import com.github.paohaijiao.exception.JAssert;
import com.github.paohaijiao.factory.JQuickSqlAbilityProviderFactory;
import com.github.paohaijiao.param.JContext;
import com.github.paohaijiao.parser.JQuickSQLLexer;
import com.github.paohaijiao.parser.JQuickSQLParser;
import com.github.paohaijiao.provider.JQuickSqlAbilityProvider;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.support.JQuickSqlDataSetHolder;
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

    private JQuickSQLRuntimeEnvironment environment;

    public JQuickSQLExecutor(JQuickSQLRuntimeEnvironment env){
        JAssert.notNull(env,"JQuickSQLRuntimeEnvironment required not null");
        JAssert.notNull(env.getAbilityProvider(),"provider required not null");
        JAssert.notNull(env.getClientConfig(),"client required not null");
        JAssert.notNull(env.getDataSet(),"dataset required not empty");
        this.environment = env;
    }

    public JQuickDataSet execute(String sql) {
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(sql));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.QueryContext tree = parser.query();
        JQuickSqlAbilityProviderFactory factory = new JQuickSqlAbilityProviderFactory(environment.getClientConfig());
        JQuickSqlAbilityProvider provider=factory.create(environment.getAbilityProvider());
        JQuickSqlDataSetHolder holder=new JQuickSqlDataSetHolder();
        for (String table:environment.getDataSet().keySet()){
            holder.addDataSet(table,environment.getDataSet().get(table));
        }
        JQuikSQLCommonVisitor tv = new JQuikSQLCommonVisitor(provider,holder, lexer, tokens, parser);
        Object object = tv.visit(tree);
        return (JQuickDataSet) object;
    }
}
