package com.github.paohaijiao.logic;
import java.util.List;
/**
 * 逻辑计划节点接口 - 纯描述性，不包含执行逻辑
 */
public interface JQuickLogicalPlanNode {

    /**
     * 获取节点类型
     */
    String getNodeType();

    /**
     * 获取子节点列表
     */
    List<JQuickLogicalPlanNode> getChildren();

    /**
     * 接受访问者
     */
    void accept(JQuickLogicalPlanVisitor visitor);

    /**
     * 获取输出列名
     */
    List<String> getOutputColumns();

    /**
     * 克隆节点
     */
    JQuickLogicalPlanNode clone();
}