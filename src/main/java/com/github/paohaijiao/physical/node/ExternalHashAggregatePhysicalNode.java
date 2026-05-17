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


import com.github.paohaijiao.context.JQuickExecutionContext;
import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.logic.domain.JQuickGroupByNode;
import com.github.paohaijiao.physical.JQuickPhysicalPlanNode;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;

import java.io.*;
import java.util.*;

/**
 * 外部哈希聚合物理节点 - 适用于大数据量分组聚合
 * 使用哈希分片 + 外部聚合，避免内存溢出
 */
public class ExternalHashAggregatePhysicalNode implements JQuickPhysicalPlanNode {

    private static final int NUM_PARTITIONS = 64;           // 分区数量
    private static final int MAX_MEMORY_PARTITIONS = 16;    // 内存中最大分区数

    private final List<JQuickExpression> groupKeys;
    private final List<JQuickGroupByNode.AggregateItem> aggregateItems;
    private final JQuickExpression havingCondition;
    private final JQuickPhysicalPlanNode child;
    private final File tempDir;

    public ExternalHashAggregatePhysicalNode(List<JQuickExpression> groupKeys, List<JQuickGroupByNode.AggregateItem> aggregateItems, JQuickExpression havingCondition, JQuickPhysicalPlanNode child) {
        this.groupKeys = groupKeys;
        this.aggregateItems = aggregateItems;
        this.havingCondition = havingCondition;
        this.child = child;
        this.tempDir = new File(System.getProperty("java.io.tmpdir"), "jquick_aggregate_" + System.nanoTime());
    }

    @Override
    public JQuickDataSet execute(JQuickExecutionContext context) {
        try {
            tempDir.mkdirs();

            // 第一阶段：哈希分区 - 将数据按分组键哈希分配到不同文件
            List<File> partitionFiles = hashPartition(context);

            // 第二阶段：分别对每个分区进行聚合
            Map<GroupKey, Map<String, Object>> finalAggState = new LinkedHashMap<>();

            for (File partitionFile : partitionFiles) {
                List<JQuickRow> partitionRows = readRowsFromFile(partitionFile);
                Map<GroupKey, Map<String, Object>> partitionAggState = aggregatePartition(partitionRows);

                // 合并聚合结果
                mergeAggState(finalAggState, partitionAggState);
            }

            // 完成最终聚合计算
            for (Map<String, Object> aggState : finalAggState.values()) {
                for (JQuickGroupByNode.AggregateItem item : aggregateItems) {
                    finalizeAggregate(aggState, item);
                }
            }

            // 构建结果
            JQuickDataSet result = buildResult(finalAggState);

            // 应用HAVING过滤
            if (havingCondition != null) {
                result = result.filter(row -> {
                    Object val = havingCondition.evaluate(row);
                    return val instanceof Boolean && (Boolean) val;
                });
            }

            // 清理临时文件
            cleanup();

            return result;

        } catch (Exception e) {
            cleanup();
            throw new RuntimeException("External hash aggregate failed", e);
        }
    }

    /**
     * 第一阶段：哈希分区
     */
    private List<File> hashPartition(JQuickExecutionContext context) throws IOException {
        List<File> partitionFiles = new ArrayList<>();
        List<BufferedWriter> writers = new ArrayList<>();

        // 创建分区文件
        for (int i = 0; i < NUM_PARTITIONS; i++) {
            File file = new File(tempDir, "partition_" + i + ".tmp");
            partitionFiles.add(file);
            writers.add(new BufferedWriter(new FileWriter(file)));
        }

        JQuickDataSet data = child.execute(context);

        for (JQuickRow row : data.getRows()) {
            // 计算分区号
            int partition = getPartition(row);

            // 写入对应分区文件
            writers.get(partition).write(serializeRow(row));
            writers.get(partition).newLine();
        }

        // 关闭所有writer
        for (BufferedWriter writer : writers) {
            writer.close();
        }

        return partitionFiles;
    }

    /**
     * 计算行的分区号
     */
    private int getPartition(JQuickRow row) {
        int hashCode = 0;
        for (JQuickExpression key : groupKeys) {
            Object value = key.evaluate(row);
            hashCode = 31 * hashCode + (value != null ? value.hashCode() : 0);
        }
        return Math.abs(hashCode) % NUM_PARTITIONS;
    }

