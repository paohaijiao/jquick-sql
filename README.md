<p align="center">
  <img src="./images/jquick-logo.svg" width="680" alt="JQuick-SQL logo" />
</p>

<h1 align="center">JQuick-SQL</h1>

<p align="center">
  A pure-Java SQL toolkit built on GRPC — SQL parsing to AST, logical transformation, dynamic SQL assembly, and an embeddable execution engine.
</p>

<p align="center">
  <b>English</b> | <a href="./README-CN.md">简体中文</a>
</p>

<p align="center">
  <a href="https://github.com/paohaijiao/jquick-sql/actions"><img src="https://github.com/paohaijiao/jquick-sql/actions/workflows/maven.yml/badge.svg" alt="Build" /></a>
  <a href="https://github.com/paohaijiao/jquick-sql/blob/main/LICENSE"><img src="https://img.shields.io/badge/license-Apache--2.0-blue.svg" alt="License" /></a>
  <a href="https://github.com/paohaijiao/jquick-sql/stargazers"><img src="https://img.shields.io/github/stars/paohaijiao/jquick-sql.svg?logo=github&amp;label=Stars" alt="GitHub stars" /></a>
  <a href="https://central.sonatype.com/artifact/io.github.paohaijiao/jquick-sql-runtime"><img src="https://img.shields.io/maven-central/v/io.github.paohaijiao/jquick-sql-runtime.svg?label=Maven%20Central" alt="Maven Central" /></a>
  <a href="https://github.com/akullpp/awesome-java"><img src="https://awesome.re/mentioned-badge.svg" alt="Awesome Java" /></a>

</p>

<p align="center">
  <img src="https://img.shields.io/badge/Java-8%2B-orange.svg" alt="Java 8+" />
  <img src="https://img.shields.io/badge/ANTLR-4.x-blue.svg" alt="ANTLR 4" />
</p>

<p align="center">
  Part of the <a href="https://github.com/paohaijiao">JQuick</a> open-source ecosystem — Five projects in this ecosystem are listed in <a href="https://github.com/akullpp/awesome-java">Awesome Java</a>.
</p>

---

## 1. Project Introduction

JQuick-SQL is a SQL AST parsing and SQL logical transformation toolkit for Java applications. It parses SQL text into a typed AST with a hand-written  grammar, converts that AST into logical and physical plans, optimizes the plan with rule-based rewriters, and executes it against datasets registered in-process. No database server, no JDBC driver, and no string-level regex surgery are required.

The library is built around four stages:

- **Parse** — SQL text becomes a syntax tree through lexer and parser, then a typed AST (`JQuickQueryNode` and 30+ node types).
- **Transform** — the AST is converted into a logical plan, rewritten by 12 optimizer rules, and lowered into a physical plan.
- **Assemble** — SQL statements can be kept in XML files and bound to Java interfaces with `#{param}` placeholders.
- **Execute** — plans run in an embeddable engine against datasets you register (in-memory rows, or rows produced by other JQuick components).

| Scenario | What JQuick-SQL provides |
|----------|--------------------------|
| Legacy system SQL splitting | Parse existing statements into an AST, then walk and split them by clause instead of by string search. |
| Cross-database compatibility | A single grammar and a single AST; dialect differences are handled at AST/plan level rather than by ad-hoc text replacement. |
| SQL audit and governance | Inspect tables, columns, predicates and function calls as typed AST nodes — no live database connection needed. |
| Dynamic condition assembly | XML-driven SQL with `#{param}` placeholders bound to Java interface methods. |
| In-process analytics | Register datasets and run `SELECT` / `JOIN` / `GROUP BY` without deploying a database. |

---

## 2. Features

**Parser**

- Typed AST model with 30+ node classes: select spec, from/join/where/group by/having/order by/limit clauses, set operations, subqueries, CTEs, case-when, function calls.
- Visitor-based traversal (`JQuickSQLVisitor`, `JQuickSQLCommonVisistor`, `JQuickSQLSelectSpecVisitor`) and a listener API for custom analysis.
- `JQuickSQLExecutor` — one-line entry point that turns a SQL string into a `JQuickQueryNode` AST.

**Logical transformation**

- AST → logical plan conversion (`JQuickASTToLogicalPlanVisitor`).
- 12 rule-based optimizer implementations: predicate pushdown, projection pushdown, projection merge, filter merge, redundant filter removal, constant folding, expression simplification, limit pushdown, aggregate pushdown, join reorder, subquery-to-join, distribution optimization.
- Logical → physical plan lowering (`JQuickPhysicalPlanGenerator`).
- 16 physical node types: table scan, filter, project, values, empty, sort, top-N, limit, hash join, nested-loop join, hash aggregate, exchange, set operation, recursive union, window, abstract base.

