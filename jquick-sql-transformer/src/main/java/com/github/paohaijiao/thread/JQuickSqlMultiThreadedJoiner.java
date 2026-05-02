package com.github.paohaijiao.thread;

import com.github.paohaijiao.condition.JQuickSqlCondition;
import com.github.paohaijiao.evalue.JQuickSqlConditionEvaluator;
import com.github.paohaijiao.exception.JAssert;
import com.github.paohaijiao.expression.JQuickSqlColumnExpression;
import com.github.paohaijiao.expression.JQuickSqlExpression;
import com.github.paohaijiao.expression.JQuickSqlFunctionCallExpression;
import com.github.paohaijiao.expression.JQuickSqlOrderByExpression;
import com.github.paohaijiao.factory.JQuickSqlDataSetJoinerStrategy;
import com.github.paohaijiao.function.JQuickSqlAggregateFunctionFactory;
import com.github.paohaijiao.handler.JQuickSqlBaseHandler;
import com.github.paohaijiao.join.JQuickSqlJoinCondition;
import com.github.paohaijiao.statement.JQuickColumnMeta;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 多线程版本的数据集连接处理器
 * 继承自JQuickSqlBaseHandler，实现JQuickSqlDataSetJoinerStrategy接口
 * 利用并行流和CompletableFuture大幅提升大数据集处理性能
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/8/17
 */
public class JQuickSqlMultiThreadedJoiner extends JQuickSqlBaseHandler implements JQuickSqlDataSetJoinerStrategy {

    // 并行处理阈值，小于此值使用单线程
    private static final int PARALLEL_THRESHOLD = 500;

    // 自定义线程池
    private final ForkJoinPool parallelPool;

    // 是否启用并行处理
    private final boolean enableParallel;

    public JQuickSqlMultiThreadedJoiner() {
        this.parallelPool = new ForkJoinPool(Math.max(4, Runtime.getRuntime().availableProcessors()));
        this.enableParallel = true;
    }

    public JQuickSqlMultiThreadedJoiner(ForkJoinPool pool) {
        this.parallelPool = pool;
        this.enableParallel = true;
    }

    public JQuickSqlMultiThreadedJoiner(boolean enableParallel) {
        this.parallelPool = new ForkJoinPool(Math.max(4, Runtime.getRuntime().availableProcessors()));
        this.enableParallel = enableParallel;
    }

    @Override
    public JQuickDataSet innerJoin(JQuickDataSet left, JQuickDataSet right, JQuickSqlJoinCondition condition) {
        List<JQuickColumnMeta> resultColumns = mergeColumns(left, right);
        if (left.isEmpty() || right.isEmpty()) {
            return new JQuickDataSet(resultColumns, new ArrayList<>());
        }
        List<JQuickRow> resultRows = processInParallel(left.getRows(), (List<JQuickRow> rows) -> {
            List<JQuickRow> batchResult = new ArrayList<>();
            for (JQuickRow leftRow : rows) {
                for (JQuickRow rightRow : right.getRows()) {
                    if (condition.test(leftRow, rightRow)) {
                        batchResult.add(mergeRows(leftRow, rightRow));
                    }
                }
            }
            return batchResult;
        });
        return new JQuickDataSet(resultColumns, resultRows);
    }

