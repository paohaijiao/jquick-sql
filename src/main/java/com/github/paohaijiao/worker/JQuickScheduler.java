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
package com.github.paohaijiao.worker;

import com.github.paohaijiao.distributed.domain.*;
import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.physical.JQuickPhysicalPlanNode;
import com.github.paohaijiao.physical.domain.JQuickPhysicalColumn;
import com.github.paohaijiao.physical.domain.JQuickTablePartitionInfo;
import com.github.paohaijiao.physical.node.*;
import com.github.paohaijiao.toplogy.JQuickClusterTopology;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 调度器 - 将物理计划转换为分布式执行计划
 */
public class JQuickScheduler {

    private final JQuickClusterTopology cluster;

    private final TaskPlanner taskPlanner;

    private final CostEstimator costEstimator;

    private final AtomicLong taskIdGenerator;

    private final Map<String, ExecutionStage> stageCache;

    public JQuickScheduler(JQuickClusterTopology cluster) {
        this.cluster = cluster;
        this.taskPlanner = new TaskPlanner(cluster);
        this.costEstimator = new CostEstimator(cluster);
        this.taskIdGenerator = new AtomicLong(0);
        this.stageCache = new ConcurrentHashMap<>();
    }

    /**
     * 将物理计划调度为执行计划
     */
    public JQuickExecutionPlan schedule(JQuickPhysicalPlanNode physicalPlan) {
        // 分析计划，生成执行Stage
        List<ExecutionStage> stages = buildStages(physicalPlan);
        // 计算Stage依赖关系
        Map<Integer, List<Integer>> dependencies = calculateDependencies(stages);
        //为每个Stage分配并行度和Worker
        for (ExecutionStage stage : stages) {
            assignParallelism(stage);
            assignWorkers(stage);
        }
        //生成Task列表
        List<JQuickTask> tasks = generateTasks(stages);
        //构建执行计划
        return new JQuickExecutionPlan(tasks, getOutputSchema(physicalPlan), stages, dependencies);
    }

    /**
     * 构建执行Stage（一个Stage包含可以并行执行的Task）
     * Exchange节点作为Stage边界
     */
    private List<ExecutionStage> buildStages(JQuickPhysicalPlanNode node) {
        List<ExecutionStage> stages = new ArrayList<>();
        buildStagesRecursive(node, stages, 0);
        //按依赖关系排序（叶子节点在前）
        stages.sort(Comparator.comparingInt(ExecutionStage::getStageId));
        return stages;
    }

    private int buildStagesRecursive(JQuickPhysicalPlanNode node, List<ExecutionStage> stages, int parentStageId) {
        if (node instanceof JQuickExchangePhysicalNode) {
            // Exchange节点切分Stage边界
            JQuickExchangePhysicalNode exchange = (JQuickExchangePhysicalNode) node;
            int childStageId = buildStagesRecursive(exchange.getChild(), stages, -1);
            int currentStageId = stages.size();
            ExecutionStage stage = new ExecutionStage(currentStageId, exchange);
            stage.addInputStage(childStageId);
            stage.setExchangeType(exchange.getExchangeType());
            stage.setPartitionStrategy(exchange.getPartitionStrategy());
            stage.setPartitionKeys(exchange.getPartitionKeys());
            stage.setTargetParallelism(exchange.getTargetParallelism());
            stages.add(stage);
            return currentStageId;
        }
        // 非Exchange节点，继续递归
        if (node.getChildren().isEmpty()) {
            int stageId = stages.size();
            ExecutionStage stage = new ExecutionStage(stageId, node);
            stage.setParentStageId(parentStageId);
            stages.add(stage);
            return stageId;
        }

        // 收集子Stage
        List<Integer> childStageIds = new ArrayList<>();
        for (JQuickPhysicalPlanNode child : node.getChildren()) {
            childStageIds.add(buildStagesRecursive(child, stages, parentStageId));
        }

        if (childStageIds.size() == 1 && parentStageId >= 0) {
            // 单一子节点，可以合并到同一个Stage
            ExecutionStage stage = stages.get(childStageIds.get(0));
            stage.setRootNode(node);
            return childStageIds.get(0);
        }

        // 多子节点（如Join），需要新的Stage
        int stageId = stages.size();
        ExecutionStage stage = new ExecutionStage(stageId, node);
        stage.setInputStageIds(childStageIds);
        stage.setParentStageId(parentStageId);
        stages.add(stage);
        return stageId;
    }

