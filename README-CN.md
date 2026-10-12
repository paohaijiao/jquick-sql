<p align="center">
  <img src="./images/jquick-logo.svg" width="680" alt="JQuick-SQL logo" />
</p>

<h1 align="center">JQuick-SQL</h1>

<p align="center">
  基于 GRPC 的纯 Java SQL 工具库 —— 从 SQL 解析到 AST、逻辑转换、动态 SQL 组装，以及可嵌入式执行引擎。
</p>

<p align="center">
  🌐 <a href="https://www.jquick.org">JQuick Website</a> ·
  📖 <a href="https://github.com/paohaijiao">GitHub</a> ·
  📦 <a href="https://central.sonatype.com/artifact/io.github.paohaijiao/javelin">Maven Central</a>
</p>

<p align="center">
  🔗 <a href="https://github.com/paohaijiao/jquick-curl">jquick-curl</a> ·
  📂 <a href="https://github.com/paohaijiao/jquick-path">jquick-path</a> ·
  📊 <a href="https://github.com/paohaijiao/jquick-excel">jquick-excel</a> ·
  📄 <a href="https://github.com/paohaijiao/jquick-pdf">jquick-pdf</a> ·
  ☕ <a href="https://github.com/paohaijiao/jquick-java">jquick-java</a> ·
  🗄️ <a href="https://github.com/paohaijiao/jquick-sql">jquick-sql</a>
</p>

<p align="center">
  <a href="./README.md">English</a> | <b>简体中文</b>
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
  <a href="https://github.com/paohaijiao">JQuick</a> 开源生态成员 —— 该生态已有 5 个项目被 <a href="https://github.com/akullpp/awesome-java">Awesome Java</a> 收录。
</p>

---

## 1. 项目简介

JQuick-SQL 是面向 Java 应用的 SQL AST 语法解析与 SQL 逻辑转换工具库。它通过手写的语法将 SQL 文本解析为强类型的 AST，再把 AST 转换为逻辑计划与物理计划，经规则化改写器优化后，在进程内对已注册的数据集执行。全程无需数据库服务、无需 JDBC 驱动，也不依赖字符串层面的正则改写。

整个库围绕四个阶段构建：

- **解析（Parse）** —— SQL 文本经 词法/语法分析器生成语法树，再转换为强类型 AST（`JQuickQueryNode` 及 30+ 节点类型）。
- **转换（Transform）** —— AST 转换为逻辑计划，经 12 条优化规则改写后下推为物理计划。
- **组装（Assemble）** —— SQL 语句可放在 XML 文件中，通过 `#{param}` 占位符绑定到 Java 接口。
- **执行（Execute）** —— 计划在嵌入式引擎中、面向你注册的数据集运行（内存行数据，或其他 JQuick 组件产出的行数据）。

| 应用场景 | JQuick-SQL 提供的能力 |
|----------|----------------------|
| 老旧系统 SQL 切割 | 把现存语句解析为 AST，再按子句遍历与切分，而不是按字符串查找。 |
| 跨数据库兼容 | 单一语法、单一 AST；方言差异在 AST/计划层面处理，而非零散的文本替换。 |
| SQL 审计与治理 | 以强类型 AST 节点检查表、列、谓词与函数调用 —— 无需连接真实数据库。 |
| 动态条件组装 | XML 驱动的 SQL，`#{param}` 占位符绑定到 Java 接口方法。 |
| 进程内分析 | 注册数据集即可运行 `SELECT` / `JOIN` / `GROUP BY`，无需部署数据库。 |

---

## 2. 核心特性

**解析层**

- 生成词法/语法分析器（`JQuickSQLLexer`、`JQuickSQLParser`）。
- 强类型 AST 模型，30+ 节点类：select 规格、from/join/where/group by/having/order by/limit 子句、集合运算、子查询、CTE、case-when、函数调用。
- 基于 Visitor 的遍历（`JQuickSQLVisitor`、`JQuickSQLCommonVisistor`、`JQuickSQLSelectSpecVisitor`），并提供 listener API 用于自定义分析。
- `JQuickSQLExecutor` —— 一行代码把 SQL 字符串转换为 `JQuickQueryNode` AST。

