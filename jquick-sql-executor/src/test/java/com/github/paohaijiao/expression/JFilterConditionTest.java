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
package com.github.paohaijiao.expression;

import com.github.paohaijiao.parser.JQuickSQLLexer;
import com.github.paohaijiao.parser.JQuickSQLParser;
import com.github.paohaijiao.visitor.JQuikSQLCommonVisitor;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.Test;

/**
 * packageName com.github.paohaijiao.expression
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/8/30
 */
public class JFilterConditionTest {
    @Test
    public void and() {
        String rule=" a is null and b=1";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.FilterConditionContext tree = parser.filterCondition();
        JQuikSQLCommonVisitor tv = new JQuikSQLCommonVisitor();
        Object object = tv.visit(tree);
        System.out.println(object);
    }
    @Test
    public void or() {
        String rule=" a is null or b=1";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.FilterConditionContext tree = parser.filterCondition();
        JQuikSQLCommonVisitor tv = new JQuikSQLCommonVisitor();
        Object object = tv.visit(tree);
        System.out.println(object);
    }
    @Test
    public void and1() {
        String rule=" a is null or (b=1 and c=5)";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.FilterConditionContext tree = parser.filterCondition();
        JQuikSQLCommonVisitor tv = new JQuikSQLCommonVisitor();
        Object object = tv.visit(tree);
        System.out.println(object);
    }
}
