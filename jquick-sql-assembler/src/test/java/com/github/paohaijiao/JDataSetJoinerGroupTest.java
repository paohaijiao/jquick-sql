package com.github.paohaijiao;

import com.github.paohaijiao.condition.JComparisonCondition;
import com.github.paohaijiao.condition.JCondition;
import com.github.paohaijiao.dataset.JColumnMeta;
import com.github.paohaijiao.dataset.JDataSet;
import com.github.paohaijiao.dataset.JRow;
import com.github.paohaijiao.enums.*;
import com.github.paohaijiao.expression.*;
import com.github.paohaijiao.function.JAggregateFunction;
import com.github.paohaijiao.support.JDataSetJoiner;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

import static org.junit.Assert.*;

public class JDataSetJoinerGroupTest {
    @Test
    public void testSelectColumns() {

        JDataSet testData = createAggregationTestData();
        List<String> groupBy = Arrays.asList("department");
        Map<String, JAggregateExpression> aggregations = new HashMap<>();
        aggregations.put("avg_salary", new JAggregateExpression(
                new JAggregateFunction(JAggregateType.AVG, new JColumnExpression("salary"))
        ));
        aggregations.put("max_experience", new JAggregateExpression(
                new JAggregateFunction(JAggregateType.MAX, new JColumnExpression("years_of_service"))
        ));

        aggregations.put("employee_count", new JAggregateExpression(
                new JAggregateFunction(JAggregateType.COUNT, new JColumnExpression("employee_name"))
        ));

//        aggregations.put("manager_count", new JAggregateExpression(
//                new JAggregateFunction(JAggregateType.SUM,
//                        new JFunctionCallExpression("CAST",
//                                Arrays.asList(
//                                        new JColumnExpression("is_manager"),
//                                        new JLiteralExpression("INT", JDataType.STRING)
//                                )
//                        )
//                )
//        ));

        JDataSet result = JDataSetJoiner.aggregate(testData, groupBy, aggregations);
        assertEquals(3, result.size()); // 3个部门
        Optional<JRow> engDept = result.getRows().stream()
                .filter(row -> "Engineering".equals(row.get("department")))
                .findFirst();
        assertTrue(engDept.isPresent());
        assertEquals(8266.67, (Double) engDept.get().get("avg_salary"), 0.01); // (8500+7500+8800)/3
        assertEquals(4, engDept.get().get("max_experience"));
        assertEquals(3L, engDept.get().get("employee_count"));
        assertEquals(1L, engDept.get().get("manager_count")); // 只有Alice是经理
    }

    public static JDataSet createAggregationTestData() {
        List<JColumnMeta> columns = Arrays.asList(
                new JColumnMeta("department", String.class, "hr"),
                new JColumnMeta("employee_name", String.class, "hr"),
                new JColumnMeta("salary", Double.class, "finance"),
                new JColumnMeta("years_of_service", Integer.class, "hr"),
                new JColumnMeta("is_manager", Boolean.class, "hr")
        );

        List<JRow> rows = Arrays.asList(
                createEmployeeRow("Engineering", "Alice", 8500.0, 3, true),
                createEmployeeRow("Engineering", "Bob", 7500.0, 2, false),
                createEmployeeRow("Marketing", "Charlie", 9200.0, 5, true),
                createEmployeeRow("Marketing", "David", 6800.0, 1, false),
                createEmployeeRow("Finance", "Eve", 10500.0, 7, true),
                createEmployeeRow("Finance", "Frank", 7800.0, 2, false),
                createEmployeeRow("Engineering", "Grace", 8800.0, 4, false)
        );

        return new JDataSet(columns, rows);
    }

    private static JRow createEmployeeRow(String department, String name,
                                                         double salary, int years, boolean isManager) {
        JRow row = new JRow();
        row.put("department", department);
        row.put("employee_name", name);
        row.put("salary", salary);
        row.put("years_of_service", years);
        row.put("is_manager", isManager);
        return row;
    }


}
