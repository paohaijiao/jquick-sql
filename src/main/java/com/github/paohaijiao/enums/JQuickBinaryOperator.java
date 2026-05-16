package com.github.paohaijiao.enums;

public enum JQuickBinaryOperator {
    // 算术运算
    PLUS("+", 10, true),
    MINUS("-", 10, true),
    MULTIPLY("*", 20, true),
    DIVIDE("/", 20, true),
    MODULO("%", 20, true),

    // 比较运算
    EQ("=", 30, false),
    NE("!=", 30, false),
    GT(">", 30, false),
    LT("<", 30, false),
    GE(">=", 30, false),
    LE("<=", 30, false),

    // 逻辑运算
    AND("AND", 5, false),
    OR("OR", 4, false),

    // 字符串运算
    LIKE("LIKE", 30, false),
    NOT_LIKE("NOT LIKE", 30, false),

    // 位运算
    BIT_AND("&", 25, true),
    BIT_OR("|", 25, true),
    BIT_XOR("^", 25, true),

    // 字符串连接
    CONCAT("||", 10, true);

    private final String symbol;
    private final int precedence;
    private final boolean isArithmetic;

    JQuickBinaryOperator(String symbol, int precedence, boolean isArithmetic) {
        this.symbol = symbol;
        this.precedence = precedence;
        this.isArithmetic = isArithmetic;
    }

    public String getSymbol() { return symbol; }
    public int getPrecedence() { return precedence; }
    public boolean isArithmetic() { return isArithmetic; }

    /**
     * 应用操作符
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public Object apply(Object left, Object right) {
        if (left == null || right == null) {
            return handleNulls(left, right);
        }

        switch (this) {
            // 算术运算
            case PLUS:
                if (left instanceof Number && right instanceof Number) {
                    return ((Number) left).doubleValue() + ((Number) right).doubleValue();
                }
                return left.toString() + right.toString();

            case MINUS:
                if (left instanceof Number && right instanceof Number) {
                    return ((Number) left).doubleValue() - ((Number) right).doubleValue();
                }
                return null;

            case MULTIPLY:
                if (left instanceof Number && right instanceof Number) {
                    return ((Number) left).doubleValue() * ((Number) right).doubleValue();
                }
                return null;

            case DIVIDE:
                if (left instanceof Number && right instanceof Number) {
                    double divisor = ((Number) right).doubleValue();
                    if (divisor == 0) return null;
                    return ((Number) left).doubleValue() / divisor;
                }
                return null;

            case MODULO:
                if (left instanceof Number && right instanceof Number) {
                    return ((Number) left).doubleValue() % ((Number) right).doubleValue();
                }
                return null;

            // 比较运算
            case EQ:
                return compare(left, right) == 0;
            case NE:
                return compare(left, right) != 0;
            case GT:
                return compare(left, right) > 0;
            case LT:
                return compare(left, right) < 0;
            case GE:
                return compare(left, right) >= 0;
            case LE:
                return compare(left, right) <= 0;

            // 逻辑运算
            case AND:
                return (Boolean) left && (Boolean) right;
            case OR:
                return (Boolean) left || (Boolean) right;

            // 字符串运算
            case LIKE:
                return left.toString().toLowerCase().contains(right.toString().toLowerCase());
            case NOT_LIKE:
                return !left.toString().toLowerCase().contains(right.toString().toLowerCase());

            // 位运算
            case BIT_AND:
                if (left instanceof Number && right instanceof Number) {
                    return ((Number) left).longValue() & ((Number) right).longValue();
                }
                return null;
            case BIT_OR:
                if (left instanceof Number && right instanceof Number) {
                    return ((Number) left).longValue() | ((Number) right).longValue();
                }
                return null;
            case BIT_XOR:
                if (left instanceof Number && right instanceof Number) {
                    return ((Number) left).longValue() ^ ((Number) right).longValue();
                }
                return null;

            case CONCAT:
                return left.toString() + right.toString();

            default:
                return null;
        }
    }

    private Object handleNulls(Object left, Object right) {
        // SQL三值逻辑：NULL参与比较返回NULL
        if (this == AND) {
            if (Boolean.FALSE.equals(left) || Boolean.FALSE.equals(right)) return false;
            return null;
        }
        if (this == OR) {
            if (Boolean.TRUE.equals(left) || Boolean.TRUE.equals(right)) return true;
            return null;
        }
        return null;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private int compare(Object left, Object right) {
        if (left instanceof Comparable && right instanceof Comparable) {
            return ((Comparable) left).compareTo(right);
        }
        return left.toString().compareTo(right.toString());
    }

    /**
     * 从字符串获取操作符
     */
    public static JQuickBinaryOperator fromString(String str) {
        String upper = str.toUpperCase();
        for (JQuickBinaryOperator op : values()) {
            if (op.symbol.equals(upper) || op.name().equals(upper)) {
                return op;
            }
        }
        return null;
    }
}
