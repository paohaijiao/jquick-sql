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

import com.github.paohaijiao.enums.JQuickJoinType;
import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.physical.JQuickPhysicalPlanNode;
import com.github.paohaijiao.physical.JQuickPhysicalPlanVisitor;
import com.github.paohaijiao.physical.domain.JQuickPhysicalColumn;
import com.github.paohaijiao.physical.domain.JQuickPhysicalStats;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JQuickHashJoinPhysicalNode extends JQuickAbstractPhysicalNode {

    private final JQuickJoinType joinType;

    private final JQuickExpression condition;

    private final List<JoinKeyPair> joinKeys;

    private final BuildSide buildSide;

    private final JoinDistribution distribution;

    public enum BuildSide { LEFT, RIGHT }

    public enum JoinDistribution {
        LOCAL, SHUFFLE_HASH, BROADCAST_HASH, PARTITIONED
    }

    public static class JoinKeyPair {

        private final JQuickExpression leftKey;

        private final JQuickExpression rightKey;

        public JoinKeyPair(JQuickExpression leftKey, JQuickExpression rightKey) {
            this.leftKey = leftKey;
            this.rightKey = rightKey;
        }

        public JQuickExpression getLeftKey() { return leftKey; }
        public JQuickExpression getRightKey() { return rightKey; }

        public JoinKeyPair clone() {
            return new JoinKeyPair(leftKey.clone(), rightKey.clone());
        }
    }

    public JQuickHashJoinPhysicalNode(JQuickJoinType joinType, JQuickPhysicalPlanNode left, JQuickPhysicalPlanNode right, JQuickExpression condition, List<JoinKeyPair> joinKeys, BuildSide buildSide, JoinDistribution distribution) {
        super(left, right);
        this.joinType = joinType;
        this.condition = condition;
        this.joinKeys = joinKeys != null ? new ArrayList<>(joinKeys) : new ArrayList<>();
        this.buildSide = buildSide;
        this.distribution = distribution;
    }

    @Override
    public String getNodeType() {
        return "HashJoin";
    }

    @Override
    public List<JQuickPhysicalColumn> getOutputSchema() {
        List<JQuickPhysicalColumn> schema = new ArrayList<>();
        schema.addAll(children.get(0).getOutputSchema());
        schema.addAll(children.get(1).getOutputSchema());
        return schema;
    }

    @Override
    public JQuickPhysicalStats getStats() {
        long leftRows = children.get(0).getStats().getEstimatedRowCount();
        long rightRows = children.get(1).getStats().getEstimatedRowCount();
        long estimatedRows = Math.min(leftRows, rightRows);
        return new JQuickPhysicalStats(estimatedRows, estimatedRows * 200, new HashMap<>());
    }

    @Override
    public JQuickPhysicalPlanNode clone() {
        List<JoinKeyPair> clonedKeys = joinKeys.stream()
                .map(JoinKeyPair::clone)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        return new JQuickHashJoinPhysicalNode(joinType,
                children.get(0).clone(), children.get(1).clone(),
                condition != null ? condition.clone() : null,
                clonedKeys, buildSide, distribution);
    }

    @Override
    public void accept(JQuickPhysicalPlanVisitor visitor) {
        visitor.visit(this);
    }

    public JQuickJoinType getJoinType() { return joinType; }

    public JQuickExpression getCondition() { return condition; }

    public List<JoinKeyPair> getJoinKeys() { return joinKeys; }

    public BuildSide getBuildSide() { return buildSide; }

    public JoinDistribution getDistribution() { return distribution; }

    public JQuickPhysicalPlanNode getLeft() { return children.get(0); }

    public JQuickPhysicalPlanNode getRight() { return children.get(1); }
}
