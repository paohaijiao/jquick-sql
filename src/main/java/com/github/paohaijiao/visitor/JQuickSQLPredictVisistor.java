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

import com.github.paohaijiao.ast.*;
import com.github.paohaijiao.parser.JQuickSQLParser;

import java.util.ArrayList;
import java.util.List;

/**
 * packageName com.github.paohaijiao.visitor
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/5/17
 */
public class JQuickSQLPredictVisistor extends JQuickSQLValueVisistor {

    @Override
    public JQuickPredicateNode.ComparisonOperator visitComparisonOperator(JQuickSQLParser.ComparisonOperatorContext ctx) {
        String operator = ctx.getText();
        switch (operator) {
            case "=":
                return JQuickPredicateNode.ComparisonOperator.EQ;
            case ">":
                return JQuickPredicateNode.ComparisonOperator.GT;
            case "<":
                return JQuickPredicateNode.ComparisonOperator.LT;
            case "<=":
                return JQuickPredicateNode.ComparisonOperator.LE;
            case ">=":
                return JQuickPredicateNode.ComparisonOperator.GE;
            case "!=":
                return JQuickPredicateNode.ComparisonOperator.NE;
            default:
                throw new RuntimeException("Unknown comparison operator: " + operator);
        }
    }
    @Override
    public JQuickFilterConditionNode.LogicalOperator visitLogicalOperator(JQuickSQLParser.LogicalOperatorContext ctx) {
        String operator = ctx.getText();
        switch (operator.toUpperCase()) {
            case "AND":
                return JQuickFilterConditionNode.LogicalOperator.AND;
            case "OR":
                return JQuickFilterConditionNode.LogicalOperator.OR;
            case "XOR":
                throw new RuntimeException("XOR operator not supported yet");
            default:
                throw new RuntimeException("Unknown logical operator: " + operator);
        }
    }
    @Override
    public JQuickExpressionAtomNode.UnaryOperator visitUnaryOperator(JQuickSQLParser.UnaryOperatorContext ctx) {
        String operator = ctx.getText();
        switch (operator) {
            case "!":
                return JQuickExpressionAtomNode.UnaryOperator.NOT;
            case "~":
                return JQuickExpressionAtomNode.UnaryOperator.BIT_NOT;
            case "+":
                return JQuickExpressionAtomNode.UnaryOperator.PLUS;
            case "-":
                return JQuickExpressionAtomNode.UnaryOperator.MINUS;
            case "NOT":
                return JQuickExpressionAtomNode.UnaryOperator.NOT;
            default:
                throw new RuntimeException("Unknown unary operator: " + operator);
        }
    }

    @Override
    public JQuickExpressionAtomNode.MathOperator visitMathOperator(JQuickSQLParser.MathOperatorContext ctx) {
        String operator = ctx.getText();
        switch (operator) {
            case "*":
                return JQuickExpressionAtomNode.MathOperator.MULTIPLY;
            case "/":
                return JQuickExpressionAtomNode.MathOperator.DIVIDE;
            case "%":
                return JQuickExpressionAtomNode.MathOperator.MODULO;
            case "+":
                return JQuickExpressionAtomNode.MathOperator.PLUS;
            case "-":
                return JQuickExpressionAtomNode.MathOperator.MINUS;
            case "--":
                return JQuickExpressionAtomNode.MathOperator.MINUS;
            default:
                throw new RuntimeException("Unknown math operator: " + operator);
        }
    }
    @Override
    public JQuickExpressionNode visitParenExpression(JQuickSQLParser.ParenExpressionContext ctx) {
        JQuickExpressionNode innerExpression = (JQuickExpressionNode) visit(ctx.expression());
        return new JQuickExpressionNode(innerExpression, true);
    }

    @Override
    public JQuickExpressionNode visitNotExpression(JQuickSQLParser.NotExpressionContext ctx) {
        JQuickExpressionNode expression = (JQuickExpressionNode) visit(ctx.expression());
        boolean isNot = true;
        return new JQuickExpressionNode(isNot, expression);
    }
    @Override
    public JQuickExpressionNode visitPredicateExpression(JQuickSQLParser.PredicateExpressionContext ctx) {
        JQuickExpressionAtomNode expressionAtom = (JQuickExpressionAtomNode) visit(ctx.expressionAtom());
        return new JQuickExpressionNode(expressionAtom);
    }

    @Override
    public JQuickExpressionNode visitSelectResult(JQuickSQLParser.SelectResultContext ctx) {
        JQuickSelectClauseNode selectClause = (JQuickSelectClauseNode) visit(ctx.selectClause());
        return new JQuickExpressionNode(selectClause);
    }
    @Override
    public JQuickExpressionsNode visitExpressions(JQuickSQLParser.ExpressionsContext ctx) {
        List<JQuickExpressionNode> expressions = new ArrayList<>();
        for (JQuickSQLParser.ExpressionContext exprCtx : ctx.expression()) {
            JQuickExpressionNode expression = (JQuickExpressionNode) visit(exprCtx);
            expressions.add(expression);
        }
        return new JQuickExpressionsNode(expressions);
    }

