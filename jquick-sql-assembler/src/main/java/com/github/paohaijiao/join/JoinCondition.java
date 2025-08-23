package com.github.paohaijiao.join;

import java.util.Map;
import java.util.Objects;
import java.util.function.BiPredicate;

@FunctionalInterface
public interface JoinCondition {

    boolean test(Map<String, Object> leftRow, Map<String, Object> rightRow);

    default JoinCondition and(JoinCondition other) {
        return (l, r) -> this.test(l, r) && other.test(l, r);
    }

    default JoinCondition or(JoinCondition other) {
        return (l, r) -> this.test(l, r) || other.test(l, r);
    }
    static JoinCondition equals(String leftColumn, String rightColumn) {
        return (l, r) -> Objects.equals(l.get(leftColumn), r.get(rightColumn));
    }
    static JoinCondition of(BiPredicate<Map<String, Object>, Map<String, Object>> predicate) {
        return predicate::test;
    }
}
