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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;
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
        if (source instanceof JDBCBaseConnectionConfig) {
            return loadFromJdbcSource((JDBCBaseConnectionConfig) source);
        } else {
            throw new IllegalArgumentException("Unsupported JDBC source type: " + source.getClass());
        }
    }
    private JDataSet loadFromJdbcSource(JDBCBaseConnectionConfig source) {
        try (Connection conn = DriverManager.getConnection(source.getUrl(), source.getUsername(), source.getPassword())) {
            try (var stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(source.getSql())) {
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();
                List<JColumnMeta> columns = new ArrayList<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    String className = metaData.getColumnClassName(i);
                    Class<?> columnType = Class.forName(className);
                    columns.add(new JColumnMeta(columnName, columnType, "jdbc"));
                }
                List<JRow> rows = new ArrayList<>();
                while (rs.next()) {
                    JRow row = new JRow();
                    for (int i = 1; i <= columnCount; i++) {
                        String columnName = metaData.getColumnName(i);
                        Object value = rs.getObject(i);
                        row.put(columnName, value);
                    }
                    rows.add(row);
                }
                return new JDataSet(columns, rows);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute JDBC query", e);
        }
    }


}
