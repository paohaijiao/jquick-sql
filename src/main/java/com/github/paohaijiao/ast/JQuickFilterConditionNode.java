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
 * filterCondition
 * : filterCondition (AND filterCondition)
 * | filterCondition (OR filterCondition)
 * | '(' filterCondition ')'
 * | predicate
 */
public class JQuickFilterConditionNode implements JQuickASTNode {
    private final JQuickFilterConditionNode left;
    private final JQuickFilterConditionNode right;
    private final LogicalOperator operator;
    private final JQuickPredicateNode predicate;
    private final boolean isParenExpression;

    // 二元逻辑运算构造器
    public JQuickFilterConditionNode(JQuickFilterConditionNode left, JQuickFilterConditionNode right, LogicalOperator operator) {
        this.left = left;
        this.right = right;
        this.operator = operator;
        this.predicate = null;
        this.isParenExpression = false;
    }

    // 括号表达式构造器
    public JQuickFilterConditionNode(JQuickFilterConditionNode inner, boolean isParen) {
        this.left = inner;
        this.right = null;
        this.operator = null;
        this.predicate = null;
        this.isParenExpression = isParen;
    }

    // 谓词构造器
    public JQuickFilterConditionNode(JQuickPredicateNode predicate) {
        this.left = null;
        this.right = null;
        this.operator = null;
        this.predicate = predicate;
        this.isParenExpression = false;
    }

    @Override
    public String getNodeType() {
        return "FilterCondition";
    }

    public boolean isBinary() {
        return left != null && right != null && operator != null;
    }

    public boolean isPredicate() {
        return predicate != null;
    }

    public boolean isParenExpression() {
        return isParenExpression;
    }

    public JQuickFilterConditionNode getLeft() {
        return left;
    }

    public JQuickFilterConditionNode getRight() {
        return right;
    }

    public LogicalOperator getOperator() {
        return operator;
    }

    public JQuickPredicateNode getPredicate() {
        return predicate;
    }

    public enum LogicalOperator {
        AND, OR
    }
}
