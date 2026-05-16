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
package com.github.paohaijiao.executor;
import com.github.paohaijiao.statement.JQuickDataSet;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 执行上下文 - 在整个SQL执行过程中传递状态和缓存
 */
public class JQuickExecutionContext {

    private final Map<String, JQuickDataSet> cteCache = new ConcurrentHashMap<>();

    // 参数映射（用于预处理语句）
    private final Map<String, Object> parameters = new HashMap<>();

    // 变量映射（用于运行时变量）
    private final Map<String, Object> variables = new ConcurrentHashMap<>();

    // 临时表缓存
    private final Map<String, JQuickDataSet> tempTables = new HashMap<>();

    // 执行ID（用于追踪）
    private final String executionId;

    // 父上下文（用于子查询）
    private JQuickExecutionContext parent;

    // 查询超时时间（毫秒）
    private long queryTimeout = 30000;

    // 开始时间
    private long startTime;

    // 是否已取消
    private boolean cancelled = false;

    // 执行统计
    private final ExecutionStats stats = new ExecutionStats();

    public JQuickExecutionContext() {
        this.executionId = UUID.randomUUID().toString();
        this.startTime = System.currentTimeMillis();
    }

    private JQuickExecutionContext(JQuickExecutionContext parent) {
        this.executionId = parent.executionId + "_sub_" + UUID.randomUUID().toString().substring(0, 8);
        this.parent = parent;
        this.parameters.putAll(parent.parameters);
        this.startTime = System.currentTimeMillis();
        this.queryTimeout = parent.queryTimeout;
    }

    /**
     * 缓存CTE结果
     */
    public void cacheCTE(String name, JQuickDataSet data) {
        cteCache.put(name.toLowerCase(), data);
    }

    /**
     * 获取缓存的CTE结果
     */
    public JQuickDataSet getCTE(String name) {
        JQuickDataSet data = cteCache.get(name.toLowerCase());
        if (data == null && parent != null) {
            return parent.getCTE(name);
        }
        return data;
    }

    /**
     * 检查CTE是否已缓存
     */
    public boolean hasCTE(String name) {
        return cteCache.containsKey(name.toLowerCase()) ||
                (parent != null && parent.hasCTE(name));
    }

    /**
     * 设置参数（位置参数，如 ?）
     */
    public void setParameter(int index, Object value) {
        parameters.put(String.valueOf(index), value);
    }

    /**
     * 设置参数（命名参数，如 :name）
     */
    public void setParameter(String name, Object value) {
        parameters.put(name, value);
    }

    /**
     * 获取参数
     */
    @SuppressWarnings("unchecked")
    public <T> T getParameter(String name) {
        Object value = parameters.get(name);
        if (value == null && parent != null) {
            return parent.getParameter(name);
        }
        return (T) value;
    }

    /**
     * 获取参数（带默认值）
     */
    @SuppressWarnings("unchecked")
    public <T> T getParameter(String name, T defaultValue) {
        Object value = parameters.get(name);
        if (value == null && parent != null) {
            return parent.getParameter(name, defaultValue);
        }
        return value != null ? (T) value : defaultValue;
    }

    /**
     * 获取位置参数
     */
    public Object getParameter(int index) {
        return getParameter(String.valueOf(index));
    }

    /**
     * 批量设置参数
     */
    public void setParameters(Map<String, Object> params) {
        this.parameters.putAll(params);
    }

    /**
     * 获取所有参数
     */
    public Map<String, Object> getParameters() {
        return new HashMap<>(parameters);
    }

    /**
     * 设置变量
     */
    public void setVariable(String name, Object value) {
        variables.put(name, value);
    }

    /**
     * 获取变量
     */
    @SuppressWarnings("unchecked")
    public <T> T getVariable(String name) {
        Object value = variables.get(name);
        if (value == null && parent != null) {
            return parent.getVariable(name);
        }
        return (T) value;
    }

    /**
     * 检查变量是否存在
     */
    public boolean hasVariable(String name) {
        return variables.containsKey(name) || (parent != null && parent.hasVariable(name));
    }

    /**
     * 移除变量
     */
    public void removeVariable(String name) {
        variables.remove(name);
    }

    /**
     * 创建临时表
     */
    public void createTempTable(String name, JQuickDataSet data) {
        tempTables.put(name, data);
    }

    /**
     * 获取临时表
     */
    public JQuickDataSet getTempTable(String name) {
        return tempTables.get(name);
    }

    /**
     * 删除临时表
     */
    public void dropTempTable(String name) {
        tempTables.remove(name);
    }

    /**
     * 创建子上下文（用于子查询）
     */
    public JQuickExecutionContext createChildContext() {
        return new JQuickExecutionContext(this);
    }

    /**
     * 获取父上下文
     */
    public JQuickExecutionContext getParent() {
        return parent;
    }

    /**
     * 设置查询超时时间
     */
    public void setQueryTimeout(long timeoutMillis) {
        this.queryTimeout = timeoutMillis;
    }

    /**
     * 检查是否超时
     */
    public void checkTimeout() {
        if (cancelled) {
            throw new RuntimeException("Query execution cancelled");
        }
        if (System.currentTimeMillis() - startTime > queryTimeout) {
            throw new RuntimeException("Query execution timeout after " + queryTimeout + " ms");
        }
    }

    /**
     * 取消执行
     */
    public void cancel() {
        this.cancelled = true;
    }

    /**
     * 是否已取消
     */
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * 记录步骤执行时间
     */
    public void recordStep(String stepName, long duration) {
        stats.recordStep(stepName, duration);
    }

    /**
     * 增加扫描行数
     */
    public void addScannedRows(long count) {
        stats.addScannedRows(count);
    }

    /**
     * 增加返回行数
     */
    public void addReturnedRows(long count) {
        stats.addReturnedRows(count);
    }

    /**
     * 获取执行统计
     */
    public ExecutionStats getStats() {
        return stats;
    }

    /**
     * 打印执行统计
     */
    public void printStats() {
        stats.print();
    }

    /**
     * 获取执行ID
     */
    public String getExecutionId() {
        return executionId;
    }

    /**
     * 获取执行耗时
     */
    public long getElapsedTime() {
        return System.currentTimeMillis() - startTime;
    }


    public static class ExecutionStats {
        private final Map<String, Long> stepDurations = new HashMap<>();

        private long scannedRows = 0;

        private long returnedRows = 0;

        public void recordStep(String stepName, long duration) {
            stepDurations.merge(stepName, duration, Long::sum);
        }

        public void addScannedRows(long count) {
            this.scannedRows += count;
        }

        public void addReturnedRows(long count) {
            this.returnedRows += count;
        }

        public void print() {
            System.out.println("=== Execution Statistics ===");
            System.out.println("Execution ID: " + UUID.randomUUID().toString());
            System.out.println("Scanned Rows: " + scannedRows);
            System.out.println("Returned Rows: " + returnedRows);
            System.out.println("Step Details:");
            for (Map.Entry<String, Long> entry : stepDurations.entrySet()) {
                System.out.println("  " + entry.getKey() + ": " + entry.getValue() + " ms");
            }
            System.out.println("============================");
        }

        public Map<String, Long> getStepDurations() {
            return new HashMap<>(stepDurations);
        }

        public long getScannedRows() {
            return scannedRows;
        }

        public long getReturnedRows() {
            return returnedRows;
        }
    }
}
