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
package com.github.paohaijiao.dataset;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * packageName com.github.paohaijiao.dataset
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/8/14
 */
public class JRow implements Map<String, Object> {

    private final Map<String, Object> data;


    /**
     * Creates an empty JRow with no associated table name.
     */
    public JRow() {
        this.data = new HashMap<>();
    }

    /**
     * Creates a JRow with the specified table name.
     *
     * @param tableName the name of the table this row belongs to
     */
    public JRow(String tableName) {
        this();
    }

    /**
     * Creates a JRow initialized with the given map data.
     *
     * @param data the initial data for this row
     */
    public JRow(Map<String, Object> data) {
        this.data = new HashMap<>(data);
    }




    @Override
    public int size() {
        return data.size();
    }

    @Override
    public boolean isEmpty() {
        return data.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return data.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return data.containsValue(value);
    }

    @Override
    public Object get(Object key) {
        return data.get(key);
    }

    @Override
    public Object put(String key, Object value) {
        return data.put(key, value);
    }

    @Override
    public Object remove(Object key) {
        return data.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ?> m) {
        data.putAll(m);
    }

    @Override
    public void clear() {
        data.clear();
    }

    @Override
    public Set<String> keySet() {
        return data.keySet();
    }

    @Override
    public Collection<Object> values() {
        return data.values();
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return data.entrySet();
    }

    // Convenience methods for database operations
    /**
     * Gets the value for the specified column as a String.
     *
     * @param columnName the column name
     * @return the String value, or null if the value is null
     * @throws ClassCastException if the value is not a String
     */
    public String getString(String columnName) {
        return (String) data.get(columnName);
    }

    /**
     * Gets the value for the specified column as an Integer.
     *
     * @param columnName the column name
     * @return the Integer value, or null if the value is null
     * @throws ClassCastException if the value is not an Integer
     */
    public Integer getInt(String columnName) {
        return (Integer) data.get(columnName);
    }

    /**
     * Gets the value for the specified column as a Long.
     *
     * @param columnName the column name
     * @return the Long value, or null if the value is null
     * @throws ClassCastException if the value is not a Long
     */
    public Long getLong(String columnName) {
        return (Long) data.get(columnName);
    }

    /**
     * Gets the value for the specified column as a Double.
     *
     * @param columnName the column name
     * @return the Double value, or null if the value is null
     * @throws ClassCastException if the value is not a Double
     */
    public Double getDouble(String columnName) {
        return (Double) data.get(columnName);
    }

    /**
     * Gets the value for the specified column as a Boolean.
     *
     * @param columnName the column name
     * @return the Boolean value, or null if the value is null
     * @throws ClassCastException if the value is not a Boolean
     */
    public Boolean getBoolean(String columnName) {
        return (Boolean) data.get(columnName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JRow jRow = (JRow) o;
        return data.equals(jRow.data) ;
    }

    @Override
    public int hashCode() {
        int result = data.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "JRow{" +
                " data=" + data +
                '}';
    }
}