    /**
     * 序列化行
     */
    private String serializeRow(JQuickRow row) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(row.toMap());
        oos.close();
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }

    /**
     * 反序列化行
     */
    @SuppressWarnings("unchecked")
    private JQuickRow deserializeRow(String line) throws IOException, ClassNotFoundException {
        byte[] data = Base64.getDecoder().decode(line);
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data));
        Map<String, Object> map = (Map<String, Object>) ois.readObject();
        return new JQuickRow(map);
    }

    /**
     * 读取分区的所有行
     */
    private List<JQuickRow> readRowsFromFile(File file) throws IOException {
        List<JQuickRow> rows = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    rows.add(deserializeRow(line));
                } catch (ClassNotFoundException e) {
                    throw new IOException("Failed to deserialize row", e);
                }
            }
        }
        return rows;
    }

    /**
     * 对单个分区进行聚合
     */
    private Map<GroupKey, Map<String, Object>> aggregatePartition(List<JQuickRow> rows) {
        Map<GroupKey, Map<String, Object>> groups = new LinkedHashMap<>();

        for (JQuickRow row : rows) {
            GroupKey key = new GroupKey(groupKeys, row);
            Map<String, Object> aggState = groups.computeIfAbsent(key, k -> new HashMap<>());

            // 初始化分组键值
            for (int i = 0; i < groupKeys.size(); i++) {
                String colName = "group_" + i;
                if (!aggState.containsKey(colName)) {
                    aggState.put(colName, groupKeys.get(i).evaluate(row));
                }
            }

            // 执行聚合
            for (JQuickGroupByNode.AggregateItem item : aggregateItems) {
                updateAggregate(aggState, item, row);
            }
        }

        return groups;
    }

    /**
     * 合并两个聚合状态
     */
    @SuppressWarnings("unchecked")
    private void mergeAggState(Map<GroupKey, Map<String, Object>> target,
                               Map<GroupKey, Map<String, Object>> source) {
        for (Map.Entry<GroupKey, Map<String, Object>> entry : source.entrySet()) {
            GroupKey key = entry.getKey();
            Map<String, Object> sourceState = entry.getValue();
            Map<String, Object> targetState = target.computeIfAbsent(key, k -> new HashMap<>());

            // 合并分组键值
            for (int i = 0; i < groupKeys.size(); i++) {
                String colName = "group_" + i;
                if (!targetState.containsKey(colName)) {
                    targetState.put(colName, sourceState.get(colName));
                }
            }

            // 合并聚合值
            for (JQuickGroupByNode.AggregateItem item : aggregateItems) {
                String alias = item.getAlias();
                String funcName = item.getFunctionName().toLowerCase();
                Object sourceVal = sourceState.get(alias);
                Object targetVal = targetState.get(alias);

                switch (funcName) {
                    case "count":
                        Long newCount = ((Long) targetVal != null ? (Long) targetVal : 0L) +
                                ((Long) sourceVal != null ? (Long) sourceVal : 0L);
                        targetState.put(alias, newCount);
                        break;

                    case "sum":
                        Double newSum = ((Double) targetVal != null ? (Double) targetVal : 0.0) +
                                ((Double) sourceVal != null ? (Double) sourceVal : 0.0);
                        targetState.put(alias, newSum);
                        break;

                    case "avg":
                        // 需要合并 sum 和 count
                        Map<String, Object> targetAvgState = (Map<String, Object>) targetState.get(alias + "_state");
                        Map<String, Object> sourceAvgState = (Map<String, Object>) sourceState.get(alias + "_state");

                        if (targetAvgState == null) {
                            targetAvgState = new HashMap<>();
                            targetAvgState.put("sum", 0.0);
                            targetAvgState.put("count", 0L);
                            targetState.put(alias + "_state", targetAvgState);
                        }
                        if (sourceAvgState != null) {
                            targetAvgState.put("sum", ((Double) targetAvgState.get("sum")) + ((Double) sourceAvgState.get("sum")));
                            targetAvgState.put("count", ((Long) targetAvgState.get("count")) + ((Long) sourceAvgState.get("count")));
                        }
                        break;

                    case "max":
                        if (sourceVal != null) {
                            if (targetVal == null ||
                                    (sourceVal instanceof Comparable &&
                                            ((Comparable) sourceVal).compareTo(targetVal) > 0)) {
                                targetState.put(alias, sourceVal);
                            }
                        }
                        break;

                    case "min":
                        if (sourceVal != null) {
                            if (targetVal == null ||
                                    (sourceVal instanceof Comparable &&
                                            ((Comparable) sourceVal).compareTo(targetVal) < 0)) {
                                targetState.put(alias, sourceVal);
                            }
                        }
                        break;
                }
            }
        }
    }

    private void updateAggregate(Map<String, Object> state, JQuickGroupByNode.AggregateItem item, JQuickRow row) {
        String key = item.getAlias();
        String funcName = item.getFunctionName().toLowerCase();
        Object value = item.getExpression().evaluate(row);

        switch (funcName) {
            case "count":
                Long count = (Long) state.get(key);
                if (count == null) count = 0L;
                if (value != null || item.isCountStar()) count++;
                state.put(key, count);
                break;

            case "sum":
                Double sum = (Double) state.get(key);
                if (sum == null) sum = 0.0;
                if (value instanceof Number) {
                    sum += ((Number) value).doubleValue();
                }
                state.put(key, sum);
                break;

            case "avg":
                @SuppressWarnings("unchecked")
                Map<String, Object> avgState = (Map<String, Object>) state.get(key + "_state");
                if (avgState == null) {
                    avgState = new HashMap<>();
                    avgState.put("sum", 0.0);
                    avgState.put("count", 0L);
                    state.put(key + "_state", avgState);
                }
                if (value instanceof Number) {
                    avgState.put("sum", ((Double) avgState.get("sum")) + ((Number) value).doubleValue());
                    avgState.put("count", ((Long) avgState.get("count")) + 1);
                }
                break;

            case "max":
                if (state.get(key) == null || (value instanceof Comparable &&
                        ((Comparable) value).compareTo(state.get(key)) > 0)) {
                    state.put(key, value);
                }
                break;

            case "min":
                if (state.get(key) == null || (value instanceof Comparable &&
                        ((Comparable) value).compareTo(state.get(key)) < 0)) {
                    state.put(key, value);
                }
                break;
        }
    }

    @SuppressWarnings("unchecked")
    private void finalizeAggregate(Map<String, Object> state, JQuickGroupByNode.AggregateItem item) {
        if (item.getFunctionName().equalsIgnoreCase("avg")) {
            Map<String, Object> avgState = (Map<String, Object>) state.get(item.getAlias() + "_state");
            if (avgState != null) {
                Double sum = (Double) avgState.get("sum");
                Long count = (Long) avgState.get("count");
                state.put(item.getAlias(), count > 0 ? sum / count : null);
                state.remove(item.getAlias() + "_state");
            }
        }
    }

    private JQuickDataSet buildResult(Map<GroupKey, Map<String, Object>> groups) {
        JQuickDataSet.Builder builder = JQuickDataSet.builder();

        for (int i = 0; i < groupKeys.size(); i++) {
            builder.addColumn("group_" + i, Object.class, "group_by");
        }
        for (JQuickGroupByNode.AggregateItem item : aggregateItems) {
            builder.addColumn(item.getAlias(), item.getOutputType(), "aggregate");
        }

        for (Map<String, Object> aggState : groups.values()) {
            JQuickRow row = new JQuickRow();
            for (Map.Entry<String, Object> entry : aggState.entrySet()) {
                if (!entry.getKey().endsWith("_state")) {
                    row.put(entry.getKey(), entry.getValue());
                }
            }
            builder.addRow(row);
        }

        return builder.build();
    }

    private void cleanup() {
        if (tempDir.exists()) {
            File[] files = tempDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }
            tempDir.delete();
        }
    }

    private static class GroupKey {
        private final List<Object> values;

        GroupKey(List<JQuickExpression> groupKeys, JQuickRow row) {
            this.values = new ArrayList<>();
            for (JQuickExpression key : groupKeys) {
                values.add(key.evaluate(row));
            }
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            GroupKey that = (GroupKey) o;
            return Objects.equals(values, that.values);
        }

        @Override
        public int hashCode() {
            return Objects.hash(values);
        }
    }

    @Override
    public String getNodeType() {
        return "ExternalHashAggregate";
    }

    @Override
    public long getEstimatedCost() {
        long childCost = child.getEstimatedCost();
        // 外部哈希聚合：磁盘IO + 哈希计算
        return childCost * 2;
    }
}
