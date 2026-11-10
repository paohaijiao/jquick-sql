package com.github.paohaijiao.xml.enums;

import lombok.Getter;

@Getter
public enum LevelEnum {

    PROVINCIAL("1", "省级"),

    CITY("2", "市（州）"),

    COUNTY("3", "区县");

    private final String code;
    private final String name;

    LevelEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
