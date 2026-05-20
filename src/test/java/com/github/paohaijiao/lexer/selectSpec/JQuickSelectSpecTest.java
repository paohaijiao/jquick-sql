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
    public void selectSpec_recursivePart() {
        String rule="select * from a";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.RecursivePartContext tree = parser.recursivePart();
        JQuickSQLCommonVisistor tv = new JQuickSQLCommonVisistor();
        Object a=tv.visit(tree);
        System.out.println(a);
    }
    @Test
    public void selectSpec_initialQuery() {
        String rule="select * from a";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.InitialQueryContext tree = parser.initialQuery();
        JQuickSQLCommonVisistor tv = new JQuickSQLCommonVisistor();
        Object a=tv.visit(tree);
        System.out.println(a);
    }
    @Test
    public void selectSpec_columnNames() {
        String rule="a,b,c,d";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.ColumnNamesContext tree = parser.columnNames();
        JQuickSQLCommonVisistor tv = new JQuickSQLCommonVisistor();
        Object a=tv.visit(tree);
        System.out.println(a);
    }
    @Test
    public void selectSpec_commonTableExpression() {
        String rule="table (a,b,c,d) as (select a,b,c,d from t2) ";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.CommonTableExpressionContext tree = parser.commonTableExpression();
        JQuickSQLCommonVisistor tv = new JQuickSQLCommonVisistor();
        Object a=tv.visit(tree);
        System.out.println(a);
    }
    @Test
    public void selectSpec_commonTableExpression1() {
        String rule="table (a,b,c,d) as (select a,b,c,d from t2 UNION ALL select a,b,c,d from t3) ";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.CommonTableExpressionContext tree = parser.commonTableExpression();
        JQuickSQLCommonVisistor tv = new JQuickSQLCommonVisistor();
        Object a=tv.visit(tree);
        System.out.println(a);
    }
    @Test
    public void selectSpec_limitWithOffset() {
        String rule="2,4 ";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.LimitWithOffsetContext tree = parser.limitWithOffset();
        JQuickSQLCommonVisistor tv = new JQuickSQLCommonVisistor();
        Object a=tv.visit(tree);
        System.out.println(a);
    }
    @Test
    public void selectSpec_limitOnly() {
        String rule="2";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.LimitOnlyContext tree = parser.limitOnly();
        JQuickSQLCommonVisistor tv = new JQuickSQLCommonVisistor();
        Object a=tv.visit(tree);
        System.out.println(a);
    }
    @Test
    public void selectSpec_limitClause() {
        String rule="limit 2,4";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.LimitClauseContext tree = parser.limitClause();
        JQuickSQLCommonVisistor tv = new JQuickSQLCommonVisistor();
        Object a=tv.visit(tree);
        System.out.println(a);
    }
    @Test
    public void selectSpec_limitClause1() {
        String rule="limit 2";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.LimitClauseContext tree = parser.limitClause();
        JQuickSQLCommonVisistor tv = new JQuickSQLCommonVisistor();
        Object a=tv.visit(tree);
        System.out.println(a);
    }
    @Test
    public void selectSpec_orderByExpression() {
        String rule=" a.id desc";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.OrderByExpressionContext tree = parser.orderByExpression();
        JQuickSQLCommonVisistor tv = new JQuickSQLCommonVisistor();
        Object a=tv.visit(tree);
        System.out.println(a);
    }
    @Test
    public void selectSpec_orderByClause() {
        String rule=" order by a.id desc";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.OrderByClauseContext tree = parser.orderByClause();
        JQuickSQLCommonVisistor tv = new JQuickSQLCommonVisistor();
        Object a=tv.visit(tree);
        System.out.println(a);
    }
    @Test
    public void selectSpec_joinType() {
        String rule=" INNER";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.JoinTypeContext tree = parser.joinType();
        JQuickSQLCommonVisistor tv = new JQuickSQLCommonVisistor();
        Object a=tv.visit(tree);
        System.out.println(a);
    }
    @Test
    public void selectSpec_joinType1() {
        String rule=" CROSS";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.JoinTypeContext tree = parser.joinType();
        JQuickSQLCommonVisistor tv = new JQuickSQLCommonVisistor();
        Object a=tv.visit(tree);
        System.out.println(a);
    }
    @Test
    public void selectSpec_joinType2s() {
        String rule=" LEFT";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.JoinTypeContext tree = parser.joinType();
        JQuickSQLCommonVisistor tv = new JQuickSQLCommonVisistor();
        Object a=tv.visit(tree);
        System.out.println(a);
    }
    @Test
    public void selectSpec_joinType3() {
        String rule=" RIGHT";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.JoinTypeContext tree = parser.joinType();
        JQuickSQLCommonVisistor tv = new JQuickSQLCommonVisistor();
        Object a=tv.visit(tree);
        System.out.println(a);
    }
    @Test
    public void selectSpec_joinType4() {
        String rule=" NATURAL";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.JoinTypeContext tree = parser.joinType();
        JQuickSQLCommonVisistor tv = new JQuickSQLCommonVisistor();
        Object a=tv.visit(tree);
        System.out.println(a);
    }
    @Test
    public void selectSpec_joinType5() {
        String rule=" FULL";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.JoinTypeContext tree = parser.joinType();
        JQuickSQLCommonVisistor tv = new JQuickSQLCommonVisistor();
        Object a=tv.visit(tree);
        System.out.println(a);
    }
    @Test
    public void selectSpec_tableNameSpec() {
        String rule=" asdfg";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.TableNameSpecContext tree = parser.tableNameSpec();
        JQuickSQLCommonVisistor tv = new JQuickSQLCommonVisistor();
        Object a=tv.visit(tree);
        System.out.println(a);
    }
    @Test
    public void selectSpec_tableNameItem() {
        String rule=" student as t1";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.TableNameItemContext tree = parser.tableNameItem();
        JQuickSQLCommonVisistor tv = new JQuickSQLCommonVisistor();
        Object a=tv.visit(tree);
        System.out.println(a);
    }
    @Test
    public void selectSpec_tableNameItem1() {
        String rule="  (select * from a) as t2 ";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.TableNameItemContext tree = parser.tableNameItem();
        JQuickSQLCommonVisistor tv = new JQuickSQLCommonVisistor();
        Object a=tv.visit(tree);
        System.out.println(a);
    }
    @Test
    public void selectSpec_joinClause() {
        String rule=" INNER JOIN student t1 ON t1.id = t1.id ";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.JoinClauseContext tree = parser.joinClause();
        JQuickSQLCommonVisistor tv = new JQuickSQLCommonVisistor();
        Object a=tv.visit(tree);
        System.out.println(a);
    }
    @Test
    public void selectSpec_joinClause2() {
        String rule=" INNER JOIN (select * from theater b) as t1 ON t1.id = t1.id ";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.JoinClauseContext tree = parser.joinClause();
        JQuickSQLCommonVisistor tv = new JQuickSQLCommonVisistor();
        Object a=tv.visit(tree);
        System.out.println(a);
    }
    @Test
    public void selectSpec_fromClause() {
        String rule=" FROM STUDENT T1";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.FromClauseContext tree = parser.fromClause();
        JQuickSQLCommonVisistor tv = new JQuickSQLCommonVisistor();
        Object a=tv.visit(tree);
        System.out.println(a);
    }
    @Test
    public void selectSpec_selectElement() {
        String rule="  A.NAME AS NAME ";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.SelectElementContext tree = parser.selectElement();
        JQuickSQLCommonVisistor tv = new JQuickSQLCommonVisistor();
        Object a=tv.visit(tree);
        System.out.println(a);
    }
    @Test
    public void selectSpec_selectElements() {
        String rule="  A.NAME AS NAME ";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.SelectElementsContext tree = parser.selectElements();
        JQuickSQLCommonVisistor tv = new JQuickSQLCommonVisistor();
        Object a=tv.visit(tree);
        System.out.println(a);
    }
    @Test
    public void selectSpec_selectElements1() {
        String rule="  * ";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.SelectElementsContext tree = parser.selectElements();
        JQuickSQLCommonVisistor tv = new JQuickSQLCommonVisistor();
        Object a=tv.visit(tree);
        System.out.println(a);
    }
    @Test
    public void selectSpec_selectElements2() {
        String rule="A.NAME AS NAME,B.NAME AS BNAME";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.SelectElementsContext tree = parser.selectElements();
        JQuickSQLCommonVisistor tv = new JQuickSQLCommonVisistor();
        Object a=tv.visit(tree);
        System.out.println(a);
    }
    @Test
    public void selectSpec_selectSpec() {
        String rule="all";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.SelectSpecContext tree = parser.selectSpec();
        JQuickSQLCommonVisistor tv = new JQuickSQLCommonVisistor();
        Object a=tv.visit(tree);
        System.out.println(a);
    }
    @Test
    public void selectSpec_selectSpec1() {
        String rule="DISTINCT";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.SelectSpecContext tree = parser.selectSpec();
        JQuickSQLCommonVisistor tv = new JQuickSQLCommonVisistor();
        Object a=tv.visit(tree);
        System.out.println(a);
    }
}
