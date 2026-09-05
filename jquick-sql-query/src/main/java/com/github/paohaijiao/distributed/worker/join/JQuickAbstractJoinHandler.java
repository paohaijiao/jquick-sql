package com.github.paohaijiao.distributed.worker.join;

import com.github.paohaijiao.console.JConsole;
import com.github.paohaijiao.distributed.worker.JQuickExpressionEvaluator;
import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.expression.domain.JQuickColumnRefExpression;
import com.github.paohaijiao.physical.node.JQuickHashJoinPhysicalNode;
import com.github.paohaijiao.statement.JQuickColumnMeta;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JOIN 处理器抽象基类
 * 提供通用的 JOIN 操作实现
 */
public abstract class JQuickAbstractJoinHandler implements JQuickJoinHandler {

    protected final JConsole console = JConsole.initConsoleEnvironment();
    protected final JQuickExpressionEvaluator expressionEvaluator;

    protected JQuickAbstractJoinHandler(JQuickExpressionEvaluator expressionEvaluator) {
        this.expressionEvaluator = expressionEvaluator;
    }

    @Override
    public Map<Object, List<JQuickRow>> buildHashTable(JQuickDataSet data, List<JQuickHashJoinPhysicalNode.JoinKeyPair> joinKeys, boolean useLeftKey) {
        Map<Object, List<JQuickRow>> hashTable = new HashMap<>();
        for (JQuickRow row : data.getRows()) {
            Object key = extractJoinKey(row, joinKeys, useLeftKey);
            if (key != null) {
                hashTable.computeIfAbsent(key, k -> new ArrayList<>()).add(row);
            }
        }
        return hashTable;
    }

    @Override
    public Object extractJoinKey(JQuickRow row, List<JQuickHashJoinPhysicalNode.JoinKeyPair> joinKeys, boolean useLeftKey) {
        if (joinKeys == null || joinKeys.isEmpty()) return null;
        if (joinKeys.size() > 1) {
            List<Object> compositeKey = new ArrayList<>();
            for (JQuickHashJoinPhysicalNode.JoinKeyPair keyPair : joinKeys) {
                compositeKey.add(getJoinKeyValue(row, keyPair, useLeftKey));
            }
            return compositeKey;
        }
        return getJoinKeyValue(row, joinKeys.get(0), useLeftKey);
    }

    /**
     * 判断是否应该使用 buildSide 优化
     * 默认返回 false，只有 INNER/NATURAL JOIN 才使用优化器推荐的构建侧
     * 外连接（LEFT/RIGHT/FULL）必须按语义固定构建侧和探测侧
     *
     * @return true 如果可以使用优化器推荐的构建侧
     */
    protected boolean shouldUseBuildSideOptimization() {
        return false;
    }

    /**
     * 获取单个 JOIN 键值
     */
    private Object getJoinKeyValue(JQuickRow row, JQuickHashJoinPhysicalNode.JoinKeyPair keyPair, boolean useLeftKey) {
        JQuickExpression keyExpr = useLeftKey ? keyPair.getLeftKey() : keyPair.getRightKey();
        if (keyExpr instanceof JQuickColumnRefExpression) {
            JQuickColumnRefExpression colRef = (JQuickColumnRefExpression) keyExpr;
            String colName = colRef.getColumnName();
            String tableAlias = colRef.getTableAlias();
            if (tableAlias != null && row.containsKey(tableAlias + "." + colName)) { //尝试带表别名的列名（如 "u.id"）
                return row.get(tableAlias + "." + colName);
            }
            if (row.containsKey(colName)) {  // 尝试不带前缀的列名（如 "id"）
                return row.get(colName);
            }
            if (row.containsKey("left." + colName)) { //尝试带 left/right 前缀的列名
                return row.get("left." + colName);
            }
            if (row.containsKey("right." + colName)) {
                return row.get("right." + colName);
            }
            for (String key : row.keySet()) {//模糊匹配：查找以列名结尾的键（兼容各种前缀）
                if (key.endsWith("." + colName)) {
                    return row.get(key);
                }
            }
        }
        return expressionEvaluator.evaluateExpression(row, keyExpr); // 复杂表达式：使用表达式求值器
    }

    @Override
    public JQuickRow joinRows(JQuickRow leftRow, JQuickRow rightRow) {
        JQuickRow result = new JQuickRow();
        if (leftRow != null) {
            for (Map.Entry<String, Object> entry : leftRow.entrySet()) {
                result.put("left." + entry.getKey(), entry.getValue());
            }
        }
        if (rightRow != null) {
            for (Map.Entry<String, Object> entry : rightRow.entrySet()) {
                result.put("right." + entry.getKey(), entry.getValue());
            }
        }
        if (leftRow != null) {
            for (Map.Entry<String, Object> entry : leftRow.entrySet()) {
                String key = entry.getKey();
                if (!result.containsKey(key)) {
                    result.put(key, entry.getValue());
                }
            }
        }
        if (rightRow != null) {
            for (Map.Entry<String, Object> entry : rightRow.entrySet()) {
                String key = entry.getKey();
                if (!result.containsKey(key)) {
                    result.put(key, entry.getValue());
                }
            }
        }

        return result;
    }

    @Override
    public boolean evaluateCondition(JQuickRow row, JQuickExpression condition) {
        if (condition == null) return true;
        return expressionEvaluator.evaluatePredicate(row, condition);
    }

    /**
     * 构建结果数据集的元数据
     *
     * @param leftData  左表数据
     * @param rightData 右表数据
     * @return 列元数据列表
     */
    protected List<JQuickColumnMeta> buildColumnMetas(JQuickDataSet leftData, JQuickDataSet rightData) {
        List<JQuickColumnMeta> columnMetas = new ArrayList<>();
        if (leftData != null) {
            columnMetas.addAll(leftData.getColumns());
        }
        if (rightData != null) {
            columnMetas.addAll(rightData.getColumns());
        }
        return columnMetas;
    }

    /**
     * 构建结果数据集
     *
     * @param resultRows 结果行列表
     * @param leftData   左表数据
     * @param rightData  右表数据
     * @return 结果数据集
     */
    protected JQuickDataSet buildResultDataSet(List<JQuickRow> resultRows, JQuickDataSet leftData, JQuickDataSet rightData) {
        List<JQuickColumnMeta> columnMetas = buildColumnMetas(leftData, rightData);
        return new JQuickDataSet(columnMetas, resultRows);
    }
}