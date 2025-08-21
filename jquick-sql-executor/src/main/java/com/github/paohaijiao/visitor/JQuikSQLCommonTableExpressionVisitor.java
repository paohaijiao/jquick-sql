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

import com.github.paohaijiao.dataset.JColumnMeta;
import com.github.paohaijiao.dataset.JDataSet;
import com.github.paohaijiao.dataset.JRow;
import com.github.paohaijiao.parser.JQuickSQLParser;
import org.antlr.v4.runtime.RuleContext;

import java.util.*;
import java.util.stream.Collectors;

/**
 * packageName com.github.paohaijiao.visitor
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/8/11
 */
public class JQuikSQLCommonTableExpressionVisitor extends JQuikSQLFunctionStatementVisitor {

    private final Map<String, JDataSet> cteRegistry = new HashMap<>();

    private int recursionDepth = 0;

    private final int maxRecursionDepth = 100; //recurse limit

    @Override
    public JDataSet visitCommonTableExpression(JQuickSQLParser.CommonTableExpressionContext ctx) {
        String cteName = ctx.uid().getText();
        List<String> columnNames = null;
        if (ctx.columnNames() != null) {
            columnNames = ctx.columnNames().uid().stream().map(RuleContext::getText).collect(Collectors.toList());
        }
        if (ctx.UNION() != null) {
            return processRecursiveCTE(ctx, cteName, columnNames);
        } else {
            return processNonRecursiveCTE(ctx, cteName, columnNames);
        }
    }
    @Override
    public JDataSet visitQuery(JQuickSQLParser.QueryContext ctx) {
        return (JDataSet)visit(ctx.selectStatement());
    }


    private JDataSet processNonRecursiveCTE(JQuickSQLParser.CommonTableExpressionContext ctx, String cteName, List<String> columnNames) {
        JDataSet result = (JDataSet)visit(ctx.selectStatement());
        // apply column aliases if specified
        if (columnNames != null && !columnNames.isEmpty()) {
            result = renameColumns(result, columnNames);
        }
        return result;
    }

    private JDataSet processRecursiveCTE(JQuickSQLParser.CommonTableExpressionContext ctx, String cteName, List<String> columnNames) {
        JDataSet anchorResult = (JDataSet)visit(ctx.initialQuery().selectStatement());
        if (columnNames != null && !columnNames.isEmpty()) {
            anchorResult = renameColumns(anchorResult, columnNames);
        }
        cteRegistry.put(cteName, anchorResult);    //register the CTE name for recursive reference
        //recursive part
        JDataSet recursiveResult = (JDataSet)visit(ctx.recursivePart().selectStatement());
        // union the results
        JDataSet combined = unionDataSets(anchorResult, recursiveResult, ctx.ALL() != null);
        // keep applying recursion until no new rows are added
        JDataSet previous;
        JDataSet current = combined;
        int iteration = 0;
        do {
            previous = current;
            cteRegistry.put(cteName, previous);  // Re-evaluate the recursive part with the updated CTE
            recursiveResult =(JDataSet) visit(ctx.recursivePart().selectStatement());
            current = unionDataSets(previous, recursiveResult, ctx.ALL() != null);       // Union with previous results
            if (++iteration > maxRecursionDepth) {// Safety check
                throw new RuntimeException("Maximum recursion depth exceeded for CTE: " + cteName);
            }
        } while (current.size() > previous.size());
        return current;
    }

    private JDataSet renameColumns(JDataSet dataset, List<String> newNames) {
        List<JColumnMeta> originalColumns = dataset.getColumns();
        if (newNames.size() != originalColumns.size()) {
            throw new IllegalArgumentException("Column count mismatch in CTE");
        }// create new column metadata
        List<JColumnMeta> newColumns = new ArrayList<>();
        for (int i = 0; i < originalColumns.size(); i++) {
            JColumnMeta original = originalColumns.get(i);
            newColumns.add(new JColumnMeta(
                    newNames.get(i),
                    original.getType(),
                    original.getSource()
            ));
        }
        // create new rows with the same data but potentially renamed columns
        List<JRow> newRows = dataset.getRows().stream()
                .map(row -> {
                    JRow newRow = new JRow();
                    for (int i = 0; i < originalColumns.size(); i++) {
                        String originalName = originalColumns.get(i).getName();
                        newRow.put(newNames.get(i), row.get(originalName));
                    }
                    return newRow;
                })
                .collect(Collectors.toList());

        return new JDataSet(newColumns, newRows);
    }

    private JDataSet unionDataSets(JDataSet ds1, JDataSet ds2, boolean all) {
        if (!areSchemasCompatible(ds1, ds2)) { // validate schema compatibility
            throw new IllegalArgumentException("Incompatible schemas for UNION operation");
        }
        List<JRow> combinedRows = new ArrayList<>(ds1.getRows());
        if (all) {
            combinedRows.addAll(ds2.getRows());
        } else {
            // for union distinct, we need to remove duplicates
            Set<JRow> uniqueRows = new HashSet<>(ds1.getRows());
            uniqueRows.addAll(ds2.getRows());
            combinedRows = new ArrayList<>(uniqueRows);
        }

        return new JDataSet(ds1.getColumns(), combinedRows);
    }
    private boolean areSchemasCompatible(JDataSet ds1, JDataSet ds2) {
        if (ds1.getColumns().size() != ds2.getColumns().size()) {
            return false;
        }
        for (int i = 0; i < ds1.getColumns().size(); i++) {
            JColumnMeta col1 = ds1.getColumns().get(i);
            JColumnMeta col2 = ds2.getColumns().get(i);
            if (!col1.getType().equals(col2.getType())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public JDataSet visitTableSourceItem(JQuickSQLParser.TableSourceItemContext ctx) {
        if (ctx.tableName() != null) {
            String tableName = ctx.tableName().getText();
            if (cteRegistry.containsKey(tableName)) { // check if this is a CTE reference
                return cteRegistry.get(tableName);
            }
            //lookup  table
            throw new UnsupportedOperationException("Actual table lookup not implemented");
        } else if (ctx.selectStatement() != null) {
            return (JDataSet)visit(ctx.selectStatement());
        } else {
            return (JDataSet)visitTableSources(ctx.tableSources());
        }
    }

}
