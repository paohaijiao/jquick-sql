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
import com.github.paohaijiao.statement.JQuickDataSet;

public class FragmentResult {
    private boolean success;
    private JQuickDataSet data;
    private Throwable error;
    private long executionTime;

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public JQuickDataSet getData() { return data; }
    public void setData(JQuickDataSet data) { this.data = data; }
    public Throwable getError() { return error; }
    public void setError(Throwable error) { this.error = error; }
    public long getExecutionTime() { return executionTime; }
    public void setExecutionTime(long executionTime) { this.executionTime = executionTime; }
}
