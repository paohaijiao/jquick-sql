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
 * IN 表达式 - 如 a IN (1, 2, 3) 或 a IN (SELECT ...)
 */
public class JQuickInExpression implements JQuickExpression {

    private final JQuickExpression left;

    private final List<JQuickExpression> rightList;

    private final boolean isNot;

    public JQuickInExpression(JQuickExpression left, List<JQuickExpression> rightList, boolean isNot) {
        this.left = left;
        this.rightList = rightList;
        this.isNot = isNot;
    }

    @Override
    public Object evaluate(JQuickRow row) {
        Object leftVal = left.evaluate(row);
        if (leftVal == null) return null;
        boolean found = false;
        for (JQuickExpression expr : rightList) {
            Object rightVal = expr.evaluate(row);
            if (leftVal.equals(rightVal)) {
                found = true;
                break;
            }
        }
        boolean result = isNot ? !found : found;
        if (leftVal == null) return null;
        return result;
    }

    @Override
    public Class<?> getType() {
        return Boolean.class;
    }

    @Override
    public boolean isConstant() {
        return left.isConstant() && rightList.stream().allMatch(JQuickExpression::isConstant);
    }

    @Override
    public List<String> getReferencedColumns() {
        List<String> columns = left.getReferencedColumns();
        for (JQuickExpression expr : rightList) {
            columns.addAll(expr.getReferencedColumns());
        }
        return columns;
    }

    @Override
    public String toSql() {
        StringBuilder sb = new StringBuilder();
        sb.append(left.toSql());
        if (isNot) {
            sb.append(" NOT IN (");
        } else {
            sb.append(" IN (");
        }
        for (int i = 0; i < rightList.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(rightList.get(i).toSql());
        }
        sb.append(")");
        return sb.toString();
    }

    @Override
    public JQuickExpression clone() {
        List<JQuickExpression> clonedRight = new ArrayList<>();
        for (JQuickExpression expr : rightList) {
            clonedRight.add(expr.clone());
        }
        return new JQuickInExpression(left.clone(), clonedRight, isNot);
    }

    public JQuickExpression getLeft() { return left; }

    public List<JQuickExpression> getRightList() { return rightList; }

    public boolean isNot() { return isNot; }
}
