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

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * packageName com.github.paohaijiao.dataset
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/8/13
 */
public class JDataSet {

    private final List<JColumnMeta> columns;

    private final List<Map<String, Object>> rows;


    public JDataSet(List<JColumnMeta> columns, List<Map<String, Object>> rows) {
        this.columns = Collections.unmodifiableList(new ArrayList<>(columns));
        this.rows = Collections.unmodifiableList(new ArrayList<>(rows));
    }

    public List<JColumnMeta> getColumns() {
        return columns;
    }
    public List<Map<String, Object>> getRows() {
        return rows;
    }

    public Stream<Object> getColumnValues(String columnName) {
        return rows.stream().map(row -> row.get(columnName));
    }

    public List<String> getColumnNames() {
        return columns.stream().map(JColumnMeta::getName).collect(Collectors.toList());
    }

    public int size() {
        return rows.size();
    }

    public boolean isEmpty() {
        return rows.isEmpty();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final List<JColumnMeta> columns = new ArrayList<>();
        private final List<Map<String, Object>> rows = new ArrayList<>();

        public Builder addColumn(String name, Class<?> type, String source) {
            columns.add(new JColumnMeta(name, type, source));
            return this;
        }

        public Builder addRow(Map<String, Object> row) {
            rows.add(new HashMap<>(row));
            return this;
        }

        public JDataSet build() {
            return new JDataSet(columns, rows);
        }
    }
}
