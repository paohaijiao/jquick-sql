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
package com.github.paohaijiao.worker;

import com.github.paohaijiao.fragment.Fragment;

import java.util.*;

/**
 * 任务执行实例
 */
public class TaskExecution {
    private final long taskId;
    private final Fragment fragment;
    private final List<String> assignedWorkers;
    private final List<FragmentResult> results;
    private TaskStatus status;
    private int retryCount;
    private Throwable error;

    public enum TaskStatus {
        PENDING, RUNNING, COMPLETED, FAILED
    }

    public TaskExecution(long taskId, Fragment fragment, List<String> assignedWorkers) {
        this.taskId = taskId;
        this.fragment = fragment;
        this.assignedWorkers = assignedWorkers;
        this.results = new ArrayList<>();
        this.status = TaskStatus.PENDING;
        this.retryCount = 0;
    }

    public void addResult(FragmentResult result) {
        results.add(result);
    }

    public void markCompleted() {
        this.status = TaskStatus.COMPLETED;
    }

    public void markFailed(Throwable e) {
        this.status = TaskStatus.FAILED;
        this.error = e;
    }

    public void incrementRetry() { retryCount++; }

    public long getTaskId() { return taskId; }
    public TaskStatus getStatus() { return status; }
    public int getRetryCount() { return retryCount; }
}
