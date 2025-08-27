package com.github.paohaijiao.evalue;

import com.github.paohaijiao.dataset.JColumnMeta;
import com.github.paohaijiao.dataset.JDataSet;
import com.github.paohaijiao.dataset.JRow;
import com.github.paohaijiao.expression.JExpression;
import com.github.paohaijiao.expression.olap.*;
import com.github.paohaijiao.support.JOLAPOperations;

import java.util.*;

public class JOLAPExpressionEvaluator extends JBaseEvaluator implements JSqlEvaluator<JExpression,Object> {

    private final JExpressionEvaluator expressionEvaluator;

    private JDataSet currentDataset;

    public JOLAPExpressionEvaluator() {
        this.expressionEvaluator = new JExpressionEvaluator();
    }

    public void setDataset(JDataSet dataset) {
        this.currentDataset = dataset;
    }

    @Override
    public Boolean evaluate(JExpression expression, Map<String, Object> row) {
        if (expression instanceof JSliceExpression) {
            return evaluateSliceCondition((JSliceExpression) expression, row);
        } else if (expression instanceof JDiceExpression) {
            return evaluateDiceCondition((JDiceExpression) expression, row);
        } else {
            Object result = expressionEvaluator.evaluate(expression, row);
            return result instanceof Boolean ? (Boolean) result : false;
        }
    }
    private Boolean evaluateSliceCondition(JSliceExpression sliceExpr, Map<String, Object> row) {
        String dimension = sliceExpr.getDimension();
        Object expectedValue = expressionEvaluator.evaluate(sliceExpr.getExpression(), row);
        Object actualValue = row.get(dimension);
        return Objects.equals(actualValue, expectedValue);
    }

    private Boolean evaluateDiceCondition(JDiceExpression diceExpr, Map<String, Object> row) {
        Map<JExpression, JExpression> conditions = diceExpr.getConditions();
        for (Map.Entry<JExpression, JExpression> entry : conditions.entrySet()) {
            Object columnNameObj = expressionEvaluator.evaluate(entry.getKey(), row);
            if (!(columnNameObj instanceof String)) {
                return false;
            }
            String columnName = (String) columnNameObj;
            Object expectedValue = expressionEvaluator.evaluate(entry.getValue(), row);
            Object actualValue = row.get(columnName);
            if (!Objects.equals(actualValue, expectedValue)) {
                return false;
            }
        }
        return true;
    }

    public JDataSet executeOLAPOperation(JExpression olapExpression) {
        if (currentDataset == null) {
            throw new IllegalStateException("Dataset not set. Call setDataset() first.");
        }
        if (olapExpression instanceof JRollUpExpression) {
            JRollUpExpression rollUpExpr = (JRollUpExpression) olapExpression;
            return JOLAPOperations.rollUp(currentDataset, rollUpExpr.getGroupByColumns(), rollUpExpr.getAggregations());
        } else if (olapExpression instanceof JDrillDownExpression) {
            JDrillDownExpression drillDownExpr = (JDrillDownExpression) olapExpression;
            return JOLAPOperations.drillDown(currentDataset, drillDownExpr.getGroupByColumns(), drillDownExpr.getAggregations());
        } else if (olapExpression instanceof JSliceExpression) {
            List<JRow> filteredRows = filterRowsByCondition(olapExpression);
            return new JDataSet(currentDataset.getColumns(), filteredRows);
        } else if (olapExpression instanceof JDiceExpression) {
            List<JRow> filteredRows = filterRowsByCondition(olapExpression);
            return new JDataSet(currentDataset.getColumns(), filteredRows);
        } else if (olapExpression instanceof JPivotExpression) {
            JPivotExpression pivotExpr = (JPivotExpression) olapExpression;
            return JOLAPOperations.pivot(currentDataset, pivotExpr.getPivotColumn(), pivotExpr.getValueColumn(), pivotExpr.getAggregator());
        } else {
            throw new UnsupportedOperationException("unsupported olap expression type: " + olapExpression.getClass().getSimpleName());
        }
    }

    private List<JRow> filterRowsByCondition(JExpression condition) {
        List<JRow> filteredRows = new ArrayList<>();
        for (JRow row : currentDataset.getRows()) {
            Map<String, Object> rowData = new HashMap<>();
            for (JColumnMeta column : currentDataset.getColumns()) {
                rowData.put(column.getName(), row.get(column.getName()));
            }
            Boolean result = evaluate(condition, rowData);
            if (result != null && result) {
                filteredRows.add(row);
            }
        }
        return filteredRows;
    }

    public static List<JRow> filterRows(JDataSet dataset, JExpression condition) {
        JOLAPExpressionEvaluator evaluator = new JOLAPExpressionEvaluator();
        evaluator.setDataset(dataset);
        return evaluator.filterRowsByCondition(condition);
    }
    public static JDataSet executeOLAP(JDataSet dataset, JExpression olapExpression) {
        JOLAPExpressionEvaluator evaluator = new JOLAPExpressionEvaluator();
        evaluator.setDataset(dataset);
        return evaluator.executeOLAPOperation(olapExpression);
    }
}
