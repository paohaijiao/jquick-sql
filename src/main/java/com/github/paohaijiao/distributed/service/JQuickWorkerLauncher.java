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
package com.github.paohaijiao.distributed.service;


import com.github.paohaijiao.datasource.JQuickDataSourceManager;
import com.github.paohaijiao.grpc.config.JQuickGrpcServerConfig;
import com.github.paohaijiao.grpc.discovery.impl.JQuickGrpcLocalDiscovery;
import com.github.paohaijiao.grpc.factory.JQuickGrpcDynamicFactory;
import com.github.paohaijiao.grpc.server.JQuickGrpcServer;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;

/**
 * Worker 启动类
 */
public class JQuickWorkerLauncher {

    public static void main(String[] args) throws Exception {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 9090;
        String workerId = "worker-" + port;
        System.out.println("Starting Worker: " + workerId + " on port " + port);
        //准备测试数据
        prepareTestData();
        //  创建服务发现（注册自己）
        JQuickGrpcLocalDiscovery discovery = new JQuickGrpcLocalDiscovery();
        discovery.registerService("FragmentService", "localhost", port, 1);
        // 创建 gRPC 服务器
        JQuickGrpcServerConfig serverConfig = new JQuickGrpcServerConfig();
        serverConfig.setPort(port);
        serverConfig.setUsePlaintext(true);
        JQuickGrpcDynamicFactory factory = new JQuickGrpcDynamicFactory();
        JQuickGrpcServer server = factory.createServer(serverConfig);
        // 注册 Fragment 服务
        server.registerService(new JQuickFragmentGrpcService());
        //启动服务器
        server.start();
        System.out.println("Worker " + workerId + " started successfully");
        //等待关闭
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down worker...");
            server.stop();
            discovery.close();
        }));
    }

    private static void prepareTestData() {
        JQuickDataSet.Builder builder = JQuickDataSet.builder();
        builder.addColumn("id", Integer.class, "test");
        builder.addColumn("name", String.class, "test");
        builder.addColumn("age", Integer.class, "test");
        for (int i = 1; i <= 1000; i++) {
            JQuickRow row = new JQuickRow();
            row.put("id", i);
            row.put("name", "user_" + i);
            row.put("age", 20 + (i % 30));
            builder.addRow(row);
        }
        JQuickDataSourceManager.registerTable("users", builder.build());
        JQuickDataSourceManager.registerTable("orders", builder.build());
        System.out.println("Test data prepared: 1000 rows in 'users' and 'orders' tables");
    }
}
