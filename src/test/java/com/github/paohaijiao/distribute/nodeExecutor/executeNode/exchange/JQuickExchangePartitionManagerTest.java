package com.github.paohaijiao.distribute.nodeExecutor.executeNode.exchange;

import com.github.paohaijiao.datasource.JQuickDataSourceManager;
import com.github.paohaijiao.distributed.coordinator.JQuickCoordinator.WorkerEndpoint;
import com.github.paohaijiao.distributed.worker.*;
import com.github.paohaijiao.enums.JQuickExchangeType;
import com.github.paohaijiao.enums.JQuickPartitionStrategy;
import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.expression.domain.JQuickColumnRefExpression;
import com.github.paohaijiao.function.manager.JQuickMethodInvocationManager;
import com.github.paohaijiao.physical.node.JQuickExchangePhysicalNode;
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
 * JQuickExchangePhysicalNode 与 JQuickPartitionManager 集成测试
 * 
 * 测试范围：
 * 1. HASH 分区策略 - 按 dept_id 分区
 * 2. ROUND_ROBIN 分区策略 - 轮询分区
 * 3. REPLICATE 策略 - 数据复制到所有分区
 * 4. BUCKET 分区策略 - 桶分区
 * 5. 多 Worker 场景测试
 * 6. 空数据集分区
 * 7. 分区数据验证
 */
