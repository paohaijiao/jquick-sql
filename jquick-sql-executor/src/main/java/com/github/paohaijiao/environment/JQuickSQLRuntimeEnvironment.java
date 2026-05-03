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
package com.github.paohaijiao.environment;

import com.github.paohaijiao.client.JQuickJQuickClient;
import com.github.paohaijiao.config.JQuickClientConfig;
import com.github.paohaijiao.console.JConsole;
import com.github.paohaijiao.enums.JLogLevel;
import com.github.paohaijiao.param.JContext;
import com.github.paohaijiao.statement.JQuickDataSet;

import java.util.HashMap;
import java.util.Map;

/**
 * packageName com.github.paohaijiao.environment
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/5/3
 */
public class JQuickSQLRuntimeEnvironment {

    private String abilityProvider;

    private JContext context=new JContext();

    private Map<String, Object> extra=new HashMap<String, Object>();

    private Map<String, JQuickDataSet> datasetMap=new HashMap<String, JQuickDataSet>();

    private JQuickClientConfig clientConfig=new JQuickClientConfig();

    public JQuickSQLRuntimeEnvironment(String provider ,JQuickClientConfig clientConfig,Map<String, JQuickDataSet> datasetMap){
        this.abilityProvider=provider;
        this.datasetMap=datasetMap;
        this.clientConfig=clientConfig;

    }
    public JQuickSQLRuntimeEnvironment(String provider ,JQuickClientConfig clientConfig ,Map<String, JQuickDataSet> datasetMap,JContext jcontext){
        this(provider,clientConfig,datasetMap);
        if(jcontext!=null&&!jcontext.isEmpty()){
            context.putAll(jcontext);
        }
    }
    public JQuickSQLRuntimeEnvironment(String provider ,JQuickClientConfig clientConfig ,Map<String, JQuickDataSet> datasetMap,Map<String, Object> jextra){
        this(provider,clientConfig,datasetMap);
        if(jextra!=null&&!jextra.isEmpty()){
            extra.putAll(jextra);
        }
    }
    public Map<String, JQuickDataSet>  getDataSet(){
        return this.datasetMap;
    }
    public JQuickDataSet getDataSet(String tableName){
        return this.datasetMap.get(tableName);
    }

    public JQuickClientConfig getClientConfig() {
        return clientConfig;
    }

    public JQuickSQLRuntimeEnvironment setAbilityProvider(String abilityProvider) {
        this.abilityProvider = abilityProvider;
        return this;
    }
    public String getAbilityProvider() {
        return abilityProvider;
    }

    public Map<String, Object> getExtra() {
        return extra;
    }

    public void setExtra(Map<String, Object> extra) {
        this.extra = extra;
    }

