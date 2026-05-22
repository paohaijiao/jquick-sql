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

/**
 * 数据交换通道
 */
public class JQuickExchangeChannel {

    private final String channelId;

    private final String sourceWorker;

    private final int sourcePort;

    private final String targetWorker;

    private final int targetPort;

    private final ChannelType type;

    public enum ChannelType {
        MEMORY,     // 内存通道（同进程内）
        NETTY,      // Netty 网络通道
        GRPC,       // gRPC 通道
        FILE        // 文件通道（溢写）
    }

    public JQuickExchangeChannel(String channelId, String sourceWorker, int sourcePort, String targetWorker, int targetPort, ChannelType type) {
        this.channelId = channelId;
        this.sourceWorker = sourceWorker;
        this.sourcePort = sourcePort;
        this.targetWorker = targetWorker;
        this.targetPort = targetPort;
        this.type = type;
    }

    public String getChannelId() { return channelId; }

    public String getSourceWorker() { return sourceWorker; }

    public int getSourcePort() { return sourcePort; }

    public String getTargetWorker() { return targetWorker; }

    public int getTargetPort() { return targetPort; }

    public ChannelType getType() { return type; }

    public boolean isLocal() {
        return sourceWorker != null && sourceWorker.equals(targetWorker);
    }
}
