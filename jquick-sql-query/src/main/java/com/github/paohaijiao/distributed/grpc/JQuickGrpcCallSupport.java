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
package com.github.paohaijiao.distributed.grpc;

import com.github.paohaijiao.console.JConsole;
import com.github.paohaijiao.grpc.context.JQuickGrpcContext;
import io.grpc.stub.StreamObserver;

/**
 * 服务端调用支撑工具
 * <p>
 * 收敛两件原本散落在各服务实现中的样板逻辑：
 * <ul>
 *     <li>响应观察者的 onNext + onCompleted 成对调用模板，避免漏调 onCompleted 造成调用方挂起；</li>
 *     <li>调用上下文（traceId）附着，使用 jquick-grpc 的 {@link JQuickGrpcContext}，
 *         与 JQuick 网关 / 注册中心的链路标识保持一致。</li>
 * </ul>
 * 说明：{@code StreamObserver} 由 proto 生成的服务契约固定（方法签名由 protoc-gen-grpc-java 决定），
 * jquick-grpc 1.4.0 未提供替代包装接口，故此处作为唯一收口点集中使用。
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/10/26
 */
public final class JQuickGrpcCallSupport {

    private static final JConsole console = JConsole.initConsoleEnvironment();

    private JQuickGrpcCallSupport() {
    }

    /**
     * 附着调用上下文，try-with-resources 自动释放
     *
     * @param traceId 链路标识，建议使用 queryId / taskId
     */
    public static JQuickGrpcContext.Scope trace(String traceId) {
        if (traceId == null || traceId.isEmpty()) {
            return JQuickGrpcContext.create().attach();
        }
        return JQuickGrpcContext.create().withTraceId(traceId).attach();
    }

    /**
     * 正常返回：onNext + onCompleted
     */
    public static <T> void complete(StreamObserver<T> responseObserver, T response) {
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    /**
     * 异常返回：统一记录日志后交给 gRPC 状态传播
     */
    public static <T> void fail(StreamObserver<T> responseObserver, String message, Throwable cause) {
        console.error(message, cause);
        responseObserver.onError(cause);
    }
}
