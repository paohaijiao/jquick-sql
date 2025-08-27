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
package com.github.paohaijiao.evalue;

import com.github.paohaijiao.expression.JColumnExpression;
import com.github.paohaijiao.expression.JExpression;
import com.github.paohaijiao.expression.JLiteralExpression;

import java.util.Map;

/**
 * packageName com.github.paohaijiao.support
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/8/25
 */
public abstract class JBaseEvaluator {

    protected Object evaluateExpression(JExpression expr, Map<String, Object> row) {
        if (expr instanceof JColumnExpression) {
            return row.get(((JColumnExpression) expr).getColumnName());
        } else if (expr instanceof JLiteralExpression) {
            return ((JLiteralExpression) expr).getValue();
        }
        throw new UnsupportedOperationException("Unsupported expression type: " + expr.getType());
    }
}