    /**
     * 计算Stage之间的依赖关系
     */
    private Map<Integer, List<Integer>> calculateDependencies(List<ExecutionStage> stages) {
        Map<Integer, List<Integer>> dependencies = new HashMap<>();
        for (ExecutionStage stage : stages) {
            List<Integer> deps = new ArrayList<>();
            // 添加输入Stage依赖
            for (Integer inputStageId : stage.getInputStageIds()) {
                if (inputStageId >= 0) {
                    deps.add(inputStageId);
                }
            }
            // 对于Join节点，需要等待左右两个Stage完成
            if (stage.getRootNode() instanceof JQuickHashJoinPhysicalNode || stage.getRootNode() instanceof JQuickNestedLoopJoinPhysicalNode) {
                // 子Stage已经在inputStageIds中
            }
            dependencies.put(stage.getStageId(), deps);
        }

        return dependencies;
    }

    /**
     * 分配并行度
     */
    private void assignParallelism(ExecutionStage stage) {
        JQuickPhysicalPlanNode node = stage.getRootNode();
        int parallelism;
        if (stage.getTargetParallelism() > 0) {
            // 使用Exchange指定的并行度
            parallelism = stage.getTargetParallelism();
        } else if (node instanceof JQuickTableScanPhysicalNode) {
            // 表扫描：根据分区数决定并行度
            JQuickTableScanPhysicalNode scan = (JQuickTableScanPhysicalNode) node;
            JQuickTablePartitionInfo partitions = scan.getPartitionInfo();
            if (partitions != null && partitions.getTotalPartitions() > 0) {
                parallelism = Math.min(partitions.getTotalPartitions(), cluster.getConfig().getDefaultParallelism() * 2);
            } else {
                parallelism = cluster.getConfig().getDefaultParallelism();
            }
        } else if (node instanceof JQuickExchangePhysicalNode) {
            // Exchange：使用目标并行度
            parallelism = ((JQuickExchangePhysicalNode) node).getTargetParallelism();
        } else {
            // 其他算子：使用默认并行度
            parallelism = cluster.getConfig().getDefaultParallelism();
        }
        // 限制最大并行度
        int maxParallelism = cluster.getAvailableWorkers() * cluster.getConfig().getMaxConcurrentTasksPerWorker();
        parallelism = Math.min(parallelism, maxParallelism);
        parallelism = Math.max(1, parallelism);
        stage.setParallelism(parallelism);
    }

    /**
     * 为Stage分配Worker
     */
    private void assignWorkers(ExecutionStage stage) {
        JQuickPhysicalPlanNode node = stage.getRootNode();
        int parallelism = stage.getParallelism();
        List<JQuickWorkerAssignment> assignments;
        if (node instanceof JQuickTableScanPhysicalNode) {
            JQuickTableScanPhysicalNode scan = (JQuickTableScanPhysicalNode) node;
            assignments = assignScanWorkers(scan, parallelism);
        } else if (node instanceof JQuickExchangePhysicalNode) {
            JQuickExchangePhysicalNode exchange = (JQuickExchangePhysicalNode) node;
            assignments = assignExchangeWorkers(exchange, parallelism);
        } else if (node instanceof JQuickHashJoinPhysicalNode) {
            JQuickHashJoinPhysicalNode join = (JQuickHashJoinPhysicalNode) node;
            assignments = assignJoinWorkers(join, parallelism, stage);
        } else if (node instanceof JQuickHashAggregatePhysicalNode) {
            JQuickHashAggregatePhysicalNode agg = (JQuickHashAggregatePhysicalNode) node;
            assignments = assignAggregateWorkers(agg, parallelism, stage);
        } else {
            assignments = assignRoundRobinWorkers(parallelism, stage);
        }

        stage.setWorkerAssignments(assignments);
    }

