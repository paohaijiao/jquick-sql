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
package com.github.paohaijiao.xml;


import com.github.paohaijiao.config.JQuickSqlConfig;
import com.github.paohaijiao.exception.JAssert;
import com.github.paohaijiao.param.JContext;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;
import com.github.paohaijiao.xml.invocation.JQuickXmlInvocationHandler;
import com.github.paohaijiao.xml.util.ParamUtil;
import com.github.paohaijiao.domain.JQuickTable;
import com.github.paohaijiao.engine.JQuickSQL;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JQuickSQLXmlInvocationHandler extends JQuickXmlInvocationHandler {

    private final JQuickSQL sql;

    private JContext context=new JContext();


    public JQuickSQLXmlInvocationHandler(List<JQuickTable> tables, JQuickSqlConfig config){
        if (null!=config){
             sql = JQuickSQL.embedded().withConfig(config);
        }else{
            sql = JQuickSQL.embedded();
        }
        JAssert.notNull(tables,"tables require not be null");
        JAssert.notEmptyCol(tables,"tables require not be empty");
        tables.forEach(t->{
            sql.registerTable(t.getTableName(), t.getColumns(), t.getRows());
        });
    }

    @Override
    protected Object loadResult(String lexerStr, JContext jcontext, Method method, Object[] args) {
        if(null!=jcontext&&!jcontext.isEmpty()){
            context.putAll(jcontext);
        }
        ParamUtil paramUtil=new ParamUtil();
        Map<String,Object> map=paramUtil.bindParams(method, args);
        if(!map.isEmpty()){
            context.putAll(map);
        }
        JQuickDataSet dataSet =sql.execute(lexerStr,context);
        Class<?> returnType = method.getReturnType();
        if (returnType.isAssignableFrom(JQuickDataSet.class)) {
            return dataSet;
        }
        if (returnType.isAssignableFrom(List.class)) {
            Type genericReturnType = method.getGenericReturnType();
            if (genericReturnType instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) genericReturnType;
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                if (actualTypeArguments.length > 0) {
                    Type rowType = actualTypeArguments[0];
                    if (rowType == JQuickRow.class) {
                        return dataSet.getRows();
                    }
                    if (rowType instanceof Class) {
                        return JQuickRow.toBeanList(restoreDateColumns(dataSet.getRows(), (Class<?>) rowType), (Class<?>) rowType);
                    }
                }
            }
        }
        throw new UnsupportedOperationException("Unsupported return type: " + returnType + ". Expected JQuickDataSet, List<JQuickRow> or List<Bean>");
    }

    /**
     * 分布式执行时日期经 protobuf Timestamp 序列化，返回后为 Instant；
     * 目标 Bean 字段为 java.util.Date 时先还原，避免 toBeanList 类型转换失败。
     */
    private List<JQuickRow> restoreDateColumns(List<JQuickRow> rows, Class<?> beanType) {
        Set<String> dateFields = new HashSet<>();
        for (Field field : beanType.getDeclaredFields()) {
            if (Date.class.isAssignableFrom(field.getType())) {
                dateFields.add(field.getName());
            }
        }
        if (dateFields.isEmpty()) {
            return rows;
        }
        for (JQuickRow row : rows) {
            for (String field : dateFields) {
                Object value = row.get(field);
                if (value instanceof Instant) {
                    row.put(field, Date.from((Instant) value));
                }
            }
        }
        return rows;
    }

}

