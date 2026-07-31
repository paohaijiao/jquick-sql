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


import com.github.paohaijiao.domain.JQuickTable;
import com.github.paohaijiao.engine.JQuickSQL;
import com.github.paohaijiao.exception.JAssert;
import com.github.paohaijiao.param.JContext;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;
import com.github.paohaijiao.xml.invocation.JQuickXmlInvocationHandler;
import com.github.paohaijiao.xml.util.ParamUtil;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class JQuickSQLXmlInvocationHandler extends JQuickXmlInvocationHandler {

    private static JQuickSQL sql;

    private JContext context=new JContext();


    public JQuickSQLXmlInvocationHandler(List<JQuickTable> tables){
        sql = JQuickSQL.embedded();
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
                }
            }
        }
        throw new UnsupportedOperationException("Unsupported return type: " + returnType + ". Expected JQuickDataSet or List<JQuickRow>");
    }

}

