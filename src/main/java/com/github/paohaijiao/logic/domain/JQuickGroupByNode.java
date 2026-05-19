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
/**
 * packageName com.github.paohaijiao.logic.domain
 * @author Martin
 * @version 1.0.0
 * @since 2026/5/17
 */

import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.logic.JQuickLogicalPlanNode;
import com.github.paohaijiao.logic.JQuickLogicalPlanVisitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 分组聚合节点 - 描述 GROUP BY 子句
 */
public class JQuickGroupByNode implements JQuickLogicalPlanNode {

    private final List<JQuickExpression> groupKeys;

    private final List<AggregateItem> aggregateItems;

    private final JQuickLogicalPlanNode child;

    private final JQuickExpression havingCondition;

    public JQuickGroupByNode(List<JQuickExpression> groupKeys, List<AggregateItem> aggregateItems, JQuickLogicalPlanNode child, JQuickExpression havingCondition) {
        this.groupKeys = Collections.unmodifiableList(new ArrayList<>(groupKeys));
        this.aggregateItems = Collections.unmodifiableList(new ArrayList<>(aggregateItems));
        this.child = child;
        this.havingCondition = havingCondition;
    }

    @Override
    public String getNodeType() {
        return "GroupBy";
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
        for (int i = 0; i < groupKeys.size(); i++) {
            columns.add("group_" + i);
        }
        for (AggregateItem item : aggregateItems) {
            columns.add(item.getAlias());
        }
        return columns;
    }

    @Override
    public JQuickLogicalPlanNode clone() {
        List<JQuickExpression> clonedKeys = new ArrayList<>();
        for (JQuickExpression key : groupKeys) {
            clonedKeys.add(key.clone());
        }
        List<AggregateItem> clonedItems = new ArrayList<>();
        for (AggregateItem item : aggregateItems) {
            clonedItems.add(item.clone());
        }
        JQuickExpression clonedHaving = havingCondition != null ? havingCondition.clone() : null;
        return new JQuickGroupByNode(clonedKeys, clonedItems, child.clone(), clonedHaving);
    }

    public List<JQuickExpression> getGroupKeys() { return groupKeys; }

    public List<AggregateItem> getAggregateItems() { return aggregateItems; }

    public JQuickLogicalPlanNode getChild() { return child; }

    public JQuickExpression getHavingCondition() { return havingCondition; }

    /**
     * 聚合项
     */
    public static class AggregateItem {

        private final JQuickExpression expression;

        private final String functionName;

        private final String alias;

        private final boolean isCountStar;

        public AggregateItem(JQuickExpression expression, String functionName, String alias) {
            this(expression, functionName, alias, false);
        }

        public AggregateItem(JQuickExpression expression, String functionName, String alias, boolean isCountStar) {
            this.expression = expression;
            this.functionName = functionName;
            this.alias = alias;
            this.isCountStar = isCountStar;
        }

        public JQuickExpression getExpression() { return expression; }

        public String getFunctionName() { return functionName; }

        public String getAlias() { return alias; }

        public boolean isCountStar() { return isCountStar; }

        public AggregateItem clone() {
            return new AggregateItem(expression.clone(), functionName, alias, isCountStar);
        }
    }
}
