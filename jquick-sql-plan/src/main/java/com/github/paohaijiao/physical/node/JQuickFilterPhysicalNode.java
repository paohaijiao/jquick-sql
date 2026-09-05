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

import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.physical.JQuickPhysicalPlanNode;
import com.github.paohaijiao.physical.JQuickPhysicalPlanVisitor;
import com.github.paohaijiao.physical.domain.JQuickPhysicalColumn;
import com.github.paohaijiao.physical.domain.JQuickPhysicalStats;

import java.util.List;

public class JQuickFilterPhysicalNode extends JQuickAbstractPhysicalNode {

    private final JQuickExpression predicate;

    public JQuickFilterPhysicalNode(JQuickExpression predicate, JQuickPhysicalPlanNode child) {
        super(child);
        this.predicate = predicate;
    }

    @Override
    public String getNodeType() {
        return "Filter";
    }

    @Override
    public List<JQuickPhysicalColumn> getOutputSchema() {
        return children.get(0).getOutputSchema();
    }

    @Override
    public JQuickPhysicalPlanNode clone() {
        return new JQuickFilterPhysicalNode(predicate.clone(), children.get(0).clone());
    }

    @Override
    public void accept(JQuickPhysicalPlanVisitor visitor) {
        visitor.visit(this);
    }

    public JQuickExpression getPredicate() { return predicate; }

    @Override
    public JQuickPhysicalStats getStats() {
        JQuickPhysicalPlanNode child = getChild();
        if (child == null) {
            return JQuickPhysicalStats.empty();
        }
        JQuickPhysicalStats childStats = child.getStats();
        double selectivity = 0.5;
        if (predicate != null) {
            String pred = predicate.toString().toLowerCase();
            if (pred.contains("=")) selectivity = 0.1;
            if (pred.contains(">") || pred.contains("<")) selectivity = 0.3;
            if (pred.contains("like")) selectivity = 0.2;
            if (pred.contains("and")) selectivity = 0.25;
            if (pred.contains("or")) selectivity = 0.5;
        }
        long estimatedRows = (long) (childStats.getEstimatedRowCount() * selectivity);
        long estimatedDataSize = (long) (childStats.getEstimatedDataSize() * selectivity);
        return new JQuickPhysicalStats(estimatedRows, estimatedDataSize, childStats.getColumnStats());
    }
}
