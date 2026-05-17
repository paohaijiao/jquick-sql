package com.github.paohaijiao.logic;

import com.github.paohaijiao.context.JQuickExecutionContext;
import com.github.paohaijiao.statement.JQuickDataSet;

import java.util.List;

public interface JQuickLogicalPlanNode {
    /**
     * 执行逻辑计划节点
     * @param context 执行上下文
     * @return 执行结果数据集
     */
    JQuickDataSet execute(JQuickExecutionContext context);

    /**
     * 获取节点类型名称
     */
    String getNodeType();

    /**
     * 获取子节点列表
     */
    List<JQuickLogicalPlanNode> getChildren();

    /**
     * 接受访问者
     * @param visitor 访问者
     */
    void accept(JQuickLogicalPlanVisitor visitor);

    /**
     * 获取节点的输出列名
     */
    List<String> getOutputColumns();

    /**
     * 克隆节点（用于优化器）
     */
    JQuickLogicalPlanNode clone();
}
