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

import com.github.paohaijiao.distributed.domain.JQuickTask;
import com.github.paohaijiao.distributed.domain.TaskResult;
import com.github.paohaijiao.distributed.domain.WorkerManagerConfig;
import com.github.paohaijiao.toplogy.JQuickClusterTopology;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * Worker管理器 - 负责Worker节点的注册、健康检查、负载管理和任务分发
 */
public class JQuickWorkerManager {

    private final JQuickClusterTopology cluster;
    private final Map<String, JQuickWorkerNode> workers;
    private final Map<String, WorkerStatus> workerStatus;
    private final Map<String, List<JQuickTask>> pendingTasks;
    private final Map<String, Set<String>> runningTasks;
    private final ExecutorService healthCheckExecutor;
    private final ScheduledExecutorService scheduler;
    private final AtomicBoolean running;
    private final AtomicLong totalTasksSubmitted;
    private final AtomicLong totalTasksCompleted;
    private final AtomicLong totalTasksFailed;

    private WorkerEventListener eventListener;
    private int maxRetries;
    private long taskTimeoutMs;

    /**
     * Worker状态
     */
    public static class WorkerStatus {
        private final String workerId;
        private volatile boolean healthy;
        private volatile long lastHeartbeat;
        private volatile int currentLoad;
        private volatile int maxLoad;
        private volatile long completedTasks;
        private volatile long failedTasks;
        private final List<String> recentErrors;

        public WorkerStatus(String workerId) {
            this.workerId = workerId;
            this.healthy = true;
            this.lastHeartbeat = System.currentTimeMillis();
            this.currentLoad = 0;
            this.maxLoad = 4;
            this.completedTasks = 0;
            this.failedTasks = 0;
            this.recentErrors = new ArrayList<>();
        }

        public String getWorkerId() { return workerId; }
        public boolean isHealthy() { return healthy; }
        public void setHealthy(boolean healthy) { this.healthy = healthy; }

        public long getLastHeartbeat() { return lastHeartbeat; }
        public void updateHeartbeat() { this.lastHeartbeat = System.currentTimeMillis(); }

        public int getCurrentLoad() { return currentLoad; }
        public synchronized void incrementLoad() { currentLoad++; }
        public synchronized void decrementLoad() { currentLoad--; }

        public int getMaxLoad() { return maxLoad; }
        public void setMaxLoad(int maxLoad) { this.maxLoad = maxLoad; }

        public boolean canAcceptTask() {
            return healthy && currentLoad < maxLoad;
        }

        public long getCompletedTasks() { return completedTasks; }
        public synchronized void incrementCompletedTasks() { completedTasks++; }

        public long getFailedTasks() { return failedTasks; }
        public synchronized void incrementFailedTasks() { failedTasks++; }

        public synchronized void addError(String error) {
            recentErrors.add(error);
            while (recentErrors.size() > 100) {
                recentErrors.remove(0);
            }
        }

        public List<String> getRecentErrors() { return new ArrayList<>(recentErrors); }

        @Override
        public String toString() {
            return String.format("WorkerStatus{id='%s', healthy=%s, load=%d/%d, completed=%d, failed=%d}",
                    workerId, healthy, currentLoad, maxLoad, completedTasks, failedTasks);
        }
    }

    /**
     * Worker事件监听器
     */
    public interface WorkerEventListener {
        void onWorkerRegistered(String workerId);
        void onWorkerUnregistered(String workerId);
        void onWorkerHealthy(String workerId);
        void onWorkerUnhealthy(String workerId);
        void onTaskCompleted(String workerId, String taskId, long durationMs);
        void onTaskFailed(String workerId, String taskId, Throwable error);
    }

    public JQuickWorkerManager(JQuickClusterTopology cluster) {
        this(cluster, new WorkerManagerConfig());
    }

    public JQuickWorkerManager(JQuickClusterTopology cluster, WorkerManagerConfig config) {
        this.cluster = cluster;
        this.workers = new ConcurrentHashMap<>();
        this.workerStatus = new ConcurrentHashMap<>();
        this.pendingTasks = new ConcurrentHashMap<>();
        this.runningTasks = new ConcurrentHashMap<>();
        this.healthCheckExecutor = Executors.newCachedThreadPool();
        this.scheduler = Executors.newScheduledThreadPool(2);
        this.running = new AtomicBoolean(true);
        this.totalTasksSubmitted = new AtomicLong(0);
        this.totalTasksCompleted = new AtomicLong(0);
        this.totalTasksFailed = new AtomicLong(0);
        this.maxRetries = config.getMaxRetries();
        this.taskTimeoutMs = config.getTaskTimeoutMs();

        // 启动健康检查
        startHealthChecker(config.getHeartbeatIntervalMs(), config.getHeartbeatTimeoutMs());
    }

