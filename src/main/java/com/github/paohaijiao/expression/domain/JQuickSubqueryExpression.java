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

import com.github.paohaijiao.context.JQuickExecutionContext;
import com.github.paohaijiao.enums.JQuickSubqueryType;
import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.logic.JQuickLogicalPlanNode;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 子查询表达式 - 处理标量子查询、EXISTS子查询、IN子查询
 */
public class JQuickSubqueryExpression implements JQuickExpression {

    private final JQuickLogicalPlanNode subquery;

    private final JQuickSubqueryType type;

    private final JQuickExpression leftExpression;  // 用于 IN/ANY/ALL 子查询

    private final JQuickExpression rightExpression; // 用于比较子查询


    // 标量子查询构造器
    public JQuickSubqueryExpression(JQuickLogicalPlanNode subquery) {
        this(subquery, JQuickSubqueryType.SCALAR, null, null);
    }

    // EXISTS/NOT_EXISTS 构造器
    public JQuickSubqueryExpression(JQuickLogicalPlanNode subquery, JQuickSubqueryType type) {
        this(subquery, type, null, null);
    }

    // IN/NOT_IN 构造器
    public JQuickSubqueryExpression(JQuickLogicalPlanNode subquery, JQuickSubqueryType type, JQuickExpression leftExpression) {
        this(subquery, type, leftExpression, null);
    }

    // ANY/ALL 构造器
    public JQuickSubqueryExpression(JQuickLogicalPlanNode subquery, JQuickSubqueryType type, JQuickExpression leftExpression, JQuickExpression rightExpression) {
        this.subquery = subquery;
        this.type = type;
        this.leftExpression = leftExpression;
        this.rightExpression = rightExpression;
    }

    @Override
    public Object evaluate(JQuickRow row) {
        // 创建子查询执行上下文
        JQuickExecutionContext childContext = createChildContext(row);
        try {
            // 执行子查询
            JQuickDataSet result = null;//subquery.execute(childContext);
            switch (type) {
                case SCALAR:
                    return evaluateScalar(result);
                case EXISTS:
                    return !result.isEmpty();
                case NOT_EXISTS:
                    return result.isEmpty();
                case IN:
                    return evaluateIn(result, row);
                case NOT_IN:
                    return !evaluateIn(result, row);
                case ANY:
                    return evaluateAny(result, row);
                case ALL:
                    return evaluateAll(result, row);
                default:
                    return null;
            }
        } catch (Exception e) {
            throw new RuntimeException("Subquery execution failed", e);
        }
    }

    @Override
    public Class<?> getType() {
        return null;
    }

    /**
     * 标量子查询求值 - 返回单行单列的值
     */
    private Object evaluateScalar(JQuickDataSet result) {
        if (result.isEmpty()) {
            return null;
        }

        JQuickRow firstRow = result.first();
        if (firstRow == null) {
            return null;
        }

        List<String> columns = result.getColumnNames();
        if (columns.isEmpty()) {
            return null;
        }

        return firstRow.get(columns.get(0));
    }

    /**
     * IN 子查询求值 - 检查左表达式是否在子查询结果中
     */
    private boolean evaluateIn(JQuickDataSet result, JQuickRow outerRow) {
        if (leftExpression == null) {
            return false;
        }

        Object leftValue = leftExpression.evaluate(outerRow);
        if (leftValue == null) {
            return false;
        }

        for (JQuickRow row : result.getRows()) {
            List<String> columns = result.getColumnNames();
            if (columns.isEmpty()) continue;

            Object rightValue = row.get(columns.get(0));
            if (leftValue.equals(rightValue)) {
                return true;
            }
        }

        return false;
    }

    /**
     * ANY 子查询求值 - 存在任意一行满足条件
     */
    private boolean evaluateAny(JQuickDataSet result, JQuickRow outerRow) {
        if (leftExpression == null || rightExpression == null) {
            return false;
        }

        Object leftValue = leftExpression.evaluate(outerRow);
        if (leftValue == null) {
            return false;
        }

        for (JQuickRow row : result.getRows()) {
            Object rightValue = rightExpression.evaluate(row);
            if (rightValue == null) continue;

            if (compare(leftValue, rightValue)) {
                return true;
            }
        }

        return false;
    }

