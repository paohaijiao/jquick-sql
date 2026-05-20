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
package com.github.paohaijiao.ast;


import java.util.List;

/**
 * expressionAtom
 * : constant                                                      #constantExpressionAtom
 * | fullColumnName                                                #fullColumnNameExpressionAtom
 * | functionCall                                                  #functionCallExpressionAtom
 * | '(' expression (',' expression)* ')'                          #nestedExpressionAtom
 * | '(' selectStatement ')'                                       #subqueryExperssionAtom
 * | left=expressionAtom mathOperator right=expressionAtom         #mathExpressionAtom
 * | unaryOperator expressionAtom                                  #unaryExpressionAtom
 */
public class JQuickExpressionAtomNode implements JQuickASTNode {
    private final JQuickConstantNode constant;
    private final JQuickFullColumnNameNode fullColumnName;
    private final JQuickFunctionCallNode functionCall;
    private final List<JQuickExpressionNode> nestedExpressions;
    private final JQuickSelectStatementNode subquery;
    private final JQuickExpressionAtomNode left;
    private final JQuickExpressionAtomNode right;
    private final MathOperator mathOperator;
    private final UnaryOperator unaryOperator;
    private final AtomType type;
    private final JQuickCaseWhenNode caseWhen;

    // 常量
    public JQuickExpressionAtomNode(JQuickConstantNode constant) {
        this.constant = constant;
        this.fullColumnName = null;
        this.functionCall = null;
        this.nestedExpressions = null;
        this.subquery = null;
        this.left = null;
        this.right = null;
        this.mathOperator = null;
        this.unaryOperator = null;
        this.type = AtomType.CONSTANT;
        this.caseWhen = null;
    }

    // 列引用
    public JQuickExpressionAtomNode(JQuickFullColumnNameNode fullColumnName) {
        this.constant = null;
        this.fullColumnName = fullColumnName;
        this.functionCall = null;
        this.nestedExpressions = null;
        this.subquery = null;
        this.left = null;
        this.right = null;
        this.mathOperator = null;
        this.unaryOperator = null;
        this.type = AtomType.COLUMN;
        this.caseWhen = null;
    }

    // 函数调用
    public JQuickExpressionAtomNode(JQuickFunctionCallNode functionCall) {
        this.constant = null;
        this.fullColumnName = null;
        this.functionCall = functionCall;
        this.nestedExpressions = null;
        this.subquery = null;
        this.left = null;
        this.right = null;
        this.mathOperator = null;
        this.unaryOperator = null;
        this.type = AtomType.FUNCTION;
        this.caseWhen = null;
    }

    // 嵌套表达式
    public JQuickExpressionAtomNode(List<JQuickExpressionNode> nestedExpressions) {
        this.constant = null;
        this.fullColumnName = null;
        this.functionCall = null;
        this.nestedExpressions = nestedExpressions;
        this.subquery = null;
        this.left = null;
        this.right = null;
        this.mathOperator = null;
        this.unaryOperator = null;
        this.type = AtomType.NESTED;
        this.caseWhen = null;
    }

    // 子查询
    public JQuickExpressionAtomNode(JQuickSelectStatementNode subquery) {
        this.constant = null;
        this.fullColumnName = null;
        this.functionCall = null;
        this.nestedExpressions = null;
        this.subquery = subquery;
        this.left = null;
        this.right = null;
        this.mathOperator = null;
        this.unaryOperator = null;
        this.type = AtomType.SUBQUERY;
        this.caseWhen = null;
    }

    // 数学运算
    public JQuickExpressionAtomNode(JQuickExpressionAtomNode left, JQuickExpressionAtomNode right, MathOperator mathOperator) {
        this.constant = null;
        this.fullColumnName = null;
        this.functionCall = null;
        this.nestedExpressions = null;
        this.subquery = null;
        this.left = left;
        this.right = right;
        this.mathOperator = mathOperator;
        this.unaryOperator = null;
        this.type = AtomType.MATH;
        this.caseWhen = null;
    }

    // 一元运算
    public JQuickExpressionAtomNode(UnaryOperator unaryOperator, JQuickExpressionAtomNode expression) {
        this.constant = null;
        this.fullColumnName = null;
        this.functionCall = null;
        this.nestedExpressions = null;
        this.subquery = null;
        this.left = expression;
        this.right = null;
        this.mathOperator = null;
        this.unaryOperator = unaryOperator;
        this.type = AtomType.UNARY;
        this.caseWhen = null;
    }
    public JQuickExpressionAtomNode(JQuickCaseWhenNode caseWhen) {
        this.constant = null;
        this.fullColumnName = null;
        this.functionCall = null;
        this.nestedExpressions = null;
        this.subquery = null;
        this.left = null;
        this.right = null;
        this.mathOperator = null;
        this.unaryOperator = null;
        this.caseWhen = caseWhen;
        this.type = AtomType.CASE_WHEN;
    }

    @Override
    public String getNodeType() {
        return "ExpressionAtom";
    }

    public AtomType getType() {
        return type;
    }

    public JQuickConstantNode getConstant() {
        return constant;
    }

    public JQuickFullColumnNameNode getFullColumnName() {
        return fullColumnName;
    }

    public JQuickFunctionCallNode getFunctionCall() {
        return functionCall;
    }

    public List<JQuickExpressionNode> getNestedExpressions() {
        return nestedExpressions;
    }

    public JQuickSelectStatementNode getSubquery() {
        return subquery;
    }

    public JQuickExpressionAtomNode getLeft() {
        return left;
    }

    public JQuickExpressionAtomNode getRight() {
        return right;
    }

    public MathOperator getMathOperator() {
        return mathOperator;
    }

    public UnaryOperator getUnaryOperator() {
        return unaryOperator;
    }

    public enum AtomType {
        CONSTANT, COLUMN, FUNCTION, NESTED, SUBQUERY, MATH, UNARY, CASE_WHEN
    }

    public enum MathOperator {
        MULTIPLY("*"), DIVIDE("/"), MODULO("%"), PLUS("+"), MINUS("-");
        private final String symbol;

        MathOperator(String symbol) {
            this.symbol = symbol;
        }

        public String getSymbol() {
            return symbol;
        }
    }

    public enum UnaryOperator {
        NOT("!"), BIT_NOT("~"), PLUS("+"), MINUS("-");
        private final String symbol;

        UnaryOperator(String symbol) {
            this.symbol = symbol;
        }

        public String getSymbol() {
            return symbol;
        }
    }
    public JQuickCaseWhenNode getCaseWhen() {
        return caseWhen;
    }
}
