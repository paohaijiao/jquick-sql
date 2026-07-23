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
 * INNER JOIN 处理器
 * 只返回左右表都匹配的行
 */
public class JQuickInnerJoinHandler extends JQuickAbstractJoinHandler {

    public JQuickInnerJoinHandler(JQuickExpressionEvaluator expressionEvaluator) {
        super(expressionEvaluator);
    }

    @Override
    public JQuickJoinType getJoinType() {
        return JQuickJoinType.INNER;
    }

    /**
     * INNER JOIN 可以使用优化器推荐的构建侧
     * 因为 INNER JOIN 的语义不依赖于构建侧和探测侧的选择
     */
    @Override
    protected boolean shouldUseBuildSideOptimization() {
        return true;
    }

    @Override
    public JQuickDataSet join(JQuickDataSet leftData, JQuickDataSet rightData, List<JQuickHashJoinPhysicalNode.JoinKeyPair> joinKeys, JQuickExpression condition, boolean buildLeft) {
        if (joinKeys == null || joinKeys.isEmpty()) {
            return executeCartesianProduct(leftData, rightData, condition);
        }
        List<JQuickRow> resultRows = new ArrayList<>();
        JQuickDataSet buildData = buildLeft ? leftData : rightData;
        JQuickDataSet probeData = buildLeft ? rightData : leftData;
        Map<Object, List<JQuickRow>> hashTable = buildHashTable(buildData, joinKeys, buildLeft);
        boolean useLeftKeyForProbe = !buildLeft;
        for (JQuickRow probeRow : probeData.getRows()) {
            Object joinKey = extractJoinKey(probeRow, joinKeys, useLeftKeyForProbe);
            List<JQuickRow> matchingRows = hashTable.get(joinKey);
            if (matchingRows != null) {
                for (JQuickRow buildRow : matchingRows) {
                    JQuickRow leftRow = buildLeft ? buildRow : probeRow;
                    JQuickRow rightRow = buildLeft ? probeRow : buildRow;
                    JQuickRow joined = joinRows(leftRow, rightRow);
                    if (evaluateCondition(joined, condition)) {
                        resultRows.add(joined);
                    }
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
        for (JQuickRow leftRow : leftData.getRows()) {
            for (JQuickRow rightRow : rightData.getRows()) {
                JQuickRow joined = joinRows(leftRow, rightRow);
                if (evaluateCondition(joined, condition)) {
                    resultRows.add(joined);
                }
            }
        }

        return buildResultDataSet(resultRows, leftData, rightData);
    }
}