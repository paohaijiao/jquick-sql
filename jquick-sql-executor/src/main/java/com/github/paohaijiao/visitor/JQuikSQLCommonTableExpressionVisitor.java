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

import com.github.paohaijiao.parser.JQuickSQLParser;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.support.JQuickSqlDataSetRecursiveQuery;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * packageName com.github.paohaijiao.visitor
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/8/11
 */
public class JQuikSQLCommonTableExpressionVisitor extends JQuikSQLSelectStatementVisitor {

    private Map<String, JQuickDataSet> cteCache = new HashMap<>();

    @Override
    public JQuickDataSet visitCteQuery(JQuickSQLParser.CteQueryContext ctx) {
        parseCteDefinitions(ctx);
        return visitSelectExpression(ctx.selectExpression());
    }

    private void parseCteDefinitions(JQuickSQLParser.CteQueryContext ctx) {
        for (JQuickSQLParser.CommonTableExpressionContext cteCtx : ctx.commonTableExpression()) {
            String cteName = cteCtx.uid().getText();
            JQuickDataSet cteDataSet;
            if (cteCtx.selectStatement() != null) {
                cteDataSet = (JQuickDataSet) visit(cteCtx.selectStatement());
            } else {
                cteDataSet = processRecursiveCte(cteCtx, ctx.RECURSIVE() != null);
            }
            cteCache.put(cteName, cteDataSet);
            dataSetHolder.addDataSet(cteName, cteDataSet);
        }
    }

    private JQuickDataSet processRecursiveCte(JQuickSQLParser.CommonTableExpressionContext cteCtx, boolean isRecursive) {
        JQuickDataSet initialDataSet = (JQuickDataSet) visit(cteCtx.initialQuery());
        Function<JQuickDataSet, JQuickDataSet> recursiveFunction = buildRecursiveFunction(cteCtx.recursivePart());
        return JQuickSqlDataSetRecursiveQuery.withRecursive(
                initialDataSet,
                recursiveFunction,
                100,
                true
        );
    }

    private Function<JQuickDataSet, JQuickDataSet> buildRecursiveFunction(JQuickSQLParser.RecursivePartContext ctx) {
        return currentDataSet -> {
            String tempRecursiveTable = "__current_recursive__";
            dataSetHolder.addDataSet(tempRecursiveTable, currentDataSet);
            try {
                return (JQuickDataSet) visit(ctx.selectStatement());
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
