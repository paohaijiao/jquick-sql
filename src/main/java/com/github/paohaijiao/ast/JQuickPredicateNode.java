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
 * predicate
 * : expressionAtom                                                #expressionAtomPredicate
 * | predicate IS NOT? NULL                                        #isNullPredicate
 * | left=predicate comparisonOperator right=predicate             #binaryComparisonPredicate
 * | predicate NOT? BETWEEN predicate AND predicate                #betweenPredicate
 * | predicate NOT? IN '(' (selectStatement | expressions) ')'     #inPredicate
 * | predicate NOT? LIKE predicate                                 #likePredicate
 * | predicate NOT? regex=REGEXP predicate                         #regexpPredicate
 * | EXISTS expression                                             #existsPredicate
 */
public class JQuickPredicateNode implements JQuickASTNode {
    private final PredicateType type;

    // 表达式原子谓词
    private final JQuickExpressionAtomNode expressionAtom;

    // IS NULL谓词
    private final JQuickPredicateNode isNullPredicate;
    private final boolean isNotNull;

    // 二元比较谓词
    private final JQuickPredicateNode left;
    private final JQuickPredicateNode right;
    private final ComparisonOperator comparisonOperator;

    // BETWEEN谓词
    private final JQuickPredicateNode betweenPredicate;
    private final boolean betweenNot;
    private final JQuickPredicateNode betweenLow;
    private final JQuickPredicateNode betweenHigh;

    // IN谓词
    private final JQuickPredicateNode inPredicate;
    private final boolean inNot;
    private final JQuickSelectStatementNode inSubquery;
    private final JQuickExpressionsNode inExpressions;

    // LIKE谓词
    private final JQuickPredicateNode likePredicate;
    private final boolean likeNot;
    private final JQuickPredicateNode likePattern;

    // REGEXP谓词
    private final JQuickPredicateNode regexpPredicate;
    private final boolean regexpNot;
    private final JQuickPredicateNode regexpPattern;

    // EXISTS谓词
    private final JQuickExpressionNode existsExpression;

    // 表达式原子谓词构造器
    public JQuickPredicateNode(JQuickExpressionAtomNode expressionAtom) {
        this.type = PredicateType.EXPRESSION_ATOM;
        this.expressionAtom = expressionAtom;
        this.isNullPredicate = null;
        this.isNotNull = false;
        this.left = null;
        this.right = null;
        this.comparisonOperator = null;
        this.betweenPredicate = null;
        this.betweenNot = false;
        this.betweenLow = null;
        this.betweenHigh = null;
        this.inPredicate = null;
        this.inNot = false;
        this.inSubquery = null;
        this.inExpressions = null;
        this.likePredicate = null;
        this.likeNot = false;
        this.likePattern = null;
        this.regexpPredicate = null;
        this.regexpNot = false;
        this.regexpPattern = null;
        this.existsExpression = null;
    }

    // IS NULL谓词构造器
    public JQuickPredicateNode(JQuickPredicateNode predicate, boolean isNotNull) {
        this.type = PredicateType.IS_NULL;
        this.expressionAtom = null;
        this.isNullPredicate = predicate;
        this.isNotNull = isNotNull;
        this.left = null;
        this.right = null;
        this.comparisonOperator = null;
        this.betweenPredicate = null;
        this.betweenNot = false;
        this.betweenLow = null;
        this.betweenHigh = null;
        this.inPredicate = null;
        this.inNot = false;
        this.inSubquery = null;
        this.inExpressions = null;
        this.likePredicate = null;
        this.likeNot = false;
        this.likePattern = null;
        this.regexpPredicate = null;
        this.regexpNot = false;
        this.regexpPattern = null;
        this.existsExpression = null;
    }

    // 二元比较谓词构造器
    public JQuickPredicateNode(JQuickPredicateNode left, JQuickPredicateNode right, ComparisonOperator comparisonOperator) {
        this.type = PredicateType.BINARY_COMPARISON;
        this.expressionAtom = null;
        this.isNullPredicate = null;
        this.isNotNull = false;
        this.left = left;
        this.right = right;
        this.comparisonOperator = comparisonOperator;
        this.betweenPredicate = null;
        this.betweenNot = false;
        this.betweenLow = null;
        this.betweenHigh = null;
        this.inPredicate = null;
        this.inNot = false;
        this.inSubquery = null;
        this.inExpressions = null;
        this.likePredicate = null;
        this.likeNot = false;
        this.likePattern = null;
        this.regexpPredicate = null;
        this.regexpNot = false;
        this.regexpPattern = null;
        this.existsExpression = null;
    }

    // BETWEEN谓词构造器
    public JQuickPredicateNode(JQuickPredicateNode predicate, boolean not, JQuickPredicateNode low, JQuickPredicateNode high) {
        this.type = PredicateType.BETWEEN;
        this.expressionAtom = null;
        this.isNullPredicate = null;
        this.isNotNull = false;
        this.left = null;
        this.right = null;
        this.comparisonOperator = null;
        this.betweenPredicate = predicate;
        this.betweenNot = not;
        this.betweenLow = low;
        this.betweenHigh = high;
        this.inPredicate = null;
        this.inNot = false;
        this.inSubquery = null;
        this.inExpressions = null;
        this.likePredicate = null;
        this.likeNot = false;
        this.likePattern = null;
        this.regexpPredicate = null;
        this.regexpNot = false;
        this.regexpPattern = null;
        this.existsExpression = null;
    }

