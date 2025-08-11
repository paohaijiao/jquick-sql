package com.github.paohaijiao.mapper;

import com.github.paohaijiao.enums.JDataType;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

public class JdbcTypeMapper {

    private static final Map<Integer, JDataType> TYPE_MAPPING = new HashMap<>();

    static {
        TYPE_MAPPING.put(Types.BIT, JDataType.BOOLEAN);
        TYPE_MAPPING.put(Types.TINYINT, JDataType.INTEGER);
        TYPE_MAPPING.put(Types.SMALLINT, JDataType.INTEGER);
        TYPE_MAPPING.put(Types.INTEGER, JDataType.INTEGER);
        TYPE_MAPPING.put(Types.BIGINT, JDataType.LONG);
        TYPE_MAPPING.put(Types.FLOAT, JDataType.FLOAT);
        TYPE_MAPPING.put(Types.REAL, JDataType.FLOAT);
        TYPE_MAPPING.put(Types.DOUBLE, JDataType.DOUBLE);
        TYPE_MAPPING.put(Types.NUMERIC, JDataType.DECIMAL);
        TYPE_MAPPING.put(Types.DECIMAL, JDataType.DECIMAL);

        // String
        TYPE_MAPPING.put(Types.CHAR, JDataType.STRING);
        TYPE_MAPPING.put(Types.VARCHAR, JDataType.STRING);
        TYPE_MAPPING.put(Types.LONGVARCHAR, JDataType.STRING);
        TYPE_MAPPING.put(Types.NCHAR, JDataType.STRING);
        TYPE_MAPPING.put(Types.NVARCHAR, JDataType.STRING);
        TYPE_MAPPING.put(Types.LONGNVARCHAR, JDataType.STRING);

        // Binary
        TYPE_MAPPING.put(Types.BINARY, JDataType.BLOB);
        TYPE_MAPPING.put(Types.VARBINARY, JDataType.BLOB);
        TYPE_MAPPING.put(Types.LONGVARBINARY, JDataType.BLOB);
        TYPE_MAPPING.put(Types.BLOB, JDataType.BLOB);

        // Date
        TYPE_MAPPING.put(Types.DATE, JDataType.DATE);
        TYPE_MAPPING.put(Types.TIME, JDataType.DATE);
        TYPE_MAPPING.put(Types.TIMESTAMP, JDataType.DATETIME);
        TYPE_MAPPING.put(Types.TIME_WITH_TIMEZONE, JDataType.DATETIME);
        TYPE_MAPPING.put(Types.TIMESTAMP_WITH_TIMEZONE, JDataType.DATETIME);

        // other
        TYPE_MAPPING.put(Types.BOOLEAN, JDataType.BOOLEAN);
        TYPE_MAPPING.put(Types.CLOB, JDataType.CLOB);
        TYPE_MAPPING.put(Types.NCLOB, JDataType.CLOB);
        TYPE_MAPPING.put(Types.SQLXML, JDataType.XML);
        TYPE_MAPPING.put(Types.ARRAY, JDataType.STRING); // 数组转为字符串表示
        TYPE_MAPPING.put(Types.STRUCT, JDataType.STRING); // 结构体转为字符串
        TYPE_MAPPING.put(Types.REF, JDataType.STRING);
        TYPE_MAPPING.put(Types.DATALINK, JDataType.STRING);
        TYPE_MAPPING.put(Types.JAVA_OBJECT, JDataType.STRING);
        TYPE_MAPPING.put(Types.OTHER, JDataType.STRING);
    }

    public static JDataType toDataType(int jdbcType) {
        JDataType type = TYPE_MAPPING.get(jdbcType);
        return type != null ? type : JDataType.STRING; // 默认返回STRING类型
    }


    public static JDataType fromTypeName(String typeName) {
        if (typeName == null) {
            return JDataType.STRING;
        }

        String lowerType = typeName.toLowerCase();

        if (lowerType.contains("int")) {
            return JDataType.INTEGER;
        }
        if (lowerType.equals("bigint")) {
            return JDataType.LONG;
        }
        if (lowerType.equals("float") || lowerType.equals("real")) {
            return JDataType.FLOAT;
        }
        if (lowerType.equals("double")) {
            return JDataType.DOUBLE;
        }
        if (lowerType.equals("decimal") || lowerType.equals("numeric")) {
            return JDataType.DECIMAL;
        }

        if (lowerType.contains("char") || lowerType.contains("text") || lowerType.equals("string")) {
            return JDataType.STRING;
        }

        if (lowerType.contains("blob") || lowerType.contains("binary")) {
            return JDataType.BLOB;
        }

        if (lowerType.equals("date")) {
            return JDataType.DATE;
        }
        if (lowerType.equals("time")) {
            return JDataType.DATE;
        }
        if (lowerType.contains("timestamp")) {
            return JDataType.DATETIME;
        }

        if (lowerType.equals("boolean") || lowerType.equals("bool") || lowerType.equals("bit")) {
            return JDataType.BOOLEAN;
        }

        if (lowerType.equals("clob")) {
            return JDataType.CLOB;
        }
        if (lowerType.equals("xml")) {
            return JDataType.XML;
        }
        if (lowerType.equals("json") || lowerType.equals("jsonb")) {
            return JDataType.JSON;
        }

        return JDataType.STRING;
    }
}
