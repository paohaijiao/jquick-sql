package com.github.paohaijiao.model;

import java.util.Objects;

public class JSortField {

    private final String fieldName;

    private final Direction direction;

    private final NullHandling nullHandling;

    public enum Direction {
        ASC,
        DESC;
        public boolean isAscending() {
            return this == ASC;
        }
        public boolean isDescending() {
            return this == DESC;
        }
        public static Direction fromString(String value) {
            if (value == null) {
                return ASC;
            }
            try {
                return Direction.valueOf(value.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ASC;
            }
        }
    }

    public enum NullHandling {
        NATIVE,
        NULLS_FIRST,
        NULLS_LAST;
        public static NullHandling fromString(String value) {
            if (value == null) {
                return NATIVE;
            }
            try {
                return NullHandling.valueOf(value.toUpperCase());
            } catch (IllegalArgumentException e) {
                return NATIVE;
            }
        }
    }

    public JSortField(String fieldName) {
        this(fieldName, Direction.ASC);
    }

    public JSortField(String fieldName, Direction direction) {
        this(fieldName, direction, NullHandling.NATIVE);
    }

    public JSortField(String fieldName, Direction direction, NullHandling nullHandling) {
        if (fieldName == null || fieldName.trim().isEmpty()) {
            throw new IllegalArgumentException("排序字段名不能为空");
        }
        this.fieldName = fieldName.trim();
        this.direction = direction != null ? direction : Direction.ASC;
        this.nullHandling = nullHandling != null ? nullHandling : NullHandling.NATIVE;
    }


    public static JSortField asc(String fieldName) {
        return new JSortField(fieldName, Direction.ASC);
    }

    public static JSortField desc(String fieldName) {
        return new JSortField(fieldName, Direction.DESC);
    }

    public static JSortField by(String fieldName, Direction direction) {
        return new JSortField(fieldName, direction);
    }

    public static JSortField by(String fieldName, String direction) {
        return new JSortField(fieldName, Direction.fromString(direction));
    }

    public String getFieldName() {
        return fieldName;
    }

    public Direction getDirection() {
        return direction;
    }

    public NullHandling getNullHandling() {
        return nullHandling;
    }

    public boolean isAscending() {
        return direction.isAscending();
    }

    public boolean isDescending() {
        return direction.isDescending();
    }
    public JSortField with(Direction direction) {
        return new JSortField(this.fieldName, direction, this.nullHandling);
    }

    public JSortField with(NullHandling nullHandling) {
        return new JSortField(this.fieldName, this.direction, nullHandling);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JSortField sortField = (JSortField) o;
        return fieldName.equalsIgnoreCase(sortField.fieldName) &&
                direction == sortField.direction &&
                nullHandling == sortField.nullHandling;
    }

    @Override
    public int hashCode() {
        return Objects.hash(fieldName.toLowerCase(), direction, nullHandling);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(fieldName);
        sb.append(" ").append(direction);
        if (nullHandling != NullHandling.NATIVE) {
            sb.append(" ").append(nullHandling);
        }
        return sb.toString();
    }
}
