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
import com.github.paohaijiao.dataset.DataSet;
import com.github.paohaijiao.dataset.Row;
import com.github.paohaijiao.enums.JSortDirection;
import com.github.paohaijiao.enums.JoinType;
import com.github.paohaijiao.evalue.JExpressionEvaluator;
import com.github.paohaijiao.exception.JAssert;
import com.github.paohaijiao.expression.JColumnExpression;
import com.github.paohaijiao.expression.JExpression;
import com.github.paohaijiao.expression.JFunctionCallExpression;
import com.github.paohaijiao.expression.JOrderByExpression;
import com.github.paohaijiao.factory.JDataSetJoinerFactory;
import com.github.paohaijiao.factory.JDataSetJoinerStrategy;
import com.github.paohaijiao.function.JAggregateFunctionFactory;
import com.github.paohaijiao.join.JoinCondition;
import com.github.paohaijiao.model.JLimitModel;
import com.github.paohaijiao.model.JSelectElementModel;
import com.github.paohaijiao.model.JSelectElementsResultModel;
import com.github.paohaijiao.model.JoinPartModel;
import com.github.paohaijiao.parser.JQuickSQLParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * packageName com.github.paohaijiao.visitor
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/8/11
 */
public class JQuikSQLSelectStatementVisitor extends JQuikSQLFilterStatementVisitor{
    @Override
    public DataSet visitSelectExpression(JQuickSQLParser.SelectExpressionContext ctx) {
        String text=ctx.getText();
        if(ctx.datasetOp()!=null){
            return visitDatasetOp(ctx.datasetOp());
        }
        if(ctx.olapOperation()!=null){
            Object value= visitOlapOperation(ctx.olapOperation());
            JAssert.isTrue(value instanceof DataSet,"the value of olapOperation is not a DataSet");
            return (DataSet)value;
        }
        JAssert.throwNewException("not support this statement");
        return null;
    }
    @Override
    public DataSet visitDatasetOp(JQuickSQLParser.DatasetOpContext ctx) {
        JAssert.notNull(ctx.selectClause(),"selectClause must not be null");
        if(ctx.selectClause().size()==1){
            return visitSelectClause(ctx.selectClause().get(0));
        }
        JAssert.isTrue(ctx.selectClause().size()==2,"selectClause must have 2 elements");
        DataSet dataSetOne=visitSelectClause(ctx.selectClause().get(0));
        DataSet dataSetTwo=visitSelectClause(ctx.selectClause().get(1));
        JDataSetJoinerStrategy strategy= JDataSetJoinerFactory.createJoiner(engine);
        if(null!=ctx.UNION()){
            DataSet  DataSet=   strategy.union(dataSetOne,dataSetTwo);
            return DataSet;
        }
        if(null!=ctx.MINUS()){
            DataSet  DataSet=   strategy.minus(dataSetOne,dataSetTwo);
            return DataSet;
        }
        if(null!=ctx.INTERSECT()){
            DataSet  DataSet=   strategy.intersect(dataSetOne,dataSetTwo);
            return DataSet;
        }
        JAssert.throwNewException(" this statement only accepts union or intersect or minus statements");
        return null;
    }

