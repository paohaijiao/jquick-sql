package com.github.paohaijiao.distributed.worker.join;

import com.github.paohaijiao.distributed.worker.JQuickExpressionEvaluator;
import com.github.paohaijiao.enums.JQuickJoinType;
import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.expression.domain.JQuickColumnRefExpression;
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

    @Override
    public JQuickDataSet join(JQuickDataSet leftData, JQuickDataSet rightData, List<JQuickHashJoinPhysicalNode.JoinKeyPair> joinKeys, JQuickExpression condition, boolean buildLeft) {
        List<JQuickHashJoinPhysicalNode.JoinKeyPair> naturalJoinKeys = detectNaturalJoinKeys(leftData, rightData); // NATURAL JOIN 自动检测同名列作为 JOIN 键
        if (naturalJoinKeys.isEmpty()) { // 如果没有同名列，执行笛卡尔积
            return executeCartesianProduct(leftData, rightData, condition);
        }
        List<JQuickRow> resultRows = new ArrayList<>(); // 使用 Hash Join 算法（INNER JOIN 语义）
        // 确定构建侧和探测侧
        JQuickDataSet buildData = buildLeft ? leftData : rightData;
        JQuickDataSet probeData = buildLeft ? rightData : leftData;
        Map<Object, List<JQuickRow>> hashTable = buildHashTable(buildData, naturalJoinKeys, buildLeft);    // 构建哈希表
        boolean useLeftKeyForProbe = !buildLeft;   // 探测匹配
        for (JQuickRow probeRow : probeData.getRows()) {
            Object joinKey = extractJoinKey(probeRow, naturalJoinKeys, useLeftKeyForProbe);
            List<JQuickRow> matchingRows = hashTable.get(joinKey);
            if (matchingRows != null) {
                for (JQuickRow buildRow : matchingRows) {
                    JQuickRow leftRow = buildLeft ? buildRow : probeRow;
                    JQuickRow rightRow = buildLeft ? probeRow : buildRow;
                    JQuickRow joined = joinRowsWithNaturalMerge(leftRow, rightRow);  // NATURAL JOIN 合并相同名称的列（只保留一个）
                    if (evaluateCondition(joined, condition)) {
                        resultRows.add(joined);
                    }
                }
            }
        }

        return buildResultDataSet(resultRows, leftData, rightData);
    }

    /**
     * 检测 NATURAL JOIN 的 JOIN 键（同名列）
     */
    private List<JQuickHashJoinPhysicalNode.JoinKeyPair> detectNaturalJoinKeys(JQuickDataSet leftData, JQuickDataSet rightData) {
        List<JQuickHashJoinPhysicalNode.JoinKeyPair> joinKeys = new ArrayList<>();
        Set<String> leftColumnNames = new HashSet<>();        // 获取左右表的列名集合
        for (JQuickColumnMeta col : leftData.getColumns()) {
            leftColumnNames.add(col.getName());
        }
        Set<String> rightColumnNames = new HashSet<>();
        for (JQuickColumnMeta col : rightData.getColumns()) {
            rightColumnNames.add(col.getName());
        }
        for (String colName : leftColumnNames) {  // 查找同名列
            if (rightColumnNames.contains(colName)) {
                JQuickColumnRefExpression leftKey = new JQuickColumnRefExpression(null, colName);
                JQuickColumnRefExpression rightKey = new JQuickColumnRefExpression(null, colName);
                joinKeys.add(new JQuickHashJoinPhysicalNode.JoinKeyPair(leftKey, rightKey));
            }
        }

        return joinKeys;
    }

    /**
     * NATURAL JOIN 合并行（相同名称的列只保留一个）
     */
    private JQuickRow joinRowsWithNaturalMerge(JQuickRow leftRow, JQuickRow rightRow) {
        JQuickRow result = new JQuickRow();
        if (leftRow != null) {   // 添加左表列（作为基准）
            for (Map.Entry<String, Object> entry : leftRow.entrySet()) {// 跳过带前缀的列
                if (!entry.getKey().startsWith("left.") && !entry.getKey().startsWith("right.")) {
                    result.put(entry.getKey(), entry.getValue());
                }
            }
        }
        if (rightRow != null) { // 添加右表列（跳过与左表同名的列）
            for (Map.Entry<String, Object> entry : rightRow.entrySet()) {
                // 跳过带前缀的列和已存在的列
                if (!entry.getKey().startsWith("left.") && !entry.getKey().startsWith("right.")
                        && !result.containsKey(entry.getKey())) {
                    result.put(entry.getKey(), entry.getValue());
                }
            }
        }

        return result;
    }

    /**
     * 执行笛卡尔积（无同名列时）
     */
    private JQuickDataSet executeCartesianProduct(JQuickDataSet leftData, JQuickDataSet rightData, JQuickExpression condition) {
        List<JQuickRow> resultRows = new ArrayList<>();
        for (JQuickRow leftRow : leftData.getRows()) {
            for (JQuickRow rightRow : rightData.getRows()) {
                JQuickRow joined = joinRowsWithNaturalMerge(leftRow, rightRow);
                if (evaluateCondition(joined, condition)) {
                    resultRows.add(joined);
                }
            }
        }

        return buildResultDataSet(resultRows, leftData, rightData);
    }
}