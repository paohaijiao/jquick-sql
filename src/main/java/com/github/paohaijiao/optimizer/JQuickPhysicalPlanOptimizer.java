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
package com.github.paohaijiao.optimizer;

import com.github.paohaijiao.enums.JQuickExchangeType;
import com.github.paohaijiao.enums.JQuickPartitionStrategy;
import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.physical.JQuickPhysicalPlanNode;
import com.github.paohaijiao.physical.node.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JQuickPhysicalPlanOptimizer {

    public JQuickPhysicalPlanNode optimize(JQuickPhysicalPlanNode plan) {
        JQuickPhysicalPlanNode current = plan;
        current = optimizeJoins(current);
        current = optimizeAggregates(current);
        current = pushdownLimit(current);
        current = addRequiredExchanges(current);
        return current;
    }

    private JQuickPhysicalPlanNode optimizeJoins(JQuickPhysicalPlanNode node) {
        if (node instanceof JQuickHashJoinPhysicalNode) {
            JQuickHashJoinPhysicalNode join = (JQuickHashJoinPhysicalNode) node;
            long leftSize = join.getLeft().getStats().getEstimatedRowCount();
            long rightSize = join.getRight().getStats().getEstimatedRowCount();

            if (leftSize < 100 || rightSize < 100) {
                return new JQuickHashJoinPhysicalNode(
                        join.getJoinType(),
                        join.getLeft(), join.getRight(),
                        join.getCondition(),
                        join.getJoinKeys(),
                        join.getBuildSide(),
                        JQuickHashJoinPhysicalNode.JoinDistribution.BROADCAST_HASH
                );
            }
        }

        for (int i = 0; i < node.getChildren().size(); i++) {
            JQuickPhysicalPlanNode optimized = optimizeJoins(node.getChildren().get(i));
            if (optimized != node.getChildren().get(i)) {
                // 需要替换子节点，由于不可变设计，这里返回新节点
                return replaceChild(node, i, optimized);
            }
        }
        return node;
    }

    private JQuickPhysicalPlanNode optimizeAggregates(JQuickPhysicalPlanNode node) {
        if (node instanceof JQuickHashAggregatePhysicalNode) {
            JQuickHashAggregatePhysicalNode agg = (JQuickHashAggregatePhysicalNode) node;
            long dataSize = agg.getChild().getStats().getEstimatedRowCount();

            if (dataSize > 10000 && !agg.getGroupKeys().isEmpty()) {
                return createTwoPhaseAggregate(agg);
            }
        }

        for (int i = 0; i < node.getChildren().size(); i++) {
            JQuickPhysicalPlanNode optimized = optimizeAggregates(node.getChildren().get(i));
            if (optimized != node.getChildren().get(i)) {
                return replaceChild(node, i, optimized);
            }
        }
        return node;
    }

    private JQuickPhysicalPlanNode createTwoPhaseAggregate(JQuickHashAggregatePhysicalNode agg) {
        List<JQuickHashAggregatePhysicalNode.AggregateFunction> partialAggs = new ArrayList<>();
        for (JQuickHashAggregatePhysicalNode.AggregateFunction func : agg.getAggregates()) {
            switch (func.getFunctionName().toLowerCase()) {
                case "count":
                    partialAggs.add(new JQuickHashAggregatePhysicalNode.AggregateFunction("sum", func.getArgument(), false, func.getAlias() + "_partial", false, null, JQuickHashAggregatePhysicalNode.AggregateStage.PARTIAL));
                    break;
                case "sum":
                    partialAggs.add(new JQuickHashAggregatePhysicalNode.AggregateFunction("sum", func.getArgument(), false, func.getAlias(), false, null, JQuickHashAggregatePhysicalNode.AggregateStage.PARTIAL));
                    break;
                case "avg":
                    partialAggs.add(new JQuickHashAggregatePhysicalNode.AggregateFunction("sum", func.getArgument(), false, func.getAlias() + "_sum", false, null, JQuickHashAggregatePhysicalNode.AggregateStage.PARTIAL));
                    partialAggs.add(new JQuickHashAggregatePhysicalNode.AggregateFunction("count", func.getArgument(), false, func.getAlias() + "_count", false, null, JQuickHashAggregatePhysicalNode.AggregateStage.PARTIAL));
                    break;
                default:
                    partialAggs.add(func);
            }
        }
        JQuickHashAggregatePhysicalNode partialAgg = new JQuickHashAggregatePhysicalNode(agg.getGroupKeys(), partialAggs, agg.getChild(), null, JQuickHashAggregatePhysicalNode.AggregateStage.PARTIAL);
        JQuickExchangePhysicalNode exchange = new JQuickExchangePhysicalNode(JQuickExchangeType.SHUFFLE, JQuickPartitionStrategy.HASH, agg.getGroupKeys(), 4, partialAgg);
        List<JQuickHashAggregatePhysicalNode.AggregateFunction> finalAggs = new ArrayList<>();
        for (JQuickHashAggregatePhysicalNode.AggregateFunction func : agg.getAggregates()) {
            if (func.getFunctionName().equalsIgnoreCase("avg")) {
                finalAggs.add(new JQuickHashAggregatePhysicalNode.AggregateFunction("divide", null, false, func.getAlias(), false, null, JQuickHashAggregatePhysicalNode.AggregateStage.FINAL));
            } else {
                finalAggs.add(func);
            }
        }

        return new JQuickHashAggregatePhysicalNode(agg.getGroupKeys(), finalAggs, exchange, agg.getHavingCondition(), JQuickHashAggregatePhysicalNode.AggregateStage.FINAL);
    }

    private JQuickPhysicalPlanNode pushdownLimit(JQuickPhysicalPlanNode node) {
        if (node instanceof JQuickLimitPhysicalNode) {
            JQuickLimitPhysicalNode limit = (JQuickLimitPhysicalNode) node;
            JQuickPhysicalPlanNode child = limit.getChildren().get(0);
            if (child instanceof JQuickSortPhysicalNode) {
                JQuickSortPhysicalNode sort = (JQuickSortPhysicalNode) child;
                return new JQuickTopNPhysicalNode(sort.getOrderByItems(), limit.getLimit(), limit.getOffset(), sort.getChildren().get(0));
            }
        }

        for (int i = 0; i < node.getChildren().size(); i++) {
            JQuickPhysicalPlanNode optimized = pushdownLimit(node.getChildren().get(i));
            if (optimized != node.getChildren().get(i)) {
                return replaceChild(node, i, optimized);
            }
        }
        return node;
    }

    private JQuickPhysicalPlanNode addRequiredExchanges(JQuickPhysicalPlanNode node) {
        if (needsExchange(node)) {
            int parallelism = 4;
            JQuickExchangePhysicalNode exchange = new JQuickExchangePhysicalNode(JQuickExchangeType.SHUFFLE, JQuickPartitionStrategy.HASH, extractPartitionKeys(node), parallelism, node);
            return exchange;
        }

        for (int i = 0; i < node.getChildren().size(); i++) {
            JQuickPhysicalPlanNode optimized = addRequiredExchanges(node.getChildren().get(i));
            if (optimized != node.getChildren().get(i)) {
                return replaceChild(node, i, optimized);
            }
        }
        return node;
    }

    private boolean needsExchange(JQuickPhysicalPlanNode node) {
        return node instanceof JQuickHashJoinPhysicalNode || node instanceof JQuickHashAggregatePhysicalNode;
    }

    private List<JQuickExpression> extractPartitionKeys(JQuickPhysicalPlanNode node) {
        if (node instanceof JQuickHashJoinPhysicalNode) {
            JQuickHashJoinPhysicalNode join = (JQuickHashJoinPhysicalNode) node;
            if (!join.getJoinKeys().isEmpty()) {
                return Collections.singletonList(join.getJoinKeys().get(0).getLeftKey());
            }
        } else if (node instanceof JQuickHashAggregatePhysicalNode) {
            JQuickHashAggregatePhysicalNode agg = (JQuickHashAggregatePhysicalNode) node;
            return agg.getGroupKeys();
        }
        return new ArrayList<>();
    }

    private JQuickPhysicalPlanNode replaceChild(JQuickPhysicalPlanNode parent, int childIndex, JQuickPhysicalPlanNode newChild) {
        if (parent instanceof JQuickFilterPhysicalNode) {
            JQuickFilterPhysicalNode filter = (JQuickFilterPhysicalNode) parent;
            return new JQuickFilterPhysicalNode(filter.getPredicate(), newChild);
        } else if (parent instanceof JQuickProjectPhysicalNode) {
            JQuickProjectPhysicalNode project = (JQuickProjectPhysicalNode) parent;
            return new JQuickProjectPhysicalNode(project.getSelectItems(), newChild, project.isDistinct());
        } else if (parent instanceof JQuickHashJoinPhysicalNode) {
            JQuickHashJoinPhysicalNode join = (JQuickHashJoinPhysicalNode) parent;
            JQuickPhysicalPlanNode left = childIndex == 0 ? newChild : join.getLeft();
            JQuickPhysicalPlanNode right = childIndex == 1 ? newChild : join.getRight();
            return new JQuickHashJoinPhysicalNode(join.getJoinType(), left, right, join.getCondition(), join.getJoinKeys(), join.getBuildSide(), join.getDistribution());
        } else if (parent instanceof JQuickHashAggregatePhysicalNode) {
            JQuickHashAggregatePhysicalNode agg = (JQuickHashAggregatePhysicalNode) parent;
            return new JQuickHashAggregatePhysicalNode(agg.getGroupKeys(), agg.getAggregates(), newChild, agg.getHavingCondition(), agg.getStage());
        } else if (parent instanceof JQuickSortPhysicalNode) {
            JQuickSortPhysicalNode sort = (JQuickSortPhysicalNode) parent;
            return new JQuickSortPhysicalNode(sort.getOrderByItems(), newChild);
        } else if (parent instanceof JQuickLimitPhysicalNode) {
            JQuickLimitPhysicalNode limit = (JQuickLimitPhysicalNode) parent;
            return new JQuickLimitPhysicalNode(limit.getLimit(), limit.getOffset(), newChild);
        }
        return parent;
    }
}
