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

import com.github.paohaijiao.plan.logical.ExecutionContext;
import com.github.paohaijiao.plan.logical.SetOperationNode;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;

import java.util.*;
import java.util.stream.Collectors;

import static com.github.paohaijiao.logic.domain.JQuickSetOperationNode.OperationType.UNION_ALL;

/**
 * 集合操作物理节点 - 处理 UNION, INTERSECT, EXCEPT
 * 支持内存和磁盘两种模式
 */
public class SetOperationPhysicalNode implements PhysicalPlanNode {

    private static final long MEMORY_THRESHOLD = 1000000;  // 100万行以上使用磁盘

    private final PhysicalPlanNode left;
    private final PhysicalPlanNode right;
    private final SetOperationNode.OperationType operationType;

    public SetOperationPhysicalNode(PhysicalPlanNode left, PhysicalPlanNode right,
                                    SetOperationNode.OperationType operationType) {
        this.left = left;
        this.right = right;
        this.operationType = operationType;
    }

    @Override
    public JQuickDataSet execute(ExecutionContext context) {
        JQuickDataSet leftData = left.execute(context);
        JQuickDataSet rightData = right.execute(context);

        long totalRows = leftData.size() + rightData.size();

        // 根据数据量选择内存或磁盘模式
        if (totalRows > MEMORY_THRESHOLD) {
            return executeDiskBased(leftData, rightData);
        }

        return executeMemoryBased(leftData, rightData);
    }

    /**
     * 内存模式 - 使用HashSet/Map
     */
    private JQuickDataSet executeMemoryBased(JQuickDataSet left, JQuickDataSet right) {
        List<JQuickRow> resultRows;

        switch (operationType) {
            case UNION:
                resultRows = executeUnionMemory(left, right, true);
                break;
            case UNION_ALL:
                resultRows = executeUnionMemory(left, right, false);
                break;
            case INTERSECT:
                resultRows = executeIntersectMemory(left, right, true);
                break;
            case INTERSECT_ALL:
                resultRows = executeIntersectMemory(left, right, false);
                break;
            case EXCEPT:
                resultRows = executeExceptMemory(left, right, true);
                break;
            case EXCEPT_ALL:
                resultRows = executeExceptMemory(left, right, false);
                break;
            default:
                throw new RuntimeException("Unsupported operation: " + operationType);
        }

        return new JQuickDataSet(left.getColumns(), resultRows);
    }

    /**
     * 磁盘模式 - 使用外部排序+归并
     */
    private JQuickDataSet executeDiskBased(JQuickDataSet left, JQuickDataSet right) {
        // 简化实现：先排序，然后归并
        // 实际实现应该使用外部排序

        List<JQuickRow> allRows = new ArrayList<>();
        allRows.addAll(left.getRows());
        allRows.addAll(right.getRows());

        // 排序（实际应该外部排序）
        allRows.sort((a, b) -> {
            List<String> columns = left.getColumnNames();
            for (String col : columns) {
                Object v1 = a.get(col);
                Object v2 = b.get(col);
                if (v1 == null && v2 == null) continue;
                if (v1 == null) return -1;
                if (v2 == null) return 1;
                @SuppressWarnings({"rawtypes", "unchecked"})
                int cmp = ((Comparable) v1).compareTo(v2);
                if (cmp != 0) return cmp;
            }
            return 0;
        });

        // 归并去重
        List<JQuickRow> result = new ArrayList<>();
        for (int i = 0; i < allRows.size(); i++) {
            if (i == 0 || !rowsEqual(allRows.get(i), allRows.get(i - 1), left.getColumnNames())) {
                result.add(allRows.get(i));
            }
        }

        return new JQuickDataSet(left.getColumns(), result);
    }

    /**
     * UNION / UNION ALL
     */
    private List<JQuickRow> executeUnionMemory(JQuickDataSet left, JQuickDataSet right, boolean distinct) {
        List<JQuickRow> result = new ArrayList<>();
        result.addAll(left.getRows());
        result.addAll(right.getRows());

        if (distinct) {
            return result.stream().distinct().collect(Collectors.toList());
        }
        return result;
    }

    /**
     * INTERSECT / INTERSECT ALL
     */
    private List<JQuickRow> executeIntersectMemory(JQuickDataSet left, JQuickDataSet right, boolean distinct) {
        if (distinct) {
            Set<JQuickRow> rightSet = new HashSet<>(right.getRows());
            Set<JQuickRow> resultSet = new LinkedHashSet<>();
            for (JQuickRow row : left.getRows()) {
                if (rightSet.contains(row)) {
                    resultSet.add(row);
                }
            }
            return new ArrayList<>(resultSet);
        } else {
            // INTERSECT ALL: 保留重复次数
            Map<JQuickRow, Integer> rightCounts = countOccurrences(right.getRows());
            Map<JQuickRow, Integer> leftCounts = countOccurrences(left.getRows());

            List<JQuickRow> result = new ArrayList<>();
            for (Map.Entry<JQuickRow, Integer> entry : leftCounts.entrySet()) {
                JQuickRow row = entry.getKey();
                int leftCount = entry.getValue();
                int rightCount = rightCounts.getOrDefault(row, 0);
                int intersectCount = Math.min(leftCount, rightCount);
                for (int i = 0; i < intersectCount; i++) {
                    result.add(row);
                }
            }
            return result;
        }
    }

    /**
     * EXCEPT / EXCEPT ALL
     */
    private List<JQuickRow> executeExceptMemory(JQuickDataSet left, JQuickDataSet right, boolean distinct) {
        if (distinct) {
            Set<JQuickRow> rightSet = new HashSet<>(right.getRows());
            Set<JQuickRow> resultSet = new LinkedHashSet<>();
            for (JQuickRow row : left.getRows()) {
                if (!rightSet.contains(row)) {
                    resultSet.add(row);
                }
            }
            return new ArrayList<>(resultSet);
        } else {
            // EXCEPT ALL: 减去出现次数
            Map<JQuickRow, Integer> rightCounts = countOccurrences(right.getRows());
            Map<JQuickRow, Integer> leftCounts = countOccurrences(left.getRows());

            List<JQuickRow> result = new ArrayList<>();
            for (Map.Entry<JQuickRow, Integer> entry : leftCounts.entrySet()) {
                JQuickRow row = entry.getKey();
                int leftCount = entry.getValue();
                int rightCount = rightCounts.getOrDefault(row, 0);
                int exceptCount = Math.max(0, leftCount - rightCount);
                for (int i = 0; i < exceptCount; i++) {
                    result.add(row);
                }
            }
            return result;
        }
    }

    /**
     * 统计行出现次数
     */
    private Map<JQuickRow, Integer> countOccurrences(List<JQuickRow> rows) {
        Map<JQuickRow, Integer> counts = new HashMap<>();
        for (JQuickRow row : rows) {
            counts.merge(row, 1, Integer::sum);
        }
        return counts;
    }

    /**
     * 比较两行是否相等
     */
    private boolean rowsEqual(JQuickRow row1, JQuickRow row2, List<String> columns) {
        for (String col : columns) {
            Object v1 = row1.get(col);
            Object v2 = row2.get(col);
            if (!Objects.equals(v1, v2)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String getNodeType() {
        return "SetOperation";
    }

    @Override
    public long getEstimatedCost() {
        long leftCost = left.getEstimatedCost();
        long rightCost = right.getEstimatedCost();

        switch (operationType) {
            case UNION:
            case UNION_ALL:
                return leftCost + rightCost;
            default:
                return leftCost + rightCost + Math.min(leftCost, rightCost);
        }
    }
}
