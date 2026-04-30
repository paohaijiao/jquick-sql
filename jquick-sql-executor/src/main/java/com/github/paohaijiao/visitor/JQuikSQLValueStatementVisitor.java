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

import com.github.paohaijiao.date.JDateUtil;
import com.github.paohaijiao.enums.JQuickSqlComparisonOperator;
import com.github.paohaijiao.enums.JQuickSqlLogicalOperator;
import com.github.paohaijiao.enums.JQuickSqlMathOperator;
import com.github.paohaijiao.enums.JQuickSqlUnaryOperator;
import com.github.paohaijiao.exception.JAssert;
import com.github.paohaijiao.expression.JQuickSqlLiteralExpression;
import com.github.paohaijiao.model.JQuickSqlFullColumnModel;
import com.github.paohaijiao.parser.JQuickSQLParser;

import java.util.Date;

/**
 * packageName com.github.paohaijiao.visitor
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/8/11
 */
public class JQuikSQLValueStatementVisitor extends JQuikSQLCoreVisitor {
    @Override
    public String visitKeyword(JQuickSQLParser.KeywordContext ctx) {
        String text = ctx.getText();
        return text;
    }

    @Override
    public String visitSimpleId(JQuickSQLParser.SimpleIdContext ctx) {
        String text = ctx.getText();
        return text;
    }

    @Override
    public Object visitUid(JQuickSQLParser.UidContext ctx) {
        JAssert.notNull(ctx.simpleId(), "not null");
        return visitSimpleId(ctx.simpleId());
    }

    @Override
    public String visitDottedId(JQuickSQLParser.DottedIdContext ctx) {
        JAssert.notNull(ctx.uid(), "variable not null");
        return ctx.uid().getText();
    }


    @Override
    public Boolean visitBooleanLiteral(JQuickSQLParser.BooleanLiteralContext ctx) {
        if (ctx.TRUE() != null) {
            return true;
        }
        if (ctx.FALSE() != null) {
            return false;
        }
        JAssert.throwNewException("invalid boolean literal");
        return false;
    }

    @Override
    public Date visitDateLiteral(JQuickSQLParser.DateLiteralContext ctx) {
        JAssert.notNull(ctx.stringLiteral(), "date  literal expected");
        JAssert.notNull(ctx.format(), "format expected");
        String format = visitFormat(ctx.format());
        String dateString = visitStringLiteral(ctx.stringLiteral());
        return JDateUtil.parse(JDateUtil.getSimpleDateFormat(format), dateString);
    }

    @Override
    public JQuickSqlLiteralExpression visitConstant(JQuickSQLParser.ConstantContext ctx) {
        if (null != ctx.stringLiteral()) {
            return JQuickSqlLiteralExpression.string(visitStringLiteral(ctx.stringLiteral()));
        } else if (ctx.decimal_literal() != null) {
            return JQuickSqlLiteralExpression.number(getNumber(ctx.decimal_literal().getText()));
        } else if (ctx.booleanLiteral() != null) {
            return JQuickSqlLiteralExpression.bool(visitBooleanLiteral(ctx.booleanLiteral()));
        } else if (null != ctx.null_literal()) {
            return JQuickSqlLiteralExpression.nullValue();
        } else if (null != ctx.dateLiteral()) {
            return JQuickSqlLiteralExpression.date(visitDateLiteral(ctx.dateLiteral()));
        }
        JAssert.throwNewException("invalid constant literal");
        return null;
    }

    @Override
    public String visitFormat(JQuickSQLParser.FormatContext ctx) {
        JAssert.notNull(ctx.stringLiteral(), "string literal expected");
        return trim(ctx.stringLiteral().getText());
    }

    @Override
    public String visitStringLiteral(JQuickSQLParser.StringLiteralContext ctx) {
        JAssert.notNull(ctx.STRING_LITERAL(), "string literal expected");
        return trim(ctx.STRING_LITERAL().getText());
    }

    @Override
    public JQuickSqlComparisonOperator visitComparisonOperator(JQuickSQLParser.ComparisonOperatorContext ctx) {
        return JQuickSqlComparisonOperator.symbolOf(ctx.getText());
    }

    @Override
    public JQuickSqlLogicalOperator visitLogicalOperator(JQuickSQLParser.LogicalOperatorContext ctx) {
        if (ctx.AND() != null || ctx.getText().equals("&&")) {
            return JQuickSqlLogicalOperator.And;
        }
        if (ctx.OR() != null || ctx.getText().equals("||")) {
            return JQuickSqlLogicalOperator.Or;
        }
        if (ctx.XOR() != null) {
            return JQuickSqlLogicalOperator.XOR;
        }
        JAssert.throwNewException("invalid logical operator");
        return null;
    }

    @Override
    public JQuickSqlUnaryOperator visitUnaryOperator(JQuickSQLParser.UnaryOperatorContext ctx) {
        return JQuickSqlUnaryOperator.symbolOf(ctx.getText());
    }

    @Override
    public JQuickSqlMathOperator visitMathOperator(JQuickSQLParser.MathOperatorContext ctx) {
        return JQuickSqlMathOperator.codeOf(ctx.getText());
    }

    @Override
    public JQuickSqlFullColumnModel visitFullColumnName(JQuickSQLParser.FullColumnNameContext ctx) {
        JQuickSqlFullColumnModel fullColumnModel = new JQuickSqlFullColumnModel();
        if (ctx.dottedId() != null) {
            fullColumnModel.setTableName(ctx.uid().getText());
            fullColumnModel.setColumnName(visitDottedId(ctx.dottedId()));
        } else {
            fullColumnModel.setColumnName(ctx.uid().getText());
        }
        return fullColumnModel;
    }


}
