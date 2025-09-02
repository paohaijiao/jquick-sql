# JQuickSQL Engine User Documentation

## Table of Contents

- [Overview](#overview)
- [Basic Syntax Structure](#basic-syntax-structure)
  - [SELECT Statement](#select-statement)
  - [FROM Clause](#from-clause)
  - [WHERE Clause](#where-clause)
  - [GROUP BY and HAVING](#group-by-and-having)
  - [ORDER BY Clause](#order-by-clause)
  - [LIMIT Clause](#limit-clause)
- [JOIN Operations](#join-operations)
- [Set Operations](#set-operations)
  - [UNION](#union)
  - [MINUS](#minus)
  - [INTERSECT](#intersect)
- [OLAP Operations](#olap-operations)
  - [ROLLUP](#rollup)
  - [DRILLDOWN](#drilldown)
  - [SLICE](#slice)
  - [DICE](#dice)
  - [PIVOT](#pivot)
- [CTE (Common Table Expressions)](#cte-common-table-expressions)
- [Functions and Expressions](#functions-and-expressions)
- [Data Types and Literals](#data-types-and-literals)
- [Example Code](#example-code)
- [Appendix: Syntax Rules Summary](#appendix-syntax-rules-summary)

## Overview

JQuickSQL is a lightweight SQL query engine that supports standard SQL syntax and OLAP operations, suitable for querying and analyzing in-memory datasets (such as `JDataSet`).

## Basic Syntax Structure

### SELECT Statement

```sql
SELECT [ALL | DISTINCT] column1, column2, ...
FROM table_name
[WHERE condition]
[GROUP BY column1, column2, ...]
[HAVING condition]
[ORDER BY column1 [ASC|DESC], ...]
[LIMIT n [OFFSET m]];
```


### FROM Clause
Supports table names, aliases, and subqueries：
```sql
FROM table_name [AS alias]
FROM (SELECT ...) AS subquery_alias
```
### WHERE Clause
Supports operators such as AND, OR, NOT, BETWEEN, IN, LIKE, IS NULL.
```sql
WHERE condition
```
### GROUP BY and HAVING
```sql
GROUP BY column1, column2, ...
HAVING aggregate_condition
```
### ORDER BY Clause
```sql
ORDER BY column1 [ASC|DESC], column2 [ASC|DESC], ...
```
### LIMIT Clause
```sql
LIMIT 10          -- 前10行
LIMIT 5, 10       -- 从第5行开始，取10行（OFFSET=5, LIMIT=10）
```
### JOIN Clause
Supports multiple JOIN types：
```sql
SELECT *
FROM table1
[INNER|LEFT|RIGHT|FULL|CROSS|NATURAL] JOIN table2 ON table1.id = table2.id
```
### Set Operation
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
### OLAP Operation
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

### Functions and Expressions

For regular functions, please refer to the methods supported by Javelin's JEvalue.

The following aggregate functions are supported by default. For extending aggregate functions, please refer to the registerFunction method of JAggregateFunctionFactory.

| 函数名称         | 图标  | 说明                                                                 | 示例                                                                 |
|------------------|-------|----------------------------------------------------------------------|----------------------------------------------------------------------|
| SUM              | ➕    | 计算指定列的总和                                                     | SELECT SUM(sales) FROM orders                                       |
| AVG              | 📊    | 计算指定列的平均值                                                   | SELECT AVG(age) FROM users                                          |
| MAX              | ⬆️    | 获取指定列的最大值                                                   | SELECT MAX(price) FROM products                                     |
| MIN              | ⬇️    | 获取指定列的最小值                                                   | SELECT MIN(order_date) FROM orders                                  |
| COUNT            | 🔢    | 统计记录行数（支持 * 或指定列）                                      | SELECT COUNT(*) FROM users <br> SELECT COUNT(DISTINCT city) FROM users |
| FIRST            | ⏮️    | 获取分组中指定列的第一条记录值                                       | SELECT department, FIRST(name) FROM employees GROUP BY department   |
| LAST             | ⏭️    | 获取分组中指定列的最后一条记录值                                     | SELECT department, LAST(name) FROM employees GROUP BY department    |
| STDDEV           | 🔄    | 计算指定列的标准差（样本标准差）                                     | SELECT STDDEV(salary) FROM employees                                |
| VARIANCE         | 🔃    | 计算指定列的方差（样本方差）                                         | SELECT VARIANCE(sales) FROM monthly_data                            |
| MEDIAN           | 📈    | 计算指定列的中位数                                                   | SELECT MEDIAN(age) FROM population                                  |
| MODE             | 🔝    | 获取指定列中出现频率最高的值（众数）                                 | SELECT MODE(product) FROM orders                                    |
| QUARTILE1        | 25%   | 计算指定列的第一四分位数（25% 分位数）                               | SELECT QUARTILE1(sales) FROM regional_data                          |
| QUARTILE3        | 75%   | 计算指定列的第三四分位数（75% 分位数）                               | SELECT QUARTILE3(sales) FROM regional_data                          |
| RANGE            | ↔️    | 计算指定列的极差（最大值与最小值之差）                               | SELECT RANGE(price) FROM products                                   |
| PRODUCT          | ✖️    | 计算指定列所有值的乘积                                               | SELECT PRODUCT(quantity) FROM order_details                         |
| CONCAT           | 🔗    | 拼接分组中指定列的字符串（支持分隔符，默认无）                       | SELECT department, CONCAT(name, ',') FROM employees GROUP BY department |
| DISTINCT_COUNT   | 🔍    | 统计指定列中不重复值的数量（等同于 COUNT (DISTINCT column)）         | SELECT DISTINCT_COUNT(city) FROM users                              |


```sql
SELECT COUNT(*), AVG(salary), MAX(age)
FROM employees
```
### Data Types and Literal
Strings: 'text' or "text"
Numbers: 123, 45.67
Booleans: TRUE, FALSE
Null value: NULL
Dates: '2024-01-01'::'yyyy-MM-dd'

###How to Use

#### 1.. Register Data Sets
```java
public class JDataSetFactory {
    public static JDataSet createUsersDataSet() {
        List<JColumnMeta> columns = Arrays.asList(
                new JColumnMeta("id", Integer.class, "users"),
                new JColumnMeta("name", String.class, "users"),
                new JColumnMeta("age", Integer.class, "users"),
                new JColumnMeta("city", String.class, "users"),
                new JColumnMeta("salary", Double.class, "users")
        );
        List<JRow> rows = Arrays.asList(
                createUserRow(1, "张三", 25, "北京", 8000.0),
                createUserRow(2, "李四", 30, "上海", 12000.0)
        );
        return new JDataSet(columns, rows);
    }
    public static JDataSet createOrdersDataSet() {
        List<JColumnMeta> columns = Arrays.asList(
                new JColumnMeta("order_id", Integer.class, "orders"),
                new JColumnMeta("user_id", Integer.class, "orders"),
                new JColumnMeta("product", String.class, "orders"),
                new JColumnMeta("quantity", Integer.class, "orders"),
                new JColumnMeta("price", Double.class, "orders"),
                new JColumnMeta("order_date", String.class, "orders")
        );
        List<JRow> rows = Arrays.asList(
                createOrderRow(101, 1, "笔记本电脑", 1, 5999.0, "2024-01-15"),
                createOrderRow(102, 1, "鼠标", 2, 199.0, "2024-01-16"),
                createOrderRow(103, 2, "手机", 1, 3999.0, "2024-01-17"),
                createOrderRow(104, 3, "耳机", 3, 299.0, "2024-01-18")
        );
        return new JDataSet(columns, rows);
    }
}
```
#### 2.Currently, the engine only supports LAMBDA expressions. For large dataset queries, please contact the author (mailto:goudingcheng@gmail.com).
```java
        String rule="select * from user a inner join user_order b on a.id=b.user_id order by b.user_id";
        JQuickSQLExecutor executor=new JQuickSQLExecutor();
        JDataSetHolder dataSetContainer=new JDataSetHolder();
        dataSetContainer.addDataSet("user", JDataSetFactory.createUsersDataSet());
        dataSetContainer.addDataSet("user_order", JDataSetFactory.createOrdersDataSet());
        executor.dataSet(dataSetContainer);
        JDataSet dataSet=executor.execute(rule, JEngineEnums.LAMBDA);
        for (JRow row : dataSet.getRows()) {
            System.out.println(row);
        }
```

# **Generating Power with Love (and Caffeine) ☕**

Thank you for using this open-source project! It is completely free and will be maintained continuously, but the developers do need your support.

---

## **How You Can Help**

1. **Buy Me a Coffee**  
   If this project has saved you time or money, please consider supporting me with a small donation.

2. **Where Your Donation Goes**
- Server costs to keep the project running.
- Feature development to add more value.
- Documentation optimization for a better user experience.

3. **Every Cent Counts**  
   Even a donation of just 1 cent motivates me to debug late into the night!



## **Why Donate?**
✔️ Keep the project **free and ad-free** forever.  
✔️ Support timely responses to issues and community inquiries.  
✔️ Enable planned features for the future.

Thank you for being a partner in making the open-source world better!

--- 

### **Additional Notes**
- The project is maintained with love and caffeine.
- Your support ensures its sustainability and growth.
---

## **🌟 Support Now**
Feel free to leave a message via [email](mailto:goudingcheng@gmail.com) when sponsoring. Your name will be included in the **"Special Thanks"** list in the project's README file!
![OCBC Pay Now](./src/main/resources/pay/paynow.jpg)
![Touch n Go ](./src/main/resources/pay/tngGo.jpg)
---