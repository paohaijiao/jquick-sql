package com.github.paohaijiao.enums;

public enum JComparisonOperator {
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

    JComparisonOperator(String symbol) {
        this.symbol = symbol;
    }

    public static JComparisonOperator symbolOf(String symbol){
        for(JComparisonOperator operator : values()){
            if(operator.symbol.equals(symbol)){
                return operator;
            }
        }
        throw new IllegalArgumentException("Unknown operator: " + symbol);
    }

    public String getSymbol() { return symbol; }
}
