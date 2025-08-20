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
import com.github.paohaijiao.parser.JQuickSQLParser;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * packageName com.github.paohaijiao.visitor
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/8/11
 */
public class JQuikSQLPredictStatementVisitor extends JQuikSQLExpressionStatementVisitor {
    @Override
    public Object visitExpressionAtomPredicate(JQuickSQLParser.ExpressionAtomPredicateContext ctx) {
        JAssert.notNull(ctx.expressionAtom(), "expressionAtom not null");
        return visit(ctx.expressionAtom());
    }

    @Override
    public Object visitIsNullPredicate(JQuickSQLParser.IsNullPredicateContext ctx) {
        Object value = visit(ctx.predicate());
        boolean isNot = ctx.NOT() != null;
        boolean isNull = (value == null);
        return isNot != isNull;
    }

    @Override
    public Object visitBinaryComparisonPredicate(JQuickSQLParser.BinaryComparisonPredicateContext ctx) {
        Object left = visit(ctx.predicate(0));
        Object right = visit(ctx.predicate(1));
        String operator = ctx.comparisonOperator().getText();
        if (left == null || right == null) {
            return handleNullComparison(operator, left, right);
        }
        switch (operator) {
            case "=":
            case "<=>":
                return compareValues(left, right) == 0;
            case "<>":
            case "!=":
                return compareValues(left, right) != 0;
            case ">":
                return compareValues(left, right) > 0;
            case "<":
                return compareValues(left, right) < 0;
            case ">=":
                return compareValues(left, right) >= 0;
            case "<=":
                return compareValues(left, right) <= 0;
            case "LIKE":
                return patternMatch(left.toString(), right.toString(), false);
            case "NOT LIKE":
                return !patternMatch(left.toString(), right.toString(), false);
            case "REGEXP":
            case "RLIKE":
                return patternMatch(left.toString(), right.toString(), true);
            default:
                throw new UnsupportedOperationException("Unsupported operator: " + operator);
        }
    }

    @Override
    public Object visitBetweenPredicate(JQuickSQLParser.BetweenPredicateContext ctx) {
        Object target = visit(ctx.predicate(0));
        Object lowerBound = visit(ctx.predicate(1));
        Object upperBound = visit(ctx.predicate(2));
        boolean isNot = ctx.NOT() != null;
        if (target == null || lowerBound == null || upperBound == null) {
            return null;
        }
        boolean isBetween = compareValues(target, lowerBound) >= 0 && compareValues(target, upperBound) <= 0;
        return isNot != isBetween;
    }

    @Override
    public Object visitInPredicate(JQuickSQLParser.InPredicateContext ctx) {
        Object leftValue = visit(ctx.predicate());
        boolean isNot = ctx.NOT() != null;
        if (leftValue == null) {
            return null;
        }
        List<Object> rightValues;
        if (ctx.selectStatement() != null) {
            rightValues = (List<Object>) visit(ctx.selectStatement());
        } else {
            rightValues = new ArrayList<>();
            for (JQuickSQLParser.ExpressionContext exprCtx : ctx.expressions().expression()) {
                rightValues.add(visit(exprCtx));
            }
        }
        boolean isIn = checkValueInList(leftValue, rightValues);
        return isNot != isIn;
    }

    @Override
    public Boolean visitLikePredicate(JQuickSQLParser.LikePredicateContext ctx) {
        Object left = visit(ctx.predicate(0));
        Object right = visit(ctx.predicate(1));
        boolean isNot = ctx.NOT() != null;
        if (left == null || right == null) {
            return null;
        }
        String input = left.toString();
        String pattern = right.toString();
        boolean matches = likeMatch(input, pattern);
        return isNot != matches;
    }

    @Override
    public Boolean visitRegexpPredicate(JQuickSQLParser.RegexpPredicateContext ctx) {
        Object inputObj = visit(ctx.predicate(0));
        Object patternObj = visit(ctx.predicate(1));
        boolean isNot = ctx.NOT() != null;
        if (inputObj == null || patternObj == null) {
            return null;
        }
        String input = inputObj.toString();
        String regex = patternObj.toString();
        boolean matches;
        try {
            matches = input.matches(regex);
        } catch (PatternSyntaxException e) {
            throw new RuntimeException("Invalid regex pattern: " + regex);
        }
        return isNot != matches;
    }


    @Override
    public Object visitExisitsExpression(JQuickSQLParser.ExisitsExpressionContext ctx) {
        Object subqueryResult = visit(ctx.expression());
        if (subqueryResult instanceof List) {
            return !((List<?>) subqueryResult).isEmpty();
        }
        throw new RuntimeException("EXISTS must be followed by a subquery");
    }
    private String removeQuotes(String str) {
        if (str == null || str.length() < 2) {
            return str;
        }
        char firstChar = str.charAt(0);
        char lastChar = str.charAt(str.length() - 1);
        if ((firstChar == '\'' && lastChar == '\'') ||
                (firstChar == '"' && lastChar == '"')) {
            return str.substring(1, str.length() - 1);
        }
        return str;
    }
    private boolean likeMatch(String input, String pattern) {
        String cleanInput = removeQuotes(input);
        String cleanPattern = removeQuotes(pattern);
        if (cleanPattern.equals("%")) {
            return !cleanInput.isEmpty();
        }
        if (cleanPattern.startsWith("%") && cleanPattern.endsWith("%")) {
            String middle = cleanPattern.substring(1, cleanPattern.length() - 1);
            return cleanInput.contains(middle);
        }
        if (cleanPattern.endsWith("%")) {
            String prefix = cleanPattern.substring(0, cleanPattern.length() - 1);
            return cleanInput.startsWith(prefix);
        }
        if (cleanPattern.startsWith("%")) {
            String suffix = cleanPattern.substring(1);
            return cleanInput.endsWith(suffix);
        }
        return cleanInput.equals(cleanPattern);
    }

    private boolean checkValueInList(Object target, List<Object> list) {
        for (Object item : list) {
            if (item == null) {
                continue;
            }
            if (compareValues(target, item) == 0) {
                return true;
            }
        }
        return false;
    }

//    private int compareValues(Object a, Object b) {
//        if (a instanceof Number && b instanceof Number) {
//            double d1 = ((Number) a).doubleValue();
//            double d2 = ((Number) b).doubleValue();
//            return Double.compare(d1, d2);
//        } else if (a instanceof Comparable && b instanceof Comparable) {
//            return ((Comparable) a).compareTo(b);
//        }
//        return a.toString().compareTo(b.toString());
//    }

    private Boolean handleNullComparison(String operator, Object left, Object right) {
        if ("<=>".equals(operator)) {
            return left == right;
        }
        return null;
    }

    private boolean patternMatch(String input, String pattern, boolean isRegex) {
        if (isRegex) {
            return input.matches(pattern);
        } else {
            String regex = pattern.replace("%", ".*").replace("_", ".");
            return input.matches(regex);
        }
    }

}
