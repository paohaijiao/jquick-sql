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
import com.github.paohaijiao.xml.element.JQuickXmlElement;
import com.github.paohaijiao.xml.handler.JQuickParseHandler;
import com.github.paohaijiao.xml.invocation.JQuickXmlInvocationHandler;


public class JQuickJavaXmlParseFactory implements JQuickParseHandler {

    private JQuickSQLRuntimeEnvironment runtimeEnvironment;

    public JQuickJavaXmlParseFactory(){

    }
    public JQuickJavaXmlParseFactory(JQuickSQLRuntimeEnvironment environment){
        JAssert.notNull(environment,"environment require not be null");
        JAssert.notNull(environment.getAbilityProvider(),"provider require not be null");
        JAssert.notNull(environment.getDataSet(),"dataset require not be null");
        JAssert.notNull(environment.getClientConfig(),"client require not be null");
        this.runtimeEnvironment=environment;
    }

    @Override
    public JQuickXmlElement createJQuickXmlElement() {
        return new JQuickSQLXmlElement();
    }

    @Override
    public JQuickXmlInvocationHandler createlInvocationHandler() {
        return new JQuickSQLXmlInvocationHandler(this.runtimeEnvironment);
    }
}
