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

public class JQuickFragmentResult {

    private final long fragmentId;

    private JQuickDataSet data;

    private long scannedRows = 0;

    private long processedRows = 0;

    private long networkBytes = 0;

    private long executionTime = 0;

    private boolean success = true;

    private Throwable error;

    public JQuickFragmentResult(long fragmentId) {
        this.fragmentId = fragmentId;
    }

    public JQuickFragmentResult(long fragmentId, JQuickDataSet data) {
        this.fragmentId = fragmentId;
        this.data = data;
        if (data != null) {
            this.processedRows = data.size();
        }
    }

    public long getFragmentId() {
        return fragmentId;
    }
    public JQuickDataSet getData() { return data; }

    public long getScannedRows() {
        return scannedRows;
    }

    public void setScannedRows(long scannedRows) {
        this.scannedRows = scannedRows;
    }

    public long getProcessedRows() {
        return processedRows;
    }
    public long getExecutionTime() { return executionTime; }

    public void setProcessedRows(long processedRows) {
        this.processedRows = processedRows;
    }

    public long getNetworkBytes() {
        return networkBytes;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Throwable getError() {
        return error;
    }

    public void setError(Throwable error) {
        this.error = error;
        this.success = false;
    }

    public void setData(JQuickDataSet data) {
        this.data = data;
        if (data != null) {
            this.processedRows = data.size();
        }
    }
    public void setExecutionTime(long executionTime) { this.executionTime = executionTime; }

    public void addScannedRows(long count) {
        this.scannedRows += count;
    }

    public void addNetworkBytes(long bytes) {
        this.networkBytes += bytes;
    }
}
