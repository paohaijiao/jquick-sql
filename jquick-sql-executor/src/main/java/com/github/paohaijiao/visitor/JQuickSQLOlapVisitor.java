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

import com.github.paohaijiao.condition.JQuickSqlComparisonCondition;
import com.github.paohaijiao.dataset.DataSet;
import com.github.paohaijiao.evalue.JQuickSqlOLAPExpressionEvaluator;
import com.github.paohaijiao.exception.JAssert;
import com.github.paohaijiao.expression.JQuickSqlColumnExpression;
import com.github.paohaijiao.expression.JQuickSqlExpression;
import com.github.paohaijiao.expression.JQuickSqlFunctionCallExpression;
import com.github.paohaijiao.expression.olap.*;
import com.github.paohaijiao.function.JQuickSqlAggregateFunctionFactory;
import com.github.paohaijiao.model.JQuickSqlSelectElementModel;
import com.github.paohaijiao.model.JQuickSqlSelectElementsResultModel;
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
public class JQuickSQLOlapVisitor extends JQuikSQLCommonTableExpressionVisitor {
    @Override
    public DataSet visitOlapOperation(JQuickSQLParser.OlapOperationContext ctx) {
        DataSet dataset = null;
        if (ctx.fromClause() != null) {
            dataset = visitFromClause(ctx.fromClause());
        }
        JAssert.notNull(dataset, "the dataset require not null");
        JQuickSqlSelectElementsResultModel selectElementsResultModel = null;
        if (ctx.selectElements() != null) {
            selectElementsResultModel = visitSelectElements(ctx.selectElements());
        }
        JQuickSqlOLAPExpressionEvaluator evaluator = new JQuickSqlOLAPExpressionEvaluator();
        List<JQuickSqlExpression> list = new ArrayList<>();
        if (ctx.olapClauseItem() != null && !ctx.olapClauseItem().isEmpty()) {
            for (int i = 0; i < ctx.olapClauseItem().size(); i++) {
                Object exp = visit(ctx.olapClauseItem().get(i));
                JAssert.isTrue(exp instanceof JQuickSqlExpression, "the expression should be an expression");
                list.add((JQuickSqlExpression) exp);
                evaluator.setDataset(dataset);
                if (exp instanceof JQuickSqlRollUpExpression) {
                    JAssert.notNull(selectElementsResultModel.getAggregateFunction(), "the aggregateFunction function should be set");
                    Map<String, Function<List<Object>, Object>> aggregations = new HashMap<>();
                    for (int j = 0; j < selectElementsResultModel.getAggregateFunction().size(); j++) {
                        JQuickSqlSelectElementModel selectElementModel = selectElementsResultModel.getAggregateFunction().get(j);
                        JQuickSqlExpression columnExpression = selectElementModel.getExpression();
                        String funcName = selectElementModel.getFunctionName();
                        JAssert.isTrue(JQuickSqlAggregateFunctionFactory.containsFunction(funcName), "the aggregateFunction not exist ");
                        ;
                        JAssert.isTrue(columnExpression instanceof JQuickSqlFunctionCallExpression, "the column expression should be a funcationcall");
                        JQuickSqlFunctionCallExpression functionCallExpression = (JQuickSqlFunctionCallExpression) columnExpression;
                        JAssert.isTrue(functionCallExpression.getArguments().size() == 1, "the functioncall expression should be at length 1");
                        JQuickSqlExpression columnExpress = functionCallExpression.getArguments().get(0);
                        JAssert.isTrue(columnExpress instanceof JQuickSqlColumnExpression, "the aggregate param type should be a column");
                        JQuickSqlColumnExpression col = (JQuickSqlColumnExpression) columnExpress;
                        aggregations.put(col.getColumnName(), JQuickSqlAggregateFunctionFactory.getFunction(funcName));
                    }
                    JQuickSqlRollUpExpression rollUp = (JQuickSqlRollUpExpression) exp;
                    JQuickSqlRollUpExpression rollUpExpression = new JQuickSqlRollUpExpression(rollUp.getGroupByColumns(), aggregations);
                    dataset = evaluator.executeOLAPOperation(rollUpExpression);
                }
                if (exp instanceof JQuickSqlDrillDownExpression) {
                    JAssert.notNull(selectElementsResultModel.getAggregateFunction(), "the aggregateFunction function should be set");
                    Map<String, Function<List<Object>, Object>> aggregations = new HashMap<>();
                    for (int j = 0; j < selectElementsResultModel.getAggregateFunction().size(); j++) {
                        JQuickSqlSelectElementModel selectElementModel = selectElementsResultModel.getAggregateFunction().get(j);
                        JQuickSqlExpression columnExpression = selectElementModel.getExpression();
                        String funcName = selectElementModel.getFunctionName();
                        JAssert.isTrue(JQuickSqlAggregateFunctionFactory.containsFunction(funcName), "the aggregateFunction not exist ");
                        ;
                        JAssert.isTrue(columnExpression instanceof JQuickSqlFunctionCallExpression, "the column expression should be a funcationcall");
                        JQuickSqlFunctionCallExpression functionCallExpression = (JQuickSqlFunctionCallExpression) columnExpression;
                        JAssert.isTrue(functionCallExpression.getArguments().size() == 1, "the functioncall expression should be at length 1");
                        JQuickSqlExpression columnExpress = functionCallExpression.getArguments().get(0);
                        JAssert.isTrue(columnExpress instanceof JQuickSqlColumnExpression, "the aggregate param type should be a column");
                        JQuickSqlColumnExpression col = (JQuickSqlColumnExpression) columnExpress;
                        aggregations.put(col.getColumnName(), JQuickSqlAggregateFunctionFactory.getFunction(funcName));
                    }
                    JQuickSqlDrillDownExpression drillDownExpression = (JQuickSqlDrillDownExpression) exp;
                    JQuickSqlDrillDownExpression drillDown = new JQuickSqlDrillDownExpression(drillDownExpression.getGroupByColumns(), aggregations);
                    dataset = evaluator.executeOLAPOperation(drillDown);
                }
                if (exp instanceof JQuickSqlPivotExpression) {
                    JAssert.notNull(selectElementsResultModel.getAggregateFunction(), "the aggregateFunction function should be set");
                    Map<String, Function<List<Object>, Object>> aggregations = new HashMap<>();
                    for (int j = 0; j < selectElementsResultModel.getAggregateFunction().size(); j++) {
                        JQuickSqlSelectElementModel selectElementModel = selectElementsResultModel.getAggregateFunction().get(j);
                        JQuickSqlExpression columnExpression = selectElementModel.getExpression();
                        String funcName = selectElementModel.getFunctionName();
                        JAssert.isTrue(JQuickSqlAggregateFunctionFactory.containsFunction(funcName), "the aggregateFunction not exist ");
                        ;
                        JAssert.isTrue(columnExpression instanceof JQuickSqlFunctionCallExpression, "the column expression should be a funcationcall");
                        JQuickSqlFunctionCallExpression functionCallExpression = (JQuickSqlFunctionCallExpression) columnExpression;
                        JAssert.isTrue(functionCallExpression.getArguments().size() == 1, "the functioncall expression should be at length 1");
                        JQuickSqlExpression columnExpress = functionCallExpression.getArguments().get(0);
                        JAssert.isTrue(columnExpress instanceof JQuickSqlColumnExpression, "the aggregate param type should be a column");
                        JQuickSqlColumnExpression col = (JQuickSqlColumnExpression) columnExpress;
                        aggregations.put(col.getColumnName(), JQuickSqlAggregateFunctionFactory.getFunction(funcName));
                    }
                    JQuickSqlPivotExpression pivotExpression = (JQuickSqlPivotExpression) exp;
                    dataset = evaluator.executeOLAPOperation(pivotExpression);
                }
                if (exp instanceof JQuickSqlSliceExpression) {
                    JQuickSqlSliceExpression expression = (JQuickSqlSliceExpression) exp;
                    dataset = evaluator.executeOLAPOperation(expression);
                }
                if (exp instanceof JQuickSqlDiceExpression) {
                    JQuickSqlDiceExpression expression = (JQuickSqlDiceExpression) exp;
                    dataset = evaluator.executeOLAPOperation(expression);
                }
            }
        }
        return dataset;
    }

