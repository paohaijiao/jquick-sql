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


import com.github.paohaijiao.enums.JQuickBinaryOperator;
import com.github.paohaijiao.context.JQuickExecutionContext;
import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.expression.domain.JQuickBinaryExpression;
import com.github.paohaijiao.expression.domain.JQuickColumnRefExpression;
import com.github.paohaijiao.logic.domain.JQuickJoinNode;
import com.github.paohaijiao.physical.JQuickPhysicalPlanNode;
import com.github.paohaijiao.statement.JQuickColumnMeta;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;

import java.util.*;

public class JQuickHashJoinPhysicalNode implements JQuickPhysicalPlanNode {
    private final JQuickJoinNode.JoinType joinType;
    private final JQuickPhysicalPlanNode left;
    private final JQuickPhysicalPlanNode right;
    private final JQuickExpression condition;
    private final boolean buildFromLeft;

    public JQuickHashJoinPhysicalNode(JQuickJoinNode.JoinType joinType, JQuickPhysicalPlanNode left, JQuickPhysicalPlanNode right, JQuickExpression condition, boolean buildFromLeft) {
        this.joinType = joinType;
        this.left = left;
        this.right = right;
        this.condition = condition;
        this.buildFromLeft = buildFromLeft;
    }

    @Override
    public JQuickDataSet execute(JQuickExecutionContext context) {
        JQuickDataSet buildData = buildFromLeft ? left.execute(context) : right.execute(context);
        JQuickDataSet probeData = buildFromLeft ? right.execute(context) : left.execute(context);

        String joinColumn = extractJoinColumn();

        // 构建哈希表
        Map<Object, List<JQuickRow>> hashTable = new HashMap<>();
        for (JQuickRow row : buildData.getRows()) {
            Object key = row.get(joinColumn);
            hashTable.computeIfAbsent(key, k -> new ArrayList<>()).add(row);
        }

        // 探测
        List<JQuickRow> resultRows = new ArrayList<>();
        Set<JQuickRow> matchedBuildRows = new HashSet<>();

        for (JQuickRow probeRow : probeData.getRows()) {
            Object key = probeRow.get(joinColumn);
            List<JQuickRow> matchingRows = hashTable.get(key);

            if (matchingRows != null) {
                for (JQuickRow buildRow : matchingRows) {
                    matchedBuildRows.add(buildRow);
                    JQuickRow merged = buildFromLeft ?
                            mergeRows(buildRow, probeRow) : mergeRows(probeRow, buildRow);
                    resultRows.add(merged);
                }
            } else if (joinType ==JQuickJoinNode.JoinType.LEFT && buildFromLeft) {
                resultRows.add(mergeRows(probeRow, createNullRow(buildData)));
            } else if (joinType == JQuickJoinNode.JoinType.RIGHT && !buildFromLeft) {
                resultRows.add(mergeRows(createNullRow(buildData), probeRow));
            }
        }

        // FULL JOIN 添加未匹配的build侧行
        if (joinType == JQuickJoinNode.JoinType.FULL) {
            for (List<JQuickRow> rows : hashTable.values()) {
                for (JQuickRow buildRow : rows) {
                    if (!matchedBuildRows.contains(buildRow)) {
                        JQuickRow merged = buildFromLeft ?
                                mergeRows(buildRow, createNullRow(probeData)) :
                                mergeRows(createNullRow(probeData), buildRow);
                        resultRows.add(merged);
                    }
                }
            }
        }

        List<JQuickColumnMeta> mergedColumns = mergeColumns(buildData, probeData);
        return new JQuickDataSet(mergedColumns, resultRows);
    }

    private String extractJoinColumn() {
        if (condition instanceof JQuickBinaryExpression) {
            JQuickBinaryExpression binary = (JQuickBinaryExpression) condition;
            if (binary.getOperator() == JQuickBinaryOperator.EQ) {
                if (binary.getLeft() instanceof JQuickColumnRefExpression) {
                    return ((JQuickColumnRefExpression) binary.getLeft()).getColumnName();
                }
                if (binary.getRight() instanceof JQuickColumnRefExpression) {
                    return ((JQuickColumnRefExpression) binary.getRight()).getColumnName();
                }
            }
        }
        return null;
    }

    private JQuickRow mergeRows(JQuickRow outer, JQuickRow inner) {
        JQuickRow merged = new JQuickRow();
        merged.putAll(outer);
        merged.putAll(inner);
        return merged;
    }

    private JQuickRow createNullRow(JQuickDataSet dataSet) {
        JQuickRow nullRow = new JQuickRow();
        for (JQuickColumnMeta col : dataSet.getColumns()) {
            nullRow.put(col.getName(), null);
        }
        return nullRow;
    }

    private List<JQuickColumnMeta> mergeColumns(JQuickDataSet left, JQuickDataSet right) {
        List<JQuickColumnMeta> merged = new ArrayList<>();
        merged.addAll(left.getColumns());
        merged.addAll(right.getColumns());
        return merged;
    }

    @Override
    public String getNodeType() {
        return "HashJoin";
    }

    @Override
    public long getEstimatedCost() {
        return left.getEstimatedCost() + right.getEstimatedCost();
    }
}
