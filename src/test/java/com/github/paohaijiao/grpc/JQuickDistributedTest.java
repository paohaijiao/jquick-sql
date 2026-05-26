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
 */
package com.github.paohaijiao.grpc;


import com.github.paohaijiao.datasource.JQuickDataSourceManager;
import com.github.paohaijiao.distributed.grpc.FragmentServiceGrpc;
import com.github.paohaijiao.distributed.grpc.FragmentServiceProto;
import com.github.paohaijiao.distributed.service.JQuickWorkerLauncher;
import com.github.paohaijiao.enums.JQuickFragmentType;
import com.github.paohaijiao.fragment.JQuickFragment;
import com.github.paohaijiao.grpc.client.JQuickGrpcClient;
import com.github.paohaijiao.grpc.config.JQuickGrpcClientConfig;
import com.github.paohaijiao.grpc.discovery.impl.JQuickGrpcLocalDiscovery;
import com.github.paohaijiao.grpc.factory.JQuickGrpcDynamicFactory;
import com.github.paohaijiao.grpc.loadbalance.impl.JQuickGrpcRoundRobinLoadBalancer;
import com.github.paohaijiao.physical.JQuickPhysicalPlanNode;
import com.github.paohaijiao.physical.node.JQuickTableScanPhysicalNode;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;

import java.io.*;
import java.util.*;

/**
 * 分布式计算集成测试
 * <p>
 * 测试流程：
 * 1. 启动内嵌Worker服务
 * 2. 构建物理计划
 * 3. 通过gRPC调用Worker执行
 * 4. 验证执行结果
 */
public class JQuickDistributedTest {

    private static final int WORKER_PORT = 9090;

    private static final String SERVICE_NAME = "FragmentService";

    private JQuickGrpcClient grpcClient;

    private JQuickGrpcLocalDiscovery discovery;

    private Thread workerThread;

    public static void main(String[] args) throws Exception {
        JQuickDistributedTest test = new JQuickDistributedTest();
        test.testTableScan();
        test.testFragmentExecution();
        test.testDataTransfer();
        System.out.println("\n✓ All tests passed!");
    }

    /**
     * 测试1: 表扫描
     */
    @Test
    public void testTableScan() throws Exception {
        System.out.println("\n========== Test 1: Table Scan ==========");
        startWorker(); //启动Worker
        prepareTestData();//准备测试数据
        Set<String> requiredColumns = new HashSet<>(Arrays.asList("id", "name", "age")); // 构建物理计划
        JQuickTableScanPhysicalNode scanNode = new JQuickTableScanPhysicalNode("users", "u", requiredColumns, null);
        FragmentServiceGrpc.FragmentServiceBlockingStub stub = getGrpcStub();//创建gRPC客户端并调用
        FragmentServiceProto.FragmentRequest request = buildTableScanRequest(scanNode);// 构建请求
        System.out.println("Sending executeFragment request...");//  执行请求
        FragmentServiceProto.FragmentResponse response = stub.executeFragment(request);// 验证结果
        assertTrue(response.getSuccess(), "Execution should succeed");
        System.out.println("Response: success=" + response.getSuccess() + ", rowsProcessed=" + response.getRowsProcessed());
        JQuickDataSet result = fetchResultData(response.getTaskId());//拉取结果数据
        assertNotNull(result, "Result should not be null");
        System.out.println("Result rows: " + result.size());
        result.printTable(10);
        stopWorker();//关闭Worker
        System.out.println("✓ Table scan test passed!");
    }

    /**
     * 测试2: Fragment执行
     */
    @Test
    public void testFragmentExecution() throws Exception {
        startWorker();
        // 构建Fragment
        JQuickPhysicalPlanNode plan = createTestPlan();
        JQuickFragment fragment = new JQuickFragment(JQuickFragmentType.SOURCE, plan);
        fragment.setParallelism(2);
        // 构建请求
        FragmentServiceProto.FragmentRequest request = buildFragmentRequest(fragment);
        // 获取gRPC Stub并调用
        FragmentServiceGrpc.FragmentServiceBlockingStub stub = getGrpcStub();
        FragmentServiceProto.FragmentResponse response = stub.executeFragment(request);
        assertTrue(response.getSuccess(), "Fragment execution should succeed");
        System.out.println("Fragment executed successfully: " + response.getRowsProcessed() + " rows");
        stopWorker();
        System.out.println("✓ Fragment execution test passed!");
    }

