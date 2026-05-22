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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Worker 节点 - 执行任务的工作节点
 */
public class JQuickWorker {

    private final String workerId;

    private final String host;

    private final int controlPort;

    private final int dataPort;

    private final int taskSlots;

    private final ExecutorService taskExecutor;

    private final Map<Long, JQuickTaskExecutor> runningTasks;

    private final DataExchangeService dataExchange;

    private final AtomicInteger activeTasks;

    public JQuickWorker(String workerId, String host, int controlPort, int dataPort, int taskSlots) {
        this.workerId = workerId;
        this.host = host;
        this.controlPort = controlPort;
        this.dataPort = dataPort;
        this.taskSlots = taskSlots;
        this.taskExecutor = Executors.newFixedThreadPool(taskSlots);
        this.runningTasks = new ConcurrentHashMap<>();
        this.dataExchange = new DataExchangeService(dataPort);
        this.activeTasks = new AtomicInteger(0);
    }

    /**
     * 启动 Worker
     */
    public void start() {
        // 启动数据交换服务
        dataExchange.start();
        // 启动控制服务
        startControlServer();
        System.out.println("Worker " + workerId + " started on " + host + ":" + controlPort);
    }

    /**
     * 启动控制服务
     */
    private void startControlServer() {
        // 实现控制服务，接收来自 Scheduler 的任务提交
        // 简化实现：使用 Netty 或 gRPC
        new Thread(() -> {
            // 伪代码：监听控制端口，接收任务请求
        }).start();
    }

    /**
     * 接收任务
     */
    public void submitTask(JQuickTask task) {
        if (activeTasks.get() >= taskSlots) {
            throw new IllegalStateException("No available task slots");
        }

        JQuickTaskExecutor executor = new JQuickTaskExecutor(task, dataExchange);
        runningTasks.put(task.getTaskId(), executor);
        activeTasks.incrementAndGet();

        taskExecutor.submit(() -> {
            try {
                executor.execute();
                task.setStatus(JQuickTask.TaskStatus.FINISHED);
            } catch (Exception e) {
                task.setStatus(JQuickTask.TaskStatus.FAILED);
                e.printStackTrace();
            } finally {
                runningTasks.remove(task.getTaskId());
                activeTasks.decrementAndGet();
            }
        });
    }

    /**
     * 停止 Worker
     */
    public void stop() {
        taskExecutor.shutdown();
        dataExchange.stop();
        System.out.println("Worker " + workerId + " stopped");
    }

    public String getWorkerId() { return workerId; }

    public int getActiveTasks() { return activeTasks.get(); }

    public int getAvailableSlots() { return taskSlots - activeTasks.get(); }
}
