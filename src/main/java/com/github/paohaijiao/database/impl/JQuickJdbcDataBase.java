/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Copyright (c) [2025-2099] Martin (goudingcheng@gmail.com)
 */
package com.github.paohaijiao.database.impl;

import com.github.paohaijiao.database.JDataBase;
import com.github.paohaijiao.enums.JDatabaseType;

import java.util.HashMap;
import java.util.Map;

/**
 * packageName com.github.paohaijiao.database.impl
 *
 * @author Martin
 * @version 1.0.0
 * @className JQuickJdbcDataBase
 * @date 2025/6/24
 * @description
 */
public class JQuickJdbcDataBase implements JDataBase {
    private JDatabaseType databaseType;
    private String host;
    private int port;
    private String databaseName;
    private String username;
    private String password;
    private Map<String, String> properties = new HashMap<>();

    public JQuickJdbcDataBase(JDatabaseType databaseType, String host, int port,
                              String databaseName, String username, String password) {
        this.databaseType = databaseType;
        this.host = host;
        this.port = port;
        this.databaseName = databaseName;
        this.username = username;
        this.password = password;
    }

    @Override
    public String getDriverClassName() {
        return databaseType.getDriverClass();
    }

    @Override
    public String getUrl() {
        switch (databaseType) {
            case MYSQL:
                return String.format("jdbc:mysql://%s:%d/%s?useSSL=false&serverTimezone=UTC",
                        host, port, databaseName);
            case POSTGRESQL:
                return String.format("jdbc:postgresql://%s:%d/%s", host, port, databaseName);
            case ORACLE:
                return String.format("jdbc:oracle:thin:@%s:%d:%s", host, port, databaseName);
            case SQLSERVER:
                return String.format("jdbc:sqlserver://%s:%d;databaseName=%s",
                        host, port, databaseName);
            case H2:
                return String.format("jdbc:h2:mem:%s", databaseName);
            case SQLITE:
                return String.format("jdbc:sqlite:%s", databaseName);
            default:
                throw new IllegalArgumentException("Unsupported database type: " + databaseType);
        }
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public Map<String, String> getConnectionProperties() {
        return properties;
    }

    public JQuickJdbcDataBase addProperty(String key, String value) {
        properties.put(key, value);
        return this;
    }
}