**Execution**

- `JQuickSQL` facade with embedded mode: `JQuickSQL.embedded()` / `embedded(n)` / `builder()`.
- Fragmenter splits a physical plan into fragments; the coordinator dispatches and collects results.
- Expression evaluator covering comparison, logic, arithmetic, `LIKE` / `REGEXP`, `IN`, `BETWEEN`, `CASE WHEN`, subqueries and function calls.
- Extensible function SPI — custom functions are discovered through `ServiceLoader`.

**Integration**

- XML dynamic SQL proxy in iBatis style: SQL in XML, interface implemented by JDK dynamic proxy, `#{param}` placeholders.
- Bundled DTD for XML validation (`paohaijiao/dtd/Jquick-sql.dtd`).
- Protobuf/gRPC definitions for the execution layer (`jquick-sql-proto`).

---

## 3. Environment

| Item | Requirement |
|------|-------------|
| JDK | 1.8 or higher (`java.version=1.8`) |
| Build tool | Maven 3.x |
| Execution layer | gRPC 1.80.0 + protobuf-java 3.21.12 + grpc-netty-shaded |
| Serialization | snappy-java 1.1.10.5 |
| JQuick dependencies | javelin-core, jquick-grpc, jquick-banner, jquick-path, jquick-xmlProxy, jquick-transform-function |

---

## 4. Quick Start

### 4.1 Maven

```xml
<dependency>
    <groupId>io.github.paohaijiao</groupId>
    <artifactId>jquick-sql-runtime</artifactId>
    <version>5.0.0</version>
</dependency>
```

`jquick-sql-runtime` is the entry artifact; it pulls `jquick-sql-query`, `jquick-sql-plan`, `jquick-sql-parser` and `jquick-sql-core` transitively.

### 4.2 Gradle

```groovy
implementation 'io.github.paohaijiao:jquick-sql-runtime:5.0.0'
```

### 4.3 Parse a SQL statement

```java
package demo;

import com.github.paohaijiao.ast.JQuickQueryNode;
import com.github.paohaijiao.executor.JQuickSQLExecutor;

/**
 * Purpose: parse a SQL string into a typed AST.
 * Use case: SQL auditing, statement splitting, custom static analysis.
 */
public class ParseDemo {

    public static void main(String[] args) {
        JQuickSQLExecutor executor = new JQuickSQLExecutor();
        JQuickQueryNode ast = executor.execute(
                "SELECT dept, COUNT(*) FROM emp WHERE salary > 20000 GROUP BY dept HAVING COUNT(*) > 1"
        );
        System.out.println(ast);
    }
}
```

### 4.4 Run a query

```java
package demo;

import com.github.paohaijiao.engine.JQuickSQL;
import com.github.paohaijiao.statement.JQuickColumnMeta;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;

import java.util.Arrays;
import java.util.List;

/**
 * Purpose: register an in-memory dataset and execute SQL against it.
 * Use case: unit tests, offline report computation, in-process analytics.
 * Note: embedded workers occupy localhost ports 19001+, so always call shutdown().
 */
public class QueryDemo {

    public static void main(String[] args) {
        JQuickSQL sql = JQuickSQL.embedded();
        try {
            List<JQuickColumnMeta> columns = Arrays.asList(
                    new JQuickColumnMeta("id", Integer.class, "users"),
                    new JQuickColumnMeta("name", String.class, "users"),
                    new JQuickColumnMeta("age", Integer.class, "users")
            );
            List<JQuickRow> rows = Arrays.asList(
                    row("id", 1, "name", "Alice", "age", 25),
                    row("id", 2, "name", "Bob", "age", 30),
                    row("id", 3, "name", "Charlie", "age", 20)
            );
            sql.registerTable("users", columns, rows);

            JQuickDataSet result = sql.execute(
                    "SELECT id, name, age FROM users WHERE age >= 25 ORDER BY age DESC"
            );
            result.printTable();
            System.out.println("rows: " + result.size());
        } finally {
            sql.shutdown();
        }
    }

    private static JQuickRow row(Object... kv) {
        JQuickRow r = new JQuickRow();
        for (int i = 0; i < kv.length; i += 2) {
            r.put((String) kv[i], kv[i + 1]);
        }
        return r;
    }
}
```

