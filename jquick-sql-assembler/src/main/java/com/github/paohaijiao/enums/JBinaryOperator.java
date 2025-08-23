package com.github.paohaijiao.enums;

public enum JBinaryOperator {
    ADD("+"),
    SUBTRACT("-"),
    MULTIPLY("*"),
    DIVIDE("/"),
    MOD("%"),
    EQ("="),
    NEQ("<>"),
    GT(">"),
    LT("<"),
    GTE(">="),
    LTE("<="),
    AND("AND"),

    OR("OR"),

    CONCAT("||");


    private final String symbol;

    JBinaryOperator(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    public static JBinaryOperator of(String symbol) {
        for (JBinaryOperator op : values()) {
            if (op.getSymbol().equals(symbol)) {
                return op;
            }
        }
        throw new IllegalArgumentException(symbol + " is not a valid binary operator");
    }
}
