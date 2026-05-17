///*
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// *
// * Copyright (c) [2025-2099] Martin (goudingcheng@gmail.com)
// */
//
//import com.github.paohaijiao.datasource.JQuickDataSourceManager;
//import com.github.paohaijiao.enums.JQuickBinaryOperator;
//import com.github.paohaijiao.context.JQuickExecutionContext;
//import com.github.paohaijiao.expression.JQuickExpression;
//import com.github.paohaijiao.expression.domain.JQuickBinaryExpression;
//import com.github.paohaijiao.expression.domain.JQuickColumnRefExpression;
//import com.github.paohaijiao.expression.domain.JQuickLiteralExpression;
//import com.github.paohaijiao.logic.domain.*;
//import com.github.paohaijiao.optimizer.JQuickLogicalPlanOptimizer;
//import com.github.paohaijiao.logic.JQuickLogicalPlanNode;
//import com.github.paohaijiao.statement.JQuickDataSet;
//import com.github.paohaijiao.statement.JQuickRow;
//
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
//
//public class Example {
//
//    public static void main(String[] args) {
//        // 准备测试数据
//        prepareTestData();
//
//        // 创建执行上下文
//        JQuickExecutionContext context = new JQuickExecutionContext();
////        context.setParameter("minAge", 25);
////        context.setParameter("city", "北京");
//
//        // 手动构建逻辑计划（模拟 SQL 解析结果）
//        JQuickLogicalPlanNode plan = buildLogicalPlan();
//
//        // 打印原始计划
//        printPlan(plan, 0);
//
//        // 优化逻辑计划
//        JQuickLogicalPlanOptimizer optimizer = new JQuickLogicalPlanOptimizer();
//        optimizer.enableRuleTracing(true);
//        JQuickLogicalPlanNode optimizedPlan = optimizer.optimize(plan);
//
//        // 打印优化后的计划
//        printPlan(optimizedPlan, 0);
//        // 执行
//        JQuickDataSet result = optimizedPlan.execute(context);
//
//        // 打印结果
//        System.out.println("\n=== 执行结果 ===");
//        result.printTable();
//
//        // 打印统计信息
//        System.out.println("\n=== 执行统计 ===");
//        context.printStats();
//    }
//
//    private static JQuickLogicalPlanNode buildLogicalPlan() {
//        // SELECT name, age FROM users WHERE age > 25 AND city = '北京' ORDER BY age DESC LIMIT 10
//
//        // 1. 表扫描
//        JQuickTableScanNode scan = new JQuickTableScanNode("users");
//
//        // 2. 过滤条件: age > 25 AND city = '北京'
//        JQuickExpression ageCondition = new JQuickBinaryExpression(
//                new JQuickColumnRefExpression("age"),
//                new JQuickLiteralExpression(25),
//                JQuickBinaryOperator.GT
//        );
//        JQuickExpression cityCondition = new JQuickBinaryExpression(
//                new JQuickColumnRefExpression("city"),
//                new JQuickLiteralExpression("北京"),
//                JQuickBinaryOperator.EQ
//        );
//        JQuickExpression filterCondition = new JQuickBinaryExpression(
//                ageCondition, cityCondition, JQuickBinaryOperator.AND
//        );
//        JQuickFilterNode filter = new JQuickFilterNode(filterCondition, scan);
//
//        // 3. 投影: name, age
//        List<JQuickProjectNode.SelectItem> selectItems = Arrays.asList(
//                new JQuickProjectNode.SelectItem(new JQuickColumnRefExpression("name"), "name"),
//                new JQuickProjectNode.SelectItem(new JQuickColumnRefExpression("age"), "age")
//        );
//        JQuickProjectNode project = new JQuickProjectNode(selectItems, filter);
//
//        // 4. 排序: age DESC
//        List<JQuickSortNode.OrderByItem> orderByItems = Collections.singletonList(
//                new JQuickSortNode.OrderByItem("age", false)
//        );
//        JQuickSortNode sort = new JQuickSortNode(orderByItems, project);
//
//        // 5. 限制: LIMIT 10
//        return new JQuickLimitNode(10, sort);
//    }
//
//    private static void printPlan(JQuickLogicalPlanNode node, int level) {
//        String indent = repeatString("  ", level);
//        System.out.println(indent + node.getNodeType());
//
//        for (JQuickLogicalPlanNode child : node.getChildren()) {
//            printPlan(child, level + 1);
//        }
//    }
//    private static String repeatString(String str, int count) {
//        if (count <= 0) {
//            return "";
//        }
//        StringBuilder sb = new StringBuilder(str.length() * count);
//        for (int i = 0; i < count; i++) {
//            sb.append(str);
//        }
//        return sb.toString();
//    }
//
//    private static void prepareTestData() {
//        JQuickDataSet.Builder builder = JQuickDataSet.builder();
//        builder.addColumn("id", Long.class, "test")
//                .addColumn("name", String.class, "test")
//                .addColumn("age", Integer.class, "test")
//                .addColumn("city", String.class, "test");
//
//        builder.addRow(createRow(1L, "张三", 25, "北京"));
//        builder.addRow(createRow(2L, "李四", 30, "上海"));
//        builder.addRow(createRow(3L, "王五", 28, "北京"));
//        builder.addRow(createRow(4L, "赵六", 22, "深圳"));
//        builder.addRow(createRow(5L, "钱七", 35, "北京"));
//        builder.addRow(createRow(6L, "孙八", 20, "上海"));
//
//        JQuickDataSourceManager.registerTable("users", builder.build());
//    }
//
//    private static JQuickRow createRow(Object... values) {
//        JQuickRow row = new JQuickRow();
//        String[] keys = {"id", "name", "age", "city"};
//        for (int i = 0; i < values.length && i < keys.length; i++) {
//            row.put(keys[i], values[i]);
//        }
//        return row;
//    }
//}