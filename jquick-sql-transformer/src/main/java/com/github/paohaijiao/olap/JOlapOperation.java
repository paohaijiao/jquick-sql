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

import com.github.paohaijiao.enums.JOlapType;
import com.github.paohaijiao.function.JAggregateFunction;

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
public class JOlapOperation {

    private JOlapType type;

    private List<String> dimensions;

    private List<JOlapCondition> conditions;

    public static JOlapOperation rollup(List<String> dimensions) {
        return new JOlapOperation(JOlapType.ROLLUP, dimensions, null);
    }
    public static JOlapOperation drillDown(List<String> baseDimensions, List<String> drillDimensions) {
        List<String> allDims = new ArrayList<>(baseDimensions);
        allDims.addAll(drillDimensions);
        return new JOlapOperation(JOlapType.DRILLDOWN, allDims, null);
    }
    public static JOlapOperation slice(List<JOlapCondition> conditions) {
        return new JOlapOperation(JOlapType.SLICE, null, conditions);
    }
    public static JOlapOperation dice(List<JOlapCondition> conditions) {
        return new JOlapOperation(JOlapType.DICE, null, conditions);
    }
    public static JOlapOperation pivot(String dimension, List<String> values, JAggregateFunction function) {
        List<JOlapCondition> conditions = Arrays.asList(
                new JOlapCondition(dimension, "IN", values),
                new JOlapCondition(function)
        );
        return new JOlapOperation(JOlapType.PIVOT, Collections.singletonList(dimension), conditions);
    }
    private JOlapOperation(JOlapType type, List<String> dimensions, List<JOlapCondition> conditions) {
        this.type = type;
        this.dimensions = dimensions;
        this.conditions = conditions;
    }
}
