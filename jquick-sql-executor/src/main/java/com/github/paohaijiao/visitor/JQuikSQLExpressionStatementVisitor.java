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

import com.github.paohaijiao.exception.JAssert;
import com.github.paohaijiao.expression.JExpression;
import com.github.paohaijiao.expression.JNotExpression;
import com.github.paohaijiao.parser.JQuickSQLParser;

import java.util.ArrayList;
import java.util.List;

/**
 * packageName com.github.paohaijiao.visitor
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/8/11
 */
public class JQuikSQLExpressionStatementVisitor extends JQuikSQLExpressionStatementAtomVisitor {
    @Override
    public List<JExpression> visitExpressions(JQuickSQLParser.ExpressionsContext ctx) {
        List<JExpression> results = new ArrayList<>();
        for (JQuickSQLParser.ExpressionContext exprCtx : ctx.expression()) {
            results.add((JExpression) visit(exprCtx));
        }
        return results;
    }

    @Override
    public JExpression visitParenExpression(JQuickSQLParser.ParenExpressionContext ctx) {
        JAssert.notNull(ctx.expression(), "parenExpression must not be null");
        Object value = visit(ctx.expression());
        JAssert.isTrue(value instanceof JExpression, "parenExpression must not be instance of JExpression");
        return (JExpression) value;
    }

    @Override
    public JExpression visitNotExpression(JQuickSQLParser.NotExpressionContext ctx) {
        Object expression = visit(ctx.expression());
        JAssert.isTrue(expression instanceof JExpression, "the expression is not a condition");
        return new JNotExpression((JExpression) expression);
    }


    @Override
    public JExpression visitPredicateExpression(JQuickSQLParser.PredicateExpressionContext ctx) {
        JAssert.notNull(ctx.expressionAtom(), "Predicate expression must not be null");
        Object value = visit(ctx.expressionAtom());
        JAssert.isTrue(value instanceof JExpression, "the expression is not a condition");
        return (JExpression) value;
    }

    @Override
    public JExpression visitSelectResult(JQuickSQLParser.SelectResultContext ctx) {
        JAssert.notNull(ctx.selectClause(), "select clause must not be null");
        Object value = visitSelectClause(ctx.selectClause());
        return (JExpression) value;
    }

}
