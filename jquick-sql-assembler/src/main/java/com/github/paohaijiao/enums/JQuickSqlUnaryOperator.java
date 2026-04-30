package com.github.paohaijiao.enums;

public enum JQuickSqlUnaryOperator {

    PLUS("+"),

    MINUS("-"),

    NOT("NOT"),

    NT("!"),

    BIT_NOT("~");

    private final String symbol;

    JQuickSqlUnaryOperator(String symbol) {
        this.symbol = symbol;
    }

    public static JQuickSqlUnaryOperator symbolOf(String symbol) {
        for (JQuickSqlUnaryOperator jUnaryOperator : values()) {
            if (jUnaryOperator.symbol.equalsIgnoreCase(symbol)) {
                return jUnaryOperator;
            }
        }
        throw new IllegalArgumentException("Unknown unary operator: " + symbol);
    }

    public String getSymbol() {
        return symbol;
    }
}