**逻辑转换**

- AST → 逻辑计划转换（`JQuickASTToLogicalPlanVisitor`）。
- 12 条规则化优化器实现：谓词下推、投影下推、投影合并、过滤合并、冗余过滤消除、常量折叠、表达式化简、limit 下推、聚合下推、join 重排序、子查询转 join、分布式优化。
- 逻辑 → 物理计划下推（`JQuickPhysicalPlanGenerator`）。
- 16 种物理节点类型：表扫描、过滤、投影、values、empty、排序、top-N、limit、hash join、nested-loop join、hash 聚合、exchange、集合运算、递归 union、窗口、抽象基类。

**执行层**

- `JQuickSQL` 门面类，支持嵌入式模式：`JQuickSQL.embedded()` / `embedded(n)` / `builder()`。
- Fragmenter 将物理计划切分为 fragment；coordinator 负责派发与结果收集。
- 表达式求值器覆盖比较、逻辑、算术、`LIKE` / `REGEXP`、`IN`、`BETWEEN`、`CASE WHEN`、子查询与函数调用。
- 可扩展的函数 SPI —— 自定义函数通过 `ServiceLoader` 发现。

**集成能力**

- iBatis 风格的 XML 动态 SQL 代理：SQL 写在 XML 中，接口由 JDK 动态代理实现，使用 `#{param}` 占位符。
- 内置 DTD 用于 XML 校验（`paohaijiao/dtd/Jquick-sql.dtd`）。
- 执行层的 Protobuf/gRPC 定义（`jquick-sql-proto`）。

---

## 3. 环境依赖

| 项目 | 要求 |
|------|------|
| JDK | 1.8 及以上（`java.version=1.8`） |
| 构建工具 | Maven 3.x |
| 执行层 | gRPC 1.80.0 + protobuf-java 3.21.12 + grpc-netty-shaded |
| 序列化 | snappy-java 1.1.10.5 |
| JQuick 依赖 | javelin-core、jquick-grpc、jquick-banner、jquick-path、jquick-xmlProxy、jquick-transform-function |

---

## 4. 快速开始

### 4.1 Maven

```xml
<dependency>
    <groupId>io.github.paohaijiao</groupId>
    <artifactId>jquick-sql-runtime</artifactId>
    <version>5.0.0</version>
</dependency>
```

`jquick-sql-runtime` 是入口构件，会传递依赖 `jquick-sql-query`、`jquick-sql-plan`、`jquick-sql-parser` 与 `jquick-sql-core`。

### 4.2 Gradle

```groovy
implementation 'io.github.paohaijiao:jquick-sql-runtime:5.0.0'
```

### 4.3 解析一条 SQL

```java
package demo;

import com.github.paohaijiao.ast.JQuickQueryNode;
import com.github.paohaijiao.executor.JQuickSQLExecutor;

/**
 * 用途：将 SQL 字符串解析为强类型 AST。
 * 场景：SQL 审计、语句切割、自定义静态分析。
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

### 4.4 执行一次查询

```java
package demo;

import com.github.paohaijiao.engine.JQuickSQL;
import com.github.paohaijiao.statement.JQuickColumnMeta;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;

import java.util.Arrays;
import java.util.List;