    // IN谓词构造器（子查询）
    public JQuickPredicateNode(JQuickPredicateNode predicate, boolean not, JQuickSelectStatementNode subquery) {
        this.type = PredicateType.IN;
        this.expressionAtom = null;
        this.isNullPredicate = null;
        this.isNotNull = false;
        this.left = null;
        this.right = null;
        this.comparisonOperator = null;
        this.betweenPredicate = null;
        this.betweenNot = false;
        this.betweenLow = null;
        this.betweenHigh = null;
        this.inPredicate = predicate;
        this.inNot = not;
        this.inSubquery = subquery;
        this.inExpressions = null;
        this.likePredicate = null;
        this.likeNot = false;
        this.likePattern = null;
        this.regexpPredicate = null;
        this.regexpNot = false;
        this.regexpPattern = null;
        this.existsExpression = null;
    }

    // IN谓词构造器（表达式列表）
    public JQuickPredicateNode(JQuickPredicateNode predicate, boolean not, JQuickExpressionsNode expressions) {
        this.type = PredicateType.IN;
        this.expressionAtom = null;
        this.isNullPredicate = null;
        this.isNotNull = false;
        this.left = null;
        this.right = null;
        this.comparisonOperator = null;
        this.betweenPredicate = null;
        this.betweenNot = false;
        this.betweenLow = null;
        this.betweenHigh = null;
        this.inPredicate = predicate;
        this.inNot = not;
        this.inSubquery = null;
        this.inExpressions = expressions;
        this.likePredicate = null;
        this.likeNot = false;
        this.likePattern = null;
        this.regexpPredicate = null;
        this.regexpNot = false;
        this.regexpPattern = null;
        this.existsExpression = null;
    }


    public JQuickPredicateNode(JQuickPredicateNode predicate, boolean not, JQuickPredicateNode pattern) {
        this.type = PredicateType.REGEXP;
        this.expressionAtom = null;
        this.isNullPredicate = null;
        this.isNotNull = false;
        this.left = null;
        this.right = null;
        this.comparisonOperator = null;
        this.betweenPredicate = null;
        this.betweenNot = false;
        this.betweenLow = null;
        this.betweenHigh = null;
        this.inPredicate = null;
        this.inNot = false;
        this.inSubquery = null;
        this.inExpressions = null;
        this.likePredicate = null;
        this.likeNot = false;
        this.likePattern = null;
        this.regexpPredicate = predicate;
        this.regexpNot = not;
        this.regexpPattern = pattern;
        this.existsExpression = null;
    }

    // EXISTS谓词构造器
    public JQuickPredicateNode(JQuickExpressionNode expression, boolean isExists) {
        this.type = PredicateType.EXISTS;
        this.expressionAtom = null;
        this.isNullPredicate = null;
        this.isNotNull = false;
        this.left = null;
        this.right = null;
        this.comparisonOperator = null;
        this.betweenPredicate = null;
        this.betweenNot = false;
        this.betweenLow = null;
        this.betweenHigh = null;
        this.inPredicate = null;
        this.inNot = false;
        this.inSubquery = null;
        this.inExpressions = null;
        this.likePredicate = null;
        this.likeNot = false;
        this.likePattern = null;
        this.regexpPredicate = null;
        this.regexpNot = false;
        this.regexpPattern = null;
        this.existsExpression = expression;
    }

    @Override
    public String getNodeType() {
        return "Predicate";
    }

    public PredicateType getType() {
        return type;
    }

    public JQuickExpressionAtomNode getExpressionAtom() {
        return expressionAtom;
    }

    public JQuickPredicateNode getIsNullPredicate() {
        return isNullPredicate;
    }

    public boolean isNotNull() {
        return isNotNull;
    }

    public JQuickPredicateNode getLeft() {
        return left;
    }

    public JQuickPredicateNode getRight() {
        return right;
    }

    public ComparisonOperator getComparisonOperator() {
        return comparisonOperator;
    }

    public JQuickPredicateNode getBetweenPredicate() {
        return betweenPredicate;
    }

    public boolean isBetweenNot() {
        return betweenNot;
    }

    public JQuickPredicateNode getBetweenLow() {
        return betweenLow;
    }

    public JQuickPredicateNode getBetweenHigh() {
        return betweenHigh;
    }

    public JQuickPredicateNode getInPredicate() {
        return inPredicate;
    }

    public boolean isInNot() {
        return inNot;
    }

    public JQuickSelectStatementNode getInSubquery() {
        return inSubquery;
    }

    public JQuickExpressionsNode getInExpressions() {
        return inExpressions;
    }

    public JQuickPredicateNode getLikePredicate() {
        return likePredicate;
    }

    public boolean isLikeNot() {
        return likeNot;
    }

    public JQuickPredicateNode getLikePattern() {
        return likePattern;
    }

    public JQuickPredicateNode getRegexpPredicate() {
        return regexpPredicate;
    }

    public boolean isRegexpNot() {
        return regexpNot;
    }

    public JQuickPredicateNode getRegexpPattern() {
        return regexpPattern;
    }

    public JQuickExpressionNode getExistsExpression() {
        return existsExpression;
    }

    public enum PredicateType {
        EXPRESSION_ATOM, IS_NULL, BINARY_COMPARISON, BETWEEN, IN, LIKE, REGEXP, EXISTS
    }

    public enum ComparisonOperator {
        EQ("="), GT(">"), LT("<"), LE("<="), GE(">="), NE("!=");
        private final String symbol;

        ComparisonOperator(String symbol) {
            this.symbol = symbol;
        }

        public String getSymbol() {
            return symbol;
        }
    }
}
