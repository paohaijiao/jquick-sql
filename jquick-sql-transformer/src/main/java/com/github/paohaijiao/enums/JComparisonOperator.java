package com.github.paohaijiao.enums;

public enum JComparisonOperator {
    EQ("="),
    NEQ("<>"),
    GT(">"),
    LT("<"),
    GTE(">="),
    LTE("<="),
    LIKE("LIKE"),
    NOT_LIKE("NOT LIKE"),
    IS("IS"),
    IS_NOT("IS NOT");

    private final String symbol;

    JComparisonOperator(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() { return symbol; }
}
