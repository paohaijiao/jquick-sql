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
 * fullColumnName : uid dottedId?
 */
public class JQuickFullColumnNameNode implements JQuickASTNode {

    private final String uid;

    private final String dottedId;

    public JQuickFullColumnNameNode(String uid, String dottedId) {
        this.uid = uid;
        this.dottedId = dottedId;
    }

    @Override
    public String getNodeType() {
        return "FullColumnName";
    }

    public String getUid() {
        return uid;
    }

    public String getDottedId() {
        return dottedId;
    }

    public boolean hasDottedId() {
        return dottedId != null;
    }

    public String getFullName() {
        return hasDottedId() ? uid + "." + dottedId : uid;
    }

    public String getColumnName() {
        return hasDottedId() ? dottedId : uid;
    }

    public String getTableAlias() {
        return hasDottedId() ? uid : null;
    }
}
