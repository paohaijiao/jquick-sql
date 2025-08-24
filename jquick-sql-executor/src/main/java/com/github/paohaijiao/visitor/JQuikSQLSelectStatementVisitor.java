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

import com.github.paohaijiao.condition.JCondition;
import com.github.paohaijiao.dataset.JDataSet;
import com.github.paohaijiao.enums.JSortDirection;
import com.github.paohaijiao.enums.JoinType;
import com.github.paohaijiao.exception.JAssert;
import com.github.paohaijiao.expression.JColumnExpression;
import com.github.paohaijiao.expression.JExpression;
import com.github.paohaijiao.expression.JFunctionCallExpression;
import com.github.paohaijiao.expression.JOrderByExpression;
import com.github.paohaijiao.factory.JDataSetJoinerFactory;
import com.github.paohaijiao.factory.JDataSetJoinerStrategy;
import com.github.paohaijiao.join.JoinCondition;
import com.github.paohaijiao.function.JAggregateFunctionFactory;
import com.github.paohaijiao.model.JLimitModel;
import com.github.paohaijiao.model.JSelectElementModel;
import com.github.paohaijiao.model.JSelectElementsResultModel;
import com.github.paohaijiao.model.JoinPartModel;
import com.github.paohaijiao.parser.JQuickSQLParser;

import java.util.*;

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
        if(ctx.whereClause()!=null){
            JCondition condition = visitWhereClause(ctx.whereClause());
            jDataSet=strategy.filter(jDataSet,condition);
        }
        JSelectElementsResultModel selectElementsResultModel=null;
        if(ctx.selectElements()!=null){
            selectElementsResultModel=visitSelectElements(ctx.selectElements());
        }
        JAssert.notNull(selectElementsResultModel," the select elements require not null");
        if(ctx.groupByClause()!=null){
            JAssert.isTrue(selectElementsResultModel.hasAggregateFunction(),"groupBy clause must have aggregateFunction");
            List<JExpression>  groupByClauses= visitGroupByClause(ctx.groupByClause());
            Map<String, JFunctionCallExpression> aggregations = new HashMap<>();
            List<JSelectElementModel> aggreateFunction=selectElementsResultModel.getAggregateFunction();
            aggreateFunction.forEach(e->{
                JFunctionCallExpression aggregateFunction=(JFunctionCallExpression)e.getExpression();
                List<JExpression> list=aggregateFunction.getArguments();
                JAssert.isTrue(!list.isEmpty(),"the aggregate function must have arguments");
                JColumnExpression columnExpression=(JColumnExpression)list.get(0);
                JAssert.notNull(e.getAlias()," the aggregate [ "+aggregateFunction.getFunctionName()+"("+columnExpression.getColumnName()+")"+" ] function must own the alias column ");
                aggregations.put(e.getAlias(), aggregateFunction);
            });
            List<String> groupByField=new ArrayList<>();
            groupByClauses.forEach(e->{
                if(e instanceof JColumnExpression){
                    groupByField.add(((JColumnExpression)e).getColumnName());
                }else{
                    JAssert.throwNewException("the groupBy clause must have column expression");
                }
            });
            jDataSet=strategy.aggregate(jDataSet,groupByField,aggregations);
            if(ctx.havingClause()!=null){
                List<JExpression> expressionList = visitHavingClause(ctx.havingClause());
                for (JExpression expression:expressionList){
                    JCondition condition= convertExpressionToCondition(expression);
                    jDataSet=strategy.filter(jDataSet,condition);
                }
            }
        }

        if(ctx.orderByClause()!=null){
            List<JOrderByExpression>  orderByExpressions= visitOrderByClause(ctx.orderByClause());
            jDataSet=strategy.sort(jDataSet,orderByExpressions);
        }
        if(ctx.limitClause()!=null){
            JLimitModel limitModel= visitLimitClause(ctx.limitClause());
            jDataSet=strategy.limit(jDataSet,limitModel.getLimit(),limitModel.getOffset());
        }
        return jDataSet;
    }
    @Override
    public List<JExpression> visitGroupByClause(JQuickSQLParser.GroupByClauseContext ctx) {
        JAssert.notNull(ctx.expressions()," the expressions require not null");
        return visitExpressions(ctx.expressions());
    }
    @Override
    public List<JExpression> visitHavingClause(JQuickSQLParser.HavingClauseContext ctx) {
        JAssert.notNull(ctx.expressions()," the expressions require not null");
        return visitExpressions(ctx.expressions());
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
    @Override
    public List<JOrderByExpression> visitOrderByClause(JQuickSQLParser.OrderByClauseContext ctx) {
        List<JOrderByExpression> list=new ArrayList<>();
        for (int i = 0; i < ctx.orderByExpression().size(); i++) {
            list.add(visitOrderByExpression(ctx.orderByExpression().get(i)));
        }
        return list;
    }
    @Override
    public JOrderByExpression visitOrderByExpression(JQuickSQLParser.OrderByExpressionContext ctx) {
        JSortDirection sortDirection=JSortDirection.ASC;
        if(ctx.ASC()!=null){
            sortDirection=JSortDirection.ASC;
        }
        if(ctx.DESC()!=null){
            sortDirection=JSortDirection.DESC;
        }
        JExpression jExpression=null;
        if(ctx.expression()!=null){
            jExpression=(JExpression)visit(ctx.expression());
        }
        JAssert.notNull(jExpression,"the expression required");
        JOrderByExpression orderByExpression=new JOrderByExpression(jExpression,sortDirection);
        return orderByExpression;
    }
    @Override
    public JSelectElementsResultModel visitSelectElements(JQuickSQLParser.SelectElementsContext ctx) {
        boolean hasStar = ctx.star != null;
        List<JSelectElementModel> elements = new ArrayList<>();
        if (hasStar) {
            elements.add(new JSelectElementModel(null, "*", false, null));
        } else {
            if (ctx.selectElement() != null) {
                for (JQuickSQLParser.SelectElementContext elemCtx : ctx.selectElement()) {
                    JSelectElementModel element = visitSelectElement(elemCtx);
                    elements.add(element);
                }
            }
        }
        return new JSelectElementsResultModel(hasStar, elements);

    }
    @Override
    public JSelectElementModel visitSelectElement(JQuickSQLParser.SelectElementContext ctx) {
        JExpression expression =(JExpression) visit(ctx.expression());
        String alias = null;
        if (ctx.uid() != null) {
            alias = ctx.uid().getText();
        }
        boolean isAggregate = false;
        String aggregateFunctionName = null;
        if (expression instanceof JFunctionCallExpression) {
            JFunctionCallExpression funcExpr = (JFunctionCallExpression) expression;
            String functionName = funcExpr.getFunctionName().toUpperCase();
            if (JAggregateFunctionFactory.containsFunction(functionName)) {
                isAggregate = true;
                aggregateFunctionName = functionName;
            }
        }
        return new JSelectElementModel(expression, alias, isAggregate, aggregateFunctionName);
    }
    @Override
    public JCondition  visitWhereClause(JQuickSQLParser.WhereClauseContext ctx) {
        JAssert.notNull(ctx.expression()," the expressions require not null");
        return (JCondition)visit(ctx.expression());
    }

}
