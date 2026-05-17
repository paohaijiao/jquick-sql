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
package com.github.paohaijiao.scheduler;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Worker 发现服务（简化实现）
 */
public class WorkerDiscovery {
    private final WorkerManager manager;
    private ServerSocket serverSocket;
    private ExecutorService executor;
    private volatile boolean running;

    public WorkerDiscovery(WorkerManager manager) {
        this.manager = manager;
    }

    public void start(int port) {
        running = true;
        executor = Executors.newCachedThreadPool();

        try {
            serverSocket = new ServerSocket(port);
            System.out.println("Worker discovery started on port " + port);

            executor.submit(() -> {
                while (running) {
                    try {
                        Socket socket = serverSocket.accept();
                        handleRegistration(socket);
                    } catch (IOException e) {
                        if (running) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleRegistration(Socket socket) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true)) {

            String line = reader.readLine();
            if (line != null && line.startsWith("REGISTER:")) {
                String[] parts = line.split(":");
                if (parts.length >= 5) {
                    String workerId = parts[1];
                    String host = parts[2];
                    int controlPort = Integer.parseInt(parts[3]);
                    int dataPort = Integer.parseInt(parts[4]);
                    int totalSlots = parts.length > 5 ? Integer.parseInt(parts[5]) : 4;

                    WorkerInfo worker = new WorkerInfo(workerId, host, controlPort, dataPort, totalSlots);
                    manager.registerWorker(worker);
                    writer.println("OK");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        running = false;
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (executor != null) {
            executor.shutdown();
        }
    }
}