    @Override
    public JQuickDataSet leftJoin(JQuickDataSet left, JQuickDataSet right, JQuickSqlJoinCondition condition) {
        List<JQuickColumnMeta> resultColumns = mergeColumns(left, right);
        if (left.isEmpty()) {
            return new JQuickDataSet(resultColumns, new ArrayList<>());
        }
        if (right.isEmpty()) {// 右侧为空，左侧所有行加上null值
            List<JQuickRow> resultRows = processInParallel(left.getRows(), (List<JQuickRow> rows) -> {
                List<JQuickRow> batchResult = new ArrayList<>();
                JQuickRow nullRow = createNullRow(right);
                for (JQuickRow leftRow : rows) {
                    batchResult.add(mergeRows(leftRow, nullRow));
                }
                return batchResult;
            });
            return new JQuickDataSet(resultColumns, resultRows);
        }

        // 为右侧数据集构建索引
        Map<Object, List<JQuickRow>> rightIndex = buildIndexForCondition(right, condition);
        List<JQuickRow> resultRows = processInParallel(left.getRows(), (List<JQuickRow> rows) -> {
            List<JQuickRow> batchResult = new ArrayList<>();
            for (JQuickRow leftRow : rows) {
                Object key = extractKey(leftRow, condition);
                List<JQuickRow> matchingRows = rightIndex.get(key);
                boolean hasMatch = false;
                if (matchingRows != null) {
                    for (JQuickRow rightRow : matchingRows) {
                        if (condition.test(leftRow, rightRow)) {
                            batchResult.add(mergeRows(leftRow, rightRow));
                            hasMatch = true;
                        }
                    }
                }
                if (!hasMatch) {
                    batchResult.add(mergeRows(leftRow, createNullRow(right)));
                }
            }
            return batchResult;
        });
        return new JQuickDataSet(resultColumns, resultRows);
    }

    @Override
    public JQuickDataSet rightJoin(JQuickDataSet left, JQuickDataSet right, JQuickSqlJoinCondition condition) {
        List<JQuickColumnMeta> resultColumns = mergeColumns(left, right);
        if (right.isEmpty()) {
            return new JQuickDataSet(resultColumns, new ArrayList<>());
        }
        if (left.isEmpty()) {
            List<JQuickRow> resultRows = processInParallel(right.getRows(), (List<JQuickRow> rows) -> {
                List<JQuickRow> batchResult = new ArrayList<>();
                JQuickRow nullRow = createNullRow(left);
                for (JQuickRow rightRow : rows) {
                    batchResult.add(mergeRows(nullRow, rightRow));
                }
                return batchResult;
            });
            return new JQuickDataSet(resultColumns, resultRows);
        }
        // 为左侧数据集构建索引
        Map<Object, List<JQuickRow>> leftIndex = buildIndexForCondition(left, condition);
        List<JQuickRow> resultRows = processInParallel(right.getRows(), (List<JQuickRow> rows) -> {
            List<JQuickRow> batchResult = new ArrayList<>();
            for (JQuickRow rightRow : rows) {
                Object key = extractKey(rightRow, condition);
                List<JQuickRow> matchingRows = leftIndex.get(key);
                boolean hasMatch = false;
                if (matchingRows != null) {
                    for (JQuickRow leftRow : matchingRows) {
                        if (condition.test(leftRow, rightRow)) {
                            batchResult.add(mergeRows(leftRow, rightRow));
                            hasMatch = true;
                        }
                    }
                }
                if (!hasMatch) {
                    batchResult.add(mergeRows(createNullRow(left), rightRow));
                }
            }
            return batchResult;
        });
        return new JQuickDataSet(resultColumns, resultRows);
    }

