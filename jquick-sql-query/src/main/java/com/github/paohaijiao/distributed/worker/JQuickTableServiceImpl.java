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
package com.github.paohaijiao.distributed.worker;
import com.github.paohaijiao.console.JConsole;
import com.github.paohaijiao.datasource.JQuickDataSourceManager;
import com.github.paohaijiao.proto.*;
import com.github.paohaijiao.statement.JQuickDataSet;
import io.grpc.stub.StreamObserver;

public class JQuickTableServiceImpl extends JQuickTableServiceGrpc.JQuickTableServiceImplBase {

    private static final JConsole console = JConsole.initConsoleEnvironment();

    private final JQuickDataConverter dataConverter;

    public JQuickTableServiceImpl(JQuickDataConverter dataConverter) {
        this.dataConverter = dataConverter;
    }

    @Override
    public void registerTable(RegisterTableRequest request, StreamObserver<RegisterTableResponse> responseObserver) {
        String tableName = request.getTableName();
        try {
            JQuickDataSet data = dataConverter.convertFromProto(request.getData());
            if (request.getOverwrite()) {
                JQuickDataSourceManager.registerOrReplace(tableName, data);
            } else {
                JQuickDataSourceManager.registerIfAbsent(tableName, data);
            }
            console.info(String.format("Table registered on worker - table: %s, rows: %d", tableName, data.size()));
            RegisterTableResponse response = RegisterTableResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("Table registered successfully")
                    .setRowCount(data.size())
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Exception e) {
            console.error("Failed to register table: " + tableName, e);
            RegisterTableResponse response = RegisterTableResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage("Failed: " + e.getMessage())
                    .setRowCount(0)
                    .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    @Override
    public void dropTable(DropTableRequest request, StreamObserver<DropTableResponse> responseObserver) {
        String tableName = request.getTableName();
        JQuickDataSet removed = JQuickDataSourceManager.removeTable(tableName);
        DropTableResponse response = DropTableResponse.newBuilder()
                .setSuccess(removed != null)
                .setMessage(removed != null ? "Table dropped" : "Table not found")
                .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
