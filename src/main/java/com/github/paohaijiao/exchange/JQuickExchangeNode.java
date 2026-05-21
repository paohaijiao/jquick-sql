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

import com.github.paohaijiao.enums.JQuickExchangeType;
import com.github.paohaijiao.enums.JQuickPartitionStrategy;
import com.github.paohaijiao.expression.JQuickExpression;

import java.util.List;
import java.util.UUID;

/**
 * 数据交换节点 - 连接不同 Fragment 的数据通道
 */
public class JQuickExchangeNode {

    private final String exchangeId;

    private final JQuickExchangeType type;

    private final JQuickPartitionStrategy partitionStrategy;

    private final JQuickExpression partitionKey;

    private final List<JQuickExpression> partitionKeys;

    private final int parallelism;


    public JQuickExchangeNode(String exchangeId, JQuickExchangeType type, JQuickPartitionStrategy partitionStrategy, JQuickExpression partitionKey, int parallelism) {
        this(exchangeId, type, partitionStrategy, partitionKey, null, parallelism);
    }

    public JQuickExchangeNode(String exchangeId, JQuickExchangeType type, JQuickPartitionStrategy partitionStrategy, List<JQuickExpression> partitionKeys, int parallelism) {
        this(exchangeId, type, partitionStrategy, null, partitionKeys, parallelism);
    }

    private JQuickExchangeNode(String exchangeId, JQuickExchangeType type, JQuickPartitionStrategy partitionStrategy, JQuickExpression partitionKey, List<JQuickExpression> partitionKeys, int parallelism) {
        this.exchangeId = exchangeId != null ? exchangeId : UUID.randomUUID().toString();
        this.type = type;
        this.partitionStrategy = partitionStrategy;
        this.partitionKey = partitionKey;
        this.partitionKeys = partitionKeys;
        this.parallelism = parallelism;
    }

    public String getExchangeId() { return exchangeId; }

    public JQuickExchangeType getType() { return type; }

    public JQuickPartitionStrategy getPartitionStrategy() { return partitionStrategy; }

    public JQuickExpression getPartitionKey() { return partitionKey; }

    public List<JQuickExpression> getPartitionKeys() { return partitionKeys; }

    public int getParallelism() { return parallelism; }

    public boolean isInput() { return type == JQuickExchangeType.RECEIVE; }

    public boolean isOutput() { return type != JQuickExchangeType.RECEIVE; }

    @Override
    public String toString() {
        return String.format("ExchangeNode{id=%s, type=%s, strategy=%s, parallelism=%d}", exchangeId, type, partitionStrategy, parallelism);
    }
}