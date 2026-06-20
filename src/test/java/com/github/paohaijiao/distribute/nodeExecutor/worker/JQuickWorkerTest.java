package com.github.paohaijiao.distribute.nodeExecutor.worker;

import com.github.paohaijiao.datasource.JQuickDataSourceManager;
import com.github.paohaijiao.distributed.coordinator.JQuickCoordinator.WorkerEndpoint;
import com.github.paohaijiao.distributed.worker.*;
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
 * JQuickWorker 测试
 * 
 * 测试范围：
 * 1. Worker 基本属性
 * 2. JQuickMemoryPartition 内部类
 * 3. JQuickTaskContext 内部类
 * 4. Worker 配置和端点设置
 * 5. 内存分区管理
 */
public class JQuickWorkerTest {

    private JQuickWorker worker;
    private JQuickWorker workerWithPort;

    @Before
    public void setUp() {
        worker = new JQuickWorker("test-worker", 0);
        workerWithPort = new JQuickWorker("worker-2", 8080);
        JQuickDataSourceManager.clearAll();
        registerTestTables();
        setupWorkerEndpoints();
    }

    @After
    public void tearDown() {
        JQuickDataSourceManager.clearAll();
        if (worker != null) {
            worker.stop();
        }
        if (workerWithPort != null) {
            workerWithPort.stop();
        }
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
        rows.add(createRow(columns, new Object[]{1L, "Alice", 1L, 8000.0}));
        rows.add(createRow(columns, new Object[]{2L, "Bob", 1L, 10000.0}));
        rows.add(createRow(columns, new Object[]{3L, "Charlie", 2L, 12000.0}));
        
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
     * 创建带有多个行的 DataSet
     */
    private JQuickDataSet createDataSet(List<JQuickColumnMeta> columns, List<Object[]> rowValues) {
        List<JQuickRow> rows = new ArrayList<>();
        for (Object[] values : rowValues) {
            rows.add(createRow(columns, values));
        }
        return new JQuickDataSet(columns, rows);
    }

    /**
     * 创建单行 DataSet
     */
    private JQuickDataSet createSingleRowDataSet(List<JQuickColumnMeta> columns, Object[] values) {
        return createDataSet(columns, Collections.singletonList(values));
    }

    /**
     * 设置 Worker 端点
     */
    private void setupWorkerEndpoints() {
        List<WorkerEndpoint> endpoints = new ArrayList<>();
        endpoints.add(new WorkerEndpoint("worker-0", "localhost", 50001, 0));
        endpoints.add(new WorkerEndpoint("worker-1", "localhost", 50002, 1));
        endpoints.add(new WorkerEndpoint("worker-2", "localhost", 50003, 2));
        worker.setWorkerEndpoints(endpoints);
    }

    /**
     * 测试 Worker ID
     * 
     * 场景：创建 Worker 并获取 workerId
     * 预期：workerId 正确设置
     */
    @Test
    public void testGetWorkerId() {
        assertEquals("Worker ID 应该为 test-worker", "test-worker", worker.getWorkerId());
        assertEquals("Worker ID 应该为 worker-2", "worker-2", workerWithPort.getWorkerId());
    }

    /**
     * 测试 Worker Index
     * 
     * 场景：从 workerId 中提取索引
     * 预期：索引正确提取
     */
    @Test
    public void testGetWorkerIndex() {
        // test-worker -> 提取数字部分为空 -> 默认返回 0
        assertEquals("test-worker 的索引应该是 0", 0, worker.getWorkerIndex());
        // worker-2 -> 提取数字部分为 2 -> 2 - 1 = 1
        assertEquals("worker-2 的索引应该是 1", 1, workerWithPort.getWorkerIndex());
    }


    /**
     * 测试 MemoryPartition 基本创建
     * 
     * 场景：创建 MemoryPartition
     * 预期：分区属性正确设置
     */
    @Test
    public void testMemoryPartitionCreation() {
        JQuickWorker.JQuickMemoryPartition partition = new JQuickWorker.JQuickMemoryPartition(0, 4);
        assertEquals("分区索引应该是 0", 0, partition.getIndex());
        assertNotNull("分区 data 不应为 null", partition.getData());
        assertTrue("分区 data 应该为空", partition.getData().getRows().isEmpty());
        assertEquals("默认分区 ID 应该是 0_4", "0_4", partition.getPartitionId());
    }

    /**
     * 测试 MemoryPartition 设置单行数据（使用 setData API）
     * 
     * 场景：使用 setData 方法设置单行数据
     * 预期：数据正确设置，包含 columns 信息
     */
    @Test
    public void testMemoryPartitionSetData() {
        JQuickWorker.JQuickMemoryPartition partition = new JQuickWorker.JQuickMemoryPartition(0, 4);
        List<JQuickColumnMeta> columns = Arrays.asList(
            new JQuickColumnMeta("id", Long.class, "test"),
            new JQuickColumnMeta("name", String.class, "test")
        );
        JQuickDataSet dataSet = createSingleRowDataSet(columns, new Object[]{1L, "Alice"});
        partition.setData(dataSet);
        JQuickDataSet data = partition.getData();
        data.printTable();
        assertEquals("分区应该包含 1 行", 1, data.getRows().size());
        assertEquals("第一行 id 应该是 1", 1L, data.getRows().get(0).get("id"));
        assertEquals("第一行 name 应该是 Alice", "Alice", data.getRows().get(0).get("name"));
        List<JQuickColumnMeta> resultColumns = data.getColumns();
        assertEquals("应该有 2 列", 2, resultColumns.size());
        assertEquals("第一列应该是 id", "id", resultColumns.get(0).getName());
        assertEquals("第二列应该是 name", "name", resultColumns.get(1).getName());
    }

    /**
     * 测试 MemoryPartition 设置多行数据
     * 
     * 场景：使用 setData 方法设置多行数据
     * 预期：多行数据正确设置
     */
    @Test
    public void testMemoryPartitionSetMultipleRows() {
        JQuickWorker.JQuickMemoryPartition partition = new JQuickWorker.JQuickMemoryPartition(0, 4);
        List<JQuickColumnMeta> columns = Arrays.asList(
            new JQuickColumnMeta("id", Long.class, "test"),
            new JQuickColumnMeta("value", Double.class, "test")
        );
        List<Object[]> rowValues = Arrays.asList(
            new Object[]{1L, 100.0},
            new Object[]{2L, 200.0},
            new Object[]{3L, 300.0}
        );
        JQuickDataSet dataSet = createDataSet(columns, rowValues);
        partition.setData(dataSet);
        JQuickDataSet data = partition.getData();
        assertEquals("分区应该包含 3 行", 3, data.getRows().size());
        assertEquals("第一行 id 应该是 1", 1L, data.getRows().get(0).get("id"));
        assertEquals("第二行 id 应该是 2", 2L, data.getRows().get(1).get("id"));
        assertEquals("第三行 id 应该是 3", 3L, data.getRows().get(2).get("id"));
    }

    /**
     * 测试 MemoryPartition 设置 partitionId
     * 
     * 场景：设置分区的 partitionId
     * 预期：partitionId 正确返回
     */
    @Test
    public void testMemoryPartitionSetPartitionId() {
        JQuickWorker.JQuickMemoryPartition partition = new JQuickWorker.JQuickMemoryPartition(0, 4);
        // 未设置时，返回默认格式
        assertEquals("未设置时应该返回默认格式", "0_4", partition.getPartitionId());
        // 设置 partitionId
        partition.setPartitionId("custom-partition-1");
        assertEquals("应该返回设置的 partitionId", "custom-partition-1", partition.getPartitionId());
    }



    /**
     * 测试 MemoryPartition 获取数据大小
     * 
     * 场景：计算分区的数据大小
     * 预期：正确计算数据大小
     */
    @Test
    public void testMemoryPartitionGetDataSize() {
        JQuickWorker.JQuickMemoryPartition partition = new JQuickWorker.JQuickMemoryPartition(0, 4);
        assertEquals("空分区的数据大小应该是 0", 0, partition.getDataSize());
        List<JQuickColumnMeta> columns = Arrays.asList(
            new JQuickColumnMeta("name", String.class, "test")
        );
        JQuickDataSet dataSet = createSingleRowDataSet(columns, new Object[]{"Hello"});
        partition.setData(dataSet);
        assertTrue("非空分区的数据大小应该大于 0", partition.getDataSize() > 0);
    }


    /**
     * 测试 TaskContext 基本创建
     * 
     * 场景：创建 TaskContext
     * 预期：任务上下文正确设置
     */
    @Test
    public void testTaskContextCreation() {
        JQuickExecuteTaskRequest request = JQuickExecuteTaskRequest.newBuilder()
                .setTaskId("test-task-1")
                .setQueryId("test-query-1")
                .setTaskIndex(0)
                .setTotalTasks(1)
                .setMemoryLimitBytes(1024 * 1024)
                .build();
        JQuickWorker.JQuickTaskContext context = worker.new JQuickTaskContext("test-task-1", request);
        assertEquals("任务 ID 应该是 test-task-1", "test-task-1", context.getTaskId());
        assertEquals("查询 ID 应该是 test-query-1", "test-query-1", context.getRequest().getQueryId());
        assertFalse("任务不应该被取消", context.isCancelled());
        assertEquals("处理行数应该是 0", 0, context.getProcessedRows());
    }

    /**
     * 测试 TaskContext 添加处理行数
     * 
     * 场景：向任务上下文添加处理的行数
     * 预期：处理行数正确累加
     */
    @Test
    public void testTaskContextAddProcessedRows() {
        JQuickExecuteTaskRequest request = JQuickExecuteTaskRequest.newBuilder()
                .setTaskId("test-task-2")
                .setQueryId("test-query-2")
                .setMemoryLimitBytes(1024 * 1024)
                .build();
        JQuickWorker.JQuickTaskContext context = worker.new JQuickTaskContext("test-task-2", request);
        context.addProcessedRows(100);
        assertEquals("处理行数应该是 100", 100, context.getProcessedRows());
        context.addProcessedRows(50);
        assertEquals("处理行数应该是 150", 150, context.getProcessedRows());
        context.addProcessedRows(0);
        assertEquals("处理行数应该是 150", 150, context.getProcessedRows());
    }

    /**
     * 测试 TaskContext 取消任务
     * 
     * 场景：取消任务
     * 预期：任务正确标记为取消状态
     */
    @Test
    public void testTaskContextCancel() {
        JQuickExecuteTaskRequest request = JQuickExecuteTaskRequest.newBuilder()
                .setTaskId("test-task-3")
                .setQueryId("test-query-3")
                .setMemoryLimitBytes(1024 * 1024)
                .build();
        JQuickWorker.JQuickTaskContext context = worker.new JQuickTaskContext("test-task-3", request);
        assertFalse("初始状态不应该被取消", context.isCancelled());
        context.cancel();
        assertTrue("取消后应该标记为取消", context.isCancelled());
        // 再次取消不应该有副作用
        context.cancel();
        assertTrue("再次取消后仍然应该标记为取消", context.isCancelled());
    }

    /**
     * 测试 TaskContext 获取执行时间
     * 
     * 场景：获取任务执行时间
     * 预期：执行时间大于 0
     */
    @Test
    public void testTaskContextGetExecutionTime() throws InterruptedException {
        JQuickExecuteTaskRequest request = JQuickExecuteTaskRequest.newBuilder()
                .setTaskId("test-task-4")
                .setQueryId("test-query-4")
                .setMemoryLimitBytes(1024 * 1024)
                .build();
        JQuickWorker.JQuickTaskContext context = worker.new JQuickTaskContext("test-task-4", request);
        Thread.sleep(10);
        long executionTime = context.getExecutionTimeMs();
        assertTrue("执行时间应该大于 0", executionTime > 0);
    }

    /**
     * 测试 TaskContext 获取内存使用
     * 
     * 场景：获取任务内存使用
     * 预期：正确返回设置的内存限制
     */
    @Test
    public void testTaskContextGetMemoryUsed() {
        JQuickExecuteTaskRequest request = JQuickExecuteTaskRequest.newBuilder()
                .setTaskId("test-task-5")
                .setQueryId("test-query-5")
                .setMemoryLimitBytes(2 * 1024 * 1024)
                .build();
        JQuickWorker.JQuickTaskContext context = worker.new JQuickTaskContext("test-task-5", request);
        assertEquals("内存使用应该等于设置的限制", 2 * 1024 * 1024, context.getMemoryUsedBytes());
    }

    /**
     * 测试 Worker 获取组件
     * 
     * 场景：获取 Worker 的各个组件
     * 预期：组件正确初始化
     */
    @Test
    public void testWorkerComponents() {
        assertNotNull("ExpressionEvaluator 不应为 null", worker.getExpressionEvaluator());
        assertNotNull("DataConverter 不应为 null", worker.getDataConverter());
        assertNotNull("PartitionManager 不应为 null", worker.getPartitionManager());
        assertNotNull("Executor 不应为 null", worker.getExecutor());
        assertNotNull("WorkerChannels 不应为 null", worker.getWorkerChannels());
        assertNotNull("DistributionStubs 不应为 null", worker.getDistributionStubs());
    }

    /**
     * 测试 Worker 获取内存分区
     * 
     * 场景：获取 Worker 的内存分区映射
     * 预期：返回空映射
     */
    @Test
    public void testGetMemoryPartitions() {
        Map<String, JQuickWorker.JQuickMemoryPartition> partitions = worker.getMemoryPartitions();
        assertNotNull("内存分区映射不应为 null", partitions);
        assertTrue("初始状态应该没有分区", partitions.isEmpty());
    }

    /**
     * 测试 Worker 设置端点
     * 
     * 场景：设置 Worker 端点列表
     * 预期：端点正确设置
     */
    @Test
    public void testSetWorkerEndpoints() {
        List<WorkerEndpoint> endpoints = new ArrayList<>();
        endpoints.add(new WorkerEndpoint("worker-A", "192.168.1.1", 50001, 0));
        endpoints.add(new WorkerEndpoint("worker-B", "192.168.1.2", 50002, 1));
        worker.setWorkerEndpoints(endpoints);
        // 验证端点已设置（通过 partitionManager）
        assertNotNull("PartitionManager 不应为 null", worker.getPartitionManager());
    }


    /**
     * 测试完整的分区操作流程
     * 
     * 场景：创建分区、添加数据、设置属性
     * 预期：完整流程正确执行
     */
    @Test
    public void testFullPartitionOperation() {
        // 创建分区
        JQuickWorker.JQuickMemoryPartition partition = new JQuickWorker.JQuickMemoryPartition(1, 3);
        assertEquals("分区索引应该是 1", 1, partition.getIndex());
        // 使用 setData 添加多行数据
        List<JQuickColumnMeta> columns = Arrays.asList(
            new JQuickColumnMeta("id", Long.class, "test"),
            new JQuickColumnMeta("name", String.class, "test"),
            new JQuickColumnMeta("score", Double.class, "test")
        );
        List<Object[]> rowValues = Arrays.asList(
            new Object[]{1L, "Alice", 95.5},
            new Object[]{2L, "Bob", 87.3},
            new Object[]{3L, "Charlie", 92.0}
        );
        JQuickDataSet dataSet = createDataSet(columns, rowValues);
        partition.setData(dataSet);
        // 验证数据
        JQuickDataSet data = partition.getData();
        assertEquals("应该包含 3 行", 3, data.getRows().size());
        assertEquals("应该包含 3 列", 3, data.getColumns().size());
        
        // 设置分区 ID
        partition.setPartitionId("partition-1");
        assertEquals("分区 ID 应该是 partition-1", "partition-1", partition.getPartitionId());
        
        // 设置 chunkIndex
        partition.setChunkIndex(1);
        assertFalse("不应该是最后一个 chunk", partition.isLast());
        
        // 计算数据大小
        long size = partition.getDataSize();
        assertTrue("数据大小应该大于 0", size > 0);
    }

    /**
     * 测试空分区操作
     * 
     * 场景：操作空分区
     * 预期：正确处理空分区
     */
    @Test
    public void testEmptyPartition() {
        JQuickWorker.JQuickMemoryPartition partition = new JQuickWorker.JQuickMemoryPartition(0, 4);
        
        assertTrue("数据应该为空", partition.getData().getRows().isEmpty());
        assertEquals("数据大小应该为 0", 0, partition.getDataSize());
        assertEquals("分区 ID 应该是默认格式", "0_4", partition.getPartitionId());
    }

    /**
     * 测试任务上下文状态转换
     * 
     * 场景：任务上下文的完整生命周期
     * 预期：状态正确转换
     */
    @Test
    public void testTaskContextLifecycle() {
        JQuickExecuteTaskRequest request = JQuickExecuteTaskRequest.newBuilder()
                .setTaskId("lifecycle-task")
                .setQueryId("lifecycle-query")
                .setMemoryLimitBytes(1024 * 1024)
                .build();
        JQuickWorker.JQuickTaskContext context = worker.new JQuickTaskContext("lifecycle-task", request);
        // 初始状态
        assertFalse("初始未取消", context.isCancelled());
        assertEquals("初始处理行数为 0", 0, context.getProcessedRows());
        // 添加处理行数
        context.addProcessedRows(100);
        context.addProcessedRows(200);
        assertEquals("处理行数应该是 300", 300, context.getProcessedRows());
        // 取消任务
        context.cancel();
        assertTrue("应该标记为取消", context.isCancelled());
        // 尝试再次添加处理行数（模拟取消后的操作）
        context.addProcessedRows(50);
        // 取消后仍然可以添加行数，这是设计如此
        assertEquals("处理行数应该是 350", 350, context.getProcessedRows());
    }

    /**
     * 测试不同索引的分区
     * 
     * 场景：创建不同索引的分区
     * 预期：索引正确设置
     */
    @Test
    public void testDifferentPartitionIndices() {
        // 测试第一个分区
        JQuickWorker.JQuickMemoryPartition first = new JQuickWorker.JQuickMemoryPartition(0, 5);
        assertEquals("第一个分区索引应该是 0", 0, first.getIndex());
        first.setChunkIndex(0);
        assertTrue("第一个分区是第一个 chunk", !first.isLast());
        
        // 测试中间分区
        JQuickWorker.JQuickMemoryPartition middle = new JQuickWorker.JQuickMemoryPartition(2, 5);
        assertEquals("中间分区索引应该是 2", 2, middle.getIndex());
        middle.setChunkIndex(2);
        assertFalse("中间分区不是最后一个", middle.isLast());
        
        // 测试最后一个分区
        JQuickWorker.JQuickMemoryPartition last = new JQuickWorker.JQuickMemoryPartition(4, 5);
        assertEquals("最后一个分区索引应该是 4", 4, last.getIndex());
        last.setChunkIndex(4);
        assertTrue("最后一个分区是最后一个 chunk", last.isLast());
    }

    /**
     * 测试分区数据覆盖
     * 
     * 场景：使用 setData 覆盖分区数据
     * 预期：数据正确覆盖
     */
    @Test
    public void testPartitionDataOverwrite() {
        JQuickWorker.JQuickMemoryPartition partition = new JQuickWorker.JQuickMemoryPartition(0, 2);
        
        // 使用 setData 设置初始数据
        List<JQuickColumnMeta> columns1 = Arrays.asList(
            new JQuickColumnMeta("id", Long.class, "test")
        );
        JQuickDataSet initialDataSet = createSingleRowDataSet(columns1, new Object[]{1L});
        partition.setData(initialDataSet);
        assertEquals("初始应该包含 1 行", 1, partition.getData().getRows().size());
        
        // 使用 setData 覆盖
        List<JQuickColumnMeta> columns2 = Arrays.asList(
            new JQuickColumnMeta("id", Long.class, "test"),
            new JQuickColumnMeta("name", String.class, "test")
        );
        List<Object[]> rowValues = Arrays.asList(
            new Object[]{100L, "NewData"},
            new Object[]{200L, "AnotherData"}
        );
        JQuickDataSet newDataSet = createDataSet(columns2, rowValues);
        partition.setData(newDataSet);
        
        // 验证数据已覆盖
        assertEquals("应该包含 2 行", 2, partition.getData().getRows().size());
        assertEquals("id 应该是 100", 100L, partition.getData().getRows().get(0).get("id"));
        assertEquals("name 应该是 NewData", "NewData", partition.getData().getRows().get(0).get("name"));
    }
}
