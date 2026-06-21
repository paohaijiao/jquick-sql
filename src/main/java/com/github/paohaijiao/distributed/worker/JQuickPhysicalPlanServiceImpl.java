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
package com.github.paohaijiao.distributed.worker;


import com.github.paohaijiao.console.JConsole;
import com.github.paohaijiao.distributed.coordinator.JQuickCoordinator;
import com.github.paohaijiao.proto.*;
import io.grpc.stub.StreamObserver;

/**
 * 物理计划服务实现类
 *
 * 实现 gRPC 服务 JQuickPhysicalPlanService 中定义的所有方法：
 * - executeTask: 执行任务（同步）
 * - executeTaskStream: 流式执行任务
 * - cancelTask: 取消任务
 */
public class JQuickPhysicalPlanServiceImpl extends JQuickPhysicalPlanServiceGrpc.JQuickPhysicalPlanServiceImplBase {

    private static final JConsole console = JConsole.initConsoleEnvironment();

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
        console.info(String.format("Received executeTask request - taskId: %s, queryId: %s, taskIndex: %d/%d", taskId, queryId, request.getTaskIndex(), request.getTotalTasks()));
        try {
            // 委托给 Worker 执行
            JQuickExecuteTaskResponse response = worker.executeTask(request);
            console.info(String.format("Task completed - taskId: %s, status: %s, processedRows: %d, executionTime: %dms", taskId, response.getStatus(), response.getProcessedRows(), response.getExecutionTimeMs()));
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            console.error( String.format("Task execution failed - taskId: %s", taskId), e);
            JQuickExecuteTaskResponse errorResponse = JQuickExecuteTaskResponse.newBuilder().setTaskId(taskId).setStatus(JQuickTaskStatusProto.TASK_FAILED).setErrorMessage(e.getMessage()).build();
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
        console.info(String.format("Received executeTaskStream request - taskId: %s, queryId: %s", taskId, queryId));
        try {
            // 委托给 Worker 流式执行
            worker.executeTaskStream(request, responseObserver);
            console.info(String.format("Streaming task completed - taskId: %s", taskId));
        } catch (Exception e) {
            console.error(String.format("Streaming task failed - taskId: %s", taskId), e);
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
        console.info(String.format("Received cancelTask request - queryId: %s, reason: %s", queryId, reason));
        try {
            JQuickCancelQueryResponse response = worker.cancelTask(request);
            console.info(String.format("Task cancelled - queryId: %s, success: %s", queryId, response.getSuccess()));
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            console.error( String.format("Cancel task failed - queryId: %s", queryId), e);
            JQuickCancelQueryResponse errorResponse = JQuickCancelQueryResponse.newBuilder()
                    .setQueryId(queryId)
                    .setSuccess(false)
                    .setMessage("Cancel failed: " + e.getMessage())
                    .build();
            responseObserver.onNext(errorResponse);
            responseObserver.onCompleted();
        }
    }

    /**
     * 心跳检测
     *
     * Worker 向 Coordinator 发送心跳，报告状态
     *
     * @param request 心跳请求
     * @param responseObserver 响应观察者
     */
    @Override
    public void heartbeat(JQuickHeartbeatRequest request, StreamObserver<JQuickHeartbeatResponse> responseObserver) {
        String workerId = request.getWorkerId();
        console.info(String.format("Received heartbeat from worker: %s, currentTasks: %d, cpuUsage: %.2f%%, memoryUsage: %.2f%%",
                workerId, request.getCurrentTasks(), request.getCpuUsage() * 100, request.getMemoryUsage() * 100));
        try {
            // 更新 Worker 状态
            worker.updateHeartbeat(request);
            
            // 构建响应
            JQuickHeartbeatResponse response = JQuickHeartbeatResponse.newBuilder()
                    .setHealthy(true)
                    .setMessage("Heartbeat received")
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            console.error(String.format("Heartbeat failed - workerId: %s", workerId), e);
            JQuickHeartbeatResponse errorResponse = JQuickHeartbeatResponse.newBuilder()
                    .setHealthy(false)
                    .setMessage("Heartbeat failed: " + e.getMessage())
                    .build();
            responseObserver.onNext(errorResponse);
            responseObserver.onCompleted();
        }
    }

    /**
     * 注册 Worker
     *
     * Worker 启动时向 Coordinator 注册
     *
     * @param request 注册请求
     * @param responseObserver 响应观察者
     */
    @Override
    public void registerWorker(JQuickRegisterWorkerRequest request, StreamObserver<JQuickRegisterWorkerResponse> responseObserver) {
        String workerId = request.getWorkerId();
        String host = request.getHost();
        int port = request.getPort();
        console.info(String.format("Received registerWorker request - workerId: %s, host: %s, port: %d, maxParallelism: %d",
                workerId, host, port, request.getMaxParallelism()));
        try {
            // 注册 Worker
            worker.registerWorker(request);
            
            // 获取所有 Worker 端点
            java.util.List<JQuickWorkerEndpointProto> allWorkers = worker.getAllWorkerEndpoints();
            
            // 构建响应
            JQuickRegisterWorkerResponse response = JQuickRegisterWorkerResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("Worker registered successfully")
                    .setCoordinatorId(worker.getWorkerId())
                    .addAllAllWorkers(allWorkers)
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            console.error(String.format("Register worker failed - workerId: %s", workerId), e);
            JQuickRegisterWorkerResponse errorResponse = JQuickRegisterWorkerResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("Registration failed: " + e.getMessage())
                    .build();
            responseObserver.onNext(errorResponse);
            responseObserver.onCompleted();
        }
    }

    /**
     * 更新 Worker 端点
     *
     * Coordinator 通知 Worker 更新其他 Worker 的端点信息
     *
     * @param request 更新请求
     * @param responseObserver 响应观察者
     */
    @Override
    public void updateWorkerEndpoints(JQuickUpdateWorkerEndpointsRequest request, StreamObserver<JQuickUpdateWorkerEndpointsResponse> responseObserver) {
        console.info(String.format("Received updateWorkerEndpoints request - endpoints count: %d, timestamp: %d", request.getEndpointsCount(), request.getTimestamp()));
        try {
            java.util.List<JQuickCoordinator.WorkerEndpoint> endpoints = new java.util.ArrayList<>();
            for (JQuickWorkerEndpointProto proto : request.getEndpointsList()) {
                JQuickCoordinator.WorkerEndpoint endpoint = new JQuickCoordinator.WorkerEndpoint(proto.getWorkerId(), proto.getHost(), proto.getPort(), proto.getIndex());
                endpoint.setHealthy(proto.getHealthy());
                endpoints.add(endpoint);
            }
            worker.setWorkerEndpoints(endpoints);
            JQuickUpdateWorkerEndpointsResponse response = JQuickUpdateWorkerEndpointsResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("Worker endpoints updated successfully")
                    .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            console.error("Update worker endpoints failed", e);
            JQuickUpdateWorkerEndpointsResponse errorResponse = JQuickUpdateWorkerEndpointsResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("Update failed: " + e.getMessage())
                    .build();
            responseObserver.onNext(errorResponse);
            responseObserver.onCompleted();
        }
    }

    /**
     * 获取 Worker 端点
     *
     * 查询当前所有 Worker 的端点信息
     *
     * @param request 查询请求
     * @param responseObserver 响应观察者
     */
    @Override
    public void getWorkerEndpoints(JQuickGetWorkerEndpointsRequest request, StreamObserver<JQuickGetWorkerEndpointsResponse> responseObserver) {
        String workerId = request.getWorkerId();
        console.info(String.format("Received getWorkerEndpoints request - workerId: %s", workerId));
        try {
            java.util.List<JQuickWorkerEndpointProto> endpoints = worker.getAllWorkerEndpoints();
            JQuickGetWorkerEndpointsResponse response = JQuickGetWorkerEndpointsResponse.newBuilder()
                    .setSuccess(true)
                    .addAllEndpoints(endpoints)
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            console.error("Get worker endpoints failed", e);
            JQuickGetWorkerEndpointsResponse errorResponse = JQuickGetWorkerEndpointsResponse.newBuilder()
                    .setSuccess(false)
                    .build();
            responseObserver.onNext(errorResponse);
            responseObserver.onCompleted();
        }
    }

    /**
     * Worker 离开
     *
     * Worker 主动离开集群
     *
     * @param request 离开请求
     * @param responseObserver 响应观察者
     */
    @Override
    public void workerLeave(JQuickWorkerLeaveRequest request, StreamObserver<JQuickWorkerLeaveResponse> responseObserver) {
        String workerId = request.getWorkerId();
        String reason = request.getReason();
        console.info(String.format("Received workerLeave request - workerId: %s, reason: %s, ongoingTasks: %d", workerId, reason, request.getOngoingTaskIdsCount()));
        try {
            worker.handleWorkerLeave(request);
            JQuickWorkerLeaveResponse response = JQuickWorkerLeaveResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("Worker leave acknowledged")
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            console.error(String.format("Worker leave failed - workerId: %s", workerId), e);
            JQuickWorkerLeaveResponse errorResponse = JQuickWorkerLeaveResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("Leave failed: " + e.getMessage())
                    .build();
            responseObserver.onNext(errorResponse);
            responseObserver.onCompleted();
        }
    }
}