---

## 5. Code Examples

### 5.1 Walk the AST and inspect clauses

```java
package demo;

import com.github.paohaijiao.ast.JQuickQueryNode;
import com.github.paohaijiao.ast.JQuickSelectClauseNode;
import com.github.paohaijiao.ast.JQuickSelectExpressionNode;
import com.github.paohaijiao.executor.JQuickSQLExecutor;

/**
 * Purpose: parse SQL and inspect the AST through typed accessors.
 * Use case: SQL audit — collect clause usage before the statement reaches a database.
 */
public class AstAuditDemo {

    public static void main(String[] args) {
        JQuickQueryNode ast = new JQuickSQLExecutor().execute(
                "SELECT u.name, o.amount FROM users u LEFT JOIN orders o ON u.id = o.user_id WHERE o.amount > 100"
        );
        JQuickSelectExpressionNode expression = ast.getSelectStatement().getSelectExpression();
        for (JQuickSelectClauseNode clause : expression.getDataSetOp().getSelectClauses()) {
            System.out.println("nodeType      : " + clause.getNodeType());
            System.out.println("distinct      : " + clause.isDistinct());
            System.out.println("fromClause    : " + (clause.getFromClause() != null));
            System.out.println("joinClauses   : " + clause.getJoinClauses().size());
            System.out.println("whereClause   : " + (clause.getWhereClause() != null));
            System.out.println("groupByClause : " + (clause.getGroupByClause() != null));
            System.out.println("orderByClause : " + (clause.getOrderByClause() != null));
        }
    }
}
```

### 5.2 Dynamic SQL assembly with the XML proxy

SQL lives in XML; the interface method call binds `#{param}` at runtime. The XML file (`jquick-sql.xml` below) must be on the classpath.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE sqls PUBLIC "-//PAOHAIJIAO//DTD API JAVA 1.0//EN"
        "classpath:paohaijiao/dtd/Jquick-sql.dtd">
<sqls namespace="demo.XmlProxyDemo.UserService">
    <sql name="topUsers" returnClass="java.util.List">
        select id, name, age from users order by age desc limit #{limit}
    </sql>
</sqls>
```

```java
package demo;

import com.github.paohaijiao.domain.JQuickTable;
import com.github.paohaijiao.statement.JQuickColumnMeta;
import com.github.paohaijiao.statement.JQuickRow;
import com.github.paohaijiao.xml.JQuickJavaXmlParseFactory;
import com.github.paohaijiao.xml.factory.JQuickFactory;
import com.github.paohaijiao.xml.factory.JQuickXmlFactory;
import com.github.paohaijiao.xml.param.Param;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Purpose: bind XML-declared SQL to a Java interface.
 * Use case: legacy iBatis-style projects that keep SQL outside Java code.
 * Note: the XML namespace must equal the interface FQCN, and method parameters
 *       are bound to #{...} placeholders through @Param.
 */
public class XmlProxyDemo {

    public interface UserService {
        List<JQuickRow> topUsers(@Param("limit") Integer limit);
    }

    public static void main(String[] args) {
        List<JQuickColumnMeta> columns = Arrays.asList(
                new JQuickColumnMeta("id", Integer.class, "users"),
                new JQuickColumnMeta("name", String.class, "users"),
                new JQuickColumnMeta("age", Integer.class, "users")
        );
        List<JQuickRow> rows = Collections.singletonList(
                row("id", 1, "name", "Alice", "age", 25)
        );

        List<JQuickTable> tables = Collections.singletonList(
                new JQuickTable("users", columns, rows)
        );
        JQuickJavaXmlParseFactory handler = new JQuickJavaXmlParseFactory(tables);
        JQuickFactory factory = new JQuickXmlFactory(handler, "jquick-sql.xml");
        UserService service = factory.createApi(UserService.class);

        service.topUsers(2).forEach(System.out::println);
    }

    private static JQuickRow row(Object... kv) {
        JQuickRow r = new JQuickRow();
        for (int i = 0; i < kv.length; i += 2) {
            r.put((String) kv[i], kv[i + 1]);
        }
        return r;
    }
}
```

### 5.3 Add a custom function through the SPI

Step 1 — extend `JQuickBaseFunctionFunctionProvider`. The two constructor arguments are the SQL function name and its description.

```java
package demo.function;

import com.github.paohaijiao.function.domain.JQuickBaseFunctionFunctionProvider;

import java.util.List;

/**
 * SQL usage: TO_CAMEL_CASE(name, '_')
 */
