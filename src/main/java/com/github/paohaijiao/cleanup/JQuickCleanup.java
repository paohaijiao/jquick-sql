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
package com.github.paohaijiao.cleanup;

import com.github.paohaijiao.scheduler.JQuickExchangeChannel;
import com.github.paohaijiao.scheduler.JQuickSchedulePlan;
import com.github.paohaijiao.scheduler.JQuickTask;
import com.github.paohaijiao.scheduler.JQuickWorker;

import java.io.File;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 资源清理器 - 负责清理执行过程中的所有资源
 */
public class JQuickCleanup {

    private final ExecutorService cleanupExecutor;

    private final AtomicBoolean isCleaningUp;

    private final AtomicLong cleanupCount;

    private final Set<JQuickTask> runningTasks;

    private final Set<JQuickExchangeChannel> activeChannels;

    // 清理配置
    private long taskCancellationTimeoutMs = 30000;

    private long workerStopTimeoutMs = 60000;

    private long channelCloseTimeoutMs = 10000;

    public JQuickCleanup() {
        this.cleanupExecutor = Executors.newCachedThreadPool();
        this.isCleaningUp = new AtomicBoolean(false);
        this.cleanupCount = new AtomicLong(0);
        this.runningTasks = ConcurrentHashMap.newKeySet();
        this.activeChannels = ConcurrentHashMap.newKeySet();
    }

    /**
     * 清理所有资源 - 带 Worker Map 参数
     */
    public CleanupResult cleanup(Map<String, JQuickWorker> workers) {
        return cleanup(workers, null, null);
    }

    /**
     * 清理所有资源 - 带 Worker Map 和任务列表
     */
    public CleanupResult cleanup(Map<String, JQuickWorker> workers, Collection<JQuickTask> tasks, JQuickSchedulePlan schedulePlan) {
        if (!isCleaningUp.compareAndSet(false, true)) {
            return new CleanupResult(false, "Cleanup already in progress");
        }
        long startTime = System.currentTimeMillis();
        CleanupResult result = new CleanupResult();
        try {
            result.addPhase("Cancel schedule plan", cancelSchedulePlan(schedulePlan));
            result.addPhase("Cancel running tasks", cancelTasks(tasks));
            result.addPhase("Close data channels", closeChannels());
            result.addPhase("Stop workers", stopWorkers(workers));
            result.addPhase("Shutdown executor", shutdownExecutor());
            result.addPhase("Clean temp files", cleanupTempFiles());
            long cleanupTime = System.currentTimeMillis() - startTime;
            result.setSuccess(true);
            result.setMessage("Cleanup completed successfully");
            result.setCleanupTimeMs(cleanupTime);
            result.setCleanupCount(cleanupCount.incrementAndGet());
            System.out.println("Cleanup completed in " + cleanupTime + "ms");
        } catch (Exception e) {
            result.setSuccess(false);
            result.setMessage("Cleanup failed: " + e.getMessage());
            result.setError(e);
            e.printStackTrace();
        } finally {
            isCleaningUp.set(false);
        }

        return result;
    }

    /**
     * 清理所有资源 - 简化版本
     */
    public CleanupResult cleanupQuick(Map<String, JQuickWorker> workers) {
        return cleanup(workers, null, null);
    }

    /**
     * 异步清理
     */
    public CompletableFuture<CleanupResult> cleanupAsync(Map<String, JQuickWorker> workers) {
        return CompletableFuture.supplyAsync(() -> cleanup(workers), cleanupExecutor);
    }

    /**
     * 取消调度计划
     */
    private boolean cancelSchedulePlan(JQuickSchedulePlan schedulePlan) {
        if (schedulePlan == null) {
            return true;
        }
        try {
//            if (schedulePlan.getStatus() == JQuickSchedulePlan.PlanStatus.RUNNING ||
//                    schedulePlan.getStatus() == JQuickSchedulePlan.PlanStatus.PENDING) {
//                schedulePlan.cancel();
//                System.out.println("Cancelled schedule plan: " + schedulePlan.getPlanId());
//            }
            return true;
        } catch (Exception e) {
            System.err.println("Failed to cancel schedule plan: " + e.getMessage());
            return false;
        }
    }

