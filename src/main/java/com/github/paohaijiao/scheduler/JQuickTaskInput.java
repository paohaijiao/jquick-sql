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


/**
 * 任务输入
 */
public class JQuickTaskInput {

    private final String inputId;

    private final long sourceTaskId;

    private final JQuickExchangeChannel channel;

    private final InputType type;

    public enum InputType {
        LOCAL,      // 本地输入
        REMOTE,     // 远程输入（需要网络传输）
        SHUFFLE,    // Shuffle 输入
        BROADCAST   // 广播输入
    }

    public JQuickTaskInput(String inputId, long sourceTaskId, JQuickExchangeChannel channel, InputType type) {
        this.inputId = inputId;
        this.sourceTaskId = sourceTaskId;
        this.channel = channel;
        this.type = type;
    }

    // Getters
    public String getInputId() { return inputId; }
    public long getSourceTaskId() { return sourceTaskId; }
    public JQuickExchangeChannel getChannel() { return channel; }
    public InputType getType() { return type; }
}