public class ToCamelCaseFunction extends JQuickBaseFunctionFunctionProvider {

    public ToCamelCaseFunction() {
        super("toCamelCase", "convert a delimited string to camelCase - usage: toCamelCase(value, delimiter)");
    }

    @Override
    public Object invoke(List<Object> args) {
        validateArgCount(args, 2);
        Object value = args.get(0);
        Object delimiter = args.get(1);
        if (value == null || delimiter == null) {
            return null;
        }
        String[] parts = value.toString().split(java.util.regex.Pattern.quote(delimiter.toString()));
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            if (parts[i].isEmpty()) {
                continue;
            }
            sb.append(i == 0 ? Character.toLowerCase(parts[i].charAt(0))
                             : Character.toUpperCase(parts[i].charAt(0)));
            if (parts[i].length() > 1) {
                sb.append(parts[i].substring(1));
            }
        }
        return sb.toString();
    }
}
```

Step 2 — register it with `ServiceLoader`. Create the file named exactly after the SPI interface FQCN:

```
src/main/resources/META-INF/services/com.github.paohaijiao.function.core.JQuickMethodFunctionProvider
```

with one implementation class per line:

```
demo.function.ToCamelCaseFunction
```

Step 3 — use it in SQL:

```java
JQuickDataSet result = sql.execute(
        "SELECT name, toCamelCase(name, '_') AS camel FROM users"
);
```

### 5.4 Configure the engine with the builder

```java
package demo;

import com.github.paohaijiao.engine.JQuickSQL;
import com.github.paohaijiao.statement.JQuickColumnMeta;
import com.github.paohaijiao.statement.JQuickRow;

import java.util.Arrays;
import java.util.List;

/**
 * Purpose: build the engine with the fluent builder.
 * Use case: code-first configuration, batch jobs, multi-tenant engine construction.
 * Note: use config(JQuickSqlConfig) together with worker(host, port) when the engine talks to
 *       externally started workers; in embedded mode the engine builds its own runtime config.
 */
public class BuilderDemo {

    public static void main(String[] args) {
        List<JQuickColumnMeta> columns = Arrays.asList(
                new JQuickColumnMeta("city", String.class, "sales"),
                new JQuickColumnMeta("amount", Long.class, "sales")
        );
        List<JQuickRow> rows = Arrays.asList(
                row("city", "Beijing", "amount", 1200L),
                row("city", "Shanghai", "amount", 2100L),
                row("city", "Beijing", "amount", 800L)
        );

        JQuickSQL sql = JQuickSQL.builder()
                .embedded(2)
                .parallelism(2)
                .table("sales", columns, rows)
                .build();
        try {
            sql.execute("SELECT city, SUM(amount) gmv FROM sales GROUP BY city ORDER BY gmv DESC")
               .printTable();
        } finally {
            sql.shutdown();
        }
    }

    private static JQuickRow row(Object... kv) {
        JQuickRow r = new JQuickRow();
        for (int i = 0; i < kv.length; i += 2) {
            r.put((String) kv[i], kv[i + 1]);
        }
        return r;
    }
}
```

### 5.5 Supported SQL constructs

```sql
-- projection, alias, expression, CASE WHEN, DISTINCT
SELECT DISTINCT dept,
       CASE WHEN salary >= 30000 THEN 'HIGH' ELSE 'LOW' END grade
  FROM emp;

-- WHERE: comparison, AND/OR/NOT, BETWEEN, IN, LIKE, REGEXP, IS NULL, EXISTS
SELECT id, name FROM users
 WHERE age > 25 AND status = 'active'
   AND name LIKE '%a%' AND name REGEXP '^A.*'
   AND addr IS NOT NULL
   AND id IN (SELECT user_id FROM orders);

-- join: INNER / LEFT / RIGHT / CROSS / NATURAL
SELECT u.name, o.amount FROM users u LEFT JOIN orders o ON u.id = o.user_id;

-- aggregation and filtering
SELECT dept, COUNT(*) c, AVG(salary) avg_sal
  FROM emp GROUP BY dept HAVING COUNT(*) >= 2 ORDER BY c DESC;

-- pagination
SELECT name FROM users ORDER BY age DESC LIMIT 2, 3;

