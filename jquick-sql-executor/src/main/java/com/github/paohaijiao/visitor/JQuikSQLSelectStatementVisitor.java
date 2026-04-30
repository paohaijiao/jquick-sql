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

import com.github.paohaijiao.condition.JQuickSqlCondition;
import com.github.paohaijiao.dataset.DataSet;
import com.github.paohaijiao.dataset.Row;
import com.github.paohaijiao.enums.JQuickSqlSortDirection;
import com.github.paohaijiao.enums.JQuickSqlJoinType;
import com.github.paohaijiao.evalue.JQuickSqlExpressionEvaluator;
import com.github.paohaijiao.exception.JAssert;
import com.github.paohaijiao.expression.JQuickSqlColumnExpression;
import com.github.paohaijiao.expression.JQuickSqlExpression;
import com.github.paohaijiao.expression.JQuickSqlFunctionCallExpression;
import com.github.paohaijiao.expression.JQuickSqlOrderByExpression;
import com.github.paohaijiao.factory.JQuickSqlDataSetJoinerFactory;
import com.github.paohaijiao.factory.JQuickSqlDataSetJoinerStrategy;
import com.github.paohaijiao.function.JQuickSqlAggregateFunctionFactory;
import com.github.paohaijiao.join.JQuickSqlJoinCondition;
import com.github.paohaijiao.model.JQuickSqlLimitModel;
import com.github.paohaijiao.model.JQuickSqlSelectElementModel;
import com.github.paohaijiao.model.JQuickSqlSelectElementsResultModel;
import com.github.paohaijiao.model.JQuickSqlJoinPartModel;
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
public class JQuikSQLSelectStatementVisitor extends JQuikSQLFilterStatementVisitor {
    @Override
    public DataSet visitSelectExpression(JQuickSQLParser.SelectExpressionContext ctx) {
        String text = ctx.getText();
        if (ctx.datasetOp() != null) {
            return visitDatasetOp(ctx.datasetOp());
        }
        if (ctx.olapOperation() != null) {
            Object value = visitOlapOperation(ctx.olapOperation());
            JAssert.isTrue(value instanceof DataSet, "the value of olapOperation is not a DataSet");
            return (DataSet) value;
        }
        JAssert.throwNewException("not support this statement");
        return null;
    }

    @Override
    public DataSet visitDatasetOp(JQuickSQLParser.DatasetOpContext ctx) {
        JAssert.notNull(ctx.selectClause(), "selectClause must not be null");
        if (ctx.selectClause().size() == 1) {
            return visitSelectClause(ctx.selectClause().get(0));
        }
        JAssert.isTrue(ctx.selectClause().size() == 2, "selectClause must have 2 elements");
        DataSet dataSetOne = visitSelectClause(ctx.selectClause().get(0));
        DataSet dataSetTwo = visitSelectClause(ctx.selectClause().get(1));
        JQuickSqlDataSetJoinerStrategy strategy = JQuickSqlDataSetJoinerFactory.createJoiner(engine);
        if (null != ctx.UNION()) {
            DataSet DataSet = strategy.union(dataSetOne, dataSetTwo);
            return DataSet;
        }
        if (null != ctx.MINUS()) {
            DataSet DataSet = strategy.minus(dataSetOne, dataSetTwo);
            return DataSet;
        }
        if (null != ctx.INTERSECT()) {
            DataSet DataSet = strategy.intersect(dataSetOne, dataSetTwo);
            return DataSet;
        }
        JAssert.throwNewException(" this statement only accepts union or intersect or minus statements");
        return null;
    }

