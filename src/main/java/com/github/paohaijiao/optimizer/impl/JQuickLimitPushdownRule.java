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
package com.github.paohaijiao.optimizer.impl;
import com.github.paohaijiao.enums.JQuickJoinType;
import com.github.paohaijiao.logic.JQuickLogicalPlanNode;
import com.github.paohaijiao.logic.domain.*;
import com.github.paohaijiao.optimizer.JQuickOptimizerRule;

import java.util.ArrayList;
import java.util.List;

/**
 * Limit下推：将Limit尽可能下推到数据源
 *
 * 优化场景：
 * 1. LIMIT + ORDER BY → Top-N 优化
 * 2. LIMIT 下推过 Project
 * 3. LIMIT 下推过 Filter
 * 4. 多层嵌套递归处理
 */
public class JQuickLimitPushdownRule implements JQuickOptimizerRule {

    @Override
    public JQuickLogicalPlanNode apply(JQuickLogicalPlanNode node) {
        node = processChildren(node);
        return pushdownLimit(node);
    }

    /**
     * 递归处理所有子节点
     */
    private JQuickLogicalPlanNode processChildren(JQuickLogicalPlanNode node) {
        List<JQuickLogicalPlanNode> children = node.getChildren();
        if (children == null || children.isEmpty()) {
            return node;
        }
        List<JQuickLogicalPlanNode> newChildren = new ArrayList<>();
        for (JQuickLogicalPlanNode child : children) {
            newChildren.add(apply(child));
        }
        return rebuildNode(node, newChildren);
    }

    /**
     * 根据原节点和优化后的子节点重建节点
     */
    private JQuickLogicalPlanNode rebuildNode(JQuickLogicalPlanNode node, List<JQuickLogicalPlanNode> newChildren) {
        if (node instanceof JQuickProjectNode) {
            JQuickProjectNode project = (JQuickProjectNode) node;
            return new JQuickProjectNode(project.getSelectItems(), newChildren.get(0), project.isDistinct());
        }
        if (node instanceof JQuickFilterNode) {
            JQuickFilterNode filter = (JQuickFilterNode) node;
            return new JQuickFilterNode(filter.getPredicate(), newChildren.get(0));
        }
        if (node instanceof JQuickSortNode) {
            JQuickSortNode sort = (JQuickSortNode) node;
            return new JQuickSortNode(sort.getOrderByItems(), newChildren.get(0));
        }
        if (node instanceof JQuickLimitNode) {
            JQuickLimitNode limit = (JQuickLimitNode) node;
            return new JQuickLimitNode(limit.getLimit(), limit.getOffset(), newChildren.get(0));
        }
        if (node instanceof JQuickJoinNode) {
            JQuickJoinNode join = (JQuickJoinNode) node;
            return new JQuickJoinNode(join.getJoinType(), newChildren.get(0), newChildren.get(1), join.getCondition(),join.getJoinKeys());
        }
        return node;
    }

    /**
     * Limit 下推的核心逻辑
     */
    private JQuickLogicalPlanNode pushdownLimit(JQuickLogicalPlanNode node) {
        if (node instanceof JQuickLimitNode) {
            JQuickLimitNode limit = (JQuickLimitNode) node;
            JQuickLogicalPlanNode child = limit.getChild();
            if (child instanceof JQuickSortNode) {
                JQuickSortNode sort = (JQuickSortNode) child;
                return new JQuickLimitNode(limit.getLimit(), limit.getOffset(), sort);
            }
            else if (child instanceof JQuickProjectNode) {
                JQuickProjectNode project = (JQuickProjectNode) child;
                JQuickLimitNode newLimit = new JQuickLimitNode(limit.getLimit(), limit.getOffset(), project.getChild());
                return new JQuickProjectNode(project.getSelectItems(), newLimit, project.isDistinct());
            }
            else if (child instanceof JQuickFilterNode) {
                JQuickFilterNode filter = (JQuickFilterNode) child;
                JQuickLimitNode newLimit = new JQuickLimitNode(limit.getLimit(), limit.getOffset(), filter.getChild());
                return new JQuickFilterNode(filter.getPredicate(), newLimit);
            }
            else if (child instanceof JQuickLimitNode) {
                JQuickLimitNode childLimit = (JQuickLimitNode) child;
                int newLimit = Math.min(limit.getLimit(), childLimit.getLimit());
                int newOffset = limit.getOffset() + childLimit.getOffset();
                return new JQuickLimitNode(newLimit, newOffset, childLimit.getChild());
            }
            else if (child instanceof JQuickJoinNode) {
                return pushdownLimitOverJoin(limit, (JQuickJoinNode) child);
            }
        }
        return node;
    }

    /**
     * LIMIT 下推过 JOIN
     *
     * 注意：LIMIT 下推过 JOIN 需要谨慎，因为 JOIN 可能会增加行数
     * 这里只做安全的优化：下推到 JOIN 的左侧（如果是 LEFT JOIN 或 INNER JOIN）
     */
    private JQuickLogicalPlanNode pushdownLimitOverJoin(JQuickLimitNode limit, JQuickJoinNode join) {
        JQuickJoinType joinType = join.getJoinType();
        if (joinType == JQuickJoinType.INNER || joinType == JQuickJoinType.LEFT) {
            JQuickLimitNode newLeftLimit = new JQuickLimitNode(limit.getLimit(), limit.getOffset(), join.getLeft());
            JQuickJoinNode newJoin = new JQuickJoinNode(joinType, newLeftLimit, join.getRight(), join.getCondition(),join.getJoinKeys());
            return new JQuickLimitNode(limit.getLimit(), limit.getOffset(), newJoin);
        }
        return limit;
    }
}