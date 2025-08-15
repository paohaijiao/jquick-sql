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
package com.github.paohaijiao.visitor;

import com.github.paohaijiao.dataset.JColumnMeta;
import com.github.paohaijiao.dataset.JDataSet;
import com.github.paohaijiao.dataset.JRow;
import com.github.paohaijiao.engine.JEntityQueryEngine;
import com.github.paohaijiao.exception.JAssert;
import com.github.paohaijiao.func.JoinCondition;
import com.github.paohaijiao.param.JContext;
import com.github.paohaijiao.parser.JQuickSQLBaseVisitor;
import com.github.paohaijiao.parser.JQuickSQLLexer;
import com.github.paohaijiao.parser.JQuickSQLParser;
import com.sun.org.apache.xalan.internal.extensions.ExpressionContext;
import org.antlr.v4.runtime.CommonTokenStream;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * packageName com.github.paohaijiao.visitor
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/8/11
 */
public class JQuikSQLCoreVisitor extends JQuickSQLBaseVisitor {

    protected JContext context;

    protected JQuickSQLLexer lexer;

    protected CommonTokenStream tokenStream;

    protected JQuickSQLParser parser;

    protected final Map<String, JDataSet> tableRegistry = new HashMap<>();

    protected JDataSet currentDataset;

    public void registerDataSet(String tableName, JDataSet dataSet) {
        tableRegistry.put(tableName, dataSet);
    }
    protected JDataSet aliasColumns(JDataSet dataset, String alias) {
        List<JColumnMeta> newColumns = new ArrayList<>();
        List<JRow> newRows = new ArrayList<>();
        for (JColumnMeta column : dataset.getColumns()) {
            newColumns.add(new JColumnMeta(
                    alias + "." + column.getName(),
                    column.getType(),
                    column.getSource()
            ));
        }
        for (JRow row : dataset.getRows()) {
            JRow newRow = new JRow();
            for (String key : row.keySet()) {
                newRow.put(alias + "." + key, row.get(key));
            }
            newRows.add(newRow);
        }
        return new JDataSet(newColumns, newRows);
    }


    public static String trim(String str) {
        if(null==str || "".equals(str)) {
            return str;
        }
        String newStr = str.replaceAll("^['\"]|['\"]$", "");
        return newStr;
    }
    protected Number getNumber(String number){
        NumberFormat format = NumberFormat.getInstance();
        Number num = null;
        try {
            num = format.parse(number);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return num;
    }
    protected boolean convertToBoolean(Object value) {
        if (value instanceof Boolean) {
            return (Boolean) value;
        } else {
            JAssert.throwNewException("Cannot convert to boolean: " + value);
            return false;
        }
    }
}