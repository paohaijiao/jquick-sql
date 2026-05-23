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
package com.github.paohaijiao.optimizer;

import com.github.paohaijiao.console.JConsole;
import com.github.paohaijiao.logic.JQuickLogicalPlanNode;
import com.github.paohaijiao.optimizer.impl.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 逻辑计划优化器 - 应用各种优化规则
 */
public class JQuickLogicalPlanOptimizer {

    private static JConsole log=JConsole.initConsoleEnvironment();

    private final List<JQuickOptimizerRule> rules;

    private boolean enableRuleTracing = false;

    public JQuickLogicalPlanOptimizer() {
        this.rules = new ArrayList<>();
        registerDefaultRules();
    }

    private void registerDefaultRules() {
        /**
         * 常量折叠
         */
        rules.add(new JQuickConstantFoldingRule());
        /**
         * 表达式简化
         */
        rules.add(new JQuickSimplifyExpressionRule());

        /**
         * 冗余过滤移除
         */
        rules.add(new JQuickRedundantFilterRemovalRule());
        /**
         * 谓词下推
         */
        rules.add(new JQuickPredicatePushdownRule());
        /**
         * 投影下推
         */
        rules.add(new JQuickProjectionPushdownRule());
        /**
         * Limit下推
         */
        rules.add(new JQuickLimitPushdownRule());
        /**
         * 过滤合并
         */
        rules.add(new JQuickFilterMergeRule());
        /**
         * 投影合并
         */
        rules.add(new JQuickProjectionMergeRule());

        /**
         * 列剪枝
         */
//        rules.add(new ColumnPruningRule());
        /**高级优化
         * Join重排序
         */
        rules.add(new JQuickJoinReorderRule());
        /**
         * 子查询转Join
         */
        rules.add(new JQuickSubqueryToJoinRule());
        /**
         * 聚合下推
         */
        rules.add(new JQuickPredicatePushdownRule());

        /**
         * 分布优化
         */
        rules.add(new JQuickDistributionOptimizationRule());
    }

    public JQuickLogicalPlanNode optimize(JQuickLogicalPlanNode plan) {
        return optimize(plan, 10);
    }

    public JQuickLogicalPlanNode optimize(JQuickLogicalPlanNode plan, int maxIterations) {
        JQuickLogicalPlanNode current = plan;
        boolean changed;
        int iteration = 0;
        do {
            changed = false;
            for (JQuickOptimizerRule rule : rules) {
                if (enableRuleTracing) {
                    log.info("Applying rule: " + rule.getClass().getSimpleName());
                }
                JQuickLogicalPlanNode result = rule.apply(current);
                if (result != current) {
                    current = result;
                    changed = true;
                    if (enableRuleTracing) {
                        log.info("  -> Applied, plan changed");
                    }
                }
            }
            iteration++;
        } while (changed && iteration < maxIterations);
        if (enableRuleTracing) {
            log.info("Optimization completed after " + iteration + " iterations");
        }
        return current;
    }

    public void enableRuleTracing(boolean enable) {
        this.enableRuleTracing = enable;
    }



//
//    /**
//     * 列剪枝：移除不需要的列
//     */
//    private static class ColumnPruningRule implements OptimizerRule {
//        @Override
//        public JQuickLogicalPlanNode apply(JQuickLogicalPlanNode node) {
//            // 从根节点开始，向下传递需要的列
//            return pruneColumns(node, collectOutputColumns(node));
//        }
//
//        private Set<String> collectOutputColumns(JQuickLogicalPlanNode node) {
//            // 收集查询最终输出的列
//            if (node instanceof JQuickProjectNode) {
//                Set<String> cols = new HashSet<>();
//                for (JQuickProjectNode.SelectItem item : ((JQuickProjectNode) node).getSelectItems()) {
//                    cols.addAll(item.getExpression().getReferencedColumns());
//                }
//                return cols;
//            }
//            return new HashSet<>();
//        }
//
//        private JQuickLogicalPlanNode pruneColumns(JQuickLogicalPlanNode node, Set<String> required) {
//            if (node instanceof JQuickProjectNode) {
//                JQuickProjectNode project = (JQuickProjectNode) node;
//                List<JQuickProjectNode.SelectItem> kept = new ArrayList<>();
//                Set<String> inputRequired = new HashSet<>();
//                for (JQuickProjectNode.SelectItem item : project.getSelectItems()) {
//                    if (required.contains(item.getAlias())) {
//                        kept.add(item);
//                        inputRequired.addAll(item.getExpression().getReferencedColumns());
//                    }
//                }
//                JQuickLogicalPlanNode newChild = pruneColumns(project.getChild(), inputRequired);
//                if (kept.isEmpty()) {
//                    if (needsAggregation(project.getChild())) {
//                        return createAggregateOnlyPlan(project);
//                    }
//                    return new JQuickEmptyNode();                }
//                return new JQuickProjectNode(kept, newChild, project.isDistinct());
//
//            } else if (node instanceof JQuickFilterNode) {
//                JQuickFilterNode filter = (JQuickFilterNode) node;
//                Set<String> filterRequired = new HashSet<>(required);
//                filterRequired.addAll(filter.getPredicate().getReferencedColumns());
//                JQuickLogicalPlanNode newChild = pruneColumns(filter.getChild(), filterRequired);
//                return new JQuickFilterNode(filter.getPredicate(), newChild);
//
//            } else if (node instanceof JQuickTableScanNode) {
//                JQuickTableScanNode scan = (JQuickTableScanNode) node;
//                return new JQuickTableScanNode(scan.getTableName(), scan.getAlias(), required, scan.getFilterPredicate());
//            }
//
//            return node;
//        }
//        private boolean needsAggregation(JQuickLogicalPlanNode node) {
//            if (node instanceof JQuickGroupByNode) {
//                return !((JQuickGroupByNode) node).getAggregateItems().isEmpty();
//            }
//            if (node instanceof JQuickAggregateNode) {
//                return !((JQuickAggregateNode) node).getAggregates().isEmpty();
//            }
//            return false;
//        }
//
//        private JQuickLogicalPlanNode createAggregateOnlyPlan(JQuickProjectNode project) {
//            return project.getChild();
//        }
//    }

}