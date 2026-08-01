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
package com.github.paohaijiao.config;

import com.github.paohaijiao.distributed.coordinator.JQuickCoordinator;
import lombok.Data;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * packageName com.github.paohaijiao.config
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/5/17
 */
@Data
public class JQuickSqlBannerConfig {

    private boolean enabled = true;

    private String version = "1.0.0";

    private String author = "泡海椒";

    private String github = "@paohaijiao";

    private String email = "goudingcheng@gmail.com";

    private String projectName = "JQUICK";

    private String slogan = "极速·简洁·现代";

    public static JQuickBannerConfig loadFromProperties() {
        JQuickBannerConfig config = new JQuickBannerConfig();
        try (InputStream input = JQuickBannerConfig.class
                .getClassLoader()
                .getResourceAsStream("jquick-sql.properties")) {
            if (input != null) {
                Properties props = new Properties();
                props.load(input);
                config.setEnabled(Boolean.parseBoolean(props.getProperty("jquick.banner.enabled", "true")));
                config.setVersion(props.getProperty("jquick.banner.version", config.getVersion()));
                config.setAuthor(props.getProperty("jquick.banner.author", config.getAuthor()));
                config.setGithub(props.getProperty("jquick.banner.github", config.getGithub()));
                config.setEmail(props.getProperty("jquick.banner.email", config.getEmail()));
                config.setProjectName(props.getProperty("jquick.banner.project", config.getProjectName()));
                config.setSlogan(props.getProperty("jquick.banner.slogan", config.getSlogan()));
            }
        } catch (Exception e) {
            // 使用默认值
        }
        return config;
    }

}
