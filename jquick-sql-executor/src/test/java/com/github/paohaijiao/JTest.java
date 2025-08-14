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

import com.github.paohaijiao.engine.JEntityQueryEngine;

import com.github.paohaijiao.model.Customer;
import com.github.paohaijiao.model.Order;
import com.github.paohaijiao.model.OrderWithCustomer;
import org.junit.Test;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * packageName com.github.paohaijiao
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/8/12
 */
public class JTest {
    @Test
    public void haha() throws IOException {
        List<Order> orders = Arrays.asList(new Order(1L, 101L, new BigDecimal("100.00")), new Order(2L, 102L, new BigDecimal("200.00"))
        );
        List<Customer> customers = Arrays.asList(new Customer(101L, "Alice", "alice@example.com"), new Customer(102L, "Bob", "bob@example.com"));
        JEntityQueryEngine<OrderWithCustomer> queryEngine = new JEntityQueryEngine<>(OrderWithCustomer.class)
                        .registerEntityList(Order.class, orders)
                        .registerEntityList(Customer.class, customers);
        String sql = "SELECT o.orderId, o.amount, c.name as customerName " +
                "FROM Order o JOIN Customer c ON o.customerId = c.customerId";
        List<OrderWithCustomer> results = queryEngine.executeQuery(sql, new ArrayList<>());
        System.out.println(results);

    }


}
