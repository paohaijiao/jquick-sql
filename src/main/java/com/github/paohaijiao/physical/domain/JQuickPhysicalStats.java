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
package com.github.paohaijiao.physical.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * 节点统计信息
 */
public class JQuickPhysicalStats {

    private final long estimatedRowCount;

    private final long estimatedDataSize;

    private final Map<String, JQuickColumnStats> columnStats;

    public JQuickPhysicalStats(long estimatedRowCount, long estimatedDataSize, Map<String, JQuickColumnStats> columnStats) {
        this.estimatedRowCount = estimatedRowCount;
        this.estimatedDataSize = estimatedDataSize;
        this.columnStats = columnStats != null ? columnStats : new HashMap<>();
    }

    public long getEstimatedRowCount() { return estimatedRowCount; }

    public long getEstimatedDataSize() { return estimatedDataSize; }

    public Map<String, JQuickColumnStats> getColumnStats() { return columnStats; }

    public static JQuickPhysicalStats empty() {
        return new JQuickPhysicalStats(0, 0, new HashMap<>());
    }
}