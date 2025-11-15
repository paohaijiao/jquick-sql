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
package com.github.paohaijiao.condition;

import com.github.paohaijiao.enums.JConditionType;
import com.github.paohaijiao.expression.JColumnExpression;
import lombok.Getter;

/**
 * packageName com.github.paohaijiao.expression
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/8/23
 */
@Getter
public class JIsNullCondition extends JCondition {

    private JColumnExpression expression;

    private Boolean not;

    public JIsNullCondition(JColumnExpression expression, Boolean not) {
        this.type = JConditionType.IS_NULL;
        this.expression = expression;
        this.not = not;
    }
}