    @Override
    public DataSet visitSelectClause(JQuickSQLParser.SelectClauseContext ctx) {
        JQuickSqlDataSetJoinerStrategy strategy = JQuickSqlDataSetJoinerFactory.createJoiner(engine);
        JAssert.notNull(ctx.fromClause(), " the from dataset require not null");
        DataSet dataSet = null;
        if (ctx.fromClause() != null) {
            dataSet = visitFromClause(ctx.fromClause());
        }
        JAssert.notNull(dataSet, " the from dataset require not null");
        if (ctx.joinClause() != null && !ctx.joinClause().isEmpty()) {
            for (int i = 0; i < ctx.joinClause().size(); i++) {
                JQuickSqlJoinPartModel joinPartModel = visitJoinClause(ctx.joinClause().get(i));
                JQuickSqlJoinType joinType = joinPartModel.getJoinType();
                JAssert.notNull(joinType, "the join type require not null");
                DataSet rightDataSet = joinPartModel.getDataset();
                JQuickSqlJoinCondition condition = null;
                if (null != dataSet.getRows() && null != joinPartModel.getLeft()) {
                    String leftColumn = joinPartModel.getLeft().getColumnName();
                    String rightColumn = joinPartModel.getRight().getColumnName();
                    condition = JQuickSqlJoinCondition.equals(leftColumn, rightColumn);
                }
                if (joinType == JQuickSqlJoinType.INNER) {
                    dataSet = strategy.innerJoin(dataSet, rightDataSet, condition);
                } else if (joinType == JQuickSqlJoinType.LEFT) {
                    dataSet = strategy.leftJoin(dataSet, rightDataSet, condition);
                } else if (joinType == JQuickSqlJoinType.RIGHT) {
                    dataSet = strategy.rightJoin(dataSet, rightDataSet, condition);
                } else if (joinType == JQuickSqlJoinType.CROSS) {
                    JAssert.isNull(joinPartModel.getRight(), " cross join require on condition is empty");
                    JAssert.isNull(joinPartModel.getLeft(), " cross join require on condition is empty");
                    dataSet = strategy.crossJoin(dataSet, rightDataSet);
                } else if (joinType == JQuickSqlJoinType.FULL) {
                    dataSet = strategy.fullOuterJoin(dataSet, rightDataSet, condition);
                } else if (joinType == JQuickSqlJoinType.NATURAL) {
                    JAssert.isNull(joinPartModel.getRight(), " natural join require on condition is empty");
                    JAssert.isNull(joinPartModel.getLeft(), " natural join require on condition is empty");
                    dataSet = strategy.naturalJoin(dataSet, rightDataSet);
                }
            }
        }
        if (ctx.whereClause() != null) {
            JQuickSqlCondition condition = visitWhereClause(ctx.whereClause());
            dataSet = strategy.filter(dataSet, condition);
        }
        JQuickSqlSelectElementsResultModel selectElementsResultModel = null;
        if (ctx.selectElements() != null) {
            selectElementsResultModel = visitSelectElements(ctx.selectElements());
        }
        if (selectElementsResultModel != null && !selectElementsResultModel.hasStar()) {
            JAssert.notNull(selectElementsResultModel, " the select elements require not null");
            if (!selectElementsResultModel.hasStar()) {
                if (ctx.groupByClause() != null && selectElementsResultModel.hasAggregateFunction()) {
                    List<JQuickSqlExpression> groupByClauses = visitGroupByClause(ctx.groupByClause());
                    JAssert.isTrue(selectElementsResultModel.hasAggregateFunction(), "groupBy clause must have aggregateFunction");
                    Map<String, JQuickSqlFunctionCallExpression> aggregations = new HashMap<>();
                    List<JQuickSqlSelectElementModel> aggreateFunction = selectElementsResultModel.getAggregateFunction();
                    aggreateFunction.forEach(e -> {
                        JQuickSqlFunctionCallExpression aggregateFunction = (JQuickSqlFunctionCallExpression) e.getExpression();
                        List<JQuickSqlExpression> list = aggregateFunction.getArguments();
                        JAssert.isTrue(!list.isEmpty(), "the aggregate function must have arguments");
                        JQuickSqlColumnExpression columnExpression = (JQuickSqlColumnExpression) list.get(0);
                        JAssert.notNull(e.getAlias(), " the aggregate [ " + aggregateFunction.getFunctionName() + "(" + columnExpression.getColumnName() + ")" + " ] function must own the alias column ");
                        aggregations.put(e.getAlias(), aggregateFunction);
                    });
                    List<String> groupByField = new ArrayList<>();
                    groupByClauses.forEach(e -> {
                        if (e instanceof JQuickSqlColumnExpression) {
                            groupByField.add(((JQuickSqlColumnExpression) e).getColumnName());
                        } else {
                            JAssert.throwNewException("the groupBy clause must have column expression");
                        }
                    });
                    dataSet = strategy.aggregate(dataSet, groupByField, aggregations);
                    if (ctx.havingClause() != null) {
                        JQuickSqlCondition condition = visitHavingClause(ctx.havingClause());
                        dataSet = strategy.filter(dataSet, condition);
                    }
                }
            }
            if (!selectElementsResultModel.getNonAggregateFunction().isEmpty()) {
                JQuickSqlExpressionEvaluator expressionEvaluator = new JQuickSqlExpressionEvaluator();
                for (int i = 0; i < selectElementsResultModel.getNonAggregateFunction().size(); i++) {
                    for (int j = 0; j < dataSet.size(); j++) {
                        JQuickSqlSelectElementModel selectElementModel = selectElementsResultModel.getNonAggregateFunction().get(i);
                        Row row = dataSet.getRows().get(j);
                        Object value = expressionEvaluator.evaluate(selectElementModel.getExpression(), row);
                        String column = null;
                        JQuickSqlExpression expression = selectElementModel.getExpression();
                        if (expression instanceof JQuickSqlColumnExpression) {
                            column = ((JQuickSqlColumnExpression) expression).getColumnName();
                        }
                        if (selectElementModel.getAlias() != null) {
                            column = selectElementModel.getAlias();
                        }
                        row.put(column, value);
                    }
                }
            }
        }

        if (ctx.orderByClause() != null) {
            List<JQuickSqlOrderByExpression> orderByExpressions = visitOrderByClause(ctx.orderByClause());
            dataSet = strategy.sort(dataSet, orderByExpressions);
        }
        if (ctx.limitClause() != null) {
            JQuickSqlLimitModel limitModel = visitLimitClause(ctx.limitClause());
            dataSet = strategy.limit(dataSet, limitModel.getLimit(), limitModel.getOffset());
        }

        return dataSet;
    }

