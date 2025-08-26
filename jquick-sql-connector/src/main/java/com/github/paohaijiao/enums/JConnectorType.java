package com.github.paohaijiao.enums;

import com.github.paohaijiao.csv.JCsvDataSetConnector;
import com.github.paohaijiao.jdbc.connector.JdbcDataSetConnector;
import lombok.Getter;

@Getter
public enum JConnectorType {

    csv("csv", JCsvDataSetConnector.class),
    jdbc("jdbc", JdbcDataSetConnector.class);


    private String code;

    private Class<?> clazz;

    private JConnectorType(String code, Class<?> clazz) {
        this.code = code;
        this.clazz = clazz;
    }
}
