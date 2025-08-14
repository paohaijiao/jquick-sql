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
package com.github.paohaijiao.plan;

import com.github.paohaijiao.condition.JCondition;
import com.github.paohaijiao.enums.JOlapOperationType;
import com.github.paohaijiao.expression.JExpression;
import com.github.paohaijiao.expression.JOrderByExpression;
import com.github.paohaijiao.function.JAggregateFunction;
import com.github.paohaijiao.join.JoinPart;
import com.github.paohaijiao.join.JTableSource;
import com.github.paohaijiao.olap.JOlapOperation;
import com.github.paohaijiao.select.JSelectColumn;
import lombok.Data;

import java.util.List;

/**
 * packageName com.github.paohaijiao.query
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/8/12
 */
@Data
public class JExecutionPlan {

    private List<JSelectColumn> selectedColumns;

    private JCondition whereCondition;

    private List<JTableSource> tableSources;

    private List<JoinPart> joinParts;


    private List<JAggregateFunction> aggregateFunctions;

    private JCondition havingCondition;


    private Integer limit;

    private JOlapOperation olapOperation;

    private List<JExpression> groupBy;

    private List<JOrderByExpression> orderByExpressions;

    private JOlapOperationType olapOperationType;

    private List<String> drillDimensions;

}