    /**
     * 注册Worker节点
     */
    public void registerWorker(JQuickWorkerNode worker) {
        String workerId = worker.getWorkerId();
        // 添加到集群拓扑
        cluster.addWorker(worker);

        // 记录Worker状态
        WorkerStatus status = new WorkerStatus(workerId);
        status.setMaxLoad(worker.getCpuCores() * 2); // 每个核心2个并发任务
        workerStatus.put(workerId, status);
        workers.put(workerId, worker);

        // 初始化任务队列
        pendingTasks.put(workerId, new CopyOnWriteArrayList<>());
        runningTasks.put(workerId, ConcurrentHashMap.newKeySet());

        // 触发事件
        if (eventListener != null) {
            eventListener.onWorkerRegistered(workerId);
        }

        System.out.println("[WorkerManager] Worker registered: " + workerId);
    }

    /**
     * 注销Worker节点
     */
    public void unregisterWorker(String workerId) {
        // 重新分配该Worker的待处理任务
        List<JQuickTask> tasks = pendingTasks.remove(workerId);
        if (tasks != null && !tasks.isEmpty()) {
            rescheduleTasks(tasks, workerId);
        }

        // 标记运行中的任务为失败
        Set<String> running = runningTasks.remove(workerId);
        if (running != null) {
            for (String taskId : running) {
                totalTasksFailed.incrementAndGet();
                if (eventListener != null) {
                    eventListener.onTaskFailed(workerId, taskId,
                            new RuntimeException("Worker unregistered: " + workerId));
                }
            }
        }

        // 从集群移除
        cluster.removeWorker(workerId);
        workerStatus.remove(workerId);
        workers.remove(workerId);

        // 触发事件
        if (eventListener != null) {
            eventListener.onWorkerUnregistered(workerId);
        }

        System.out.println("[WorkerManager] Worker unregistered: " + workerId);
    }

    /**
     * 提交任务到Worker
     */
    public CompletableFuture<TaskResult> submitTask(JQuickTask task) {
        return submitTask(task, 0);
    }

    /**
     * 提交任务到Worker（支持重试）
     */
    public CompletableFuture<TaskResult> submitTask(JQuickTask task, int retryCount) {
        String workerId = task.getWorkerId();
        String taskId = task.getTaskId();

        // 检查Worker是否健康
        WorkerStatus status = workerStatus.get(workerId);
        if (status == null || !status.canAcceptTask()) {
            // Worker不可用，尝试重新分配
            return rescheduleTask(task, retryCount);
        }

        CompletableFuture<TaskResult> future = new CompletableFuture<>();

        // 更新状态
        status.incrementLoad();
        runningTasks.computeIfAbsent(workerId, k -> ConcurrentHashMap.newKeySet()).add(taskId);
        totalTasksSubmitted.incrementAndGet();

        // 异步执行任务
        healthCheckExecutor.submit(() -> {
            long startTime = System.currentTimeMillis();
            try {
                // 实际执行任务（通过Worker的RPC调用）
                TaskResult result = executeTaskOnWorker(workerId, task);

                long duration = System.currentTimeMillis() - startTime;

                // 更新统计
                status.decrementLoad();
                status.incrementCompletedTasks();
                runningTasks.get(workerId).remove(taskId);
                totalTasksCompleted.incrementAndGet();

                // 触发事件
                if (eventListener != null) {
                    eventListener.onTaskCompleted(workerId, taskId, duration);
                }

                future.complete(result);

            } catch (Exception e) {
                status.decrementLoad();
                runningTasks.get(workerId).remove(taskId);

                // 处理失败和重试
                handleTaskFailure(task, e, retryCount, future);
            }
        });

        return future;
    }

    /**
     * 重新调度任务
     */
    private CompletableFuture<TaskResult> rescheduleTask(JQuickTask task, int retryCount) {
        if (retryCount >= maxRetries) {
            CompletableFuture<TaskResult> failed = new CompletableFuture<>();
            failed.completeExceptionally(new RuntimeException(
                    "Task failed after " + maxRetries + " retries: " + task.getTaskId()));
            return failed;
        }

        // 选择新的Worker
        String newWorkerId = selectWorkerForTask(task);
        if (newWorkerId == null) {
            CompletableFuture<TaskResult> failed = new CompletableFuture<>();
            failed.completeExceptionally(new RuntimeException("No available worker for task: " + task.getTaskId()));
            return failed;
        }

        // 创建新任务
        JQuickTask retryTask = new JQuickTask(
                task.getTaskId() + "_retry_" + (retryCount + 1),
                task.getStageId(),
                newWorkerId,
                task.getPhysicalPlan(),
                task.getContext(),
                task.getUpstreamStageIds(),
                task.getExchangeType(),
                task.getPartitionStrategy()
        );

        System.out.println("[WorkerManager] Rescheduling task " + task.getTaskId() +
                " from " + task.getWorkerId() + " to " + newWorkerId +
                " (retry " + (retryCount + 1) + "/" + maxRetries + ")");

        return submitTask(retryTask, retryCount + 1);
    }

