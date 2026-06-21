package com.github.paohaijiao.proto;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 * <pre>
 * 数据分发服务（Worker 间直接通信）
 * </pre>
 */
@io.grpc.stub.annotations.GrpcGenerated
public final class JQuickDataDistributionServiceGrpc {

  private JQuickDataDistributionServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "com.github.paohaijiao.proto.JQuickDataDistributionService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.github.paohaijiao.proto.JQuickDataChunkProto,
      com.github.paohaijiao.proto.JQuickEmptyNodeProto> getSendDataMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "SendData",
      requestType = com.github.paohaijiao.proto.JQuickDataChunkProto.class,
      responseType = com.github.paohaijiao.proto.JQuickEmptyNodeProto.class,
      methodType = io.grpc.MethodDescriptor.MethodType.CLIENT_STREAMING)
  public static io.grpc.MethodDescriptor<com.github.paohaijiao.proto.JQuickDataChunkProto,
      com.github.paohaijiao.proto.JQuickEmptyNodeProto> getSendDataMethod() {
    io.grpc.MethodDescriptor<com.github.paohaijiao.proto.JQuickDataChunkProto, com.github.paohaijiao.proto.JQuickEmptyNodeProto> getSendDataMethod;
    if ((getSendDataMethod = JQuickDataDistributionServiceGrpc.getSendDataMethod) == null) {
      synchronized (JQuickDataDistributionServiceGrpc.class) {
        if ((getSendDataMethod = JQuickDataDistributionServiceGrpc.getSendDataMethod) == null) {
          JQuickDataDistributionServiceGrpc.getSendDataMethod = getSendDataMethod =
              io.grpc.MethodDescriptor.<com.github.paohaijiao.proto.JQuickDataChunkProto, com.github.paohaijiao.proto.JQuickEmptyNodeProto>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.CLIENT_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "SendData"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.github.paohaijiao.proto.JQuickDataChunkProto.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.github.paohaijiao.proto.JQuickEmptyNodeProto.getDefaultInstance()))
              .setSchemaDescriptor(new JQuickDataDistributionServiceMethodDescriptorSupplier("SendData"))
              .build();
        }
      }
    }
    return getSendDataMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.github.paohaijiao.proto.JQuickFetchDataRequest,
      com.github.paohaijiao.proto.JQuickFetchDataResponse> getReceiveDataMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ReceiveData",
      requestType = com.github.paohaijiao.proto.JQuickFetchDataRequest.class,
      responseType = com.github.paohaijiao.proto.JQuickFetchDataResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
  public static io.grpc.MethodDescriptor<com.github.paohaijiao.proto.JQuickFetchDataRequest,
      com.github.paohaijiao.proto.JQuickFetchDataResponse> getReceiveDataMethod() {
    io.grpc.MethodDescriptor<com.github.paohaijiao.proto.JQuickFetchDataRequest, com.github.paohaijiao.proto.JQuickFetchDataResponse> getReceiveDataMethod;
    if ((getReceiveDataMethod = JQuickDataDistributionServiceGrpc.getReceiveDataMethod) == null) {
      synchronized (JQuickDataDistributionServiceGrpc.class) {
        if ((getReceiveDataMethod = JQuickDataDistributionServiceGrpc.getReceiveDataMethod) == null) {
          JQuickDataDistributionServiceGrpc.getReceiveDataMethod = getReceiveDataMethod =
              io.grpc.MethodDescriptor.<com.github.paohaijiao.proto.JQuickFetchDataRequest, com.github.paohaijiao.proto.JQuickFetchDataResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ReceiveData"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.github.paohaijiao.proto.JQuickFetchDataRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.github.paohaijiao.proto.JQuickFetchDataResponse.getDefaultInstance()))
              .setSchemaDescriptor(new JQuickDataDistributionServiceMethodDescriptorSupplier("ReceiveData"))
              .build();
        }
      }
    }
    return getReceiveDataMethod;
  }

  private static volatile io.grpc.MethodDescriptor<com.github.paohaijiao.proto.JQuickDataChunkProto,
      com.github.paohaijiao.proto.JQuickBroadcastResponse> getBroadcastDataMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "BroadcastData",
      requestType = com.github.paohaijiao.proto.JQuickDataChunkProto.class,
      responseType = com.github.paohaijiao.proto.JQuickBroadcastResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.CLIENT_STREAMING)
  public static io.grpc.MethodDescriptor<com.github.paohaijiao.proto.JQuickDataChunkProto,
      com.github.paohaijiao.proto.JQuickBroadcastResponse> getBroadcastDataMethod() {
    io.grpc.MethodDescriptor<com.github.paohaijiao.proto.JQuickDataChunkProto, com.github.paohaijiao.proto.JQuickBroadcastResponse> getBroadcastDataMethod;
    if ((getBroadcastDataMethod = JQuickDataDistributionServiceGrpc.getBroadcastDataMethod) == null) {
      synchronized (JQuickDataDistributionServiceGrpc.class) {
        if ((getBroadcastDataMethod = JQuickDataDistributionServiceGrpc.getBroadcastDataMethod) == null) {
          JQuickDataDistributionServiceGrpc.getBroadcastDataMethod = getBroadcastDataMethod =
              io.grpc.MethodDescriptor.<com.github.paohaijiao.proto.JQuickDataChunkProto, com.github.paohaijiao.proto.JQuickBroadcastResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.CLIENT_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "BroadcastData"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.github.paohaijiao.proto.JQuickDataChunkProto.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.github.paohaijiao.proto.JQuickBroadcastResponse.getDefaultInstance()))
              .setSchemaDescriptor(new JQuickDataDistributionServiceMethodDescriptorSupplier("BroadcastData"))
              .build();
        }
      }
    }
    return getBroadcastDataMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static JQuickDataDistributionServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<JQuickDataDistributionServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<JQuickDataDistributionServiceStub>() {
        @java.lang.Override
        public JQuickDataDistributionServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new JQuickDataDistributionServiceStub(channel, callOptions);
        }
      };
    return JQuickDataDistributionServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports all types of calls on the service
   */
  public static JQuickDataDistributionServiceBlockingV2Stub newBlockingV2Stub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<JQuickDataDistributionServiceBlockingV2Stub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<JQuickDataDistributionServiceBlockingV2Stub>() {
        @java.lang.Override
        public JQuickDataDistributionServiceBlockingV2Stub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new JQuickDataDistributionServiceBlockingV2Stub(channel, callOptions);
        }
      };
    return JQuickDataDistributionServiceBlockingV2Stub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static JQuickDataDistributionServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<JQuickDataDistributionServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<JQuickDataDistributionServiceBlockingStub>() {
        @java.lang.Override
        public JQuickDataDistributionServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new JQuickDataDistributionServiceBlockingStub(channel, callOptions);
        }
      };
    return JQuickDataDistributionServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static JQuickDataDistributionServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<JQuickDataDistributionServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<JQuickDataDistributionServiceFutureStub>() {
        @java.lang.Override
        public JQuickDataDistributionServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new JQuickDataDistributionServiceFutureStub(channel, callOptions);
        }
      };
    return JQuickDataDistributionServiceFutureStub.newStub(factory, channel);
  }

  /**
   * <pre>
   * 数据分发服务（Worker 间直接通信）
   * </pre>
   */
  public interface AsyncService {

    /**
     */
    default io.grpc.stub.StreamObserver<com.github.paohaijiao.proto.JQuickDataChunkProto> sendData(
        io.grpc.stub.StreamObserver<com.github.paohaijiao.proto.JQuickEmptyNodeProto> responseObserver) {
      return io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall(getSendDataMethod(), responseObserver);
    }

    /**
     */
    default void receiveData(com.github.paohaijiao.proto.JQuickFetchDataRequest request,
        io.grpc.stub.StreamObserver<com.github.paohaijiao.proto.JQuickFetchDataResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getReceiveDataMethod(), responseObserver);
    }

    /**
     */
    default io.grpc.stub.StreamObserver<com.github.paohaijiao.proto.JQuickDataChunkProto> broadcastData(
        io.grpc.stub.StreamObserver<com.github.paohaijiao.proto.JQuickBroadcastResponse> responseObserver) {
      return io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall(getBroadcastDataMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service JQuickDataDistributionService.
   * <pre>
   * 数据分发服务（Worker 间直接通信）
   * </pre>
   */
  public static abstract class JQuickDataDistributionServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return JQuickDataDistributionServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service JQuickDataDistributionService.
   * <pre>
   * 数据分发服务（Worker 间直接通信）
   * </pre>
   */
  public static final class JQuickDataDistributionServiceStub
      extends io.grpc.stub.AbstractAsyncStub<JQuickDataDistributionServiceStub> {
    private JQuickDataDistributionServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected JQuickDataDistributionServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new JQuickDataDistributionServiceStub(channel, callOptions);
    }

    /**
     */
    public io.grpc.stub.StreamObserver<com.github.paohaijiao.proto.JQuickDataChunkProto> sendData(
        io.grpc.stub.StreamObserver<com.github.paohaijiao.proto.JQuickEmptyNodeProto> responseObserver) {
      return io.grpc.stub.ClientCalls.asyncClientStreamingCall(
          getChannel().newCall(getSendDataMethod(), getCallOptions()), responseObserver);
    }

    /**
     */
    public void receiveData(com.github.paohaijiao.proto.JQuickFetchDataRequest request,
        io.grpc.stub.StreamObserver<com.github.paohaijiao.proto.JQuickFetchDataResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncServerStreamingCall(
          getChannel().newCall(getReceiveDataMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public io.grpc.stub.StreamObserver<com.github.paohaijiao.proto.JQuickDataChunkProto> broadcastData(
        io.grpc.stub.StreamObserver<com.github.paohaijiao.proto.JQuickBroadcastResponse> responseObserver) {
      return io.grpc.stub.ClientCalls.asyncClientStreamingCall(
          getChannel().newCall(getBroadcastDataMethod(), getCallOptions()), responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service JQuickDataDistributionService.
   * <pre>
   * 数据分发服务（Worker 间直接通信）
   * </pre>
   */
  public static final class JQuickDataDistributionServiceBlockingV2Stub
      extends io.grpc.stub.AbstractBlockingStub<JQuickDataDistributionServiceBlockingV2Stub> {
    private JQuickDataDistributionServiceBlockingV2Stub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected JQuickDataDistributionServiceBlockingV2Stub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new JQuickDataDistributionServiceBlockingV2Stub(channel, callOptions);
    }

    /**
     */
    @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/10918")
    public io.grpc.stub.BlockingClientCall<com.github.paohaijiao.proto.JQuickDataChunkProto, com.github.paohaijiao.proto.JQuickEmptyNodeProto>
        sendData() {
      return io.grpc.stub.ClientCalls.blockingClientStreamingCall(
          getChannel(), getSendDataMethod(), getCallOptions());
    }

    /**
     */
    @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/10918")
    public io.grpc.stub.BlockingClientCall<?, com.github.paohaijiao.proto.JQuickFetchDataResponse>
        receiveData(com.github.paohaijiao.proto.JQuickFetchDataRequest request) {
      return io.grpc.stub.ClientCalls.blockingV2ServerStreamingCall(
          getChannel(), getReceiveDataMethod(), getCallOptions(), request);
    }

    /**
     */
    @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/10918")
    public io.grpc.stub.BlockingClientCall<com.github.paohaijiao.proto.JQuickDataChunkProto, com.github.paohaijiao.proto.JQuickBroadcastResponse>
        broadcastData() {
      return io.grpc.stub.ClientCalls.blockingClientStreamingCall(
          getChannel(), getBroadcastDataMethod(), getCallOptions());
    }
  }

  /**
   * A stub to allow clients to do limited synchronous rpc calls to service JQuickDataDistributionService.
   * <pre>
   * 数据分发服务（Worker 间直接通信）
   * </pre>
   */
  public static final class JQuickDataDistributionServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<JQuickDataDistributionServiceBlockingStub> {
    private JQuickDataDistributionServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected JQuickDataDistributionServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new JQuickDataDistributionServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public java.util.Iterator<com.github.paohaijiao.proto.JQuickFetchDataResponse> receiveData(
        com.github.paohaijiao.proto.JQuickFetchDataRequest request) {
      return io.grpc.stub.ClientCalls.blockingServerStreamingCall(
          getChannel(), getReceiveDataMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service JQuickDataDistributionService.
   * <pre>
   * 数据分发服务（Worker 间直接通信）
   * </pre>
   */
  public static final class JQuickDataDistributionServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<JQuickDataDistributionServiceFutureStub> {
    private JQuickDataDistributionServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected JQuickDataDistributionServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new JQuickDataDistributionServiceFutureStub(channel, callOptions);
    }
  }

  private static final int METHODID_RECEIVE_DATA = 0;
  private static final int METHODID_SEND_DATA = 1;
  private static final int METHODID_BROADCAST_DATA = 2;

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
        case METHODID_RECEIVE_DATA:
          serviceImpl.receiveData((com.github.paohaijiao.proto.JQuickFetchDataRequest) request,
              (io.grpc.stub.StreamObserver<com.github.paohaijiao.proto.JQuickFetchDataResponse>) responseObserver);
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
        case METHODID_SEND_DATA:
          return (io.grpc.stub.StreamObserver<Req>) serviceImpl.sendData(
              (io.grpc.stub.StreamObserver<com.github.paohaijiao.proto.JQuickEmptyNodeProto>) responseObserver);
        case METHODID_BROADCAST_DATA:
          return (io.grpc.stub.StreamObserver<Req>) serviceImpl.broadcastData(
              (io.grpc.stub.StreamObserver<com.github.paohaijiao.proto.JQuickBroadcastResponse>) responseObserver);
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getSendDataMethod(),
          io.grpc.stub.ServerCalls.asyncClientStreamingCall(
            new MethodHandlers<
              com.github.paohaijiao.proto.JQuickDataChunkProto,
              com.github.paohaijiao.proto.JQuickEmptyNodeProto>(
                service, METHODID_SEND_DATA)))
        .addMethod(
          getReceiveDataMethod(),
          io.grpc.stub.ServerCalls.asyncServerStreamingCall(
            new MethodHandlers<
              com.github.paohaijiao.proto.JQuickFetchDataRequest,
              com.github.paohaijiao.proto.JQuickFetchDataResponse>(
                service, METHODID_RECEIVE_DATA)))
        .addMethod(
          getBroadcastDataMethod(),
          io.grpc.stub.ServerCalls.asyncClientStreamingCall(
            new MethodHandlers<
              com.github.paohaijiao.proto.JQuickDataChunkProto,
              com.github.paohaijiao.proto.JQuickBroadcastResponse>(
                service, METHODID_BROADCAST_DATA)))
        .build();
  }

  private static abstract class JQuickDataDistributionServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    JQuickDataDistributionServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.github.paohaijiao.proto.JQuickPhysicalPlanProto.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("JQuickDataDistributionService");
    }
  }

  private static final class JQuickDataDistributionServiceFileDescriptorSupplier
      extends JQuickDataDistributionServiceBaseDescriptorSupplier {
    JQuickDataDistributionServiceFileDescriptorSupplier() {}
  }

  private static final class JQuickDataDistributionServiceMethodDescriptorSupplier
      extends JQuickDataDistributionServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    JQuickDataDistributionServiceMethodDescriptorSupplier(java.lang.String methodName) {
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
      synchronized (JQuickDataDistributionServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new JQuickDataDistributionServiceFileDescriptorSupplier())
              .addMethod(getSendDataMethod())
              .addMethod(getReceiveDataMethod())
              .addMethod(getBroadcastDataMethod())
              .build();
        }
      }
    }
    return result;
  }
}
