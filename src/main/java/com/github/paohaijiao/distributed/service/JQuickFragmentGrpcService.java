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

import com.github.paohaijiao.distributed.grpc.FragmentServiceGrpc;
import com.github.paohaijiao.distributed.grpc.FragmentServiceProto;
import com.github.paohaijiao.enums.JQuickFragmentType;
import com.github.paohaijiao.fragment.JQuickFragment;
import com.github.paohaijiao.grpc.annotation.JQuickGrpcService;
import com.github.paohaijiao.physical.JQuickPhysicalPlanNode;
import com.github.paohaijiao.statement.JQuickDataSet;
import io.grpc.stub.StreamObserver;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@JQuickGrpcService(name = "FragmentService", version = 1)
public class JQuickFragmentGrpcService extends FragmentServiceGrpc.FragmentServiceImplBase {

    private final Map<String, JQuickDataSet> receivedData = new ConcurrentHashMap<>();

    @Override
    public void executeFragment(FragmentServiceProto.FragmentRequest request, StreamObserver<FragmentServiceProto.FragmentResponse> responseObserver) {
        try {
            JQuickPhysicalPlanNode plan = deserializePlan(request.getSerializedPlan().toByteArray());

            //创建 Fragment
            JQuickFragmentType fragmentType = JQuickFragmentType.valueOf(request.getFragmentType());
            JQuickFragment fragment = new JQuickFragment(fragmentType, plan);
            fragment.setParallelism(request.getParallelism());

            //直接使用 Proto 的 ExchangeInput 构建输入信息
            List<FragmentServiceProto.ExchangeInput> inputs = request.getInputsList();

            // 直接使用 Proto 的 ExchangeOutput
            FragmentServiceProto.ExchangeOutput output = request.hasOutput() ? request.getOutput() : null;

            // 异步执行 - 直接传入 Proto 对象
            JQuickFragmentExecutor executor = new JQuickFragmentExecutor(fragment, inputs, output, receivedData);

            CompletableFuture.supplyAsync(executor::execute)
                    .thenAccept(result -> {
                        FragmentServiceProto.FragmentResponse response =
                                FragmentServiceProto.FragmentResponse.newBuilder()
                                        .setTaskId(request.getTaskId())
                                        .setFragmentId(request.getFragmentId())
                                        .setSuccess(true)
                                        .setRowsProcessed(result != null ? result.size() : 0)
                                        .build();
                        responseObserver.onNext(response);
                        responseObserver.onCompleted();
                    })
                    .exceptionally(throwable -> {
                        FragmentServiceProto.FragmentResponse response =
                                FragmentServiceProto.FragmentResponse.newBuilder()
                                        .setTaskId(request.getTaskId())
                                        .setFragmentId(request.getFragmentId())
                                        .setSuccess(false)
                                        .setErrorMessage(throwable.getMessage())
                                        .build();
                        responseObserver.onNext(response);
                        responseObserver.onCompleted();
                        return null;
                    });

        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    private JQuickPhysicalPlanNode deserializePlan(byte[] data) throws Exception {
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data))) {
            return (JQuickPhysicalPlanNode) ois.readObject();
        }
    }

    @Override
    public StreamObserver<FragmentServiceProto.DataChunk> sendData(
            StreamObserver<FragmentServiceProto.DataAck> responseObserver) {
        return new StreamObserver<FragmentServiceProto.DataChunk>() {
            private final Map<String, ByteArrayOutputStream> buffers = new HashMap<>();

            @Override
            public void onNext(FragmentServiceProto.DataChunk chunk) {
                try {
                    String key = chunk.getExchangeId() + "_" + chunk.getPartitionId();
                    ByteArrayOutputStream buffer = buffers.computeIfAbsent(key,
                            k -> new ByteArrayOutputStream());
                    buffer.write(chunk.getData().toByteArray());

                    if (chunk.getIsLast()) {
                        try (ObjectInputStream ois = new ObjectInputStream(
                                new ByteArrayInputStream(buffer.toByteArray()))) {
                            JQuickDataSet dataset = (JQuickDataSet) ois.readObject();
                            receivedData.put(key, dataset);
                        }
                        buffers.remove(key);

                        FragmentServiceProto.DataAck ack =
                                FragmentServiceProto.DataAck.newBuilder()
                                        .setExchangeId(chunk.getExchangeId())
                                        .setPartitionId(chunk.getPartitionId())
                                        .setReceivedSeq(chunk.getSequenceNumber())
                                        .setSuccess(true)
                                        .build();
                        responseObserver.onNext(ack);
                    }
                } catch (Exception e) {
                    responseObserver.onError(e);
                }
            }

            @Override
            public void onError(Throwable t) {
                responseObserver.onError(t);
            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }

    @Override
    public void fetchData(FragmentServiceProto.FetchRequest request,
                          StreamObserver<FragmentServiceProto.DataChunk> responseObserver) {
        try {
            String key = request.getExchangeId() + "_" + request.getPartitionId();
            JQuickDataSet dataset = receivedData.remove(key);

            if (dataset != null) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
                    oos.writeObject(dataset);
                    oos.flush();
                }

                FragmentServiceProto.DataChunk chunk =
                        FragmentServiceProto.DataChunk.newBuilder()
                                .setExchangeId(request.getExchangeId())
                                .setPartitionId(request.getPartitionId())
                                .setSequenceNumber(0)
                                .setData(com.google.protobuf.ByteString.copyFrom(baos.toByteArray()))
                                .setIsLast(true)
                                .build();
                responseObserver.onNext(chunk);
            }
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(e);
        }
    }

    @Override
    public void healthCheck(FragmentServiceProto.HealthCheckRequest request,
                            StreamObserver<FragmentServiceProto.HealthCheckResponse> responseObserver) {
        FragmentServiceProto.HealthCheckResponse response =
                FragmentServiceProto.HealthCheckResponse.newBuilder()
                        .setStatus(FragmentServiceProto.HealthCheckResponse.ServingStatus.SERVING)
                        .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
