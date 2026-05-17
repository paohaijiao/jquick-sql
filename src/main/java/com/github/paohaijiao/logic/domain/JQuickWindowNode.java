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
package com.github.paohaijiao.logic.domain;

import com.github.paohaijiao.context.JQuickExecutionContext;
import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.expression.domain.JQuickColumnRefExpression;
import com.github.paohaijiao.logic.JQuickLogicalPlanNode;
import com.github.paohaijiao.logic.JQuickLogicalPlanVisitor;
import com.github.paohaijiao.statement.JQuickColumnMeta;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;

import java.util.*;
import java.util.stream.Collectors;

/**
 * packageName com.github.paohaijiao.plan.logic.domain
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/5/16
 */
public class JQuickWindowNode implements JQuickLogicalPlanNode {
    private final List<WindowFunction> windowFunctions;
    private final JQuickLogicalPlanNode child;

    public JQuickWindowNode(List<WindowFunction> windowFunctions, JQuickLogicalPlanNode child) {
        this.windowFunctions = Collections.unmodifiableList(new ArrayList<>(windowFunctions));
        this.child = child;
    }

    @Override
    public JQuickDataSet execute(JQuickExecutionContext context) {
        JQuickDataSet data = child.execute(context);
        List<JQuickRow> rows = data.getRows();
        for (WindowFunction wf : windowFunctions) {
            rows = computeWindowFunction(rows, wf);
        }
        JQuickDataSet.Builder builder = JQuickDataSet.builder();

        // 添加原始列
        for (JQuickColumnMeta col : data.getColumns()) {
            builder.addColumn(col.getName(), col.getType(), col.getSource());
        }

        // 添加窗口函数结果列
        for (WindowFunction wf : windowFunctions) {
            builder.addColumn(wf.getAlias(), wf.getReturnType(), "window");
        }

        // 添加行
        for (JQuickRow row : rows) {
            builder.addRow(row);
        }

        return builder.build();
    }

    /**
     * 计算窗口函数
     */
    private List<JQuickRow> computeWindowFunction(List<JQuickRow> rows, WindowFunction wf) {
        WindowSpec spec = wf.getWindowSpec();

        // 1. 分区
        Map<Object, List<JQuickRow>> partitions = partitionRows(rows, spec.getPartitionKeys());

        List<JQuickRow> result = new ArrayList<>();

        for (Map.Entry<Object, List<JQuickRow>> entry : partitions.entrySet()) {
            List<JQuickRow> partitionRows = entry.getValue();

            // 2. 分区内排序
            sortPartition(partitionRows, spec.getOrderKeys());

            // 3. 计算窗口函数值
            computeFunctionOnPartition(partitionRows, wf);

            result.addAll(partitionRows);
        }

        return result;
    }

    /**
     * 按分区键分组
     */
    private Map<Object, List<JQuickRow>> partitionRows(List<JQuickRow> rows, List<JQuickExpression> partitionKeys) {
        if (partitionKeys.isEmpty()) {
            Map<Object, List<JQuickRow>> result = new HashMap<>();
            result.put("__single_partition__", new ArrayList<>(rows));
            return result;
        }

        return rows.stream().collect(Collectors.groupingBy(row -> {
            if (partitionKeys.size() == 1) {
                return partitionKeys.get(0).evaluate(row);
            }
            List<Object> key = new ArrayList<>();
            for (JQuickExpression keyExpr : partitionKeys) {
                key.add(keyExpr.evaluate(row));
            }
            return key;
        }));
    }

    /**
     * 分区内排序
     */
    private void sortPartition(List<JQuickRow> rows, List<OrderByKey> orderKeys) {
        if (orderKeys.isEmpty()) return;

        rows.sort((row1, row2) -> {
            for (OrderByKey key : orderKeys) {
                Object v1 = key.getExpression().evaluate(row1);
                Object v2 = key.getExpression().evaluate(row2);

                if (v1 == null && v2 == null) continue;
                if (v1 == null) return key.isNullsFirst() ? -1 : 1;
                if (v2 == null) return key.isNullsFirst() ? 1 : -1;

                @SuppressWarnings({"rawtypes", "unchecked"})
                int cmp = ((Comparable) v1).compareTo(v2);
                if (cmp != 0) {
                    return key.isAscending() ? cmp : -cmp;
                }
            }
            return 0;
        });
    }

