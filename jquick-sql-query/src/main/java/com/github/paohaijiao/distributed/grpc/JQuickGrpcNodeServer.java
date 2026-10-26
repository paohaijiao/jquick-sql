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
package com.github.paohaijiao.distributed.grpc;

import com.github.paohaijiao.console.JConsole;
import com.github.paohaijiao.grpc.config.JQuickGrpcServerConfig;
import com.github.paohaijiao.grpc.server.JQuickGrpcServer;
import com.github.paohaijiao.grpc.server.impl.JQuickGrpcNettyServer;
import io.grpc.BindableService;

/**
 * 节点 gRPC 服务端封装
 * <p>
 * 通过 jquick-grpc 发布 jquick-sql 执行层服务，替代原生 {@code ServerBuilder#addService(...)#build()#start()}：
 * <ul>
 *     <li>统一端口 / 最大入站消息 / 明文传输配置，映射自 jquick-sql 现有运行时配置，不新增配置项；</li>
 *     <li>服务注册以 proto 服务全限定名（{@link JQuickGrpcServiceNames}）为键，注册即同步 gRPC 健康检查状态；</li>
 *     <li>生命周期由 jquick-grpc 统一管理（start / stop / 优雅关闭）。</li>
 * </ul>
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/10/26
 */
public class JQuickGrpcNodeServer {

    private static final JConsole console = JConsole.initConsoleEnvironment();

    private final int port;

    private final int maxInboundMessageSize;

    private final JQuickGrpcServer server;

    private volatile boolean started = false;

    /**
     * @param port                 监听端口，0 表示由系统分配随机端口
     * @param maxInboundMessageSize 最大入站消息字节数，沿用 jquick.runtime.maxFileSize
     */
    public JQuickGrpcNodeServer(int port, int maxInboundMessageSize) {
        this.port = port;
        this.maxInboundMessageSize = maxInboundMessageSize;
        this.server = new JQuickGrpcNettyServer(buildServerConfig(port, maxInboundMessageSize));
    }

    /**
     * 构建 jquick-grpc 服务端配置，保持与原 ServerBuilder 行为一致（明文传输 + 最大入站消息限制）
     */
    private static JQuickGrpcServerConfig buildServerConfig(int port, int maxInboundMessageSize) {
        JQuickGrpcServerConfig config = new JQuickGrpcServerConfig();
        config.setPort(port);
        config.setMaxInboundMessageSize(maxInboundMessageSize);
        config.setUsePlaintext(true);
        return config;
    }

    /**
     * 注册服务实现，服务名使用 proto 全限定名，供健康检查与 JQuick 网关发现
     *
     * @param serviceName proto 服务全限定名，见 {@link JQuickGrpcServiceNames}
     * @param service     生成的 ImplBase 实现
     */
    public void registerService(String serviceName, BindableService service) {
        server.registerService(serviceName, service);
        console.info("Registered gRPC service: " + serviceName);
    }

    /**
     * 启动服务
     */
    public void start() throws Exception {
        server.start();
        this.started = true;
        console.info("jquick-grpc server started on port " + getPort());
    }

    /**
     * 停止服务（jquick-grpc 内部优雅关闭）
     */
    public void stop() {
        if (!started) {
            return;
        }
        server.stop();
        this.started = false;
        console.info("jquick-grpc server stopped, previous port: " + port);
    }

    /**
     * 实际监听端口（端口配置为 0 时由系统分配）
     */
    public int getPort() {
        return started ? server.getPort() : port;
    }

    public boolean isRunning() {
        return started && server.isRunning();
    }

    /**
     * 阻塞等待服务终止
     */
    public void awaitTermination() throws InterruptedException {
        if (!started) {
            return;
        }
        server.getServer().awaitTermination();
    }

    public int getMaxInboundMessageSize() {
        return maxInboundMessageSize;
    }
}
