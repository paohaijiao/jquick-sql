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
package com.github.paohaijiao.distributed.service;

import com.github.paohaijiao.datasource.JQuickDataSourceManager;
import com.github.paohaijiao.distributed.grpc.FragmentServiceGrpc;
import com.github.paohaijiao.distributed.grpc.FragmentServiceProto;
import com.github.paohaijiao.fragment.JQuickFragment;
import com.github.paohaijiao.physical.JQuickPhysicalPlanNode;
import com.github.paohaijiao.physical.node.JQuickTableScanPhysicalNode;
import com.github.paohaijiao.statement.JQuickDataSet;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Fragment 执行器 - 实际执行计算逻辑
 */
public class JQuickFragmentExecutor {

    private final JQuickFragment fragment;

    private final List<FragmentServiceProto.ExchangeInput> inputs;

    private final FragmentServiceProto.ExchangeOutput output;

    private final Map<String, JQuickDataSet> receivedData;

    public JQuickFragmentExecutor(JQuickFragment fragment,
                            List<FragmentServiceProto.ExchangeInput> inputs,
                            FragmentServiceProto.ExchangeOutput output,
                            Map<String, JQuickDataSet> receivedData) {
        this.fragment = fragment;
        this.inputs = inputs;
        this.output = output;
        this.receivedData = receivedData;
    }

    public JQuickDataSet execute() {
        // 1. 收集输入数据
        JQuickDataSet inputData = collectInputData();

        // 2. 执行物理计划
        JQuickDataSet result = executePlan(fragment.getPlan(), inputData);

        // 3. 如果有输出，发送数据
        if (output != null) {
            sendOutputData(result);
        }

        return result;
    }

    private JQuickDataSet collectInputData() {
        if (inputs.isEmpty()) {
            // SOURCE Fragment：从数据源读取
            return readFromDataSource();
        }

        // INTERMEDIATE Fragment：从远程拉取数据
        JQuickDataSet merged = null;
        for (FragmentServiceProto.ExchangeInput input : inputs) {
            JQuickDataSet data = fetchDataFromRemote(
                    input.getSourceHost(),
                    input.getSourcePort(),
                    input.getExchangeId()
            );
            if (merged == null) {
                merged = data;
            } else {
                merged = merged.concat(data);
            }
        }
        return merged != null ? merged : JQuickDataSet.builder().build();
    }

    private JQuickDataSet readFromDataSource() {
        JQuickPhysicalPlanNode plan = fragment.getPlan();
        if (plan instanceof JQuickTableScanPhysicalNode) {
            JQuickTableScanPhysicalNode scan = (JQuickTableScanPhysicalNode) plan;
            JQuickDataSet table = JQuickDataSourceManager.getTable(scan.getTableName());
            Set<String> requiredColumns = scan.getRequiredColumns();
            if (requiredColumns != null && !requiredColumns.isEmpty()) {
                return table.select(requiredColumns.toArray(new String[0]));
            }
            return table;
        }
        return JQuickDataSet.builder().build();
    }

    private JQuickDataSet fetchDataFromRemote(String host, int port, String exchangeId) {
        try {
            ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port)
                    .usePlaintext()
                    .build();

            FragmentServiceGrpc.FragmentServiceBlockingStub stub =
                    FragmentServiceGrpc.newBlockingStub(channel);

            FragmentServiceProto.FetchRequest request =
                    FragmentServiceProto.FetchRequest.newBuilder()
                            .setExchangeId(exchangeId)
                            .setPartitionId(0)
                            .build();

            Iterator<FragmentServiceProto.DataChunk> chunks = stub.fetchData(request);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            while (chunks.hasNext()) {
                FragmentServiceProto.DataChunk chunk = chunks.next();
                baos.write(chunk.getData().toByteArray());
            }

            try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()))) {
                return (JQuickDataSet) ois.readObject();
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch data from " + host + ":" + port, e);
        }
    }

    private JQuickDataSet executePlan(JQuickPhysicalPlanNode node, JQuickDataSet input) {
        return input;
    }

    private void sendOutputData(JQuickDataSet data) {
    }
}
