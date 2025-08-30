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
 * @since 2025/8/17
 */
public class JExpressionAtomTest {
    @Test
    public void expressionAtom() {
        String rule="1";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.ExpressionAtomContext tree = parser.expressionAtom();
        JQuikSQLCommonVisitor tv = new JQuikSQLCommonVisitor();
        Object object = tv.visit(tree);
        System.out.println(object);
    }
    @Test
    public void expressionAtom1() {
        String rule="'scdxd'";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.ExpressionAtomContext tree = parser.expressionAtom();
        JQuikSQLCommonVisitor tv = new JQuikSQLCommonVisitor();
        Object object = tv.visit(tree);
        System.out.println(object);
    }
    @Test
    public void date() {
        String rule="'2026-01-01'::'yyyy-MM-dd'";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.ExpressionAtomContext tree = parser.expressionAtom();
        JQuikSQLCommonVisitor tv = new JQuikSQLCommonVisitor();
        Object object = tv.visit(tree);
        System.out.println(object);
    }
    @Test
    public void null_() {
        String rule="null";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.ExpressionAtomContext tree = parser.expressionAtom();
        JQuikSQLCommonVisitor tv = new JQuikSQLCommonVisitor();
        Object object = tv.visit(tree);
        System.out.println(object);
    }
    @Test
    public void bool() {
        String rule="true";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.ExpressionAtomContext tree = parser.expressionAtom();
        JQuikSQLCommonVisitor tv = new JQuikSQLCommonVisitor();
        Object object = tv.visit(tree);
        System.out.println(object);
    }


    @Test
    public void fullColumnName() {
        String rule="b.c";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.ExpressionAtomContext tree = parser.expressionAtom();
        JQuikSQLCommonVisitor tv = new JQuikSQLCommonVisitor();
        Object object = tv.visit(tree);
        System.out.println(object);
    }
    @Test
    public void functionCall() {
        String rule="sum(1,2)";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.ExpressionAtomContext tree = parser.expressionAtom();
        JQuikSQLCommonVisitor tv = new JQuikSQLCommonVisitor();
        Object object = tv.visit(tree);
        System.out.println(object);
    }
    @Test
    public void nestedExpressionAtom() {
        String rule="(1,2)";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.ExpressionAtomContext tree = parser.expressionAtom();
        JQuikSQLCommonVisitor tv = new JQuikSQLCommonVisitor();
        Object object = tv.visit(tree);
        System.out.println(object);
    }
    @Test
    public void subqueryExperssionAtom() {
        String rule="(select 1 from a)";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.ExpressionAtomContext tree = parser.expressionAtom();
        JQuikSQLCommonVisitor tv = new JQuikSQLCommonVisitor();
        Object object = tv.visit(tree);
        System.out.println(object);
    }
    @Test
    public void mathExpressionAtom() {
        String rule="1+3";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.ExpressionAtomContext tree = parser.expressionAtom();
        JQuikSQLCommonVisitor tv = new JQuikSQLCommonVisitor();
        Object object = tv.visit(tree);
        System.out.println(object);
    }
    @Test
    public void unaryExpressionAtom() {
        String rule="!true";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.ExpressionAtomContext tree = parser.expressionAtom();
        JQuikSQLCommonVisitor tv = new JQuikSQLCommonVisitor();
        Object object = tv.visit(tree);
        System.out.println(object);
    }

}
