package com.github.paohaijiao.database;

import java.util.Map;

public interface JDataBase {

    String getDriverClassName();

    String getUrl();

    String getUsername();

    String getPassword();

    Map<String, String> getConnectionProperties();

    default String getProperty(String key) {
        return getConnectionProperties().get(key);
    }

    default String getProperty(String key, String defaultValue) {
        return getConnectionProperties().getOrDefault(key, defaultValue);
    }

}
