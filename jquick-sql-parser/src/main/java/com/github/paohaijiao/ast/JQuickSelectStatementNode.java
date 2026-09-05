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
 * selectStatement
 * : WITH RECURSIVE? cte=commonTableExpression (',' commonTableExpression)* selectExpression #cteQuery
 * | selectExpression #singleQuery
 */
public class JQuickSelectStatementNode implements JQuickASTNode {

    private final boolean withRecursive;

    private final List<JQuickCommonTableExpressionNode> ctes;

    private final JQuickSelectExpressionNode selectExpression;

    private final boolean isCteQuery;

    // CTE查询构造器
    public JQuickSelectStatementNode(boolean withRecursive, List<JQuickCommonTableExpressionNode> ctes, JQuickSelectExpressionNode selectExpression) {
        this.withRecursive = withRecursive;
        this.ctes = ctes;
        this.selectExpression = selectExpression;
        this.isCteQuery = true;
    }

    // 普通查询构造器
    public JQuickSelectStatementNode(JQuickSelectExpressionNode selectExpression) {
        this.withRecursive = false;
        this.ctes = null;
        this.selectExpression = selectExpression;
        this.isCteQuery = false;
    }

    @Override
    public String getNodeType() {
        return "SelectStatement";
    }

    public boolean isWithRecursive() {
        return withRecursive;
    }

    public List<JQuickCommonTableExpressionNode> getCtes() {
        return ctes;
    }

    public JQuickSelectExpressionNode getSelectExpression() {
        return selectExpression;
    }

    public boolean isCteQuery() {
        return isCteQuery;
    }
}
