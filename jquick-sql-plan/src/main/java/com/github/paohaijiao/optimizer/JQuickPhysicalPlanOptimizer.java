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

import com.github.paohaijiao.enums.JQuickAggregateFunction;
import com.github.paohaijiao.enums.JQuickExchangeType;
import com.github.paohaijiao.enums.JQuickPartitionStrategy;
import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.physical.JQuickPhysicalPlanNode;
import com.github.paohaijiao.physical.node.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JQuickPhysicalPlanOptimizer {
    //倾斜检测阈值：某个值占比超过此比例视为倾斜
    private static final double SKEW_THRESHOLD = 0.3;

    // 倾斜处理时打散的桶数
    private static final int SALT_BUCKETS = 10;

    // 默认并行度
    private static final int DEFAULT_PARALLELISM = 4;

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
            JQuickHashJoinPhysicalNode.JoinDistribution distribution;
            if (leftSize < 1000 || rightSize < 1000) {
                distribution = JQuickHashJoinPhysicalNode.JoinDistribution.BROADCAST_HASH;
            } else if (leftSize < 10000 && rightSize < 10000) {
                distribution = JQuickHashJoinPhysicalNode.JoinDistribution.LOCAL;
            } else if (leftSize > 100000 || rightSize > 100000) {
                distribution = JQuickHashJoinPhysicalNode.JoinDistribution.PARTITIONED;
            } else {
                distribution = JQuickHashJoinPhysicalNode.JoinDistribution.SHUFFLE_HASH;
            }
            JQuickHashJoinPhysicalNode.BuildSide buildSide = leftSize < rightSize ? JQuickHashJoinPhysicalNode.BuildSide.LEFT : JQuickHashJoinPhysicalNode.BuildSide.RIGHT;
            return new JQuickHashJoinPhysicalNode(join.getJoinType(), join.getLeft(), join.getRight(), join.getCondition(), join.getJoinKeys(), buildSide, distribution);
        }
        for (int i = 0; i < node.getChildren().size(); i++) {
            JQuickPhysicalPlanNode optimized = optimizeJoins(node.getChildren().get(i));
            if (optimized != node.getChildren().get(i)) {
                return replaceChild(node, i, optimized);
            }
        }
        return node;
    }

    private JQuickPhysicalPlanNode optimizeAggregates(JQuickPhysicalPlanNode node) {
        if (node instanceof JQuickHashAggregatePhysicalNode) {
            JQuickHashAggregatePhysicalNode agg = (JQuickHashAggregatePhysicalNode) node;
            long dataSize = agg.getChild().getStats().getEstimatedRowCount();
            if (dataSize > 10000 && !agg.getGroupKeys().isEmpty()) {// 大数据量且有分组键，使用两阶段聚合
                return createTwoPhaseAggregate(agg);
            } else {// 小数据量，使用单阶段聚合
                return new JQuickHashAggregatePhysicalNode(agg.getGroupKeys(), agg.getAggregates(), agg.getChild(), agg.getHavingCondition(), JQuickHashAggregatePhysicalNode.AggregateStage.SINGLE);
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
        // 阶段1：局部聚合
        List<JQuickHashAggregatePhysicalNode.AggregateFunction> partialAggs = new ArrayList<>();
        for (JQuickHashAggregatePhysicalNode.AggregateFunction func : agg.getAggregates()) {
            String funcName = func.getFunctionName().toUpperCase();
            if (!JQuickAggregateFunction.isAggregateFunction(funcName)) {// 检查是否为支持的聚合函数
                partialAggs.add(func);
                continue;
            }
            JQuickAggregateFunction aggFunc = JQuickAggregateFunction.valueOf(funcName);
            switch (aggFunc) {
                case COUNT:
                    if (func.isCountStar()) {
                        partialAggs.add(new JQuickHashAggregatePhysicalNode.AggregateFunction("COUNT", null, false, func.getAlias() + "_partial", true, null, JQuickHashAggregatePhysicalNode.AggregateStage.PARTIAL));
                    } else if (func.isDistinct()) {
                        partialAggs.add(new JQuickHashAggregatePhysicalNode.AggregateFunction("COUNT", func.getArgument(), true, func.getAlias() + "_partial", false, null, JQuickHashAggregatePhysicalNode.AggregateStage.PARTIAL));
                    } else {
                        partialAggs.add(new JQuickHashAggregatePhysicalNode.AggregateFunction("SUM", func.getArgument(), false, func.getAlias() + "_partial", false, null, JQuickHashAggregatePhysicalNode.AggregateStage.PARTIAL));
                    }
                    break;
                case SUM:
                    partialAggs.add(new JQuickHashAggregatePhysicalNode.AggregateFunction("SUM", func.getArgument(), false, func.getAlias(), false, null, JQuickHashAggregatePhysicalNode.AggregateStage.PARTIAL));
                    break;
                case AVG:
                    partialAggs.add(new JQuickHashAggregatePhysicalNode.AggregateFunction("SUM", func.getArgument(), false, func.getAlias() + "_sum", false, null, JQuickHashAggregatePhysicalNode.AggregateStage.PARTIAL));
                    partialAggs.add(new JQuickHashAggregatePhysicalNode.AggregateFunction("COUNT", func.getArgument(), false, func.getAlias() + "_count", false, null, JQuickHashAggregatePhysicalNode.AggregateStage.PARTIAL));
                    break;

                case MAX:
                    partialAggs.add(new JQuickHashAggregatePhysicalNode.AggregateFunction("MAX", func.getArgument(), false, func.getAlias(), false, null, JQuickHashAggregatePhysicalNode.AggregateStage.PARTIAL));
                    break;
                case MIN:
                    partialAggs.add(new JQuickHashAggregatePhysicalNode.AggregateFunction("MIN", func.getArgument(), false, func.getAlias(), false, null, JQuickHashAggregatePhysicalNode.AggregateStage.PARTIAL));
                    break;

                case FIRST:
                    partialAggs.add(new JQuickHashAggregatePhysicalNode.AggregateFunction("FIRST", func.getArgument(), false, func.getAlias(), false, null, JQuickHashAggregatePhysicalNode.AggregateStage.PARTIAL));
                    break;

                case LAST:
                    partialAggs.add(new JQuickHashAggregatePhysicalNode.AggregateFunction("LAST", func.getArgument(), false, func.getAlias(), false, null, JQuickHashAggregatePhysicalNode.AggregateStage.PARTIAL));
                    break;
                case MEDIAN:
                case STDDEV:
                case VARIANCE:
                default:
                    partialAggs.add(func);
                    break;
            }
        }

        // 局部聚合节点
        JQuickHashAggregatePhysicalNode partialAgg = new JQuickHashAggregatePhysicalNode(agg.getGroupKeys(), partialAggs, agg.getChild(), null, JQuickHashAggregatePhysicalNode.AggregateStage.PARTIAL);
        // Shuffle 交换
        JQuickExchangePhysicalNode exchange = new JQuickExchangePhysicalNode(JQuickExchangeType.SHUFFLE, JQuickPartitionStrategy.HASH, agg.getGroupKeys(), 4, partialAgg);
        // 阶段2：全局聚合
        List<JQuickHashAggregatePhysicalNode.AggregateFunction> finalAggs = new ArrayList<>();
        for (JQuickHashAggregatePhysicalNode.AggregateFunction func : agg.getAggregates()) {
            String funcName = func.getFunctionName().toUpperCase();
            if (!JQuickAggregateFunction.isAggregateFunction(funcName)) {
                finalAggs.add(func);
                continue;
            }
            JQuickAggregateFunction aggFunc = JQuickAggregateFunction.valueOf(funcName);
            switch (aggFunc) {
                case COUNT:
                    finalAggs.add(new JQuickHashAggregatePhysicalNode.AggregateFunction(
                            "SUM", new com.github.paohaijiao.expression.domain.JQuickColumnRefExpression(func.getAlias() + "_partial"),
                            false, func.getAlias(), false, null, JQuickHashAggregatePhysicalNode.AggregateStage.FINAL));
                    break;
                case SUM:
                    finalAggs.add(new JQuickHashAggregatePhysicalNode.AggregateFunction(
                            "SUM", new com.github.paohaijiao.expression.domain.JQuickColumnRefExpression(func.getAlias()),
                            false, func.getAlias(), false, null, JQuickHashAggregatePhysicalNode.AggregateStage.FINAL));
                    break;
                case AVG:
                    finalAggs.add(new JQuickHashAggregatePhysicalNode.AggregateFunction("DIVIDE", null, false, func.getAlias(), false, null, JQuickHashAggregatePhysicalNode.AggregateStage.FINAL));
                    break;
                case MAX:
                    finalAggs.add(new JQuickHashAggregatePhysicalNode.AggregateFunction(
                            "MAX", new com.github.paohaijiao.expression.domain.JQuickColumnRefExpression(func.getAlias()),
                            false, func.getAlias(), false, null, JQuickHashAggregatePhysicalNode.AggregateStage.FINAL));
                    break;
                case MIN:
                    finalAggs.add(new JQuickHashAggregatePhysicalNode.AggregateFunction(
                            "MIN", new com.github.paohaijiao.expression.domain.JQuickColumnRefExpression(func.getAlias()),
                            false, func.getAlias(), false, null, JQuickHashAggregatePhysicalNode.AggregateStage.FINAL));
                    break;
                default:
                    finalAggs.add(new JQuickHashAggregatePhysicalNode.AggregateFunction(
                            func.getFunctionName(), func.getArgument(), func.isDistinct(), func.getAlias(),
                            func.isCountStar(), func.getSeparator(), JQuickHashAggregatePhysicalNode.AggregateStage.FINAL));
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
        if (node instanceof JQuickHashAggregatePhysicalNode) {
            JQuickHashAggregatePhysicalNode agg = (JQuickHashAggregatePhysicalNode) node;
            if (agg.getStage() == JQuickHashAggregatePhysicalNode.AggregateStage.FINAL) {
                return false;
            }
        }
        return node instanceof JQuickHashJoinPhysicalNode ||
                node instanceof JQuickHashAggregatePhysicalNode;
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
