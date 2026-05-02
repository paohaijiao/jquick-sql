package com.github.paohaijiao.join;

import java.util.Map;
import java.util.Objects;
import java.util.function.BiPredicate;

@FunctionalInterface
public interface JQuickSqlJoinCondition {

    static JQuickSqlJoinCondition equals(String leftColumn, String rightColumn) {
        return (l, r) -> {
            Object leftVal = l.get(leftColumn);
            Object rightVal = r.get(rightColumn);
            boolean result = Objects.equals(leftVal, rightVal);
            return result;
        };
    }

    static JQuickSqlJoinCondition of(BiPredicate<Map<String, Object>, Map<String, Object>> predicate) {
        return predicate::test;
    }

    boolean test(Map<String, Object> leftRow, Map<String, Object> rightRow);

    default JQuickSqlJoinCondition and(JQuickSqlJoinCondition other) {
        return (l, r) -> this.test(l, r) && other.test(l, r);
    }

    default JQuickSqlJoinCondition or(JQuickSqlJoinCondition other) {
        return (l, r) -> this.test(l, r) || other.test(l, r);
    }
    default String getLeftColumn() {
        return null;
    }
    default String getRightColumn() {
        return null;
    }
    class EqualsJoinCondition implements JQuickSqlJoinCondition {
        private final String leftColumn;

        private final String rightColumn;

        public EqualsJoinCondition(String leftColumn, String rightColumn) {
            this.leftColumn = leftColumn;
            this.rightColumn = rightColumn;
        }

        @Override
        public boolean test(Map<String, Object> leftRow, Map<String, Object> rightRow) {
            Object leftVal = leftRow.get(leftColumn);
            Object rightVal = rightRow.get(rightColumn);
            return Objects.equals(leftVal, rightVal);
        }

        @Override
        public String getLeftColumn() {
            return leftColumn;
        }

        @Override
        public String getRightColumn() {
            return rightColumn;
        }
    }
}
