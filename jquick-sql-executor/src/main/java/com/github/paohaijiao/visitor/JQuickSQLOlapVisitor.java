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

import com.github.paohaijiao.condition.JComparisonCondition;
import com.github.paohaijiao.dataset.JDataSet;
import com.github.paohaijiao.evalue.JOLAPExpressionEvaluator;
import com.github.paohaijiao.exception.JAssert;
import com.github.paohaijiao.expression.JBinaryExpression;
import com.github.paohaijiao.expression.JColumnExpression;
import com.github.paohaijiao.expression.JExpression;
import com.github.paohaijiao.expression.JFunctionCallExpression;
import com.github.paohaijiao.expression.olap.*;
import com.github.paohaijiao.function.JAggregateFunctionFactory;
import com.github.paohaijiao.model.JSelectElementModel;
import com.github.paohaijiao.model.JSelectElementsResultModel;
import com.github.paohaijiao.parser.JQuickSQLParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * packageName com.github.paohaijiao.visitor
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/8/17
 */
public class JQuickSQLOlapVisitor extends JQuikSQLCommonTableExpressionVisitor{
    @Override
    public JDataSet visitOlapOperation(JQuickSQLParser.OlapOperationContext ctx){
        JDataSet dataset=null;
        if(ctx.fromClause() != null){
            dataset=visitFromClause(ctx.fromClause());
        }
        JAssert.notNull(dataset,"the dataset require not null");
        JSelectElementsResultModel selectElementsResultModel=null;
        if(ctx.selectElements()!=null){
            selectElementsResultModel=visitSelectElements(ctx.selectElements());
        }
        JOLAPExpressionEvaluator evaluator = new JOLAPExpressionEvaluator();
        List<JExpression> list=new ArrayList<>();
        if(ctx.olapClauseItem() != null&&!ctx.olapClauseItem().isEmpty()){
            for (int i = 0; i < ctx.olapClauseItem().size(); i++) {
               Object exp= visit(ctx.olapClauseItem().get(i));
               JAssert.isTrue(exp instanceof JExpression,"the expression should be an expression");
               list.add((JExpression)exp);
               evaluator.setDataset(dataset);
                if(exp instanceof JRollUpExpression){
                    JAssert.notNull(selectElementsResultModel.getAggregateFunction(),"the aggregateFunction function should be set");
                    Map<String, Function<List<Object>, Object>> aggregations=new HashMap<>();
                    for (int j = 0; j < selectElementsResultModel.getAggregateFunction().size(); j++) {
                        JSelectElementModel selectElementModel= selectElementsResultModel.getAggregateFunction().get(j);
                        JExpression columnExpression=selectElementModel.getExpression();
                        String funcName= selectElementModel.getFunctionName();
                        JAssert.isTrue(JAggregateFunctionFactory.containsFunction(funcName),"the aggregateFunction not exist ");;
                        JAssert.isTrue(columnExpression instanceof JFunctionCallExpression,"the column expression should be a funcationcall");
                        JFunctionCallExpression functionCallExpression=(JFunctionCallExpression)columnExpression;
                        JAssert.isTrue(functionCallExpression.getArguments().size()==1,"the functioncall expression should be at length 1");
                        JExpression columnExpress=functionCallExpression.getArguments().get(0);
                        JAssert.isTrue(columnExpress instanceof JColumnExpression,"the aggregate param type should be a column");
                        JColumnExpression col=(JColumnExpression)columnExpress;
                        aggregations.put(col.getColumnName(), JAggregateFunctionFactory.getFunction(funcName));
                    }
                    JRollUpExpression rollUp=(JRollUpExpression)exp;
                    JRollUpExpression rollUpExpression =new JRollUpExpression(rollUp.getGroupByColumns(),aggregations);
                    dataset= evaluator.executeOLAPOperation(rollUpExpression);
                }
                if(exp instanceof JDrillDownExpression){
                    JAssert.notNull(selectElementsResultModel.getAggregateFunction(),"the aggregateFunction function should be set");
                    Map<String, Function<List<Object>, Object>> aggregations=new HashMap<>();
                    for (int j = 0; j < selectElementsResultModel.getAggregateFunction().size(); j++) {
                        JSelectElementModel selectElementModel= selectElementsResultModel.getAggregateFunction().get(j);
                        JExpression columnExpression=selectElementModel.getExpression();
                        String funcName= selectElementModel.getFunctionName();
                        JAssert.isTrue(JAggregateFunctionFactory.containsFunction(funcName),"the aggregateFunction not exist ");;
                        JAssert.isTrue(columnExpression instanceof JFunctionCallExpression,"the column expression should be a funcationcall");
                        JFunctionCallExpression functionCallExpression=(JFunctionCallExpression)columnExpression;
                        JAssert.isTrue(functionCallExpression.getArguments().size()==1,"the functioncall expression should be at length 1");
                        JExpression columnExpress=functionCallExpression.getArguments().get(0);
                        JAssert.isTrue(columnExpress instanceof JColumnExpression,"the aggregate param type should be a column");
                        JColumnExpression col=(JColumnExpression)columnExpress;
                        aggregations.put(col.getColumnName(), JAggregateFunctionFactory.getFunction(funcName));
                    }
                    JDrillDownExpression drillDownExpression=(JDrillDownExpression)exp;
                    JDrillDownExpression drillDown =new JDrillDownExpression(drillDownExpression.getGroupByColumns(),aggregations);
                    dataset= evaluator.executeOLAPOperation(drillDown);
                }
                if(exp instanceof JPivotExpression){
                    JAssert.notNull(selectElementsResultModel.getAggregateFunction(),"the aggregateFunction function should be set");
                    Map<String, Function<List<Object>, Object>> aggregations=new HashMap<>();
                    for (int j = 0; j < selectElementsResultModel.getAggregateFunction().size(); j++) {
                        JSelectElementModel selectElementModel= selectElementsResultModel.getAggregateFunction().get(j);
                        JExpression columnExpression=selectElementModel.getExpression();
                        String funcName= selectElementModel.getFunctionName();
                        JAssert.isTrue(JAggregateFunctionFactory.containsFunction(funcName),"the aggregateFunction not exist ");;
                        JAssert.isTrue(columnExpression instanceof JFunctionCallExpression,"the column expression should be a funcationcall");
                        JFunctionCallExpression functionCallExpression=(JFunctionCallExpression)columnExpression;
                        JAssert.isTrue(functionCallExpression.getArguments().size()==1,"the functioncall expression should be at length 1");
                        JExpression columnExpress=functionCallExpression.getArguments().get(0);
                        JAssert.isTrue(columnExpress instanceof JColumnExpression,"the aggregate param type should be a column");
                        JColumnExpression col=(JColumnExpression)columnExpress;
                        aggregations.put(col.getColumnName(), JAggregateFunctionFactory.getFunction(funcName));
                    }
                    JPivotExpression pivotExpression=(JPivotExpression)exp;
                    dataset= evaluator.executeOLAPOperation(pivotExpression);
                }
                if(exp instanceof JSliceExpression){
                    JSliceExpression expression=(JSliceExpression)exp;
                    dataset= evaluator.executeOLAPOperation(expression);
                }
                if(exp instanceof JDiceExpression){
                    JDiceExpression expression=(JDiceExpression)exp;
                    dataset= evaluator.executeOLAPOperation(expression);
                }
            }
        }
        return dataset;
    }
    @Override
    public JRollUpExpression visitRollupOperation(JQuickSQLParser.RollupOperationContext ctx) {
        List<String> groupByColumns = new ArrayList<>();
        Map<String, Function<List<Object>, Object>> aggregations = new HashMap<>();
        JQuickSQLParser.RollupDimensionsContext dimensionsCtx = ctx.rollupDimensions();
        for (JQuickSQLParser.ExpressionContext exprCtx : dimensionsCtx.expression()) {
            JExpression expression = (JExpression) visit(exprCtx);
            if (expression instanceof JColumnExpression) {
                JColumnExpression colExpr = (JColumnExpression) expression;
                groupByColumns.add(colExpr.getColumnName());
            } else if(expression instanceof JFunctionCallExpression){
                JFunctionCallExpression colExpr = (JFunctionCallExpression) expression;
                JAssert.isTrue(JAggregateFunctionFactory.containsFunction( colExpr.getFunctionName()),"the aggregate function does not exist");
                Function<List<Object>, Object> function=JAggregateFunctionFactory.getFunction(colExpr.getFunctionName());
                aggregations.put( colExpr.getFunctionName(), function);
            }else{
                JAssert.throwNewException("only support Column and AggregateFunction expressions");
            }
        }
        return new JRollUpExpression(groupByColumns, aggregations);
    }
    @Override
    public JDrillDownExpression visitDrilldownOperation(JQuickSQLParser.DrilldownOperationContext ctx) {
        List<String> groupByColumns = new ArrayList<>();
        Map<String, Function<List<Object>, Object>> aggregations = new HashMap<>();
        JQuickSQLParser.DrilldownDimensionsContext dimensionsCtx = ctx.drilldownDimensions();
        for (JQuickSQLParser.ExpressionContext exprCtx : dimensionsCtx.expression()) {
            JExpression expression = (JExpression) visit(exprCtx);
            if (expression instanceof JColumnExpression) {
                JColumnExpression colExpr = (JColumnExpression) expression;
                groupByColumns.add(colExpr.getColumnName());
            } else if(expression instanceof JFunctionCallExpression){
                JFunctionCallExpression colExpr = (JFunctionCallExpression) expression;
                JAssert.isTrue(JAggregateFunctionFactory.containsFunction( colExpr.getFunctionName()),"the aggregate function does not exist");
                Function<List<Object>, Object> function=JAggregateFunctionFactory.getFunction(colExpr.getFunctionName());
                aggregations.put( colExpr.getFunctionName(), function);
            }else{
                JAssert.throwNewException("only support Column and AggregateFunction expressions");
            }
        }
        return new JDrillDownExpression(groupByColumns, aggregations);
    }
    @Override
    public JSliceExpression visitSliceOperation(JQuickSQLParser.SliceOperationContext ctx) {
        JQuickSQLParser.SliceConditionContext sliceCtx = ctx.sliceCondition();
        String dimension = sliceCtx.uid().getText();
        Object object=visit(sliceCtx.expression());
        JAssert.isTrue(object instanceof JExpression,"the slice value should be JExpression");
        JExpression valueExpression = (JExpression) object;
        return new JSliceExpression(dimension, valueExpression);
    }
    @Override
    public JDiceExpression visitDiceOperation(JQuickSQLParser.DiceOperationContext ctx) {
        Map<JExpression, JExpression> conditions = new HashMap<>();
        JQuickSQLParser.DiceConditionsContext diceCtx = ctx.diceConditions();
        for (JQuickSQLParser.DiceConditionContext conditionCtx : diceCtx.diceCondition()) {
            Object predicate = visit(conditionCtx.predicate());
            JComparisonCondition comparisonCondition = (JComparisonCondition) predicate;
            JAssert.isTrue("=".equals(comparisonCondition.getOperator().getSymbol()),"the operator must be =");
            JExpression leftExpression=comparisonCondition.getLeft();
            JExpression rightExpression=comparisonCondition.getRight();
            JAssert.isTrue(leftExpression instanceof JColumnExpression,"the left expression should be JColumnExpression");
            conditions.put(leftExpression,rightExpression);
        }
        return new JDiceExpression(conditions);
    }

    @Override
    public JPivotExpression visitPivotOperation(JQuickSQLParser.PivotOperationContext ctx) {
        JQuickSQLParser.PivotSpecContext pivotCtx = ctx.pivotSpec();
        JAssert.isTrue(pivotCtx.uid().size()==3,"the pivot expression should have two uids");
        String pivotColumn = pivotCtx.uid(0).getText();
        String valueColumn = pivotCtx.uid(1).getText();
        String aggregator = pivotCtx.uid(2).getText();
        JAssert.isTrue(JAggregateFunctionFactory.containsFunction(aggregator),"the function does not exist");;
        Function<List<Object>, Object> function=JAggregateFunctionFactory.getFunction(aggregator);
        return new JPivotExpression(pivotColumn, valueColumn, function);
    }







}
