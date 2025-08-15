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

import com.github.paohaijiao.dataset.JDataSet;
import com.github.paohaijiao.exception.JAssert;
import com.github.paohaijiao.func.JoinCondition;
import com.github.paohaijiao.param.JContext;
import com.github.paohaijiao.parser.JQuickSQLLexer;
import com.github.paohaijiao.parser.JQuickSQLParser;
import com.github.paohaijiao.support.JDataSetJoiner;
import com.sun.org.apache.xalan.internal.extensions.ExpressionContext;
import org.antlr.v4.runtime.CommonTokenStream;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * packageName com.github.paohaijiao.visitor
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/8/11
 */
public class JQuikSQLSelectStatementVisitor extends JQuikSQLCoreVisitor{

    @Override
    public JDataSet visitSelectClause(JQuickSQLParser.SelectClauseContext ctx) {
        JAssert.notNull(ctx.fromClause()," the from dataset require not null");
        JDataSet jDataSet=null;
        if(ctx.fromClause()!=null){
            jDataSet=visitFromClause(ctx.fromClause());
        }

        return jDataSet;
    }
    @Override
    public JDataSet visitFromClause(JQuickSQLParser.FromClauseContext ctx) {
        if(ctx.tableSources()!=null){
           return visitTableSources(ctx.tableSources());
        }
        JAssert.throwNewException("the from dataset require not null");
        return null;
    }
    @Override
    public JDataSet visitTableSources(JQuickSQLParser.TableSourcesContext ctx) {
        JDataSet result = null;
        for (JQuickSQLParser.TableSourceContext tableSourceCtx : ctx.tableSource()) {
            JDataSet current = visitTableSource(tableSourceCtx);
            if (result == null) {
                result = current;
            } else {
                result = JDataSetJoiner.crossJoin(result, current);
            }
        }
        return result;
    }
    @Override
    public JDataSet visitTableSource(JQuickSQLParser.TableSourceContext ctx) {
        currentDataset =(JDataSet) visit(ctx.tableSourceItem());
        for (JQuickSQLParser.JoinPartContext joinPartCtx : ctx.joinPart()) {
            visitJoinPart(joinPartCtx);
        }
        return currentDataset;
    }
    @Override
    public JDataSet visitJoinPart(JQuickSQLParser.JoinPartContext ctx) {
        JDataSet leftDataset = this.currentDataset;
        JDataSet rightDataset =(JDataSet) visit(ctx.tableSourceItem());
        JoinCondition condition = (l, r) -> true;
        if (ctx.ON() != null) {
            condition = createJoinCondition(ctx.expression(), leftDataset, rightDataset);
        }

        if (ctx.NATURAL() != null) {
            this.currentDataset = JDataSetJoiner.naturalJoin(leftDataset, rightDataset);
        } else if (ctx.LEFT() != null) {
            this.currentDataset = JDataSetJoiner.leftJoin(leftDataset, rightDataset, condition);
        } else if (ctx.RIGHT() != null) {
            // RIGHT JOIN 转换为 LEFT JOIN
            this.currentDataset = JDataSetJoiner.leftJoin(rightDataset, leftDataset, (r, l) -> condition.test(l, r));
//        } else if (ctx.FULL() != null) {
//            this.currentDataset = JDataSetJoiner.fullOuterJoin(leftDataset, rightDataset, condition);
//        }
        } else {
            // 默认INNER JOIN
            this.currentDataset = JDataSetJoiner.innerJoin(leftDataset, rightDataset, condition);
        }

    }

    @Override
    public JDataSet visitTableSourceItem(JQuickSQLParser.TableSourceItemContext ctx) {
        if (ctx.tableName() != null) {
            String tableName = ctx.tableName().getText();
            String alias = ctx.uid() != null ? ctx.uid().getText() : tableName;
            JDataSet dataset = tableRegistry.getOrDefault(tableName, tableRegistry.getOrDefault(tableName, new JDataSet(Collections.emptyList(), Collections.emptyList())));
            return aliasColumns(dataset, alias);
        } else if (ctx.selectStatement() != null) {
            JDataSet subqueryResult = (JDataSet)visit(ctx.selectStatement());
            String alias = ctx.uid().getText();
            return aliasColumns(subqueryResult, alias);
        } else if (ctx.tableSources() != null) {
            return (JDataSet)visit(ctx.tableSources());
        }
        throw new UnsupportedOperationException("Unsupported table source item");
    }








}
