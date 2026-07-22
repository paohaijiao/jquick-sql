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
package com.github.paohaijiao.demo.groupby;

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

public class JQuickSQLGroupByTest {

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
    public void testGroupBy() {
        JQuickDataSet result1 = sql.execute(
                "SELECT status, COUNT(*) as count, AVG(age) as avg_age " +
                        "FROM users " +
                        "GROUP BY status " +
                        "ORDER BY status"
        );
        result1.printTable();
    }
    @Test
    public void testGroupByWithHavingAndOrderBy() {
        JQuickDataSet result1 = sql.execute(
                "SELECT status, COUNT(age) as count, AVG(age) as avg_age " +
                        "FROM users " +
                        "GROUP BY status " +
                        "HAVING COUNT(age) >=1 " +
                        "ORDER BY count DESC"
        );
        result1.printTable();
    }




}