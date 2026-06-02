package com.github.paohaijiao.distributed.worker;

import com.github.paohaijiao.enums.JQuickExchangeType;
import com.github.paohaijiao.function.manager.JQuickMethodInvocationManager;
import com.github.paohaijiao.proto.*;
import com.github.paohaijiao.statement.JQuickColumnMeta;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;
import io.grpc.ManagedChannel;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Worker 节点 - 执行物理计划片段
 */
public class JQuickWorker {

    private final String workerId;

    private final int port;

    private final ExecutorService executor;

    private final Map<String, JQuickTaskContext> activeTasks;

    private final Map<String, JQuickMemoryPartition> memoryPartitions;

    private final Map<Integer, ManagedChannel> workerChannels;

    private final Map<Integer, JQuickDataDistributionServiceGrpc.JQuickDataDistributionServiceStub> distributionStubs;

    private final JQuickMethodInvocationManager functionManager;
    // 服务组件

    private final JQuickExpressionEvaluator expressionEvaluator;

    private final JQuickPartitionManager partitionManager;

    private final JQuickDataConverter dataConverter;

    private final JQuickNodeExecutor nodeExecutor;

    private Server server;

    private JQuickDataDistributionServiceImpl distributionService;

    public JQuickWorker(String workerId, int port) {
        this.workerId = workerId;
        this.port = port;
        this.executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        this.activeTasks = new ConcurrentHashMap<>();
        this.memoryPartitions = new ConcurrentHashMap<>();
        this.workerChannels = new ConcurrentHashMap<>();
        this.distributionStubs = new ConcurrentHashMap<>();
        this.functionManager = JQuickMethodInvocationManager.getInstance();
        this.expressionEvaluator = new JQuickExpressionEvaluator(functionManager);
        this.partitionManager = new JQuickPartitionManager();
        this.dataConverter = new JQuickDataConverter();
        this.nodeExecutor = new JQuickNodeExecutor(this, expressionEvaluator, partitionManager, dataConverter);
    }

    public String getWorkerId() {
        return workerId;
    }

    public Map<String, JQuickMemoryPartition> getMemoryPartitions() {
        return memoryPartitions;
    }

    public JQuickDataConverter getDataConverter() {
        return dataConverter;
    }

    public JQuickExpressionEvaluator getExpressionEvaluator() {
        return expressionEvaluator;
    }

    public JQuickPartitionManager getPartitionManager() {
        return partitionManager;
    }

    public ExecutorService getExecutor() {
        return executor;
    }

    public Map<Integer, ManagedChannel> getWorkerChannels() {
        return workerChannels;
    }

    public Map<Integer, JQuickDataDistributionServiceGrpc.JQuickDataDistributionServiceStub> getDistributionStubs() {
        return distributionStubs;
    }

    /**
     * 执行任务（实现 gRPC 服务方法）
     */
    public JQuickExecuteTaskResponse executeTask(JQuickExecuteTaskRequest request) {
        String taskId = request.getTaskId();
        JQuickTaskContext context = new JQuickTaskContext(taskId, request);
        activeTasks.put(taskId, context);
        try {
            // 接收输入分区数据
            for (JQuickMemoryPartitionProto inputPartition : request.getInputPartitionsList()) {
                receivePartition(inputPartition);
            }
            // 执行物理计划片段
            JQuickDataSet result = nodeExecutor.executeFragment(request.getFragment(), context);
            // 输出结果分区
            if (request.hasOutputPartition()) {
                sendOutputPartition(result, request.getOutputPartition());
            }
            // 构建响应
            return JQuickExecuteTaskResponse.newBuilder()
                    .setTaskId(taskId)
                    .setStatus(JQuickTaskStatusProto.TASK_SUCCESS)
                    .setResultData(dataConverter.convertToProto(result))
                    .setProcessedRows(context.getProcessedRows())
                    .setExecutionTimeMs(context.getExecutionTimeMs())
                    .setMemoryUsedBytes(context.getMemoryUsedBytes())
                    .build();
        } catch (Exception e) {
            return JQuickExecuteTaskResponse.newBuilder().setTaskId(taskId).setStatus(JQuickTaskStatusProto.TASK_FAILED).setErrorMessage(e.getMessage()).build();
        } finally {
            activeTasks.remove(taskId);
        }
    }

    /**
     * 流式执行任务
     */
    public void executeTaskStream(JQuickExecuteTaskRequest request, StreamObserver<JQuickDataChunkProto> responseObserver) {
        String taskId = request.getTaskId();
        JQuickTaskContext context = new JQuickTaskContext(taskId, request);
        activeTasks.put(taskId, context);
        try {
            for (JQuickMemoryPartitionProto inputPartition : request.getInputPartitionsList()) {
                receivePartition(inputPartition);
            }
            JQuickDataSet result = nodeExecutor.executeFragment(request.getFragment(), context);
            sendInChunks(result, responseObserver);
            if (request.hasOutputPartition()) {
                sendOutputPartition(result, request.getOutputPartition());
            }
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        } finally {
            activeTasks.remove(taskId);
        }
    }

    /**
     * 取消任务
     */
    public JQuickCancelQueryResponse cancelTask(JQuickCancelQueryRequest request) {
        String taskId = request.getQueryId();
        JQuickTaskContext context = activeTasks.get(taskId);
        if (context != null) {
            context.cancel();
            activeTasks.remove(taskId);
            return JQuickCancelQueryResponse.newBuilder().setQueryId(request.getQueryId()).setSuccess(true).setMessage("Task cancelled: " + request.getReason()).build();
        }
        return JQuickCancelQueryResponse.newBuilder().setQueryId(request.getQueryId()).setSuccess(false).setMessage("Task not found: " + taskId).build();
    }

