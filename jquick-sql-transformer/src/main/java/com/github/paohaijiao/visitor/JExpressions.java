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
package com.github.paohaijiao.visitor;

import com.github.paohaijiao.enums.JBinaryOperator;
import com.github.paohaijiao.expression.JBinaryExpression;
import com.github.paohaijiao.expression.JColumnExpression;
import com.github.paohaijiao.expression.JExpression;
import com.github.paohaijiao.expression.JLiteralExpression;

/**
 * packageName com.github.paohaijiao.visitor
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/8/12
 */
public class JExpressions {

    public static JColumnExpression col(String name) {
        return new JColumnExpression(name);
    }

    public static JColumnExpression col(String table, String name) {
        return new JColumnExpression(table, name);
    }

    public static JLiteralExpression val(Object value) {
        if (value == null) {
            return JLiteralExpression.nullValue();
        } else if (value instanceof String) {
            return JLiteralExpression.string((String)value);
        } else if (value instanceof Number) {
            return JLiteralExpression.number((Number)value);
        } else if (value instanceof Boolean) {
            return JLiteralExpression.bool((Boolean)value);
        }
        throw new IllegalArgumentException("Unsupported literal type");
    }

    public static JBinaryExpression eq(JExpression left, JExpression right) {
        return new JBinaryExpression(left, JBinaryOperator.EQ, right);
    }

    public static JBinaryExpression gt(JExpression left, JExpression right) {
        return new JBinaryExpression(left, JBinaryOperator.GT, right);
    }
//    Expression ageCondition = Expressions.gt(
//            Expressions.col("age"),
//            Expressions.val(18)
//    );
}
