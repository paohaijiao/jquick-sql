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
package com.github.paohaijiao.distributed.coordinator;

import com.github.paohaijiao.console.JConsole;
import com.github.paohaijiao.datasource.JQuickDataSourceManager;
import com.github.paohaijiao.distributed.JQuickDistributedPlan;
import com.github.paohaijiao.distributed.worker.JQuickDataConverter;
import com.github.paohaijiao.enums.JQuickFragmentType;
import com.github.paohaijiao.fragment.JQuickFragment;
import com.github.paohaijiao.fragment.JQuickFragmenter;
import com.github.paohaijiao.physical.JQuickPhysicalPlanNode;
import com.github.paohaijiao.physical.domain.JQuickPhysicalColumn;
import com.github.paohaijiao.physical.domain.JQuickPhysicalStats;
import com.github.paohaijiao.physical.node.*;
import com.github.paohaijiao.proto.*;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * 协调器节点 - 负责分布式计划的调度和执行
 * <p>
 * 核心职责：
 * 1. 接收物理计划，通过 Fragmenter 切分为分布式片段
 * 2. 调度各片段到 Worker 节点执行
 * 3. 收集执行结果并合并
 * 4. 处理任务失败和重试
 * 5. 管理查询生命周期
 */
public class JQuickCoordinator extends JQuickConvertService{


    private JConsole console=JConsole.initConsoleEnvironment();


    // 默认配置
    private static final int DEFAULT_WORKER_PORT = 9000;

    private static final long DEFAULT_TASK_TIMEOUT_MS = 30000;

    private static final int DEFAULT_MAX_RETRIES = 3;

    private static final int DEFAULT_BATCH_SIZE = 1000;

    private final String coordinatorId;

    private final JQuickFragmenter fragmenter;

    private final JQuickDataConverter dataConverter;

    private final List<WorkerEndpoint> workers;

    private final Map<String, WorkerEndpoint> workerIdMap;

    private final Map<Integer, WorkerEndpoint> workerIndexMap;

    // gRPC 连接管理
    private final Map<String, ManagedChannel> workerChannels;

    private final Map<String, JQuickPhysicalPlanServiceGrpc.JQuickPhysicalPlanServiceStub> workerStubs;

    private final Map<String, QueryExecution> activeQueries;

    // 配置
    private final long taskTimeoutMs;

    private final int maxRetries;

    private final int batchSize;

    private volatile boolean running = true;

    /**
     * 构造函数 - 使用 Worker 列表
     */
    public JQuickCoordinator(String coordinatorId, List<WorkerEndpoint> workers) {
        this(coordinatorId, workers, DEFAULT_TASK_TIMEOUT_MS, DEFAULT_MAX_RETRIES, DEFAULT_BATCH_SIZE);
    }

    /**
     * 构造函数 - 完整参数
     */
    public JQuickCoordinator(String coordinatorId, List<WorkerEndpoint> workers, long taskTimeoutMs, int maxRetries, int batchSize) {
        this.coordinatorId = coordinatorId;
        this.fragmenter = new JQuickFragmenter();
        this.dataConverter = new JQuickDataConverter();
        this.workers = new ArrayList<>(workers);
        this.workerIdMap = new ConcurrentHashMap<>();
        this.workerIndexMap = new ConcurrentHashMap<>();
        this.workerChannels = new ConcurrentHashMap<>();
        this.workerStubs = new ConcurrentHashMap<>();
        this.activeQueries = new ConcurrentHashMap<>();
        this.taskTimeoutMs = taskTimeoutMs;
        this.maxRetries = maxRetries;
        this.batchSize = batchSize;
        for (int i = 0; i < workers.size(); i++) {
            WorkerEndpoint worker = workers.get(i);
            workerIdMap.put(worker.getWorkerId(), worker);
            workerIndexMap.put(i, worker);
        }
        console.info("Coordinator initialized - no executor threads, all operations will be synchronous");
    }


    /**
     * 设置带超时的查询执行
     */
    public JQuickDataSet executeQuery(String queryId, JQuickPhysicalPlanNode physicalPlan) {
        console.info("Executing query synchronously - queryId: " + queryId);
        JQuickDistributedPlan distributedPlan = fragmenter.fragment(physicalPlan);//切分物理计划为分布式片段
        QueryExecution execution = new QueryExecution(queryId, distributedPlan); //创建查询执行上下文
        activeQueries.put(queryId, execution);
        execution.setStatus(QueryExecution.QueryStatus.PLANNING);
        try {
            return doExecuteQuery(execution);
        } catch (Exception e) {
            execution.setStatus(QueryExecution.QueryStatus.FAILED);
            execution.setErrorMessage(e.getMessage());
            console.error(String.format("Query execution failed - queryId: %s", queryId), e);
            throw new RuntimeException("Query execution failed", e);
        } finally {
            cleanupQuery(queryId);
        }
    }

