package com.github.paohaijiao.lexer;
import com.github.paohaijiao.parser.JQuickSQLLexer;
import com.github.paohaijiao.parser.JQuickSQLParser;
import com.github.paohaijiao.visitor.JQuickSQLCommonVisistor;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.Test;

public class JQuickSQLValueTest {
    @Test
    public void simpleId() {
        String rule="a";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.SimpleIdContext tree = parser.simpleId();
        JQuickSQLCommonVisistor tv = new JQuickSQLCommonVisistor();
        Object a=tv.visit(tree);
        System.out.println(a);
    }
    @Test
    public void stringLiteral() {
        String rule="\"a\"";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.StringLiteralContext tree = parser.stringLiteral();
        JQuickSQLCommonVisistor tv = new JQuickSQLCommonVisistor();
        Object a=  tv.visit(tree);
        System.out.println(a);
    }
    @Test
    public void booleanLiteral() {
        String rule="true";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.BooleanLiteralContext tree = parser.booleanLiteral();
        JQuickSQLCommonVisistor tv = new JQuickSQLCommonVisistor();
        Object a=  tv.visit(tree);
        System.out.println(a);
    }
    @Test
    public void uid() {
        String rule="a";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.UidContext tree = parser.uid();
        JQuickSQLCommonVisistor tv = new JQuickSQLCommonVisistor();
        Object a=  tv.visit(tree);
        System.out.println(a);
    }
    @Test
    public void dottedId() {
        String rule=".a";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.DottedIdContext tree = parser.dottedId();
        JQuickSQLCommonVisistor tv = new JQuickSQLCommonVisistor();
        Object a=  tv.visit(tree);
        System.out.println(a);
    }
    @Test
    public void format() {
        String rule="\"YYYY-MM-DD\"";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.FormatContext tree = parser.format();
        JQuickSQLCommonVisistor tv = new JQuickSQLCommonVisistor();
        Object a=  tv.visit(tree);
        System.out.println(a);
    }
    @Test
    public void dateLiteral() {
        String rule="\"2026-01-02\"::\"YYYY-MM-DD\"";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.DateLiteralContext tree = parser.dateLiteral();
        JQuickSQLCommonVisistor tv = new JQuickSQLCommonVisistor();
        Object a=  tv.visit(tree);
        System.out.println(a);
    }
    @Test
    public void null_literal() {
        String rule="null";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.Null_literalContext tree = parser.null_literal();
        JQuickSQLCommonVisistor tv = new JQuickSQLCommonVisistor();
        Object a=  tv.visit(tree);
        System.out.println(a);
    }
    @Test
    public void decimal_literal() {
        String rule="12.3";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.Decimal_literalContext tree = parser.decimal_literal();
        JQuickSQLCommonVisistor tv = new JQuickSQLCommonVisistor();
        Object a=  tv.visit(tree);
        System.out.println(a);
    }
    @Test
    public void constant_decimal_literal() {
        String rule="12.3";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.ConstantContext tree = parser.constant();
        JQuickSQLCommonVisistor tv = new JQuickSQLCommonVisistor();
        Object a=  tv.visit(tree);
        System.out.println(a);
    }
    @Test
    public void constant_stringLiteral() {
        String rule="\"YYYY-MM-DD\"";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.ConstantContext tree = parser.constant();
        JQuickSQLCommonVisistor tv = new JQuickSQLCommonVisistor();
        Object a=  tv.visit(tree);
        System.out.println(a);
    }
    @Test
    public void negativeconstant_decimal_literal() {
        String rule="-12.3";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.ConstantContext tree = parser.constant();
        JQuickSQLCommonVisistor tv = new JQuickSQLCommonVisistor();
        Object a=  tv.visit(tree);
        System.out.println(a);
    }
    @Test
    public void negativeconstant_booleanLiteral() {
        String rule="true";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.ConstantContext tree = parser.constant();
        JQuickSQLCommonVisistor tv = new JQuickSQLCommonVisistor();
        Object a=  tv.visit(tree);
        System.out.println(a);
    }
    @Test
    public void constant_null_literal() {
        String rule="null";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.ConstantContext tree = parser.constant();
        JQuickSQLCommonVisistor tv = new JQuickSQLCommonVisistor();
        Object a=  tv.visit(tree);
        System.out.println(a);
    }
    @Test
    public void constant_dateLiteral() {
        String rule="\"2026-01-01\"::\"YYYY-MM-DD\"";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.ConstantContext tree = parser.constant();
        JQuickSQLCommonVisistor tv = new JQuickSQLCommonVisistor();
        Object a=  tv.visit(tree);
        System.out.println(a);
    }
}
