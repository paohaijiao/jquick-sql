/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Copyright (c) [2025-2099] Martin (goudingcheng@gmail.com)
 */
package com.github.paohaijiao.log;

import com.github.paohaijiao.console.JConsole;

/**
 * packageName com.github.paohaijiao.log
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/5/1
 */
public class JQuickSqlLogManager {

    private static JConsole jConsole;

    private JQuickSqlLogManager() {
    }

    public static JConsole getConsole() {
        if (jConsole == null) {
            synchronized (JQuickSqlLogManager.class) {
                if (jConsole == null) {
                    jConsole = JConsole.initConsoleEnvironment();
                    printInfo("JConsole 环境初始化完成");
                }
            }
        }
        return jConsole;
    }

    /**
     * 判断 JConsole 是否已经加载初始化
     */
    public static boolean isLoaded() {
        return jConsole != null;
    }

    /**
     * 手动销毁重置控制台实例
     */
    public static void destroy() {
        if (jConsole != null) {
            jConsole = null;
            printWarn("JConsole 已手动销毁重置");
        }
    }

    public static boolean isConsoleLoaded() {
        return jConsole != null;
    }

    public static void reset() {
        jConsole = null;
    }

    /**
     * 普通信息日志
     */
    public static void printInfo(String msg) {
        getConsole().info("[INFO] " + msg);
    }

    /**
     * 警告日志
     */
    public static void printWarn(String msg) {
        getConsole().warn("[WARN] " + msg);
    }

    /**
     * 错误日志
     */
    public static void printError(String msg) {
        getConsole().error("[ERROR] " + msg);
    }

    /**
     * 直接输出原始文本
     */
    public static void debug(String msg) {
        getConsole().debug(msg);
    }

    /**
     * 打印执行的 SQL 语句
     */
    public static void infoSql(String sql) {
        getConsole().info("========================================");
        getConsole().info("【执行SQL】\n" + sql);
        getConsole().info("========================================");
    }

    /**
     * 打印 SQL 执行耗时
     */
    public static void infoSqlCost(String sql, long costMs) {
        getConsole().info("【SQL执行耗时】" + costMs + " ms");
        infoSql(sql);
    }

    /**
     * 打印异常堆栈简易信息
     */
    public static void printException(Exception e) {
        printError("异常信息：" + e.getMessage());
        e.printStackTrace();
    }

    /**
     * 打印分隔线
     */
    public static void printLine() {
        getConsole().info("----------------------------------------");
    }

    /**
     * 打印标题块
     */
    public static void printTitle(String title) {
        printLine();
        getConsole().info("===== " + title + " =====");
        printLine();
    }
}
