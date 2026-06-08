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
package com.github.paohaijiao.distribute.nodeExecutor;

import com.github.paohaijiao.datasource.JQuickDataSourceManager;
import com.github.paohaijiao.distributed.JQuickDistributedPlan;
import com.github.paohaijiao.distributed.worker.*;
import com.github.paohaijiao.enums.JQuickBinaryOperator;
import com.github.paohaijiao.enums.JQuickSQLOperationType;
import com.github.paohaijiao.expression.domain.JQuickBinaryExpression;
import com.github.paohaijiao.expression.domain.JQuickColumnRefExpression;
import com.github.paohaijiao.expression.domain.JQuickLiteralExpression;
import com.github.paohaijiao.fragment.JQuickFragmenter;
import com.github.paohaijiao.function.manager.JQuickMethodInvocationManager;
import com.github.paohaijiao.logic.JQuickLogicalPlanNode;
import com.github.paohaijiao.logic.domain.JQuickFilterNode;
import com.github.paohaijiao.logic.domain.JQuickProjectNode;
import com.github.paohaijiao.logic.domain.JQuickSetOperationNode;
import com.github.paohaijiao.logic.domain.JQuickTableScanNode;
import com.github.paohaijiao.logic2physical.JQuickPhysicalPlanGenerator;
import com.github.paohaijiao.physical.JQuickPhysicalPlanNode;
import com.github.paohaijiao.physical.node.JQuickSetOperationPhysicalNode;
import com.github.paohaijiao.proto.JQuickExecuteTaskRequest;
import com.github.paohaijiao.statement.JQuickColumnMeta;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * packageName com.github.paohaijiao.physical
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/5/24
 */
public class JQuickDatasetOpTest {

    private static final String TABLE_USERS = "users";
    private static final String TABLE_ORDERS_2023 = "orders_2023";
    private static final String TABLE_ORDERS_2024 = "orders_2024";
    private static final String TABLE_TABLE1 = "table1";
    private static final String TABLE_TABLE2 = "table2";
    private static final String TABLE_TABLE3 = "table3";
    private static final String TABLE_TABLE4 = "table4";

    private JQuickPhysicalPlanGenerator generator;

    private JQuickFragmenter fragmenter;

    private JQuickFragmenter verboseFragmenter;


    private JQuickNodeExecutor nodeExecutor;

    private JQuickWorker testWorker;

    private JQuickDataConverter dataConverter;

    private JQuickExpressionEvaluator expressionEvaluator;

    private JQuickPartitionManager partitionManager;

