package com.github.paohaijiao.exception;

import com.github.paohaijiao.enums.JErrorCode;

import java.util.HashMap;
import java.util.Map;

public class JDataAccessException  extends RuntimeException {


    private final JErrorCode errorCode;

    private final String originalSql; // 原始SQL语句(如果是SQL异常)
    private final Map<String, Object> context; // 异常上下文信息

    public JDataAccessException(JErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.originalSql = null;
        this.context = new HashMap<>();
    }

    public JDataAccessException(JErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.originalSql = null;
        this.context = new HashMap<>();
    }

    public JDataAccessException(JErrorCode errorCode, String message, String sql) {
        super(message);
        this.errorCode = errorCode;
        this.originalSql = sql;
        this.context = new HashMap<>();
    }

    public JDataAccessException(JErrorCode errorCode, String message, String sql, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.originalSql = sql;
        this.context = new HashMap<>();
    }


    public JDataAccessException addContext(String key, Object value) {
        this.context.put(key, value);
        return this;
    }

    public Map<String, Object> getContext() {
        return new HashMap<>(context);
    }

    public Object getContextValue(String key) {
        return context.get(key);
    }

    public JErrorCode getErrorCode() {
        return errorCode;
    }

    public String getOriginalSql() {
        return originalSql;
    }

    public static JDataAccessException connectionFailed(String message, Throwable cause) {
        return new JDataAccessException(JErrorCode.CONNECTION_FAILED, message, cause);
    }

    public static JDataAccessException sqlExecutionError(String sql, String message, Throwable cause) {
        return new JDataAccessException(JErrorCode.SQL_EXECUTION_ERROR, message, sql, cause);
    }

    public static JDataAccessException resultProcessingError(String message, Throwable cause) {
        return new JDataAccessException(JErrorCode.RESULT_PROCESSING_ERROR, message, cause);
    }

    public static JDataAccessException transactionError(String message, Throwable cause) {
        return new JDataAccessException(JErrorCode.TRANSACTION_ERROR, message, cause);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("DataAccessException{errorCode=").append(errorCode)
                .append(", message=").append(getMessage())
                .append(", originalSql=").append(originalSql);

        if (!context.isEmpty()) {
            sb.append(", context=").append(context);
        }

        if (getCause() != null) {
            sb.append(", cause=").append(getCause().getClass().getName())
                    .append(": ").append(getCause().getMessage());
        }

        sb.append('}');
        return sb.toString();
    }
}
