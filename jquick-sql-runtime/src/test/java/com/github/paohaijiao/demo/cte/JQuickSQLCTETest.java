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
package com.github.paohaijiao.demo.cte;
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

import static org.junit.Assert.*;

public class JQuickSQLCTETest {

    private static JQuickSQL sql;

    @BeforeClass
    public static void setUpClass() {
        sql = JQuickSQL.embedded();
        registerAllTestData();
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

    private static void registerAllTestData() {
        registerUserTable();
        registerDepartmentTable();
        registerOrderTable();
    }

    private static void registerUserTable() {
        List<JQuickColumnMeta> userColumns = Arrays.asList(
                new JQuickColumnMeta("id", Integer.class, "users"),
                new JQuickColumnMeta("name", String.class, "users"),
                new JQuickColumnMeta("age", Integer.class, "users"),
                new JQuickColumnMeta("status", String.class, "users"),
                new JQuickColumnMeta("enable", Boolean.class, "users"),
                new JQuickColumnMeta("addr", String.class, "users"),
                new JQuickColumnMeta("birthday", Date.class, "users"),
                new JQuickColumnMeta("dept_id", Integer.class, "users")
        );

        List<JQuickRow> userRows = Arrays.asList(
                createRow("id", 1, "name", "Alice", "age", 25, "status", "active", "enable", true, "addr", "beijing", "birthday", getDate("2020-04-09"), "dept_id", 1),
                createRow("id", 2, "name", "Bob", "age", 30, "status", "active", "enable", true, "addr", "shanghai", "birthday", getDate("1991-08-09"), "dept_id", 2),
                createRow("id", 3, "name", "Charlie", "age", 20, "status", "pending", "enable", false, "addr", "chengdu", "birthday", getDate("1988-07-12"), "dept_id", 3),
                createRow("id", 4, "name", "David", "age", 35, "status", "inactive", "enable", true, "addr", "xian", "birthday", getDate("1955-11-29"), "dept_id", 1),
                createRow("id", 5, "name", "Eve", "age", 28, "status", "active", "enable", true, "addr", "chongqing", "birthday", getDate("2003-07-12"), "dept_id", 2),
                createRow("id", 6, "name", "Martin", "age", 30, "status", "active", "enable", true, "addr", "guangzhou", "birthday", getDate("1978-06-30"), "dept_id", 4),
                createRow("id", 7, "name", "Davila", "age", 39, "status", "active", "enable", true, "addr", null, "birthday", getDate("1999-06-30"), "dept_id", 5)
        );

        sql.registerTable("users", userColumns, userRows);
    }

    /**
     * 注册带有父子层级关系的部门数据
     * 树形结构：总公司 -> 分公司/部门 -> 子部门
     */
    private static void registerDepartmentTable() {
        List<JQuickColumnMeta> deptColumns = Arrays.asList(
                new JQuickColumnMeta("dept_id", Integer.class, "departments"),
                new JQuickColumnMeta("dept_name", String.class, "departments"),
                new JQuickColumnMeta("parent_id", Integer.class, "departments"),
                new JQuickColumnMeta("level", Integer.class, "departments")
        );

        List<JQuickRow> deptRows = Arrays.asList(
                createRow("dept_id", 1, "dept_name", "总公司", "parent_id", null, "level", 0),
                createRow("dept_id", 2, "dept_name", "技术部", "parent_id", 1, "level", 1),
                createRow("dept_id", 3, "dept_name", "市场部", "parent_id", 1, "level", 1),
                createRow("dept_id", 4, "dept_name", "财务部", "parent_id", 1, "level", 1),
                createRow("dept_id", 5, "dept_name", "研发中心", "parent_id", 2, "level", 2),
                createRow("dept_id", 6, "dept_name", "测试中心", "parent_id", 2, "level", 2),
                createRow("dept_id", 7, "dept_name", "Java开发组", "parent_id", 5, "level", 3),
                createRow("dept_id", 8, "dept_name", "前端开发组", "parent_id", 5, "level", 3),
                createRow("dept_id", 9, "dept_name", "推广部", "parent_id", 3, "level", 2),
                createRow("dept_id", 10, "dept_name", "公关部", "parent_id", 3, "level", 2)
        );

        sql.registerTable("departments", deptColumns, deptRows);
    }

    private static void registerOrderTable() {
        List<JQuickColumnMeta> orderColumns = Arrays.asList(
                new JQuickColumnMeta("order_id", Integer.class, "orders"),
                new JQuickColumnMeta("user_id", Integer.class, "orders"),
                new JQuickColumnMeta("amount", Double.class, "orders"),
                new JQuickColumnMeta("order_date", Date.class, "orders")
        );

        List<JQuickRow> orderRows = Arrays.asList(
                createRow("order_id", 1, "user_id", 1, "amount", 100.5, "order_date", getDate("2024-01-15")),
                createRow("order_id", 2, "user_id", 2, "amount", 200.0, "order_date", getDate("2024-01-16")),
                createRow("order_id", 3, "user_id", 3, "amount", 150.75, "order_date", getDate("2024-01-17")),
                createRow("order_id", 4, "user_id", 4, "amount", 300.0, "order_date", getDate("2024-01-18")),
                createRow("order_id", 5, "user_id", 5, "amount", 250.5, "order_date", getDate("2024-01-19")),
                createRow("order_id", 6, "user_id", 6, "amount", 180.0, "order_date", getDate("2024-01-20")),
                createRow("order_id", 7, "user_id", 1, "amount", 320.0, "order_date", getDate("2024-02-01")),
                createRow("order_id", 8, "user_id", 2, "amount", 450.0, "order_date", getDate("2024-02-05"))
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
    public void testSimpleNonRecursiveCTE() {
        String sqlQuery = "WITH active_users AS (" +
                "    SELECT * FROM users WHERE status = 'active'" +
                ") " +
                "SELECT * FROM active_users ORDER BY age DESC";

        JQuickDataSet result = sql.execute(sqlQuery);
        result.printTable();
    }

    @Test
    public void testMultipleNonRecursiveCTE() {
        String sqlQuery = "WITH active_users AS (" +
                "    SELECT * FROM users WHERE status = 'active'" +
                "), " +
                "young_users AS (" +
                "    SELECT * FROM active_users WHERE age < 30" +
                ") " +
                "SELECT * FROM young_users ORDER BY age";

        JQuickDataSet result = sql.execute(sqlQuery);
        result.printTable();
    }

    /**
     * 测试CTE与CASE WHEN
     * WITH age_group AS (
     *   SELECT *, CASE WHEN age < 25 THEN 'Young' WHEN age BETWEEN 25 AND 30 THEN 'Middle' ELSE 'Senior' END as age_category
     *   FROM users
     * )
     * SELECT * FROM age_group ORDER BY age_category, age
     */
    @Test
    public void testCTEWithCaseWhen() {
        String sqlQuery = "WITH age_group AS (" +
                "    SELECT " +
                "        *," +
                "        CASE " +
                "            WHEN age < 25 THEN 'Young'" +
                "            WHEN age >= 25 AND age <= 30 THEN 'Middle'" +
                "            ELSE 'Senior'" +
                "        END as age_category" +
                "    FROM users" +
                ") " +
                "SELECT * FROM age_group ORDER BY age_category, age";
        System.out.println(sqlQuery);
        JQuickDataSet result = sql.execute(sqlQuery);
        System.out.println("=== CTE与CASE WHEN：年龄分组 ===");
        result.printTable();
    }

    /**
     * 测试CTE与JOIN
     * WITH user_dept AS (
     *   SELECT u.*, d.dept_name FROM users u LEFT JOIN departments d ON u.dept_id = d.dept_id
     * )
     * SELECT * FROM user_dept WHERE status = 'active' ORDER BY dept_name, age
     */
    @Test
    public void testCTEWithJoin() {
        String sqlQuery = "WITH user_dept AS (" +
                "    SELECT u.*, d.dept_name " +
                "    FROM users u " +
                "    LEFT JOIN departments d ON u.dept_id = d.dept_id" +
                ") " +
                "SELECT * FROM user_dept " +
                "WHERE status = 'active' " +
                "ORDER BY dept_name, age";

        JQuickDataSet result = sql.execute(sqlQuery);
        System.out.println("=== CTE与JOIN：用户及其部门信息 ===");
        result.printTable();
    }

    /**
     * 测试CTE与IN子查询
     * WITH high_value_orders AS (
     *   SELECT user_id, SUM(amount) as total FROM orders GROUP BY user_id HAVING SUM(amount) > 300
     * )
     * SELECT * FROM users WHERE id IN (SELECT user_id FROM high_value_orders)
     */
    @Test
    public void testCTEWithInSubquery() {
        String sqlQuery = "WITH high_value_orders AS (" +
                "    SELECT user_id, SUM(amount) as total " +
                "    FROM orders " +
                "    GROUP BY user_id " +
                "    HAVING SUM(amount) > 300" +
                ") " +
                "SELECT u.* FROM users u " +
                "WHERE u.id IN (SELECT user_id FROM high_value_orders) " +
                "ORDER BY u.id";

        JQuickDataSet result = sql.execute(sqlQuery);
        System.out.println("=== CTE与IN子查询：高价值订单用户 ===");
        result.printTable();
    }

    /**
     * 测试CTE与EXISTS
     * WITH active_users AS (SELECT * FROM users WHERE status = 'active')
     * SELECT * FROM departments d WHERE EXISTS (SELECT 1 FROM active_users u WHERE u.dept_id = d.dept_id)
     */
    @Test
    public void testCTEWithExists() {
        String sqlQuery = "WITH active_users AS (" +
                "    SELECT * FROM users WHERE status = 'active'" +
                ") " +
                "SELECT d.* FROM departments d " +
                "WHERE EXISTS (SELECT 1 FROM active_users u WHERE u.dept_id = d.dept_id) " +
                "ORDER BY d.dept_id";

        JQuickDataSet result = sql.execute(sqlQuery);
        System.out.println("=== CTE与EXISTS：有活跃用户的部门 ===");
        result.printTable();
    }

    /**
     * 测试CTE与ORDER BY + LIMIT
     * WITH sorted_users AS (
     *   SELECT * FROM users ORDER BY age DESC LIMIT 3
     * )
     * SELECT * FROM sorted_users
     */
    @Test
    public void testCTEWithOrderByAndLimit() {
        String sqlQuery = "WITH top_oldest AS (" +
                "    SELECT * FROM users ORDER BY age DESC LIMIT 3" +
                ") " +
                "SELECT * FROM top_oldest";

        JQuickDataSet result = sql.execute(sqlQuery);
        System.out.println("=== CTE与ORDER BY + LIMIT：年龄最大的3位用户 ===");
        result.printTable();
    }

    /**
     * 测试CTE与UNION
     * WITH active_users AS (SELECT * FROM users WHERE status = 'active')
     * SELECT name, age, 'Active' as type FROM active_users
     * UNION
     * SELECT name, age, 'Inactive' as type FROM users WHERE status != 'active'
     */
    @Test
    public void testCTEWithUnion() {
        String sqlQuery = "WITH active_users AS (" +
                "    SELECT * FROM users WHERE status = 'active'" +
                ") " +
                "SELECT name, age, 'Active' as type FROM active_users " +
                "UNION " +
                "SELECT name, age, 'Inactive' as type FROM users WHERE status != 'active' " +
                "ORDER BY name";

        JQuickDataSet result = sql.execute(sqlQuery);
        System.out.println("=== CTE与UNION：用户状态分类 ===");
        result.printTable();
    }

    /**
     * 测试递归CTE - 查询部门树
     * WITH RECURSIVE dept_tree AS (
     *   SELECT dept_id, dept_name, parent_id, level, CAST(dept_name AS VARCHAR(100)) AS path
     *   FROM departments WHERE parent_id IS NULL
     *   UNION ALL
     *   SELECT d.dept_id, d.dept_name, d.parent_id, d.level, CAST(CONCAT(dt.path, ' -> ', d.dept_name) AS VARCHAR(100))
     *   FROM departments d INNER JOIN dept_tree dt ON d.parent_id = dt.dept_id
     * )
     * SELECT * FROM dept_tree ORDER BY path
     */
    @Test
    public void testRecursiveCTEDeptTree() {
        String sqlQuery = "WITH RECURSIVE dept_tree AS (" +
                "    SELECT dept_id, dept_name, parent_id, level, " +
                "           dept_name AS path " +
                "    FROM departments WHERE parent_id IS NULL " +
                "    UNION ALL " +
                "    SELECT d.dept_id, d.dept_name, d.parent_id, d.level, " +
                "           CONCAT(dt.path, ' -> ', d.dept_name) AS path " +
                "    FROM departments d " +
                "    INNER JOIN dept_tree dt ON d.parent_id = dt.dept_id " +
                ") SELECT * FROM dept_tree ORDER BY path";

        JQuickDataSet result = sql.execute(sqlQuery);
        System.out.println("=== 递归CTE：部门树形结构 ===");
        result.printTable();
    }

    /**
     * 测试递归CTE - 查询子部门
     * WITH RECURSIVE dept_children AS (
     *   SELECT * FROM departments WHERE dept_id = 2
     *   UNION ALL
     *   SELECT d.* FROM departments d INNER JOIN dept_children dc ON d.parent_id = dc.dept_id
     * )
     * SELECT * FROM dept_children ORDER BY level, dept_id
     */
    @Test
    public void testRecursiveCTEFindChildren() {
        String sqlQuery = "WITH RECURSIVE dept_children AS (" +
                "    SELECT dept_id, dept_name, parent_id, level " +
                "    FROM departments WHERE dept_id = 2 " +
                "    UNION ALL " +
                "    SELECT d.dept_id, d.dept_name, d.parent_id, d.level " +
                "    FROM departments d " +
                "    INNER JOIN dept_children dc ON d.parent_id = dc.dept_id " +
                ") SELECT * FROM dept_children ORDER BY level, dept_id";

        JQuickDataSet result = sql.execute(sqlQuery);
        System.out.println("=== 递归CTE：技术部及其所有子部门 ===");
        result.printTable();
    }

    /**
     * 测试递归CTE - 查询父路径
     * WITH RECURSIVE dept_path AS (
     *   SELECT * FROM departments WHERE dept_id = 7
     *   UNION ALL
     *   SELECT d.* FROM departments d INNER JOIN dept_path dp ON d.dept_id = dp.parent_id
     * )
     * SELECT * FROM dept_path ORDER BY level
     */
    @Test
    public void testRecursiveCTEFindParentPath() {
        String sqlQuery = "WITH RECURSIVE dept_path AS (" +
                "    SELECT dept_id, dept_name, parent_id, level " +
                "    FROM departments WHERE dept_id = 7 " +
                "    UNION ALL " +
                "    SELECT d.dept_id, d.dept_name, d.parent_id, d.level " +
                "    FROM departments d " +
                "    INNER JOIN dept_path dp ON d.dept_id = dp.parent_id " +
                ") SELECT * FROM dept_path ORDER BY level";

        JQuickDataSet result = sql.execute(sqlQuery);
        System.out.println("=== 递归CTE：Java开发组的完整父路径 ===");
        result.printTable();
    }

    /**
     * 测试CTE与子查询表达式
     * WITH dept_avg_age AS (
     *   SELECT dept_id, AVG(age) as avg_age FROM users GROUP BY dept_id
     * )
     * SELECT u.*, d.dept_name, da.avg_age FROM users u
     * JOIN dept_avg_age da ON u.dept_id = da.dept_id
     * JOIN departments d ON u.dept_id = d.dept_id
     * WHERE u.age > (SELECT avg_age FROM dept_avg_age WHERE dept_id = u.dept_id)
     */
    @Test
    public void testCTEWithSubqueryExpression() {
        String sqlQuery = "WITH dept_avg_age AS (" +
                "    SELECT dept_id, AVG(age) as avg_age " +
                "    FROM users " +
                "    GROUP BY dept_id" +
                ") " +
                "SELECT u.id, u.name, u.age, d.dept_name, da.avg_age " +
                "FROM users u " +
                "JOIN dept_avg_age da ON u.dept_id = da.dept_id " +
                "JOIN departments d ON u.dept_id = d.dept_id " +
                "WHERE u.age > da.avg_age " +
                "ORDER BY u.dept_id, u.age DESC";

        JQuickDataSet result = sql.execute(sqlQuery);
        System.out.println("=== CTE与子查询表达式：高于部门平均年龄的用户 ===");
        result.printTable();
    }

    /**
     * 测试CTE中的日期字面量
     * WITH recent_orders AS (
     *   SELECT * FROM orders WHERE order_date > '2024-01-18'::'yyyy-MM-dd'
     * )
     * SELECT * FROM recent_orders ORDER BY order_date
     */
    @Test
    public void testCTEWithDateLiteral() {
        String sqlQuery = "WITH recent_orders AS (" +
                "    SELECT * FROM orders " +
                "    WHERE order_date > '2024-01-18'::'yyyy-MM-dd'" +
                ") " +
                "SELECT * FROM recent_orders ORDER BY order_date";

        JQuickDataSet result = sql.execute(sqlQuery);
        System.out.println("=== CTE与日期字面量：2024-01-18之后的订单 ===");
        result.printTable();
    }

    /**
     * 测试CTE与BETWEEN
     * WITH active_users AS (SELECT * FROM users WHERE status = 'active')
     * SELECT * FROM active_users WHERE age BETWEEN 25 AND 35 ORDER BY age
     */
    @Test
    public void testCTEWithBetween() {
        String sqlQuery = "WITH active_users AS (" +
                "    SELECT * FROM users WHERE status = 'active'" +
                ") " +
                "SELECT * FROM active_users " +
                "WHERE age BETWEEN 25 AND 35 " +
                "ORDER BY age";

        JQuickDataSet result = sql.execute(sqlQuery);
        System.out.println("=== CTE与BETWEEN：年龄在25-35之间的活跃用户 ===");
        result.printTable();

        assertNotNull(result);

        for (JQuickRow row : result.getRows()) {
            assertEquals("active", row.get("status"));
            int age = (int) row.get("age");
            assertTrue(age >= 25 && age <= 35);
        }
    }

    /**
     * 测试CTE与聚合 + HAVING
     * WITH dept_stats AS (
     *   SELECT u.dept_id, d.dept_name, COUNT(*) as user_count, AVG(u.age) as avg_age
     *   FROM users u JOIN departments d ON u.dept_id = d.dept_id
     *   GROUP BY u.dept_id, d.dept_name
     *   HAVING COUNT(*) >= 2
     * )
     * SELECT * FROM dept_stats ORDER BY user_count DESC
     */
    @Test
    public void testCTEWithHaving() {
        String sqlQuery = "WITH dept_stats AS (" +
                "    SELECT u.dept_id, d.dept_name, COUNT(*) as user_count, AVG(u.age) as avg_age " +
                "    FROM users u " +
                "    JOIN departments d ON u.dept_id = d.dept_id " +
                "    GROUP BY u.dept_id, d.dept_name " +
                "    HAVING COUNT(*) >= 2" +
                ") " +
                "SELECT * FROM dept_stats ORDER BY user_count DESC";

        JQuickDataSet result = sql.execute(sqlQuery);
        System.out.println("=== CTE与HAVING：用户数>=2的部门统计 ===");
        result.printTable();

        assertNotNull(result);

        for (JQuickRow row : result.getRows()) {
            int userCount = (int) row.get("user_count");
            assertTrue(userCount >= 2);
        }
    }
}