    /**
     * 使用已切分好的计划执行查询
     */
    public JQuickDataSet executeQueryWithPlan(String queryId, JQuickDistributedPlan distributedPlan) {
        console.info("Executing query with pre-built plan - queryId: " + queryId);
        QueryExecution execution = new QueryExecution(queryId, distributedPlan);
        activeQueries.put(queryId, execution);
        execution.setStatus(QueryExecution.QueryStatus.PLANNING);
        try {
            return doExecuteQuery(execution);
        } catch (Exception e) {
            execution.setStatus(QueryExecution.QueryStatus.FAILED);
            execution.setErrorMessage(e.getMessage());
            console.error(String.format("Query execution failed - queryId: %s", queryId), e);
            throw new RuntimeException("Query execution failed", e);
        } finally {
            cleanupQuery(queryId);
        }
    }

    /**
     * 实际执行查询（同步版本）
     */
    private JQuickDataSet doExecuteQuery(QueryExecution execution) throws Exception {
        console.info("Executing doExecuteQuery synchronously - queryId: " + execution.getQueryId());
        JQuickDistributedPlan plan = execution.getDistributedPlan();
        JQuickFragment rootFragment = plan.getRootFragment();
        execution.setStatus(QueryExecution.QueryStatus.SCHEDULING);
        List<JQuickFragment> allFragments = collectAllFragments(rootFragment); //遍历所有 Fragment，调度执行
        List<JQuickFragment> sortedFragments = topologicalSort(allFragments); //按依赖关系排序（叶子节点先执行）
        console.info("Fragment execution order: " + sortedFragments.stream().map(f -> f.getFragmentId() + "(" + f.getType() + ")").collect(Collectors.joining(" -> ")));
        execution.setStatus(QueryExecution.QueryStatus.RUNNING);
        // 按顺序执行 Fragment，确保子 Fragment 完成后再执行父 Fragment
        // 拓扑排序已经保证了顺序：SOURCE -> INTERMEDIATE -> SINK
        for (JQuickFragment fragment : sortedFragments) {
            console.info("Executing fragment synchronously - fragmentId: " + fragment.getFragmentId() + ", type: " + fragment.getType());
            // 执行当前 Fragment（同步执行）
            List<JQuickDataSet> fragmentResult = executeFragment(fragment, execution);
            // 使用 execution 存储 fragment results，以便后续 fragment 可以通过 getFragmentResult 获取
            execution.addFragmentResult(fragment.getFragmentId(), fragmentResult);
            console.info("Fragment " + fragment.getFragmentId() + " completed, result rows: " + (fragmentResult != null ? fragmentResult.stream().mapToInt(JQuickDataSet::size).sum() : 0));
        }
        List<JQuickDataSet> rootResults = execution.getFragmentResult(rootFragment.getFragmentId()); //获取根 Fragment 结果
        JQuickDataSet finalResult = mergeResults(rootResults); //合并结果
        finalResult.printSummary();
        console.info("Executing finalResult : " + execution.getQueryId());
        execution.setStatus(QueryExecution.QueryStatus.COMPLETED);
        console.info(String.format("Query completed - queryId: %s, duration: %dms, resultRows: %d", execution.getQueryId(), execution.getExecutionTimeMs(), finalResult.size()));
        return finalResult;
    }

    /**
     * 执行单个 Fragment（同步版本）
     */
    private List<JQuickDataSet> executeFragment(JQuickFragment fragment, QueryExecution execution) throws Exception {
        JQuickFragmentType fragmentType = fragment.getType();
        int parallelism = fragment.getParallelism();
        console.info(String.format("Executing fragment synchronously - fragmentId: %d, type: %s, parallelism: %d", fragment.getFragmentId(), fragmentType, parallelism));
        List<JQuickDataSet> results = new ArrayList<>();
        for (int taskIndex = 0; taskIndex < parallelism; taskIndex++) { // 为每个并行度创建任务
            // 同步执行任务
            JQuickExecuteTaskResponse response = scheduleTask(fragment, taskIndex, parallelism, execution);
            if (response.getStatus() == JQuickTaskStatusProto.TASK_SUCCESS) {
                JQuickDataSet data = dataConverter.convertFromProto(response.getResultData());
                if (!data.isEmpty()) {
                    results.add(data);
                }
            } else {
                console.warn(String.format("Task failed - fragmentId: %d, error: %s", fragment.getFragmentId(), response.getErrorMessage()));
            }
        }
        
        return results;
    }

