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

import com.github.paohaijiao.domain.JQuickTable;
import com.github.paohaijiao.param.JContext;
import com.github.paohaijiao.statement.JQuickColumnMeta;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;
import com.github.paohaijiao.xml.factory.JQuickFactory;
import com.github.paohaijiao.xml.factory.JQuickXmlFactory;
import com.github.paohaijiao.xml.service.JQuickUserService;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * packageName com.github.paohaijiao.xml
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/7/31
 */
public class JQuickLimitTest {

    private static Date getDate(String date)  {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            return formatter.parse(date);
        }catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }
    private static List<JQuickColumnMeta> getUserColumns() {
        List<JQuickColumnMeta> userColumns = Arrays.asList(
                new JQuickColumnMeta("id", Integer.class, "users"),
                new JQuickColumnMeta("name", String.class, "users"),
                new JQuickColumnMeta("age", Integer.class, "users"),
                new JQuickColumnMeta("status", String.class, "users"),
                new JQuickColumnMeta("enable", String.class, "users"),
                new JQuickColumnMeta("addr", String.class, "users"),
                new JQuickColumnMeta("birthday", Date.class, "users")
        );
        return userColumns;
    }
    private static List<JQuickRow> getUserRows() {
        List<JQuickRow> userRows = Arrays.asList(
                createRow("id", 1, "name", "Alice", "age", 25, "status", "active","enable",true,"addr","beijing","birthday",getDate("2020-04-09")),
                createRow("id", 2, "name", "Bob", "age", 30, "status", "active","enable",true,"addr","shanghai","birthday",getDate("1991-08-09")),
                createRow("id", 3, "name", "Charlie", "age", 20, "status", "pending","enable",false,"addr","chengdu","birthday",getDate("1988-07-12")),
                createRow("id", 4, "name", "David", "age", 35, "status", "inactive","enable",true,"addr","xian","birthday",getDate("1955-11-29")),
                createRow("id", 5, "name", "Eve", "age", 28, "status", "active","enable",true,"addr","chongqing","birthday",getDate("2003-07-12")),
                createRow("id", 6, "name", "Martin", "age", 30, "status", "active","enable",true,"addr","guangzhou","birthday",getDate("1978-06-30")),
                createRow("id", 7, "name", "Davila", "age", 39, "status", "active","enable",true,"addr",null,"birthday",getDate("1999-06-30"))
        );
        return userRows;

    }

    private static JQuickRow createRow(Object... keyValues) {
        JQuickRow row = new JQuickRow();
        for (int i = 0; i < keyValues.length; i += 2) {
            row.put((String) keyValues[i], keyValues[i + 1]);
        }
        return row;
    }


    @Test
    public void limit() {
        JQuickTable table=new JQuickTable("users",getUserColumns(),getUserRows());
        JQuickJavaXmlParseFactory handler=new JQuickJavaXmlParseFactory(Arrays.asList(table));
        JQuickFactory factory = new JQuickXmlFactory(handler,"jquick-sql.xml");
        JQuickUserService orderService = factory.createApi(JQuickUserService.class);
        JQuickDataSet dataSet= orderService.getUsers(2);
        dataSet.printTable();
    }
    @Test
    public void limit1() {
        JQuickTable table=new JQuickTable("users",getUserColumns(),getUserRows());
        JQuickJavaXmlParseFactory handler=new JQuickJavaXmlParseFactory(Arrays.asList(table));
        JQuickFactory factory = new JQuickXmlFactory(handler,"jquick-sql.xml");
        JQuickUserService orderService = factory.createApi(JQuickUserService.class);
        List<JQuickRow> dataSet= orderService.getUsersOne(2);
        System.out.println(dataSet);
    }
    @Test
    public void limit3() {
        JQuickTable table=new JQuickTable("users",getUserColumns(),getUserRows());
        JQuickJavaXmlParseFactory handler=new JQuickJavaXmlParseFactory(Arrays.asList(table));
        JQuickFactory factory = new JQuickXmlFactory(handler,"jquick-sql.xml");
        JQuickUserService orderService = factory.createApi(JQuickUserService.class);
        Integer dataSet= orderService.getUsersTwo(2);
        System.out.println(dataSet);
    }
}