    /**
     * 在分区上计算窗口函数
     */
    private void computeFunctionOnPartition(List<JQuickRow> rows, WindowFunction wf) {
        String alias = wf.getAlias();
        WindowSpec spec = wf.getWindowSpec();
        WindowFrame frame = spec.getFrame();

        for (int i = 0; i < rows.size(); i++) {
            // 确定窗口帧范围
            int start = getFrameStart(i, rows.size(), frame);
            int end = getFrameEnd(i, rows.size(), frame);

            // 获取窗口帧内的行
            List<JQuickRow> frameRows = rows.subList(start, end + 1);

            // 计算窗口函数值
            Object result = computeWindowFunctionValue(wf, frameRows, i, rows);

            // 将结果添加到行中
            JQuickRow row = rows.get(i);
            row.put(alias, result);
        }
    }

    /**
     * 计算窗口函数值
     */
    private Object computeWindowFunctionValue(WindowFunction wf, List<JQuickRow> frameRows,
                                              int currentIndex, List<JQuickRow> allRows) {
        String funcName = wf.getFunctionName().toLowerCase();
        JQuickExpression arg = wf.getArgument();

        switch (funcName) {
            // 排名函数
            case "row_number":
                return (long) (currentIndex + 1);

            case "rank":
                return computeRank(frameRows, currentIndex, allRows, wf.getOrderKeys());

            case "dense_rank":
                return computeDenseRank(frameRows, currentIndex, allRows, wf.getOrderKeys());

            case "percent_rank":
                return computePercentRank(frameRows, currentIndex, allRows);

            case "cume_dist":
                return computeCumeDist(frameRows, currentIndex, allRows);

            case "ntile":
                if (arg != null) {
                    Integer buckets = (Integer) arg.evaluate(null);
                    if (buckets != null && buckets > 0) {
                        return computeNtile(currentIndex, allRows.size(), buckets);
                    }
                }
                return null;

            // 值函数
            case "lag":
                return computeLag(frameRows, currentIndex, allRows, arg);

            case "lead":
                return computeLead(frameRows, currentIndex, allRows, arg);

            case "first_value":
                return frameRows.isEmpty() ? null :
                        wf.getArgument().evaluate(frameRows.get(0));

            case "last_value":
                return frameRows.isEmpty() ? null :
                        wf.getArgument().evaluate(frameRows.get(frameRows.size() - 1));

            case "nth_value":
                return computeNthValue(frameRows, arg);

            // 聚合函数
            case "sum":
                return computeSum(frameRows, wf.getArgument());

            case "avg":
                return computeAvg(frameRows, wf.getArgument());

            case "count":
                return computeCount(frameRows, wf.getArgument());

            case "max":
                return computeMax(frameRows, wf.getArgument());

            case "min":
                return computeMin(frameRows, wf.getArgument());

            default:
                return null;
        }
    }

    private long computeRank(List<JQuickRow> frameRows, int currentIndex, List<JQuickRow> allRows, List<OrderByKey> orderKeys) {
        if (currentIndex == 0) return 1L;
        JQuickRow currentRow = allRows.get(currentIndex);
        long rank = 1L;
        for (int i = 0; i < currentIndex; i++) {
            if (!isEqualByOrderKeys(allRows.get(i), currentRow, orderKeys)) {
                rank = i + 2;
            }
        }
        return rank;
    }

