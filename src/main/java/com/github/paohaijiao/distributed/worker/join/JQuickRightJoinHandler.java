package com.github.paohaijiao.distributed.worker.join;

import com.github.paohaijiao.distributed.worker.JQuickExpressionEvaluator;
import com.github.paohaijiao.enums.JQuickJoinType;
import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.physical.node.JQuickHashJoinPhysicalNode;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;

import java.util.*;

/**
 * RIGHT JOIN 处理器
 * 返回右表的所有行，左表匹配的行，以及右表无匹配时左表列为 NULL 的行
 */
public class JQuickRightJoinHandler extends JQuickAbstractJoinHandler {

    public JQuickRightJoinHandler(JQuickExpressionEvaluator expressionEvaluator) {
        super(expressionEvaluator);
    }

    @Override
    public JQuickJoinType getJoinType() {
        return JQuickJoinType.RIGHT;
    }

    @Override
    public JQuickDataSet join(JQuickDataSet leftData, JQuickDataSet rightData, List<JQuickHashJoinPhysicalNode.JoinKeyPair> joinKeys, JQuickExpression condition, boolean buildLeft) {
        // RIGHT JOIN 忽略 buildLeft 参数（优化器推荐的构建侧）
        // 必须固定以左表为构建表，右表为探测表，才能保证右表所有行都被保留
        // 如果使用右表作为构建表，无法在探测阶段处理右表未匹配的行
        if (joinKeys == null || joinKeys.isEmpty()) {
            return executeCartesianProduct(leftData, rightData, condition);
        }
        List<JQuickRow> resultRows = new ArrayList<>();
        Map<Object, List<JQuickRow>> hashTable = buildHashTable(leftData, joinKeys, true);
        Set<JQuickRow> matchedLeftRows = new HashSet<>();
        for (JQuickRow rightRow : rightData.getRows()) {
            Object joinKey = extractJoinKey(rightRow, joinKeys, false);
            List<JQuickRow> matchingRows = hashTable.get(joinKey);
            boolean foundMatch = false;
            if (matchingRows != null) {
                for (JQuickRow leftRow : matchingRows) {
                    JQuickRow joined = joinRows(leftRow, rightRow);
                    if (evaluateCondition(joined, condition)) {
                        resultRows.add(joined);
                        matchedLeftRows.add(leftRow);
                        foundMatch = true;
                    }
                }
            }
            // 右表行无匹配：输出右表行（左表列为 NULL）
            if (!foundMatch) {
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
        for (JQuickRow rightRow : rightData.getRows()) {
            boolean foundMatch = false;
            for (JQuickRow leftRow : leftData.getRows()) {
                JQuickRow joined = joinRows(leftRow, rightRow);
                if (evaluateCondition(joined, condition)) {
                    resultRows.add(joined);
                    foundMatch = true;
                }
            }
            if (!foundMatch) { // 右表行无匹配：输出右表行（左表列为 NULL）
                JQuickRow joined = joinRows(null, rightRow);
                if (evaluateCondition(joined, condition)) {
                    resultRows.add(joined);
                }
            }
        }

        return buildResultDataSet(resultRows, leftData, rightData);
    }
}