    /**
     * 发送数据块
     */
    private void sendInChunks(JQuickDataSet data, StreamObserver<JQuickDataChunkProto> observer) {
        List<JQuickRow> rows = data.getRows();
        int batchSize = 1000;
        int totalChunks = (rows.size() + batchSize - 1) / batchSize;
        for (int i = 0; i < totalChunks; i++) {
            int start = i * batchSize;
            int end = Math.min(start + batchSize, rows.size());
            List<JQuickRow> batch = rows.subList(start, end);
            JQuickDataSet batchDataSet = new JQuickDataSet(data.getColumns(), new ArrayList<>(batch));
            JQuickDataChunkProto chunk = JQuickDataChunkProto.newBuilder()
                    .setChunkIndex(i)
                    .setIsLast(i == totalChunks - 1)
                    .setData(dataConverter.convertToProto(batchDataSet))
                    .setSequenceId(System.currentTimeMillis())
                    .build();
            observer.onNext(chunk);
        }
    }

    private void receivePartition(JQuickMemoryPartitionProto partition) {
        JQuickMemoryPartition memPartition = new JQuickMemoryPartition(partition.getPartitionIndex(), partition.getTotalPartitions());
        memPartition.setData(dataConverter.convertFromProto(partition.getData()));
        memPartition.setChunkIndex(partition.getPartitionIndex());
        memoryPartitions.put(partition.getPartitionId(), memPartition);
    }

    private void sendOutputPartition(JQuickDataSet result, JQuickMemoryPartitionProto outputPartition) {
        JQuickMemoryPartition partition = new JQuickMemoryPartition(outputPartition.getPartitionIndex(), outputPartition.getTotalPartitions());
        partition.setData(result);
        partitionManager.sendToWorker(partition, 1, JQuickExchangeType.GATHER, this);
    }

    /**
     * 启动 Worker 服务
     */
    public void start() throws IOException {
        JQuickPhysicalPlanServiceImpl planService = new JQuickPhysicalPlanServiceImpl(this);
        distributionService = new JQuickDataDistributionServiceImpl(this);
        server = ServerBuilder.forPort(port)
                .addService(planService)
                .addService(distributionService)
                .build()
                .start();
        System.out.println("Worker " + workerId + " started on port " + port);
        System.out.println("Loaded " + functionManager.getAllInvokers().size() + " functions via SPI");
    }

    /**
     * 停止 Worker 服务
     */
    public void stop() {
        if (server != null) {
            server.shutdown();
        }
        executor.shutdown();
        for (ManagedChannel channel : workerChannels.values()) {
            channel.shutdown();
        }
        if (distributionService != null) {
            distributionService.shutdown();
        }
        System.out.println("Worker " + workerId + " stopped");
    }

    /**
     * 等待服务终止
     */
    public void awaitTermination() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    /**
     * 内存分区内部类
     */
    static class JQuickMemoryPartition {
        private final int index;
        private final int total;
        private JQuickDataSet data;
        private int chunkIndex;

        JQuickMemoryPartition(int index, int total) {
            this.index = index;
            this.total = total;
            this.data = JQuickDataSet.builder().build();
            this.chunkIndex = 0;
        }

        void addRow(JQuickRow row) {
            JQuickDataSet.Builder builder = JQuickDataSet.builder();
            for (JQuickColumnMeta col : data.getColumns()) {
                builder.addColumn(col.getName(), col.getType(), col.getSource());
            }
            builder.addRow(row);
            for (JQuickRow existingRow : data.getRows()) {
                builder.addRow(existingRow);
            }
            this.data = builder.build();
        }

        JQuickDataSet getData() {
            return data;
        }

        void setData(JQuickDataSet data) {
            this.data = data;
        }

        /**
         * 获取分区ID - 返回 String 类型
         * 格式: "{index}_{total}"，例如 "0_4" 表示第0个分区，共4个分区
         */
        String getPartitionId() {
            return index + "_" + total;
        }

        int getIndex() {
            return index;
        }

        int getChunkIndex() {
            return chunkIndex;
        }

        void setChunkIndex(int chunkIndex) {
            this.chunkIndex = chunkIndex;
        }

        boolean isLast() {
            return chunkIndex == total - 1;
        }

        long getDataSize() {
            long size = 0;
            for (JQuickRow row : data.getRows()) {
                for (Object value : row.values()) {
                    if (value != null) {
                        size += value.toString().length();
                    }
                }
            }
            return size;
        }
    }

    /**
     * 任务上下文内部类
     */
    class JQuickTaskContext {

        private final String taskId;

        private final JQuickExecuteTaskRequest request;

        private long processedRows;

        private long startTime;

        private long memoryUsed;

        private volatile boolean cancelled;

        JQuickTaskContext(String taskId, JQuickExecuteTaskRequest request) {
            this.taskId = taskId;
            this.request = request;
            this.startTime = System.currentTimeMillis();
            this.memoryUsed = request.getMemoryLimitBytes();
            this.cancelled = false;
        }

        void addProcessedRows(long rows) {
            this.processedRows += rows;
        }

        void cancel() {
            this.cancelled = true;
        }

        boolean isCancelled() {
            return cancelled;
        }

        long getProcessedRows() {
            return processedRows;
        }

        long getExecutionTimeMs() {
            return System.currentTimeMillis() - startTime;
        }

        long getMemoryUsedBytes() {
            return memoryUsed;
        }
    }
}