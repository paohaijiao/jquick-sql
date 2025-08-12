package com.github.paohaijiao.enums;

public enum JAggregateFunctionType {
    COUNT, SUM, AVG, MAX, MIN;

    private String measureColumn;

    public String getMeasureColumn() {
        return measureColumn;
    }

    public JAggregateFunctionType forMeasure(String column) {
        this.measureColumn = column;
        return this;
    }
}
