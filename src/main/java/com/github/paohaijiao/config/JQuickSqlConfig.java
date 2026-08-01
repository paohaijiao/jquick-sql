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

import com.github.paohaijiao.console.JConsole;
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
public class JQuickSqlConfig {

    private static final JConsole console=JConsole.initConsoleEnvironment();

    private  JQuickSqlBannerConfig banner=null;

    private  JQuickSqlRuntimeConfig runtime=null;




    public JQuickSqlConfig() {
        initBanner();
        initRuntime();
    }

    public void initBanner(){
        JQuickBannerConfig bannerConfig=JQuickSqlBannerConfig.loadFromProperties();
        JQuickSqlBannerConfig sqlBannerConfig=new JQuickSqlBannerConfig();
        sqlBannerConfig.setEnabled(bannerConfig.isEnabled());
        sqlBannerConfig.setVersion(bannerConfig.getVersion());
        sqlBannerConfig.setAuthor(bannerConfig.getAuthor());
        sqlBannerConfig.setGithub(bannerConfig.getGithub());
        sqlBannerConfig.setEmail(bannerConfig.getEmail());
        sqlBannerConfig.setProjectName(bannerConfig.getProjectName());
        sqlBannerConfig.setSlogan(bannerConfig.getSlogan());
        this.banner=sqlBannerConfig;
    }
    public void initRuntime(){
        JQuickSqlRuntimeConfig runtimeConfig=JQuickSqlRuntimeConfig.loadFromProperties();
        this.runtime=runtimeConfig;
    }

    /**
     * 从文件加载配置
     */
    public static JQuickSqlConfig loadFromFile(String path) throws IOException {
        JQuickSqlConfig config = new JQuickSqlConfig();
        config.loadPropertiesFile(path);
        return config;
    }

    /**
     * 从Properties对象加载配置
     */
    public static JQuickSqlConfig loadFromProperties(Properties props) {
        JQuickSqlConfig config = new JQuickSqlConfig();
        config.applyProperties(props);
        return config;
    }

    /**
     * 从系统属性加载配置
     */
    public static JQuickSqlConfig loadFromSystemProperties() {
        JQuickSqlConfig config = new JQuickSqlConfig();
        config.applyProperties(System.getProperties());
        return config;
    }

    /**
     * 加载默认配置
     */
    private void loadFromProperty() {
        try (InputStream is = getClass().getResourceAsStream("/jquick-default.properties")) {
            if (is != null) {
                Properties props = new Properties();
                props.load(is);
                applyProperties(props);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (InputStream is = getClass().getResourceAsStream("/jquick.properties")) {
            if (is != null) {
                Properties props = new Properties();
                props.load(is);
                applyProperties(props);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String configPath = System.getProperty("jquick.config.path");
        if (configPath != null) {
            try {
                loadPropertiesFile(configPath);
            } catch (IOException e) {
                System.err.println("Failed to load config from " + configPath + ": " + e.getMessage());
            }
        }
    }
    public void loadPropertiesFile(String path) throws IOException {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(path)) {
            props.load(fis);
        }
        applyProperties(props);
    }
    private void setIfPresent(Properties props, String key, PropertySetter setter) {
        String value = props.getProperty(key);
        if (value != null) {
            try {
                setter.set(value);
            } catch (Exception e) {
                console.error("Failed to set property " + key + ": " + e.getMessage());
            }
        }
    }
    private void applyProperties(Properties props) {
        this.applyBannerProperties(props);
        this.applyRuntimeProperties(props);

    }
    private void applyRuntimeProperties(Properties props) {
        setIfPresent(props, "jquick.runtime.defaultParallelism", v -> runtime.setDefaultParallelism(Integer.parseInt(v)));
        setIfPresent(props, "jquick.runtime.maxFileSize", v -> runtime.setMaxFileSize(Integer.parseInt(v)));
    }
    /**
     * 应用Properties配置
     */
    private void applyBannerProperties(Properties props) {
        setIfPresent(props, "jquick.banner.enabled", v -> banner.setEnabled(Boolean.parseBoolean(v)));
        setIfPresent(props, "jquick.banner.version", v -> banner.setVersion(v));
        setIfPresent(props, "jquick.banner.author", v -> banner.setAuthor(v));
        setIfPresent(props, "jquick.banner.github", v -> banner.setGithub(v));
        setIfPresent(props, "jquick.banner.email", v -> banner.setEmail(v));
        setIfPresent(props, "jquick.banner.project", v -> banner.setProjectName(v));
        setIfPresent(props, "jquick.banner.slogan", v -> banner.setSlogan(v));
    }
    @FunctionalInterface
    private interface PropertySetter {
        void set(String value);
    }


}
