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
package com.github.paohaijiao.client;


import com.github.paohaijiao.config.JQuickClientConfig;
import com.github.paohaijiao.console.JConsole;

/**
 * 客户端抽象基类
 */
public abstract class JQuickAbstractJQuickClient implements JQuickJQuickClient {


    protected final JQuickClientConfig config;

    protected final String clientType;

    protected volatile boolean initialized = false;

    protected volatile boolean closed = false;

    protected JQuickAbstractJQuickClient(String clientType) {
        this(clientType, JQuickClientConfig.defaultConfig());
    }

    protected JQuickAbstractJQuickClient(String clientType, JQuickClientConfig config) {
        this.clientType = clientType;
        this.config = config;
    }

    @Override
    public String getType() {
        return clientType;
    }

    @Override
    public JQuickJQuickClient getClient() {
        return this;
    }

    @Override
    public void init() {
        if (!initialized) {
            synchronized (this) {
                if (!initialized) {
                    doInit();
                    initialized = true;
                    JConsole console=JConsole.initConsoleEnvironment();
                    console.info(getName() + " 初始化完成");
                }
            }
        }
    }

    @Override
    public void close() {
        if (!closed) {
            synchronized (this) {
                if (!closed) {
                    doClose();
                    closed = true;
                    JConsole console=JConsole.initConsoleEnvironment();
                    console.info(getName() + " 已关闭");
                }
            }
        }
    }

    @Override
    public boolean isAvailable() {
        return initialized && !closed;
    }

    @Override
    public String getName() {
        return clientType + "-client";
    }

    /**
     * 执行初始化逻辑
     */
    protected abstract void doInit();

    /**
     * 执行关闭逻辑
     */
    protected abstract void doClose();
}
