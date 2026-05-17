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

import com.github.paohaijiao.distributed.DistributedPlan;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 作业执行实例
 */
public class JobExecution {
    private static final AtomicLong idGenerator = new AtomicLong(0);

    private final long jobId;
    private final DistributedPlan plan;
    private final List<TaskExecution> tasks;
    private JobStatus status;
    private Throwable error;
    private long startTime;
    private long endTime;

    public enum JobStatus {
        PENDING, RUNNING, COMPLETED, FAILED, CANCELLED
    }

    public JobExecution(DistributedPlan plan) {
        this.jobId = idGenerator.incrementAndGet();
        this.plan = plan;
        this.tasks = new ArrayList<>();
        this.status = JobStatus.PENDING;
        this.startTime = System.currentTimeMillis();
    }

    public void addTask(TaskExecution task) {
        tasks.add(task);
    }

    public void markCompleted() {
        this.status = JobStatus.COMPLETED;
        this.endTime = System.currentTimeMillis();
    }

    public void markFailed(Throwable e) {
        this.status = JobStatus.FAILED;
        this.error = e;
        this.endTime = System.currentTimeMillis();
    }

    public void setError(Throwable e) {
        this.error = e;
        this.status = JobStatus.FAILED;
    }

    public boolean isCancelled() { return status == JobStatus.CANCELLED; }
    public long getJobId() { return jobId; }
    public JobStatus getStatus() { return status; }
    public long getExecutionTime() { return endTime - startTime; }
}
