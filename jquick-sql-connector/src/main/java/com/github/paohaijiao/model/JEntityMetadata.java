package com.github.paohaijiao.model;

import lombok.Data;

import java.util.List;

@Data
public class JEntityMetadata {
    private String name;

    private String description;

    private List<JFieldMetadata> fields;
}
