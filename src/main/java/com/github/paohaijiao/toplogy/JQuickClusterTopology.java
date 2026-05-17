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
package com.github.paohaijiao.toplogy;

import com.github.paohaijiao.config.JQuickClusterConfig;
import com.github.paohaijiao.stats.JQuickClusterStats;
import com.github.paohaijiao.stats.JQuickDataPartition;
import com.github.paohaijiao.worker.JQuickWorkerAssignment;
import com.github.paohaijiao.worker.JQuickWorkerNode;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 集群拓扑信息 - 描述集群中所有Worker节点及其状态
 */
public class JQuickClusterTopology {

    private final Map<String, JQuickWorkerNode> workers;

    private final List<String> workerIds;

    private final Map<String, List<String>> rackToWorkers;

    private final Map<String, List<String>> locationToWorkers;

    private final JQuickClusterConfig config;

    public JQuickClusterTopology() {
        this(new ArrayList<>());
    }

    public JQuickClusterTopology(List<JQuickWorkerNode> workers) {
        this(workers, new JQuickClusterConfig());
    }

    public JQuickClusterTopology(List<JQuickWorkerNode> workers, JQuickClusterConfig config) {
        this.workers = new ConcurrentHashMap<>();
        this.workerIds = new ArrayList<>();
        this.rackToWorkers = new HashMap<>();
        this.locationToWorkers = new HashMap<>();
        this.config = config != null ? config : new JQuickClusterConfig();
        for (JQuickWorkerNode worker : workers) {
            addWorker(worker);
        }
    }

    /**
     * 添加Worker节点
     */
    public void addWorker(JQuickWorkerNode worker) {
        workers.put(worker.getWorkerId(), worker);
        workerIds.add(worker.getWorkerId());
        // 按机架分组
        String rack = worker.getRack();
        if (rack != null) {
            rackToWorkers.computeIfAbsent(rack, k -> new ArrayList<>()).add(worker.getWorkerId());
        }
        // 按位置分组（用于数据本地性）
        String location = worker.getLocation();
        if (location != null) {
            locationToWorkers.computeIfAbsent(location, k -> new ArrayList<>()).add(worker.getWorkerId());
        }
    }

    /**
     * 移除Worker节点
     */
    public void removeWorker(String workerId) {
        JQuickWorkerNode worker = workers.remove(workerId);
        if (worker != null) {
            workerIds.remove(workerId);
            String rack = worker.getRack();
            if (rack != null) {
                List<String> rackWorkers = rackToWorkers.get(rack);
                if (rackWorkers != null) {
                    rackWorkers.remove(workerId);
                }
            }
            String location = worker.getLocation();
            if (location != null) {
                List<String> locationWorkers = locationToWorkers.get(location);
                if (locationWorkers != null) {
                    locationWorkers.remove(workerId);
                }
            }
        }
    }

    /**
     * 获取Worker节点
     */
    public JQuickWorkerNode getWorker(String workerId) {
        return workers.get(workerId);
    }

    /**
     * 获取所有Worker节点
     */
    public Collection<JQuickWorkerNode> getAllWorkers() {
        return workers.values();
    }

    /**
     * 获取所有Worker ID
     */
    public List<String> getWorkerIds() {
        return new ArrayList<>(workerIds);
    }

    /**
     * 获取可用Worker数量
     */
    public int getAvailableWorkers() {
        return (int) workers.values().stream().filter(JQuickWorkerNode::isHealthy).count();
    }

    /**
     * 轮询方式获取Worker
     */
    public String getWorkerByIndex(int index) {
        if (workerIds.isEmpty()) {
            return null;
        }
        List<String> healthyWorkers = getHealthyWorkerIds();
        if (healthyWorkers.isEmpty()) {
            return workerIds.get(index % workerIds.size());
        }
        return healthyWorkers.get(index % healthyWorkers.size());
    }

    /**
     * 根据数据位置获取最优Worker（数据本地性优先）
     */
    public String getWorkerForLocation(String location) {
        if (location == null) {
            return getWorkerByIndex(0);
        }
        // 优先选择相同位置的Worker
        List<String> locationWorkers = locationToWorkers.get(location);
        if (locationWorkers != null && !locationWorkers.isEmpty()) {
            for (String workerId : locationWorkers) {
                JQuickWorkerNode worker = workers.get(workerId);
                if (worker != null && worker.isHealthy()) {
                    return workerId;
                }
            }
        }
        // 其次选择相同机架的Worker
        String rack = extractRackFromLocation(location);
        if (rack != null) {
            List<String> rackWorkers = rackToWorkers.get(rack);
            if (rackWorkers != null && !rackWorkers.isEmpty()) {
                for (String workerId : rackWorkers) {
                    JQuickWorkerNode worker = workers.get(workerId);
                    if (worker != null && worker.isHealthy()) {
                        return workerId;
                    }
                }
            }
        }
        // 后轮询选择
        return getWorkerByIndex(location.hashCode());
    }

