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
package com.github.paohaijiao.jdbc.conf;

import com.github.paohaijiao.config.JConnectorConfig;
import com.github.paohaijiao.holder.JConnectorConfigHolder;
import lombok.Data;

/**
 * packageName com.github.paohaijiao.jdbc.conf
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/8/26
 */
@Data
public class JDbcConnectorConfig extends JConnectorConfigHolder {
    private String url;
    private String username;
    private String password;
    private String sql;
    private String maxPoolSize;
    private String minPoolSize;
    private String connectionTimeout;

    public JConnectorConfig createJdbcConfig(String url, String username, String password, String sql) {
        return new JConnectorConfig()
                .set("type", "jdbc")
                .set("url", url)
                .set("username", username)
                .set("password", password)
                .set("sql", sql)
                .set("driverClass", "")
                .set("connectionTimeout", 30)
                .set("maxPoolSize", 10)
                .set("minPoolSize", 1);
    }
}
