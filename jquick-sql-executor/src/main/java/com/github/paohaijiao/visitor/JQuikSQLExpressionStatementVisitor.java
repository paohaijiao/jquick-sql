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

import com.github.paohaijiao.enums.JLogicalOperator;
import com.github.paohaijiao.exception.JAssert;
import com.github.paohaijiao.parser.JQuickSQLParser;

/**
 * packageName com.github.paohaijiao.visitor
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/8/11
 */
public class JQuikSQLExpressionStatementVisitor extends JQuikSQLValueStatementVisitor{
    @Override
    public Object visitNotExpression(JQuickSQLParser.NotExpressionContext ctx) {
        Object childResult = visit(ctx.expression());
        if (childResult instanceof Boolean) {
            return !((Boolean) childResult);
        } else {
            JAssert.throwNewException("NOT operator can only be applied to boolean expressions");
            return null;
        }
    }
    @Override
    public Object visitLogicalExpression(JQuickSQLParser.LogicalExpressionContext ctx) {
        Object left = visit(ctx.expression(0));
        Object right = visit(ctx.expression(1));
        JLogicalOperator operator = visitLogicalOperator(ctx.logicalOperator());
        boolean leftBool = convertToBoolean(left);
        boolean rightBool = convertToBoolean(right);
        switch (operator) {
            case And:
                return leftBool && rightBool;
            case Or:
                return leftBool || rightBool;
            case XOR:
                return leftBool ^ rightBool;
            default:
                JAssert.throwNewException("Unknown operator: " + operator);
        }
        JAssert
                .throwNewException("Unknown operator: " + operator);
        return null;
    }
    @Override
    public Object visitPredicateExpression(JQuickSQLParser.PredicateExpressionContext ctx) {
        JAssert.notNull(ctx.predicate(),"Predicate expression must not be null");
        return visit(ctx.predicate());
    }
    @Override
    public Object visitSelectResult(JQuickSQLParser.SelectResultContext ctx) {
        JAssert.notNull(ctx.selectClause(),"select clause must not be null");
       return  visitSelectClause(ctx.selectClause());
    }
    @Override
    public Object visitParenExpression(JQuickSQLParser.ParenExpressionContext ctx) {
        JAssert.notNull(ctx.expression(),"parenExpression must not be null");
        return visit(ctx.expression());
    }



}