    @Override
    public JQuickSqlRollUpExpression visitRollupOperation(JQuickSQLParser.RollupOperationContext ctx) {
        List<String> groupByColumns = new ArrayList<>();
        Map<String, Function<List<Object>, Object>> aggregations = new HashMap<>();
        JQuickSQLParser.RollupDimensionsContext dimensionsCtx = ctx.rollupDimensions();
        for (JQuickSQLParser.ExpressionContext exprCtx : dimensionsCtx.expression()) {
            JQuickSqlExpression expression = (JQuickSqlExpression) visit(exprCtx);
            if (expression instanceof JQuickSqlColumnExpression) {
                JQuickSqlColumnExpression colExpr = (JQuickSqlColumnExpression) expression;
                groupByColumns.add(colExpr.getColumnName());
            } else if (expression instanceof JQuickSqlFunctionCallExpression) {
                JQuickSqlFunctionCallExpression colExpr = (JQuickSqlFunctionCallExpression) expression;
                JAssert.isTrue(JQuickSqlAggregateFunctionFactory.containsFunction(colExpr.getFunctionName()), "the aggregate function does not exist");
                Function<List<Object>, Object> function = JQuickSqlAggregateFunctionFactory.getFunction(colExpr.getFunctionName());
                aggregations.put(colExpr.getFunctionName(), function);
            } else {
                JAssert.throwNewException("only support Column and AggregateFunction expressions");
            }
        }
        return new JQuickSqlRollUpExpression(groupByColumns, aggregations);
    }

