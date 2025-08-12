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
package com.github.paohaijiao;

import com.github.paohaijiao.condition.JComparisonCondition;
import com.github.paohaijiao.condition.JCondition;
import com.github.paohaijiao.enums.*;
import com.github.paohaijiao.expression.JColumnExpression;
import com.github.paohaijiao.expression.JExpression;
import com.github.paohaijiao.expression.JLiteralExpression;
import com.github.paohaijiao.expression.JOrderByExpression;
import com.github.paohaijiao.function.JAggregateFunction;
import com.github.paohaijiao.olap.JOlapOperation;
import com.github.paohaijiao.query.JQueryPlan;
import com.github.paohaijiao.select.JSelectColumn;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/**
 * packageName com.github.paohaijiao
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/8/12
 */
public class JExpressionsTest {

    @Test
    public void tesstAction() throws IOException {
        List<JSelectColumn> columns = Arrays.asList(
                new JSelectColumn(new JColumnExpression("customer_id")),
                new JSelectColumn(new JColumnExpression("customer_name"), "name"),
                new JSelectColumn(new JAggregateFunction(JAggregateType.SUM, new JColumnExpression("order_amount")), "total_amount")
        );
        JCondition whereCondition = new JComparisonCondition(
                new JColumnExpression("order_date"),
                JComparisonOperator.GTE,
                new JLiteralExpression(LocalDate.of(2023, 1, 1), JDataType.DATE)
        );

        List<JExpression> groupBy = Arrays.asList(
                new JColumnExpression("customer_id"),
                new JColumnExpression("customer_name")
        );

        List<JOrderByExpression> orderBy = Arrays.asList(
                new JOrderByExpression(new JColumnExpression("total_amount"), JSortDirection.DESC)
        );
        JOlapOperation olapOp = JOlapOperation.rollup(Arrays.asList("year", "quarter", "month"));
        JQueryPlan queryPlan = new JQueryPlan();
        queryPlan.setSelectedColumns(columns);
        queryPlan.setWhereCondition(whereCondition);
        queryPlan.setGroupBy(groupBy);
        queryPlan.setOrderByExpressions(orderBy);
        queryPlan.setOlapOperationType(JOlapOperationType.ROLLUP);
        queryPlan.setOlapOperation(olapOp);
        System.out.println(queryPlan);
    }
}
