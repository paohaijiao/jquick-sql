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

    // 字符串运算
    REGEX("REGEX", 30, false),

    NOT_REGEX("NOT REGEX", 30, false),
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
            case PLUS:
                if (left instanceof Number && right instanceof Number) {
                    Number leftNum = toNumber(left);
                    Number rightNum = toNumber(right);
                    return leftNum.doubleValue() + rightNum.doubleValue();
                }
                return left.toString() + right.toString();

            case MINUS:
                if (left instanceof Number && right instanceof Number) {
                    Number leftNum = toNumber(left);
                    Number rightNum = toNumber(right);
                    return leftNum.doubleValue() - rightNum.doubleValue();
                }
                return null;

            case MULTIPLY:
                if (left instanceof Number && right instanceof Number) {
                    Number leftNum = toNumber(left);
                    Number rightNum = toNumber(right);
                    return leftNum.doubleValue() * rightNum.doubleValue();
                }
                return null;

            case DIVIDE:
                if (left instanceof Number && right instanceof Number) {
                    Number leftNum = toNumber(left);
                    Number rightNum = toNumber(right);
                    double divisor = ((Number) right).doubleValue();
                    if (divisor == 0) return null;
                    return leftNum.doubleValue() / rightNum.doubleValue();

                }
                return null;
            case MODULO:
                if (left instanceof Number && right instanceof Number) {
                    return ((Number) left).doubleValue() % ((Number) right).doubleValue();
                }
                return null;
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
            case AND:
                return (Boolean) left && (Boolean) right;
            case OR:
                return (Boolean) left || (Boolean) right;
            case LIKE:
                return left.toString().toLowerCase().contains(right.toString().toLowerCase());
            case NOT_LIKE:
                return !left.toString().toLowerCase().contains(right.toString().toLowerCase());
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
    private Number toNumber(Object value) {
        if (value instanceof Number) {
            return Double.valueOf(value.toString());
        }
        if (value instanceof String) {
            try {
                return Double.parseDouble((String) value);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }
    private Object handleNulls(Object left, Object right) {
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
        if (left == null && right == null) return 0;
        if (left == null) return -1;
        if (right == null) return 1;
        if (left == right) return 0;
        if (left instanceof Number && right instanceof Number) {
            return compareNumbers((Number) left, (Number) right);
        }
        if (left instanceof String && right instanceof String) {
            return ((String) left).compareTo((String) right);
        }
        if (left instanceof Boolean && right instanceof Boolean) {
            return ((Boolean) left).compareTo((Boolean) right);
        }
        if (left instanceof Character && right instanceof Character) {
            return ((Character) left).compareTo((Character) right);
        }
        if (left instanceof java.util.Date && right instanceof java.util.Date) {
            return ((java.util.Date) left).compareTo((java.util.Date) right);
        }
        if (left instanceof Enum && right instanceof Enum) {
            int ordinalCompare = Integer.compare(((Enum<?>) left).ordinal(), ((Enum<?>) right).ordinal());
            if (ordinalCompare != 0) return ordinalCompare;
            return ((Enum<?>) left).name().compareTo(((Enum<?>) right).name());
        }
        if (left.getClass() == right.getClass() && left instanceof Comparable) {
            @SuppressWarnings("unchecked")
            Comparable<Object> comparable = (Comparable<Object>) left;
            return comparable.compareTo(right);
        }
        return left.toString().compareTo(right.toString());
    }
    private int compareNumbers(Number left, Number right) {
        java.math.BigDecimal leftDec = toBigDecimal(left);
        java.math.BigDecimal rightDec = toBigDecimal(right);
        return leftDec.compareTo(rightDec);
    }
    /**
     * 安全地将 Number 转换为 BigDecimal
     */
    private java.math.BigDecimal toBigDecimal(Number num) {
        if (num == null) {
            return java.math.BigDecimal.ZERO;
        }
        if (num instanceof java.math.BigDecimal) {
            return (java.math.BigDecimal) num;
        }
        if (num instanceof java.math.BigInteger) {
            return new java.math.BigDecimal((java.math.BigInteger) num);
        }
        if (num instanceof Double) {
            double d = num.doubleValue();
            if (Double.isNaN(d)) {
                return java.math.BigDecimal.ZERO;
            }
            if (Double.isInfinite(d)) {
                return d > 0 ? java.math.BigDecimal.valueOf(Long.MAX_VALUE)
                        : java.math.BigDecimal.valueOf(Long.MIN_VALUE);
            }
            return new java.math.BigDecimal(Double.toString(d));
        }
        if (num instanceof Float) {
            float f = num.floatValue();
            if (Float.isNaN(f)) {
                return java.math.BigDecimal.ZERO;
            }
            if (Float.isInfinite(f)) {
                return f > 0 ? java.math.BigDecimal.valueOf(Long.MAX_VALUE)
                        : java.math.BigDecimal.valueOf(Long.MIN_VALUE);
            }
            return new java.math.BigDecimal(Float.toString(f));
        }
        if (num instanceof Long) {
            return java.math.BigDecimal.valueOf(num.longValue());
        }
        return java.math.BigDecimal.valueOf(num.longValue());
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
