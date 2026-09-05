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
 * commonTableExpression
 *   : uid columnNames? AS '(' selectStatement ')'                  // 非递归CTE
 *   | uid columnNames? AS '(' initialQuery UNION ALL? recursivePart ')'  // 递归CTE
 */
public class JQuickCommonTableExpressionNode implements JQuickASTNode {

    private final String name;

    private final List<String> columnNames;

    private final JQuickSelectStatementNode query;

    private final boolean recursive;

    private final JQuickSelectStatementNode initialQuery;

    private final JQuickSelectStatementNode recursivePart;

    private final boolean unionAll;

    // 非递归CTE构造器
    public JQuickCommonTableExpressionNode(String name, List<String> columnNames, JQuickSelectStatementNode query) {
        this.name = name;
        this.columnNames = columnNames;
        this.query = query;
        this.recursive = false;
        this.initialQuery = null;
        this.recursivePart = null;
        this.unionAll = false;
    }

    // 递归CTE构造器
    public JQuickCommonTableExpressionNode(String name, List<String> columnNames, JQuickSelectStatementNode initialQuery, JQuickSelectStatementNode recursivePart, boolean unionAll) {
        this.name = name;
        this.columnNames = columnNames;
        this.query = null;
        this.recursive = true;
        this.initialQuery = initialQuery;
        this.recursivePart = recursivePart;
        this.unionAll = unionAll;
    }

    @Override
    public String getNodeType() {
        return "CommonTableExpression";
    }

    public String getName() {
        return name;
    }

    public List<String> getColumnNames() {
        return columnNames;
    }

    public JQuickSelectStatementNode getQuery() {
        return query;
    }

    public boolean isRecursive() {
        return recursive;
    }

    public JQuickSelectStatementNode getInitialQuery() {
        return initialQuery;
    }

    public JQuickSelectStatementNode getRecursivePart() {
        return recursivePart;
    }

    public boolean isUnionAll() {
        return unionAll;
    }
}
