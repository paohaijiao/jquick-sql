# JQuick-SQL

A lightweight SQL parser and distributed query engine , supporting standard SQL syntax and various OLAP operations.

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
- ✅ Recursive CTE (Common Table Expression)
- ✅ gRPC-based Data Exchange

## Quick Start

### Maven Dependency

```xml
<dependency>
    <groupId>io.github.paohaijiao</groupId>
    <artifactId>jquick-sql</artifactId>
    <version>2.0.0</version>
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
> ```

### Load from MySQL Database
```sql
-- Define a virtual table 'users' that queries a MySQL table
SELECT
    field(id) -> id:Integer,
        field(name) -> name:String,
        field(age) -> age:Integer,
        field(status) -> status:String,
        field(enable) -> enable:Boolean,
        field(addr) -> addr:String,
        field(birthday) -> birthday:Date
FROM MYSQL(
        url: 'jdbc:mysql://localhost:3306/mydb',
        username: 'root',
        password: 'password',
       sql: 'SELECT * FROM users',
       driver: 'com.mysql.jdbc.Driver'
     );
```

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
| UNION / UNION ALL                       | ✅ |
| Aggregation (COUNT/SUM/AVG/MIN/MAX)     | ✅ |
| Recursive CTE                           | ✅ |
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

#### 2.10
**SQL Code**
```sql
SELECT * FROM users WHERE age  between 25 and 30
```
```log
[2026-07-23 11:40:51.131] [INFO] +----+---------+-----+----------+--------+---------+----------------------+
[2026-07-23 11:40:51.131] [INFO] | id | name    | age | status   | enable | addr    | birthday             |
[2026-07-23 11:40:51.131] [INFO] +----+---------+-----+----------+--------+---------+----------------------+
[2026-07-23 11:40:51.131] [INFO] | 3  | Charlie | 20  | pending  | false  | chengdu | 1988-07-11T15:00:00Z |
[2026-07-23 11:40:51.132] [INFO] | 4  | David   | 35  | inactive | true   | xian    | 1955-11-28T16:00:00Z |
[2026-07-23 11:40:51.132] [INFO] | 7  | Davila  | 39  | active   | true   | null    | 1999-06-29T16:00:00Z |
[2026-07-23 11:40:51.132] [INFO] +----+---------+-----+----------+--------+---------+----------------------+
[2026-07-23 11:40:51.132] [INFO] Total: 3 rows
```

#### 2.11
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

#### 2.12
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

#### 2.13
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

#### 2.14
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

#### 2.15
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
