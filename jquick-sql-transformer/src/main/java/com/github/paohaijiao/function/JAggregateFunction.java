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

import com.github.paohaijiao.enums.JAggregateType;
import com.github.paohaijiao.expression.JExpression;

/**
 * packageName com.github.paohaijiao.function
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/8/12
 */
public class JAggregateFunction extends JExpression {
    private JAggregateType type;
    private JExpression argument;
    private boolean distinct;
    private String alias;


    public JAggregateFunction(JAggregateType type, JExpression argument) {
        this.type = type;
        this.argument = argument;
    }

    public JAggregateFunction(JAggregateType type, JExpression argument, boolean distinct) {
        this(type, argument);
        this.distinct = distinct;
    }

    public JAggregateFunction(JAggregateType type, JExpression argument, String alias) {
        this(type, argument);
        this.alias = alias;
    }

    public static JAggregateFunction count(JExpression expr) {
        return new JAggregateFunction(JAggregateType.COUNT, expr);
    }

    public static JAggregateFunction sum(JExpression expr) {
        return new JAggregateFunction(JAggregateType.SUM, expr);
    }
}
