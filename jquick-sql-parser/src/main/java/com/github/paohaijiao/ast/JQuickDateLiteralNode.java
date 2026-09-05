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
 * packageName com.github.paohaijiao.ast
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/5/17
 */
public class JQuickDateLiteralNode implements JQuickASTNode {

    private final String dateString;

    private final String format;

    public JQuickDateLiteralNode(String dateString, String format) {
        this.dateString = dateString;
        this.format = format;
    }

    @Override
    public String getNodeType() {
        return "DateLiteral";
    }

    public String getDateString() { return dateString; }
    public String getFormat() { return format; }
}
