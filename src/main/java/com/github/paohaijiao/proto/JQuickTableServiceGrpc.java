package com.github.paohaijiao.proto;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 * <pre>
 * ============================================================================
 * gRPC 服务定义
 * ============================================================================
 * </pre>
 */
@io.grpc.stub.annotations.GrpcGenerated
public final class JQuickTableServiceGrpc {

  private JQuickTableServiceGrpc() {}

  public static final String SERVICE_NAME = "com.github.paohaijiao.proto.JQuickTableService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<RegisterTableRequest,
      RegisterTableResponse> getRegisterTableMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "RegisterTable",
      requestType = RegisterTableRequest.class,
      responseType = RegisterTableResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<RegisterTableRequest,
      RegisterTableResponse> getRegisterTableMethod() {
    io.grpc.MethodDescriptor<RegisterTableRequest, RegisterTableResponse> getRegisterTableMethod;
    if ((getRegisterTableMethod = JQuickTableServiceGrpc.getRegisterTableMethod) == null) {
      synchronized (JQuickTableServiceGrpc.class) {
        if ((getRegisterTableMethod = JQuickTableServiceGrpc.getRegisterTableMethod) == null) {
          JQuickTableServiceGrpc.getRegisterTableMethod = getRegisterTableMethod =
              io.grpc.MethodDescriptor.<RegisterTableRequest, RegisterTableResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "RegisterTable"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  RegisterTableRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  RegisterTableResponse.getDefaultInstance()))
              .setSchemaDescriptor(new JQuickTableServiceMethodDescriptorSupplier("RegisterTable"))
              .build();
        }
      }
    }
    return getRegisterTableMethod;
  }

  private static volatile io.grpc.MethodDescriptor<RegisterTableRequest,
      RegisterTablesResponse> getRegisterTablesMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "RegisterTables",
      requestType = RegisterTableRequest.class,
      responseType = RegisterTablesResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.CLIENT_STREAMING)
  public static io.grpc.MethodDescriptor<RegisterTableRequest,
      RegisterTablesResponse> getRegisterTablesMethod() {
    io.grpc.MethodDescriptor<RegisterTableRequest, RegisterTablesResponse> getRegisterTablesMethod;
    if ((getRegisterTablesMethod = JQuickTableServiceGrpc.getRegisterTablesMethod) == null) {
      synchronized (JQuickTableServiceGrpc.class) {
        if ((getRegisterTablesMethod = JQuickTableServiceGrpc.getRegisterTablesMethod) == null) {
          JQuickTableServiceGrpc.getRegisterTablesMethod = getRegisterTablesMethod =
              io.grpc.MethodDescriptor.<RegisterTableRequest, RegisterTablesResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.CLIENT_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "RegisterTables"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  RegisterTableRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  RegisterTablesResponse.getDefaultInstance()))
              .setSchemaDescriptor(new JQuickTableServiceMethodDescriptorSupplier("RegisterTables"))
              .build();
        }
      }
    }
    return getRegisterTablesMethod;
  }

  private static volatile io.grpc.MethodDescriptor<DropTableRequest,
      DropTableResponse> getDropTableMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "DropTable",
      requestType = DropTableRequest.class,
      responseType = DropTableResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<DropTableRequest,
      DropTableResponse> getDropTableMethod() {
    io.grpc.MethodDescriptor<DropTableRequest, DropTableResponse> getDropTableMethod;
    if ((getDropTableMethod = JQuickTableServiceGrpc.getDropTableMethod) == null) {
      synchronized (JQuickTableServiceGrpc.class) {
        if ((getDropTableMethod = JQuickTableServiceGrpc.getDropTableMethod) == null) {
          JQuickTableServiceGrpc.getDropTableMethod = getDropTableMethod =
              io.grpc.MethodDescriptor.<DropTableRequest, DropTableResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "DropTable"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  DropTableRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  DropTableResponse.getDefaultInstance()))
              .setSchemaDescriptor(new JQuickTableServiceMethodDescriptorSupplier("DropTable"))
              .build();
        }
      }
    }
    return getDropTableMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static JQuickTableServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<JQuickTableServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<JQuickTableServiceStub>() {
        @Override
        public JQuickTableServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new JQuickTableServiceStub(channel, callOptions);
        }
      };
    return JQuickTableServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports all types of calls on the service
   */
  public static JQuickTableServiceBlockingV2Stub newBlockingV2Stub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<JQuickTableServiceBlockingV2Stub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<JQuickTableServiceBlockingV2Stub>() {
        @Override
        public JQuickTableServiceBlockingV2Stub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new JQuickTableServiceBlockingV2Stub(channel, callOptions);
        }
      };
    return JQuickTableServiceBlockingV2Stub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static JQuickTableServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<JQuickTableServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<JQuickTableServiceBlockingStub>() {
        @Override
        public JQuickTableServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new JQuickTableServiceBlockingStub(channel, callOptions);
        }
      };
    return JQuickTableServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static JQuickTableServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<JQuickTableServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<JQuickTableServiceFutureStub>() {
        @Override
        public JQuickTableServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new JQuickTableServiceFutureStub(channel, callOptions);
        }
      };
    return JQuickTableServiceFutureStub.newStub(factory, channel);
  }

  /**
   * <pre>
   * ============================================================================
   * gRPC 服务定义
   * ============================================================================
   * </pre>
   */
  public interface AsyncService {

    /**
     * <pre>
     * 注册表数据到 Worker
     * </pre>
     */
    default void registerTable(RegisterTableRequest request,
                               io.grpc.stub.StreamObserver<RegisterTableResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getRegisterTableMethod(), responseObserver);
    }

    /**
     * <pre>
     * 批量注册表
     * </pre>
     */
    default io.grpc.stub.StreamObserver<RegisterTableRequest> registerTables(
        io.grpc.stub.StreamObserver<RegisterTablesResponse> responseObserver) {
      return io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall(getRegisterTablesMethod(), responseObserver);
    }

    /**
     * <pre>
     * 删除表
     * </pre>
     */
    default void dropTable(DropTableRequest request,
                           io.grpc.stub.StreamObserver<DropTableResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getDropTableMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service JQuickTableService.
   * <pre>
   * ============================================================================
   * gRPC 服务定义
   * ============================================================================
   * </pre>
   */
  public static abstract class JQuickTableServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @Override public final io.grpc.ServerServiceDefinition bindService() {
      return JQuickTableServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service JQuickTableService.
   * <pre>
   * ============================================================================
   * gRPC 服务定义
   * ============================================================================
   * </pre>
   */
  public static final class JQuickTableServiceStub
      extends io.grpc.stub.AbstractAsyncStub<JQuickTableServiceStub> {
    private JQuickTableServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected JQuickTableServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new JQuickTableServiceStub(channel, callOptions);
    }

    /**
     * <pre>
     * 注册表数据到 Worker
     * </pre>
     */
    public void registerTable(RegisterTableRequest request,
                              io.grpc.stub.StreamObserver<RegisterTableResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getRegisterTableMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * 批量注册表
     * </pre>
     */
    public io.grpc.stub.StreamObserver<RegisterTableRequest> registerTables(
        io.grpc.stub.StreamObserver<RegisterTablesResponse> responseObserver) {
      return io.grpc.stub.ClientCalls.asyncClientStreamingCall(
          getChannel().newCall(getRegisterTablesMethod(), getCallOptions()), responseObserver);
    }

    /**
     * <pre>
     * 删除表
     * </pre>
     */
    public void dropTable(DropTableRequest request,
                          io.grpc.stub.StreamObserver<DropTableResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getDropTableMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service JQuickTableService.
   * <pre>
   * ============================================================================
   * gRPC 服务定义
   * ============================================================================
   * </pre>
   */
  public static final class JQuickTableServiceBlockingV2Stub
      extends io.grpc.stub.AbstractBlockingStub<JQuickTableServiceBlockingV2Stub> {
    private JQuickTableServiceBlockingV2Stub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected JQuickTableServiceBlockingV2Stub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new JQuickTableServiceBlockingV2Stub(channel, callOptions);
    }

    /**
     * <pre>
     * 注册表数据到 Worker
     * </pre>
     */
    public RegisterTableResponse registerTable(RegisterTableRequest request) throws io.grpc.StatusException {
      return io.grpc.stub.ClientCalls.blockingV2UnaryCall(
          getChannel(), getRegisterTableMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * 批量注册表
     * </pre>
     */
    @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/10918")
    public io.grpc.stub.BlockingClientCall<RegisterTableRequest, RegisterTablesResponse>
        registerTables() {
      return io.grpc.stub.ClientCalls.blockingClientStreamingCall(
          getChannel(), getRegisterTablesMethod(), getCallOptions());
    }

    /**
     * <pre>
     * 删除表
     * </pre>
     */
    public DropTableResponse dropTable(DropTableRequest request) throws io.grpc.StatusException {
      return io.grpc.stub.ClientCalls.blockingV2UnaryCall(
          getChannel(), getDropTableMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do limited synchronous rpc calls to service JQuickTableService.
   * <pre>
   * ============================================================================
   * gRPC 服务定义
   * ============================================================================
   * </pre>
   */
  public static final class JQuickTableServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<JQuickTableServiceBlockingStub> {
    private JQuickTableServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected JQuickTableServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new JQuickTableServiceBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * 注册表数据到 Worker
     * </pre>
     */
    public RegisterTableResponse registerTable(RegisterTableRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getRegisterTableMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * 删除表
     * </pre>
     */
    public DropTableResponse dropTable(DropTableRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getDropTableMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service JQuickTableService.
   * <pre>
   * ============================================================================
   * gRPC 服务定义
   * ============================================================================
   * </pre>
   */
  public static final class JQuickTableServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<JQuickTableServiceFutureStub> {
    private JQuickTableServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected JQuickTableServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new JQuickTableServiceFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * 注册表数据到 Worker
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<RegisterTableResponse> registerTable(
        RegisterTableRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getRegisterTableMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * 删除表
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<DropTableResponse> dropTable(
        DropTableRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getDropTableMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_REGISTER_TABLE = 0;
  private static final int METHODID_DROP_TABLE = 1;
  private static final int METHODID_REGISTER_TABLES = 2;

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
        case METHODID_REGISTER_TABLE:
          serviceImpl.registerTable((RegisterTableRequest) request,
              (io.grpc.stub.StreamObserver<RegisterTableResponse>) responseObserver);
          break;
        case METHODID_DROP_TABLE:
          serviceImpl.dropTable((DropTableRequest) request,
              (io.grpc.stub.StreamObserver<DropTableResponse>) responseObserver);
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
        case METHODID_REGISTER_TABLES:
          return (io.grpc.stub.StreamObserver<Req>) serviceImpl.registerTables(
              (io.grpc.stub.StreamObserver<RegisterTablesResponse>) responseObserver);
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getRegisterTableMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              RegisterTableRequest,
              RegisterTableResponse>(
                service, METHODID_REGISTER_TABLE)))
        .addMethod(
          getRegisterTablesMethod(),
          io.grpc.stub.ServerCalls.asyncClientStreamingCall(
            new MethodHandlers<
              RegisterTableRequest,
              RegisterTablesResponse>(
                service, METHODID_REGISTER_TABLES)))
        .addMethod(
          getDropTableMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              DropTableRequest,
              DropTableResponse>(
                service, METHODID_DROP_TABLE)))
        .build();
  }

  private static abstract class JQuickTableServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    JQuickTableServiceBaseDescriptorSupplier() {}

    @Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return JQuickPhysicalPlanProto.getDescriptor();
    }

    @Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("JQuickTableService");
    }
  }

  private static final class JQuickTableServiceFileDescriptorSupplier
      extends JQuickTableServiceBaseDescriptorSupplier {
    JQuickTableServiceFileDescriptorSupplier() {}
  }

  private static final class JQuickTableServiceMethodDescriptorSupplier
      extends JQuickTableServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    JQuickTableServiceMethodDescriptorSupplier(String methodName) {
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
      synchronized (JQuickTableServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new JQuickTableServiceFileDescriptorSupplier())
              .addMethod(getRegisterTableMethod())
              .addMethod(getRegisterTablesMethod())
              .addMethod(getDropTableMethod())
              .build();
        }
      }
    }
    return result;
  }
}