    @Override
    public JQuickSqlDrillDownExpression visitDrilldownOperation(JQuickSQLParser.DrilldownOperationContext ctx) {
        List<String> groupByColumns = new ArrayList<>();
        Map<String, Function<List<Object>, Object>> aggregations = new HashMap<>();
        JQuickSQLParser.DrilldownDimensionsContext dimensionsCtx = ctx.drilldownDimensions();
        for (JQuickSQLParser.ExpressionContext exprCtx : dimensionsCtx.expression()) {
            JQuickSqlExpression expression = (JQuickSqlExpression) visit(exprCtx);
            if (expression instanceof JQuickSqlColumnExpression) {
                JQuickSqlColumnExpression colExpr = (JQuickSqlColumnExpression) expression;
                groupByColumns.add(colExpr.getColumnName());
            } else if (expression instanceof JQuickSqlFunctionCallExpression) {
                JQuickSqlFunctionCallExpression colExpr = (JQuickSqlFunctionCallExpression) expression;
                JAssert.isTrue(JQuickSqlAggregateFunctionFactory.containsFunction(colExpr.getFunctionName()), "the aggregate function does not exist");
                Function<List<Object>, Object> function = JQuickSqlAggregateFunctionFactory.getFunction(colExpr.getFunctionName());
                aggregations.put(colExpr.getFunctionName(), function);
            } else {
                JAssert.throwNewException("only support Column and AggregateFunction expressions");
            }
        }
        return new JQuickSqlDrillDownExpression(groupByColumns, aggregations);
    }

    @Override
    public JQuickSqlSliceExpression visitSliceOperation(JQuickSQLParser.SliceOperationContext ctx) {
        JQuickSQLParser.SliceConditionContext sliceCtx = ctx.sliceCondition();
        String dimension = sliceCtx.uid().getText();
        Object object = visit(sliceCtx.expression());
        JAssert.isTrue(object instanceof JQuickSqlExpression, "the slice value should be JExpression");
        JQuickSqlExpression valueExpression = (JQuickSqlExpression) object;
        return new JQuickSqlSliceExpression(dimension, valueExpression);
    }

    @Override
    public JQuickSqlDiceExpression visitDiceOperation(JQuickSQLParser.DiceOperationContext ctx) {
        Map<JQuickSqlExpression, JQuickSqlExpression> conditions = new HashMap<>();
        JQuickSQLParser.DiceConditionsContext diceCtx = ctx.diceConditions();
        for (JQuickSQLParser.DiceConditionContext conditionCtx : diceCtx.diceCondition()) {
            Object predicate = visit(conditionCtx.predicate());
            JQuickSqlComparisonCondition comparisonCondition = (JQuickSqlComparisonCondition) predicate;
            JAssert.isTrue("=".equals(comparisonCondition.getOperator().getSymbol()), "the operator must be =");
            JQuickSqlExpression leftExpression = comparisonCondition.getLeft();
            JQuickSqlExpression rightExpression = comparisonCondition.getRight();
            JAssert.isTrue(leftExpression instanceof JQuickSqlColumnExpression, "the left expression should be JColumnExpression");
            conditions.put(leftExpression, rightExpression);
        }
        return new JQuickSqlDiceExpression(conditions);
    }

    @Override
    public JQuickSqlPivotExpression visitPivotOperation(JQuickSQLParser.PivotOperationContext ctx) {
        JQuickSQLParser.PivotSpecContext pivotCtx = ctx.pivotSpec();
        JAssert.isTrue(pivotCtx.uid().size() == 3, "the pivot expression should have two uids");
        String pivotColumn = pivotCtx.uid(0).getText();
        String valueColumn = pivotCtx.uid(1).getText();
        String aggregator = pivotCtx.uid(2).getText();
        JAssert.isTrue(JQuickSqlAggregateFunctionFactory.containsFunction(aggregator), "the function does not exist");
        ;
        Function<List<Object>, Object> function = JQuickSqlAggregateFunctionFactory.getFunction(aggregator);
        return new JQuickSqlPivotExpression(pivotColumn, valueColumn, function);
    }


}
