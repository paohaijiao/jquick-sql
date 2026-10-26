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

import com.github.paohaijiao.config.WorkerEndpoint;
import com.github.paohaijiao.console.JConsole;
import com.github.paohaijiao.grpc.config.JQuickGrpcClientConfig;
import com.github.paohaijiao.grpc.interceptor.JQuickGrpcClientInterceptor;
import com.github.paohaijiao.grpc.pool.JQuickGrpcChannelPool;
import com.github.paohaijiao.proto.JQuickDataDistributionServiceGrpc;
import com.github.paohaijiao.proto.JQuickPhysicalPlanServiceGrpc;
import com.github.paohaijiao.proto.JQuickTableServiceGrpc;
import io.grpc.Channel;
import io.grpc.ClientInterceptors;
import io.grpc.ManagedChannel;
import io.grpc.stub.AbstractStub;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 客户端通道注册表 - jquick-sql 节点间调用的唯一通道来源
 * <p>
 * 用 jquick-grpc 的通道封装替代原生 {@code ManagedChannelBuilder}：
 * <ul>
 *     <li>通道由 {@link JQuickGrpcChannelPool} 创建（连接池 / keepAlive / 最大入站消息统一由 jquick-grpc 管理）；</li>
 *     <li>按 {@code host:port} 缓存长连接，等价于原实现的「每个 Worker 一条缓存通道」；</li>
 *     <li>统一挂载 jquick-grpc 客户端拦截器（{@link JQuickGrpcClientInterceptor}），traceId / userId 随调用透传，
 *         后续可由 JQuick 网关与注册中心消费；</li>
 *     <li>统一收口 deadline 与资源释放，业务代码不再持有任何 gRPC 通道 / Stub 创建逻辑。</li>
 * </ul>
 * 说明：生成的 Stub 类为抽象类，jquick-grpc 1.4.0 的 {@code JQuickGrpcClient#getService} 基于 JDK 动态代理，
 * 仅支持接口，因此本类以 jquick-grpc 的通道池 + 生成的 Stub 工厂组合使用，详见迁移 Checklist 待跟进项。
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/10/26
 */
public class JQuickGrpcChannelRegistry implements AutoCloseable {

    private static final JConsole console = JConsole.initConsoleEnvironment();

    /**
     * 单节点连接池上限，与本节点可并发访问的对端数量解耦（每个对端仅占用一条长连接）
     */
    private static final int DEFAULT_MAX_CONNECTIONS = 16;

    private final JQuickGrpcClientConfig clientConfig;

    private final long defaultDeadlineMillis;

    private final Map<String, JQuickGrpcChannelPool> channelPools;

    private final Map<String, ManagedChannel> managedChannels;

    private final Map<String, Channel> channels;

    private final AtomicBoolean closed;

    public JQuickGrpcChannelRegistry(int maxInboundMessageSize) {
        this(maxInboundMessageSize, 0L);
    }

    /**
     * @param maxInboundMessageSize 最大入站消息字节数，沿用 jquick.runtime.maxFileSize
     * @param defaultDeadlineMillis 默认调用超时（毫秒），0 表示不设置超时，与原实现行为一致
     */
    public JQuickGrpcChannelRegistry(int maxInboundMessageSize, long defaultDeadlineMillis) {
        this.clientConfig = buildClientConfig(maxInboundMessageSize);
        this.defaultDeadlineMillis = defaultDeadlineMillis;
        this.channelPools = new ConcurrentHashMap<>();
        this.managedChannels = new ConcurrentHashMap<>();
        this.channels = new ConcurrentHashMap<>();
        this.closed = new AtomicBoolean(false);
    }

    private static JQuickGrpcClientConfig buildClientConfig(int maxInboundMessageSize) {
        JQuickGrpcClientConfig config = new JQuickGrpcClientConfig();
        config.setClientType("pooled");
        config.setUsePlaintext(true);
        config.setMaxInboundMessageSize(maxInboundMessageSize);
        config.setMaxConnections(DEFAULT_MAX_CONNECTIONS);
        config.setMaxIdle(DEFAULT_MAX_CONNECTIONS);
        config.setMinIdle(0);
        config.setDeadlineMillis(0L);
        config.setMaxRetries(0);
        return config;
    }

    /**
     * 物理计划服务 - 阻塞式 Stub
     */
    public JQuickPhysicalPlanServiceGrpc.JQuickPhysicalPlanServiceBlockingStub physicalPlanBlocking(WorkerEndpoint endpoint) {
        return physicalPlanBlocking(endpoint.getHost(), endpoint.getPort());
    }

    /**
     * 物理计划服务 - 阻塞式 Stub
     */
    public JQuickPhysicalPlanServiceGrpc.JQuickPhysicalPlanServiceBlockingStub physicalPlanBlocking(String host, int port) {
        return withDeadline(JQuickPhysicalPlanServiceGrpc.newBlockingStub(channel(host, port)));
    }

    /**
     * 物理计划服务 - 异步 Stub
     */
    public JQuickPhysicalPlanServiceGrpc.JQuickPhysicalPlanServiceStub physicalPlanAsync(WorkerEndpoint endpoint) {
        return physicalPlanAsync(endpoint.getHost(), endpoint.getPort());
    }

    /**
     * 物理计划服务 - 异步 Stub
     */
    public JQuickPhysicalPlanServiceGrpc.JQuickPhysicalPlanServiceStub physicalPlanAsync(String host, int port) {
        return withDeadline(JQuickPhysicalPlanServiceGrpc.newStub(channel(host, port)));
    }

    /**
     * 数据分发服务 - 异步 Stub（双向流）
     */
    public JQuickDataDistributionServiceGrpc.JQuickDataDistributionServiceStub distributionAsync(WorkerEndpoint endpoint) {
        return distributionAsync(endpoint.getHost(), endpoint.getPort());
    }

    /**
     * 数据分发服务 - 异步 Stub（双向流）
     */
    public JQuickDataDistributionServiceGrpc.JQuickDataDistributionServiceStub distributionAsync(String host, int port) {
        return withDeadline(JQuickDataDistributionServiceGrpc.newStub(channel(host, port)));
    }

    /**
     * 数据分发服务 - 阻塞式 Stub
     */
    public JQuickDataDistributionServiceGrpc.JQuickDataDistributionServiceBlockingStub distributionBlocking(WorkerEndpoint endpoint) {
        return withDeadline(JQuickDataDistributionServiceGrpc.newBlockingStub(channel(endpoint.getHost(), endpoint.getPort())));
    }

    /**
     * 表服务 - 阻塞式 Stub
     */
    public JQuickTableServiceGrpc.JQuickTableServiceBlockingStub tableBlocking(WorkerEndpoint endpoint) {
        return withDeadline(JQuickTableServiceGrpc.newBlockingStub(channel(endpoint.getHost(), endpoint.getPort())));
    }

    /**
     * 获取（或创建）指向目标节点的通道
     * <p>
     * 通道由 jquick-grpc 连接池创建并按地址缓存，等价于原实现中「每个 Worker 一条长连接」的复用策略。
     */
    private Channel channel(String host, int port) {
        if (closed.get()) {
            throw new IllegalStateException("jquick-grpc channel registry already closed");
        }
        String address = address(host, port);
        Channel channel = channels.get(address);
        if (channel != null) {
            return channel;
        }
        synchronized (this) {
            channel = channels.get(address);
            if (channel != null) {
                return channel;
            }
            return createChannel(address);
        }
    }

    private Channel createChannel(String address) {
        JQuickGrpcChannelPool pool = new JQuickGrpcChannelPool(address, clientConfig);
        try {
            ManagedChannel managedChannel = pool.borrowObject();
            Channel intercepted = ClientInterceptors.intercept(managedChannel, new JQuickGrpcClientInterceptor());
            channelPools.put(address, pool);
            managedChannels.put(address, managedChannel);
            channels.put(address, intercepted);
            console.info("Created jquick-grpc channel - target: " + address);
            return intercepted;
        } catch (Exception e) {
            pool.close();
            console.error("Failed to create jquick-grpc channel - target: " + address, e);
            throw new IllegalStateException("Failed to create jquick-grpc channel for " + address, e);
        }
    }

    /**
     * 设置单次调用超时，未开启全局超时时原样返回
     */
    @SuppressWarnings("unchecked")
    private <T extends AbstractStub<T>> T withDeadline(T stub) {
        if (defaultDeadlineMillis <= 0) {
            return stub;
        }
        return stub.withDeadlineAfter(defaultDeadlineMillis, TimeUnit.MILLISECONDS);
    }

    /**
     * 释放指定节点的通道（节点下线 / 被移除时调用）
     */
    public void release(String host, int port) {
        String address = address(host, port);
        Channel channel = channels.remove(address);
        ManagedChannel managedChannel = managedChannels.remove(address);
        JQuickGrpcChannelPool pool = channelPools.remove(address);
        shutdown(address, channel, managedChannel, pool);
    }

    /**
     * 释放指定节点的通道
     */
    public void release(WorkerEndpoint endpoint) {
        release(endpoint.getHost(), endpoint.getPort());
    }

    /**
     * 已建立的通道数量
     */
    public int size() {
        return channels.size();
    }

    public boolean isClosed() {
        return closed.get();
    }

    @Override
    public void close() {
        if (!closed.compareAndSet(false, true)) {
            return;
        }
        for (String address : channels.keySet()) {
            shutdown(address, channels.remove(address), managedChannels.remove(address), channelPools.remove(address));
        }
        channels.clear();
        managedChannels.clear();
        channelPools.clear();
        console.info("jquick-grpc channel registry closed");
    }

    private void shutdown(String address, Channel channel, ManagedChannel managedChannel, JQuickGrpcChannelPool pool) {
        if (managedChannel != null) {
            managedChannel.shutdown();
        }
        if (pool != null) {
            pool.close();
        }
        if (channel != null || managedChannel != null) {
            console.info("Released jquick-grpc channel - target: " + address);
        }
    }

    private static String address(String host, int port) {
        return host + ":" + port;
    }
}
