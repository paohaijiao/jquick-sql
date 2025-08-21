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
package com.github.paohaijiao;

import com.github.paohaijiao.dataset.JDataSet;
import com.github.paohaijiao.dataset.JRow;
import com.github.paohaijiao.func.JoinCondition;
import com.github.paohaijiao.support.JDataSetJoiner;
import org.junit.Test;

import java.util.Map;

/**
 * packageName com.github.paohaijiao
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/8/12
 */
public class JDataSetTest {
    public static JDataSet buildUserDataSet() {
        return JDataSet.builder()
                .addColumn("id", Integer.class, "user")
                .addColumn("name", String.class, "user")
                .addColumn("age", Integer.class, "user")
                .addRow(buildRow("id", 1, "name", "Alice", "age", 25))
                .addRow(buildRow("id", 2, "name", "Bob", "age", 30))
                .addRow(buildRow("id", 3, "name", "Charlie", "age", 22))
                .build();
    }

    public static JDataSet buildOrderDataSet() {
        return JDataSet.builder()
                .addColumn("order_id", Integer.class, "order")
                .addColumn("user_id", Integer.class, "order")
                .addColumn("amount", Double.class, "order")
                .addRow(buildRow("order_id", 101, "user_id", 1, "amount", 99.99))
                .addRow(buildRow("order_id", 102, "user_id", 2, "amount", 199.99))
                .addRow(buildRow("order_id", 103, "user_id", 4, "amount", 59.99))
                .build();
    }

    public static JDataSet buildUserAddressDataSet() {
        return JDataSet.builder()
                .addColumn("id", Integer.class, "address")
                .addColumn("name", String.class, "address")
                .addColumn("city", String.class, "address")
                .addRow(buildRow("id", 1, "name", "Alice", "city", "New York"))
                .addRow(buildRow("id", 2, "name", "Bob", "city", "London"))
                .addRow(buildRow("id", 4, "name", "David", "city", "Tokyo")) // 无匹配用户
                .build();
    }

    private static JRow buildRow(Object... keyValues) {
        JRow row = new JRow();
        for (int i = 0; i < keyValues.length; i += 2) {
            row.put((String) keyValues[i], keyValues[i + 1]);
        }
        return row;
    }
    @Test
    public void testInnerJoin() {
        JDataSet users = buildUserDataSet();
        JDataSet orders = buildOrderDataSet();
        JoinCondition condition = JoinCondition.equals("id", "user_id");
        JDataSet result = JDataSetJoiner.innerJoin(users, orders, condition);
        for (Map<String, Object> r: result.getRows()) {
            System.out.println(r);
        }
    }

    @Test
    public void testLeftJoin() {
        JDataSet users = buildUserDataSet();
        JDataSet orders = buildOrderDataSet();
        JoinCondition condition = JoinCondition.equals("id", "user_id");
        JDataSet result = JDataSetJoiner.leftJoin(users, orders, condition);
        for (Map<String, Object> r: result.getRows()) {
            System.out.println(r);
        }
    }

    @Test
    public  void testFullOuterJoin() {
        JDataSet users = buildUserDataSet();
        JDataSet orders = buildOrderDataSet();
        JoinCondition condition = JoinCondition.equals("id", "user_id");
        JDataSet result = JDataSetJoiner.fullOuterJoin(users, orders, condition);
        for (Map<String, Object> r: result.getRows()) {
            System.out.println(r);
        }
    }

    @Test
    public void testCrossJoin() {
        JDataSet users = buildUserDataSet();
        JDataSet orders = buildOrderDataSet();
        JDataSet result = JDataSetJoiner.crossJoin(users, orders);
        for (Map<String, Object> r: result.getRows()) {
            System.out.println(r);
        }
    }

    @Test
    public void testNaturalJoin() {
        JDataSet users = buildUserDataSet();
        JDataSet addresses = buildUserAddressDataSet();
        JDataSet result = JDataSetJoiner.naturalJoin(users, addresses);
        for (Map<String, Object> r: result.getRows()) {
            System.out.println(r);
        }
    }

    @Test
    public void testUnion() {
        JDataSet ds1 = buildUserDataSet();
        JDataSet ds2 = buildUserDataSet();
        JDataSet result = JDataSetJoiner.union(ds1, ds2);
        for (Map<String, Object> r: result.getRows()) {
            System.out.println(r);
        }
    }

    @Test
    public void testIntersect() {
        JDataSet ds1 = buildUserDataSet();
        JDataSet ds2 = JDataSet.builder()
                .addColumn("id", Integer.class, "tmp")
                .addColumn("name", String.class, "tmp")
                .addColumn("age", Integer.class, "tmp")
                .addRow(buildRow("id", 1, "name", "Alice", "age", 25))
                .addRow(buildRow("id", 4, "name", "David", "age", 40))
                .build();
        JDataSet result = JDataSetJoiner.intersect(ds1, ds2);
        for (Map<String, Object> r: result.getRows()) {
            System.out.println(r);
        }
    }

    @Test
    public void testMinus() {
        JDataSet ds1 =buildUserDataSet();
        JDataSet ds2 = JDataSet.builder()
                .addColumn("id", Integer.class, "tmp")
                .addColumn("name", String.class, "tmp")
                .addColumn("age", Integer.class, "tmp")
                .addRow(buildRow("id", 1, "name", "Alice", "age", 25))
                .build();
        JDataSet result = JDataSetJoiner.minus(ds1, ds2);
        for (Map<String, Object> r: result.getRows()) {
            System.out.println(r);
        }
    }
}
