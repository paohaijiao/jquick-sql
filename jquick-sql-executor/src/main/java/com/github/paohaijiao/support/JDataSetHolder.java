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
package com.github.paohaijiao.support;

import com.github.paohaijiao.dataset.JDataSet;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * packageName com.github.paohaijiao.support
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/8/21
 */
public class JDataSetHolder {

    private final Map<String, JDataSet> dataSetMap = new ConcurrentHashMap<>();

    private final Map<String, String> aliasToTableNameMap = new ConcurrentHashMap<>();

    public JDataSetHolder addDataSet(String tableName, JDataSet dataSet) {
        if (tableName == null || tableName.trim().isEmpty()) {
            throw new IllegalArgumentException("tableName required not null");
        }
        dataSetMap.put(tableName.toLowerCase(), dataSet);
        return this;
    }
    public JDataSetHolder addAlias(String tableName, String alias) {
        if (tableName == null || alias == null) {
            throw new IllegalArgumentException("tableName and alias required not null");
        }
        if (!dataSetMap.containsKey(tableName.toLowerCase())) {
            throw new IllegalArgumentException("table '" + tableName + "' not exists");
        }
        aliasToTableNameMap.put(alias.toLowerCase(), tableName.toLowerCase());
        return this;
    }

    public JDataSetHolder addAliases(String tableName, String... aliases) {
        for (String alias : aliases) {
            addAlias(tableName, alias);
        }
        return this;
    }
    public JDataSet getDataSet(String name) {
        if (name == null) {
            return null;
        }
        String key = name.toLowerCase();
        if (dataSetMap.containsKey(key)) {
            return dataSetMap.get(key);
        }
        if (aliasToTableNameMap.containsKey(key)) {
            String actualTableName = aliasToTableNameMap.get(key);
            return dataSetMap.get(actualTableName);
        }
        return null;
    }
    public boolean containsDataSet(String name) {
        if (name == null) {
            return false;
        }
        String key = name.toLowerCase();
        return dataSetMap.containsKey(key) || aliasToTableNameMap.containsKey(key);
    }
    public JDataSet removeDataSet(String tableName) {
        if (tableName == null) {
            return null;
        }
        String key = tableName.toLowerCase();
        JDataSet removed = dataSetMap.remove(key);
        if (removed != null) {
            aliasToTableNameMap.entrySet().removeIf(entry ->
                    entry.getValue().equals(key));
        }
        return removed;
    }
    public String removeAlias(String alias) {
        if (alias == null) {
            return null;
        }
        return aliasToTableNameMap.remove(alias.toLowerCase());
    }
    public Set<String> getTableNames() {
        return Collections.unmodifiableSet(dataSetMap.keySet());
    }
    public Set<String> getAliases() {
        return Collections.unmodifiableSet(aliasToTableNameMap.keySet());
    }
    public String getTableNameByAlias(String alias) {
        if (alias == null) {
            return null;
        }
        return aliasToTableNameMap.get(alias.toLowerCase());
    }
    public Collection<JDataSet> getAllDataSets() {
        return Collections.unmodifiableCollection(dataSetMap.values());
    }
    public void clear() {
        dataSetMap.clear();
        aliasToTableNameMap.clear();
    }
    public int size() {
        return dataSetMap.size();
    }
    public boolean isEmpty() {
        return dataSetMap.isEmpty();
    }
    public String getStats() {
        return String.format("DataSetContainer{table num: %d, alias num: %d}",
                dataSetMap.size(), aliasToTableNameMap.size());
    }
}
