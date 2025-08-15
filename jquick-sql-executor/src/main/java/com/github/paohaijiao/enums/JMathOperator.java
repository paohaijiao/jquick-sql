package com.github.paohaijiao.enums;

public enum JMathOperator {
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

    JMathOperator(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static JMathOperator codeOf(String code) {
        for (JMathOperator op : values()) {
            if (op.code.equals(code)) {
                return op;
            }
        }
        throw new IllegalArgumentException("Unknown operator code: " + code);
    }
}
