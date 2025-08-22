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
package com.github.paohaijiao.function;

import com.github.paohaijiao.expression.JExpression;
import lombok.Getter;

/**
 * packageName com.github.paohaijiao.function
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/8/12
 */
@Getter
public class JAggregateFunction extends JExpression {

    private String aggregateType;

    private JExpression argument;

    private boolean distinct;

    private String alias;


    public JAggregateFunction(String aggregateType, JExpression argument) {
        this.aggregateType = aggregateType;
        this.argument = argument;
    }

    public JAggregateFunction(String aggregateType, JExpression argument, boolean distinct) {
        this(aggregateType, argument);
        this.distinct = distinct;
    }

    public JAggregateFunction(String aggregateType, JExpression argument, String alias) {
        this(aggregateType, argument);
        this.alias = alias;
    }


}
