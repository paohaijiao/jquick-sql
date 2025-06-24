package com.github.paohaijiao.factory;

import com.github.paohaijiao.database.JDataBase;

import java.sql.Connection;
import java.sql.SQLException;

public interface JConnectionFactory {

    Connection createConnection() throws SQLException;

    void closeConnection(Connection connection);

    JDataBase getConfig();

    default boolean validateConnection() {
        try (Connection conn = createConnection()) {
            return conn.isValid(5); // 5秒超时验证
        } catch (SQLException e) {
            return false;
        }
    }
}