    private long computeDenseRank(List<JQuickRow> frameRows, int currentIndex, List<JQuickRow> allRows, List<OrderByKey> orderKeys) {
        if (currentIndex == 0) return 1L;
        JQuickRow currentRow = allRows.get(currentIndex);
        long rank = 1L;
        for (int i = 0; i < currentIndex; i++) {
            if (!isEqualByOrderKeys(allRows.get(i), currentRow, orderKeys)) {
                rank++;
            }
        }
        return rank;
    }

    private double computePercentRank(List<JQuickRow> frameRows, int currentIndex, List<JQuickRow> allRows) {
        if (allRows.size() <= 1) return 0.0;
        long rank = computeRank(frameRows, currentIndex, allRows, Collections.emptyList());
        return (double) (rank - 1) / (allRows.size() - 1);
    }

    private double computeCumeDist(List<JQuickRow> frameRows, int currentIndex, List<JQuickRow> allRows) {
        if (allRows.isEmpty()) return 0.0;
        return (double) (currentIndex + 1) / allRows.size();
    }

    private long computeNtile(int currentIndex, int totalSize, int buckets) {
        if (buckets <= 0) return 1L;
        long bucketSize = (totalSize + buckets - 1) / buckets;
        return (currentIndex / bucketSize) + 1;
    }


    private Object computeLag(List<JQuickRow> frameRows, int currentIndex, List<JQuickRow> allRows, JQuickExpression arg) {
        int offset = 1;
        Object defaultValue = null;
        if (arg != null) {
            // 解析参数: LAG(expr, offset, default)
            // 简化处理
        }

        int targetIndex = currentIndex - offset;
        if (targetIndex >= 0 && targetIndex < allRows.size()) {
            return allRows.get(targetIndex).get(getColumnName(arg));
        }
        return defaultValue;
    }

    private Object computeLead(List<JQuickRow> frameRows, int currentIndex, List<JQuickRow> allRows, JQuickExpression arg) {
        int offset = 1;
        Object defaultValue = null;
        int targetIndex = currentIndex + offset;
        if (targetIndex >= 0 && targetIndex < allRows.size()) {
            return allRows.get(targetIndex).get(getColumnName(arg));
        }
        return defaultValue;
    }

    private Object computeNthValue(List<JQuickRow> frameRows, JQuickExpression arg) {
        // Nth_value 需要指定第几个，简化处理返回第一个
        if (frameRows.isEmpty()) return null;
        return arg.evaluate(frameRows.get(0));
    }


    private Double computeSum(List<JQuickRow> rows, JQuickExpression arg) {
        return rows.stream()
                .map(row -> arg.evaluate(row))
                .filter(v -> v instanceof Number)
                .mapToDouble(v -> ((Number) v).doubleValue())
                .sum();
    }

    private Double computeAvg(List<JQuickRow> rows, JQuickExpression arg) {
        return rows.stream()
                .map(row -> arg.evaluate(row))
                .filter(v -> v instanceof Number)
                .mapToDouble(v -> ((Number) v).doubleValue())
                .average()
                .orElse(0.0);
    }

    private Long computeCount(List<JQuickRow> rows, JQuickExpression arg) {
        if (arg == null) {
            return (long) rows.size();
        }
        return rows.stream()
                .map(row -> arg.evaluate(row))
                .filter(Objects::nonNull)
                .count();
    }

    private Object computeMax(List<JQuickRow> rows, JQuickExpression arg) {
        return rows.stream()
                .map(row -> arg.evaluate(row))
                .filter(Objects::nonNull)
                .max((a, b) -> {
                    if (a instanceof Comparable && b instanceof Comparable) {
                        return ((Comparable) a).compareTo(b);
                    }
                    return a.toString().compareTo(b.toString());
                })
                .orElse(null);
    }

    private Object computeMin(List<JQuickRow> rows, JQuickExpression arg) {
        return rows.stream()
                .map(row -> arg.evaluate(row))
                .filter(Objects::nonNull)
                .min((a, b) -> {
                    if (a instanceof Comparable && b instanceof Comparable) {
                        return ((Comparable) a).compareTo(b);
                    }
                    return a.toString().compareTo(b.toString());
                })
                .orElse(null);
    }

