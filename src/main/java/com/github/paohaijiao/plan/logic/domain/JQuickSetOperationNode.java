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
package com.github.paohaijiao.plan.logic.domain;

import com.github.paohaijiao.executor.JQuickExecutionContext;
import com.github.paohaijiao.plan.logic.JQuickLogicalPlanNode;
import com.github.paohaijiao.plan.logic.JQuickLogicalPlanVisitor;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;

import java.util.*;

/**
 * 集合操作节点 - UNION, INTERSECT, EXCEPT
 */
public class JQuickSetOperationNode implements JQuickLogicalPlanNode {

    private final JQuickLogicalPlanNode left;
    private final JQuickLogicalPlanNode right;
    private final OperationType operationType;
    public JQuickSetOperationNode(JQuickLogicalPlanNode left, JQuickLogicalPlanNode right, OperationType operationType) {
        this.left = left;
        this.right = right;
        this.operationType = operationType;
    }

    @Override
    public JQuickDataSet execute(JQuickExecutionContext context) {
        JQuickDataSet leftData = left.execute(context);
        JQuickDataSet rightData = right.execute(context);
        List<JQuickRow> resultRows;
        switch (operationType) {
            case UNION:
                resultRows = executeUnion(leftData, rightData, true);
                break;
            case UNION_ALL:
                resultRows = executeUnion(leftData, rightData, false);
                break;
            case INTERSECT:
                resultRows = executeIntersect(leftData, rightData, true);
                break;
            case INTERSECT_ALL:
                resultRows = executeIntersect(leftData, rightData, false);
                break;
            case EXCEPT:
                resultRows = executeExcept(leftData, rightData, true);
                break;
            case EXCEPT_ALL:
                resultRows = executeExcept(leftData, rightData, false);
                break;
            default:
                throw new RuntimeException("Unsupported operation: " + operationType);
        }

        // 使用左表的列元数据（假设两表结构相同）
        return new JQuickDataSet(leftData.getColumns(), resultRows);
    }

    private List<JQuickRow> executeUnion(JQuickDataSet left, JQuickDataSet right, boolean distinct) {
        List<JQuickRow> result = new ArrayList<>();
        result.addAll(left.getRows());
        result.addAll(right.getRows());

        if (distinct) {
            Set<JQuickRow> set = new LinkedHashSet<>(result);
            return new ArrayList<>(set);
        }

        return result;
    }

    private List<JQuickRow> executeIntersect(JQuickDataSet left, JQuickDataSet right, boolean distinct) {
        if (distinct) {
            Set<JQuickRow> rightSet = new HashSet<>(right.getRows());
            List<JQuickRow> result = new ArrayList<>();
            for (JQuickRow row : left.getRows()) {
                if (rightSet.contains(row)) {
                    result.add(row);
                }
            }
            return result;
        } else {
            // INTERSECT ALL - 保留重复次数
            Map<JQuickRow, Integer> rightCounts = new HashMap<>();
            for (JQuickRow row : right.getRows()) {
                rightCounts.merge(row, 1, Integer::sum);
            }

            List<JQuickRow> result = new ArrayList<>();
            Map<JQuickRow, Integer> leftCounts = new HashMap<>();
            for (JQuickRow row : left.getRows()) {
                leftCounts.merge(row, 1, Integer::sum);
            }

            for (Map.Entry<JQuickRow, Integer> entry : leftCounts.entrySet()) {
                Integer rightCount = rightCounts.get(entry.getKey());
                if (rightCount != null) {
                    int minCount = Math.min(entry.getValue(), rightCount);
                    for (int i = 0; i < minCount; i++) {
                        result.add(entry.getKey());
                    }
                }
            }
            return result;
        }
    }

    private List<JQuickRow> executeExcept(JQuickDataSet left, JQuickDataSet right, boolean distinct) {
        if (distinct) {
            Set<JQuickRow> rightSet = new HashSet<>(right.getRows());
            List<JQuickRow> result = new ArrayList<>();
            for (JQuickRow row : left.getRows()) {
                if (!rightSet.contains(row)) {
                    result.add(row);
                }
            }
            return result;
        } else {
            // EXCEPT ALL - 减去出现的次数
            Map<JQuickRow, Integer> rightCounts = new HashMap<>();
            for (JQuickRow row : right.getRows()) {
                rightCounts.merge(row, 1, Integer::sum);
            }

            List<JQuickRow> result = new ArrayList<>();
            Map<JQuickRow, Integer> leftCounts = new HashMap<>();
            for (JQuickRow row : left.getRows()) {
                leftCounts.merge(row, 1, Integer::sum);
            }

            for (Map.Entry<JQuickRow, Integer> entry : leftCounts.entrySet()) {
                Integer rightCount = rightCounts.getOrDefault(entry.getKey(), 0);
                int remaining = entry.getValue() - rightCount;
                for (int i = 0; i < remaining; i++) {
                    result.add(entry.getKey());
                }
            }
            return result;
        }
    }

    @Override
    public String getNodeType() {
        return "SetOperation";
    }

    @Override
    public List<JQuickLogicalPlanNode> getChildren() {
        return Arrays.asList(left, right);
    }

    @Override
    public void accept(JQuickLogicalPlanVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public List<String> getOutputColumns() {
        return left.getOutputColumns();
    }

    @Override
    public JQuickLogicalPlanNode clone() {
        return new JQuickSetOperationNode(left.clone(), right.clone(), operationType);
    }

    public JQuickLogicalPlanNode getLeft() {
        return left;
    }

    public JQuickLogicalPlanNode getRight() {
        return right;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public enum OperationType {
        UNION, UNION_ALL, INTERSECT, INTERSECT_ALL, EXCEPT, EXCEPT_ALL
    }
}
