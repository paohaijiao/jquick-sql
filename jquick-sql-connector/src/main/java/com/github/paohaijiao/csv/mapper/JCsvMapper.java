package com.github.paohaijiao.csv.mapper;

import com.github.paohaijiao.enums.JErrorCode;
import com.github.paohaijiao.exception.JDataAccessException;
import com.github.paohaijiao.csv.schema.JCsvSchema;

import java.io.*;
import java.util.Map;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class JCsvMapper {

    private final JCsvSchema schema;

    public JCsvMapper(JCsvSchema schema) {
        this.schema = schema;
    }


    public List<Map<String, Object>> read(Path filePath) throws JDataAccessException {
        try (BufferedReader reader = Files.newBufferedReader(filePath,
                Charset.forName(schema.getEncoding()))) {
            return read(reader);
        } catch (IOException e) {
            throw JDataAccessException.connectionFailed("读取CSV文件失败: " + filePath, e)
                    .addContext("filePath", filePath.toString())
                    .addContext("schema", schema);
        }
    }


    public List<Map<String, Object>> read(InputStream inputStream) throws JDataAccessException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName(schema.getEncoding())));
        return read(reader);
    }


    private List<Map<String, Object>> read(BufferedReader reader) throws JDataAccessException {
        List<Map<String, Object>> result = new ArrayList<>();
        String line;
        int lineNumber = 0;
        String[] headers = null;
        try {
            if (schema.hasHeader()) {
                line = reader.readLine();
                lineNumber++;
                if (line != null) {
                    headers = parseLine(line);
                }
            } else {
                headers = schema.getColumns().stream()
                        .map(JCsvSchema.Column::getName)
                        .toArray(String[]::new);
            }

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                String[] values = parseLine(line);
                Map<String, Object> row = new HashMap<>();
                for (int i = 0; i < Math.min(headers.length, values.length); i++) {
                    String columnName = headers[i];
                    String value = values[i];
                    row.put(columnName, convertValue(columnName, value));
                }
                result.add(row);
            }
        } catch (IOException e) {
            throw JDataAccessException.resultProcessingError("处理CSV数据时出错", e)
                    .addContext("lineNumber", lineNumber);
        } catch (Exception e) {
            throw JDataAccessException.resultProcessingError("CSV数据格式错误", e)
                    .addContext("lineNumber", lineNumber);
        }

        return result;
    }

    /**
     * 解析单行数据
     */
    private String[] parseLine(String line) {
        List<String> values = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder sb = new StringBuilder();
        char delimiter = schema.getDelimiter();
        char quoteChar = schema.getQuoteChar();
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == quoteChar) {
                inQuotes = !inQuotes;
                if (inQuotes && i < line.length() - 1 && line.charAt(i + 1) == quoteChar) {
                    sb.append(quoteChar);
                    i++;
                }
            } else if (c == delimiter && !inQuotes) {
                values.add(sb.toString());
                sb = new StringBuilder();
            } else {
                sb.append(c);
            }
        }
        values.add(sb.toString());
        return values.toArray(new String[0]);
    }


    private Object convertValue(String columnName, String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }

        // 查找列定义
        JCsvSchema.Column column = schema.getColumns().stream()
                .filter(c -> c.getName().equals(columnName))
                .findFirst()
                .orElseThrow(() -> new JDataAccessException(
                        JErrorCode.CONFIGURATION_ERROR,
                        "未定义的列: " + columnName));

        try {
            switch (column.getDataType()) {
                case STRING:
                    return value;
                case INTEGER:
                    return Integer.parseInt(value);
                case LONG:
                    return Long.parseLong(value);
                case FLOAT:
                    return Float.parseFloat(value);
                case DOUBLE:
                    return Double.parseDouble(value);
                case DECIMAL:
                    return new java.math.BigDecimal(value);
                case BOOLEAN:
                    return Boolean.parseBoolean(value) || "1".equals(value) || "Y".equalsIgnoreCase(value);
                case DATE:
                    return column.getFormat() != null ?
                            java.time.LocalDate.parse(value, java.time.format.DateTimeFormatter.ofPattern(column.getFormat())) :
                            java.time.LocalDate.parse(value);
                case DATETIME:
                    return column.getFormat() != null ?
                            java.time.LocalDateTime.parse(value, java.time.format.DateTimeFormatter.ofPattern(column.getFormat())) :
                            java.time.LocalDateTime.parse(value);
                default:
                    return value;
            }
        } catch (Exception e) {
            throw new JDataAccessException(
                    JErrorCode.RESULT_PROCESSING_ERROR,
                    "列[" + columnName + "]值转换失败: " + value, e)
                    .addContext("dataType", column.getDataType())
                    .addContext("format", column.getFormat());
        }
    }

    public void write(Path filePath, List<Map<String, Object>> data) throws JDataAccessException {
        try (BufferedWriter writer = Files.newBufferedWriter(filePath,
                Charset.forName(schema.getEncoding()))) {
            if (schema.hasHeader()) {
                writeLine(writer, schema.getColumns().stream()
                        .map(JCsvSchema.Column::getName)
                        .toArray(String[]::new));
            }
            for (Map<String, Object> row : data) {
                String[] values = schema.getColumns().stream()
                        .map(column -> convertToString(row.get(column.getName()), column))
                        .toArray(String[]::new);
                writeLine(writer, values);
            }
        } catch (IOException e) {
            throw JDataAccessException.connectionFailed("写入CSV文件失败: " + filePath, e)
                    .addContext("filePath", filePath.toString());
        }
    }

    /**
     * 写入单行数据
     */
    private void writeLine(BufferedWriter writer, String[] values) throws IOException {
        char delimiter = schema.getDelimiter();
        char quoteChar = schema.getQuoteChar();
        boolean first = true;
        for (String value : values) {
            if (!first) {
                writer.write(delimiter);
            }
            first = false;
            if (value == null) {
                continue;
            }
            boolean needQuotes = value.indexOf(delimiter) >= 0 || value.indexOf('\n') >= 0 || value.indexOf(quoteChar) >= 0;
            if (needQuotes) {
                writer.write(quoteChar);
                writer.write(value.replace("" + quoteChar, "" + quoteChar + quoteChar));
                writer.write(quoteChar);
            } else {
                writer.write(value);
            }
        }
        writer.write(schema.getLineSeparator());
    }

    private String convertToString(Object value, JCsvSchema.Column column) {
        if (value == null) {
            return "";
        }

        if (column.getFormat() != null) {
            if (value instanceof java.time.temporal.TemporalAccessor) {
                return java.time.format.DateTimeFormatter.ofPattern(column.getFormat())
                        .format((java.time.temporal.TemporalAccessor) value);
            } else if (value instanceof Number) {
                return new java.text.DecimalFormat(column.getFormat()).format(value);
            }
        }

        return value.toString();
    }
}
