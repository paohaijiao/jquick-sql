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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 投影节点 - 描述 SELECT 子句
 */
public class JQuickProjectNode implements JQuickLogicalPlanNode {

    private final List<SelectItem> selectItems;

    private final JQuickLogicalPlanNode child;


    private final boolean distinct;

    public JQuickProjectNode(List<SelectItem> selectItems, JQuickLogicalPlanNode child) {
        this(selectItems, child, false);
    }

    public JQuickProjectNode(List<SelectItem> selectItems, JQuickLogicalPlanNode child, boolean distinct) {
        this.selectItems = Collections.unmodifiableList(new ArrayList<>(selectItems));
        this.child = child;
        this.distinct = distinct;
    }

    @Override
    public String getNodeType() {
        return "Project";
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
        return selectItems.stream()
                .filter(item -> !item.isStar())
                .map(SelectItem::getAlias)
                .collect(Collectors.toList());
    }

    @Override
    public JQuickLogicalPlanNode clone() {
        List<SelectItem> clonedItems = new ArrayList<>();
        for (SelectItem item : selectItems) {
            clonedItems.add(item.clone());
        }
        return new JQuickProjectNode(clonedItems, child.clone(), distinct);
    }
    public List<SelectItem> getSelectItems() { return selectItems; }

    public JQuickLogicalPlanNode getChild() { return child; }

    public boolean isDistinct() { return distinct; }

    /**
     * 选择项
     */
    public static class SelectItem {

        private final JQuickExpression expression;

        private final String alias;

        private final boolean isStar;

        public SelectItem(JQuickExpression expression, String alias) {
            this.expression = expression;
            this.alias = alias != null ? alias : generateAlias(expression);
            this.isStar = false;
        }

        private SelectItem(boolean isStar) {
            this.expression = null;
            this.alias = "*";
            this.isStar = isStar;
        }

        public static SelectItem star() {
            return new SelectItem(true);
        }

        private String generateAlias(JQuickExpression expr) {
            String sql = expr.toSql();
            if (sql.length() <= 20) {
                return sql;
            }
            return "col_" + Math.abs(sql.hashCode());
        }

        public JQuickExpression getExpression() { return expression; }

        public String getAlias() { return alias; }

        public boolean isStar() { return isStar; }

        public SelectItem clone() {
            if (isStar) {
                return star();
            }
            return new SelectItem(expression.clone(), alias);
        }
    }
}
