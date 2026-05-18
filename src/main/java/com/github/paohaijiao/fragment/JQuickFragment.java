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
package com.github.paohaijiao.fragment;


import com.github.paohaijiao.exchange.JQuickExchangeNode;
import com.github.paohaijiao.physical.JQuickPhysicalPlanNode;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 执行片段 - 分布式执行的最小单元
 */
public class JQuickFragment {
    private static final AtomicLong idGenerator = new AtomicLong(0);

    private final long fragmentId;
    private final FragmentType type;
    private final JQuickPhysicalPlanNode plan;
    private final List<JQuickFragment> children;
    private final List<JQuickExchangeNode> inputs;
    private JQuickExchangeNode output;
    private int parallelism;
    private String assignedHost;

    public enum FragmentType {
        SOURCE,      // 源端片段（扫描数据）
        INTERMEDIATE, // 中间片段（处理数据）
        SINK         // 汇片段（输出结果）
    }

    public JQuickFragment(FragmentType type, JQuickPhysicalPlanNode plan) {
        this.fragmentId = idGenerator.incrementAndGet();
        this.type = type;
        this.plan = plan;
        this.children = new ArrayList<>();
        this.inputs = new ArrayList<>();
        this.parallelism = 1;
    }

    public void addChild(JQuickFragment child) {
        this.children.add(child);
    }

    public void addInput(JQuickExchangeNode input) {
        this.inputs.add(input);
    }

    public void setOutput(JQuickExchangeNode output) {
        this.output = output;
    }

    public void setParallelism(int parallelism) {
        this.parallelism = parallelism;
    }

    public void setAssignedHost(String host) {
        this.assignedHost = host;
    }

    public long getFragmentId() { return fragmentId; }
    public FragmentType getType() { return type; }
    public JQuickPhysicalPlanNode getPlan() { return plan; }
    public List<JQuickFragment> getChildren() { return children; }
    public List<JQuickExchangeNode> getInputs() { return inputs; }
    public JQuickExchangeNode getOutput() { return output; }
    public int getParallelism() { return parallelism; }
    public String getAssignedHost() { return assignedHost; }

    @Override
    public String toString() {
        return String.format("Fragment{id=%d, type=%s, parallelism=%d}",
                fragmentId, type, parallelism);
    }
}




