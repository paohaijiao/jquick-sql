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

import com.github.paohaijiao.logic.domain.JQuickSortNode;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;

import java.io.*;
import java.util.*;

/**
 * 外部排序物理节点 - 适用于大数据量排序
 * 使用多路归并算法，将数据分块排序后合并
 */
public class ExternalSortPhysicalNode implements JQuickPhysicalPlanNode {

    private static final int MAX_IN_MEMORY_ROWS = 100000;  // 内存中最大行数
    private static final int MERGE_WAY = 16;               // 归并路数
    private final List<JQuickSortNode.OrderByItem> orderByItems;
    private final JQuickPhysicalPlanNode child;
    private final File tempDir;

    public ExternalSortPhysicalNode(List<JQuickSortNode.OrderByItem> orderByItems, JQuickPhysicalPlanNode child) {
        this.orderByItems = orderByItems;
        this.child = child;
        this.tempDir = new File(System.getProperty("java.io.tmpdir"), "jquick_sort_" + System.nanoTime());
    }

    @Override
    public JQuickDataSet execute(JQuickPhysicalPlanNode context) {
        try {
            tempDir.mkdirs();

            // 第一阶段：排序分块
            List<File> sortedChunks = createSortedChunks(context);

            // 第二阶段：多路归并
            List<JQuickRow> mergedRows = mergeChunks(sortedChunks);

            // 清理临时文件
            cleanup();

            // 构建结果数据集
            JQuickDataSet originalData = child.execute(context);
            return new JQuickDataSet(originalData.getColumns(), mergedRows);

        } catch (Exception e) {
            cleanup();
            throw new RuntimeException("External sort failed", e);
        }
    }

    /**
     * 第一阶段：将数据分成多个块，每个块在内存中排序后写入临时文件
     */
    private List<File> createSortedChunks(JQuickPhysicalPlanNode context) throws IOException {
        List<File> chunks = new ArrayList<>();
        List<JQuickRow> buffer = new ArrayList<>();

        JQuickDataSet data = child.execute(context);

        for (JQuickRow row : data.getRows()) {
            buffer.add(row);

            if (buffer.size() >= MAX_IN_MEMORY_ROWS) {
                // 排序当前块
                buffer.sort(getRowComparator());

                // 写入临时文件
                File chunkFile = new File(tempDir, "chunk_" + chunks.size() + ".tmp");
                writeRowsToFile(buffer, chunkFile);
                chunks.add(chunkFile);

                buffer.clear();
            }
        }

        // 处理剩余的块
        if (!buffer.isEmpty()) {
            buffer.sort(getRowComparator());
            File chunkFile = new File(tempDir, "chunk_" + chunks.size() + ".tmp");
            writeRowsToFile(buffer, chunkFile);
            chunks.add(chunkFile);
            buffer.clear();
        }

        return chunks;
    }

    /**
     * 第二阶段：多路归并
     */
    private List<JQuickRow> mergeChunks(List<File> chunks) throws IOException {
        if (chunks.isEmpty()) {
            return new ArrayList<>();
        }

        if (chunks.size() == 1) {
            return readRowsFromFile(chunks.get(0));
        }

        // 使用优先级队列进行多路归并
        PriorityQueue<MergeEntry> heap = new PriorityQueue<>(
                (a, b) -> compareRows(a.row, b.row)
        );

        // 初始化：从每个块读取第一行
        List<BufferedReader> readers = new ArrayList<>();
        List<Queue<JQuickRow>> chunkQueues = new ArrayList<>();

        for (int i = 0; i < chunks.size(); i++) {
            List<JQuickRow> rows = readRowsFromFile(chunks.get(i));
            Queue<JQuickRow> queue = new ArrayDeque<>(rows);
            chunkQueues.add(queue);

            if (!queue.isEmpty()) {
                heap.offer(new MergeEntry(queue.poll(), i));
            }
        }

        // 归并
        List<JQuickRow> result = new ArrayList<>();
        while (!heap.isEmpty()) {
            MergeEntry entry = heap.poll();
            result.add(entry.row);

            // 从同一块读取下一行
            Queue<JQuickRow> queue = chunkQueues.get(entry.chunkIndex);
            if (!queue.isEmpty()) {
                heap.offer(new MergeEntry(queue.poll(), entry.chunkIndex));
            }
        }

        // 关闭所有reader
        for (BufferedReader reader : readers) {
            try { reader.close(); } catch (IOException e) { /* ignore */ }
        }

        return result;
    }

    /**
     * 写入行到文件
     */
    private void writeRowsToFile(List<JQuickRow> rows, File file) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeInt(rows.size());
            for (JQuickRow row : rows) {
                oos.writeObject(row.toMap());
            }
        }
    }

    /**
     * 从文件读取行
     */
    @SuppressWarnings("unchecked")
    private List<JQuickRow> readRowsFromFile(File file) throws IOException {
        List<JQuickRow> rows = new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            int size = ois.readInt();
            for (int i = 0; i < size; i++) {
                Map<String, Object> data = (Map<String, Object>) ois.readObject();
                rows.add(new JQuickRow(data));
            }
        } catch (ClassNotFoundException e) {
            throw new IOException("Failed to deserialize rows", e);
        }
        return rows;
    }

    /**
     * 获取行比较器
     */
    private Comparator<JQuickRow> getRowComparator() {
        return (row1, row2) -> {
            for (JQuickSortNode.OrderByItem item : orderByItems) {
                Object v1 = row1.get(item.getColumnName());
                Object v2 = row2.get(item.getColumnName());

                if (v1 == null && v2 == null) continue;
                if (v1 == null) return 1;
                if (v2 == null) return -1;
                @SuppressWarnings({"rawtypes", "unchecked"})
                int cmp = ((Comparable) v1).compareTo(v2);
                if (cmp != 0) {
                    return item.isAscending() ? cmp : -cmp;
                }
            }
            return 0;
        };
    }

    /**
     * 比较两行
     */
    private int compareRows(JQuickRow row1, JQuickRow row2) {
        for (JQuickSortNode.OrderByItem item : orderByItems) {
            Object v1 = row1.get(item.getColumnName());
            Object v2 = row2.get(item.getColumnName());

            if (v1 == null && v2 == null) continue;
            if (v1 == null) return 1;
            if (v2 == null) return -1;

            @SuppressWarnings({"rawtypes", "unchecked"})
            int cmp = ((Comparable) v1).compareTo(v2);
            if (cmp != 0) {
                return item.isAscending() ? cmp : -cmp;
            }
        }
        return 0;
    }

    /**
     * 清理临时文件
     */
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

    private static class MergeEntry {
        final JQuickRow row;
        final int chunkIndex;

        MergeEntry(JQuickRow row, int chunkIndex) {
            this.row = row;
            this.chunkIndex = chunkIndex;
        }
    }

    @Override
    public String getNodeType() {
        return "ExternalSort";
    }

    @Override
    public long getEstimatedCost() {
        long childCost = child.getEstimatedCost();
        // 外部排序：磁盘IO + 排序成本
        return childCost * 3;  // 粗略估算，包含读写磁盘开销
    }
}
