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

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 数据交换服务 - 负责 Task 之间的数据传输
 */
public class DataExchangeService {

    private final int port;

    private final Map<String, BlockingQueue<Object>> channels;

    private final ExecutorService nettyWorker;

    private volatile boolean running;

    public DataExchangeService(int port) {
        this.port = port;
        this.channels = new ConcurrentHashMap<>();
        this.nettyWorker = Executors.newCachedThreadPool();
    }

    public void start() {
        running = true;
        System.out.println("Data exchange service started on port " + port);
        // 启动 Netty 服务（简化实现）
    }

    /**
     * 发送数据
     */
    public void send(JQuickExchangeChannel channel, Object data) {
        String channelId = channel.getChannelId();
        if (channel.isLocal()) {
            // 本地传输：直接放入队列
            BlockingQueue<Object> queue = channels.computeIfAbsent(channelId,
                    k -> new LinkedBlockingQueue<>());
            queue.offer(data);
        } else {
            // 远程传输：通过网络发送
            sendRemote(channel, data);
        }
    }

    /**
     * 接收数据
     */
    public Iterator<Object> receive(JQuickExchangeChannel channel) {
        String channelId = channel.getChannelId();
        if (channel.isLocal()) {
            // 本地接收：从队列读取
            BlockingQueue<Object> queue = channels.computeIfAbsent(channelId, k -> new LinkedBlockingQueue<>());
            return new QueueIterator(queue);
        } else {
            // 远程接收：通过网络接收
            return receiveRemote(channel);
        }
    }

    /**
     * 标记输出完成
     */
    public void complete(JQuickTaskOutput output) {
        for (JQuickExchangeChannel channel : output.getChannels()) {
            String channelId = channel.getChannelId();
            BlockingQueue<Object> queue = channels.get(channelId);
            if (queue != null) {
                queue.offer(END_MARKER);
            }
        }
    }

    private void sendRemote(JQuickExchangeChannel channel, Object data) {
        // 通过网络发送数据（使用 Netty）
        nettyWorker.submit(() -> {
            // 伪代码：序列化并发送
        });
    }

    private Iterator<Object> receiveRemote(JQuickExchangeChannel channel) {
        // 通过网络接收数据
        return new RemoteIterator(channel);
    }

    private static final Object END_MARKER = new Object();

    private static class QueueIterator implements Iterator<Object> {
        private final BlockingQueue<Object> queue;
        private Object next;

        QueueIterator(BlockingQueue<Object> queue) {
            this.queue = queue;
            advance();
        }

        private void advance() {
            try {
                next = queue.poll(100, TimeUnit.MILLISECONDS);
                if (next == END_MARKER) {
                    next = null;
                }
            } catch (InterruptedException e) {
                next = null;
            }
        }

        @Override
        public boolean hasNext() { return next != null; }

        @Override
        public Object next() {
            Object result = next;
            advance();
            return result;
        }
    }

    private static class RemoteIterator implements Iterator<Object> {
        private final JQuickExchangeChannel channel;

        RemoteIterator(JQuickExchangeChannel channel) {
            this.channel = channel;
        }

        @Override
        public boolean hasNext() { return false; }

        @Override
        public Object next() { return null; }
    }

    public void stop() {
        running = false;
        nettyWorker.shutdown();
    }
}
