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

/**
 * expression
 * : '(' expression ')'                                 #parenExpression
 * | notOperator=(NOT | '!') expression                #notExpression
 * | expressionAtom                                    #predicateExpression
 * | selectClause                                      #selectResult
 */
public class JQuickExpressionNode implements JQuickASTNode {
    private final JQuickExpressionNode innerExpression;
    private final boolean isParen;
    private final boolean isNot;
    private final JQuickExpressionAtomNode expressionAtom;
    private final JQuickSelectClauseNode selectClause;
    private final JQuickCaseWhenNode caseWhenNode;
    private final ExpressionType type;

    // 括号表达式
    public JQuickExpressionNode(JQuickExpressionNode inner, boolean isParen) {
        this.innerExpression = inner;
        this.isParen = isParen;
        this.isNot = false;
        this.expressionAtom = null;
        this.selectClause = null;
        this.caseWhenNode = null;
        this.type = ExpressionType.PAREN;
    }

    // NOT表达式
    public JQuickExpressionNode(boolean isNot, JQuickExpressionNode expr) {
        this.innerExpression = expr;
        this.isParen = false;
        this.isNot = isNot;
        this.expressionAtom = null;
        this.selectClause = null;
        this.caseWhenNode = null;
        this.type = ExpressionType.NOT;
    }

    // 表达式原子
    public JQuickExpressionNode(JQuickExpressionAtomNode expressionAtom) {
        this.innerExpression = null;
        this.isParen = false;
        this.isNot = false;
        this.expressionAtom = expressionAtom;
        this.selectClause = null;
        this.caseWhenNode = null;
        this.type = ExpressionType.ATOM;
    }

    // 子查询
    public JQuickExpressionNode(JQuickSelectClauseNode selectClause) {
        this.innerExpression = null;
        this.isParen = false;
        this.isNot = false;
        this.expressionAtom = null;
        this.selectClause = selectClause;
        this.caseWhenNode = null;
        this.type = ExpressionType.SELECT;
    }
    public JQuickExpressionNode(JQuickCaseWhenNode caseWhenNode) {
        this.innerExpression = null;
        this.isParen = false;
        this.isNot = false;
        this.expressionAtom = null;
        this.caseWhenNode = caseWhenNode;
        this.selectClause = null;
        this.type = ExpressionType.CASEWHEN;
    }

    @Override
    public String getNodeType() {
        return "Expression";
    }

    public ExpressionType getType() {
        return type;
    }

    public JQuickExpressionNode getInnerExpression() {
        return innerExpression;
    }

    public boolean isParen() {
        return isParen;
    }

    public boolean isNot() {
        return isNot;
    }

    public JQuickExpressionAtomNode getExpressionAtom() {
        return expressionAtom;
    }

    public JQuickSelectClauseNode getSelectClause() {
        return selectClause;
    }

    public enum ExpressionType {
        PAREN, NOT, ATOM, SELECT,CASEWHEN
    }
}
