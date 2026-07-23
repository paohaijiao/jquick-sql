package com.github.paohaijiao.distributed.worker.join;

import com.github.paohaijiao.enums.JQuickJoinType;
import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.physical.node.JQuickHashJoinPhysicalNode;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;

import java.util.List;
import java.util.Map;

/**
 * JOIN 处理器接口
 * 定义 JOIN 操作的通用行为
 */
public interface JQuickJoinHandler {

    /**
     * 获取支持的 JOIN 类型
     *
     * @return JOIN 类型
     */
    JQuickJoinType getJoinType();

    /**
     * 执行 JOIN 操作
     *
     * @param leftData  左表数据
     * @param rightData 右表数据
     * @param joinKeys  JOIN 键对列表
     * @param condition 额外条件表达式
     * @param buildLeft 是否以左表为构建表
     * @return JOIN 结果数据集
     */
    JQuickDataSet join(JQuickDataSet leftData, JQuickDataSet rightData, List<JQuickHashJoinPhysicalNode.JoinKeyPair> joinKeys, JQuickExpression condition, boolean buildLeft);

    /**
     * 构建哈希表（用于 Hash Join）
     *
     * @param data       构建表数据
     * @param joinKeys   JOIN 键对列表
     * @param useLeftKey 是否使用左表键
     * @return 哈希表（键 -> 行列表）
     */
    Map<Object, List<JQuickRow>> buildHashTable(JQuickDataSet data, List<JQuickHashJoinPhysicalNode.JoinKeyPair> joinKeys, boolean useLeftKey);

    /**
     * 提取 JOIN 键值
     *
     * @param row        数据行
     * @param joinKeys   JOIN 键对列表
     * @param useLeftKey 是否使用左表键
     * @return JOIN 键值
     */
    Object extractJoinKey(JQuickRow row, List<JQuickHashJoinPhysicalNode.JoinKeyPair> joinKeys, boolean useLeftKey);

    /**
     * 合并左右表行
     *
     * @param leftRow  左表行
     * @param rightRow 右表行
     * @return 合并后的行
     */
    JQuickRow joinRows(JQuickRow leftRow, JQuickRow rightRow);

    /**
     * 评估条件表达式
     *
     * @param row       数据行
     * @param condition 条件表达式
     * @return 条件是否满足
     */
    boolean evaluateCondition(JQuickRow row, JQuickExpression condition);
}