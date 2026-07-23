package com.github.paohaijiao.distributed.worker.join;

import com.github.paohaijiao.distributed.worker.JQuickExpressionEvaluator;
import com.github.paohaijiao.enums.JQuickJoinType;
import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.physical.node.JQuickHashJoinPhysicalNode;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;

import java.util.*;

/**
 * FULL JOIN 处理器
 * 返回左右表的所有行，无匹配时对应列为 NULL
 */
public class JQuickFullJoinHandler extends JQuickAbstractJoinHandler {

    public JQuickFullJoinHandler(JQuickExpressionEvaluator expressionEvaluator) {
        super(expressionEvaluator);
    }

    @Override
    public JQuickJoinType getJoinType() {
        return JQuickJoinType.FULL;
    }

    @Override
    public JQuickDataSet join(JQuickDataSet leftData, JQuickDataSet rightData, List<JQuickHashJoinPhysicalNode.JoinKeyPair> joinKeys, JQuickExpression condition, boolean buildLeft) {
        if (joinKeys == null || joinKeys.isEmpty()) {
            return executeCartesianProduct(leftData, rightData, condition);
        }
        List<JQuickRow> resultRows = new ArrayList<>();
        Map<Object, List<JQuickRow>> hashTableRight = buildHashTable(rightData, joinKeys, false);
        Set<JQuickRow> matchedRightRows = new HashSet<>();
        for (JQuickRow leftRow : leftData.getRows()) {
            Object joinKey = extractJoinKey(leftRow, joinKeys, true);
            List<JQuickRow> matchingRows = hashTableRight.get(joinKey);
            boolean foundMatch = false;
            if (matchingRows != null) {
                for (JQuickRow rightRow : matchingRows) {
                    JQuickRow joined = joinRows(leftRow, rightRow);
                    if (evaluateCondition(joined, condition)) {
                        resultRows.add(joined);
                        matchedRightRows.add(rightRow);
                        foundMatch = true;
                    }
                }
            }
            if (!foundMatch) { // 左表行无匹配：输出左表行（右表列为 NULL）
                JQuickRow joined = joinRows(leftRow, null);
                if (evaluateCondition(joined, condition)) {
                    resultRows.add(joined);
                }
            }
        }
        for (JQuickRow rightRow : rightData.getRows()) { // 第二步：处理右表未匹配行
            if (!matchedRightRows.contains(rightRow)) {
                JQuickRow joined = joinRows(null, rightRow);
                if (evaluateCondition(joined, condition)) {
                    resultRows.add(joined);
                }
            }
        }

        return buildResultDataSet(resultRows, leftData, rightData);
    }

    /**
     * 执行笛卡尔积（无 JOIN 键时）
     */
    private JQuickDataSet executeCartesianProduct(JQuickDataSet leftData, JQuickDataSet rightData, JQuickExpression condition) {
        List<JQuickRow> resultRows = new ArrayList<>();
        Set<JQuickRow> matchedRightRows = new HashSet<>();
        for (JQuickRow leftRow : leftData.getRows()) {
            boolean foundMatch = false;
            for (JQuickRow rightRow : rightData.getRows()) {
                JQuickRow joined = joinRows(leftRow, rightRow);
                if (evaluateCondition(joined, condition)) {
                    resultRows.add(joined);
                    matchedRightRows.add(rightRow);
                    foundMatch = true;
                }
            }
            // 左表行无匹配：输出左表行（右表列为 NULL）
            if (!foundMatch) {
                JQuickRow joined = joinRows(leftRow, null);
                if (evaluateCondition(joined, condition)) {
                    resultRows.add(joined);
                }
            }
        }
        // 处理右表未匹配行
        for (JQuickRow rightRow : rightData.getRows()) {
            if (!matchedRightRows.contains(rightRow)) {
                JQuickRow joined = joinRows(null, rightRow);
                if (evaluateCondition(joined, condition)) {
                    resultRows.add(joined);
                }
            }
        }

        return buildResultDataSet(resultRows, leftData, rightData);
    }
}