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
 * tableNameItem
 * : tableNameSpec (AS? uid)?
 * | '(' selectExpression ')' AS uid
 */
public class JQuickTableNameItemNode implements JQuickASTNode {

    private final String tableName;

    private final String alias;

    private final JQuickSelectExpressionNode subquery;

    private final boolean isSubquery;

    public JQuickTableNameItemNode(String tableName, String alias) {
        this.tableName = tableName;
        this.alias = alias;
        this.subquery = null;
        this.isSubquery = false;
    }

    // 子查询构造器
    public JQuickTableNameItemNode(JQuickSelectExpressionNode subquery, String alias) {
        this.tableName = null;
        this.alias = alias;
        this.subquery = subquery;
        this.isSubquery = true;
    }

    @Override
    public String getNodeType() {
        return "TableNameItem";
    }

    public String getTableName() {
        return tableName;
    }

    public String getAlias() {
        return alias;
    }

    public JQuickSelectExpressionNode getSubquery() {
        return subquery;
    }

    public boolean isSubquery() {
        return isSubquery;
    }
}

