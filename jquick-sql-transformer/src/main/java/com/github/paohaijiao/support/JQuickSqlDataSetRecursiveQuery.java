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


import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * packageName com.github.paohaijiao.support
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/8/20
 */
public class JQuickSqlDataSetRecursiveQuery {
    /**
     * @param initialDataSet
     * @param recursiveFunction
     * @param maxDepth
     * @param distinct
     * @return
     */
    public static JQuickDataSet withRecursive(JQuickDataSet initialDataSet, Function<JQuickDataSet, JQuickDataSet> recursiveFunction, int maxDepth, boolean distinct) {
        if (maxDepth <= 0) {
            throw new IllegalArgumentException("maxDepth must be positive");
        }
        Set<JQuickRow> allRows = new LinkedHashSet<>();
        JQuickDataSet currentDataSet = initialDataSet;
        if (distinct) {
            allRows.addAll(new LinkedHashSet<>(currentDataSet.getRows()));
        } else {
            allRows.addAll(currentDataSet.getRows());
        }
        int depth = 1;
        while (depth < maxDepth && !currentDataSet.isEmpty()) {
            JQuickDataSet nextDataSet = recursiveFunction.apply(currentDataSet);
            if (nextDataSet.isEmpty()) {
                break;
            }
            if (distinct) {
                Set<JQuickRow> newRows = new LinkedHashSet<>(nextDataSet.getRows());
                newRows.removeAll(allRows);
                if (newRows.isEmpty()) {
                    break; //stop
                }
                allRows.addAll(newRows);
                currentDataSet = new JQuickDataSet(nextDataSet.getColumns(), new ArrayList<>(newRows));
            } else {
                allRows.addAll(nextDataSet.getRows());
                currentDataSet = nextDataSet;
            }
            depth++;
        }

        return new JQuickDataSet(initialDataSet.getColumns(), new ArrayList<>(allRows));
    }


    public static JQuickDataSet withRecursive(JQuickDataSet initialDataSet, Function<JQuickDataSet, JQuickDataSet> recursiveFunction, int maxDepth) {
        return withRecursive(initialDataSet, recursiveFunction, maxDepth, true);
    }

    public static JQuickDataSet withRecursive(JQuickDataSet initialDataSet, Function<JQuickDataSet, JQuickDataSet> recursiveFunction) {
        return withRecursive(initialDataSet, recursiveFunction, 100, true);
    }

    public static Function<JQuickDataSet, JQuickDataSet> buildHierarchicalRecursiveFunction(JQuickDataSet fullDataSet, String parentKeyColumn, String childKeyColumn) {
        return currentDataSet -> {
            Set<Object> childKeys = currentDataSet.getRows().stream()
                    .map(row -> row.get(childKeyColumn))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            if (childKeys.isEmpty()) {
                return JQuickDataSet.builder().build();
            }
            List<JQuickRow> nextLevelRows = fullDataSet.getRows().stream()
                    .filter(row -> {
                        Object parentKey = row.get(parentKeyColumn);
                        return parentKey != null && childKeys.contains(parentKey);
                    })
                    .collect(Collectors.toList());

            return new JQuickDataSet(fullDataSet.getColumns(), nextLevelRows);
        };
    }
}