    public JContext getContext() {
        return context;
    }
    public JQuickSQLRuntimeEnvironment setContext(JContext context) {
        if (null != context && !context.isEmpty()) {
            this.context.putAll(context);
        }
        return this;
    }
    public JQuickSQLRuntimeEnvironment with(String key, Object value) {
        this.context.put(key, value);
        return this;
    }
    public JQuickSQLRuntimeEnvironment withAll(JContext additionalContext) {
        if (null != additionalContext && !additionalContext.isEmpty()) {
            this.context.putAll(additionalContext);
        }
        return this;
    }
    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) this.context.get(key);
    }
    @SuppressWarnings("unchecked")
    public <T> T get(String key, T defaultValue) {
        T value = (T) this.context.get(key);
        return value != null ? value : defaultValue;
    }
    public String getString(String key) {
        Object value = this.context.get(key);
        return value != null ? value.toString() : null;
    }
    public String getString(String key, String defaultValue) {
        String value = getString(key);
        return value != null ? value : defaultValue;
    }
    public Integer getInt(String key) {
        Object value = this.context.get(key);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        if (value instanceof String) {
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
    public Boolean getBoolean(String key) {
        Object value = this.context.get(key);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        if (value instanceof String) {
            return Boolean.parseBoolean((String) value);
        }
        return null;
    }
    public boolean getBoolean(String key, boolean defaultValue) {
        Boolean value = getBoolean(key);
        return value != null ? value : defaultValue;
    }
    public boolean contains(String key) {
        return this.context.containsKey(key);
    }
    public JQuickSQLRuntimeEnvironment remove(String key) {
        this.context.remove(key);
        return this;
    }
    public JQuickSQLRuntimeEnvironment clear() {
        this.context.clear();
        return this;
    }
    public boolean isLocalProvider() {
        return "local".equalsIgnoreCase(abilityProvider);
    }
    public boolean isForkJoinProvider() {
        return "forkJoin".equalsIgnoreCase(abilityProvider);
    }
    public boolean isFlinkProvider() {
        return "flink".equalsIgnoreCase(abilityProvider);
    }
    public boolean isSparkProvider() {
        return "spark".equalsIgnoreCase(abilityProvider);
    }
    public void printInfo() {
        JConsole console = JConsole.initConsoleEnvironment();
        console.info("╔════════════════════════════════════════════════════════════════╗");
        console.info("║               JQuickSQLRuntimeEnvironment                      ║");
        console.info("║                     Detailed Info                              ║");
        console.info("╚════════════════════════════════════════════════════════════════╝");
        console.info("┌─────────────────── Basic Information ───────────────────┐");
        console.info("│ Ability Provider : " + abilityProvider);
        console.info("│ Context Size     : " + context.size());
        console.info("└──────────────────────────────────────────────────────────┘");
        if (console.getLevel().ordinal() <= JLogLevel.DEBUG.ordinal()) {
            console.info("┌─────────────────── Context Content ─────────────────────┐");
            if (context.isEmpty()) {
                console.info("│ (empty)                                             │");
            } else {
                for (Map.Entry<String, Object> entry : context.entrySet()) {
                    String key = entry.getKey();
                    String value = String.valueOf(entry.getValue());
                    if (value.length() > 40) {
                        value = value.substring(0, 37) + "...";
                    }
                    console.info("│ " + String.format("%-20s", key) + " : " + value + " │");
                }
            }
            console.info("└──────────────────────────────────────────────────────────┘");
        } else {
            console.info("Context Content: " + context);
        }
        console.info("┌─────────────────── System Information ──────────────────┐");
        console.info("│ Java Version     : " + System.getProperty("java.version"));
        console.info("│ Java Vendor      : " + System.getProperty("java.vendor"));
        console.info("│ OS Name          : " + System.getProperty("os.name"));
        console.info("│ OS Arch          : " + System.getProperty("os.arch"));
        console.info("│ User Dir         : " + System.getProperty("user.dir"));
        console.info("│ Available Cores  : " + Runtime.getRuntime().availableProcessors());
        console.info("└──────────────────────────────────────────────────────────┘");
        console.info("┌─────────────────── Memory Information ──────────────────┐");
        console.info("│ Max Memory       : " + Runtime.getRuntime().maxMemory() / 1024 / 1024 + " MB");
        console.info("│ Total Memory     : " + Runtime.getRuntime().totalMemory() / 1024 / 1024 + " MB");
        console.info("│ Free Memory      : " + Runtime.getRuntime().freeMemory() / 1024 / 1024 + " MB");
        console.info("│ Used Memory      : " + (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024 + " MB");
        console.info("└──────────────────────────────────────────────────────────┘");
    }

    @Override
    public String toString() {
        return "JQuickJavaRuntimeEnvironment{" +
                "abilityProvider='" + abilityProvider + '\'' +
                ", context=" + context +
                '}';
    }
    public static JQuickSQLRuntimeEnvironment local(Map<String, JQuickDataSet> datasetMap ,JQuickClientConfig clientConfig) {
        return new JQuickSQLRuntimeEnvironment("local",clientConfig,datasetMap);
    }
    public static JQuickSQLRuntimeEnvironment forkJoin(Map<String, JQuickDataSet> datasetMap ,JQuickClientConfig clientConfig) {
        return new JQuickSQLRuntimeEnvironment("forkJoin",clientConfig,datasetMap);
    }

    public static JQuickSQLRuntimeEnvironment flink(Map<String, JQuickDataSet> datasetMap ,JQuickClientConfig clientConfig) {
        return new JQuickSQLRuntimeEnvironment("flink",clientConfig,datasetMap);
    }
    public static JQuickSQLRuntimeEnvironment spark(Map<String, JQuickDataSet> datasetMap ,JQuickClientConfig clientConfig) {
        return new JQuickSQLRuntimeEnvironment("spark",clientConfig,datasetMap);
    }
    public static JQuickSQLRuntimeEnvironment custom(String provider,Map<String, JQuickDataSet> datasetMap ,JQuickClientConfig clientConfig) {
        return new JQuickSQLRuntimeEnvironment(provider,clientConfig,datasetMap);
    }
}
