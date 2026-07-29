package com.github.paohaijiao.demo.subquery;
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

public class JQuickSQLSubqueryTest {

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

    private static Date getDate(String date) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            return formatter.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }

    private static void registerTestData() {
        // Users table
        List<JQuickColumnMeta> userColumns = Arrays.asList(
                new JQuickColumnMeta("id", Integer.class, "users"),
                new JQuickColumnMeta("name", String.class, "users"),
                new JQuickColumnMeta("age", Integer.class, "users"),
                new JQuickColumnMeta("status", String.class, "users"),
                new JQuickColumnMeta("enable", String.class, "users"),
                new JQuickColumnMeta("addr", String.class, "users"),
                new JQuickColumnMeta("birthday", Date.class, "users"),
                new JQuickColumnMeta("department_id", Integer.class, "users")
        );
        List<JQuickRow> userRows = Arrays.asList(
                createRow("id", 1, "name", "Alice", "age", 25, "status", "active", "enable", true, "addr", "beijing", "birthday", getDate("2020-04-09"), "department_id", 1),
                createRow("id", 2, "name", "Bob", "age", 30, "status", "active", "enable", true, "addr", "shanghai", "birthday", getDate("1991-08-09"), "department_id", 2),
                createRow("id", 3, "name", "Charlie", "age", 20, "status", "pending", "enable", false, "addr", "chengdu", "birthday", getDate("1988-07-12"), "department_id", 1),
                createRow("id", 4, "name", "David", "age", 35, "status", "inactive", "enable", true, "addr", "xian", "birthday", getDate("1955-11-29"), "department_id", 3),
                createRow("id", 5, "name", "Eve", "age", 28, "status", "active", "enable", true, "addr", "chongqing", "birthday", getDate("2003-07-12"), "department_id", 2),
                createRow("id", 6, "name", "Martin", "age", 30, "status", "active", "enable", true, "addr", "guangzhou", "birthday", getDate("1978-06-30"), "department_id", 3),
                createRow("id", 7, "name", "Davila", "age", 39, "status", "active", "enable", true, "addr", null, "birthday", getDate("1999-06-30"), "department_id", 1)
        );
        sql.registerTable("users", userColumns, userRows);
        // Departments table for subquery testing
        List<JQuickColumnMeta> deptColumns = Arrays.asList(
                new JQuickColumnMeta("dept_id", Integer.class, "departments"),
                new JQuickColumnMeta("dept_name", String.class, "departments"),
                new JQuickColumnMeta("location", String.class, "departments"),
                new JQuickColumnMeta("budget", Double.class, "departments")
        );
        List<JQuickRow> deptRows = Arrays.asList(
                createRow("dept_id", 1, "dept_name", "Engineering", "location", "Building A", "budget", 500000.0),
                createRow("dept_id", 2, "dept_name", "Marketing", "location", "Building B", "budget", 300000.0),
                createRow("dept_id", 3, "dept_name", "Finance", "location", "Building C", "budget", 400000.0),
                createRow("dept_id", 4, "dept_name", "HR", "location", "Building D", "budget", 200000.0)
        );
        sql.registerTable("departments", deptColumns, deptRows);
        // Orders table for subquery with aggregate functions
        List<JQuickColumnMeta> orderColumns = Arrays.asList(
                new JQuickColumnMeta("order_id", Integer.class, "orders"),
                new JQuickColumnMeta("user_id", Integer.class, "orders"),
                new JQuickColumnMeta("amount", Double.class, "orders"),
                new JQuickColumnMeta("order_date", Date.class, "orders")
        );

        List<JQuickRow> orderRows = Arrays.asList(
                createRow("order_id", 101, "user_id", 1, "amount", 150.50, "order_date", getDate("2024-01-15")),
                createRow("order_id", 102, "user_id", 2, "amount", 200.00, "order_date", getDate("2024-01-16")),
                createRow("order_id", 103, "user_id", 1, "amount", 75.25, "order_date", getDate("2024-01-17")),
                createRow("order_id", 104, "user_id", 3, "amount", 300.00, "order_date", getDate("2024-01-18")),
                createRow("order_id", 105, "user_id", 5, "amount", 120.00, "order_date", getDate("2024-01-19")),
                createRow("order_id", 106, "user_id", 2, "amount", 450.50, "order_date", getDate("2024-01-20"))
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
    public void testAvg() {
        JQuickDataSet result = sql.execute(
                "SELECT AVG(age) FROM users"
        );
        result.printTable();
    }
    /**
     * 测试子查询在WHERE子句中使用 (SUBQUERY IN WHERE CLAUSE)
     * 查询年龄大于平均年龄的用户
     */
    @Test
    public void testSubqueryInWhereWithAggregate() {
        System.out.println("=== Subquery in WHERE clause (age > avg age) ===");
        JQuickDataSet result = sql.execute(
                "SELECT * FROM users " + "WHERE age > (SELECT AVG(age) FROM users)"
        );
        result.printTable();
    }

    /**
     * 测试子查询在WHERE子句中使用IN操作符
     * 查询在Engineering或Marketing部门的用户
     */
    @Test
    public void testSubqueryInWhereWithIn() {
        JQuickDataSet result = sql.execute(
                "SELECT * FROM users " +
                        "WHERE department_id IN (SELECT dept_id FROM departments WHERE dept_name IN ('Engineering', 'Marketing'))"
        );
        result.printTable();
    }

    /**
     * 测试子查询在WHERE子句中使用EXISTS
     * 查询有订单的用户
     */
    @Test
    public void testSubqueryInWhereWithExists() {
        System.out.println("=== Subquery with EXISTS ===");
        JQuickDataSet result = sql.execute(
                "SELECT * FROM users u " +
                        "WHERE EXISTS (SELECT 1 FROM orders o WHERE o.user_id = u.id)"
        );
        result.printTable();
    }

    /**
     * 测试子查询在FROM子句中使用 (派生表)
     * 计算每个部门的平均年龄
     */
    @Test
    public void testSubqueryInFromClause() {
        System.out.println("=== Subquery in FROM clause (derived table) ===");
        JQuickDataSet result = sql.execute(
                "SELECT dept_id, avg_age " +
                        "FROM (SELECT department_id as dept_id, AVG(age) as avg_age FROM users GROUP BY department_id) " +
                        "WHERE avg_age > 28"
        );
        result.printTable();
    }

    /**
     * 测试子查询在SELECT子句中使用 (标量子查询)
     * 查询每个用户及其部门的预算
     */
    @Test
    public void testSubqueryInSelectClause() {
        System.out.println("=== Subquery in SELECT clause (scalar subquery) ===");
        JQuickDataSet result = sql.execute(
                "SELECT u.name, u.age, " +
                        "(SELECT dept_name FROM departments d WHERE d.dept_id = u.department_id) as dept_name, " +
                        "(SELECT budget FROM departments d WHERE d.dept_id = u.department_id) as dept_budget " +
                        "FROM users u"
        );
        result.printTable();
    }

    /**
     * 测试子查询在HAVING子句中使用
     * 查询订单总额大于平均订单总额的用户
     */
    @Test
    public void testSubqueryInHavingClause() {
        JQuickDataSet result = sql.execute(
                "SELECT user_id, SUM(amount) as total_amount " +
                        "FROM orders " +
                        "GROUP BY user_id " +
                        "HAVING SUM(amount) > (SELECT AVG(amount) FROM orders)"
        );
        result.printTable();
    }

    /**
     * 测试子查询在ORDER BY子句中使用
     * 按部门预算排序用户
     */
    @Test
    public void testSubqueryInOrderByClause() {
        JQuickDataSet result = sql.execute(
                "SELECT u.name, u.age, u.department_id " +
                        "FROM users u " +
                        "ORDER BY (SELECT budget FROM departments d WHERE d.dept_id = u.department_id) DESC, u.age"
        );
        result.printTable();
    }

    /**
     * 测试嵌套子查询 (Nested Subquery)
     * 查询订单金额高于平均订单金额的用户信息
     */
    @Test
    public void testNestedSubquery() {
        JQuickDataSet result = sql.execute(
                "SELECT * FROM users " +
                        "WHERE id IN (" +
                        "    SELECT user_id FROM orders " +
                        "    WHERE amount > (SELECT AVG(amount) FROM orders)" +
                        ")"
        );
        result.printTable();
    }

    /**
     * 测试关联子查询 (Correlated Subquery)
     * 查询每个部门中年龄最大的用户
     */
    @Test
    public void testCorrelatedSubquery() {
        JQuickDataSet result = sql.execute(
                "SELECT u1.* FROM users u1 " +
                        "WHERE u1.age = (" +
                        "    SELECT MAX(u2.age) FROM users u2 " +
                        "    WHERE u2.department_id = u1.department_id" +
                        ")"
        );
        result.printTable();
    }

    /**
     * 测试子查询与CASE WHEN结合
     * 根据用户订单数量进行分类
     */
    @Test
    public void testSubqueryWithCaseWhen() {
        JQuickDataSet result = sql.execute(
                "SELECT u.name, u.age, " +
                        "CASE " +
                        "    WHEN (SELECT COUNT(*) FROM orders o WHERE o.user_id = u.id) > 2 THEN 'High Volume' " +
                        "    WHEN (SELECT COUNT(*) FROM orders o WHERE o.user_id = u.id) > 0 THEN 'Normal' " +
                        "    ELSE 'No Orders' " +
                        "END as order_volume " +
                        "FROM users u"
        );
        result.printTable();
    }

    /**
     * 测试多列子查询 (Multiple Column Subquery)
     */
    @Test
    public void testMultipleColumnSubquery() {
        JQuickDataSet result = sql.execute(
                "SELECT * FROM users " +
                        "WHERE (department_id, age) IN (" +
                        "    SELECT department_id, MAX(age) FROM users GROUP BY department_id" +
                        ")"
        );
        result.printTable();
    }

    /**
     * 测试子查询中使用聚合函数和分组
     * 查询订单总金额超过部门预算的用户
     */
    @Test
    public void testSubqueryWithAggregateAndGroupBy() {
        JQuickDataSet result = sql.execute(
                "SELECT u.name, u.department_id, " +
                        "(SELECT SUM(o.amount) FROM orders o WHERE o.user_id = u.id) as user_total_orders " +
                        "FROM users u " +
                        "WHERE (SELECT SUM(o.amount) FROM orders o WHERE o.user_id = u.id) > " +
                        "(SELECT AVG(budget) FROM departments)"
        );
        result.printTable();
    }

    /**
     * 测试子查询在JOIN中使用
     */
    @Test
    public void testSubqueryInJoin() {
        System.out.println("=== Subquery in JOIN ===");
        JQuickDataSet result = sql.execute(
                "SELECT u.name, u.age, dept_stats.dept_name, dept_stats.avg_age " +
                        "FROM users u " +
                        "JOIN (" +
                        "    SELECT d.dept_id, d.dept_name, AVG(u2.age) as avg_age " +
                        "    FROM departments d " +
                        "    LEFT JOIN users u2 ON d.dept_id = u2.department_id " +
                        "    GROUP BY d.dept_id, d.dept_name" +
                        ") dept_stats ON u.department_id = dept_stats.dept_id"
        );
        result.printTable();
    }
}
