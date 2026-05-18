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
package com.github.paohaijiao.scheduler;


import com.github.paohaijiao.distributed.JQuickDistributedPlan;
import com.github.paohaijiao.exchange.JQuickExchangeNode;
import com.github.paohaijiao.fragment.JQuickFragment;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 任务调度器 - 将分布式计划转换为可执行任务
 */
public class JQuickTaskScheduler {

    private final JQuickDistributedPlan distributedPlan;
    private final WorkerManager workerManager;
    private final SchedulingStrategy strategy;
    private final Map<Long, List<JQuickTask>> fragmentTasksMap;
    private final Map<Long, JQuickTask> taskMap;

    public enum SchedulingStrategy {
        ROUND_ROBIN,    // 轮询
        LEAST_LOAD,     // 最少负载
        DATA_LOCALITY   // 数据本地性
    }

    public JQuickTaskScheduler(JQuickDistributedPlan distributedPlan, WorkerManager workerManager) {
        this(distributedPlan, workerManager, SchedulingStrategy.DATA_LOCALITY);
    }

    public JQuickTaskScheduler(JQuickDistributedPlan distributedPlan,
                               WorkerManager workerManager,
                               SchedulingStrategy strategy) {
        this.distributedPlan = distributedPlan;
        this.workerManager = workerManager;
        this.strategy = strategy;
        this.fragmentTasksMap = new HashMap<>();
        this.taskMap = new ConcurrentHashMap<>();
    }

    /**
     * 将分布式计划转换为调度计划
     */
    public JQuickSchedulePlan schedule() {
        // 1. 获取执行顺序
        List<JQuickFragment> executionOrder = distributedPlan.getExecutionOrder();

        // 2. 为每个 Fragment 创建 Tasks
        for (JQuickFragment fragment : executionOrder) {
            createTasksForFragment(fragment);
        }

        // 3. 建立 Task 之间的数据依赖关系
        buildTaskDependencies();

        // 4. 分配 Worker
        assignWorkers();

        // 5. 创建调度计划
        return new JQuickSchedulePlan(
                taskMap.values(),
                getRootTasks(),
                getStageDependencies(),
                workerManager.getWorkers()
        );
    }

    /**
     * 为 Fragment 创建 Task
     */
    private void createTasksForFragment(JQuickFragment fragment) {
        int parallelism = fragment.getParallelism();
        List<JQuickTask> tasks = new ArrayList<>();

        JQuickTask.TaskType taskType = convertTaskType(fragment.getType());

        for (int i = 0; i < parallelism; i++) {
            JQuickTask task = new JQuickTask(
                    fragment.getFragmentId(),
                    i,
                    fragment,
                    taskType
            );
            tasks.add(task);
            taskMap.put(task.getTaskId(), task);
        }

        fragmentTasksMap.put(fragment.getFragmentId(), tasks);
    }

    /**
     * 转换 Task 类型
     */
    private JQuickTask.TaskType convertTaskType(JQuickFragment.FragmentType fragmentType) {
        switch (fragmentType) {
            case SOURCE:
                return JQuickTask.TaskType.SOURCE_TASK;
            case SINK:
                return JQuickTask.TaskType.SINK_TASK;
            default:
                return JQuickTask.TaskType.PROCESS_TASK;
        }
    }

    /**
     * 建立 Task 之间的依赖关系
     */
    private void buildTaskDependencies() {
        for (JQuickFragment fragment : distributedPlan.getAllFragments()) {
            List<JQuickTask> tasks = fragmentTasksMap.get(fragment.getFragmentId());
            if (tasks == null || tasks.isEmpty()) continue;

            // 处理输入依赖
            for (JQuickExchangeNode input : fragment.getInputs()) {
                // 找到产生这个输入的源 Fragment
                JQuickFragment sourceFragment = findSourceFragmentForInput(input);
                if (sourceFragment != null) {
                    List<JQuickTask> sourceTasks = fragmentTasksMap.get(sourceFragment.getFragmentId());
                    if (sourceTasks != null) {
                        // 建立 Shuffle 连接
                        buildShuffleDependencies(sourceTasks, tasks, input);
                    }
                }
            }
        }
    }

