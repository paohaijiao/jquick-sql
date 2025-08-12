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

import com.github.paohaijiao.enums.JBinaryOperator;
import com.github.paohaijiao.enums.JExpressionType;
import lombok.Getter;

/**
 * packageName com.github.paohaijiao.expression
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/8/12
 */
@Getter
public class JBinaryExpression extends JExpression {
    private JExpression left;

    private JExpression right;

    private JBinaryOperator operator;


    public JBinaryExpression(JExpression left, JBinaryOperator operator, JExpression right) {
        this.type = JExpressionType.BINARY;
        this.left = left;
        this.operator = operator;
        this.right = right;
    }


//    @Override
//    public <T> T accept(ExpressionVisitor<T> visitor) {
//        return visitor.visit(this);
//    }

}
