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

import com.github.paohaijiao.enums.JQuickBinaryOperator;
import com.github.paohaijiao.enums.JQuickJoinType;
import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.expression.domain.JQuickBinaryExpression;
import com.github.paohaijiao.expression.domain.JQuickColumnRefExpression;
import com.github.paohaijiao.expression.domain.JQuickLiteralExpression;
import com.github.paohaijiao.expression.domain.JQuickUnaryExpression;
import com.github.paohaijiao.logic.JQuickLogicalPlanNode;
import com.github.paohaijiao.logic.domain.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 逻辑计划优化器 - 应用各种优化规则
 */
public class JQuickLogicalPlanOptimizer {

    private final List<OptimizerRule> rules;

    private boolean enableRuleTracing = false;

    public JQuickLogicalPlanOptimizer() {
        this.rules = new ArrayList<>();
        registerDefaultRules();
    }

    private void registerDefaultRules() {
        // 基础优化规则
        rules.add(new ConstantFoldingRule());           // 常量折叠
        rules.add(new PredicatePushdownRule());         // 谓词下推
        rules.add(new ProjectionPushdownRule());        // 投影下推
        rules.add(new LimitPushdownRule());             // Limit下推
        rules.add(new FilterMergeRule());               // 过滤合并
        rules.add(new ProjectionMergeRule());           // 投影合并
        rules.add(new RedundantFilterRemovalRule());    // 冗余过滤移除
        rules.add(new ColumnPruningRule());             // 列剪枝

        // 高级优化规则
        rules.add(new JoinReorderRule());               // Join重排序
        rules.add(new SubqueryToJoinRule());            // 子查询转Join
        rules.add(new AggregatePushdownRule());         // 聚合下推
        rules.add(new SimplifyExpressionRule());        // 表达式简化
        rules.add(new DistributionOptimizationRule());  // 分布优化
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
                    System.out.println("Applying rule: " + rule.getClass().getSimpleName());
                }
                JQuickLogicalPlanNode result = rule.apply(current);
                if (result != current) {
                    current = result;
                    changed = true;
                    if (enableRuleTracing) {
                        System.out.println("  -> Applied, plan changed");
                    }
                }
            }
            iteration++;
        } while (changed && iteration < maxIterations);
        if (enableRuleTracing) {
            System.out.println("Optimization completed after " + iteration + " iterations");
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
     * 常量折叠：将编译时就能确定的表达式计算结果替换为常量
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
     * 谓词下推：将过滤条件尽可能下推到数据源
     * 例如：SELECT * FROM (SELECT * FROM t1) WHERE a > 10 → SELECT * FROM t1 WHERE a > 10
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
                } else if (child instanceof JQuickProjectNode) {
                    // 交换Limit和Project
                    JQuickProjectNode project = (JQuickProjectNode) child;
                    return new JQuickProjectNode(project.getSelectItems(),
                            new JQuickLimitNode(limit.getLimit(), limit.getOffset(), project.getChild()),
                            project.isDistinct());
                } else if (child instanceof JQuickFilterNode) {
                    // 保持顺序
                    JQuickFilterNode filter = (JQuickFilterNode) child;
                    return new JQuickFilterNode(filter.getPredicate(),
                            new JQuickLimitNode(limit.getLimit(), limit.getOffset(), filter.getChild()));
                }
            }
            return node;
        }
    }
    /**
     * 过滤合并：将连续的Filter节点合并为一个
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
     * 投影合并：将连续的Project节点合并为一个
     */
    private static class ProjectionMergeRule implements OptimizerRule {
        @Override
        public JQuickLogicalPlanNode apply(JQuickLogicalPlanNode node) {
            if (node instanceof JQuickProjectNode) {
                JQuickProjectNode outer = (JQuickProjectNode) node;
                if (outer.getChild() instanceof JQuickProjectNode) {
                    JQuickProjectNode inner = (JQuickProjectNode) outer.getChild();

                    // 构建内层投影的表达式映射
                    Map<String, JQuickExpression> innerExprMap = new HashMap<>();
                    for (JQuickProjectNode.SelectItem item : inner.getSelectItems()) {
                        innerExprMap.put(item.getAlias(), item.getExpression());
                    }

                    // 替换外层投影中的列引用
                    List<JQuickProjectNode.SelectItem> merged = new ArrayList<>();
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
                return new JQuickBinaryExpression(
                        replaceColumns(binary.getLeft(), columnMap),
                        replaceColumns(binary.getRight(), columnMap),
                        binary.getOperator()
                );
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

                // 检查是否为恒真条件
                if (isAlwaysTrue(predicate)) {
                    return filter.getChild();
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
            if (node instanceof JQuickProjectNode) {
                JQuickProjectNode project = (JQuickProjectNode) node;
                Set<String> requiredColumns = project.getSelectItems().stream()
                        .map(JQuickProjectNode.SelectItem::getAlias)
                        .collect(Collectors.toSet());
                return pruneColumns(project, requiredColumns);
            } else if (node instanceof JQuickSortNode) {
                JQuickSortNode sort = (JQuickSortNode) node;
                Set<String> requiredColumns = sort.getOrderByItems().stream()
                        .map(JQuickSortNode.OrderByItem::getColumnName)
                        .collect(Collectors.toSet());
                return pruneColumns(sort, requiredColumns);
            }
            return node;
        }

        private JQuickLogicalPlanNode pruneColumns(JQuickLogicalPlanNode node, Set<String> requiredColumns) {
            if (node instanceof JQuickProjectNode) {
                JQuickProjectNode project = (JQuickProjectNode) node;
                List<JQuickProjectNode.SelectItem> pruned = project.getSelectItems().stream()
                        .filter(item -> requiredColumns.contains(item.getAlias()))
                        .collect(Collectors.toList());

                if (pruned.isEmpty()) {
                    pruned = project.getSelectItems(); // 保留至少一个
                }

                return new JQuickProjectNode(pruned, project.getChild(), project.isDistinct());
            }
            return node;
        }
    }


    /**
     * Join重排序：根据表大小和选择率重新排列Join顺序
     */
    private static class JoinReorderRule implements OptimizerRule {
        @Override
        public JQuickLogicalPlanNode apply(JQuickLogicalPlanNode node) {
            if (node instanceof JQuickJoinNode) {
                JQuickJoinNode join = (JQuickJoinNode) node;
                return reorderJoins(join);
            }
            return node;
        }

        private JQuickLogicalPlanNode reorderJoins(JQuickJoinNode join) {
            // 收集所有Join表
            List<JQuickLogicalPlanNode> tables = new ArrayList<>();
            collectTables(join, tables);
            // 根据预估大小排序（小表优先）
            tables.sort(Comparator.comparingLong(this::estimateSize));
            // 重新构建Join树
            if (tables.size() <= 1) return join;

            JQuickLogicalPlanNode result = tables.get(0);
            for (int i = 1; i < tables.size(); i++) {
                result = new JQuickJoinNode(JQuickJoinType.INNER, result, tables.get(i), null);
            }

            return result;
        }

        private void collectTables(JQuickLogicalPlanNode node, List<JQuickLogicalPlanNode> tables) {
            if (node instanceof JQuickJoinNode) {
                JQuickJoinNode join = (JQuickJoinNode) node;
                collectTables(join.getLeft(), tables);
                collectTables(join.getRight(), tables);
            } else {
                tables.add(node);
            }
        }

        private long estimateSize(JQuickLogicalPlanNode node) {
            if (node instanceof JQuickTableScanNode) {
                return 1000; // 应从统计信息获取
            }
            return 100;
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
            } else if (node instanceof JQuickProjectNode) {
                JQuickProjectNode project = (JQuickProjectNode) node;
                List<JQuickProjectNode.SelectItem> newItems = new ArrayList<>();
                for (JQuickProjectNode.SelectItem item : project.getSelectItems()) {
                    JQuickExpression simplified = simplifyExpression(item.getExpression());
                    newItems.add(new JQuickProjectNode.SelectItem(simplified, item.getAlias()));
                }
                return new JQuickProjectNode(newItems, project.getChild(), project.isDistinct());
            }
            return node;
        }

        private JQuickExpression simplifyExpression(JQuickExpression expr) {
            if (expr instanceof JQuickBinaryExpression) {
                JQuickBinaryExpression binary = (JQuickBinaryExpression) expr;
                JQuickExpression left = simplifyExpression(binary.getLeft());
                JQuickExpression right = simplifyExpression(binary.getRight());
                // NOT (x = y) → x != y
                if (binary.getOperator() == JQuickBinaryOperator.EQ && left instanceof JQuickUnaryExpression) {
                    // 处理NOT
                }

                return new JQuickBinaryExpression(left, right, binary.getOperator());
            }
            return expr;
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
                            new JQuickGroupByNode(groupBy.getGroupKeys(), groupBy.getAggregateItems(),
                                    ((JQuickFilterNode) child).getChild(), groupBy.getHavingCondition()));
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