    /**
     * 调度单个任务到 Worker（同步版本）
     */
    private JQuickExecuteTaskResponse scheduleTask(JQuickFragment fragment, int taskIndex, int totalTasks, QueryExecution execution) throws Exception {
        String taskId = String.format("%s_%d_%d", execution.getQueryId(), fragment.getFragmentId(), taskIndex);
        WorkerEndpoint worker = selectWorker(taskIndex, totalTasks);
        TaskExecution taskExec = new TaskExecution(fragment.getFragmentId(), taskIndex, totalTasks, worker);
        execution.getTasks().put(taskId, taskExec);
        taskExec.setStatus(TaskExecution.TaskStatus.SCHEDULED);
        taskExec.setStartTime(System.currentTimeMillis());
        try {
            taskExec.setStatus(TaskExecution.TaskStatus.RUNNING);
            JQuickExecuteTaskResponse response = executeTaskWithRetry(buildTaskRequest(execution.getQueryId(), taskId, fragment, taskIndex, totalTasks, execution), taskExec, execution);
            taskExec.setStatus(TaskExecution.TaskStatus.SUCCESS);
            taskExec.setEndTime(System.currentTimeMillis());
            console.info(String.format("Task completed - taskId: %s, worker: %s, duration: %dms", taskId, worker.getWorkerId(), taskExec.getExecutionTimeMs()));
            return response;
        } catch (Exception e) {
            taskExec.setStatus(TaskExecution.TaskStatus.FAILED);
            taskExec.setErrorMessage(e.getMessage());
            taskExec.setEndTime(System.currentTimeMillis());
            console.warn(String.format("Task failed - taskId: %s, worker: %s", taskId, worker.getWorkerId()), e);
            throw e;
        }
    }

    /**
     * 带重试的任务执行
     */
    private JQuickExecuteTaskResponse executeTaskWithRetry(JQuickExecuteTaskRequest request, TaskExecution taskExec, QueryExecution execution) throws Exception {
        int attempt = 0;
        Exception lastException = null;
        while (attempt <= maxRetries && !execution.isCancelled()) {
            try {
                WorkerEndpoint worker = taskExec.getAssignedWorker();
                if (!worker.isHealthy()) {
                    // 重新选择健康的 Worker
                    worker = selectHealthyWorker();
                    taskExec.getAssignedWorker().setHealthy(false);
                    console.info(String.format("Reassigning task due to unhealthy worker - taskId: %s, newWorker: %s", request.getTaskId(), worker.getWorkerId()));
                }
                return doExecuteTask(request, worker);
            } catch (Exception e) {
                lastException = e;
                attempt++;
                taskExec.incrementRetryCount();
                if (attempt <= maxRetries) {
                    long backoffMs = Math.min(1000 * (long) Math.pow(2, attempt), 10000);
                    console.info(String.format("Retrying task - taskId: %s, attempt: %d/%d, backoff: %dms", request.getTaskId(), attempt, maxRetries, backoffMs));
                    Thread.sleep(backoffMs);
                }
            }
        }
        throw new RuntimeException("Task failed after " + maxRetries + " retries", lastException);
    }

    /**
     * 实际执行单个任务
     */
    private JQuickExecuteTaskResponse doExecuteTask(JQuickExecuteTaskRequest request, WorkerEndpoint worker) throws Exception {
        JQuickPhysicalPlanServiceGrpc.JQuickPhysicalPlanServiceBlockingStub stub = getBlockingStub(worker);
        return stub.executeTask(request);
    }