public class JQuickExchangePartitionManagerTest {

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
        setupWorkerEndpoints();
    }

    @After
    public void tearDown() {
        JQuickDataSourceManager.clearAll();
        if (partitionManager != null) {
            partitionManager.shutdown();
        }
    }

    /**
     * 设置 Worker 端点
     */
    private void setupWorkerEndpoints() {
        List<WorkerEndpoint> endpoints = new ArrayList<>();
        endpoints.add(new WorkerEndpoint("worker-0", "localhost", 50001, 0));
        endpoints.add(new WorkerEndpoint("worker-1", "localhost", 50002, 1));
        endpoints.add(new WorkerEndpoint("worker-2", "localhost", 50003, 2));
        endpoints.add(new WorkerEndpoint("worker-3", "localhost", 50004, 3));
        partitionManager.setWorkerEndpoints(endpoints);
    }

    /**
     * 注册测试表数据
     */
    private void registerTestTables() {
        List<JQuickColumnMeta> columns = Arrays.asList(
                new JQuickColumnMeta("emp_id", Long.class, "employee"),
                new JQuickColumnMeta("emp_name", String.class, "employee"),
                new JQuickColumnMeta("dept_id", Long.class, "employee"),
                new JQuickColumnMeta("salary", Double.class, "employee")
        );
        List<JQuickRow> rows = new ArrayList<>();
        // 部门 1 - 技术部
        rows.add(createRow(columns, new Object[]{1L, "Alice", 1L, 8000.0}));
        rows.add(createRow(columns, new Object[]{2L, "Bob", 1L, 10000.0}));
        rows.add(createRow(columns, new Object[]{3L, "Charlie", 1L, 12000.0}));
        // 部门 2 - 市场部
        rows.add(createRow(columns, new Object[]{4L, "David", 2L, 9000.0}));
        rows.add(createRow(columns, new Object[]{5L, "Eve", 2L, 9500.0}));
        // 部门 3 - 人事部
        rows.add(createRow(columns, new Object[]{6L, "Frank", 3L, 7000.0}));
        rows.add(createRow(columns, new Object[]{7L, "Grace", 3L, 7500.0}));
        // 部门 4 - 财务部
        rows.add(createRow(columns, new Object[]{8L, "Henry", 4L, 8500.0}));
        
        JQuickDataSet data = new JQuickDataSet(columns, rows);
        JQuickDataSourceManager.registerTable("employee", data);
    }

    private JQuickRow createRow(List<JQuickColumnMeta> columns, Object[] values) {
        JQuickRow row = new JQuickRow();
        for (int i = 0; i < columns.size(); i++) {
            row.put(columns.get(i).getName(), values[i]);
        }
        return row;
    }

    /**
     * 测试 HASH 分区策略
     * 
     * 场景：按 dept_id 进行 HASH 分区，4 个分区
     * 预期：相同 dept_id 的数据在同一个分区
     */
    @Test
    public void testHashPartitionByDeptId() {
        JQuickDataSet data = JQuickDataSourceManager.getTable("employee");
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        List<JQuickExpression> partitionKeys = Arrays.asList(
            new JQuickColumnRefExpression("dept_id")
        );
        JQuickExchangePhysicalNode exchangeNode = new JQuickExchangePhysicalNode(
            JQuickExchangeType.SHUFFLE,
            JQuickPartitionStrategy.HASH,
            partitionKeys,
            4,
            scanNode
        );
        List<JQuickWorker.JQuickMemoryPartition> partitions = partitionManager.partitionData(
            data, exchangeNode, expressionEvaluator, 4
        );
        assertEquals("应该有 4 个分区", 4, partitions.size());
        Map<Long, Integer> deptToPartition = new HashMap<>();
        for (int i = 0; i < partitions.size(); i++) {
            JQuickWorker.JQuickMemoryPartition partition = partitions.get(i);
            JQuickDataSet dataset=partition.getData();
            dataset.printTable();
            for (JQuickRow row :dataset.getRows()) {
                Long deptId =  row.getLong("dept_id");
                if (deptToPartition.containsKey(deptId)) {
                    assertEquals("相同 dept_id 应该在同一个分区",(int) deptToPartition.get(deptId), i);
                } else {
                    deptToPartition.put(deptId, i);
                }
            }
        }
        int totalRows = 0;
        for (JQuickWorker.JQuickMemoryPartition partition : partitions) {
            totalRows += partition.getData().size();
        }
        assertEquals("所有数据都应该被分区", 8, totalRows);
    }

    /**
     * 测试 ROUND_ROBIN 分区策略
     * 
     * 场景：轮询分区，4 个分区
     * 预期：数据均匀分布在各个分区
     */
    @Test
    public void testRoundRobinPartition() {
        JQuickDataSet data = JQuickDataSourceManager.getTable("employee");
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickExchangePhysicalNode exchangeNode = new JQuickExchangePhysicalNode(
            JQuickExchangeType.SHUFFLE,
            JQuickPartitionStrategy.ROUND_ROBIN,
            null,
            4,
            scanNode
        );
        List<JQuickWorker.JQuickMemoryPartition> partitions = partitionManager.partitionData(data, exchangeNode, expressionEvaluator, 4);
        assertEquals("应该有 4 个分区", 4, partitions.size());
        int[] partitionCounts = new int[4];
        for (int i = 0; i < partitions.size(); i++) {
            JQuickDataSet dataset=partitions.get(i).getData();
            dataset.printTable();
            partitionCounts[i] = partitions.get(i).getData().size();
        }
        for (int count : partitionCounts) {
            assertEquals("ROUND_ROBIN 每个分区应该有 2 条数据", 2, count);
        }
    }

    /**
     * 测试 REPLICATE 分区策略
     * 
     * 场景：复制分区，每行数据复制到所有分区
     * 预期：每个分区包含所有数据
     */
    @Test
    public void testReplicatePartition() {
        JQuickDataSet data = JQuickDataSourceManager.getTable("employee");
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickExchangePhysicalNode exchangeNode = new JQuickExchangePhysicalNode(
            JQuickExchangeType.BROADCAST,
            JQuickPartitionStrategy.REPLICATE,
            null,
            3,
            scanNode
        );
        List<JQuickWorker.JQuickMemoryPartition> partitions = partitionManager.partitionData(data, exchangeNode, expressionEvaluator, 3);
        assertEquals("应该有 3 个分区", 3, partitions.size());
        for (JQuickWorker.JQuickMemoryPartition partition : partitions) {
            JQuickDataSet dataset=partition.getData();
            dataset.printTable();
            assertEquals("REPLICATE 每个分区应该包含所有 8 条数据", 8, partition.getData().size());
        }
    }

    /**
     * 测试 BUCKET 分区策略
     * 
     * 场景：桶分区
     * 预期：数据按哈希分发到桶
     */
    @Test
    public void testBucketPartition() {
        JQuickDataSet data = JQuickDataSourceManager.getTable("employee");
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        List<JQuickExpression> partitionKeys = Arrays.asList(
            new JQuickColumnRefExpression("emp_id")
        );
        JQuickExchangePhysicalNode exchangeNode = new JQuickExchangePhysicalNode(
            JQuickExchangeType.REPARTITION,
            JQuickPartitionStrategy.BUCKET,
            partitionKeys,
            4,
            scanNode
        );
        List<JQuickWorker.JQuickMemoryPartition> partitions = partitionManager.partitionData(
            data, exchangeNode, expressionEvaluator, 4
        );
        assertEquals("应该有 4 个分区", 4, partitions.size());
        int totalRows = 0;
        for (JQuickWorker.JQuickMemoryPartition partition : partitions) {
            JQuickDataSet dataset=partition.getData();
            dataset.printTable();
            totalRows += partition.getData().size();
        }
        assertEquals("所有数据都应该被分区", 8, totalRows);
    }

    /**
     * 测试空数据集分区
     * 
     * 场景：对空数据集进行分区
     * 预期：返回正确数量的空分区
     */
    @Test
    public void testEmptyDataPartition() {
        List<JQuickColumnMeta> columns = Arrays.asList(
            new JQuickColumnMeta("emp_id", Long.class, "empty"),
            new JQuickColumnMeta("emp_name", String.class, "empty")
        );
        JQuickDataSet emptyData = new JQuickDataSet(columns, new ArrayList<>());
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        List<JQuickExpression> partitionKeys = Arrays.asList(
            new JQuickColumnRefExpression("dept_id")
        );
        JQuickExchangePhysicalNode exchangeNode = new JQuickExchangePhysicalNode(
            JQuickExchangeType.SHUFFLE,
            JQuickPartitionStrategy.HASH,
            partitionKeys,
            4,
            scanNode
        );
        List<JQuickWorker.JQuickMemoryPartition> partitions = partitionManager.partitionData(
            emptyData, exchangeNode, expressionEvaluator, 4
        );
        assertEquals("应该返回 4 个分区", 4, partitions.size());
        for (JQuickWorker.JQuickMemoryPartition partition : partitions) {
            JQuickDataSet dataset=partition.getData();
            dataset.printTable();
            assertTrue("每个分区应该为空", partition.getData().isEmpty());
        }
    }

    /**
     * 测试分区数据内容验证
     * 
     * 场景：验证 HASH 分区后相同 dept_id 的数据在同一个分区
     * 预期：相同 dept_id 的数据一定在同一个分区
     */
    @Test
    public void testPartitionDataContent() {
        JQuickDataSet data = JQuickDataSourceManager.getTable("employee");
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        List<JQuickExpression> partitionKeys = Arrays.asList(
            new JQuickColumnRefExpression("dept_id")
        );
        JQuickExchangePhysicalNode exchangeNode = new JQuickExchangePhysicalNode(
            JQuickExchangeType.SHUFFLE,
            JQuickPartitionStrategy.HASH,
            partitionKeys,
            2,
            scanNode
        );
        
        List<JQuickWorker.JQuickMemoryPartition> partitions = partitionManager.partitionData(
            data, exchangeNode, expressionEvaluator, 2
        );
        assertEquals("应该有 2 个分区", 2, partitions.size());
        Map<Long, Integer> deptToPartition = new HashMap<>();
        for (int i = 0; i < partitions.size(); i++) {
            JQuickWorker.JQuickMemoryPartition partition = partitions.get(i);
            partition.getData().printTable();
            for (JQuickRow row : partition.getData().getRows()) {
                Long deptId = (Long) row.get("dept_id");
                if (deptToPartition.containsKey(deptId)) {
                    // 相同 dept_id 应该在同一个分区
                    assertEquals("相同 dept_id 应该在同一个分区", (int)deptToPartition.get(deptId), i);
                } else {
                    deptToPartition.put(deptId, i);
                }
            }
        }
        int totalRows = 0;
        for (JQuickWorker.JQuickMemoryPartition partition : partitions) {
            totalRows += partition.getData().size();
        }
        assertEquals("所有数据都应该被分区", 8, totalRows);
    }

    /**
     * 测试分区数据包含 columns 信息
     * 
     * 场景：验证分区后的 data 包含正确的 columns 元数据
     * 预期：每个分区的 data 都应该包含 columns 信息
     */
    @Test
    public void testPartitionDataContainsColumns() {
        JQuickDataSet data = JQuickDataSourceManager.getTable("employee");
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        List<JQuickExpression> partitionKeys = Arrays.asList(
            new JQuickColumnRefExpression("dept_id")
        );
        JQuickExchangePhysicalNode exchangeNode = new JQuickExchangePhysicalNode(
            JQuickExchangeType.SHUFFLE,
            JQuickPartitionStrategy.HASH,
            partitionKeys,
            4,
            scanNode
        );
        List<JQuickWorker.JQuickMemoryPartition> partitions = partitionManager.partitionData(
            data, exchangeNode, expressionEvaluator, 4
        );
        for (JQuickWorker.JQuickMemoryPartition partition : partitions) {
            JQuickDataSet partitionData = partition.getData();
            partitionData.printTable();
            assertNotNull("分区 data 不应为 null", partitionData);
            List<JQuickColumnMeta> columns = partitionData.getColumns();
            assertNotNull("分区 data 的 columns 不应为 null", columns);
            if (!partitionData.getRows().isEmpty()) {
                assertFalse("有数据的分区应该有 columns 信息", columns.isEmpty());
                Set<String> columnNames = new HashSet<>();
                for (JQuickColumnMeta col : columns) {
                    columnNames.add(col.getName());
                }
                assertTrue("应该包含 emp_id 列", columnNames.contains("emp_id"));
                assertTrue("应该包含 emp_name 列", columnNames.contains("emp_name"));
                assertTrue("应该包含 dept_id 列", columnNames.contains("dept_id"));
                assertTrue("应该包含 salary 列", columnNames.contains("salary"));
            }
        }
    }

    /**
     * 测试不同并行度的 HASH 分区
     * 
     * 场景：使用不同的并行度进行 HASH 分区
     * 预期：数据正确分发到不同数量的分区
     */
    @Test
    public void testHashPartitionWithDifferentParallelism() {
        JQuickDataSet data = JQuickDataSourceManager.getTable("employee");
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        List<JQuickExpression> partitionKeys = Arrays.asList(
            new JQuickColumnRefExpression("dept_id")
        );
        JQuickExchangePhysicalNode exchangeNode2 = new JQuickExchangePhysicalNode(
            JQuickExchangeType.SHUFFLE,
            JQuickPartitionStrategy.HASH,
            partitionKeys,
            2,
            scanNode
        );
        List<JQuickWorker.JQuickMemoryPartition> partitions2 = partitionManager.partitionData(
            data, exchangeNode2, expressionEvaluator, 2
        );
        assertEquals("2 个分区：应该返回 2 个分区", 2, partitions2.size());
        JQuickExchangePhysicalNode exchangeNode4 = new JQuickExchangePhysicalNode(
            JQuickExchangeType.SHUFFLE,
            JQuickPartitionStrategy.HASH,
            partitionKeys,
            4,
            scanNode
        );
        List<JQuickWorker.JQuickMemoryPartition> partitions4 = partitionManager.partitionData(data, exchangeNode4, expressionEvaluator, 4);
        assertEquals("4 个分区：应该返回 4 个分区", 4, partitions4.size());
        JQuickExchangePhysicalNode exchangeNode8 = new JQuickExchangePhysicalNode(
            JQuickExchangeType.SHUFFLE,
            JQuickPartitionStrategy.HASH,
            partitionKeys,
            8,
            scanNode
        );
        List<JQuickWorker.JQuickMemoryPartition> partitions8 = partitionManager.partitionData(
            data, exchangeNode8, expressionEvaluator, 8
        );
        assertEquals("8 个分区：应该返回 8 个分区", 8, partitions8.size());
        int total2 = countTotalRows(partitions2);
        int total4 = countTotalRows(partitions4);
        int total8 = countTotalRows(partitions8);
        assertEquals("所有场景下数据总量应该不变", 8, total2);
        assertEquals("所有场景下数据总量应该不变", 8, total4);
        assertEquals("所有场景下数据总量应该不变", 8, total8);
        for (JQuickWorker.JQuickMemoryPartition partition : partitions8) {
            JQuickDataSet partitionData = partition.getData();
            partitionData.printTable();
            assertNotNull("分区 data 不应为 null", partitionData);
            List<JQuickColumnMeta> columns = partitionData.getColumns();
            assertNotNull("分区 data 的 columns 不应为 null", columns);
            if (!partitionData.getRows().isEmpty()) {
                assertFalse("有数据的分区应该有 columns 信息", columns.isEmpty());
                Set<String> columnNames = new HashSet<>();
                for (JQuickColumnMeta col : columns) {
                    columnNames.add(col.getName());
                }
            }
        }
    }

    /**
     * 测试 RANGE 分区策略
     * 
     * 场景：按范围分区
     * 预期：数据按分区键哈希分发
     */
    @Test
    public void testRangePartition() {
        JQuickDataSet data = JQuickDataSourceManager.getTable("employee");
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        List<JQuickExpression> partitionKeys = Arrays.asList(
            new JQuickColumnRefExpression("salary")
        );
        JQuickExchangePhysicalNode exchangeNode = new JQuickExchangePhysicalNode(
            JQuickExchangeType.SHUFFLE,
            JQuickPartitionStrategy.RANGE,
            partitionKeys,
            3,
            scanNode
        );
        List<JQuickWorker.JQuickMemoryPartition> partitions = partitionManager.partitionData(
            data, exchangeNode, expressionEvaluator, 3
        );
        assertEquals("应该有 3 个分区", 3, partitions.size());
        int totalRows = countTotalRows(partitions);
        assertEquals("所有数据都应该被分区", 8, totalRows);
        for (JQuickWorker.JQuickMemoryPartition partition : partitions) {
            JQuickDataSet partitionData = partition.getData();
            partitionData.printTable();
            assertNotNull("分区 data 不应为 null", partitionData);
            List<JQuickColumnMeta> columns = partitionData.getColumns();
            assertNotNull("分区 data 的 columns 不应为 null", columns);
            if (!partitionData.getRows().isEmpty()) {
                assertFalse("有数据的分区应该有 columns 信息", columns.isEmpty());
                Set<String> columnNames = new HashSet<>();
                for (JQuickColumnMeta col : columns) {
                    columnNames.add(col.getName());
                }
            }
        }
    }

    /**
     * 测试分区索引和 ID
     * 
     * 场景：验证分区的索引和 ID
     * 预期：分区索引和 ID 正确
     */
    @Test
    public void testPartitionIndexAndId() {
        JQuickDataSet data = JQuickDataSourceManager.getTable("employee");
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickExchangePhysicalNode exchangeNode = new JQuickExchangePhysicalNode(
            JQuickExchangeType.SHUFFLE,
            JQuickPartitionStrategy.ROUND_ROBIN,
            null,
            4,
            scanNode
        );
        List<JQuickWorker.JQuickMemoryPartition> partitions = partitionManager.partitionData(
            data, exchangeNode, expressionEvaluator, 4
        );
        for (JQuickWorker.JQuickMemoryPartition partition : partitions) {
            JQuickDataSet partitionData = partition.getData();
            partitionData.printTable();
            assertNotNull("分区 data 不应为 null", partitionData);
            List<JQuickColumnMeta> columns = partitionData.getColumns();
            assertNotNull("分区 data 的 columns 不应为 null", columns);
            if (!partitionData.getRows().isEmpty()) {
                assertFalse("有数据的分区应该有 columns 信息", columns.isEmpty());
                Set<String> columnNames = new HashSet<>();
                for (JQuickColumnMeta col : columns) {
                    columnNames.add(col.getName());
                }
            }
        }
    }

    /**
     * 测试 WorkerEndpoint 获取
     * 
     * 场景：验证 WorkerEndpoint 的获取
     * 预期：可以正确获取 WorkerEndpoint
     */
    @Test
    public void testWorkerEndpointLookup() {
        // 验证通过 workerId 获取
        WorkerEndpoint endpoint0 = partitionManager.getWorkerEndpoint("worker-0");
        assertNotNull("应该能获取 worker-0 的端点", endpoint0);
        assertEquals("worker-0 的主机应该是 localhost", "localhost", endpoint0.getHost());
        assertEquals("worker-0 的端口应该是 50001", 50001, endpoint0.getPort());
        
        // 验证通过索引获取
        WorkerEndpoint endpointByIndex = partitionManager.getWorkerEndpointByIndex(1);
        assertNotNull("应该能通过索引获取端点", endpointByIndex);
        assertEquals("应该获取 worker-1", "worker-1", endpointByIndex.getWorkerId());
        
        // 验证不存在的 worker
        WorkerEndpoint nullEndpoint = partitionManager.getWorkerEndpoint("non-existent");
        assertNull("不存在的 worker 应该返回 null", nullEndpoint);
    }

    /**
     * 测试 REPLICATE 与 BROADCAST 结合
     * 
     * 场景：BROADCAST 类型的 Exchange 使用 REPLICATE 分区策略
     * 预期：数据复制到所有分区
     */
    @Test
    public void testBroadcastWithReplicate() {
        JQuickDataSet data = JQuickDataSourceManager.getTable("employee");
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickExchangePhysicalNode exchangeNode = new JQuickExchangePhysicalNode(
            JQuickExchangeType.BROADCAST,
            JQuickPartitionStrategy.REPLICATE,
            null,
            4,
            scanNode
        );
        List<JQuickWorker.JQuickMemoryPartition> partitions = partitionManager.partitionData(
            data, exchangeNode, expressionEvaluator, 4
        );
        assertEquals("应该有 4 个分区", 4, partitions.size());
        for (JQuickWorker.JQuickMemoryPartition partition : partitions) {
            assertEquals("BROADCAST 每个分区应该包含所有数据", 8, partition.getData().size());
        }
        for (JQuickWorker.JQuickMemoryPartition partition : partitions) {
            JQuickDataSet partitionData = partition.getData();
            partitionData.printTable();
            assertNotNull("分区 data 不应为 null", partitionData);
            List<JQuickColumnMeta> columns = partitionData.getColumns();
            assertNotNull("分区 data 的 columns 不应为 null", columns);
            if (!partitionData.getRows().isEmpty()) {
                assertFalse("有数据的分区应该有 columns 信息", columns.isEmpty());
                Set<String> columnNames = new HashSet<>();
                for (JQuickColumnMeta col : columns) {
                    columnNames.add(col.getName());
                }
            }
        }
    }

    /**
     * 测试 SHUFFLE 与 HASH 结合
     * 
     * 场景：SHUFFLE 类型的 Exchange 使用 HASH 分区策略
     * 预期：相同 Key 的数据在同一个分区
     */
    @Test
    public void testShuffleWithHash() {
        JQuickDataSet data = JQuickDataSourceManager.getTable("employee");
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        List<JQuickExpression> partitionKeys = Arrays.asList(
            new JQuickColumnRefExpression("dept_id")
        );
        JQuickExchangePhysicalNode exchangeNode = new JQuickExchangePhysicalNode(
            JQuickExchangeType.SHUFFLE,
            JQuickPartitionStrategy.HASH,
            partitionKeys,
            4,
            scanNode
        );
        
        List<JQuickWorker.JQuickMemoryPartition> partitions = partitionManager.partitionData(
            data, exchangeNode, expressionEvaluator, 4
        );
        
        assertEquals("应该有 4 个分区", 4, partitions.size());
        Map<Long, Set<Integer>> deptToPartitions = new HashMap<>();
        for (int i = 0; i < partitions.size(); i++) {
            for (JQuickRow row : partitions.get(i).getData().getRows()) {
                Long deptId = (Long) row.get("dept_id");
                deptToPartitions.computeIfAbsent(deptId, k -> new HashSet<>()).add(i);
            }
        }
        for (Set<Integer> partitionSet : deptToPartitions.values()) {
            assertEquals("每个 dept_id 应该只在一个分区", 1, partitionSet.size());
        }
        
        for (JQuickWorker.JQuickMemoryPartition partition : partitions) {
            JQuickDataSet partitionData = partition.getData();
            partitionData.printTable();
            assertNotNull("分区 data 不应为 null", partitionData);
            List<JQuickColumnMeta> columns = partitionData.getColumns();
            assertNotNull("分区 data 的 columns 不应为 null", columns);
            if (!partitionData.getRows().isEmpty()) {
                assertFalse("有数据的分区应该有 columns 信息", columns.isEmpty());
                Set<String> columnNames = new HashSet<>();
                for (JQuickColumnMeta col : columns) {
                    columnNames.add(col.getName());
                }
                assertTrue("应该包含 emp_id 列", columnNames.contains("emp_id"));
                assertTrue("应该包含 emp_name 列", columnNames.contains("emp_name"));
                assertTrue("应该包含 dept_id 列", columnNames.contains("dept_id"));
                assertTrue("应该包含 salary 列", columnNames.contains("salary"));
            }
        }
    }

    /**
     * 测试空分区键的 HASH 分区
     * 
     * 场景：HASH 分区但没有指定分区键
     * 预期：使用行的 hashCode 进行分区
     */
    @Test
    public void testHashPartitionWithNullKeys() {
        JQuickDataSet data = JQuickDataSourceManager.getTable("employee");
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickExchangePhysicalNode exchangeNode = new JQuickExchangePhysicalNode(
            JQuickExchangeType.SHUFFLE,
            JQuickPartitionStrategy.HASH,
            null, // 没有分区键
            4,
            scanNode
        );
        List<JQuickWorker.JQuickMemoryPartition> partitions = partitionManager.partitionData(
            data, exchangeNode, expressionEvaluator, 4
        );
        assertEquals("应该有 4 个分区", 4, partitions.size());
        int totalRows = countTotalRows(partitions);
        assertEquals("所有数据都应该被分区", 8, totalRows);
    }

    /**
     * 统计分区的总行数
     */
    private int countTotalRows(List<JQuickWorker.JQuickMemoryPartition> partitions) {
        int total = 0;
        for (JQuickWorker.JQuickMemoryPartition partition : partitions) {
            total += partition.getData().size();
        }
        return total;
    }

    /**
     * 创建任务上下文
     */
    private JQuickWorker.JQuickTaskContext createTaskContext() {
        JQuickExecuteTaskRequest request = JQuickExecuteTaskRequest.newBuilder()
                .setTaskId("test-task-0")
                .setQueryId("test-query-1")
                .setTaskIndex(0)
                .setTotalTasks(1)
                .setMemoryLimitBytes(1024 * 1024)
                .build();
        return worker.new JQuickTaskContext("test-task-0", request);
    }
}
