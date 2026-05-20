package com.github.paohaijiao.lexer.selectStatement;

import com.github.paohaijiao.parser.JQuickSQLLexer;
import com.github.paohaijiao.parser.JQuickSQLParser;
import com.github.paohaijiao.visitor.JQuickSQLCommonVisistor;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.junit.Test;

public class JQuickSelectStatementTest {
    @Test
    public void selectStatement_havingClause() {
        String rule="having a.age>8";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.HavingClauseContext tree = parser.havingClause();
        JQuickSQLCommonVisistor tv = new JQuickSQLCommonVisistor();
        Object a=tv.visit(tree);
        System.out.println(a);
    }
    @Test
    public void selectStatement_groupByClause() {
        String rule="group by a.age";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.GroupByClauseContext tree = parser.groupByClause();
        JQuickSQLCommonVisistor tv = new JQuickSQLCommonVisistor();
        Object a=tv.visit(tree);
        System.out.println(a);
    }
    @Test
    public void selectStatement_whereClause() {
        String rule="where  a.age=9";
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.WhereClauseContext tree = parser.whereClause();
        JQuickSQLCommonVisistor tv = new JQuickSQLCommonVisistor();
        Object a=tv.visit(tree);
        System.out.println(a);
    }
    @Test
    public void selectStatement_selectClause() {
        String rule="SELECT DISTINCT\n" +
                "    u.user_id,\n" +
                "    u.user_name,\n" +
                "    u.email,\n" +
                "    COUNT(o.order_id) AS order_count,\n" +
                "    SUM(o.amount) AS total_amount,\n" +
                "    AVG(o.amount) AS avg_amount,\n" +
                "    MAX(o.create_time) AS last_order_time,\n" +
                "    CASE \n" +
                "        WHEN amount > 10000 THEN 'VIP'\n" +
                "        WHEN amount > 5000 THEN '高级'\n" +
                "        ELSE '普通'\n" +
                "    END AS user_level\n" +
                "FROM users u\n" +
                "LEFT JOIN orders o ON u.user_id = o.user_id\n" +
                "INNER JOIN user_role ur ON u.user_id = ur.user_id\n" +
                "INNER JOIN role r ON ur.role_id = r.role_id\n" +
                "WHERE \n" +
                "    u.status = 1\n" +
                "    AND u.create_time >= '2024-01-01'\n" +
                "    AND o.create_time BETWEEN '2024-01-01' AND '2024-12-31'\n" +
                "    AND r.role_name IN ('user', 'premium')\n" +
                "GROUP BY \n" +
                "    u.user_id, \n" +
                "    u.user_name, \n" +
                "    u.email\n" +
                "HAVING \n" +
                "    COUNT(o.order_id) > 5 \n" +
                "    AND SUM(o.amount) > 1000\n" +
                "ORDER BY \n" +
                "    total_amount DESC,\n" +
                "    order_count DESC\n" +
                "LIMIT 50 ;";
        System.out.println(rule);
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.SelectClauseContext tree = parser.selectClause();
        JQuickSQLCommonVisistor tv = new JQuickSQLCommonVisistor();
        Object a=tv.visit(tree);
        System.out.println(a);
    }
    @Test
    public void selectStatement_datasetOp() {
        String rule="select * from a union  select * from b";
        System.out.println(rule);
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.DatasetOpContext tree = parser.datasetOp();
        JQuickSQLCommonVisistor tv = new JQuickSQLCommonVisistor();
        Object a=tv.visit(tree);
        System.out.println(a);
    }
    @Test
    public void selectStatement_datasetOp1() {
        String rule="select * from a MINUS  select * from b";
        System.out.println(rule);
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.DatasetOpContext tree = parser.datasetOp();
        JQuickSQLCommonVisistor tv = new JQuickSQLCommonVisistor();
        Object a=tv.visit(tree);
        System.out.println(a);
    }
    @Test
    public void selectStatement_datasetOp2() {
        String rule="select * from a INTERSECT  select * from b";
        System.out.println(rule);
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.DatasetOpContext tree = parser.datasetOp();
        JQuickSQLCommonVisistor tv = new JQuickSQLCommonVisistor();
        Object a=tv.visit(tree);
        System.out.println(a);
    }
    @Test
    public void selectStatement_selectExpression() {
        String rule="select * from a ";
        System.out.println(rule);
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.SelectExpressionContext tree = parser.selectExpression();
        JQuickSQLCommonVisistor tv = new JQuickSQLCommonVisistor();
        Object a=tv.visit(tree);
        System.out.println(a);
    }
    @Test
    public void selectStatement_selectStatement() {
        String rule="select * from a ";
        System.out.println(rule);
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.SelectStatementContext tree = parser.selectStatement();
        JQuickSQLCommonVisistor tv = new JQuickSQLCommonVisistor();
        Object a=tv.visit(tree);
        System.out.println(a);
    }
    @Test
    public void selectStatement_selectStatement1() {
        String rule="WITH sales_cte AS (\n" +
                "    SELECT \n" +
                "        product_id,\n" +
                "        SUM(amount) AS total_sales\n" +
                "    FROM orders\n" +
                "    GROUP BY product_id\n" +
                ")\n" +
                "SELECT \n" +
                "    s.product_id,\n" +
                "    s.total_sales,\n" +
                "    p.product_name\n" +
                "FROM sales_cte s\n" +
                "INNER JOIN products p ON s.product_id = p.product_id\n" +
                "WHERE s.total_sales > 1000\n" +
                "ORDER BY s.total_sales DESC;";
        System.out.println(rule);
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.SelectStatementContext tree = parser.selectStatement();
        JQuickSQLCommonVisistor tv = new JQuickSQLCommonVisistor();
        Object a=tv.visit(tree);
        System.out.println(a);
    }
    @Test
    public void selectStatement_query() {
        String rule="WITH sales_cte AS (\n" +
                "    SELECT \n" +
                "        product_id,\n" +
                "        SUM(amount) AS total_sales\n" +
                "    FROM orders\n" +
                "    GROUP BY product_id\n" +
                ")\n" +
                "SELECT \n" +
                "    s.product_id,\n" +
                "    s.total_sales,\n" +
                "    p.product_name\n" +
                "FROM sales_cte s\n" +
                "INNER JOIN products p ON s.product_id = p.product_id\n" +
                "WHERE s.total_sales > 1000\n" +
                "ORDER BY s.total_sales DESC;";
        System.out.println(rule);
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(rule));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.QueryContext tree = parser.query();
        JQuickSQLCommonVisistor tv = new JQuickSQLCommonVisistor();
        Object a=tv.visit(tree);
        System.out.println(a);
    }
}