    /**
     * 构建任务请求
     */
    private JQuickExecuteTaskRequest buildTaskRequest(String queryId, String taskId, JQuickFragment fragment, int taskIndex, int totalTasks, QueryExecution execution) {
        JQuickFragmentProto fragmentProto = convertFragmentToProto(fragment);
        console.info("buildTaskRequest - Building task request for fragment: " + fragment.getFragmentId() + ", taskIndex: " + taskIndex + ", totalTasks: " + totalTasks);
        JQuickExecuteTaskRequest.Builder builder = JQuickExecuteTaskRequest.newBuilder()
                .setQueryId(queryId)
                .setTaskId(taskId)
                .setTaskIndex(taskIndex)
                .setTotalTasks(totalTasks)
                .setFragment(fragmentProto)
                .setMemoryLimitBytes(1024 * 1024 * 1024);
        console.info("buildTaskRequest - Fragment " + fragment.getFragmentId() + " has " + fragment.getInputs().size() + " input exchanges, " + fragment.getChildren().size() + " children");
        if(null!=fragment&&JQuickFragmentType.SOURCE.equals(fragment.getType())&&null!=fragment.getPlan()){
            if(fragment.getPlan() instanceof JQuickTableScanPhysicalNode){
                JQuickTableScanPhysicalNode jQuickPhysicalPlanNode=(JQuickTableScanPhysicalNode)fragment.getPlan();
                JQuickDataSet dataSet= JQuickDataSourceManager.getTable(jQuickPhysicalPlanNode.getTableName());
                JQuickMemoryPartitionProto inputPartition = JQuickMemoryPartitionProto.newBuilder()
                        .setPartitionId(fragment.getOutput().getExchangeId() + "_" + taskIndex)
                        .setPartitionIndex(taskIndex)
                        .setTotalPartitions(totalTasks)
                        .setData(dataConverter.convertToProto(dataSet))
                        .build();
                builder.addInputPartitions(inputPartition);
            }
        }
        // 设置输出分区
        if (fragment.getOutput() != null) {
            JQuickMemoryPartitionProto outputPartition = JQuickMemoryPartitionProto.newBuilder()
                    .setPartitionId(fragment.getOutput().getExchangeId() + "_" + taskIndex)
                    .setPartitionIndex(taskIndex)
                    .setTotalPartitions(totalTasks)
                    .build();
            builder.setOutputPartition(outputPartition);
            console.info("Setting output partition for fragment " + fragment.getFragmentId() + ": " + fragment.getOutput().getExchangeId() + "_" + taskIndex);
        }
        for (WorkerEndpoint endpoint : workers) {
//            builder.addWorkerEndpoints(JQuickWorkerEndpointProto.newBuilder()
//                    .setWorkerId(endpoint.getWorkerId())
//                    .setHost(endpoint.getHost())
//                    .setPort(endpoint.getPort())
//                    .setIndex(endpoint.getIndex())
//                    .build());
        }
        return builder.build();
    }

    /**
     * 将 Fragment 转换为 Proto
     */
    private JQuickFragmentProto convertFragmentToProto(JQuickFragment fragment) {
        JQuickFragmentProto.Builder builder = JQuickFragmentProto.newBuilder()
                .setFragmentId(fragment.getFragmentId())
                .setType(convertFragmentType(fragment.getType()))
                .setParallelism(fragment.getParallelism());
        // 转换物理计划节点
        builder.setPlan(convertPhysicalPlanToProto(fragment.getPlan()));
        return builder.build();
    }

    /**
     * 转换物理计划节点为 Proto
     */
    private JQuickPhysicalPlanNodeProto convertPhysicalPlanToProto(JQuickPhysicalPlanNode node) {
        if (node == null||node.getNodeType().equalsIgnoreCase("Empty")) {
            return JQuickPhysicalPlanNodeProto.newBuilder()
                    .setNodeId("empty")
                    .setNodeType("Empty")
                    .setEmpty(JQuickEmptyNodeProto.newBuilder().build())
                    .build();
        }
        JQuickPhysicalPlanNodeProto.Builder builder = JQuickPhysicalPlanNodeProto.newBuilder()
                .setNodeId(UUID.randomUUID().toString())
                .setNodeType(node.getNodeType());

        for (JQuickPhysicalColumn col : node.getOutputSchema()) {
            builder.addOutputSchema(convertPhysicalColumnToProto(col));
        }

        // 转换统计信息
        JQuickPhysicalStats stats = node.getStats();
        if (stats != null && stats.getEstimatedRowCount() > 0) {
            builder.setStats(convertStatsToProto(stats));
        }

        String nodeType = node.getNodeType();

        switch (nodeType) {
            case "TableScan":
                builder.setTableScan(convertTableScanToProto((JQuickTableScanPhysicalNode) node));
                break;
            case "Filter":
                builder.setFilter(convertFilterToProto((JQuickFilterPhysicalNode) node));
                break;
            case "Project":
                builder.setProject(convertProjectToProto((JQuickProjectPhysicalNode) node));
                break;
            case "HashJoin":
                builder.setHashJoin(convertHashJoinToProto((JQuickHashJoinPhysicalNode) node));
                break;
            case "NestedLoopJoin":
                builder.setNestedLoopJoin(convertNestedLoopJoinToProto((JQuickNestedLoopJoinPhysicalNode) node));
                break;
            case "HashAggregate":
                builder.setHashAggregate(convertHashAggregateToProto((JQuickHashAggregatePhysicalNode) node));
                break;
            case "Sort":
                builder.setSort(convertSortToProto((JQuickSortPhysicalNode) node));
                break;
            case "Limit":
                builder.setLimit(convertLimitToProto((JQuickLimitPhysicalNode) node));
                break;
            case "Exchange":
                builder.setExchange(convertExchangeToProto((JQuickExchangePhysicalNode) node));
                break;
            case "Values":
                builder.setValues(convertValuesToProto((JQuickValuesPhysicalNode) node));
                break;
            case "Empty":
                builder.setEmpty(JQuickEmptyNodeProto.newBuilder().build());
                break;
            case "Window":
                builder.setWindow(convertWindowToProto((JQuickWindowPhysicalNode) node));
                break;
            case "SetOperation":
                builder.setSetOperation(convertSetOperationToProto((JQuickSetOperationPhysicalNode) node));
                break;
            case "TopN":
                builder.setTopN(convertTopNToProto((JQuickTopNPhysicalNode) node));
                break;
            case "RecursiveUnion":
                builder.setRecursiveUnion(convertRecursiveUnionToProto((JQuickRecursiveUnionPhysicalNode) node));
                break;
            default:
                builder.setEmpty(JQuickEmptyNodeProto.newBuilder().build());
        }
        for (JQuickPhysicalPlanNode child : node.getChildren()) {
            builder.addChildNodeIds("child_" + child.getNodeType());
        }
        return builder.build();
    }


