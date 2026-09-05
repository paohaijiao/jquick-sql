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

import com.github.paohaijiao.enums.JQuickSQLOperationType;
import com.github.paohaijiao.physical.JQuickPhysicalPlanNode;
import com.github.paohaijiao.physical.JQuickPhysicalPlanVisitor;
import com.github.paohaijiao.physical.domain.JQuickPhysicalColumn;
import com.github.paohaijiao.physical.domain.JQuickPhysicalStats;

import java.util.HashMap;
import java.util.List;

public class JQuickSetOperationPhysicalNode extends JQuickAbstractPhysicalNode {

    private final JQuickSQLOperationType operationType;

    public JQuickSetOperationPhysicalNode(JQuickSQLOperationType operationType, JQuickPhysicalPlanNode left, JQuickPhysicalPlanNode right) {
        super(left, right);
        this.operationType = operationType;
    }

    @Override
    public String getNodeType() {
        return "SetOperation";
    }

    @Override
    public List<JQuickPhysicalColumn> getOutputSchema() {
        return children.get(0).getOutputSchema();
    }

    @Override
    public JQuickPhysicalPlanNode clone() {
        return new JQuickSetOperationPhysicalNode(operationType, children.get(0).clone(), children.get(1).clone());
    }

    @Override
    public void accept(JQuickPhysicalPlanVisitor visitor) {
        visitor.visit(this);
    }

    public JQuickSQLOperationType getOperationType() { return operationType; }

    @Override
    public JQuickPhysicalStats getStats() {
        JQuickPhysicalPlanNode left = getLeft();
        JQuickPhysicalPlanNode right = getRight();
        if (left == null || right == null) {
            return JQuickPhysicalStats.empty();
        }
        JQuickPhysicalStats leftStats = left.getStats();
        JQuickPhysicalStats rightStats = right.getStats();
        long leftRows = leftStats.getEstimatedRowCount();
        long rightRows = rightStats.getEstimatedRowCount();
        long estimatedRows;
        switch (operationType) {
            case UNION:
            case UNION_ALL:
                estimatedRows = leftRows + rightRows;
                break;
            case INTERSECT:
                estimatedRows = (long) (Math.min(leftRows, rightRows) * 0.3);
                break;
            case EXCEPT:
                estimatedRows = (long) (leftRows * 0.7);
                break;
            default:
                estimatedRows = leftRows;
        }
        if (estimatedRows < 0) {
            estimatedRows = Long.MAX_VALUE;
        }
        long estimatedDataSize = estimatedRows * 200;
        return new JQuickPhysicalStats(estimatedRows, estimatedDataSize, new HashMap<>());
    }
}