    private int getFrameStart(int currentIndex, int totalSize, WindowFrame frame) {
        if (frame == null) return 0;

        switch (frame.getStartType()) {
            case UNBOUNDED_PRECEDING:
                return 0;
            case CURRENT_ROW:
                return currentIndex;
            case PRECEDING:
                int offset = frame.getStartOffset() != null ?
                        getOffsetValue(frame.getStartOffset()) : 1;
                return Math.max(0, currentIndex - offset);
            default:
                return 0;
        }
    }

    private int getFrameEnd(int currentIndex, int totalSize, WindowFrame frame) {
        if (frame == null) return totalSize - 1;

        switch (frame.getEndType()) {
            case UNBOUNDED_FOLLOWING:
                return totalSize - 1;
            case CURRENT_ROW:
                return currentIndex;
            case FOLLOWING:
                int offset = frame.getEndOffset() != null ?
                        getOffsetValue(frame.getEndOffset()) : 1;
                return Math.min(totalSize - 1, currentIndex + offset);
            default:
                return totalSize - 1;
        }
    }

    private int getOffsetValue(JQuickExpression offsetExpr) {
        Object val = offsetExpr.evaluate(null);
        if (val instanceof Number) {
            return ((Number) val).intValue();
        }
        return 1;
    }

    private boolean isEqualByOrderKeys(JQuickRow row1, JQuickRow row2, List<OrderByKey> orderKeys) {
        for (OrderByKey key : orderKeys) {
            Object v1 = key.getExpression().evaluate(row1);
            Object v2 = key.getExpression().evaluate(row2);
            if (!Objects.equals(v1, v2)) {
                return false;
            }
        }
        return true;
    }

    private String getColumnName(JQuickExpression expr) {
        if (expr instanceof JQuickColumnRefExpression) {
            return ((JQuickColumnRefExpression) expr).getColumnName();
        }
        return expr.toSql();
    }

    @Override
    public String getNodeType() {
        return "Window";
    }

    @Override
    public List<JQuickLogicalPlanNode> getChildren() {
        return Collections.singletonList(child);
    }

