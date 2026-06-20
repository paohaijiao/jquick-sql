package com.github.paohaijiao.distribute.nodeExecutor.executeNode.exchange;

import com.github.paohaijiao.datasource.JQuickDataSourceManager;
import com.github.paohaijiao.distributed.worker.*;
import com.github.paohaijiao.enums.JQuickExchangeType;
import com.github.paohaijiao.enums.JQuickPartitionStrategy;
import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.expression.domain.JQuickColumnRefExpression;
import com.github.paohaijiao.function.manager.JQuickMethodInvocationManager;
import com.github.paohaijiao.physical.domain.JQuickPhysicalStats;
import com.github.paohaijiao.physical.node.JQuickExchangePhysicalNode;
import com.github.paohaijiao.physical.node.JQuickFilterPhysicalNode;
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
 * JQuickExchangePhysicalNode 测试
 * 
 * 测试范围：
 * 1. 节点属性验证
 * 2. 克隆功能
 * 3. 不同 Exchange 类型（SHUFFLE, BROADCAST, REPARTITION, GATHER, RECEIVE, PIPELINE）
 * 4. 不同分区策略（HASH, RANGE, ROUND_ROBIN, BUCKET, REPLICATE）
 * 5. 分区键设置
 * 6. 目标并行度设置
 * 7. 与其他节点组合（Filter + Exchange）
 */
