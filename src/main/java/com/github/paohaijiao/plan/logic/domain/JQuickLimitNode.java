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
package com.github.paohaijiao.plan.logic.domain;


import com.github.paohaijiao.executor.JQuickExecutionContext;
import com.github.paohaijiao.plan.logic.JQuickLogicalPlanNode;
import com.github.paohaijiao.plan.logic.JQuickLogicalPlanVisitor;
import com.github.paohaijiao.statement.JQuickDataSet;

import java.util.Collections;
import java.util.List;

/**
 * 限制节点 - LIMIT 子句
 */
public class JQuickLimitNode implements JQuickLogicalPlanNode {

    private final int limit;

    private final int offset;

    private final JQuickLogicalPlanNode child;

    public JQuickLimitNode(int limit, JQuickLogicalPlanNode child) {
        this(limit, 0, child);
    }

    public JQuickLimitNode(int limit, int offset, JQuickLogicalPlanNode child) {
        this.limit = limit;
        this.offset = offset;
        this.child = child;
    }

    @Override
    public JQuickDataSet execute(JQuickExecutionContext context) {
        JQuickDataSet data = child.execute(context);

        if (offset > 0) {
            data = data.skip(offset);
        }
        return data.limit(limit);
    }

    @Override
    public String getNodeType() {
        return "Limit";
    }

    @Override
    public List<JQuickLogicalPlanNode> getChildren() {
        return Collections.singletonList(child);
    }

    @Override
    public void accept(JQuickLogicalPlanVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public List<String> getOutputColumns() {
        return child.getOutputColumns();
    }

    @Override
    public JQuickLogicalPlanNode clone() {
        return new JQuickLimitNode(limit, offset, child.clone());
    }

    public int getLimit() {
        return limit;
    }

    public int getOffset() {
        return offset;
    }

    public JQuickLogicalPlanNode getChild() {
        return child;
    }
}
