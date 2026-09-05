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


import com.github.paohaijiao.enums.JQuickSQLOperationType;
import com.github.paohaijiao.logic.JQuickLogicalPlanNode;
import com.github.paohaijiao.logic.JQuickLogicalPlanVisitor;

import java.util.Arrays;
import java.util.List;

/**
 * 集合操作节点 - 描述 UNION, INTERSECT, EXCEPT
 */
public class JQuickSetOperationNode implements JQuickLogicalPlanNode {


    private final JQuickSQLOperationType operationType;

    private final JQuickLogicalPlanNode left;

    private final JQuickLogicalPlanNode right;

    public JQuickSetOperationNode(JQuickSQLOperationType operationType, JQuickLogicalPlanNode left, JQuickLogicalPlanNode right) {
        this.operationType = operationType;
        this.left = left;
        this.right = right;
    }

    @Override
    public String getNodeType() {
        return "SetOperation";
    }

    @Override
    public List<JQuickLogicalPlanNode> getChildren() {
        return Arrays.asList(left, right);
    }

    @Override
    public void accept(JQuickLogicalPlanVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public List<String> getOutputColumns() {
        return left.getOutputColumns();
    }

    @Override
    public JQuickLogicalPlanNode clone() {
        return new JQuickSetOperationNode(operationType, left.clone(), right.clone());
    }

    public JQuickSQLOperationType getOperationType() { return operationType; }

    public JQuickLogicalPlanNode getLeft() { return left; }

    public JQuickLogicalPlanNode getRight() { return right; }
}
