package com.github.paohaijiao.distributed.grpc;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@io.grpc.stub.annotations.GrpcGenerated
public final class FragmentServiceGrpc {

  private FragmentServiceGrpc() {}

  public static final String SERVICE_NAME = "com.github.paohaijiao.distributed.grpc.FragmentService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<FragmentServiceProto.FragmentRequest,
      FragmentServiceProto.FragmentResponse> getExecuteFragmentMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ExecuteFragment",
      requestType = FragmentServiceProto.FragmentRequest.class,
      responseType = FragmentServiceProto.FragmentResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<FragmentServiceProto.FragmentRequest,
      FragmentServiceProto.FragmentResponse> getExecuteFragmentMethod() {
    io.grpc.MethodDescriptor<FragmentServiceProto.FragmentRequest, FragmentServiceProto.FragmentResponse> getExecuteFragmentMethod;
    if ((getExecuteFragmentMethod = FragmentServiceGrpc.getExecuteFragmentMethod) == null) {
      synchronized (FragmentServiceGrpc.class) {
        if ((getExecuteFragmentMethod = FragmentServiceGrpc.getExecuteFragmentMethod) == null) {
          FragmentServiceGrpc.getExecuteFragmentMethod = getExecuteFragmentMethod =
              io.grpc.MethodDescriptor.<FragmentServiceProto.FragmentRequest, FragmentServiceProto.FragmentResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ExecuteFragment"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  FragmentServiceProto.FragmentRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  FragmentServiceProto.FragmentResponse.getDefaultInstance()))
              .setSchemaDescriptor(new FragmentServiceMethodDescriptorSupplier("ExecuteFragment"))
              .build();
        }
      }
    }
    return getExecuteFragmentMethod;
  }

