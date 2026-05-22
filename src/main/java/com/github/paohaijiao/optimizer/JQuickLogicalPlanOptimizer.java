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
import com.github.paohaijiao.datasource.JQuickDataSourceManager;
import com.github.paohaijiao.enums.JQuickBinaryOperator;
import com.github.paohaijiao.enums.JQuickJoinType;
import com.github.paohaijiao.enums.JQuickUnaryOperator;
import com.github.paohaijiao.exception.JAssert;
import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.expression.domain.JQuickBinaryExpression;
import com.github.paohaijiao.expression.domain.JQuickColumnRefExpression;
import com.github.paohaijiao.expression.domain.JQuickLiteralExpression;
import com.github.paohaijiao.expression.domain.JQuickUnaryExpression;
import com.github.paohaijiao.logic.JQuickLogicalPlanNode;
import com.github.paohaijiao.logic.domain.*;
import com.github.paohaijiao.statement.JQuickColumnMeta;
import com.github.paohaijiao.statement.JQuickDataSet;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 逻辑计划优化器 - 应用各种优化规则
 */
public class JQuickLogicalPlanOptimizer {

    private static JConsole log=JConsole.initConsoleEnvironment();

    private final List<OptimizerRule> rules;

    private boolean enableRuleTracing = false;

    public JQuickLogicalPlanOptimizer() {
        this.rules = new ArrayList<>();
        registerDefaultRules();
    }

    private void registerDefaultRules() {
        /**
         * 常量折叠
         */
        rules.add(new ConstantFoldingRule());
        /**
         * 谓词下推
         */
        rules.add(new PredicatePushdownRule());
        /**
         * 投影下推
         */
        rules.add(new ProjectionPushdownRule());
        /**
         * Limit下推
         */
        rules.add(new LimitPushdownRule());
        /**
         * 过滤合并
         */
        rules.add(new FilterMergeRule());
        /**
         * 投影合并
         */
        rules.add(new ProjectionMergeRule());
        /**
         * 冗余过滤移除
         */
        rules.add(new RedundantFilterRemovalRule());
        /**
         * 列剪枝
         */
        rules.add(new ColumnPruningRule());
        /**高级优化
         * Join重排序
         */
        rules.add(new JoinReorderRule());
        /**
         * 子查询转Join
         */
        rules.add(new SubqueryToJoinRule());
        /**
         * 聚合下推
         */
        rules.add(new AggregatePushdownRule());
        /**
         * 表达式简化
         */
        rules.add(new SimplifyExpressionRule());
        /**
         * 分布优化
         */
        rules.add(new DistributionOptimizationRule());
    }

    public JQuickLogicalPlanNode optimize(JQuickLogicalPlanNode plan) {
        return optimize(plan, 10); // 最多优化10轮
    }

