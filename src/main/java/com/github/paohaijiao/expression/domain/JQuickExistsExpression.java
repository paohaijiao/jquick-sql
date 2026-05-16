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
import com.github.paohaijiao.plan.logic.domain.JQuickSubqueryNode;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;

import java.util.Collections;
import java.util.List;

/**
 * EXISTS 表达式 - 子查询存在性检查
 */
public class JQuickExistsExpression implements JQuickExpression {

    private final JQuickSubqueryNode subquery;
    private final boolean isNotExists;

    public JQuickExistsExpression(JQuickSubqueryNode subquery, boolean isNotExists) {
        this.subquery = subquery;
        this.isNotExists = isNotExists;
    }

    @Override
    public Object evaluate(JQuickRow row) {
        // EXISTS 子查询需要单独执行
        JQuickDataSet result = subquery.execute(null);
        boolean exists = !result.isEmpty();
        return isNotExists ? !exists : exists;
    }

    @Override
    public Class<?> getType() {
        return Boolean.class;
    }

    @Override
    public boolean isConstant() {
        return false;
    }

    @Override
    public List<String> getReferencedColumns() {
        return Collections.emptyList();
    }

    @Override
    public String toSql() {
        return (isNotExists ? "NOT EXISTS " : "EXISTS ") + "(subquery)";
    }

    @Override
    public JQuickExpression clone() {
        return new JQuickExistsExpression((JQuickSubqueryNode) subquery.clone(), isNotExists);
    }

    public JQuickSubqueryNode getSubquery() { return subquery; }
    public boolean isNotExists() { return isNotExists; }
}