    /**
     * 建立 Shuffle 依赖（M:N 连接）
     */
    private void buildShuffleDependencies(List<JQuickTask> sourceTasks,
                                          List<JQuickTask> targetTasks,
                                          JQuickExchangeNode exchange) {
        for (JQuickTask targetTask : targetTasks) {
            for (JQuickTask sourceTask : sourceTasks) {
                // 根据分区策略确定是否建立连接
                if (shouldConnect(sourceTask, targetTask, exchange)) {
                    JQuickExchangeChannel channel = createExchangeChannel(
                            sourceTask, targetTask, exchange
                    );

                    JQuickTaskInput input = new JQuickTaskInput(
                            "input_" + sourceTask.getTaskId() + "_" + targetTask.getTaskId(),
                            sourceTask.getTaskId(),
                            channel,
                            getInputType(exchange)
                    );
                    targetTask.addInput(input);

                    // 添加输出
                    if (targetTask.getOutput() == null) {
                        targetTask.setOutput(new JQuickTaskOutput(
                                "output_" + targetTask.getTaskId(),
                                getOutputType(exchange)
                        ));
                    }
                    targetTask.getOutput().addChannel(channel);
                }
            }
        }
    }

    /**
     * 判断是否应该建立连接
     */
    private boolean shouldConnect(JQuickTask sourceTask, JQuickTask targetTask, JQuickExchangeNode exchange) {
        JQuickExchangeNode.PartitionStrategy strategy = exchange.getPartitionStrategy();

        switch (strategy) {
            case HASH:
                // Hash 分区：根据分区键的 hash 值决定目标 task
                return getHashPartition(sourceTask.getTaskIndex(), targetTask.getTaskIndex(), exchange);

            case ROUND_ROBIN:
                // Round Robin：轮询分配
                return targetTask.getTaskIndex() ==
                        (sourceTask.getTaskIndex() % targetTask.getFragment().getParallelism());

            case REPLICATE:
                // 广播：所有 source 连接所有 target
                return true;

            default:
                return true;
        }
    }

    /**
     * Hash 分区逻辑
     */
    private boolean getHashPartition(int sourceIndex, int targetIndex, JQuickExchangeNode exchange) {
        // 简化的 hash 分区：sourceIndex % targetCount == targetIndex
        int targetCount = exchange.getParallelism();
        return (sourceIndex % targetCount) == targetIndex;
    }

    /**
     * 为输入找到源 Fragment
     */
    private JQuickFragment findSourceFragmentForInput(JQuickExchangeNode input) {
        for (JQuickFragment fragment : distributedPlan.getAllFragments()) {
            JQuickExchangeNode output = fragment.getOutput();
            if (output != null && input.getExchangeId().equals(output.getExchangeId())) {
                return fragment;
            }
        }
        return null;
    }

    /**
     * 创建交换通道
     */
    private JQuickExchangeChannel createExchangeChannel(JQuickTask source,
                                                        JQuickTask target,
                                                        JQuickExchangeNode exchange) {
        String channelId = String.format("channel_%d_%d", source.getTaskId(), target.getTaskId());
        String sourceWorker = source.getAssignedWorker();
        String targetWorker = target.getAssignedWorker();

        // 获取 Worker 地址信息
        WorkerInfo sourceInfo = workerManager.getWorker(sourceWorker);
        WorkerInfo targetInfo = workerManager.getWorker(targetWorker);

        JQuickExchangeChannel.ChannelType channelType =
                sourceWorker != null && sourceWorker.equals(targetWorker) ?
                        JQuickExchangeChannel.ChannelType.MEMORY :
                        JQuickExchangeChannel.ChannelType.NETTY;

        return new JQuickExchangeChannel(
                channelId,
                sourceWorker,
                sourceInfo != null ? sourceInfo.getDataPort() : 0,
                targetWorker,
                targetInfo != null ? targetInfo.getDataPort() : 0,
                channelType
        );
    }

