package com.github.paohaijiao.evalue;

import com.github.paohaijiao.dataset.ColumnMeta;
import com.github.paohaijiao.dataset.DataSet;
import com.github.paohaijiao.dataset.Row;
import com.github.paohaijiao.expression.JQuickSqlColumnExpression;
import com.github.paohaijiao.expression.JQuickSqlExpression;
import com.github.paohaijiao.expression.olap.*;
import com.github.paohaijiao.support.JQuickSqlOLAPOperations;

import java.util.*;

public class JQuickSqlOLAPExpressionEvaluator extends JQuickSqlBaseEvaluator implements JQuickSqlEvaluator<JQuickSqlExpression, Object> {

    private final JQuickSqlExpressionEvaluator expressionEvaluator;

    private DataSet currentDataset;

    public JQuickSqlOLAPExpressionEvaluator() {
        this.expressionEvaluator = new JQuickSqlExpressionEvaluator();
    }

    public static List<Row> filterRows(DataSet dataset, JQuickSqlExpression condition) {
        JQuickSqlOLAPExpressionEvaluator evaluator = new JQuickSqlOLAPExpressionEvaluator();
        evaluator.setDataset(dataset);
        return evaluator.filterRowsByCondition(condition);
    }

    public static DataSet executeOLAP(DataSet dataset, JQuickSqlExpression olapExpression) {
        JQuickSqlOLAPExpressionEvaluator evaluator = new JQuickSqlOLAPExpressionEvaluator();
        evaluator.setDataset(dataset);
        return evaluator.executeOLAPOperation(olapExpression);
    }

    public void setDataset(DataSet dataset) {
        this.currentDataset = dataset;
    }

    @Override
    public Boolean evaluate(JQuickSqlExpression expression, Map<String, Object> row) {
        if (expression instanceof JQuickSqlSliceExpression) {
            return evaluateSliceCondition((JQuickSqlSliceExpression) expression, row);
        } else if (expression instanceof JQuickSqlDiceExpression) {
            return evaluateDiceCondition((JQuickSqlDiceExpression) expression, row);
        } else {
            Object result = expressionEvaluator.evaluate(expression, row);
            return result instanceof Boolean ? (Boolean) result : false;
        }
    }

    private Boolean evaluateSliceCondition(JQuickSqlSliceExpression sliceExpr, Map<String, Object> row) {
        String dimension = sliceExpr.getDimension();
        Object expectedValue = expressionEvaluator.evaluate(sliceExpr.getExpression(), row);
        Object actualValue = row.get(dimension);
        return Objects.equals(actualValue, expectedValue);
    }

    private Boolean evaluateDiceCondition(JQuickSqlDiceExpression diceExpr, Map<String, Object> row) {
        Map<JQuickSqlExpression, JQuickSqlExpression> conditions = diceExpr.getConditions();
        for (Map.Entry<JQuickSqlExpression, JQuickSqlExpression> entry : conditions.entrySet()) {
            Object columnNameObj = entry.getKey();
            if (!(columnNameObj instanceof JQuickSqlColumnExpression)) {
                return false;
            }
            String columnName = ((JQuickSqlColumnExpression) columnNameObj).getColumnName();
            Object expectedValue = expressionEvaluator.evaluate(entry.getValue(), row);
            Object actualValue = row.get(columnName);
            if (!Objects.equals(actualValue, expectedValue)) {
                return false;
            }
        }
        return true;
    }

    public DataSet executeOLAPOperation(JQuickSqlExpression olapExpression) {
        if (currentDataset == null) {
            throw new IllegalStateException("Dataset not set. Call setDataset() first.");
        }
        if (olapExpression instanceof JQuickSqlRollUpExpression) {
            JQuickSqlRollUpExpression rollUpExpr = (JQuickSqlRollUpExpression) olapExpression;
            return JQuickSqlOLAPOperations.rollUp(currentDataset, rollUpExpr.getGroupByColumns(), rollUpExpr.getAggregations());
        } else if (olapExpression instanceof JQuickSqlDrillDownExpression) {
            JQuickSqlDrillDownExpression drillDownExpr = (JQuickSqlDrillDownExpression) olapExpression;
            return JQuickSqlOLAPOperations.drillDown(currentDataset, drillDownExpr.getGroupByColumns(), drillDownExpr.getAggregations());
        } else if (olapExpression instanceof JQuickSqlSliceExpression) {
            List<Row> filteredRows = filterRowsByCondition(olapExpression);
            return new DataSet(currentDataset.getColumns(), filteredRows);
        } else if (olapExpression instanceof JQuickSqlDiceExpression) {
            List<Row> filteredRows = filterRowsByCondition(olapExpression);
            return new DataSet(currentDataset.getColumns(), filteredRows);
        } else if (olapExpression instanceof JQuickSqlPivotExpression) {
            JQuickSqlPivotExpression pivotExpr = (JQuickSqlPivotExpression) olapExpression;
            return JQuickSqlOLAPOperations.pivot(currentDataset, pivotExpr.getPivotColumn(), pivotExpr.getValueColumn(), pivotExpr.getAggregator());
        } else {
            throw new UnsupportedOperationException("unsupported olap expression type: " + olapExpression.getClass().getSimpleName());
        }
    }

    private List<Row> filterRowsByCondition(JQuickSqlExpression condition) {
        List<Row> filteredRows = new ArrayList<>();
        for (Row row : currentDataset.getRows()) {
            Map<String, Object> rowData = new HashMap<>();
            for (ColumnMeta column : currentDataset.getColumns()) {
                rowData.put(column.getName(), row.get(column.getName()));
            }
            Boolean result = evaluate(condition, rowData);
            if (result != null && result) {
                filteredRows.add(row);
            }
        }
        return filteredRows;
    }
}
