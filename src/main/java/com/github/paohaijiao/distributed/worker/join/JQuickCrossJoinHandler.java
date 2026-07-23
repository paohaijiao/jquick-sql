package com.github.paohaijiao.distributed.worker.join;

import com.github.paohaijiao.distributed.worker.JQuickExpressionEvaluator;
import com.github.paohaijiao.enums.JQuickJoinType;
import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.physical.node.JQuickHashJoinPhysicalNode;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;

import java.util.ArrayList;
import java.util.List;

/**
 * CROSS JOIN 处理器
 * 返回左右表的笛卡尔积
 */
public class JQuickCrossJoinHandler extends JQuickAbstractJoinHandler {

    public JQuickCrossJoinHandler(JQuickExpressionEvaluator expressionEvaluator) {
        super(expressionEvaluator);
    }

    @Override
    public JQuickJoinType getJoinType() {
        return JQuickJoinType.CROSS;
    }

    @Override
    public JQuickDataSet join(JQuickDataSet leftData, JQuickDataSet rightData, List<JQuickHashJoinPhysicalNode.JoinKeyPair> joinKeys, JQuickExpression condition, boolean buildLeft) {
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