    /**
     * 为数据分区分配Worker（考虑负载均衡）
     */
    public List<JQuickWorkerAssignment> assignPartitions(List<JQuickDataPartition> partitions, int parallelism) {
        List<JQuickWorkerAssignment> assignments = new ArrayList<>();
        // 统计每个Worker当前负载
        Map<String, Integer> workerLoad = new HashMap<>();
        for (JQuickWorkerNode worker : workers.values()) {
            workerLoad.put(worker.getWorkerId(), 0);
        }
        // 按数据本地性排序分区
        List<JQuickDataPartition> sortedPartitions = new ArrayList<>(partitions);
        sortedPartitions.sort((p1, p2) -> {
            // 优先分配有本地数据的Worker
            String loc1 = p1.getLocation();
            String loc2 = p2.getLocation();
            return Integer.compare(getWorkerCountForLocation(loc2), getWorkerCountForLocation(loc1));
        });

        for (int i = 0; i < Math.min(parallelism, sortedPartitions.size()); i++) {
            JQuickDataPartition partition = sortedPartitions.get(i);
            String preferredWorker = getWorkerForLocation(partition.getLocation());
            // 负载均衡：如果首选Worker负载过高，选择负载最低的
            String selectedWorker = selectWorkerWithLoadBalance(preferredWorker, workerLoad);
            HashMap<String,Object> map=new HashMap<>();
            map.put("partition", partition);
            assignments.add(new JQuickWorkerAssignment(i, selectedWorker, map));
            workerLoad.put(selectedWorker, workerLoad.get(selectedWorker) + 1);
        }
        // 如果分区数少于并行度，剩余的任务轮询分配
        for (int i = sortedPartitions.size(); i < parallelism; i++) {
            String selectedWorker = getWorkerWithMinLoad(workerLoad);
            HashMap<String,Object> map=new HashMap<>();
            map.put("parallelismId", i);
            assignments.add(new JQuickWorkerAssignment(i, selectedWorker, map));
            workerLoad.put(selectedWorker, workerLoad.get(selectedWorker) + 1);
        }

        return assignments;
    }

    /**
     * 选择负载均衡的Worker
     */
    private String selectWorkerWithLoadBalance(String preferredWorker, Map<String, Integer> workerLoad) {
        JQuickWorkerNode preferred = workers.get(preferredWorker);
        if (preferred != null && preferred.isHealthy()) {
            int preferredLoad = workerLoad.getOrDefault(preferredWorker, 0);
            int minLoad = getMinLoad(workerLoad);
            // 如果首选Worker负载不是太高，使用首选
            if (preferredLoad <= minLoad + config.getLoadBalanceThreshold()) {
                return preferredWorker;
            }
        }

        return getWorkerWithMinLoad(workerLoad);
    }

    /**
     * 获取负载最小的Worker
     */
    private String getWorkerWithMinLoad(Map<String, Integer> workerLoad) {
        return workerLoad.entrySet().stream()
                .filter(entry -> {
                    JQuickWorkerNode worker = workers.get(entry.getKey());
                    return worker != null && worker.isHealthy();
                })
                .min(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(workerIds.isEmpty() ? null : workerIds.get(0));
    }

    /**
     * 获取最小负载
     */
    private int getMinLoad(Map<String, Integer> workerLoad) {
        return workerLoad.values().stream()
                .mapToInt(Integer::intValue)
                .min()
                .orElse(0);
    }

    /**
     * 获取健康Worker列表
     */
    private List<String> getHealthyWorkerIds() {
        return workers.values().stream()
                .filter(JQuickWorkerNode::isHealthy)
                .map(JQuickWorkerNode::getWorkerId)
                .collect(Collectors.toList());
    }

    /**
     * 获取位置对应的Worker数量
     */
    private int getWorkerCountForLocation(String location) {
        List<String> workers = locationToWorkers.get(location);
        return workers != null ? workers.size() : 0;
    }

    /**
     * 从位置提取机架信息
     */
    private String extractRackFromLocation(String location) {
        if (location == null) return null;
        // 示例：/rack1/node1 -> rack1
        int lastSlash = location.lastIndexOf('/');
        if (lastSlash > 0) {
            return location.substring(0, lastSlash);
        }
        return location;
    }

    /**
     * 获取集群配置
     */
    public JQuickClusterConfig getConfig() {
        return config;
    }

    /**
     * 获取集群统计信息
     */
    public JQuickClusterStats getStats() {

        long totalCores = 0;

        long totalMemory = 0;

        int healthyCount = 0;
        for (JQuickWorkerNode worker : workers.values()) {
            totalCores += worker.getCpuCores();
            totalMemory += worker.getMemoryBytes();
            if (worker.isHealthy()) {
                healthyCount++;
            }
        }

        return new JQuickClusterStats(workers.size(), healthyCount, totalCores, totalMemory);
    }
}