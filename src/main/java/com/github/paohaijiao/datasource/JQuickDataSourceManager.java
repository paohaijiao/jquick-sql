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
package com.github.paohaijiao.datasource;


import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JQuickDataSourceManager {

    private static final Map<String, JQuickDataSet> dataSources = new ConcurrentHashMap<>();

    private static final Map<String, Map<Object, JQuickDataSet>> indexes = new ConcurrentHashMap<>();

    private static final Map<String, Long> tableSizes = new ConcurrentHashMap<>();

    public static void registerTable(String name, JQuickDataSet data) {
        dataSources.put(name.toLowerCase(), data);
        tableSizes.put(name.toLowerCase(), (long) data.size());
    }

    public static JQuickDataSet getTable(String name) {
        JQuickDataSet data = dataSources.get(name.toLowerCase());
        if (data == null) {
            throw new RuntimeException("Table not found: " + name);
        }
        return data;
    }

    public static JQuickDataSet getTableByIndex(String tableName, String columnName, Object value) {
        String indexKey = tableName + "." + columnName;
        Map<Object, JQuickDataSet> indexMap = indexes.get(indexKey);
        if (indexMap != null) {
            JQuickDataSet result = indexMap.get(value);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    public static void createIndex(String tableName, String columnName) {
        JQuickDataSet table = getTable(tableName);
        Map<Object, JQuickDataSet> indexMap = new HashMap<>();
        Map<Object, java.util.List<JQuickRow>> grouped = table.groupBy(columnName);
        for (Map.Entry<Object, java.util.List<JQuickRow>> entry : grouped.entrySet()) {
            indexMap.put(entry.getKey(), new JQuickDataSet(table.getColumns(), entry.getValue()));
        }

        indexes.put(tableName + "." + columnName, indexMap);
    }

    public static long getTableSize(String tableName) {
        return tableSizes.getOrDefault(tableName.toLowerCase(), 0L);
    }

    public static boolean hasIndex(String tableName, String columnName) {
        return indexes.containsKey(tableName + "." + columnName);
    }
}
