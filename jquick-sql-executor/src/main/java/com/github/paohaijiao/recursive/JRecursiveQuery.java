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
package com.github.paohaijiao.recursive;

import com.github.paohaijiao.dataset.JDataSet;
import com.github.paohaijiao.dataset.JRow;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * packageName com.github.paohaijiao.recursive
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/8/15
 */
public class JRecursiveQuery {

    private final Map<String, JDataSet> cteCache = new HashMap<>();
    private int maxDepth = 100;

    public JDataSet executeRecursive(String cteName, Function<JDataSet, JDataSet> seedQuery, Function<JDataSet, JDataSet> recursiveQuery, Function<JDataSet, JDataSet> finalQuery) {
        //init
        JDataSet seed = seedQuery.apply(cteCache.getOrDefault(cteName, emptyDataSet()));
        // iterator
        Iterator<JDataSet> recursiveIterator = new Iterator<JDataSet>() {
            JDataSet current = seed;
            int depth = 0;

            @Override
            public boolean hasNext() {
                return !current.isEmpty() && depth < maxDepth;
            }

            @Override
            public JDataSet next() {
                cteCache.put(cteName, current);
                JDataSet next = recursiveQuery.apply(current);
                depth++;
                current = next;
                return current;
            }
        };
        List<JRow> result = Stream.concat(
                        Stream.of(seed),
                        StreamSupport.stream(
                                Spliterators.spliteratorUnknownSize(
                                        recursiveIterator,
                                        Spliterator.ORDERED),
                                false)
                )
                .flatMap(ds -> ds.getRows().stream())
                .collect(Collectors.toList());
        // finaly
        cteCache.put(cteName, new JDataSet(seed.getColumns(), result));
        return finalQuery.apply(cteCache.get(cteName));
    }

    private JDataSet emptyDataSet() {
        return new JDataSet(Collections.emptyList(), Collections.emptyList());
    }
}
