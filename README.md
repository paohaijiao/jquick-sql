# JQuickSQL 引擎使用文档
简体中文 | [English](./README-EN.md)
## 项目状态
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![GitHub Stars](https://img.shields.io/github/stars/paohaijiao/jquick-sql.svg?style=social&label=Stars)](https://github.com/paohaijiao/jquick-sql)
[![GitHub Forks](https://img.shields.io/github/forks/paohaijiao/jquick-sql.svg?style=social&label=Forks)](https://github.com/paohaijiao/jquick-sql/fork)
[![Last Commit](https://img.shields.io/github/last-commit/paohaijiao/jquick-sql.svg)](https://github.com/paohaijiao/jquick-sql/commits/main)
[![Language](https://img.shields.io/github/languages/top/paohaijiao/jquick-sql.svg)](https://github.com/paohaijiao/jquick-sql)
## 目录
- [概述](#概述)
- [基本语法结构](#基本语法结构)
    - [SELECT 语句](#select-语句)
    - [FROM 子句](#from-子句)
    - [WHERE 子句](#where-子句)
    - [GROUP BY 与 HAVING](#group-by-与-having)
    - [ORDER BY 子句](#order-by-子句)
    - [LIMIT 子句](#limit-子句)
- [JOIN 操作](#join-操作)
- [集合操作](#集合操作)
    - [UNION](#union)
    - [MINUS](#minus)
    - [INTERSECT](#intersect)
- [OLAP 操作](#olap-操作)
    - [ROLLUP](#rollup)
    - [DRILLDOWN](#drilldown)
    - [SLICE](#slice)
    - [DICE](#dice)
    - [PIVOT](#pivot)
- [函数与表达式](#函数与表达式)
- [数据类型与字面量](#数据类型与字面量)
- [示例代码](#示例代码)
- [附录：语法规则摘要](#附录语法规则摘要)
## 概述
JQuickSQL 是一个轻量级 SQL 查询引擎，支持异构数据库,文件,restful 请求等进行关联查询，JOIN，提供了标准 SQL 语法和 OLAP
操作，适用于通用数据的查询和分析。
## 总体架构
### SQL 执行流程
1. 开始：打印 "=== SQL Execution Started ===" 和 SQL 语句
   ↓
2. 词法分析 (JQuickSQLLexer)
   ↓
3. 语法分析 (JQuickSQLParser)
   ↓
4. 构建 AST (buildAST)
   ↓
5. AST → 逻辑计划 (JQuickASTToLogicalPlanVisitor)
   ↓
6. 逻辑计划优化 (optimizer.optimize)
   ↓
7. 逻辑计划 → 物理计划 (physicalGenerator.generate)
   ↓
8. 物理计划 → 分布式计划 (fragmenter.fragment)
  - 打印分布式计划片段 (printFragments)
    ↓
9. 启动 Worker 发现服务 (workerManager.startDiscovery)
  - 注册 3 个 Worker 节点
    ↓
10. 任务调度 (JQuickTaskScheduler)
  - 调度策略: DATA_LOCALITY
  - 生成调度计划并打印 (printSchedulePlan)
    ↓
11. 启动 Workers 并执行任务 (startWorkers)
    ↓
12. 注册结果收集器 (registerResultCollector)
    ↓
13. 提交任务并收集结果 (executeAndCollect) → 得到 JQuickDataSet
    ↓
14. 清理资源 (cleanup)

| 序号 | 优化规则名称 | 说明 | 类型 |
|:---:|:---|:---|:---:|
| 1 | `ConstantFoldingRule` | 常量折叠 | 基础优化 |
| 2 | `PredicatePushdownRule` | 谓词下推 | 基础优化 |
| 3 | `ProjectionPushdownRule` | 投影下推 | 基础优化 |
| 4 | `LimitPushdownRule` | Limit下推 | 基础优化 |
| 5 | `FilterMergeRule` | 过滤合并 | 基础优化 |
| 6 | `ProjectionMergeRule` | 投影合并 | 基础优化 |
| 7 | `RedundantFilterRemovalRule` | 冗余过滤移除 | 基础优化 |
| 8 | `ColumnPruningRule` | 列剪枝 | 基础优化 |
| 9 | `JoinReorderRule` | Join重排序 | 高级优化 |
| 10 | `SubqueryToJoinRule` | 子查询转Join | 高级优化 |
| 11 | `AggregatePushdownRule` | 聚合下推 | 高级优化 |
| 12 | `SimplifyExpressionRule` | 表达式简化 | 高级优化 |
| 13 | `DistributionOptimizationRule` | 分布优化 | 分布优化 |
## 基本语法结构
### SELECT 语句
```sql
SELECT [ALL | DISTINCT] column1, column2, ...
FROM table_name
[WHERE condition]
[GROUP BY column1, column2, ...]
[HAVING condition]
[ORDER BY column1 [ASC|DESC], ...]
[LIMIT n [OFFSET m]];
```
### FROM 子句
支持表名、别名和子查询：
```sql
FROM table_name [AS alias]
FROM (SELECT ...) AS subquery_alias
```

### WHERE 子句

支持 AND、OR、NOT、BETWEEN、IN、LIKE、IS NULL 等操作符。

```sql
WHERE condition
```

### GROUP BY 与 HAVING

```sql
GROUP BY column1, column2, ...
HAVING aggregate_condition
```

### ORDER BY 子句

```sql
ORDER BY column1 [ASC|DESC], column2 [ASC|DESC], ...
```

### LIMIT 子句

```sql
LIMIT 10          -- 前10行
LIMIT 5, 10       -- 从第5行开始，取10行（OFFSET=5, LIMIT=10）
```

### JOIN 操作

支持多种 JOIN 类型：

```sql
SELECT *
FROM table1
[INNER|LEFT|RIGHT|FULL|CROSS|NATURAL] JOIN table2 ON table1.id = table2.id
```

### 集合操作

#### UNION

```sql
SELECT * FROM table1
UNION
SELECT * FROM table2
```

#### MINUS

```sql
SELECT * FROM table1
MINUS
SELECT * FROM table2
```

#### INTERSECT

```sql
SELECT * FROM table1
INTERSECT
SELECT * FROM table2
```

### OLAP 操作

#### ROLLUP

```sql
SELECT region, department, SUM(sales)
FROM sales_data
ROLLUP (region, department)
```

#### DRILLDOWN

```sql
SELECT region, department, SUM(sales)
FROM sales_data
DRILLDOWN (region, department)
```

#### SLICE

```sql
SELECT *
FROM sales_data
SLICE (department = 'Electronics')
```

#### DICE

```sql
SELECT *
FROM sales_data
DICE (region = 'North', department = 'Electronics')
```

#### PIVOT

```sql
SELECT *
FROM sales_data
PIVOT (department, sales, SUM)
```

### 函数与表达式

普通函数请查看javelin 的JEvalue支持的方法

默认支持的聚合函数如下，若需要扩展聚合函数请参考JAggregateFunctionFactory的registerFunction 方法

| 函数名称           | 图标  | 说明                                         | 示例                                                                      |
|----------------|-----|--------------------------------------------|-------------------------------------------------------------------------|
| SUM            | ➕   | 计算指定列的总和                                   | SELECT SUM(sales) FROM orders                                           |
| AVG            | 📊  | 计算指定列的平均值                                  | SELECT AVG(age) FROM users                                              |
| MAX            | ⬆️  | 获取指定列的最大值                                  | SELECT MAX(price) FROM products                                         |
| MIN            | ⬇️  | 获取指定列的最小值                                  | SELECT MIN(order_date) FROM orders                                      |
| COUNT          | 🔢  | 统计记录行数（支持 * 或指定列）                          | SELECT COUNT(*) FROM users <br> SELECT COUNT(DISTINCT city) FROM users  |
| FIRST          | ⏮️  | 获取分组中指定列的第一条记录值                            | SELECT department, FIRST(name) FROM employees GROUP BY department       |
| LAST           | ⏭️  | 获取分组中指定列的最后一条记录值                           | SELECT department, LAST(name) FROM employees GROUP BY department        |
| STDDEV         | 🔄  | 计算指定列的标准差（样本标准差）                           | SELECT STDDEV(salary) FROM employees                                    |
| VARIANCE       | 🔃  | 计算指定列的方差（样本方差）                             | SELECT VARIANCE(sales) FROM monthly_data                                |
| MEDIAN         | 📈  | 计算指定列的中位数                                  | SELECT MEDIAN(age) FROM population                                      |
| MODE           | 🔝  | 获取指定列中出现频率最高的值（众数）                         | SELECT MODE(product) FROM orders                                        |
| QUARTILE1      | 25% | 计算指定列的第一四分位数（25% 分位数）                      | SELECT QUARTILE1(sales) FROM regional_data                              |
| QUARTILE3      | 75% | 计算指定列的第三四分位数（75% 分位数）                      | SELECT QUARTILE3(sales) FROM regional_data                              |
| RANGE          | ↔️  | 计算指定列的极差（最大值与最小值之差）                        | SELECT RANGE(price) FROM products                                       |
| PRODUCT        | ✖️  | 计算指定列所有值的乘积                                | SELECT PRODUCT(quantity) FROM order_details                             |
| CONCAT         | 🔗  | 拼接分组中指定列的字符串（支持分隔符，默认无）                    | SELECT department, CONCAT(name, ',') FROM employees GROUP BY department |
| DISTINCT_COUNT | 🔍  | 统计指定列中不重复值的数量（等同于 COUNT (DISTINCT column)） | SELECT DISTINCT_COUNT(city) FROM users                                  |

```sql
SELECT COUNT(*), AVG(salary), MAX(age)
FROM employees
```

### 函数与表达式

数据类型与字面量

1. 字符串：'text' 或 "text"
2. 数字：123, 45.67
3. 布尔值：TRUE, FALSE
4. 空值：NULL
5. 日期：'2024-01-01'::'yyyy-MM-dd'

###如何使用

#### 1.注册数据集

```java
public class DataSetFactory {
    /**
     * 创建用户数据集
     */
    public static DataSet createUsersDataSet() {
        List<JColumnMeta> columns = Arrays.asList(
                new JColumnMeta("id", Integer.class, "users"),
                new JColumnMeta("name", String.class, "users"),
                new JColumnMeta("age", Integer.class, "users"),
                new JColumnMeta("city", String.class, "users"),
                new JColumnMeta("salary", Double.class, "users")
        );
        List<Row> rows = Arrays.asList(
                createUserRow(1, "张三", 25, "北京", 8000.0),
                createUserRow(2, "李四", 30, "上海", 12000.0)
        );
        return new DataSet(columns, rows);
    }
    public static DataSet createOrdersDataSet() {
        List<JColumnMeta> columns = Arrays.asList(
                new JColumnMeta("order_id", Integer.class, "orders"),
                new JColumnMeta("user_id", Integer.class, "orders"),
                new JColumnMeta("product", String.class, "orders"),
                new JColumnMeta("quantity", Integer.class, "orders"),
                new JColumnMeta("price", Double.class, "orders"),
                new JColumnMeta("order_date", String.class, "orders")
        );
        List<Row> rows = Arrays.asList(
                createOrderRow(101, 1, "笔记本电脑", 1, 5999.0, "2024-01-15"),
                createOrderRow(102, 1, "鼠标", 2, 199.0, "2024-01-16"),
                createOrderRow(103, 2, "手机", 1, 3999.0, "2024-01-17"),
                createOrderRow(104, 3, "耳机", 3, 299.0, "2024-01-18")
        );
        return new DataSet(columns, rows);
    }
}
```

#### 2.使用如何使用 目前引擎只支持LAMBDA表达式，对于大数据量的查询请联系作者(mailto:goudingcheng@gmail.com)

```java
        String rule="select * from user a inner join user_order b on a.id=b.user_id order by b.user_id";
        JQuickSQLExecutor executor=new JQuickSQLExecutor();
        DataSetHolder dataSetContainer=new DataSetHolder();
        dataSetContainer.addDataSet("user", DataSetFactory.createUsersDataSet());
        dataSetContainer.addDataSet("user_order", DataSetFactory.createOrdersDataSet());
        executor.dataSet(dataSetContainer);
        DataSet dataSet=executor.execute(rule, JEngineEnums.LAMBDA);
        for (Row row : dataSet.getRows()) {
            System.out.println(row);
        }
```

# **捐献 ☕**

感谢您使用这个开源项目！它完全免费并将持续维护，但开发者确实需要您的支持。

---

## **如何支持我们**

1. **请我喝杯咖啡**  
   果这个项目为您节省了时间或金钱，请考虑通过小额捐赠支持我。

2. **您的捐赠用途**

- 维持项目运行的服务器成本.
- 开发新功能以提供更多价值.
- 优化文档以提升用户体验.

3. **每一分都很重要**  
   即使是1分钱的捐赠也能激励我熬夜调试！

## **为什么捐赠?**

✔️ 保持项目永远免费且无广告.  
✔️ 支持及时响应问题和社区咨询.  
✔️ 实现计划中的未来功能.

感谢您成为让开源世界更美好的伙伴！

--- 

### **补充说明**

- 本项目和产品维护.
- 您的支持确保其可持续性和成长 .

---

## **🌟 立即支持**

赞助时欢迎通过 [email](mailto:goudingcheng@gmail.com) 留言。您的名字将被列入项目README文件的 **"特别感谢"** 名单中！
![Ali Pay](./pay/alipay.jpg)
![Wechat Pay](./pay/wechat.jpg)

---
