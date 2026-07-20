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
package com.github.paohaijiao.engine;

import com.github.paohaijiao.ast.JQuickQueryNode;
import com.github.paohaijiao.ast2logic.JQuickASTToLogicalPlanVisitor;
import com.github.paohaijiao.config.JQuickSqlConfig;
import com.github.paohaijiao.console.JConsole;
import com.github.paohaijiao.datasource.JQuickDataSourceManager;
import com.github.paohaijiao.distributed.JQuickDistributedPlan;
import com.github.paohaijiao.distributed.coordinator.JQuickCoordinator;
import com.github.paohaijiao.distributed.worker.JQuickWorker;
import com.github.paohaijiao.executor.JQuickSQLExecutor;
import com.github.paohaijiao.fragment.JQuickFragmenter;
import com.github.paohaijiao.logic.JQuickLogicalPlanNode;
import com.github.paohaijiao.logic2physical.JQuickPhysicalPlanGenerator;
import com.github.paohaijiao.optimizer.JQuickLogicalPlanOptimizer;
import com.github.paohaijiao.parser.JQuickSQLLexer;
import com.github.paohaijiao.parser.JQuickSQLParser;
import com.github.paohaijiao.physical.JQuickPhysicalPlanNode;
import com.github.paohaijiao.statement.JQuickColumnMeta;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JQuickSQL {

    private final JConsole console=JConsole.initConsoleEnvironment();

    private final JQuickSqlConfig config;

    private final JQuickPhysicalPlanGenerator physicalGenerator;

    private final JQuickLogicalPlanOptimizer optimizer;

    private final JQuickFragmenter fragmenter;

    private final List<JQuickWorker> workers;

    private JQuickCoordinator coordinator;

    private boolean embeddedMode;

    private JQuickSQL(JQuickSqlConfig config) {
        this.config = config != null ? config : new JQuickSqlConfig();
        this.physicalGenerator = new JQuickPhysicalPlanGenerator();
        this.fragmenter = new JQuickFragmenter(this.config.getDefaultParallelism());
        this.workers = new ArrayList<>();
        this.embeddedMode = false;
        this.optimizer=new JQuickLogicalPlanOptimizer();
    }

    public static JQuickSQL create() {
        return new JQuickSQL(new JQuickSqlConfig());
    }

    public static JQuickSQL create(JQuickSqlConfig config) {
        return new JQuickSQL(config);
    }

    public static JQuickSQL embedded() {
        JQuickSQL sql = new JQuickSQL(new JQuickSqlConfig());
        sql.embeddedMode = true;
        return sql;
    }

    public static JQuickSQL embedded(int workerCount) {
        JQuickSQL sql = new JQuickSQL(new JQuickSqlConfig());
        sql.embeddedMode = true;
        sql.config.setDefaultParallelism(workerCount);
        return sql;
    }

    public JQuickSQL withParallelism(int parallelism) {
        this.config.setDefaultParallelism(parallelism);
        return this;
    }

    public JQuickSQL withWorker(String host, int port) {
        List<JQuickCoordinator.WorkerEndpoint> endpoints = new ArrayList<>(config.getWorkers() != null ? config.getWorkers() : new ArrayList<>());
        endpoints.add(new JQuickCoordinator.WorkerEndpoint("worker-" + (endpoints.size() + 1), host, port, endpoints.size()));
        config.setWorkers(endpoints);
        return this;
    }

    public JQuickSQL withWorkers(List<String> addresses) {
        List<JQuickCoordinator.WorkerEndpoint> endpoints = new ArrayList<>();
        for (int i = 0; i < addresses.size(); i++) {
            String[] parts = addresses.get(i).split(":");
            String host = parts[0];
            int port = Integer.parseInt(parts[1]);
            endpoints.add(new JQuickCoordinator.WorkerEndpoint("worker-" + (i + 1), host, port, i));
        }
        config.setWorkers(endpoints);
        return this;
    }

    public JQuickSQL withConfig(JQuickSqlConfig config) {
        if (config != null) {
            this.config.setDefaultParallelism(config.getDefaultParallelism());
            this.config.setWorkers(config.getWorkers());
            this.config.setMaxTaskRetries(config.getMaxTaskRetries());
            this.config.setTaskTimeoutMs(config.getTaskTimeoutMs());
            this.config.setExecutionMode(config.getExecutionMode());
        }
        return this;
    }

    public JQuickSQL registerTable(String tableName, JQuickDataSet data) {
        JQuickDataSourceManager.registerTable(tableName, data);
        return this;
    }

    public JQuickSQL registerTable(String tableName, List<JQuickColumnMeta> columns, List<JQuickRow> rows) {
        JQuickDataSourceManager.registerTable(tableName, new JQuickDataSet(columns, rows));
        return this;
    }

    public JQuickSQL registerTable(String tableName, List<JQuickColumnMeta> columns, JQuickRow... rows) {
        JQuickDataSourceManager.registerTable(tableName, new JQuickDataSet(columns, Arrays.asList(rows)));
        return this;
    }

    public JQuickSQL registerTable(String tableName, Object... keyValuePairs) {
        if (keyValuePairs.length == 0) {
            return this;
        }
        List<String> columnNames = new ArrayList<>();
        List<Object> firstRowValues = new ArrayList<>();
        for (int i = 0; i < keyValuePairs.length; i += 2) {
            String colName = (String) keyValuePairs[i];
            Object value = keyValuePairs[i + 1];
            columnNames.add(colName);
            firstRowValues.add(value);
        }
        List<JQuickColumnMeta> columns = new ArrayList<>();
        for (int i = 0; i < columnNames.size(); i++) {
            columns.add(new JQuickColumnMeta(columnNames.get(i), firstRowValues.get(i) != null ? firstRowValues.get(i).getClass() : Object.class, tableName));
        }
        JQuickRow row = new JQuickRow();
        for (int i = 0; i < columnNames.size(); i++) {
            row.put(columnNames.get(i), firstRowValues.get(i));
        }
        JQuickDataSourceManager.registerTable(tableName, new JQuickDataSet(columns, Arrays.asList(row)));
        return this;
    }

    public JQuickDataSet execute(String sql) {
        ensureInitialized();
        return executeSQL(sql);
    }

    public JQuickDataSet execute(String sql, Map<String, Object> parameters) {
        ensureInitialized();
        return executeSQL(sql);
    }

    public JQuickDataSet executeSQL(String sql) {
        String queryId = "sql_" + System.currentTimeMillis();
        JQuickLogicalPlanNode logicalPlan = parseSQLToLogicalPlan(sql);
        JQuickLogicalPlanNode optimizedPlan = optimizer.optimize(logicalPlan);
        JQuickPhysicalPlanNode physicalPlan = physicalGenerator.generate(optimizedPlan);
        JQuickDistributedPlan distributedPlan = fragmenter.fragment(physicalPlan);
        fragmenter.printFragments(distributedPlan);
        return coordinator.executeQueryWithPlan(queryId, distributedPlan);
    }

    private JQuickLogicalPlanNode parseSQLToLogicalPlan(String sql) {
        console.info("Trying to parse SQL: " + sql);
        JQuickSQLLexer lexer = new JQuickSQLLexer(CharStreams.fromString(sql));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JQuickSQLParser parser = new JQuickSQLParser(tokens);
        JQuickSQLParser.QueryContext parseTree = parser.query();
        JQuickSQLExecutor executor = new JQuickSQLExecutor();
        JQuickQueryNode ast = executor.execute(sql);
        JQuickASTToLogicalPlanVisitor visitor = new JQuickASTToLogicalPlanVisitor();
        return visitor.visit(ast);
    }

    private synchronized void ensureInitialized() {
        if (coordinator == null) {
            if (embeddedMode && config.getWorkers() == null || config.getWorkers().isEmpty()) {
                startEmbeddedWorkers();
            }
            coordinator = new JQuickCoordinator(config);
        }
    }

    private void startEmbeddedWorkers() {
        int workerCount = config.getDefaultParallelism();
        List<JQuickCoordinator.WorkerEndpoint> endpoints = new ArrayList<>();
        int basePort = 19001;
        for (int i = 0; i < workerCount; i++) {
            int port = basePort + i;
            JQuickWorker worker = new JQuickWorker("worker-" + (i + 1), port);
            try {
                worker.start();
            } catch (java.io.IOException e) {
                throw new RuntimeException("Failed to start worker on port " + port, e);
            }
            workers.add(worker);
            endpoints.add(new JQuickCoordinator.WorkerEndpoint("worker-" + (i + 1), "localhost", port, i));
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        config.setWorkers(endpoints);
        for (JQuickWorker worker : workers) {
            worker.setWorkerEndpoints(endpoints);
        }
    }

    public void shutdown() {
        if (coordinator != null) {
            coordinator.shutdown();
            coordinator = null;
        }
        for (JQuickWorker worker : workers) {
            worker.stop();
        }
        workers.clear();
    }

    public JQuickSqlConfig getConfig() {
        return config;
    }

    public JQuickCoordinator getCoordinator() {
        ensureInitialized();
        return coordinator;
    }

    public List<String> getRegisteredTables() {
        return new ArrayList<>(JQuickDataSourceManager.getTableNames());
    }

    public JQuickDataSet getTable(String tableName) {
        return JQuickDataSourceManager.getTable(tableName);
    }

    public boolean hasTable(String tableName) {
        return JQuickDataSourceManager.containsTable(tableName);
    }

    public JQuickSQL clearTables() {
        JQuickDataSourceManager.clearAll();
        return this;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private JQuickSqlConfig config;
        private boolean embeddedMode;
        private int embeddedWorkerCount = 1;
        private List<TableDefinition> tables = new ArrayList<>();

        public Builder config(JQuickSqlConfig config) {
            this.config = config;
            return this;
        }

        public Builder embedded() {
            this.embeddedMode = true;
            return this;
        }

        public Builder embedded(int workerCount) {
            this.embeddedMode = true;
            this.embeddedWorkerCount = workerCount;
            return this;
        }

        public Builder parallelism(int parallelism) {
            if (config == null) {
                config = new JQuickSqlConfig();
            }
            config.setDefaultParallelism(parallelism);
            return this;
        }

        public Builder worker(String host, int port) {
            if (config == null) {
                config = new JQuickSqlConfig();
            }
            List<JQuickCoordinator.WorkerEndpoint> endpoints = new ArrayList<>(config.getWorkers() != null ? config.getWorkers() : new ArrayList<>());
            endpoints.add(new JQuickCoordinator.WorkerEndpoint("worker-" + (endpoints.size() + 1), host, port, endpoints.size()));
            config.setWorkers(endpoints);
            return this;
        }

        public Builder table(String name, List<JQuickColumnMeta> columns, List<JQuickRow> rows) {
            tables.add(new TableDefinition(name, columns, rows));
            return this;
        }

        public Builder table(String name, JQuickDataSet data) {
            tables.add(new TableDefinition(name, data.getColumns(), data.getRows()));
            return this;
        }

        public JQuickSQL build() {
            JQuickSQL sql;
            if (embeddedMode) {
                sql = JQuickSQL.embedded(embeddedWorkerCount);
            } else {
                sql = JQuickSQL.create(config != null ? config : new JQuickSqlConfig());
            }
            
            for (TableDefinition table : tables) {
                sql.registerTable(table.name, table.columns, table.rows);
            }
            
            return sql;
        }
    }

    private static class TableDefinition {

        final String name;

        final List<JQuickColumnMeta> columns;

        final List<JQuickRow> rows;

        TableDefinition(String name, List<JQuickColumnMeta> columns, List<JQuickRow> rows) {
            this.name = name;
            this.columns = columns;
            this.rows = rows;
        }
    }
}