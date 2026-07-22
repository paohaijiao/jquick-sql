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

import com.github.paohaijiao.enums.JQuickJoinType;
import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.logic.JQuickLogicalPlanNode;
import com.github.paohaijiao.logic.JQuickLogicalPlanVisitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * packageName com.github.paohaijiao.logic.domain
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/5/17
 */
public class JQuickJoinNode implements JQuickLogicalPlanNode {

    private final JQuickJoinType joinType;

    private final JQuickLogicalPlanNode left;

    private final JQuickLogicalPlanNode right;

    private final JQuickExpression condition;

    private final List<JoinKey> joinKeys;

    public static class JoinKey {

        private final JQuickExpression leftKey;

        private final JQuickExpression rightKey;

        public JoinKey(JQuickExpression leftKey, JQuickExpression rightKey) {
            this.leftKey = leftKey;
            this.rightKey = rightKey;
        }

        public JQuickExpression getLeftKey() { return leftKey; }

        public JQuickExpression getRightKey() { return rightKey; }
    }

    public JQuickJoinNode(JQuickJoinType joinType, JQuickLogicalPlanNode left, JQuickLogicalPlanNode right, JQuickExpression condition, List<JoinKey> joinKeys) {
        this.joinType = joinType;
        this.left = left;
        this.right = right;
        this.condition = condition;
        this.joinKeys = joinKeys != null ? new ArrayList<>(joinKeys) : new ArrayList<>();
    }

    @Override
    public String getNodeType() {
        return "Join";
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
        List<String> columns = new ArrayList<>();
        columns.addAll(left.getOutputColumns());
        columns.addAll(right.getOutputColumns());
        return columns;
    }

    @Override
    public JQuickLogicalPlanNode clone() {
        List<JoinKey> clonedKeys = new ArrayList<>();
        if (joinKeys != null) {
            for (JoinKey key : joinKeys) {
                clonedKeys.add(new JoinKey(key.leftKey.clone(), key.rightKey.clone()));
            }
        }
        return new JQuickJoinNode(joinType, left.clone(), right.clone(),
                condition != null ? condition.clone() : null,
                clonedKeys.isEmpty() ? null : clonedKeys);
    }

    public JQuickJoinType getJoinType() { return joinType; }

    public JQuickLogicalPlanNode getLeft() { return left; }

    public JQuickLogicalPlanNode getRight() { return right; }

    public JQuickExpression getCondition() { return condition; }

    public List<JoinKey> getJoinKeys() { return joinKeys; }
}
