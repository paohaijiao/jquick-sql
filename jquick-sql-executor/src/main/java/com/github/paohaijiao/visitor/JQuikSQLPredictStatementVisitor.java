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
import com.github.paohaijiao.dataset.DataSet;
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
public class JQuikSQLPredictStatementVisitor extends JQuikSQLFunctionStatementVisitor {
    @Override
    public JExpression visitExpressionAtomPredicate(JQuickSQLParser.ExpressionAtomPredicateContext ctx) {
        JAssert.notNull(ctx.expressionAtom(), "expressionAtom not null");
        Object value=visit(ctx.expressionAtom());
        JAssert.isTrue(value instanceof JExpression,"the value must to be expression");
        return (JExpression)value;
    }

    @Override
    public JCondition visitIsNullPredicate(JQuickSQLParser.IsNullPredicateContext ctx) {
        Object value = visit(ctx.predicate());
        JAssert.isTrue(value instanceof JColumnExpression,"the value must to be column expression");
        boolean isNot = ctx.NOT() != null;
        return new JIsNullCondition((JColumnExpression)value,isNot);
    }

    @Override
    public JCondition visitBinaryComparisonPredicate(JQuickSQLParser.BinaryComparisonPredicateContext ctx) {
        JExpression left = (JExpression)visit(ctx.predicate(0));
        JExpression right = (JExpression)visit(ctx.predicate(1));
        String operator = ctx.comparisonOperator().getText();
        JComparisonCondition expression=new JComparisonCondition(left, JComparisonOperator.symbolOf(operator),right);
        return expression;
    }

    @Override
    public JCondition visitBetweenPredicate(JQuickSQLParser.BetweenPredicateContext ctx) {
        Object target = visit(ctx.predicate(0));
        JAssert.isTrue(target instanceof JExpression,"the value must to be  expression");
        Object lowerBound = visit(ctx.predicate(1));
        JAssert.isTrue(lowerBound instanceof JLiteralExpression,"the value must is literalExpression");
        Object upperBound = visit(ctx.predicate(2));
        JAssert.isTrue(upperBound instanceof JLiteralExpression,"the value must is literalExpression");
        boolean isNot = ctx.NOT() != null;
        if (target == null || lowerBound == null || upperBound == null) {
            return null;
        }
        return new JBetweenCondition((JExpression)target,(JLiteralExpression)lowerBound,(JLiteralExpression)upperBound,isNot);
    }

    @Override
    public JCondition visitInPredicate(JQuickSQLParser.InPredicateContext ctx) {
        Object target = visit(ctx.predicate());
        JAssert.isTrue(target instanceof JExpression,"the value must to be  expression");
        boolean isNot = ctx.NOT() != null;
        List<JExpression> inList=new ArrayList<>();
        if (ctx.selectStatement() != null) {
            inList = (List<JExpression>) visit(ctx.selectStatement());
        } else {
            for (JQuickSQLParser.ExpressionContext exprCtx : ctx.expressions().expression()) {
                Object exp =  visit(exprCtx);
                JAssert.isTrue(exp instanceof JExpression,"the value must to be  expression");
                JExpression expression=(JExpression)exp;
                inList.add(expression);
            }
        }
        return new JInCondition((JExpression)target,isNot,inList);
    }

    @Override
    public JCondition visitLikePredicate(JQuickSQLParser.LikePredicateContext ctx) {
        Object target = visit(ctx.predicate(0));
        JAssert.isTrue(target instanceof JExpression,"the value must to be  expression");
        Object right = visit(ctx.predicate(1));
        JAssert.notNull(right,"the pattern not null");
        JAssert.isTrue(right instanceof JLiteralExpression,"the pattern is String type");
        boolean isNot = ctx.NOT() != null;
        JLiteralExpression pattern = (JLiteralExpression)right;
        return new JLikeCondition((JExpression)target,isNot,(String)pattern.getValue());
    }

    @Override
    public JCondition visitRegexpPredicate(JQuickSQLParser.RegexpPredicateContext ctx) {
        Object target = visit(ctx.predicate(0));
        JAssert.isTrue(target instanceof JExpression,"the value must to be column expression");
        Object right = visit(ctx.predicate(1));
        JAssert.notNull(right,"the pattern not null");
        JAssert.isTrue(right instanceof JLiteralExpression,"the pattern is String type");
        boolean isNot = ctx.NOT() != null;
        JLiteralExpression val=(JLiteralExpression)right;
        JRegexCondition jRegexCondition=new JRegexCondition((JExpression)target,isNot,(String)val.getValue());
        return  jRegexCondition;
    }



    @Override
    public JCondition visitExisitsPredicate(JQuickSQLParser.ExisitsPredicateContext ctx) {
        Object subqueryResult = visit(ctx.expression());
        JAssert.isTrue(subqueryResult instanceof DataSet,"the type should be DataSet");
        return new JExistsCondition((DataSet)subqueryResult);
    }



    private boolean isLiteral(Object value) {
        if (value == null) return true;
        Class<?> clazz = value.getClass();
        if (clazz.isPrimitive() ||
                clazz.equals(String.class) ||
                clazz.equals(Integer.class) ||
                clazz.equals(Long.class) ||
                clazz.equals(Double.class) ||
                clazz.equals(Float.class) ||
                clazz.equals(Boolean.class) ||
                clazz.equals(Short.class) ||
                clazz.equals(Byte.class) ||
                clazz.equals(Character.class) ||
                clazz.equals(BigDecimal.class) ||
                clazz.equals(BigInteger.class) ||
                clazz.equals(java.util.Date.class) ||
                clazz.equals(java.sql.Date.class) ||
                clazz.equals(java.sql.Timestamp.class)) {
            return true;
        }
        if (clazz.isEnum()) {
            return true;
        }
        return false;
    }

}
