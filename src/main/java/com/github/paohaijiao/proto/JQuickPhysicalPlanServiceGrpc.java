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
  private static volatile io.grpc.MethodDescriptor<JQuickExecuteTaskRequest,
      JQuickExecuteTaskResponse> getExecuteTaskMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ExecuteTask",
      requestType = JQuickExecuteTaskRequest.class,
      responseType = JQuickExecuteTaskResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<JQuickExecuteTaskRequest,
      JQuickExecuteTaskResponse> getExecuteTaskMethod() {
    io.grpc.MethodDescriptor<JQuickExecuteTaskRequest, JQuickExecuteTaskResponse> getExecuteTaskMethod;
    if ((getExecuteTaskMethod = JQuickPhysicalPlanServiceGrpc.getExecuteTaskMethod) == null) {
      synchronized (JQuickPhysicalPlanServiceGrpc.class) {
        if ((getExecuteTaskMethod = JQuickPhysicalPlanServiceGrpc.getExecuteTaskMethod) == null) {
          JQuickPhysicalPlanServiceGrpc.getExecuteTaskMethod = getExecuteTaskMethod =
              io.grpc.MethodDescriptor.<JQuickExecuteTaskRequest, JQuickExecuteTaskResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ExecuteTask"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  JQuickExecuteTaskRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  JQuickExecuteTaskResponse.getDefaultInstance()))
              .setSchemaDescriptor(new JQuickPhysicalPlanServiceMethodDescriptorSupplier("ExecuteTask"))
              .build();
        }
      }
    }
    return getExecuteTaskMethod;
  }

  private static volatile io.grpc.MethodDescriptor<JQuickExecuteTaskRequest,
      JQuickDataChunkProto> getExecuteTaskStreamMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ExecuteTaskStream",
      requestType = JQuickExecuteTaskRequest.class,
      responseType = JQuickDataChunkProto.class,
      methodType = io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
  public static io.grpc.MethodDescriptor<JQuickExecuteTaskRequest,
      JQuickDataChunkProto> getExecuteTaskStreamMethod() {
    io.grpc.MethodDescriptor<JQuickExecuteTaskRequest, JQuickDataChunkProto> getExecuteTaskStreamMethod;
    if ((getExecuteTaskStreamMethod = JQuickPhysicalPlanServiceGrpc.getExecuteTaskStreamMethod) == null) {
      synchronized (JQuickPhysicalPlanServiceGrpc.class) {
        if ((getExecuteTaskStreamMethod = JQuickPhysicalPlanServiceGrpc.getExecuteTaskStreamMethod) == null) {
          JQuickPhysicalPlanServiceGrpc.getExecuteTaskStreamMethod = getExecuteTaskStreamMethod =
              io.grpc.MethodDescriptor.<JQuickExecuteTaskRequest, JQuickDataChunkProto>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ExecuteTaskStream"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  JQuickExecuteTaskRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  JQuickDataChunkProto.getDefaultInstance()))
              .setSchemaDescriptor(new JQuickPhysicalPlanServiceMethodDescriptorSupplier("ExecuteTaskStream"))
              .build();
        }
      }
    }
    return getExecuteTaskStreamMethod;
  }

  private static volatile io.grpc.MethodDescriptor<JQuickCancelQueryRequest,
      JQuickCancelQueryResponse> getCancelTaskMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "CancelTask",
      requestType = JQuickCancelQueryRequest.class,
      responseType = JQuickCancelQueryResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<JQuickCancelQueryRequest,
      JQuickCancelQueryResponse> getCancelTaskMethod() {
    io.grpc.MethodDescriptor<JQuickCancelQueryRequest, JQuickCancelQueryResponse> getCancelTaskMethod;
    if ((getCancelTaskMethod = JQuickPhysicalPlanServiceGrpc.getCancelTaskMethod) == null) {
      synchronized (JQuickPhysicalPlanServiceGrpc.class) {
        if ((getCancelTaskMethod = JQuickPhysicalPlanServiceGrpc.getCancelTaskMethod) == null) {
          JQuickPhysicalPlanServiceGrpc.getCancelTaskMethod = getCancelTaskMethod =
              io.grpc.MethodDescriptor.<JQuickCancelQueryRequest, JQuickCancelQueryResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "CancelTask"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  JQuickCancelQueryRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  JQuickCancelQueryResponse.getDefaultInstance()))
              .setSchemaDescriptor(new JQuickPhysicalPlanServiceMethodDescriptorSupplier("CancelTask"))
              .build();
        }
      }
    }
    return getCancelTaskMethod;
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
    default void executeTask(JQuickExecuteTaskRequest request,
                             io.grpc.stub.StreamObserver<JQuickExecuteTaskResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getExecuteTaskMethod(), responseObserver);
    }

    /**
     */
    default void executeTaskStream(JQuickExecuteTaskRequest request,
                                   io.grpc.stub.StreamObserver<JQuickDataChunkProto> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getExecuteTaskStreamMethod(), responseObserver);
    }

    /**
     */
    default void cancelTask(JQuickCancelQueryRequest request,
                            io.grpc.stub.StreamObserver<JQuickCancelQueryResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getCancelTaskMethod(), responseObserver);
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
    public void executeTask(JQuickExecuteTaskRequest request,
                            io.grpc.stub.StreamObserver<JQuickExecuteTaskResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getExecuteTaskMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void executeTaskStream(JQuickExecuteTaskRequest request,
                                  io.grpc.stub.StreamObserver<JQuickDataChunkProto> responseObserver) {
      io.grpc.stub.ClientCalls.asyncServerStreamingCall(
          getChannel().newCall(getExecuteTaskStreamMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void cancelTask(JQuickCancelQueryRequest request,
                           io.grpc.stub.StreamObserver<JQuickCancelQueryResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getCancelTaskMethod(), getCallOptions()), request, responseObserver);
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
    public JQuickExecuteTaskResponse executeTask(JQuickExecuteTaskRequest request) throws io.grpc.StatusException {
      return io.grpc.stub.ClientCalls.blockingV2UnaryCall(
          getChannel(), getExecuteTaskMethod(), getCallOptions(), request);
    }

    /**
     */
    @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/10918")
    public io.grpc.stub.BlockingClientCall<?, JQuickDataChunkProto>
        executeTaskStream(JQuickExecuteTaskRequest request) {
      return io.grpc.stub.ClientCalls.blockingV2ServerStreamingCall(
          getChannel(), getExecuteTaskStreamMethod(), getCallOptions(), request);
    }

    /**
     */
    public JQuickCancelQueryResponse cancelTask(JQuickCancelQueryRequest request) throws io.grpc.StatusException {
      return io.grpc.stub.ClientCalls.blockingV2UnaryCall(
          getChannel(), getCancelTaskMethod(), getCallOptions(), request);
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
    public JQuickExecuteTaskResponse executeTask(JQuickExecuteTaskRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getExecuteTaskMethod(), getCallOptions(), request);
    }

    /**
     */
    public java.util.Iterator<JQuickDataChunkProto> executeTaskStream(
        JQuickExecuteTaskRequest request) {
      return io.grpc.stub.ClientCalls.blockingServerStreamingCall(
          getChannel(), getExecuteTaskStreamMethod(), getCallOptions(), request);
    }

    /**
     */
    public JQuickCancelQueryResponse cancelTask(JQuickCancelQueryRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getCancelTaskMethod(), getCallOptions(), request);
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
    public com.google.common.util.concurrent.ListenableFuture<JQuickExecuteTaskResponse> executeTask(
        JQuickExecuteTaskRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getExecuteTaskMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<JQuickCancelQueryResponse> cancelTask(
        JQuickCancelQueryRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getCancelTaskMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_EXECUTE_TASK = 0;
  private static final int METHODID_EXECUTE_TASK_STREAM = 1;
  private static final int METHODID_CANCEL_TASK = 2;

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
          serviceImpl.executeTask((JQuickExecuteTaskRequest) request,
              (io.grpc.stub.StreamObserver<JQuickExecuteTaskResponse>) responseObserver);
          break;
        case METHODID_EXECUTE_TASK_STREAM:
          serviceImpl.executeTaskStream((JQuickExecuteTaskRequest) request,
              (io.grpc.stub.StreamObserver<JQuickDataChunkProto>) responseObserver);
          break;
        case METHODID_CANCEL_TASK:
          serviceImpl.cancelTask((JQuickCancelQueryRequest) request,
              (io.grpc.stub.StreamObserver<JQuickCancelQueryResponse>) responseObserver);
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
              JQuickExecuteTaskRequest,
              JQuickExecuteTaskResponse>(
                service, METHODID_EXECUTE_TASK)))
        .addMethod(
          getExecuteTaskStreamMethod(),
          io.grpc.stub.ServerCalls.asyncServerStreamingCall(
            new MethodHandlers<
              JQuickExecuteTaskRequest,
              JQuickDataChunkProto>(
                service, METHODID_EXECUTE_TASK_STREAM)))
        .addMethod(
          getCancelTaskMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              JQuickCancelQueryRequest,
              JQuickCancelQueryResponse>(
                service, METHODID_CANCEL_TASK)))
        .build();
  }

  private static abstract class JQuickPhysicalPlanServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    JQuickPhysicalPlanServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return JQuickPhysicalPlanProto.getDescriptor();
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
              .build();
        }
      }
    }
    return result;
  }
}
