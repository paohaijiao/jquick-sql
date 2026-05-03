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

import com.github.paohaijiao.environment.JQuickSQLRuntimeEnvironment;
import com.github.paohaijiao.exception.JAssert;
import com.github.paohaijiao.executor.JQuickSQLExecutor;
import com.github.paohaijiao.param.JContext;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.support.JQuickSqlDataSetHolder;
import com.github.paohaijiao.xml.invocation.JQuickXmlInvocationHandler;
import com.github.paohaijiao.xml.util.ParamUtil;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class JQuickSQLXmlInvocationHandler extends JQuickXmlInvocationHandler {

    private JQuickSQLRuntimeEnvironment runtimeEnvironment;

    private JContext context=new JContext();

    private Map<String, Object> extra=new HashMap<String, Object>();


    public JQuickSQLXmlInvocationHandler(){

    }
    public JQuickSQLXmlInvocationHandler(JQuickSQLRuntimeEnvironment environment){
        JAssert.notNull(environment,"environment require not be null");
        JAssert.notNull(environment.getAbilityProvider(),"provider require not be null");
        JAssert.notNull(environment.getClientConfig(),"client require not be null");
        JAssert.isFalse(environment.getDataSet().isEmpty(),"datasets require not be null");
        this.runtimeEnvironment=environment;
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
        JQuickSQLExecutor executor = new JQuickSQLExecutor(  this.runtimeEnvironment);
        JQuickDataSet dataSet = executor.execute(lexerStr);
        return dataSet;
    }

}

