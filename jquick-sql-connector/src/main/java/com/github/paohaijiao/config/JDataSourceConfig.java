package com.github.paohaijiao.config;

import com.github.paohaijiao.ds.JDBCBaseConnectionConfig;

import java.util.HashMap;

public class JDataSourceConfig extends HashMap<String, Object> {

    public static final String TYPE = "type";
    public static final String jdbc = "jdbc";
    public static final String PATH = "path";

    public String getType() {
        return getString(TYPE);
    }

    public JDataSourceConfig setType(String type) {
        put(TYPE, type);
        return this;
    }

    public JDBCBaseConnectionConfig getJdbc() {
         Object value=get(jdbc);
         return null==value ? null : (JDBCBaseConnectionConfig)value;
    }

    public JDataSourceConfig setJdbc(JDBCBaseConnectionConfig config) {
        this.put(jdbc,config);
        return this;
    }

    public JDataSourceConfig setPath(String path) {
        put(PATH, path);
        return this;
    }


    public String getPath() {
        return getString(PATH);
    }



    public String getString(String key) {
        Object value = get(key);
        return value != null ? value.toString() : null;
    }

    public Integer getInteger(String key) {
        Object value = get(key);
        if (value instanceof Number) {
            return ((Number)value).intValue();
        } else if (value != null) {
            try {
                return Integer.parseInt(value.toString());
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    public Long getLong(String key) {
        Object value = get(key);
        if (value instanceof Number) {
            return ((Number)value).longValue();
        } else if (value != null) {
            try {
                return Long.parseLong(value.toString());
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    public Boolean getBoolean(String key) {
        Object value = get(key);
        if (value instanceof Boolean) {
            return (Boolean)value;
        } else if (value != null) {
            return Boolean.parseBoolean(value.toString());
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> clazz) {
        Object value = get(key);
        return clazz.isInstance(value) ? (T)value : null;
    }

    public JDataSourceConfig with(String key, Object value) {
        put(key, value);
        return this;
    }
}