    /**
     * ALL 子查询求值 - 所有行都满足条件
     */
    private boolean evaluateAll(JQuickDataSet result, JQuickRow outerRow) {
        if (leftExpression == null || rightExpression == null) {
            return false;
        }

        Object leftValue = leftExpression.evaluate(outerRow);
        if (leftValue == null) {
            return false;
        }

        if (result.isEmpty()) {
            return true;  // 空集时 ALL 返回 true
        }

        for (JQuickRow row : result.getRows()) {
            Object rightValue = rightExpression.evaluate(row);
            if (rightValue == null) continue;

            if (!compare(leftValue, rightValue)) {
                return false;
            }
        }

        return true;
    }

    /**
     * 比较两个值
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private boolean compare(Object left, Object right) {
        if (left instanceof Comparable && right instanceof Comparable) {
            int cmp = ((Comparable) left).compareTo(right);
            // 这里简化使用等值比较，实际应根据操作符
            return cmp == 0;
        }
        return left.equals(right);
    }

    /**
     * 创建子查询执行上下文
     */
    private JQuickExecutionContext createChildContext(JQuickRow outerRow) {
        JQuickExecutionContext context = new JQuickExecutionContext();
        if (outerRow != null) {
            for (String key : outerRow.keySet()) {
                Map map=context.getParameters();
                map.put(key, outerRow.get(key));
            }
        }

        return context;
    }

    /**
     * 缓存子查询结果（用于优化）
     */



//    @Override
//    public Class<?> getType() {
//        switch (type) {
//            case EXISTS:
//            case NOT_EXISTS:
//            case IN:
//            case NOT_IN:
//            case ANY:
//            case ALL:
//                return Boolean.class;
//            case SCALAR:
//            default:
//                return Object.class;
//        }
//    }

    @Override
    public boolean isConstant() {
        return false;
    }

    @Override
    public List<String> getReferencedColumns() {
        if (leftExpression != null) {
            return leftExpression.getReferencedColumns();
        }
        return Collections.emptyList();
    }

    @Override
    public String toSql() {
        StringBuilder sb = new StringBuilder();
        switch (type) {
            case SCALAR:
                sb.append("(").append(subquery.toString()).append(")");
                break;
            case EXISTS:
                sb.append("EXISTS (").append(subquery.toString()).append(")");
                break;
            case NOT_EXISTS:
                sb.append("NOT EXISTS (").append(subquery.toString()).append(")");
                break;
            case IN:
                sb.append(leftExpression.toSql()).append(" IN (").append(subquery.toString()).append(")");
                break;
            case NOT_IN:
                sb.append(leftExpression.toSql()).append(" NOT IN (").append(subquery.toString()).append(")");
                break;
            case ANY:
                sb.append(leftExpression.toSql()).append(" = ANY (").append(subquery.toString()).append(")");
                break;
            case ALL:
                sb.append(leftExpression.toSql()).append(" = ALL (").append(subquery.toString()).append(")");
                break;
        }
        return sb.toString();
    }

    @Override
    public JQuickExpression clone() {
        // 深拷贝子查询计划
        JQuickLogicalPlanNode clonedSubquery = subquery.clone();

        JQuickExpression clonedLeft = leftExpression != null ? leftExpression.clone() : null;
        JQuickExpression clonedRight = rightExpression != null ? rightExpression.clone() : null;

        return new JQuickSubqueryExpression(clonedSubquery, type, clonedLeft, clonedRight);
    }


    public JQuickLogicalPlanNode getSubquery() { return subquery; }
    //public JQuickSubqueryType getType() { return type; }
    public JQuickExpression getLeftExpression() { return leftExpression; }
    public JQuickExpression getRightExpression() { return rightExpression; }
}