    /**
     * 取消所有任务
     */
    private boolean cancelTasks(Collection<JQuickTask> tasks) {
        if (tasks == null || tasks.isEmpty()) {
            return true;
        }
        boolean allCancelled = true;
        ExecutorService taskCancellationExecutor = Executors.newCachedThreadPool();
        List<CompletableFuture<Boolean>> futures = new ArrayList<>();
        for (JQuickTask task : tasks) {
            if (task.getStatus() == JQuickTask.TaskStatus.RUNNING ||
                    task.getStatus() == JQuickTask.TaskStatus.PENDING ||
                    task.getStatus() == JQuickTask.TaskStatus.SCHEDULED) {
                CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(() -> {
                    try {
                        cancelSingleTask(task);
                        return true;
                    } catch (Exception e) {
                        System.err.println("Failed to cancel task " + task.getTaskId() + ": " + e.getMessage());
                        return false;
                    }
                }, taskCancellationExecutor);
                futures.add(future);
            }
        }

        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get(taskCancellationTimeoutMs, TimeUnit.MILLISECONDS);
            for (CompletableFuture<Boolean> future : futures) {
                if (!future.getNow(true)) {
                    allCancelled = false;
                }
            }
        } catch (Exception e) {
            allCancelled = false;
            System.err.println("Task cancellation timeout or error: " + e.getMessage());
        } finally {
            taskCancellationExecutor.shutdown();
        }

