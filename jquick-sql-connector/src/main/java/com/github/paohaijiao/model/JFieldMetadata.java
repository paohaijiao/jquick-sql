package com.github.paohaijiao.model;

import com.github.paohaijiao.enums.JDataType;
import lombok.Data;

@Data
public class JFieldMetadata {

    private String name;

    private JDataType type;

    private boolean nullable;

    private boolean primaryKey;

    private int length;

    private int precision;

    private int scale;
}
