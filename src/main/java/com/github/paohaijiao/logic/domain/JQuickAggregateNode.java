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
import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.logic.JQuickLogicalPlanNode;
import com.github.paohaijiao.logic.JQuickLogicalPlanVisitor;

import java.util.*;

/**
 * 聚合节点 - 支持 GROUPING SETS, ROLLUP, CUBE
 */
public class JQuickAggregateNode implements JQuickLogicalPlanNode {

    private final List<JQuickExpression> groupKeys;

    private final List<GroupingSet> groupingSets;

    private final List<AggregateFunction> aggregates;

    private final JQuickLogicalPlanNode child;

    private final JQuickExpression havingCondition;

    private final boolean distinct;


    public JQuickAggregateNode(List<JQuickExpression> groupKeys, List<AggregateFunction> aggregates, JQuickLogicalPlanNode child, JQuickExpression havingCondition, boolean distinct) {
        this.groupKeys = groupKeys != null ? new ArrayList<>(groupKeys) : null;
        this.groupingSets = null;
        this.aggregates = new ArrayList<>(aggregates);
        this.child = child;
        this.havingCondition = havingCondition;
        this.distinct = distinct;
    }

    public JQuickAggregateNode(List<GroupingSet> groupingSets, List<AggregateFunction> aggregates, JQuickLogicalPlanNode child, JQuickExpression havingCondition) {
        this.groupKeys = null;
        this.groupingSets = new ArrayList<>(groupingSets);
        this.aggregates = new ArrayList<>(aggregates);
        this.child = child;
        this.havingCondition = havingCondition;
        this.distinct = false;
    }

    @Override
    public String getNodeType() {
        return "Aggregate";
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
        List<String> columns = new ArrayList<>();
        if (groupKeys != null) {
            for (int i = 0; i < groupKeys.size(); i++) {
                columns.add("group_" + i);
            }
        }
        for (AggregateFunction agg : aggregates) {
            columns.add(agg.getAlias());
        }
        return columns;
    }

    @Override
    public JQuickLogicalPlanNode clone() {
        if (groupKeys != null) {
            List<JQuickExpression> clonedKeys = new ArrayList<>();
            for (JQuickExpression key : groupKeys) {
                clonedKeys.add(key.clone());
            }
            List<AggregateFunction> clonedAggs = new ArrayList<>();
            for (AggregateFunction agg : aggregates) {
                clonedAggs.add(agg.clone());
            }
            JQuickExpression clonedHaving = havingCondition != null ? havingCondition.clone() : null;
            return new JQuickAggregateNode(clonedKeys, clonedAggs, child.clone(), clonedHaving, distinct);
        } else {
            List<GroupingSet> clonedSets = new ArrayList<>();
            for (GroupingSet gs : groupingSets) {
                clonedSets.add(gs.clone());
            }
            List<AggregateFunction> clonedAggs = new ArrayList<>();
            for (AggregateFunction agg : aggregates) {
                clonedAggs.add(agg.clone());
            }
            JQuickExpression clonedHaving = havingCondition != null ? havingCondition.clone() : null;
            return new JQuickAggregateNode(clonedSets, clonedAggs, child.clone(), clonedHaving);
        }
    }

    public List<JQuickExpression> getGroupKeys() { return groupKeys; }

    public List<GroupingSet> getGroupingSets() { return groupingSets; }

    public List<AggregateFunction> getAggregates() { return aggregates; }

    public JQuickLogicalPlanNode getChild() { return child; }

    public JQuickExpression getHavingCondition() { return havingCondition; }

    public boolean isDistinct() { return distinct; }

    /**
     * 聚合函数
     */
    public static class AggregateFunction {

        private final String functionName;

        private final JQuickExpression argument;

        private final boolean distinct;

        private final String alias;

        private final boolean countStar;

        private final String separator;

        public AggregateFunction(String functionName, JQuickExpression argument, boolean distinct, String alias) {
            this(functionName, argument, distinct, alias, false, null);
        }

        public AggregateFunction(String functionName, JQuickExpression argument, boolean distinct, String alias, boolean countStar, String separator) {
            this.functionName = functionName;
            this.argument = argument;
            this.distinct = distinct;
            this.alias = alias;
            this.countStar = countStar;
            this.separator = separator;
        }

        public String getFunctionName() { return functionName; }

        public JQuickExpression getArgument() { return argument; }

        public boolean isDistinct() { return distinct; }

        public String getAlias() { return alias; }

        public boolean isCountStar() { return countStar; }

        public String getSeparator() { return separator; }

        public AggregateFunction clone() {
            return new AggregateFunction(functionName, argument != null ? argument.clone() : null, distinct, alias, countStar, separator);
        }
    }

    /**
     * 分组集
     */
    public static class GroupingSet {

        private final List<JQuickExpression> keys;

        private final GroupingSetType type;

        public enum GroupingSetType {
            SIMPLE, ROLLUP, CUBE, GROUPING_SET
        }

        public GroupingSet(List<JQuickExpression> keys, GroupingSetType type) {
            this.keys = new ArrayList<>(keys);
            this.type = type;
        }

        public List<JQuickExpression> getKeys() { return keys; }

        public GroupingSetType getType() { return type; }

        public GroupingSet clone() {
            List<JQuickExpression> clonedKeys = new ArrayList<>();
            for (JQuickExpression key : keys) {
                clonedKeys.add(key.clone());
            }
            return new GroupingSet(clonedKeys, type);
        }
    }
}