    @Override
    public void accept(JQuickLogicalPlanVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public List<String> getOutputColumns() {
        List<String> columns = new ArrayList<>(child.getOutputColumns());
        for (WindowFunction wf : windowFunctions) {
            columns.add(wf.getAlias());
        }
        return columns;
    }

    @Override
    public JQuickLogicalPlanNode clone() {
        List<WindowFunction> clonedFunctions = new ArrayList<>();
        for (WindowFunction wf : windowFunctions) {
            clonedFunctions.add(wf.clone());
        }
        return new JQuickWindowNode(clonedFunctions, child.clone());
    }

    public List<WindowFunction> getWindowFunctions() {
        return windowFunctions;
    }

    public JQuickLogicalPlanNode getChild() {
        return child;
    }

    /**
     * 窗口函数定义
     */
    public static class WindowFunction {
        private final String functionName;      // 函数名: ROW_NUMBER, RANK, SUM, AVG, LAG等
        private final JQuickExpression argument;       // 函数参数
        private final WindowSpec windowSpec;     // OVER子句定义
        private final String alias;              // 别名

        public WindowFunction(String functionName, JQuickExpression argument, WindowSpec windowSpec, String alias) {
            this.functionName = functionName;
            this.argument = argument;
            this.windowSpec = windowSpec;
            this.alias = alias != null ? alias : functionName;
        }

        public Class<?> getReturnType() {
            switch (functionName.toLowerCase()) {
                case "row_number":
                case "rank":
                case "dense_rank":
                case "ntile":
                    return Long.class;
                case "percent_rank":
                case "cume_dist":
                    return Double.class;
                case "sum":
                case "avg":
                    return Double.class;
                case "count":
                    return Long.class;
                default:
                    return Object.class;
            }
        }

        public String getFunctionName() {
            return functionName;
        }

        public JQuickExpression getArgument() {
            return argument;
        }

        public WindowSpec getWindowSpec() {
            return windowSpec;
        }

        public String getAlias() {
            return alias;
        }

        public List<OrderByKey> getOrderKeys() {
            return windowSpec.getOrderKeys();
        }

        public WindowFunction clone() {
            JQuickExpression clonedArg = argument != null ? argument.clone() : null;
            return new WindowFunction(functionName, clonedArg, windowSpec.clone(), alias);
        }
    }

    /**
     * 窗口规格 - OVER子句
     */
    public static class WindowSpec {
        private final List<JQuickExpression> partitionKeys;  // PARTITION BY
        private final List<OrderByKey> orderKeys;       // ORDER BY
        private final WindowFrame frame;                // 窗口帧

        public WindowSpec(List<JQuickExpression> partitionKeys, List<OrderByKey> orderKeys, WindowFrame frame) {
            this.partitionKeys = partitionKeys != null ? partitionKeys : Collections.emptyList();
            this.orderKeys = orderKeys != null ? orderKeys : Collections.emptyList();
            this.frame = frame;
        }

        public List<JQuickExpression> getPartitionKeys() {
            return partitionKeys;
        }

        public List<OrderByKey> getOrderKeys() {
            return orderKeys;
        }

        public WindowFrame getFrame() {
            return frame;
        }

        public WindowSpec clone() {
            List<JQuickExpression> clonedPartitionKeys = new ArrayList<>();
            for (JQuickExpression key : partitionKeys) {
                clonedPartitionKeys.add(key.clone());
            }
            List<OrderByKey> clonedOrderKeys = new ArrayList<>();
            for (OrderByKey key : orderKeys) {
                clonedOrderKeys.add(key.clone());
            }
            WindowFrame clonedFrame = frame != null ? frame.clone() : null;
            return new WindowSpec(clonedPartitionKeys, clonedOrderKeys, clonedFrame);
        }
    }

    /**
     * 排序键
     */
    public static class OrderByKey {
        private final JQuickExpression expression;
        private final boolean ascending;
        private final boolean nullsFirst;

        public OrderByKey(JQuickExpression expression, boolean ascending, boolean nullsFirst) {
            this.expression = expression;
            this.ascending = ascending;
            this.nullsFirst = nullsFirst;
        }

        public JQuickExpression getExpression() {
            return expression;
        }

        public boolean isAscending() {
            return ascending;
        }

        public boolean isNullsFirst() {
            return nullsFirst;
        }

        public OrderByKey clone() {
            return new OrderByKey(expression.clone(), ascending, nullsFirst);
        }
    }

    /**
     * 窗口帧 - ROWS/RANGE BETWEEN
     */
    public static class WindowFrame {
        private final FrameType frameType;
        private final BoundaryType startType;
        private final JQuickExpression startOffset;
        private final BoundaryType endType;
        private final JQuickExpression endOffset;
        public WindowFrame(FrameType frameType, BoundaryType startType, JQuickExpression startOffset, BoundaryType endType, JQuickExpression endOffset) {
            this.frameType = frameType;
            this.startType = startType;
            this.startOffset = startOffset;
            this.endType = endType;
            this.endOffset = endOffset;
        }

        public FrameType getFrameType() {
            return frameType;
        }

        public BoundaryType getStartType() {
            return startType;
        }

        public JQuickExpression getStartOffset() {
            return startOffset;
        }

        public BoundaryType getEndType() {
            return endType;
        }

        public JQuickExpression getEndOffset() {
            return endOffset;
        }

        public WindowFrame clone() {
            return new WindowFrame(frameType, startType,
                    startOffset != null ? startOffset.clone() : null,
                    endType, endOffset != null ? endOffset.clone() : null);
        }

        public enum FrameType {ROWS, RANGE}

        public enum BoundaryType {
            UNBOUNDED_PRECEDING, PRECEDING, CURRENT_ROW,
            FOLLOWING, UNBOUNDED_FOLLOWING
        }
    }
}
