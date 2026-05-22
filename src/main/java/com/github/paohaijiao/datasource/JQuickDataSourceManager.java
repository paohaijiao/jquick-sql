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
package com.github.paohaijiao.datasource;


import com.github.paohaijiao.console.JConsole;
import com.github.paohaijiao.statement.JQuickDataSet;
import com.github.paohaijiao.statement.JQuickRow;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class JQuickDataSourceManager {

    private static final Map<String, Long> lastUpdateTime = new ConcurrentHashMap<>();

    private static final Map<String, JQuickDataSet> dataSources = new ConcurrentHashMap<>();

    private static final Map<String, Long> tableSizes = new ConcurrentHashMap<>();

    private static JConsole console = JConsole.initConsoleEnvironment();

    /**
     * Registers a table with the given name.
     *
     * @param name the table name
     * @param data the dataset
     */
    public static void registerTable(String name, JQuickDataSet data) {
        String key = name.toLowerCase();
        dataSources.put(key, data);
        tableSizes.put(key, (long) data.size());
        lastUpdateTime.put(key, System.currentTimeMillis());
    }

    /**
     * Registers a table, replacing if it already exists.
     *
     * @param name the table name
     * @param data the dataset
     * @return the previous dataset, or null if none existed
     */
    public static JQuickDataSet registerOrReplace(String name, JQuickDataSet data) {
        String key = name.toLowerCase();
        JQuickDataSet previous = dataSources.put(key, data);
        tableSizes.put(key, (long) data.size());
        lastUpdateTime.put(key, System.currentTimeMillis());
        return previous;
    }

    /**
     * Registers a table only if it doesn't already exist.
     *
     * @param name the table name
     * @param data the dataset
     * @return true if registered, false if already exists
     */
    public static boolean registerIfAbsent(String name, JQuickDataSet data) {
        String key = name.toLowerCase();
        if (dataSources.containsKey(key)) {
            return false;
        }
        dataSources.put(key, data);
        tableSizes.put(key, (long) data.size());
        lastUpdateTime.put(key, System.currentTimeMillis());
        return true;
    }

    /**
     * Gets a table by name.
     *
     * @param name the table name
     * @return the dataset
     * @throws RuntimeException if table not found
     */
    public static JQuickDataSet getTable(String name) {
        JQuickDataSet data = dataSources.get(name.toLowerCase());
        if (data == null) {
            throw new RuntimeException("Table not found: " + name);
        }
        return data;
    }

    /**
     * Gets a table by name, returning a default if not found.
     *
     * @param name the table name
     * @param defaultValue the default dataset to return
     * @return the dataset or default value
     */
    public static JQuickDataSet getTableOrDefault(String name, JQuickDataSet defaultValue) {
        return dataSources.getOrDefault(name.toLowerCase(), defaultValue);
    }

    /**
     * Checks if a table exists.
     *
     * @param name the table name
     * @return true if the table exists
     */
    public static boolean containsTable(String name) {
        return dataSources.containsKey(name.toLowerCase());
    }

    /**
     * Removes a table from the manager.
     *
     * @param name the table name
     * @return the removed dataset, or null if not found
     */
    public static JQuickDataSet removeTable(String name) {
        String key = name.toLowerCase();
        tableSizes.remove(key);
        lastUpdateTime.remove(key);
        return dataSources.remove(key);
    }

    /**
     * Gets all registered table names.
     *
     * @return a set of all table names
     */
    public static Set<String> getTableNames() {
        return new HashSet<>(dataSources.keySet());
    }

    /**
     * Gets the number of registered tables.
     *
     * @return the count of tables
     */
    public static int getTableCount() {
        return dataSources.size();
    }

    /**
     * Gets the total number of rows across all tables.
     *
     * @return total row count
     */
    public static long getTotalRowCount() {
        return tableSizes.values().stream().mapToLong(Long::longValue).sum();
    }

    /**
     * Gets the size of a specific table.
     *
     * @param tableName the table name
     * @return the row count, or 0 if not found
     */
    public static long getTableSize(String tableName) {
        return tableSizes.getOrDefault(tableName.toLowerCase(), 0L);
    }

    /**
     * Gets the last update time of a table.
     *
     * @param tableName the table name
     * @return the last update timestamp in milliseconds, or -1 if not found
     */
    public static long getLastUpdateTime(String tableName) {
        return lastUpdateTime.getOrDefault(tableName.toLowerCase(), -1L);
    }

    /**
     * Updates an existing table with new data.
     *
     * @param name the table name
     * @param data the new dataset
     * @return true if updated, false if table didn't exist
     */
    public static boolean updateTable(String name, JQuickDataSet data) {
        String key = name.toLowerCase();
        if (!dataSources.containsKey(key)) {
            return false;
        }
        dataSources.put(key, data);
        tableSizes.put(key, (long) data.size());
        lastUpdateTime.put(key, System.currentTimeMillis());
        return true;
    }

    /**
     * Clears all registered tables.
     */
    public static void clearAll() {
        dataSources.clear();
        tableSizes.clear();
        lastUpdateTime.clear();
    }

    /**
     * Executes a consumer function for each registered table.
     *
     * @param action the action to perform on each table
     */
    public static void forEachTable(Consumer<Map.Entry<String, JQuickDataSet>> action) {
        dataSources.entrySet().forEach(action);
    }

    /**
     * Filters tables by a predicate and returns a map of matching tables.
     *
     * @param predicate the filter condition
     * @return a map of matching tables
     */
    public static Map<String, JQuickDataSet> filterTables(Predicate<Map.Entry<String, JQuickDataSet>> predicate) {
        return dataSources.entrySet().stream()
                .filter(predicate)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Gets tables that have names starting with the given prefix.
     *
     * @param prefix the prefix to match
     * @return a map of matching tables
     */
    public static Map<String, JQuickDataSet> getTablesByPrefix(String prefix) {
        String lowerPrefix = prefix.toLowerCase();
        return dataSources.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(lowerPrefix))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Gets tables that have names containing the given substring.
     *
     * @param substring the substring to search for
     * @return a map of matching tables
     */
    public static Map<String, JQuickDataSet> searchTables(String substring) {
        String lowerSubstring = substring.toLowerCase();
        return dataSources.entrySet().stream()
                .filter(entry -> entry.getKey().contains(lowerSubstring))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * Checks if any table is empty (has no rows).
     *
     * @return true if at least one table is empty
     */
    public static boolean hasEmptyTable() {
        return tableSizes.values().stream().anyMatch(size -> size == 0);
    }

    /**
     * Checks if all tables are non-empty.
     *
     * @return true if all tables have at least one row
     */
    public static boolean areAllTablesNonEmpty() {
        return tableSizes.values().stream().noneMatch(size -> size == 0);
    }

    /**
     * Gets the names of empty tables.
     *
     * @return a set of empty table names
     */
    public static Set<String> getEmptyTableNames() {
        return tableSizes.entrySet().stream()
                .filter(entry -> entry.getValue() == 0)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }

    /**
     * Creates a copy of a table.
     *
     * @param sourceName the source table name
     * @param targetName the target table name
     * @return true if copied successfully, false if source not found
     */
    public static boolean copyTable(String sourceName, String targetName) {
        JQuickDataSet source = dataSources.get(sourceName.toLowerCase());
        if (source == null) {
            return false;
        }
        // Create a shallow copy (the dataset is immutable)
        registerTable(targetName, source);
        return true;
    }

    /**
     * Renames an existing table.
     *
     * @param oldName the current table name
     * @param newName the new table name
     * @return true if renamed successfully, false if old table doesn't exist or new name already taken
     */
    public static boolean renameTable(String oldName, String newName) {
        String oldKey = oldName.toLowerCase();
        String newKey = newName.toLowerCase();
        if (!dataSources.containsKey(oldKey) || dataSources.containsKey(newKey)) {
            return false;
        }
        JQuickDataSet data = dataSources.remove(oldKey);
        Long size = tableSizes.remove(oldKey);
        Long time = lastUpdateTime.remove(oldKey);
        dataSources.put(newKey, data);
        if (size != null) tableSizes.put(newKey, size);
        if (time != null) lastUpdateTime.put(newKey, time);
        return true;
    }

    /**
     * Merges two tables into a new table.
     *
     * @param table1     the first table name
     * @param table2     the second table name
     * @param mergedName the name for the merged table
     * @return true if merged successfully
     */
    public static boolean mergeTables(String table1, String table2, String mergedName) {
        JQuickDataSet data1 = dataSources.get(table1.toLowerCase());
        JQuickDataSet data2 = dataSources.get(table2.toLowerCase());
        if (data1 == null || data2 == null) {
            return false;
        }
        JQuickDataSet merged = data1.concat(data2);
        registerTable(mergedName, merged);
        return true;
    }

    /**
     * Gets a summary of all registered tables.
     *
     * @return a map containing table names and their row counts
     */
    public static Map<String, Long> getTableSummary() {
        return new HashMap<>(tableSizes);
    }

    /**
     * Prints a summary of all registered tables to console.
     */
    public static void printSummary() {
        JConsole console = JConsole.getInstance();
        console.info("=== Data Source Manager Summary ===");
        console.info("Total tables: {}", getTableCount());
        console.info("Total rows: {}", getTotalRowCount());
        console.info("--- Table Details ---");
        for (Map.Entry<String, Long> entry : tableSizes.entrySet()) {
            console.info("  {}: {} rows", entry.getKey(), entry.getValue());
        }
        console.info("===================================");
    }

    /**
     * Executes a function on a table and returns the result.
     *
     * @param tableName the table name
     * @param function  the function to apply
     * @param <R>       the result type
     * @return the function result, or null if table not found
     */
    public static <R> R queryTable(String tableName, Function<JQuickDataSet, R> function) {
        JQuickDataSet table = dataSources.get(tableName.toLowerCase());
        if (table == null) {
            return null;
        }
        return function.apply(table);
    }

    /**
     * Gets the first row of a table (useful for quick inspection).
     *
     * @param tableName the table name
     * @return the first row, or null if table not found or empty
     */
    public static JQuickRow getFirstRow(String tableName) {
        JQuickDataSet table = dataSources.get(tableName.toLowerCase());
        return table != null ? table.first() : null;
    }

    /**
     * Gets a sample of rows from a table.
     *
     * @param tableName the table name
     * @param limit     the maximum number of rows to return
     * @return a list of sample rows, or empty list if table not found
     */
    public static List<JQuickRow> getSampleRows(String tableName, int limit) {
        JQuickDataSet table = dataSources.get(tableName.toLowerCase());
        if (table == null || table.isEmpty()) {
            return Collections.emptyList();
        }
        return table.limit(limit).getRows();
    }

    /**
     * Checks if a table is registered and has data.
     *
     * @param tableName the table name
     * @return true if table exists and is non-empty
     */
    public static boolean isTableReady(String tableName) {
        JQuickDataSet table = dataSources.get(tableName.toLowerCase());
        return table != null && !table.isEmpty();
    }

    /**
     * Gets the column names of a table.
     *
     * @param tableName the table name
     * @return list of column names, or empty list if table not found
     */
    public static List<String> getColumnNames(String tableName) {
        JQuickDataSet table = dataSources.get(tableName.toLowerCase());
        return table != null ? table.getColumnNames() : Collections.emptyList();
    }

    /**
     * Gets the column metadata of a table.
     *
     * @param tableName the table name
     * @return list of column metadata, or empty list if table not found
     */
    public static List<com.github.paohaijiao.statement.JQuickColumnMeta> getColumnMeta(String tableName) {
        JQuickDataSet table = dataSources.get(tableName.toLowerCase());
        return table != null ? table.getColumns() : Collections.emptyList();
    }

    /**
     * Checks if a table has a specific column.
     *
     * @param tableName  the table name
     * @param columnName the column name
     * @return true if table exists and has the column
     */
    public static boolean hasColumn(String tableName, String columnName) {
        JQuickDataSet table = dataSources.get(tableName.toLowerCase());
        if (table == null) return false;
        return table.getColumnNames().stream().anyMatch(name -> name.equalsIgnoreCase(columnName));
    }

    /**
     * Gets distinct values from a column across all rows.
     *
     * @param tableName  the table name
     * @param columnName the column name
     * @return set of distinct values, or empty set if table not found
     */
    public static Set<Object> getDistinctValues(String tableName, String columnName) {
        JQuickDataSet table = dataSources.get(tableName.toLowerCase());
        if (table == null) return Collections.emptySet();
        return table.getColumnValues(columnName).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    /**
     * Gets the count of distinct values in a column.
     *
     * @param tableName  the table name
     * @param columnName the column name
     * @return distinct count, or 0 if table not found
     */
    public static long getDistinctCount(String tableName, String columnName) {
        return getDistinctValues(tableName, columnName).size();
    }

    /**
     * Gets the value count for each distinct value in a column.
     *
     * @param tableName  the table name
     * @param columnName the column name
     * @return map from value to count
     */
    public static Map<Object, Long> getValueFrequency(String tableName, String columnName) {
        JQuickDataSet table = dataSources.get(tableName.toLowerCase());
        if (table == null) return Collections.emptyMap();
        return table.getRows().stream()
                .collect(Collectors.groupingBy(
                        row -> row.get(columnName),
                        Collectors.counting()
                ));
    }

    /**
     * Executes a SQL-like query on a table (filter + select).
     *
     * @param tableName       the table name
     * @param filterPredicate filter condition
     * @param selectedColumns columns to select
     * @return filtered and selected dataset, or null if table not found
     */
    public static JQuickDataSet query(String tableName, Predicate<JQuickRow> filterPredicate, String... selectedColumns) {
        JQuickDataSet table = dataSources.get(tableName.toLowerCase());
        if (table == null) return null;
        JQuickDataSet filtered = table.filter(filterPredicate);
        if (selectedColumns.length > 0) {
            return filtered.select(selectedColumns);
        }
        return filtered;
    }

    /**
     * Exports a table to a list of maps.
     *
     * @param tableName the table name
     * @return list of maps, or empty list if table not found
     */
    public static List<Map<String, Object>> exportToMapList(String tableName) {
        JQuickDataSet table = dataSources.get(tableName.toLowerCase());
        return table != null ? table.toMapList() : Collections.emptyList();
    }

    /**
     * Exports a table to a list of beans.
     *
     * @param tableName the table name
     * @param beanClass the target bean class
     * @param <T>       the bean type
     * @return list of beans, or empty list if table not found
     */
    public static <T> List<T> exportToBeanList(String tableName, Class<T> beanClass) {
        JQuickDataSet table = dataSources.get(tableName.toLowerCase());
        return table != null ? table.toBeanList(beanClass) : Collections.emptyList();
    }

    /**
     * Batch registers multiple tables.
     *
     * @param tables map of table name to dataset
     */
    public static void registerAll(Map<String, JQuickDataSet> tables) {
        for (Map.Entry<String, JQuickDataSet> entry : tables.entrySet()) {
            registerTable(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Batch registers multiple tables with a prefix.
     *
     * @param prefix the prefix to add to each table name
     * @param tables map of table name to dataset
     */
    public static void registerAllWithPrefix(String prefix, Map<String, JQuickDataSet> tables) {
        String normalizedPrefix = prefix.endsWith("_") ? prefix : prefix + "_";
        for (Map.Entry<String, JQuickDataSet> entry : tables.entrySet()) {
            registerTable(normalizedPrefix + entry.getKey(), entry.getValue());
        }
    }

    /**
     * Gets multiple tables at once.
     *
     * @param tableNames the table names
     * @return map of existing table names to datasets (non-existing tables are omitted)
     */
    public static Map<String, JQuickDataSet> getTables(String... tableNames) {
        Map<String, JQuickDataSet> result = new HashMap<>();
        for (String name : tableNames) {
            JQuickDataSet table = dataSources.get(name.toLowerCase());
            if (table != null) {
                result.put(name, table);
            }
        }
        return result;
    }

    /**
     * Creates a cross-tabulation (pivot table) of two columns.
     *
     * @param tableName   the table name
     * @param rowColumn   the column for row headers
     * @param colColumn   the column for column headers
     * @param valueColumn the column for values (counts if null)
     * @return a map representing the pivot table
     */
    public static Map<Object, Map<Object, Long>> pivot(String tableName, String rowColumn, String colColumn, String valueColumn) {
        JQuickDataSet table = dataSources.get(tableName.toLowerCase());
        if (table == null) return Collections.emptyMap();
        Map<Object, Map<Object, Long>> result = new LinkedHashMap<>();
        for (JQuickRow row : table.getRows()) {
            Object rowKey = row.get(rowColumn);
            Object colKey = row.get(colColumn);
            Long value = valueColumn != null ? (Long) row.getAsConverted(valueColumn, Long.class) : 1L;
            result.computeIfAbsent(rowKey, k -> new LinkedHashMap<>()).merge(colKey, value, Long::sum);
        }
        return result;
    }

    /**
     * Compares two tables and returns the differences.
     *
     * @param tableName1 first table name
     * @param tableName2 second table name
     * @return a map containing added, removed, and common rows
     */
    public static Map<String, List<JQuickRow>> compareTables(String tableName1, String tableName2) {
        JQuickDataSet table1 = dataSources.get(tableName1.toLowerCase());
        JQuickDataSet table2 = dataSources.get(tableName2.toLowerCase());
        if (table1 == null || table2 == null) {
            throw new RuntimeException("One or both tables not found");
        }
        // Simple comparison using row equality
        List<JQuickRow> rows1 = new ArrayList<>(table1.getRows());
        List<JQuickRow> rows2 = new ArrayList<>(table2.getRows());
        List<JQuickRow> common = new ArrayList<>();
        List<JQuickRow> onlyIn1 = new ArrayList<>(rows1);
        List<JQuickRow> onlyIn2 = new ArrayList<>(rows2);
        // This is a simple O(n*m) comparison - for large datasets, consider using hash
        for (JQuickRow row1 : rows1) {
            if (rows2.stream().anyMatch(row2 -> row2.equals(row1))) {
                common.add(row1);
                onlyIn1.remove(row1);
            }
        }
        for (JQuickRow row2 : rows2) {
            if (rows1.stream().anyMatch(row1 -> row1.equals(row2))) {
                onlyIn2.remove(row2);
            }
        }
        Map<String, List<JQuickRow>> result = new HashMap<>();
        result.put("common", common);
        result.put("only_in_" + tableName1, onlyIn1);
        result.put("only_in_" + tableName2, onlyIn2);
        return result;
    }

    /**
     * Validates that a table meets certain conditions.
     *
     * @param tableName   the table name
     * @param validations list of validation functions
     * @return map of validation name to result (true/false)
     */
    public static Map<String, Boolean> validateTable(String tableName, Map<String, Predicate<JQuickDataSet>> validations) {
        JQuickDataSet table = dataSources.get(tableName.toLowerCase());
        Map<String, Boolean> results = new LinkedHashMap<>();
        if (table == null) {
            results.put("table_exists", false);
            return results;
        }
        results.put("table_exists", true);
        for (Map.Entry<String, Predicate<JQuickDataSet>> validation : validations.entrySet()) {
            results.put(validation.getKey(), validation.getValue().test(table));
        }
        return results;
    }

    /**
     * Gets the schema information for a table.
     *
     * @param tableName the table name
     * @return a map containing schema details
     */
    public static Map<String, Object> getSchemaInfo(String tableName) {
        JQuickDataSet table = dataSources.get(tableName.toLowerCase());
        if (table == null) return Collections.emptyMap();
        Map<String, Object> schema = new LinkedHashMap<>();
        schema.put("table_name", tableName);
        schema.put("row_count", table.size());
        schema.put("column_count", table.getColumns().size());
        List<Map<String, Object>> columnInfo = new ArrayList<>();
        for (com.github.paohaijiao.statement.JQuickColumnMeta col : table.getColumns()) {
            Map<String, Object> colInfo = new LinkedHashMap<>();
            colInfo.put("name", col.getName());
            colInfo.put("type", col.getType().getSimpleName());
            colInfo.put("source", col.getSource());
            // Calculate null count
            long nullCount = table.getRows().stream()
                    .filter(row -> row.get(col.getName()) == null)
                    .count();
            colInfo.put("null_count", nullCount);
            colInfo.put("null_percentage", table.isEmpty() ? 0 : (nullCount * 100.0 / table.size()));

            columnInfo.add(colInfo);
        }
        schema.put("columns", columnInfo);
        return schema;
    }

    /**
     * Backs up a table with a timestamp suffix.
     *
     * @param tableName the table name
     * @return the backup table name, or null if original not found
     */
    public static String backupTable(String tableName) {
        JQuickDataSet table = dataSources.get(tableName.toLowerCase());
        if (table == null) return null;
        String backupName = tableName + "_backup_" + System.currentTimeMillis();
        registerTable(backupName, table);
        return backupName;
    }

    /**
     * Restores a table from a backup.
     *
     * @param backupName the backup table name
     * @param targetName the target table name
     * @return true if restored successfully
     */
    public static boolean restoreFromBackup(String backupName, String targetName) {
        JQuickDataSet backup = dataSources.get(backupName.toLowerCase());
        if (backup == null) return false;
        registerOrReplace(targetName, backup);
        return true;
    }

    /**
     * Gets all tables that contain a specific value in any column.
     *
     * @param searchValue the value to search for
     * @return map of table names to lists of rows containing the value
     */
    public static Map<String, List<JQuickRow>> searchValueInAllTables(Object searchValue) {
        Map<String, List<JQuickRow>> results = new LinkedHashMap<>();
        String searchStr = searchValue != null ? searchValue.toString().toLowerCase() : "null";
        for (Map.Entry<String, JQuickDataSet> entry : dataSources.entrySet()) {
            String tableName = entry.getKey();
            JQuickDataSet table = entry.getValue();

            List<JQuickRow> matchingRows = table.getRows().stream()
                    .filter(row -> row.values().stream()
                            .anyMatch(v -> v != null ?
                                    v.toString().toLowerCase().contains(searchStr) :
                                    "null".equals(searchStr)))
                    .collect(Collectors.toList());

            if (!matchingRows.isEmpty()) {
                results.put(tableName, matchingRows);
            }
        }
        return results;
    }

    /**
     * Gets the memory usage estimate for a table.
     *
     * @param tableName the table name
     * @return estimated memory usage in bytes, or 0 if table not found
     */
    public static long estimateMemoryUsage(String tableName) {
        JQuickDataSet table = dataSources.get(tableName.toLowerCase());
        if (table == null) return 0;
        // Rough estimate: each row overhead + each cell overhead
        long rowOverhead = 64; // Approximate JVM object overhead
        long cellOverhead = 32; // Approximate Map entry overhead
        long total = rowOverhead * table.size();
        for (JQuickRow row : table.getRows()) {
            total += row.size() * cellOverhead;
            for (Object value : row.values()) {
                if (value != null) {
                    total += value.toString().length() * 2; // Rough string memory
                }
            }
        }
        return total;
    }

    /**
     * Saves table metadata to a JSON-like map.
     *
     * @return map containing metadata for all tables
     */
    public static Map<String, Object> exportMetadata() {
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("total_tables", getTableCount());
        metadata.put("total_rows", getTotalRowCount());
        metadata.put("timestamp", System.currentTimeMillis());
        Map<String, Object> tableDetails = new LinkedHashMap<>();
        for (Map.Entry<String, JQuickDataSet> entry : dataSources.entrySet()) {
            Map<String, Object> details = new LinkedHashMap<>();
            details.put("row_count", entry.getValue().size());
            details.put("column_count", entry.getValue().getColumns().size());
            details.put("last_update", lastUpdateTime.getOrDefault(entry.getKey(), -1L));
            details.put("column_names", entry.getValue().getColumnNames());
            tableDetails.put(entry.getKey(), details);
        }
        metadata.put("tables", tableDetails);
        return metadata;
    }

    /**
     * Creates a new table by applying a transformation to an existing table.
     *
     * @param sourceTable the source table name
     * @param targetTable the target table name
     * @param transformer the transformation function
     * @return true if created successfully
     */
    public static boolean transformTable(String sourceTable, String targetTable, Function<JQuickDataSet, JQuickDataSet> transformer) {
        JQuickDataSet source = dataSources.get(sourceTable.toLowerCase());
        if (source == null) return false;
        JQuickDataSet transformed = transformer.apply(source);
        registerTable(targetTable, transformed);
        return true;
    }

    /**
     * Performs a union of multiple tables (requires same schema).
     *
     * @param tableNames the table names to union
     * @param resultName the name for the result table
     * @return true if union was successful
     */
    public static boolean unionTables(String resultName, String... tableNames) {
        if (tableNames.length == 0) return false;
        JQuickDataSet result = null;
        for (String name : tableNames) {
            JQuickDataSet table = dataSources.get(name.toLowerCase());
            if (table == null) return false;
            if (result == null) {
                result = table;
            } else {
                result = result.concat(table);
            }
        }
        if (result != null) {
            registerTable(resultName, result);
            return true;
        }
        return false;
    }

    /**
     * Gets tables ordered by size (largest first).
     *
     * @return list of table names ordered by row count descending
     */
    public static List<String> getTablesOrderedBySize() {
        return tableSizes.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    /**
     * Prints detailed statistics for a specific table.
     *
     * @param tableName the table name
     */
    public static void printTableStatistics(String tableName) {
        JQuickDataSet table = dataSources.get(tableName.toLowerCase());
        if (table == null) {
            console.warn("Table not found: {}", tableName);
            return;
        }
        console.info("=== Statistics for: {} ===", tableName);
        console.info("Total rows: {}", table.size());
        console.info("Total columns: {}", table.getColumns().size());
        console.info("Last update: {}", new java.util.Date(getLastUpdateTime(tableName)));
        console.info("--- Column Statistics ---");
        for (com.github.paohaijiao.statement.JQuickColumnMeta col : table.getColumns()) {
            String colName = col.getName();
            long nonNull = table.getRows().stream().filter(row -> row.get(colName) != null).count();
            long distinct = getDistinctCount(tableName, colName);
            double nonNullPercent = table.isEmpty() ? 0 : (nonNull * 100.0 / table.size());
            console.info("  {} ({}): rows={}, non-null={} ({:.1f}%), distinct={}", colName, col.getType().getSimpleName(), table.size(), nonNull, nonNullPercent, distinct);
        }
        console.info("=================================");
    }

    /**
     * Prints the schema information for a table using JConsole.
     *
     * @param tableName the table name
     */
    public static void printSchemaInfo(String tableName) {
        JConsole console = JConsole.getInstance();
        JQuickDataSet table = dataSources.get(tableName.toLowerCase());
        if (table == null) {
            console.warn("Table not found: {}", tableName);
            return;
        }
        console.info("=== Schema for: {} ===", tableName);
        console.info("Row count: {}", table.size());
        console.info("Column count: {}", table.getColumns().size());

        for (com.github.paohaijiao.statement.JQuickColumnMeta col : table.getColumns()) {
            long nullCount = table.getRows().stream()
                    .filter(row -> row.get(col.getName()) == null)
                    .count();
            double nullPercent = table.isEmpty() ? 0 : (nullCount * 100.0 / table.size());
            console.info("  - {}: {} (null: {} - {:.1f}%)", col.getName(), col.getType().getSimpleName(), nullCount, nullPercent);
        }
        console.info("================================");
    }

    /**
     * Prints a table preview (first few rows) using JConsole.
     *
     * @param tableName the table name
     * @param rowCount  number of rows to preview
     */
    public static void printTablePreview(String tableName, int rowCount) {
        JQuickDataSet table = dataSources.get(tableName.toLowerCase());
        if (table == null) {
            console.warn("Table not found: {}", tableName);
            return;
        }
        console.info("=== Preview of '{}' (first {} rows) ===", tableName, Math.min(rowCount, table.size()));
        List<String> columnNames = table.getColumnNames();
        if (columnNames.isEmpty()) {
            console.warn("Table has no columns");
            return;
        }
        // Print headers
        StringBuilder header = new StringBuilder("|");
        for (String col : columnNames) {
            header.append(" ").append(col).append(" |");
        }
        console.info(header.toString());
        console.info("|---" + repeatChar('-', header.length() - 2) + "|");
        // Print rows
        int displayed = 0;
        for (JQuickRow row : table.getRows()) {
            if (displayed++ >= rowCount) break;
            StringBuilder rowStr = new StringBuilder("|");
            for (String col : columnNames) {
                Object value = row.get(col);
                String strValue = value != null ? value.toString() : "null";
                // Truncate long values
                if (strValue.length() > 30) {
                    strValue = strValue.substring(0, 27) + "...";
                }
                rowStr.append(" ").append(strValue).append(" |");
            }
            console.debug(rowStr.toString());
        }

        if (table.size() > rowCount) {
            console.info("... and {} more rows (total: {} rows)", table.size() - rowCount, table.size());
        }
        console.info("=====================================");
    }

    /**
     * Helper method to repeat a character.
     */
    private static String repeatChar(char c, int count) {
        StringBuilder sb = new StringBuilder(count);
        for (int i = 0; i < count; i++) {
            sb.append(c);
        }
        return sb.toString();
    }

    /**
     * Logs a warning for tables with low row count.
     *
     * @param minRows minimum acceptable row count
     */
    public static void warnSmallTables(int minRows) {
        JConsole console = JConsole.getInstance();
        List<String> smallTables = new ArrayList<>();
        for (Map.Entry<String, Long> entry : tableSizes.entrySet()) {
            if (entry.getValue() < minRows) {
                smallTables.add(entry.getKey() + " (" + entry.getValue() + " rows)");
            }
        }
        if (!smallTables.isEmpty()) {
            console.warn("Tables with less than {} rows: {}", minRows, String.join(", ", smallTables));
        } else {
            console.debug("All tables have at least {} rows", minRows);
        }
    }

    /**
     * Logs the result of a table validation.
     *
     * @param tableName      the table name
     * @param validationName the validation name
     * @param isValid        whether the validation passed
     * @param details        additional details
     */
    public static void logValidationResult(String tableName, String validationName, boolean isValid, String details) {
        if (isValid) {
            console.debug("Validation '{}' passed for table '{}'{}", validationName, tableName, details != null ? ": " + details : "");
        } else {
            console.warn("Validation '{}' FAILED for table '{}'{}", validationName, tableName, details != null ? ": " + details : "");
        }
    }
}
