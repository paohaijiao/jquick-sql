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
package com.github.paohaijiao.util;

import com.github.paohaijiao.expression.JColumnExpression;
import com.github.paohaijiao.expression.JExpression;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * packageName com.github.paohaijiao.util
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/8/12
 */
public class JEntityAccessor<T>{

    private final Class<T> entityClass;

    private final Map<String, Field> fieldCache = new ConcurrentHashMap<>();

    private final Map<String, Method> getterCache = new ConcurrentHashMap<>();

    private final Map<String, Method> setterCache = new ConcurrentHashMap<>();

    public JEntityAccessor(Class<T> entityClass) {
        this.entityClass = entityClass;
        initializeCache();
    }
    public List<String> getFieldNames() {
        return fieldCache.values().stream().map(field -> field.getName()).collect(Collectors.toList());
    }
    private void initializeCache() {
        Arrays.stream(entityClass.getDeclaredFields())
                .forEach(field -> {
                    field.setAccessible(true);
                    fieldCache.put(field.getName(), field);
                });
        Arrays.stream(entityClass.getMethods())
                .forEach(method -> {
                    String methodName = method.getName();
                    if (methodName.startsWith("get") && method.getParameterCount() == 0) {
                        String fieldName = uncapitalize(methodName.substring(3));
                        getterCache.put(fieldName, method);
                    } else if (methodName.startsWith("is") && method.getParameterCount() == 0
                            && (method.getReturnType() == boolean.class || method.getReturnType() == Boolean.class)) {
                        String fieldName = uncapitalize(methodName.substring(2));
                        getterCache.put(fieldName, method);
                    } else if (methodName.startsWith("set") && method.getParameterCount() == 1) {
                        String fieldName = uncapitalize(methodName.substring(3));
                        setterCache.put(fieldName, method);
                    }
                });
    }

    public Object getValue(T entity, String fieldName) {
        try {
            Method getter = getterCache.get(fieldName);
            if (getter != null) {
                return getter.invoke(entity);
            }

            Field field = fieldCache.get(fieldName);
            if (field != null) {
                return field.get(entity);
            }
            throw new IllegalArgumentException("Field or getter not found: " + fieldName);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get field value: " + fieldName, e);
        }
    }

    /**
     * 获取属性值（通过表达式）
     */
    public Object getValue(T entity, JExpression expression) {
        if (expression instanceof JColumnExpression) {
            return getValue(entity, ((JColumnExpression) expression).getColumnName());
        }
        throw new UnsupportedOperationException("Unsupported expression type: " + expression.getClass());
    }

    public void setValue(T entity, String fieldName, Object value) {
        try {
            Method setter = setterCache.get(fieldName);
            if (setter != null) {
                setter.invoke(entity, value);
                return;
            }
            Field field = fieldCache.get(fieldName);
            if (field != null) {
                field.set(entity, value);
                return;
            }
            throw new IllegalArgumentException("Field or setter not found: " + fieldName);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set field value: " + fieldName, e);
        }
    }

    public boolean hasField(String fieldName) {
        return fieldCache.containsKey(fieldName) || getterCache.containsKey(fieldName);
    }

    private String uncapitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }
}
