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
 * selectElement : expression (AS? uid)?
 */
public class JQuickSelectElementNode implements JQuickASTNode {
    private final JQuickExpressionNode expression;
    private final String alias;

    public JQuickSelectElementNode(JQuickExpressionNode expression, String alias) {
        this.expression = expression;
        this.alias = alias;
    }

    @Override
    public String getNodeType() {
        return "SelectElement";
    }

    public JQuickExpressionNode getExpression() {
        return expression;
    }

    public String getAlias() {
        return alias;
    }

    public boolean hasAlias() {
        return alias != null && !alias.isEmpty();
    }
}