    @Override
    public JQuickDataSet fullOuterJoin(JQuickDataSet left, JQuickDataSet right, JQuickSqlJoinCondition condition) {
        List<JQuickColumnMeta> resultColumns = mergeColumns(left, right);
        if (left.isEmpty() && right.isEmpty()) {
            return new JQuickDataSet(resultColumns, new ArrayList<>());
        }
        // 使用并发集合记录匹配
        Set<JQuickRow> matchedLeftRows = ConcurrentHashMap.newKeySet();
        Set<JQuickRow> matchedRightRows = ConcurrentHashMap.newKeySet();
        // 并行处理匹配的行
        List<JQuickRow> matchedResult = processCartesianInParallel(left.getRows(), right.getRows(), condition,
                (leftRow, rightRow) -> {
                    if (condition.test(leftRow, rightRow)) {
                        matchedLeftRows.add(leftRow);
                        matchedRightRows.add(rightRow);
                        return mergeRows(leftRow, rightRow);
                    }
                    return null;
                });
        List<JQuickRow> resultRows = new CopyOnWriteArrayList<>();
        resultRows.addAll(matchedResult.stream().filter(Objects::nonNull).collect(Collectors.toList()));
        // 处理未匹配的左侧行
        JQuickRow nullRightRow = createNullRow(right);
        List<JQuickRow> unmatchedLeftRows = left.getRows().parallelStream()
                .filter(leftRow -> !matchedLeftRows.contains(leftRow))
                .map(leftRow -> mergeRows(leftRow, nullRightRow))
                .collect(Collectors.toList());
        resultRows.addAll(unmatchedLeftRows);
        // 处理未匹配的右侧行
        JQuickRow nullLeftRow = createNullRow(left);
        List<JQuickRow> unmatchedRightRows = right.getRows().parallelStream()
                .filter(rightRow -> !matchedRightRows.contains(rightRow))
                .map(rightRow -> mergeRows(nullLeftRow, rightRow))
                .collect(Collectors.toList());
        resultRows.addAll(unmatchedRightRows);
        return new JQuickDataSet(resultColumns, new ArrayList<>(resultRows));
    }

    @Override
    public JQuickDataSet crossJoin(JQuickDataSet left, JQuickDataSet right) {
        return innerJoin(left, right, (l, r) -> true);
    }

    @Override
    public JQuickDataSet naturalJoin(JQuickDataSet left, JQuickDataSet right) {
        Set<String> leftColumns = new HashSet<>(left.getColumnNames());
        Set<String> rightColumns = new HashSet<>(right.getColumnNames());
        Set<String> commonColumns = leftColumns.stream()
                .filter(rightColumns::contains)
                .collect(Collectors.toSet());
        if (commonColumns.isEmpty()) {
            return crossJoin(left, right);
        }
        List<JQuickColumnMeta> resultColumns = new ArrayList<>();
        resultColumns.addAll(left.getColumns());
        resultColumns.addAll(right.getColumns());
        // 并行处理自然连接
        List<JQuickRow> resultRows = processCartesianInParallel(left.getRows(), right.getRows(), null,
                (leftRow, rightRow) -> {
                    if (isMatch(leftRow, rightRow, commonColumns)) {
                        return mergeRows(leftRow, rightRow);
                    }
                    return null;
                }).stream().filter(Objects::nonNull).collect(Collectors.toList());

        return new JQuickDataSet(resultColumns, resultRows);
    }

    @Override
    public JQuickDataSet union(JQuickDataSet ds1, JQuickDataSet ds2) {
        validateUnionCompatible(ds1, ds2);
        List<JQuickRow> combinedRows = new ArrayList<>();
        combinedRows.addAll(ds1.getRows());
        combinedRows.addAll(ds2.getRows());
        return new JQuickDataSet(ds1.getColumns(), combinedRows);
    }

    @Override
    public JQuickDataSet intersect(JQuickDataSet ds1, JQuickDataSet ds2) {
        validateUnionCompatible(ds1, ds2);
        Set<JQuickRow> set2 = new HashSet<>(ds2.getRows());
        List<JQuickRow> result = ds1.getRows().parallelStream()
                .filter(set2::contains)
                .distinct()
                .collect(Collectors.toList());
        return new JQuickDataSet(ds1.getColumns(), result);
    }

    @Override
    public JQuickDataSet minus(JQuickDataSet ds1, JQuickDataSet ds2) {
        validateUnionCompatible(ds1, ds2);
        Set<JQuickRow> set2 = new HashSet<>(ds2.getRows());
        List<JQuickRow> result = ds1.getRows().parallelStream()
                .filter(row -> !set2.contains(row))
                .collect(Collectors.toList());
        return new JQuickDataSet(ds1.getColumns(), result);
    }

