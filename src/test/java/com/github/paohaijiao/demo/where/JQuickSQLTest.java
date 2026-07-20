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
                createRow("id", 6, "name", "Martin", "age", 30, "status", "active","enable",true,"addr","guangzhou","birthday",getDate("1978-06-30"))
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
    public void testSimpleQuery() {//pass
        System.out.println("=== testSimpleQuery ===");
        JQuickDataSet result = sql.execute("SELECT id, name,age, status,enable,addr,birthday FROM users");
        result.printTable();
        System.out.println("Rows: " + result.size());
    }

    @Test
    public void testFilterQuery() {
        System.out.println("=== testFilterQuery ===");
        JQuickDataSet result = sql.execute("SELECT * FROM users WHERE status = 'active'");
        result.printTable();
        System.out.println("Active users: " + result.size());
    }

    @Test
    public void testAggregationQuery() {
        System.out.println("=== testAggregationQuery ===");
        JQuickDataSet result = sql.execute("SELECT status, COUNT(*) as count FROM users GROUP BY status");
        result.printTable();
    }

    @Test
    public void testJoinQuery() {
        System.out.println("=== testJoinQuery ===");
        JQuickDataSet result = sql.execute("SELECT u.name, o.amount FROM users u JOIN orders o ON u.id = o.user_id");
        result.printTable();
    }

    @Test
    public void testSubqueryExpression() {
        System.out.println("=== testSubqueryExpression ===");
        JQuickDataSet result = sql.execute("SELECT id, name, (SELECT COUNT(*) FROM orders WHERE user_id = users.id) as order_count FROM users");
        result.printTable();
    }

    @Test
    public void testBuilderPattern() {
        System.out.println("=== testBuilderPattern ===");
        List<JQuickColumnMeta> productColumns = Arrays.asList(
                new JQuickColumnMeta("id", Integer.class, "products"),
                new JQuickColumnMeta("name", String.class, "products"),
                new JQuickColumnMeta("price", Double.class, "products")
        );
        List<JQuickRow> productRows = Arrays.asList(
                createRow("id", 1, "name", "Laptop", "price", 999.99),
                createRow("id", 2, "name", "Phone", "price", 599.99)
        );
        JQuickSQL sqlBuilder = JQuickSQL.builder()
                .embedded(2)
                .parallelism(2)
                .table("products", productColumns, productRows)
                .build();
        try {
            JQuickDataSet result = sqlBuilder.execute("SELECT * FROM products");
            result.printTable();
        } finally {
            sqlBuilder.shutdown();
        }
    }

    @Test
    public void testWithParallelism() {
        System.out.println("=== testWithParallelism ===");
        JQuickSQL sqlParallel = JQuickSQL.embedded().withParallelism(2);
        sqlParallel.registerTable("test_table", 
                Arrays.asList(new JQuickColumnMeta("id", Integer.class, "test_table")),
                Arrays.asList(createRow("id", 1),
                              createRow("id", 2),
                              createRow("id", 3)));
        
        try {
            JQuickDataSet result = sqlParallel.execute("SELECT * FROM test_table");
            result.printTable();
        } finally {
            sqlParallel.shutdown();
        }
    }

    @Test
    public void testTableOperations() {
        System.out.println("=== testTableOperations ===");
        System.out.println("Registered tables: " + sql.getRegisteredTables());
        System.out.println("Has users table: " + sql.hasTable("users"));
        JQuickDataSet users = sql.getTable("users");
        System.out.println("Users table rows: " + users.size());
    }

    @Test
    public void testOrderByQuery() {
        System.out.println("=== testOrderByQuery ===");
        JQuickDataSet result = sql.execute("SELECT name, age FROM users ORDER BY age DESC");
        result.printTable();
    }

    @Test
    public void testLimitQuery() {
        System.out.println("=== testLimitQuery ===");
        JQuickDataSet result = sql.execute("SELECT name, age FROM users ORDER BY age DESC LIMIT 3");
        result.printTable();
    }
}