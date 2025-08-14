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

    CONCAT("||"),

    IS("IS"),

    NOT("NOT"),

    LIKE("LIKE"),

    NOT_LIKE("NOT LIKE");


    private final String symbol;

    JBinaryOperator(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }
}