    /**
     * 处理任务失败
     */
    private void handleTaskFailure(JQuickTask task, Throwable error,
                                   int retryCount, CompletableFuture<TaskResult> future) {
        String workerId = task.getWorkerId();
        WorkerStatus status = workerStatus.get(workerId);

        if (status != null) {
            status.incrementFailedTasks();
            status.addError(error.getMessage());

            // 连续失败多次，标记Worker为不健康
            if (status.getFailedTasks() > 5 && status.getFailedTasks() > status.getCompletedTasks() / 2) {
                markWorkerUnhealthy(workerId, "Too many failures: " + error.getMessage());
            }
        }

        totalTasksFailed.incrementAndGet();

        if (eventListener != null) {
            eventListener.onTaskFailed(workerId, task.getTaskId(), error);
        }

        // 重试
        if (retryCount < maxRetries) {
            rescheduleTask(task, retryCount).thenAccept(future::complete)
                    .exceptionally(ex -> {
                        future.completeExceptionally(ex);
                        return null;
                    });
        } else {
            future.completeExceptionally(new RuntimeException(
                    "Task failed after " + maxRetries + " retries: " + task.getTaskId(), error));
        }
    }

    /**
     * 为任务选择Worker
     */
    private String selectWorkerForTask(JQuickTask task) {
        // 获取所有可用Worker
        List<WorkerStatus> availableWorkers = workerStatus.values().stream()
                .filter(WorkerStatus::canAcceptTask)
                .sorted(Comparator.comparingInt(WorkerStatus::getCurrentLoad))
                .collect(Collectors.toList());

        if (availableWorkers.isEmpty()) {
            return null;
        }

        // 优先选择数据本地性Worker
        String preferredWorker = task.getWorkerId();
        if (preferredWorker != null) {
            WorkerStatus preferred = workerStatus.get(preferredWorker);
            if (preferred != null && preferred.canAcceptTask()) {
                return preferredWorker;
            }
        }

        // 返回负载最小的Worker
        return availableWorkers.get(0).getWorkerId();
    }

    /**
     * 重新分配任务（当Worker失效时）
     */
    private void rescheduleTasks(List<JQuickTask> tasks, String failedWorkerId) {
        for (JQuickTask task : tasks) {
            rescheduleTask(task, 0);
        }
    }

    /**
     * 在Worker上执行任务
     */
    private TaskResult executeTaskOnWorker(String workerId, JQuickTask task) throws Exception {
        JQuickWorkerNode worker = workers.get(workerId);
        if (worker == null) {
            throw new RuntimeException("Worker not found: " + workerId);
        }

        // 这里需要通过RPC调用Worker执行任务
        // 简化实现，实际应该使用Netty/gRPC等
        return callWorkerRpc(worker, task);
    }

    /**
     * RPC调用Worker
     */
    private TaskResult callWorkerRpc(JQuickWorkerNode worker, JQuickTask task) throws Exception {
        // TODO: 实现RPC调用
        // 这里简化返回成功结果
        return TaskResult.success(task.getTaskId(), null);
    }

    /**
     * 标记Worker为不健康
     */
    public void markWorkerUnhealthy(String workerId, String reason) {
        WorkerStatus status = workerStatus.get(workerId);
        if (status != null && status.isHealthy()) {
            status.setHealthy(false);
            status.addError(reason);

            // 重新分配待处理任务
            List<JQuickTask> tasks = pendingTasks.remove(workerId);
            if (tasks != null) {
                rescheduleTasks(tasks, workerId);
            }

            if (eventListener != null) {
                eventListener.onWorkerUnhealthy(workerId);
            }

            System.err.println("[WorkerManager] Worker marked unhealthy: " + workerId +
                    " - " + reason);
        }
    }

    /**
     * 标记Worker为健康
     */
    public void markWorkerHealthy(String workerId) {
        WorkerStatus status = workerStatus.get(workerId);
        if (status != null && !status.isHealthy()) {
            status.setHealthy(true);
            status.updateHeartbeat();

            if (eventListener != null) {
                eventListener.onWorkerHealthy(workerId);
            }

            System.out.println("[WorkerManager] Worker marked healthy: " + workerId);
        }
    }

