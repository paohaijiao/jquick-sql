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

/**
 * packageName com.github.paohaijiao.client.impl
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/5/3
 */
public class JQuickLocalClient extends JQuickAbstractJQuickClient {

    public JQuickLocalClient() {
        super("local");
    }

    public JQuickLocalClient(JQuickClientConfig config) {
        super("local", config);
    }

    @Override
    protected void doInit() {
        JConsole console=JConsole.initConsoleEnvironment();
        console.info("本地单线程客户端初始化成功");
    }

    @Override
    protected void doClose() {
        // 无需特殊处理
    }

}
