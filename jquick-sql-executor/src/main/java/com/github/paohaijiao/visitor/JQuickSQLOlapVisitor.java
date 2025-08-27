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
import com.github.paohaijiao.evalue.JOLAPExpressionEvaluator;
import com.github.paohaijiao.exception.JAssert;
import com.github.paohaijiao.expression.JBinaryExpression;
import com.github.paohaijiao.expression.JColumnExpression;
import com.github.paohaijiao.expression.JExpression;
import com.github.paohaijiao.expression.JFunctionCallExpression;
import com.github.paohaijiao.expression.olap.*;
import com.github.paohaijiao.function.JAggregateFunctionFactory;
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
        if(ctx.selectClause() != null){
            dataset=visitSelectClause(ctx.selectClause());
        }
        JAssert.notNull(dataset,"the dataset require not null");
        if(ctx.olapClause() != null){
            List<JExpression> expressionList=visitOlapClause(ctx.olapClause());
            JOLAPExpressionEvaluator evaluator = new JOLAPExpressionEvaluator();
            for(JExpression expression:expressionList){
                evaluator.setDataset(dataset);
                dataset= evaluator.executeOLAPOperation(expression);
            }
        }
        return dataset;
    }
    @Override
    public List<JExpression> visitOlapClause(JQuickSQLParser.OlapClauseContext ctx) {
        List<JExpression> list=new ArrayList<JExpression>();
        for (JQuickSQLParser.OlapClauseItemContext item:ctx.olapClauseItem()){
            Object value= this.visit(item);
            JAssert.notNull(value,"the value is null");
            JExpression expression= (JExpression) value;
            list.add(expression);
        }
        return list;
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
            JAssert.isTrue(predicate instanceof JBinaryExpression,"the predicate should be instanceof JBinaryExpression");
            JBinaryExpression binaryExpression = (JBinaryExpression) predicate;
            JAssert.isTrue("=".equals(binaryExpression.getOperator().getSymbol()),"the operator must be =");
            JExpression leftExpression=binaryExpression.getLeft();
            JAssert.isTrue(leftExpression instanceof JColumnExpression,"the left expression should be JColumnExpression");
        }
        return new JDiceExpression(conditions);
    }

    @Override
    public JPivotExpression visitPivotOperation(JQuickSQLParser.PivotOperationContext ctx) {
        JQuickSQLParser.PivotSpecContext pivotCtx = ctx.pivotSpec();
        JAssert.isTrue(pivotCtx.uid().size()==2,"the pivot expression should have two uids");
        String pivotColumn = pivotCtx.uid(0).getText();
        String valueColumn = pivotCtx.uid(1).getText();
        if (pivotCtx.functionCall() != null) {
            JQuickSQLParser.FunctionCallContext funcCtx = pivotCtx.functionCall();
            String functionName = funcCtx.uid().getText();
            JAssert.isTrue(JAggregateFunctionFactory.containsFunction(functionName),"the function does not exist");;
            Function<List<Object>, Object> function=JAggregateFunctionFactory.getFunction(functionName);
            return new JPivotExpression(pivotColumn, valueColumn, function);
        }
        return new JPivotExpression(pivotColumn, valueColumn, null);
    }







}
