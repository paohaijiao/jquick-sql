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
package com.github.paohaijiao.logic.domain;

import com.github.paohaijiao.context.JQuickExecutionContext;
import com.github.paohaijiao.enums.JQuickJoinType;
import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.logic.JQuickLogicalPlanNode;
import com.github.paohaijiao.logic.JQuickLogicalPlanVisitor;
import com.github.paohaijiao.statement.JQuickColumnMeta;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;

import java.util.*;

/**
 * 连接节点 - JOIN 子句
 */
public class JQuickJoinNode implements JQuickLogicalPlanNode {

    private final JQuickJoinType joinType;

    private final JQuickLogicalPlanNode left;

    private final JQuickLogicalPlanNode right;

    private final JQuickExpression condition;

    public JQuickJoinNode(JQuickJoinType joinType, JQuickLogicalPlanNode left, JQuickLogicalPlanNode right, JQuickExpression condition) {
        this.joinType = joinType;
        this.left = left;
        this.right = right;
        this.condition = condition;
    }

    @Override
    public JQuickDataSet execute(JQuickExecutionContext context) {
        JQuickDataSet leftData = left.execute(context);
        JQuickDataSet rightData = right.execute(context);

        List<JQuickRow> resultRows;

        switch (joinType) {
            case CROSS:
                resultRows = executeCrossJoin(leftData, rightData);
                break;
            case INNER:
                resultRows = executeInnerJoin(leftData, rightData);
                break;
            case LEFT:
                resultRows = executeLeftJoin(leftData, rightData);
                break;
            case RIGHT:
                resultRows = executeRightJoin(leftData, rightData);
                break;
            case FULL:
                resultRows = executeFullJoin(leftData, rightData);
                break;
            case SEMI:
                resultRows = executeSemiJoin(leftData, rightData);
                break;
            case ANTI:
                resultRows = executeAntiJoin(leftData, rightData);
                break;
            default:
                throw new RuntimeException("Unsupported join type: " + joinType);
        }

        // 合并列元数据
        List<JQuickColumnMeta> mergedColumns = mergeColumns(leftData, rightData);
        return new JQuickDataSet(mergedColumns, resultRows);
    }

    private List<JQuickRow> executeCrossJoin(JQuickDataSet left, JQuickDataSet right) {
        List<JQuickRow> result = new ArrayList<>();
        for (JQuickRow leftRow : left.getRows()) {
            for (JQuickRow rightRow : right.getRows()) {
                result.add(mergeRows(leftRow, rightRow));
            }
        }
        return result;
    }

    private List<JQuickRow> executeInnerJoin(JQuickDataSet left, JQuickDataSet right) {
        List<JQuickRow> result = new ArrayList<>();
        for (JQuickRow leftRow : left.getRows()) {
            for (JQuickRow rightRow : right.getRows()) {
                JQuickRow merged = mergeRows(leftRow, rightRow);
                if (evaluateCondition(merged)) {
                    result.add(merged);
                }
            }
        }
        return result;
    }

    private List<JQuickRow> executeLeftJoin(JQuickDataSet left, JQuickDataSet right) {
        List<JQuickRow> result = new ArrayList<>();
        Set<JQuickRow> matchedRightRows = new HashSet<>();

        for (JQuickRow leftRow : left.getRows()) {
            boolean matched = false;
            for (JQuickRow rightRow : right.getRows()) {
                JQuickRow merged = mergeRows(leftRow, rightRow);
                if (evaluateCondition(merged)) {
                    result.add(merged);
                    matchedRightRows.add(rightRow);
                    matched = true;
                }
            }
            if (!matched) {
                result.add(mergeRows(leftRow, createNullRow(right)));
            }
        }
        return result;
    }

    private List<JQuickRow> executeRightJoin(JQuickDataSet left, JQuickDataSet right) {
        List<JQuickRow> result = new ArrayList<>();
        Set<JQuickRow> matchedLeftRows = new HashSet<>();

        for (JQuickRow rightRow : right.getRows()) {
            boolean matched = false;
            for (JQuickRow leftRow : left.getRows()) {
                JQuickRow merged = mergeRows(leftRow, rightRow);
                if (evaluateCondition(merged)) {
                    result.add(merged);
                    matchedLeftRows.add(leftRow);
                    matched = true;
                }
            }
            if (!matched) {
                result.add(mergeRows(createNullRow(left), rightRow));
            }
        }
        return result;
    }

    private List<JQuickRow> executeFullJoin(JQuickDataSet left, JQuickDataSet right) {
        Set<JQuickRow> rightMatched = new HashSet<>();
        Set<JQuickRow> leftMatched = new HashSet<>();
        List<JQuickRow> result = new ArrayList<>();

        // 左连接部分
        for (JQuickRow leftRow : left.getRows()) {
            boolean matched = false;
            for (JQuickRow rightRow : right.getRows()) {
                JQuickRow merged = mergeRows(leftRow, rightRow);
                if (evaluateCondition(merged)) {
                    result.add(merged);
                    rightMatched.add(rightRow);
                    leftMatched.add(leftRow);
                    matched = true;
                }
            }
            if (!matched) {
                result.add(mergeRows(leftRow, createNullRow(right)));
            }
        }

        // 右连接未匹配部分
        for (JQuickRow rightRow : right.getRows()) {
            if (!rightMatched.contains(rightRow)) {
                result.add(mergeRows(createNullRow(left), rightRow));
            }
        }

        return result;
    }

    private List<JQuickRow> executeSemiJoin(JQuickDataSet left, JQuickDataSet right) {
        List<JQuickRow> result = new ArrayList<>();
        for (JQuickRow leftRow : left.getRows()) {
            for (JQuickRow rightRow : right.getRows()) {
                if (evaluateCondition(mergeRows(leftRow, rightRow))) {
                    result.add(leftRow);
                    break;
                }
            }
        }
        return result;
    }

    private List<JQuickRow> executeAntiJoin(JQuickDataSet left, JQuickDataSet right) {
        List<JQuickRow> result = new ArrayList<>();
        for (JQuickRow leftRow : left.getRows()) {
            boolean found = false;
            for (JQuickRow rightRow : right.getRows()) {
                if (evaluateCondition(mergeRows(leftRow, rightRow))) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                result.add(leftRow);
            }
        }
        return result;
    }

    private JQuickRow mergeRows(JQuickRow left, JQuickRow right) {
        JQuickRow merged = new JQuickRow();
        merged.putAll(left);
        merged.putAll(right);
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
        return "Join";
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
        List<String> columns = new ArrayList<>();
        columns.addAll(left.getOutputColumns());
        columns.addAll(right.getOutputColumns());
        return columns;
    }

    @Override
    public JQuickLogicalPlanNode clone() {
        return new JQuickJoinNode(joinType, left.clone(), right.clone(), condition);
    }

    public JQuickJoinType getJoinType() {
        return joinType;
    }

    public JQuickLogicalPlanNode getLeft() {
        return left;
    }

    public JQuickLogicalPlanNode getRight() {
        return right;
    }

    public JQuickExpression getCondition() {
        return condition;
    }


}
