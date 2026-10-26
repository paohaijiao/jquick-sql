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

import com.github.paohaijiao.proto.JQuickCoordinatorServiceGrpc;
import com.github.paohaijiao.proto.JQuickDataDistributionServiceGrpc;
import com.github.paohaijiao.proto.JQuickPhysicalPlanServiceGrpc;
import com.github.paohaijiao.proto.JQuickTableServiceGrpc;

/**
 * 分布式执行层 gRPC 服务名常量
 * <p>
 * 服务名统一取自 jquick-sql-proto 生成的 {@code SERVICE_NAME} 常量（即 proto 中服务全限定名），
 * 服务端注册与 JQuick 网关 / 注册中心发现使用同一份名称，避免两端不一致导致的 UNIMPLEMENTED。
 *
 * @author Martin
 * @version 1.0.0
 * @since 2026/10/26
 */
public final class JQuickGrpcServiceNames {

    /**
     * 物理计划执行服务（Worker 对外提供任务执行 / 取消 / 心跳 / 注册）
     */
    public static final String PHYSICAL_PLAN = JQuickPhysicalPlanServiceGrpc.SERVICE_NAME;

    /**
     * 数据分发服务（Worker 之间 shuffle / broadcast 数据搬运）
     */
    public static final String DATA_DISTRIBUTION = JQuickDataDistributionServiceGrpc.SERVICE_NAME;

    /**
     * 表注册服务（远程建表 / 删表）
     */
    public static final String TABLE = JQuickTableServiceGrpc.SERVICE_NAME;

    /**
     * 协调器服务（集群入口，供网关与客户端提交查询）
     */
    public static final String COORDINATOR = JQuickCoordinatorServiceGrpc.SERVICE_NAME;

    private JQuickGrpcServiceNames() {
    }
}
