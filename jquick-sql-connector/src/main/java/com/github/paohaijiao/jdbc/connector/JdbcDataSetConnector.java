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
package com.github.paohaijiao.jdbc.connector;

import com.github.paohaijiao.connector.JDataSetConnector;
import com.github.paohaijiao.dataset.JColumnMeta;
import com.github.paohaijiao.dataset.JDataSet;
import com.github.paohaijiao.dataset.JRow;
import com.github.paohaijiao.ds.JDBCBaseConnectionConfig;
import com.github.paohaijiao.enums.JConnectorType;
import com.github.paohaijiao.jdbc.conf.JDbcConnectorConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * packageName com.github.paohaijiao.jdbc
 *
 * @author Martin
 * @version 1.0.0
 * @since 2025/8/26
 */
public class JdbcDataSetConnector implements JDataSetConnector {

    @Override
    public String getConnectorType() {
        return JConnectorType.jdbc.getCode();
    }

    @Override
    public JDataSet load(Object source) {
        if (source instanceof JDbcConnectorConfig) {
            return loadFromJdbcSource((JDbcConnectorConfig) source);
        } else {
            throw new IllegalArgumentException("Unsupported JDBC source type: " + source.getClass());
        }
    }
    private JDataSet loadFromJdbcSource(JDbcConnectorConfig source) {
        try (Connection conn = DriverManager.getConnection(source.getUrl(), source.getUsername(), source.getPassword());
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(source.getSql())) {
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            List<JColumnMeta> columns = IntStream.rangeClosed(1, columnCount)
                    .mapToObj(i -> {
                        try {
                            String columnName = metaData.getColumnName(i);
                            String className = metaData.getColumnClassName(i);
                            Class<?> columnType = Class.forName(className);
                            return new JColumnMeta(columnName, columnType, "jdbc");
                        } catch (Exception e) {
                            throw new RuntimeException("Failed to process column metadata", e);
                        }
                    })
                    .collect(Collectors.toList());
            List<JRow> rows = new ArrayList<>();
            while (rs.next()) {
                JRow row = new JRow();
                IntStream.rangeClosed(1, columnCount)
                        .forEach(i -> {
                            try {
                                String columnName = metaData.getColumnName(i);
                                Object value = rs.getObject(i);
                                row.put(columnName, value);
                            } catch (Exception e) {
                                throw new RuntimeException("Failed to process row data", e);
                            }
                        });
                rows.add(row);
            }
            return new JDataSet(columns, rows);
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute JDBC query", e);
        }
    }


}
