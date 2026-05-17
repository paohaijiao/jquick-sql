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

import com.github.paohaijiao.context.JQuickExecutionContext;
import com.github.paohaijiao.logic.JQuickLogicalPlanNode;
import com.github.paohaijiao.logic.JQuickLogicalPlanVisitor;
import com.github.paohaijiao.statement.JQuickDataSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 空节点 - 表示空数据集（用于优化器替换）
 */
public class JQuickEmptyNode implements JQuickLogicalPlanNode {

    @Override
    public JQuickDataSet execute(JQuickExecutionContext context) {
        return new JQuickDataSet(new ArrayList<>(), new ArrayList<>());
    }

    @Override
    public String getNodeType() {
        return "Empty";
    }

    @Override
    public List<JQuickLogicalPlanNode> getChildren() {
        return Collections.emptyList();
    }

    @Override
    public void accept(JQuickLogicalPlanVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public List<String> getOutputColumns() {
        return Collections.emptyList();
    }

    @Override
    public JQuickLogicalPlanNode clone() {
        return new JQuickEmptyNode();
    }
}
