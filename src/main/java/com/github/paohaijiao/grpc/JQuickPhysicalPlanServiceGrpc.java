package com.github.paohaijiao.proto;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 * <pre>
 * 物理计划服务（Worker 端）
 * </pre>
 */
@io.grpc.stub.annotations.GrpcGenerated
public final class JQuickPhysicalPlanServiceGrpc {

  private JQuickPhysicalPlanServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "com.github.paohaijiao.proto.JQuickPhysicalPlanService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.github.paohaijiao.proto.JQuickExecuteTaskRequest,
      com.github.paohaijiao.proto.JQuickExecuteTaskResponse> getExecuteTaskMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ExecuteTask",
      requestType = com.github.paohaijiao.proto.JQuickExecuteTaskRequest.class,
      responseType = com.github.paohaijiao.proto.JQuickExecuteTaskResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.github.paohaijiao.proto.JQuickExecuteTaskRequest,
      com.github.paohaijiao.proto.JQuickExecuteTaskResponse> getExecuteTaskMethod() {
    io.grpc.MethodDescriptor<com.github.paohaijiao.proto.JQuickExecuteTaskRequest, com.github.paohaijiao.proto.JQuickExecuteTaskResponse> getExecuteTaskMethod;
    if ((getExecuteTaskMethod = JQuickPhysicalPlanServiceGrpc.getExecuteTaskMethod) == null) {
      synchronized (JQuickPhysicalPlanServiceGrpc.class) {
        if ((getExecuteTaskMethod = JQuickPhysicalPlanServiceGrpc.getExecuteTaskMethod) == null) {
          JQuickPhysicalPlanServiceGrpc.getExecuteTaskMethod = getExecuteTaskMethod =
              io.grpc.MethodDescriptor.<com.github.paohaijiao.proto.JQuickExecuteTaskRequest, com.github.paohaijiao.proto.JQuickExecuteTaskResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ExecuteTask"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.github.paohaijiao.proto.JQuickExecuteTaskRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.github.paohaijiao.proto.JQuickExecuteTaskResponse.getDefaultInstance()))
              .setSchemaDescriptor(new JQuickPhysicalPlanServiceMethodDescriptorSupplier("ExecuteTask"))
              .build();
        }
      }
    }
    return getExecuteTaskMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.github.paohaijiao.proto.JQuickExecuteTaskRequest,
      com.github.paohaijiao.proto.JQuickDataChunkProto> getExecuteTaskStreamMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ExecuteTaskStream",
      requestType = com.github.paohaijiao.proto.JQuickExecuteTaskRequest.class,
      responseType = com.github.paohaijiao.proto.JQuickDataChunkProto.class,
      methodType = io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
  public static io.grpc.MethodDescriptor<com.github.paohaijiao.proto.JQuickExecuteTaskRequest,
      com.github.paohaijiao.proto.JQuickDataChunkProto> getExecuteTaskStreamMethod() {
    io.grpc.MethodDescriptor<com.github.paohaijiao.proto.JQuickExecuteTaskRequest, com.github.paohaijiao.proto.JQuickDataChunkProto> getExecuteTaskStreamMethod;
    if ((getExecuteTaskStreamMethod = JQuickPhysicalPlanServiceGrpc.getExecuteTaskStreamMethod) == null) {
      synchronized (JQuickPhysicalPlanServiceGrpc.class) {
        if ((getExecuteTaskStreamMethod = JQuickPhysicalPlanServiceGrpc.getExecuteTaskStreamMethod) == null) {
          JQuickPhysicalPlanServiceGrpc.getExecuteTaskStreamMethod = getExecuteTaskStreamMethod =
              io.grpc.MethodDescriptor.<com.github.paohaijiao.proto.JQuickExecuteTaskRequest, com.github.paohaijiao.proto.JQuickDataChunkProto>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ExecuteTaskStream"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.github.paohaijiao.proto.JQuickExecuteTaskRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.github.paohaijiao.proto.JQuickDataChunkProto.getDefaultInstance()))
              .setSchemaDescriptor(new JQuickPhysicalPlanServiceMethodDescriptorSupplier("ExecuteTaskStream"))
              .build();
        }
      }
    }
    return getExecuteTaskStreamMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.github.paohaijiao.proto.JQuickCancelQueryRequest,
      com.github.paohaijiao.proto.JQuickCancelQueryResponse> getCancelTaskMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "CancelTask",
      requestType = com.github.paohaijiao.proto.JQuickCancelQueryRequest.class,
      responseType = com.github.paohaijiao.proto.JQuickCancelQueryResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.github.paohaijiao.proto.JQuickCancelQueryRequest,
      com.github.paohaijiao.proto.JQuickCancelQueryResponse> getCancelTaskMethod() {
    io.grpc.MethodDescriptor<com.github.paohaijiao.proto.JQuickCancelQueryRequest, com.github.paohaijiao.proto.JQuickCancelQueryResponse> getCancelTaskMethod;
    if ((getCancelTaskMethod = JQuickPhysicalPlanServiceGrpc.getCancelTaskMethod) == null) {
      synchronized (JQuickPhysicalPlanServiceGrpc.class) {
        if ((getCancelTaskMethod = JQuickPhysicalPlanServiceGrpc.getCancelTaskMethod) == null) {
          JQuickPhysicalPlanServiceGrpc.getCancelTaskMethod = getCancelTaskMethod =
              io.grpc.MethodDescriptor.<com.github.paohaijiao.proto.JQuickCancelQueryRequest, com.github.paohaijiao.proto.JQuickCancelQueryResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "CancelTask"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.github.paohaijiao.proto.JQuickCancelQueryRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.github.paohaijiao.proto.JQuickCancelQueryResponse.getDefaultInstance()))
              .setSchemaDescriptor(new JQuickPhysicalPlanServiceMethodDescriptorSupplier("CancelTask"))
              .build();
        }
      }
    }
    return getCancelTaskMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.github.paohaijiao.proto.JQuickHeartbeatRequest,
      com.github.paohaijiao.proto.JQuickHeartbeatResponse> getHeartbeatMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Heartbeat",
      requestType = com.github.paohaijiao.proto.JQuickHeartbeatRequest.class,
      responseType = com.github.paohaijiao.proto.JQuickHeartbeatResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.github.paohaijiao.proto.JQuickHeartbeatRequest,
      com.github.paohaijiao.proto.JQuickHeartbeatResponse> getHeartbeatMethod() {
    io.grpc.MethodDescriptor<com.github.paohaijiao.proto.JQuickHeartbeatRequest, com.github.paohaijiao.proto.JQuickHeartbeatResponse> getHeartbeatMethod;
    if ((getHeartbeatMethod = JQuickPhysicalPlanServiceGrpc.getHeartbeatMethod) == null) {
      synchronized (JQuickPhysicalPlanServiceGrpc.class) {
        if ((getHeartbeatMethod = JQuickPhysicalPlanServiceGrpc.getHeartbeatMethod) == null) {
          JQuickPhysicalPlanServiceGrpc.getHeartbeatMethod = getHeartbeatMethod =
              io.grpc.MethodDescriptor.<com.github.paohaijiao.proto.JQuickHeartbeatRequest, com.github.paohaijiao.proto.JQuickHeartbeatResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Heartbeat"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.github.paohaijiao.proto.JQuickHeartbeatRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.github.paohaijiao.proto.JQuickHeartbeatResponse.getDefaultInstance()))
              .setSchemaDescriptor(new JQuickPhysicalPlanServiceMethodDescriptorSupplier("Heartbeat"))
              .build();
        }
      }
    }
    return getHeartbeatMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.github.paohaijiao.proto.JQuickRegisterWorkerRequest,
      com.github.paohaijiao.proto.JQuickRegisterWorkerResponse> getRegisterWorkerMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "RegisterWorker",
      requestType = com.github.paohaijiao.proto.JQuickRegisterWorkerRequest.class,
      responseType = com.github.paohaijiao.proto.JQuickRegisterWorkerResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.github.paohaijiao.proto.JQuickRegisterWorkerRequest,
      com.github.paohaijiao.proto.JQuickRegisterWorkerResponse> getRegisterWorkerMethod() {
    io.grpc.MethodDescriptor<com.github.paohaijiao.proto.JQuickRegisterWorkerRequest, com.github.paohaijiao.proto.JQuickRegisterWorkerResponse> getRegisterWorkerMethod;
    if ((getRegisterWorkerMethod = JQuickPhysicalPlanServiceGrpc.getRegisterWorkerMethod) == null) {
      synchronized (JQuickPhysicalPlanServiceGrpc.class) {
        if ((getRegisterWorkerMethod = JQuickPhysicalPlanServiceGrpc.getRegisterWorkerMethod) == null) {
          JQuickPhysicalPlanServiceGrpc.getRegisterWorkerMethod = getRegisterWorkerMethod =
              io.grpc.MethodDescriptor.<com.github.paohaijiao.proto.JQuickRegisterWorkerRequest, com.github.paohaijiao.proto.JQuickRegisterWorkerResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "RegisterWorker"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.github.paohaijiao.proto.JQuickRegisterWorkerRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.github.paohaijiao.proto.JQuickRegisterWorkerResponse.getDefaultInstance()))
              .setSchemaDescriptor(new JQuickPhysicalPlanServiceMethodDescriptorSupplier("RegisterWorker"))
              .build();
        }
      }
    }
    return getRegisterWorkerMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.github.paohaijiao.proto.JQuickUpdateWorkerEndpointsRequest,
      com.github.paohaijiao.proto.JQuickUpdateWorkerEndpointsResponse> getUpdateWorkerEndpointsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "UpdateWorkerEndpoints",
      requestType = com.github.paohaijiao.proto.JQuickUpdateWorkerEndpointsRequest.class,
      responseType = com.github.paohaijiao.proto.JQuickUpdateWorkerEndpointsResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.github.paohaijiao.proto.JQuickUpdateWorkerEndpointsRequest,
      com.github.paohaijiao.proto.JQuickUpdateWorkerEndpointsResponse> getUpdateWorkerEndpointsMethod() {
    io.grpc.MethodDescriptor<com.github.paohaijiao.proto.JQuickUpdateWorkerEndpointsRequest, com.github.paohaijiao.proto.JQuickUpdateWorkerEndpointsResponse> getUpdateWorkerEndpointsMethod;
    if ((getUpdateWorkerEndpointsMethod = JQuickPhysicalPlanServiceGrpc.getUpdateWorkerEndpointsMethod) == null) {
      synchronized (JQuickPhysicalPlanServiceGrpc.class) {
        if ((getUpdateWorkerEndpointsMethod = JQuickPhysicalPlanServiceGrpc.getUpdateWorkerEndpointsMethod) == null) {
          JQuickPhysicalPlanServiceGrpc.getUpdateWorkerEndpointsMethod = getUpdateWorkerEndpointsMethod =
              io.grpc.MethodDescriptor.<com.github.paohaijiao.proto.JQuickUpdateWorkerEndpointsRequest, com.github.paohaijiao.proto.JQuickUpdateWorkerEndpointsResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "UpdateWorkerEndpoints"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.github.paohaijiao.proto.JQuickUpdateWorkerEndpointsRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.github.paohaijiao.proto.JQuickUpdateWorkerEndpointsResponse.getDefaultInstance()))
              .setSchemaDescriptor(new JQuickPhysicalPlanServiceMethodDescriptorSupplier("UpdateWorkerEndpoints"))
              .build();
        }
      }
    }
    return getUpdateWorkerEndpointsMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.github.paohaijiao.proto.JQuickGetWorkerEndpointsRequest,
      com.github.paohaijiao.proto.JQuickGetWorkerEndpointsResponse> getGetWorkerEndpointsMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetWorkerEndpoints",
      requestType = com.github.paohaijiao.proto.JQuickGetWorkerEndpointsRequest.class,
      responseType = com.github.paohaijiao.proto.JQuickGetWorkerEndpointsResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.github.paohaijiao.proto.JQuickGetWorkerEndpointsRequest,
      com.github.paohaijiao.proto.JQuickGetWorkerEndpointsResponse> getGetWorkerEndpointsMethod() {
    io.grpc.MethodDescriptor<com.github.paohaijiao.proto.JQuickGetWorkerEndpointsRequest, com.github.paohaijiao.proto.JQuickGetWorkerEndpointsResponse> getGetWorkerEndpointsMethod;
    if ((getGetWorkerEndpointsMethod = JQuickPhysicalPlanServiceGrpc.getGetWorkerEndpointsMethod) == null) {
      synchronized (JQuickPhysicalPlanServiceGrpc.class) {
        if ((getGetWorkerEndpointsMethod = JQuickPhysicalPlanServiceGrpc.getGetWorkerEndpointsMethod) == null) {
          JQuickPhysicalPlanServiceGrpc.getGetWorkerEndpointsMethod = getGetWorkerEndpointsMethod =
              io.grpc.MethodDescriptor.<com.github.paohaijiao.proto.JQuickGetWorkerEndpointsRequest, com.github.paohaijiao.proto.JQuickGetWorkerEndpointsResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetWorkerEndpoints"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.github.paohaijiao.proto.JQuickGetWorkerEndpointsRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.github.paohaijiao.proto.JQuickGetWorkerEndpointsResponse.getDefaultInstance()))
              .setSchemaDescriptor(new JQuickPhysicalPlanServiceMethodDescriptorSupplier("GetWorkerEndpoints"))
              .build();
        }
      }
    }
    return getGetWorkerEndpointsMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.github.paohaijiao.proto.JQuickWorkerLeaveRequest,
      com.github.paohaijiao.proto.JQuickWorkerLeaveResponse> getWorkerLeaveMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "WorkerLeave",
      requestType = com.github.paohaijiao.proto.JQuickWorkerLeaveRequest.class,
      responseType = com.github.paohaijiao.proto.JQuickWorkerLeaveResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<com.github.paohaijiao.proto.JQuickWorkerLeaveRequest,
      com.github.paohaijiao.proto.JQuickWorkerLeaveResponse> getWorkerLeaveMethod() {
    io.grpc.MethodDescriptor<com.github.paohaijiao.proto.JQuickWorkerLeaveRequest, com.github.paohaijiao.proto.JQuickWorkerLeaveResponse> getWorkerLeaveMethod;
    if ((getWorkerLeaveMethod = JQuickPhysicalPlanServiceGrpc.getWorkerLeaveMethod) == null) {
      synchronized (JQuickPhysicalPlanServiceGrpc.class) {
        if ((getWorkerLeaveMethod = JQuickPhysicalPlanServiceGrpc.getWorkerLeaveMethod) == null) {
          JQuickPhysicalPlanServiceGrpc.getWorkerLeaveMethod = getWorkerLeaveMethod =
              io.grpc.MethodDescriptor.<com.github.paohaijiao.proto.JQuickWorkerLeaveRequest, com.github.paohaijiao.proto.JQuickWorkerLeaveResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "WorkerLeave"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.github.paohaijiao.proto.JQuickWorkerLeaveRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.github.paohaijiao.proto.JQuickWorkerLeaveResponse.getDefaultInstance()))
              .setSchemaDescriptor(new JQuickPhysicalPlanServiceMethodDescriptorSupplier("WorkerLeave"))
              .build();
        }
      }
    }
    return getWorkerLeaveMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static JQuickPhysicalPlanServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<JQuickPhysicalPlanServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<JQuickPhysicalPlanServiceStub>() {
        @java.lang.Override
        public JQuickPhysicalPlanServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new JQuickPhysicalPlanServiceStub(channel, callOptions);
        }
      };
    return JQuickPhysicalPlanServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports all types of calls on the service
   */
  public static JQuickPhysicalPlanServiceBlockingV2Stub newBlockingV2Stub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<JQuickPhysicalPlanServiceBlockingV2Stub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<JQuickPhysicalPlanServiceBlockingV2Stub>() {
        @java.lang.Override
        public JQuickPhysicalPlanServiceBlockingV2Stub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new JQuickPhysicalPlanServiceBlockingV2Stub(channel, callOptions);
        }
      };
    return JQuickPhysicalPlanServiceBlockingV2Stub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static JQuickPhysicalPlanServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<JQuickPhysicalPlanServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<JQuickPhysicalPlanServiceBlockingStub>() {
        @java.lang.Override
        public JQuickPhysicalPlanServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new JQuickPhysicalPlanServiceBlockingStub(channel, callOptions);
        }
      };
    return JQuickPhysicalPlanServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static JQuickPhysicalPlanServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<JQuickPhysicalPlanServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<JQuickPhysicalPlanServiceFutureStub>() {
        @java.lang.Override
        public JQuickPhysicalPlanServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new JQuickPhysicalPlanServiceFutureStub(channel, callOptions);
        }
      };
    return JQuickPhysicalPlanServiceFutureStub.newStub(factory, channel);
  }

  /**
   * <pre>
   * 物理计划服务（Worker 端）
   * </pre>
   */
  public interface AsyncService {

    /**
     */
    default void executeTask(com.github.paohaijiao.proto.JQuickExecuteTaskRequest request,
        io.grpc.stub.StreamObserver<com.github.paohaijiao.proto.JQuickExecuteTaskResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getExecuteTaskMethod(), responseObserver);
    }

    /**
     */
    default void executeTaskStream(com.github.paohaijiao.proto.JQuickExecuteTaskRequest request,
        io.grpc.stub.StreamObserver<com.github.paohaijiao.proto.JQuickDataChunkProto> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getExecuteTaskStreamMethod(), responseObserver);
    }

    /**
     */
    default void cancelTask(com.github.paohaijiao.proto.JQuickCancelQueryRequest request,
        io.grpc.stub.StreamObserver<com.github.paohaijiao.proto.JQuickCancelQueryResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getCancelTaskMethod(), responseObserver);
    }

    /**
     */
    default void heartbeat(com.github.paohaijiao.proto.JQuickHeartbeatRequest request,
        io.grpc.stub.StreamObserver<com.github.paohaijiao.proto.JQuickHeartbeatResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getHeartbeatMethod(), responseObserver);
    }

    /**
     */
    default void registerWorker(com.github.paohaijiao.proto.JQuickRegisterWorkerRequest request,
        io.grpc.stub.StreamObserver<com.github.paohaijiao.proto.JQuickRegisterWorkerResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getRegisterWorkerMethod(), responseObserver);
    }

    /**
     */
    default void updateWorkerEndpoints(com.github.paohaijiao.proto.JQuickUpdateWorkerEndpointsRequest request,
        io.grpc.stub.StreamObserver<com.github.paohaijiao.proto.JQuickUpdateWorkerEndpointsResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getUpdateWorkerEndpointsMethod(), responseObserver);
    }

    /**
     */
    default void getWorkerEndpoints(com.github.paohaijiao.proto.JQuickGetWorkerEndpointsRequest request,
        io.grpc.stub.StreamObserver<com.github.paohaijiao.proto.JQuickGetWorkerEndpointsResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetWorkerEndpointsMethod(), responseObserver);
    }

    /**
     */
    default void workerLeave(com.github.paohaijiao.proto.JQuickWorkerLeaveRequest request,
        io.grpc.stub.StreamObserver<com.github.paohaijiao.proto.JQuickWorkerLeaveResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getWorkerLeaveMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service JQuickPhysicalPlanService.
   * <pre>
   * 物理计划服务（Worker 端）
   * </pre>
   */
  public static abstract class JQuickPhysicalPlanServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return JQuickPhysicalPlanServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service JQuickPhysicalPlanService.
   * <pre>
   * 物理计划服务（Worker 端）
   * </pre>
   */
  public static final class JQuickPhysicalPlanServiceStub
      extends io.grpc.stub.AbstractAsyncStub<JQuickPhysicalPlanServiceStub> {
    private JQuickPhysicalPlanServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected JQuickPhysicalPlanServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new JQuickPhysicalPlanServiceStub(channel, callOptions);
    }

    /**
     */
    public void executeTask(com.github.paohaijiao.proto.JQuickExecuteTaskRequest request,
        io.grpc.stub.StreamObserver<com.github.paohaijiao.proto.JQuickExecuteTaskResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getExecuteTaskMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void executeTaskStream(com.github.paohaijiao.proto.JQuickExecuteTaskRequest request,
        io.grpc.stub.StreamObserver<com.github.paohaijiao.proto.JQuickDataChunkProto> responseObserver) {
      io.grpc.stub.ClientCalls.asyncServerStreamingCall(
          getChannel().newCall(getExecuteTaskStreamMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void cancelTask(com.github.paohaijiao.proto.JQuickCancelQueryRequest request,
        io.grpc.stub.StreamObserver<com.github.paohaijiao.proto.JQuickCancelQueryResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getCancelTaskMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void heartbeat(com.github.paohaijiao.proto.JQuickHeartbeatRequest request,
        io.grpc.stub.StreamObserver<com.github.paohaijiao.proto.JQuickHeartbeatResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getHeartbeatMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void registerWorker(com.github.paohaijiao.proto.JQuickRegisterWorkerRequest request,
        io.grpc.stub.StreamObserver<com.github.paohaijiao.proto.JQuickRegisterWorkerResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getRegisterWorkerMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void updateWorkerEndpoints(com.github.paohaijiao.proto.JQuickUpdateWorkerEndpointsRequest request,
        io.grpc.stub.StreamObserver<com.github.paohaijiao.proto.JQuickUpdateWorkerEndpointsResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getUpdateWorkerEndpointsMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getWorkerEndpoints(com.github.paohaijiao.proto.JQuickGetWorkerEndpointsRequest request,
        io.grpc.stub.StreamObserver<com.github.paohaijiao.proto.JQuickGetWorkerEndpointsResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetWorkerEndpointsMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void workerLeave(com.github.paohaijiao.proto.JQuickWorkerLeaveRequest request,
        io.grpc.stub.StreamObserver<com.github.paohaijiao.proto.JQuickWorkerLeaveResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getWorkerLeaveMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service JQuickPhysicalPlanService.
   * <pre>
   * 物理计划服务（Worker 端）
   * </pre>
   */
  public static final class JQuickPhysicalPlanServiceBlockingV2Stub
      extends io.grpc.stub.AbstractBlockingStub<JQuickPhysicalPlanServiceBlockingV2Stub> {
    private JQuickPhysicalPlanServiceBlockingV2Stub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected JQuickPhysicalPlanServiceBlockingV2Stub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new JQuickPhysicalPlanServiceBlockingV2Stub(channel, callOptions);
    }

    /**
     */
    public com.github.paohaijiao.proto.JQuickExecuteTaskResponse executeTask(com.github.paohaijiao.proto.JQuickExecuteTaskRequest request) throws io.grpc.StatusException {
      return io.grpc.stub.ClientCalls.blockingV2UnaryCall(
          getChannel(), getExecuteTaskMethod(), getCallOptions(), request);
    }

    /**
     */
    @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/10918")
    public io.grpc.stub.BlockingClientCall<?, com.github.paohaijiao.proto.JQuickDataChunkProto>
        executeTaskStream(com.github.paohaijiao.proto.JQuickExecuteTaskRequest request) {
      return io.grpc.stub.ClientCalls.blockingV2ServerStreamingCall(
          getChannel(), getExecuteTaskStreamMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.github.paohaijiao.proto.JQuickCancelQueryResponse cancelTask(com.github.paohaijiao.proto.JQuickCancelQueryRequest request) throws io.grpc.StatusException {
      return io.grpc.stub.ClientCalls.blockingV2UnaryCall(
          getChannel(), getCancelTaskMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.github.paohaijiao.proto.JQuickHeartbeatResponse heartbeat(com.github.paohaijiao.proto.JQuickHeartbeatRequest request) throws io.grpc.StatusException {
      return io.grpc.stub.ClientCalls.blockingV2UnaryCall(
          getChannel(), getHeartbeatMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.github.paohaijiao.proto.JQuickRegisterWorkerResponse registerWorker(com.github.paohaijiao.proto.JQuickRegisterWorkerRequest request) throws io.grpc.StatusException {
      return io.grpc.stub.ClientCalls.blockingV2UnaryCall(
          getChannel(), getRegisterWorkerMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.github.paohaijiao.proto.JQuickUpdateWorkerEndpointsResponse updateWorkerEndpoints(com.github.paohaijiao.proto.JQuickUpdateWorkerEndpointsRequest request) throws io.grpc.StatusException {
      return io.grpc.stub.ClientCalls.blockingV2UnaryCall(
          getChannel(), getUpdateWorkerEndpointsMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.github.paohaijiao.proto.JQuickGetWorkerEndpointsResponse getWorkerEndpoints(com.github.paohaijiao.proto.JQuickGetWorkerEndpointsRequest request) throws io.grpc.StatusException {
      return io.grpc.stub.ClientCalls.blockingV2UnaryCall(
          getChannel(), getGetWorkerEndpointsMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.github.paohaijiao.proto.JQuickWorkerLeaveResponse workerLeave(com.github.paohaijiao.proto.JQuickWorkerLeaveRequest request) throws io.grpc.StatusException {
      return io.grpc.stub.ClientCalls.blockingV2UnaryCall(
          getChannel(), getWorkerLeaveMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do limited synchronous rpc calls to service JQuickPhysicalPlanService.
   * <pre>
   * 物理计划服务（Worker 端）
   * </pre>
   */
  public static final class JQuickPhysicalPlanServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<JQuickPhysicalPlanServiceBlockingStub> {
    private JQuickPhysicalPlanServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected JQuickPhysicalPlanServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new JQuickPhysicalPlanServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public com.github.paohaijiao.proto.JQuickExecuteTaskResponse executeTask(com.github.paohaijiao.proto.JQuickExecuteTaskRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getExecuteTaskMethod(), getCallOptions(), request);
    }

    /**
     */
    public java.util.Iterator<com.github.paohaijiao.proto.JQuickDataChunkProto> executeTaskStream(
        com.github.paohaijiao.proto.JQuickExecuteTaskRequest request) {
      return io.grpc.stub.ClientCalls.blockingServerStreamingCall(
          getChannel(), getExecuteTaskStreamMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.github.paohaijiao.proto.JQuickCancelQueryResponse cancelTask(com.github.paohaijiao.proto.JQuickCancelQueryRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getCancelTaskMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.github.paohaijiao.proto.JQuickHeartbeatResponse heartbeat(com.github.paohaijiao.proto.JQuickHeartbeatRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getHeartbeatMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.github.paohaijiao.proto.JQuickRegisterWorkerResponse registerWorker(com.github.paohaijiao.proto.JQuickRegisterWorkerRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getRegisterWorkerMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.github.paohaijiao.proto.JQuickUpdateWorkerEndpointsResponse updateWorkerEndpoints(com.github.paohaijiao.proto.JQuickUpdateWorkerEndpointsRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getUpdateWorkerEndpointsMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.github.paohaijiao.proto.JQuickGetWorkerEndpointsResponse getWorkerEndpoints(com.github.paohaijiao.proto.JQuickGetWorkerEndpointsRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetWorkerEndpointsMethod(), getCallOptions(), request);
    }

    /**
     */
    public com.github.paohaijiao.proto.JQuickWorkerLeaveResponse workerLeave(com.github.paohaijiao.proto.JQuickWorkerLeaveRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getWorkerLeaveMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service JQuickPhysicalPlanService.
   * <pre>
   * 物理计划服务（Worker 端）
   * </pre>
   */
  public static final class JQuickPhysicalPlanServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<JQuickPhysicalPlanServiceFutureStub> {
    private JQuickPhysicalPlanServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected JQuickPhysicalPlanServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new JQuickPhysicalPlanServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.github.paohaijiao.proto.JQuickExecuteTaskResponse> executeTask(
        com.github.paohaijiao.proto.JQuickExecuteTaskRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getExecuteTaskMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.github.paohaijiao.proto.JQuickCancelQueryResponse> cancelTask(
        com.github.paohaijiao.proto.JQuickCancelQueryRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getCancelTaskMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.github.paohaijiao.proto.JQuickHeartbeatResponse> heartbeat(
        com.github.paohaijiao.proto.JQuickHeartbeatRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getHeartbeatMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.github.paohaijiao.proto.JQuickRegisterWorkerResponse> registerWorker(
        com.github.paohaijiao.proto.JQuickRegisterWorkerRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getRegisterWorkerMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.github.paohaijiao.proto.JQuickUpdateWorkerEndpointsResponse> updateWorkerEndpoints(
        com.github.paohaijiao.proto.JQuickUpdateWorkerEndpointsRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getUpdateWorkerEndpointsMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.github.paohaijiao.proto.JQuickGetWorkerEndpointsResponse> getWorkerEndpoints(
        com.github.paohaijiao.proto.JQuickGetWorkerEndpointsRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetWorkerEndpointsMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.github.paohaijiao.proto.JQuickWorkerLeaveResponse> workerLeave(
        com.github.paohaijiao.proto.JQuickWorkerLeaveRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getWorkerLeaveMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_EXECUTE_TASK = 0;
  private static final int METHODID_EXECUTE_TASK_STREAM = 1;
  private static final int METHODID_CANCEL_TASK = 2;
  private static final int METHODID_HEARTBEAT = 3;
  private static final int METHODID_REGISTER_WORKER = 4;
  private static final int METHODID_UPDATE_WORKER_ENDPOINTS = 5;
  private static final int METHODID_GET_WORKER_ENDPOINTS = 6;
  private static final int METHODID_WORKER_LEAVE = 7;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_EXECUTE_TASK:
          serviceImpl.executeTask((com.github.paohaijiao.proto.JQuickExecuteTaskRequest) request,
              (io.grpc.stub.StreamObserver<com.github.paohaijiao.proto.JQuickExecuteTaskResponse>) responseObserver);
          break;
        case METHODID_EXECUTE_TASK_STREAM:
          serviceImpl.executeTaskStream((com.github.paohaijiao.proto.JQuickExecuteTaskRequest) request,
              (io.grpc.stub.StreamObserver<com.github.paohaijiao.proto.JQuickDataChunkProto>) responseObserver);
          break;
        case METHODID_CANCEL_TASK:
          serviceImpl.cancelTask((com.github.paohaijiao.proto.JQuickCancelQueryRequest) request,
              (io.grpc.stub.StreamObserver<com.github.paohaijiao.proto.JQuickCancelQueryResponse>) responseObserver);
          break;
        case METHODID_HEARTBEAT:
          serviceImpl.heartbeat((com.github.paohaijiao.proto.JQuickHeartbeatRequest) request,
              (io.grpc.stub.StreamObserver<com.github.paohaijiao.proto.JQuickHeartbeatResponse>) responseObserver);
          break;
        case METHODID_REGISTER_WORKER:
          serviceImpl.registerWorker((com.github.paohaijiao.proto.JQuickRegisterWorkerRequest) request,
              (io.grpc.stub.StreamObserver<com.github.paohaijiao.proto.JQuickRegisterWorkerResponse>) responseObserver);
          break;
        case METHODID_UPDATE_WORKER_ENDPOINTS:
          serviceImpl.updateWorkerEndpoints((com.github.paohaijiao.proto.JQuickUpdateWorkerEndpointsRequest) request,
              (io.grpc.stub.StreamObserver<com.github.paohaijiao.proto.JQuickUpdateWorkerEndpointsResponse>) responseObserver);
          break;
        case METHODID_GET_WORKER_ENDPOINTS:
          serviceImpl.getWorkerEndpoints((com.github.paohaijiao.proto.JQuickGetWorkerEndpointsRequest) request,
              (io.grpc.stub.StreamObserver<com.github.paohaijiao.proto.JQuickGetWorkerEndpointsResponse>) responseObserver);
          break;
        case METHODID_WORKER_LEAVE:
          serviceImpl.workerLeave((com.github.paohaijiao.proto.JQuickWorkerLeaveRequest) request,
              (io.grpc.stub.StreamObserver<com.github.paohaijiao.proto.JQuickWorkerLeaveResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getExecuteTaskMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.github.paohaijiao.proto.JQuickExecuteTaskRequest,
              com.github.paohaijiao.proto.JQuickExecuteTaskResponse>(
                service, METHODID_EXECUTE_TASK)))
        .addMethod(
          getExecuteTaskStreamMethod(),
          io.grpc.stub.ServerCalls.asyncServerStreamingCall(
            new MethodHandlers<
              com.github.paohaijiao.proto.JQuickExecuteTaskRequest,
              com.github.paohaijiao.proto.JQuickDataChunkProto>(
                service, METHODID_EXECUTE_TASK_STREAM)))
        .addMethod(
          getCancelTaskMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.github.paohaijiao.proto.JQuickCancelQueryRequest,
              com.github.paohaijiao.proto.JQuickCancelQueryResponse>(
                service, METHODID_CANCEL_TASK)))
        .addMethod(
          getHeartbeatMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.github.paohaijiao.proto.JQuickHeartbeatRequest,
              com.github.paohaijiao.proto.JQuickHeartbeatResponse>(
                service, METHODID_HEARTBEAT)))
        .addMethod(
          getRegisterWorkerMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.github.paohaijiao.proto.JQuickRegisterWorkerRequest,
              com.github.paohaijiao.proto.JQuickRegisterWorkerResponse>(
                service, METHODID_REGISTER_WORKER)))
        .addMethod(
          getUpdateWorkerEndpointsMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.github.paohaijiao.proto.JQuickUpdateWorkerEndpointsRequest,
              com.github.paohaijiao.proto.JQuickUpdateWorkerEndpointsResponse>(
                service, METHODID_UPDATE_WORKER_ENDPOINTS)))
        .addMethod(
          getGetWorkerEndpointsMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.github.paohaijiao.proto.JQuickGetWorkerEndpointsRequest,
              com.github.paohaijiao.proto.JQuickGetWorkerEndpointsResponse>(
                service, METHODID_GET_WORKER_ENDPOINTS)))
        .addMethod(
          getWorkerLeaveMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              com.github.paohaijiao.proto.JQuickWorkerLeaveRequest,
              com.github.paohaijiao.proto.JQuickWorkerLeaveResponse>(
                service, METHODID_WORKER_LEAVE)))
        .build();
  }

  private static abstract class JQuickPhysicalPlanServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    JQuickPhysicalPlanServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.github.paohaijiao.proto.JQuickPhysicalPlanProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("JQuickPhysicalPlanService");
    }
  }

  private static final class JQuickPhysicalPlanServiceFileDescriptorSupplier
      extends JQuickPhysicalPlanServiceBaseDescriptorSupplier {
    JQuickPhysicalPlanServiceFileDescriptorSupplier() {}
  }

  private static final class JQuickPhysicalPlanServiceMethodDescriptorSupplier
      extends JQuickPhysicalPlanServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    JQuickPhysicalPlanServiceMethodDescriptorSupplier(java.lang.String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (JQuickPhysicalPlanServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new JQuickPhysicalPlanServiceFileDescriptorSupplier())
              .addMethod(getExecuteTaskMethod())
              .addMethod(getExecuteTaskStreamMethod())
              .addMethod(getCancelTaskMethod())
              .addMethod(getHeartbeatMethod())
              .addMethod(getRegisterWorkerMethod())
              .addMethod(getUpdateWorkerEndpointsMethod())
              .addMethod(getGetWorkerEndpointsMethod())
              .addMethod(getWorkerLeaveMethod())
              .build();
        }
      }
    }
    return result;
  }
}
