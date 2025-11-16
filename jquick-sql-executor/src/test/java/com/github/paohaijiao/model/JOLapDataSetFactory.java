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

import com.github.paohaijiao.dataset.ColumnMeta;
import com.github.paohaijiao.dataset.DataSet;
import com.github.paohaijiao.dataset.Row;

import java.util.Arrays;
import java.util.List;

/**
 * packageName com.github.paohaijiao.model
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/8/31
 */
public class JOLapDataSetFactory {
    public static DataSet createSalesDataSet() {
        List<ColumnMeta> columns = Arrays.asList(
                new ColumnMeta("region", String.class, "sales_data"),
                new ColumnMeta("department", String.class, "sales_data"),
                new ColumnMeta("category", String.class, "sales_data"),
                new ColumnMeta("product", String.class, "sales_data"),
                new ColumnMeta("sales", Double.class, "sales_data"),
                new ColumnMeta("quarter", String.class, "sales_data"),
                new ColumnMeta("year", Integer.class, "sales_data")
        );

        List<Row> rows = Arrays.asList(
                createSalesRow("North", "Electronics", "Computers", "Laptop", 15000.00, "Q1", 2024),
                createSalesRow("North", "Electronics", "Computers", "Desktop", 12000.00, "Q1", 2024),
                createSalesRow("North", "Electronics", "Phones", "Smartphone", 20000.00, "Q1", 2024),
                createSalesRow("South", "Electronics", "Computers", "Laptop", 18000.00, "Q1", 2024),
                createSalesRow("South", "Electronics", "Phones", "Smartphone", 22000.00, "Q1", 2024),
                createSalesRow("North", "Clothing", "Men", "Shirt", 8000.00, "Q1", 2024),
                createSalesRow("North", "Clothing", "Women", "Dress", 12000.00, "Q1", 2024),
                createSalesRow("South", "Clothing", "Men", "Shirt", 9000.00, "Q1", 2024),
                createSalesRow("South", "Clothing", "Women", "Dress", 15000.00, "Q1", 2024)
        );

        return new DataSet(columns, rows);
    }

    private static Row createSalesRow(String region, String department, String category,
                                      String product, double sales, String quarter, int year) {
        Row row = new Row();
        row.put("region", region);
        row.put("department", department);
        row.put("category", category);
        row.put("product", product);
        row.put("sales", sales);
        row.put("quarter", quarter);
        row.put("year", year);
        return row;
    }

    public static DataSet createEmployeeDataSet() {
        List<ColumnMeta> columns = Arrays.asList(
                new ColumnMeta("employee_id", Integer.class, "employee_data"),
                new ColumnMeta("name", String.class, "employee_data"),
                new ColumnMeta("department", String.class, "employee_data"),
                new ColumnMeta("salary", Double.class, "employee_data"),
                new ColumnMeta("hire_date", String.class, "employee_data"),
                new ColumnMeta("region", String.class, "employee_data")
        );

        List<Row> rows = Arrays.asList(
                createEmployeeRow(1, "张三", "IT", 75000.00, "2023-03-15", "North"),
                createEmployeeRow(2, "李四", "IT", 85000.00, "2022-06-20", "North"),
                createEmployeeRow(3, "王五", "Sales", 60000.00, "2023-01-10", "South"),
                createEmployeeRow(4, "赵六", "Sales", 70000.00, "2022-08-05", "South"),
                createEmployeeRow(5, "钱七", "HR", 55000.00, "2023-05-30", "North"),
                createEmployeeRow(6, "孙八", "HR", 65000.00, "2022-11-12", "South"),
                createEmployeeRow(7, "周九", "IT", 80000.00, "2023-07-22", "South"),
                createEmployeeRow(8, "吴十", "Sales", 72000.00, "2022-09-18", "North")
        );

        return new DataSet(columns, rows);
    }

    private static Row createEmployeeRow(int employeeId, String name, String department,
                                         double salary, String hireDate, String region) {
        Row row = new Row();
        row.put("employee_id", employeeId);
        row.put("name", name);
        row.put("department", department);
        row.put("salary", salary);
        row.put("hire_date", hireDate);
        row.put("region", region);
        return row;
    }

    public static DataSet createFinancialDataSet() {
        List<ColumnMeta> columns = Arrays.asList(
                new ColumnMeta("year", Integer.class, "financial_data"),
                new ColumnMeta("quarter", String.class, "financial_data"),
                new ColumnMeta("month", Integer.class, "financial_data"),
                new ColumnMeta("revenue", Double.class, "financial_data"),
                new ColumnMeta("expense", Double.class, "financial_data"),
                new ColumnMeta("department", String.class, "financial_data")
        );

        List<Row> rows = Arrays.asList(
                createFinancialRow(2024, "Q1", 1, 500000.00, 300000.00, "Sales"),
                createFinancialRow(2024, "Q1", 2, 550000.00, 320000.00, "Sales"),
                createFinancialRow(2024, "Q1", 3, 600000.00, 350000.00, "Sales"),
                createFinancialRow(2024, "Q1", 1, 200000.00, 150000.00, "IT"),
                createFinancialRow(2024, "Q1", 2, 220000.00, 160000.00, "IT"),
                createFinancialRow(2024, "Q1", 3, 250000.00, 170000.00, "IT"),
                createFinancialRow(2023, "Q4", 10, 480000.00, 290000.00, "Sales"),
                createFinancialRow(2023, "Q4", 11, 520000.00, 310000.00, "Sales"),
                createFinancialRow(2023, "Q4", 12, 580000.00, 340000.00, "Sales")
        );

        return new DataSet(columns, rows);
    }

    private static Row createFinancialRow(int year, String quarter, int month,
                                          double revenue, double expense, String department) {
        Row row = new Row();
        row.put("year", year);
        row.put("quarter", quarter);
        row.put("month", month);
        row.put("revenue", revenue);
        row.put("expense", expense);
        row.put("department", department);
        return row;
    }
}
