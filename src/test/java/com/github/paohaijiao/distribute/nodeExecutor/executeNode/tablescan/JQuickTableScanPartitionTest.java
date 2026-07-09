package com.github.paohaijiao.distribute.nodeExecutor.executeNode.tablescan;

import com.github.paohaijiao.datasource.JQuickDataSourceManager;
import com.github.paohaijiao.distributed.worker.*;
import com.github.paohaijiao.enums.JQuickBinaryOperator;
import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.expression.domain.JQuickBinaryExpression;
import com.github.paohaijiao.expression.domain.JQuickColumnRefExpression;
import com.github.paohaijiao.expression.domain.JQuickLiteralExpression;
import com.github.paohaijiao.function.manager.JQuickMethodInvocationManager;
import com.github.paohaijiao.physical.domain.JQuickTablePartitionInfo;
import com.github.paohaijiao.physical.node.JQuickTableScanPhysicalNode;
import com.github.paohaijiao.proto.JQuickExecuteTaskRequest;
import com.github.paohaijiao.statement.JQuickColumnMeta;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

/**
 * TableScan 内存分区测试
 * 
 * 测试范围：
 * 1. 从数据源读取数据
 * 2. 从内存分区读取数据
 * 3. 带过滤条件的 TableScan
 * 4. 带列选择的 TableScan
 * 5. 并行任务的数据分片
 * 6. 空表和空分区处理
 */
public class JQuickTableScanPartitionTest {

    private JQuickWorker worker;
    private JQuickNodeExecutor nodeExecutor;
    private JQuickExpressionEvaluator expressionEvaluator;
    private JQuickDataConverter dataConverter;
    private JQuickPartitionManager partitionManager;

    @Before
    public void setUp() {
        JQuickMethodInvocationManager functionManager = JQuickMethodInvocationManager.getInstance();
        expressionEvaluator = new JQuickExpressionEvaluator(functionManager);
        dataConverter = new JQuickDataConverter();
        partitionManager = new JQuickPartitionManager();
        worker = new JQuickWorker("test-worker", 0);
        nodeExecutor = new JQuickNodeExecutor(worker, expressionEvaluator, partitionManager, dataConverter);
        JQuickDataSourceManager.clearAll();
        registerTestTables();
    }

    @After
    public void tearDown() {
        JQuickDataSourceManager.clearAll();
        if (partitionManager != null) {
            partitionManager.shutdown();
        }
    }

