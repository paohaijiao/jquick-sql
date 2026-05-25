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

import com.github.paohaijiao.distributed.grpc.FragmentServiceProto;
import com.github.paohaijiao.exchange.JQuickExchangeNode;
import com.github.paohaijiao.expression.JQuickExpression;
import com.github.paohaijiao.fragment.JQuickFragment;
import com.github.paohaijiao.physical.JQuickPhysicalPlanNode;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class JQuickDistributedCoordinator {

    /**
     * 构建 Fragment 请求 - 直接使用 Proto 的 ExchangeInput
     */
    private FragmentServiceProto.FragmentRequest buildFragmentRequest(
            JQuickFragment fragment,
            String targetHost,
            int targetPort) {

        try {
            byte[] serializedPlan = serializePlan(fragment.getPlan());
            FragmentServiceProto.FragmentRequest.Builder builder =
                    FragmentServiceProto.FragmentRequest.newBuilder()
                            .setTaskId(UUID.randomUUID().toString())
                            .setFragmentId(String.valueOf(fragment.getFragmentId()))
                            .setFragmentType(fragment.getType().name())
                            .setSerializedPlan(com.google.protobuf.ByteString.copyFrom(serializedPlan))
                            .setParallelism(fragment.getParallelism());
            for (JQuickExchangeNode input : fragment.getInputs()) {
                FragmentServiceProto.ExchangeInput exchangeInput =
                        FragmentServiceProto.ExchangeInput.newBuilder()
                                .setExchangeId(input.getExchangeId())
                                .setSourceHost(targetHost)
                                .setSourcePort(targetPort)
                                .setPartitionStrategy(input.getPartitionStrategy().name())
                                .addAllPartitionKeys(extractPartitionKeyNames(input.getPartitionKeys()))
                                .build();
                builder.addInputs(exchangeInput);
            }
            if (fragment.getOutput() != null) {
                FragmentServiceProto.ExchangeOutput exchangeOutput =
                        FragmentServiceProto.ExchangeOutput.newBuilder()
                                .setExchangeId(fragment.getOutput().getExchangeId())
                                .setPartitionStrategy(fragment.getOutput().getPartitionStrategy().name())
                                .addAllPartitionKeys(extractPartitionKeyNames(fragment.getOutput().getPartitionKeys()))
                                .setTargetParallelism(fragment.getOutput().getParallelism())
                                .build();
                builder.setOutput(exchangeOutput);
            }

            return builder.build();

        } catch (Exception e) {
            throw new RuntimeException("Failed to build fragment request", e);
        }
    }

    private byte[] serializePlan(JQuickPhysicalPlanNode plan) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(plan);
            oos.flush();
            return baos.toByteArray();
        }
    }

    private List<String> extractPartitionKeyNames(List<JQuickExpression> expressions) {
        if (expressions == null) return Collections.emptyList();
        List<String> names = new ArrayList<>();
        for (JQuickExpression expr : expressions) {
            names.add(expr.toString());
        }
        return names;
    }
}