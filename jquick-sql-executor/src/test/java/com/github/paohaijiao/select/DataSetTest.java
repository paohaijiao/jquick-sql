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
import com.github.paohaijiao.support.JDataSetHolder;
import org.junit.Test;

/**
 * packageName com.github.paohaijiao.value
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/8/17
 */
public class DataSetTest {

    @Test
    public void union() {
        String rule="select * from user1 a union select * from user2 b";
        JQuickSQLExecutor executor=new JQuickSQLExecutor();
        JDataSetHolder dataSetContainer=new JDataSetHolder();
        dataSetContainer.addDataSet("user1", JDataSetFactory.createUsersDataSet());
        dataSetContainer.addDataSet("user2", JDataSetFactory.createUsersDataSet1());
        executor.dataSet(dataSetContainer);
        JDataSet dataSet=executor.execute(rule, JEngineEnums.LAMBDA);
        for (JRow row : dataSet.getRows()) {
            System.out.println(row);
        }
    }
    @Test
    public void minus() {
        String rule="select * from user1 a minus select * from user2 b";
        JQuickSQLExecutor executor=new JQuickSQLExecutor();
        JDataSetHolder dataSetContainer=new JDataSetHolder();
        dataSetContainer.addDataSet("user1", JDataSetFactory.createUsersDataSet());
        dataSetContainer.addDataSet("user2", JDataSetFactory.createUsersDataSet1());
        executor.dataSet(dataSetContainer);
        JDataSet dataSet=executor.execute(rule, JEngineEnums.LAMBDA);
        for (JRow row : dataSet.getRows()) {
            System.out.println(row);
        }
    }
    @Test
    public void intersect() {
        String rule="select * from user1 a intersect select * from user2 b";
        JQuickSQLExecutor executor=new JQuickSQLExecutor();
        JDataSetHolder dataSetContainer=new JDataSetHolder();
        dataSetContainer.addDataSet("user1", JDataSetFactory.createUsersDataSet());
        dataSetContainer.addDataSet("user2", JDataSetFactory.createUsersDataSet1());
        executor.dataSet(dataSetContainer);
        JDataSet dataSet=executor.execute(rule, JEngineEnums.LAMBDA);
        for (JRow row : dataSet.getRows()) {
            System.out.println(row);
        }
    }

}
