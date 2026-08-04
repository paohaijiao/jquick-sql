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
| XML Confuguration                       | ✅ |

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
 SELECT
	tmp.dept_id,
	avg_age
FROM
	(
	SELECT
		department_id as dept_id,
		AVG(age) as avg_age
	FROM
		users
	GROUP BY
		department_id
		) as tmp
WHERE
	avg_age > 28

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


### 10. Function
> JQuick-SQL's built-in functions are provided by [**jquick-transform-function**](https://github.com/paohaijiao/jquick-transform-function) (200+ functions covering strings, dates, aggregation, encryption, conversion, business validation, etc.) and can be extended with custom functions via **SPI (Service Provider Interface)**. Once registered, custom functions can be used directly in `SELECT`, `WHERE`, `GROUP BY`, `HAVING`, `ORDER BY` and other clauses just like built-in functions, e.g. `jsonPath(detail, '$.position')`.

# Built-in Methods
| No  | Method Name                     | Description                                                                              |
|-----|---------------------------------| ---------------------------------------------------------------------------------------- |
| 1   | isArray                         | Check if it is an array or list - usage: isArray(value)                                |
| 2   | bitAnd                          | Bitwise AND - usage: bitAnd(a, b)                                                     |
| 3   | bitOr                           | Bitwise OR - usage: bitOr(a, b)                                                       |
| 4   | bitXor                          | Bitwise XOR - usage: bitXor(a, b)                                                     |
| 5   | isBoolean                       | Check if it is a boolean - usage: isBoolean(value)                                    |
| 6   | bankCardMask                    | Bank card number mask - usage: bankCardMask(cardNo, keepStart?, keepEnd?)             |
| 7   | bankCardValidate                | Validate whether bank card number is valid (Luhn algorithm) - usage: bankCardValidate(cardNo) |
| 8   | emailMask                       | Email mask - usage: emailMask(email)                                                  |
| 9   | genderName                      | Get gender name - usage: genderName(code) supports: M/F, 1/0, male/female             |
| 10  | idCardAge                       | Calculate age from ID card number - usage: idCardAge(idCard, referenceDate?)          |
| 11  | idCardBirthday                  | Extract birthday from ID card number - usage: idCardBirthday(idCard, pattern?)        |
| 12  | idCardGender                    | Get gender from ID card number - usage: idCardGender(idCard, format?)                 |
| 13  | idCardInfo                      | Extract ID card information - usage: idCardInfo(idCard, field?) field: birthday/age/gender/region |
| 14  | idCardValidate                  | Validate whether ID card number is valid - usage: idCardValidate(idCard)              |
| 15  | phoneInfo                       | Get phone number information - usage: phoneInfo(phone, field?) field: carrier/prefix/location |
| 16  | phoneMask                       | Phone number mask - usage: phoneMask(phone, keepStart?, keepEnd?)                     |
| 17  | phoneValidate                   | Validate whether phone number is valid - usage: phoneValidate(phone)                  |
| 18  | isEmpty                         | Check if object is empty - supports String/Collection/Map/Array                       |
| 19  | join                            | Concatenate collection elements - usage: join(list, delimiter)                        |
| 20  | size                            | Get length of collection, array, Map or string                                        |
| 21  | between                         | Range check - usage: between(value, min, max, inclusive?)                             |
| 22  | caseWhen                        | CASE WHEN conditional expression - usage: caseWhen(condition1, result1, condition2, result2, ..., defaultResult) |
| 23  | coalesce                        | Return the first non-null value - usage: coalesce(value1, value2, ...)                |
| 24  | defaultIfNull                   | Replace null with default value - usage: defaultIfNull(value, defaultValue)           |
| 25  | eq                              | Equality check - usage: eq(a, b, ignoreCase?)                                         |
| 26  | gte                             | Greater than or equal check - usage: gte(a, b)                                        |
| 27  | gt                              | Greater than check - usage: gt(a, b)                                                  |
| 28  | ifElse                          | Multi-condition check - usage: ifElse(condition1, value1, condition2, value2, ..., defaultValue) |
| 29  | if                              | Conditional check - usage: if(condition, trueValue, falseValue)                       |
| 30  | lte                             | Less than or equal check - usage: lte(a, b)                                           |
| 31  | lt                              | Less than check - usage: lt(a, b)                                                     |
| 32  | ne                              | Inequality check - usage: ne(a, b, ignoreCase?)                                       |
| 33  | nvl                             | Null replacement - usage: nvl(value, defaultValue)                                    |
| 34  | switch                          | Multi-branch matching - usage: switch(value, case1, result1, case2, result2, ..., defaultValue) |
| 35  | toBoolean                       | Convert to boolean - usage: toBoolean(value, defaultValue?)                           |
| 36  | toDate                          | Convert to date (LocalDate) - usage: toDate(value, pattern?)                          |
| 37  | toDateTime                      | Convert to datetime (LocalDateTime) - usage: toDateTime(value, pattern?)              |
| 38  | toShort                         | Convert to short integer - usage: toShort(value, defaultValue?)                       |
| 39  | addDays                         | Add days - usage: addDays(date, days)                                                 |
| 40  | addHours                        | Add hours - usage: addHours(datetime, hours)                                          |
| 41  | addMinutes                      | Add minutes - usage: addMinutes(datetime, minutes)                                    |
| 42  | addMonths                       | Add months - usage: addMonths(date, months)                                           |
| 43  | addSeconds                      | Add seconds - usage: addSeconds(datetime, seconds)                                    |
| 44  | addYears                        | Add years - usage: addYears(date, years)                                              |
| 45  | age                             | Calculate age - usage: age(birthDate, referenceDate?)                                 |
| 46  | day                             | Get day of month (1-31) - usage: day(date?)                                           |
| 47  | dayOfWeek                       | Get day of week - usage: dayOfWeek(date?, locale?) returns 1-7 (Mon=1) or name        |
| 48  | dayOfYear                       | Get day of year (1-366) - usage: dayOfYear(date?)                                     |
| 49  | daysBetween                     | Calculate difference in days between two dates - usage: daysBetween(date1, date2)     |
| 50  | endOfDay                        | Get end of day (23:59:59) - usage: endOfDay(date?)                                    |
| 51  | endOfMonth                      | Get last day of month - usage: endOfMonth(date?)                                      |
| 52  | endOfYear                       | Get last day of year - usage: endOfYear(date?)                                        |
| 53  | hour                            | Get hour (0-23) - usage: hour(datetime?)                                              |
| 54  | hoursBetween                    | Calculate difference in hours between two datetimes - usage: hoursBetween(datetime1, datetime2) |
| 55  | isAfter                         | Check if date is after another - usage: isAfter(date1, date2)                         |
| 56  | isBefore                        | Check if date is before another - usage: isBefore(date1, date2)                       |
| 57  | isDate                          | Check if it is a date - usage: isDate(value)                                          |
| 58  | isLeapYear                      | Check if it is a leap year - usage: isLeapYear(year?)                                 |
| 59  | isSameDay                       | Check if two dates are the same day - usage: isSameDay(date1, date2)                  |
| 60  | isWeekend                       | Check if it is a weekend - usage: isWeekend(date?)                                    |
| 61  | minute                          | Get minute (0-59) - usage: minute(datetime?)                                          |
| 62  | month                           | Get month (1-12) - usage: month(date?)                                                |
| 63  | monthsBetween                   | Calculate difference in months between two dates - usage: monthsBetween(date1, date2) |
| 64  | second                          | Get second (0-59) - usage: second(datetime?)                                          |
| 65  | startOfDay                      | Get start of day (00:00:00) - usage: startOfDay(date?)                                |
| 66  | startOfMonth                    | Get first day of month - usage: startOfMonth(date?)                                   |
| 67  | startOfYear                     | Get first day of year - usage: startOfYear(date?)                                     |
| 68  | weekOfYear                      | Get week of year - usage: weekOfYear(date?)                                           |
| 69  | year                            | Get year - usage: year(date?) returns current year if no argument is passed           |
| 70  | yearsBetween                    | Calculate difference in years between two dates - usage: yearsBetween(date1, date2)   |
| 71  | areaCircle                      | Calculate area of circle - usage: areaCircle(radius)                                  |
| 72  | areaRectangle                   | Calculate area of rectangle - usage: areaRectangle(length, width)                     |
| 73  | areaTriangle                    | Calculate area of triangle - usage: areaTriangle(base, height) or areaTriangle(a, b, c) Heron's formula |
| 74  | circumference                   | Calculate circumference of circle - usage: circumference(radius)                      |
| 75  | clamp                           | Clamp value to range - usage: clamp(value, min, max)                                  |
| 76  | combination                     | Calculate combinations C(n,k) - usage: combination(n, k)                              |
| 77  | cross                           | Calculate vector cross product (2D scalar) - usage: cross(x1, y1, x2, y2)             |
| 78  | distance                        | Calculate distance between two points - usage: distance(x1, y1, x2, y2) or distance(x1, y1, z1, x2, y2, z2) |
| 79  | dot                             | Calculate vector dot product - usage: dot(vector1, vector2)                           |
| 80  | factorial                       | Calculate factorial - usage: factorial(n)                                             |
| 81  | fibonacci                       | Calculate Fibonacci number - usage: fibonacci(n)                                      |
| 82  | gcd                             | Calculate greatest common divisor - usage: gcd(a, b, ...)                             |
| 83  | hypot                           | Calculate sqrt(x²+y²) - usage: hypot(x, y)                                            |
| 84  | isPowerOfTwo                    | Check if it is a power of two - usage: isPowerOfTwo(n)                                |
| 85  | isPrime                         | Check if it is a prime number - usage: isPrime(n)                                     |
| 86  | lcm                             | Calculate least common multiple - usage: lcm(a, b, ...)                               |
| 87  | lerp                            | Linear interpolation - usage: lerp(a, b, t) returns a + (b - a) * t                   |
| 88  | map                             | Map value range - usage: map(value, fromLow, fromHigh, toLow, toHigh, clamp?)         |
| 89  | permutation                     | Calculate permutations P(n,k) - usage: permutation(n, k)                              |
| 90  | cast                            | Force type conversion - usage: cast(value, targetClass)                                |
| 91  | formatNumber                    | Format number - usage: formatNumber(number, pattern)                                  |
| 92  | parseNumber                     | Parse formatted number - usage: parseNumber(str, pattern)                             |
| 93  | toArray                         | Convert to array - usage: toArray(value1, value2, ...)                                 |
| 94  | toCurrency                      | Convert to currency format - usage: toCurrency(number, locale?)                       |
| 95  | toList                          | Convert to list - usage: toList(value1, value2, ...)                                  |
| 96  | toPercentage                    | Convert to percentage format - usage: toPercentage(number, decimals?)                 |
| 97  | typeOf                          | Get type name of object - usage: typeOf(value)                                        |
| 98  | abs                             | Absolute value                                                                         |
| 99  | acos                            | Arc cosine - usage: acos(value)                                                        |
| 100 | add                             | Sum                                                                                    |
| 101 | asin                            | Arc sine - usage: asin(value)                                                          |
| 102 | atan                            | Arc tangent - usage: atan(value)                                                       |
| 103 | atan2                           | Coordinate arc tangent - usage: atan2(y, x) returns angle of coordinate (x,y)         |
| 104 | avg                             | Average                                                                                |
| 105 | ceil                            | Round up                                                                               |
| 106 | ceilTo                          | Round up to specified decimal places - usage: ceilTo(value, places)                    |
| 107 | e                               | Get value of natural constant e                                                        |
| 108 | pi                              | Get value of pi                                                                        |
| 109 | cos                             | Cosine - usage: cos(radians)                                                           |
| 110 | cosh                            | Hyperbolic cosine - usage: cosh(value)                                                 |
| 111 | divide                          | Division - usage: divide(a, b, ...) returns a/b/...                                    |
| 112 | exp                             | Exponential e^x - usage: exp(value)                                                    |
| 113 | expm1                           | Exponential e^x - 1 - usage: expm1(value)                                              |
| 114 | floor                           | Round down                                                                             |
| 115 | floorTo                         | Round down to specified decimal places - usage: floorTo(value, places)                 |
| 116 | greatest                        | Return maximum - usage: greatest(value1, value2, ...)                                  |
| 117 | isNumber                        | Check if object is a number type or numeric string                                     |
| 118 | least                           | Return minimum - usage: least(value1, value2, ...)                                     |
| 119 | log                             | Natural logarithm - usage: log(value)                                                  |
| 120 | log10                           | Common logarithm (base 10) - usage: log10(value)                                      |
| 121 | log1p                           | Natural logarithm log(1+x) - usage: log1p(value)                                      |
| 122 | max                             | Maximum                                                                                |
| 123 | median                          | Calculate median - usage: median(numbers...)                                          |
| 124 | min                             | Minimum                                                                                |
| 125 | mode                            | Calculate mode (most frequent value) - usage: mode(numbers...)                        |
| 126 | mod                             | Modulo operation - usage: mod(a, b) returns a % b                                      |
| 127 | multiply                        | Multiplication                                                                         |
| 128 | parseBinary                     | Convert binary string to number - usage: parseBinary(binaryStr)                        |
| 129 | parseHex                        | Convert hex string to number - usage: parseHex(hexStr)                                 |
| 130 | percentile                      | Calculate percentile - usage: percentile(numbers..., percentile)                      |
| 131 | pow                             | Power operation - usage: pow(base, exponent)                                          |
| 132 | range                           | Calculate range (max - min) - usage: range(numbers...)                                 |
| 133 | round                           | Round                                                                                  |
| 134 | roundTo                         | Round to specified decimal places - usage: roundTo(value, places)                      |
| 135 | signum                          | Sign function - returns -1, 0, 1 - usage: signum(value)                                |
| 136 | sin                             | Sine - usage: sin(radians)                                                             |
| 137 | sinh                            | Hyperbolic sine - usage: sinh(value)                                                   |
| 138 | sqrt                            | Square root - usage: sqrt(value)                                                       |
| 139 | stdDev                          | Calculate standard deviation - usage: stdDev(numbers...)                              |
| 140 | subtract                        | Subtraction - usage: subtract(a, b, ...) returns a-b-...                              |
| 141 | tan                             | Tangent - usage: tan(radians)                                                          |
| 142 | tanh                            | Hyperbolic tangent - usage: tanh(value)                                                |
| 143 | toBinary                        | Convert to binary string - usage: toBinary(number)                                    |
| 144 | toDegrees                       | Convert radians to degrees - usage: toDegrees(radians)                                 |
| 145 | toDouble                        | Convert to double - usage: toDouble(value, defaultValue?)                             |
| 146 | toFloat                         | Convert to float - usage: toFloat(value, defaultValue?)                                |
| 147 | toHex                           | Convert to hex string - usage: toHex(number)                                           |
| 148 | toInt                           | Convert to integer - usage: toInt(value, defaultValue?)                                |
| 149 | toLong                          | Convert to long integer - usage: toLong(value, defaultValue?)                          |
| 150 | toNumberString                  | Convert to number string - usage: toNumberString(number, pattern?)                     |
| 151 | toOctal                         | Convert to octal string - usage: toOctal(number)                                       |
| 152 | toRadians                       | Convert degrees to radians - usage: toRadians(degrees)                                 |
| 153 | ulp                             | Get unit in the last place of floating point - usage: ulp(value)                       |
| 154 | variance                        | Calculate variance - usage: variance(numbers...)                                       |
| 155 | randomBoolean                   | Generate random boolean - usage: randomBoolean() or randomBoolean(trueProbability?)    |
| 156 | randomChoice                    | Randomly select an element from list - usage: randomChoice(list) or randomChoice(elem1, elem2, ...) |
| 157 | randomDouble                    | Generate random double - usage: randomDouble() or randomDouble(min, max)               |
| 158 | random                          | Get a random element from array - usage: random(arr) or random(elem1, elem2, ...)      |
| 159 | randomInt                       | Generate random integer - usage: randomInt() or randomInt(max) or randomInt(min, max) |
| 160 | randomIntArray                  | Generate random integer array - usage: randomIntArray(size, min, max)                 |
| 161 | randomLong                      | Generate random long integer - usage: randomLong() or randomLong(max) or randomLong(min, max) |
| 162 | randomSample                    | Random sampling - usage: randomSample(list, count, allowRepeat?)                      |
| 163 | shuffle                         | Randomly shuffle list/array order - usage: shuffle(list)                              |
| 164 | randomString                    | Generate random string - usage: randomString(length)                                  |
| 165 | randomUUID                      | Generate random UUID - usage: randomUUID(withoutDashes?)                              |
| 166 | abbreviate                      | Abbreviate string - usage: abbreviate(str, maxWidth, ellipsis?)                       |
| 167 | capitalize                      | Capitalize first letter - usage: capitalize(str)                                      |
| 168 | centerPad                       | Center-align padding - usage: centerPad(str, size, padChar?)                          |
| 169 | compareTo                       | Lexicographical comparison - usage: compareTo(str1, str2, ignoreCase?)                |
| 170 | concat                          | Concatenate multiple strings                                                           |
| 171 | contains                        | Check if string contains substring                                                     |
| 172 | tokenize                        | Tokenize by multiple delimiters - usage: tokenize(str, delimiters)                     |
| 173 | countChar                       | Count character occurrences - usage: countChar(str, ch, ignoreCase?)                  |
| 174 | countMatches                    | Count substring occurrences - usage: countMatches(str, sub, ignoreCase?)              |
| 175 | equalsAny                       | Check if string equals any target - usage: equalsAny(str, target1, target2, ...)       |
| 176 | equalsIgnoreCase                | Compare strings ignoring case - usage: equalsIgnoreCase(str1, str2)                    |
| 177 | escapeHtml                      | HTML escape - convert special characters to HTML entities                              |
| 178 | escapeRegex                     | Escape regex special characters                                                         |
| 179 | format                          | Format string - usage: format(pattern, arg1, arg2, ...)                               |
| 180 | indexOf                         | Find first occurrence of substring - usage: indexOf(str, search, fromIndex?)          |
| 181 | isAlpha                         | Check if string contains only letters                                                 |
| 182 | isAlphaNumeric                  | Check if string contains only letters and digits                                       |
| 183 | isBlank                         | Check if string is null, empty or only whitespace                                      |
| 184 | isNumeric                       | Check if string contains only digits                                                   |
| 185 | isString                        | Check if it is a string - usage: isString(value)                                       |
| 186 | left                            | Get left N characters - usage: left(str, n)                                            |
| 187 | leftPad                         | Left padding - usage: leftPad(str, size, padChar?)                                     |
| 188 | length                          | Get string length                                                                      |
| 189 | levenshtein                     | Calculate Levenshtein edit distance - usage: levenshtein(str1, str2)                   |
| 190 | maskEmail                       | Email mask - usage: maskEmail(email) e.g. te***@example.com                            |
| 191 | mask                            | Mask processing - usage: mask(str, start, end, maskChar?)                              |
| 192 | matches                         | Regex match - usage: matches(str, regex)                                               |
| 193 | mid                             | Get middle part - usage: mid(str, start, length?)                                      |
| 194 | removeDuplicates                | Remove adjacent duplicate characters - usage: removeDuplicates(str)                    |
| 195 | removeEnd                       | Remove ending suffix - usage: removeEnd(str, suffix, ignoreCase?)                      |
| 196 | removeStart                     | Remove starting prefix - usage: removeStart(str, prefix, ignoreCase?)                  |
| 197 | removeWhitespace                | Remove all whitespace characters                                                       |
| 198 | repeat                          | Repeat string - usage: repeat(str, count, separator?)                                  |
| 199 | repeatChar                      | Repeat character - usage: repeatChar(ch, count)                                        |
| 200 | replace                         | Replace string - usage: replace(str, target, replacement)                              |
| 201 | replaceAll                      | Regex replace all - usage: replaceAll(str, regex, replacement)                         |
| 202 | reverse                         | Reverse string                                                                         |
| 203 | right                           | Get right N characters - usage: right(str, n)                                          |
| 204 | rightPad                        | Right padding - usage: rightPad(str, size, padChar?)                                   |
| 205 | similarity                      | Calculate string similarity (percentage) - usage: similarity(str1, str2)               |
| 206 | split                           | Split string - usage: split(str, regex)                                                |
| 207 | splitByLength                   | Split string by specified length - usage: splitByLength(str, chunkSize)                 |
| 208 | substring                       | Extract substring - usage: substring(str, beginIndex) or substring(str, beginIndex, endIndex) |
| 209 | substringAfter                  | Get content after specified substring - usage: substringAfter(str, separator)           |
| 210 | substringBefore                 | Get content before specified substring - usage: substringBefore(str, separator)         |
| 211 | substringBetween                | Get content between two substrings - usage: substringBetween(str, open, close)          |
| 212 | swapCase                        | Swap case - upper to lower, lower to upper                                              |
| 213 | toCamelCase                     | Convert to camel case - usage: toCamelCase(str, firstUpper?)                            |
| 214 | toLower                         | Convert string to lowercase                                                             |
| 215 | toSnakeCase                     | Convert to snake case - usage: toSnakeCase(str)                                        |
| 216 | toString                        | Convert to string - usage: toString(value, pattern?)                                   |
| 217 | toUpper                         | Convert string to uppercase                                                             |
| 218 | trim                            | Trim leading and trailing whitespace of string                                          |
| 219 | uncapitalize                    | Lowercase first letter - usage: uncapitalize(str)                                      |
| 220 | unescapeHtml                    | HTML unescape - restore HTML entities to characters                                     |
| 221 | uniqueChars                     | Keep unique characters (in order of first occurrence)                                   |
| 222 | wordCount                       | Count number of words - usage: wordCount(str)                                          |
| 223 | translate                       | Code value translation - usage: translate(context, code, dictType, defaultValue?)       |
| 224 | formatDate                      | Format date - usage: formatDate(date, pattern)                                          |
| 225 | now                             | Get current datetime                                                                   |
| 226 | parseDate                       | Parse date string - usage: parseDate(dateStr, pattern)                                  |
| 227 | timestamp                       | Get current timestamp                                                                  |
| 228 | today                           | Get current date                                                                       |
| 229 | toIsoString                     | Convert to ISO format string - usage: toIsoString(date)                                 |
| 230 | complexAdd                      | Complex number addition - usage: complexAdd(r1, i1, r2, i2) returns [real, imaginary]  |
| 231 | complexMultiply                 | Complex number multiplication - usage: complexMultiply(r1, i1, r2, i2) returns [real, imaginary] |
| 232 | matrixAdd                       | Matrix addition - usage: matrixAdd(matrix1, matrix2)                                    |
| 233 | toJson                          | Convert object to JSON string                                                           |
| 234 | randomColor                     | Generate random color - usage: randomColor(type?) type: 'hex', 'rgb', 'preset'          |
| 235 | randomDate                      | Generate random date - usage: randomDate(startDate, endDate, pattern?)                 |
| 236 | base64Decode                    | Base64 decode - usage: base64Decode(encodedStr)                                         |
| 237 | base64Encode                    | Base64 encode - usage: base64Encode(str)                                                |
| 238 | decodeUrl                       | URL decode - usage: decodeUrl(str)                                                      |
| 239 | encodeUrl                       | URL encode - usage: encodeUrl(str)                                                     |
| 240 | md5                             | MD5 hash - usage: md5(str)                                                              |
| 241 | isEmail                         | Check if it is a valid email address                                                    |
| 242 | countDistinct                   | Distinct count - returns count of distinct elements                                     |
| 243 | count                           | Count - returns the number of arguments                                                  |
| 244 | countNonNull                    | Non-null count - returns the number of non-null arguments                                |
| 245 | product                         | Product - calculates the product of all numeric arguments                                |
| 246 | sum                             | Sum - calculates the sum of all numeric arguments                                        |
| 247 | groupConcat                     | Group concatenation - merges strings with specified delimiter, first argument is delimiter |
| 248 | stringAgg                       | String aggregation - merges strings with specified delimiter                              |

