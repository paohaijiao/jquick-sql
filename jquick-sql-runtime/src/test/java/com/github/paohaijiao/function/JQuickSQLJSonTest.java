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
package com.github.paohaijiao.function;

import com.github.paohaijiao.engine.JQuickSQL;
import com.github.paohaijiao.model.JSONObject;
import com.github.paohaijiao.statement.JQuickColumnMeta;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * packageName com.github.paohaijiao.demo.aggregation
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/7/27
 */
public class JQuickSQLJSonTest {

    private static JQuickSQL sql;

    @BeforeClass
    public static void setUpClass() {
        sql = JQuickSQL.embedded();
        registerTestData();
    }

    @AfterClass
    public static void tearDownClass() {
        if (sql != null) {
            sql.shutdown();
        }
    }

    private static Date getDate(String date) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            return formatter.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }

    private static void registerTestData() {
        List<JQuickColumnMeta> userColumns = Arrays.asList(
                new JQuickColumnMeta("id", Integer.class, "users"),
                new JQuickColumnMeta("name", String.class, "users"),
                new JQuickColumnMeta("age", Integer.class, "users"),
                new JQuickColumnMeta("status", String.class, "users"),
                new JQuickColumnMeta("enable", String.class, "users"),
                new JQuickColumnMeta("addr", String.class, "users"),
                new JQuickColumnMeta("birthday", Date.class, "users"),
                new JQuickColumnMeta("salary", Double.class, "users"),
                new JQuickColumnMeta("detail", JSONObject.class, "users")
        );

        List<JQuickRow> userRows = Arrays.asList(
                // 1. Alice - 带JSON详情
                createRow(
                        "id", 1,
                        "name", "Alice",
                        "age", 25,
                        "status", "active",
                        "enable", true,
                        "addr", "beijing",
                        "birthday", getDate("2020-04-09"),
                        "salary", 5000.0,
                        "detail", createJsonObject(
                                "department", "Engineering",
                                "position", "Senior Developer",
                                "hobbies", Arrays.asList("reading", "swimming"),
                                "contact", createJsonObject(
                                        "email", "alice@example.com",
                                        "phone", "13800001111"
                                ),
                                "score", 95.5,
                                "isActive", true
                        )
                ),
                // 2. Bob - 带JSON详情
                createRow(
                        "id", 2,
                        "name", "Bob",
                        "age", 30,
                        "status", "active",
                        "enable", true,
                        "addr", "shanghai",
                        "birthday", getDate("1991-08-09"),
                        "salary", 6000.0,
                        "detail", createJsonObject(
                                "department", "Marketing",
                                "position", "Marketing Manager",
                                "hobbies", Arrays.asList("traveling", "photography"),
                                "contact", createJsonObject(
                                        "email", "bob@example.com",
                                        "phone", "13800002222"
                                ),
                                "score", 88.0,
                                "isActive", true
                        )
                ),
                // 3. Charlie - 带JSON详情
                createRow(
                        "id", 3,
                        "name", "Charlie",
                        "age", 20,
                        "status", "pending",
                        "enable", false,
                        "addr", "chengdu",
                        "birthday", getDate("1988-07-12"),
                        "salary", 4500.0,
                        "detail", createJsonObject(
                                "department", "Finance",
                                "position", "Junior Accountant",
                                "hobbies", Arrays.asList("gaming"),
                                "contact", createJsonObject(
                                        "email", "charlie@example.com",
                                        "phone", "13800003333"
                                ),
                                "score", 72.5,
                                "isActive", false
                        )
                ),
                // 4. David - 带JSON详情
                createRow(
                        "id", 4,
                        "name", "David",
                        "age", 35,
                        "status", "inactive",
                        "enable", true,
                        "addr", "xian",
                        "birthday", getDate("1955-11-29"),
                        "salary", 7000.0,
                        "detail", createJsonObject(
                                "department", "Human Resources",
                                "position", "HR Director",
                                "hobbies", Arrays.asList("reading", "running", "cooking"),
                                "contact", createJsonObject(
                                        "email", "david@example.com",
                                        "phone", "13800004444"
                                ),
                                "score", 92.0,
                                "isActive", true
                        )
                ),
                // 5. Eve - 带JSON详情
                createRow(
                        "id", 5,
                        "name", "Eve",
                        "age", 28,
                        "status", "active",
                        "enable", true,
                        "addr", "chongqing",
                        "birthday", getDate("2003-07-12"),
                        "salary", 5500.0,
                        "detail", createJsonObject(
                                "department", "Engineering",
                                "position", "Frontend Developer",
                                "hobbies", Arrays.asList("painting", "music"),
                                "contact", createJsonObject(
                                        "email", "eve@example.com",
                                        "phone", "13800005555"
                                ),
                                "score", 90.0,
                                "isActive", true
                        )
                ),
                // 6. Martin - 带JSON详情
                createRow(
                        "id", 6,
                        "name", "Martin",
                        "age", 30,
                        "status", "active",
                        "enable", true,
                        "addr", "guangzhou",
                        "birthday", getDate("1978-06-30"),
                        "salary", 6500.0,
                        "detail", createJsonObject(
                                "department", "Technology",
                                "position", "Architect",
                                "hobbies", Arrays.asList("chess", "coding"),
                                "contact", createJsonObject(
                                        "email", "martin@example.com",
                                        "phone", "13800006666"
                                ),
                                "score", 98.5,
                                "isActive", true
                        )
                ),
                // 7. Davila - 带JSON详情
                createRow(
                        "id", 7,
                        "name", "Davila",
                        "age", 39,
                        "status", "active",
                        "enable", true,
                        "addr", null,
                        "birthday", getDate("1999-06-30"),
                        "salary", 8000.0,
                        "detail", createJsonObject(
                                "department", "Engineering",
                                "position", "Tech Lead",
                                "hobbies", Arrays.asList("reading", "hiking", "swimming"),
                                "contact", createJsonObject(
                                        "email", "davila@example.com",
                                        "phone", "13800007777"
                                ),
                                "score", 96.0,
                                "isActive", true
                        )
                )
        );

        sql.registerTable("users", userColumns, userRows);
    }

    private static JQuickRow createRow(Object... keyValues) {
        JQuickRow row = new JQuickRow();
        for (int i = 0; i < keyValues.length; i += 2) {
            row.put((String) keyValues[i], keyValues[i + 1]);
        }
        return row;
    }


    /**
     * 创建JSONObject对象（支持嵌套）
     */
    private static JSONObject createJsonObject(Object... keyValues) {
        JSONObject json = new JSONObject();
        for (int i = 0; i < keyValues.length; i += 2) {
            json.put((String) keyValues[i], keyValues[i + 1]);
        }
        return json;
    }

    @Test
    public void testCountAll() {
        JQuickDataSet result = sql.execute("SELECT name,age,status,enable,jsonPath(detail,'$.position') FROM users");
        result.printTable();
    }
}
