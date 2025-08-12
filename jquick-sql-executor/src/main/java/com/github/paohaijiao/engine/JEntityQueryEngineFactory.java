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
package com.github.paohaijiao.engine;

import com.github.paohaijiao.enums.JHandlerType;
import com.github.paohaijiao.handler.JBaseQueryHandlerFactory;
import com.github.paohaijiao.handler.JQueryHandler;
import com.github.paohaijiao.handler.impl.JFilterHandler;
import com.github.paohaijiao.handler.impl.JGroupByHandler;
import com.github.paohaijiao.handler.impl.JMultiTableHandler;
import com.github.paohaijiao.handler.impl.JOrderByHandler;
import com.github.paohaijiao.query.JQueryPlan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * packageName com.github.paohaijiao.engine
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/8/12
 */
public class JEntityQueryEngineFactory<T> extends JBaseQueryHandlerFactory<T> {


    public JEntityQueryEngineFactory(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public JQueryHandler<T> createHandler(JQueryPlan plan) {
        JHandlerType type = determineHandlerType(plan);
        switch (type) {
            case FILTER:
                return new JFilterHandler<>(
                        plan.getWhereCondition() != null ?
                                plan.getWhereCondition() : plan.getHavingCondition(),
                        entityClass
                );
            case GROUP_BY:
                return new JGroupByHandler<>(
                        plan.getGroupBy(),
                        plan.getOlapOperationType(),
                        entityClass
                );
            case ORDER_BY:
                return new JOrderByHandler<>(
                        plan.getOrderByExpressions(),
                        entityClass
                );
//            case LIMIT:
//                return new LimitHandler<>(
//                        plan.getLimitClause()
//                );
//            case PROJECTION:
//                return new ProjectionHandler<>(
//                        plan.getSelectedColumns(),
//                        entityClass
//                );
            case JOIN:
                 return   new JMultiTableHandler<>(
                        entityClass,
                        plan.getTableSources().get(0),
                        plan.getJoinParts(),
                        new HashMap<>()
                );

            default:
                throw new IllegalArgumentException("Unsupported query type");
        }
    }


    public List<JQueryHandler<T>> createExecutionChain(JQueryPlan plan) {
        List<JQueryHandler<T>> chain = new ArrayList<>();
        if (plan.getWhereCondition() != null) {
            chain.add(createHandler(plan));
        }
        if (plan.getGroupBy() != null || plan.getOlapOperationType() != null) {
            chain.add(createHandler(plan));
        }
        if (plan.getHavingCondition() != null) {
            chain.add(createHandler(plan));
        }
        if (plan.getOrderByExpressions() != null) {
            chain.add(createHandler(plan));
        }
        if (plan.getLimit() != null) {
            chain.add(createHandler(plan));
        }
        if (plan.getSelectedColumns() != null && !plan.getSelectedColumns().isEmpty()) {
            chain.add(createHandler(plan));
        }

        return chain;
    }
}
