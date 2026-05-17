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
package com.github.paohaijiao.context;
import com.github.paohaijiao.config.JQuickConfiguration;
import com.github.paohaijiao.logic.JQuickLogicalPlanNode;
import com.github.paohaijiao.logic.domain.JQuickLimitNode;
import com.github.paohaijiao.logic.domain.JQuickProjectNode;
import com.github.paohaijiao.logic.domain.JQuickSortNode;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.stats.JQuickExecutionStats;
import lombok.Data;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 执行上下文 - 在整个SQL执行过程中传递状态和缓存
 */
@Data
public class JQuickExecutionContext {

    private final Map<String, JQuickDataSet> cteCache = new ConcurrentHashMap<>();

    private final Map<String, Object> parameters = new HashMap<>();

    private final Map<String, Object> variables = new ConcurrentHashMap<>();

    private final Map<String, JQuickDataSet> tempTables = new HashMap<>();

    private final String executionId;

    private JQuickExecutionContext parent;

    private JQuickConfiguration configuration;


    private long queryTimeout = 30000;

    private long startTime;

    private boolean cancelled = false;

    private final JQuickExecutionStats stats = new JQuickExecutionStats();
    // 全局排序要求
    private boolean globalSort = false;

    private List<JQuickSortNode.OrderByItem> globalSortItems = new ArrayList<>();

    // 全局去重要求
    private boolean globalDistinct = false;

    // 全局LIMIT
    private boolean hasLimit = false;

    private int limit = -1;

    private int offset = 0;

    // 性能分析开关
    private boolean profileEnabled = false;

    private long resultSize = 0;

    // 子查询结果缓存
    private final Map<String, JQuickDataSet> subqueryCache = new ConcurrentHashMap<>();

    // 分布式执行相关
    private boolean distributedMode = false;

    private String jobId;


    /**
     * 设置全局排序
     */
    public void setGlobalSort(List<JQuickSortNode.OrderByItem> sortItems) {
        this.globalSort = true;
        this.globalSortItems = sortItems != null ? new ArrayList<>(sortItems) : new ArrayList<>();
    }

    /**
     * 添加排序项
     */
    public void addGlobalSortItem(String columnName, boolean ascending) {
        this.globalSort = true;
        this.globalSortItems.add(new JQuickSortNode.OrderByItem(columnName, ascending));
    }

    /**
     * 是否有全局排序要求
     */
    public boolean hasGlobalSort() {
        return globalSort && !globalSortItems.isEmpty();
    }

    /**
     * 获取全局排序项
     */
    public List<JQuickSortNode.OrderByItem> getGlobalSortItems() {
        return new ArrayList<>(globalSortItems);
    }

    /**
     * 设置全局去重
     */
    public void setGlobalDistinct(boolean distinct) {
        this.globalDistinct = distinct;
    }

    /**
     * 是否有全局去重要求
     */
    public boolean hasGlobalDistinct() {
        return globalDistinct;
    }

    /**
     * 设置全局LIMIT
     */
    public void setGlobalLimit(int limit, int offset) {
        this.hasLimit = true;
        this.limit = limit;
        this.offset = offset;
    }

    /**
     * 设置全局LIMIT（无偏移量）
     */
    public void setGlobalLimit(int limit) {
        this.hasLimit = true;
        this.limit = limit;
        this.offset = 0;
    }

    /**
     * 是否有LIMIT要求
     */
    public boolean hasLimit() {
        return hasLimit && limit > 0;
    }

    /**
     * 获取LIMIT值
     */
    public int getLimit() {
        return limit;
    }

    /**
     * 获取OFFSET值
     */
    public int getOffset() {
        return offset;
    }

    /**
     * 启用/禁用性能分析
     */
    public void setProfileEnabled(boolean enabled) {
        this.profileEnabled = enabled;
    }

    /**
     * 是否启用性能分析
     */
    public boolean isProfileEnabled() {
        return profileEnabled;
    }

    /**
     * 记录结果集大小
     */
    public void recordResultSize(long size) {
        this.resultSize = size;
        if (profileEnabled) {
            stats.addReturnedRows(size);
        }
    }

    /**
     * 获取结果集大小
     */
    public long getResultSize() {
        return resultSize;
    }

    /**
     * 缓存子查询结果
     */
    public void cacheSubqueryResult(String key, JQuickDataSet result) {
        subqueryCache.put(key, result);
    }

    /**
     * 获取缓存的子查询结果
     */
    public JQuickDataSet getCachedSubqueryResult(String key) {
        return subqueryCache.get(key);
    }

    /**
     * 清除子查询缓存
     */
    public void clearSubqueryCache() {
        subqueryCache.clear();
    }

    /**
     * 设置分布式模式
     */
    public void setDistributedMode(boolean enabled) {
        this.distributedMode = enabled;
    }

    /**
     * 是否分布式模式
     */
    public boolean isDistributedMode() {
        return distributedMode;
    }

    /**
     * 设置作业ID
     */
    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    /**
     * 获取作业ID
     */
    public String getJobId() {
        return jobId;
    }

    // ========== 原有的构造器和方法保持不变 ==========

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
        // 继承全局设置
        this.globalSort = parent.globalSort;
        this.globalSortItems = new ArrayList<>(parent.globalSortItems);
        this.globalDistinct = parent.globalDistinct;
        this.hasLimit = parent.hasLimit;
        this.limit = parent.limit;
        this.offset = parent.offset;
        this.profileEnabled = parent.profileEnabled;
    }


    /**
     * 创建子上下文（用于子查询）- 需要覆盖以继承全局设置
     */
    public JQuickExecutionContext createChildContext() {
        return new JQuickExecutionContext(this);
    }

    /**
     * 重置全局设置（用于新的查询）
     */
    public void resetGlobalSettings() {
        this.globalSort = false;
        this.globalSortItems.clear();
        this.globalDistinct = false;
        this.hasLimit = false;
        this.limit = -1;
        this.offset = 0;
        this.resultSize = 0;
        this.subqueryCache.clear();
    }

    /**
     * 从SQL查询中提取全局设置
     * 例如：ORDER BY, DISTINCT, LIMIT 等
     */
    public void extractGlobalSettingsFromPlan(JQuickLogicalPlanNode plan) {
        if (plan instanceof JQuickSortNode) {
            JQuickSortNode sortNode = (JQuickSortNode) plan;
            setGlobalSort(sortNode.getOrderByItems());
        }

        if (plan instanceof JQuickProjectNode) {
            JQuickProjectNode projectNode = (JQuickProjectNode) plan;
            if (projectNode.isDistinct()) {
                setGlobalDistinct(true);
            }
        }

        if (plan instanceof JQuickLimitNode) {
            JQuickLimitNode limitNode = (JQuickLimitNode) plan;
            setGlobalLimit(limitNode.getLimit(), limitNode.getOffset());
        }
        for (JQuickLogicalPlanNode child : plan.getChildren()) {
            extractGlobalSettingsFromPlan(child);
        }
    }

}
