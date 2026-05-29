package com.github.paohaijiao.proto;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@io.grpc.stub.annotations.GrpcGenerated
public final class JQuickPhysicalPlanServiceGrpc {

  private JQuickPhysicalPlanServiceGrpc() {}

  public static final String SERVICE_NAME = "com.github.paohaijiao.proto.JQuickPhysicalPlanService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<GetPlanRequest,
      GetPlanResponse> getGetDistributedPlanMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetDistributedPlan",
      requestType = GetPlanRequest.class,
      responseType = GetPlanResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<GetPlanRequest,
      GetPlanResponse> getGetDistributedPlanMethod() {
    io.grpc.MethodDescriptor<GetPlanRequest, GetPlanResponse> getGetDistributedPlanMethod;
    if ((getGetDistributedPlanMethod = JQuickPhysicalPlanServiceGrpc.getGetDistributedPlanMethod) == null) {
      synchronized (JQuickPhysicalPlanServiceGrpc.class) {
        if ((getGetDistributedPlanMethod = JQuickPhysicalPlanServiceGrpc.getGetDistributedPlanMethod) == null) {
          JQuickPhysicalPlanServiceGrpc.getGetDistributedPlanMethod = getGetDistributedPlanMethod =
              io.grpc.MethodDescriptor.<GetPlanRequest, GetPlanResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetDistributedPlan"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  GetPlanRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  GetPlanResponse.getDefaultInstance()))
              .setSchemaDescriptor(new JQuickPhysicalPlanServiceMethodDescriptorSupplier("GetDistributedPlan"))
              .build();
        }
      }
    }
    return getGetDistributedPlanMethod;
  }

  private static volatile io.grpc.MethodDescriptor<ExecuteTaskRequest,
      ExecuteTaskResponse> getExecuteTaskMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ExecuteTask",
      requestType = ExecuteTaskRequest.class,
      responseType = ExecuteTaskResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<ExecuteTaskRequest,
      ExecuteTaskResponse> getExecuteTaskMethod() {
    io.grpc.MethodDescriptor<ExecuteTaskRequest, ExecuteTaskResponse> getExecuteTaskMethod;
    if ((getExecuteTaskMethod = JQuickPhysicalPlanServiceGrpc.getExecuteTaskMethod) == null) {
      synchronized (JQuickPhysicalPlanServiceGrpc.class) {
        if ((getExecuteTaskMethod = JQuickPhysicalPlanServiceGrpc.getExecuteTaskMethod) == null) {
          JQuickPhysicalPlanServiceGrpc.getExecuteTaskMethod = getExecuteTaskMethod =
              io.grpc.MethodDescriptor.<ExecuteTaskRequest, ExecuteTaskResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ExecuteTask"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ExecuteTaskRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ExecuteTaskResponse.getDefaultInstance()))
              .setSchemaDescriptor(new JQuickPhysicalPlanServiceMethodDescriptorSupplier("ExecuteTask"))
              .build();
        }
      }
    }
    return getExecuteTaskMethod;
  }

  private static volatile io.grpc.MethodDescriptor<ExecuteTaskRequest,
      DataChunk> getExecuteTaskStreamMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ExecuteTaskStream",
      requestType = ExecuteTaskRequest.class,
      responseType = DataChunk.class,
      methodType = io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
  public static io.grpc.MethodDescriptor<ExecuteTaskRequest,
      DataChunk> getExecuteTaskStreamMethod() {
    io.grpc.MethodDescriptor<ExecuteTaskRequest, DataChunk> getExecuteTaskStreamMethod;
    if ((getExecuteTaskStreamMethod = JQuickPhysicalPlanServiceGrpc.getExecuteTaskStreamMethod) == null) {
      synchronized (JQuickPhysicalPlanServiceGrpc.class) {
        if ((getExecuteTaskStreamMethod = JQuickPhysicalPlanServiceGrpc.getExecuteTaskStreamMethod) == null) {
          JQuickPhysicalPlanServiceGrpc.getExecuteTaskStreamMethod = getExecuteTaskStreamMethod =
              io.grpc.MethodDescriptor.<ExecuteTaskRequest, DataChunk>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ExecuteTaskStream"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  ExecuteTaskRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  DataChunk.getDefaultInstance()))
              .setSchemaDescriptor(new JQuickPhysicalPlanServiceMethodDescriptorSupplier("ExecuteTaskStream"))
              .build();
        }
      }
    }
    return getExecuteTaskStreamMethod;
  }