    @Before
    public void setUp() {
        generator = new JQuickPhysicalPlanGenerator();
        fragmenter = new JQuickFragmenter(4);
        verboseFragmenter = new JQuickFragmenter(8);
        generator = new JQuickPhysicalPlanGenerator();
        fragmenter = new JQuickFragmenter(4);
        testWorker = new JQuickWorker("test-worker", 0);
        JQuickMethodInvocationManager functionManager = JQuickMethodInvocationManager.getInstance();
        expressionEvaluator = new JQuickExpressionEvaluator(functionManager);
        partitionManager = new JQuickPartitionManager();
        dataConverter = new JQuickDataConverter();
        nodeExecutor = new JQuickNodeExecutor(testWorker, expressionEvaluator, partitionManager, dataConverter);

        // 注册测试数据
        registerTestData();
    }
    @After
    public void tearDown() {
        // 清理测试数据
        JQuickDataSourceManager.clearAll();
    }
    /**
     * 注册测试数据
     */
    private void registerTestData() {
        // 注册 users 表
        List<JQuickColumnMeta> userColumns = Arrays.asList(
                new JQuickColumnMeta("id", Integer.class, TABLE_USERS),
                new JQuickColumnMeta("name", String.class, TABLE_USERS),
                new JQuickColumnMeta("age", Integer.class, TABLE_USERS),
                new JQuickColumnMeta("status", String.class, TABLE_USERS)
        );

        List<JQuickRow> userRows = Arrays.asList(
                createRow("id", 1, "name", "Alice", "age", 25, "status", "active"),
                createRow("id", 2, "name", "Bob", "age", 30, "status", "active"),
                createRow("id", 3, "name", "Charlie", "age", 20, "status", "pending"),
                createRow("id", 4, "name", "David", "age", 35, "status", "inactive"),
                createRow("id", 5, "name", "Eve", "age", 28, "status", "active"),
                createRow("id", 6, "name", "Frank", "age", 22, "status", "pending"),
                createRow("id", 7, "name", "Grace", "age", 40, "status", "active"),
                createRow("id", 8, "name", "Henry", "age", 19, "status", "inactive")
        );

        JQuickDataSet usersData = new JQuickDataSet(userColumns, userRows);
        JQuickDataSourceManager.registerTable(TABLE_USERS, usersData);

        // 注册 orders_2023 表
        List<JQuickColumnMeta> orderColumns = Arrays.asList(
                new JQuickColumnMeta("product_id", Integer.class, TABLE_ORDERS_2023),
                new JQuickColumnMeta("amount", Double.class, TABLE_ORDERS_2023)
        );

        List<JQuickRow> order2023Rows = Arrays.asList(
                createRow("product_id", 101, "amount", 100.0),
                createRow("product_id", 102, "amount", 200.0),
                createRow("product_id", 103, "amount", 150.0),
                createRow("product_id", 104, "amount", 300.0),
                createRow("product_id", 105, "amount", 250.0)
        );

        JQuickDataSet orders2023Data = new JQuickDataSet(orderColumns, order2023Rows);
        JQuickDataSourceManager.registerTable(TABLE_ORDERS_2023, orders2023Data);

        // 注册 orders_2024 表
        List<JQuickRow> order2024Rows = Arrays.asList(
                createRow("product_id", 101, "amount", 120.0),
                createRow("product_id", 102, "amount", 180.0),
                createRow("product_id", 106, "amount", 400.0),
                createRow("product_id", 107, "amount", 350.0),
                createRow("product_id", 108, "amount", 280.0)
        );

        JQuickDataSet orders2024Data = new JQuickDataSet(orderColumns, order2024Rows);
        JQuickDataSourceManager.registerTable(TABLE_ORDERS_2024, orders2024Data);

        // 注册 table1, table2, table3, table4 用于嵌套集合操作测试
        List<JQuickColumnMeta> simpleColumns = Arrays.asList(new JQuickColumnMeta("id", Integer.class, "table"));
        JQuickDataSet table1Data = new JQuickDataSet(simpleColumns, Arrays.asList(createRow("id", 1), createRow("id", 2), createRow("id", 3)));
        JQuickDataSet table2Data = new JQuickDataSet(simpleColumns, Arrays.asList(createRow("id", 2), createRow("id", 3), createRow("id", 4)));
        JQuickDataSet table3Data = new JQuickDataSet(simpleColumns, Arrays.asList(createRow("id", 3), createRow("id", 4), createRow("id", 5)));
        JQuickDataSet table4Data = new JQuickDataSet(simpleColumns, Arrays.asList(createRow("id", 4), createRow("id", 5), createRow("id", 6)));
        JQuickDataSourceManager.registerTable(TABLE_TABLE1, table1Data);
        JQuickDataSourceManager.registerTable(TABLE_TABLE2, table2Data);
        JQuickDataSourceManager.registerTable(TABLE_TABLE3, table3Data);
        JQuickDataSourceManager.registerTable(TABLE_TABLE4, table4Data);
    }

    /**
     * 创建行的辅助方法
     */
    private JQuickRow createRow(Object... keyValues) {
        JQuickRow row = new JQuickRow();
        for (int i = 0; i < keyValues.length; i += 2) {
            row.put((String) keyValues[i], keyValues[i + 1]);
        }
        return row;
    }