    /**
     * 为表扫描分配Worker（数据本地性优先）
     */
    private List<JQuickWorkerAssignment> assignScanWorkers(JQuickTableScanPhysicalNode scan, int parallelism) {
        List<JQuickWorkerAssignment> assignments = new ArrayList<>();
        JQuickTablePartitionInfo partitions = scan.getPartitionInfo();
        if (partitions != null && partitions.getTotalPartitions() > 0) {
            // 为每个分区分配Worker
            for (int i = 0; i < Math.min(parallelism, partitions.getTotalPartitions()); i++) {
                JQuickTablePartitionInfo.Partition partition = partitions.getPartitions().get(i);
                String preferredWorker = cluster.getWorkerForLocation(partition.getLocation());
                Map<String, Object> context = new HashMap<>();
                context.put("partition", partition);
                context.put("tableName", scan.getTableName());
                context.put("alias", scan.getAlias());
                context.put("requiredColumns", scan.getRequiredColumns());
                context.put("filterPredicate", scan.getFilterPredicate());
                assignments.add(new JQuickWorkerAssignment(i, preferredWorker, context));
            }
            // 如果分区数少于并行度，剩余的任务循环使用Worker
            for (int i = partitions.getTotalPartitions(); i < parallelism; i++) {
                String worker = cluster.getWorkerByIndex(i);
                Map<String, Object> context = new HashMap<>();
                context.put("tableName", scan.getTableName());
                context.put("alias", scan.getAlias());
                context.put("requiredColumns", scan.getRequiredColumns());
                context.put("filterPredicate", scan.getFilterPredicate());
                context.put("isDummyTask", true);
                assignments.add(new JQuickWorkerAssignment(i, worker, context));
            }
        } else {
            // 无分区信息，轮询分配
            for (int i = 0; i < parallelism; i++) {
                String worker = cluster.getWorkerByIndex(i);
                Map<String, Object> context = new HashMap<>();
                context.put("tableName", scan.getTableName());
                context.put("alias", scan.getAlias());
                context.put("requiredColumns", scan.getRequiredColumns());
                context.put("filterPredicate", scan.getFilterPredicate());
                assignments.add(new JQuickWorkerAssignment(i, worker, context));
            }
        }

        return assignments;
    }

    /**
     * 为Exchange分配Worker
     */
    private List<JQuickWorkerAssignment> assignExchangeWorkers(JQuickExchangePhysicalNode exchange, int parallelism) {
        List<JQuickWorkerAssignment> assignments = new ArrayList<>();
        for (int i = 0; i < parallelism; i++) {
            String worker = cluster.getWorkerByIndex(i);
            Map<String, Object> context = new HashMap<>();
            context.put("partitionId", i);
            context.put("exchangeType", exchange.getExchangeType());
            context.put("partitionStrategy", exchange.getPartitionStrategy());
            context.put("partitionKeys", exchange.getPartitionKeys());
            context.put("targetParallelism", exchange.getTargetParallelism());
            assignments.add(new JQuickWorkerAssignment(i, worker, context));
        }

        return assignments;
    }

    /**
     * 为Join分配Worker
     */
    private List<JQuickWorkerAssignment> assignJoinWorkers(JQuickHashJoinPhysicalNode join, int parallelism, ExecutionStage stage) {
        List<JQuickWorkerAssignment> assignments = new ArrayList<>();
        // 获取输入Stage的分配信息
        List<Integer> inputStageIds = stage.getInputStageIds();
        List<List<JQuickWorkerAssignment>> inputAssignments = new ArrayList<>();
        for (int stageId : inputStageIds) {
            ExecutionStage inputStage = stageCache.get(String.valueOf(stageId));
            if (inputStage != null) {
                inputAssignments.add(inputStage.getWorkerAssignments());
            }
        }
        // 确定Co-location策略
        if (canCoLocate(inputAssignments, parallelism)) {
            // 如果输入Stage已经按照Join键分区，可以本地Join
            assignments = createCoLocatedAssignments(join, parallelism, inputAssignments);
        } else {
            // 否则需要Shuffle
            for (int i = 0; i < parallelism; i++) {
                String worker = cluster.getWorkerByIndex(i);
                Map<String, Object> context = new HashMap<>();
                context.put("joinType", join.getJoinType());
                context.put("joinKeys", join.getJoinKeys());
                context.put("buildSide", join.getBuildSide());
                context.put("partitionId", i);
                assignments.add(new JQuickWorkerAssignment(i, worker, context));
            }
        }

        return assignments;
    }

