package com.github.paohaijiao.distributed.worker.join;

import com.github.paohaijiao.distributed.worker.JQuickExpressionEvaluator;
import com.github.paohaijiao.enums.JQuickJoinType;
import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.physical.node.JQuickHashJoinPhysicalNode;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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
        
        if (leftData == null || leftData.isEmpty()) {
            console.warn("CROSS JOIN: leftData is empty");
            return buildResultDataSet(resultRows, leftData, rightData);
        }
        
        if (rightData == null || rightData.isEmpty()) {
            console.warn("CROSS JOIN: rightData is empty");
            return buildResultDataSet(resultRows, leftData, rightData);
        }
        
        List<JQuickRow> leftRows = leftData.getRows();
        List<JQuickRow> rightRows = rightData.getRows();
        
        Set<String> seenRows = new LinkedHashSet<>();
        
        for (JQuickRow leftRow : leftRows) {
            for (JQuickRow rightRow : rightRows) {
                JQuickRow joined = joinRows(leftRow, rightRow);
                if (joined != null && evaluateCondition(joined, condition)) {
                    String rowKey = buildRowKey(joined);
                    if (!seenRows.contains(rowKey)) {
                        seenRows.add(rowKey);
                        resultRows.add(joined);
                    }
                }
            }
        }

        console.info("CROSS JOIN: leftRows=" + leftRows.size() + ", rightRows=" + rightRows.size() + 
                    ", expected=" + (leftRows.size() * rightRows.size()) + ", actual=" + resultRows.size());
        
        return buildResultDataSet(resultRows, leftData, rightData);
    }
    
    private String buildRowKey(JQuickRow row) {
        StringBuilder key = new StringBuilder();
        for (String col : row.keySet()) {
            if (key.length() > 0) key.append("|");
            key.append(col).append("=").append(row.get(col));
        }
        return key.toString();
    }
}