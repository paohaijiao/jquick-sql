package com.github.paohaijiao.plan;

import java.util.Map;

public class JDataRow {
    private Map<String, Object> values;

    public Object getValue(String fieldName) {
        return values.get(fieldName);
    }
}
