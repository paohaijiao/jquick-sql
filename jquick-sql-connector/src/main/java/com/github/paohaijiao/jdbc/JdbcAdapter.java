package com.github.paohaijiao.jdbc;

import com.github.paohaijiao.adaptor.JDataSourceAdapter;
import com.github.paohaijiao.config.JDataSourceConfig;
import com.github.paohaijiao.ds.JDBCBaseConnectionConfig;
import com.github.paohaijiao.enums.JErrorCode;
import com.github.paohaijiao.exception.JDataAccessException;
import com.github.paohaijiao.mapper.JdbcTypeMapper;
import com.github.paohaijiao.model.JEntityMetadata;
import com.github.paohaijiao.model.JFieldMetadata;
import com.github.paohaijiao.model.JMetadata;
import com.github.paohaijiao.query.JDataSet;
import com.github.paohaijiao.query.JQuery;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JdbcAdapter implements JDataSourceAdapter {

    private Connection connection;

    private JDBCBaseConnectionConfig config;

    @Override
    public void init(JDataSourceConfig config) {
        this.config = config.getJdbc();
        try {
            Class.forName(this.config.getDriverClassName());
            this.connection = DriverManager.getConnection(this.config.getUrl(), this.config.getUsername(), this.config.getPassword());
        } catch (Exception e) {
            throw new JDataAccessException(JErrorCode.CONNECTION_FAILED,"JDBC connection failed", e);
        }
    }

    @Override
    public JMetadata getMetadata() {
        JMetadata metadata = new JMetadata();
        try {
            DatabaseMetaData dbMeta = connection.getMetaData();
            ResultSet tables = dbMeta.getTables(null, null, "%", new String[]{"TABLE"});
            while (tables.next()) {
                JEntityMetadata entity = new JEntityMetadata();
                entity.setName(tables.getString("TABLE_NAME"));
                ResultSet columns = dbMeta.getColumns(null, null, entity.getName(), null);
                List<JFieldMetadata> fields = new ArrayList<>();
                while (columns.next()) {
                    JFieldMetadata field = new JFieldMetadata();
                    field.setName(columns.getString("COLUMN_NAME"));
                    field.setType(JdbcTypeMapper.toDataType(columns.getInt("DATA_TYPE")));
                    fields.add(field);
                }
                entity.setFields(fields);
                metadata.getEntities().add(entity);
            }
        } catch (SQLException e) {
            throw new JDataAccessException(JErrorCode.SQL_EXECUTION_ERROR,"Failed to get metadata", e);
        }
        return metadata;
    }

    @Override
    public JDataSet executeQuery(JQuery query) {
     return null;
    }

    @Override
    public void close() {

    }

    @Override
    public boolean testConnection() {
        return true;
    }
}
