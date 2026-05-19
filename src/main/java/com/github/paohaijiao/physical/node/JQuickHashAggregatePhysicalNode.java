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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class JQuickHashAggregatePhysicalNode extends JQuickAbstractPhysicalNode {

    private final List<JQuickExpression> groupKeys;

    private final List<AggregateFunction> aggregates;

    private final JQuickExpression havingCondition;

    private final AggregateStage stage;

    private final List<GroupingSet> groupingSets;

    public enum AggregateStage {
        PARTIAL, FINAL, SINGLE
    }

    public static class AggregateFunction {

        private final String functionName;

        private final JQuickExpression argument;

        private final boolean distinct;

        private final String alias;

        private final boolean isCountStar;

        private final String separator;

        private final AggregateStage internalStage;

        public AggregateFunction(String functionName, JQuickExpression argument, boolean distinct, String alias, boolean isCountStar, String separator, AggregateStage internalStage) {
            this.functionName = functionName;
            this.argument = argument;
            this.distinct = distinct;
            this.alias = alias;
            this.isCountStar = isCountStar;
            this.separator = separator;
            this.internalStage = internalStage;
        }

        public AggregateFunction(String functionName, JQuickExpression argument, boolean distinct, String alias) {
            this(functionName, argument, distinct, alias, false, null, AggregateStage.SINGLE);
        }

        public String getFunctionName() { return functionName; }

        public JQuickExpression getArgument() { return argument; }

        public boolean isDistinct() { return distinct; }

        public String getAlias() { return alias; }

        public boolean isCountStar() { return isCountStar; }

        public String getSeparator() { return separator; }

        public AggregateStage getInternalStage() { return internalStage; }

        public AggregateFunction clone() {
            return new AggregateFunction(functionName, argument != null ? argument.clone() : null, distinct, alias, isCountStar, separator, internalStage);
        }
    }

    public static class GroupingSet {

        private final List<JQuickExpression> keys;

        private final GroupingSetType type;

        public enum GroupingSetType { SIMPLE, ROLLUP, CUBE }

        public GroupingSet(List<JQuickExpression> keys, GroupingSetType type) {
            this.keys = new ArrayList<>(keys);
            this.type = type;
        }

        public List<JQuickExpression> getKeys() { return keys; }

        public GroupingSetType getType() { return type; }
    }

    public JQuickHashAggregatePhysicalNode(List<JQuickExpression> groupKeys, List<AggregateFunction> aggregates, JQuickPhysicalPlanNode child, JQuickExpression havingCondition, AggregateStage stage) {
        this(groupKeys, aggregates, child, havingCondition, stage, null);
    }

    public JQuickHashAggregatePhysicalNode(List<JQuickExpression> groupKeys, List<AggregateFunction> aggregates, JQuickPhysicalPlanNode child, JQuickExpression havingCondition, AggregateStage stage, List<GroupingSet> groupingSets) {
        super(child);
        this.groupKeys = groupKeys != null ? new ArrayList<>(groupKeys) : new ArrayList<>();
        this.aggregates = new ArrayList<>(aggregates);
        this.havingCondition = havingCondition;
        this.stage = stage;
        this.groupingSets = groupingSets;
    }

    @Override
    public String getNodeType() {
        return "HashAggregate";
    }

    @Override
    public List<JQuickPhysicalColumn> getOutputSchema() {
        List<JQuickPhysicalColumn> schema = new ArrayList<>();
        for (int i = 0; i < groupKeys.size(); i++) {
            schema.add(new JQuickPhysicalColumn("group_key_" + i, Object.class, null, false));
        }
        for (AggregateFunction agg : aggregates) {
            schema.add(new JQuickPhysicalColumn(agg.getAlias(), Object.class, null, true));
        }
        return schema;
    }

    @Override
    public JQuickPhysicalStats getStats() {
        long estimatedGroups = Math.min(1000, getChild().getStats().getEstimatedRowCount());
        return new JQuickPhysicalStats(estimatedGroups, estimatedGroups * 200, new HashMap<>());
    }

    @Override
    public JQuickPhysicalPlanNode clone() {
        List<JQuickExpression> clonedKeys = groupKeys.stream().map(JQuickExpression::clone).collect(Collectors.toList());
        List<AggregateFunction> clonedAggs = aggregates.stream().map(AggregateFunction::clone).collect(Collectors.toList());
        JQuickExpression clonedHaving = havingCondition != null ? havingCondition.clone() : null;
        return new JQuickHashAggregatePhysicalNode(clonedKeys, clonedAggs, getChild().clone(), clonedHaving, stage);
    }

    @Override
    public void accept(JQuickPhysicalPlanVisitor visitor) {
        visitor.visit(this);
    }


    public List<JQuickExpression> getGroupKeys() { return groupKeys; }

    public List<AggregateFunction> getAggregates() { return aggregates; }

    public JQuickExpression getHavingCondition() { return havingCondition; }

    public AggregateStage getStage() { return stage; }

    public List<GroupingSet> getGroupingSets() { return groupingSets; }

    /**
     * 获取子节点（聚合节点的第一个子节点）
     */
    public JQuickPhysicalPlanNode getChild() {
        return children.isEmpty() ? null : children.get(0);
    }
}