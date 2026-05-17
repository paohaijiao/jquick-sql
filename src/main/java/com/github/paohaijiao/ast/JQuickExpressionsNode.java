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
 * expressions : expression (',' expression)*
 */
public class JQuickExpressionsNode implements JQuickASTNode {

    private final List<JQuickExpressionNode> expressions;

    public JQuickExpressionsNode(List<JQuickExpressionNode> expressions) {
        this.expressions = expressions;
    }

    @Override
    public String getNodeType() {
        return "Expressions";
    }

    public List<JQuickExpressionNode> getExpressions() {
        return expressions;
    }

    public int size() {
        return expressions.size();
    }
}