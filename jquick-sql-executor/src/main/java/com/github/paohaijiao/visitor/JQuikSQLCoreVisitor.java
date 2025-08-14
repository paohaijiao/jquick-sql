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
import com.github.paohaijiao.engine.JEntityQueryEngine;
import com.github.paohaijiao.param.JContext;
import com.github.paohaijiao.parser.JQuickSQLBaseVisitor;
import com.github.paohaijiao.parser.JQuickSQLLexer;
import com.github.paohaijiao.parser.JQuickSQLParser;
import org.antlr.v4.runtime.CommonTokenStream;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * packageName com.github.paohaijiao.visitor
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/8/11
 */
public class JQuikSQLCoreVisitor extends JQuickSQLBaseVisitor {

    protected JContext context;

    protected JQuickSQLLexer lexer;

    protected CommonTokenStream tokenStream;

    protected JQuickSQLParser parser;

    protected final Map<String, JDataSet> tableRegistry = new HashMap<>();


    public void registerDataSet(String tableName, JDataSet dataSet) {
        tableRegistry.put(tableName, dataSet);
    }
}