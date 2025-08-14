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

import com.github.paohaijiao.condition.JCondition;
import com.github.paohaijiao.handler.JQueryHandler;
import com.github.paohaijiao.plan.JExecutionPlan;
import com.github.paohaijiao.util.JEntityAccessor;

import java.util.List;
import java.util.stream.Collectors;

/**
 * packageName com.github.paohaijiao.handler.impl
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/8/12
 */
public class JFilterHandler<T> implements JQueryHandler<T> {
    private final JCondition condition;

    private final JEntityAccessor<T> accessor;

    public JFilterHandler(JCondition condition, Class<T> entityClass) {
        this.condition = condition;
        this.accessor = new JEntityAccessor<>(entityClass);
    }

    @Override
    public List<T> handle(List<T> dataset, JExecutionPlan plan) {
        return dataset.stream()
                .filter(entity -> evaluateCondition(entity, condition))
                .collect(Collectors.toList());
    }

    private boolean evaluateCondition(T entity, JCondition condition) {
        return true;
    }
}
