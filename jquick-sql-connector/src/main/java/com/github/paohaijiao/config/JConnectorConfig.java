package com.github.paohaijiao.config;

import java.util.HashMap;

public class JConnectorConfig extends HashMap<String, Object> {

    public JConnectorConfig() {
        super();
    }

    public JConnectorConfig(HashMap<String, Object> map) {
        super(map);
    }

    public JConnectorConfig set(String key, Object value) {
        put(key, value);
        return this;
    }

    public String getString(String key) {
        Object value = get(key);
        return value != null ? value.toString() : null;
    }

    public String getString(String key, String defaultValue) {
        Object value = get(key);
        return value != null ? value.toString() : defaultValue;
    }

    public Integer getInt(String key) {
        Object value = get(key);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        } else if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    public int getInt(String key, int defaultValue) {
        Integer value = getInt(key);
        return value != null ? value : defaultValue;
    }

    public Long getLong(String key) {
        Object value = get(key);
        if (value instanceof Number) {
            return ((Number) value).longValue();
        } else if (value instanceof String) {
            try {
                return Long.parseLong((String) value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    public long getLong(String key, long defaultValue) {
        Long value = getLong(key);
        return value != null ? value : defaultValue;
    }

    public Double getDouble(String key) {
        Object value = get(key);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        } else if (value instanceof String) {
            try {
                return Double.parseDouble((String) value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    public double getDouble(String key, double defaultValue) {
        Double value = getDouble(key);
        return value != null ? value : defaultValue;
    }

    public Boolean getBoolean(String key) {
        Object value = get(key);
        if (value instanceof Boolean) {
            return (Boolean) value;
        } else if (value instanceof String) {
            return Boolean.parseBoolean((String) value);
        } else if (value instanceof Number) {
            return ((Number) value).intValue() != 0;
        }
        return null;
    }

    public boolean getBoolean(String key, boolean defaultValue) {
        Boolean value = getBoolean(key);
        return value != null ? value : defaultValue;
    }

    public boolean contains(String key) {
        return containsKey(key);
    }

    public boolean isEmpty(String key) {
        Object value = get(key);
        return value == null ||
                (value instanceof String && ((String) value).isEmpty());
    }

    public JConnectorConfig merge(JConnectorConfig other) {
        if (other != null) {
            putAll(other);
        }
        return this;
    }

    public JConnectorConfig copy() {
        return new JConnectorConfig(new HashMap<>(this));
    }

}
