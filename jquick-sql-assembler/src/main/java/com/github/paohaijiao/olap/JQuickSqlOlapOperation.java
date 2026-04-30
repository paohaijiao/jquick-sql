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
package com.github.paohaijiao.olap;

import com.github.paohaijiao.enums.JQuickSqlOlapType;
import com.github.paohaijiao.function.JQuickSqlAggregateFunction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * packageName com.github.paohaijiao.olap
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/8/12
 */
public class JQuickSqlOlapOperation {

    private JQuickSqlOlapType type;

    private List<String> dimensions;

    private List<JQuickSqlOlapCondition> conditions;

    private JQuickSqlOlapOperation(JQuickSqlOlapType type, List<String> dimensions, List<JQuickSqlOlapCondition> conditions) {
        this.type = type;
        this.dimensions = dimensions;
        this.conditions = conditions;
    }

    public static JQuickSqlOlapOperation rollup(List<String> dimensions) {
        return new JQuickSqlOlapOperation(JQuickSqlOlapType.ROLLUP, dimensions, null);
    }

    public static JQuickSqlOlapOperation drillDown(List<String> baseDimensions, List<String> drillDimensions) {
        List<String> allDims = new ArrayList<>(baseDimensions);
        allDims.addAll(drillDimensions);
        return new JQuickSqlOlapOperation(JQuickSqlOlapType.DRILLDOWN, allDims, null);
    }

    public static JQuickSqlOlapOperation slice(List<JQuickSqlOlapCondition> conditions) {
        return new JQuickSqlOlapOperation(JQuickSqlOlapType.SLICE, null, conditions);
    }

    public static JQuickSqlOlapOperation dice(List<JQuickSqlOlapCondition> conditions) {
        return new JQuickSqlOlapOperation(JQuickSqlOlapType.DICE, null, conditions);
    }

    public static JQuickSqlOlapOperation pivot(String dimension, List<String> values, JQuickSqlAggregateFunction function) {
        List<JQuickSqlOlapCondition> conditions = Arrays.asList(
                new JQuickSqlOlapCondition(dimension, "IN", values),
                new JQuickSqlOlapCondition(function)
        );
        return new JQuickSqlOlapOperation(JQuickSqlOlapType.PIVOT, Collections.singletonList(dimension), conditions);
    }
}
