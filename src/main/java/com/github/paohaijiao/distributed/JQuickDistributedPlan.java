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
package com.github.paohaijiao.distributed;

import com.github.paohaijiao.enums.JQuickFragmentType;
import com.github.paohaijiao.fragment.JQuickFragment;
import java.util.*;

/**
 * 分布式执行计划
 */
public class JQuickDistributedPlan {

    private final JQuickFragment rootFragment;

    private int defaultParallelism;

    private final Map<Long, JQuickFragment> fragmentMap;

    public JQuickDistributedPlan(JQuickFragment rootFragment) {
        this.rootFragment = rootFragment;
        this.fragmentMap = new HashMap<>();
        buildFragmentMap(rootFragment);
    }

    private void buildFragmentMap(JQuickFragment fragment) {
        fragmentMap.put(fragment.getFragmentId(), fragment);
        for (JQuickFragment child : fragment.getChildren()) {
            buildFragmentMap(child);
        }
    }

    public JQuickFragment getRootFragment() {
        return rootFragment;
    }

    public void setDefaultParallelism(int parallelism) {
        this.defaultParallelism = parallelism;
    }

    public int getDefaultParallelism() {
        return defaultParallelism;
    }

    /**
     * 获取所有 Fragment（广度优先遍历）
     */
    public List<JQuickFragment> getAllFragments() {
        List<JQuickFragment> result = new ArrayList<>();
        Queue<JQuickFragment> queue = new LinkedList<>();
        queue.add(rootFragment);
        while (!queue.isEmpty()) {
            JQuickFragment fragment = queue.poll();
            result.add(fragment);
            queue.addAll(fragment.getChildren());
        }
        return result;
    }

    /**
     * 获取所有 SOURCE 类型的 Fragment
     */
    public List<JQuickFragment> getSourceFragments() {
        List<JQuickFragment> result = new ArrayList<>();
        for (JQuickFragment fragment : getAllFragments()) {
            if (fragment.getType() == JQuickFragmentType.SOURCE) {
                result.add(fragment);
            }
        }
        return result;
    }

    /**
     * 获取 Fragment 的执行顺序（拓扑排序）
     */
    public List<JQuickFragment> getExecutionOrder() {
        List<JQuickFragment> order = new ArrayList<>();
        Set<Long> visited = new HashSet<>();
        for (JQuickFragment fragment : getAllFragments()) {
            if (!visited.contains(fragment.getFragmentId())) {
                topologicalSort(fragment, order, visited);
            }
        }
        return order;
    }

    private void topologicalSort(JQuickFragment fragment, List<JQuickFragment> order, Set<Long> visited) {
        visited.add(fragment.getFragmentId());
        for (JQuickFragment child : fragment.getChildren()) {
            if (!visited.contains(child.getFragmentId())) {
                topologicalSort(child, order, visited);
            }
        }
        order.add(fragment);
    }

    public JQuickFragment getFragmentById(long id) {
        return fragmentMap.get(id);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("JQuickDistributedPlan {\n");
        sb.append("  defaultParallelism: ").append(defaultParallelism).append("\n");
        sb.append("  fragments: [\n");
        for (JQuickFragment fragment : getAllFragments()) {
            sb.append("    ").append(fragment).append("\n");
        }
        sb.append("  ]\n");
        sb.append("}");
        return sb.toString();
    }
}