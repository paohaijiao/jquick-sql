package com.github.paohaijiao.coordinator;/*
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

import com.github.paohaijiao.datasource.JQuickDataSourceManager;
import com.github.paohaijiao.enums.JQuickBinaryOperator;
import com.github.paohaijiao.enums.JQuickExchangeType;
import com.github.paohaijiao.enums.JQuickPartitionStrategy;
import com.github.paohaijiao.exchange.JQuickExchangeNode;
import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.expression.domain.*;
import com.github.paohaijiao.fragment.JQuickFragment;
import com.github.paohaijiao.physical.JQuickPhysicalPlanNode;
import com.github.paohaijiao.physical.domain.JQuickPhysicalColumn;
import com.github.paohaijiao.physical.domain.JQuickPhysicalStats;
import com.github.paohaijiao.physical.domain.JQuickTablePartitionInfo;
import com.github.paohaijiao.physical.node.*;
import com.github.paohaijiao.proto.*;
import com.github.paohaijiao.statement.JQuickColumnMeta;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;
import com.google.protobuf.Any;
import com.google.protobuf.Value;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

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
    private Server server;

    public JQuickWorker(String workerId, int port) {
        this.workerId = workerId;
        this.port = port;
        this.executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        this.activeTasks = new ConcurrentHashMap<>();
        this.memoryPartitions = new ConcurrentHashMap<>();
        this.workerChannels = new ConcurrentHashMap<>();
        this.distributionStubs = new ConcurrentHashMap<>();
    }

    /**
     * 执行任务（实现 gRPC 服务方法）
     */
    public JQuickExecuteTaskResponse executeTask(JQuickExecuteTaskRequest request) {
        String taskId = request.getTaskId();
        JQuickTaskContext context = new JQuickTaskContext(taskId, request);
        activeTasks.put(taskId, context);

        try {
            // 1. 接收输入分区数据
            for (JQuickMemoryPartitionProto inputPartition : request.getInputPartitionsList()) {
                receivePartition(inputPartition);
            }

            // 2. 执行物理计划片段
            JQuickDataSet result = executeFragment(request.getFragment(), context);

            // 3. 输出结果分区
            if (request.hasOutputPartition()) {
                sendOutputPartition(result, request.getOutputPartition());
            }

            // 4. 构建响应
            return JQuickExecuteTaskResponse.newBuilder()
                    .setTaskId(taskId)
                    .setStatus(JQuickTaskStatusProto.TASK_SUCCESS)
                    .setResultData(convertToProto(result))
                    .setProcessedRows(context.getProcessedRows())
                    .setExecutionTimeMs(context.getExecutionTimeMs())
                    .setMemoryUsedBytes(context.getMemoryUsedBytes())
                    .build();

        } catch (Exception e) {
            return JQuickExecuteTaskResponse.newBuilder()
                    .setTaskId(taskId)
                    .setStatus(JQuickTaskStatusProto.TASK_FAILED)
                    .setErrorMessage(e.getMessage())
                    .build();
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

            JQuickDataSet result = executeFragment(request.getFragment(), context);
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
                    .setData(convertToProto(batchDataSet))
                    .setSequenceId(System.currentTimeMillis())
                    .build();

            observer.onNext(chunk);
        }
    }

    /**
     * 执行片段的核心方法
     */
    private JQuickDataSet executeFragment(JQuickFragmentProto fragment, JQuickTaskContext context) {
        JQuickPhysicalPlanNode rootNode = buildPhysicalNode(fragment.getPlan());
        return executeNode(rootNode, context);
    }

    /**
     * 递归执行物理计划节点
     */
    private JQuickDataSet executeNode(JQuickPhysicalPlanNode node, JQuickTaskContext context) {
        if (node == null) {
            return JQuickDataSet.builder().build();
        }

        if (node instanceof JQuickTableScanPhysicalNode) {
            return executeTableScan((JQuickTableScanPhysicalNode) node, context);
        } else if (node instanceof JQuickFilterPhysicalNode) {
            return executeFilter((JQuickFilterPhysicalNode) node, context);
        } else if (node instanceof JQuickProjectPhysicalNode) {
            return executeProject((JQuickProjectPhysicalNode) node, context);
        } else if (node instanceof JQuickHashJoinPhysicalNode) {
            return executeHashJoin((JQuickHashJoinPhysicalNode) node, context);
        } else if (node instanceof JQuickNestedLoopJoinPhysicalNode) {
            return executeNestedLoopJoin((JQuickNestedLoopJoinPhysicalNode) node, context);
        } else if (node instanceof JQuickHashAggregatePhysicalNode) {
            return executeHashAggregate((JQuickHashAggregatePhysicalNode) node, context);
        } else if (node instanceof JQuickSortPhysicalNode) {
            return executeSort((JQuickSortPhysicalNode) node, context);
        } else if (node instanceof JQuickExchangePhysicalNode) {
            return executeExchange((JQuickExchangePhysicalNode) node, context);
        } else if (node instanceof JQuickLimitPhysicalNode) {
            return executeLimit((JQuickLimitPhysicalNode) node, context);
        } else if (node instanceof JQuickTopNPhysicalNode) {
            return executeTopN((JQuickTopNPhysicalNode) node, context);
        } else if (node instanceof JQuickWindowPhysicalNode) {
            return executeWindow((JQuickWindowPhysicalNode) node, context);
        } else if (node instanceof JQuickSetOperationPhysicalNode) {
            return executeSetOperation((JQuickSetOperationPhysicalNode) node, context);
        } else if (node instanceof JQuickValuesPhysicalNode) {
            return executeValues((JQuickValuesPhysicalNode) node, context);
        } else if (node instanceof JQuickEmptyPhysicalNode) {
            return JQuickDataSet.builder().build();
        }

        throw new UnsupportedOperationException("Unknown node type: " + node.getNodeType());
    }

    /**
     * 执行 TableScan - 从数据源读取数据
     */
    private JQuickDataSet executeTableScan(JQuickTableScanPhysicalNode node, JQuickTaskContext context) {
        String tableName = node.getTableName();
        Set<String> requiredColumns = node.getRequiredColumns();

        JQuickDataSet data;

        if (node.getPartitionInfo() != null && node.isUseMemoryDistribution()) {
            data = readFromMemoryPartition(tableName, requiredColumns);
        } else {
            data = readFromDataSource(tableName, requiredColumns);
        }

        if (node.getFilterPredicate() != null) {
            data = applyFilter(data, node.getFilterPredicate());
        }

        context.addProcessedRows(data.size());
        return data;
    }

    /**
     * 执行 Filter - 过滤数据
     */
    private JQuickDataSet executeFilter(JQuickFilterPhysicalNode node, JQuickTaskContext context) {
        JQuickDataSet input = executeNode(node.getChild(), context);
        JQuickDataSet result = input.filter(row -> evaluatePredicate(row, node.getPredicate()));
        context.addProcessedRows(input.size());
        return result;
    }

    /**
     * 执行 Project - 投影
     */
    private JQuickDataSet executeProject(JQuickProjectPhysicalNode node, JQuickTaskContext context) {
        JQuickDataSet input = executeNode(node.getChild(), context);

        List<JQuickRow> projectedRows = new ArrayList<>();
        for (JQuickRow row : input.getRows()) {
            JQuickRow newRow = new JQuickRow();
            for (JQuickProjectPhysicalNode.SelectItem item : node.getSelectItems()) {
                Object value = evaluateExpression(row, item.getExpression());
                String alias = item.getAlias() != null ? item.getAlias() : "col_" + projectedRows.size();
                newRow.put(alias, value);
            }
            projectedRows.add(newRow);
        }

        if (node.isDistinct()) {
            projectedRows = projectedRows.stream()
                    .distinct()
                    .collect(Collectors.toList());
        }

        return new JQuickDataSet(buildOutputSchemaForProject(node), projectedRows);
    }

    /**
     * 执行 Hash Join
     */
    private JQuickDataSet executeHashJoin(JQuickHashJoinPhysicalNode node, JQuickTaskContext context) {
        JQuickPhysicalPlanNode buildSide = node.getBuildSide() == JQuickHashJoinPhysicalNode.BuildSide.LEFT
                ? node.getLeft() : node.getRight();
        JQuickPhysicalPlanNode probeSide = node.getBuildSide() == JQuickHashJoinPhysicalNode.BuildSide.LEFT
                ? node.getRight() : node.getLeft();

        JQuickDataSet buildData = executeNode(buildSide, context);
        Map<Object, List<JQuickRow>> hashTable = buildHashTable(buildData, node);

        JQuickDataSet probeData = executeNode(probeSide, context);
        List<JQuickRow> resultRows = new ArrayList<>();

        for (JQuickRow probeRow : probeData.getRows()) {
            Object joinKey = extractJoinKey(probeRow, node, false);
            List<JQuickRow> matchingRows = hashTable.get(joinKey);

            if (matchingRows != null) {
                for (JQuickRow buildRow : matchingRows) {
                    JQuickRow joined = joinRows(probeRow, buildRow, node.getJoinType(),
                            node.getBuildSide() == JQuickHashJoinPhysicalNode.BuildSide.LEFT);
                    if (joined != null) {
                        resultRows.add(joined);
                    }
                }
            } else if (node.getJoinType() == com.github.paohaijiao.enums.JQuickJoinType.LEFT ||
                    node.getJoinType() == com.github.paohaijiao.enums.JQuickJoinType.FULL) {
                JQuickRow joined = joinRows(probeRow, null, node.getJoinType(), true);
                if (joined != null) {
                    resultRows.add(joined);
                }
            }
        }

        return new JQuickDataSet(buildOutputSchema(node), resultRows);
    }

    /**
     * 执行 Nested Loop Join
     */
    private JQuickDataSet executeNestedLoopJoin(JQuickNestedLoopJoinPhysicalNode node, JQuickTaskContext context) {
        JQuickDataSet leftData = executeNode(node.getLeft(), context);
        JQuickDataSet rightData = executeNode(node.getRight(), context);
        List<JQuickRow> resultRows = new ArrayList<>();

        for (JQuickRow leftRow : leftData.getRows()) {
            for (JQuickRow rightRow : rightData.getRows()) {
                JQuickRow joined = joinRows(leftRow, rightRow, node.getJoinType(), true);
                if (joined != null) {
                    if (node.getCondition() == null || evaluatePredicate(joined, node.getCondition())) {
                        resultRows.add(joined);
                    }
                }
            }
        }

        return new JQuickDataSet(buildOutputSchema(node), resultRows);
    }

    /**
     * 构建哈希表
     */
    private Map<Object, List<JQuickRow>> buildHashTable(JQuickDataSet data, JQuickHashJoinPhysicalNode node) {
        Map<Object, List<JQuickRow>> hashTable = new HashMap<>();
        for (JQuickRow row : data.getRows()) {
            Object key = extractJoinKey(row, node, true);
            if (key != null) {
                hashTable.computeIfAbsent(key, k -> new ArrayList<>()).add(row);
            }
        }
        return hashTable;
    }

    /**
     * 执行 Hash Aggregate
     */
    private JQuickDataSet executeHashAggregate(JQuickHashAggregatePhysicalNode node, JQuickTaskContext context) {
        JQuickDataSet input = executeNode(node.getChild(), context);

        if (node.getGroupKeys() == null || node.getGroupKeys().isEmpty()) {
            return executeGlobalAggregate(input, node);
        } else {
            return executeGroupedAggregate(input, node);
        }
    }

    /**
     * 分组聚合
     */
    private JQuickDataSet executeGroupedAggregate(JQuickDataSet input, JQuickHashAggregatePhysicalNode node) {
        Map<JQuickRow, List<JQuickRow>> groups = new HashMap<>();

        for (JQuickRow row : input.getRows()) {
            JQuickRow groupKey = extractGroupKey(row, node.getGroupKeys());
            groups.computeIfAbsent(groupKey, k -> new ArrayList<>()).add(row);
        }

        List<JQuickRow> resultRows = new ArrayList<>();
        for (Map.Entry<JQuickRow, List<JQuickRow>> entry : groups.entrySet()) {
            JQuickRow aggregated = new JQuickRow();

            for (Map.Entry<String, Object> keyEntry : entry.getKey().entrySet()) {
                aggregated.put(keyEntry.getKey(), keyEntry.getValue());
            }

            for (JQuickHashAggregatePhysicalNode.AggregateFunction agg : node.getAggregates()) {
                Object value = computeAggregate(entry.getValue(), agg);
                String alias = agg.getAlias() != null ? agg.getAlias() : agg.getFunctionName();
                aggregated.put(alias, value);
            }

            resultRows.add(aggregated);
        }

        if (node.getHavingCondition() != null) {
            resultRows = resultRows.stream()
                    .filter(row -> evaluatePredicate(row, node.getHavingCondition()))
                    .collect(Collectors.toList());
        }

        return new JQuickDataSet(buildOutputSchema(node), resultRows);
    }

    /**
     * 全局聚合
     */
    private JQuickDataSet executeGlobalAggregate(JQuickDataSet input, JQuickHashAggregatePhysicalNode node) {
        JQuickRow result = new JQuickRow();
        for (JQuickHashAggregatePhysicalNode.AggregateFunction agg : node.getAggregates()) {
            Object value = computeAggregate(input.getRows(), agg);
            String alias = agg.getAlias() != null ? agg.getAlias() : agg.getFunctionName();
            result.put(alias, value);
        }
        return new JQuickDataSet(buildOutputSchema(node), Collections.singletonList(result));
    }

    /**
     * 执行 Sort
     */
    private JQuickDataSet executeSort(JQuickSortPhysicalNode node, JQuickTaskContext context) {
        JQuickDataSet input = executeNode(node.getChild(), context);

        List<JQuickRow> sortedRows = new ArrayList<>(input.getRows());
        sortedRows.sort((row1, row2) -> {
            for (JQuickSortPhysicalNode.OrderByItem item : node.getOrderByItems()) {
                Object v1 = row1.get(item.getColumnName());
                Object v2 = row2.get(item.getColumnName());
                int cmp = compareValues(v1, v2, item.isNullsFirst());
                if (cmp != 0) {
                    return item.isAscending() ? cmp : -cmp;
                }
            }
            return 0;
        });

        return new JQuickDataSet(input.getColumns(), sortedRows);
    }

    /**
     * 执行 TopN
     */
    private JQuickDataSet executeTopN(JQuickTopNPhysicalNode node, JQuickTaskContext context) {
        JQuickDataSet sorted = executeSort(node, context);
        List<JQuickRow> rows = sorted.getRows();
        int limit = node.getLimit();
        int offset = node.getOffset();

        if (offset >= rows.size()) {
            return JQuickDataSet.builder().build();
        }

        int endIndex = Math.min(offset + limit, rows.size());
        List<JQuickRow> limitedRows = rows.subList(offset, endIndex);
        return new JQuickDataSet(sorted.getColumns(), new ArrayList<>(limitedRows));
    }

    /**
     * 执行 Limit
     */
    private JQuickDataSet executeLimit(JQuickLimitPhysicalNode node, JQuickTaskContext context) {
        JQuickDataSet input = executeNode(node.getChild(), context);
        List<JQuickRow> rows = input.getRows();
        int limit = node.getLimit();
        int offset = node.getOffset();

        if (offset >= rows.size()) {
            return JQuickDataSet.builder().build();
        }

        int endIndex = Math.min(offset + limit, rows.size());
        List<JQuickRow> limitedRows = rows.subList(offset, endIndex);
        return new JQuickDataSet(input.getColumns(), new ArrayList<>(limitedRows));
    }

    /**
     * 执行 Window Function
     */
    private JQuickDataSet executeWindow(JQuickWindowPhysicalNode node, JQuickTaskContext context) {
        JQuickDataSet input = executeNode(node.getChild(), context);
        List<JQuickRow> resultRows = new ArrayList<>();

        for (JQuickRow row : input.getRows()) {
            JQuickRow newRow = new JQuickRow(row);
            for (JQuickWindowPhysicalNode.WindowFunction wf : node.getWindowFunctions()) {
                Object value = evaluateWindowFunction(input, row, wf);
                newRow.put(wf.getAlias(), value);
            }
            resultRows.add(newRow);
        }

        return new JQuickDataSet(buildOutputSchema(node), resultRows);
    }

    /**
     * 执行 Set Operation
     */
    private JQuickDataSet executeSetOperation(JQuickSetOperationPhysicalNode node, JQuickTaskContext context) {
        JQuickDataSet leftData = executeNode(node.getLeft(), context);
        JQuickDataSet rightData = executeNode(node.getRight(), context);

        List<JQuickRow> resultRows;
        switch (node.getOperationType()) {
            case UNION:
                Set<JQuickRow> unionSet = new HashSet<>(leftData.getRows());
                unionSet.addAll(rightData.getRows());
                resultRows = new ArrayList<>(unionSet);
                break;
            case UNION_ALL:
                resultRows = new ArrayList<>(leftData.getRows());
                resultRows.addAll(rightData.getRows());
                break;
            case INTERSECT:
                Set<JQuickRow> intersectSet = new HashSet<>(leftData.getRows());
                intersectSet.retainAll(new HashSet<>(rightData.getRows()));
                resultRows = new ArrayList<>(intersectSet);
                break;
            case EXCEPT:
                Set<JQuickRow> exceptSet = new HashSet<>(leftData.getRows());
                exceptSet.removeAll(new HashSet<>(rightData.getRows()));
                resultRows = new ArrayList<>(exceptSet);
                break;
            default:
                resultRows = new ArrayList<>(leftData.getRows());
        }

        return new JQuickDataSet(leftData.getColumns(), resultRows);
    }

    /**
     * 执行 Values
     */
    private JQuickDataSet executeValues(JQuickValuesPhysicalNode node, JQuickTaskContext context) {
        List<JQuickRow> rows = new ArrayList<>();
        for (List<Object> rowValues : node.getRows()) {
            JQuickRow row = new JQuickRow();
            for (int i = 0; i < node.getColumnNames().size() && i < rowValues.size(); i++) {
                row.put(node.getColumnNames().get(i), rowValues.get(i));
            }
            rows.add(row);
        }
        return new JQuickDataSet(buildOutputSchema(node), rows);
    }

    /**
     * 执行 Exchange - 数据分发
     */
    private JQuickDataSet executeExchange(JQuickExchangePhysicalNode node, JQuickTaskContext context) {
        JQuickDataSet input = executeNode(node.getChild(), context);

        List<JQuickMemoryPartition> partitions = partitionData(input, node);

        for (JQuickMemoryPartition partition : partitions) {
            sendToWorker(partition, node.getTargetParallelism(), node.getExchangeType());
        }

        return JQuickDataSet.builder().build();
    }

    /**
     * 数据分区逻辑
     */
    private List<JQuickMemoryPartition> partitionData(JQuickDataSet data, JQuickExchangePhysicalNode node) {
        int numPartitions = node.getTargetParallelism();
        List<JQuickMemoryPartition> partitions = new ArrayList<>();
        JQuickPartitionStrategy strategy = node.getPartitionStrategy();

        for (int i = 0; i < numPartitions; i++) {
            partitions.add(new JQuickMemoryPartition(i, numPartitions));
        }

        if (data.isEmpty()) {
            return partitions;
        }

        switch (strategy) {
            case HASH:
                for (JQuickRow row : data.getRows()) {
                    int partition = computeHashPartition(row, node.getPartitionKeys(), numPartitions);
                    partitions.get(partition).addRow(row);
                }
                break;

            case RANGE:
                for (JQuickRow row : data.getRows()) {
                    int partition = computeRangePartition(row, node.getPartitionKeys(), numPartitions);
                    partitions.get(partition).addRow(row);
                }
                break;

            case ROUND_ROBIN:
                int idx = 0;
                for (JQuickRow row : data.getRows()) {
                    partitions.get(idx++ % numPartitions).addRow(row);
                }
                break;

            case BROADCAST:
                for (JQuickRow row : data.getRows()) {
                    for (JQuickMemoryPartition partition : partitions) {
                        partition.addRow(row);
                    }
                }
                break;

            case FORWARD:
            default:
                if (!partitions.isEmpty()) {
                    for (JQuickRow row : data.getRows()) {
                        partitions.get(0).addRow(row);
                    }
                }
                break;
        }

        return partitions;
    }

    /**
     * 计算哈希分区
     */
    private int computeHashPartition(JQuickRow row, List<JQuickExpression> partitionKeys, int numPartitions) {
        int hash = 0;
        for (JQuickExpression key : partitionKeys) {
            Object value = evaluateExpression(row, key);
            hash = 31 * hash + (value != null ? value.hashCode() : 0);
        }
        return Math.abs(hash) % numPartitions;
    }

    /**
     * 计算范围分区
     */
    private int computeRangePartition(JQuickRow row, List<JQuickExpression> partitionKeys, int numPartitions) {
        if (partitionKeys.isEmpty()) {
            return 0;
        }
        Object value = evaluateExpression(row, partitionKeys.get(0));
        if (value == null) {
            return 0;
        }
        // 简化的范围分区：基于 hashCode
        return Math.abs(value.hashCode()) % numPartitions;
    }

    /**
     * 发送数据到目标 Worker
     */
    private void sendToWorker(JQuickMemoryPartition partition, int targetParallelism, JQuickExchangeType exchangeType) {
        if (exchangeType == JQuickExchangeType.GATHER) {
            sendToSingleWorker(partition, 0);
        } else if (exchangeType == JQuickExchangeType.BROADCAST) {
            sendToAllWorkers(partition);
        } else {
            int targetWorkerId = partition.getIndex() % targetParallelism;
            sendToSingleWorker(partition, targetWorkerId);
        }
    }

    /**
     * 发送到单个 Worker
     */
    private void sendToSingleWorker(JQuickMemoryPartition partition, int targetWorkerId) {
        JQuickDataChunkProto chunk = buildDataChunk(partition);
        sendChunkAsync(chunk, targetWorkerId);
    }

    /**
     * 发送到所有 Worker
     */
    private void sendToAllWorkers(JQuickMemoryPartition partition) {
        JQuickDataChunkProto chunk = buildDataChunk(partition);
        for (int i = 0; i < 4; i++) {
            sendChunkAsync(chunk, i);
        }
    }

    /**
     * 构建数据块
     */
    private JQuickDataChunkProto buildDataChunk(JQuickMemoryPartition partition) {
        return JQuickDataChunkProto.newBuilder()
                .setPartitionId(partition.getPartitionId())
                .setData(convertToProto(partition.getData()))
                .setChunkIndex(partition.getChunkIndex())
                .setIsLast(partition.isLast())
                .setSequenceId(System.currentTimeMillis())
                .setOriginalSize(partition.getDataSize())
                .build();
    }

    /**
     * 异步发送数据块
     */
    private void sendChunkAsync(JQuickDataChunkProto chunk, int targetWorkerId) {
        executor.submit(() -> {
            try {
                JQuickDataDistributionServiceGrpc.JQuickDataDistributionServiceStub stub = getDistributionStub(targetWorkerId);
                CompletableFuture<Void> future = new CompletableFuture<>();

                stub.sendData(new StreamObserver<JQuickEmptyNodeProto>() {
                    @Override
                    public void onNext(JQuickEmptyNodeProto value) {}

                    @Override
                    public void onError(Throwable t) {
                        future.completeExceptionally(t);
                    }

                    @Override
                    public void onCompleted() {
                        future.complete(null);
                    }
                }).onNext(chunk);

                future.get(30, TimeUnit.SECONDS);
            } catch (Exception e) {
                // 记录错误，不抛出
            }
        });
    }

    /**
     * 获取分发服务 Stub
     */
    private JQuickDataDistributionServiceGrpc.JQuickDataDistributionServiceStub getDistributionStub(int workerId) {
        return distributionStubs.computeIfAbsent(workerId, id -> {
            ManagedChannel channel = ManagedChannelBuilder
                    .forAddress("localhost", 9000 + id)
                    .usePlaintext()
                    .build();
            workerChannels.put(id, channel);
            return JQuickDataDistributionServiceGrpc.newStub(channel);
        });
    }

    // ========== 表达式求值方法 ==========

    private boolean evaluatePredicate(JQuickRow row, JQuickExpression predicate) {
        if (predicate == null) return true;
        Object result = evaluateExpression(row, predicate);
        if (result instanceof Boolean) {
            return (Boolean) result;
        }
        return result != null;
    }

    private Object evaluateExpression(JQuickRow row, JQuickExpression expr) {
        if (expr == null) return null;

        if (expr instanceof JQuickColumnRefExpression) {
            return row.get(((JQuickColumnRefExpression) expr).getColumnName());

        } else if (expr instanceof JQuickLiteralExpression) {
            return ((JQuickLiteralExpression) expr).getValue();

        } else if (expr instanceof JQuickBinaryExpression) {
            JQuickBinaryExpression binary = (JQuickBinaryExpression) expr;
            Object left = evaluateExpression(row, binary.getLeft());
            Object right = evaluateExpression(row, binary.getRight());
            return applyBinaryOperator(left, right, binary.getOperator());

        } else if (expr instanceof JQuickUnaryExpression) {
            JQuickUnaryExpression unary = (JQuickUnaryExpression) expr;
            Object value = evaluateExpression(row, unary.getExpression());
            return applyUnaryOperator(value, unary.getOperator());

        } else if (expr instanceof JQuickFunctionCallExpression) {
            JQuickFunctionCallExpression func = (JQuickFunctionCallExpression) expr;
            List<Object> args = new ArrayList<>();
            for (JQuickExpression arg : func.getArguments()) {
                args.add(evaluateExpression(row, arg));
            }
            return evaluateFunction(func.getFunctionName(), args);

        } else if (expr instanceof JQuickBetweenExpression) {
            JQuickBetweenExpression between = (JQuickBetweenExpression) expr;
            Object value = evaluateExpression(row, between.getExpression());
            Object low = evaluateExpression(row, between.getLow());
            Object high = evaluateExpression(row, between.getHigh());
            return evaluateBetween(value, low, high, between.isNot());

        } else if (expr instanceof JQuickInExpression) {
            JQuickInExpression in = (JQuickInExpression) expr;
            Object left = evaluateExpression(row, in.getLeft());
            boolean found = false;
            for (JQuickExpression rightExpr : in.getRightList()) {
                Object right = evaluateExpression(row, rightExpr);
                if (Objects.equals(left, right)) {
                    found = true;
                    break;
                }
            }
            return in.isNot() ? !found : found;

        } else if (expr instanceof JQuickCaseWhenExpression) {
            JQuickCaseWhenExpression caseWhen = (JQuickCaseWhenExpression) expr;
            for (int i = 0; i < caseWhen.getConditions().size(); i++) {
                Object condition = evaluateExpression(row, caseWhen.getConditions().get(i));
                if (condition instanceof Boolean && (Boolean) condition) {
                    return evaluateExpression(row, caseWhen.getResults().get(i));
                }
            }
            if (caseWhen.getElseResult() != null) {
                return evaluateExpression(row, caseWhen.getElseResult());
            }
            return null;
        }

        return null;
    }

    private Object applyBinaryOperator(Object left, Object right, JQuickBinaryOperator operator) {
        if (left == null || right == null) {
            return false;
        }

        switch (operator) {
            case EQ:
                return Objects.equals(left, right);
            case NE:
                return !Objects.equals(left, right);
            case GT:
                return compareValues(left, right, false) > 0;
            case LT:
                return compareValues(left, right, false) < 0;
            case GE:
                return compareValues(left, right, false) >= 0;
            case LE:
                return compareValues(left, right, false) <= 0;
            case AND:
                return (left instanceof Boolean && (Boolean) left) && (right instanceof Boolean && (Boolean) right);
            case OR:
                return (left instanceof Boolean && (Boolean) left) || (right instanceof Boolean && (Boolean) right);
            case PLUS:
                return asNumber(left).doubleValue() + asNumber(right).doubleValue();
            case MINUS:
                return asNumber(left).doubleValue() - asNumber(right).doubleValue();
            case MULTIPLY:
                return asNumber(left).doubleValue() * asNumber(right).doubleValue();
            case DIVIDE:
                double divisor = asNumber(right).doubleValue();
                if (divisor == 0) return null;
                return asNumber(left).doubleValue() / divisor;
            case MODULO:
                return asNumber(left).doubleValue() % asNumber(right).doubleValue();
            case LIKE:
                return likeMatch(left.toString(), right.toString());
            default:
                return false;
        }
    }

    private Object applyUnaryOperator(Object value, com.github.paohaijiao.enums.JQuickUnaryOperator operator) {
        switch (operator) {
            case NOT:
                if (value instanceof Boolean) return !(Boolean) value;
                return null;
            case PLUS:
                if (value instanceof Number) return asNumber(value).doubleValue();
                return null;
            case MINUS:
                if (value instanceof Number) return -asNumber(value).doubleValue();
                return null;
            case IS_NULL:
                return value == null;
            case IS_NOT_NULL:
                return value != null;
            default:
                return null;
        }
    }

    private Object evaluateFunction(String functionName, List<Object> args) {
        functionName = functionName.toLowerCase();

        switch (functionName) {
            case "upper":
                return args.isEmpty() || args.get(0) == null ? null : args.get(0).toString().toUpperCase();
            case "lower":
                return args.isEmpty() || args.get(0) == null ? null : args.get(0).toString().toLowerCase();
            case "length":
                return args.isEmpty() || args.get(0) == null ? 0L : (long) args.get(0).toString().length();
            case "concat":
                StringBuilder sb = new StringBuilder();
                for (Object arg : args) {
                    if (arg != null) sb.append(arg);
                }
                return sb.toString();
            case "substring":
                if (args.size() < 2) return null;
                String str = args.get(0) == null ? "" : args.get(0).toString();
                int start = ((Number) args.get(1)).intValue() - 1;
                if (start < 0) start = 0;
                if (start >= str.length()) return "";
                if (args.size() >= 3) {
                    int length = ((Number) args.get(2)).intValue();
                    int end = Math.min(start + length, str.length());
                    return str.substring(start, end);
                }
                return str.substring(start);
            case "trim":
                return args.isEmpty() || args.get(0) == null ? null : args.get(0).toString().trim();
            case "abs":
                return args.isEmpty() || args.get(0) == null ? null : Math.abs(asNumber(args.get(0)).doubleValue());
            case "round":
                if (args.isEmpty() || args.get(0) == null) return null;
                double value = asNumber(args.get(0)).doubleValue();
                if (args.size() >= 2) {
                    int scale = ((Number) args.get(1)).intValue();
                    double factor = Math.pow(10, scale);
                    return Math.round(value * factor) / factor;
                }
                return (double) Math.round(value);
            case "year":
                return extractYear(args.isEmpty() ? null : args.get(0));
            case "month":
                return extractMonth(args.isEmpty() ? null : args.get(0));
            case "day":
                return extractDay(args.isEmpty() ? null : args.get(0));
            case "now":
                return new Date();
            case "current_date":
                return java.time.LocalDate.now();
            default:
                return null;
        }
    }

    private Integer extractYear(Object date) {
        if (date instanceof Date) {
            Calendar cal = Calendar.getInstance();
            cal.setTime((Date) date);
            return cal.get(Calendar.YEAR);
        }
        if (date instanceof java.time.LocalDate) {
            return ((java.time.LocalDate) date).getYear();
        }
        return null;
    }

    private Integer extractMonth(Object date) {
        if (date instanceof Date) {
            Calendar cal = Calendar.getInstance();
            cal.setTime((Date) date);
            return cal.get(Calendar.MONTH) + 1;
        }
        if (date instanceof java.time.LocalDate) {
            return ((java.time.LocalDate) date).getMonthValue();
        }
        return null;
    }

    private Integer extractDay(Object date) {
        if (date instanceof Date) {
            Calendar cal = Calendar.getInstance();
            cal.setTime((Date) date);
            return cal.get(Calendar.DAY_OF_MONTH);
        }
        if (date instanceof java.time.LocalDate) {
            return ((java.time.LocalDate) date).getDayOfMonth();
        }
        return null;
    }

    private Object evaluateBetween(Object value, Object low, Object high, boolean isNot) {
        if (value == null || low == null || high == null) return null;
        boolean result = compareValues(value, low, false) >= 0 && compareValues(value, high, false) <= 0;
        return isNot ? !result : result;
    }

    private boolean likeMatch(String value, String pattern) {
        if (value == null || pattern == null) return false;
        String regex = pattern.replace("%", ".*").replace("_", ".");
        return value.matches(regex);
    }

    private Number asNumber(Object value) {
        if (value instanceof Number) return (Number) value;
        if (value instanceof String) {
            try {
                return Double.parseDouble((String) value);
            } catch (NumberFormatException e) {
                return 0.0;
            }
        }
        return 0.0;
    }

    private int compareValues(Object v1, Object v2, boolean nullsFirst) {
        if (v1 == null && v2 == null) return 0;
        if (v1 == null) return nullsFirst ? -1 : 1;
        if (v2 == null) return nullsFirst ? 1 : -1;
        if (v1 instanceof Comparable && v2 instanceof Comparable) {
            @SuppressWarnings("unchecked")
            int cmp = ((Comparable<Object>) v1).compareTo(v2);
            return cmp;
        }
        return v1.toString().compareTo(v2.toString());
    }

    private Object extractJoinKey(JQuickRow row, JQuickHashJoinPhysicalNode node, boolean isBuildSide) {
        List<JQuickHashJoinPhysicalNode.JoinKeyPair> joinKeys = node.getJoinKeys();
        if (joinKeys.isEmpty()) return null;

        JQuickHashJoinPhysicalNode.JoinKeyPair keyPair = joinKeys.get(0);
        JQuickExpression keyExpr = isBuildSide ? keyPair.getLeftKey() : keyPair.getRightKey();
        return evaluateExpression(row, keyExpr);
    }

    private JQuickRow extractGroupKey(JQuickRow row, List<JQuickExpression> groupKeys) {
        JQuickRow key = new JQuickRow();
        for (JQuickExpression expr : groupKeys) {
            if (expr instanceof JQuickColumnRefExpression) {
                String colName = ((JQuickColumnRefExpression) expr).getColumnName();
                key.put(colName, row.get(colName));
            }
        }
        return key;
    }

    private Object computeAggregate(List<JQuickRow> rows, JQuickHashAggregatePhysicalNode.AggregateFunction agg) {
        String funcName = agg.getFunctionName().toLowerCase();

        switch (funcName) {
            case "count":
                if (agg.isDistinct()) {
                    return rows.stream()
                            .map(r -> evaluateExpression(r, agg.getArgument()))
                            .distinct()
                            .count();
                }
                return (long) rows.size();

            case "sum":
                return rows.stream()
                        .mapToDouble(r -> {
                            Object val = evaluateExpression(r, agg.getArgument());
                            return val instanceof Number ? ((Number) val).doubleValue() : 0.0;
                        })
                        .sum();

            case "avg":
                return rows.stream()
                        .mapToDouble(r -> {
                            Object val = evaluateExpression(r, agg.getArgument());
                            return val instanceof Number ? ((Number) val).doubleValue() : 0.0;
                        })
                        .average()
                        .orElse(0.0);

            case "max":
                return rows.stream()
                        .map(r -> evaluateExpression(r, agg.getArgument()))
                        .max((a, b) -> compareValues(a, b, false))
                        .orElse(null);

            case "min":
                return rows.stream()
                        .map(r -> evaluateExpression(r, agg.getArgument()))
                        .min((a, b) -> compareValues(a, b, false))
                        .orElse(null);

            default:
                return null;
        }
    }

    private Object evaluateWindowFunction(JQuickDataSet data, JQuickRow currentRow, JQuickWindowPhysicalNode.WindowFunction wf) {
        String funcName = wf.getFunctionName().toLowerCase();
        List<JQuickRow> windowRows = data.getRows();

        switch (funcName) {
            case "row_number":
                return (long) (windowRows.indexOf(currentRow) + 1);
            case "rank":
                // 简化实现
                return (long) (windowRows.indexOf(currentRow) + 1);
            case "dense_rank":
                return (long) (windowRows.indexOf(currentRow) + 1);
            case "lead":
                int idx = windowRows.indexOf(currentRow);
                if (idx + 1 < windowRows.size()) {
                    return evaluateExpression(windowRows.get(idx + 1), wf.getArgument());
                }
                return null;
            case "lag":
                idx = windowRows.indexOf(currentRow);
                if (idx - 1 >= 0) {
                    return evaluateExpression(windowRows.get(idx - 1), wf.getArgument());
                }
                return null;
            default:
                return null;
        }
    }

    private JQuickRow joinRows(JQuickRow leftRow, JQuickRow rightRow,
                               com.github.paohaijiao.enums.JQuickJoinType joinType,
                               boolean leftIsBuild) {
        JQuickRow result = new JQuickRow();

        if (leftIsBuild) {
            if (leftRow != null) result.putAll(leftRow);
            if (rightRow != null) result.putAll(rightRow);
        } else {
            if (rightRow != null) result.putAll(rightRow);
            if (leftRow != null) result.putAll(leftRow);
        }

        return result;
    }

    private JQuickDataSet applyFilter(JQuickDataSet data, JQuickExpression predicate) {
        return data.filter(row -> evaluatePredicate(row, predicate));
    }

    // ========== 数据转换方法 ==========

    private JQuickDataSetProto convertToProto(JQuickDataSet data) {
        JQuickDataSetProto.Builder builder = JQuickDataSetProto.newBuilder();

        for (JQuickColumnMeta col : data.getColumns()) {
            builder.addColumns(JQuickColumnMetaProto.newBuilder()
                    .setName(col.getName())
                    .setTypeName(col.getType().getName())
                    .setSource(col.getSource())
                    .build());
        }

        for (JQuickRow row : data.getRows()) {
            JQuickRowProto.Builder rowBuilder = JQuickRowProto.newBuilder();
            for (Map.Entry<String, Object> entry : row.entrySet()) {
                Any anyValue = Any.pack(Value.newBuilder()
                        .setStringValue(entry.getValue() != null ? entry.getValue().toString() : "")
                        .build());
                rowBuilder.putData(entry.getKey(), anyValue);
            }
            builder.addRows(rowBuilder.build());
        }

        builder.setTotalRows(data.size());
        return builder.build();
    }

    private JQuickDataSet convertFromProto(JQuickDataSetProto proto) {
        List<JQuickColumnMeta> columns = new ArrayList<>();
        for (JQuickColumnMetaProto colProto : proto.getColumnsList()) {
            columns.add(new JQuickColumnMeta(colProto.getName(), Object.class, colProto.getSource()));
        }

        List<JQuickRow> rows = new ArrayList<>();
        for (JQuickRowProto rowProto : proto.getRowsList()) {
            JQuickRow row = new JQuickRow();
            for (Map.Entry<String, Any> entry : rowProto.getDataMap().entrySet()) {
                try {
                    Value value = entry.getValue().unpack(Value.class);
                    row.put(entry.getKey(), value.getStringValue());
                } catch (Exception e) {
                    row.put(entry.getKey(), entry.getValue().toString());
                }
            }
            rows.add(row);
        }

        return new JQuickDataSet(columns, rows);
    }

    private JQuickPhysicalPlanNode buildPhysicalNode(JQuickPhysicalPlanNodeProto proto) {
        if (proto == null) return null;

        switch (proto.getNodeCase()) {
            case TABLE_SCAN:
                JQuickTableScanNodeProto scanProto = proto.getTableScan();
                Set<String> requiredColumns = new HashSet<>(scanProto.getRequiredColumnsList());
                JQuickTablePartitionInfo partitionInfo = null;
                if (scanProto.hasPartitionInfo()) {
                    partitionInfo = buildPartitionInfo(scanProto.getPartitionInfo());
                }
                return new JQuickTableScanPhysicalNode(
                        scanProto.getTableName(),
                        scanProto.getAlias(),
                        requiredColumns,
                        null,
                        partitionInfo
                );

            case FILTER:
                JQuickFilterNodeProto filterProto = proto.getFilter();
                return new JQuickFilterPhysicalNode(
                        buildExpression(filterProto.getPredicate()),
                        null
                );

            case PROJECT:
                JQuickProjectNodeProto projectProto = proto.getProject();
                List<JQuickProjectPhysicalNode.SelectItem> selectItems = new ArrayList<>();
                for (JQuickProjectNodeProto.SelectItemProto itemProto : projectProto.getSelectItemsList()) {
                    selectItems.add(new JQuickProjectPhysicalNode.SelectItem(
                            buildExpression(itemProto.getExpression()),
                            itemProto.getAlias()
                    ));
                }
                return new JQuickProjectPhysicalNode(selectItems, null, projectProto.getDistinct());

            case HASH_JOIN:
                JQuickHashJoinNodeProto joinProto = proto.getHashJoin();
                JQuickHashJoinPhysicalNode.BuildSide buildSide = joinProto.getBuildSide() == JQuickBuildSideProto.BUILD_SIDE_LEFT
                        ? JQuickHashJoinPhysicalNode.BuildSide.LEFT
                        : JQuickHashJoinPhysicalNode.BuildSide.RIGHT;
                List<JQuickHashJoinPhysicalNode.JoinKeyPair> joinKeys = new ArrayList<>();
                for (JQuickHashJoinNodeProto.JoinKeyPairProto keyProto : joinProto.getJoinKeysList()) {
                    joinKeys.add(new JQuickHashJoinPhysicalNode.JoinKeyPair(
                            buildExpression(keyProto.getLeftKey()),
                            buildExpression(keyProto.getRightKey())
                    ));
                }
                return new JQuickHashJoinPhysicalNode(
                        convertJoinType(joinProto.getJoinType()),
                        null, null,
                        buildExpression(joinProto.getCondition()),
                        joinKeys,
                        buildSide,
                        convertJoinDistribution(joinProto.getDistribution())
                );

            case HASH_AGGREGATE:
                JQuickHashAggregateNodeProto aggProto = proto.getHashAggregate();
                List<JQuickExpression> groupKeys = new ArrayList<>();
                for (JQuickExpressionProto keyProto : aggProto.getGroupKeysList()) {
                    groupKeys.add(buildExpression(keyProto));
                }
                List<JQuickHashAggregatePhysicalNode.AggregateFunction> aggregates = new ArrayList<>();
                for (JQuickHashAggregateNodeProto.AggregateFunctionProto aggFuncProto : aggProto.getAggregatesList()) {
                    aggregates.add(new JQuickHashAggregatePhysicalNode.AggregateFunction(
                            aggFuncProto.getFunctionName(),
                            buildExpression(aggFuncProto.getArgument()),
                            aggFuncProto.getDistinct(),
                            aggFuncProto.getAlias()
                    ));
                }
                return new JQuickHashAggregatePhysicalNode(
                        groupKeys,
                        aggregates,
                        null,
                        buildExpression(aggProto.getHavingCondition()),
                        convertAggregateStage(aggProto.getStage())
                );

            case SORT:
                JQuickSortNodeProto sortProto = proto.getSort();
                List<JQuickSortPhysicalNode.OrderByItem> orderByItems = new ArrayList<>();
                for (JQuickSortNodeProto.OrderByItemProto itemProto : sortProto.getOrderByItemsList()) {
                    orderByItems.add(new JQuickSortPhysicalNode.OrderByItem(
                            itemProto.getColumnName(),
                            itemProto.getAscending(),
                            itemProto.getNullsFirst()
                    ));
                }
                return new JQuickSortPhysicalNode(orderByItems, null);

            case LIMIT:
                JQuickLimitNodeProto limitProto = proto.getLimit();
                return new JQuickLimitPhysicalNode(limitProto.getLimit(), limitProto.getOffset(), null);

            case EXCHANGE:
                JQuickExchangeNodeProto exchangeProto = proto.getExchange();
                return new JQuickExchangePhysicalNode(
                        convertExchangeType(exchangeProto.getExchangeType()),
                        convertPartitionStrategy(exchangeProto.getPartitionStrategy()),
                        new ArrayList<>(),
                        exchangeProto.getParallelism(),
                        null
                );

            case VALUES:
                JQuickValuesNodeProto valuesProto = proto.getValues();
                List<List<Object>> rows = new ArrayList<>();
                for (JQuickRowProto rowProto : valuesProto.getRowsList()) {
                    List<Object> row = new ArrayList<>();
                    for (Map.Entry<String, Any> entry : rowProto.getDataMap().entrySet()) {
                        row.add(entry.getValue().toString());
                    }
                    rows.add(row);
                }
                return new JQuickValuesPhysicalNode(
                        rows,
                        valuesProto.getColumnNamesList(),
                        new ArrayList<>()
                );

            case EMPTY:
                return JQuickEmptyPhysicalNode.INSTANCE;

            default:
                return null;
        }
    }

    private JQuickExpression buildExpression(JQuickExpressionProto proto) {
        if (proto == null) return null;

        switch (proto.getType()) {
            case EXPR_COLUMN_REF:
                return new JQuickColumnRefExpression(proto.getValue());

            case EXPR_LITERAL:
                return new JQuickLiteralExpression(proto.getValue());

            case EXPR_BINARY_OPERATOR:
                List<JQuickExpression> children = new ArrayList<>();
                for (JQuickExpressionProto child : proto.getChildrenList()) {
                    children.add(buildExpression(child));
                }
                if (children.size() >= 2) {
                    return new JQuickBinaryExpression(
                            children.get(0),
                            children.get(1),
                            convertBinaryOperator(proto.getBinaryOperator())
                    );
                }
                return null;

            case EXPR_UNARY_OPERATOR:
                List<JQuickExpression> unaryChildren = new ArrayList<>();
                for (JQuickExpressionProto child : proto.getChildrenList()) {
                    unaryChildren.add(buildExpression(child));
                }
                if (!unaryChildren.isEmpty()) {
                    return new JQuickUnaryExpression(
                            convertUnaryOperator(proto.getValue()),
                            unaryChildren.get(0)
                    );
                }
                return null;

            case EXPR_FUNCTION:
                List<JQuickExpression> args = new ArrayList<>();
                for (JQuickExpressionProto arg : proto.getArgumentsList()) {
                    args.add(buildExpression(arg));
                }
                return new JQuickFunctionCallExpression(proto.getFunctionName(), args);

            case EXPR_CASE_WHEN:
                List<JQuickExpression> conditions = new ArrayList<>();
                List<JQuickExpression> results = new ArrayList<>();
                for (int i = 0; i < proto.getChildrenList().size(); i += 2) {
                    if (i + 1 < proto.getChildrenList().size()) {
                        conditions.add(buildExpression(proto.getChildrenList().get(i)));
                        results.add(buildExpression(proto.getChildrenList().get(i + 1)));
                    }
                }
                JQuickExpression elseResult = null;
                if (proto.getChildrenList().size() % 2 == 1) {
                    elseResult = buildExpression(proto.getChildrenList().get(proto.getChildrenList().size() - 1));
                }
                return new JQuickCaseWhenExpression(conditions, results, elseResult);

            default:
                return null;
        }
    }


    private JQuickTablePartitionInfo buildPartitionInfo(JQuickTablePartitionInfoProto proto) {
        List<JQuickTablePartitionInfo.Partition> partitions = new ArrayList<>();
        for (JQuickMemoryPartitionProto partitionProto : proto.getPartitionsList()) {
            partitions.add(new JQuickTablePartitionInfo.Partition(
                    partitionProto.getTargetHost(),
                    partitionProto.getEstimatedSize(),
                    0,
                    new HashMap<>()
            ));
        }
        return new JQuickTablePartitionInfo(
                proto.getTableName(),
                partitions,
                proto.getPartitionColumn()
        );
    }

    private com.github.paohaijiao.enums.JQuickJoinType convertJoinType(JQuickJoinTypeProto proto) {
        switch (proto) {
            case JOIN_INNER: return com.github.paohaijiao.enums.JQuickJoinType.INNER;
            case JOIN_LEFT: return com.github.paohaijiao.enums.JQuickJoinType.LEFT;
            case JOIN_RIGHT: return com.github.paohaijiao.enums.JQuickJoinType.RIGHT;
            case JOIN_FULL: return com.github.paohaijiao.enums.JQuickJoinType.FULL;
            case JOIN_CROSS: return com.github.paohaijiao.enums.JQuickJoinType.CROSS;
            case JOIN_SEMI: return com.github.paohaijiao.enums.JQuickJoinType.SEMI;
            case JOIN_ANTI: return com.github.paohaijiao.enums.JQuickJoinType.ANTI;
            default: return com.github.paohaijiao.enums.JQuickJoinType.INNER;
        }
    }

    private JQuickHashJoinPhysicalNode.JoinDistribution convertJoinDistribution(JQuickJoinDistributionProto proto) {
        switch (proto) {
            case JOIN_DIST_LOCAL: return JQuickHashJoinPhysicalNode.JoinDistribution.LOCAL;
            case JOIN_DIST_SHUFFLE: return JQuickHashJoinPhysicalNode.JoinDistribution.SHUFFLE_HASH;
            case JOIN_DIST_BROADCAST: return JQuickHashJoinPhysicalNode.JoinDistribution.BROADCAST_HASH;
            case JOIN_DIST_PARTITIONED: return JQuickHashJoinPhysicalNode.JoinDistribution.PARTITIONED;
            default: return JQuickHashJoinPhysicalNode.JoinDistribution.LOCAL;
        }
    }

    private JQuickHashAggregatePhysicalNode.AggregateStage convertAggregateStage(JQuickAggregateStageProto proto) {
        switch (proto) {
            case AGG_PARTIAL: return JQuickHashAggregatePhysicalNode.AggregateStage.PARTIAL;
            case AGG_FINAL: return JQuickHashAggregatePhysicalNode.AggregateStage.FINAL;
            default: return JQuickHashAggregatePhysicalNode.AggregateStage.SINGLE;
        }
    }

    private JQuickExchangeType convertExchangeType(JQuickExchangeTypeProto proto) {
        switch (proto) {
            case EX_SHUFFLE: return JQuickExchangeType.SHUFFLE;
            case EX_BROADCAST: return JQuickExchangeType.BROADCAST;
            case EX_GATHER: return JQuickExchangeType.GATHER;
            case EX_REPARTITION: return JQuickExchangeType.REPARTITION;
            case EX_PIPELINE: return JQuickExchangeType.PIPELINE;
            default: return JQuickExchangeType.SHUFFLE;
        }
    }

    private JQuickPartitionStrategy convertPartitionStrategy(JQuickPartitionStrategyProto proto) {
        switch (proto) {
            case PARTITION_HASH: return JQuickPartitionStrategy.HASH;
            case PARTITION_RANGE: return JQuickPartitionStrategy.RANGE;
            case PARTITION_ROUND_ROBIN: return JQuickPartitionStrategy.ROUND_ROBIN;
            case PARTITION_BROADCAST: return JQuickPartitionStrategy.BROADCAST;
            case PARTITION_FORWARD: return JQuickPartitionStrategy.FORWARD;
            default: return JQuickPartitionStrategy.HASH;
        }
    }

    private JQuickBinaryOperator convertBinaryOperator(JQuickBinaryOperatorProto proto) {
        switch (proto) {
            case OP_EQ: return JQuickBinaryOperator.EQ;
            case OP_NE: return JQuickBinaryOperator.NE;
            case OP_LT: return JQuickBinaryOperator.LT;
            case OP_LTE: return JQuickBinaryOperator.LE;
            case OP_GT: return JQuickBinaryOperator.GT;
            case OP_GTE: return JQuickBinaryOperator.GE;
            case OP_AND: return JQuickBinaryOperator.AND;
            case OP_OR: return JQuickBinaryOperator.OR;
            case OP_LIKE: return JQuickBinaryOperator.LIKE;
            case OP_PLUS: return JQuickBinaryOperator.PLUS;
            case OP_MINUS: return JQuickBinaryOperator.MINUS;
            case OP_MULTIPLY: return JQuickBinaryOperator.MULTIPLY;
            case OP_DIVIDE: return JQuickBinaryOperator.DIVIDE;
            case OP_MOD: return JQuickBinaryOperator.MOD;
            default: return JQuickBinaryOperator.EQ;
        }
    }

    private com.github.paohaijiao.enums.JQuickUnaryOperator convertUnaryOperator(String value) {
        if ("NOT".equalsIgnoreCase(value)) return com.github.paohaijiao.enums.JQuickUnaryOperator.NOT;
        if ("IS_NULL".equalsIgnoreCase(value)) return com.github.paohaijiao.enums.JQuickUnaryOperator.IS_NULL;
        if ("IS_NOT_NULL".equalsIgnoreCase(value)) return com.github.paohaijiao.enums.JQuickUnaryOperator.IS_NOT_NULL;
        return com.github.paohaijiao.enums.JQuickUnaryOperator.NOT;
    }

    private List<JQuickPhysicalColumn> buildOutputSchema(JQuickPhysicalPlanNode node) {
        if (node == null) return new ArrayList<>();
        return node.getOutputSchema();
    }

    private List<JQuickPhysicalColumn> buildOutputSchemaForProject(JQuickProjectPhysicalNode node) {
        List<JQuickPhysicalColumn> schema = new ArrayList<>();
        for (JQuickProjectPhysicalNode.SelectItem item : node.getSelectItems()) {
            String name = item.getAlias() != null ? item.getAlias() : "expr";
            schema.add(new JQuickPhysicalColumn(name, Object.class, null, true));
        }
        return schema;
    }

    private JQuickDataSet readFromDataSource(String tableName, Set<String> columns) {
        if (tableName == null) {
            return JQuickDataSet.builder().build();
        }

        long rowCount = JQuickDataSourceManager.getRowCount(tableName);
        if (rowCount == 0) {
            return JQuickDataSet.builder().build();
        }

        JQuickDataSet.Builder builder = JQuickDataSet.builder();
        List<String> columnNames = JQuickDataSourceManager.getColumnNames(tableName);

        if (columns != null && !columns.isEmpty()) {
            columnNames = columnNames.stream()
                    .filter(columns::contains)
                    .collect(Collectors.toList());
        }

        for (String colName : columnNames) {
            builder.addColumn(colName, Object.class, tableName);
        }

        // 模拟数据读取（实际应从数据源读取）
        for (int i = 0; i < Math.min(rowCount, 10000); i++) {
            JQuickRow row = new JQuickRow();
            for (String colName : columnNames) {
                row.put(colName, "sample_" + i);
            }
            builder.addRow(row);
        }

        return builder.build();
    }

    private JQuickDataSet readFromMemoryPartition(String partitionId, Set<String> columns) {
        JQuickMemoryPartition partition = memoryPartitions.get(partitionId);
        if (partition == null) {
            return JQuickDataSet.builder().build();
        }
        JQuickDataSet data = partition.getData();
        if (columns == null || columns.isEmpty()) {
            return data;
        }
        return data.select(columns.toArray(new String[0]));
    }

    private void receivePartition(JQuickMemoryPartitionProto partition) {
        JQuickMemoryPartition memPartition = new JQuickMemoryPartition(
                partition.getPartitionIndex(),
                partition.getTotalPartitions()
        );
        memPartition.setData(convertFromProto(partition.getData()));
        memPartition.setChunkIndex(partition.getPartitionIndex());
        memoryPartitions.put(partition.getPartitionId(), memPartition);
    }

    private void sendOutputPartition(JQuickDataSet result, JQuickMemoryPartitionProto outputPartition) {
        JQuickMemoryPartition partition = new JQuickMemoryPartition(
                outputPartition.getPartitionIndex(),
                outputPartition.getTotalPartitions()
        );
        partition.setData(result);
        sendToWorker(partition, 1, JQuickExchangeType.GATHER);
    }

    /**
     * 启动 Worker 服务
     */
    public void start() throws IOException {
        JQuickPhysicalPlanServiceImpl planService = new JQuickPhysicalPlanServiceImpl(this);
        JQuickDataDistributionServiceImpl distributionService = new JQuickDataDistributionServiceImpl(this);

        server = ServerBuilder.forPort(port)
                .addService(planService)
                .addService(distributionService)
                .build()
                .start();

        System.out.println("Worker " + workerId + " started on port " + port);
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

    // ========== 内部类 ==========

    class JQuickTaskContext {
        private final String taskId;
        private final JQuickExecuteTaskRequest request;
        private long processedRows;
        private long startTime;
        private long memoryUsed;

        JQuickTaskContext(String taskId, JQuickExecuteTaskRequest request) {
            this.taskId = taskId;
            this.request = request;
            this.startTime = System.currentTimeMillis();
            this.memoryUsed = request.getMemoryLimitBytes();
        }

        void addProcessedRows(long rows) {
            this.processedRows += rows;
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

    class JQuickMemoryPartition {
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

        void setData(JQuickDataSet data) {
            this.data = data;
        }

        JQuickDataSet getData() {
            return data;
        }

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
}

/**
 * gRPC 服务实现类
 */
class JQuickPhysicalPlanServiceImpl extends JQuickPhysicalPlanServiceGrpc.JQuickPhysicalPlanServiceImplBase {
    private final JQuickWorker worker;

    JQuickPhysicalPlanServiceImpl(JQuickWorker worker) {
        this.worker = worker;
    }

    @Override
    public void executeTask(JQuickExecuteTaskRequest request,
                            StreamObserver<JQuickExecuteTaskResponse> responseObserver) {
        JQuickExecuteTaskResponse response = worker.executeTask(request);
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void executeTaskStream(JQuickExecuteTaskRequest request,
                                  StreamObserver<JQuickDataChunkProto> responseObserver) {
        worker.executeTaskStream(request, responseObserver);
    }

    @Override
    public void cancelTask(JQuickCancelQueryRequest request,
                           StreamObserver<JQuickCancelQueryResponse> responseObserver) {
        JQuickCancelQueryResponse response = JQuickCancelQueryResponse.newBuilder()
                .setQueryId(request.getQueryId())
                .setSuccess(true)
                .setMessage("Task cancelled")
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}

/**
 * 数据分发服务实现类
 */
class JQuickDataDistributionServiceImpl extends JQuickDataDistributionServiceGrpc.JQuickDataDistributionServiceImplBase {
    private final JQuickWorker worker;

    JQuickDataDistributionServiceImpl(JQuickWorker worker) {
        this.worker = worker;
    }

    @Override
    public StreamObserver<JQuickDataChunkProto> sendData(StreamObserver<JQuickEmptyNodeProto> responseObserver) {
        return new StreamObserver<JQuickDataChunkProto>() {
            private final List<JQuickDataChunkProto> receivedChunks = new ArrayList<>();

            @Override
            public void onNext(JQuickDataChunkProto chunk) {
                receivedChunks.add(chunk);
            }

            @Override
            public void onError(Throwable t) {
                responseObserver.onError(t);
            }

            @Override
            public void onCompleted() {
                responseObserver.onNext(JQuickEmptyNodeProto.newBuilder().build());
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public void receiveData(JQuickFetchDataRequest request,
                            StreamObserver<JQuickFetchDataResponse> responseObserver) {
        JQuickFetchDataResponse response = JQuickFetchDataResponse.newBuilder()
                .setPartitionId(request.getPartitionId())
                .setChunkIndex(request.getChunkIndex())
                .setIsLast(true)
                .setData(JQuickDataSetProto.newBuilder().build())
                .setDataSizeBytes(0)
                .setFromMemory(true)
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void broadcastData(StreamObserver<JQuickDataChunkProto> responseObserver,
                              StreamObserver<JQuickBroadcastResponse> responseObserver1) {
        // 广播实现
        responseObserver1.onNext(JQuickBroadcastResponse.newBuilder()
                .setSuccess(true)
                .setSuccessCount(1)
                .build());
        responseObserver1.onCompleted();
    }
}