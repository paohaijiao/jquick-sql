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

import com.github.paohaijiao.enums.JQuickJoinType;

/**
 * joinClause : joinType JOIN tableNameItem (ON fullColumnName '=' fullColumnName)?
 */
public class JQuickJoinClauseNode implements JQuickASTNode {
    private final JQuickJoinType joinType;
    private final JQuickTableNameItemNode tableNameItem;
    private final JQuickFullColumnNameNode leftColumn;
    private final JQuickFullColumnNameNode rightColumn;

    public JQuickJoinClauseNode(JQuickJoinType joinType, JQuickTableNameItemNode tableNameItem, JQuickFullColumnNameNode leftColumn, JQuickFullColumnNameNode rightColumn) {
        this.joinType = joinType;
        this.tableNameItem = tableNameItem;
        this.leftColumn = leftColumn;
        this.rightColumn = rightColumn;
    }

    @Override
    public String getNodeType() {
        return "JoinClause";
    }

    public JQuickJoinType getJoinType() {
        return joinType;
    }

    public JQuickTableNameItemNode getTableNameItem() {
        return tableNameItem;
    }

    public JQuickFullColumnNameNode getLeftColumn() {
        return leftColumn;
    }

    public JQuickFullColumnNameNode getRightColumn() {
        return rightColumn;
    }

    public boolean hasOnCondition() {
        return leftColumn != null && rightColumn != null;
    }

}
