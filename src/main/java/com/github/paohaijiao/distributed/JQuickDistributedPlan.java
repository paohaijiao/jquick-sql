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

import com.github.paohaijiao.console.JConsole;
import com.github.paohaijiao.enums.JQuickFragmentType;
import com.github.paohaijiao.exchange.JQuickExchangeNode;
import com.github.paohaijiao.fragment.JQuickFragment;

import java.util.*;

/**
 * 分布式执行计划
 */
public class JQuickDistributedPlan {

    private static JConsole console=JConsole.initConsoleEnvironment();

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
    /**
     * 美观打印分布式执行计划
     */
    public  void printPlan() {
        if (this == null) {
            console.error("Cannot print null plan");
            return;
        }

        console.info("╔══════════════════════════════════════════════════════════════╗");
        console.info("║                 JQuick Distributed Execution Plan           ║");
        console.info("╚══════════════════════════════════════════════════════════════╝");
        console.info("");

        // 基本信息
        console.info("┌─────────────────── Plan Overview ───────────────────┐");
        console.info("│ Default Parallelism: " + padRight(this.getDefaultParallelism(), 34) + "│");
        console.info("│ Total Fragments:    " + padRight(this.getAllFragments().size(), 34) + "│");
        console.info("│ Source Fragments:   " + padRight(this.getSourceFragments().size(), 34) + "│");
        console.info("└────────────────────────────────────────────────────┘");
        console.info("");

        // 打印树形结构
        console.info("┌────────────────── Fragment Tree ────────────────────┐");
        printFragmentTree(this.getRootFragment(), "", true);
        console.info("└────────────────────────────────────────────────────┘");
        console.info("");

        // 打印执行顺序
        console.info("┌──────────────── Execution Order ────────────────────┐");
        List<JQuickFragment> order = this.getExecutionOrder();
        for (int i = 0; i < order.size(); i++) {
            JQuickFragment fragment = order.get(i);
            String marker = (i == order.size() - 1) ? "└── " : "├── ";
            console.info(marker + formatFragmentInfo(fragment));
        }
        console.info("└────────────────────────────────────────────────────┘");
        console.info("");

        // 打印详细信息
        printDetailedFragments(this);
    }

    /**
     * 递归打印 Fragment 树
     */
    private static void printFragmentTree(JQuickFragment fragment, String prefix, boolean isTail) {
        String connector = isTail ? "└── " : "├── ";
        String line = prefix + connector + formatFragmentHeader(fragment);
        console.info(line);

        List<JQuickFragment> children = fragment.getChildren();
        for (int i = 0; i < children.size(); i++) {
            boolean childIsTail = (i == children.size() - 1);
            String childPrefix = prefix + (isTail ? "    " : "│   ");
            printFragmentTree(children.get(i), childPrefix, childIsTail);
        }
    }

    /**
     * 格式化 Fragment 头部信息
     */
    private static String formatFragmentHeader(JQuickFragment fragment) {
        StringBuilder sb = new StringBuilder();
        sb.append("[F").append(fragment.getFragmentId()).append("] ");

        // 根据类型使用不同符号
        switch (fragment.getType()) {
            case SOURCE:
                sb.append("🗄️  SOURCE");
                break;
            case INTERMEDIATE:
                sb.append("⚙️  INTERMEDIATE");
                break;
            case SINK:
                sb.append("💾 SINK");
                break;
            default:
                sb.append(fragment.getType());
        }

        sb.append(" (parallelism: ").append(fragment.getParallelism()).append(")");

        if (fragment.getAssignedHost() != null) {
            sb.append(" @").append(fragment.getAssignedHost());
        }

        return sb.toString();
    }

    /**
     * 格式化 Fragment 详细信息（用于执行顺序）
     */
    private static String formatFragmentInfo(JQuickFragment fragment) {
        StringBuilder sb = new StringBuilder();
        sb.append("F").append(fragment.getFragmentId());
        sb.append(" [").append(fragment.getType()).append("]");
        sb.append(" → ").append(fragment.getPlan().getClass().getSimpleName());

        if (fragment.getInputs() != null && !fragment.getInputs().isEmpty()) {
            sb.append(" | inputs: ").append(fragment.getInputs().size());
        }

        if (fragment.getOutput() != null) {
            sb.append(" | has output");
        }

        return sb.toString();
    }

    /**
     * 打印所有 Fragment 的详细信息
     */
    private static void printDetailedFragments(JQuickDistributedPlan plan) {
        console.info("┌─────────────── Fragment Details ───────────────┐");
        for (JQuickFragment fragment : plan.getAllFragments()) {
            console.info("│");
            console.info("├─ Fragment " + fragment.getFragmentId() + " " + repeatStr("─", 45 - String.valueOf(fragment.getFragmentId()).length()));
            console.info("│  Type:        " + fragment.getType());
            console.info("│  Parallelism: " + fragment.getParallelism());
            console.info("│  Plan Node:   " + fragment.getPlan().getClass().getSimpleName());
            if (fragment.getAssignedHost() != null) {
                console.info("│  Assigned To: " + fragment.getAssignedHost());
            }
            if (fragment.getInputs() != null && !fragment.getInputs().isEmpty()) {
                console.info("│  Inputs:      " + fragment.getInputs().size());
                for (JQuickExchangeNode input : fragment.getInputs()) {
                    console.info("│    └─ " + input.getClass().getSimpleName());
                }
            }
            if (fragment.getOutput() != null) {
                console.info("│  Output:      " + fragment.getOutput().getClass().getSimpleName());
            }
            if (!fragment.getChildren().isEmpty()) {
                console.info("│  Children:    " + fragment.getChildren().size());
                for (JQuickFragment child : fragment.getChildren()) {
                    console.info("│    └─ F" + child.getFragmentId());
                }
            }
        }
        console.info("└────────────────────────────────────────────────┘");
    }

    /**
     * 简化版打印（适用于日志）
     */
    public static void printPlanCompact(JQuickDistributedPlan plan) {
        if (plan == null) return;
        console.info("[Plan] " + plan.getAllFragments().size() + " fragments, " + "parallelism=" + plan.getDefaultParallelism());
        for (JQuickFragment fragment : plan.getAllFragments()) {
            console.debug("  └─ F" + fragment.getFragmentId() + ": " + fragment.getType() + " (p=" + fragment.getParallelism() + ")");
        }
    }

    /**
     * 打印 Fragment 之间的数据流关系
     */
    public static void printDataFlow(JQuickDistributedPlan plan) {
        console.info("┌──────────────── Data Flow ─────────────────┐");
        for (JQuickFragment fragment : plan.getAllFragments()) {
            if (fragment.getOutput() != null) {
                for (JQuickFragment child : fragment.getChildren()) {
                    console.info("  F" + fragment.getFragmentId() + " ──► F" + child.getFragmentId());
                }
            }
        }
        console.info("└────────────────────────────────────────────┘");
    }

    // 辅助方法
    private static String padRight(Object obj, int length) {
        String str = String.valueOf(obj);
        if (str.length() >= length) return str;
        return str + repeatStr(" ", length - str.length());
    }

    private static String repeatStr(String str, int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(str);
        }
        return sb.toString();
    }
}