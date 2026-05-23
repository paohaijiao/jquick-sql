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

import com.github.paohaijiao.logic.JQuickLogicalPlanNode;
import com.github.paohaijiao.logic.domain.JQuickJoinNode;
import com.github.paohaijiao.logic.domain.JQuickProjectNode;
import com.github.paohaijiao.logic.domain.JQuickTableScanNode;
import com.github.paohaijiao.optimizer.JQuickOptimizerRule;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 投影下推：只保留需要的列，减少数据传输
 */
public class JQuickProjectionPushdownRule implements JQuickOptimizerRule {
    @Override
    public JQuickLogicalPlanNode apply(JQuickLogicalPlanNode node) {
        if (node instanceof JQuickProjectNode) {
            JQuickProjectNode project = (JQuickProjectNode) node;
            Set<String> requiredColumns = project.getSelectItems().stream()
                    .map(JQuickProjectNode.SelectItem::getAlias)
                    .collect(Collectors.toSet());
            return pushdownProjection(project, requiredColumns);
        }
        return node;
    }

    private JQuickLogicalPlanNode pushdownProjection(JQuickProjectNode project, Set<String> requiredColumns) {
        JQuickLogicalPlanNode child = project.getChild();
        if (child instanceof JQuickTableScanNode) {
            JQuickTableScanNode scan = (JQuickTableScanNode) child;
            return new JQuickProjectNode(project.getSelectItems(),
                    new JQuickTableScanNode(scan.getTableName(), scan.getAlias(), requiredColumns, scan.getFilterPredicate()),
                    project.isDistinct());
        } else if (child instanceof JQuickProjectNode) {
            // 合并投影
            JQuickProjectNode childProject = (JQuickProjectNode) child;
            return new JQuickProjectNode(project.getSelectItems(), childProject.getChild(), project.isDistinct());
        } else if (child instanceof JQuickJoinNode) {
            // 将所需列下推到Join的左右子节点
            JQuickJoinNode join = (JQuickJoinNode) child;
            Set<String> leftColumns = extractColumnsFromJoin(join.getLeft(), requiredColumns);
            Set<String> rightColumns = extractColumnsFromJoin(join.getRight(), requiredColumns);
            JQuickLogicalPlanNode newLeft = pushdownToNode(join.getLeft(), leftColumns);
            JQuickLogicalPlanNode newRight = pushdownToNode(join.getRight(), rightColumns);
            JQuickJoinNode newJoin = new JQuickJoinNode(join.getJoinType(), newLeft, newRight, join.getCondition());
            return new JQuickProjectNode(project.getSelectItems(), newJoin, project.isDistinct());
        }

        return project;
    }

    private Set<String> extractColumnsFromJoin(JQuickLogicalPlanNode node, Set<String> requiredColumns) {
        Set<String> result = new HashSet<>();
        // 从表达式中提取列名
        if (node instanceof JQuickProjectNode) {
            JQuickProjectNode project = (JQuickProjectNode) node;
            for (JQuickProjectNode.SelectItem item : project.getSelectItems()) {
                if (requiredColumns.contains(item.getAlias())) {
                    result.addAll(item.getExpression().getReferencedColumns());
                }
            }
        }
        return result;
    }

    private JQuickLogicalPlanNode pushdownToNode(JQuickLogicalPlanNode node, Set<String> requiredColumns) {
        if (node instanceof JQuickTableScanNode) {
            JQuickTableScanNode scan = (JQuickTableScanNode) node;
            return new JQuickTableScanNode(scan.getTableName(), scan.getAlias(), requiredColumns, scan.getFilterPredicate());
        } else if (node instanceof JQuickProjectNode) {
            JQuickProjectNode project = (JQuickProjectNode) node;
            List<JQuickProjectNode.SelectItem> filtered = project.getSelectItems().stream()
                    .filter(item -> requiredColumns.contains(item.getAlias()))
                    .collect(Collectors.toList());
            return new JQuickProjectNode(filtered, project.getChild(), project.isDistinct());
        }
        return node;
    }

}
