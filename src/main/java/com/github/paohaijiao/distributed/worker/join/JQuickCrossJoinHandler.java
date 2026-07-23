package com.github.paohaijiao.distributed.worker.join;

import com.github.paohaijiao.distributed.worker.JQuickExpressionEvaluator;
import com.github.paohaijiao.enums.JQuickJoinType;
import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.physical.node.JQuickHashJoinPhysicalNode;
import com.github.paohaijiao.statement.JQuickColumnMeta;
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
        
        List<JQuickRow> leftRows = deduplicateRows(leftData.getRows(), leftData.getColumns());
        List<JQuickRow> rightRows = deduplicateRows(rightData.getRows(), rightData.getColumns());
        
        console.info("CROSS JOIN: original leftRows=" + leftData.getRows().size() + 
                    ", deduplicated leftRows=" + leftRows.size() +
                    ", original rightRows=" + rightData.getRows().size() +
                    ", deduplicated rightRows=" + rightRows.size());
        
        for (JQuickRow leftRow : leftRows) {
            for (JQuickRow rightRow : rightRows) {
                JQuickRow joined = joinRows(leftRow, rightRow);
                if (joined != null && evaluateCondition(joined, condition)) {
                    resultRows.add(joined);
                }
            }
        }

        console.info("CROSS JOIN: leftRows=" + leftRows.size() + ", rightRows=" + rightRows.size() + 
                    ", expected=" + (leftRows.size() * rightRows.size()) + ", actual=" + resultRows.size());
        
        return buildResultDataSet(resultRows, leftData, rightData);
    }
    
    private List<JQuickRow> deduplicateRows(List<JQuickRow> rows, List<JQuickColumnMeta> columns) {
        Set<String> seen = new LinkedHashSet<>();
        List<JQuickRow> result = new ArrayList<>();
        for (JQuickRow row : rows) {
            String key = buildRowKey(row, columns);
            if (!seen.contains(key)) {
                seen.add(key);
                result.add(row);
            }
        }
        return result;
    }
    
    private String buildRowKey(JQuickRow row, List<JQuickColumnMeta> columns) {
        StringBuilder key = new StringBuilder();
        for (JQuickColumnMeta col : columns) {
            if (key.length() > 0) key.append("|");
            String colName = col.getName();
            Object value = row.get(colName);
            key.append(colName).append("=").append(value);
        }
        return key.toString();
    }
}