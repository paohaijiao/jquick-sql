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
package com.github.paohaijiao.condition;

import com.github.paohaijiao.dataset.JRow;

import java.util.function.Predicate;

/**
 * packageName com.github.paohaijiao.condition
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/8/23
 */
public class JSimpleCondition  extends JCondition {

    private final Predicate<JRow> predicate;

    public JSimpleCondition(Predicate<JRow> predicate) {
        this.predicate = predicate;
    }

    @Override
    public boolean test(JRow row) {
        return predicate.test(row);
    }

    // 便捷方法
    public static JCondition of(Predicate<JRow> predicate) {
        return new JSimpleCondition(predicate);
    }

    // 常用的简单条件
    public static JCondition equals(String columnName, Object value) {
        return of(row -> {
            Object rowValue = row.get(columnName);
            return rowValue != null && rowValue.equals(value);
        });
    }

    public static JCondition notEquals(String columnName, Object value) {
        return of(row -> {
            Object rowValue = row.get(columnName);
            return rowValue != null && !rowValue.equals(value);
        });
    }

    public static JCondition greaterThan(String columnName, Comparable value) {
        return of(row -> {
            Object rowValue = row.get(columnName);
            return rowValue != null && ((Comparable) rowValue).compareTo(value) > 0;
        });
    }

    public static JCondition lessThan(String columnName, Comparable value) {
        return of(row -> {
            Object rowValue = row.get(columnName);
            return rowValue != null && ((Comparable) rowValue).compareTo(value) < 0;
        });
    }
}
