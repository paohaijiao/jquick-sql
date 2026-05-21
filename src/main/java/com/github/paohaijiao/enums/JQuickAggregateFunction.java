package com.github.paohaijiao.enums;

import lombok.Getter;

@Getter
public enum JQuickAggregateFunction {

    SUM("SUM","计算某列的总和"),

    AVG("AVG","计算某列的平均值"),

    COUNT("COUNT","统计行数或非空值的个数"),

    MAX("MAX","获取某列的最大值"),

    MIN("MIN","获取某列的最小值"),

    MEDIAN("MEDIAN","计算某列的中位数"),

    STDDEV("STDDEV","计算样本标准差"),

    VARIANCE("VARIANCE","计算样本方差"),

    FIRST("FIRST","第一个"),

    LAST("LAST","最后一个");


    private String code;

    private String description;

    JQuickAggregateFunction(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static Boolean isAggregateFunction(String functionName){
        for (JQuickAggregateFunction aggregateFunction : JQuickAggregateFunction.values()) {
            if(aggregateFunction.getCode().equalsIgnoreCase(functionName)){
                return true;
            }
        }
        return false;
    }
}
