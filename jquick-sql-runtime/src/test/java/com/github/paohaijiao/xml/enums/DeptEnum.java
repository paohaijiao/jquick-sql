package com.github.paohaijiao.xml.enums;

import lombok.Getter;

@Getter
public enum DeptEnum {


    PROVINCE(0L, -1L, "四川省"),

    PROVINCIAL_ECONOMY_AND_INFORMATION(1L, 0L, "省经济和信息化厅"),

    PROVINCIAL_PUBLIC_SECURITY(2L, 0L, "省公安厅"),

    PROVINCIAL_EDUCATION(3L, 0L, "省教育厅"),

    PROVINCIAL_CIVIL_AFFAIRS(4L, 0L, "省民政厅"),

    PROVINCIAL_HUMAN_RESOURCES_AND_SOCIAL_SECURITY(5L, 0L, "省人力资源社会保障厅"),

    PROVINCIAL_NATURAL_RESOURCES(6L, 0L, "省自然资源厅"),

    PROVINCIAL_HEALTH_COMMISSION(7L, 0L, "省卫生健康委"),

    PROVINCIAL_TRANSPORT(8L, 0L, "省交通运输厅"),

    PROVINCIAL_ECOLOGY_AND_ENVIRONMENT(9L, 0L, "省生态环境厅"),

    PROVINCIAL_MARKET_REGULATION(10L, 0L, "省市场监督管理局"),

    CITY_CHENGDU(11L, 0L, "成都市"),

    CHENGDU_ECONOMY_AND_INFORMATION(101L, 11L, "成都市经济和信息化局"),

    CHENGDU_PUBLIC_SECURITY(102L, 11L, "成都市公安局"),

    CHENGDU_EDUCATION(103L, 11L, "成都市教育局"),

    CHENGDU_CIVIL_AFFAIRS(104L, 11L, "成都市民政局"),

    CHENGDU_HUMAN_RESOURCES_AND_SOCIAL_SECURITY(105L, 11L, "成都市人力资源和社会保障局"),

    CHENGDU_NATURAL_RESOURCES(106L, 11L, "成都市规划和自然资源局"),

    CHENGDU_HEALTH_COMMISSION(107L, 11L, "成都市卫生健康委员会"),

    CHENGDU_TRANSPORT(108L, 11L, "成都市交通运输局"),

    CHENGDU_ECOLOGY_AND_ENVIRONMENT(109L, 11L, "成都市生态环境局"),

    CHENGDU_MARKET_REGULATION(110L, 11L, "成都市市场监督管理局");

    private final Integer id;

    private final Integer parentId;

    private final String name;

     DeptEnum(Long id, Long parentId, String name) {
        this.id = id.intValue();
        this.parentId = parentId.intValue();
        this.name = name;
    }


}
