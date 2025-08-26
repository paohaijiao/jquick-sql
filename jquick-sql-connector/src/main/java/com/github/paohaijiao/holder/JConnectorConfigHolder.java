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
package com.github.paohaijiao.holder;

import com.github.paohaijiao.config.JConnectorConfig;
import lombok.Data;

import java.util.HashMap;

/**
 * packageName com.github.paohaijiao.holder
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/8/26
 */
@Data
public class JConnectorConfigHolder  {

    protected  String type;




    public JConnectorConfig createJsonConfig(String filePath) {
        return new JConnectorConfig()
                .set("type", "json")
                .set("filePath", filePath)
                .set("encoding", "UTF-8")
                .set("prettyPrint", false);
    }



    public JConnectorConfig createHttpConfig(String url) {
        return new JConnectorConfig()
                .set("type", "http")
                .set("url", url)
                .set("method", "GET")
                .set("timeout", 5000)
                .set("headers", new HashMap<String, String>());
    }
    public JConnectorConfig createKafkaConfig(String bootstrapServers, String topic) {
        return new JConnectorConfig()
                .set("type", "kafka")
                .set("bootstrapServers", bootstrapServers)
                .set("topic", topic)
                .set("groupId", "default-group")
                .set("autoOffsetReset", "latest");
    }

    public JConnectorConfig createRedisConfig(String host, int port) {
        return new JConnectorConfig()
                .set("type", "redis")
                .set("host", host)
                .set("port", port)
                .set("database", 0)
                .set("timeout", 2000);
    }

    public JConnectorConfig createMongoConfig(String connectionString, String database) {
        return new JConnectorConfig()
                .set("type", "mongodb")
                .set("connectionString", connectionString)
                .set("database", database)
                .set("collection", "")
                .set("maxPoolSize", 10);
    }

    public JConnectorConfig createElasticsearchConfig(String host, int port) {
        return new JConnectorConfig()
                .set("type", "elasticsearch")
                .set("host", host)
                .set("port", port)
                .set("scheme", "http")
                .set("index", "");
    }

    public JConnectorConfig createCustomConfig(String type) {
        return new JConnectorConfig()
                .set("type", type)
                .set("enabled", true);
    }

}
