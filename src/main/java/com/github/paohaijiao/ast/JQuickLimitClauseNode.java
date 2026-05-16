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

import ognl.ExpressionNode;

/**
 * limitClause
 * : LIMIT limitOnly
 * | LIMIT limitWithOffset
 */
public class JQuickLimitClauseNode implements JQuickASTNode {
    private final ExpressionNode limit;
    private final ExpressionNode offset;
    private final boolean hasOffset;

    public JQuickLimitClauseNode(ExpressionNode limit) {
        this.limit = limit;
        this.offset = null;
        this.hasOffset = false;
    }

    // limitWithOffset构造器
    public JQuickLimitClauseNode(ExpressionNode offset, ExpressionNode limit) {
        this.limit = limit;
        this.offset = offset;
        this.hasOffset = true;
    }

    @Override
    public String getNodeType() {
        return "LimitClause";
    }

    public ExpressionNode getLimit() {
        return limit;
    }

    public ExpressionNode getOffset() {
        return offset;
    }

    public boolean hasOffset() {
        return hasOffset;
    }
}
