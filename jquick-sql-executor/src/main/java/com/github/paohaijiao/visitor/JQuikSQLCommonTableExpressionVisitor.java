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

import com.github.paohaijiao.dataset.DataSet;
import com.github.paohaijiao.parser.JQuickSQLParser;
import com.github.paohaijiao.support.JDataSetRecursiveQuery;


import java.util.*;
import java.util.function.Function;

/**
 * packageName com.github.paohaijiao.visitor
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/8/11
 */
public class JQuikSQLCommonTableExpressionVisitor extends JQuikSQLSelectStatementVisitor {

    private Map<String, DataSet> cteCache = new HashMap<>();
    @Override
    public DataSet visitCteQuery(JQuickSQLParser.CteQueryContext ctx) {
        parseCteDefinitions(ctx);
        return visitSelectExpression(ctx.selectExpression());
    }

    private void parseCteDefinitions(JQuickSQLParser.CteQueryContext ctx) {
        for (JQuickSQLParser.CommonTableExpressionContext cteCtx : ctx.commonTableExpression()) {
            String cteName = cteCtx.uid().getText();
            DataSet cteDataSet;
            if (cteCtx.selectStatement() != null) {
                cteDataSet =(DataSet) visit(cteCtx.selectStatement());
            } else {
                cteDataSet = processRecursiveCte(cteCtx, ctx.RECURSIVE() != null);
            }
            cteCache.put(cteName, cteDataSet);
            dataSetHolder.addDataSet(cteName, cteDataSet);
        }
    }

    private DataSet processRecursiveCte(JQuickSQLParser.CommonTableExpressionContext cteCtx, boolean isRecursive) {
        DataSet initialDataSet = (DataSet)visit(cteCtx.initialQuery());
        Function<DataSet, DataSet> recursiveFunction = buildRecursiveFunction(cteCtx.recursivePart());
        return JDataSetRecursiveQuery.withRecursive(
                initialDataSet,
                recursiveFunction,
                100,
                true
        );
    }

    private Function<DataSet, DataSet> buildRecursiveFunction(JQuickSQLParser.RecursivePartContext ctx) {
        return currentDataSet -> {
            String tempRecursiveTable = "__current_recursive__";
            dataSetHolder.addDataSet(tempRecursiveTable, currentDataSet);
            try {
                return (DataSet)visit(ctx.selectStatement());
            } finally {
                dataSetHolder.removeDataSet(tempRecursiveTable);
            }
        };
    }

    public void cleanup() {
        cteCache.keySet().forEach(dataSetHolder::removeDataSet);
        cteCache.clear();
    }


}
