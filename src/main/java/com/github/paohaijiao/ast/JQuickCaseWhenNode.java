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
 * caseWhen
 * : CASE caseBase? whenClause+ (ELSE expression)? END
 *
 * whenClause
 * : WHEN condition=predicate THEN result=expression
 */
public class JQuickCaseWhenNode implements JQuickASTNode {

    private final JQuickExpressionNode caseBase;

    private final List<WhenClause> whenClauses;

    private final JQuickExpressionNode elseExpression;

    private final CaseType caseType;

    /**
     * 构造器
     * @param caseBase CASE 后面的表达式（简单CASE时有值，搜索CASE时为null）
     * @param whenClauses WHEN 子句列表
     * @param elseExpression ELSE 表达式（可能为null）
     */
    public JQuickCaseWhenNode(JQuickExpressionNode caseBase, List<WhenClause> whenClauses, JQuickExpressionNode elseExpression) {
        this.caseBase = caseBase;
        this.whenClauses = whenClauses;
        this.elseExpression = elseExpression;
        this.caseType = caseBase == null ? CaseType.SEARCHED : CaseType.SIMPLE;
    }

    @Override
    public String getNodeType() {
        return "CaseWhen";
    }

    public JQuickExpressionNode getCaseBase() {
        return caseBase;
    }

    public List<WhenClause> getWhenClauses() {
        return whenClauses;
    }

    public JQuickExpressionNode getElseExpression() {
        return elseExpression;
    }

    public CaseType getCaseType() {
        return caseType;
    }

    public boolean hasElse() {
        return elseExpression != null;
    }

    public boolean isSimpleCase() {
        return caseType == CaseType.SIMPLE;
    }

    public boolean isSearchedCase() {
        return caseType == CaseType.SEARCHED;
    }

    /**
     * WHEN 子句
     * condition 使用 predicate 而不是 expression
     */
    public static class WhenClause {

        private final JQuickPredicateNode condition;

        private final JQuickExpressionNode result;

        public WhenClause(JQuickPredicateNode condition, JQuickExpressionNode result) {
            this.condition = condition;
            this.result = result;
        }

        public JQuickPredicateNode getCondition() {
            return condition;
        }

        public JQuickExpressionNode getResult() {
            return result;
        }
    }

    public enum CaseType {
        /** 简单CASE: CASE column WHEN value1 THEN result1 ... */
        SIMPLE,
        /** 搜索CASE: CASE WHEN condition1 THEN result1 ... */
        SEARCHED
    }
}