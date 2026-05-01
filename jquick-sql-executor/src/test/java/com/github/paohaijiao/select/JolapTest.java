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

import com.github.paohaijiao.enums.JQuickSqlEngineEnums;
import com.github.paohaijiao.executor.JQuickSQLExecutor;
import com.github.paohaijiao.model.JOLapDataSetFactory;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;
import com.github.paohaijiao.support.JQuickSqlDataSetHolder;
import org.junit.Test;

/**
 * packageName com.github.paohaijiao.select
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/8/31
 */
public class JolapTest {
    @Test
    public void rollup() {
        String rule = "SELECT region, department, category, SUM(sales) as total_sales\n" +
                "FROM sales_data\n" +
                "ROLLUP (region, department, category)\n";
        //"ORDER BY region, department, category;";
        System.out.println(rule);
        JQuickSQLExecutor executor = new JQuickSQLExecutor();
        JQuickSqlDataSetHolder dataSetContainer = new JQuickSqlDataSetHolder();
        dataSetContainer.addDataSet("sales_data", JOLapDataSetFactory.createSalesDataSet());
        executor.dataSet(dataSetContainer);
        JQuickDataSet dataSet = executor.execute(rule, JQuickSqlEngineEnums.LAMBDA);
        for (JQuickRow row : dataSet.getRows()) {
            System.out.println(row);
        }
    }

    @Test
    public void drillDown() {
        String rule = "SELECT region, department, SUM(sales) as total_sales\n" +
                "FROM sales_data\n" +
                "DRILLDOWN (region, department)";
        //"ORDER BY region, department, category;";
        System.out.println(rule);
        JQuickSQLExecutor executor = new JQuickSQLExecutor();
        JQuickSqlDataSetHolder dataSetContainer = new JQuickSqlDataSetHolder();
        dataSetContainer.addDataSet("sales_data", JOLapDataSetFactory.createSalesDataSet());
        executor.dataSet(dataSetContainer);
        JQuickDataSet dataSet = executor.execute(rule, JQuickSqlEngineEnums.LAMBDA);
        for (JQuickRow row : dataSet.getRows()) {
            System.out.println(row);
        }
    }

    @Test
    public void slice() {
        String rule = "SELECT * FROM sales_data\n" +
                "SLICE (department = 'Electronics')";
        System.out.println(rule);
        JQuickSQLExecutor executor = new JQuickSQLExecutor();
        JQuickSqlDataSetHolder dataSetContainer = new JQuickSqlDataSetHolder();
        dataSetContainer.addDataSet("sales_data", JOLapDataSetFactory.createSalesDataSet());
        executor.dataSet(dataSetContainer);
        JQuickDataSet dataSet = executor.execute(rule, JQuickSqlEngineEnums.LAMBDA);
        for (JQuickRow row : dataSet.getRows()) {
            System.out.println(row);
        }
    }

    @Test
    public void dice() {
        String rule = "SELECT * FROM sales_data\n" +
                "DICE (region = 'North', department = 'Electronics')";
        System.out.println(rule);
        JQuickSQLExecutor executor = new JQuickSQLExecutor();
        JQuickSqlDataSetHolder dataSetContainer = new JQuickSqlDataSetHolder();
        dataSetContainer.addDataSet("sales_data", JOLapDataSetFactory.createSalesDataSet());
        executor.dataSet(dataSetContainer);
        JQuickDataSet dataSet = executor.execute(rule, JQuickSqlEngineEnums.LAMBDA);
        for (JQuickRow row : dataSet.getRows()) {
            System.out.println(row);
        }
    }

    @Test
    public void pivot() {
        String rule = "SELECT * FROM sales_data\n" +
                "PIVOT (department, sales, SUM)";
        System.out.println(rule);
        JQuickSQLExecutor executor = new JQuickSQLExecutor();
        JQuickSqlDataSetHolder dataSetContainer = new JQuickSqlDataSetHolder();
        dataSetContainer.addDataSet("sales_data", JOLapDataSetFactory.createSalesDataSet());
        executor.dataSet(dataSetContainer);
        JQuickDataSet dataSet = executor.execute(rule, JQuickSqlEngineEnums.LAMBDA);
        for (JQuickRow row : dataSet.getRows()) {
            System.out.println(row);
        }
    }
}
