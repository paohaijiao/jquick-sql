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

### 1. Simple Query
**Input Data**

| id | name    | age | status   | enable | addr       | birthday   |
|----|---------|-----|----------|--------|------------|------------|
| 1  | Alice   | 25  | active   | true   | beijing    | 2020-04-09 |
| 2  | Bob     | 30  | active   | true   | shanghai   | 1991-08-09 |
| 3  | Charlie | 20  | pending  | false  | chengdu    | 1988-07-12 |
| 4  | David   | 35  | inactive | true   | xian       | 1955-11-29 |
| 5  | Eve     | 28  | active   | true   | chongqing  | 2003-07-12 |
| 6  | Martin  | 30  | active   | true   | guangzhou  | 1978-06-30 |

**SQL Code**
```sql
SELECT id, name,age, status,enable,addr,birthday FROM users
```
**Output Data**

| id | name    | age | status   | enable | addr      | birthday                 |
|----|---------|-----|----------|--------|-----------|--------------------------|
| 1  | Alice   | 25  | active   | true   | beijing   | 2020-04-08T16:00:00Z     |
| 2  | Bob     | 30  | active   | true   | shanghai  | 1991-08-08T15:00:00Z     |
| 3  | Charlie | 20  | pending  | false  | chengdu   | 1988-07-11T15:00:00Z     |
| 4  | David   | 35  | inactive | true   | xian      | 1955-11-28T16:00:00Z     |
| 5  | Eve     | 28  | active   | true   | chongqing | 2003-07-11T16:00:00Z     |
| 6  | Martin  | 30  | active   | true   | guangzhou | 1978-06-29T16:00:00Z     |

---

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
