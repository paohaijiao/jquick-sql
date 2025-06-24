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
package com.github.paohaijiao.model;

import java.util.HashMap;
import java.util.Map;

/**
 * packageName com.github.paohaijiao.model
 *
 * @author Martin
 * @version 1.0.0
 * @className JTypeRow
 * @date 2025/6/24
 * @description
 */
public class JTypeRow extends JRow{
    private final Map<String, Class<?>> columnTypes;

    public JTypeRow() {
        super();
        this.columnTypes = new HashMap<>();
    }

    @Override
    public void set(String columnName, Object value) {
        super.set(columnName, value);
        if (value != null) {
            columnTypes.put(columnName, value.getClass());
        }
    }

    public Class<?> getColumnType(String columnName) {
        return columnTypes.get(columnName);
    }

    public <T> void setWithType(String columnName, T value, Class<T> type) {
        super.set(columnName, value);
        columnTypes.put(columnName, type);
    }

    @Override
    public Object remove(String columnName) {
        columnTypes.remove(columnName);
        return super.remove(columnName);
    }

    @Override
    public void clear() {
        super.clear();
        columnTypes.clear();
    }
}
