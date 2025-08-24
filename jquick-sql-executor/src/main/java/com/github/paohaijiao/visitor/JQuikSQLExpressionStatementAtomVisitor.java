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

import com.github.paohaijiao.dataset.JDataSet;
import com.github.paohaijiao.exception.JAssert;
import com.github.paohaijiao.expression.*;
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
public class JQuikSQLExpressionStatementAtomVisitor extends JQuikSQLValueStatementVisitor{
    @Override
    public JLiteralExpression visitConstantExpressionAtom(JQuickSQLParser.ConstantExpressionAtomContext ctx) {
        JAssert.notNull(ctx.constant(),"constant must not be null");
        return visitConstant(ctx.constant());
    }
    @Override
    public JColumnExpression visitFullColumnNameExpressionAtom(JQuickSQLParser.FullColumnNameExpressionAtomContext ctx) {
        String  column= ctx.fullColumnName().getText();
        JColumnExpression columnExpression = new JColumnExpression(column);
        return columnExpression;
    }
    @Override
    public JFunctionCallExpression visitFunctionCallExpressionAtom(JQuickSQLParser.FunctionCallExpressionAtomContext ctx) {
        JQuickSQLParser.FunctionCallContext funcCtx = ctx.functionCall();
        return (JFunctionCallExpression)visit(funcCtx);
    }
    @Override
    public List<JExpression> visitNestedExpressionAtom(JQuickSQLParser.NestedExpressionAtomContext ctx) {
        List<JExpression> list=new ArrayList<>();
        for (int i = 0; i < ctx.expression().size(); i++) {
            Object object=visit(ctx.expression(i));
            JAssert.isTrue(object instanceof JExpression,"parenExpression must  be expression");
            list.add((JExpression)object);
        }
        return list;
    }
    @Override
    public JExpression visitSubqueryExperssionAtom(JQuickSQLParser.SubqueryExperssionAtomContext ctx) {
        JAssert.notNull(ctx.selectStatement(),"selectStatement must not be null");
        Object dataSet=visit(ctx.selectStatement());
        JAssert.notNull(dataSet instanceof JDataSet,"dataSet must  be instance of dataSet");
        return new JDataSetExpression((JDataSet)dataSet);
    }
    @Override
    public JExpression visitMathExpressionAtom(JQuickSQLParser.MathExpressionAtomContext ctx) {
        JAssert.isTrue(ctx.expressionAtom().size()==2,"mathExpressionAtom must have 2 expressions");
        Object valLeft=visit(ctx.expressionAtom(0));
        Object valRight=visit(ctx.expressionAtom(1));
        JAssert.isTrue( valLeft instanceof JLiteralExpression,"expression[0] must is literal");
        JAssert.isTrue( valRight instanceof JLiteralExpression,"expression[1] must is literal");
        JLiteralExpression left = (JLiteralExpression)valLeft;
        JLiteralExpression right = (JLiteralExpression)valRight;
        String operator = ctx.mathOperator().getText();
        if (left == null || right == null) {
            return null;
        }
        Number leftNum = convertToNumber(left.getValue());
        Number rightNum = convertToNumber(right.getValue());
        switch (operator) {
            case "+":
                return JLiteralExpression.number(leftNum.doubleValue() + rightNum.doubleValue());
            case "-":
                return JLiteralExpression.number(leftNum.doubleValue() - rightNum.doubleValue());
            case "*":
                return JLiteralExpression.number(leftNum.doubleValue() * rightNum.doubleValue());
            case "/":
                if (rightNum.doubleValue() == 0) {
                    throw new ArithmeticException("Division by zero");
                }
                return JLiteralExpression.number(leftNum.doubleValue() / rightNum.doubleValue());
            case "--":
                return JLiteralExpression.number(leftNum.doubleValue() - 1);
            default:
                throw new UnsupportedOperationException("Unknown operator: " + operator);
        }
    }
    @Override
    public JExpression visitUnaryExpressionAtom(JQuickSQLParser.UnaryExpressionAtomContext ctx) {
        Object operand = visit(ctx.expressionAtom());
        JAssert.isTrue( operand instanceof JLiteralExpression,"expression[1] must is literal");
        String operator = ctx.unaryOperator().getText();
        JLiteralExpression value=(JLiteralExpression)operand;
        if (operand == null) {
            return null;
        }
        switch (operator) {
            case "+":
                return JLiteralExpression.number(convertToNumber(value.getValue()));
            case "-":
                return JLiteralExpression.number(negateNumber(value.getValue()));
            case "!":
            case "NOT":
                return JLiteralExpression.bool(logicalNot(value.getValue()));
            case "~":
                return JLiteralExpression.number(bitwiseNot(value.getValue()));
            default:
                throw new UnsupportedOperationException("unknown unary operator: " + operator);
        }
    }
    private Number negateNumber(Object value) {
        Number num = convertToNumber(value);
        if (num instanceof Integer) {
            return -num.intValue();
        } else if (num instanceof Long) {
            return -num.longValue();
        }
        return -num.doubleValue();
    }
    private Boolean logicalNot(Object value) {
        if (value instanceof Boolean) {
            return !((Boolean) value);
        } else if (value instanceof Number) {
            return ((Number) value).doubleValue() == 0;
        }
        throw new RuntimeException("NOT operator requires boolean or numeric operand");
    }
    private Number bitwiseNot(Object value) {
        if (value instanceof Integer) {
            return ~((Integer) value);
        } else if (value instanceof Long) {
            return ~((Long) value);
        }
        throw new RuntimeException("Bitwise NOT requires integer operand");
    }

}
