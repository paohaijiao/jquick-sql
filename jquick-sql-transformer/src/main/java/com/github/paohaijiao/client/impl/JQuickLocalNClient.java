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


import com.github.paohaijiao.client.JQuickAbstractJQuickClient;
import com.github.paohaijiao.config.JQuickClientConfig;
import com.github.paohaijiao.console.JConsole;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 本地多线程客户端
 */
public class JQuickLocalNClient extends JQuickAbstractJQuickClient {

    private ExecutorService executorService;

    public JQuickLocalNClient() {
        super("local_n");
    }

    public JQuickLocalNClient(JQuickClientConfig config) {
        super("local_n", config);
    }

    @Override
    protected void doInit() {
        executorService = Executors.newFixedThreadPool(config.getParallelism());
        JConsole console=JConsole.initConsoleEnvironment();
        console.info("本地多线程客户端初始化成功 - ThreadPoolSize: "+ config.getParallelism());
    }

    @Override
    protected void doClose() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
            executorService = null;
        }
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public void submit(Runnable task) {
        executorService.submit(task);
    }
}