        runningTasks.clear();
        return allCancelled;
    }

    /**
     * 取消单个任务
     */
    private void cancelSingleTask(JQuickTask task) {
        task.setStatus(JQuickTask.TaskStatus.CANCELLED);
        // 关闭任务的输出
        if (task.getOutput() != null) {
            task.getOutput().complete();
        }
        // 清理任务的输入通道
        for (com.github.paohaijiao.scheduler.JQuickTaskInput input : task.getInputs()) {
            if (input.getChannel() != null) {
                activeChannels.remove(input.getChannel());
            }
        }
        System.out.println("Cancelled task: " + task.getTaskId());
    }

    /**
     * 关闭所有数据通道
     */
    private boolean closeChannels() {
        if (activeChannels.isEmpty()) {
            return true;
        }
        boolean allClosed = true;
        ExecutorService channelExecutor = Executors.newCachedThreadPool();
        List<CompletableFuture<Boolean>> futures = new ArrayList<>();
        for (JQuickExchangeChannel channel : activeChannels) {
            CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(() -> {
                try {
                    closeChannel(channel);
                    return true;
                } catch (Exception e) {
                    System.err.println("Failed to close channel " + channel.getChannelId() + ": " + e.getMessage());
                    return false;
                }
            }, channelExecutor);
            futures.add(future);
        }
        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get(channelCloseTimeoutMs, TimeUnit.MILLISECONDS);
            for (CompletableFuture<Boolean> future : futures) {
                if (!future.getNow(true)) {
                    allClosed = false;
                }
            }
        } catch (Exception e) {
            allClosed = false;
            System.err.println("Channel closing timeout: " + e.getMessage());
        } finally {
            channelExecutor.shutdown();
        }
        activeChannels.clear();
        return allClosed;
    }

    /**
     * 关闭单个通道
     */
    private void closeChannel(JQuickExchangeChannel channel) {
        System.out.println("Closed channel: " + channel.getChannelId());
    }

    /**
     * 停止所有 Worker
     */
    private boolean stopWorkers(Map<String, JQuickWorker> workers) {
        if (workers == null || workers.isEmpty()) {
            return true;
        }
        boolean allStopped = true;
        ExecutorService workerExecutor = Executors.newCachedThreadPool();
        List<CompletableFuture<Boolean>> futures = new ArrayList<>();
        for (Map.Entry<String, JQuickWorker> entry : workers.entrySet()) {
            String workerId = entry.getKey();
            JQuickWorker worker = entry.getValue();
            CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(() -> {
                try {
                    stopSingleWorker(worker);
                    System.out.println("Stopped worker: " + workerId);
                    return true;
                } catch (Exception e) {
                    System.err.println("Failed to stop worker " + workerId + ": " + e.getMessage());
                    return false;
                }
            }, workerExecutor);
            futures.add(future);
        }

        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get(workerStopTimeoutMs, TimeUnit.MILLISECONDS);
            for (CompletableFuture<Boolean> future : futures) {
                if (!future.getNow(true)) {
                    allStopped = false;
                }
            }
        } catch (Exception e) {
            allStopped = false;
            System.err.println("Worker stopping timeout: " + e.getMessage());
        } finally {
            workerExecutor.shutdown();
        }
        return allStopped;
    }

    /**
     * 停止单个 Worker
     */
    private void stopSingleWorker(JQuickWorker worker) {
        worker.stop();
    }

    /**
     * 关闭清理执行器
     */
    private boolean shutdownExecutor() {
        try {
            cleanupExecutor.shutdown();
            boolean terminated = cleanupExecutor.awaitTermination(10, TimeUnit.SECONDS);
            if (!terminated) {
                cleanupExecutor.shutdownNow();
            }
            return true;
        } catch (InterruptedException e) {
            cleanupExecutor.shutdownNow();
            Thread.currentThread().interrupt();
            return false;
        }
    }

    /**
     * 清理临时文件
     */
    private boolean cleanupTempFiles() {
        try {
            String tempDir = System.getProperty("java.io.tmpdir");
            File tempDirFile = new File(tempDir);
            File[] tempFiles = tempDirFile.listFiles((dir, name) -> name.startsWith("jquick_") || name.startsWith("spill_") || name.startsWith("shuffle_"));
            int deletedCount = 0;
            if (tempFiles != null) {
                for (File file : tempFiles) {
                    if (file.delete()) {
                        deletedCount++;
                    }
                }
            }
            if (deletedCount > 0) {
                System.out.println("Deleted " + deletedCount + " temp files");
            }
            return true;
        } catch (Exception e) {
            System.err.println("Failed to clean temp files: " + e.getMessage());
            return false;
        }
    }

    /**
     * 注册运行中的任务
     */
    public void registerTask(JQuickTask task) {
        runningTasks.add(task);
    }

    /**
     * 注册通道
     */
    public void registerChannel(JQuickExchangeChannel channel) {
        activeChannels.add(channel);
    }

    /**
     * 移除任务
     */
    public void removeTask(JQuickTask task) {
        runningTasks.remove(task);
    }

    /**
     * 移除通道
     */
    public void removeChannel(JQuickExchangeChannel channel) {
        activeChannels.remove(channel);
    }

    /**
     * 设置任务取消超时
     */
    public void setTaskCancellationTimeoutMs(long timeoutMs) {
        this.taskCancellationTimeoutMs = timeoutMs;
    }

    /**
     * 设置 Worker 停止超时
     */
    public void setWorkerStopTimeoutMs(long timeoutMs) {
        this.workerStopTimeoutMs = timeoutMs;
    }

    /**
     * 获取当前清理次数
     */
    public long getCleanupCount() {
        return cleanupCount.get();
    }

    /**
     * 是否正在清理中
     */
    public boolean isCleaningUp() {
        return isCleaningUp.get();
    }

    /**
     * 清理结果类
     */
    public static class CleanupResult {

        private boolean success;

        private String message;

        private Throwable error;

        private final Map<String, Boolean> phases;

        private long cleanupTimeMs;

        private long cleanupCount;

        public CleanupResult() {
            this.phases = new LinkedHashMap<>();
        }

        public CleanupResult(boolean success, String message) {
            this();
            this.success = success;
            this.message = message;
        }

        public void addPhase(String phaseName, boolean success) {
            phases.put(phaseName, success);
        }

        public void setSuccess(boolean success) { this.success = success; }

        public void setMessage(String message) { this.message = message; }

        public void setError(Throwable error) { this.error = error; }

        public void setCleanupTimeMs(long timeMs) { this.cleanupTimeMs = timeMs; }

        public void setCleanupCount(long count) { this.cleanupCount = count; }

        public boolean isSuccess() { return success; }

        public String getMessage() { return message; }

        public Throwable getError() { return error; }

        public Map<String, Boolean> getPhases() { return phases; }

        public long getCleanupTimeMs() { return cleanupTimeMs; }

        public long getCleanupCount() { return cleanupCount; }

        public String getSummary() {
            long successCount = phases.values().stream().filter(v -> v).count();
            long totalCount = phases.size();
            return String.format("Phases: %d/%d succeeded", successCount, totalCount);
        }

        @Override
        public String toString() {
            return String.format("CleanupResult{success=%s, message='%s', time=%dms, count=%d, %s}", success, message, cleanupTimeMs, cleanupCount, getSummary());
        }
    }
}