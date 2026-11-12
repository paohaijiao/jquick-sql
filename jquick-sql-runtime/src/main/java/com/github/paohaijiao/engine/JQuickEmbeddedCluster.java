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
 * Shared embedded worker cluster.
 * <p>
 * All {@code JQuickSQL.embedded(...)} instances in one JVM share the same workers,
 * so the gRPC ports are bound only once. Workers are stopped when the last holder
 * calls {@link #release()}.
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/11/12
 */
final class JQuickEmbeddedCluster {

    private static final JConsole console = JConsole.initConsoleEnvironment();

    private static final Object LOCK = new Object();

    private static JQuickEmbeddedCluster instance;

    private final int basePort;

    private final List<JQuickWorker> workers = new ArrayList<>();

    private final List<WorkerEndpoint> endpoints = new ArrayList<>();

    private int refCount;

    private JQuickEmbeddedCluster(int basePort) {
        this.basePort = basePort;
    }

    /**
     * Returns the shared cluster, starting workers on first use and increasing the reference count.
     *
     * @param workerCount expected worker count; existing workers are reused when sufficient
     * @param basePort    base gRPC port, applied only when the cluster is created
     */
    static JQuickEmbeddedCluster acquire(int workerCount, int basePort) {
        synchronized (LOCK) {
            if (instance == null) {
                instance = new JQuickEmbeddedCluster(basePort);
            }
            instance.startWorkers(workerCount);
            instance.refCount++;
            return instance;
        }
    }

    /**
     * Returns a copy of the started worker endpoints.
     */
    List<WorkerEndpoint> getEndpoints() {
        synchronized (LOCK) {
            return new ArrayList<>(endpoints);
        }
    }

    /**
     * Releases a reference and stops all workers when the count reaches zero.
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
                int port = basePort + index;
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
        console.info("Embedded worker cluster ready on ports [" + (basePort + startIndex)
                + " - " + (basePort + workers.size() - 1) + "]");
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
