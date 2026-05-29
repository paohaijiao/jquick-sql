package com.github.paohaijiao.proto;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@io.grpc.stub.annotations.GrpcGenerated
public final class JQuickCoordinatorServiceGrpc {

  private JQuickCoordinatorServiceGrpc() {}

  public static final String SERVICE_NAME = "com.github.paohaijiao.proto.JQuickCoordinatorService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<SubmitQueryRequest,
      SubmitQueryResponse> getSubmitQueryMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "SubmitQuery",
      requestType = SubmitQueryRequest.class,
      responseType = SubmitQueryResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<SubmitQueryRequest,
      SubmitQueryResponse> getSubmitQueryMethod() {
    io.grpc.MethodDescriptor<SubmitQueryRequest, SubmitQueryResponse> getSubmitQueryMethod;
    if ((getSubmitQueryMethod = JQuickCoordinatorServiceGrpc.getSubmitQueryMethod) == null) {
      synchronized (JQuickCoordinatorServiceGrpc.class) {
        if ((getSubmitQueryMethod = JQuickCoordinatorServiceGrpc.getSubmitQueryMethod) == null) {
          JQuickCoordinatorServiceGrpc.getSubmitQueryMethod = getSubmitQueryMethod =
              io.grpc.MethodDescriptor.<SubmitQueryRequest, SubmitQueryResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "SubmitQuery"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  SubmitQueryRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  SubmitQueryResponse.getDefaultInstance()))
              .setSchemaDescriptor(new JQuickCoordinatorServiceMethodDescriptorSupplier("SubmitQuery"))
              .build();
        }
      }
    }
    return getSubmitQueryMethod;
  }

  private static volatile io.grpc.MethodDescriptor<GetQueryStatusRequest,
      GetQueryStatusResponse> getGetQueryStatusMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetQueryStatus",
      requestType = GetQueryStatusRequest.class,
      responseType = GetQueryStatusResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<GetQueryStatusRequest,
      GetQueryStatusResponse> getGetQueryStatusMethod() {
    io.grpc.MethodDescriptor<GetQueryStatusRequest, GetQueryStatusResponse> getGetQueryStatusMethod;
    if ((getGetQueryStatusMethod = JQuickCoordinatorServiceGrpc.getGetQueryStatusMethod) == null) {
      synchronized (JQuickCoordinatorServiceGrpc.class) {
        if ((getGetQueryStatusMethod = JQuickCoordinatorServiceGrpc.getGetQueryStatusMethod) == null) {
          JQuickCoordinatorServiceGrpc.getGetQueryStatusMethod = getGetQueryStatusMethod =
              io.grpc.MethodDescriptor.<GetQueryStatusRequest, GetQueryStatusResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetQueryStatus"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  GetQueryStatusRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  GetQueryStatusResponse.getDefaultInstance()))
              .setSchemaDescriptor(new JQuickCoordinatorServiceMethodDescriptorSupplier("GetQueryStatus"))
              .build();
        }
      }
    }
    return getGetQueryStatusMethod;
  }

  private static volatile io.grpc.MethodDescriptor<CancelQueryRequest,
      CancelQueryResponse> getCancelQueryMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "CancelQuery",
      requestType = CancelQueryRequest.class,
      responseType = CancelQueryResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<CancelQueryRequest,
      CancelQueryResponse> getCancelQueryMethod() {
    io.grpc.MethodDescriptor<CancelQueryRequest, CancelQueryResponse> getCancelQueryMethod;
    if ((getCancelQueryMethod = JQuickCoordinatorServiceGrpc.getCancelQueryMethod) == null) {
      synchronized (JQuickCoordinatorServiceGrpc.class) {
        if ((getCancelQueryMethod = JQuickCoordinatorServiceGrpc.getCancelQueryMethod) == null) {
          JQuickCoordinatorServiceGrpc.getCancelQueryMethod = getCancelQueryMethod =
              io.grpc.MethodDescriptor.<CancelQueryRequest, CancelQueryResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "CancelQuery"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  CancelQueryRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  CancelQueryResponse.getDefaultInstance()))
              .setSchemaDescriptor(new JQuickCoordinatorServiceMethodDescriptorSupplier("CancelQuery"))
              .build();
        }
      }
    }
    return getCancelQueryMethod;
  }

  private static volatile io.grpc.MethodDescriptor<GetQueryResultRequest,
      DataChunk> getGetQueryResultMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetQueryResult",
      requestType = GetQueryResultRequest.class,
      responseType = DataChunk.class,
      methodType = io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
  public static io.grpc.MethodDescriptor<GetQueryResultRequest,
      DataChunk> getGetQueryResultMethod() {
    io.grpc.MethodDescriptor<GetQueryResultRequest, DataChunk> getGetQueryResultMethod;
    if ((getGetQueryResultMethod = JQuickCoordinatorServiceGrpc.getGetQueryResultMethod) == null) {
      synchronized (JQuickCoordinatorServiceGrpc.class) {
        if ((getGetQueryResultMethod = JQuickCoordinatorServiceGrpc.getGetQueryResultMethod) == null) {
          JQuickCoordinatorServiceGrpc.getGetQueryResultMethod = getGetQueryResultMethod =
              io.grpc.MethodDescriptor.<GetQueryResultRequest, DataChunk>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetQueryResult"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  GetQueryResultRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  DataChunk.getDefaultInstance()))
              .setSchemaDescriptor(new JQuickCoordinatorServiceMethodDescriptorSupplier("GetQueryResult"))
              .build();
        }
      }
    }
    return getGetQueryResultMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static JQuickCoordinatorServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<JQuickCoordinatorServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<JQuickCoordinatorServiceStub>() {
        @Override
        public JQuickCoordinatorServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new JQuickCoordinatorServiceStub(channel, callOptions);
        }
      };
    return JQuickCoordinatorServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports all types of calls on the service
   */
  public static JQuickCoordinatorServiceBlockingV2Stub newBlockingV2Stub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<JQuickCoordinatorServiceBlockingV2Stub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<JQuickCoordinatorServiceBlockingV2Stub>() {
        @Override
        public JQuickCoordinatorServiceBlockingV2Stub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new JQuickCoordinatorServiceBlockingV2Stub(channel, callOptions);
        }
      };
    return JQuickCoordinatorServiceBlockingV2Stub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static JQuickCoordinatorServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<JQuickCoordinatorServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<JQuickCoordinatorServiceBlockingStub>() {
        @Override
        public JQuickCoordinatorServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new JQuickCoordinatorServiceBlockingStub(channel, callOptions);
        }
      };
    return JQuickCoordinatorServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static JQuickCoordinatorServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<JQuickCoordinatorServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<JQuickCoordinatorServiceFutureStub>() {
        @Override
        public JQuickCoordinatorServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new JQuickCoordinatorServiceFutureStub(channel, callOptions);
        }
      };
    return JQuickCoordinatorServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     * <pre>
     * 提交查询
     * </pre>
     */
    default void submitQuery(SubmitQueryRequest request,
                             io.grpc.stub.StreamObserver<SubmitQueryResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getSubmitQueryMethod(), responseObserver);
    }

    /**
     * <pre>
     * 获取查询状态
     * </pre>
     */
    default void getQueryStatus(GetQueryStatusRequest request,
                                io.grpc.stub.StreamObserver<GetQueryStatusResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetQueryStatusMethod(), responseObserver);
    }

    /**
     * <pre>
     * 取消查询
     * </pre>
     */
    default void cancelQuery(CancelQueryRequest request,
                             io.grpc.stub.StreamObserver<CancelQueryResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getCancelQueryMethod(), responseObserver);
    }

    /**
     * <pre>
     * 获取查询结果
     * </pre>
     */
    default void getQueryResult(GetQueryResultRequest request,
                                io.grpc.stub.StreamObserver<DataChunk> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetQueryResultMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service JQuickCoordinatorService.
   */
  public static abstract class JQuickCoordinatorServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @Override public final io.grpc.ServerServiceDefinition bindService() {
      return JQuickCoordinatorServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service JQuickCoordinatorService.
   */
  public static final class JQuickCoordinatorServiceStub
      extends io.grpc.stub.AbstractAsyncStub<JQuickCoordinatorServiceStub> {
    private JQuickCoordinatorServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected JQuickCoordinatorServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new JQuickCoordinatorServiceStub(channel, callOptions);
    }

    /**
     * <pre>
     * 提交查询
     * </pre>
     */
    public void submitQuery(SubmitQueryRequest request,
                            io.grpc.stub.StreamObserver<SubmitQueryResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getSubmitQueryMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * 获取查询状态
     * </pre>
     */
    public void getQueryStatus(GetQueryStatusRequest request,
                               io.grpc.stub.StreamObserver<GetQueryStatusResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetQueryStatusMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * 取消查询
     * </pre>
     */
    public void cancelQuery(CancelQueryRequest request,
                            io.grpc.stub.StreamObserver<CancelQueryResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getCancelQueryMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     * <pre>
     * 获取查询结果
     * </pre>
     */
    public void getQueryResult(GetQueryResultRequest request,
                               io.grpc.stub.StreamObserver<DataChunk> responseObserver) {
      io.grpc.stub.ClientCalls.asyncServerStreamingCall(
          getChannel().newCall(getGetQueryResultMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service JQuickCoordinatorService.
   */
  public static final class JQuickCoordinatorServiceBlockingV2Stub
      extends io.grpc.stub.AbstractBlockingStub<JQuickCoordinatorServiceBlockingV2Stub> {
    private JQuickCoordinatorServiceBlockingV2Stub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected JQuickCoordinatorServiceBlockingV2Stub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new JQuickCoordinatorServiceBlockingV2Stub(channel, callOptions);
    }

    /**
     * <pre>
     * 提交查询
     * </pre>
     */
    public SubmitQueryResponse submitQuery(SubmitQueryRequest request) throws io.grpc.StatusException {
      return io.grpc.stub.ClientCalls.blockingV2UnaryCall(
          getChannel(), getSubmitQueryMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * 获取查询状态
     * </pre>
     */
    public GetQueryStatusResponse getQueryStatus(GetQueryStatusRequest request) throws io.grpc.StatusException {
      return io.grpc.stub.ClientCalls.blockingV2UnaryCall(
          getChannel(), getGetQueryStatusMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * 取消查询
     * </pre>
     */
    public CancelQueryResponse cancelQuery(CancelQueryRequest request) throws io.grpc.StatusException {
      return io.grpc.stub.ClientCalls.blockingV2UnaryCall(
          getChannel(), getCancelQueryMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * 获取查询结果
     * </pre>
     */
    @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/10918")
    public io.grpc.stub.BlockingClientCall<?, DataChunk>
        getQueryResult(GetQueryResultRequest request) {
      return io.grpc.stub.ClientCalls.blockingV2ServerStreamingCall(
          getChannel(), getGetQueryResultMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do limited synchronous rpc calls to service JQuickCoordinatorService.
   */
  public static final class JQuickCoordinatorServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<JQuickCoordinatorServiceBlockingStub> {
    private JQuickCoordinatorServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected JQuickCoordinatorServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new JQuickCoordinatorServiceBlockingStub(channel, callOptions);
    }

    /**
     * <pre>
     * 提交查询
     * </pre>
     */
    public SubmitQueryResponse submitQuery(SubmitQueryRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getSubmitQueryMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * 获取查询状态
     * </pre>
     */
    public GetQueryStatusResponse getQueryStatus(GetQueryStatusRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetQueryStatusMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * 取消查询
     * </pre>
     */
    public CancelQueryResponse cancelQuery(CancelQueryRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getCancelQueryMethod(), getCallOptions(), request);
    }

    /**
     * <pre>
     * 获取查询结果
     * </pre>
     */
    public java.util.Iterator<DataChunk> getQueryResult(
        GetQueryResultRequest request) {
      return io.grpc.stub.ClientCalls.blockingServerStreamingCall(
          getChannel(), getGetQueryResultMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service JQuickCoordinatorService.
   */
  public static final class JQuickCoordinatorServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<JQuickCoordinatorServiceFutureStub> {
    private JQuickCoordinatorServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @Override
    protected JQuickCoordinatorServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new JQuickCoordinatorServiceFutureStub(channel, callOptions);
    }

    /**
     * <pre>
     * 提交查询
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<SubmitQueryResponse> submitQuery(
        SubmitQueryRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getSubmitQueryMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * 获取查询状态
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<GetQueryStatusResponse> getQueryStatus(
        GetQueryStatusRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetQueryStatusMethod(), getCallOptions()), request);
    }

    /**
     * <pre>
     * 取消查询
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<CancelQueryResponse> cancelQuery(
        CancelQueryRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getCancelQueryMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_SUBMIT_QUERY = 0;
  private static final int METHODID_GET_QUERY_STATUS = 1;
  private static final int METHODID_CANCEL_QUERY = 2;
  private static final int METHODID_GET_QUERY_RESULT = 3;

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
        case METHODID_SUBMIT_QUERY:
          serviceImpl.submitQuery((SubmitQueryRequest) request,
              (io.grpc.stub.StreamObserver<SubmitQueryResponse>) responseObserver);
          break;
        case METHODID_GET_QUERY_STATUS:
          serviceImpl.getQueryStatus((GetQueryStatusRequest) request,
              (io.grpc.stub.StreamObserver<GetQueryStatusResponse>) responseObserver);
          break;
        case METHODID_CANCEL_QUERY:
          serviceImpl.cancelQuery((CancelQueryRequest) request,
              (io.grpc.stub.StreamObserver<CancelQueryResponse>) responseObserver);
          break;
        case METHODID_GET_QUERY_RESULT:
          serviceImpl.getQueryResult((GetQueryResultRequest) request,
              (io.grpc.stub.StreamObserver<DataChunk>) responseObserver);
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
          getSubmitQueryMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              SubmitQueryRequest,
              SubmitQueryResponse>(
                service, METHODID_SUBMIT_QUERY)))
        .addMethod(
          getGetQueryStatusMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              GetQueryStatusRequest,
              GetQueryStatusResponse>(
                service, METHODID_GET_QUERY_STATUS)))
        .addMethod(
          getCancelQueryMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              CancelQueryRequest,
              CancelQueryResponse>(
                service, METHODID_CANCEL_QUERY)))
        .addMethod(
          getGetQueryResultMethod(),
          io.grpc.stub.ServerCalls.asyncServerStreamingCall(
            new MethodHandlers<
              GetQueryResultRequest,
              DataChunk>(
                service, METHODID_GET_QUERY_RESULT)))
        .build();
  }

  private static abstract class JQuickCoordinatorServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    JQuickCoordinatorServiceBaseDescriptorSupplier() {}

    @Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return JQuickPhysicalPlanProto.getDescriptor();
    }

    @Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("JQuickCoordinatorService");
    }
  }

  private static final class JQuickCoordinatorServiceFileDescriptorSupplier
      extends JQuickCoordinatorServiceBaseDescriptorSupplier {
    JQuickCoordinatorServiceFileDescriptorSupplier() {}
  }

  private static final class JQuickCoordinatorServiceMethodDescriptorSupplier
      extends JQuickCoordinatorServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    JQuickCoordinatorServiceMethodDescriptorSupplier(String methodName) {
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
      synchronized (JQuickCoordinatorServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new JQuickCoordinatorServiceFileDescriptorSupplier())
              .addMethod(getSubmitQueryMethod())
              .addMethod(getGetQueryStatusMethod())
              .addMethod(getCancelQueryMethod())
              .addMethod(getGetQueryResultMethod())
              .build();
        }
      }
    }
    return result;
  }
}
