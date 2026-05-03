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
package com.github.paohaijiao.manage;

import com.github.paohaijiao.enums.JQuickSqlBinaryOperator;
import com.github.paohaijiao.expression.JQuickSqlBinaryExpression;
import com.github.paohaijiao.expression.JQuickSqlColumnExpression;
import com.github.paohaijiao.expression.JQuickSqlExpression;
import com.github.paohaijiao.expression.JQuickSqlLiteralExpression;

/**
 * packageName com.github.paohaijiao.visitor
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/8/12
 */
public class JQuickSqlExpressionFactory {

    public static JQuickSqlColumnExpression col(String name) {
        return new JQuickSqlColumnExpression(name);
    }

    public static JQuickSqlColumnExpression col(String table, String name) {
        return new JQuickSqlColumnExpression(table, name);
    }

    public static JQuickSqlLiteralExpression val(Object value) {
        if (value == null) {
            return JQuickSqlLiteralExpression.nullValue();
        } else if (value instanceof String) {
            return JQuickSqlLiteralExpression.string((String) value);
        } else if (value instanceof Number) {
            return JQuickSqlLiteralExpression.number((Number) value);
        } else if (value instanceof Boolean) {
            return JQuickSqlLiteralExpression.bool((Boolean) value);
        }
        throw new IllegalArgumentException("Unsupported literal type");
    }

    public static JQuickSqlBinaryExpression eq(JQuickSqlExpression left, JQuickSqlExpression right) {
        return new JQuickSqlBinaryExpression(left, JQuickSqlBinaryOperator.EQ, right);
    }

    public static JQuickSqlBinaryExpression gt(JQuickSqlExpression left, JQuickSqlExpression right) {
        return new JQuickSqlBinaryExpression(left, JQuickSqlBinaryOperator.GT, right);
    }
}
