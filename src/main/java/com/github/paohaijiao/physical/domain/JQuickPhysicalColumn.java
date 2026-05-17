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

import java.util.Objects;

/**
 * 物理列定义
 */
public class JQuickPhysicalColumn {

    private final String name;

    private final Class<?> type;

    private final String sourceTable;

    private final boolean nullable;

    public JQuickPhysicalColumn(String name, Class<?> type, String sourceTable, boolean nullable) {
        this.name = name;
        this.type = type;
        this.sourceTable = sourceTable;
        this.nullable = nullable;
    }

    public String getName() { return name; }
    public Class<?> getType() { return type; }
    public String getSourceTable() { return sourceTable; }
    public boolean isNullable() { return nullable; }

    @Override
    public String toString() {
        return name + ":" + type.getSimpleName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JQuickPhysicalColumn that = (JQuickPhysicalColumn) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}