  private static volatile io.grpc.MethodDescriptor<FragmentServiceProto.DataChunk,
      FragmentServiceProto.DataAck> getSendDataMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "SendData",
      requestType = FragmentServiceProto.DataChunk.class,
      responseType = FragmentServiceProto.DataAck.class,
      methodType = io.grpc.MethodDescriptor.MethodType.CLIENT_STREAMING)
  public static io.grpc.MethodDescriptor<FragmentServiceProto.DataChunk,
      FragmentServiceProto.DataAck> getSendDataMethod() {
    io.grpc.MethodDescriptor<FragmentServiceProto.DataChunk, FragmentServiceProto.DataAck> getSendDataMethod;
    if ((getSendDataMethod = FragmentServiceGrpc.getSendDataMethod) == null) {
      synchronized (FragmentServiceGrpc.class) {
        if ((getSendDataMethod = FragmentServiceGrpc.getSendDataMethod) == null) {
          FragmentServiceGrpc.getSendDataMethod = getSendDataMethod =
              io.grpc.MethodDescriptor.<FragmentServiceProto.DataChunk, FragmentServiceProto.DataAck>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.CLIENT_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "SendData"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  FragmentServiceProto.DataChunk.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  FragmentServiceProto.DataAck.getDefaultInstance()))
              .setSchemaDescriptor(new FragmentServiceMethodDescriptorSupplier("SendData"))
              .build();
        }
      }
    }
    return getSendDataMethod;
  }

  private static volatile io.grpc.MethodDescriptor<FragmentServiceProto.FetchRequest,
      FragmentServiceProto.DataChunk> getFetchDataMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "FetchData",
      requestType = FragmentServiceProto.FetchRequest.class,
      responseType = FragmentServiceProto.DataChunk.class,
      methodType = io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
  public static io.grpc.MethodDescriptor<FragmentServiceProto.FetchRequest,
      FragmentServiceProto.DataChunk> getFetchDataMethod() {
    io.grpc.MethodDescriptor<FragmentServiceProto.FetchRequest, FragmentServiceProto.DataChunk> getFetchDataMethod;
    if ((getFetchDataMethod = FragmentServiceGrpc.getFetchDataMethod) == null) {
      synchronized (FragmentServiceGrpc.class) {
        if ((getFetchDataMethod = FragmentServiceGrpc.getFetchDataMethod) == null) {
          FragmentServiceGrpc.getFetchDataMethod = getFetchDataMethod =
              io.grpc.MethodDescriptor.<FragmentServiceProto.FetchRequest, FragmentServiceProto.DataChunk>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "FetchData"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  FragmentServiceProto.FetchRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  FragmentServiceProto.DataChunk.getDefaultInstance()))
              .setSchemaDescriptor(new FragmentServiceMethodDescriptorSupplier("FetchData"))
              .build();
        }
      }
    }
    return getFetchDataMethod;
  }

  private static volatile io.grpc.MethodDescriptor<FragmentServiceProto.HealthCheckRequest,
      FragmentServiceProto.HealthCheckResponse> getHealthCheckMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "HealthCheck",
      requestType = FragmentServiceProto.HealthCheckRequest.class,
      responseType = FragmentServiceProto.HealthCheckResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<FragmentServiceProto.HealthCheckRequest,
      FragmentServiceProto.HealthCheckResponse> getHealthCheckMethod() {
    io.grpc.MethodDescriptor<FragmentServiceProto.HealthCheckRequest, FragmentServiceProto.HealthCheckResponse> getHealthCheckMethod;
    if ((getHealthCheckMethod = FragmentServiceGrpc.getHealthCheckMethod) == null) {
      synchronized (FragmentServiceGrpc.class) {
        if ((getHealthCheckMethod = FragmentServiceGrpc.getHealthCheckMethod) == null) {
          FragmentServiceGrpc.getHealthCheckMethod = getHealthCheckMethod =
              io.grpc.MethodDescriptor.<FragmentServiceProto.HealthCheckRequest, FragmentServiceProto.HealthCheckResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "HealthCheck"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  FragmentServiceProto.HealthCheckRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  FragmentServiceProto.HealthCheckResponse.getDefaultInstance()))
              .setSchemaDescriptor(new FragmentServiceMethodDescriptorSupplier("HealthCheck"))
              .build();
        }
      }
    }
    return getHealthCheckMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static FragmentServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<FragmentServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<FragmentServiceStub>() {
        @Override
        public FragmentServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new FragmentServiceStub(channel, callOptions);
        }
      };
    return FragmentServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports all types of calls on the service
   */
  public static FragmentServiceBlockingV2Stub newBlockingV2Stub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<FragmentServiceBlockingV2Stub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<FragmentServiceBlockingV2Stub>() {
        @Override
        public FragmentServiceBlockingV2Stub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new FragmentServiceBlockingV2Stub(channel, callOptions);
        }
      };
    return FragmentServiceBlockingV2Stub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static FragmentServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<FragmentServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<FragmentServiceBlockingStub>() {
        @Override
        public FragmentServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new FragmentServiceBlockingStub(channel, callOptions);
        }
      };
    return FragmentServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static FragmentServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<FragmentServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<FragmentServiceFutureStub>() {
        @Override
        public FragmentServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new FragmentServiceFutureStub(channel, callOptions);
        }
      };
    return FragmentServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     * <pre>
     * 提交Fragment执行
     * </pre>
     */
    default void executeFragment(FragmentServiceProto.FragmentRequest request,
                                 io.grpc.stub.StreamObserver<FragmentServiceProto.FragmentResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getExecuteFragmentMethod(), responseObserver);
    }

    /**
     * <pre>
     * 流式数据传输
     * </pre>
     */
    default io.grpc.stub.StreamObserver<FragmentServiceProto.DataChunk> sendData(
        io.grpc.stub.StreamObserver<FragmentServiceProto.DataAck> responseObserver) {
      return io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall(getSendDataMethod(), responseObserver);
    }

    /**
     * <pre>
     * 拉取数据
     * </pre>
     */
    default void fetchData(FragmentServiceProto.FetchRequest request,
                           io.grpc.stub.StreamObserver<FragmentServiceProto.DataChunk> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getFetchDataMethod(), responseObserver);
    }

    /**
     * <pre>
     * 健康检查
     * </pre>
     */
    default void healthCheck(FragmentServiceProto.HealthCheckRequest request,
                             io.grpc.stub.StreamObserver<FragmentServiceProto.HealthCheckResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getHealthCheckMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service FragmentService.
   */
  public static abstract class FragmentServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @Override public final io.grpc.ServerServiceDefinition bindService() {
      return FragmentServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service FragmentService.
   */
  public static final class FragmentServiceStub
      extends io.grpc.stub.AbstractAsyncStub<FragmentServiceStub> {
    private FragmentServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected FragmentServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new FragmentServiceStub(channel, callOptions);
    }

    /**
     * <pre>
     * 提交Fragment执行
     * </pre>
     */
    public void executeFragment(FragmentServiceProto.FragmentRequest request,
                                io.grpc.stub.StreamObserver<FragmentServiceProto.FragmentResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getExecuteFragmentMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * 流式数据传输
     * </pre>
     */
    public io.grpc.stub.StreamObserver<FragmentServiceProto.DataChunk> sendData(
        io.grpc.stub.StreamObserver<FragmentServiceProto.DataAck> responseObserver) {
      return io.grpc.stub.ClientCalls.asyncClientStreamingCall(
          getChannel().newCall(getSendDataMethod(), getCallOptions()), responseObserver);
    }

    /**
     * <pre>
     * 拉取数据
     * </pre>
     */
    public void fetchData(FragmentServiceProto.FetchRequest request,
                          io.grpc.stub.StreamObserver<FragmentServiceProto.DataChunk> responseObserver) {
      io.grpc.stub.ClientCalls.asyncServerStreamingCall(
          getChannel().newCall(getFetchDataMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * 健康检查
     * </pre>
     */
    public void healthCheck(FragmentServiceProto.HealthCheckRequest request,
                            io.grpc.stub.StreamObserver<FragmentServiceProto.HealthCheckResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getHealthCheckMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service FragmentService.
   */
  public static final class FragmentServiceBlockingV2Stub
      extends io.grpc.stub.AbstractBlockingStub<FragmentServiceBlockingV2Stub> {
    private FragmentServiceBlockingV2Stub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected FragmentServiceBlockingV2Stub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new FragmentServiceBlockingV2Stub(channel, callOptions);
    }

    /**
     * <pre>
     * 提交Fragment执行
     * </pre>
     */
    public FragmentServiceProto.FragmentResponse executeFragment(FragmentServiceProto.FragmentRequest request) throws io.grpc.StatusException {
      return io.grpc.stub.ClientCalls.blockingV2UnaryCall(
          getChannel(), getExecuteFragmentMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * 流式数据传输
     * </pre>
     */
    @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/10918")
    public io.grpc.stub.BlockingClientCall<FragmentServiceProto.DataChunk, FragmentServiceProto.DataAck>
        sendData() {
      return io.grpc.stub.ClientCalls.blockingClientStreamingCall(
          getChannel(), getSendDataMethod(), getCallOptions());
    }

    /**
     * <pre>
     * 拉取数据
     * </pre>
     */
    @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/10918")
    public io.grpc.stub.BlockingClientCall<?, FragmentServiceProto.DataChunk>
        fetchData(FragmentServiceProto.FetchRequest request) {
      return io.grpc.stub.ClientCalls.blockingV2ServerStreamingCall(
          getChannel(), getFetchDataMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * 健康检查
     * </pre>
     */
    public FragmentServiceProto.HealthCheckResponse healthCheck(FragmentServiceProto.HealthCheckRequest request) throws io.grpc.StatusException {
      return io.grpc.stub.ClientCalls.blockingV2UnaryCall(
          getChannel(), getHealthCheckMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do limited synchronous rpc calls to service FragmentService.
   */
  public static final class FragmentServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<FragmentServiceBlockingStub> {
    private FragmentServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected FragmentServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new FragmentServiceBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * 提交Fragment执行
     * </pre>
     */
    public FragmentServiceProto.FragmentResponse executeFragment(FragmentServiceProto.FragmentRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getExecuteFragmentMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * 拉取数据
     * </pre>
     */
    public java.util.Iterator<FragmentServiceProto.DataChunk> fetchData(
        FragmentServiceProto.FetchRequest request) {
      return io.grpc.stub.ClientCalls.blockingServerStreamingCall(
          getChannel(), getFetchDataMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * 健康检查
     * </pre>
     */
    public FragmentServiceProto.HealthCheckResponse healthCheck(FragmentServiceProto.HealthCheckRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getHealthCheckMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service FragmentService.
   */
  public static final class FragmentServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<FragmentServiceFutureStub> {
    private FragmentServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected FragmentServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new FragmentServiceFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * 提交Fragment执行
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<FragmentServiceProto.FragmentResponse> executeFragment(
        FragmentServiceProto.FragmentRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getExecuteFragmentMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * 健康检查
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<FragmentServiceProto.HealthCheckResponse> healthCheck(
        FragmentServiceProto.HealthCheckRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getHealthCheckMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_EXECUTE_FRAGMENT = 0;
  private static final int METHODID_FETCH_DATA = 1;
  private static final int METHODID_HEALTH_CHECK = 2;
  private static final int METHODID_SEND_DATA = 3;

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
        case METHODID_EXECUTE_FRAGMENT:
          serviceImpl.executeFragment((FragmentServiceProto.FragmentRequest) request,
              (io.grpc.stub.StreamObserver<FragmentServiceProto.FragmentResponse>) responseObserver);
          break;
        case METHODID_FETCH_DATA:
          serviceImpl.fetchData((FragmentServiceProto.FetchRequest) request,
              (io.grpc.stub.StreamObserver<FragmentServiceProto.DataChunk>) responseObserver);
          break;
        case METHODID_HEALTH_CHECK:
          serviceImpl.healthCheck((FragmentServiceProto.HealthCheckRequest) request,
              (io.grpc.stub.StreamObserver<FragmentServiceProto.HealthCheckResponse>) responseObserver);
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
        case METHODID_SEND_DATA:
          return (io.grpc.stub.StreamObserver<Req>) serviceImpl.sendData(
              (io.grpc.stub.StreamObserver<FragmentServiceProto.DataAck>) responseObserver);
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getExecuteFragmentMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              FragmentServiceProto.FragmentRequest,
              FragmentServiceProto.FragmentResponse>(
                service, METHODID_EXECUTE_FRAGMENT)))
        .addMethod(
          getSendDataMethod(),
          io.grpc.stub.ServerCalls.asyncClientStreamingCall(
            new MethodHandlers<
              FragmentServiceProto.DataChunk,
              FragmentServiceProto.DataAck>(
                service, METHODID_SEND_DATA)))
        .addMethod(
          getFetchDataMethod(),
          io.grpc.stub.ServerCalls.asyncServerStreamingCall(
            new MethodHandlers<
              FragmentServiceProto.FetchRequest,
              FragmentServiceProto.DataChunk>(
                service, METHODID_FETCH_DATA)))
        .addMethod(
          getHealthCheckMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              FragmentServiceProto.HealthCheckRequest,
              FragmentServiceProto.HealthCheckResponse>(
                service, METHODID_HEALTH_CHECK)))
        .build();
  }

  private static abstract class FragmentServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    FragmentServiceBaseDescriptorSupplier() {}

    @Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return FragmentServiceProto.getDescriptor();
    }

    @Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("FragmentService");
    }
  }

  private static final class FragmentServiceFileDescriptorSupplier
      extends FragmentServiceBaseDescriptorSupplier {
    FragmentServiceFileDescriptorSupplier() {}
  }

  private static final class FragmentServiceMethodDescriptorSupplier
      extends FragmentServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    FragmentServiceMethodDescriptorSupplier(String methodName) {
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
      synchronized (FragmentServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new FragmentServiceFileDescriptorSupplier())
              .addMethod(getExecuteFragmentMethod())
              .addMethod(getSendDataMethod())
              .addMethod(getFetchDataMethod())
              .addMethod(getHealthCheckMethod())
              .build();
        }
      }
    }
    return result;
  }
}
