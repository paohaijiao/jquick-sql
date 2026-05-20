package com.github.paohaijiao.lexer.selectSpec;

import com.github.paohaijiao.parser.JQuickSQLLexer;
import com.github.paohaijiao.parser.JQuickSQLParser;
import com.github.paohaijiao.visitor.JQuickSQLCommonVisistor;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.Test;

public class JQuickSelectSpecTest {

    @Test
    public void selectSpec_filterCondition() {
        String rule="true";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.FilterConditionContext tree = parser.filterCondition();
        JQuickSQLCommonVisistor tv = new JQuickSQLCommonVisistor();
        Object a=tv.visit(tree);
        System.out.println(a);
    }
    @Test
    public void selectSpec_filterCondition1() {
        String rule="true and false ";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.FilterConditionContext tree = parser.filterCondition();
        JQuickSQLCommonVisistor tv = new JQuickSQLCommonVisistor();
        Object a=tv.visit(tree);
        System.out.println(a);
    }
    @Test
    public void selectSpec_filterCondition2() {
        String rule="true or false ";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.FilterConditionContext tree = parser.filterCondition();
        JQuickSQLCommonVisistor tv = new JQuickSQLCommonVisistor();
        Object a=tv.visit(tree);
        System.out.println(a);
    }
    @Test
    public void selectSpec_filterCondition3() {
        String rule="(true) ";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.FilterConditionContext tree = parser.filterCondition();
        JQuickSQLCommonVisistor tv = new JQuickSQLCommonVisistor();
        Object a=tv.visit(tree);
        System.out.println(a);
    }
    @Test
    public void selectSpec_functionArg() {
        String rule="32 ";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.FunctionArgContext tree = parser.functionArg();
        JQuickSQLCommonVisistor tv = new JQuickSQLCommonVisistor();
        Object a=tv.visit(tree);
        System.out.println(a);
    }
    @Test
    public void selectSpec_functionArgs() {
        String rule="32,8 ";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.FunctionArgsContext tree = parser.functionArgs();
        JQuickSQLCommonVisistor tv = new JQuickSQLCommonVisistor();
        Object a=tv.visit(tree);
        System.out.println(a);
    }
    @Test
    public void selectSpec_functionCall() {
        String rule="add(32,8)";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.FunctionCallContext tree = parser.functionCall();
        JQuickSQLCommonVisistor tv = new JQuickSQLCommonVisistor();
        Object a=tv.visit(tree);
        System.out.println(a);
    }
    @Test
    public void selectSpec_recursivePart() {//error
        String rule="select * from a";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.RecursivePartContext tree = parser.recursivePart();
        JQuickSQLCommonVisistor tv = new JQuickSQLCommonVisistor();
        Object a=tv.visit(tree);
        System.out.println(a);
    }
}
