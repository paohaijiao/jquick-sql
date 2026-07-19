# JQuickSQL 引擎使用文档
简体中文 | [English](./README-EN.md)
## 项目状态
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![GitHub Stars](https://img.shields.io/github/stars/paohaijiao/jquick-sql.svg?style=social&label=Stars)](https://github.com/paohaijiao/jquick-sql)
[![GitHub Forks](https://img.shields.io/github/forks/paohaijiao/jquick-sql.svg?style=social&label=Forks)](https://github.com/paohaijiao/jquick-sql/fork)
[![Last Commit](https://img.shields.io/github/last-commit/paohaijiao/jquick-sql.svg)](https://github.com/paohaijiao/jquick-sql/commits/main)
[![Language](https://img.shields.io/github/languages/top/paohaijiao/jquick-sql.svg)](https://github.com/paohaijiao/jquick-sql)
## 目录

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
