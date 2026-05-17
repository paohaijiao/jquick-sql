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
package com.github.paohaijiao.physical.node;
import com.github.paohaijiao.context.JQuickExecutionContext;
import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.physical.JQuickPhysicalPlanNode;
import com.github.paohaijiao.statement.JQuickDataSet;

public class JQuickFilterPhysicalNode implements JQuickPhysicalPlanNode {
    private final JQuickExpression predicate;
    private final JQuickPhysicalPlanNode child;

    public JQuickFilterPhysicalNode(JQuickExpression predicate, JQuickPhysicalPlanNode child) {
        this.predicate = predicate;
        this.child = child;
    }

    @Override
    public JQuickDataSet execute(JQuickExecutionContext context) {
        JQuickDataSet data = child.execute(context);
        return data.filter(row -> {
            Object result = predicate.evaluate(row);
            return result instanceof Boolean && (Boolean) result;
        });
    }

    @Override
    public String getNodeType() {
        return "Filter";
    }

    @Override
    public long getEstimatedCost() {
        return child.getEstimatedCost();
    }
}
