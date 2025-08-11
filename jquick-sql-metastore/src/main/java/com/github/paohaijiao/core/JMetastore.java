package com.github.paohaijiao.core;

import com.github.paohaijiao.exception.JMetadataException;
import com.github.paohaijiao.model.database.JDatabase;
import com.github.paohaijiao.model.function.JFunction;
import com.github.paohaijiao.model.table.JPartition;
import com.github.paohaijiao.model.table.JTable;

import java.util.List;

public interface JMetastore {

    JDatabase getDatabase(String dbName) throws JMetadataException;

    List<JDatabase> getAllDatabases() throws JMetadataException;

    void createDatabase(JDatabase db) throws JMetadataException;

    void alterDatabase(String dbName, JDatabase newDb) throws JMetadataException;

    void dropDatabase(String dbName) throws JMetadataException;

    JTable getTable(String dbName, String tableName) throws JMetadataException;

    List<JTable> getTables(String dbName) throws JMetadataException;

    void createTable(JTable table) throws JMetadataException;

    void alterTable(String dbName, String tableName, JTable newTable) throws JMetadataException;

    void dropTable(String dbName, String tableName) throws JMetadataException;

    List<JPartition> getPartitions(String dbName, String tableName) throws JMetadataException;

    void addPartition(JPartition partition) throws JMetadataException;

    void dropPartition(String dbName, String tableName, List<String> partValues) throws JMetadataException;

    void createFunction(JFunction function) throws JMetadataException;

    void dropFunction(String dbName, String functionName) throws JMetadataException;

    JFunction getFunction(String dbName, String functionName) throws JMetadataException;
}
