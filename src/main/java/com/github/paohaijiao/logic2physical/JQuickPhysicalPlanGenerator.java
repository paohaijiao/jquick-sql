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
package com.github.paohaijiao.logic2physical;

import com.github.paohaijiao.enums.JQuickBinaryOperator;
import com.github.paohaijiao.enums.JQuickJoinType;
import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.expression.domain.JQuickBinaryExpression;
import com.github.paohaijiao.logic.JQuickLogicalPlanNode;
import com.github.paohaijiao.logic.JQuickLogicalPlanVisitor;
import com.github.paohaijiao.logic.domain.*;
import com.github.paohaijiao.optimizer.JQuickPhysicalPlanOptimizer;
import com.github.paohaijiao.physical.JQuickPhysicalPlanNode;
import com.github.paohaijiao.physical.node.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JQuickPhysicalPlanGenerator implements JQuickLogicalPlanVisitor {

    private final Map<JQuickLogicalPlanNode, JQuickPhysicalPlanNode> logicalToPhysical = new HashMap<>();

    private final JQuickPhysicalPlanOptimizer optimizer;

    public JQuickPhysicalPlanGenerator() {
        this.optimizer = new JQuickPhysicalPlanOptimizer();
    }

    public JQuickPhysicalPlanNode generate(JQuickLogicalPlanNode logicalPlan) {
        logicalPlan.accept(this);
        JQuickPhysicalPlanNode physicalPlan = logicalToPhysical.get(logicalPlan);
        if (physicalPlan != null) {
            physicalPlan = optimizer.optimize(physicalPlan);
        }
        return physicalPlan;
    }

    @Override
    public void visit(JQuickTableScanNode node) {
        JQuickTableScanPhysicalNode physicalNode = new JQuickTableScanPhysicalNode(node.getTableName(), node.getAlias(), node.getRequiredColumns(), node.getFilterPredicate());
        logicalToPhysical.put(node, physicalNode);
    }

    @Override
    public void visit(JQuickProjectNode node) {
        node.getChild().accept(this);
        JQuickPhysicalPlanNode childPhysical = logicalToPhysical.get(node.getChild());
        List<JQuickProjectPhysicalNode.SelectItem> items = new ArrayList<>();
        for (JQuickProjectNode.SelectItem item : node.getSelectItems()) {
            items.add(new JQuickProjectPhysicalNode.SelectItem(item.getExpression(), item.getAlias()));
        }
        JQuickProjectPhysicalNode physicalNode = new JQuickProjectPhysicalNode(items, childPhysical, node.isDistinct());
        logicalToPhysical.put(node, physicalNode);
    }

    @Override
    public void visit(JQuickFilterNode node) {
        node.getChild().accept(this);
        JQuickPhysicalPlanNode childPhysical = logicalToPhysical.get(node.getChild());
        JQuickFilterPhysicalNode physicalNode = new JQuickFilterPhysicalNode(node.getPredicate(), childPhysical);
        logicalToPhysical.put(node, physicalNode);
    }

    @Override
    public void visit(JQuickJoinNode node) {
        node.getLeft().accept(this);
        node.getRight().accept(this);
        JQuickPhysicalPlanNode leftPhysical = logicalToPhysical.get(node.getLeft());
        JQuickPhysicalPlanNode rightPhysical = logicalToPhysical.get(node.getRight());
        JQuickPhysicalPlanNode physicalNode;
        if (isHashJoinApplicable(node)) {
            List<JQuickHashJoinPhysicalNode.JoinKeyPair> joinKeys = extractJoinKeys(node.getCondition());
            JQuickHashJoinPhysicalNode.BuildSide buildSide = determineBuildSide(leftPhysical, rightPhysical);
            physicalNode = new JQuickHashJoinPhysicalNode(node.getJoinType(), leftPhysical, rightPhysical, node.getCondition(), joinKeys, buildSide, JQuickHashJoinPhysicalNode.JoinDistribution.SHUFFLE_HASH
            );
        } else {
            physicalNode = new JQuickNestedLoopJoinPhysicalNode(node.getJoinType(), leftPhysical, rightPhysical, node.getCondition());
        }
        logicalToPhysical.put(node, physicalNode);
    }

    @Override
    public void visit(JQuickGroupByNode node) {
        node.getChild().accept(this);
        JQuickPhysicalPlanNode childPhysical = logicalToPhysical.get(node.getChild());
        List<JQuickHashAggregatePhysicalNode.AggregateFunction> aggregates = new ArrayList<>();
        for (JQuickGroupByNode.AggregateItem item : node.getAggregateItems()) {
            aggregates.add(new JQuickHashAggregatePhysicalNode.AggregateFunction(item.getFunctionName(), item.getExpression(), false, item.getAlias(), item.isCountStar(), null, JQuickHashAggregatePhysicalNode.AggregateStage.SINGLE));
        }
        JQuickHashAggregatePhysicalNode physicalNode = new JQuickHashAggregatePhysicalNode(node.getGroupKeys(), aggregates, childPhysical, node.getHavingCondition(), JQuickHashAggregatePhysicalNode.AggregateStage.SINGLE);
        logicalToPhysical.put(node, physicalNode);
    }

    @Override
    public void visit(JQuickSortNode node) {
        node.getChild().accept(this);
        JQuickPhysicalPlanNode childPhysical = logicalToPhysical.get(node.getChild());
        List<JQuickSortPhysicalNode.OrderByItem> items = new ArrayList<>();
        for (JQuickSortNode.OrderByItem item : node.getOrderByItems()) {
            items.add(new JQuickSortPhysicalNode.OrderByItem(item.getColumnName(), item.isAscending(), item.isNullsFirst()));
        }
        JQuickSortPhysicalNode physicalNode = new JQuickSortPhysicalNode(items, childPhysical);
        logicalToPhysical.put(node, physicalNode);
    }

    @Override
    public void visit(JQuickLimitNode node) {
        node.getChild().accept(this);
        JQuickPhysicalPlanNode childPhysical = logicalToPhysical.get(node.getChild());
        JQuickLimitPhysicalNode physicalNode = new JQuickLimitPhysicalNode(node.getLimit(), node.getOffset(), childPhysical);
        logicalToPhysical.put(node, physicalNode);
    }

    @Override
    public void visit(JQuickWindowNode node) {
        node.getChild().accept(this);
        JQuickPhysicalPlanNode childPhysical = logicalToPhysical.get(node.getChild());
        List<JQuickWindowPhysicalNode.WindowFunction> functions = new ArrayList<>();
        for (JQuickWindowNode.WindowFunction wf : node.getWindowFunctions()) {
            JQuickWindowPhysicalNode.WindowSpec spec = convertWindowSpec(wf.getWindowSpec());
            functions.add(new JQuickWindowPhysicalNode.WindowFunction(wf.getFunctionName(), wf.getArgument(), spec, wf.getAlias()));
        }
        JQuickWindowPhysicalNode physicalNode = new JQuickWindowPhysicalNode(functions, childPhysical);
        logicalToPhysical.put(node, physicalNode);
    }

    @Override
    public void visit(JQuickWithNode node) {
        for (JQuickLogicalPlanNode cte : node.getCtes().values()) {
            cte.accept(this);
        }
        node.getChild().accept(this);
        JQuickPhysicalPlanNode childPhysical = logicalToPhysical.get(node.getChild());
        logicalToPhysical.put(node, childPhysical);
    }

    @Override
    public void visit(JQuickSetOperationNode node) {
        node.getLeft().accept(this);
        node.getRight().accept(this);
        JQuickPhysicalPlanNode leftPhysical = logicalToPhysical.get(node.getLeft());
        JQuickPhysicalPlanNode rightPhysical = logicalToPhysical.get(node.getRight());
        JQuickSetOperationPhysicalNode physicalNode = new JQuickSetOperationPhysicalNode(node.getOperationType(), leftPhysical, rightPhysical);
        logicalToPhysical.put(node, physicalNode);
    }

    @Override
    public void visit(JQuickAggregateNode node) {
        node.getChild().accept(this);
        JQuickPhysicalPlanNode childPhysical = logicalToPhysical.get(node.getChild());
        List<JQuickHashAggregatePhysicalNode.AggregateFunction> aggregates = new ArrayList<>();
        for (JQuickAggregateNode.AggregateFunction agg : node.getAggregates()) {
            aggregates.add(new JQuickHashAggregatePhysicalNode.AggregateFunction(agg.getFunctionName(), agg.getArgument(), agg.isDistinct(), agg.getAlias(), agg.isCountStar(), agg.getSeparator(), JQuickHashAggregatePhysicalNode.AggregateStage.SINGLE));
        }
        JQuickHashAggregatePhysicalNode physicalNode;
        if (node.getGroupKeys() != null) {
            physicalNode = new JQuickHashAggregatePhysicalNode(node.getGroupKeys(), aggregates, childPhysical, node.getHavingCondition(), JQuickHashAggregatePhysicalNode.AggregateStage.SINGLE);
        } else {
            physicalNode = new JQuickHashAggregatePhysicalNode(new ArrayList<>(), aggregates, childPhysical, node.getHavingCondition(), JQuickHashAggregatePhysicalNode.AggregateStage.SINGLE);
        }
        logicalToPhysical.put(node, physicalNode);
    }

    @Override
    public void visit(JQuickValuesNode node) {
        JQuickValuesPhysicalNode physicalNode = new JQuickValuesPhysicalNode(node.getRows(), node.getColumnNames(), node.getColumnTypes());
        logicalToPhysical.put(node, physicalNode);
    }

    @Override
    public void visit(JQuickEmptyNode node) {
        logicalToPhysical.put(node, JQuickEmptyPhysicalNode.INSTANCE);
    }

    @Override
    public void visit(JQuickRecursiveUnionNode node) {
        if (node.getInitialPlan() != null) {
            node.getInitialPlan().accept(this);
        }
        if (node.getRecursivePlan() != null) {
            node.getRecursivePlan().accept(this);
        }
        JQuickPhysicalPlanNode initialPhysical = node.getInitialPlan() != null
                ? logicalToPhysical.get(node.getInitialPlan())
                : null;
        JQuickPhysicalPlanNode recursivePhysical = node.getRecursivePlan() != null
                ? logicalToPhysical.get(node.getRecursivePlan())
                : null;
        JQuickRecursiveUnionPhysicalNode physicalNode = new JQuickRecursiveUnionPhysicalNode(
                node.getCteName(),
                node.getColumnNames(),
                initialPhysical,
                recursivePhysical,
                node.isUnionAll()
        );
        logicalToPhysical.put(node, physicalNode);
    }

    private boolean isHashJoinApplicable(JQuickJoinNode join) {
        JQuickExpression condition = join.getCondition();
        if (condition == null) return false;
        return hasEqualityCondition(condition);
    }

    private boolean hasEqualityCondition(JQuickExpression expr) {
        if (expr instanceof JQuickBinaryExpression) {
            JQuickBinaryExpression binary = (JQuickBinaryExpression) expr;
            if (binary.getOperator() == JQuickBinaryOperator.EQ) {
                return true;
            }
            if (binary.getOperator() == JQuickBinaryOperator.AND) {
                return hasEqualityCondition(binary.getLeft()) || hasEqualityCondition(binary.getRight());
            }
        }
        return false;
    }

    private List<JQuickHashJoinPhysicalNode.JoinKeyPair> extractJoinKeys(JQuickExpression condition) {
        List<JQuickHashJoinPhysicalNode.JoinKeyPair> keys = new ArrayList<>();
        extractEqualityConditions(condition, keys);
        return keys;
    }

    private void extractEqualityConditions(JQuickExpression expr, List<JQuickHashJoinPhysicalNode.JoinKeyPair> keys) {
        if (expr instanceof JQuickBinaryExpression) {
            JQuickBinaryExpression binary = (JQuickBinaryExpression) expr;
            if (binary.getOperator() == JQuickBinaryOperator.EQ) {
                keys.add(new JQuickHashJoinPhysicalNode.JoinKeyPair(binary.getLeft(), binary.getRight()));
            } else if (binary.getOperator() == JQuickBinaryOperator.AND) {
                extractEqualityConditions(binary.getLeft(), keys);
                extractEqualityConditions(binary.getRight(), keys);
            }
        }
    }

    private JQuickHashJoinPhysicalNode.BuildSide determineBuildSide(JQuickPhysicalPlanNode left, JQuickPhysicalPlanNode right) {
        long leftRows = left.getStats().getEstimatedRowCount();
        long rightRows = right.getStats().getEstimatedRowCount();
        return leftRows <= rightRows ? JQuickHashJoinPhysicalNode.BuildSide.LEFT : JQuickHashJoinPhysicalNode.BuildSide.RIGHT;
    }



    private JQuickWindowPhysicalNode.WindowSpec convertWindowSpec(JQuickWindowNode.WindowSpec spec) {
        List<JQuickSortPhysicalNode.OrderByItem> orderKeys = new ArrayList<>();
        if (spec.getOrderKeys() != null) {
            for (JQuickSortNode.OrderByItem item : spec.getOrderKeys()) {
                orderKeys.add(new JQuickSortPhysicalNode.OrderByItem(item.getColumnName(), item.isAscending(), item.isNullsFirst()));
            }
        }
        return new JQuickWindowPhysicalNode.WindowSpec(spec.getPartitionKeys(), orderKeys, null);
    }
}