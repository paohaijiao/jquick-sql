# JQuick-SQL

JQuick-SQL is a lightweight distributed SQL query engine for Java applications, providing SQL parsing, query planning, optimization, 
and distributed execution. Together with **jquick-connector**, it forms a **logical data warehouse** with **federated query capabilities**—delivering 
unified SQL access across heterogeneous data sources (relational databases, files, NoSQL, REST APIs, and more). No data movement, no complex
pipelines. Simply query, join, and aggregate across systems with minimal overhead.





<div align="center">

[![GitHub Stars](https://img.shields.io/github/stars/paohaijiao/jquick-sql?style=flat-square)](https://github.com/paohaijiao/jquick-sql/stargazers)
[![GitHub Forks](https://img.shields.io/github/forks/paohaijiao/jquick-sql?style=flat-square)](https://github.com/paohaijiao/jquick-sql/forks)
[![GitHub Issues](https://img.shields.io/github/issues/paohaijiao/jquick-sql?style=flat-square)](https://github.com/paohaijiao/jquick-sql/issues)
[![License](https://img.shields.io/github/license/paohaijiao/jquick-sql?style=flat-square)](LICENSE)
[![Java Version](https://img.shields.io/badge/Java-8%2B-blue?style=flat-square&logo=java)](https://www.oracle.com/java/)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.paohaijiao/jquick-sql?style=flat-square)](https://search.maven.org/search?q=g:com.github.paohaijiao%20AND%20a:jquick-sql)
[![Last Commit](https://img.shields.io/github/last-commit/paohaijiao/jquick-sql?style=flat-square)](https://github.com/paohaijiao/jquick-sql/commits)
[![Code Size](https://img.shields.io/github/languages/code-size/paohaijiao/jquick-sql?style=flat-square)](https://github.com/paohaijiao/jquick-sql)
[![GitHub Release](https://img.shields.io/github/release/paohaijiao/jquick-sql?style=flat-square)](https://github.com/paohaijiao/jquick-sql/releases)

</div>

## Architecture

```
┌───────────────────────────────────────────────────────────────────────────────┐
│                           JQuickSQL Engine                                   │
├───────────────────────────────────────────────────────────────────────────────┤
│  SQL Input → Parser → AST → Logical Plan → Optimizer → Physical Plan        │
│                                                                              │
│                              ↓                                               │
│                        Fragmenter                                            │
│                                                                              │
│                              ↓                                               │
│                   Coordinator → Workers (gRPC)                              │
│                                                                              │
│                              ↓                                               │
│                        Result → DataSet                                     │
└───────────────────────────────────────────────────────────────────────────────┘
```
## Features

- ✅ SQL Parser 
- ✅ Logical and Physical Query Plan
- ✅ Query Optimization (predicate pushdown, projection pushdown, join reorder, etc.)
- ✅ Distributed Query Execution (Coordinator-Worker architecture)
- ✅ Two-phase Aggregation (Partial → Shuffle → Final)
- ✅ Hash Join / Nested Loop Join
- ✅ Sort / Limit / TopN
- ✅ gRPC-based Data Exchange

## Quick Start

### Maven Dependency

```xml
<dependency>
    <groupId>io.github.paohaijiao</groupId>
    <artifactId>jquick-sql</artifactId>
    <version>${latest.version}</version>
</dependency>
```

### Basic Usage

## Data Source Integration
JQuick-SQL, combined with **JQuick-Connector**, enables you to integrate external data into `JQuickDataSet`, which can then be transformed and processed using JQuick-SQL's query engine.

> **Connector Project:** [paohaijiao/jquick-connector](https://github.com/paohaijiao/jquick-connector)
>
> **Maven Dependency:**
> ```xml
> <dependency>
>     <groupId>io.github.paohaijiao</groupId>
>     <artifactId>jquick-connector</artifactId>
>     <version>${latest.version}</version>
> </dependency>

## Supported SQL Features


```java
// Create embedded SQL engine
JQuickSQL sql = JQuickSQL.embedded();
// Register test data
List<JQuickColumnMeta> columns = Arrays.asList(
    new JQuickColumnMeta("id", Integer.class, "users"),
    new JQuickColumnMeta("name", String.class, "users"),
    new JQuickColumnMeta("age", Integer.class, "users")
);
List<JQuickRow> rows = Arrays.asList(
    createRow("id", 1, "name", "Alice", "age", 25),
    createRow("id", 2, "name", "Bob", "age", 30)
);
sql.registerTable("users", columns, rows);
// Execute SQL
JQuickDataSet result = sql.execute("SELECT * FROM users");
result.printTable();
// Shutdown
sql.shutdown();
```

## SQL Examples

| Feature                                 | Status |
|-----------------------------------------|--------|
| SELECT                                  | ✅ |
| WHERE                                   | ✅ |
| ORDER BY                                | ✅ |
| LIMIT / OFFSET                          | ✅ |
| GROUP BY/HAVING                         | ✅ |
| JOIN (INNER/LEFT/RIGHT/FULL/CROSS JOIN) | ✅ |
| UNION/ MINUS/INTERSECT                  | ✅ |
| Aggregation (COUNT/SUM/AVG/MIN/MAX)     | ✅ |
| Subquery                                | ✅ |
| Functions                               | ✅ |

### 1. SELECT Query
**Input Data**

| id | name | age | status | enable | addr | birthday |
|----|------|-----|--------|--------|------|----------|
| 1 | Alice | 25 | active | true | beijing | 2020-04-09 |
| 2 | Bob | 30 | active | true | shanghai | 1991-08-09 |
| 3 | Charlie | 20 | pending | false | chengdu | 1988-07-12 |
| 4 | David | 35 | inactive | true | xian | 1955-11-29 |
| 5 | Eve | 28 | active | true | chongqing | 2003-07-12 |
| 6 | Martin | 30 | active | true | guangzhou | 1978-06-30 |

#### 1.1 
> Returns all columns and rows from the `users` table. Useful for viewing the complete dataset.

**SQL Code**
```sql
SELECT * FROM users
```
**Output Data**
```log
[2026-07-23 11:19:19.052] [INFO] +----+---------+-----+----------+--------+-----------+----------------------+
[2026-07-23 11:19:19.052] [INFO] | id | name    | age | status   | enable | addr      | birthday             |
[2026-07-23 11:19:19.052] [INFO] +----+---------+-----+----------+--------+-----------+----------------------+
[2026-07-23 11:19:19.053] [INFO] | 1  | Alice   | 25  | active   | true   | beijing   | 2020-04-08T16:00:00Z |
[2026-07-23 11:19:19.053] [INFO] | 2  | Bob     | 30  | active   | true   | shanghai  | 1991-08-08T15:00:00Z |
[2026-07-23 11:19:19.053] [INFO] | 3  | Charlie | 20  | pending  | false  | chengdu   | 1988-07-11T15:00:00Z |
[2026-07-23 11:19:19.053] [INFO] | 4  | David   | 35  | inactive | true   | xian      | 1955-11-28T16:00:00Z |
[2026-07-23 11:19:19.053] [INFO] | 5  | Eve     | 28  | active   | true   | chongqing | 2003-07-11T16:00:00Z |
[2026-07-23 11:19:19.053] [INFO] | 6  | Martin  | 30  | active   | true   | guangzhou | 1978-06-29T16:00:00Z |
[2026-07-23 11:19:19.053] [INFO] +----+---------+-----+----------+--------+-----------+----------------------+
```
---
#### 1.2
>  Returns only the specified columns (`id`, `name`, `age`, `status`, `enable`, `addr`) from the `users` table

**SQL Code**
```sql
SELECT id, name,age, status,enable,addr FROM users
```
```log
[2026-07-23 11:21:39.973] [INFO] +----+---------+-----+----------+--------+-----------+
[2026-07-23 11:21:39.973] [INFO] | id | name    | age | status   | enable | addr      |
[2026-07-23 11:21:39.973] [INFO] +----+---------+-----+----------+--------+-----------+
[2026-07-23 11:21:39.973] [INFO] | 1  | Alice   | 25  | active   | true   | beijing   |
[2026-07-23 11:21:39.973] [INFO] | 2  | Bob     | 30  | active   | true   | shanghai  |
[2026-07-23 11:21:39.974] [INFO] | 3  | Charlie | 20  | pending  | false  | chengdu   |
[2026-07-23 11:21:39.974] [INFO] | 4  | David   | 35  | inactive | true   | xian      |
[2026-07-23 11:21:39.974] [INFO] | 5  | Eve     | 28  | active   | true   | chongqing |
[2026-07-23 11:21:39.974] [INFO] | 6  | Martin  | 30  | active   | true   | guangzhou |
[2026-07-23 11:21:39.974] [INFO] +----+---------+-----+----------+--------+-----------+
```

#### 1.3
> Built-in functions like `toUpper()` are provided by [**jquick-transform-function**](https://github.com/paohaijiao/jquick-transform-function) and can be extended via <span style="color:red"> **SPI**</span>.(Service Provider Interface).

**SQL Code**
```sql
SELECT id, toUpper(name) as upperName,age, status,enable,addr,birthday FROM users
```
```log
[2026-07-23 11:22:48.126] [INFO] +----+-----------+-----+----------+--------+-----------+----------------------+
[2026-07-23 11:22:48.126] [INFO] | id | upperName | age | status   | enable | addr      | birthday             |
[2026-07-23 11:22:48.126] [INFO] +----+-----------+-----+----------+--------+-----------+----------------------+
[2026-07-23 11:22:48.126] [INFO] | 1  | ALICE     | 25  | active   | true   | beijing   | 2020-04-08T16:00:00Z |
[2026-07-23 11:22:48.126] [INFO] | 2  | BOB       | 30  | active   | true   | shanghai  | 1991-08-08T15:00:00Z |
[2026-07-23 11:22:48.126] [INFO] | 3  | CHARLIE   | 20  | pending  | false  | chengdu   | 1988-07-11T15:00:00Z |
[2026-07-23 11:22:48.126] [INFO] | 4  | DAVID     | 35  | inactive | true   | xian      | 1955-11-28T16:00:00Z |
[2026-07-23 11:22:48.126] [INFO] | 5  | EVE       | 28  | active   | true   | chongqing | 2003-07-11T16:00:00Z |
[2026-07-23 11:22:48.126] [INFO] | 6  | MARTIN    | 30  | active   | true   | guangzhou | 1978-06-29T16:00:00Z |
[2026-07-23 11:22:48.126] [INFO] +----+-----------+-----+----------+--------+-----------+----------------------+
```

#### 1.4
>Supports nested arithmetic expressions, e.g., (age + 1) * 3 on the age column, and aliases name as upperName.

**SQL Code**
```sql
SELECT id, name as upperName,(age+1)*3 as age, status,enable,addr,birthday FROM users
```
```log
[2026-07-23 11:23:35.632] [INFO] +----+-----------+-------+----------+--------+-----------+----------------------+
[2026-07-23 11:23:35.632] [INFO] | id | upperName | age   | status   | enable | addr      | birthday             |
[2026-07-23 11:23:35.632] [INFO] +----+-----------+-------+----------+--------+-----------+----------------------+
[2026-07-23 11:23:35.632] [INFO] | 1  | Alice     | 78.0  | active   | true   | beijing   | 2020-04-08T16:00:00Z |
[2026-07-23 11:23:35.632] [INFO] | 2  | Bob       | 93.0  | active   | true   | shanghai  | 1991-08-08T15:00:00Z |
[2026-07-23 11:23:35.633] [INFO] | 3  | Charlie   | 63.0  | pending  | false  | chengdu   | 1988-07-11T15:00:00Z |
[2026-07-23 11:23:35.633] [INFO] | 4  | David     | 108.0 | inactive | true   | xian      | 1955-11-28T16:00:00Z |
[2026-07-23 11:23:35.633] [INFO] | 5  | Eve       | 87.0  | active   | true   | chongqing | 2003-07-11T16:00:00Z |
[2026-07-23 11:23:35.633] [INFO] | 6  | Martin    | 93.0  | active   | true   | guangzhou | 1978-06-29T16:00:00Z |
[2026-07-23 11:23:35.633] [INFO] +----+-----------+-------+----------+--------+-----------+----------------------+
```

#### 1.5
> Supports CASE WHEN conditional expressions to categorize age into groups.

**SQL Code**
```sql
SELECT id, name, age, CASE WHEN age >= 30 THEN '中年'      WHEN age >= 20 THEN '青年'      ELSE '少年' END AS age_group FROM users
```
```log
[2026-07-23 11:24:12.889] [INFO] +----+---------+-----+-----------+
[2026-07-23 11:24:12.890] [INFO] | id | name    | age | age_group |
[2026-07-23 11:24:12.890] [INFO] +----+---------+-----+-----------+
[2026-07-23 11:24:12.890] [INFO] | 1  | Alice   | 25  | 青年        |
[2026-07-23 11:24:12.890] [INFO] | 2  | Bob     | 30  | 中年        |
[2026-07-23 11:24:12.890] [INFO] | 3  | Charlie | 20  | 青年        |
[2026-07-23 11:24:12.890] [INFO] | 4  | David   | 35  | 中年        |
[2026-07-23 11:24:12.890] [INFO] | 5  | Eve     | 28  | 青年        |
[2026-07-23 11:24:12.890] [INFO] | 6  | Martin  | 30  | 中年        |
[2026-07-23 11:24:12.890] [INFO] +----+---------+-----+-----------+
```

#### 1.6
>Returns distinct  values from the users table, removing duplicates.

**SQL Code**
```sql
SELECT distinct age FROM users
```
```log
[2026-07-23 11:25:44.641] [INFO] +-----+
[2026-07-23 11:25:44.641] [INFO] | age |
[2026-07-23 11:25:44.641] [INFO] +-----+
[2026-07-23 11:25:44.641] [INFO] | 25  |
[2026-07-23 11:25:44.641] [INFO] | 30  |
[2026-07-23 11:25:44.641] [INFO] | 20  |
[2026-07-23 11:25:44.641] [INFO] | 35  |
[2026-07-23 11:25:44.641] [INFO] | 28  |
[2026-07-23 11:25:44.641] [INFO] +-----+
[2026-07-23 11:25:44.641] [INFO] Total: 5 rows
```
#### 1.7
>Supports  express '!' conduct boolean negation (e.g., !enable).

**SQL Code**
```sql
SELECT id, toUpper(name) as upperName,age, status,!enable,addr,birthday FROM users
```
```log
[2026-07-23 11:26:55.089] [INFO] +----+-----------+-----+----------+------------+-----------+----------------------+
[2026-07-23 11:26:55.089] [INFO] | id | upperName | age | status   | NOT enable | addr      | birthday             |
[2026-07-23 11:26:55.089] [INFO] +----+-----------+-----+----------+------------+-----------+----------------------+
[2026-07-23 11:26:55.089] [INFO] | 1  | ALICE     | 25  | active   | false      | beijing   | 2020-04-08T16:00:00Z |
[2026-07-23 11:26:55.089] [INFO] | 2  | BOB       | 30  | active   | false      | shanghai  | 1991-08-08T15:00:00Z |
[2026-07-23 11:26:55.089] [INFO] | 3  | CHARLIE   | 20  | pending  | true       | chengdu   | 1988-07-11T15:00:00Z |
[2026-07-23 11:26:55.089] [INFO] | 4  | DAVID     | 35  | inactive | false      | xian      | 1955-11-28T16:00:00Z |
[2026-07-23 11:26:55.090] [INFO] | 5  | EVE       | 28  | active   | false      | chongqing | 2003-07-11T16:00:00Z |
[2026-07-23 11:26:55.090] [INFO] | 6  | MARTIN    | 30  | active   | false      | guangzhou | 1978-06-29T16:00:00Z |
[2026-07-23 11:26:55.090] [INFO] +----+-----------+-----+----------+------------+-----------+----------------------+
[2026-07-23 11:26:55.090] [INFO] Total: 6 rows
```
#### 1.8
> Supports constant expressions (e.g., 0 as index, 'hello' as greeting, 1 + 1 as two) as fields in the SELECT clause.

**SQL Code**
```sql
SELECT 0 as index,id, toUpper(name) as upperName,age, status,!enable,addr,birthday FROM users
```
```log
[2026-07-23 11:27:41.668] [INFO] +-------+----+-----------+-----+----------+------------+-----------+----------------------+
[2026-07-23 11:27:41.668] [INFO] | index | id | upperName | age | status   | NOT enable | addr      | birthday             |
[2026-07-23 11:27:41.669] [INFO] +-------+----+-----------+-----+----------+------------+-----------+----------------------+
[2026-07-23 11:27:41.669] [INFO] | 0.0   | 1  | ALICE     | 25  | active   | false      | beijing   | 2020-04-08T16:00:00Z |
[2026-07-23 11:27:41.669] [INFO] | 0.0   | 2  | BOB       | 30  | active   | false      | shanghai  | 1991-08-08T15:00:00Z |
[2026-07-23 11:27:41.669] [INFO] | 0.0   | 3  | CHARLIE   | 20  | pending  | true       | chengdu   | 1988-07-11T15:00:00Z |
[2026-07-23 11:27:41.669] [INFO] | 0.0   | 4  | DAVID     | 35  | inactive | false      | xian      | 1955-11-28T16:00:00Z |
[2026-07-23 11:27:41.669] [INFO] | 0.0   | 5  | EVE       | 28  | active   | false      | chongqing | 2003-07-11T16:00:00Z |
[2026-07-23 11:27:41.670] [INFO] | 0.0   | 6  | MARTIN    | 30  | active   | false      | guangzhou | 1978-06-29T16:00:00Z |
[2026-07-23 11:27:41.670] [INFO] +-------+----+-----------+-----+----------+------------+-----------+----------------------+
[2026-07-23 11:27:41.670] [INFO] Total: 6 rows
```
### 2. WHERE Query
>Filters rows based on specified conditions. Supports comparison operators (=, >, >=, <, <=, <>), 
> logical operators (AND, OR, NOT), NULL checks (IS NULL, IS NOT NULL), range queries (BETWEEN), 
> Set membership (IN), Pattern Matching (LIKE), regular expressions (REGEXP), and subqueries (EXISTS).


**Input Data**

| id | name | age | status | enable | addr | birthday |
|----|------|-----|--------|--------|------|----------|
| 1 | Alice | 25 | active | true | beijing | 2020-04-09 |
| 2 | Bob | 30 | active | true | shanghai | 1991-08-09 |
| 3 | Charlie | 20 | pending | false | chengdu | 1988-07-12 |
| 4 | David | 35 | inactive | true | xian | 1955-11-29 |
| 5 | Eve | 28 | active | true | chongqing | 2003-07-12 |
| 6 | Martin | 30 | active | true | guangzhou | 1978-06-30 |
| 7 | Davila | 39 | active | true | null | 1999-06-30 |

#### 2.1 
> WHERE column = value — Filters rows based on an equality condition, e.g., status = 'active'.

**SQL Code**
```sql
SELECT * FROM users WHERE status = 'active'
```
```log
[2026-07-23 11:32:33.878] [INFO] +----+--------+-----+--------+--------+-----------+----------------------+
[2026-07-23 11:32:33.878] [INFO] | id | name   | age | status | enable | addr      | birthday             |
[2026-07-23 11:32:33.878] [INFO] +----+--------+-----+--------+--------+-----------+----------------------+
[2026-07-23 11:32:33.878] [INFO] | 1  | Alice  | 25  | active | true   | beijing   | 2020-04-08T16:00:00Z |
[2026-07-23 11:32:33.878] [INFO] | 2  | Bob    | 30  | active | true   | shanghai  | 1991-08-08T15:00:00Z |
[2026-07-23 11:32:33.878] [INFO] | 5  | Eve    | 28  | active | true   | chongqing | 2003-07-11T16:00:00Z |
[2026-07-23 11:32:33.878] [INFO] | 6  | Martin | 30  | active | true   | guangzhou | 1978-06-29T16:00:00Z |
[2026-07-23 11:32:33.878] [INFO] | 7  | Davila | 39  | active | true   | null      | 1999-06-29T16:00:00Z |
[2026-07-23 11:32:33.878] [INFO] +----+--------+-----+--------+--------+-----------+----------------------+
[2026-07-23 11:32:33.878] [INFO] Total: 5 rows
```
#### 2.2
>WHERE condition1 AND condition2 — Filters rows using multiple conditions with logical AND. 
> This query returns active users with age greater than 25.

**SQL Code**
```sql
SELECT * FROM users WHERE age > 25 AND status = 'active'
```
```log
[2026-07-23 11:33:27.498] [INFO] +----+--------+-----+--------+--------+-----------+----------------------+
[2026-07-23 11:33:27.498] [INFO] | id | name   | age | status | enable | addr      | birthday             |
[2026-07-23 11:33:27.498] [INFO] +----+--------+-----+--------+--------+-----------+----------------------+
[2026-07-23 11:33:27.498] [INFO] | 2  | Bob    | 30  | active | true   | shanghai  | 1991-08-08T15:00:00Z |
[2026-07-23 11:33:27.498] [INFO] | 5  | Eve    | 28  | active | true   | chongqing | 2003-07-11T16:00:00Z |
[2026-07-23 11:33:27.498] [INFO] | 6  | Martin | 30  | active | true   | guangzhou | 1978-06-29T16:00:00Z |
[2026-07-23 11:33:27.498] [INFO] | 7  | Davila | 39  | active | true   | null      | 1999-06-29T16:00:00Z |
[2026-07-23 11:33:27.498] [INFO] +----+--------+-----+--------+--------+-----------+----------------------+
[2026-07-23 11:33:27.498] [INFO] Total: 4 rows
```

#### 2.3
>WHERE condition1 OR condition2 — Filters rows using logical OR. 
> This query returns users who are either pending or enabled.
> 
**SQL Code**
```sql
SELECT * FROM users WHERE status = 'pending' OR enable = true
```
```log
[2026-07-23 11:34:40.825] [INFO] +----+---------+-----+----------+--------+-----------+----------------------+
[2026-07-23 11:34:40.825] [INFO] | id | name    | age | status   | enable | addr      | birthday             |
[2026-07-23 11:34:40.825] [INFO] +----+---------+-----+----------+--------+-----------+----------------------+
[2026-07-23 11:34:40.825] [INFO] | 1  | Alice   | 25  | active   | true   | beijing   | 2020-04-08T16:00:00Z |
[2026-07-23 11:34:40.825] [INFO] | 2  | Bob     | 30  | active   | true   | shanghai  | 1991-08-08T15:00:00Z |
[2026-07-23 11:34:40.825] [INFO] | 3  | Charlie | 20  | pending  | false  | chengdu   | 1988-07-11T15:00:00Z |
[2026-07-23 11:34:40.825] [INFO] | 4  | David   | 35  | inactive | true   | xian      | 1955-11-28T16:00:00Z |
[2026-07-23 11:34:40.826] [INFO] | 5  | Eve     | 28  | active   | true   | chongqing | 2003-07-11T16:00:00Z |
[2026-07-23 11:34:40.826] [INFO] | 6  | Martin  | 30  | active   | true   | guangzhou | 1978-06-29T16:00:00Z |
[2026-07-23 11:34:40.826] [INFO] | 7  | Davila  | 39  | active   | true   | null      | 1999-06-29T16:00:00Z |
[2026-07-23 11:34:40.826] [INFO] +----+---------+-----+----------+--------+-----------+----------------------+
[2026-07-23 11:34:40.826] [INFO] Total: 7 rows
```

#### 2.4
>WHERE condition OR (condition OR condition) — Supports nested parentheses for complex logical grouping. This query
> returns users who are older than 30, or have status 'pending', or live in 'chengdu'.
>

**SQL Code**
```sql
SELECT * FROM users WHERE age > 30 OR (status = 'pending' OR addr = 'chengdu')
```
```log
[2026-07-23 11:35:36.293] [INFO] +----+---------+-----+----------+--------+---------+----------------------+
[2026-07-23 11:35:36.293] [INFO] | id | name    | age | status   | enable | addr    | birthday             |
[2026-07-23 11:35:36.293] [INFO] +----+---------+-----+----------+--------+---------+----------------------+
[2026-07-23 11:35:36.293] [INFO] | 3  | Charlie | 20  | pending  | false  | chengdu | 1988-07-11T15:00:00Z |
[2026-07-23 11:35:36.294] [INFO] | 4  | David   | 35  | inactive | true   | xian    | 1955-11-28T16:00:00Z |
[2026-07-23 11:35:36.294] [INFO] | 7  | Davila  | 39  | active   | true   | null    | 1999-06-29T16:00:00Z |
[2026-07-23 11:35:36.294] [INFO] +----+---------+-----+----------+--------+---------+----------------------+
[2026-07-23 11:35:36.294] [INFO] Total: 3 rows
```

#### 2.5
>WHERE true — Filters rows with a constant boolean condition. This query returns all rows from the users table.


**SQL Code**
```sql
SELECT * FROM users WHERE true
```
```log
[2026-07-23 11:36:17.974] [INFO] +----+---------+-----+----------+--------+-----------+----------------------+
[2026-07-23 11:36:17.975] [INFO] | id | name    | age | status   | enable | addr      | birthday             |
[2026-07-23 11:36:17.975] [INFO] +----+---------+-----+----------+--------+-----------+----------------------+
[2026-07-23 11:36:17.975] [INFO] | 1  | Alice   | 25  | active   | true   | beijing   | 2020-04-08T16:00:00Z |
[2026-07-23 11:36:17.975] [INFO] | 2  | Bob     | 30  | active   | true   | shanghai  | 1991-08-08T15:00:00Z |
[2026-07-23 11:36:17.975] [INFO] | 3  | Charlie | 20  | pending  | false  | chengdu   | 1988-07-11T15:00:00Z |
[2026-07-23 11:36:17.975] [INFO] | 4  | David   | 35  | inactive | true   | xian      | 1955-11-28T16:00:00Z |
[2026-07-23 11:36:17.975] [INFO] | 5  | Eve     | 28  | active   | true   | chongqing | 2003-07-11T16:00:00Z |
[2026-07-23 11:36:17.975] [INFO] | 6  | Martin  | 30  | active   | true   | guangzhou | 1978-06-29T16:00:00Z |
[2026-07-23 11:36:17.975] [INFO] | 7  | Davila  | 39  | active   | true   | null      | 1999-06-29T16:00:00Z |
[2026-07-23 11:36:17.975] [INFO] +----+---------+-----+----------+--------+-----------+----------------------+
[2026-07-23 11:36:17.975] [INFO] Total: 7 rows
```

#### 2.6
> WHERE column — Filters rows where the boolean column evaluates to true. This query returns all enabled users.

**SQL Code**
```sql
SELECT * FROM users WHERE enable
```
```log
[2026-07-23 11:37:11.555] [INFO] +----+--------+-----+----------+--------+-----------+----------------------+
[2026-07-23 11:37:11.555] [INFO] | id | name   | age | status   | enable | addr      | birthday             |
[2026-07-23 11:37:11.555] [INFO] +----+--------+-----+----------+--------+-----------+----------------------+
[2026-07-23 11:37:11.555] [INFO] | 1  | Alice  | 25  | active   | true   | beijing   | 2020-04-08T16:00:00Z |
[2026-07-23 11:37:11.555] [INFO] | 2  | Bob    | 30  | active   | true   | shanghai  | 1991-08-08T15:00:00Z |
[2026-07-23 11:37:11.555] [INFO] | 4  | David  | 35  | inactive | true   | xian      | 1955-11-28T16:00:00Z |
[2026-07-23 11:37:11.555] [INFO] | 5  | Eve    | 28  | active   | true   | chongqing | 2003-07-11T16:00:00Z |
[2026-07-23 11:37:11.555] [INFO] | 6  | Martin | 30  | active   | true   | guangzhou | 1978-06-29T16:00:00Z |
[2026-07-23 11:37:11.556] [INFO] | 7  | Davila | 39  | active   | true   | null      | 1999-06-29T16:00:00Z |
[2026-07-23 11:37:11.556] [INFO] +----+--------+-----+----------+--------+-----------+----------------------+
[2026-07-23 11:37:11.556] [INFO] Total: 6 rows
```


#### 2.7
>WHERE function(column) = value — Supports function calls in filter conditions. This query converts name to uppercase and returns the user whose name is 'ALICE'.

**SQL Code**
```sql
SELECT * FROM users WHERE toUpper(name)='ALICE'
```
```log
[2026-07-23 11:37:58.226] [INFO] +----+-------+-----+--------+--------+---------+----------------------+
[2026-07-23 11:37:58.226] [INFO] | id | name  | age | status | enable | addr    | birthday             |
[2026-07-23 11:37:58.226] [INFO] +----+-------+-----+--------+--------+---------+----------------------+
[2026-07-23 11:37:58.226] [INFO] | 1  | Alice | 25  | active | true   | beijing | 2020-04-08T16:00:00Z |
[2026-07-23 11:37:58.226] [INFO] +----+-------+-----+--------+--------+---------+----------------------+
[2026-07-23 11:37:58.226] [INFO] Total: 1 rows
```

#### 2.8
>WHERE column IS NULL — Filters rows where a column is NULL. This query returns users whose addr is missing.

**SQL Code**
```sql
SELECT * FROM users WHERE addr is null
```
```log
[2026-07-23 11:38:36.946] [INFO] +----+--------+-----+--------+--------+------+----------------------+
[2026-07-23 11:38:36.946] [INFO] | id | name   | age | status | enable | addr | birthday             |
[2026-07-23 11:38:36.946] [INFO] +----+--------+-----+--------+--------+------+----------------------+
[2026-07-23 11:38:36.946] [INFO] | 7  | Davila | 39  | active | true   | null | 1999-06-29T16:00:00Z |
[2026-07-23 11:38:36.946] [INFO] +----+--------+-----+--------+--------+------+----------------------+
[2026-07-23 11:38:36.946] [INFO] Total: 1 rows
```
#### 2.9
>WHERE column IS NOT NULL — Filters rows where a column is not NULL. This query returns users whose addr has a value.

**SQL Code**
```sql
SELECT * FROM users WHERE addr is not null
```
```log
[2026-07-23 11:39:15.906] [INFO] +----+---------+-----+----------+--------+-----------+----------------------+
[2026-07-23 11:39:15.906] [INFO] | id | name    | age | status   | enable | addr      | birthday             |
[2026-07-23 11:39:15.906] [INFO] +----+---------+-----+----------+--------+-----------+----------------------+
[2026-07-23 11:39:15.906] [INFO] | 1  | Alice   | 25  | active   | true   | beijing   | 2020-04-08T16:00:00Z |
[2026-07-23 11:39:15.907] [INFO] | 2  | Bob     | 30  | active   | true   | shanghai  | 1991-08-08T15:00:00Z |
[2026-07-23 11:39:15.907] [INFO] | 3  | Charlie | 20  | pending  | false  | chengdu   | 1988-07-11T15:00:00Z |
[2026-07-23 11:39:15.907] [INFO] | 4  | David   | 35  | inactive | true   | xian      | 1955-11-28T16:00:00Z |
[2026-07-23 11:39:15.907] [INFO] | 5  | Eve     | 28  | active   | true   | chongqing | 2003-07-11T16:00:00Z |
[2026-07-23 11:39:15.907] [INFO] | 6  | Martin  | 30  | active   | true   | guangzhou | 1978-06-29T16:00:00Z |
[2026-07-23 11:39:15.907] [INFO] +----+---------+-----+----------+--------+-----------+----------------------+
```

#### 2.10
>WHERE column > value — Filters rows using a comparison operator (>, >=, <, <=, =, <>). This query returns users older than 25.

**SQL Code**
```sql
SELECT * FROM users WHERE age >25
```
```log
[2026-07-23 11:39:59.745] [INFO] +----+--------+-----+----------+--------+-----------+----------------------+
[2026-07-23 11:39:59.745] [INFO] | id | name   | age | status   | enable | addr      | birthday             |
[2026-07-23 11:39:59.745] [INFO] +----+--------+-----+----------+--------+-----------+----------------------+
[2026-07-23 11:39:59.745] [INFO] | 2  | Bob    | 30  | active   | true   | shanghai  | 1991-08-08T15:00:00Z |
[2026-07-23 11:39:59.745] [INFO] | 4  | David  | 35  | inactive | true   | xian      | 1955-11-28T16:00:00Z |
[2026-07-23 11:39:59.745] [INFO] | 5  | Eve    | 28  | active   | true   | chongqing | 2003-07-11T16:00:00Z |
[2026-07-23 11:39:59.746] [INFO] | 6  | Martin | 30  | active   | true   | guangzhou | 1978-06-29T16:00:00Z |
[2026-07-23 11:39:59.746] [INFO] | 7  | Davila | 39  | active   | true   | null      | 1999-06-29T16:00:00Z |
[2026-07-23 11:39:59.746] [INFO] +----+--------+-----+----------+--------+-----------+----------------------+
[2026-07-23 11:39:59.746] [INFO] Total: 5 rows
```

#### 2.11
> WHERE column BETWEEN min AND max — Filters rows within a range . This query returns users with age between 25 and 30.

**SQL Code**
```sql
SELECT * FROM users WHERE age  between 25 and 30
```
```log
[2026-07-26 15:24:44.552] [INFO] +----+--------+-----+--------+--------+-----------+----------------------+
[2026-07-26 15:24:44.552] [INFO] | id | name   | age | status | enable | addr      | birthday             |
[2026-07-26 15:24:44.552] [INFO] +----+--------+-----+--------+--------+-----------+----------------------+
[2026-07-26 15:24:44.552] [INFO] | 1  | Alice  | 25  | active | true   | beijing   | 2020-04-08T16:00:00Z |
[2026-07-26 15:24:44.553] [INFO] | 2  | Bob    | 30  | active | true   | shanghai  | 1991-08-08T15:00:00Z |
[2026-07-26 15:24:44.554] [INFO] | 5  | Eve    | 28  | active | true   | chongqing | 2003-07-11T16:00:00Z |
[2026-07-26 15:24:44.554] [INFO] | 6  | Martin | 30  | active | true   | guangzhou | 1978-06-29T16:00:00Z |
[2026-07-26 15:24:44.554] [INFO] +----+--------+-----+--------+--------+-----------+----------------------+
[2026-07-26 15:24:44.554] [INFO] Total: 4 rows
```

#### 2.12
> WHERE column IN (value1, value2, ...) — Filters rows matching any value in a list. This query returns users with age 25 or 30.

**SQL Code**
```sql
SELECT * FROM users WHERE age  in ( 25 , 30)
```
```log
[2026-07-23 11:41:31.655] [INFO] +----+--------+-----+--------+--------+-----------+----------------------+
[2026-07-23 11:41:31.655] [INFO] | id | name   | age | status | enable | addr      | birthday             |
[2026-07-23 11:41:31.655] [INFO] +----+--------+-----+--------+--------+-----------+----------------------+
[2026-07-23 11:41:31.655] [INFO] | 1  | Alice  | 25  | active | true   | beijing   | 2020-04-08T16:00:00Z |
[2026-07-23 11:41:31.655] [INFO] | 2  | Bob    | 30  | active | true   | shanghai  | 1991-08-08T15:00:00Z |
[2026-07-23 11:41:31.655] [INFO] | 6  | Martin | 30  | active | true   | guangzhou | 1978-06-29T16:00:00Z |
[2026-07-23 11:41:31.655] [INFO] +----+--------+-----+--------+--------+-----------+----------------------+
[2026-07-23 11:41:31.655] [INFO] Total: 3 rows
```

#### 2.13
> WHERE column NOT IN (value1, value2, ...) — Filters rows that do not match any value in a list. This query returns users whose age is neither 25 nor 30.
**SQL Code**
```sql
SELECT * FROM users WHERE age not in ( 25 , 30)
```
```log
[2026-07-23 11:42:21.731] [INFO] +----+---------+-----+----------+--------+-----------+----------------------+
[2026-07-23 11:42:21.731] [INFO] | id | name    | age | status   | enable | addr      | birthday             |
[2026-07-23 11:42:21.731] [INFO] +----+---------+-----+----------+--------+-----------+----------------------+
[2026-07-23 11:42:21.731] [INFO] | 3  | Charlie | 20  | pending  | false  | chengdu   | 1988-07-11T15:00:00Z |
[2026-07-23 11:42:21.731] [INFO] | 4  | David   | 35  | inactive | true   | xian      | 1955-11-28T16:00:00Z |
[2026-07-23 11:42:21.731] [INFO] | 5  | Eve     | 28  | active   | true   | chongqing | 2003-07-11T16:00:00Z |
[2026-07-23 11:42:21.731] [INFO] | 7  | Davila  | 39  | active   | true   | null      | 1999-06-29T16:00:00Z |
[2026-07-23 11:42:21.731] [INFO] +----+---------+-----+----------+--------+-----------+----------------------+
[2026-07-23 11:42:21.731] [INFO] Total: 4 rows
```

#### 2.14
> WHERE column LIKE pattern or WHERE column NOT LIKE pattern — Filters rows using pattern matching with wildcards (% for any sequence, _ for a single character). This query returns users whose name contains 'Davi'.

**SQL Code**
```sql
SELECT * FROM users WHERE name like '%Davi%'
```
```log
[2026-07-23 11:43:10.536] [INFO] +----+--------+-----+----------+--------+------+----------------------+
[2026-07-23 11:43:10.536] [INFO] | id | name   | age | status   | enable | addr | birthday             |
[2026-07-23 11:43:10.536] [INFO] +----+--------+-----+----------+--------+------+----------------------+
[2026-07-23 11:43:10.536] [INFO] | 4  | David  | 35  | inactive | true   | xian | 1955-11-28T16:00:00Z |
[2026-07-23 11:43:10.536] [INFO] | 7  | Davila | 39  | active   | true   | null | 1999-06-29T16:00:00Z |
[2026-07-23 11:43:10.536] [INFO] +----+--------+-----+----------+--------+------+----------------------+
[2026-07-23 11:43:10.536] [INFO] Total: 2 rows
```

#### 2.15
> WHERE column REGEXP pattern or WHERE column NOT REGEXP pattern — Filters rows using regular expression matching. This query returns users whose name starts with 'A'.

**SQL Code**
```sql
SELECT * FROM users WHERE name REGEXP '^A.*'
```
```log
[2026-07-23 11:43:45.932] [INFO] +----+-------+-----+--------+--------+---------+----------------------+
[2026-07-23 11:43:45.933] [INFO] | id | name  | age | status | enable | addr    | birthday             |
[2026-07-23 11:43:45.933] [INFO] +----+-------+-----+--------+--------+---------+----------------------+
[2026-07-23 11:43:45.933] [INFO] | 1  | Alice | 25  | active | true   | beijing | 2020-04-08T16:00:00Z |
[2026-07-23 11:43:45.933] [INFO] +----+-------+-----+--------+--------+---------+----------------------+
[2026-07-23 11:43:45.933] [INFO] Total: 1 rows
```

#### 2.16
> WHERE EXISTS (subquery) — Filters rows based on the existence of matching records in a subquery. This query returns users who have at least one order.

**SQL Code**
```sql
SELECT * FROM users u WHERE EXISTS (   SELECT 1 FROM orders o WHERE o.user_id = u.id)
```
```log
[2026-07-23 12:13:18.994] [INFO] +----+---------+-----+---------+--------+----------+----------------------+
[2026-07-23 12:13:18.994] [INFO] | id | name    | age | status  | enable | addr     | birthday             |
[2026-07-23 12:13:18.994] [INFO] +----+---------+-----+---------+--------+----------+----------------------+
[2026-07-23 12:13:18.994] [INFO] | 1  | Alice   | 25  | active  | true   | beijing  | 2020-04-08T16:00:00Z |
[2026-07-23 12:13:18.994] [INFO] | 2  | Bob     | 30  | active  | true   | shanghai | 1991-08-08T15:00:00Z |
[2026-07-23 12:13:18.994] [INFO] | 3  | Charlie | 20  | pending | false  | chengdu  | 1988-07-11T15:00:00Z |
[2026-07-23 12:13:18.994] [INFO] +----+---------+-----+---------+--------+----------+----------------------+
[2026-07-23 12:13:18.994] [INFO] Total: 3 rows
```
### 3. ORDER BY Query
**Input Data**

| id | name | age | status | enable | addr | birthday |
|----|------|-----|--------|--------|------|----------|
| 1 | Alice | 25 | active | true | beijing | 2020-04-09 |
| 2 | Bob | 30 | active | true | shanghai | 1991-08-09 |
| 3 | Charlie | 20 | pending | false | chengdu | 1988-07-12 |
| 4 | David | 35 | inactive | true | xian | 1955-11-29 |
| 5 | Eve | 28 | active | true | chongqing | 2003-07-12 |
| 6 | Martin | 30 | active | true | guangzhou | 1978-06-30 |
| 7 | Davila | 39 | active | true | null | 1999-06-30 |

#### 3.1
> ORDER BY column ASC — Sorts results by the specified column in ascending order. This query returns all users sorted by age from youngest to oldest.

**SQL Code**
```sql
SELECT * FROM users ORDER BY age ASC
```
```log
[2026-07-23 16:53:29.684] [INFO] +----+---------+-----+----------+--------+-----------+----------------------+
[2026-07-23 16:53:29.684] [INFO] | id | name    | age | status   | enable | addr      | birthday             |
[2026-07-23 16:53:29.684] [INFO] +----+---------+-----+----------+--------+-----------+----------------------+
[2026-07-23 16:53:29.684] [INFO] | 3  | Charlie | 20  | pending  | false  | chengdu   | 1988-07-11T15:00:00Z |
[2026-07-23 16:53:29.684] [INFO] | 1  | Alice   | 25  | active   | true   | beijing   | 2020-04-08T16:00:00Z |
[2026-07-23 16:53:29.684] [INFO] | 5  | Eve     | 28  | active   | true   | chongqing | 2003-07-11T16:00:00Z |
[2026-07-23 16:53:29.684] [INFO] | 2  | Bob     | 30  | active   | true   | shanghai  | 1991-08-08T15:00:00Z |
[2026-07-23 16:53:29.685] [INFO] | 6  | Martin  | 30  | active   | true   | guangzhou | 1978-06-29T16:00:00Z |
[2026-07-23 16:53:29.685] [INFO] | 4  | David   | 35  | inactive | true   | xian      | 1955-11-28T16:00:00Z |
[2026-07-23 16:53:29.685] [INFO] | 7  | Davila  | 39  | active   | true   | null      | 1999-06-29T16:00:00Z |
[2026-07-23 16:53:29.685] [INFO] +----+---------+-----+----------+--------+-----------+----------------------+
[2026-07-23 16:53:29.685] [INFO] Total: 7 rows
```

#### 3.2
> ORDER BY column DESC — Sorts results by the specified column in descending order. This query returns all users sorted by age from oldest to youngest.

**SQL Code**
```sql
SELECT * FROM users ORDER BY age DESC
```
```log
[2026-07-23 16:54:29.749] [INFO] +----+---------+-----+----------+--------+-----------+----------------------+
[2026-07-23 16:54:29.749] [INFO] | id | name    | age | status   | enable | addr      | birthday             |
[2026-07-23 16:54:29.749] [INFO] +----+---------+-----+----------+--------+-----------+----------------------+
[2026-07-23 16:54:29.749] [INFO] | 7  | Davila  | 39  | active   | true   | null      | 1999-06-29T16:00:00Z |
[2026-07-23 16:54:29.749] [INFO] | 4  | David   | 35  | inactive | true   | xian      | 1955-11-28T16:00:00Z |
[2026-07-23 16:54:29.750] [INFO] | 2  | Bob     | 30  | active   | true   | shanghai  | 1991-08-08T15:00:00Z |
[2026-07-23 16:54:29.750] [INFO] | 6  | Martin  | 30  | active   | true   | guangzhou | 1978-06-29T16:00:00Z |
[2026-07-23 16:54:29.750] [INFO] | 5  | Eve     | 28  | active   | true   | chongqing | 2003-07-11T16:00:00Z |
[2026-07-23 16:54:29.750] [INFO] | 1  | Alice   | 25  | active   | true   | beijing   | 2020-04-08T16:00:00Z |
[2026-07-23 16:54:29.750] [INFO] | 3  | Charlie | 20  | pending  | false  | chengdu   | 1988-07-11T15:00:00Z |
[2026-07-23 16:54:29.750] [INFO] +----+---------+-----+----------+--------+-----------+----------------------+
[2026-07-23 16:54:29.750] [INFO] Total: 7 rows
```

#### 3.3
> ORDER BY column1 ASC, column2 DESC — Sorts results by multiple columns with different sort directions. This query sorts users by status ascending, then by age descending within the same status group.

**SQL Code**
```sql
SELECT * FROM users ORDER BY status ASC, age DESC
```
```log
[2026-07-23 16:55:13.550] [INFO] +----+---------+-----+----------+--------+-----------+----------------------+
[2026-07-23 16:55:13.550] [INFO] | id | name    | age | status   | enable | addr      | birthday             |
[2026-07-23 16:55:13.550] [INFO] +----+---------+-----+----------+--------+-----------+----------------------+
[2026-07-23 16:55:13.550] [INFO] | 7  | Davila  | 39  | active   | true   | null      | 1999-06-29T16:00:00Z |
[2026-07-23 16:55:13.550] [INFO] | 2  | Bob     | 30  | active   | true   | shanghai  | 1991-08-08T15:00:00Z |
[2026-07-23 16:55:13.550] [INFO] | 6  | Martin  | 30  | active   | true   | guangzhou | 1978-06-29T16:00:00Z |
[2026-07-23 16:55:13.550] [INFO] | 5  | Eve     | 28  | active   | true   | chongqing | 2003-07-11T16:00:00Z |
[2026-07-23 16:55:13.550] [INFO] | 1  | Alice   | 25  | active   | true   | beijing   | 2020-04-08T16:00:00Z |
[2026-07-23 16:55:13.551] [INFO] | 4  | David   | 35  | inactive | true   | xian      | 1955-11-28T16:00:00Z |
[2026-07-23 16:55:13.551] [INFO] | 3  | Charlie | 20  | pending  | false  | chengdu   | 1988-07-11T15:00:00Z |
[2026-07-23 16:55:13.551] [INFO] +----+---------+-----+----------+--------+-----------+----------------------+
[2026-07-23 16:55:13.551] [INFO] Total: 7 rows
```
#### 3.4
>ORDER BY column1 DESC, column2 ASC — Sorts results by multiple columns with different sort directions. This query sorts users by enable descending, then by age ascending within the same group.

**SQL Code**
```sql
SELECT * FROM users ORDER BY enable DESC, age ASC
```
```log
[2026-07-23 16:56:03.295] [INFO] +----+---------+-----+----------+--------+-----------+----------------------+
[2026-07-23 16:56:03.295] [INFO] | id | name    | age | status   | enable | addr      | birthday             |
[2026-07-23 16:56:03.295] [INFO] +----+---------+-----+----------+--------+-----------+----------------------+
[2026-07-23 16:56:03.295] [INFO] | 1  | Alice   | 25  | active   | true   | beijing   | 2020-04-08T16:00:00Z |
[2026-07-23 16:56:03.296] [INFO] | 5  | Eve     | 28  | active   | true   | chongqing | 2003-07-11T16:00:00Z |
[2026-07-23 16:56:03.296] [INFO] | 2  | Bob     | 30  | active   | true   | shanghai  | 1991-08-08T15:00:00Z |
[2026-07-23 16:56:03.296] [INFO] | 6  | Martin  | 30  | active   | true   | guangzhou | 1978-06-29T16:00:00Z |
[2026-07-23 16:56:03.296] [INFO] | 4  | David   | 35  | inactive | true   | xian      | 1955-11-28T16:00:00Z |
[2026-07-23 16:56:03.296] [INFO] | 7  | Davila  | 39  | active   | true   | null      | 1999-06-29T16:00:00Z |
[2026-07-23 16:56:03.296] [INFO] | 3  | Charlie | 20  | pending  | false  | chengdu   | 1988-07-11T15:00:00Z |
[2026-07-23 16:56:03.296] [INFO] +----+---------+-----+----------+--------+-----------+----------------------+
[2026-07-23 16:56:03.296] [INFO] Total: 7 rows
```
#### 3.4
> Skip the first 2 records (offset), then return the next 3 rows
**SQL Code**
```sql
SELECT * FROM users LIMIT  2, 3
```
```log

[2026-07-28 16:58:34.179] [INFO] +----+---------+-----+----------+--------+-----------+----------------------+
[2026-07-28 16:58:34.179] [INFO] | id | name    | age | status   | enable | addr      | birthday             |
[2026-07-28 16:58:34.179] [INFO] +----+---------+-----+----------+--------+-----------+----------------------+
[2026-07-28 16:58:34.179] [INFO] | 3  | Charlie | 20  | pending  | false  | chengdu   | 1988-07-11T15:00:00Z |
[2026-07-28 16:58:34.180] [INFO] | 4  | David   | 35  | inactive | true   | xian      | 1955-11-28T16:00:00Z |
[2026-07-28 16:58:34.180] [INFO] | 5  | Eve     | 28  | active   | true   | chongqing | 2003-07-11T16:00:00Z |
[2026-07-28 16:58:34.180] [INFO] +----+---------+-----+----------+--------+-----------+----------------------+
[2026-07-28 16:58:34.180] [INFO] Total: 3 rows
```

### 4. LIMIT OFFSET Query
**Input Data**

| id | name | age | status | enable | addr | birthday |
|----|------|-----|--------|--------|------|----------|
| 1 | Alice | 25 | active | true | beijing | 2020-04-09 |
| 2 | Bob | 30 | active | true | shanghai | 1991-08-09 |
| 3 | Charlie | 20 | pending | false | chengdu | 1988-07-12 |
| 4 | David | 35 | inactive | true | xian | 1955-11-29 |
| 5 | Eve | 28 | active | true | chongqing | 2003-07-12 |
| 6 | Martin | 30 | active | true | guangzhou | 1978-06-30 |
| 7 | Davila | 39 | active | true | null | 1999-06-30 |


#### 4.1
> LIMIT n — Limits the number of rows returned. This query returns the first 3 rows from the users table.

**SQL Code**
```sql
SELECT * FROM users LIMIT 3
```
```log
[2026-07-23 16:59:29.806] [INFO] +----+---------+-----+---------+--------+----------+----------------------+
[2026-07-23 16:59:29.806] [INFO] | id | name    | age | status  | enable | addr     | birthday             |
[2026-07-23 16:59:29.807] [INFO] +----+---------+-----+---------+--------+----------+----------------------+
[2026-07-23 16:59:29.807] [INFO] | 1  | Alice   | 25  | active  | true   | beijing  | 2020-04-08T16:00:00Z |
[2026-07-23 16:59:29.807] [INFO] | 2  | Bob     | 30  | active  | true   | shanghai | 1991-08-08T15:00:00Z |
[2026-07-23 16:59:29.807] [INFO] | 3  | Charlie | 20  | pending | false  | chengdu  | 1988-07-11T15:00:00Z |
[2026-07-23 16:59:29.807] [INFO] +----+---------+-----+---------+--------+----------+----------------------+
[2026-07-23 16:59:29.807] [INFO] Total: 3 rows
```

#### 4.2
> LIMIT offset, limit — Skips the specified number of rows (offset) before returning the result (limit). This query skips the first 2 rows and returns the next 3 rows from the users table.

**SQL Code**
```sql
SELECT * FROM users LIMIT  2, 3
```
```log
[2026-07-23 17:00:04.403] [INFO] +----+------+-----+--------+--------+-----------+----------------------+
[2026-07-23 17:00:04.403] [INFO] | id | name | age | status | enable | addr      | birthday             |
[2026-07-23 17:00:04.403] [INFO] +----+------+-----+--------+--------+-----------+----------------------+
[2026-07-23 17:00:04.403] [INFO] | 5  | Eve  | 28  | active | true   | chongqing | 2003-07-11T16:00:00Z |
[2026-07-23 17:00:04.403] [INFO] +----+------+-----+--------+--------+-----------+----------------------+
[2026-07-23 17:00:04.403] [INFO] Total: 1 rows
```

#### 4.3
>ORDER BY ... LIMIT offset, limit — Sorts the result first, then applies the pagination. This query orders users by age ascending and returns the first 3 rows.

**SQL Code**
```sql
SELECT * FROM users order by age asc LIMIT  0, 3
```
```log
[2026-07-23 17:00:32.950] [INFO] +----+---------+-----+---------+--------+-----------+----------------------+
[2026-07-23 17:00:32.950] [INFO] | id | name    | age | status  | enable | addr      | birthday             |
[2026-07-23 17:00:32.950] [INFO] +----+---------+-----+---------+--------+-----------+----------------------+
[2026-07-23 17:00:32.951] [INFO] | 3  | Charlie | 20  | pending | false  | chengdu   | 1988-07-11T15:00:00Z |
[2026-07-23 17:00:32.951] [INFO] | 1  | Alice   | 25  | active  | true   | beijing   | 2020-04-08T16:00:00Z |
[2026-07-23 17:00:32.951] [INFO] | 5  | Eve     | 28  | active  | true   | chongqing | 2003-07-11T16:00:00Z |
[2026-07-23 17:00:32.951] [INFO] +----+---------+-----+---------+--------+-----------+----------------------+
[2026-07-23 17:00:32.951] [INFO] Total: 3 rows
```

### 5. Group by/Having  Query
**Input Data**

| id | name | age | status | enable | addr | birthday |
|----|------|-----|--------|--------|------|----------|
| 1 | Alice | 25 | active | true | beijing | 2020-04-09 |
| 2 | Bob | 30 | active | true | shanghai | 1991-08-09 |
| 3 | Charlie | 20 | pending | false | chengdu | 1988-07-12 |
| 4 | David | 35 | inactive | true | xian | 1955-11-29 |
| 5 | Eve | 28 | active | true | chongqing | 2003-07-12 |
| 6 | Martin | 30 | active | true | guangzhou | 1978-06-30 |
| 7 | Davila | 39 | active | true | null | 1999-06-30 |


#### 5.1
>GROUP BY column, aggregate_function(column) — Groups rows by a specified column and applies aggregate functions (COUNT, AVG, SUM, MIN, MAX) on each group, combined with ORDER BY. This query groups users by status, calculates the count and average age per group, and orders the results by status.

**SQL Code**
```sql
SELECT status, COUNT(*) as count, AVG(age) as avg_age FROM users GROUP BY status ORDER BY status
```
```log
[2026-07-23 17:02:47.779] [INFO] +----------+-------+---------+
[2026-07-23 17:02:47.780] [INFO] | status   | count | avg_age |
[2026-07-23 17:02:47.780] [INFO] +----------+-------+---------+
[2026-07-23 17:02:47.780] [INFO] | active   | 5     | 30.4    |
[2026-07-23 17:02:47.780] [INFO] | inactive | 1     | 35.0    |
[2026-07-23 17:02:47.780] [INFO] | pending  | 1     | 20.0    |
[2026-07-23 17:02:47.780] [INFO] +----------+-------+---------+
[2026-07-23 17:02:47.780] [INFO] Total: 3 rows
```
#### 5.2
>GROUP BY ... HAVING condition ORDER BY ... — Groups rows, filters groups using HAVING (with aggregate functions),
> and sorts results. This query groups users by status, counts and averages ages per group, keeps only
> groups with more than 1 user, and orders by count descending.

**SQL Code**
```sql
 SELECT status, COUNT(age) as count, AVG(age) as avg_age FROM users GROUP BY status HAVING COUNT(age) >1 ORDER BY count DESC
```
```log
[2026-07-23 17:04:55.056] [INFO] +--------+-------+---------+
[2026-07-23 17:04:55.056] [INFO] | status | count | avg_age |
[2026-07-23 17:04:55.056] [INFO] +--------+-------+---------+
[2026-07-23 17:04:55.056] [INFO] | active | 5     | 30.4    |
[2026-07-23 17:04:55.056] [INFO] +--------+-------+---------+
[2026-07-23 17:04:55.056] [INFO] Total: 1 rows
```

### 6. JOIN (INNER/LEFT/RIGHT/FULL/CROSS JOIN)  Query
**Input Data**

#### users 表

| id | name | age | status | enable | addr | birthday |
|----|------|-----|--------|--------|------|----------|
| 1 | Alice | 25 | active | true | beijing | 2020-04-09 |
| 2 | Bob | 30 | active | true | shanghai | 1991-08-09 |
| 3 | Charlie | 20 | pending | false | chengdu | 1988-07-12 |
| 4 | David | 35 | inactive | true | xian | 1955-11-29 |
| 5 | Eve | 28 | active | true | chongqing | 2003-07-12 |
| 6 | Martin | 30 | active | true | guangzhou | 1978-06-30 |
| 7 | Davila | 39 | active | true | null | 1999-06-30 |

#### orders 表

| id | user_id    | amount   |
|----|------------|----------|
| 101 | 1          | 100.0    |
| 102 | 1          | 200.0    |
| 103 | 2          | 150.0    |
| 104 | 3          | 300.0    |


#### 6.1
>INNER JOIN table ON condition — Returns only rows with matching keys in both tables. This query returns users who have orders, along with their order details.

**SQL Code**
```sql
SELECT u.id, u.name, u.age, o.id as order_id, o.amount FROM users u INNER JOIN orders o ON u.id = o.user_id
```
```log
[2026-07-23 22:26:39.908] [INFO] +------+---------+-------+----------+----------+
[2026-07-23 22:26:39.909] [INFO] | u.id | u.name  | u.age | order_id | o.amount |
[2026-07-23 22:26:39.909] [INFO] +------+---------+-------+----------+----------+
[2026-07-23 22:26:39.909] [INFO] | 1    | Alice   | 25    | 1        | 100.0    |
[2026-07-23 22:26:39.909] [INFO] | 1    | Alice   | 25    | 1        | 200.0    |
[2026-07-23 22:26:39.909] [INFO] | 2    | Bob     | 30    | 2        | 150.0    |
[2026-07-23 22:26:39.910] [INFO] | 3    | Charlie | 20    | 3        | 300.0    |
[2026-07-23 22:26:39.910] [INFO] +------+---------+-------+----------+----------+
[2026-07-23 22:26:39.910] [INFO] Total: 4 rows
```
#### 6.2
>INNER JOIN ... ON condition WHERE condition — Combines rows from multiple tables based on a matching condition, then applies additional filters. This query returns active users with orders of at least 150.

**SQL Code**
```sql
 SELECT u.name, u.age, u.status, o.id as order_id, o.amount FROM users u INNER JOIN orders o ON u.id = o.user_id WHERE u.status = 'active' AND o.amount >= 150

```
```log
[2026-07-23 22:27:57.249] [INFO] +--------+-------+----------+----------+----------+
[2026-07-23 22:27:57.249] [INFO] | u.name | u.age | u.status | order_id | o.amount |
[2026-07-23 22:27:57.249] [INFO] +--------+-------+----------+----------+----------+
[2026-07-23 22:27:57.250] [INFO] | Alice  | 25    | active   | 1        | 200.0    |
[2026-07-23 22:27:57.250] [INFO] | Bob    | 30    | active   | 2        | 150.0    |
[2026-07-23 22:27:57.250] [INFO] +--------+-------+----------+----------+----------+
[2026-07-23 22:27:57.251] [INFO] Total: 2 rows
```

#### 6.3
>LEFT JOIN table ON condition — Returns all rows from the left table, with matching rows from the right table (or NULL if no match). This query returns all users, along with their orders if they exist.

**SQL Code**
```sql
 SELECT u.id, u.name, u.age, o.id as order_id, o.amount FROM users u LEFT JOIN orders o ON u.id = o.user_id

```
```log
[2026-07-23 22:28:58.433] [INFO] +------+---------+-------+----------+----------+
[2026-07-23 22:28:58.434] [INFO] | u.id | u.name  | u.age | order_id | o.amount |
[2026-07-23 22:28:58.434] [INFO] +------+---------+-------+----------+----------+
[2026-07-23 22:28:58.434] [INFO] | 1    | Alice   | 25    | 1        | 100.0    |
[2026-07-23 22:28:58.434] [INFO] | 1    | Alice   | 25    | 1        | 200.0    |
[2026-07-23 22:28:58.434] [INFO] | 2    | Bob     | 30    | 2        | 150.0    |
[2026-07-23 22:28:58.435] [INFO] | 3    | Charlie | 20    | 3        | 300.0    |
[2026-07-23 22:28:58.435] [INFO] | 4    | David   | 35    | 4        | null     |
[2026-07-23 22:28:58.435] [INFO] | 5    | Eve     | 28    | 5        | null     |
[2026-07-23 22:28:58.435] [INFO] | 6    | Martin  | 30    | 6        | null     |
[2026-07-23 22:28:58.435] [INFO] | 7    | Davila  | 39    | 7        | null     |
[2026-07-23 22:28:58.435] [INFO] +------+---------+-------+----------+----------+
[2026-07-23 22:28:58.436] [INFO] Total: 8 rows
```

#### 6.4
>RIGHT JOIN table ON condition — Returns all rows from the right table, with matching rows from the left table (or NULL if no match). This query returns all orders, along with user details if they exist.

**SQL Code**
```sql
 SELECT u.id, u.name, u.age, o.id as order_id, o.amount FROM users u RIGHT JOIN orders o ON u.id = o.user_id

```
```log
[2026-07-23 22:30:04.935] [INFO] +------+---------+-------+----------+----------+
[2026-07-23 22:30:04.935] [INFO] | u.id | u.name  | u.age | order_id | o.amount |
[2026-07-23 22:30:04.935] [INFO] +------+---------+-------+----------+----------+
[2026-07-23 22:30:04.935] [INFO] | 1    | Alice   | 25    | 1        | 100.0    |
[2026-07-23 22:30:04.935] [INFO] | 1    | Alice   | 25    | 1        | 200.0    |
[2026-07-23 22:30:04.935] [INFO] | 2    | Bob     | 30    | 2        | 150.0    |
[2026-07-23 22:30:04.935] [INFO] | 3    | Charlie | 20    | 3        | 300.0    |
[2026-07-23 22:30:04.936] [INFO] +------+---------+-------+----------+----------+
[2026-07-23 22:30:04.936] [INFO] Total: 4 rows
```

#### 6.5
>FULL JOIN table ON condition — Returns all rows from both tables, with matching rows joined and NULL for non-matching sides. This query returns all users and all orders, matching them where a relationship exists.

**SQL Code**
```sql
 SELECT u.id, u.name, u.age, o.id as order_id, o.amount FROM users u FULL JOIN orders o ON u.id = o.user_id

```
```log
[2026-07-23 22:31:13.677] [INFO] +------+---------+-------+----------+----------+
[2026-07-23 22:31:13.679] [INFO] | u.id | u.name  | u.age | order_id | o.amount |
[2026-07-23 22:31:13.679] [INFO] +------+---------+-------+----------+----------+
[2026-07-23 22:31:13.679] [INFO] | 1    | Alice   | 25    | 1        | 100.0    |
[2026-07-23 22:31:13.680] [INFO] | 1    | Alice   | 25    | 1        | 200.0    |
[2026-07-23 22:31:13.680] [INFO] | 2    | Bob     | 30    | 2        | 150.0    |
[2026-07-23 22:31:13.680] [INFO] | 3    | Charlie | 20    | 3        | 300.0    |
[2026-07-23 22:31:13.681] [INFO] | 4    | David   | 35    | 4        | null     |
[2026-07-23 22:31:13.681] [INFO] | 5    | Eve     | 28    | 5        | null     |
[2026-07-23 22:31:13.681] [INFO] | 6    | Martin  | 30    | 6        | null     |
[2026-07-23 22:31:13.682] [INFO] | 7    | Davila  | 39    | 7        | null     |
[2026-07-23 22:31:13.682] [INFO] +------+---------+-------+----------+----------+
[2026-07-23 22:31:13.682] [INFO] Total: 8 rows
```
#### 6.6
>FULL JOIN table ON condition — Returns all rows from both tables, with matching rows joined and NULL for non-matching sides. This query returns all users and all orders, matching them where a relationship exists.

**SQL Code**
```sql
SELECT u.id, u.name, u.age, o.id as order_id, o.amount FROM users u FULL JOIN orders o ON u.id = o.user_id

```
```log
[2026-07-24 08:49:12.518] [INFO] +------+---------+-------+----------+----------+
[2026-07-24 08:49:12.518] [INFO] | u.id | u.name  | u.age | order_id | o.amount |
[2026-07-24 08:49:12.518] [INFO] +------+---------+-------+----------+----------+
[2026-07-24 08:49:12.518] [INFO] | 1    | Alice   | 25    | 1        | 100.0    |
[2026-07-24 08:49:12.519] [INFO] | 1    | Alice   | 25    | 1        | 200.0    |
[2026-07-24 08:49:12.519] [INFO] | 2    | Bob     | 30    | 2        | 150.0    |
[2026-07-24 08:49:12.519] [INFO] | 3    | Charlie | 20    | 3        | 300.0    |
[2026-07-24 08:49:12.519] [INFO] | 4    | David   | 35    | 4        | null     |
[2026-07-24 08:49:12.519] [INFO] | 5    | Eve     | 28    | 5        | null     |
[2026-07-24 08:49:12.519] [INFO] | 6    | Martin  | 30    | 6        | null     |
[2026-07-24 08:49:12.519] [INFO] | 7    | Davila  | 39    | 7        | null     |
[2026-07-24 08:49:12.519] [INFO] +------+---------+-------+----------+----------+
[2026-07-24 08:49:12.519] [INFO] Total: 8 rows
```

#### 6.7
>CROSS JOIN table — Returns the Cartesian product of both tables (all row combinations). This query pairs every user with every order.

**SQL Code**
```sql
SELECT u.name, u.age, o.id, o.amount FROM users u CROSS JOIN orders o

```
```log
[2026-07-24 08:53:28.324] [INFO] +---------+-------+------+----------+
[2026-07-24 08:53:28.324] [INFO] | u.name  | u.age | o.id | o.amount |
[2026-07-24 08:53:28.324] [INFO] +---------+-------+------+----------+
[2026-07-24 08:53:28.324] [INFO] | Alice   | 25    | 101  | 100.0    |
[2026-07-24 08:53:28.324] [INFO] | Alice   | 25    | 102  | 200.0    |
[2026-07-24 08:53:28.326] [INFO] | Alice   | 25    | 103  | 150.0    |
[2026-07-24 08:53:28.326] [INFO] | Alice   | 25    | 104  | 300.0    |
[2026-07-24 08:53:28.326] [INFO] | Bob     | 30    | 101  | 100.0    |
[2026-07-24 08:53:28.326] [INFO] | Bob     | 30    | 102  | 200.0    |
[2026-07-24 08:53:28.326] [INFO] | Bob     | 30    | 103  | 150.0    |
[2026-07-24 08:53:28.326] [INFO] | Bob     | 30    | 104  | 300.0    |
[2026-07-24 08:53:28.326] [INFO] | Charlie | 20    | 101  | 100.0    |
[2026-07-24 08:53:28.326] [INFO] | Charlie | 20    | 102  | 200.0    |
[2026-07-24 08:53:28.326] [INFO] | Charlie | 20    | 103  | 150.0    |
[2026-07-24 08:53:28.326] [INFO] | Charlie | 20    | 104  | 300.0    |
[2026-07-24 08:53:28.326] [INFO] | David   | 35    | 101  | 100.0    |
[2026-07-24 08:53:28.326] [INFO] | David   | 35    | 102  | 200.0    |
[2026-07-24 08:53:28.326] [INFO] | David   | 35    | 103  | 150.0    |
[2026-07-24 08:53:28.326] [INFO] | David   | 35    | 104  | 300.0    |
[2026-07-24 08:53:28.326] [INFO] | Eve     | 28    | 101  | 100.0    |
[2026-07-24 08:53:28.326] [INFO] | Eve     | 28    | 102  | 200.0    |
[2026-07-24 08:53:28.326] [INFO] | Eve     | 28    | 103  | 150.0    |
[2026-07-24 08:53:28.326] [INFO] | Eve     | 28    | 104  | 300.0    |
[2026-07-24 08:53:28.326] [INFO] | Martin  | 30    | 101  | 100.0    |
[2026-07-24 08:53:28.326] [INFO] | Martin  | 30    | 102  | 200.0    |
[2026-07-24 08:53:28.326] [INFO] | Martin  | 30    | 103  | 150.0    |
[2026-07-24 08:53:28.326] [INFO] | Martin  | 30    | 104  | 300.0    |
[2026-07-24 08:53:28.328] [INFO] | Davila  | 39    | 101  | 100.0    |
[2026-07-24 08:53:28.328] [INFO] | Davila  | 39    | 102  | 200.0    |
[2026-07-24 08:53:28.328] [INFO] | Davila  | 39    | 103  | 150.0    |
[2026-07-24 08:53:28.328] [INFO] | Davila  | 39    | 104  | 300.0    |
[2026-07-24 08:53:28.328] [INFO] +---------+-------+------+----------+
[2026-07-24 08:53:28.328] [INFO] Total: 28 rows
```

#### 6.8
>NATURAL JOIN table — Automatically joins tables on columns with the same name. This query joins users and orders on all common column names (e.g., id), returning only rows with matching values.

**SQL Code**
```sql
SELECT u.name, u.age, o.id, o.amount FROM users u NATURAL JOIN orders o

```
```log
[2026-07-24 14:02:03.903] [INFO] +--------+-------+------+----------+
[2026-07-24 14:02:03.903] [INFO] | u.name | u.age | o.id | o.amount |
[2026-07-24 14:02:03.903] [INFO] +--------+-------+------+----------+
[2026-07-24 14:02:03.903] [INFO] +--------+-------+------+----------+
[2026-07-24 14:02:03.903] [INFO] Total: 0 rows
```

#### 7 UNION/ MINUS/INTERSECT Query
##### 7.1
>SELECT ... UNION SELECT ... — Combines results from two queries and removes duplicates. This query returns users older than 25 or with status 'active'.

**SQL Code**
```sql
SELECT name, age, status FROM users WHERE age > 25 
UNION 
SELECT name, age, status FROM users WHERE status = 'active'
```
```log
[2026-07-26 12:59:08.817] [INFO] +-------+-----+----------+
[2026-07-26 12:59:08.819] [INFO] | name  | age | status   |
[2026-07-26 12:59:08.819] [INFO] +-------+-----+----------+
[2026-07-26 12:59:08.823] [INFO] | Bob   | 30  | active   |
[2026-07-26 12:59:08.823] [INFO] | David | 35  | inactive |
[2026-07-26 12:59:08.824] [INFO] | Eve   | 28  | active   |
[2026-07-26 12:59:08.824] [INFO] | Frank | 30  | pending  |
[2026-07-26 12:59:08.825] [INFO] | Alice | 32  | pending  |
[2026-07-26 12:59:08.825] [INFO] | Alice | 25  | active   |
[2026-07-26 12:59:08.825] [INFO] +-------+-----+----------+
[2026-07-26 12:59:08.825] [INFO] Total: 6 rows
```
##### 7.2
>SELECT ... MINUS SELECT ... — Returns rows from the first query that are not present in the second query. This query returns users aged 25 or older who are not active.

**SQL Code**
```sql
SELECT name, age, status FROM users WHERE age >= 25 
    MINUS                                
SELECT name, age, status FROM users WHERE status = 'active'
```
```log
[2026-07-26 13:03:04.755] [INFO] +-------+-----+----------+
[2026-07-26 13:03:04.755] [INFO] | name  | age | status   |
[2026-07-26 13:03:04.755] [INFO] +-------+-----+----------+
[2026-07-26 13:03:04.755] [INFO] | David | 35  | inactive |
[2026-07-26 13:03:04.755] [INFO] | Frank | 30  | pending  |
[2026-07-26 13:03:04.755] [INFO] | Alice | 32  | pending  |
[2026-07-26 13:03:04.755] [INFO] +-------+-----+----------+
[2026-07-26 13:03:04.755] [INFO] Total: 3 rows
```

##### 7.3
>SELECT ... INTERSECT SELECT ... — Returns rows that are present in both queries. This query returns users who are both aged 25 or older and have status 'active'.

**SQL Code**
```sql
SELECT name, age, status FROM users WHERE age >= 25 
   INTERSECT 
SELECT name, age, status FROM users WHERE status = 'active'
```
```log
[2026-07-26 13:09:45.576] [INFO] +-------+-----+--------+
[2026-07-26 13:09:45.577] [INFO] | name  | age | status |
[2026-07-26 13:09:45.578] [INFO] +-------+-----+--------+
[2026-07-26 13:09:45.580] [INFO] | Alice | 25  | active |
[2026-07-26 13:09:45.581] [INFO] | Bob   | 30  | active |
[2026-07-26 13:09:45.582] [INFO] | Eve   | 28  | active |
[2026-07-26 13:09:45.582] [INFO] +-------+-----+--------+
[2026-07-26 13:09:45.583] [INFO] Total: 3 rows
```
### 8. JAggregation (COUNT/SUM/AVG/MIN/MAX)
**Input Data**

#### users 表
| id | name | age | status | enable | addr | birthday | salary |
|----|------|-----|--------|--------|------|----------|--------|
| 1 | Alice | 25 | active | true | beijing | 2020-04-09 | 5000.0 |
| 2 | Bob | 30 | active | true | shanghai | 1991-08-09 | 6000.0 |
| 3 | Charlie | 20 | pending | false | chengdu | 1988-07-12 | 4500.0 |
| 4 | David | 35 | inactive | true | xian | 1955-11-29 | 7000.0 |
| 5 | Eve | 28 | active | true | chongqing | 2003-07-12 | 5500.0 |
| 6 | Martin | 30 | active | true | guangzhou | 1978-06-30 | 6500.0 |
| 7 | Davila | 39 | active | true | null | 1999-06-30 | 8000.0 |




#### 8.1
>GROUP BY column — Groups rows by a specified column and applies aggregate functions like COUNT. This query groups users by status and returns the count for each group.

**SQL Code**
```sql
SELECT status, COUNT(*) AS count FROM users GROUP BY status
```
```log
    [2026-07-27 07:03:18.201] [INFO] +----------+-------+
    [2026-07-27 07:03:18.201] [INFO] | status   | count |
    [2026-07-27 07:03:18.201] [INFO] +----------+-------+
    [2026-07-27 07:03:18.201] [INFO] | inactive | 1     |
    [2026-07-27 07:03:18.201] [INFO] | pending  | 1     |
    [2026-07-27 07:03:18.201] [INFO] | active   | 5     |
    [2026-07-27 07:03:18.201] [INFO] +----------+-------+
    [2026-07-27 07:03:18.201] [INFO] Total: 3 rows
```

#### 8.2
>GROUP BY column, SUM(column) — Groups rows by a specified column and calculates the sum of another column for each group. This query groups users by status and returns the total age for each group.

**SQL Code**
```sql
SELECT status, SUM(age) AS age_sum FROM users GROUP BY status
```
```log
    [2026-07-27 07:07:19.828] [INFO] +----------+---------+
    [2026-07-27 07:07:19.828] [INFO] | status   | age_sum |
    [2026-07-27 07:07:19.830] [INFO] +----------+---------+
    [2026-07-27 07:07:19.830] [INFO] | inactive | 35.0    |
    [2026-07-27 07:07:19.830] [INFO] | pending  | 20.0    |
    [2026-07-27 07:07:19.830] [INFO] | active   | 152.0   |
    [2026-07-27 07:07:19.830] [INFO] +----------+---------+
    [2026-07-27 07:07:19.830] [INFO] Total: 3 rows
```


#### 8.3
>GROUP BY column, AVG(column) — Groups rows by a specified column and calculates the average value of another column for each group. This query groups users by status and returns the average age for each group.

**SQL Code**
```sql
SELECT status,AVG(age) AS avg_age FROM users GROUP BY status
```
```log
[2026-07-27 07:10:15.850] [INFO] +----------+---------+
[2026-07-27 07:10:15.850] [INFO] | status   | avg_age |
[2026-07-27 07:10:15.850] [INFO] +----------+---------+
[2026-07-27 07:10:15.850] [INFO] | inactive | 35.0    |
[2026-07-27 07:10:15.850] [INFO] | pending  | 20.0    |
[2026-07-27 07:10:15.850] [INFO] | active   | 30.4    |
[2026-07-27 07:10:15.850] [INFO] +----------+---------+
[2026-07-27 07:10:15.851] [INFO] Total: 3 rows
```

#### 8.4
>GROUP BY column, MIN(column) — Groups rows by a specified column and finds the minimum value of another column for each group. This query groups users by status and returns the minimum age for each group.

**SQL Code**
```sql
SELECT status,MIN(age) AS active_min_age FROM users GROUP BY status
```
```log
[2026-07-27 07:12:58.509] [INFO] +----------+----------------+
[2026-07-27 07:12:58.509] [INFO] | status   | active_min_age |
[2026-07-27 07:12:58.509] [INFO] +----------+----------------+
[2026-07-27 07:12:58.509] [INFO] | inactive | 35             |
[2026-07-27 07:12:58.511] [INFO] | pending  | 20             |
[2026-07-27 07:12:58.511] [INFO] | active   | 25             |
[2026-07-27 07:12:58.512] [INFO] +----------+----------------+
[2026-07-27 07:12:58.512] [INFO] Total: 3 rows
```
#### 8.5
>GROUP BY column, MAX(column) — Groups rows by a specified column and finds the maximum value of another column for each group. This query groups users by status and returns the maximum age for each group.

**SQL Code**
```sql
SELECT status,Max(age) AS active_max_age FROM users GROUP BY status
```
```log
[2026-07-27 07:14:15.781] [INFO] +----------+----------------+
[2026-07-27 07:14:15.782] [INFO] | status   | active_max_age |
[2026-07-27 07:14:15.782] [INFO] +----------+----------------+
[2026-07-27 07:14:15.782] [INFO] | inactive | 35             |
[2026-07-27 07:14:15.782] [INFO] | pending  | 20             |
[2026-07-27 07:14:15.782] [INFO] | active   | 39             |
[2026-07-27 07:14:15.782] [INFO] +----------+----------------+
[2026-07-27 07:14:15.782] [INFO] Total: 3 rows
```


### 9. Subquery
**Input Data**

#### users 表
## users 表

| id | name | age | status | enable | addr | birthday | department_id |
|----|------|-----|--------|--------|------|----------|---------------|
| 1 | Alice | 25 | active | true | beijing | 2020-04-09 | 1 |
| 2 | Bob | 30 | active | true | shanghai | 1991-08-09 | 2 |
| 3 | Charlie | 20 | pending | false | chengdu | 1988-07-12 | 1 |
| 4 | David | 35 | inactive | true | xian | 1955-11-29 | 3 |
| 5 | Eve | 28 | active | true | chongqing | 2003-07-12 | 2 |
| 6 | Martin | 30 | active | true | guangzhou | 1978-06-30 | 3 |
| 7 | Davila | 39 | active | true | null | 1999-06-30 | 1 |

## departments 表

| dept_id | dept_name | location | budget |
|---------|-----------|----------|--------|
| 1 | Engineering | Building A | 500000.0 |
| 2 | Marketing | Building B | 300000.0 |
| 3 | Finance | Building C | 400000.0 |
| 4 | HR | Building D | 200000.0 |

## orders 表

| order_id | user_id | amount | order_date |
|----------|---------|--------|------------|
| 101 | 1 | 150.50 | 2024-01-15 |
| 102 | 2 | 200.00 | 2024-01-16 |
| 103 | 1 | 75.25 | 2024-01-17 |
| 104 | 3 | 300.00 | 2024-01-18 |
| 105 | 5 | 120.00 | 2024-01-19 |
| 106 | 2 | 450.50 | 2024-01-20 |


#### 9.1
>

**SQL Code**
```sql
SELECT * FROM users " + "WHERE age > (SELECT AVG(age) FROM users)
```
```log
[2026-07-31 16:08:24.517] [INFO] +----+--------+-----+----------+--------+-----------+----------------------+---------------+
[2026-07-31 16:08:24.517] [INFO] | id | name   | age | status   | enable | addr      | birthday             | department_id |
[2026-07-31 16:08:24.517] [INFO] +----+--------+-----+----------+--------+-----------+----------------------+---------------+
[2026-07-31 16:08:24.517] [INFO] | 2  | Bob    | 30  | active   | true   | shanghai  | 1991-08-08T15:00:00Z | 2             |
[2026-07-31 16:08:24.518] [INFO] | 4  | David  | 35  | inactive | true   | xian      | 1955-11-28T16:00:00Z | 3             |
[2026-07-31 16:08:24.518] [INFO] | 6  | Martin | 30  | active   | true   | guangzhou | 1978-06-29T16:00:00Z | 3             |
[2026-07-31 16:08:24.519] [INFO] | 7  | Davila | 39  | active   | true   | null      | 1999-06-29T16:00:00Z | 1             |
[2026-07-31 16:08:24.519] [INFO] +----+--------+-----+----------+--------+-----------+----------------------+---------------+
[2026-07-31 16:08:24.519] [INFO] Total: 4 rows
```

#### 9.2
>

**SQL Code**
```sql
SELECT * FROM users WHERE department_id IN (SELECT dept_id FROM departments WHERE dept_name IN ('Engineering', 'Marketing'))
```
```log
[2026-07-31 16:09:51.077] [INFO] +----+---------+-----+---------+--------+-----------+----------------------+---------------+
[2026-07-31 16:09:51.077] [INFO] | id | name    | age | status  | enable | addr      | birthday             | department_id |
[2026-07-31 16:09:51.077] [INFO] +----+---------+-----+---------+--------+-----------+----------------------+---------------+
[2026-07-31 16:09:51.077] [INFO] | 1  | Alice   | 25  | active  | true   | beijing   | 2020-04-08T16:00:00Z | 1             |
[2026-07-31 16:09:51.077] [INFO] | 2  | Bob     | 30  | active  | true   | shanghai  | 1991-08-08T15:00:00Z | 2             |
[2026-07-31 16:09:51.077] [INFO] | 3  | Charlie | 20  | pending | false  | chengdu   | 1988-07-11T15:00:00Z | 1             |
[2026-07-31 16:09:51.078] [INFO] | 5  | Eve     | 28  | active  | true   | chongqing | 2003-07-11T16:00:00Z | 2             |
[2026-07-31 16:09:51.078] [INFO] | 7  | Davila  | 39  | active  | true   | null      | 1999-06-29T16:00:00Z | 1             |
[2026-07-31 16:09:51.078] [INFO] +----+---------+-----+---------+--------+-----------+----------------------+---------------+
[2026-07-31 16:09:51.078] [INFO] Total: 5 rows
```

#### 9.3
>

**SQL Code**
```sql
SELECT * FROM users u WHERE EXISTS (SELECT 1 FROM orders o WHERE o.user_id = u.id)
```
```log
[2026-07-31 16:11:20.306] [INFO] +----+---------+-----+---------+--------+-----------+----------------------+---------------+
[2026-07-31 16:11:20.306] [INFO] | id | name    | age | status  | enable | addr      | birthday             | department_id |
[2026-07-31 16:11:20.306] [INFO] +----+---------+-----+---------+--------+-----------+----------------------+---------------+
[2026-07-31 16:11:20.306] [INFO] | 1  | Alice   | 25  | active  | true   | beijing   | 2020-04-08T16:00:00Z | 1             |
[2026-07-31 16:11:20.306] [INFO] | 2  | Bob     | 30  | active  | true   | shanghai  | 1991-08-08T15:00:00Z | 2             |
[2026-07-31 16:11:20.306] [INFO] | 3  | Charlie | 20  | pending | false  | chengdu   | 1988-07-11T15:00:00Z | 1             |
[2026-07-31 16:11:20.306] [INFO] | 5  | Eve     | 28  | active  | true   | chongqing | 2003-07-11T16:00:00Z | 2             |
[2026-07-31 16:11:20.306] [INFO] +----+---------+-----+---------+--------+-----------+----------------------+---------------+
[2026-07-31 16:11:20.307] [INFO] Total: 4 rows
```

#### 9.4
>

**SQL Code**
```sql
SELECT * FROM users u WHERE EXISTS (SELECT 1 FROM orders o WHERE o.user_id = u.id)
```
```log
[2026-07-31 16:11:20.306] [INFO] +----+---------+-----+---------+--------+-----------+----------------------+---------------+
[2026-07-31 16:11:20.306] [INFO] | id | name    | age | status  | enable | addr      | birthday             | department_id |
[2026-07-31 16:11:20.306] [INFO] +----+---------+-----+---------+--------+-----------+----------------------+---------------+
[2026-07-31 16:11:20.306] [INFO] | 1  | Alice   | 25  | active  | true   | beijing   | 2020-04-08T16:00:00Z | 1             |
[2026-07-31 16:11:20.306] [INFO] | 2  | Bob     | 30  | active  | true   | shanghai  | 1991-08-08T15:00:00Z | 2             |
[2026-07-31 16:11:20.306] [INFO] | 3  | Charlie | 20  | pending | false  | chengdu   | 1988-07-11T15:00:00Z | 1             |
[2026-07-31 16:11:20.306] [INFO] | 5  | Eve     | 28  | active  | true   | chongqing | 2003-07-11T16:00:00Z | 2             |
[2026-07-31 16:11:20.306] [INFO] +----+---------+-----+---------+--------+-----------+----------------------+---------------+
[2026-07-31 16:11:20.307] [INFO] Total: 4 rows
```
#### 9.5
>

**SQL Code**
```sql
SELECT u.name, u.age, (SELECT dept_name FROM departments d WHERE d.dept_id = u.department_id) as dept_name, (SELECT budget FROM departments d WHERE d.dept_id = u.department_id) as dept_budget FROM users u
```
```log
[2026-07-31 16:17:14.396] [INFO] | name    | age | dept_name   | dept_budget |
[2026-07-31 16:17:14.396] [INFO] +---------+-----+-------------+-------------+
[2026-07-31 16:17:14.396] [INFO] | Alice   | 25  | Engineering | 500000.0    |
[2026-07-31 16:17:14.396] [INFO] | Bob     | 30  | Marketing   | 300000.0    |
[2026-07-31 16:17:14.396] [INFO] | Charlie | 20  | Engineering | 500000.0    |
[2026-07-31 16:17:14.396] [INFO] | David   | 35  | Finance     | 400000.0    |
[2026-07-31 16:17:14.397] [INFO] | Eve     | 28  | Marketing   | 300000.0    |
[2026-07-31 16:17:14.397] [INFO] | Martin  | 30  | Finance     | 400000.0    |
[2026-07-31 16:17:14.397] [INFO] | Davila  | 39  | Engineering | 500000.0    |
[2026-07-31 16:17:14.397] [INFO] +---------+-----+-------------+-------------+
[2026-07-31 16:17:14.397] [INFO] Total: 7 rows
```
#### 9.6
>

**SQL Code**
```sql
 SELECT u.name, u.age, u.department_id FROM users u ORDER BY (SELECT budget FROM departments d WHERE d.dept_id = u.department_id) DESC, u.age
```
```log
[2026-07-31 16:19:35.492] [INFO] +---------+--------------+
[2026-07-31 16:19:35.492] [INFO] | user_id | total_amount |
[2026-07-31 16:19:35.492] [INFO] +---------+--------------+
[2026-07-31 16:19:35.492] [INFO] | 1       | 225.75       |
[2026-07-31 16:19:35.492] [INFO] | 2       | 650.5        |
[2026-07-31 16:19:35.492] [INFO] | 3       | 300.0        |
[2026-07-31 16:19:35.492] [INFO] +---------+--------------+
[2026-07-31 16:19:35.492] [INFO] Total: 3 rows
```

#### 9.7
>

**SQL Code**
```sql
SELECT user_id, SUM(amount) as total_amount FROM orders GROUP BY user_id HAVING SUM(amount) > (SELECT AVG(amount) FROM orders)
```
```log
[2026-07-31 16:20:38.719] [INFO] +---------+-----+---------------+
[2026-07-31 16:20:38.719] [INFO] | name    | age | department_id |
[2026-07-31 16:20:38.719] [INFO] +---------+-----+---------------+
[2026-07-31 16:20:38.719] [INFO] | Charlie | 20  | 1             |
[2026-07-31 16:20:38.719] [INFO] | Alice   | 25  | 1             |
[2026-07-31 16:20:38.720] [INFO] | Eve     | 28  | 2             |
[2026-07-31 16:20:38.720] [INFO] | Bob     | 30  | 2             |
[2026-07-31 16:20:38.720] [INFO] | Martin  | 30  | 3             |
[2026-07-31 16:20:38.720] [INFO] | David   | 35  | 3             |
[2026-07-31 16:20:38.720] [INFO] | Davila  | 39  | 1             |
[2026-07-31 16:20:38.720] [INFO] +---------+-----+---------------+
[2026-07-31 16:20:38.720] [INFO] Total: 7 rows
```

#### 9.8
>

**SQL Code**
```sql
SELECT * FROM users WHERE id IN (    SELECT user_id FROM orders     WHERE amount > (SELECT AVG(amount) FROM orders))
```
```log
[2026-07-31 16:22:25.297] [INFO] +----+---------+-----+---------+--------+----------+----------------------+---------------+
[2026-07-31 16:22:25.298] [INFO] | id | name    | age | status  | enable | addr     | birthday             | department_id |
[2026-07-31 16:22:25.298] [INFO] +----+---------+-----+---------+--------+----------+----------------------+---------------+
[2026-07-31 16:22:25.298] [INFO] | 2  | Bob     | 30  | active  | true   | shanghai | 1991-08-08T15:00:00Z | 2             |
[2026-07-31 16:22:25.298] [INFO] | 3  | Charlie | 20  | pending | false  | chengdu  | 1988-07-11T15:00:00Z | 1             |
[2026-07-31 16:22:25.298] [INFO] +----+---------+-----+---------+--------+----------+----------------------+---------------+
[2026-07-31 16:22:25.298] [INFO] Total: 2 rows
```


#### 9.9
>

**SQL Code**
```sql
SELECT u.name, u.age, CASE     WHEN (SELECT COUNT(*) FROM orders o WHERE o.user_id = u.id) > 2 THEN 'High Volume'     WHEN (SELECT COUNT(*) FROM orders o WHERE o.user_id = u.id) > 0 THEN 'Normal'     ELSE 'No Orders' END as order_volume FROM users u
```
```log
[2026-07-31 16:23:27.211] [INFO] +---------+-----+--------------+
[2026-07-31 16:23:27.213] [INFO] | name    | age | order_volume |
[2026-07-31 16:23:27.213] [INFO] +---------+-----+--------------+
[2026-07-31 16:23:27.213] [INFO] | Alice   | 25  | Normal       |
[2026-07-31 16:23:27.213] [INFO] | Bob     | 30  | Normal       |
[2026-07-31 16:23:27.213] [INFO] | Charlie | 20  | Normal       |
[2026-07-31 16:23:27.213] [INFO] | David   | 35  | No Orders    |
[2026-07-31 16:23:27.213] [INFO] | Eve     | 28  | Normal       |
[2026-07-31 16:23:27.213] [INFO] | Martin  | 30  | No Orders    |
[2026-07-31 16:23:27.213] [INFO] | Davila  | 39  | No Orders    |
[2026-07-31 16:23:27.213] [INFO] +---------+-----+--------------+
[2026-07-31 16:23:27.213] [INFO] Total: 7 rows
```
#### 9.10
>

**SQL Code**
```sql
SELECT u.name, u.age, CASE     WHEN (SELECT COUNT(*) FROM orders o WHERE o.user_id = u.id) > 2 THEN 'High Volume'     WHEN (SELECT COUNT(*) FROM orders o WHERE o.user_id = u.id) > 0 THEN 'Normal'     ELSE 'No Orders' END as order_volume FROM users u
```
```log
[2026-07-31 16:23:27.211] [INFO] +---------+-----+--------------+
[2026-07-31 16:23:27.213] [INFO] | name    | age | order_volume |
[2026-07-31 16:23:27.213] [INFO] +---------+-----+--------------+
[2026-07-31 16:23:27.213] [INFO] | Alice   | 25  | Normal       |
[2026-07-31 16:23:27.213] [INFO] | Bob     | 30  | Normal       |
[2026-07-31 16:23:27.213] [INFO] | Charlie | 20  | Normal       |
[2026-07-31 16:23:27.213] [INFO] | David   | 35  | No Orders    |
[2026-07-31 16:23:27.213] [INFO] | Eve     | 28  | Normal       |
[2026-07-31 16:23:27.213] [INFO] | Martin  | 30  | No Orders    |
[2026-07-31 16:23:27.213] [INFO] | Davila  | 39  | No Orders    |
[2026-07-31 16:23:27.213] [INFO] +---------+-----+--------------+
[2026-07-31 16:23:27.213] [INFO] Total: 7 rows
```

#### 9.11
>

**SQL Code**
```sql
SELECT u.name, u.department_id, (SELECT SUM(o.amount) FROM orders o WHERE o.user_id = u.id) as user_total_orders FROM users u WHERE (SELECT SUM(o.amount) FROM orders o WHERE o.user_id = u.id) > (SELECT AVG(budget) FROM departments)
```
```log
+------+---------------+-------------------+
| name | department_id | user_total_orders |
+------+---------------+-------------------+
(empty)
+------+---------------+-------------------+
```


#### 9.12
>

**SQL Code**
```sql
SELECT u.name, u.age, dept_stats.dept_name, dept_stats.avg_age FROM users u left JOIN (    SELECT d.dept_id, d.dept_name, AVG(u2.age) as avg_age     FROM departments d     LEFT JOIN users u2 ON d.dept_id = u2.department_id     GROUP BY d.dept_id, d.dept_name) as dept_stats ON u.department_id = dept_stats.dept_id
```
```log
[2026-07-31 16:27:08.546] [INFO] +---------+-----+-------------+---------+
[2026-07-31 16:27:08.546] [INFO] | name    | age | dept_name   | avg_age |
[2026-07-31 16:27:08.546] [INFO] +---------+-----+-------------+---------+
[2026-07-31 16:27:08.546] [INFO] | Alice   | 25  | Engineering | 28.0    |
[2026-07-31 16:27:08.546] [INFO] | Bob     | 30  | Marketing   | 29.0    |
[2026-07-31 16:27:08.546] [INFO] | Charlie | 20  | Engineering | 28.0    |
[2026-07-31 16:27:08.546] [INFO] | David   | 35  | Finance     | 32.5    |
[2026-07-31 16:27:08.546] [INFO] | Eve     | 28  | Marketing   | 29.0    |
[2026-07-31 16:27:08.546] [INFO] | Martin  | 30  | Finance     | 32.5    |
[2026-07-31 16:27:08.546] [INFO] | Davila  | 39  | Engineering | 28.0    |
[2026-07-31 16:27:08.546] [INFO] +---------+-----+-------------+---------+
[2026-07-31 16:27:08.546] [INFO] Total: 7 rows
```
#### 9.13
>

**SQL Code**
```sql
 SELECT tmp.dept_id, avg_age FROM (SELECT department_id as dept_id, AVG(age) as avg_age FROM users GROUP BY department_id) as tmp WHERE avg_age > 28
```
```log
[2026-07-31 16:30:31.067] [INFO] Global config updated
[2026-07-31 16:30:31.067] [INFO] +---------+---------+
[2026-07-31 16:30:31.067] [INFO] | dept_id | avg_age |
[2026-07-31 16:30:31.068] [INFO] +---------+---------+
[2026-07-31 16:30:31.068] [INFO] | 2       | 29.0    |
[2026-07-31 16:30:31.068] [INFO] | 3       | 32.5    |
[2026-07-31 16:30:31.068] [INFO] +---------+---------+
[2026-07-31 16:30:31.068] [INFO] Total: 2 rows
```



## API Reference

### JQuickSQL

| Method | Description |
|--------|-------------|
| `JQuickSQL.embedded(int parallelism)` | Create embedded SQL engine with specified parallelism |
| `JQuickSQL.builder()` | Create builder for custom configuration |
| `registerTable(String name, List<JQuickColumnMeta> columns, List<JQuickRow> rows)` | Register a table |
| `execute(String sql)` | Execute SQL query |
| `getTable(String name)` | Get registered table data |
| `hasTable(String name)` | Check if table exists |
| `getRegisteredTables()` | Get all registered table names |
| `shutdown()` | Shutdown the engine |

### JQuickSQL Builder

```java
JQuickSQL sql = JQuickSQL.builder()
    .embedded(2)           // Use embedded mode with 2 workers
    .parallelism(2)        // Set parallelism
    .table("products", columns, rows)  // Register table
    .build();
```





## License

Apache License 2.0
