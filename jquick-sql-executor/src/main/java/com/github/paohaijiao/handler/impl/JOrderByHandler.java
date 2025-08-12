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
package com.github.paohaijiao.handler.impl;

import com.github.paohaijiao.enums.JSortDirection;
import com.github.paohaijiao.expression.JOrderByExpression;
import com.github.paohaijiao.handler.JQueryHandler;
import com.github.paohaijiao.query.JQueryPlan;
import com.github.paohaijiao.util.JEntityAccessor;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * packageName com.github.paohaijiao.handler.impl
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/8/12
 */
public class JOrderByHandler<T> implements JQueryHandler<T> {
    private final List<JOrderByExpression> orderByExpressions;
    private final JEntityAccessor<T> accessor;

    public JOrderByHandler(List<JOrderByExpression> orderByExpressions,
                           Class<T> entityClass) {
        this.orderByExpressions = orderByExpressions;
        this.accessor = new JEntityAccessor<>(entityClass);
    }

    @Override
    public List<T> handle(List<T> dataset, JQueryPlan plan) {
        Comparator<T> comparator = createComparator();
        return dataset.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    private Comparator<T> createComparator() {
        Comparator<T> comparator = null;

        for (JOrderByExpression orderBy : orderByExpressions) {
            Comparator<T> current = Comparator.comparing(
                    entity -> (Comparable) accessor.getValue(entity, orderBy.getExpression()),
                    getComparatorForDirection(orderBy.getDirection())
            );

            if (comparator == null) {
                comparator = current;
            } else {
                comparator = comparator.thenComparing(current);
            }
        }

        return comparator;
    }

    private Comparator<Comparable> getComparatorForDirection(JSortDirection direction) {
        return direction == JSortDirection.DESC ?
                Comparator.reverseOrder() :
                Comparator.naturalOrder();
    }
}
