package com.github.paohaijiao.expression;

import com.github.paohaijiao.statement.JQuickRow;

import java.io.Serializable;
import java.util.List;

/**
 * 表达式接口 - 所有表达式节点的基类
 */
public interface JQuickExpression extends Serializable {

    /**
     * 计算表达式的值
     * @param row 当前行数据
     * @return 计算结果
     */
    Object evaluate(JQuickRow row);

    /**
     * 获取表达式的返回值类型
     */
    Class<?> getType();

    /**
     * 判断表达式是否为常量（不依赖行数据）
     */
    boolean isConstant();

    /**
     * 获取表达式引用的列名列表
     */
    List<String> getReferencedColumns();

    /**
     * 转换为SQL字符串
     */
    String toSql();

    /**
     * 克隆表达式
     */
    JQuickExpression clone();
}
