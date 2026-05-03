package com.github.paohaijiao.client;

public interface JQuickJQuickClient {

    public String getType();

    public JQuickJQuickClient getClient();

    /**
     * 初始化客户端
     */
    default void init() {

    }
    /**
     * 关闭客户端
     */
    default void close() {

    }
    default boolean isAvailable() {
        return true;
    }
    default String getName() {
        return getType();
    }

    /**
     * 获取客户端版本
     */
    default String getVersion() {
        return "1.0.0";
    }
}
