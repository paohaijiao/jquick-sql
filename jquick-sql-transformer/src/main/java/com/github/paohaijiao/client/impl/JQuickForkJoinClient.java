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
package com.github.paohaijiao.client.impl;

/**
 * packageName com.github.paohaijiao.client.impl
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/5/3
 */

import com.github.paohaijiao.client.JQuickAbstractJQuickClient;
import com.github.paohaijiao.config.JQuickClientConfig;
import com.github.paohaijiao.console.JConsole;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;

/**
 * ForkJoin 客户端实现
 * 支持并行计算
 */
public class JQuickForkJoinClient extends JQuickAbstractJQuickClient {

    private ForkJoinPool forkJoinPool;

    private ForkJoinPool.ForkJoinWorkerThreadFactory threadFactory;

    public JQuickForkJoinClient() {
        super("forkjoin");
    }

    public JQuickForkJoinClient(JQuickClientConfig config) {
        super("forkjoin", config);
    }

    @Override
    protected void doInit() {
        try {
            threadFactory = new ForkJoinPool.ForkJoinWorkerThreadFactory() { // 创建线程工厂
                @Override
                public ForkJoinWorkerThread newThread(ForkJoinPool pool) {
                    ForkJoinWorkerThread thread = ForkJoinPool.defaultForkJoinWorkerThreadFactory.newThread(pool);
                    thread.setName("jquick-forkjoin-" + thread.getPoolIndex());
                    thread.setDaemon(false);
                    return thread;
                }
            };
            forkJoinPool = new ForkJoinPool(// 创建 ForkJoinPool
                    config.getForkJoinPoolSize(),
                    threadFactory,
                    null,
                    config.isAsyncMode()
            );
            JConsole console=JConsole.initConsoleEnvironment();

            console.info("ForkJoin 客户端初始化成功 - PoolSize:"+config.getForkJoinPoolSize()+", AsyncMode: "+ config.isAsyncMode());

        } catch (Exception e) {
            JConsole console=JConsole.initConsoleEnvironment();
            console.error("ForkJoin 客户端初始化失败", e);
            throw new RuntimeException("ForkJoin 客户端初始化失败", e);
        }
    }

    @Override
    protected void doClose() {
        if (forkJoinPool != null && !forkJoinPool.isShutdown()) {
            forkJoinPool.shutdown();
            forkJoinPool = null;
        }
    }

    public ForkJoinPool getForkJoinPool() {
        checkAvailable();
        return forkJoinPool;
    }

    /**
     * 提交任务
     */
    public <T> ForkJoinTask<T> submit(ForkJoinTask<T> task) {
        checkAvailable();
        return (ForkJoinTask<T>) forkJoinPool.submit(task);
    }
    /**
     * 执行任务
     */
    public void execute(Runnable task) {
        checkAvailable();
        forkJoinPool.execute(task);
    }

    /**
     * 获取并行度
     */
    public int getParallelism() {
        return forkJoinPool != null ? forkJoinPool.getParallelism() : 0;
    }

    /**
     * 获取活动线程数
     */
    public int getActiveThreadCount() {
        return forkJoinPool != null ? forkJoinPool.getActiveThreadCount() : 0;
    }

    /**
     * 获取队列任务数
     */
    public long getQueuedTaskCount() {
        return forkJoinPool != null ? forkJoinPool.getQueuedTaskCount() : 0;
    }

    private void checkAvailable() {
        if (!isAvailable()) {
            throw new IllegalStateException("ForkJoin 客户端未初始化或已关闭");
        }
    }

    public static abstract class ForkJoinTask<T> extends java.util.concurrent.RecursiveTask<T> {

        protected final String taskName;

        public ForkJoinTask(String taskName) {
            this.taskName = taskName;
        }

        public String getTaskName() {
            return taskName;
        }
    }
}
