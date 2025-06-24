package com.github.paohaijiao.enums;

public enum JDatabaseType {
    MYSQL("MySQL", "com.mysql.cj.jdbc.Driver"),
    POSTGRESQL("PostgreSQL", "org.postgresql.Driver"),
    ORACLE("Oracle", "oracle.jdbc.OracleDriver"),
    SQLSERVER("SQL Server", "com.microsoft.sqlserver.jdbc.SQLServerDriver"),
    H2("H2", "org.h2.Driver"),
    SQLITE("SQLite", "org.sqlite.JDBC");

    private final String displayName;
    private final String driverClass;

    JDatabaseType(String displayName, String driverClass) {
        this.displayName = displayName;
        this.driverClass = driverClass;
    }

    public String getDriverClass() {
        return driverClass;
    }

    public String getDisplayName() {
        return displayName;
    }
}
