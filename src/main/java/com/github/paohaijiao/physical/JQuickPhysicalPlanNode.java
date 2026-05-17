package com.github.paohaijiao.physical;

import com.github.paohaijiao.context.JQuickExecutionContext;
import com.github.paohaijiao.statement.JQuickDataSet;

import java.util.List;

public interface JQuickPhysicalPlanNode {

    /**
     * 执行物理计划 - 只有这里有执行逻辑
     */
    JQuickDataSet execute(JQuickExecutionContext context);

    /**
     * 获取节点类型
     */
    String getNodeType();

    /**
     * 获取估算成本
     */
    long getEstimatedCost();

    /**
     * 获取子节点
     */
    List<JQuickPhysicalPlanNode> getChildren();
}
