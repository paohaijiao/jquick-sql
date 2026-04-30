package com.github.paohaijiao.enums;

import lombok.Getter;

@Getter
public enum JQuickSqlMathOperator {

    MULTIPLY("*", "Multiply"),

    DIVIDE("/", "Divide"),

    MODULO("%", "Modulo"),

    DIV("DIV", "Integer Division"),

    MOD("MOD", "Modulus"),

    ADD("+", "Add"),

    SUBTRACT("-", "Subtract"),

    DECREMENT("--", "Decrement");

    private final String code;
    private final String name;

    JQuickSqlMathOperator(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static JQuickSqlMathOperator codeOf(String code) {
        for (JQuickSqlMathOperator op : values()) {
            if (op.code.equals(code)) {
                return op;
            }
        }
        throw new IllegalArgumentException("Unknown operator code: " + code);
    }
}