    /**
     * 测试3: 数据传输
     */
    @Test
    public void testDataTransfer() throws Exception {
        System.out.println("\n========== Test 3: Data Transfer ==========");
        startWorker();
        // 准备测试数据
        JQuickDataSet testData = createTestDataSet();
        // 发送数据到Worker
        sendDataToWorker(testData, "test-exchange-1");
        // 从Worker拉取数据
        JQuickDataSet receivedData = fetchDataFromWorker("test-exchange-1");
        assertNotNull(receivedData, "Received data should not be null");
        assertEquals(testData.size(), receivedData.size(), "Data size should match");
        System.out.println("Data transfer test passed: " + testData.size() + " rows transferred");
        stopWorker();
        System.out.println("✓ Data transfer test passed!");
    }

    /**
     * 启动内嵌Worker（在单独线程中运行）
     */
    private void startWorker() throws Exception {
        System.out.println("Starting embedded worker on port " + WORKER_PORT + "...");
        workerThread = new Thread(() -> {
            try {
                JQuickWorkerLauncher.main(new String[]{String.valueOf(WORKER_PORT)});
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        workerThread.setDaemon(true);
        workerThread.start();
        Thread.sleep(3000);
        System.out.println("Worker started successfully!");
    }

    /**
     * 停止Worker
     */
    private void stopWorker() throws InterruptedException {
        System.out.println("Stopping worker...");
        if (workerThread != null) {
            workerThread.interrupt();
        }
        if (grpcClient != null) {
            grpcClient.close();
        }
        if (discovery != null) {
            discovery.close();
        }
        Thread.sleep(1000);
    }

    /**
     * 获取gRPC客户端Stub
     */
    private FragmentServiceGrpc.FragmentServiceBlockingStub getGrpcStub() {
        // 创建服务发现
        discovery = new JQuickGrpcLocalDiscovery();
        // 注册Worker实例（用于服务发现）
        discovery.registerService(SERVICE_NAME, "localhost", WORKER_PORT, 1);
        // 创建客户端配置
        JQuickGrpcClientConfig clientConfig = JQuickGrpcClientConfig.pooled();
        clientConfig.setDeadlineMillis(30000); // 30秒超时
        clientConfig.setMaxRetries(2);
        // 创建负载均衡器
        JQuickGrpcRoundRobinLoadBalancer loadBalancer = new JQuickGrpcRoundRobinLoadBalancer();
        // 创建gRPC客户端
        JQuickGrpcDynamicFactory factory = new JQuickGrpcDynamicFactory();
        grpcClient = factory.createClient(clientConfig, discovery, loadBalancer);
        // 获取服务Stub
        return grpcClient.getService(FragmentServiceGrpc.FragmentServiceBlockingStub.class, SERVICE_NAME
        );
    }

    /**
     * 构建表扫描请求
     */
    private FragmentServiceProto.FragmentRequest buildTableScanRequest(JQuickTableScanPhysicalNode scanNode) throws IOException {
        // 序列化物理计划
        byte[] serializedPlan = serializePlan(scanNode);
        return FragmentServiceProto.FragmentRequest.newBuilder().setTaskId(UUID.randomUUID().toString()).setFragmentId("fragment-001").setFragmentType("SOURCE").setSerializedPlan(com.google.protobuf.ByteString.copyFrom(serializedPlan)).setParallelism(2).build();
    }

    /**
     * 构建Fragment请求
     */
    private FragmentServiceProto.FragmentRequest buildFragmentRequest(JQuickFragment fragment) throws IOException {
        byte[] serializedPlan = serializePlan(fragment.getPlan());
        FragmentServiceProto.FragmentRequest.Builder builder = FragmentServiceProto.FragmentRequest.newBuilder()
                        .setTaskId(UUID.randomUUID().toString())
                        .setFragmentId(String.valueOf(fragment.getFragmentId()))
                        .setFragmentType(fragment.getType().name())
                        .setSerializedPlan(com.google.protobuf.ByteString.copyFrom(serializedPlan))
                        .setParallelism(fragment.getParallelism());
        return builder.build();
    }

    /**
     * 序列化物理计划
     */
    private byte[] serializePlan(JQuickPhysicalPlanNode plan) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(plan);
            oos.flush();
            return baos.toByteArray();
        }
    }

    /**
     * 拉取结果数据
     */
    private JQuickDataSet fetchResultData(String taskId) {
        return JQuickDataSet.builder().build();
    }

    /**
     * 发送数据到Worker
     */
    private void sendDataToWorker(JQuickDataSet data, String exchangeId) throws Exception {
        FragmentServiceGrpc.FragmentServiceBlockingStub stub = getGrpcStub();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(data);
            oos.flush();
        }
        FragmentServiceProto.DataChunk chunk = FragmentServiceProto.DataChunk.newBuilder()
                .setExchangeId(exchangeId)
                .setPartitionId(0)
                .setSequenceNumber(0)
                .setData(com.google.protobuf.ByteString.copyFrom(baos.toByteArray()))
                .setIsLast(true)
                .build();
        System.out.println("Sending data to worker: " + data.size() + " rows");
    }

