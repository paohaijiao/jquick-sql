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

import com.github.paohaijiao.logic.JQuickLogicalPlanNode;
import com.github.paohaijiao.logic.JQuickLogicalPlanVisitor;

import java.util.*;

/**
 * CTE节点 - 描述 WITH 子句
 */
public class JQuickWithNode implements JQuickLogicalPlanNode {

    private final JQuickLogicalPlanNode child;

    private final Map<String, JQuickLogicalPlanNode> ctes;

    public JQuickWithNode(JQuickLogicalPlanNode child, Map<String, JQuickLogicalPlanNode> ctes) {
        this.child = child;
        this.ctes = Collections.unmodifiableMap(new LinkedHashMap<>(ctes));
    }

    @Override
    public String getNodeType() {
        return "With";
    }

    @Override
    public List<JQuickLogicalPlanNode> getChildren() {
        List<JQuickLogicalPlanNode> children = new ArrayList<>();
        children.addAll(ctes.values());
        children.add(child);
        return children;
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
        Map<String, JQuickLogicalPlanNode> clonedCtes = new LinkedHashMap<>();
        for (Map.Entry<String, JQuickLogicalPlanNode> entry : ctes.entrySet()) {
            clonedCtes.put(entry.getKey(), entry.getValue().clone());
        }
        return new JQuickWithNode(child.clone(), clonedCtes);
    }

    public JQuickLogicalPlanNode getChild() { return child; }

    public Map<String, JQuickLogicalPlanNode> getCtes() {
        return ctes;
    }
}
