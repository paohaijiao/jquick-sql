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
import com.github.paohaijiao.enums.JoinType;
import com.github.paohaijiao.exception.JAssert;
import com.github.paohaijiao.factory.JDataSetJoinerFactory;
import com.github.paohaijiao.factory.JDataSetJoinerStrategy;
import com.github.paohaijiao.func.JoinCondition;
import com.github.paohaijiao.lamda.JLamdaJoinJoinerHandler;
import com.github.paohaijiao.model.JLimitModel;
import com.github.paohaijiao.model.JoinPartModel;
import com.github.paohaijiao.parser.JQuickSQLParser;

import java.util.Collections;

/**
 * packageName com.github.paohaijiao.visitor
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/8/11
 */
public class JQuikSQLSelectStatementVisitor extends JQuikSQLFunctionStatementVisitor{

    @Override
    public JDataSet visitSelectClause(JQuickSQLParser.SelectClauseContext ctx) {
        JDataSetJoinerStrategy strategy= JDataSetJoinerFactory.createJoiner(engine);
        JAssert.notNull(ctx.fromClause()," the from dataset require not null");
        JDataSet jDataSet=null;
        if(ctx.fromClause()!=null){
            jDataSet=visitFromClause(ctx.fromClause());
        }
        JAssert.notNull(jDataSet," the from dataset require not null");
        if(ctx.limitClause()!=null){
            JLimitModel limitModel= visitLimitClause(ctx.limitClause());
            jDataSet=strategy.limit(jDataSet,limitModel.getLimit(),limitModel.getOffset());
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
        JAssert.isFalse(ctx.tableSource().isEmpty(),"the table sources require not empty");
        JDataSet leftDataset = visitTableSource(ctx.tableSource(0));
        if (ctx.tableSource().size() == 1) {
            return leftDataset;
        }
        for (int i = 1; i < ctx.tableSource().size(); i++) {
            JDataSet rightDataset = visitTableSource(ctx.tableSource(i));
        }
        return null;
    }
    @Override
    public JDataSet visitTableSource(JQuickSQLParser.TableSourceContext ctx) {
        JDataSet dataSet =(JDataSet) visit(ctx.tableSourceItem());
        for (JQuickSQLParser.JoinPartContext joinPartCtx : ctx.joinPart()) {
            JoinPartModel joinPartModel= visitJoinPart(joinPartCtx);
            JoinType joinType=joinPartModel.getJoinType();
            JAssert.notNull(joinType,"the join type require not null");
            JDataSet rightDataSet=joinPartModel.getDataset();
            String leftColumn = joinPartModel.getLeft().getColumnName();
            String rightColumn =joinPartModel.getRight().getColumnName();
            JoinCondition condition = JoinCondition.equals(leftColumn, rightColumn);
            if(joinType==JoinType.INNER){
                joinerStrategy.innerJoin(dataSet,rightDataSet,condition);
            }else if(joinType==JoinType.LEFT){
                joinerStrategy.leftJoin(dataSet,rightDataSet,condition);
            }else if(joinType==JoinType.RIGHT){
                JAssert.throwNewException("the join type require not supported");
            }else if(joinType==JoinType.CROSS){
                joinerStrategy.crossJoin(dataSet,rightDataSet);
            }else if(joinType==JoinType.FULL){
                joinerStrategy.fullOuterJoin(dataSet,rightDataSet,condition);
            }else if(joinType==JoinType.NATURAL){
                joinerStrategy.naturalJoin(dataSet,rightDataSet);
            }else if(joinType==JoinType.UNION){
                joinerStrategy.union(dataSet,rightDataSet);
            }else if(joinType==JoinType.INTERSECT){
                joinerStrategy.intersect(dataSet,rightDataSet);
            }else if(joinType==JoinType.MINUS){
                joinerStrategy.minus(dataSet,rightDataSet);
            }
        }
        return dataSet;
    }
    @Override
    public JoinPartModel visitJoinPart(JQuickSQLParser.JoinPartContext ctx) {
        JoinPartModel joinPartModel=new JoinPartModel();
        JDataSet dataset =(JDataSet) visit(ctx.tableSourceItem());
        joinPartModel.setDataset(dataset);
        joinPartModel.setJoinType(visitJoinType(ctx.joinType()));
        JAssert.isTrue(ctx.fullColumnName().size()==2,"the full column name require 2 columns");
        joinPartModel.setLeft(visitFullColumnName(ctx.fullColumnName(0)));
        joinPartModel.setRight(visitFullColumnName(ctx.fullColumnName(1)));
        return joinPartModel;

    }
    @Override
    public JoinType visitJoinType(JQuickSQLParser.JoinTypeContext ctx) {
        if(ctx.INNER()!=null){
            return JoinType.INNER;
        } else if (ctx.CROSS()!=null) {
            return JoinType.CROSS;
        }else if (ctx.LEFT()!=null) {
            return JoinType.LEFT;
        }else if (ctx.RIGHT()!=null) {
            return JoinType.RIGHT;
        }else if (ctx.NATURAL()!=null) {
            return JoinType.NATURAL;
        }else if (ctx.FULL()!=null) {
            return JoinType.FULL;
        }else if (ctx.NATURAL()!=null) {
            return JoinType.NATURAL;
        }
        JAssert.throwNewException("the join type require not null");
        return null;
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
    @Override
    public JLimitModel visitLimitClause(JQuickSQLParser.LimitClauseContext ctx) {
        JLimitModel jLimitModel=new JLimitModel();
        if(ctx.limitWithOffset()!=null){
            JAssert.notNull(ctx.limitWithOffset().limit,"the limit required");
            JAssert.notNull(ctx.limitWithOffset().offset,"the offset required");
            String limit=ctx.limitWithOffset().limit.getText();
            String offset=ctx.limitWithOffset().offset.getText();
            jLimitModel.setLimit(Integer.parseInt(limit));
            jLimitModel.setOffset(Integer.parseInt(offset));
            return jLimitModel;
        }
        if(ctx.limitOnly()!=null){
            JAssert.notNull(ctx.limitOnly().limit,"the limit required");
            String limit=ctx.limitOnly().limit.getText();
            jLimitModel.setLimit(Integer.parseInt(limit));
            return jLimitModel;
        }
        return null;

    }

}
