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
 * constant
 * : stringLiteral
 * | decimal_literal
 * | '-' decimal_literal
 * | booleanLiteral
 * | null_literal
 * | dateLiteral
 */
public class JQuickConstantNode implements JQuickASTNode {

    private final Object value;

    private final ConstantType type;

    public JQuickConstantNode(Object value, ConstantType type) {
        this.value = value;
        this.type = type;
    }

    @Override
    public String getNodeType() {
        return "Constant";
    }

    public Object getValue() {
        return value;
    }

    public ConstantType getType() {
        return type;
    }

    public boolean isString() {
        return type == ConstantType.STRING;
    }

    public boolean isNumber() {
        return type == ConstantType.DECIMAL;
    }

    public boolean isBoolean() {
        return type == ConstantType.BOOLEAN;
    }

    public boolean isNull() {
        return type == ConstantType.NULL;
    }

    public boolean isDate() {
        return type == ConstantType.DATE;
    }

    public String getStringValue() {
        return (String) value;
    }

    public Number getNumberValue() {
        return (Number) value;
    }

    public Boolean getBooleanValue() {
        return (Boolean) value;
    }

    public enum ConstantType {
        STRING, DECIMAL, BOOLEAN, NULL, DATE
    }
}
