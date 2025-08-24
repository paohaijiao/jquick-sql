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

import com.github.paohaijiao.condition.*;
import com.github.paohaijiao.dataset.JDataSet;
import com.github.paohaijiao.enums.JComparisonOperator;
import com.github.paohaijiao.exception.JAssert;
import com.github.paohaijiao.expression.JColumnExpression;
import com.github.paohaijiao.expression.JExpression;
import com.github.paohaijiao.expression.JLiteralExpression;
import com.github.paohaijiao.parser.JQuickSQLParser;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * packageName com.github.paohaijiao.visitor
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/8/11
 */
public class JQuikSQLFilterStatementVisitor extends JQuikSQLPredictStatementVisitor {
    @Override
    public JCondition visitFilterCondition(JQuickSQLParser.FilterConditionContext ctx) {
        if (ctx.AND() != null) {
            JCondition left =(JCondition)visit(ctx.filterCondition(0));
            JCondition right = (JCondition)visit(ctx.filterCondition(1));
            if (left instanceof JAndCondition) {
                ((JAndCondition) left).addCondition(right);
                return left;
            }
            else {
                JAndCondition andCondition = new JAndCondition();
                andCondition.addCondition(left);
                andCondition.addCondition(right);
                return andCondition;
            }
        }
        if (ctx.OR() != null) {
            JCondition left = (JCondition)visit(ctx.filterCondition(0));
            JCondition right = (JCondition)visit(ctx.filterCondition(1));
            if (left instanceof JOrCondition) {
                ((JOrCondition) left).addCondition(right);
                return left;
            }
            else {
                JOrCondition orCondition = new JOrCondition();
                orCondition.addCondition(left);
                orCondition.addCondition(right);
                return orCondition;
            }
        }
        if (ctx.getChildCount() == 3 &&
                ctx.getChild(0).getText().equals("(") &&
                ctx.getChild(2).getText().equals(")")) {
            JParenthesesCondition parentheses = new JParenthesesCondition();
            parentheses.setInnerCondition((JCondition)visit(ctx.filterCondition(0)));
            return parentheses;
        }
        if (ctx.predicate() != null) {
            return (JCondition)visit(ctx.predicate());
        }
        throw new IllegalArgumentException("unsupported filter condition: " + ctx.getText());
    }

}
