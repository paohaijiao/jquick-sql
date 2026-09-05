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


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JQuickTablePartitionInfo {

    private final String tableName;

    private final List<Partition> partitions;

    private final String partitionColumn;

    public static class Partition {

        private final String location;

        private final long size;

        private final long rowCount;

        private final Map<String, Object> partitionValues;

        public Partition(String location, long size, long rowCount, Map<String, Object> partitionValues) {
            this.location = location;
            this.size = size;
            this.rowCount = rowCount;
            this.partitionValues = partitionValues != null ? partitionValues : new HashMap<>();
        }

        public String getLocation() { return location; }

        public long getSize() { return size; }

        public long getRowCount() { return rowCount; }

        public Map<String, Object> getPartitionValues() { return partitionValues; }
    }

    public JQuickTablePartitionInfo(String tableName, List<Partition> partitions, String partitionColumn) {
        this.tableName = tableName;
        this.partitions = partitions != null ? partitions : new ArrayList<>();
        this.partitionColumn = partitionColumn;
    }

    public String getTableName() { return tableName; }

    public List<Partition> getPartitions() { return partitions; }

    public String getPartitionColumn() { return partitionColumn; }

    public int getTotalPartitions() { return partitions.size(); }
}
