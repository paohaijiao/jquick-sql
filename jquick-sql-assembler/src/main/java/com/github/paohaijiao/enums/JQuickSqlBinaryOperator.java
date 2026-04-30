package com.github.paohaijiao.enums;

public enum JQuickSqlBinaryOperator {
    ADD("+"),
    SUBTRACT("-"),
    MULTIPLY("*"),
    DIVIDE("/"),
    MOD("%"),
    EQ("="),
    NEQ("<>"),
    GT(">"),
    LT("<"),
    GE(">="),
    LE("<="),
    AND("AND"),

    OR("OR"),

    CONCAT("||");


    private final String symbol;

    JQuickSqlBinaryOperator(String symbol) {
        this.symbol = symbol;
    }

    public static JQuickSqlBinaryOperator of(String symbol) {
        for (JQuickSqlBinaryOperator op : values()) {
            if (op.getSymbol().equals(symbol)) {
                return op;
            }
        }
        throw new IllegalArgumentException(symbol + " is not a valid binary operator");
    }

    public String getSymbol() {
        return symbol;
    }
}