    @Override
    public JQuickSqlJoinPartModel visitJoinClause(JQuickSQLParser.JoinClauseContext ctx) {
        JQuickSqlJoinPartModel joinPartModel = new JQuickSqlJoinPartModel();
        DataSet dataset = (DataSet) visit(ctx.tableNameItem());
        joinPartModel.setDataset(dataset);
        joinPartModel.setJoinType(visitJoinType(ctx.joinType()));
        if (ctx.fullColumnName().size() == 2) {
            joinPartModel.setLeft(visitFullColumnName(ctx.fullColumnName(0)));
            joinPartModel.setRight(visitFullColumnName(ctx.fullColumnName(1)));
        }
        return joinPartModel;
    }

    @Override
    public List<JQuickSqlExpression> visitGroupByClause(JQuickSQLParser.GroupByClauseContext ctx) {
        JAssert.notNull(ctx.expressions(), " the expressions require not null");
        return visitExpressions(ctx.expressions());
    }

    @Override
    public JQuickSqlCondition visitHavingClause(JQuickSQLParser.HavingClauseContext ctx) {
        JAssert.notNull(ctx.filterCondition(), " the expressions require not null");
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
            String alias = ctx.uid().getText();
            dataSetHolder.addAlias(tableName, alias);
//            dataSet.setTableName(tableName);
//            dataSet.setAlias(alias);
        }
        return dataSet;
    }


    @Override
    public JQuickSqlJoinType visitJoinType(JQuickSQLParser.JoinTypeContext ctx) {
        if (ctx.INNER() != null) {
            return JQuickSqlJoinType.INNER;
        } else if (ctx.CROSS() != null) {
            return JQuickSqlJoinType.CROSS;
        } else if (ctx.LEFT() != null) {
            return JQuickSqlJoinType.LEFT;
        } else if (ctx.RIGHT() != null) {
            return JQuickSqlJoinType.RIGHT;
        } else if (ctx.NATURAL() != null) {
            return JQuickSqlJoinType.NATURAL;
        } else if (ctx.FULL() != null) {
            return JQuickSqlJoinType.FULL;
        } else if (ctx.NATURAL() != null) {
            return JQuickSqlJoinType.NATURAL;
        }
        JAssert.throwNewException("the join type require not null");
        return null;
    }

    @Override
    public JQuickSqlLimitModel visitLimitClause(JQuickSQLParser.LimitClauseContext ctx) {
        JQuickSqlLimitModel jLimitModel = new JQuickSqlLimitModel();
        if (ctx.limitWithOffset() != null) {
            JAssert.notNull(ctx.limitWithOffset().limit, "the limit required");
            JAssert.notNull(ctx.limitWithOffset().offset, "the offset required");
            String limit = ctx.limitWithOffset().limit.getText();
            String offset = ctx.limitWithOffset().offset.getText();
            jLimitModel.setLimit(Integer.parseInt(limit));
            jLimitModel.setOffset(Integer.parseInt(offset));
            return jLimitModel;
        }
        if (ctx.limitOnly() != null) {
            JAssert.notNull(ctx.limitOnly().limit, "the limit required");
            String limit = ctx.limitOnly().limit.getText();
            jLimitModel.setLimit(Integer.parseInt(limit));
            return jLimitModel;
        }
        return null;
    }

    @Override
    public List<JQuickSqlOrderByExpression> visitOrderByClause(JQuickSQLParser.OrderByClauseContext ctx) {
        List<JQuickSqlOrderByExpression> list = new ArrayList<>();
        for (int i = 0; i < ctx.orderByExpression().size(); i++) {
            list.add(visitOrderByExpression(ctx.orderByExpression().get(i)));
        }
        return list;
    }

    @Override
    public JQuickSqlOrderByExpression visitOrderByExpression(JQuickSQLParser.OrderByExpressionContext ctx) {
        JQuickSqlSortDirection sortDirection = JQuickSqlSortDirection.ASC;
        if (ctx.ASC() != null) {
            sortDirection = JQuickSqlSortDirection.ASC;
        }
        if (ctx.DESC() != null) {
            sortDirection = JQuickSqlSortDirection.DESC;
        }
        JQuickSqlExpression jExpression = null;
        if (ctx.expression() != null) {
            jExpression = (JQuickSqlExpression) visit(ctx.expression());
        }
        JAssert.notNull(jExpression, "the expression required");
        JQuickSqlOrderByExpression orderByExpression = new JQuickSqlOrderByExpression(jExpression, sortDirection);
        return orderByExpression;
    }

    @Override
    public JQuickSqlSelectElementsResultModel visitSelectElements(JQuickSQLParser.SelectElementsContext ctx) {
        boolean hasStar = ctx.star != null;
        List<JQuickSqlSelectElementModel> elements = new ArrayList<>();
        if (hasStar) {
            elements.add(new JQuickSqlSelectElementModel(null, "*", false, null));
        } else {
            if (ctx.selectElement() != null) {
                for (JQuickSQLParser.SelectElementContext elemCtx : ctx.selectElement()) {
                    JQuickSqlSelectElementModel element = visitSelectElement(elemCtx);
                    elements.add(element);
                }
            }
        }
        return new JQuickSqlSelectElementsResultModel(hasStar, elements);

    }

    @Override
    public JQuickSqlSelectElementModel visitSelectElement(JQuickSQLParser.SelectElementContext ctx) {
        JQuickSqlExpression expression = (JQuickSqlExpression) visit(ctx.expression());
        String alias = null;
        if (ctx.uid() != null) {
            alias = ctx.uid().getText();
        }
        boolean isAggregate = false;
        String aggregateFunctionName = null;
        if (expression instanceof JQuickSqlFunctionCallExpression) {
            JQuickSqlFunctionCallExpression funcExpr = (JQuickSqlFunctionCallExpression) expression;
            String functionName = funcExpr.getFunctionName().toUpperCase();
            if (JQuickSqlAggregateFunctionFactory.containsFunction(functionName)) {
                isAggregate = true;
                aggregateFunctionName = functionName;
            }
        }
        return new JQuickSqlSelectElementModel(expression, alias, isAggregate, aggregateFunctionName);
    }

    @Override
    public JQuickSqlCondition visitWhereClause(JQuickSQLParser.WhereClauseContext ctx) {
        JAssert.notNull(ctx.filterCondition(), " the expressions require not null");
        return (JQuickSqlCondition) visitFilterCondition(ctx.filterCondition());
    }

}