    /**
     * 创建简单的表扫描节点
     */
    private JQuickTableScanNode createTableScan(String tableName) {
        return new JQuickTableScanNode(tableName);
    }

    /**
     * 创建带别名的表扫描节点
     */
    private JQuickTableScanNode createTableScan(String tableName, String alias) {
        return new JQuickTableScanNode(tableName, alias);
    }

    /**
     * 创建投影节点
     */
    private JQuickProjectNode createProject(JQuickLogicalPlanNode child, String... columns) {
        List<JQuickProjectNode.SelectItem> items = new ArrayList<>();
        for (String col : columns) {
            items.add(new JQuickProjectNode.SelectItem(new JQuickColumnRefExpression(col), col));
        }
        return new JQuickProjectNode(items, child);
    }

    /**
     * 创建过滤节点
     */
    private JQuickFilterNode createFilter(JQuickLogicalPlanNode child, String column, String operator, Object value) {
        JQuickBinaryOperator op;
        switch (operator) {
            case ">": op = JQuickBinaryOperator.GT; break;
            case "<": op = JQuickBinaryOperator.LT; break;
            case "=": op = JQuickBinaryOperator.EQ; break;
            default: op = JQuickBinaryOperator.EQ;
        }
        JQuickBinaryExpression predicate = new JQuickBinaryExpression(new JQuickColumnRefExpression(column), new JQuickLiteralExpression(value), op);
        return new JQuickFilterNode(predicate, child);
    }

