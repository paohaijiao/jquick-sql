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
public class JoinTest {
    @Test
    public void leftjoin() {
        String rule="select * from user a left join user_order b on a.id=b.user_id ";
        JQuickSQLExecutor executor=new JQuickSQLExecutor();
        JDataSetHolder dataSetContainer=new JDataSetHolder();
        dataSetContainer.addDataSet("user", JDataSetFactory.createUsersDataSet());
        dataSetContainer.addDataSet("user_order", JDataSetFactory.createOrdersDataSet());
        executor.dataSet(dataSetContainer);
        JDataSet dataSet=executor.execute(rule, JEngineEnums.LAMBDA);
        for (JRow row : dataSet.getRows()) {
            System.out.println(row);
        }
    }


}
