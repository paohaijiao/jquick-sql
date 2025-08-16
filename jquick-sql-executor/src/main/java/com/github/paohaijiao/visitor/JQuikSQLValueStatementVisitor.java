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

import com.github.paohaijiao.enums.JComparisonOperator;
import com.github.paohaijiao.enums.JLogicalOperator;
import com.github.paohaijiao.enums.JMathOperator;
import com.github.paohaijiao.enums.JUnaryOperator;
import com.github.paohaijiao.exception.JAssert;
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
    public String visitSchemaName(JQuickSQLParser.SchemaNameContext ctx) {
        return ctx.getText();
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
    public Object visitConstant(JQuickSQLParser.ConstantContext ctx) {
        if(null!=ctx.STRING_LITERAL()){
            return trim(ctx.STRING_LITERAL().getText());
        } else if (ctx.DECIMAL_LITERAL()!=null) {
            return getNumber(ctx.DECIMAL_LITERAL().getText());
        } else if (ctx.booleanLiteral()!=null) {
            return visitBooleanLiteral(ctx.booleanLiteral());
        }else if(null!=ctx.NULL_LITERAL()){
            return null;
        }else if(null!=ctx.dateLiteral()){
            return visit(ctx.dateLiteral());
        }
        JAssert.throwNewException("invalid constant literal");
        return null;
    }
    @Override
    public String visitFormat(JQuickSQLParser.FormatContext ctx) {
        return ctx.STRING_LITERAL().getText();
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
    public Object visitUid(JQuickSQLParser.UidContext ctx) {
        if (ctx.STRING_LITERAL() != null) {
            return trim(ctx.STRING_LITERAL().getText());
        }
        return visit(ctx.simpleId());
    }

    @Override
    public Object visitSimpleId(JQuickSQLParser.SimpleIdContext ctx) {
        if (ctx.ID_LITERAL() != null) {
            return ctx.ID_LITERAL().getText();
        }
        return ctx.keyword().getText();
    }

    @Override
    public Object visitDottedId(JQuickSQLParser.DottedIdContext ctx) {
        return "." + visit(ctx.uid());
    }
    @Override
    public JFullColumnModel visitFullColumnNameExpressionAtom(
            JQuickSQLParser.FullColumnNameExpressionAtomContext ctx) {
        String columnPath = ctx.fullColumnName().getText();
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
