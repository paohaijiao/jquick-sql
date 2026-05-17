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
package com.github.paohaijiao.stats;

/**
 * packageName com.github.paohaijiao.stats
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/5/17
 */

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

/**
 * 执行统计 - 记录SQL执行过程中的各种指标
 */
public class JQuickExecutionStats {


    /** 扫描的总行数 */
    private final LongAdder scannedRows = new LongAdder();

    /** 返回的结果行数 */
    private final LongAdder returnedRows = new LongAdder();

    /** 过滤掉的行数 */
    private final LongAdder filteredRows = new LongAdder();

    /** 连接处理的行数 */
    private final LongAdder joinedRows = new LongAdder();

    /** 聚合处理的行数 */
    private final LongAdder aggregatedRows = new LongAdder();

    /** 排序处理的行数 */
    private final LongAdder sortedRows = new LongAdder();


    /** 各阶段执行时间（毫秒） */
    private final Map<String, Long> stepDurations = new ConcurrentHashMap<>();

    /** 解析开始时间 */
    private long parseStartTime = 0;

    /** 解析结束时间 */
    private long parseEndTime = 0;

    /** 优化开始时间 */
    private long optimizeStartTime = 0;

    /** 优化结束时间 */
    private long optimizeEndTime = 0;

    /** 执行开始时间 */
    private long executeStartTime = 0;

    /** 执行结束时间 */
    private long executeEndTime = 0;

    /** 总开始时间 */
    private final long totalStartTime;

    /** 总结束时间 */
    private long totalEndTime = 0;


    /** 使用的内存（字节） */
    private long memoryUsed = 0;

    /** 峰值内存（字节） */
    private long peakMemoryUsed = 0;

    /** 磁盘写入字节数 */
    private long diskBytesWritten = 0;

    /** 磁盘读取字节数 */
    private long diskBytesRead = 0;

    /** 网络传输字节数 */
    private long networkBytesTransferred = 0;


    /** CTE缓存命中次数 */
    private final AtomicLong cteCacheHits = new AtomicLong(0);

    /** CTE缓存未命中次数 */
    private final AtomicLong cteCacheMisses = new AtomicLong(0);

    /** 子查询缓存命中次数 */
    private final AtomicLong subqueryCacheHits = new AtomicLong(0);

    /** 子查询缓存未命中次数 */
    private final AtomicLong subqueryCacheMisses = new AtomicLong(0);


    /** 参与的工作节点数 */
    private int workerCount = 0;

    /** 数据分区数 */
    private int partitionCount = 0;

    /** 洗牌数据量（字节） */
    private long shuffleBytes = 0;

    /** 洗牌记录数 */
    private long shuffleRecords = 0;


    public JQuickExecutionStats() {
        this.totalStartTime = System.currentTimeMillis();
    }


    /**
     * 开始解析阶段
     */
    public void startParse() {
        this.parseStartTime = System.currentTimeMillis();
    }

    /**
     * 结束解析阶段
     */
    public void endParse() {
        this.parseEndTime = System.currentTimeMillis();
        recordStep("parse", getParseTime());
    }

    /**
     * 开始优化阶段
     */
    public void startOptimize() {
        this.optimizeStartTime = System.currentTimeMillis();
    }

    /**
     * 结束优化阶段
     */
    public void endOptimize() {
        this.optimizeEndTime = System.currentTimeMillis();
        recordStep("optimize", getOptimizeTime());
    }

    /**
     * 开始执行阶段
     */
    public void startExecute() {
        this.executeStartTime = System.currentTimeMillis();
    }

    /**
     * 结束执行阶段
     */
    public void endExecute() {
        this.executeEndTime = System.currentTimeMillis();
        this.totalEndTime = System.currentTimeMillis();
        recordStep("execute", getExecuteTime());
    }

    /**
     * 记录步骤执行时间
     */
    public void recordStep(String stepName, long duration) {
        stepDurations.merge(stepName, duration, Long::sum);
    }

