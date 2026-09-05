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
 * functionCall : uid '(' arg? ')'
 */
public class JQuickFunctionCallNode implements JQuickASTNode {

    private final String functionName;

    private final List<JQuickFunctionArgNode> arguments;

    private final boolean isStarArg;

    public JQuickFunctionCallNode(String functionName, List<JQuickFunctionArgNode> arguments, boolean isStarArg) {
        this.functionName = functionName;
        this.arguments = arguments;
        this.isStarArg = isStarArg;
    }

    @Override
    public String getNodeType() {
        return "FunctionCall";
    }

    public String getFunctionName() {
        return functionName;
    }

    public List<JQuickFunctionArgNode> getArguments() {
        return arguments;
    }

    public boolean isStarArg() {
        return isStarArg;
    }

    public boolean hasArguments() {
        return arguments != null && !arguments.isEmpty();
    }
}
