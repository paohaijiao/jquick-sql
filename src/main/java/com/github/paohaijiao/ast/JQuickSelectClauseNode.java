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
package com.github.paohaijiao.ast;

import java.util.ArrayList;
import java.util.List;

/**
 * selectClause
 * : SELECT selectSpec? selectElements
 * fromClause
 * (joinClause)*
 * whereClause?
 * groupByClause?
 * havingClause?
 * orderByClause?
 * limitClause?
 */
public class JQuickSelectClauseNode implements JQuickASTNode {
    private final JQuickSelectSpecNode selectSpec;
    private final JQuickSelectElementsNode selectElements;
    private final JQuickFromClauseNode fromClause;
    private final List<JQuickJoinClauseNode> joinClauses;
    private final JQuickWhereClauseNode whereClause;
    private final JQuickGroupByClauseNode groupByClause;
    private final JQuickHavingClauseNode havingClause;
    private final JQuickOrderByClauseNode orderByClause;
    private final JQuickLimitClauseNode limitClause;

    private JQuickSelectClauseNode(Builder builder) {
        this.selectSpec = builder.selectSpec;
        this.selectElements = builder.selectElements;
        this.fromClause = builder.fromClause;
        this.joinClauses = builder.joinClauses;
        this.whereClause = builder.whereClause;
        this.groupByClause = builder.groupByClause;
        this.havingClause = builder.havingClause;
        this.orderByClause = builder.orderByClause;
        this.limitClause = builder.limitClause;
    }

    @Override
    public String getNodeType() {
        return "SelectClause";
    }

    public boolean isDistinct() {
        return selectSpec != null && selectSpec.isDistinct();
    }

    public JQuickSelectSpecNode getSelectSpec() {
        return selectSpec;
    }

    public JQuickSelectElementsNode getSelectElements() {
        return selectElements;
    }

    public JQuickFromClauseNode getFromClause() {
        return fromClause;
    }

    public List<JQuickJoinClauseNode> getJoinClauses() {
        return joinClauses;
    }

    public JQuickWhereClauseNode getWhereClause() {
        return whereClause;
    }

    public JQuickGroupByClauseNode getGroupByClause() {
        return groupByClause;
    }

    public JQuickHavingClauseNode getHavingClause() {
        return havingClause;
    }

    public JQuickOrderByClauseNode getOrderByClause() {
        return orderByClause;
    }

    public JQuickLimitClauseNode getLimitClause() {
        return limitClause;
    }

    public static class Builder {
        private JQuickSelectSpecNode selectSpec;
        private JQuickSelectElementsNode selectElements;
        private JQuickFromClauseNode fromClause;
        private List<JQuickJoinClauseNode> joinClauses = new ArrayList<>();
        private JQuickWhereClauseNode whereClause;
        private JQuickGroupByClauseNode groupByClause;
        private JQuickHavingClauseNode havingClause;
        private JQuickOrderByClauseNode orderByClause;
        private JQuickLimitClauseNode limitClause;

        public Builder setSelectSpec(JQuickSelectSpecNode selectSpec) {
            this.selectSpec = selectSpec;
            return this;
        }

        public Builder setSelectElements(JQuickSelectElementsNode selectElements) {
            this.selectElements = selectElements;
            return this;
        }

        public Builder setFromClause(JQuickFromClauseNode fromClause) {
            this.fromClause = fromClause;
            return this;
        }

        public Builder setJoinClauses(List<JQuickJoinClauseNode> joinClauses) {
            this.joinClauses = joinClauses;
            return this;
        }

        public Builder addJoinClause(JQuickJoinClauseNode joinClause) {
            this.joinClauses.add(joinClause);
            return this;
        }

        public Builder setWhereClause(JQuickWhereClauseNode whereClause) {
            this.whereClause = whereClause;
            return this;
        }

        public Builder setGroupByClause(JQuickGroupByClauseNode groupByClause) {
            this.groupByClause = groupByClause;
            return this;
        }

        public Builder setHavingClause(JQuickHavingClauseNode havingClause) {
            this.havingClause = havingClause;
            return this;
        }

        public Builder setOrderByClause(JQuickOrderByClauseNode orderByClause) {
            this.orderByClause = orderByClause;
            return this;
        }

        public Builder setLimitClause(JQuickLimitClauseNode limitClause) {
            this.limitClause = limitClause;
            return this;
        }

        public JQuickSelectClauseNode build() {
            return new JQuickSelectClauseNode(this);
        }
    }
}
