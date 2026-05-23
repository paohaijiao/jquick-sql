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

import com.github.paohaijiao.enums.JQuickBinaryOperator;
import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.expression.domain.JQuickBinaryExpression;
import com.github.paohaijiao.logic.JQuickLogicalPlanNode;
import com.github.paohaijiao.logic.domain.JQuickFilterNode;
import com.github.paohaijiao.logic.domain.JQuickJoinNode;
import com.github.paohaijiao.logic.domain.JQuickProjectNode;
import com.github.paohaijiao.logic.domain.JQuickTableScanNode;
import com.github.paohaijiao.optimizer.JQuickOptimizerRule;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 谓词下推：尽早过滤数据，减少后续处理的数据量
 * 优化前：先读取所有数据 → 再过滤
 * 优化后：先过滤数据 → 只读取需要的数据
 * SELECT * FROM (
 *     SELECT * FROM orders WHERE status = 'PAID'  -- 内层过滤
 * ) t
 * WHERE amount > 1000  -- 外层过滤
 * -- 谓词下推后
 * SELECT * FROM orders
 * WHERE status = 'PAID' AND amount > 1000  -- 合并到内层
 */
public class JQuickPredicatePushdownRule implements JQuickOptimizerRule {
    @Override
    public JQuickLogicalPlanNode apply(JQuickLogicalPlanNode node) {
        if (node instanceof JQuickFilterNode) {
            JQuickFilterNode filter = (JQuickFilterNode) node;
            JQuickLogicalPlanNode child = filter.getChild();
            JQuickExpression predicate = filter.getPredicate();
            if (child instanceof JQuickTableScanNode) {
                JQuickTableScanNode scan = (JQuickTableScanNode) child;
                // 将过滤条件下推到表扫描节点
                JQuickExpression existingFilter = scan.getFilterPredicate();
                JQuickExpression combined = existingFilter != null ? new JQuickBinaryExpression(existingFilter, predicate, JQuickBinaryOperator.AND) : predicate;
                return new JQuickTableScanNode(scan.getTableName(), scan.getAlias(), scan.getRequiredColumns(), combined);
            } else if (child instanceof JQuickProjectNode) {
                // 投影和过滤交换（如果过滤列都在投影中）
                JQuickProjectNode project = (JQuickProjectNode) child;
                if (canPushdown(predicate, project)) {
                    return new JQuickProjectNode(project.getSelectItems(), new JQuickFilterNode(predicate, project.getChild()), project.isDistinct());
                }
            } else if (child instanceof JQuickJoinNode) {
                // 将过滤条件推入Join
                return pushFilterIntoJoin(filter, (JQuickJoinNode) child);
            }
        }
        return node;
    }

    private boolean canPushdown(JQuickExpression predicate, JQuickProjectNode project) {
        Set<String> projectColumns = project.getSelectItems().stream().map(JQuickProjectNode.SelectItem::getAlias).collect(Collectors.toSet());
        return projectColumns.containsAll(predicate.getReferencedColumns());
    }

    private JQuickLogicalPlanNode pushFilterIntoJoin(JQuickFilterNode filter, JQuickJoinNode join) {
        JQuickExpression predicate = filter.getPredicate();
        Set<String> leftColumns = getColumnNames(join.getLeft());
        Set<String> rightColumns = getColumnNames(join.getRight());
        // 分离过滤条件
        List<JQuickExpression> leftFilters = new ArrayList<>();
        List<JQuickExpression> rightFilters = new ArrayList<>();
        List<JQuickExpression> joinFilters = new ArrayList<>();
        splitPredicate(predicate, leftColumns, rightColumns, leftFilters, rightFilters, joinFilters);
        JQuickLogicalPlanNode newLeft = join.getLeft();
        JQuickLogicalPlanNode newRight = join.getRight();
        if (!leftFilters.isEmpty()) {
            JQuickExpression leftFilter = combinePredicates(leftFilters);
            newLeft = new JQuickFilterNode(leftFilter, newLeft);
        }
        if (!rightFilters.isEmpty()) {
            JQuickExpression rightFilter = combinePredicates(rightFilters);
            newRight = new JQuickFilterNode(rightFilter, newRight);
        }
        JQuickExpression joinFilter = combinePredicates(joinFilters);
        JQuickJoinNode newJoin = new JQuickJoinNode(join.getJoinType(), newLeft, newRight, joinFilter);
        return newJoin;
    }

    private void splitPredicate(JQuickExpression expr, Set<String> leftCols, Set<String> rightCols, List<JQuickExpression> leftFilters, List<JQuickExpression> rightFilters, List<JQuickExpression> joinFilters) {
        if (expr instanceof JQuickBinaryExpression && ((JQuickBinaryExpression) expr).getOperator() == JQuickBinaryOperator.AND) {
            JQuickBinaryExpression binary = (JQuickBinaryExpression) expr;
            splitPredicate(binary.getLeft(), leftCols, rightCols, leftFilters, rightFilters, joinFilters);
            splitPredicate(binary.getRight(), leftCols, rightCols, leftFilters, rightFilters, joinFilters);
            return;
        }
        Set<String> referenced = new HashSet<>(expr.getReferencedColumns());
        if (referenced.stream().allMatch(leftCols::contains)) {
            leftFilters.add(expr);
        } else if (referenced.stream().allMatch(rightCols::contains)) {
            rightFilters.add(expr);
        } else {
            joinFilters.add(expr);
        }
    }

    private JQuickExpression combinePredicates(List<JQuickExpression> predicates) {
        if (predicates.isEmpty()) return null;
        if (predicates.size() == 1) return predicates.get(0);
        JQuickExpression result = predicates.get(0);
        for (int i = 1; i < predicates.size(); i++) {
            result = new JQuickBinaryExpression(result, predicates.get(i), JQuickBinaryOperator.AND);
        }
        return result;
    }

    private Set<String> getColumnNames(JQuickLogicalPlanNode node) {
        Set<String> columns = new HashSet<>();
        if (node instanceof JQuickTableScanNode) {
            // 可以从表元数据获取
        } else if (node instanceof JQuickProjectNode) {
            JQuickProjectNode project = (JQuickProjectNode) node;
            for (JQuickProjectNode.SelectItem item : project.getSelectItems()) {
                columns.add(item.getAlias());
            }
        }
        return columns;
    }
}