    /**
     * 创建带过滤条件的投影
     */
    private JQuickProjectNode createProjectWithFilter(String tableName, String filterCol, Object filterValue, String... columns) {
        JQuickTableScanNode scan = createTableScan(tableName);
        JQuickFilterNode filter = createFilter(scan, filterCol, "=", filterValue);
        return createProject(filter, columns);
    }
    /**
     * 直接执行物理节点（用于测试）
     */
    private JQuickDataSet executeNode(JQuickPhysicalPlanNode node) {
        JQuickWorker.JQuickTaskContext context = testWorker.new JQuickTaskContext("test-task", JQuickExecuteTaskRequest.newBuilder().setTaskId("test").setQueryId("test-query").build());
        return nodeExecutor.executeNode(node, context);
    }
    /**
     * 测试1：UNION 操作
     *
     * SQL示例：
     * SELECT id, name FROM users WHERE status = 'active'
     * UNION
     * SELECT id, name FROM users WHERE status = 'pending'
     */
    @Test
    public void testUnionOperation() {
        JQuickProjectNode leftQuery = createProjectWithFilter("users", "status", "active", "id", "name");
        JQuickProjectNode rightQuery = createProjectWithFilter("users", "status", "pending", "id", "name");
        JQuickSetOperationNode unionNode = new JQuickSetOperationNode(JQuickSQLOperationType.UNION, leftQuery, rightQuery);
        JQuickPhysicalPlanNode physicalPlan = generator.generate(unionNode);
        JQuickDataSet result = executeNode(physicalPlan);
        result.printTable();
        System.out.println("=== UNION操作测试通过 ===");
        System.out.println("物理计划结构: SetOperation(UNION) -> Project, Project");
    }
    /**
     * 测试2：UNION ALL 操作
     *
     * SQL示例：
     * SELECT id, name FROM users WHERE age > 18
     * UNION ALL
     * SELECT id, name FROM users WHERE age <= 18
     */
    @Test
    public void testUnionAllOperation() {
        JQuickProjectNode leftQuery = createProjectWithFilter("users", "age", 18, "id", "name");
        JQuickFilterNode filter = (JQuickFilterNode) leftQuery.getChild();
        JQuickBinaryExpression newPredicate = new JQuickBinaryExpression(new JQuickColumnRefExpression("age"), new JQuickLiteralExpression(30), JQuickBinaryOperator.GT);
        JQuickFilterNode newFilter = new JQuickFilterNode(newPredicate, ((JQuickFilterNode) filter).getChild());
        JQuickProjectNode leftQueryModified = new JQuickProjectNode(leftQuery.getSelectItems(), newFilter, leftQuery.isDistinct());
        JQuickProjectNode rightQuery = createProjectWithFilter("users", "age", 18, "id", "name");
        JQuickFilterNode rightFilter = (JQuickFilterNode) rightQuery.getChild();
        JQuickBinaryExpression rightPredicate = new JQuickBinaryExpression(new JQuickColumnRefExpression("age"), new JQuickLiteralExpression(22), JQuickBinaryOperator.LE);
        JQuickFilterNode newRightFilter = new JQuickFilterNode(rightPredicate, rightFilter.getChild());
        JQuickProjectNode rightQueryModified = new JQuickProjectNode(rightQuery.getSelectItems(), newRightFilter, rightQuery.isDistinct());
        JQuickSetOperationNode unionAllNode = new JQuickSetOperationNode(JQuickSQLOperationType.UNION_ALL, leftQueryModified, rightQueryModified);
        JQuickPhysicalPlanNode physicalPlan = generator.generate(unionAllNode);
        JQuickDistributedPlan distributedPlan= fragmenter.fragment(physicalPlan);
        JQuickDataSet result = executeNode(physicalPlan);
        result.printTable();
        System.out.println(physicalPlan);
    }
    /**
     * 测试3：INTERSECT 操作
     *
     * SQL示例：
     * SELECT product_id FROM orders_2023
     * INTERSECT
     * SELECT product_id FROM orders_2024
     */
    @Test
    public void testIntersectOperation() {
        JQuickProjectNode leftQuery = createProject(createTableScan("orders_2023"), "product_id");
        JQuickProjectNode rightQuery = createProject(createTableScan("orders_2024"), "product_id");
        JQuickSetOperationNode intersectNode = new JQuickSetOperationNode(JQuickSQLOperationType.INTERSECT, leftQuery, rightQuery);
        JQuickPhysicalPlanNode physicalPlan = generator.generate(intersectNode);
        JQuickSetOperationPhysicalNode setOpNode = (JQuickSetOperationPhysicalNode) physicalPlan;
        JQuickDistributedPlan distributedPlan= fragmenter.fragment(setOpNode);
        fragmenter.printFragments(distributedPlan);
        JQuickDataSet result = executeNode(physicalPlan);
        result.printTable();
        System.out.println(physicalPlan);
    }
    /**
     * 测试5：多个集合操作嵌套
     *
     * SQL示例：
     * (SELECT id FROM table1 UNION SELECT id FROM table2)
     * INTERSECT
     * (SELECT id FROM table3 UNION SELECT id FROM table4)
     */
    @Test
    public void testNestedSetOperations() {
        JQuickProjectNode table1 = createProject(createTableScan("table1"), "id");
        JQuickProjectNode table2 = createProject(createTableScan("table2"), "id");
        JQuickSetOperationNode innerUnion = new JQuickSetOperationNode(JQuickSQLOperationType.UNION, table1, table2);
        JQuickProjectNode table3 = createProject(createTableScan("table3"), "id");
        JQuickProjectNode table4 = createProject(createTableScan("table4"), "id");
        JQuickSetOperationNode innerUnion2 = new JQuickSetOperationNode(JQuickSQLOperationType.UNION, table3, table4);
        JQuickSetOperationNode outerIntersect = new JQuickSetOperationNode(JQuickSQLOperationType.INTERSECT, innerUnion, innerUnion2);
        JQuickPhysicalPlanNode physicalPlan = generator.generate(outerIntersect);
        JQuickDistributedPlan distributedPlan= fragmenter.fragment(physicalPlan);
        fragmenter.printFragments(distributedPlan);
        JQuickDataSet result = executeNode(physicalPlan);
        result.printTable();
    }


}
