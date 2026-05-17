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
package com.github.paohaijiao.scheduler;


import com.github.paohaijiao.fragment.JQuickFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 执行任务 - 最小调度单元
 */
public class JQuickTask {
    private static final AtomicLong idGenerator = new AtomicLong(0);

    private final long taskId;
    private final long fragmentId;
    private final int taskIndex;  // 并行任务索引 (0..parallelism-1)
    private final JQuickFragment fragment;
    private final TaskType type;
    private final List<JQuickTaskInput> inputs;
    private JQuickTaskOutput output;
    private TaskStatus status;
    private String assignedWorker;
    private long startTime;
    private long endTime;

    public enum TaskType {
        SOURCE_TASK,     // 数据源任务
        PROCESS_TASK,    // 处理任务
        SINK_TASK        // 汇出任务
    }

    public enum TaskStatus {
        PENDING, SCHEDULED, RUNNING, FINISHED, FAILED, CANCELLED
    }

    public JQuickTask(long fragmentId, int taskIndex, JQuickFragment fragment, TaskType type) {
        this.taskId = idGenerator.incrementAndGet();
        this.fragmentId = fragmentId;
        this.taskIndex = taskIndex;
        this.fragment = fragment;
        this.type = type;
        this.inputs = new ArrayList<>();
        this.status = TaskStatus.PENDING;
    }

    public void addInput(JQuickTaskInput input) {
        this.inputs.add(input);
    }

    public void setOutput(JQuickTaskOutput output) {
        this.output = output;
    }

    public void setAssignedWorker(String worker) {
        this.assignedWorker = worker;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
        if (status == TaskStatus.RUNNING && startTime == 0) {
            this.startTime = System.currentTimeMillis();
        } else if (status == TaskStatus.FINISHED || status == TaskStatus.FAILED) {
            this.endTime = System.currentTimeMillis();
        }
    }

    // Getters
    public long getTaskId() { return taskId; }
    public long getFragmentId() { return fragmentId; }
    public int getTaskIndex() { return taskIndex; }
    public JQuickFragment getFragment() { return fragment; }
    public TaskType getType() { return type; }
    public List<JQuickTaskInput> getInputs() { return inputs; }
    public JQuickTaskOutput getOutput() { return output; }
    public TaskStatus getStatus() { return status; }
    public String getAssignedWorker() { return assignedWorker; }
    public long getStartTime() { return startTime; }
    public long getEndTime() { return endTime; }
    public long getExecutionTime() {
        return endTime > 0 ? endTime - startTime : 0;
    }

    @Override
    public String toString() {
        return String.format("Task{id=%d, fragment=%d, index=%d, type=%s, status=%s, worker=%s}",
                taskId, fragmentId, taskIndex, type, status, assignedWorker);
    }
}
