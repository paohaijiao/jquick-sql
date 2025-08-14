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

import com.github.paohaijiao.enums.JDataType;
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
public class JLiteralExpression extends JExpression {

    private Object value;

    private JDataType dataType;



    public JLiteralExpression(Object value, JDataType dataType) {
        this.type = JExpressionType.LITERAL;
        this.value = value;
        this.dataType = dataType;
    }

    public static JLiteralExpression string(String value) {
        return new JLiteralExpression(value, JDataType.STRING);
    }

    public static JLiteralExpression number(Number value) {
        return new JLiteralExpression(value, JDataType.NUMBER);
    }

    public static JLiteralExpression bool(boolean value) {
        return new JLiteralExpression(value, JDataType.BOOLEAN);
    }

    public static JLiteralExpression nullValue() {
        return new JLiteralExpression(null, JDataType.NULL);
    }

//    @Override
//    public <T> T accept(ExpressionVisitor<T> visitor) {
//        return visitor.visit(this);
//    }
}
