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
import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.logic.JQuickLogicalPlanNode;
import com.github.paohaijiao.logic.domain.*;
import com.github.paohaijiao.optimizer.JQuickOptimizerRule;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 聚合下推：将聚合操作下推到数据源，减少Join等操作的数据量
 * 优化场景：
 * 1. 聚合下推到Join的一侧 - 先聚合再Join
 * 2. 聚合和Filter交换 - 先过滤再聚合
 * 优化前：Join → GroupBy
 * 优化后：GroupBy (left) → Join
 */
public class JQuickAggregatePushdownRule implements JQuickOptimizerRule {
    @Override
    public JQuickLogicalPlanNode apply(JQuickLogicalPlanNode node) {
        if (!(node instanceof JQuickGroupByNode)) {
            return node;
        }
        JQuickGroupByNode groupBy = (JQuickGroupByNode) node;
        JQuickLogicalPlanNode child = groupBy.getChild();
        if (!(child instanceof JQuickJoinNode)) {
            return node;
        }
        JQuickJoinNode join = (JQuickJoinNode) child;
        if (join.getJoinType() != JQuickJoinType.INNER) {
            return node;
        }
        return tryPushdown(groupBy, join);
    }

    /**
     * 尝试下推聚合
     */
    private JQuickLogicalPlanNode tryPushdown(JQuickGroupByNode groupBy, JQuickJoinNode join) {
        Set<String> leftColumns = collectAllColumns(join.getLeft());
        Set<String> rightColumns = collectAllColumns(join.getRight());
        Set<String> requiredColumns = getRequiredColumns(groupBy);
        if (leftColumns.containsAll(requiredColumns) && !hasIntersection(rightColumns, requiredColumns)) {
            return pushToLeft(groupBy, join);
        }
        if (rightColumns.containsAll(requiredColumns) && !hasIntersection(leftColumns, requiredColumns)) {
            return pushToRight(groupBy, join);
        }
        return groupBy;
    }

    /**
     * 检查两个集合是否有交集
     */
    private boolean hasIntersection(Set<String> set1, Set<String> set2) {
        for (String item : set2) {
            if (set1.contains(item)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 下推到左表
     */
    private JQuickLogicalPlanNode pushToLeft(JQuickGroupByNode groupBy, JQuickJoinNode join) {
        JQuickGroupByNode leftAgg = new JQuickGroupByNode(copyGroupKeys(groupBy.getGroupKeys()), copyAggregateItems(groupBy.getAggregateItems()), join.getLeft(), copyHaving(groupBy.getHavingCondition()));
        return new JQuickJoinNode(JQuickJoinType.INNER, leftAgg, join.getRight(), copyCondition(join.getCondition()),join.getJoinKeys());
    }

    /**
     * 下推到右表
     */
    private JQuickLogicalPlanNode pushToRight(JQuickGroupByNode groupBy, JQuickJoinNode join) {
        JQuickGroupByNode rightAgg = new JQuickGroupByNode(copyGroupKeys(groupBy.getGroupKeys()), copyAggregateItems(groupBy.getAggregateItems()), join.getRight(), copyHaving(groupBy.getHavingCondition()));
        return new JQuickJoinNode(JQuickJoinType.INNER, join.getLeft(), rightAgg, copyCondition(join.getCondition()),join.getJoinKeys());
    }

    /**
     * 收集节点中的所有列名
     */
    private Set<String> collectAllColumns(JQuickLogicalPlanNode node) {
        Set<String> columns = new HashSet<>();
        if (node instanceof JQuickTableScanNode) {
            JQuickTableScanNode scan = (JQuickTableScanNode) node;
            if (scan.getRequiredColumns() != null) {
                columns.addAll(scan.getRequiredColumns());
            }
        }
        else if (node instanceof JQuickProjectNode) {
            JQuickProjectNode project = (JQuickProjectNode) node;
            for (JQuickProjectNode.SelectItem item : project.getSelectItems()) {
                if (item.getExpression() != null) {
                    columns.addAll(item.getExpression().getReferencedColumns());
                }
            }
            columns.addAll(collectAllColumns(project.getChild()));
        }
        else if (node instanceof JQuickFilterNode) {
            JQuickFilterNode filter = (JQuickFilterNode) node;
            if (filter.getPredicate() != null) {
                columns.addAll(filter.getPredicate().getReferencedColumns());
            }
            columns.addAll(collectAllColumns(filter.getChild()));
        }
        else if (node instanceof JQuickJoinNode) {
            JQuickJoinNode join = (JQuickJoinNode) node;
            columns.addAll(collectAllColumns(join.getLeft()));
            columns.addAll(collectAllColumns(join.getRight()));
            if (join.getCondition() != null) {
                columns.addAll(join.getCondition().getReferencedColumns());
            }
        }
        return columns;
    }

    /**
     * 获取聚合节点需要的所有列
     */
    private Set<String> getRequiredColumns(JQuickGroupByNode groupBy) {
        Set<String> columns = new HashSet<>();
        for (JQuickExpression key : groupBy.getGroupKeys()) {
            columns.addAll(key.getReferencedColumns());
        }
        for (JQuickGroupByNode.AggregateItem item : groupBy.getAggregateItems()) {
            if (item.getExpression() != null) {
                columns.addAll(item.getExpression().getReferencedColumns());
            }
        }
        if (groupBy.getHavingCondition() != null) {
            columns.addAll(groupBy.getHavingCondition().getReferencedColumns());
        }
        return columns;
    }

    /**
     * 复制分组键
     */
    private List<JQuickExpression> copyGroupKeys(List<JQuickExpression> keys) {
        List<JQuickExpression> copied = new ArrayList<>();
        for (JQuickExpression key : keys) {
            copied.add(key.clone());
        }
        return copied;
    }

    /**
     * 复制聚合项
     */
    private List<JQuickGroupByNode.AggregateItem> copyAggregateItems(List<JQuickGroupByNode.AggregateItem> items) {
        List<JQuickGroupByNode.AggregateItem> copied = new ArrayList<>();
        for (JQuickGroupByNode.AggregateItem item : items) {
            copied.add(item.clone());
        }
        return copied;
    }

    /**
     * 复制 HAVING 条件
     */
    private JQuickExpression copyHaving(JQuickExpression having) {
        return having != null ? having.clone() : null;
    }

    /**
     * 复制 JOIN 条件
     */
    private JQuickExpression copyCondition(JQuickExpression condition) {
        return condition != null ? condition.clone() : null;
    }
}