#### 10.1 Built-in Functions
JQuick-SQL ships with 200+ built-in functions provided by [**jquick-transform-function**](https://github.com/paohaijiao/jquick-transform-function), organized into the following categories:

| Category | Description | Representative Functions |
|----------|-------------|--------------------------|
| String | String manipulation | `toUpper`, `toLower`, `concat`, `substring`, `replace`, `length`, `trim`, `mask`, `md5`, `base64Encode`, `reverse`, `split`, `pad` |
| Math | Math & aggregation | `abs`, `ceil`, `floor`, `round`, `max`, `min`, `sum`, `avg`, `count`, `sqrt`, `pow`, `log`, `toInt`, `toDouble`, `stdDev`, `median` |
| Date | Date & time | `now`, `today`, `addDays`, `formatDate`, `day`, `month`, `year`, `isWeekend`, `daysBetween`, `startOfMonth`, `endOfYear` |
| Condition | Conditional logic | `caseWhen`, `if`, `ifElse`, `coalesce`, `nvl`, `between`, `switch`, `defaultIfNull` |
| Convert | Type conversion | `toBoolean`, `toDate`, `toDateTime`, `toShort` |
| Business | Business validation & masking | `bankCardMask`, `bankCardValidate`, `idCardValidate`, `idCardInfo`, `phoneInfo`, `phoneMask`, `emailMask`, `isEmail` |
| Crypto | Encryption (AES / RSA / ECC) | `aesEncrypt`, `aesDecrypt`, `rsaEncrypt`, `rsaDecrypt`, `eccEncrypt`, `eccDecrypt`, `aesGenerateKey` |
| Collection | Collection operations | `isEmpty`, `size`, `join` |
| Bit | Bitwise operations | `bitAnd`, `bitOr`, `bitXor` |
| Geometry | Geometry & combinatorics | `areaCircle`, `areaRectangle`, `distance`, `factorial`, `gcd`, `lcm`, `permutation`, `crossProduct`, `dotProduct` |
| Random | Random generation | `randomInt`, `randomDouble`, `randomUUID`, `randomString`, `randomChoice`, `randomSample`, `randomShuffle` |
| Extra | Formatting & casting | `cast`, `formatNumber`, `parseNumber`, `toCurrency`, `toPercentage`, `typeOf`, `toArray`, `toList` |
| JSON | JSON handling | `toJson` |

All built-in functions can be called directly in SQL. Function names are case-insensitive:

```sql
SELECT toUpper(name) AS upper_name,
       length(addr) AS addr_len,
       formatDate(birthday, 'yyyy-MM') AS birth_month,
       caseWhen(age >= 30, 'senior', 'junior') AS level,
       mask(phoneInfo(addr), '***') AS masked_addr
FROM users
```

```log
[INFO] +-----------+----------+-------------+--------+-------------+
[INFO] | upper_name | addr_len | birth_month | level  | masked_addr |
[INFO] +-----------+----------+-------------+--------+-------------+
[INFO] | ALICE     | 7        | 2020-04      | junior | ***         |
[INFO] | BOB       | 8        | 1991-08      | senior | ***         |
[INFO] +-----------+----------+-------------+--------+-------------+
[INFO] Total: 7 rows
```

#### 10.2 Extension Mechanism
JQuick-SQL's function manager `JQuickMethodInvocationManager` is a singleton. At startup it loads all function providers through the custom SPI loader `com.github.paohaijiao.spi.ServiceLoader.loadServicesByPriority(Class)` (which ultimately delegates to the JDK `java.util.ServiceLoader`, reading `META-INF/services/<fully-qualified-interface-name>` files from the classpath) and sorts them by the `@Priority` annotation. The worker node startup log prints the number loaded:
```
[INFO] Loaded 259 functions via SPI
```

Extending a custom function takes three steps:
1. **Implement the function provider**: extend `JQuickBaseFunctionFunctionProvider` (which indirectly implements the `JQuickMethodFunctionProvider` interface) and write the function logic in `invoke(List<Object> args)`.
2. **Declare priority (optional)**: use the `@Priority` annotation to declare the priority; a smaller value ranks earlier. When not specified, the default is `5000` (`PriorityConstants.DEFAULT`).
3. **Register in the SPI container**: create a file named `com.github.paohaijiao.function.core.JQuickMethodFunctionProvider` under `src/main/resources/META-INF/services/` whose content is the fully-qualified name of the implementation class.

> ⚠️ **Common pitfall**: the SPI registration file name **must exactly equal the fully-qualified name of the interface** `com.github.paohaijiao.function.core.JQuickMethodFunctionProvider`. Using any other name (e.g. `com.github.paohaijiao.spi.JQuickFunctionProvider`) will cause `ServiceLoader` to never find the file, so the function provider will not be loaded.

#### 10.2 Built-in Priorities
The priority constants are defined in `com.github.paohaijiao.spi.constants.PriorityConstants`:

| Constant | Value | Description |
|----------|-------|-------------|
| `SYSTEM_HIGHEST` | 1 | System level, highest |
| `SYSTEM_HIGH` | 10 | System level, high |
| `SYSTEM_MEDIUM` | 20 | System level, medium |
| `SYSTEM_LOW` | 30 | System level, low |
| `APPLICATION_HIGHEST` | 100 | Application level, highest |
| `APPLICATION_HIGH` | 200 | Application level, high |
| `APPLICATION_MEDIUM` | 300 | Application level, medium |
| `APPLICATION_LOW` | 400 | Application level, low |
| `BUSINESS_HIGHEST` | 1000 | Business level, highest |
| `BUSINESS_HIGH` | 2000 | Business level, high |
| `BUSINESS_MEDIUM` | 3000 | Business level, medium |
| `BUSINESS_LOW` | 4000 | Business level, low |
| `USER_HIGHEST` | 5000 | User level, highest (default) |
| `USER_HIGH` | 6000 | User level, high |
| `USER_MEDIUM` | 7000 | User level, medium |
| `USER_LOW` | 8000 | User level, low |
| `LOWEST` | 10000 | Lowest priority |

When multiple function providers register functions with the same name, the one with the smaller priority value is ranked first and is invoked first.

#### 10.3 Example: Custom `jsonPath` Function
This example implements a `jsonPath(json, path)` function that extracts data from a JSON field by path.

**Step 1: Implement the Function Provider**

Create a function provider class that extends `JQuickBaseFunctionFunctionProvider` and implements the function logic in the `invoke` method:

```java
package com.github.paohaijiao.provider;

import com.github.paohaijiao.builder.JSONPathQueryBuilder;
import com.github.paohaijiao.exception.JAssert;
import com.github.paohaijiao.function.domain.JQuickBaseFunctionFunctionProvider;
import com.github.paohaijiao.model.JSONObject;
import com.github.paohaijiao.model.JSONPathResult;
import com.github.paohaijiao.spi.anno.Priority;
import com.github.paohaijiao.spi.constants.PriorityConstants;

import java.util.List;

@Priority(PriorityConstants.SYSTEM_HIGH)
public class JQuickJSonPathFunctionFunctionProvider extends JQuickBaseFunctionFunctionProvider {

    public JQuickJSonPathFunctionFunctionProvider() {
        super("jsonPath", "Extract data by JSON path expression - usage: jsonPath(jsonField, pathExpression)");
    }

    @Override
    public Object invoke(List<Object> args) {
        validateArgCount(args, 2);
        Object object = args.get(0);
        JAssert.notNull(object, "para1 require not null");
        String path = (String) args.get(1);
        JSONObject obj;
        if (object instanceof JSONObject) {
            obj = (JSONObject) object;
        } else if (object instanceof java.util.Map) {
            @SuppressWarnings("unchecked")
            java.util.Map<String, Object> map = (java.util.Map<String, Object>) object;
            obj = new JSONObject(map);
        } else {
            throw new IllegalArgumentException(
                    "jsonPath first argument must be a JSON object or row, but was: "
                            + (object == null ? "null" : object.getClass().getName()));
        }
        JSONPathResult result = JSONPathQueryBuilder.from(obj)
                .path(path)
                .execute();
        return result.getRawData();
    }
}
```

Notes:
- The constructor `super(methodName, description)` registers the function name and description; the function name is the name used when calling it in SQL (case-insensitive).
- `JQuickBaseFunctionFunctionProvider` provides parameter conversion and validation helper methods: `asString`, `asInt`, `asLong`, `asDouble`, `asBoolean`, `validateArgCount`, `validateArgCountRange`.
- `@Priority(PriorityConstants.SYSTEM_HIGH)` declares the priority; any other implementation with the same name (if present) will be ranked after it.

**Step 2: Register via SPI**

Create a file named `com.github.paohaijiao.function.core.JQuickMethodFunctionProvider` under `src/main/resources/META-INF/services/` whose content is the fully-qualified name of the implementation class (one class per line; `#` comments are supported):

```
com.github.paohaijiao.provider.JQuickJSonPathFunctionFunctionProvider
```

> When registering multiple function providers, each class occupies one line. For functions with the same name, `@Priority` determines which implementation is invoked first.

**Step 3: Use in SQL**

Once registered, it can be called in SQL just like a built-in function:

```sql
SELECT name, age, status, enable, jsonPath(detail, '$.position') FROM users
```

```log
[INFO] +----+---------+-----+----------+--------+--------------------+
[INFO] | id | name    | age | status   | enable | position            |
[INFO] +----+---------+-----+----------+--------+--------------------+
[INFO] | 1  | Alice   | 25  | active   | true   | Senior Developer   |
[INFO] | 2  | Bob     | 30  | active   | true   | Marketing Manager  |
[INFO] | 3  | Charlie | 20  | pending  | false  | Junior Accountant  |
[INFO] | 4  | David   | 35  | inactive | true   | HR Director        |
[INFO] | 5  | Eve     | 28  | active   | true   | Frontend Developer |
[INFO] | 6  | Martin  | 30  | active   | true   | Architect          |
[INFO] | 7  | Davila  | 39  | active   | true   | Tech Lead          |
[INFO] +----+---------+-----+----------+--------+--------------------+
[INFO] Total: 7 rows
```

#### 10.4 Programmatic Registration (Optional)
In addition to SPI auto-discovery, you can also register functions manually in code (useful for scenarios that require runtime dynamic registration):

```java
JQuickMethodInvocationManager manager = JQuickMethodInvocationManager.getInstance();
manager.registerInvoker(new JQuickJSonPathFunctionFunctionProvider());

// Or register a simple function directly as a lambda
manager.registerInvoker("greet", args -> "Hello, " + args.get(0), "Concatenate a greeting", PriorityConstants.USER_HIGH);
```

> Manually registered functions share the same registry as SPI-loaded functions. A function registered later with the same name will not override one registered earlier (use `registerOrReplaceInvoker` to override).

## XML Configuration Usage

JQuick-SQL provides an XML-based configuration approach that allows you to define SQL queries in an XML file and generate service interfaces dynamically. This is particularly useful for organizing large numbers of SQL statements and maintaining clean separation between SQL and Java code.

### XML Configuration File

Create an XML file (e.g., `jquick-sql.xml`) in your classpath:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE sqls PUBLIC "-//PAOHAIJIAO//DTD API JAVA 1.0//EN"
        "classpath:paohaijiao/dtd/Jquick-sql.dtd">
<sqls namespace="com.example.service.UserService">
    <sql name="getUsers" returnClass="java.util.List">
        SELECT * FROM users LIMIT #{limit}
    </sql>
</sqls>
```

### Define Service Interface

Define a Java interface with methods annotated by `@Param`:

```java
package com.example.service;

import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;
import com.github.paohaijiao.xml.param.Param;

import java.util.List;

public interface UserService {
    JQuickDataSet getUsers(@Param("limit") Integer limit);
}
```

The method names in the interface must match the name attribute of the <sql> elements in the XML configuration file. The @Param annotation binds method parameters to the placeholders (#{...}) used in the SQL statements.

### Register Table Data

Register your table data with `JQuickTable`:

```java
// Create column metadata
List<JQuickColumnMeta> columns = Arrays.asList(
                new JQuickColumnMeta("id", Integer.class, "users"),
                new JQuickColumnMeta("name", String.class, "users"),
                new JQuickColumnMeta("age", Integer.class, "users")
        );

// Create row data
List<JQuickRow> rows = Arrays.asList(
        createRow("id", 1, "name", "Alice", "age", 25),
        createRow("id", 2, "name", "Bob", "age", 30)
);

JQuickTable table = new JQuickTable("users", columns, rows);
```
### Create and Use Service API

```java
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.xml.factory.JQuickFactory;
import com.github.paohaijiao.xml.factory.JQuickXmlFactory;

public class Example {
    public static void main(String[] args) {
        // Create table data
        JQuickTable table = new JQuickTable("users", getUserColumns(), getUserRows());
        JQuickJavaXmlParseFactory handler = new JQuickJavaXmlParseFactory(Arrays.asList(table));

        // Create factory with XML configuration
        JQuickFactory factory = new JQuickXmlFactory(handler, "jquick-sql.xml");

        // Generate service API dynamically
        UserService userService = factory.createApi(UserService.class);

        // Execute queries
        JQuickDataSet dataSet = userService.getUsers(2);
        dataSet.printTable();  // Print the result table
    }
}
```
### Configuration Elements

| XML Element | Attribute | Description |
|-------------|-----------|-------------|
| `<sqls>` | `namespace` | The fully qualified name of the service interface |
| `<sql>` | `name` | The method name in the service interface |
| `<sql>` | `returnClass` | The return type of the method (e.g., `java.util.List`, `java.lang.Integer`) |

### Parameter Binding

Use `#{paramName}` syntax in SQL to bind method parameters:

```xml
<sql name="findUsers" returnClass="java.util.List">
    SELECT * FROM users
    WHERE age > #{minAge}
    AND status = #{status}
    LIMIT #{limit}
</sql>
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
