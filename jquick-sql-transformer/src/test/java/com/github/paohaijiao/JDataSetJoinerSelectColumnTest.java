package com.github.paohaijiao;

import com.github.paohaijiao.condition.JComparisonCondition;
import com.github.paohaijiao.condition.JCondition;
import com.github.paohaijiao.dataset.JColumnMeta;
import com.github.paohaijiao.dataset.JDataSet;
import com.github.paohaijiao.enums.*;
import com.github.paohaijiao.expression.*;
import com.github.paohaijiao.function.JAggregateFunction;
import com.github.paohaijiao.support.JDataSetJoiner;
import org.junit.Test;

import java.io.IOException;
import java.sql.SQLOutput;
import java.util.*;

import static org.junit.Assert.*;

public class JDataSetJoinerSelectColumnTest {
    @Test
    public void testSelectColumns() {
        JDataSet testDataSet = createTestDataSet();
        List<String> selectedColumns = Arrays.asList("name", "age");
        JDataSet result = JDataSetJoiner.selectColumns(testDataSet, selectedColumns);
        assertEquals(2, result.getColumns().size());
        assertEquals(3, result.getRows().size());
        assertTrue(result.getColumnNames().contains("name"));
        assertTrue(result.getColumnNames().contains("age"));
        assertFalse(result.getColumnNames().contains("city"));
        Map<String, Object> firstRow = result.getRows().get(0);
        assertEquals("Alice", firstRow.get("name"));
        assertEquals(25, firstRow.get("age"));
        assertNull(firstRow.get("city"));
    }

    private JDataSet createTestDataSet() {
        List<JColumnMeta> columns = Arrays.asList(
                new JColumnMeta("name", String.class, "source1"),
                new JColumnMeta("age", Integer.class, "source1"),
                new JColumnMeta("city", String.class, "source1")
        );
        List<Map<String, Object>> rows = Arrays.asList(
                createRow("Alice", 25, "New York"),
                createRow("Bob", 30, "London"),
                createRow("Charlie", 35, "Paris")
        );

        return new JDataSet(columns, rows);
    }

    private Map<String, Object> createRow(String name, int age, String city) {
        Map<String, Object> row = new HashMap<>();
        row.put("name", name);
        row.put("age", age);
        row.put("city", city);
        return row;
    }
    @Test
    public void tesstAction() throws IOException {
        // age > 30
        JCondition condition = new JComparisonCondition(
                new JColumnExpression("age"),
                JComparisonOperator.GT,
                new JLiteralExpression(30, JDataType.NUMBER)
        );
        JDataSet testDataSet = createTestDataSet();
        JDataSet filtered = JDataSetJoiner.filter(testDataSet, condition);
        System.out.println(filtered);
    }
    @Test
    public void transfer() throws IOException {
        JDataSet testData = createTestDataSet();
        Map<String, JFunctionCallExpression> transformations = new HashMap<>();
        transformations.put("name", new JFunctionCallExpression("UPPER",
                Collections.singletonList(new JColumnExpression("name"))));
        transformations.put("age", new JFunctionCallExpression("ROUND",
                Collections.singletonList(new JColumnExpression("age"))));
        JDataSet transformed = JDataSetJoiner.transform(testData, transformations);
        System.out.println(transformed);
    }
    @Test
    public void setSorted() throws IOException {
        JDataSet testData = createTestDataSet();
        List<JOrderByExpression> orderBys = Arrays.asList(
                new JOrderByExpression(new JColumnExpression("age"), JSortDirection.DESC, JNullsOrder.LAST),
                new JOrderByExpression(new JColumnExpression("name"), JSortDirection.ASC)
        );
        JDataSet sorted = JDataSetJoiner.sort(testData, orderBys);
        System.out.println(sorted);
    }
    @Test
    public void mapping() throws IOException {
        JDataSet testData = createTestDataSet();
        Map<String, JExpression> aliases = new HashMap<>();
        aliases.put("full_name", new JColumnExpression("name"));
        aliases.put("age_years", new JColumnExpression("age"));
//        aliases.put("name_length", new JFunctionCallExpression("LENGTH",
//                Collections.singletonList(new JColumnExpression("name"))));
//        aliases.put("is_adult", new JFunctionCallExpression("GREATER_THAN",
//                Arrays.asList(new JColumnExpression("age"), new JLiteralExpression(18, JDataType.NUMBER))));
        JDataSet aliasedData = JDataSetJoiner.alias(testData, aliases);
        System.out.println(aliasedData);
    }



}
