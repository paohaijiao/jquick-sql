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
        Set<String> matchedRightRowKeys = new HashSet<>();
        for (JQuickRow leftRow : leftData.getRows()) {
            Object joinKey = extractJoinKey(leftRow, joinKeys, true);
            List<JQuickRow> matchingRows = hashTableRight.get(joinKey);
            int matchCount = 0;
            if (matchingRows != null) {
                for (JQuickRow rightRow : matchingRows) {
                    JQuickRow joined = joinRows(leftRow, rightRow);
                    if (evaluateCondition(joined, condition)) {
                        resultRows.add(joined);
                        matchedRightRowKeys.add(generateRowKey(rightRow));
                        matchCount++;
                    }
                }
            }
            if (matchCount == 0) {
                JQuickRow joined = joinRows(leftRow, null);
                resultRows.add(joined);
            }
        }
        for (JQuickRow rightRow : rightData.getRows()) {
            if (!matchedRightRowKeys.contains(generateRowKey(rightRow))) {
                JQuickRow joined = joinRows(null, rightRow);
                resultRows.add(joined);
            }
        }

        return buildResultDataSet(resultRows, leftData, rightData);
    }

    private String generateRowKey(JQuickRow row) {
        StringBuilder sb = new StringBuilder();
        List<String> sortedKeys = new ArrayList<>(row.keySet());
        Collections.sort(sortedKeys);
        for (String key : sortedKeys) {
            if (sb.length() > 0) sb.append("|");
            sb.append(key).append("=").append(row.get(key));
        }
        return sb.toString();
    }

    /**
     * 执行笛卡尔积（无 JOIN 键时）
     */
    private JQuickDataSet executeCartesianProduct(JQuickDataSet leftData, JQuickDataSet rightData, JQuickExpression condition) {
        List<JQuickRow> resultRows = new ArrayList<>();
        Set<String> matchedRightRowKeys = new HashSet<>();
        for (JQuickRow leftRow : leftData.getRows()) {
            boolean foundMatch = false;
            for (JQuickRow rightRow : rightData.getRows()) {
                JQuickRow joined = joinRows(leftRow, rightRow);
                if (evaluateCondition(joined, condition)) {
                    resultRows.add(joined);
                    matchedRightRowKeys.add(generateRowKey(rightRow));
                    foundMatch = true;
                }
            }
            if (!foundMatch) {
                JQuickRow joined = joinRows(leftRow, null);
                resultRows.add(joined);
            }
        }
        for (JQuickRow rightRow : rightData.getRows()) {
            if (!matchedRightRowKeys.contains(generateRowKey(rightRow))) {
                JQuickRow joined = joinRows(null, rightRow);
                resultRows.add(joined);
            }
        }

        return buildResultDataSet(resultRows, leftData, rightData);
    }
}