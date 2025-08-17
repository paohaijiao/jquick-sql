package com.github.paohaijiao.enums;

public enum JUnaryOperator {

    PLUS("+"),

    MINUS("-"),

    NOT("NOT"),

    BIT_NOT("~");

    private final String symbol;

    JUnaryOperator(String symbol) {
        this.symbol = symbol;
    }

    public static JUnaryOperator symbolOf(String symbol) {
        for (JUnaryOperator jUnaryOperator : values()) {
            if (jUnaryOperator.symbol.equals(symbol)) {
                return jUnaryOperator;
            }
        }
        throw new IllegalArgumentException("Unknown unary operator: " + symbol);
    }

    public String getSymbol() {
        return symbol;
    }
}