    public JQuickLogicalPlanNode optimize(JQuickLogicalPlanNode plan, int maxIterations) {
        JQuickLogicalPlanNode current = plan;
        boolean changed;
        int iteration = 0;
        do {
            changed = false;
            for (OptimizerRule rule : rules) {
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

    public interface OptimizerRule {
        JQuickLogicalPlanNode apply(JQuickLogicalPlanNode node);
    }

    /**
     * 常减少运行时计算开销
     * 未优化：扫描100万行 → 计算100万次乘法
     * 优化后：扫描100万行 → 计算0次（常量已提前计算）
     * 例如：1 + 2 → 3, age > 10 AND age < 20 → age BETWEEN 11 AND 19
     */
    private static class ConstantFoldingRule implements OptimizerRule {
        @Override
        public JQuickLogicalPlanNode apply(JQuickLogicalPlanNode node) {
            if (node instanceof JQuickFilterNode) {
                JQuickFilterNode filter = (JQuickFilterNode) node;
                JQuickExpression folded = foldConstants(filter.getPredicate());
                return new JQuickFilterNode(folded, filter.getChild());
            } else if (node instanceof JQuickProjectNode) {
                JQuickProjectNode project = (JQuickProjectNode) node;
                List<JQuickProjectNode.SelectItem> newItems = new ArrayList<>();
                for (JQuickProjectNode.SelectItem item : project.getSelectItems()) {
                    JQuickExpression folded = foldConstants(item.getExpression());
                    newItems.add(new JQuickProjectNode.SelectItem(folded, item.getAlias()));
                }
                return new JQuickProjectNode(newItems, project.getChild(), project.isDistinct());
            }
            return node;
        }

        private JQuickExpression foldConstants(JQuickExpression expr) {
            if (expr instanceof JQuickBinaryExpression) {
                JQuickBinaryExpression binary = (JQuickBinaryExpression) expr;
                JQuickExpression left = foldConstants(binary.getLeft());
                JQuickExpression right = foldConstants(binary.getRight());
                if (left.isConstant() && right.isConstant()) {
                    Object value = binary.getOperator().apply(
                            left.evaluate(null), right.evaluate(null));
                    return new JQuickLiteralExpression(value);
                }
                // 简化：x + 0 → x
                if (binary.getOperator() == JQuickBinaryOperator.PLUS) {
                    if (isZero(right)) return left;
                    if (isZero(left)) return right;
                }

                // 简化：x * 1 → x
                if (binary.getOperator() == JQuickBinaryOperator.MULTIPLY) {
                    if (isOne(right)) return left;
                    if (isOne(left)) return right;
                }

                // 简化：x * 0 → 0
                if (binary.getOperator() == JQuickBinaryOperator.MULTIPLY) {
                    if (isZero(right) || isZero(left)) {
                        return new JQuickLiteralExpression(0);
                    }
                }

                return new JQuickBinaryExpression(left, right, binary.getOperator());
            }
            return expr;
        }

        private boolean isZero(JQuickExpression expr) {
            if (expr instanceof JQuickLiteralExpression) {
                Object value = ((JQuickLiteralExpression) expr).getValue();
                return value instanceof Number && ((Number) value).doubleValue() == 0;
            }
            return false;
        }

        private boolean isOne(JQuickExpression expr) {
            if (expr instanceof JQuickLiteralExpression) {
                Object value = ((JQuickLiteralExpression) expr).getValue();
                return value instanceof Number && ((Number) value).doubleValue() == 1;
            }
            return false;
        }
    }

    /**
     * 谓词下推：尽早过滤数据，减少后续处理的数据量
     * 优化前：先读取所有数据 → 再过滤
     * 优化后：先过滤数据 → 只读取需要的数据
     * SELECT * FROM (
     *     SELECT * FROM orders WHERE status = 'PAID'  -- 内层过滤
     * ) t
     * WHERE amount > 1000  -- 外层过滤
     * -- 谓词下推后
     * SELECT * FROM orders
     * WHERE status = 'PAID' AND amount > 1000  -- 合并到内层
     */
    private static class PredicatePushdownRule implements OptimizerRule {
        @Override
        public JQuickLogicalPlanNode apply(JQuickLogicalPlanNode node) {
            if (node instanceof JQuickFilterNode) {
                JQuickFilterNode filter = (JQuickFilterNode) node;
                JQuickLogicalPlanNode child = filter.getChild();
                JQuickExpression predicate = filter.getPredicate();
                if (child instanceof JQuickTableScanNode) {
                    JQuickTableScanNode scan = (JQuickTableScanNode) child;
                    // 将过滤条件下推到表扫描节点
                    JQuickExpression existingFilter = scan.getFilterPredicate();
                    JQuickExpression combined = existingFilter != null ? new JQuickBinaryExpression(existingFilter, predicate, JQuickBinaryOperator.AND) : predicate;
                    return new JQuickTableScanNode(scan.getTableName(), scan.getAlias(), scan.getRequiredColumns(), combined);
                } else if (child instanceof JQuickProjectNode) {
                    // 投影和过滤交换（如果过滤列都在投影中）
                    JQuickProjectNode project = (JQuickProjectNode) child;
                    if (canPushdown(predicate, project)) {
                        return new JQuickProjectNode(project.getSelectItems(), new JQuickFilterNode(predicate, project.getChild()), project.isDistinct());
                    }
                } else if (child instanceof JQuickJoinNode) {
                    // 将过滤条件推入Join
                    return pushFilterIntoJoin(filter, (JQuickJoinNode) child);
                }
            }
            return node;
        }

        private boolean canPushdown(JQuickExpression predicate, JQuickProjectNode project) {
            Set<String> projectColumns = project.getSelectItems().stream().map(JQuickProjectNode.SelectItem::getAlias).collect(Collectors.toSet());
            return projectColumns.containsAll(predicate.getReferencedColumns());
        }

        private JQuickLogicalPlanNode pushFilterIntoJoin(JQuickFilterNode filter, JQuickJoinNode join) {
            JQuickExpression predicate = filter.getPredicate();
            Set<String> leftColumns = getColumnNames(join.getLeft());
            Set<String> rightColumns = getColumnNames(join.getRight());
            // 分离过滤条件
            List<JQuickExpression> leftFilters = new ArrayList<>();
            List<JQuickExpression> rightFilters = new ArrayList<>();
            List<JQuickExpression> joinFilters = new ArrayList<>();
            splitPredicate(predicate, leftColumns, rightColumns, leftFilters, rightFilters, joinFilters);
            JQuickLogicalPlanNode newLeft = join.getLeft();
            JQuickLogicalPlanNode newRight = join.getRight();
            if (!leftFilters.isEmpty()) {
                JQuickExpression leftFilter = combinePredicates(leftFilters);
                newLeft = new JQuickFilterNode(leftFilter, newLeft);
            }
            if (!rightFilters.isEmpty()) {
                JQuickExpression rightFilter = combinePredicates(rightFilters);
                newRight = new JQuickFilterNode(rightFilter, newRight);
            }
            JQuickExpression joinFilter = combinePredicates(joinFilters);
            JQuickJoinNode newJoin = new JQuickJoinNode(join.getJoinType(), newLeft, newRight, joinFilter);
            return newJoin;
        }

        private void splitPredicate(JQuickExpression expr, Set<String> leftCols, Set<String> rightCols, List<JQuickExpression> leftFilters, List<JQuickExpression> rightFilters, List<JQuickExpression> joinFilters) {
            if (expr instanceof JQuickBinaryExpression && ((JQuickBinaryExpression) expr).getOperator() == JQuickBinaryOperator.AND) {
                JQuickBinaryExpression binary = (JQuickBinaryExpression) expr;
                splitPredicate(binary.getLeft(), leftCols, rightCols, leftFilters, rightFilters, joinFilters);
                splitPredicate(binary.getRight(), leftCols, rightCols, leftFilters, rightFilters, joinFilters);
                return;
            }
            Set<String> referenced = new HashSet<>(expr.getReferencedColumns());
            if (referenced.stream().allMatch(leftCols::contains)) {
                leftFilters.add(expr);
            } else if (referenced.stream().allMatch(rightCols::contains)) {
                rightFilters.add(expr);
            } else {
                joinFilters.add(expr);
            }
        }

        private JQuickExpression combinePredicates(List<JQuickExpression> predicates) {
            if (predicates.isEmpty()) return null;
            if (predicates.size() == 1) return predicates.get(0);

            JQuickExpression result = predicates.get(0);
            for (int i = 1; i < predicates.size(); i++) {
                result = new JQuickBinaryExpression(result, predicates.get(i), JQuickBinaryOperator.AND);
            }
            return result;
        }

        private Set<String> getColumnNames(JQuickLogicalPlanNode node) {
            Set<String> columns = new HashSet<>();
            if (node instanceof JQuickTableScanNode) {
                // 可以从表元数据获取
            } else if (node instanceof JQuickProjectNode) {
                JQuickProjectNode project = (JQuickProjectNode) node;
                for (JQuickProjectNode.SelectItem item : project.getSelectItems()) {
                    columns.add(item.getAlias());
                }
            }
            return columns;
        }
    }


    /**
     * 投影下推：只保留需要的列，减少数据传输
     */
    private static class ProjectionPushdownRule implements OptimizerRule {
        @Override
        public JQuickLogicalPlanNode apply(JQuickLogicalPlanNode node) {
            if (node instanceof JQuickProjectNode) {
                JQuickProjectNode project = (JQuickProjectNode) node;
                Set<String> requiredColumns = project.getSelectItems().stream()
                        .map(JQuickProjectNode.SelectItem::getAlias)
                        .collect(Collectors.toSet());
                return pushdownProjection(project, requiredColumns);
            }
            return node;
        }

        private JQuickLogicalPlanNode pushdownProjection(JQuickProjectNode project, Set<String> requiredColumns) {
            JQuickLogicalPlanNode child = project.getChild();
            if (child instanceof JQuickTableScanNode) {
                JQuickTableScanNode scan = (JQuickTableScanNode) child;
                return new JQuickProjectNode(project.getSelectItems(),
                        new JQuickTableScanNode(scan.getTableName(), scan.getAlias(), requiredColumns, scan.getFilterPredicate()),
                        project.isDistinct());
            } else if (child instanceof JQuickProjectNode) {
                // 合并投影
                JQuickProjectNode childProject = (JQuickProjectNode) child;
                return new JQuickProjectNode(project.getSelectItems(), childProject.getChild(), project.isDistinct());
            } else if (child instanceof JQuickJoinNode) {
                // 将所需列下推到Join的左右子节点
                JQuickJoinNode join = (JQuickJoinNode) child;
                Set<String> leftColumns = extractColumnsFromJoin(join.getLeft(), requiredColumns);
                Set<String> rightColumns = extractColumnsFromJoin(join.getRight(), requiredColumns);
                JQuickLogicalPlanNode newLeft = pushdownToNode(join.getLeft(), leftColumns);
                JQuickLogicalPlanNode newRight = pushdownToNode(join.getRight(), rightColumns);
                JQuickJoinNode newJoin = new JQuickJoinNode(join.getJoinType(), newLeft, newRight, join.getCondition());
                return new JQuickProjectNode(project.getSelectItems(), newJoin, project.isDistinct());
            }

            return project;
        }

        private Set<String> extractColumnsFromJoin(JQuickLogicalPlanNode node, Set<String> requiredColumns) {
            Set<String> result = new HashSet<>();
            // 从表达式中提取列名
            if (node instanceof JQuickProjectNode) {
                JQuickProjectNode project = (JQuickProjectNode) node;
                for (JQuickProjectNode.SelectItem item : project.getSelectItems()) {
                    if (requiredColumns.contains(item.getAlias())) {
                        result.addAll(item.getExpression().getReferencedColumns());
                    }
                }
            }
            return result;
        }

        private JQuickLogicalPlanNode pushdownToNode(JQuickLogicalPlanNode node, Set<String> requiredColumns) {
            if (node instanceof JQuickTableScanNode) {
                JQuickTableScanNode scan = (JQuickTableScanNode) node;
                return new JQuickTableScanNode(scan.getTableName(), scan.getAlias(), requiredColumns, scan.getFilterPredicate());
            } else if (node instanceof JQuickProjectNode) {
                JQuickProjectNode project = (JQuickProjectNode) node;
                List<JQuickProjectNode.SelectItem> filtered = project.getSelectItems().stream()
                        .filter(item -> requiredColumns.contains(item.getAlias()))
                        .collect(Collectors.toList());
                return new JQuickProjectNode(filtered, project.getChild(), project.isDistinct());
            }
            return node;
        }
    }

    /**
     * Limit下推：将Limit尽可能下推到数据源
     */
    private static class LimitPushdownRule implements OptimizerRule {
        @Override
        public JQuickLogicalPlanNode apply(JQuickLogicalPlanNode node) {
            if (node instanceof JQuickLimitNode) {
                JQuickLimitNode limit = (JQuickLimitNode) node;
                JQuickLogicalPlanNode child = limit.getChild();
                if (child instanceof JQuickSortNode) {
                    // LIMIT + ORDER BY 可以优化为 Top-N
                    JQuickSortNode sort = (JQuickSortNode) child;
                    return new JQuickLimitNode(limit.getLimit(), limit.getOffset(), sort);
                } else if (child instanceof JQuickProjectNode) {//安全：投影不改变行数
                    // 交换Limit和Project
                    JQuickProjectNode project = (JQuickProjectNode) child;
                    return new JQuickProjectNode(project.getSelectItems(), new JQuickLimitNode(limit.getLimit(), limit.getOffset(), project.getChild()), project.isDistinct());
                } else if (child instanceof JQuickFilterNode) {//过滤不改变行数关系
                    // 保持顺序
                    JQuickFilterNode filter = (JQuickFilterNode) child;
                    return new JQuickFilterNode(filter.getPredicate(), new JQuickLimitNode(limit.getLimit(), limit.getOffset(), filter.getChild()));
                }
            }
            return node;
        }
    }
    /**
     * 过滤合并：将连续的Filter节点合并为一个
     * -- 原始：嵌套过滤
     * WHERE age > 18
     *   AND WHERE city = 'Beijing'
     * -- 合并后：单一过滤
     * WHERE age > 18 AND city = 'Beijing'
     */
    private static class FilterMergeRule implements OptimizerRule {
        @Override
        public JQuickLogicalPlanNode apply(JQuickLogicalPlanNode node) {
            if (node instanceof JQuickFilterNode) {
                JQuickFilterNode outer = (JQuickFilterNode) node;
                if (outer.getChild() instanceof JQuickFilterNode) {
                    JQuickFilterNode inner = (JQuickFilterNode) outer.getChild();
                    JQuickExpression combined = new JQuickBinaryExpression(outer.getPredicate(), inner.getPredicate(), JQuickBinaryOperator.AND);
                    return new JQuickFilterNode(combined, inner.getChild());
                }
            }
            return node;
        }
    }
    /**
     * 投影合并：将连续的Project节点合并为一个，减少不必要的数据转换和列映射开销
     * -- 原始：嵌套投影
     * SELECT name, age FROM (
     *     SELECT id, name, age, city FROM users
     * ) t
     *
     * -- 合并后：单一投影
     * SELECT name, age FROM users
     */
    private static class ProjectionMergeRule implements OptimizerRule {
        @Override
        public JQuickLogicalPlanNode apply(JQuickLogicalPlanNode node) {
            if (node instanceof JQuickProjectNode) {
                JQuickProjectNode outer = (JQuickProjectNode) node;
                if (outer.getChild() instanceof JQuickProjectNode) {
                    JQuickProjectNode inner = (JQuickProjectNode) outer.getChild();
                    Map<String, JQuickExpression> innerExprMap = new HashMap<>();// 构建内层投影的表达式映射
                    for (JQuickProjectNode.SelectItem item : inner.getSelectItems()) {
                        innerExprMap.put(item.getAlias(), item.getExpression());
                    }
                    List<JQuickProjectNode.SelectItem> merged = new ArrayList<>();// 替换外层投影中的列引用
                    for (JQuickProjectNode.SelectItem outerItem : outer.getSelectItems()) {
                        JQuickExpression replaced = replaceColumns(outerItem.getExpression(), innerExprMap);
                        merged.add(new JQuickProjectNode.SelectItem(replaced, outerItem.getAlias()));
                    }
                    return new JQuickProjectNode(merged, inner.getChild(), outer.isDistinct() || inner.isDistinct());
                }
            }
            return node;
        }

        private JQuickExpression replaceColumns(JQuickExpression expr, Map<String, JQuickExpression> columnMap) {
            if (expr instanceof JQuickColumnRefExpression) {
                JQuickColumnRefExpression col = (JQuickColumnRefExpression) expr;
                JQuickExpression replacement = columnMap.get(col.getColumnName());
                return replacement != null ? replacement : expr;
            } else if (expr instanceof JQuickBinaryExpression) {
                JQuickBinaryExpression binary = (JQuickBinaryExpression) expr;
                return new JQuickBinaryExpression(replaceColumns(binary.getLeft(), columnMap), replaceColumns(binary.getRight(), columnMap), binary.getOperator());
            }
            return expr;
        }
    }


    /**
     * 冗余过滤移除：移除始终为true或重复的过滤条件
     */
    private static class RedundantFilterRemovalRule implements OptimizerRule {
        @Override
        public JQuickLogicalPlanNode apply(JQuickLogicalPlanNode node) {
            if (node instanceof JQuickFilterNode) {
                JQuickFilterNode filter = (JQuickFilterNode) node;
                JQuickExpression predicate = filter.getPredicate();
                if (isAlwaysTrue(predicate)) { // 检查是否为恒真条件
                    return filter.getChild();//直接返回子节点，移除了Filter
                }
                // 检查是否为恒假条件
                if (isAlwaysFalse(predicate)) {
                    return new JQuickEmptyNode();
                }
            }
            return node;
        }
        private boolean isAlwaysTrue(JQuickExpression expr) {
            if (expr instanceof JQuickLiteralExpression) {
                Object value = ((JQuickLiteralExpression) expr).getValue();
                return Boolean.TRUE.equals(value);
            }
            return false;
        }
        private boolean isAlwaysFalse(JQuickExpression expr) {
            if (expr instanceof JQuickLiteralExpression) {
                Object value = ((JQuickLiteralExpression) expr).getValue();
                return Boolean.FALSE.equals(value);
            }
            return false;
        }
    }


    /**
     * 列剪枝：移除不需要的列
     */
    private static class ColumnPruningRule implements OptimizerRule {
        @Override
        public JQuickLogicalPlanNode apply(JQuickLogicalPlanNode node) {
            // 从根节点开始，向下传递需要的列
            return pruneColumns(node, collectOutputColumns(node));
        }

        private Set<String> collectOutputColumns(JQuickLogicalPlanNode node) {
            // 收集查询最终输出的列
            if (node instanceof JQuickProjectNode) {
                Set<String> cols = new HashSet<>();
                for (JQuickProjectNode.SelectItem item : ((JQuickProjectNode) node).getSelectItems()) {
                    cols.addAll(item.getExpression().getReferencedColumns());
                }
                return cols;
            }
            return new HashSet<>();
        }

        private JQuickLogicalPlanNode pruneColumns(JQuickLogicalPlanNode node, Set<String> required) {
            if (node instanceof JQuickProjectNode) {
                JQuickProjectNode project = (JQuickProjectNode) node;
                //分析当前投影：哪些输出被需要，需要哪些输入列
                List<JQuickProjectNode.SelectItem> kept = new ArrayList<>();
                Set<String> inputRequired = new HashSet<>();
                for (JQuickProjectNode.SelectItem item : project.getSelectItems()) {
                    if (required.contains(item.getAlias())) {
                        kept.add(item);
                        inputRequired.addAll(item.getExpression().getReferencedColumns());
                    }
                }
                //递归处理子节点
                JQuickLogicalPlanNode newChild = pruneColumns(project.getChild(), inputRequired);
                //如果没有任何输出被需要，但子节点仍需执行（如COUNT(*)）
                if (kept.isEmpty()) {
                    // 添加一个常量投影
                    kept.add(new JQuickProjectNode.SelectItem(new JQuickLiteralExpression(1), "dummy"));
                }

                return new JQuickProjectNode(kept, newChild, project.isDistinct());

            } else if (node instanceof JQuickFilterNode) {
                JQuickFilterNode filter = (JQuickFilterNode) node;
                // Filter需要的列 = 上层需要的 + 过滤条件引用的列
                Set<String> filterRequired = new HashSet<>(required);
                filterRequired.addAll(filter.getPredicate().getReferencedColumns());
                JQuickLogicalPlanNode newChild = pruneColumns(filter.getChild(), filterRequired);
                return new JQuickFilterNode(filter.getPredicate(), newChild);

            } else if (node instanceof JQuickTableScanNode) {
                JQuickTableScanNode scan = (JQuickTableScanNode) node;
                // 只读取需要的列
                return new JQuickTableScanNode(scan.getTableName(), scan.getAlias(), required, scan.getFilterPredicate());
            }

            return node;
        }
    }

    /**
     * Join重排序：根据表大小和选择率重新排列Join顺序
     * 只对INNER JOIN进行重排序，保持OUTER JOIN的语义
     */
    private static class JoinReorderRule implements OptimizerRule {
        @Override
        public JQuickLogicalPlanNode apply(JQuickLogicalPlanNode node) {
            if (node instanceof JQuickJoinNode) {
                JQuickJoinNode join = (JQuickJoinNode) node;
                if (join.getJoinType() == JQuickJoinType.INNER) {
                    return reorderInnerJoins(join);
                }
            }
            return node;
        }

        /**
         * 重排序INNER JOIN
         * 策略：只将有过滤条件的表提前
         */
        private JQuickLogicalPlanNode reorderInnerJoins(JQuickJoinNode join) {
            JoinGraph graph = buildJoinGraph(join); //收集所有参与Join的表和对应的Join条件
            if (graph.tables.size() <= 1) {
                return join;
            }
            //只做一种优化：将有过滤条件的表移到前面
            List<JQuickLogicalPlanNode> reordered = new ArrayList<>();
            List<JQuickLogicalPlanNode> noFilter = new ArrayList<>();
            for (JQuickLogicalPlanNode table : graph.tables) {
                if (hasFilterPredicate(table)) {
                    reordered.add(table);
                } else {
                    noFilter.add(table);
                }
            }
            reordered.addAll(noFilter);
            if (isSameOrder(graph.tables, reordered)) { //如果顺序没变，直接返回
                return join;
            }
            return buildJoinTree(reordered, graph);//重建Join树
        }

        /**
         * 构建Join图：收集所有表和表之间的Join条件
         */
        private JoinGraph buildJoinGraph(JQuickJoinNode join) {
            JoinGraph graph = new JoinGraph();
            collectJoinInfo(join, graph);
            return graph;
        }

        /**
         * 递归收集Join信息
         */
        private void collectJoinInfo(JQuickLogicalPlanNode node, JoinGraph graph) {
            if (node instanceof JQuickJoinNode) {
                JQuickJoinNode join = (JQuickJoinNode) node;
                if (join.getCondition() != null) {// 记录Join条件
                    graph.addCondition(join);
                }
                collectJoinInfo(join.getLeft(), graph); // 递归处理子节点
                collectJoinInfo(join.getRight(), graph);
            } else {
                graph.addTable(node);// 叶子节点（表）
            }
        }

        /**
         * 检查节点是否有过滤条件
         */
        private boolean hasFilterPredicate(JQuickLogicalPlanNode node) {
            if (node instanceof JQuickFilterNode) {
                return true;
            }
            if (node instanceof JQuickTableScanNode) {
                return ((JQuickTableScanNode) node).getFilterPredicate() != null;
            }
            if (node instanceof JQuickProjectNode) {
                return hasFilterPredicate(((JQuickProjectNode) node).getChild());
            }
            return false;
        }

        /**
         * 提取表名（用于匹配Join条件）
         */
        private String extractTableName(JQuickLogicalPlanNode node) {
            if (node instanceof JQuickTableScanNode) {
                return ((JQuickTableScanNode) node).getTableName();
            }
            if (node instanceof JQuickProjectNode) {
                return extractTableName(((JQuickProjectNode) node).getChild());
            }
            if (node instanceof JQuickFilterNode) {
                return extractTableName(((JQuickFilterNode) node).getChild());
            }
            // 对于子查询或复杂节点，使用节点ID
            JAssert.throwNewException( "tableName  is null");
            return null;
        }

        /**
         * 获取表涉及的列（用于匹配Join条件）
         * 通过分析子树中的所有表达式
         */
        private Set<String> getTableColumns(JQuickLogicalPlanNode node) {
            Set<String> columns = new HashSet<>();
            if (node instanceof JQuickTableScanNode) {
                JQuickTableScanNode scan = (JQuickTableScanNode) node;
                if (scan.getRequiredColumns() != null) {
                    columns.addAll(scan.getRequiredColumns());
                }
            } else if (node instanceof JQuickProjectNode) {
                JQuickProjectNode project = (JQuickProjectNode) node;
                for (JQuickProjectNode.SelectItem item : project.getSelectItems()) {
                    columns.addAll(item.getExpression().getReferencedColumns());
                }
                columns.addAll(getTableColumns(project.getChild()));// 递归获取子节点列
            } else if (node instanceof JQuickFilterNode) {
                JQuickFilterNode filter = (JQuickFilterNode) node;
                columns.addAll(filter.getPredicate().getReferencedColumns());
                columns.addAll(getTableColumns(filter.getChild()));
            } else if (node instanceof JQuickJoinNode) {
                JQuickJoinNode join = (JQuickJoinNode) node;
                if (join.getCondition() != null) {
                    columns.addAll(join.getCondition().getReferencedColumns());
                }
                columns.addAll(getTableColumns(join.getLeft()));
                columns.addAll(getTableColumns(join.getRight()));
            }

            return columns;
        }

        /**
         * 判断两个表是否相同
         */
        private boolean isSameTable(JQuickLogicalPlanNode a, JQuickLogicalPlanNode b) {
            return extractTableName(a).equals(extractTableName(b));
        }

        /**
         * 检查顺序是否相同
         */
        private boolean isSameOrder(List<JQuickLogicalPlanNode> original, List<JQuickLogicalPlanNode> reordered) {
            if (original.size() != reordered.size()) {
                return false;
            }
            for (int i = 0; i < original.size(); i++) {
                if (!isSameTable(original.get(i), reordered.get(i))) {
                    return false;
                }
            }
            return true;
        }

        /**
         * 根据排序后的表和Join条件重建Join树
         */
        private JQuickLogicalPlanNode buildJoinTree(List<JQuickLogicalPlanNode> orderedTables, JoinGraph graph) {
            if (orderedTables.isEmpty()) return null;
            if (orderedTables.size() == 1) return orderedTables.get(0);
            JQuickLogicalPlanNode result = orderedTables.get(0);
            for (int i = 1; i < orderedTables.size(); i++) {
                JQuickLogicalPlanNode right = orderedTables.get(i);
                // 查找两个表之间的Join条件
                JQuickExpression condition = graph.findCondition(extractTableName(result), extractTableName(right));
                result = new JQuickJoinNode(JQuickJoinType.INNER, result, right, condition  );// 可能为null（笛卡尔积）
            }
            return result;
        }

        /**
         * Join图：存储所有表和Join条件
         */
        private static class JoinGraph {

            List<JQuickLogicalPlanNode> tables = new ArrayList<>();

            Map<String, Map<String, JQuickExpression>> conditions = new HashMap<>();

            void addTable(JQuickLogicalPlanNode node) {
                tables.add(node);
            }
            void addCondition(JQuickJoinNode join) {
                String leftName = extractTableNameFromNode(join.getLeft());
                String rightName = extractTableNameFromNode(join.getRight());
                conditions.computeIfAbsent(leftName, k -> new HashMap()).put(rightName, join.getCondition());
                conditions.computeIfAbsent(rightName, k -> new HashMap()).put(leftName, join.getCondition());
            }

            JQuickExpression findCondition(String left, String right) {
                Map<String, JQuickExpression> leftMap = conditions.get(left);
                if (leftMap != null) {
                    return leftMap.get(right);
                }
                return null;
            }

            private String extractTableNameFromNode(JQuickLogicalPlanNode node) {
                if (node instanceof JQuickTableScanNode) {
                    return ((JQuickTableScanNode) node).getTableName();
                }
                if (node instanceof JQuickProjectNode) {
                    return extractTableNameFromNode(((JQuickProjectNode) node).getChild());
                }
                if (node instanceof JQuickFilterNode) {
                    return extractTableNameFromNode(((JQuickFilterNode) node).getChild());
                }
                JAssert.throwNewException( "tableName is null");
                if (node instanceof JQuickJoinNode) {
                    return "subquery_" + System.identityHashCode(node);    // 如果是Join，返回组合名
                }
                return "unknown_" + System.identityHashCode(node);
            }
        }
    }
    /**
     * 表达式简化：简化复杂的表达式
     */
    private static class SimplifyExpressionRule implements OptimizerRule {
        @Override
        public JQuickLogicalPlanNode apply(JQuickLogicalPlanNode node) {
            if (node instanceof JQuickFilterNode) {
                JQuickFilterNode filter = (JQuickFilterNode) node;
                JQuickExpression simplified = simplifyExpression(filter.getPredicate());
                return new JQuickFilterNode(simplified, filter.getChild());

            }
            return node;
        }

        private JQuickExpression simplifyExpression(JQuickExpression expr) {
            if (expr instanceof JQuickLiteralExpression) {
                return expr;  // 常量不需要简化
            }
            if (expr instanceof JQuickBinaryExpression) {
                JQuickBinaryExpression binary = (JQuickBinaryExpression) expr;
                JQuickExpression left = simplifyExpression(binary.getLeft());
                JQuickExpression right = simplifyExpression(binary.getRight());
                //布尔代数简化
                if (binary.getOperator() == JQuickBinaryOperator.AND) {//  x AND true → x
                    if (isTrue(right)) return left;
                    if (isTrue(left)) return right;
                    // x AND false → false
                    if (isFalse(right) || isFalse(left)) {
                        return new JQuickLiteralExpression(false);
                    }
                    // x AND x → x
                    if (left.equals(right)) return left;
                }

                // 2. x OR false → x
                if (binary.getOperator() == JQuickBinaryOperator.OR) {
                    if (isFalse(right)) return left;
                    if (isFalse(left)) return right;
                    // x OR true → true
                    if (isTrue(right) || isTrue(left)) {
                        return new JQuickLiteralExpression(true);
                    }
                    // x OR x → x
                    if (left.equals(right)) return left;
                }
                // 4. x + 0 → x
                if (binary.getOperator() == JQuickBinaryOperator.PLUS) {
                    if (isZero(right)) return left;
                    if (isZero(left)) return right;
                }

                // 5. x - 0 → x
                if (binary.getOperator() == JQuickBinaryOperator.MINUS) {
                    if (isZero(right)) return left;
                }

                // 6. x * 1 → x
                if (binary.getOperator() == JQuickBinaryOperator.MULTIPLY) {
                    if (isOne(right)) return left;
                    if (isOne(left)) return right;
                    // x * 0 → 0
                    if (isZero(right) || isZero(left)) {
                        return new JQuickLiteralExpression(0);
                    }
                }

                // 7. x / 1 → x
                if (binary.getOperator() == JQuickBinaryOperator.DIVIDE) {
                    if (isOne(right)) return left;
                }
                // 8. x = x → true
                if (binary.getOperator() == JQuickBinaryOperator.EQ) {
                    if (left.equals(right)) {
                        return new JQuickLiteralExpression(true);
                    }
                }

                // 9. x != x → false
                if (binary.getOperator() == JQuickBinaryOperator.NE) {
                    if (left.equals(right)) {
                        return new JQuickLiteralExpression(false);
                    }
                }


                // 11. 常量比较折叠
                if (left instanceof JQuickLiteralExpression && right instanceof JQuickLiteralExpression) {
                    Object result = binary.getOperator().apply(((JQuickLiteralExpression) left).getValue(), ((JQuickLiteralExpression) right).getValue());
                    return new JQuickLiteralExpression(result);
                }

                return new JQuickBinaryExpression(left, right, binary.getOperator());
            }

            return expr;
        }

        // 辅助方法
        private boolean isTrue(JQuickExpression expr) {
            if (expr instanceof JQuickLiteralExpression) {
                Object value = ((JQuickLiteralExpression) expr).getValue();
                return Boolean.TRUE.equals(value);
            }
            return false;
        }

        private boolean isFalse(JQuickExpression expr) {
            if (expr instanceof JQuickLiteralExpression) {
                Object value = ((JQuickLiteralExpression) expr).getValue();
                return Boolean.FALSE.equals(value);
            }
            return false;
        }

        private boolean isZero(JQuickExpression expr) {
            if (expr instanceof JQuickLiteralExpression) {
                Object value = ((JQuickLiteralExpression) expr).getValue();
                if (value instanceof Number) {
                    return ((Number) value).doubleValue() == 0;
                }
            }
            return false;
        }

        private boolean isOne(JQuickExpression expr) {
            if (expr instanceof JQuickLiteralExpression) {
                Object value = ((JQuickLiteralExpression) expr).getValue();
                if (value instanceof Number) {
                    return ((Number) value).doubleValue() == 1;
                }
            }
            return false;
        }
    }

    /**
     * 分布优化：将单机操作转换为分布式操作（当数据量大时）
     */
    private static class DistributionOptimizationRule implements OptimizerRule {
        @Override
        public JQuickLogicalPlanNode apply(JQuickLogicalPlanNode node) {
            return node;
        }
    }
    /**
     * 子查询转Join：将子查询转换为Join操作
     */
    private static class SubqueryToJoinRule implements OptimizerRule {
        @Override
        public JQuickLogicalPlanNode apply(JQuickLogicalPlanNode node) {
            // 需要子查询节点支持
            return node;
        }
    }


    /**
     * 聚合下推：将聚合操作下推到数据源
     */
    private static class AggregatePushdownRule implements OptimizerRule {
        @Override
        public JQuickLogicalPlanNode apply(JQuickLogicalPlanNode node) {
            if (node instanceof JQuickGroupByNode) {
                JQuickGroupByNode groupBy = (JQuickGroupByNode) node;
                JQuickLogicalPlanNode child = groupBy.getChild();
                if (child instanceof JQuickJoinNode) {
                    // 如果聚合只依赖一侧，可以下推
                    return pushAggregateToJoinSide(groupBy, (JQuickJoinNode) child);
                } else if (child instanceof JQuickFilterNode) {
                    // 交换聚合和过滤（如果可能）
                    return new JQuickFilterNode(((JQuickFilterNode) child).getPredicate(),
                            new JQuickGroupByNode(groupBy.getGroupKeys(), groupBy.getAggregateItems(), ((JQuickFilterNode) child).getChild(), groupBy.getHavingCondition()));
                }
            }
            return node;
        }

        private JQuickLogicalPlanNode pushAggregateToJoinSide(JQuickGroupByNode groupBy, JQuickJoinNode join) {
            // 检查聚合是否只依赖左表或右表
            Set<String> groupColumns = extractColumnNames(groupBy.getGroupKeys());
            Set<String> leftColumns = getTableColumns(join.getLeft());
            Set<String> rightColumns = getTableColumns(join.getRight());
            if (leftColumns.containsAll(groupColumns)) {
                // 聚合只依赖左表，可以先聚合再Join
                JQuickGroupByNode leftAgg = new JQuickGroupByNode(groupBy.getGroupKeys(), groupBy.getAggregateItems(), join.getLeft(), null);
                return new JQuickJoinNode(join.getJoinType(), leftAgg, join.getRight(), join.getCondition());
            } else if (rightColumns.containsAll(groupColumns)) {
                JQuickGroupByNode rightAgg = new JQuickGroupByNode(groupBy.getGroupKeys(), groupBy.getAggregateItems(), join.getRight(), null);
                return new JQuickJoinNode(join.getJoinType(), join.getLeft(), rightAgg, join.getCondition());
            }
            return groupBy;
        }

        private Set<String> extractColumnNames(List<JQuickExpression> expressions) {
            Set<String> columns = new HashSet<>();
            for (JQuickExpression expr : expressions) {
                columns.addAll(expr.getReferencedColumns());
            }
            return columns;
        }

        private Set<String> getTableColumns(JQuickLogicalPlanNode node) {
            if (node instanceof JQuickTableScanNode) {
                JQuickTableScanNode scan = (JQuickTableScanNode) node;
                // 从表元数据获取列名
                return new HashSet<>();
            }
            return new HashSet<>();
        }
    }

}