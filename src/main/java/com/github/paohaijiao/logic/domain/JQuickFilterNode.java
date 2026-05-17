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

import java.util.Collections;
import java.util.List;

/**
 * 过滤节点 - 描述 WHERE 子句
 */
public class JQuickFilterNode implements JQuickLogicalPlanNode {

    private final JQuickExpression predicate;

    private final JQuickLogicalPlanNode child;

    public JQuickFilterNode(JQuickExpression predicate, JQuickLogicalPlanNode child) {
        this.predicate = predicate;
        this.child = child;
    }

    @Override
    public String getNodeType() {
        return "Filter";
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
        return new JQuickFilterNode(predicate.clone(), child.clone());
    }

    public JQuickExpression getPredicate() { return predicate; }

    public JQuickLogicalPlanNode getChild() { return child; }
}