    private JQuickTaskInput.InputType getInputType(JQuickExchangeNode exchange) {
        switch (exchange.getType()) {
            case BROADCAST:
                return JQuickTaskInput.InputType.BROADCAST;
            case SHUFFLE:
                return JQuickTaskInput.InputType.SHUFFLE;
            default:
                return JQuickTaskInput.InputType.REMOTE;
        }
    }

    private JQuickTaskOutput.OutputType getOutputType(JQuickExchangeNode exchange) {
        switch (exchange.getType()) {
            case BROADCAST:
                return JQuickTaskOutput.OutputType.BROADCAST;
            case SHUFFLE:
                return JQuickTaskOutput.OutputType.SHUFFLE;
            case GATHER:
                return JQuickTaskOutput.OutputType.COLLECT;
            default:
                return JQuickTaskOutput.OutputType.LOCAL;
        }
    }

    /**
     * 分配 Worker
     */
    private void assignWorkers() {
        List<WorkerInfo> workers = workerManager.getAvailableWorkers();

        for (List<JQuickTask> tasks : fragmentTasksMap.values()) {
            for (JQuickTask task : tasks) {
                WorkerInfo assigned = selectWorker(task, workers);
                if (assigned != null) {
                    task.setAssignedWorker(assigned.getWorkerId());
                }
            }
        }
    }

    /**
     * 选择 Worker
     */
    private WorkerInfo selectWorker(JQuickTask task, List<WorkerInfo> workers) {
        if (workers.isEmpty()) return null;

        switch (strategy) {
            case ROUND_ROBIN:
                return workers.get((int) (task.getTaskId() % workers.size()));

            case LEAST_LOAD:
                return workers.stream()
                        .min(Comparator.comparingInt(WorkerInfo::getRunningTasks))
                        .orElse(workers.get(0));

            case DATA_LOCALITY:
                // 对于 SOURCE 任务，优先分配到数据所在节点
                if (task.getType() == JQuickTask.TaskType.SOURCE_TASK) {
                    String localWorker = findDataLocalWorker(task);
                    if (localWorker != null) {
                        return workers.stream()
                                .filter(w -> w.getWorkerId().equals(localWorker))
                                .findFirst()
                                .orElse(workers.get(0));
                    }
                }
                return workers.get((int) (task.getTaskId() % workers.size()));

            default:
                return workers.get(0);
        }
    }

    /**
     * 查找数据本地性 Worker
     */
    private String findDataLocalWorker(JQuickTask task) {
        // 从 Fragment 的 TableScan 中获取表的分区信息
        // 简化的实现：返回第一个 Worker
        List<WorkerInfo> workers = workerManager.getAvailableWorkers();
        return workers.isEmpty() ? null : workers.get(0).getWorkerId();
    }

    /**
     * 获取根任务（SINK 类型）
     */
    private List<JQuickTask> getRootTasks() {
        JQuickFragment root = distributedPlan.getRootFragment();
        return fragmentTasksMap.getOrDefault(root.getFragmentId(), Collections.emptyList());
    }

    /**
     * 获取 Stage 依赖关系
     */
    private Map<Integer, List<Integer>> getStageDependencies() {
        Map<Integer, List<Integer>> dependencies = new HashMap<>();
        // 根据执行顺序构建 stage 依赖
        List<JQuickFragment> order = distributedPlan.getExecutionOrder();
        for (int i = 0; i < order.size(); i++) {
            List<Integer> deps = new ArrayList<>();
            for (JQuickFragment child : order.get(i).getChildren()) {
                int childIndex = order.indexOf(child);
                if (childIndex >= 0) {
                    deps.add(childIndex);
                }
            }
            dependencies.put(i, deps);
        }
        return dependencies;
    }
}
