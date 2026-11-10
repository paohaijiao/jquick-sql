package com.github.paohaijiao.xml.enums;

import lombok.Getter;

@Getter
public enum TypeEnum {

    BASIC("1", "基础信息资源目录"),

    THEME("2", "主题信息资源目录");

    private final String code;
    private final String name;

    TypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