    @Override
    public JQuickExpressionAtomNode visitConstantExpressionAtom(JQuickSQLParser.ConstantExpressionAtomContext ctx) {
        JQuickConstantNode constant = (JQuickConstantNode) visit(ctx.constant());
        return new JQuickExpressionAtomNode(constant);
    }
    @Override
    public JQuickExpressionAtomNode visitFullColumnNameExpressionAtom(JQuickSQLParser.FullColumnNameExpressionAtomContext ctx) {
        JQuickFullColumnNameNode fullColumnName = (JQuickFullColumnNameNode) visit(ctx.fullColumnName());
        return new JQuickExpressionAtomNode(fullColumnName);
    }
    @Override
    public JQuickExpressionAtomNode visitFunctionCallExpressionAtom(JQuickSQLParser.FunctionCallExpressionAtomContext ctx) {
        JQuickFunctionCallNode functionCall = (JQuickFunctionCallNode) visit(ctx.functionCall());
        return new JQuickExpressionAtomNode(functionCall);
    }

    @Override
    public JQuickExpressionAtomNode visitNestedExpressionAtom(JQuickSQLParser.NestedExpressionAtomContext ctx) {
        List<JQuickExpressionNode> expressions = new ArrayList<>();
        for (JQuickSQLParser.ExpressionContext exprCtx : ctx.expression()) {
            expressions.add((JQuickExpressionNode) visit(exprCtx));
        }
        return new JQuickExpressionAtomNode(expressions);
    }

    @Override
    public JQuickExpressionAtomNode visitSubqueryExperssionAtom(JQuickSQLParser.SubqueryExperssionAtomContext ctx) {
        JQuickSelectStatementNode subquery = (JQuickSelectStatementNode) visit(ctx.selectStatement());
        return new JQuickExpressionAtomNode(subquery);
    }


    @Override
    public JQuickExpressionAtomNode visitMathExpressionAtom(JQuickSQLParser.MathExpressionAtomContext ctx) {
        JQuickExpressionAtomNode left = (JQuickExpressionAtomNode) visit(ctx.left);
        JQuickExpressionAtomNode right = (JQuickExpressionAtomNode) visit(ctx.right);
        JQuickExpressionAtomNode.MathOperator operator = parseMathOperator(ctx.mathOperator());
        return new JQuickExpressionAtomNode(left, right, operator);
    }


    @Override
    public JQuickExpressionAtomNode visitUnaryExpressionAtom(JQuickSQLParser.UnaryExpressionAtomContext ctx) {
        JQuickExpressionAtomNode expression = (JQuickExpressionAtomNode) visit(ctx.expressionAtom());
        JQuickExpressionAtomNode.UnaryOperator operator = parseUnaryOperator(ctx.unaryOperator());
        return new JQuickExpressionAtomNode(operator, expression);
    }
    /**
     * 访问表达式原子节点中的 CASE WHEN
     */
    @Override
    public JQuickExpressionAtomNode visitCaseWhenExpressionAtom(JQuickSQLParser.CaseWhenExpressionAtomContext ctx) {
        JQuickCaseWhenNode caseWhenNode= (JQuickCaseWhenNode) visit(ctx.caseWhen());
        return new JQuickExpressionAtomNode(caseWhenNode);
    }
    @Override
    public JQuickCaseWhenNode visitCaseWhen(JQuickSQLParser.CaseWhenContext ctx) {
        JQuickExpressionNode caseBase = null;
        if (ctx.caseBase() != null) {
            caseBase = (JQuickExpressionNode) visit(ctx.caseBase());
        }
        List<JQuickCaseWhenNode.WhenClause> whenClauses = new ArrayList<>();
        for (JQuickSQLParser.WhenClauseContext whenCtx : ctx.whenClause()) {
            JQuickPredicateNode condition = (JQuickPredicateNode) visit(whenCtx.condition);
            JQuickExpressionNode result = (JQuickExpressionNode) visit(whenCtx.result);
            whenClauses.add(new JQuickCaseWhenNode.WhenClause(condition, result));
        }
        JQuickExpressionNode elseExpression = null;
        if (ctx.ELSE() != null) {
            elseExpression = (JQuickExpressionNode) visit(ctx.expression());
        }
        return new JQuickCaseWhenNode(caseBase, whenClauses, elseExpression);
    }
    @Override
    public JQuickExpressionNode visitCaseBase(JQuickSQLParser.CaseBaseContext ctx) {
        return (JQuickExpressionNode) visit(ctx.expression());
    }



