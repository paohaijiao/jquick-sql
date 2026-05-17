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
import com.github.paohaijiao.physical.domain.JQuickPhysicalStats;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public abstract class JQuickAbstractPhysicalNode implements JQuickPhysicalPlanNode {

    protected final List<JQuickPhysicalPlanNode> children = new ArrayList<>();

    protected JQuickAbstractPhysicalNode(JQuickPhysicalPlanNode... children) {
        if (children != null) {
            this.children.addAll(Arrays.asList(children));
        }
    }

    protected JQuickAbstractPhysicalNode(List<? extends JQuickPhysicalPlanNode> children) {
        if (children != null) {
            this.children.addAll(children);
        }
    }

    @Override
    public List<JQuickPhysicalPlanNode> getChildren() {
        return Collections.unmodifiableList(children);
    }

    @Override
    public JQuickPhysicalStats getStats() {
        return JQuickPhysicalStats.empty();
    }

    /**
     * 获取第一个子节点（便捷方法）
     */
    public JQuickPhysicalPlanNode getChild() {
        return children.isEmpty() ? null : children.get(0);
    }

    /**
     * 获取左子节点（用于Join等二元操作）
     */
    public JQuickPhysicalPlanNode getLeft() {
        return children.size() > 0 ? children.get(0) : null;
    }

    /**
     * 获取右子节点（用于Join等二元操作）
     */
    public JQuickPhysicalPlanNode getRight() {
        return children.size() > 1 ? children.get(1) : null;
    }
}
