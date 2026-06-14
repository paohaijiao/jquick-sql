package com.github.paohaijiao.distributed.worker;

import com.github.paohaijiao.console.JConsole;
import com.github.paohaijiao.datasource.JQuickDataSourceManager;
import com.github.paohaijiao.distributed.coordinator.JQuickCoordinator;
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
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Worker 节点 - 执行物理计划片段
 */
public class JQuickWorker {

    private static JConsole console=JConsole.initConsoleEnvironment();

    private final String workerId;

    private final int port;

    private final ExecutorService executor;

    private final Map<String, JQuickTaskContext> activeTasks;

    private final Map<String, JQuickMemoryPartition> memoryPartitions;

    private final Map<Integer, ManagedChannel> workerChannels;

    private JQuickTableServiceImpl tableService;


    private final Map<Integer, JQuickDataDistributionServiceGrpc.JQuickDataDistributionServiceStub> distributionStubs;

    private final JQuickMethodInvocationManager functionManager;

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
    public void setWorkerEndpoints(List<JQuickCoordinator.WorkerEndpoint> endpoints) {
        partitionManager.setWorkerEndpoints(endpoints);
    }
    
    public String getWorkerId() {
        return workerId;
    }
    
    /**
     * 获取当前 Worker 的索引
     * 从 workerId 中提取索引（例如 "worker-1" -> 0, "worker-2" -> 1）
     */
    public int getWorkerIndex() {
        // 从 workerId 中提取数字部分
        try {
            String numStr = workerId.replaceAll("[^0-9]", "");
            if (!numStr.isEmpty()) {
                return Integer.parseInt(numStr) - 1;  // worker-1 -> 0, worker-2 -> 1
            }
        } catch (NumberFormatException e) {
            // 忽略
        }
        return 0;  // 默认返回 0
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
     * 获取所有通过 gRPC 接收到的分区ID
     */
    public Set<String> getAllReceivedPartitions() {
        return distributionService.getAllCachedPartitions();
    }

    /**
     * 获取指定分区的数据（通过 gRPC 接收的）
     */
    public JQuickDataSet getReceivedPartitionData(String partitionId) {
        return distributionService.getPartitionData(partitionId);
    }

    /**
     * 执行任务（实现 gRPC 服务方法）
     */
    public JQuickExecuteTaskResponse executeTask(JQuickExecuteTaskRequest request) {
        String taskId = request.getTaskId();
        JQuickTaskContext context = new JQuickTaskContext(taskId, request);
        activeTasks.put(taskId, context);
        try {
            for (JQuickMemoryPartitionProto inputPartition : request.getInputPartitionsList()) {
                receivePartition(inputPartition);// 接收输入分区数据
            }
            JQuickDataSet result = nodeExecutor.executeFragment(request.getFragment(), context);// 执行物理计划片段
            if (request.hasOutputPartition()) {
                sendOutputPartition(result, request.getOutputPartition());// 输出结果分区
            }
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
        console.info("=== sendOutputPartition Debug ===");
        result.printTable();
        console.info("Result rows: " + result.size());
        console.info("Output partition: " + outputPartition.getPartitionId());
        console.info("Partition index: " + outputPartition.getPartitionIndex());
        console.info("Total partitions: " + outputPartition.getTotalPartitions());
        JQuickMemoryPartition partition = new JQuickMemoryPartition(outputPartition.getPartitionIndex(), outputPartition.getTotalPartitions());
        partition.setPartitionId(outputPartition.getPartitionId()); // 设置正确的 partition ID
        partition.setData(result);
        partitionManager.sendToWorker(partition, 1, JQuickExchangeType.GATHER, this);
    }

    /**
     * 启动 Worker 服务
     */
    public void start() throws IOException {
        JQuickPhysicalPlanServiceImpl planService = new JQuickPhysicalPlanServiceImpl(this);
        distributionService = new JQuickDataDistributionServiceImpl(this);
        tableService = new JQuickTableServiceImpl(dataConverter);
        server = ServerBuilder.forPort(port)
                .addService(planService)
                .addService(distributionService)
                .addService(tableService)
                .build()
                .start();
        console.info("Worker " + workerId + " started on port " + port);
        console.info("Loaded " + functionManager.getAllInvokers().size() + " functions via SPI");
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
        console.info("Worker " + workerId + " stopped");
    }

    /**
     * 等待服务终止
     */
    public void awaitTermination() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }
    public void registerTableLocally(String tableName, JQuickDataSet data) {
        JQuickDataSourceManager.registerTable(tableName, data);
    }

    /**
     * 内存分区内部类
     */
    public static class JQuickMemoryPartition {

        private final int index;

        private final int total;

        private String partitionId;

        private JQuickDataSet data;

        private int chunkIndex;

        public JQuickMemoryPartition(int index, int total) {
            this.index = index;
            this.total = total;
            this.data = JQuickDataSet.builder().build();
            this.chunkIndex = 0;
        }

        public void setPartitionId(String partitionId) {
            this.partitionId = partitionId;
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

        public JQuickDataSet getData() {
            return data;
        }

        public void setData(JQuickDataSet data) {
            this.data = data;
        }

        /**
         * 获取分区ID - 如果设置了 partitionId 则返回，否则返回默认格式
         * 格式: "{index}_{total}"，例如 "0_4" 表示第0个分区，共4个分区
         */
        public String getPartitionId() {
            return partitionId != null ? partitionId : (index + "_" + total);
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
   public class JQuickTaskContext {

        private final String taskId;

        private final JQuickExecuteTaskRequest request;

        private long processedRows;

        private long startTime;

        private long memoryUsed;

        private volatile boolean cancelled;

        public JQuickTaskContext(String taskId, JQuickExecuteTaskRequest request) {
            this.taskId = taskId;
            this.request = request;
            this.startTime = System.currentTimeMillis();
            this.memoryUsed = request.getMemoryLimitBytes();
            this.cancelled = false;
        }

        public void addProcessedRows(long rows) {
            this.processedRows += rows;
        }

        public void cancel() {
            this.cancelled = true;
        }

        public boolean isCancelled() {
            return cancelled;
        }

        public long getProcessedRows() {
            return processedRows;
        }

        public long getExecutionTimeMs() {
            return System.currentTimeMillis() - startTime;
        }

        public long getMemoryUsedBytes() {
            return memoryUsed;
        }
    }
}