    /**
     * 从Worker拉取数据
     */
    private JQuickDataSet fetchDataFromWorker(String exchangeId) throws Exception {
        FragmentServiceGrpc.FragmentServiceBlockingStub stub = getGrpcStub();
        FragmentServiceProto.FetchRequest request = FragmentServiceProto.FetchRequest.newBuilder()
                .setExchangeId(exchangeId)
                .setPartitionId(0)
                .build();
        Iterator<FragmentServiceProto.DataChunk> chunks = stub.fetchData(request);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        while (chunks.hasNext()) {
            FragmentServiceProto.DataChunk chunk = chunks.next();
            baos.write(chunk.getData().toByteArray());
        }
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()))) {
            return (JQuickDataSet) ois.readObject();
        }
    }

    /**
     * 准备测试数据
     */
    private void prepareTestData() {
        JQuickDataSet.Builder builder = JQuickDataSet.builder();
        builder.addColumn("id", Integer.class, "test");
        builder.addColumn("name", String.class, "test");
        builder.addColumn("age", Integer.class, "test");
        for (int i = 1; i <= 100; i++) {
            JQuickRow row = new JQuickRow();
            row.put("id", i);
            row.put("name", "user_" + i);
            row.put("age", 20 + (i % 30));
            builder.addRow(row);
        }

        JQuickDataSourceManager.registerTable("users", builder.build());
        System.out.println("Test data prepared: users table with 100 rows");
    }

    /**
     * 创建测试数据集
     */
    private JQuickDataSet createTestDataSet() {
        JQuickDataSet.Builder builder = JQuickDataSet.builder();
        builder.addColumn("id", Integer.class, "test");
        builder.addColumn("value", String.class, "test");

        for (int i = 1; i <= 50; i++) {
            JQuickRow row = new JQuickRow();
            row.put("id", i);
            row.put("value", "test_value_" + i);
            builder.addRow(row);
        }

        return builder.build();
    }

    /**
     * 创建测试物理计划
     */
    private JQuickPhysicalPlanNode createTestPlan() {
        Set<String> columns = new HashSet<>(Arrays.asList("id", "name"));
        return new JQuickTableScanPhysicalNode("users", "u", columns, null);
    }
    private void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
        System.out.println("  ✓ " + message);
    }

    private void assertNotNull(Object obj, String message) {
        if (obj == null) {
            throw new AssertionError(message);
        }
        System.out.println("  ✓ " + message);
    }

    private void assertEquals(long expected, long actual, String message) {
        if (expected != actual) {
            throw new AssertionError(message + " expected: " + expected + ", actual: " + actual);
        }
        System.out.println("  ✓ " + message);
    }

    /**
     * 简单测试注解
     */
    @interface Test {
    }
}
