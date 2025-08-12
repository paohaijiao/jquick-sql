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

    public String getSymbol() {
        return symbol;
    }
}
