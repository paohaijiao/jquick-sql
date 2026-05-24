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
package com.github.paohaijiao.physical.node;


import com.github.paohaijiao.enums.JQuickExchangeType;
import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.physical.JQuickPhysicalPlanNode;
import com.github.paohaijiao.physical.JQuickPhysicalPlanVisitor;
import com.github.paohaijiao.physical.domain.JQuickPhysicalColumn;
import com.github.paohaijiao.physical.domain.JQuickPhysicalStats;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class JQuickExchangePhysicalNode extends JQuickAbstractPhysicalNode {

    private final JQuickExchangeType exchangeType;

    private final PartitionStrategy partitionStrategy;

    private final List<JQuickExpression> partitionKeys;

    private final int targetParallelism;

    public enum PartitionStrategy {
        HASH, RANGE, ROUND_ROBIN, BUCKET, REPLICATE
    }

    public JQuickExchangePhysicalNode(JQuickExchangeType exchangeType, PartitionStrategy partitionStrategy, List<JQuickExpression> partitionKeys, int targetParallelism, JQuickPhysicalPlanNode child) {
        super(child);
        this.exchangeType = exchangeType;
        this.partitionStrategy = partitionStrategy;
        this.partitionKeys = partitionKeys != null ? new ArrayList<>(partitionKeys) : new ArrayList<>();
        this.targetParallelism = targetParallelism;
    }

    @Override
    public String getNodeType() {
        return "Exchange";
    }

    @Override
    public List<JQuickPhysicalColumn> getOutputSchema() {
        return children.get(0).getOutputSchema();
    }

    @Override
    public JQuickPhysicalStats getStats() {
        return children.get(0).getStats();
    }

    @Override
    public JQuickPhysicalPlanNode clone() {
        List<JQuickExpression> clonedKeys = partitionKeys.stream().map(JQuickExpression::clone).collect(Collectors.toList());
        return new JQuickExchangePhysicalNode(exchangeType, partitionStrategy, clonedKeys, targetParallelism, children.get(0).clone());
    }

    @Override
    public void accept(JQuickPhysicalPlanVisitor visitor) {
        visitor.visit(this);
    }

    public JQuickExchangeType getExchangeType() { return exchangeType; }

    public PartitionStrategy getPartitionStrategy() { return partitionStrategy; }

    public List<JQuickExpression> getPartitionKeys() { return partitionKeys; }

    public int getTargetParallelism() { return targetParallelism; }
}
