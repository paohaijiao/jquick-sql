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
     * 接受访问者，触发对应的 visit 方法，不用自己写遍历逻辑
     *
     * 用途：
     * - 将算法与数据结构解耦
     * - 新增操作（打印、优化、分析）无需修改节点类
     * 实现要点：
     * - 仅调用 visitor.visit(this)
     * - 不在此方法内遍历子节点（由 visitor 决定遍历时机）
     */
    void accept(JQuickLogicalPlanVisitor visitor);

    /**
     * 获取输出列名
     */
    List<String> getOutputColumns();

    /**
     * 深拷贝当前节点及其整个子树
     *
     * 用途：
     * - 优化器修改计划前创建副本，避免破坏原计划
     * - 在逻辑优化器中会不断地改写计划树（增加、删除、移动节点）。如果直接在原树上修改，会破坏原始计
     * 实现要点：
     * - 递归 clone 所有子节点
     * - 递归 clone 所有表达式（JQuickExpression）
     * - 不可变对象（如 JQuickEmptyNode）返回自身
     */
    JQuickLogicalPlanNode clone();
}