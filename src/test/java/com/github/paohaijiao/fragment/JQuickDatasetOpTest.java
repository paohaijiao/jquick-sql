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
package com.github.paohaijiao.fragment;

import com.github.paohaijiao.distributed.JQuickDistributedPlan;
import com.github.paohaijiao.enums.JQuickBinaryOperator;
import com.github.paohaijiao.enums.JQuickSQLOperationType;
import com.github.paohaijiao.expression.domain.JQuickBinaryExpression;
import com.github.paohaijiao.expression.domain.JQuickColumnRefExpression;
import com.github.paohaijiao.expression.domain.JQuickLiteralExpression;
import com.github.paohaijiao.logic.JQuickLogicalPlanNode;
import com.github.paohaijiao.logic.domain.JQuickFilterNode;
import com.github.paohaijiao.logic.domain.JQuickProjectNode;
import com.github.paohaijiao.logic.domain.JQuickSetOperationNode;
import com.github.paohaijiao.logic.domain.JQuickTableScanNode;
import com.github.paohaijiao.logic2physical.JQuickPhysicalPlanGenerator;
import com.github.paohaijiao.physical.JQuickPhysicalPlanNode;
import com.github.paohaijiao.physical.node.JQuickSetOperationPhysicalNode;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * packageName com.github.paohaijiao.physical
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/5/24
 */
public class JQuickDatasetOpTest {

    private JQuickPhysicalPlanGenerator generator;
    private JQuickFragmenter fragmenter;
    private JQuickFragmenter verboseFragmenter;
    @Before
    public void setUp() {
        generator = new JQuickPhysicalPlanGenerator();
        fragmenter = new JQuickFragmenter(4);
        verboseFragmenter = new JQuickFragmenter(8);
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
        JQuickDistributedPlan distributedPlan= fragmenter.fragment(physicalPlan);
//        distributedPlan.printPlan();
        fragmenter.printFragments(distributedPlan);
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
        JQuickBinaryExpression newPredicate = new JQuickBinaryExpression(new JQuickColumnRefExpression("age"), new JQuickLiteralExpression(18), JQuickBinaryOperator.GT);
        JQuickFilterNode newFilter = new JQuickFilterNode(newPredicate, ((JQuickFilterNode) filter).getChild());
        JQuickProjectNode leftQueryModified = new JQuickProjectNode(leftQuery.getSelectItems(), newFilter, leftQuery.isDistinct());
        JQuickProjectNode rightQuery = createProjectWithFilter("users", "age", 18, "id", "name");
        JQuickFilterNode rightFilter = (JQuickFilterNode) rightQuery.getChild();
        JQuickBinaryExpression rightPredicate = new JQuickBinaryExpression(new JQuickColumnRefExpression("age"), new JQuickLiteralExpression(18), JQuickBinaryOperator.LE);
        JQuickFilterNode newRightFilter = new JQuickFilterNode(rightPredicate, rightFilter.getChild());
        JQuickProjectNode rightQueryModified = new JQuickProjectNode(rightQuery.getSelectItems(), newRightFilter, rightQuery.isDistinct());
        JQuickSetOperationNode unionAllNode = new JQuickSetOperationNode(JQuickSQLOperationType.UNION_ALL, leftQueryModified, rightQueryModified);
        JQuickPhysicalPlanNode physicalPlan = generator.generate(unionAllNode);
        JQuickDistributedPlan distributedPlan= fragmenter.fragment(physicalPlan);
        fragmenter.printFragments(distributedPlan);
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
        System.out.println(setOpNode);
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
        System.out.println(physicalPlan);
    }


}
