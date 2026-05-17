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

import com.github.paohaijiao.fragment.JQuickFragment;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * packageName com.github.paohaijiao.distributed
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/5/17
 */
public class JQuickDistributedPlan {

    private final Map<Long, JQuickFragment> fragments;

    private final JQuickFragment rootFragment;

    private final Map<String, Integer> clusterTopology;

    private final int defaultParallelism;

    public JQuickDistributedPlan(JQuickFragment rootFragment) {
        this.fragments = new LinkedHashMap<>();
        this.rootFragment = rootFragment;
        this.clusterTopology = new HashMap<>();
        this.defaultParallelism = 4;
        buildFragmentMap(rootFragment);
    }

    private void buildFragmentMap(JQuickFragment fragment) {
        fragments.put(fragment.getFragmentId(), fragment);
        for (JQuickFragment child : fragment.getChildren()) {
            buildFragmentMap(child);
        }
    }

    public void addWorker(String host, int cores) {
        clusterTopology.put(host, cores);
    }

    public JQuickFragment getRootFragment() { return rootFragment; }

    public Map<Long, JQuickFragment> getFragments() { return fragments; }

    public Map<String, Integer> getClusterTopology() { return clusterTopology; }

    public int getDefaultParallelism() { return defaultParallelism; }

    public void printPlan() {
        System.out.println("=== Distributed Plan ===");
        printFragment(rootFragment, 0);
    }

    private void printFragment(JQuickFragment fragment, int level) {
        String  indent = IntStream.range(0, level).mapToObj(i -> " ").collect(Collectors.joining());
        System.out.println(indent + fragment);
        if (fragment.getOutput() != null) {
            System.out.println(indent + "  └── Exchange: " + fragment.getOutput().getType());
        }
        for (JQuickFragment child : fragment.getChildren()) {
            printFragment(child, level + 1);
        }
    }

    public void setDefaultParallelism(int defaultParallelism) {
    }
}
