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
import com.github.paohaijiao.exception.JAssert;
import com.github.paohaijiao.xml.element.JQuickXmlElement;
import com.github.paohaijiao.xml.handler.JQuickParseHandler;
import com.github.paohaijiao.xml.invocation.JQuickXmlInvocationHandler;

import java.util.List;


public class JQuickJavaXmlParseFactory implements JQuickParseHandler {

    private List<JQuickTable> tables;

    public JQuickJavaXmlParseFactory(List<JQuickTable> tables){
        JAssert.notNull(tables,"tables require not be null");
        JAssert.notEmptyCol(tables,"tables require not be empty ");
        this.tables=tables;
    }

    @Override
    public JQuickXmlElement createJQuickXmlElement() {
        return new JQuickSQLXmlElement();
    }

    @Override
    public JQuickXmlInvocationHandler createlInvocationHandler() {
        return new JQuickSQLXmlInvocationHandler(tables);
    }
}