  private static volatile io.grpc.MethodDescriptor<CancelTaskRequest,
      CancelTaskResponse> getCancelTaskMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "CancelTask",
      requestType = CancelTaskRequest.class,
      responseType = CancelTaskResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<CancelTaskRequest,
      CancelTaskResponse> getCancelTaskMethod() {
    io.grpc.MethodDescriptor<CancelTaskRequest, CancelTaskResponse> getCancelTaskMethod;
    if ((getCancelTaskMethod = JQuickPhysicalPlanServiceGrpc.getCancelTaskMethod) == null) {
      synchronized (JQuickPhysicalPlanServiceGrpc.class) {
        if ((getCancelTaskMethod = JQuickPhysicalPlanServiceGrpc.getCancelTaskMethod) == null) {
          JQuickPhysicalPlanServiceGrpc.getCancelTaskMethod = getCancelTaskMethod =
              io.grpc.MethodDescriptor.<CancelTaskRequest, CancelTaskResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "CancelTask"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  CancelTaskRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  CancelTaskResponse.getDefaultInstance()))
              .setSchemaDescriptor(new JQuickPhysicalPlanServiceMethodDescriptorSupplier("CancelTask"))
              .build();
        }
      }
    }
    return getCancelTaskMethod;
  }

  private static volatile io.grpc.MethodDescriptor<HeartbeatRequest,
      HeartbeatResponse> getHeartbeatMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Heartbeat",
      requestType = HeartbeatRequest.class,
      responseType = HeartbeatResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<HeartbeatRequest,
      HeartbeatResponse> getHeartbeatMethod() {
    io.grpc.MethodDescriptor<HeartbeatRequest, HeartbeatResponse> getHeartbeatMethod;
    if ((getHeartbeatMethod = JQuickPhysicalPlanServiceGrpc.getHeartbeatMethod) == null) {
      synchronized (JQuickPhysicalPlanServiceGrpc.class) {
        if ((getHeartbeatMethod = JQuickPhysicalPlanServiceGrpc.getHeartbeatMethod) == null) {
          JQuickPhysicalPlanServiceGrpc.getHeartbeatMethod = getHeartbeatMethod =
              io.grpc.MethodDescriptor.<HeartbeatRequest, HeartbeatResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Heartbeat"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  HeartbeatRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  HeartbeatResponse.getDefaultInstance()))
              .setSchemaDescriptor(new JQuickPhysicalPlanServiceMethodDescriptorSupplier("Heartbeat"))
              .build();
        }
      }
    }
    return getHeartbeatMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static JQuickPhysicalPlanServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<JQuickPhysicalPlanServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<JQuickPhysicalPlanServiceStub>() {
        @Override
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
        @Override
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
        @Override
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
        @Override
        public JQuickPhysicalPlanServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new JQuickPhysicalPlanServiceFutureStub(channel, callOptions);
        }
      };
    return JQuickPhysicalPlanServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     * <pre>
     * 获取分布式执行计划
     * </pre>
     */
    default void getDistributedPlan(GetPlanRequest request,
                                    io.grpc.stub.StreamObserver<GetPlanResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetDistributedPlanMethod(), responseObserver);
    }

    /**
     * <pre>
     * 执行单个任务
     * </pre>
     */
    default void executeTask(ExecuteTaskRequest request,
                             io.grpc.stub.StreamObserver<ExecuteTaskResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getExecuteTaskMethod(), responseObserver);
    }

    /**
     * <pre>
     * 流式执行任务（返回数据流）
     * </pre>
     */
    default void executeTaskStream(ExecuteTaskRequest request,
                                   io.grpc.stub.StreamObserver<DataChunk> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getExecuteTaskStreamMethod(), responseObserver);
    }

    /**
     * <pre>
     * 取消任务
     * </pre>
     */
    default void cancelTask(CancelTaskRequest request,
                            io.grpc.stub.StreamObserver<CancelTaskResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getCancelTaskMethod(), responseObserver);
    }

    /**
     * <pre>
     * 心跳检测
     * </pre>
     */
    default void heartbeat(HeartbeatRequest request,
                           io.grpc.stub.StreamObserver<HeartbeatResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getHeartbeatMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service JQuickPhysicalPlanService.
   */
  public static abstract class JQuickPhysicalPlanServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @Override public final io.grpc.ServerServiceDefinition bindService() {
      return JQuickPhysicalPlanServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service JQuickPhysicalPlanService.
   */
  public static final class JQuickPhysicalPlanServiceStub
      extends io.grpc.stub.AbstractAsyncStub<JQuickPhysicalPlanServiceStub> {
    private JQuickPhysicalPlanServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected JQuickPhysicalPlanServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new JQuickPhysicalPlanServiceStub(channel, callOptions);
    }

    /**
     * <pre>
     * 获取分布式执行计划
     * </pre>
     */
    public void getDistributedPlan(GetPlanRequest request,
                                   io.grpc.stub.StreamObserver<GetPlanResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetDistributedPlanMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * 执行单个任务
     * </pre>
     */
    public void executeTask(ExecuteTaskRequest request,
                            io.grpc.stub.StreamObserver<ExecuteTaskResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getExecuteTaskMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * 流式执行任务（返回数据流）
     * </pre>
     */
    public void executeTaskStream(ExecuteTaskRequest request,
                                  io.grpc.stub.StreamObserver<DataChunk> responseObserver) {
      io.grpc.stub.ClientCalls.asyncServerStreamingCall(
          getChannel().newCall(getExecuteTaskStreamMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * 取消任务
     * </pre>
     */
    public void cancelTask(CancelTaskRequest request,
                           io.grpc.stub.StreamObserver<CancelTaskResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getCancelTaskMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * 心跳检测
     * </pre>
     */
    public void heartbeat(HeartbeatRequest request,
                          io.grpc.stub.StreamObserver<HeartbeatResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getHeartbeatMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service JQuickPhysicalPlanService.
   */
  public static final class JQuickPhysicalPlanServiceBlockingV2Stub
      extends io.grpc.stub.AbstractBlockingStub<JQuickPhysicalPlanServiceBlockingV2Stub> {
    private JQuickPhysicalPlanServiceBlockingV2Stub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected JQuickPhysicalPlanServiceBlockingV2Stub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new JQuickPhysicalPlanServiceBlockingV2Stub(channel, callOptions);
    }

    /**
     * <pre>
     * 获取分布式执行计划
     * </pre>
     */
    public GetPlanResponse getDistributedPlan(GetPlanRequest request) throws io.grpc.StatusException {
      return io.grpc.stub.ClientCalls.blockingV2UnaryCall(
          getChannel(), getGetDistributedPlanMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * 执行单个任务
     * </pre>
     */
    public ExecuteTaskResponse executeTask(ExecuteTaskRequest request) throws io.grpc.StatusException {
      return io.grpc.stub.ClientCalls.blockingV2UnaryCall(
          getChannel(), getExecuteTaskMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * 流式执行任务（返回数据流）
     * </pre>
     */
    @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/10918")
    public io.grpc.stub.BlockingClientCall<?, DataChunk>
        executeTaskStream(ExecuteTaskRequest request) {
      return io.grpc.stub.ClientCalls.blockingV2ServerStreamingCall(
          getChannel(), getExecuteTaskStreamMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * 取消任务
     * </pre>
     */
    public CancelTaskResponse cancelTask(CancelTaskRequest request) throws io.grpc.StatusException {
      return io.grpc.stub.ClientCalls.blockingV2UnaryCall(
          getChannel(), getCancelTaskMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * 心跳检测
     * </pre>
     */
    public HeartbeatResponse heartbeat(HeartbeatRequest request) throws io.grpc.StatusException {
      return io.grpc.stub.ClientCalls.blockingV2UnaryCall(
          getChannel(), getHeartbeatMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do limited synchronous rpc calls to service JQuickPhysicalPlanService.
   */
  public static final class JQuickPhysicalPlanServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<JQuickPhysicalPlanServiceBlockingStub> {
    private JQuickPhysicalPlanServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected JQuickPhysicalPlanServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new JQuickPhysicalPlanServiceBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * 获取分布式执行计划
     * </pre>
     */
    public GetPlanResponse getDistributedPlan(GetPlanRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetDistributedPlanMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * 执行单个任务
     * </pre>
     */
    public ExecuteTaskResponse executeTask(ExecuteTaskRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getExecuteTaskMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * 流式执行任务（返回数据流）
     * </pre>
     */
    public java.util.Iterator<DataChunk> executeTaskStream(
        ExecuteTaskRequest request) {
      return io.grpc.stub.ClientCalls.blockingServerStreamingCall(
          getChannel(), getExecuteTaskStreamMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * 取消任务
     * </pre>
     */
    public CancelTaskResponse cancelTask(CancelTaskRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getCancelTaskMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * 心跳检测
     * </pre>
     */
    public HeartbeatResponse heartbeat(HeartbeatRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getHeartbeatMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service JQuickPhysicalPlanService.
   */
  public static final class JQuickPhysicalPlanServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<JQuickPhysicalPlanServiceFutureStub> {
    private JQuickPhysicalPlanServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected JQuickPhysicalPlanServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new JQuickPhysicalPlanServiceFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * 获取分布式执行计划
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<GetPlanResponse> getDistributedPlan(
        GetPlanRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetDistributedPlanMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * 执行单个任务
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<ExecuteTaskResponse> executeTask(
        ExecuteTaskRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getExecuteTaskMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * 取消任务
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<CancelTaskResponse> cancelTask(
        CancelTaskRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getCancelTaskMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * 心跳检测
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<HeartbeatResponse> heartbeat(
        HeartbeatRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getHeartbeatMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_GET_DISTRIBUTED_PLAN = 0;
  private static final int METHODID_EXECUTE_TASK = 1;
  private static final int METHODID_EXECUTE_TASK_STREAM = 2;
  private static final int METHODID_CANCEL_TASK = 3;
  private static final int METHODID_HEARTBEAT = 4;

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

    @Override
    @SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_GET_DISTRIBUTED_PLAN:
          serviceImpl.getDistributedPlan((GetPlanRequest) request,
              (io.grpc.stub.StreamObserver<GetPlanResponse>) responseObserver);
          break;
        case METHODID_EXECUTE_TASK:
          serviceImpl.executeTask((ExecuteTaskRequest) request,
              (io.grpc.stub.StreamObserver<ExecuteTaskResponse>) responseObserver);
          break;
        case METHODID_EXECUTE_TASK_STREAM:
          serviceImpl.executeTaskStream((ExecuteTaskRequest) request,
              (io.grpc.stub.StreamObserver<DataChunk>) responseObserver);
          break;
        case METHODID_CANCEL_TASK:
          serviceImpl.cancelTask((CancelTaskRequest) request,
              (io.grpc.stub.StreamObserver<CancelTaskResponse>) responseObserver);
          break;
        case METHODID_HEARTBEAT:
          serviceImpl.heartbeat((HeartbeatRequest) request,
              (io.grpc.stub.StreamObserver<HeartbeatResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @Override
    @SuppressWarnings("unchecked")
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
          getGetDistributedPlanMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              GetPlanRequest,
              GetPlanResponse>(
                service, METHODID_GET_DISTRIBUTED_PLAN)))
        .addMethod(
          getExecuteTaskMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              ExecuteTaskRequest,
              ExecuteTaskResponse>(
                service, METHODID_EXECUTE_TASK)))
        .addMethod(
          getExecuteTaskStreamMethod(),
          io.grpc.stub.ServerCalls.asyncServerStreamingCall(
            new MethodHandlers<
              ExecuteTaskRequest,
              DataChunk>(
                service, METHODID_EXECUTE_TASK_STREAM)))
        .addMethod(
          getCancelTaskMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              CancelTaskRequest,
              CancelTaskResponse>(
                service, METHODID_CANCEL_TASK)))
        .addMethod(
          getHeartbeatMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              HeartbeatRequest,
              HeartbeatResponse>(
                service, METHODID_HEARTBEAT)))
        .build();
  }

  private static abstract class JQuickPhysicalPlanServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    JQuickPhysicalPlanServiceBaseDescriptorSupplier() {}

    @Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return JQuickPhysicalPlanProto.getDescriptor();
    }

    @Override
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
    private final String methodName;

    JQuickPhysicalPlanServiceMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @Override
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
              .addMethod(getGetDistributedPlanMethod())
              .addMethod(getExecuteTaskMethod())
              .addMethod(getExecuteTaskStreamMethod())
              .addMethod(getCancelTaskMethod())
              .addMethod(getHeartbeatMethod())
              .build();
        }
      }
    }
    return result;
  }
}
