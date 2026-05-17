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


import com.github.paohaijiao.physical.JQuickPhysicalPlanNode;
import com.github.paohaijiao.physical.JQuickPhysicalPlanVisitor;
import com.github.paohaijiao.physical.domain.JQuickPhysicalColumn;
import com.github.paohaijiao.physical.domain.JQuickPhysicalStats;

import java.util.Collections;
import java.util.List;

public class JQuickEmptyPhysicalNode implements JQuickPhysicalPlanNode {

    public static final JQuickEmptyPhysicalNode INSTANCE = new JQuickEmptyPhysicalNode();

    private JQuickEmptyPhysicalNode() {}

    @Override
    public String getNodeType() {
        return "Empty";
    }

    @Override
    public List<JQuickPhysicalPlanNode> getChildren() {
        return Collections.emptyList();
    }

    @Override
    public List<JQuickPhysicalColumn> getOutputSchema() {
        return Collections.emptyList();
    }

    @Override
    public JQuickPhysicalStats getStats() {
        return JQuickPhysicalStats.empty();
    }

    @Override
    public JQuickPhysicalPlanNode clone() {
        return INSTANCE;
    }

    @Override
    public void accept(JQuickPhysicalPlanVisitor visitor) {
        visitor.visit(this);
    }
}