    /**
     * 更新Worker心跳
     */
    public void updateHeartbeat(String workerId) {
        WorkerStatus status = workerStatus.get(workerId);
        if (status != null) {
            status.updateHeartbeat();
            if (!status.isHealthy()) {
                markWorkerHealthy(workerId);
            }
        }
    }

    /**
     * 更新Worker负载
     */
    public void updateWorkerLoad(String workerId, int currentLoad) {
        WorkerStatus status = workerStatus.get(workerId);
        if (status != null) {
            status.currentLoad = currentLoad;
        }
    }

    /**
     * 启动健康检查
     */
    private void startHealthChecker(long intervalMs, long timeoutMs) {
        scheduler.scheduleAtFixedRate(() -> {
            if (!running.get()) return;

            long now = System.currentTimeMillis();

            for (WorkerStatus status : workerStatus.values()) {
                if (status.isHealthy() && (now - status.getLastHeartbeat()) > timeoutMs) {
                    markWorkerUnhealthy(status.getWorkerId(), "Heartbeat timeout");
                }
            }
        }, intervalMs, intervalMs, TimeUnit.MILLISECONDS);
    }

    /**
     * 获取Worker状态
     */
    public WorkerStatus getWorkerStatus(String workerId) {
        return workerStatus.get(workerId);
    }

    /**
     * 获取所有Worker状态
     */
    public Map<String, WorkerStatus> getAllWorkerStatus() {
        return new HashMap<>(workerStatus);
    }

    /**
     * 获取可用Worker数量
     */
    public int getAvailableWorkerCount() {
        return (int) workerStatus.values().stream()
                .filter(WorkerStatus::canAcceptTask)
                .count();
    }

    /**
     * 获取统计信息
     */
    public WorkerManagerStats getStats() {
        return new WorkerManagerStats(
                workers.size(),
                getAvailableWorkerCount(),
                totalTasksSubmitted.get(),
                totalTasksCompleted.get(),
                totalTasksFailed.get(),
                workerStatus.values().stream()
                        .mapToInt(WorkerStatus::getCurrentLoad)
                        .sum(),
                workerStatus.values().stream()
                        .mapToInt(WorkerStatus::getMaxLoad)
                        .sum()
        );
    }

    /**
     * 设置事件监听器
     */
    public void setEventListener(WorkerEventListener listener) {
        this.eventListener = listener;
    }

    /**
     * 关闭管理器
     */
    public void shutdown() {
        running.set(false);
        healthCheckExecutor.shutdown();
        scheduler.shutdown();
        try {
            healthCheckExecutor.awaitTermination(10, TimeUnit.SECONDS);
            scheduler.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 统计信息
     */
    public static class WorkerManagerStats {
        private final int totalWorkers;
        private final int availableWorkers;
        private final long totalTasksSubmitted;
        private final long totalTasksCompleted;
        private final long totalTasksFailed;
        private final int currentTotalLoad;
        private final int maxTotalLoad;

        public WorkerManagerStats(int totalWorkers, int availableWorkers,
                                  long totalTasksSubmitted, long totalTasksCompleted,
                                  long totalTasksFailed, int currentTotalLoad, int maxTotalLoad) {
            this.totalWorkers = totalWorkers;
            this.availableWorkers = availableWorkers;
            this.totalTasksSubmitted = totalTasksSubmitted;
            this.totalTasksCompleted = totalTasksCompleted;
            this.totalTasksFailed = totalTasksFailed;
            this.currentTotalLoad = currentTotalLoad;
            this.maxTotalLoad = maxTotalLoad;
        }

        public int getTotalWorkers() { return totalWorkers; }
        public int getAvailableWorkers() { return availableWorkers; }
        public long getTotalTasksSubmitted() { return totalTasksSubmitted; }
        public long getTotalTasksCompleted() { return totalTasksCompleted; }
        public long getTotalTasksFailed() { return totalTasksFailed; }
        public int getCurrentTotalLoad() { return currentTotalLoad; }
        public int getMaxTotalLoad() { return maxTotalLoad; }
        public double getLoadRatio() {
            return maxTotalLoad > 0 ? (double) currentTotalLoad / maxTotalLoad : 0;
        }
        public double getSuccessRate() {
            long total = totalTasksCompleted + totalTasksFailed;
            return total > 0 ? (double) totalTasksCompleted / total : 1.0;
        }

        @Override
        public String toString() {
            return String.format("WorkerManagerStats{workers=%d/%d, tasks=%d/%d/%d, load=%.2f%%, rate=%.2f%%}",
                    availableWorkers, totalWorkers, totalTasksSubmitted, totalTasksCompleted, totalTasksFailed,
                    getLoadRatio() * 100, getSuccessRate() * 100);
        }
    }
}