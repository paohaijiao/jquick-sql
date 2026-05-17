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


import java.util.ArrayList;
import java.util.List;

/**
 * 任务输出
 */
public class JQuickTaskOutput {
    private final String outputId;
    private final List<JQuickExchangeChannel> channels;
    private final OutputType type;

    public enum OutputType {
        LOCAL,      // 本地输出
        SHUFFLE,    // Shuffle 输出
        BROADCAST,  // 广播输出
        COLLECT     // 收集输出
    }

    public JQuickTaskOutput(String outputId, OutputType type) {
        this.outputId = outputId;
        this.type = type;
        this.channels = new ArrayList<>();
    }

    public void addChannel(JQuickExchangeChannel channel) {
        this.channels.add(channel);
    }

    // Getters
    public String getOutputId() { return outputId; }
    public List<JQuickExchangeChannel> getChannels() { return channels; }
    public OutputType getType() { return type; }
}
