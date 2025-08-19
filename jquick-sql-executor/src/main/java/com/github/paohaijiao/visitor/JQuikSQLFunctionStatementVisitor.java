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
import com.github.paohaijiao.model.JDistinctArgument;
import com.github.paohaijiao.model.JStarArgumentModel;
import com.github.paohaijiao.parser.JQuickSQLParser;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;

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
    public Object visitFunctionArgs(JQuickSQLParser.FunctionArgsContext ctx) {
        if (ctx.getText().equals("*")) {
            return new JStarArgumentModel();
        }
        if (ctx.DISTINCT() != null) {
            List<Object> distinctArgs = new ArrayList<>();
            for (JQuickSQLParser.FunctionArgContext argCtx : ctx.functionArg()) {
                distinctArgs.add(visit(argCtx));
            }
            return new JDistinctArgument(distinctArgs);
        }
        List<Object> args = new ArrayList<>();
        for (int i = 0; i < ctx.getChildCount(); i++) {
            ParseTree child = ctx.getChild(i);
            if (child instanceof TerminalNode) continue;
            args.add(visit(child));
        }
        return args;
    }

    @Override
    public Object visitAggregateFunction(JQuickSQLParser.AggregateFunctionContext ctx) {
        return null;
    }

    @Override
    public Object visitBinaryComparisonPredicate(
            JQuickSQLParser.BinaryComparisonPredicateContext ctx) {
        Object left = visit(ctx.predicate(0));
        Object right = visit(ctx.predicate(1));
        String operator = ctx.comparisonOperator().getText();

        // NULL处理（标准SQL规则）
        if (left == null || right == null) {
            return handleNullComparison(operator, left, right);
        }

        // 根据运算符类型比较
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
    public List<Object> visitExpressions(JQuickSQLParser.ExpressionsContext ctx) {
        List<Object> results = new ArrayList<>();
        for (JQuickSQLParser.ExpressionContext exprCtx : ctx.expression()) {
            results.add(visit(exprCtx));
        }
        return results;
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
