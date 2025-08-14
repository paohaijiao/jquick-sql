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
package com.github.paohaijiao.handler;

import com.github.paohaijiao.enums.JHandlerType;
import com.github.paohaijiao.handler.impl.JFilterHandler;
import com.github.paohaijiao.handler.impl.JGroupByHandler;
import com.github.paohaijiao.handler.impl.JOrderByHandler;
import com.github.paohaijiao.plan.JExecutionPlan;

import java.util.ArrayList;
import java.util.List;

/**
 * packageName com.github.paohaijiao.handler
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/8/12
 */
public abstract  class JBaseQueryHandlerFactory<T> implements JQueryHandlerFactory<T> {

    protected   Class<T> entityClass;

    protected JHandlerType determineHandlerType(JExecutionPlan plan) {
        if (plan.getWhereCondition() != null || plan.getHavingCondition() != null) {
            return JHandlerType.FILTER;
        } else if (plan.getGroupBy() != null || plan.getOlapOperationType() != null) {
            return JHandlerType.GROUP_BY;
        } else if (plan.getOrderByExpressions() != null) {
            return JHandlerType.ORDER_BY;
        } else if (plan.getLimit() != null) {
            return JHandlerType.LIMIT;
        } else if (plan.getSelectedColumns() != null && !plan.getSelectedColumns().isEmpty()) {
            return JHandlerType.PROJECTION;
        }
        return null;
    }
    public List<JQueryHandler<T>> createExecutionChain(JExecutionPlan plan)
    {

        List<JQueryHandler<T>> chain = new ArrayList<>();
        if (plan.getWhereCondition() != null) {
            chain.add(new JFilterHandler<>(plan.getWhereCondition(), entityClass));
        }
        if (plan.getGroupBy() != null || plan.getOlapOperationType() != null) {
            chain.add(new JGroupByHandler<>(
                    plan.getGroupBy(),
                    plan.getOlapOperationType(),
                    entityClass
            ));
        }
        if (plan.getHavingCondition() != null) {
            chain.add(new JFilterHandler<>(plan.getHavingCondition(), entityClass));
        }
        if (plan.getOrderByExpressions() != null) {
            chain.add(new JOrderByHandler<>(plan.getOrderByExpressions(), entityClass));
        }
//        if (plan.getLimit() != null) {
//            chain.add(new LimitHandler<>(plan.getLimitClause()));
//        }
//        if (plan.getSelectedColumns() != null && !plan.getSelectedColumns().isEmpty()) {
//            chain.add(new ProjectionHandler<>(plan.getSelectedColumns(), entityClass));
//        }

        return chain;
    }

}