    /**
     * 转换 Fragment 类型
     */
    private JQuickFragmentTypeProto convertFragmentType(JQuickFragmentType type) {
        switch (type) {
            case SOURCE:
                return JQuickFragmentTypeProto.FRAGMENT_SOURCE;
            case INTERMEDIATE:
                return JQuickFragmentTypeProto.FRAGMENT_INTERMEDIATE;
            case SINK:
                return JQuickFragmentTypeProto.FRAGMENT_SINK;
            default:
                return JQuickFragmentTypeProto.FRAGMENT_INTERMEDIATE;
        }
    }

    /**
     * 选择 Worker（轮询策略）
     */
    private WorkerEndpoint selectWorker(int taskIndex, int totalTasks) {
        List<WorkerEndpoint> healthyWorkers = workers.stream()
                .filter(WorkerEndpoint::isHealthy)
                .collect(Collectors.toList());

        if (healthyWorkers.isEmpty()) {
            healthyWorkers = workers;
        }

        int index = taskIndex % healthyWorkers.size();
        return healthyWorkers.get(index);
    }

    /**
     * 选择健康的 Worker
     */
    private WorkerEndpoint selectHealthyWorker() {
        return workers.stream()
                .filter(WorkerEndpoint::isHealthy)
                .findFirst()
                .orElse(workers.get(0));
    }

    /**
     * 获取阻塞式 gRPC Stub
     */
    private JQuickPhysicalPlanServiceGrpc.JQuickPhysicalPlanServiceBlockingStub getBlockingStub(WorkerEndpoint worker) {
        ManagedChannel channel = workerChannels.computeIfAbsent(worker.getWorkerId(), k ->
                ManagedChannelBuilder.forAddress(worker.getHost(), worker.getPort())
                        .usePlaintext()
                        .build()
        );
        return JQuickPhysicalPlanServiceGrpc.newBlockingStub(channel);
    }

    /**
     * 获取异步 gRPC Stub
     */
    private JQuickPhysicalPlanServiceGrpc.JQuickPhysicalPlanServiceStub getAsyncStub(WorkerEndpoint worker) {
        ManagedChannel channel = workerChannels.computeIfAbsent(worker.getWorkerId(), k ->
                ManagedChannelBuilder.forAddress(worker.getHost(), worker.getPort())
                        .usePlaintext()
                        .build()
        );
        return JQuickPhysicalPlanServiceGrpc.newStub(channel);
    }

    /**
     * 收集所有 Fragment
     */
    private List<JQuickFragment> collectAllFragments(JQuickFragment root) {
        List<JQuickFragment> fragments = new ArrayList<>();
        collectFragmentsRecursive(root, fragments);
        return fragments;
    }

    private void collectFragmentsRecursive(JQuickFragment fragment, List<JQuickFragment> fragments) {
        fragments.add(fragment);
        for (JQuickFragment child : fragment.getChildren()) {
            collectFragmentsRecursive(child, fragments);
        }
    }

    /**
     * 拓扑排序 Fragment（确保依赖顺序）
     */
    private List<JQuickFragment> topologicalSort(List<JQuickFragment> fragments) {
        Map<JQuickFragment, Integer> depthMap = new HashMap<>();
        for (JQuickFragment fragment : fragments) {
            depthMap.put(fragment, calculateDepth(fragment));
        }
        return fragments.stream().sorted(Comparator.comparingInt(depthMap::get)).collect(Collectors.toList());
    }

    private int calculateDepth(JQuickFragment fragment) {
        int maxChildDepth = 0;
        for (JQuickFragment child : fragment.getChildren()) {
            maxChildDepth = Math.max(maxChildDepth, calculateDepth(child));
        }
        return maxChildDepth + 1;
    }

