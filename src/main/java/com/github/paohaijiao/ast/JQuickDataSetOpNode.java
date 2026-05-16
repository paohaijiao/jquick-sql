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

import java.util.List;

/**
 * datasetOp
 * : selectClause
 * | selectClause UNION selectClause
 * | selectClause MINUS selectClause
 * | selectClause INTERSECT selectClause
 */
public class JQuickDataSetOpNode implements JQuickASTNode {
    private final List<JQuickSelectClauseNode> selectClauses;
    private final List<SetOperator> operators;

    public JQuickDataSetOpNode(List<JQuickSelectClauseNode> selectClauses, List<SetOperator> operators) {
        this.selectClauses = selectClauses;
        this.operators = operators;
    }

    @Override
    public String getNodeType() {
        return "DataSetOp";
    }

    public List<JQuickSelectClauseNode> getSelectClauses() {
        return selectClauses;
    }

    public List<SetOperator> getOperators() {
        return operators;
    }

    public boolean isSingleSelect() {
        return selectClauses.size() == 1;
    }

    public enum SetOperator {
        UNION, MINUS, INTERSECT
    }
}
