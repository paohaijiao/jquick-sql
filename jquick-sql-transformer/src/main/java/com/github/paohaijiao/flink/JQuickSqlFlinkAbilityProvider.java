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
package com.github.paohaijiao.flink;

import com.github.paohaijiao.condition.JQuickSqlCondition;
import com.github.paohaijiao.console.JConsole;
import com.github.paohaijiao.expression.JQuickSqlExpression;
import com.github.paohaijiao.expression.JQuickSqlFunctionCallExpression;
import com.github.paohaijiao.expression.JQuickSqlOrderByExpression;
import com.github.paohaijiao.extra.JQuickFlinkRender;
import com.github.paohaijiao.join.JQuickSqlJoinCondition;
import com.github.paohaijiao.provider.JQuickSqlAbilityProvider;
import com.github.paohaijiao.spi.anno.Priority;
import com.github.paohaijiao.spi.constants.PriorityConstants;
import com.github.paohaijiao.statement.JQuickColumnMeta;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.BroadcastStream;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.streaming.api.functions.co.BroadcastProcessFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * packageName com.github.paohaijiao.flink
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/8/17
 */
@Priority(PriorityConstants.SYSTEM_MEDIUM)
public class JQuickSqlFlinkAbilityProvider extends JQuickFlinkRender implements JQuickSqlAbilityProvider {

    private final StreamExecutionEnvironment env;

    public JQuickSqlFlinkAbilityProvider(StreamExecutionEnvironment env) {
        this.env = env;
        JConsole console = JConsole.initConsoleEnvironment();
        console.info("JQuickSqlFlinkAbilityProvider initialized with StreamExecutionEnvironment");
    }

