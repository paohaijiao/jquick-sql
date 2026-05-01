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

import com.github.paohaijiao.function.JQuickSqlAggregateFunctionFactory;
import com.github.paohaijiao.statement.JQuickColumnMeta;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;
import com.github.paohaijiao.support.JQuickSqlOLAPOperations;
import org.junit.Test;

import java.util.*;
import java.util.function.Function;

/**
 * packageName PACKAGE_NAME
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/8/17
 */
public class JOlapTest {
    private static JQuickRow createRow(String region, String product, String quarter, double sales) {
        JQuickRow row = new JQuickRow();
        row.put("region", region);
        row.put("product", product);
        row.put("quarter", quarter);
        row.put("sales", sales);
        return row;
    }

    private static JQuickDataSet buildDataSet() {
        List<JQuickColumnMeta> columns = Arrays.asList(
                new JQuickColumnMeta("region", String.class, "source"),
                new JQuickColumnMeta("product", String.class, "source"),
                new JQuickColumnMeta("quarter", String.class, "source"),
                new JQuickColumnMeta("sales", Double.class, "source")
        );

        List<JQuickRow> rows = new ArrayList<>();
        rows.add(new JQuickRow(createRow("East", "A", "Q1", 100.0)));
        rows.add(new JQuickRow(createRow("East", "A", "Q2", 150.0)));
        rows.add(new JQuickRow(createRow("East", "B", "Q1", 200.0)));
        rows.add(new JQuickRow(createRow("West", "A", "Q1", 120.0)));
        rows.add(new JQuickRow(createRow("West", "B", "Q2", 180.0)));
        JQuickDataSet dataset = new JQuickDataSet(columns, rows);
        return dataset;
    }

    @Test
    public void rollUp() {
        Map<String, Function<List<Object>, Object>> aggregations = new HashMap<>();
        aggregations.put("sales", JQuickSqlAggregateFunctionFactory.getFunction(JQuickSqlAggregateFunctionFactory.SUM));
        aggregations = Collections.unmodifiableMap(aggregations);
        JQuickDataSet rolledUp = JQuickSqlOLAPOperations.rollUp(
                buildDataSet(),
                Arrays.asList("region", "product"),
                aggregations
        );
        for (JQuickRow row : rolledUp.getRows()) {
            System.out.println(row);
        }
    }

    @Test
    public void drilledDown() {
        Map<String, Function<List<Object>, Object>> aggregations = new HashMap<>();
        aggregations.put("sales", JQuickSqlAggregateFunctionFactory.getFunction(JQuickSqlAggregateFunctionFactory.SUM));
        aggregations = Collections.unmodifiableMap(aggregations);
        JQuickDataSet drilledDown = JQuickSqlOLAPOperations.drillDown(
                buildDataSet(),
                Collections.singletonList("quarter"),
                aggregations
        );
        for (JQuickRow row : drilledDown.getRows()) {
            System.out.println(row);
        }
    }

    @Test
    public void slice() {
        JQuickDataSet sliced = JQuickSqlOLAPOperations.slice(buildDataSet(), "region", "East");
        for (JQuickRow row : sliced.getRows()) {
            System.out.println(row);
        }
    }

    @Test
    public void diced() {
        Map<String, Object> map = new HashMap<>();
        map.put("region", "East");
        map.put("product", "A");
        JQuickDataSet diced = JQuickSqlOLAPOperations.dice(
                buildDataSet(),
                map
        );
        for (JQuickRow row : diced.getRows()) {
            System.out.println(row);
        }
    }

    @Test
    public void pivoted() {
        JQuickDataSet pivoted = JQuickSqlOLAPOperations.pivot(
                buildDataSet(),
                "quarter",
                "sales",
                JQuickSqlAggregateFunctionFactory.getFunction(JQuickSqlAggregateFunctionFactory.SUM)
        );
        for (JQuickRow row : pivoted.getRows()) {
            System.out.println(row);
        }
    }

}
