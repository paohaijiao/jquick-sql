package com.github.paohaijiao.model;

import lombok.Data;

import java.util.List;
@Data
public class JMetadata {

    private String dataSourceName;

    private List<JEntityMetadata> entities;
}
