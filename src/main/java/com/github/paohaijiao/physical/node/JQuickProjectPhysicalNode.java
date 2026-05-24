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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class JQuickProjectPhysicalNode extends JQuickAbstractPhysicalNode {

    private final List<SelectItem> selectItems;

    private final boolean distinct;

    public static class SelectItem {

        private final JQuickExpression expression;

        private final String alias;

        public SelectItem(JQuickExpression expression, String alias) {
            this.expression = expression;
            this.alias = alias;
        }

        public JQuickExpression getExpression() { return expression; }

        public String getAlias() { return alias; }

        public SelectItem clone() {
            return new SelectItem(expression.clone(), alias);
        }
    }

    public JQuickProjectPhysicalNode(List<SelectItem> selectItems, JQuickPhysicalPlanNode child, boolean distinct) {
        super(child);
        this.selectItems = new ArrayList<>(selectItems);
        this.distinct = distinct;
    }

    @Override
    public String getNodeType() {
        return "Project";
    }

    @Override
    public List<JQuickPhysicalColumn> getOutputSchema() {
        return selectItems.stream()
                .map(item -> new JQuickPhysicalColumn(item.getAlias(), Object.class, null, true))
                .collect(Collectors.toList());
    }

    @Override
    public JQuickPhysicalPlanNode clone() {
        List<SelectItem> clonedItems = selectItems.stream().map(SelectItem::clone)
                .collect(Collectors.toList());
        return new JQuickProjectPhysicalNode(clonedItems, children.get(0).clone(), distinct);
    }

    @Override
    public void accept(JQuickPhysicalPlanVisitor visitor) {
        visitor.visit(this);
    }

    public List<SelectItem> getSelectItems() { return selectItems; }
    public boolean isDistinct() { return distinct; }
}
