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
package com.github.paohaijiao.logic.domain;

import com.github.paohaijiao.context.JQuickExecutionContext;
import com.github.paohaijiao.logic.JQuickLogicalPlanNode;
import com.github.paohaijiao.logic.JQuickLogicalPlanVisitor;
import com.github.paohaijiao.statement.JQuickDataSet;

import java.util.Collections;
import java.util.List;

/**
 * 子查询节点 - 用于标量子查询或 EXISTS 子查询
 */
public class JQuickSubqueryNode implements JQuickLogicalPlanNode {

    private final JQuickLogicalPlanNode subquery;
    private final SubqueryType type;

    public JQuickSubqueryNode(JQuickLogicalPlanNode subquery, SubqueryType type) {
        this.subquery = subquery;
        this.type = type;
    }

    @Override
    public JQuickDataSet execute(JQuickExecutionContext context) {
        return subquery.execute(context.createChildContext());
    }

    @Override
    public String getNodeType() {
        return "Subquery";
    }

    @Override
    public List<JQuickLogicalPlanNode> getChildren() {
        return Collections.singletonList(subquery);
    }

    @Override
    public void accept(JQuickLogicalPlanVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public List<String> getOutputColumns() {
        return Collections.singletonList("subquery_result");
    }

    @Override
    public JQuickLogicalPlanNode clone() {
        return new JQuickSubqueryNode(subquery.clone(), type);
    }

    public JQuickLogicalPlanNode getSubquery() {
        return subquery;
    }

    public SubqueryType getType() {
        return type;
    }

    public enum SubqueryType {
        SCALAR,      // 标量子查询（返回单行单列）
        EXISTS,      // EXISTS 子查询
        IN,          // IN 子查询
        ANY,         // ANY 子查询
        ALL          // ALL 子查询
    }
}
