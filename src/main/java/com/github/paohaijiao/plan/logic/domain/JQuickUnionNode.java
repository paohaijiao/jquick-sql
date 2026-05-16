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
package com.github.paohaijiao.plan.logic.domain;

/**
 * packageName com.github.paohaijiao.plan.logic.domain
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/5/16
 */

import com.github.paohaijiao.executor.JQuickExecutionContext;
import com.github.paohaijiao.plan.logic.JQuickLogicalPlanNode;
import com.github.paohaijiao.plan.logic.JQuickLogicalPlanVisitor;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 并集节点 - UNION / UNION ALL
 *
 * UNION: 合并两个或多个查询结果，并去重
 * UNION ALL: 合并两个或多个查询结果，保留重复
 */
public class JQuickUnionNode implements JQuickLogicalPlanNode {

    private final List<JQuickLogicalPlanNode> children;
    private final boolean distinct;  // true: UNION, false: UNION ALL

    public JQuickUnionNode(List<JQuickLogicalPlanNode> children, boolean distinct) {
        this.children = Collections.unmodifiableList(new ArrayList<>(children));
        this.distinct = distinct;
    }

    public JQuickUnionNode(JQuickLogicalPlanNode left, JQuickLogicalPlanNode right, boolean distinct) {
        this(Arrays.asList(left, right), distinct);
    }

    @Override
    public JQuickDataSet execute(JQuickExecutionContext context) {
        if (children.isEmpty()) {
            return new JQuickDataSet(new ArrayList<>(), new ArrayList<>());
        }

        // 执行所有子查询
        List<JQuickDataSet> results = new ArrayList<>();
        for (JQuickLogicalPlanNode child : children) {
            results.add(child.execute(context));
        }

        // 合并所有行
        List<JQuickRow> combinedRows = new ArrayList<>();
        for (JQuickDataSet data : results) {
            combinedRows.addAll(data.getRows());
        }

        // 去重（如果需要）
        if (distinct) {
            combinedRows = combinedRows.stream().distinct().collect(Collectors.toList());
        }
        // 使用第一个结果的列元数据（假设所有结果列结构相同）
        JQuickDataSet firstResult = results.get(0);
        return new JQuickDataSet(firstResult.getColumns(), combinedRows);
    }

    @Override
    public String getNodeType() {
        return distinct ? "Union" : "UnionAll";
    }

    @Override
    public List<JQuickLogicalPlanNode> getChildren() {
        return children;
    }

    @Override
    public void accept(JQuickLogicalPlanVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public List<String> getOutputColumns() {
        if (children.isEmpty()) {
            return Collections.emptyList();
        }
        return children.get(0).getOutputColumns();
    }

    @Override
    public JQuickLogicalPlanNode clone() {
        List<JQuickLogicalPlanNode> clonedChildren = new ArrayList<>();
        for (JQuickLogicalPlanNode child : children) {
            clonedChildren.add(child.clone());
        }
        return new JQuickUnionNode(clonedChildren, distinct);
    }

    public boolean isDistinct() { return distinct; }
    public boolean isUnionAll() { return !distinct; }
}
