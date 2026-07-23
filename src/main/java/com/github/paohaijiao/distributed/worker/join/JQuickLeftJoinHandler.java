package com.github.paohaijiao.distributed.worker.join;

import com.github.paohaijiao.distributed.worker.JQuickExpressionEvaluator;
import com.github.paohaijiao.enums.JQuickJoinType;
import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.physical.node.JQuickHashJoinPhysicalNode;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * LEFT JOIN 处理器
 * 返回左表的所有行，右表匹配的行，以及左表无匹配时右表列为 NULL 的行
 */
public class JQuickLeftJoinHandler extends JQuickAbstractJoinHandler {

    public JQuickLeftJoinHandler(JQuickExpressionEvaluator expressionEvaluator) {
        super(expressionEvaluator);
    }

    @Override
    public JQuickJoinType getJoinType() {
        return JQuickJoinType.LEFT;
    }

    @Override
    public JQuickDataSet join(JQuickDataSet leftData, JQuickDataSet rightData, List<JQuickHashJoinPhysicalNode.JoinKeyPair> joinKeys, JQuickExpression condition, boolean buildLeft) {
        if (joinKeys == null || joinKeys.isEmpty()) {
            return executeCartesianProduct(leftData, rightData, condition);
        }
        List<JQuickRow> resultRows = new ArrayList<>();
        Map<Object, List<JQuickRow>> hashTable = buildHashTable(rightData, joinKeys, false);
        for (JQuickRow leftRow : leftData.getRows()) {
            Object joinKey = extractJoinKey(leftRow, joinKeys, true);
            List<JQuickRow> matchingRows = hashTable.get(joinKey);
            boolean foundMatch = false;
            if (matchingRows != null) {
                for (JQuickRow rightRow : matchingRows) {
                    JQuickRow joined = joinRows(leftRow, rightRow);
                    if (evaluateCondition(joined, condition)) {
                        resultRows.add(joined);
                        foundMatch = true;
                    }
                }
            }
            if (!foundMatch) {
                JQuickRow joined = joinRows(leftRow, null);
                resultRows.add(joined);
            }
        }

        return buildResultDataSet(resultRows, leftData, rightData);
    }

    /**
     * 执行笛卡尔积（无 JOIN 键时）
     */
    private JQuickDataSet executeCartesianProduct(JQuickDataSet leftData, JQuickDataSet rightData, JQuickExpression condition) {
        List<JQuickRow> resultRows = new ArrayList<>();
        for (JQuickRow leftRow : leftData.getRows()) {
            boolean foundMatch = false;
            for (JQuickRow rightRow : rightData.getRows()) {
                JQuickRow joined = joinRows(leftRow, rightRow);
                if (evaluateCondition(joined, condition)) {
                    resultRows.add(joined);
                    foundMatch = true;
                }
            }
            if (!foundMatch) {
                JQuickRow joined = joinRows(leftRow, null);
                resultRows.add(joined);
            }
        }

        return buildResultDataSet(resultRows, leftData, rightData);
    }
}