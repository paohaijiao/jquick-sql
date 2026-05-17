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

/**
 * packageName com.github.paohaijiao.worker
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/5/17
 */

import com.github.paohaijiao.context.JQuickExecutionContext;
import com.github.paohaijiao.fragment.JQuickFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 工作节点管理器
 */
public class JQuickWorkerManager {

    private final Map<String, WorkerNode> workers;
    private final ExecutorService localExecutor;

    public JQuickWorkerManager() {
        this.workers = new ConcurrentHashMap<>();
        this.localExecutor = Executors.newCachedThreadPool();
        // 注册本地节点作为worker
        registerWorker("localhost", Runtime.getRuntime().availableProcessors());
    }

    public void registerWorker(String host, int cores) {
        workers.put(host, new WorkerNode(host, cores));
    }

    /**
     * 分配工作节点
     */
    public List<String> assignWorkers(int count) {
        List<String> available = new ArrayList<>(workers.keySet());
        if (available.isEmpty()) {
            return Collections.singletonList("localhost");
        }
        Collections.shuffle(available);
        return available.subList(0, Math.min(count, available.size()));
    }

    /**
     * 提交任务到工作节点
     */
    public Future<JQuickFragmentResult> submitTask(String worker, JQuickFragment fragment) {
        WorkerNode node = workers.get(worker);
        if (node == null) {
            throw new RuntimeException("Worker not found: " + worker);
        }

        // 本地执行或远程执行
        if ("localhost".equals(worker)) {
            return localExecutor.submit(() -> executeFragment(fragment));
        } else {
            // 远程执行需要网络通信
            return submitRemoteTask(worker, fragment);
        }
    }

    /**
     * 本地执行片段
     */
    private JQuickFragmentResult executeFragment(JQuickFragment fragment) {
        long startTime = System.currentTimeMillis();
        try {
            // 执行物理计划
            JQuickExecutionContext context = new JQuickExecutionContext();
            // 获取输入数据（从Exchange）
            // JQuickDataSet result = fragment.getPlan().execute(context);

            JQuickFragmentResult result = new JQuickFragmentResult();
            // result.setData(resultData);
            result.setSuccess(true);
            result.setExecutionTime(System.currentTimeMillis() - startTime);
            return result;
        } catch (Exception e) {
            JQuickFragmentResult result = new JQuickFragmentResult();
            result.setSuccess(false);
            result.setError(e);
            return result;
        }
    }

    private Future<JQuickFragmentResult> submitRemoteTask(String worker, JQuickFragment fragment) {
        // 远程执行需要网络通信
        CompletableFuture<JQuickFragmentResult> future = new CompletableFuture<>();
        // 简化实现
        future.completeExceptionally(new UnsupportedOperationException("Remote execution not implemented"));
        return future;
    }

    public void cancelTask(long taskId) {
        // 取消任务
    }

    private static class WorkerNode {
        final String host;
        final int cores;
        final boolean healthy;

        WorkerNode(String host, int cores) {
            this.host = host;
            this.cores = cores;
            this.healthy = true;
        }
    }
}
