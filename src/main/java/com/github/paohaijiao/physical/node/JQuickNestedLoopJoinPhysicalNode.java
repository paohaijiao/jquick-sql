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

import com.github.paohaijiao.enums.JQuickJoinType;
import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.physical.JQuickPhysicalPlanNode;
import com.github.paohaijiao.physical.JQuickPhysicalPlanVisitor;
import com.github.paohaijiao.physical.domain.JQuickPhysicalColumn;
import com.github.paohaijiao.physical.domain.JQuickPhysicalStats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JQuickNestedLoopJoinPhysicalNode extends JQuickAbstractPhysicalNode {

    private final JQuickJoinType joinType;

    private final JQuickExpression condition;

    public JQuickNestedLoopJoinPhysicalNode(JQuickJoinType joinType, JQuickPhysicalPlanNode left, JQuickPhysicalPlanNode right, JQuickExpression condition) {
        super(left, right);
        this.joinType = joinType;
        this.condition = condition;
    }

    @Override
    public String getNodeType() {
        return "NestedLoopJoin";
    }

    @Override
    public List<JQuickPhysicalColumn> getOutputSchema() {
        List<JQuickPhysicalColumn> schema = new ArrayList<>();
        schema.addAll(children.get(0).getOutputSchema());
        schema.addAll(children.get(1).getOutputSchema());
        return schema;
    }


    @Override
    public JQuickPhysicalPlanNode clone() {
        return new JQuickNestedLoopJoinPhysicalNode(joinType, children.get(0).clone(), children.get(1).clone(), condition != null ? condition.clone() : null);
    }

    @Override
    public void accept(JQuickPhysicalPlanVisitor visitor) {
        visitor.visit(this);
    }

    public JQuickJoinType getJoinType() { return joinType; }public JQuickExpression getCondition() { return condition; }

    @Override
    public JQuickPhysicalStats getStats() {
        JQuickPhysicalPlanNode left = getLeft();
        JQuickPhysicalPlanNode right = getRight();
        if (left == null || right == null) {
            return JQuickPhysicalStats.empty();
        }
        long leftRows = left.getStats().getEstimatedRowCount();
        long rightRows = right.getStats().getEstimatedRowCount();
        long estimatedRows = leftRows * rightRows;
        if (leftRows > 0 && rightRows > 0 && estimatedRows / rightRows != leftRows) {
            estimatedRows = Long.MAX_VALUE;
        }
        if (condition != null) {
            double selectivity = estimateSelectivity();
            estimatedRows = (long) (estimatedRows * selectivity);
        }
        long estimatedDataSize = estimatedRows * 200;
        return new JQuickPhysicalStats(estimatedRows, estimatedDataSize, new HashMap<>());
    }

    /**
     * 估算条件选择性
     */
    private double estimateSelectivity() {
        if (condition == null) {
            return 1.0;
        }
        String cond = condition.toString().toLowerCase();
        if (cond.contains("=")) {
            return 0.01;
        }
        if (cond.contains(">") || cond.contains("<")) {
            return 0.3;
        }
        if (cond.contains("like")) {
            return 0.1;
        }

        return 0.5;
    }
}