/**
 * 用途：注册内存数据集并对其执行 SQL。
 * 场景：单元测试、离线报表计算、进程内分析。
 * 注意：嵌入式 worker 会占用本机 19001 起的端口，务必调用 shutdown()。
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

## 5. 代码示例

### 5.1 遍历 AST 并检查各子句

```java
package demo;

import com.github.paohaijiao.ast.JQuickQueryNode;
import com.github.paohaijiao.ast.JQuickSelectClauseNode;
import com.github.paohaijiao.ast.JQuickSelectExpressionNode;
import com.github.paohaijiao.executor.JQuickSQLExecutor;

/**
 * 用途：解析 SQL 并通过强类型访问器检查 AST。
 * 场景：SQL 审计 —— 在语句触达数据库之前采集各子句的使用情况。
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

### 5.2 使用 XML 代理组装动态 SQL

SQL 存放在 XML 中；接口方法调用在运行时绑定 `#{param}`。下文使用的 `jquick-sql.xml` 需位于 classpath 下。

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
 * 用途：把 XML 中声明的 SQL 绑定到 Java 接口。
 * 场景：将 SQL 与 Java 代码分离的老式 iBatis 风格项目。
 * 注意：XML 的 namespace 必须与接口全类名一致，方法参数通过 @Param 绑定到 #{...} 占位符。
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

### 5.3 通过 SPI 添加自定义函数

第 1 步 —— 继承 `JQuickBaseFunctionFunctionProvider`。构造函数的两个参数分别是 SQL 中的函数名与函数说明。

```java
package demo.function;

import com.github.paohaijiao.function.domain.JQuickBaseFunctionFunctionProvider;

import java.util.List;

/**
 * SQL 用法：TO_CAMEL_CASE(name, '_')
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

第 2 步 —— 通过 `ServiceLoader` 注册。创建以 SPI 接口全类名精确命名的文件：

```
src/main/resources/META-INF/services/com.github.paohaijiao.function.core.JQuickMethodFunctionProvider
```

每行写一个实现类：

```
demo.function.ToCamelCaseFunction
```

第 3 步 —— 在 SQL 中使用：

```java
JQuickDataSet result = sql.execute(
        "SELECT name, toCamelCase(name, '_') AS camel FROM users"
);
```

### 5.4 使用 Builder 配置引擎

```java
package demo;

import com.github.paohaijiao.engine.JQuickSQL;
import com.github.paohaijiao.statement.JQuickColumnMeta;
import com.github.paohaijiao.statement.JQuickRow;

import java.util.Arrays;
import java.util.List;

/**
 * 用途：使用流式 Builder 构建引擎。
 * 场景：代码优先的配置方式、批处理任务、多租户引擎构建。
 * 注意：当引擎对接外部已启动的 worker 时，使用 config(JQuickSqlConfig) 配合 worker(host, port)；
 *       在嵌入式模式下，引擎会自行构建默认运行时配置。
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

### 5.5 支持的 SQL 语法

```sql
-- 投影、别名、表达式、CASE WHEN、DISTINCT
SELECT DISTINCT dept,
       CASE WHEN salary >= 30000 THEN 'HIGH' ELSE 'LOW' END grade
  FROM emp;

-- WHERE：比较、AND/OR/NOT、BETWEEN、IN、LIKE、REGEXP、IS NULL、EXISTS
SELECT id, name FROM users
 WHERE age > 25 AND status = 'active'
   AND name LIKE '%a%' AND name REGEXP '^A.*'
   AND addr IS NOT NULL
   AND id IN (SELECT user_id FROM orders);

-- join：INNER / LEFT / RIGHT / CROSS / NATURAL
SELECT u.name, o.amount FROM users u LEFT JOIN orders o ON u.id = o.user_id;

-- 聚合与过滤
SELECT dept, COUNT(*) c, AVG(salary) avg_sal
  FROM emp GROUP BY dept HAVING COUNT(*) >= 2 ORDER BY c DESC;

-- 分页
SELECT name FROM users ORDER BY age DESC LIMIT 2, 3;

-- 集合运算
SELECT name FROM list_a UNION     SELECT name FROM list_b;
SELECT name FROM list_a MINUS     SELECT name FROM list_b;
SELECT name FROM list_a INTERSECT SELECT name FROM list_b;
```

---

## 6. 支持数据库

JQuick-SQL 是语法层面的 SQL 解析器与进程内执行引擎，不提供 JDBC 方言层与数据库驱动：SQL 文本被解析为 AST，数据则由你的应用注册为数据集（内存行数据，或其他 JQuick 组件产出的行数据）。

| SQL 能力 | 可接受的语法 |
|----------|--------------|
| 投影 | `SELECT *`、列清单、别名、算术、`CASE WHEN`、`DISTINCT`、标量子查询 |
| 过滤 | `=`、`<>`、`>`、`>=`、`<`、`<=`、`AND` / `OR` / `NOT`、`IS [NOT] NULL`、`BETWEEN`、`IN`、`LIKE`、`REGEXP`、`EXISTS` |
| 连接 | `INNER`、`LEFT`、`RIGHT`、`CROSS`、`NATURAL` |
| 聚合 | `COUNT`、`SUM`、`AVG`、`MIN`、`MAX`，配合 `GROUP BY` 与 `HAVING` |
| 排序 | `ORDER BY col [ASC\|DESC]`、多字段排序、按表达式排序 |
| 分页 | `LIMIT n`、`LIMIT offset, n` |
| 集合运算 | `UNION`、`MINUS`、`INTERSECT` |
| 子查询 | 可用于 `WHERE` / `SELECT` / `HAVING` / `ORDER BY` / `FROM` / `JOIN`，支持嵌套与多列 |

说明：

- 不支持 `FULL OUTER JOIN`。
- 数据源连接不属于本库职责；数据集通过 `registerTable(...)` 注册。

---

## 7. 模块说明

| 模块 | 打包方式 | 职责 |
|------|----------|------|
| `jquick-sql-core` | jar | 配置（`JQuickSqlConfig`、`JQuickSqlRuntimeConfig`）、枚举、数据源注册、执行统计、工具类 |
| `jquick-sql-parser` | jar | 生成的词法/语法分析器、AST 节点模型、visitor、`JQuickSQLExecutor` |
| `jquick-sql-plan` | jar | AST → 逻辑计划、12 条优化规则、逻辑 → 物理计划、物理节点实现、物理计划优化器 |
| `jquick-sql-proto` | jar | 执行层的 Protobuf 消息与 gRPC 服务桩（`physical.proto`） |
| `jquick-sql-query` | jar | 执行层：fragmenter、exchange 节点、coordinator、worker、分区管理、表达式求值器 |
| `jquick-sql-runtime` | jar | 入口 `JQuickSQL`、XML 动态代理、SPI 函数提供者、XML DTD；依赖 `jquick-sql-query` |

在你的项目中只需引入 `jquick-sql-runtime`，其余模块会传递依赖解析。

---

## 8. 兼容性说明

| 项目 | 说明 |
|------|------|
| JDK | 支持 8 及以上；构建目标为 `1.8` |
| 嵌入式引擎 | `embedded(n)` 会在 `19001..19000+n` 端口启动 `n` 个本地 worker；请在 `finally` 块中调用 `shutdown()` |
| 列元数据 | `JQuickColumnMeta` 的第三个参数是表别名；SQL 中的别名必须与之匹配，否则列解析失败 |
| 布尔列 | 请声明为 `Boolean.class`；字符串 `"true"` / `"false"` 不会被自动转换 |
| 线程安全 | `JQuickSQL` 可安全共享；`JQuickDataSet` 非线程安全 |
| XML 代理 | `<sqls namespace>` 必须等于所绑定接口的全类名；DTD 已内置在 `classpath:paohaijiao/dtd/Jquick-sql.dtd` |
| 自定义函数 | `META-INF/services/` 下的文件名必须等于 SPI 接口全类名 `com.github.paohaijiao.function.core.JQuickMethodFunctionProvider` |
| MyBatis 集成 | 未提供专用的 MyBatis / MyBatis-Plus 适配器；内置的 XML 动态代理为 iBatis 风格，可独立使用 |
| 不支持的 SQL | `FULL OUTER JOIN` |

---

## 9. 开源协议

JQuick-SQL 基于 [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt) 发布。

```
Copyright (c) 2025-2099 Martin (goudingcheng@gmail.com)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0
```

---

## 10. 参与贡献

1. Fork 仓库并创建分支：`feature/xxx`、`fix/issue-123` 或 `docs/xxx`。
2. 提交前确保 `mvn clean test` 通过。
3. 保持模块边界清晰 —— 语法变更放在 `jquick-sql-parser`，优化规则放在 `jquick-sql-plan`。
4. 新增能力必须附带 `src/test/java` 下可运行的测试或示例类。
5. PR 标题遵循 `[module] short description`，例如 `[optimizer] Extend projection pushdown to derived tables`。

仓库地址：<https://github.com/paohaijiao/jquick-sql>