    @Override
    public JQuickPredicateNode visitExpressionAtomPredicate(JQuickSQLParser.ExpressionAtomPredicateContext ctx) {
        JQuickExpressionAtomNode expressionAtom = (JQuickExpressionAtomNode) visit(ctx.expressionAtom());
        return new JQuickPredicateNode(expressionAtom);
    }
    @Override
    public JQuickPredicateNode visitIsNullPredicate(JQuickSQLParser.IsNullPredicateContext ctx) {
        JQuickPredicateNode predicate = (JQuickPredicateNode) visit(ctx.predicate());
        boolean isNotNull = ctx.NOT() != null;
        return new JQuickPredicateNode(predicate, isNotNull);
    }
    @Override
    public JQuickPredicateNode visitBinaryComparisonPredicate(JQuickSQLParser.BinaryComparisonPredicateContext ctx) {
        JQuickPredicateNode left = (JQuickPredicateNode) visit(ctx.left);
        JQuickPredicateNode right = (JQuickPredicateNode) visit(ctx.right);
        JQuickPredicateNode.ComparisonOperator operator = parseComparisonOperator(ctx.comparisonOperator());
        return new JQuickPredicateNode(left, right, operator);
    }
    @Override
    public JQuickPredicateNode visitBetweenPredicate(JQuickSQLParser.BetweenPredicateContext ctx) {
        JQuickPredicateNode predicate = (JQuickPredicateNode) visit(ctx.predicate(0));
        boolean not = ctx.NOT() != null;
        JQuickPredicateNode low = (JQuickPredicateNode) visit(ctx.predicate(1));
        JQuickPredicateNode high = (JQuickPredicateNode) visit(ctx.predicate(2));
        return new JQuickPredicateNode(predicate, not, low, high);
    }
    @Override
    public JQuickPredicateNode visitInPredicate(JQuickSQLParser.InPredicateContext ctx) {
        JQuickPredicateNode predicate = (JQuickPredicateNode) visit(ctx.predicate());
        boolean not = ctx.NOT() != null;
        if (ctx.selectStatement() != null) {
            JQuickSelectStatementNode subquery = (JQuickSelectStatementNode) visit(ctx.selectStatement());
            return new JQuickPredicateNode(predicate, not, subquery);
        } else {
            JQuickExpressionsNode expressions = (JQuickExpressionsNode) visit(ctx.expressions());
            return new JQuickPredicateNode(predicate, not, expressions);
        }
    }

    @Override
    public JQuickPredicateNode visitLikePredicate(JQuickSQLParser.LikePredicateContext ctx) {
        JQuickPredicateNode predicate = (JQuickPredicateNode) visit(ctx.predicate(0));
        boolean not = ctx.NOT() != null;
        JQuickPredicateNode pattern = (JQuickPredicateNode) visit(ctx.predicate(1));
        JQuickPredicateNode.PredicateSubType subType= JQuickPredicateNode.PredicateSubType.LIKE;
        return new JQuickPredicateNode(predicate, not, pattern,subType);
    }

    @Override
    public JQuickPredicateNode visitRegexpPredicate(JQuickSQLParser.RegexpPredicateContext ctx) {
        JQuickPredicateNode predicate = (JQuickPredicateNode) visit(ctx.predicate(0));
        boolean not = ctx.NOT() != null;
        JQuickPredicateNode pattern = (JQuickPredicateNode) visit(ctx.predicate(1));
        JQuickPredicateNode predicateNode= new JQuickPredicateNode(predicate, not, pattern);
        return predicateNode;
    }

    @Override
    public JQuickPredicateNode visitExisitsPredicate(JQuickSQLParser.ExisitsPredicateContext ctx) {
        JQuickExpressionNode expression = (JQuickExpressionNode) visit(ctx.expression());
        return new JQuickPredicateNode(expression, true);
    }
    private JQuickPredicateNode.ComparisonOperator parseComparisonOperator(JQuickSQLParser.ComparisonOperatorContext ctx) {
        String operator = ctx.getText();
        switch (operator) {
            case "=": return JQuickPredicateNode.ComparisonOperator.EQ;
            case ">": return JQuickPredicateNode.ComparisonOperator.GT;
            case "<": return JQuickPredicateNode.ComparisonOperator.LT;
            case "<=": return JQuickPredicateNode.ComparisonOperator.LE;
            case ">=": return JQuickPredicateNode.ComparisonOperator.GE;
            case "!=": return JQuickPredicateNode.ComparisonOperator.NE;
            default: throw new RuntimeException("Unknown comparison operator: " + operator);
        }
    }




}
