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
package com.github.paohaijiao.expression;

import com.github.paohaijiao.enums.JExpressionType;
import com.github.paohaijiao.enums.JUnaryOperator;
import lombok.Getter;

/**
 * packageName com.github.paohaijiao.expression
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/8/12
 */
@Getter
public class JUnaryExpression extends JExpression {

    private JExpression expression;

    private JUnaryOperator operator;


    public JUnaryExpression(JUnaryOperator operator, JExpression expression) {
        this.type = JExpressionType.UNARY;
        this.operator = operator;
        this.expression = expression;
    }
}
