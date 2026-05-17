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
package com.github.paohaijiao.expression.domain;


import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.statement.JQuickRow;

import java.util.ArrayList;
import java.util.List;

/**
 * CASE WHEN 表达式 - 条件分支
 */
public class JQuickCaseWhenExpression implements JQuickExpression {

    private final List<JQuickExpression> conditions;

    private final List<JQuickExpression> results;

    private final JQuickExpression elseResult;

    public JQuickCaseWhenExpression(List<JQuickExpression> conditions, List<JQuickExpression> results, JQuickExpression elseResult) {
        this.conditions = conditions;
        this.results = results;
        this.elseResult = elseResult;
    }

    @Override
    public Object evaluate(JQuickRow row) {
        for (int i = 0; i < conditions.size(); i++) {
            Object condition = conditions.get(i).evaluate(row);
            if (condition instanceof Boolean && (Boolean) condition) {
                return results.get(i).evaluate(row);
            }
        }
        return elseResult != null ? elseResult.evaluate(row) : null;
    }

    @Override
    public Class<?> getType() {
        // 返回第一个结果的类型
        if (!results.isEmpty()) {
            return results.get(0).getType();
        }
        if (elseResult != null) {
            return elseResult.getType();
        }
        return Object.class;
    }

    @Override
    public boolean isConstant() {
        for (JQuickExpression cond : conditions) {
            if (!cond.isConstant()) return false;
        }
        for (JQuickExpression result : results) {
            if (!result.isConstant()) return false;
        }
        return elseResult == null || elseResult.isConstant();
    }

    @Override
    public List<String> getReferencedColumns() {
        List<String> columns = new ArrayList<>();
        for (JQuickExpression cond : conditions) {
            columns.addAll(cond.getReferencedColumns());
        }
        for (JQuickExpression result : results) {
            columns.addAll(result.getReferencedColumns());
        }
        if (elseResult != null) {
            columns.addAll(elseResult.getReferencedColumns());
        }
        return columns;
    }

    @Override
    public String toSql() {
        StringBuilder sb = new StringBuilder("CASE ");
        for (int i = 0; i < conditions.size(); i++) {
            sb.append("WHEN ").append(conditions.get(i).toSql())
                    .append(" THEN ").append(results.get(i).toSql()).append(" ");
        }
        if (elseResult != null) {
            sb.append("ELSE ").append(elseResult.toSql()).append(" ");
        }
        sb.append("END");
        return sb.toString();
    }

    @Override
    public JQuickExpression clone() {
        List<JQuickExpression> clonedConditions = new ArrayList<>();
        List<JQuickExpression> clonedResults = new ArrayList<>();
        for (JQuickExpression cond : conditions) {
            clonedConditions.add(cond.clone());
        }
        for (JQuickExpression result : results) {
            clonedResults.add(result.clone());
        }
        JQuickExpression clonedElse = elseResult != null ? elseResult.clone() : null;
        return new JQuickCaseWhenExpression(clonedConditions, clonedResults, clonedElse);
    }

    public List<JQuickExpression> getConditions() { return conditions; }
    public List<JQuickExpression> getResults() { return results; }
    public JQuickExpression getElseResult() { return elseResult; }
}
