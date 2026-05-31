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


import com.github.paohaijiao.proto.*;
import io.grpc.stub.StreamObserver;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 物理计划服务实现类
 *
 * 实现 gRPC 服务 JQuickPhysicalPlanService 中定义的所有方法：
 * - executeTask: 执行任务（同步）
 * - executeTaskStream: 流式执行任务
 * - cancelTask: 取消任务
 */
public class JQuickPhysicalPlanServiceImpl extends JQuickPhysicalPlanServiceGrpc.JQuickPhysicalPlanServiceImplBase {

    private static final Logger LOGGER = Logger.getLogger(JQuickPhysicalPlanServiceImpl.class.getName());

    private final JQuickWorker worker;

    public JQuickPhysicalPlanServiceImpl(JQuickWorker worker) {
        this.worker = worker;
    }

    /**
     * 执行任务 - 同步方式
     *
     * 接收客户端发送的任务请求，执行后返回完整响应
     *
     * @param request 任务执行请求
     * @param responseObserver 响应观察者
     */
    @Override
    public void executeTask(JQuickExecuteTaskRequest request, StreamObserver<JQuickExecuteTaskResponse> responseObserver) {
        String taskId = request.getTaskId();
        String queryId = request.getQueryId();
        LOGGER.info(String.format("Received executeTask request - taskId: %s, queryId: %s, taskIndex: %d/%d", taskId, queryId, request.getTaskIndex(), request.getTotalTasks()));
        try {
            // 委托给 Worker 执行
            JQuickExecuteTaskResponse response = worker.executeTask(request);
            LOGGER.info(String.format("Task completed - taskId: %s, status: %s, processedRows: %d, executionTime: %dms", taskId, response.getStatus(), response.getProcessedRows(), response.getExecutionTimeMs()));
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, String.format("Task execution failed - taskId: %s", taskId), e);
            JQuickExecuteTaskResponse errorResponse = JQuickExecuteTaskResponse.newBuilder()
                    .setTaskId(taskId)
                    .setStatus(JQuickTaskStatusProto.TASK_FAILED)
                    .setErrorMessage(e.getMessage())
                    .build();
            responseObserver.onNext(errorResponse);
            responseObserver.onCompleted();
        }
    }

    /**
     * 流式执行任务
     *
     * 将结果分批以流式方式返回，适合大数据量结果集
     *
     * @param request 任务执行请求
     * @param responseObserver 流式响应观察者
     */
    @Override
    public void executeTaskStream(JQuickExecuteTaskRequest request, StreamObserver<JQuickDataChunkProto> responseObserver) {
        String taskId = request.getTaskId();
        String queryId = request.getQueryId();
        LOGGER.info(String.format("Received executeTaskStream request - taskId: %s, queryId: %s", taskId, queryId));
        try {
            // 委托给 Worker 流式执行
            worker.executeTaskStream(request, responseObserver);
            LOGGER.info(String.format("Streaming task completed - taskId: %s", taskId));
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, String.format("Streaming task failed - taskId: %s", taskId), e);
            responseObserver.onError(e);
        }
    }

    /**
     * 取消任务
     *
     * 取消正在执行的任务
     *
     * @param request 取消请求
     * @param responseObserver 响应观察者
     */
    @Override
    public void cancelTask(JQuickCancelQueryRequest request, StreamObserver<JQuickCancelQueryResponse> responseObserver) {
        String queryId = request.getQueryId();
        String reason = request.getReason();
        LOGGER.info(String.format("Received cancelTask request - queryId: %s, reason: %s", queryId, reason));
        try {
            JQuickCancelQueryResponse response = worker.cancelTask(request);
            LOGGER.info(String.format("Task cancelled - queryId: %s, success: %s", queryId, response.getSuccess()));
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, String.format("Cancel task failed - queryId: %s", queryId), e);
            JQuickCancelQueryResponse errorResponse = JQuickCancelQueryResponse.newBuilder()
                    .setQueryId(queryId)
                    .setSuccess(false)
                    .setMessage("Cancel failed: " + e.getMessage())
                    .build();
            responseObserver.onNext(errorResponse);
            responseObserver.onCompleted();
        }
    }
}
