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
package com.github.paohaijiao.stats;

import java.util.*;

/**
 * 数据分区信息
 */
public class JQuickDataPartition {

    private final String partitionId;

    private final String tableName;

    private final String location;

    private final long size;

    private final long rowCount;

    private final Map<String, Object> partitionValues;

    private final List<String> hosts;

    public JQuickDataPartition(String partitionId, String tableName, String location) {
        this(partitionId, tableName, location, 0, 0, new HashMap<>(), new ArrayList<>());
    }

    public JQuickDataPartition(String partitionId, String tableName, String location, long size, long rowCount, Map<String, Object> partitionValues, List<String> hosts) {
        this.partitionId = partitionId;
        this.tableName = tableName;
        this.location = location;
        this.size = size;
        this.rowCount = rowCount;
        this.partitionValues = partitionValues != null ? new HashMap<>(partitionValues) : new HashMap<>();
        this.hosts = hosts != null ? new ArrayList<>(hosts) : new ArrayList<>();
    }

    public String getPartitionId() { return partitionId; }

    public String getTableName() { return tableName; }

    public String getLocation() { return location; }

    public long getSize() { return size; }

    public long getRowCount() { return rowCount; }

    public Map<String, Object> getPartitionValues() { return Collections.unmodifiableMap(partitionValues); }

    public List<String> getHosts() { return Collections.unmodifiableList(hosts); }

    @Override
    public String toString() {
        return String.format("DataPartition{id='%s', table='%s', location='%s', rows=%d, size=%d}", partitionId, tableName, location, rowCount, size);
    }
}
