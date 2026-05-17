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

import com.github.paohaijiao.fragment.Fragment;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * packageName com.github.paohaijiao.distributed
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/5/17
 */
public class DistributedPlan {

    private final Map<Long, Fragment> fragments;

    private final Fragment rootFragment;

    private final Map<String, Integer> clusterTopology;

    private final int defaultParallelism;

    public DistributedPlan(Fragment rootFragment) {
        this.fragments = new LinkedHashMap<>();
        this.rootFragment = rootFragment;
        this.clusterTopology = new HashMap<>();
        this.defaultParallelism = 4;
        buildFragmentMap(rootFragment);
    }

    private void buildFragmentMap(Fragment fragment) {
        fragments.put(fragment.getFragmentId(), fragment);
        for (Fragment child : fragment.getChildren()) {
            buildFragmentMap(child);
        }
    }

    public void addWorker(String host, int cores) {
        clusterTopology.put(host, cores);
    }

    public Fragment getRootFragment() { return rootFragment; }
    public Map<Long, Fragment> getFragments() { return fragments; }
    public Map<String, Integer> getClusterTopology() { return clusterTopology; }
    public int getDefaultParallelism() { return defaultParallelism; }

    public void printPlan() {
        System.out.println("=== Distributed Plan ===");
        printFragment(rootFragment, 0);
    }

    private void printFragment(Fragment fragment, int level) {
        String indent = "  ".repeat(level);
        System.out.println(indent + fragment);
        if (fragment.getOutput() != null) {
            System.out.println(indent + "  └── Exchange: " + fragment.getOutput().getType());
        }
        for (Fragment child : fragment.getChildren()) {
            printFragment(child, level + 1);
        }
    }

    public void setDefaultParallelism(int defaultParallelism) {
    }
}
