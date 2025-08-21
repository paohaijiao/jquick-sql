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

import com.github.paohaijiao.dataset.JColumnMeta;
import com.github.paohaijiao.dataset.JDataSet;
import com.github.paohaijiao.dataset.JRow;
import com.github.paohaijiao.function.JAggregateFunctionFactory;
import com.github.paohaijiao.support.JOLAPOperations;
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
    private static JRow createRow(String region, String product, String quarter, double sales) {
        JRow row = new JRow();
        row.put("region", region);
        row.put("product", product);
        row.put("quarter", quarter);
        row.put("sales", sales);
        return row;
    }
    private static JDataSet  buildDataSet(){
        List<JColumnMeta> columns = Arrays.asList(
                new JColumnMeta("region", String.class, "source"),
                new JColumnMeta("product", String.class, "source"),
                new JColumnMeta("quarter", String.class, "source"),
                new JColumnMeta("sales", Double.class, "source")
        );

        List<JRow> rows = new ArrayList<>();
        rows.add(new JRow(createRow( "East", "A", "Q1",  100.0)));
        rows.add(new JRow(createRow( "East", "A", "Q2",  150.0)));
        rows.add(new JRow(createRow( "East",  "B", "Q1",  200.0)));
        rows.add(new JRow(createRow( "West",  "A",  "Q1",  120.0)));
        rows.add(new JRow(createRow( "West",  "B", "Q2",  180.0)));

        JDataSet dataset = new JDataSet(columns, rows);
        return dataset;
    }
    @Test
    public void rollUp() {
        Map<String, Function<List<Object>, Object>> aggregations =new HashMap<>();
        aggregations.put("sales", JAggregateFunctionFactory.getFunction(JAggregateFunctionFactory.SUM));
        aggregations = Collections.unmodifiableMap(aggregations);
        JDataSet rolledUp = JOLAPOperations.rollUp(
                buildDataSet(),
                Arrays.asList("region", "product"),
                aggregations
        );
        for (JRow row:rolledUp.getRows()){
            System.out.println(row);
        }
    }
    @Test
    public void drilledDown() {
        Map<String, Function<List<Object>, Object>> aggregations =new HashMap<>();
        aggregations.put("sales", JAggregateFunctionFactory.getFunction(JAggregateFunctionFactory.SUM));
        aggregations = Collections.unmodifiableMap(aggregations);
        JDataSet drilledDown = JOLAPOperations.drillDown(
                buildDataSet(),
                Collections.singletonList("quarter"),
                aggregations
        );
        for (JRow row:drilledDown.getRows()){
            System.out.println(row);
        }
    }
    @Test
    public void slice() {
        JDataSet sliced = JOLAPOperations.slice(buildDataSet(), "region", "East");
        for (JRow row:sliced.getRows()){
            System.out.println(row);
        }
    }
    @Test
    public void diced () {
        Map<String, Object> map=new HashMap<>();
        map.put("region", "East");
        map.put("product", "A");
        JDataSet diced = JOLAPOperations.dice(
                buildDataSet(),
                map
        );
        for (JRow row:diced.getRows()){
            System.out.println(row);
        }
    }
    @Test
    public void pivoted  () {
        JDataSet pivoted = JOLAPOperations.pivot(
                buildDataSet(),
                "quarter",
                "sales",
                JAggregateFunctionFactory.getFunction(JAggregateFunctionFactory.SUM)
        );
        for (JRow row:pivoted.getRows()){
            System.out.println(row);
        }
    }

}
