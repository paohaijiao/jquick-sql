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
import com.github.paohaijiao.expression.JFunctionCallExpression;
import com.github.paohaijiao.parser.JQuickSQLParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * packageName com.github.paohaijiao.visitor
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/8/11
 */
public class JQuikSQLFunctionStatementVisitor extends JQuikSQLExpressionStatementVisitor {
    @Override
    public JExpression visitFunctionArg(JQuickSQLParser.FunctionArgContext ctx) {
        JAssert.notNull(ctx.expression(), "expression not  null");
        return (JExpression)visit(ctx.expression());
    }

    @Override
    public List<JExpression> visitFunctionArgs(JQuickSQLParser.FunctionArgsContext ctx) {
        List<JExpression> args = new ArrayList<>();
        for (int i = 0; i < ctx.functionArg().size(); i++) {
            JExpression obj=visitFunctionArg(ctx.functionArg(i));
            args.add(obj);
        }
        return args;
    }

    @Override
    public JFunctionCallExpression visitFunctionCall(JQuickSQLParser.FunctionCallContext ctx) {
        JAssert.notNull(ctx.uid(),"uid must not be null");
        String funcName = ctx.uid().getText();
        List<JExpression> args = new ArrayList<>();
        if (ctx.functionArgs() != null) {
            args=visitFunctionArgs(ctx.functionArgs());
        }
        JFunctionCallExpression jFunctionCallModel = new JFunctionCallExpression(funcName,args);
        return jFunctionCallModel;
    }


}