    @Override
    public JQuickDataSet selectColumns(JQuickDataSet dataset, List<String> columnNames) {
        List<JQuickColumnMeta> currentColumns = dataset.getColumns();
        List<JQuickColumnMeta> newColumns = currentColumns.parallelStream()
                .filter(col -> columnNames.contains(col.getName()))
                .collect(Collectors.toList());
        List<JQuickRow> newRows = dataset.getRows().parallelStream()
                .map(row -> {
                    JQuickRow newRow = new JQuickRow();
                    columnNames.forEach(col -> {
                        if (row.containsKey(col)) {
                            newRow.put(col, row.get(col));
                        }
                    });
                    return newRow;
                })
                .collect(Collectors.toList());
        return new JQuickDataSet(newColumns, newRows);
    }

    @Override
    public JQuickDataSet filter(JQuickDataSet dataset, JQuickSqlCondition condition) {
        JQuickSqlConditionEvaluator evaluator = new JQuickSqlConditionEvaluator();
        List<JQuickRow> filteredRows = dataset.getRows().parallelStream()
                .filter(row -> evaluator.evaluateCondition(condition, row))
                .map(row -> {
                    JQuickRow newRow = new JQuickRow();
                    newRow.putAll(row);
                    return newRow;
                })
                .collect(Collectors.toList());
        return new JQuickDataSet(dataset.getColumns(), filteredRows);
    }

    @Override
    public JQuickDataSet transform(JQuickDataSet dataset, Map<String, JQuickSqlFunctionCallExpression> transformations) {
        List<JQuickColumnMeta> newColumns = new ArrayList<>();
        for (JQuickColumnMeta column : dataset.getColumns()) {
            if (transformations.containsKey(column.getName())) {
                newColumns.add(new JQuickColumnMeta(
                        column.getName(),
                        Object.class,
                        column.getSource() + "_transformed"
                ));
            } else {
                newColumns.add(column);
            }
        }

        List<JQuickRow> newRows = dataset.getRows().parallelStream()
                .map(row -> transformRow(row, transformations))
                .collect(Collectors.toList());
        return new JQuickDataSet(newColumns, newRows);
    }

    @Override
    public JQuickDataSet sort(JQuickDataSet dataset, List<JQuickSqlOrderByExpression> orderByExpressions) {
        if (orderByExpressions == null || orderByExpressions.isEmpty()) {
            return dataset;
        }
        Comparator<Map<String, Object>> comparator = createComparatorChain(orderByExpressions);
        List<JQuickRow> sortedRows;
        if (enableParallel && dataset.size() > PARALLEL_THRESHOLD) {
            sortedRows = dataset.getRows().parallelStream()
                    .sorted(comparator)
                    .collect(Collectors.toList());
        } else {
            sortedRows = new ArrayList<>(dataset.getRows());
            sortedRows.sort(comparator);
        }
        return new JQuickDataSet(dataset.getColumns(), sortedRows);
    }