    /**
     * 合并多个 DataSet 的结果
     */
    private JQuickDataSet mergeResults(List<JQuickDataSet> results) {
        if (results.isEmpty()) {
            return JQuickDataSet.builder().build();
        }
        if (results.size() == 1) {
            return results.get(0);
        }

        List<JQuickRow> allRows = new ArrayList<>();
        for (JQuickDataSet dataSet : results) {
            allRows.addAll(dataSet.getRows());
        }

        return new JQuickDataSet(results.get(0).getColumns(), allRows);
    }

    /**
     * 取消查询
     */
    public CompletableFuture<Boolean> cancelQuery(String queryId, String reason) {
        console.info(String.format("Cancelling query - queryId: %s, reason: %s", queryId, reason));
        QueryExecution execution = activeQueries.get(queryId);
        if (execution == null) {
            return CompletableFuture.completedFuture(false);
        }
        execution.cancel();
        execution.setStatus(QueryExecution.QueryStatus.CANCELLED);
        List<CompletableFuture<Void>> cancelFutures = new ArrayList<>();
        // 向所有正在执行任务的 Worker 发送取消请求
        for (TaskExecution task : execution.getTasks().values()) {
            if (task.getStatus() == TaskExecution.TaskStatus.RUNNING || task.getStatus() == TaskExecution.TaskStatus.SCHEDULED) {
                CompletableFuture<Void> cancelFuture = new CompletableFuture<>();
                cancelFutures.add(cancelFuture);
                JQuickCancelQueryRequest request = JQuickCancelQueryRequest.newBuilder()
                        .setQueryId(queryId)
                        .setReason(reason)
                        .build();
                WorkerEndpoint worker = task.getAssignedWorker();
                JQuickPhysicalPlanServiceGrpc.JQuickPhysicalPlanServiceStub stub = getAsyncStub(worker);
                stub.cancelTask(request, new StreamObserver<JQuickCancelQueryResponse>() {
                    @Override
                    public void onNext(JQuickCancelQueryResponse response) {
                        console.info(String.format("Task cancelled - queryId: %s, worker: %s, success: %s", queryId, worker.getWorkerId(), response.getSuccess()));
                    }
                    @Override
                    public void onError(Throwable t) {
                        console.error(String.format("Cancel task error - queryId: %s", queryId), t);
                        cancelFuture.complete(null);
                    }

                    @Override
                    public void onCompleted() {
                        cancelFuture.complete(null);
                    }
                });
            }
        }

        return CompletableFuture.allOf(cancelFutures.toArray(new CompletableFuture[0]))
                .thenApply(v -> true)
                .exceptionally(e -> false);
    }

    /**
     * 清理查询资源
     */
    private void cleanupQuery(String queryId) {
        activeQueries.remove(queryId);
        console.info(String.format("Cleaned up query - queryId: %s", queryId));
    }

    /**
     * 获取查询状态
     */
    public QueryExecution getQueryStatus(String queryId) {
        return activeQueries.get(queryId);
    }

    /**
     * 获取所有活跃查询
     */
    public Map<String, QueryExecution> getActiveQueries() {
        return Collections.unmodifiableMap(activeQueries);
    }

    /**
     * 获取 Worker 状态
     */
    public List<WorkerEndpoint> getWorkerStatus() {
        return Collections.unmodifiableList(workers);
    }

    /**
     * 添加 Worker
     */
    public void addWorker(WorkerEndpoint worker) {
        workers.add(worker);
        workerIdMap.put(worker.getWorkerId(), worker);
        workerIndexMap.put(workers.size() - 1, worker);
        console.info(String.format("Worker added - %s", worker));
    }

    /**
     * 移除 Worker
     */
    public void removeWorker(String workerId) {
        WorkerEndpoint worker = workerIdMap.remove(workerId);
        if (worker != null) {
            workers.remove(worker);
            workerIndexMap.values().remove(worker);
            ManagedChannel channel = workerChannels.remove(workerId);
            if (channel != null) {
                channel.shutdown();
            }
            console.info(String.format("Worker removed - %s", worker));
        }
    }

    /**
     * 关闭 Coordinator
     */
    public void shutdown() {
        console.info("Shutting down JQuickCoordinator...");
        running = false;
        // 取消所有活跃查询
        for (String queryId : new ArrayList<>(activeQueries.keySet())) {
            cancelQuery(queryId, "Coordinator shutting down");
        }
        // 关闭所有 gRPC 连接
        for (ManagedChannel channel : workerChannels.values()) {
            channel.shutdown();
        }
        console.info("JQuickCoordinator shutdown complete");
    }

