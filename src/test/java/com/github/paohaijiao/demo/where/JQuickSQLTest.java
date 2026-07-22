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
package com.github.paohaijiao.demo.where;

import com.github.paohaijiao.engine.JQuickSQL;
import com.github.paohaijiao.statement.JQuickColumnMeta;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class JQuickSQLTest {

    private static JQuickSQL sql;

    @BeforeClass
    public static void setUpClass() {
        sql = JQuickSQL.embedded();
        registerTestData();
    }

    @AfterClass
    public static void tearDownClass() {
        if (sql != null) {
            sql.shutdown();
        }
    }
    private static Date getDate(String date)  {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            return formatter.parse(date);
        }catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }
    private static void registerTestData() {
        List<JQuickColumnMeta> userColumns = Arrays.asList(
                new JQuickColumnMeta("id", Integer.class, "users"),
                new JQuickColumnMeta("name", String.class, "users"),
                new JQuickColumnMeta("age", Integer.class, "users"),
                new JQuickColumnMeta("status", String.class, "users"),
                new JQuickColumnMeta("enable", String.class, "users"),
                new JQuickColumnMeta("addr", String.class, "users"),
                new JQuickColumnMeta("birthday", Date.class, "users")
        );

        List<JQuickRow> userRows = Arrays.asList(
                createRow("id", 1, "name", "Alice", "age", 25, "status", "active","enable",true,"addr","beijing","birthday",getDate("2020-04-09")),
                createRow("id", 2, "name", "Bob", "age", 30, "status", "active","enable",true,"addr","shanghai","birthday",getDate("1991-08-09")),
                createRow("id", 3, "name", "Charlie", "age", 20, "status", "pending","enable",false,"addr","chengdu","birthday",getDate("1988-07-12")),
                createRow("id", 4, "name", "David", "age", 35, "status", "inactive","enable",true,"addr","xian","birthday",getDate("1955-11-29")),
                createRow("id", 5, "name", "Eve", "age", 28, "status", "active","enable",true,"addr","chongqing","birthday",getDate("2003-07-12")),
                createRow("id", 6, "name", "Martin", "age", 30, "status", "active","enable",true,"addr","guangzhou","birthday",getDate("1978-06-30")),
                createRow("id", 7, "name", "Davila", "age", 39, "status", "active","enable",true,"addr",null,"birthday",getDate("1999-06-30"))
        );

        sql.registerTable("users", userColumns, userRows);

        List<JQuickColumnMeta> orderColumns = Arrays.asList(
                new JQuickColumnMeta("id", Integer.class, "orders"),
                new JQuickColumnMeta("user_id", Integer.class, "orders"),
                new JQuickColumnMeta("amount", Double.class, "orders")
        );

        List<JQuickRow> orderRows = Arrays.asList(
                createRow("id", 101, "user_id", 1, "amount", 100.0),
                createRow("id", 102, "user_id", 1, "amount", 200.0),
                createRow("id", 103, "user_id", 2, "amount", 150.0),
                createRow("id", 104, "user_id", 3, "amount", 300.0)
        );

        sql.registerTable("orders", orderColumns, orderRows);
    }

    private static JQuickRow createRow(Object... keyValues) {
        JQuickRow row = new JQuickRow();
        for (int i = 0; i < keyValues.length; i += 2) {
            row.put((String) keyValues[i], keyValues[i + 1]);
        }
        return row;
    }


    @Test
    public void testFilterQuery() {
        JQuickDataSet result = sql.execute("SELECT * FROM users WHERE status = 'active'");
        result.printTable();
        System.out.println("Active users: " + result.size());
    }
    @Test
    public void testFilterQueryWithAnd() {
        JQuickDataSet result = sql.execute("SELECT * FROM users WHERE age > 25 AND status = 'active'");
        result.printTable();
    }
    @Test
    public void testFilterQueryWithOr() {
        JQuickDataSet result = sql.execute("SELECT * FROM users WHERE status = 'pending' OR enable = true");
        result.printTable();
    }
    @Test
    public void testFilterQueryWithNested() {
        JQuickDataSet result = sql.execute("SELECT * FROM users WHERE age > 30 OR (status = 'pending' OR addr = 'chengdu')");
        result.printTable();
    }
    @Test
    public void testFilterQueryWithConstant() {
        JQuickDataSet result = sql.execute("SELECT * FROM users WHERE true");
        result.printTable();
    }
    @Test
    public void testFilterQueryWithColumn() {
        JQuickDataSet result = sql.execute("SELECT * FROM users WHERE enable");
        result.printTable();
    }
    @Test
    public void testFilterQueryWithFunctionCall() {
        JQuickDataSet result = sql.execute("SELECT * FROM users WHERE toUpper(name)='ALICE'");
        result.printTable();
    }
    @Test
    public void testFilterQueryWithIsNull() {
        JQuickDataSet result = sql.execute("SELECT * FROM users WHERE addr is null");
        result.printTable();
    }
    @Test
    public void testFilterQueryWithIsNotNull() {
        JQuickDataSet result = sql.execute("SELECT * FROM users WHERE addr is not null");
        result.printTable();
    }
    @Test
    public void testFilterQueryWithAge1() {
        JQuickDataSet result = sql.execute("SELECT * FROM users WHERE age >25");
        result.printTable();
    }
    @Test
    public void testFilterQueryWithAge2() {
        JQuickDataSet result = sql.execute("SELECT * FROM users WHERE age >=25");
        result.printTable();
    }
    @Test
    public void testFilterQueryWithAge3() {
        JQuickDataSet result = sql.execute("SELECT * FROM users WHERE age <25");
        result.printTable();
    }
    @Test
    public void testFilterQueryWithAge4() {
        JQuickDataSet result = sql.execute("SELECT * FROM users WHERE age <=20");
        result.printTable();
    }
    @Test
    public void testFilterQueryWithBetween() {
        JQuickDataSet result = sql.execute("SELECT * FROM users WHERE age  between 25 and 30");
        result.printTable();
    }
    @Test
    public void testFilterQueryWithNotBetween() {
        JQuickDataSet result = sql.execute("SELECT * FROM users WHERE age  not between 25 and 30");
        result.printTable();
    }
    @Test
    public void testFilterQueryWithIn() {
        JQuickDataSet result = sql.execute("SELECT * FROM users WHERE age  in ( 25 , 30)");
        result.printTable();
    }
    @Test
    public void testFilterQueryWithNotIn() {
        JQuickDataSet result = sql.execute("SELECT * FROM users WHERE age not in ( 25 , 30)");
        result.printTable();
    }
    @Test
    public void testFilterQueryWithLike() {
        JQuickDataSet result = sql.execute("SELECT * FROM users WHERE name like '%Davi%'");
        result.printTable();
    }
    @Test
    public void testFilterQueryWithNotLike() {
        JQuickDataSet result = sql.execute("SELECT * FROM users WHERE name not like '%Davi%'");
        result.printTable();
    }
    @Test
    public void testRegexQuery() {
        JQuickDataSet result1 = sql.execute("SELECT * FROM users WHERE name REGEXP '^A.*'");
        System.out.println("=== REGEX 1 (name REGEXP '^A') ===");
        result1.printTable();
        System.out.println("结果数量: " + result1.size());
        System.out.println();
    }
    @Test
    public void testNotRegexQuery() {
        JQuickDataSet result1 = sql.execute("SELECT * FROM users WHERE name not REGEXP '^A.*'");
        System.out.println("=== REGEX 1 (name REGEXP '^A') ===");
        result1.printTable();
        System.out.println("结果数量: " + result1.size());
        System.out.println();
    }



}