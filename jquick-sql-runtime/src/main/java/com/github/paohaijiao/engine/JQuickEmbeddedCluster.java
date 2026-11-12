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
package com.github.paohaijiao.engine;

import com.github.paohaijiao.config.WorkerEndpoint;
import com.github.paohaijiao.console.JConsole;
import com.github.paohaijiao.distributed.worker.JQuickWorker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 嵌入式 Worker 集群单例
 * <p>
 * 同一 JVM 内所有 {@code JQuickSQL.embedded(...)} 实例共享同一组 Worker，gRPC 端口只在首次使用时绑定一次，
 * 避免多个 SQL 实例重复绑定固定端口（19001 起）导致的 {@code Address already in use: bind}。
 * 生命周期通过引用计数管理：最后一个持有者 {@link #release()} 后才真正停止 Worker。
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/11/12
 */
final class JQuickEmbeddedCluster {

    private static final JConsole console = JConsole.initConsoleEnvironment();

    /**
     * 嵌入式 Worker 起始端口，与原实现保持一致
     */
    private static final int BASE_PORT = 19001;

    private static final Object LOCK = new Object();

    private static JQuickEmbeddedCluster instance;

    private final List<JQuickWorker> workers = new ArrayList<>();

    private final List<WorkerEndpoint> endpoints = new ArrayList<>();

    private int refCount;

    private JQuickEmbeddedCluster() {
    }

    /**
     * 获取共享集群（首次调用时按需启动 Worker），并增加引用计数
     *
     * @param workerCount 期望的 Worker 数量，已有 Worker 足够时直接复用
     */
    static JQuickEmbeddedCluster acquire(int workerCount) {
        synchronized (LOCK) {
            if (instance == null) {
                instance = new JQuickEmbeddedCluster();
            }
            instance.startWorkers(workerCount);
            instance.refCount++;
            return instance;
        }
    }

    /**
     * 已启动的 Worker 端点（副本）
     */
    List<WorkerEndpoint> getEndpoints() {
        synchronized (LOCK) {
            return new ArrayList<>(endpoints);
        }
    }

    /**
     * 释放引用，引用计数归零时停止全部 Worker 并销毁单例
     */
    void release() {
        synchronized (LOCK) {
            if (this != instance) {
                return;
            }
            refCount = Math.max(0, refCount - 1);
            if (refCount == 0) {
                stopWorkers();
                instance = null;
                console.info("Embedded worker cluster stopped");
            }
        }
    }

    private void startWorkers(int workerCount) {
        if (workerCount <= workers.size()) {
            return;
        }
        int startIndex = workers.size();
        try {
            for (int index = startIndex; index < workerCount; index++) {
                int port = BASE_PORT + index;
                JQuickWorker worker = new JQuickWorker("worker-" + (index + 1), port);
                try {
                    worker.start();
                } catch (IOException e) {
                    worker.stop();
                    rollback(startIndex);
                    throw new IllegalStateException("Failed to start embedded worker on port " + port, e);
                }
                workers.add(worker);
                endpoints.add(new WorkerEndpoint(worker.getWorkerId(), "localhost", port, index));
                Thread.sleep(200);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            rollback(startIndex);
            throw new IllegalStateException("Interrupted while starting embedded workers", e);
        }
        for (JQuickWorker worker : workers) {
            worker.setWorkerEndpoints(new ArrayList<>(endpoints));
        }
        console.info("Embedded worker cluster ready on ports [" + (BASE_PORT + startIndex)
                + " - " + (BASE_PORT + workers.size() - 1) + "]");
    }

    private void rollback(int startIndex) {
        while (workers.size() > startIndex) {
            int last = workers.size() - 1;
            workers.remove(last).stop();
            endpoints.remove(last);
        }
    }

    private void stopWorkers() {
        for (JQuickWorker worker : workers) {
            worker.stop();
        }
        workers.clear();
        endpoints.clear();
    }
}
