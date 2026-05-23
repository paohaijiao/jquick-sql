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

import com.github.paohaijiao.logic.JQuickLogicalPlanNode;
import com.github.paohaijiao.logic.domain.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * packageName com.github.paohaijiao.optimizer
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/5/23
 */
public abstract class JQuickRecursiveOptimizerRule implements JQuickOptimizerRule{
    @Override
    public JQuickLogicalPlanNode apply(JQuickLogicalPlanNode node) {
        JQuickLogicalPlanNode optimizedNode = optimizeChildren(node);
        return optimizeNode(optimizedNode);
    }

    /**
     * 递归优化所有子节点
     */
    private JQuickLogicalPlanNode optimizeChildren(JQuickLogicalPlanNode node) {
        List<JQuickLogicalPlanNode> children = node.getChildren();
        if (children == null || children.isEmpty()) {
            return node;
        }
        List<JQuickLogicalPlanNode> optimizedChildren = new ArrayList<>();
        for (JQuickLogicalPlanNode child : children) {
            optimizedChildren.add(apply(child));
        }
        return rebuildWithChildren(node, optimizedChildren);
    }

    /**
     * 根据原节点和优化后的子节点重建节点
     */
    protected JQuickLogicalPlanNode rebuildWithChildren(JQuickLogicalPlanNode node, List<JQuickLogicalPlanNode> newChildren) {
        if (node instanceof JQuickFilterNode) {
            JQuickFilterNode filter = (JQuickFilterNode) node;
            return new JQuickFilterNode(filter.getPredicate(), newChildren.get(0));
        }
        if (node instanceof JQuickProjectNode) {
            JQuickProjectNode project = (JQuickProjectNode) node;
            return new JQuickProjectNode(project.getSelectItems(), newChildren.get(0), project.isDistinct());
        }
        if (node instanceof JQuickGroupByNode) {
            JQuickGroupByNode groupBy = (JQuickGroupByNode) node;
            return new JQuickGroupByNode(groupBy.getGroupKeys(), groupBy.getAggregateItems(), newChildren.get(0), groupBy.getHavingCondition());
        }
        if (node instanceof JQuickJoinNode) {
            JQuickJoinNode join = (JQuickJoinNode) node;
            return new JQuickJoinNode(join.getJoinType(), newChildren.get(0), newChildren.get(1), join.getCondition());
        }
        if (node instanceof JQuickSortNode) {
            JQuickSortNode sort = (JQuickSortNode) node;
            return new JQuickSortNode(sort.getOrderByItems(), newChildren.get(0));
        }
        if (node instanceof JQuickLimitNode) {
            JQuickLimitNode limit = (JQuickLimitNode) node;
            return new JQuickLimitNode(limit.getLimit(), limit.getOffset(), newChildren.get(0));
        }
        if (node instanceof JQuickWindowNode) {
            JQuickWindowNode window = (JQuickWindowNode) node;
            return new JQuickWindowNode(window.getWindowFunctions(), newChildren.get(0));
        }
        if (node instanceof JQuickSetOperationNode) {
            JQuickSetOperationNode setOp = (JQuickSetOperationNode) node;
            return new JQuickSetOperationNode(setOp.getOperationType(), newChildren.get(0), newChildren.get(1));
        }
        if (node instanceof JQuickWithNode) {
            JQuickWithNode with = (JQuickWithNode) node;
            Map<String, JQuickLogicalPlanNode> newCtes = new LinkedHashMap<>();
            int cteCount = with.getCtes().size();
            int idx = 0;
            for (String cteName : with.getCtes().keySet()) {
                newCtes.put(cteName, newChildren.get(idx++));
            }
            return new JQuickWithNode(newChildren.get(newChildren.size() - 1), newCtes);
        }
        if (node instanceof JQuickAggregateNode) {
            JQuickAggregateNode agg = (JQuickAggregateNode) node;
            if (agg.getGroupKeys() != null) {
                return new JQuickAggregateNode(agg.getGroupKeys(), agg.getAggregates(), newChildren.get(0), agg.getHavingCondition(), agg.isDistinct());
            } else {
                return new JQuickAggregateNode(agg.getGroupingSets(), agg.getAggregates(), newChildren.get(0), agg.getHavingCondition());
            }
        }
        if (node instanceof JQuickRecursiveUnionNode) {
            JQuickRecursiveUnionNode ru = (JQuickRecursiveUnionNode) node;
            return new JQuickRecursiveUnionNode(ru.getCteName(), ru.getColumnNames(),
                    newChildren.get(0), newChildren.get(1), ru.isUnionAll());
        }
        return node;
    }

    /**
     * 优化当前节点（子类实现）
     */
    protected abstract JQuickLogicalPlanNode optimizeNode(JQuickLogicalPlanNode node);
}
