package com.github.paohaijiao.proto;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 * <pre>
 * 协调器服务（客户端端）
 * </pre>
 */
@io.grpc.stub.annotations.GrpcGenerated
public final class JQuickCoordinatorServiceGrpc {

  private JQuickCoordinatorServiceGrpc() {}

  public static final String SERVICE_NAME = "com.github.paohaijiao.proto.JQuickCoordinatorService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<JQuickSubmitQueryRequest,
      JQuickSubmitQueryResponse> getSubmitQueryMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "SubmitQuery",
      requestType = JQuickSubmitQueryRequest.class,
      responseType = JQuickSubmitQueryResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<JQuickSubmitQueryRequest,
      JQuickSubmitQueryResponse> getSubmitQueryMethod() {
    io.grpc.MethodDescriptor<JQuickSubmitQueryRequest, JQuickSubmitQueryResponse> getSubmitQueryMethod;
    if ((getSubmitQueryMethod = JQuickCoordinatorServiceGrpc.getSubmitQueryMethod) == null) {
      synchronized (JQuickCoordinatorServiceGrpc.class) {
        if ((getSubmitQueryMethod = JQuickCoordinatorServiceGrpc.getSubmitQueryMethod) == null) {
          JQuickCoordinatorServiceGrpc.getSubmitQueryMethod = getSubmitQueryMethod =
              io.grpc.MethodDescriptor.<JQuickSubmitQueryRequest, JQuickSubmitQueryResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "SubmitQuery"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  JQuickSubmitQueryRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  JQuickSubmitQueryResponse.getDefaultInstance()))
              .setSchemaDescriptor(new JQuickCoordinatorServiceMethodDescriptorSupplier("SubmitQuery"))
              .build();
        }
      }
    }
    return getSubmitQueryMethod;
  }

  private static volatile io.grpc.MethodDescriptor<JQuickQueryStatusRequest,
      JQuickQueryStatusResponse> getGetQueryStatusMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetQueryStatus",
      requestType = JQuickQueryStatusRequest.class,
      responseType = JQuickQueryStatusResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<JQuickQueryStatusRequest,
      JQuickQueryStatusResponse> getGetQueryStatusMethod() {
    io.grpc.MethodDescriptor<JQuickQueryStatusRequest, JQuickQueryStatusResponse> getGetQueryStatusMethod;
    if ((getGetQueryStatusMethod = JQuickCoordinatorServiceGrpc.getGetQueryStatusMethod) == null) {
      synchronized (JQuickCoordinatorServiceGrpc.class) {
        if ((getGetQueryStatusMethod = JQuickCoordinatorServiceGrpc.getGetQueryStatusMethod) == null) {
          JQuickCoordinatorServiceGrpc.getGetQueryStatusMethod = getGetQueryStatusMethod =
              io.grpc.MethodDescriptor.<JQuickQueryStatusRequest, JQuickQueryStatusResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetQueryStatus"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  JQuickQueryStatusRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  JQuickQueryStatusResponse.getDefaultInstance()))
              .setSchemaDescriptor(new JQuickCoordinatorServiceMethodDescriptorSupplier("GetQueryStatus"))
              .build();
        }
      }
    }
    return getGetQueryStatusMethod;
  }

  private static volatile io.grpc.MethodDescriptor<JQuickCancelQueryRequest,
      JQuickCancelQueryResponse> getCancelQueryMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "CancelQuery",
      requestType = JQuickCancelQueryRequest.class,
      responseType = JQuickCancelQueryResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<JQuickCancelQueryRequest,
      JQuickCancelQueryResponse> getCancelQueryMethod() {
    io.grpc.MethodDescriptor<JQuickCancelQueryRequest, JQuickCancelQueryResponse> getCancelQueryMethod;
    if ((getCancelQueryMethod = JQuickCoordinatorServiceGrpc.getCancelQueryMethod) == null) {
      synchronized (JQuickCoordinatorServiceGrpc.class) {
        if ((getCancelQueryMethod = JQuickCoordinatorServiceGrpc.getCancelQueryMethod) == null) {
          JQuickCoordinatorServiceGrpc.getCancelQueryMethod = getCancelQueryMethod =
              io.grpc.MethodDescriptor.<JQuickCancelQueryRequest, JQuickCancelQueryResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "CancelQuery"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  JQuickCancelQueryRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  JQuickCancelQueryResponse.getDefaultInstance()))
              .setSchemaDescriptor(new JQuickCoordinatorServiceMethodDescriptorSupplier("CancelQuery"))
              .build();
        }
      }
    }
    return getCancelQueryMethod;
  }

  private static volatile io.grpc.MethodDescriptor<JQuickGetResultRequest,
      JQuickDataChunkProto> getGetQueryResultMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetQueryResult",
      requestType = JQuickGetResultRequest.class,
      responseType = JQuickDataChunkProto.class,
      methodType = io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
  public static io.grpc.MethodDescriptor<JQuickGetResultRequest,
      JQuickDataChunkProto> getGetQueryResultMethod() {
    io.grpc.MethodDescriptor<JQuickGetResultRequest, JQuickDataChunkProto> getGetQueryResultMethod;
    if ((getGetQueryResultMethod = JQuickCoordinatorServiceGrpc.getGetQueryResultMethod) == null) {
      synchronized (JQuickCoordinatorServiceGrpc.class) {
        if ((getGetQueryResultMethod = JQuickCoordinatorServiceGrpc.getGetQueryResultMethod) == null) {
          JQuickCoordinatorServiceGrpc.getGetQueryResultMethod = getGetQueryResultMethod =
              io.grpc.MethodDescriptor.<JQuickGetResultRequest, JQuickDataChunkProto>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetQueryResult"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  JQuickGetResultRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  JQuickDataChunkProto.getDefaultInstance()))
              .setSchemaDescriptor(new JQuickCoordinatorServiceMethodDescriptorSupplier("GetQueryResult"))
              .build();
        }
      }
    }
    return getGetQueryResultMethod;
  }

  private static volatile io.grpc.MethodDescriptor<JQuickHeartbeatRequest,
      JQuickHeartbeatResponse> getHeartbeatMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "Heartbeat",
      requestType = JQuickHeartbeatRequest.class,
      responseType = JQuickHeartbeatResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<JQuickHeartbeatRequest,
      JQuickHeartbeatResponse> getHeartbeatMethod() {
    io.grpc.MethodDescriptor<JQuickHeartbeatRequest, JQuickHeartbeatResponse> getHeartbeatMethod;
    if ((getHeartbeatMethod = JQuickCoordinatorServiceGrpc.getHeartbeatMethod) == null) {
      synchronized (JQuickCoordinatorServiceGrpc.class) {
        if ((getHeartbeatMethod = JQuickCoordinatorServiceGrpc.getHeartbeatMethod) == null) {
          JQuickCoordinatorServiceGrpc.getHeartbeatMethod = getHeartbeatMethod =
              io.grpc.MethodDescriptor.<JQuickHeartbeatRequest, JQuickHeartbeatResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "Heartbeat"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  JQuickHeartbeatRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  JQuickHeartbeatResponse.getDefaultInstance()))
              .setSchemaDescriptor(new JQuickCoordinatorServiceMethodDescriptorSupplier("Heartbeat"))
              .build();
        }
      }
    }
    return getHeartbeatMethod;
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
   * <pre>
   * 协调器服务（客户端端）
   * </pre>
   */
  public interface AsyncService {

    /**
     */
    default void submitQuery(JQuickSubmitQueryRequest request,
                             io.grpc.stub.StreamObserver<JQuickSubmitQueryResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getSubmitQueryMethod(), responseObserver);
    }

    /**
     */
    default void getQueryStatus(JQuickQueryStatusRequest request,
                                io.grpc.stub.StreamObserver<JQuickQueryStatusResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetQueryStatusMethod(), responseObserver);
    }

    /**
     */
    default void cancelQuery(JQuickCancelQueryRequest request,
                             io.grpc.stub.StreamObserver<JQuickCancelQueryResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getCancelQueryMethod(), responseObserver);
    }

    /**
     */
    default void getQueryResult(JQuickGetResultRequest request,
                                io.grpc.stub.StreamObserver<JQuickDataChunkProto> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetQueryResultMethod(), responseObserver);
    }

    /**
     */
    default void heartbeat(JQuickHeartbeatRequest request,
                           io.grpc.stub.StreamObserver<JQuickHeartbeatResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getHeartbeatMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service JQuickCoordinatorService.
   * <pre>
   * 协调器服务（客户端端）
   * </pre>
   */
  public static abstract class JQuickCoordinatorServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @Override public final io.grpc.ServerServiceDefinition bindService() {
      return JQuickCoordinatorServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service JQuickCoordinatorService.
   * <pre>
   * 协调器服务（客户端端）
   * </pre>
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
     */
    public void submitQuery(JQuickSubmitQueryRequest request,
                            io.grpc.stub.StreamObserver<JQuickSubmitQueryResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getSubmitQueryMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getQueryStatus(JQuickQueryStatusRequest request,
                               io.grpc.stub.StreamObserver<JQuickQueryStatusResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetQueryStatusMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void cancelQuery(JQuickCancelQueryRequest request,
                            io.grpc.stub.StreamObserver<JQuickCancelQueryResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getCancelQueryMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void getQueryResult(JQuickGetResultRequest request,
                               io.grpc.stub.StreamObserver<JQuickDataChunkProto> responseObserver) {
      io.grpc.stub.ClientCalls.asyncServerStreamingCall(
          getChannel().newCall(getGetQueryResultMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void heartbeat(JQuickHeartbeatRequest request,
                          io.grpc.stub.StreamObserver<JQuickHeartbeatResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getHeartbeatMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service JQuickCoordinatorService.
   * <pre>
   * 协调器服务（客户端端）
   * </pre>
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
     */
    public JQuickSubmitQueryResponse submitQuery(JQuickSubmitQueryRequest request) throws io.grpc.StatusException {
      return io.grpc.stub.ClientCalls.blockingV2UnaryCall(
          getChannel(), getSubmitQueryMethod(), getCallOptions(), request);
    }

    /**
     */
    public JQuickQueryStatusResponse getQueryStatus(JQuickQueryStatusRequest request) throws io.grpc.StatusException {
      return io.grpc.stub.ClientCalls.blockingV2UnaryCall(
          getChannel(), getGetQueryStatusMethod(), getCallOptions(), request);
    }

    /**
     */
    public JQuickCancelQueryResponse cancelQuery(JQuickCancelQueryRequest request) throws io.grpc.StatusException {
      return io.grpc.stub.ClientCalls.blockingV2UnaryCall(
          getChannel(), getCancelQueryMethod(), getCallOptions(), request);
    }

    /**
     */
    @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/10918")
    public io.grpc.stub.BlockingClientCall<?, JQuickDataChunkProto>
        getQueryResult(JQuickGetResultRequest request) {
      return io.grpc.stub.ClientCalls.blockingV2ServerStreamingCall(
          getChannel(), getGetQueryResultMethod(), getCallOptions(), request);
    }

    /**
     */
    public JQuickHeartbeatResponse heartbeat(JQuickHeartbeatRequest request) throws io.grpc.StatusException {
      return io.grpc.stub.ClientCalls.blockingV2UnaryCall(
          getChannel(), getHeartbeatMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do limited synchronous rpc calls to service JQuickCoordinatorService.
   * <pre>
   * 协调器服务（客户端端）
   * </pre>
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
     */
    public JQuickSubmitQueryResponse submitQuery(JQuickSubmitQueryRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getSubmitQueryMethod(), getCallOptions(), request);
    }

    /**
     */
    public JQuickQueryStatusResponse getQueryStatus(JQuickQueryStatusRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetQueryStatusMethod(), getCallOptions(), request);
    }

    /**
     */
    public JQuickCancelQueryResponse cancelQuery(JQuickCancelQueryRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getCancelQueryMethod(), getCallOptions(), request);
    }

    /**
     */
    public java.util.Iterator<JQuickDataChunkProto> getQueryResult(
        JQuickGetResultRequest request) {
      return io.grpc.stub.ClientCalls.blockingServerStreamingCall(
          getChannel(), getGetQueryResultMethod(), getCallOptions(), request);
    }

    /**
     */
    public JQuickHeartbeatResponse heartbeat(JQuickHeartbeatRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getHeartbeatMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service JQuickCoordinatorService.
   * <pre>
   * 协调器服务（客户端端）
   * </pre>
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
     */
    public com.google.common.util.concurrent.ListenableFuture<JQuickSubmitQueryResponse> submitQuery(
        JQuickSubmitQueryRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getSubmitQueryMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<JQuickQueryStatusResponse> getQueryStatus(
        JQuickQueryStatusRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetQueryStatusMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<JQuickCancelQueryResponse> cancelQuery(
        JQuickCancelQueryRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getCancelQueryMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<JQuickHeartbeatResponse> heartbeat(
        JQuickHeartbeatRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getHeartbeatMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_SUBMIT_QUERY = 0;
  private static final int METHODID_GET_QUERY_STATUS = 1;
  private static final int METHODID_CANCEL_QUERY = 2;
  private static final int METHODID_GET_QUERY_RESULT = 3;
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
        case METHODID_SUBMIT_QUERY:
          serviceImpl.submitQuery((JQuickSubmitQueryRequest) request,
              (io.grpc.stub.StreamObserver<JQuickSubmitQueryResponse>) responseObserver);
          break;
        case METHODID_GET_QUERY_STATUS:
          serviceImpl.getQueryStatus((JQuickQueryStatusRequest) request,
              (io.grpc.stub.StreamObserver<JQuickQueryStatusResponse>) responseObserver);
          break;
        case METHODID_CANCEL_QUERY:
          serviceImpl.cancelQuery((JQuickCancelQueryRequest) request,
              (io.grpc.stub.StreamObserver<JQuickCancelQueryResponse>) responseObserver);
          break;
        case METHODID_GET_QUERY_RESULT:
          serviceImpl.getQueryResult((JQuickGetResultRequest) request,
              (io.grpc.stub.StreamObserver<JQuickDataChunkProto>) responseObserver);
          break;
        case METHODID_HEARTBEAT:
          serviceImpl.heartbeat((JQuickHeartbeatRequest) request,
              (io.grpc.stub.StreamObserver<JQuickHeartbeatResponse>) responseObserver);
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
              JQuickSubmitQueryRequest,
              JQuickSubmitQueryResponse>(
                service, METHODID_SUBMIT_QUERY)))
        .addMethod(
          getGetQueryStatusMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              JQuickQueryStatusRequest,
              JQuickQueryStatusResponse>(
                service, METHODID_GET_QUERY_STATUS)))
        .addMethod(
          getCancelQueryMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              JQuickCancelQueryRequest,
              JQuickCancelQueryResponse>(
                service, METHODID_CANCEL_QUERY)))
        .addMethod(
          getGetQueryResultMethod(),
          io.grpc.stub.ServerCalls.asyncServerStreamingCall(
            new MethodHandlers<
              JQuickGetResultRequest,
              JQuickDataChunkProto>(
                service, METHODID_GET_QUERY_RESULT)))
        .addMethod(
          getHeartbeatMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              JQuickHeartbeatRequest,
              JQuickHeartbeatResponse>(
                service, METHODID_HEARTBEAT)))
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
              .addMethod(getHeartbeatMethod())
              .build();
        }
      }
    }
    return result;
  }
}
