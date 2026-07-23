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

import java.util.*;

/**
 * 逻辑计划到物理计划转换器
 *
 */
public class JQuickPhysicalPlanGenerator implements JQuickLogicalPlanVisitor {

    private final Map<JQuickLogicalPlanNode, JQuickPhysicalPlanNode> logicalToPhysical = new HashMap<>();

    /**
     * CTE定义映射：CTE名称 -> 逻辑计划节点
     * 用于在转换主查询时解析CTE引用
     */
    private Map<String, JQuickLogicalPlanNode> cteDefinitions;

    /**
     * 是否是递归CTE（递归CTE不能内联）
     */
    private Set<String> recursiveCteNames;

    private final JQuickPhysicalPlanOptimizer optimizer;
    /**
     * 存储CTE物理计划（用于内联）
     */
    private Map<String, JQuickPhysicalPlanNode> ctePhysicalPlans = new HashMap<>();


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
        String tableName = node.getTableName();
        if (cteDefinitions != null && cteDefinitions.containsKey(tableName)) {
            JQuickLogicalPlanNode cteLogicalPlan = cteDefinitions.get(tableName);
            if (recursiveCteNames != null && recursiveCteNames.contains(tableName)) {
                if (!logicalToPhysical.containsKey(cteLogicalPlan)) {
                    cteLogicalPlan.accept(this);
                }
                JQuickPhysicalPlanNode ctePhysical = logicalToPhysical.get(cteLogicalPlan);
                logicalToPhysical.put(node, ctePhysical);
            } else {
                if (!logicalToPhysical.containsKey(cteLogicalPlan)) {
                    cteLogicalPlan.accept(this);
                }
                JQuickPhysicalPlanNode ctePhysical = logicalToPhysical.get(cteLogicalPlan);
                if (node.getRequiredColumns() != null && !node.getRequiredColumns().isEmpty()) {
                    ctePhysical = applyColumnPruning(ctePhysical, node.getRequiredColumns());
                }
                if (node.getFilterPredicate() != null) {
                    ctePhysical = new JQuickFilterPhysicalNode(node.getFilterPredicate(), ctePhysical);
                }
                logicalToPhysical.put(node, ctePhysical);
            }
        } else {
            JQuickTableScanPhysicalNode physicalNode = new JQuickTableScanPhysicalNode(node.getTableName(), node.getAlias(), node.getRequiredColumns(), node.getFilterPredicate());
            logicalToPhysical.put(node, physicalNode);
        }
    }

    @Override
    public void visit(JQuickProjectNode node) {
        node.getChild().accept(this);
        JQuickPhysicalPlanNode childPhysical = logicalToPhysical.get(node.getChild());
        List<JQuickProjectPhysicalNode.SelectItem> items = new ArrayList<>();
        boolean hasStar = false;
        for (JQuickProjectNode.SelectItem item : node.getSelectItems()) {
            if (item.isStar()) {
                hasStar = true;
            }
            items.add(new JQuickProjectPhysicalNode.SelectItem(item.getExpression(), item.getAlias()));
        }
        JQuickProjectPhysicalNode physicalNode = new JQuickProjectPhysicalNode(items, childPhysical, node.isDistinct(), hasStar);
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
        if (node.getJoinType() == JQuickJoinType.CROSS || isHashJoinApplicable(node)) {
            List<JQuickHashJoinPhysicalNode.JoinKeyPair> joinKeyPairs = convertJoinKeys(node.getJoinKeys());
            if (joinKeyPairs.isEmpty() && node.getCondition() != null) {
                joinKeyPairs = extractJoinKeys(node.getCondition());
            }
            JQuickHashJoinPhysicalNode.BuildSide buildSide = determineBuildSide(leftPhysical, rightPhysical);
            physicalNode = new JQuickHashJoinPhysicalNode(node.getJoinType(), leftPhysical, rightPhysical, node.getCondition(), joinKeyPairs, buildSide, JQuickHashJoinPhysicalNode.JoinDistribution.SHUFFLE_HASH);
        } else {
            physicalNode = new JQuickNestedLoopJoinPhysicalNode(node.getJoinType(), leftPhysical, rightPhysical, node.getCondition());
        }
        logicalToPhysical.put(node, physicalNode);
    }

    private List<JQuickHashJoinPhysicalNode.JoinKeyPair> convertJoinKeys(List<JQuickJoinNode.JoinKey> joinKeys) {
        List<JQuickHashJoinPhysicalNode.JoinKeyPair> result = new ArrayList<>();
        if (joinKeys == null || joinKeys.isEmpty()) {
            return result;
        }
        for (JQuickJoinNode.JoinKey key : joinKeys) {
            result.add(new JQuickHashJoinPhysicalNode.JoinKeyPair(key.getLeftKey(), key.getRightKey()));
        }
        return result;
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

    /**     *
     * CTE内联策略：
     * 1. 收集所有CTE定义（不包含递归CTE）
     * 2. 将主查询中的CTE引用替换为CTE定义的实际计划
     * 3. 递归CTE需要特殊处理，保留RecursiveUnion结构
     */
    @Override
    public void visit(JQuickWithNode node) {
        Map<String, JQuickLogicalPlanNode> allCtes = node.getCtes();
        Set<String> recursiveNames = new HashSet<>();
        Map<String, JQuickLogicalPlanNode> nonRecursiveCtes = new LinkedHashMap<>();
        for (Map.Entry<String, JQuickLogicalPlanNode> entry : allCtes.entrySet()) {
            String cteName = entry.getKey();
            JQuickLogicalPlanNode ctePlan = entry.getValue();
            if (ctePlan instanceof JQuickRecursiveUnionNode) {
                recursiveNames.add(cteName);
                if (!logicalToPhysical.containsKey(ctePlan)) {
                    ctePlan.accept(this);
                }
            } else {
                nonRecursiveCtes.put(cteName, ctePlan);
            }
        }
        Map<String, JQuickLogicalPlanNode> savedCteDefinitions = this.cteDefinitions;
        Set<String> savedRecursiveCteNames = this.recursiveCteNames;
        try {
            this.cteDefinitions = new LinkedHashMap<>();
            if (savedCteDefinitions != null) {
                this.cteDefinitions.putAll(savedCteDefinitions);
            }
            this.cteDefinitions.putAll(nonRecursiveCtes);
            this.recursiveCteNames = new HashSet<>();
            if (savedRecursiveCteNames != null) {
                this.recursiveCteNames.addAll(savedRecursiveCteNames);
            }
            this.recursiveCteNames.addAll(recursiveNames);
            for (Map.Entry<String, JQuickLogicalPlanNode> entry : nonRecursiveCtes.entrySet()) {
                String cteName = entry.getKey();
                JQuickLogicalPlanNode ctePlan = entry.getValue();
                if (!logicalToPhysical.containsKey(ctePlan)) {
                    ctePlan.accept(this);
                }
                JQuickPhysicalPlanNode ctePhysical = logicalToPhysical.get(ctePlan);
                storeCtePhysicalPlan(cteName, ctePhysical);
            }
            node.getChild().accept(this);
            JQuickPhysicalPlanNode childPhysical = logicalToPhysical.get(node.getChild());
            for (String recursiveName : recursiveNames) {
                JQuickLogicalPlanNode recursiveCtePlan = allCtes.get(recursiveName);
                if (recursiveCtePlan instanceof JQuickRecursiveUnionNode) {
                    JQuickRecursiveUnionNode recursiveNode = (JQuickRecursiveUnionNode) recursiveCtePlan;
                    if (!logicalToPhysical.containsKey(recursiveNode)) {
                        recursiveNode.accept(this);
                    }
                    JQuickPhysicalPlanNode recursivePhysical = logicalToPhysical.get(recursiveNode);
                    if (isCteReferencedInPlan(childPhysical, recursiveName)) {
                        childPhysical = replaceCteReference(childPhysical, recursiveName, recursivePhysical);
                    }
                }
            }
            logicalToPhysical.put(node, childPhysical);
        } finally {
            this.cteDefinitions = savedCteDefinitions;
            this.recursiveCteNames = savedRecursiveCteNames;
        }
    }


    private void storeCtePhysicalPlan(String cteName, JQuickPhysicalPlanNode plan) {
        ctePhysicalPlans.put(cteName, plan);
    }

    /**
     * 检查计划中是否引用了指定的CTE
     */
    private boolean isCteReferencedInPlan(JQuickPhysicalPlanNode plan, String cteName) {
        if (plan == null) return false;
        if (plan instanceof JQuickTableScanPhysicalNode) {
            JQuickTableScanPhysicalNode scan = (JQuickTableScanPhysicalNode) plan;
            return cteName.equals(scan.getTableName());
        }
        for (JQuickPhysicalPlanNode child : plan.getChildren()) {
            if (isCteReferencedInPlan(child, cteName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 替换计划中的CTE引用
     */
    private JQuickPhysicalPlanNode replaceCteReference(JQuickPhysicalPlanNode plan, String cteName, JQuickPhysicalPlanNode replacement) {
        if (plan == null) return null;
        if (plan instanceof JQuickTableScanPhysicalNode) {
            JQuickTableScanPhysicalNode scan = (JQuickTableScanPhysicalNode) plan;
            if (cteName.equals(scan.getTableName())) {
                return replacement;
            }
        }
        if (plan instanceof JQuickProjectPhysicalNode) {
            JQuickProjectPhysicalNode project = (JQuickProjectPhysicalNode) plan;
            JQuickPhysicalPlanNode newChild = replaceCteReference(project.getChildren().get(0), cteName, replacement);
            return new JQuickProjectPhysicalNode(project.getSelectItems(), newChild, project.isDistinct(), project.isStar());
        }
        if (plan instanceof JQuickFilterPhysicalNode) {
            JQuickFilterPhysicalNode filter = (JQuickFilterPhysicalNode) plan;
            JQuickPhysicalPlanNode newChild = replaceCteReference(filter.getChildren().get(0), cteName, replacement);
            return new JQuickFilterPhysicalNode(filter.getPredicate(), newChild);
        }

        if (plan instanceof JQuickHashJoinPhysicalNode) {
            JQuickHashJoinPhysicalNode join = (JQuickHashJoinPhysicalNode) plan;
            JQuickPhysicalPlanNode newLeft = replaceCteReference(join.getLeft(), cteName, replacement);
            JQuickPhysicalPlanNode newRight = replaceCteReference(join.getRight(), cteName, replacement);
            return new JQuickHashJoinPhysicalNode(join.getJoinType(), newLeft, newRight, join.getCondition(), join.getJoinKeys(), join.getBuildSide(), join.getDistribution());
        }

        return plan;
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
            aggregates.add(new JQuickHashAggregatePhysicalNode.AggregateFunction(
                    agg.getFunctionName(), agg.getArgument(), agg.isDistinct(),
                    agg.getAlias(), agg.isCountStar(), agg.getSeparator(),
                    JQuickHashAggregatePhysicalNode.AggregateStage.SINGLE
            ));
        }
        JQuickHashAggregatePhysicalNode physicalNode;
        if (node.getGroupKeys() != null) {
            physicalNode = new JQuickHashAggregatePhysicalNode(
                    node.getGroupKeys(), aggregates, childPhysical,
                    node.getHavingCondition(),
                    JQuickHashAggregatePhysicalNode.AggregateStage.SINGLE
            );
        } else {
            physicalNode = new JQuickHashAggregatePhysicalNode(
                    new ArrayList<>(), aggregates, childPhysical,
                    node.getHavingCondition(), JQuickHashAggregatePhysicalNode.AggregateStage.SINGLE
            );
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
        JQuickPhysicalPlanNode initialPhysical = node.getInitialPlan() != null ? logicalToPhysical.get(node.getInitialPlan()) : null;
        JQuickPhysicalPlanNode recursivePhysical = node.getRecursivePlan() != null ? logicalToPhysical.get(node.getRecursivePlan()) : null;
        JQuickRecursiveUnionPhysicalNode physicalNode = new JQuickRecursiveUnionPhysicalNode(
                node.getCteName(), node.getColumnNames(),
                initialPhysical, recursivePhysical, node.isUnionAll()
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
                orderKeys.add(new JQuickSortPhysicalNode.OrderByItem(
                        item.getColumnName(), item.isAscending(), item.isNullsFirst()
                ));
            }
        }
        return new JQuickWindowPhysicalNode.WindowSpec(spec.getPartitionKeys(), orderKeys, null);
    }

    /**
     * 应用列裁剪优化
     */
    private JQuickPhysicalPlanNode applyColumnPruning(JQuickPhysicalPlanNode plan, Set<String> requiredColumns) {
        if (plan instanceof JQuickTableScanPhysicalNode) {
            JQuickTableScanPhysicalNode scan = (JQuickTableScanPhysicalNode) plan;
            return new JQuickTableScanPhysicalNode(
                    scan.getTableName(), scan.getAlias(), requiredColumns, scan.getFilterPredicate()
            );
        }
        if (plan instanceof JQuickProjectPhysicalNode) {
            JQuickProjectPhysicalNode project = (JQuickProjectPhysicalNode) plan;
            if (project.isStar()) {
                return project;
            }
            List<JQuickProjectPhysicalNode.SelectItem> filteredItems = new ArrayList<>();
            for (JQuickProjectPhysicalNode.SelectItem item : project.getSelectItems()) {
                if (requiredColumns.contains(item.getAlias())) {
                    filteredItems.add(item);
                }
            }
            if (filteredItems.size() < project.getSelectItems().size()) {
                return new JQuickProjectPhysicalNode(filteredItems, project.getChildren().get(0), project.isDistinct(), false);
            }
        }

        return plan;
    }
}