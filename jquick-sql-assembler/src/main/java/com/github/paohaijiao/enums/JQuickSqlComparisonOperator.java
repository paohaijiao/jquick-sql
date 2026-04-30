package com.github.paohaijiao.enums;

public enum JQuickSqlComparisonOperator {
    EQ("="),
    GT(">"),
    LT("<"),
    GE(">="),
    LE("<="),
    NEQ("!="),
    LIKE("LIKE"),
    NOT_LIKE("NOT LIKE"),
    IS("IS"),
    IS_NOT("IS NOT");

    private final String symbol;

    JQuickSqlComparisonOperator(String symbol) {
        this.symbol = symbol;
    }

    public static JQuickSqlComparisonOperator symbolOf(String symbol) {
        for (JQuickSqlComparisonOperator operator : values()) {
            if (operator.symbol.equals(symbol)) {
                return operator;
            }
        }
        throw new IllegalArgumentException("Unknown operator: " + symbol);
    }

    public String getSymbol() {
        return symbol;
    }
}
