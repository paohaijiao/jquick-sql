package com.github.paohaijiao.evalue;

import com.github.paohaijiao.expression.JQuickSqlColumnExpression;
import com.github.paohaijiao.expression.JQuickSqlExpression;
import com.github.paohaijiao.expression.olap.*;
import com.github.paohaijiao.statement.JQuickColumnMeta;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;
import com.github.paohaijiao.support.JQuickSqlOLAPOperations;

import java.util.*;

public class JQuickSqlOLAPExpressionEvaluator extends JQuickSqlBaseEvaluator implements JQuickSqlEvaluator<JQuickSqlExpression, Object> {

    private final JQuickSqlExpressionEvaluator expressionEvaluator;

    private JQuickDataSet currentDataset;

    public JQuickSqlOLAPExpressionEvaluator() {
        this.expressionEvaluator = new JQuickSqlExpressionEvaluator();
    }

    public static List<JQuickRow> filterRows(JQuickDataSet dataset, JQuickSqlExpression condition) {
        JQuickSqlOLAPExpressionEvaluator evaluator = new JQuickSqlOLAPExpressionEvaluator();
        evaluator.setDataset(dataset);
        return evaluator.filterRowsByCondition(condition);
    }

    public static JQuickDataSet executeOLAP(JQuickDataSet dataset, JQuickSqlExpression olapExpression) {
        JQuickSqlOLAPExpressionEvaluator evaluator = new JQuickSqlOLAPExpressionEvaluator();
        evaluator.setDataset(dataset);
        return evaluator.executeOLAPOperation(olapExpression);
    }

    public void setDataset(JQuickDataSet dataset) {
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

    public JQuickDataSet executeOLAPOperation(JQuickSqlExpression olapExpression) {
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
            List<JQuickRow> filteredRows = filterRowsByCondition(olapExpression);
            return new JQuickDataSet(currentDataset.getColumns(), filteredRows);
        } else if (olapExpression instanceof JQuickSqlDiceExpression) {
            List<JQuickRow> filteredRows = filterRowsByCondition(olapExpression);
            return new JQuickDataSet(currentDataset.getColumns(), filteredRows);
        } else if (olapExpression instanceof JQuickSqlPivotExpression) {
            JQuickSqlPivotExpression pivotExpr = (JQuickSqlPivotExpression) olapExpression;
            return JQuickSqlOLAPOperations.pivot(currentDataset, pivotExpr.getPivotColumn(), pivotExpr.getValueColumn(), pivotExpr.getAggregator());
        } else {
            throw new UnsupportedOperationException("unsupported olap expression type: " + olapExpression.getClass().getSimpleName());
        }
    }

    private List<JQuickRow> filterRowsByCondition(JQuickSqlExpression condition) {
        List<JQuickRow> filteredRows = new ArrayList<>();
        for (JQuickRow row : currentDataset.getRows()) {
            Map<String, Object> rowData = new HashMap<>();
            for (JQuickColumnMeta column : currentDataset.getColumns()) {
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
