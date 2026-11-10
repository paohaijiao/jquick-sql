package com.github.paohaijiao.xml.model;

import lombok.Data;

import java.util.Date;

@Data
public class DataStatistics {

    private String deptName;

    private Integer deptId;

    private Integer compiledCatalogTotal;

    private Integer claimedCatalogTotal;

    private String claimRatio;

    private Integer completedCompilation;

    private Integer pendingCompilation;

    private String compilationRatio;


    private String compilationProgress;

    //筛选字段
    private String   type;

    private String level;

    private Date createDate;
}