    /**
     * 打印执行统计信息
     */
    public void printStatistics() {
        console.info("=== JQuickCoordinator Statistics ===");
        console.info("Coordinator ID: " + coordinatorId);
        console.info("Active Queries: " + activeQueries.size());
        console.info("Workers: " + workers.size());
        console.info("Healthy Workers: " + workers.stream().filter(WorkerEndpoint::isHealthy).count());
        for (QueryExecution execution : activeQueries.values()) {
            console.info("Query: " + execution.getQueryId());
            console.info("  Status: " + execution.getStatus());
            console.info("  Duration: " + execution.getExecutionTimeMs() + "ms");
            console.info("  Tasks: " + execution.getTasks().size());
            long successTasks = execution.getTasks().values().stream().filter(t -> t.getStatus() == TaskExecution.TaskStatus.SUCCESS).count();
            long failedTasks = execution.getTasks().values().stream().filter(t -> t.getStatus() == TaskExecution.TaskStatus.FAILED).count();
            console.info("  Successful Tasks: " + successTasks);
            console.info("  Failed Tasks: " + failedTasks);
        }
    }


    /**
     * 广播表数据到所有 Worker
     */
    public CompletableFuture<Void> broadcastTable(String tableName, JQuickDataSet data, boolean overwrite) {
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (WorkerEndpoint worker : workers) {
            CompletableFuture<Void> future = sendTableToWorker(worker, tableName, data, overwrite);
            futures.add(future);
        }
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    /**
     * 发送表数据到指定 Worker
     */
    private CompletableFuture<Void> sendTableToWorker(WorkerEndpoint worker, String tableName, JQuickDataSet data, boolean overwrite) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        try {
            JQuickTableServiceGrpc.JQuickTableServiceStub stub = getTableServiceStub(worker);
            RegisterTableRequest request = RegisterTableRequest.newBuilder()
                    .setTableName(tableName)
                    .setData(dataConverter.convertToProto(data))
                    .setOverwrite(overwrite)
                    .build();
            stub.registerTable(request, new StreamObserver<RegisterTableResponse>() {
                @Override
                public void onNext(RegisterTableResponse response) {
                    if (response.getSuccess()) {
                        console.info(String.format("Table registered on worker %s - table: %s, rows: %d", worker.getWorkerId(), tableName, response.getRowCount()));
                    } else {
                        console.warn(String.format("Table registration failed on worker %s - table: %s, message: %s", worker.getWorkerId(), tableName, response.getMessage()));
                    }
                }

                @Override
                public void onError(Throwable t) {
                    console.error(String.format("Failed to send table to worker %s", worker.getWorkerId()), t);
                    future.completeExceptionally(t);
                }

                @Override
                public void onCompleted() {
                    future.complete(null);
                }
            });

        } catch (Exception e) {
            future.completeExceptionally(e);
        }

        return future;
    }

    /**
     * 获取 TableService Stub
     */
    private JQuickTableServiceGrpc.JQuickTableServiceStub getTableServiceStub(WorkerEndpoint worker) {
        ManagedChannel channel = workerChannels.computeIfAbsent(worker.getWorkerId(), k ->
                ManagedChannelBuilder.forAddress(worker.getHost(), worker.getPort())
                        .usePlaintext()
                        .build()
        );
        return JQuickTableServiceGrpc.newStub(channel);
    }

    /**
     * 批量广播多个表
     */
    public CompletableFuture<Void> broadcastTables(Map<String, JQuickDataSet> tables, boolean overwrite) {
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (Map.Entry<String, JQuickDataSet> entry : tables.entrySet()) {
            CompletableFuture<Void> future = broadcastTable(entry.getKey(), entry.getValue(), overwrite);
            futures.add(future);
        }
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }
    /**
     * Worker 节点端点信息（轻量级，仅包含连接信息）
     * 注意：这不是 Worker 实现，而是对远程 Worker 的引用
     */
    public static class WorkerEndpoint {

        private final String workerId;

        private final String host;

        private final int port;

        private final int index;

        private volatile boolean healthy;

        private volatile long lastHeartbeat;

        public WorkerEndpoint(String workerId, String host, int port, int index) {
            this.workerId = workerId;
            this.host = host;
            this.port = port;
            this.index = index;
            this.healthy = true;
            this.lastHeartbeat = System.currentTimeMillis();
        }

        public String getWorkerId() {
            return workerId;
        }

        public String getHost() {
            return host;
        }

        public int getPort() {
            return port;
        }

        public int getIndex() {
            return index;
        }

        public boolean isHealthy() {
            return healthy;
        }

        public void setHealthy(boolean healthy) {
            this.healthy = healthy;
        }

        public void updateHeartbeat() {
            this.lastHeartbeat = System.currentTimeMillis();
        }

        public long getLastHeartbeat() {
            return lastHeartbeat;
        }

        @Override
        public String toString() {
            return String.format("WorkerEndpoint{id=%s, host=%s:%d, healthy=%s}", workerId, host, port, healthy);
        }
    }

    /**
     * 任务执行状态
     */
    public static class TaskExecution {