    @Override
    public JQuickDataSet aggregate(JQuickDataSet dataset, List<String> groupBy, Map<String, JQuickSqlFunctionCallExpression> aggregations) {
        // 并行分组聚合
        Map<List<Object>, List<Map<String, Object>>> groups = dataset.getRows().parallelStream()
                .collect(Collectors.groupingByConcurrent(
                        row -> groupBy.stream()
                                .map(row::get)
                                .collect(Collectors.toList())
                ));
        // 构建结果列
        List<JQuickColumnMeta> newColumns = new ArrayList<>();
        for (String col : groupBy) {
            Class<?> type = dataset.getColumns().stream()
                    .filter(c -> c.getName().equals(col))
                    .findFirst()
                    .<Class<?>>map(JQuickColumnMeta::getType)
                    .orElse(Object.class);
            newColumns.add(new JQuickColumnMeta(col, type, "group_by"));
        }
        for (String aggCol : aggregations.keySet()) {
            newColumns.add(new JQuickColumnMeta(aggCol, Object.class, "aggregate"));
        }
        // 并行计算聚合结果
        List<JQuickRow> resultRows = groups.entrySet().parallelStream()
                .map(entry -> {
                    JQuickRow resultRow = new JQuickRow();

                    for (int i = 0; i < groupBy.size(); i++) {
                        resultRow.put(groupBy.get(i), entry.getKey().get(i));
                    }

                    for (Map.Entry<String, JQuickSqlFunctionCallExpression> aggEntry : aggregations.entrySet()) {
                        String colName = aggEntry.getKey();
                        JQuickSqlFunctionCallExpression function = aggEntry.getValue();
                        JAssert.isTrue(function.getArguments().size() == 1, "the aggregation function must have exactly one argument");
                        JQuickSqlExpression jExpression = function.getArguments().get(0);
                        List<Object> values = entry.getValue().stream()
                                .map(row -> {
                                    if (jExpression instanceof JQuickSqlColumnExpression) {
                                        return row.get(((JQuickSqlColumnExpression) jExpression).getColumnName());
                                    }
                                    throw new UnsupportedOperationException("only column expressions supported");
                                })
                                .filter(Objects::nonNull)
                                .collect(Collectors.toList());
                        if (function.isDistinct()) {
                            values = values.stream().distinct().collect(Collectors.toList());
                        }
                        Function<List<Object>, Object> func = JQuickSqlAggregateFunctionFactory.getFunction(function.getFunctionName());
                        JAssert.notNull(func, "function not supported");
                        Object aggValue = func.apply(values);
                        resultRow.put(colName, aggValue);
                    }
                    return resultRow;
                })
                .collect(Collectors.toList());

        return new JQuickDataSet(newColumns, resultRows);
    }

    @Override
    public JQuickDataSet alias(JQuickDataSet dataset, Map<String, JQuickSqlExpression> aliases) {
        List<JQuickColumnMeta> newColumns = new ArrayList<>();
        for (JQuickColumnMeta column : dataset.getColumns()) {
            if (!aliases.containsValue(new JQuickSqlColumnExpression(column.getName()))) {
                newColumns.add(column);
            }
        }
        for (Map.Entry<String, JQuickSqlExpression> entry : aliases.entrySet()) {
            String alias = entry.getKey();
            JQuickSqlExpression expr = entry.getValue();
            Class<?> type = determineExpressionType(expr);
            newColumns.add(new JQuickColumnMeta(alias, type, "alias"));
        }
        List<JQuickRow> newRows = dataset.getRows().parallelStream()
                .map(row -> createAliasedRow(row, aliases))
                .collect(Collectors.toList());
        return new JQuickDataSet(newColumns, newRows);
    }

    @Override
    public JQuickDataSet limit(JQuickDataSet dataset, Integer limit, Integer offset) {
        int finalLimit = limit != null ? limit : Integer.MAX_VALUE;
        int finalOffset = offset != null ? offset : 0;
        if (finalLimit <= 0 || finalOffset < 0) {
            return new JQuickDataSet(dataset.getColumns(), Collections.emptyList());
        }
        List<JQuickRow> processedRows = dataset.getRows().stream()
                .skip(finalOffset)
                .limit(finalLimit)
                .collect(Collectors.toList());
        return new JQuickDataSet(dataset.getColumns(), processedRows);
    }

    /**
     * 为连接条件构建索引
     */
    private Map<Object, List<JQuickRow>> buildIndexForCondition(JQuickDataSet dataset, JQuickSqlJoinCondition condition) {
        return dataset.getRows().parallelStream()
                .collect(Collectors.groupingByConcurrent(
                        row -> extractKey(row, condition),
                        ConcurrentHashMap::new,
                        Collectors.toList()
                ));
    }

    /**
     * 从行中提取键值
     */
    private Object extractKey(JQuickRow row, JQuickSqlJoinCondition condition) {
        return row;
    }

