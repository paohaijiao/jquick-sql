package com.github.paohaijiao.distributed.worker.join;

import com.github.paohaijiao.distributed.worker.JQuickExpressionEvaluator;
import com.github.paohaijiao.enums.JQuickJoinType;

import java.util.EnumMap;
import java.util.Map;

/**
 * JOIN 处理器工厂类
 * 根据 JOIN 类型创建对应的处理器实例
 */
public class JQuickJoinHandlerFactory {

    private final Map<JQuickJoinType, JQuickJoinHandler> handlers = new EnumMap<>(JQuickJoinType.class);

    /**
     * 构造函数：初始化所有 JOIN 处理器
     */
    public JQuickJoinHandlerFactory(JQuickExpressionEvaluator expressionEvaluator) {
        handlers.put(JQuickJoinType.INNER, new JQuickInnerJoinHandler(expressionEvaluator));
        handlers.put(JQuickJoinType.CROSS, new JQuickCrossJoinHandler(expressionEvaluator));
        handlers.put(JQuickJoinType.LEFT, new JQuickLeftJoinHandler(expressionEvaluator));
        handlers.put(JQuickJoinType.RIGHT, new JQuickRightJoinHandler(expressionEvaluator));
        handlers.put(JQuickJoinType.NATURAL, new JQuickNaturalJoinHandler(expressionEvaluator));
        handlers.put(JQuickJoinType.FULL, new JQuickFullJoinHandler(expressionEvaluator));
    }

    /**
     * 根据 JOIN 类型获取对应的处理器
     *
     * @param joinType JOIN 类型
     * @return JOIN 处理器
     */
    public JQuickJoinHandler getHandler(JQuickJoinType joinType) {
        JQuickJoinHandler handler = handlers.get(joinType);
        if (handler == null) {
            return handlers.get(JQuickJoinType.INNER);// 默认返回 INNER JOIN 处理器
        }
        return handler;
    }

    /**
     * 获取所有支持的 JOIN 类型
     *
     * @return JOIN 类型集合
     */
    public java.util.Set<JQuickJoinType> getSupportedJoinTypes() {
        return handlers.keySet();
    }

    /**
     * 注册自定义 JOIN 处理器
     *
     * @param joinType JOIN 类型
     * @param handler  JOIN 处理器
     */
    public void registerHandler(JQuickJoinType joinType, JQuickJoinHandler handler) {
        handlers.put(joinType, handler);
    }
}