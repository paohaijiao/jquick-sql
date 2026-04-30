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

import com.github.paohaijiao.condition.JQuickSqlAndCondition;
import com.github.paohaijiao.condition.JQuickSqlCondition;
import com.github.paohaijiao.condition.JQuickSqlOrCondition;
import com.github.paohaijiao.condition.JQuickSqlParenthesesCondition;
import com.github.paohaijiao.parser.JQuickSQLParser;

/**
 * packageName com.github.paohaijiao.visitor
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/8/11
 */
public class JQuikSQLFilterStatementVisitor extends JQuikSQLPredictStatementVisitor {
    @Override
    public JQuickSqlCondition visitFilterCondition(JQuickSQLParser.FilterConditionContext ctx) {
        if (ctx.AND() != null) {
            JQuickSqlCondition left = (JQuickSqlCondition) visit(ctx.filterCondition(0));
            JQuickSqlCondition right = (JQuickSqlCondition) visit(ctx.filterCondition(1));
            if (left instanceof JQuickSqlAndCondition) {
                ((JQuickSqlAndCondition) left).addCondition(right);
                return left;
            } else {
                JQuickSqlAndCondition andCondition = new JQuickSqlAndCondition();
                andCondition.addCondition(left);
                andCondition.addCondition(right);
                return andCondition;
            }
        }
        if (ctx.OR() != null) {
            JQuickSqlCondition left = (JQuickSqlCondition) visit(ctx.filterCondition(0));
            JQuickSqlCondition right = (JQuickSqlCondition) visit(ctx.filterCondition(1));
            if (left instanceof JQuickSqlOrCondition) {
                ((JQuickSqlOrCondition) left).addCondition(right);
                return left;
            } else {
                JQuickSqlOrCondition orCondition = new JQuickSqlOrCondition();
                orCondition.addCondition(left);
                orCondition.addCondition(right);
                return orCondition;
            }
        }
        if (ctx.getChildCount() == 3 &&
                ctx.getChild(0).getText().equals("(") &&
                ctx.getChild(2).getText().equals(")")) {
            JQuickSqlParenthesesCondition parentheses = new JQuickSqlParenthesesCondition();
            parentheses.setInnerCondition((JQuickSqlCondition) visit(ctx.filterCondition(0)));
            return parentheses;
        }
        if (ctx.predicate() != null) {
            return (JQuickSqlCondition) visit(ctx.predicate());
        }
        throw new IllegalArgumentException("unsupported filter condition: " + ctx.getText());
    }

}
