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
package com.github.paohaijiao.plan.physical.node;

import com.github.paohaijiao.executor.JQuickExecutionContext;
import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.plan.logic.domain.JQuickJoinNode;
import com.github.paohaijiao.statement.JQuickColumnMeta;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;

import java.util.ArrayList;
import java.util.List;

public class JQuickNestedLoopJoinPhysicalNode implements JQuickPhysicalPlanNode {
    private final JQuickJoinNode.JoinType joinType;
    private final JQuickPhysicalPlanNode left;
    private final JQuickPhysicalPlanNode right;
    private final JQuickExpression condition;
    private final boolean leftIsSmaller;

    public JQuickNestedLoopJoinPhysicalNode(JQuickJoinNode.JoinType joinType, JQuickPhysicalPlanNode left, JQuickPhysicalPlanNode right, JQuickExpression condition, boolean leftIsSmaller) {
        this.joinType = joinType;
        this.left = left;
        this.right = right;
        this.condition = condition;
        this.leftIsSmaller = leftIsSmaller;
    }

    @Override
    public JQuickDataSet execute(JQuickExecutionContext context) {
        JQuickDataSet leftData = left.execute(context);
        JQuickDataSet rightData = right.execute(context);

        // 选择小表作为外层循环
        if (leftIsSmaller) {
            return executeNestedLoop(leftData, rightData);
        } else {
            return executeNestedLoop(rightData, leftData);
        }
    }

    private JQuickDataSet executeNestedLoop(JQuickDataSet outer, JQuickDataSet inner) {
        List<JQuickRow> resultRows = new ArrayList<>();

        for (JQuickRow outerRow : outer.getRows()) {
            int matchCount = 0;
            for (JQuickRow innerRow : inner.getRows()) {
                JQuickRow merged = mergeRows(outerRow, innerRow);
                if (evaluateCondition(merged)) {
                    resultRows.add(merged);
                    matchCount++;
                }
            }

            // LEFT JOIN 需要添加未匹配的行
            if (joinType == JQuickJoinNode.JoinType.LEFT && matchCount == 0) {
                resultRows.add(mergeRows(outerRow, createNullRow(inner)));
            }
        }

        List<JQuickColumnMeta> mergedColumns = mergeColumns(outer, inner);
        return new JQuickDataSet(mergedColumns, resultRows);
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

    private boolean evaluateCondition(JQuickRow row) {
        if (condition == null) return true;
        Object result = condition.evaluate(row);
        return result instanceof Boolean && (Boolean) result;
    }

    private List<JQuickColumnMeta> mergeColumns(JQuickDataSet left, JQuickDataSet right) {
        List<JQuickColumnMeta> merged = new ArrayList<>();
        merged.addAll(left.getColumns());
        merged.addAll(right.getColumns());
        return merged;
    }

    @Override
    public String getNodeType() {
        return "NestedLoopJoin";
    }

    @Override
    public long getEstimatedCost() {
        return left.getEstimatedCost() * right.getEstimatedCost();
    }
}
