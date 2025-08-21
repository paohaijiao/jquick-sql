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
import com.github.paohaijiao.support.JDataSetHolder;
import org.junit.Test;

/**
 * packageName com.github.paohaijiao.value
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/8/17
 */
public class JSelectTest {
    private static JDataSet createOrdersDataSet() {
        JDataSet.Builder builder = JDataSet.builder();
        builder.addColumn("order_id", Integer.class, "orders")
                .addColumn("user_id", Integer.class, "orders")
                .addColumn("amount", Double.class, "orders");

        JRow row1 = new JRow("orders");
        row1.put("order_id", 1001);
        row1.put("user_id", 1);
        row1.put("amount", 99.99);

        JRow row2 = new JRow("orders");
        row2.put("order_id", 1002);
        row2.put("user_id", 2);
        row2.put("amount", 149.99);

        builder.addRow(row1).addRow(row2);
        return builder.build();
    }
    @Test
    public void limit() {
        String rule="select * from orders limit 1";
        JQuickSQLExecutor executor=new JQuickSQLExecutor();
        JDataSetHolder dataSetContainer=new JDataSetHolder();
        dataSetContainer.addDataSet("orders",createOrdersDataSet());
        executor.dataSet(dataSetContainer);
        JDataSet dataSet=executor.execute(rule, JEngineEnums.LAMBDA);
        for (JRow row : dataSet.getRows()) {
            System.out.println(row);
        }
    }
    @Test
    public void limitOffset() {
        String rule="select * from orders limit  1 , 1";
        JQuickSQLExecutor executor=new JQuickSQLExecutor();
        JDataSetHolder dataSetContainer=new JDataSetHolder();
        dataSetContainer.addDataSet("orders",createOrdersDataSet());
        executor.dataSet(dataSetContainer);
        JDataSet dataSet=executor.execute(rule, JEngineEnums.LAMBDA);
        for (JRow row : dataSet.getRows()) {
            System.out.println(row);
        }
    }



}
