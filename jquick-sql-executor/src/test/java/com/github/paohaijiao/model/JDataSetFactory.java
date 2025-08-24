/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Copyright (c) [2025-2099] Martin (goudingcheng@gmail.com)
 */
package com.github.paohaijiao.model;

import com.github.paohaijiao.dataset.JColumnMeta;
import com.github.paohaijiao.dataset.JDataSet;
import com.github.paohaijiao.dataset.JRow;

import java.util.Arrays;
import java.util.List;

/**
 * packageName com.github.paohaijiao.model
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/8/24
 */
public class JDataSetFactory {
    /**
     * 创建用户数据集
     */
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
                createUserRow(2, "李四", 30, "上海", 12000.0),
                createUserRow(3, "王五", 28, "广州", 9500.0),
                createUserRow(4, "赵六", 35, "深圳", 15000.0),
                createUserRow(5, "钱七", 22, "杭州", 7000.0),
                createUserRow(6, "孙八", 40, "北京", 18000.0),
                createUserRow(7, "周九", 26, "上海", 8500.0),
                createUserRow(8, "吴十", 33, "成都", 11000.0)
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
                createOrderRow(104, 3, "耳机", 3, 299.0, "2024-01-18"),
                createOrderRow(105, 4, "平板电脑", 1, 2999.0, "2024-01-19"),
                createOrderRow(106, 5, "键盘", 1, 499.0, "2024-01-20"),
                createOrderRow(107, 6, "显示器", 2, 1999.0, "2024-01-21"),
                createOrderRow(108, 7, "打印机", 1, 899.0, "2024-01-22"),
                createOrderRow(109, 8, "扫描仪", 1, 699.0, "2024-01-23"),
                createOrderRow(110, 2, "充电宝", 2, 199.0, "2024-01-24"),
                createOrderRow(111, 9, "智能手表", 1, 1299.0, "2024-01-25") // 用户9不存在，用于测试LEFT JOIN
        );
        return new JDataSet(columns, rows);
    }

    public static JDataSet createDepartmentsDataSet() {
        List<JColumnMeta> columns = Arrays.asList(
                new JColumnMeta("dept_id", Integer.class, "departments"),
                new JColumnMeta("dept_name", String.class, "departments"),
                new JColumnMeta("manager_id", Integer.class, "departments"),
                new JColumnMeta("budget", Double.class, "departments")
        );
        List<JRow> rows = Arrays.asList(
                createDepartmentRow(1, "技术部", 2, 500000.0),
                createDepartmentRow(2, "销售部", 4, 300000.0),
                createDepartmentRow(3, "市场部", 6, 200000.0),
                createDepartmentRow(4, "财务部", 8, 400000.0),
                createDepartmentRow(5, "人事部", 10, 150000.0) // 经理10不存在，用于测试
        );
        return new JDataSet(columns, rows);
    }
    public static JDataSet createProductsDataSet() {
        List<JColumnMeta> columns = Arrays.asList(
                new JColumnMeta("product_id", Integer.class, "products"),
                new JColumnMeta("product_name", String.class, "products"),
                new JColumnMeta("category", String.class, "products")
        );
        List<JRow> rows = Arrays.asList(
                createProductRow(1, "iPhone", "手机"),
                createProductRow(2, "MacBook", "电脑"),
                createProductRow(3, "iPad", "平板")
        );
        return new JDataSet(columns, rows);
    }
    public static JDataSet createSmallUsersDataSet() {
        List<JColumnMeta> columns = Arrays.asList(
                new JColumnMeta("id", Integer.class, "small_users"),
                new JColumnMeta("name", String.class, "small_users")
        );
        List<JRow> rows = Arrays.asList(
                createSimpleUserRow(1, "测试用户1"),
                createSimpleUserRow(2, "测试用户2"),
                createSimpleUserRow(3, "测试用户3")
        );
        return new JDataSet(columns, rows);
    }

    public static JDataSet createSmallOrdersDataSet() {
        List<JColumnMeta> columns = Arrays.asList(
                new JColumnMeta("order_id", Integer.class, "small_orders"),
                new JColumnMeta("user_id", Integer.class, "small_orders"),
                new JColumnMeta("product", String.class, "small_orders")
        );
        List<JRow> rows = Arrays.asList(
                createSimpleOrderRow(101, 1, "产品A"),
                createSimpleOrderRow(102, 1, "产品B"),
                createSimpleOrderRow(103, 2, "产品C"),
                createSimpleOrderRow(104, 4, "产品D") // 用户4不存在
        );
        return new JDataSet(columns, rows);
    }
    private static JRow createUserRow(int id, String name, int age, String city, double salary) {
        JRow row = new JRow();
        row.put("id", id);
        row.put("name", name);
        row.put("age", age);
        row.put("city", city);
        row.put("salary", salary);
        return row;
    }

    private static JRow createOrderRow(int orderId, int userId, String product, int quantity, double price, String date) {
        JRow row = new JRow();
        row.put("order_id", orderId);
        row.put("user_id", userId);
        row.put("product", product);
        row.put("quantity", quantity);
        row.put("price", price);
        row.put("order_date", date);
        return row;
    }

    private static JRow createDepartmentRow(int deptId, String deptName, int managerId, double budget) {
        JRow row = new JRow();
        row.put("dept_id", deptId);
        row.put("dept_name", deptName);
        row.put("manager_id", managerId);
        row.put("budget", budget);
        return row;
    }

    private static JRow createProductRow(int productId, String productName, String category) {
        JRow row = new JRow();
        row.put("product_id", productId);
        row.put("product_name", productName);
        row.put("category", category);
        return row;
    }

    private static JRow createSimpleUserRow(int id, String name) {
        JRow row = new JRow();
        row.put("id", id);
        row.put("name", name);
        return row;
    }

    private static JRow createSimpleOrderRow(int orderId, int userId, String product) {
        JRow row = new JRow();
        row.put("order_id", orderId);
        row.put("user_id", userId);
        row.put("product", product);
        return row;
    }

    public static void printDataSet(JDataSet dataSet, String title) {
        System.out.println("=== " + title + " ===");
        System.out.println("列信息: " + dataSet.getColumnNames());
        System.out.println("行数: " + dataSet.getRows().size());

        // 打印前5行数据
        System.out.println("示例数据:");
        for (int i = 0; i < Math.min(5, dataSet.getRows().size()); i++) {
            System.out.println("行 " + (i + 1) + ": " + dataSet.getRows().get(i));
        }
        System.out.println();
    }
}
