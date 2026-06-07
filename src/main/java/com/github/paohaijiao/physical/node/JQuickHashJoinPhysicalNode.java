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

import com.github.paohaijiao.datasource.JQuickDataSourceManager;
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
        LOCAL,//连接表数据都和主表保持一致
        SHUFFLE_HASH,//两表都大且未预分区
        BROADCAST_HASH,// 小表广播到所有节点，大表原地不动
        PARTITIONED // 两表分区键不同但有对应关系
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
    public JQuickPhysicalPlanNode clone() {
        List<JoinKeyPair> clonedKeys = joinKeys.stream().map(JoinKeyPair::clone).collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        return new JQuickHashJoinPhysicalNode(joinType, children.get(0).clone(), children.get(1).clone(), condition != null ? condition.clone() : null, clonedKeys, buildSide, distribution);
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

    @Override
    public JQuickPhysicalStats getStats() {
        JQuickPhysicalPlanNode left = getLeft();
        JQuickPhysicalPlanNode right = getRight();
        if (left == null || right == null) {
            return JQuickPhysicalStats.empty();
        }
        JQuickPhysicalStats leftStats = left.getStats();
        JQuickPhysicalStats rightStats = right.getStats();
        long leftRows = leftStats.getEstimatedRowCount();
        long rightRows = rightStats.getEstimatedRowCount();
        double selectivity = estimateJoinSelectivity();
        long estimatedRows;
        switch (joinType) {
            case INNER:
                estimatedRows = (long) (Math.min(leftRows, rightRows) * selectivity);
                break;
            case LEFT:
                estimatedRows = (long) (leftRows + (rightRows * selectivity));
                break;
            case RIGHT:
                estimatedRows = (long) (rightRows + (leftRows * selectivity));
                break;
            case FULL:
                estimatedRows = (long) (leftRows + rightRows - (Math.min(leftRows, rightRows) * selectivity));
                break;
            case CROSS:
                estimatedRows = leftRows * rightRows;
                break;
            default:
                estimatedRows = (long) (Math.min(leftRows, rightRows) * selectivity);
        }
        if (joinType == JQuickJoinType.CROSS && estimatedRows > 10000000) {
            estimatedRows = 10000000;
        }
        long estimatedDataSize = estimatedRows * 200;
        return new JQuickPhysicalStats(estimatedRows, estimatedDataSize, new HashMap<>());
    }
    /**
     * 估算 Join 选择性（匹配比例）
     */
    private double estimateJoinSelectivity() {
        if (condition != null) {
            String cond = condition.toString().toLowerCase();
            if (cond.contains("=")) {
                return 0.1;
            }
            if (cond.contains(">") || cond.contains("<")) {
                return 0.3;
            }
        }
        if (joinKeys != null && !joinKeys.isEmpty()) {
            return 0.1;
        }
        return 1.0;
    }
}
