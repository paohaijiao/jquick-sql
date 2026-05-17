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


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Worker分配信息
 */
public class JQuickWorkerAssignment {

    private final int taskIndex;

    private final String workerId;

    private final Map<String, Object> context;

    public JQuickWorkerAssignment(int taskIndex, String workerId) {
        this(taskIndex, workerId, new HashMap<>());
    }

    public JQuickWorkerAssignment(int taskIndex, String workerId, Map<String, Object> context) {
        this.taskIndex = taskIndex;
        this.workerId = workerId;
        this.context = context != null ? new HashMap<>(context) : new HashMap<>();
    }

    public int getTaskIndex() { return taskIndex; }

    public String getWorkerId() { return workerId; }

    public Map<String, Object> getContext() { return Collections.unmodifiableMap(context); }

    public <T> T getContextValue(String key, Class<T> type) {
        Object value = context.get(key);
        if (value != null && type.isAssignableFrom(value.getClass())) {
            return type.cast(value);
        }
        return null;
    }

    @Override
    public String toString() {
        return String.format("WorkerAssignment{task=%d, worker='%s'}", taskIndex, workerId);
    }
}