    /**
     * 注册测试表数据
     */
    private void registerTestTables() {
        List<JQuickColumnMeta> employeeColumns = Arrays.asList(
                new JQuickColumnMeta("id", Long.class, "employee"),
                new JQuickColumnMeta("name", String.class, "employee"),
                new JQuickColumnMeta("age", Integer.class, "employee"),
                new JQuickColumnMeta("salary", Double.class, "employee"),
                new JQuickColumnMeta("department", String.class, "employee")
        );
        List<JQuickRow> employeeRows = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            JQuickRow row = new JQuickRow();
            row.put("id", (long) i);
            row.put("name", "员工" + i);
            row.put("age", 20 + i);
            row.put("salary", 5000.0 + i * 1000);
            row.put("department", i % 3 == 0 ? "市场部" : (i % 3 == 1 ? "技术部" : "销售部"));
            employeeRows.add(row);
        }
        JQuickDataSet employeeTable = new JQuickDataSet(employeeColumns, employeeRows);
        JQuickDataSourceManager.registerTable("employee", employeeTable);
        List<JQuickColumnMeta> productColumns = Arrays.asList(
                new JQuickColumnMeta("product_id", Long.class, "products"),
                new JQuickColumnMeta("product_name", String.class, "products"),
                new JQuickColumnMeta("price", Double.class, "products"),
                new JQuickColumnMeta("category", String.class, "products")
        );
        List<JQuickRow> productRows = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            JQuickRow row = new JQuickRow();
            row.put("product_id", (long) i);
            row.put("product_name", "产品" + i);
            row.put("price", 100.0 * i);
            row.put("category", i % 4 == 0 ? "电子" : (i % 4 == 1 ? "服装" : (i % 4 == 2 ? "食品" : "家居")));
            productRows.add(row);
        }
        JQuickDataSet productTable = new JQuickDataSet(productColumns, productRows);
        JQuickDataSourceManager.registerTable("products", productTable);
    }

    /**
     * 创建任务上下文
     */
    private JQuickWorker.JQuickTaskContext createTaskContext(int taskIndex, int totalTasks) {
        JQuickExecuteTaskRequest request = JQuickExecuteTaskRequest.newBuilder()
                .setTaskId("test-task-" + taskIndex)
                .setQueryId("test-query-1")
                .setTaskIndex(taskIndex)
                .setTotalTasks(totalTasks)
                .setMemoryLimitBytes(1024 * 1024)
                .build();
        return worker.new JQuickTaskContext("test-task-" + taskIndex, request);
    }

    /**
     * 测试从数据源读取 - 基本读取
     * 
     * 目的：验证能够从数据源正确读取全表数据
     * 预期：返回表中的所有数据
     */
    @Test
    public void testReadFromDataSource_Basic() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null);
        JQuickWorker.JQuickTaskContext context = createTaskContext(0, 1);
        JQuickDataSet result = nodeExecutor.executeNode(scanNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("employee 表应该有 10 条数据", 10, result.size());
        assertEquals("应该有 5 列", 5, result.getColumns().size());
    }

    /**
     * 测试从数据源读取 - 带列选择
     * 
     * 目的：验证能够只读取指定的列
     * 预期：只返回指定的列
     */
    @Test
    public void testReadFromDataSource_WithRequiredColumns() {
        Set<String> requiredColumns = new HashSet<>(Arrays.asList("id", "name", "salary"));
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", "e",requiredColumns, null, null);
        JQuickWorker.JQuickTaskContext context = createTaskContext(0, 1);
        JQuickDataSet result = nodeExecutor.executeNode(scanNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("employee 表应该有 10 条数据", 10, result.size());
        assertTrue("应该包含 id 列", result.getRows().get(0).containsKey("id"));
        assertTrue("应该包含 name 列", result.getRows().get(0).containsKey("name"));
        assertTrue("应该包含 salary 列", result.getRows().get(0).containsKey("salary"));
    }

    /**
     * 测试从数据源读取 - 带过滤条件
     * 
     * 目的：验证能够在扫描时应用过滤条件
     * 预期：只返回满足条件的数据
     */
    @Test
    public void testReadFromDataSource_WithFilter() {
        JQuickExpression filterPredicate = new JQuickBinaryExpression(new JQuickColumnRefExpression("department"), new JQuickLiteralExpression("技术部"), JQuickBinaryOperator.EQ);
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null,null, filterPredicate);
        JQuickWorker.JQuickTaskContext context = createTaskContext(0, 1);
        JQuickDataSet result = nodeExecutor.executeNode(scanNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertTrue("应该返回技术部的数据", result.size() > 0);
        for (JQuickRow row : result.getRows()) {
            assertEquals("所有结果应该是技术部", "技术部", row.get("department"));
        }
    }

    /**
     * 测试从数据源读取 - 带过滤和列选择
     * 
     * 目的：验证能够同时应用过滤条件和列选择
     * 预期：返回满足条件的指定列数据
     */
    @Test
    public void testReadFromDataSource_WithFilterAndColumns() {
        Set<String> requiredColumns = new HashSet<>(Arrays.asList("id", "age","name"));
        JQuickExpression filterPredicate = new JQuickBinaryExpression(new JQuickColumnRefExpression("age"), new JQuickLiteralExpression(25), JQuickBinaryOperator.GT);
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null,requiredColumns, filterPredicate );
        JQuickWorker.JQuickTaskContext context = createTaskContext(0, 1);
        JQuickDataSet result = nodeExecutor.executeNode(scanNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertTrue("应该返回年龄大于 25 的数据", result.size() > 0);

    }
    /**
     * 测试从内存分区读取 - 基本读取
     * 
     * 目的：验证能够从内存分区正确读取数据
     * 预期：返回分区中的数据
     */
    @Test
    public void testReadFromMemoryPartition_Basic() {
        JQuickDataSet.Builder builder = JQuickDataSet.builder();
        builder.addColumn("id", Long.class, "test_partition");
        builder.addColumn("name", String.class, "test_partition");
        for (int i = 1; i <= 5; i++) {
            JQuickRow row = new JQuickRow();
            row.put("id", (long) i);
            row.put("name", "partition_data_" + i);
            builder.addRow(row);
        }
        JQuickDataSet partitionData = builder.build();
        JQuickWorker.JQuickMemoryPartition partition = new JQuickWorker.JQuickMemoryPartition(0, 1);
        partition.setPartitionId("test_partition");
        partition.setData(partitionData);
        worker.getMemoryPartitions().put("test_partition", partition);
        // 创建带分区信息的 TableScan（使用 JQuickTablePartitionInfo）
        JQuickTablePartitionInfo partitionInfo = new JQuickTablePartitionInfo("test_partition", Collections.emptyList(), null);
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("test_partition", null, null, null, partitionInfo);
        JQuickWorker.JQuickTaskContext context = createTaskContext(1, 1);
        JQuickDataSet result = nodeExecutor.executeNode(scanNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 5 条数据", 5, result.size());
    }

    /**
     * 测试从内存分区读取 - 多分区
     * 
     * 目的：验证能够从多个内存分区读取数据
     * 预期：每个分区返回正确的数据
     */
    @Test
    public void testReadFromMemoryPartition_MultiplePartitions() {
        for (int p = 0; p < 3; p++) { // 创建 3 个分区
            JQuickDataSet.Builder builder = JQuickDataSet.builder();
            builder.addColumn("id", Long.class, "multi_partition");
            builder.addColumn("value", Integer.class, "multi_partition");
            for (int i = 0; i < 10; i++) {
                JQuickRow row = new JQuickRow();
                row.put("id", (long) (p * 10 + i));
                row.put("value", p * 10 + i);
                builder.addRow(row);
            }
            JQuickDataSet partitionData = builder.build();
            JQuickWorker.JQuickMemoryPartition partition = new JQuickWorker.JQuickMemoryPartition(p, 3);
            partition.setPartitionId("multi_partition_" + p);
            partition.setData(partitionData);
            worker.getMemoryPartitions().put("multi_partition_" + p, partition);
        }
        
        // 分别读取每个分区
        for (int p = 0; p < 3; p++) {
            JQuickTablePartitionInfo partitionInfo = new JQuickTablePartitionInfo("multi_partition_" + p, Collections.emptyList(), null);
            JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("multi_partition_" + p, null, null, null, partitionInfo);
            JQuickWorker.JQuickTaskContext context = createTaskContext(0, 1);
            JQuickDataSet result = nodeExecutor.executeNode(scanNode, context);
            result.printTable();
            assertNotNull("结果不应为 null", result);
            assertEquals("分区 " + p + " 应该有 10 条数据", 10, result.size());
        }
    }

    /**
     * 测试从内存分区读取 - 空分区
     * 
     * 目的：验证读取空分区时返回空结果
     * 预期：返回空数据集
     */
    @Test
    public void testReadFromMemoryPartition_EmptyPartition() {
        // 创建空分区
        JQuickWorker.JQuickMemoryPartition partition = new JQuickWorker.JQuickMemoryPartition(0, 1);
        partition.setPartitionId("empty_partition");
        partition.setData(JQuickDataSet.builder().build());
        worker.getMemoryPartitions().put("empty_partition", partition);
        JQuickTablePartitionInfo partitionInfo = new JQuickTablePartitionInfo("empty_partition", Collections.emptyList(), null);
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("empty_partition", null, null, null, partitionInfo);
        JQuickWorker.JQuickTaskContext context = createTaskContext(0, 1);
        JQuickDataSet result = nodeExecutor.executeNode(scanNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertTrue("空分区应该返回空结果", result.isEmpty());
    }

    /**
     * 测试从内存分区读取 - 不存在的分区
     * 
     * 目的：验证读取不存在的分区时返回空结果
     * 预期：返回空数据集
     */
    @Test
    public void testReadFromMemoryPartition_NonExistent() {
        JQuickTablePartitionInfo partitionInfo = new JQuickTablePartitionInfo("non_existent_partition", Collections.emptyList(), null);
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("non_existent_partition", null, null, null, partitionInfo);
        JQuickWorker.JQuickTaskContext context = createTaskContext(0, 1);
        JQuickDataSet result = nodeExecutor.executeNode(scanNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertTrue("不存在的分区应该返回空结果", result.isEmpty());
    }
    /**
     * 测试并行任务 - 2 个任务分片
     * 
     * 目的：验证数据能够正确分片到 2 个并行任务
     * 预期：每个任务获取一半数据
     */
    @Test
    public void testParallelSharding_TwoTasks() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null);
        // 任务 0
        JQuickWorker.JQuickTaskContext context0 = createTaskContext(0, 2);
        JQuickDataSet result0 = nodeExecutor.executeNode(scanNode, context0);
        result0.printTable();
        // 任务 1
        JQuickWorker.JQuickTaskContext context1 = createTaskContext(1, 2);
        JQuickDataSet result1 = nodeExecutor.executeNode(scanNode, context1);
        result1.printTable();
        assertEquals("任务 0 应该有 5 条数据", 5, result0.size());
        assertEquals("任务 1 应该有 5 条数据", 5, result1.size());
        assertEquals("两个任务的数据总数应该是 10", 10, result0.size() + result1.size());
    }

    /**
     * 测试并行任务 - 4 个任务分片
     * 
     * 目的：验证数据能够正确分片到 4 个并行任务
     * 预期：每个任务获取相应比例的数据
     */
    @Test
    public void testParallelSharding_FourTasks() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("products", null, null, null);
        int totalRows = 0;
        for (int i = 0; i < 4; i++) {
            JQuickWorker.JQuickTaskContext context = createTaskContext(i, 4);
            JQuickDataSet result = nodeExecutor.executeNode(scanNode, context);
            result.printTable();
            totalRows += result.size();
            assertTrue("每个任务应该有 5 条数据", result.size() == 5);
        }
        assertEquals("所有任务的数据总数应该是 20", 20, totalRows);
    }

    /**
     * 测试并行任务 - 不均匀分片
     * 
     * 目的：验证数据不能被整除时的分片行为
     * 预期：数据正确分配，总数正确
     */
    @Test
    public void testParallelSharding_UnevenDistribution() {
        // employee 表有 10 条数据，用 3 个任务分片
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null);
        int totalRows = 0;
        int[] taskSizes = new int[3];
        for (int i = 0; i < 3; i++) {
            JQuickWorker.JQuickTaskContext context = createTaskContext(i, 3);
            JQuickDataSet result = nodeExecutor.executeNode(scanNode, context);
            result.printTable();
            taskSizes[i] = result.size();
            totalRows += result.size();
        }
        assertEquals("所有任务的数据总数应该是 10", 10, totalRows);
        // 验证分片：10 条数据分 3 个任务，应该是 4, 3, 3 或 4, 4, 2 等
        assertTrue("任务 0 应该有 3-4 条数据", taskSizes[0] >= 3 && taskSizes[0] <= 4);
        assertTrue("任务 1 应该有 3-4 条数据", taskSizes[1] >= 3 && taskSizes[1] <= 4);
        assertTrue("任务 2 应该有 2-4 条数据", taskSizes[2] >= 2 && taskSizes[2] <= 4);
    }

    /**
     * 测试并行任务 - 单任务（无分片）
     * 
     * 目的：验证单任务时返回全部数据
     * 预期：返回所有数据
     */
    @Test
    public void testParallelSharding_SingleTask() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null);
        JQuickWorker.JQuickTaskContext context = createTaskContext(0, 1);
        JQuickDataSet result = nodeExecutor.executeNode(scanNode, context);
        result.printTable();
        assertEquals("单任务应该返回全部 10 条数据", 10, result.size());
    }

    /**
     * 测试空表扫描
     * 
     * 目的：验证扫描空表时返回空结果
     * 预期：返回空数据集
     */
    @Test
    public void testScanEmptyTable() {
        JQuickDataSourceManager.registerTable("empty_table", JQuickDataSet.builder().build());
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("empty_table", null, null, null);
        JQuickWorker.JQuickTaskContext context = createTaskContext(0, 1);
        JQuickDataSet result = nodeExecutor.executeNode(scanNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertTrue("空表应该返回空结果", result.isEmpty());
    }

    /**
     * 测试不存在的表
     * 
     * 目的：验证扫描不存在的表时的行为
     * 预期：返回空数据集或抛出异常
     */
    @Test
    public void testScanNonExistentTable() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("non_existent_table", null, null, null);
        JQuickWorker.JQuickTaskContext context = createTaskContext(0, 1);
        try {
            JQuickDataSet result = nodeExecutor.executeNode(scanNode, context);
            result.printTable();
            assertNotNull("结果不应为 null", result);
        } catch (Exception e) {
            // 如果抛出异常，也是合理的行为
            assertTrue("异常消息应该包含表名", e.getMessage().contains("non_existent_table") || e.getMessage().contains("not found") || e.getMessage().contains("不存在"));
        }
    }

    /**
     * 测试带 null 值的数据
     * 
     * 目的：验证能够正确处理包含 null 值的数据
     * 预期：null 值被正确处理
     */
    @Test
    public void testScanWithNullValues() {
        // 创建包含 null 值的表
        JQuickDataSet.Builder builder = JQuickDataSet.builder();
        builder.addColumn("id", Long.class, "null_test");
        builder.addColumn("name", String.class, "null_test");
        builder.addColumn("value", Double.class, "null_test");
        for (int i = 0; i < 5; i++) {
            JQuickRow row = new JQuickRow();
            row.put("id", (long) i);
            row.put("name", i % 2 == 0 ? "name_" + i : null);
            row.put("value", i % 2 == 0 ? i * 1.5 : null);
            builder.addRow(row);
        }
        JQuickDataSet nullTable = builder.build();
        JQuickDataSourceManager.registerTable("null_test", nullTable);
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("null_test", null, null, null);
        JQuickWorker.JQuickTaskContext context = createTaskContext(0, 1);
        JQuickDataSet result = nodeExecutor.executeNode(scanNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该有 5 条数据", 5, result.size());
        // 验证 null 值存在
        int nullCount = 0;
        for (JQuickRow row : result.getRows()) {
            if (row.get("name") == null) nullCount++;
        }
        assertEquals("应该有 2 个 null 值", 2, nullCount);
    }

    /**
     * 测试大数据量扫描
     * 
     * 目的：验证大数据量扫描的性能
     * 预期：能够正确处理大数据量
     */
    @Test
    public void testScanLargeData() {
        JQuickDataSet.Builder builder = JQuickDataSet.builder();
        builder.addColumn("id", Long.class, "large_table");
        builder.addColumn("data", String.class, "large_table");
        int rowCount = 10000;
        for (int i = 0; i < rowCount; i++) {
            JQuickRow row = new JQuickRow();
            row.put("id", (long) i);
            row.put("data", "data_" + i);
            builder.addRow(row);
        }
        JQuickDataSet largeTable = builder.build();
        JQuickDataSourceManager.registerTable("large_table", largeTable);
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("large_table", null, null, null);
        JQuickWorker.JQuickTaskContext context = createTaskContext(0, 1);
        long startTime = System.currentTimeMillis();
        JQuickDataSet result = nodeExecutor.executeNode(scanNode, context);
        result.printTable();
        long endTime = System.currentTimeMillis();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该有 10000 条数据", rowCount, result.size());
        System.out.println("扫描 " + rowCount + " 条数据耗时: " + (endTime - startTime) + "ms");
    }

//    /**
//     * 测试并行扫描大数据量
//     *
//     * 目的：验证并行扫描大数据量的正确性
//     * 预期：所有并行任务的数据总数正确
//     */
//    @Test
//    public void testParallelScanLargeData() {
//        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("large_table", null, null, null);
//        int totalRows = 0;
//        int numTasks = 4;
//        for (int i = 0; i < numTasks; i++) {
//            JQuickWorker.JQuickTaskContext context = createTaskContext(i, numTasks);
//            JQuickDataSet result = nodeExecutor.executeNode(scanNode, context);
//            result.printTable();
//            totalRows += result.size();
//        }
//        assertEquals("所有任务的数据总数应该是 10000", 10000, totalRows);
//    }
    /**
     * 测试组合 - 内存分区 + 过滤
     * 
     * 目的：验证从内存分区读取并应用过滤条件
     * 预期：返回过滤后的数据
     */
    @Test
    public void testMemoryPartitionWithFilter() {
        JQuickDataSet.Builder builder = JQuickDataSet.builder();
        builder.addColumn("id", Long.class, "filter_partition");
        builder.addColumn("value", Integer.class, "filter_partition");
        builder.addColumn("category", String.class, "filter_partition");
        for (int i = 0; i < 20; i++) {
            JQuickRow row = new JQuickRow();
            row.put("id", (long) i);
            row.put("value", i);
            row.put("category", i % 2 == 0 ? "A" : "B");
            builder.addRow(row);
        }
        JQuickDataSet partitionData = builder.build();
        JQuickWorker.JQuickMemoryPartition partition = new JQuickWorker.JQuickMemoryPartition(0, 1);
        partition.setPartitionId("filter_partition");
        partition.setData(partitionData);
        worker.getMemoryPartitions().put("filter_partition", partition);
        // 创建带过滤条件的 TableScan
        JQuickExpression filterPredicate = new JQuickBinaryExpression(new JQuickColumnRefExpression("category"), new JQuickLiteralExpression("A"), JQuickBinaryOperator.EQ);
        JQuickTablePartitionInfo partitionInfo = new JQuickTablePartitionInfo("filter_partition", Collections.emptyList(), null);
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("filter_partition", null, null, filterPredicate, partitionInfo);
        JQuickWorker.JQuickTaskContext context = createTaskContext(0, 1);
        JQuickDataSet result = nodeExecutor.executeNode(scanNode, context);
        result.printTable();
        assertNotNull("结果不应为 null", result);
        assertEquals("应该返回 10 条 category='A' 的数据", 10, result.size());
        for (JQuickRow row : result.getRows()) {
            assertEquals("所有结果的 category 应该是 A", "A", row.get("category"));
        }
    }

    /**
     * 测试组合 - 内存分区 + 并行分片
     * 
     * 目的：验证从内存分区读取并进行并行分片
     * 预期：数据正确分片
     */
    @Test
    public void testMemoryPartitionWithParallelSharding() {
        // 创建测试数据并放入内存分区
        JQuickDataSet.Builder builder = JQuickDataSet.builder();
        builder.addColumn("id", Long.class, "parallel_partition");
        builder.addColumn("data", String.class, "parallel_partition");
        for (int i = 0; i < 100; i++) {
            JQuickRow row = new JQuickRow();
            row.put("id", (long) i);
            row.put("data", "data_" + i);
            builder.addRow(row);
        }
        JQuickDataSet partitionData = builder.build();
        JQuickWorker.JQuickMemoryPartition partition = new JQuickWorker.JQuickMemoryPartition(0, 1);
        partition.setPartitionId("parallel_partition");
        partition.setData(partitionData);
        worker.getMemoryPartitions().put("parallel_partition", partition);
        JQuickTablePartitionInfo partitionInfo = new JQuickTablePartitionInfo("parallel_partition", Collections.emptyList(), null);
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("parallel_partition", null, null, null, partitionInfo);
        int totalRows = 0;
        int numTasks = 5;
        for (int i = 0; i < numTasks; i++) {
            JQuickWorker.JQuickTaskContext context = createTaskContext(i, numTasks);
            JQuickDataSet result = nodeExecutor.executeNode(scanNode, context);
            result.printTable();
            totalRows += result.size();
        }
        assertEquals("所有任务的数据总数应该是 100", 100, totalRows);
    }
}
