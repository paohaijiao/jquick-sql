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

import com.github.paohaijiao.ast.JQuickDateLiteralNode;
import com.github.paohaijiao.ast.JQuickUidNode;
import com.github.paohaijiao.parser.JQuickSQLParser;

/**
 * packageName com.github.paohaijiao.visitor
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/5/17
 */
public class JQuickSQLValueVisistor extends JQuickSQLCoreVisistor {

    @Override
    public String visitSimpleId(JQuickSQLParser.SimpleIdContext ctx) {
        return ctx.IDENTIFIER().getText();
    }
    @Override
    public String visitStringLiteral(JQuickSQLParser.StringLiteralContext ctx) {
        String text = ctx.STRING_LITERAL().getText();
        return text.substring(1, text.length() - 1);
    }
    @Override
    public JQuickUidNode visitUid(JQuickSQLParser.UidContext ctx) {
        String uid=visitSimpleId(ctx.simpleId());
        return new JQuickUidNode(uid);
    }
    @Override
    public JQuickUidNode visitDottedId(JQuickSQLParser.DottedIdContext ctx) {
        return visitUid(ctx.uid());
    }
    @Override
    public Boolean visitBooleanLiteral(JQuickSQLParser.BooleanLiteralContext ctx) {
        return ctx.TRUE() != null;
    }
    @Override
    public String visitFormat(JQuickSQLParser.FormatContext ctx) {
        return visitStringLiteral(ctx.stringLiteral());
    }

    @Override
    public String visitNull_literal(JQuickSQLParser.Null_literalContext ctx) {
        return ctx.NULL().getText();
    }

    @Override
    public JQuickDateLiteralNode visitDateLiteral(JQuickSQLParser.DateLiteralContext ctx) {
        String dateString = visitStringLiteral(ctx.stringLiteral());
        String format = visitFormat(ctx.format());
        return new JQuickDateLiteralNode(dateString, format);
    }
    @Override
    public String visitDecimal_literal(JQuickSQLParser.Decimal_literalContext ctx) {
        return ctx.DECIMAL_LITERAL().getText();
    }



}