    /**
     * 为聚合分配Worker
     */
    private List<JQuickWorkerAssignment> assignAggregateWorkers(JQuickHashAggregatePhysicalNode agg, int parallelism, ExecutionStage stage) {
        List<JQuickWorkerAssignment> assignments = new ArrayList<>();
        if (agg.getStage() == JQuickHashAggregatePhysicalNode.AggregateStage.PARTIAL) {
            // 部分聚合：在数据所在节点执行
            List<Integer> inputStageIds = stage.getInputStageIds();
            if (!inputStageIds.isEmpty()) {
                ExecutionStage inputStage = stageCache.get(String.valueOf(inputStageIds.get(0)));
                if (inputStage != null) {
                    List<JQuickWorkerAssignment> inputAssignments = inputStage.getWorkerAssignments();
                    Map<String, Integer> workerCount = new HashMap<>();
                    for (JQuickWorkerAssignment assignment : inputAssignments) {
                        int count = workerCount.getOrDefault(assignment.getWorkerId(), 0);
                        if (count < cluster.getConfig().getMaxConcurrentTasksPerWorker()) {
                            Map<String, Object> context = new HashMap<>();
                            context.put("groupKeys", agg.getGroupKeys());
                            context.put("aggregates", agg.getAggregates());
                            context.put("stage", "PARTIAL");
                            context.put("originalAssignment", assignment);
                            assignments.add(new JQuickWorkerAssignment(assignments.size(), assignment.getWorkerId(), context));
                            workerCount.put(assignment.getWorkerId(), count + 1);
                        }
                    }
                }
            }
            // 如果没有足够的分配，补充轮询分配
            if (assignments.isEmpty()) {
                assignments = assignRoundRobinWorkers(parallelism, stage);
            }
        } else {
            // 最终聚合：汇聚到少量节点
            int finalParallelism = Math.min(parallelism, 4); // 最终聚合使用较少的并行度
            for (int i = 0; i < finalParallelism; i++) {
                String worker = cluster.getWorkerByIndex(i);
                Map<String, Object> context = new HashMap<>();
                context.put("groupKeys", agg.getGroupKeys());
                context.put("aggregates", agg.getAggregates());
                context.put("havingCondition", agg.getHavingCondition());
                context.put("stage", "FINAL");
                context.put("partitionId", i);
                assignments.add(new JQuickWorkerAssignment(i, worker, context));
            }
        }

        return assignments;
    }

    /**
     * 轮询分配Worker
     */
    private List<JQuickWorkerAssignment> assignRoundRobinWorkers(int parallelism, ExecutionStage stage) {
        List<JQuickWorkerAssignment> assignments = new ArrayList<>();
        for (int i = 0; i < parallelism; i++) {
            String worker = cluster.getWorkerByIndex(i);
            Map<String, Object> context = new HashMap<>();
            context.put("taskIndex", i);
            context.put("rootNode", stage.getRootNode());
            assignments.add(new JQuickWorkerAssignment(i, worker, context));
        }
        return assignments;
    }

    /**
     * 判断是否可以Co-locate（本地Join）
     */
    private boolean canCoLocate(List<List<JQuickWorkerAssignment>> inputAssignments, int parallelism) {
        if (inputAssignments.size() != 2) return false;
        List<JQuickWorkerAssignment> leftAssignments = inputAssignments.get(0);
        List<JQuickWorkerAssignment> rightAssignments = inputAssignments.get(1);
        if (leftAssignments.size() != rightAssignments.size()) return false;
        for (int i = 0; i < leftAssignments.size(); i++) {
            if (!leftAssignments.get(i).getWorkerId().equals(rightAssignments.get(i).getWorkerId())) {
                return false;
            }
        }

        return true;
    }