    @Override
    public DataSet visitSelectClause(JQuickSQLParser.SelectClauseContext ctx) {
        JDataSetJoinerStrategy strategy= JDataSetJoinerFactory.createJoiner(engine);
        JAssert.notNull(ctx.fromClause()," the from dataset require not null");
        DataSet dataSet=null;
        if(ctx.fromClause()!=null){
            dataSet=visitFromClause(ctx.fromClause());
        }
        JAssert.notNull(dataSet," the from dataset require not null");
        if (ctx.joinClause() != null && !ctx.joinClause().isEmpty()) {
            for (int i = 0; i < ctx.joinClause().size(); i++) {
                JoinPartModel joinPartModel= visitJoinClause(ctx.joinClause().get(i));
                JoinType joinType=joinPartModel.getJoinType();
                JAssert.notNull(joinType,"the join type require not null");
                DataSet rightDataSet=joinPartModel.getDataset();
                JoinCondition condition=null;
                if(null!=dataSet.getRows()&&null!=joinPartModel.getLeft()){
                        String leftColumn = joinPartModel.getLeft().getColumnName();
                        String rightColumn =joinPartModel.getRight().getColumnName();
                        condition = JoinCondition.equals(leftColumn, rightColumn);
                }
                if(joinType==JoinType.INNER){
                    dataSet= strategy.innerJoin(dataSet,rightDataSet,condition);
                }else if(joinType==JoinType.LEFT){
                    dataSet=  strategy.leftJoin(dataSet,rightDataSet,condition);
                }else if(joinType==JoinType.RIGHT){
                    dataSet=  strategy.rightJoin(dataSet ,rightDataSet,condition);
                }else if(joinType==JoinType.CROSS){
                    JAssert.isNull(joinPartModel.getRight()," cross join require on condition is empty");
                    JAssert.isNull(joinPartModel.getLeft()," cross join require on condition is empty");
                    dataSet=  strategy.crossJoin(dataSet,rightDataSet);
                }else if(joinType==JoinType.FULL){
                    dataSet=  strategy.fullOuterJoin(dataSet,rightDataSet,condition);
                }else if(joinType==JoinType.NATURAL){
                    JAssert.isNull(joinPartModel.getRight()," natural join require on condition is empty");
                    JAssert.isNull(joinPartModel.getLeft()," natural join require on condition is empty");
                    dataSet=  strategy.naturalJoin(dataSet,rightDataSet);
                }
            }
        }
        if(ctx.whereClause()!=null){
            JCondition condition = visitWhereClause(ctx.whereClause());
            dataSet=strategy.filter(dataSet,condition);
        }
        JSelectElementsResultModel   selectElementsResultModel=null;
        if(ctx.selectElements()!=null){
            selectElementsResultModel=visitSelectElements(ctx.selectElements());
        }
        if(selectElementsResultModel!=null&&!selectElementsResultModel.hasStar()){
            JAssert.notNull(selectElementsResultModel," the select elements require not null");
            if (!selectElementsResultModel.hasStar()) {
                if(ctx.groupByClause()!=null&&selectElementsResultModel.hasAggregateFunction()){
                    List<JExpression>  groupByClauses= visitGroupByClause(ctx.groupByClause());
                    JAssert.isTrue(selectElementsResultModel.hasAggregateFunction(),"groupBy clause must have aggregateFunction");
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
                    dataSet=strategy.aggregate(dataSet,groupByField,aggregations);
                    if(ctx.havingClause()!=null){
                        JCondition condition = visitHavingClause(ctx.havingClause());
                        dataSet=strategy.filter(dataSet,condition);
                    }
                }
            }
            if(!selectElementsResultModel.getNonAggregateFunction().isEmpty()){
                JExpressionEvaluator expressionEvaluator=new JExpressionEvaluator();
                for (int i = 0; i < selectElementsResultModel.getNonAggregateFunction().size(); i++) {
                    for (int j=0;j<dataSet.size();j++){
                        JSelectElementModel selectElementModel= selectElementsResultModel.getNonAggregateFunction().get(i);
                        Row row=dataSet.getRows().get(j);
                        Object value= expressionEvaluator.evaluate(selectElementModel.getExpression(),row);
                        String column=null;
                        JExpression expression=selectElementModel.getExpression();
                        if(expression instanceof JColumnExpression){
                            column=((JColumnExpression)expression).getColumnName();
                        }
                        if(selectElementModel.getAlias()!=null){
                            column=selectElementModel.getAlias();
                        }
                        row.put(column,value);
                    }
                }
            }
        }

        if(ctx.orderByClause()!=null){
            List<JOrderByExpression>  orderByExpressions= visitOrderByClause(ctx.orderByClause());
            dataSet=strategy.sort(dataSet,orderByExpressions);
        }
        if(ctx.limitClause()!=null){
            JLimitModel limitModel= visitLimitClause(ctx.limitClause());
            dataSet=strategy.limit(dataSet,limitModel.getLimit(),limitModel.getOffset());
        }

        return dataSet;
    }

    @Override
    public JoinPartModel visitJoinClause(JQuickSQLParser.JoinClauseContext ctx) {
        JoinPartModel joinPartModel = new JoinPartModel();
        DataSet dataset = (DataSet) visit(ctx.tableNameItem());
        joinPartModel.setDataset(dataset);
        joinPartModel.setJoinType(visitJoinType(ctx.joinType()));
        if(ctx.fullColumnName().size() == 2){
           joinPartModel.setLeft(visitFullColumnName(ctx.fullColumnName(0)));
           joinPartModel.setRight(visitFullColumnName(ctx.fullColumnName(1)));
        }
        return joinPartModel;
    }

    @Override
    public List<JExpression> visitGroupByClause(JQuickSQLParser.GroupByClauseContext ctx) {
        JAssert.notNull(ctx.expressions()," the expressions require not null");
        return visitExpressions(ctx.expressions());
    }
    @Override
    public JCondition visitHavingClause(JQuickSQLParser.HavingClauseContext ctx) {
        JAssert.notNull(ctx.filterCondition()," the expressions require not null");
        return visitFilterCondition(ctx.filterCondition());
    }


    @Override
    public DataSet visitFromClause(JQuickSQLParser.FromClauseContext ctx) {
        if (ctx.tableNameItem() != null) {
            return visitTableNameItem(ctx.tableNameItem());
        }
        JAssert.throwNewException("the from dataset require not null");
        return null;
    }
    @Override
    public DataSet visitTableNameItem(JQuickSQLParser.TableNameItemContext ctx) {
        DataSet dataSet = null;
        String tableName = null;
        if (ctx.tableNameSpec() != null) {
            tableName = ctx.tableNameSpec().getText();
            dataSet = dataSetHolder.getDataSet(tableName);
        }
        JAssert.notNull(tableName, " the tableName require not null");
        JAssert.notNull(dataSet, " the table dataset require not null");
        if (ctx.uid() != null) {
            String alias= ctx.uid().getText();
            dataSetHolder.addAlias(tableName, alias);
//            dataSet.setTableName(tableName);
//            dataSet.setAlias(alias);
        }
        return dataSet;
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
        JAssert.notNull(ctx.filterCondition()," the expressions require not null");
        return (JCondition)visitFilterCondition(ctx.filterCondition());
    }

}
