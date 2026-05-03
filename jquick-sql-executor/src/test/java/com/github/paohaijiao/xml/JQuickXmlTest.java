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
package com.github.paohaijiao.xml;

import com.github.paohaijiao.config.JQuickClientConfig;
import com.github.paohaijiao.environment.JQuickSQLRuntimeEnvironment;
import com.github.paohaijiao.executor.JQuickSQLExecutor;
import com.github.paohaijiao.param.JContext;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.xml.factory.JQuickFactory;
import com.github.paohaijiao.xml.factory.JQuickXmlFactory;
import com.github.paohaijiao.xml.service.JQuickOrderService;
import org.junit.Test;

import java.util.HashMap;

import static com.github.paohaijiao.select.JSelectTest.createOrdersDataSet;

/**
 * packageName com.github.paohaijiao.xml
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/5/3
 */
public class JQuickXmlTest {
    @Test
    public void limit() {
        JQuickClientConfig client=new JQuickClientConfig();
        HashMap<String, JQuickDataSet> datasetMap=new HashMap<>();
        datasetMap.put("orders",createOrdersDataSet());
        JQuickSQLRuntimeEnvironment environment=new JQuickSQLRuntimeEnvironment("local",client,datasetMap);
        JContext context=new JContext();
        context.put("limit",2);
        environment.setContext(context);
        JQuickSQLXmlParseFactory handler=new JQuickSQLXmlParseFactory(environment);
        JQuickFactory factory = new JQuickXmlFactory(handler,"jquick-sql.xml");
        JQuickOrderService orderService = factory.createApi(JQuickOrderService.class);
        JQuickDataSet dataSet= orderService.getOrders(2);
        dataSet.printTable();
    }
}
