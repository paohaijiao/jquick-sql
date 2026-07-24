package com.github.paohaijiao.distributed.worker.join;

import com.github.paohaijiao.distributed.worker.JQuickExpressionEvaluator;
import com.github.paohaijiao.enums.JQuickJoinType;
import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.physical.node.JQuickHashJoinPhysicalNode;
import com.github.paohaijiao.statement.JQuickColumnMeta;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;

import java.util.*;

/**
 * NATURAL JOIN 处理器
 * 自动根据相同名称的列进行 INNER JOIN
 */
public class JQuickNaturalJoinHandler extends JQuickAbstractJoinHandler {

    public JQuickNaturalJoinHandler(JQuickExpressionEvaluator expressionEvaluator) {
        super(expressionEvaluator);
    }

    @Override
    public JQuickJoinType getJoinType() {
        return JQuickJoinType.NATURAL;
    }

    /**
     * NATURAL JOIN 的核心逻辑：按照同名列进行 INNER JOIN
     * 无论是否有 JOIN 键，都按照 NATURAL JOIN 的语义进行处理
     */
    @Override
    public JQuickDataSet join(JQuickDataSet leftData, JQuickDataSet rightData, List<JQuickHashJoinPhysicalNode.JoinKeyPair> joinKeys, JQuickExpression condition, boolean buildLeft) {
        console.info("========== JQuickNaturalJoinHandler.join() called ==========");
        Set<String> leftColumnSet = extractColumnNames(leftData);
        Set<String> rightColumnSet = extractColumnNames(rightData);
        console.info("NATURAL JOIN - left columns: " + leftColumnSet);
        console.info("NATURAL JOIN - right columns: " + rightColumnSet);
        Set<String> commonColumns = new HashSet<>(leftColumnSet);
        commonColumns.retainAll(rightColumnSet);
        console.info("NATURAL JOIN - common columns: " + commonColumns);
        if (commonColumns.isEmpty()) {
            console.warn("NATURAL JOIN - no common columns found, returning empty result");
            return buildResultDataSet(new ArrayList<>(), leftData, rightData);
        }
        List<JQuickRow> resultRows = new ArrayList<>();
        // NATURAL JOIN 语义：按照同名列进行 INNER JOIN
        // 遍历左表和右表，找到同名列值相等的行
        for (JQuickRow leftRow : leftData.getRows()) {
            for (JQuickRow rightRow : rightData.getRows()) {
                // 检查所有同名列的值是否相等
                boolean match = true;
                for (String colName : commonColumns) {
                    Object leftValue = getRowValue(leftRow, colName);
                    Object rightValue = getRowValue(rightRow, colName);
                    console.info("NATURAL JOIN - comparing column '" + colName + "': left=" + leftValue + ", right=" + rightValue);
                    if (leftValue == null || rightValue == null) {// 任一值为 null 则不匹配
                        match = false;
                        break;
                    }
                    // 值不相等则不匹配
                    if (!leftValue.equals(rightValue)) {
                        match = false;
                        break;
                    }
                }
                
                if (match) {
                    JQuickRow joined = mergeRows(leftRow, rightRow, commonColumns);
                    if (evaluateCondition(joined, condition)) {
                        resultRows.add(joined);
                    }
                }
            }
        }

        console.info("NATURAL JOIN - result rows: " + resultRows.size());
        return buildResultDataSet(resultRows, leftData, rightData);
    }

    @Override
    protected boolean shouldUseBuildSideOptimization() {
        return false;
    }

    @Override
    public Map<Object, List<JQuickRow>> buildHashTable(JQuickDataSet data, List<JQuickHashJoinPhysicalNode.JoinKeyPair> joinKeys, boolean useLeftKey) {
        return new HashMap<>();
    }

    @Override
    public Object extractJoinKey(JQuickRow row, List<JQuickHashJoinPhysicalNode.JoinKeyPair> joinKeys, boolean useLeftKey) {
        return null;
    }

    /**
     * 从数据集中提取列名集合（不区分大小写）
     */
    private Set<String> extractColumnNames(JQuickDataSet data) {
        Set<String> columnNames = new HashSet<>();
        for (JQuickColumnMeta col : data.getColumns()) {
            String name = col.getName();
            // 去除表前缀，只保留纯列名
            int dotIndex = name.lastIndexOf('.');
            if (dotIndex >= 0) {
                name = name.substring(dotIndex + 1);
            }
            columnNames.add(name.toLowerCase());
        }
        return columnNames;
    }

    /**
     * 从行中获取指定列的值（不区分大小写）
     */
    private Object getRowValue(JQuickRow row, String colName) {
        String lowerColName = colName.toLowerCase();
        
        // 1. 直接使用原始列名
        if (row.containsKey(colName)) {
            return row.get(colName);
        }
        
        // 2. 使用小写列名
        if (row.containsKey(lowerColName)) {
            return row.get(lowerColName);
        }
        
        // 3. 遍历所有键，查找匹配的列（不区分大小写）
        for (String key : row.keySet()) {
            String pureName = key;
            int dotIndex = key.lastIndexOf('.');
            if (dotIndex >= 0) {
                pureName = key.substring(dotIndex + 1);
            }
            if (pureName.toLowerCase().equals(lowerColName)) {
                return row.get(key);
            }
        }
        
        console.warn("NATURAL JOIN - getRowValue: column '" + colName + "' not found in row keys: " + row.keySet());
        return null;
    }

    /**
     * NATURAL JOIN 合并行（相同名称的列只保留一个）
     */
    private JQuickRow mergeRows(JQuickRow leftRow, JQuickRow rightRow, Set<String> commonColumns) {
        JQuickRow result = new JQuickRow();
        
        // 添加左表列
        if (leftRow != null) {
            for (Map.Entry<String, Object> entry : leftRow.entrySet()) {
                String key = entry.getKey();
                String pureKey = key;
                int dotIndex = key.lastIndexOf('.');
                if (dotIndex >= 0) {
                    pureKey = key.substring(dotIndex + 1);
                }
                // 使用纯列名作为键
                if (!result.containsKey(pureKey)) {
                    result.put(pureKey, entry.getValue());
                }
            }
        }
        
        // 添加右表列（跳过与左表同名的列）
        if (rightRow != null) {
            for (Map.Entry<String, Object> entry : rightRow.entrySet()) {
                String key = entry.getKey();
                String pureKey = key;
                int dotIndex = key.lastIndexOf('.');
                if (dotIndex >= 0) {
                    pureKey = key.substring(dotIndex + 1);
                }
                // 使用纯列名作为键，跳过已存在的列
                if (!result.containsKey(pureKey)) {
                    result.put(pureKey, entry.getValue());
                }
            }
        }
        
        return result;
    }
}
