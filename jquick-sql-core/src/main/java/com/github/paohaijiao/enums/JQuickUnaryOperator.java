package com.github.paohaijiao.enums;

public enum JQuickUnaryOperator {

    NOT("NOT", false),

    PLUS("+", false),

    MINUS("-", false),

    IS_NULL("IS NULL", false),

    IS_NOT_NULL("IS NOT NULL", false);

    private final String symbol;

    private final boolean isPostfix;

    JQuickUnaryOperator(String symbol, boolean isPostfix) {
        this.symbol = symbol;
        this.isPostfix = isPostfix;
    }

    public String getSymbol() { return symbol; }

    public boolean isPostfix() { return isPostfix; }
}