    /**
     * 创建Co-located分配
     */
    private List<JQuickWorkerAssignment> createCoLocatedAssignments(JQuickHashJoinPhysicalNode join, int parallelism, List<List<JQuickWorkerAssignment>> inputAssignments) {
        List<JQuickWorkerAssignment> assignments = new ArrayList<>();
        List<JQuickWorkerAssignment> leftAssignments = inputAssignments.get(0);
        for (int i = 0; i < Math.min(parallelism, leftAssignments.size()); i++) {
            JQuickWorkerAssignment leftAssign = leftAssignments.get(i);
            Map<String, Object> context = new HashMap<>();
            context.put("joinType", join.getJoinType());
            context.put("joinKeys", join.getJoinKeys());
            context.put("buildSide", join.getBuildSide());
            context.put("isCoLocated", true);
            context.put("leftContext", leftAssign.getContext());
            assignments.add(new JQuickWorkerAssignment(i, leftAssign.getWorkerId(), context));
        }
        return assignments;
    }

    /**
     * 生成Task
     */
    private List<JQuickTask> generateTasks(List<ExecutionStage> stages) {
        List<JQuickTask> tasks = new ArrayList<>();
        // 缓存Stage信息供后续使用
        for (ExecutionStage stage : stages) {
            stageCache.put(String.valueOf(stage.getStageId()), stage);
        }

        for (ExecutionStage stage : stages) {
            JQuickPhysicalPlanNode node = stage.getRootNode();
            List<JQuickWorkerAssignment> assignments = stage.getWorkerAssignments();
            for (JQuickWorkerAssignment assignment : assignments) {
                String taskId = generateTaskId(stage.getStageId(), assignment.getTaskIndex());
                // 深拷贝物理计划节点，避免多个Task共享同一个节点
                JQuickPhysicalPlanNode taskPlan = cloneForTask(node, stage, assignment);
                JQuickTask task = new JQuickTask(taskId, stage.getStageId(), assignment.getWorkerId(), taskPlan, assignment.getContext(), stage.getInputStageIds(), stage.getExchangeType(), stage.getPartitionStrategy());
                tasks.add(task);
            }
        }

        return tasks;
    }

    /**
     * 为Task克隆物理计划节点
     */
    private JQuickPhysicalPlanNode cloneForTask(JQuickPhysicalPlanNode node, ExecutionStage stage, JQuickWorkerAssignment assignment) {
        JQuickPhysicalPlanNode cloned = node.clone();
        // 如果是表扫描，注入分区信息
        if (cloned instanceof JQuickTableScanPhysicalNode) {
            Map<String, Object> context = assignment.getContext();
            JQuickTablePartitionInfo.Partition partition = (JQuickTablePartitionInfo.Partition) context.get("partition");
            if (partition != null) {
                JQuickTablePartitionInfo partitionInfo = new JQuickTablePartitionInfo((String) context.get("tableName"), Collections.singletonList(partition), null);
                return new JQuickTableScanPhysicalNode((String) context.get("tableName"), (String) context.get("alias"), (Set<String>) context.get("requiredColumns"), (JQuickExpression) context.get("filterPredicate"), partitionInfo);
            }
        }

        return cloned;
    }

    /**
     * 生成Task ID
     */
    private String generateTaskId(int stageId, int taskIndex) {
        return "task_" + stageId + "_" + taskIndex + "_" + taskIdGenerator.incrementAndGet();
    }

    /**
     * 获取输出Schema
     */
    private List<JQuickPhysicalColumn> getOutputSchema(JQuickPhysicalPlanNode physicalPlan) {
        return physicalPlan.getOutputSchema();
    }

    /**
     * 获取集群拓扑
     */
    public JQuickClusterTopology getCluster() {
        return cluster;
    }

    /**
     * 获取成本估算器
     */
    public CostEstimator getCostEstimator() {
        return costEstimator;
    }
}