        private final long fragmentId;

        private final int taskIndex;

        private final int totalTasks;

        private final WorkerEndpoint assignedWorker;

        private final CompletableFuture<JQuickExecuteTaskResponse> future;

        private volatile TaskStatus status;

        private volatile int retryCount;

        private volatile String errorMessage;

        private volatile long startTime;

        private volatile long endTime;

        public TaskExecution(long fragmentId, int taskIndex, int totalTasks, WorkerEndpoint worker) {
            this.fragmentId = fragmentId;
            this.taskIndex = taskIndex;
            this.totalTasks = totalTasks;
            this.assignedWorker = worker;
            this.future = new CompletableFuture<>();
            this.status = TaskStatus.PENDING;
            this.retryCount = 0;
            this.startTime = 0;
            this.endTime = 0;
        }

        public long getFragmentId() {
            return fragmentId;
        }

        public int getTaskIndex() {
            return taskIndex;
        }

        public int getTotalTasks() {
            return totalTasks;
        }

        public WorkerEndpoint getAssignedWorker() {
            return assignedWorker;
        }

        public CompletableFuture<JQuickExecuteTaskResponse> getFuture() {
            return future;
        }

        public TaskStatus getStatus() {
            return status;
        }

        public void setStatus(TaskStatus status) {
            this.status = status;
        }

        public int getRetryCount() {
            return retryCount;
        }

        public void incrementRetryCount() {
            retryCount++;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public long getStartTime() {
            return startTime;
        }

        public void setStartTime(long startTime) {
            this.startTime = startTime;
        }

        public long getEndTime() {
            return endTime;
        }

        public void setEndTime(long endTime) {
            this.endTime = endTime;
        }

        public long getExecutionTimeMs() {
            return endTime > 0 ? endTime - startTime : System.currentTimeMillis() - startTime;
        }

        public enum TaskStatus {
            PENDING, SCHEDULED, RUNNING, SUCCESS, FAILED, CANCELLED
        }
    }

    /**
     * 查询执行上下文
     */
    public static class QueryExecution {

        private final String queryId;

        private final JQuickDistributedPlan distributedPlan;

        private final Map<String, TaskExecution> tasks;  // taskId -> TaskExecution

        private final Map<Long, List<JQuickDataChunkProto>> resultChunks;

        private final Map<Long, List<JQuickDataSet>> fragmentResults;  // fragmentId -> List of DataSets

        private final CompletableFuture<JQuickDataSet> resultFuture;

        private final long startTime;

        private volatile boolean cancelled;

        private volatile QueryStatus status;

        private String errorMessage;

        public QueryExecution(String queryId, JQuickDistributedPlan distributedPlan) {
            this.queryId = queryId;
            this.distributedPlan = distributedPlan;
            this.tasks = new ConcurrentHashMap<>();
            this.resultChunks = new ConcurrentHashMap<>();
            this.fragmentResults = new ConcurrentHashMap<>();
            this.resultFuture = new CompletableFuture<>();
            this.startTime = System.currentTimeMillis();
            this.status = QueryStatus.PENDING;
            this.cancelled = false;
        }

        public String getQueryId() {
            return queryId;
        }

        public JQuickDistributedPlan getDistributedPlan() {
            return distributedPlan;
        }

        public Map<String, TaskExecution> getTasks() {
            return tasks;
        }

        public Map<Long, List<JQuickDataSet>> getFragmentResults() {
            return fragmentResults;
        }

        public void addFragmentResult(long fragmentId, List<JQuickDataSet> results) {
            fragmentResults.put(fragmentId, results);
        }

        public List<JQuickDataSet> getFragmentResult(long fragmentId) {
            return fragmentResults.get(fragmentId);
        }

        public CompletableFuture<JQuickDataSet> getResultFuture() {
            return resultFuture;
        }

        public long getStartTime() {
            return startTime;
        }

        public QueryStatus getStatus() {
            return status;
        }

        public void setStatus(QueryStatus status) {
            this.status = status;
        }

        public boolean isCancelled() {
            return cancelled;
        }

        public void cancel() {
            this.cancelled = true;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public long getExecutionTimeMs() {
            return System.currentTimeMillis() - startTime;
        }

        public void addResultChunk(long fragmentId, JQuickDataChunkProto chunk) {
            resultChunks.computeIfAbsent(fragmentId, k -> new CopyOnWriteArrayList<>()).add(chunk);
        }

        public List<JQuickDataChunkProto> getResultChunks(long fragmentId) {
            return resultChunks.getOrDefault(fragmentId, Collections.emptyList());
        }

        public enum QueryStatus {
            PENDING, PLANNING, SCHEDULING, RUNNING, COMPLETED, FAILED, CANCELLED
        }
    }
}