    /**
     * 分区处理数据集
     */
    private <T> List<List<T>> partitionList(List<T> list, int partitionSize) {
        List<List<T>> partitions = new ArrayList<>();
        for (int i = 0; i < list.size(); i += partitionSize) {
            partitions.add(list.subList(i, Math.min(i + partitionSize, list.size())));
        }
        return partitions;
    }

    /**
     * 并行处理数据（核心方法）
     *
     * @param rows      要处理的行数据
     * @param processor 处理函数，输入分区行列表，输出结果列表
     * @return 处理后的结果列表
     */
    private <T> List<T> processInParallel(List<JQuickRow> rows, Function<List<JQuickRow>, List<T>> processor) {
        if (!enableParallel || rows.size() < PARALLEL_THRESHOLD) {
            return processor.apply(rows);
        }
        List<List<JQuickRow>> partitions = partitionList(rows, PARALLEL_THRESHOLD);
        List<CompletableFuture<List<T>>> futures = partitions.stream()
                .map(partition -> CompletableFuture.supplyAsync(() -> processor.apply(partition), parallelPool))
                .collect(Collectors.toList());
        return futures.stream()
                .flatMap(future -> future.join().stream())
                .collect(Collectors.toList());
    }

    /**
     * 并行处理笛卡尔积
     */
    private List<JQuickRow> processCartesianInParallel(List<JQuickRow> leftRows, List<JQuickRow> rightRows, JQuickSqlJoinCondition condition, BiFunctionWithException<JQuickRow, JQuickRow, JQuickRow> processor) {
        if (!enableParallel || leftRows.size() * rightRows.size() < PARALLEL_THRESHOLD) {
            List<JQuickRow> result = new ArrayList<>();
            for (JQuickRow leftRow : leftRows) {
                for (JQuickRow rightRow : rightRows) {
                    JQuickRow processed = processor.apply(leftRow, rightRow);
                    if (processed != null) {
                        result.add(processed);
                    }
                }
            }
            return result;
        }
        List<List<JQuickRow>> leftPartitions = partitionList(leftRows, Math.max(1, PARALLEL_THRESHOLD / 10));
        List<CompletableFuture<List<JQuickRow>>> futures = new ArrayList<>();
        for (List<JQuickRow> leftPartition : leftPartitions) {
            futures.add(CompletableFuture.supplyAsync(() -> {
                List<JQuickRow> partitionResult = new ArrayList<>();
                for (JQuickRow leftRow : leftPartition) {
                    for (JQuickRow rightRow : rightRows) {
                        JQuickRow processed = processor.apply(leftRow, rightRow);
                        if (processed != null) {
                            partitionResult.add(processed);
                        }
                    }
                }
                return partitionResult;
            }, parallelPool));
        }

        return futures.stream().flatMap(future -> future.join().stream()).collect(Collectors.toList());
    }


    /**
     * 计算函数值
     */
    private Object evaluateFunction(JQuickRow row, JQuickSqlFunctionCallExpression function) {
        if (function.getArguments().isEmpty()) {
            return null;
        }
        JQuickSqlExpression arg = function.getArguments().get(0);
        if (arg instanceof JQuickSqlColumnExpression) {
            String columnName = ((JQuickSqlColumnExpression) arg).getColumnName();
            return row.get(columnName);
        }
        return null;
    }

    /**
     * 关闭线程池
     */
    public void shutdown() {
        if (parallelPool != null && !parallelPool.isShutdown()) {
            parallelPool.shutdown();
            try {
                if (!parallelPool.awaitTermination(30, TimeUnit.SECONDS)) {
                    parallelPool.shutdownNow();
                }
            } catch (InterruptedException e) {
                parallelPool.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * 函数式接口，支持带两个参数的函数
     */
    @FunctionalInterface
    private interface BiFunctionWithException<T, U, R> {
        R apply(T t, U u);
    }
}