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
package com.github.paohaijiao.demo.project;

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

public class JQuickSQLProjectTest {

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
                createRow("id", 6, "name", "Martin", "age", 30, "status", "active","enable",true,"addr","guangzhou","birthday",getDate("1978-06-30"))
        );

        sql.registerTable("users", userColumns, userRows);
    }

    private static JQuickRow createRow(Object... keyValues) {
        JQuickRow row = new JQuickRow();
        for (int i = 0; i < keyValues.length; i += 2) {
            row.put((String) keyValues[i], keyValues[i + 1]);
        }
        return row;
    }



    @Test
    public void testSimpleQueryWithStar() {
        JQuickDataSet result = sql.execute("SELECT * FROM users ");
        result.printTable();
        System.out.println("Active users: " + result.size());
    }
    @Test
    public void testSimpleQuery() {
        JQuickDataSet result = sql.execute("SELECT id, name,age, status,enable,addr FROM users");
        result.printTable();
        System.out.println("Rows: " + result.size());
    }
    @Test
    public void testSimpleQueryWithFunction() {//pass
        JQuickDataSet result = sql.execute("SELECT id, toUpper(name) as upperName,age, status,enable,addr,birthday FROM users");
        result.printTable();
    }
    @Test
    public void testSimpleQueryWithConstant() {//pass
        JQuickDataSet result = sql.execute("SELECT id, name as upperName,(age+1)*3 as age, status,enable,addr,birthday FROM users");
        result.printTable();
    }
    @Test
    public void testCaseWhenSimple() {
        JQuickDataSet result = sql.execute(
                "SELECT id, name, age, " +
                        "CASE WHEN age >= 30 THEN '中年' " +
                        "     WHEN age >= 20 THEN '青年' " +
                        "     ELSE '少年' END AS age_group " +
                        "FROM users"
        );
        result.printTable();
    }
    @Test
    public void testSimpleQueryWithDistinct() {//pass
        JQuickDataSet result = sql.execute("SELECT distinct age FROM users");
        result.printTable();
    }
    @Test
    public void testSimpleQueryWithNonExpresssion() {//pass
        JQuickDataSet result = sql.execute("SELECT id, toUpper(name) as upperName,age, status,!enable,addr,birthday FROM users");
        result.printTable();
    }
    @Test
    public void testSimpleQueryWithConst() {//pass
        JQuickDataSet result = sql.execute("SELECT 0 as index,id, toUpper(name) as upperName,age, status,!enable,addr,birthday FROM users");
        result.printTable();
    }
    @Test
    public void testSubqueryInSelectScalar() {
        JQuickDataSet result = sql.execute(
                "SELECT id, name, age, " +
                        "(SELECT count(id) FROM orders WHERE orders.user_id = users.id) AS order_count " +
                        "FROM users"
        );
        result.printTable();
    }
}