    /**
     * 记录步骤执行时间（开始/结束模式）
     */
    public void recordStep(String stepName, long startTime, long endTime) {
        recordStep(stepName, endTime - startTime);
    }

    public void addScannedRows(long count) {
        scannedRows.add(count);
    }

    public void addReturnedRows(long count) {
        returnedRows.add(count);
    }

    public void addFilteredRows(long count) {
        filteredRows.add(count);
    }

    public void addJoinedRows(long count) {
        joinedRows.add(count);
    }

    public void addAggregatedRows(long count) {
        aggregatedRows.add(count);
    }

    public void addSortedRows(long count) {
        sortedRows.add(count);
    }


    public void updateMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        long used = runtime.totalMemory() - runtime.freeMemory();
        this.memoryUsed = used;
        if (used > this.peakMemoryUsed) {
            this.peakMemoryUsed = used;
        }
    }

    public void addDiskBytesWritten(long bytes) {
        this.diskBytesWritten += bytes;
    }

    public void addDiskBytesRead(long bytes) {
        this.diskBytesRead += bytes;
    }

    public void addNetworkBytesTransferred(long bytes) {
        this.networkBytesTransferred += bytes;
    }

    public void recordCteCacheHit() {
        cteCacheHits.incrementAndGet();
    }

    public void recordCteCacheMiss() {
        cteCacheMisses.incrementAndGet();
    }

    public void recordSubqueryCacheHit() {
        subqueryCacheHits.incrementAndGet();
    }

    public void recordSubqueryCacheMiss() {
        subqueryCacheMisses.incrementAndGet();
    }

    public double getCteCacheHitRate() {
        long hits = cteCacheHits.get();
        long total = hits + cteCacheMisses.get();
        return total == 0 ? 0 : (double) hits / total;
    }

    public double getSubqueryCacheHitRate() {
        long hits = subqueryCacheHits.get();
        long total = hits + subqueryCacheMisses.get();
        return total == 0 ? 0 : (double) hits / total;
    }


    public void setWorkerCount(int count) {
        this.workerCount = count;
    }

    public void setPartitionCount(int count) {
        this.partitionCount = count;
    }

    public void addShuffleBytes(long bytes) {
        this.shuffleBytes += bytes;
    }

    public void addShuffleRecords(long records) {
        this.shuffleRecords += records;
    }


    public long getScannedRows() { return scannedRows.sum(); }
    public long getReturnedRows() { return returnedRows.sum(); }
    public long getFilteredRows() { return filteredRows.sum(); }
    public long getJoinedRows() { return joinedRows.sum(); }
    public long getAggregatedRows() { return aggregatedRows.sum(); }
    public long getSortedRows() { return sortedRows.sum(); }

    public long getParseTime() { return parseEndTime - parseStartTime; }
    public long getOptimizeTime() { return optimizeEndTime - optimizeStartTime; }
    public long getExecuteTime() { return executeEndTime - executeStartTime; }
    public long getTotalTime() { return totalEndTime - totalStartTime; }

    public long getMemoryUsed() { return memoryUsed; }
    public long getPeakMemoryUsed() { return peakMemoryUsed; }
    public long getDiskBytesWritten() { return diskBytesWritten; }
    public long getDiskBytesRead() { return diskBytesRead; }
    public long getNetworkBytesTransferred() { return networkBytesTransferred; }

    public long getCteCacheHits() { return cteCacheHits.get(); }
    public long getCteCacheMisses() { return cteCacheMisses.get(); }
    public long getSubqueryCacheHits() { return subqueryCacheHits.get(); }
    public long getSubqueryCacheMisses() { return subqueryCacheMisses.get(); }

    public int getWorkerCount() { return workerCount; }
    public int getPartitionCount() { return partitionCount; }
    public long getShuffleBytes() { return shuffleBytes; }
    public long getShuffleRecords() { return shuffleRecords; }

    public Map<String, Long> getStepDurations() {
        return new HashMap<>(stepDurations);
    }


    /**
     * 格式化字节大小
     */
    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.2f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.2f MB", bytes / (1024.0 * 1024));
        return String.format("%.2f GB", bytes / (1024.0 * 1024 * 1024));
    }

    /**
     * 格式化时间
     */
    private String formatTime(long millis) {
        if (millis < 1000) return millis + " ms";
        if (millis < 60000) return String.format("%.2f s", millis / 1000.0);
        return String.format("%.2f min", millis / 60000.0);
    }

    /**
     * 打印统计信息
     */
    public void print() {
        System.out.println();
        System.out.println("╔══════════════════════════════════════════════════════════════════╗");
        System.out.println("║                    Execution Statistics                         ║");
        System.out.println("╠══════════════════════════════════════════════════════════════════╣");

        // 时间统计
        System.out.println("║ Time Statistics:                                                ║");
        System.out.printf("║   Parse:     %s%n", formatTime(getParseTime()));
        System.out.printf("║   Optimize:  %s%n", formatTime(getOptimizeTime()));
        System.out.printf("║   Execute:   %s%n", formatTime(getExecuteTime()));
        System.out.printf("║   Total:     %s%n", formatTime(getTotalTime()));
        System.out.println("║                                                                ║");

        // 行数统计
        System.out.println("║ Row Statistics:                                                ║");
        System.out.printf("║   Scanned:    %,d rows%n", getScannedRows());
        System.out.printf("║   Filtered:   %,d rows%n", getFilteredRows());
        System.out.printf("║   Joined:     %,d rows%n", getJoinedRows());
        System.out.printf("║   Aggregated: %,d rows%n", getAggregatedRows());
        System.out.printf("║   Sorted:     %,d rows%n", getSortedRows());
        System.out.printf("║   Returned:   %,d rows%n", getReturnedRows());

        if (getScannedRows() > 0) {
            double selectivity = (double) getReturnedRows() / getScannedRows() * 100;
            System.out.printf("║   Selectivity: %.2f%%%n", selectivity);
        }
        System.out.println("║                                                                ║");

        // 资源统计
        System.out.println("║ Resource Statistics:                                           ║");
        System.out.printf("║   Memory Used: %s%n", formatBytes(getMemoryUsed()));
        System.out.printf("║   Peak Memory: %s%n", formatBytes(getPeakMemoryUsed()));
        System.out.printf("║   Disk Read:   %s%n", formatBytes(getDiskBytesRead()));
        System.out.printf("║   Disk Write:  %s%n", formatBytes(getDiskBytesWritten()));
        System.out.printf("║   Network:     %s%n", formatBytes(getNetworkBytesTransferred()));
        System.out.println("║                                                                ║");

        // 缓存统计
        System.out.println("║ Cache Statistics:                                              ║");
        System.out.printf("║   CTE Cache:     %,d hits / %,d misses (%.1f%% hit rate)%n",
                getCteCacheHits(), getCteCacheMisses(), getCteCacheHitRate() * 100);
        System.out.printf("║   Subquery Cache: %,d hits / %,d misses (%.1f%% hit rate)%n",
                getSubqueryCacheHits(), getSubqueryCacheMisses(), getSubqueryCacheHitRate() * 100);
        System.out.println("║                                                                ║");

        // 分布式统计（如果适用）
        if (workerCount > 0 || partitionCount > 0) {
            System.out.println("║ Distributed Statistics:                                        ║");
            System.out.printf("║   Workers:    %d%n", workerCount);
            System.out.printf("║   Partitions: %d%n", partitionCount);
            System.out.printf("║   Shuffle:    %s (%d records)%n",
                    formatBytes(shuffleBytes), shuffleRecords);
            System.out.println("║                                                                ║");
        }

        // 步骤详情
        if (!stepDurations.isEmpty()) {
            System.out.println("║ Step Details:                                                 ║");
            for (Map.Entry<String, Long> entry : stepDurations.entrySet()) {
                System.out.printf("║   %-20s: %s%n", entry.getKey(), formatTime(entry.getValue()));
            }
            System.out.println("║                                                                ║");
        }

        System.out.println("╚══════════════════════════════════════════════════════════════════╝");
        System.out.println();
    }

    /**
     * 获取JSON格式的统计信息
     */
    public String toJson() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"parseTime\":").append(getParseTime()).append(",");
        sb.append("\"optimizeTime\":").append(getOptimizeTime()).append(",");
        sb.append("\"executeTime\":").append(getExecuteTime()).append(",");
        sb.append("\"totalTime\":").append(getTotalTime()).append(",");
        sb.append("\"scannedRows\":").append(getScannedRows()).append(",");
        sb.append("\"returnedRows\":").append(getReturnedRows()).append(",");
        sb.append("\"filteredRows\":").append(getFilteredRows()).append(",");
        sb.append("\"joinedRows\":").append(getJoinedRows()).append(",");
        sb.append("\"aggregatedRows\":").append(getAggregatedRows()).append(",");
        sb.append("\"sortedRows\":").append(getSortedRows()).append(",");
        sb.append("\"memoryUsed\":").append(getMemoryUsed()).append(",");
        sb.append("\"peakMemoryUsed\":").append(getPeakMemoryUsed()).append(",");
        sb.append("\"cteCacheHits\":").append(getCteCacheHits()).append(",");
        sb.append("\"cteCacheMisses\":").append(getCteCacheMisses()).append(",");
        sb.append("\"subqueryCacheHits\":").append(getSubqueryCacheHits()).append(",");
        sb.append("\"subqueryCacheMisses\":").append(getSubqueryCacheMisses());
        sb.append("}");
        return sb.toString();
    }

    /**
     * 重置所有统计
     */
    public void reset() {
        scannedRows.reset();
        returnedRows.reset();
        filteredRows.reset();
        joinedRows.reset();
        aggregatedRows.reset();
        sortedRows.reset();

        stepDurations.clear();

        parseStartTime = 0;
        parseEndTime = 0;
        optimizeStartTime = 0;
        optimizeEndTime = 0;
        executeStartTime = 0;
        executeEndTime = 0;

        memoryUsed = 0;
        peakMemoryUsed = 0;
        diskBytesWritten = 0;
        diskBytesRead = 0;
        networkBytesTransferred = 0;

        cteCacheHits.set(0);
        cteCacheMisses.set(0);
        subqueryCacheHits.set(0);
        subqueryCacheMisses.set(0);

        workerCount = 0;
        partitionCount = 0;
        shuffleBytes = 0;
        shuffleRecords = 0;
    }

    /**
     * 合并另一个统计对象（用于分布式执行）
     */
    public void merge(JQuickExecutionStats other) {
        this.scannedRows.add(other.getScannedRows());
        this.returnedRows.add(other.getReturnedRows());
        this.filteredRows.add(other.getFilteredRows());
        this.joinedRows.add(other.getJoinedRows());
        this.aggregatedRows.add(other.getAggregatedRows());
        this.sortedRows.add(other.getSortedRows());

        for (Map.Entry<String, Long> entry : other.stepDurations.entrySet()) {
            this.stepDurations.merge(entry.getKey(), entry.getValue(), Long::sum);
        }

        this.diskBytesWritten += other.diskBytesWritten;
        this.diskBytesRead += other.diskBytesRead;
        this.networkBytesTransferred += other.networkBytesTransferred;

        this.cteCacheHits.addAndGet(other.cteCacheHits.get());
        this.cteCacheMisses.addAndGet(other.cteCacheMisses.get());
        this.subqueryCacheHits.addAndGet(other.subqueryCacheHits.get());
        this.subqueryCacheMisses.addAndGet(other.subqueryCacheMisses.get());

        this.shuffleBytes += other.shuffleBytes;
        this.shuffleRecords += other.shuffleRecords;

        this.workerCount = Math.max(this.workerCount, other.workerCount);
        this.partitionCount = Math.max(this.partitionCount, other.partitionCount);
    }

}