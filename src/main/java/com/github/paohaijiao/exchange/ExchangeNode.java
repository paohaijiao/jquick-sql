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
package com.github.paohaijiao.exchange;

import com.github.paohaijiao.expression.JQuickExpression;

/**
 * packageName com.github.paohaijiao.exchange
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/5/17
 */
public class ExchangeNode {
    private final ExchangeType type;
    private final PartitionStrategy partitionStrategy;
    private final JQuickExpression partitionKey;
    private final int parallelism;

    public enum ExchangeType {
        SHUFFLE,      // 哈希重分区
        BROADCAST,    // 广播
        FORWARD,      // 转发（保持分区）
        GATHER        // 收集到单节点
    }

    public enum PartitionStrategy {
        HASH,         // 哈希分区
        RANGE,        // 范围分区
        ROUND_ROBIN,  // 轮询
        REPLICATE     // 复制（广播）
    }

    public ExchangeNode(ExchangeType type, PartitionStrategy partitionStrategy,
                        JQuickExpression partitionKey, int parallelism) {
        this.type = type;
        this.partitionStrategy = partitionStrategy;
        this.partitionKey = partitionKey;
        this.parallelism = parallelism;
    }

    // Getters
    public ExchangeType getType() { return type; }
    public PartitionStrategy getPartitionStrategy() { return partitionStrategy; }
    public JQuickExpression getPartitionKey() { return partitionKey; }
    public int getParallelism() { return parallelism; }
}
