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
package com.github.paohaijiao.select;
import com.github.paohaijiao.dataset.JDataSet;
import com.github.paohaijiao.dataset.JRow;
import com.github.paohaijiao.enums.JEngineEnums;
import com.github.paohaijiao.executor.JQuickSQLExecutor;
import com.github.paohaijiao.model.JDataSetFactory;
import com.github.paohaijiao.parser.JQuickSQLParser;
import com.github.paohaijiao.support.JDataSetHolder;
import com.github.paohaijiao.visitor.JQuikSQLCommonTableExpressionVisitor;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

/**
 * packageName com.github.paohaijiao.select
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/8/24
 */
public class JCteQueryTest {
    private JRow addRow(Integer id,String name,Integer managerId){
        JRow row = new JRow();
        row.put("id",id);
        row.put("name",name);
        row.put("manager_id",managerId);
        return row;
    }
    private JRow addEmRow(Integer emp_id,String emp_name,Integer manager_id,String department,Integer salary){
        JRow row = new JRow();
        row.put("emp_id",emp_id);
        row.put("emp_name",emp_name);
        row.put("manager_id",manager_id);
        row.put("department",department);
        row.put("salary",salary);
        return row;
    }

    private JDataSet setupTestData() {
        JDataSet employees = JDataSet.builder()
                .addColumn("id",Integer.class,"")
                .addColumn("name",String.class,"")
                .addColumn("manager_id",Integer.class,"")
                .addRow(addRow(1, "CEO", null))
                .addRow(addRow(2, "CTO", 1))
                .addRow(addRow(3, "CFO", 1))
                .addRow(addRow(4, "Dev Manager", 2))
                .addRow(addRow(5, "QA Manager", 2))
                .addRow(addRow(6, "Senior Dev", 4))
                .addRow(addRow(7, "Junior Dev", 4))
                .build();
        return employees;
    }
    private JDataSet buildEmployeeHierarchy() {
        JDataSet employeeHierarchy = JDataSet.builder()
                .addColumn("emp_id",Integer.class,"")
                .addColumn("emp_name",String.class,"")
                .addColumn("manager_id",Integer.class,"")
                .addColumn("department",String.class,"")
                .addColumn("salary",Integer.class,"")
                .addRow(addEmRow(1, "John CEO", null, "Executive", 100000))
                .addRow(addEmRow(2, "Alice CTO", 1, "Technology", 90000))
                .addRow(addEmRow(3, "Bob CFO", 1, "Finance", 90000))
                .addRow(addEmRow(4, "Carol Dev Manager", 2, "Development", 80000))
                .addRow(addEmRow(5, "Dave QA Manager", 2, "Quality", 75000))
                .addRow(addEmRow(6, "Eve Senior Dev", 4, "Development", 70000))
                .addRow(addEmRow(7, "Frank Junior Dev", 4, "Development", 60000))
                .addRow(addEmRow(8, "Grace Senior QA", 5, "Quality", 65000))
                .addRow(addEmRow(9, "Henry Junior QA", 5, "Quality", 55000))
                .addRow(addEmRow(10, "Ivy Finance Manager", 3, "Finance", 80000))
                .addRow(addEmRow(11, "Jack Accountant", 10, "Finance", 60000))
                .build();
        return employeeHierarchy;
    }
    @Test
    public void testNonRecursiveCte() {
        String sql = "WITH department_stats AS (" +
                "  SELECT manager_id as dept_id, COUNT(*) as emp_count " +
                "  FROM employees " +
                "  WHERE manager_id IS NOT NULL " +
                "  GROUP BY manager_id" +
                ") " +
                "SELECT e.id as id,e.name as manager_name, ds.emp_count " +
                "FROM employees e " +
                "left JOIN department_stats ds ON e.id = ds.dept_id " +
                "ORDER BY ds.emp_count DESC";
        JQuickSQLExecutor executor=new JQuickSQLExecutor();
        JDataSetHolder dataSetContainer=new JDataSetHolder();
        dataSetContainer.addDataSet("employees", setupTestData());
        executor.dataSet(dataSetContainer);
        JDataSet dataSet=executor.execute(sql, JEngineEnums.LAMBDA);
        for (JRow row : dataSet.getRows()) {
            System.out.println(row);
        }
//        assertNotNull(result);
//        assertEquals(3, result.rowCount()); // CTO, CFO, Dev Manager, QA Manager
//        assertEquals(2, result.columnCount());
    }

    @Test
    public void testRecursiveCte() {
        String sql = "WITH RECURSIVE employee_hierarchy AS (" +
                "  SELECT id, name, manager_id " +
                "  FROM employees " +
                "  WHERE manager_id IS NULL " +
                "  " +
                "  UNION ALL " +
                "  " +
                "  SELECT e.id as id, e.name as name, e.manager_id"+
                "  FROM employees e " +
                "  INNER JOIN employee_hierarchy eh ON e.manager_id = eh.id" +
                ") " +
                "SELECT name, level " +
                "FROM employee_hierarchy " +
                "ORDER BY level, name";
        System.out.println(sql);
        JQuickSQLExecutor executor=new JQuickSQLExecutor();
        JDataSetHolder dataSetContainer=new JDataSetHolder();
        dataSetContainer.addDataSet("employees", setupTestData());
        dataSetContainer.addDataSet("employee_hierarchy", buildEmployeeHierarchy());
        executor.dataSet(dataSetContainer);
        JDataSet dataSet=executor.execute(sql, JEngineEnums.LAMBDA);
        for (JRow row : dataSet.getRows()) {
            System.out.println(row);
        }
        JRow ceoRow = dataSet.getRows().stream()
                .filter(row -> "CEO".equals(row.get("name")))
                .findFirst().orElse(null);
        System.out.println(ceoRow);
    }
//
//    @Test
//    public void testMultipleCtes() {
//        String sql = "WITH " +
//                "manager_counts AS (" +
//                "  SELECT manager_id, COUNT(*) as report_count " +
//                "  FROM employees " +
//                "  WHERE manager_id IS NOT NULL " +
//                "  GROUP BY manager_id" +
//                "), " +
//                "top_managers AS (" +
//                "  SELECT manager_id, report_count " +
//                "  FROM manager_counts " +
//                "  WHERE report_count >= 2" +
//                ") " +
//                "SELECT e.name as manager_name, tm.report_count " +
//                "FROM employees e " +
//                "JOIN top_managers tm ON e.id = tm.manager_id " +
//                "ORDER BY tm.report_count DESC";
//
//        JDataSet result = visitor.visit(parseSql(sql));
//        assertNotNull(result);
//        assertEquals(2, result.rowCount());
//    }

//    @Test
//    public void testCteWithParameters() {
//        String sql = "WITH filtered_employees AS (" +
//                "  SELECT * FROM employees WHERE manager_id = ?" +
//                ") " +
//                "SELECT COUNT(*) as count FROM filtered_employees";
//
//        JDataSet result = visitor.visit(parseSql(sql.replace("?", "2")));
//
//        assertNotNull(result);
//        assertEquals(1, result.rowCount());
//    }
}
