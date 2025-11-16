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
package com.github.paohaijiao.comparisonOperator;

import com.github.paohaijiao.parser.JQuickSQLLexer;
import com.github.paohaijiao.parser.JQuickSQLParser;
import com.github.paohaijiao.visitor.JQuikSQLCommonVisitor;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.Test;

/**
 * packageName com.github.paohaijiao.comparisonOperator
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/8/19
 */
public class JComparisonOperatorTest {
    @Test
    public void eq() {
        String rule = "=";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.ComparisonOperatorContext tree = parser.comparisonOperator();
        JQuikSQLCommonVisitor tv = new JQuikSQLCommonVisitor();
        Object object = tv.visit(tree);
        System.out.println(object);
    }

    @Test
    public void gt() {
        String rule = ">";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.ComparisonOperatorContext tree = parser.comparisonOperator();
        JQuikSQLCommonVisitor tv = new JQuikSQLCommonVisitor();
        Object object = tv.visit(tree);
        System.out.println(object);
    }

    @Test
    public void lt() {
        String rule = "<";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.ComparisonOperatorContext tree = parser.comparisonOperator();
        JQuikSQLCommonVisitor tv = new JQuikSQLCommonVisitor();
        Object object = tv.visit(tree);
        System.out.println(object);
    }

    @Test
    public void le() {
        String rule = "<=";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.ComparisonOperatorContext tree = parser.comparisonOperator();
        JQuikSQLCommonVisitor tv = new JQuikSQLCommonVisitor();
        Object object = tv.visit(tree);
        System.out.println(object);
    }

    @Test
    public void ge() {
        String rule = ">=";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.ComparisonOperatorContext tree = parser.comparisonOperator();
        JQuikSQLCommonVisitor tv = new JQuikSQLCommonVisitor();
        Object object = tv.visit(tree);
        System.out.println(object);
    }

    @Test
    public void neq() {
        String rule = "<>";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.ComparisonOperatorContext tree = parser.comparisonOperator();
        JQuikSQLCommonVisitor tv = new JQuikSQLCommonVisitor();
        Object object = tv.visit(tree);
        System.out.println(object);
    }

    @Test
    public void neq1() {
        String rule = "!=";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.ComparisonOperatorContext tree = parser.comparisonOperator();
        JQuikSQLCommonVisitor tv = new JQuikSQLCommonVisitor();
        Object object = tv.visit(tree);
        System.out.println(object);
    }

    @Test
    public void and() {
        String rule = "and";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.LogicalOperatorContext tree = parser.logicalOperator();
        JQuikSQLCommonVisitor tv = new JQuikSQLCommonVisitor();
        Object object = tv.visit(tree);
        System.out.println(object);
    }

    @Test
    public void or() {
        String rule = "or";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.LogicalOperatorContext tree = parser.logicalOperator();
        JQuikSQLCommonVisitor tv = new JQuikSQLCommonVisitor();
        Object object = tv.visit(tree);
        System.out.println(object);
    }

    @Test
    public void xor() {
        String rule = "xor";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.LogicalOperatorContext tree = parser.logicalOperator();
        JQuikSQLCommonVisitor tv = new JQuikSQLCommonVisitor();
        Object object = tv.visit(tree);
        System.out.println(object);
    }

    @Test
    public void unaryOperator() {
        String rule = "!";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.UnaryOperatorContext tree = parser.unaryOperator();
        JQuikSQLCommonVisitor tv = new JQuikSQLCommonVisitor();
        Object object = tv.visit(tree);
        System.out.println(object);
    }

    @Test
    public void unaryOperator1() {
        String rule = "~";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.UnaryOperatorContext tree = parser.unaryOperator();
        JQuikSQLCommonVisitor tv = new JQuikSQLCommonVisitor();
        Object object = tv.visit(tree);
        System.out.println(object);
    }

    @Test
    public void unaryOperator12() {
        String rule = "+";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.UnaryOperatorContext tree = parser.unaryOperator();
        JQuikSQLCommonVisitor tv = new JQuikSQLCommonVisitor();
        Object object = tv.visit(tree);
        System.out.println(object);
    }

    @Test
    public void unaryOperator3() {
        String rule = "-";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.UnaryOperatorContext tree = parser.unaryOperator();
        JQuikSQLCommonVisitor tv = new JQuikSQLCommonVisitor();
        Object object = tv.visit(tree);
        System.out.println(object);
    }

    @Test
    public void unaryOperator4() {
        String rule = "not";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.UnaryOperatorContext tree = parser.unaryOperator();
        JQuikSQLCommonVisitor tv = new JQuikSQLCommonVisitor();
        Object object = tv.visit(tree);
        System.out.println(object);
    }

    @Test
    public void mathOperator() {
        String rule = "*";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.MathOperatorContext tree = parser.mathOperator();
        JQuikSQLCommonVisitor tv = new JQuikSQLCommonVisitor();
        Object object = tv.visit(tree);
        System.out.println(object);
    }

    @Test
    public void mathOperator1() {
        String rule = "/";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.MathOperatorContext tree = parser.mathOperator();
        JQuikSQLCommonVisitor tv = new JQuikSQLCommonVisitor();
        Object object = tv.visit(tree);
        System.out.println(object);
    }

    @Test
    public void mathOperator2() {
        String rule = "%";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.MathOperatorContext tree = parser.mathOperator();
        JQuikSQLCommonVisitor tv = new JQuikSQLCommonVisitor();
        Object object = tv.visit(tree);
        System.out.println(object);
    }

    @Test
    public void mathOperator3() {
        String rule = "+";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.MathOperatorContext tree = parser.mathOperator();
        JQuikSQLCommonVisitor tv = new JQuikSQLCommonVisitor();
        Object object = tv.visit(tree);
        System.out.println(object);
    }

    @Test
    public void mathOperator4() {
        String rule = "-";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.MathOperatorContext tree = parser.mathOperator();
        JQuikSQLCommonVisitor tv = new JQuikSQLCommonVisitor();
        Object object = tv.visit(tree);
        System.out.println(object);
    }

    @Test
    public void mathOperator5() {
        String rule = "--";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.MathOperatorContext tree = parser.mathOperator();
        JQuikSQLCommonVisitor tv = new JQuikSQLCommonVisitor();
        Object object = tv.visit(tree);
        System.out.println(object);
    }
}
