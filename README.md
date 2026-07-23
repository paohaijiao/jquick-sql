# JQuick-SQL

A lightweight SQL parser and distributed query engine , supporting standard SQL syntax and various OLAP operations.
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

```java
// Create embedded SQL engine
JQuickSQL sql = JQuickSQL.embedded(3);
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

### 1. project Query
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
### 2. filter Query
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



## Supported SQL Features

| Feature | Status |
|---------|--------|
| SELECT | ✅ |
| WHERE | ✅ |
| GROUP BY | ✅ |
| HAVING | ✅ |
| ORDER BY | ✅ |
| LIMIT / OFFSET | ✅ |
| JOIN (INNER/LEFT/RIGHT/FULL) | ✅ |
| CROSS JOIN | ✅ |
| UNION / UNION ALL | ✅ |
| Aggregation (COUNT/SUM/AVG/MIN/MAX) | ✅ |
| Recursive CTE | ✅ |
| Subquery | ✅ |
| Functions | ✅ |

## License

Apache License 2.0
