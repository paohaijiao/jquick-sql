package com.github.paohaijiao.csv.schema;

import com.github.paohaijiao.enums.JDataType;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JCsvSchema {

    private final List<Column> columns;

    private final boolean hasHeader;

    private final char delimiter;

    private final char quoteChar;

    private final String lineSeparator;

    private final String encoding;

    private JCsvSchema(Builder builder) {
        this.columns = Collections.unmodifiableList(new ArrayList<>(builder.columns));
        this.hasHeader = builder.hasHeader;
        this.delimiter = builder.delimiter;
        this.quoteChar = builder.quoteChar;
        this.lineSeparator = builder.lineSeparator;
        this.encoding = builder.encoding;
    }

    public static Builder builder() {
        return new Builder();
    }

    public List<Column> getColumns() {
        return columns;
    }

    public boolean hasHeader() {
        return hasHeader;
    }

    public char getDelimiter() {
        return delimiter;
    }

    public char getQuoteChar() {
        return quoteChar;
    }

    public String getLineSeparator() {
        return lineSeparator;
    }

    public String getEncoding() {
        return encoding;
    }
    @Data
    public static class Column {

        private  String name;

        private JDataType dataType;

        private  boolean required;

        private  String format;

        public Column(String name, JDataType dataType, boolean required, String format) {
            this.name = name;
            this.dataType = dataType;
            this.required = required;
            this.format = format;
        }
    }


    public static class Builder {
        private List<Column> columns = new ArrayList<>();
        private boolean hasHeader = true;
        private char delimiter = ',';
        private char quoteChar = '"';
        private String lineSeparator = "\n";
        private String encoding = "UTF-8";

        public Builder addColumn(String name, JDataType dataType) {
            return addColumn(name, dataType, true, null);
        }

        public Builder addColumn(String name, JDataType dataType, boolean required, String format) {
            columns.add(new Column(name, dataType, required, format));
            return this;
        }

        public Builder hasHeader(boolean hasHeader) {
            this.hasHeader = hasHeader;
            return this;
        }

        public Builder delimiter(char delimiter) {
            this.delimiter = delimiter;
            return this;
        }

        public Builder quoteChar(char quoteChar) {
            this.quoteChar = quoteChar;
            return this;
        }

        public Builder lineSeparator(String lineSeparator) {
            this.lineSeparator = lineSeparator;
            return this;
        }

        public Builder encoding(String encoding) {
            this.encoding = encoding;
            return this;
        }

        public JCsvSchema build() {
            return new JCsvSchema(this);
        }

        public List<Column> getColumns() {
            return columns;
        }
    }
}