    @Override
    public JQuickDataSet innerJoin(JQuickDataSet left, JQuickDataSet right, JQuickSqlJoinCondition condition) {

        String leftKey = condition.getLeftColumn();
        String rightKey = condition.getRightColumn();
        DataStream<JQuickRow> leftStream = env.fromCollection(left.getRows());
        DataStream<JQuickRow> rightStream = env.fromCollection(right.getRows());
        DataStream<JQuickRow> joined =
                leftStream
                        .join(rightStream)
                        .where(row -> row.get(leftKey))
                        .equalTo(row -> row.get(rightKey))
                        .window(TumblingProcessingTimeWindows.of(Time.of(5000, TimeUnit.MICROSECONDS)))
                        .apply((l, r) -> {
                            JQuickRow merged = new JQuickRow();
                            merged.putAll(l);
                            merged.putAll(r);
                            return merged;
                        });

        List<JQuickRow> resultRows = new ArrayList<>();
        try {
            joined.executeAndCollect().forEachRemaining(resultRows::add);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        List<JQuickColumnMeta> mergedColumns = new ArrayList<>();
        mergedColumns.addAll(left.getColumns());
        mergedColumns.addAll(right.getColumns());
        return new JQuickDataSet(mergedColumns, resultRows);
    }

    @Override
    public JQuickDataSet leftJoin(JQuickDataSet left, JQuickDataSet right, JQuickSqlJoinCondition condition) {
        String leftColumn = condition.getLeftColumn();
        String rightColumn = condition.getRightColumn();
        List<JQuickRow> result = new ArrayList<>();
        // 右表索引
        Map<Object, List<JQuickRow>> rightIndex = right.groupBy(rightColumn);
        for (JQuickRow leftRow : left.getRows()) {
            Object key = leftRow.get(leftColumn);
            List<JQuickRow> matches = rightIndex.get(key);
            if (matches != null && !matches.isEmpty()) {// 命中：正常 merge
                for (JQuickRow rightRow : matches) {
                    JQuickRow merged = new JQuickRow();
                    merged.putAll(leftRow);
                    merged.putAll(rightRow);
                    result.add(merged);
                }
            } else {// 未命中：右表字段补 null
                JQuickRow merged = new JQuickRow();
                merged.putAll(leftRow);
                for (JQuickColumnMeta col : right.getColumns()) {
                    if (!merged.containsKey(col.getName())) {
                        merged.put(col.getName(), null);
                    }
                }
                result.add(merged);
            }
        }
        List<JQuickColumnMeta> mergedColumns = new ArrayList<>();// 合并列定义
        mergedColumns.addAll(left.getColumns());
        for (JQuickColumnMeta col : right.getColumns()) {
            if (mergedColumns.stream().noneMatch(c -> c.getName().equals(col.getName()))) {
                mergedColumns.add(col);
            }
        }
        return new JQuickDataSet(mergedColumns, result);
    }

    @Override
    public JQuickDataSet rightJoin(JQuickDataSet left, JQuickDataSet right, JQuickSqlJoinCondition condition) {
        String leftKey = condition.getLeftColumn();
        String rightKey = condition.getRightColumn();
        DataStream<JQuickRow> leftStream = env.fromCollection(left.getRows());
        DataStream<JQuickRow> rightStream = env.fromCollection(right.getRows());
        DataStream<JQuickRow> joined =
                leftStream
                        .coGroup(rightStream)
                        .where(row -> row.get(leftKey))
                        .equalTo(row -> row.get(rightKey))
                        .window(TumblingProcessingTimeWindows.of(Time.seconds(5)))
                        .apply((Iterable<JQuickRow> leftRows,
                                Iterable<JQuickRow> rightRows,
                                Collector<JQuickRow> out) -> {
                            List<JQuickRow> leftList = new ArrayList<>();
                            leftRows.forEach(leftList::add);
                            List<JQuickRow> rightList = new ArrayList<>();
                            rightRows.forEach(rightList::add);
                            if (!rightList.isEmpty()) {
                                for (JQuickRow r : rightList) {
                                    if (!leftList.isEmpty()) {
                                        for (JQuickRow l : leftList) {
                                            out.collect(mergeRow(l, r, left, right));
                                        }
                                    } else {
                                        out.collect(mergeRow(null, r, left, right));
                                    }
                                }

                            }
                        });
        List<JQuickRow> resultRows = new ArrayList<>();
        try {
            joined.executeAndCollect().forEachRemaining(resultRows::add);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        List<JQuickColumnMeta> mergedColumns = buildColumns(left, right);
        return new JQuickDataSet(mergedColumns, resultRows);
    }

    @Override
    public JQuickDataSet fullOuterJoin(JQuickDataSet left, JQuickDataSet right, JQuickSqlJoinCondition condition) {
        String leftKey = condition.getLeftColumn();
        String rightKey = condition.getRightColumn();
        DataStream<JQuickRow> leftStream = env.fromCollection(left.getRows());
        DataStream<JQuickRow> rightStream = env.fromCollection(right.getRows());
        DataStream<JQuickRow> joined =
                leftStream
                        .coGroup(rightStream)
                        .where(row -> row.get(leftKey))
                        .equalTo(row -> row.get(rightKey))
                        .window(TumblingProcessingTimeWindows.of(Time.seconds(5)))
                        .apply((Iterable<JQuickRow> leftRows,
                                Iterable<JQuickRow> rightRows,
                                Collector<JQuickRow> out) -> {
                            List<JQuickRow> leftList = new ArrayList<>();
                            leftRows.forEach(leftList::add);
                            List<JQuickRow> rightList = new ArrayList<>();
                            rightRows.forEach(rightList::add);
                            if (!leftList.isEmpty() && !rightList.isEmpty()) {//两边都有 → 笛卡尔
                                for (JQuickRow l : leftList) {
                                    for (JQuickRow r : rightList) {
                                        out.collect(mergeRow(l, r, left, right));
                                    }
                                }
                            }
                            else if (!leftList.isEmpty()) {//情况2：只有 left
                                for (JQuickRow l : leftList) {
                                    out.collect(mergeRow(l, null, left, right));
                                }
                            }
                            else if (!rightList.isEmpty()) {//情况3：只有 right
                                for (JQuickRow r : rightList) {
                                    out.collect(mergeRow(null, r, left, right));
                                }
                            }
                        });

        // 收集结果
        List<JQuickRow> resultRows = new ArrayList<>();
        try {
            joined.executeAndCollect().forEachRemaining(resultRows::add);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        List<JQuickColumnMeta> mergedColumns = buildColumns(left, right);
        return new JQuickDataSet(mergedColumns, resultRows);
    }

    @Override
    public JQuickDataSet crossJoin(JQuickDataSet left, JQuickDataSet right) {
        DataStream<JQuickRow> leftStream = env.fromCollection(left.getRows());
        DataStream<JQuickRow> rightStream = env.fromCollection(right.getRows());
        // right 广播
        MapStateDescriptor<String, List<JQuickRow>> broadcastState = new MapStateDescriptor<>(
                        "rightBroadcastState",
                        String.class,
                        (Class<List<JQuickRow>>) (Class<?>) List.class
        );
        BroadcastStream<JQuickRow> rightBroadcast = rightStream.broadcast(broadcastState);
        DataStream<JQuickRow> joined = leftStream
                        .connect(rightBroadcast)
                        .process(new BroadcastProcessFunction<JQuickRow, JQuickRow, JQuickRow>() {
                            private List<JQuickRow> rightCache = new ArrayList<>();
                            @Override
                            public void processBroadcastElement(JQuickRow value, Context ctx, Collector<JQuickRow> out) {
                                rightCache.add(value);
                            }

                            @Override
                            public void processElement(JQuickRow leftRow, ReadOnlyContext ctx, Collector<JQuickRow> out) {
                                for (JQuickRow rightRow : rightCache) {
                                    out.collect(mergeRow(leftRow, rightRow, left, right));
                                }
                            }
                        });
        List<JQuickRow> resultRows = new ArrayList<>();// 收集结果
        try {
            joined.executeAndCollect().forEachRemaining(resultRows::add);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        List<JQuickColumnMeta> mergedColumns = buildColumns(left, right);
        return new JQuickDataSet(mergedColumns, resultRows);
    }

    @Override
    public JQuickDataSet naturalJoin(JQuickDataSet left, JQuickDataSet right) {
        //找同名列
        Set<String> leftCols = left.getColumnNames().stream().collect(Collectors.toSet());
        Set<String> rightCols = right.getColumnNames().stream().collect(Collectors.toSet());
        List<String> joinCols = leftCols.stream().filter(rightCols::contains).collect(Collectors.toList());
        //没有公共列 → CROSS JOIN
        if (joinCols.isEmpty()) {
            return crossJoin(left, right);
        }
        //构造 DataStream
        DataStream<JQuickRow> leftStream = env.fromCollection(left.getRows());
        DataStream<JQuickRow> rightStream = env.fromCollection(right.getRows());
        //构造 composite key（多列 join）
        DataStream<JQuickRow> joined =
                leftStream
                        .coGroup(rightStream)
                        .where(row -> buildKey(row, joinCols))
                        .equalTo(row -> buildKey(row, joinCols))
                        .window(TumblingProcessingTimeWindows.of(Time.seconds(5)))
                        .apply((Iterable<JQuickRow> leftRows, Iterable<JQuickRow> rightRows, Collector<JQuickRow> out) -> {
                            List<JQuickRow> leftList = new ArrayList<>();
                            leftRows.forEach(leftList::add);
                            List<JQuickRow> rightList = new ArrayList<>();
                            rightRows.forEach(rightList::add);
                            // inner join 行为
                            if (!leftList.isEmpty() && !rightList.isEmpty()) {
                                for (JQuickRow l : leftList) {
                                    for (JQuickRow r : rightList) {
                                        out.collect(mergeNaturalRow(l, r, joinCols));
                                    }
                                }
                            }
                        });
        // 收集结果
        List<JQuickRow> resultRows = new ArrayList<>();
        try {
            joined.executeAndCollect().forEachRemaining(resultRows::add);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // 构建列（去重）
        List<JQuickColumnMeta> mergedColumns = buildNaturalColumns(left, right, joinCols);
        return new JQuickDataSet(mergedColumns, resultRows);
    }

    @Override
    public JQuickDataSet union(JQuickDataSet ds1, JQuickDataSet ds2) {
        validateSchema(ds1, ds2); //校验列结构一致
        DataStream<JQuickRow> stream1 = env.fromCollection(ds1.getRows());
        DataStream<JQuickRow> stream2 = env.fromCollection(ds2.getRows());
        DataStream<JQuickRow> unioned = stream1.union(stream2); //合并
        DataStream<JQuickRow> distinct = unioned//去重（基于整行）
                        .keyBy(row -> row.toMap().toString())
                        .process(new KeyedProcessFunction<String, JQuickRow, JQuickRow>() {
                            private transient ValueState<Boolean> seen;

                            @Override
                            public void open(Configuration parameters) {
                                ValueStateDescriptor<Boolean> desc = new ValueStateDescriptor<>("seen", Boolean.class);
                                seen = getRuntimeContext().getState(desc);
                            }

                            @Override
                            public void processElement(JQuickRow value, Context ctx, Collector<JQuickRow> out) throws Exception {
                                if (seen.value() == null) {
                                    seen.update(true);
                                    out.collect(value);
                                }
                            }
                        });
        List<JQuickRow> resultRows = new ArrayList<>(); //收集结果
        try {
            distinct.executeAndCollect().forEachRemaining(resultRows::add);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new JQuickDataSet(ds1.getColumns(), resultRows);
    }

    @Override
    public JQuickDataSet intersect(JQuickDataSet ds1, JQuickDataSet ds2) {
        validateSchema(ds1, ds2);
        List<String> columns = ds1.getColumnNames();
        DataStream<JQuickRow> stream1 = env.fromCollection(ds1.getRows());
        DataStream<JQuickRow> stream2 = env.fromCollection(ds2.getRows());
        DataStream<JQuickRow> intersected =
                stream1
                        .coGroup(stream2)
                        .where(row -> buildRowKey(row, columns))
                        .equalTo(row -> buildRowKey(row, columns))
                        .window(TumblingProcessingTimeWindows.of(Time.seconds(5)))
                        .apply((Iterable<JQuickRow> rows1, Iterable<JQuickRow> rows2, Collector<JQuickRow> out) -> {
                            boolean hasLeft = rows1.iterator().hasNext();
                            boolean hasRight = rows2.iterator().hasNext();
                            // 👉 两边都存在才输出
                            if (hasLeft && hasRight) {
                                // 只输出一条（去重语义）
                                out.collect(rows1.iterator().next());
                            }
                        });
        // 收集结果
        List<JQuickRow> resultRows = new ArrayList<>();
        try {
            intersected.executeAndCollect().forEachRemaining(resultRows::add);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new JQuickDataSet(ds1.getColumns(), resultRows);

    }

    @Override
    public JQuickDataSet minus(JQuickDataSet ds1, JQuickDataSet ds2) {
        validateSchema(ds1, ds2);
        List<String> columns = ds1.getColumnNames();
        DataStream<JQuickRow> stream1 = env.fromCollection(ds1.getRows());
        DataStream<JQuickRow> stream2 = env.fromCollection(ds2.getRows());
        DataStream<JQuickRow> result =
                stream1
                        .coGroup(stream2)
                        .where(row -> buildRowKey(row, columns))
                        .equalTo(row -> buildRowKey(row, columns))
                        .window(TumblingProcessingTimeWindows.of(Time.seconds(5)))
                        .apply((Iterable<JQuickRow> rows1, Iterable<JQuickRow> rows2, Collector<JQuickRow> out) -> {
                            boolean hasLeft = rows1.iterator().hasNext();
                            boolean hasRight = rows2.iterator().hasNext();
                            if (hasLeft && !hasRight) {//只在 left 存在、right 不存在时输出
                                // DISTINCT：只输出一条
                                out.collect(rows1.iterator().next());
                            }
                        });
        // 收集结果
        List<JQuickRow> resultRows = new ArrayList<>();
        try {
            result.executeAndCollect().forEachRemaining(resultRows::add);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new JQuickDataSet(ds1.getColumns(), resultRows);
    }

    @Override
    public JQuickDataSet selectColumns(JQuickDataSet dataset, List<String> columnNames) {
        //校验列存在
        validateColumns(dataset, columnNames);
        DataStream<JQuickRow> stream = env.fromCollection(dataset.getRows());
        //投影（projection）
        DataStream<JQuickRow> projected =
                stream.map(row -> {
                    JQuickRow newRow = new JQuickRow();
                    for (String col : columnNames) {
                        newRow.put(col, row.get(col));
                    }
                    return newRow;
                });
        List<JQuickRow> resultRows = new ArrayList<>(); // 收集结果
        try {
            projected.executeAndCollect().forEachRemaining(resultRows::add);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        List<JQuickColumnMeta> newColumns = dataset.getColumns().stream() //构建新 schema（按顺序）
                        .filter(col -> columnNames.contains(col.getName()))
                        .sorted(Comparator.comparingInt(col -> columnNames.indexOf(col.getName())))
                        .collect(Collectors.toList());
        return new JQuickDataSet(newColumns, resultRows);
    }

    @Override
    public JQuickDataSet filter(JQuickDataSet dataset, JQuickSqlCondition condition) {
//        DataStream<JQuickRow> stream = env.fromCollection(dataset.getRows());
//        DataStream<JQuickRow> filtered = stream.filter(condition::test);
//        //收集结果
//        List<JQuickRow> resultRows = new ArrayList<>();
//        try {
//            filtered.executeAndCollect().forEachRemaining(resultRows::add);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//        // schema 不变
//        return new JQuickDataSet(dataset.getColumns(), resultRows);
        return null;
    }

    @Override
    public JQuickDataSet transform(JQuickDataSet dataset, Map<String, JQuickSqlFunctionCallExpression> transformations) {
        return null;
    }

    @Override
    public JQuickDataSet sort(JQuickDataSet dataset, List<JQuickSqlOrderByExpression> orderByExpressions) {
        //拷贝数据（避免修改原数据）
        List<JQuickRow> rows = new ArrayList<>(dataset.getRows());
        //构建 Comparator
        Comparator<JQuickRow> comparator = buildComparator(orderByExpressions);
        rows.sort(comparator);//排序
        return new JQuickDataSet(dataset.getColumns(), rows);
    }

    @Override
    public JQuickDataSet aggregate(JQuickDataSet dataset, List<String> groupBy, Map<String, JQuickSqlFunctionCallExpression> aggregations) {
        List<JQuickRow> rows = dataset.getRows();
        //分组
        Map<String, List<JQuickRow>> groups = new LinkedHashMap<>();
        for (JQuickRow row : rows) {
            String key = buildGroupKey(row, groupBy);
            groups.computeIfAbsent(key, k -> new ArrayList<>()).add(row);
        }
        //聚合
        List<JQuickRow> resultRows = new ArrayList<>();
        for (Map.Entry<String, List<JQuickRow>> entry : groups.entrySet()) {
            List<JQuickRow> groupRows = entry.getValue();
            JQuickRow resultRow = new JQuickRow();
            for (String col : groupBy) {//groupBy 列（取第一行）
                resultRow.put(col, groupRows.get(0).get(col));
            }
            for (Map.Entry<String, JQuickSqlFunctionCallExpression> aggEntry : aggregations.entrySet()) {//聚合列
                String alias = aggEntry.getKey();
                JQuickSqlFunctionCallExpression expr = aggEntry.getValue();
//                JQuickAggregator aggregator = expr.newAggregator();
//
//                for (JQuickRow r : groupRows) {
//                    aggregator.add(r);
//                }
                resultRow.put(alias, null);
            }
            resultRows.add(resultRow);
        }
        // 构建 schema
        List<JQuickColumnMeta> newColumns = new ArrayList<>();
        // groupBy 列
        for (String col : groupBy) {
            newColumns.add(new JQuickColumnMeta(col, Object.class, "group"));
        }
        // 聚合列
        for (String alias : aggregations.keySet()) {
            newColumns.add(new JQuickColumnMeta(alias, Object.class, "agg"));
        }
        return new JQuickDataSet(newColumns, resultRows);
    }

    @Override
    public JQuickDataSet alias(JQuickDataSet dataset, Map<String, JQuickSqlExpression> aliases) {
        DataStream<JQuickRow> stream = env.fromCollection(dataset.getRows());
        DataStream<JQuickRow> mapped = stream.map(row -> {
                    JQuickRow newRow = new JQuickRow();
                    newRow.putAll(row);//copy数据
                    return newRow;
                });
        List<JQuickRow> resultRows = new ArrayList<>();
        try {
            mapped.executeAndCollect().forEachRemaining(resultRows::add);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        List<JQuickColumnMeta> newColumns = new ArrayList<>(dataset.getColumns());
        for (Map.Entry<String, JQuickSqlExpression> entry : aliases.entrySet()) {
            newColumns.add(new JQuickColumnMeta(
                    entry.getKey(),
                    Object.class,   // 可优化类型推断
                    "alias"
            ));
        }
        return new JQuickDataSet(newColumns, resultRows);
    }

    @Override
    public JQuickDataSet limit(JQuickDataSet dataset, Integer limit, Integer offset) {
        if (limit == null || limit < 0) {
            throw new IllegalArgumentException("limit must be >= 0");
        }
        if (offset == null || offset < 0) {
            offset = 0;
        }
        List<JQuickRow> rows = dataset.getRows();
        int start = Math.min(offset, rows.size());
        int end = Math.min(start + limit, rows.size());
        List<JQuickRow> subList = rows.subList(start, end);
        List<JQuickRow> result = new ArrayList<>(subList);
        return new JQuickDataSet(dataset.getColumns(), result);
    }
}
