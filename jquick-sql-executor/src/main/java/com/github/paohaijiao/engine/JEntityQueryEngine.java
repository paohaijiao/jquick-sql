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
package com.github.paohaijiao.engine;

import com.github.paohaijiao.handler.JQueryHandler;
import com.github.paohaijiao.handler.JQueryHandlerFactory;
import com.github.paohaijiao.plan.JExecutionPlan;

import javax.sql.DataSource;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * packageName com.github.paohaijiao.engine
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/8/12
 */
public class JEntityQueryEngine<T>  {

    private final Class<T> entityClass;

    private final JQueryHandlerFactory<T> handlerFactory;

    private final Map<String, List<?>> tableRegistry = new HashMap<>();

    private final Map<String, Class<?>> entityClassRegistry = new HashMap<>();

    public JEntityQueryEngine(Class<T> entityClass) {
        this.entityClass = entityClass;
        this.handlerFactory = new JEntityQueryEngineFactory<>(entityClass);
    }

    public List<T> executeQuery(String sql, List<T> dataset) {
        JExecutionPlan plan = parseSql(sql);
        List<JQueryHandler<T>> handlers = handlerFactory.createExecutionChain(this,plan);
        List<T> result = dataset;
        for (JQueryHandler<T> handler : handlers) {
            result = handler.handle(result, plan);
        }
        return result;
    }

    private JExecutionPlan parseSql(String sql) {
        return new JExecutionPlan();
    }
    public JEntityQueryEngine<T> registerJdbcTable(String tableName, DataSource dataSource) {
        return this;
    }
    public JEntityQueryEngine<T> registerCsvTable(String tableName, Path csvFile) {
        return this;
    }

    public <E> JEntityQueryEngine<T> registerEntityList(Class<E> entityClass, List<E> data) {
        String tableName = entityClass.getSimpleName();
        return registerEntityList(tableName, entityClass, data);
    }
    public <E> JEntityQueryEngine<T> registerEntityList(String tableName, Class<E> entityClass, List<E> data) {
        Objects.requireNonNull(tableName, "table name cannot be null");
        Objects.requireNonNull(entityClass, "entity class cannot be null");
        Objects.requireNonNull(data, "data list cannot be null");
        if (tableName.trim().isEmpty()) {
            throw new IllegalArgumentException("table name cannot be empty");
        }
        tableRegistry.put(tableName, data);
        entityClassRegistry.put(tableName, entityClass);
        return this;
    }

    public List<?> getTableData(String tableName) {
        if (!tableRegistry.containsKey(tableName)) {
            throw new IllegalArgumentException("table not registered: " + tableName);
        }
        return tableRegistry.get(tableName);
    }

    public Class<?> getEntityClass(String tableName) {
        if (!entityClassRegistry.containsKey(tableName)) {
            throw new IllegalArgumentException("table not registered: " + tableName);
        }
        return entityClassRegistry.get(tableName);
    }

}
