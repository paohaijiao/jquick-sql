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
public class JQuikSQLPredictStatementVisitor extends JQuikSQLExpressionStatementVisitor {
    @Override
    public JCondition visitExpressionAtomPredicate(JQuickSQLParser.ExpressionAtomPredicateContext ctx) {
        JAssert.notNull(ctx.expressionAtom(), "expressionAtom not null");
        Object value=visit(ctx.expressionAtom());
        JAssert.isTrue(value instanceof JExpression,"the value must to be expression");
        return new JExpressionAtomPredicateCondition((JExpression)value);
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
        JAssert.isTrue(target instanceof JColumnExpression,"the value must to be column expression");
        Object lowerBound = visit(ctx.predicate(1));
        JAssert.isTrue(lowerBound instanceof JLiteralExpression,"the value must is literalExpression");
        Object upperBound = visit(ctx.predicate(2));
        JAssert.isTrue(upperBound instanceof JLiteralExpression,"the value must is literalExpression");
        boolean isNot = ctx.NOT() != null;
        if (target == null || lowerBound == null || upperBound == null) {
            return null;
        }
        return new JBetweenCondition((JColumnExpression)target,(JLiteralExpression)lowerBound,(JLiteralExpression)upperBound,isNot);
    }

    @Override
    public JCondition visitInPredicate(JQuickSQLParser.InPredicateContext ctx) {
        Object target = visit(ctx.predicate());
        JAssert.isTrue(target instanceof JColumnExpression,"the value must to be column expression");
        boolean isNot = ctx.NOT() != null;
        List<Object> inList=new ArrayList<>();
        if (ctx.selectStatement() != null) {
            inList = (List<Object>) visit(ctx.selectStatement());
            for (Object item : inList) {
                if (!isLiteral(item)) {
                    throw new IllegalArgumentException("IN predicate requires scalar values, not complex objects");
                }
            }
        } else {
            for (JQuickSQLParser.ExpressionContext exprCtx : ctx.expressions().expression()) {
                Object exp =  visit(exprCtx);
                JAssert.isTrue(exp instanceof JLiteralExpression,"the value must to be literal expression");
                JLiteralExpression literal=(JLiteralExpression)exp;
                inList.add(literal.getValue());
            }
        }
        return new JInCondition((JColumnExpression)target,isNot,inList);
    }

    @Override
    public JCondition visitLikePredicate(JQuickSQLParser.LikePredicateContext ctx) {
        Object target = visit(ctx.predicate(0));
        JAssert.isTrue(target instanceof JColumnExpression,"the value must to be column expression");
        Object right = visit(ctx.predicate(1));
        JAssert.notNull(right,"the pattern not null");
        boolean isNot = ctx.NOT() != null;
        String pattern = right.toString();
        return new JLikeCondition((JColumnExpression)target,isNot,pattern);
    }

    @Override
    public JCondition visitRegexpPredicate(JQuickSQLParser.RegexpPredicateContext ctx) {
        Object target = visit(ctx.predicate(0));
        JAssert.isTrue(target instanceof JColumnExpression,"the value must to be column expression");
        Object right = visit(ctx.predicate(1));
        JAssert.notNull(right,"the pattern not null");
        boolean isNot = ctx.NOT() != null;
        JRegexCondition jRegexCondition=new JRegexCondition((JColumnExpression)target,isNot,right.toString());
        return  jRegexCondition;
    }



    @Override
    public JCondition visitExisitsPredicate(JQuickSQLParser.ExisitsPredicateContext ctx) {
        Object subqueryResult = visit(ctx.expression());
        JAssert.isTrue(subqueryResult instanceof JDataSet,"the type should be DataSet");
        return new JExistsCondition((JDataSet)subqueryResult);
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