-- set operations
SELECT name FROM list_a UNION     SELECT name FROM list_b;
SELECT name FROM list_a MINUS     SELECT name FROM list_b;
SELECT name FROM list_a INTERSECT SELECT name FROM list_b;
```

---

## 6. Supported Databases

JQuick-SQL is a grammar-level SQL parser and an in-process execution engine. It does not ship a JDBC dialect layer or database drivers: SQL text is parsed into an AST, and data is supplied as datasets registered by your application (in-memory rows, or rows produced by other JQuick components).

| SQL capability | Accepted syntax |
|----------------|-----------------|
| Projection | `SELECT *`, column list, aliases, arithmetic, `CASE WHEN`, `DISTINCT`, scalar subqueries |
| Filtering | `=`, `<>`, `>`, `>=`, `<`, `<=`, `AND` / `OR` / `NOT`, `IS [NOT] NULL`, `BETWEEN`, `IN`, `LIKE`, `REGEXP`, `EXISTS` |
| Joins | `INNER`, `LEFT`, `RIGHT`, `CROSS`, `NATURAL` |
| Aggregation | `COUNT`, `SUM`, `AVG`, `MIN`, `MAX` with `GROUP BY` and `HAVING` |
| Sorting | `ORDER BY col [ASC\|DESC]`, multiple keys, expression ordering |
| Pagination | `LIMIT n`, `LIMIT offset, n` |
| Set operations | `UNION`, `MINUS`, `INTERSECT` |
| Subqueries | in `WHERE` / `SELECT` / `HAVING` / `ORDER BY` / `FROM` / `JOIN`, nested and multi-column |

Notes:

- `FULL OUTER JOIN` is not supported.
- Data source connectivity is not part of this library; datasets are registered through `registerTable(...)`.

---

## 7. Module Description

| Module | Packaging | Responsibility |
|--------|-----------|----------------|
| `jquick-sql-core` | jar | Configuration (`JQuickSqlConfig`, `JQuickSqlRuntimeConfig`), enums, datasource registry, execution statistics, utilities |
| `jquick-sql-parser` | jar | grammar, generated lexer/parser, AST node model, visitors, `JQuickSQLExecutor` |
| `jquick-sql-plan` | jar | AST → logical plan, 12 optimizer rules, logical → physical plan, physical node implementations, physical plan optimizer |
| `jquick-sql-proto` | jar | Protobuf messages and gRPC service stubs for the execution layer (`physical.proto`) |
| `jquick-sql-query` | jar | Execution layer: fragmenter, exchange node, coordinator, worker, partition manager, expression evaluator |
| `jquick-sql-runtime` | jar | Entry point `JQuickSQL`, XML dynamic proxy, SPI function provider, XML DTD; depends on `jquick-sql-query` |

Add `jquick-sql-runtime` to your project; the remaining modules are resolved transitively.

---

## 8. Compatibility Notes

| Item | Note |
|------|------|
| JDK | 8 and above; the build targets `1.8` |
| Embedded engine | `embedded(n)` starts `n` local workers on ports `19001..19000+n`; call `shutdown()` in a `finally` block |
| Column metadata | The third argument of `JQuickColumnMeta` is the table alias; SQL aliases must match it, otherwise column resolution fails |
| Boolean columns | Declare them as `Boolean.class`; string `"true"` / `"false"` values are not coerced |
| Thread safety | `JQuickSQL` is safe to share; `JQuickDataSet` is not thread-safe |
| XML proxy | The `<sqls namespace>` must equal the bound interface FQCN; the DTD is bundled at `classpath:paohaijiao/dtd/Jquick-sql.dtd` |
| Custom functions | The `META-INF/services/` file name must equal the SPI interface FQCN `com.github.paohaijiao.function.core.JQuickMethodFunctionProvider` |
| MyBatis integration | A dedicated MyBatis / MyBatis-Plus adapter is not shipped; the built-in XML dynamic proxy is iBatis-style and can be used on its own |
| Unsupported SQL | `FULL OUTER JOIN` |

---

## 9. License

JQuick-SQL is released under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt).

```
Copyright (c) 2025-2099 Martin (goudingcheng@gmail.com)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0
```

---

## 10. Contribute

1. Fork the repository and create a branch: `feature/xxx`, `fix/issue-123` or `docs/xxx`.
2. Make sure `mvn clean test` passes before committing.
3. Keep the module boundaries intact — grammar changes belong in `jquick-sql-parser`, optimizer rules in `jquick-sql-plan`.
4. New capabilities must come with a runnable test or demo class under `src/test/java`.
5. PR titles follow `[module] short description`, for example `[optimizer] Extend projection pushdown to derived tables`.

Repository: <https://github.com/paohaijiao/jquick-sql>
