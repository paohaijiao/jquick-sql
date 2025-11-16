package com.github.paohaijiao.join;

import java.util.Map;
import java.util.Objects;
import java.util.function.BiPredicate;

@FunctionalInterface
public interface JoinCondition {

    static JoinCondition equals(String leftColumn, String rightColumn) {
        return (l, r) -> {
            Object leftVal = l.get(leftColumn);
            Object rightVal = r.get(rightColumn);
            boolean result = Objects.equals(leftVal, rightVal);
            return result;
        };
    }

    static JoinCondition of(BiPredicate<Map<String, Object>, Map<String, Object>> predicate) {
        return predicate::test;
    }

    boolean test(Map<String, Object> leftRow, Map<String, Object> rightRow);

    default JoinCondition and(JoinCondition other) {
        return (l, r) -> this.test(l, r) && other.test(l, r);
    }

    default JoinCondition or(JoinCondition other) {
        return (l, r) -> this.test(l, r) || other.test(l, r);
    }
}
