# JQuick-SQL · 嵌入式 SQL 查询引擎

> 🏠 组织：[paohaijiao](https://github.com/paohaijiao) · 🧩 生态：[【JQuick 生态导航】](#三jquick-生态导航) · 📜 协议：**Apache-2.0** ✅ 免费商用
>
> 英文名：JQuick-SQL · Embedded SQL Query Engine · JDK 8+ · Maven `io.github.paohaijiao:jquick-sql:4.1.0`

JQuick-SQL 是一款 **纯 Java、嵌入式运行、无需部署服务** 的轻量级 SQL 查询引擎。基于 ANTLR4 自研 Parser，支持 `SELECT / WHERE / JOIN / GROUP BY / HAVING / UNION / MINUS / INTERSECT / 子查询 / CASE WHEN` 等语法；搭配 `jquick-curl`、`jquick-excel` 等生态组件，可在**同一 JVM 内**对内存数据与外部异构源执行 JOIN 与聚合——**不搬迁数据、不写中间件、一条 SQL 搞定**。

<div align="center">

[![GitHub Stars](https://img.shields.io/github/stars/paohaijiao/jquick-sql?style=flat-square&logo=github)](https://github.com/paohaijiao/jquick-sql/stargazers)
[![GitHub Forks](https://img.shields.io/github/forks/paohaijiao/jquick-sql?style=flat-square&logo=github)](https://github.com/paohaijiao/jquick-sql/forks)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.paohaijiao/jquick-sql?style=flat-square)](https://search.maven.org/artifact/io.github.paohaijiao/jquick-sql)
[![Java](https://img.shields.io/badge/Java-8%2B-ED8B00?style=flat-square&logo=java)](https://adoptium.net/)
[![ANTLR](https://img.shields.io/badge/ANTLR4-4.x-success?style=flat-square&logo=antlr)](https://www.antlr.org/)
[![License](https://img.shields.io/badge/License-Apache--2.0-yellowgreen?style=flat-square)](LICENSE)
[![Last Commit](https://img.shields.io/github/last-commit/paohaijiao/jquick-sql?style=flat-square)](https://github.com/paohaijiao/jquick-sql/commits)

**国内 Gitee 镜像 →** [paohaijiao / jquick-sql · Gitee](https://gitee.com/paohaijiao/jquick-sql)

</div>

---

## 一、项目简介

### 1.1 典型痛点与 JQuick-SQL 方案

| 业务痛点 | JQuick-SQL 怎么解决 |
|---------|-------------------|
| 报表跨 MySQL / Oracle / Excel 取数，手工写 N 个 DAO 再用 Java 拼数据 | 一条 SQL 直接 `JOIN` 所有已注册内存表，引擎自动完成谓词与合并聚合 |
| 老项目 Tomcat 7 + iBatis + JDK 8 升级成本高 | 保留 iBatis 风格 XML 动态代理；最低 JDK 8，单应用无服务化，一个 jar 嵌入即可 |
| 批量统计任务单线程慢，CPU 利用率上不去 | `embedded(n)` 在**同一 JVM 内**启动 n 个并行 Worker，通过 Fragment 切分并行执行 |
| 数据中台/湖仓一体太重，业务要快速出数 | 嵌入式 JVM 内完成 ETL：`读→算→写` 一个 jar 包搞定 |
| SQL 方言能力弱，业务 SQL 复杂 CASE/子查询写不出来 | Parser 支持 SELECT 子句/ WHERE / JOIN / GROUP BY / HAVING / ORDER BY / LIMIT / UNION / MINUS / INTERSECT / 子查询 / CASE WHEN 等完整语法 |

### 1.2 架构图

```
┌──────────────────────────────────────────────────────────────────┐
│                     JQuick-SQL Engine (嵌入式 JVM 内)            │
├──────────────────────────────────────────────────────────────────┤
│  SQL Input → Parser(ANTLR4 AST) → Logical Plan → Optimizer      │
│                                           ↓                       │
│                                   Physical Plan                  │
│                              (HashJoin/Sort/TopN/Agg)            │
│                                           ↓                       │
│                       Fragmenter → 并行 Worker(n) 执行            │
│                              (同一 JVM 内，embedded(n) 指定)       │
│                                           ↓                       │
│                           JQuickDataSet (打印/导出/再查询)         │
└──────────────────────────────────────────────────────────────────┘
```

---

## 二、快速开始（3 分钟跑通）

### 2.1 Maven 依赖

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

### 2.3 Hello World（完整可运行，含 main）

```java
package demo;

import com.github.paohaijiao.engine.JQuickSQL;
import com.github.paohaijiao.statement.JQuickColumnMeta;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;

import java.util.Arrays;
import java.util.List;

/**
 * 功能作用：以 embedded 嵌入式模式启动 JQuick-SQL，注册内存数据表并执行 SELECT。
 * 使用场景：本地单元测试、离线报表计算、ETL 原型验证、小工具脚本。
 * 注意事项：embedded() 会占用 19001+ 端口启动同 JVM 内并行 Worker；
 *           用完必须在 finally 中调用 shutdown() 释放资源。
 */
public class QuickStartDemo {

    public static void main(String[] args) {
        // 1) 创建嵌入式引擎；embedded(4) 可指定同 JVM 内并行 Worker 数
        JQuickSQL sql = JQuickSQL.embedded();
        try {
            // 2) 列元数据：列名 + Java 类型 + 表别名（JOIN 时必须匹配）
            List<JQuickColumnMeta> columns = Arrays.asList(
                    new JQuickColumnMeta("id",   Integer.class, "users"),
                    new JQuickColumnMeta("name", String.class,  "users"),
                    new JQuickColumnMeta("age",  Integer.class, "users")
            );

            // 3) 数据行：key-value 交替，顺序自动对应列
            List<JQuickRow> rows = Arrays.asList(
                    row("id", 1, "name", "Alice",   "age", 25),
                    row("id", 2, "name", "Bob",     "age", 30),
                    row("id", 3, "name", "Charlie", "age", 20)
            );

            // 4) 注册表
            sql.registerTable("users", columns, rows);

            // 5) 执行 SQL，打印可视化表格
            JQuickDataSet result = sql.execute(
                    "SELECT id, name, age FROM users WHERE age >= 25 ORDER BY age DESC"
            );
            result.printTable();

            System.out.println("总行数：" + result.size());

        } finally {
            // 6) 关闭引擎，释放端口
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

## 三、JQuick 生态导航

> 点击下表即可跳转到对应子项目。所有项目共享 `io.github.paohaijiao` groupId。
> 协议：Apache-2.0（✅ 免费商用），**除 jquick-pdf 为 AGPL-3.0（⚠️ 商用需授权）**。

| # | 项目名称 | 仓库地址 | 简介 | 开源协议 |
|---|---------|---------|------|---------|
| 1 | **jquick-sql** ⭐ | [paohaijiao/jquick-sql](https://github.com/paohaijiao/jquick-sql) | 嵌入式 SQL 查询引擎（当前项目） | Apache-2.0 |
| 2 | jquick-gateway | [paohaijiao/jquick-gateway](https://github.com/paohaijiao/jquick-gateway) | 基于 Netty 的轻量级 API 网关（路由 / 限流 / 熔断 / 灰度） | Apache-2.0 |
| 3 | jquick-excel | [paohaijiao/jquick-excel](https://github.com/paohaijiao/jquick-excel) | Excel 读写工具；大文件 SAX 流式；老项目 POI 3.x 兼容 | Apache-2.0 |
| 4 | jquick-pdf | [paohaijiao/jquick-pdf](https://github.com/paohaijiao/jquick-pdf) | 基于 iText7 的 PDF 处理（模板渲染 / 签章 / 水印 / 合并拆分） | **AGPL-3.0 ⚠️** |
| 5 | jquick-asm | [paohaijiao/jquick-asm](https://github.com/paohaijiao/jquick-asm) | ASM 9.x 字节码增强；AOP 代理 / 动态 Bean / 类转换 | Apache-2.0 |
| 6 | jquick-curl | [paohaijiao/jquick-curl](https://github.com/paohaijiao/jquick-curl) | HTTP 客户端（链式 Fluent API / 连接池 / 断点续传 / 重试） | Apache-2.0 |
| 7 | jquick-java | [paohaijiao/jquick-java](https://github.com/paohaijiao/jquick-java) | ANTLR4 脚本引擎 + XML 动态代理；规则引擎热加载 | Apache-2.0 |

> 📌 **跨项目组合示例**：jquick-sql + jquick-curl → 将 REST JSON 数据注册为内存表后联邦查询；jquick-sql + jquick-excel → 读取 Excel 行后与业务表 JOIN；更多见各项目 README。

---

## 四、支持的 SQL 语法

> 本节所列能力均来自 `src/test/java/com/github/paohaijiao/demo/` 下真实单元测试套件，用户可直接查看对应目录下的 `*Test.java` 获取更多示例。

### 4.1 SELECT 子句（Projection / Project）

支持形式：
- `SELECT *`：全列投影；
- 列列表：`SELECT id, name, age`；
- 列别名：`SELECT name AS username`；
- 常量与算术表达式：`SELECT 1, 2+3, salary*12 annual`；
- 函数表达式：`SELECT toUpper(name) uname, ROUND(AVG(salary),2)`；
- `CASE WHEN ... THEN ... ELSE ... END`；
- `DISTINCT` 去重：`SELECT DISTINCT dept`；
- 标量子查询：`SELECT name, (SELECT COUNT(*) FROM orders o WHERE o.user_id=u.id) cnt FROM users u`。

示例：
```sql
SELECT DISTINCT dept,
       CASE WHEN salary>=30000 THEN '高'
            WHEN salary>=20000 THEN '中'
            ELSE '低' END grade,
       salary * 12 annual
FROM emp;
```
对应测试：`demo/project/JQuickSQLProjectTest.java`。

### 4.2 WHERE 条件过滤

支持形式：
- 比较运算：`=`, `<>`, `>`, `>=`, `<`, `<=`；
- 逻辑运算：`AND` / `OR` / `NOT` + 嵌套括号；
- 布尔常量：`WHERE true`、`WHERE 1=1`；
- 布尔列：`WHERE enable`（列类型必须是 `Boolean.class`）；
- 函数结果比较：`WHERE toUpper(name)='ALICE'`；
- 空值判断：`IS NULL` / `IS NOT NULL`；
- 范围：`BETWEEN a AND b` / `NOT BETWEEN a AND b`；
- 集合：`IN (v1,v2,...)` / `NOT IN (...)`；
- 模糊：`LIKE '%x%'` / `NOT LIKE`；
- 正则：`REGEXP '^A.*'` / `NOT REGEXP`；
- 存在性：`EXISTS (子查询)`。

示例：
```sql
SELECT id, name FROM users
 WHERE age > 25 AND status = 'active'
   AND addr IN ('beijing','shanghai')
   AND name LIKE '%Davi%'
   AND name REGEXP '^A.*'
   AND addr IS NOT NULL;
```
对应测试：`demo/where/JQuickSQLWhereTest.java`。

### 4.3 JOIN 多表关联

支持形式（5 种）：
- `INNER JOIN ... ON ...`：内连接；
- `LEFT [OUTER] JOIN ... ON ...`：左外连接；
- `RIGHT [OUTER] JOIN ... ON ...`：右外连接；
- `CROSS JOIN`：笛卡尔积；
- `NATURAL JOIN`：按同名列自然连接。

注意：`FULL OUTER JOIN` 当前不支持（demo 中已显式 try/catch 验证会抛出不支持异常）。

示例：
```sql
SELECT u.name, o.amount
  FROM users u LEFT JOIN orders o ON u.id = o.user_id;
```
对应测试：`demo/joinClause/JQuickSQLJoinTest.java`。

### 4.4 GROUP BY + HAVING + 聚合函数

支持形式：
- `GROUP BY col1, col2, ...`：按单列或多列分组；
- `HAVING agg_expr condition`：对聚合结果再过滤；
- 聚合函数：`COUNT(*)` / `COUNT(col)` / `AVG(col)` / `SUM(col)` / `MAX(col)` / `MIN(col)` / `ROUND(expr, scale)`。

示例：
```sql
SELECT dept, COUNT(*) c, AVG(salary) avg_s
  FROM emp
 GROUP BY dept
HAVING COUNT(*) >= 2
 ORDER BY c DESC;
```
对应测试：`demo/groupby/JQuickSQLGroupByTest.java`、`demo/aggregation/JQuickSQLAggregateTest.java`。

### 4.5 ORDER BY 排序

支持形式：
- `ORDER BY col ASC` / `DESC`：单列升降序；
- `ORDER BY col1, col2 DESC`：多字段排序；
- `ORDER BY CASE WHEN ... THEN ... END`：表达式排序；
- 布尔列排序：`ORDER BY enable DESC`。

示例：
```sql
SELECT name, salary, dept
  FROM emp
 ORDER BY dept ASC, salary DESC;
```
对应测试：`demo/orderBy/JQuickSQLOrderByTest.java`。

### 4.6 LIMIT 分页

支持两种 MySQL 风格：
- `LIMIT n`：取前 n 行；
- `LIMIT offset, n`：跳过 offset 行，取 n 行。

示例：
```sql
SELECT name FROM users ORDER BY age DESC LIMIT 2, 3;
```
对应测试：`demo/limitClause/JQuickSQLLimitTest.java`。

### 4.7 集合运算：UNION / MINUS / INTERSECT

支持形式：
- `SELECT ... UNION     SELECT ...`：合并去重；
- `SELECT ... MINUS     SELECT ...`：差集（A 有 B 无）；
- `SELECT ... INTERSECT SELECT ...`：交集。

示例：
```sql
-- 交集：同时出现在两份名单中的用户
SELECT name FROM list_a  INTERSECT  SELECT name FROM list_b;
```
对应测试：`demo/union/JQuickSQLUnionTest.java`。

### 4.8 子查询

子查询位置均已支持：
- `WHERE` 中的相关子查询与非相关子查询：`IN (子查询)` / `EXISTS (子查询)` / 比较运算 + 标量子查询；
- `SELECT` 列表中的标量子查询；
- `HAVING` 中的子查询；
- `ORDER BY` 中的子查询；
- `FROM` 后的派生表（inline view）：`SELECT * FROM (SELECT ...) t`；
- `JOIN` 中的子查询：`SELECT * FROM t1 JOIN (SELECT ...) t2 ON ...`；
- 嵌套子查询与多列子查询。

示例：
```sql
SELECT u.name,
       (SELECT COUNT(*) FROM orders o WHERE o.user_id = u.id) order_cnt
  FROM users u
 WHERE EXISTS (SELECT 1 FROM orders o WHERE o.user_id = u.id);
```
对应测试：`demo/subquery/JQuickSQLSubqueryTest.java`。

### 4.9 表达式与自定义函数

- 内置字符串/数学函数可直接在 SELECT 与 WHERE 中使用；
- 自定义函数：通过 `jquick-transform-function` SPI 扩展，示例中使用 `toUpper(name)` 即为此类扩展；
- 算术运算：`+ - * /`，字符串拼接可在 SELECT 列表中使用。

### 4.10 XML 动态代理与 Builder API

- XML 动态代理（iBatis 风格）：把 SQL 写在 classpath XML 中，通过 JDK 动态代理生成 Service/DAO 实现，支持 `#{param}` 占位符（见下文 Demo05）；
- Builder 流式 API：`JQuickSQL.builder().embedded(n).config(cfg).table(name,cols,rows).build()`（见下文 Demo06）。

---

## 五、Demo 示例

> 每个案例都是**完整独立可运行**的 Java 类，可直接复制到 `src/test/java/demo/` 运行。
> 更多案例见仓库 `src/test/java/com/github/paohaijiao/demo/` 目录。

---

### 案例 1：WHERE 条件过滤 + 函数表达式

#### 功能说明
支持 `AND/OR/NOT`、比较、`BETWEEN`、`IN`、`LIKE`、`REGEXP`、`IS NULL`、`EXISTS`，并可在条件与字段中使用 `toUpper()` 等 SPI 扩展函数。适用于报表过滤、名单筛选、数据质量校验。

#### 完整 Demo 代码

```java
package demo;

import com.github.paohaijiao.engine.JQuickSQL;
import com.github.paohaijiao.statement.JQuickColumnMeta;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;

import java.util.Arrays;
import java.util.List;

/**
 * 功能作用：演示等值/范围/模糊/正则/函数/空值 6 类典型 WHERE 条件。
 * 使用场景：ETL 清洗、名单筛选、动态条件报表。
 * 注意事项：enable 字段类型为 Boolean.class；toUpper() 由 jquick-transform-function SPI 提供。
 */
public class Demo01WhereCondition {

    public static void main(String[] args) {
        JQuickSQL sql = JQuickSQL.embedded();
        try {
            registerUsers(sql);

            System.out.println("=== 1) active 且 年龄 > 25 ===");
            sql.execute("SELECT id,name,age,status FROM users " +
                    "WHERE age > 25 AND status = 'active'").printTable();

            System.out.println("=== 2) 年龄 BETWEEN 20 AND 30 ===");
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

### 案例 2：多表 JOIN + EXISTS 子查询

#### 功能说明
支持 `INNER / LEFT / RIGHT / CROSS / NATURAL` 五种 JOIN；`EXISTS / IN (子查询)` 语法。适用于订单-用户联查、主从数据对照。注意：FULL OUTER JOIN 当前不支持。

#### 完整 Demo 代码

```java
package demo;

import com.github.paohaijiao.engine.JQuickSQL;
import com.github.paohaijiao.statement.JQuickColumnMeta;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;

import java.util.Arrays;
import java.util.List;

/**
 * 功能作用：演示 INNER / LEFT / RIGHT JOIN + EXISTS 子查询 + JOIN 聚合。
 * 使用场景：订单-用户联表、缺失数据补齐（LEFT JOIN）、名单对照。
 * 注意事项：两表关联字段类型需一致（Integer↔Integer）；否则会按 Object 比较导致不匹配。
 */
public class Demo02JoinAndSubquery {

    public static void main(String[] args) {
        JQuickSQL sql = JQuickSQL.embedded(2);   // 同 JVM 内 2 个并行 Worker
        try {
            registerUsers(sql);
            registerOrders(sql);

            System.out.println("===== INNER JOIN：有订单的用户 =====");
            sql.execute("SELECT u.id,u.name,o.id order_id,o.amount " +
                    "FROM users u INNER JOIN orders o ON u.id=o.user_id " +
                    "ORDER BY u.id, o.id").printTable();

            System.out.println("===== LEFT JOIN：所有用户（含未下单） =====");
            sql.execute("SELECT u.name, o.amount FROM users u " +
                    "LEFT JOIN orders o ON u.id = o.user_id").printTable();

            System.out.println("===== RIGHT JOIN：所有订单 + 对应用户 =====");
            sql.execute("SELECT u.name, o.id, o.amount FROM users u " +
                    "RIGHT JOIN orders o ON u.id = o.user_id").printTable();

            System.out.println("===== EXISTS：有订单的用户 =====");
            sql.execute("SELECT u.id, u.name FROM users u WHERE EXISTS (" +
                    "SELECT 1 FROM orders o WHERE o.user_id = u.id)" +
                    " ORDER BY u.id").printTable();

            System.out.println("===== 聚合 JOIN：每个用户订单总额 =====");
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
                row("id", 105, "user_id", 99, "amount", 99.99)); // 不存在的用户
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

### 案例 3：GROUP BY + HAVING + 分页（ORDER BY + LIMIT）

#### 功能说明
内存聚合 + HAVING 过滤 + 多字段排序 + MySQL 风格 `LIMIT offset, size` 分页；配合 embedded(n) 在同 JVM 内并行完成大表计算。

#### 完整 Demo 代码

```java
package demo;

import com.github.paohaijiao.engine.JQuickSQL;
import com.github.paohaijiao.statement.JQuickColumnMeta;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;

import java.util.Arrays;
import java.util.List;

/**
 * 功能作用：COUNT/SUM/AVG/MIN/MAX + HAVING + CASE WHEN 分组 + 多字段排序 + 分页。
 * 使用场景：BI 图表数据、排行榜、列表分页。
 * 注意事项：LIMIT 同时支持 `LIMIT n` 与 `LIMIT offset, n`。
 */
public class Demo03GroupByPageOrder {

    public static void main(String[] args) {
        JQuickSQL sql = JQuickSQL.embedded();
        try {
            registerEmployees(sql);

            System.out.println("===== 部门维度统计 =====");
            sql.execute("SELECT dept, COUNT(*) emp_cnt, ROUND(AVG(salary),2) avg_sal," +
                    " SUM(salary) sum_sal FROM emp GROUP BY dept ORDER BY sum_sal DESC").printTable();

            System.out.println("===== HAVING：只保留人数>=2 的部门 =====");
            sql.execute("SELECT dept, COUNT(*) c FROM emp GROUP BY dept " +
                    "HAVING COUNT(*) >= 2 ORDER BY c DESC").printTable();

            System.out.println("===== CASE WHEN 分段 + 多列排序 =====");
            sql.execute("SELECT name, salary, dept, CASE " +
                    "WHEN salary>=30000 THEN '高' WHEN salary>=20000 THEN '中' ELSE '低' END grade " +
                    "FROM emp ORDER BY dept ASC, salary DESC").printTable();

            System.out.println("===== 分页：按工资倒序，跳过前 2 取 3 条 =====");
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
                row("id",1,"name","Alice", "dept","研发",  "salary",32000),
                row("id",2,"name","Bob",   "dept","研发",  "salary",28000),
                row("id",3,"name","Carol", "dept","市场",  "salary",22000),
                row("id",4,"name","David", "dept","市场",  "salary",18000),
                row("id",5,"name","Eve",   "dept","产品",  "salary",25000),
                row("id",6,"name","Frank", "dept","研发",  "salary",35000),
                row("id",7,"name","Grace", "dept","人力",  "salary",15000));
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

### 案例 4：集合运算 UNION / MINUS / INTERSECT

#### 功能说明
支持合并（UNION 去重）、差集（MINUS）、交集（INTERSECT）三种集合运算；两边 SELECT 列数与列类型需对齐。适用于名单合并、差异比对、共同项筛选。

#### 完整 Demo 代码

```java
package demo;

import com.github.paohaijiao.engine.JQuickSQL;
import com.github.paohaijiao.statement.JQuickColumnMeta;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;

import java.util.Arrays;
import java.util.List;

/**
 * 功能作用：演示 UNION（合并去重）、MINUS（差集）、INTERSECT（交集）三种集合运算。
 * 使用场景：名单合并、两批数据差异比对、同时出现在两份清单中的共同项筛选。
 * 注意事项：两边 SELECT 的列数与对应列的 Java 类型需保持一致；
 *           UNION 自动去重，不去重的 UNION ALL 当前未支持。
 */
public class Demo04SetOperations {

    public static void main(String[] args) {
        JQuickSQL sql = JQuickSQL.embedded();
        try {
            registerListA(sql);
            registerListB(sql);

            System.out.println("===== UNION：A 名单 + B 名单 合并去重 =====");
            sql.execute("SELECT id, name FROM list_a " +
                        "UNION " +
                        "SELECT id, name FROM list_b " +
                        "ORDER BY id").printTable();

            System.out.println("===== MINUS：只在 A 中出现、不在 B 中出现的用户 =====");
            sql.execute("SELECT id, name FROM list_a " +
                        "MINUS " +
                        "SELECT id, name FROM list_b " +
                        "ORDER BY id").printTable();

            System.out.println("===== INTERSECT：同时出现在 A 与 B 中的用户 =====");
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
                row("id", 4, "name", "David"));      // A 独有的 David
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

### 案例 5：XML 动态 SQL 代理（iBatis 风格，老项目无缝迁移）

#### 功能说明
沿用遗留 iBatis / MyBatis 习惯，将 SQL 写在 XML 中，通过 JDK 动态代理生成 Service 接口实现，**老项目零侵入迁移**。

#### 完整 Demo 代码

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
 * 功能作用：将 SQL 写在 XML 文件中，通过 JDK 动态代理生成 DAO/Service 接口实现；支持 #{param} 占位。
 * 使用场景：老系统 iBatis 2.x / Struts 项目无缝迁移到 JQuick-SQL；SQL 集中管理。
 * 注意事项：XML 的 <sqls namespace="..."> 必须与代理接口全类名一致；
 *           DTD 路径 classpath:paohaijiao/dtd/Jquick-sql.dtd 已内置。
 *
 * <pre>{@code
 * <!-- classpath:jquick-sql.xml 示例 -->
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

    /** 业务接口：namespace 必须与 XML 完全一致（jquick-sql.xml 中写 demo.Demo05XmlProxy.UserService） */
    public interface UserService {
        List<JQuickRow> topUsers(int limit);
    }

    public static void main(String[] args) {
        JQuickSQL sql = JQuickSQL.embedded();
        try {
            // 1) 注册内存表
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

            // 2) 封装为 JQuickTable 交给 XML 代理工厂
            List<JQuickTable> tables = Arrays.asList(new JQuickTable("users", cols, rows));
            JQuickJavaXmlParseFactory factory = new JQuickJavaXmlParseFactory(tables);

            // 3) 生成接口代理，调用时自动根据 XML 里的 SQL 替换 #{limit}
            UserService service = (UserService) factory
                    .createlInvocationHandler()
                    .getProxy(UserService.class);

            List<JQuickRow> list = service.topUsers(2);
            System.out.println("===== 年龄 Top 2（取 XML SQL 执行结果）=====");
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

### 案例 6：Builder 模式 + 嵌入式并行配置

#### 功能说明
`JQuickSQL.builder()` 流式 API 配置并行度、超时重试、预注册表；`embedded(n)` 在同 JVM 内启动 n 个并行 Worker，适用于代码化配置、多租户引擎构造。

#### 完整 Demo 代码

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
 * 功能作用：流式 Builder 构造 JQuickSQL；可配置并行度、任务超时、重试次数、预注册表。
 * 使用场景：代码化配置、多租户引擎构造、批量任务统一构造。
 * 注意事项：示例使用 embedded(2) 在同 JVM 内启动 2 个并行 Worker，保证本 Demo 开箱即用。
 */
public class Demo06BuilderEmbedded {

    public static void main(String[] args) {
        // 1) 运行时配置
        JQuickSqlRuntimeConfig rt = new JQuickSqlRuntimeConfig();
        rt.setDefaultParallelism(2);
        rt.setMaxTaskRetries(3);
        rt.setTaskTimeoutMs(60_000);
        JQuickSqlConfig cfg = new JQuickSqlConfig();
        cfg.setRuntime(rt);

        // 2) 准备城市销售表
        List<JQuickColumnMeta> cols = Arrays.asList(
                new JQuickColumnMeta("city",   String.class, "sales"),
                new JQuickColumnMeta("amount", Long.class,   "sales"));
        List<JQuickRow> rows = Arrays.asList(
                row("city","北京","amount",1200L),
                row("city","上海","amount",2100L),
                row("city","广州","amount",980L),
                row("city","北京","amount",800L),
                row("city","成都","amount",650L),
                row("city","上海","amount",1300L));

        // 3) Builder 模式构造引擎（embedded(2) 同 JVM 内 2 个并行 Worker）
        JQuickSQL sql = JQuickSQL.builder()
                .embedded(2)
                .config(cfg)
                .table("sales", cols, rows)
                .build();

        try {
            System.out.println("===== 各城市 GMV 聚合 =====");
            JQuickDataSet r = sql.execute(
                    "SELECT city, SUM(amount) gmv, COUNT(*) order_cnt " +
                    "FROM sales GROUP BY city ORDER BY gmv DESC");
            r.printTable();
            System.out.println("已注册表：" + sql.getRegisteredTables());

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

## 六、核心特性

| 分类 | 能力 | 状态 |
|------|------|------|
| 🧩 SQL 语法 | SELECT 子句(*) / WHERE / ORDER BY / LIMIT / GROUP BY / HAVING / JOIN(5种: INNER/LEFT/RIGHT/CROSS/NATURAL) / UNION / MINUS / INTERSECT / 子查询(8种位置) / CASE WHEN / DISTINCT | ✅ |
| 🔍 函数 | 内置数学 / 字符串 + **SPI 自定义函数**（jquick-transform-function 扩展） | ✅ |
| 🧠 优化器 | 谓词下推 · 投影下推 · 常量折叠 · 过滤合并 等基础规则 | ✅ |
| 🚀 并行执行 | `embedded(n)` 同一 JVM 内 n 个并行 Worker；Fragment 切分；Hash/NestedLoop Join | ✅ |
| 🗂️ 数据源 | 内存注册表 + 生态组件接入：jquick-curl(REST JSON) / jquick-excel(Excel) / jquick-java(规则脚本)；更多 RDBMS 连接器在 jquick-connector 路线图中 | ✅ / 🗺️ |
| 🧓 老旧兼容 | JDK 8、XML 动态代理（iBatis 风格）、单 Worker 模式可嵌入 Tomcat 7 应用 | ✅ |
| 🔗 生态组合 | jquick-curl（REST 数据注册） · jquick-excel（Excel 行注册后 JOIN） · jquick-java（规则 SQL） | ✅ |

> 🗺️ 路线图：国产数据库 Dialect 适配（达梦 / 人大金仓 / OceanBase / GaussDB / TiDB）规划通过 jquick-connector 子项目统一发布，当前未内置。

---

## 七、依赖配置

### 7.1 完整 pom.xml 最小可运行模板

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
        <!-- JQuick-SQL 嵌入式 SQL 查询引擎 -->
        <dependency>
            <groupId>io.github.paohaijiao</groupId>
            <artifactId>jquick-sql</artifactId>
            <version>${jquick.version}</version>
        </dependency>

        <!-- 可选：REST JSON 数据注册为内存表 -->
        <dependency>
            <groupId>io.github.paohaijiao</groupId>
            <artifactId>jquick-curl</artifactId>
            <version>1.3.2</version>
        </dependency>

        <!-- 可选：Excel 数据注册为内存表 -->
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

### 7.2 jquick.properties（可选，放 classpath）

```properties
# Banner
jquick.banner.enabled=true
jquick.banner.slogan=JQuick-SQL · Just Query, Quickly!

# Runtime（同 JVM 内并行 Worker 配置）
jquick.runtime.defaultParallelism=4
jquick.runtime.maxFileSize=134217728
jquick.runtime.maxTaskRetries=3
jquick.runtime.taskTimeoutMs=60000
```

---

## 八、注意事项

| # | 说明 |
|---|------|
| 1 | `embedded(n)` 会占用 `19001 ~ 19000+n` 端口；防火墙需放开；`shutdown()` 必须在 `finally` 中调用。 |
| 2 | `JQuickColumnMeta` 第 3 个参数是表别名（tableAlias），多表 JOIN 时 SQL 里的别名必须与此一致，否则列解析失败。 |
| 3 | 布尔字段建议用 `Boolean.class`；若存 `String "true/false"`，`WHERE enable` 会按字符串处理导致报错。 |
| 4 | **Tomcat 7 / 老应用** 推荐 `embedded(1)` 单 Worker 模式；gRPC Netty Shaded 在老 JDK 8u 早期版本存在反射权限问题时，可替换为 `grpc-netty`。 |
| 5 | **国产数据库方言**：当前仅作为 jquick-connector 子项目路线图规划中，jquick-sql 核心包暂不内置对应方言与分页自动翻译。 |
| 6 | **协议边界**：jquick-sql 自身 Apache-2.0，免费商用；若业务同时使用 jquick-pdf，请单独阅读 AGPL-3.0 商业授权条款。 |
| 7 | `JQuickSQL` 单例即可（内部同步锁）；`JQuickDataSet` 非线程安全。 |
| 8 | 大 SQL / 大数据量请把 `taskTimeoutMs` 调大并适当提升 `embedded(n)` 的并行度（`Runtime.getRuntime().availableProcessors()` 做参考）。 |
| 9 | FULL OUTER JOIN 当前未支持；CTE / 递归 UNION / 窗口函数 未实现，请勿在生产 SQL 中使用。 |

---

## 九、贡献指南

> 参考 [paohaijiao 组织贡献规范](https://github.com/paohaijiao)，针对 JQuick-SQL 特殊补充：

1. Fork → 新建分支 `feature/xxx` / `fix/issue-123` / `docs/xxx`；
2. 本地 `mvn clean test` 全部通过后提交；
3. **新增或修改能力必须附带独立完整可运行 Demo**（放入 `src/test/java/com/github/paohaijiao/demo/` 目录，含 import + main 或 @Test + 注释头）；
4. 每条 Optimizer Rule 附带**正反向**测试（`src/test/java/com/github/paohaijiao/optimizer/*Test.java`）；
5. 并行执行相关 Bug 请提供 `embedded(2)` 最小复现用例；
6. PR 标题：`[模块] 一句话描述`，例：`[optimizer] ProjectionPushdown 支持子查询派生表`；
7. 合入后自动更新贡献者墙 🙌。

---

<div align="center">

**⬆️ 回到 [【JQuick 生态导航】](#三jquick-生态导航) · [Apache-2.0](LICENSE) · Made with ❤️ by [paohaijiao](https://github.com/paohaijiao)**

</div>
