# JQuick-SQL · Embedded SQL Query Engine

> 🏠 Org: [paohaijiao](https://github.com/paohaijiao) · 🧩 Ecosystem: [【JQuick Ecosystem】](#iii-jquick-ecosystem-navigation) · 📜 License: **Apache-2.0** ✅ Free for Commercial Use
>
> Chinese name (中文名)：JQuick-SQL 嵌入式 SQL 查询引擎 · JDK 8+ · Maven: `io.github.paohaijiao:jquick-sql:4.1.0`

JQuick-SQL is a **pure-Java, embeddable, zero-server** lightweight SQL query engine. It ships a hand-written ANTLR4 parser supporting `SELECT / WHERE / JOIN / GROUP BY / HAVING / UNION / MINUS / INTERSECT / subqueries / CASE WHEN`; together with ecosystem libraries like **jquick-curl** and **jquick-excel**, you can run `JOIN` and aggregation across heterogeneous in-memory datasets **inside the same JVM** — **no data movement, no middleware, one SQL statement is all you need**.

<div align="center">

[![GitHub Stars](https://img.shields.io/github/stars/paohaijiao/jquick-sql?style=flat-square&logo=github)](https://github.com/paohaijiao/jquick-sql/stargazers)
[![GitHub Forks](https://img.shields.io/github/forks/paohaijiao/jquick-sql?style=flat-square&logo=github)](https://github.com/paohaijiao/jquick-sql/forks)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.paohaijiao/jquick-sql?style=flat-square)](https://search.maven.org/artifact/io.github.paohaijiao/jquick-sql)
[![Java](https://img.shields.io/badge/Java-8%2B-ED8B00?style=flat-square&logo=java)](https://adoptium.net/)
[![ANTLR](https://img.shields.io/badge/ANTLR4-4.x-success?style=flat-square&logo=antlr)](https://www.antlr.org/)
[![License](https://img.shields.io/badge/License-Apache--2.0-yellowgreen?style=flat-square)](LICENSE)
[![Last Commit](https://img.shields.io/github/last-commit/paohaijiao/jquick-sql?style=flat-square)](https://github.com/paohaijiao/jquick-sql/commits)

</div>

---

## I. Project Overview

### 1.1 Pain Points → JQuick-SQL Solutions

| Business Pain | How JQuick-SQL Solves It |
|--------------|-------------------------|
| Reports pull data from MySQL / Oracle / Excel at once — devs hand-write N DAOs and stitch results in Java | One SQL directly `JOIN`s all registered in-memory tables; the engine handles predicates & merged aggregates. |
| Legacy Tomcat 7 + iBatis + JDK 8 projects are expensive to modernize | Keep iBatis-style XML dynamic proxy; min JDK 8; drop a single JAR into your existing WAR — no service to deploy. |
| Batch stats run slow on a single thread and CPU sits idle | `embedded(n)` spins up **n parallel Workers inside the same JVM** and splits work via Fragments. |
| Lakehouses / data platforms are too heavy for quick BI deliverables | Embedded inside the JVM: **read → compute → write** in one JAR. |
| Weak SQL dialects can't express CASEs, nested queries and complex filters | Parser covers SELECT clause / WHERE / JOIN / GROUP BY / HAVING / ORDER BY / LIMIT / UNION / MINUS / INTERSECT / subqueries / CASE WHEN. |

### 1.2 Architecture

```
┌──────────────────────────────────────────────────────────────────┐
│                JQuick-SQL Engine (Embedded, inside JVM)          │
├──────────────────────────────────────────────────────────────────┤
│  SQL Input → Parser (ANTLR4 AST) → Logical Plan → Optimizer     │
│                                           ↓                       │
│                                   Physical Plan                  │
│                              (HashJoin / Sort / TopN / Agg)      │
│                                           ↓                       │
│                       Fragmenter → Parallel Worker(n) execution  │
│                           (Same JVM, count = embedded(n) param)   │
│                                           ↓                       │
│                           JQuickDataSet (print / export / query) │
└──────────────────────────────────────────────────────────────────┘
```

---

## II. Quick Start (3 minutes)

### 2.1 Maven

```xml
<dependency>
    <groupId>io.github.paohaijiao</groupId>
    <artifactId>jquick-sql</artifactId>
    <version>4.1.0</version>
</dependency>
```

### 2.2 Gradle

```groovy
implementation 'io.github.paohaijiao:jquick-sql:4.1.0'
```

### 2.3 Hello World (fully runnable, includes `main`)

```java
package demo;

import com.github.paohaijiao.engine.JQuickSQL;
import com.github.paohaijiao.statement.JQuickColumnMeta;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;

import java.util.Arrays;
import java.util.List;

/**
 * Function: Boots JQuick-SQL in EMBEDDED mode, registers an in-memory table and runs SELECT.
 * Scenario: Unit tests, offline report computation, ETL prototyping, utility scripts.
 * Notes: embedded() occupies ports 19001+ for parallel Workers inside the same JVM;
 *        always call shutdown() in finally to release resources.
 */
public class QuickStartDemo {

    public static void main(String[] args) {
        // 1) Create embedded engine; embedded(4) sets parallel Worker count in the same JVM
        JQuickSQL sql = JQuickSQL.embedded();
        try {
            // 2) Column metadata: column name + Java type + table alias (must match in JOINs)
            List<JQuickColumnMeta> columns = Arrays.asList(
                    new JQuickColumnMeta("id",   Integer.class, "users"),
                    new JQuickColumnMeta("name", String.class,  "users"),
                    new JQuickColumnMeta("age",  Integer.class, "users")
            );

            // 3) Rows: alternating (key, value) pairs
            List<JQuickRow> rows = Arrays.asList(
                    row("id", 1, "name", "Alice",   "age", 25),
                    row("id", 2, "name", "Bob",     "age", 30),
                    row("id", 3, "name", "Charlie", "age", 20)
            );

            // 4) Register table
            sql.registerTable("users", columns, rows);

            // 5) Execute SQL and pretty-print
            JQuickDataSet result = sql.execute(
                    "SELECT id, name, age FROM users WHERE age >= 25 ORDER BY age DESC"
            );
            result.printTable();

            System.out.println("Total rows: " + result.size());

        } finally {
            // 6) Shutdown engine, release ports
            sql.shutdown();
        }
    }

    private static JQuickRow row(Object... kv) {
        JQuickRow r = new JQuickRow();
        for (int i = 0; i < kv.length; i += 2) r.put((String) kv[i], kv[i + 1]);
        return r;
    }
}
```

---

## III. JQuick Ecosystem Navigation

> Click any row to jump to a sibling project. All projects share `io.github.paohaijiao` groupId.
> License baseline: Apache-2.0 (✅ free commercial use), **except jquick-pdf which is AGPL-3.0 (⚠️ commercial license required)**.

| # | Project | Repository | Description | License |
|---|---------|-----------|-------------|---------|
| 1 | **jquick-sql** ⭐ | [paohaijiao/jquick-sql](https://github.com/paohaijiao/jquick-sql) | Embedded SQL Query Engine (this repo) | Apache-2.0 |
| 2 | jquick-gateway | [paohaijiao/jquick-gateway](https://github.com/paohaijiao/jquick-gateway) | Lightweight Netty API Gateway (routing / rate-limit / circuit-breaker / canary) | Apache-2.0 |
| 3 | jquick-excel | [paohaijiao/jquick-excel](https://github.com/paohaijiao/jquick-excel) | Excel read/write; SAX streaming for huge files; legacy POI 3.x compatible | Apache-2.0 |
| 4 | jquick-pdf | [paohaijiao/jquick-pdf](https://github.com/paohaijiao/jquick-pdf) | PDF tooling based on iText7 (template render / sign / watermark / merge-split) | **AGPL-3.0 ⚠️** |
| 5 | jquick-asm | [paohaijiao/jquick-asm](https://github.com/paohaijiao/jquick-asm) | ASM 9.x bytecode toolkit (AOP proxy / dynamic Bean / class transformer) | Apache-2.0 |
| 6 | jquick-curl | [paohaijiao/jquick-curl](https://github.com/paohaijiao/jquick-curl) | HTTP client (fluent API / pool / resume-download / retry) | Apache-2.0 |
| 7 | jquick-java | [paohaijiao/jquick-java](https://github.com/paohaijiao/jquick-java) | ANTLR4 script engine + XML dynamic proxy (hot-reload rule engine) | Apache-2.0 |

> 📌 **Cross-project combinations**: jquick-sql + jquick-curl → register REST JSON rows as in-memory tables, then JOIN them; jquick-sql + jquick-excel → read Excel rows then JOIN against your RDBMS data. See each project's README for more.

---

## IV. SQL Syntax Supported

> Every item below is backed by a real unit-test suite under `src/test/java/com/github/paohaijiao/demo/`. Open the `*Test.java` files in the listed directories for more samples.

### 4.1 SELECT Clause (Projection / Project)

Supported forms:
- `SELECT *` — project all columns;
- Column list: `SELECT id, name, age`;
- Column alias: `SELECT name AS username`;
- Constants & arithmetic: `SELECT 1, 2+3, salary*12 annual`;
- Function expressions: `SELECT toUpper(name) uname, ROUND(AVG(salary),2)`;
- `CASE WHEN ... THEN ... ELSE ... END`;
- `DISTINCT` deduplication: `SELECT DISTINCT dept`;
- Scalar subqueries in SELECT list: `SELECT name, (SELECT COUNT(*) FROM orders o WHERE o.user_id=u.id) cnt FROM users u`.

Example:
```sql
SELECT DISTINCT dept,
       CASE WHEN salary>=30000 THEN 'HIGH'
            WHEN salary>=20000 THEN 'MID'
            ELSE 'LOW' END grade,
       salary * 12 annual
FROM emp;
```
Ref test: `demo/project/JQuickSQLProjectTest.java`.

### 4.2 WHERE Filters

Supported forms:
- Comparisons: `=`, `<>`, `>`, `>=`, `<`, `<=`;
- Logical: `AND` / `OR` / `NOT` + nested parentheses;
- Boolean constants: `WHERE true`, `WHERE 1=1`;
- Boolean column: `WHERE enable` (column type MUST be `Boolean.class`);
- Function results as predicates: `WHERE toUpper(name)='ALICE'`;
- Null checks: `IS NULL` / `IS NOT NULL`;
- Range: `BETWEEN a AND b` / `NOT BETWEEN a AND b`;
- Set membership: `IN (v1,v2,...)` / `NOT IN (...)`;
- Pattern: `LIKE '%x%'` / `NOT LIKE`;
- Regex: `REGEXP '^A.*'` / `NOT REGEXP`;
- Existence: `EXISTS (subquery)`.

Example:
```sql
SELECT id, name FROM users
 WHERE age > 25 AND status = 'active'
   AND addr IN ('beijing','shanghai')
   AND name LIKE '%Davi%'
   AND name REGEXP '^A.*'
   AND addr IS NOT NULL;
```
Ref test: `demo/where/JQuickSQLWhereTest.java`.

### 4.3 JOINs (Multi-Table)

Supported forms (5 types):
- `INNER JOIN ... ON ...`;
- `LEFT [OUTER] JOIN ... ON ...`;
- `RIGHT [OUTER] JOIN ... ON ...`;
- `CROSS JOIN` (Cartesian);
- `NATURAL JOIN` (same-named columns).

Note: `FULL OUTER JOIN` is **not supported** yet (a `try/catch` in the join demo explicitly asserts the "not-supported" exception).

Example:
```sql
SELECT u.name, o.amount
  FROM users u LEFT JOIN orders o ON u.id = o.user_id;
```
Ref test: `demo/joinClause/JQuickSQLJoinTest.java`.

### 4.4 GROUP BY + HAVING + Aggregate Functions

Supported forms:
- `GROUP BY col1, col2, ...` (single or multi-column);
- `HAVING agg_expr condition` — filter after aggregation;
- Aggregates: `COUNT(*)` / `COUNT(col)` / `AVG(col)` / `SUM(col)` / `MAX(col)` / `MIN(col)` / `ROUND(expr, scale)`.

Example:
```sql
SELECT dept, COUNT(*) c, AVG(salary) avg_s
  FROM emp
 GROUP BY dept
HAVING COUNT(*) >= 2
 ORDER BY c DESC;
```
Ref tests: `demo/groupby/JQuickSQLGroupByTest.java`, `demo/aggregation/JQuickSQLAggregateTest.java`.

### 4.5 ORDER BY

Supported forms:
- `ORDER BY col ASC` / `DESC` — single column;
- `ORDER BY col1, col2 DESC` — multi-column;
- `ORDER BY CASE WHEN ... THEN ... END` — expression ordering;
- Boolean column ordering: `ORDER BY enable DESC`.

Example:
```sql
SELECT name, salary, dept
  FROM emp
 ORDER BY dept ASC, salary DESC;
```
Ref test: `demo/orderBy/JQuickSQLOrderByTest.java`.

### 4.6 LIMIT (Pagination)

Two MySQL-style forms supported:
- `LIMIT n` — take top n rows;
- `LIMIT offset, n` — skip `offset`, take `n`.

Example:
```sql
SELECT name FROM users ORDER BY age DESC LIMIT 2, 3;
```
Ref test: `demo/limitClause/JQuickSQLLimitTest.java`.

### 4.7 Set Operations: UNION / MINUS / INTERSECT

Supported forms:
- `SELECT ... UNION     SELECT ...` — union with dedup;
- `SELECT ... MINUS     SELECT ...` — set difference (A only);
- `SELECT ... INTERSECT SELECT ...` — set intersection.

Example:
```sql
-- Intersection: users that appear on BOTH lists
SELECT name FROM list_a  INTERSECT  SELECT name FROM list_b;
```
Ref test: `demo/union/JQuickSQLUnionTest.java`.

### 4.8 Subqueries

Subqueries are supported in all of these positions:
- `WHERE` — correlated & non-correlated; `IN (sub)` / `EXISTS (sub)` / compared scalar subqueries;
- `SELECT` list — scalar subqueries;
- `HAVING`;
- `ORDER BY`;
- `FROM` clause — inline views / derived tables: `SELECT * FROM (SELECT ...) t`;
- `JOIN` clause: `SELECT * FROM t1 JOIN (SELECT ...) t2 ON ...`;
- Nested subqueries & multi-column subqueries.

Example:
```sql
SELECT u.name,
       (SELECT COUNT(*) FROM orders o WHERE o.user_id = u.id) order_cnt
  FROM users u
 WHERE EXISTS (SELECT 1 FROM orders o WHERE o.user_id = u.id);
```
Ref test: `demo/subquery/JQuickSQLSubqueryTest.java`.

### 4.9 Expressions & Custom Functions

- Built-in string & math functions work directly in SELECT and WHERE;
- Custom functions: extensible via the `jquick-transform-function` SPI (`toUpper(name)` used in demos is one such extension);
- Arithmetic `+ - * /`; string concatenation available in SELECT list.

### 4.10 XML Dynamic Proxy & Builder API

- **XML dynamic proxy (iBatis style)**: store SQL statements in classpath XML, materialize Service/DAO interfaces via JDK dynamic proxy with `#{param}` placeholders — see Demo 5 below;
- **Builder fluent API**: `JQuickSQL.builder().embedded(n).config(cfg).table(name,cols,rows).build()` — see Demo 6 below.

---

## V. Demo Examples

> Every demo is a **complete, standalone, runnable** Java class — copy it directly into `src/test/java/demo/` and execute `main()`.
> More cases live in the repo's `src/test/java/com/github/paohaijiao/demo/` directory.

---

### Demo 1: WHERE Filters + Function Expressions

#### Description
Supports `AND / OR / NOT`, comparison operators, `BETWEEN`, `IN`, `LIKE`, `REGEXP`, `IS NULL`, `EXISTS`, plus SPI-extensible functions like `toUpper()` inside expressions and predicates.

#### Complete Runnable Code

```java
package demo;

import com.github.paohaijiao.engine.JQuickSQL;
import com.github.paohaijiao.statement.JQuickColumnMeta;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;

import java.util.Arrays;
import java.util.List;

/**
 * Function: Demonstrates 6 typical WHERE categories: eq/range/in/like/regexp/function/null.
 * Scenario: ETL cleansing, inclusion/exclusion list filtering, dynamic-report conditions.
 * Notes: The `enable` column uses Boolean.class; toUpper() is provided by the jquick-transform-function SPI.
 */
public class Demo01WhereCondition {

    public static void main(String[] args) {
        JQuickSQL sql = JQuickSQL.embedded();
        try {
            registerUsers(sql);

            System.out.println("=== 1) active AND age > 25 ===");
            sql.execute("SELECT id,name,age,status FROM users " +
                    "WHERE age > 25 AND status = 'active'").printTable();

            System.out.println("=== 2) age BETWEEN 20 AND 30 ===");
            sql.execute("SELECT * FROM users WHERE age BETWEEN 20 AND 30").printTable();

            System.out.println("=== 3) addr IN (beijing, shanghai) ===");
            sql.execute("SELECT name, addr FROM users WHERE addr IN ('beijing','shanghai')").printTable();

            System.out.println("=== 4) name LIKE '%Davi%' ===");
            sql.execute("SELECT id, name FROM users WHERE name LIKE '%Davi%'").printTable();

            System.out.println("=== 5) name REGEXP '^A.*' ===");
            sql.execute("SELECT name FROM users WHERE name REGEXP '^A.*'").printTable();

            System.out.println("=== 6) toUpper(name) = 'ALICE' ===");
            sql.execute("SELECT id, toUpper(name) uname FROM users WHERE toUpper(name)='ALICE'").printTable();

            System.out.println("=== 7) addr IS NULL ===");
            sql.execute("SELECT name FROM users WHERE addr IS NULL").printTable();

        } finally {
            sql.shutdown();
        }
    }

    private static void registerUsers(JQuickSQL sql) {
        List<JQuickColumnMeta> cols = Arrays.asList(
                new JQuickColumnMeta("id", Integer.class, "users"),
                new JQuickColumnMeta("name", String.class, "users"),
                new JQuickColumnMeta("age", Integer.class, "users"),
                new JQuickColumnMeta("status", String.class, "users"),
                new JQuickColumnMeta("enable", Boolean.class, "users"),
                new JQuickColumnMeta("addr", String.class, "users")
        );
        List<JQuickRow> rows = Arrays.asList(
                row("id",1,"name","Alice",  "age",25,"status","active",  "enable",true, "addr","beijing"),
                row("id",2,"name","Bob",    "age",30,"status","active",  "enable",true, "addr","shanghai"),
                row("id",3,"name","Charlie","age",20,"status","pending", "enable",false,"addr","chengdu"),
                row("id",4,"name","David",  "age",35,"status","inactive","enable",true, "addr","xian"),
                row("id",5,"name","Eve",    "age",28,"status","active",  "enable",true, "addr","chongqing"),
                row("id",6,"name","Martin", "age",30,"status","active",  "enable",true, "addr","guangzhou"),
                row("id",7,"name","Davila", "age",39,"status","active",  "enable",true, "addr",null)
        );
        sql.registerTable("users", cols, rows);
    }

    private static JQuickRow row(Object... kv) {
        JQuickRow r = new JQuickRow();
        for (int i = 0; i < kv.length; i += 2) r.put((String) kv[i], kv[i + 1]);
        return r;
    }
}
```

---

### Demo 2: Multi-Table JOIN + EXISTS Subquery

#### Description
Supports 5 JOIN types (`INNER / LEFT / RIGHT / CROSS / NATURAL`) plus `EXISTS / IN (subquery)`. Note: FULL OUTER JOIN is not currently supported.

#### Complete Runnable Code

```java
package demo;

import com.github.paohaijiao.engine.JQuickSQL;
import com.github.paohaijiao.statement.JQuickColumnMeta;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;

import java.util.Arrays;
import java.util.List;

/**
 * Function: INNER / LEFT / RIGHT JOIN + EXISTS subquery + aggregated JOIN.
 * Scenario: Order-to-user lookup, LEFT JOIN gap-filling, master/child data reconciliation.
 * Notes: Join-key types on both sides MUST match (Integer↔Integer); otherwise they compare as Object and miss.
 */
public class Demo02JoinAndSubquery {

    public static void main(String[] args) {
        JQuickSQL sql = JQuickSQL.embedded(2);   // 2 parallel Workers inside the same JVM
        try {
            registerUsers(sql);
            registerOrders(sql);

            System.out.println("===== INNER JOIN: Users with orders =====");
            sql.execute("SELECT u.id,u.name,o.id order_id,o.amount " +
                    "FROM users u INNER JOIN orders o ON u.id=o.user_id " +
                    "ORDER BY u.id, o.id").printTable();

            System.out.println("===== LEFT JOIN: All users (incl. no-orders) =====");
            sql.execute("SELECT u.name, o.amount FROM users u " +
                    "LEFT JOIN orders o ON u.id = o.user_id").printTable();

            System.out.println("===== RIGHT JOIN: All orders + corresponding users =====");
            sql.execute("SELECT u.name, o.id, o.amount FROM users u " +
                    "RIGHT JOIN orders o ON u.id = o.user_id").printTable();

            System.out.println("===== EXISTS: Users who have orders =====");
            sql.execute("SELECT u.id, u.name FROM users u WHERE EXISTS (" +
                    "SELECT 1 FROM orders o WHERE o.user_id = u.id)" +
                    " ORDER BY u.id").printTable();

            System.out.println("===== Aggregated JOIN: Total order amount per user =====");
            sql.execute("SELECT u.name, SUM(o.amount) total " +
                    "FROM users u LEFT JOIN orders o ON u.id=o.user_id " +
                    "GROUP BY u.name ORDER BY total DESC NULLS LAST").printTable();

        } finally {
            sql.shutdown();
        }
    }

    private static void registerUsers(JQuickSQL sql) {
        List<JQuickColumnMeta> cols = Arrays.asList(
                new JQuickColumnMeta("id", Integer.class, "users"),
                new JQuickColumnMeta("name", String.class, "users"));
        List<JQuickRow> rows = Arrays.asList(
                row("id", 1, "name", "Alice"),
                row("id", 2, "name", "Bob"),
                row("id", 3, "name", "Charlie"),
                row("id", 4, "name", "David"));
        sql.registerTable("users", cols, rows);
    }

    private static void registerOrders(JQuickSQL sql) {
        List<JQuickColumnMeta> cols = Arrays.asList(
                new JQuickColumnMeta("id", Integer.class, "orders"),
                new JQuickColumnMeta("user_id", Integer.class, "orders"),
                new JQuickColumnMeta("amount", Double.class, "orders"));
        List<JQuickRow> rows = Arrays.asList(
                row("id", 101, "user_id", 1, "amount", 100.0),
                row("id", 102, "user_id", 1, "amount", 200.0),
                row("id", 103, "user_id", 2, "amount", 150.0),
                row("id", 104, "user_id", 3, "amount", 300.0),
                row("id", 105, "user_id", 99, "amount", 99.99)); // orphan user_id
        sql.registerTable("orders", cols, rows);
    }

    private static JQuickRow row(Object... kv) {
        JQuickRow r = new JQuickRow();
        for (int i = 0; i < kv.length; i += 2) r.put((String) kv[i], kv[i + 1]);
        return r;
    }
}
```

---

### Demo 3: GROUP BY + HAVING + ORDER BY + LIMIT Pagination

#### Description
In-memory aggregation + HAVING filtering + multi-column ORDER BY + MySQL-style `LIMIT offset, size` pagination; with `embedded(n)` the engine parallelises large tables inside one JVM.

#### Complete Runnable Code

```java
package demo;

import com.github.paohaijiao.engine.JQuickSQL;
import com.github.paohaijiao.statement.JQuickColumnMeta;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;

import java.util.Arrays;
import java.util.List;

/**
 * Function: COUNT/SUM/AVG/MIN/MAX + HAVING + CASE WHEN bucketing + multi-key sort + pagination.
 * Scenario: BI charts, leaderboards, paginated list APIs.
 * Notes: LIMIT accepts both `LIMIT n` and `LIMIT offset, n` syntax.
 */
public class Demo03GroupByPageOrder {

    public static void main(String[] args) {
        JQuickSQL sql = JQuickSQL.embedded();
        try {
            registerEmployees(sql);

            System.out.println("===== Department-level statistics =====");
            sql.execute("SELECT dept, COUNT(*) emp_cnt, ROUND(AVG(salary),2) avg_sal," +
                    " SUM(salary) sum_sal FROM emp GROUP BY dept ORDER BY sum_sal DESC").printTable();

            System.out.println("===== HAVING: Departments with >= 2 people =====");
            sql.execute("SELECT dept, COUNT(*) c FROM emp GROUP BY dept " +
                    "HAVING COUNT(*) >= 2 ORDER BY c DESC").printTable();

            System.out.println("===== CASE WHEN bucketing + multi-column ORDER BY =====");
            sql.execute("SELECT name, salary, dept, CASE " +
                    "WHEN salary>=30000 THEN 'HIGH' WHEN salary>=20000 THEN 'MID' ELSE 'LOW' END grade " +
                    "FROM emp ORDER BY dept ASC, salary DESC").printTable();

            System.out.println("===== Pagination: salary DESC, skip 2, take 3 =====");
            sql.execute("SELECT name, dept, salary FROM emp " +
                    "ORDER BY salary DESC LIMIT 2, 3").printTable();

        } finally {
            sql.shutdown();
        }
    }

    private static void registerEmployees(JQuickSQL sql) {
        List<JQuickColumnMeta> cols = Arrays.asList(
                new JQuickColumnMeta("id", Integer.class, "emp"),
                new JQuickColumnMeta("name", String.class, "emp"),
                new JQuickColumnMeta("dept", String.class, "emp"),
                new JQuickColumnMeta("salary", Integer.class, "emp"));
        List<JQuickRow> rows = Arrays.asList(
                row("id",1,"name","Alice", "dept","R&D",      "salary",32000),
                row("id",2,"name","Bob",   "dept","R&D",      "salary",28000),
                row("id",3,"name","Carol", "dept","Marketing","salary",22000),
                row("id",4,"name","David", "dept","Marketing","salary",18000),
                row("id",5,"name","Eve",   "dept","Product",  "salary",25000),
                row("id",6,"name","Frank", "dept","R&D",      "salary",35000),
                row("id",7,"name","Grace", "dept","HR",       "salary",15000));
        sql.registerTable("emp", cols, rows);
    }

    private static JQuickRow row(Object... kv) {
        JQuickRow r = new JQuickRow();
        for (int i = 0; i < kv.length; i += 2) r.put((String) kv[i], kv[i + 1]);
        return r;
    }
}
```

---

### Demo 4: Set Operations — UNION / MINUS / INTERSECT

#### Description
Union (deduplicated), set difference (MINUS) and set intersection (INTERSECT). Left & right SELECTs must match in column count and Java types. Perfect for list merging, diff checking and common-item filtering.

#### Complete Runnable Code

```java
package demo;

import com.github.paohaijiao.engine.JQuickSQL;
import com.github.paohaijiao.statement.JQuickColumnMeta;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;

import java.util.Arrays;
import java.util.List;

/**
 * Function: Demonstrates 3 set operations: UNION (dedup), MINUS (difference), INTERSECT.
 * Scenario: Merge two user lists, compute the diff between two snapshots, find common entries.
 * Notes: Column count & column Java types on both sides MUST match.
 *        UNION deduplicates automatically; UNION ALL (no dedup) is NOT supported yet.
 */
public class Demo04SetOperations {

    public static void main(String[] args) {
        JQuickSQL sql = JQuickSQL.embedded();
        try {
            registerListA(sql);
            registerListB(sql);

            System.out.println("===== UNION: list_a + list_b, dedup =====");
            sql.execute("SELECT id, name FROM list_a " +
                        "UNION " +
                        "SELECT id, name FROM list_b " +
                        "ORDER BY id").printTable();

            System.out.println("===== MINUS: users only in A, not in B =====");
            sql.execute("SELECT id, name FROM list_a " +
                        "MINUS " +
                        "SELECT id, name FROM list_b " +
                        "ORDER BY id").printTable();

            System.out.println("===== INTERSECT: users in both A and B =====");
            sql.execute("SELECT id, name FROM list_a " +
                        "INTERSECT " +
                        "SELECT id, name FROM list_b " +
                        "ORDER BY id").printTable();

        } finally {
            sql.shutdown();
        }
    }

    private static void registerListA(JQuickSQL sql) {
        List<JQuickColumnMeta> cols = Arrays.asList(
                new JQuickColumnMeta("id",   Integer.class, "list_a"),
                new JQuickColumnMeta("name", String.class,  "list_a"));
        List<JQuickRow> rows = Arrays.asList(
                row("id", 1, "name", "Alice"),
                row("id", 2, "name", "Bob"),
                row("id", 3, "name", "Charlie"),
                row("id", 4, "name", "David"));      // David only in A
        sql.registerTable("list_a", cols, rows);
    }

    private static void registerListB(JQuickSQL sql) {
        List<JQuickColumnMeta> cols = Arrays.asList(
                new JQuickColumnMeta("id",   Integer.class, "list_b"),
                new JQuickColumnMeta("name", String.class,  "list_b"));
        List<JQuickRow> rows = Arrays.asList(
                row("id", 1, "name", "Alice"),
                row("id", 2, "name", "Bob"),
                row("id", 5, "name", "Eve"),
                row("id", 6, "name", "Frank"));
        sql.registerTable("list_b", cols, rows);
    }

    private static JQuickRow row(Object... kv) {
        JQuickRow r = new JQuickRow();
        for (int i = 0; i < kv.length; i += 2) r.put((String) kv[i], kv[i + 1]);
        return r;
    }
}
```

---

### Demo 5: XML Dynamic SQL Proxy (iBatis Style, Seamless Legacy Migration)

#### Description
Keep the legacy iBatis / MyBatis habit: write SQL in XML files and materialize Service interfaces via JDK dynamic proxy. **Zero-invasion migration for legacy apps**.

#### Complete Runnable Code

```java
package demo;

import com.github.paohaijiao.domain.JQuickTable;
import com.github.paohaijiao.engine.JQuickSQL;
import com.github.paohaijiao.statement.JQuickColumnMeta;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;
import com.github.paohaijiao.xml.JQuickJavaXmlParseFactory;

import java.util.Arrays;
import java.util.List;

/**
 * Function: Store SQL statements in XML; generate DAO/Service proxy via JDK dynamic proxy;
 *           #{param} placeholders are auto-substituted.
 * Scenario: Migrate legacy iBatis 2.x / Struts apps onto JQuick-SQL without rewriting SQL code.
 * Notes: XML `<sqls namespace="...">` MUST match the interface FQCN exactly;
 *        DTD `classpath:paohaijiao/dtd/Jquick-sql.dtd` is bundled with the jar.
 *
 * <pre>{@code
 * <!-- Example: classpath:jquick-sql.xml -->
 * <?xml version="1.0" encoding="UTF-8"?>
 * <!DOCTYPE sqls PUBLIC "-//PAOHAIJIAO//DTD API JAVA 1.0//EN"
 *         "classpath:paohaijiao/dtd/Jquick-sql.dtd">
 * <sqls namespace="demo.Demo05XmlProxy.UserService">
 *     <sql name="topUsers" returnClass="java.util.List">
 *         select id, name, age from users order by age desc limit #{limit}
 *     </sql>
 * </sqls>
 * }</pre>
 */
public class Demo05XmlProxy {

    /** Business interface — namespace in XML must match this FQCN exactly: demo.Demo05XmlProxy.UserService */
    public interface UserService {
        List<JQuickRow> topUsers(int limit);
    }

    public static void main(String[] args) {
        JQuickSQL sql = JQuickSQL.embedded();
        try {
            // 1) Register in-memory table
            List<JQuickColumnMeta> cols = Arrays.asList(
                    new JQuickColumnMeta("id", Integer.class, "users"),
                    new JQuickColumnMeta("name", String.class, "users"),
                    new JQuickColumnMeta("age", Integer.class, "users"));
            List<JQuickRow> rows = Arrays.asList(
                    row("id",1,"name","Alice",  "age",25),
                    row("id",2,"name","Bob",    "age",30),
                    row("id",3,"name","Charlie","age",20),
                    row("id",4,"name","David",  "age",35));
            sql.registerTable("users", cols, rows);

            // 2) Wrap tables as JQuickTable list and feed the XML proxy factory
            List<JQuickTable> tables = Arrays.asList(new JQuickTable("users", cols, rows));
            JQuickJavaXmlParseFactory factory = new JQuickJavaXmlParseFactory(tables);

            // 3) Create proxy — #{limit} placeholders are bound at call time
            UserService service = (UserService) factory
                    .createlInvocationHandler()
                    .getProxy(UserService.class);

            List<JQuickRow> list = service.topUsers(2);
            System.out.println("===== Top 2 by age (XML SQL result) =====");
            new JQuickDataSet(cols, list).printTable();

        } finally {
            sql.shutdown();
        }
    }

    private static JQuickRow row(Object... kv) {
        JQuickRow r = new JQuickRow();
        for (int i = 0; i < kv.length; i += 2) r.put((String) kv[i], kv[i + 1]);
        return r;
    }
}
```

---

### Demo 6: Builder Pattern + Embedded Parallelism Config

#### Description
`JQuickSQL.builder()` fluent API for parallelism, timeouts, retries, and pre-registered tables. `embedded(n)` spins up n parallel Workers inside the same JVM. Useful for code-first configuration and multi-tenant engine factories.

#### Complete Runnable Code

```java
package demo;

import com.github.paohaijiao.config.JQuickSqlConfig;
import com.github.paohaijiao.config.JQuickSqlRuntimeConfig;
import com.github.paohaijiao.engine.JQuickSQL;
import com.github.paohaijiao.statement.JQuickColumnMeta;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;

import java.util.Arrays;
import java.util.List;

/**
 * Function: Build JQuickSQL fluently — parallelism, task timeout, retry count, pre-registered tables.
 * Scenario: Code-first configuration; multi-tenant engine factory; batch-job orchestration.
 * Notes: Example uses embedded(2) to launch 2 parallel Workers inside the same JVM to keep the demo self-contained.
 */
public class Demo06BuilderEmbedded {

    public static void main(String[] args) {
        // 1) Runtime config
        JQuickSqlRuntimeConfig rt = new JQuickSqlRuntimeConfig();
        rt.setDefaultParallelism(2);
        rt.setMaxTaskRetries(3);
        rt.setTaskTimeoutMs(60_000);
        JQuickSqlConfig cfg = new JQuickSqlConfig();
        cfg.setRuntime(rt);

        // 2) City sales table
        List<JQuickColumnMeta> cols = Arrays.asList(
                new JQuickColumnMeta("city",   String.class, "sales"),
                new JQuickColumnMeta("amount", Long.class,   "sales"));
        List<JQuickRow> rows = Arrays.asList(
                row("city","Beijing", "amount",1200L),
                row("city","Shanghai","amount",2100L),
                row("city","Guangzhou","amount",980L),
                row("city","Beijing", "amount",800L),
                row("city","Chengdu", "amount",650L),
                row("city","Shanghai","amount",1300L));

        // 3) Builder (embedded(2) → 2 parallel Workers inside one JVM)
        JQuickSQL sql = JQuickSQL.builder()
                .embedded(2)
                .config(cfg)
                .table("sales", cols, rows)
                .build();

        try {
            System.out.println("===== City GMV aggregation =====");
            JQuickDataSet r = sql.execute(
                    "SELECT city, SUM(amount) gmv, COUNT(*) order_cnt " +
                    "FROM sales GROUP BY city ORDER BY gmv DESC");
            r.printTable();
            System.out.println("Registered tables: " + sql.getRegisteredTables());

        } finally {
            sql.shutdown();
        }
    }

    private static JQuickRow row(Object... kv) {
        JQuickRow r = new JQuickRow();
        for (int i = 0; i < kv.length; i += 2) r.put((String) kv[i], kv[i + 1]);
        return r;
    }
}
```

---

## VI. Core Features

| Category | Capability | Status |
|----------|-----------|--------|
| 🧩 SQL Syntax | SELECT (*) / WHERE / ORDER BY / LIMIT / GROUP BY / HAVING / JOIN (5 types: INNER/LEFT/RIGHT/CROSS/NATURAL) / UNION / MINUS / INTERSECT / Subqueries (8 positions) / CASE WHEN / DISTINCT | ✅ |
| 🔍 Functions | Built-in math / string + **SPI custom functions** (jquick-transform-function) | ✅ |
| 🧠 Optimizer | Predicate pushdown · Projection pushdown · Constant folding · Filter merge (basic rules) | ✅ |
| 🚀 Parallel Execution | `embedded(n)` parallel Workers inside the same JVM; Fragment slicing; Hash/NestedLoop Join | ✅ |
| 🗂️ Data Sources | In-memory registration + ecosystem bridges: jquick-curl (REST JSON) / jquick-excel (Excel rows) / jquick-java (rule scripts); further RDBMS connectors are on the jquick-connector roadmap | ✅ / 🗺️ |
| 🧓 Legacy Compat | JDK 8 baseline; iBatis-style XML dynamic proxy; single-Worker mode embeds into Tomcat 7 apps | ✅ |
| 🔗 Ecosystem Integration | jquick-curl (register REST JSON rows) · jquick-excel (register Excel rows then JOIN) · jquick-java (rule SQL) | ✅ |

> 🗺️ Roadmap: Domestic-Database Dialects (Dameng / KingbaseES / OceanBase / GaussDB / TiDB) are planned to ship as part of the jquick-connector sub-project; they are **not bundled** in jquick-sql core today.

---

## VII. Dependency Configuration

### 7.1 Minimal Runnable `pom.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example</groupId>
    <artifactId>jquick-sql-demo</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <maven.compiler.source>8</maven.compiler.source>
        <maven.compiler.target>8</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <jquick.version>4.1.0</jquick.version>
        <junit.version>4.13.2</junit.version>
    </properties>

    <dependencies>
        <!-- JQuick-SQL Embedded SQL Query Engine -->
        <dependency>
            <groupId>io.github.paohaijiao</groupId>
            <artifactId>jquick-sql</artifactId>
            <version>${jquick.version}</version>
        </dependency>

        <!-- Optional: register REST JSON rows as in-memory tables -->
        <dependency>
            <groupId>io.github.paohaijiao</groupId>
            <artifactId>jquick-curl</artifactId>
            <version>1.3.2</version>
        </dependency>

        <!-- Optional: register Excel rows as in-memory tables -->
        <dependency>
            <groupId>io.github.paohaijiao</groupId>
            <artifactId>jquick-excel</artifactId>
            <version>2.0.1</version>
        </dependency>

        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>${junit.version}</version>
            <scope>test</scope>
        </dependency>
    </dependencies>
</project>
```

### 7.2 `jquick.properties` (optional, classpath)

```properties
# Banner
jquick.banner.enabled=true
jquick.banner.slogan=JQuick-SQL · Just Query, Quickly!

# Runtime (parallel Workers inside the same JVM)
jquick.runtime.defaultParallelism=4
jquick.runtime.maxFileSize=134217728
jquick.runtime.maxTaskRetries=3
jquick.runtime.taskTimeoutMs=60000
```

---

## VIII. Caveats & Notes

| # | Notes |
|---|-------|
| 1 | `embedded(n)` occupies ports `19001 .. 19000+n`; open firewall accordingly. Always `shutdown()` in a `finally` block. |
| 2 | The 3rd constructor arg of `JQuickColumnMeta` is the **table alias**; SQL aliases must match exactly, otherwise column resolution fails. |
| 3 | Booleans: prefer `Boolean.class` over `String "true"/"false"`; otherwise `WHERE enable` is a string comparison and fails. |
| 4 | **Tomcat 7 / legacy apps**: prefer `embedded(1)` single-worker mode. If gRPC-netty-shaded hits reflection-permission issues on older JDK 8 builds, swap to `grpc-netty`. |
| 5 | **Domestic dialects**: planned only as part of the jquick-connector sub-project roadmap — no dialect packs or automatic pagination translation ship with jquick-sql core. |
| 6 | **License boundary**: jquick-sql itself is Apache-2.0 (free commercial use). If you also ship jquick-pdf, please review the AGPL-3.0 separately. |
| 7 | `JQuickSQL` is thread-safe (internal lock) — keep it as a singleton. `JQuickDataSet` is NOT thread-safe. |
| 8 | For huge SQL / huge datasets, raise `taskTimeoutMs` and bump `embedded(n)` parallelism (use `Runtime.getRuntime().availableProcessors()` as a baseline). |
| 9 | FULL OUTER JOIN is not supported. CTE / Recursive UNION / Window Functions are **not implemented** — do not rely on them in production SQL. |

---

## IX. Contributing

> Refer to the [paohaijiao organization contribution guide](https://github.com/paohaijiao). JQuick-SQL specific add-ons:

1. Fork → branch `feature/xxx` / `fix/issue-123` / `docs/xxx`;
2. Make sure local `mvn clean test` passes fully before committing;
3. **Any capability add/change MUST ship a standalone runnable Demo** under `src/test/java/com/github/paohaijiao/demo/` with import + main/@Test + comment header;
4. Every Optimizer Rule needs both **positive & negative** tests under `src/test/java/com/github/paohaijiao/optimizer/*Test.java`;
5. Parallel-execution bugs: ship a minimal reproduction with an `embedded(2)` test case;
6. PR titles use `[module] short description`, e.g. `[optimizer] Extend ProjectionPushdown to support derived tables`;
7. Once merged, your avatar auto-joins the contributors wall 🙌.

---

<div align="center">

**⬆️ Back to [【JQuick Ecosystem】](#iii-jquick-ecosystem-navigation) · [Apache-2.0](LICENSE) · Made with ❤️ by [paohaijiao](https://github.com/paohaijiao)**

</div>