public class JQuickExchangePhysicalNodeTest {

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
        rows.add(createRow(columns, new Object[]{4L, "David", 2L, 9000.0}));
        rows.add(createRow(columns, new Object[]{5L, "Eve", 3L, 7500.0}));
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
     * 测试 SHUFFLE Exchange 类型
     * 
     * 场景：创建 SHUFFLE 类型的 Exchange 节点
     * 预期：节点属性正确设置
     */
    @Test
    public void testShuffleExchange() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        List<JQuickExpression> partitionKeys = Arrays.asList(new JQuickColumnRefExpression("dept_id"));
        JQuickExchangePhysicalNode exchangeNode = new JQuickExchangePhysicalNode(
            JQuickExchangeType.SHUFFLE,
            JQuickPartitionStrategy.HASH,
            partitionKeys,
            4,
            scanNode
        );
        assertEquals("节点类型应该是 Exchange", "Exchange", exchangeNode.getNodeType());
        assertEquals("Exchange 类型应该是 SHUFFLE", JQuickExchangeType.SHUFFLE, exchangeNode.getExchangeType());
        assertEquals("分区策略应该是 HASH", JQuickPartitionStrategy.HASH, exchangeNode.getPartitionStrategy());
        assertEquals("目标并行度应该是 4", 4, exchangeNode.getTargetParallelism());
        assertEquals("分区键数量应该是 1", 1, exchangeNode.getPartitionKeys().size());
        assertEquals("分区键名称应该是 dept_id", "dept_id", exchangeNode.getPartitionKeys().get(0).getReferencedColumns().get(0));
        assertNotNull("子节点不应为 null", exchangeNode.getChild());
    }

    /**
     * 测试 BROADCAST Exchange 类型
     * 
     * 场景：创建 BROADCAST 类型的 Exchange 节点
     * 预期：节点属性正确设置
     */
    @Test
    public void testBroadcastExchange() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickExchangePhysicalNode exchangeNode = new JQuickExchangePhysicalNode(
            JQuickExchangeType.BROADCAST,
            JQuickPartitionStrategy.REPLICATE,
            null, // 广播不需要分区键
            3,
            scanNode
        );
        assertEquals("Exchange 类型应该是 BROADCAST", JQuickExchangeType.BROADCAST, exchangeNode.getExchangeType());
        assertEquals("分区策略应该是 REPLICATE", JQuickPartitionStrategy.REPLICATE, exchangeNode.getPartitionStrategy());
        assertEquals("目标并行度应该是 3", 3, exchangeNode.getTargetParallelism());
        assertTrue("分区键应该为空", exchangeNode.getPartitionKeys().isEmpty());
    }

    /**
     * 测试 REPARTITION Exchange 类型
     * 
     * 场景：创建 REPARTITION 类型的 Exchange 节点
     * 预期：节点属性正确设置
     */
    @Test
    public void testRepartitionExchange() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickExchangePhysicalNode exchangeNode = new JQuickExchangePhysicalNode(
            JQuickExchangeType.REPARTITION,
            JQuickPartitionStrategy.ROUND_ROBIN,
            null, // 重新分区不需要指定键
            5,
            scanNode
        );
        assertEquals("Exchange 类型应该是 REPARTITION", JQuickExchangeType.REPARTITION, exchangeNode.getExchangeType());
        assertEquals("分区策略应该是 ROUND_ROBIN", JQuickPartitionStrategy.ROUND_ROBIN, exchangeNode.getPartitionStrategy());
        assertEquals("目标并行度应该是 5", 5, exchangeNode.getTargetParallelism());
    }

    /**
     * 测试 GATHER Exchange 类型
     * 
     * 场景：创建 GATHER 类型的 Exchange 节点（用于结果汇聚）
     * 预期：节点属性正确设置
     */
    @Test
    public void testGatherExchange() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickExchangePhysicalNode exchangeNode = new JQuickExchangePhysicalNode(
            JQuickExchangeType.GATHER,
            null, // GATHER 不需要分区策略
            null,
            1, // 汇聚到单个节点
            scanNode
        );
        assertEquals("Exchange 类型应该是 GATHER", JQuickExchangeType.GATHER, exchangeNode.getExchangeType());
        assertNull("分区策略应该为 null", exchangeNode.getPartitionStrategy());
        assertEquals("目标并行度应该是 1", 1, exchangeNode.getTargetParallelism());
    }

    /**
     * 测试 RECEIVE Exchange 类型
     * 
     * 场景：创建 RECEIVE 类型的 Exchange 节点（用于接收远程数据）
     * 预期：节点属性正确设置
     */
    @Test
    public void testReceiveExchange() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickExchangePhysicalNode exchangeNode = new JQuickExchangePhysicalNode(
            JQuickExchangeType.RECEIVE,
            JQuickPartitionStrategy.HASH,
            Arrays.asList(new JQuickColumnRefExpression("dept_id")),
            4,
            scanNode
        );
        
        // 验证节点属性
        assertEquals("Exchange 类型应该是 RECEIVE", JQuickExchangeType.RECEIVE, exchangeNode.getExchangeType());
        assertEquals("分区策略应该是 HASH", JQuickPartitionStrategy.HASH, exchangeNode.getPartitionStrategy());
    }

    /**
     * 测试 PIPELINE Exchange 类型
     * 
     * 场景：创建 PIPELINE 类型的 Exchange 节点（用于本地流水线操作）
     * 预期：节点属性正确设置
     */
    @Test
    public void testPipelineExchange() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickExchangePhysicalNode exchangeNode = new JQuickExchangePhysicalNode(
            JQuickExchangeType.PIPELINE,
            null,
            null,
            1,
            scanNode
        );
        assertEquals("Exchange 类型应该是 PIPELINE", JQuickExchangeType.PIPELINE, exchangeNode.getExchangeType());
        assertNull("分区策略应该为 null", exchangeNode.getPartitionStrategy());
    }

    /**
     * 测试 HASH 分区策略
     * 
     * 场景：使用 HASH 分区策略
     * 预期：按哈希分区键进行分区
     */
    @Test
    public void testHashPartitionStrategy() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        List<JQuickExpression> partitionKeys = Arrays.asList(
            new JQuickColumnRefExpression("emp_id")
        );
        JQuickExchangePhysicalNode exchangeNode = new JQuickExchangePhysicalNode(
            JQuickExchangeType.SHUFFLE,
            JQuickPartitionStrategy.HASH,
            partitionKeys,
            3,
            scanNode
        );
        assertEquals("分区策略应该是 HASH", JQuickPartitionStrategy.HASH, exchangeNode.getPartitionStrategy());
    }

    /**
     * 测试 RANGE 分区策略
     * 
     * 场景：使用 RANGE 分区策略
     * 预期：按范围进行分区
     */
    @Test
    public void testRangePartitionStrategy() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        List<JQuickExpression> partitionKeys = Arrays.asList(
            new JQuickColumnRefExpression("salary")
        );
        JQuickExchangePhysicalNode exchangeNode = new JQuickExchangePhysicalNode(
            JQuickExchangeType.SHUFFLE,
            JQuickPartitionStrategy.RANGE,
            partitionKeys,
            2,
            scanNode
        );
        assertEquals("分区策略应该是 RANGE", JQuickPartitionStrategy.RANGE, exchangeNode.getPartitionStrategy());
    }

    /**
     * 测试 BUCKET 分区策略
     * 
     * 场景：使用 BUCKET 分区策略
     * 预期：按桶进行分区
     */
    @Test
    public void testBucketPartitionStrategy() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickExchangePhysicalNode exchangeNode = new JQuickExchangePhysicalNode(
            JQuickExchangeType.REPARTITION,
            JQuickPartitionStrategy.BUCKET,
            null,
            4,
            scanNode
        );
        assertEquals("分区策略应该是 BUCKET", JQuickPartitionStrategy.BUCKET, exchangeNode.getPartitionStrategy());
    }

    /**
     * 测试多分区键
     * 
     * 场景：设置多个分区键
     * 预期：正确处理多分区键
     */
    @Test
    public void testMultiplePartitionKeys() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        List<JQuickExpression> partitionKeys = Arrays.asList(
            new JQuickColumnRefExpression("dept_id"),
            new JQuickColumnRefExpression("emp_id")
        );
        JQuickExchangePhysicalNode exchangeNode = new JQuickExchangePhysicalNode(
            JQuickExchangeType.SHUFFLE,
            JQuickPartitionStrategy.HASH,
            partitionKeys,
            4,
            scanNode
        );
        assertEquals("分区键数量应该是 2", 2, exchangeNode.getPartitionKeys().size());
    }

    /**
     * 测试克隆功能
     * 
     * 场景：克隆 Exchange 节点
     * 预期：克隆后的节点与原节点配置一致
     */
    @Test
    public void testClone() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        List<JQuickExpression> partitionKeys = Arrays.asList(
            new JQuickColumnRefExpression("dept_id")
        );
        JQuickExchangePhysicalNode originalNode = new JQuickExchangePhysicalNode(
            JQuickExchangeType.SHUFFLE,
            JQuickPartitionStrategy.HASH,
            partitionKeys,
            4,
            scanNode
        );
        JQuickExchangePhysicalNode clonedNode = (JQuickExchangePhysicalNode) originalNode.clone();
        assertNotNull("克隆节点不应为 null", clonedNode);
        assertEquals("Exchange 类型应该相同", originalNode.getExchangeType(), clonedNode.getExchangeType());
        assertEquals("分区策略应该相同", originalNode.getPartitionStrategy(), clonedNode.getPartitionStrategy());
        assertEquals("目标并行度应该相同", originalNode.getTargetParallelism(), clonedNode.getTargetParallelism());
        assertEquals("分区键数量应该相同", originalNode.getPartitionKeys().size(), clonedNode.getPartitionKeys().size());
        assertNotSame("子节点应该是不同的对象", originalNode.getChild(), clonedNode.getChild());
    }

    /**
     * 测试节点属性
     * 
     * 场景：验证节点的各种属性
     * 预期：属性值正确
     */
    @Test
    public void testNodeProperties() {
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
        assertNotNull("输出 Schema 不应为 null", exchangeNode.getOutputSchema());
        assertFalse("输出 Schema 不应为空", exchangeNode.getOutputSchema().isEmpty());
        assertNotNull("Stats 不应为 null", exchangeNode.getStats());
        assertNotNull("子节点不应为 null", exchangeNode.getChild());
        assertEquals("子节点类型应该是 TableScan", "TableScan", exchangeNode.getChild().getNodeType());
    }

    /**
     * 测试 Filter + Exchange 组合
     * 
     * 场景：Filter 节点后接 Exchange 节点
     * 预期：组合后的节点树正确
     */
    @Test
    public void testFilterWithExchange() {
        JQuickExpression filterExpr = new JQuickColumnRefExpression("dept_id");
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickFilterPhysicalNode filterNode = new JQuickFilterPhysicalNode(
                filterExpr,
                scanNode
        );

        List<JQuickExpression> partitionKeys = Arrays.asList(
            new JQuickColumnRefExpression("dept_id")
        );
        JQuickExchangePhysicalNode exchangeNode = new JQuickExchangePhysicalNode(
            JQuickExchangeType.SHUFFLE,
            JQuickPartitionStrategy.HASH,
            partitionKeys,
            4,
            filterNode
        );
        assertNotNull("Filter 子节点应为 TableScan", filterNode.getChild());
        assertEquals("Exchange 子节点应为 Filter", filterNode, exchangeNode.getChild());
    }

    /**
     * 测试不同目标并行度
     * 
     * 场景：设置不同的目标并行度
     * 预期：并行度正确设置
     */
    @Test
    public void testDifferentParallelism() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickExchangePhysicalNode exchangeNode1 = new JQuickExchangePhysicalNode(
            JQuickExchangeType.GATHER,
            null,
            null,
            1,
            scanNode
        );
        assertEquals("并行度应该是 1", 1, exchangeNode1.getTargetParallelism());
        JQuickExchangePhysicalNode exchangeNode8 = new JQuickExchangePhysicalNode(
            JQuickExchangeType.REPARTITION,
            JQuickPartitionStrategy.ROUND_ROBIN,
            null,
            8,
            scanNode
        );
        assertEquals("并行度应该是 8", 8, exchangeNode8.getTargetParallelism());
    }

    /**
     * 测试 null 分区键
     * 
     * 场景：创建不带分区键的 Exchange 节点
     * 预期：分区键为空列表
     */
    @Test
    public void testNullPartitionKeys() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickExchangePhysicalNode exchangeNode = new JQuickExchangePhysicalNode(
            JQuickExchangeType.BROADCAST,
            JQuickPartitionStrategy.REPLICATE,
            null,
            3,
            scanNode
        );
        assertNotNull("分区键列表不应为 null", exchangeNode.getPartitionKeys());
        assertTrue("分区键应该为空", exchangeNode.getPartitionKeys().isEmpty());
    }

    /**
     * 测试 getStats 方法
     * 
     * 场景：获取节点的统计信息
     * 预期：返回子节点的统计信息
     */
    @Test
    public void testGetStats() {
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("employee", null, null, null, null);
        JQuickExchangePhysicalNode exchangeNode = new JQuickExchangePhysicalNode(
            JQuickExchangeType.SHUFFLE,
            JQuickPartitionStrategy.HASH,
            Arrays.asList(new JQuickColumnRefExpression("dept_id")),
            4,
            scanNode
        );

        JQuickPhysicalStats stats = exchangeNode.getStats();
        assertNotNull("统计信息不应为 null", stats);
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
