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
import com.github.paohaijiao.enums.JComparisonOperator;
import com.github.paohaijiao.enums.JLogicalOperator;
import com.github.paohaijiao.enums.JMathOperator;
import com.github.paohaijiao.enums.JUnaryOperator;
import com.github.paohaijiao.exception.JAssert;
import com.github.paohaijiao.expression.JColumnExpression;
import com.github.paohaijiao.expression.JLiteralExpression;
import com.github.paohaijiao.model.JFullColumnModel;
import com.github.paohaijiao.parser.JQuickSQLParser;

import java.util.Date;

/**
 * packageName com.github.paohaijiao.visitor
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/8/11
 */
public class JQuikSQLValueStatementVisitor extends JQuikSQLCoreVisitor{
    @Override
    public String visitKeyword(JQuickSQLParser.KeywordContext ctx) {
        String text=ctx.getText();
        return text;
    }
    @Override
    public String visitSimpleId(JQuickSQLParser.SimpleIdContext ctx) {
        String text=ctx.getText();
        return text;
    }
    @Override
    public Object visitUid(JQuickSQLParser.UidContext ctx) {
        JAssert.notNull(ctx.simpleId(),"not null");
        return visitSimpleId(ctx.simpleId());
    }

    @Override
    public Object visitDottedId(JQuickSQLParser.DottedIdContext ctx) {
        return visit(ctx.uid());
    }
    @Override
    public JFullColumnModel visitTableName(JQuickSQLParser.TableNameContext ctx) {
        JFullColumnModel jFullColumnModel = new JFullColumnModel();
        if(ctx.schema!=null){
            jFullColumnModel.setSchemaName(ctx.schema.getText());
        }
        if(ctx.table!=null){
            jFullColumnModel.setTableName(ctx.table.getText());
        }
        return jFullColumnModel;
    }


    @Override
    public Boolean visitBooleanLiteral(JQuickSQLParser.BooleanLiteralContext ctx) {
        if(ctx.TRUE() != null){
            return true;
        }
        if(ctx.FALSE() != null){
            return false;
        }
        JAssert.throwNewException("invalid boolean literal");
        return false;
    }
    @Override
    public Date visitDateLiteral(JQuickSQLParser.DateLiteralContext ctx) {
        JAssert.notNull(ctx.stringLiteral(), "date  literal expected");
        JAssert.notNull(ctx.format(), "format expected");
        String format=visitFormat( ctx.format());
        String dateString=visitStringLiteral( ctx.stringLiteral());
        return JDateUtil.parse(JDateUtil.getSimpleDateFormat(format),dateString);
    }

    @Override
    public JLiteralExpression visitConstant(JQuickSQLParser.ConstantContext ctx) {
        if(null!=ctx.stringLiteral()){
            return JLiteralExpression.string(visitStringLiteral(ctx.stringLiteral()));
        } else if (ctx.decimal_literal()!=null) {
            return JLiteralExpression.number(getNumber(ctx.decimal_literal().getText()));
        } else if (ctx.booleanLiteral()!=null) {
            return JLiteralExpression.bool(visitBooleanLiteral(ctx.booleanLiteral()));
        }else if(null!=ctx.null_literal()){
            return JLiteralExpression.nullValue();
        }else if(null!=ctx.dateLiteral()){
            return JLiteralExpression.date(visitDateLiteral(ctx.dateLiteral()));
        }
        JAssert.throwNewException("invalid constant literal");
        return null;
    }
    @Override
    public String visitFormat(JQuickSQLParser.FormatContext ctx) {
        JAssert.notNull(ctx.stringLiteral(),"string literal expected");
        return trim(ctx.stringLiteral().getText());
    }
    @Override
    public String visitStringLiteral(JQuickSQLParser.StringLiteralContext ctx) {
        JAssert.notNull(ctx.STRING_LITERAL(),"string literal expected");
        return trim(ctx.STRING_LITERAL().getText());
    }

    @Override
    public JComparisonOperator visitComparisonOperator(JQuickSQLParser.ComparisonOperatorContext ctx) {
        return JComparisonOperator.symbolOf(ctx.getText());
    }
    @Override
    public JLogicalOperator visitLogicalOperator(JQuickSQLParser.LogicalOperatorContext ctx) {
        if(ctx.AND() != null||ctx.getText().equals("&&")){
            return JLogicalOperator.And;
        }
        if(ctx.OR() != null||ctx.getText().equals("||")){
            return JLogicalOperator.Or;
        }
        if(ctx.XOR() != null){
            return JLogicalOperator.XOR;
        }
        JAssert.throwNewException("invalid logical operator");
        return null;
    }
    @Override
    public JUnaryOperator visitUnaryOperator(JQuickSQLParser.UnaryOperatorContext ctx) {
        return JUnaryOperator.symbolOf(ctx.getText());
    }
    @Override
    public JMathOperator visitMathOperator(JQuickSQLParser.MathOperatorContext ctx) {
        return JMathOperator.codeOf(ctx.getText());
    }


    @Override
    public JColumnExpression visitFullColumnNameExpressionAtom(JQuickSQLParser.FullColumnNameExpressionAtomContext ctx) {
        String  column= ctx.fullColumnName().getText();
        JColumnExpression columnExpression = new JColumnExpression(column);
        return columnExpression;
    }
    @Override
    public JFullColumnModel visitFullColumnName(JQuickSQLParser.FullColumnNameContext ctx) {
        String columnPath =ctx.getText();
        String[] parts = columnPath.split("\\.");
        JFullColumnModel fullColumnModel = new JFullColumnModel();
        if(parts.length==1){
            fullColumnModel.setColumnName(parts[0]);
        }else if(parts.length==2){
            fullColumnModel.setColumnName(parts[0]);
            fullColumnModel.setTableName(parts[1]);
        } else if (parts.length==3) {
            fullColumnModel.setColumnName(parts[0]);
            fullColumnModel.setTableName(parts[1]);
            fullColumnModel.setSchemaName(parts[2]);
        }else{
            JAssert.throwNewException("Invalid column name: " + columnPath);
            return null;
        }
        return fullColumnModel;
    }





}
