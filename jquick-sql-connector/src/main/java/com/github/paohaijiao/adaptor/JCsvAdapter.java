package com.github.paohaijiao.adaptor;

import com.github.paohaijiao.config.JDataSourceConfig;
import com.github.paohaijiao.csv.schema.JCsvSchema;
import com.github.paohaijiao.enums.JDataType;
import com.github.paohaijiao.enums.JErrorCode;
import com.github.paohaijiao.exception.JDataAccessException;
import com.github.paohaijiao.model.JEntityMetadata;
import com.github.paohaijiao.model.JFieldMetadata;
import com.github.paohaijiao.model.JMetadata;
import com.github.paohaijiao.plan.JDataSet;
import com.github.paohaijiao.plan.JQuery;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class JCsvAdapter implements JDataSourceAdapter {
    private File csvFile;
    private JCsvSchema schema;
    private boolean hasHeader;
    private Charset charset;
    private char delimiter;

    @Override
    public void init(JDataSourceConfig config) {
        if (config.getPath() == null || config.getPath().isEmpty()) {
            throw new JDataAccessException(JErrorCode.CONFIGURATION_ERROR,"CSV文件路径不能为空");
        }

        this.csvFile = new File(config.getPath());
        if (!csvFile.exists() || !csvFile.isFile()) {
            throw new JDataAccessException(JErrorCode.CONFIGURATION_ERROR,"CSV文件不存在或不是有效文件: " + config.getPath());
        }
        this.hasHeader = config.getBoolean("hasHeader");
        this.delimiter = parseDelimiter(config.getString("delimiter"));
        this.charset = parseCharset(config.getString("encoding"));
        this.schema = inferSchema();
    }

    @Override
    public JMetadata getMetadata() {
        JMetadata metadata = new JMetadata();
        metadata.setDataSourceName(csvFile.getName());
        JEntityMetadata entity = new JEntityMetadata();
        entity.setName(csvFile.getName().replace(".csv", ""));
        entity.setDescription("CSV文件: " + csvFile.getAbsolutePath());

        List<JFieldMetadata> fields = new ArrayList<>();
        for (JCsvSchema.Column column : schema.getColumns()) {
            JFieldMetadata field = new JFieldMetadata();
            field.setName(column.getName());
            field.setType(column.getDataType());
            field.setNullable(!column.isRequired());
            field.setLength(-1);
            fields.add(field);
        }
        entity.setFields(fields);
        metadata.getEntities().add(entity);
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
        return false;
    }

    private JCsvSchema inferSchema() {
        JCsvSchema.Builder builder = JCsvSchema.builder()
                .hasHeader(hasHeader)
                .delimiter(delimiter)
                .encoding(charset.name());

        try (BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {
            String firstLine = reader.readLine();
            if (firstLine == null) {
                throw new JDataAccessException(JErrorCode.CONFIGURATION_ERROR,"CSV文件为空: " + csvFile.getPath());
            }
            String[] headers = firstLine.split(String.valueOf(delimiter));
            if (hasHeader) {
                for (String header : headers) {
                    builder.addColumn(header.trim(), JDataType.STRING);
                }
            } else {
                for (int i = 0; i < headers.length; i++) {
                    builder.addColumn("column_" + (i + 1), JDataType.STRING);
                }
            }
            if (hasHeader) {
                inferDataTypes(builder, reader);
            }

        } catch (IOException e) {
            throw new JDataAccessException(
                    JErrorCode.CONNECTION_FAILED,
                    "读取CSV文件失败: " + csvFile.getPath(), e);
        }
        return builder.build();
    }
    private void inferDataTypes(JCsvSchema.Builder builder, BufferedReader reader) throws IOException {
        final int sampleSize = 100;
        String[][] samples = new String[sampleSize][];
        int rowCount = 0;
        String line;
        while ((line = reader.readLine()) != null && rowCount < sampleSize) {
            samples[rowCount++] = line.split(String.valueOf(delimiter));
        }
        for (int col = 0; col < builder.getColumns().size(); col++) {
            JDataType inferredType = JDataType.STRING; // 默认类型
            if (rowCount > 0) {
                inferredType = inferColumnType(samples, rowCount, col);
            }
            builder.getColumns().get(col).setDataType(inferredType) ;
        }
    }
    private JDataType inferColumnType(String[][] samples, int rowCount, int col) {
        boolean allNull = true;
        JDataType currentType = JDataType.STRING;
        for (int row = 0; row < rowCount; row++) {
            if (col >= samples[row].length) continue;
            String value = samples[row][col].trim();
            if (value.isEmpty()) continue;
            allNull = false;
            JDataType valueType = inferValueType(value);
            currentType = widenDataType(currentType, valueType);
            if (currentType == JDataType.STRING) {
                break;
            }
        }

        return allNull ? JDataType.STRING : currentType;
    }
    private JDataType inferValueType(String value) {
        if (value.matches("-?\\d+")) {
            try {
                Long.parseLong(value);
                return JDataType.LONG;
            } catch (NumberFormatException e) {
                return JDataType.STRING;
            }
        }
        if (value.matches("-?\\d+\\.\\d+([Ee][+-]?\\d+)?")) {
            try {
                Double.parseDouble(value);
                return JDataType.DOUBLE;
            } catch (NumberFormatException e) {
                return JDataType.STRING;
            }
        }
        if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")
                || value.equals("1") || value.equals("0")) {
            return JDataType.BOOLEAN;
        }
        if (isDate(value)) {
            return JDataType.DATE;
        }
        return JDataType.STRING;
    }
    private JDataType widenDataType(JDataType current, JDataType newType) {
        if (current == newType) {
            return current;
        }
        if (current == JDataType.LONG && newType == JDataType.DOUBLE) {
            return JDataType.DOUBLE;
        }
        if (current == JDataType.DOUBLE && newType == JDataType.LONG) {
            return JDataType.DOUBLE;
        }
        return JDataType.STRING;
    }
    private char parseDelimiter(String delimiterStr) {
        if (delimiterStr == null || delimiterStr.isEmpty()) {
            return ',';
        }

        if (delimiterStr.length() == 1) {
            return delimiterStr.charAt(0);
        }
        switch (delimiterStr) {
            case "\\t": return '\t';
            case "\\n": return '\n';
            case "\\r": return '\r';
            case "\\0": return '\0';
            default: return ',';
        }
    }
    private Charset parseCharset(String charsetName) {
        try {
            return Charset.forName(charsetName);
        } catch (Exception e) {
            return StandardCharsets.UTF_8;
        }
    }
    private boolean isDate(String value) {
        return value.matches("\\d{4}-\\d{2}-\\d{2}") ||  // yyyy-MM-dd
                value.matches("\\d{2}/\\d{2}/\\d{4}") ||  // MM/dd/yyyy
                value.matches("\\d{4}\\d{2}\\d{2}");      // yyyyMMdd
    }
}
