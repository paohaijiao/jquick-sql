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

import com.github.paohaijiao.enums.JFunctionCallType;
import com.github.paohaijiao.exception.JAssert;
import com.github.paohaijiao.model.JFunctionCallModel;
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
public class JQuikSQLFunctionStatementVisitor extends JQuikSQLPredictStatementVisitor {
    @Override
    public Object visitFunctionArg(JQuickSQLParser.FunctionArgContext ctx) {
        JAssert.notNull(ctx.expression(), "expression not  null");
        return visit(ctx.expression());
    }

    @Override
    public List<Object> visitFunctionArgs(JQuickSQLParser.FunctionArgsContext ctx) {
        List<Object> args = new ArrayList<>();
        for (int i = 0; i < ctx.functionArg().size(); i++) {
            Object obj=visitFunctionArg(ctx.functionArg(i));
            args.add(obj);
        }
        return args;
    }

    @Override
    public Object visitBinaryComparisonPredicate(
            JQuickSQLParser.BinaryComparisonPredicateContext ctx) {
        Object left = visit(ctx.predicate(0));
        Object right = visit(ctx.predicate(1));
        String operator = ctx.comparisonOperator().getText();
        if (left == null || right == null) {
            return handleNullComparison(operator, left, right);
        }
        switch (operator) {
            case "=":
                return compareValues(left, right) == 0;
            case "<>":
            case "!=":
                return compareValues(left, right) != 0;
            case "<":
                return compareValues(left, right) < 0;
            case "<=":
                return compareValues(left, right) <= 0;
            case ">":
                return compareValues(left, right) > 0;
            case ">=":
                return compareValues(left, right) >= 0;
            case "<=>":
                return Objects.equals(left, right);
            case "LIKE":
                return likeMatch(left.toString(), right.toString(), null);
            case "NOT LIKE":
                return !likeMatch(left.toString(), right.toString(), null);
            case "REGEXP":
            case "RLIKE":
                return regexpMatch(left.toString(), right.toString(), false);
            default:
                throw new UnsupportedOperationException("Unsupported operator: " + operator);
        }
    }
    @Override
    public JFunctionCallModel visitFunctionCall(JQuickSQLParser.FunctionCallContext ctx) {
        JAssert.notNull(ctx.uid(),"uid must not be null");
        JFunctionCallModel jFunctionCallModel = new JFunctionCallModel();
        String funcName = ctx.uid().getText();
        jFunctionCallModel.setFunctionName(funcName);
        List<Object> args = new ArrayList<>();
        if (ctx.functionArgs() != null) {
            args=visitFunctionArgs(ctx.functionArgs());
        }
        jFunctionCallModel.setArgument(args);
        jFunctionCallModel.setType(JFunctionCallType.Scalar);
        return jFunctionCallModel;
    }


    private Boolean handleNullComparison(String operator, Object left, Object right) {
        if ("<=>".equals(operator)) {
            return left == right; // MySQL特殊处理
        }
        if (operator.equalsIgnoreCase("IS")) {
            return left == null && right == null;
        }
        if (operator.equalsIgnoreCase("IS NOT")) {
            return left != null || right != null;
        }
        return null;
    }

    public boolean likeMatch(String input, String pattern, Character escapeChar) {
        StringBuilder regex = new StringBuilder();
        boolean escaping = false;
        for (int i = 0; i < pattern.length(); i++) {
            char c = pattern.charAt(i);
            if (escaping) {
                regex.append(Pattern.quote(String.valueOf(c)));
                escaping = false;
            } else {
                if (escapeChar != null && c == escapeChar) {
                    escaping = true;
                    continue;
                }
                switch (c) {
                    case '%':
                        regex.append(".*");
                        break;
                    case '_':
                        regex.append(".");
                        break;
                    default:
                        regex.append(Pattern.quote(String.valueOf(c)));
                }
            }
        }
        String regexPattern = "^" + regex + "$";
        return input.matches(regexPattern);
    }

    public boolean regexpMatch(String input, String pattern, boolean caseSensitive) {
        String javaPattern = pattern
                .replace("[[:<:]]", "\\b")
                .replace("[[:>:]]", "\\b");
        int flags = caseSensitive ? 0 : Pattern.CASE_INSENSITIVE;
        Pattern compiledPattern;
        try {
            compiledPattern = Pattern.compile(javaPattern, flags);
        } catch (PatternSyntaxException e) {
            throw new RuntimeException("Invalid regex pattern: " + pattern, e);
        }
        return compiledPattern.matcher(input).find();
    }


}
